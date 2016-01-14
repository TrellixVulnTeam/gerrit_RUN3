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
DECL|package|com.google.gerrit.server.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|change
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
name|ChangeHooks
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
name|TimeUtil
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
name|changes
operator|.
name|AbandonInput
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
name|ChangeInfo
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
name|ResourceConflictException
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
name|RestModifyView
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
name|webui
operator|.
name|UiAction
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
name|Change
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
name|ChangeMessage
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
name|PatchSet
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
name|server
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
name|ChangeMessagesUtil
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
name|ChangeUtil
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
name|git
operator|.
name|BatchUpdate
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
name|BatchUpdate
operator|.
name|ChangeContext
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
name|BatchUpdate
operator|.
name|Context
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
name|UpdateException
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
name|AbandonedSender
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
name|ReplyToChangeSender
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
name|notedb
operator|.
name|ChangeUpdate
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
name|project
operator|.
name|ChangeControl
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
name|google
operator|.
name|inject
operator|.
name|Provider
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
name|util
operator|.
name|Collections
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|Abandon
specifier|public
class|class
name|Abandon
implements|implements
name|RestModifyView
argument_list|<
name|ChangeResource
argument_list|,
name|AbandonInput
argument_list|>
implements|,
name|UiAction
argument_list|<
name|ChangeResource
argument_list|>
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
name|Abandon
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|hooks
specifier|private
specifier|final
name|ChangeHooks
name|hooks
decl_stmt|;
DECL|field|abandonedSenderFactory
specifier|private
specifier|final
name|AbandonedSender
operator|.
name|Factory
name|abandonedSenderFactory
decl_stmt|;
DECL|field|dbProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|dbProvider
decl_stmt|;
DECL|field|json
specifier|private
specifier|final
name|ChangeJson
operator|.
name|Factory
name|json
decl_stmt|;
DECL|field|cmUtil
specifier|private
specifier|final
name|ChangeMessagesUtil
name|cmUtil
decl_stmt|;
DECL|field|batchUpdateFactory
specifier|private
specifier|final
name|BatchUpdate
operator|.
name|Factory
name|batchUpdateFactory
decl_stmt|;
annotation|@
name|Inject
DECL|method|Abandon (ChangeHooks hooks, AbandonedSender.Factory abandonedSenderFactory, Provider<ReviewDb> dbProvider, ChangeJson.Factory json, ChangeMessagesUtil cmUtil, BatchUpdate.Factory batchUpdateFactory)
name|Abandon
parameter_list|(
name|ChangeHooks
name|hooks
parameter_list|,
name|AbandonedSender
operator|.
name|Factory
name|abandonedSenderFactory
parameter_list|,
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|dbProvider
parameter_list|,
name|ChangeJson
operator|.
name|Factory
name|json
parameter_list|,
name|ChangeMessagesUtil
name|cmUtil
parameter_list|,
name|BatchUpdate
operator|.
name|Factory
name|batchUpdateFactory
parameter_list|)
block|{
name|this
operator|.
name|hooks
operator|=
name|hooks
expr_stmt|;
name|this
operator|.
name|abandonedSenderFactory
operator|=
name|abandonedSenderFactory
expr_stmt|;
name|this
operator|.
name|dbProvider
operator|=
name|dbProvider
expr_stmt|;
name|this
operator|.
name|json
operator|=
name|json
expr_stmt|;
name|this
operator|.
name|cmUtil
operator|=
name|cmUtil
expr_stmt|;
name|this
operator|.
name|batchUpdateFactory
operator|=
name|batchUpdateFactory
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (ChangeResource req, final AbandonInput input)
specifier|public
name|ChangeInfo
name|apply
parameter_list|(
name|ChangeResource
name|req
parameter_list|,
specifier|final
name|AbandonInput
name|input
parameter_list|)
throws|throws
name|RestApiException
throws|,
name|UpdateException
throws|,
name|OrmException
block|{
name|ChangeControl
name|control
init|=
name|req
operator|.
name|getControl
argument_list|()
decl_stmt|;
name|IdentifiedUser
name|caller
init|=
name|control
operator|.
name|getUser
argument_list|()
operator|.
name|asIdentifiedUser
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|control
operator|.
name|canAbandon
argument_list|(
name|dbProvider
operator|.
name|get
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AuthException
argument_list|(
literal|"abandon not permitted"
argument_list|)
throw|;
block|}
name|Change
name|change
init|=
name|abandon
argument_list|(
name|control
argument_list|,
name|input
operator|.
name|message
argument_list|,
name|caller
operator|.
name|getAccount
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|json
operator|.
name|create
argument_list|(
name|ChangeJson
operator|.
name|NO_OPTIONS
argument_list|)
operator|.
name|format
argument_list|(
name|change
argument_list|)
return|;
block|}
DECL|method|abandon (ChangeControl control, final String msgTxt, final Account account)
specifier|public
name|Change
name|abandon
parameter_list|(
name|ChangeControl
name|control
parameter_list|,
specifier|final
name|String
name|msgTxt
parameter_list|,
specifier|final
name|Account
name|account
parameter_list|)
throws|throws
name|RestApiException
throws|,
name|UpdateException
block|{
name|Op
name|op
init|=
operator|new
name|Op
argument_list|(
name|msgTxt
argument_list|,
name|account
argument_list|)
decl_stmt|;
name|Change
name|c
init|=
name|control
operator|.
name|getChange
argument_list|()
decl_stmt|;
try|try
init|(
name|BatchUpdate
name|u
init|=
name|batchUpdateFactory
operator|.
name|create
argument_list|(
name|dbProvider
operator|.
name|get
argument_list|()
argument_list|,
name|c
operator|.
name|getProject
argument_list|()
argument_list|,
name|control
operator|.
name|getUser
argument_list|()
argument_list|,
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
init|)
block|{
name|u
operator|.
name|addOp
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|,
name|op
argument_list|)
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
return|return
name|op
operator|.
name|change
return|;
block|}
DECL|class|Op
specifier|private
class|class
name|Op
extends|extends
name|BatchUpdate
operator|.
name|Op
block|{
DECL|field|account
specifier|private
specifier|final
name|Account
name|account
decl_stmt|;
DECL|field|msgTxt
specifier|private
specifier|final
name|String
name|msgTxt
decl_stmt|;
DECL|field|change
specifier|private
name|Change
name|change
decl_stmt|;
DECL|field|patchSet
specifier|private
name|PatchSet
name|patchSet
decl_stmt|;
DECL|field|message
specifier|private
name|ChangeMessage
name|message
decl_stmt|;
DECL|method|Op (String msgTxt, Account account)
specifier|private
name|Op
parameter_list|(
name|String
name|msgTxt
parameter_list|,
name|Account
name|account
parameter_list|)
block|{
name|this
operator|.
name|account
operator|=
name|account
expr_stmt|;
name|this
operator|.
name|msgTxt
operator|=
name|msgTxt
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|updateChange (ChangeContext ctx)
specifier|public
name|void
name|updateChange
parameter_list|(
name|ChangeContext
name|ctx
parameter_list|)
throws|throws
name|OrmException
throws|,
name|ResourceConflictException
block|{
name|change
operator|=
name|ctx
operator|.
name|getChange
argument_list|()
expr_stmt|;
name|PatchSet
operator|.
name|Id
name|psId
init|=
name|change
operator|.
name|currentPatchSetId
argument_list|()
decl_stmt|;
name|ChangeUpdate
name|update
init|=
name|ctx
operator|.
name|getUpdate
argument_list|(
name|psId
argument_list|)
decl_stmt|;
if|if
condition|(
name|change
operator|==
literal|null
operator|||
operator|!
name|change
operator|.
name|getStatus
argument_list|()
operator|.
name|isOpen
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
literal|"change is "
operator|+
name|status
argument_list|(
name|change
argument_list|)
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|change
operator|.
name|getStatus
argument_list|()
operator|==
name|Change
operator|.
name|Status
operator|.
name|DRAFT
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
literal|"draft changes cannot be abandoned"
argument_list|)
throw|;
block|}
name|patchSet
operator|=
name|ctx
operator|.
name|getDb
argument_list|()
operator|.
name|patchSets
argument_list|()
operator|.
name|get
argument_list|(
name|psId
argument_list|)
expr_stmt|;
name|change
operator|.
name|setStatus
argument_list|(
name|Change
operator|.
name|Status
operator|.
name|ABANDONED
argument_list|)
expr_stmt|;
name|change
operator|.
name|setLastUpdatedOn
argument_list|(
name|ctx
operator|.
name|getWhen
argument_list|()
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|getDb
argument_list|()
operator|.
name|changes
argument_list|()
operator|.
name|update
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|change
argument_list|)
argument_list|)
expr_stmt|;
name|update
operator|.
name|setStatus
argument_list|(
name|change
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|message
operator|=
name|newMessage
argument_list|(
name|ctx
operator|.
name|getDb
argument_list|()
argument_list|)
expr_stmt|;
name|cmUtil
operator|.
name|addChangeMessage
argument_list|(
name|ctx
operator|.
name|getDb
argument_list|()
argument_list|,
name|update
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
DECL|method|newMessage (ReviewDb db)
specifier|private
name|ChangeMessage
name|newMessage
parameter_list|(
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
block|{
name|StringBuilder
name|msg
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|msg
operator|.
name|append
argument_list|(
literal|"Abandoned"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Strings
operator|.
name|nullToEmpty
argument_list|(
name|msgTxt
argument_list|)
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|msg
operator|.
name|append
argument_list|(
literal|"\n\n"
argument_list|)
expr_stmt|;
name|msg
operator|.
name|append
argument_list|(
name|msgTxt
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ChangeMessage
name|message
init|=
operator|new
name|ChangeMessage
argument_list|(
operator|new
name|ChangeMessage
operator|.
name|Key
argument_list|(
name|change
operator|.
name|getId
argument_list|()
argument_list|,
name|ChangeUtil
operator|.
name|messageUUID
argument_list|(
name|db
argument_list|)
argument_list|)
argument_list|,
name|account
operator|!=
literal|null
condition|?
name|account
operator|.
name|getId
argument_list|()
else|:
literal|null
argument_list|,
name|change
operator|.
name|getLastUpdatedOn
argument_list|()
argument_list|,
name|change
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
decl_stmt|;
name|message
operator|.
name|setMessage
argument_list|(
name|msg
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|message
return|;
block|}
annotation|@
name|Override
DECL|method|postUpdate (Context ctx)
specifier|public
name|void
name|postUpdate
parameter_list|(
name|Context
name|ctx
parameter_list|)
throws|throws
name|OrmException
block|{
try|try
block|{
name|ReplyToChangeSender
name|cm
init|=
name|abandonedSenderFactory
operator|.
name|create
argument_list|(
name|change
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|account
operator|!=
literal|null
condition|)
block|{
name|cm
operator|.
name|setFrom
argument_list|(
name|account
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|cm
operator|.
name|setChangeMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|cm
operator|.
name|send
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cannot email update for change "
operator|+
name|change
operator|.
name|getId
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|hooks
operator|.
name|doChangeAbandonedHook
argument_list|(
name|change
argument_list|,
name|account
argument_list|,
name|patchSet
argument_list|,
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|msgTxt
argument_list|)
argument_list|,
name|ctx
operator|.
name|getDb
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getDescription (ChangeResource resource)
specifier|public
name|UiAction
operator|.
name|Description
name|getDescription
parameter_list|(
name|ChangeResource
name|resource
parameter_list|)
block|{
name|boolean
name|canAbandon
init|=
literal|false
decl_stmt|;
try|try
block|{
name|canAbandon
operator|=
name|resource
operator|.
name|getControl
argument_list|()
operator|.
name|canAbandon
argument_list|(
name|dbProvider
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cannot check canAbandon status. Assuming false."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|UiAction
operator|.
name|Description
argument_list|()
operator|.
name|setLabel
argument_list|(
literal|"Abandon"
argument_list|)
operator|.
name|setTitle
argument_list|(
literal|"Abandon the change"
argument_list|)
operator|.
name|setVisible
argument_list|(
name|resource
operator|.
name|getChange
argument_list|()
operator|.
name|getStatus
argument_list|()
operator|.
name|isOpen
argument_list|()
operator|&&
name|resource
operator|.
name|getChange
argument_list|()
operator|.
name|getStatus
argument_list|()
operator|!=
name|Change
operator|.
name|Status
operator|.
name|DRAFT
operator|&&
name|canAbandon
argument_list|)
return|;
block|}
DECL|method|status (Change change)
specifier|private
specifier|static
name|String
name|status
parameter_list|(
name|Change
name|change
parameter_list|)
block|{
return|return
name|change
operator|!=
literal|null
condition|?
name|change
operator|.
name|getStatus
argument_list|()
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|()
else|:
literal|"deleted"
return|;
block|}
block|}
end_class

end_unit

