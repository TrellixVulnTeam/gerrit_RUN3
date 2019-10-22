begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2018 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.restapi.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|restapi
operator|.
name|change
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|ChangeMessagesUtil
operator|.
name|createChangeMessageInfo
import|;
end_import

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
name|Nullable
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
name|entities
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
name|entities
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
name|entities
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
name|extensions
operator|.
name|api
operator|.
name|changes
operator|.
name|DeleteChangeMessageInput
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
name|ChangeMessageInfo
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
name|CurrentUser
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
name|AccountLoader
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
name|ChangeMessageResource
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
name|ChangeNotes
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
name|permissions
operator|.
name|GlobalPermission
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
name|permissions
operator|.
name|PermissionBackend
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
name|permissions
operator|.
name|PermissionBackendException
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
name|util
operator|.
name|time
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/** Deletes a change message by rewriting history. */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|DeleteChangeMessage
specifier|public
class|class
name|DeleteChangeMessage
implements|implements
name|RestModifyView
argument_list|<
name|ChangeMessageResource
argument_list|,
name|DeleteChangeMessageInput
argument_list|>
block|{
DECL|field|userProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|userProvider
decl_stmt|;
DECL|field|permissionBackend
specifier|private
specifier|final
name|PermissionBackend
name|permissionBackend
decl_stmt|;
DECL|field|updateFactory
specifier|private
specifier|final
name|BatchUpdate
operator|.
name|Factory
name|updateFactory
decl_stmt|;
DECL|field|changeMessagesUtil
specifier|private
specifier|final
name|ChangeMessagesUtil
name|changeMessagesUtil
decl_stmt|;
DECL|field|accountLoaderFactory
specifier|private
specifier|final
name|AccountLoader
operator|.
name|Factory
name|accountLoaderFactory
decl_stmt|;
DECL|field|notesFactory
specifier|private
specifier|final
name|ChangeNotes
operator|.
name|Factory
name|notesFactory
decl_stmt|;
annotation|@
name|Inject
DECL|method|DeleteChangeMessage ( Provider<CurrentUser> userProvider, PermissionBackend permissionBackend, BatchUpdate.Factory updateFactory, ChangeMessagesUtil changeMessagesUtil, AccountLoader.Factory accountLoaderFactory, ChangeNotes.Factory notesFactory)
specifier|public
name|DeleteChangeMessage
parameter_list|(
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|userProvider
parameter_list|,
name|PermissionBackend
name|permissionBackend
parameter_list|,
name|BatchUpdate
operator|.
name|Factory
name|updateFactory
parameter_list|,
name|ChangeMessagesUtil
name|changeMessagesUtil
parameter_list|,
name|AccountLoader
operator|.
name|Factory
name|accountLoaderFactory
parameter_list|,
name|ChangeNotes
operator|.
name|Factory
name|notesFactory
parameter_list|)
block|{
name|this
operator|.
name|userProvider
operator|=
name|userProvider
expr_stmt|;
name|this
operator|.
name|permissionBackend
operator|=
name|permissionBackend
expr_stmt|;
name|this
operator|.
name|updateFactory
operator|=
name|updateFactory
expr_stmt|;
name|this
operator|.
name|changeMessagesUtil
operator|=
name|changeMessagesUtil
expr_stmt|;
name|this
operator|.
name|accountLoaderFactory
operator|=
name|accountLoaderFactory
expr_stmt|;
name|this
operator|.
name|notesFactory
operator|=
name|notesFactory
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply ( ChangeMessageResource resource, DeleteChangeMessageInput input)
specifier|public
name|Response
argument_list|<
name|ChangeMessageInfo
argument_list|>
name|apply
parameter_list|(
name|ChangeMessageResource
name|resource
parameter_list|,
name|DeleteChangeMessageInput
name|input
parameter_list|)
throws|throws
name|RestApiException
throws|,
name|PermissionBackendException
throws|,
name|UpdateException
throws|,
name|IOException
block|{
name|CurrentUser
name|user
init|=
name|userProvider
operator|.
name|get
argument_list|()
decl_stmt|;
name|permissionBackend
operator|.
name|user
argument_list|(
name|user
argument_list|)
operator|.
name|check
argument_list|(
name|GlobalPermission
operator|.
name|ADMINISTRATE_SERVER
argument_list|)
expr_stmt|;
name|String
name|newChangeMessage
init|=
name|createNewChangeMessage
argument_list|(
name|user
operator|.
name|asIdentifiedUser
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|input
operator|.
name|reason
argument_list|)
decl_stmt|;
name|DeleteChangeMessageOp
name|deleteChangeMessageOp
init|=
operator|new
name|DeleteChangeMessageOp
argument_list|(
name|resource
operator|.
name|getChangeMessageId
argument_list|()
argument_list|,
name|newChangeMessage
argument_list|)
decl_stmt|;
try|try
init|(
name|BatchUpdate
name|batchUpdate
init|=
name|updateFactory
operator|.
name|create
argument_list|(
name|resource
operator|.
name|getChangeResource
argument_list|()
operator|.
name|getProject
argument_list|()
argument_list|,
name|user
argument_list|,
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
init|)
block|{
name|batchUpdate
operator|.
name|addOp
argument_list|(
name|resource
operator|.
name|getChangeId
argument_list|()
argument_list|,
name|deleteChangeMessageOp
argument_list|)
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
name|ChangeMessageInfo
name|updatedMessageInfo
init|=
name|createUpdatedChangeMessageInfo
argument_list|(
name|resource
operator|.
name|getChangeId
argument_list|()
argument_list|,
name|resource
operator|.
name|getChangeMessageIndex
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|Response
operator|.
name|created
argument_list|(
name|updatedMessageInfo
argument_list|)
return|;
block|}
DECL|method|createUpdatedChangeMessageInfo (Change.Id id, int targetIdx)
specifier|private
name|ChangeMessageInfo
name|createUpdatedChangeMessageInfo
parameter_list|(
name|Change
operator|.
name|Id
name|id
parameter_list|,
name|int
name|targetIdx
parameter_list|)
throws|throws
name|PermissionBackendException
block|{
name|List
argument_list|<
name|ChangeMessage
argument_list|>
name|messages
init|=
name|changeMessagesUtil
operator|.
name|byChange
argument_list|(
name|notesFactory
operator|.
name|createChecked
argument_list|(
name|id
argument_list|)
argument_list|)
decl_stmt|;
name|ChangeMessage
name|updatedChangeMessage
init|=
name|messages
operator|.
name|get
argument_list|(
name|targetIdx
argument_list|)
decl_stmt|;
name|AccountLoader
name|accountLoader
init|=
name|accountLoaderFactory
operator|.
name|create
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|ChangeMessageInfo
name|info
init|=
name|createChangeMessageInfo
argument_list|(
name|updatedChangeMessage
argument_list|,
name|accountLoader
argument_list|)
decl_stmt|;
name|accountLoader
operator|.
name|fill
argument_list|()
expr_stmt|;
return|return
name|info
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|createNewChangeMessage (String deletedBy, @Nullable String deletedReason)
specifier|public
specifier|static
name|String
name|createNewChangeMessage
parameter_list|(
name|String
name|deletedBy
parameter_list|,
annotation|@
name|Nullable
name|String
name|deletedReason
parameter_list|)
block|{
name|requireNonNull
argument_list|(
name|deletedBy
argument_list|,
literal|"user name must not be null"
argument_list|)
expr_stmt|;
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|deletedReason
argument_list|)
condition|)
block|{
return|return
name|createNewChangeMessage
argument_list|(
name|deletedBy
argument_list|)
return|;
block|}
return|return
name|String
operator|.
name|format
argument_list|(
literal|"Change message removed by: %s\nReason: %s"
argument_list|,
name|deletedBy
argument_list|,
name|deletedReason
argument_list|)
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|createNewChangeMessage (String deletedBy)
specifier|public
specifier|static
name|String
name|createNewChangeMessage
parameter_list|(
name|String
name|deletedBy
parameter_list|)
block|{
name|requireNonNull
argument_list|(
name|deletedBy
argument_list|,
literal|"user name must not be null"
argument_list|)
expr_stmt|;
return|return
literal|"Change message removed by: "
operator|+
name|deletedBy
return|;
block|}
DECL|class|DeleteChangeMessageOp
specifier|private
class|class
name|DeleteChangeMessageOp
implements|implements
name|BatchUpdateOp
block|{
DECL|field|targetMessageId
specifier|private
specifier|final
name|String
name|targetMessageId
decl_stmt|;
DECL|field|newMessage
specifier|private
specifier|final
name|String
name|newMessage
decl_stmt|;
DECL|method|DeleteChangeMessageOp (String targetMessageIdx, String newMessage)
name|DeleteChangeMessageOp
parameter_list|(
name|String
name|targetMessageIdx
parameter_list|,
name|String
name|newMessage
parameter_list|)
block|{
name|this
operator|.
name|targetMessageId
operator|=
name|targetMessageIdx
expr_stmt|;
name|this
operator|.
name|newMessage
operator|=
name|newMessage
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
block|{
name|PatchSet
operator|.
name|Id
name|psId
init|=
name|ctx
operator|.
name|getChange
argument_list|()
operator|.
name|currentPatchSetId
argument_list|()
decl_stmt|;
name|changeMessagesUtil
operator|.
name|replaceChangeMessage
argument_list|(
name|ctx
operator|.
name|getUpdate
argument_list|(
name|psId
argument_list|)
argument_list|,
name|targetMessageId
argument_list|,
name|newMessage
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
annotation|@
name|Singleton
DECL|class|DefaultDeleteChangeMessage
specifier|public
specifier|static
class|class
name|DefaultDeleteChangeMessage
implements|implements
name|RestModifyView
argument_list|<
name|ChangeMessageResource
argument_list|,
name|Input
argument_list|>
block|{
DECL|field|deleteChangeMessage
specifier|private
specifier|final
name|DeleteChangeMessage
name|deleteChangeMessage
decl_stmt|;
annotation|@
name|Inject
DECL|method|DefaultDeleteChangeMessage (DeleteChangeMessage deleteChangeMessage)
specifier|public
name|DefaultDeleteChangeMessage
parameter_list|(
name|DeleteChangeMessage
name|deleteChangeMessage
parameter_list|)
block|{
name|this
operator|.
name|deleteChangeMessage
operator|=
name|deleteChangeMessage
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (ChangeMessageResource resource, Input input)
specifier|public
name|Response
argument_list|<
name|ChangeMessageInfo
argument_list|>
name|apply
parameter_list|(
name|ChangeMessageResource
name|resource
parameter_list|,
name|Input
name|input
parameter_list|)
throws|throws
name|RestApiException
throws|,
name|PermissionBackendException
throws|,
name|UpdateException
throws|,
name|IOException
block|{
return|return
name|deleteChangeMessage
operator|.
name|apply
argument_list|(
name|resource
argument_list|,
operator|new
name|DeleteChangeMessageInput
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

