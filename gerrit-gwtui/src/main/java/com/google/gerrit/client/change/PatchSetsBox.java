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
name|FormatUtil
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
name|changes
operator|.
name|ChangeApi
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
name|changes
operator|.
name|ChangeList
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
name|info
operator|.
name|ChangeInfo
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
name|info
operator|.
name|ChangeInfo
operator|.
name|CommitInfo
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
name|info
operator|.
name|ChangeInfo
operator|.
name|EditInfo
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
name|info
operator|.
name|ChangeInfo
operator|.
name|RevisionInfo
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
name|NativeMap
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
name|RestApi
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
name|FancyFlexTableImpl
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
name|extensions
operator|.
name|client
operator|.
name|ListChangesOption
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
name|Change
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
name|dom
operator|.
name|client
operator|.
name|Element
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
name|resources
operator|.
name|client
operator|.
name|CssResource
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
name|uibinder
operator|.
name|client
operator|.
name|UiBinder
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
name|uibinder
operator|.
name|client
operator|.
name|UiField
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
name|Composite
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
name|HTMLPanel
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
name|PopupPanel
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
name|Widget
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
import|;
end_import

begin_class
DECL|class|PatchSetsBox
class|class
name|PatchSetsBox
extends|extends
name|Composite
block|{
DECL|interface|Binder
interface|interface
name|Binder
extends|extends
name|UiBinder
argument_list|<
name|HTMLPanel
argument_list|,
name|PatchSetsBox
argument_list|>
block|{}
DECL|field|uiBinder
specifier|private
specifier|static
specifier|final
name|Binder
name|uiBinder
init|=
name|GWT
operator|.
name|create
argument_list|(
name|Binder
operator|.
name|class
argument_list|)
decl_stmt|;
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
specifier|native
name|void
name|init
parameter_list|(
name|String
name|o
parameter_list|)
comment|/*-{     $wnd[o] = $entry(function(e,i) {       return @com.google.gerrit.client.change.PatchSetsBox::onOpen(Lcom/google/gwt/dom/client/NativeEvent;I)(e,i);     });   }-*/
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
name|PatchSetsBox
name|t
init|=
name|getRevisionBox
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
DECL|method|getRevisionBox (NativeEvent event)
specifier|private
specifier|static
name|PatchSetsBox
name|getRevisionBox
parameter_list|(
name|NativeEvent
name|event
parameter_list|)
block|{
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
name|PatchSetsBox
condition|)
block|{
return|return
operator|(
name|PatchSetsBox
operator|)
name|l
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|interface|Style
interface|interface
name|Style
extends|extends
name|CssResource
block|{
DECL|method|current ()
name|String
name|current
parameter_list|()
function_decl|;
DECL|method|legacy_id ()
name|String
name|legacy_id
parameter_list|()
function_decl|;
DECL|method|commit ()
name|String
name|commit
parameter_list|()
function_decl|;
DECL|method|draft_comment ()
name|String
name|draft_comment
parameter_list|()
function_decl|;
block|}
DECL|field|changeId
specifier|private
specifier|final
name|Change
operator|.
name|Id
name|changeId
decl_stmt|;
DECL|field|revision
specifier|private
specifier|final
name|String
name|revision
decl_stmt|;
DECL|field|edit
specifier|private
specifier|final
name|EditInfo
name|edit
decl_stmt|;
DECL|field|loaded
specifier|private
name|boolean
name|loaded
decl_stmt|;
DECL|field|revisions
specifier|private
name|JsArray
argument_list|<
name|RevisionInfo
argument_list|>
name|revisions
decl_stmt|;
DECL|field|table
annotation|@
name|UiField
name|FlexTable
name|table
decl_stmt|;
DECL|field|style
annotation|@
name|UiField
name|Style
name|style
decl_stmt|;
DECL|method|PatchSetsBox (Change.Id changeId, String revision, EditInfo edit)
name|PatchSetsBox
parameter_list|(
name|Change
operator|.
name|Id
name|changeId
parameter_list|,
name|String
name|revision
parameter_list|,
name|EditInfo
name|edit
parameter_list|)
block|{
name|this
operator|.
name|changeId
operator|=
name|changeId
expr_stmt|;
name|this
operator|.
name|revision
operator|=
name|revision
expr_stmt|;
name|this
operator|.
name|edit
operator|=
name|edit
expr_stmt|;
name|initWidget
argument_list|(
name|uiBinder
operator|.
name|createAndBindUi
argument_list|(
name|this
argument_list|)
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
if|if
condition|(
operator|!
name|loaded
condition|)
block|{
name|RestApi
name|call
init|=
name|ChangeApi
operator|.
name|detail
argument_list|(
name|changeId
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
name|ChangeList
operator|.
name|addOptions
argument_list|(
name|call
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|ListChangesOption
operator|.
name|ALL_COMMITS
argument_list|,
name|ListChangesOption
operator|.
name|ALL_REVISIONS
argument_list|)
argument_list|)
expr_stmt|;
name|call
operator|.
name|get
argument_list|(
operator|new
name|AsyncCallback
argument_list|<
name|ChangeInfo
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|ChangeInfo
name|result
parameter_list|)
block|{
if|if
condition|(
name|edit
operator|!=
literal|null
condition|)
block|{
name|edit
operator|.
name|setName
argument_list|(
name|edit
operator|.
name|commit
argument_list|()
operator|.
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|revisions
argument_list|()
operator|.
name|put
argument_list|(
name|edit
operator|.
name|name
argument_list|()
argument_list|,
name|RevisionInfo
operator|.
name|fromEdit
argument_list|(
name|edit
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|render
argument_list|(
name|result
operator|.
name|revisions
argument_list|()
argument_list|)
expr_stmt|;
name|loaded
operator|=
literal|true
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
block|{         }
block|}
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|onOpenRow (int idx)
specifier|private
name|void
name|onOpenRow
parameter_list|(
name|int
name|idx
parameter_list|)
block|{
name|closeParent
argument_list|()
expr_stmt|;
name|Gerrit
operator|.
name|display
argument_list|(
name|url
argument_list|(
name|revisions
operator|.
name|get
argument_list|(
name|idx
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|render (NativeMap<RevisionInfo> map)
specifier|private
name|void
name|render
parameter_list|(
name|NativeMap
argument_list|<
name|RevisionInfo
argument_list|>
name|map
parameter_list|)
block|{
name|map
operator|.
name|copyKeysIntoChildren
argument_list|(
literal|"name"
argument_list|)
expr_stmt|;
name|revisions
operator|=
name|map
operator|.
name|values
argument_list|()
expr_stmt|;
name|RevisionInfo
operator|.
name|sortRevisionInfoByNumber
argument_list|(
name|revisions
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|reverse
argument_list|(
name|Natives
operator|.
name|asList
argument_list|(
name|revisions
argument_list|)
argument_list|)
expr_stmt|;
name|SafeHtmlBuilder
name|sb
init|=
operator|new
name|SafeHtmlBuilder
argument_list|()
decl_stmt|;
name|header
argument_list|(
name|sb
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|revisions
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|revision
argument_list|(
name|sb
argument_list|,
name|i
argument_list|,
name|revisions
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|GWT
operator|.
expr|<
name|FancyFlexTableImpl
operator|>
name|create
argument_list|(
name|FancyFlexTableImpl
operator|.
name|class
argument_list|)
operator|.
name|resetHtml
argument_list|(
name|table
argument_list|,
name|sb
argument_list|)
expr_stmt|;
block|}
DECL|method|header (SafeHtmlBuilder sb)
specifier|private
name|void
name|header
parameter_list|(
name|SafeHtmlBuilder
name|sb
parameter_list|)
block|{
name|sb
operator|.
name|openTr
argument_list|()
operator|.
name|openTh
argument_list|()
operator|.
name|setStyleName
argument_list|(
name|style
operator|.
name|legacy_id
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|Resources
operator|.
name|C
operator|.
name|patchSet
argument_list|()
argument_list|)
operator|.
name|closeTh
argument_list|()
operator|.
name|openTh
argument_list|()
operator|.
name|append
argument_list|(
name|Resources
operator|.
name|C
operator|.
name|commit
argument_list|()
argument_list|)
operator|.
name|closeTh
argument_list|()
operator|.
name|openTh
argument_list|()
operator|.
name|append
argument_list|(
name|Resources
operator|.
name|C
operator|.
name|date
argument_list|()
argument_list|)
operator|.
name|closeTh
argument_list|()
operator|.
name|openTh
argument_list|()
operator|.
name|append
argument_list|(
name|Resources
operator|.
name|C
operator|.
name|author
argument_list|()
argument_list|)
operator|.
name|closeTh
argument_list|()
operator|.
name|closeTr
argument_list|()
expr_stmt|;
block|}
DECL|method|revision (SafeHtmlBuilder sb, int index, RevisionInfo r)
specifier|private
name|void
name|revision
parameter_list|(
name|SafeHtmlBuilder
name|sb
parameter_list|,
name|int
name|index
parameter_list|,
name|RevisionInfo
name|r
parameter_list|)
block|{
name|CommitInfo
name|c
init|=
name|r
operator|.
name|commit
argument_list|()
decl_stmt|;
name|sb
operator|.
name|openTr
argument_list|()
expr_stmt|;
if|if
condition|(
name|revision
operator|.
name|equals
argument_list|(
name|r
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
name|sb
operator|.
name|setStyleName
argument_list|(
name|style
operator|.
name|current
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|openTd
argument_list|()
operator|.
name|setStyleName
argument_list|(
name|style
operator|.
name|legacy_id
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|r
operator|.
name|draft
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|Resources
operator|.
name|C
operator|.
name|draft
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|r
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|closeTd
argument_list|()
expr_stmt|;
name|sb
operator|.
name|openTd
argument_list|()
operator|.
name|setStyleName
argument_list|(
name|style
operator|.
name|commit
argument_list|()
argument_list|)
operator|.
name|openAnchor
argument_list|()
operator|.
name|setAttribute
argument_list|(
literal|"href"
argument_list|,
literal|"#"
operator|+
name|url
argument_list|(
name|r
argument_list|)
argument_list|)
operator|.
name|setAttribute
argument_list|(
literal|"onclick"
argument_list|,
name|OPEN
operator|+
literal|"(event,"
operator|+
name|index
operator|+
literal|")"
argument_list|)
operator|.
name|append
argument_list|(
name|r
operator|.
name|name
argument_list|()
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|10
argument_list|)
argument_list|)
operator|.
name|closeAnchor
argument_list|()
operator|.
name|closeTd
argument_list|()
expr_stmt|;
name|sb
operator|.
name|openTd
argument_list|()
operator|.
name|append
argument_list|(
name|FormatUtil
operator|.
name|shortFormatDayTime
argument_list|(
name|c
operator|.
name|committer
argument_list|()
operator|.
name|date
argument_list|()
argument_list|)
argument_list|)
operator|.
name|closeTd
argument_list|()
expr_stmt|;
name|String
name|an
init|=
name|c
operator|.
name|author
argument_list|()
operator|!=
literal|null
condition|?
name|c
operator|.
name|author
argument_list|()
operator|.
name|name
argument_list|()
else|:
literal|""
decl_stmt|;
name|String
name|cn
init|=
name|c
operator|.
name|committer
argument_list|()
operator|!=
literal|null
condition|?
name|c
operator|.
name|committer
argument_list|()
operator|.
name|name
argument_list|()
else|:
literal|""
decl_stmt|;
name|sb
operator|.
name|openTd
argument_list|()
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|an
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|an
argument_list|)
operator|&&
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|cn
argument_list|)
operator|&&
operator|!
name|an
operator|.
name|equals
argument_list|(
name|cn
argument_list|)
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" / "
argument_list|)
operator|.
name|append
argument_list|(
name|cn
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
DECL|method|url (RevisionInfo r)
specifier|private
name|String
name|url
parameter_list|(
name|RevisionInfo
name|r
parameter_list|)
block|{
return|return
name|PageLinks
operator|.
name|toChange
argument_list|(
name|changeId
argument_list|,
name|r
operator|.
name|id
argument_list|()
argument_list|)
return|;
block|}
DECL|method|closeParent ()
specifier|private
name|void
name|closeParent
parameter_list|()
block|{
for|for
control|(
name|Widget
name|w
init|=
name|getParent
argument_list|()
init|;
name|w
operator|!=
literal|null
condition|;
name|w
operator|=
name|w
operator|.
name|getParent
argument_list|()
control|)
block|{
if|if
condition|(
name|w
operator|instanceof
name|PopupPanel
condition|)
block|{
operator|(
operator|(
name|PopupPanel
operator|)
name|w
operator|)
operator|.
name|hide
argument_list|(
literal|true
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
end_class

end_unit

