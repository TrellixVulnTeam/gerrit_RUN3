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
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
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
name|flogger
operator|.
name|FluentLogger
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
name|extensions
operator|.
name|events
operator|.
name|AssigneeChanged
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
name|send
operator|.
name|SetAssigneeSender
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
name|plugincontext
operator|.
name|PluginSetContext
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
name|update
operator|.
name|BatchUpdateOp
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
name|update
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
name|update
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
name|validators
operator|.
name|AssigneeValidationListener
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
name|validators
operator|.
name|ValidationException
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
name|assistedinject
operator|.
name|Assisted
import|;
end_import

begin_class
DECL|class|SetAssigneeOp
specifier|public
class|class
name|SetAssigneeOp
implements|implements
name|BatchUpdateOp
block|{
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|FluentLogger
name|logger
init|=
name|FluentLogger
operator|.
name|forEnclosingClass
argument_list|()
decl_stmt|;
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (IdentifiedUser assignee)
name|SetAssigneeOp
name|create
parameter_list|(
name|IdentifiedUser
name|assignee
parameter_list|)
function_decl|;
block|}
DECL|field|cmUtil
specifier|private
specifier|final
name|ChangeMessagesUtil
name|cmUtil
decl_stmt|;
DECL|field|validationListeners
specifier|private
specifier|final
name|PluginSetContext
argument_list|<
name|AssigneeValidationListener
argument_list|>
name|validationListeners
decl_stmt|;
DECL|field|newAssignee
specifier|private
specifier|final
name|IdentifiedUser
name|newAssignee
decl_stmt|;
DECL|field|assigneeChanged
specifier|private
specifier|final
name|AssigneeChanged
name|assigneeChanged
decl_stmt|;
DECL|field|setAssigneeSenderFactory
specifier|private
specifier|final
name|SetAssigneeSender
operator|.
name|Factory
name|setAssigneeSenderFactory
decl_stmt|;
DECL|field|user
specifier|private
specifier|final
name|Provider
argument_list|<
name|IdentifiedUser
argument_list|>
name|user
decl_stmt|;
DECL|field|userFactory
specifier|private
specifier|final
name|IdentifiedUser
operator|.
name|GenericFactory
name|userFactory
decl_stmt|;
DECL|field|change
specifier|private
name|Change
name|change
decl_stmt|;
DECL|field|oldAssignee
specifier|private
name|IdentifiedUser
name|oldAssignee
decl_stmt|;
annotation|@
name|Inject
DECL|method|SetAssigneeOp ( ChangeMessagesUtil cmUtil, PluginSetContext<AssigneeValidationListener> validationListeners, AssigneeChanged assigneeChanged, SetAssigneeSender.Factory setAssigneeSenderFactory, Provider<IdentifiedUser> user, IdentifiedUser.GenericFactory userFactory, @Assisted IdentifiedUser newAssignee)
name|SetAssigneeOp
parameter_list|(
name|ChangeMessagesUtil
name|cmUtil
parameter_list|,
name|PluginSetContext
argument_list|<
name|AssigneeValidationListener
argument_list|>
name|validationListeners
parameter_list|,
name|AssigneeChanged
name|assigneeChanged
parameter_list|,
name|SetAssigneeSender
operator|.
name|Factory
name|setAssigneeSenderFactory
parameter_list|,
name|Provider
argument_list|<
name|IdentifiedUser
argument_list|>
name|user
parameter_list|,
name|IdentifiedUser
operator|.
name|GenericFactory
name|userFactory
parameter_list|,
annotation|@
name|Assisted
name|IdentifiedUser
name|newAssignee
parameter_list|)
block|{
name|this
operator|.
name|cmUtil
operator|=
name|cmUtil
expr_stmt|;
name|this
operator|.
name|validationListeners
operator|=
name|validationListeners
expr_stmt|;
name|this
operator|.
name|assigneeChanged
operator|=
name|assigneeChanged
expr_stmt|;
name|this
operator|.
name|setAssigneeSenderFactory
operator|=
name|setAssigneeSenderFactory
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|userFactory
operator|=
name|userFactory
expr_stmt|;
name|this
operator|.
name|newAssignee
operator|=
name|requireNonNull
argument_list|(
name|newAssignee
argument_list|,
literal|"assignee"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|updateChange (ChangeContext ctx)
specifier|public
name|boolean
name|updateChange
parameter_list|(
name|ChangeContext
name|ctx
parameter_list|)
throws|throws
name|RestApiException
block|{
name|change
operator|=
name|ctx
operator|.
name|getChange
argument_list|()
expr_stmt|;
if|if
condition|(
name|newAssignee
operator|.
name|getAccountId
argument_list|()
operator|.
name|equals
argument_list|(
name|change
operator|.
name|getAssignee
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
try|try
block|{
name|validationListeners
operator|.
name|runEach
argument_list|(
name|l
lambda|->
name|l
operator|.
name|validateAssignee
argument_list|(
name|change
argument_list|,
name|newAssignee
operator|.
name|getAccount
argument_list|()
argument_list|)
argument_list|,
name|ValidationException
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ValidationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceConflictException
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
if|if
condition|(
name|change
operator|.
name|getAssignee
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|oldAssignee
operator|=
name|userFactory
operator|.
name|create
argument_list|(
name|change
operator|.
name|getAssignee
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ChangeUpdate
name|update
init|=
name|ctx
operator|.
name|getUpdate
argument_list|(
name|change
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
decl_stmt|;
comment|// notedb
name|update
operator|.
name|setAssignee
argument_list|(
name|newAssignee
operator|.
name|getAccountId
argument_list|()
argument_list|)
expr_stmt|;
comment|// reviewdb
name|change
operator|.
name|setAssignee
argument_list|(
name|newAssignee
operator|.
name|getAccountId
argument_list|()
argument_list|)
expr_stmt|;
name|addMessage
argument_list|(
name|ctx
argument_list|,
name|update
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|addMessage (ChangeContext ctx, ChangeUpdate update)
specifier|private
name|void
name|addMessage
parameter_list|(
name|ChangeContext
name|ctx
parameter_list|,
name|ChangeUpdate
name|update
parameter_list|)
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
name|oldAssignee
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
name|getNameEmail
argument_list|()
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
name|oldAssignee
operator|.
name|getNameEmail
argument_list|()
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
name|getNameEmail
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ChangeMessage
name|cmsg
init|=
name|ChangeMessagesUtil
operator|.
name|newMessage
argument_list|(
name|ctx
argument_list|,
name|msg
operator|.
name|toString
argument_list|()
argument_list|,
name|ChangeMessagesUtil
operator|.
name|TAG_SET_ASSIGNEE
argument_list|)
decl_stmt|;
name|cmUtil
operator|.
name|addChangeMessage
argument_list|(
name|update
argument_list|,
name|cmsg
argument_list|)
expr_stmt|;
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
block|{
try|try
block|{
name|SetAssigneeSender
name|cm
init|=
name|setAssigneeSenderFactory
operator|.
name|create
argument_list|(
name|change
operator|.
name|getProject
argument_list|()
argument_list|,
name|change
operator|.
name|getId
argument_list|()
argument_list|,
name|newAssignee
operator|.
name|getAccountId
argument_list|()
argument_list|)
decl_stmt|;
name|cm
operator|.
name|setFrom
argument_list|(
name|user
operator|.
name|get
argument_list|()
operator|.
name|getAccountId
argument_list|()
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
name|err
parameter_list|)
block|{
name|logger
operator|.
name|atSevere
argument_list|()
operator|.
name|withCause
argument_list|(
name|err
argument_list|)
operator|.
name|log
argument_list|(
literal|"Cannot send email to new assignee of change %s"
argument_list|,
name|change
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assigneeChanged
operator|.
name|fire
argument_list|(
name|change
argument_list|,
name|ctx
operator|.
name|getAccount
argument_list|()
argument_list|,
name|oldAssignee
operator|!=
literal|null
condition|?
name|oldAssignee
operator|.
name|state
argument_list|()
else|:
literal|null
argument_list|,
name|ctx
operator|.
name|getWhen
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

