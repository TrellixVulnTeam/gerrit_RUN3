begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2019 The Android Open Source Project
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
DECL|package|com.google.gerrit.reviewdb.client
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|reviewdb
operator|.
name|client
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
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|PatchTest
specifier|public
class|class
name|PatchTest
block|{
annotation|@
name|Test
DECL|method|isMagic ()
specifier|public
name|void
name|isMagic
parameter_list|()
block|{
name|assertThat
argument_list|(
name|Patch
operator|.
name|isMagic
argument_list|(
literal|"/COMMIT_MSG"
argument_list|)
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|Patch
operator|.
name|isMagic
argument_list|(
literal|"/MERGE_LIST"
argument_list|)
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|Patch
operator|.
name|isMagic
argument_list|(
literal|"/COMMIT_MSG/"
argument_list|)
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|Patch
operator|.
name|isMagic
argument_list|(
literal|"COMMIT_MSG"
argument_list|)
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|Patch
operator|.
name|isMagic
argument_list|(
literal|"/commit_msg"
argument_list|)
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|parseKey ()
specifier|public
name|void
name|parseKey
parameter_list|()
block|{
name|assertThat
argument_list|(
name|Patch
operator|.
name|Key
operator|.
name|parse
argument_list|(
literal|"1,2,foo.txt"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|Patch
operator|.
name|key
argument_list|(
name|PatchSet
operator|.
name|id
argument_list|(
name|Change
operator|.
name|id
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|"foo.txt"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Patch
operator|.
name|Key
operator|.
name|parse
argument_list|(
literal|"01,02,foo.txt"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|Patch
operator|.
name|key
argument_list|(
name|PatchSet
operator|.
name|id
argument_list|(
name|Change
operator|.
name|id
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|"foo.txt"
argument_list|)
argument_list|)
expr_stmt|;
name|assertInvalidKey
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertInvalidKey
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|assertInvalidKey
argument_list|(
literal|"1,2"
argument_list|)
expr_stmt|;
name|assertInvalidKey
argument_list|(
literal|"1, 2, foo.txt"
argument_list|)
expr_stmt|;
name|assertInvalidKey
argument_list|(
literal|"1,foo.txt"
argument_list|)
expr_stmt|;
name|assertInvalidKey
argument_list|(
literal|"1,foo.txt,2"
argument_list|)
expr_stmt|;
name|assertInvalidKey
argument_list|(
literal|"foo.txt,1,2"
argument_list|)
expr_stmt|;
name|String
name|hexComma
init|=
literal|"%"
operator|+
name|String
operator|.
name|format
argument_list|(
literal|"%02x"
argument_list|,
operator|(
name|int
operator|)
literal|','
argument_list|)
decl_stmt|;
name|assertInvalidKey
argument_list|(
literal|"1"
operator|+
name|hexComma
operator|+
literal|"2"
operator|+
name|hexComma
operator|+
literal|"foo.txt"
argument_list|)
expr_stmt|;
block|}
DECL|method|assertInvalidKey (String str)
specifier|private
specifier|static
name|void
name|assertInvalidKey
parameter_list|(
name|String
name|str
parameter_list|)
block|{
name|assertThrows
argument_list|(
name|RuntimeException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|Patch
operator|.
name|Key
operator|.
name|parse
argument_list|(
name|str
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

