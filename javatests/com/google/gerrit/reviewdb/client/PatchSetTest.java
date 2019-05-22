begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2014 The Android Open Source Project
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
name|reviewdb
operator|.
name|client
operator|.
name|PatchSet
operator|.
name|joinGroups
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
name|reviewdb
operator|.
name|client
operator|.
name|PatchSet
operator|.
name|splitGroups
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
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|PatchSetTest
specifier|public
class|class
name|PatchSetTest
block|{
annotation|@
name|Test
DECL|method|parseRefNames ()
specifier|public
name|void
name|parseRefNames
parameter_list|()
block|{
name|assertRef
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|"refs/changes/01/1/1"
argument_list|)
expr_stmt|;
name|assertRef
argument_list|(
literal|1234
argument_list|,
literal|56
argument_list|,
literal|"refs/changes/34/1234/56"
argument_list|)
expr_stmt|;
comment|// Not even close.
name|assertNotRef
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertNotRef
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|assertNotRef
argument_list|(
literal|"01/1/1"
argument_list|)
expr_stmt|;
name|assertNotRef
argument_list|(
literal|"HEAD"
argument_list|)
expr_stmt|;
name|assertNotRef
argument_list|(
literal|"refs/tags/v1"
argument_list|)
expr_stmt|;
comment|// Invalid characters.
name|assertNotRef
argument_list|(
literal|"refs/changes/0x/1/1"
argument_list|)
expr_stmt|;
name|assertNotRef
argument_list|(
literal|"refs/changes/01/x/1"
argument_list|)
expr_stmt|;
name|assertNotRef
argument_list|(
literal|"refs/changes/01/1/x"
argument_list|)
expr_stmt|;
comment|// Truncations.
name|assertNotRef
argument_list|(
literal|"refs/changes/"
argument_list|)
expr_stmt|;
name|assertNotRef
argument_list|(
literal|"refs/changes/1"
argument_list|)
expr_stmt|;
name|assertNotRef
argument_list|(
literal|"refs/changes/01"
argument_list|)
expr_stmt|;
name|assertNotRef
argument_list|(
literal|"refs/changes/01/"
argument_list|)
expr_stmt|;
name|assertNotRef
argument_list|(
literal|"refs/changes/01/1/"
argument_list|)
expr_stmt|;
name|assertNotRef
argument_list|(
literal|"refs/changes/01/1/1/"
argument_list|)
expr_stmt|;
name|assertNotRef
argument_list|(
literal|"refs/changes/01//1/1"
argument_list|)
expr_stmt|;
comment|// Leading zeroes.
name|assertNotRef
argument_list|(
literal|"refs/changes/01/01/1"
argument_list|)
expr_stmt|;
name|assertNotRef
argument_list|(
literal|"refs/changes/01/1/01"
argument_list|)
expr_stmt|;
comment|// Mismatched last 2 digits.
name|assertNotRef
argument_list|(
literal|"refs/changes/35/1234/56"
argument_list|)
expr_stmt|;
comment|// Something other than patch set after change.
name|assertNotRef
argument_list|(
literal|"refs/changes/34/1234/0"
argument_list|)
expr_stmt|;
name|assertNotRef
argument_list|(
literal|"refs/changes/34/1234/foo"
argument_list|)
expr_stmt|;
name|assertNotRef
argument_list|(
literal|"refs/changes/34/1234|56"
argument_list|)
expr_stmt|;
name|assertNotRef
argument_list|(
literal|"refs/changes/34/1234foo"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSplitGroups ()
specifier|public
name|void
name|testSplitGroups
parameter_list|()
block|{
name|assertRuntimeException
argument_list|(
parameter_list|()
lambda|->
name|splitGroups
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|splitGroups
argument_list|(
literal|""
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|splitGroups
argument_list|(
literal|"abcd"
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|"abcd"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|splitGroups
argument_list|(
literal|"ab,cd"
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|"ab"
argument_list|,
literal|"cd"
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|splitGroups
argument_list|(
literal|"ab , cd"
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|"ab "
argument_list|,
literal|" cd"
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|splitGroups
argument_list|(
literal|"ab,"
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|"ab"
argument_list|,
literal|""
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|splitGroups
argument_list|(
literal|",cd"
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|""
argument_list|,
literal|"cd"
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJoinGroups ()
specifier|public
name|void
name|testJoinGroups
parameter_list|()
block|{
name|assertRuntimeException
argument_list|(
parameter_list|()
lambda|->
name|joinGroups
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertRuntimeException
argument_list|(
parameter_list|()
lambda|->
name|joinGroups
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"a,"
argument_list|,
literal|"b"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|joinGroups
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
literal|""
argument_list|)
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|joinGroups
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"abcd"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"abcd"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|joinGroups
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"ab"
argument_list|,
literal|"cd"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"ab,cd"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|joinGroups
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"ab "
argument_list|,
literal|" cd"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"ab , cd"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|joinGroups
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"ab"
argument_list|,
literal|""
argument_list|)
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"ab,"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|joinGroups
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
literal|""
argument_list|,
literal|"cd"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|",cd"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|toRefName ()
specifier|public
name|void
name|toRefName
parameter_list|()
block|{
name|assertThat
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
literal|23
argument_list|)
operator|.
name|toRefName
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"refs/changes/01/1/23"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|PatchSet
operator|.
name|id
argument_list|(
name|Change
operator|.
name|id
argument_list|(
literal|1234
argument_list|)
argument_list|,
literal|5
argument_list|)
operator|.
name|toRefName
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"refs/changes/34/1234/5"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|parseId ()
specifier|public
name|void
name|parseId
parameter_list|()
block|{
name|assertThat
argument_list|(
name|PatchSet
operator|.
name|Id
operator|.
name|parse
argument_list|(
literal|"1,2"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
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
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|PatchSet
operator|.
name|Id
operator|.
name|parse
argument_list|(
literal|"01,02"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
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
argument_list|)
expr_stmt|;
name|assertInvalidId
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertInvalidId
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|assertInvalidId
argument_list|(
literal|"1"
argument_list|)
expr_stmt|;
name|assertInvalidId
argument_list|(
literal|"1,foo.txt"
argument_list|)
expr_stmt|;
name|assertInvalidId
argument_list|(
literal|"foo.txt,1"
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
name|assertInvalidId
argument_list|(
literal|"1"
operator|+
name|hexComma
operator|+
literal|"2"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|idToString ()
specifier|public
name|void
name|idToString
parameter_list|()
block|{
name|assertThat
argument_list|(
name|PatchSet
operator|.
name|id
argument_list|(
name|Change
operator|.
name|id
argument_list|(
literal|2
argument_list|)
argument_list|,
literal|3
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"2,3"
argument_list|)
expr_stmt|;
block|}
DECL|method|assertRef (int changeId, int psId, String refName)
specifier|private
specifier|static
name|void
name|assertRef
parameter_list|(
name|int
name|changeId
parameter_list|,
name|int
name|psId
parameter_list|,
name|String
name|refName
parameter_list|)
block|{
name|assertThat
argument_list|(
name|PatchSet
operator|.
name|isChangeRef
argument_list|(
name|refName
argument_list|)
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|PatchSet
operator|.
name|Id
operator|.
name|fromRef
argument_list|(
name|refName
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|PatchSet
operator|.
name|id
argument_list|(
name|Change
operator|.
name|id
argument_list|(
name|changeId
argument_list|)
argument_list|,
name|psId
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertNotRef (String refName)
specifier|private
specifier|static
name|void
name|assertNotRef
parameter_list|(
name|String
name|refName
parameter_list|)
block|{
name|assertThat
argument_list|(
name|PatchSet
operator|.
name|isChangeRef
argument_list|(
name|refName
argument_list|)
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|PatchSet
operator|.
name|Id
operator|.
name|fromRef
argument_list|(
name|refName
argument_list|)
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
block|}
DECL|method|assertInvalidId (String str)
specifier|private
specifier|static
name|void
name|assertInvalidId
parameter_list|(
name|String
name|str
parameter_list|)
block|{
name|assertRuntimeException
argument_list|(
parameter_list|()
lambda|->
name|PatchSet
operator|.
name|Id
operator|.
name|parse
argument_list|(
name|str
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertRuntimeException (Runnable runnable)
specifier|private
specifier|static
name|void
name|assertRuntimeException
parameter_list|(
name|Runnable
name|runnable
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
name|runnable
operator|.
name|run
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

