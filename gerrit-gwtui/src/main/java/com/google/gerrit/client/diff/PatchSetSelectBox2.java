begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|//Copyright (C) 2013 The Android Open Source Project
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
DECL|package|com.google.gerrit.client.diff
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|diff
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
name|WebLinkInfo
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
name|ChangeFileApi
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
name|patches
operator|.
name|PatchUtil
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
name|ui
operator|.
name|InlineHyperlink
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
name|gerrit
operator|.
name|reviewdb
operator|.
name|client
operator|.
name|Patch
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
name|uibinder
operator|.
name|client
operator|.
name|UiHandler
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
name|Image
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
name|ImageResourceRenderer
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
name|gwtorm
operator|.
name|client
operator|.
name|KeyUtil
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

begin_comment
comment|/** HTMLPanel to select among patch sets */
end_comment

begin_class
DECL|class|PatchSetSelectBox2
class|class
name|PatchSetSelectBox2
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
name|PatchSetSelectBox2
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
DECL|interface|BoxStyle
interface|interface
name|BoxStyle
extends|extends
name|CssResource
block|{
DECL|method|selected ()
name|String
name|selected
parameter_list|()
function_decl|;
DECL|method|replyBox ()
name|String
name|replyBox
parameter_list|()
function_decl|;
block|}
DECL|field|icon
annotation|@
name|UiField
name|Image
name|icon
decl_stmt|;
DECL|field|linkPanel
annotation|@
name|UiField
name|HTMLPanel
name|linkPanel
decl_stmt|;
DECL|field|style
annotation|@
name|UiField
name|BoxStyle
name|style
decl_stmt|;
DECL|field|parent
specifier|private
name|SideBySide2
name|parent
decl_stmt|;
DECL|field|side
specifier|private
name|DisplaySide
name|side
decl_stmt|;
DECL|field|sideA
specifier|private
name|boolean
name|sideA
decl_stmt|;
DECL|field|path
specifier|private
name|String
name|path
decl_stmt|;
DECL|field|changeId
specifier|private
name|Change
operator|.
name|Id
name|changeId
decl_stmt|;
DECL|field|revision
specifier|private
name|PatchSet
operator|.
name|Id
name|revision
decl_stmt|;
DECL|field|idActive
specifier|private
name|PatchSet
operator|.
name|Id
name|idActive
decl_stmt|;
DECL|field|other
specifier|private
name|PatchSetSelectBox2
name|other
decl_stmt|;
DECL|method|PatchSetSelectBox2 (SideBySide2 parent, DisplaySide side, Change.Id changeId, PatchSet.Id revision, String path)
name|PatchSetSelectBox2
parameter_list|(
name|SideBySide2
name|parent
parameter_list|,
name|DisplaySide
name|side
parameter_list|,
name|Change
operator|.
name|Id
name|changeId
parameter_list|,
name|PatchSet
operator|.
name|Id
name|revision
parameter_list|,
name|String
name|path
parameter_list|)
block|{
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
name|icon
operator|.
name|setTitle
argument_list|(
name|PatchUtil
operator|.
name|C
operator|.
name|addFileCommentToolTip
argument_list|()
argument_list|)
expr_stmt|;
name|icon
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
name|link
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|side
operator|=
name|side
expr_stmt|;
name|this
operator|.
name|sideA
operator|=
name|side
operator|==
name|DisplaySide
operator|.
name|A
expr_stmt|;
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
name|idActive
operator|=
operator|(
name|sideA
operator|&&
name|revision
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|revision
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
DECL|method|setUpPatchSetNav (JsArray<RevisionInfo> list, DiffInfo.FileMeta meta, boolean editExists, int currentPatchSet)
name|void
name|setUpPatchSetNav
parameter_list|(
name|JsArray
argument_list|<
name|RevisionInfo
argument_list|>
name|list
parameter_list|,
name|DiffInfo
operator|.
name|FileMeta
name|meta
parameter_list|,
name|boolean
name|editExists
parameter_list|,
name|int
name|currentPatchSet
parameter_list|)
block|{
name|InlineHyperlink
name|baseLink
init|=
literal|null
decl_stmt|;
name|InlineHyperlink
name|selectedLink
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|sideA
condition|)
block|{
name|baseLink
operator|=
name|createLink
argument_list|(
name|PatchUtil
operator|.
name|C
operator|.
name|patchBase
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|linkPanel
operator|.
name|add
argument_list|(
name|baseLink
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|list
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|RevisionInfo
name|r
init|=
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|InlineHyperlink
name|link
init|=
name|createLink
argument_list|(
name|r
operator|.
name|id
argument_list|()
argument_list|,
operator|new
name|PatchSet
operator|.
name|Id
argument_list|(
name|changeId
argument_list|,
name|r
operator|.
name|_number
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|linkPanel
operator|.
name|add
argument_list|(
name|link
argument_list|)
expr_stmt|;
if|if
condition|(
name|revision
operator|!=
literal|null
operator|&&
name|r
operator|.
name|id
argument_list|()
operator|.
name|equals
argument_list|(
name|revision
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
name|selectedLink
operator|=
name|link
expr_stmt|;
block|}
block|}
if|if
condition|(
name|selectedLink
operator|!=
literal|null
condition|)
block|{
name|selectedLink
operator|.
name|setStyleName
argument_list|(
name|style
operator|.
name|selected
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sideA
condition|)
block|{
name|baseLink
operator|.
name|setStyleName
argument_list|(
name|style
operator|.
name|selected
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|meta
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
operator|!
name|Patch
operator|.
name|COMMIT_MSG
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|linkPanel
operator|.
name|add
argument_list|(
name|createDownloadLink
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|idActive
operator|!=
literal|null
operator|&&
name|Gerrit
operator|.
name|isSignedIn
argument_list|()
operator|&&
operator|!
name|Patch
operator|.
name|COMMIT_MSG
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|)
block|{
if|if
condition|(
operator|(
name|editExists
operator|&&
name|idActive
operator|.
name|get
argument_list|()
operator|==
literal|0
operator|)
operator|||
operator|(
operator|!
name|editExists
operator|&&
name|idActive
operator|.
name|get
argument_list|()
operator|==
name|currentPatchSet
operator|)
condition|)
block|{
name|linkPanel
operator|.
name|add
argument_list|(
name|createEditIcon
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|WebLinkInfo
argument_list|>
name|webLinks
init|=
name|Natives
operator|.
name|asList
argument_list|(
name|meta
operator|.
name|web_links
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|webLinks
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|WebLinkInfo
name|weblink
range|:
name|webLinks
control|)
block|{
name|Anchor
name|a
init|=
operator|new
name|Anchor
argument_list|()
decl_stmt|;
name|a
operator|.
name|setHref
argument_list|(
name|weblink
operator|.
name|url
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|weblink
operator|.
name|target
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|weblink
operator|.
name|target
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|a
operator|.
name|setTarget
argument_list|(
name|weblink
operator|.
name|target
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|weblink
operator|.
name|imageUrl
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|weblink
operator|.
name|imageUrl
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|Image
name|img
init|=
operator|new
name|Image
argument_list|()
decl_stmt|;
name|img
operator|.
name|setAltText
argument_list|(
name|weblink
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|img
operator|.
name|setUrl
argument_list|(
name|weblink
operator|.
name|imageUrl
argument_list|()
argument_list|)
expr_stmt|;
name|img
operator|.
name|setTitle
argument_list|(
name|weblink
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|a
operator|.
name|getElement
argument_list|()
operator|.
name|appendChild
argument_list|(
name|img
operator|.
name|getElement
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|a
operator|.
name|setText
argument_list|(
literal|"("
operator|+
name|weblink
operator|.
name|name
argument_list|()
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
name|linkPanel
operator|.
name|add
argument_list|(
name|a
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|createEditIcon ()
specifier|private
name|Widget
name|createEditIcon
parameter_list|()
block|{
specifier|final
name|Anchor
name|anchor
init|=
operator|new
name|Anchor
argument_list|(
operator|new
name|ImageResourceRenderer
argument_list|()
operator|.
name|render
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|edit
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|anchor
operator|.
name|addClickHandler
argument_list|(
operator|new
name|ClickHandler
argument_list|()
block|{
name|boolean
name|editing
init|=
literal|false
decl_stmt|;
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
specifier|final
name|PatchSet
operator|.
name|Id
name|id
init|=
operator|(
name|idActive
operator|==
literal|null
operator|)
condition|?
name|other
operator|.
name|idActive
else|:
name|idActive
decl_stmt|;
name|editing
operator|=
operator|!
name|editing
expr_stmt|;
name|parent
operator|.
name|editSideB
argument_list|(
name|editing
argument_list|)
expr_stmt|;
if|if
condition|(
name|editing
condition|)
block|{
name|ChangeFileApi
operator|.
name|getContent
argument_list|(
name|id
argument_list|,
name|path
argument_list|,
operator|new
name|GerritCallback
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|String
name|content
parameter_list|)
block|{
name|parent
operator|.
name|setSideBContent
argument_list|(
name|content
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|anchor
operator|.
name|setHTML
argument_list|(
operator|new
name|ImageResourceRenderer
argument_list|()
operator|.
name|render
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|save
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|anchor
operator|.
name|setHTML
argument_list|(
operator|new
name|ImageResourceRenderer
argument_list|()
operator|.
name|render
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|edit
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|siteBContent
init|=
name|parent
operator|.
name|getSideBContent
argument_list|()
decl_stmt|;
name|ChangeFileApi
operator|.
name|putContent
argument_list|(
name|id
argument_list|,
name|path
argument_list|,
name|siteBContent
argument_list|,
operator|new
name|GerritCallback
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
block|{                 }
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|anchor
operator|.
name|setTitle
argument_list|(
name|PatchUtil
operator|.
name|C
operator|.
name|edit
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|anchor
return|;
block|}
DECL|method|link (PatchSetSelectBox2 a, PatchSetSelectBox2 b)
specifier|static
name|void
name|link
parameter_list|(
name|PatchSetSelectBox2
name|a
parameter_list|,
name|PatchSetSelectBox2
name|b
parameter_list|)
block|{
name|a
operator|.
name|other
operator|=
name|b
expr_stmt|;
name|b
operator|.
name|other
operator|=
name|a
expr_stmt|;
block|}
DECL|method|createLink (String label, PatchSet.Id id)
specifier|private
name|InlineHyperlink
name|createLink
parameter_list|(
name|String
name|label
parameter_list|,
name|PatchSet
operator|.
name|Id
name|id
parameter_list|)
block|{
assert|assert
name|other
operator|!=
literal|null
assert|;
if|if
condition|(
name|sideA
condition|)
block|{
assert|assert
name|other
operator|.
name|idActive
operator|!=
literal|null
assert|;
block|}
return|return
operator|new
name|InlineHyperlink
argument_list|(
name|label
argument_list|,
name|Dispatcher
operator|.
name|toSideBySide
argument_list|(
name|sideA
condition|?
name|id
else|:
name|other
operator|.
name|idActive
argument_list|,
name|sideA
condition|?
name|other
operator|.
name|idActive
else|:
name|id
argument_list|,
name|path
argument_list|)
argument_list|)
return|;
block|}
DECL|method|createDownloadLink ()
specifier|private
name|Anchor
name|createDownloadLink
parameter_list|()
block|{
name|PatchSet
operator|.
name|Id
name|id
init|=
operator|(
name|idActive
operator|==
literal|null
operator|)
condition|?
name|other
operator|.
name|idActive
else|:
name|idActive
decl_stmt|;
name|String
name|sideURL
init|=
operator|(
name|idActive
operator|==
literal|null
operator|)
condition|?
literal|"1"
else|:
literal|"0"
decl_stmt|;
name|String
name|base
init|=
name|GWT
operator|.
name|getHostPageBaseURL
argument_list|()
operator|+
literal|"cat/"
decl_stmt|;
name|Anchor
name|anchor
init|=
operator|new
name|Anchor
argument_list|(
operator|new
name|ImageResourceRenderer
argument_list|()
operator|.
name|render
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|downloadIcon
argument_list|()
argument_list|)
argument_list|,
name|base
operator|+
name|KeyUtil
operator|.
name|encode
argument_list|(
name|id
operator|+
literal|","
operator|+
name|path
argument_list|)
operator|+
literal|"^"
operator|+
name|sideURL
argument_list|)
decl_stmt|;
name|anchor
operator|.
name|setTitle
argument_list|(
name|PatchUtil
operator|.
name|C
operator|.
name|download
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|anchor
return|;
block|}
annotation|@
name|UiHandler
argument_list|(
literal|"icon"
argument_list|)
DECL|method|onIconClick (ClickEvent e)
name|void
name|onIconClick
parameter_list|(
name|ClickEvent
name|e
parameter_list|)
block|{
name|parent
operator|.
name|getCmFromSide
argument_list|(
name|side
argument_list|)
operator|.
name|scrollToY
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|parent
operator|.
name|getCommentManager
argument_list|()
operator|.
name|insertNewDraft
argument_list|(
name|side
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

