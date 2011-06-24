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
DECL|package|com.google.gerrit.server.git
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
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
name|reviewdb
operator|.
name|Project
operator|.
name|NameKey
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
name|reviewdb
operator|.
name|ReviewDb
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
name|project
operator|.
name|NoSuchProjectException
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
name|project
operator|.
name|ProjectControl
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
name|google
operator|.
name|gwtorm
operator|.
name|client
operator|.
name|SchemaFactory
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
name|assistedinject
operator|.
name|Assisted
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
name|JSchException
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
name|errors
operator|.
name|NoRemoteRepositoryException
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
name|errors
operator|.
name|NotSupportedException
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
name|errors
operator|.
name|RepositoryNotFoundException
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
name|errors
operator|.
name|TransportException
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
name|Constants
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
name|NullProgressMonitor
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
name|Ref
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
name|Repository
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
name|transport
operator|.
name|CredentialsProvider
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
name|transport
operator|.
name|FetchConnection
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
name|transport
operator|.
name|PushResult
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
name|transport
operator|.
name|RefSpec
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
name|transport
operator|.
name|RemoteConfig
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
name|transport
operator|.
name|RemoteRefUpdate
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
name|transport
operator|.
name|Transport
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
name|transport
operator|.
name|URIish
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

begin_comment
comment|/**  * A push to remote operation started by {@link ReplicationQueue}.  *<p>  * Instance members are protected by the lock within PushQueue. Callers must  * take that lock to ensure they are working with a current view of the object.  */
end_comment

begin_class
DECL|class|PushOp
class|class
name|PushOp
implements|implements
name|ProjectRunnable
block|{
DECL|interface|Factory
interface|interface
name|Factory
block|{
DECL|method|create (Project.NameKey d, URIish u)
name|PushOp
name|create
parameter_list|(
name|Project
operator|.
name|NameKey
name|d
parameter_list|,
name|URIish
name|u
parameter_list|)
function_decl|;
block|}
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|PushReplication
operator|.
name|log
decl_stmt|;
DECL|field|MIRROR_ALL
specifier|static
specifier|final
name|String
name|MIRROR_ALL
init|=
literal|"..all.."
decl_stmt|;
DECL|field|repoManager
specifier|private
specifier|final
name|GitRepositoryManager
name|repoManager
decl_stmt|;
DECL|field|schema
specifier|private
specifier|final
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|schema
decl_stmt|;
DECL|field|pool
specifier|private
specifier|final
name|PushReplication
operator|.
name|ReplicationConfig
name|pool
decl_stmt|;
DECL|field|config
specifier|private
specifier|final
name|RemoteConfig
name|config
decl_stmt|;
DECL|field|credentialsProvider
specifier|private
specifier|final
name|CredentialsProvider
name|credentialsProvider
decl_stmt|;
DECL|field|tagCache
specifier|private
specifier|final
name|TagCache
name|tagCache
decl_stmt|;
DECL|field|delta
specifier|private
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
specifier|private
specifier|final
name|Project
operator|.
name|NameKey
name|projectName
decl_stmt|;
DECL|field|uri
specifier|private
specifier|final
name|URIish
name|uri
decl_stmt|;
DECL|field|mirror
specifier|private
name|boolean
name|mirror
decl_stmt|;
DECL|field|db
specifier|private
name|Repository
name|db
decl_stmt|;
comment|/**    * It indicates if the current instance is in fact retrying to push.    */
DECL|field|retrying
specifier|private
name|boolean
name|retrying
decl_stmt|;
DECL|field|canceled
specifier|private
name|boolean
name|canceled
decl_stmt|;
annotation|@
name|Inject
DECL|method|PushOp (final GitRepositoryManager grm, final SchemaFactory<ReviewDb> s, final PushReplication.ReplicationConfig p, final RemoteConfig c, final SecureCredentialsProvider.Factory cpFactory, final TagCache tc, @Assisted final Project.NameKey d, @Assisted final URIish u)
name|PushOp
parameter_list|(
specifier|final
name|GitRepositoryManager
name|grm
parameter_list|,
specifier|final
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|s
parameter_list|,
specifier|final
name|PushReplication
operator|.
name|ReplicationConfig
name|p
parameter_list|,
specifier|final
name|RemoteConfig
name|c
parameter_list|,
specifier|final
name|SecureCredentialsProvider
operator|.
name|Factory
name|cpFactory
parameter_list|,
specifier|final
name|TagCache
name|tc
parameter_list|,
annotation|@
name|Assisted
specifier|final
name|Project
operator|.
name|NameKey
name|d
parameter_list|,
annotation|@
name|Assisted
specifier|final
name|URIish
name|u
parameter_list|)
block|{
name|repoManager
operator|=
name|grm
expr_stmt|;
name|schema
operator|=
name|s
expr_stmt|;
name|pool
operator|=
name|p
expr_stmt|;
name|config
operator|=
name|c
expr_stmt|;
name|credentialsProvider
operator|=
name|cpFactory
operator|.
name|create
argument_list|(
name|c
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|tagCache
operator|=
name|tc
expr_stmt|;
name|projectName
operator|=
name|d
expr_stmt|;
name|uri
operator|=
name|u
expr_stmt|;
block|}
DECL|method|isRetrying ()
specifier|public
name|boolean
name|isRetrying
parameter_list|()
block|{
return|return
name|retrying
return|;
block|}
DECL|method|setToRetry ()
specifier|public
name|void
name|setToRetry
parameter_list|()
block|{
name|retrying
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|cancel ()
specifier|public
name|void
name|cancel
parameter_list|()
block|{
name|canceled
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|wasCanceled ()
specifier|public
name|boolean
name|wasCanceled
parameter_list|()
block|{
return|return
name|canceled
return|;
block|}
DECL|method|getURI ()
name|URIish
name|getURI
parameter_list|()
block|{
return|return
name|uri
return|;
block|}
DECL|method|addRef (final String ref)
name|void
name|addRef
parameter_list|(
specifier|final
name|String
name|ref
parameter_list|)
block|{
if|if
condition|(
name|MIRROR_ALL
operator|.
name|equals
argument_list|(
name|ref
argument_list|)
condition|)
block|{
name|delta
operator|.
name|clear
argument_list|()
expr_stmt|;
name|mirror
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|mirror
condition|)
block|{
name|delta
operator|.
name|add
argument_list|(
name|ref
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getRefs ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getRefs
parameter_list|()
block|{
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|refs
decl_stmt|;
if|if
condition|(
name|mirror
condition|)
block|{
name|refs
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|refs
operator|.
name|add
argument_list|(
name|MIRROR_ALL
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|refs
operator|=
name|delta
expr_stmt|;
block|}
return|return
name|refs
return|;
block|}
DECL|method|addRefs (Set<String> refs)
specifier|public
name|void
name|addRefs
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|refs
parameter_list|)
block|{
if|if
condition|(
operator|!
name|mirror
condition|)
block|{
for|for
control|(
name|String
name|ref
range|:
name|refs
control|)
block|{
name|addRef
argument_list|(
name|ref
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
name|PerThreadRequestScope
name|ctx
init|=
operator|new
name|PerThreadRequestScope
argument_list|()
decl_stmt|;
name|PerThreadRequestScope
name|old
init|=
name|PerThreadRequestScope
operator|.
name|set
argument_list|(
name|ctx
argument_list|)
decl_stmt|;
try|try
block|{
name|runPushOperation
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|PerThreadRequestScope
operator|.
name|set
argument_list|(
name|old
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|runPushOperation ()
specifier|private
name|void
name|runPushOperation
parameter_list|()
block|{
comment|// Lock the queue, and remove ourselves, so we can't be modified once
comment|// we start replication (instead a new instance, with the same URI, is
comment|// created and scheduled for a future point in time.)
comment|//
name|pool
operator|.
name|notifyStarting
argument_list|(
name|this
argument_list|)
expr_stmt|;
comment|// It should only verify if it was canceled after calling notifyStarting,
comment|// since the canceled flag would be set locking the queue.
if|if
condition|(
operator|!
name|canceled
condition|)
block|{
try|try
block|{
name|db
operator|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|projectName
argument_list|)
expr_stmt|;
name|runImpl
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryNotFoundException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cannot replicate "
operator|+
name|projectName
operator|+
literal|"; "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoRemoteRepositoryException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cannot replicate to "
operator|+
name|uri
operator|+
literal|"; repository not found"
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
name|uri
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TransportException
name|e
parameter_list|)
block|{
specifier|final
name|Throwable
name|cause
init|=
name|e
operator|.
name|getCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|cause
operator|instanceof
name|JSchException
operator|&&
name|cause
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"UnknownHostKey:"
argument_list|)
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cannot replicate to "
operator|+
name|uri
operator|+
literal|": "
operator|+
name|cause
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cannot replicate to "
operator|+
name|uri
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|// The remote push operation should be retried.
name|pool
operator|.
name|reschedule
argument_list|(
name|this
argument_list|)
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
literal|"Cannot replicate to "
operator|+
name|uri
argument_list|,
name|e
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
literal|"Unexpected error during replication to "
operator|+
name|uri
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
literal|"Unexpected error during replication to "
operator|+
name|uri
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|db
operator|!=
literal|null
condition|)
block|{
name|db
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|(
name|mirror
condition|?
literal|"mirror "
else|:
literal|"push "
operator|)
operator|+
name|uri
return|;
block|}
DECL|method|runImpl ()
specifier|private
name|void
name|runImpl
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Transport
name|tn
init|=
name|Transport
operator|.
name|open
argument_list|(
name|db
argument_list|,
name|uri
argument_list|)
decl_stmt|;
specifier|final
name|PushResult
name|res
decl_stmt|;
try|try
block|{
name|res
operator|=
name|pushVia
argument_list|(
name|tn
argument_list|)
expr_stmt|;
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
if|if
condition|(
literal|"non-fast-forward"
operator|.
name|equals
argument_list|(
name|u
operator|.
name|getMessage
argument_list|()
argument_list|)
condition|)
block|{
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
name|uri
operator|+
literal|", remote rejected non-fast-forward push."
operator|+
literal|"  Check receive.denyNonFastForwards variable in config file"
operator|+
literal|" of destination repository."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
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
block|}
break|break;
block|}
block|}
block|}
DECL|method|pushVia (final Transport tn)
specifier|private
name|PushResult
name|pushVia
parameter_list|(
specifier|final
name|Transport
name|tn
parameter_list|)
throws|throws
name|IOException
throws|,
name|NotSupportedException
throws|,
name|TransportException
block|{
name|tn
operator|.
name|applyConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|tn
operator|.
name|setCredentialsProvider
argument_list|(
name|credentialsProvider
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|RemoteRefUpdate
argument_list|>
name|todo
init|=
name|generateUpdates
argument_list|(
name|tn
argument_list|)
decl_stmt|;
if|if
condition|(
name|todo
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// If we have no commands selected, we have nothing to do.
comment|// Calling JGit at this point would just redo the work we
comment|// already did, and come up with the same answer. Instead
comment|// send back an empty result.
comment|//
return|return
operator|new
name|PushResult
argument_list|()
return|;
block|}
return|return
name|tn
operator|.
name|push
argument_list|(
name|NullProgressMonitor
operator|.
name|INSTANCE
argument_list|,
name|todo
argument_list|)
return|;
block|}
DECL|method|generateUpdates (final Transport tn)
specifier|private
name|List
argument_list|<
name|RemoteRefUpdate
argument_list|>
name|generateUpdates
parameter_list|(
specifier|final
name|Transport
name|tn
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|ProjectControl
name|pc
decl_stmt|;
try|try
block|{
name|pc
operator|=
name|pool
operator|.
name|controlFor
argument_list|(
name|projectName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchProjectException
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
name|Map
argument_list|<
name|String
argument_list|,
name|Ref
argument_list|>
name|local
init|=
name|db
operator|.
name|getAllRefs
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|pc
operator|.
name|allRefsAreVisible
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|mirror
condition|)
block|{
comment|// If we aren't mirroring, reduce the space we need to filter
comment|// to only the references we will update during this operation.
comment|//
name|Map
argument_list|<
name|String
argument_list|,
name|Ref
argument_list|>
name|n
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Ref
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|src
range|:
name|delta
control|)
block|{
name|Ref
name|r
init|=
name|local
operator|.
name|get
argument_list|(
name|src
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|!=
literal|null
condition|)
block|{
name|n
operator|.
name|put
argument_list|(
name|src
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
block|}
name|local
operator|=
name|n
expr_stmt|;
block|}
specifier|final
name|ReviewDb
name|meta
decl_stmt|;
try|try
block|{
name|meta
operator|=
name|schema
operator|.
name|open
argument_list|()
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
literal|"Cannot read database to replicate to "
operator|+
name|projectName
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
try|try
block|{
name|local
operator|=
operator|new
name|VisibleRefFilter
argument_list|(
name|tagCache
argument_list|,
name|db
argument_list|,
name|pc
argument_list|,
name|meta
argument_list|,
literal|true
argument_list|)
operator|.
name|filter
argument_list|(
name|local
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|meta
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|final
name|List
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
if|if
condition|(
name|mirror
condition|)
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Ref
argument_list|>
name|remote
init|=
name|listRemote
argument_list|(
name|tn
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|Ref
name|src
range|:
name|local
operator|.
name|values
argument_list|()
control|)
block|{
specifier|final
name|RefSpec
name|spec
init|=
name|matchSrc
argument_list|(
name|src
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|spec
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Ref
name|dst
init|=
name|remote
operator|.
name|get
argument_list|(
name|spec
operator|.
name|getDestination
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|dst
operator|==
literal|null
operator|||
operator|!
name|src
operator|.
name|getObjectId
argument_list|()
operator|.
name|equals
argument_list|(
name|dst
operator|.
name|getObjectId
argument_list|()
argument_list|)
condition|)
block|{
comment|// Doesn't exist yet, or isn't the same value, request to push.
comment|//
name|send
argument_list|(
name|cmds
argument_list|,
name|spec
argument_list|,
name|src
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
specifier|final
name|Ref
name|ref
range|:
name|remote
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|Constants
operator|.
name|HEAD
operator|.
name|equals
argument_list|(
name|ref
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|RefSpec
name|spec
init|=
name|matchDst
argument_list|(
name|ref
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|spec
operator|!=
literal|null
operator|&&
operator|!
name|local
operator|.
name|containsKey
argument_list|(
name|spec
operator|.
name|getSource
argument_list|()
argument_list|)
condition|)
block|{
comment|// No longer on local side, request removal.
comment|//
name|delete
argument_list|(
name|cmds
argument_list|,
name|spec
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
block|{
for|for
control|(
specifier|final
name|String
name|src
range|:
name|delta
control|)
block|{
specifier|final
name|RefSpec
name|spec
init|=
name|matchSrc
argument_list|(
name|src
argument_list|)
decl_stmt|;
if|if
condition|(
name|spec
operator|!=
literal|null
condition|)
block|{
comment|// If the ref still exists locally, send it, otherwise delete it.
comment|//
name|Ref
name|srcRef
init|=
name|local
operator|.
name|get
argument_list|(
name|src
argument_list|)
decl_stmt|;
if|if
condition|(
name|srcRef
operator|!=
literal|null
condition|)
block|{
name|send
argument_list|(
name|cmds
argument_list|,
name|spec
argument_list|,
name|srcRef
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|delete
argument_list|(
name|cmds
argument_list|,
name|spec
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|cmds
return|;
block|}
DECL|method|listRemote (final Transport tn)
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Ref
argument_list|>
name|listRemote
parameter_list|(
specifier|final
name|Transport
name|tn
parameter_list|)
throws|throws
name|NotSupportedException
throws|,
name|TransportException
block|{
specifier|final
name|FetchConnection
name|fc
init|=
name|tn
operator|.
name|openFetch
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|fc
operator|.
name|getRefsMap
argument_list|()
return|;
block|}
finally|finally
block|{
name|fc
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|matchSrc (final String ref)
specifier|private
name|RefSpec
name|matchSrc
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
name|ref
argument_list|)
condition|)
block|{
return|return
name|s
operator|.
name|expandFromSource
argument_list|(
name|ref
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|matchDst (final String ref)
specifier|private
name|RefSpec
name|matchDst
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
name|matchDestination
argument_list|(
name|ref
argument_list|)
condition|)
block|{
return|return
name|s
operator|.
name|expandFromDestination
argument_list|(
name|ref
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|send (final List<RemoteRefUpdate> cmds, final RefSpec spec, final Ref src)
specifier|private
name|void
name|send
parameter_list|(
specifier|final
name|List
argument_list|<
name|RemoteRefUpdate
argument_list|>
name|cmds
parameter_list|,
specifier|final
name|RefSpec
name|spec
parameter_list|,
specifier|final
name|Ref
name|src
parameter_list|)
throws|throws
name|IOException
block|{
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
name|src
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
DECL|method|delete (final List<RemoteRefUpdate> cmds, final RefSpec spec)
specifier|private
name|void
name|delete
parameter_list|(
specifier|final
name|List
argument_list|<
name|RemoteRefUpdate
argument_list|>
name|cmds
parameter_list|,
specifier|final
name|RefSpec
name|spec
parameter_list|)
throws|throws
name|IOException
block|{
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
operator|(
name|Ref
operator|)
literal|null
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
annotation|@
name|Override
DECL|method|getProjectNameKey ()
specifier|public
name|NameKey
name|getProjectNameKey
parameter_list|()
block|{
return|return
name|projectName
return|;
block|}
annotation|@
name|Override
DECL|method|getRemoteName ()
specifier|public
name|String
name|getRemoteName
parameter_list|()
block|{
return|return
name|config
operator|.
name|getName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hasCustomizedPrint ()
specifier|public
name|boolean
name|hasCustomizedPrint
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

