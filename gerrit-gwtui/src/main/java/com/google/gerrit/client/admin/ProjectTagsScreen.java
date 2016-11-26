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
name|client
operator|.
name|ui
operator|.
name|Util
operator|.
name|highlight
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
name|ConfirmationCallback
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
name|ConfirmationDialog
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
name|access
operator|.
name|AccessMap
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
name|access
operator|.
name|ProjectAccessInfo
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
name|TagInfo
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
name|Natives
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
name|Hyperlink
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
name|PagingHyperlink
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
name|JsArray
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
name|event
operator|.
name|dom
operator|.
name|client
operator|.
name|KeyUpEvent
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
name|KeyUpHandler
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
name|HorizontalPanel
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
name|InlineHTML
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
name|Label
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
name|TextBox
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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtexpui
operator|.
name|safehtml
operator|.
name|client
operator|.
name|SafeHtmlBuilder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_class
DECL|class|ProjectTagsScreen
specifier|public
class|class
name|ProjectTagsScreen
extends|extends
name|PaginatedProjectScreen
block|{
DECL|field|prev
specifier|private
name|Hyperlink
name|prev
decl_stmt|;
DECL|field|next
specifier|private
name|Hyperlink
name|next
decl_stmt|;
DECL|field|tagTable
specifier|private
name|TagsTable
name|tagTable
decl_stmt|;
DECL|field|addTag
specifier|private
name|Button
name|addTag
decl_stmt|;
DECL|field|nameTxtBox
specifier|private
name|HintTextBox
name|nameTxtBox
decl_stmt|;
DECL|field|irevTxtBox
specifier|private
name|HintTextBox
name|irevTxtBox
decl_stmt|;
DECL|field|addPanel
specifier|private
name|FlowPanel
name|addPanel
decl_stmt|;
DECL|field|filterTxt
specifier|private
name|NpTextBox
name|filterTxt
decl_stmt|;
DECL|field|query
specifier|private
name|Query
name|query
decl_stmt|;
DECL|method|ProjectTagsScreen (Project.NameKey toShow)
specifier|public
name|ProjectTagsScreen
parameter_list|(
name|Project
operator|.
name|NameKey
name|toShow
parameter_list|)
block|{
name|super
argument_list|(
name|toShow
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getScreenToken ()
specifier|public
name|String
name|getScreenToken
parameter_list|()
block|{
return|return
name|PageLinks
operator|.
name|toProjectTags
argument_list|(
name|getProjectKey
argument_list|()
argument_list|)
return|;
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
name|addPanel
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|AccessMap
operator|.
name|get
argument_list|(
name|getProjectKey
argument_list|()
argument_list|,
operator|new
name|GerritCallback
argument_list|<
name|ProjectAccessInfo
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|ProjectAccessInfo
name|result
parameter_list|)
block|{
name|addPanel
operator|.
name|setVisible
argument_list|(
name|result
operator|.
name|canAddRefs
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|Query
argument_list|(
name|match
argument_list|)
operator|.
name|start
argument_list|(
name|start
argument_list|)
operator|.
name|run
argument_list|()
expr_stmt|;
name|savedPanel
operator|=
name|TAGS
expr_stmt|;
block|}
DECL|method|updateForm ()
specifier|private
name|void
name|updateForm
parameter_list|()
block|{
name|addTag
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|nameTxtBox
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|irevTxtBox
operator|.
name|setEnabled
argument_list|(
literal|true
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
name|initPageHeader
argument_list|()
expr_stmt|;
name|prev
operator|=
name|PagingHyperlink
operator|.
name|createPrev
argument_list|()
expr_stmt|;
name|prev
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|next
operator|=
name|PagingHyperlink
operator|.
name|createNext
argument_list|()
expr_stmt|;
name|next
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|addPanel
operator|=
operator|new
name|FlowPanel
argument_list|()
expr_stmt|;
name|Grid
name|addGrid
init|=
operator|new
name|Grid
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|addGrid
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
name|addBranch
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|texBoxLength
init|=
literal|50
decl_stmt|;
name|nameTxtBox
operator|=
operator|new
name|HintTextBox
argument_list|()
expr_stmt|;
name|nameTxtBox
operator|.
name|setVisibleLength
argument_list|(
name|texBoxLength
argument_list|)
expr_stmt|;
name|nameTxtBox
operator|.
name|setHintText
argument_list|(
name|AdminConstants
operator|.
name|I
operator|.
name|defaultTagName
argument_list|()
argument_list|)
expr_stmt|;
name|nameTxtBox
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
name|doAddNewTag
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|addGrid
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
name|columnTagName
argument_list|()
operator|+
literal|":"
argument_list|)
expr_stmt|;
name|addGrid
operator|.
name|setWidget
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
name|nameTxtBox
argument_list|)
expr_stmt|;
name|irevTxtBox
operator|=
operator|new
name|HintTextBox
argument_list|()
expr_stmt|;
name|irevTxtBox
operator|.
name|setVisibleLength
argument_list|(
name|texBoxLength
argument_list|)
expr_stmt|;
name|irevTxtBox
operator|.
name|setHintText
argument_list|(
name|AdminConstants
operator|.
name|I
operator|.
name|defaultRevisionSpec
argument_list|()
argument_list|)
expr_stmt|;
name|irevTxtBox
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
name|doAddNewTag
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|addGrid
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
name|initialRevision
argument_list|()
operator|+
literal|":"
argument_list|)
expr_stmt|;
name|addGrid
operator|.
name|setWidget
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
name|irevTxtBox
argument_list|)
expr_stmt|;
name|addTag
operator|=
operator|new
name|Button
argument_list|(
name|AdminConstants
operator|.
name|I
operator|.
name|buttonAddTag
argument_list|()
argument_list|)
expr_stmt|;
name|addTag
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
name|doAddNewTag
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|addPanel
operator|.
name|add
argument_list|(
name|addGrid
argument_list|)
expr_stmt|;
name|addPanel
operator|.
name|add
argument_list|(
name|addTag
argument_list|)
expr_stmt|;
name|tagTable
operator|=
operator|new
name|TagsTable
argument_list|()
expr_stmt|;
name|HorizontalPanel
name|buttons
init|=
operator|new
name|HorizontalPanel
argument_list|()
decl_stmt|;
name|buttons
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
name|branchTablePrevNextLinks
argument_list|()
argument_list|)
expr_stmt|;
name|buttons
operator|.
name|add
argument_list|(
name|prev
argument_list|)
expr_stmt|;
name|buttons
operator|.
name|add
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|tagTable
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|buttons
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|addPanel
argument_list|)
expr_stmt|;
block|}
DECL|method|initPageHeader ()
specifier|private
name|void
name|initPageHeader
parameter_list|()
block|{
name|parseToken
argument_list|()
expr_stmt|;
name|HorizontalPanel
name|hp
init|=
operator|new
name|HorizontalPanel
argument_list|()
decl_stmt|;
name|hp
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
name|projectFilterPanel
argument_list|()
argument_list|)
expr_stmt|;
name|Label
name|filterLabel
init|=
operator|new
name|Label
argument_list|(
name|AdminConstants
operator|.
name|I
operator|.
name|projectFilter
argument_list|()
argument_list|)
decl_stmt|;
name|filterLabel
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
name|projectFilterLabel
argument_list|()
argument_list|)
expr_stmt|;
name|hp
operator|.
name|add
argument_list|(
name|filterLabel
argument_list|)
expr_stmt|;
name|filterTxt
operator|=
operator|new
name|NpTextBox
argument_list|()
expr_stmt|;
name|filterTxt
operator|.
name|setValue
argument_list|(
name|match
argument_list|)
expr_stmt|;
name|filterTxt
operator|.
name|addKeyUpHandler
argument_list|(
operator|new
name|KeyUpHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onKeyUp
parameter_list|(
name|KeyUpEvent
name|event
parameter_list|)
block|{
name|Query
name|q
init|=
operator|new
name|Query
argument_list|(
name|filterTxt
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|match
operator|.
name|equals
argument_list|(
name|q
operator|.
name|qMatch
argument_list|)
condition|)
block|{
name|q
operator|.
name|start
argument_list|(
name|start
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|query
operator|==
literal|null
condition|)
block|{
name|q
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
name|query
operator|=
name|q
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|hp
operator|.
name|add
argument_list|(
name|filterTxt
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|hp
argument_list|)
expr_stmt|;
block|}
DECL|method|doAddNewTag ()
specifier|private
name|void
name|doAddNewTag
parameter_list|()
block|{
name|String
name|tagName
init|=
name|nameTxtBox
operator|.
name|getText
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|tagName
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|nameTxtBox
operator|.
name|setFocus
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
name|rev
init|=
name|irevTxtBox
operator|.
name|getText
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|rev
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|irevTxtBox
operator|.
name|setText
argument_list|(
literal|"HEAD"
argument_list|)
expr_stmt|;
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
name|irevTxtBox
operator|.
name|selectAll
argument_list|()
expr_stmt|;
name|irevTxtBox
operator|.
name|setFocus
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return;
block|}
name|addTag
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|ProjectApi
operator|.
name|createTag
argument_list|(
name|getProjectKey
argument_list|()
argument_list|,
name|tagName
argument_list|,
name|rev
argument_list|,
operator|new
name|GerritCallback
argument_list|<
name|TagInfo
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|TagInfo
name|tag
parameter_list|)
block|{
name|showAddedTag
argument_list|(
name|tag
argument_list|)
expr_stmt|;
name|nameTxtBox
operator|.
name|setText
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|irevTxtBox
operator|.
name|setText
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|Query
argument_list|(
name|match
argument_list|)
operator|.
name|start
argument_list|(
name|start
argument_list|)
operator|.
name|run
argument_list|()
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
name|addTag
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|selectAllAndFocus
argument_list|(
name|nameTxtBox
argument_list|)
expr_stmt|;
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
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|showAddedTag (TagInfo tag)
name|void
name|showAddedTag
parameter_list|(
name|TagInfo
name|tag
parameter_list|)
block|{
name|SafeHtmlBuilder
name|b
init|=
operator|new
name|SafeHtmlBuilder
argument_list|()
decl_stmt|;
name|b
operator|.
name|openElement
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|Gerrit
operator|.
name|C
operator|.
name|tagCreationConfirmationMessage
argument_list|()
argument_list|)
expr_stmt|;
name|b
operator|.
name|closeElement
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|b
operator|.
name|openElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|tag
operator|.
name|ref
argument_list|()
argument_list|)
expr_stmt|;
name|b
operator|.
name|closeElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
name|ConfirmationDialog
name|confirmationDialog
init|=
operator|new
name|ConfirmationDialog
argument_list|(
name|Gerrit
operator|.
name|C
operator|.
name|tagCreationDialogTitle
argument_list|()
argument_list|,
name|b
operator|.
name|toSafeHtml
argument_list|()
argument_list|,
operator|new
name|ConfirmationCallback
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onOk
parameter_list|()
block|{
comment|//do nothing
block|}
block|}
argument_list|)
decl_stmt|;
name|confirmationDialog
operator|.
name|center
argument_list|()
expr_stmt|;
name|confirmationDialog
operator|.
name|setCancelVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|selectAllAndFocus (TextBox textBox)
specifier|private
specifier|static
name|void
name|selectAllAndFocus
parameter_list|(
name|TextBox
name|textBox
parameter_list|)
block|{
name|textBox
operator|.
name|selectAll
argument_list|()
expr_stmt|;
name|textBox
operator|.
name|setFocus
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|class|TagsTable
specifier|private
class|class
name|TagsTable
extends|extends
name|NavigationTable
argument_list|<
name|TagInfo
argument_list|>
block|{
DECL|method|TagsTable ()
name|TagsTable
parameter_list|()
block|{
name|table
operator|.
name|setWidth
argument_list|(
literal|""
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
name|AdminConstants
operator|.
name|I
operator|.
name|columnTagName
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|setText
argument_list|(
literal|0
argument_list|,
literal|3
argument_list|,
name|AdminConstants
operator|.
name|I
operator|.
name|columnTagRevision
argument_list|()
argument_list|)
expr_stmt|;
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
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|iconHeader
argument_list|()
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
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|dataHeader
argument_list|()
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
literal|0
argument_list|,
literal|3
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|dataHeader
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getCheckedRefs ()
name|Set
argument_list|<
name|String
argument_list|>
name|getCheckedRefs
parameter_list|()
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|refs
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|row
init|=
literal|1
init|;
name|row
operator|<
name|table
operator|.
name|getRowCount
argument_list|()
condition|;
name|row
operator|++
control|)
block|{
name|TagInfo
name|k
init|=
name|getRowItem
argument_list|(
name|row
argument_list|)
decl_stmt|;
if|if
condition|(
name|k
operator|!=
literal|null
operator|&&
name|table
operator|.
name|getWidget
argument_list|(
name|row
argument_list|,
literal|1
argument_list|)
operator|instanceof
name|CheckBox
operator|&&
operator|(
operator|(
name|CheckBox
operator|)
name|table
operator|.
name|getWidget
argument_list|(
name|row
argument_list|,
literal|1
argument_list|)
operator|)
operator|.
name|getValue
argument_list|()
condition|)
block|{
name|refs
operator|.
name|add
argument_list|(
name|k
operator|.
name|ref
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|refs
return|;
block|}
DECL|method|setChecked (Set<String> refs)
name|void
name|setChecked
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|refs
parameter_list|)
block|{
for|for
control|(
name|int
name|row
init|=
literal|1
init|;
name|row
operator|<
name|table
operator|.
name|getRowCount
argument_list|()
condition|;
name|row
operator|++
control|)
block|{
name|TagInfo
name|k
init|=
name|getRowItem
argument_list|(
name|row
argument_list|)
decl_stmt|;
if|if
condition|(
name|k
operator|!=
literal|null
operator|&&
name|refs
operator|.
name|contains
argument_list|(
name|k
operator|.
name|ref
argument_list|()
argument_list|)
operator|&&
name|table
operator|.
name|getWidget
argument_list|(
name|row
argument_list|,
literal|1
argument_list|)
operator|instanceof
name|CheckBox
condition|)
block|{
operator|(
operator|(
name|CheckBox
operator|)
name|table
operator|.
name|getWidget
argument_list|(
name|row
argument_list|,
literal|1
argument_list|)
operator|)
operator|.
name|setValue
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|display (List<TagInfo> tags)
name|void
name|display
parameter_list|(
name|List
argument_list|<
name|TagInfo
argument_list|>
name|tags
parameter_list|)
block|{
name|displaySubset
argument_list|(
name|tags
argument_list|,
literal|0
argument_list|,
name|tags
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|displaySubset (List<TagInfo> tags, int fromIndex, int toIndex)
name|void
name|displaySubset
parameter_list|(
name|List
argument_list|<
name|TagInfo
argument_list|>
name|tags
parameter_list|,
name|int
name|fromIndex
parameter_list|,
name|int
name|toIndex
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
block|{
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
block|}
for|for
control|(
name|TagInfo
name|k
range|:
name|tags
operator|.
name|subList
argument_list|(
name|fromIndex
argument_list|,
name|toIndex
argument_list|)
control|)
block|{
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
DECL|method|populate (int row, TagInfo k)
name|void
name|populate
parameter_list|(
name|int
name|row
parameter_list|,
name|TagInfo
name|k
parameter_list|)
block|{
name|table
operator|.
name|setText
argument_list|(
name|row
argument_list|,
literal|1
argument_list|,
literal|""
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
operator|new
name|InlineHTML
argument_list|(
name|highlight
argument_list|(
name|k
operator|.
name|getShortName
argument_list|()
argument_list|,
name|match
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|k
operator|.
name|revision
argument_list|()
operator|!=
literal|null
condition|)
block|{
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
name|revision
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|table
operator|.
name|setText
argument_list|(
name|row
argument_list|,
literal|3
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
name|FlexCellFormatter
name|fmt
init|=
name|table
operator|.
name|getFlexCellFormatter
argument_list|()
decl_stmt|;
name|String
name|iconCellStyle
init|=
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|iconCell
argument_list|()
decl_stmt|;
name|String
name|dataCellStyle
init|=
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|dataCell
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
name|iconCellStyle
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
name|dataCellStyle
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
name|row
argument_list|,
literal|3
argument_list|,
name|dataCellStyle
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
annotation|@
name|Override
DECL|method|onOpenRow (int row)
specifier|protected
name|void
name|onOpenRow
parameter_list|(
name|int
name|row
parameter_list|)
block|{
if|if
condition|(
name|row
operator|>
literal|0
condition|)
block|{
name|movePointerTo
argument_list|(
name|row
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getRowItemKey (TagInfo item)
specifier|protected
name|Object
name|getRowItemKey
parameter_list|(
name|TagInfo
name|item
parameter_list|)
block|{
return|return
name|item
operator|.
name|ref
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|onShowView ()
specifier|public
name|void
name|onShowView
parameter_list|()
block|{
name|super
operator|.
name|onShowView
argument_list|()
expr_stmt|;
if|if
condition|(
name|match
operator|!=
literal|null
condition|)
block|{
name|filterTxt
operator|.
name|setCursorPos
argument_list|(
name|match
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|filterTxt
operator|.
name|setFocus
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|class|Query
specifier|private
class|class
name|Query
block|{
DECL|field|qMatch
specifier|private
name|String
name|qMatch
decl_stmt|;
DECL|field|qStart
specifier|private
name|int
name|qStart
decl_stmt|;
DECL|method|Query (String match)
name|Query
parameter_list|(
name|String
name|match
parameter_list|)
block|{
name|this
operator|.
name|qMatch
operator|=
name|match
expr_stmt|;
block|}
DECL|method|start (int start)
name|Query
name|start
parameter_list|(
name|int
name|start
parameter_list|)
block|{
name|this
operator|.
name|qStart
operator|=
name|start
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|run ()
name|Query
name|run
parameter_list|()
block|{
comment|// Retrieve one more tag than page size to determine if there are more
comment|// tags to display
name|ProjectApi
operator|.
name|getTags
argument_list|(
name|getProjectKey
argument_list|()
argument_list|,
name|pageSize
operator|+
literal|1
argument_list|,
name|qStart
argument_list|,
name|qMatch
argument_list|,
operator|new
name|ScreenLoadCallback
argument_list|<
name|JsArray
argument_list|<
name|TagInfo
argument_list|>
argument_list|>
argument_list|(
name|ProjectTagsScreen
operator|.
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|preDisplay
parameter_list|(
name|JsArray
argument_list|<
name|TagInfo
argument_list|>
name|result
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isAttached
argument_list|()
condition|)
block|{
comment|// View has been disposed.
block|}
elseif|else
if|if
condition|(
name|query
operator|==
name|Query
operator|.
name|this
condition|)
block|{
name|query
operator|=
literal|null
expr_stmt|;
name|showList
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|query
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|showList (JsArray<TagInfo> result)
name|void
name|showList
parameter_list|(
name|JsArray
argument_list|<
name|TagInfo
argument_list|>
name|result
parameter_list|)
block|{
name|setToken
argument_list|(
name|getTokenForScreen
argument_list|(
name|qMatch
argument_list|,
name|qStart
argument_list|)
argument_list|)
expr_stmt|;
name|ProjectTagsScreen
operator|.
name|this
operator|.
name|match
operator|=
name|qMatch
expr_stmt|;
name|ProjectTagsScreen
operator|.
name|this
operator|.
name|start
operator|=
name|qStart
expr_stmt|;
if|if
condition|(
name|result
operator|.
name|length
argument_list|()
operator|<=
name|pageSize
condition|)
block|{
name|tagTable
operator|.
name|display
argument_list|(
name|Natives
operator|.
name|asList
argument_list|(
name|result
argument_list|)
argument_list|)
expr_stmt|;
name|next
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tagTable
operator|.
name|displaySubset
argument_list|(
name|Natives
operator|.
name|asList
argument_list|(
name|result
argument_list|)
argument_list|,
literal|0
argument_list|,
name|result
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|setupNavigationLink
argument_list|(
name|next
argument_list|,
name|qMatch
argument_list|,
name|qStart
operator|+
name|pageSize
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|qStart
operator|>
literal|0
condition|)
block|{
name|setupNavigationLink
argument_list|(
name|prev
argument_list|,
name|qMatch
argument_list|,
name|qStart
operator|-
name|pageSize
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|prev
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|checkedRefs
init|=
name|tagTable
operator|.
name|getCheckedRefs
argument_list|()
decl_stmt|;
name|tagTable
operator|.
name|setChecked
argument_list|(
name|checkedRefs
argument_list|)
expr_stmt|;
name|updateForm
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|isCurrentView
argument_list|()
condition|)
block|{
name|display
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

