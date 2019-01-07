begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2015 The Android Open Source Project
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
name|MethodNotAllowedException
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
name|server
operator|.
name|PatchSetUtil
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
name|StarredChangesUtil
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
name|AccountPatchReviewStore
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
name|ChangeDeleted
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
name|PluginItemContext
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
name|RepoContext
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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
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
name|ObjectId
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
name|revwalk
operator|.
name|RevWalk
import|;
end_import

begin_class
DECL|class|DeleteChangeOp
specifier|public
class|class
name|DeleteChangeOp
implements|implements
name|BatchUpdateOp
block|{
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (Change.Id id)
name|DeleteChangeOp
name|create
parameter_list|(
name|Change
operator|.
name|Id
name|id
parameter_list|)
function_decl|;
block|}
DECL|field|psUtil
specifier|private
specifier|final
name|PatchSetUtil
name|psUtil
decl_stmt|;
DECL|field|starredChangesUtil
specifier|private
specifier|final
name|StarredChangesUtil
name|starredChangesUtil
decl_stmt|;
DECL|field|accountPatchReviewStore
specifier|private
specifier|final
name|PluginItemContext
argument_list|<
name|AccountPatchReviewStore
argument_list|>
name|accountPatchReviewStore
decl_stmt|;
DECL|field|changeDeleted
specifier|private
specifier|final
name|ChangeDeleted
name|changeDeleted
decl_stmt|;
DECL|field|id
specifier|private
specifier|final
name|Change
operator|.
name|Id
name|id
decl_stmt|;
annotation|@
name|Inject
DECL|method|DeleteChangeOp ( PatchSetUtil psUtil, StarredChangesUtil starredChangesUtil, PluginItemContext<AccountPatchReviewStore> accountPatchReviewStore, ChangeDeleted changeDeleted, @Assisted Change.Id id)
name|DeleteChangeOp
parameter_list|(
name|PatchSetUtil
name|psUtil
parameter_list|,
name|StarredChangesUtil
name|starredChangesUtil
parameter_list|,
name|PluginItemContext
argument_list|<
name|AccountPatchReviewStore
argument_list|>
name|accountPatchReviewStore
parameter_list|,
name|ChangeDeleted
name|changeDeleted
parameter_list|,
annotation|@
name|Assisted
name|Change
operator|.
name|Id
name|id
parameter_list|)
block|{
name|this
operator|.
name|psUtil
operator|=
name|psUtil
expr_stmt|;
name|this
operator|.
name|starredChangesUtil
operator|=
name|starredChangesUtil
expr_stmt|;
name|this
operator|.
name|accountPatchReviewStore
operator|=
name|accountPatchReviewStore
expr_stmt|;
name|this
operator|.
name|changeDeleted
operator|=
name|changeDeleted
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
comment|// The relative order of updateChange and updateRepo doesn't matter as long as all operations are
comment|// executed in a single atomic BatchRefUpdate. Actually deleting the change refs first would not
comment|// fail gracefully if the second delete fails, but fortunately that's not what happens.
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
throws|,
name|OrmException
throws|,
name|IOException
block|{
name|Collection
argument_list|<
name|PatchSet
argument_list|>
name|patchSets
init|=
name|psUtil
operator|.
name|byChange
argument_list|(
name|ctx
operator|.
name|getNotes
argument_list|()
argument_list|)
decl_stmt|;
name|ensureDeletable
argument_list|(
name|ctx
argument_list|,
name|id
argument_list|,
name|patchSets
argument_list|)
expr_stmt|;
comment|// Cleaning up is only possible as long as the change and its elements are
comment|// still part of the database.
name|cleanUpReferences
argument_list|(
name|ctx
argument_list|,
name|id
argument_list|,
name|patchSets
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|deleteChange
argument_list|()
expr_stmt|;
name|changeDeleted
operator|.
name|fire
argument_list|(
name|ctx
operator|.
name|getChange
argument_list|()
argument_list|,
name|ctx
operator|.
name|getAccount
argument_list|()
argument_list|,
name|ctx
operator|.
name|getWhen
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|ensureDeletable (ChangeContext ctx, Change.Id id, Collection<PatchSet> patchSets)
specifier|private
name|void
name|ensureDeletable
parameter_list|(
name|ChangeContext
name|ctx
parameter_list|,
name|Change
operator|.
name|Id
name|id
parameter_list|,
name|Collection
argument_list|<
name|PatchSet
argument_list|>
name|patchSets
parameter_list|)
throws|throws
name|ResourceConflictException
throws|,
name|MethodNotAllowedException
throws|,
name|IOException
block|{
name|Change
operator|.
name|Status
name|status
init|=
name|ctx
operator|.
name|getChange
argument_list|()
operator|.
name|getStatus
argument_list|()
decl_stmt|;
if|if
condition|(
name|status
operator|==
name|Change
operator|.
name|Status
operator|.
name|MERGED
condition|)
block|{
throw|throw
operator|new
name|MethodNotAllowedException
argument_list|(
literal|"Deleting merged change "
operator|+
name|id
operator|+
literal|" is not allowed"
argument_list|)
throw|;
block|}
for|for
control|(
name|PatchSet
name|patchSet
range|:
name|patchSets
control|)
block|{
if|if
condition|(
name|isPatchSetMerged
argument_list|(
name|ctx
argument_list|,
name|patchSet
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Cannot delete change %s: patch set %s is already merged"
argument_list|,
name|id
argument_list|,
name|patchSet
operator|.
name|getPatchSetId
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|isPatchSetMerged (ChangeContext ctx, PatchSet patchSet)
specifier|private
name|boolean
name|isPatchSetMerged
parameter_list|(
name|ChangeContext
name|ctx
parameter_list|,
name|PatchSet
name|patchSet
parameter_list|)
throws|throws
name|IOException
block|{
name|Optional
argument_list|<
name|ObjectId
argument_list|>
name|destId
init|=
name|ctx
operator|.
name|getRepoView
argument_list|()
operator|.
name|getRef
argument_list|(
name|ctx
operator|.
name|getChange
argument_list|()
operator|.
name|getDest
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|destId
operator|.
name|isPresent
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|RevWalk
name|revWalk
init|=
name|ctx
operator|.
name|getRevWalk
argument_list|()
decl_stmt|;
name|ObjectId
name|objectId
init|=
name|ObjectId
operator|.
name|fromString
argument_list|(
name|patchSet
operator|.
name|getRevision
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|revWalk
operator|.
name|isMergedInto
argument_list|(
name|revWalk
operator|.
name|parseCommit
argument_list|(
name|objectId
argument_list|)
argument_list|,
name|revWalk
operator|.
name|parseCommit
argument_list|(
name|destId
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|cleanUpReferences (ChangeContext ctx, Change.Id id, Collection<PatchSet> patchSets)
specifier|private
name|void
name|cleanUpReferences
parameter_list|(
name|ChangeContext
name|ctx
parameter_list|,
name|Change
operator|.
name|Id
name|id
parameter_list|,
name|Collection
argument_list|<
name|PatchSet
argument_list|>
name|patchSets
parameter_list|)
throws|throws
name|OrmException
throws|,
name|NoSuchChangeException
block|{
for|for
control|(
name|PatchSet
name|ps
range|:
name|patchSets
control|)
block|{
name|accountPatchReviewStore
operator|.
name|run
argument_list|(
name|s
lambda|->
name|s
operator|.
name|clearReviewed
argument_list|(
name|ps
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|,
name|OrmException
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|// Non-atomic operation on Accounts table; not much we can do to make it
comment|// atomic.
name|starredChangesUtil
operator|.
name|unstarAll
argument_list|(
name|ctx
operator|.
name|getChange
argument_list|()
operator|.
name|getProject
argument_list|()
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|updateRepo (RepoContext ctx)
specifier|public
name|void
name|updateRepo
parameter_list|(
name|RepoContext
name|ctx
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|prefix
init|=
operator|new
name|PatchSet
operator|.
name|Id
argument_list|(
name|id
argument_list|,
literal|1
argument_list|)
operator|.
name|toRefName
argument_list|()
decl_stmt|;
name|prefix
operator|=
name|prefix
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|prefix
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|ObjectId
argument_list|>
name|e
range|:
name|ctx
operator|.
name|getRepoView
argument_list|()
operator|.
name|getRefs
argument_list|(
name|prefix
argument_list|)
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|ctx
operator|.
name|addRefUpdate
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|,
name|ObjectId
operator|.
name|zeroId
argument_list|()
argument_list|,
name|prefix
operator|+
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

