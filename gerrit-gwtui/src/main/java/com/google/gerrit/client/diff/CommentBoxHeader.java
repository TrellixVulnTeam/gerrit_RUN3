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
name|account
operator|.
name|AccountInfo
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
name|event
operator|.
name|dom
operator|.
name|client
operator|.
name|HasClickHandlers
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
name|shared
operator|.
name|HandlerRegistration
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
name|java
operator|.
name|sql
operator|.
name|Timestamp
import|;
end_import

begin_comment
comment|/**  * An HtmlPanel representing the header of a CommentBox, displaying  * the author's avatar (if applicable), the author's name, the summary,  * and the date.  */
end_comment

begin_class
DECL|class|CommentBoxHeader
class|class
name|CommentBoxHeader
extends|extends
name|Composite
implements|implements
name|HasClickHandlers
block|{
DECL|interface|Binder
interface|interface
name|Binder
extends|extends
name|UiBinder
argument_list|<
name|HTMLPanel
argument_list|,
name|CommentBoxHeader
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
DECL|interface|CommentBoxHeaderStyle
interface|interface
name|CommentBoxHeaderStyle
extends|extends
name|CssResource
block|{
DECL|method|name ()
name|String
name|name
parameter_list|()
function_decl|;
DECL|method|summary ()
name|String
name|summary
parameter_list|()
function_decl|;
DECL|method|date ()
name|String
name|date
parameter_list|()
function_decl|;
block|}
DECL|field|draft
specifier|private
name|boolean
name|draft
decl_stmt|;
annotation|@
name|UiField
DECL|field|avatarCell
name|Element
name|avatarCell
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
annotation|@
name|UiField
DECL|field|name
name|Element
name|name
decl_stmt|;
annotation|@
name|UiField
DECL|field|summary
name|Element
name|summary
decl_stmt|;
annotation|@
name|UiField
DECL|field|date
name|Element
name|date
decl_stmt|;
annotation|@
name|UiField
DECL|field|headerStyle
name|CommentBoxHeaderStyle
name|headerStyle
decl_stmt|;
DECL|method|CommentBoxHeader (AccountInfo author, Timestamp when, boolean isDraft)
name|CommentBoxHeader
parameter_list|(
name|AccountInfo
name|author
parameter_list|,
name|Timestamp
name|when
parameter_list|,
name|boolean
name|isDraft
parameter_list|)
block|{
if|if
condition|(
name|author
operator|!=
literal|null
condition|)
block|{
name|avatar
operator|=
operator|new
name|AvatarImage
argument_list|(
name|author
argument_list|,
literal|26
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
if|if
condition|(
name|author
operator|==
literal|null
condition|)
block|{
name|UIObject
operator|.
name|setVisible
argument_list|(
name|avatarCell
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|draft
operator|=
name|isDraft
expr_stmt|;
if|if
condition|(
name|when
operator|!=
literal|null
condition|)
block|{
name|setDate
argument_list|(
name|when
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isDraft
condition|)
block|{
name|name
operator|.
name|setInnerText
argument_list|(
name|PatchUtil
operator|.
name|C
operator|.
name|draft
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|name
operator|.
name|setInnerText
argument_list|(
name|FormatUtil
operator|.
name|name
argument_list|(
name|author
argument_list|)
argument_list|)
expr_stmt|;
name|name
operator|.
name|setTitle
argument_list|(
name|FormatUtil
operator|.
name|nameEmail
argument_list|(
name|author
argument_list|)
argument_list|)
expr_stmt|;
name|date
operator|.
name|setTitle
argument_list|(
name|FormatUtil
operator|.
name|mediumFormat
argument_list|(
name|when
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setDate (Timestamp when)
name|void
name|setDate
parameter_list|(
name|Timestamp
name|when
parameter_list|)
block|{
if|if
condition|(
name|draft
condition|)
block|{
name|date
operator|.
name|setInnerText
argument_list|(
name|PatchUtil
operator|.
name|M
operator|.
name|draftSaved
argument_list|(
name|when
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|date
operator|.
name|setInnerText
argument_list|(
name|FormatUtil
operator|.
name|shortFormatDayTime
argument_list|(
name|when
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setSummaryText (String message)
name|void
name|setSummaryText
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|summary
operator|.
name|setInnerText
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addClickHandler (ClickHandler handler)
specifier|public
name|HandlerRegistration
name|addClickHandler
parameter_list|(
name|ClickHandler
name|handler
parameter_list|)
block|{
return|return
name|addDomHandler
argument_list|(
name|handler
argument_list|,
name|ClickEvent
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

