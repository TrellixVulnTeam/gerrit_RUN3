begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2014 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.api.changes
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|api
operator|.
name|changes
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|api
operator|.
name|ApiUtil
operator|.
name|asRestApiException
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
name|DraftApi
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
name|DraftInput
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
name|server
operator|.
name|change
operator|.
name|DeleteDraftComment
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
name|DraftCommentResource
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
name|GetDraftComment
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
name|PutDraftComment
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

begin_class
DECL|class|DraftApiImpl
class|class
name|DraftApiImpl
implements|implements
name|DraftApi
block|{
DECL|interface|Factory
interface|interface
name|Factory
block|{
DECL|method|create (DraftCommentResource d)
name|DraftApiImpl
name|create
parameter_list|(
name|DraftCommentResource
name|d
parameter_list|)
function_decl|;
block|}
DECL|field|deleteDraft
specifier|private
specifier|final
name|DeleteDraftComment
name|deleteDraft
decl_stmt|;
DECL|field|getDraft
specifier|private
specifier|final
name|GetDraftComment
name|getDraft
decl_stmt|;
DECL|field|putDraft
specifier|private
specifier|final
name|PutDraftComment
name|putDraft
decl_stmt|;
DECL|field|draft
specifier|private
specifier|final
name|DraftCommentResource
name|draft
decl_stmt|;
annotation|@
name|Inject
DECL|method|DraftApiImpl ( DeleteDraftComment deleteDraft, GetDraftComment getDraft, PutDraftComment putDraft, @Assisted DraftCommentResource draft)
name|DraftApiImpl
parameter_list|(
name|DeleteDraftComment
name|deleteDraft
parameter_list|,
name|GetDraftComment
name|getDraft
parameter_list|,
name|PutDraftComment
name|putDraft
parameter_list|,
annotation|@
name|Assisted
name|DraftCommentResource
name|draft
parameter_list|)
block|{
name|this
operator|.
name|deleteDraft
operator|=
name|deleteDraft
expr_stmt|;
name|this
operator|.
name|getDraft
operator|=
name|getDraft
expr_stmt|;
name|this
operator|.
name|putDraft
operator|=
name|putDraft
expr_stmt|;
name|this
operator|.
name|draft
operator|=
name|draft
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get ()
specifier|public
name|CommentInfo
name|get
parameter_list|()
throws|throws
name|RestApiException
block|{
try|try
block|{
return|return
name|getDraft
operator|.
name|apply
argument_list|(
name|draft
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|asRestApiException
argument_list|(
literal|"Cannot retrieve draft"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|update (DraftInput in)
specifier|public
name|CommentInfo
name|update
parameter_list|(
name|DraftInput
name|in
parameter_list|)
throws|throws
name|RestApiException
block|{
try|try
block|{
return|return
name|putDraft
operator|.
name|apply
argument_list|(
name|draft
argument_list|,
name|in
argument_list|)
operator|.
name|value
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|asRestApiException
argument_list|(
literal|"Cannot update draft"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
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
try|try
block|{
name|deleteDraft
operator|.
name|apply
argument_list|(
name|draft
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|asRestApiException
argument_list|(
literal|"Cannot delete draft"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

