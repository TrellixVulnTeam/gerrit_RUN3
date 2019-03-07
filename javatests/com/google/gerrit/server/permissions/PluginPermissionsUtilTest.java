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
DECL|package|com.google.gerrit.server.permissions
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|permissions
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
name|truth
operator|.
name|Truth
operator|.
name|assertThat
import|;
end_import

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
name|permissions
operator|.
name|PluginPermissionsUtil
operator|.
name|isPluginPermission
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

begin_comment
comment|/** Small tests for {@link PluginPermissionsUtil}. */
end_comment

begin_class
DECL|class|PluginPermissionsUtilTest
specifier|public
class|class
name|PluginPermissionsUtilTest
block|{
annotation|@
name|Test
DECL|method|pluginPermissionNameInConfigPattern ()
specifier|public
name|void
name|pluginPermissionNameInConfigPattern
parameter_list|()
block|{
name|assertThat
argument_list|(
name|isPluginPermission
argument_list|(
literal|"create"
argument_list|)
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|isPluginPermission
argument_list|(
literal|"label-Code-Review"
argument_list|)
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|isPluginPermission
argument_list|(
literal|"plugin-foo"
argument_list|)
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|isPluginPermission
argument_list|(
literal|"plugin-foo"
argument_list|)
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|isPluginPermission
argument_list|(
literal|"plugin-foo-a"
argument_list|)
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
comment|// "-" is allowed for a plugin name. Here "foo-a" should be the name of the plugin.
name|assertThat
argument_list|(
name|isPluginPermission
argument_list|(
literal|"plugin-foo-a-b"
argument_list|)
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|isPluginPermission
argument_list|(
literal|"plugin-foo-a-"
argument_list|)
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|isPluginPermission
argument_list|(
literal|"plugin-foo-a1"
argument_list|)
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

