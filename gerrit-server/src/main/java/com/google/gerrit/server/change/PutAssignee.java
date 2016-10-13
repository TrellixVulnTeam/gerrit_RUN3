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
name|AddReviewerInput
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
name|api
operator|.
name|changes
operator|.
name|NotifyHandling
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
name|client
operator|.
name|ReviewerState
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
name|AccountInfo
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
name|Response
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
name|account
operator|.
name|AccountJson
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
name|PostReviewers
operator|.
name|Addition
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
name|UpdateException
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|PutAssignee
specifier|public
class|class
name|PutAssignee
implements|implements
name|RestModifyView
argument_list|<
name|ChangeResource
argument_list|,
name|AssigneeInput
argument_list|>
implements|,
name|UiAction
argument_list|<
name|ChangeResource
argument_list|>
block|{
DECL|field|assigneeFactory
specifier|private
specifier|final
name|SetAssigneeOp
operator|.
name|Factory
name|assigneeFactory
decl_stmt|;
DECL|field|batchUpdateFactory
specifier|private
specifier|final
name|BatchUpdate
operator|.
name|Factory
name|batchUpdateFactory
decl_stmt|;
DECL|field|db
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
decl_stmt|;
DECL|field|postReviewers
specifier|private
specifier|final
name|PostReviewers
name|postReviewers
decl_stmt|;
annotation|@
name|Inject
DECL|method|PutAssignee (SetAssigneeOp.Factory assigneeFactory, BatchUpdate.Factory batchUpdateFactory, Provider<ReviewDb> db, PostReviewers postReviewers)
name|PutAssignee
parameter_list|(
name|SetAssigneeOp
operator|.
name|Factory
name|assigneeFactory
parameter_list|,
name|BatchUpdate
operator|.
name|Factory
name|batchUpdateFactory
parameter_list|,
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|,
name|PostReviewers
name|postReviewers
parameter_list|)
block|{
name|this
operator|.
name|assigneeFactory
operator|=
name|assigneeFactory
expr_stmt|;
name|this
operator|.
name|batchUpdateFactory
operator|=
name|batchUpdateFactory
expr_stmt|;
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|postReviewers
operator|=
name|postReviewers
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (ChangeResource rsrc, AssigneeInput input)
specifier|public
name|Response
argument_list|<
name|AccountInfo
argument_list|>
name|apply
parameter_list|(
name|ChangeResource
name|rsrc
parameter_list|,
name|AssigneeInput
name|input
parameter_list|)
throws|throws
name|RestApiException
throws|,
name|UpdateException
throws|,
name|OrmException
throws|,
name|IOException
block|{
if|if
condition|(
operator|!
name|rsrc
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
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|input
operator|.
name|assignee
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"missing assignee field"
argument_list|)
throw|;
block|}
try|try
init|(
name|BatchUpdate
name|bu
init|=
name|batchUpdateFactory
operator|.
name|create
argument_list|(
name|db
operator|.
name|get
argument_list|()
argument_list|,
name|rsrc
operator|.
name|getChange
argument_list|()
operator|.
name|getProject
argument_list|()
argument_list|,
name|rsrc
operator|.
name|getControl
argument_list|()
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
name|SetAssigneeOp
name|op
init|=
name|assigneeFactory
operator|.
name|create
argument_list|(
name|input
operator|.
name|assignee
argument_list|)
decl_stmt|;
name|bu
operator|.
name|addOp
argument_list|(
name|rsrc
operator|.
name|getId
argument_list|()
argument_list|,
name|op
argument_list|)
expr_stmt|;
name|PostReviewers
operator|.
name|Addition
name|reviewersAddition
init|=
name|addAssigneeAsCC
argument_list|(
name|rsrc
argument_list|,
name|input
operator|.
name|assignee
argument_list|)
decl_stmt|;
name|bu
operator|.
name|addOp
argument_list|(
name|rsrc
operator|.
name|getId
argument_list|()
argument_list|,
name|reviewersAddition
operator|.
name|op
argument_list|)
expr_stmt|;
name|bu
operator|.
name|execute
argument_list|()
expr_stmt|;
return|return
name|Response
operator|.
name|ok
argument_list|(
name|AccountJson
operator|.
name|toAccountInfo
argument_list|(
name|op
operator|.
name|getNewAssignee
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|method|addAssigneeAsCC (ChangeResource rsrc, String assignee)
specifier|private
name|Addition
name|addAssigneeAsCC
parameter_list|(
name|ChangeResource
name|rsrc
parameter_list|,
name|String
name|assignee
parameter_list|)
throws|throws
name|OrmException
throws|,
name|RestApiException
throws|,
name|IOException
block|{
name|AddReviewerInput
name|reviewerInput
init|=
operator|new
name|AddReviewerInput
argument_list|()
decl_stmt|;
name|reviewerInput
operator|.
name|reviewer
operator|=
name|assignee
expr_stmt|;
name|reviewerInput
operator|.
name|state
operator|=
name|ReviewerState
operator|.
name|CC
expr_stmt|;
name|reviewerInput
operator|.
name|confirmed
operator|=
literal|true
expr_stmt|;
name|reviewerInput
operator|.
name|notify
operator|=
name|NotifyHandling
operator|.
name|NONE
expr_stmt|;
return|return
name|postReviewers
operator|.
name|prepareApplication
argument_list|(
name|rsrc
argument_list|,
name|reviewerInput
argument_list|)
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
literal|"Edit Assignee"
argument_list|)
operator|.
name|setVisible
argument_list|(
name|resource
operator|.
name|getControl
argument_list|()
operator|.
name|canEditAssignee
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

