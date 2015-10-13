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
name|AbandonInput
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
name|AddReviewerInput
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
name|FixInput
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
name|HashtagsInput
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
name|RestoreInput
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
name|RevertInput
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
name|client
operator|.
name|ListChangesOption
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
name|EditInfo
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
name|SuggestedReviewerInfo
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
name|Response
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
name|change
operator|.
name|Abandon
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
name|ChangeEdits
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
name|ChangeJson
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
name|ChangeResource
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
name|Check
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
name|GetHashtags
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
name|GetTopic
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
name|ListChangeComments
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
name|ListChangeDrafts
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
name|PostHashtags
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
name|PostReviewers
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
name|PutTopic
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
name|Restore
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
name|Revert
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
name|Revisions
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
name|SubmittedTogether
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
name|SuggestReviewers
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
name|UpdateException
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
name|InvalidChangeOperationException
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
name|EnumSet
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
DECL|class|ChangeApiImpl
class|class
name|ChangeApiImpl
implements|implements
name|ChangeApi
block|{
DECL|interface|Factory
interface|interface
name|Factory
block|{
DECL|method|create (ChangeResource change)
name|ChangeApiImpl
name|create
parameter_list|(
name|ChangeResource
name|change
parameter_list|)
function_decl|;
block|}
DECL|field|user
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|user
decl_stmt|;
DECL|field|changeApi
specifier|private
specifier|final
name|Changes
name|changeApi
decl_stmt|;
DECL|field|revisions
specifier|private
specifier|final
name|Revisions
name|revisions
decl_stmt|;
DECL|field|revisionApi
specifier|private
specifier|final
name|RevisionApiImpl
operator|.
name|Factory
name|revisionApi
decl_stmt|;
DECL|field|suggestReviewers
specifier|private
specifier|final
name|Provider
argument_list|<
name|SuggestReviewers
argument_list|>
name|suggestReviewers
decl_stmt|;
DECL|field|change
specifier|private
specifier|final
name|ChangeResource
name|change
decl_stmt|;
DECL|field|abandon
specifier|private
specifier|final
name|Abandon
name|abandon
decl_stmt|;
DECL|field|revert
specifier|private
specifier|final
name|Revert
name|revert
decl_stmt|;
DECL|field|restore
specifier|private
specifier|final
name|Restore
name|restore
decl_stmt|;
DECL|field|submittedTogether
specifier|private
specifier|final
name|SubmittedTogether
name|submittedTogether
decl_stmt|;
DECL|field|getTopic
specifier|private
specifier|final
name|GetTopic
name|getTopic
decl_stmt|;
DECL|field|putTopic
specifier|private
specifier|final
name|PutTopic
name|putTopic
decl_stmt|;
DECL|field|postReviewers
specifier|private
specifier|final
name|PostReviewers
name|postReviewers
decl_stmt|;
DECL|field|changeJson
specifier|private
specifier|final
name|ChangeJson
operator|.
name|Factory
name|changeJson
decl_stmt|;
DECL|field|postHashtags
specifier|private
specifier|final
name|PostHashtags
name|postHashtags
decl_stmt|;
DECL|field|getHashtags
specifier|private
specifier|final
name|GetHashtags
name|getHashtags
decl_stmt|;
DECL|field|listComments
specifier|private
specifier|final
name|ListChangeComments
name|listComments
decl_stmt|;
DECL|field|listDrafts
specifier|private
specifier|final
name|ListChangeDrafts
name|listDrafts
decl_stmt|;
DECL|field|check
specifier|private
specifier|final
name|Check
name|check
decl_stmt|;
DECL|field|editDetail
specifier|private
specifier|final
name|ChangeEdits
operator|.
name|Detail
name|editDetail
decl_stmt|;
annotation|@
name|Inject
DECL|method|ChangeApiImpl (Provider<CurrentUser> user, Changes changeApi, Revisions revisions, RevisionApiImpl.Factory revisionApi, Provider<SuggestReviewers> suggestReviewers, Abandon abandon, Revert revert, Restore restore, SubmittedTogether submittedTogether, GetTopic getTopic, PutTopic putTopic, PostReviewers postReviewers, ChangeJson.Factory changeJson, PostHashtags postHashtags, GetHashtags getHashtags, ListChangeComments listComments, ListChangeDrafts listDrafts, Check check, ChangeEdits.Detail editDetail, @Assisted ChangeResource change)
name|ChangeApiImpl
parameter_list|(
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|user
parameter_list|,
name|Changes
name|changeApi
parameter_list|,
name|Revisions
name|revisions
parameter_list|,
name|RevisionApiImpl
operator|.
name|Factory
name|revisionApi
parameter_list|,
name|Provider
argument_list|<
name|SuggestReviewers
argument_list|>
name|suggestReviewers
parameter_list|,
name|Abandon
name|abandon
parameter_list|,
name|Revert
name|revert
parameter_list|,
name|Restore
name|restore
parameter_list|,
name|SubmittedTogether
name|submittedTogether
parameter_list|,
name|GetTopic
name|getTopic
parameter_list|,
name|PutTopic
name|putTopic
parameter_list|,
name|PostReviewers
name|postReviewers
parameter_list|,
name|ChangeJson
operator|.
name|Factory
name|changeJson
parameter_list|,
name|PostHashtags
name|postHashtags
parameter_list|,
name|GetHashtags
name|getHashtags
parameter_list|,
name|ListChangeComments
name|listComments
parameter_list|,
name|ListChangeDrafts
name|listDrafts
parameter_list|,
name|Check
name|check
parameter_list|,
name|ChangeEdits
operator|.
name|Detail
name|editDetail
parameter_list|,
annotation|@
name|Assisted
name|ChangeResource
name|change
parameter_list|)
block|{
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|changeApi
operator|=
name|changeApi
expr_stmt|;
name|this
operator|.
name|revert
operator|=
name|revert
expr_stmt|;
name|this
operator|.
name|revisions
operator|=
name|revisions
expr_stmt|;
name|this
operator|.
name|revisionApi
operator|=
name|revisionApi
expr_stmt|;
name|this
operator|.
name|suggestReviewers
operator|=
name|suggestReviewers
expr_stmt|;
name|this
operator|.
name|abandon
operator|=
name|abandon
expr_stmt|;
name|this
operator|.
name|restore
operator|=
name|restore
expr_stmt|;
name|this
operator|.
name|submittedTogether
operator|=
name|submittedTogether
expr_stmt|;
name|this
operator|.
name|getTopic
operator|=
name|getTopic
expr_stmt|;
name|this
operator|.
name|putTopic
operator|=
name|putTopic
expr_stmt|;
name|this
operator|.
name|postReviewers
operator|=
name|postReviewers
expr_stmt|;
name|this
operator|.
name|changeJson
operator|=
name|changeJson
expr_stmt|;
name|this
operator|.
name|postHashtags
operator|=
name|postHashtags
expr_stmt|;
name|this
operator|.
name|getHashtags
operator|=
name|getHashtags
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
name|check
operator|=
name|check
expr_stmt|;
name|this
operator|.
name|editDetail
operator|=
name|editDetail
expr_stmt|;
name|this
operator|.
name|change
operator|=
name|change
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|id ()
specifier|public
name|String
name|id
parameter_list|()
block|{
return|return
name|Integer
operator|.
name|toString
argument_list|(
name|change
operator|.
name|getChange
argument_list|()
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|current ()
specifier|public
name|RevisionApi
name|current
parameter_list|()
throws|throws
name|RestApiException
block|{
return|return
name|revision
argument_list|(
literal|"current"
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|revision (int id)
specifier|public
name|RevisionApi
name|revision
parameter_list|(
name|int
name|id
parameter_list|)
throws|throws
name|RestApiException
block|{
return|return
name|revision
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|id
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|revision (String id)
specifier|public
name|RevisionApi
name|revision
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
name|revisionApi
operator|.
name|create
argument_list|(
name|revisions
operator|.
name|parse
argument_list|(
name|change
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
decl||
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RestApiException
argument_list|(
literal|"Cannot parse revision"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|abandon ()
specifier|public
name|void
name|abandon
parameter_list|()
throws|throws
name|RestApiException
block|{
name|abandon
argument_list|(
operator|new
name|AbandonInput
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|abandon (AbandonInput in)
specifier|public
name|void
name|abandon
parameter_list|(
name|AbandonInput
name|in
parameter_list|)
throws|throws
name|RestApiException
block|{
try|try
block|{
name|abandon
operator|.
name|apply
argument_list|(
name|change
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
decl||
name|UpdateException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RestApiException
argument_list|(
literal|"Cannot abandon change"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|restore ()
specifier|public
name|void
name|restore
parameter_list|()
throws|throws
name|RestApiException
block|{
name|restore
argument_list|(
operator|new
name|RestoreInput
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|restore (RestoreInput in)
specifier|public
name|void
name|restore
parameter_list|(
name|RestoreInput
name|in
parameter_list|)
throws|throws
name|RestApiException
block|{
try|try
block|{
name|restore
operator|.
name|apply
argument_list|(
name|change
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
decl||
name|UpdateException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RestApiException
argument_list|(
literal|"Cannot restore change"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|revert ()
specifier|public
name|ChangeApi
name|revert
parameter_list|()
throws|throws
name|RestApiException
block|{
return|return
name|revert
argument_list|(
operator|new
name|RevertInput
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|revert (RevertInput in)
specifier|public
name|ChangeApi
name|revert
parameter_list|(
name|RevertInput
name|in
parameter_list|)
throws|throws
name|RestApiException
block|{
try|try
block|{
return|return
name|changeApi
operator|.
name|id
argument_list|(
name|revert
operator|.
name|apply
argument_list|(
name|change
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
decl||
name|UpdateException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RestApiException
argument_list|(
literal|"Cannot revert change"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|submittedTogether ()
specifier|public
name|List
argument_list|<
name|ChangeInfo
argument_list|>
name|submittedTogether
parameter_list|()
throws|throws
name|RestApiException
block|{
try|try
block|{
return|return
name|submittedTogether
operator|.
name|apply
argument_list|(
name|change
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
operator|new
name|RestApiException
argument_list|(
literal|"Cannot query submittedTogether"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|topic ()
specifier|public
name|String
name|topic
parameter_list|()
throws|throws
name|RestApiException
block|{
return|return
name|getTopic
operator|.
name|apply
argument_list|(
name|change
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|topic (String topic)
specifier|public
name|void
name|topic
parameter_list|(
name|String
name|topic
parameter_list|)
throws|throws
name|RestApiException
block|{
name|PutTopic
operator|.
name|Input
name|in
init|=
operator|new
name|PutTopic
operator|.
name|Input
argument_list|()
decl_stmt|;
name|in
operator|.
name|topic
operator|=
name|topic
expr_stmt|;
try|try
block|{
name|putTopic
operator|.
name|apply
argument_list|(
name|change
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
decl||
name|UpdateException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RestApiException
argument_list|(
literal|"Cannot set topic"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|addReviewer (String reviewer)
specifier|public
name|void
name|addReviewer
parameter_list|(
name|String
name|reviewer
parameter_list|)
throws|throws
name|RestApiException
block|{
name|AddReviewerInput
name|in
init|=
operator|new
name|AddReviewerInput
argument_list|()
decl_stmt|;
name|in
operator|.
name|reviewer
operator|=
name|reviewer
expr_stmt|;
name|addReviewer
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addReviewer (AddReviewerInput in)
specifier|public
name|void
name|addReviewer
parameter_list|(
name|AddReviewerInput
name|in
parameter_list|)
throws|throws
name|RestApiException
block|{
try|try
block|{
name|postReviewers
operator|.
name|apply
argument_list|(
name|change
argument_list|,
name|in
argument_list|)
expr_stmt|;
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
literal|"Cannot add change reviewer"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|suggestReviewers ()
specifier|public
name|SuggestedReviewersRequest
name|suggestReviewers
parameter_list|()
throws|throws
name|RestApiException
block|{
return|return
operator|new
name|SuggestedReviewersRequest
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|SuggestedReviewerInfo
argument_list|>
name|get
parameter_list|()
throws|throws
name|RestApiException
block|{
return|return
name|ChangeApiImpl
operator|.
name|this
operator|.
name|suggestReviewers
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|suggestReviewers (String query)
specifier|public
name|SuggestedReviewersRequest
name|suggestReviewers
parameter_list|(
name|String
name|query
parameter_list|)
throws|throws
name|RestApiException
block|{
return|return
name|suggestReviewers
argument_list|()
operator|.
name|withQuery
argument_list|(
name|query
argument_list|)
return|;
block|}
DECL|method|suggestReviewers (SuggestedReviewersRequest r)
specifier|private
name|List
argument_list|<
name|SuggestedReviewerInfo
argument_list|>
name|suggestReviewers
parameter_list|(
name|SuggestedReviewersRequest
name|r
parameter_list|)
throws|throws
name|RestApiException
block|{
try|try
block|{
name|SuggestReviewers
name|mySuggestReviewers
init|=
name|suggestReviewers
operator|.
name|get
argument_list|()
decl_stmt|;
name|mySuggestReviewers
operator|.
name|setQuery
argument_list|(
name|r
operator|.
name|getQuery
argument_list|()
argument_list|)
expr_stmt|;
name|mySuggestReviewers
operator|.
name|setLimit
argument_list|(
name|r
operator|.
name|getLimit
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|mySuggestReviewers
operator|.
name|apply
argument_list|(
name|change
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
literal|"Cannot retrieve suggested reviewers"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|get (EnumSet<ListChangesOption> s)
specifier|public
name|ChangeInfo
name|get
parameter_list|(
name|EnumSet
argument_list|<
name|ListChangesOption
argument_list|>
name|s
parameter_list|)
throws|throws
name|RestApiException
block|{
try|try
block|{
name|CurrentUser
name|u
init|=
name|user
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|u
operator|.
name|isIdentifiedUser
argument_list|()
condition|)
block|{
operator|(
operator|(
name|IdentifiedUser
operator|)
name|u
operator|)
operator|.
name|clearStarredChanges
argument_list|()
expr_stmt|;
block|}
return|return
name|changeJson
operator|.
name|create
argument_list|(
name|s
argument_list|)
operator|.
name|format
argument_list|(
name|change
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
literal|"Cannot retrieve change"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|get ()
specifier|public
name|ChangeInfo
name|get
parameter_list|()
throws|throws
name|RestApiException
block|{
return|return
name|get
argument_list|(
name|EnumSet
operator|.
name|complementOf
argument_list|(
name|EnumSet
operator|.
name|of
argument_list|(
name|ListChangesOption
operator|.
name|CHECK
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getEdit ()
specifier|public
name|EditInfo
name|getEdit
parameter_list|()
throws|throws
name|RestApiException
block|{
try|try
block|{
name|Response
argument_list|<
name|EditInfo
argument_list|>
name|edit
init|=
name|editDetail
operator|.
name|apply
argument_list|(
name|change
argument_list|)
decl_stmt|;
return|return
name|edit
operator|.
name|isNone
argument_list|()
condition|?
literal|null
else|:
name|edit
operator|.
name|value
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|OrmException
decl||
name|InvalidChangeOperationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RestApiException
argument_list|(
literal|"Cannot retrieve change edit"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|info ()
specifier|public
name|ChangeInfo
name|info
parameter_list|()
throws|throws
name|RestApiException
block|{
return|return
name|get
argument_list|(
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|ListChangesOption
operator|.
name|class
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setHashtags (HashtagsInput input)
specifier|public
name|void
name|setHashtags
parameter_list|(
name|HashtagsInput
name|input
parameter_list|)
throws|throws
name|RestApiException
block|{
try|try
block|{
name|postHashtags
operator|.
name|apply
argument_list|(
name|change
argument_list|,
name|input
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RestApiException
decl||
name|UpdateException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RestApiException
argument_list|(
literal|"Cannot post hashtags"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getHashtags ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getHashtags
parameter_list|()
throws|throws
name|RestApiException
block|{
try|try
block|{
return|return
name|getHashtags
operator|.
name|apply
argument_list|(
name|change
argument_list|)
operator|.
name|value
argument_list|()
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
literal|"Cannot get hashtags"
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
name|change
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
literal|"Cannot get comments"
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
name|change
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
literal|"Cannot get drafts"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|check ()
specifier|public
name|ChangeInfo
name|check
parameter_list|()
throws|throws
name|RestApiException
block|{
try|try
block|{
return|return
name|check
operator|.
name|apply
argument_list|(
name|change
argument_list|)
operator|.
name|value
argument_list|()
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
literal|"Cannot check change"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|check (FixInput fix)
specifier|public
name|ChangeInfo
name|check
parameter_list|(
name|FixInput
name|fix
parameter_list|)
throws|throws
name|RestApiException
block|{
try|try
block|{
return|return
name|check
operator|.
name|apply
argument_list|(
name|change
argument_list|,
name|fix
argument_list|)
operator|.
name|value
argument_list|()
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
literal|"Cannot check change"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

