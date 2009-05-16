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
name|gerrit
operator|.
name|client
operator|.
name|ui
operator|.
name|NavigationTable
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
name|SmallHeading
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
name|dom
operator|.
name|client
operator|.
name|ClickEvent
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
name|dom
operator|.
name|client
operator|.
name|ClickHandler
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
name|dom
operator|.
name|client
operator|.
name|KeyCodes
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
name|History
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
name|Hyperlink
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
name|VerticalPanel
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
name|FlexTable
operator|.
name|FlexCellFormatter
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
name|HTMLTable
operator|.
name|Cell
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
DECL|class|ProjectListScreen
specifier|public
class|class
name|ProjectListScreen
extends|extends
name|AccountScreen
block|{
DECL|field|projects
specifier|private
name|ProjectTable
name|projects
decl_stmt|;
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
name|ownedProjects
argument_list|(
operator|new
name|ScreenLoadCallback
argument_list|<
name|List
argument_list|<
name|Project
argument_list|>
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
name|List
argument_list|<
name|Project
argument_list|>
name|result
parameter_list|)
block|{
name|projects
operator|.
name|display
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|projects
operator|.
name|finishDisplay
argument_list|()
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
name|setPageTitle
argument_list|(
name|Util
operator|.
name|C
operator|.
name|projectListTitle
argument_list|()
argument_list|)
expr_stmt|;
name|projects
operator|=
operator|new
name|ProjectTable
argument_list|()
expr_stmt|;
name|add
argument_list|(
name|projects
argument_list|)
expr_stmt|;
specifier|final
name|VerticalPanel
name|fp
init|=
operator|new
name|VerticalPanel
argument_list|()
decl_stmt|;
name|fp
operator|.
name|setStyleName
argument_list|(
literal|"gerrit-AddSshKeyPanel"
argument_list|)
expr_stmt|;
name|fp
operator|.
name|add
argument_list|(
operator|new
name|SmallHeading
argument_list|(
name|Util
operator|.
name|C
operator|.
name|headingCreateGroup
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|registerKeys ()
specifier|public
name|void
name|registerKeys
parameter_list|()
block|{
name|super
operator|.
name|registerKeys
argument_list|()
expr_stmt|;
name|projects
operator|.
name|setRegisterKeys
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|class|ProjectTable
specifier|private
class|class
name|ProjectTable
extends|extends
name|NavigationTable
argument_list|<
name|Project
argument_list|>
block|{
DECL|method|ProjectTable ()
name|ProjectTable
parameter_list|()
block|{
name|setSavePointerId
argument_list|(
name|Link
operator|.
name|ADMIN_PROJECTS
argument_list|)
expr_stmt|;
name|keysNavigation
operator|.
name|add
argument_list|(
operator|new
name|PrevKeyCommand
argument_list|(
literal|0
argument_list|,
literal|'k'
argument_list|,
name|Util
operator|.
name|C
operator|.
name|projectListPrev
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|keysNavigation
operator|.
name|add
argument_list|(
operator|new
name|NextKeyCommand
argument_list|(
literal|0
argument_list|,
literal|'j'
argument_list|,
name|Util
operator|.
name|C
operator|.
name|projectListNext
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|keysNavigation
operator|.
name|add
argument_list|(
operator|new
name|OpenKeyCommand
argument_list|(
literal|0
argument_list|,
literal|'o'
argument_list|,
name|Util
operator|.
name|C
operator|.
name|projectListOpen
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|keysNavigation
operator|.
name|add
argument_list|(
operator|new
name|OpenKeyCommand
argument_list|(
literal|0
argument_list|,
name|KeyCodes
operator|.
name|KEY_ENTER
argument_list|,
name|Util
operator|.
name|C
operator|.
name|projectListOpen
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|table
operator|.
name|setText
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
name|Util
operator|.
name|C
operator|.
name|columnProjectName
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|setText
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|,
name|Util
operator|.
name|C
operator|.
name|columnProjectDescription
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addClickHandler
argument_list|(
operator|new
name|ClickHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onClick
parameter_list|(
specifier|final
name|ClickEvent
name|event
parameter_list|)
block|{
specifier|final
name|Cell
name|cell
init|=
name|table
operator|.
name|getCellForEvent
argument_list|(
name|event
argument_list|)
decl_stmt|;
if|if
condition|(
name|cell
operator|!=
literal|null
operator|&&
name|cell
operator|.
name|getCellIndex
argument_list|()
operator|!=
literal|1
operator|&&
name|getRowItem
argument_list|(
name|cell
operator|.
name|getRowIndex
argument_list|()
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|movePointerTo
argument_list|(
name|cell
operator|.
name|getRowIndex
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
specifier|final
name|FlexCellFormatter
name|fmt
init|=
name|table
operator|.
name|getFlexCellFormatter
argument_list|()
decl_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
name|S_DATA_HEADER
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|,
name|S_DATA_HEADER
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getRowItemKey (final Project item)
specifier|protected
name|Object
name|getRowItemKey
parameter_list|(
specifier|final
name|Project
name|item
parameter_list|)
block|{
return|return
name|item
operator|.
name|getId
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|onOpenItem (final Project item)
specifier|protected
name|void
name|onOpenItem
parameter_list|(
specifier|final
name|Project
name|item
parameter_list|)
block|{
name|History
operator|.
name|newItem
argument_list|(
name|link
argument_list|(
name|item
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|link (final Project item)
specifier|private
name|String
name|link
parameter_list|(
specifier|final
name|Project
name|item
parameter_list|)
block|{
return|return
name|Link
operator|.
name|toProjectAdmin
argument_list|(
name|item
operator|.
name|getId
argument_list|()
argument_list|,
name|ProjectAdminScreen
operator|.
name|INFO_TAB
argument_list|)
return|;
block|}
DECL|method|display (final List<Project> result)
name|void
name|display
parameter_list|(
specifier|final
name|List
argument_list|<
name|Project
argument_list|>
name|result
parameter_list|)
block|{
while|while
condition|(
literal|1
operator|<
name|table
operator|.
name|getRowCount
argument_list|()
condition|)
name|table
operator|.
name|removeRow
argument_list|(
name|table
operator|.
name|getRowCount
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|Project
name|k
range|:
name|result
control|)
block|{
specifier|final
name|int
name|row
init|=
name|table
operator|.
name|getRowCount
argument_list|()
decl_stmt|;
name|table
operator|.
name|insertRow
argument_list|(
name|row
argument_list|)
expr_stmt|;
name|applyDataRowStyle
argument_list|(
name|row
argument_list|)
expr_stmt|;
name|populate
argument_list|(
name|row
argument_list|,
name|k
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|populate (final int row, final Project k)
name|void
name|populate
parameter_list|(
specifier|final
name|int
name|row
parameter_list|,
specifier|final
name|Project
name|k
parameter_list|)
block|{
name|table
operator|.
name|setWidget
argument_list|(
name|row
argument_list|,
literal|1
argument_list|,
operator|new
name|Hyperlink
argument_list|(
name|k
operator|.
name|getName
argument_list|()
argument_list|,
name|link
argument_list|(
name|k
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|table
operator|.
name|setText
argument_list|(
name|row
argument_list|,
literal|2
argument_list|,
name|k
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|FlexCellFormatter
name|fmt
init|=
name|table
operator|.
name|getFlexCellFormatter
argument_list|()
decl_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
name|row
argument_list|,
literal|1
argument_list|,
name|S_DATA_CELL
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
name|row
argument_list|,
literal|2
argument_list|,
name|S_DATA_CELL
argument_list|)
expr_stmt|;
name|setRowItem
argument_list|(
name|row
argument_list|,
name|k
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

