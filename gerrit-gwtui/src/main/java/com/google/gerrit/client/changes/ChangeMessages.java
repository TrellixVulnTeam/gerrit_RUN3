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
name|Messages
import|;
end_import

begin_interface
DECL|interface|ChangeMessages
specifier|public
interface|interface
name|ChangeMessages
extends|extends
name|Messages
block|{
DECL|method|accountDashboardTitle (String fullName)
name|String
name|accountDashboardTitle
parameter_list|(
name|String
name|fullName
parameter_list|)
function_decl|;
DECL|method|revertChangeDefaultMessage (String commitMsg, String commitId)
name|String
name|revertChangeDefaultMessage
parameter_list|(
name|String
name|commitMsg
parameter_list|,
name|String
name|commitId
parameter_list|)
function_decl|;
DECL|method|cherryPickedChangeDefaultMessage (String commitMsg, String commitId)
name|String
name|cherryPickedChangeDefaultMessage
parameter_list|(
name|String
name|commitMsg
parameter_list|,
name|String
name|commitId
parameter_list|)
function_decl|;
DECL|method|changeScreenTitleId (String changeId)
name|String
name|changeScreenTitleId
parameter_list|(
name|String
name|changeId
parameter_list|)
function_decl|;
DECL|method|loadingPatchSet (int id)
name|String
name|loadingPatchSet
parameter_list|(
name|int
name|id
parameter_list|)
function_decl|;
DECL|method|patchTableSize_Modify (int insertions, int deletions)
name|String
name|patchTableSize_Modify
parameter_list|(
name|int
name|insertions
parameter_list|,
name|int
name|deletions
parameter_list|)
function_decl|;
DECL|method|patchTableSize_ModifyBinaryFiles (String bytesInserted, String bytesDeleted)
name|String
name|patchTableSize_ModifyBinaryFiles
parameter_list|(
name|String
name|bytesInserted
parameter_list|,
name|String
name|bytesDeleted
parameter_list|)
function_decl|;
DECL|method|patchTableSize_ModifyBinaryFilesWithPercentages ( String bytesInserted, String percentageInserted, String bytesDeleted, String percentageDeleted)
name|String
name|patchTableSize_ModifyBinaryFilesWithPercentages
parameter_list|(
name|String
name|bytesInserted
parameter_list|,
name|String
name|percentageInserted
parameter_list|,
name|String
name|bytesDeleted
parameter_list|,
name|String
name|percentageDeleted
parameter_list|)
function_decl|;
DECL|method|patchTableSize_LongModify (int insertions, int deletions)
name|String
name|patchTableSize_LongModify
parameter_list|(
name|int
name|insertions
parameter_list|,
name|int
name|deletions
parameter_list|)
function_decl|;
DECL|method|removeReviewer (String fullName)
name|String
name|removeReviewer
parameter_list|(
name|String
name|fullName
parameter_list|)
function_decl|;
DECL|method|removeVote (String label)
name|String
name|removeVote
parameter_list|(
name|String
name|label
parameter_list|)
function_decl|;
DECL|method|blockedOn (String labelName)
name|String
name|blockedOn
parameter_list|(
name|String
name|labelName
parameter_list|)
function_decl|;
DECL|method|needs (String labelName)
name|String
name|needs
parameter_list|(
name|String
name|labelName
parameter_list|)
function_decl|;
DECL|method|changeQueryWindowTitle (String query)
name|String
name|changeQueryWindowTitle
parameter_list|(
name|String
name|query
parameter_list|)
function_decl|;
DECL|method|changeQueryPageTitle (String query)
name|String
name|changeQueryPageTitle
parameter_list|(
name|String
name|query
parameter_list|)
function_decl|;
DECL|method|insertionsAndDeletions (int insertions, int deletions)
name|String
name|insertionsAndDeletions
parameter_list|(
name|int
name|insertions
parameter_list|,
name|int
name|deletions
parameter_list|)
function_decl|;
DECL|method|diffBaseParent (int parentNum)
name|String
name|diffBaseParent
parameter_list|(
name|int
name|parentNum
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

