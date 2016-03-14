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
DECL|package|com.google.gerrit.server.notedb
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|notedb
package|;
end_package

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
name|RevId
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
name|errors
operator|.
name|ConfigInvalidException
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
name|ObjectReader
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
name|notes
operator|.
name|Note
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
name|notes
operator|.
name|NoteMap
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
name|HashMap
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
DECL|class|RevisionNoteMap
class|class
name|RevisionNoteMap
block|{
DECL|field|noteMap
specifier|final
name|NoteMap
name|noteMap
decl_stmt|;
DECL|field|revisionNotes
specifier|final
name|ImmutableMap
argument_list|<
name|RevId
argument_list|,
name|RevisionNote
argument_list|>
name|revisionNotes
decl_stmt|;
DECL|method|parse (ChangeNoteUtil noteUtil, Change.Id changeId, ObjectReader reader, NoteMap noteMap, boolean draftsOnly)
specifier|static
name|RevisionNoteMap
name|parse
parameter_list|(
name|ChangeNoteUtil
name|noteUtil
parameter_list|,
name|Change
operator|.
name|Id
name|changeId
parameter_list|,
name|ObjectReader
name|reader
parameter_list|,
name|NoteMap
name|noteMap
parameter_list|,
name|boolean
name|draftsOnly
parameter_list|)
throws|throws
name|ConfigInvalidException
throws|,
name|IOException
block|{
name|Map
argument_list|<
name|RevId
argument_list|,
name|RevisionNote
argument_list|>
name|result
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Note
name|note
range|:
name|noteMap
control|)
block|{
name|RevisionNote
name|rn
init|=
operator|new
name|RevisionNote
argument_list|(
name|noteUtil
argument_list|,
name|changeId
argument_list|,
name|reader
argument_list|,
name|note
operator|.
name|getData
argument_list|()
argument_list|,
name|draftsOnly
argument_list|)
decl_stmt|;
name|result
operator|.
name|put
argument_list|(
operator|new
name|RevId
argument_list|(
name|note
operator|.
name|name
argument_list|()
argument_list|)
argument_list|,
name|rn
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|RevisionNoteMap
argument_list|(
name|noteMap
argument_list|,
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|result
argument_list|)
argument_list|)
return|;
block|}
DECL|method|emptyMap ()
specifier|static
name|RevisionNoteMap
name|emptyMap
parameter_list|()
block|{
return|return
operator|new
name|RevisionNoteMap
argument_list|(
name|NoteMap
operator|.
name|newEmptyMap
argument_list|()
argument_list|,
name|ImmutableMap
operator|.
expr|<
name|RevId
argument_list|,
name|RevisionNote
operator|>
name|of
argument_list|()
argument_list|)
return|;
block|}
DECL|method|RevisionNoteMap (NoteMap noteMap, ImmutableMap<RevId, RevisionNote> revisionNotes)
specifier|private
name|RevisionNoteMap
parameter_list|(
name|NoteMap
name|noteMap
parameter_list|,
name|ImmutableMap
argument_list|<
name|RevId
argument_list|,
name|RevisionNote
argument_list|>
name|revisionNotes
parameter_list|)
block|{
name|this
operator|.
name|noteMap
operator|=
name|noteMap
expr_stmt|;
name|this
operator|.
name|revisionNotes
operator|=
name|revisionNotes
expr_stmt|;
block|}
block|}
end_class

end_unit

