begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2016 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.project
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|project
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|String
operator|.
name|format
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|transport
operator|.
name|ReceiveCommand
operator|.
name|Type
operator|.
name|DELETE
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
name|restapi
operator|.
name|ResourceConflictException
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
name|client
operator|.
name|Branch
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
name|IdentifiedUser
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
name|extensions
operator|.
name|events
operator|.
name|GitReferenceUpdated
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
name|GitRepositoryManager
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
name|query
operator|.
name|change
operator|.
name|InternalChangeQuery
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
name|server
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
name|inject
operator|.
name|Provider
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
name|google
operator|.
name|inject
operator|.
name|assistedinject
operator|.
name|AssistedInject
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
name|LockFailedException
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
name|BatchRefUpdate
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
name|ObjectId
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
name|RefUpdate
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
name|revwalk
operator|.
name|RevWalk
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
name|ReceiveCommand
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
name|ReceiveCommand
operator|.
name|Result
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
name|List
import|;
end_import

begin_class
DECL|class|DeleteRef
specifier|public
class|class
name|DeleteRef
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
name|DeleteRef
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|MAX_LOCK_FAILURE_CALLS
specifier|private
specifier|static
specifier|final
name|int
name|MAX_LOCK_FAILURE_CALLS
init|=
literal|10
decl_stmt|;
DECL|field|SLEEP_ON_LOCK_FAILURE_MS
specifier|private
specifier|static
specifier|final
name|long
name|SLEEP_ON_LOCK_FAILURE_MS
init|=
literal|15
decl_stmt|;
DECL|field|identifiedUser
specifier|private
specifier|final
name|Provider
argument_list|<
name|IdentifiedUser
argument_list|>
name|identifiedUser
decl_stmt|;
DECL|field|repoManager
specifier|private
specifier|final
name|GitRepositoryManager
name|repoManager
decl_stmt|;
DECL|field|referenceUpdated
specifier|private
specifier|final
name|GitReferenceUpdated
name|referenceUpdated
decl_stmt|;
DECL|field|refDeletionValidator
specifier|private
specifier|final
name|RefValidationHelper
name|refDeletionValidator
decl_stmt|;
DECL|field|queryProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|InternalChangeQuery
argument_list|>
name|queryProvider
decl_stmt|;
DECL|field|resource
specifier|private
specifier|final
name|ProjectResource
name|resource
decl_stmt|;
DECL|field|refsToDelete
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|refsToDelete
decl_stmt|;
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (ProjectResource r)
specifier|public
name|DeleteRef
name|create
parameter_list|(
name|ProjectResource
name|r
parameter_list|)
function_decl|;
block|}
annotation|@
name|AssistedInject
DECL|method|DeleteRef (Provider<IdentifiedUser> identifiedUser, GitRepositoryManager repoManager, GitReferenceUpdated referenceUpdated, RefValidationHelper.Factory refDeletionValidatorFactory, Provider<InternalChangeQuery> queryProvider, @Assisted ProjectResource resource)
name|DeleteRef
parameter_list|(
name|Provider
argument_list|<
name|IdentifiedUser
argument_list|>
name|identifiedUser
parameter_list|,
name|GitRepositoryManager
name|repoManager
parameter_list|,
name|GitReferenceUpdated
name|referenceUpdated
parameter_list|,
name|RefValidationHelper
operator|.
name|Factory
name|refDeletionValidatorFactory
parameter_list|,
name|Provider
argument_list|<
name|InternalChangeQuery
argument_list|>
name|queryProvider
parameter_list|,
annotation|@
name|Assisted
name|ProjectResource
name|resource
parameter_list|)
block|{
name|this
operator|.
name|identifiedUser
operator|=
name|identifiedUser
expr_stmt|;
name|this
operator|.
name|repoManager
operator|=
name|repoManager
expr_stmt|;
name|this
operator|.
name|referenceUpdated
operator|=
name|referenceUpdated
expr_stmt|;
name|this
operator|.
name|refDeletionValidator
operator|=
name|refDeletionValidatorFactory
operator|.
name|create
argument_list|(
name|DELETE
argument_list|)
expr_stmt|;
name|this
operator|.
name|queryProvider
operator|=
name|queryProvider
expr_stmt|;
name|this
operator|.
name|resource
operator|=
name|resource
expr_stmt|;
name|this
operator|.
name|refsToDelete
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
DECL|method|ref (String ref)
specifier|public
name|DeleteRef
name|ref
parameter_list|(
name|String
name|ref
parameter_list|)
block|{
name|this
operator|.
name|refsToDelete
operator|.
name|add
argument_list|(
name|ref
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|refs (List<String> refs)
specifier|public
name|DeleteRef
name|refs
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|refs
parameter_list|)
block|{
name|this
operator|.
name|refsToDelete
operator|.
name|addAll
argument_list|(
name|refs
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|delete ()
specifier|public
name|void
name|delete
parameter_list|()
throws|throws
name|OrmException
throws|,
name|IOException
throws|,
name|ResourceConflictException
block|{
if|if
condition|(
operator|!
name|refsToDelete
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
try|try
init|(
name|Repository
name|r
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|resource
operator|.
name|getNameKey
argument_list|()
argument_list|)
init|)
block|{
if|if
condition|(
name|refsToDelete
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|deleteSingleRef
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|deleteMultipleRefs
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|deleteSingleRef (Repository r)
specifier|private
name|void
name|deleteSingleRef
parameter_list|(
name|Repository
name|r
parameter_list|)
throws|throws
name|IOException
throws|,
name|ResourceConflictException
block|{
name|String
name|ref
init|=
name|refsToDelete
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|RefUpdate
operator|.
name|Result
name|result
decl_stmt|;
name|RefUpdate
name|u
init|=
name|r
operator|.
name|updateRef
argument_list|(
name|ref
argument_list|)
decl_stmt|;
name|u
operator|.
name|setForceUpdate
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|refDeletionValidator
operator|.
name|validateRefOperation
argument_list|(
name|ref
argument_list|,
name|identifiedUser
operator|.
name|get
argument_list|()
argument_list|,
name|u
argument_list|)
expr_stmt|;
name|int
name|remainingLockFailureCalls
init|=
name|MAX_LOCK_FAILURE_CALLS
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
try|try
block|{
name|result
operator|=
name|u
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LockFailedException
name|e
parameter_list|)
block|{
name|result
operator|=
name|RefUpdate
operator|.
name|Result
operator|.
name|LOCK_FAILURE
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
literal|"Cannot delete "
operator|+
name|ref
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
if|if
condition|(
name|result
operator|==
name|RefUpdate
operator|.
name|Result
operator|.
name|LOCK_FAILURE
operator|&&
operator|--
name|remainingLockFailureCalls
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|SLEEP_ON_LOCK_FAILURE_MS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
comment|// ignore
block|}
block|}
else|else
block|{
break|break;
block|}
block|}
switch|switch
condition|(
name|result
condition|)
block|{
case|case
name|NEW
case|:
case|case
name|NO_CHANGE
case|:
case|case
name|FAST_FORWARD
case|:
case|case
name|FORCED
case|:
name|referenceUpdated
operator|.
name|fire
argument_list|(
name|resource
operator|.
name|getNameKey
argument_list|()
argument_list|,
name|u
argument_list|,
name|ReceiveCommand
operator|.
name|Type
operator|.
name|DELETE
argument_list|,
name|identifiedUser
operator|.
name|get
argument_list|()
operator|.
name|getAccount
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|REJECTED_CURRENT_BRANCH
case|:
name|log
operator|.
name|error
argument_list|(
literal|"Cannot delete "
operator|+
name|ref
operator|+
literal|": "
operator|+
name|result
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
literal|"cannot delete current branch"
argument_list|)
throw|;
case|case
name|IO_FAILURE
case|:
case|case
name|LOCK_FAILURE
case|:
case|case
name|NOT_ATTEMPTED
case|:
case|case
name|REJECTED
case|:
case|case
name|RENAMED
case|:
default|default:
name|log
operator|.
name|error
argument_list|(
literal|"Cannot delete "
operator|+
name|ref
operator|+
literal|": "
operator|+
name|result
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
literal|"cannot delete: "
operator|+
name|result
operator|.
name|name
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|method|deleteMultipleRefs (Repository r)
specifier|private
name|void
name|deleteMultipleRefs
parameter_list|(
name|Repository
name|r
parameter_list|)
throws|throws
name|OrmException
throws|,
name|IOException
throws|,
name|ResourceConflictException
block|{
name|BatchRefUpdate
name|batchUpdate
init|=
name|r
operator|.
name|getRefDatabase
argument_list|()
operator|.
name|newBatchUpdate
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|ref
range|:
name|refsToDelete
control|)
block|{
name|batchUpdate
operator|.
name|addCommand
argument_list|(
name|createDeleteCommand
argument_list|(
name|resource
argument_list|,
name|r
argument_list|,
name|ref
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
init|(
name|RevWalk
name|rw
init|=
operator|new
name|RevWalk
argument_list|(
name|r
argument_list|)
init|)
block|{
name|batchUpdate
operator|.
name|execute
argument_list|(
name|rw
argument_list|,
name|NullProgressMonitor
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
name|StringBuilder
name|errorMessages
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|ReceiveCommand
name|command
range|:
name|batchUpdate
operator|.
name|getCommands
argument_list|()
control|)
block|{
if|if
condition|(
name|command
operator|.
name|getResult
argument_list|()
operator|==
name|Result
operator|.
name|OK
condition|)
block|{
name|postDeletion
argument_list|(
name|resource
argument_list|,
name|command
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|appendAndLogErrorMessage
argument_list|(
name|errorMessages
argument_list|,
name|command
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|errorMessages
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
name|errorMessages
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|method|createDeleteCommand (ProjectResource project, Repository r, String refName)
specifier|private
name|ReceiveCommand
name|createDeleteCommand
parameter_list|(
name|ProjectResource
name|project
parameter_list|,
name|Repository
name|r
parameter_list|,
name|String
name|refName
parameter_list|)
throws|throws
name|OrmException
throws|,
name|IOException
throws|,
name|ResourceConflictException
block|{
name|Ref
name|ref
init|=
name|r
operator|.
name|getRefDatabase
argument_list|()
operator|.
name|getRef
argument_list|(
name|refName
argument_list|)
decl_stmt|;
name|ReceiveCommand
name|command
decl_stmt|;
if|if
condition|(
name|ref
operator|==
literal|null
condition|)
block|{
name|command
operator|=
operator|new
name|ReceiveCommand
argument_list|(
name|ObjectId
operator|.
name|zeroId
argument_list|()
argument_list|,
name|ObjectId
operator|.
name|zeroId
argument_list|()
argument_list|,
name|refName
argument_list|)
expr_stmt|;
name|command
operator|.
name|setResult
argument_list|(
name|Result
operator|.
name|REJECTED_OTHER_REASON
argument_list|,
literal|"it doesn't exist or you do not have permission to delete it"
argument_list|)
expr_stmt|;
return|return
name|command
return|;
block|}
name|command
operator|=
operator|new
name|ReceiveCommand
argument_list|(
name|ref
operator|.
name|getObjectId
argument_list|()
argument_list|,
name|ObjectId
operator|.
name|zeroId
argument_list|()
argument_list|,
name|ref
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|project
operator|.
name|getControl
argument_list|()
operator|.
name|controlForRef
argument_list|(
name|refName
argument_list|)
operator|.
name|canDelete
argument_list|()
condition|)
block|{
name|command
operator|.
name|setResult
argument_list|(
name|Result
operator|.
name|REJECTED_OTHER_REASON
argument_list|,
literal|"it doesn't exist or you do not have permission to delete it"
argument_list|)
expr_stmt|;
block|}
comment|//TODO: this check should not be done when deletion of tags is added
name|Branch
operator|.
name|NameKey
name|branchKey
init|=
operator|new
name|Branch
operator|.
name|NameKey
argument_list|(
name|project
operator|.
name|getNameKey
argument_list|()
argument_list|,
name|ref
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|queryProvider
operator|.
name|get
argument_list|()
operator|.
name|setLimit
argument_list|(
literal|1
argument_list|)
operator|.
name|byBranchOpen
argument_list|(
name|branchKey
argument_list|)
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|command
operator|.
name|setResult
argument_list|(
name|Result
operator|.
name|REJECTED_OTHER_REASON
argument_list|,
literal|"it has open changes"
argument_list|)
expr_stmt|;
block|}
name|RefUpdate
name|u
init|=
name|r
operator|.
name|updateRef
argument_list|(
name|refName
argument_list|)
decl_stmt|;
name|u
operator|.
name|setForceUpdate
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|refDeletionValidator
operator|.
name|validateRefOperation
argument_list|(
name|project
operator|.
name|getName
argument_list|()
argument_list|,
name|identifiedUser
operator|.
name|get
argument_list|()
argument_list|,
name|u
argument_list|)
expr_stmt|;
return|return
name|command
return|;
block|}
DECL|method|appendAndLogErrorMessage (StringBuilder errorMessages, ReceiveCommand cmd)
specifier|private
name|void
name|appendAndLogErrorMessage
parameter_list|(
name|StringBuilder
name|errorMessages
parameter_list|,
name|ReceiveCommand
name|cmd
parameter_list|)
block|{
name|String
name|msg
init|=
literal|null
decl_stmt|;
switch|switch
condition|(
name|cmd
operator|.
name|getResult
argument_list|()
condition|)
block|{
case|case
name|REJECTED_CURRENT_BRANCH
case|:
name|msg
operator|=
name|format
argument_list|(
literal|"Cannot delete %s: it is the current branch"
argument_list|,
name|cmd
operator|.
name|getRefName
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|REJECTED_OTHER_REASON
case|:
name|msg
operator|=
name|format
argument_list|(
literal|"Cannot delete %s: %s"
argument_list|,
name|cmd
operator|.
name|getRefName
argument_list|()
argument_list|,
name|cmd
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|LOCK_FAILURE
case|:
case|case
name|NOT_ATTEMPTED
case|:
case|case
name|OK
case|:
case|case
name|REJECTED_MISSING_OBJECT
case|:
case|case
name|REJECTED_NOCREATE
case|:
case|case
name|REJECTED_NODELETE
case|:
case|case
name|REJECTED_NONFASTFORWARD
case|:
default|default:
name|msg
operator|=
name|format
argument_list|(
literal|"Cannot delete %s: %s"
argument_list|,
name|cmd
operator|.
name|getRefName
argument_list|()
argument_list|,
name|cmd
operator|.
name|getResult
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
name|log
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|errorMessages
operator|.
name|append
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|errorMessages
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
DECL|method|postDeletion (ProjectResource project, ReceiveCommand cmd)
specifier|private
name|void
name|postDeletion
parameter_list|(
name|ProjectResource
name|project
parameter_list|,
name|ReceiveCommand
name|cmd
parameter_list|)
block|{
name|referenceUpdated
operator|.
name|fire
argument_list|(
name|project
operator|.
name|getNameKey
argument_list|()
argument_list|,
name|cmd
argument_list|,
name|identifiedUser
operator|.
name|get
argument_list|()
operator|.
name|getAccount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

