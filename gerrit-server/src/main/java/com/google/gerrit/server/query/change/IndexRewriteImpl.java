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
name|IndexRewrite
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
operator|.
name|getClass
argument_list|()
operator|==
name|NotPredicate
operator|.
name|class
condition|)
block|{
return|return
name|EnumSet
operator|.
name|complementOf
argument_list|(
name|getPossibleStatus
argument_list|(
name|in
operator|.
name|getChild
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|in
operator|.
name|getClass
argument_list|()
operator|==
name|OrPredicate
operator|.
name|class
condition|)
block|{
name|EnumSet
argument_list|<
name|Change
operator|.
name|Status
argument_list|>
name|s
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
name|s
operator|.
name|addAll
argument_list|(
name|getPossibleStatus
argument_list|(
name|in
operator|.
name|getChild
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
elseif|else
if|if
condition|(
name|in
operator|.
name|getClass
argument_list|()
operator|==
name|AndPredicate
operator|.
name|class
condition|)
block|{
name|EnumSet
argument_list|<
name|Change
operator|.
name|Status
argument_list|>
name|s
init|=
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
name|s
operator|.
name|retainAll
argument_list|(
name|getPossibleStatus
argument_list|(
name|in
operator|.
name|getChild
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
elseif|else
if|if
condition|(
name|in
operator|.
name|getChildCount
argument_list|()
operator|==
literal|0
condition|)
block|{
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
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Invalid predicate type in change index query: "
operator|+
name|in
operator|.
name|getClass
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|field|index
specifier|private
specifier|final
name|ChangeIndex
name|index
decl_stmt|;
annotation|@
name|Inject
DECL|method|IndexRewriteImpl (ChangeIndex index)
name|IndexRewriteImpl
parameter_list|(
name|ChangeIndex
name|index
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|rewrite (Predicate<ChangeData> in)
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
parameter_list|)
block|{
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|out
init|=
name|rewriteImpl
argument_list|(
name|in
argument_list|)
decl_stmt|;
if|if
condition|(
name|out
operator|==
literal|null
condition|)
block|{
return|return
name|in
return|;
block|}
elseif|else
if|if
condition|(
name|out
operator|==
name|in
condition|)
block|{
return|return
name|wrap
argument_list|(
name|out
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|out
return|;
block|}
block|}
comment|/**    * Rewrite a single predicate subtree.    *    * @param in predicate to rewrite.    * @return {@code null} if no part of this subtree can be queried in the    *     index directly. {@code in} if this subtree and all its children can be    *     queried directly in the index. Otherwise, a predicate that is    *     semantically equivalent, with some of its subtrees wrapped to query the    *     index directly.    */
DECL|method|rewriteImpl (Predicate<ChangeData> in)
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
parameter_list|)
block|{
if|if
condition|(
name|in
operator|instanceof
name|IndexPredicate
condition|)
block|{
return|return
name|in
return|;
block|}
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
name|toKeep
init|=
operator|new
name|BitSet
argument_list|(
name|n
argument_list|)
decl_stmt|;
name|BitSet
name|toWrap
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
argument_list|)
decl_stmt|;
if|if
condition|(
name|nc
operator|==
literal|null
condition|)
block|{
name|toKeep
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
name|c
condition|)
block|{
name|toWrap
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
name|toKeep
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
comment|// Can't rewrite any children.
block|}
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
comment|// All children were partially, but not fully, rewritten.
return|return
name|in
operator|.
name|copy
argument_list|(
name|newChildren
argument_list|)
return|;
block|}
if|if
condition|(
name|toWrap
operator|.
name|cardinality
argument_list|()
operator|==
name|n
condition|)
block|{
comment|// All children can be fully rewritten, push work to parent.
return|return
name|in
return|;
block|}
return|return
name|partitionChildren
argument_list|(
name|in
argument_list|,
name|newChildren
argument_list|,
name|toWrap
argument_list|)
return|;
block|}
DECL|method|partitionChildren (Predicate<ChangeData> in, List<Predicate<ChangeData>> newChildren, BitSet toWrap)
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
name|toWrap
parameter_list|)
block|{
if|if
condition|(
name|toWrap
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
name|toWrap
operator|.
name|nextSetBit
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|newChildren
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|wrap
argument_list|(
name|newChildren
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|in
operator|.
name|copy
argument_list|(
name|newChildren
argument_list|)
return|;
block|}
comment|// Group all toWrap predicates into a wrapped subtree and place it as a
comment|// sibling of the non-/partially-wrapped predicates. Assumes partitioning
comment|// the children into arbitrary subtrees of the same type is logically
comment|// equivalent to having them as siblings.
name|List
argument_list|<
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
argument_list|>
name|wrapped
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|toWrap
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
name|toWrap
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
name|child
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
name|toWrap
operator|.
name|get
argument_list|(
name|i
argument_list|)
condition|)
block|{
name|wrapped
operator|.
name|add
argument_list|(
name|child
argument_list|)
expr_stmt|;
if|if
condition|(
name|allNonIndexOnly
argument_list|(
name|child
argument_list|)
condition|)
block|{
comment|// Duplicate non-index-only predicate subtrees alongside the wrapped
comment|// subtrees so they can provide index hints to the DB-based rewriter.
name|all
operator|.
name|add
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|all
operator|.
name|add
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
block|}
name|all
operator|.
name|add
argument_list|(
name|wrap
argument_list|(
name|in
operator|.
name|copy
argument_list|(
name|wrapped
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|in
operator|.
name|copy
argument_list|(
name|all
argument_list|)
return|;
block|}
DECL|method|allNonIndexOnly (Predicate<ChangeData> p)
specifier|private
specifier|static
name|boolean
name|allNonIndexOnly
parameter_list|(
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|p
parameter_list|)
block|{
if|if
condition|(
name|p
operator|instanceof
name|IndexPredicate
condition|)
block|{
return|return
operator|!
operator|(
operator|(
name|IndexPredicate
argument_list|<
name|ChangeData
argument_list|>
operator|)
name|p
operator|)
operator|.
name|isIndexOnly
argument_list|()
return|;
block|}
if|if
condition|(
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
condition|)
block|{
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
if|if
condition|(
operator|!
name|allNonIndexOnly
argument_list|(
name|p
operator|.
name|getChild
argument_list|(
name|i
argument_list|)
argument_list|)
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
else|else
block|{
return|return
literal|true
return|;
block|}
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
block|{
try|try
block|{
return|return
operator|new
name|PredicateWrapper
argument_list|(
name|index
argument_list|,
name|p
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|QueryParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Failed to convert "
operator|+
name|p
operator|+
literal|" to index predicate"
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
if|if
condition|(
name|p
operator|.
name|getClass
argument_list|()
operator|!=
name|AndPredicate
operator|.
name|class
operator|&&
name|p
operator|.
name|getClass
argument_list|()
operator|!=
name|OrPredicate
operator|.
name|class
operator|&&
name|p
operator|.
name|getClass
argument_list|()
operator|!=
name|NotPredicate
operator|.
name|class
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|p
operator|.
name|getChildCount
argument_list|()
operator|>
literal|0
return|;
block|}
block|}
end_class

end_unit

