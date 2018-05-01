begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2009 The Android Open Source Project
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// Licensed under the Apache License, Version 2.0 (the "License");
end_comment

begin_comment
comment|// you may not use this file except in compliance with the License.
end_comment

begin_comment
comment|// You may obtain a copy of the License at
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// http://www.apache.org/licenses/LICENSE-2.0
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// Unless required by applicable law or agreed to in writing, software
end_comment

begin_comment
comment|// distributed under the License is distributed on an "AS IS" BASIS,
end_comment

begin_comment
comment|// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
end_comment

begin_comment
comment|// See the License for the specific language governing permissions and
end_comment

begin_comment
comment|// limitations under the License.
end_comment

begin_package
DECL|package|com.google.gerrit.sshd
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|sshd
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|extensions
operator|.
name|registration
operator|.
name|PrivateInternals_DynamicTypes
operator|.
name|registerInParentInjectors
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Scopes
operator|.
name|SINGLETON
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|CharMatcher
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Splitter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|extensions
operator|.
name|registration
operator|.
name|DynamicItem
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|extensions
operator|.
name|registration
operator|.
name|DynamicMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|extensions
operator|.
name|registration
operator|.
name|DynamicSet
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|lifecycle
operator|.
name|LifecycleModule
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|DynamicOptions
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|PeerDaemonUser
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|RemotePeer
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|config
operator|.
name|GerritConfigListener
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|config
operator|.
name|GerritRequestModule
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|config
operator|.
name|GerritServerConfig
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|git
operator|.
name|QueueProvider
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|git
operator|.
name|receive
operator|.
name|AsyncReceiveCommits
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|plugins
operator|.
name|ModuleGenerator
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|plugins
operator|.
name|ReloadPluginListener
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|plugins
operator|.
name|StartPluginListener
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|ssh
operator|.
name|SshInfo
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|util
operator|.
name|RequestScopePropagator
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|sshd
operator|.
name|commands
operator|.
name|QueryShell
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|internal
operator|.
name|UniqueAnnotations
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|servlet
operator|.
name|RequestScoped
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ScheduledThreadPoolExecutor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|sshd
operator|.
name|server
operator|.
name|CommandFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|sshd
operator|.
name|server
operator|.
name|auth
operator|.
name|gss
operator|.
name|GSSAuthenticator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|sshd
operator|.
name|server
operator|.
name|auth
operator|.
name|pubkey
operator|.
name|PublickeyAuthenticator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Config
import|;
end_import

begin_comment
comment|/** Configures standard dependencies for {@link SshDaemon}. */
end_comment

begin_class
DECL|class|SshModule
specifier|public
class|class
name|SshModule
extends|extends
name|LifecycleModule
block|{
DECL|field|aliases
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|aliases
decl_stmt|;
annotation|@
name|Inject
DECL|method|SshModule (@erritServerConfig Config cfg)
name|SshModule
parameter_list|(
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|)
block|{
name|aliases
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|name
range|:
name|cfg
operator|.
name|getNames
argument_list|(
literal|"ssh-alias"
argument_list|,
literal|true
argument_list|)
control|)
block|{
name|aliases
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|cfg
operator|.
name|getString
argument_list|(
literal|"ssh-alias"
argument_list|,
literal|null
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|configure ()
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|bindScope
argument_list|(
name|RequestScoped
operator|.
name|class
argument_list|,
name|SshScope
operator|.
name|REQUEST
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|RequestScopePropagator
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|SshScope
operator|.
name|Propagator
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|SshScope
operator|.
name|class
argument_list|)
operator|.
name|in
argument_list|(
name|SINGLETON
argument_list|)
expr_stmt|;
name|configureRequestScope
argument_list|()
expr_stmt|;
name|install
argument_list|(
operator|new
name|AsyncReceiveCommits
operator|.
name|Module
argument_list|()
argument_list|)
expr_stmt|;
name|configureAliases
argument_list|()
expr_stmt|;
name|bind
argument_list|(
name|SshLog
operator|.
name|class
argument_list|)
expr_stmt|;
name|DynamicSet
operator|.
name|bind
argument_list|(
name|binder
argument_list|()
argument_list|,
name|GerritConfigListener
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|SshLog
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|SshInfo
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|SshDaemon
operator|.
name|class
argument_list|)
operator|.
name|in
argument_list|(
name|SINGLETON
argument_list|)
expr_stmt|;
name|factory
argument_list|(
name|DispatchCommand
operator|.
name|Factory
operator|.
name|class
argument_list|)
expr_stmt|;
name|factory
argument_list|(
name|QueryShell
operator|.
name|Factory
operator|.
name|class
argument_list|)
expr_stmt|;
name|factory
argument_list|(
name|PeerDaemonUser
operator|.
name|Factory
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|DispatchCommandProvider
operator|.
name|class
argument_list|)
operator|.
name|annotatedWith
argument_list|(
name|Commands
operator|.
name|CMD_ROOT
argument_list|)
operator|.
name|toInstance
argument_list|(
operator|new
name|DispatchCommandProvider
argument_list|(
name|Commands
operator|.
name|CMD_ROOT
argument_list|)
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|CommandFactoryProvider
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|CommandFactory
operator|.
name|class
argument_list|)
operator|.
name|toProvider
argument_list|(
name|CommandFactoryProvider
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|ScheduledThreadPoolExecutor
operator|.
name|class
argument_list|)
operator|.
name|annotatedWith
argument_list|(
name|StreamCommandExecutor
operator|.
name|class
argument_list|)
operator|.
name|toProvider
argument_list|(
name|StreamCommandExecutorProvider
operator|.
name|class
argument_list|)
operator|.
name|in
argument_list|(
name|SINGLETON
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|QueueProvider
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|CommandExecutorQueueProvider
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|GSSAuthenticator
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|GerritGSSAuthenticator
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|PublickeyAuthenticator
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|CachingPublicKeyAuthenticator
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|ModuleGenerator
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|SshAutoRegisterModuleGenerator
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|SshPluginStarterCallback
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|StartPluginListener
operator|.
name|class
argument_list|)
operator|.
name|annotatedWith
argument_list|(
name|UniqueAnnotations
operator|.
name|create
argument_list|()
argument_list|)
operator|.
name|to
argument_list|(
name|SshPluginStarterCallback
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|ReloadPluginListener
operator|.
name|class
argument_list|)
operator|.
name|annotatedWith
argument_list|(
name|UniqueAnnotations
operator|.
name|create
argument_list|()
argument_list|)
operator|.
name|to
argument_list|(
name|SshPluginStarterCallback
operator|.
name|class
argument_list|)
expr_stmt|;
name|DynamicMap
operator|.
name|mapOf
argument_list|(
name|binder
argument_list|()
argument_list|,
name|DynamicOptions
operator|.
name|DynamicBean
operator|.
name|class
argument_list|)
expr_stmt|;
name|DynamicItem
operator|.
name|itemOf
argument_list|(
name|binder
argument_list|()
argument_list|,
name|SshCreateCommandInterceptor
operator|.
name|class
argument_list|)
expr_stmt|;
name|listener
argument_list|()
operator|.
name|toInstance
argument_list|(
name|registerInParentInjectors
argument_list|()
argument_list|)
expr_stmt|;
name|listener
argument_list|()
operator|.
name|to
argument_list|(
name|SshLog
operator|.
name|class
argument_list|)
expr_stmt|;
name|listener
argument_list|()
operator|.
name|to
argument_list|(
name|SshDaemon
operator|.
name|class
argument_list|)
expr_stmt|;
name|listener
argument_list|()
operator|.
name|to
argument_list|(
name|CommandFactoryProvider
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|configureAliases ()
specifier|private
name|void
name|configureAliases
parameter_list|()
block|{
name|CommandName
name|gerrit
init|=
name|Commands
operator|.
name|named
argument_list|(
literal|"gerrit"
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|e
range|:
name|aliases
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|dest
init|=
name|Splitter
operator|.
name|on
argument_list|(
name|CharMatcher
operator|.
name|whitespace
argument_list|()
argument_list|)
operator|.
name|splitToList
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|CommandName
name|cmd
init|=
name|Commands
operator|.
name|named
argument_list|(
name|dest
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|dest
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|cmd
operator|=
name|Commands
operator|.
name|named
argument_list|(
name|cmd
argument_list|,
name|dest
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|bind
argument_list|(
name|Commands
operator|.
name|key
argument_list|(
name|gerrit
argument_list|,
name|name
argument_list|)
argument_list|)
operator|.
name|toProvider
argument_list|(
operator|new
name|AliasCommandProvider
argument_list|(
name|cmd
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|configureRequestScope ()
specifier|private
name|void
name|configureRequestScope
parameter_list|()
block|{
name|bind
argument_list|(
name|SshScope
operator|.
name|Context
operator|.
name|class
argument_list|)
operator|.
name|toProvider
argument_list|(
name|SshScope
operator|.
name|ContextProvider
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|SshSession
operator|.
name|class
argument_list|)
operator|.
name|toProvider
argument_list|(
name|SshScope
operator|.
name|SshSessionProvider
operator|.
name|class
argument_list|)
operator|.
name|in
argument_list|(
name|SshScope
operator|.
name|REQUEST
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|SocketAddress
operator|.
name|class
argument_list|)
operator|.
name|annotatedWith
argument_list|(
name|RemotePeer
operator|.
name|class
argument_list|)
operator|.
name|toProvider
argument_list|(
name|SshRemotePeerProvider
operator|.
name|class
argument_list|)
operator|.
name|in
argument_list|(
name|SshScope
operator|.
name|REQUEST
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|ScheduledThreadPoolExecutor
operator|.
name|class
argument_list|)
operator|.
name|annotatedWith
argument_list|(
name|CommandExecutor
operator|.
name|class
argument_list|)
operator|.
name|toProvider
argument_list|(
name|CommandExecutorProvider
operator|.
name|class
argument_list|)
operator|.
name|in
argument_list|(
name|SshScope
operator|.
name|REQUEST
argument_list|)
expr_stmt|;
name|install
argument_list|(
operator|new
name|GerritRequestModule
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

