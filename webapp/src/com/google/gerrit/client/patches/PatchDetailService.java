begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright 2008 Google Inc.
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
DECL|package|com.google.gerrit.client.patches
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|patches
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
name|client
operator|.
name|data
operator|.
name|SideBySidePatchDetail
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
name|client
operator|.
name|data
operator|.
name|UnifiedPatchDetail
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
name|client
operator|.
name|reviewdb
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
name|gwt
operator|.
name|user
operator|.
name|client
operator|.
name|rpc
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
name|client
operator|.
name|AllowCrossSiteRequest
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
name|client
operator|.
name|RemoteJsonService
import|;
end_import

begin_interface
DECL|interface|PatchDetailService
specifier|public
interface|interface
name|PatchDetailService
extends|extends
name|RemoteJsonService
block|{
annotation|@
name|AllowCrossSiteRequest
DECL|method|sideBySidePatchDetail (Patch.Id key, AsyncCallback<SideBySidePatchDetail> callback)
name|void
name|sideBySidePatchDetail
parameter_list|(
name|Patch
operator|.
name|Id
name|key
parameter_list|,
name|AsyncCallback
argument_list|<
name|SideBySidePatchDetail
argument_list|>
name|callback
parameter_list|)
function_decl|;
annotation|@
name|AllowCrossSiteRequest
DECL|method|unifiedPatchDetail (Patch.Id key, AsyncCallback<UnifiedPatchDetail> callback)
name|void
name|unifiedPatchDetail
parameter_list|(
name|Patch
operator|.
name|Id
name|key
parameter_list|,
name|AsyncCallback
argument_list|<
name|UnifiedPatchDetail
argument_list|>
name|callback
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

