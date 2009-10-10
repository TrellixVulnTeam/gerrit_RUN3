begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2009 The Android Open Source Project
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
DECL|package|org.apache.commons.net.smtp
package|package
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|net
operator|.
name|smtp
package|;
end_package

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|Base64
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
name|security
operator|.
name|InvalidKeyException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|crypto
operator|.
name|Mac
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|crypto
operator|.
name|spec
operator|.
name|SecretKeySpec
import|;
end_import

begin_class
DECL|class|AuthSMTPClient
specifier|public
class|class
name|AuthSMTPClient
extends|extends
name|SMTPClient
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AuthSMTPClient
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|authTypes
specifier|private
name|String
name|authTypes
decl_stmt|;
DECL|field|allowedRcptTo
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|allowedRcptTo
decl_stmt|;
DECL|method|AuthSMTPClient (final String charset)
specifier|public
name|AuthSMTPClient
parameter_list|(
specifier|final
name|String
name|charset
parameter_list|)
block|{
name|super
argument_list|(
name|charset
argument_list|)
expr_stmt|;
block|}
DECL|method|setAllowRcpt (final String[] allowed)
specifier|public
name|void
name|setAllowRcpt
parameter_list|(
specifier|final
name|String
index|[]
name|allowed
parameter_list|)
block|{
if|if
condition|(
name|allowed
operator|!=
literal|null
operator|&&
name|allowed
operator|.
name|length
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|allowedRcptTo
operator|==
literal|null
condition|)
block|{
name|allowedRcptTo
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
for|for
control|(
specifier|final
name|String
name|addr
range|:
name|allowed
control|)
block|{
name|allowedRcptTo
operator|.
name|add
argument_list|(
name|addr
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|rcpt (final String forwardPath)
specifier|public
name|int
name|rcpt
parameter_list|(
specifier|final
name|String
name|forwardPath
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|allowRcpt
argument_list|(
name|forwardPath
argument_list|)
condition|)
block|{
return|return
name|super
operator|.
name|rcpt
argument_list|(
name|forwardPath
argument_list|)
return|;
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Not emailing "
operator|+
name|forwardPath
operator|+
literal|" (prohibited by allowrcpt)"
argument_list|)
expr_stmt|;
return|return
name|SMTPReply
operator|.
name|ACTION_OK
return|;
block|}
block|}
DECL|method|allowRcpt (String addr)
specifier|private
name|boolean
name|allowRcpt
parameter_list|(
name|String
name|addr
parameter_list|)
block|{
if|if
condition|(
name|allowedRcptTo
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|addr
operator|.
name|startsWith
argument_list|(
literal|"<"
argument_list|)
operator|&&
name|addr
operator|.
name|endsWith
argument_list|(
literal|">"
argument_list|)
condition|)
block|{
name|addr
operator|=
name|addr
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|addr
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|allowedRcptTo
operator|.
name|contains
argument_list|(
name|addr
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
specifier|final
name|int
name|at
init|=
name|addr
operator|.
name|indexOf
argument_list|(
literal|'@'
argument_list|)
decl_stmt|;
if|if
condition|(
name|at
operator|>
literal|0
condition|)
block|{
return|return
name|allowedRcptTo
operator|.
name|contains
argument_list|(
name|addr
operator|.
name|substring
argument_list|(
name|at
argument_list|)
argument_list|)
operator|||
name|allowedRcptTo
operator|.
name|contains
argument_list|(
name|addr
operator|.
name|substring
argument_list|(
name|at
operator|+
literal|1
argument_list|)
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|getReplyStrings ()
specifier|public
name|String
index|[]
name|getReplyStrings
parameter_list|()
block|{
return|return
name|_replyLines
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|_replyLines
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|login ()
specifier|public
name|boolean
name|login
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|name
init|=
name|getLocalAddress
argument_list|()
operator|.
name|getHostName
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|boolean
name|ok
init|=
name|SMTPReply
operator|.
name|isPositiveCompletion
argument_list|(
name|sendCommand
argument_list|(
literal|"EHLO"
argument_list|,
name|name
argument_list|)
argument_list|)
decl_stmt|;
name|authTypes
operator|=
literal|""
expr_stmt|;
for|for
control|(
name|String
name|line
range|:
name|getReplyStrings
argument_list|()
control|)
block|{
if|if
condition|(
name|line
operator|!=
literal|null
operator|&&
name|line
operator|.
name|startsWith
argument_list|(
literal|"250 AUTH "
argument_list|)
condition|)
block|{
name|authTypes
operator|=
name|line
expr_stmt|;
break|break;
block|}
block|}
return|return
name|ok
return|;
block|}
DECL|method|auth (String smtpUser, String smtpPass)
specifier|public
name|boolean
name|auth
parameter_list|(
name|String
name|smtpUser
parameter_list|,
name|String
name|smtpPass
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|types
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|authTypes
operator|.
name|split
argument_list|(
literal|" "
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
literal|""
operator|.
name|equals
argument_list|(
name|authTypes
argument_list|)
condition|)
block|{
comment|// Server didn't advertise authentication support.
comment|//
return|return
literal|true
return|;
block|}
if|if
condition|(
name|smtpPass
operator|==
literal|null
condition|)
block|{
name|smtpPass
operator|=
literal|""
expr_stmt|;
block|}
if|if
condition|(
name|types
operator|.
name|contains
argument_list|(
literal|"CRAM-SHA1"
argument_list|)
condition|)
block|{
return|return
name|authCram
argument_list|(
name|smtpUser
argument_list|,
name|smtpPass
argument_list|,
literal|"SHA1"
argument_list|)
return|;
block|}
if|if
condition|(
name|types
operator|.
name|contains
argument_list|(
literal|"CRAM-MD5"
argument_list|)
condition|)
block|{
return|return
name|authCram
argument_list|(
name|smtpUser
argument_list|,
name|smtpPass
argument_list|,
literal|"MD5"
argument_list|)
return|;
block|}
if|if
condition|(
name|types
operator|.
name|contains
argument_list|(
literal|"PLAIN"
argument_list|)
condition|)
block|{
return|return
name|authPlain
argument_list|(
name|smtpUser
argument_list|,
name|smtpPass
argument_list|)
return|;
block|}
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unsupported AUTH: "
operator|+
name|authTypes
argument_list|)
throw|;
block|}
DECL|method|authCram (String smtpUser, String smtpPass, String alg)
specifier|private
name|boolean
name|authCram
parameter_list|(
name|String
name|smtpUser
parameter_list|,
name|String
name|smtpPass
parameter_list|,
name|String
name|alg
parameter_list|)
throws|throws
name|UnsupportedEncodingException
throws|,
name|IOException
block|{
specifier|final
name|String
name|macName
init|=
literal|"Hmac"
operator|+
name|alg
decl_stmt|;
if|if
condition|(
name|sendCommand
argument_list|(
literal|"AUTH"
argument_list|,
literal|"CRAM-"
operator|+
name|alg
argument_list|)
operator|!=
literal|334
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|byte
index|[]
name|nonce
init|=
name|Base64
operator|.
name|decode
argument_list|(
name|getReplyStrings
argument_list|()
index|[
literal|0
index|]
operator|.
name|split
argument_list|(
literal|" "
argument_list|,
literal|2
argument_list|)
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
specifier|final
name|String
name|sec
decl_stmt|;
try|try
block|{
name|Mac
name|mac
init|=
name|Mac
operator|.
name|getInstance
argument_list|(
name|macName
argument_list|)
decl_stmt|;
name|mac
operator|.
name|init
argument_list|(
operator|new
name|SecretKeySpec
argument_list|(
name|smtpPass
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|,
name|macName
argument_list|)
argument_list|)
expr_stmt|;
name|sec
operator|=
name|toHex
argument_list|(
name|mac
operator|.
name|doFinal
argument_list|(
name|nonce
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot use CRAM-"
operator|+
name|alg
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InvalidKeyException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot use CRAM-"
operator|+
name|alg
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|String
name|token
init|=
name|smtpUser
operator|+
literal|' '
operator|+
name|sec
decl_stmt|;
name|String
name|cmd
init|=
name|Base64
operator|.
name|encodeBytes
argument_list|(
name|token
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|SMTPReply
operator|.
name|isPositiveCompletion
argument_list|(
name|sendCommand
argument_list|(
name|cmd
argument_list|)
argument_list|)
return|;
block|}
DECL|field|hexchar
specifier|private
specifier|static
specifier|final
name|char
index|[]
name|hexchar
init|=
block|{
literal|'0'
block|,
literal|'1'
block|,
literal|'2'
block|,
literal|'3'
block|,
literal|'4'
block|,
literal|'5'
block|,
literal|'6'
block|,
literal|'7'
block|,
literal|'8'
block|,
literal|'9'
block|,
literal|'a'
block|,
literal|'b'
block|,
literal|'c'
block|,
literal|'d'
block|,
literal|'e'
block|,
literal|'f'
block|}
decl_stmt|;
DECL|method|toHex (final byte[] b)
specifier|private
name|String
name|toHex
parameter_list|(
specifier|final
name|byte
index|[]
name|b
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|sec
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|b
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|u
init|=
operator|(
name|b
index|[
name|i
index|]
operator|>>
literal|4
operator|)
operator|&
literal|0xf
decl_stmt|;
specifier|final
name|int
name|l
init|=
name|b
index|[
name|i
index|]
operator|&
literal|0xf
decl_stmt|;
name|sec
operator|.
name|append
argument_list|(
name|hexchar
index|[
name|u
index|]
argument_list|)
expr_stmt|;
name|sec
operator|.
name|append
argument_list|(
name|hexchar
index|[
name|l
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|sec
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|authPlain (String smtpUser, String smtpPass)
specifier|private
name|boolean
name|authPlain
parameter_list|(
name|String
name|smtpUser
parameter_list|,
name|String
name|smtpPass
parameter_list|)
throws|throws
name|UnsupportedEncodingException
throws|,
name|IOException
block|{
name|String
name|token
init|=
literal|'\0'
operator|+
name|smtpUser
operator|+
literal|'\0'
operator|+
name|smtpPass
decl_stmt|;
name|String
name|cmd
init|=
literal|"PLAIN "
operator|+
name|Base64
operator|.
name|encodeBytes
argument_list|(
name|token
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|SMTPReply
operator|.
name|isPositiveCompletion
argument_list|(
name|sendCommand
argument_list|(
literal|"AUTH"
argument_list|,
name|cmd
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

