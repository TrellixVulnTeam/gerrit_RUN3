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
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|PatchLineCommentsUtil
operator|.
name|PLC_ORDER
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
name|Maps
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
name|PatchLineComment
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
DECL|class|RevisionNoteBuilder
class|class
name|RevisionNoteBuilder
block|{
DECL|field|comments
specifier|private
specifier|final
name|Map
argument_list|<
name|PatchLineComment
operator|.
name|Key
argument_list|,
name|PatchLineComment
argument_list|>
name|comments
decl_stmt|;
DECL|method|RevisionNoteBuilder (RevisionNote base)
name|RevisionNoteBuilder
parameter_list|(
name|RevisionNote
name|base
parameter_list|)
block|{
if|if
condition|(
name|base
operator|!=
literal|null
condition|)
block|{
name|comments
operator|=
name|Maps
operator|.
name|newHashMapWithExpectedSize
argument_list|(
name|base
operator|.
name|comments
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|PatchLineComment
name|c
range|:
name|base
operator|.
name|comments
control|)
block|{
name|addComment
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|comments
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|addComment (PatchLineComment comment)
name|void
name|addComment
parameter_list|(
name|PatchLineComment
name|comment
parameter_list|)
block|{
name|comments
operator|.
name|put
argument_list|(
name|comment
operator|.
name|getKey
argument_list|()
argument_list|,
name|comment
argument_list|)
expr_stmt|;
block|}
DECL|method|build (CommentsInNotesUtil commentsUtil)
name|byte
index|[]
name|build
parameter_list|(
name|CommentsInNotesUtil
name|commentsUtil
parameter_list|)
block|{
return|return
name|commentsUtil
operator|.
name|buildNote
argument_list|(
name|PLC_ORDER
operator|.
name|sortedCopy
argument_list|(
name|comments
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

