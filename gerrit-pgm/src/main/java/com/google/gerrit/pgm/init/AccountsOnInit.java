begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2017 The Android Open Source Project
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
DECL|package|com.google.gerrit.pgm.init
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|pgm
operator|.
name|init
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
name|gerrit
operator|.
name|pgm
operator|.
name|init
operator|.
name|api
operator|.
name|AllUsersNameOnInitProvider
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
name|pgm
operator|.
name|init
operator|.
name|api
operator|.
name|InitFlags
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
name|GerritPersonIdentProvider
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
name|account
operator|.
name|AccountConfig
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
name|account
operator|.
name|Accounts
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
name|SitePaths
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
name|java
operator|.
name|io
operator|.
name|File
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
name|nio
operator|.
name|file
operator|.
name|Path
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
name|DirCacheEditor
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
name|DirCacheEditor
operator|.
name|PathEdit
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
name|internal
operator|.
name|storage
operator|.
name|file
operator|.
name|FileRepository
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
name|CommitBuilder
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
name|PersonIdent
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
name|RefUpdate
operator|.
name|Result
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
name|lib
operator|.
name|RepositoryCache
operator|.
name|FileKey
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
name|FS
import|;
end_import

begin_class
DECL|class|AccountsOnInit
specifier|public
class|class
name|AccountsOnInit
block|{
DECL|field|flags
specifier|private
specifier|final
name|InitFlags
name|flags
decl_stmt|;
DECL|field|site
specifier|private
specifier|final
name|SitePaths
name|site
decl_stmt|;
DECL|field|allUsers
specifier|private
specifier|final
name|String
name|allUsers
decl_stmt|;
annotation|@
name|Inject
DECL|method|AccountsOnInit (InitFlags flags, SitePaths site, AllUsersNameOnInitProvider allUsers)
specifier|public
name|AccountsOnInit
parameter_list|(
name|InitFlags
name|flags
parameter_list|,
name|SitePaths
name|site
parameter_list|,
name|AllUsersNameOnInitProvider
name|allUsers
parameter_list|)
block|{
name|this
operator|.
name|flags
operator|=
name|flags
expr_stmt|;
name|this
operator|.
name|site
operator|=
name|site
expr_stmt|;
name|this
operator|.
name|allUsers
operator|=
name|allUsers
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
DECL|method|insert (ReviewDb db, Account account)
specifier|public
name|void
name|insert
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|Account
name|account
parameter_list|)
throws|throws
name|OrmException
throws|,
name|IOException
block|{
name|db
operator|.
name|accounts
argument_list|()
operator|.
name|insert
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|account
argument_list|)
argument_list|)
expr_stmt|;
name|File
name|path
init|=
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
try|try
init|(
name|Repository
name|repo
init|=
operator|new
name|FileRepository
argument_list|(
name|path
argument_list|)
init|;           ObjectInserter oi = repo.newObjectInserter()
block|)
block|{
name|PersonIdent
name|ident
init|=
operator|new
name|PersonIdent
argument_list|(
operator|new
name|GerritPersonIdentProvider
argument_list|(
name|flags
operator|.
name|cfg
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
name|account
operator|.
name|getRegisteredOn
argument_list|()
argument_list|)
decl_stmt|;
name|Config
name|accountConfig
init|=
operator|new
name|Config
argument_list|()
decl_stmt|;
name|AccountConfig
operator|.
name|writeToConfig
argument_list|(
name|account
argument_list|,
name|accountConfig
argument_list|)
expr_stmt|;
name|DirCache
name|newTree
init|=
name|DirCache
operator|.
name|newInCore
argument_list|()
decl_stmt|;
name|DirCacheEditor
name|editor
init|=
name|newTree
operator|.
name|editor
argument_list|()
decl_stmt|;
specifier|final
name|ObjectId
name|blobId
init|=
name|oi
operator|.
name|insert
argument_list|(
name|Constants
operator|.
name|OBJ_BLOB
argument_list|,
name|accountConfig
operator|.
name|toText
argument_list|()
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|editor
operator|.
name|add
argument_list|(
operator|new
name|PathEdit
argument_list|(
name|AccountConfig
operator|.
name|ACCOUNT_CONFIG
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|apply
parameter_list|(
name|DirCacheEntry
name|ent
parameter_list|)
block|{
name|ent
operator|.
name|setFileMode
argument_list|(
name|FileMode
operator|.
name|REGULAR_FILE
argument_list|)
expr_stmt|;
name|ent
operator|.
name|setObjectId
argument_list|(
name|blobId
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|editor
operator|.
name|finish
argument_list|()
expr_stmt|;
name|ObjectId
name|treeId
init|=
name|newTree
operator|.
name|writeTree
argument_list|(
name|oi
argument_list|)
decl_stmt|;
name|CommitBuilder
name|cb
init|=
operator|new
name|CommitBuilder
argument_list|()
decl_stmt|;
name|cb
operator|.
name|setTreeId
argument_list|(
name|treeId
argument_list|)
expr_stmt|;
name|cb
operator|.
name|setCommitter
argument_list|(
name|ident
argument_list|)
expr_stmt|;
name|cb
operator|.
name|setAuthor
argument_list|(
name|ident
argument_list|)
expr_stmt|;
name|cb
operator|.
name|setMessage
argument_list|(
literal|"Create Account"
argument_list|)
expr_stmt|;
name|ObjectId
name|id
init|=
name|oi
operator|.
name|insert
argument_list|(
name|cb
argument_list|)
decl_stmt|;
name|oi
operator|.
name|flush
argument_list|()
expr_stmt|;
name|String
name|refName
init|=
name|RefNames
operator|.
name|refsUsers
argument_list|(
name|account
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|RefUpdate
name|ru
init|=
name|repo
operator|.
name|updateRef
argument_list|(
name|refName
argument_list|)
decl_stmt|;
name|ru
operator|.
name|setExpectedOldObjectId
argument_list|(
name|ObjectId
operator|.
name|zeroId
argument_list|()
argument_list|)
expr_stmt|;
name|ru
operator|.
name|setNewObjectId
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|ru
operator|.
name|setRefLogIdent
argument_list|(
name|ident
argument_list|)
expr_stmt|;
name|ru
operator|.
name|setRefLogMessage
argument_list|(
literal|"Create Account"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Result
name|result
init|=
name|ru
operator|.
name|update
argument_list|()
decl_stmt|;
if|if
condition|(
name|result
operator|!=
name|Result
operator|.
name|NEW
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Failed to update ref %s: %s"
argument_list|,
name|refName
argument_list|,
name|result
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
name|account
operator|.
name|setMetaId
argument_list|(
name|id
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_function
DECL|method|hasAnyAccount ()
specifier|public
name|boolean
name|hasAnyAccount
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|path
init|=
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
try|try
init|(
name|Repository
name|repo
init|=
operator|new
name|FileRepository
argument_list|(
name|path
argument_list|)
init|)
block|{
return|return
name|Accounts
operator|.
name|hasAnyAccount
argument_list|(
name|repo
argument_list|)
return|;
block|}
block|}
end_function

begin_function
DECL|method|getPath ()
specifier|private
name|File
name|getPath
parameter_list|()
block|{
name|Path
name|basePath
init|=
name|site
operator|.
name|resolve
argument_list|(
name|flags
operator|.
name|cfg
operator|.
name|getString
argument_list|(
literal|"gerrit"
argument_list|,
literal|null
argument_list|,
literal|"basePath"
argument_list|)
argument_list|)
decl_stmt|;
name|checkArgument
argument_list|(
name|basePath
operator|!=
literal|null
argument_list|,
literal|"gerrit.basePath must be configured"
argument_list|)
expr_stmt|;
return|return
name|FileKey
operator|.
name|resolve
argument_list|(
name|basePath
operator|.
name|resolve
argument_list|(
name|allUsers
argument_list|)
operator|.
name|toFile
argument_list|()
argument_list|,
name|FS
operator|.
name|DETECTED
argument_list|)
return|;
block|}
end_function

unit|}
end_unit

