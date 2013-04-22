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
DECL|package|com.google.gerrit.httpd.rpc.patch
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|httpd
operator|.
name|rpc
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
name|common
operator|.
name|data
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
name|common
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
name|common
operator|.
name|data
operator|.
name|PatchScript
operator|.
name|DisplayMethod
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
name|prettify
operator|.
name|common
operator|.
name|EditList
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
name|prettify
operator|.
name|common
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
name|reviewdb
operator|.
name|client
operator|.
name|AccountDiffPreference
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
name|reviewdb
operator|.
name|client
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
name|reviewdb
operator|.
name|client
operator|.
name|Project
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
name|AccountDiffPreference
operator|.
name|Whitespace
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
name|FileTypeRegistry
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
name|patch
operator|.
name|IntraLineDiff
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
name|patch
operator|.
name|IntraLineDiffKey
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
name|patch
operator|.
name|PatchListCache
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
name|patch
operator|.
name|PatchListEntry
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
name|patch
operator|.
name|Text
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_import
import|import
name|eu
operator|.
name|medsea
operator|.
name|mimeutil
operator|.
name|MimeType
import|;
end_import

begin_import
import|import
name|eu
operator|.
name|medsea
operator|.
name|mimeutil
operator|.
name|MimeUtil2
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
name|diff
operator|.
name|Edit
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
name|CorruptObjectException
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
name|IncorrectObjectTypeException
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
name|MissingObjectException
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
name|Constants
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
name|FileMode
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
name|lib
operator|.
name|Repository
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
name|revwalk
operator|.
name|RevTree
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
name|revwalk
operator|.
name|RevWalk
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
name|treewalk
operator|.
name|TreeWalk
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
DECL|field|db
specifier|private
name|Repository
name|db
decl_stmt|;
DECL|field|projectKey
specifier|private
name|Project
operator|.
name|NameKey
name|projectKey
decl_stmt|;
DECL|field|reader
specifier|private
name|ObjectReader
name|reader
decl_stmt|;
DECL|field|change
specifier|private
name|Change
name|change
decl_stmt|;
DECL|field|diffPrefs
specifier|private
name|AccountDiffPreference
name|diffPrefs
decl_stmt|;
DECL|field|againstParent
specifier|private
name|boolean
name|againstParent
decl_stmt|;
DECL|field|aId
specifier|private
name|ObjectId
name|aId
decl_stmt|;
DECL|field|bId
specifier|private
name|ObjectId
name|bId
decl_stmt|;
DECL|field|a
specifier|private
specifier|final
name|Side
name|a
decl_stmt|;
DECL|field|b
specifier|private
specifier|final
name|Side
name|b
decl_stmt|;
DECL|field|edits
specifier|private
name|List
argument_list|<
name|Edit
argument_list|>
name|edits
decl_stmt|;
DECL|field|registry
specifier|private
specifier|final
name|FileTypeRegistry
name|registry
decl_stmt|;
DECL|field|patchListCache
specifier|private
specifier|final
name|PatchListCache
name|patchListCache
decl_stmt|;
DECL|field|context
specifier|private
name|int
name|context
decl_stmt|;
annotation|@
name|Inject
DECL|method|PatchScriptBuilder (final FileTypeRegistry ftr, final PatchListCache plc)
name|PatchScriptBuilder
parameter_list|(
specifier|final
name|FileTypeRegistry
name|ftr
parameter_list|,
specifier|final
name|PatchListCache
name|plc
parameter_list|)
block|{
name|a
operator|=
operator|new
name|Side
argument_list|()
expr_stmt|;
name|b
operator|=
operator|new
name|Side
argument_list|()
expr_stmt|;
name|registry
operator|=
name|ftr
expr_stmt|;
name|patchListCache
operator|=
name|plc
expr_stmt|;
block|}
DECL|method|setRepository (Repository r, Project.NameKey projectKey)
name|void
name|setRepository
parameter_list|(
name|Repository
name|r
parameter_list|,
name|Project
operator|.
name|NameKey
name|projectKey
parameter_list|)
block|{
name|this
operator|.
name|db
operator|=
name|r
expr_stmt|;
name|this
operator|.
name|projectKey
operator|=
name|projectKey
expr_stmt|;
block|}
DECL|method|setChange (final Change c)
name|void
name|setChange
parameter_list|(
specifier|final
name|Change
name|c
parameter_list|)
block|{
name|this
operator|.
name|change
operator|=
name|c
expr_stmt|;
block|}
DECL|method|setDiffPrefs (final AccountDiffPreference dp)
name|void
name|setDiffPrefs
parameter_list|(
specifier|final
name|AccountDiffPreference
name|dp
parameter_list|)
block|{
name|diffPrefs
operator|=
name|dp
expr_stmt|;
name|context
operator|=
name|diffPrefs
operator|.
name|getContext
argument_list|()
expr_stmt|;
if|if
condition|(
name|context
operator|==
name|AccountDiffPreference
operator|.
name|WHOLE_FILE_CONTEXT
condition|)
block|{
name|context
operator|=
name|MAX_CONTEXT
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|context
operator|>
name|MAX_CONTEXT
condition|)
block|{
name|context
operator|=
name|MAX_CONTEXT
expr_stmt|;
block|}
block|}
DECL|method|setTrees (final boolean ap, final ObjectId a, final ObjectId b)
name|void
name|setTrees
parameter_list|(
specifier|final
name|boolean
name|ap
parameter_list|,
specifier|final
name|ObjectId
name|a
parameter_list|,
specifier|final
name|ObjectId
name|b
parameter_list|)
block|{
name|againstParent
operator|=
name|ap
expr_stmt|;
name|aId
operator|=
name|a
expr_stmt|;
name|bId
operator|=
name|b
expr_stmt|;
block|}
DECL|method|toPatchScript (final PatchListEntry content, final CommentDetail comments, final List<Patch> history)
name|PatchScript
name|toPatchScript
parameter_list|(
specifier|final
name|PatchListEntry
name|content
parameter_list|,
specifier|final
name|CommentDetail
name|comments
parameter_list|,
specifier|final
name|List
argument_list|<
name|Patch
argument_list|>
name|history
parameter_list|)
throws|throws
name|IOException
block|{
name|reader
operator|=
name|db
operator|.
name|newObjectReader
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|build
argument_list|(
name|content
argument_list|,
name|comments
argument_list|,
name|history
argument_list|)
return|;
block|}
finally|finally
block|{
name|reader
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|build (final PatchListEntry content, final CommentDetail comments, final List<Patch> history)
specifier|private
name|PatchScript
name|build
parameter_list|(
specifier|final
name|PatchListEntry
name|content
parameter_list|,
specifier|final
name|CommentDetail
name|comments
parameter_list|,
specifier|final
name|List
argument_list|<
name|Patch
argument_list|>
name|history
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|intralineDifferenceIsPossible
init|=
literal|true
decl_stmt|;
name|boolean
name|intralineFailure
init|=
literal|false
decl_stmt|;
name|boolean
name|intralineTimeout
init|=
literal|false
decl_stmt|;
name|a
operator|.
name|path
operator|=
name|oldName
argument_list|(
name|content
argument_list|)
expr_stmt|;
name|b
operator|.
name|path
operator|=
name|newName
argument_list|(
name|content
argument_list|)
expr_stmt|;
name|a
operator|.
name|resolve
argument_list|(
literal|null
argument_list|,
name|aId
argument_list|)
expr_stmt|;
name|b
operator|.
name|resolve
argument_list|(
name|a
argument_list|,
name|bId
argument_list|)
expr_stmt|;
name|edits
operator|=
operator|new
name|ArrayList
argument_list|<
name|Edit
argument_list|>
argument_list|(
name|content
operator|.
name|getEdits
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|isModify
argument_list|(
name|content
argument_list|)
condition|)
block|{
name|intralineDifferenceIsPossible
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|diffPrefs
operator|.
name|isIntralineDifference
argument_list|()
condition|)
block|{
name|IntraLineDiff
name|d
init|=
name|patchListCache
operator|.
name|getIntraLineDiff
argument_list|(
operator|new
name|IntraLineDiffKey
argument_list|(
name|a
operator|.
name|id
argument_list|,
name|a
operator|.
name|src
argument_list|,
name|b
operator|.
name|id
argument_list|,
name|b
operator|.
name|src
argument_list|,
name|edits
argument_list|,
name|projectKey
argument_list|,
name|bId
argument_list|,
name|b
operator|.
name|path
argument_list|,
name|diffPrefs
operator|.
name|getIgnoreWhitespace
argument_list|()
operator|!=
name|Whitespace
operator|.
name|IGNORE_NONE
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|d
operator|!=
literal|null
condition|)
block|{
switch|switch
condition|(
name|d
operator|.
name|getStatus
argument_list|()
condition|)
block|{
case|case
name|EDIT_LIST
case|:
name|edits
operator|=
operator|new
name|ArrayList
argument_list|<
name|Edit
argument_list|>
argument_list|(
name|d
operator|.
name|getEdits
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|DISABLED
case|:
name|intralineDifferenceIsPossible
operator|=
literal|false
expr_stmt|;
break|break;
case|case
name|ERROR
case|:
name|intralineDifferenceIsPossible
operator|=
literal|false
expr_stmt|;
name|intralineFailure
operator|=
literal|true
expr_stmt|;
break|break;
case|case
name|TIMEOUT
case|:
name|intralineDifferenceIsPossible
operator|=
literal|false
expr_stmt|;
name|intralineTimeout
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
else|else
block|{
name|intralineDifferenceIsPossible
operator|=
literal|false
expr_stmt|;
name|intralineFailure
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|ensureCommentsVisible
argument_list|(
name|comments
argument_list|)
expr_stmt|;
name|boolean
name|hugeFile
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|a
operator|.
name|mode
operator|==
name|FileMode
operator|.
name|GITLINK
operator|||
name|b
operator|.
name|mode
operator|==
name|FileMode
operator|.
name|GITLINK
condition|)
block|{      }
elseif|else
if|if
condition|(
name|a
operator|.
name|src
operator|==
name|b
operator|.
name|src
operator|&&
name|a
operator|.
name|size
argument_list|()
operator|<=
name|context
operator|&&
name|content
operator|.
name|getEdits
argument_list|()
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
name|a
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|a
operator|.
name|addLine
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|edits
operator|=
operator|new
name|ArrayList
argument_list|<
name|Edit
argument_list|>
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|edits
operator|.
name|add
argument_list|(
operator|new
name|Edit
argument_list|(
name|a
operator|.
name|size
argument_list|()
argument_list|,
name|a
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
name|a
operator|.
name|size
argument_list|()
argument_list|,
name|b
operator|.
name|size
argument_list|()
argument_list|)
condition|)
block|{
comment|// IF the file is really large, we disable things to avoid choking
comment|// the browser client.
comment|//
name|diffPrefs
operator|.
name|setContext
argument_list|(
operator|(
name|short
operator|)
name|Math
operator|.
name|min
argument_list|(
literal|25
argument_list|,
name|context
argument_list|)
argument_list|)
expr_stmt|;
name|diffPrefs
operator|.
name|setSyntaxHighlighting
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|context
operator|=
name|diffPrefs
operator|.
name|getContext
argument_list|()
expr_stmt|;
name|hugeFile
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
comment|// In order to expand the skipped common lines or syntax highlight the
comment|// file properly we need to give the client the complete file contents.
comment|// So force our context temporarily to the complete file size.
comment|//
name|context
operator|=
name|MAX_CONTEXT
expr_stmt|;
block|}
name|packContent
argument_list|(
name|diffPrefs
operator|.
name|getIgnoreWhitespace
argument_list|()
operator|!=
name|Whitespace
operator|.
name|IGNORE_NONE
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|PatchScript
argument_list|(
name|change
operator|.
name|getKey
argument_list|()
argument_list|,
name|content
operator|.
name|getChangeType
argument_list|()
argument_list|,
name|content
operator|.
name|getOldName
argument_list|()
argument_list|,
name|content
operator|.
name|getNewName
argument_list|()
argument_list|,
name|a
operator|.
name|fileMode
argument_list|,
name|b
operator|.
name|fileMode
argument_list|,
name|content
operator|.
name|getHeaderLines
argument_list|()
argument_list|,
name|diffPrefs
argument_list|,
name|a
operator|.
name|dst
argument_list|,
name|b
operator|.
name|dst
argument_list|,
name|edits
argument_list|,
name|a
operator|.
name|displayMethod
argument_list|,
name|b
operator|.
name|displayMethod
argument_list|,
name|comments
argument_list|,
name|history
argument_list|,
name|hugeFile
argument_list|,
name|intralineDifferenceIsPossible
argument_list|,
name|intralineFailure
argument_list|,
name|intralineTimeout
argument_list|)
return|;
block|}
DECL|method|isModify (PatchListEntry content)
specifier|private
specifier|static
name|boolean
name|isModify
parameter_list|(
name|PatchListEntry
name|content
parameter_list|)
block|{
switch|switch
condition|(
name|content
operator|.
name|getChangeType
argument_list|()
condition|)
block|{
case|case
name|MODIFIED
case|:
case|case
name|COPIED
case|:
case|case
name|RENAMED
case|:
return|return
literal|true
return|;
case|case
name|ADDED
case|:
case|case
name|DELETED
case|:
default|default:
return|return
literal|false
return|;
block|}
block|}
DECL|method|oldName (final PatchListEntry entry)
specifier|private
specifier|static
name|String
name|oldName
parameter_list|(
specifier|final
name|PatchListEntry
name|entry
parameter_list|)
block|{
switch|switch
condition|(
name|entry
operator|.
name|getChangeType
argument_list|()
condition|)
block|{
case|case
name|ADDED
case|:
return|return
literal|null
return|;
case|case
name|DELETED
case|:
case|case
name|MODIFIED
case|:
return|return
name|entry
operator|.
name|getNewName
argument_list|()
return|;
case|case
name|COPIED
case|:
case|case
name|RENAMED
case|:
default|default:
return|return
name|entry
operator|.
name|getOldName
argument_list|()
return|;
block|}
block|}
DECL|method|newName (final PatchListEntry entry)
specifier|private
specifier|static
name|String
name|newName
parameter_list|(
specifier|final
name|PatchListEntry
name|entry
parameter_list|)
block|{
switch|switch
condition|(
name|entry
operator|.
name|getChangeType
argument_list|()
condition|)
block|{
case|case
name|DELETED
case|:
return|return
literal|null
return|;
case|case
name|ADDED
case|:
case|case
name|MODIFIED
case|:
case|case
name|COPIED
case|:
case|case
name|RENAMED
case|:
default|default:
return|return
name|entry
operator|.
name|getNewName
argument_list|()
return|;
block|}
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
specifier|final
name|int
name|b
init|=
name|mapA2B
argument_list|(
name|a
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
literal|0
operator|<=
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
name|a
operator|-
literal|1
argument_list|,
name|b
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
specifier|final
name|int
name|a
init|=
name|mapB2A
argument_list|(
name|b
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
literal|0
operator|<=
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
argument_list|,
name|b
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|lastLine
operator|=
name|b
expr_stmt|;
block|}
block|}
comment|// Sort the final list by the index in A, so packContent can combine
comment|// them correctly later.
comment|//
name|edits
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
name|edits
argument_list|,
name|EDIT_SORT
argument_list|)
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|edits
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Edit
name|e
init|=
name|edits
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|<
name|e
operator|.
name|getBeginA
argument_list|()
condition|)
block|{
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
comment|// Special case of context at start of file.
comment|//
return|return
name|a
return|;
block|}
return|return
name|e
operator|.
name|getBeginB
argument_list|()
operator|-
operator|(
name|e
operator|.
name|getBeginA
argument_list|()
operator|-
name|a
operator|)
return|;
block|}
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
operator|-
literal|1
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|edits
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Edit
name|e
init|=
name|edits
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|b
operator|<
name|e
operator|.
name|getBeginB
argument_list|()
condition|)
block|{
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
comment|// Special case of context at start of file.
comment|//
return|return
name|b
return|;
block|}
return|return
name|e
operator|.
name|getBeginA
argument_list|()
operator|-
operator|(
name|e
operator|.
name|getBeginB
argument_list|()
operator|-
name|b
operator|)
return|;
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
return|return
operator|-
literal|1
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
DECL|method|packContent (boolean ignoredWhitespace)
specifier|private
name|void
name|packContent
parameter_list|(
name|boolean
name|ignoredWhitespace
parameter_list|)
block|{
name|EditList
name|list
init|=
operator|new
name|EditList
argument_list|(
name|edits
argument_list|,
name|context
argument_list|,
name|a
operator|.
name|size
argument_list|()
argument_list|,
name|b
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|EditList
operator|.
name|Hunk
name|hunk
range|:
name|list
operator|.
name|getHunks
argument_list|()
control|)
block|{
while|while
condition|(
name|hunk
operator|.
name|next
argument_list|()
condition|)
block|{
if|if
condition|(
name|hunk
operator|.
name|isContextLine
argument_list|()
condition|)
block|{
specifier|final
name|String
name|lineA
init|=
name|a
operator|.
name|src
operator|.
name|getString
argument_list|(
name|hunk
operator|.
name|getCurA
argument_list|()
argument_list|)
decl_stmt|;
name|a
operator|.
name|dst
operator|.
name|addLine
argument_list|(
name|hunk
operator|.
name|getCurA
argument_list|()
argument_list|,
name|lineA
argument_list|)
expr_stmt|;
if|if
condition|(
name|ignoredWhitespace
condition|)
block|{
comment|// If we ignored whitespace in some form, also get the line
comment|// from b when it does not exactly match the line from a.
comment|//
specifier|final
name|String
name|lineB
init|=
name|b
operator|.
name|src
operator|.
name|getString
argument_list|(
name|hunk
operator|.
name|getCurB
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|lineA
operator|.
name|equals
argument_list|(
name|lineB
argument_list|)
condition|)
block|{
name|b
operator|.
name|dst
operator|.
name|addLine
argument_list|(
name|hunk
operator|.
name|getCurB
argument_list|()
argument_list|,
name|lineB
argument_list|)
expr_stmt|;
block|}
block|}
name|hunk
operator|.
name|incBoth
argument_list|()
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|hunk
operator|.
name|isDeletedA
argument_list|()
condition|)
block|{
name|a
operator|.
name|addLine
argument_list|(
name|hunk
operator|.
name|getCurA
argument_list|()
argument_list|)
expr_stmt|;
name|hunk
operator|.
name|incA
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|hunk
operator|.
name|isInsertedB
argument_list|()
condition|)
block|{
name|b
operator|.
name|addLine
argument_list|(
name|hunk
operator|.
name|getCurB
argument_list|()
argument_list|)
expr_stmt|;
name|hunk
operator|.
name|incB
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|class|Side
specifier|private
class|class
name|Side
block|{
DECL|field|path
name|String
name|path
decl_stmt|;
DECL|field|id
name|ObjectId
name|id
decl_stmt|;
DECL|field|mode
name|FileMode
name|mode
decl_stmt|;
DECL|field|srcContent
name|byte
index|[]
name|srcContent
decl_stmt|;
DECL|field|src
name|Text
name|src
decl_stmt|;
DECL|field|mimeType
name|MimeType
name|mimeType
init|=
name|MimeUtil2
operator|.
name|UNKNOWN_MIME_TYPE
decl_stmt|;
DECL|field|displayMethod
name|DisplayMethod
name|displayMethod
init|=
name|DisplayMethod
operator|.
name|DIFF
decl_stmt|;
DECL|field|fileMode
name|PatchScript
operator|.
name|FileMode
name|fileMode
init|=
name|PatchScript
operator|.
name|FileMode
operator|.
name|FILE
decl_stmt|;
DECL|field|dst
specifier|final
name|SparseFileContent
name|dst
init|=
operator|new
name|SparseFileContent
argument_list|()
decl_stmt|;
DECL|method|size ()
name|int
name|size
parameter_list|()
block|{
return|return
name|src
operator|!=
literal|null
condition|?
name|src
operator|.
name|size
argument_list|()
else|:
literal|0
return|;
block|}
DECL|method|addLine (int line)
name|void
name|addLine
parameter_list|(
name|int
name|line
parameter_list|)
block|{
name|dst
operator|.
name|addLine
argument_list|(
name|line
argument_list|,
name|src
operator|.
name|getString
argument_list|(
name|line
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|resolve (final Side other, final ObjectId within)
name|void
name|resolve
parameter_list|(
specifier|final
name|Side
name|other
parameter_list|,
specifier|final
name|ObjectId
name|within
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
specifier|final
name|boolean
name|reuse
decl_stmt|;
if|if
condition|(
name|Patch
operator|.
name|COMMIT_MSG
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|)
block|{
if|if
condition|(
name|againstParent
operator|&&
operator|(
name|aId
operator|==
name|within
operator|||
name|within
operator|.
name|equals
argument_list|(
name|aId
argument_list|)
operator|)
condition|)
block|{
name|id
operator|=
name|ObjectId
operator|.
name|zeroId
argument_list|()
expr_stmt|;
name|src
operator|=
name|Text
operator|.
name|EMPTY
expr_stmt|;
name|srcContent
operator|=
name|Text
operator|.
name|NO_BYTES
expr_stmt|;
name|mode
operator|=
name|FileMode
operator|.
name|MISSING
expr_stmt|;
name|displayMethod
operator|=
name|DisplayMethod
operator|.
name|NONE
expr_stmt|;
block|}
else|else
block|{
name|id
operator|=
name|within
expr_stmt|;
name|src
operator|=
name|Text
operator|.
name|forCommit
argument_list|(
name|db
argument_list|,
name|reader
argument_list|,
name|within
argument_list|)
expr_stmt|;
name|srcContent
operator|=
name|src
operator|.
name|getContent
argument_list|()
expr_stmt|;
if|if
condition|(
name|src
operator|==
name|Text
operator|.
name|EMPTY
condition|)
block|{
name|mode
operator|=
name|FileMode
operator|.
name|MISSING
expr_stmt|;
name|displayMethod
operator|=
name|DisplayMethod
operator|.
name|NONE
expr_stmt|;
block|}
else|else
block|{
name|mode
operator|=
name|FileMode
operator|.
name|REGULAR_FILE
expr_stmt|;
block|}
block|}
name|reuse
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|TreeWalk
name|tw
init|=
name|find
argument_list|(
name|within
argument_list|)
decl_stmt|;
name|id
operator|=
name|tw
operator|!=
literal|null
condition|?
name|tw
operator|.
name|getObjectId
argument_list|(
literal|0
argument_list|)
else|:
name|ObjectId
operator|.
name|zeroId
argument_list|()
expr_stmt|;
name|mode
operator|=
name|tw
operator|!=
literal|null
condition|?
name|tw
operator|.
name|getFileMode
argument_list|(
literal|0
argument_list|)
else|:
name|FileMode
operator|.
name|MISSING
expr_stmt|;
name|reuse
operator|=
name|other
operator|!=
literal|null
operator|&&
name|other
operator|.
name|id
operator|.
name|equals
argument_list|(
name|id
argument_list|)
operator|&&
name|other
operator|.
name|mode
operator|==
name|mode
expr_stmt|;
if|if
condition|(
name|reuse
condition|)
block|{
name|srcContent
operator|=
name|other
operator|.
name|srcContent
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|mode
operator|.
name|getObjectType
argument_list|()
operator|==
name|Constants
operator|.
name|OBJ_BLOB
condition|)
block|{
name|srcContent
operator|=
name|Text
operator|.
name|asByteArray
argument_list|(
name|db
operator|.
name|open
argument_list|(
name|id
argument_list|,
name|Constants
operator|.
name|OBJ_BLOB
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|srcContent
operator|=
name|Text
operator|.
name|NO_BYTES
expr_stmt|;
block|}
if|if
condition|(
name|reuse
condition|)
block|{
name|mimeType
operator|=
name|other
operator|.
name|mimeType
expr_stmt|;
name|displayMethod
operator|=
name|other
operator|.
name|displayMethod
expr_stmt|;
name|src
operator|=
name|other
operator|.
name|src
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|srcContent
operator|.
name|length
operator|>
literal|0
operator|&&
name|FileMode
operator|.
name|SYMLINK
operator|!=
name|mode
condition|)
block|{
name|mimeType
operator|=
name|registry
operator|.
name|getMimeType
argument_list|(
name|path
argument_list|,
name|srcContent
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"image"
operator|.
name|equals
argument_list|(
name|mimeType
operator|.
name|getMediaType
argument_list|()
argument_list|)
operator|&&
name|registry
operator|.
name|isSafeInline
argument_list|(
name|mimeType
argument_list|)
condition|)
block|{
name|displayMethod
operator|=
name|DisplayMethod
operator|.
name|IMG
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|mode
operator|==
name|FileMode
operator|.
name|MISSING
condition|)
block|{
name|displayMethod
operator|=
name|DisplayMethod
operator|.
name|NONE
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|reuse
condition|)
block|{
if|if
condition|(
name|srcContent
operator|==
name|Text
operator|.
name|NO_BYTES
condition|)
block|{
name|src
operator|=
name|Text
operator|.
name|EMPTY
expr_stmt|;
block|}
else|else
block|{
name|src
operator|=
operator|new
name|Text
argument_list|(
name|srcContent
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|srcContent
operator|.
name|length
operator|>
literal|0
operator|&&
name|srcContent
index|[
name|srcContent
operator|.
name|length
operator|-
literal|1
index|]
operator|!=
literal|'\n'
condition|)
block|{
name|dst
operator|.
name|setMissingNewlineAtEnd
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|dst
operator|.
name|setSize
argument_list|(
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|dst
operator|.
name|setPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
name|mode
operator|==
name|FileMode
operator|.
name|SYMLINK
condition|)
block|{
name|fileMode
operator|=
name|PatchScript
operator|.
name|FileMode
operator|.
name|SYMLINK
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|mode
operator|==
name|FileMode
operator|.
name|GITLINK
condition|)
block|{
name|fileMode
operator|=
name|PatchScript
operator|.
name|FileMode
operator|.
name|GITLINK
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|err
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot read "
operator|+
name|within
operator|.
name|name
argument_list|()
operator|+
literal|":"
operator|+
name|path
argument_list|,
name|err
argument_list|)
throw|;
block|}
block|}
DECL|method|find (final ObjectId within)
specifier|private
name|TreeWalk
name|find
parameter_list|(
specifier|final
name|ObjectId
name|within
parameter_list|)
throws|throws
name|MissingObjectException
throws|,
name|IncorrectObjectTypeException
throws|,
name|CorruptObjectException
throws|,
name|IOException
block|{
if|if
condition|(
name|path
operator|==
literal|null
operator|||
name|within
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|RevWalk
name|rw
init|=
operator|new
name|RevWalk
argument_list|(
name|reader
argument_list|)
decl_stmt|;
specifier|final
name|RevTree
name|tree
init|=
name|rw
operator|.
name|parseTree
argument_list|(
name|within
argument_list|)
decl_stmt|;
return|return
name|TreeWalk
operator|.
name|forPath
argument_list|(
name|reader
argument_list|,
name|path
argument_list|,
name|tree
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

