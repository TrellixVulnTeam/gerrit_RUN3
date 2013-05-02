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
name|extensions
operator|.
name|restapi
operator|.
name|TopLevelResource
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
name|server
operator|.
name|account
operator|.
name|CreateAccount
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
name|List
import|;
end_import

begin_comment
comment|/** Create a new user account. **/
end_comment

begin_class
annotation|@
name|RequiresCapability
argument_list|(
name|GlobalCapability
operator|.
name|CREATE_ACCOUNT
argument_list|)
annotation|@
name|CommandMetaData
argument_list|(
name|name
operator|=
literal|"create-account"
argument_list|,
name|descr
operator|=
literal|"Create a new batch/role account"
argument_list|)
DECL|class|CreateAccountCommand
specifier|final
class|class
name|CreateAccountCommand
extends|extends
name|SshCommand
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
literal|"password for HTTP authentication"
argument_list|)
DECL|field|httpPassword
specifier|private
name|String
name|httpPassword
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
DECL|field|createAccountFactory
specifier|private
name|CreateAccount
operator|.
name|Factory
name|createAccountFactory
decl_stmt|;
annotation|@
name|Override
DECL|method|run ()
specifier|protected
name|void
name|run
parameter_list|()
throws|throws
name|OrmException
throws|,
name|IOException
throws|,
name|UnloggedFailure
block|{
name|CreateAccount
operator|.
name|Input
name|input
init|=
operator|new
name|CreateAccount
operator|.
name|Input
argument_list|()
decl_stmt|;
name|input
operator|.
name|username
operator|=
name|username
expr_stmt|;
name|input
operator|.
name|email
operator|=
name|email
expr_stmt|;
name|input
operator|.
name|name
operator|=
name|fullName
expr_stmt|;
name|input
operator|.
name|sshKey
operator|=
name|readSshKey
argument_list|()
expr_stmt|;
name|input
operator|.
name|httpPassword
operator|=
name|httpPassword
expr_stmt|;
name|input
operator|.
name|groups
operator|=
name|Lists
operator|.
name|transform
argument_list|(
name|groups
argument_list|,
operator|new
name|Function
argument_list|<
name|AccountGroup
operator|.
name|Id
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
name|AccountGroup
operator|.
name|Id
name|id
parameter_list|)
block|{
return|return
name|id
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
try|try
block|{
name|createAccountFactory
operator|.
name|create
argument_list|(
name|username
argument_list|)
operator|.
name|apply
argument_list|(
name|TopLevelResource
operator|.
name|INSTANCE
argument_list|,
name|input
argument_list|)
expr_stmt|;
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
DECL|method|readSshKey ()
specifier|private
name|String
name|readSshKey
parameter_list|()
throws|throws
name|UnsupportedEncodingException
throws|,
name|IOException
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
name|sshKey
return|;
block|}
block|}
end_class

end_unit

