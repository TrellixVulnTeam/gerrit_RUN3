begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2019 The Android Open Source Project
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
DECL|package|com.google.gerrit.acceptance.api.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|api
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
name|acceptance
operator|.
name|AbstractPluginFieldsTest
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
name|acceptance
operator|.
name|NoHttpd
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
name|annotations
operator|.
name|Exports
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
name|ChangeAttributeFactory
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
name|AbstractModule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
annotation|@
name|NoHttpd
DECL|class|PluginFieldsIT
specifier|public
class|class
name|PluginFieldsIT
extends|extends
name|AbstractPluginFieldsTest
block|{
comment|// No tests for /detail via the extension API, since the extension API doesn't have that method.
annotation|@
name|Test
DECL|method|queryChangeWithNullAttribute ()
specifier|public
name|void
name|queryChangeWithNullAttribute
parameter_list|()
throws|throws
name|Exception
block|{
name|getChangeWithNullAttribute
argument_list|(
name|id
lambda|->
name|pluginInfoFromSingletonList
argument_list|(
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|query
argument_list|(
name|id
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|getChangeWithNullAttribute ()
specifier|public
name|void
name|getChangeWithNullAttribute
parameter_list|()
throws|throws
name|Exception
block|{
name|getChangeWithNullAttribute
argument_list|(
name|id
lambda|->
name|pluginInfoFromChangeInfo
argument_list|(
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|id
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|queryChangeWithSimpleAttribute ()
specifier|public
name|void
name|queryChangeWithSimpleAttribute
parameter_list|()
throws|throws
name|Exception
block|{
name|getChangeWithSimpleAttribute
argument_list|(
name|id
lambda|->
name|pluginInfoFromSingletonList
argument_list|(
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|query
argument_list|(
name|id
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|getChangeWithSimpleAttribute ()
specifier|public
name|void
name|getChangeWithSimpleAttribute
parameter_list|()
throws|throws
name|Exception
block|{
name|getChangeWithSimpleAttribute
argument_list|(
name|id
lambda|->
name|pluginInfoFromChangeInfo
argument_list|(
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|id
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|queryChangeWithOption ()
specifier|public
name|void
name|queryChangeWithOption
parameter_list|()
throws|throws
name|Exception
block|{
name|getChangeWithOption
argument_list|(
name|id
lambda|->
name|pluginInfoFromSingletonList
argument_list|(
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|query
argument_list|(
name|id
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
argument_list|,
parameter_list|(
name|id
parameter_list|,
name|opts
parameter_list|)
lambda|->
name|pluginInfoFromSingletonList
argument_list|(
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|query
argument_list|(
name|id
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|withPluginOptions
argument_list|(
name|opts
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|getChangeWithOption ()
specifier|public
name|void
name|getChangeWithOption
parameter_list|()
throws|throws
name|Exception
block|{
name|getChangeWithOption
argument_list|(
name|id
lambda|->
name|pluginInfoFromChangeInfo
argument_list|(
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|id
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
argument_list|,
parameter_list|(
name|id
parameter_list|,
name|opts
parameter_list|)
lambda|->
name|pluginInfoFromChangeInfo
argument_list|(
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|id
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|get
argument_list|(
name|opts
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|SimpleAttributeWithExplicitExportModule
specifier|static
class|class
name|SimpleAttributeWithExplicitExportModule
extends|extends
name|AbstractModule
block|{
annotation|@
name|Override
DECL|method|configure ()
specifier|public
name|void
name|configure
parameter_list|()
block|{
name|bind
argument_list|(
name|ChangeAttributeFactory
operator|.
name|class
argument_list|)
operator|.
name|annotatedWith
argument_list|(
name|Exports
operator|.
name|named
argument_list|(
literal|"simple"
argument_list|)
argument_list|)
operator|.
name|toInstance
argument_list|(
parameter_list|(
name|cd
parameter_list|,
name|bp
parameter_list|,
name|p
parameter_list|)
lambda|->
operator|new
name|MyInfo
argument_list|(
literal|"change "
operator|+
name|cd
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|getChangeWithSimpleAttributeWithExplicitExport ()
specifier|public
name|void
name|getChangeWithSimpleAttributeWithExplicitExport
parameter_list|()
throws|throws
name|Exception
block|{
comment|// For backwards compatibility with old plugins, allow modules to bind into the
comment|// DynamicSet<ChangeAttributeFactory> as if it were a DynamicMap. We only need one variant of
comment|// this test to prove that the mapping works.
name|getChangeWithSimpleAttribute
argument_list|(
name|id
lambda|->
name|pluginInfoFromChangeInfo
argument_list|(
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|id
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
argument_list|,
name|SimpleAttributeWithExplicitExportModule
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

