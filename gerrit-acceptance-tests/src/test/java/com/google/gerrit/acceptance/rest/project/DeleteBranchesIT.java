begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2016 The Android Open Source Project
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
import|import static
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
operator|.
name|RefAssert
operator|.
name|assertRefNames
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
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
name|ImmutableList
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
name|Lists
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
name|DeleteBranchesInput
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
name|ProjectApi
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
name|BadRequestException
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
name|RefNames
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
annotation|@
name|NoHttpd
DECL|class|DeleteBranchesIT
specifier|public
class|class
name|DeleteBranchesIT
extends|extends
name|AbstractDaemonTest
block|{
DECL|field|BRANCHES
specifier|private
specifier|static
specifier|final
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|BRANCHES
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"refs/heads/test-1"
argument_list|,
literal|"refs/heads/test-2"
argument_list|,
literal|"refs/heads/test-3"
argument_list|)
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
for|for
control|(
name|String
name|name
range|:
name|BRANCHES
control|)
block|{
name|project
argument_list|()
operator|.
name|branch
argument_list|(
name|name
argument_list|)
operator|.
name|create
argument_list|(
operator|new
name|BranchInput
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertBranches
argument_list|(
name|BRANCHES
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|deleteBranches ()
specifier|public
name|void
name|deleteBranches
parameter_list|()
throws|throws
name|Exception
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|RevCommit
argument_list|>
name|initialRevisions
init|=
name|initialRevisions
argument_list|(
name|BRANCHES
argument_list|)
decl_stmt|;
name|DeleteBranchesInput
name|input
init|=
operator|new
name|DeleteBranchesInput
argument_list|()
decl_stmt|;
name|input
operator|.
name|branches
operator|=
name|BRANCHES
expr_stmt|;
name|project
argument_list|()
operator|.
name|deleteBranches
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|assertBranchesDeleted
argument_list|()
expr_stmt|;
name|assertRefUpdatedEvents
argument_list|(
name|initialRevisions
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|deleteBranchesForbidden ()
specifier|public
name|void
name|deleteBranchesForbidden
parameter_list|()
throws|throws
name|Exception
block|{
name|DeleteBranchesInput
name|input
init|=
operator|new
name|DeleteBranchesInput
argument_list|()
decl_stmt|;
name|input
operator|.
name|branches
operator|=
name|BRANCHES
expr_stmt|;
name|setApiUser
argument_list|(
name|user
argument_list|)
expr_stmt|;
try|try
block|{
name|project
argument_list|()
operator|.
name|deleteBranches
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected ResourceConflictException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ResourceConflictException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
argument_list|)
operator|.
name|hasMessage
argument_list|(
name|errorMessageForBranches
argument_list|(
name|BRANCHES
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|setApiUser
argument_list|(
name|admin
argument_list|)
expr_stmt|;
name|assertBranches
argument_list|(
name|BRANCHES
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|deleteBranchesNotFound ()
specifier|public
name|void
name|deleteBranchesNotFound
parameter_list|()
throws|throws
name|Exception
block|{
name|DeleteBranchesInput
name|input
init|=
operator|new
name|DeleteBranchesInput
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|branches
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|BRANCHES
argument_list|)
decl_stmt|;
name|branches
operator|.
name|add
argument_list|(
literal|"refs/heads/does-not-exist"
argument_list|)
expr_stmt|;
name|input
operator|.
name|branches
operator|=
name|branches
expr_stmt|;
try|try
block|{
name|project
argument_list|()
operator|.
name|deleteBranches
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected ResourceConflictException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ResourceConflictException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
argument_list|)
operator|.
name|hasMessage
argument_list|(
name|errorMessageForBranches
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"refs/heads/does-not-exist"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertBranchesDeleted
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|deleteBranchesNotFoundContinue ()
specifier|public
name|void
name|deleteBranchesNotFoundContinue
parameter_list|()
throws|throws
name|Exception
block|{
comment|// If it fails on the first branch in the input, it should still
comment|// continue to process the remaining branches.
name|DeleteBranchesInput
name|input
init|=
operator|new
name|DeleteBranchesInput
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|branches
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"refs/heads/does-not-exist"
argument_list|)
decl_stmt|;
name|branches
operator|.
name|addAll
argument_list|(
name|BRANCHES
argument_list|)
expr_stmt|;
name|input
operator|.
name|branches
operator|=
name|branches
expr_stmt|;
try|try
block|{
name|project
argument_list|()
operator|.
name|deleteBranches
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected ResourceConflictException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ResourceConflictException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
argument_list|)
operator|.
name|hasMessage
argument_list|(
name|errorMessageForBranches
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"refs/heads/does-not-exist"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertBranchesDeleted
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|missingInput ()
specifier|public
name|void
name|missingInput
parameter_list|()
throws|throws
name|Exception
block|{
name|DeleteBranchesInput
name|input
init|=
literal|null
decl_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|BadRequestException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"branches must be specified"
argument_list|)
expr_stmt|;
name|project
argument_list|()
operator|.
name|deleteBranches
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|missingBranchList ()
specifier|public
name|void
name|missingBranchList
parameter_list|()
throws|throws
name|Exception
block|{
name|DeleteBranchesInput
name|input
init|=
operator|new
name|DeleteBranchesInput
argument_list|()
decl_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|BadRequestException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"branches must be specified"
argument_list|)
expr_stmt|;
name|project
argument_list|()
operator|.
name|deleteBranches
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|emptyBranchList ()
specifier|public
name|void
name|emptyBranchList
parameter_list|()
throws|throws
name|Exception
block|{
name|DeleteBranchesInput
name|input
init|=
operator|new
name|DeleteBranchesInput
argument_list|()
decl_stmt|;
name|input
operator|.
name|branches
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|()
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|BadRequestException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"branches must be specified"
argument_list|)
expr_stmt|;
name|project
argument_list|()
operator|.
name|deleteBranches
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
DECL|method|errorMessageForBranches (List<String> branches)
specifier|private
name|String
name|errorMessageForBranches
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|branches
parameter_list|)
block|{
name|StringBuilder
name|message
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|branch
range|:
name|branches
control|)
block|{
name|message
operator|.
name|append
argument_list|(
literal|"Cannot delete "
argument_list|)
operator|.
name|append
argument_list|(
name|branch
argument_list|)
operator|.
name|append
argument_list|(
literal|": it doesn't exist or you do not have permission "
argument_list|)
operator|.
name|append
argument_list|(
literal|"to delete it\n"
argument_list|)
expr_stmt|;
block|}
return|return
name|message
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|initialRevisions (List<String> branches)
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|RevCommit
argument_list|>
name|initialRevisions
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|branches
parameter_list|)
throws|throws
name|Exception
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|RevCommit
argument_list|>
name|result
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|branch
range|:
name|branches
control|)
block|{
name|result
operator|.
name|put
argument_list|(
name|branch
argument_list|,
name|getRemoteHead
argument_list|(
name|project
argument_list|,
name|branch
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|assertRefUpdatedEvents (HashMap<String, RevCommit> revisions)
specifier|private
name|void
name|assertRefUpdatedEvents
parameter_list|(
name|HashMap
argument_list|<
name|String
argument_list|,
name|RevCommit
argument_list|>
name|revisions
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|String
name|branch
range|:
name|revisions
operator|.
name|keySet
argument_list|()
control|)
block|{
name|RevCommit
name|revision
init|=
name|revisions
operator|.
name|get
argument_list|(
name|branch
argument_list|)
decl_stmt|;
name|eventRecorder
operator|.
name|assertRefUpdatedEvents
argument_list|(
name|project
operator|.
name|get
argument_list|()
argument_list|,
name|branch
argument_list|,
literal|null
argument_list|,
name|revision
argument_list|,
name|revision
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|project ()
specifier|private
name|ProjectApi
name|project
parameter_list|()
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
return|;
block|}
DECL|method|assertBranches (List<String> branches)
specifier|private
name|void
name|assertBranches
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|branches
parameter_list|)
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|expected
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"HEAD"
argument_list|,
name|RefNames
operator|.
name|REFS_CONFIG
argument_list|,
literal|"refs/heads/master"
argument_list|)
decl_stmt|;
name|expected
operator|.
name|addAll
argument_list|(
name|branches
argument_list|)
expr_stmt|;
name|assertRefNames
argument_list|(
name|expected
argument_list|,
name|project
argument_list|()
operator|.
name|branches
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertBranchesDeleted ()
specifier|private
name|void
name|assertBranchesDeleted
parameter_list|()
throws|throws
name|Exception
block|{
name|assertBranches
argument_list|(
name|ImmutableList
operator|.
expr|<
name|String
operator|>
name|of
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

