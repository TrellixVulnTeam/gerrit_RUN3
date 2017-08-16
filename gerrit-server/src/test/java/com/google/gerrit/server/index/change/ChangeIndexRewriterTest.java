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
DECL|package|com.google.gerrit.server.index.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|index
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
name|gerrit
operator|.
name|common
operator|.
name|data
operator|.
name|GlobalCapability
operator|.
name|DEFAULT_MAX_QUERY_LIMIT
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
name|index
operator|.
name|query
operator|.
name|Predicate
operator|.
name|and
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
name|index
operator|.
name|query
operator|.
name|Predicate
operator|.
name|or
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
name|reviewdb
operator|.
name|client
operator|.
name|Change
operator|.
name|Status
operator|.
name|ABANDONED
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
name|reviewdb
operator|.
name|client
operator|.
name|Change
operator|.
name|Status
operator|.
name|DRAFT
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
name|reviewdb
operator|.
name|client
operator|.
name|Change
operator|.
name|Status
operator|.
name|MERGED
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
name|reviewdb
operator|.
name|client
operator|.
name|Change
operator|.
name|Status
operator|.
name|NEW
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
name|index
operator|.
name|change
operator|.
name|IndexedChangeQuery
operator|.
name|convertOptions
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
name|assertEquals
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
name|index
operator|.
name|IndexConfig
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
name|index
operator|.
name|QueryOptions
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
name|index
operator|.
name|query
operator|.
name|Predicate
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
name|index
operator|.
name|query
operator|.
name|QueryParseException
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
name|server
operator|.
name|query
operator|.
name|change
operator|.
name|AndChangeSource
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
name|query
operator|.
name|change
operator|.
name|ChangeQueryBuilder
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
name|ChangeStatusPredicate
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
name|OrSource
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
name|GerritBaseTests
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|Set
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
DECL|class|ChangeIndexRewriterTest
specifier|public
class|class
name|ChangeIndexRewriterTest
extends|extends
name|GerritBaseTests
block|{
DECL|field|CONFIG
specifier|private
specifier|static
specifier|final
name|IndexConfig
name|CONFIG
init|=
name|IndexConfig
operator|.
name|createDefault
argument_list|()
decl_stmt|;
DECL|field|index
specifier|private
name|FakeChangeIndex
name|index
decl_stmt|;
DECL|field|indexes
specifier|private
name|ChangeIndexCollection
name|indexes
decl_stmt|;
DECL|field|queryBuilder
specifier|private
name|ChangeQueryBuilder
name|queryBuilder
decl_stmt|;
DECL|field|rewrite
specifier|private
name|ChangeIndexRewriter
name|rewrite
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
name|index
operator|=
operator|new
name|FakeChangeIndex
argument_list|(
name|FakeChangeIndex
operator|.
name|V2
argument_list|)
expr_stmt|;
name|indexes
operator|=
operator|new
name|ChangeIndexCollection
argument_list|()
expr_stmt|;
name|indexes
operator|.
name|setSearchIndex
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|queryBuilder
operator|=
operator|new
name|FakeQueryBuilder
argument_list|(
name|indexes
argument_list|)
expr_stmt|;
name|rewrite
operator|=
operator|new
name|ChangeIndexRewriter
argument_list|(
name|indexes
argument_list|,
name|IndexConfig
operator|.
name|builder
argument_list|()
operator|.
name|maxTerms
argument_list|(
literal|3
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|indexPredicate ()
specifier|public
name|void
name|indexPredicate
parameter_list|()
throws|throws
name|Exception
block|{
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|in
init|=
name|parse
argument_list|(
literal|"file:a"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|rewrite
argument_list|(
name|in
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|query
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|nonIndexPredicate ()
specifier|public
name|void
name|nonIndexPredicate
parameter_list|()
throws|throws
name|Exception
block|{
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|in
init|=
name|parse
argument_list|(
literal|"foo:a"
argument_list|)
decl_stmt|;
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|out
init|=
name|rewrite
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|AndChangeSource
operator|.
name|class
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|out
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|getChildren
argument_list|()
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|query
argument_list|(
name|ChangeStatusPredicate
operator|.
name|open
argument_list|()
argument_list|)
argument_list|,
name|in
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|indexPredicates ()
specifier|public
name|void
name|indexPredicates
parameter_list|()
throws|throws
name|Exception
block|{
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|in
init|=
name|parse
argument_list|(
literal|"file:a file:b"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|rewrite
argument_list|(
name|in
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|query
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|nonIndexPredicates ()
specifier|public
name|void
name|nonIndexPredicates
parameter_list|()
throws|throws
name|Exception
block|{
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|in
init|=
name|parse
argument_list|(
literal|"foo:a OR foo:b"
argument_list|)
decl_stmt|;
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|out
init|=
name|rewrite
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|AndChangeSource
operator|.
name|class
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|out
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|getChildren
argument_list|()
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|query
argument_list|(
name|ChangeStatusPredicate
operator|.
name|open
argument_list|()
argument_list|)
argument_list|,
name|in
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|oneIndexPredicate ()
specifier|public
name|void
name|oneIndexPredicate
parameter_list|()
throws|throws
name|Exception
block|{
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|in
init|=
name|parse
argument_list|(
literal|"foo:a file:b"
argument_list|)
decl_stmt|;
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|out
init|=
name|rewrite
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|AndChangeSource
operator|.
name|class
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|out
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|getChildren
argument_list|()
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|query
argument_list|(
name|in
operator|.
name|getChild
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|,
name|in
operator|.
name|getChild
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|threeLevelTreeWithAllIndexPredicates ()
specifier|public
name|void
name|threeLevelTreeWithAllIndexPredicates
parameter_list|()
throws|throws
name|Exception
block|{
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|in
init|=
name|parse
argument_list|(
literal|"-status:abandoned (file:a OR file:b)"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|rewrite
operator|.
name|rewrite
argument_list|(
name|in
argument_list|,
name|options
argument_list|(
literal|0
argument_list|,
name|DEFAULT_MAX_QUERY_LIMIT
argument_list|)
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|query
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|threeLevelTreeWithSomeIndexPredicates ()
specifier|public
name|void
name|threeLevelTreeWithSomeIndexPredicates
parameter_list|()
throws|throws
name|Exception
block|{
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|in
init|=
name|parse
argument_list|(
literal|"-foo:a (file:b OR file:c)"
argument_list|)
decl_stmt|;
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|out
init|=
name|rewrite
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|getClass
argument_list|()
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|AndChangeSource
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|getChildren
argument_list|()
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|query
argument_list|(
name|in
operator|.
name|getChild
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|,
name|in
operator|.
name|getChild
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|multipleIndexPredicates ()
specifier|public
name|void
name|multipleIndexPredicates
parameter_list|()
throws|throws
name|Exception
block|{
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|in
init|=
name|parse
argument_list|(
literal|"file:a OR foo:b OR file:c OR foo:d"
argument_list|)
decl_stmt|;
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|out
init|=
name|rewrite
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|getClass
argument_list|()
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|OrSource
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|getChildren
argument_list|()
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|query
argument_list|(
name|or
argument_list|(
name|in
operator|.
name|getChild
argument_list|(
literal|0
argument_list|)
argument_list|,
name|in
operator|.
name|getChild
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|in
operator|.
name|getChild
argument_list|(
literal|1
argument_list|)
argument_list|,
name|in
operator|.
name|getChild
argument_list|(
literal|3
argument_list|)
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|indexAndNonIndexPredicates ()
specifier|public
name|void
name|indexAndNonIndexPredicates
parameter_list|()
throws|throws
name|Exception
block|{
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|in
init|=
name|parse
argument_list|(
literal|"status:new bar:p file:a"
argument_list|)
decl_stmt|;
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|out
init|=
name|rewrite
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|AndChangeSource
operator|.
name|class
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|out
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|getChildren
argument_list|()
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|query
argument_list|(
name|and
argument_list|(
name|in
operator|.
name|getChild
argument_list|(
literal|0
argument_list|)
argument_list|,
name|in
operator|.
name|getChild
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|in
operator|.
name|getChild
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|duplicateCompoundNonIndexOnlyPredicates ()
specifier|public
name|void
name|duplicateCompoundNonIndexOnlyPredicates
parameter_list|()
throws|throws
name|Exception
block|{
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|in
init|=
name|parse
argument_list|(
literal|"(status:new OR status:draft) bar:p file:a"
argument_list|)
decl_stmt|;
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|out
init|=
name|rewrite
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|getClass
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|AndChangeSource
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|getChildren
argument_list|()
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|query
argument_list|(
name|and
argument_list|(
name|in
operator|.
name|getChild
argument_list|(
literal|0
argument_list|)
argument_list|,
name|in
operator|.
name|getChild
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|in
operator|.
name|getChild
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|duplicateCompoundIndexOnlyPredicates ()
specifier|public
name|void
name|duplicateCompoundIndexOnlyPredicates
parameter_list|()
throws|throws
name|Exception
block|{
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|in
init|=
name|parse
argument_list|(
literal|"(status:new OR file:a) bar:p file:b"
argument_list|)
decl_stmt|;
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|out
init|=
name|rewrite
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|getClass
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|AndChangeSource
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|getChildren
argument_list|()
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|query
argument_list|(
name|and
argument_list|(
name|in
operator|.
name|getChild
argument_list|(
literal|0
argument_list|)
argument_list|,
name|in
operator|.
name|getChild
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|in
operator|.
name|getChild
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|optionsArgumentOverridesAllLimitPredicates ()
specifier|public
name|void
name|optionsArgumentOverridesAllLimitPredicates
parameter_list|()
throws|throws
name|Exception
block|{
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|in
init|=
name|parse
argument_list|(
literal|"limit:1 file:a limit:3"
argument_list|)
decl_stmt|;
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|out
init|=
name|rewrite
argument_list|(
name|in
argument_list|,
name|options
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|getClass
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|AndChangeSource
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|getChildren
argument_list|()
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|query
argument_list|(
name|in
operator|.
name|getChild
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|5
argument_list|)
argument_list|,
name|parse
argument_list|(
literal|"limit:5"
argument_list|)
argument_list|,
name|parse
argument_list|(
literal|"limit:5"
argument_list|)
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|startIncreasesLimitInQueryButNotPredicate ()
specifier|public
name|void
name|startIncreasesLimitInQueryButNotPredicate
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|n
init|=
literal|3
decl_stmt|;
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|f
init|=
name|parse
argument_list|(
literal|"file:a"
argument_list|)
decl_stmt|;
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|l
init|=
name|parse
argument_list|(
literal|"limit:"
operator|+
name|n
argument_list|)
decl_stmt|;
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|in
init|=
name|andSource
argument_list|(
name|f
argument_list|,
name|l
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|rewrite
operator|.
name|rewrite
argument_list|(
name|in
argument_list|,
name|options
argument_list|(
literal|0
argument_list|,
name|n
argument_list|)
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|andSource
argument_list|(
name|query
argument_list|(
name|f
argument_list|,
literal|3
argument_list|)
argument_list|,
name|l
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|rewrite
operator|.
name|rewrite
argument_list|(
name|in
argument_list|,
name|options
argument_list|(
literal|1
argument_list|,
name|n
argument_list|)
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|andSource
argument_list|(
name|query
argument_list|(
name|f
argument_list|,
literal|4
argument_list|)
argument_list|,
name|l
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|rewrite
operator|.
name|rewrite
argument_list|(
name|in
argument_list|,
name|options
argument_list|(
literal|2
argument_list|,
name|n
argument_list|)
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|andSource
argument_list|(
name|query
argument_list|(
name|f
argument_list|,
literal|5
argument_list|)
argument_list|,
name|l
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|getPossibleStatus ()
specifier|public
name|void
name|getPossibleStatus
parameter_list|()
throws|throws
name|Exception
block|{
name|assertThat
argument_list|(
name|status
argument_list|(
literal|"file:a"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|EnumSet
operator|.
name|allOf
argument_list|(
name|Change
operator|.
name|Status
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|status
argument_list|(
literal|"is:new"
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|NEW
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|status
argument_list|(
literal|"-is:new"
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|DRAFT
argument_list|,
name|MERGED
argument_list|,
name|ABANDONED
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|status
argument_list|(
literal|"is:new OR is:merged"
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|NEW
argument_list|,
name|MERGED
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|status
argument_list|(
literal|"is:new is:merged"
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|status
argument_list|(
literal|"(is:new is:draft) (is:merged)"
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|status
argument_list|(
literal|"(is:new is:draft) (is:merged)"
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|status
argument_list|(
literal|"(is:new is:draft) OR (is:merged)"
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|MERGED
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|unsupportedIndexOperator ()
specifier|public
name|void
name|unsupportedIndexOperator
parameter_list|()
throws|throws
name|Exception
block|{
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|in
init|=
name|parse
argument_list|(
literal|"status:merged file:a"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|rewrite
argument_list|(
name|in
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|query
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
name|indexes
operator|.
name|setSearchIndex
argument_list|(
operator|new
name|FakeChangeIndex
argument_list|(
name|FakeChangeIndex
operator|.
name|V1
argument_list|)
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|QueryParseException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"Unsupported index predicate: file:a"
argument_list|)
expr_stmt|;
name|rewrite
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|tooManyTerms ()
specifier|public
name|void
name|tooManyTerms
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|q
init|=
literal|"file:a OR file:b OR file:c"
decl_stmt|;
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|in
init|=
name|parse
argument_list|(
name|q
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|query
argument_list|(
name|in
argument_list|)
argument_list|,
name|rewrite
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|QueryParseException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"too many terms in query"
argument_list|)
expr_stmt|;
name|rewrite
argument_list|(
name|parse
argument_list|(
name|q
operator|+
literal|" OR file:d"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConvertOptions ()
specifier|public
name|void
name|testConvertOptions
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|options
argument_list|(
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|,
name|convertOptions
argument_list|(
name|options
argument_list|(
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|options
argument_list|(
literal|0
argument_list|,
literal|4
argument_list|)
argument_list|,
name|convertOptions
argument_list|(
name|options
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|options
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|)
argument_list|,
name|convertOptions
argument_list|(
name|options
argument_list|(
literal|2
argument_list|,
literal|3
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|addingStartToLimitDoesNotExceedBackendLimit ()
specifier|public
name|void
name|addingStartToLimitDoesNotExceedBackendLimit
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|max
init|=
name|CONFIG
operator|.
name|maxLimit
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|options
argument_list|(
literal|0
argument_list|,
name|max
argument_list|)
argument_list|,
name|convertOptions
argument_list|(
name|options
argument_list|(
literal|0
argument_list|,
name|max
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|options
argument_list|(
literal|0
argument_list|,
name|max
argument_list|)
argument_list|,
name|convertOptions
argument_list|(
name|options
argument_list|(
literal|1
argument_list|,
name|max
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|options
argument_list|(
literal|0
argument_list|,
name|max
argument_list|)
argument_list|,
name|convertOptions
argument_list|(
name|options
argument_list|(
literal|1
argument_list|,
name|max
operator|-
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|options
argument_list|(
literal|0
argument_list|,
name|max
argument_list|)
argument_list|,
name|convertOptions
argument_list|(
name|options
argument_list|(
literal|2
argument_list|,
name|max
operator|-
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|parse (String query)
specifier|private
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|parse
parameter_list|(
name|String
name|query
parameter_list|)
throws|throws
name|QueryParseException
block|{
return|return
name|queryBuilder
operator|.
name|parse
argument_list|(
name|query
argument_list|)
return|;
block|}
annotation|@
name|SafeVarargs
DECL|method|andSource (Predicate<ChangeData>.... preds)
specifier|private
specifier|static
name|AndChangeSource
name|andSource
parameter_list|(
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
modifier|...
name|preds
parameter_list|)
block|{
return|return
operator|new
name|AndChangeSource
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|preds
argument_list|)
argument_list|)
return|;
block|}
DECL|method|rewrite (Predicate<ChangeData> in)
specifier|private
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|rewrite
parameter_list|(
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|in
parameter_list|)
throws|throws
name|QueryParseException
block|{
return|return
name|rewrite
operator|.
name|rewrite
argument_list|(
name|in
argument_list|,
name|options
argument_list|(
literal|0
argument_list|,
name|DEFAULT_MAX_QUERY_LIMIT
argument_list|)
argument_list|)
return|;
block|}
DECL|method|rewrite (Predicate<ChangeData> in, QueryOptions opts)
specifier|private
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|rewrite
parameter_list|(
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|in
parameter_list|,
name|QueryOptions
name|opts
parameter_list|)
throws|throws
name|QueryParseException
block|{
return|return
name|rewrite
operator|.
name|rewrite
argument_list|(
name|in
argument_list|,
name|opts
argument_list|)
return|;
block|}
DECL|method|query (Predicate<ChangeData> p)
specifier|private
name|IndexedChangeQuery
name|query
parameter_list|(
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|p
parameter_list|)
throws|throws
name|QueryParseException
block|{
return|return
name|query
argument_list|(
name|p
argument_list|,
name|DEFAULT_MAX_QUERY_LIMIT
argument_list|)
return|;
block|}
DECL|method|query (Predicate<ChangeData> p, int limit)
specifier|private
name|IndexedChangeQuery
name|query
parameter_list|(
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|p
parameter_list|,
name|int
name|limit
parameter_list|)
throws|throws
name|QueryParseException
block|{
return|return
operator|new
name|IndexedChangeQuery
argument_list|(
name|index
argument_list|,
name|p
argument_list|,
name|options
argument_list|(
literal|0
argument_list|,
name|limit
argument_list|)
argument_list|)
return|;
block|}
DECL|method|options (int start, int limit)
specifier|private
specifier|static
name|QueryOptions
name|options
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|limit
parameter_list|)
block|{
return|return
name|IndexedChangeQuery
operator|.
name|createOptions
argument_list|(
name|CONFIG
argument_list|,
name|start
argument_list|,
name|limit
argument_list|,
name|ImmutableSet
operator|.
expr|<
name|String
operator|>
name|of
argument_list|()
argument_list|)
return|;
block|}
DECL|method|status (String query)
specifier|private
name|Set
argument_list|<
name|Change
operator|.
name|Status
argument_list|>
name|status
parameter_list|(
name|String
name|query
parameter_list|)
throws|throws
name|QueryParseException
block|{
return|return
name|ChangeIndexRewriter
operator|.
name|getPossibleStatus
argument_list|(
name|parse
argument_list|(
name|query
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

