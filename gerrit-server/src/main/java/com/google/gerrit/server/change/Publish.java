begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2013 The Android Open Source Project
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
name|ResourceNotFoundException
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
name|change
operator|.
name|Publish
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
name|config
operator|.
name|GerritServerConfig
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
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Config
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
DECL|class|Publish
specifier|public
class|class
name|Publish
implements|implements
name|RestModifyView
argument_list|<
name|RevisionResource
argument_list|,
name|Input
argument_list|>
implements|,
name|UiAction
argument_list|<
name|RevisionResource
argument_list|>
block|{
DECL|class|Input
specifier|public
specifier|static
class|class
name|Input
block|{   }
DECL|field|dbProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|dbProvider
decl_stmt|;
DECL|field|updateFactory
specifier|private
specifier|final
name|ChangeUpdate
operator|.
name|Factory
name|updateFactory
decl_stmt|;
DECL|field|sender
specifier|private
specifier|final
name|PatchSetNotificationSender
name|sender
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
DECL|field|allowDrafts
specifier|private
specifier|final
name|boolean
name|allowDrafts
decl_stmt|;
annotation|@
name|Inject
DECL|method|Publish (Provider<ReviewDb> dbProvider, ChangeUpdate.Factory updateFactory, PatchSetNotificationSender sender, ChangeHooks hooks, ChangeIndexer indexer, @GerritServerConfig Config cfg)
specifier|public
name|Publish
parameter_list|(
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|dbProvider
parameter_list|,
name|ChangeUpdate
operator|.
name|Factory
name|updateFactory
parameter_list|,
name|PatchSetNotificationSender
name|sender
parameter_list|,
name|ChangeHooks
name|hooks
parameter_list|,
name|ChangeIndexer
name|indexer
parameter_list|,
annotation|@
name|GerritServerConfig
name|Config
name|cfg
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
name|updateFactory
operator|=
name|updateFactory
expr_stmt|;
name|this
operator|.
name|sender
operator|=
name|sender
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
name|allowDrafts
operator|=
name|cfg
operator|.
name|getBoolean
argument_list|(
literal|"change"
argument_list|,
literal|"allowDrafts"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (RevisionResource rsrc, Input input)
specifier|public
name|Response
argument_list|<
name|?
argument_list|>
name|apply
parameter_list|(
name|RevisionResource
name|rsrc
parameter_list|,
name|Input
name|input
parameter_list|)
throws|throws
name|AuthException
throws|,
name|ResourceNotFoundException
throws|,
name|ResourceConflictException
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
name|getPatchSet
argument_list|()
operator|.
name|isDraft
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
literal|"Patch set is not a draft"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|rsrc
operator|.
name|getControl
argument_list|()
operator|.
name|canPublish
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
literal|"Cannot publish this draft patch set"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|allowDrafts
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
literal|"Draft workflow is disabled."
argument_list|)
throw|;
block|}
name|PatchSet
name|updatedPatchSet
init|=
name|updateDraftPatchSet
argument_list|(
name|rsrc
argument_list|)
decl_stmt|;
name|Change
name|updatedChange
init|=
name|updateDraftChange
argument_list|(
name|rsrc
argument_list|)
decl_stmt|;
name|ChangeUpdate
name|update
init|=
name|updateFactory
operator|.
name|create
argument_list|(
name|rsrc
operator|.
name|getControl
argument_list|()
argument_list|,
name|updatedChange
operator|.
name|getLastUpdatedOn
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
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
operator|.
name|getId
argument_list|()
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
name|dbProvider
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|sender
operator|.
name|send
argument_list|(
name|rsrc
operator|.
name|getNotes
argument_list|()
argument_list|,
name|update
argument_list|,
name|rsrc
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
name|rsrc
operator|.
name|getUser
argument_list|()
argument_list|,
name|updatedChange
argument_list|,
name|updatedPatchSet
argument_list|,
name|rsrc
operator|.
name|getControl
argument_list|()
operator|.
name|getLabelTypes
argument_list|()
argument_list|)
expr_stmt|;
name|indexFuture
operator|.
name|checkedGet
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|PatchSetInfoNotAvailableException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|Response
operator|.
name|none
argument_list|()
return|;
block|}
DECL|method|updateDraftChange (RevisionResource rsrc)
specifier|private
name|Change
name|updateDraftChange
parameter_list|(
name|RevisionResource
name|rsrc
parameter_list|)
throws|throws
name|OrmException
block|{
return|return
name|dbProvider
operator|.
name|get
argument_list|()
operator|.
name|changes
argument_list|()
operator|.
name|atomicUpdate
argument_list|(
name|rsrc
operator|.
name|getChange
argument_list|()
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
return|;
block|}
DECL|method|updateDraftPatchSet (RevisionResource rsrc)
specifier|private
name|PatchSet
name|updateDraftPatchSet
parameter_list|(
name|RevisionResource
name|rsrc
parameter_list|)
throws|throws
name|OrmException
block|{
return|return
name|dbProvider
operator|.
name|get
argument_list|()
operator|.
name|patchSets
argument_list|()
operator|.
name|atomicUpdate
argument_list|(
name|rsrc
operator|.
name|getPatchSet
argument_list|()
operator|.
name|getId
argument_list|()
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
return|;
block|}
annotation|@
name|Override
DECL|method|getDescription (RevisionResource rsrc)
specifier|public
name|UiAction
operator|.
name|Description
name|getDescription
parameter_list|(
name|RevisionResource
name|rsrc
parameter_list|)
block|{
try|try
block|{
return|return
operator|new
name|UiAction
operator|.
name|Description
argument_list|()
operator|.
name|setTitle
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Publish revision %d"
argument_list|,
name|rsrc
operator|.
name|getPatchSet
argument_list|()
operator|.
name|getPatchSetId
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setVisible
argument_list|(
name|rsrc
operator|.
name|getPatchSet
argument_list|()
operator|.
name|isDraft
argument_list|()
operator|&&
name|rsrc
operator|.
name|getControl
argument_list|()
operator|.
name|canPublish
argument_list|(
name|dbProvider
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|class|CurrentRevision
specifier|public
specifier|static
class|class
name|CurrentRevision
implements|implements
name|RestModifyView
argument_list|<
name|ChangeResource
argument_list|,
name|Input
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
DECL|field|publish
specifier|private
specifier|final
name|Publish
name|publish
decl_stmt|;
annotation|@
name|Inject
DECL|method|CurrentRevision (Provider<ReviewDb> dbProvider, Publish publish)
name|CurrentRevision
parameter_list|(
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|dbProvider
parameter_list|,
name|Publish
name|publish
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
name|publish
operator|=
name|publish
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (ChangeResource rsrc, Input input)
specifier|public
name|Response
argument_list|<
name|?
argument_list|>
name|apply
parameter_list|(
name|ChangeResource
name|rsrc
parameter_list|,
name|Input
name|input
parameter_list|)
throws|throws
name|AuthException
throws|,
name|ResourceConflictException
throws|,
name|ResourceNotFoundException
throws|,
name|IOException
throws|,
name|OrmException
block|{
name|PatchSet
name|ps
init|=
name|dbProvider
operator|.
name|get
argument_list|()
operator|.
name|patchSets
argument_list|()
operator|.
name|get
argument_list|(
name|rsrc
operator|.
name|getChange
argument_list|()
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|ps
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
literal|"current revision is missing"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
operator|!
name|rsrc
operator|.
name|getControl
argument_list|()
operator|.
name|isPatchVisible
argument_list|(
name|ps
argument_list|,
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
literal|"current revision not accessible"
argument_list|)
throw|;
block|}
return|return
name|publish
operator|.
name|apply
argument_list|(
operator|new
name|RevisionResource
argument_list|(
name|rsrc
argument_list|,
name|ps
argument_list|)
argument_list|,
name|input
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

