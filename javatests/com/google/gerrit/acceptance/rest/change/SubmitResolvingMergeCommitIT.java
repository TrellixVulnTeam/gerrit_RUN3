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
DECL|package|com.google.gerrit.acceptance.rest.change
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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|truth
operator|.
name|TruthJUnit
operator|.
name|assume
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
name|entities
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
name|extensions
operator|.
name|client
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
name|server
operator|.
name|permissions
operator|.
name|PermissionBackendException
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
name|server
operator|.
name|restapi
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
name|submit
operator|.
name|ChangeSet
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
name|submit
operator|.
name|MergeSuperSet
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
name|testing
operator|.
name|ConfigSuite
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

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|errors
operator|.
name|IncorrectObjectTypeException
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
name|errors
operator|.
name|MissingObjectException
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
name|internal
operator|.
name|storage
operator|.
name|dfs
operator|.
name|InMemoryRepository
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
name|Config
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
DECL|class|SubmitResolvingMergeCommitIT
specifier|public
class|class
name|SubmitResolvingMergeCommitIT
extends|extends
name|AbstractDaemonTest
block|{
DECL|field|mergeSuperSet
annotation|@
name|Inject
specifier|private
name|Provider
argument_list|<
name|MergeSuperSet
argument_list|>
name|mergeSuperSet
decl_stmt|;
DECL|field|submit
annotation|@
name|Inject
specifier|private
name|Submit
name|submit
decl_stmt|;
annotation|@
name|ConfigSuite
operator|.
name|Default
DECL|method|submitWholeTopicEnabled ()
specifier|public
specifier|static
name|Config
name|submitWholeTopicEnabled
parameter_list|()
block|{
return|return
name|submitWholeTopicEnabledConfig
argument_list|()
return|;
block|}
annotation|@
name|Test
DECL|method|resolvingMergeCommitAtEndOfChain ()
specifier|public
name|void
name|resolvingMergeCommitAtEndOfChain
parameter_list|()
throws|throws
name|Exception
block|{
comment|/*       A<- B<- C<------- D       ^                    ^       |                    |       E<- F<- G<- H<-- M*        G has a conflict with C and is resolved in M which is a merge       commit of H and D.     */
name|PushOneCommit
operator|.
name|Result
name|a
init|=
name|createChange
argument_list|(
literal|"A"
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|b
init|=
name|createChange
argument_list|(
literal|"B"
argument_list|,
literal|"new.txt"
argument_list|,
literal|"No conflict line"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|a
operator|.
name|getCommit
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|c
init|=
name|createChange
argument_list|(
literal|"C"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|b
operator|.
name|getCommit
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|d
init|=
name|createChange
argument_list|(
literal|"D"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|c
operator|.
name|getCommit
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|e
init|=
name|createChange
argument_list|(
literal|"E"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|a
operator|.
name|getCommit
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|f
init|=
name|createChange
argument_list|(
literal|"F"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|e
operator|.
name|getCommit
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|g
init|=
name|createChange
argument_list|(
literal|"G"
argument_list|,
literal|"new.txt"
argument_list|,
literal|"Conflicting line"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|f
operator|.
name|getCommit
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|h
init|=
name|createChange
argument_list|(
literal|"H"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|g
operator|.
name|getCommit
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|approve
argument_list|(
name|a
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|approve
argument_list|(
name|b
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|approve
argument_list|(
name|c
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|approve
argument_list|(
name|d
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|submit
argument_list|(
name|d
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|approve
argument_list|(
name|e
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|approve
argument_list|(
name|f
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|approve
argument_list|(
name|g
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|approve
argument_list|(
name|h
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertMergeable
argument_list|(
name|e
operator|.
name|getChange
argument_list|()
argument_list|)
expr_stmt|;
name|assertMergeable
argument_list|(
name|f
operator|.
name|getChange
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotMergeable
argument_list|(
name|g
operator|.
name|getChange
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotMergeable
argument_list|(
name|h
operator|.
name|getChange
argument_list|()
argument_list|)
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|m
init|=
name|createChange
argument_list|(
literal|"M"
argument_list|,
literal|"new.txt"
argument_list|,
literal|"Resolved conflict"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|d
operator|.
name|getCommit
argument_list|()
argument_list|,
name|h
operator|.
name|getCommit
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|approve
argument_list|(
name|m
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertChangeSetMergeable
argument_list|(
name|m
operator|.
name|getChange
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertMergeable
argument_list|(
name|m
operator|.
name|getChange
argument_list|()
argument_list|)
expr_stmt|;
name|submit
argument_list|(
name|m
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertMerged
argument_list|(
name|e
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertMerged
argument_list|(
name|f
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertMerged
argument_list|(
name|g
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertMerged
argument_list|(
name|h
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertMerged
argument_list|(
name|m
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|resolvingMergeCommitComingBeforeConflict ()
specifier|public
name|void
name|resolvingMergeCommitComingBeforeConflict
parameter_list|()
throws|throws
name|Exception
block|{
comment|/*       A<- B<- C<- D       ^    ^       |    |       E<- F*<- G        F is a merge commit of E and B and resolves any conflict.       However G is conflicting with C.     */
name|PushOneCommit
operator|.
name|Result
name|a
init|=
name|createChange
argument_list|(
literal|"A"
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|b
init|=
name|createChange
argument_list|(
literal|"B"
argument_list|,
literal|"new.txt"
argument_list|,
literal|"No conflict line"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|a
operator|.
name|getCommit
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|c
init|=
name|createChange
argument_list|(
literal|"C"
argument_list|,
literal|"new.txt"
argument_list|,
literal|"No conflict line #2"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|b
operator|.
name|getCommit
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|d
init|=
name|createChange
argument_list|(
literal|"D"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|c
operator|.
name|getCommit
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|e
init|=
name|createChange
argument_list|(
literal|"E"
argument_list|,
literal|"new.txt"
argument_list|,
literal|"Conflicting line"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|a
operator|.
name|getCommit
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|f
init|=
name|createChange
argument_list|(
literal|"F"
argument_list|,
literal|"new.txt"
argument_list|,
literal|"Resolved conflict"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|b
operator|.
name|getCommit
argument_list|()
argument_list|,
name|e
operator|.
name|getCommit
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|g
init|=
name|createChange
argument_list|(
literal|"G"
argument_list|,
literal|"new.txt"
argument_list|,
literal|"Conflicting line #2"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|f
operator|.
name|getCommit
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertMergeable
argument_list|(
name|e
operator|.
name|getChange
argument_list|()
argument_list|)
expr_stmt|;
name|approve
argument_list|(
name|a
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|approve
argument_list|(
name|b
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|submit
argument_list|(
name|b
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotMergeable
argument_list|(
name|e
operator|.
name|getChange
argument_list|()
argument_list|)
expr_stmt|;
name|assertMergeable
argument_list|(
name|f
operator|.
name|getChange
argument_list|()
argument_list|)
expr_stmt|;
name|assertMergeable
argument_list|(
name|g
operator|.
name|getChange
argument_list|()
argument_list|)
expr_stmt|;
name|approve
argument_list|(
name|c
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|approve
argument_list|(
name|d
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|submit
argument_list|(
name|d
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|approve
argument_list|(
name|e
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|approve
argument_list|(
name|f
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|approve
argument_list|(
name|g
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotMergeable
argument_list|(
name|g
operator|.
name|getChange
argument_list|()
argument_list|)
expr_stmt|;
name|assertChangeSetMergeable
argument_list|(
name|g
operator|.
name|getChange
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|resolvingMergeCommitWithTopics ()
specifier|public
name|void
name|resolvingMergeCommitWithTopics
parameter_list|()
throws|throws
name|Exception
block|{
comment|/*       Project1:         A<- B<-- C<---         ^    ^          |         |    |          |         E<- F*<- G<- L*        G clashes with C, and F resolves the clashes between E and B.       Later, L resolves the clashes between C and G.        Project2:         H<- I         ^    ^         |    |         J<- K*        J clashes with I, and K resolves all problems.       G, K and L are in the same topic.     */
name|assume
argument_list|()
operator|.
name|that
argument_list|(
name|isSubmitWholeTopicEnabled
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|String
name|project1Name
init|=
name|name
argument_list|(
literal|"Project1"
argument_list|)
decl_stmt|;
name|String
name|project2Name
init|=
name|name
argument_list|(
literal|"Project2"
argument_list|)
decl_stmt|;
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|create
argument_list|(
name|project1Name
argument_list|)
expr_stmt|;
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|create
argument_list|(
name|project2Name
argument_list|)
expr_stmt|;
name|TestRepository
argument_list|<
name|InMemoryRepository
argument_list|>
name|project1
init|=
name|cloneProject
argument_list|(
name|Project
operator|.
name|nameKey
argument_list|(
name|project1Name
argument_list|)
argument_list|)
decl_stmt|;
name|TestRepository
argument_list|<
name|InMemoryRepository
argument_list|>
name|project2
init|=
name|cloneProject
argument_list|(
name|Project
operator|.
name|nameKey
argument_list|(
name|project2Name
argument_list|)
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|a
init|=
name|createChange
argument_list|(
name|project1
argument_list|,
literal|"A"
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|b
init|=
name|createChange
argument_list|(
name|project1
argument_list|,
literal|"B"
argument_list|,
literal|"new.txt"
argument_list|,
literal|"No conflict line"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|a
operator|.
name|getCommit
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|c
init|=
name|createChange
argument_list|(
name|project1
argument_list|,
literal|"C"
argument_list|,
literal|"new.txt"
argument_list|,
literal|"No conflict line #2"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|b
operator|.
name|getCommit
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|approve
argument_list|(
name|a
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|approve
argument_list|(
name|b
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|approve
argument_list|(
name|c
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|submit
argument_list|(
name|c
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|e
init|=
name|createChange
argument_list|(
name|project1
argument_list|,
literal|"E"
argument_list|,
literal|"new.txt"
argument_list|,
literal|"Conflicting line"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|a
operator|.
name|getCommit
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|f
init|=
name|createChange
argument_list|(
name|project1
argument_list|,
literal|"F"
argument_list|,
literal|"new.txt"
argument_list|,
literal|"Resolved conflict"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|b
operator|.
name|getCommit
argument_list|()
argument_list|,
name|e
operator|.
name|getCommit
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|g
init|=
name|createChange
argument_list|(
name|project1
argument_list|,
literal|"G"
argument_list|,
literal|"new.txt"
argument_list|,
literal|"Conflicting line #2"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|f
operator|.
name|getCommit
argument_list|()
argument_list|)
argument_list|,
literal|"refs/for/master/"
operator|+
name|name
argument_list|(
literal|"topic1"
argument_list|)
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|h
init|=
name|createChange
argument_list|(
name|project2
argument_list|,
literal|"H"
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|i
init|=
name|createChange
argument_list|(
name|project2
argument_list|,
literal|"I"
argument_list|,
literal|"new.txt"
argument_list|,
literal|"No conflict line"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|h
operator|.
name|getCommit
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|j
init|=
name|createChange
argument_list|(
name|project2
argument_list|,
literal|"J"
argument_list|,
literal|"new.txt"
argument_list|,
literal|"Conflicting line"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|h
operator|.
name|getCommit
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|k
init|=
name|createChange
argument_list|(
name|project2
argument_list|,
literal|"K"
argument_list|,
literal|"new.txt"
argument_list|,
literal|"Sadly conflicting topic-wise"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|i
operator|.
name|getCommit
argument_list|()
argument_list|,
name|j
operator|.
name|getCommit
argument_list|()
argument_list|)
argument_list|,
literal|"refs/for/master/"
operator|+
name|name
argument_list|(
literal|"topic1"
argument_list|)
argument_list|)
decl_stmt|;
name|approve
argument_list|(
name|h
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|approve
argument_list|(
name|i
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|submit
argument_list|(
name|i
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|approve
argument_list|(
name|e
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|approve
argument_list|(
name|f
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|approve
argument_list|(
name|g
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|approve
argument_list|(
name|j
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|approve
argument_list|(
name|k
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertChangeSetMergeable
argument_list|(
name|g
operator|.
name|getChange
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertChangeSetMergeable
argument_list|(
name|k
operator|.
name|getChange
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|l
init|=
name|createChange
argument_list|(
name|project1
argument_list|,
literal|"L"
argument_list|,
literal|"new.txt"
argument_list|,
literal|"Resolving conflicts again"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|c
operator|.
name|getCommit
argument_list|()
argument_list|,
name|g
operator|.
name|getCommit
argument_list|()
argument_list|)
argument_list|,
literal|"refs/for/master/"
operator|+
name|name
argument_list|(
literal|"topic1"
argument_list|)
argument_list|)
decl_stmt|;
name|approve
argument_list|(
name|l
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertChangeSetMergeable
argument_list|(
name|l
operator|.
name|getChange
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|submit
argument_list|(
name|l
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertMerged
argument_list|(
name|c
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertMerged
argument_list|(
name|g
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertMerged
argument_list|(
name|k
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|resolvingMergeCommitAtEndOfChainAndNotUpToDate ()
specifier|public
name|void
name|resolvingMergeCommitAtEndOfChainAndNotUpToDate
parameter_list|()
throws|throws
name|Exception
block|{
comment|/*         A<-- B          \           C<- D            \   /              E          B is the target branch, and D should be merged with B, but one         of C conflicts with B     */
name|PushOneCommit
operator|.
name|Result
name|a
init|=
name|createChange
argument_list|(
literal|"A"
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|b
init|=
name|createChange
argument_list|(
literal|"B"
argument_list|,
literal|"new.txt"
argument_list|,
literal|"No conflict line"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|a
operator|.
name|getCommit
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|approve
argument_list|(
name|a
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|approve
argument_list|(
name|b
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|submit
argument_list|(
name|b
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|c
init|=
name|createChange
argument_list|(
literal|"C"
argument_list|,
literal|"new.txt"
argument_list|,
literal|"Create conflicts"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|a
operator|.
name|getCommit
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|e
init|=
name|createChange
argument_list|(
literal|"E"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|c
operator|.
name|getCommit
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|d
init|=
name|createChange
argument_list|(
literal|"D"
argument_list|,
literal|"new.txt"
argument_list|,
literal|"Resolves conflicts"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|c
operator|.
name|getCommit
argument_list|()
argument_list|,
name|e
operator|.
name|getCommit
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|approve
argument_list|(
name|c
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|approve
argument_list|(
name|e
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|approve
argument_list|(
name|d
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotMergeable
argument_list|(
name|d
operator|.
name|getChange
argument_list|()
argument_list|)
expr_stmt|;
name|assertChangeSetMergeable
argument_list|(
name|d
operator|.
name|getChange
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|submit (String changeId)
specifier|private
name|void
name|submit
parameter_list|(
name|String
name|changeId
parameter_list|)
throws|throws
name|Exception
block|{
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
name|current
argument_list|()
operator|.
name|submit
argument_list|()
expr_stmt|;
block|}
DECL|method|assertChangeSetMergeable (ChangeData change, boolean expected)
specifier|private
name|void
name|assertChangeSetMergeable
parameter_list|(
name|ChangeData
name|change
parameter_list|,
name|boolean
name|expected
parameter_list|)
throws|throws
name|MissingObjectException
throws|,
name|IncorrectObjectTypeException
throws|,
name|IOException
throws|,
name|PermissionBackendException
block|{
name|ChangeSet
name|cs
init|=
name|mergeSuperSet
operator|.
name|get
argument_list|()
operator|.
name|completeChangeSet
argument_list|(
name|change
operator|.
name|change
argument_list|()
argument_list|,
name|user
argument_list|(
name|admin
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|submit
operator|.
name|unmergeableChanges
argument_list|(
name|cs
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
DECL|method|assertMergeable (ChangeData change)
specifier|private
name|void
name|assertMergeable
parameter_list|(
name|ChangeData
name|change
parameter_list|)
throws|throws
name|Exception
block|{
name|change
operator|.
name|setMergeable
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|change
operator|.
name|isMergeable
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
block|}
DECL|method|assertNotMergeable (ChangeData change)
specifier|private
name|void
name|assertNotMergeable
parameter_list|(
name|ChangeData
name|change
parameter_list|)
throws|throws
name|Exception
block|{
name|change
operator|.
name|setMergeable
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|change
operator|.
name|isMergeable
argument_list|()
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
block|}
DECL|method|assertMerged (String changeId)
specifier|private
name|void
name|assertMerged
parameter_list|(
name|String
name|changeId
parameter_list|)
throws|throws
name|Exception
block|{
name|assertThat
argument_list|(
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
operator|.
name|status
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|ChangeStatus
operator|.
name|MERGED
argument_list|)
expr_stmt|;
block|}
DECL|method|createChange ( TestRepository<?> repo, String subject, String fileName, String content, List<RevCommit> parents, String ref)
specifier|private
name|PushOneCommit
operator|.
name|Result
name|createChange
parameter_list|(
name|TestRepository
argument_list|<
name|?
argument_list|>
name|repo
parameter_list|,
name|String
name|subject
parameter_list|,
name|String
name|fileName
parameter_list|,
name|String
name|content
parameter_list|,
name|List
argument_list|<
name|RevCommit
argument_list|>
name|parents
parameter_list|,
name|String
name|ref
parameter_list|)
throws|throws
name|Exception
block|{
name|PushOneCommit
name|push
init|=
name|pushFactory
operator|.
name|create
argument_list|(
name|admin
operator|.
name|newIdent
argument_list|()
argument_list|,
name|repo
argument_list|,
name|subject
argument_list|,
name|fileName
argument_list|,
name|content
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|parents
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|push
operator|.
name|setParents
argument_list|(
name|parents
argument_list|)
expr_stmt|;
block|}
name|PushOneCommit
operator|.
name|Result
name|result
decl_stmt|;
if|if
condition|(
name|fileName
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|result
operator|=
name|push
operator|.
name|execute
argument_list|(
name|ref
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
name|push
operator|.
name|to
argument_list|(
name|ref
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|assertOkStatus
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|createChange (TestRepository<?> repo, String subject)
specifier|private
name|PushOneCommit
operator|.
name|Result
name|createChange
parameter_list|(
name|TestRepository
argument_list|<
name|?
argument_list|>
name|repo
parameter_list|,
name|String
name|subject
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|createChange
argument_list|(
name|repo
argument_list|,
name|subject
argument_list|,
literal|"x"
argument_list|,
literal|"x"
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|,
literal|"refs/for/master"
argument_list|)
return|;
block|}
DECL|method|createChange ( TestRepository<?> repo, String subject, String fileName, String content, List<RevCommit> parents)
specifier|private
name|PushOneCommit
operator|.
name|Result
name|createChange
parameter_list|(
name|TestRepository
argument_list|<
name|?
argument_list|>
name|repo
parameter_list|,
name|String
name|subject
parameter_list|,
name|String
name|fileName
parameter_list|,
name|String
name|content
parameter_list|,
name|List
argument_list|<
name|RevCommit
argument_list|>
name|parents
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|createChange
argument_list|(
name|repo
argument_list|,
name|subject
argument_list|,
name|fileName
argument_list|,
name|content
argument_list|,
name|parents
argument_list|,
literal|"refs/for/master"
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createChange (String subject)
specifier|protected
name|PushOneCommit
operator|.
name|Result
name|createChange
parameter_list|(
name|String
name|subject
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|createChange
argument_list|(
name|testRepo
argument_list|,
name|subject
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|,
literal|"refs/for/master"
argument_list|)
return|;
block|}
DECL|method|createChange (String subject, List<RevCommit> parents)
specifier|private
name|PushOneCommit
operator|.
name|Result
name|createChange
parameter_list|(
name|String
name|subject
parameter_list|,
name|List
argument_list|<
name|RevCommit
argument_list|>
name|parents
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|createChange
argument_list|(
name|testRepo
argument_list|,
name|subject
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|,
name|parents
argument_list|,
literal|"refs/for/master"
argument_list|)
return|;
block|}
DECL|method|createChange ( String subject, String fileName, String content, List<RevCommit> parents)
specifier|private
name|PushOneCommit
operator|.
name|Result
name|createChange
parameter_list|(
name|String
name|subject
parameter_list|,
name|String
name|fileName
parameter_list|,
name|String
name|content
parameter_list|,
name|List
argument_list|<
name|RevCommit
argument_list|>
name|parents
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|createChange
argument_list|(
name|testRepo
argument_list|,
name|subject
argument_list|,
name|fileName
argument_list|,
name|content
argument_list|,
name|parents
argument_list|,
literal|"refs/for/master"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

