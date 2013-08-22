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
DECL|package|com.google.gerrit.server.index
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
name|reviewdb
operator|.
name|client
operator|.
name|Change
operator|.
name|Status
operator|.
name|SUBMITTED
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
name|util
operator|.
name|concurrent
operator|.
name|ListenableFuture
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
name|AndPredicate
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
name|OperatorPredicate
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
name|server
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
name|server
operator|.
name|query
operator|.
name|RewritePredicate
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
name|AndSource
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
name|ChangeDataSource
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
name|server
operator|.
name|query
operator|.
name|change
operator|.
name|SqlRewriterImpl
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
name|gwtorm
operator|.
name|server
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|class|IndexRewriteTest
specifier|public
class|class
name|IndexRewriteTest
extends|extends
name|TestCase
block|{
DECL|field|V1
specifier|private
specifier|static
name|Schema
argument_list|<
name|ChangeData
argument_list|>
name|V1
init|=
operator|new
name|Schema
argument_list|<
name|ChangeData
argument_list|>
argument_list|(
literal|1
argument_list|,
literal|false
argument_list|,
name|ImmutableList
operator|.
expr|<
name|FieldDef
argument_list|<
name|ChangeData
argument_list|,
name|?
argument_list|>
operator|>
name|of
argument_list|(
name|ChangeField
operator|.
name|STATUS
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|V2
specifier|private
specifier|static
name|Schema
argument_list|<
name|ChangeData
argument_list|>
name|V2
init|=
operator|new
name|Schema
argument_list|<
name|ChangeData
argument_list|>
argument_list|(
literal|2
argument_list|,
literal|false
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|ChangeField
operator|.
name|STATUS
argument_list|,
name|ChangeField
operator|.
name|FILE
argument_list|)
argument_list|)
decl_stmt|;
DECL|class|DummyIndex
specifier|private
specifier|static
class|class
name|DummyIndex
implements|implements
name|ChangeIndex
block|{
DECL|field|schema
specifier|private
specifier|final
name|Schema
argument_list|<
name|ChangeData
argument_list|>
name|schema
decl_stmt|;
DECL|method|DummyIndex (Schema<ChangeData> schema)
specifier|private
name|DummyIndex
parameter_list|(
name|Schema
argument_list|<
name|ChangeData
argument_list|>
name|schema
parameter_list|)
block|{
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|insert (ChangeData cd)
specifier|public
name|ListenableFuture
argument_list|<
name|Void
argument_list|>
name|insert
parameter_list|(
name|ChangeData
name|cd
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|replace (ChangeData cd)
specifier|public
name|ListenableFuture
argument_list|<
name|Void
argument_list|>
name|replace
parameter_list|(
name|ChangeData
name|cd
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|delete (ChangeData cd)
specifier|public
name|ListenableFuture
argument_list|<
name|Void
argument_list|>
name|delete
parameter_list|(
name|ChangeData
name|cd
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|deleteAll ()
specifier|public
name|void
name|deleteAll
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getSource (Predicate<ChangeData> p, int limit)
specifier|public
name|ChangeDataSource
name|getSource
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
name|Source
argument_list|(
name|p
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSchema ()
specifier|public
name|Schema
argument_list|<
name|ChangeData
argument_list|>
name|getSchema
parameter_list|()
block|{
return|return
name|schema
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|markReady (boolean ready)
specifier|public
name|void
name|markReady
parameter_list|(
name|boolean
name|ready
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
DECL|class|Source
specifier|private
specifier|static
class|class
name|Source
implements|implements
name|ChangeDataSource
block|{
DECL|field|p
specifier|private
specifier|final
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|p
decl_stmt|;
DECL|method|Source (Predicate<ChangeData> p)
name|Source
parameter_list|(
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|p
parameter_list|)
block|{
name|this
operator|.
name|p
operator|=
name|p
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCardinality ()
specifier|public
name|int
name|getCardinality
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|hasChange ()
specifier|public
name|boolean
name|hasChange
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|read ()
specifier|public
name|ResultSet
argument_list|<
name|ChangeData
argument_list|>
name|read
parameter_list|()
throws|throws
name|OrmException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|p
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
DECL|class|QueryBuilder
specifier|public
class|class
name|QueryBuilder
extends|extends
name|ChangeQueryBuilder
block|{
DECL|method|QueryBuilder ()
name|QueryBuilder
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|QueryBuilder
operator|.
name|Definition
argument_list|<
name|ChangeData
argument_list|,
name|QueryBuilder
argument_list|>
argument_list|(
name|QueryBuilder
operator|.
name|class
argument_list|)
argument_list|,
operator|new
name|ChangeQueryBuilder
operator|.
name|Arguments
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|indexes
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Operator
DECL|method|foo (String value)
specifier|public
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|foo
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|predicate
argument_list|(
literal|"foo"
argument_list|,
name|value
argument_list|)
return|;
block|}
annotation|@
name|Operator
DECL|method|bar (String value)
specifier|public
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|bar
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|predicate
argument_list|(
literal|"bar"
argument_list|,
name|value
argument_list|)
return|;
block|}
DECL|method|predicate (String name, String value)
specifier|private
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|predicate
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
operator|new
name|OperatorPredicate
argument_list|<
name|ChangeData
argument_list|>
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|match
parameter_list|(
name|ChangeData
name|object
parameter_list|)
throws|throws
name|OrmException
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getCost
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
return|;
block|}
block|}
DECL|field|index
specifier|private
name|DummyIndex
name|index
decl_stmt|;
DECL|field|indexes
specifier|private
name|IndexCollection
name|indexes
decl_stmt|;
DECL|field|queryBuilder
specifier|private
name|ChangeQueryBuilder
name|queryBuilder
decl_stmt|;
DECL|field|rewrite
specifier|private
name|IndexRewriteImpl
name|rewrite
decl_stmt|;
annotation|@
name|Override
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
name|index
operator|=
operator|new
name|DummyIndex
argument_list|(
name|V2
argument_list|)
expr_stmt|;
name|indexes
operator|=
operator|new
name|IndexCollection
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
name|QueryBuilder
argument_list|()
expr_stmt|;
name|rewrite
operator|=
operator|new
name|IndexRewriteImpl
argument_list|(
name|indexes
argument_list|,
literal|null
argument_list|,
operator|new
name|IndexRewriteImpl
operator|.
name|BasicRewritesImpl
argument_list|(
literal|null
argument_list|)
argument_list|,
operator|new
name|SqlRewriterImpl
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIndexPredicate ()
specifier|public
name|void
name|testIndexPredicate
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
block|}
DECL|method|testNonIndexPredicate ()
specifier|public
name|void
name|testNonIndexPredicate
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
name|assertSame
argument_list|(
name|in
argument_list|,
name|rewrite
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIndexPredicates ()
specifier|public
name|void
name|testIndexPredicates
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
block|}
DECL|method|testNonIndexPredicates ()
specifier|public
name|void
name|testNonIndexPredicates
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
name|assertEquals
argument_list|(
name|in
argument_list|,
name|rewrite
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testOneIndexPredicate ()
specifier|public
name|void
name|testOneIndexPredicate
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
name|assertSame
argument_list|(
name|AndSource
operator|.
name|class
argument_list|,
name|out
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableList
operator|.
name|of
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
argument_list|,
name|out
operator|.
name|getChildren
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testThreeLevelTreeWithAllIndexPredicates ()
specifier|public
name|void
name|testThreeLevelTreeWithAllIndexPredicates
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
literal|"-status:abandoned (status:open OR status:merged)"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|query
argument_list|(
name|parse
argument_list|(
literal|"status:new OR status:submitted OR status:draft OR status:merged"
argument_list|)
argument_list|)
argument_list|,
name|rewrite
operator|.
name|rewrite
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testThreeLevelTreeWithSomeIndexPredicates ()
specifier|public
name|void
name|testThreeLevelTreeWithSomeIndexPredicates
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
name|assertEquals
argument_list|(
name|AndSource
operator|.
name|class
argument_list|,
name|out
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableList
operator|.
name|of
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
argument_list|,
name|out
operator|.
name|getChildren
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultipleIndexPredicates ()
specifier|public
name|void
name|testMultipleIndexPredicates
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
name|assertSame
argument_list|(
name|OrSource
operator|.
name|class
argument_list|,
name|out
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|query
argument_list|(
name|Predicate
operator|.
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
argument_list|,
name|out
operator|.
name|getChildren
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testIndexAndNonIndexPredicates ()
specifier|public
name|void
name|testIndexAndNonIndexPredicates
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
name|assertSame
argument_list|(
name|AndSource
operator|.
name|class
argument_list|,
name|out
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|query
argument_list|(
name|Predicate
operator|.
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
argument_list|,
name|out
operator|.
name|getChildren
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testDuplicateCompoundNonIndexOnlyPredicates ()
specifier|public
name|void
name|testDuplicateCompoundNonIndexOnlyPredicates
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
name|assertSame
argument_list|(
name|AndSource
operator|.
name|class
argument_list|,
name|out
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|query
argument_list|(
name|Predicate
operator|.
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
argument_list|,
name|out
operator|.
name|getChildren
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testDuplicateCompoundIndexOnlyPredicates ()
specifier|public
name|void
name|testDuplicateCompoundIndexOnlyPredicates
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
name|assertSame
argument_list|(
name|AndSource
operator|.
name|class
argument_list|,
name|out
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|query
argument_list|(
name|Predicate
operator|.
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
argument_list|,
name|out
operator|.
name|getChildren
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testLimit ()
specifier|public
name|void
name|testLimit
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
literal|"file:a limit:3"
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
name|assertSame
argument_list|(
name|AndSource
operator|.
name|class
argument_list|,
name|out
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|query
argument_list|(
name|in
operator|.
name|getChild
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|4
argument_list|)
argument_list|,
name|in
operator|.
name|getChild
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|,
name|out
operator|.
name|getChildren
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testGetPossibleStatus ()
specifier|public
name|void
name|testGetPossibleStatus
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
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
argument_list|,
name|status
argument_list|(
literal|"file:a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|EnumSet
operator|.
name|of
argument_list|(
name|NEW
argument_list|)
argument_list|,
name|status
argument_list|(
literal|"is:new"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|EnumSet
operator|.
name|of
argument_list|(
name|SUBMITTED
argument_list|,
name|DRAFT
argument_list|,
name|MERGED
argument_list|,
name|ABANDONED
argument_list|)
argument_list|,
name|status
argument_list|(
literal|"-is:new"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|EnumSet
operator|.
name|of
argument_list|(
name|NEW
argument_list|,
name|MERGED
argument_list|)
argument_list|,
name|status
argument_list|(
literal|"is:new OR is:merged"
argument_list|)
argument_list|)
expr_stmt|;
name|EnumSet
argument_list|<
name|Change
operator|.
name|Status
argument_list|>
name|none
init|=
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|Change
operator|.
name|Status
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|none
argument_list|,
name|status
argument_list|(
literal|"is:new is:merged"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|none
argument_list|,
name|status
argument_list|(
literal|"(is:new is:draft) (is:merged is:submitted)"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|none
argument_list|,
name|status
argument_list|(
literal|"(is:new is:draft) (is:merged is:submitted)"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|EnumSet
operator|.
name|of
argument_list|(
name|MERGED
argument_list|,
name|SUBMITTED
argument_list|)
argument_list|,
name|status
argument_list|(
literal|"(is:new is:draft) OR (is:merged OR is:submitted)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testUnsupportedIndexOperator ()
specifier|public
name|void
name|testUnsupportedIndexOperator
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
name|indexes
operator|.
name|setSearchIndex
argument_list|(
operator|new
name|DummyIndex
argument_list|(
name|V1
argument_list|)
argument_list|)
expr_stmt|;
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
name|assertTrue
argument_list|(
name|out
operator|instanceof
name|AndPredicate
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|query
argument_list|(
name|in
operator|.
name|getChild
argument_list|(
literal|0
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
argument_list|,
name|out
operator|.
name|getChildren
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoChangeIndexUsesSqlRewrites ()
specifier|public
name|void
name|testNoChangeIndexUsesSqlRewrites
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
literal|"status:open project:p ref:b"
argument_list|)
decl_stmt|;
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|out
decl_stmt|;
name|out
operator|=
name|rewrite
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|out
operator|instanceof
name|AndPredicate
operator|||
name|out
operator|instanceof
name|IndexedChangeQuery
argument_list|)
expr_stmt|;
name|indexes
operator|.
name|setSearchIndex
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|out
operator|=
name|rewrite
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|out
operator|instanceof
name|RewritePredicate
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
name|IndexRewriteImpl
operator|.
name|MAX_LIMIT
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
name|limit
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
name|IndexRewriteImpl
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

