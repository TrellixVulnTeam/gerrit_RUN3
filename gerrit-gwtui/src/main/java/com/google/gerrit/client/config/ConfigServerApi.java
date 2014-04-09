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
DECL|package|com.google.gerrit.client.config
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|config
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
name|account
operator|.
name|Preferences
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
name|extensions
operator|.
name|TopMenuList
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
name|rpc
operator|.
name|NativeMap
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
name|rpc
operator|.
name|RestApi
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

begin_comment
comment|/**  * A collection of static methods which work on the Gerrit REST API for server  * configuration.  */
end_comment

begin_class
DECL|class|ConfigServerApi
specifier|public
class|class
name|ConfigServerApi
block|{
comment|/** map of the server wide capabilities (core& plugins). */
DECL|method|capabilities (AsyncCallback<NativeMap<CapabilityInfo>> cb)
specifier|public
specifier|static
name|void
name|capabilities
parameter_list|(
name|AsyncCallback
argument_list|<
name|NativeMap
argument_list|<
name|CapabilityInfo
argument_list|>
argument_list|>
name|cb
parameter_list|)
block|{
operator|new
name|RestApi
argument_list|(
literal|"/config/server/capabilities/"
argument_list|)
operator|.
name|get
argument_list|(
name|cb
argument_list|)
expr_stmt|;
block|}
DECL|method|topMenus (AsyncCallback<TopMenuList> cb)
specifier|public
specifier|static
name|void
name|topMenus
parameter_list|(
name|AsyncCallback
argument_list|<
name|TopMenuList
argument_list|>
name|cb
parameter_list|)
block|{
operator|new
name|RestApi
argument_list|(
literal|"/config/server/top-menus"
argument_list|)
operator|.
name|get
argument_list|(
name|cb
argument_list|)
expr_stmt|;
block|}
DECL|method|defaultPreferences (AsyncCallback<Preferences> cb)
specifier|public
specifier|static
name|void
name|defaultPreferences
parameter_list|(
name|AsyncCallback
argument_list|<
name|Preferences
argument_list|>
name|cb
parameter_list|)
block|{
operator|new
name|RestApi
argument_list|(
literal|"/config/server/preferences"
argument_list|)
operator|.
name|get
argument_list|(
name|cb
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

