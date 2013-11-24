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
DECL|package|com.google.gerrit.common.data
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|common
operator|.
name|data
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
name|audit
operator|.
name|Audit
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
name|auth
operator|.
name|SignInRequired
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
name|RemoteJsonService
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
name|RpcImpl
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
name|RpcImpl
operator|.
name|Version
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

begin_interface
annotation|@
name|RpcImpl
argument_list|(
name|version
operator|=
name|Version
operator|.
name|V2_0
argument_list|)
DECL|interface|PatchDetailService
specifier|public
interface|interface
name|PatchDetailService
extends|extends
name|RemoteJsonService
block|{
annotation|@
name|Audit
DECL|method|patchScript (Patch.Key key, PatchSet.Id a, PatchSet.Id b, AccountDiffPreference diffPrefs, AsyncCallback<PatchScript> callback)
name|void
name|patchScript
parameter_list|(
name|Patch
operator|.
name|Key
name|key
parameter_list|,
name|PatchSet
operator|.
name|Id
name|a
parameter_list|,
name|PatchSet
operator|.
name|Id
name|b
parameter_list|,
name|AccountDiffPreference
name|diffPrefs
parameter_list|,
name|AsyncCallback
argument_list|<
name|PatchScript
argument_list|>
name|callback
parameter_list|)
function_decl|;
annotation|@
name|Audit
annotation|@
name|SignInRequired
DECL|method|saveDraft (PatchLineComment comment, AsyncCallback<PatchLineComment> callback)
name|void
name|saveDraft
parameter_list|(
name|PatchLineComment
name|comment
parameter_list|,
name|AsyncCallback
argument_list|<
name|PatchLineComment
argument_list|>
name|callback
parameter_list|)
function_decl|;
annotation|@
name|Audit
annotation|@
name|SignInRequired
DECL|method|deleteDraft (PatchLineComment.Key key, AsyncCallback<VoidResult> callback)
name|void
name|deleteDraft
parameter_list|(
name|PatchLineComment
operator|.
name|Key
name|key
parameter_list|,
name|AsyncCallback
argument_list|<
name|VoidResult
argument_list|>
name|callback
parameter_list|)
function_decl|;
comment|/**    * Deletes the specified draft patch set. If the draft patch set is the only    * patch set of the change, then also the change gets deleted.    *    * @param psid ID of the draft patch set that should be deleted    * @param callback callback to report the result of the draft patch set    *        deletion operation; if the draft patch set was successfully deleted    *        {@link AsyncCallback#onSuccess(Object)} is invoked and the change    *        details are passed as parameter; if the change gets deleted because    *        the draft patch set that was deleted was the only patch set in the    *        change, then {@code null} is passed as result to    *        {@link AsyncCallback#onSuccess(Object)}    */
annotation|@
name|Audit
annotation|@
name|SignInRequired
DECL|method|deleteDraftPatchSet (PatchSet.Id psid, AsyncCallback<ChangeDetail> callback)
name|void
name|deleteDraftPatchSet
parameter_list|(
name|PatchSet
operator|.
name|Id
name|psid
parameter_list|,
name|AsyncCallback
argument_list|<
name|ChangeDetail
argument_list|>
name|callback
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

