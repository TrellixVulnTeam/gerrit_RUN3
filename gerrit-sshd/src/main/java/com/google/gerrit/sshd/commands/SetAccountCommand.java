begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2012 The Android Open Source Project
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
DECL|package|com.google.gerrit.sshd.commands
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|sshd
operator|.
name|commands
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
name|RawInputUtil
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
name|GlobalCapability
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
name|EmailException
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
name|annotations
operator|.
name|RequiresCapability
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
name|api
operator|.
name|accounts
operator|.
name|EmailInput
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
name|SshKeyInfo
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
name|RestApiException
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
name|account
operator|.
name|AccountResource
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
name|AddSshKey
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
name|CreateEmail
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
name|DeleteActive
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
name|DeleteEmail
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
name|DeleteSshKey
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
name|GetEmails
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
name|GetEmails
operator|.
name|EmailInfo
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
name|GetSshKeys
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
name|PutActive
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
name|PutHttpPassword
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
name|PutName
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
name|PutPreferred
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
name|sshd
operator|.
name|CommandMetaData
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
name|sshd
operator|.
name|SshCommand
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
name|org
operator|.
name|kohsuke
operator|.
name|args4j
operator|.
name|Argument
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
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|InputStreamReader
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
name|List
import|;
end_import

begin_comment
comment|/** Set a user's account settings. **/
end_comment

begin_class
annotation|@
name|CommandMetaData
argument_list|(
name|name
operator|=
literal|"set-account"
argument_list|,
name|description
operator|=
literal|"Change an account's settings"
argument_list|)
annotation|@
name|RequiresCapability
argument_list|(
name|GlobalCapability
operator|.
name|MODIFY_ACCOUNT
argument_list|)
DECL|class|SetAccountCommand
specifier|final
class|class
name|SetAccountCommand
extends|extends
name|SshCommand
block|{
annotation|@
name|Argument
argument_list|(
name|index
operator|=
literal|0
argument_list|,
name|required
operator|=
literal|true
argument_list|,
name|metaVar
operator|=
literal|"USER"
argument_list|,
name|usage
operator|=
literal|"full name, email-address, ssh username or account id"
argument_list|)
DECL|field|id
specifier|private
name|Account
operator|.
name|Id
name|id
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--full-name"
argument_list|,
name|metaVar
operator|=
literal|"NAME"
argument_list|,
name|usage
operator|=
literal|"display name of the account"
argument_list|)
DECL|field|fullName
specifier|private
name|String
name|fullName
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--active"
argument_list|,
name|usage
operator|=
literal|"set account's state to active"
argument_list|)
DECL|field|active
specifier|private
name|boolean
name|active
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--inactive"
argument_list|,
name|usage
operator|=
literal|"set account's state to inactive"
argument_list|)
DECL|field|inactive
specifier|private
name|boolean
name|inactive
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--add-email"
argument_list|,
name|metaVar
operator|=
literal|"EMAIL"
argument_list|,
name|usage
operator|=
literal|"email addresses to add to the account"
argument_list|)
DECL|field|addEmails
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|addEmails
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--delete-email"
argument_list|,
name|metaVar
operator|=
literal|"EMAIL"
argument_list|,
name|usage
operator|=
literal|"email addresses to delete from the account"
argument_list|)
DECL|field|deleteEmails
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|deleteEmails
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--preferred-email"
argument_list|,
name|metaVar
operator|=
literal|"EMAIL"
argument_list|,
name|usage
operator|=
literal|"a registered email address from the account"
argument_list|)
DECL|field|preferredEmail
specifier|private
name|String
name|preferredEmail
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--add-ssh-key"
argument_list|,
name|metaVar
operator|=
literal|"-|KEY"
argument_list|,
name|usage
operator|=
literal|"public keys to add to the account"
argument_list|)
DECL|field|addSshKeys
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|addSshKeys
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--delete-ssh-key"
argument_list|,
name|metaVar
operator|=
literal|"-|KEY"
argument_list|,
name|usage
operator|=
literal|"public keys to delete from the account"
argument_list|)
DECL|field|deleteSshKeys
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|deleteSshKeys
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--http-password"
argument_list|,
name|metaVar
operator|=
literal|"PASSWORD"
argument_list|,
name|usage
operator|=
literal|"password for HTTP authentication for the account"
argument_list|)
DECL|field|httpPassword
specifier|private
name|String
name|httpPassword
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--clear-http-password"
argument_list|,
name|usage
operator|=
literal|"clear HTTP password for the account"
argument_list|)
DECL|field|clearHttpPassword
specifier|private
name|boolean
name|clearHttpPassword
decl_stmt|;
annotation|@
name|Inject
DECL|field|genericUserFactory
specifier|private
name|IdentifiedUser
operator|.
name|GenericFactory
name|genericUserFactory
decl_stmt|;
annotation|@
name|Inject
DECL|field|createEmailFactory
specifier|private
name|CreateEmail
operator|.
name|Factory
name|createEmailFactory
decl_stmt|;
annotation|@
name|Inject
DECL|field|getEmails
specifier|private
name|GetEmails
name|getEmails
decl_stmt|;
annotation|@
name|Inject
DECL|field|deleteEmail
specifier|private
name|DeleteEmail
name|deleteEmail
decl_stmt|;
annotation|@
name|Inject
DECL|field|putPreferred
specifier|private
name|PutPreferred
name|putPreferred
decl_stmt|;
annotation|@
name|Inject
DECL|field|putName
specifier|private
name|PutName
name|putName
decl_stmt|;
annotation|@
name|Inject
DECL|field|putHttpPassword
specifier|private
name|PutHttpPassword
name|putHttpPassword
decl_stmt|;
annotation|@
name|Inject
DECL|field|putActive
specifier|private
name|PutActive
name|putActive
decl_stmt|;
annotation|@
name|Inject
DECL|field|deleteActive
specifier|private
name|DeleteActive
name|deleteActive
decl_stmt|;
annotation|@
name|Inject
DECL|field|addSshKey
specifier|private
name|AddSshKey
name|addSshKey
decl_stmt|;
annotation|@
name|Inject
DECL|field|getSshKeys
specifier|private
name|GetSshKeys
name|getSshKeys
decl_stmt|;
annotation|@
name|Inject
DECL|field|deleteSshKey
specifier|private
name|DeleteSshKey
name|deleteSshKey
decl_stmt|;
DECL|field|user
specifier|private
name|IdentifiedUser
name|user
decl_stmt|;
DECL|field|rsrc
specifier|private
name|AccountResource
name|rsrc
decl_stmt|;
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|validate
argument_list|()
expr_stmt|;
name|setAccount
argument_list|()
expr_stmt|;
block|}
DECL|method|validate ()
specifier|private
name|void
name|validate
parameter_list|()
throws|throws
name|UnloggedFailure
block|{
if|if
condition|(
name|active
operator|&&
name|inactive
condition|)
block|{
throw|throw
operator|new
name|UnloggedFailure
argument_list|(
literal|1
argument_list|,
literal|"--active and --inactive options are mutually exclusive."
argument_list|)
throw|;
block|}
if|if
condition|(
name|clearHttpPassword
operator|&&
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|httpPassword
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|UnloggedFailure
argument_list|(
literal|1
argument_list|,
literal|"--http-password and --clear-http-password options are mutually "
operator|+
literal|"exclusive."
argument_list|)
throw|;
block|}
if|if
condition|(
name|addSshKeys
operator|.
name|contains
argument_list|(
literal|"-"
argument_list|)
operator|&&
name|deleteSshKeys
operator|.
name|contains
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|UnloggedFailure
argument_list|(
literal|1
argument_list|,
literal|"Only one option may use the stdin"
argument_list|)
throw|;
block|}
if|if
condition|(
name|deleteSshKeys
operator|.
name|contains
argument_list|(
literal|"ALL"
argument_list|)
condition|)
block|{
name|deleteSshKeys
operator|=
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"ALL"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|deleteEmails
operator|.
name|contains
argument_list|(
literal|"ALL"
argument_list|)
condition|)
block|{
name|deleteEmails
operator|=
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"ALL"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|deleteEmails
operator|.
name|contains
argument_list|(
name|preferredEmail
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|UnloggedFailure
argument_list|(
literal|1
argument_list|,
literal|"--preferred-email and --delete-email options are mutually "
operator|+
literal|"exclusive for the same email address."
argument_list|)
throw|;
block|}
block|}
DECL|method|setAccount ()
specifier|private
name|void
name|setAccount
parameter_list|()
throws|throws
name|OrmException
throws|,
name|IOException
throws|,
name|UnloggedFailure
block|{
name|user
operator|=
name|genericUserFactory
operator|.
name|create
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|rsrc
operator|=
operator|new
name|AccountResource
argument_list|(
name|user
argument_list|)
expr_stmt|;
try|try
block|{
for|for
control|(
name|String
name|email
range|:
name|addEmails
control|)
block|{
name|addEmail
argument_list|(
name|email
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|email
range|:
name|deleteEmails
control|)
block|{
name|deleteEmail
argument_list|(
name|email
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|preferredEmail
operator|!=
literal|null
condition|)
block|{
name|putPreferred
argument_list|(
name|preferredEmail
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fullName
operator|!=
literal|null
condition|)
block|{
name|PutName
operator|.
name|Input
name|in
init|=
operator|new
name|PutName
operator|.
name|Input
argument_list|()
decl_stmt|;
name|in
operator|.
name|name
operator|=
name|fullName
expr_stmt|;
name|putName
operator|.
name|apply
argument_list|(
name|rsrc
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|httpPassword
operator|!=
literal|null
operator|||
name|clearHttpPassword
condition|)
block|{
name|PutHttpPassword
operator|.
name|Input
name|in
init|=
operator|new
name|PutHttpPassword
operator|.
name|Input
argument_list|()
decl_stmt|;
name|in
operator|.
name|httpPassword
operator|=
name|httpPassword
expr_stmt|;
name|putHttpPassword
operator|.
name|apply
argument_list|(
name|rsrc
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|active
condition|)
block|{
name|putActive
operator|.
name|apply
argument_list|(
name|rsrc
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|inactive
condition|)
block|{
try|try
block|{
name|deleteActive
operator|.
name|apply
argument_list|(
name|rsrc
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ResourceNotFoundException
name|e
parameter_list|)
block|{
comment|// user is already inactive
block|}
block|}
name|addSshKeys
operator|=
name|readSshKey
argument_list|(
name|addSshKeys
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|addSshKeys
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|addSshKeys
argument_list|(
name|addSshKeys
argument_list|)
expr_stmt|;
block|}
name|deleteSshKeys
operator|=
name|readSshKey
argument_list|(
name|deleteSshKeys
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|deleteSshKeys
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|deleteSshKeys
argument_list|(
name|deleteSshKeys
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|RestApiException
name|e
parameter_list|)
block|{
throw|throw
name|die
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|method|addSshKeys (List<String> sshKeys)
specifier|private
name|void
name|addSshKeys
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|sshKeys
parameter_list|)
throws|throws
name|RestApiException
throws|,
name|OrmException
throws|,
name|IOException
block|{
for|for
control|(
specifier|final
name|String
name|sshKey
range|:
name|sshKeys
control|)
block|{
name|AddSshKey
operator|.
name|Input
name|in
init|=
operator|new
name|AddSshKey
operator|.
name|Input
argument_list|()
decl_stmt|;
name|in
operator|.
name|raw
operator|=
name|RawInputUtil
operator|.
name|create
argument_list|(
name|sshKey
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|"plain/text"
argument_list|)
expr_stmt|;
name|addSshKey
operator|.
name|apply
argument_list|(
name|rsrc
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|deleteSshKeys (List<String> sshKeys)
specifier|private
name|void
name|deleteSshKeys
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|sshKeys
parameter_list|)
throws|throws
name|RestApiException
throws|,
name|OrmException
block|{
name|List
argument_list|<
name|SshKeyInfo
argument_list|>
name|infos
init|=
name|getSshKeys
operator|.
name|apply
argument_list|(
name|rsrc
argument_list|)
decl_stmt|;
if|if
condition|(
name|sshKeys
operator|.
name|contains
argument_list|(
literal|"ALL"
argument_list|)
condition|)
block|{
for|for
control|(
name|SshKeyInfo
name|i
range|:
name|infos
control|)
block|{
name|deleteSshKey
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|String
name|sshKey
range|:
name|sshKeys
control|)
block|{
for|for
control|(
name|SshKeyInfo
name|i
range|:
name|infos
control|)
block|{
if|if
condition|(
name|sshKey
operator|.
name|trim
argument_list|()
operator|.
name|equals
argument_list|(
name|i
operator|.
name|sshPublicKey
argument_list|)
operator|||
name|sshKey
operator|.
name|trim
argument_list|()
operator|.
name|equals
argument_list|(
name|i
operator|.
name|comment
argument_list|)
condition|)
block|{
name|deleteSshKey
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
DECL|method|deleteSshKey (SshKeyInfo i)
specifier|private
name|void
name|deleteSshKey
parameter_list|(
name|SshKeyInfo
name|i
parameter_list|)
throws|throws
name|AuthException
throws|,
name|OrmException
block|{
name|AccountSshKey
name|sshKey
init|=
operator|new
name|AccountSshKey
argument_list|(
operator|new
name|AccountSshKey
operator|.
name|Id
argument_list|(
name|user
operator|.
name|getAccountId
argument_list|()
argument_list|,
name|i
operator|.
name|seq
argument_list|)
argument_list|,
name|i
operator|.
name|sshPublicKey
argument_list|)
decl_stmt|;
name|deleteSshKey
operator|.
name|apply
argument_list|(
operator|new
name|AccountResource
operator|.
name|SshKey
argument_list|(
name|user
argument_list|,
name|sshKey
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|addEmail (String email)
specifier|private
name|void
name|addEmail
parameter_list|(
name|String
name|email
parameter_list|)
throws|throws
name|UnloggedFailure
throws|,
name|RestApiException
throws|,
name|OrmException
block|{
name|EmailInput
name|in
init|=
operator|new
name|EmailInput
argument_list|()
decl_stmt|;
name|in
operator|.
name|email
operator|=
name|email
expr_stmt|;
name|in
operator|.
name|noConfirmation
operator|=
literal|true
expr_stmt|;
try|try
block|{
name|createEmailFactory
operator|.
name|create
argument_list|(
name|email
argument_list|)
operator|.
name|apply
argument_list|(
name|rsrc
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EmailException
name|e
parameter_list|)
block|{
throw|throw
name|die
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|method|deleteEmail (String email)
specifier|private
name|void
name|deleteEmail
parameter_list|(
name|String
name|email
parameter_list|)
throws|throws
name|RestApiException
throws|,
name|OrmException
block|{
if|if
condition|(
name|email
operator|.
name|equals
argument_list|(
literal|"ALL"
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|EmailInfo
argument_list|>
name|emails
init|=
name|getEmails
operator|.
name|apply
argument_list|(
name|rsrc
argument_list|)
decl_stmt|;
for|for
control|(
name|EmailInfo
name|e
range|:
name|emails
control|)
block|{
name|deleteEmail
operator|.
name|apply
argument_list|(
operator|new
name|AccountResource
operator|.
name|Email
argument_list|(
name|user
argument_list|,
name|e
operator|.
name|email
argument_list|)
argument_list|,
operator|new
name|DeleteEmail
operator|.
name|Input
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|deleteEmail
operator|.
name|apply
argument_list|(
operator|new
name|AccountResource
operator|.
name|Email
argument_list|(
name|user
argument_list|,
name|email
argument_list|)
argument_list|,
operator|new
name|DeleteEmail
operator|.
name|Input
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|putPreferred (String email)
specifier|private
name|void
name|putPreferred
parameter_list|(
name|String
name|email
parameter_list|)
throws|throws
name|RestApiException
throws|,
name|OrmException
block|{
for|for
control|(
name|EmailInfo
name|e
range|:
name|getEmails
operator|.
name|apply
argument_list|(
name|rsrc
argument_list|)
control|)
block|{
if|if
condition|(
name|e
operator|.
name|email
operator|.
name|equals
argument_list|(
name|email
argument_list|)
condition|)
block|{
name|putPreferred
operator|.
name|apply
argument_list|(
operator|new
name|AccountResource
operator|.
name|Email
argument_list|(
name|user
argument_list|,
name|email
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
name|stderr
operator|.
name|println
argument_list|(
literal|"preferred email not found: "
operator|+
name|email
argument_list|)
expr_stmt|;
block|}
DECL|method|readSshKey (final List<String> sshKeys)
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|readSshKey
parameter_list|(
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|sshKeys
parameter_list|)
throws|throws
name|UnsupportedEncodingException
throws|,
name|IOException
block|{
if|if
condition|(
operator|!
name|sshKeys
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|int
name|idx
init|=
name|sshKeys
operator|.
name|indexOf
argument_list|(
literal|"-"
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|>=
literal|0
condition|)
block|{
name|StringBuilder
name|sshKey
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|BufferedReader
name|br
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|in
argument_list|,
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|sshKey
operator|.
name|append
argument_list|(
name|line
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
name|sshKeys
operator|.
name|set
argument_list|(
name|idx
argument_list|,
name|sshKey
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sshKeys
return|;
block|}
block|}
end_class

end_unit

