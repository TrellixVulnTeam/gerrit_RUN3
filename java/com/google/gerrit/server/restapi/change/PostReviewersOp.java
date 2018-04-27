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
DECL|package|com.google.gerrit.server.restapi.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|restapi
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
name|checkState
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
name|base
operator|.
name|MoreObjects
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
name|change
operator|.
name|ChangeResource
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
name|server
operator|.
name|mail
operator|.
name|send
operator|.
name|AddReviewerSender
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

begin_class
DECL|class|PostReviewersOp
specifier|public
class|class
name|PostReviewersOp
implements|implements
name|BatchUpdateOp
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
name|PostReviewersOp
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create ( ChangeResource rsrc, Set<Account.Id> reviewers, Collection<Address> reviewersByEmail, ReviewerState state, @Nullable NotifyHandling notify, ListMultimap<RecipientType, Account.Id> accountsToNotify)
name|PostReviewersOp
name|create
parameter_list|(
name|ChangeResource
name|rsrc
parameter_list|,
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|reviewers
parameter_list|,
name|Collection
argument_list|<
name|Address
argument_list|>
name|reviewersByEmail
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
DECL|method|builder ()
specifier|static
name|Builder
name|builder
parameter_list|()
block|{
return|return
operator|new
name|AutoValue_PostReviewersOp_Result
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
DECL|method|setAddedReviewers (ImmutableList<PatchSetApproval> addedReviewers)
specifier|abstract
name|Builder
name|setAddedReviewers
parameter_list|(
name|ImmutableList
argument_list|<
name|PatchSetApproval
argument_list|>
name|addedReviewers
parameter_list|)
function_decl|;
DECL|method|setAddedCCs (ImmutableList<Account.Id> addedCCs)
specifier|abstract
name|Builder
name|setAddedCCs
parameter_list|(
name|ImmutableList
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|addedCCs
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
DECL|field|addReviewerSenderFactory
specifier|private
specifier|final
name|AddReviewerSender
operator|.
name|Factory
name|addReviewerSenderFactory
decl_stmt|;
DECL|field|migration
specifier|private
specifier|final
name|NotesMigration
name|migration
decl_stmt|;
DECL|field|user
specifier|private
specifier|final
name|Provider
argument_list|<
name|IdentifiedUser
argument_list|>
name|user
decl_stmt|;
DECL|field|dbProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|dbProvider
decl_stmt|;
DECL|field|rsrc
specifier|private
specifier|final
name|ChangeResource
name|rsrc
decl_stmt|;
DECL|field|reviewers
specifier|private
specifier|final
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|reviewers
decl_stmt|;
DECL|field|reviewersByEmail
specifier|private
specifier|final
name|Collection
argument_list|<
name|Address
argument_list|>
name|reviewersByEmail
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
DECL|field|addedReviewers
specifier|private
name|List
argument_list|<
name|PatchSetApproval
argument_list|>
name|addedReviewers
init|=
operator|new
name|ArrayList
argument_list|<>
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
operator|new
name|ArrayList
argument_list|<>
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
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
DECL|method|PostReviewersOp ( ApprovalsUtil approvalsUtil, PatchSetUtil psUtil, ReviewerAdded reviewerAdded, AccountCache accountCache, ProjectCache projectCache, AddReviewerSender.Factory addReviewerSenderFactory, NotesMigration migration, Provider<IdentifiedUser> user, Provider<ReviewDb> dbProvider, @Assisted ChangeResource rsrc, @Assisted Set<Account.Id> reviewers, @Assisted Collection<Address> reviewersByEmail, @Assisted ReviewerState state, @Assisted @Nullable NotifyHandling notify, @Assisted ListMultimap<RecipientType, Account.Id> accountsToNotify)
name|PostReviewersOp
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
name|AddReviewerSender
operator|.
name|Factory
name|addReviewerSenderFactory
parameter_list|,
name|NotesMigration
name|migration
parameter_list|,
name|Provider
argument_list|<
name|IdentifiedUser
argument_list|>
name|user
parameter_list|,
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|dbProvider
parameter_list|,
annotation|@
name|Assisted
name|ChangeResource
name|rsrc
parameter_list|,
annotation|@
name|Assisted
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|reviewers
parameter_list|,
annotation|@
name|Assisted
name|Collection
argument_list|<
name|Address
argument_list|>
name|reviewersByEmail
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
name|addReviewerSenderFactory
operator|=
name|addReviewerSenderFactory
expr_stmt|;
name|this
operator|.
name|migration
operator|=
name|migration
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|dbProvider
operator|=
name|dbProvider
expr_stmt|;
name|this
operator|.
name|rsrc
operator|=
name|rsrc
expr_stmt|;
name|this
operator|.
name|reviewers
operator|=
name|reviewers
expr_stmt|;
name|this
operator|.
name|reviewersByEmail
operator|=
name|reviewersByEmail
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
if|if
condition|(
operator|!
name|reviewers
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
name|ctx
operator|.
name|getChange
argument_list|()
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
argument_list|,
name|reviewers
argument_list|)
expr_stmt|;
if|if
condition|(
name|addedCCs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
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
name|ctx
operator|.
name|getChange
argument_list|()
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
argument_list|,
name|projectCache
operator|.
name|checkedGet
argument_list|(
name|rsrc
operator|.
name|getProject
argument_list|()
argument_list|)
operator|.
name|getLabelTypes
argument_list|(
name|rsrc
operator|.
name|getChange
argument_list|()
operator|.
name|getDest
argument_list|()
argument_list|,
name|ctx
operator|.
name|getUser
argument_list|()
argument_list|)
argument_list|,
name|rsrc
operator|.
name|getChange
argument_list|()
argument_list|,
name|reviewers
argument_list|)
expr_stmt|;
if|if
condition|(
name|addedReviewers
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
for|for
control|(
name|Address
name|a
range|:
name|reviewersByEmail
control|)
block|{
name|ctx
operator|.
name|getUpdate
argument_list|(
name|ctx
operator|.
name|getChange
argument_list|()
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
operator|.
name|putReviewerByEmail
argument_list|(
name|a
argument_list|,
name|ReviewerStateInternal
operator|.
name|fromReviewerState
argument_list|(
name|state
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|patchSet
operator|=
name|psUtil
operator|.
name|current
argument_list|(
name|dbProvider
operator|.
name|get
argument_list|()
argument_list|,
name|rsrc
operator|.
name|getNotes
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
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
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|addedReviewers
argument_list|)
argument_list|)
operator|.
name|setAddedCCs
argument_list|(
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|addedCCs
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|emailReviewers
argument_list|(
name|rsrc
operator|.
name|getChange
argument_list|()
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
operator|==
literal|null
condition|?
name|ImmutableList
operator|.
name|of
argument_list|()
else|:
name|addedCCs
argument_list|,
name|reviewersByEmail
argument_list|,
name|addedCCsByEmail
argument_list|,
name|notify
argument_list|,
name|accountsToNotify
argument_list|,
operator|!
name|rsrc
operator|.
name|getChange
argument_list|()
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
name|rsrc
operator|.
name|getChange
argument_list|()
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
DECL|method|emailReviewers ( Change change, Collection<Account.Id> added, Collection<Account.Id> copied, Collection<Address> addedByEmail, Collection<Address> copiedByEmail, NotifyHandling notify, ListMultimap<RecipientType, Account.Id> accountsToNotify, boolean readyForReview)
specifier|public
name|void
name|emailReviewers
parameter_list|(
name|Change
name|change
parameter_list|,
name|Collection
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|added
parameter_list|,
name|Collection
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|copied
parameter_list|,
name|Collection
argument_list|<
name|Address
argument_list|>
name|addedByEmail
parameter_list|,
name|Collection
argument_list|<
name|Address
argument_list|>
name|copiedByEmail
parameter_list|,
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
parameter_list|,
name|boolean
name|readyForReview
parameter_list|)
block|{
if|if
condition|(
name|added
operator|.
name|isEmpty
argument_list|()
operator|&&
name|copied
operator|.
name|isEmpty
argument_list|()
operator|&&
name|addedByEmail
operator|.
name|isEmpty
argument_list|()
operator|&&
name|copiedByEmail
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
comment|// Email the reviewers
comment|//
comment|// The user knows they added themselves, don't bother emailing them.
name|List
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|toMail
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|added
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|Account
operator|.
name|Id
name|userId
init|=
name|user
operator|.
name|get
argument_list|()
operator|.
name|getAccountId
argument_list|()
decl_stmt|;
for|for
control|(
name|Account
operator|.
name|Id
name|id
range|:
name|added
control|)
block|{
if|if
condition|(
operator|!
name|id
operator|.
name|equals
argument_list|(
name|userId
argument_list|)
condition|)
block|{
name|toMail
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|toCopy
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|copied
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Account
operator|.
name|Id
name|id
range|:
name|copied
control|)
block|{
if|if
condition|(
operator|!
name|id
operator|.
name|equals
argument_list|(
name|userId
argument_list|)
condition|)
block|{
name|toCopy
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|toMail
operator|.
name|isEmpty
argument_list|()
operator|&&
name|toCopy
operator|.
name|isEmpty
argument_list|()
operator|&&
name|addedByEmail
operator|.
name|isEmpty
argument_list|()
operator|&&
name|copiedByEmail
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
try|try
block|{
name|AddReviewerSender
name|cm
init|=
name|addReviewerSenderFactory
operator|.
name|create
argument_list|(
name|change
operator|.
name|getProject
argument_list|()
argument_list|,
name|change
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
comment|// Default to silent operation on WIP changes.
name|NotifyHandling
name|defaultNotifyHandling
init|=
name|readyForReview
condition|?
name|NotifyHandling
operator|.
name|ALL
else|:
name|NotifyHandling
operator|.
name|NONE
decl_stmt|;
name|cm
operator|.
name|setNotify
argument_list|(
name|MoreObjects
operator|.
name|firstNonNull
argument_list|(
name|notify
argument_list|,
name|defaultNotifyHandling
argument_list|)
argument_list|)
expr_stmt|;
name|cm
operator|.
name|setAccountsToNotify
argument_list|(
name|accountsToNotify
argument_list|)
expr_stmt|;
name|cm
operator|.
name|setFrom
argument_list|(
name|userId
argument_list|)
expr_stmt|;
name|cm
operator|.
name|addReviewers
argument_list|(
name|toMail
argument_list|)
expr_stmt|;
name|cm
operator|.
name|addReviewersByEmail
argument_list|(
name|addedByEmail
argument_list|)
expr_stmt|;
name|cm
operator|.
name|addExtraCC
argument_list|(
name|toCopy
argument_list|)
expr_stmt|;
name|cm
operator|.
name|addExtraCCByEmail
argument_list|(
name|copiedByEmail
argument_list|)
expr_stmt|;
name|cm
operator|.
name|send
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|err
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cannot send email to new reviewers of change "
operator|+
name|change
operator|.
name|getId
argument_list|()
argument_list|,
name|err
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

