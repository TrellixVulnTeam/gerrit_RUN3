begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2015 The Android Open Source Project
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
name|common
operator|.
name|collect
operator|.
name|Collections2
operator|.
name|permutations
import|;
end_import

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
name|reviewdb
operator|.
name|client
operator|.
name|Account
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
name|Change
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
name|PatchSet
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
name|Project
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
name|RevId
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
name|WalkSorter
operator|.
name|PatchSetData
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
name|query
operator|.
name|change
operator|.
name|ChangeData
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
name|testutil
operator|.
name|InMemoryRepositoryManager
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
name|testutil
operator|.
name|InMemoryRepositoryManager
operator|.
name|Repo
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
name|testutil
operator|.
name|TestChanges
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
name|client
operator|.
name|KeyUtil
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
name|StandardKeyEncoder
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
name|RevWalk
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
name|List
import|;
end_import

begin_class
DECL|class|WalkSorterTest
specifier|public
class|class
name|WalkSorterTest
block|{
static|static
block|{
name|KeyUtil
operator|.
name|setEncoderImpl
argument_list|(
operator|new
name|StandardKeyEncoder
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|field|userId
specifier|private
name|Account
operator|.
name|Id
name|userId
decl_stmt|;
DECL|field|repoManager
specifier|private
name|InMemoryRepositoryManager
name|repoManager
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|userId
operator|=
operator|new
name|Account
operator|.
name|Id
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|repoManager
operator|=
operator|new
name|InMemoryRepositoryManager
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|seriesOfChanges ()
specifier|public
name|void
name|seriesOfChanges
parameter_list|()
throws|throws
name|Exception
block|{
name|TestRepository
argument_list|<
name|Repo
argument_list|>
name|p
init|=
name|newRepo
argument_list|(
literal|"p"
argument_list|)
decl_stmt|;
name|RevCommit
name|c1_1
init|=
name|p
operator|.
name|commit
argument_list|()
operator|.
name|create
argument_list|()
decl_stmt|;
name|RevCommit
name|c2_1
init|=
name|p
operator|.
name|commit
argument_list|()
operator|.
name|parent
argument_list|(
name|c1_1
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|RevCommit
name|c3_1
init|=
name|p
operator|.
name|commit
argument_list|()
operator|.
name|parent
argument_list|(
name|c2_1
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|ChangeData
name|cd1
init|=
name|newChange
argument_list|(
name|p
argument_list|,
name|c1_1
argument_list|)
decl_stmt|;
name|ChangeData
name|cd2
init|=
name|newChange
argument_list|(
name|p
argument_list|,
name|c2_1
argument_list|)
decl_stmt|;
name|ChangeData
name|cd3
init|=
name|newChange
argument_list|(
name|p
argument_list|,
name|c3_1
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ChangeData
argument_list|>
name|changes
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|cd1
argument_list|,
name|cd2
argument_list|,
name|cd3
argument_list|)
decl_stmt|;
name|WalkSorter
name|sorter
init|=
operator|new
name|WalkSorter
argument_list|(
name|repoManager
argument_list|)
decl_stmt|;
name|assertSorted
argument_list|(
name|sorter
argument_list|,
name|changes
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|patchSetData
argument_list|(
name|cd3
argument_list|,
name|c3_1
argument_list|)
argument_list|,
name|patchSetData
argument_list|(
name|cd2
argument_list|,
name|c2_1
argument_list|)
argument_list|,
name|patchSetData
argument_list|(
name|cd1
argument_list|,
name|c1_1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Add new patch sets whose commits are in reverse order, so output is in
comment|// reverse order.
name|RevCommit
name|c3_2
init|=
name|p
operator|.
name|commit
argument_list|()
operator|.
name|create
argument_list|()
decl_stmt|;
name|RevCommit
name|c2_2
init|=
name|p
operator|.
name|commit
argument_list|()
operator|.
name|parent
argument_list|(
name|c3_2
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|RevCommit
name|c1_2
init|=
name|p
operator|.
name|commit
argument_list|()
operator|.
name|parent
argument_list|(
name|c2_2
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|addPatchSet
argument_list|(
name|cd1
argument_list|,
name|c1_2
argument_list|)
expr_stmt|;
name|addPatchSet
argument_list|(
name|cd2
argument_list|,
name|c2_2
argument_list|)
expr_stmt|;
name|addPatchSet
argument_list|(
name|cd3
argument_list|,
name|c3_2
argument_list|)
expr_stmt|;
name|assertSorted
argument_list|(
name|sorter
argument_list|,
name|changes
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|patchSetData
argument_list|(
name|cd1
argument_list|,
name|c1_2
argument_list|)
argument_list|,
name|patchSetData
argument_list|(
name|cd2
argument_list|,
name|c2_2
argument_list|)
argument_list|,
name|patchSetData
argument_list|(
name|cd3
argument_list|,
name|c3_2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|seriesOfChangesAtSameTimestamp ()
specifier|public
name|void
name|seriesOfChangesAtSameTimestamp
parameter_list|()
throws|throws
name|Exception
block|{
name|TestRepository
argument_list|<
name|Repo
argument_list|>
name|p
init|=
name|newRepo
argument_list|(
literal|"p"
argument_list|)
decl_stmt|;
name|RevCommit
name|c1
init|=
name|p
operator|.
name|commit
argument_list|()
operator|.
name|tick
argument_list|(
literal|0
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|RevCommit
name|c2
init|=
name|p
operator|.
name|commit
argument_list|()
operator|.
name|tick
argument_list|(
literal|0
argument_list|)
operator|.
name|parent
argument_list|(
name|c1
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|RevCommit
name|c3
init|=
name|p
operator|.
name|commit
argument_list|()
operator|.
name|tick
argument_list|(
literal|0
argument_list|)
operator|.
name|parent
argument_list|(
name|c2
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|RevCommit
name|c4
init|=
name|p
operator|.
name|commit
argument_list|()
operator|.
name|tick
argument_list|(
literal|0
argument_list|)
operator|.
name|parent
argument_list|(
name|c3
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|RevWalk
name|rw
init|=
name|p
operator|.
name|getRevWalk
argument_list|()
decl_stmt|;
name|rw
operator|.
name|parseCommit
argument_list|(
name|c1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|rw
operator|.
name|parseCommit
argument_list|(
name|c2
argument_list|)
operator|.
name|getCommitTime
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|c1
operator|.
name|getCommitTime
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|rw
operator|.
name|parseCommit
argument_list|(
name|c3
argument_list|)
operator|.
name|getCommitTime
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|c1
operator|.
name|getCommitTime
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|rw
operator|.
name|parseCommit
argument_list|(
name|c4
argument_list|)
operator|.
name|getCommitTime
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|c1
operator|.
name|getCommitTime
argument_list|()
argument_list|)
expr_stmt|;
name|ChangeData
name|cd1
init|=
name|newChange
argument_list|(
name|p
argument_list|,
name|c1
argument_list|)
decl_stmt|;
name|ChangeData
name|cd2
init|=
name|newChange
argument_list|(
name|p
argument_list|,
name|c2
argument_list|)
decl_stmt|;
name|ChangeData
name|cd3
init|=
name|newChange
argument_list|(
name|p
argument_list|,
name|c3
argument_list|)
decl_stmt|;
name|ChangeData
name|cd4
init|=
name|newChange
argument_list|(
name|p
argument_list|,
name|c4
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ChangeData
argument_list|>
name|changes
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|cd1
argument_list|,
name|cd2
argument_list|,
name|cd3
argument_list|,
name|cd4
argument_list|)
decl_stmt|;
name|WalkSorter
name|sorter
init|=
operator|new
name|WalkSorter
argument_list|(
name|repoManager
argument_list|)
decl_stmt|;
name|assertSorted
argument_list|(
name|sorter
argument_list|,
name|changes
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|patchSetData
argument_list|(
name|cd4
argument_list|,
name|c4
argument_list|)
argument_list|,
name|patchSetData
argument_list|(
name|cd3
argument_list|,
name|c3
argument_list|)
argument_list|,
name|patchSetData
argument_list|(
name|cd2
argument_list|,
name|c2
argument_list|)
argument_list|,
name|patchSetData
argument_list|(
name|cd1
argument_list|,
name|c1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|projectsSortedByName ()
specifier|public
name|void
name|projectsSortedByName
parameter_list|()
throws|throws
name|Exception
block|{
name|TestRepository
argument_list|<
name|Repo
argument_list|>
name|pa
init|=
name|newRepo
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|TestRepository
argument_list|<
name|Repo
argument_list|>
name|pb
init|=
name|newRepo
argument_list|(
literal|"b"
argument_list|)
decl_stmt|;
name|RevCommit
name|c1
init|=
name|pa
operator|.
name|commit
argument_list|()
operator|.
name|create
argument_list|()
decl_stmt|;
name|RevCommit
name|c2
init|=
name|pb
operator|.
name|commit
argument_list|()
operator|.
name|create
argument_list|()
decl_stmt|;
name|RevCommit
name|c3
init|=
name|pa
operator|.
name|commit
argument_list|()
operator|.
name|parent
argument_list|(
name|c1
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|RevCommit
name|c4
init|=
name|pb
operator|.
name|commit
argument_list|()
operator|.
name|parent
argument_list|(
name|c2
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|ChangeData
name|cd1
init|=
name|newChange
argument_list|(
name|pa
argument_list|,
name|c1
argument_list|)
decl_stmt|;
name|ChangeData
name|cd2
init|=
name|newChange
argument_list|(
name|pb
argument_list|,
name|c2
argument_list|)
decl_stmt|;
name|ChangeData
name|cd3
init|=
name|newChange
argument_list|(
name|pa
argument_list|,
name|c3
argument_list|)
decl_stmt|;
name|ChangeData
name|cd4
init|=
name|newChange
argument_list|(
name|pb
argument_list|,
name|c4
argument_list|)
decl_stmt|;
name|assertSorted
argument_list|(
operator|new
name|WalkSorter
argument_list|(
name|repoManager
argument_list|)
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|cd1
argument_list|,
name|cd2
argument_list|,
name|cd3
argument_list|,
name|cd4
argument_list|)
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|patchSetData
argument_list|(
name|cd3
argument_list|,
name|c3
argument_list|)
argument_list|,
name|patchSetData
argument_list|(
name|cd1
argument_list|,
name|c1
argument_list|)
argument_list|,
name|patchSetData
argument_list|(
name|cd4
argument_list|,
name|c4
argument_list|)
argument_list|,
name|patchSetData
argument_list|(
name|cd2
argument_list|,
name|c2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|restrictToPatchSets ()
specifier|public
name|void
name|restrictToPatchSets
parameter_list|()
throws|throws
name|Exception
block|{
name|TestRepository
argument_list|<
name|Repo
argument_list|>
name|p
init|=
name|newRepo
argument_list|(
literal|"p"
argument_list|)
decl_stmt|;
name|RevCommit
name|c1_1
init|=
name|p
operator|.
name|commit
argument_list|()
operator|.
name|create
argument_list|()
decl_stmt|;
name|RevCommit
name|c2_1
init|=
name|p
operator|.
name|commit
argument_list|()
operator|.
name|parent
argument_list|(
name|c1_1
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|ChangeData
name|cd1
init|=
name|newChange
argument_list|(
name|p
argument_list|,
name|c1_1
argument_list|)
decl_stmt|;
name|ChangeData
name|cd2
init|=
name|newChange
argument_list|(
name|p
argument_list|,
name|c2_1
argument_list|)
decl_stmt|;
comment|// Add new patch sets whose commits are in reverse order.
name|RevCommit
name|c2_2
init|=
name|p
operator|.
name|commit
argument_list|()
operator|.
name|create
argument_list|()
decl_stmt|;
name|RevCommit
name|c1_2
init|=
name|p
operator|.
name|commit
argument_list|()
operator|.
name|parent
argument_list|(
name|c2_2
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|addPatchSet
argument_list|(
name|cd1
argument_list|,
name|c1_2
argument_list|)
expr_stmt|;
name|addPatchSet
argument_list|(
name|cd2
argument_list|,
name|c2_2
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ChangeData
argument_list|>
name|changes
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|cd1
argument_list|,
name|cd2
argument_list|)
decl_stmt|;
name|WalkSorter
name|sorter
init|=
operator|new
name|WalkSorter
argument_list|(
name|repoManager
argument_list|)
decl_stmt|;
name|assertSorted
argument_list|(
name|sorter
argument_list|,
name|changes
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|patchSetData
argument_list|(
name|cd1
argument_list|,
name|c1_2
argument_list|)
argument_list|,
name|patchSetData
argument_list|(
name|cd2
argument_list|,
name|c2_2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// If we restrict to PS1 of each change, the sorter uses that commit.
name|sorter
operator|.
name|includePatchSets
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
operator|new
name|PatchSet
operator|.
name|Id
argument_list|(
name|cd1
operator|.
name|getId
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|,
operator|new
name|PatchSet
operator|.
name|Id
argument_list|(
name|cd2
operator|.
name|getId
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertSorted
argument_list|(
name|sorter
argument_list|,
name|changes
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|patchSetData
argument_list|(
name|cd2
argument_list|,
literal|1
argument_list|,
name|c2_1
argument_list|)
argument_list|,
name|patchSetData
argument_list|(
name|cd1
argument_list|,
literal|1
argument_list|,
name|c1_1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|restrictToPatchSetsOmittingWholeProject ()
specifier|public
name|void
name|restrictToPatchSetsOmittingWholeProject
parameter_list|()
throws|throws
name|Exception
block|{
name|TestRepository
argument_list|<
name|Repo
argument_list|>
name|pa
init|=
name|newRepo
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|TestRepository
argument_list|<
name|Repo
argument_list|>
name|pb
init|=
name|newRepo
argument_list|(
literal|"b"
argument_list|)
decl_stmt|;
name|RevCommit
name|c1
init|=
name|pa
operator|.
name|commit
argument_list|()
operator|.
name|create
argument_list|()
decl_stmt|;
name|RevCommit
name|c2
init|=
name|pa
operator|.
name|commit
argument_list|()
operator|.
name|create
argument_list|()
decl_stmt|;
name|ChangeData
name|cd1
init|=
name|newChange
argument_list|(
name|pa
argument_list|,
name|c1
argument_list|)
decl_stmt|;
name|ChangeData
name|cd2
init|=
name|newChange
argument_list|(
name|pb
argument_list|,
name|c2
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ChangeData
argument_list|>
name|changes
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|cd1
argument_list|,
name|cd2
argument_list|)
decl_stmt|;
name|WalkSorter
name|sorter
init|=
operator|new
name|WalkSorter
argument_list|(
name|repoManager
argument_list|)
operator|.
name|includePatchSets
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|cd1
operator|.
name|currentPatchSet
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertSorted
argument_list|(
name|sorter
argument_list|,
name|changes
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|patchSetData
argument_list|(
name|cd1
argument_list|,
name|c1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|retainBody ()
specifier|public
name|void
name|retainBody
parameter_list|()
throws|throws
name|Exception
block|{
name|TestRepository
argument_list|<
name|Repo
argument_list|>
name|p
init|=
name|newRepo
argument_list|(
literal|"p"
argument_list|)
decl_stmt|;
name|RevCommit
name|c
init|=
name|p
operator|.
name|commit
argument_list|()
operator|.
name|message
argument_list|(
literal|"message"
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|ChangeData
name|cd
init|=
name|newChange
argument_list|(
name|p
argument_list|,
name|c
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ChangeData
argument_list|>
name|changes
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|cd
argument_list|)
decl_stmt|;
name|RevCommit
name|actual
init|=
operator|new
name|WalkSorter
argument_list|(
name|repoManager
argument_list|)
operator|.
name|setRetainBody
argument_list|(
literal|true
argument_list|)
operator|.
name|sort
argument_list|(
name|changes
argument_list|)
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|commit
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|actual
operator|.
name|getRawBuffer
argument_list|()
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|actual
operator|.
name|getShortMessage
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"message"
argument_list|)
expr_stmt|;
name|actual
operator|=
operator|new
name|WalkSorter
argument_list|(
name|repoManager
argument_list|)
operator|.
name|setRetainBody
argument_list|(
literal|false
argument_list|)
operator|.
name|sort
argument_list|(
name|changes
argument_list|)
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|actual
operator|.
name|getRawBuffer
argument_list|()
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
block|}
DECL|method|newChange (TestRepository<Repo> tr, ObjectId id)
specifier|private
name|ChangeData
name|newChange
parameter_list|(
name|TestRepository
argument_list|<
name|Repo
argument_list|>
name|tr
parameter_list|,
name|ObjectId
name|id
parameter_list|)
throws|throws
name|Exception
block|{
name|Project
operator|.
name|NameKey
name|project
init|=
name|tr
operator|.
name|getRepository
argument_list|()
operator|.
name|getDescription
argument_list|()
operator|.
name|getProject
argument_list|()
decl_stmt|;
name|Change
name|c
init|=
name|TestChanges
operator|.
name|newChange
argument_list|(
name|project
argument_list|,
name|userId
argument_list|)
decl_stmt|;
name|ChangeData
name|cd
init|=
name|ChangeData
operator|.
name|createForTest
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|cd
operator|.
name|setChange
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|cd
operator|.
name|currentPatchSet
argument_list|()
operator|.
name|setRevision
argument_list|(
operator|new
name|RevId
argument_list|(
name|id
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|cd
operator|.
name|setPatchSets
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|cd
operator|.
name|currentPatchSet
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|cd
return|;
block|}
DECL|method|addPatchSet (ChangeData cd, ObjectId id)
specifier|private
name|PatchSet
name|addPatchSet
parameter_list|(
name|ChangeData
name|cd
parameter_list|,
name|ObjectId
name|id
parameter_list|)
throws|throws
name|Exception
block|{
name|TestChanges
operator|.
name|incrementPatchSet
argument_list|(
name|cd
operator|.
name|change
argument_list|()
argument_list|)
expr_stmt|;
name|PatchSet
name|ps
init|=
operator|new
name|PatchSet
argument_list|(
name|cd
operator|.
name|change
argument_list|()
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
decl_stmt|;
name|ps
operator|.
name|setRevision
argument_list|(
operator|new
name|RevId
argument_list|(
name|id
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|PatchSet
argument_list|>
name|patchSets
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|cd
operator|.
name|patchSets
argument_list|()
argument_list|)
decl_stmt|;
name|patchSets
operator|.
name|add
argument_list|(
name|ps
argument_list|)
expr_stmt|;
name|cd
operator|.
name|setPatchSets
argument_list|(
name|patchSets
argument_list|)
expr_stmt|;
return|return
name|ps
return|;
block|}
DECL|method|newRepo (String name)
specifier|private
name|TestRepository
argument_list|<
name|Repo
argument_list|>
name|newRepo
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|new
name|TestRepository
argument_list|<>
argument_list|(
name|repoManager
operator|.
name|createRepository
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
name|name
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
DECL|method|patchSetData (ChangeData cd, RevCommit commit)
specifier|private
specifier|static
name|PatchSetData
name|patchSetData
parameter_list|(
name|ChangeData
name|cd
parameter_list|,
name|RevCommit
name|commit
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|PatchSetData
operator|.
name|create
argument_list|(
name|cd
argument_list|,
name|cd
operator|.
name|currentPatchSet
argument_list|()
argument_list|,
name|commit
argument_list|)
return|;
block|}
DECL|method|patchSetData (ChangeData cd, int psId, RevCommit commit)
specifier|private
specifier|static
name|PatchSetData
name|patchSetData
parameter_list|(
name|ChangeData
name|cd
parameter_list|,
name|int
name|psId
parameter_list|,
name|RevCommit
name|commit
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|PatchSetData
operator|.
name|create
argument_list|(
name|cd
argument_list|,
name|cd
operator|.
name|patchSet
argument_list|(
operator|new
name|PatchSet
operator|.
name|Id
argument_list|(
name|cd
operator|.
name|getId
argument_list|()
argument_list|,
name|psId
argument_list|)
argument_list|)
argument_list|,
name|commit
argument_list|)
return|;
block|}
DECL|method|assertSorted (WalkSorter sorter, List<ChangeData> changes, List<PatchSetData> expected)
specifier|private
specifier|static
name|void
name|assertSorted
parameter_list|(
name|WalkSorter
name|sorter
parameter_list|,
name|List
argument_list|<
name|ChangeData
argument_list|>
name|changes
parameter_list|,
name|List
argument_list|<
name|PatchSetData
argument_list|>
name|expected
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|List
argument_list|<
name|ChangeData
argument_list|>
name|list
range|:
name|permutations
argument_list|(
name|changes
argument_list|)
control|)
block|{
name|assertThat
argument_list|(
name|sorter
operator|.
name|sort
argument_list|(
name|list
argument_list|)
argument_list|)
operator|.
name|containsExactlyElementsIn
argument_list|(
name|expected
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

