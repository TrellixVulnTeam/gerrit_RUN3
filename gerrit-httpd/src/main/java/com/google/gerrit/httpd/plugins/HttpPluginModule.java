begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2012 The Android Open Source Project
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
DECL|package|com.google.gerrit.httpd.plugins
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|httpd
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
name|httpd
operator|.
name|resources
operator|.
name|Resource
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
name|resources
operator|.
name|ResourceKey
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
name|resources
operator|.
name|ResourceWeigher
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
name|cache
operator|.
name|CacheModule
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
name|HttpModuleGenerator
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
name|ReloadPluginListener
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
name|StartPluginListener
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
name|internal
operator|.
name|UniqueAnnotations
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
name|servlet
operator|.
name|ServletModule
import|;
end_import

begin_class
DECL|class|HttpPluginModule
specifier|public
class|class
name|HttpPluginModule
extends|extends
name|ServletModule
block|{
DECL|field|PLUGIN_RESOURCES
specifier|static
specifier|final
name|String
name|PLUGIN_RESOURCES
init|=
literal|"plugin_resources"
decl_stmt|;
annotation|@
name|Override
DECL|method|configureServlets ()
specifier|protected
name|void
name|configureServlets
parameter_list|()
block|{
name|bind
argument_list|(
name|HttpPluginServlet
operator|.
name|class
argument_list|)
expr_stmt|;
name|serveRegex
argument_list|(
literal|"^/(?:a/)?plugins/(.*)?$"
argument_list|)
operator|.
name|with
argument_list|(
name|HttpPluginServlet
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|StartPluginListener
operator|.
name|class
argument_list|)
operator|.
name|annotatedWith
argument_list|(
name|UniqueAnnotations
operator|.
name|create
argument_list|()
argument_list|)
operator|.
name|to
argument_list|(
name|HttpPluginServlet
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|ReloadPluginListener
operator|.
name|class
argument_list|)
operator|.
name|annotatedWith
argument_list|(
name|UniqueAnnotations
operator|.
name|create
argument_list|()
argument_list|)
operator|.
name|to
argument_list|(
name|HttpPluginServlet
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|HttpModuleGenerator
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|HttpAutoRegisterModuleGenerator
operator|.
name|class
argument_list|)
expr_stmt|;
name|install
argument_list|(
operator|new
name|CacheModule
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|cache
argument_list|(
name|PLUGIN_RESOURCES
argument_list|,
name|ResourceKey
operator|.
name|class
argument_list|,
name|Resource
operator|.
name|class
argument_list|)
operator|.
name|maximumWeight
argument_list|(
literal|2
operator|<<
literal|20
argument_list|)
operator|.
name|weigher
argument_list|(
name|ResourceWeigher
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

