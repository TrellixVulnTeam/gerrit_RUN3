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
DECL|package|com.google.gerrit.git
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|git
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|reviewdb
operator|.
name|Project
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
name|GerritServer
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtjsonrpc
operator|.
name|server
operator|.
name|XsrfException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|client
operator|.
name|OrmException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|jcraft
operator|.
name|jsch
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|errors
operator|.
name|ConfigInvalidException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|lib
operator|.
name|RepositoryConfig
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|transport
operator|.
name|OpenSshConfig
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|transport
operator|.
name|RefSpec
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|transport
operator|.
name|RemoteConfig
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|transport
operator|.
name|SshConfigSessionFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|transport
operator|.
name|SshSessionFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|transport
operator|.
name|URIish
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|TimeUnit
import|;
end_import

begin_comment
comment|/** Manages automatic replication to remote repositories. */
end_comment

begin_class
DECL|class|PushQueue
specifier|public
class|class
name|PushQueue
block|{
DECL|field|log
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|PushQueue
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|configs
specifier|private
specifier|static
name|List
argument_list|<
name|ReplicationConfig
argument_list|>
name|configs
decl_stmt|;
static|static
block|{
comment|// Install our own factory which always runs in batch mode, as we
comment|// have no UI available for interactive prompting.
comment|//
name|SshSessionFactory
operator|.
name|setInstance
argument_list|(
operator|new
name|SshConfigSessionFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|configure
parameter_list|(
name|OpenSshConfig
operator|.
name|Host
name|hc
parameter_list|,
name|Session
name|session
parameter_list|)
block|{
comment|// Default configuration is batch mode.
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Determine if replication is enabled, or not. */
DECL|method|isReplicationEnabled ()
specifier|public
specifier|static
name|boolean
name|isReplicationEnabled
parameter_list|()
block|{
return|return
operator|!
name|allConfigs
argument_list|()
operator|.
name|isEmpty
argument_list|()
return|;
block|}
comment|/**    * Schedule a full replication for a single project.    *<p>    * All remote URLs are checked to verify the are current with regards to the    * local project state. If not, they are updated by pushing new refs, updating    * existing ones which don't match, and deleting stale refs which have been    * removed from the local repository.    *     * @param project identity of the project to replicate.    * @param urlMatch substring that must appear in a URI to support replication.    */
DECL|method|scheduleFullSync (final Project.NameKey project, final String urlMatch)
specifier|public
specifier|static
name|void
name|scheduleFullSync
parameter_list|(
specifier|final
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
specifier|final
name|String
name|urlMatch
parameter_list|)
block|{
for|for
control|(
specifier|final
name|ReplicationConfig
name|cfg
range|:
name|allConfigs
argument_list|()
control|)
block|{
for|for
control|(
specifier|final
name|URIish
name|uri
range|:
name|cfg
operator|.
name|getURIs
argument_list|(
name|project
argument_list|,
name|urlMatch
argument_list|)
control|)
block|{
name|cfg
operator|.
name|schedule
argument_list|(
name|project
argument_list|,
name|PushOp
operator|.
name|MIRROR_ALL
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Schedule update of a single ref.    *<p>    * This method automatically tries to batch together multiple requests in the    * same project, to take advantage of Git's native ability to update multiple    * refs during a single push operation.    *     * @param project identity of the project to replicate.    * @param ref unique name of the ref; must start with {@code refs/}.    */
DECL|method|scheduleUpdate (final Project.NameKey project, final String ref)
specifier|public
specifier|static
name|void
name|scheduleUpdate
parameter_list|(
specifier|final
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
specifier|final
name|String
name|ref
parameter_list|)
block|{
for|for
control|(
specifier|final
name|ReplicationConfig
name|cfg
range|:
name|allConfigs
argument_list|()
control|)
block|{
if|if
condition|(
name|cfg
operator|.
name|wouldPushRef
argument_list|(
name|ref
argument_list|)
condition|)
block|{
for|for
control|(
specifier|final
name|URIish
name|uri
range|:
name|cfg
operator|.
name|getURIs
argument_list|(
name|project
argument_list|,
literal|null
argument_list|)
control|)
block|{
name|cfg
operator|.
name|schedule
argument_list|(
name|project
argument_list|,
name|ref
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|replace (final String pat, final String key, final String val)
specifier|private
specifier|static
name|String
name|replace
parameter_list|(
specifier|final
name|String
name|pat
parameter_list|,
specifier|final
name|String
name|key
parameter_list|,
specifier|final
name|String
name|val
parameter_list|)
block|{
specifier|final
name|int
name|n
init|=
name|pat
operator|.
name|indexOf
argument_list|(
literal|"${"
operator|+
name|key
operator|+
literal|"}"
argument_list|)
decl_stmt|;
return|return
name|pat
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|n
argument_list|)
operator|+
name|val
operator|+
name|pat
operator|.
name|substring
argument_list|(
name|n
operator|+
literal|3
operator|+
name|key
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
DECL|method|allConfigs ()
specifier|private
specifier|static
specifier|synchronized
name|List
argument_list|<
name|ReplicationConfig
argument_list|>
name|allConfigs
parameter_list|()
block|{
if|if
condition|(
name|configs
operator|==
literal|null
condition|)
block|{
specifier|final
name|File
name|path
decl_stmt|;
try|try
block|{
specifier|final
name|GerritServer
name|gs
init|=
name|GerritServer
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|path
operator|=
name|gs
operator|.
name|getSitePath
argument_list|()
expr_stmt|;
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|XsrfException
name|e
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
specifier|final
name|File
name|cfgFile
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|,
literal|"replication.config"
argument_list|)
decl_stmt|;
specifier|final
name|RepositoryConfig
name|cfg
init|=
operator|new
name|RepositoryConfig
argument_list|(
literal|null
argument_list|,
name|cfgFile
argument_list|)
decl_stmt|;
try|try
block|{
name|cfg
operator|.
name|load
argument_list|()
expr_stmt|;
specifier|final
name|List
argument_list|<
name|ReplicationConfig
argument_list|>
name|r
init|=
operator|new
name|ArrayList
argument_list|<
name|ReplicationConfig
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|RemoteConfig
name|c
range|:
name|RemoteConfig
operator|.
name|getAllRemoteConfigs
argument_list|(
name|cfg
argument_list|)
control|)
block|{
if|if
condition|(
name|c
operator|.
name|getURIs
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
continue|continue;
block|}
for|for
control|(
specifier|final
name|URIish
name|u
range|:
name|c
operator|.
name|getURIs
argument_list|()
control|)
block|{
if|if
condition|(
name|u
operator|.
name|getPath
argument_list|()
operator|==
literal|null
operator|||
operator|!
name|u
operator|.
name|getPath
argument_list|()
operator|.
name|contains
argument_list|(
literal|"${name}"
argument_list|)
condition|)
block|{
specifier|final
name|String
name|s
init|=
name|u
operator|.
name|toString
argument_list|()
decl_stmt|;
throw|throw
operator|new
name|URISyntaxException
argument_list|(
name|s
argument_list|,
literal|"No ${name}"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|c
operator|.
name|getPushRefSpecs
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|RefSpec
name|spec
init|=
operator|new
name|RefSpec
argument_list|()
decl_stmt|;
name|spec
operator|=
name|spec
operator|.
name|setSourceDestination
argument_list|(
literal|"refs/*"
argument_list|,
literal|"refs/*"
argument_list|)
expr_stmt|;
name|spec
operator|=
name|spec
operator|.
name|setForceUpdate
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|c
operator|.
name|addPushRefSpec
argument_list|(
name|spec
argument_list|)
expr_stmt|;
block|}
name|r
operator|.
name|add
argument_list|(
operator|new
name|ReplicationConfig
argument_list|(
name|c
argument_list|,
name|cfg
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|configs
operator|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"No "
operator|+
name|cfgFile
operator|+
literal|"; not replicating"
argument_list|)
expr_stmt|;
name|configs
operator|=
name|Collections
operator|.
name|emptyList
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConfigInvalidException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Can't read "
operator|+
name|cfgFile
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Can't read "
operator|+
name|cfgFile
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Invalid URI in "
operator|+
name|cfgFile
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
block|}
return|return
name|configs
return|;
block|}
DECL|class|ReplicationConfig
specifier|static
class|class
name|ReplicationConfig
block|{
DECL|field|remote
specifier|private
specifier|final
name|RemoteConfig
name|remote
decl_stmt|;
DECL|field|delay
specifier|private
specifier|final
name|int
name|delay
decl_stmt|;
DECL|field|pool
specifier|private
specifier|final
name|WorkQueue
operator|.
name|Executor
name|pool
decl_stmt|;
DECL|field|pending
specifier|private
specifier|final
name|Map
argument_list|<
name|URIish
argument_list|,
name|PushOp
argument_list|>
name|pending
init|=
operator|new
name|HashMap
argument_list|<
name|URIish
argument_list|,
name|PushOp
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|ReplicationConfig (final RemoteConfig rc, final RepositoryConfig cfg)
name|ReplicationConfig
parameter_list|(
specifier|final
name|RemoteConfig
name|rc
parameter_list|,
specifier|final
name|RepositoryConfig
name|cfg
parameter_list|)
block|{
name|remote
operator|=
name|rc
expr_stmt|;
name|delay
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|getInt
argument_list|(
name|rc
argument_list|,
name|cfg
argument_list|,
literal|"replicationdelay"
argument_list|,
literal|15
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|int
name|poolSize
init|=
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|getInt
argument_list|(
name|rc
argument_list|,
name|cfg
argument_list|,
literal|"threads"
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|String
name|poolName
init|=
literal|"ReplicateTo-"
operator|+
name|rc
operator|.
name|getName
argument_list|()
decl_stmt|;
name|pool
operator|=
name|WorkQueue
operator|.
name|createQueue
argument_list|(
name|poolSize
argument_list|,
name|poolName
argument_list|)
expr_stmt|;
block|}
DECL|method|getInt (final RemoteConfig rc, final RepositoryConfig cfg, final String name, final int defValue)
specifier|private
specifier|static
name|int
name|getInt
parameter_list|(
specifier|final
name|RemoteConfig
name|rc
parameter_list|,
specifier|final
name|RepositoryConfig
name|cfg
parameter_list|,
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|int
name|defValue
parameter_list|)
block|{
return|return
name|cfg
operator|.
name|getInt
argument_list|(
literal|"remote"
argument_list|,
name|rc
operator|.
name|getName
argument_list|()
argument_list|,
name|name
argument_list|,
name|defValue
argument_list|)
return|;
block|}
DECL|method|schedule (final Project.NameKey project, final String ref, final URIish uri)
name|void
name|schedule
parameter_list|(
specifier|final
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
specifier|final
name|String
name|ref
parameter_list|,
specifier|final
name|URIish
name|uri
parameter_list|)
block|{
synchronized|synchronized
init|(
name|pending
init|)
block|{
name|PushOp
name|e
init|=
name|pending
operator|.
name|get
argument_list|(
name|uri
argument_list|)
decl_stmt|;
if|if
condition|(
name|e
operator|==
literal|null
condition|)
block|{
name|e
operator|=
operator|new
name|PushOp
argument_list|(
name|this
argument_list|,
name|project
operator|.
name|get
argument_list|()
argument_list|,
name|remote
argument_list|,
name|uri
argument_list|)
expr_stmt|;
name|pool
operator|.
name|schedule
argument_list|(
name|e
argument_list|,
name|delay
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|pending
operator|.
name|put
argument_list|(
name|uri
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|e
operator|.
name|addRef
argument_list|(
name|ref
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|notifyStarting (final PushOp op)
name|void
name|notifyStarting
parameter_list|(
specifier|final
name|PushOp
name|op
parameter_list|)
block|{
synchronized|synchronized
init|(
name|pending
init|)
block|{
name|pending
operator|.
name|remove
argument_list|(
name|op
operator|.
name|getURI
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|wouldPushRef (final String ref)
name|boolean
name|wouldPushRef
parameter_list|(
specifier|final
name|String
name|ref
parameter_list|)
block|{
for|for
control|(
specifier|final
name|RefSpec
name|s
range|:
name|remote
operator|.
name|getPushRefSpecs
argument_list|()
control|)
block|{
if|if
condition|(
name|s
operator|.
name|matchSource
argument_list|(
name|ref
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|getURIs (final Project.NameKey project, final String urlMatch)
name|List
argument_list|<
name|URIish
argument_list|>
name|getURIs
parameter_list|(
specifier|final
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
specifier|final
name|String
name|urlMatch
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|URIish
argument_list|>
name|r
init|=
operator|new
name|ArrayList
argument_list|<
name|URIish
argument_list|>
argument_list|(
name|remote
operator|.
name|getURIs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|URIish
name|uri
range|:
name|remote
operator|.
name|getURIs
argument_list|()
control|)
block|{
if|if
condition|(
name|matches
argument_list|(
name|uri
argument_list|,
name|urlMatch
argument_list|)
condition|)
block|{
name|uri
operator|=
name|uri
operator|.
name|setPath
argument_list|(
name|replace
argument_list|(
name|uri
operator|.
name|getPath
argument_list|()
argument_list|,
literal|"name"
argument_list|,
name|project
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|r
operator|.
name|add
argument_list|(
name|uri
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|r
return|;
block|}
DECL|method|matches (URIish uri, final String urlMatch)
specifier|private
specifier|static
name|boolean
name|matches
parameter_list|(
name|URIish
name|uri
parameter_list|,
specifier|final
name|String
name|urlMatch
parameter_list|)
block|{
if|if
condition|(
name|urlMatch
operator|==
literal|null
operator|||
name|urlMatch
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
operator|||
name|urlMatch
operator|.
name|equals
argument_list|(
literal|"*"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
name|uri
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
name|urlMatch
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

