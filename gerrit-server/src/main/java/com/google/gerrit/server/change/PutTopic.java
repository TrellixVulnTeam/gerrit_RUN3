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
name|PutTopic
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
DECL|class|PutTopic
class|class
name|PutTopic
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
DECL|field|dbProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|dbProvider
decl_stmt|;
DECL|field|indexer
specifier|private
specifier|final
name|ChangeIndexer
name|indexer
decl_stmt|;
DECL|field|hooks
specifier|private
specifier|final
name|ChangeHooks
name|hooks
decl_stmt|;
DECL|class|Input
specifier|static
class|class
name|Input
block|{
annotation|@
name|DefaultInput
DECL|field|topic
name|String
name|topic
decl_stmt|;
DECL|field|message
name|String
name|message
decl_stmt|;
block|}
annotation|@
name|Inject
DECL|method|PutTopic (Provider<ReviewDb> dbProvider, ChangeIndexer indexer, ChangeHooks hooks)
name|PutTopic
parameter_list|(
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|dbProvider
parameter_list|,
name|ChangeIndexer
name|indexer
parameter_list|,
name|ChangeHooks
name|hooks
parameter_list|)
block|{
name|this
operator|.
name|dbProvider
operator|=
name|dbProvider
expr_stmt|;
name|this
operator|.
name|indexer
operator|=
name|indexer
expr_stmt|;
name|this
operator|.
name|hooks
operator|=
name|hooks
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
if|if
condition|(
name|input
operator|==
literal|null
condition|)
block|{
name|input
operator|=
operator|new
name|Input
argument_list|()
expr_stmt|;
block|}
name|ChangeControl
name|control
init|=
name|req
operator|.
name|getControl
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
name|canEditTopicName
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AuthException
argument_list|(
literal|"changing topic not permitted"
argument_list|)
throw|;
block|}
name|ReviewDb
name|db
init|=
name|dbProvider
operator|.
name|get
argument_list|()
decl_stmt|;
specifier|final
name|String
name|newTopicName
init|=
name|Strings
operator|.
name|nullToEmpty
argument_list|(
name|input
operator|.
name|topic
argument_list|)
decl_stmt|;
name|String
name|oldTopicName
init|=
name|Strings
operator|.
name|nullToEmpty
argument_list|(
name|change
operator|.
name|getTopic
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|oldTopicName
operator|.
name|equals
argument_list|(
name|newTopicName
argument_list|)
condition|)
block|{
name|String
name|summary
decl_stmt|;
if|if
condition|(
name|oldTopicName
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|summary
operator|=
literal|"Topic set to \""
operator|+
name|newTopicName
operator|+
literal|"\"."
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|newTopicName
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|summary
operator|=
literal|"Topic \""
operator|+
name|oldTopicName
operator|+
literal|"\" removed."
expr_stmt|;
block|}
else|else
block|{
name|summary
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"Topic updated from \"%s\" to \"%s\"."
argument_list|,
name|oldTopicName
argument_list|,
name|newTopicName
argument_list|)
expr_stmt|;
block|}
name|IdentifiedUser
name|currentUser
init|=
operator|(
operator|(
name|IdentifiedUser
operator|)
name|control
operator|.
name|getCurrentUser
argument_list|()
operator|)
decl_stmt|;
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
name|currentUser
operator|.
name|getAccountId
argument_list|()
argument_list|,
name|change
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
decl_stmt|;
name|StringBuilder
name|msgBuf
init|=
operator|new
name|StringBuilder
argument_list|(
name|summary
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|input
operator|.
name|message
argument_list|)
condition|)
block|{
name|msgBuf
operator|.
name|append
argument_list|(
literal|"\n\n"
argument_list|)
expr_stmt|;
name|msgBuf
operator|.
name|append
argument_list|(
name|input
operator|.
name|message
argument_list|)
expr_stmt|;
block|}
name|cmsg
operator|.
name|setMessage
argument_list|(
name|msgBuf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
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
name|change
operator|.
name|setTopic
argument_list|(
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|newTopicName
argument_list|)
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
block|}
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
name|cmsg
argument_list|)
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
name|hooks
operator|.
name|doTopicChangedHook
argument_list|(
name|change
argument_list|,
name|currentUser
operator|.
name|getAccount
argument_list|()
argument_list|,
name|oldTopicName
argument_list|,
name|db
argument_list|)
expr_stmt|;
name|indexFuture
operator|.
name|checkedGet
argument_list|()
expr_stmt|;
block|}
return|return
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|newTopicName
argument_list|)
condition|?
name|Response
operator|.
name|none
argument_list|()
else|:
name|newTopicName
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
literal|"Edit Topic"
argument_list|)
operator|.
name|setVisible
argument_list|(
name|resource
operator|.
name|getControl
argument_list|()
operator|.
name|canEditTopicName
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

