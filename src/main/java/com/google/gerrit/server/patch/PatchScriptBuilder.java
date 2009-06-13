begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2009 The Android Open Source Project
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
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|data
operator|.
name|PatchScript
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
name|client
operator|.
name|data
operator|.
name|PatchScriptSettings
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
name|client
operator|.
name|data
operator|.
name|SparseFileContent
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
name|client
operator|.
name|patches
operator|.
name|CommentDetail
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
name|client
operator|.
name|reviewdb
operator|.
name|Patch
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
name|client
operator|.
name|reviewdb
operator|.
name|PatchLineComment
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
name|client
operator|.
name|rpc
operator|.
name|CorruptEntityException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|diff
operator|.
name|Edit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|errors
operator|.
name|MissingObjectException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Constants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
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
name|spearce
operator|.
name|jgit
operator|.
name|lib
operator|.
name|ObjectLoader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Repository
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|patch
operator|.
name|CombinedFileHeader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|patch
operator|.
name|FileHeader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|util
operator|.
name|IntList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|util
operator|.
name|RawParseUtils
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
name|ArrayList
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
name|Comparator
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
DECL|class|PatchScriptBuilder
class|class
name|PatchScriptBuilder
block|{
DECL|field|MAX_CONTEXT
specifier|static
specifier|final
name|int
name|MAX_CONTEXT
init|=
literal|5000000
decl_stmt|;
DECL|field|BIG_FILE
specifier|static
specifier|final
name|int
name|BIG_FILE
init|=
literal|9000
decl_stmt|;
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|PatchScriptBuilder
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|EDIT_SORT
specifier|private
specifier|static
specifier|final
name|Comparator
argument_list|<
name|Edit
argument_list|>
name|EDIT_SORT
init|=
operator|new
name|Comparator
argument_list|<
name|Edit
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
specifier|final
name|Edit
name|o1
parameter_list|,
specifier|final
name|Edit
name|o2
parameter_list|)
block|{
return|return
name|o1
operator|.
name|getBeginA
argument_list|()
operator|-
name|o2
operator|.
name|getBeginA
argument_list|()
return|;
block|}
block|}
decl_stmt|;
DECL|field|header
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|header
decl_stmt|;
DECL|field|dstA
specifier|private
specifier|final
name|SparseFileContent
name|dstA
decl_stmt|;
DECL|field|dstB
specifier|private
specifier|final
name|SparseFileContent
name|dstB
decl_stmt|;
DECL|field|db
specifier|private
name|Repository
name|db
decl_stmt|;
DECL|field|patch
specifier|private
name|Patch
name|patch
decl_stmt|;
DECL|field|patchKey
specifier|private
name|Patch
operator|.
name|Key
name|patchKey
decl_stmt|;
DECL|field|settings
specifier|private
name|PatchScriptSettings
name|settings
decl_stmt|;
DECL|field|srcA
specifier|private
name|Text
name|srcA
decl_stmt|;
DECL|field|srcB
specifier|private
name|Text
name|srcB
decl_stmt|;
DECL|field|edits
specifier|private
name|List
argument_list|<
name|Edit
argument_list|>
name|edits
decl_stmt|;
DECL|method|PatchScriptBuilder ()
name|PatchScriptBuilder
parameter_list|()
block|{
name|header
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|dstA
operator|=
operator|new
name|SparseFileContent
argument_list|()
expr_stmt|;
name|dstB
operator|=
operator|new
name|SparseFileContent
argument_list|()
expr_stmt|;
block|}
DECL|method|setRepository (final Repository r)
name|void
name|setRepository
parameter_list|(
specifier|final
name|Repository
name|r
parameter_list|)
block|{
name|db
operator|=
name|r
expr_stmt|;
block|}
DECL|method|setPatch (final Patch p)
name|void
name|setPatch
parameter_list|(
specifier|final
name|Patch
name|p
parameter_list|)
block|{
name|patch
operator|=
name|p
expr_stmt|;
name|patchKey
operator|=
name|patch
operator|.
name|getKey
argument_list|()
expr_stmt|;
block|}
DECL|method|setSettings (final PatchScriptSettings s)
name|void
name|setSettings
parameter_list|(
specifier|final
name|PatchScriptSettings
name|s
parameter_list|)
block|{
name|settings
operator|=
name|s
expr_stmt|;
block|}
DECL|method|context ()
specifier|private
name|int
name|context
parameter_list|()
block|{
return|return
name|settings
operator|.
name|getContext
argument_list|()
return|;
block|}
DECL|method|toPatchScript (final DiffCacheContent content, final CommentDetail comments)
name|PatchScript
name|toPatchScript
parameter_list|(
specifier|final
name|DiffCacheContent
name|content
parameter_list|,
specifier|final
name|CommentDetail
name|comments
parameter_list|)
throws|throws
name|CorruptEntityException
block|{
specifier|final
name|FileHeader
name|fh
init|=
name|content
operator|.
name|getFileHeader
argument_list|()
decl_stmt|;
if|if
condition|(
name|fh
operator|instanceof
name|CombinedFileHeader
condition|)
block|{
comment|// For a diff --cc format we don't support converting it into
comment|// a patch script. Instead treat everything as a file header.
comment|//
name|edits
operator|=
name|Collections
operator|.
name|emptyList
argument_list|()
expr_stmt|;
name|packHeader
argument_list|(
name|fh
argument_list|)
expr_stmt|;
return|return
operator|new
name|PatchScript
argument_list|(
name|header
argument_list|,
name|settings
argument_list|,
name|dstA
argument_list|,
name|dstB
argument_list|,
name|edits
argument_list|)
return|;
block|}
name|srcA
operator|=
name|open
argument_list|(
name|content
operator|.
name|getOldId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|eq
argument_list|(
name|content
operator|.
name|getOldId
argument_list|()
argument_list|,
name|content
operator|.
name|getNewId
argument_list|()
argument_list|)
condition|)
block|{
name|srcB
operator|=
name|srcA
expr_stmt|;
block|}
else|else
block|{
name|srcB
operator|=
name|open
argument_list|(
name|content
operator|.
name|getNewId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|edits
operator|=
name|content
operator|.
name|getEdits
argument_list|()
expr_stmt|;
name|ensureCommentsVisible
argument_list|(
name|comments
argument_list|)
expr_stmt|;
name|dstA
operator|.
name|setMissingNewlineAtEnd
argument_list|(
name|srcA
operator|.
name|isMissingNewlineAtEnd
argument_list|()
argument_list|)
expr_stmt|;
name|dstB
operator|.
name|setMissingNewlineAtEnd
argument_list|(
name|srcA
operator|.
name|isMissingNewlineAtEnd
argument_list|()
argument_list|)
expr_stmt|;
name|dstA
operator|.
name|setSize
argument_list|(
name|srcA
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|dstB
operator|.
name|setSize
argument_list|(
name|srcB
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|fh
operator|!=
literal|null
condition|)
block|{
name|packHeader
argument_list|(
name|fh
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|srcA
operator|==
name|srcB
operator|&&
name|srcA
operator|.
name|size
argument_list|()
operator|<=
name|context
argument_list|()
operator|&&
name|edits
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// Odd special case; the files are identical (100% rename or copy)
comment|// and the user has asked for context that is larger than the file.
comment|// Send them the entire file, with an empty edit after the last line.
comment|//
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|srcA
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|srcA
operator|.
name|addLineTo
argument_list|(
name|dstA
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
name|edits
operator|=
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|Edit
argument_list|(
name|srcA
operator|.
name|size
argument_list|()
argument_list|,
name|srcA
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|BIG_FILE
operator|<
name|Math
operator|.
name|max
argument_list|(
name|srcA
operator|.
name|size
argument_list|()
argument_list|,
name|srcB
operator|.
name|size
argument_list|()
argument_list|)
operator|&&
literal|25
operator|<
name|context
argument_list|()
condition|)
block|{
name|settings
operator|.
name|setContext
argument_list|(
literal|25
argument_list|)
expr_stmt|;
block|}
name|packContent
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|PatchScript
argument_list|(
name|header
argument_list|,
name|settings
argument_list|,
name|dstA
argument_list|,
name|dstB
argument_list|,
name|edits
argument_list|)
return|;
block|}
DECL|method|ensureCommentsVisible (final CommentDetail comments)
specifier|private
name|void
name|ensureCommentsVisible
parameter_list|(
specifier|final
name|CommentDetail
name|comments
parameter_list|)
block|{
if|if
condition|(
name|comments
operator|.
name|getCommentsA
argument_list|()
operator|.
name|isEmpty
argument_list|()
operator|&&
name|comments
operator|.
name|getCommentsB
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// No comments, no additional dummy edits are required.
comment|//
return|return;
block|}
comment|// Construct empty Edit blocks around each location where a comment is.
comment|// This will force the later packContent method to include the regions
comment|// containing comments, potentially combining those regions together if
comment|// they have overlapping contexts. UI renders will also be able to make
comment|// correct hunks from this, but because the Edit is empty they will not
comment|// style it specially.
comment|//
specifier|final
name|List
argument_list|<
name|Edit
argument_list|>
name|empty
init|=
operator|new
name|ArrayList
argument_list|<
name|Edit
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|lastLine
decl_stmt|;
name|lastLine
operator|=
operator|-
literal|1
expr_stmt|;
for|for
control|(
name|PatchLineComment
name|plc
range|:
name|comments
operator|.
name|getCommentsA
argument_list|()
control|)
block|{
specifier|final
name|int
name|a
init|=
name|plc
operator|.
name|getLine
argument_list|()
decl_stmt|;
if|if
condition|(
name|lastLine
operator|!=
name|a
condition|)
block|{
name|safeAdd
argument_list|(
name|empty
argument_list|,
operator|new
name|Edit
argument_list|(
name|a
operator|-
literal|1
argument_list|,
name|mapA2B
argument_list|(
name|a
operator|-
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|lastLine
operator|=
name|a
expr_stmt|;
block|}
block|}
name|lastLine
operator|=
operator|-
literal|1
expr_stmt|;
for|for
control|(
name|PatchLineComment
name|plc
range|:
name|comments
operator|.
name|getCommentsB
argument_list|()
control|)
block|{
specifier|final
name|int
name|b
init|=
name|plc
operator|.
name|getLine
argument_list|()
decl_stmt|;
if|if
condition|(
name|lastLine
operator|!=
name|b
condition|)
block|{
name|safeAdd
argument_list|(
name|empty
argument_list|,
operator|new
name|Edit
argument_list|(
name|mapB2A
argument_list|(
name|b
operator|-
literal|1
argument_list|)
argument_list|,
name|b
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|lastLine
operator|=
name|b
expr_stmt|;
block|}
block|}
comment|// Build the final list as a copy, as we cannot modify the cached
comment|// edit list we started out with. Also sort the final list by the
comment|// index in A, so packContent can combine them correctly later.
comment|//
specifier|final
name|List
argument_list|<
name|Edit
argument_list|>
name|n
init|=
operator|new
name|ArrayList
argument_list|<
name|Edit
argument_list|>
argument_list|(
name|edits
operator|.
name|size
argument_list|()
operator|+
name|empty
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|n
operator|.
name|addAll
argument_list|(
name|edits
argument_list|)
expr_stmt|;
name|n
operator|.
name|addAll
argument_list|(
name|empty
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|n
argument_list|,
name|EDIT_SORT
argument_list|)
expr_stmt|;
name|edits
operator|=
name|n
expr_stmt|;
block|}
DECL|method|safeAdd (final List<Edit> empty, final Edit toAdd)
specifier|private
name|void
name|safeAdd
parameter_list|(
specifier|final
name|List
argument_list|<
name|Edit
argument_list|>
name|empty
parameter_list|,
specifier|final
name|Edit
name|toAdd
parameter_list|)
block|{
specifier|final
name|int
name|a
init|=
name|toAdd
operator|.
name|getBeginA
argument_list|()
decl_stmt|;
specifier|final
name|int
name|b
init|=
name|toAdd
operator|.
name|getBeginB
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Edit
name|e
range|:
name|edits
control|)
block|{
if|if
condition|(
name|e
operator|.
name|getBeginA
argument_list|()
operator|<=
name|a
operator|&&
name|a
operator|<=
name|e
operator|.
name|getEndA
argument_list|()
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|e
operator|.
name|getBeginB
argument_list|()
operator|<=
name|b
operator|&&
name|b
operator|<=
name|e
operator|.
name|getEndB
argument_list|()
condition|)
block|{
return|return;
block|}
block|}
name|empty
operator|.
name|add
argument_list|(
name|toAdd
argument_list|)
expr_stmt|;
block|}
DECL|method|mapA2B (final int a)
specifier|private
name|int
name|mapA2B
parameter_list|(
specifier|final
name|int
name|a
parameter_list|)
block|{
if|if
condition|(
name|edits
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// Magic special case of an unmodified file.
comment|//
return|return
name|a
return|;
block|}
if|if
condition|(
name|a
operator|<
name|edits
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getBeginA
argument_list|()
condition|)
block|{
comment|// Special case of context at start of file.
comment|//
return|return
name|a
return|;
block|}
for|for
control|(
name|Edit
name|e
range|:
name|edits
control|)
block|{
if|if
condition|(
name|e
operator|.
name|getBeginA
argument_list|()
operator|<=
name|a
operator|&&
name|a
operator|<=
name|e
operator|.
name|getEndA
argument_list|()
condition|)
block|{
return|return
name|e
operator|.
name|getBeginB
argument_list|()
operator|+
operator|(
name|a
operator|-
name|e
operator|.
name|getBeginA
argument_list|()
operator|)
return|;
block|}
block|}
specifier|final
name|Edit
name|last
init|=
name|edits
operator|.
name|get
argument_list|(
name|edits
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
return|return
name|last
operator|.
name|getBeginB
argument_list|()
operator|+
operator|(
name|a
operator|-
name|last
operator|.
name|getEndA
argument_list|()
operator|)
return|;
block|}
DECL|method|mapB2A (final int b)
specifier|private
name|int
name|mapB2A
parameter_list|(
specifier|final
name|int
name|b
parameter_list|)
block|{
if|if
condition|(
name|edits
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// Magic special case of an unmodified file.
comment|//
return|return
name|b
return|;
block|}
if|if
condition|(
name|b
operator|<
name|edits
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getBeginB
argument_list|()
condition|)
block|{
comment|// Special case of context at start of file.
comment|//
return|return
name|b
return|;
block|}
for|for
control|(
name|Edit
name|e
range|:
name|edits
control|)
block|{
if|if
condition|(
name|e
operator|.
name|getBeginB
argument_list|()
operator|<=
name|b
operator|&&
name|b
operator|<=
name|e
operator|.
name|getEndB
argument_list|()
condition|)
block|{
return|return
name|e
operator|.
name|getBeginA
argument_list|()
operator|+
operator|(
name|b
operator|-
name|e
operator|.
name|getBeginB
argument_list|()
operator|)
return|;
block|}
block|}
specifier|final
name|Edit
name|last
init|=
name|edits
operator|.
name|get
argument_list|(
name|edits
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
return|return
name|last
operator|.
name|getBeginA
argument_list|()
operator|+
operator|(
name|b
operator|-
name|last
operator|.
name|getEndB
argument_list|()
operator|)
return|;
block|}
DECL|method|eq (final ObjectId a, final ObjectId b)
specifier|private
specifier|static
name|boolean
name|eq
parameter_list|(
specifier|final
name|ObjectId
name|a
parameter_list|,
specifier|final
name|ObjectId
name|b
parameter_list|)
block|{
if|if
condition|(
name|a
operator|==
literal|null
operator|&&
name|b
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
name|a
operator|!=
literal|null
operator|&&
name|b
operator|!=
literal|null
condition|?
name|a
operator|.
name|equals
argument_list|(
name|b
argument_list|)
else|:
literal|false
return|;
block|}
DECL|method|open (final ObjectId id)
specifier|private
name|Text
name|open
parameter_list|(
specifier|final
name|ObjectId
name|id
parameter_list|)
throws|throws
name|CorruptEntityException
block|{
if|if
condition|(
name|id
operator|==
literal|null
condition|)
block|{
return|return
name|Text
operator|.
name|EMPTY
return|;
block|}
try|try
block|{
specifier|final
name|ObjectLoader
name|ldr
init|=
name|db
operator|.
name|openObject
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|ldr
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|MissingObjectException
argument_list|(
name|id
argument_list|,
name|Constants
operator|.
name|TYPE_BLOB
argument_list|)
throw|;
block|}
return|return
operator|new
name|Text
argument_list|(
name|ldr
operator|.
name|getCachedBytes
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"In "
operator|+
name|patchKey
operator|+
literal|" blob "
operator|+
name|id
operator|.
name|name
argument_list|()
operator|+
literal|" gone"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|CorruptEntityException
argument_list|(
name|patchKey
argument_list|)
throw|;
block|}
block|}
DECL|method|packHeader (final FileHeader fh)
specifier|private
name|void
name|packHeader
parameter_list|(
specifier|final
name|FileHeader
name|fh
parameter_list|)
block|{
specifier|final
name|byte
index|[]
name|buf
init|=
name|fh
operator|.
name|getBuffer
argument_list|()
decl_stmt|;
specifier|final
name|IntList
name|m
init|=
name|RawParseUtils
operator|.
name|lineMap
argument_list|(
name|buf
argument_list|,
name|fh
operator|.
name|getStartOffset
argument_list|()
argument_list|,
name|end
argument_list|(
name|fh
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|m
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|b
init|=
name|m
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|int
name|e
init|=
name|m
operator|.
name|get
argument_list|(
name|i
operator|+
literal|1
argument_list|)
decl_stmt|;
name|header
operator|.
name|add
argument_list|(
name|RawParseUtils
operator|.
name|decode
argument_list|(
name|Constants
operator|.
name|CHARSET
argument_list|,
name|buf
argument_list|,
name|b
argument_list|,
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|packContent ()
specifier|private
name|void
name|packContent
parameter_list|()
block|{
for|for
control|(
name|int
name|curIdx
init|=
literal|0
init|;
name|curIdx
operator|<
name|edits
operator|.
name|size
argument_list|()
condition|;
control|)
block|{
name|Edit
name|curEdit
init|=
name|edits
operator|.
name|get
argument_list|(
name|curIdx
argument_list|)
decl_stmt|;
specifier|final
name|int
name|endIdx
init|=
name|findCombinedEnd
argument_list|(
name|edits
argument_list|,
name|curIdx
argument_list|)
decl_stmt|;
specifier|final
name|Edit
name|endEdit
init|=
name|edits
operator|.
name|get
argument_list|(
name|endIdx
argument_list|)
decl_stmt|;
name|int
name|aCur
init|=
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|curEdit
operator|.
name|getBeginA
argument_list|()
operator|-
name|context
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|bCur
init|=
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|curEdit
operator|.
name|getBeginB
argument_list|()
operator|-
name|context
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|int
name|aEnd
init|=
name|Math
operator|.
name|min
argument_list|(
name|srcA
operator|.
name|size
argument_list|()
argument_list|,
name|endEdit
operator|.
name|getEndA
argument_list|()
operator|+
name|context
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|int
name|bEnd
init|=
name|Math
operator|.
name|min
argument_list|(
name|srcB
operator|.
name|size
argument_list|()
argument_list|,
name|endEdit
operator|.
name|getEndB
argument_list|()
operator|+
name|context
argument_list|()
argument_list|)
decl_stmt|;
while|while
condition|(
name|aCur
operator|<
name|aEnd
operator|||
name|bCur
operator|<
name|bEnd
condition|)
block|{
if|if
condition|(
name|aCur
operator|<
name|curEdit
operator|.
name|getBeginA
argument_list|()
operator|||
name|endIdx
operator|+
literal|1
operator|<
name|curIdx
condition|)
block|{
name|srcA
operator|.
name|addLineTo
argument_list|(
name|dstA
argument_list|,
name|aCur
argument_list|)
expr_stmt|;
name|aCur
operator|++
expr_stmt|;
name|bCur
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|aCur
operator|<
name|curEdit
operator|.
name|getEndA
argument_list|()
condition|)
block|{
name|srcA
operator|.
name|addLineTo
argument_list|(
name|dstA
argument_list|,
name|aCur
operator|++
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|bCur
operator|<
name|curEdit
operator|.
name|getEndB
argument_list|()
condition|)
block|{
name|srcB
operator|.
name|addLineTo
argument_list|(
name|dstB
argument_list|,
name|bCur
operator|++
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|end
argument_list|(
name|curEdit
argument_list|,
name|aCur
argument_list|,
name|bCur
argument_list|)
operator|&&
operator|++
name|curIdx
operator|<
name|edits
operator|.
name|size
argument_list|()
condition|)
name|curEdit
operator|=
name|edits
operator|.
name|get
argument_list|(
name|curIdx
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|findCombinedEnd (final List<Edit> edits, final int i)
specifier|private
name|int
name|findCombinedEnd
parameter_list|(
specifier|final
name|List
argument_list|<
name|Edit
argument_list|>
name|edits
parameter_list|,
specifier|final
name|int
name|i
parameter_list|)
block|{
name|int
name|end
init|=
name|i
operator|+
literal|1
decl_stmt|;
while|while
condition|(
name|end
operator|<
name|edits
operator|.
name|size
argument_list|()
operator|&&
operator|(
name|combineA
argument_list|(
name|edits
argument_list|,
name|end
argument_list|)
operator|||
name|combineB
argument_list|(
name|edits
argument_list|,
name|end
argument_list|)
operator|)
condition|)
name|end
operator|++
expr_stmt|;
return|return
name|end
operator|-
literal|1
return|;
block|}
DECL|method|combineA (final List<Edit> e, final int i)
specifier|private
name|boolean
name|combineA
parameter_list|(
specifier|final
name|List
argument_list|<
name|Edit
argument_list|>
name|e
parameter_list|,
specifier|final
name|int
name|i
parameter_list|)
block|{
return|return
name|e
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getBeginA
argument_list|()
operator|-
name|e
operator|.
name|get
argument_list|(
name|i
operator|-
literal|1
argument_list|)
operator|.
name|getEndA
argument_list|()
operator|<=
literal|2
operator|*
name|context
argument_list|()
return|;
block|}
DECL|method|combineB (final List<Edit> e, final int i)
specifier|private
name|boolean
name|combineB
parameter_list|(
specifier|final
name|List
argument_list|<
name|Edit
argument_list|>
name|e
parameter_list|,
specifier|final
name|int
name|i
parameter_list|)
block|{
return|return
name|e
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getBeginB
argument_list|()
operator|-
name|e
operator|.
name|get
argument_list|(
name|i
operator|-
literal|1
argument_list|)
operator|.
name|getEndB
argument_list|()
operator|<=
literal|2
operator|*
name|context
argument_list|()
return|;
block|}
DECL|method|end (final Edit edit, final int a, final int b)
specifier|private
specifier|static
name|boolean
name|end
parameter_list|(
specifier|final
name|Edit
name|edit
parameter_list|,
specifier|final
name|int
name|a
parameter_list|,
specifier|final
name|int
name|b
parameter_list|)
block|{
return|return
name|edit
operator|.
name|getEndA
argument_list|()
operator|<=
name|a
operator|&&
name|edit
operator|.
name|getEndB
argument_list|()
operator|<=
name|b
return|;
block|}
DECL|method|end (final FileHeader h)
specifier|private
specifier|static
name|int
name|end
parameter_list|(
specifier|final
name|FileHeader
name|h
parameter_list|)
block|{
if|if
condition|(
name|h
operator|instanceof
name|CombinedFileHeader
condition|)
block|{
return|return
name|h
operator|.
name|getEndOffset
argument_list|()
return|;
block|}
if|if
condition|(
operator|!
name|h
operator|.
name|getHunks
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|h
operator|.
name|getHunks
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStartOffset
argument_list|()
return|;
block|}
return|return
name|h
operator|.
name|getEndOffset
argument_list|()
return|;
block|}
block|}
end_class

end_unit

