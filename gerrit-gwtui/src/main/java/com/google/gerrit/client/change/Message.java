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
DECL|field|message
annotation|@
name|UiField
name|Element
name|message
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
DECL|method|Message (CommentLinkProcessor clp, MessageInfo info)
name|Message
parameter_list|(
name|CommentLinkProcessor
name|clp
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
name|name
operator|.
name|setInnerText
argument_list|(
name|authorName
argument_list|(
name|info
argument_list|)
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
name|clp
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

