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
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
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
name|extensions
operator|.
name|api
operator|.
name|changes
operator|.
name|ChangeApi
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
name|Changes
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
name|CherryPickInput
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
name|CommentApi
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
name|api
operator|.
name|changes
operator|.
name|FileApi
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
name|RebaseInput
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
name|ReviewInput
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
name|RevisionApi
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
name|SubmitInput
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
name|restapi
operator|.
name|IdString
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
name|extensions
operator|.
name|restapi
operator|.
name|RestModifyView
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
name|CherryPick
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
name|Comments
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
name|CreateDraftComment
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
name|change
operator|.
name|DraftComments
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
name|FileResource
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
name|Files
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
name|GetRevisionActions
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
name|ListComments
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
name|ListDraftComments
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
name|Mergeable
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
name|PostReview
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
name|PublishDraftPatchSet
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
name|Rebase
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
name|Reviewed
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
name|RevisionResource
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
name|Submit
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
name|RebaseChange
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
DECL|class|RevisionApiImpl
class|class
name|RevisionApiImpl
implements|implements
name|RevisionApi
block|{
DECL|interface|Factory
interface|interface
name|Factory
block|{
DECL|method|create (RevisionResource r)
name|RevisionApiImpl
name|create
parameter_list|(
name|RevisionResource
name|r
parameter_list|)
function_decl|;
block|}
DECL|field|changes
specifier|private
specifier|final
name|Changes
name|changes
decl_stmt|;
DECL|field|cherryPick
specifier|private
specifier|final
name|CherryPick
name|cherryPick
decl_stmt|;
DECL|field|deleteDraft
specifier|private
specifier|final
name|DeleteDraftPatchSet
name|deleteDraft
decl_stmt|;
DECL|field|rebase
specifier|private
specifier|final
name|Rebase
name|rebase
decl_stmt|;
DECL|field|rebaseChange
specifier|private
specifier|final
name|RebaseChange
name|rebaseChange
decl_stmt|;
DECL|field|submit
specifier|private
specifier|final
name|Submit
name|submit
decl_stmt|;
DECL|field|publish
specifier|private
specifier|final
name|PublishDraftPatchSet
name|publish
decl_stmt|;
DECL|field|putReviewed
specifier|private
specifier|final
name|Reviewed
operator|.
name|PutReviewed
name|putReviewed
decl_stmt|;
DECL|field|deleteReviewed
specifier|private
specifier|final
name|Reviewed
operator|.
name|DeleteReviewed
name|deleteReviewed
decl_stmt|;
DECL|field|revision
specifier|private
specifier|final
name|RevisionResource
name|revision
decl_stmt|;
DECL|field|files
specifier|private
specifier|final
name|Provider
argument_list|<
name|Files
argument_list|>
name|files
decl_stmt|;
DECL|field|listFiles
specifier|private
specifier|final
name|Provider
argument_list|<
name|Files
operator|.
name|ListFiles
argument_list|>
name|listFiles
decl_stmt|;
DECL|field|review
specifier|private
specifier|final
name|Provider
argument_list|<
name|PostReview
argument_list|>
name|review
decl_stmt|;
DECL|field|mergeable
specifier|private
specifier|final
name|Provider
argument_list|<
name|Mergeable
argument_list|>
name|mergeable
decl_stmt|;
DECL|field|fileApi
specifier|private
specifier|final
name|FileApiImpl
operator|.
name|Factory
name|fileApi
decl_stmt|;
DECL|field|listComments
specifier|private
specifier|final
name|ListComments
name|listComments
decl_stmt|;
DECL|field|listDrafts
specifier|private
specifier|final
name|ListDraftComments
name|listDrafts
decl_stmt|;
DECL|field|createDraft
specifier|private
specifier|final
name|CreateDraftComment
name|createDraft
decl_stmt|;
DECL|field|drafts
specifier|private
specifier|final
name|DraftComments
name|drafts
decl_stmt|;
DECL|field|draftFactory
specifier|private
specifier|final
name|DraftApiImpl
operator|.
name|Factory
name|draftFactory
decl_stmt|;
DECL|field|comments
specifier|private
specifier|final
name|Comments
name|comments
decl_stmt|;
DECL|field|commentFactory
specifier|private
specifier|final
name|CommentApiImpl
operator|.
name|Factory
name|commentFactory
decl_stmt|;
DECL|field|revisionActions
specifier|private
specifier|final
name|GetRevisionActions
name|revisionActions
decl_stmt|;
annotation|@
name|Inject
DECL|method|RevisionApiImpl (Changes changes, CherryPick cherryPick, DeleteDraftPatchSet deleteDraft, Rebase rebase, RebaseChange rebaseChange, Submit submit, PublishDraftPatchSet publish, Reviewed.PutReviewed putReviewed, Reviewed.DeleteReviewed deleteReviewed, Provider<Files> files, Provider<Files.ListFiles> listFiles, Provider<PostReview> review, Provider<Mergeable> mergeable, FileApiImpl.Factory fileApi, ListComments listComments, ListDraftComments listDrafts, CreateDraftComment createDraft, DraftComments drafts, DraftApiImpl.Factory draftFactory, Comments comments, CommentApiImpl.Factory commentFactory, GetRevisionActions revisionActions, @Assisted RevisionResource r)
name|RevisionApiImpl
parameter_list|(
name|Changes
name|changes
parameter_list|,
name|CherryPick
name|cherryPick
parameter_list|,
name|DeleteDraftPatchSet
name|deleteDraft
parameter_list|,
name|Rebase
name|rebase
parameter_list|,
name|RebaseChange
name|rebaseChange
parameter_list|,
name|Submit
name|submit
parameter_list|,
name|PublishDraftPatchSet
name|publish
parameter_list|,
name|Reviewed
operator|.
name|PutReviewed
name|putReviewed
parameter_list|,
name|Reviewed
operator|.
name|DeleteReviewed
name|deleteReviewed
parameter_list|,
name|Provider
argument_list|<
name|Files
argument_list|>
name|files
parameter_list|,
name|Provider
argument_list|<
name|Files
operator|.
name|ListFiles
argument_list|>
name|listFiles
parameter_list|,
name|Provider
argument_list|<
name|PostReview
argument_list|>
name|review
parameter_list|,
name|Provider
argument_list|<
name|Mergeable
argument_list|>
name|mergeable
parameter_list|,
name|FileApiImpl
operator|.
name|Factory
name|fileApi
parameter_list|,
name|ListComments
name|listComments
parameter_list|,
name|ListDraftComments
name|listDrafts
parameter_list|,
name|CreateDraftComment
name|createDraft
parameter_list|,
name|DraftComments
name|drafts
parameter_list|,
name|DraftApiImpl
operator|.
name|Factory
name|draftFactory
parameter_list|,
name|Comments
name|comments
parameter_list|,
name|CommentApiImpl
operator|.
name|Factory
name|commentFactory
parameter_list|,
name|GetRevisionActions
name|revisionActions
parameter_list|,
annotation|@
name|Assisted
name|RevisionResource
name|r
parameter_list|)
block|{
name|this
operator|.
name|changes
operator|=
name|changes
expr_stmt|;
name|this
operator|.
name|cherryPick
operator|=
name|cherryPick
expr_stmt|;
name|this
operator|.
name|deleteDraft
operator|=
name|deleteDraft
expr_stmt|;
name|this
operator|.
name|rebase
operator|=
name|rebase
expr_stmt|;
name|this
operator|.
name|rebaseChange
operator|=
name|rebaseChange
expr_stmt|;
name|this
operator|.
name|review
operator|=
name|review
expr_stmt|;
name|this
operator|.
name|submit
operator|=
name|submit
expr_stmt|;
name|this
operator|.
name|publish
operator|=
name|publish
expr_stmt|;
name|this
operator|.
name|files
operator|=
name|files
expr_stmt|;
name|this
operator|.
name|putReviewed
operator|=
name|putReviewed
expr_stmt|;
name|this
operator|.
name|deleteReviewed
operator|=
name|deleteReviewed
expr_stmt|;
name|this
operator|.
name|listFiles
operator|=
name|listFiles
expr_stmt|;
name|this
operator|.
name|mergeable
operator|=
name|mergeable
expr_stmt|;
name|this
operator|.
name|fileApi
operator|=
name|fileApi
expr_stmt|;
name|this
operator|.
name|listComments
operator|=
name|listComments
expr_stmt|;
name|this
operator|.
name|listDrafts
operator|=
name|listDrafts
expr_stmt|;
name|this
operator|.
name|createDraft
operator|=
name|createDraft
expr_stmt|;
name|this
operator|.
name|drafts
operator|=
name|drafts
expr_stmt|;
name|this
operator|.
name|draftFactory
operator|=
name|draftFactory
expr_stmt|;
name|this
operator|.
name|comments
operator|=
name|comments
expr_stmt|;
name|this
operator|.
name|commentFactory
operator|=
name|commentFactory
expr_stmt|;
name|this
operator|.
name|revisionActions
operator|=
name|revisionActions
expr_stmt|;
name|this
operator|.
name|revision
operator|=
name|r
expr_stmt|;
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
try|try
block|{
name|review
operator|.
name|get
argument_list|()
operator|.
name|apply
argument_list|(
name|revision
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
decl||
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RestApiException
argument_list|(
literal|"Cannot post review"
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
name|SubmitInput
name|in
init|=
operator|new
name|SubmitInput
argument_list|()
decl_stmt|;
name|in
operator|.
name|waitForMerge
operator|=
literal|true
expr_stmt|;
name|submit
argument_list|(
name|in
argument_list|)
expr_stmt|;
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
try|try
block|{
name|submit
operator|.
name|apply
argument_list|(
name|revision
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
decl||
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RestApiException
argument_list|(
literal|"Cannot submit change"
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
try|try
block|{
name|publish
operator|.
name|apply
argument_list|(
name|revision
argument_list|,
operator|new
name|PublishDraftPatchSet
operator|.
name|Input
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
decl||
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RestApiException
argument_list|(
literal|"Cannot publish draft patch set"
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
name|revision
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
decl||
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RestApiException
argument_list|(
literal|"Cannot delete draft ps"
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
name|RebaseInput
name|in
init|=
operator|new
name|RebaseInput
argument_list|()
decl_stmt|;
return|return
name|rebase
argument_list|(
name|in
argument_list|)
return|;
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
try|try
block|{
return|return
name|changes
operator|.
name|id
argument_list|(
name|rebase
operator|.
name|apply
argument_list|(
name|revision
argument_list|,
name|in
argument_list|)
operator|.
name|_number
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|OrmException
decl||
name|EmailException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RestApiException
argument_list|(
literal|"Cannot rebase ps"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|canRebase ()
specifier|public
name|boolean
name|canRebase
parameter_list|()
block|{
return|return
name|rebaseChange
operator|.
name|canRebase
argument_list|(
name|revision
argument_list|)
return|;
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
try|try
block|{
return|return
name|changes
operator|.
name|id
argument_list|(
name|cherryPick
operator|.
name|apply
argument_list|(
name|revision
argument_list|,
name|in
argument_list|)
operator|.
name|_number
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|OrmException
decl||
name|EmailException
decl||
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RestApiException
argument_list|(
literal|"Cannot cherry pick"
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
try|try
block|{
name|RestModifyView
argument_list|<
name|FileResource
argument_list|,
name|Reviewed
operator|.
name|Input
argument_list|>
name|view
decl_stmt|;
if|if
condition|(
name|reviewed
condition|)
block|{
name|view
operator|=
name|putReviewed
expr_stmt|;
block|}
else|else
block|{
name|view
operator|=
name|deleteReviewed
expr_stmt|;
block|}
name|view
operator|.
name|apply
argument_list|(
name|files
operator|.
name|get
argument_list|()
operator|.
name|parse
argument_list|(
name|revision
argument_list|,
name|IdString
operator|.
name|fromDecoded
argument_list|(
name|path
argument_list|)
argument_list|)
argument_list|,
operator|new
name|Reviewed
operator|.
name|Input
argument_list|()
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
operator|new
name|RestApiException
argument_list|(
literal|"Cannot update reviewed flag"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
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
try|try
block|{
return|return
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
operator|(
name|Iterable
argument_list|<
name|String
argument_list|>
operator|)
name|listFiles
operator|.
name|get
argument_list|()
operator|.
name|setReviewed
argument_list|(
literal|true
argument_list|)
operator|.
name|apply
argument_list|(
name|revision
argument_list|)
operator|.
name|value
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|OrmException
decl||
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RestApiException
argument_list|(
literal|"Cannot list reviewed files"
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
try|try
block|{
return|return
name|mergeable
operator|.
name|get
argument_list|()
operator|.
name|apply
argument_list|(
name|revision
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|OrmException
decl||
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RestApiException
argument_list|(
literal|"Cannot check mergeability"
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
try|try
block|{
name|Mergeable
name|m
init|=
name|mergeable
operator|.
name|get
argument_list|()
decl_stmt|;
name|m
operator|.
name|setOtherBranches
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|m
operator|.
name|apply
argument_list|(
name|revision
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|OrmException
decl||
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RestApiException
argument_list|(
literal|"Cannot check mergeability"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
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
try|try
block|{
return|return
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|FileInfo
argument_list|>
operator|)
name|listFiles
operator|.
name|get
argument_list|()
operator|.
name|apply
argument_list|(
name|revision
argument_list|)
operator|.
name|value
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|OrmException
decl||
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RestApiException
argument_list|(
literal|"Cannot retrieve files"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
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
try|try
block|{
return|return
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|FileInfo
argument_list|>
operator|)
name|listFiles
operator|.
name|get
argument_list|()
operator|.
name|setBase
argument_list|(
name|base
argument_list|)
operator|.
name|apply
argument_list|(
name|revision
argument_list|)
operator|.
name|value
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|OrmException
decl||
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RestApiException
argument_list|(
literal|"Cannot retrieve files"
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
return|return
name|fileApi
operator|.
name|create
argument_list|(
name|files
operator|.
name|get
argument_list|()
operator|.
name|parse
argument_list|(
name|revision
argument_list|,
name|IdString
operator|.
name|fromDecoded
argument_list|(
name|path
argument_list|)
argument_list|)
argument_list|)
return|;
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
try|try
block|{
return|return
name|listComments
operator|.
name|apply
argument_list|(
name|revision
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RestApiException
argument_list|(
literal|"Cannot retrieve comments"
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
try|try
block|{
return|return
name|listDrafts
operator|.
name|apply
argument_list|(
name|revision
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RestApiException
argument_list|(
literal|"Cannot retrieve drafts"
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
try|try
block|{
return|return
name|draftFactory
operator|.
name|create
argument_list|(
name|drafts
operator|.
name|parse
argument_list|(
name|revision
argument_list|,
name|IdString
operator|.
name|fromDecoded
argument_list|(
name|id
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RestApiException
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
try|try
block|{
return|return
name|draft
argument_list|(
name|createDraft
operator|.
name|apply
argument_list|(
name|revision
argument_list|,
name|in
argument_list|)
operator|.
name|value
argument_list|()
operator|.
name|id
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|OrmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RestApiException
argument_list|(
literal|"Cannot create draft"
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
try|try
block|{
return|return
name|commentFactory
operator|.
name|create
argument_list|(
name|comments
operator|.
name|parse
argument_list|(
name|revision
argument_list|,
name|IdString
operator|.
name|fromDecoded
argument_list|(
name|id
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RestApiException
argument_list|(
literal|"Cannot retrieve comment"
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
return|return
name|revisionActions
operator|.
name|apply
argument_list|(
name|revision
argument_list|)
operator|.
name|value
argument_list|()
return|;
block|}
block|}
end_class

end_unit

