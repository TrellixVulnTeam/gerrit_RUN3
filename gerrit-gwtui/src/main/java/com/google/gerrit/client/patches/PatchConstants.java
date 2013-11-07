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
DECL|package|com.google.gerrit.client.patches
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|patches
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
DECL|interface|PatchConstants
specifier|public
interface|interface
name|PatchConstants
extends|extends
name|Constants
block|{
DECL|method|draft ()
name|String
name|draft
parameter_list|()
function_decl|;
DECL|method|buttonReply ()
name|String
name|buttonReply
parameter_list|()
function_decl|;
DECL|method|buttonEdit ()
name|String
name|buttonEdit
parameter_list|()
function_decl|;
DECL|method|buttonSave ()
name|String
name|buttonSave
parameter_list|()
function_decl|;
DECL|method|buttonCancel ()
name|String
name|buttonCancel
parameter_list|()
function_decl|;
DECL|method|buttonDiscard ()
name|String
name|buttonDiscard
parameter_list|()
function_decl|;
DECL|method|noDifference ()
name|String
name|noDifference
parameter_list|()
function_decl|;
DECL|method|patchBase ()
name|String
name|patchBase
parameter_list|()
function_decl|;
DECL|method|patchBaseAutoMerge ()
name|String
name|patchBaseAutoMerge
parameter_list|()
function_decl|;
DECL|method|patchHeaderPatchSet ()
name|String
name|patchHeaderPatchSet
parameter_list|()
function_decl|;
DECL|method|patchHeaderOld ()
name|String
name|patchHeaderOld
parameter_list|()
function_decl|;
DECL|method|patchHeaderNew ()
name|String
name|patchHeaderNew
parameter_list|()
function_decl|;
DECL|method|patchSet ()
name|String
name|patchSet
parameter_list|()
function_decl|;
DECL|method|patchHistoryTitle ()
name|String
name|patchHistoryTitle
parameter_list|()
function_decl|;
DECL|method|disabledOnLargeFiles ()
name|String
name|disabledOnLargeFiles
parameter_list|()
function_decl|;
DECL|method|intralineFailure ()
name|String
name|intralineFailure
parameter_list|()
function_decl|;
DECL|method|intralineTimeout ()
name|String
name|intralineTimeout
parameter_list|()
function_decl|;
DECL|method|illegalNumberOfColumns ()
name|String
name|illegalNumberOfColumns
parameter_list|()
function_decl|;
DECL|method|upToChange ()
name|String
name|upToChange
parameter_list|()
function_decl|;
DECL|method|openReply ()
name|String
name|openReply
parameter_list|()
function_decl|;
DECL|method|linePrev ()
name|String
name|linePrev
parameter_list|()
function_decl|;
DECL|method|lineNext ()
name|String
name|lineNext
parameter_list|()
function_decl|;
DECL|method|chunkPrev ()
name|String
name|chunkPrev
parameter_list|()
function_decl|;
DECL|method|chunkNext ()
name|String
name|chunkNext
parameter_list|()
function_decl|;
DECL|method|chunkPrev2 ()
name|String
name|chunkPrev2
parameter_list|()
function_decl|;
DECL|method|chunkNext2 ()
name|String
name|chunkNext2
parameter_list|()
function_decl|;
DECL|method|commentPrev ()
name|String
name|commentPrev
parameter_list|()
function_decl|;
DECL|method|commentNext ()
name|String
name|commentNext
parameter_list|()
function_decl|;
DECL|method|fileList ()
name|String
name|fileList
parameter_list|()
function_decl|;
DECL|method|expandComment ()
name|String
name|expandComment
parameter_list|()
function_decl|;
DECL|method|expandAllCommentsOnCurrentLine ()
name|String
name|expandAllCommentsOnCurrentLine
parameter_list|()
function_decl|;
DECL|method|toggleReviewed ()
name|String
name|toggleReviewed
parameter_list|()
function_decl|;
DECL|method|markAsReviewedAndGoToNext ()
name|String
name|markAsReviewedAndGoToNext
parameter_list|()
function_decl|;
DECL|method|commentEditorSet ()
name|String
name|commentEditorSet
parameter_list|()
function_decl|;
DECL|method|commentInsert ()
name|String
name|commentInsert
parameter_list|()
function_decl|;
DECL|method|commentSaveDraft ()
name|String
name|commentSaveDraft
parameter_list|()
function_decl|;
DECL|method|commentCancelEdit ()
name|String
name|commentCancelEdit
parameter_list|()
function_decl|;
DECL|method|whitespaceIGNORE_NONE ()
name|String
name|whitespaceIGNORE_NONE
parameter_list|()
function_decl|;
DECL|method|whitespaceIGNORE_SPACE_AT_EOL ()
name|String
name|whitespaceIGNORE_SPACE_AT_EOL
parameter_list|()
function_decl|;
DECL|method|whitespaceIGNORE_SPACE_CHANGE ()
name|String
name|whitespaceIGNORE_SPACE_CHANGE
parameter_list|()
function_decl|;
DECL|method|whitespaceIGNORE_ALL_SPACE ()
name|String
name|whitespaceIGNORE_ALL_SPACE
parameter_list|()
function_decl|;
DECL|method|previousFileHelp ()
name|String
name|previousFileHelp
parameter_list|()
function_decl|;
DECL|method|nextFileHelp ()
name|String
name|nextFileHelp
parameter_list|()
function_decl|;
DECL|method|reviewedAnd ()
name|String
name|reviewedAnd
parameter_list|()
function_decl|;
DECL|method|next ()
name|String
name|next
parameter_list|()
function_decl|;
DECL|method|download ()
name|String
name|download
parameter_list|()
function_decl|;
DECL|method|addFileCommentToolTip ()
name|String
name|addFileCommentToolTip
parameter_list|()
function_decl|;
DECL|method|addFileCommentByDoubleClick ()
name|String
name|addFileCommentByDoubleClick
parameter_list|()
function_decl|;
DECL|method|buttonReplyDone ()
name|String
name|buttonReplyDone
parameter_list|()
function_decl|;
DECL|method|cannedReplyDone ()
name|String
name|cannedReplyDone
parameter_list|()
function_decl|;
DECL|method|fileTypeSymlink ()
name|String
name|fileTypeSymlink
parameter_list|()
function_decl|;
DECL|method|fileTypeGitlink ()
name|String
name|fileTypeGitlink
parameter_list|()
function_decl|;
DECL|method|patchSkipRegionStart ()
name|String
name|patchSkipRegionStart
parameter_list|()
function_decl|;
DECL|method|patchSkipRegionEnd ()
name|String
name|patchSkipRegionEnd
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

