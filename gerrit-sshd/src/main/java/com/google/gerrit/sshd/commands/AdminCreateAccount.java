begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2010 The Android Open Source Project
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
name|InvalidSshKeyException
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
name|AccountExternalId
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
name|AccountGroupMemberAudit
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
name|sshd
operator|.
name|AdminCommand
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
name|BaseCommand
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
name|client
operator|.
name|OrmDuplicateKeyException
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
name|client
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
name|apache
operator|.
name|sshd
operator|.
name|server
operator|.
name|Environment
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
name|HashSet
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
comment|/** Create a new user account. **/
end_comment

begin_class
annotation|@
name|AdminCommand
DECL|class|AdminCreateAccount
specifier|final
class|class
name|AdminCreateAccount
extends|extends
name|BaseCommand
block|{
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--group"
argument_list|,
name|aliases
operator|=
block|{
literal|"-g"
block|}
argument_list|,
name|metaVar
operator|=
literal|"GROUP"
argument_list|,
name|usage
operator|=
literal|"groups to add account to"
argument_list|)
DECL|field|groups
specifier|private
name|List
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
name|groups
init|=
operator|new
name|ArrayList
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
argument_list|()
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
literal|"--email"
argument_list|,
name|metaVar
operator|=
literal|"EMAIL"
argument_list|,
name|usage
operator|=
literal|"email address of the account"
argument_list|)
DECL|field|email
specifier|private
name|String
name|email
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--ssh-key"
argument_list|,
name|metaVar
operator|=
literal|"-|KEY"
argument_list|,
name|usage
operator|=
literal|"public key for SSH authentication"
argument_list|)
DECL|field|sshKey
specifier|private
name|String
name|sshKey
decl_stmt|;
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
literal|"USERNAME"
argument_list|,
name|usage
operator|=
literal|"name of the user account"
argument_list|)
DECL|field|username
specifier|private
name|String
name|username
decl_stmt|;
annotation|@
name|Inject
DECL|field|currentUser
specifier|private
name|IdentifiedUser
name|currentUser
decl_stmt|;
annotation|@
name|Inject
DECL|field|db
specifier|private
name|ReviewDb
name|db
decl_stmt|;
annotation|@
name|Inject
DECL|field|sshKeyCache
specifier|private
name|SshKeyCache
name|sshKeyCache
decl_stmt|;
annotation|@
name|Inject
DECL|field|accountCache
specifier|private
name|AccountCache
name|accountCache
decl_stmt|;
annotation|@
name|Inject
DECL|field|byEmailCache
specifier|private
name|AccountByEmailCache
name|byEmailCache
decl_stmt|;
annotation|@
name|Override
DECL|method|start (final Environment env)
specifier|public
name|void
name|start
parameter_list|(
specifier|final
name|Environment
name|env
parameter_list|)
block|{
name|startThread
argument_list|(
operator|new
name|CommandRunnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|parseCommandLine
argument_list|()
expr_stmt|;
name|createAccount
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|createAccount ()
specifier|private
name|void
name|createAccount
parameter_list|()
throws|throws
name|OrmException
throws|,
name|IOException
throws|,
name|InvalidSshKeyException
throws|,
name|UnloggedFailure
block|{
if|if
condition|(
operator|!
name|username
operator|.
name|matches
argument_list|(
name|Account
operator|.
name|USER_NAME_PATTERN
argument_list|)
condition|)
block|{
throw|throw
name|die
argument_list|(
literal|"Username '"
operator|+
name|username
operator|+
literal|"'"
operator|+
literal|" must contain only letters, numbers, _, - or ."
argument_list|)
throw|;
block|}
specifier|final
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
specifier|final
name|AccountSshKey
name|key
init|=
name|readSshKey
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|AccountExternalId
name|extUser
init|=
operator|new
name|AccountExternalId
argument_list|(
name|id
argument_list|,
operator|new
name|AccountExternalId
operator|.
name|Key
argument_list|(
name|AccountExternalId
operator|.
name|SCHEME_USERNAME
argument_list|,
name|username
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|db
operator|.
name|accountExternalIds
argument_list|()
operator|.
name|get
argument_list|(
name|extUser
operator|.
name|getKey
argument_list|()
argument_list|)
operator|!=
literal|null
condition|)
block|{
throw|throw
name|die
argument_list|(
literal|"username '"
operator|+
name|username
operator|+
literal|"' already exists"
argument_list|)
throw|;
block|}
if|if
condition|(
name|email
operator|!=
literal|null
operator|&&
name|db
operator|.
name|accountExternalIds
argument_list|()
operator|.
name|get
argument_list|(
name|getEmailKey
argument_list|()
argument_list|)
operator|!=
literal|null
condition|)
block|{
throw|throw
name|die
argument_list|(
literal|"email '"
operator|+
name|email
operator|+
literal|"' already exists"
argument_list|)
throw|;
block|}
try|try
block|{
name|db
operator|.
name|accountExternalIds
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|extUser
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmDuplicateKeyException
name|duplicateKey
parameter_list|)
block|{
throw|throw
name|die
argument_list|(
literal|"username '"
operator|+
name|username
operator|+
literal|"' already exists"
argument_list|)
throw|;
block|}
if|if
condition|(
name|email
operator|!=
literal|null
condition|)
block|{
name|AccountExternalId
name|extMailto
init|=
operator|new
name|AccountExternalId
argument_list|(
name|id
argument_list|,
name|getEmailKey
argument_list|()
argument_list|)
decl_stmt|;
name|extMailto
operator|.
name|setEmailAddress
argument_list|(
name|email
argument_list|)
expr_stmt|;
try|try
block|{
name|db
operator|.
name|accountExternalIds
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|extMailto
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmDuplicateKeyException
name|duplicateKey
parameter_list|)
block|{
try|try
block|{
name|db
operator|.
name|accountExternalIds
argument_list|()
operator|.
name|delete
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|extUser
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|cleanupError
parameter_list|)
block|{         }
throw|throw
name|die
argument_list|(
literal|"email '"
operator|+
name|email
operator|+
literal|"' already exists"
argument_list|)
throw|;
block|}
block|}
name|Account
name|a
init|=
operator|new
name|Account
argument_list|(
name|id
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
name|key
operator|!=
literal|null
condition|)
block|{
name|db
operator|.
name|accountSshKeys
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|AccountGroup
operator|.
name|Id
name|groupId
range|:
operator|new
name|HashSet
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
argument_list|(
name|groups
argument_list|)
control|)
block|{
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
name|groupId
argument_list|)
argument_list|)
decl_stmt|;
name|db
operator|.
name|accountGroupMembersAudit
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
comment|//
operator|new
name|AccountGroupMemberAudit
argument_list|(
name|m
argument_list|,
name|currentUser
operator|.
name|getAccountId
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
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
name|sshKeyCache
operator|.
name|evict
argument_list|(
name|username
argument_list|)
expr_stmt|;
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
block|}
DECL|method|getEmailKey ()
specifier|private
name|AccountExternalId
operator|.
name|Key
name|getEmailKey
parameter_list|()
block|{
return|return
operator|new
name|AccountExternalId
operator|.
name|Key
argument_list|(
name|AccountExternalId
operator|.
name|SCHEME_MAILTO
argument_list|,
name|email
argument_list|)
return|;
block|}
DECL|method|readSshKey (final Account.Id id)
specifier|private
name|AccountSshKey
name|readSshKey
parameter_list|(
specifier|final
name|Account
operator|.
name|Id
name|id
parameter_list|)
throws|throws
name|UnsupportedEncodingException
throws|,
name|IOException
throws|,
name|InvalidSshKeyException
block|{
if|if
condition|(
name|sshKey
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
literal|"-"
operator|.
name|equals
argument_list|(
name|sshKey
argument_list|)
condition|)
block|{
name|sshKey
operator|=
literal|""
expr_stmt|;
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
literal|"UTF-8"
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
operator|+=
name|line
operator|+
literal|"\n"
expr_stmt|;
block|}
block|}
return|return
name|sshKeyCache
operator|.
name|create
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
name|sshKey
operator|.
name|trim
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

