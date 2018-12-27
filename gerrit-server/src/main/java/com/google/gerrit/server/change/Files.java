begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2013 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|change
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
name|Lists
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
name|Sets
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
name|common
operator|.
name|FileInfo
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
name|registration
operator|.
name|DynamicMap
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
name|restapi
operator|.
name|AuthException
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
name|restapi
operator|.
name|BadRequestException
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
name|restapi
operator|.
name|CacheControl
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
name|restapi
operator|.
name|ChildCollection
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
name|restapi
operator|.
name|IdString
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
name|restapi
operator|.
name|ResourceNotFoundException
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
name|restapi
operator|.
name|Response
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
name|restapi
operator|.
name|RestReadView
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
name|restapi
operator|.
name|RestView
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
name|Account
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
name|AccountPatchReview
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
name|PatchSet
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
name|server
operator|.
name|ReviewDb
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
name|CurrentUser
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
name|IdentifiedUser
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
name|patch
operator|.
name|PatchList
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
name|PatchListNotAvailableException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|server
operator|.
name|OrmException
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Provider
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
name|treewalk
operator|.
name|filter
operator|.
name|PathFilterGroup
import|;
end_import

begin_import
import|import
name|org
operator|.
name|kohsuke
operator|.
name|args4j
operator|.
name|Option
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_class
DECL|class|Files
class|class
name|Files
implements|implements
name|ChildCollection
argument_list|<
name|RevisionResource
argument_list|,
name|FileResource
argument_list|>
block|{
DECL|field|views
specifier|private
specifier|final
name|DynamicMap
argument_list|<
name|RestView
argument_list|<
name|FileResource
argument_list|>
argument_list|>
name|views
decl_stmt|;
DECL|field|list
specifier|private
specifier|final
name|Provider
argument_list|<
name|ListFiles
argument_list|>
name|list
decl_stmt|;
annotation|@
name|Inject
DECL|method|Files (DynamicMap<RestView<FileResource>> views, Provider<ListFiles> list)
name|Files
parameter_list|(
name|DynamicMap
argument_list|<
name|RestView
argument_list|<
name|FileResource
argument_list|>
argument_list|>
name|views
parameter_list|,
name|Provider
argument_list|<
name|ListFiles
argument_list|>
name|list
parameter_list|)
block|{
name|this
operator|.
name|views
operator|=
name|views
expr_stmt|;
name|this
operator|.
name|list
operator|=
name|list
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|views ()
specifier|public
name|DynamicMap
argument_list|<
name|RestView
argument_list|<
name|FileResource
argument_list|>
argument_list|>
name|views
parameter_list|()
block|{
return|return
name|views
return|;
block|}
annotation|@
name|Override
DECL|method|list ()
specifier|public
name|RestView
argument_list|<
name|RevisionResource
argument_list|>
name|list
parameter_list|()
throws|throws
name|AuthException
block|{
return|return
name|list
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|parse (RevisionResource rev, IdString id)
specifier|public
name|FileResource
name|parse
parameter_list|(
name|RevisionResource
name|rev
parameter_list|,
name|IdString
name|id
parameter_list|)
throws|throws
name|ResourceNotFoundException
throws|,
name|OrmException
throws|,
name|AuthException
block|{
return|return
operator|new
name|FileResource
argument_list|(
name|rev
argument_list|,
name|id
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
DECL|class|ListFiles
specifier|private
specifier|static
specifier|final
class|class
name|ListFiles
implements|implements
name|RestReadView
argument_list|<
name|RevisionResource
argument_list|>
block|{
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
name|ListFiles
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--base"
argument_list|,
name|metaVar
operator|=
literal|"revision-id"
argument_list|)
DECL|field|base
name|String
name|base
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--reviewed"
argument_list|)
DECL|field|reviewed
name|boolean
name|reviewed
decl_stmt|;
DECL|field|db
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
decl_stmt|;
DECL|field|self
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|self
decl_stmt|;
DECL|field|fileInfoJson
specifier|private
specifier|final
name|FileInfoJson
name|fileInfoJson
decl_stmt|;
DECL|field|revisions
specifier|private
specifier|final
name|Provider
argument_list|<
name|Revisions
argument_list|>
name|revisions
decl_stmt|;
DECL|field|gitManager
specifier|private
specifier|final
name|GitRepositoryManager
name|gitManager
decl_stmt|;
DECL|field|patchListCache
specifier|private
specifier|final
name|PatchListCache
name|patchListCache
decl_stmt|;
annotation|@
name|Inject
DECL|method|ListFiles (Provider<ReviewDb> db, Provider<CurrentUser> self, FileInfoJson fileInfoJson, Provider<Revisions> revisions, GitRepositoryManager gitManager, PatchListCache patchListCache)
name|ListFiles
parameter_list|(
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|,
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|self
parameter_list|,
name|FileInfoJson
name|fileInfoJson
parameter_list|,
name|Provider
argument_list|<
name|Revisions
argument_list|>
name|revisions
parameter_list|,
name|GitRepositoryManager
name|gitManager
parameter_list|,
name|PatchListCache
name|patchListCache
parameter_list|)
block|{
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|self
operator|=
name|self
expr_stmt|;
name|this
operator|.
name|fileInfoJson
operator|=
name|fileInfoJson
expr_stmt|;
name|this
operator|.
name|revisions
operator|=
name|revisions
expr_stmt|;
name|this
operator|.
name|gitManager
operator|=
name|gitManager
expr_stmt|;
name|this
operator|.
name|patchListCache
operator|=
name|patchListCache
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (RevisionResource resource)
specifier|public
name|Response
argument_list|<
name|?
argument_list|>
name|apply
parameter_list|(
name|RevisionResource
name|resource
parameter_list|)
throws|throws
name|AuthException
throws|,
name|BadRequestException
throws|,
name|ResourceNotFoundException
throws|,
name|OrmException
block|{
if|if
condition|(
name|base
operator|!=
literal|null
operator|&&
name|reviewed
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"cannot combine base and reviewed"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|reviewed
condition|)
block|{
return|return
name|Response
operator|.
name|ok
argument_list|(
name|reviewed
argument_list|(
name|resource
argument_list|)
argument_list|)
return|;
block|}
name|PatchSet
name|basePatchSet
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|base
operator|!=
literal|null
condition|)
block|{
name|RevisionResource
name|baseResource
init|=
name|revisions
operator|.
name|get
argument_list|()
operator|.
name|parse
argument_list|(
name|resource
operator|.
name|getChangeResource
argument_list|()
argument_list|,
name|IdString
operator|.
name|fromDecoded
argument_list|(
name|base
argument_list|)
argument_list|)
decl_stmt|;
name|basePatchSet
operator|=
name|baseResource
operator|.
name|getPatchSet
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|Response
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|FileInfo
argument_list|>
argument_list|>
name|r
init|=
name|Response
operator|.
name|ok
argument_list|(
name|fileInfoJson
operator|.
name|toFileInfoMap
argument_list|(
name|resource
operator|.
name|getChange
argument_list|()
argument_list|,
name|resource
operator|.
name|getPatchSet
argument_list|()
argument_list|,
name|basePatchSet
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|resource
operator|.
name|isCacheable
argument_list|()
condition|)
block|{
name|r
operator|.
name|caching
argument_list|(
name|CacheControl
operator|.
name|PRIVATE
argument_list|(
literal|7
argument_list|,
name|TimeUnit
operator|.
name|DAYS
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
catch|catch
parameter_list|(
name|PatchListNotAvailableException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|method|reviewed (RevisionResource resource)
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|reviewed
parameter_list|(
name|RevisionResource
name|resource
parameter_list|)
throws|throws
name|AuthException
throws|,
name|OrmException
block|{
name|CurrentUser
name|user
init|=
name|self
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|user
operator|.
name|isIdentifiedUser
argument_list|()
operator|)
condition|)
block|{
throw|throw
operator|new
name|AuthException
argument_list|(
literal|"Authentication required"
argument_list|)
throw|;
block|}
name|Account
operator|.
name|Id
name|userId
init|=
operator|(
operator|(
name|IdentifiedUser
operator|)
name|user
operator|)
operator|.
name|getAccountId
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|r
init|=
name|scan
argument_list|(
name|userId
argument_list|,
name|resource
operator|.
name|getPatchSet
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|.
name|isEmpty
argument_list|()
operator|&&
literal|1
operator|<
name|resource
operator|.
name|getPatchSet
argument_list|()
operator|.
name|getPatchSetId
argument_list|()
condition|)
block|{
for|for
control|(
name|Integer
name|id
range|:
name|reverseSortPatchSets
argument_list|(
name|resource
argument_list|)
control|)
block|{
name|PatchSet
operator|.
name|Id
name|old
init|=
operator|new
name|PatchSet
operator|.
name|Id
argument_list|(
name|resource
operator|.
name|getChange
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|id
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|o
init|=
name|scan
argument_list|(
name|userId
argument_list|,
name|old
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|o
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
try|try
block|{
name|r
operator|=
name|copy
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
name|o
argument_list|)
argument_list|,
name|old
argument_list|,
name|resource
argument_list|,
name|userId
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|PatchListNotAvailableException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Cannot copy patch review flags"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
block|}
block|}
return|return
name|r
return|;
block|}
DECL|method|scan (Account.Id userId, PatchSet.Id psId)
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|scan
parameter_list|(
name|Account
operator|.
name|Id
name|userId
parameter_list|,
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|)
throws|throws
name|OrmException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|r
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|AccountPatchReview
name|w
range|:
name|db
operator|.
name|get
argument_list|()
operator|.
name|accountPatchReviews
argument_list|()
operator|.
name|byReviewer
argument_list|(
name|userId
argument_list|,
name|psId
argument_list|)
control|)
block|{
name|r
operator|.
name|add
argument_list|(
name|w
operator|.
name|getKey
argument_list|()
operator|.
name|getPatchKey
argument_list|()
operator|.
name|getFileName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
DECL|method|reverseSortPatchSets ( RevisionResource resource)
specifier|private
name|List
argument_list|<
name|Integer
argument_list|>
name|reverseSortPatchSets
parameter_list|(
name|RevisionResource
name|resource
parameter_list|)
throws|throws
name|OrmException
block|{
name|SortedSet
argument_list|<
name|Integer
argument_list|>
name|ids
init|=
name|Sets
operator|.
name|newTreeSet
argument_list|()
decl_stmt|;
for|for
control|(
name|PatchSet
name|p
range|:
name|db
operator|.
name|get
argument_list|()
operator|.
name|patchSets
argument_list|()
operator|.
name|byChange
argument_list|(
name|resource
operator|.
name|getChange
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
control|)
block|{
if|if
condition|(
name|p
operator|.
name|getPatchSetId
argument_list|()
operator|<
name|resource
operator|.
name|getPatchSet
argument_list|()
operator|.
name|getPatchSetId
argument_list|()
condition|)
block|{
name|ids
operator|.
name|add
argument_list|(
name|p
operator|.
name|getPatchSetId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|Integer
argument_list|>
name|r
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|ids
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|reverse
argument_list|(
name|r
argument_list|)
expr_stmt|;
return|return
name|r
return|;
block|}
DECL|method|copy (Set<String> paths, PatchSet.Id old, RevisionResource resource, Account.Id userId)
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|copy
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|,
name|PatchSet
operator|.
name|Id
name|old
parameter_list|,
name|RevisionResource
name|resource
parameter_list|,
name|Account
operator|.
name|Id
name|userId
parameter_list|)
throws|throws
name|IOException
throws|,
name|PatchListNotAvailableException
throws|,
name|OrmException
block|{
name|Repository
name|git
init|=
name|gitManager
operator|.
name|openRepository
argument_list|(
name|resource
operator|.
name|getChange
argument_list|()
operator|.
name|getProject
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|ObjectReader
name|reader
init|=
name|git
operator|.
name|newObjectReader
argument_list|()
decl_stmt|;
try|try
block|{
name|PatchList
name|oldList
init|=
name|patchListCache
operator|.
name|get
argument_list|(
name|resource
operator|.
name|getChange
argument_list|()
argument_list|,
name|db
operator|.
name|get
argument_list|()
operator|.
name|patchSets
argument_list|()
operator|.
name|get
argument_list|(
name|old
argument_list|)
argument_list|)
decl_stmt|;
name|PatchList
name|curList
init|=
name|patchListCache
operator|.
name|get
argument_list|(
name|resource
operator|.
name|getChange
argument_list|()
argument_list|,
name|resource
operator|.
name|getPatchSet
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|sz
init|=
name|paths
operator|.
name|size
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|AccountPatchReview
argument_list|>
name|inserts
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|sz
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|pathList
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|sz
argument_list|)
decl_stmt|;
name|RevWalk
name|rw
init|=
operator|new
name|RevWalk
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|TreeWalk
name|tw
init|=
operator|new
name|TreeWalk
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|tw
operator|.
name|setFilter
argument_list|(
name|PathFilterGroup
operator|.
name|createFromStrings
argument_list|(
name|paths
argument_list|)
argument_list|)
expr_stmt|;
name|tw
operator|.
name|setRecursive
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|int
name|o
init|=
name|tw
operator|.
name|addTree
argument_list|(
name|rw
operator|.
name|parseCommit
argument_list|(
name|oldList
operator|.
name|getNewId
argument_list|()
argument_list|)
operator|.
name|getTree
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|c
init|=
name|tw
operator|.
name|addTree
argument_list|(
name|rw
operator|.
name|parseCommit
argument_list|(
name|curList
operator|.
name|getNewId
argument_list|()
argument_list|)
operator|.
name|getTree
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|op
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|oldList
operator|.
name|getOldId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|op
operator|=
name|tw
operator|.
name|addTree
argument_list|(
name|rw
operator|.
name|parseTree
argument_list|(
name|oldList
operator|.
name|getOldId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|int
name|cp
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|curList
operator|.
name|getOldId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|cp
operator|=
name|tw
operator|.
name|addTree
argument_list|(
name|rw
operator|.
name|parseTree
argument_list|(
name|curList
operator|.
name|getOldId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
name|tw
operator|.
name|next
argument_list|()
condition|)
block|{
name|String
name|path
init|=
name|tw
operator|.
name|getPathString
argument_list|()
decl_stmt|;
if|if
condition|(
name|tw
operator|.
name|getRawMode
argument_list|(
name|o
argument_list|)
operator|!=
literal|0
operator|&&
name|tw
operator|.
name|getRawMode
argument_list|(
name|c
argument_list|)
operator|!=
literal|0
operator|&&
name|tw
operator|.
name|idEqual
argument_list|(
name|o
argument_list|,
name|c
argument_list|)
operator|&&
name|paths
operator|.
name|contains
argument_list|(
name|path
argument_list|)
condition|)
block|{
comment|// File exists in previously reviewed oldList and in curList.
comment|// File content is identical.
name|inserts
operator|.
name|add
argument_list|(
operator|new
name|AccountPatchReview
argument_list|(
operator|new
name|Patch
operator|.
name|Key
argument_list|(
name|resource
operator|.
name|getPatchSet
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|path
argument_list|)
argument_list|,
name|userId
argument_list|)
argument_list|)
expr_stmt|;
name|pathList
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|op
operator|>=
literal|0
operator|&&
name|cp
operator|>=
literal|0
operator|&&
name|tw
operator|.
name|getRawMode
argument_list|(
name|o
argument_list|)
operator|==
literal|0
operator|&&
name|tw
operator|.
name|getRawMode
argument_list|(
name|c
argument_list|)
operator|==
literal|0
operator|&&
name|tw
operator|.
name|getRawMode
argument_list|(
name|op
argument_list|)
operator|!=
literal|0
operator|&&
name|tw
operator|.
name|getRawMode
argument_list|(
name|cp
argument_list|)
operator|!=
literal|0
operator|&&
name|tw
operator|.
name|idEqual
argument_list|(
name|op
argument_list|,
name|cp
argument_list|)
operator|&&
name|paths
operator|.
name|contains
argument_list|(
name|path
argument_list|)
condition|)
block|{
comment|// File was deleted in previously reviewed oldList and curList.
comment|// File exists in ancestor of oldList and curList.
comment|// File content is identical in ancestors.
name|inserts
operator|.
name|add
argument_list|(
operator|new
name|AccountPatchReview
argument_list|(
operator|new
name|Patch
operator|.
name|Key
argument_list|(
name|resource
operator|.
name|getPatchSet
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|path
argument_list|)
argument_list|,
name|userId
argument_list|)
argument_list|)
expr_stmt|;
name|pathList
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
name|db
operator|.
name|get
argument_list|()
operator|.
name|accountPatchReviews
argument_list|()
operator|.
name|insert
argument_list|(
name|inserts
argument_list|)
expr_stmt|;
return|return
name|pathList
return|;
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|git
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

