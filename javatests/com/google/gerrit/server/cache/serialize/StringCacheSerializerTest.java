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
DECL|package|com.google.gerrit.server.cache.serialize
package|package
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
name|serialize
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
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|testing
operator|.
name|GerritJUnit
operator|.
name|assertThrows
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|CharacterCodingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
DECL|class|StringCacheSerializerTest
specifier|public
class|class
name|StringCacheSerializerTest
block|{
annotation|@
name|Test
DECL|method|serialize ()
specifier|public
name|void
name|serialize
parameter_list|()
block|{
name|assertThat
argument_list|(
name|StringCacheSerializer
operator|.
name|INSTANCE
operator|.
name|serialize
argument_list|(
literal|""
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|StringCacheSerializer
operator|.
name|INSTANCE
operator|.
name|serialize
argument_list|(
literal|"abc"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|'a'
block|,
literal|'b'
block|,
literal|'c'
block|}
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|StringCacheSerializer
operator|.
name|INSTANCE
operator|.
name|serialize
argument_list|(
literal|"a\u1234c"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|'a'
block|,
operator|(
name|byte
operator|)
literal|0xe1
block|,
operator|(
name|byte
operator|)
literal|0x88
block|,
operator|(
name|byte
operator|)
literal|0xb4
block|,
literal|'c'
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|serializeInvalidChar ()
specifier|public
name|void
name|serializeInvalidChar
parameter_list|()
block|{
comment|// Can't use UTF-8 for the test, since it can encode all Unicode code points.
name|IllegalStateException
name|thrown
init|=
name|assertThrows
argument_list|(
name|IllegalStateException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|StringCacheSerializer
operator|.
name|serialize
argument_list|(
name|StandardCharsets
operator|.
name|US_ASCII
argument_list|,
literal|"\u1234"
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|thrown
argument_list|)
operator|.
name|hasCauseThat
argument_list|()
operator|.
name|isInstanceOf
argument_list|(
name|CharacterCodingException
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|deserialize ()
specifier|public
name|void
name|deserialize
parameter_list|()
block|{
name|assertThat
argument_list|(
name|StringCacheSerializer
operator|.
name|INSTANCE
operator|.
name|deserialize
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|StringCacheSerializer
operator|.
name|INSTANCE
operator|.
name|deserialize
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|'a'
block|,
literal|'b'
block|,
literal|'c'
block|}
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"abc"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|StringCacheSerializer
operator|.
name|INSTANCE
operator|.
name|deserialize
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|'a'
block|,
operator|(
name|byte
operator|)
literal|0xe1
block|,
operator|(
name|byte
operator|)
literal|0x88
block|,
operator|(
name|byte
operator|)
literal|0xb4
block|,
literal|'c'
block|}
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"a\u1234c"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|deserializeInvalidChar ()
specifier|public
name|void
name|deserializeInvalidChar
parameter_list|()
block|{
name|IllegalStateException
name|thrown
init|=
name|assertThrows
argument_list|(
name|IllegalStateException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|StringCacheSerializer
operator|.
name|INSTANCE
operator|.
name|deserialize
argument_list|(
operator|new
name|byte
index|[]
block|{
operator|(
name|byte
operator|)
literal|0xff
block|}
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|thrown
argument_list|)
operator|.
name|hasCauseThat
argument_list|()
operator|.
name|isInstanceOf
argument_list|(
name|CharacterCodingException
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

