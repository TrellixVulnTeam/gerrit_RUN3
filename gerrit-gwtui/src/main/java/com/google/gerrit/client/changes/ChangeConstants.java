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
DECL|package|com.google.gerrit.client.changes
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|changes
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|i18n
operator|.
name|client
operator|.
name|Constants
import|;
end_import

begin_interface
DECL|interface|ChangeConstants
specifier|public
interface|interface
name|ChangeConstants
extends|extends
name|Constants
block|{
DECL|method|statusLongNew ()
name|String
name|statusLongNew
parameter_list|()
function_decl|;
DECL|method|statusLongSubmitted ()
name|String
name|statusLongSubmitted
parameter_list|()
function_decl|;
DECL|method|statusLongMerged ()
name|String
name|statusLongMerged
parameter_list|()
function_decl|;
DECL|method|statusLongAbandoned ()
name|String
name|statusLongAbandoned
parameter_list|()
function_decl|;
DECL|method|statusLongDraft ()
name|String
name|statusLongDraft
parameter_list|()
function_decl|;
DECL|method|readyToSubmit ()
name|String
name|readyToSubmit
parameter_list|()
function_decl|;
DECL|method|mergeConflict ()
name|String
name|mergeConflict
parameter_list|()
function_decl|;
DECL|method|myDashboardTitle ()
name|String
name|myDashboardTitle
parameter_list|()
function_decl|;
DECL|method|unknownDashboardTitle ()
name|String
name|unknownDashboardTitle
parameter_list|()
function_decl|;
DECL|method|incomingReviews ()
name|String
name|incomingReviews
parameter_list|()
function_decl|;
DECL|method|outgoingReviews ()
name|String
name|outgoingReviews
parameter_list|()
function_decl|;
DECL|method|recentlyClosed ()
name|String
name|recentlyClosed
parameter_list|()
function_decl|;
DECL|method|starredHeading ()
name|String
name|starredHeading
parameter_list|()
function_decl|;
DECL|method|watchedHeading ()
name|String
name|watchedHeading
parameter_list|()
function_decl|;
DECL|method|draftsHeading ()
name|String
name|draftsHeading
parameter_list|()
function_decl|;
DECL|method|allOpenChanges ()
name|String
name|allOpenChanges
parameter_list|()
function_decl|;
DECL|method|allAbandonedChanges ()
name|String
name|allAbandonedChanges
parameter_list|()
function_decl|;
DECL|method|allMergedChanges ()
name|String
name|allMergedChanges
parameter_list|()
function_decl|;
DECL|method|changeTableColumnSubject ()
name|String
name|changeTableColumnSubject
parameter_list|()
function_decl|;
DECL|method|changeTableColumnStatus ()
name|String
name|changeTableColumnStatus
parameter_list|()
function_decl|;
DECL|method|changeTableColumnOwner ()
name|String
name|changeTableColumnOwner
parameter_list|()
function_decl|;
DECL|method|changeTableColumnReviewers ()
name|String
name|changeTableColumnReviewers
parameter_list|()
function_decl|;
DECL|method|changeTableColumnProject ()
name|String
name|changeTableColumnProject
parameter_list|()
function_decl|;
DECL|method|changeTableColumnBranch ()
name|String
name|changeTableColumnBranch
parameter_list|()
function_decl|;
DECL|method|changeTableColumnLastUpdate ()
name|String
name|changeTableColumnLastUpdate
parameter_list|()
function_decl|;
DECL|method|changeTableNone ()
name|String
name|changeTableNone
parameter_list|()
function_decl|;
DECL|method|changeItemHelp ()
name|String
name|changeItemHelp
parameter_list|()
function_decl|;
DECL|method|changeTableStar ()
name|String
name|changeTableStar
parameter_list|()
function_decl|;
DECL|method|changeTablePagePrev ()
name|String
name|changeTablePagePrev
parameter_list|()
function_decl|;
DECL|method|changeTablePageNext ()
name|String
name|changeTablePageNext
parameter_list|()
function_decl|;
DECL|method|upToChangeList ()
name|String
name|upToChangeList
parameter_list|()
function_decl|;
DECL|method|expandCollapseDependencies ()
name|String
name|expandCollapseDependencies
parameter_list|()
function_decl|;
DECL|method|previousPatchSet ()
name|String
name|previousPatchSet
parameter_list|()
function_decl|;
DECL|method|nextPatchSet ()
name|String
name|nextPatchSet
parameter_list|()
function_decl|;
DECL|method|keyReload ()
name|String
name|keyReload
parameter_list|()
function_decl|;
DECL|method|keyPublishComments ()
name|String
name|keyPublishComments
parameter_list|()
function_decl|;
DECL|method|keyEditTopic ()
name|String
name|keyEditTopic
parameter_list|()
function_decl|;
DECL|method|keyEditMessage ()
name|String
name|keyEditMessage
parameter_list|()
function_decl|;
DECL|method|patchTableColumnName ()
name|String
name|patchTableColumnName
parameter_list|()
function_decl|;
DECL|method|patchTableColumnComments ()
name|String
name|patchTableColumnComments
parameter_list|()
function_decl|;
DECL|method|patchTableColumnSize ()
name|String
name|patchTableColumnSize
parameter_list|()
function_decl|;
DECL|method|patchTableColumnDiff ()
name|String
name|patchTableColumnDiff
parameter_list|()
function_decl|;
DECL|method|patchTableDiffSideBySide ()
name|String
name|patchTableDiffSideBySide
parameter_list|()
function_decl|;
DECL|method|patchTableDiffUnified ()
name|String
name|patchTableDiffUnified
parameter_list|()
function_decl|;
DECL|method|patchTableDownloadPreImage ()
name|String
name|patchTableDownloadPreImage
parameter_list|()
function_decl|;
DECL|method|patchTableDownloadPostImage ()
name|String
name|patchTableDownloadPostImage
parameter_list|()
function_decl|;
DECL|method|patchTableBinary ()
name|String
name|patchTableBinary
parameter_list|()
function_decl|;
DECL|method|commitMessage ()
name|String
name|commitMessage
parameter_list|()
function_decl|;
DECL|method|fileCommentHeader ()
name|String
name|fileCommentHeader
parameter_list|()
function_decl|;
DECL|method|patchTablePrev ()
name|String
name|patchTablePrev
parameter_list|()
function_decl|;
DECL|method|patchTableNext ()
name|String
name|patchTableNext
parameter_list|()
function_decl|;
DECL|method|patchTableOpenDiff ()
name|String
name|patchTableOpenDiff
parameter_list|()
function_decl|;
DECL|method|patchTableOpenUnifiedDiff ()
name|String
name|patchTableOpenUnifiedDiff
parameter_list|()
function_decl|;
DECL|method|upToChangeIconLink ()
name|String
name|upToChangeIconLink
parameter_list|()
function_decl|;
DECL|method|prevPatchLinkIcon ()
name|String
name|prevPatchLinkIcon
parameter_list|()
function_decl|;
DECL|method|nextPatchLinkIcon ()
name|String
name|nextPatchLinkIcon
parameter_list|()
function_decl|;
DECL|method|changeScreenIncludedIn ()
name|String
name|changeScreenIncludedIn
parameter_list|()
function_decl|;
DECL|method|changeScreenDependencies ()
name|String
name|changeScreenDependencies
parameter_list|()
function_decl|;
DECL|method|changeScreenDependsOn ()
name|String
name|changeScreenDependsOn
parameter_list|()
function_decl|;
DECL|method|changeScreenNeededBy ()
name|String
name|changeScreenNeededBy
parameter_list|()
function_decl|;
DECL|method|changeScreenComments ()
name|String
name|changeScreenComments
parameter_list|()
function_decl|;
DECL|method|changeScreenAddComment ()
name|String
name|changeScreenAddComment
parameter_list|()
function_decl|;
DECL|method|approvalTableReviewer ()
name|String
name|approvalTableReviewer
parameter_list|()
function_decl|;
DECL|method|approvalTableAddReviewer ()
name|String
name|approvalTableAddReviewer
parameter_list|()
function_decl|;
DECL|method|approvalTableRemoveNotPermitted ()
name|String
name|approvalTableRemoveNotPermitted
parameter_list|()
function_decl|;
DECL|method|approvalTableCouldNotRemove ()
name|String
name|approvalTableCouldNotRemove
parameter_list|()
function_decl|;
DECL|method|approvalTableAddReviewerHint ()
name|String
name|approvalTableAddReviewerHint
parameter_list|()
function_decl|;
DECL|method|approvalTableAddManyReviewersConfirmationDialogTitle ()
name|String
name|approvalTableAddManyReviewersConfirmationDialogTitle
parameter_list|()
function_decl|;
DECL|method|changeInfoBlockOwner ()
name|String
name|changeInfoBlockOwner
parameter_list|()
function_decl|;
DECL|method|changeInfoBlockProject ()
name|String
name|changeInfoBlockProject
parameter_list|()
function_decl|;
DECL|method|changeInfoBlockBranch ()
name|String
name|changeInfoBlockBranch
parameter_list|()
function_decl|;
DECL|method|changeInfoBlockTopic ()
name|String
name|changeInfoBlockTopic
parameter_list|()
function_decl|;
DECL|method|changeInfoBlockTopicAlterTopicToolTip ()
name|String
name|changeInfoBlockTopicAlterTopicToolTip
parameter_list|()
function_decl|;
DECL|method|changeInfoBlockUploaded ()
name|String
name|changeInfoBlockUploaded
parameter_list|()
function_decl|;
DECL|method|changeInfoBlockUpdated ()
name|String
name|changeInfoBlockUpdated
parameter_list|()
function_decl|;
DECL|method|changeInfoBlockStatus ()
name|String
name|changeInfoBlockStatus
parameter_list|()
function_decl|;
DECL|method|changeInfoBlockSubmitType ()
name|String
name|changeInfoBlockSubmitType
parameter_list|()
function_decl|;
DECL|method|changePermalink ()
name|String
name|changePermalink
parameter_list|()
function_decl|;
DECL|method|changeInfoBlockCanMerge ()
name|String
name|changeInfoBlockCanMerge
parameter_list|()
function_decl|;
DECL|method|changeInfoBlockCanMergeYes ()
name|String
name|changeInfoBlockCanMergeYes
parameter_list|()
function_decl|;
DECL|method|changeInfoBlockCanMergeNo ()
name|String
name|changeInfoBlockCanMergeNo
parameter_list|()
function_decl|;
DECL|method|buttonAlterTopic ()
name|String
name|buttonAlterTopic
parameter_list|()
function_decl|;
DECL|method|buttonAlterTopicBegin ()
name|String
name|buttonAlterTopicBegin
parameter_list|()
function_decl|;
DECL|method|buttonAlterTopicSend ()
name|String
name|buttonAlterTopicSend
parameter_list|()
function_decl|;
DECL|method|buttonAlterTopicCancel ()
name|String
name|buttonAlterTopicCancel
parameter_list|()
function_decl|;
DECL|method|headingAlterTopicMessage ()
name|String
name|headingAlterTopicMessage
parameter_list|()
function_decl|;
DECL|method|alterTopicTitle ()
name|String
name|alterTopicTitle
parameter_list|()
function_decl|;
DECL|method|alterTopicLabel ()
name|String
name|alterTopicLabel
parameter_list|()
function_decl|;
DECL|method|includedInTableBranch ()
name|String
name|includedInTableBranch
parameter_list|()
function_decl|;
DECL|method|includedInTableTag ()
name|String
name|includedInTableTag
parameter_list|()
function_decl|;
DECL|method|messageNoAuthor ()
name|String
name|messageNoAuthor
parameter_list|()
function_decl|;
DECL|method|messageExpandMostRecent ()
name|String
name|messageExpandMostRecent
parameter_list|()
function_decl|;
DECL|method|messageExpandRecent ()
name|String
name|messageExpandRecent
parameter_list|()
function_decl|;
DECL|method|messageExpandAll ()
name|String
name|messageExpandAll
parameter_list|()
function_decl|;
DECL|method|messageCollapseAll ()
name|String
name|messageCollapseAll
parameter_list|()
function_decl|;
DECL|method|messageNeedsRebaseOrHasDependency ()
name|String
name|messageNeedsRebaseOrHasDependency
parameter_list|()
function_decl|;
DECL|method|patchSetInfoAuthor ()
name|String
name|patchSetInfoAuthor
parameter_list|()
function_decl|;
DECL|method|patchSetInfoCommitter ()
name|String
name|patchSetInfoCommitter
parameter_list|()
function_decl|;
DECL|method|patchSetInfoDownload ()
name|String
name|patchSetInfoDownload
parameter_list|()
function_decl|;
DECL|method|patchSetInfoParents ()
name|String
name|patchSetInfoParents
parameter_list|()
function_decl|;
DECL|method|patchSetWithDraftCommentsToolTip ()
name|String
name|patchSetWithDraftCommentsToolTip
parameter_list|()
function_decl|;
DECL|method|initialCommit ()
name|String
name|initialCommit
parameter_list|()
function_decl|;
DECL|method|buttonRebaseChange ()
name|String
name|buttonRebaseChange
parameter_list|()
function_decl|;
DECL|method|buttonRevertChangeBegin ()
name|String
name|buttonRevertChangeBegin
parameter_list|()
function_decl|;
DECL|method|buttonRevertChangeSend ()
name|String
name|buttonRevertChangeSend
parameter_list|()
function_decl|;
DECL|method|headingRevertMessage ()
name|String
name|headingRevertMessage
parameter_list|()
function_decl|;
DECL|method|revertChangeTitle ()
name|String
name|revertChangeTitle
parameter_list|()
function_decl|;
DECL|method|headingEditCommitMessage ()
name|String
name|headingEditCommitMessage
parameter_list|()
function_decl|;
DECL|method|editCommitMessageToolTip ()
name|String
name|editCommitMessageToolTip
parameter_list|()
function_decl|;
DECL|method|titleEditCommitMessage ()
name|String
name|titleEditCommitMessage
parameter_list|()
function_decl|;
DECL|method|buttonCherryPickChangeBegin ()
name|String
name|buttonCherryPickChangeBegin
parameter_list|()
function_decl|;
DECL|method|buttonCherryPickChangeSend ()
name|String
name|buttonCherryPickChangeSend
parameter_list|()
function_decl|;
DECL|method|headingCherryPickBranch ()
name|String
name|headingCherryPickBranch
parameter_list|()
function_decl|;
DECL|method|cherryPickCommitMessage ()
name|String
name|cherryPickCommitMessage
parameter_list|()
function_decl|;
DECL|method|cherryPickTitle ()
name|String
name|cherryPickTitle
parameter_list|()
function_decl|;
DECL|method|buttonAbandonChangeBegin ()
name|String
name|buttonAbandonChangeBegin
parameter_list|()
function_decl|;
DECL|method|buttonAbandonChangeSend ()
name|String
name|buttonAbandonChangeSend
parameter_list|()
function_decl|;
DECL|method|headingAbandonMessage ()
name|String
name|headingAbandonMessage
parameter_list|()
function_decl|;
DECL|method|abandonChangeTitle ()
name|String
name|abandonChangeTitle
parameter_list|()
function_decl|;
DECL|method|referenceVersion ()
name|String
name|referenceVersion
parameter_list|()
function_decl|;
DECL|method|baseDiffItem ()
name|String
name|baseDiffItem
parameter_list|()
function_decl|;
DECL|method|autoMerge ()
name|String
name|autoMerge
parameter_list|()
function_decl|;
DECL|method|buttonReview ()
name|String
name|buttonReview
parameter_list|()
function_decl|;
DECL|method|buttonPublishCommentsSend ()
name|String
name|buttonPublishCommentsSend
parameter_list|()
function_decl|;
DECL|method|buttonPublishSubmitSend ()
name|String
name|buttonPublishSubmitSend
parameter_list|()
function_decl|;
DECL|method|buttonPublishCommentsCancel ()
name|String
name|buttonPublishCommentsCancel
parameter_list|()
function_decl|;
DECL|method|headingCoverMessage ()
name|String
name|headingCoverMessage
parameter_list|()
function_decl|;
DECL|method|headingPatchComments ()
name|String
name|headingPatchComments
parameter_list|()
function_decl|;
DECL|method|buttonRestoreChangeBegin ()
name|String
name|buttonRestoreChangeBegin
parameter_list|()
function_decl|;
DECL|method|restoreChangeTitle ()
name|String
name|restoreChangeTitle
parameter_list|()
function_decl|;
DECL|method|headingRestoreMessage ()
name|String
name|headingRestoreMessage
parameter_list|()
function_decl|;
DECL|method|buttonRestoreChangeSend ()
name|String
name|buttonRestoreChangeSend
parameter_list|()
function_decl|;
DECL|method|buttonPublishPatchSet ()
name|String
name|buttonPublishPatchSet
parameter_list|()
function_decl|;
DECL|method|buttonDeleteDraftChange ()
name|String
name|buttonDeleteDraftChange
parameter_list|()
function_decl|;
DECL|method|buttonDeleteDraftPatchSet ()
name|String
name|buttonDeleteDraftPatchSet
parameter_list|()
function_decl|;
DECL|method|pagedChangeListPrev ()
name|String
name|pagedChangeListPrev
parameter_list|()
function_decl|;
DECL|method|pagedChangeListNext ()
name|String
name|pagedChangeListNext
parameter_list|()
function_decl|;
DECL|method|draftPatchSetLabel ()
name|String
name|draftPatchSetLabel
parameter_list|()
function_decl|;
DECL|method|reviewed ()
name|String
name|reviewed
parameter_list|()
function_decl|;
DECL|method|submitFailed ()
name|String
name|submitFailed
parameter_list|()
function_decl|;
DECL|method|buttonClose ()
name|String
name|buttonClose
parameter_list|()
function_decl|;
DECL|method|diffAllSideBySide ()
name|String
name|diffAllSideBySide
parameter_list|()
function_decl|;
DECL|method|diffAllUnified ()
name|String
name|diffAllUnified
parameter_list|()
function_decl|;
DECL|method|inTheFuture ()
name|String
name|inTheFuture
parameter_list|()
function_decl|;
DECL|method|month ()
name|String
name|month
parameter_list|()
function_decl|;
DECL|method|months ()
name|String
name|months
parameter_list|()
function_decl|;
DECL|method|year ()
name|String
name|year
parameter_list|()
function_decl|;
DECL|method|years ()
name|String
name|years
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

