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
name|AvatarImage
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
name|ChangeInfo
operator|.
name|MessageInfo
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
name|CommentInfo
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
name|Util
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
name|CommentLinkProcessor
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
name|Style
operator|.
name|Visibility
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
name|UIObject
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
name|ArrayList
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_class
DECL|class|Message
class|class
name|Message
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
name|Message
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
DECL|interface|Style
specifier|static
interface|interface
name|Style
extends|extends
name|CssResource
block|{
DECL|method|closed ()
name|String
name|closed
parameter_list|()
function_decl|;
block|}
DECL|field|style
annotation|@
name|UiField
name|Style
name|style
decl_stmt|;
DECL|field|header
annotation|@
name|UiField
name|HTMLPanel
name|header
decl_stmt|;
DECL|field|name
annotation|@
name|UiField
name|Element
name|name
decl_stmt|;
DECL|field|summary
annotation|@
name|UiField
name|Element
name|summary
decl_stmt|;
DECL|field|date
annotation|@
name|UiField
name|Element
name|date
decl_stmt|;
DECL|field|reply
annotation|@
name|UiField
name|Button
name|reply
decl_stmt|;
DECL|field|message
annotation|@
name|UiField
name|Element
name|message
decl_stmt|;
DECL|field|comments
annotation|@
name|UiField
name|FlowPanel
name|comments
decl_stmt|;
DECL|field|history
specifier|private
specifier|final
name|History
name|history
decl_stmt|;
DECL|field|info
specifier|private
specifier|final
name|MessageInfo
name|info
decl_stmt|;
DECL|field|commentList
specifier|private
name|List
argument_list|<
name|CommentInfo
argument_list|>
name|commentList
decl_stmt|;
DECL|field|autoOpen
specifier|private
name|boolean
name|autoOpen
decl_stmt|;
annotation|@
name|UiField
argument_list|(
name|provided
operator|=
literal|true
argument_list|)
DECL|field|avatar
name|AvatarImage
name|avatar
decl_stmt|;
DECL|method|Message (History parent, MessageInfo info)
name|Message
parameter_list|(
name|History
name|parent
parameter_list|,
name|MessageInfo
name|info
parameter_list|)
block|{
if|if
condition|(
name|info
operator|.
name|author
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|avatar
operator|=
operator|new
name|AvatarImage
argument_list|(
name|info
operator|.
name|author
argument_list|()
argument_list|)
expr_stmt|;
name|avatar
operator|.
name|setSize
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|avatar
operator|=
operator|new
name|AvatarImage
argument_list|()
expr_stmt|;
block|}
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
name|header
operator|.
name|addDomHandler
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
name|setOpen
argument_list|(
operator|!
name|isOpen
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
name|ClickEvent
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|history
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|info
operator|=
name|info
expr_stmt|;
name|setName
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|date
operator|.
name|setInnerText
argument_list|(
name|FormatUtil
operator|.
name|shortFormatDayTime
argument_list|(
name|info
operator|.
name|date
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|info
operator|.
name|message
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|String
name|msg
init|=
name|info
operator|.
name|message
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
name|summary
operator|.
name|setInnerText
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|message
operator|.
name|setInnerSafeHtml
argument_list|(
name|history
operator|.
name|getCommentLinkProcessor
argument_list|()
operator|.
name|apply
argument_list|(
operator|new
name|SafeHtmlBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|msg
argument_list|)
operator|.
name|wikify
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|reply
operator|.
name|getElement
argument_list|()
operator|.
name|getStyle
argument_list|()
operator|.
name|setVisibility
argument_list|(
name|Visibility
operator|.
name|HIDDEN
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|UiHandler
argument_list|(
literal|"reply"
argument_list|)
DECL|method|onReply (ClickEvent e)
name|void
name|onReply
parameter_list|(
name|ClickEvent
name|e
parameter_list|)
block|{
name|e
operator|.
name|stopPropagation
argument_list|()
expr_stmt|;
if|if
condition|(
name|Gerrit
operator|.
name|isSignedIn
argument_list|()
condition|)
block|{
name|history
operator|.
name|replyTo
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Gerrit
operator|.
name|doSignIn
argument_list|(
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
operator|.
name|getToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getMessageInfo ()
name|MessageInfo
name|getMessageInfo
parameter_list|()
block|{
return|return
name|info
return|;
block|}
DECL|method|isOpen ()
specifier|private
name|boolean
name|isOpen
parameter_list|()
block|{
return|return
name|UIObject
operator|.
name|isVisible
argument_list|(
name|message
argument_list|)
return|;
block|}
DECL|method|setOpen (boolean open)
name|void
name|setOpen
parameter_list|(
name|boolean
name|open
parameter_list|)
block|{
if|if
condition|(
name|open
operator|&&
name|info
operator|.
name|_revisionNumber
argument_list|()
operator|>
literal|0
operator|&&
operator|!
name|commentList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|renderComments
argument_list|(
name|commentList
argument_list|)
expr_stmt|;
name|commentList
operator|=
name|Collections
operator|.
name|emptyList
argument_list|()
expr_stmt|;
block|}
name|setName
argument_list|(
name|open
argument_list|)
expr_stmt|;
name|UIObject
operator|.
name|setVisible
argument_list|(
name|summary
argument_list|,
operator|!
name|open
argument_list|)
expr_stmt|;
name|UIObject
operator|.
name|setVisible
argument_list|(
name|message
argument_list|,
name|open
argument_list|)
expr_stmt|;
name|comments
operator|.
name|setVisible
argument_list|(
name|open
operator|&&
name|comments
operator|.
name|getWidgetCount
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|open
condition|)
block|{
name|removeStyleName
argument_list|(
name|style
operator|.
name|closed
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|addStyleName
argument_list|(
name|style
operator|.
name|closed
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setName (boolean open)
specifier|private
name|void
name|setName
parameter_list|(
name|boolean
name|open
parameter_list|)
block|{
name|name
operator|.
name|setInnerText
argument_list|(
name|open
condition|?
name|authorName
argument_list|(
name|info
argument_list|)
else|:
name|elide
argument_list|(
name|authorName
argument_list|(
name|info
argument_list|)
argument_list|,
literal|20
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|elide (final String s, final int len)
specifier|private
specifier|static
name|String
name|elide
parameter_list|(
specifier|final
name|String
name|s
parameter_list|,
specifier|final
name|int
name|len
parameter_list|)
block|{
if|if
condition|(
name|s
operator|==
literal|null
operator|||
name|s
operator|.
name|length
argument_list|()
operator|<=
name|len
operator|||
name|len
operator|<=
literal|10
condition|)
block|{
return|return
name|s
return|;
block|}
name|int
name|i
init|=
operator|(
name|len
operator|-
literal|3
operator|)
operator|/
literal|2
decl_stmt|;
return|return
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
operator|+
literal|"..."
operator|+
name|s
operator|.
name|substring
argument_list|(
name|s
operator|.
name|length
argument_list|()
operator|-
name|i
argument_list|)
return|;
block|}
DECL|method|autoOpen ()
name|void
name|autoOpen
parameter_list|()
block|{
if|if
condition|(
name|commentList
operator|==
literal|null
condition|)
block|{
name|autoOpen
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|commentList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|setOpen
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addComments (List<CommentInfo> list)
name|void
name|addComments
parameter_list|(
name|List
argument_list|<
name|CommentInfo
argument_list|>
name|list
parameter_list|)
block|{
if|if
condition|(
name|isOpen
argument_list|()
condition|)
block|{
name|renderComments
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|comments
operator|.
name|setVisible
argument_list|(
name|comments
operator|.
name|getWidgetCount
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|commentList
operator|=
name|Collections
operator|.
name|emptyList
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|commentList
operator|=
name|list
expr_stmt|;
if|if
condition|(
name|autoOpen
operator|&&
operator|!
name|commentList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|setOpen
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|renderComments (List<CommentInfo> list)
specifier|private
name|void
name|renderComments
parameter_list|(
name|List
argument_list|<
name|CommentInfo
argument_list|>
name|list
parameter_list|)
block|{
name|CommentLinkProcessor
name|clp
init|=
name|history
operator|.
name|getCommentLinkProcessor
argument_list|()
decl_stmt|;
name|PatchSet
operator|.
name|Id
name|ps
init|=
operator|new
name|PatchSet
operator|.
name|Id
argument_list|(
name|history
operator|.
name|getChangeId
argument_list|()
argument_list|,
name|info
operator|.
name|_revisionNumber
argument_list|()
argument_list|)
decl_stmt|;
name|TreeMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|CommentInfo
argument_list|>
argument_list|>
name|m
init|=
name|byPath
argument_list|(
name|list
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|CommentInfo
argument_list|>
name|l
init|=
name|m
operator|.
name|remove
argument_list|(
name|Patch
operator|.
name|COMMIT_MSG
argument_list|)
decl_stmt|;
if|if
condition|(
name|l
operator|!=
literal|null
condition|)
block|{
name|comments
operator|.
name|add
argument_list|(
operator|new
name|FileComments
argument_list|(
name|clp
argument_list|,
name|ps
argument_list|,
name|Util
operator|.
name|C
operator|.
name|commitMessage
argument_list|()
argument_list|,
name|l
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|CommentInfo
argument_list|>
argument_list|>
name|e
range|:
name|m
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|comments
operator|.
name|add
argument_list|(
operator|new
name|FileComments
argument_list|(
name|clp
argument_list|,
name|ps
argument_list|,
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|TreeMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|CommentInfo
argument_list|>
argument_list|>
DECL|method|byPath (List<CommentInfo> list)
name|byPath
parameter_list|(
name|List
argument_list|<
name|CommentInfo
argument_list|>
name|list
parameter_list|)
block|{
name|TreeMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|CommentInfo
argument_list|>
argument_list|>
name|m
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|CommentInfo
name|c
range|:
name|list
control|)
block|{
name|List
argument_list|<
name|CommentInfo
argument_list|>
name|l
init|=
name|m
operator|.
name|get
argument_list|(
name|c
operator|.
name|path
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|l
operator|==
literal|null
condition|)
block|{
name|l
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
name|c
operator|.
name|path
argument_list|()
argument_list|,
name|l
argument_list|)
expr_stmt|;
block|}
name|l
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
return|return
name|m
return|;
block|}
DECL|method|authorName (MessageInfo info)
specifier|static
name|String
name|authorName
parameter_list|(
name|MessageInfo
name|info
parameter_list|)
block|{
if|if
condition|(
name|info
operator|.
name|author
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|info
operator|.
name|author
argument_list|()
operator|.
name|name
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|info
operator|.
name|author
argument_list|()
operator|.
name|name
argument_list|()
return|;
block|}
return|return
name|Gerrit
operator|.
name|getConfig
argument_list|()
operator|.
name|getAnonymousCowardName
argument_list|()
return|;
block|}
return|return
name|Util
operator|.
name|C
operator|.
name|messageNoAuthor
argument_list|()
return|;
block|}
block|}
end_class

end_unit

