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
DECL|package|com.google.gerrit.httpd.rpc.changedetail
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
name|changedetail
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
name|ApprovalDetail
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
name|ChangeInfo
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
name|SubmitRecord
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
name|PatchSetAncestor
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
name|RevId
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
name|AnonymousUser
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
name|ChangeUtil
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
name|config
operator|.
name|GerritServerConfig
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
name|MergeOp
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
name|CategoryFunction
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
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Config
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
name|Comparator
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
comment|/** Creates a {@link ChangeDetail} from a {@link Change}. */
end_comment

begin_class
DECL|class|ChangeDetailFactory
specifier|public
class|class
name|ChangeDetailFactory
extends|extends
name|Handler
argument_list|<
name|ChangeDetail
argument_list|>
block|{
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (Change.Id id)
name|ChangeDetailFactory
name|create
parameter_list|(
name|Change
operator|.
name|Id
name|id
parameter_list|)
function_decl|;
block|}
DECL|field|approvalTypes
specifier|private
specifier|final
name|ApprovalTypes
name|approvalTypes
decl_stmt|;
DECL|field|changeControlFactory
specifier|private
specifier|final
name|ChangeControl
operator|.
name|Factory
name|changeControlFactory
decl_stmt|;
DECL|field|functionState
specifier|private
specifier|final
name|FunctionState
operator|.
name|Factory
name|functionState
decl_stmt|;
DECL|field|patchSetDetail
specifier|private
specifier|final
name|PatchSetDetailFactory
operator|.
name|Factory
name|patchSetDetail
decl_stmt|;
DECL|field|aic
specifier|private
specifier|final
name|AccountInfoCacheFactory
name|aic
decl_stmt|;
DECL|field|anonymousUser
specifier|private
specifier|final
name|AnonymousUser
name|anonymousUser
decl_stmt|;
DECL|field|db
specifier|private
specifier|final
name|ReviewDb
name|db
decl_stmt|;
DECL|field|changeId
specifier|private
specifier|final
name|Change
operator|.
name|Id
name|changeId
decl_stmt|;
DECL|field|detail
specifier|private
name|ChangeDetail
name|detail
decl_stmt|;
DECL|field|control
specifier|private
name|ChangeControl
name|control
decl_stmt|;
DECL|field|opFactory
specifier|private
specifier|final
name|MergeOp
operator|.
name|Factory
name|opFactory
decl_stmt|;
DECL|field|testMerge
specifier|private
name|boolean
name|testMerge
decl_stmt|;
annotation|@
name|Inject
DECL|method|ChangeDetailFactory (final ApprovalTypes approvalTypes, final FunctionState.Factory functionState, final PatchSetDetailFactory.Factory patchSetDetail, final ReviewDb db, final ChangeControl.Factory changeControlFactory, final AccountInfoCacheFactory.Factory accountInfoCacheFactory, final AnonymousUser anonymousUser, final MergeOp.Factory opFactory, @GerritServerConfig final Config cfg, @Assisted final Change.Id id)
name|ChangeDetailFactory
parameter_list|(
specifier|final
name|ApprovalTypes
name|approvalTypes
parameter_list|,
specifier|final
name|FunctionState
operator|.
name|Factory
name|functionState
parameter_list|,
specifier|final
name|PatchSetDetailFactory
operator|.
name|Factory
name|patchSetDetail
parameter_list|,
specifier|final
name|ReviewDb
name|db
parameter_list|,
specifier|final
name|ChangeControl
operator|.
name|Factory
name|changeControlFactory
parameter_list|,
specifier|final
name|AccountInfoCacheFactory
operator|.
name|Factory
name|accountInfoCacheFactory
parameter_list|,
specifier|final
name|AnonymousUser
name|anonymousUser
parameter_list|,
specifier|final
name|MergeOp
operator|.
name|Factory
name|opFactory
parameter_list|,
annotation|@
name|GerritServerConfig
specifier|final
name|Config
name|cfg
parameter_list|,
annotation|@
name|Assisted
specifier|final
name|Change
operator|.
name|Id
name|id
parameter_list|)
block|{
name|this
operator|.
name|approvalTypes
operator|=
name|approvalTypes
expr_stmt|;
name|this
operator|.
name|functionState
operator|=
name|functionState
expr_stmt|;
name|this
operator|.
name|patchSetDetail
operator|=
name|patchSetDetail
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
name|anonymousUser
operator|=
name|anonymousUser
expr_stmt|;
name|this
operator|.
name|aic
operator|=
name|accountInfoCacheFactory
operator|.
name|create
argument_list|()
expr_stmt|;
name|this
operator|.
name|opFactory
operator|=
name|opFactory
expr_stmt|;
name|this
operator|.
name|testMerge
operator|=
name|cfg
operator|.
name|getBoolean
argument_list|(
literal|"changeMerge"
argument_list|,
literal|"test"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|changeId
operator|=
name|id
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|ChangeDetail
name|call
parameter_list|()
throws|throws
name|OrmException
throws|,
name|NoSuchEntityException
throws|,
name|PatchSetInfoNotAvailableException
throws|,
name|NoSuchChangeException
block|{
name|control
operator|=
name|changeControlFactory
operator|.
name|validateFor
argument_list|(
name|changeId
argument_list|)
expr_stmt|;
specifier|final
name|Change
name|change
init|=
name|control
operator|.
name|getChange
argument_list|()
decl_stmt|;
specifier|final
name|PatchSet
name|patch
init|=
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|get
argument_list|(
name|change
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|patch
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NoSuchEntityException
argument_list|()
throw|;
block|}
name|aic
operator|.
name|want
argument_list|(
name|change
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|detail
operator|=
operator|new
name|ChangeDetail
argument_list|()
expr_stmt|;
name|detail
operator|.
name|setChange
argument_list|(
name|change
argument_list|)
expr_stmt|;
name|detail
operator|.
name|setAllowsAnonymous
argument_list|(
name|control
operator|.
name|forUser
argument_list|(
name|anonymousUser
argument_list|)
operator|.
name|isVisible
argument_list|(
name|db
argument_list|)
argument_list|)
expr_stmt|;
name|detail
operator|.
name|setCanAbandon
argument_list|(
name|change
operator|.
name|getStatus
argument_list|()
operator|.
name|isOpen
argument_list|()
operator|&&
name|control
operator|.
name|canAbandon
argument_list|()
argument_list|)
expr_stmt|;
name|detail
operator|.
name|setCanRestore
argument_list|(
name|change
operator|.
name|getStatus
argument_list|()
operator|==
name|Change
operator|.
name|Status
operator|.
name|ABANDONED
operator|&&
name|control
operator|.
name|canRestore
argument_list|()
argument_list|)
expr_stmt|;
name|detail
operator|.
name|setStarred
argument_list|(
name|control
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getStarredChanges
argument_list|()
operator|.
name|contains
argument_list|(
name|changeId
argument_list|)
argument_list|)
expr_stmt|;
name|detail
operator|.
name|setCanRevert
argument_list|(
name|change
operator|.
name|getStatus
argument_list|()
operator|==
name|Change
operator|.
name|Status
operator|.
name|MERGED
operator|&&
name|control
operator|.
name|canAddPatchSet
argument_list|()
argument_list|)
expr_stmt|;
name|detail
operator|.
name|setCanEdit
argument_list|(
name|control
operator|.
name|getRefControl
argument_list|()
operator|.
name|canWrite
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|detail
operator|.
name|getChange
argument_list|()
operator|.
name|getStatus
argument_list|()
operator|.
name|isOpen
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|SubmitRecord
argument_list|>
name|submitRecords
init|=
name|control
operator|.
name|canSubmit
argument_list|(
name|db
argument_list|,
name|patch
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|SubmitRecord
name|rec
range|:
name|submitRecords
control|)
block|{
if|if
condition|(
name|rec
operator|.
name|labels
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|SubmitRecord
operator|.
name|Label
name|lbl
range|:
name|rec
operator|.
name|labels
control|)
block|{
name|aic
operator|.
name|want
argument_list|(
name|lbl
operator|.
name|appliedBy
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|rec
operator|.
name|status
operator|==
name|SubmitRecord
operator|.
name|Status
operator|.
name|OK
operator|&&
name|control
operator|.
name|getRefControl
argument_list|()
operator|.
name|canSubmit
argument_list|()
condition|)
block|{
name|detail
operator|.
name|setCanSubmit
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
name|detail
operator|.
name|setSubmitRecords
argument_list|(
name|submitRecords
argument_list|)
expr_stmt|;
block|}
name|loadPatchSets
argument_list|()
expr_stmt|;
name|loadMessages
argument_list|()
expr_stmt|;
if|if
condition|(
name|change
operator|.
name|currentPatchSetId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|loadCurrentPatchSet
argument_list|()
expr_stmt|;
block|}
name|load
argument_list|()
expr_stmt|;
name|detail
operator|.
name|setAccounts
argument_list|(
name|aic
operator|.
name|create
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|detail
return|;
block|}
DECL|method|loadPatchSets ()
specifier|private
name|void
name|loadPatchSets
parameter_list|()
throws|throws
name|OrmException
block|{
name|detail
operator|.
name|setPatchSets
argument_list|(
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|byChange
argument_list|(
name|changeId
argument_list|)
operator|.
name|toList
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|loadMessages ()
specifier|private
name|void
name|loadMessages
parameter_list|()
throws|throws
name|OrmException
block|{
name|detail
operator|.
name|setMessages
argument_list|(
name|db
operator|.
name|changeMessages
argument_list|()
operator|.
name|byChange
argument_list|(
name|changeId
argument_list|)
operator|.
name|toList
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|ChangeMessage
name|m
range|:
name|detail
operator|.
name|getMessages
argument_list|()
control|)
block|{
name|aic
operator|.
name|want
argument_list|(
name|m
operator|.
name|getAuthor
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|load ()
specifier|private
name|void
name|load
parameter_list|()
throws|throws
name|OrmException
throws|,
name|NoSuchChangeException
block|{
if|if
condition|(
name|detail
operator|.
name|getChange
argument_list|()
operator|.
name|getStatus
argument_list|()
operator|.
name|equals
argument_list|(
name|Change
operator|.
name|Status
operator|.
name|NEW
argument_list|)
operator|&&
name|testMerge
condition|)
block|{
name|ChangeUtil
operator|.
name|testMerge
argument_list|(
name|opFactory
argument_list|,
name|detail
operator|.
name|getChange
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|PatchSet
operator|.
name|Id
name|psId
init|=
name|detail
operator|.
name|getChange
argument_list|()
operator|.
name|currentPatchSetId
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|PatchSetApproval
argument_list|>
name|allApprovals
init|=
name|db
operator|.
name|patchSetApprovals
argument_list|()
operator|.
name|byChange
argument_list|(
name|changeId
argument_list|)
operator|.
name|toList
argument_list|()
decl_stmt|;
if|if
condition|(
name|detail
operator|.
name|getChange
argument_list|()
operator|.
name|getStatus
argument_list|()
operator|.
name|isOpen
argument_list|()
condition|)
block|{
specifier|final
name|FunctionState
name|fs
init|=
name|functionState
operator|.
name|create
argument_list|(
name|control
argument_list|,
name|psId
argument_list|,
name|allApprovals
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|ApprovalType
name|at
range|:
name|approvalTypes
operator|.
name|getApprovalTypes
argument_list|()
control|)
block|{
name|CategoryFunction
operator|.
name|forCategory
argument_list|(
name|at
operator|.
name|getCategory
argument_list|()
argument_list|)
operator|.
name|run
argument_list|(
name|at
argument_list|,
name|fs
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|boolean
name|canRemoveReviewers
init|=
name|detail
operator|.
name|getChange
argument_list|()
operator|.
name|getStatus
argument_list|()
operator|.
name|isOpen
argument_list|()
comment|//
operator|&&
name|control
operator|.
name|getCurrentUser
argument_list|()
operator|instanceof
name|IdentifiedUser
decl_stmt|;
specifier|final
name|HashMap
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|ApprovalDetail
argument_list|>
name|ad
init|=
operator|new
name|HashMap
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|ApprovalDetail
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|PatchSetApproval
name|ca
range|:
name|allApprovals
control|)
block|{
name|ApprovalDetail
name|d
init|=
name|ad
operator|.
name|get
argument_list|(
name|ca
operator|.
name|getAccountId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|d
operator|==
literal|null
condition|)
block|{
name|d
operator|=
operator|new
name|ApprovalDetail
argument_list|(
name|ca
operator|.
name|getAccountId
argument_list|()
argument_list|)
expr_stmt|;
name|d
operator|.
name|setCanRemove
argument_list|(
name|canRemoveReviewers
argument_list|)
expr_stmt|;
name|ad
operator|.
name|put
argument_list|(
name|d
operator|.
name|getAccount
argument_list|()
argument_list|,
name|d
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|d
operator|.
name|canRemove
argument_list|()
condition|)
block|{
name|d
operator|.
name|setCanRemove
argument_list|(
name|control
operator|.
name|canRemoveReviewer
argument_list|(
name|ca
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ca
operator|.
name|getPatchSetId
argument_list|()
operator|.
name|equals
argument_list|(
name|psId
argument_list|)
condition|)
block|{
name|d
operator|.
name|add
argument_list|(
name|ca
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|Account
operator|.
name|Id
name|owner
init|=
name|detail
operator|.
name|getChange
argument_list|()
operator|.
name|getOwner
argument_list|()
decl_stmt|;
if|if
condition|(
name|ad
operator|.
name|containsKey
argument_list|(
name|owner
argument_list|)
condition|)
block|{
comment|// Ensure the owner always sorts to the top of the table
comment|//
name|ad
operator|.
name|get
argument_list|(
name|owner
argument_list|)
operator|.
name|sortFirst
argument_list|()
expr_stmt|;
block|}
name|aic
operator|.
name|want
argument_list|(
name|ad
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
name|detail
operator|.
name|setApprovals
argument_list|(
name|ad
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|loadCurrentPatchSet ()
specifier|private
name|void
name|loadCurrentPatchSet
parameter_list|()
throws|throws
name|OrmException
throws|,
name|NoSuchEntityException
throws|,
name|PatchSetInfoNotAvailableException
throws|,
name|NoSuchChangeException
block|{
specifier|final
name|PatchSet
operator|.
name|Id
name|psId
init|=
name|detail
operator|.
name|getChange
argument_list|()
operator|.
name|currentPatchSetId
argument_list|()
decl_stmt|;
specifier|final
name|PatchSetDetailFactory
name|loader
init|=
name|patchSetDetail
operator|.
name|create
argument_list|(
literal|null
argument_list|,
name|psId
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|loader
operator|.
name|patchSet
operator|=
name|detail
operator|.
name|getCurrentPatchSet
argument_list|()
expr_stmt|;
name|loader
operator|.
name|control
operator|=
name|control
expr_stmt|;
name|detail
operator|.
name|setCurrentPatchSetDetail
argument_list|(
name|loader
operator|.
name|call
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|HashSet
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
name|changesToGet
init|=
operator|new
name|HashSet
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|HashMap
argument_list|<
name|Change
operator|.
name|Id
argument_list|,
name|PatchSet
operator|.
name|Id
argument_list|>
name|ancestorPatchIds
init|=
operator|new
name|HashMap
argument_list|<
name|Change
operator|.
name|Id
argument_list|,
name|PatchSet
operator|.
name|Id
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
name|ancestorOrder
init|=
operator|new
name|ArrayList
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|PatchSetAncestor
name|a
range|:
name|db
operator|.
name|patchSetAncestors
argument_list|()
operator|.
name|ancestorsOf
argument_list|(
name|psId
argument_list|)
control|)
block|{
for|for
control|(
name|PatchSet
name|p
range|:
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|byRevision
argument_list|(
name|a
operator|.
name|getAncestorRevision
argument_list|()
argument_list|)
control|)
block|{
specifier|final
name|Change
operator|.
name|Id
name|ck
init|=
name|p
operator|.
name|getId
argument_list|()
operator|.
name|getParentKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|changesToGet
operator|.
name|add
argument_list|(
name|ck
argument_list|)
condition|)
block|{
name|ancestorPatchIds
operator|.
name|put
argument_list|(
name|ck
argument_list|,
name|p
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|ancestorOrder
operator|.
name|add
argument_list|(
name|ck
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|final
name|RevId
name|cprev
init|=
name|loader
operator|.
name|patchSet
operator|.
name|getRevision
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
name|descendants
init|=
operator|new
name|HashSet
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|cprev
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|PatchSetAncestor
name|a
range|:
name|db
operator|.
name|patchSetAncestors
argument_list|()
operator|.
name|descendantsOf
argument_list|(
name|cprev
argument_list|)
control|)
block|{
specifier|final
name|Change
operator|.
name|Id
name|ck
init|=
name|a
operator|.
name|getPatchSet
argument_list|()
operator|.
name|getParentKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|descendants
operator|.
name|add
argument_list|(
name|ck
argument_list|)
condition|)
block|{
name|changesToGet
operator|.
name|add
argument_list|(
name|a
operator|.
name|getPatchSet
argument_list|()
operator|.
name|getParentKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|final
name|Map
argument_list|<
name|Change
operator|.
name|Id
argument_list|,
name|Change
argument_list|>
name|m
init|=
name|db
operator|.
name|changes
argument_list|()
operator|.
name|toMap
argument_list|(
name|db
operator|.
name|changes
argument_list|()
operator|.
name|get
argument_list|(
name|changesToGet
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|ArrayList
argument_list|<
name|ChangeInfo
argument_list|>
name|dependsOn
init|=
operator|new
name|ArrayList
argument_list|<
name|ChangeInfo
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Change
operator|.
name|Id
name|a
range|:
name|ancestorOrder
control|)
block|{
specifier|final
name|Change
name|ac
init|=
name|m
operator|.
name|get
argument_list|(
name|a
argument_list|)
decl_stmt|;
if|if
condition|(
name|ac
operator|!=
literal|null
condition|)
block|{
name|dependsOn
operator|.
name|add
argument_list|(
name|newChangeInfo
argument_list|(
name|ac
argument_list|,
name|ancestorPatchIds
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|ArrayList
argument_list|<
name|ChangeInfo
argument_list|>
name|neededBy
init|=
operator|new
name|ArrayList
argument_list|<
name|ChangeInfo
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Change
operator|.
name|Id
name|a
range|:
name|descendants
control|)
block|{
specifier|final
name|Change
name|ac
init|=
name|m
operator|.
name|get
argument_list|(
name|a
argument_list|)
decl_stmt|;
if|if
condition|(
name|ac
operator|!=
literal|null
condition|)
block|{
name|neededBy
operator|.
name|add
argument_list|(
name|newChangeInfo
argument_list|(
name|ac
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|neededBy
argument_list|,
operator|new
name|Comparator
argument_list|<
name|ChangeInfo
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
specifier|final
name|ChangeInfo
name|o1
parameter_list|,
specifier|final
name|ChangeInfo
name|o2
parameter_list|)
block|{
return|return
name|o1
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
operator|-
name|o2
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|detail
operator|.
name|setDependsOn
argument_list|(
name|dependsOn
argument_list|)
expr_stmt|;
name|detail
operator|.
name|setNeededBy
argument_list|(
name|neededBy
argument_list|)
expr_stmt|;
block|}
DECL|method|newChangeInfo (final Change ac, Map<Change.Id,PatchSet.Id> ancestorPatchIds)
specifier|private
name|ChangeInfo
name|newChangeInfo
parameter_list|(
specifier|final
name|Change
name|ac
parameter_list|,
name|Map
argument_list|<
name|Change
operator|.
name|Id
argument_list|,
name|PatchSet
operator|.
name|Id
argument_list|>
name|ancestorPatchIds
parameter_list|)
block|{
name|aic
operator|.
name|want
argument_list|(
name|ac
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|ChangeInfo
name|ci
decl_stmt|;
if|if
condition|(
name|ancestorPatchIds
operator|==
literal|null
condition|)
block|{
name|ci
operator|=
operator|new
name|ChangeInfo
argument_list|(
name|ac
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ci
operator|=
operator|new
name|ChangeInfo
argument_list|(
name|ac
argument_list|,
name|ancestorPatchIds
operator|.
name|get
argument_list|(
name|ac
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ci
operator|.
name|setStarred
argument_list|(
name|isStarred
argument_list|(
name|ac
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|ci
return|;
block|}
DECL|method|isStarred (final Change ac)
specifier|private
name|boolean
name|isStarred
parameter_list|(
specifier|final
name|Change
name|ac
parameter_list|)
block|{
return|return
name|control
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getStarredChanges
argument_list|()
operator|.
name|contains
argument_list|(
name|ac
operator|.
name|getId
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

