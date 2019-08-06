begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2017 The Android Open Source Project
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
DECL|package|com.google.gerrit.acceptance.api.project
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
name|project
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
import|import static
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
operator|.
name|toList
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Constants
operator|.
name|R_TAGS
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
operator|.
name|Result
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
name|TestAccount
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
name|data
operator|.
name|Permission
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
name|IncludedInInfo
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
name|projects
operator|.
name|BranchInput
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
name|projects
operator|.
name|TagInput
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
name|ChangeMessageInfo
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
name|CommitInfo
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
name|GitPerson
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
name|reviewdb
operator|.
name|client
operator|.
name|Branch
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|ObjectId
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
name|revwalk
operator|.
name|RevCommit
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

begin_class
annotation|@
name|NoHttpd
DECL|class|CommitIT
specifier|public
class|class
name|CommitIT
extends|extends
name|AbstractDaemonTest
block|{
annotation|@
name|Test
DECL|method|getCommitInfo ()
specifier|public
name|void
name|getCommitInfo
parameter_list|()
throws|throws
name|Exception
block|{
name|Result
name|result
init|=
name|createChange
argument_list|()
decl_stmt|;
name|String
name|commitId
init|=
name|result
operator|.
name|getCommit
argument_list|()
operator|.
name|getId
argument_list|()
operator|.
name|name
argument_list|()
decl_stmt|;
name|CommitInfo
name|info
init|=
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|name
argument_list|(
name|project
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|commit
argument_list|(
name|commitId
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|commit
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|commitId
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|parents
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|c
lambda|->
name|c
operator|.
name|commit
argument_list|)
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|result
operator|.
name|getCommit
argument_list|()
operator|.
name|getParent
argument_list|(
literal|0
argument_list|)
operator|.
name|name
argument_list|()
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
name|result
operator|.
name|getCommit
argument_list|()
operator|.
name|getShortMessage
argument_list|()
argument_list|)
expr_stmt|;
name|assertPerson
argument_list|(
name|info
operator|.
name|author
argument_list|,
name|admin
argument_list|)
expr_stmt|;
name|assertPerson
argument_list|(
name|info
operator|.
name|committer
argument_list|,
name|admin
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|webLinks
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|includedInOpenChange ()
specifier|public
name|void
name|includedInOpenChange
parameter_list|()
throws|throws
name|Exception
block|{
name|Result
name|result
init|=
name|createChange
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|getIncludedIn
argument_list|(
name|result
operator|.
name|getCommit
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|branches
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|getIncludedIn
argument_list|(
name|result
operator|.
name|getCommit
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|tags
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|includedInMergedChange ()
specifier|public
name|void
name|includedInMergedChange
parameter_list|()
throws|throws
name|Exception
block|{
name|Result
name|result
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
name|result
operator|.
name|getChangeId
argument_list|()
argument_list|)
operator|.
name|revision
argument_list|(
name|result
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
name|result
operator|.
name|getChangeId
argument_list|()
argument_list|)
operator|.
name|revision
argument_list|(
name|result
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
name|getIncludedIn
argument_list|(
name|result
operator|.
name|getCommit
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|branches
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|"master"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|getIncludedIn
argument_list|(
name|result
operator|.
name|getCommit
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|tags
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|grant
argument_list|(
name|project
argument_list|,
name|R_TAGS
operator|+
literal|"*"
argument_list|,
name|Permission
operator|.
name|CREATE_TAG
argument_list|)
expr_stmt|;
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|name
argument_list|(
name|result
operator|.
name|getChange
argument_list|()
operator|.
name|project
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|tag
argument_list|(
literal|"test-tag"
argument_list|)
operator|.
name|create
argument_list|(
operator|new
name|TagInput
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|getIncludedIn
argument_list|(
name|result
operator|.
name|getCommit
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|tags
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|"test-tag"
argument_list|)
expr_stmt|;
name|createBranch
argument_list|(
operator|new
name|Branch
operator|.
name|NameKey
argument_list|(
name|project
operator|.
name|get
argument_list|()
argument_list|,
literal|"test-branch"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|getIncludedIn
argument_list|(
name|result
operator|.
name|getCommit
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|branches
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|"master"
argument_list|,
literal|"test-branch"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|cherryPickCommitWithoutChangeId ()
specifier|public
name|void
name|cherryPickCommitWithoutChangeId
parameter_list|()
throws|throws
name|Exception
block|{
comment|// This test is a little superfluous, since the current cherry-pick code ignores
comment|// the commit message of the to-be-cherry-picked change, using the one in
comment|// CherryPickInput instead.
name|CherryPickInput
name|input
init|=
operator|new
name|CherryPickInput
argument_list|()
decl_stmt|;
name|input
operator|.
name|destination
operator|=
literal|"foo"
expr_stmt|;
name|input
operator|.
name|message
operator|=
literal|"it goes to foo branch"
expr_stmt|;
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|name
argument_list|(
name|project
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|branch
argument_list|(
name|input
operator|.
name|destination
argument_list|)
operator|.
name|create
argument_list|(
operator|new
name|BranchInput
argument_list|()
argument_list|)
expr_stmt|;
name|RevCommit
name|revCommit
init|=
name|createNewCommitWithoutChangeId
argument_list|(
literal|"refs/heads/master"
argument_list|,
literal|"a.txt"
argument_list|,
literal|"content"
argument_list|)
decl_stmt|;
name|ChangeInfo
name|changeInfo
init|=
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|name
argument_list|(
name|project
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|commit
argument_list|(
name|revCommit
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|cherryPick
argument_list|(
name|input
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|changeInfo
operator|.
name|messages
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|ChangeMessageInfo
argument_list|>
name|messageIterator
init|=
name|changeInfo
operator|.
name|messages
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|String
name|expectedMessage
init|=
name|String
operator|.
name|format
argument_list|(
literal|"Patch Set 1: Cherry Picked from commit %s."
argument_list|,
name|revCommit
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|messageIterator
operator|.
name|next
argument_list|()
operator|.
name|message
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|expectedMessage
argument_list|)
expr_stmt|;
name|RevisionInfo
name|revInfo
init|=
name|changeInfo
operator|.
name|revisions
operator|.
name|get
argument_list|(
name|changeInfo
operator|.
name|currentRevision
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|revInfo
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|CommitInfo
name|commitInfo
init|=
name|revInfo
operator|.
name|commit
decl_stmt|;
name|assertThat
argument_list|(
name|commitInfo
operator|.
name|message
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|input
operator|.
name|message
operator|+
literal|"\n\nChange-Id: "
operator|+
name|changeInfo
operator|.
name|changeId
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|cherryPickCommitWithChangeId ()
specifier|public
name|void
name|cherryPickCommitWithChangeId
parameter_list|()
throws|throws
name|Exception
block|{
name|CherryPickInput
name|input
init|=
operator|new
name|CherryPickInput
argument_list|()
decl_stmt|;
name|input
operator|.
name|destination
operator|=
literal|"foo"
expr_stmt|;
name|RevCommit
name|revCommit
init|=
name|createChange
argument_list|()
operator|.
name|getCommit
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|footers
init|=
name|revCommit
operator|.
name|getFooterLines
argument_list|(
literal|"Change-Id"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|footers
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|String
name|changeId
init|=
name|footers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|input
operator|.
name|message
operator|=
literal|"it goes to foo branch\n\nChange-Id: "
operator|+
name|changeId
expr_stmt|;
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|name
argument_list|(
name|project
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|branch
argument_list|(
name|input
operator|.
name|destination
argument_list|)
operator|.
name|create
argument_list|(
operator|new
name|BranchInput
argument_list|()
argument_list|)
expr_stmt|;
name|ChangeInfo
name|changeInfo
init|=
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|name
argument_list|(
name|project
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|commit
argument_list|(
name|revCommit
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|cherryPick
argument_list|(
name|input
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|changeInfo
operator|.
name|messages
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|ChangeMessageInfo
argument_list|>
name|messageIterator
init|=
name|changeInfo
operator|.
name|messages
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|String
name|expectedMessage
init|=
name|String
operator|.
name|format
argument_list|(
literal|"Patch Set 1: Cherry Picked from commit %s."
argument_list|,
name|revCommit
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|messageIterator
operator|.
name|next
argument_list|()
operator|.
name|message
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|expectedMessage
argument_list|)
expr_stmt|;
name|RevisionInfo
name|revInfo
init|=
name|changeInfo
operator|.
name|revisions
operator|.
name|get
argument_list|(
name|changeInfo
operator|.
name|currentRevision
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|revInfo
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|revInfo
operator|.
name|commit
operator|.
name|message
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|input
operator|.
name|message
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
DECL|method|getIncludedIn (ObjectId id)
specifier|private
name|IncludedInInfo
name|getIncludedIn
parameter_list|(
name|ObjectId
name|id
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|name
argument_list|(
name|project
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|commit
argument_list|(
name|id
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|includedIn
argument_list|()
return|;
block|}
DECL|method|assertPerson (GitPerson actual, TestAccount expected)
specifier|private
specifier|static
name|void
name|assertPerson
parameter_list|(
name|GitPerson
name|actual
parameter_list|,
name|TestAccount
name|expected
parameter_list|)
block|{
name|assertThat
argument_list|(
name|actual
operator|.
name|email
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|expected
operator|.
name|email
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|actual
operator|.
name|name
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|expected
operator|.
name|fullName
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

