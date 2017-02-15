begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2011 The Android Open Source Project
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
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|common
operator|.
name|data
operator|.
name|GlobalCapability
operator|.
name|CREATE_PROJECT
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
name|Dispatcher
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
name|ErrorDialog
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
name|NotFoundScreen
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
name|VoidResult
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
name|account
operator|.
name|AccountCapabilities
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
name|projects
operator|.
name|ProjectApi
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
name|projects
operator|.
name|ProjectInfo
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
name|projects
operator|.
name|ProjectMap
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
name|GerritCallback
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
name|OnEditEnabler
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
name|ProjectListPopup
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
name|ProjectNameSuggestOracle
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
name|ProjectsTable
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
name|RemoteSuggestBox
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
name|Screen
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
name|PageLinks
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
name|ProjectUtil
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
name|reviewdb
operator|.
name|client
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
name|gwt
operator|.
name|core
operator|.
name|client
operator|.
name|Scheduler
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
name|Scheduler
operator|.
name|ScheduledCommand
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
name|event
operator|.
name|dom
operator|.
name|client
operator|.
name|KeyPressEvent
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
name|KeyPressHandler
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
name|Event
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
name|rpc
operator|.
name|AsyncCallback
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
name|Anchor
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
name|Button
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
name|CheckBox
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
name|Grid
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
name|gwtexpui
operator|.
name|globalkey
operator|.
name|client
operator|.
name|NpTextBox
import|;
end_import

begin_class
DECL|class|CreateProjectScreen
specifier|public
class|class
name|CreateProjectScreen
extends|extends
name|Screen
block|{
DECL|field|grid
specifier|private
name|Grid
name|grid
decl_stmt|;
DECL|field|project
specifier|private
name|NpTextBox
name|project
decl_stmt|;
DECL|field|create
specifier|private
name|Button
name|create
decl_stmt|;
DECL|field|browse
specifier|private
name|Button
name|browse
decl_stmt|;
DECL|field|parent
specifier|private
name|RemoteSuggestBox
name|parent
decl_stmt|;
DECL|field|emptyCommit
specifier|private
name|CheckBox
name|emptyCommit
decl_stmt|;
DECL|field|permissionsOnly
specifier|private
name|CheckBox
name|permissionsOnly
decl_stmt|;
DECL|field|suggestedParentsTab
specifier|private
name|ProjectsTable
name|suggestedParentsTab
decl_stmt|;
DECL|field|projectsPopup
specifier|private
name|ProjectListPopup
name|projectsPopup
decl_stmt|;
DECL|method|CreateProjectScreen ()
specifier|public
name|CreateProjectScreen
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|setRequiresSignIn
argument_list|(
literal|true
argument_list|)
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
name|AccountCapabilities
operator|.
name|all
argument_list|(
operator|new
name|GerritCallback
argument_list|<
name|AccountCapabilities
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|AccountCapabilities
name|ac
parameter_list|)
block|{
if|if
condition|(
name|ac
operator|.
name|canPerform
argument_list|(
name|CREATE_PROJECT
argument_list|)
condition|)
block|{
name|display
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|Gerrit
operator|.
name|display
argument_list|(
name|PageLinks
operator|.
name|ADMIN_CREATE_PROJECT
argument_list|,
operator|new
name|NotFoundScreen
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|,
name|CREATE_PROJECT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onUnload ()
specifier|protected
name|void
name|onUnload
parameter_list|()
block|{
name|super
operator|.
name|onUnload
argument_list|()
expr_stmt|;
name|projectsPopup
operator|.
name|closePopup
argument_list|()
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
name|AdminConstants
operator|.
name|I
operator|.
name|createProjectTitle
argument_list|()
argument_list|)
expr_stmt|;
name|addCreateProjectPanel
argument_list|()
expr_stmt|;
comment|/* popup */
name|projectsPopup
operator|=
operator|new
name|ProjectListPopup
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|onMovePointerTo
parameter_list|(
name|String
name|projectName
parameter_list|)
block|{
comment|// prevent user input from being overwritten by simply poping up
if|if
condition|(
operator|!
name|projectsPopup
operator|.
name|isPoppingUp
argument_list|()
operator|||
literal|""
operator|.
name|equals
argument_list|(
name|parent
operator|.
name|getText
argument_list|()
argument_list|)
condition|)
block|{
name|parent
operator|.
name|setText
argument_list|(
name|projectName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
expr_stmt|;
name|projectsPopup
operator|.
name|initPopup
argument_list|(
name|AdminConstants
operator|.
name|I
operator|.
name|projects
argument_list|()
argument_list|,
name|PageLinks
operator|.
name|ADMIN_PROJECTS
argument_list|)
expr_stmt|;
block|}
DECL|method|addCreateProjectPanel ()
specifier|private
name|void
name|addCreateProjectPanel
parameter_list|()
block|{
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
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|createProjectPanel
argument_list|()
argument_list|)
expr_stmt|;
name|initCreateButton
argument_list|()
expr_stmt|;
name|initCreateTxt
argument_list|()
expr_stmt|;
name|initParentBox
argument_list|()
expr_stmt|;
name|addGrid
argument_list|(
name|fp
argument_list|)
expr_stmt|;
name|emptyCommit
operator|=
operator|new
name|CheckBox
argument_list|(
name|AdminConstants
operator|.
name|I
operator|.
name|checkBoxEmptyCommit
argument_list|()
argument_list|)
expr_stmt|;
name|permissionsOnly
operator|=
operator|new
name|CheckBox
argument_list|(
name|AdminConstants
operator|.
name|I
operator|.
name|checkBoxPermissionsOnly
argument_list|()
argument_list|)
expr_stmt|;
name|fp
operator|.
name|add
argument_list|(
name|emptyCommit
argument_list|)
expr_stmt|;
name|fp
operator|.
name|add
argument_list|(
name|permissionsOnly
argument_list|)
expr_stmt|;
name|fp
operator|.
name|add
argument_list|(
name|create
argument_list|)
expr_stmt|;
name|VerticalPanel
name|vp
init|=
operator|new
name|VerticalPanel
argument_list|()
decl_stmt|;
name|vp
operator|.
name|add
argument_list|(
name|fp
argument_list|)
expr_stmt|;
name|initSuggestedParents
argument_list|()
expr_stmt|;
name|vp
operator|.
name|add
argument_list|(
name|suggestedParentsTab
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|vp
argument_list|)
expr_stmt|;
block|}
DECL|method|initCreateTxt ()
specifier|private
name|void
name|initCreateTxt
parameter_list|()
block|{
name|project
operator|=
operator|new
name|NpTextBox
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onBrowserEvent
parameter_list|(
name|Event
name|event
parameter_list|)
block|{
name|super
operator|.
name|onBrowserEvent
argument_list|(
name|event
argument_list|)
expr_stmt|;
if|if
condition|(
name|event
operator|.
name|getTypeInt
argument_list|()
operator|==
name|Event
operator|.
name|ONPASTE
condition|)
block|{
name|Scheduler
operator|.
name|get
argument_list|()
operator|.
name|scheduleDeferred
argument_list|(
operator|new
name|ScheduledCommand
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|()
block|{
if|if
condition|(
name|project
operator|.
name|getValue
argument_list|()
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|create
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
expr_stmt|;
name|project
operator|.
name|sinkEvents
argument_list|(
name|Event
operator|.
name|ONPASTE
argument_list|)
expr_stmt|;
name|project
operator|.
name|setVisibleLength
argument_list|(
literal|50
argument_list|)
expr_stmt|;
name|project
operator|.
name|addKeyPressHandler
argument_list|(
operator|new
name|KeyPressHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onKeyPress
parameter_list|(
name|KeyPressEvent
name|event
parameter_list|)
block|{
if|if
condition|(
name|event
operator|.
name|getNativeEvent
argument_list|()
operator|.
name|getKeyCode
argument_list|()
operator|==
name|KeyCodes
operator|.
name|KEY_ENTER
condition|)
block|{
name|doCreateProject
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
operator|new
name|OnEditEnabler
argument_list|(
name|create
argument_list|,
name|project
argument_list|)
expr_stmt|;
block|}
DECL|method|initCreateButton ()
specifier|private
name|void
name|initCreateButton
parameter_list|()
block|{
name|create
operator|=
operator|new
name|Button
argument_list|(
name|AdminConstants
operator|.
name|I
operator|.
name|buttonCreateProject
argument_list|()
argument_list|)
expr_stmt|;
name|create
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|create
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
name|doCreateProject
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|browse
operator|=
operator|new
name|Button
argument_list|(
name|AdminConstants
operator|.
name|I
operator|.
name|buttonBrowseProjects
argument_list|()
argument_list|)
expr_stmt|;
name|browse
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
name|int
name|top
init|=
name|grid
operator|.
name|getAbsoluteTop
argument_list|()
operator|-
literal|50
decl_stmt|;
comment|// under page header
comment|// Try to place it to the right of everything else, but not
comment|// right justified
name|int
name|left
init|=
literal|5
operator|+
name|Math
operator|.
name|max
argument_list|(
name|grid
operator|.
name|getAbsoluteLeft
argument_list|()
operator|+
name|grid
operator|.
name|getOffsetWidth
argument_list|()
argument_list|,
name|suggestedParentsTab
operator|.
name|getAbsoluteLeft
argument_list|()
operator|+
name|suggestedParentsTab
operator|.
name|getOffsetWidth
argument_list|()
argument_list|)
decl_stmt|;
name|projectsPopup
operator|.
name|setPreferredCoordinates
argument_list|(
name|top
argument_list|,
name|left
argument_list|)
expr_stmt|;
name|projectsPopup
operator|.
name|displayPopup
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|initParentBox ()
specifier|private
name|void
name|initParentBox
parameter_list|()
block|{
name|parent
operator|=
operator|new
name|RemoteSuggestBox
argument_list|(
operator|new
name|ProjectNameSuggestOracle
argument_list|()
argument_list|)
expr_stmt|;
name|parent
operator|.
name|setVisibleLength
argument_list|(
literal|50
argument_list|)
expr_stmt|;
block|}
DECL|method|initSuggestedParents ()
specifier|private
name|void
name|initSuggestedParents
parameter_list|()
block|{
name|suggestedParentsTab
operator|=
operator|new
name|ProjectsTable
argument_list|()
block|{
block|{
name|table
operator|.
name|setText
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
name|AdminConstants
operator|.
name|I
operator|.
name|parentSuggestions
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|populate
parameter_list|(
specifier|final
name|int
name|row
parameter_list|,
specifier|final
name|ProjectInfo
name|k
parameter_list|)
block|{
specifier|final
name|Anchor
name|projectLink
init|=
operator|new
name|Anchor
argument_list|(
name|k
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|projectLink
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
name|ClickEvent
name|event
parameter_list|)
block|{
name|parent
operator|.
name|setText
argument_list|(
name|getRowItem
argument_list|(
name|row
argument_list|)
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|table
operator|.
name|setWidget
argument_list|(
name|row
argument_list|,
literal|2
argument_list|,
name|projectLink
argument_list|)
expr_stmt|;
name|table
operator|.
name|setText
argument_list|(
name|row
argument_list|,
literal|3
argument_list|,
name|k
operator|.
name|description
argument_list|()
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
expr_stmt|;
name|suggestedParentsTab
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|ProjectMap
operator|.
name|parentCandidates
argument_list|(
operator|new
name|GerritCallback
argument_list|<
name|ProjectMap
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|ProjectMap
name|list
parameter_list|)
block|{
if|if
condition|(
operator|!
name|list
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|suggestedParentsTab
operator|.
name|setVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|suggestedParentsTab
operator|.
name|display
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|suggestedParentsTab
operator|.
name|finishDisplay
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|addGrid (final VerticalPanel fp)
specifier|private
name|void
name|addGrid
parameter_list|(
specifier|final
name|VerticalPanel
name|fp
parameter_list|)
block|{
name|grid
operator|=
operator|new
name|Grid
argument_list|(
literal|2
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|grid
operator|.
name|setStyleName
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|infoBlock
argument_list|()
argument_list|)
expr_stmt|;
name|grid
operator|.
name|setText
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|AdminConstants
operator|.
name|I
operator|.
name|columnProjectName
argument_list|()
operator|+
literal|":"
argument_list|)
expr_stmt|;
name|grid
operator|.
name|setWidget
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
name|project
argument_list|)
expr_stmt|;
name|grid
operator|.
name|setText
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
name|AdminConstants
operator|.
name|I
operator|.
name|headingParentProjectName
argument_list|()
operator|+
literal|":"
argument_list|)
expr_stmt|;
name|grid
operator|.
name|setWidget
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
name|parent
argument_list|)
expr_stmt|;
name|grid
operator|.
name|setWidget
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
name|browse
argument_list|)
expr_stmt|;
name|fp
operator|.
name|add
argument_list|(
name|grid
argument_list|)
expr_stmt|;
block|}
DECL|method|doCreateProject ()
specifier|private
name|void
name|doCreateProject
parameter_list|()
block|{
specifier|final
name|String
name|projectName
init|=
name|project
operator|.
name|getText
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
specifier|final
name|String
name|parentName
init|=
name|parent
operator|.
name|getText
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
literal|""
operator|.
name|equals
argument_list|(
name|projectName
argument_list|)
condition|)
block|{
name|project
operator|.
name|setFocus
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return;
block|}
name|enableForm
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|ProjectApi
operator|.
name|createProject
argument_list|(
name|projectName
argument_list|,
name|parentName
argument_list|,
name|emptyCommit
operator|.
name|getValue
argument_list|()
argument_list|,
name|permissionsOnly
operator|.
name|getValue
argument_list|()
argument_list|,
operator|new
name|AsyncCallback
argument_list|<
name|VoidResult
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|VoidResult
name|result
parameter_list|)
block|{
name|String
name|nameWithoutSuffix
init|=
name|ProjectUtil
operator|.
name|stripGitSuffix
argument_list|(
name|projectName
argument_list|)
decl_stmt|;
name|History
operator|.
name|newItem
argument_list|(
name|Dispatcher
operator|.
name|toProjectAdmin
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
name|nameWithoutSuffix
argument_list|)
argument_list|,
name|ProjectScreen
operator|.
name|INFO
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|caught
parameter_list|)
block|{
operator|new
name|ErrorDialog
argument_list|(
name|caught
operator|.
name|getMessage
argument_list|()
argument_list|)
operator|.
name|center
argument_list|()
expr_stmt|;
name|enableForm
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|enableForm (final boolean enabled)
specifier|private
name|void
name|enableForm
parameter_list|(
specifier|final
name|boolean
name|enabled
parameter_list|)
block|{
name|project
operator|.
name|setEnabled
argument_list|(
name|enabled
argument_list|)
expr_stmt|;
name|create
operator|.
name|setEnabled
argument_list|(
name|enabled
argument_list|)
expr_stmt|;
name|parent
operator|.
name|setEnabled
argument_list|(
name|enabled
argument_list|)
expr_stmt|;
name|emptyCommit
operator|.
name|setEnabled
argument_list|(
name|enabled
argument_list|)
expr_stmt|;
name|permissionsOnly
operator|.
name|setEnabled
argument_list|(
name|enabled
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

