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
DECL|package|com.google.gerrit.client.admin
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|admin
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
name|Gerrit
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
name|Link
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
name|Project
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
name|ScreenLoadCallback
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
name|ui
operator|.
name|AccountScreen
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
name|event
operator|.
name|logical
operator|.
name|shared
operator|.
name|SelectionEvent
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
name|event
operator|.
name|logical
operator|.
name|shared
operator|.
name|SelectionHandler
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
name|ui
operator|.
name|LazyPanel
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
name|ui
operator|.
name|TabPanel
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
DECL|class|ProjectAdminScreen
specifier|public
class|class
name|ProjectAdminScreen
extends|extends
name|AccountScreen
block|{
DECL|field|INFO_TAB
specifier|static
specifier|final
name|String
name|INFO_TAB
init|=
literal|"info"
decl_stmt|;
DECL|field|BRANCH_TAB
specifier|static
specifier|final
name|String
name|BRANCH_TAB
init|=
literal|"branches"
decl_stmt|;
DECL|field|ACCESS_TAB
specifier|static
specifier|final
name|String
name|ACCESS_TAB
init|=
literal|"access"
decl_stmt|;
DECL|field|projectName
specifier|private
specifier|final
name|Project
operator|.
name|NameKey
name|projectName
decl_stmt|;
DECL|field|initialTabToken
specifier|private
specifier|final
name|String
name|initialTabToken
decl_stmt|;
DECL|field|tabTokens
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|tabTokens
decl_stmt|;
DECL|field|tabs
specifier|private
name|TabPanel
name|tabs
decl_stmt|;
DECL|method|ProjectAdminScreen (final Project.NameKey toShow, final String token)
specifier|public
name|ProjectAdminScreen
parameter_list|(
specifier|final
name|Project
operator|.
name|NameKey
name|toShow
parameter_list|,
specifier|final
name|String
name|token
parameter_list|)
block|{
name|projectName
operator|=
name|toShow
expr_stmt|;
name|initialTabToken
operator|=
name|token
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onLoad ()
specifier|protected
name|void
name|onLoad
parameter_list|()
block|{
name|super
operator|.
name|onLoad
argument_list|()
expr_stmt|;
name|Util
operator|.
name|PROJECT_SVC
operator|.
name|projectDetail
argument_list|(
name|projectName
argument_list|,
operator|new
name|ScreenLoadCallback
argument_list|<
name|ProjectDetail
argument_list|>
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|preDisplay
parameter_list|(
specifier|final
name|ProjectDetail
name|result
parameter_list|)
block|{
name|display
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|tabs
operator|.
name|selectTab
argument_list|(
name|tabTokens
operator|.
name|indexOf
argument_list|(
name|initialTabToken
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onInitUI ()
specifier|protected
name|void
name|onInitUI
parameter_list|()
block|{
name|super
operator|.
name|onInitUI
argument_list|()
expr_stmt|;
name|tabTokens
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|tabs
operator|=
operator|new
name|TabPanel
argument_list|()
expr_stmt|;
name|tabs
operator|.
name|setWidth
argument_list|(
literal|"98%"
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|tabs
argument_list|)
expr_stmt|;
name|tabs
operator|.
name|add
argument_list|(
operator|new
name|LazyPanel
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|ProjectInfoPanel
name|createWidget
parameter_list|()
block|{
return|return
operator|new
name|ProjectInfoPanel
argument_list|(
name|projectName
argument_list|)
return|;
block|}
block|}
argument_list|,
name|Util
operator|.
name|C
operator|.
name|projectAdminTabGeneral
argument_list|()
argument_list|)
expr_stmt|;
name|tabTokens
operator|.
name|add
argument_list|(
name|Link
operator|.
name|toProjectAdmin
argument_list|(
name|projectName
argument_list|,
name|INFO_TAB
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Gerrit
operator|.
name|getConfig
argument_list|()
operator|.
name|getWildProject
argument_list|()
operator|.
name|equals
argument_list|(
name|projectName
argument_list|)
condition|)
block|{
name|tabs
operator|.
name|add
argument_list|(
operator|new
name|LazyPanel
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|ProjectBranchesPanel
name|createWidget
parameter_list|()
block|{
return|return
operator|new
name|ProjectBranchesPanel
argument_list|(
name|projectName
argument_list|)
return|;
block|}
block|}
argument_list|,
name|Util
operator|.
name|C
operator|.
name|projectAdminTabBranches
argument_list|()
argument_list|)
expr_stmt|;
name|tabTokens
operator|.
name|add
argument_list|(
name|Link
operator|.
name|toProjectAdmin
argument_list|(
name|projectName
argument_list|,
name|BRANCH_TAB
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|tabs
operator|.
name|add
argument_list|(
operator|new
name|LazyPanel
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|ProjectRightsPanel
name|createWidget
parameter_list|()
block|{
return|return
operator|new
name|ProjectRightsPanel
argument_list|(
name|projectName
argument_list|)
return|;
block|}
block|}
argument_list|,
name|Util
operator|.
name|C
operator|.
name|projectAdminTabAccess
argument_list|()
argument_list|)
expr_stmt|;
name|tabTokens
operator|.
name|add
argument_list|(
name|Link
operator|.
name|toProjectAdmin
argument_list|(
name|projectName
argument_list|,
name|ACCESS_TAB
argument_list|)
argument_list|)
expr_stmt|;
name|tabs
operator|.
name|addSelectionHandler
argument_list|(
operator|new
name|SelectionHandler
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSelection
parameter_list|(
specifier|final
name|SelectionEvent
argument_list|<
name|Integer
argument_list|>
name|event
parameter_list|)
block|{
name|Gerrit
operator|.
name|display
argument_list|(
name|tabTokens
operator|.
name|get
argument_list|(
name|event
operator|.
name|getSelectedItem
argument_list|()
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|display (final ProjectDetail result)
specifier|private
name|void
name|display
parameter_list|(
specifier|final
name|ProjectDetail
name|result
parameter_list|)
block|{
specifier|final
name|Project
name|project
init|=
name|result
operator|.
name|project
decl_stmt|;
name|setPageTitle
argument_list|(
name|Util
operator|.
name|M
operator|.
name|project
argument_list|(
name|project
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

