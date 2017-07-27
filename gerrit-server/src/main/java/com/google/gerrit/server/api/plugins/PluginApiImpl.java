begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2017 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.api.plugins
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|api
operator|.
name|plugins
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
name|api
operator|.
name|plugins
operator|.
name|PluginApi
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
name|common
operator|.
name|PluginInfo
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
name|server
operator|.
name|plugins
operator|.
name|DisablePlugin
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
name|plugins
operator|.
name|EnablePlugin
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
name|plugins
operator|.
name|GetStatus
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
name|plugins
operator|.
name|PluginResource
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
name|plugins
operator|.
name|ReloadPlugin
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

begin_class
DECL|class|PluginApiImpl
specifier|public
class|class
name|PluginApiImpl
implements|implements
name|PluginApi
block|{
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (PluginResource resource)
name|PluginApiImpl
name|create
parameter_list|(
name|PluginResource
name|resource
parameter_list|)
function_decl|;
block|}
DECL|field|getStatus
specifier|private
specifier|final
name|GetStatus
name|getStatus
decl_stmt|;
DECL|field|enable
specifier|private
specifier|final
name|EnablePlugin
name|enable
decl_stmt|;
DECL|field|disable
specifier|private
specifier|final
name|DisablePlugin
name|disable
decl_stmt|;
DECL|field|reload
specifier|private
specifier|final
name|ReloadPlugin
name|reload
decl_stmt|;
DECL|field|resource
specifier|private
specifier|final
name|PluginResource
name|resource
decl_stmt|;
annotation|@
name|Inject
DECL|method|PluginApiImpl ( GetStatus getStatus, EnablePlugin enable, DisablePlugin disable, ReloadPlugin reload, @Assisted PluginResource resource)
name|PluginApiImpl
parameter_list|(
name|GetStatus
name|getStatus
parameter_list|,
name|EnablePlugin
name|enable
parameter_list|,
name|DisablePlugin
name|disable
parameter_list|,
name|ReloadPlugin
name|reload
parameter_list|,
annotation|@
name|Assisted
name|PluginResource
name|resource
parameter_list|)
block|{
name|this
operator|.
name|getStatus
operator|=
name|getStatus
expr_stmt|;
name|this
operator|.
name|enable
operator|=
name|enable
expr_stmt|;
name|this
operator|.
name|disable
operator|=
name|disable
expr_stmt|;
name|this
operator|.
name|reload
operator|=
name|reload
expr_stmt|;
name|this
operator|.
name|resource
operator|=
name|resource
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get ()
specifier|public
name|PluginInfo
name|get
parameter_list|()
throws|throws
name|RestApiException
block|{
return|return
name|getStatus
operator|.
name|apply
argument_list|(
name|resource
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|enable ()
specifier|public
name|void
name|enable
parameter_list|()
throws|throws
name|RestApiException
block|{
name|enable
operator|.
name|apply
argument_list|(
name|resource
argument_list|,
operator|new
name|EnablePlugin
operator|.
name|Input
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|disable ()
specifier|public
name|void
name|disable
parameter_list|()
throws|throws
name|RestApiException
block|{
name|disable
operator|.
name|apply
argument_list|(
name|resource
argument_list|,
operator|new
name|DisablePlugin
operator|.
name|Input
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reload ()
specifier|public
name|void
name|reload
parameter_list|()
throws|throws
name|RestApiException
block|{
name|reload
operator|.
name|apply
argument_list|(
name|resource
argument_list|,
operator|new
name|ReloadPlugin
operator|.
name|Input
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

