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
DECL|package|com.google.gerrit.server.mail.receive
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
operator|.
name|receive
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
name|primitives
operator|.
name|Ints
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
name|WorkQueue
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
name|mail
operator|.
name|EmailSettings
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
name|mail
operator|.
name|Encryption
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
name|pop3
operator|.
name|POP3Client
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
name|pop3
operator|.
name|POP3MessageInfo
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
name|pop3
operator|.
name|POP3SClient
import|;
end_import

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

begin_class
annotation|@
name|Singleton
DECL|class|Pop3MailReceiver
specifier|public
class|class
name|Pop3MailReceiver
extends|extends
name|MailReceiver
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
name|Pop3MailReceiver
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Inject
DECL|method|Pop3MailReceiver (EmailSettings mailSettings, MailProcessor mailProcessor, WorkQueue workQueue)
name|Pop3MailReceiver
parameter_list|(
name|EmailSettings
name|mailSettings
parameter_list|,
name|MailProcessor
name|mailProcessor
parameter_list|,
name|WorkQueue
name|workQueue
parameter_list|)
block|{
name|super
argument_list|(
name|mailSettings
argument_list|,
name|mailProcessor
argument_list|,
name|workQueue
argument_list|)
expr_stmt|;
block|}
comment|/**    * handleEmails will open a connection to the mail server, remove emails    * where deletion is pending, read new email and close the connection.    * @param async Determines if processing messages should happen asynchronous.    */
annotation|@
name|Override
DECL|method|handleEmails (boolean async)
specifier|public
specifier|synchronized
name|void
name|handleEmails
parameter_list|(
name|boolean
name|async
parameter_list|)
block|{
name|POP3Client
name|pop3
decl_stmt|;
if|if
condition|(
name|mailSettings
operator|.
name|encryption
operator|!=
name|Encryption
operator|.
name|NONE
condition|)
block|{
name|pop3
operator|=
operator|new
name|POP3SClient
argument_list|(
name|mailSettings
operator|.
name|encryption
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|pop3
operator|=
operator|new
name|POP3Client
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|mailSettings
operator|.
name|port
operator|>
literal|0
condition|)
block|{
name|pop3
operator|.
name|setDefaultPort
argument_list|(
name|mailSettings
operator|.
name|port
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|pop3
operator|.
name|connect
argument_list|(
name|mailSettings
operator|.
name|host
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Could not connect to POP3 email server"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
try|try
block|{
try|try
block|{
if|if
condition|(
operator|!
name|pop3
operator|.
name|login
argument_list|(
name|mailSettings
operator|.
name|username
argument_list|,
name|mailSettings
operator|.
name|password
argument_list|)
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Could not login to POP3 email server."
operator|+
literal|" Check username and password"
argument_list|)
expr_stmt|;
return|return;
block|}
try|try
block|{
name|POP3MessageInfo
index|[]
name|messages
init|=
name|pop3
operator|.
name|listMessages
argument_list|()
decl_stmt|;
if|if
condition|(
name|messages
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Could not retrieve message list via POP3"
argument_list|)
expr_stmt|;
return|return;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Received "
operator|+
name|messages
operator|.
name|length
operator|+
literal|" messages via POP3"
argument_list|)
expr_stmt|;
comment|// Fetch messages
name|List
argument_list|<
name|MailMessage
argument_list|>
name|mailMessages
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|POP3MessageInfo
name|msginfo
range|:
name|messages
control|)
block|{
if|if
condition|(
name|msginfo
operator|==
literal|null
condition|)
block|{
comment|// Message was deleted
continue|continue;
block|}
try|try
init|(
name|BufferedReader
name|reader
init|=
operator|(
name|BufferedReader
operator|)
name|pop3
operator|.
name|retrieveMessage
argument_list|(
name|msginfo
operator|.
name|number
argument_list|)
init|)
block|{
if|if
condition|(
name|reader
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Could not retrieve POP3 message header for message {}"
argument_list|,
name|msginfo
operator|.
name|identifier
argument_list|)
expr_stmt|;
return|return;
block|}
name|int
index|[]
name|message
init|=
name|fetchMessage
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|MailMessage
name|mailMessage
init|=
name|RawMailParser
operator|.
name|parse
argument_list|(
name|message
argument_list|)
decl_stmt|;
comment|// Delete messages where deletion is pending. This requires
comment|// knowing the integer message ID of the email. We therefore parse
comment|// the message first and extract the Message-ID specified in RFC
comment|// 822 and delete the message if deletion is pending.
if|if
condition|(
name|pendingDeletion
operator|.
name|contains
argument_list|(
name|mailMessage
operator|.
name|id
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|pop3
operator|.
name|deleteMessage
argument_list|(
name|msginfo
operator|.
name|number
argument_list|)
condition|)
block|{
name|pendingDeletion
operator|.
name|remove
argument_list|(
name|mailMessage
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Could not delete message "
operator|+
name|msginfo
operator|.
name|number
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// Process message further
name|mailMessages
operator|.
name|add
argument_list|(
name|mailMessage
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|MailParsingException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Could not parse message "
operator|+
name|msginfo
operator|.
name|number
argument_list|)
expr_stmt|;
block|}
block|}
name|dispatchMailProcessor
argument_list|(
name|mailMessages
argument_list|,
name|async
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|pop3
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|pop3
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
name|log
operator|.
name|error
argument_list|(
literal|"Error while issuing POP3 command"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|fetchMessage (BufferedReader reader)
specifier|public
specifier|final
name|int
index|[]
name|fetchMessage
parameter_list|(
name|BufferedReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|Integer
argument_list|>
name|character
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|ch
decl_stmt|;
while|while
condition|(
operator|(
name|ch
operator|=
name|reader
operator|.
name|read
argument_list|()
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|character
operator|.
name|add
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
return|return
name|Ints
operator|.
name|toArray
argument_list|(
name|character
argument_list|)
return|;
block|}
block|}
end_class

end_unit

