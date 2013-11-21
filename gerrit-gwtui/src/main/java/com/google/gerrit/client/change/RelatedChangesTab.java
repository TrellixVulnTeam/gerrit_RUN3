begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2013 The Android Open Source Project
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
DECL|package|com.google.gerrit.client.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
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
name|GitwebLink
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
name|change
operator|.
name|RelatedChanges
operator|.
name|ChangeAndCommit
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
name|PatchSet
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
name|RepeatingCommand
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
name|dom
operator|.
name|client
operator|.
name|NativeEvent
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
name|DOM
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
name|EventListener
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
name|InlineLabel
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
name|ScrollPanel
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
name|impl
operator|.
name|HyperlinkImpl
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
name|progress
operator|.
name|client
operator|.
name|ProgressBar
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

begin_class
DECL|class|RelatedChangesTab
class|class
name|RelatedChangesTab
block|{
DECL|field|OPEN
specifier|private
specifier|static
specifier|final
name|String
name|OPEN
decl_stmt|;
DECL|field|link
specifier|private
specifier|static
specifier|final
name|HyperlinkImpl
name|link
init|=
name|GWT
operator|.
name|create
argument_list|(
name|HyperlinkImpl
operator|.
name|class
argument_list|)
decl_stmt|;
static|static
block|{
name|OPEN
operator|=
name|DOM
operator|.
name|createUniqueId
argument_list|()
operator|.
name|replace
argument_list|(
literal|'-'
argument_list|,
literal|'_'
argument_list|)
expr_stmt|;
name|init
argument_list|(
name|OPEN
argument_list|)
expr_stmt|;
block|}
DECL|method|init (String o)
specifier|private
specifier|static
specifier|final
specifier|native
name|void
name|init
parameter_list|(
name|String
name|o
parameter_list|)
comment|/*-{     $wnd[o] = $entry(function(e,i) {       return @com.google.gerrit.client.change.RelatedChangesTab::onOpen(Lcom/google/gwt/dom/client/NativeEvent;I)(e,i);     });   }-*/
function_decl|;
DECL|method|onOpen (NativeEvent e, int idx)
specifier|private
specifier|static
name|boolean
name|onOpen
parameter_list|(
name|NativeEvent
name|e
parameter_list|,
name|int
name|idx
parameter_list|)
block|{
if|if
condition|(
name|link
operator|.
name|handleAsClick
argument_list|(
name|e
operator|.
expr|<
name|Event
operator|>
name|cast
argument_list|()
argument_list|)
condition|)
block|{
name|MyTable
name|t
init|=
name|getMyTable
argument_list|(
name|e
argument_list|)
decl_stmt|;
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
name|t
operator|.
name|onOpenRow
argument_list|(
name|idx
argument_list|)
expr_stmt|;
name|e
operator|.
name|preventDefault
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|getMyTable (NativeEvent event)
specifier|private
specifier|static
name|MyTable
name|getMyTable
parameter_list|(
name|NativeEvent
name|event
parameter_list|)
block|{
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
name|Element
name|e
init|=
name|event
operator|.
name|getEventTarget
argument_list|()
operator|.
name|cast
argument_list|()
decl_stmt|;
for|for
control|(
name|e
operator|=
name|DOM
operator|.
name|getParent
argument_list|(
name|e
argument_list|)
init|;
name|e
operator|!=
literal|null
condition|;
name|e
operator|=
name|DOM
operator|.
name|getParent
argument_list|(
name|e
argument_list|)
control|)
block|{
name|EventListener
name|l
init|=
name|DOM
operator|.
name|getEventListener
argument_list|(
name|e
argument_list|)
decl_stmt|;
if|if
condition|(
name|l
operator|instanceof
name|MyTable
condition|)
block|{
return|return
operator|(
name|MyTable
operator|)
name|l
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|field|parent
specifier|private
specifier|final
name|RelatedChanges
name|parent
decl_stmt|;
DECL|field|index
specifier|private
specifier|final
name|int
name|index
decl_stmt|;
DECL|field|panel
specifier|private
specifier|final
name|FlowPanel
name|panel
decl_stmt|;
DECL|field|scroll
specifier|private
specifier|final
name|ScrollPanel
name|scroll
decl_stmt|;
DECL|field|progress
specifier|private
specifier|final
name|ProgressBar
name|progress
decl_stmt|;
DECL|field|project
specifier|private
name|String
name|project
decl_stmt|;
DECL|field|table
specifier|private
name|MyTable
name|table
decl_stmt|;
DECL|field|register
specifier|private
name|boolean
name|register
decl_stmt|;
DECL|method|RelatedChangesTab (RelatedChanges parent, int index, FlowPanel panel)
name|RelatedChangesTab
parameter_list|(
name|RelatedChanges
name|parent
parameter_list|,
name|int
name|index
parameter_list|,
name|FlowPanel
name|panel
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|panel
operator|=
name|panel
expr_stmt|;
name|scroll
operator|=
operator|new
name|ScrollPanel
argument_list|()
expr_stmt|;
name|scroll
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|panel
operator|.
name|add
argument_list|(
name|scroll
argument_list|)
expr_stmt|;
name|progress
operator|=
operator|new
name|ProgressBar
argument_list|()
expr_stmt|;
name|panel
operator|.
name|add
argument_list|(
name|progress
argument_list|)
expr_stmt|;
block|}
DECL|method|setTitle (String title)
name|void
name|setTitle
parameter_list|(
name|String
name|title
parameter_list|)
block|{
name|parent
operator|.
name|setTabTitle
argument_list|(
name|index
argument_list|,
name|title
argument_list|)
expr_stmt|;
block|}
DECL|method|setChanges (String project, String revision, JsArray<ChangeAndCommit> changes)
name|void
name|setChanges
parameter_list|(
name|String
name|project
parameter_list|,
name|String
name|revision
parameter_list|,
name|JsArray
argument_list|<
name|ChangeAndCommit
argument_list|>
name|changes
parameter_list|)
block|{
name|this
operator|.
name|project
operator|=
name|project
expr_stmt|;
name|render
argument_list|(
name|revision
argument_list|,
name|changes
argument_list|)
expr_stmt|;
block|}
DECL|method|setError (String message)
name|void
name|setError
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|progress
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|scroll
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|panel
operator|.
name|add
argument_list|(
operator|new
name|InlineLabel
argument_list|(
name|message
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|setMaxHeight (int height)
name|void
name|setMaxHeight
parameter_list|(
name|int
name|height
parameter_list|)
block|{
name|scroll
operator|.
name|setHeight
argument_list|(
name|height
operator|+
literal|"px"
argument_list|)
expr_stmt|;
block|}
DECL|method|registerKeys (boolean on)
name|void
name|registerKeys
parameter_list|(
name|boolean
name|on
parameter_list|)
block|{
name|register
operator|=
name|on
expr_stmt|;
if|if
condition|(
name|table
operator|!=
literal|null
condition|)
block|{
name|table
operator|.
name|setRegisterKeys
argument_list|(
name|on
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|render (String revision, JsArray<ChangeAndCommit> list)
specifier|private
name|void
name|render
parameter_list|(
name|String
name|revision
parameter_list|,
name|JsArray
argument_list|<
name|ChangeAndCommit
argument_list|>
name|list
parameter_list|)
block|{
if|if
condition|(
literal|0
operator|<
name|list
operator|.
name|length
argument_list|()
condition|)
block|{
name|DisplayCommand
name|cmd
init|=
operator|new
name|DisplayCommand
argument_list|(
name|revision
argument_list|,
name|list
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmd
operator|.
name|execute
argument_list|()
condition|)
block|{
name|Scheduler
operator|.
name|get
argument_list|()
operator|.
name|scheduleIncremental
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|progress
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|panel
operator|.
name|add
argument_list|(
operator|new
name|InlineLabel
argument_list|(
name|Resources
operator|.
name|C
operator|.
name|noChanges
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setTable (MyTable t)
specifier|private
name|void
name|setTable
parameter_list|(
name|MyTable
name|t
parameter_list|)
block|{
name|progress
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|scroll
operator|.
name|clear
argument_list|()
expr_stmt|;
name|scroll
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|scroll
operator|.
name|setVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|table
operator|=
name|t
expr_stmt|;
name|table
operator|.
name|setRegisterKeys
argument_list|(
name|register
argument_list|)
expr_stmt|;
block|}
DECL|method|url (ChangeAndCommit c)
specifier|private
name|String
name|url
parameter_list|(
name|ChangeAndCommit
name|c
parameter_list|)
block|{
if|if
condition|(
name|c
operator|.
name|has_change_number
argument_list|()
operator|&&
name|c
operator|.
name|has_revision_number
argument_list|()
condition|)
block|{
name|PatchSet
operator|.
name|Id
name|id
init|=
name|c
operator|.
name|patch_set_id
argument_list|()
decl_stmt|;
return|return
literal|"#"
operator|+
name|PageLinks
operator|.
name|toChange
argument_list|(
name|id
operator|.
name|getParentKey
argument_list|()
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|id
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
name|GitwebLink
name|gw
init|=
name|Gerrit
operator|.
name|getGitwebLink
argument_list|()
decl_stmt|;
if|if
condition|(
name|gw
operator|!=
literal|null
condition|)
block|{
return|return
name|gw
operator|.
name|toRevision
argument_list|(
name|project
argument_list|,
name|c
operator|.
name|commit
argument_list|()
operator|.
name|commit
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|class|MyTable
specifier|private
class|class
name|MyTable
extends|extends
name|NavigationTable
argument_list|<
name|ChangeAndCommit
argument_list|>
block|{
DECL|field|list
specifier|private
specifier|final
name|JsArray
argument_list|<
name|ChangeAndCommit
argument_list|>
name|list
decl_stmt|;
DECL|method|MyTable (JsArray<ChangeAndCommit> list)
name|MyTable
parameter_list|(
name|JsArray
argument_list|<
name|ChangeAndCommit
argument_list|>
name|list
parameter_list|)
block|{
name|this
operator|.
name|list
operator|=
name|list
expr_stmt|;
name|table
operator|.
name|setWidth
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|keysNavigation
operator|.
name|setName
argument_list|(
name|Gerrit
operator|.
name|C
operator|.
name|sectionNavigation
argument_list|()
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
literal|'K'
argument_list|,
name|Resources
operator|.
name|C
operator|.
name|previousChange
argument_list|()
argument_list|)
argument_list|,
operator|new
name|NextKeyCommand
argument_list|(
literal|0
argument_list|,
literal|'J'
argument_list|,
name|Resources
operator|.
name|C
operator|.
name|nextChange
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
literal|'O'
argument_list|,
name|Resources
operator|.
name|C
operator|.
name|openChange
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getRowItemKey (ChangeAndCommit item)
specifier|protected
name|Object
name|getRowItemKey
parameter_list|(
name|ChangeAndCommit
name|item
parameter_list|)
block|{
return|return
name|item
operator|.
name|id
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getRowItem (int row)
specifier|protected
name|ChangeAndCommit
name|getRowItem
parameter_list|(
name|int
name|row
parameter_list|)
block|{
if|if
condition|(
literal|0
operator|<=
name|row
operator|&&
name|row
operator|<=
name|list
operator|.
name|length
argument_list|()
condition|)
block|{
return|return
name|list
operator|.
name|get
argument_list|(
name|row
argument_list|)
return|;
block|}
return|return
literal|null
return|;
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
literal|0
operator|<=
name|row
operator|&&
name|row
operator|<=
name|list
operator|.
name|length
argument_list|()
condition|)
block|{
name|ChangeAndCommit
name|c
init|=
name|list
operator|.
name|get
argument_list|(
name|row
argument_list|)
decl_stmt|;
name|String
name|url
init|=
name|url
argument_list|(
name|c
argument_list|)
decl_stmt|;
if|if
condition|(
name|url
operator|!=
literal|null
operator|&&
name|url
operator|.
name|startsWith
argument_list|(
literal|"#"
argument_list|)
condition|)
block|{
name|Gerrit
operator|.
name|display
argument_list|(
name|url
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|url
operator|!=
literal|null
condition|)
block|{
name|Window
operator|.
name|Location
operator|.
name|assign
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|selectRow (int select)
name|void
name|selectRow
parameter_list|(
name|int
name|select
parameter_list|)
block|{
name|movePointerTo
argument_list|(
name|select
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|DisplayCommand
specifier|private
specifier|final
class|class
name|DisplayCommand
implements|implements
name|RepeatingCommand
block|{
DECL|field|sb
specifier|private
specifier|final
name|SafeHtmlBuilder
name|sb
init|=
operator|new
name|SafeHtmlBuilder
argument_list|()
decl_stmt|;
DECL|field|table
specifier|private
specifier|final
name|MyTable
name|table
decl_stmt|;
DECL|field|revision
specifier|private
specifier|final
name|String
name|revision
decl_stmt|;
DECL|field|list
specifier|private
specifier|final
name|JsArray
argument_list|<
name|ChangeAndCommit
argument_list|>
name|list
decl_stmt|;
DECL|field|attached
specifier|private
name|boolean
name|attached
decl_stmt|;
DECL|field|row
specifier|private
name|int
name|row
decl_stmt|;
DECL|field|select
specifier|private
name|int
name|select
decl_stmt|;
DECL|field|start
specifier|private
name|double
name|start
decl_stmt|;
DECL|method|DisplayCommand (String revision, JsArray<ChangeAndCommit> list)
specifier|private
name|DisplayCommand
parameter_list|(
name|String
name|revision
parameter_list|,
name|JsArray
argument_list|<
name|ChangeAndCommit
argument_list|>
name|list
parameter_list|)
block|{
name|this
operator|.
name|table
operator|=
operator|new
name|MyTable
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|this
operator|.
name|revision
operator|=
name|revision
expr_stmt|;
name|this
operator|.
name|list
operator|=
name|list
expr_stmt|;
block|}
DECL|method|execute ()
specifier|public
name|boolean
name|execute
parameter_list|()
block|{
name|boolean
name|attachedNow
init|=
name|parent
operator|.
name|isAttached
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|attached
operator|&&
name|attachedNow
condition|)
block|{
comment|// Remember that we have been attached at least once. If
comment|// later we find we aren't attached we should stop running.
name|attached
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|attached
operator|&&
operator|!
name|attachedNow
condition|)
block|{
comment|// If the user navigated away, we aren't in the DOM anymore.
comment|// Don't continue to render.
return|return
literal|false
return|;
block|}
name|start
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
while|while
condition|(
name|row
operator|<
name|list
operator|.
name|length
argument_list|()
condition|)
block|{
name|ChangeAndCommit
name|info
init|=
name|list
operator|.
name|get
argument_list|(
name|row
argument_list|)
decl_stmt|;
if|if
condition|(
name|revision
operator|.
name|equals
argument_list|(
name|info
operator|.
name|commit
argument_list|()
operator|.
name|commit
argument_list|()
argument_list|)
condition|)
block|{
name|select
operator|=
name|row
expr_stmt|;
block|}
name|render
argument_list|(
name|sb
argument_list|,
name|row
argument_list|,
name|info
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
operator|++
name|row
operator|%
literal|10
operator|)
operator|==
literal|0
operator|&&
name|longRunning
argument_list|()
condition|)
block|{
name|updateMeter
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
name|table
operator|.
name|resetHtml
argument_list|(
name|sb
argument_list|)
expr_stmt|;
name|setTable
argument_list|(
name|table
argument_list|)
expr_stmt|;
name|table
operator|.
name|selectRow
argument_list|(
name|select
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
DECL|method|render (SafeHtmlBuilder sb, int row, ChangeAndCommit info)
specifier|private
name|void
name|render
parameter_list|(
name|SafeHtmlBuilder
name|sb
parameter_list|,
name|int
name|row
parameter_list|,
name|ChangeAndCommit
name|info
parameter_list|)
block|{
name|sb
operator|.
name|openTr
argument_list|()
expr_stmt|;
name|sb
operator|.
name|openTd
argument_list|()
operator|.
name|setStyleName
argument_list|(
name|FileTable
operator|.
name|R
operator|.
name|css
argument_list|()
operator|.
name|pointer
argument_list|()
argument_list|)
operator|.
name|closeTd
argument_list|()
expr_stmt|;
name|sb
operator|.
name|openTd
argument_list|()
operator|.
name|addStyleName
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|relatedChangesSubject
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|url
init|=
name|url
argument_list|(
name|info
argument_list|)
decl_stmt|;
if|if
condition|(
name|url
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|openAnchor
argument_list|()
operator|.
name|setAttribute
argument_list|(
literal|"href"
argument_list|,
name|url
argument_list|)
expr_stmt|;
if|if
condition|(
name|url
operator|.
name|startsWith
argument_list|(
literal|"#"
argument_list|)
condition|)
block|{
name|sb
operator|.
name|setAttribute
argument_list|(
literal|"onclick"
argument_list|,
name|OPEN
operator|+
literal|"(event,"
operator|+
name|row
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|info
operator|.
name|commit
argument_list|()
operator|.
name|subject
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|closeAnchor
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|info
operator|.
name|commit
argument_list|()
operator|.
name|subject
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|closeTd
argument_list|()
expr_stmt|;
name|sb
operator|.
name|closeTr
argument_list|()
expr_stmt|;
block|}
DECL|method|updateMeter ()
specifier|private
name|void
name|updateMeter
parameter_list|()
block|{
name|progress
operator|.
name|setValue
argument_list|(
operator|(
literal|100
operator|*
name|row
operator|)
operator|/
name|list
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|longRunning ()
specifier|private
name|boolean
name|longRunning
parameter_list|()
block|{
return|return
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|>
literal|200
return|;
block|}
block|}
block|}
end_class

end_unit

