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
DECL|package|com.google.gerrit.server.logging
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|logging
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
name|ImmutableSet
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
name|ImmutableSetMultimap
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
DECL|class|MutableTagsTest
specifier|public
class|class
name|MutableTagsTest
block|{
DECL|field|tags
specifier|private
name|MutableTags
name|tags
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|tags
operator|=
operator|new
name|MutableTags
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|addTag ()
specifier|public
name|void
name|addTag
parameter_list|()
block|{
name|assertThat
argument_list|(
name|tags
operator|.
name|add
argument_list|(
literal|"name"
argument_list|,
literal|"value"
argument_list|)
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertTags
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"name"
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"value"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|addTagsWithDifferentName ()
specifier|public
name|void
name|addTagsWithDifferentName
parameter_list|()
block|{
name|assertThat
argument_list|(
name|tags
operator|.
name|add
argument_list|(
literal|"name1"
argument_list|,
literal|"value1"
argument_list|)
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|tags
operator|.
name|add
argument_list|(
literal|"name2"
argument_list|,
literal|"value2"
argument_list|)
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertTags
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"name1"
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"value1"
argument_list|)
argument_list|,
literal|"name2"
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"value2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|addTagsWithSameNameButDifferentValues ()
specifier|public
name|void
name|addTagsWithSameNameButDifferentValues
parameter_list|()
block|{
name|assertThat
argument_list|(
name|tags
operator|.
name|add
argument_list|(
literal|"name"
argument_list|,
literal|"value1"
argument_list|)
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|tags
operator|.
name|add
argument_list|(
literal|"name"
argument_list|,
literal|"value2"
argument_list|)
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertTags
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"name"
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"value1"
argument_list|,
literal|"value2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|addTagsWithSameNameAndSameValue ()
specifier|public
name|void
name|addTagsWithSameNameAndSameValue
parameter_list|()
block|{
name|assertThat
argument_list|(
name|tags
operator|.
name|add
argument_list|(
literal|"name"
argument_list|,
literal|"value"
argument_list|)
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|tags
operator|.
name|add
argument_list|(
literal|"name"
argument_list|,
literal|"value"
argument_list|)
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
name|assertTags
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"name"
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"value"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|getEmptyTags ()
specifier|public
name|void
name|getEmptyTags
parameter_list|()
block|{
name|assertThat
argument_list|(
name|tags
operator|.
name|getTags
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertTags
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|isEmpty ()
specifier|public
name|void
name|isEmpty
parameter_list|()
block|{
name|assertThat
argument_list|(
name|tags
operator|.
name|isEmpty
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|tags
operator|.
name|add
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tags
operator|.
name|isEmpty
argument_list|()
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
name|tags
operator|.
name|remove
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tags
operator|.
name|isEmpty
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|removeTags ()
specifier|public
name|void
name|removeTags
parameter_list|()
block|{
name|tags
operator|.
name|add
argument_list|(
literal|"name1"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
name|tags
operator|.
name|add
argument_list|(
literal|"name1"
argument_list|,
literal|"value2"
argument_list|)
expr_stmt|;
name|tags
operator|.
name|add
argument_list|(
literal|"name2"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|assertTags
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"name1"
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"value1"
argument_list|,
literal|"value2"
argument_list|)
argument_list|,
literal|"name2"
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"value"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|tags
operator|.
name|remove
argument_list|(
literal|"name2"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|assertTags
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"name1"
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"value1"
argument_list|,
literal|"value2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|tags
operator|.
name|remove
argument_list|(
literal|"name1"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
name|assertTags
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"name1"
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"value2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|tags
operator|.
name|remove
argument_list|(
literal|"name1"
argument_list|,
literal|"value2"
argument_list|)
expr_stmt|;
name|assertTags
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|removeNonExistingTag ()
specifier|public
name|void
name|removeNonExistingTag
parameter_list|()
block|{
name|tags
operator|.
name|add
argument_list|(
literal|"name"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|assertTags
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"name"
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"value"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|tags
operator|.
name|remove
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|assertTags
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"name"
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"value"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|tags
operator|.
name|remove
argument_list|(
literal|"name"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|assertTags
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"name"
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"value"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|setTags ()
specifier|public
name|void
name|setTags
parameter_list|()
block|{
name|tags
operator|.
name|add
argument_list|(
literal|"name"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|assertTags
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"name"
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"value"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|tags
operator|.
name|set
argument_list|(
name|ImmutableSetMultimap
operator|.
name|of
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
literal|"foo"
argument_list|,
literal|"baz"
argument_list|,
literal|"bar"
argument_list|,
literal|"baz"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTags
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"foo"
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"bar"
argument_list|,
literal|"baz"
argument_list|)
argument_list|,
literal|"bar"
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"baz"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|asMap ()
specifier|public
name|void
name|asMap
parameter_list|()
block|{
name|tags
operator|.
name|add
argument_list|(
literal|"name"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tags
operator|.
name|asMap
argument_list|()
argument_list|)
operator|.
name|containsExactlyEntriesIn
argument_list|(
name|ImmutableSetMultimap
operator|.
name|of
argument_list|(
literal|"name"
argument_list|,
literal|"value"
argument_list|)
argument_list|)
expr_stmt|;
name|tags
operator|.
name|set
argument_list|(
name|ImmutableSetMultimap
operator|.
name|of
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
literal|"foo"
argument_list|,
literal|"baz"
argument_list|,
literal|"bar"
argument_list|,
literal|"baz"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tags
operator|.
name|asMap
argument_list|()
argument_list|)
operator|.
name|containsExactlyEntriesIn
argument_list|(
name|ImmutableSetMultimap
operator|.
name|of
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
literal|"foo"
argument_list|,
literal|"baz"
argument_list|,
literal|"bar"
argument_list|,
literal|"baz"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|clearTags ()
specifier|public
name|void
name|clearTags
parameter_list|()
block|{
name|tags
operator|.
name|add
argument_list|(
literal|"name1"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
name|tags
operator|.
name|add
argument_list|(
literal|"name1"
argument_list|,
literal|"value2"
argument_list|)
expr_stmt|;
name|tags
operator|.
name|add
argument_list|(
literal|"name2"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|assertTags
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"name1"
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"value1"
argument_list|,
literal|"value2"
argument_list|)
argument_list|,
literal|"name2"
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"value"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|tags
operator|.
name|clear
argument_list|()
expr_stmt|;
name|assertTags
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|addInvalidTag ()
specifier|public
name|void
name|addInvalidTag
parameter_list|()
block|{
name|assertNullPointerException
argument_list|(
literal|"tag name is required"
argument_list|,
parameter_list|()
lambda|->
name|tags
operator|.
name|add
argument_list|(
literal|null
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNullPointerException
argument_list|(
literal|"tag value is required"
argument_list|,
parameter_list|()
lambda|->
name|tags
operator|.
name|add
argument_list|(
literal|"foo"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|removeInvalidTag ()
specifier|public
name|void
name|removeInvalidTag
parameter_list|()
block|{
name|assertNullPointerException
argument_list|(
literal|"tag name is required"
argument_list|,
parameter_list|()
lambda|->
name|tags
operator|.
name|remove
argument_list|(
literal|null
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNullPointerException
argument_list|(
literal|"tag value is required"
argument_list|,
parameter_list|()
lambda|->
name|tags
operator|.
name|remove
argument_list|(
literal|"foo"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertTags (ImmutableMap<String, ImmutableSet<String>> expectedTagMap)
specifier|private
name|void
name|assertTags
parameter_list|(
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|ImmutableSet
argument_list|<
name|String
argument_list|>
argument_list|>
name|expectedTagMap
parameter_list|)
block|{
name|SortedMap
argument_list|<
name|String
argument_list|,
name|SortedSet
argument_list|<
name|Object
argument_list|>
argument_list|>
name|actualTagMap
init|=
name|tags
operator|.
name|getTags
argument_list|()
operator|.
name|asMap
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|actualTagMap
operator|.
name|keySet
argument_list|()
argument_list|)
operator|.
name|containsExactlyElementsIn
argument_list|(
name|expectedTagMap
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|ImmutableSet
argument_list|<
name|String
argument_list|>
argument_list|>
name|expectedEntry
range|:
name|expectedTagMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|assertThat
argument_list|(
name|actualTagMap
operator|.
name|get
argument_list|(
name|expectedEntry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
operator|.
name|containsExactlyElementsIn
argument_list|(
name|expectedEntry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertNullPointerException (String expectedMessage, Runnable r)
specifier|private
name|void
name|assertNullPointerException
parameter_list|(
name|String
name|expectedMessage
parameter_list|,
name|Runnable
name|r
parameter_list|)
block|{
name|NullPointerException
name|thrown
init|=
name|assertThrows
argument_list|(
name|NullPointerException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|r
operator|.
name|run
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|thrown
argument_list|)
operator|.
name|hasMessageThat
argument_list|()
operator|.
name|isEqualTo
argument_list|(
name|expectedMessage
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

