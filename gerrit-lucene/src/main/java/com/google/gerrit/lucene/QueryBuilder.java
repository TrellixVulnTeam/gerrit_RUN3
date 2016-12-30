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
DECL|package|com.google.gerrit.lucene
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|lucene
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
name|base
operator|.
name|Preconditions
operator|.
name|checkArgument
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
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
name|server
operator|.
name|index
operator|.
name|FieldType
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
name|IndexPredicate
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
name|IntegerRangePredicate
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
name|RegexPredicate
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
name|Schema
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
name|TimestampRangePredicate
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
name|NotPredicate
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
name|PostFilterPredicate
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
name|java
operator|.
name|util
operator|.
name|Date
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
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Term
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|BooleanQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|MatchAllDocsQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|NumericRangeQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|PrefixQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Query
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|RegexpQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|TermQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BytesRefBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|NumericUtils
import|;
end_import

begin_class
DECL|class|QueryBuilder
specifier|public
class|class
name|QueryBuilder
parameter_list|<
name|V
parameter_list|>
block|{
DECL|method|intTerm (String name, int value)
specifier|static
name|Term
name|intTerm
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|value
parameter_list|)
block|{
name|BytesRefBuilder
name|builder
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
name|NumericUtils
operator|.
name|intToPrefixCoded
argument_list|(
name|value
argument_list|,
literal|0
argument_list|,
name|builder
argument_list|)
expr_stmt|;
return|return
operator|new
name|Term
argument_list|(
name|name
argument_list|,
name|builder
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
DECL|method|stringTerm (String name, String value)
specifier|static
name|Term
name|stringTerm
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|BytesRefBuilder
name|builder
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|value
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|,
literal|0
argument_list|,
name|value
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|Term
argument_list|(
name|name
argument_list|,
name|builder
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
DECL|field|schema
specifier|private
specifier|final
name|Schema
argument_list|<
name|V
argument_list|>
name|schema
decl_stmt|;
DECL|field|queryBuilder
specifier|private
specifier|final
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|QueryBuilder
name|queryBuilder
decl_stmt|;
DECL|method|QueryBuilder (Schema<V> schema, Analyzer analyzer)
specifier|public
name|QueryBuilder
parameter_list|(
name|Schema
argument_list|<
name|V
argument_list|>
name|schema
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|queryBuilder
operator|=
operator|new
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|QueryBuilder
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
block|}
DECL|method|toQuery (Predicate<V> p)
specifier|public
name|Query
name|toQuery
parameter_list|(
name|Predicate
argument_list|<
name|V
argument_list|>
name|p
parameter_list|)
throws|throws
name|QueryParseException
block|{
if|if
condition|(
name|p
operator|instanceof
name|AndPredicate
condition|)
block|{
return|return
name|and
argument_list|(
name|p
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|p
operator|instanceof
name|OrPredicate
condition|)
block|{
return|return
name|or
argument_list|(
name|p
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|p
operator|instanceof
name|NotPredicate
condition|)
block|{
return|return
name|not
argument_list|(
name|p
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|p
operator|instanceof
name|IndexPredicate
condition|)
block|{
return|return
name|fieldQuery
argument_list|(
operator|(
name|IndexPredicate
argument_list|<
name|V
argument_list|>
operator|)
name|p
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|p
operator|instanceof
name|PostFilterPredicate
condition|)
block|{
return|return
operator|new
name|MatchAllDocsQuery
argument_list|()
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|QueryParseException
argument_list|(
literal|"cannot create query for index: "
operator|+
name|p
argument_list|)
throw|;
block|}
block|}
DECL|method|or (Predicate<V> p)
specifier|private
name|Query
name|or
parameter_list|(
name|Predicate
argument_list|<
name|V
argument_list|>
name|p
parameter_list|)
throws|throws
name|QueryParseException
block|{
try|try
block|{
name|BooleanQuery
operator|.
name|Builder
name|q
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|p
operator|.
name|getChildCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|q
operator|.
name|add
argument_list|(
name|toQuery
argument_list|(
name|p
operator|.
name|getChild
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|,
name|SHOULD
argument_list|)
expr_stmt|;
block|}
return|return
name|q
operator|.
name|build
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|BooleanQuery
operator|.
name|TooManyClauses
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|QueryParseException
argument_list|(
literal|"cannot create query for index: "
operator|+
name|p
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|and (Predicate<V> p)
specifier|private
name|Query
name|and
parameter_list|(
name|Predicate
argument_list|<
name|V
argument_list|>
name|p
parameter_list|)
throws|throws
name|QueryParseException
block|{
try|try
block|{
name|BooleanQuery
operator|.
name|Builder
name|b
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Query
argument_list|>
name|not
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|p
operator|.
name|getChildCount
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|p
operator|.
name|getChildCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Predicate
argument_list|<
name|V
argument_list|>
name|c
init|=
name|p
operator|.
name|getChild
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|instanceof
name|NotPredicate
condition|)
block|{
name|Predicate
argument_list|<
name|V
argument_list|>
name|n
init|=
name|c
operator|.
name|getChild
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|instanceof
name|TimestampRangePredicate
condition|)
block|{
name|b
operator|.
name|add
argument_list|(
name|notTimestamp
argument_list|(
operator|(
name|TimestampRangePredicate
argument_list|<
name|V
argument_list|>
operator|)
name|n
argument_list|)
argument_list|,
name|MUST
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|not
operator|.
name|add
argument_list|(
name|toQuery
argument_list|(
name|n
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|b
operator|.
name|add
argument_list|(
name|toQuery
argument_list|(
name|c
argument_list|)
argument_list|,
name|MUST
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Query
name|q
range|:
name|not
control|)
block|{
name|b
operator|.
name|add
argument_list|(
name|q
argument_list|,
name|MUST_NOT
argument_list|)
expr_stmt|;
block|}
return|return
name|b
operator|.
name|build
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|BooleanQuery
operator|.
name|TooManyClauses
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|QueryParseException
argument_list|(
literal|"cannot create query for index: "
operator|+
name|p
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|not (Predicate<V> p)
specifier|private
name|Query
name|not
parameter_list|(
name|Predicate
argument_list|<
name|V
argument_list|>
name|p
parameter_list|)
throws|throws
name|QueryParseException
block|{
name|Predicate
argument_list|<
name|V
argument_list|>
name|n
init|=
name|p
operator|.
name|getChild
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|instanceof
name|TimestampRangePredicate
condition|)
block|{
return|return
name|notTimestamp
argument_list|(
operator|(
name|TimestampRangePredicate
argument_list|<
name|V
argument_list|>
operator|)
name|n
argument_list|)
return|;
block|}
comment|// Lucene does not support negation, start with all and subtract.
return|return
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|MUST
argument_list|)
operator|.
name|add
argument_list|(
name|toQuery
argument_list|(
name|n
argument_list|)
argument_list|,
name|MUST_NOT
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|fieldQuery (IndexPredicate<V> p)
specifier|private
name|Query
name|fieldQuery
parameter_list|(
name|IndexPredicate
argument_list|<
name|V
argument_list|>
name|p
parameter_list|)
throws|throws
name|QueryParseException
block|{
name|checkArgument
argument_list|(
name|schema
operator|.
name|hasField
argument_list|(
name|p
operator|.
name|getField
argument_list|()
argument_list|)
argument_list|,
literal|"field not in schema v%s: %s"
argument_list|,
name|schema
operator|.
name|getVersion
argument_list|()
argument_list|,
name|p
operator|.
name|getField
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|p
operator|.
name|getType
argument_list|()
operator|==
name|FieldType
operator|.
name|INTEGER
condition|)
block|{
return|return
name|intQuery
argument_list|(
name|p
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|p
operator|.
name|getType
argument_list|()
operator|==
name|FieldType
operator|.
name|INTEGER_RANGE
condition|)
block|{
return|return
name|intRangeQuery
argument_list|(
name|p
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|p
operator|.
name|getType
argument_list|()
operator|==
name|FieldType
operator|.
name|TIMESTAMP
condition|)
block|{
return|return
name|timestampQuery
argument_list|(
name|p
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|p
operator|.
name|getType
argument_list|()
operator|==
name|FieldType
operator|.
name|EXACT
condition|)
block|{
return|return
name|exactQuery
argument_list|(
name|p
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|p
operator|.
name|getType
argument_list|()
operator|==
name|FieldType
operator|.
name|PREFIX
condition|)
block|{
return|return
name|prefixQuery
argument_list|(
name|p
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|p
operator|.
name|getType
argument_list|()
operator|==
name|FieldType
operator|.
name|FULL_TEXT
condition|)
block|{
return|return
name|fullTextQuery
argument_list|(
name|p
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
name|FieldType
operator|.
name|badFieldType
argument_list|(
name|p
operator|.
name|getType
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|method|intQuery (IndexPredicate<V> p)
specifier|private
name|Query
name|intQuery
parameter_list|(
name|IndexPredicate
argument_list|<
name|V
argument_list|>
name|p
parameter_list|)
throws|throws
name|QueryParseException
block|{
name|int
name|value
decl_stmt|;
try|try
block|{
comment|// Can't use IntPredicate because it and IndexPredicate are different
comment|// subclasses of OperatorPredicate.
name|value
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|p
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|QueryParseException
argument_list|(
literal|"not an integer: "
operator|+
name|p
operator|.
name|getValue
argument_list|()
argument_list|)
throw|;
block|}
return|return
operator|new
name|TermQuery
argument_list|(
name|intTerm
argument_list|(
name|p
operator|.
name|getField
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|value
argument_list|)
argument_list|)
return|;
block|}
DECL|method|intRangeQuery (IndexPredicate<V> p)
specifier|private
name|Query
name|intRangeQuery
parameter_list|(
name|IndexPredicate
argument_list|<
name|V
argument_list|>
name|p
parameter_list|)
throws|throws
name|QueryParseException
block|{
if|if
condition|(
name|p
operator|instanceof
name|IntegerRangePredicate
condition|)
block|{
name|IntegerRangePredicate
argument_list|<
name|V
argument_list|>
name|r
init|=
operator|(
name|IntegerRangePredicate
argument_list|<
name|V
argument_list|>
operator|)
name|p
decl_stmt|;
name|int
name|minimum
init|=
name|r
operator|.
name|getMinimumValue
argument_list|()
decl_stmt|;
name|int
name|maximum
init|=
name|r
operator|.
name|getMaximumValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|minimum
operator|==
name|maximum
condition|)
block|{
comment|// Just fall back to a standard integer query.
return|return
operator|new
name|TermQuery
argument_list|(
name|intTerm
argument_list|(
name|p
operator|.
name|getField
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|minimum
argument_list|)
argument_list|)
return|;
block|}
return|return
name|NumericRangeQuery
operator|.
name|newIntRange
argument_list|(
name|r
operator|.
name|getField
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|minimum
argument_list|,
name|maximum
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
return|;
block|}
throw|throw
operator|new
name|QueryParseException
argument_list|(
literal|"not an integer range: "
operator|+
name|p
argument_list|)
throw|;
block|}
DECL|method|timestampQuery (IndexPredicate<V> p)
specifier|private
name|Query
name|timestampQuery
parameter_list|(
name|IndexPredicate
argument_list|<
name|V
argument_list|>
name|p
parameter_list|)
throws|throws
name|QueryParseException
block|{
if|if
condition|(
name|p
operator|instanceof
name|TimestampRangePredicate
condition|)
block|{
name|TimestampRangePredicate
argument_list|<
name|V
argument_list|>
name|r
init|=
operator|(
name|TimestampRangePredicate
argument_list|<
name|V
argument_list|>
operator|)
name|p
decl_stmt|;
return|return
name|NumericRangeQuery
operator|.
name|newLongRange
argument_list|(
name|r
operator|.
name|getField
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|r
operator|.
name|getMinTimestamp
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|,
name|r
operator|.
name|getMaxTimestamp
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
return|;
block|}
throw|throw
operator|new
name|QueryParseException
argument_list|(
literal|"not a timestamp: "
operator|+
name|p
argument_list|)
throw|;
block|}
DECL|method|notTimestamp (TimestampRangePredicate<V> r)
specifier|private
name|Query
name|notTimestamp
parameter_list|(
name|TimestampRangePredicate
argument_list|<
name|V
argument_list|>
name|r
parameter_list|)
throws|throws
name|QueryParseException
block|{
if|if
condition|(
name|r
operator|.
name|getMinTimestamp
argument_list|()
operator|.
name|getTime
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|NumericRangeQuery
operator|.
name|newLongRange
argument_list|(
name|r
operator|.
name|getField
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|r
operator|.
name|getMaxTimestamp
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
return|;
block|}
throw|throw
operator|new
name|QueryParseException
argument_list|(
literal|"cannot negate: "
operator|+
name|r
argument_list|)
throw|;
block|}
DECL|method|exactQuery (IndexPredicate<V> p)
specifier|private
name|Query
name|exactQuery
parameter_list|(
name|IndexPredicate
argument_list|<
name|V
argument_list|>
name|p
parameter_list|)
block|{
if|if
condition|(
name|p
operator|instanceof
name|RegexPredicate
argument_list|<
name|?
argument_list|>
condition|)
block|{
return|return
name|regexQuery
argument_list|(
name|p
argument_list|)
return|;
block|}
return|return
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|p
operator|.
name|getField
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|p
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|regexQuery (IndexPredicate<V> p)
specifier|private
name|Query
name|regexQuery
parameter_list|(
name|IndexPredicate
argument_list|<
name|V
argument_list|>
name|p
parameter_list|)
block|{
name|String
name|re
init|=
name|p
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|re
operator|.
name|startsWith
argument_list|(
literal|"^"
argument_list|)
condition|)
block|{
name|re
operator|=
name|re
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|re
operator|.
name|endsWith
argument_list|(
literal|"$"
argument_list|)
operator|&&
operator|!
name|re
operator|.
name|endsWith
argument_list|(
literal|"\\$"
argument_list|)
condition|)
block|{
name|re
operator|=
name|re
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|re
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|RegexpQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|p
operator|.
name|getField
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|re
argument_list|)
argument_list|)
return|;
block|}
DECL|method|prefixQuery (IndexPredicate<V> p)
specifier|private
name|Query
name|prefixQuery
parameter_list|(
name|IndexPredicate
argument_list|<
name|V
argument_list|>
name|p
parameter_list|)
block|{
return|return
operator|new
name|PrefixQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|p
operator|.
name|getField
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|p
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|fullTextQuery (IndexPredicate<V> p)
specifier|private
name|Query
name|fullTextQuery
parameter_list|(
name|IndexPredicate
argument_list|<
name|V
argument_list|>
name|p
parameter_list|)
throws|throws
name|QueryParseException
block|{
name|String
name|value
init|=
name|p
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryParseException
argument_list|(
literal|"Full-text search over empty string not supported"
argument_list|)
throw|;
block|}
name|Query
name|query
init|=
name|queryBuilder
operator|.
name|createPhraseQuery
argument_list|(
name|p
operator|.
name|getField
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|query
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryParseException
argument_list|(
literal|"Cannot create full-text query with value: "
operator|+
name|value
argument_list|)
throw|;
block|}
return|return
name|query
return|;
block|}
DECL|method|toIndexTimeInMinutes (Date ts)
specifier|public
name|int
name|toIndexTimeInMinutes
parameter_list|(
name|Date
name|ts
parameter_list|)
block|{
return|return
call|(
name|int
call|)
argument_list|(
name|ts
operator|.
name|getTime
argument_list|()
operator|/
literal|60000
argument_list|)
return|;
block|}
block|}
end_class

end_unit

