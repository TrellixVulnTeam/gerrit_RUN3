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
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
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
name|base
operator|.
name|Function
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
name|base
operator|.
name|Objects
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
name|Iterables
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
name|reviewdb
operator|.
name|server
operator|.
name|ReviewDb
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
name|Paginated
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
name|SortKeyPredicate
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
name|Iterator
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

begin_comment
comment|/**  * Wrapper combining an {@link IndexPredicate} together with a  * {@link ChangeDataSource} that returns matching results from the index.  *<p>  * Appropriate to return as the rootmost predicate that can be processed using  * the secondary index; such predicates must also implement  * {@link ChangeDataSource} to be chosen by the query processor.  */
end_comment

begin_class
DECL|class|IndexedChangeQuery
specifier|public
class|class
name|IndexedChangeQuery
extends|extends
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
implements|implements
name|ChangeDataSource
implements|,
name|Paginated
block|{
comment|/**    * Replace all {@link SortKeyPredicate}s in a tree.    *<p>    * Strictly speaking this should replace only the {@link SortKeyPredicate} at    * the top-level AND node, but this implementation is simpler, and the    * behavior of having multiple sortkey operators is undefined anyway.    *    * @param p predicate to replace in.    * @param newValue new cut value to replace all sortkey operators with.    * @return a copy of {@code p} with all sortkey predicates replaced; or p    *     itself.    */
annotation|@
name|VisibleForTesting
DECL|method|replaceSortKeyPredicates ( Predicate<ChangeData> p, String newValue)
specifier|static
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|replaceSortKeyPredicates
parameter_list|(
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|p
parameter_list|,
name|String
name|newValue
parameter_list|)
block|{
if|if
condition|(
name|p
operator|instanceof
name|SortKeyPredicate
condition|)
block|{
return|return
operator|(
operator|(
name|SortKeyPredicate
operator|)
name|p
operator|)
operator|.
name|copy
argument_list|(
name|newValue
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|p
operator|.
name|getChildCount
argument_list|()
operator|>
literal|0
condition|)
block|{
name|List
argument_list|<
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
argument_list|>
name|newChildren
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
name|boolean
name|replaced
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|c
range|:
name|p
operator|.
name|getChildren
argument_list|()
control|)
block|{
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|nc
init|=
name|replaceSortKeyPredicates
argument_list|(
name|c
argument_list|,
name|newValue
argument_list|)
decl_stmt|;
name|newChildren
operator|.
name|add
argument_list|(
name|nc
argument_list|)
expr_stmt|;
if|if
condition|(
name|nc
operator|!=
name|c
condition|)
block|{
name|replaced
operator|=
literal|true
expr_stmt|;
block|}
block|}
return|return
name|replaced
condition|?
name|p
operator|.
name|copy
argument_list|(
name|newChildren
argument_list|)
else|:
name|p
return|;
block|}
else|else
block|{
return|return
name|p
return|;
block|}
block|}
DECL|field|db
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
decl_stmt|;
DECL|field|index
specifier|private
specifier|final
name|ChangeIndex
name|index
decl_stmt|;
DECL|field|limit
specifier|private
specifier|final
name|int
name|limit
decl_stmt|;
DECL|field|pred
specifier|private
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|pred
decl_stmt|;
DECL|field|source
specifier|private
name|ChangeDataSource
name|source
decl_stmt|;
DECL|method|IndexedChangeQuery (Provider<ReviewDb> db, ChangeIndex index, Predicate<ChangeData> pred, int limit)
specifier|public
name|IndexedChangeQuery
parameter_list|(
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|,
name|ChangeIndex
name|index
parameter_list|,
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|pred
parameter_list|,
name|int
name|limit
parameter_list|)
throws|throws
name|QueryParseException
block|{
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|limit
operator|=
name|limit
expr_stmt|;
name|this
operator|.
name|pred
operator|=
name|pred
expr_stmt|;
name|this
operator|.
name|source
operator|=
name|index
operator|.
name|getSource
argument_list|(
name|pred
argument_list|,
name|limit
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getChildCount ()
specifier|public
name|int
name|getChildCount
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|getChild (int i)
specifier|public
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|getChild
parameter_list|(
name|int
name|i
parameter_list|)
block|{
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
return|return
name|pred
return|;
block|}
throw|throw
operator|new
name|ArrayIndexOutOfBoundsException
argument_list|(
name|i
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getChildren ()
specifier|public
name|List
argument_list|<
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
argument_list|>
name|getChildren
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|pred
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|limit ()
specifier|public
name|int
name|limit
parameter_list|()
block|{
return|return
name|limit
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
return|return
name|source
operator|!=
literal|null
condition|?
name|source
operator|.
name|getCardinality
argument_list|()
else|:
name|limit
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hasChange ()
specifier|public
name|boolean
name|hasChange
parameter_list|()
block|{
return|return
name|index
operator|.
name|getSchema
argument_list|()
operator|.
name|getFields
argument_list|()
operator|.
name|containsKey
argument_list|(
name|ChangeField
operator|.
name|CHANGE
operator|.
name|getName
argument_list|()
argument_list|)
return|;
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
specifier|final
name|ChangeDataSource
name|currSource
init|=
name|source
decl_stmt|;
specifier|final
name|ResultSet
argument_list|<
name|ChangeData
argument_list|>
name|rs
init|=
name|currSource
operator|.
name|read
argument_list|()
decl_stmt|;
return|return
operator|new
name|ResultSet
argument_list|<
name|ChangeData
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|ChangeData
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Iterables
operator|.
name|transform
argument_list|(
name|rs
argument_list|,
operator|new
name|Function
argument_list|<
name|ChangeData
argument_list|,
name|ChangeData
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ChangeData
name|apply
parameter_list|(
name|ChangeData
name|input
parameter_list|)
block|{
name|input
operator|.
name|cacheFromSource
argument_list|(
name|currSource
argument_list|)
expr_stmt|;
return|return
name|input
return|;
block|}
block|}
argument_list|)
operator|.
name|iterator
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|ChangeData
argument_list|>
name|toList
parameter_list|()
block|{
name|List
argument_list|<
name|ChangeData
argument_list|>
name|r
init|=
name|rs
operator|.
name|toList
argument_list|()
decl_stmt|;
for|for
control|(
name|ChangeData
name|cd
range|:
name|r
control|)
block|{
name|cd
operator|.
name|cacheFromSource
argument_list|(
name|currSource
argument_list|)
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
name|rs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|restart (ChangeData last)
specifier|public
name|ResultSet
argument_list|<
name|ChangeData
argument_list|>
name|restart
parameter_list|(
name|ChangeData
name|last
parameter_list|)
throws|throws
name|OrmException
block|{
name|pred
operator|=
name|replaceSortKeyPredicates
argument_list|(
name|pred
argument_list|,
name|last
operator|.
name|change
argument_list|(
name|db
argument_list|)
operator|.
name|getSortKey
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|source
operator|=
name|index
operator|.
name|getSource
argument_list|(
name|pred
argument_list|,
name|limit
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|QueryParseException
name|e
parameter_list|)
block|{
comment|// Don't need to show this exception to the user; the only thing that
comment|// changed about pred was its SortKeyPredicates, and any other QPEs
comment|// that might happen should have already thrown from the constructor.
throw|throw
operator|new
name|OrmException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|read
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|copy ( Collection<? extends Predicate<ChangeData>> children)
specifier|public
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|copy
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
name|children
parameter_list|)
block|{
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|match (ChangeData cd)
specifier|public
name|boolean
name|match
parameter_list|(
name|ChangeData
name|cd
parameter_list|)
throws|throws
name|OrmException
block|{
return|return
operator|(
name|source
operator|!=
literal|null
operator|&&
name|cd
operator|.
name|isFromSource
argument_list|(
name|source
argument_list|)
operator|)
operator|||
name|pred
operator|.
name|match
argument_list|(
name|cd
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getCost ()
specifier|public
name|int
name|getCost
parameter_list|()
block|{
comment|// Index queries are assumed to be cheaper than any other type of query, so
comment|// so try to make sure they get picked. Note that pred's cost may be higher
comment|// because it doesn't know whether it's being used in an index query or not.
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|pred
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object other)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|other
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|IndexedChangeQuery
name|o
init|=
operator|(
name|IndexedChangeQuery
operator|)
name|other
decl_stmt|;
return|return
name|pred
operator|.
name|equals
argument_list|(
name|o
operator|.
name|pred
argument_list|)
operator|&&
name|limit
operator|==
name|o
operator|.
name|limit
return|;
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
name|Objects
operator|.
name|toStringHelper
argument_list|(
literal|"index"
argument_list|)
operator|.
name|add
argument_list|(
literal|"p"
argument_list|,
name|pred
argument_list|)
operator|.
name|add
argument_list|(
literal|"limit"
argument_list|,
name|limit
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

