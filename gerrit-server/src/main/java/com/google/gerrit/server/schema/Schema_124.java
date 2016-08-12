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
DECL|package|com.google.gerrit.server.schema
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|schema
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
name|common
operator|.
name|collect
operator|.
name|ArrayListMultimap
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
name|Multimap
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
name|Ordering
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
name|GerritPersonIdent
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
name|account
operator|.
name|VersionedAuthorizedKeys
operator|.
name|SimpleSshKeyCreator
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
name|AllUsersName
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
name|extensions
operator|.
name|events
operator|.
name|GitReferenceUpdated
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
name|MetaDataUpdate
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
name|jdbc
operator|.
name|JdbcSchema
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
name|BatchRefUpdate
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
name|NullProgressMonitor
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
name|sql
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Statement
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
name|Collection
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

begin_class
DECL|class|Schema_124
specifier|public
class|class
name|Schema_124
extends|extends
name|SchemaVersion
block|{
DECL|field|repoManager
specifier|private
specifier|final
name|GitRepositoryManager
name|repoManager
decl_stmt|;
DECL|field|allUsersName
specifier|private
specifier|final
name|AllUsersName
name|allUsersName
decl_stmt|;
DECL|field|serverUser
specifier|private
specifier|final
name|PersonIdent
name|serverUser
decl_stmt|;
annotation|@
name|Inject
DECL|method|Schema_124 (Provider<Schema_123> prior, GitRepositoryManager repoManager, AllUsersName allUsersName, @GerritPersonIdent PersonIdent serverUser)
name|Schema_124
parameter_list|(
name|Provider
argument_list|<
name|Schema_123
argument_list|>
name|prior
parameter_list|,
name|GitRepositoryManager
name|repoManager
parameter_list|,
name|AllUsersName
name|allUsersName
parameter_list|,
annotation|@
name|GerritPersonIdent
name|PersonIdent
name|serverUser
parameter_list|)
block|{
name|super
argument_list|(
name|prior
argument_list|)
expr_stmt|;
name|this
operator|.
name|repoManager
operator|=
name|repoManager
expr_stmt|;
name|this
operator|.
name|allUsersName
operator|=
name|allUsersName
expr_stmt|;
name|this
operator|.
name|serverUser
operator|=
name|serverUser
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|migrateData (ReviewDb db, UpdateUI ui)
specifier|protected
name|void
name|migrateData
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|UpdateUI
name|ui
parameter_list|)
throws|throws
name|OrmException
throws|,
name|SQLException
block|{
name|Multimap
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|AccountSshKey
argument_list|>
name|imports
init|=
name|ArrayListMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
try|try
init|(
name|Statement
name|stmt
init|=
operator|(
operator|(
name|JdbcSchema
operator|)
name|db
operator|)
operator|.
name|getConnection
argument_list|()
operator|.
name|createStatement
argument_list|()
init|;
name|ResultSet
name|rs
operator|=
name|stmt
operator|.
name|executeQuery
argument_list|(
literal|"SELECT "
operator|+
literal|"account_id, "
operator|+
literal|"seq, "
operator|+
literal|"ssh_public_key, "
operator|+
literal|"valid "
operator|+
literal|"FROM account_ssh_keys"
argument_list|)
init|)
block|{
while|while
condition|(
name|rs
operator|.
name|next
argument_list|()
condition|)
block|{
name|Account
operator|.
name|Id
name|accountId
init|=
operator|new
name|Account
operator|.
name|Id
argument_list|(
name|rs
operator|.
name|getInt
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|seq
init|=
name|rs
operator|.
name|getInt
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|String
name|sshPublicKey
init|=
name|rs
operator|.
name|getString
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|AccountSshKey
name|key
init|=
operator|new
name|AccountSshKey
argument_list|(
operator|new
name|AccountSshKey
operator|.
name|Id
argument_list|(
name|accountId
argument_list|,
name|seq
argument_list|)
argument_list|,
name|sshPublicKey
argument_list|)
decl_stmt|;
name|boolean
name|valid
init|=
name|toBoolean
argument_list|(
name|rs
operator|.
name|getString
argument_list|(
literal|4
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|valid
condition|)
block|{
name|key
operator|.
name|setInvalid
argument_list|()
expr_stmt|;
block|}
name|imports
operator|.
name|put
argument_list|(
name|accountId
argument_list|,
name|key
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|imports
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
try|try
init|(
name|Repository
name|git
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|allUsersName
argument_list|)
init|;
name|RevWalk
name|rw
operator|=
operator|new
name|RevWalk
argument_list|(
name|git
argument_list|)
init|)
block|{
name|BatchRefUpdate
name|bru
init|=
name|git
operator|.
name|getRefDatabase
argument_list|()
operator|.
name|newBatchUpdate
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|Collection
argument_list|<
name|AccountSshKey
argument_list|>
argument_list|>
name|e
range|:
name|imports
operator|.
name|asMap
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
try|try
init|(
name|MetaDataUpdate
name|md
init|=
operator|new
name|MetaDataUpdate
argument_list|(
name|GitReferenceUpdated
operator|.
name|DISABLED
argument_list|,
name|allUsersName
argument_list|,
name|git
argument_list|,
name|bru
argument_list|)
init|)
block|{
name|md
operator|.
name|getCommitBuilder
argument_list|()
operator|.
name|setAuthor
argument_list|(
name|serverUser
argument_list|)
expr_stmt|;
name|md
operator|.
name|getCommitBuilder
argument_list|()
operator|.
name|setCommitter
argument_list|(
name|serverUser
argument_list|)
expr_stmt|;
name|VersionedAuthorizedKeys
name|authorizedKeys
init|=
operator|new
name|VersionedAuthorizedKeys
argument_list|(
operator|new
name|SimpleSshKeyCreator
argument_list|()
argument_list|,
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|authorizedKeys
operator|.
name|load
argument_list|(
name|md
argument_list|)
expr_stmt|;
name|authorizedKeys
operator|.
name|setKeys
argument_list|(
name|fixInvalidSequenceNumbers
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|authorizedKeys
operator|.
name|commit
argument_list|(
name|md
argument_list|)
expr_stmt|;
block|}
block|}
name|bru
operator|.
name|execute
argument_list|(
name|rw
argument_list|,
name|NullProgressMonitor
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConfigInvalidException
decl||
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|OrmException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
DECL|method|fixInvalidSequenceNumbers ( Collection<AccountSshKey> keys)
specifier|private
name|Collection
argument_list|<
name|AccountSshKey
argument_list|>
name|fixInvalidSequenceNumbers
parameter_list|(
name|Collection
argument_list|<
name|AccountSshKey
argument_list|>
name|keys
parameter_list|)
block|{
name|Ordering
argument_list|<
name|AccountSshKey
argument_list|>
name|o
init|=
name|Ordering
operator|.
name|natural
argument_list|()
operator|.
name|onResultOf
argument_list|(
operator|new
name|Function
argument_list|<
name|AccountSshKey
argument_list|,
name|Integer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Integer
name|apply
parameter_list|(
name|AccountSshKey
name|sshKey
parameter_list|)
block|{
return|return
name|sshKey
operator|.
name|getKey
argument_list|()
operator|.
name|get
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|AccountSshKey
argument_list|>
name|fixedKeys
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|keys
argument_list|)
decl_stmt|;
name|AccountSshKey
name|minKey
init|=
name|o
operator|.
name|min
argument_list|(
name|keys
argument_list|)
decl_stmt|;
while|while
condition|(
name|minKey
operator|.
name|getKey
argument_list|()
operator|.
name|get
argument_list|()
operator|<=
literal|0
condition|)
block|{
name|AccountSshKey
name|fixedKey
init|=
operator|new
name|AccountSshKey
argument_list|(
operator|new
name|AccountSshKey
operator|.
name|Id
argument_list|(
name|minKey
operator|.
name|getKey
argument_list|()
operator|.
name|getParentKey
argument_list|()
argument_list|,
name|Math
operator|.
name|max
argument_list|(
name|o
operator|.
name|max
argument_list|(
name|keys
argument_list|)
operator|.
name|getKey
argument_list|()
operator|.
name|get
argument_list|()
operator|+
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|,
name|minKey
operator|.
name|getSshPublicKey
argument_list|()
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|replaceAll
argument_list|(
name|fixedKeys
argument_list|,
name|minKey
argument_list|,
name|fixedKey
argument_list|)
expr_stmt|;
name|minKey
operator|=
name|o
operator|.
name|min
argument_list|(
name|fixedKeys
argument_list|)
expr_stmt|;
block|}
return|return
name|fixedKeys
return|;
block|}
DECL|method|toBoolean (String v)
specifier|private
specifier|static
name|boolean
name|toBoolean
parameter_list|(
name|String
name|v
parameter_list|)
block|{
return|return
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|v
argument_list|)
operator|&&
name|v
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"Y"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

