begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2009 The Android Open Source Project
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
DECL|package|com.google.gerrit.httpd.rpc.changedetail
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|httpd
operator|.
name|rpc
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
name|gerrit
operator|.
name|common
operator|.
name|data
operator|.
name|ChangeDetail
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
name|ChangeManageService
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
name|gwtjsonrpc
operator|.
name|common
operator|.
name|AsyncCallback
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtjsonrpc
operator|.
name|common
operator|.
name|VoidResult
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

begin_class
DECL|class|ChangeManageServiceImpl
class|class
name|ChangeManageServiceImpl
implements|implements
name|ChangeManageService
block|{
DECL|field|rebaseChangeFactory
specifier|private
specifier|final
name|RebaseChangeHandler
operator|.
name|Factory
name|rebaseChangeFactory
decl_stmt|;
DECL|field|publishAction
specifier|private
specifier|final
name|PublishAction
operator|.
name|Factory
name|publishAction
decl_stmt|;
DECL|field|deleteDraftChangeFactory
specifier|private
specifier|final
name|DeleteDraftChange
operator|.
name|Factory
name|deleteDraftChangeFactory
decl_stmt|;
annotation|@
name|Inject
DECL|method|ChangeManageServiceImpl ( final RebaseChangeHandler.Factory rebaseChangeFactory, final PublishAction.Factory publishAction, final DeleteDraftChange.Factory deleteDraftChangeFactory)
name|ChangeManageServiceImpl
parameter_list|(
specifier|final
name|RebaseChangeHandler
operator|.
name|Factory
name|rebaseChangeFactory
parameter_list|,
specifier|final
name|PublishAction
operator|.
name|Factory
name|publishAction
parameter_list|,
specifier|final
name|DeleteDraftChange
operator|.
name|Factory
name|deleteDraftChangeFactory
parameter_list|)
block|{
name|this
operator|.
name|rebaseChangeFactory
operator|=
name|rebaseChangeFactory
expr_stmt|;
name|this
operator|.
name|publishAction
operator|=
name|publishAction
expr_stmt|;
name|this
operator|.
name|deleteDraftChangeFactory
operator|=
name|deleteDraftChangeFactory
expr_stmt|;
block|}
DECL|method|rebaseChange (final PatchSet.Id patchSetId, final AsyncCallback<ChangeDetail> callback)
specifier|public
name|void
name|rebaseChange
parameter_list|(
specifier|final
name|PatchSet
operator|.
name|Id
name|patchSetId
parameter_list|,
specifier|final
name|AsyncCallback
argument_list|<
name|ChangeDetail
argument_list|>
name|callback
parameter_list|)
block|{
name|rebaseChangeFactory
operator|.
name|create
argument_list|(
name|patchSetId
argument_list|)
operator|.
name|to
argument_list|(
name|callback
argument_list|)
expr_stmt|;
block|}
DECL|method|publish (final PatchSet.Id patchSetId, final AsyncCallback<ChangeDetail> callback)
specifier|public
name|void
name|publish
parameter_list|(
specifier|final
name|PatchSet
operator|.
name|Id
name|patchSetId
parameter_list|,
specifier|final
name|AsyncCallback
argument_list|<
name|ChangeDetail
argument_list|>
name|callback
parameter_list|)
block|{
name|publishAction
operator|.
name|create
argument_list|(
name|patchSetId
argument_list|)
operator|.
name|to
argument_list|(
name|callback
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteDraftChange (final PatchSet.Id patchSetId, final AsyncCallback<VoidResult> callback)
specifier|public
name|void
name|deleteDraftChange
parameter_list|(
specifier|final
name|PatchSet
operator|.
name|Id
name|patchSetId
parameter_list|,
specifier|final
name|AsyncCallback
argument_list|<
name|VoidResult
argument_list|>
name|callback
parameter_list|)
block|{
name|deleteDraftChangeFactory
operator|.
name|create
argument_list|(
name|patchSetId
argument_list|)
operator|.
name|to
argument_list|(
name|callback
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

