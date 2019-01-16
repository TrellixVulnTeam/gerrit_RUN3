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
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
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
name|flogger
operator|.
name|FluentLogger
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
name|data
operator|.
name|LabelType
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
name|data
operator|.
name|LabelTypes
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
name|exceptions
operator|.
name|EmailException
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
name|exceptions
operator|.
name|StorageException
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
name|DeleteReviewerInput
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
name|restapi
operator|.
name|AuthException
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
name|ChangeMessage
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
name|client
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
name|ChangeMessagesUtil
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
name|ReviewerDeleted
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
name|DeleteReviewerSender
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
name|ChangeUpdate
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
name|permissions
operator|.
name|PermissionBackendException
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
name|project
operator|.
name|RemoveReviewerControl
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

begin_class
DECL|class|DeleteReviewerOp
specifier|public
class|class
name|DeleteReviewerOp
implements|implements
name|BatchUpdateOp
block|{
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|FluentLogger
name|logger
init|=
name|FluentLogger
operator|.
name|forEnclosingClass
argument_list|()
decl_stmt|;
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (AccountState reviewerAccount, DeleteReviewerInput input)
name|DeleteReviewerOp
name|create
parameter_list|(
name|AccountState
name|reviewerAccount
parameter_list|,
name|DeleteReviewerInput
name|input
parameter_list|)
function_decl|;
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
DECL|field|cmUtil
specifier|private
specifier|final
name|ChangeMessagesUtil
name|cmUtil
decl_stmt|;
DECL|field|userFactory
specifier|private
specifier|final
name|IdentifiedUser
operator|.
name|GenericFactory
name|userFactory
decl_stmt|;
DECL|field|reviewerDeleted
specifier|private
specifier|final
name|ReviewerDeleted
name|reviewerDeleted
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
DECL|field|deleteReviewerSenderFactory
specifier|private
specifier|final
name|DeleteReviewerSender
operator|.
name|Factory
name|deleteReviewerSenderFactory
decl_stmt|;
DECL|field|removeReviewerControl
specifier|private
specifier|final
name|RemoveReviewerControl
name|removeReviewerControl
decl_stmt|;
DECL|field|projectCache
specifier|private
specifier|final
name|ProjectCache
name|projectCache
decl_stmt|;
DECL|field|reviewer
specifier|private
specifier|final
name|AccountState
name|reviewer
decl_stmt|;
DECL|field|input
specifier|private
specifier|final
name|DeleteReviewerInput
name|input
decl_stmt|;
DECL|field|changeMessage
name|ChangeMessage
name|changeMessage
decl_stmt|;
DECL|field|currChange
name|Change
name|currChange
decl_stmt|;
DECL|field|currPs
name|PatchSet
name|currPs
decl_stmt|;
DECL|field|newApprovals
name|Map
argument_list|<
name|String
argument_list|,
name|Short
argument_list|>
name|newApprovals
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|oldApprovals
name|Map
argument_list|<
name|String
argument_list|,
name|Short
argument_list|>
name|oldApprovals
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Inject
DECL|method|DeleteReviewerOp ( ApprovalsUtil approvalsUtil, PatchSetUtil psUtil, ChangeMessagesUtil cmUtil, IdentifiedUser.GenericFactory userFactory, ReviewerDeleted reviewerDeleted, Provider<IdentifiedUser> user, DeleteReviewerSender.Factory deleteReviewerSenderFactory, RemoveReviewerControl removeReviewerControl, ProjectCache projectCache, @Assisted AccountState reviewerAccount, @Assisted DeleteReviewerInput input)
name|DeleteReviewerOp
parameter_list|(
name|ApprovalsUtil
name|approvalsUtil
parameter_list|,
name|PatchSetUtil
name|psUtil
parameter_list|,
name|ChangeMessagesUtil
name|cmUtil
parameter_list|,
name|IdentifiedUser
operator|.
name|GenericFactory
name|userFactory
parameter_list|,
name|ReviewerDeleted
name|reviewerDeleted
parameter_list|,
name|Provider
argument_list|<
name|IdentifiedUser
argument_list|>
name|user
parameter_list|,
name|DeleteReviewerSender
operator|.
name|Factory
name|deleteReviewerSenderFactory
parameter_list|,
name|RemoveReviewerControl
name|removeReviewerControl
parameter_list|,
name|ProjectCache
name|projectCache
parameter_list|,
annotation|@
name|Assisted
name|AccountState
name|reviewerAccount
parameter_list|,
annotation|@
name|Assisted
name|DeleteReviewerInput
name|input
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
name|cmUtil
operator|=
name|cmUtil
expr_stmt|;
name|this
operator|.
name|userFactory
operator|=
name|userFactory
expr_stmt|;
name|this
operator|.
name|reviewerDeleted
operator|=
name|reviewerDeleted
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|deleteReviewerSenderFactory
operator|=
name|deleteReviewerSenderFactory
expr_stmt|;
name|this
operator|.
name|removeReviewerControl
operator|=
name|removeReviewerControl
expr_stmt|;
name|this
operator|.
name|projectCache
operator|=
name|projectCache
expr_stmt|;
name|this
operator|.
name|reviewer
operator|=
name|reviewerAccount
expr_stmt|;
name|this
operator|.
name|input
operator|=
name|input
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
name|AuthException
throws|,
name|ResourceNotFoundException
throws|,
name|StorageException
throws|,
name|PermissionBackendException
throws|,
name|IOException
block|{
name|Account
operator|.
name|Id
name|reviewerId
init|=
name|reviewer
operator|.
name|getAccount
argument_list|()
operator|.
name|getId
argument_list|()
decl_stmt|;
comment|// Check of removing this reviewer (even if there is no vote processed by the loop below) is OK
name|removeReviewerControl
operator|.
name|checkRemoveReviewer
argument_list|(
name|ctx
operator|.
name|getNotes
argument_list|()
argument_list|,
name|ctx
operator|.
name|getUser
argument_list|()
argument_list|,
name|reviewerId
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|approvalsUtil
operator|.
name|getReviewers
argument_list|(
name|ctx
operator|.
name|getNotes
argument_list|()
argument_list|)
operator|.
name|all
argument_list|()
operator|.
name|contains
argument_list|(
name|reviewerId
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|()
throw|;
block|}
name|currChange
operator|=
name|ctx
operator|.
name|getChange
argument_list|()
expr_stmt|;
name|currPs
operator|=
name|psUtil
operator|.
name|current
argument_list|(
name|ctx
operator|.
name|getNotes
argument_list|()
argument_list|)
expr_stmt|;
name|LabelTypes
name|labelTypes
init|=
name|projectCache
operator|.
name|checkedGet
argument_list|(
name|ctx
operator|.
name|getProject
argument_list|()
argument_list|)
operator|.
name|getLabelTypes
argument_list|(
name|ctx
operator|.
name|getNotes
argument_list|()
argument_list|)
decl_stmt|;
comment|// removing a reviewer will remove all her votes
for|for
control|(
name|LabelType
name|lt
range|:
name|labelTypes
operator|.
name|getLabelTypes
argument_list|()
control|)
block|{
name|newApprovals
operator|.
name|put
argument_list|(
name|lt
operator|.
name|getName
argument_list|()
argument_list|,
operator|(
name|short
operator|)
literal|0
argument_list|)
expr_stmt|;
block|}
name|StringBuilder
name|msg
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|msg
operator|.
name|append
argument_list|(
literal|"Removed reviewer "
operator|+
name|reviewer
operator|.
name|getAccount
argument_list|()
operator|.
name|getFullName
argument_list|()
argument_list|)
expr_stmt|;
name|StringBuilder
name|removedVotesMsg
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|removedVotesMsg
operator|.
name|append
argument_list|(
literal|" with the following votes:\n\n"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|PatchSetApproval
argument_list|>
name|del
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|boolean
name|votesRemoved
init|=
literal|false
decl_stmt|;
for|for
control|(
name|PatchSetApproval
name|a
range|:
name|approvals
argument_list|(
name|ctx
argument_list|,
name|reviewerId
argument_list|)
control|)
block|{
comment|// Check if removing this vote is OK
name|removeReviewerControl
operator|.
name|checkRemoveReviewer
argument_list|(
name|ctx
operator|.
name|getNotes
argument_list|()
argument_list|,
name|ctx
operator|.
name|getUser
argument_list|()
argument_list|,
name|a
argument_list|)
expr_stmt|;
name|del
operator|.
name|add
argument_list|(
name|a
argument_list|)
expr_stmt|;
if|if
condition|(
name|a
operator|.
name|getPatchSetId
argument_list|()
operator|.
name|equals
argument_list|(
name|currPs
operator|.
name|getId
argument_list|()
argument_list|)
operator|&&
name|a
operator|.
name|getValue
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|oldApprovals
operator|.
name|put
argument_list|(
name|a
operator|.
name|getLabel
argument_list|()
argument_list|,
name|a
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|removedVotesMsg
operator|.
name|append
argument_list|(
literal|"* "
argument_list|)
operator|.
name|append
argument_list|(
name|a
operator|.
name|getLabel
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|formatLabelValue
argument_list|(
name|a
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|" by "
argument_list|)
operator|.
name|append
argument_list|(
name|userFactory
operator|.
name|create
argument_list|(
name|a
operator|.
name|getAccountId
argument_list|()
argument_list|)
operator|.
name|getNameEmail
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|votesRemoved
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|votesRemoved
condition|)
block|{
name|msg
operator|.
name|append
argument_list|(
name|removedVotesMsg
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|msg
operator|.
name|append
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
block|}
name|ChangeUpdate
name|update
init|=
name|ctx
operator|.
name|getUpdate
argument_list|(
name|currPs
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|update
operator|.
name|removeReviewer
argument_list|(
name|reviewerId
argument_list|)
expr_stmt|;
name|changeMessage
operator|=
name|ChangeMessagesUtil
operator|.
name|newMessage
argument_list|(
name|ctx
argument_list|,
name|msg
operator|.
name|toString
argument_list|()
argument_list|,
name|ChangeMessagesUtil
operator|.
name|TAG_DELETE_REVIEWER
argument_list|)
expr_stmt|;
name|cmUtil
operator|.
name|addChangeMessage
argument_list|(
name|update
argument_list|,
name|changeMessage
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
block|{
name|NotifyResolver
operator|.
name|Result
name|notify
init|=
name|ctx
operator|.
name|getNotify
argument_list|(
name|currChange
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|input
operator|.
name|notify
operator|==
literal|null
operator|&&
name|currChange
operator|.
name|isWorkInProgress
argument_list|()
operator|&&
operator|!
name|oldApprovals
operator|.
name|isEmpty
argument_list|()
operator|&&
name|notify
operator|.
name|handling
argument_list|()
operator|.
name|compareTo
argument_list|(
name|NotifyHandling
operator|.
name|OWNER
argument_list|)
operator|<
literal|0
condition|)
block|{
comment|// Override NotifyHandling from the context to notify owner if votes were removed on a WIP
comment|// change.
name|notify
operator|=
name|notify
operator|.
name|withHandling
argument_list|(
name|NotifyHandling
operator|.
name|OWNER
argument_list|)
expr_stmt|;
block|}
try|try
block|{
if|if
condition|(
name|notify
operator|.
name|shouldNotify
argument_list|()
condition|)
block|{
name|emailReviewers
argument_list|(
name|ctx
operator|.
name|getProject
argument_list|()
argument_list|,
name|currChange
argument_list|,
name|changeMessage
argument_list|,
name|notify
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|err
parameter_list|)
block|{
name|logger
operator|.
name|atSevere
argument_list|()
operator|.
name|withCause
argument_list|(
name|err
argument_list|)
operator|.
name|log
argument_list|(
literal|"Cannot email update for change %s"
argument_list|,
name|currChange
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|reviewerDeleted
operator|.
name|fire
argument_list|(
name|currChange
argument_list|,
name|currPs
argument_list|,
name|reviewer
argument_list|,
name|ctx
operator|.
name|getAccount
argument_list|()
argument_list|,
name|changeMessage
operator|.
name|getMessage
argument_list|()
argument_list|,
name|newApprovals
argument_list|,
name|oldApprovals
argument_list|,
name|notify
operator|.
name|handling
argument_list|()
argument_list|,
name|ctx
operator|.
name|getWhen
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|approvals (ChangeContext ctx, Account.Id accountId)
specifier|private
name|Iterable
argument_list|<
name|PatchSetApproval
argument_list|>
name|approvals
parameter_list|(
name|ChangeContext
name|ctx
parameter_list|,
name|Account
operator|.
name|Id
name|accountId
parameter_list|)
throws|throws
name|StorageException
block|{
name|Iterable
argument_list|<
name|PatchSetApproval
argument_list|>
name|approvals
decl_stmt|;
name|approvals
operator|=
name|approvalsUtil
operator|.
name|byChange
argument_list|(
name|ctx
operator|.
name|getNotes
argument_list|()
argument_list|)
operator|.
name|values
argument_list|()
expr_stmt|;
return|return
name|Iterables
operator|.
name|filter
argument_list|(
name|approvals
argument_list|,
name|psa
lambda|->
name|accountId
operator|.
name|equals
argument_list|(
name|psa
operator|.
name|getAccountId
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|formatLabelValue (short value)
specifier|private
name|String
name|formatLabelValue
parameter_list|(
name|short
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|>
literal|0
condition|)
block|{
return|return
literal|"+"
operator|+
name|value
return|;
block|}
return|return
name|Short
operator|.
name|toString
argument_list|(
name|value
argument_list|)
return|;
block|}
DECL|method|emailReviewers ( NameKey projectName, Change change, ChangeMessage changeMessage, NotifyResolver.Result notify)
specifier|private
name|void
name|emailReviewers
parameter_list|(
name|NameKey
name|projectName
parameter_list|,
name|Change
name|change
parameter_list|,
name|ChangeMessage
name|changeMessage
parameter_list|,
name|NotifyResolver
operator|.
name|Result
name|notify
parameter_list|)
throws|throws
name|EmailException
block|{
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
if|if
condition|(
name|userId
operator|.
name|equals
argument_list|(
name|reviewer
operator|.
name|getAccount
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
comment|// The user knows they removed themselves, don't bother emailing them.
return|return;
block|}
name|DeleteReviewerSender
name|cm
init|=
name|deleteReviewerSenderFactory
operator|.
name|create
argument_list|(
name|projectName
argument_list|,
name|change
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
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
name|Collections
operator|.
name|singleton
argument_list|(
name|reviewer
operator|.
name|getAccount
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|cm
operator|.
name|setChangeMessage
argument_list|(
name|changeMessage
operator|.
name|getMessage
argument_list|()
argument_list|,
name|changeMessage
operator|.
name|getWrittenOn
argument_list|()
argument_list|)
expr_stmt|;
name|cm
operator|.
name|setNotify
argument_list|(
name|notify
argument_list|)
expr_stmt|;
name|cm
operator|.
name|send
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

