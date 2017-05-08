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
name|imap
operator|.
name|IMAPClient
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
name|imap
operator|.
name|IMAPSClient
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

begin_class
annotation|@
name|Singleton
DECL|class|ImapMailReceiver
specifier|public
class|class
name|ImapMailReceiver
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
name|ImapMailReceiver
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|INBOX_FOLDER
specifier|private
specifier|static
specifier|final
name|String
name|INBOX_FOLDER
init|=
literal|"INBOX"
decl_stmt|;
annotation|@
name|Inject
DECL|method|ImapMailReceiver (EmailSettings mailSettings, MailProcessor mailProcessor, WorkQueue workQueue)
name|ImapMailReceiver
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
comment|/**    * handleEmails will open a connection to the mail server, remove emails where deletion is    * pending, read new email and close the connection.    *    * @param async Determines if processing messages should happen asynchronous.    */
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
name|IMAPClient
name|imap
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
name|imap
operator|=
operator|new
name|IMAPSClient
argument_list|(
name|mailSettings
operator|.
name|encryption
operator|.
name|name
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|imap
operator|=
operator|new
name|IMAPClient
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
name|imap
operator|.
name|setDefaultPort
argument_list|(
name|mailSettings
operator|.
name|port
argument_list|)
expr_stmt|;
block|}
comment|// Set a 30s timeout for each operation
name|imap
operator|.
name|setDefaultTimeout
argument_list|(
literal|30
operator|*
literal|1000
argument_list|)
expr_stmt|;
try|try
block|{
name|imap
operator|.
name|connect
argument_list|(
name|mailSettings
operator|.
name|host
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|imap
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
literal|"Could not login to IMAP server"
argument_list|)
expr_stmt|;
return|return;
block|}
try|try
block|{
if|if
condition|(
operator|!
name|imap
operator|.
name|select
argument_list|(
name|INBOX_FOLDER
argument_list|)
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Could not select IMAP folder "
operator|+
name|INBOX_FOLDER
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Fetch just the internal dates first to know how many messages we
comment|// should fetch.
if|if
condition|(
operator|!
name|imap
operator|.
name|fetch
argument_list|(
literal|"1:*"
argument_list|,
literal|"(INTERNALDATE)"
argument_list|)
condition|)
block|{
comment|// false indicates that there are no messages to fetch
name|log
operator|.
name|info
argument_list|(
literal|"Fetched 0 messages via IMAP"
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Format of reply is one line per email and one line to indicate
comment|// that the fetch was successful.
comment|// Example:
comment|// * 1 FETCH (INTERNALDATE "Mon, 24 Oct 2016 16:53:22 +0200 (CEST)")
comment|// * 2 FETCH (INTERNALDATE "Mon, 24 Oct 2016 16:53:22 +0200 (CEST)")
comment|// AAAC OK FETCH completed.
name|int
name|numMessages
init|=
name|imap
operator|.
name|getReplyStrings
argument_list|()
operator|.
name|length
operator|-
literal|1
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Fetched "
operator|+
name|numMessages
operator|+
literal|" messages via IMAP"
argument_list|)
expr_stmt|;
if|if
condition|(
name|numMessages
operator|==
literal|0
condition|)
block|{
return|return;
block|}
comment|// Fetch the full version of all emails
name|List
argument_list|<
name|MailMessage
argument_list|>
name|mailMessages
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numMessages
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|numMessages
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|imap
operator|.
name|fetch
argument_list|(
name|i
operator|+
literal|":"
operator|+
name|i
argument_list|,
literal|"(BODY.PEEK[])"
argument_list|)
condition|)
block|{
comment|// Obtain full reply
name|String
index|[]
name|rawMessage
init|=
name|imap
operator|.
name|getReplyStrings
argument_list|()
decl_stmt|;
if|if
condition|(
name|rawMessage
operator|.
name|length
operator|<
literal|2
condition|)
block|{
continue|continue;
block|}
comment|// First and last line are IMAP status codes. We have already
comment|// checked, that the fetch returned true (OK), so we safely ignore
comment|// those two lines.
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|(
literal|2
operator|*
operator|(
name|rawMessage
operator|.
name|length
operator|-
literal|2
operator|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|rawMessage
operator|.
name|length
operator|-
literal|1
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|j
operator|>
literal|1
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
name|rawMessage
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|MailMessage
name|mailMessage
init|=
name|RawMailParser
operator|.
name|parse
argument_list|(
name|b
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
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
comment|// Mark message as deleted
if|if
condition|(
name|imap
operator|.
name|store
argument_list|(
name|i
operator|+
literal|":"
operator|+
name|i
argument_list|,
literal|"+FLAGS"
argument_list|,
literal|"(\\Deleted)"
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
literal|"Could not mark mail message as deleted: "
operator|+
name|mailMessage
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
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
literal|"Exception while parsing email after IMAP fetch"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|log
operator|.
name|error
argument_list|(
literal|"IMAP fetch failed. Will retry in next fetch cycle."
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Permanently delete emails marked for deletion
if|if
condition|(
operator|!
name|imap
operator|.
name|expunge
argument_list|()
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Could not expunge IMAP emails"
argument_list|)
expr_stmt|;
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
name|imap
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|imap
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
literal|"Error while talking to IMAP server"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
block|}
end_class

end_unit

