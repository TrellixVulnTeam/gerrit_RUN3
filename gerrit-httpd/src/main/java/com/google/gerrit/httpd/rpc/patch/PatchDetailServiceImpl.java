begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2008 The Android Open Source Project
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
DECL|package|com.google.gerrit.httpd.rpc.patch
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|httpd
operator|.
name|rpc
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
name|ApprovalSummary
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
name|ApprovalSummarySet
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
name|common
operator|.
name|data
operator|.
name|ChangeDetail
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
name|PatchDetailService
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
name|PatchScript
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
name|ReviewResult
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
name|errors
operator|.
name|NoSuchEntityException
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
name|httpd
operator|.
name|rpc
operator|.
name|BaseServiceImplementation
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
name|httpd
operator|.
name|rpc
operator|.
name|Handler
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
name|httpd
operator|.
name|rpc
operator|.
name|changedetail
operator|.
name|ChangeDetailFactory
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
name|AccountDiffPreference
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
name|AccountPatchReview
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
name|client
operator|.
name|ApprovalCategoryValue
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
name|Patch
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
name|PatchLineComment
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
name|Patch
operator|.
name|Key
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
name|AccountInfoCacheFactory
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
name|changedetail
operator|.
name|DeleteDraftPatchSet
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
name|patch
operator|.
name|PatchSetInfoNotAvailableException
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
name|patch
operator|.
name|PublishComments
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
name|workflow
operator|.
name|FunctionState
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|user
operator|.
name|client
operator|.
name|rpc
operator|.
name|AsyncCallback
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
name|client
operator|.
name|VoidResult
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
name|Set
import|;
end_import

begin_class
DECL|class|PatchDetailServiceImpl
class|class
name|PatchDetailServiceImpl
extends|extends
name|BaseServiceImplementation
implements|implements
name|PatchDetailService
block|{
DECL|field|approvalTypes
specifier|private
specifier|final
name|ApprovalTypes
name|approvalTypes
decl_stmt|;
DECL|field|accountInfoCacheFactory
specifier|private
specifier|final
name|AccountInfoCacheFactory
operator|.
name|Factory
name|accountInfoCacheFactory
decl_stmt|;
DECL|field|addReviewerHandlerFactory
specifier|private
specifier|final
name|AddReviewerHandler
operator|.
name|Factory
name|addReviewerHandlerFactory
decl_stmt|;
DECL|field|changeControlFactory
specifier|private
specifier|final
name|ChangeControl
operator|.
name|Factory
name|changeControlFactory
decl_stmt|;
DECL|field|deleteDraftPatchSetFactory
specifier|private
specifier|final
name|DeleteDraftPatchSet
operator|.
name|Factory
name|deleteDraftPatchSetFactory
decl_stmt|;
DECL|field|removeReviewerHandlerFactory
specifier|private
specifier|final
name|RemoveReviewerHandler
operator|.
name|Factory
name|removeReviewerHandlerFactory
decl_stmt|;
DECL|field|functionStateFactory
specifier|private
specifier|final
name|FunctionState
operator|.
name|Factory
name|functionStateFactory
decl_stmt|;
DECL|field|publishCommentsFactory
specifier|private
specifier|final
name|PublishComments
operator|.
name|Factory
name|publishCommentsFactory
decl_stmt|;
DECL|field|patchScriptFactoryFactory
specifier|private
specifier|final
name|PatchScriptFactory
operator|.
name|Factory
name|patchScriptFactoryFactory
decl_stmt|;
DECL|field|saveDraftFactory
specifier|private
specifier|final
name|SaveDraft
operator|.
name|Factory
name|saveDraftFactory
decl_stmt|;
DECL|field|changeDetailFactory
specifier|private
specifier|final
name|ChangeDetailFactory
operator|.
name|Factory
name|changeDetailFactory
decl_stmt|;
annotation|@
name|Inject
DECL|method|PatchDetailServiceImpl (final Provider<ReviewDb> schema, final Provider<CurrentUser> currentUser, final ApprovalTypes approvalTypes, final AccountInfoCacheFactory.Factory accountInfoCacheFactory, final AddReviewerHandler.Factory addReviewerHandlerFactory, final RemoveReviewerHandler.Factory removeReviewerHandlerFactory, final ChangeControl.Factory changeControlFactory, final DeleteDraftPatchSet.Factory deleteDraftPatchSetFactory, final FunctionState.Factory functionStateFactory, final PatchScriptFactory.Factory patchScriptFactoryFactory, final PublishComments.Factory publishCommentsFactory, final SaveDraft.Factory saveDraftFactory, final ChangeDetailFactory.Factory changeDetailFactory)
name|PatchDetailServiceImpl
parameter_list|(
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|schema
parameter_list|,
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|currentUser
parameter_list|,
specifier|final
name|ApprovalTypes
name|approvalTypes
parameter_list|,
specifier|final
name|AccountInfoCacheFactory
operator|.
name|Factory
name|accountInfoCacheFactory
parameter_list|,
specifier|final
name|AddReviewerHandler
operator|.
name|Factory
name|addReviewerHandlerFactory
parameter_list|,
specifier|final
name|RemoveReviewerHandler
operator|.
name|Factory
name|removeReviewerHandlerFactory
parameter_list|,
specifier|final
name|ChangeControl
operator|.
name|Factory
name|changeControlFactory
parameter_list|,
specifier|final
name|DeleteDraftPatchSet
operator|.
name|Factory
name|deleteDraftPatchSetFactory
parameter_list|,
specifier|final
name|FunctionState
operator|.
name|Factory
name|functionStateFactory
parameter_list|,
specifier|final
name|PatchScriptFactory
operator|.
name|Factory
name|patchScriptFactoryFactory
parameter_list|,
specifier|final
name|PublishComments
operator|.
name|Factory
name|publishCommentsFactory
parameter_list|,
specifier|final
name|SaveDraft
operator|.
name|Factory
name|saveDraftFactory
parameter_list|,
specifier|final
name|ChangeDetailFactory
operator|.
name|Factory
name|changeDetailFactory
parameter_list|)
block|{
name|super
argument_list|(
name|schema
argument_list|,
name|currentUser
argument_list|)
expr_stmt|;
name|this
operator|.
name|approvalTypes
operator|=
name|approvalTypes
expr_stmt|;
name|this
operator|.
name|accountInfoCacheFactory
operator|=
name|accountInfoCacheFactory
expr_stmt|;
name|this
operator|.
name|addReviewerHandlerFactory
operator|=
name|addReviewerHandlerFactory
expr_stmt|;
name|this
operator|.
name|removeReviewerHandlerFactory
operator|=
name|removeReviewerHandlerFactory
expr_stmt|;
name|this
operator|.
name|changeControlFactory
operator|=
name|changeControlFactory
expr_stmt|;
name|this
operator|.
name|deleteDraftPatchSetFactory
operator|=
name|deleteDraftPatchSetFactory
expr_stmt|;
name|this
operator|.
name|functionStateFactory
operator|=
name|functionStateFactory
expr_stmt|;
name|this
operator|.
name|patchScriptFactoryFactory
operator|=
name|patchScriptFactoryFactory
expr_stmt|;
name|this
operator|.
name|publishCommentsFactory
operator|=
name|publishCommentsFactory
expr_stmt|;
name|this
operator|.
name|saveDraftFactory
operator|=
name|saveDraftFactory
expr_stmt|;
name|this
operator|.
name|changeDetailFactory
operator|=
name|changeDetailFactory
expr_stmt|;
block|}
DECL|method|patchScript (final Patch.Key patchKey, final PatchSet.Id psa, final PatchSet.Id psb, final AccountDiffPreference dp, final AsyncCallback<PatchScript> callback)
specifier|public
name|void
name|patchScript
parameter_list|(
specifier|final
name|Patch
operator|.
name|Key
name|patchKey
parameter_list|,
specifier|final
name|PatchSet
operator|.
name|Id
name|psa
parameter_list|,
specifier|final
name|PatchSet
operator|.
name|Id
name|psb
parameter_list|,
specifier|final
name|AccountDiffPreference
name|dp
parameter_list|,
specifier|final
name|AsyncCallback
argument_list|<
name|PatchScript
argument_list|>
name|callback
parameter_list|)
block|{
if|if
condition|(
name|psb
operator|==
literal|null
condition|)
block|{
name|callback
operator|.
name|onFailure
argument_list|(
operator|new
name|NoSuchEntityException
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|patchScriptFactoryFactory
operator|.
name|create
argument_list|(
name|patchKey
argument_list|,
name|psa
argument_list|,
name|psb
argument_list|,
name|dp
argument_list|)
operator|.
name|to
argument_list|(
name|callback
argument_list|)
expr_stmt|;
block|}
DECL|method|saveDraft (final PatchLineComment comment, final AsyncCallback<PatchLineComment> callback)
specifier|public
name|void
name|saveDraft
parameter_list|(
specifier|final
name|PatchLineComment
name|comment
parameter_list|,
specifier|final
name|AsyncCallback
argument_list|<
name|PatchLineComment
argument_list|>
name|callback
parameter_list|)
block|{
name|saveDraftFactory
operator|.
name|create
argument_list|(
name|comment
argument_list|)
operator|.
name|to
argument_list|(
name|callback
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteDraft (final PatchLineComment.Key commentKey, final AsyncCallback<VoidResult> callback)
specifier|public
name|void
name|deleteDraft
parameter_list|(
specifier|final
name|PatchLineComment
operator|.
name|Key
name|commentKey
parameter_list|,
specifier|final
name|AsyncCallback
argument_list|<
name|VoidResult
argument_list|>
name|callback
parameter_list|)
block|{
name|run
argument_list|(
name|callback
argument_list|,
operator|new
name|Action
argument_list|<
name|VoidResult
argument_list|>
argument_list|()
block|{
specifier|public
name|VoidResult
name|run
parameter_list|(
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
throws|,
name|Failure
block|{
name|Change
operator|.
name|Id
name|id
init|=
name|commentKey
operator|.
name|getParentKey
argument_list|()
operator|.
name|getParentKey
argument_list|()
operator|.
name|getParentKey
argument_list|()
decl_stmt|;
name|db
operator|.
name|changes
argument_list|()
operator|.
name|beginTransaction
argument_list|(
name|id
argument_list|)
expr_stmt|;
try|try
block|{
specifier|final
name|PatchLineComment
name|comment
init|=
name|db
operator|.
name|patchComments
argument_list|()
operator|.
name|get
argument_list|(
name|commentKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|comment
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
operator|new
name|NoSuchEntityException
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|getAccountId
argument_list|()
operator|.
name|equals
argument_list|(
name|comment
operator|.
name|getAuthor
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
operator|new
name|NoSuchEntityException
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|comment
operator|.
name|getStatus
argument_list|()
operator|!=
name|PatchLineComment
operator|.
name|Status
operator|.
name|DRAFT
condition|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
operator|new
name|IllegalStateException
argument_list|(
literal|"Comment published"
argument_list|)
argument_list|)
throw|;
block|}
name|db
operator|.
name|patchComments
argument_list|()
operator|.
name|delete
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|comment
argument_list|)
argument_list|)
expr_stmt|;
name|db
operator|.
name|commit
argument_list|()
expr_stmt|;
return|return
name|VoidResult
operator|.
name|INSTANCE
return|;
block|}
finally|finally
block|{
name|db
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteDraftPatchSet (final PatchSet.Id psid, final AsyncCallback<ChangeDetail> callback)
specifier|public
name|void
name|deleteDraftPatchSet
parameter_list|(
specifier|final
name|PatchSet
operator|.
name|Id
name|psid
parameter_list|,
specifier|final
name|AsyncCallback
argument_list|<
name|ChangeDetail
argument_list|>
name|callback
parameter_list|)
block|{
name|run
argument_list|(
name|callback
argument_list|,
operator|new
name|Action
argument_list|<
name|ChangeDetail
argument_list|>
argument_list|()
block|{
specifier|public
name|ChangeDetail
name|run
parameter_list|(
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
throws|,
name|Failure
block|{
name|ReviewResult
name|result
init|=
literal|null
decl_stmt|;
try|try
block|{
name|result
operator|=
name|deleteDraftPatchSetFactory
operator|.
name|create
argument_list|(
name|psid
argument_list|)
operator|.
name|call
argument_list|()
expr_stmt|;
if|if
condition|(
name|result
operator|.
name|getErrors
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
operator|new
name|NoSuchEntityException
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|result
operator|.
name|getChangeId
argument_list|()
operator|==
literal|null
condition|)
block|{
comment|// the change was deleted because the draft patch set that was
comment|// deleted was the only patch set in the change
return|return
literal|null
return|;
block|}
return|return
name|changeDetailFactory
operator|.
name|create
argument_list|(
name|result
operator|.
name|getChangeId
argument_list|()
argument_list|)
operator|.
name|call
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchChangeException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
operator|new
name|NoSuchChangeException
argument_list|(
name|result
operator|.
name|getChangeId
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|NoSuchEntityException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|PatchSetInfoNotAvailableException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|publishComments (final PatchSet.Id psid, final String msg, final Set<ApprovalCategoryValue.Id> tags, final AsyncCallback<VoidResult> cb)
specifier|public
name|void
name|publishComments
parameter_list|(
specifier|final
name|PatchSet
operator|.
name|Id
name|psid
parameter_list|,
specifier|final
name|String
name|msg
parameter_list|,
specifier|final
name|Set
argument_list|<
name|ApprovalCategoryValue
operator|.
name|Id
argument_list|>
name|tags
parameter_list|,
specifier|final
name|AsyncCallback
argument_list|<
name|VoidResult
argument_list|>
name|cb
parameter_list|)
block|{
name|Handler
operator|.
name|wrap
argument_list|(
name|publishCommentsFactory
operator|.
name|create
argument_list|(
name|psid
argument_list|,
name|msg
argument_list|,
name|tags
argument_list|,
literal|false
argument_list|)
argument_list|)
operator|.
name|to
argument_list|(
name|cb
argument_list|)
expr_stmt|;
block|}
comment|/**    * Update the reviewed status for the file by user @code{account}    */
DECL|method|setReviewedByCurrentUser (final Key patchKey, final boolean reviewed, AsyncCallback<VoidResult> callback)
specifier|public
name|void
name|setReviewedByCurrentUser
parameter_list|(
specifier|final
name|Key
name|patchKey
parameter_list|,
specifier|final
name|boolean
name|reviewed
parameter_list|,
name|AsyncCallback
argument_list|<
name|VoidResult
argument_list|>
name|callback
parameter_list|)
block|{
name|run
argument_list|(
name|callback
argument_list|,
operator|new
name|Action
argument_list|<
name|VoidResult
argument_list|>
argument_list|()
block|{
specifier|public
name|VoidResult
name|run
parameter_list|(
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
block|{
name|Account
operator|.
name|Id
name|account
init|=
name|getAccountId
argument_list|()
decl_stmt|;
name|AccountPatchReview
operator|.
name|Key
name|key
init|=
operator|new
name|AccountPatchReview
operator|.
name|Key
argument_list|(
name|patchKey
argument_list|,
name|account
argument_list|)
decl_stmt|;
name|db
operator|.
name|accounts
argument_list|()
operator|.
name|beginTransaction
argument_list|(
name|account
argument_list|)
expr_stmt|;
try|try
block|{
name|AccountPatchReview
name|apr
init|=
name|db
operator|.
name|accountPatchReviews
argument_list|()
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|apr
operator|==
literal|null
operator|&&
name|reviewed
condition|)
block|{
name|db
operator|.
name|accountPatchReviews
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
operator|new
name|AccountPatchReview
argument_list|(
name|patchKey
argument_list|,
name|account
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|apr
operator|!=
literal|null
operator|&&
operator|!
name|reviewed
condition|)
block|{
name|db
operator|.
name|accountPatchReviews
argument_list|()
operator|.
name|delete
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|apr
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|db
operator|.
name|commit
argument_list|()
expr_stmt|;
return|return
name|VoidResult
operator|.
name|INSTANCE
return|;
block|}
finally|finally
block|{
name|db
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|addReviewers (final Change.Id id, final List<String> reviewers, final boolean confirmed, final AsyncCallback<ReviewerResult> callback)
specifier|public
name|void
name|addReviewers
parameter_list|(
specifier|final
name|Change
operator|.
name|Id
name|id
parameter_list|,
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|reviewers
parameter_list|,
specifier|final
name|boolean
name|confirmed
parameter_list|,
specifier|final
name|AsyncCallback
argument_list|<
name|ReviewerResult
argument_list|>
name|callback
parameter_list|)
block|{
name|addReviewerHandlerFactory
operator|.
name|create
argument_list|(
name|id
argument_list|,
name|reviewers
argument_list|,
name|confirmed
argument_list|)
operator|.
name|to
argument_list|(
name|callback
argument_list|)
expr_stmt|;
block|}
DECL|method|removeReviewer (final Change.Id id, final Account.Id reviewerId, final AsyncCallback<ReviewerResult> callback)
specifier|public
name|void
name|removeReviewer
parameter_list|(
specifier|final
name|Change
operator|.
name|Id
name|id
parameter_list|,
specifier|final
name|Account
operator|.
name|Id
name|reviewerId
parameter_list|,
specifier|final
name|AsyncCallback
argument_list|<
name|ReviewerResult
argument_list|>
name|callback
parameter_list|)
block|{
name|removeReviewerHandlerFactory
operator|.
name|create
argument_list|(
name|id
argument_list|,
name|reviewerId
argument_list|)
operator|.
name|to
argument_list|(
name|callback
argument_list|)
expr_stmt|;
block|}
DECL|method|userApprovals (final Set<Change.Id> cids, final Account.Id aid, final AsyncCallback<ApprovalSummarySet> callback)
specifier|public
name|void
name|userApprovals
parameter_list|(
specifier|final
name|Set
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
name|cids
parameter_list|,
specifier|final
name|Account
operator|.
name|Id
name|aid
parameter_list|,
specifier|final
name|AsyncCallback
argument_list|<
name|ApprovalSummarySet
argument_list|>
name|callback
parameter_list|)
block|{
name|run
argument_list|(
name|callback
argument_list|,
operator|new
name|Action
argument_list|<
name|ApprovalSummarySet
argument_list|>
argument_list|()
block|{
specifier|public
name|ApprovalSummarySet
name|run
parameter_list|(
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
block|{
specifier|final
name|Map
argument_list|<
name|Change
operator|.
name|Id
argument_list|,
name|ApprovalSummary
argument_list|>
name|approvals
init|=
operator|new
name|HashMap
argument_list|<
name|Change
operator|.
name|Id
argument_list|,
name|ApprovalSummary
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|AccountInfoCacheFactory
name|aicFactory
init|=
name|accountInfoCacheFactory
operator|.
name|create
argument_list|()
decl_stmt|;
name|aicFactory
operator|.
name|want
argument_list|(
name|aid
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|Change
operator|.
name|Id
name|id
range|:
name|cids
control|)
block|{
try|try
block|{
specifier|final
name|ChangeControl
name|cc
init|=
name|changeControlFactory
operator|.
name|validateFor
argument_list|(
name|id
argument_list|)
decl_stmt|;
specifier|final
name|Change
name|change
init|=
name|cc
operator|.
name|getChange
argument_list|()
decl_stmt|;
specifier|final
name|PatchSet
operator|.
name|Id
name|ps_id
init|=
name|change
operator|.
name|currentPatchSetId
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|ApprovalCategory
operator|.
name|Id
argument_list|,
name|PatchSetApproval
argument_list|>
name|psas
init|=
operator|new
name|HashMap
argument_list|<
name|ApprovalCategory
operator|.
name|Id
argument_list|,
name|PatchSetApproval
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|FunctionState
name|fs
init|=
name|functionStateFactory
operator|.
name|create
argument_list|(
name|cc
argument_list|,
name|ps_id
argument_list|,
name|psas
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|PatchSetApproval
name|ca
range|:
name|db
operator|.
name|patchSetApprovals
argument_list|()
operator|.
name|byPatchSetUser
argument_list|(
name|ps_id
argument_list|,
name|aid
argument_list|)
control|)
block|{
specifier|final
name|ApprovalCategory
operator|.
name|Id
name|category
init|=
name|ca
operator|.
name|getCategoryId
argument_list|()
decl_stmt|;
if|if
condition|(
name|ApprovalCategory
operator|.
name|SUBMIT
operator|.
name|equals
argument_list|(
name|category
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|change
operator|.
name|getStatus
argument_list|()
operator|.
name|isOpen
argument_list|()
condition|)
block|{
name|fs
operator|.
name|normalize
argument_list|(
name|approvalTypes
operator|.
name|byId
argument_list|(
name|category
argument_list|)
argument_list|,
name|ca
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ca
operator|.
name|getValue
argument_list|()
operator|==
literal|0
condition|)
block|{
continue|continue;
block|}
name|psas
operator|.
name|put
argument_list|(
name|category
argument_list|,
name|ca
argument_list|)
expr_stmt|;
block|}
name|approvals
operator|.
name|put
argument_list|(
name|id
argument_list|,
operator|new
name|ApprovalSummary
argument_list|(
name|psas
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchChangeException
name|nsce
parameter_list|)
block|{
comment|/*              * The user has no access to see this change, so we simply do not              * provide any details about it.              */
block|}
block|}
return|return
operator|new
name|ApprovalSummarySet
argument_list|(
name|aicFactory
operator|.
name|create
argument_list|()
argument_list|,
name|approvals
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|strongestApprovals (final Set<Change.Id> cids, final AsyncCallback<ApprovalSummarySet> callback)
specifier|public
name|void
name|strongestApprovals
parameter_list|(
specifier|final
name|Set
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
name|cids
parameter_list|,
specifier|final
name|AsyncCallback
argument_list|<
name|ApprovalSummarySet
argument_list|>
name|callback
parameter_list|)
block|{
name|run
argument_list|(
name|callback
argument_list|,
operator|new
name|Action
argument_list|<
name|ApprovalSummarySet
argument_list|>
argument_list|()
block|{
specifier|public
name|ApprovalSummarySet
name|run
parameter_list|(
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
block|{
specifier|final
name|Map
argument_list|<
name|Change
operator|.
name|Id
argument_list|,
name|ApprovalSummary
argument_list|>
name|approvals
init|=
operator|new
name|HashMap
argument_list|<
name|Change
operator|.
name|Id
argument_list|,
name|ApprovalSummary
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|AccountInfoCacheFactory
name|aicFactory
init|=
name|accountInfoCacheFactory
operator|.
name|create
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Change
operator|.
name|Id
name|id
range|:
name|cids
control|)
block|{
try|try
block|{
specifier|final
name|ChangeControl
name|cc
init|=
name|changeControlFactory
operator|.
name|validateFor
argument_list|(
name|id
argument_list|)
decl_stmt|;
specifier|final
name|Change
name|change
init|=
name|cc
operator|.
name|getChange
argument_list|()
decl_stmt|;
specifier|final
name|PatchSet
operator|.
name|Id
name|ps_id
init|=
name|change
operator|.
name|currentPatchSetId
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|ApprovalCategory
operator|.
name|Id
argument_list|,
name|PatchSetApproval
argument_list|>
name|psas
init|=
operator|new
name|HashMap
argument_list|<
name|ApprovalCategory
operator|.
name|Id
argument_list|,
name|PatchSetApproval
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|FunctionState
name|fs
init|=
name|functionStateFactory
operator|.
name|create
argument_list|(
name|cc
argument_list|,
name|ps_id
argument_list|,
name|psas
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|PatchSetApproval
name|ca
range|:
name|db
operator|.
name|patchSetApprovals
argument_list|()
operator|.
name|byPatchSet
argument_list|(
name|ps_id
argument_list|)
control|)
block|{
specifier|final
name|ApprovalCategory
operator|.
name|Id
name|category
init|=
name|ca
operator|.
name|getCategoryId
argument_list|()
decl_stmt|;
if|if
condition|(
name|ApprovalCategory
operator|.
name|SUBMIT
operator|.
name|equals
argument_list|(
name|category
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|change
operator|.
name|getStatus
argument_list|()
operator|.
name|isOpen
argument_list|()
condition|)
block|{
name|fs
operator|.
name|normalize
argument_list|(
name|approvalTypes
operator|.
name|byId
argument_list|(
name|category
argument_list|)
argument_list|,
name|ca
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ca
operator|.
name|getValue
argument_list|()
operator|==
literal|0
condition|)
block|{
continue|continue;
block|}
name|boolean
name|keep
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|psas
operator|.
name|containsKey
argument_list|(
name|category
argument_list|)
condition|)
block|{
specifier|final
name|short
name|oldValue
init|=
name|psas
operator|.
name|get
argument_list|(
name|category
argument_list|)
operator|.
name|getValue
argument_list|()
decl_stmt|;
specifier|final
name|short
name|newValue
init|=
name|ca
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|keep
operator|=
operator|(
name|Math
operator|.
name|abs
argument_list|(
name|oldValue
argument_list|)
operator|<
name|Math
operator|.
name|abs
argument_list|(
name|newValue
argument_list|)
operator|)
operator|||
operator|(
operator|(
name|Math
operator|.
name|abs
argument_list|(
name|oldValue
argument_list|)
operator|==
name|Math
operator|.
name|abs
argument_list|(
name|newValue
argument_list|)
operator|&&
operator|(
name|newValue
operator|<
name|oldValue
operator|)
operator|)
operator|)
expr_stmt|;
block|}
if|if
condition|(
name|keep
condition|)
block|{
name|aicFactory
operator|.
name|want
argument_list|(
name|ca
operator|.
name|getAccountId
argument_list|()
argument_list|)
expr_stmt|;
name|psas
operator|.
name|put
argument_list|(
name|category
argument_list|,
name|ca
argument_list|)
expr_stmt|;
block|}
block|}
name|approvals
operator|.
name|put
argument_list|(
name|id
argument_list|,
operator|new
name|ApprovalSummary
argument_list|(
name|psas
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchChangeException
name|nsce
parameter_list|)
block|{
comment|/*              * The user has no access to see this change, so we simply do not              * provide any details about it.              */
block|}
block|}
return|return
operator|new
name|ApprovalSummarySet
argument_list|(
name|aicFactory
operator|.
name|create
argument_list|()
argument_list|,
name|approvals
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

