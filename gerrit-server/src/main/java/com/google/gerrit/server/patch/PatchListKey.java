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
DECL|package|com.google.gerrit.server.patch
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|patch
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
name|checkState
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|ObjectIdSerialization
operator|.
name|readCanBeNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|ObjectIdSerialization
operator|.
name|readNotNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|ObjectIdSerialization
operator|.
name|writeCanBeNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|ObjectIdSerialization
operator|.
name|writeNotNull
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
name|ImmutableBiMap
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
name|common
operator|.
name|Nullable
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
name|extensions
operator|.
name|client
operator|.
name|DiffPreferencesInfo
operator|.
name|Whitespace
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|AnyObjectId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|ObjectId
import|;
end_import

begin_class
DECL|class|PatchListKey
specifier|public
class|class
name|PatchListKey
implements|implements
name|Serializable
block|{
DECL|field|serialVersionUID
specifier|public
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|28L
decl_stmt|;
comment|// TODO(aliceks): Get rid of this enum and the parameter in the PatchListKey as we only use one of
comment|// its values.
DECL|enum|Algorithm
specifier|public
enum|enum
name|Algorithm
block|{
DECL|enumConstant|PURE_TREE_DIFF
name|PURE_TREE_DIFF
block|,
DECL|enumConstant|OPTIMIZED_DIFF
name|OPTIMIZED_DIFF
block|}
DECL|field|ALGORITHM_TYPES
specifier|private
specifier|static
specifier|final
name|ImmutableBiMap
argument_list|<
name|Algorithm
argument_list|,
name|Character
argument_list|>
name|ALGORITHM_TYPES
init|=
name|ImmutableBiMap
operator|.
name|of
argument_list|(
name|Algorithm
operator|.
name|PURE_TREE_DIFF
argument_list|,
literal|'T'
argument_list|,
name|Algorithm
operator|.
name|OPTIMIZED_DIFF
argument_list|,
literal|'O'
argument_list|)
decl_stmt|;
DECL|field|WHITESPACE_TYPES
specifier|public
specifier|static
specifier|final
name|ImmutableBiMap
argument_list|<
name|Whitespace
argument_list|,
name|Character
argument_list|>
name|WHITESPACE_TYPES
init|=
name|ImmutableBiMap
operator|.
name|of
argument_list|(
name|Whitespace
operator|.
name|IGNORE_NONE
argument_list|,
literal|'N'
argument_list|,
name|Whitespace
operator|.
name|IGNORE_TRAILING
argument_list|,
literal|'E'
argument_list|,
name|Whitespace
operator|.
name|IGNORE_LEADING_AND_TRAILING
argument_list|,
literal|'S'
argument_list|,
name|Whitespace
operator|.
name|IGNORE_ALL
argument_list|,
literal|'A'
argument_list|)
decl_stmt|;
static|static
block|{
name|checkState
argument_list|(
name|WHITESPACE_TYPES
operator|.
name|size
argument_list|()
operator|==
name|Whitespace
operator|.
name|values
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|checkState
argument_list|(
name|ALGORITHM_TYPES
operator|.
name|size
argument_list|()
operator|==
name|Algorithm
operator|.
name|values
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|againstDefaultBase (AnyObjectId newId, Whitespace ws)
specifier|public
specifier|static
name|PatchListKey
name|againstDefaultBase
parameter_list|(
name|AnyObjectId
name|newId
parameter_list|,
name|Whitespace
name|ws
parameter_list|)
block|{
return|return
operator|new
name|PatchListKey
argument_list|(
literal|null
argument_list|,
name|newId
argument_list|,
name|ws
argument_list|,
name|Algorithm
operator|.
name|OPTIMIZED_DIFF
argument_list|)
return|;
block|}
DECL|method|againstParentNum (int parentNum, AnyObjectId newId, Whitespace ws)
specifier|public
specifier|static
name|PatchListKey
name|againstParentNum
parameter_list|(
name|int
name|parentNum
parameter_list|,
name|AnyObjectId
name|newId
parameter_list|,
name|Whitespace
name|ws
parameter_list|)
block|{
return|return
operator|new
name|PatchListKey
argument_list|(
name|parentNum
argument_list|,
name|newId
argument_list|,
name|ws
argument_list|,
name|Algorithm
operator|.
name|OPTIMIZED_DIFF
argument_list|)
return|;
block|}
DECL|method|againstCommit ( AnyObjectId otherCommitId, AnyObjectId newId, Whitespace whitespace)
specifier|public
specifier|static
name|PatchListKey
name|againstCommit
parameter_list|(
name|AnyObjectId
name|otherCommitId
parameter_list|,
name|AnyObjectId
name|newId
parameter_list|,
name|Whitespace
name|whitespace
parameter_list|)
block|{
return|return
operator|new
name|PatchListKey
argument_list|(
name|otherCommitId
argument_list|,
name|newId
argument_list|,
name|whitespace
argument_list|,
name|Algorithm
operator|.
name|OPTIMIZED_DIFF
argument_list|)
return|;
block|}
comment|/**    * Old patch-set ID    *    *<p>When null, it represents the Base of the newId for a non-merge commit.    *    *<p>When newId is a merge commit, null value of the oldId represents either the auto-merge    * commit of the newId or a parent commit of the newId. These two cases are distinguished by the    * parentNum.    */
DECL|field|oldId
specifier|private
specifier|transient
name|ObjectId
name|oldId
decl_stmt|;
comment|/**    * 1-based parent number when newId is a merge commit    *    *<p>For the auto-merge case this field is null.    *    *<p>Used only when oldId is null and newId is a merge commit    */
DECL|field|parentNum
specifier|private
specifier|transient
name|Integer
name|parentNum
decl_stmt|;
DECL|field|newId
specifier|private
specifier|transient
name|ObjectId
name|newId
decl_stmt|;
DECL|field|whitespace
specifier|private
specifier|transient
name|Whitespace
name|whitespace
decl_stmt|;
DECL|field|algorithm
specifier|private
specifier|transient
name|Algorithm
name|algorithm
decl_stmt|;
DECL|method|PatchListKey (AnyObjectId a, AnyObjectId b, Whitespace ws, Algorithm algorithm)
specifier|private
name|PatchListKey
parameter_list|(
name|AnyObjectId
name|a
parameter_list|,
name|AnyObjectId
name|b
parameter_list|,
name|Whitespace
name|ws
parameter_list|,
name|Algorithm
name|algorithm
parameter_list|)
block|{
name|oldId
operator|=
name|a
operator|!=
literal|null
condition|?
name|a
operator|.
name|copy
argument_list|()
else|:
literal|null
expr_stmt|;
name|newId
operator|=
name|b
operator|.
name|copy
argument_list|()
expr_stmt|;
name|whitespace
operator|=
name|ws
expr_stmt|;
name|this
operator|.
name|algorithm
operator|=
name|algorithm
expr_stmt|;
block|}
DECL|method|PatchListKey (int parentNum, AnyObjectId b, Whitespace ws, Algorithm algorithm)
specifier|private
name|PatchListKey
parameter_list|(
name|int
name|parentNum
parameter_list|,
name|AnyObjectId
name|b
parameter_list|,
name|Whitespace
name|ws
parameter_list|,
name|Algorithm
name|algorithm
parameter_list|)
block|{
name|this
operator|.
name|parentNum
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|parentNum
argument_list|)
expr_stmt|;
name|newId
operator|=
name|b
operator|.
name|copy
argument_list|()
expr_stmt|;
name|whitespace
operator|=
name|ws
expr_stmt|;
name|this
operator|.
name|algorithm
operator|=
name|algorithm
expr_stmt|;
block|}
comment|/** For use only by DiffSummaryKey. */
DECL|method|PatchListKey ( ObjectId oldId, Integer parentNum, ObjectId newId, Whitespace whitespace, Algorithm algorithm)
name|PatchListKey
parameter_list|(
name|ObjectId
name|oldId
parameter_list|,
name|Integer
name|parentNum
parameter_list|,
name|ObjectId
name|newId
parameter_list|,
name|Whitespace
name|whitespace
parameter_list|,
name|Algorithm
name|algorithm
parameter_list|)
block|{
name|this
operator|.
name|oldId
operator|=
name|oldId
expr_stmt|;
name|this
operator|.
name|parentNum
operator|=
name|parentNum
expr_stmt|;
name|this
operator|.
name|newId
operator|=
name|newId
expr_stmt|;
name|this
operator|.
name|whitespace
operator|=
name|whitespace
expr_stmt|;
name|this
operator|.
name|algorithm
operator|=
name|algorithm
expr_stmt|;
block|}
comment|/** Old side commit, or null to assume ancestor or combined merge. */
annotation|@
name|Nullable
DECL|method|getOldId ()
specifier|public
name|ObjectId
name|getOldId
parameter_list|()
block|{
return|return
name|oldId
return|;
block|}
comment|/** Parent number (old side) of the new side (merge) commit */
annotation|@
name|Nullable
DECL|method|getParentNum ()
specifier|public
name|Integer
name|getParentNum
parameter_list|()
block|{
return|return
name|parentNum
return|;
block|}
comment|/** New side commit name. */
DECL|method|getNewId ()
specifier|public
name|ObjectId
name|getNewId
parameter_list|()
block|{
return|return
name|newId
return|;
block|}
DECL|method|getWhitespace ()
specifier|public
name|Whitespace
name|getWhitespace
parameter_list|()
block|{
return|return
name|whitespace
return|;
block|}
DECL|method|getAlgorithm ()
specifier|public
name|Algorithm
name|getAlgorithm
parameter_list|()
block|{
return|return
name|algorithm
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
name|Objects
operator|.
name|hash
argument_list|(
name|oldId
argument_list|,
name|parentNum
argument_list|,
name|newId
argument_list|,
name|whitespace
argument_list|,
name|algorithm
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|PatchListKey
condition|)
block|{
name|PatchListKey
name|k
init|=
operator|(
name|PatchListKey
operator|)
name|o
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|oldId
argument_list|,
name|k
operator|.
name|oldId
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|parentNum
argument_list|,
name|k
operator|.
name|parentNum
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|newId
argument_list|,
name|k
operator|.
name|newId
argument_list|)
operator|&&
name|whitespace
operator|==
name|k
operator|.
name|whitespace
operator|&&
name|algorithm
operator|==
name|k
operator|.
name|algorithm
return|;
block|}
return|return
literal|false
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
name|StringBuilder
name|n
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|n
operator|.
name|append
argument_list|(
literal|"PatchListKey["
argument_list|)
expr_stmt|;
name|n
operator|.
name|append
argument_list|(
name|oldId
operator|!=
literal|null
condition|?
name|oldId
operator|.
name|name
argument_list|()
else|:
literal|"BASE"
argument_list|)
expr_stmt|;
name|n
operator|.
name|append
argument_list|(
literal|".."
argument_list|)
expr_stmt|;
name|n
operator|.
name|append
argument_list|(
name|newId
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|n
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
if|if
condition|(
name|parentNum
operator|!=
literal|null
condition|)
block|{
name|n
operator|.
name|append
argument_list|(
name|parentNum
argument_list|)
expr_stmt|;
name|n
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
name|n
operator|.
name|append
argument_list|(
name|whitespace
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|n
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|n
operator|.
name|append
argument_list|(
name|algorithm
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|n
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
return|return
name|n
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|writeObject (ObjectOutputStream out)
specifier|private
name|void
name|writeObject
parameter_list|(
name|ObjectOutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|writeCanBeNull
argument_list|(
name|out
argument_list|,
name|oldId
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|parentNum
operator|==
literal|null
condition|?
literal|0
else|:
name|parentNum
argument_list|)
expr_stmt|;
name|writeNotNull
argument_list|(
name|out
argument_list|,
name|newId
argument_list|)
expr_stmt|;
name|Character
name|c
init|=
name|WHITESPACE_TYPES
operator|.
name|get
argument_list|(
name|whitespace
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid whitespace type: "
operator|+
name|whitespace
argument_list|)
throw|;
block|}
name|out
operator|.
name|writeChar
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeChar
argument_list|(
name|ALGORITHM_TYPES
operator|.
name|get
argument_list|(
name|algorithm
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|readObject (ObjectInputStream in)
specifier|private
name|void
name|readObject
parameter_list|(
name|ObjectInputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|oldId
operator|=
name|readCanBeNull
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|int
name|n
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|parentNum
operator|=
name|n
operator|==
literal|0
condition|?
literal|null
else|:
name|Integer
operator|.
name|valueOf
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|newId
operator|=
name|readNotNull
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|char
name|t
init|=
name|in
operator|.
name|readChar
argument_list|()
decl_stmt|;
name|whitespace
operator|=
name|WHITESPACE_TYPES
operator|.
name|inverse
argument_list|()
operator|.
name|get
argument_list|(
name|t
argument_list|)
expr_stmt|;
if|if
condition|(
name|whitespace
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid whitespace type code: "
operator|+
name|t
argument_list|)
throw|;
block|}
name|char
name|algorithmCharacter
init|=
name|in
operator|.
name|readChar
argument_list|()
decl_stmt|;
name|algorithm
operator|=
name|ALGORITHM_TYPES
operator|.
name|inverse
argument_list|()
operator|.
name|get
argument_list|(
name|algorithmCharacter
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

