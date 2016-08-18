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
import|import static
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Constants
operator|.
name|OBJ_BLOB
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
name|base
operator|.
name|Throwables
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
name|extensions
operator|.
name|client
operator|.
name|DiffPreferencesInfo
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
name|server
operator|.
name|config
operator|.
name|ConfigUtil
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
name|config
operator|.
name|GerritServerConfig
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
name|gerrit
operator|.
name|server
operator|.
name|git
operator|.
name|InMemoryInserter
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
name|MergeUtil
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
name|assistedinject
operator|.
name|Assisted
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
name|assistedinject
operator|.
name|AssistedInject
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
name|lib
operator|.
name|Config
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
name|ThreeWayMergeStrategy
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
name|treewalk
operator|.
name|TreeWalk
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
name|List
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutionException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutorService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Future
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeoutException
import|;
end_import

begin_class
DECL|class|PatchListLoader
specifier|public
class|class
name|PatchListLoader
implements|implements
name|Callable
argument_list|<
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
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (PatchListKey key, Project.NameKey project)
name|PatchListLoader
name|create
parameter_list|(
name|PatchListKey
name|key
parameter_list|,
name|Project
operator|.
name|NameKey
name|project
parameter_list|)
function_decl|;
block|}
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
DECL|field|mergeStrategy
specifier|private
specifier|final
name|ThreeWayMergeStrategy
name|mergeStrategy
decl_stmt|;
DECL|field|diffExecutor
specifier|private
specifier|final
name|ExecutorService
name|diffExecutor
decl_stmt|;
DECL|field|autoMerger
specifier|private
specifier|final
name|AutoMerger
name|autoMerger
decl_stmt|;
DECL|field|key
specifier|private
specifier|final
name|PatchListKey
name|key
decl_stmt|;
DECL|field|project
specifier|private
specifier|final
name|Project
operator|.
name|NameKey
name|project
decl_stmt|;
DECL|field|timeoutMillis
specifier|private
specifier|final
name|long
name|timeoutMillis
decl_stmt|;
DECL|field|save
specifier|private
specifier|final
name|boolean
name|save
decl_stmt|;
annotation|@
name|AssistedInject
DECL|method|PatchListLoader (GitRepositoryManager mgr, PatchListCache plc, @GerritServerConfig Config cfg, @DiffExecutor ExecutorService de, AutoMerger am, @Assisted PatchListKey k, @Assisted Project.NameKey p)
name|PatchListLoader
parameter_list|(
name|GitRepositoryManager
name|mgr
parameter_list|,
name|PatchListCache
name|plc
parameter_list|,
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|,
annotation|@
name|DiffExecutor
name|ExecutorService
name|de
parameter_list|,
name|AutoMerger
name|am
parameter_list|,
annotation|@
name|Assisted
name|PatchListKey
name|k
parameter_list|,
annotation|@
name|Assisted
name|Project
operator|.
name|NameKey
name|p
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
name|mergeStrategy
operator|=
name|MergeUtil
operator|.
name|getMergeStrategy
argument_list|(
name|cfg
argument_list|)
expr_stmt|;
name|diffExecutor
operator|=
name|de
expr_stmt|;
name|autoMerger
operator|=
name|am
expr_stmt|;
name|key
operator|=
name|k
expr_stmt|;
name|project
operator|=
name|p
expr_stmt|;
name|timeoutMillis
operator|=
name|ConfigUtil
operator|.
name|getTimeUnit
argument_list|(
name|cfg
argument_list|,
literal|"cache"
argument_list|,
name|PatchListCacheImpl
operator|.
name|FILE_NAME
argument_list|,
literal|"timeout"
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|convert
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|save
operator|=
name|AutoMerger
operator|.
name|cacheAutomerge
argument_list|(
name|cfg
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|PatchList
name|call
parameter_list|()
throws|throws
name|IOException
throws|,
name|PatchListNotAvailableException
block|{
try|try
init|(
name|Repository
name|repo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|project
argument_list|)
init|;
name|ObjectInserter
name|ins
operator|=
name|newInserter
argument_list|(
name|repo
argument_list|)
init|;
name|ObjectReader
name|reader
operator|=
name|ins
operator|.
name|newReader
argument_list|()
init|;
name|RevWalk
name|rw
operator|=
operator|new
name|RevWalk
argument_list|(
name|reader
argument_list|)
init|)
block|{
return|return
name|readPatchList
argument_list|(
name|repo
argument_list|,
name|rw
argument_list|,
name|ins
argument_list|)
return|;
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
name|IGNORE_ALL
case|:
return|return
name|RawTextComparator
operator|.
name|WS_IGNORE_ALL
return|;
case|case
name|IGNORE_TRAILING
case|:
return|return
name|RawTextComparator
operator|.
name|WS_IGNORE_TRAILING
return|;
case|case
name|IGNORE_LEADING_AND_TRAILING
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
DECL|method|newInserter (Repository repo)
specifier|private
name|ObjectInserter
name|newInserter
parameter_list|(
name|Repository
name|repo
parameter_list|)
block|{
return|return
name|save
condition|?
name|repo
operator|.
name|newObjectInserter
argument_list|()
else|:
operator|new
name|InMemoryInserter
argument_list|(
name|repo
argument_list|)
return|;
block|}
DECL|method|readPatchList (Repository repo, RevWalk rw, ObjectInserter ins)
specifier|public
name|PatchList
name|readPatchList
parameter_list|(
name|Repository
name|repo
parameter_list|,
name|RevWalk
name|rw
parameter_list|,
name|ObjectInserter
name|ins
parameter_list|)
throws|throws
name|IOException
throws|,
name|PatchListNotAvailableException
block|{
name|ObjectReader
name|reader
init|=
name|rw
operator|.
name|getObjectReader
argument_list|()
decl_stmt|;
name|checkArgument
argument_list|(
name|reader
operator|.
name|getCreatedFromInserter
argument_list|()
operator|==
name|ins
argument_list|)
expr_stmt|;
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
try|try
init|(
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
init|)
block|{
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
name|ins
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
operator|.
name|equals
argument_list|(
name|a
argument_list|)
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
name|df
operator|.
name|setReader
argument_list|(
name|reader
argument_list|,
name|repo
operator|.
name|getConfig
argument_list|()
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
literal|null
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|getOldId
argument_list|()
operator|!=
literal|null
operator|&&
name|b
operator|.
name|getParentCount
argument_list|()
operator|==
literal|1
condition|)
block|{
name|PatchListKey
name|newKey
init|=
name|PatchListKey
operator|.
name|againstDefaultBase
argument_list|(
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
decl_stmt|;
name|PatchListKey
name|oldKey
init|=
name|PatchListKey
operator|.
name|againstDefaultBase
argument_list|(
name|key
operator|.
name|getOldId
argument_list|()
argument_list|,
name|key
operator|.
name|getWhitespace
argument_list|()
argument_list|)
decl_stmt|;
name|paths
operator|=
name|FluentIterable
operator|.
name|from
argument_list|(
name|patchListCache
operator|.
name|get
argument_list|(
name|newKey
argument_list|,
name|project
argument_list|)
operator|.
name|getPatches
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|patchListCache
operator|.
name|get
argument_list|(
name|oldKey
argument_list|,
name|project
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
expr_stmt|;
block|}
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
name|reader
argument_list|,
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
name|e
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
name|e
operator|.
name|getNewPath
argument_list|()
argument_list|)
operator|||
name|paths
operator|.
name|contains
argument_list|(
name|e
operator|.
name|getOldPath
argument_list|()
argument_list|)
condition|)
block|{
name|FileHeader
name|fh
init|=
name|toFileHeader
argument_list|(
name|key
argument_list|,
name|df
argument_list|,
name|e
argument_list|)
decl_stmt|;
name|long
name|oldSize
init|=
name|getFileSize
argument_list|(
name|reader
argument_list|,
name|e
operator|.
name|getOldMode
argument_list|()
argument_list|,
name|e
operator|.
name|getOldPath
argument_list|()
argument_list|,
name|aTree
argument_list|)
decl_stmt|;
name|long
name|newSize
init|=
name|getFileSize
argument_list|(
name|reader
argument_list|,
name|e
operator|.
name|getNewMode
argument_list|()
argument_list|,
name|e
operator|.
name|getNewPath
argument_list|()
argument_list|,
name|bTree
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
argument_list|,
name|newSize
argument_list|,
name|newSize
operator|-
name|oldSize
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
block|}
DECL|method|getFileSize (ObjectReader reader, FileMode mode, String path, RevTree t)
specifier|private
specifier|static
name|long
name|getFileSize
parameter_list|(
name|ObjectReader
name|reader
parameter_list|,
name|FileMode
name|mode
parameter_list|,
name|String
name|path
parameter_list|,
name|RevTree
name|t
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|isBlob
argument_list|(
name|mode
argument_list|)
condition|)
block|{
return|return
literal|0
return|;
block|}
try|try
init|(
name|TreeWalk
name|tw
init|=
name|TreeWalk
operator|.
name|forPath
argument_list|(
name|reader
argument_list|,
name|path
argument_list|,
name|t
argument_list|)
init|)
block|{
return|return
name|tw
operator|!=
literal|null
condition|?
name|reader
operator|.
name|open
argument_list|(
name|tw
operator|.
name|getObjectId
argument_list|(
literal|0
argument_list|)
argument_list|,
name|OBJ_BLOB
argument_list|)
operator|.
name|getSize
argument_list|()
else|:
literal|0
return|;
block|}
block|}
DECL|method|isBlob (FileMode mode)
specifier|private
specifier|static
name|boolean
name|isBlob
parameter_list|(
name|FileMode
name|mode
parameter_list|)
block|{
name|int
name|t
init|=
name|mode
operator|.
name|getBits
argument_list|()
operator|&
name|FileMode
operator|.
name|TYPE_MASK
decl_stmt|;
return|return
name|t
operator|==
name|FileMode
operator|.
name|TYPE_FILE
operator|||
name|t
operator|==
name|FileMode
operator|.
name|TYPE_SYMLINK
return|;
block|}
DECL|method|toFileHeader (PatchListKey key, final DiffFormatter diffFormatter, final DiffEntry diffEntry)
specifier|private
name|FileHeader
name|toFileHeader
parameter_list|(
name|PatchListKey
name|key
parameter_list|,
specifier|final
name|DiffFormatter
name|diffFormatter
parameter_list|,
specifier|final
name|DiffEntry
name|diffEntry
parameter_list|)
throws|throws
name|IOException
block|{
name|Future
argument_list|<
name|FileHeader
argument_list|>
name|result
init|=
name|diffExecutor
operator|.
name|submit
argument_list|(
operator|new
name|Callable
argument_list|<
name|FileHeader
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|FileHeader
name|call
parameter_list|()
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|diffEntry
init|)
block|{
return|return
name|diffFormatter
operator|.
name|toFileHeader
argument_list|(
name|diffEntry
argument_list|)
return|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|result
operator|.
name|get
argument_list|(
name|timeoutMillis
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
decl||
name|TimeoutException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
name|timeoutMillis
operator|+
literal|" ms timeout reached for Diff loader"
operator|+
literal|" in project "
operator|+
name|project
operator|+
literal|" on commit "
operator|+
name|key
operator|.
name|getNewId
argument_list|()
operator|.
name|name
argument_list|()
operator|+
literal|" on path "
operator|+
name|diffEntry
operator|.
name|getNewPath
argument_list|()
operator|+
literal|" comparing "
operator|+
name|diffEntry
operator|.
name|getOldId
argument_list|()
operator|.
name|name
argument_list|()
operator|+
literal|".."
operator|+
name|diffEntry
operator|.
name|getNewId
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|cancel
argument_list|(
literal|true
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|diffEntry
init|)
block|{
return|return
name|toFileHeaderWithoutMyersDiff
argument_list|(
name|diffFormatter
argument_list|,
name|diffEntry
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
comment|// If there was an error computing the result, carry it
comment|// up to the caller so the cache knows this key is invalid.
name|Throwables
operator|.
name|propagateIfInstanceOf
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|,
name|IOException
operator|.
name|class
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|method|toFileHeaderWithoutMyersDiff (DiffFormatter diffFormatter, DiffEntry diffEntry)
specifier|private
name|FileHeader
name|toFileHeaderWithoutMyersDiff
parameter_list|(
name|DiffFormatter
name|diffFormatter
parameter_list|,
name|DiffEntry
name|diffEntry
parameter_list|)
throws|throws
name|IOException
block|{
name|HistogramDiff
name|histogramDiff
init|=
operator|new
name|HistogramDiff
argument_list|()
decl_stmt|;
name|histogramDiff
operator|.
name|setFallbackAlgorithm
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|diffFormatter
operator|.
name|setDiffAlgorithm
argument_list|(
name|histogramDiff
argument_list|)
expr_stmt|;
return|return
name|diffFormatter
operator|.
name|toFileHeader
argument_list|(
name|diffEntry
argument_list|)
return|;
block|}
DECL|method|newCommitMessage (final RawTextComparator cmp, final ObjectReader reader, final RevCommit aCommit, final RevCommit bCommit)
specifier|private
name|PatchListEntry
name|newCommitMessage
parameter_list|(
specifier|final
name|RawTextComparator
name|cmp
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
name|UTF_8
argument_list|)
decl_stmt|;
name|byte
index|[]
name|aContent
init|=
name|aText
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|byte
index|[]
name|bContent
init|=
name|bText
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|long
name|size
init|=
name|bContent
operator|.
name|length
decl_stmt|;
name|long
name|sizeDelta
init|=
name|bContent
operator|.
name|length
operator|-
name|aContent
operator|.
name|length
decl_stmt|;
name|RawText
name|aRawText
init|=
operator|new
name|RawText
argument_list|(
name|aContent
argument_list|)
decl_stmt|;
name|RawText
name|bRawText
init|=
operator|new
name|RawText
argument_list|(
name|bContent
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
argument_list|,
name|size
argument_list|,
name|sizeDelta
argument_list|)
return|;
block|}
DECL|method|newEntry (RevTree aTree, FileHeader fileHeader, long size, long sizeDelta)
specifier|private
name|PatchListEntry
name|newEntry
parameter_list|(
name|RevTree
name|aTree
parameter_list|,
name|FileHeader
name|fileHeader
parameter_list|,
name|long
name|size
parameter_list|,
name|long
name|sizeDelta
parameter_list|)
block|{
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
argument_list|,
name|size
argument_list|,
name|sizeDelta
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
argument_list|,
name|size
argument_list|,
name|sizeDelta
argument_list|)
return|;
block|}
return|return
operator|new
name|PatchListEntry
argument_list|(
name|fileHeader
argument_list|,
name|edits
argument_list|,
name|size
argument_list|,
name|sizeDelta
argument_list|)
return|;
block|}
DECL|method|aFor (PatchListKey key, Repository repo, RevWalk rw, ObjectInserter ins, RevCommit b)
specifier|private
name|RevObject
name|aFor
parameter_list|(
name|PatchListKey
name|key
parameter_list|,
name|Repository
name|repo
parameter_list|,
name|RevWalk
name|rw
parameter_list|,
name|ObjectInserter
name|ins
parameter_list|,
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
name|ins
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
if|if
condition|(
name|key
operator|.
name|getParentNum
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|RevCommit
name|r
init|=
name|b
operator|.
name|getParent
argument_list|(
name|key
operator|.
name|getParentNum
argument_list|()
operator|-
literal|1
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
return|return
name|autoMerger
operator|.
name|merge
argument_list|(
name|repo
argument_list|,
name|rw
argument_list|,
name|ins
argument_list|,
name|b
argument_list|,
name|mergeStrategy
argument_list|)
return|;
default|default:
comment|// TODO(sop) handle an octopus merge.
return|return
literal|null
return|;
block|}
block|}
DECL|method|emptyTree (ObjectInserter ins)
specifier|private
specifier|static
name|ObjectId
name|emptyTree
parameter_list|(
name|ObjectInserter
name|ins
parameter_list|)
throws|throws
name|IOException
block|{
name|ObjectId
name|id
init|=
name|ins
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
name|ins
operator|.
name|flush
argument_list|()
expr_stmt|;
return|return
name|id
return|;
block|}
block|}
end_class

end_unit

