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
DECL|package|com.google.gerrit.acceptance.rest.project
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|rest
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
name|GitUtil
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
name|acceptance
operator|.
name|RestResponse
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
name|common
operator|.
name|CommitInfo
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
name|junit
operator|.
name|TestRepository
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
name|lib
operator|.
name|Repository
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
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
DECL|class|GetCommitIT
specifier|public
class|class
name|GetCommitIT
extends|extends
name|AbstractDaemonTest
block|{
DECL|field|repo
specifier|private
name|TestRepository
argument_list|<
name|Repository
argument_list|>
name|repo
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|repo
operator|=
name|GitUtil
operator|.
name|newTestRepository
argument_list|(
name|repoManager
operator|.
name|openRepository
argument_list|(
name|project
argument_list|)
argument_list|)
expr_stmt|;
name|blockRead
argument_list|(
literal|"refs/*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|repo
operator|!=
literal|null
condition|)
block|{
name|repo
operator|.
name|getRepository
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|getNonExistingCommit_NotFound ()
specifier|public
name|void
name|getNonExistingCommit_NotFound
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNotFound
argument_list|(
name|ObjectId
operator|.
name|fromString
argument_list|(
literal|"deadbeefdeadbeefdeadbeefdeadbeefdeadbeef"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|getMergedCommit_Found ()
specifier|public
name|void
name|getMergedCommit_Found
parameter_list|()
throws|throws
name|Exception
block|{
name|unblockRead
argument_list|()
expr_stmt|;
name|RevCommit
name|commit
init|=
name|repo
operator|.
name|parseBody
argument_list|(
name|repo
operator|.
name|branch
argument_list|(
literal|"master"
argument_list|)
operator|.
name|commit
argument_list|()
operator|.
name|message
argument_list|(
literal|"Create\n\nNew commit\n"
argument_list|)
operator|.
name|create
argument_list|()
argument_list|)
decl_stmt|;
name|CommitInfo
name|info
init|=
name|getCommit
argument_list|(
name|commit
argument_list|)
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
name|commit
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
literal|"Create"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|message
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"Create\n\nNew commit\n"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|author
operator|.
name|name
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"J. Author"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|author
operator|.
name|email
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"jauthor@example.com"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|committer
operator|.
name|name
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"J. Committer"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|committer
operator|.
name|email
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"jcommitter@example.com"
argument_list|)
expr_stmt|;
name|CommitInfo
name|parent
init|=
name|Iterables
operator|.
name|getOnlyElement
argument_list|(
name|info
operator|.
name|parents
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|parent
operator|.
name|commit
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|commit
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
name|parent
operator|.
name|subject
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"Initial empty repository"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parent
operator|.
name|message
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|parent
operator|.
name|author
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|parent
operator|.
name|committer
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|getMergedCommit_NotFound ()
specifier|public
name|void
name|getMergedCommit_NotFound
parameter_list|()
throws|throws
name|Exception
block|{
name|RevCommit
name|commit
init|=
name|repo
operator|.
name|parseBody
argument_list|(
name|repo
operator|.
name|branch
argument_list|(
literal|"master"
argument_list|)
operator|.
name|commit
argument_list|()
operator|.
name|message
argument_list|(
literal|"Create\n\nNew commit\n"
argument_list|)
operator|.
name|create
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotFound
argument_list|(
name|commit
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|getOpenChange_Found ()
specifier|public
name|void
name|getOpenChange_Found
parameter_list|()
throws|throws
name|Exception
block|{
name|unblockRead
argument_list|()
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|pushFactory
operator|.
name|create
argument_list|(
name|admin
operator|.
name|getIdent
argument_list|()
argument_list|,
name|testRepo
argument_list|)
operator|.
name|to
argument_list|(
literal|"refs/for/master"
argument_list|)
decl_stmt|;
name|r
operator|.
name|assertOkStatus
argument_list|()
expr_stmt|;
name|CommitInfo
name|info
init|=
name|getCommit
argument_list|(
name|r
operator|.
name|getCommit
argument_list|()
argument_list|)
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
name|r
operator|.
name|getCommit
argument_list|()
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
literal|"test commit"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|message
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"test commit\n\nChange-Id: "
operator|+
name|r
operator|.
name|getChangeId
argument_list|()
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|author
operator|.
name|name
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"Administrator"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|author
operator|.
name|email
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"admin@example.com"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|committer
operator|.
name|name
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"Administrator"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|committer
operator|.
name|email
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"admin@example.com"
argument_list|)
expr_stmt|;
name|CommitInfo
name|parent
init|=
name|Iterables
operator|.
name|getOnlyElement
argument_list|(
name|info
operator|.
name|parents
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|parent
operator|.
name|commit
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|r
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
name|parent
operator|.
name|subject
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"Initial empty repository"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parent
operator|.
name|message
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|parent
operator|.
name|author
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|parent
operator|.
name|committer
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|getOpenChange_NotFound ()
specifier|public
name|void
name|getOpenChange_NotFound
parameter_list|()
throws|throws
name|Exception
block|{
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|pushFactory
operator|.
name|create
argument_list|(
name|admin
operator|.
name|getIdent
argument_list|()
argument_list|,
name|testRepo
argument_list|)
operator|.
name|to
argument_list|(
literal|"refs/for/master"
argument_list|)
decl_stmt|;
name|r
operator|.
name|assertOkStatus
argument_list|()
expr_stmt|;
name|assertNotFound
argument_list|(
name|r
operator|.
name|getCommit
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|unblockRead ()
specifier|private
name|void
name|unblockRead
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|ProjectConfigUpdate
name|u
init|=
name|updateProject
argument_list|(
name|project
argument_list|)
init|)
block|{
name|u
operator|.
name|getConfig
argument_list|()
operator|.
name|getAccessSection
argument_list|(
literal|"refs/*"
argument_list|)
operator|.
name|remove
argument_list|(
operator|new
name|Permission
argument_list|(
name|Permission
operator|.
name|READ
argument_list|)
argument_list|)
expr_stmt|;
name|u
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|assertNotFound (ObjectId id)
specifier|private
name|void
name|assertNotFound
parameter_list|(
name|ObjectId
name|id
parameter_list|)
throws|throws
name|Exception
block|{
name|userRestSession
operator|.
name|get
argument_list|(
literal|"/projects/"
operator|+
name|project
operator|.
name|get
argument_list|()
operator|+
literal|"/commits/"
operator|+
name|id
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|assertNotFound
argument_list|()
expr_stmt|;
block|}
DECL|method|getCommit (ObjectId id)
specifier|private
name|CommitInfo
name|getCommit
parameter_list|(
name|ObjectId
name|id
parameter_list|)
throws|throws
name|Exception
block|{
name|RestResponse
name|r
init|=
name|userRestSession
operator|.
name|get
argument_list|(
literal|"/projects/"
operator|+
name|project
operator|.
name|get
argument_list|()
operator|+
literal|"/commits/"
operator|+
name|id
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|r
operator|.
name|assertOK
argument_list|()
expr_stmt|;
name|CommitInfo
name|result
init|=
name|newGson
argument_list|()
operator|.
name|fromJson
argument_list|(
name|r
operator|.
name|getReader
argument_list|()
argument_list|,
name|CommitInfo
operator|.
name|class
argument_list|)
decl_stmt|;
name|r
operator|.
name|consume
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

