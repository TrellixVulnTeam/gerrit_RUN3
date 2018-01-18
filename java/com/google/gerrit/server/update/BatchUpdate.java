begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2017 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.update
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|update
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkArgument
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkState
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMultiset
operator|.
name|toImmutableMultiset
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
name|Throwables
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
name|collect
operator|.
name|ImmutableList
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
name|collect
operator|.
name|ImmutableMap
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
name|collect
operator|.
name|ListMultimap
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
name|collect
operator|.
name|MultimapBuilder
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
name|collect
operator|.
name|Multiset
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
name|common
operator|.
name|Nullable
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
name|config
operator|.
name|FactoryModule
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
name|extensions
operator|.
name|restapi
operator|.
name|ResourceNotFoundException
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
name|RestApiException
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
name|Change
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
name|server
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
name|CurrentUser
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
name|account
operator|.
name|AccountState
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
name|git
operator|.
name|validators
operator|.
name|OnSubmitValidators
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
name|notedb
operator|.
name|NotesMigration
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
name|InvalidChangeOperationException
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
name|NoSuchChangeException
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
name|NoSuchRefException
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
name|RequestId
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
name|Module
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
name|Singleton
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
name|sql
operator|.
name|Timestamp
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
name|Collection
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
name|Optional
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimeZone
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
name|ObjectInserter
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
name|PersonIdent
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
name|PushCertificate
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

begin_comment
comment|/**  * Helper for a set of updates that should be applied for a site.  *  *<p>An update operation can be divided into three phases:  *  *<ol>  *<li>Git reference updates  *<li>Database updates  *<li>Post-update steps  *<li>  *</ol>  *  * A single conceptual operation, such as a REST API call or a merge operation, may make multiple  * changes at each step, which all need to be serialized relative to each other. Moreover, for  * consistency,<em>all</em> git ref updates must be performed before<em>any</em> database updates,  * since database updates might refer to newly-created patch set refs. And all post-update steps,  * such as hooks, should run only after all storage mutations have completed.  *  *<p>Depending on the backend used, each step might support batching, for example in a {@code  * BatchRefUpdate} or one or more database transactions. All operations in one phase must complete  * successfully before proceeding to the next phase.  */
end_comment

begin_class
DECL|class|BatchUpdate
specifier|public
specifier|abstract
class|class
name|BatchUpdate
implements|implements
name|AutoCloseable
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
name|BatchUpdate
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|module ()
specifier|public
specifier|static
name|Module
name|module
parameter_list|()
block|{
return|return
operator|new
name|FactoryModule
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|configure
parameter_list|()
block|{
name|factory
argument_list|(
name|ReviewDbBatchUpdate
operator|.
name|AssistedFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|factory
argument_list|(
name|NoteDbBatchUpdate
operator|.
name|AssistedFactory
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
annotation|@
name|Singleton
DECL|class|Factory
specifier|public
specifier|static
class|class
name|Factory
block|{
DECL|field|migration
specifier|private
specifier|final
name|NotesMigration
name|migration
decl_stmt|;
DECL|field|reviewDbBatchUpdateFactory
specifier|private
specifier|final
name|ReviewDbBatchUpdate
operator|.
name|AssistedFactory
name|reviewDbBatchUpdateFactory
decl_stmt|;
DECL|field|noteDbBatchUpdateFactory
specifier|private
specifier|final
name|NoteDbBatchUpdate
operator|.
name|AssistedFactory
name|noteDbBatchUpdateFactory
decl_stmt|;
comment|// TODO(dborowitz): Make this non-injectable to force all callers to use RetryHelper.
annotation|@
name|Inject
DECL|method|Factory ( NotesMigration migration, ReviewDbBatchUpdate.AssistedFactory reviewDbBatchUpdateFactory, NoteDbBatchUpdate.AssistedFactory noteDbBatchUpdateFactory)
name|Factory
parameter_list|(
name|NotesMigration
name|migration
parameter_list|,
name|ReviewDbBatchUpdate
operator|.
name|AssistedFactory
name|reviewDbBatchUpdateFactory
parameter_list|,
name|NoteDbBatchUpdate
operator|.
name|AssistedFactory
name|noteDbBatchUpdateFactory
parameter_list|)
block|{
name|this
operator|.
name|migration
operator|=
name|migration
expr_stmt|;
name|this
operator|.
name|reviewDbBatchUpdateFactory
operator|=
name|reviewDbBatchUpdateFactory
expr_stmt|;
name|this
operator|.
name|noteDbBatchUpdateFactory
operator|=
name|noteDbBatchUpdateFactory
expr_stmt|;
block|}
DECL|method|create ( ReviewDb db, Project.NameKey project, CurrentUser user, Timestamp when)
specifier|public
name|BatchUpdate
name|create
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
name|CurrentUser
name|user
parameter_list|,
name|Timestamp
name|when
parameter_list|)
block|{
if|if
condition|(
name|migration
operator|.
name|disableChangeReviewDb
argument_list|()
condition|)
block|{
return|return
name|noteDbBatchUpdateFactory
operator|.
name|create
argument_list|(
name|db
argument_list|,
name|project
argument_list|,
name|user
argument_list|,
name|when
argument_list|)
return|;
block|}
return|return
name|reviewDbBatchUpdateFactory
operator|.
name|create
argument_list|(
name|db
argument_list|,
name|project
argument_list|,
name|user
argument_list|,
name|when
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|,
literal|"unchecked"
block|}
argument_list|)
DECL|method|execute ( Collection<BatchUpdate> updates, BatchUpdateListener listener, @Nullable RequestId requestId, boolean dryRun)
specifier|public
name|void
name|execute
parameter_list|(
name|Collection
argument_list|<
name|BatchUpdate
argument_list|>
name|updates
parameter_list|,
name|BatchUpdateListener
name|listener
parameter_list|,
annotation|@
name|Nullable
name|RequestId
name|requestId
parameter_list|,
name|boolean
name|dryRun
parameter_list|)
throws|throws
name|UpdateException
throws|,
name|RestApiException
block|{
name|checkNotNull
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|checkDifferentProject
argument_list|(
name|updates
argument_list|)
expr_stmt|;
comment|// It's safe to downcast all members of the input collection in this case, because the only
comment|// way a caller could have gotten any BatchUpdates in the first place is to call the create
comment|// method above, which always returns instances of the type we expect. Just to be safe,
comment|// copy them into an ImmutableList so there is no chance the callee can pollute the input
comment|// collection.
if|if
condition|(
name|migration
operator|.
name|disableChangeReviewDb
argument_list|()
condition|)
block|{
name|ImmutableList
argument_list|<
name|NoteDbBatchUpdate
argument_list|>
name|noteDbUpdates
init|=
operator|(
name|ImmutableList
operator|)
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|updates
argument_list|)
decl_stmt|;
name|NoteDbBatchUpdate
operator|.
name|execute
argument_list|(
name|noteDbUpdates
argument_list|,
name|listener
argument_list|,
name|requestId
argument_list|,
name|dryRun
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ImmutableList
argument_list|<
name|ReviewDbBatchUpdate
argument_list|>
name|reviewDbUpdates
init|=
operator|(
name|ImmutableList
operator|)
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|updates
argument_list|)
decl_stmt|;
name|ReviewDbBatchUpdate
operator|.
name|execute
argument_list|(
name|reviewDbUpdates
argument_list|,
name|listener
argument_list|,
name|requestId
argument_list|,
name|dryRun
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|checkDifferentProject (Collection<BatchUpdate> updates)
specifier|private
specifier|static
name|void
name|checkDifferentProject
parameter_list|(
name|Collection
argument_list|<
name|BatchUpdate
argument_list|>
name|updates
parameter_list|)
block|{
name|Multiset
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
name|projectCounts
init|=
name|updates
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|u
lambda|->
name|u
operator|.
name|project
argument_list|)
operator|.
name|collect
argument_list|(
name|toImmutableMultiset
argument_list|()
argument_list|)
decl_stmt|;
name|checkArgument
argument_list|(
name|projectCounts
operator|.
name|entrySet
argument_list|()
operator|.
name|size
argument_list|()
operator|==
name|updates
operator|.
name|size
argument_list|()
argument_list|,
literal|"updates must all be for different projects, got: %s"
argument_list|,
name|projectCounts
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setRequestIds ( Collection<? extends BatchUpdate> updates, @Nullable RequestId requestId)
specifier|static
name|void
name|setRequestIds
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|BatchUpdate
argument_list|>
name|updates
parameter_list|,
annotation|@
name|Nullable
name|RequestId
name|requestId
parameter_list|)
block|{
if|if
condition|(
name|requestId
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|BatchUpdate
name|u
range|:
name|updates
control|)
block|{
name|checkArgument
argument_list|(
name|u
operator|.
name|requestId
operator|==
literal|null
operator|||
name|u
operator|.
name|requestId
operator|==
name|requestId
argument_list|,
literal|"refusing to overwrite RequestId %s in update with %s"
argument_list|,
name|u
operator|.
name|requestId
argument_list|,
name|requestId
argument_list|)
expr_stmt|;
name|u
operator|.
name|setRequestId
argument_list|(
name|requestId
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getOrder (Collection<? extends BatchUpdate> updates, BatchUpdateListener listener)
specifier|static
name|Order
name|getOrder
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|BatchUpdate
argument_list|>
name|updates
parameter_list|,
name|BatchUpdateListener
name|listener
parameter_list|)
block|{
name|Order
name|o
init|=
literal|null
decl_stmt|;
for|for
control|(
name|BatchUpdate
name|u
range|:
name|updates
control|)
block|{
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
name|o
operator|=
name|u
operator|.
name|order
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|u
operator|.
name|order
operator|!=
name|o
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot mix execution orders"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|o
operator|!=
name|Order
operator|.
name|REPO_BEFORE_DB
condition|)
block|{
name|checkArgument
argument_list|(
name|listener
operator|==
name|BatchUpdateListener
operator|.
name|NONE
argument_list|,
literal|"BatchUpdateListener not supported for order %s"
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
return|return
name|o
return|;
block|}
DECL|method|getUpdateChangesInParallel (Collection<? extends BatchUpdate> updates)
specifier|static
name|boolean
name|getUpdateChangesInParallel
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|BatchUpdate
argument_list|>
name|updates
parameter_list|)
block|{
name|checkArgument
argument_list|(
operator|!
name|updates
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|Boolean
name|p
init|=
literal|null
decl_stmt|;
for|for
control|(
name|BatchUpdate
name|u
range|:
name|updates
control|)
block|{
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
name|p
operator|=
name|u
operator|.
name|updateChangesInParallel
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|u
operator|.
name|updateChangesInParallel
operator|!=
name|p
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot mix parallel and non-parallel operations"
argument_list|)
throw|;
block|}
block|}
comment|// Properly implementing this would involve hoisting the parallel loop up
comment|// even further. As of this writing, the only user is ReceiveCommits,
comment|// which only executes a single BatchUpdate at a time. So bail for now.
name|checkArgument
argument_list|(
operator|!
name|p
operator|||
name|updates
operator|.
name|size
argument_list|()
operator|<=
literal|1
argument_list|,
literal|"cannot execute ChangeOps in parallel with more than 1 BatchUpdate"
argument_list|)
expr_stmt|;
return|return
name|p
return|;
block|}
DECL|method|wrapAndThrowException (Exception e)
specifier|static
name|void
name|wrapAndThrowException
parameter_list|(
name|Exception
name|e
parameter_list|)
throws|throws
name|UpdateException
throws|,
name|RestApiException
block|{
name|Throwables
operator|.
name|throwIfUnchecked
argument_list|(
name|e
argument_list|)
expr_stmt|;
comment|// Propagate REST API exceptions thrown by operations; they commonly throw exceptions like
comment|// ResourceConflictException to indicate an atomic update failure.
name|Throwables
operator|.
name|throwIfInstanceOf
argument_list|(
name|e
argument_list|,
name|UpdateException
operator|.
name|class
argument_list|)
expr_stmt|;
name|Throwables
operator|.
name|throwIfInstanceOf
argument_list|(
name|e
argument_list|,
name|RestApiException
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// Convert other common non-REST exception types with user-visible messages to corresponding
comment|// REST exception types
if|if
condition|(
name|e
operator|instanceof
name|InvalidChangeOperationException
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|e
operator|instanceof
name|NoSuchChangeException
operator|||
name|e
operator|instanceof
name|NoSuchRefException
operator|||
name|e
operator|instanceof
name|NoSuchProjectException
condition|)
block|{
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
comment|// Otherwise, wrap in a generic UpdateException, which does not include a user-visible message.
throw|throw
operator|new
name|UpdateException
argument_list|(
name|e
argument_list|)
throw|;
block|}
DECL|field|repoManager
specifier|protected
name|GitRepositoryManager
name|repoManager
decl_stmt|;
DECL|field|project
specifier|protected
specifier|final
name|Project
operator|.
name|NameKey
name|project
decl_stmt|;
DECL|field|user
specifier|protected
specifier|final
name|CurrentUser
name|user
decl_stmt|;
DECL|field|when
specifier|protected
specifier|final
name|Timestamp
name|when
decl_stmt|;
DECL|field|tz
specifier|protected
specifier|final
name|TimeZone
name|tz
decl_stmt|;
DECL|field|ops
specifier|protected
specifier|final
name|ListMultimap
argument_list|<
name|Change
operator|.
name|Id
argument_list|,
name|BatchUpdateOp
argument_list|>
name|ops
init|=
name|MultimapBuilder
operator|.
name|linkedHashKeys
argument_list|()
operator|.
name|arrayListValues
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
DECL|field|newChanges
specifier|protected
specifier|final
name|Map
argument_list|<
name|Change
operator|.
name|Id
argument_list|,
name|Change
argument_list|>
name|newChanges
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|repoOnlyOps
specifier|protected
specifier|final
name|List
argument_list|<
name|RepoOnlyOp
argument_list|>
name|repoOnlyOps
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|repoView
specifier|protected
name|RepoView
name|repoView
decl_stmt|;
DECL|field|batchRefUpdate
specifier|protected
name|BatchRefUpdate
name|batchRefUpdate
decl_stmt|;
DECL|field|order
specifier|protected
name|Order
name|order
decl_stmt|;
DECL|field|onSubmitValidators
specifier|protected
name|OnSubmitValidators
name|onSubmitValidators
decl_stmt|;
DECL|field|requestId
specifier|protected
name|RequestId
name|requestId
decl_stmt|;
DECL|field|pushCert
specifier|protected
name|PushCertificate
name|pushCert
decl_stmt|;
DECL|field|refLogMessage
specifier|protected
name|String
name|refLogMessage
decl_stmt|;
DECL|field|updateChangesInParallel
specifier|private
name|boolean
name|updateChangesInParallel
decl_stmt|;
DECL|method|BatchUpdate ( GitRepositoryManager repoManager, PersonIdent serverIdent, Project.NameKey project, CurrentUser user, Timestamp when)
specifier|protected
name|BatchUpdate
parameter_list|(
name|GitRepositoryManager
name|repoManager
parameter_list|,
name|PersonIdent
name|serverIdent
parameter_list|,
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
name|CurrentUser
name|user
parameter_list|,
name|Timestamp
name|when
parameter_list|)
block|{
name|this
operator|.
name|repoManager
operator|=
name|repoManager
expr_stmt|;
name|this
operator|.
name|project
operator|=
name|project
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|when
operator|=
name|when
expr_stmt|;
name|tz
operator|=
name|serverIdent
operator|.
name|getTimeZone
argument_list|()
expr_stmt|;
name|order
operator|=
name|Order
operator|.
name|REPO_BEFORE_DB
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|repoView
operator|!=
literal|null
condition|)
block|{
name|repoView
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|execute (BatchUpdateListener listener)
specifier|public
specifier|abstract
name|void
name|execute
parameter_list|(
name|BatchUpdateListener
name|listener
parameter_list|)
throws|throws
name|UpdateException
throws|,
name|RestApiException
function_decl|;
DECL|method|execute ()
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|UpdateException
throws|,
name|RestApiException
block|{
name|execute
argument_list|(
name|BatchUpdateListener
operator|.
name|NONE
argument_list|)
expr_stmt|;
block|}
DECL|method|newContext ()
specifier|protected
specifier|abstract
name|Context
name|newContext
parameter_list|()
function_decl|;
DECL|method|setRequestId (RequestId requestId)
specifier|public
name|BatchUpdate
name|setRequestId
parameter_list|(
name|RequestId
name|requestId
parameter_list|)
block|{
name|this
operator|.
name|requestId
operator|=
name|requestId
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setRepository (Repository repo, RevWalk revWalk, ObjectInserter inserter)
specifier|public
name|BatchUpdate
name|setRepository
parameter_list|(
name|Repository
name|repo
parameter_list|,
name|RevWalk
name|revWalk
parameter_list|,
name|ObjectInserter
name|inserter
parameter_list|)
block|{
name|checkState
argument_list|(
name|this
operator|.
name|repoView
operator|==
literal|null
argument_list|,
literal|"repo already set"
argument_list|)
expr_stmt|;
name|repoView
operator|=
operator|new
name|RepoView
argument_list|(
name|repo
argument_list|,
name|revWalk
argument_list|,
name|inserter
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setPushCertificate (@ullable PushCertificate pushCert)
specifier|public
name|BatchUpdate
name|setPushCertificate
parameter_list|(
annotation|@
name|Nullable
name|PushCertificate
name|pushCert
parameter_list|)
block|{
name|this
operator|.
name|pushCert
operator|=
name|pushCert
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setRefLogMessage (@ullable String refLogMessage)
specifier|public
name|BatchUpdate
name|setRefLogMessage
parameter_list|(
annotation|@
name|Nullable
name|String
name|refLogMessage
parameter_list|)
block|{
name|this
operator|.
name|refLogMessage
operator|=
name|refLogMessage
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setOrder (Order order)
specifier|public
name|BatchUpdate
name|setOrder
parameter_list|(
name|Order
name|order
parameter_list|)
block|{
name|this
operator|.
name|order
operator|=
name|order
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Add a validation step for intended ref operations, which will be performed at the end of {@link    * RepoOnlyOp#updateRepo(RepoContext)} step.    */
DECL|method|setOnSubmitValidators (OnSubmitValidators onSubmitValidators)
specifier|public
name|BatchUpdate
name|setOnSubmitValidators
parameter_list|(
name|OnSubmitValidators
name|onSubmitValidators
parameter_list|)
block|{
name|this
operator|.
name|onSubmitValidators
operator|=
name|onSubmitValidators
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Execute {@link BatchUpdateOp#updateChange(ChangeContext)} in parallel for each change.    *    *<p>This improves performance of writing to multiple changes in separate ReviewDb transactions.    * When only NoteDb is used, updates to all changes are written in a single batch ref update, so    * parallelization is not used and this option is ignored.    */
DECL|method|updateChangesInParallel ()
specifier|public
name|BatchUpdate
name|updateChangesInParallel
parameter_list|()
block|{
name|this
operator|.
name|updateChangesInParallel
operator|=
literal|true
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|initRepository ()
specifier|protected
name|void
name|initRepository
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|repoView
operator|==
literal|null
condition|)
block|{
name|repoView
operator|=
operator|new
name|RepoView
argument_list|(
name|repoManager
argument_list|,
name|project
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getRepoView ()
specifier|protected
name|RepoView
name|getRepoView
parameter_list|()
throws|throws
name|IOException
block|{
name|initRepository
argument_list|()
expr_stmt|;
return|return
name|repoView
return|;
block|}
DECL|method|getUser ()
specifier|protected
name|CurrentUser
name|getUser
parameter_list|()
block|{
return|return
name|user
return|;
block|}
DECL|method|getAccount ()
specifier|protected
name|Optional
argument_list|<
name|AccountState
argument_list|>
name|getAccount
parameter_list|()
block|{
return|return
name|user
operator|.
name|isIdentifiedUser
argument_list|()
condition|?
name|Optional
operator|.
name|of
argument_list|(
name|user
operator|.
name|asIdentifiedUser
argument_list|()
operator|.
name|state
argument_list|()
argument_list|)
else|:
name|Optional
operator|.
name|empty
argument_list|()
return|;
block|}
DECL|method|getRevWalk ()
specifier|protected
name|RevWalk
name|getRevWalk
parameter_list|()
throws|throws
name|IOException
block|{
name|initRepository
argument_list|()
expr_stmt|;
return|return
name|repoView
operator|.
name|getRevWalk
argument_list|()
return|;
block|}
DECL|method|getRefUpdates ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|ReceiveCommand
argument_list|>
name|getRefUpdates
parameter_list|()
block|{
return|return
name|repoView
operator|!=
literal|null
condition|?
name|repoView
operator|.
name|getCommands
argument_list|()
operator|.
name|getCommands
argument_list|()
else|:
name|ImmutableMap
operator|.
name|of
argument_list|()
return|;
block|}
DECL|method|addOp (Change.Id id, BatchUpdateOp op)
specifier|public
name|BatchUpdate
name|addOp
parameter_list|(
name|Change
operator|.
name|Id
name|id
parameter_list|,
name|BatchUpdateOp
name|op
parameter_list|)
block|{
name|checkArgument
argument_list|(
operator|!
operator|(
name|op
operator|instanceof
name|InsertChangeOp
operator|)
argument_list|,
literal|"use insertChange"
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|op
argument_list|)
expr_stmt|;
name|ops
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|op
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|addRepoOnlyOp (RepoOnlyOp op)
specifier|public
name|BatchUpdate
name|addRepoOnlyOp
parameter_list|(
name|RepoOnlyOp
name|op
parameter_list|)
block|{
name|checkArgument
argument_list|(
operator|!
operator|(
name|op
operator|instanceof
name|BatchUpdateOp
operator|)
argument_list|,
literal|"use addOp()"
argument_list|)
expr_stmt|;
name|repoOnlyOps
operator|.
name|add
argument_list|(
name|op
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|insertChange (InsertChangeOp op)
specifier|public
name|BatchUpdate
name|insertChange
parameter_list|(
name|InsertChangeOp
name|op
parameter_list|)
throws|throws
name|IOException
block|{
name|Context
name|ctx
init|=
name|newContext
argument_list|()
decl_stmt|;
name|Change
name|c
init|=
name|op
operator|.
name|createChange
argument_list|(
name|ctx
argument_list|)
decl_stmt|;
name|checkArgument
argument_list|(
operator|!
name|newChanges
operator|.
name|containsKey
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|,
literal|"only one op allowed to create change %s"
argument_list|,
name|c
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|newChanges
operator|.
name|put
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|ops
operator|.
name|get
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
literal|0
argument_list|,
name|op
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|logDebug (String msg, Throwable t)
specifier|protected
name|void
name|logDebug
parameter_list|(
name|String
name|msg
parameter_list|,
name|Throwable
name|t
parameter_list|)
block|{
if|if
condition|(
name|requestId
operator|!=
literal|null
operator|&&
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
name|requestId
operator|+
name|msg
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|logDebug (String msg, Object... args)
specifier|protected
name|void
name|logDebug
parameter_list|(
name|String
name|msg
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
comment|// Only log if there is a requestId assigned, since those are the
comment|// expensive/complicated requests like MergeOp. Doing it every time would be
comment|// noisy.
if|if
condition|(
name|requestId
operator|!=
literal|null
operator|&&
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
name|requestId
operator|+
name|msg
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

