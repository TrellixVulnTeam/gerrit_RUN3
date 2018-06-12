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
DECL|package|com.google.gerrit.elasticsearch
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|elasticsearch
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
name|elasticsearch
operator|.
name|builders
operator|.
name|BoolQueryBuilder
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
name|elasticsearch
operator|.
name|builders
operator|.
name|QueryBuilder
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
name|elasticsearch
operator|.
name|builders
operator|.
name|QueryBuilders
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
name|FieldDef
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
name|index
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
name|index
operator|.
name|query
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
name|index
operator|.
name|query
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
name|index
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
name|index
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
name|index
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
name|index
operator|.
name|query
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
name|index
operator|.
name|query
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
name|change
operator|.
name|AfterPredicate
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|Instant
import|;
end_import

begin_class
DECL|class|ElasticQueryBuilder
specifier|public
class|class
name|ElasticQueryBuilder
block|{
DECL|method|toQueryBuilder (Predicate<T> p)
parameter_list|<
name|T
parameter_list|>
name|QueryBuilder
name|toQueryBuilder
parameter_list|(
name|Predicate
argument_list|<
name|T
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
name|T
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
name|QueryBuilders
operator|.
name|matchAllQuery
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
DECL|method|and (Predicate<T> p)
specifier|private
parameter_list|<
name|T
parameter_list|>
name|BoolQueryBuilder
name|and
parameter_list|(
name|Predicate
argument_list|<
name|T
argument_list|>
name|p
parameter_list|)
throws|throws
name|QueryParseException
block|{
name|BoolQueryBuilder
name|b
init|=
name|QueryBuilders
operator|.
name|boolQuery
argument_list|()
decl_stmt|;
for|for
control|(
name|Predicate
argument_list|<
name|T
argument_list|>
name|c
range|:
name|p
operator|.
name|getChildren
argument_list|()
control|)
block|{
name|b
operator|.
name|must
argument_list|(
name|toQueryBuilder
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|b
return|;
block|}
DECL|method|or (Predicate<T> p)
specifier|private
parameter_list|<
name|T
parameter_list|>
name|BoolQueryBuilder
name|or
parameter_list|(
name|Predicate
argument_list|<
name|T
argument_list|>
name|p
parameter_list|)
throws|throws
name|QueryParseException
block|{
name|BoolQueryBuilder
name|q
init|=
name|QueryBuilders
operator|.
name|boolQuery
argument_list|()
decl_stmt|;
for|for
control|(
name|Predicate
argument_list|<
name|T
argument_list|>
name|c
range|:
name|p
operator|.
name|getChildren
argument_list|()
control|)
block|{
name|q
operator|.
name|should
argument_list|(
name|toQueryBuilder
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|q
return|;
block|}
DECL|method|not (Predicate<T> p)
specifier|private
parameter_list|<
name|T
parameter_list|>
name|QueryBuilder
name|not
parameter_list|(
name|Predicate
argument_list|<
name|T
argument_list|>
name|p
parameter_list|)
throws|throws
name|QueryParseException
block|{
name|Predicate
argument_list|<
name|T
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
name|T
argument_list|>
operator|)
name|n
argument_list|)
return|;
block|}
comment|// Lucene does not support negation, start with all and subtract.
name|BoolQueryBuilder
name|q
init|=
name|QueryBuilders
operator|.
name|boolQuery
argument_list|()
decl_stmt|;
name|q
operator|.
name|must
argument_list|(
name|QueryBuilders
operator|.
name|matchAllQuery
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|.
name|mustNot
argument_list|(
name|toQueryBuilder
argument_list|(
name|n
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|q
return|;
block|}
DECL|method|fieldQuery (IndexPredicate<T> p)
specifier|private
parameter_list|<
name|T
parameter_list|>
name|QueryBuilder
name|fieldQuery
parameter_list|(
name|IndexPredicate
argument_list|<
name|T
argument_list|>
name|p
parameter_list|)
throws|throws
name|QueryParseException
block|{
name|FieldType
argument_list|<
name|?
argument_list|>
name|type
init|=
name|p
operator|.
name|getType
argument_list|()
decl_stmt|;
name|FieldDef
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|field
init|=
name|p
operator|.
name|getField
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|field
operator|.
name|getName
argument_list|()
decl_stmt|;
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
name|type
operator|==
name|FieldType
operator|.
name|INTEGER
condition|)
block|{
comment|// QueryBuilder encodes integer fields as prefix coded bits,
comment|// which elasticsearch's queryString can't handle.
comment|// Create integer terms with string representations instead.
return|return
name|QueryBuilders
operator|.
name|termQuery
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|type
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
name|type
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
name|type
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
name|type
operator|==
name|FieldType
operator|.
name|PREFIX
condition|)
block|{
return|return
name|QueryBuilders
operator|.
name|matchPhrasePrefixQuery
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|FieldType
operator|.
name|FULL_TEXT
condition|)
block|{
return|return
name|QueryBuilders
operator|.
name|matchPhraseQuery
argument_list|(
name|name
argument_list|,
name|value
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
DECL|method|intRangeQuery (IndexPredicate<T> p)
specifier|private
parameter_list|<
name|T
parameter_list|>
name|QueryBuilder
name|intRangeQuery
parameter_list|(
name|IndexPredicate
argument_list|<
name|T
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
name|T
argument_list|>
name|r
init|=
operator|(
name|IntegerRangePredicate
argument_list|<
name|T
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
name|QueryBuilders
operator|.
name|termQuery
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
return|;
block|}
return|return
name|QueryBuilders
operator|.
name|rangeQuery
argument_list|(
name|p
operator|.
name|getField
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|gte
argument_list|(
name|minimum
argument_list|)
operator|.
name|lte
argument_list|(
name|maximum
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
DECL|method|notTimestamp (TimestampRangePredicate<T> r)
specifier|private
parameter_list|<
name|T
parameter_list|>
name|QueryBuilder
name|notTimestamp
parameter_list|(
name|TimestampRangePredicate
argument_list|<
name|T
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
name|QueryBuilders
operator|.
name|rangeQuery
argument_list|(
name|r
operator|.
name|getField
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|gt
argument_list|(
name|Instant
operator|.
name|ofEpochMilli
argument_list|(
name|r
operator|.
name|getMaxTimestamp
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|)
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
DECL|method|timestampQuery (IndexPredicate<T> p)
specifier|private
parameter_list|<
name|T
parameter_list|>
name|QueryBuilder
name|timestampQuery
parameter_list|(
name|IndexPredicate
argument_list|<
name|T
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
name|T
argument_list|>
name|r
init|=
operator|(
name|TimestampRangePredicate
argument_list|<
name|T
argument_list|>
operator|)
name|p
decl_stmt|;
if|if
condition|(
name|p
operator|instanceof
name|AfterPredicate
condition|)
block|{
return|return
name|QueryBuilders
operator|.
name|rangeQuery
argument_list|(
name|r
operator|.
name|getField
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|gte
argument_list|(
name|Instant
operator|.
name|ofEpochMilli
argument_list|(
name|r
operator|.
name|getMinTimestamp
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
return|return
name|QueryBuilders
operator|.
name|rangeQuery
argument_list|(
name|r
operator|.
name|getField
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|gte
argument_list|(
name|Instant
operator|.
name|ofEpochMilli
argument_list|(
name|r
operator|.
name|getMinTimestamp
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|)
argument_list|)
operator|.
name|lte
argument_list|(
name|Instant
operator|.
name|ofEpochMilli
argument_list|(
name|r
operator|.
name|getMaxTimestamp
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|)
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
DECL|method|exactQuery (IndexPredicate<T> p)
specifier|private
parameter_list|<
name|T
parameter_list|>
name|QueryBuilder
name|exactQuery
parameter_list|(
name|IndexPredicate
argument_list|<
name|T
argument_list|>
name|p
parameter_list|)
block|{
name|String
name|name
init|=
name|p
operator|.
name|getField
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
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
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
operator|new
name|BoolQueryBuilder
argument_list|()
operator|.
name|mustNot
argument_list|(
name|QueryBuilders
operator|.
name|existsQuery
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|p
operator|instanceof
name|RegexPredicate
condition|)
block|{
if|if
condition|(
name|value
operator|.
name|startsWith
argument_list|(
literal|"^"
argument_list|)
condition|)
block|{
name|value
operator|=
name|value
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|value
operator|.
name|endsWith
argument_list|(
literal|"$"
argument_list|)
operator|&&
operator|!
name|value
operator|.
name|endsWith
argument_list|(
literal|"\\$"
argument_list|)
operator|&&
operator|!
name|value
operator|.
name|endsWith
argument_list|(
literal|"\\\\$"
argument_list|)
condition|)
block|{
name|value
operator|=
name|value
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|value
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|QueryBuilders
operator|.
name|regexpQuery
argument_list|(
name|name
operator|+
literal|".key"
argument_list|,
name|value
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|QueryBuilders
operator|.
name|termQuery
argument_list|(
name|name
operator|+
literal|".key"
argument_list|,
name|value
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

