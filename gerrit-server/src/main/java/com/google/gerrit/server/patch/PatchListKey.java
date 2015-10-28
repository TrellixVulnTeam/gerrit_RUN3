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
name|BiMap
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

begin_class
DECL|class|PatchListKey
specifier|public
class|class
name|PatchListKey
implements|implements
name|Serializable
block|{
DECL|field|serialVersionUID
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|19L
decl_stmt|;
DECL|field|WHITESPACE_TYPES
specifier|public
specifier|static
specifier|final
name|BiMap
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
block|}
DECL|field|oldId
specifier|private
specifier|transient
name|ObjectId
name|oldId
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
DECL|method|PatchListKey (AnyObjectId a, AnyObjectId b, Whitespace ws)
specifier|public
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
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|h
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|oldId
operator|!=
literal|null
condition|)
block|{
name|h
operator|=
name|h
operator|*
literal|31
operator|+
name|oldId
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
name|h
operator|=
name|h
operator|*
literal|31
operator|+
name|newId
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|h
operator|=
name|h
operator|*
literal|31
operator|+
name|whitespace
operator|.
name|name
argument_list|()
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|h
return|;
block|}
annotation|@
name|Override
DECL|method|equals (final Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
specifier|final
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
specifier|final
name|PatchListKey
name|k
init|=
operator|(
name|PatchListKey
operator|)
name|o
decl_stmt|;
return|return
name|eq
argument_list|(
name|oldId
argument_list|,
name|k
operator|.
name|oldId
argument_list|)
comment|//
operator|&&
name|eq
argument_list|(
name|newId
argument_list|,
name|k
operator|.
name|newId
argument_list|)
comment|//
operator|&&
name|whitespace
operator|==
name|k
operator|.
name|whitespace
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
DECL|method|eq (final ObjectId a, final ObjectId b)
specifier|private
specifier|static
name|boolean
name|eq
parameter_list|(
specifier|final
name|ObjectId
name|a
parameter_list|,
specifier|final
name|ObjectId
name|b
parameter_list|)
block|{
if|if
condition|(
name|a
operator|==
literal|null
operator|&&
name|b
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
name|a
operator|!=
literal|null
operator|&&
name|b
operator|!=
literal|null
operator|&&
name|AnyObjectId
operator|.
name|equals
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
return|;
block|}
DECL|method|writeObject (final ObjectOutputStream out)
specifier|private
name|void
name|writeObject
parameter_list|(
specifier|final
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
block|}
DECL|method|readObject (final ObjectInputStream in)
specifier|private
name|void
name|readObject
parameter_list|(
specifier|final
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
block|}
block|}
end_class

end_unit

