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
name|MoreObjects
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
name|common
operator|.
name|collect
operator|.
name|Sets
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
name|Change
operator|.
name|Status
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
name|BasicChangeRewrites
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
name|ChangeQueryRewriter
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
name|inject
operator|.
name|Inject
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|BitSet
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
name|List
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

begin_comment
comment|/** Rewriter that pushes boolean logic into the secondary index. */
end_comment

begin_class
DECL|class|IndexRewriteImpl
specifier|public
class|class
name|IndexRewriteImpl
implements|implements
name|ChangeQueryRewriter
block|{
comment|/** Set of all open change statuses. */
DECL|field|OPEN_STATUSES
specifier|public
specifier|static
specifier|final
name|Set
argument_list|<
name|Change
operator|.
name|Status
argument_list|>
name|OPEN_STATUSES
decl_stmt|;
comment|/** Set of all closed change statuses. */
DECL|field|CLOSED_STATUSES
specifier|public
specifier|static
specifier|final
name|Set
argument_list|<
name|Change
operator|.
name|Status
argument_list|>
name|CLOSED_STATUSES
decl_stmt|;
static|static
block|{
name|EnumSet
argument_list|<
name|Change
operator|.
name|Status
argument_list|>
name|open
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
name|EnumSet
argument_list|<
name|Change
operator|.
name|Status
argument_list|>
name|closed
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
for|for
control|(
name|Change
operator|.
name|Status
name|s
range|:
name|Change
operator|.
name|Status
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|s
operator|.
name|isOpen
argument_list|()
condition|)
block|{
name|open
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|closed
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
name|OPEN_STATUSES
operator|=
name|Sets
operator|.
name|immutableEnumSet
argument_list|(
name|open
argument_list|)
expr_stmt|;
name|CLOSED_STATUSES
operator|=
name|Sets
operator|.
name|immutableEnumSet
argument_list|(
name|closed
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|field|MAX_LIMIT
specifier|static
specifier|final
name|int
name|MAX_LIMIT
init|=
literal|1000
decl_stmt|;
comment|/**    * Get the set of statuses that changes matching the given predicate may have.    *    * @param in predicate    * @return the maximal set of statuses that any changes matching the input    *     predicates may have, based on examining boolean and    *     {@link ChangeStatusPredicate}s.    */
DECL|method|getPossibleStatus (Predicate<ChangeData> in)
specifier|public
specifier|static
name|EnumSet
argument_list|<
name|Change
operator|.
name|Status
argument_list|>
name|getPossibleStatus
parameter_list|(
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|in
parameter_list|)
block|{
name|EnumSet
argument_list|<
name|Change
operator|.
name|Status
argument_list|>
name|s
init|=
name|extractStatus
argument_list|(
name|in
argument_list|)
decl_stmt|;
return|return
name|s
operator|!=
literal|null
condition|?
name|s
else|:
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
return|;
block|}
DECL|method|extractStatus (Predicate<ChangeData> in)
specifier|private
specifier|static
name|EnumSet
argument_list|<
name|Change
operator|.
name|Status
argument_list|>
name|extractStatus
parameter_list|(
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|in
parameter_list|)
block|{
if|if
condition|(
name|in
operator|instanceof
name|ChangeStatusPredicate
condition|)
block|{
return|return
name|EnumSet
operator|.
name|of
argument_list|(
operator|(
operator|(
name|ChangeStatusPredicate
operator|)
name|in
operator|)
operator|.
name|getStatus
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|in
operator|instanceof
name|NotPredicate
condition|)
block|{
name|EnumSet
argument_list|<
name|Status
argument_list|>
name|s
init|=
name|extractStatus
argument_list|(
name|in
operator|.
name|getChild
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|s
operator|!=
literal|null
condition|?
name|EnumSet
operator|.
name|complementOf
argument_list|(
name|s
argument_list|)
else|:
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|in
operator|instanceof
name|OrPredicate
condition|)
block|{
name|EnumSet
argument_list|<
name|Change
operator|.
name|Status
argument_list|>
name|r
init|=
literal|null
decl_stmt|;
name|int
name|childrenWithStatus
init|=
literal|0
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
name|in
operator|.
name|getChildCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|EnumSet
argument_list|<
name|Status
argument_list|>
name|c
init|=
name|extractStatus
argument_list|(
name|in
operator|.
name|getChild
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|r
operator|==
literal|null
condition|)
block|{
name|r
operator|=
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
expr_stmt|;
block|}
name|r
operator|.
name|addAll
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|childrenWithStatus
operator|++
expr_stmt|;
block|}
block|}
if|if
condition|(
name|r
operator|!=
literal|null
operator|&&
name|childrenWithStatus
operator|<
name|in
operator|.
name|getChildCount
argument_list|()
condition|)
block|{
comment|// At least one child supplied a status but another did not.
comment|// Assume all statuses for the children that did not feed a
comment|// status at this part of the tree. This matches behavior if
comment|// the child was used at the root of a query.
return|return
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
return|;
block|}
return|return
name|r
return|;
block|}
elseif|else
if|if
condition|(
name|in
operator|instanceof
name|AndPredicate
condition|)
block|{
name|EnumSet
argument_list|<
name|Change
operator|.
name|Status
argument_list|>
name|r
init|=
literal|null
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
name|in
operator|.
name|getChildCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|EnumSet
argument_list|<
name|Change
operator|.
name|Status
argument_list|>
name|c
init|=
name|extractStatus
argument_list|(
name|in
operator|.
name|getChild
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|r
operator|==
literal|null
condition|)
block|{
name|r
operator|=
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
expr_stmt|;
block|}
name|r
operator|.
name|retainAll
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|r
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|field|indexes
specifier|private
specifier|final
name|IndexCollection
name|indexes
decl_stmt|;
DECL|field|basicRewrites
specifier|private
specifier|final
name|BasicChangeRewrites
name|basicRewrites
decl_stmt|;
annotation|@
name|Inject
DECL|method|IndexRewriteImpl (IndexCollection indexes, BasicChangeRewrites basicRewrites)
name|IndexRewriteImpl
parameter_list|(
name|IndexCollection
name|indexes
parameter_list|,
name|BasicChangeRewrites
name|basicRewrites
parameter_list|)
block|{
name|this
operator|.
name|indexes
operator|=
name|indexes
expr_stmt|;
name|this
operator|.
name|basicRewrites
operator|=
name|basicRewrites
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|rewrite (Predicate<ChangeData> in, int start)
specifier|public
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
name|int
name|start
parameter_list|)
throws|throws
name|QueryParseException
block|{
name|ChangeIndex
name|index
init|=
name|indexes
operator|.
name|getSearchIndex
argument_list|()
decl_stmt|;
name|in
operator|=
name|basicRewrites
operator|.
name|rewrite
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|int
name|limit
init|=
name|MoreObjects
operator|.
name|firstNonNull
argument_list|(
name|ChangeQueryBuilder
operator|.
name|getLimit
argument_list|(
name|in
argument_list|)
argument_list|,
name|MAX_LIMIT
argument_list|)
decl_stmt|;
comment|// Increase the limit rather than skipping, since we don't know how many
comment|// skipped results would have been filtered out by the enclosing AndSource.
name|limit
operator|+=
name|start
expr_stmt|;
name|limit
operator|=
name|Math
operator|.
name|max
argument_list|(
name|limit
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|limit
operator|=
name|Math
operator|.
name|min
argument_list|(
name|limit
argument_list|,
name|MAX_LIMIT
argument_list|)
expr_stmt|;
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|out
init|=
name|rewriteImpl
argument_list|(
name|in
argument_list|,
name|index
argument_list|,
name|limit
argument_list|)
decl_stmt|;
if|if
condition|(
name|in
operator|==
name|out
operator|||
name|out
operator|instanceof
name|IndexPredicate
condition|)
block|{
return|return
operator|new
name|IndexedChangeQuery
argument_list|(
name|index
argument_list|,
name|out
argument_list|,
name|limit
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|out
operator|==
literal|null
comment|/* cannot rewrite */
condition|)
block|{
return|return
name|in
return|;
block|}
else|else
block|{
return|return
name|out
return|;
block|}
block|}
comment|/**    * Rewrite a single predicate subtree.    *    * @param in predicate to rewrite.    * @param index index whose schema determines which fields are indexed.    * @param limit maximum number of results to return.    * @return {@code null} if no part of this subtree can be queried in the    *     index directly. {@code in} if this subtree and all its children can be    *     queried directly in the index. Otherwise, a predicate that is    *     semantically equivalent, with some of its subtrees wrapped to query the    *     index directly.    * @throws QueryParseException if the underlying index implementation does not    *     support this predicate.    */
DECL|method|rewriteImpl (Predicate<ChangeData> in, ChangeIndex index, int limit)
specifier|private
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|rewriteImpl
parameter_list|(
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|in
parameter_list|,
name|ChangeIndex
name|index
parameter_list|,
name|int
name|limit
parameter_list|)
throws|throws
name|QueryParseException
block|{
if|if
condition|(
name|isIndexPredicate
argument_list|(
name|in
argument_list|,
name|index
argument_list|)
condition|)
block|{
return|return
name|in
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|isRewritePossible
argument_list|(
name|in
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
comment|// magic to indicate "in" cannot be rewritten
block|}
name|int
name|n
init|=
name|in
operator|.
name|getChildCount
argument_list|()
decl_stmt|;
name|BitSet
name|isIndexed
init|=
operator|new
name|BitSet
argument_list|(
name|n
argument_list|)
decl_stmt|;
name|BitSet
name|notIndexed
init|=
operator|new
name|BitSet
argument_list|(
name|n
argument_list|)
decl_stmt|;
name|BitSet
name|rewritten
init|=
operator|new
name|BitSet
argument_list|(
name|n
argument_list|)
decl_stmt|;
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
name|n
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
name|n
condition|;
name|i
operator|++
control|)
block|{
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|c
init|=
name|in
operator|.
name|getChild
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|nc
init|=
name|rewriteImpl
argument_list|(
name|c
argument_list|,
name|index
argument_list|,
name|limit
argument_list|)
decl_stmt|;
if|if
condition|(
name|nc
operator|==
name|c
condition|)
block|{
name|isIndexed
operator|.
name|set
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|newChildren
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|nc
operator|==
literal|null
comment|/* cannot rewrite c */
condition|)
block|{
name|notIndexed
operator|.
name|set
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|newChildren
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|rewritten
operator|.
name|set
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|newChildren
operator|.
name|add
argument_list|(
name|nc
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|isIndexed
operator|.
name|cardinality
argument_list|()
operator|==
name|n
condition|)
block|{
return|return
name|in
return|;
comment|// All children are indexed, leave as-is for parent.
block|}
elseif|else
if|if
condition|(
name|notIndexed
operator|.
name|cardinality
argument_list|()
operator|==
name|n
condition|)
block|{
return|return
literal|null
return|;
comment|// Can't rewrite any children, so cannot rewrite in.
block|}
elseif|else
if|if
condition|(
name|rewritten
operator|.
name|cardinality
argument_list|()
operator|==
name|n
condition|)
block|{
return|return
name|in
operator|.
name|copy
argument_list|(
name|newChildren
argument_list|)
return|;
comment|// All children were rewritten.
block|}
return|return
name|partitionChildren
argument_list|(
name|in
argument_list|,
name|newChildren
argument_list|,
name|isIndexed
argument_list|,
name|index
argument_list|,
name|limit
argument_list|)
return|;
block|}
DECL|method|isIndexPredicate (Predicate<ChangeData> in, ChangeIndex index)
specifier|private
name|boolean
name|isIndexPredicate
parameter_list|(
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|in
parameter_list|,
name|ChangeIndex
name|index
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|in
operator|instanceof
name|IndexPredicate
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|IndexPredicate
argument_list|<
name|ChangeData
argument_list|>
name|p
init|=
operator|(
name|IndexPredicate
argument_list|<
name|ChangeData
argument_list|>
operator|)
name|in
decl_stmt|;
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
name|p
operator|.
name|getField
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
DECL|method|partitionChildren ( Predicate<ChangeData> in, List<Predicate<ChangeData>> newChildren, BitSet isIndexed, ChangeIndex index, int limit)
specifier|private
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|partitionChildren
parameter_list|(
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|in
parameter_list|,
name|List
argument_list|<
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
argument_list|>
name|newChildren
parameter_list|,
name|BitSet
name|isIndexed
parameter_list|,
name|ChangeIndex
name|index
parameter_list|,
name|int
name|limit
parameter_list|)
throws|throws
name|QueryParseException
block|{
if|if
condition|(
name|isIndexed
operator|.
name|cardinality
argument_list|()
operator|==
literal|1
condition|)
block|{
name|int
name|i
init|=
name|isIndexed
operator|.
name|nextSetBit
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|newChildren
operator|.
name|add
argument_list|(
literal|0
argument_list|,
operator|new
name|IndexedChangeQuery
argument_list|(
name|index
argument_list|,
name|newChildren
operator|.
name|remove
argument_list|(
name|i
argument_list|)
argument_list|,
name|limit
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|copy
argument_list|(
name|in
argument_list|,
name|newChildren
argument_list|)
return|;
block|}
comment|// Group all indexed predicates into a wrapped subtree.
name|List
argument_list|<
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
argument_list|>
name|indexed
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|isIndexed
operator|.
name|cardinality
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
argument_list|>
name|all
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|newChildren
operator|.
name|size
argument_list|()
operator|-
name|isIndexed
operator|.
name|cardinality
argument_list|()
operator|+
literal|1
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
name|newChildren
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|c
init|=
name|newChildren
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|isIndexed
operator|.
name|get
argument_list|(
name|i
argument_list|)
condition|)
block|{
name|indexed
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|all
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
name|all
operator|.
name|add
argument_list|(
literal|0
argument_list|,
operator|new
name|IndexedChangeQuery
argument_list|(
name|index
argument_list|,
name|in
operator|.
name|copy
argument_list|(
name|indexed
argument_list|)
argument_list|,
name|limit
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|copy
argument_list|(
name|in
argument_list|,
name|all
argument_list|)
return|;
block|}
DECL|method|copy ( Predicate<ChangeData> in, List<Predicate<ChangeData>> all)
specifier|private
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|copy
parameter_list|(
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|in
parameter_list|,
name|List
argument_list|<
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
argument_list|>
name|all
parameter_list|)
block|{
if|if
condition|(
name|in
operator|instanceof
name|AndPredicate
condition|)
block|{
return|return
operator|new
name|AndSource
argument_list|(
name|all
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|in
operator|instanceof
name|OrPredicate
condition|)
block|{
return|return
operator|new
name|OrSource
argument_list|(
name|all
argument_list|)
return|;
block|}
return|return
name|in
operator|.
name|copy
argument_list|(
name|all
argument_list|)
return|;
block|}
DECL|method|isRewritePossible (Predicate<ChangeData> p)
specifier|private
specifier|static
name|boolean
name|isRewritePossible
parameter_list|(
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|p
parameter_list|)
block|{
return|return
name|p
operator|.
name|getChildCount
argument_list|()
operator|>
literal|0
operator|&&
operator|(
name|p
operator|instanceof
name|AndPredicate
operator|||
name|p
operator|instanceof
name|OrPredicate
operator|||
name|p
operator|instanceof
name|NotPredicate
operator|)
return|;
block|}
block|}
end_class

end_unit

