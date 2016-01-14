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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkState
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
name|lib
operator|.
name|Ref
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
name|transport
operator|.
name|ReceiveCommand
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_class
DECL|class|DeleteDraftChangeOp
class|class
name|DeleteDraftChangeOp
extends|extends
name|BatchUpdate
operator|.
name|Op
block|{
DECL|method|allowDrafts (Config cfg)
specifier|static
name|boolean
name|allowDrafts
parameter_list|(
name|Config
name|cfg
parameter_list|)
block|{
return|return
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
return|;
block|}
DECL|field|starredChangesUtil
specifier|private
specifier|final
name|StarredChangesUtil
name|starredChangesUtil
decl_stmt|;
DECL|field|allowDrafts
specifier|private
specifier|final
name|boolean
name|allowDrafts
decl_stmt|;
DECL|field|id
specifier|private
name|Change
operator|.
name|Id
name|id
decl_stmt|;
annotation|@
name|Inject
DECL|method|DeleteDraftChangeOp (StarredChangesUtil starredChangesUtil, @GerritServerConfig Config cfg)
name|DeleteDraftChangeOp
parameter_list|(
name|StarredChangesUtil
name|starredChangesUtil
parameter_list|,
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|)
block|{
name|this
operator|.
name|starredChangesUtil
operator|=
name|starredChangesUtil
expr_stmt|;
name|this
operator|.
name|allowDrafts
operator|=
name|allowDrafts
argument_list|(
name|cfg
argument_list|)
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
name|RestApiException
throws|,
name|OrmException
block|{
name|checkState
argument_list|(
name|ctx
operator|.
name|getOrder
argument_list|()
operator|==
name|BatchUpdate
operator|.
name|Order
operator|.
name|DB_BEFORE_REPO
argument_list|,
literal|"must use DeleteDraftChangeOp with DB_BEFORE_REPO"
argument_list|)
expr_stmt|;
name|checkState
argument_list|(
name|id
operator|==
literal|null
argument_list|,
literal|"cannot reuse DeleteDraftChangeOp"
argument_list|)
expr_stmt|;
name|Change
name|change
init|=
name|ctx
operator|.
name|getChange
argument_list|()
decl_stmt|;
name|id
operator|=
name|change
operator|.
name|getId
argument_list|()
expr_stmt|;
name|ReviewDb
name|db
init|=
name|ctx
operator|.
name|getDb
argument_list|()
decl_stmt|;
if|if
condition|(
name|change
operator|.
name|getStatus
argument_list|()
operator|!=
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
literal|"Change is not a draft: "
operator|+
name|id
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
name|MethodNotAllowedException
argument_list|(
literal|"Draft workflow is disabled"
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
name|canDeleteDraft
argument_list|(
name|ctx
operator|.
name|getDb
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AuthException
argument_list|(
literal|"Not permitted to delete this draft change"
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|PatchSet
argument_list|>
name|patchSets
init|=
name|ctx
operator|.
name|getDb
argument_list|()
operator|.
name|patchSets
argument_list|()
operator|.
name|byChange
argument_list|(
name|id
argument_list|)
operator|.
name|toList
argument_list|()
decl_stmt|;
for|for
control|(
name|PatchSet
name|ps
range|:
name|patchSets
control|)
block|{
if|if
condition|(
operator|!
name|ps
operator|.
name|isDraft
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
literal|"Cannot delete draft change "
operator|+
name|id
operator|+
literal|": patch set "
operator|+
name|ps
operator|.
name|getPatchSetId
argument_list|()
operator|+
literal|" is not a draft"
argument_list|)
throw|;
block|}
name|db
operator|.
name|accountPatchReviews
argument_list|()
operator|.
name|delete
argument_list|(
name|db
operator|.
name|accountPatchReviews
argument_list|()
operator|.
name|byPatchSet
argument_list|(
name|ps
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// No need to delete from notedb; draft patch sets will be filtered out.
name|db
operator|.
name|patchComments
argument_list|()
operator|.
name|delete
argument_list|(
name|db
operator|.
name|patchComments
argument_list|()
operator|.
name|byChange
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|db
operator|.
name|patchSetApprovals
argument_list|()
operator|.
name|delete
argument_list|(
name|db
operator|.
name|patchSetApprovals
argument_list|()
operator|.
name|byChange
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|delete
argument_list|(
name|patchSets
argument_list|)
expr_stmt|;
name|db
operator|.
name|changeMessages
argument_list|()
operator|.
name|delete
argument_list|(
name|db
operator|.
name|changeMessages
argument_list|()
operator|.
name|byChange
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|starredChangesUtil
operator|.
name|unstarAll
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|db
operator|.
name|changes
argument_list|()
operator|.
name|delete
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|change
argument_list|)
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|markDeleted
argument_list|()
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
name|Ref
name|ref
range|:
name|ctx
operator|.
name|getRepository
argument_list|()
operator|.
name|getRefDatabase
argument_list|()
operator|.
name|getRefs
argument_list|(
name|prefix
argument_list|)
operator|.
name|values
argument_list|()
control|)
block|{
name|ctx
operator|.
name|addRefUpdate
argument_list|(
operator|new
name|ReceiveCommand
argument_list|(
name|ref
operator|.
name|getObjectId
argument_list|()
argument_list|,
name|ObjectId
operator|.
name|zeroId
argument_list|()
argument_list|,
name|ref
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

