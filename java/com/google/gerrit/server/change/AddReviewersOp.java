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
DECL|package|com.google.gerrit.server.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|change
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
name|ImmutableList
operator|.
name|toImmutableList
import|;
end_import

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
name|client
operator|.
name|ReviewerState
operator|.
name|CC
import|;
end_import

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
name|client
operator|.
name|ReviewerState
operator|.
name|REVIEWER
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
operator|.
name|toList
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|auto
operator|.
name|value
operator|.
name|AutoValue
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
name|ImmutableSet
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
name|Lists
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
name|Streams
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
name|api
operator|.
name|changes
operator|.
name|NotifyHandling
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
name|api
operator|.
name|changes
operator|.
name|RecipientType
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
name|client
operator|.
name|ReviewerState
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
name|mail
operator|.
name|Address
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
name|Account
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
name|PatchSet
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
name|PatchSetApproval
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
name|ApprovalsUtil
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
name|PatchSetUtil
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
name|AccountCache
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
name|extensions
operator|.
name|events
operator|.
name|ReviewerAdded
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
name|notedb
operator|.
name|ReviewerStateInternal
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
name|ProjectCache
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
name|update
operator|.
name|BatchUpdateOp
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
name|update
operator|.
name|ChangeContext
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
name|update
operator|.
name|Context
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
name|Collection
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
name|Set
import|;
end_import

begin_class
DECL|class|AddReviewersOp
specifier|public
class|class
name|AddReviewersOp
implements|implements
name|BatchUpdateOp
block|{
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
comment|/**      * Create a new op.      *      *<p>Users may be added by account or by email addresses, as determined by {@code accountIds}      * and {@code addresses}. The reviewer state for both accounts and email addresses is determined      * by {@code state}.      *      * @param accountIds account IDs to add.      * @param addresses email addresses to add.      * @param state resulting reviewer state.      * @param notify notification handling.      * @param accountsToNotify additional accounts to notify.      * @return batch update operation.      */
DECL|method|create ( Set<Account.Id> accountIds, Collection<Address> addresses, ReviewerState state, @Nullable NotifyHandling notify, ListMultimap<RecipientType, Account.Id> accountsToNotify)
name|AddReviewersOp
name|create
parameter_list|(
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|accountIds
parameter_list|,
name|Collection
argument_list|<
name|Address
argument_list|>
name|addresses
parameter_list|,
name|ReviewerState
name|state
parameter_list|,
annotation|@
name|Nullable
name|NotifyHandling
name|notify
parameter_list|,
name|ListMultimap
argument_list|<
name|RecipientType
argument_list|,
name|Account
operator|.
name|Id
argument_list|>
name|accountsToNotify
parameter_list|)
function_decl|;
block|}
annotation|@
name|AutoValue
DECL|class|Result
specifier|public
specifier|abstract
specifier|static
class|class
name|Result
block|{
DECL|method|addedReviewers ()
specifier|public
specifier|abstract
name|ImmutableList
argument_list|<
name|PatchSetApproval
argument_list|>
name|addedReviewers
parameter_list|()
function_decl|;
DECL|method|addedReviewersByEmail ()
specifier|public
specifier|abstract
name|ImmutableList
argument_list|<
name|Address
argument_list|>
name|addedReviewersByEmail
parameter_list|()
function_decl|;
DECL|method|addedCCs ()
specifier|public
specifier|abstract
name|ImmutableList
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|addedCCs
parameter_list|()
function_decl|;
DECL|method|addedCCsByEmail ()
specifier|public
specifier|abstract
name|ImmutableList
argument_list|<
name|Address
argument_list|>
name|addedCCsByEmail
parameter_list|()
function_decl|;
DECL|method|builder ()
specifier|static
name|Builder
name|builder
parameter_list|()
block|{
return|return
operator|new
name|AutoValue_AddReviewersOp_Result
operator|.
name|Builder
argument_list|()
return|;
block|}
annotation|@
name|AutoValue
operator|.
name|Builder
DECL|class|Builder
specifier|abstract
specifier|static
class|class
name|Builder
block|{
DECL|method|setAddedReviewers (Iterable<PatchSetApproval> addedReviewers)
specifier|abstract
name|Builder
name|setAddedReviewers
parameter_list|(
name|Iterable
argument_list|<
name|PatchSetApproval
argument_list|>
name|addedReviewers
parameter_list|)
function_decl|;
DECL|method|setAddedReviewersByEmail (Iterable<Address> addedReviewersByEmail)
specifier|abstract
name|Builder
name|setAddedReviewersByEmail
parameter_list|(
name|Iterable
argument_list|<
name|Address
argument_list|>
name|addedReviewersByEmail
parameter_list|)
function_decl|;
DECL|method|setAddedCCs (Iterable<Account.Id> addedCCs)
specifier|abstract
name|Builder
name|setAddedCCs
parameter_list|(
name|Iterable
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|addedCCs
parameter_list|)
function_decl|;
DECL|method|setAddedCCsByEmail (Iterable<Address> addedCCsByEmail)
specifier|abstract
name|Builder
name|setAddedCCsByEmail
parameter_list|(
name|Iterable
argument_list|<
name|Address
argument_list|>
name|addedCCsByEmail
parameter_list|)
function_decl|;
DECL|method|build ()
specifier|abstract
name|Result
name|build
parameter_list|()
function_decl|;
block|}
block|}
DECL|field|approvalsUtil
specifier|private
specifier|final
name|ApprovalsUtil
name|approvalsUtil
decl_stmt|;
DECL|field|psUtil
specifier|private
specifier|final
name|PatchSetUtil
name|psUtil
decl_stmt|;
DECL|field|reviewerAdded
specifier|private
specifier|final
name|ReviewerAdded
name|reviewerAdded
decl_stmt|;
DECL|field|accountCache
specifier|private
specifier|final
name|AccountCache
name|accountCache
decl_stmt|;
DECL|field|projectCache
specifier|private
specifier|final
name|ProjectCache
name|projectCache
decl_stmt|;
DECL|field|addReviewersEmail
specifier|private
specifier|final
name|AddReviewersEmail
name|addReviewersEmail
decl_stmt|;
DECL|field|migration
specifier|private
specifier|final
name|NotesMigration
name|migration
decl_stmt|;
DECL|field|accountIds
specifier|private
specifier|final
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|accountIds
decl_stmt|;
DECL|field|addresses
specifier|private
specifier|final
name|Collection
argument_list|<
name|Address
argument_list|>
name|addresses
decl_stmt|;
DECL|field|state
specifier|private
specifier|final
name|ReviewerState
name|state
decl_stmt|;
DECL|field|notify
specifier|private
specifier|final
name|NotifyHandling
name|notify
decl_stmt|;
DECL|field|accountsToNotify
specifier|private
specifier|final
name|ListMultimap
argument_list|<
name|RecipientType
argument_list|,
name|Account
operator|.
name|Id
argument_list|>
name|accountsToNotify
decl_stmt|;
comment|// Unlike addedCCs, addedReviewers is a PatchSetApproval because the AddReviewerResult returned
comment|// via the REST API is supposed to include vote information.
DECL|field|addedReviewers
specifier|private
name|List
argument_list|<
name|PatchSetApproval
argument_list|>
name|addedReviewers
init|=
name|ImmutableList
operator|.
name|of
argument_list|()
decl_stmt|;
DECL|field|addedReviewersByEmail
specifier|private
name|Collection
argument_list|<
name|Address
argument_list|>
name|addedReviewersByEmail
init|=
name|ImmutableList
operator|.
name|of
argument_list|()
decl_stmt|;
DECL|field|addedCCs
specifier|private
name|Collection
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|addedCCs
init|=
name|ImmutableList
operator|.
name|of
argument_list|()
decl_stmt|;
DECL|field|addedCCsByEmail
specifier|private
name|Collection
argument_list|<
name|Address
argument_list|>
name|addedCCsByEmail
init|=
name|ImmutableList
operator|.
name|of
argument_list|()
decl_stmt|;
DECL|field|change
specifier|private
name|Change
name|change
decl_stmt|;
DECL|field|patchSet
specifier|private
name|PatchSet
name|patchSet
decl_stmt|;
DECL|field|opResult
specifier|private
name|Result
name|opResult
decl_stmt|;
annotation|@
name|Inject
DECL|method|AddReviewersOp ( ApprovalsUtil approvalsUtil, PatchSetUtil psUtil, ReviewerAdded reviewerAdded, AccountCache accountCache, ProjectCache projectCache, AddReviewersEmail addReviewersEmail, NotesMigration migration, @Assisted Set<Account.Id> accountIds, @Assisted Collection<Address> addresses, @Assisted ReviewerState state, @Assisted @Nullable NotifyHandling notify, @Assisted ListMultimap<RecipientType, Account.Id> accountsToNotify)
name|AddReviewersOp
parameter_list|(
name|ApprovalsUtil
name|approvalsUtil
parameter_list|,
name|PatchSetUtil
name|psUtil
parameter_list|,
name|ReviewerAdded
name|reviewerAdded
parameter_list|,
name|AccountCache
name|accountCache
parameter_list|,
name|ProjectCache
name|projectCache
parameter_list|,
name|AddReviewersEmail
name|addReviewersEmail
parameter_list|,
name|NotesMigration
name|migration
parameter_list|,
annotation|@
name|Assisted
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|accountIds
parameter_list|,
annotation|@
name|Assisted
name|Collection
argument_list|<
name|Address
argument_list|>
name|addresses
parameter_list|,
annotation|@
name|Assisted
name|ReviewerState
name|state
parameter_list|,
annotation|@
name|Assisted
annotation|@
name|Nullable
name|NotifyHandling
name|notify
parameter_list|,
annotation|@
name|Assisted
name|ListMultimap
argument_list|<
name|RecipientType
argument_list|,
name|Account
operator|.
name|Id
argument_list|>
name|accountsToNotify
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|state
operator|==
name|REVIEWER
operator|||
name|state
operator|==
name|CC
argument_list|,
literal|"must be %s or %s: %s"
argument_list|,
name|REVIEWER
argument_list|,
name|CC
argument_list|,
name|state
argument_list|)
expr_stmt|;
name|this
operator|.
name|approvalsUtil
operator|=
name|approvalsUtil
expr_stmt|;
name|this
operator|.
name|psUtil
operator|=
name|psUtil
expr_stmt|;
name|this
operator|.
name|reviewerAdded
operator|=
name|reviewerAdded
expr_stmt|;
name|this
operator|.
name|accountCache
operator|=
name|accountCache
expr_stmt|;
name|this
operator|.
name|projectCache
operator|=
name|projectCache
expr_stmt|;
name|this
operator|.
name|addReviewersEmail
operator|=
name|addReviewersEmail
expr_stmt|;
name|this
operator|.
name|migration
operator|=
name|migration
expr_stmt|;
name|this
operator|.
name|accountIds
operator|=
name|accountIds
expr_stmt|;
name|this
operator|.
name|addresses
operator|=
name|addresses
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|this
operator|.
name|notify
operator|=
name|notify
expr_stmt|;
name|this
operator|.
name|accountsToNotify
operator|=
name|accountsToNotify
expr_stmt|;
block|}
DECL|method|setPatchSet (PatchSet patchSet)
name|void
name|setPatchSet
parameter_list|(
name|PatchSet
name|patchSet
parameter_list|)
block|{
name|this
operator|.
name|patchSet
operator|=
name|requireNonNull
argument_list|(
name|patchSet
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|updateChange (ChangeContext ctx)
specifier|public
name|boolean
name|updateChange
parameter_list|(
name|ChangeContext
name|ctx
parameter_list|)
throws|throws
name|RestApiException
throws|,
name|OrmException
throws|,
name|IOException
block|{
name|change
operator|=
name|ctx
operator|.
name|getChange
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|accountIds
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|migration
operator|.
name|readChanges
argument_list|()
operator|&&
name|state
operator|==
name|CC
condition|)
block|{
name|addedCCs
operator|=
name|approvalsUtil
operator|.
name|addCcs
argument_list|(
name|ctx
operator|.
name|getNotes
argument_list|()
argument_list|,
name|ctx
operator|.
name|getUpdate
argument_list|(
name|change
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
argument_list|,
name|accountIds
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|addedReviewers
operator|=
name|approvalsUtil
operator|.
name|addReviewers
argument_list|(
name|ctx
operator|.
name|getDb
argument_list|()
argument_list|,
name|ctx
operator|.
name|getNotes
argument_list|()
argument_list|,
name|ctx
operator|.
name|getUpdate
argument_list|(
name|change
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
argument_list|,
name|projectCache
operator|.
name|checkedGet
argument_list|(
name|change
operator|.
name|getProject
argument_list|()
argument_list|)
operator|.
name|getLabelTypes
argument_list|(
name|change
operator|.
name|getDest
argument_list|()
argument_list|)
argument_list|,
name|change
argument_list|,
name|accountIds
argument_list|)
expr_stmt|;
block|}
block|}
name|ImmutableList
argument_list|<
name|Address
argument_list|>
name|addressesToAdd
init|=
name|ImmutableList
operator|.
name|of
argument_list|()
decl_stmt|;
name|ReviewerStateInternal
name|internalState
init|=
name|ReviewerStateInternal
operator|.
name|fromReviewerState
argument_list|(
name|state
argument_list|)
decl_stmt|;
if|if
condition|(
name|migration
operator|.
name|readChanges
argument_list|()
condition|)
block|{
comment|// TODO(dborowitz): This behavior should live in ApprovalsUtil or something, like addCcs does.
name|ImmutableSet
argument_list|<
name|Address
argument_list|>
name|existing
init|=
name|ctx
operator|.
name|getNotes
argument_list|()
operator|.
name|getReviewersByEmail
argument_list|()
operator|.
name|byState
argument_list|(
name|internalState
argument_list|)
decl_stmt|;
name|addressesToAdd
operator|=
name|addresses
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|a
lambda|->
operator|!
name|existing
operator|.
name|contains
argument_list|(
name|a
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|toImmutableList
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|state
operator|==
name|CC
condition|)
block|{
name|addedCCsByEmail
operator|=
name|addressesToAdd
expr_stmt|;
block|}
else|else
block|{
name|addedReviewersByEmail
operator|=
name|addressesToAdd
expr_stmt|;
block|}
for|for
control|(
name|Address
name|a
range|:
name|addressesToAdd
control|)
block|{
name|ctx
operator|.
name|getUpdate
argument_list|(
name|change
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
operator|.
name|putReviewerByEmail
argument_list|(
name|a
argument_list|,
name|internalState
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|addedCCs
operator|.
name|isEmpty
argument_list|()
operator|&&
name|addedReviewers
operator|.
name|isEmpty
argument_list|()
operator|&&
name|addressesToAdd
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|checkAdded
argument_list|()
expr_stmt|;
if|if
condition|(
name|patchSet
operator|==
literal|null
condition|)
block|{
name|patchSet
operator|=
name|requireNonNull
argument_list|(
name|psUtil
operator|.
name|current
argument_list|(
name|ctx
operator|.
name|getDb
argument_list|()
argument_list|,
name|ctx
operator|.
name|getNotes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|checkAdded ()
specifier|private
name|void
name|checkAdded
parameter_list|()
block|{
comment|// Should only affect either reviewers or CCs, not both. But the logic in updateChange is
comment|// complex, so programmer error is conceivable.
name|boolean
name|addedAnyReviewers
init|=
operator|!
name|addedReviewers
operator|.
name|isEmpty
argument_list|()
operator|||
operator|!
name|addedReviewersByEmail
operator|.
name|isEmpty
argument_list|()
decl_stmt|;
name|boolean
name|addedAnyCCs
init|=
operator|!
name|addedCCs
operator|.
name|isEmpty
argument_list|()
operator|||
operator|!
name|addedCCsByEmail
operator|.
name|isEmpty
argument_list|()
decl_stmt|;
name|checkState
argument_list|(
operator|!
operator|(
name|addedAnyReviewers
operator|&&
name|addedAnyCCs
operator|)
argument_list|,
literal|"should not have added both reviewers and CCs:\n"
operator|+
literal|"Arguments:\n"
operator|+
literal|"  accountIds=%s\n"
operator|+
literal|"  addresses=%s\n"
operator|+
literal|"Results:\n"
operator|+
literal|"  addedReviewers=%s\n"
operator|+
literal|"  addedReviewersByEmail=%s\n"
operator|+
literal|"  addedCCs=%s\n"
operator|+
literal|"  addedCCsByEmail=%s"
argument_list|,
name|accountIds
argument_list|,
name|addresses
argument_list|,
name|addedReviewers
argument_list|,
name|addedReviewersByEmail
argument_list|,
name|addedCCs
argument_list|,
name|addedCCsByEmail
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|postUpdate (Context ctx)
specifier|public
name|void
name|postUpdate
parameter_list|(
name|Context
name|ctx
parameter_list|)
throws|throws
name|Exception
block|{
name|opResult
operator|=
name|Result
operator|.
name|builder
argument_list|()
operator|.
name|setAddedReviewers
argument_list|(
name|addedReviewers
argument_list|)
operator|.
name|setAddedReviewersByEmail
argument_list|(
name|addedReviewersByEmail
argument_list|)
operator|.
name|setAddedCCs
argument_list|(
name|addedCCs
argument_list|)
operator|.
name|setAddedCCsByEmail
argument_list|(
name|addedCCsByEmail
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|addReviewersEmail
operator|.
name|emailReviewers
argument_list|(
name|ctx
operator|.
name|getUser
argument_list|()
operator|.
name|asIdentifiedUser
argument_list|()
argument_list|,
name|change
argument_list|,
name|Lists
operator|.
name|transform
argument_list|(
name|addedReviewers
argument_list|,
name|PatchSetApproval
operator|::
name|getAccountId
argument_list|)
argument_list|,
name|addedCCs
argument_list|,
name|addedReviewersByEmail
argument_list|,
name|addedCCsByEmail
argument_list|,
name|notify
argument_list|,
name|accountsToNotify
argument_list|,
operator|!
name|change
operator|.
name|isWorkInProgress
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|addedReviewers
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|AccountState
argument_list|>
name|reviewers
init|=
name|addedReviewers
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|r
lambda|->
name|accountCache
operator|.
name|get
argument_list|(
name|r
operator|.
name|getAccountId
argument_list|()
argument_list|)
argument_list|)
operator|.
name|flatMap
argument_list|(
name|Streams
operator|::
name|stream
argument_list|)
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|reviewerAdded
operator|.
name|fire
argument_list|(
name|change
argument_list|,
name|patchSet
argument_list|,
name|reviewers
argument_list|,
name|ctx
operator|.
name|getAccount
argument_list|()
argument_list|,
name|ctx
operator|.
name|getWhen
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getResult ()
specifier|public
name|Result
name|getResult
parameter_list|()
block|{
name|checkState
argument_list|(
name|opResult
operator|!=
literal|null
argument_list|,
literal|"Batch update wasn't executed yet"
argument_list|)
expr_stmt|;
return|return
name|opResult
return|;
block|}
block|}
end_class

end_unit

