begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2008 The Android Open Source Project
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
DECL|package|com.google.gerrit.httpd.rpc.patch
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
name|patch
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
name|PatchDetailService
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
name|PatchScript
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
name|common
operator|.
name|errors
operator|.
name|NoSuchEntityException
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
name|httpd
operator|.
name|rpc
operator|.
name|BaseServiceImplementation
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
name|httpd
operator|.
name|rpc
operator|.
name|changedetail
operator|.
name|ChangeDetailFactory
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
name|AccountDiffPreference
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
name|Patch
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
name|PatchLineComment
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
name|changedetail
operator|.
name|DeleteDraftPatchSet
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
name|project
operator|.
name|NoSuchProjectException
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
name|errors
operator|.
name|RepositoryNotFoundException
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
DECL|class|PatchDetailServiceImpl
class|class
name|PatchDetailServiceImpl
extends|extends
name|BaseServiceImplementation
implements|implements
name|PatchDetailService
block|{
DECL|field|deleteDraftPatchSetFactory
specifier|private
specifier|final
name|DeleteDraftPatchSet
operator|.
name|Factory
name|deleteDraftPatchSetFactory
decl_stmt|;
DECL|field|patchScriptFactoryFactory
specifier|private
specifier|final
name|PatchScriptFactory
operator|.
name|Factory
name|patchScriptFactoryFactory
decl_stmt|;
DECL|field|saveDraftFactory
specifier|private
specifier|final
name|SaveDraft
operator|.
name|Factory
name|saveDraftFactory
decl_stmt|;
DECL|field|changeDetailFactory
specifier|private
specifier|final
name|ChangeDetailFactory
operator|.
name|Factory
name|changeDetailFactory
decl_stmt|;
annotation|@
name|Inject
DECL|method|PatchDetailServiceImpl (final Provider<ReviewDb> schema, final Provider<CurrentUser> currentUser, final DeleteDraftPatchSet.Factory deleteDraftPatchSetFactory, final PatchScriptFactory.Factory patchScriptFactoryFactory, final SaveDraft.Factory saveDraftFactory, final ChangeDetailFactory.Factory changeDetailFactory)
name|PatchDetailServiceImpl
parameter_list|(
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|schema
parameter_list|,
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|currentUser
parameter_list|,
specifier|final
name|DeleteDraftPatchSet
operator|.
name|Factory
name|deleteDraftPatchSetFactory
parameter_list|,
specifier|final
name|PatchScriptFactory
operator|.
name|Factory
name|patchScriptFactoryFactory
parameter_list|,
specifier|final
name|SaveDraft
operator|.
name|Factory
name|saveDraftFactory
parameter_list|,
specifier|final
name|ChangeDetailFactory
operator|.
name|Factory
name|changeDetailFactory
parameter_list|)
block|{
name|super
argument_list|(
name|schema
argument_list|,
name|currentUser
argument_list|)
expr_stmt|;
name|this
operator|.
name|deleteDraftPatchSetFactory
operator|=
name|deleteDraftPatchSetFactory
expr_stmt|;
name|this
operator|.
name|patchScriptFactoryFactory
operator|=
name|patchScriptFactoryFactory
expr_stmt|;
name|this
operator|.
name|saveDraftFactory
operator|=
name|saveDraftFactory
expr_stmt|;
name|this
operator|.
name|changeDetailFactory
operator|=
name|changeDetailFactory
expr_stmt|;
block|}
DECL|method|patchScript (final Patch.Key patchKey, final PatchSet.Id psa, final PatchSet.Id psb, final AccountDiffPreference dp, final AsyncCallback<PatchScript> callback)
specifier|public
name|void
name|patchScript
parameter_list|(
specifier|final
name|Patch
operator|.
name|Key
name|patchKey
parameter_list|,
specifier|final
name|PatchSet
operator|.
name|Id
name|psa
parameter_list|,
specifier|final
name|PatchSet
operator|.
name|Id
name|psb
parameter_list|,
specifier|final
name|AccountDiffPreference
name|dp
parameter_list|,
specifier|final
name|AsyncCallback
argument_list|<
name|PatchScript
argument_list|>
name|callback
parameter_list|)
block|{
if|if
condition|(
name|psb
operator|==
literal|null
condition|)
block|{
name|callback
operator|.
name|onFailure
argument_list|(
operator|new
name|NoSuchEntityException
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|patchScriptFactoryFactory
operator|.
name|create
argument_list|(
name|patchKey
argument_list|,
name|psa
argument_list|,
name|psb
argument_list|,
name|dp
argument_list|)
operator|.
name|to
argument_list|(
name|callback
argument_list|)
expr_stmt|;
block|}
DECL|method|saveDraft (final PatchLineComment comment, final AsyncCallback<PatchLineComment> callback)
specifier|public
name|void
name|saveDraft
parameter_list|(
specifier|final
name|PatchLineComment
name|comment
parameter_list|,
specifier|final
name|AsyncCallback
argument_list|<
name|PatchLineComment
argument_list|>
name|callback
parameter_list|)
block|{
name|saveDraftFactory
operator|.
name|create
argument_list|(
name|comment
argument_list|)
operator|.
name|to
argument_list|(
name|callback
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteDraft (final PatchLineComment.Key commentKey, final AsyncCallback<VoidResult> callback)
specifier|public
name|void
name|deleteDraft
parameter_list|(
specifier|final
name|PatchLineComment
operator|.
name|Key
name|commentKey
parameter_list|,
specifier|final
name|AsyncCallback
argument_list|<
name|VoidResult
argument_list|>
name|callback
parameter_list|)
block|{
name|run
argument_list|(
name|callback
argument_list|,
operator|new
name|Action
argument_list|<
name|VoidResult
argument_list|>
argument_list|()
block|{
specifier|public
name|VoidResult
name|run
parameter_list|(
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
throws|,
name|Failure
block|{
name|Change
operator|.
name|Id
name|id
init|=
name|commentKey
operator|.
name|getParentKey
argument_list|()
operator|.
name|getParentKey
argument_list|()
operator|.
name|getParentKey
argument_list|()
decl_stmt|;
name|db
operator|.
name|changes
argument_list|()
operator|.
name|beginTransaction
argument_list|(
name|id
argument_list|)
expr_stmt|;
try|try
block|{
specifier|final
name|PatchLineComment
name|comment
init|=
name|db
operator|.
name|patchComments
argument_list|()
operator|.
name|get
argument_list|(
name|commentKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|comment
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
operator|new
name|NoSuchEntityException
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|getAccountId
argument_list|()
operator|.
name|equals
argument_list|(
name|comment
operator|.
name|getAuthor
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
operator|new
name|NoSuchEntityException
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|comment
operator|.
name|getStatus
argument_list|()
operator|!=
name|PatchLineComment
operator|.
name|Status
operator|.
name|DRAFT
condition|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
operator|new
name|IllegalStateException
argument_list|(
literal|"Comment published"
argument_list|)
argument_list|)
throw|;
block|}
name|db
operator|.
name|patchComments
argument_list|()
operator|.
name|delete
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|comment
argument_list|)
argument_list|)
expr_stmt|;
name|db
operator|.
name|commit
argument_list|()
expr_stmt|;
return|return
name|VoidResult
operator|.
name|INSTANCE
return|;
block|}
finally|finally
block|{
name|db
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteDraftPatchSet (final PatchSet.Id psid, final AsyncCallback<ChangeDetail> callback)
specifier|public
name|void
name|deleteDraftPatchSet
parameter_list|(
specifier|final
name|PatchSet
operator|.
name|Id
name|psid
parameter_list|,
specifier|final
name|AsyncCallback
argument_list|<
name|ChangeDetail
argument_list|>
name|callback
parameter_list|)
block|{
name|run
argument_list|(
name|callback
argument_list|,
operator|new
name|Action
argument_list|<
name|ChangeDetail
argument_list|>
argument_list|()
block|{
specifier|public
name|ChangeDetail
name|run
parameter_list|(
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
throws|,
name|Failure
block|{
name|ReviewResult
name|result
init|=
literal|null
decl_stmt|;
try|try
block|{
name|result
operator|=
name|deleteDraftPatchSetFactory
operator|.
name|create
argument_list|(
name|psid
argument_list|)
operator|.
name|call
argument_list|()
expr_stmt|;
if|if
condition|(
name|result
operator|.
name|getErrors
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
operator|new
name|NoSuchEntityException
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|result
operator|.
name|getChangeId
argument_list|()
operator|==
literal|null
condition|)
block|{
comment|// the change was deleted because the draft patch set that was
comment|// deleted was the only patch set in the change
return|return
literal|null
return|;
block|}
return|return
name|changeDetailFactory
operator|.
name|create
argument_list|(
name|result
operator|.
name|getChangeId
argument_list|()
argument_list|)
operator|.
name|call
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchChangeException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
operator|new
name|NoSuchChangeException
argument_list|(
name|result
operator|.
name|getChangeId
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|NoSuchProjectException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|NoSuchEntityException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|PatchSetInfoNotAvailableException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|RepositoryNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

