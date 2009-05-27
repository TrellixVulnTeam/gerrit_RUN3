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
name|NotSupportedException
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
name|TransportException
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
name|NullProgressMonitor
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
name|Repository
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
name|PushResult
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
name|RemoteRefUpdate
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
name|Transport
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
name|HashSet
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
name|Set
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

begin_class
DECL|class|PushQueue
specifier|public
class|class
name|PushQueue
block|{
DECL|field|log
specifier|private
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
DECL|field|startDelay
specifier|private
specifier|static
specifier|final
name|int
name|startDelay
init|=
literal|15
decl_stmt|;
comment|// seconds
DECL|field|configs
specifier|private
specifier|static
name|List
argument_list|<
name|RemoteConfig
argument_list|>
name|configs
decl_stmt|;
DECL|field|active
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|URIish
argument_list|,
name|PushOp
argument_list|>
name|active
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
name|RemoteConfig
name|srcConf
range|:
name|allConfigs
argument_list|()
control|)
block|{
name|RefSpec
name|spec
init|=
literal|null
decl_stmt|;
for|for
control|(
specifier|final
name|RefSpec
name|s
range|:
name|srcConf
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
name|spec
operator|=
name|s
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|spec
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
for|for
control|(
name|URIish
name|uri
range|:
name|srcConf
operator|.
name|getURIs
argument_list|()
control|)
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
name|scheduleImp
argument_list|(
name|project
argument_list|,
name|ref
argument_list|,
name|srcConf
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|scheduleImp (final Project.NameKey project, final String ref, final RemoteConfig srcConf, final URIish uri)
specifier|private
specifier|static
specifier|synchronized
name|void
name|scheduleImp
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
name|RemoteConfig
name|srcConf
parameter_list|,
specifier|final
name|URIish
name|uri
parameter_list|)
block|{
name|PushOp
name|e
init|=
name|active
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
specifier|final
name|PushOp
name|newOp
init|=
operator|new
name|PushOp
argument_list|(
name|project
operator|.
name|get
argument_list|()
argument_list|,
name|srcConf
argument_list|,
name|uri
argument_list|)
decl_stmt|;
name|WorkQueue
operator|.
name|schedule
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|pushImpl
argument_list|(
name|newOp
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Unexpected error during replication"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Error
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Unexpected error during replication"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|,
name|startDelay
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|active
operator|.
name|put
argument_list|(
name|uri
argument_list|,
name|newOp
argument_list|)
expr_stmt|;
name|e
operator|=
name|newOp
expr_stmt|;
block|}
name|e
operator|.
name|delta
operator|.
name|add
argument_list|(
name|ref
argument_list|)
expr_stmt|;
block|}
DECL|method|pushImpl (final PushOp op)
specifier|private
specifier|static
name|void
name|pushImpl
parameter_list|(
specifier|final
name|PushOp
name|op
parameter_list|)
block|{
name|removeFromActive
argument_list|(
name|op
argument_list|)
expr_stmt|;
specifier|final
name|Repository
name|db
decl_stmt|;
try|try
block|{
name|db
operator|=
name|GerritServer
operator|.
name|getInstance
argument_list|()
operator|.
name|getRepositoryCache
argument_list|()
operator|.
name|get
argument_list|(
name|op
operator|.
name|projectName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cannot open repository cache"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|XsrfException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cannot open repository cache"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|InvalidRepositoryException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cannot replicate "
operator|+
name|op
operator|.
name|projectName
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|ArrayList
argument_list|<
name|RemoteRefUpdate
argument_list|>
name|cmds
init|=
operator|new
name|ArrayList
argument_list|<
name|RemoteRefUpdate
argument_list|>
argument_list|()
decl_stmt|;
try|try
block|{
for|for
control|(
specifier|final
name|String
name|ref
range|:
name|op
operator|.
name|delta
control|)
block|{
specifier|final
name|String
name|src
init|=
name|ref
decl_stmt|;
name|RefSpec
name|spec
init|=
literal|null
decl_stmt|;
for|for
control|(
specifier|final
name|RefSpec
name|s
range|:
name|op
operator|.
name|config
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
name|src
argument_list|)
condition|)
block|{
name|spec
operator|=
name|s
operator|.
name|expandFromSource
argument_list|(
name|src
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|spec
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
comment|// If the ref still exists locally, send it, else delete it.
comment|//
specifier|final
name|String
name|srcexp
init|=
name|db
operator|.
name|resolve
argument_list|(
name|src
argument_list|)
operator|!=
literal|null
condition|?
name|src
else|:
literal|null
decl_stmt|;
specifier|final
name|String
name|dst
init|=
name|spec
operator|.
name|getDestination
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|force
init|=
name|spec
operator|.
name|isForceUpdate
argument_list|()
decl_stmt|;
name|cmds
operator|.
name|add
argument_list|(
operator|new
name|RemoteRefUpdate
argument_list|(
name|db
argument_list|,
name|srcexp
argument_list|,
name|dst
argument_list|,
name|force
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
literal|"Cannot replicate "
operator|+
name|op
operator|.
name|projectName
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|Transport
name|tn
decl_stmt|;
try|try
block|{
name|tn
operator|=
name|Transport
operator|.
name|open
argument_list|(
name|db
argument_list|,
name|op
operator|.
name|uri
argument_list|)
expr_stmt|;
name|tn
operator|.
name|applyConfig
argument_list|(
name|op
operator|.
name|config
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NotSupportedException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cannot replicate to "
operator|+
name|op
operator|.
name|uri
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|PushResult
name|res
decl_stmt|;
try|try
block|{
name|res
operator|=
name|tn
operator|.
name|push
argument_list|(
name|NullProgressMonitor
operator|.
name|INSTANCE
argument_list|,
name|cmds
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NotSupportedException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cannot replicate to "
operator|+
name|op
operator|.
name|uri
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|TransportException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cannot replicate to "
operator|+
name|op
operator|.
name|uri
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
finally|finally
block|{
try|try
block|{
name|tn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e2
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Unexpected error while closing "
operator|+
name|op
operator|.
name|uri
argument_list|,
name|e2
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
specifier|final
name|RemoteRefUpdate
name|u
range|:
name|res
operator|.
name|getRemoteUpdates
argument_list|()
control|)
block|{
switch|switch
condition|(
name|u
operator|.
name|getStatus
argument_list|()
condition|)
block|{
case|case
name|OK
case|:
case|case
name|UP_TO_DATE
case|:
case|case
name|NON_EXISTING
case|:
break|break;
case|case
name|NOT_ATTEMPTED
case|:
case|case
name|AWAITING_REPORT
case|:
case|case
name|REJECTED_NODELETE
case|:
case|case
name|REJECTED_NONFASTFORWARD
case|:
case|case
name|REJECTED_REMOTE_CHANGED
case|:
name|log
operator|.
name|error
argument_list|(
literal|"Failed replicate of "
operator|+
name|u
operator|.
name|getRemoteName
argument_list|()
operator|+
literal|" to "
operator|+
name|op
operator|.
name|uri
operator|+
literal|": status "
operator|+
name|u
operator|.
name|getStatus
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|REJECTED_OTHER_REASON
case|:
name|log
operator|.
name|error
argument_list|(
literal|"Failed replicate of "
operator|+
name|u
operator|.
name|getRemoteName
argument_list|()
operator|+
literal|" to "
operator|+
name|op
operator|.
name|uri
operator|+
literal|", reason: "
operator|+
name|u
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
DECL|method|removeFromActive (final PushOp op)
specifier|private
specifier|static
specifier|synchronized
name|void
name|removeFromActive
parameter_list|(
specifier|final
name|PushOp
name|op
parameter_list|)
block|{
name|active
operator|.
name|remove
argument_list|(
name|op
operator|.
name|uri
argument_list|)
expr_stmt|;
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
name|RemoteConfig
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
operator|||
name|gs
operator|.
name|getRepositoryCache
argument_list|()
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
name|ArrayList
argument_list|<
name|RemoteConfig
argument_list|>
name|r
init|=
operator|new
name|ArrayList
argument_list|<
name|RemoteConfig
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
name|c
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
block|}
return|return
name|configs
return|;
block|}
DECL|class|PushOp
specifier|private
specifier|static
class|class
name|PushOp
block|{
DECL|field|delta
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|delta
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|projectName
specifier|final
name|String
name|projectName
decl_stmt|;
DECL|field|config
specifier|final
name|RemoteConfig
name|config
decl_stmt|;
DECL|field|uri
specifier|final
name|URIish
name|uri
decl_stmt|;
DECL|method|PushOp (final String d, final RemoteConfig c, final URIish u)
name|PushOp
parameter_list|(
specifier|final
name|String
name|d
parameter_list|,
specifier|final
name|RemoteConfig
name|c
parameter_list|,
specifier|final
name|URIish
name|u
parameter_list|)
block|{
name|projectName
operator|=
name|d
expr_stmt|;
name|config
operator|=
name|c
expr_stmt|;
name|uri
operator|=
name|u
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

