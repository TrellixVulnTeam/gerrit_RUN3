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
DECL|package|com.google.gerrit.client
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|core
operator|.
name|client
operator|.
name|EntryPoint
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
name|core
operator|.
name|client
operator|.
name|GWT
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
name|Window
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
name|WindowResizeListener
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
name|DockPanel
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
name|FlowPanel
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
name|RootPanel
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
name|StackPanel
import|;
end_import

begin_class
DECL|class|Gerrit
specifier|public
class|class
name|Gerrit
implements|implements
name|EntryPoint
block|{
DECL|field|C
specifier|public
specifier|static
name|GerritConstants
name|C
decl_stmt|;
DECL|field|linkManager
specifier|private
specifier|static
name|Link
name|linkManager
decl_stmt|;
DECL|field|body
specifier|static
name|DockPanel
name|body
decl_stmt|;
DECL|field|leftMenu
specifier|static
name|StackPanel
name|leftMenu
decl_stmt|;
DECL|field|currentScreen
specifier|private
specifier|static
name|Screen
name|currentScreen
decl_stmt|;
DECL|method|display (final Screen view)
specifier|public
specifier|static
name|void
name|display
parameter_list|(
specifier|final
name|Screen
name|view
parameter_list|)
block|{
if|if
condition|(
name|currentScreen
operator|!=
literal|null
condition|)
block|{
name|body
operator|.
name|remove
argument_list|(
name|currentScreen
argument_list|)
expr_stmt|;
block|}
name|currentScreen
operator|=
name|view
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
name|currentScreen
argument_list|,
name|DockPanel
operator|.
name|CENTER
argument_list|)
expr_stmt|;
block|}
DECL|method|onModuleLoad ()
specifier|public
name|void
name|onModuleLoad
parameter_list|()
block|{
name|C
operator|=
name|GWT
operator|.
name|create
argument_list|(
name|GerritConstants
operator|.
name|class
argument_list|)
expr_stmt|;
name|linkManager
operator|=
operator|new
name|Link
argument_list|()
expr_stmt|;
name|History
operator|.
name|addHistoryListener
argument_list|(
name|linkManager
argument_list|)
expr_stmt|;
name|body
operator|=
operator|new
name|DockPanel
argument_list|()
expr_stmt|;
name|body
operator|.
name|setWidth
argument_list|(
literal|"100%"
argument_list|)
expr_stmt|;
name|body
operator|.
name|setHeight
argument_list|(
name|Window
operator|.
name|getClientHeight
argument_list|()
operator|+
literal|"px"
argument_list|)
expr_stmt|;
name|Window
operator|.
name|addWindowResizeListener
argument_list|(
operator|new
name|WindowResizeListener
argument_list|()
block|{
specifier|public
name|void
name|onWindowResized
parameter_list|(
specifier|final
name|int
name|width
parameter_list|,
specifier|final
name|int
name|height
parameter_list|)
block|{
name|body
operator|.
name|setHeight
argument_list|(
name|height
operator|+
literal|"px"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|RootPanel
operator|.
name|get
argument_list|(
literal|"gerrit_body"
argument_list|)
operator|.
name|add
argument_list|(
name|body
argument_list|)
expr_stmt|;
name|leftMenu
operator|=
operator|new
name|StackPanel
argument_list|()
expr_stmt|;
name|leftMenu
operator|.
name|addStyleName
argument_list|(
literal|"gerrit-LeftMenu"
argument_list|)
expr_stmt|;
name|leftMenu
operator|.
name|add
argument_list|(
name|createCodeReviewMenu
argument_list|()
argument_list|,
name|C
operator|.
name|leftMenuCodeReviews
argument_list|()
argument_list|)
expr_stmt|;
name|leftMenu
operator|.
name|add
argument_list|(
name|createAdminMenu
argument_list|()
argument_list|,
name|C
operator|.
name|leftMenuAdmin
argument_list|()
argument_list|)
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
name|leftMenu
argument_list|,
name|DockPanel
operator|.
name|WEST
argument_list|)
expr_stmt|;
name|body
operator|.
name|setCellWidth
argument_list|(
name|leftMenu
argument_list|,
literal|"150px"
argument_list|)
expr_stmt|;
if|if
condition|(
literal|""
operator|.
name|equals
argument_list|(
name|History
operator|.
name|getToken
argument_list|()
argument_list|)
condition|)
block|{
name|History
operator|.
name|newItem
argument_list|(
name|Link
operator|.
name|MINE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|History
operator|.
name|fireCurrentHistoryState
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|createCodeReviewMenu ()
specifier|private
name|FlowPanel
name|createCodeReviewMenu
parameter_list|()
block|{
specifier|final
name|FlowPanel
name|menu
init|=
operator|new
name|FlowPanel
argument_list|()
decl_stmt|;
name|menu
operator|.
name|setStyleName
argument_list|(
literal|"gerrit-MenuList"
argument_list|)
expr_stmt|;
name|menu
operator|.
name|add
argument_list|(
operator|new
name|Hyperlink
argument_list|(
name|C
operator|.
name|menuMyChanges
argument_list|()
argument_list|,
name|Link
operator|.
name|MINE
argument_list|)
argument_list|)
expr_stmt|;
name|menu
operator|.
name|add
argument_list|(
operator|new
name|Hyperlink
argument_list|(
name|C
operator|.
name|menuMyUnclaimedChanges
argument_list|()
argument_list|,
name|Link
operator|.
name|MINE_UNCLAIMED
argument_list|)
argument_list|)
expr_stmt|;
name|menu
operator|.
name|add
argument_list|(
operator|new
name|Hyperlink
argument_list|(
name|C
operator|.
name|menuAllRecentChanges
argument_list|()
argument_list|,
name|Link
operator|.
name|ALL
argument_list|)
argument_list|)
expr_stmt|;
name|menu
operator|.
name|add
argument_list|(
operator|new
name|Hyperlink
argument_list|(
name|C
operator|.
name|menuAllUnclaimedChanges
argument_list|()
argument_list|,
name|Link
operator|.
name|ALL_UNCLAIMED
argument_list|)
argument_list|)
expr_stmt|;
name|menu
operator|.
name|add
argument_list|(
operator|new
name|Hyperlink
argument_list|(
name|C
operator|.
name|menuMyStarredChanges
argument_list|()
argument_list|,
name|Link
operator|.
name|MINE_STARRED
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|menu
return|;
block|}
DECL|method|createAdminMenu ()
specifier|private
name|FlowPanel
name|createAdminMenu
parameter_list|()
block|{
specifier|final
name|FlowPanel
name|menu
init|=
operator|new
name|FlowPanel
argument_list|()
decl_stmt|;
name|menu
operator|.
name|setStyleName
argument_list|(
literal|"gerrit-MenuList"
argument_list|)
expr_stmt|;
name|menu
operator|.
name|add
argument_list|(
operator|new
name|Hyperlink
argument_list|(
name|C
operator|.
name|menuPeople
argument_list|()
argument_list|,
name|Link
operator|.
name|ADMIN_PEOPLE
argument_list|)
argument_list|)
expr_stmt|;
name|menu
operator|.
name|add
argument_list|(
operator|new
name|Hyperlink
argument_list|(
name|C
operator|.
name|menuGroups
argument_list|()
argument_list|,
name|Link
operator|.
name|ADMIN_GROUPS
argument_list|)
argument_list|)
expr_stmt|;
name|menu
operator|.
name|add
argument_list|(
operator|new
name|Hyperlink
argument_list|(
name|C
operator|.
name|menuProjects
argument_list|()
argument_list|,
name|Link
operator|.
name|ADMIN_PROJECTS
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|menu
return|;
block|}
block|}
end_class

end_unit

