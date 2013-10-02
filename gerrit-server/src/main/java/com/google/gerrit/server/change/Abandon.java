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
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CheckedFuture
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
name|BadRequestException
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
name|DefaultInput
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
name|ApprovalsUtil
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
name|change
operator|.
name|Abandon
operator|.
name|Input
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
name|change
operator|.
name|ChangeJson
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
name|server
operator|.
name|index
operator|.
name|ChangeIndexer
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
name|AtomicUpdate
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
name|IOException
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
DECL|class|Abandon
specifier|public
class|class
name|Abandon
implements|implements
name|RestModifyView
argument_list|<
name|ChangeResource
argument_list|,
name|Input
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
name|json
decl_stmt|;
DECL|field|indexer
specifier|private
specifier|final
name|ChangeIndexer
name|indexer
decl_stmt|;
DECL|class|Input
specifier|public
specifier|static
class|class
name|Input
block|{
annotation|@
name|DefaultInput
DECL|field|message
specifier|public
name|String
name|message
decl_stmt|;
block|}
annotation|@
name|Inject
DECL|method|Abandon (ChangeHooks hooks, AbandonedSender.Factory abandonedSenderFactory, Provider<ReviewDb> dbProvider, ChangeJson json, ChangeIndexer indexer)
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
name|json
parameter_list|,
name|ChangeIndexer
name|indexer
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
name|indexer
operator|=
name|indexer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (ChangeResource req, Input input)
specifier|public
name|Object
name|apply
parameter_list|(
name|ChangeResource
name|req
parameter_list|,
name|Input
name|input
parameter_list|)
throws|throws
name|BadRequestException
throws|,
name|AuthException
throws|,
name|ResourceConflictException
throws|,
name|Exception
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
operator|(
name|IdentifiedUser
operator|)
name|control
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
name|Change
name|change
init|=
name|req
operator|.
name|getChange
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|control
operator|.
name|canAbandon
argument_list|()
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
elseif|else
if|if
condition|(
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
name|ChangeMessage
name|message
decl_stmt|;
name|ReviewDb
name|db
init|=
name|dbProvider
operator|.
name|get
argument_list|()
decl_stmt|;
name|db
operator|.
name|changes
argument_list|()
operator|.
name|beginTransaction
argument_list|(
name|change
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|change
operator|=
name|db
operator|.
name|changes
argument_list|()
operator|.
name|atomicUpdate
argument_list|(
name|change
operator|.
name|getId
argument_list|()
argument_list|,
operator|new
name|AtomicUpdate
argument_list|<
name|Change
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Change
name|update
parameter_list|(
name|Change
name|change
parameter_list|)
block|{
if|if
condition|(
name|change
operator|.
name|getStatus
argument_list|()
operator|.
name|isOpen
argument_list|()
condition|)
block|{
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
name|ChangeUtil
operator|.
name|updated
argument_list|(
name|change
argument_list|)
expr_stmt|;
return|return
name|change
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|change
operator|==
literal|null
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
name|db
operator|.
name|changes
argument_list|()
operator|.
name|get
argument_list|(
name|req
operator|.
name|getChange
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
argument_list|)
throw|;
block|}
name|message
operator|=
name|newMessage
argument_list|(
name|input
argument_list|,
name|caller
argument_list|,
name|change
argument_list|)
expr_stmt|;
name|db
operator|.
name|changeMessages
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|message
argument_list|)
argument_list|)
expr_stmt|;
operator|new
name|ApprovalsUtil
argument_list|(
name|db
argument_list|)
operator|.
name|syncChangeStatus
argument_list|(
name|change
argument_list|)
expr_stmt|;
name|db
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|db
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
name|CheckedFuture
argument_list|<
name|?
argument_list|,
name|IOException
argument_list|>
name|indexFuture
init|=
name|indexer
operator|.
name|indexAsync
argument_list|(
name|change
argument_list|)
decl_stmt|;
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
argument_list|)
decl_stmt|;
name|cm
operator|.
name|setFrom
argument_list|(
name|caller
operator|.
name|getAccountId
argument_list|()
argument_list|)
expr_stmt|;
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
name|getChangeId
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
name|caller
operator|.
name|getAccount
argument_list|()
argument_list|,
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|get
argument_list|(
name|change
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
argument_list|,
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|input
operator|.
name|message
argument_list|)
argument_list|,
name|db
argument_list|)
expr_stmt|;
name|ChangeInfo
name|result
init|=
name|json
operator|.
name|format
argument_list|(
name|change
argument_list|)
decl_stmt|;
name|indexFuture
operator|.
name|checkedGet
argument_list|()
expr_stmt|;
return|return
name|result
return|;
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
name|getControl
argument_list|()
operator|.
name|canAbandon
argument_list|()
argument_list|)
return|;
block|}
DECL|method|newMessage (Input input, IdentifiedUser caller, Change change)
specifier|private
name|ChangeMessage
name|newMessage
parameter_list|(
name|Input
name|input
parameter_list|,
name|IdentifiedUser
name|caller
parameter_list|,
name|Change
name|change
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
name|input
operator|.
name|message
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
name|input
operator|.
name|message
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
name|dbProvider
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|caller
operator|.
name|getAccountId
argument_list|()
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

