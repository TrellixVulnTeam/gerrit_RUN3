begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright 2008 Google Inc.
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
DECL|method|changesStartedBy (String fullName)
name|String
name|changesStartedBy
parameter_list|(
name|String
name|fullName
parameter_list|)
function_decl|;
DECL|method|changesReviewableBy (String fullName)
name|String
name|changesReviewableBy
parameter_list|(
name|String
name|fullName
parameter_list|)
function_decl|;
DECL|method|changeScreenTitleId (int id)
name|String
name|changeScreenTitleId
parameter_list|(
name|int
name|id
parameter_list|)
function_decl|;
DECL|method|patchSetHeader (int id)
name|String
name|patchSetHeader
parameter_list|(
name|int
name|id
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
DECL|method|patchSetAction (String action, int id)
name|String
name|patchSetAction
parameter_list|(
name|String
name|action
parameter_list|,
name|int
name|id
parameter_list|)
function_decl|;
DECL|method|repoDownload (String project, int change, int ps)
name|String
name|repoDownload
parameter_list|(
name|String
name|project
parameter_list|,
name|int
name|change
parameter_list|,
name|int
name|ps
parameter_list|)
function_decl|;
DECL|method|patchTableComments (@luralCount int count)
name|String
name|patchTableComments
parameter_list|(
annotation|@
name|PluralCount
name|int
name|count
parameter_list|)
function_decl|;
DECL|method|patchTableDrafts (@luralCount int count)
name|String
name|patchTableDrafts
parameter_list|(
annotation|@
name|PluralCount
name|int
name|count
parameter_list|)
function_decl|;
DECL|method|messageWrittenOn (String date)
name|String
name|messageWrittenOn
parameter_list|(
name|String
name|date
parameter_list|)
function_decl|;
DECL|method|renamedFrom (String sourcePath)
name|String
name|renamedFrom
parameter_list|(
name|String
name|sourcePath
parameter_list|)
function_decl|;
DECL|method|copiedFrom (String sourcePath)
name|String
name|copiedFrom
parameter_list|(
name|String
name|sourcePath
parameter_list|)
function_decl|;
DECL|method|otherFrom (String sourcePath)
name|String
name|otherFrom
parameter_list|(
name|String
name|sourcePath
parameter_list|)
function_decl|;
DECL|method|needApproval (String categoryName)
name|String
name|needApproval
parameter_list|(
name|String
name|categoryName
parameter_list|)
function_decl|;
DECL|method|publishComments (int change, int ps)
name|String
name|publishComments
parameter_list|(
name|int
name|change
parameter_list|,
name|int
name|ps
parameter_list|)
function_decl|;
DECL|method|lineHeader (int line)
name|String
name|lineHeader
parameter_list|(
name|int
name|line
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

