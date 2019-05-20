begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2010 The Android Open Source Project
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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
operator|.
name|toImmutableList
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
name|exceptions
operator|.
name|StorageException
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
name|FieldBundle
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
name|LazyResultSet
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
name|ResultSet
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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|Optional
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
DECL|class|OrSource
specifier|public
class|class
name|OrSource
extends|extends
name|OrPredicate
argument_list|<
name|ChangeData
argument_list|>
implements|implements
name|ChangeDataSource
block|{
DECL|field|cardinality
specifier|private
name|int
name|cardinality
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|OrSource (Collection<? extends Predicate<ChangeData>> that)
specifier|public
name|OrSource
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
argument_list|>
name|that
parameter_list|)
block|{
name|super
argument_list|(
name|that
argument_list|)
expr_stmt|;
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
block|{
name|Optional
argument_list|<
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
argument_list|>
name|nonChangeDataSource
init|=
name|getChildren
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|p
lambda|->
operator|!
operator|(
name|p
operator|instanceof
name|ChangeDataSource
operator|)
argument_list|)
operator|.
name|findAny
argument_list|()
decl_stmt|;
if|if
condition|(
name|nonChangeDataSource
operator|.
name|isPresent
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|StorageException
argument_list|(
literal|"No ChangeDataSource: "
operator|+
name|nonChangeDataSource
operator|.
name|get
argument_list|()
argument_list|)
throw|;
block|}
comment|// ResultSets are lazy. Calling #read here first and then dealing with ResultSets only when
comment|// requested allows the index to run asynchronous queries.
name|List
argument_list|<
name|ResultSet
argument_list|<
name|ChangeData
argument_list|>
argument_list|>
name|results
init|=
name|getChildren
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|p
lambda|->
operator|(
operator|(
name|ChangeDataSource
operator|)
name|p
operator|)
operator|.
name|read
argument_list|()
argument_list|)
operator|.
name|collect
argument_list|(
name|toImmutableList
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|LazyResultSet
argument_list|<>
argument_list|(
parameter_list|()
lambda|->
block|{
name|List
argument_list|<
name|ChangeData
argument_list|>
name|r
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
name|have
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ResultSet
argument_list|<
name|ChangeData
argument_list|>
name|resultSet
range|:
name|results
control|)
block|{
for|for
control|(
name|ChangeData
name|result
range|:
name|resultSet
control|)
block|{
if|if
condition|(
name|have
operator|.
name|add
argument_list|(
name|result
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
name|r
operator|.
name|add
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|r
argument_list|)
return|;
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|readRaw ()
specifier|public
name|ResultSet
argument_list|<
name|FieldBundle
argument_list|>
name|readRaw
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"not implemented"
argument_list|)
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
for|for
control|(
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|p
range|:
name|getChildren
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
operator|(
name|p
operator|instanceof
name|ChangeDataSource
operator|)
operator|||
operator|!
operator|(
operator|(
name|ChangeDataSource
operator|)
name|p
operator|)
operator|.
name|hasChange
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|getCardinality ()
specifier|public
name|int
name|getCardinality
parameter_list|()
block|{
if|if
condition|(
name|cardinality
operator|<
literal|0
condition|)
block|{
name|cardinality
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|p
range|:
name|getChildren
argument_list|()
control|)
block|{
if|if
condition|(
name|p
operator|instanceof
name|ChangeDataSource
condition|)
block|{
name|cardinality
operator|+=
operator|(
operator|(
name|ChangeDataSource
operator|)
name|p
operator|)
operator|.
name|getCardinality
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
name|cardinality
return|;
block|}
block|}
end_class

end_unit

