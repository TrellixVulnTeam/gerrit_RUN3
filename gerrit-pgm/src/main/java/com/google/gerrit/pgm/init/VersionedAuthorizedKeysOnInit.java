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
name|checkState
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
name|Optional
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
name|Strings
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
name|AccountSshKey
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
name|account
operator|.
name|AuthorizedKeys
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
name|VersionedAuthorizedKeys
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
name|gerrit
operator|.
name|server
operator|.
name|git
operator|.
name|VersionedMetaData
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
name|assistedinject
operator|.
name|Assisted
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
name|FS
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_class
DECL|class|VersionedAuthorizedKeysOnInit
specifier|public
class|class
name|VersionedAuthorizedKeysOnInit
extends|extends
name|VersionedMetaData
block|{
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (Account.Id accountId)
name|VersionedAuthorizedKeysOnInit
name|create
parameter_list|(
name|Account
operator|.
name|Id
name|accountId
parameter_list|)
function_decl|;
block|}
DECL|field|accountId
specifier|private
specifier|final
name|Account
operator|.
name|Id
name|accountId
decl_stmt|;
DECL|field|ref
specifier|private
specifier|final
name|String
name|ref
decl_stmt|;
DECL|field|project
specifier|private
specifier|final
name|String
name|project
decl_stmt|;
DECL|field|site
specifier|private
specifier|final
name|SitePaths
name|site
decl_stmt|;
DECL|field|flags
specifier|private
specifier|final
name|InitFlags
name|flags
decl_stmt|;
DECL|field|keys
specifier|private
name|List
argument_list|<
name|Optional
argument_list|<
name|AccountSshKey
argument_list|>
argument_list|>
name|keys
decl_stmt|;
DECL|field|revision
specifier|private
name|ObjectId
name|revision
decl_stmt|;
annotation|@
name|Inject
DECL|method|VersionedAuthorizedKeysOnInit ( AllUsersNameOnInitProvider allUsers, SitePaths site, InitFlags flags, @Assisted Account.Id accountId)
specifier|public
name|VersionedAuthorizedKeysOnInit
parameter_list|(
name|AllUsersNameOnInitProvider
name|allUsers
parameter_list|,
name|SitePaths
name|site
parameter_list|,
name|InitFlags
name|flags
parameter_list|,
annotation|@
name|Assisted
name|Account
operator|.
name|Id
name|accountId
parameter_list|)
block|{
name|this
operator|.
name|project
operator|=
name|allUsers
operator|.
name|get
argument_list|()
expr_stmt|;
name|this
operator|.
name|site
operator|=
name|site
expr_stmt|;
name|this
operator|.
name|flags
operator|=
name|flags
expr_stmt|;
name|this
operator|.
name|accountId
operator|=
name|accountId
expr_stmt|;
name|this
operator|.
name|ref
operator|=
name|RefNames
operator|.
name|refsUsers
argument_list|(
name|accountId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getRefName ()
specifier|protected
name|String
name|getRefName
parameter_list|()
block|{
return|return
name|ref
return|;
block|}
DECL|method|load ()
specifier|public
name|VersionedAuthorizedKeysOnInit
name|load
parameter_list|()
throws|throws
name|IOException
throws|,
name|ConfigInvalidException
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
init|)
block|{
name|load
argument_list|(
name|repo
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|this
return|;
block|}
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
if|if
condition|(
name|basePath
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"gerrit.basePath must be configured"
argument_list|)
throw|;
block|}
return|return
name|FileKey
operator|.
name|resolve
argument_list|(
name|basePath
operator|.
name|resolve
argument_list|(
name|project
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
annotation|@
name|Override
DECL|method|onLoad ()
specifier|protected
name|void
name|onLoad
parameter_list|()
throws|throws
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|revision
operator|=
name|getRevision
argument_list|()
expr_stmt|;
name|keys
operator|=
name|AuthorizedKeys
operator|.
name|parse
argument_list|(
name|accountId
argument_list|,
name|readUTF8
argument_list|(
name|AuthorizedKeys
operator|.
name|FILE_NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|addKey (String pub)
specifier|public
name|AccountSshKey
name|addKey
parameter_list|(
name|String
name|pub
parameter_list|)
block|{
name|checkState
argument_list|(
name|keys
operator|!=
literal|null
argument_list|,
literal|"SSH keys not loaded yet"
argument_list|)
expr_stmt|;
name|int
name|seq
init|=
name|keys
operator|.
name|isEmpty
argument_list|()
condition|?
literal|1
else|:
name|keys
operator|.
name|size
argument_list|()
operator|+
literal|1
decl_stmt|;
name|AccountSshKey
operator|.
name|Id
name|keyId
init|=
operator|new
name|AccountSshKey
operator|.
name|Id
argument_list|(
name|accountId
argument_list|,
name|seq
argument_list|)
decl_stmt|;
name|AccountSshKey
name|key
init|=
operator|new
name|VersionedAuthorizedKeys
operator|.
name|SimpleSshKeyCreator
argument_list|()
operator|.
name|create
argument_list|(
name|keyId
argument_list|,
name|pub
argument_list|)
decl_stmt|;
name|keys
operator|.
name|add
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|key
return|;
block|}
DECL|method|save (String message)
specifier|public
name|void
name|save
parameter_list|(
name|String
name|message
parameter_list|)
throws|throws
name|IOException
block|{
name|save
argument_list|(
operator|new
name|PersonIdent
argument_list|(
literal|"Gerrit Initialization"
argument_list|,
literal|"init@gerrit"
argument_list|)
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
DECL|method|save (PersonIdent ident, String msg)
specifier|private
name|void
name|save
parameter_list|(
name|PersonIdent
name|ident
parameter_list|,
name|String
name|msg
parameter_list|)
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
throw|throw
operator|new
name|IOException
argument_list|(
name|project
operator|+
literal|" does not exist."
argument_list|)
throw|;
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
init|;
name|ObjectInserter
name|i
operator|=
name|repo
operator|.
name|newObjectInserter
argument_list|()
init|;
name|ObjectReader
name|r
operator|=
name|repo
operator|.
name|newObjectReader
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
name|inserter
operator|=
name|i
expr_stmt|;
name|reader
operator|=
name|r
expr_stmt|;
name|RevTree
name|srcTree
init|=
name|revision
operator|!=
literal|null
condition|?
name|rw
operator|.
name|parseTree
argument_list|(
name|revision
argument_list|)
else|:
literal|null
decl_stmt|;
name|newTree
operator|=
name|readTree
argument_list|(
name|srcTree
argument_list|)
expr_stmt|;
name|CommitBuilder
name|commit
init|=
operator|new
name|CommitBuilder
argument_list|()
decl_stmt|;
name|commit
operator|.
name|setAuthor
argument_list|(
name|ident
argument_list|)
expr_stmt|;
name|commit
operator|.
name|setCommitter
argument_list|(
name|ident
argument_list|)
expr_stmt|;
name|commit
operator|.
name|setMessage
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|onSave
argument_list|(
name|commit
argument_list|)
expr_stmt|;
name|ObjectId
name|res
init|=
name|newTree
operator|.
name|writeTree
argument_list|(
name|inserter
argument_list|)
decl_stmt|;
if|if
condition|(
name|res
operator|.
name|equals
argument_list|(
name|srcTree
argument_list|)
condition|)
block|{
return|return;
block|}
name|commit
operator|.
name|setTreeId
argument_list|(
name|res
argument_list|)
expr_stmt|;
if|if
condition|(
name|revision
operator|!=
literal|null
condition|)
block|{
name|commit
operator|.
name|addParentId
argument_list|(
name|revision
argument_list|)
expr_stmt|;
block|}
name|ObjectId
name|newRevision
init|=
name|inserter
operator|.
name|insert
argument_list|(
name|commit
argument_list|)
decl_stmt|;
name|updateRef
argument_list|(
name|repo
argument_list|,
name|ident
argument_list|,
name|newRevision
argument_list|,
literal|"commit: "
operator|+
name|msg
argument_list|)
expr_stmt|;
name|revision
operator|=
name|newRevision
expr_stmt|;
block|}
finally|finally
block|{
name|inserter
operator|=
literal|null
expr_stmt|;
name|reader
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|onSave (CommitBuilder commit)
specifier|protected
name|boolean
name|onSave
parameter_list|(
name|CommitBuilder
name|commit
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|commit
operator|.
name|getMessage
argument_list|()
argument_list|)
condition|)
block|{
name|commit
operator|.
name|setMessage
argument_list|(
literal|"Updated SSH keys\n"
argument_list|)
expr_stmt|;
block|}
name|saveUTF8
argument_list|(
name|AuthorizedKeys
operator|.
name|FILE_NAME
argument_list|,
name|AuthorizedKeys
operator|.
name|serialize
argument_list|(
name|keys
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|updateRef (Repository repo, PersonIdent ident, ObjectId newRevision, String refLogMsg)
specifier|private
name|void
name|updateRef
parameter_list|(
name|Repository
name|repo
parameter_list|,
name|PersonIdent
name|ident
parameter_list|,
name|ObjectId
name|newRevision
parameter_list|,
name|String
name|refLogMsg
parameter_list|)
throws|throws
name|IOException
block|{
name|RefUpdate
name|ru
init|=
name|repo
operator|.
name|updateRef
argument_list|(
name|getRefName
argument_list|()
argument_list|)
decl_stmt|;
name|ru
operator|.
name|setRefLogIdent
argument_list|(
name|ident
argument_list|)
expr_stmt|;
name|ru
operator|.
name|setNewObjectId
argument_list|(
name|newRevision
argument_list|)
expr_stmt|;
name|ru
operator|.
name|setExpectedOldObjectId
argument_list|(
name|revision
argument_list|)
expr_stmt|;
name|ru
operator|.
name|setRefLogMessage
argument_list|(
name|refLogMsg
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|RefUpdate
operator|.
name|Result
name|r
init|=
name|ru
operator|.
name|update
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|r
condition|)
block|{
case|case
name|FAST_FORWARD
case|:
case|case
name|NEW
case|:
case|case
name|NO_CHANGE
case|:
break|break;
case|case
name|FORCED
case|:
case|case
name|IO_FAILURE
case|:
case|case
name|LOCK_FAILURE
case|:
case|case
name|NOT_ATTEMPTED
case|:
case|case
name|REJECTED
case|:
case|case
name|REJECTED_CURRENT_BRANCH
case|:
case|case
name|RENAMED
case|:
default|default:
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to update "
operator|+
name|getRefName
argument_list|()
operator|+
literal|" of "
operator|+
name|project
operator|+
literal|": "
operator|+
name|r
operator|.
name|name
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

