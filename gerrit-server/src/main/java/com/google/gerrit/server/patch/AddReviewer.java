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
DECL|package|com.google.gerrit.server.patch
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|patch
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
name|common
operator|.
name|data
operator|.
name|ReviewerResult
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
name|ApprovalType
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
name|ApprovalTypes
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
name|ApprovalCategory
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
name|account
operator|.
name|AccountResolver
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
name|project
operator|.
name|ChangeControl
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
name|Callable
import|;
end_import

begin_class
DECL|class|AddReviewer
specifier|public
class|class
name|AddReviewer
implements|implements
name|Callable
argument_list|<
name|ReviewerResult
argument_list|>
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
name|AddReviewer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (Change.Id changeId, Collection<String> nameOrEmails)
name|AddReviewer
name|create
parameter_list|(
name|Change
operator|.
name|Id
name|changeId
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|nameOrEmails
parameter_list|)
function_decl|;
block|}
DECL|field|addReviewerSenderFactory
specifier|private
specifier|final
name|AddReviewerSender
operator|.
name|Factory
name|addReviewerSenderFactory
decl_stmt|;
DECL|field|accountResolver
specifier|private
specifier|final
name|AccountResolver
name|accountResolver
decl_stmt|;
DECL|field|changeControlFactory
specifier|private
specifier|final
name|ChangeControl
operator|.
name|Factory
name|changeControlFactory
decl_stmt|;
DECL|field|db
specifier|private
specifier|final
name|ReviewDb
name|db
decl_stmt|;
DECL|field|currentUser
specifier|private
specifier|final
name|IdentifiedUser
name|currentUser
decl_stmt|;
DECL|field|identifiedUserFactory
specifier|private
specifier|final
name|IdentifiedUser
operator|.
name|GenericFactory
name|identifiedUserFactory
decl_stmt|;
DECL|field|addReviewerCategoryId
specifier|private
specifier|final
name|ApprovalCategory
operator|.
name|Id
name|addReviewerCategoryId
decl_stmt|;
DECL|field|changeId
specifier|private
specifier|final
name|Change
operator|.
name|Id
name|changeId
decl_stmt|;
DECL|field|reviewers
specifier|private
specifier|final
name|Collection
argument_list|<
name|String
argument_list|>
name|reviewers
decl_stmt|;
annotation|@
name|Inject
DECL|method|AddReviewer (final AddReviewerSender.Factory addReviewerSenderFactory, final AccountResolver accountResolver, final ChangeControl.Factory changeControlFactory, final ReviewDb db, final IdentifiedUser.GenericFactory identifiedUserFactory, final IdentifiedUser currentUser, final ApprovalTypes approvalTypes, @Assisted final Change.Id changeId, @Assisted final Collection<String> nameOrEmails)
name|AddReviewer
parameter_list|(
specifier|final
name|AddReviewerSender
operator|.
name|Factory
name|addReviewerSenderFactory
parameter_list|,
specifier|final
name|AccountResolver
name|accountResolver
parameter_list|,
specifier|final
name|ChangeControl
operator|.
name|Factory
name|changeControlFactory
parameter_list|,
specifier|final
name|ReviewDb
name|db
parameter_list|,
specifier|final
name|IdentifiedUser
operator|.
name|GenericFactory
name|identifiedUserFactory
parameter_list|,
specifier|final
name|IdentifiedUser
name|currentUser
parameter_list|,
specifier|final
name|ApprovalTypes
name|approvalTypes
parameter_list|,
annotation|@
name|Assisted
specifier|final
name|Change
operator|.
name|Id
name|changeId
parameter_list|,
annotation|@
name|Assisted
specifier|final
name|Collection
argument_list|<
name|String
argument_list|>
name|nameOrEmails
parameter_list|)
block|{
name|this
operator|.
name|addReviewerSenderFactory
operator|=
name|addReviewerSenderFactory
expr_stmt|;
name|this
operator|.
name|accountResolver
operator|=
name|accountResolver
expr_stmt|;
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|changeControlFactory
operator|=
name|changeControlFactory
expr_stmt|;
name|this
operator|.
name|identifiedUserFactory
operator|=
name|identifiedUserFactory
expr_stmt|;
name|this
operator|.
name|currentUser
operator|=
name|currentUser
expr_stmt|;
specifier|final
name|List
argument_list|<
name|ApprovalType
argument_list|>
name|allTypes
init|=
name|approvalTypes
operator|.
name|getApprovalTypes
argument_list|()
decl_stmt|;
name|addReviewerCategoryId
operator|=
name|allTypes
operator|.
name|get
argument_list|(
name|allTypes
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|getCategory
argument_list|()
operator|.
name|getId
argument_list|()
expr_stmt|;
name|this
operator|.
name|changeId
operator|=
name|changeId
expr_stmt|;
name|this
operator|.
name|reviewers
operator|=
name|nameOrEmails
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|ReviewerResult
name|call
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|reviewerIds
init|=
operator|new
name|HashSet
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|ChangeControl
name|control
init|=
name|changeControlFactory
operator|.
name|validateFor
argument_list|(
name|changeId
argument_list|)
decl_stmt|;
specifier|final
name|ReviewerResult
name|result
init|=
operator|new
name|ReviewerResult
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|nameOrEmail
range|:
name|reviewers
control|)
block|{
specifier|final
name|Account
name|account
init|=
name|accountResolver
operator|.
name|find
argument_list|(
name|nameOrEmail
argument_list|)
decl_stmt|;
if|if
condition|(
name|account
operator|==
literal|null
condition|)
block|{
name|result
operator|.
name|addError
argument_list|(
operator|new
name|ReviewerResult
operator|.
name|Error
argument_list|(
name|ReviewerResult
operator|.
name|Error
operator|.
name|Type
operator|.
name|ACCOUNT_NOT_FOUND
argument_list|,
name|nameOrEmail
argument_list|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
operator|!
name|account
operator|.
name|isActive
argument_list|()
condition|)
block|{
name|result
operator|.
name|addError
argument_list|(
operator|new
name|ReviewerResult
operator|.
name|Error
argument_list|(
name|ReviewerResult
operator|.
name|Error
operator|.
name|Type
operator|.
name|ACCOUNT_INACTIVE
argument_list|,
name|formatUser
argument_list|(
name|account
argument_list|,
name|nameOrEmail
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
specifier|final
name|IdentifiedUser
name|user
init|=
name|identifiedUserFactory
operator|.
name|create
argument_list|(
name|account
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|control
operator|.
name|forUser
argument_list|(
name|user
argument_list|)
operator|.
name|isVisible
argument_list|()
condition|)
block|{
name|result
operator|.
name|addError
argument_list|(
operator|new
name|ReviewerResult
operator|.
name|Error
argument_list|(
name|ReviewerResult
operator|.
name|Error
operator|.
name|Type
operator|.
name|CHANGE_NOT_VISIBLE
argument_list|,
name|formatUser
argument_list|(
name|account
argument_list|,
name|nameOrEmail
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|reviewerIds
operator|.
name|add
argument_list|(
name|account
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|reviewerIds
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|result
return|;
block|}
comment|// Add the reviewers to the database
comment|//
specifier|final
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|added
init|=
operator|new
name|HashSet
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|PatchSetApproval
argument_list|>
name|toInsert
init|=
operator|new
name|ArrayList
argument_list|<
name|PatchSetApproval
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|PatchSet
operator|.
name|Id
name|psid
init|=
name|control
operator|.
name|getChange
argument_list|()
operator|.
name|currentPatchSetId
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Account
operator|.
name|Id
name|reviewer
range|:
name|reviewerIds
control|)
block|{
if|if
condition|(
operator|!
name|exists
argument_list|(
name|psid
argument_list|,
name|reviewer
argument_list|)
condition|)
block|{
comment|// This reviewer has not entered an approval for this change yet.
comment|//
specifier|final
name|PatchSetApproval
name|myca
init|=
name|dummyApproval
argument_list|(
name|psid
argument_list|,
name|reviewer
argument_list|)
decl_stmt|;
name|toInsert
operator|.
name|add
argument_list|(
name|myca
argument_list|)
expr_stmt|;
name|added
operator|.
name|add
argument_list|(
name|reviewer
argument_list|)
expr_stmt|;
block|}
block|}
name|db
operator|.
name|patchSetApprovals
argument_list|()
operator|.
name|insert
argument_list|(
name|toInsert
argument_list|)
expr_stmt|;
comment|// Email the reviewers
comment|//
comment|// The user knows they added themselves, don't bother emailing them.
name|added
operator|.
name|remove
argument_list|(
name|currentUser
operator|.
name|getAccountId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|added
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
specifier|final
name|AddReviewerSender
name|cm
decl_stmt|;
name|cm
operator|=
name|addReviewerSenderFactory
operator|.
name|create
argument_list|(
name|control
operator|.
name|getChange
argument_list|()
argument_list|)
expr_stmt|;
name|cm
operator|.
name|setFrom
argument_list|(
name|currentUser
operator|.
name|getAccountId
argument_list|()
argument_list|)
expr_stmt|;
name|cm
operator|.
name|addReviewers
argument_list|(
name|added
argument_list|)
expr_stmt|;
name|cm
operator|.
name|send
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|formatUser (Account account, String nameOrEmail)
specifier|private
name|String
name|formatUser
parameter_list|(
name|Account
name|account
parameter_list|,
name|String
name|nameOrEmail
parameter_list|)
block|{
if|if
condition|(
name|nameOrEmail
operator|.
name|matches
argument_list|(
literal|"^[1-9][0-9]*$"
argument_list|)
condition|)
block|{
return|return
name|RemoveReviewer
operator|.
name|formatUser
argument_list|(
name|account
argument_list|,
name|nameOrEmail
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|nameOrEmail
return|;
block|}
block|}
DECL|method|exists (final PatchSet.Id patchSetId, final Account.Id reviewerId)
specifier|private
name|boolean
name|exists
parameter_list|(
specifier|final
name|PatchSet
operator|.
name|Id
name|patchSetId
parameter_list|,
specifier|final
name|Account
operator|.
name|Id
name|reviewerId
parameter_list|)
throws|throws
name|OrmException
block|{
return|return
name|db
operator|.
name|patchSetApprovals
argument_list|()
operator|.
name|byPatchSetUser
argument_list|(
name|patchSetId
argument_list|,
name|reviewerId
argument_list|)
operator|.
name|iterator
argument_list|()
operator|.
name|hasNext
argument_list|()
return|;
block|}
DECL|method|dummyApproval (final PatchSet.Id patchSetId, final Account.Id reviewerId)
specifier|private
name|PatchSetApproval
name|dummyApproval
parameter_list|(
specifier|final
name|PatchSet
operator|.
name|Id
name|patchSetId
parameter_list|,
specifier|final
name|Account
operator|.
name|Id
name|reviewerId
parameter_list|)
block|{
return|return
operator|new
name|PatchSetApproval
argument_list|(
operator|new
name|PatchSetApproval
operator|.
name|Key
argument_list|(
name|patchSetId
argument_list|,
name|reviewerId
argument_list|,
name|addReviewerCategoryId
argument_list|)
argument_list|,
operator|(
name|short
operator|)
literal|0
argument_list|)
return|;
block|}
block|}
end_class

end_unit

