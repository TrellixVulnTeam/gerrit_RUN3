begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2014 The Android Open Source Project
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
name|common
operator|.
name|data
operator|.
name|GroupReference
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
name|errors
operator|.
name|NoSuchGroupException
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
name|AuthType
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
name|ConsoleUI
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
name|pgm
operator|.
name|init
operator|.
name|api
operator|.
name|InitStep
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
name|SequencesOnInit
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
name|account
operator|.
name|AccountState
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
name|externalids
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
name|group
operator|.
name|InternalGroup
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
name|AccountIndex
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
name|AccountIndexCollection
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
name|group
operator|.
name|GroupIndex
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
name|group
operator|.
name|GroupIndexCollection
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
name|Files
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
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|validator
operator|.
name|routines
operator|.
name|EmailValidator
import|;
end_import

begin_class
DECL|class|InitAdminUser
specifier|public
class|class
name|InitAdminUser
implements|implements
name|InitStep
block|{
DECL|field|flags
specifier|private
specifier|final
name|InitFlags
name|flags
decl_stmt|;
DECL|field|ui
specifier|private
specifier|final
name|ConsoleUI
name|ui
decl_stmt|;
DECL|field|allUsers
specifier|private
specifier|final
name|AllUsersNameOnInitProvider
name|allUsers
decl_stmt|;
DECL|field|accounts
specifier|private
specifier|final
name|AccountsOnInit
name|accounts
decl_stmt|;
DECL|field|authorizedKeysFactory
specifier|private
specifier|final
name|VersionedAuthorizedKeysOnInit
operator|.
name|Factory
name|authorizedKeysFactory
decl_stmt|;
DECL|field|externalIds
specifier|private
specifier|final
name|ExternalIdsOnInit
name|externalIds
decl_stmt|;
DECL|field|sequencesOnInit
specifier|private
specifier|final
name|SequencesOnInit
name|sequencesOnInit
decl_stmt|;
DECL|field|groupsOnInit
specifier|private
specifier|final
name|GroupsOnInit
name|groupsOnInit
decl_stmt|;
DECL|field|dbFactory
specifier|private
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|dbFactory
decl_stmt|;
DECL|field|accountIndexCollection
specifier|private
name|AccountIndexCollection
name|accountIndexCollection
decl_stmt|;
DECL|field|groupIndexCollection
specifier|private
name|GroupIndexCollection
name|groupIndexCollection
decl_stmt|;
annotation|@
name|Inject
DECL|method|InitAdminUser ( InitFlags flags, ConsoleUI ui, AllUsersNameOnInitProvider allUsers, AccountsOnInit accounts, VersionedAuthorizedKeysOnInit.Factory authorizedKeysFactory, ExternalIdsOnInit externalIds, SequencesOnInit sequencesOnInit, GroupsOnInit groupsOnInit)
name|InitAdminUser
parameter_list|(
name|InitFlags
name|flags
parameter_list|,
name|ConsoleUI
name|ui
parameter_list|,
name|AllUsersNameOnInitProvider
name|allUsers
parameter_list|,
name|AccountsOnInit
name|accounts
parameter_list|,
name|VersionedAuthorizedKeysOnInit
operator|.
name|Factory
name|authorizedKeysFactory
parameter_list|,
name|ExternalIdsOnInit
name|externalIds
parameter_list|,
name|SequencesOnInit
name|sequencesOnInit
parameter_list|,
name|GroupsOnInit
name|groupsOnInit
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
name|ui
operator|=
name|ui
expr_stmt|;
name|this
operator|.
name|allUsers
operator|=
name|allUsers
expr_stmt|;
name|this
operator|.
name|accounts
operator|=
name|accounts
expr_stmt|;
name|this
operator|.
name|authorizedKeysFactory
operator|=
name|authorizedKeysFactory
expr_stmt|;
name|this
operator|.
name|externalIds
operator|=
name|externalIds
expr_stmt|;
name|this
operator|.
name|sequencesOnInit
operator|=
name|sequencesOnInit
expr_stmt|;
name|this
operator|.
name|groupsOnInit
operator|=
name|groupsOnInit
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{}
annotation|@
name|Inject
argument_list|(
name|optional
operator|=
literal|true
argument_list|)
DECL|method|set (SchemaFactory<ReviewDb> dbFactory)
name|void
name|set
parameter_list|(
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|dbFactory
parameter_list|)
block|{
name|this
operator|.
name|dbFactory
operator|=
name|dbFactory
expr_stmt|;
block|}
annotation|@
name|Inject
DECL|method|set (AccountIndexCollection accountIndexCollection)
name|void
name|set
parameter_list|(
name|AccountIndexCollection
name|accountIndexCollection
parameter_list|)
block|{
name|this
operator|.
name|accountIndexCollection
operator|=
name|accountIndexCollection
expr_stmt|;
block|}
annotation|@
name|Inject
DECL|method|set (GroupIndexCollection groupIndexCollection)
name|void
name|set
parameter_list|(
name|GroupIndexCollection
name|groupIndexCollection
parameter_list|)
block|{
name|this
operator|.
name|groupIndexCollection
operator|=
name|groupIndexCollection
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|postRun ()
specifier|public
name|void
name|postRun
parameter_list|()
throws|throws
name|Exception
block|{
name|AuthType
name|authType
init|=
name|flags
operator|.
name|cfg
operator|.
name|getEnum
argument_list|(
name|AuthType
operator|.
name|values
argument_list|()
argument_list|,
literal|"auth"
argument_list|,
literal|null
argument_list|,
literal|"type"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|authType
operator|!=
name|AuthType
operator|.
name|DEVELOPMENT_BECOME_ANY_ACCOUNT
condition|)
block|{
return|return;
block|}
try|try
init|(
name|ReviewDb
name|db
init|=
name|dbFactory
operator|.
name|open
argument_list|()
init|)
block|{
if|if
condition|(
operator|!
name|accounts
operator|.
name|hasAnyAccount
argument_list|()
condition|)
block|{
name|ui
operator|.
name|header
argument_list|(
literal|"Gerrit Administrator"
argument_list|)
expr_stmt|;
if|if
condition|(
name|ui
operator|.
name|yesno
argument_list|(
literal|true
argument_list|,
literal|"Create administrator user"
argument_list|)
condition|)
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
name|sequencesOnInit
operator|.
name|nextAccountId
argument_list|(
name|db
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|username
init|=
name|ui
operator|.
name|readString
argument_list|(
literal|"admin"
argument_list|,
literal|"username"
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|ui
operator|.
name|readString
argument_list|(
literal|"Administrator"
argument_list|,
literal|"name"
argument_list|)
decl_stmt|;
name|String
name|httpPassword
init|=
name|ui
operator|.
name|readString
argument_list|(
literal|"secret"
argument_list|,
literal|"HTTP password"
argument_list|)
decl_stmt|;
name|AccountSshKey
name|sshKey
init|=
name|readSshKey
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|String
name|email
init|=
name|readEmail
argument_list|(
name|sshKey
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
name|httpPassword
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
name|externalIds
operator|.
name|insert
argument_list|(
literal|"Add external IDs for initial admin user"
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
name|name
argument_list|)
expr_stmt|;
name|a
operator|.
name|setPreferredEmail
argument_list|(
name|email
argument_list|)
expr_stmt|;
name|accounts
operator|.
name|insert
argument_list|(
name|a
argument_list|)
expr_stmt|;
comment|// Only two groups should exist at this point in time and hence iterating over all of them
comment|// is cheap.
name|Optional
argument_list|<
name|GroupReference
argument_list|>
name|adminGroupReference
init|=
name|groupsOnInit
operator|.
name|getAllGroupReferences
argument_list|(
name|db
argument_list|)
operator|.
name|filter
argument_list|(
name|group
lambda|->
name|group
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"Administrators"
argument_list|)
argument_list|)
operator|.
name|findAny
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|adminGroupReference
operator|.
name|isPresent
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchGroupException
argument_list|(
literal|"Administrators"
argument_list|)
throw|;
block|}
name|GroupReference
name|adminGroup
init|=
name|adminGroupReference
operator|.
name|get
argument_list|()
decl_stmt|;
name|groupsOnInit
operator|.
name|addGroupMember
argument_list|(
name|db
argument_list|,
name|adminGroup
operator|.
name|getUUID
argument_list|()
argument_list|,
name|a
argument_list|)
expr_stmt|;
if|if
condition|(
name|sshKey
operator|!=
literal|null
condition|)
block|{
name|VersionedAuthorizedKeysOnInit
name|authorizedKeys
init|=
name|authorizedKeysFactory
operator|.
name|create
argument_list|(
name|id
argument_list|)
operator|.
name|load
argument_list|()
decl_stmt|;
name|authorizedKeys
operator|.
name|addKey
argument_list|(
name|sshKey
operator|.
name|getSshPublicKey
argument_list|()
argument_list|)
expr_stmt|;
name|authorizedKeys
operator|.
name|save
argument_list|(
literal|"Add SSH key for initial admin user\n"
argument_list|)
expr_stmt|;
block|}
name|AccountState
name|as
init|=
name|AccountState
operator|.
name|forAccount
argument_list|(
operator|new
name|AllUsersName
argument_list|(
name|allUsers
operator|.
name|get
argument_list|()
argument_list|)
argument_list|,
name|a
argument_list|,
name|extIds
argument_list|)
decl_stmt|;
for|for
control|(
name|AccountIndex
name|accountIndex
range|:
name|accountIndexCollection
operator|.
name|getWriteIndexes
argument_list|()
control|)
block|{
name|accountIndex
operator|.
name|replace
argument_list|(
name|as
argument_list|)
expr_stmt|;
block|}
name|InternalGroup
name|adminInternalGroup
init|=
name|groupsOnInit
operator|.
name|getExistingGroup
argument_list|(
name|db
argument_list|,
name|adminGroup
argument_list|)
decl_stmt|;
for|for
control|(
name|GroupIndex
name|groupIndex
range|:
name|groupIndexCollection
operator|.
name|getWriteIndexes
argument_list|()
control|)
block|{
name|groupIndex
operator|.
name|replace
argument_list|(
name|adminInternalGroup
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
DECL|method|readEmail (AccountSshKey sshKey)
specifier|private
name|String
name|readEmail
parameter_list|(
name|AccountSshKey
name|sshKey
parameter_list|)
block|{
name|String
name|defaultEmail
init|=
literal|"admin@example.com"
decl_stmt|;
if|if
condition|(
name|sshKey
operator|!=
literal|null
operator|&&
name|sshKey
operator|.
name|getComment
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|String
name|c
init|=
name|sshKey
operator|.
name|getComment
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|EmailValidator
operator|.
name|getInstance
argument_list|()
operator|.
name|isValid
argument_list|(
name|c
argument_list|)
condition|)
block|{
name|defaultEmail
operator|=
name|c
expr_stmt|;
block|}
block|}
return|return
name|readEmail
argument_list|(
name|defaultEmail
argument_list|)
return|;
block|}
DECL|method|readEmail (String defaultEmail)
specifier|private
name|String
name|readEmail
parameter_list|(
name|String
name|defaultEmail
parameter_list|)
block|{
name|String
name|email
init|=
name|ui
operator|.
name|readString
argument_list|(
name|defaultEmail
argument_list|,
literal|"email"
argument_list|)
decl_stmt|;
if|if
condition|(
name|email
operator|!=
literal|null
operator|&&
operator|!
name|EmailValidator
operator|.
name|getInstance
argument_list|()
operator|.
name|isValid
argument_list|(
name|email
argument_list|)
condition|)
block|{
name|ui
operator|.
name|message
argument_list|(
literal|"error: invalid email address\n"
argument_list|)
expr_stmt|;
return|return
name|readEmail
argument_list|(
name|defaultEmail
argument_list|)
return|;
block|}
return|return
name|email
return|;
block|}
DECL|method|readSshKey (Account.Id id)
specifier|private
name|AccountSshKey
name|readSshKey
parameter_list|(
name|Account
operator|.
name|Id
name|id
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|defaultPublicSshKeyFile
init|=
literal|""
decl_stmt|;
name|Path
name|defaultPublicSshKeyPath
init|=
name|Paths
operator|.
name|get
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.home"
argument_list|)
argument_list|,
literal|".ssh"
argument_list|,
literal|"id_rsa.pub"
argument_list|)
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|defaultPublicSshKeyPath
argument_list|)
condition|)
block|{
name|defaultPublicSshKeyFile
operator|=
name|defaultPublicSshKeyPath
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|String
name|publicSshKeyFile
init|=
name|ui
operator|.
name|readString
argument_list|(
name|defaultPublicSshKeyFile
argument_list|,
literal|"public SSH key file"
argument_list|)
decl_stmt|;
return|return
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|publicSshKeyFile
argument_list|)
condition|?
name|createSshKey
argument_list|(
name|id
argument_list|,
name|publicSshKeyFile
argument_list|)
else|:
literal|null
return|;
block|}
DECL|method|createSshKey (Account.Id id, String keyFile)
specifier|private
name|AccountSshKey
name|createSshKey
parameter_list|(
name|Account
operator|.
name|Id
name|id
parameter_list|,
name|String
name|keyFile
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|p
init|=
name|Paths
operator|.
name|get
argument_list|(
name|keyFile
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Files
operator|.
name|exists
argument_list|(
name|p
argument_list|)
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
literal|"Cannot add public SSH key: %s is not a file"
argument_list|,
name|keyFile
argument_list|)
argument_list|)
throw|;
block|}
name|String
name|content
init|=
operator|new
name|String
argument_list|(
name|Files
operator|.
name|readAllBytes
argument_list|(
name|p
argument_list|)
argument_list|,
name|UTF_8
argument_list|)
decl_stmt|;
return|return
operator|new
name|AccountSshKey
argument_list|(
operator|new
name|AccountSshKey
operator|.
name|Id
argument_list|(
name|id
argument_list|,
literal|1
argument_list|)
argument_list|,
name|content
argument_list|)
return|;
block|}
block|}
end_class

end_unit

