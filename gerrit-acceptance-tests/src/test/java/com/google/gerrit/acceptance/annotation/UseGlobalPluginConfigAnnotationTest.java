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
DECL|package|com.google.gerrit.acceptance.annotation
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|annotation
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
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|AbstractDaemonTest
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
name|GlobalPluginConfig
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
name|UseLocalDisk
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
name|lib
operator|.
name|Config
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
DECL|class|UseGlobalPluginConfigAnnotationTest
specifier|public
class|class
name|UseGlobalPluginConfigAnnotationTest
extends|extends
name|AbstractDaemonTest
block|{
DECL|method|cfg ()
specifier|private
name|Config
name|cfg
parameter_list|()
block|{
return|return
name|pluginConfig
operator|.
name|getGlobalPluginConfig
argument_list|(
literal|"test"
argument_list|)
return|;
block|}
annotation|@
name|Test
annotation|@
name|UseLocalDisk
annotation|@
name|GlobalPluginConfig
argument_list|(
name|pluginName
operator|=
literal|"test"
argument_list|,
name|name
operator|=
literal|"section.name"
argument_list|,
name|value
operator|=
literal|"value"
argument_list|)
DECL|method|testOne ()
specifier|public
name|void
name|testOne
parameter_list|()
block|{
name|assertThat
argument_list|(
name|cfg
argument_list|()
operator|.
name|getString
argument_list|(
literal|"section"
argument_list|,
literal|null
argument_list|,
literal|"name"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"value"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|UseLocalDisk
annotation|@
name|GlobalPluginConfig
argument_list|(
name|pluginName
operator|=
literal|"test"
argument_list|,
name|name
operator|=
literal|"section.subsection.name"
argument_list|,
name|value
operator|=
literal|"value"
argument_list|)
DECL|method|testOneWithSubsection ()
specifier|public
name|void
name|testOneWithSubsection
parameter_list|()
block|{
name|assertThat
argument_list|(
name|cfg
argument_list|()
operator|.
name|getString
argument_list|(
literal|"section"
argument_list|,
literal|"subsection"
argument_list|,
literal|"name"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"value"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|UseLocalDisk
annotation|@
name|GlobalPluginConfig
argument_list|(
name|pluginName
operator|=
literal|"test"
argument_list|,
name|name
operator|=
literal|"section.name"
argument_list|,
name|value
operator|=
literal|"value"
argument_list|)
annotation|@
name|GlobalPluginConfig
argument_list|(
name|pluginName
operator|=
literal|"test"
argument_list|,
name|name
operator|=
literal|"section1.name"
argument_list|,
name|value
operator|=
literal|"value1"
argument_list|)
annotation|@
name|GlobalPluginConfig
argument_list|(
name|pluginName
operator|=
literal|"test"
argument_list|,
name|name
operator|=
literal|"section.subsection.name"
argument_list|,
name|value
operator|=
literal|"value"
argument_list|)
annotation|@
name|GlobalPluginConfig
argument_list|(
name|pluginName
operator|=
literal|"test"
argument_list|,
name|name
operator|=
literal|"section.subsection1.name"
argument_list|,
name|value
operator|=
literal|"value1"
argument_list|)
DECL|method|testMultiple ()
specifier|public
name|void
name|testMultiple
parameter_list|()
block|{
name|assertThat
argument_list|(
name|cfg
argument_list|()
operator|.
name|getString
argument_list|(
literal|"section"
argument_list|,
literal|null
argument_list|,
literal|"name"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"value"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cfg
argument_list|()
operator|.
name|getString
argument_list|(
literal|"section1"
argument_list|,
literal|null
argument_list|,
literal|"name"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"value1"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cfg
argument_list|()
operator|.
name|getString
argument_list|(
literal|"section"
argument_list|,
literal|"subsection"
argument_list|,
literal|"name"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"value"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cfg
argument_list|()
operator|.
name|getString
argument_list|(
literal|"section"
argument_list|,
literal|"subsection1"
argument_list|,
literal|"name"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"value1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|UseLocalDisk
annotation|@
name|GlobalPluginConfig
argument_list|(
name|pluginName
operator|=
literal|"test"
argument_list|,
name|name
operator|=
literal|"section.name"
argument_list|,
name|values
operator|=
block|{
literal|"value-1"
block|,
literal|"value-2"
block|}
argument_list|)
DECL|method|testList ()
specifier|public
name|void
name|testList
parameter_list|()
block|{
name|assertThat
argument_list|(
name|cfg
argument_list|()
operator|.
name|getStringList
argument_list|(
literal|"section"
argument_list|,
literal|null
argument_list|,
literal|"name"
argument_list|)
argument_list|)
operator|.
name|asList
argument_list|()
operator|.
name|containsExactly
argument_list|(
literal|"value-1"
argument_list|,
literal|"value-2"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|UseLocalDisk
annotation|@
name|GlobalPluginConfig
argument_list|(
name|pluginName
operator|=
literal|"test"
argument_list|,
name|name
operator|=
literal|"section.subsection.name"
argument_list|,
name|values
operator|=
block|{
literal|"value-1"
block|,
literal|"value-2"
block|}
argument_list|)
DECL|method|testListWithSubsection ()
specifier|public
name|void
name|testListWithSubsection
parameter_list|()
block|{
name|assertThat
argument_list|(
name|cfg
argument_list|()
operator|.
name|getStringList
argument_list|(
literal|"section"
argument_list|,
literal|"subsection"
argument_list|,
literal|"name"
argument_list|)
argument_list|)
operator|.
name|asList
argument_list|()
operator|.
name|containsExactly
argument_list|(
literal|"value-1"
argument_list|,
literal|"value-2"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|UseLocalDisk
annotation|@
name|GlobalPluginConfig
argument_list|(
name|pluginName
operator|=
literal|"test"
argument_list|,
name|name
operator|=
literal|"section.name"
argument_list|,
name|value
operator|=
literal|"value-1"
argument_list|,
name|values
operator|=
block|{
literal|"value-2"
block|,
literal|"value-3"
block|}
argument_list|)
DECL|method|valueHasPrecedenceOverValues ()
specifier|public
name|void
name|valueHasPrecedenceOverValues
parameter_list|()
block|{
name|assertThat
argument_list|(
name|cfg
argument_list|()
operator|.
name|getStringList
argument_list|(
literal|"section"
argument_list|,
literal|null
argument_list|,
literal|"name"
argument_list|)
argument_list|)
operator|.
name|asList
argument_list|()
operator|.
name|containsExactly
argument_list|(
literal|"value-1"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

