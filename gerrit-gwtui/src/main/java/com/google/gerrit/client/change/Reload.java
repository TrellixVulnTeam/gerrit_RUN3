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
name|changes
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
name|MouseOutEvent
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
name|MouseOutHandler
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
name|MouseOverEvent
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
name|MouseOverHandler
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

begin_class
DECL|class|Reload
class|class
name|Reload
extends|extends
name|Image
implements|implements
name|ClickHandler
implements|,
name|MouseOverHandler
implements|,
name|MouseOutHandler
block|{
DECL|field|changeId
specifier|private
name|Change
operator|.
name|Id
name|changeId
decl_stmt|;
DECL|field|in
specifier|private
name|boolean
name|in
decl_stmt|;
DECL|method|Reload ()
name|Reload
parameter_list|()
block|{
name|setResource
argument_list|(
name|Resources
operator|.
name|I
operator|.
name|reload_black
argument_list|()
argument_list|)
expr_stmt|;
name|addClickHandler
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|addMouseOverHandler
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|addMouseOutHandler
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|set (ChangeInfo info)
name|void
name|set
parameter_list|(
name|ChangeInfo
name|info
parameter_list|)
block|{
name|changeId
operator|=
name|info
operator|.
name|legacy_id
argument_list|()
expr_stmt|;
block|}
DECL|method|reload ()
name|void
name|reload
parameter_list|()
block|{
name|Gerrit
operator|.
name|display
argument_list|(
name|PageLinks
operator|.
name|toChange2
argument_list|(
name|changeId
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onMouseOver (MouseOverEvent event)
specifier|public
name|void
name|onMouseOver
parameter_list|(
name|MouseOverEvent
name|event
parameter_list|)
block|{
if|if
condition|(
operator|!
name|in
condition|)
block|{
name|in
operator|=
literal|true
expr_stmt|;
name|setResource
argument_list|(
name|Resources
operator|.
name|I
operator|.
name|reload_white
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|onMouseOut (MouseOutEvent event)
specifier|public
name|void
name|onMouseOut
parameter_list|(
name|MouseOutEvent
name|event
parameter_list|)
block|{
if|if
condition|(
name|in
condition|)
block|{
name|in
operator|=
literal|false
expr_stmt|;
name|setResource
argument_list|(
name|Resources
operator|.
name|I
operator|.
name|reload_black
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|onClick (ClickEvent e)
specifier|public
name|void
name|onClick
parameter_list|(
name|ClickEvent
name|e
parameter_list|)
block|{
name|e
operator|.
name|preventDefault
argument_list|()
expr_stmt|;
name|e
operator|.
name|stopPropagation
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

