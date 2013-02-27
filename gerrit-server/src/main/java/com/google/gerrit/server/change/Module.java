begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2012 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|change
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
name|change
operator|.
name|ChangeResource
operator|.
name|CHANGE_KIND
import|;
end_import

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
name|change
operator|.
name|DraftResource
operator|.
name|DRAFT_KIND
import|;
end_import

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
name|change
operator|.
name|PatchResource
operator|.
name|PATCH_KIND
import|;
end_import

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
name|change
operator|.
name|ReviewerResource
operator|.
name|REVIEWER_KIND
import|;
end_import

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
name|change
operator|.
name|RevisionResource
operator|.
name|REVISION_KIND
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
name|registration
operator|.
name|DynamicMap
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
name|RestApiModule
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
name|AccountInfo
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
operator|.
name|DeleteReviewed
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
operator|.
name|PutReviewed
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
name|FactoryModule
import|;
end_import

begin_class
DECL|class|Module
specifier|public
class|class
name|Module
extends|extends
name|RestApiModule
block|{
annotation|@
name|Override
DECL|method|configure ()
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|bind
argument_list|(
name|Revisions
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|Reviewers
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|Drafts
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|Patches
operator|.
name|class
argument_list|)
expr_stmt|;
name|DynamicMap
operator|.
name|mapOf
argument_list|(
name|binder
argument_list|()
argument_list|,
name|CHANGE_KIND
argument_list|)
expr_stmt|;
name|DynamicMap
operator|.
name|mapOf
argument_list|(
name|binder
argument_list|()
argument_list|,
name|DRAFT_KIND
argument_list|)
expr_stmt|;
name|DynamicMap
operator|.
name|mapOf
argument_list|(
name|binder
argument_list|()
argument_list|,
name|PATCH_KIND
argument_list|)
expr_stmt|;
name|DynamicMap
operator|.
name|mapOf
argument_list|(
name|binder
argument_list|()
argument_list|,
name|REVIEWER_KIND
argument_list|)
expr_stmt|;
name|DynamicMap
operator|.
name|mapOf
argument_list|(
name|binder
argument_list|()
argument_list|,
name|REVISION_KIND
argument_list|)
expr_stmt|;
name|get
argument_list|(
name|CHANGE_KIND
argument_list|)
operator|.
name|to
argument_list|(
name|GetChange
operator|.
name|class
argument_list|)
expr_stmt|;
name|get
argument_list|(
name|CHANGE_KIND
argument_list|,
literal|"detail"
argument_list|)
operator|.
name|to
argument_list|(
name|GetDetail
operator|.
name|class
argument_list|)
expr_stmt|;
name|get
argument_list|(
name|CHANGE_KIND
argument_list|,
literal|"topic"
argument_list|)
operator|.
name|to
argument_list|(
name|GetTopic
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|CHANGE_KIND
argument_list|,
literal|"topic"
argument_list|)
operator|.
name|to
argument_list|(
name|PutTopic
operator|.
name|class
argument_list|)
expr_stmt|;
name|delete
argument_list|(
name|CHANGE_KIND
argument_list|,
literal|"topic"
argument_list|)
operator|.
name|to
argument_list|(
name|PutTopic
operator|.
name|class
argument_list|)
expr_stmt|;
name|post
argument_list|(
name|CHANGE_KIND
argument_list|,
literal|"abandon"
argument_list|)
operator|.
name|to
argument_list|(
name|Abandon
operator|.
name|class
argument_list|)
expr_stmt|;
name|post
argument_list|(
name|CHANGE_KIND
argument_list|,
literal|"restore"
argument_list|)
operator|.
name|to
argument_list|(
name|Restore
operator|.
name|class
argument_list|)
expr_stmt|;
name|post
argument_list|(
name|CHANGE_KIND
argument_list|,
literal|"revert"
argument_list|)
operator|.
name|to
argument_list|(
name|Revert
operator|.
name|class
argument_list|)
expr_stmt|;
name|post
argument_list|(
name|CHANGE_KIND
argument_list|,
literal|"submit"
argument_list|)
operator|.
name|to
argument_list|(
name|Submit
operator|.
name|CurrentRevision
operator|.
name|class
argument_list|)
expr_stmt|;
name|post
argument_list|(
name|CHANGE_KIND
argument_list|,
literal|"reviewers"
argument_list|)
operator|.
name|to
argument_list|(
name|PostReviewers
operator|.
name|class
argument_list|)
expr_stmt|;
name|child
argument_list|(
name|CHANGE_KIND
argument_list|,
literal|"reviewers"
argument_list|)
operator|.
name|to
argument_list|(
name|Reviewers
operator|.
name|class
argument_list|)
expr_stmt|;
name|get
argument_list|(
name|REVIEWER_KIND
argument_list|)
operator|.
name|to
argument_list|(
name|GetReviewer
operator|.
name|class
argument_list|)
expr_stmt|;
name|delete
argument_list|(
name|REVIEWER_KIND
argument_list|)
operator|.
name|to
argument_list|(
name|DeleteReviewer
operator|.
name|class
argument_list|)
expr_stmt|;
name|child
argument_list|(
name|CHANGE_KIND
argument_list|,
literal|"revisions"
argument_list|)
operator|.
name|to
argument_list|(
name|Revisions
operator|.
name|class
argument_list|)
expr_stmt|;
name|get
argument_list|(
name|REVISION_KIND
argument_list|,
literal|"review"
argument_list|)
operator|.
name|to
argument_list|(
name|GetReview
operator|.
name|class
argument_list|)
expr_stmt|;
name|post
argument_list|(
name|REVISION_KIND
argument_list|,
literal|"review"
argument_list|)
operator|.
name|to
argument_list|(
name|PostReview
operator|.
name|class
argument_list|)
expr_stmt|;
name|post
argument_list|(
name|REVISION_KIND
argument_list|,
literal|"submit"
argument_list|)
operator|.
name|to
argument_list|(
name|Submit
operator|.
name|class
argument_list|)
expr_stmt|;
name|get
argument_list|(
name|REVISION_KIND
argument_list|,
literal|"submit_type"
argument_list|)
operator|.
name|to
argument_list|(
name|TestSubmitType
operator|.
name|Get
operator|.
name|class
argument_list|)
expr_stmt|;
name|post
argument_list|(
name|REVISION_KIND
argument_list|,
literal|"test.submit_rule"
argument_list|)
operator|.
name|to
argument_list|(
name|TestSubmitRule
operator|.
name|class
argument_list|)
expr_stmt|;
name|post
argument_list|(
name|REVISION_KIND
argument_list|,
literal|"test.submit_type"
argument_list|)
operator|.
name|to
argument_list|(
name|TestSubmitType
operator|.
name|class
argument_list|)
expr_stmt|;
name|child
argument_list|(
name|REVISION_KIND
argument_list|,
literal|"drafts"
argument_list|)
operator|.
name|to
argument_list|(
name|Drafts
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|REVISION_KIND
argument_list|,
literal|"drafts"
argument_list|)
operator|.
name|to
argument_list|(
name|CreateDraft
operator|.
name|class
argument_list|)
expr_stmt|;
name|get
argument_list|(
name|DRAFT_KIND
argument_list|)
operator|.
name|to
argument_list|(
name|GetDraft
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|DRAFT_KIND
argument_list|)
operator|.
name|to
argument_list|(
name|PutDraft
operator|.
name|class
argument_list|)
expr_stmt|;
name|delete
argument_list|(
name|DRAFT_KIND
argument_list|)
operator|.
name|to
argument_list|(
name|DeleteDraft
operator|.
name|class
argument_list|)
expr_stmt|;
name|child
argument_list|(
name|REVISION_KIND
argument_list|,
literal|"files"
argument_list|)
operator|.
name|to
argument_list|(
name|Patches
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|PATCH_KIND
argument_list|,
literal|"reviewed"
argument_list|)
operator|.
name|to
argument_list|(
name|PutReviewed
operator|.
name|class
argument_list|)
expr_stmt|;
name|delete
argument_list|(
name|PATCH_KIND
argument_list|,
literal|"reviewed"
argument_list|)
operator|.
name|to
argument_list|(
name|DeleteReviewed
operator|.
name|class
argument_list|)
expr_stmt|;
name|install
argument_list|(
operator|new
name|FactoryModule
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|factory
argument_list|(
name|ReviewerResource
operator|.
name|Factory
operator|.
name|class
argument_list|)
expr_stmt|;
name|factory
argument_list|(
name|AccountInfo
operator|.
name|Loader
operator|.
name|Factory
operator|.
name|class
argument_list|)
expr_stmt|;
name|factory
argument_list|(
name|EmailReviewComments
operator|.
name|Factory
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

