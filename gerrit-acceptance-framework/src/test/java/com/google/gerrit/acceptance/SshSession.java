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
name|checkState
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
name|truth
operator|.
name|Truth
operator|.
name|assertThat
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
name|truth
operator|.
name|Truth
operator|.
name|assert_
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
name|jcraft
operator|.
name|jsch
operator|.
name|ChannelExec
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
name|Session
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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Scanner
import|;
end_import

begin_class
DECL|class|SshSession
specifier|public
class|class
name|SshSession
block|{
DECL|field|addr
specifier|private
specifier|final
name|InetSocketAddress
name|addr
decl_stmt|;
DECL|field|account
specifier|private
specifier|final
name|TestAccount
name|account
decl_stmt|;
DECL|field|session
specifier|private
name|Session
name|session
decl_stmt|;
DECL|field|error
specifier|private
name|String
name|error
decl_stmt|;
DECL|method|SshSession (GerritServer server, TestAccount account)
specifier|public
name|SshSession
parameter_list|(
name|GerritServer
name|server
parameter_list|,
name|TestAccount
name|account
parameter_list|)
block|{
name|this
operator|.
name|addr
operator|=
name|server
operator|.
name|getSshdAddress
argument_list|()
expr_stmt|;
name|this
operator|.
name|account
operator|=
name|account
expr_stmt|;
block|}
DECL|method|open ()
specifier|public
name|void
name|open
parameter_list|()
throws|throws
name|JSchException
block|{
name|getSession
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"resource"
argument_list|)
DECL|method|exec (String command, InputStream opt)
specifier|public
name|String
name|exec
parameter_list|(
name|String
name|command
parameter_list|,
name|InputStream
name|opt
parameter_list|)
throws|throws
name|JSchException
throws|,
name|IOException
block|{
name|ChannelExec
name|channel
init|=
operator|(
name|ChannelExec
operator|)
name|getSession
argument_list|()
operator|.
name|openChannel
argument_list|(
literal|"exec"
argument_list|)
decl_stmt|;
try|try
block|{
name|channel
operator|.
name|setCommand
argument_list|(
name|command
argument_list|)
expr_stmt|;
name|channel
operator|.
name|setInputStream
argument_list|(
name|opt
argument_list|)
expr_stmt|;
name|InputStream
name|in
init|=
name|channel
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|InputStream
name|err
init|=
name|channel
operator|.
name|getErrStream
argument_list|()
decl_stmt|;
name|channel
operator|.
name|connect
argument_list|()
expr_stmt|;
name|Scanner
name|s
init|=
operator|new
name|Scanner
argument_list|(
name|err
argument_list|,
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|useDelimiter
argument_list|(
literal|"\\A"
argument_list|)
decl_stmt|;
name|error
operator|=
name|s
operator|.
name|hasNext
argument_list|()
condition|?
name|s
operator|.
name|next
argument_list|()
else|:
literal|null
expr_stmt|;
name|s
operator|=
operator|new
name|Scanner
argument_list|(
name|in
argument_list|,
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|useDelimiter
argument_list|(
literal|"\\A"
argument_list|)
expr_stmt|;
return|return
name|s
operator|.
name|hasNext
argument_list|()
condition|?
name|s
operator|.
name|next
argument_list|()
else|:
literal|""
return|;
block|}
finally|finally
block|{
name|channel
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|exec2 (String command, InputStream opt)
specifier|public
name|InputStream
name|exec2
parameter_list|(
name|String
name|command
parameter_list|,
name|InputStream
name|opt
parameter_list|)
throws|throws
name|JSchException
throws|,
name|IOException
block|{
name|ChannelExec
name|channel
init|=
operator|(
name|ChannelExec
operator|)
name|getSession
argument_list|()
operator|.
name|openChannel
argument_list|(
literal|"exec"
argument_list|)
decl_stmt|;
name|channel
operator|.
name|setCommand
argument_list|(
name|command
argument_list|)
expr_stmt|;
name|channel
operator|.
name|setInputStream
argument_list|(
name|opt
argument_list|)
expr_stmt|;
name|InputStream
name|in
init|=
name|channel
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|channel
operator|.
name|connect
argument_list|()
expr_stmt|;
return|return
name|in
return|;
block|}
DECL|method|exec (String command)
specifier|public
name|String
name|exec
parameter_list|(
name|String
name|command
parameter_list|)
throws|throws
name|JSchException
throws|,
name|IOException
block|{
return|return
name|exec
argument_list|(
name|command
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|hasError ()
specifier|private
name|boolean
name|hasError
parameter_list|()
block|{
return|return
name|error
operator|!=
literal|null
return|;
block|}
DECL|method|getError ()
specifier|public
name|String
name|getError
parameter_list|()
block|{
return|return
name|error
return|;
block|}
DECL|method|assertSuccess ()
specifier|public
name|void
name|assertSuccess
parameter_list|()
block|{
name|assert_
argument_list|()
operator|.
name|withFailureMessage
argument_list|(
name|getError
argument_list|()
argument_list|)
operator|.
name|that
argument_list|(
name|hasError
argument_list|()
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
block|}
DECL|method|assertFailure ()
specifier|public
name|void
name|assertFailure
parameter_list|()
block|{
name|assertThat
argument_list|(
name|hasError
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
block|}
DECL|method|assertFailure (String error)
specifier|public
name|void
name|assertFailure
parameter_list|(
name|String
name|error
parameter_list|)
block|{
name|assertThat
argument_list|(
name|hasError
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|getError
argument_list|()
argument_list|)
operator|.
name|contains
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|session
operator|!=
literal|null
condition|)
block|{
name|session
operator|.
name|disconnect
argument_list|()
expr_stmt|;
name|session
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|getSession ()
specifier|private
name|Session
name|getSession
parameter_list|()
throws|throws
name|JSchException
block|{
if|if
condition|(
name|session
operator|==
literal|null
condition|)
block|{
name|JSch
name|jsch
init|=
operator|new
name|JSch
argument_list|()
decl_stmt|;
name|jsch
operator|.
name|addIdentity
argument_list|(
literal|"KeyPair"
argument_list|,
name|account
operator|.
name|privateKey
argument_list|()
argument_list|,
name|account
operator|.
name|sshKey
operator|.
name|getPublicKeyBlob
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|session
operator|=
name|jsch
operator|.
name|getSession
argument_list|(
name|account
operator|.
name|username
argument_list|,
name|addr
operator|.
name|getAddress
argument_list|()
operator|.
name|getHostAddress
argument_list|()
argument_list|,
name|addr
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|.
name|setConfig
argument_list|(
literal|"StrictHostKeyChecking"
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|session
operator|.
name|connect
argument_list|()
expr_stmt|;
block|}
return|return
name|session
return|;
block|}
DECL|method|getUrl ()
specifier|public
name|String
name|getUrl
parameter_list|()
block|{
name|checkState
argument_list|(
name|session
operator|!=
literal|null
argument_list|,
literal|"session must be opened"
argument_list|)
expr_stmt|;
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"ssh://"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|session
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"@"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|session
operator|.
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|session
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getAccount ()
specifier|public
name|TestAccount
name|getAccount
parameter_list|()
block|{
return|return
name|account
return|;
block|}
block|}
end_class

end_unit

