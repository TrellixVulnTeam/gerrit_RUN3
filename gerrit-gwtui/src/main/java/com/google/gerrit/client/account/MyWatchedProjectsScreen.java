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
DECL|package|com.google.gerrit.client.account
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|account
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
name|HintTextBox
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
name|data
operator|.
name|AccountProjectWatchInfo
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
name|HTMLTable
operator|.
name|CellFormatter
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
name|SuggestBox
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
name|SuggestBox
operator|.
name|DefaultSuggestionDisplay
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
name|SuggestOracle
operator|.
name|Suggestion
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
DECL|class|MyWatchedProjectsScreen
specifier|public
class|class
name|MyWatchedProjectsScreen
extends|extends
name|SettingsScreen
block|{
DECL|field|addNew
specifier|private
name|Button
name|addNew
decl_stmt|;
DECL|field|nameBox
specifier|private
name|HintTextBox
name|nameBox
decl_stmt|;
DECL|field|nameTxt
specifier|private
name|SuggestBox
name|nameTxt
decl_stmt|;
DECL|field|filterTxt
specifier|private
name|HintTextBox
name|filterTxt
decl_stmt|;
DECL|field|watchesTab
specifier|private
name|MyWatchesTable
name|watchesTab
decl_stmt|;
DECL|field|browse
specifier|private
name|Button
name|browse
decl_stmt|;
DECL|field|delSel
specifier|private
name|Button
name|delSel
decl_stmt|;
DECL|field|submitOnSelection
specifier|private
name|boolean
name|submitOnSelection
decl_stmt|;
DECL|field|grid
specifier|private
name|Grid
name|grid
decl_stmt|;
DECL|field|projectsPopup
specifier|private
name|ProjectListPopup
name|projectsPopup
decl_stmt|;
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
name|createWidgets
argument_list|()
expr_stmt|;
comment|/* top table */
name|grid
operator|=
operator|new
name|Grid
argument_list|(
literal|2
argument_list|,
literal|2
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
name|Util
operator|.
name|C
operator|.
name|watchedProjectName
argument_list|()
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
name|nameTxt
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
name|Util
operator|.
name|C
operator|.
name|watchedProjectFilter
argument_list|()
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
name|filterTxt
argument_list|)
expr_stmt|;
specifier|final
name|CellFormatter
name|fmt
init|=
name|grid
operator|.
name|getCellFormatter
argument_list|()
decl_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|topmost
argument_list|()
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|topmost
argument_list|()
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|header
argument_list|()
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|header
argument_list|()
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|bottomheader
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|FlowPanel
name|fp
init|=
operator|new
name|FlowPanel
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
name|addWatchPanel
argument_list|()
argument_list|)
expr_stmt|;
name|fp
operator|.
name|add
argument_list|(
name|grid
argument_list|)
expr_stmt|;
name|fp
operator|.
name|add
argument_list|(
name|addNew
argument_list|)
expr_stmt|;
name|fp
operator|.
name|add
argument_list|(
name|browse
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|fp
argument_list|)
expr_stmt|;
comment|/* bottom table */
name|add
argument_list|(
name|watchesTab
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|delSel
argument_list|)
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
name|nameBox
operator|.
name|getText
argument_list|()
argument_list|)
condition|)
block|{
name|nameBox
operator|.
name|setText
argument_list|(
name|projectName
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|openRow
parameter_list|(
name|String
name|projectName
parameter_list|)
block|{
name|nameBox
operator|.
name|setText
argument_list|(
name|projectName
argument_list|)
expr_stmt|;
name|doAddNew
argument_list|()
expr_stmt|;
block|}
block|}
expr_stmt|;
name|projectsPopup
operator|.
name|initPopup
argument_list|(
name|Util
operator|.
name|C
operator|.
name|projects
argument_list|()
argument_list|,
name|PageLinks
operator|.
name|SETTINGS_PROJECTS
argument_list|)
expr_stmt|;
block|}
DECL|method|createWidgets ()
specifier|protected
name|void
name|createWidgets
parameter_list|()
block|{
name|nameBox
operator|=
operator|new
name|HintTextBox
argument_list|()
expr_stmt|;
name|nameTxt
operator|=
operator|new
name|SuggestBox
argument_list|(
operator|new
name|ProjectNameSuggestOracle
argument_list|()
argument_list|,
name|nameBox
argument_list|)
expr_stmt|;
name|nameBox
operator|.
name|setVisibleLength
argument_list|(
literal|50
argument_list|)
expr_stmt|;
name|nameBox
operator|.
name|setHintText
argument_list|(
name|Util
operator|.
name|C
operator|.
name|defaultProjectName
argument_list|()
argument_list|)
expr_stmt|;
name|nameBox
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
name|submitOnSelection
operator|=
literal|false
expr_stmt|;
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
if|if
condition|(
operator|(
operator|(
name|DefaultSuggestionDisplay
operator|)
name|nameTxt
operator|.
name|getSuggestionDisplay
argument_list|()
operator|)
operator|.
name|isSuggestionListShowing
argument_list|()
condition|)
block|{
name|submitOnSelection
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|doAddNew
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|nameTxt
operator|.
name|addSelectionHandler
argument_list|(
operator|new
name|SelectionHandler
argument_list|<
name|Suggestion
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSelection
parameter_list|(
name|SelectionEvent
argument_list|<
name|Suggestion
argument_list|>
name|event
parameter_list|)
block|{
if|if
condition|(
name|submitOnSelection
condition|)
block|{
name|submitOnSelection
operator|=
literal|false
expr_stmt|;
name|doAddNew
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|filterTxt
operator|=
operator|new
name|HintTextBox
argument_list|()
expr_stmt|;
name|filterTxt
operator|.
name|setVisibleLength
argument_list|(
literal|50
argument_list|)
expr_stmt|;
name|filterTxt
operator|.
name|setHintText
argument_list|(
name|Util
operator|.
name|C
operator|.
name|defaultFilter
argument_list|()
argument_list|)
expr_stmt|;
name|filterTxt
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
name|doAddNew
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|addNew
operator|=
operator|new
name|Button
argument_list|(
name|Util
operator|.
name|C
operator|.
name|buttonWatchProject
argument_list|()
argument_list|)
expr_stmt|;
name|addNew
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
name|doAddNew
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
name|Util
operator|.
name|C
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
name|watchesTab
operator|.
name|getAbsoluteLeft
argument_list|()
operator|+
name|watchesTab
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
name|watchesTab
operator|=
operator|new
name|MyWatchesTable
argument_list|()
expr_stmt|;
name|delSel
operator|=
operator|new
name|Button
argument_list|(
name|Util
operator|.
name|C
operator|.
name|buttonDeleteSshKey
argument_list|()
argument_list|)
expr_stmt|;
name|delSel
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
name|watchesTab
operator|.
name|deleteChecked
argument_list|()
expr_stmt|;
block|}
block|}
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
name|populateWatches
argument_list|()
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
DECL|method|doAddNew ()
specifier|protected
name|void
name|doAddNew
parameter_list|()
block|{
specifier|final
name|String
name|projectName
init|=
name|nameTxt
operator|.
name|getText
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
return|return;
block|}
name|String
name|filter
init|=
name|filterTxt
operator|.
name|getText
argument_list|()
decl_stmt|;
if|if
condition|(
name|filter
operator|==
literal|null
operator|||
name|filter
operator|.
name|isEmpty
argument_list|()
operator|||
name|filter
operator|.
name|equals
argument_list|(
name|Util
operator|.
name|C
operator|.
name|defaultFilter
argument_list|()
argument_list|)
condition|)
block|{
name|filter
operator|=
literal|null
expr_stmt|;
block|}
name|addNew
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|nameBox
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|filterTxt
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Util
operator|.
name|ACCOUNT_SVC
operator|.
name|addProjectWatch
argument_list|(
name|projectName
argument_list|,
name|filter
argument_list|,
operator|new
name|GerritCallback
argument_list|<
name|AccountProjectWatchInfo
argument_list|>
argument_list|()
block|{
specifier|public
name|void
name|onSuccess
parameter_list|(
specifier|final
name|AccountProjectWatchInfo
name|result
parameter_list|)
block|{
name|addNew
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|nameBox
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|filterTxt
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|nameTxt
operator|.
name|setText
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|watchesTab
operator|.
name|insertWatch
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
specifier|final
name|Throwable
name|caught
parameter_list|)
block|{
name|addNew
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|nameBox
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|filterTxt
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|super
operator|.
name|onFailure
argument_list|(
name|caught
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|populateWatches ()
specifier|protected
name|void
name|populateWatches
parameter_list|()
block|{
name|Util
operator|.
name|ACCOUNT_SVC
operator|.
name|myProjectWatch
argument_list|(
operator|new
name|ScreenLoadCallback
argument_list|<
name|List
argument_list|<
name|AccountProjectWatchInfo
argument_list|>
argument_list|>
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|preDisplay
parameter_list|(
specifier|final
name|List
argument_list|<
name|AccountProjectWatchInfo
argument_list|>
name|result
parameter_list|)
block|{
name|watchesTab
operator|.
name|display
argument_list|(
name|result
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

