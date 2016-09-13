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
name|Optional
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
name|AssigneeInput
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
name|UnprocessableEntityException
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
name|account
operator|.
name|AccountInfoCacheFactory
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
name|AccountsCollection
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
name|AnonymousCowardName
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
name|notedb
operator|.
name|NotesMigration
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
name|assistedinject
operator|.
name|Assisted
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
name|assistedinject
operator|.
name|AssistedInject
import|;
end_import

begin_class
DECL|class|SetAssigneeOp
specifier|public
class|class
name|SetAssigneeOp
extends|extends
name|BatchUpdate
operator|.
name|Op
block|{
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (AssigneeInput input)
name|SetAssigneeOp
name|create
parameter_list|(
name|AssigneeInput
name|input
parameter_list|)
function_decl|;
block|}
DECL|field|input
specifier|private
specifier|final
name|AssigneeInput
name|input
decl_stmt|;
DECL|field|accounts
specifier|private
specifier|final
name|AccountsCollection
name|accounts
decl_stmt|;
DECL|field|cmUtil
specifier|private
specifier|final
name|ChangeMessagesUtil
name|cmUtil
decl_stmt|;
DECL|field|accountInfosFactory
specifier|private
specifier|final
name|AccountInfoCacheFactory
operator|.
name|Factory
name|accountInfosFactory
decl_stmt|;
DECL|field|notesMigration
specifier|private
specifier|final
name|NotesMigration
name|notesMigration
decl_stmt|;
DECL|field|anonymousCowardName
specifier|private
specifier|final
name|String
name|anonymousCowardName
decl_stmt|;
DECL|field|newAssignee
specifier|private
name|Account
name|newAssignee
decl_stmt|;
annotation|@
name|AssistedInject
DECL|method|SetAssigneeOp (AccountsCollection accounts, NotesMigration notesMigration, ChangeMessagesUtil cmUtil, AccountInfoCacheFactory.Factory accountInfosFactory, @AnonymousCowardName String anonymousCowardName, @Assisted AssigneeInput input)
name|SetAssigneeOp
parameter_list|(
name|AccountsCollection
name|accounts
parameter_list|,
name|NotesMigration
name|notesMigration
parameter_list|,
name|ChangeMessagesUtil
name|cmUtil
parameter_list|,
name|AccountInfoCacheFactory
operator|.
name|Factory
name|accountInfosFactory
parameter_list|,
annotation|@
name|AnonymousCowardName
name|String
name|anonymousCowardName
parameter_list|,
annotation|@
name|Assisted
name|AssigneeInput
name|input
parameter_list|)
block|{
name|this
operator|.
name|accounts
operator|=
name|accounts
expr_stmt|;
name|this
operator|.
name|notesMigration
operator|=
name|notesMigration
expr_stmt|;
name|this
operator|.
name|cmUtil
operator|=
name|cmUtil
expr_stmt|;
name|this
operator|.
name|accountInfosFactory
operator|=
name|accountInfosFactory
expr_stmt|;
name|this
operator|.
name|anonymousCowardName
operator|=
name|anonymousCowardName
expr_stmt|;
name|this
operator|.
name|input
operator|=
name|input
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|updateChange (BatchUpdate.ChangeContext ctx)
specifier|public
name|boolean
name|updateChange
parameter_list|(
name|BatchUpdate
operator|.
name|ChangeContext
name|ctx
parameter_list|)
throws|throws
name|OrmException
throws|,
name|RestApiException
block|{
if|if
condition|(
operator|!
name|notesMigration
operator|.
name|readChanges
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"Cannot add Assignee; NoteDb is disabled"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|ctx
operator|.
name|getControl
argument_list|()
operator|.
name|canEditAssignee
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AuthException
argument_list|(
literal|"Changing Assignee not permitted"
argument_list|)
throw|;
block|}
name|ChangeUpdate
name|update
init|=
name|ctx
operator|.
name|getUpdate
argument_list|(
name|ctx
operator|.
name|getChange
argument_list|()
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
decl_stmt|;
name|Optional
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|oldAssigneeId
init|=
name|update
operator|.
name|getNotes
argument_list|()
operator|.
name|getAssignee
argument_list|()
decl_stmt|;
if|if
condition|(
name|input
operator|.
name|assignee
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|oldAssigneeId
operator|!=
literal|null
operator|&&
name|oldAssigneeId
operator|.
name|isPresent
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AuthException
argument_list|(
literal|"Cannot set Assignee to empty"
argument_list|)
throw|;
block|}
return|return
literal|false
return|;
block|}
name|Account
name|oldAssignee
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|oldAssigneeId
operator|!=
literal|null
operator|&&
name|oldAssigneeId
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|oldAssignee
operator|=
name|accountInfosFactory
operator|.
name|create
argument_list|()
operator|.
name|get
argument_list|(
name|oldAssigneeId
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|IdentifiedUser
name|newAssigneeUser
init|=
name|accounts
operator|.
name|parse
argument_list|(
name|input
operator|.
name|assignee
argument_list|)
decl_stmt|;
if|if
condition|(
name|oldAssigneeId
operator|!=
literal|null
operator|&&
name|oldAssigneeId
operator|.
name|equals
argument_list|(
name|newAssigneeUser
operator|.
name|getAccountId
argument_list|()
argument_list|)
condition|)
block|{
name|newAssignee
operator|=
name|oldAssignee
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|newAssigneeUser
operator|.
name|getAccount
argument_list|()
operator|.
name|isActive
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|UnprocessableEntityException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Account of %s is not active"
argument_list|,
name|newAssigneeUser
operator|.
name|getUserName
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|ctx
operator|.
name|getControl
argument_list|()
operator|.
name|forUser
argument_list|(
name|newAssigneeUser
argument_list|)
operator|.
name|isRefVisible
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AuthException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Change %s is not visible to %s."
argument_list|,
name|ctx
operator|.
name|getChange
argument_list|()
operator|.
name|getChangeId
argument_list|()
argument_list|,
name|newAssigneeUser
operator|.
name|getUserName
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
name|update
operator|.
name|setAssignee
argument_list|(
name|Optional
operator|.
name|fromNullable
argument_list|(
name|newAssigneeUser
operator|.
name|getAccountId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|newAssignee
operator|=
name|newAssigneeUser
operator|.
name|getAccount
argument_list|()
expr_stmt|;
name|addMessage
argument_list|(
name|ctx
argument_list|,
name|update
argument_list|,
name|oldAssignee
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|addMessage (BatchUpdate.ChangeContext ctx, ChangeUpdate update, Account previousAssignee)
specifier|private
name|void
name|addMessage
parameter_list|(
name|BatchUpdate
operator|.
name|ChangeContext
name|ctx
parameter_list|,
name|ChangeUpdate
name|update
parameter_list|,
name|Account
name|previousAssignee
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
literal|"Assignee "
argument_list|)
expr_stmt|;
if|if
condition|(
name|previousAssignee
operator|==
literal|null
condition|)
block|{
name|msg
operator|.
name|append
argument_list|(
literal|"added: "
argument_list|)
expr_stmt|;
name|msg
operator|.
name|append
argument_list|(
name|newAssignee
operator|.
name|getName
argument_list|(
name|anonymousCowardName
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|msg
operator|.
name|append
argument_list|(
literal|"changed from: "
argument_list|)
expr_stmt|;
name|msg
operator|.
name|append
argument_list|(
name|previousAssignee
operator|.
name|getName
argument_list|(
name|anonymousCowardName
argument_list|)
argument_list|)
expr_stmt|;
name|msg
operator|.
name|append
argument_list|(
literal|" to: "
argument_list|)
expr_stmt|;
name|msg
operator|.
name|append
argument_list|(
name|newAssignee
operator|.
name|getName
argument_list|(
name|anonymousCowardName
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ChangeMessage
name|cmsg
init|=
operator|new
name|ChangeMessage
argument_list|(
operator|new
name|ChangeMessage
operator|.
name|Key
argument_list|(
name|ctx
operator|.
name|getChange
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|ChangeUtil
operator|.
name|messageUUID
argument_list|(
name|ctx
operator|.
name|getDb
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|ctx
operator|.
name|getAccountId
argument_list|()
argument_list|,
name|ctx
operator|.
name|getWhen
argument_list|()
argument_list|,
name|ctx
operator|.
name|getChange
argument_list|()
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
decl_stmt|;
name|cmsg
operator|.
name|setMessage
argument_list|(
name|msg
operator|.
name|toString
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
name|cmsg
argument_list|)
expr_stmt|;
block|}
DECL|method|getNewAssignee ()
specifier|public
name|Account
name|getNewAssignee
parameter_list|()
block|{
return|return
name|newAssignee
return|;
block|}
block|}
end_class

end_unit

