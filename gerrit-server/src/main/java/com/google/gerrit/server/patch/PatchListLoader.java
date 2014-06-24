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

begin_comment
comment|//
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
name|common
operator|.
name|base
operator|.
name|Function
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
name|cache
operator|.
name|CacheLoader
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
name|FluentIterable
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
name|RefNames
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
name|git
operator|.
name|GitRepositoryManager
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
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|diff
operator|.
name|DiffEntry
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
name|DiffFormatter
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
name|diff
operator|.
name|EditList
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
name|HistogramDiff
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
name|RawText
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
name|RawTextComparator
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
name|Sequence
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
name|dircache
operator|.
name|DirCache
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
name|dircache
operator|.
name|DirCacheBuilder
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
name|dircache
operator|.
name|DirCacheEntry
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
name|ObjectInserter
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
name|Ref
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
name|RefUpdate
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
name|merge
operator|.
name|MergeFormatter
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
name|merge
operator|.
name|MergeResult
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
name|merge
operator|.
name|MergeStrategy
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
name|merge
operator|.
name|ResolveMerger
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
name|patch
operator|.
name|FileHeader
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
name|patch
operator|.
name|FileHeader
operator|.
name|PatchType
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
name|RevCommit
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
name|RevObject
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
name|util
operator|.
name|TemporaryBuffer
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
name|util
operator|.
name|io
operator|.
name|DisabledOutputStream
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
name|InputStream
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
name|HashMap
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
DECL|class|PatchListLoader
specifier|public
class|class
name|PatchListLoader
extends|extends
name|CacheLoader
argument_list|<
name|PatchListKey
argument_list|,
name|PatchList
argument_list|>
block|{
DECL|field|log
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|PatchListLoader
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|repoManager
specifier|private
specifier|final
name|GitRepositoryManager
name|repoManager
decl_stmt|;
DECL|field|patchListCache
specifier|private
specifier|final
name|PatchListCache
name|patchListCache
decl_stmt|;
annotation|@
name|Inject
DECL|method|PatchListLoader (GitRepositoryManager mgr, PatchListCache plc)
name|PatchListLoader
parameter_list|(
name|GitRepositoryManager
name|mgr
parameter_list|,
name|PatchListCache
name|plc
parameter_list|)
block|{
name|repoManager
operator|=
name|mgr
expr_stmt|;
name|patchListCache
operator|=
name|plc
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|load (final PatchListKey key)
specifier|public
name|PatchList
name|load
parameter_list|(
specifier|final
name|PatchListKey
name|key
parameter_list|)
throws|throws
name|IOException
throws|,
name|PatchListNotAvailableException
block|{
specifier|final
name|Repository
name|repo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|key
operator|.
name|projectKey
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|readPatchList
argument_list|(
name|key
argument_list|,
name|repo
argument_list|)
return|;
block|}
finally|finally
block|{
name|repo
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|comparatorFor (Whitespace ws)
specifier|private
specifier|static
name|RawTextComparator
name|comparatorFor
parameter_list|(
name|Whitespace
name|ws
parameter_list|)
block|{
switch|switch
condition|(
name|ws
condition|)
block|{
case|case
name|IGNORE_ALL_SPACE
case|:
return|return
name|RawTextComparator
operator|.
name|WS_IGNORE_ALL
return|;
case|case
name|IGNORE_SPACE_AT_EOL
case|:
return|return
name|RawTextComparator
operator|.
name|WS_IGNORE_TRAILING
return|;
case|case
name|IGNORE_SPACE_CHANGE
case|:
return|return
name|RawTextComparator
operator|.
name|WS_IGNORE_CHANGE
return|;
case|case
name|IGNORE_NONE
case|:
default|default:
return|return
name|RawTextComparator
operator|.
name|DEFAULT
return|;
block|}
block|}
DECL|method|readPatchList (final PatchListKey key, final Repository repo)
specifier|private
name|PatchList
name|readPatchList
parameter_list|(
specifier|final
name|PatchListKey
name|key
parameter_list|,
specifier|final
name|Repository
name|repo
parameter_list|)
throws|throws
name|IOException
throws|,
name|PatchListNotAvailableException
block|{
specifier|final
name|RawTextComparator
name|cmp
init|=
name|comparatorFor
argument_list|(
name|key
operator|.
name|getWhitespace
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|ObjectReader
name|reader
init|=
name|repo
operator|.
name|newObjectReader
argument_list|()
decl_stmt|;
try|try
block|{
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
name|RevCommit
name|b
init|=
name|rw
operator|.
name|parseCommit
argument_list|(
name|key
operator|.
name|getNewId
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|RevObject
name|a
init|=
name|aFor
argument_list|(
name|key
argument_list|,
name|repo
argument_list|,
name|rw
argument_list|,
name|b
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|==
literal|null
condition|)
block|{
comment|// TODO(sop) Remove this case.
comment|// This is a merge commit, compared to its ancestor.
comment|//
specifier|final
name|PatchListEntry
index|[]
name|entries
init|=
operator|new
name|PatchListEntry
index|[
literal|1
index|]
decl_stmt|;
name|entries
index|[
literal|0
index|]
operator|=
name|newCommitMessage
argument_list|(
name|cmp
argument_list|,
name|repo
argument_list|,
name|reader
argument_list|,
literal|null
argument_list|,
name|b
argument_list|)
expr_stmt|;
return|return
operator|new
name|PatchList
argument_list|(
name|a
argument_list|,
name|b
argument_list|,
literal|true
argument_list|,
name|entries
argument_list|)
return|;
block|}
specifier|final
name|boolean
name|againstParent
init|=
name|b
operator|.
name|getParentCount
argument_list|()
operator|>
literal|0
operator|&&
name|b
operator|.
name|getParent
argument_list|(
literal|0
argument_list|)
operator|==
name|a
decl_stmt|;
name|RevCommit
name|aCommit
init|=
name|a
operator|instanceof
name|RevCommit
condition|?
operator|(
name|RevCommit
operator|)
name|a
else|:
literal|null
decl_stmt|;
name|RevTree
name|aTree
init|=
name|rw
operator|.
name|parseTree
argument_list|(
name|a
argument_list|)
decl_stmt|;
name|RevTree
name|bTree
init|=
name|b
operator|.
name|getTree
argument_list|()
decl_stmt|;
name|DiffFormatter
name|df
init|=
operator|new
name|DiffFormatter
argument_list|(
name|DisabledOutputStream
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
name|df
operator|.
name|setRepository
argument_list|(
name|repo
argument_list|)
expr_stmt|;
name|df
operator|.
name|setDiffComparator
argument_list|(
name|cmp
argument_list|)
expr_stmt|;
name|df
operator|.
name|setDetectRenames
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|DiffEntry
argument_list|>
name|diffEntries
init|=
name|df
operator|.
name|scan
argument_list|(
name|aTree
argument_list|,
name|bTree
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|key
operator|.
name|getOldId
argument_list|()
operator|!=
literal|null
condition|?
name|FluentIterable
operator|.
name|from
argument_list|(
name|patchListCache
operator|.
name|get
argument_list|(
operator|new
name|PatchListKey
argument_list|(
name|key
operator|.
name|projectKey
argument_list|,
literal|null
argument_list|,
name|key
operator|.
name|getNewId
argument_list|()
argument_list|,
name|key
operator|.
name|getWhitespace
argument_list|()
argument_list|)
argument_list|)
operator|.
name|getPatches
argument_list|()
argument_list|)
operator|.
name|transform
argument_list|(
operator|new
name|Function
argument_list|<
name|PatchListEntry
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
name|PatchListEntry
name|entry
parameter_list|)
block|{
return|return
name|entry
operator|.
name|getNewName
argument_list|()
return|;
block|}
block|}
argument_list|)
operator|.
name|toSet
argument_list|()
else|:
literal|null
decl_stmt|;
name|int
name|cnt
init|=
name|diffEntries
operator|.
name|size
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|PatchListEntry
argument_list|>
name|entries
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|entries
operator|.
name|add
argument_list|(
name|newCommitMessage
argument_list|(
name|cmp
argument_list|,
name|repo
argument_list|,
name|reader
argument_list|,
comment|//
name|againstParent
condition|?
literal|null
else|:
name|aCommit
argument_list|,
name|b
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|cnt
condition|;
name|i
operator|++
control|)
block|{
name|DiffEntry
name|diffEntry
init|=
name|diffEntries
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|paths
operator|==
literal|null
operator|||
name|paths
operator|.
name|contains
argument_list|(
name|diffEntry
operator|.
name|getNewPath
argument_list|()
argument_list|)
operator|||
name|paths
operator|.
name|contains
argument_list|(
name|diffEntry
operator|.
name|getOldPath
argument_list|()
argument_list|)
condition|)
block|{
name|FileHeader
name|fh
init|=
name|df
operator|.
name|toFileHeader
argument_list|(
name|diffEntry
argument_list|)
decl_stmt|;
name|entries
operator|.
name|add
argument_list|(
name|newEntry
argument_list|(
name|aTree
argument_list|,
name|fh
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|PatchList
argument_list|(
name|a
argument_list|,
name|b
argument_list|,
name|againstParent
argument_list|,
name|entries
operator|.
name|toArray
argument_list|(
operator|new
name|PatchListEntry
index|[
name|entries
operator|.
name|size
argument_list|()
index|]
argument_list|)
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
DECL|method|newCommitMessage (final RawTextComparator cmp, final Repository db, final ObjectReader reader, final RevCommit aCommit, final RevCommit bCommit)
specifier|private
name|PatchListEntry
name|newCommitMessage
parameter_list|(
specifier|final
name|RawTextComparator
name|cmp
parameter_list|,
specifier|final
name|Repository
name|db
parameter_list|,
specifier|final
name|ObjectReader
name|reader
parameter_list|,
specifier|final
name|RevCommit
name|aCommit
parameter_list|,
specifier|final
name|RevCommit
name|bCommit
parameter_list|)
throws|throws
name|IOException
block|{
name|StringBuilder
name|hdr
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|hdr
operator|.
name|append
argument_list|(
literal|"diff --git"
argument_list|)
expr_stmt|;
if|if
condition|(
name|aCommit
operator|!=
literal|null
condition|)
block|{
name|hdr
operator|.
name|append
argument_list|(
literal|" a/"
argument_list|)
operator|.
name|append
argument_list|(
name|Patch
operator|.
name|COMMIT_MSG
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|hdr
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|FileHeader
operator|.
name|DEV_NULL
argument_list|)
expr_stmt|;
block|}
name|hdr
operator|.
name|append
argument_list|(
literal|" b/"
argument_list|)
operator|.
name|append
argument_list|(
name|Patch
operator|.
name|COMMIT_MSG
argument_list|)
expr_stmt|;
name|hdr
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
if|if
condition|(
name|aCommit
operator|!=
literal|null
condition|)
block|{
name|hdr
operator|.
name|append
argument_list|(
literal|"--- a/"
argument_list|)
operator|.
name|append
argument_list|(
name|Patch
operator|.
name|COMMIT_MSG
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|hdr
operator|.
name|append
argument_list|(
literal|"--- "
argument_list|)
operator|.
name|append
argument_list|(
name|FileHeader
operator|.
name|DEV_NULL
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
name|hdr
operator|.
name|append
argument_list|(
literal|"+++ b/"
argument_list|)
operator|.
name|append
argument_list|(
name|Patch
operator|.
name|COMMIT_MSG
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|Text
name|aText
init|=
name|aCommit
operator|!=
literal|null
condition|?
name|Text
operator|.
name|forCommit
argument_list|(
name|db
argument_list|,
name|reader
argument_list|,
name|aCommit
argument_list|)
else|:
name|Text
operator|.
name|EMPTY
decl_stmt|;
name|Text
name|bText
init|=
name|Text
operator|.
name|forCommit
argument_list|(
name|db
argument_list|,
name|reader
argument_list|,
name|bCommit
argument_list|)
decl_stmt|;
name|byte
index|[]
name|rawHdr
init|=
name|hdr
operator|.
name|toString
argument_list|()
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|RawText
name|aRawText
init|=
operator|new
name|RawText
argument_list|(
name|aText
operator|.
name|getContent
argument_list|()
argument_list|)
decl_stmt|;
name|RawText
name|bRawText
init|=
operator|new
name|RawText
argument_list|(
name|bText
operator|.
name|getContent
argument_list|()
argument_list|)
decl_stmt|;
name|EditList
name|edits
init|=
operator|new
name|HistogramDiff
argument_list|()
operator|.
name|diff
argument_list|(
name|cmp
argument_list|,
name|aRawText
argument_list|,
name|bRawText
argument_list|)
decl_stmt|;
name|FileHeader
name|fh
init|=
operator|new
name|FileHeader
argument_list|(
name|rawHdr
argument_list|,
name|edits
argument_list|,
name|PatchType
operator|.
name|UNIFIED
argument_list|)
decl_stmt|;
return|return
operator|new
name|PatchListEntry
argument_list|(
name|fh
argument_list|,
name|edits
argument_list|)
return|;
block|}
DECL|method|newEntry (RevTree aTree, FileHeader fileHeader)
specifier|private
name|PatchListEntry
name|newEntry
parameter_list|(
name|RevTree
name|aTree
parameter_list|,
name|FileHeader
name|fileHeader
parameter_list|)
block|{
specifier|final
name|FileMode
name|oldMode
init|=
name|fileHeader
operator|.
name|getOldMode
argument_list|()
decl_stmt|;
specifier|final
name|FileMode
name|newMode
init|=
name|fileHeader
operator|.
name|getNewMode
argument_list|()
decl_stmt|;
if|if
condition|(
name|oldMode
operator|==
name|FileMode
operator|.
name|GITLINK
operator|||
name|newMode
operator|==
name|FileMode
operator|.
name|GITLINK
condition|)
block|{
return|return
operator|new
name|PatchListEntry
argument_list|(
name|fileHeader
argument_list|,
name|Collections
operator|.
expr|<
name|Edit
operator|>
name|emptyList
argument_list|()
argument_list|)
return|;
block|}
if|if
condition|(
name|aTree
operator|==
literal|null
comment|// want combined diff
operator|||
name|fileHeader
operator|.
name|getPatchType
argument_list|()
operator|!=
name|PatchType
operator|.
name|UNIFIED
operator|||
name|fileHeader
operator|.
name|getHunks
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
operator|new
name|PatchListEntry
argument_list|(
name|fileHeader
argument_list|,
name|Collections
operator|.
expr|<
name|Edit
operator|>
name|emptyList
argument_list|()
argument_list|)
return|;
block|}
name|List
argument_list|<
name|Edit
argument_list|>
name|edits
init|=
name|fileHeader
operator|.
name|toEditList
argument_list|()
decl_stmt|;
if|if
condition|(
name|edits
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
operator|new
name|PatchListEntry
argument_list|(
name|fileHeader
argument_list|,
name|Collections
operator|.
expr|<
name|Edit
operator|>
name|emptyList
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|PatchListEntry
argument_list|(
name|fileHeader
argument_list|,
name|edits
argument_list|)
return|;
block|}
block|}
DECL|method|aFor (final PatchListKey key, final Repository repo, final RevWalk rw, final RevCommit b)
specifier|private
specifier|static
name|RevObject
name|aFor
parameter_list|(
specifier|final
name|PatchListKey
name|key
parameter_list|,
specifier|final
name|Repository
name|repo
parameter_list|,
specifier|final
name|RevWalk
name|rw
parameter_list|,
specifier|final
name|RevCommit
name|b
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|key
operator|.
name|getOldId
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|rw
operator|.
name|parseAny
argument_list|(
name|key
operator|.
name|getOldId
argument_list|()
argument_list|)
return|;
block|}
switch|switch
condition|(
name|b
operator|.
name|getParentCount
argument_list|()
condition|)
block|{
case|case
literal|0
case|:
return|return
name|rw
operator|.
name|parseAny
argument_list|(
name|emptyTree
argument_list|(
name|repo
argument_list|)
argument_list|)
return|;
case|case
literal|1
case|:
block|{
name|RevCommit
name|r
init|=
name|b
operator|.
name|getParent
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|rw
operator|.
name|parseBody
argument_list|(
name|r
argument_list|)
expr_stmt|;
return|return
name|r
return|;
block|}
case|case
literal|2
case|:
return|return
name|automerge
argument_list|(
name|repo
argument_list|,
name|rw
argument_list|,
name|b
argument_list|)
return|;
default|default:
comment|// TODO(sop) handle an octopus merge.
return|return
literal|null
return|;
block|}
block|}
DECL|method|automerge (Repository repo, RevWalk rw, RevCommit b)
specifier|public
specifier|static
name|RevTree
name|automerge
parameter_list|(
name|Repository
name|repo
parameter_list|,
name|RevWalk
name|rw
parameter_list|,
name|RevCommit
name|b
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|automerge
argument_list|(
name|repo
argument_list|,
name|rw
argument_list|,
name|b
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|automerge (Repository repo, RevWalk rw, RevCommit b, boolean save)
specifier|public
specifier|static
name|RevTree
name|automerge
parameter_list|(
name|Repository
name|repo
parameter_list|,
name|RevWalk
name|rw
parameter_list|,
name|RevCommit
name|b
parameter_list|,
name|boolean
name|save
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|hash
init|=
name|b
operator|.
name|name
argument_list|()
decl_stmt|;
name|String
name|refName
init|=
name|RefNames
operator|.
name|REFS_CACHE_AUTOMERGE
operator|+
name|hash
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
operator|+
literal|"/"
operator|+
name|hash
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|Ref
name|ref
init|=
name|repo
operator|.
name|getRef
argument_list|(
name|refName
argument_list|)
decl_stmt|;
if|if
condition|(
name|ref
operator|!=
literal|null
operator|&&
name|ref
operator|.
name|getObjectId
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|rw
operator|.
name|parseTree
argument_list|(
name|ref
operator|.
name|getObjectId
argument_list|()
argument_list|)
return|;
block|}
name|ObjectId
name|treeId
decl_stmt|;
name|ResolveMerger
name|m
init|=
operator|(
name|ResolveMerger
operator|)
name|MergeStrategy
operator|.
name|RESOLVE
operator|.
name|newMerger
argument_list|(
name|repo
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|ObjectInserter
name|ins
init|=
name|repo
operator|.
name|newObjectInserter
argument_list|()
decl_stmt|;
try|try
block|{
name|DirCache
name|dc
init|=
name|DirCache
operator|.
name|newInCore
argument_list|()
decl_stmt|;
name|m
operator|.
name|setDirCache
argument_list|(
name|dc
argument_list|)
expr_stmt|;
name|m
operator|.
name|setObjectInserter
argument_list|(
operator|new
name|ObjectInserter
operator|.
name|Filter
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|ObjectInserter
name|delegate
parameter_list|()
block|{
return|return
name|ins
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|flush
parameter_list|()
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|release
parameter_list|()
block|{         }
block|}
argument_list|)
expr_stmt|;
name|boolean
name|couldMerge
decl_stmt|;
try|try
block|{
name|couldMerge
operator|=
name|m
operator|.
name|merge
argument_list|(
name|b
operator|.
name|getParents
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// It is not safe to continue further down in this method as throwing
comment|// an exception most likely means that the merge tree was not created
comment|// and m.getMergeResults() is empty. This would mean that all paths are
comment|// unmerged and Gerrit UI would show all paths in the patch list.
name|log
operator|.
name|warn
argument_list|(
literal|"Error attempting automerge "
operator|+
name|refName
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
if|if
condition|(
name|couldMerge
condition|)
block|{
name|treeId
operator|=
name|m
operator|.
name|getResultTreeId
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|RevCommit
name|ours
init|=
name|b
operator|.
name|getParent
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|RevCommit
name|theirs
init|=
name|b
operator|.
name|getParent
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|rw
operator|.
name|parseBody
argument_list|(
name|ours
argument_list|)
expr_stmt|;
name|rw
operator|.
name|parseBody
argument_list|(
name|theirs
argument_list|)
expr_stmt|;
name|String
name|oursMsg
init|=
name|ours
operator|.
name|getShortMessage
argument_list|()
decl_stmt|;
name|String
name|theirsMsg
init|=
name|theirs
operator|.
name|getShortMessage
argument_list|()
decl_stmt|;
name|String
name|oursName
init|=
name|String
operator|.
name|format
argument_list|(
literal|"HEAD   (%s %s)"
argument_list|,
name|ours
operator|.
name|abbreviate
argument_list|(
literal|6
argument_list|)
operator|.
name|name
argument_list|()
argument_list|,
name|oursMsg
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|oursMsg
operator|.
name|length
argument_list|()
argument_list|,
literal|60
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|theirsName
init|=
name|String
operator|.
name|format
argument_list|(
literal|"BRANCH (%s %s)"
argument_list|,
name|theirs
operator|.
name|abbreviate
argument_list|(
literal|6
argument_list|)
operator|.
name|name
argument_list|()
argument_list|,
name|theirsMsg
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|theirsMsg
operator|.
name|length
argument_list|()
argument_list|,
literal|60
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|MergeFormatter
name|fmt
init|=
operator|new
name|MergeFormatter
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|MergeResult
argument_list|<
name|?
extends|extends
name|Sequence
argument_list|>
argument_list|>
name|r
init|=
name|m
operator|.
name|getMergeResults
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|ObjectId
argument_list|>
name|resolved
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|MergeResult
argument_list|<
name|?
extends|extends
name|Sequence
argument_list|>
argument_list|>
name|entry
range|:
name|r
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|MergeResult
argument_list|<
name|?
extends|extends
name|Sequence
argument_list|>
name|p
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|TemporaryBuffer
name|buf
init|=
operator|new
name|TemporaryBuffer
operator|.
name|LocalFile
argument_list|(
literal|10
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
decl_stmt|;
try|try
block|{
name|fmt
operator|.
name|formatMerge
argument_list|(
name|buf
argument_list|,
name|p
argument_list|,
literal|"BASE"
argument_list|,
name|oursName
argument_list|,
name|theirsName
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|close
argument_list|()
expr_stmt|;
name|InputStream
name|in
init|=
name|buf
operator|.
name|openInputStream
argument_list|()
decl_stmt|;
try|try
block|{
name|resolved
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|ins
operator|.
name|insert
argument_list|(
name|Constants
operator|.
name|OBJ_BLOB
argument_list|,
name|buf
operator|.
name|length
argument_list|()
argument_list|,
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|buf
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
block|}
name|DirCacheBuilder
name|builder
init|=
name|dc
operator|.
name|builder
argument_list|()
decl_stmt|;
name|int
name|cnt
init|=
name|dc
operator|.
name|getEntryCount
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|cnt
condition|;
control|)
block|{
name|DirCacheEntry
name|entry
init|=
name|dc
operator|.
name|getEntry
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|entry
operator|.
name|getStage
argument_list|()
operator|==
literal|0
condition|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|entry
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
continue|continue;
block|}
name|int
name|next
init|=
name|dc
operator|.
name|nextEntry
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|entry
operator|.
name|getPathString
argument_list|()
decl_stmt|;
name|DirCacheEntry
name|res
init|=
operator|new
name|DirCacheEntry
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|resolved
operator|.
name|containsKey
argument_list|(
name|path
argument_list|)
condition|)
block|{
comment|// For a file with content merge conflict that we produced a result
comment|// above on, collapse the file down to a single stage 0 with just
comment|// the blob content, and a randomly selected mode (the lowest stage,
comment|// which should be the merge base, or ours).
name|res
operator|.
name|setFileMode
argument_list|(
name|entry
operator|.
name|getFileMode
argument_list|()
argument_list|)
expr_stmt|;
name|res
operator|.
name|setObjectId
argument_list|(
name|resolved
operator|.
name|get
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|next
operator|==
name|i
operator|+
literal|1
condition|)
block|{
comment|// If there is exactly one stage present, shouldn't be a conflict...
name|res
operator|.
name|setFileMode
argument_list|(
name|entry
operator|.
name|getFileMode
argument_list|()
argument_list|)
expr_stmt|;
name|res
operator|.
name|setObjectId
argument_list|(
name|entry
operator|.
name|getObjectId
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|next
operator|==
name|i
operator|+
literal|2
condition|)
block|{
comment|// Two stages suggests a delete/modify conflict. Pick the higher
comment|// stage as the automatic result.
name|entry
operator|=
name|dc
operator|.
name|getEntry
argument_list|(
name|i
operator|+
literal|1
argument_list|)
expr_stmt|;
name|res
operator|.
name|setFileMode
argument_list|(
name|entry
operator|.
name|getFileMode
argument_list|()
argument_list|)
expr_stmt|;
name|res
operator|.
name|setObjectId
argument_list|(
name|entry
operator|.
name|getObjectId
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// 3 stage conflict, no resolve above
comment|// Punt on the 3-stage conflict and show the base, for now.
name|res
operator|.
name|setFileMode
argument_list|(
name|entry
operator|.
name|getFileMode
argument_list|()
argument_list|)
expr_stmt|;
name|res
operator|.
name|setObjectId
argument_list|(
name|entry
operator|.
name|getObjectId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|add
argument_list|(
name|res
argument_list|)
expr_stmt|;
name|i
operator|=
name|next
expr_stmt|;
block|}
name|builder
operator|.
name|finish
argument_list|()
expr_stmt|;
name|treeId
operator|=
name|dc
operator|.
name|writeTree
argument_list|(
name|ins
argument_list|)
expr_stmt|;
block|}
name|ins
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|ins
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|save
condition|)
block|{
name|RefUpdate
name|update
init|=
name|repo
operator|.
name|updateRef
argument_list|(
name|refName
argument_list|)
decl_stmt|;
name|update
operator|.
name|setNewObjectId
argument_list|(
name|treeId
argument_list|)
expr_stmt|;
name|update
operator|.
name|disableRefLog
argument_list|()
expr_stmt|;
name|update
operator|.
name|forceUpdate
argument_list|()
expr_stmt|;
block|}
return|return
name|rw
operator|.
name|parseTree
argument_list|(
name|treeId
argument_list|)
return|;
block|}
DECL|method|emptyTree (final Repository repo)
specifier|private
specifier|static
name|ObjectId
name|emptyTree
parameter_list|(
specifier|final
name|Repository
name|repo
parameter_list|)
throws|throws
name|IOException
block|{
name|ObjectInserter
name|oi
init|=
name|repo
operator|.
name|newObjectInserter
argument_list|()
decl_stmt|;
try|try
block|{
name|ObjectId
name|id
init|=
name|oi
operator|.
name|insert
argument_list|(
name|Constants
operator|.
name|OBJ_TREE
argument_list|,
operator|new
name|byte
index|[]
block|{}
argument_list|)
decl_stmt|;
name|oi
operator|.
name|flush
argument_list|()
expr_stmt|;
return|return
name|id
return|;
block|}
finally|finally
block|{
name|oi
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

