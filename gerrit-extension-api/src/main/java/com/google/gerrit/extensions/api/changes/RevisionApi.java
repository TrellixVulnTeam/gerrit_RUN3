begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2013 The Android Open Source Project
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
DECL|package|com.google.gerrit.extensions.api.changes
package|package
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
name|extensions
operator|.
name|client
operator|.
name|SubmitType
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
name|common
operator|.
name|ActionInfo
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
name|common
operator|.
name|CommentInfo
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
name|common
operator|.
name|FileInfo
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
name|common
operator|.
name|MergeableInfo
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
name|common
operator|.
name|TestSubmitRuleInput
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
name|BinaryResult
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
name|NotImplementedException
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

begin_interface
DECL|interface|RevisionApi
specifier|public
interface|interface
name|RevisionApi
block|{
DECL|method|delete ()
name|void
name|delete
parameter_list|()
throws|throws
name|RestApiException
function_decl|;
DECL|method|review (ReviewInput in)
name|void
name|review
parameter_list|(
name|ReviewInput
name|in
parameter_list|)
throws|throws
name|RestApiException
function_decl|;
DECL|method|submit ()
name|void
name|submit
parameter_list|()
throws|throws
name|RestApiException
function_decl|;
DECL|method|submit (SubmitInput in)
name|void
name|submit
parameter_list|(
name|SubmitInput
name|in
parameter_list|)
throws|throws
name|RestApiException
function_decl|;
DECL|method|publish ()
name|void
name|publish
parameter_list|()
throws|throws
name|RestApiException
function_decl|;
DECL|method|cherryPick (CherryPickInput in)
name|ChangeApi
name|cherryPick
parameter_list|(
name|CherryPickInput
name|in
parameter_list|)
throws|throws
name|RestApiException
function_decl|;
DECL|method|rebase ()
name|ChangeApi
name|rebase
parameter_list|()
throws|throws
name|RestApiException
function_decl|;
DECL|method|rebase (RebaseInput in)
name|ChangeApi
name|rebase
parameter_list|(
name|RebaseInput
name|in
parameter_list|)
throws|throws
name|RestApiException
function_decl|;
DECL|method|canRebase ()
name|boolean
name|canRebase
parameter_list|()
function_decl|;
DECL|method|setReviewed (String path, boolean reviewed)
name|void
name|setReviewed
parameter_list|(
name|String
name|path
parameter_list|,
name|boolean
name|reviewed
parameter_list|)
throws|throws
name|RestApiException
function_decl|;
DECL|method|reviewed ()
name|Set
argument_list|<
name|String
argument_list|>
name|reviewed
parameter_list|()
throws|throws
name|RestApiException
function_decl|;
DECL|method|files ()
name|Map
argument_list|<
name|String
argument_list|,
name|FileInfo
argument_list|>
name|files
parameter_list|()
throws|throws
name|RestApiException
function_decl|;
DECL|method|files (String base)
name|Map
argument_list|<
name|String
argument_list|,
name|FileInfo
argument_list|>
name|files
parameter_list|(
name|String
name|base
parameter_list|)
throws|throws
name|RestApiException
function_decl|;
DECL|method|file (String path)
name|FileApi
name|file
parameter_list|(
name|String
name|path
parameter_list|)
function_decl|;
DECL|method|mergeable ()
name|MergeableInfo
name|mergeable
parameter_list|()
throws|throws
name|RestApiException
function_decl|;
DECL|method|mergeableOtherBranches ()
name|MergeableInfo
name|mergeableOtherBranches
parameter_list|()
throws|throws
name|RestApiException
function_decl|;
DECL|method|comments ()
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|CommentInfo
argument_list|>
argument_list|>
name|comments
parameter_list|()
throws|throws
name|RestApiException
function_decl|;
DECL|method|drafts ()
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|CommentInfo
argument_list|>
argument_list|>
name|drafts
parameter_list|()
throws|throws
name|RestApiException
function_decl|;
DECL|method|commentsAsList ()
name|List
argument_list|<
name|CommentInfo
argument_list|>
name|commentsAsList
parameter_list|()
throws|throws
name|RestApiException
function_decl|;
DECL|method|draftsAsList ()
name|List
argument_list|<
name|CommentInfo
argument_list|>
name|draftsAsList
parameter_list|()
throws|throws
name|RestApiException
function_decl|;
DECL|method|createDraft (DraftInput in)
name|DraftApi
name|createDraft
parameter_list|(
name|DraftInput
name|in
parameter_list|)
throws|throws
name|RestApiException
function_decl|;
DECL|method|draft (String id)
name|DraftApi
name|draft
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|RestApiException
function_decl|;
DECL|method|comment (String id)
name|CommentApi
name|comment
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|RestApiException
function_decl|;
comment|/**    * Returns patch of revision.    */
DECL|method|patch ()
name|BinaryResult
name|patch
parameter_list|()
throws|throws
name|RestApiException
function_decl|;
DECL|method|actions ()
name|Map
argument_list|<
name|String
argument_list|,
name|ActionInfo
argument_list|>
name|actions
parameter_list|()
throws|throws
name|RestApiException
function_decl|;
DECL|method|submitType ()
name|SubmitType
name|submitType
parameter_list|()
throws|throws
name|RestApiException
function_decl|;
DECL|method|testSubmitType (TestSubmitRuleInput in)
name|SubmitType
name|testSubmitType
parameter_list|(
name|TestSubmitRuleInput
name|in
parameter_list|)
throws|throws
name|RestApiException
function_decl|;
comment|/**    * A default implementation which allows source compatibility    * when adding new methods to the interface.    **/
DECL|class|NotImplemented
specifier|public
class|class
name|NotImplemented
implements|implements
name|RevisionApi
block|{
annotation|@
name|Override
DECL|method|delete ()
specifier|public
name|void
name|delete
parameter_list|()
throws|throws
name|RestApiException
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|review (ReviewInput in)
specifier|public
name|void
name|review
parameter_list|(
name|ReviewInput
name|in
parameter_list|)
throws|throws
name|RestApiException
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|submit ()
specifier|public
name|void
name|submit
parameter_list|()
throws|throws
name|RestApiException
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|submit (SubmitInput in)
specifier|public
name|void
name|submit
parameter_list|(
name|SubmitInput
name|in
parameter_list|)
throws|throws
name|RestApiException
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|publish ()
specifier|public
name|void
name|publish
parameter_list|()
throws|throws
name|RestApiException
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|cherryPick (CherryPickInput in)
specifier|public
name|ChangeApi
name|cherryPick
parameter_list|(
name|CherryPickInput
name|in
parameter_list|)
throws|throws
name|RestApiException
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|rebase ()
specifier|public
name|ChangeApi
name|rebase
parameter_list|()
throws|throws
name|RestApiException
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|rebase (RebaseInput in)
specifier|public
name|ChangeApi
name|rebase
parameter_list|(
name|RebaseInput
name|in
parameter_list|)
throws|throws
name|RestApiException
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|canRebase ()
specifier|public
name|boolean
name|canRebase
parameter_list|()
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|setReviewed (String path, boolean reviewed)
specifier|public
name|void
name|setReviewed
parameter_list|(
name|String
name|path
parameter_list|,
name|boolean
name|reviewed
parameter_list|)
throws|throws
name|RestApiException
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|reviewed ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|reviewed
parameter_list|()
throws|throws
name|RestApiException
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|mergeable ()
specifier|public
name|MergeableInfo
name|mergeable
parameter_list|()
throws|throws
name|RestApiException
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|mergeableOtherBranches ()
specifier|public
name|MergeableInfo
name|mergeableOtherBranches
parameter_list|()
throws|throws
name|RestApiException
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|files (String base)
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|FileInfo
argument_list|>
name|files
parameter_list|(
name|String
name|base
parameter_list|)
throws|throws
name|RestApiException
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|files ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|FileInfo
argument_list|>
name|files
parameter_list|()
throws|throws
name|RestApiException
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|file (String path)
specifier|public
name|FileApi
name|file
parameter_list|(
name|String
name|path
parameter_list|)
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|comments ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|CommentInfo
argument_list|>
argument_list|>
name|comments
parameter_list|()
throws|throws
name|RestApiException
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|commentsAsList ()
specifier|public
name|List
argument_list|<
name|CommentInfo
argument_list|>
name|commentsAsList
parameter_list|()
throws|throws
name|RestApiException
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|draftsAsList ()
specifier|public
name|List
argument_list|<
name|CommentInfo
argument_list|>
name|draftsAsList
parameter_list|()
throws|throws
name|RestApiException
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|drafts ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|CommentInfo
argument_list|>
argument_list|>
name|drafts
parameter_list|()
throws|throws
name|RestApiException
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|createDraft (DraftInput in)
specifier|public
name|DraftApi
name|createDraft
parameter_list|(
name|DraftInput
name|in
parameter_list|)
throws|throws
name|RestApiException
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|draft (String id)
specifier|public
name|DraftApi
name|draft
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|RestApiException
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|comment (String id)
specifier|public
name|CommentApi
name|comment
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|RestApiException
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|patch ()
specifier|public
name|BinaryResult
name|patch
parameter_list|()
throws|throws
name|RestApiException
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|actions ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|ActionInfo
argument_list|>
name|actions
parameter_list|()
throws|throws
name|RestApiException
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|submitType ()
specifier|public
name|SubmitType
name|submitType
parameter_list|()
throws|throws
name|RestApiException
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|testSubmitType (TestSubmitRuleInput in)
specifier|public
name|SubmitType
name|testSubmitType
parameter_list|(
name|TestSubmitRuleInput
name|in
parameter_list|)
throws|throws
name|RestApiException
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
block|}
block|}
end_interface

end_unit

