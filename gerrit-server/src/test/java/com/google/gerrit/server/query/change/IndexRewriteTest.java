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
comment|// limitations under the License.package com.google.gerrit.server.git;
end_comment

begin_package
DECL|package|com.google.gerrit.server.query.change
package|package
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
package|;
end_package

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
name|server
operator|.
name|index
operator|.
name|ChangeIndex
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
name|index
operator|.
name|PredicateWrapper
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
name|OrPredicate
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
name|io
operator|.
name|IOException
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
DECL|class|DummyIndex
specifier|private
specifier|static
class|class
name|DummyIndex
implements|implements
name|ChangeIndex
block|{
annotation|@
name|Override
DECL|method|insert (ChangeData cd)
specifier|public
name|void
name|insert
parameter_list|(
name|ChangeData
name|cd
parameter_list|)
throws|throws
name|IOException
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
name|void
name|replace
parameter_list|(
name|ChangeData
name|cd
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getSource (Predicate<ChangeData> p)
specifier|public
name|ChangeDataSource
name|getSource
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
operator|new
name|Source
argument_list|()
return|;
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
block|}
DECL|field|index
specifier|private
name|DummyIndex
name|index
decl_stmt|;
DECL|field|queryBuilder
specifier|private
name|ChangeQueryBuilder
name|queryBuilder
decl_stmt|;
DECL|field|rewrite
specifier|private
name|IndexRewrite
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
argument_list|()
expr_stmt|;
name|queryBuilder
operator|=
operator|new
name|ChangeQueryBuilder
argument_list|(
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
literal|null
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|rewrite
operator|=
operator|new
name|IndexRewriteImpl
argument_list|(
name|index
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
name|wrap
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
literal|"branch:a"
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
name|wrap
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
literal|"branch:a OR branch:b"
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
literal|"branch:a file:b"
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
name|AndPredicate
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
name|in
operator|.
name|getChild
argument_list|(
literal|0
argument_list|)
argument_list|,
name|wrap
argument_list|(
name|in
operator|.
name|getChild
argument_list|(
literal|1
argument_list|)
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
name|wrap
argument_list|(
name|in
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
literal|"-branch:a (file:b OR file:c)"
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
name|AndPredicate
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
name|in
operator|.
name|getChild
argument_list|(
literal|0
argument_list|)
argument_list|,
name|wrap
argument_list|(
name|in
operator|.
name|getChild
argument_list|(
literal|1
argument_list|)
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
literal|"file:a OR branch:b OR file:c OR branch:d"
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
name|OrPredicate
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
argument_list|,
name|wrap
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
argument_list|)
argument_list|,
name|out
operator|.
name|getChildren
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testDuplicateSimpleNonIndexOnlyPredicates ()
specifier|public
name|void
name|testDuplicateSimpleNonIndexOnlyPredicates
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
literal|"status:new project:p file:a"
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
name|AndPredicate
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
literal|1
argument_list|)
argument_list|,
name|wrap
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
literal|"(status:new OR status:draft) project:p file:a"
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
name|AndPredicate
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
literal|1
argument_list|)
argument_list|,
name|wrap
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
literal|"(status:new OR file:a) project:p file:b"
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
name|AndPredicate
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
name|in
operator|.
name|getChild
argument_list|(
literal|1
argument_list|)
argument_list|,
name|wrap
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
argument_list|)
argument_list|,
name|out
operator|.
name|getChildren
argument_list|()
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
DECL|method|wrap (Predicate<ChangeData> p)
specifier|private
name|PredicateWrapper
name|wrap
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
operator|new
name|PredicateWrapper
argument_list|(
name|p
argument_list|,
name|index
argument_list|)
return|;
block|}
block|}
end_class

end_unit

