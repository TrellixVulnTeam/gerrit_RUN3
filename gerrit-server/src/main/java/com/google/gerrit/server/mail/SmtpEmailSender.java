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
DECL|package|com.google.gerrit.server.mail
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|mail
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
name|Version
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
name|ConfigUtil
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
name|GerritServerConfig
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|net
operator|.
name|smtp
operator|.
name|AuthSMTPClient
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
name|net
operator|.
name|smtp
operator|.
name|SMTPClient
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
name|net
operator|.
name|smtp
operator|.
name|SMTPReply
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
name|Config
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedWriter
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
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
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
name|Date
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
name|LinkedHashMap
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/** Sends email via a nearby SMTP server. */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|SmtpEmailSender
specifier|public
class|class
name|SmtpEmailSender
implements|implements
name|EmailSender
block|{
DECL|enum|Encryption
specifier|public
specifier|static
enum|enum
name|Encryption
block|{
DECL|enumConstant|NONE
DECL|enumConstant|SSL
DECL|enumConstant|TLS
name|NONE
block|,
name|SSL
block|,
name|TLS
block|;   }
DECL|field|enabled
specifier|private
specifier|final
name|boolean
name|enabled
decl_stmt|;
DECL|field|smtpHost
specifier|private
name|String
name|smtpHost
decl_stmt|;
DECL|field|smtpPort
specifier|private
name|int
name|smtpPort
decl_stmt|;
DECL|field|smtpUser
specifier|private
name|String
name|smtpUser
decl_stmt|;
DECL|field|smtpPass
specifier|private
name|String
name|smtpPass
decl_stmt|;
DECL|field|smtpEncryption
specifier|private
name|Encryption
name|smtpEncryption
decl_stmt|;
DECL|field|sslVerify
specifier|private
name|boolean
name|sslVerify
decl_stmt|;
DECL|field|allowrcpt
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|allowrcpt
decl_stmt|;
DECL|field|importance
specifier|private
name|String
name|importance
decl_stmt|;
DECL|field|expiryDays
specifier|private
name|int
name|expiryDays
decl_stmt|;
annotation|@
name|Inject
DECL|method|SmtpEmailSender (@erritServerConfig final Config cfg)
name|SmtpEmailSender
parameter_list|(
annotation|@
name|GerritServerConfig
specifier|final
name|Config
name|cfg
parameter_list|)
block|{
name|enabled
operator|=
name|cfg
operator|.
name|getBoolean
argument_list|(
literal|"sendemail"
argument_list|,
literal|null
argument_list|,
literal|"enable"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|smtpHost
operator|=
name|cfg
operator|.
name|getString
argument_list|(
literal|"sendemail"
argument_list|,
literal|null
argument_list|,
literal|"smtpserver"
argument_list|)
expr_stmt|;
if|if
condition|(
name|smtpHost
operator|==
literal|null
condition|)
block|{
name|smtpHost
operator|=
literal|"127.0.0.1"
expr_stmt|;
block|}
name|smtpEncryption
operator|=
name|ConfigUtil
operator|.
name|getEnum
argument_list|(
name|cfg
argument_list|,
literal|"sendemail"
argument_list|,
literal|null
argument_list|,
literal|"smtpencryption"
argument_list|,
name|Encryption
operator|.
name|NONE
argument_list|)
expr_stmt|;
name|sslVerify
operator|=
name|cfg
operator|.
name|getBoolean
argument_list|(
literal|"sendemail"
argument_list|,
literal|null
argument_list|,
literal|"sslverify"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|int
name|defaultPort
decl_stmt|;
switch|switch
condition|(
name|smtpEncryption
condition|)
block|{
case|case
name|SSL
case|:
name|defaultPort
operator|=
literal|465
expr_stmt|;
break|break;
case|case
name|NONE
case|:
case|case
name|TLS
case|:
default|default:
name|defaultPort
operator|=
literal|25
expr_stmt|;
break|break;
block|}
name|smtpPort
operator|=
name|cfg
operator|.
name|getInt
argument_list|(
literal|"sendemail"
argument_list|,
literal|null
argument_list|,
literal|"smtpserverport"
argument_list|,
name|defaultPort
argument_list|)
expr_stmt|;
name|smtpUser
operator|=
name|cfg
operator|.
name|getString
argument_list|(
literal|"sendemail"
argument_list|,
literal|null
argument_list|,
literal|"smtpuser"
argument_list|)
expr_stmt|;
name|smtpPass
operator|=
name|cfg
operator|.
name|getString
argument_list|(
literal|"sendemail"
argument_list|,
literal|null
argument_list|,
literal|"smtppass"
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|rcpt
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|addr
range|:
name|cfg
operator|.
name|getStringList
argument_list|(
literal|"sendemail"
argument_list|,
literal|null
argument_list|,
literal|"allowrcpt"
argument_list|)
control|)
block|{
name|rcpt
operator|.
name|add
argument_list|(
name|addr
argument_list|)
expr_stmt|;
block|}
name|allowrcpt
operator|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|rcpt
argument_list|)
expr_stmt|;
name|importance
operator|=
name|cfg
operator|.
name|getString
argument_list|(
literal|"sendemail"
argument_list|,
literal|null
argument_list|,
literal|"importance"
argument_list|)
expr_stmt|;
name|expiryDays
operator|=
name|cfg
operator|.
name|getInt
argument_list|(
literal|"sendemail"
argument_list|,
literal|null
argument_list|,
literal|"expiryDays"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isEnabled ()
specifier|public
name|boolean
name|isEnabled
parameter_list|()
block|{
return|return
name|enabled
return|;
block|}
annotation|@
name|Override
DECL|method|canEmail (String address)
specifier|public
name|boolean
name|canEmail
parameter_list|(
name|String
name|address
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isEnabled
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|allowrcpt
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|allowrcpt
operator|.
name|contains
argument_list|(
name|address
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
name|String
name|domain
init|=
name|address
operator|.
name|substring
argument_list|(
name|address
operator|.
name|lastIndexOf
argument_list|(
literal|'@'
argument_list|)
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|allowrcpt
operator|.
name|contains
argument_list|(
name|domain
argument_list|)
operator|||
name|allowrcpt
operator|.
name|contains
argument_list|(
literal|"@"
operator|+
name|domain
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|send (final Address from, Collection<Address> rcpt, final Map<String, EmailHeader> callerHeaders, final String body)
specifier|public
name|void
name|send
parameter_list|(
specifier|final
name|Address
name|from
parameter_list|,
name|Collection
argument_list|<
name|Address
argument_list|>
name|rcpt
parameter_list|,
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|EmailHeader
argument_list|>
name|callerHeaders
parameter_list|,
specifier|final
name|String
name|body
parameter_list|)
throws|throws
name|EmailException
block|{
if|if
condition|(
operator|!
name|isEnabled
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|EmailException
argument_list|(
literal|"Sending email is disabled"
argument_list|)
throw|;
block|}
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|EmailHeader
argument_list|>
name|hdrs
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|EmailHeader
argument_list|>
argument_list|(
name|callerHeaders
argument_list|)
decl_stmt|;
name|setMissingHeader
argument_list|(
name|hdrs
argument_list|,
literal|"MIME-Version"
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
name|setMissingHeader
argument_list|(
name|hdrs
argument_list|,
literal|"Content-Type"
argument_list|,
literal|"text/plain; charset=UTF-8"
argument_list|)
expr_stmt|;
name|setMissingHeader
argument_list|(
name|hdrs
argument_list|,
literal|"Content-Transfer-Encoding"
argument_list|,
literal|"8bit"
argument_list|)
expr_stmt|;
name|setMissingHeader
argument_list|(
name|hdrs
argument_list|,
literal|"Content-Disposition"
argument_list|,
literal|"inline"
argument_list|)
expr_stmt|;
name|setMissingHeader
argument_list|(
name|hdrs
argument_list|,
literal|"User-Agent"
argument_list|,
literal|"Gerrit/"
operator|+
name|Version
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|importance
operator|!=
literal|null
condition|)
block|{
name|setMissingHeader
argument_list|(
name|hdrs
argument_list|,
literal|"Importance"
argument_list|,
name|importance
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|expiryDays
operator|>
literal|0
condition|)
block|{
name|Date
name|expiry
init|=
operator|new
name|Date
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|expiryDays
operator|*
literal|24
operator|*
literal|60
operator|*
literal|60
operator|*
literal|1000
argument_list|)
decl_stmt|;
name|setMissingHeader
argument_list|(
name|hdrs
argument_list|,
literal|"Expiry-Date"
argument_list|,
operator|new
name|SimpleDateFormat
argument_list|(
literal|"EEE, dd MMM yyyy HH:mm:ss Z"
argument_list|)
operator|.
name|format
argument_list|(
name|expiry
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
specifier|final
name|SMTPClient
name|client
init|=
name|open
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|client
operator|.
name|setSender
argument_list|(
name|from
operator|.
name|email
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|EmailException
argument_list|(
literal|"Server "
operator|+
name|smtpHost
operator|+
literal|" rejected from address "
operator|+
name|from
operator|.
name|email
argument_list|)
throw|;
block|}
for|for
control|(
name|Address
name|addr
range|:
name|rcpt
control|)
block|{
if|if
condition|(
operator|!
name|client
operator|.
name|addRecipient
argument_list|(
name|addr
operator|.
name|email
argument_list|)
condition|)
block|{
name|String
name|error
init|=
name|client
operator|.
name|getReplyString
argument_list|()
decl_stmt|;
throw|throw
operator|new
name|EmailException
argument_list|(
literal|"Server "
operator|+
name|smtpHost
operator|+
literal|" rejected recipient "
operator|+
name|addr
operator|+
literal|": "
operator|+
name|error
argument_list|)
throw|;
block|}
block|}
name|Writer
name|w
init|=
name|client
operator|.
name|sendMessageData
argument_list|()
decl_stmt|;
if|if
condition|(
name|w
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|EmailException
argument_list|(
literal|"Server "
operator|+
name|smtpHost
operator|+
literal|" rejected body"
argument_list|)
throw|;
block|}
name|w
operator|=
operator|new
name|BufferedWriter
argument_list|(
name|w
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|EmailHeader
argument_list|>
name|h
range|:
name|hdrs
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|h
operator|.
name|getValue
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|w
operator|.
name|write
argument_list|(
name|h
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
literal|": "
argument_list|)
expr_stmt|;
name|h
operator|.
name|getValue
argument_list|()
operator|.
name|write
argument_list|(
name|w
argument_list|)
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
literal|"\r\n"
argument_list|)
expr_stmt|;
block|}
block|}
name|w
operator|.
name|write
argument_list|(
literal|"\r\n"
argument_list|)
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
name|body
argument_list|)
expr_stmt|;
name|w
operator|.
name|flush
argument_list|()
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|client
operator|.
name|completePendingCommand
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|EmailException
argument_list|(
literal|"Server "
operator|+
name|smtpHost
operator|+
literal|" rejected body"
argument_list|)
throw|;
block|}
name|client
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|client
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|EmailException
argument_list|(
literal|"Cannot send outgoing email"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|setMissingHeader (final Map<String, EmailHeader> hdrs, final String name, final String value)
specifier|private
name|void
name|setMissingHeader
parameter_list|(
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|EmailHeader
argument_list|>
name|hdrs
parameter_list|,
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
operator|!
name|hdrs
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
operator|||
name|hdrs
operator|.
name|get
argument_list|(
name|name
argument_list|)
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|hdrs
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|EmailHeader
operator|.
name|String
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|open ()
specifier|private
name|SMTPClient
name|open
parameter_list|()
throws|throws
name|EmailException
block|{
specifier|final
name|AuthSMTPClient
name|client
init|=
operator|new
name|AuthSMTPClient
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
if|if
condition|(
name|smtpEncryption
operator|==
name|Encryption
operator|.
name|SSL
condition|)
block|{
name|client
operator|.
name|enableSSL
argument_list|(
name|sslVerify
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|client
operator|.
name|connect
argument_list|(
name|smtpHost
argument_list|,
name|smtpPort
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|SMTPReply
operator|.
name|isPositiveCompletion
argument_list|(
name|client
operator|.
name|getReplyCode
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|EmailException
argument_list|(
literal|"SMTP server rejected connection"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|client
operator|.
name|login
argument_list|()
condition|)
block|{
name|String
name|e
init|=
name|client
operator|.
name|getReplyString
argument_list|()
decl_stmt|;
throw|throw
operator|new
name|EmailException
argument_list|(
literal|"SMTP server rejected login: "
operator|+
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|smtpEncryption
operator|==
name|Encryption
operator|.
name|TLS
condition|)
block|{
if|if
condition|(
operator|!
name|client
operator|.
name|startTLS
argument_list|(
name|smtpHost
argument_list|,
name|smtpPort
argument_list|,
name|sslVerify
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|EmailException
argument_list|(
literal|"SMTP server does not support TLS"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|client
operator|.
name|login
argument_list|()
condition|)
block|{
name|String
name|e
init|=
name|client
operator|.
name|getReplyString
argument_list|()
decl_stmt|;
throw|throw
operator|new
name|EmailException
argument_list|(
literal|"SMTP server rejected login: "
operator|+
name|e
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|smtpUser
operator|!=
literal|null
operator|&&
operator|!
name|client
operator|.
name|auth
argument_list|(
name|smtpUser
argument_list|,
name|smtpPass
argument_list|)
condition|)
block|{
name|String
name|e
init|=
name|client
operator|.
name|getReplyString
argument_list|()
decl_stmt|;
throw|throw
operator|new
name|EmailException
argument_list|(
literal|"SMTP server rejected auth: "
operator|+
name|e
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|client
operator|.
name|isConnected
argument_list|()
condition|)
block|{
try|try
block|{
name|client
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e2
parameter_list|)
block|{         }
block|}
throw|throw
operator|new
name|EmailException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|EmailException
name|e
parameter_list|)
block|{
if|if
condition|(
name|client
operator|.
name|isConnected
argument_list|()
condition|)
block|{
try|try
block|{
name|client
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e2
parameter_list|)
block|{         }
block|}
throw|throw
name|e
throw|;
block|}
return|return
name|client
return|;
block|}
block|}
end_class

end_unit

