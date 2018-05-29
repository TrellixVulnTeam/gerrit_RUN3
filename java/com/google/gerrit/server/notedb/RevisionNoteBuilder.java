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
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkArgument
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
name|CommentsUtil
operator|.
name|COMMENT_ORDER
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
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
name|ListMultimap
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
name|common
operator|.
name|collect
operator|.
name|MultimapBuilder
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
name|Comment
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
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
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
name|HashMap
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
name|Set
import|;
end_import

begin_class
DECL|class|RevisionNoteBuilder
class|class
name|RevisionNoteBuilder
block|{
DECL|class|Cache
specifier|static
class|class
name|Cache
block|{
DECL|field|revisionNoteMap
specifier|private
specifier|final
name|RevisionNoteMap
argument_list|<
name|?
extends|extends
name|RevisionNote
argument_list|<
name|?
extends|extends
name|Comment
argument_list|>
argument_list|>
name|revisionNoteMap
decl_stmt|;
DECL|field|builders
specifier|private
specifier|final
name|Map
argument_list|<
name|RevId
argument_list|,
name|RevisionNoteBuilder
argument_list|>
name|builders
decl_stmt|;
DECL|method|Cache (RevisionNoteMap<? extends RevisionNote<? extends Comment>> revisionNoteMap)
name|Cache
parameter_list|(
name|RevisionNoteMap
argument_list|<
name|?
extends|extends
name|RevisionNote
argument_list|<
name|?
extends|extends
name|Comment
argument_list|>
argument_list|>
name|revisionNoteMap
parameter_list|)
block|{
name|this
operator|.
name|revisionNoteMap
operator|=
name|revisionNoteMap
expr_stmt|;
name|this
operator|.
name|builders
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
DECL|method|get (RevId revId)
name|RevisionNoteBuilder
name|get
parameter_list|(
name|RevId
name|revId
parameter_list|)
block|{
name|RevisionNoteBuilder
name|b
init|=
name|builders
operator|.
name|get
argument_list|(
name|revId
argument_list|)
decl_stmt|;
if|if
condition|(
name|b
operator|==
literal|null
condition|)
block|{
name|b
operator|=
operator|new
name|RevisionNoteBuilder
argument_list|(
name|revisionNoteMap
operator|.
name|revisionNotes
operator|.
name|get
argument_list|(
name|revId
argument_list|)
argument_list|)
expr_stmt|;
name|builders
operator|.
name|put
argument_list|(
name|revId
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
return|return
name|b
return|;
block|}
DECL|method|getBuilders ()
name|Map
argument_list|<
name|RevId
argument_list|,
name|RevisionNoteBuilder
argument_list|>
name|getBuilders
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|builders
argument_list|)
return|;
block|}
block|}
DECL|field|baseRaw
specifier|final
name|byte
index|[]
name|baseRaw
decl_stmt|;
DECL|field|baseComments
specifier|final
name|List
argument_list|<
name|?
extends|extends
name|Comment
argument_list|>
name|baseComments
decl_stmt|;
DECL|field|put
specifier|final
name|Map
argument_list|<
name|Comment
operator|.
name|Key
argument_list|,
name|Comment
argument_list|>
name|put
decl_stmt|;
DECL|field|delete
specifier|final
name|Set
argument_list|<
name|Comment
operator|.
name|Key
argument_list|>
name|delete
decl_stmt|;
DECL|field|pushCert
specifier|private
name|String
name|pushCert
decl_stmt|;
DECL|method|RevisionNoteBuilder (RevisionNote<? extends Comment> base)
name|RevisionNoteBuilder
parameter_list|(
name|RevisionNote
argument_list|<
name|?
extends|extends
name|Comment
argument_list|>
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
name|baseRaw
operator|=
name|base
operator|.
name|getRaw
argument_list|()
expr_stmt|;
name|baseComments
operator|=
name|base
operator|.
name|getComments
argument_list|()
expr_stmt|;
name|put
operator|=
name|Maps
operator|.
name|newHashMapWithExpectedSize
argument_list|(
name|baseComments
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|base
operator|instanceof
name|ChangeRevisionNote
condition|)
block|{
name|pushCert
operator|=
operator|(
operator|(
name|ChangeRevisionNote
operator|)
name|base
operator|)
operator|.
name|getPushCert
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|baseRaw
operator|=
operator|new
name|byte
index|[
literal|0
index|]
expr_stmt|;
name|baseComments
operator|=
name|Collections
operator|.
name|emptyList
argument_list|()
expr_stmt|;
name|put
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|pushCert
operator|=
literal|null
expr_stmt|;
block|}
name|delete
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
block|}
DECL|method|build (ChangeNoteUtil noteUtil, boolean writeJson)
specifier|public
name|byte
index|[]
name|build
parameter_list|(
name|ChangeNoteUtil
name|noteUtil
parameter_list|,
name|boolean
name|writeJson
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|build
argument_list|(
name|noteUtil
operator|.
name|getChangeNoteJson
argument_list|()
argument_list|,
name|noteUtil
operator|.
name|getLegacyChangeNoteWrite
argument_list|()
argument_list|,
name|writeJson
argument_list|)
return|;
block|}
DECL|method|build ( ChangeNoteJson changeNoteJson, LegacyChangeNoteWrite legacyChangeNoteWrite, boolean writeJson)
specifier|public
name|byte
index|[]
name|build
parameter_list|(
name|ChangeNoteJson
name|changeNoteJson
parameter_list|,
name|LegacyChangeNoteWrite
name|legacyChangeNoteWrite
parameter_list|,
name|boolean
name|writeJson
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
if|if
condition|(
name|writeJson
condition|)
block|{
name|buildNoteJson
argument_list|(
name|changeNoteJson
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buildNoteLegacy
argument_list|(
name|legacyChangeNoteWrite
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
return|return
name|out
operator|.
name|toByteArray
argument_list|()
return|;
block|}
DECL|method|putComment (Comment comment)
name|void
name|putComment
parameter_list|(
name|Comment
name|comment
parameter_list|)
block|{
name|checkArgument
argument_list|(
operator|!
name|delete
operator|.
name|contains
argument_list|(
name|comment
operator|.
name|key
argument_list|)
argument_list|,
literal|"cannot both delete and put %s"
argument_list|,
name|comment
operator|.
name|key
argument_list|)
expr_stmt|;
name|put
operator|.
name|put
argument_list|(
name|comment
operator|.
name|key
argument_list|,
name|comment
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteComment (Comment.Key key)
name|void
name|deleteComment
parameter_list|(
name|Comment
operator|.
name|Key
name|key
parameter_list|)
block|{
name|checkArgument
argument_list|(
operator|!
name|put
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
argument_list|,
literal|"cannot both delete and put %s"
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|delete
operator|.
name|add
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
DECL|method|setPushCertificate (String pushCert)
name|void
name|setPushCertificate
parameter_list|(
name|String
name|pushCert
parameter_list|)
block|{
name|this
operator|.
name|pushCert
operator|=
name|pushCert
expr_stmt|;
block|}
DECL|method|buildCommentMap ()
specifier|private
name|ListMultimap
argument_list|<
name|Integer
argument_list|,
name|Comment
argument_list|>
name|buildCommentMap
parameter_list|()
block|{
name|ListMultimap
argument_list|<
name|Integer
argument_list|,
name|Comment
argument_list|>
name|all
init|=
name|MultimapBuilder
operator|.
name|hashKeys
argument_list|()
operator|.
name|arrayListValues
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
for|for
control|(
name|Comment
name|c
range|:
name|baseComments
control|)
block|{
if|if
condition|(
operator|!
name|delete
operator|.
name|contains
argument_list|(
name|c
operator|.
name|key
argument_list|)
operator|&&
operator|!
name|put
operator|.
name|containsKey
argument_list|(
name|c
operator|.
name|key
argument_list|)
condition|)
block|{
name|all
operator|.
name|put
argument_list|(
name|c
operator|.
name|key
operator|.
name|patchSetId
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Comment
name|c
range|:
name|put
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|delete
operator|.
name|contains
argument_list|(
name|c
operator|.
name|key
argument_list|)
condition|)
block|{
name|all
operator|.
name|put
argument_list|(
name|c
operator|.
name|key
operator|.
name|patchSetId
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|all
return|;
block|}
DECL|method|buildNoteJson (ChangeNoteJson noteUtil, OutputStream out)
specifier|private
name|void
name|buildNoteJson
parameter_list|(
name|ChangeNoteJson
name|noteUtil
parameter_list|,
name|OutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|ListMultimap
argument_list|<
name|Integer
argument_list|,
name|Comment
argument_list|>
name|comments
init|=
name|buildCommentMap
argument_list|()
decl_stmt|;
if|if
condition|(
name|comments
operator|.
name|isEmpty
argument_list|()
operator|&&
name|pushCert
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|RevisionNoteData
name|data
init|=
operator|new
name|RevisionNoteData
argument_list|()
decl_stmt|;
name|data
operator|.
name|comments
operator|=
name|COMMENT_ORDER
operator|.
name|sortedCopy
argument_list|(
name|comments
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
name|data
operator|.
name|pushCert
operator|=
name|pushCert
expr_stmt|;
try|try
init|(
name|OutputStreamWriter
name|osw
init|=
operator|new
name|OutputStreamWriter
argument_list|(
name|out
argument_list|,
name|UTF_8
argument_list|)
init|)
block|{
name|noteUtil
operator|.
name|getGson
argument_list|()
operator|.
name|toJson
argument_list|(
name|data
argument_list|,
name|osw
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|buildNoteLegacy (LegacyChangeNoteWrite noteUtil, OutputStream out)
specifier|private
name|void
name|buildNoteLegacy
parameter_list|(
name|LegacyChangeNoteWrite
name|noteUtil
parameter_list|,
name|OutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|pushCert
operator|!=
literal|null
condition|)
block|{
name|byte
index|[]
name|certBytes
init|=
name|pushCert
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|certBytes
argument_list|,
literal|0
argument_list|,
name|trimTrailingNewlines
argument_list|(
name|certBytes
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
name|noteUtil
operator|.
name|buildNote
argument_list|(
name|buildCommentMap
argument_list|()
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
DECL|method|trimTrailingNewlines (byte[] bytes)
specifier|private
specifier|static
name|int
name|trimTrailingNewlines
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
name|int
name|p
init|=
name|bytes
operator|.
name|length
decl_stmt|;
while|while
condition|(
name|p
operator|>
literal|1
operator|&&
name|bytes
index|[
name|p
operator|-
literal|1
index|]
operator|==
literal|'\n'
condition|)
block|{
name|p
operator|--
expr_stmt|;
block|}
return|return
name|p
return|;
block|}
block|}
end_class

end_unit

