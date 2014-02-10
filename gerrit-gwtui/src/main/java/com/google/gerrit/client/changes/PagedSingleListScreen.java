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
DECL|package|com.google.gerrit.client.changes
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|changes
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
name|reviewdb
operator|.
name|client
operator|.
name|AccountGeneralPreferences
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
name|HorizontalPanel
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
name|KeyCommand
import|;
end_import

begin_class
DECL|class|PagedSingleListScreen
specifier|public
specifier|abstract
class|class
name|PagedSingleListScreen
extends|extends
name|Screen
block|{
DECL|field|pageSize
specifier|protected
specifier|final
name|int
name|pageSize
decl_stmt|;
DECL|field|start
specifier|protected
specifier|final
name|int
name|start
decl_stmt|;
DECL|field|anchorPrefix
specifier|private
specifier|final
name|String
name|anchorPrefix
decl_stmt|;
DECL|field|changes
specifier|protected
name|ChangeList
name|changes
decl_stmt|;
DECL|field|table
specifier|private
name|ChangeTable2
name|table
decl_stmt|;
DECL|field|section
specifier|private
name|ChangeTable2
operator|.
name|Section
name|section
decl_stmt|;
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
DECL|method|PagedSingleListScreen (String anchorToken, int start)
specifier|protected
name|PagedSingleListScreen
parameter_list|(
name|String
name|anchorToken
parameter_list|,
name|int
name|start
parameter_list|)
block|{
name|anchorPrefix
operator|=
name|anchorToken
expr_stmt|;
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
if|if
condition|(
name|Gerrit
operator|.
name|isSignedIn
argument_list|()
condition|)
block|{
specifier|final
name|AccountGeneralPreferences
name|p
init|=
name|Gerrit
operator|.
name|getUserAccount
argument_list|()
operator|.
name|getGeneralPreferences
argument_list|()
decl_stmt|;
specifier|final
name|short
name|m
init|=
name|p
operator|.
name|getMaximumPageSize
argument_list|()
decl_stmt|;
name|pageSize
operator|=
literal|0
operator|<
name|m
condition|?
name|m
else|:
name|AccountGeneralPreferences
operator|.
name|DEFAULT_PAGESIZE
expr_stmt|;
block|}
else|else
block|{
name|pageSize
operator|=
name|AccountGeneralPreferences
operator|.
name|DEFAULT_PAGESIZE
expr_stmt|;
block|}
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
name|prev
operator|=
operator|new
name|Hyperlink
argument_list|(
name|Util
operator|.
name|C
operator|.
name|pagedChangeListPrev
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|""
argument_list|)
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
operator|new
name|Hyperlink
argument_list|(
name|Util
operator|.
name|C
operator|.
name|pagedChangeListNext
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|next
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|table
operator|=
operator|new
name|ChangeTable2
argument_list|()
block|{
block|{
name|keysNavigation
operator|.
name|add
argument_list|(
operator|new
name|DoLinkCommand
argument_list|(
literal|0
argument_list|,
literal|'p'
argument_list|,
name|Util
operator|.
name|C
operator|.
name|changeTablePagePrev
argument_list|()
argument_list|,
name|prev
argument_list|)
argument_list|,
operator|new
name|DoLinkCommand
argument_list|(
literal|0
argument_list|,
literal|'n'
argument_list|,
name|Util
operator|.
name|C
operator|.
name|changeTablePageNext
argument_list|()
argument_list|,
name|next
argument_list|)
argument_list|)
expr_stmt|;
name|keysNavigation
operator|.
name|add
argument_list|(
operator|new
name|DoLinkCommand
argument_list|(
literal|0
argument_list|,
literal|'['
argument_list|,
name|Util
operator|.
name|C
operator|.
name|changeTablePagePrev
argument_list|()
argument_list|,
name|prev
argument_list|)
argument_list|,
operator|new
name|DoLinkCommand
argument_list|(
literal|0
argument_list|,
literal|']'
argument_list|,
name|Util
operator|.
name|C
operator|.
name|changeTablePageNext
argument_list|()
argument_list|,
name|next
argument_list|)
argument_list|)
expr_stmt|;
name|keysNavigation
operator|.
name|add
argument_list|(
operator|new
name|KeyCommand
argument_list|(
literal|0
argument_list|,
literal|'R'
argument_list|,
name|Util
operator|.
name|C
operator|.
name|keyReloadSearch
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|onKeyPress
parameter_list|(
specifier|final
name|KeyPressEvent
name|event
parameter_list|)
block|{
name|Gerrit
operator|.
name|display
argument_list|(
name|getToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
expr_stmt|;
name|section
operator|=
operator|new
name|ChangeTable2
operator|.
name|Section
argument_list|()
expr_stmt|;
name|table
operator|.
name|addSection
argument_list|(
name|section
argument_list|)
expr_stmt|;
name|table
operator|.
name|setSavePointerId
argument_list|(
name|anchorPrefix
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|table
argument_list|)
expr_stmt|;
specifier|final
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
name|changeTablePrevNextLinks
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
name|buttons
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|registerKeys ()
specifier|public
name|void
name|registerKeys
parameter_list|()
block|{
name|super
operator|.
name|registerKeys
argument_list|()
expr_stmt|;
name|table
operator|.
name|setRegisterKeys
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|loadCallback ()
specifier|protected
name|AsyncCallback
argument_list|<
name|ChangeList
argument_list|>
name|loadCallback
parameter_list|()
block|{
return|return
operator|new
name|ScreenLoadCallback
argument_list|<
name|ChangeList
argument_list|>
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|preDisplay
parameter_list|(
name|ChangeList
name|result
parameter_list|)
block|{
name|display
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
DECL|method|display (ChangeList result)
specifier|protected
name|void
name|display
parameter_list|(
name|ChangeList
name|result
parameter_list|)
block|{
name|changes
operator|=
name|result
expr_stmt|;
if|if
condition|(
name|changes
operator|.
name|length
argument_list|()
operator|!=
literal|0
condition|)
block|{
if|if
condition|(
name|start
operator|>
literal|0
condition|)
block|{
name|int
name|p
init|=
name|start
operator|-
name|pageSize
decl_stmt|;
name|prev
operator|.
name|setTargetHistoryToken
argument_list|(
name|anchorPrefix
operator|+
operator|(
name|p
operator|>
literal|0
condition|?
literal|","
operator|+
name|p
else|:
literal|""
operator|)
argument_list|)
expr_stmt|;
name|prev
operator|.
name|setVisible
argument_list|(
literal|true
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
name|int
name|n
init|=
name|start
operator|+
name|changes
operator|.
name|length
argument_list|()
decl_stmt|;
name|next
operator|.
name|setTargetHistoryToken
argument_list|(
name|anchorPrefix
operator|+
literal|","
operator|+
name|n
argument_list|)
expr_stmt|;
name|next
operator|.
name|setVisible
argument_list|(
name|changes
operator|.
name|get
argument_list|(
name|changes
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|_more_changes
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|table
operator|.
name|updateColumnsForLabels
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|section
operator|.
name|display
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|table
operator|.
name|finishDisplay
argument_list|()
expr_stmt|;
block|}
DECL|class|DoLinkCommand
specifier|private
specifier|static
specifier|final
class|class
name|DoLinkCommand
extends|extends
name|KeyCommand
block|{
DECL|field|link
specifier|private
specifier|final
name|Hyperlink
name|link
decl_stmt|;
DECL|method|DoLinkCommand (int mask, char key, String help, Hyperlink l)
specifier|private
name|DoLinkCommand
parameter_list|(
name|int
name|mask
parameter_list|,
name|char
name|key
parameter_list|,
name|String
name|help
parameter_list|,
name|Hyperlink
name|l
parameter_list|)
block|{
name|super
argument_list|(
name|mask
argument_list|,
name|key
argument_list|,
name|help
argument_list|)
expr_stmt|;
name|link
operator|=
name|l
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onKeyPress (final KeyPressEvent event)
specifier|public
name|void
name|onKeyPress
parameter_list|(
specifier|final
name|KeyPressEvent
name|event
parameter_list|)
block|{
if|if
condition|(
name|link
operator|.
name|isVisible
argument_list|()
condition|)
block|{
name|History
operator|.
name|newItem
argument_list|(
name|link
operator|.
name|getTargetHistoryToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

