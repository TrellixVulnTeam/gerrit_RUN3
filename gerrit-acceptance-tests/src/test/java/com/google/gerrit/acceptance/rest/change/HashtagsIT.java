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
DECL|package|com.google.gerrit.acceptance.rest.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|rest
operator|.
name|change
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
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
name|base
operator|.
name|CharMatcher
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
name|base
operator|.
name|Splitter
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
name|gerrit
operator|.
name|acceptance
operator|.
name|AbstractDaemonTest
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
name|acceptance
operator|.
name|RestResponse
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
name|api
operator|.
name|changes
operator|.
name|HashtagsInput
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
name|notedb
operator|.
name|NotesMigration
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
name|testutil
operator|.
name|ConfigSuite
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|reflect
operator|.
name|TypeToken
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpStatus
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
name|Config
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
name|HashSet
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

begin_class
DECL|class|HashtagsIT
specifier|public
class|class
name|HashtagsIT
extends|extends
name|AbstractDaemonTest
block|{
annotation|@
name|ConfigSuite
operator|.
name|Default
DECL|method|defaultConfig ()
specifier|public
specifier|static
name|Config
name|defaultConfig
parameter_list|()
block|{
return|return
name|NotesMigration
operator|.
name|allEnabledConfig
argument_list|()
return|;
block|}
DECL|method|assertResult (RestResponse r, List<String> expected)
specifier|private
name|void
name|assertResult
parameter_list|(
name|RestResponse
name|r
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|expected
parameter_list|)
throws|throws
name|IOException
block|{
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_OK
argument_list|,
name|r
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|result
init|=
name|toHashtagList
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetNoHashtags ()
specifier|public
name|void
name|testGetNoHashtags
parameter_list|()
throws|throws
name|Exception
block|{
comment|// GET hashtags on a change with no hashtags returns an empty list
name|String
name|changeId
init|=
name|createChange
argument_list|()
operator|.
name|getChangeId
argument_list|()
decl_stmt|;
name|assertResult
argument_list|(
name|GET
argument_list|(
name|changeId
argument_list|)
argument_list|,
name|ImmutableList
operator|.
expr|<
name|String
operator|>
name|of
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAddSingleHashtag ()
specifier|public
name|void
name|testAddSingleHashtag
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|changeId
init|=
name|createChange
argument_list|()
operator|.
name|getChangeId
argument_list|()
decl_stmt|;
comment|// POST adding a single hashtag returns a single hashtag
name|List
argument_list|<
name|String
argument_list|>
name|expected
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"tag2"
argument_list|)
decl_stmt|;
name|assertResult
argument_list|(
name|POST
argument_list|(
name|changeId
argument_list|,
literal|"tag2"
argument_list|,
literal|null
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|GET
argument_list|(
name|changeId
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
comment|// POST adding another single hashtag to change that already has one
comment|// hashtag returns a sorted list of hashtags with existing and new
name|expected
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"tag1"
argument_list|,
literal|"tag2"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|POST
argument_list|(
name|changeId
argument_list|,
literal|"tag1"
argument_list|,
literal|null
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|GET
argument_list|(
name|changeId
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAddMultipleHashtags ()
specifier|public
name|void
name|testAddMultipleHashtags
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|changeId
init|=
name|createChange
argument_list|()
operator|.
name|getChangeId
argument_list|()
decl_stmt|;
comment|// POST adding multiple hashtags returns a sorted list of hashtags
name|List
argument_list|<
name|String
argument_list|>
name|expected
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"tag1"
argument_list|,
literal|"tag3"
argument_list|)
decl_stmt|;
name|assertResult
argument_list|(
name|POST
argument_list|(
name|changeId
argument_list|,
literal|"tag3, tag1"
argument_list|,
literal|null
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|GET
argument_list|(
name|changeId
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
comment|// POST adding multiple hashtags to change that already has hashtags
comment|// returns a sorted list of hashtags with existing and new
name|expected
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"tag1"
argument_list|,
literal|"tag2"
argument_list|,
literal|"tag3"
argument_list|,
literal|"tag4"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|POST
argument_list|(
name|changeId
argument_list|,
literal|"tag2, tag4"
argument_list|,
literal|null
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|GET
argument_list|(
name|changeId
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAddAlreadyExistingHashtag ()
specifier|public
name|void
name|testAddAlreadyExistingHashtag
parameter_list|()
throws|throws
name|Exception
block|{
comment|// POST adding a hashtag that already exists on the change returns a
comment|// sorted list of hashtags without duplicates
name|String
name|changeId
init|=
name|createChange
argument_list|()
operator|.
name|getChangeId
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|expected
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"tag2"
argument_list|)
decl_stmt|;
name|assertResult
argument_list|(
name|POST
argument_list|(
name|changeId
argument_list|,
literal|"tag2"
argument_list|,
literal|null
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|GET
argument_list|(
name|changeId
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|POST
argument_list|(
name|changeId
argument_list|,
literal|"tag2"
argument_list|,
literal|null
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|GET
argument_list|(
name|changeId
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|expected
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"tag1"
argument_list|,
literal|"tag2"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|POST
argument_list|(
name|changeId
argument_list|,
literal|"tag2, tag1"
argument_list|,
literal|null
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|GET
argument_list|(
name|changeId
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHashtagsWithPrefix ()
specifier|public
name|void
name|testHashtagsWithPrefix
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|changeId
init|=
name|createChange
argument_list|()
operator|.
name|getChangeId
argument_list|()
decl_stmt|;
comment|// Leading # is stripped from added tag
name|List
argument_list|<
name|String
argument_list|>
name|expected
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"tag1"
argument_list|)
decl_stmt|;
name|assertResult
argument_list|(
name|POST
argument_list|(
name|changeId
argument_list|,
literal|"#tag1"
argument_list|,
literal|null
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|GET
argument_list|(
name|changeId
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
comment|// Leading # is stripped from multiple added tags
name|expected
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"tag1"
argument_list|,
literal|"tag2"
argument_list|,
literal|"tag3"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|POST
argument_list|(
name|changeId
argument_list|,
literal|"#tag2, #tag3"
argument_list|,
literal|null
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|GET
argument_list|(
name|changeId
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
comment|// Leading # is stripped from removed tag
name|expected
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"tag1"
argument_list|,
literal|"tag3"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|POST
argument_list|(
name|changeId
argument_list|,
literal|null
argument_list|,
literal|"#tag2"
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|GET
argument_list|(
name|changeId
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
comment|// Leading # is stripped from multiple removed tags
name|expected
operator|=
name|Collections
operator|.
name|emptyList
argument_list|()
expr_stmt|;
name|assertResult
argument_list|(
name|POST
argument_list|(
name|changeId
argument_list|,
literal|null
argument_list|,
literal|"#tag1, #tag3"
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|GET
argument_list|(
name|changeId
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
comment|// Leading # and space are stripped from added tag
name|expected
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"tag1"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|POST
argument_list|(
name|changeId
argument_list|,
literal|"# tag1"
argument_list|,
literal|null
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|GET
argument_list|(
name|changeId
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
comment|// Multiple leading # are stripped from added tag
name|expected
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"tag1"
argument_list|,
literal|"tag2"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|POST
argument_list|(
name|changeId
argument_list|,
literal|"##tag2"
argument_list|,
literal|null
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|GET
argument_list|(
name|changeId
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
comment|// Multiple leading spaces and # are stripped from added tag
name|expected
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"tag1"
argument_list|,
literal|"tag2"
argument_list|,
literal|"tag3"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|POST
argument_list|(
name|changeId
argument_list|,
literal|" # # tag3"
argument_list|,
literal|null
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|GET
argument_list|(
name|changeId
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRemoveSingleHashtag ()
specifier|public
name|void
name|testRemoveSingleHashtag
parameter_list|()
throws|throws
name|Exception
block|{
comment|// POST removing a single tag from a change that only has that tag
comment|// returns an empty list
name|String
name|changeId
init|=
name|createChange
argument_list|()
operator|.
name|getChangeId
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|expected
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"tag1"
argument_list|)
decl_stmt|;
name|assertResult
argument_list|(
name|POST
argument_list|(
name|changeId
argument_list|,
literal|"tag1"
argument_list|,
literal|null
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|POST
argument_list|(
name|changeId
argument_list|,
literal|null
argument_list|,
literal|"tag1"
argument_list|)
argument_list|,
name|ImmutableList
operator|.
expr|<
name|String
operator|>
name|of
argument_list|()
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|GET
argument_list|(
name|changeId
argument_list|)
argument_list|,
name|ImmutableList
operator|.
expr|<
name|String
operator|>
name|of
argument_list|()
argument_list|)
expr_stmt|;
comment|// POST removing a single tag from a change that has multiple tags
comment|// returns a sorted list of remaining tags
name|expected
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"tag1"
argument_list|,
literal|"tag2"
argument_list|,
literal|"tag3"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|POST
argument_list|(
name|changeId
argument_list|,
literal|"tag1, tag2, tag3"
argument_list|,
literal|null
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|expected
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"tag1"
argument_list|,
literal|"tag3"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|POST
argument_list|(
name|changeId
argument_list|,
literal|null
argument_list|,
literal|"tag2"
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|GET
argument_list|(
name|changeId
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRemoveMultipleHashtags ()
specifier|public
name|void
name|testRemoveMultipleHashtags
parameter_list|()
throws|throws
name|Exception
block|{
comment|// POST removing multiple tags from a change that only has those tags
comment|// returns an empty list
name|String
name|changeId
init|=
name|createChange
argument_list|()
operator|.
name|getChangeId
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|expected
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"tag1"
argument_list|,
literal|"tag2"
argument_list|)
decl_stmt|;
name|assertResult
argument_list|(
name|POST
argument_list|(
name|changeId
argument_list|,
literal|"tag1, tag2"
argument_list|,
literal|null
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|POST
argument_list|(
name|changeId
argument_list|,
literal|null
argument_list|,
literal|"tag1, tag2"
argument_list|)
argument_list|,
name|ImmutableList
operator|.
expr|<
name|String
operator|>
name|of
argument_list|()
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|GET
argument_list|(
name|changeId
argument_list|)
argument_list|,
name|ImmutableList
operator|.
expr|<
name|String
operator|>
name|of
argument_list|()
argument_list|)
expr_stmt|;
comment|// POST removing multiple tags from a change that has multiple changes
comment|// returns a sorted list of remaining changes
name|expected
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"tag1"
argument_list|,
literal|"tag2"
argument_list|,
literal|"tag3"
argument_list|,
literal|"tag4"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|POST
argument_list|(
name|changeId
argument_list|,
literal|"tag1, tag2, tag3, tag4"
argument_list|,
literal|null
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|expected
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"tag2"
argument_list|,
literal|"tag4"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|POST
argument_list|(
name|changeId
argument_list|,
literal|null
argument_list|,
literal|"tag1, tag3"
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|GET
argument_list|(
name|changeId
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRemoveNotExistingHashtag ()
specifier|public
name|void
name|testRemoveNotExistingHashtag
parameter_list|()
throws|throws
name|Exception
block|{
comment|// POST removing a single hashtag from change that has no hashtags
comment|// returns an empty list
name|String
name|changeId
init|=
name|createChange
argument_list|()
operator|.
name|getChangeId
argument_list|()
decl_stmt|;
name|assertResult
argument_list|(
name|POST
argument_list|(
name|changeId
argument_list|,
literal|null
argument_list|,
literal|"tag1"
argument_list|)
argument_list|,
name|ImmutableList
operator|.
expr|<
name|String
operator|>
name|of
argument_list|()
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|GET
argument_list|(
name|changeId
argument_list|)
argument_list|,
name|ImmutableList
operator|.
expr|<
name|String
operator|>
name|of
argument_list|()
argument_list|)
expr_stmt|;
comment|// POST removing a single non-existing tag from a change that only
comment|// has one other tag returns a list of only one tag
name|List
argument_list|<
name|String
argument_list|>
name|expected
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"tag1"
argument_list|)
decl_stmt|;
name|assertResult
argument_list|(
name|POST
argument_list|(
name|changeId
argument_list|,
literal|"tag1"
argument_list|,
literal|null
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|POST
argument_list|(
name|changeId
argument_list|,
literal|null
argument_list|,
literal|"tag4"
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|GET
argument_list|(
name|changeId
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
comment|// POST removing a single non-existing tag from a change that has multiple
comment|// tags returns a sorted list of tags without any deleted
name|expected
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"tag1"
argument_list|,
literal|"tag2"
argument_list|,
literal|"tag3"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|POST
argument_list|(
name|changeId
argument_list|,
literal|"tag1, tag2, tag3"
argument_list|,
literal|null
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|POST
argument_list|(
name|changeId
argument_list|,
literal|null
argument_list|,
literal|"tag4"
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|GET
argument_list|(
name|changeId
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
DECL|method|GET (String changeId)
specifier|private
name|RestResponse
name|GET
parameter_list|(
name|String
name|changeId
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|adminSession
operator|.
name|get
argument_list|(
literal|"/changes/"
operator|+
name|changeId
operator|+
literal|"/hashtags/"
argument_list|)
return|;
block|}
DECL|method|POST (String changeId, String toAdd, String toRemove)
specifier|private
name|RestResponse
name|POST
parameter_list|(
name|String
name|changeId
parameter_list|,
name|String
name|toAdd
parameter_list|,
name|String
name|toRemove
parameter_list|)
throws|throws
name|IOException
block|{
name|HashtagsInput
name|input
init|=
operator|new
name|HashtagsInput
argument_list|()
decl_stmt|;
if|if
condition|(
name|toAdd
operator|!=
literal|null
condition|)
block|{
name|input
operator|.
name|add
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|Splitter
operator|.
name|on
argument_list|(
name|CharMatcher
operator|.
name|anyOf
argument_list|(
literal|","
argument_list|)
argument_list|)
operator|.
name|split
argument_list|(
name|toAdd
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|toRemove
operator|!=
literal|null
condition|)
block|{
name|input
operator|.
name|remove
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|Splitter
operator|.
name|on
argument_list|(
name|CharMatcher
operator|.
name|anyOf
argument_list|(
literal|","
argument_list|)
argument_list|)
operator|.
name|split
argument_list|(
name|toRemove
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|adminSession
operator|.
name|post
argument_list|(
literal|"/changes/"
operator|+
name|changeId
operator|+
literal|"/hashtags/"
argument_list|,
name|input
argument_list|)
return|;
block|}
DECL|method|toHashtagList (RestResponse r)
specifier|private
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|toHashtagList
parameter_list|(
name|RestResponse
name|r
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|result
init|=
name|newGson
argument_list|()
operator|.
name|fromJson
argument_list|(
name|r
operator|.
name|getReader
argument_list|()
argument_list|,
operator|new
name|TypeToken
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
block|{}
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

