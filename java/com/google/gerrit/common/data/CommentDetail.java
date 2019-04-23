begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2009 The Android Open Source Project
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
DECL|package|com.google.gerrit.common.data
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|common
operator|.
name|data
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
name|Comment
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
name|PatchSet
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
name|HashMap
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
name|Map
import|;
end_import

begin_class
DECL|class|CommentDetail
specifier|public
class|class
name|CommentDetail
block|{
DECL|field|a
specifier|protected
name|List
argument_list|<
name|Comment
argument_list|>
name|a
decl_stmt|;
DECL|field|b
specifier|protected
name|List
argument_list|<
name|Comment
argument_list|>
name|b
decl_stmt|;
DECL|field|idA
specifier|private
specifier|transient
name|PatchSet
operator|.
name|Id
name|idA
decl_stmt|;
DECL|field|idB
specifier|private
specifier|transient
name|PatchSet
operator|.
name|Id
name|idB
decl_stmt|;
DECL|field|forA
specifier|private
specifier|transient
name|Map
argument_list|<
name|Integer
argument_list|,
name|List
argument_list|<
name|Comment
argument_list|>
argument_list|>
name|forA
decl_stmt|;
DECL|field|forB
specifier|private
specifier|transient
name|Map
argument_list|<
name|Integer
argument_list|,
name|List
argument_list|<
name|Comment
argument_list|>
argument_list|>
name|forB
decl_stmt|;
DECL|method|CommentDetail (PatchSet.Id idA, PatchSet.Id idB)
specifier|public
name|CommentDetail
parameter_list|(
name|PatchSet
operator|.
name|Id
name|idA
parameter_list|,
name|PatchSet
operator|.
name|Id
name|idB
parameter_list|)
block|{
name|this
operator|.
name|a
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|b
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|idA
operator|=
name|idA
expr_stmt|;
name|this
operator|.
name|idB
operator|=
name|idB
expr_stmt|;
block|}
DECL|method|CommentDetail ()
specifier|protected
name|CommentDetail
parameter_list|()
block|{}
DECL|method|include (Change.Id changeId, Comment p)
specifier|public
name|void
name|include
parameter_list|(
name|Change
operator|.
name|Id
name|changeId
parameter_list|,
name|Comment
name|p
parameter_list|)
block|{
name|PatchSet
operator|.
name|Id
name|psId
init|=
name|PatchSet
operator|.
name|id
argument_list|(
name|changeId
argument_list|,
name|p
operator|.
name|key
operator|.
name|patchSetId
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|.
name|side
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|idA
operator|==
literal|null
operator|&&
name|idB
operator|.
name|equals
argument_list|(
name|psId
argument_list|)
condition|)
block|{
name|a
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|p
operator|.
name|side
operator|==
literal|1
condition|)
block|{
if|if
condition|(
name|idA
operator|!=
literal|null
operator|&&
name|idA
operator|.
name|equals
argument_list|(
name|psId
argument_list|)
condition|)
block|{
name|a
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|idB
operator|.
name|equals
argument_list|(
name|psId
argument_list|)
condition|)
block|{
name|b
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getCommentsA ()
specifier|public
name|List
argument_list|<
name|Comment
argument_list|>
name|getCommentsA
parameter_list|()
block|{
return|return
name|a
return|;
block|}
DECL|method|getCommentsB ()
specifier|public
name|List
argument_list|<
name|Comment
argument_list|>
name|getCommentsB
parameter_list|()
block|{
return|return
name|b
return|;
block|}
DECL|method|isEmpty ()
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|a
operator|.
name|isEmpty
argument_list|()
operator|&&
name|b
operator|.
name|isEmpty
argument_list|()
return|;
block|}
DECL|method|getForA (int lineNbr)
specifier|public
name|List
argument_list|<
name|Comment
argument_list|>
name|getForA
parameter_list|(
name|int
name|lineNbr
parameter_list|)
block|{
if|if
condition|(
name|forA
operator|==
literal|null
condition|)
block|{
name|forA
operator|=
name|index
argument_list|(
name|a
argument_list|)
expr_stmt|;
block|}
return|return
name|get
argument_list|(
name|forA
argument_list|,
name|lineNbr
argument_list|)
return|;
block|}
DECL|method|getForB (int lineNbr)
specifier|public
name|List
argument_list|<
name|Comment
argument_list|>
name|getForB
parameter_list|(
name|int
name|lineNbr
parameter_list|)
block|{
if|if
condition|(
name|forB
operator|==
literal|null
condition|)
block|{
name|forB
operator|=
name|index
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
return|return
name|get
argument_list|(
name|forB
argument_list|,
name|lineNbr
argument_list|)
return|;
block|}
DECL|method|get (Map<Integer, List<Comment>> m, int i)
specifier|private
specifier|static
name|List
argument_list|<
name|Comment
argument_list|>
name|get
parameter_list|(
name|Map
argument_list|<
name|Integer
argument_list|,
name|List
argument_list|<
name|Comment
argument_list|>
argument_list|>
name|m
parameter_list|,
name|int
name|i
parameter_list|)
block|{
name|List
argument_list|<
name|Comment
argument_list|>
name|r
init|=
name|m
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
return|return
name|r
operator|!=
literal|null
condition|?
name|orderComments
argument_list|(
name|r
argument_list|)
else|:
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
comment|/**    * Order the comments based on their parent_uuid parent. It is possible to do this by iterating    * over the list only once but it's probably overkill since the number of comments on a given line    * will be small most of the time.    *    * @param comments The list of comments for a given line.    * @return The comments sorted as they should appear in the UI    */
DECL|method|orderComments (List<Comment> comments)
specifier|private
specifier|static
name|List
argument_list|<
name|Comment
argument_list|>
name|orderComments
parameter_list|(
name|List
argument_list|<
name|Comment
argument_list|>
name|comments
parameter_list|)
block|{
comment|// Map of comments keyed by their parent. The values are lists of comments since it is
comment|// possible for several comments to have the same parent (this can happen if two reviewers
comment|// click Reply on the same comment at the same time). Such comments will be displayed under
comment|// their correct parent in chronological order.
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Comment
argument_list|>
argument_list|>
name|parentMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// It's possible to have more than one root comment if two reviewers create a comment on the
comment|// same line at the same time
name|List
argument_list|<
name|Comment
argument_list|>
name|rootComments
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Store all the comments in parentMap, keyed by their parent
for|for
control|(
name|Comment
name|c
range|:
name|comments
control|)
block|{
name|String
name|parentUuid
init|=
name|c
operator|.
name|parentUuid
decl_stmt|;
name|List
argument_list|<
name|Comment
argument_list|>
name|l
init|=
name|parentMap
operator|.
name|get
argument_list|(
name|parentUuid
argument_list|)
decl_stmt|;
if|if
condition|(
name|l
operator|==
literal|null
condition|)
block|{
name|l
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|parentMap
operator|.
name|put
argument_list|(
name|parentUuid
argument_list|,
name|l
argument_list|)
expr_stmt|;
block|}
name|l
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
if|if
condition|(
name|parentUuid
operator|==
literal|null
condition|)
block|{
name|rootComments
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Add the comments in the list, starting with the head and then going through all the
comment|// comments that have it as a parent, and so on
name|List
argument_list|<
name|Comment
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|addChildren
argument_list|(
name|parentMap
argument_list|,
name|rootComments
argument_list|,
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/** Add the comments to {@code outResult}, depth first */
DECL|method|addChildren ( Map<String, List<Comment>> parentMap, List<Comment> children, List<Comment> outResult)
specifier|private
specifier|static
name|void
name|addChildren
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Comment
argument_list|>
argument_list|>
name|parentMap
parameter_list|,
name|List
argument_list|<
name|Comment
argument_list|>
name|children
parameter_list|,
name|List
argument_list|<
name|Comment
argument_list|>
name|outResult
parameter_list|)
block|{
if|if
condition|(
name|children
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Comment
name|c
range|:
name|children
control|)
block|{
name|outResult
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|addChildren
argument_list|(
name|parentMap
argument_list|,
name|parentMap
operator|.
name|get
argument_list|(
name|c
operator|.
name|key
operator|.
name|uuid
argument_list|)
argument_list|,
name|outResult
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|index (List<Comment> in)
specifier|private
name|Map
argument_list|<
name|Integer
argument_list|,
name|List
argument_list|<
name|Comment
argument_list|>
argument_list|>
name|index
parameter_list|(
name|List
argument_list|<
name|Comment
argument_list|>
name|in
parameter_list|)
block|{
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|List
argument_list|<
name|Comment
argument_list|>
argument_list|>
name|r
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Comment
name|p
range|:
name|in
control|)
block|{
name|List
argument_list|<
name|Comment
argument_list|>
name|l
init|=
name|r
operator|.
name|get
argument_list|(
name|p
operator|.
name|lineNbr
argument_list|)
decl_stmt|;
if|if
condition|(
name|l
operator|==
literal|null
condition|)
block|{
name|l
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|r
operator|.
name|put
argument_list|(
name|p
operator|.
name|lineNbr
argument_list|,
name|l
argument_list|)
expr_stmt|;
block|}
name|l
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
block|}
end_class

end_unit

