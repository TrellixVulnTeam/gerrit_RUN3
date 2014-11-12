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
DECL|package|com.google.gerrit.acceptance.api.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|api
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
name|common
operator|.
name|truth
operator|.
name|Truth
operator|.
name|assertThat
import|;
end_import

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
name|common
operator|.
name|collect
operator|.
name|Iterables
import|;
end_import

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
name|Sets
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
name|acceptance
operator|.
name|AbstractDaemonTest
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
name|acceptance
operator|.
name|NoHttpd
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
name|acceptance
operator|.
name|PushOneCommit
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
name|common
operator|.
name|ApprovalInfo
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
name|ChangeStatus
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
name|LabelInfo
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
name|RevisionInfo
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
name|ResourceConflictException
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
name|client
operator|.
name|Account
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
name|Constants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|Set
import|;
end_import

begin_class
annotation|@
name|NoHttpd
DECL|class|ChangeIT
specifier|public
class|class
name|ChangeIT
extends|extends
name|AbstractDaemonTest
block|{
annotation|@
name|Test
DECL|method|get ()
specifier|public
name|void
name|get
parameter_list|()
throws|throws
name|Exception
block|{
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|createChange
argument_list|()
decl_stmt|;
name|String
name|triplet
init|=
literal|"p~master~"
operator|+
name|r
operator|.
name|getChangeId
argument_list|()
decl_stmt|;
name|ChangeInfo
name|c
init|=
name|info
argument_list|(
name|triplet
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|id
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|triplet
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|project
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|branch
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"master"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|status
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|ChangeStatus
operator|.
name|NEW
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|subject
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"test commit"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|mergeable
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|changeId
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|created
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|c
operator|.
name|updated
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|_number
argument_list|)
operator|.
name|is
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|abandon ()
specifier|public
name|void
name|abandon
parameter_list|()
throws|throws
name|Exception
block|{
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|createChange
argument_list|()
decl_stmt|;
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
operator|.
name|abandon
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|restore ()
specifier|public
name|void
name|restore
parameter_list|()
throws|throws
name|Exception
block|{
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|createChange
argument_list|()
decl_stmt|;
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
operator|.
name|abandon
argument_list|()
expr_stmt|;
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
operator|.
name|restore
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|revert ()
specifier|public
name|void
name|revert
parameter_list|()
throws|throws
name|Exception
block|{
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|createChange
argument_list|()
decl_stmt|;
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
operator|.
name|revision
argument_list|(
name|r
operator|.
name|getCommit
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|review
argument_list|(
name|ReviewInput
operator|.
name|approve
argument_list|()
argument_list|)
expr_stmt|;
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
operator|.
name|revision
argument_list|(
name|r
operator|.
name|getCommit
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|submit
argument_list|()
expr_stmt|;
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
operator|.
name|revert
argument_list|()
expr_stmt|;
block|}
comment|// Change is already up to date
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ResourceConflictException
operator|.
name|class
argument_list|)
DECL|method|rebase ()
specifier|public
name|void
name|rebase
parameter_list|()
throws|throws
name|Exception
block|{
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|createChange
argument_list|()
decl_stmt|;
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
operator|.
name|revision
argument_list|(
name|r
operator|.
name|getCommit
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|rebase
argument_list|()
expr_stmt|;
block|}
DECL|method|getReviewers (String changeId)
specifier|private
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|getReviewers
parameter_list|(
name|String
name|changeId
parameter_list|)
throws|throws
name|Exception
block|{
name|ChangeInfo
name|ci
init|=
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|changeId
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|result
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|LabelInfo
name|li
range|:
name|ci
operator|.
name|labels
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|ApprovalInfo
name|ai
range|:
name|li
operator|.
name|all
control|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|Account
operator|.
name|Id
argument_list|(
name|ai
operator|.
name|_accountId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Test
DECL|method|addReviewer ()
specifier|public
name|void
name|addReviewer
parameter_list|()
throws|throws
name|Exception
block|{
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|createChange
argument_list|()
decl_stmt|;
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
name|user
operator|.
name|email
expr_stmt|;
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
operator|.
name|addReviewer
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|getReviewers
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
argument_list|)
operator|.
name|containsExactlyElementsIn
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|user
operator|.
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|addReviewerToClosedChange ()
specifier|public
name|void
name|addReviewerToClosedChange
parameter_list|()
throws|throws
name|Exception
block|{
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|createChange
argument_list|()
decl_stmt|;
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
operator|.
name|revision
argument_list|(
name|r
operator|.
name|getCommit
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|review
argument_list|(
name|ReviewInput
operator|.
name|approve
argument_list|()
argument_list|)
expr_stmt|;
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
operator|.
name|revision
argument_list|(
name|r
operator|.
name|getCommit
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|submit
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|getReviewers
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
argument_list|)
operator|.
name|containsExactlyElementsIn
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|admin
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
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
name|user
operator|.
name|email
expr_stmt|;
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
operator|.
name|addReviewer
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|getReviewers
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
argument_list|)
operator|.
name|containsExactlyElementsIn
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|admin
operator|.
name|getId
argument_list|()
argument_list|,
name|user
operator|.
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|createEmptyChange ()
specifier|public
name|void
name|createEmptyChange
parameter_list|()
throws|throws
name|Exception
block|{
name|ChangeInfo
name|in
init|=
operator|new
name|ChangeInfo
argument_list|()
decl_stmt|;
name|in
operator|.
name|branch
operator|=
name|Constants
operator|.
name|MASTER
expr_stmt|;
name|in
operator|.
name|subject
operator|=
literal|"Create a change from the API"
expr_stmt|;
name|in
operator|.
name|project
operator|=
name|project
operator|.
name|get
argument_list|()
expr_stmt|;
name|ChangeInfo
name|info
init|=
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|create
argument_list|(
name|in
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|project
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|in
operator|.
name|project
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|branch
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|in
operator|.
name|branch
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|subject
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|in
operator|.
name|subject
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|queryChangesNoQuery ()
specifier|public
name|void
name|queryChangesNoQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|PushOneCommit
operator|.
name|Result
name|r1
init|=
name|createChange
argument_list|()
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r2
init|=
name|createChange
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ChangeInfo
argument_list|>
name|results
init|=
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|query
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|results
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|changeId
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|r2
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|results
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|changeId
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|r1
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|queryChangesNoResults ()
specifier|public
name|void
name|queryChangesNoResults
parameter_list|()
throws|throws
name|Exception
block|{
name|createChange
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|ChangeInfo
argument_list|>
name|results
init|=
name|query
argument_list|(
literal|"status:open"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|results
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|results
operator|=
name|query
argument_list|(
literal|"status:closed"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|results
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|queryChangesOneTerm ()
specifier|public
name|void
name|queryChangesOneTerm
parameter_list|()
throws|throws
name|Exception
block|{
name|PushOneCommit
operator|.
name|Result
name|r1
init|=
name|createChange
argument_list|()
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r2
init|=
name|createChange
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ChangeInfo
argument_list|>
name|results
init|=
name|query
argument_list|(
literal|"status:open"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|results
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|changeId
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|r2
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|results
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|changeId
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|r1
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|queryChangesMultipleTerms ()
specifier|public
name|void
name|queryChangesMultipleTerms
parameter_list|()
throws|throws
name|Exception
block|{
name|PushOneCommit
operator|.
name|Result
name|r1
init|=
name|createChange
argument_list|()
decl_stmt|;
name|createChange
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|ChangeInfo
argument_list|>
name|results
init|=
name|query
argument_list|(
literal|"status:open "
operator|+
name|r1
operator|.
name|getChangeId
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|Iterables
operator|.
name|getOnlyElement
argument_list|(
name|results
argument_list|)
operator|.
name|changeId
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|r1
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|queryChangesLimit ()
specifier|public
name|void
name|queryChangesLimit
parameter_list|()
throws|throws
name|Exception
block|{
name|createChange
argument_list|()
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r2
init|=
name|createChange
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ChangeInfo
argument_list|>
name|results
init|=
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|query
argument_list|()
operator|.
name|withLimit
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|results
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Iterables
operator|.
name|getOnlyElement
argument_list|(
name|results
argument_list|)
operator|.
name|changeId
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|r2
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|queryChangesStart ()
specifier|public
name|void
name|queryChangesStart
parameter_list|()
throws|throws
name|Exception
block|{
name|PushOneCommit
operator|.
name|Result
name|r1
init|=
name|createChange
argument_list|()
decl_stmt|;
name|createChange
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|ChangeInfo
argument_list|>
name|results
init|=
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|query
argument_list|()
operator|.
name|withStart
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|Iterables
operator|.
name|getOnlyElement
argument_list|(
name|results
argument_list|)
operator|.
name|changeId
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|r1
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|queryChangesNoOptions ()
specifier|public
name|void
name|queryChangesNoOptions
parameter_list|()
throws|throws
name|Exception
block|{
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|createChange
argument_list|()
decl_stmt|;
name|ChangeInfo
name|result
init|=
name|Iterables
operator|.
name|getOnlyElement
argument_list|(
name|query
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|labels
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|messages
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|revisions
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|actions
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|queryChangesOptions ()
specifier|public
name|void
name|queryChangesOptions
parameter_list|()
throws|throws
name|Exception
block|{
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|createChange
argument_list|()
decl_stmt|;
name|ChangeInfo
name|result
init|=
name|Iterables
operator|.
name|getOnlyElement
argument_list|(
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|query
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
operator|.
name|withOptions
argument_list|(
name|EnumSet
operator|.
name|allOf
argument_list|(
name|ListChangesOption
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|Iterables
operator|.
name|getOnlyElement
argument_list|(
name|result
operator|.
name|labels
operator|.
name|keySet
argument_list|()
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"Code-Review"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|messages
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|actions
argument_list|)
operator|.
name|isNotEmpty
argument_list|()
expr_stmt|;
name|RevisionInfo
name|rev
init|=
name|Iterables
operator|.
name|getOnlyElement
argument_list|(
name|result
operator|.
name|revisions
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|rev
operator|.
name|_number
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|r
operator|.
name|getPatchSetId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|rev
operator|.
name|actions
argument_list|)
operator|.
name|isNotEmpty
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|queryChangesOwnerWithDifferentUsers ()
specifier|public
name|void
name|queryChangesOwnerWithDifferentUsers
parameter_list|()
throws|throws
name|Exception
block|{
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|createChange
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|Iterables
operator|.
name|getOnlyElement
argument_list|(
name|query
argument_list|(
literal|"owner:self"
argument_list|)
argument_list|)
operator|.
name|changeId
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|setApiUser
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|query
argument_list|(
literal|"owner:self"
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|checkReviewedFlagBeforeAndAfterReview ()
specifier|public
name|void
name|checkReviewedFlagBeforeAndAfterReview
parameter_list|()
throws|throws
name|Exception
block|{
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|createChange
argument_list|()
decl_stmt|;
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
name|user
operator|.
name|email
expr_stmt|;
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
operator|.
name|addReviewer
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|setApiUser
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|get
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
operator|.
name|reviewed
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|revision
argument_list|(
name|r
argument_list|)
operator|.
name|review
argument_list|(
name|ReviewInput
operator|.
name|recommend
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|get
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
operator|.
name|reviewed
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|topic ()
specifier|public
name|void
name|topic
parameter_list|()
throws|throws
name|Exception
block|{
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|createChange
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
operator|.
name|topic
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
operator|.
name|topic
argument_list|(
literal|"mytopic"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
operator|.
name|topic
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"mytopic"
argument_list|)
expr_stmt|;
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
operator|.
name|topic
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
operator|.
name|topic
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

