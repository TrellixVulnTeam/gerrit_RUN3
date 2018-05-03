begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2018 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
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
name|truth
operator|.
name|Truth
operator|.
name|assertThat
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
name|cache
operator|.
name|CacheSerializer
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
name|cache
operator|.
name|proto
operator|.
name|Cache
operator|.
name|ChangeKindKeyProto
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|ByteString
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
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|ChangeKindCacheImplTest
specifier|public
class|class
name|ChangeKindCacheImplTest
block|{
annotation|@
name|Test
DECL|method|keySerializer ()
specifier|public
name|void
name|keySerializer
parameter_list|()
throws|throws
name|Exception
block|{
name|ChangeKindCacheImpl
operator|.
name|Key
name|key
init|=
operator|new
name|ChangeKindCacheImpl
operator|.
name|Key
argument_list|(
name|ObjectId
operator|.
name|zeroId
argument_list|()
argument_list|,
name|ObjectId
operator|.
name|fromString
argument_list|(
literal|"deadbeefdeadbeefdeadbeefdeadbeefdeadbeef"
argument_list|)
argument_list|,
literal|"aStrategy"
argument_list|)
decl_stmt|;
name|CacheSerializer
argument_list|<
name|ChangeKindCacheImpl
operator|.
name|Key
argument_list|>
name|s
init|=
operator|new
name|ChangeKindCacheImpl
operator|.
name|Key
operator|.
name|Serializer
argument_list|()
decl_stmt|;
name|byte
index|[]
name|serialized
init|=
name|s
operator|.
name|serialize
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|ChangeKindKeyProto
operator|.
name|parseFrom
argument_list|(
name|serialized
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|ChangeKindKeyProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setPrior
argument_list|(
name|bytes
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
operator|.
name|setNext
argument_list|(
name|bytes
argument_list|(
literal|0xde
argument_list|,
literal|0xad
argument_list|,
literal|0xbe
argument_list|,
literal|0xef
argument_list|,
literal|0xde
argument_list|,
literal|0xad
argument_list|,
literal|0xbe
argument_list|,
literal|0xef
argument_list|,
literal|0xde
argument_list|,
literal|0xad
argument_list|,
literal|0xbe
argument_list|,
literal|0xef
argument_list|,
literal|0xde
argument_list|,
literal|0xad
argument_list|,
literal|0xbe
argument_list|,
literal|0xef
argument_list|,
literal|0xde
argument_list|,
literal|0xad
argument_list|,
literal|0xbe
argument_list|,
literal|0xef
argument_list|)
argument_list|)
operator|.
name|setStrategyName
argument_list|(
literal|"aStrategy"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|deserialize
argument_list|(
name|serialized
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
DECL|method|bytes (int... ints)
specifier|private
specifier|static
name|ByteString
name|bytes
parameter_list|(
name|int
modifier|...
name|ints
parameter_list|)
block|{
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|ints
operator|.
name|length
index|]
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
name|ints
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|bytes
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|ints
index|[
name|i
index|]
expr_stmt|;
block|}
return|return
name|ByteString
operator|.
name|copyFrom
argument_list|(
name|bytes
argument_list|)
return|;
block|}
block|}
end_class

end_unit

