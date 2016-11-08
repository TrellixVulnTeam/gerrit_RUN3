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
name|annotations
operator|.
name|VisibleForTesting
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
name|events
operator|.
name|LifecycleListener
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
name|lifecycle
operator|.
name|LifecycleModule
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
name|inject
operator|.
name|Inject
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Timer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimerTask
import|;
end_import

begin_comment
comment|/** MailReceiver implements base functionality for receiving emails. */
end_comment

begin_class
DECL|class|MailReceiver
specifier|public
specifier|abstract
class|class
name|MailReceiver
implements|implements
name|LifecycleListener
block|{
DECL|field|mailSettings
specifier|protected
name|EmailSettings
name|mailSettings
decl_stmt|;
DECL|field|pendingDeletion
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|pendingDeletion
decl_stmt|;
DECL|field|timer
specifier|private
name|Timer
name|timer
decl_stmt|;
DECL|class|Module
specifier|public
specifier|static
class|class
name|Module
extends|extends
name|LifecycleModule
block|{
DECL|field|mailSettings
specifier|private
specifier|final
name|EmailSettings
name|mailSettings
decl_stmt|;
annotation|@
name|Inject
DECL|method|Module (EmailSettings mailSettings)
name|Module
parameter_list|(
name|EmailSettings
name|mailSettings
parameter_list|)
block|{
name|this
operator|.
name|mailSettings
operator|=
name|mailSettings
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|configure ()
specifier|protected
name|void
name|configure
parameter_list|()
block|{
if|if
condition|(
name|mailSettings
operator|.
name|protocol
operator|==
name|Protocol
operator|.
name|NONE
condition|)
block|{
return|return;
block|}
name|listener
argument_list|()
operator|.
name|to
argument_list|(
name|MailReceiver
operator|.
name|class
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|mailSettings
operator|.
name|protocol
condition|)
block|{
case|case
name|IMAP
case|:
name|bind
argument_list|(
name|MailReceiver
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|ImapMailReceiver
operator|.
name|class
argument_list|)
expr_stmt|;
break|break;
case|case
name|POP3
case|:
name|bind
argument_list|(
name|MailReceiver
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|Pop3MailReceiver
operator|.
name|class
argument_list|)
expr_stmt|;
break|break;
case|case
name|NONE
case|:
default|default:
block|}
block|}
block|}
DECL|method|MailReceiver (EmailSettings mailSettings)
specifier|public
name|MailReceiver
parameter_list|(
name|EmailSettings
name|mailSettings
parameter_list|)
block|{
name|this
operator|.
name|mailSettings
operator|=
name|mailSettings
expr_stmt|;
name|pendingDeletion
operator|=
name|Collections
operator|.
name|synchronizedSet
argument_list|(
operator|new
name|HashSet
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{
if|if
condition|(
name|timer
operator|==
literal|null
condition|)
block|{
name|timer
operator|=
operator|new
name|Timer
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|timer
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
name|timer
operator|.
name|scheduleAtFixedRate
argument_list|(
operator|new
name|TimerTask
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|MailReceiver
operator|.
name|this
operator|.
name|handleEmails
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|,
literal|0L
argument_list|,
name|mailSettings
operator|.
name|fetchInterval
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
if|if
condition|(
name|timer
operator|!=
literal|null
condition|)
block|{
name|timer
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * requestDeletion will enqueue an email for deletion and delete it the    * next time we connect to the email server. This does not guarantee deletion    * as the Gerrit instance might fail before we connect to the email server.    * @param messageId    */
DECL|method|requestDeletion (String messageId)
specifier|public
name|void
name|requestDeletion
parameter_list|(
name|String
name|messageId
parameter_list|)
block|{
name|pendingDeletion
operator|.
name|add
argument_list|(
name|messageId
argument_list|)
expr_stmt|;
block|}
comment|/**    * handleEmails will open a connection to the mail server, remove emails    * where deletion is pending, read new email and close the connection.    */
annotation|@
name|VisibleForTesting
DECL|method|handleEmails ()
specifier|public
specifier|abstract
name|void
name|handleEmails
parameter_list|()
function_decl|;
block|}
end_class

end_unit

