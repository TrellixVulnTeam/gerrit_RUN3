begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2018 The Android Open Source Project
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
DECL|package|com.google.gerrit.acceptance.testsuite.account
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|testsuite
operator|.
name|account
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
name|acceptance
operator|.
name|SshEnabled
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
name|server
operator|.
name|Sequences
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
name|ServerInitiated
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
name|account
operator|.
name|AccountsUpdate
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
name|InternalAccountUpdate
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
name|IOException
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
name|Optional
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

begin_comment
comment|/**  * The implementation of {@code AccountOperations}.  *  *<p>There is only one implementation of {@code AccountOperations}. Nevertheless, we keep the  * separation between interface and implementation to enhance clarity.  */
end_comment

begin_class
DECL|class|AccountOperationsImpl
specifier|public
class|class
name|AccountOperationsImpl
implements|implements
name|AccountOperations
block|{
DECL|field|accounts
specifier|private
specifier|final
name|Accounts
name|accounts
decl_stmt|;
DECL|field|accountsUpdate
specifier|private
specifier|final
name|AccountsUpdate
name|accountsUpdate
decl_stmt|;
DECL|field|seq
specifier|private
specifier|final
name|Sequences
name|seq
decl_stmt|;
DECL|field|sshKeyCache
specifier|private
specifier|final
name|SshKeyCache
name|sshKeyCache
decl_stmt|;
DECL|field|authorizedKeys
specifier|private
specifier|final
name|VersionedAuthorizedKeys
operator|.
name|Accessor
name|authorizedKeys
decl_stmt|;
DECL|field|sshEnabled
specifier|private
specifier|final
name|boolean
name|sshEnabled
decl_stmt|;
annotation|@
name|Inject
DECL|method|AccountOperationsImpl ( Accounts accounts, @ServerInitiated AccountsUpdate accountsUpdate, Sequences seq, SshKeyCache sshKeyCache, VersionedAuthorizedKeys.Accessor authorizedKeys, @SshEnabled boolean sshEnabled)
specifier|public
name|AccountOperationsImpl
parameter_list|(
name|Accounts
name|accounts
parameter_list|,
annotation|@
name|ServerInitiated
name|AccountsUpdate
name|accountsUpdate
parameter_list|,
name|Sequences
name|seq
parameter_list|,
name|SshKeyCache
name|sshKeyCache
parameter_list|,
name|VersionedAuthorizedKeys
operator|.
name|Accessor
name|authorizedKeys
parameter_list|,
comment|// TODO(ekempin,aliceks): Find a way not to use this config parameter here. Ideally,
comment|// completely factor out SSH from this class.
annotation|@
name|SshEnabled
name|boolean
name|sshEnabled
parameter_list|)
block|{
name|this
operator|.
name|accounts
operator|=
name|accounts
expr_stmt|;
name|this
operator|.
name|accountsUpdate
operator|=
name|accountsUpdate
expr_stmt|;
name|this
operator|.
name|seq
operator|=
name|seq
expr_stmt|;
name|this
operator|.
name|sshKeyCache
operator|=
name|sshKeyCache
expr_stmt|;
name|this
operator|.
name|authorizedKeys
operator|=
name|authorizedKeys
expr_stmt|;
name|this
operator|.
name|sshEnabled
operator|=
name|sshEnabled
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|account (Account.Id accountId)
specifier|public
name|MoreAccountOperations
name|account
parameter_list|(
name|Account
operator|.
name|Id
name|accountId
parameter_list|)
block|{
return|return
operator|new
name|MoreAccountOperationsImpl
argument_list|(
name|accountId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newAccount ()
specifier|public
name|TestAccountCreation
operator|.
name|Builder
name|newAccount
parameter_list|()
block|{
return|return
name|TestAccountCreation
operator|.
name|builder
argument_list|(
name|this
operator|::
name|createAccount
argument_list|)
return|;
block|}
DECL|method|createAccount (TestAccountCreation accountCreation)
specifier|private
name|TestAccount
name|createAccount
parameter_list|(
name|TestAccountCreation
name|accountCreation
parameter_list|)
throws|throws
name|Exception
block|{
name|AccountsUpdate
operator|.
name|AccountUpdater
name|accountUpdater
init|=
parameter_list|(
name|account
parameter_list|,
name|updateBuilder
parameter_list|)
lambda|->
name|fillBuilder
argument_list|(
name|updateBuilder
argument_list|,
name|accountCreation
argument_list|,
name|account
operator|.
name|getAccount
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|AccountState
name|createdAccount
init|=
name|createAccount
argument_list|(
name|accountUpdater
argument_list|)
decl_stmt|;
name|TestAccount
operator|.
name|Builder
name|builder
init|=
name|toTestAccount
argument_list|(
name|createdAccount
argument_list|)
decl_stmt|;
name|Optional
argument_list|<
name|String
argument_list|>
name|userName
init|=
name|createdAccount
operator|.
name|getUserName
argument_list|()
decl_stmt|;
if|if
condition|(
name|sshEnabled
operator|&&
name|userName
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|addSshKeyPair
argument_list|(
name|builder
argument_list|,
name|createdAccount
operator|.
name|getAccount
argument_list|()
argument_list|,
name|userName
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|createAccount (AccountsUpdate.AccountUpdater accountUpdater)
specifier|private
name|AccountState
name|createAccount
parameter_list|(
name|AccountsUpdate
operator|.
name|AccountUpdater
name|accountUpdater
parameter_list|)
throws|throws
name|OrmException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
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
name|seq
operator|.
name|nextAccountId
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|accountsUpdate
operator|.
name|insert
argument_list|(
literal|"Create Test Account"
argument_list|,
name|accountId
argument_list|,
name|accountUpdater
argument_list|)
return|;
block|}
DECL|method|fillBuilder ( InternalAccountUpdate.Builder builder, TestAccountCreation accountCreation, Account.Id accountId)
specifier|private
specifier|static
name|void
name|fillBuilder
parameter_list|(
name|InternalAccountUpdate
operator|.
name|Builder
name|builder
parameter_list|,
name|TestAccountCreation
name|accountCreation
parameter_list|,
name|Account
operator|.
name|Id
name|accountId
parameter_list|)
block|{
name|accountCreation
operator|.
name|fullname
argument_list|()
operator|.
name|ifPresent
argument_list|(
name|builder
operator|::
name|setFullName
argument_list|)
expr_stmt|;
name|accountCreation
operator|.
name|preferredEmail
argument_list|()
operator|.
name|ifPresent
argument_list|(
name|e
lambda|->
name|setPreferredEmail
argument_list|(
name|builder
argument_list|,
name|accountId
argument_list|,
name|e
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|httpPassword
init|=
name|accountCreation
operator|.
name|httpPassword
argument_list|()
operator|.
name|orElse
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|accountCreation
operator|.
name|username
argument_list|()
operator|.
name|ifPresent
argument_list|(
name|u
lambda|->
name|setUsername
argument_list|(
name|builder
argument_list|,
name|accountId
argument_list|,
name|u
argument_list|,
name|httpPassword
argument_list|)
argument_list|)
expr_stmt|;
name|accountCreation
operator|.
name|status
argument_list|()
operator|.
name|ifPresent
argument_list|(
name|builder
operator|::
name|setStatus
argument_list|)
expr_stmt|;
block|}
DECL|method|addSshKeyPair (TestAccount.Builder builder, Account account, String username)
specifier|private
name|void
name|addSshKeyPair
parameter_list|(
name|TestAccount
operator|.
name|Builder
name|builder
parameter_list|,
name|Account
name|account
parameter_list|,
name|String
name|username
parameter_list|)
throws|throws
name|Exception
block|{
name|KeyPair
name|sshKey
init|=
name|genSshKey
argument_list|()
decl_stmt|;
name|authorizedKeys
operator|.
name|addKey
argument_list|(
name|account
operator|.
name|getId
argument_list|()
argument_list|,
name|publicKey
argument_list|(
name|sshKey
argument_list|,
name|account
operator|.
name|getPreferredEmail
argument_list|()
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
name|builder
operator|.
name|sshKeyPair
argument_list|(
name|sshKey
argument_list|)
expr_stmt|;
block|}
DECL|method|genSshKey ()
specifier|private
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
specifier|private
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
DECL|method|toTestAccount (AccountState accountState)
specifier|private
specifier|static
name|TestAccount
operator|.
name|Builder
name|toTestAccount
parameter_list|(
name|AccountState
name|accountState
parameter_list|)
block|{
name|Account
name|createdAccount
init|=
name|accountState
operator|.
name|getAccount
argument_list|()
decl_stmt|;
return|return
name|TestAccount
operator|.
name|builder
argument_list|()
operator|.
name|accountId
argument_list|(
name|createdAccount
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|preferredEmail
argument_list|(
name|Optional
operator|.
name|ofNullable
argument_list|(
name|createdAccount
operator|.
name|getPreferredEmail
argument_list|()
argument_list|)
argument_list|)
operator|.
name|fullname
argument_list|(
name|Optional
operator|.
name|ofNullable
argument_list|(
name|createdAccount
operator|.
name|getFullName
argument_list|()
argument_list|)
argument_list|)
operator|.
name|username
argument_list|(
name|accountState
operator|.
name|getUserName
argument_list|()
argument_list|)
return|;
block|}
DECL|method|setPreferredEmail ( InternalAccountUpdate.Builder builder, Account.Id accountId, String preferredEmail)
specifier|private
specifier|static
name|InternalAccountUpdate
operator|.
name|Builder
name|setPreferredEmail
parameter_list|(
name|InternalAccountUpdate
operator|.
name|Builder
name|builder
parameter_list|,
name|Account
operator|.
name|Id
name|accountId
parameter_list|,
name|String
name|preferredEmail
parameter_list|)
block|{
return|return
name|builder
operator|.
name|setPreferredEmail
argument_list|(
name|preferredEmail
argument_list|)
operator|.
name|addExternalId
argument_list|(
name|ExternalId
operator|.
name|createEmail
argument_list|(
name|accountId
argument_list|,
name|preferredEmail
argument_list|)
argument_list|)
return|;
block|}
DECL|method|setUsername ( InternalAccountUpdate.Builder builder, Account.Id accountId, String username, String httpPassword)
specifier|private
specifier|static
name|InternalAccountUpdate
operator|.
name|Builder
name|setUsername
parameter_list|(
name|InternalAccountUpdate
operator|.
name|Builder
name|builder
parameter_list|,
name|Account
operator|.
name|Id
name|accountId
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|httpPassword
parameter_list|)
block|{
return|return
name|builder
operator|.
name|addExternalId
argument_list|(
name|ExternalId
operator|.
name|createUsername
argument_list|(
name|username
argument_list|,
name|accountId
argument_list|,
name|httpPassword
argument_list|)
argument_list|)
return|;
block|}
DECL|class|MoreAccountOperationsImpl
specifier|private
class|class
name|MoreAccountOperationsImpl
implements|implements
name|MoreAccountOperations
block|{
DECL|field|accountId
specifier|private
specifier|final
name|Account
operator|.
name|Id
name|accountId
decl_stmt|;
DECL|method|MoreAccountOperationsImpl (Account.Id accountId)
name|MoreAccountOperationsImpl
parameter_list|(
name|Account
operator|.
name|Id
name|accountId
parameter_list|)
block|{
name|this
operator|.
name|accountId
operator|=
name|accountId
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|exists ()
specifier|public
name|boolean
name|exists
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|accounts
operator|.
name|get
argument_list|(
name|accountId
argument_list|)
operator|.
name|isPresent
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|get ()
specifier|public
name|TestAccount
name|get
parameter_list|()
throws|throws
name|Exception
block|{
name|AccountState
name|account
init|=
name|accounts
operator|.
name|get
argument_list|(
name|accountId
argument_list|)
operator|.
name|orElseThrow
argument_list|(
parameter_list|()
lambda|->
operator|new
name|IllegalStateException
argument_list|(
literal|"Tried to get non-existing test account"
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|toTestAccount
argument_list|(
name|account
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|forUpdate ()
specifier|public
name|TestAccountUpdate
operator|.
name|Builder
name|forUpdate
parameter_list|()
block|{
return|return
name|TestAccountUpdate
operator|.
name|builder
argument_list|(
name|this
operator|::
name|updateAccount
argument_list|)
return|;
block|}
DECL|method|updateAccount (TestAccountUpdate accountUpdate)
specifier|private
name|TestAccount
name|updateAccount
parameter_list|(
name|TestAccountUpdate
name|accountUpdate
parameter_list|)
throws|throws
name|OrmException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|AccountsUpdate
operator|.
name|AccountUpdater
name|accountUpdater
init|=
parameter_list|(
name|account
parameter_list|,
name|updateBuilder
parameter_list|)
lambda|->
name|fillBuilder
argument_list|(
name|updateBuilder
argument_list|,
name|accountUpdate
argument_list|,
name|accountId
argument_list|)
decl_stmt|;
name|Optional
argument_list|<
name|AccountState
argument_list|>
name|updatedAccount
init|=
name|updateAccount
argument_list|(
name|accountUpdater
argument_list|)
decl_stmt|;
name|checkState
argument_list|(
name|updatedAccount
operator|.
name|isPresent
argument_list|()
argument_list|,
literal|"Tried to update non-existing test account"
argument_list|)
expr_stmt|;
return|return
name|toTestAccount
argument_list|(
name|updatedAccount
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|updateAccount (AccountsUpdate.AccountUpdater accountUpdater)
specifier|private
name|Optional
argument_list|<
name|AccountState
argument_list|>
name|updateAccount
parameter_list|(
name|AccountsUpdate
operator|.
name|AccountUpdater
name|accountUpdater
parameter_list|)
throws|throws
name|OrmException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
return|return
name|accountsUpdate
operator|.
name|update
argument_list|(
literal|"Update Test Account"
argument_list|,
name|accountId
argument_list|,
name|accountUpdater
argument_list|)
return|;
block|}
DECL|method|fillBuilder ( InternalAccountUpdate.Builder builder, TestAccountUpdate accountUpdate, Account.Id accountId)
specifier|private
name|void
name|fillBuilder
parameter_list|(
name|InternalAccountUpdate
operator|.
name|Builder
name|builder
parameter_list|,
name|TestAccountUpdate
name|accountUpdate
parameter_list|,
name|Account
operator|.
name|Id
name|accountId
parameter_list|)
block|{
name|accountUpdate
operator|.
name|fullname
argument_list|()
operator|.
name|ifPresent
argument_list|(
name|builder
operator|::
name|setFullName
argument_list|)
expr_stmt|;
name|accountUpdate
operator|.
name|preferredEmail
argument_list|()
operator|.
name|ifPresent
argument_list|(
name|e
lambda|->
name|setPreferredEmail
argument_list|(
name|builder
argument_list|,
name|accountId
argument_list|,
name|e
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|httpPassword
init|=
name|accountUpdate
operator|.
name|httpPassword
argument_list|()
operator|.
name|orElse
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|accountUpdate
operator|.
name|username
argument_list|()
operator|.
name|ifPresent
argument_list|(
name|u
lambda|->
name|setUsername
argument_list|(
name|builder
argument_list|,
name|accountId
argument_list|,
name|u
argument_list|,
name|httpPassword
argument_list|)
argument_list|)
expr_stmt|;
name|accountUpdate
operator|.
name|status
argument_list|()
operator|.
name|ifPresent
argument_list|(
name|builder
operator|::
name|setStatus
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

