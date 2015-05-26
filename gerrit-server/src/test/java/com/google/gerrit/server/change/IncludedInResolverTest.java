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
name|IncludedInDetail
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
name|api
operator|.
name|Git
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
name|api
operator|.
name|MergeCommand
operator|.
name|FastForwardMode
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
name|RepositoryTestCase
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
name|Ref
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
name|eclipse
operator|.
name|jgit
operator|.
name|revwalk
operator|.
name|RevTag
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
name|RevWalk
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
name|Assert
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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

begin_class
DECL|class|IncludedInResolverTest
specifier|public
class|class
name|IncludedInResolverTest
extends|extends
name|RepositoryTestCase
block|{
comment|// Branch names
DECL|field|BRANCH_MASTER
specifier|private
specifier|static
specifier|final
name|String
name|BRANCH_MASTER
init|=
literal|"master"
decl_stmt|;
DECL|field|BRANCH_1_0
specifier|private
specifier|static
specifier|final
name|String
name|BRANCH_1_0
init|=
literal|"rel-1.0"
decl_stmt|;
DECL|field|BRANCH_1_3
specifier|private
specifier|static
specifier|final
name|String
name|BRANCH_1_3
init|=
literal|"rel-1.3"
decl_stmt|;
DECL|field|BRANCH_2_0
specifier|private
specifier|static
specifier|final
name|String
name|BRANCH_2_0
init|=
literal|"rel-2.0"
decl_stmt|;
DECL|field|BRANCH_2_5
specifier|private
specifier|static
specifier|final
name|String
name|BRANCH_2_5
init|=
literal|"rel-2.5"
decl_stmt|;
comment|// Tag names
DECL|field|TAG_1_0
specifier|private
specifier|static
specifier|final
name|String
name|TAG_1_0
init|=
literal|"1.0"
decl_stmt|;
DECL|field|TAG_1_0_1
specifier|private
specifier|static
specifier|final
name|String
name|TAG_1_0_1
init|=
literal|"1.0.1"
decl_stmt|;
DECL|field|TAG_1_3
specifier|private
specifier|static
specifier|final
name|String
name|TAG_1_3
init|=
literal|"1.3"
decl_stmt|;
DECL|field|TAG_2_0_1
specifier|private
specifier|static
specifier|final
name|String
name|TAG_2_0_1
init|=
literal|"2.0.1"
decl_stmt|;
DECL|field|TAG_2_0
specifier|private
specifier|static
specifier|final
name|String
name|TAG_2_0
init|=
literal|"2.0"
decl_stmt|;
DECL|field|TAG_2_5
specifier|private
specifier|static
specifier|final
name|String
name|TAG_2_5
init|=
literal|"2.5"
decl_stmt|;
DECL|field|TAG_2_5_ANNOTATED
specifier|private
specifier|static
specifier|final
name|String
name|TAG_2_5_ANNOTATED
init|=
literal|"2.5-annotated"
decl_stmt|;
DECL|field|TAG_2_5_ANNOTATED_TWICE
specifier|private
specifier|static
specifier|final
name|String
name|TAG_2_5_ANNOTATED_TWICE
init|=
literal|"2.5-annotated_twice"
decl_stmt|;
comment|// Commits
DECL|field|commit_initial
specifier|private
name|RevCommit
name|commit_initial
decl_stmt|;
DECL|field|commit_v1_3
specifier|private
name|RevCommit
name|commit_v1_3
decl_stmt|;
DECL|field|commit_v2_5
specifier|private
name|RevCommit
name|commit_v2_5
decl_stmt|;
DECL|field|expTags
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|expTags
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|expBranches
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|expBranches
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|revWalk
specifier|private
name|RevWalk
name|revWalk
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
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
comment|/*- The following graph will be created.        o   tag 2.5, 2.5_annotated, 2.5_annotated_twice       |\       | o tag 2.0.1       | o tag 2.0       o | tag 1.3       |/       o   c3        | o tag 1.0.1       |/       o   tag 1.0       o   c2       o   c1       */
name|Git
name|git
init|=
operator|new
name|Git
argument_list|(
name|db
argument_list|)
decl_stmt|;
name|revWalk
operator|=
operator|new
name|RevWalk
argument_list|(
name|db
argument_list|)
expr_stmt|;
comment|// Version 1.0
name|commit_initial
operator|=
name|git
operator|.
name|commit
argument_list|()
operator|.
name|setMessage
argument_list|(
literal|"c1"
argument_list|)
operator|.
name|call
argument_list|()
expr_stmt|;
name|git
operator|.
name|commit
argument_list|()
operator|.
name|setMessage
argument_list|(
literal|"c2"
argument_list|)
operator|.
name|call
argument_list|()
expr_stmt|;
name|RevCommit
name|commit_v1_0
init|=
name|git
operator|.
name|commit
argument_list|()
operator|.
name|setMessage
argument_list|(
literal|"version 1.0"
argument_list|)
operator|.
name|call
argument_list|()
decl_stmt|;
name|git
operator|.
name|tag
argument_list|()
operator|.
name|setName
argument_list|(
name|TAG_1_0
argument_list|)
operator|.
name|setObjectId
argument_list|(
name|commit_v1_0
argument_list|)
operator|.
name|call
argument_list|()
expr_stmt|;
name|RevCommit
name|c3
init|=
name|git
operator|.
name|commit
argument_list|()
operator|.
name|setMessage
argument_list|(
literal|"c3"
argument_list|)
operator|.
name|call
argument_list|()
decl_stmt|;
comment|// Version 1.01
name|createAndCheckoutBranch
argument_list|(
name|commit_v1_0
argument_list|,
name|BRANCH_1_0
argument_list|)
expr_stmt|;
name|RevCommit
name|commit_v1_0_1
init|=
name|git
operator|.
name|commit
argument_list|()
operator|.
name|setMessage
argument_list|(
literal|"verREFS_HEADS_RELsion 1.0.1"
argument_list|)
operator|.
name|call
argument_list|()
decl_stmt|;
name|git
operator|.
name|tag
argument_list|()
operator|.
name|setName
argument_list|(
name|TAG_1_0_1
argument_list|)
operator|.
name|setObjectId
argument_list|(
name|commit_v1_0_1
argument_list|)
operator|.
name|call
argument_list|()
expr_stmt|;
comment|// Version 1.3
name|createAndCheckoutBranch
argument_list|(
name|c3
argument_list|,
name|BRANCH_1_3
argument_list|)
expr_stmt|;
name|commit_v1_3
operator|=
name|git
operator|.
name|commit
argument_list|()
operator|.
name|setMessage
argument_list|(
literal|"version 1.3"
argument_list|)
operator|.
name|call
argument_list|()
expr_stmt|;
name|git
operator|.
name|tag
argument_list|()
operator|.
name|setName
argument_list|(
name|TAG_1_3
argument_list|)
operator|.
name|setObjectId
argument_list|(
name|commit_v1_3
argument_list|)
operator|.
name|call
argument_list|()
expr_stmt|;
comment|// Version 2.0
name|createAndCheckoutBranch
argument_list|(
name|c3
argument_list|,
name|BRANCH_2_0
argument_list|)
expr_stmt|;
name|RevCommit
name|commit_v2_0
init|=
name|git
operator|.
name|commit
argument_list|()
operator|.
name|setMessage
argument_list|(
literal|"version 2.0"
argument_list|)
operator|.
name|call
argument_list|()
decl_stmt|;
name|git
operator|.
name|tag
argument_list|()
operator|.
name|setName
argument_list|(
name|TAG_2_0
argument_list|)
operator|.
name|setObjectId
argument_list|(
name|commit_v2_0
argument_list|)
operator|.
name|call
argument_list|()
expr_stmt|;
name|RevCommit
name|commit_v2_0_1
init|=
name|git
operator|.
name|commit
argument_list|()
operator|.
name|setMessage
argument_list|(
literal|"version 2.0.1"
argument_list|)
operator|.
name|call
argument_list|()
decl_stmt|;
name|git
operator|.
name|tag
argument_list|()
operator|.
name|setName
argument_list|(
name|TAG_2_0_1
argument_list|)
operator|.
name|setObjectId
argument_list|(
name|commit_v2_0_1
argument_list|)
operator|.
name|call
argument_list|()
expr_stmt|;
comment|// Version 2.5
name|createAndCheckoutBranch
argument_list|(
name|commit_v1_3
argument_list|,
name|BRANCH_2_5
argument_list|)
expr_stmt|;
name|git
operator|.
name|merge
argument_list|()
operator|.
name|include
argument_list|(
name|commit_v2_0_1
argument_list|)
operator|.
name|setCommit
argument_list|(
literal|false
argument_list|)
operator|.
name|setFastForward
argument_list|(
name|FastForwardMode
operator|.
name|NO_FF
argument_list|)
operator|.
name|call
argument_list|()
expr_stmt|;
name|commit_v2_5
operator|=
name|git
operator|.
name|commit
argument_list|()
operator|.
name|setMessage
argument_list|(
literal|"version 2.5"
argument_list|)
operator|.
name|call
argument_list|()
expr_stmt|;
name|git
operator|.
name|tag
argument_list|()
operator|.
name|setName
argument_list|(
name|TAG_2_5
argument_list|)
operator|.
name|setObjectId
argument_list|(
name|commit_v2_5
argument_list|)
operator|.
name|setAnnotated
argument_list|(
literal|false
argument_list|)
operator|.
name|call
argument_list|()
expr_stmt|;
name|Ref
name|ref_tag_2_5_annotated
init|=
name|git
operator|.
name|tag
argument_list|()
operator|.
name|setName
argument_list|(
name|TAG_2_5_ANNOTATED
argument_list|)
operator|.
name|setObjectId
argument_list|(
name|commit_v2_5
argument_list|)
operator|.
name|setAnnotated
argument_list|(
literal|true
argument_list|)
operator|.
name|call
argument_list|()
decl_stmt|;
name|RevTag
name|tag_2_5_annotated
init|=
name|revWalk
operator|.
name|parseTag
argument_list|(
name|ref_tag_2_5_annotated
operator|.
name|getObjectId
argument_list|()
argument_list|)
decl_stmt|;
name|git
operator|.
name|tag
argument_list|()
operator|.
name|setName
argument_list|(
name|TAG_2_5_ANNOTATED_TWICE
argument_list|)
operator|.
name|setObjectId
argument_list|(
name|tag_2_5_annotated
argument_list|)
operator|.
name|setAnnotated
argument_list|(
literal|true
argument_list|)
operator|.
name|call
argument_list|()
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
name|revWalk
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|resolveLatestCommit ()
specifier|public
name|void
name|resolveLatestCommit
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Check tip commit
name|IncludedInDetail
name|detail
init|=
name|resolve
argument_list|(
name|commit_v2_5
argument_list|)
decl_stmt|;
comment|// Check that only tags and branches which refer the tip are returned
name|expTags
operator|.
name|add
argument_list|(
name|TAG_2_5
argument_list|)
expr_stmt|;
name|expTags
operator|.
name|add
argument_list|(
name|TAG_2_5_ANNOTATED
argument_list|)
expr_stmt|;
name|expTags
operator|.
name|add
argument_list|(
name|TAG_2_5_ANNOTATED_TWICE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expTags
argument_list|,
name|detail
operator|.
name|getTags
argument_list|()
argument_list|)
expr_stmt|;
name|expBranches
operator|.
name|add
argument_list|(
name|BRANCH_2_5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expBranches
argument_list|,
name|detail
operator|.
name|getBranches
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|resolveFirstCommit ()
specifier|public
name|void
name|resolveFirstCommit
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Check first commit
name|IncludedInDetail
name|detail
init|=
name|resolve
argument_list|(
name|commit_initial
argument_list|)
decl_stmt|;
comment|// Check whether all tags and branches are returned
name|expTags
operator|.
name|add
argument_list|(
name|TAG_1_0
argument_list|)
expr_stmt|;
name|expTags
operator|.
name|add
argument_list|(
name|TAG_1_0_1
argument_list|)
expr_stmt|;
name|expTags
operator|.
name|add
argument_list|(
name|TAG_1_3
argument_list|)
expr_stmt|;
name|expTags
operator|.
name|add
argument_list|(
name|TAG_2_0
argument_list|)
expr_stmt|;
name|expTags
operator|.
name|add
argument_list|(
name|TAG_2_0_1
argument_list|)
expr_stmt|;
name|expTags
operator|.
name|add
argument_list|(
name|TAG_2_5
argument_list|)
expr_stmt|;
name|expTags
operator|.
name|add
argument_list|(
name|TAG_2_5_ANNOTATED
argument_list|)
expr_stmt|;
name|expTags
operator|.
name|add
argument_list|(
name|TAG_2_5_ANNOTATED_TWICE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expTags
argument_list|,
name|detail
operator|.
name|getTags
argument_list|()
argument_list|)
expr_stmt|;
name|expBranches
operator|.
name|add
argument_list|(
name|BRANCH_MASTER
argument_list|)
expr_stmt|;
name|expBranches
operator|.
name|add
argument_list|(
name|BRANCH_1_0
argument_list|)
expr_stmt|;
name|expBranches
operator|.
name|add
argument_list|(
name|BRANCH_1_3
argument_list|)
expr_stmt|;
name|expBranches
operator|.
name|add
argument_list|(
name|BRANCH_2_0
argument_list|)
expr_stmt|;
name|expBranches
operator|.
name|add
argument_list|(
name|BRANCH_2_5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expBranches
argument_list|,
name|detail
operator|.
name|getBranches
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|resolveBetwixtCommit ()
specifier|public
name|void
name|resolveBetwixtCommit
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Check a commit somewhere in the middle
name|IncludedInDetail
name|detail
init|=
name|resolve
argument_list|(
name|commit_v1_3
argument_list|)
decl_stmt|;
comment|// Check whether all succeeding tags and branches are returned
name|expTags
operator|.
name|add
argument_list|(
name|TAG_1_3
argument_list|)
expr_stmt|;
name|expTags
operator|.
name|add
argument_list|(
name|TAG_2_5
argument_list|)
expr_stmt|;
name|expTags
operator|.
name|add
argument_list|(
name|TAG_2_5_ANNOTATED
argument_list|)
expr_stmt|;
name|expTags
operator|.
name|add
argument_list|(
name|TAG_2_5_ANNOTATED_TWICE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expTags
argument_list|,
name|detail
operator|.
name|getTags
argument_list|()
argument_list|)
expr_stmt|;
name|expBranches
operator|.
name|add
argument_list|(
name|BRANCH_1_3
argument_list|)
expr_stmt|;
name|expBranches
operator|.
name|add
argument_list|(
name|BRANCH_2_5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expBranches
argument_list|,
name|detail
operator|.
name|getBranches
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|resolve (RevCommit commit)
specifier|private
name|IncludedInDetail
name|resolve
parameter_list|(
name|RevCommit
name|commit
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|IncludedInResolver
operator|.
name|resolve
argument_list|(
name|db
argument_list|,
name|revWalk
argument_list|,
name|commit
argument_list|)
return|;
block|}
DECL|method|assertEquals (List<String> list1, List<String> list2)
specifier|private
name|void
name|assertEquals
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|list1
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|list2
parameter_list|)
block|{
name|Collections
operator|.
name|sort
argument_list|(
name|list1
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|list2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|list1
argument_list|,
name|list2
argument_list|)
expr_stmt|;
block|}
DECL|method|createAndCheckoutBranch (ObjectId objectId, String branchName)
specifier|private
name|void
name|createAndCheckoutBranch
parameter_list|(
name|ObjectId
name|objectId
parameter_list|,
name|String
name|branchName
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|fullBranchName
init|=
literal|"refs/heads/"
operator|+
name|branchName
decl_stmt|;
name|super
operator|.
name|createBranch
argument_list|(
name|objectId
argument_list|,
name|fullBranchName
argument_list|)
expr_stmt|;
name|super
operator|.
name|checkoutBranch
argument_list|(
name|fullBranchName
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

