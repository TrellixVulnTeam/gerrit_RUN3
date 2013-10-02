begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2011 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.changedetail
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|changedetail
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
name|common
operator|.
name|data
operator|.
name|LabelTypes
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
name|data
operator|.
name|ReviewResult
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
name|account
operator|.
name|AccountResolver
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
name|GitRepositoryManager
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
name|CreateChangeSender
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
name|PatchSetNotificationSender
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
name|ReplacePatchSetSender
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
name|patch
operator|.
name|PatchSetInfoFactory
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
name|patch
operator|.
name|PatchSetInfoNotAvailableException
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
name|gerrit
operator|.
name|server
operator|.
name|project
operator|.
name|NoSuchChangeException
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
name|assistedinject
operator|.
name|Assisted
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
name|concurrent
operator|.
name|Callable
import|;
end_import

begin_class
DECL|class|PublishDraft
specifier|public
class|class
name|PublishDraft
implements|implements
name|Callable
argument_list|<
name|ReviewResult
argument_list|>
block|{
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (PatchSet.Id patchSetId)
name|PublishDraft
name|create
parameter_list|(
name|PatchSet
operator|.
name|Id
name|patchSetId
parameter_list|)
function_decl|;
block|}
DECL|field|changeControlFactory
specifier|private
specifier|final
name|ChangeControl
operator|.
name|Factory
name|changeControlFactory
decl_stmt|;
DECL|field|db
specifier|private
specifier|final
name|ReviewDb
name|db
decl_stmt|;
DECL|field|hooks
specifier|private
specifier|final
name|ChangeHooks
name|hooks
decl_stmt|;
DECL|field|indexer
specifier|private
specifier|final
name|ChangeIndexer
name|indexer
decl_stmt|;
DECL|field|sender
specifier|private
specifier|final
name|PatchSetNotificationSender
name|sender
decl_stmt|;
DECL|field|patchSetId
specifier|private
specifier|final
name|PatchSet
operator|.
name|Id
name|patchSetId
decl_stmt|;
annotation|@
name|Inject
DECL|method|PublishDraft (final ChangeControl.Factory changeControlFactory, final ReviewDb db, final ChangeHooks hooks, final GitRepositoryManager repoManager, final PatchSetInfoFactory patchSetInfoFactory, final ApprovalsUtil approvalsUtil, final AccountResolver accountResolver, final CreateChangeSender.Factory createChangeSenderFactory, final ReplacePatchSetSender.Factory replacePatchSetFactory, final ChangeIndexer indexer, final PatchSetNotificationSender sender, @Assisted final PatchSet.Id patchSetId)
name|PublishDraft
parameter_list|(
specifier|final
name|ChangeControl
operator|.
name|Factory
name|changeControlFactory
parameter_list|,
specifier|final
name|ReviewDb
name|db
parameter_list|,
specifier|final
name|ChangeHooks
name|hooks
parameter_list|,
specifier|final
name|GitRepositoryManager
name|repoManager
parameter_list|,
specifier|final
name|PatchSetInfoFactory
name|patchSetInfoFactory
parameter_list|,
specifier|final
name|ApprovalsUtil
name|approvalsUtil
parameter_list|,
specifier|final
name|AccountResolver
name|accountResolver
parameter_list|,
specifier|final
name|CreateChangeSender
operator|.
name|Factory
name|createChangeSenderFactory
parameter_list|,
specifier|final
name|ReplacePatchSetSender
operator|.
name|Factory
name|replacePatchSetFactory
parameter_list|,
specifier|final
name|ChangeIndexer
name|indexer
parameter_list|,
specifier|final
name|PatchSetNotificationSender
name|sender
parameter_list|,
annotation|@
name|Assisted
specifier|final
name|PatchSet
operator|.
name|Id
name|patchSetId
parameter_list|)
block|{
name|this
operator|.
name|changeControlFactory
operator|=
name|changeControlFactory
expr_stmt|;
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|hooks
operator|=
name|hooks
expr_stmt|;
name|this
operator|.
name|indexer
operator|=
name|indexer
expr_stmt|;
name|this
operator|.
name|sender
operator|=
name|sender
expr_stmt|;
name|this
operator|.
name|patchSetId
operator|=
name|patchSetId
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|ReviewResult
name|call
parameter_list|()
throws|throws
name|NoSuchChangeException
throws|,
name|OrmException
throws|,
name|IOException
throws|,
name|PatchSetInfoNotAvailableException
block|{
specifier|final
name|ReviewResult
name|result
init|=
operator|new
name|ReviewResult
argument_list|()
decl_stmt|;
specifier|final
name|Change
operator|.
name|Id
name|changeId
init|=
name|patchSetId
operator|.
name|getParentKey
argument_list|()
decl_stmt|;
name|result
operator|.
name|setChangeId
argument_list|(
name|changeId
argument_list|)
expr_stmt|;
specifier|final
name|ChangeControl
name|control
init|=
name|changeControlFactory
operator|.
name|validateFor
argument_list|(
name|changeId
argument_list|)
decl_stmt|;
specifier|final
name|LabelTypes
name|labelTypes
init|=
name|control
operator|.
name|getLabelTypes
argument_list|()
decl_stmt|;
specifier|final
name|PatchSet
name|patch
init|=
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|get
argument_list|(
name|patchSetId
argument_list|)
decl_stmt|;
if|if
condition|(
name|patch
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NoSuchChangeException
argument_list|(
name|changeId
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|patch
operator|.
name|isDraft
argument_list|()
condition|)
block|{
name|result
operator|.
name|addError
argument_list|(
operator|new
name|ReviewResult
operator|.
name|Error
argument_list|(
name|ReviewResult
operator|.
name|Error
operator|.
name|Type
operator|.
name|NOT_A_DRAFT
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
if|if
condition|(
operator|!
name|control
operator|.
name|canPublish
argument_list|(
name|db
argument_list|)
condition|)
block|{
name|result
operator|.
name|addError
argument_list|(
operator|new
name|ReviewResult
operator|.
name|Error
argument_list|(
name|ReviewResult
operator|.
name|Error
operator|.
name|Type
operator|.
name|PUBLISH_NOT_PERMITTED
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|PatchSet
name|updatedPatchSet
init|=
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|atomicUpdate
argument_list|(
name|patchSetId
argument_list|,
operator|new
name|AtomicUpdate
argument_list|<
name|PatchSet
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|PatchSet
name|update
parameter_list|(
name|PatchSet
name|patchset
parameter_list|)
block|{
name|patchset
operator|.
name|setDraft
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
name|patchset
return|;
block|}
block|}
argument_list|)
decl_stmt|;
specifier|final
name|Change
name|updatedChange
init|=
name|db
operator|.
name|changes
argument_list|()
operator|.
name|atomicUpdate
argument_list|(
name|changeId
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
operator|==
name|Change
operator|.
name|Status
operator|.
name|DRAFT
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
name|NEW
argument_list|)
expr_stmt|;
name|ChangeUtil
operator|.
name|updated
argument_list|(
name|change
argument_list|)
expr_stmt|;
block|}
return|return
name|change
return|;
block|}
block|}
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|updatedPatchSet
operator|.
name|isDraft
argument_list|()
operator|||
name|updatedChange
operator|.
name|getStatus
argument_list|()
operator|==
name|Change
operator|.
name|Status
operator|.
name|NEW
condition|)
block|{
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
name|updatedChange
argument_list|)
decl_stmt|;
name|hooks
operator|.
name|doDraftPublishedHook
argument_list|(
name|updatedChange
argument_list|,
name|updatedPatchSet
argument_list|,
name|db
argument_list|)
expr_stmt|;
name|sender
operator|.
name|send
argument_list|(
name|control
operator|.
name|getChange
argument_list|()
operator|.
name|getStatus
argument_list|()
operator|==
name|Change
operator|.
name|Status
operator|.
name|DRAFT
argument_list|,
operator|(
name|IdentifiedUser
operator|)
name|control
operator|.
name|getCurrentUser
argument_list|()
argument_list|,
name|updatedChange
argument_list|,
name|updatedPatchSet
argument_list|,
name|labelTypes
argument_list|)
expr_stmt|;
name|indexFuture
operator|.
name|checkedGet
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

