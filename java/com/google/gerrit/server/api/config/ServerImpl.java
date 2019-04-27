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
DECL|package|com.google.gerrit.server.api.config
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
name|config
package|;
end_package

begin_import
import|import static
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
name|ApiUtil
operator|.
name|asRestApiException
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
name|Version
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
name|api
operator|.
name|config
operator|.
name|ConsistencyCheckInfo
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
name|api
operator|.
name|config
operator|.
name|ConsistencyCheckInput
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
name|api
operator|.
name|config
operator|.
name|Server
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
name|client
operator|.
name|DiffPreferencesInfo
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
name|client
operator|.
name|EditPreferencesInfo
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
name|client
operator|.
name|GeneralPreferencesInfo
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
name|ServerInfo
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
name|extensions
operator|.
name|webui
operator|.
name|TopMenu
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
name|ConfigResource
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
name|restapi
operator|.
name|config
operator|.
name|CheckConsistency
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
name|restapi
operator|.
name|config
operator|.
name|GetDiffPreferences
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
name|restapi
operator|.
name|config
operator|.
name|GetEditPreferences
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
name|restapi
operator|.
name|config
operator|.
name|GetPreferences
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
name|restapi
operator|.
name|config
operator|.
name|GetServerInfo
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
name|restapi
operator|.
name|config
operator|.
name|ListTopMenus
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
name|restapi
operator|.
name|config
operator|.
name|SetDiffPreferences
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
name|restapi
operator|.
name|config
operator|.
name|SetEditPreferences
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
name|restapi
operator|.
name|config
operator|.
name|SetPreferences
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Singleton
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
annotation|@
name|Singleton
DECL|class|ServerImpl
specifier|public
class|class
name|ServerImpl
implements|implements
name|Server
block|{
DECL|field|getPreferences
specifier|private
specifier|final
name|GetPreferences
name|getPreferences
decl_stmt|;
DECL|field|setPreferences
specifier|private
specifier|final
name|SetPreferences
name|setPreferences
decl_stmt|;
DECL|field|getDiffPreferences
specifier|private
specifier|final
name|GetDiffPreferences
name|getDiffPreferences
decl_stmt|;
DECL|field|setDiffPreferences
specifier|private
specifier|final
name|SetDiffPreferences
name|setDiffPreferences
decl_stmt|;
DECL|field|getEditPreferences
specifier|private
specifier|final
name|GetEditPreferences
name|getEditPreferences
decl_stmt|;
DECL|field|setEditPreferences
specifier|private
specifier|final
name|SetEditPreferences
name|setEditPreferences
decl_stmt|;
DECL|field|getServerInfo
specifier|private
specifier|final
name|GetServerInfo
name|getServerInfo
decl_stmt|;
DECL|field|checkConsistency
specifier|private
specifier|final
name|Provider
argument_list|<
name|CheckConsistency
argument_list|>
name|checkConsistency
decl_stmt|;
DECL|field|listTopMenus
specifier|private
specifier|final
name|ListTopMenus
name|listTopMenus
decl_stmt|;
annotation|@
name|Inject
DECL|method|ServerImpl ( GetPreferences getPreferences, SetPreferences setPreferences, GetDiffPreferences getDiffPreferences, SetDiffPreferences setDiffPreferences, GetEditPreferences getEditPreferences, SetEditPreferences setEditPreferences, GetServerInfo getServerInfo, Provider<CheckConsistency> checkConsistency, ListTopMenus listTopMenus)
name|ServerImpl
parameter_list|(
name|GetPreferences
name|getPreferences
parameter_list|,
name|SetPreferences
name|setPreferences
parameter_list|,
name|GetDiffPreferences
name|getDiffPreferences
parameter_list|,
name|SetDiffPreferences
name|setDiffPreferences
parameter_list|,
name|GetEditPreferences
name|getEditPreferences
parameter_list|,
name|SetEditPreferences
name|setEditPreferences
parameter_list|,
name|GetServerInfo
name|getServerInfo
parameter_list|,
name|Provider
argument_list|<
name|CheckConsistency
argument_list|>
name|checkConsistency
parameter_list|,
name|ListTopMenus
name|listTopMenus
parameter_list|)
block|{
name|this
operator|.
name|getPreferences
operator|=
name|getPreferences
expr_stmt|;
name|this
operator|.
name|setPreferences
operator|=
name|setPreferences
expr_stmt|;
name|this
operator|.
name|getDiffPreferences
operator|=
name|getDiffPreferences
expr_stmt|;
name|this
operator|.
name|setDiffPreferences
operator|=
name|setDiffPreferences
expr_stmt|;
name|this
operator|.
name|getEditPreferences
operator|=
name|getEditPreferences
expr_stmt|;
name|this
operator|.
name|setEditPreferences
operator|=
name|setEditPreferences
expr_stmt|;
name|this
operator|.
name|getServerInfo
operator|=
name|getServerInfo
expr_stmt|;
name|this
operator|.
name|checkConsistency
operator|=
name|checkConsistency
expr_stmt|;
name|this
operator|.
name|listTopMenus
operator|=
name|listTopMenus
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getVersion ()
specifier|public
name|String
name|getVersion
parameter_list|()
throws|throws
name|RestApiException
block|{
return|return
name|Version
operator|.
name|getVersion
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getInfo ()
specifier|public
name|ServerInfo
name|getInfo
parameter_list|()
throws|throws
name|RestApiException
block|{
try|try
block|{
return|return
name|getServerInfo
operator|.
name|apply
argument_list|(
operator|new
name|ConfigResource
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|asRestApiException
argument_list|(
literal|"Cannot get server info"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getDefaultPreferences ()
specifier|public
name|GeneralPreferencesInfo
name|getDefaultPreferences
parameter_list|()
throws|throws
name|RestApiException
block|{
try|try
block|{
return|return
name|getPreferences
operator|.
name|apply
argument_list|(
operator|new
name|ConfigResource
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|asRestApiException
argument_list|(
literal|"Cannot get default general preferences"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|setDefaultPreferences (GeneralPreferencesInfo in)
specifier|public
name|GeneralPreferencesInfo
name|setDefaultPreferences
parameter_list|(
name|GeneralPreferencesInfo
name|in
parameter_list|)
throws|throws
name|RestApiException
block|{
try|try
block|{
return|return
name|setPreferences
operator|.
name|apply
argument_list|(
operator|new
name|ConfigResource
argument_list|()
argument_list|,
name|in
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|asRestApiException
argument_list|(
literal|"Cannot set default general preferences"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getDefaultDiffPreferences ()
specifier|public
name|DiffPreferencesInfo
name|getDefaultDiffPreferences
parameter_list|()
throws|throws
name|RestApiException
block|{
try|try
block|{
return|return
name|getDiffPreferences
operator|.
name|apply
argument_list|(
operator|new
name|ConfigResource
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|asRestApiException
argument_list|(
literal|"Cannot get default diff preferences"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|setDefaultDiffPreferences (DiffPreferencesInfo in)
specifier|public
name|DiffPreferencesInfo
name|setDefaultDiffPreferences
parameter_list|(
name|DiffPreferencesInfo
name|in
parameter_list|)
throws|throws
name|RestApiException
block|{
try|try
block|{
return|return
name|setDiffPreferences
operator|.
name|apply
argument_list|(
operator|new
name|ConfigResource
argument_list|()
argument_list|,
name|in
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|asRestApiException
argument_list|(
literal|"Cannot set default diff preferences"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getDefaultEditPreferences ()
specifier|public
name|EditPreferencesInfo
name|getDefaultEditPreferences
parameter_list|()
throws|throws
name|RestApiException
block|{
try|try
block|{
return|return
name|getEditPreferences
operator|.
name|apply
argument_list|(
operator|new
name|ConfigResource
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|asRestApiException
argument_list|(
literal|"Cannot get default edit preferences"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|setDefaultEditPreferences (EditPreferencesInfo in)
specifier|public
name|EditPreferencesInfo
name|setDefaultEditPreferences
parameter_list|(
name|EditPreferencesInfo
name|in
parameter_list|)
throws|throws
name|RestApiException
block|{
try|try
block|{
return|return
name|setEditPreferences
operator|.
name|apply
argument_list|(
operator|new
name|ConfigResource
argument_list|()
argument_list|,
name|in
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|asRestApiException
argument_list|(
literal|"Cannot set default edit preferences"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|checkConsistency (ConsistencyCheckInput in)
specifier|public
name|ConsistencyCheckInfo
name|checkConsistency
parameter_list|(
name|ConsistencyCheckInput
name|in
parameter_list|)
throws|throws
name|RestApiException
block|{
try|try
block|{
return|return
name|checkConsistency
operator|.
name|get
argument_list|()
operator|.
name|apply
argument_list|(
operator|new
name|ConfigResource
argument_list|()
argument_list|,
name|in
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|asRestApiException
argument_list|(
literal|"Cannot check consistency"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|topMenus ()
specifier|public
name|List
argument_list|<
name|TopMenu
operator|.
name|MenuEntry
argument_list|>
name|topMenus
parameter_list|()
block|{
return|return
name|listTopMenus
operator|.
name|apply
argument_list|(
operator|new
name|ConfigResource
argument_list|()
argument_list|)
operator|.
name|value
argument_list|()
return|;
block|}
block|}
end_class

end_unit

