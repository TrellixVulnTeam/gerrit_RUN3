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
DECL|package|com.google.gerrit.acceptance
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
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
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
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
name|US_ASCII
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
name|TimeUtil
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
name|AccountGroup
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
name|AccountGroupMember
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
name|account
operator|.
name|AccountByEmailCache
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
name|AccountCache
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
name|ExternalId
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
name|ExternalIdsUpdate
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
name|GroupCache
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
name|index
operator|.
name|account
operator|.
name|AccountIndexer
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
name|ssh
operator|.
name|SshKeyCache
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
name|testutil
operator|.
name|SshMode
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
name|SchemaFactory
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
name|Singleton
import|;
end_import

begin_import
import|import
name|com
operator|.
name|jcraft
operator|.
name|jsch
operator|.
name|JSch
import|;
end_import

begin_import
import|import
name|com
operator|.
name|jcraft
operator|.
name|jsch
operator|.
name|JSchException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|jcraft
operator|.
name|jsch
operator|.
name|KeyPair
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
name|UnsupportedEncodingException
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

begin_class
annotation|@
name|Singleton
DECL|class|AccountCreator
specifier|public
class|class
name|AccountCreator
block|{
DECL|field|accounts
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|TestAccount
argument_list|>
name|accounts
decl_stmt|;
DECL|field|reviewDbProvider
specifier|private
specifier|final
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|reviewDbProvider
decl_stmt|;
DECL|field|authorizedKeys
specifier|private
specifier|final
name|VersionedAuthorizedKeys
operator|.
name|Accessor
name|authorizedKeys
decl_stmt|;
DECL|field|groupCache
specifier|private
specifier|final
name|GroupCache
name|groupCache
decl_stmt|;
DECL|field|sshKeyCache
specifier|private
specifier|final
name|SshKeyCache
name|sshKeyCache
decl_stmt|;
DECL|field|accountCache
specifier|private
specifier|final
name|AccountCache
name|accountCache
decl_stmt|;
DECL|field|byEmailCache
specifier|private
specifier|final
name|AccountByEmailCache
name|byEmailCache
decl_stmt|;
DECL|field|indexer
specifier|private
specifier|final
name|AccountIndexer
name|indexer
decl_stmt|;
DECL|field|externalIdsUpdate
specifier|private
specifier|final
name|ExternalIdsUpdate
operator|.
name|Server
name|externalIdsUpdate
decl_stmt|;
annotation|@
name|Inject
DECL|method|AccountCreator ( SchemaFactory<ReviewDb> schema, VersionedAuthorizedKeys.Accessor authorizedKeys, GroupCache groupCache, SshKeyCache sshKeyCache, AccountCache accountCache, AccountByEmailCache byEmailCache, AccountIndexer indexer, ExternalIdsUpdate.Server externalIdsUpdate)
name|AccountCreator
parameter_list|(
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|schema
parameter_list|,
name|VersionedAuthorizedKeys
operator|.
name|Accessor
name|authorizedKeys
parameter_list|,
name|GroupCache
name|groupCache
parameter_list|,
name|SshKeyCache
name|sshKeyCache
parameter_list|,
name|AccountCache
name|accountCache
parameter_list|,
name|AccountByEmailCache
name|byEmailCache
parameter_list|,
name|AccountIndexer
name|indexer
parameter_list|,
name|ExternalIdsUpdate
operator|.
name|Server
name|externalIdsUpdate
parameter_list|)
block|{
name|accounts
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|reviewDbProvider
operator|=
name|schema
expr_stmt|;
name|this
operator|.
name|authorizedKeys
operator|=
name|authorizedKeys
expr_stmt|;
name|this
operator|.
name|groupCache
operator|=
name|groupCache
expr_stmt|;
name|this
operator|.
name|sshKeyCache
operator|=
name|sshKeyCache
expr_stmt|;
name|this
operator|.
name|accountCache
operator|=
name|accountCache
expr_stmt|;
name|this
operator|.
name|byEmailCache
operator|=
name|byEmailCache
expr_stmt|;
name|this
operator|.
name|indexer
operator|=
name|indexer
expr_stmt|;
name|this
operator|.
name|externalIdsUpdate
operator|=
name|externalIdsUpdate
expr_stmt|;
block|}
DECL|method|create ( String username, String email, String fullName, String... groups)
specifier|public
specifier|synchronized
name|TestAccount
name|create
parameter_list|(
name|String
name|username
parameter_list|,
name|String
name|email
parameter_list|,
name|String
name|fullName
parameter_list|,
name|String
modifier|...
name|groups
parameter_list|)
throws|throws
name|Exception
block|{
name|TestAccount
name|account
init|=
name|accounts
operator|.
name|get
argument_list|(
name|username
argument_list|)
decl_stmt|;
if|if
condition|(
name|account
operator|!=
literal|null
condition|)
block|{
return|return
name|account
return|;
block|}
try|try
init|(
name|ReviewDb
name|db
init|=
name|reviewDbProvider
operator|.
name|open
argument_list|()
init|)
block|{
name|Account
operator|.
name|Id
name|id
init|=
operator|new
name|Account
operator|.
name|Id
argument_list|(
name|db
operator|.
name|nextAccountId
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ExternalId
argument_list|>
name|extIds
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|String
name|httpPass
init|=
literal|"http-pass"
decl_stmt|;
name|extIds
operator|.
name|add
argument_list|(
name|ExternalId
operator|.
name|createUsername
argument_list|(
name|username
argument_list|,
name|id
argument_list|,
name|httpPass
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|email
operator|!=
literal|null
condition|)
block|{
name|extIds
operator|.
name|add
argument_list|(
name|ExternalId
operator|.
name|createEmail
argument_list|(
name|id
argument_list|,
name|email
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|externalIdsUpdate
operator|.
name|create
argument_list|()
operator|.
name|insert
argument_list|(
name|db
argument_list|,
name|extIds
argument_list|)
expr_stmt|;
name|Account
name|a
init|=
operator|new
name|Account
argument_list|(
name|id
argument_list|,
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
decl_stmt|;
name|a
operator|.
name|setFullName
argument_list|(
name|fullName
argument_list|)
expr_stmt|;
name|a
operator|.
name|setPreferredEmail
argument_list|(
name|email
argument_list|)
expr_stmt|;
name|db
operator|.
name|accounts
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|a
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|groups
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|n
range|:
name|groups
control|)
block|{
name|AccountGroup
operator|.
name|NameKey
name|k
init|=
operator|new
name|AccountGroup
operator|.
name|NameKey
argument_list|(
name|n
argument_list|)
decl_stmt|;
name|AccountGroup
name|g
init|=
name|groupCache
operator|.
name|get
argument_list|(
name|k
argument_list|)
decl_stmt|;
name|checkArgument
argument_list|(
name|g
operator|!=
literal|null
argument_list|,
literal|"group not found: %s"
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|AccountGroupMember
name|m
init|=
operator|new
name|AccountGroupMember
argument_list|(
operator|new
name|AccountGroupMember
operator|.
name|Key
argument_list|(
name|id
argument_list|,
name|g
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|db
operator|.
name|accountGroupMembers
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|m
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|KeyPair
name|sshKey
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|SshMode
operator|.
name|useSsh
argument_list|()
condition|)
block|{
name|sshKey
operator|=
name|genSshKey
argument_list|()
expr_stmt|;
name|authorizedKeys
operator|.
name|addKey
argument_list|(
name|id
argument_list|,
name|publicKey
argument_list|(
name|sshKey
argument_list|,
name|email
argument_list|)
argument_list|)
expr_stmt|;
name|sshKeyCache
operator|.
name|evict
argument_list|(
name|username
argument_list|)
expr_stmt|;
block|}
name|accountCache
operator|.
name|evictByUsername
argument_list|(
name|username
argument_list|)
expr_stmt|;
name|byEmailCache
operator|.
name|evict
argument_list|(
name|email
argument_list|)
expr_stmt|;
name|indexer
operator|.
name|index
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|account
operator|=
operator|new
name|TestAccount
argument_list|(
name|id
argument_list|,
name|username
argument_list|,
name|email
argument_list|,
name|fullName
argument_list|,
name|sshKey
argument_list|,
name|httpPass
argument_list|)
expr_stmt|;
name|accounts
operator|.
name|put
argument_list|(
name|username
argument_list|,
name|account
argument_list|)
expr_stmt|;
return|return
name|account
return|;
block|}
block|}
DECL|method|create (String username, String group)
specifier|public
name|TestAccount
name|create
parameter_list|(
name|String
name|username
parameter_list|,
name|String
name|group
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|create
argument_list|(
name|username
argument_list|,
literal|null
argument_list|,
name|username
argument_list|,
name|group
argument_list|)
return|;
block|}
DECL|method|create (String username)
specifier|public
name|TestAccount
name|create
parameter_list|(
name|String
name|username
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|create
argument_list|(
name|username
argument_list|,
literal|null
argument_list|,
name|username
argument_list|,
operator|(
name|String
index|[]
operator|)
literal|null
argument_list|)
return|;
block|}
DECL|method|admin ()
specifier|public
name|TestAccount
name|admin
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|create
argument_list|(
literal|"admin"
argument_list|,
literal|"admin@example.com"
argument_list|,
literal|"Administrator"
argument_list|,
literal|"Administrators"
argument_list|)
return|;
block|}
DECL|method|admin2 ()
specifier|public
name|TestAccount
name|admin2
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|create
argument_list|(
literal|"admin2"
argument_list|,
literal|"admin2@example.com"
argument_list|,
literal|"Administrator2"
argument_list|,
literal|"Administrators"
argument_list|)
return|;
block|}
DECL|method|user ()
specifier|public
name|TestAccount
name|user
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|create
argument_list|(
literal|"user"
argument_list|,
literal|"user@example.com"
argument_list|,
literal|"User"
argument_list|)
return|;
block|}
DECL|method|user2 ()
specifier|public
name|TestAccount
name|user2
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|create
argument_list|(
literal|"user2"
argument_list|,
literal|"user2@example.com"
argument_list|,
literal|"User2"
argument_list|)
return|;
block|}
DECL|method|get (String username)
specifier|public
name|TestAccount
name|get
parameter_list|(
name|String
name|username
parameter_list|)
block|{
return|return
name|checkNotNull
argument_list|(
name|accounts
operator|.
name|get
argument_list|(
name|username
argument_list|)
argument_list|,
literal|"No TestAccount created for %s"
argument_list|,
name|username
argument_list|)
return|;
block|}
DECL|method|genSshKey ()
specifier|public
specifier|static
name|KeyPair
name|genSshKey
parameter_list|()
throws|throws
name|JSchException
block|{
name|JSch
name|jsch
init|=
operator|new
name|JSch
argument_list|()
decl_stmt|;
return|return
name|KeyPair
operator|.
name|genKeyPair
argument_list|(
name|jsch
argument_list|,
name|KeyPair
operator|.
name|RSA
argument_list|)
return|;
block|}
DECL|method|publicKey (KeyPair sshKey, String comment)
specifier|public
specifier|static
name|String
name|publicKey
parameter_list|(
name|KeyPair
name|sshKey
parameter_list|,
name|String
name|comment
parameter_list|)
throws|throws
name|UnsupportedEncodingException
block|{
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|sshKey
operator|.
name|writePublicKey
argument_list|(
name|out
argument_list|,
name|comment
argument_list|)
expr_stmt|;
return|return
name|out
operator|.
name|toString
argument_list|(
name|US_ASCII
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|trim
argument_list|()
return|;
block|}
block|}
end_class

end_unit

