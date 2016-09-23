begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2016 The Android Open Source Project
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
name|gerrit
operator|.
name|server
operator|.
name|ioutil
operator|.
name|BasicSerialization
operator|.
name|readString
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|ioutil
operator|.
name|BasicSerialization
operator|.
name|readVarInt32
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|ioutil
operator|.
name|BasicSerialization
operator|.
name|writeString
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|ioutil
operator|.
name|BasicSerialization
operator|.
name|writeVarInt32
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
name|Arrays
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|DeflaterOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|InflaterInputStream
import|;
end_import

begin_class
DECL|class|DiffSummary
specifier|public
class|class
name|DiffSummary
implements|implements
name|Serializable
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
name|PatchListKey
operator|.
name|serialVersionUID
decl_stmt|;
DECL|field|paths
specifier|private
specifier|transient
name|String
index|[]
name|paths
decl_stmt|;
DECL|method|DiffSummary (String[] paths)
specifier|public
name|DiffSummary
parameter_list|(
name|String
index|[]
name|paths
parameter_list|)
block|{
name|this
operator|.
name|paths
operator|=
name|paths
expr_stmt|;
block|}
DECL|method|getPaths ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getPaths
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|paths
argument_list|)
argument_list|)
return|;
block|}
DECL|method|writeObject (ObjectOutputStream output)
specifier|private
name|void
name|writeObject
parameter_list|(
name|ObjectOutputStream
name|output
parameter_list|)
throws|throws
name|IOException
block|{
name|writeVarInt32
argument_list|(
name|output
argument_list|,
name|paths
operator|.
name|length
argument_list|)
expr_stmt|;
try|try
init|(
name|DeflaterOutputStream
name|out
init|=
operator|new
name|DeflaterOutputStream
argument_list|(
name|output
argument_list|)
init|)
block|{
for|for
control|(
name|String
name|p
range|:
name|paths
control|)
block|{
name|writeString
argument_list|(
name|out
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|readObject (ObjectInputStream input)
specifier|private
name|void
name|readObject
parameter_list|(
name|ObjectInputStream
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|paths
operator|=
operator|new
name|String
index|[
name|readVarInt32
argument_list|(
name|input
argument_list|)
index|]
expr_stmt|;
try|try
init|(
name|InflaterInputStream
name|in
init|=
operator|new
name|InflaterInputStream
argument_list|(
name|input
argument_list|)
init|)
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
name|paths
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|paths
index|[
name|i
index|]
operator|=
name|readString
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

