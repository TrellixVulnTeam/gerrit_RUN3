begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2009 The Android Open Source Project
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
DECL|package|com.google.gerrit.client
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
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
name|gwtexpui
operator|.
name|globalkey
operator|.
name|client
operator|.
name|CompoundKeyCommand
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
name|GlobalKey
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
name|KeyCommandSet
import|;
end_import

begin_class
DECL|class|JumpKeys
specifier|public
class|class
name|JumpKeys
block|{
DECL|field|activeHandler
specifier|private
specifier|static
name|HandlerRegistration
name|activeHandler
decl_stmt|;
DECL|field|keys
specifier|private
specifier|static
name|KeyCommandSet
name|keys
decl_stmt|;
DECL|field|bodyWidget
specifier|private
specifier|static
name|Widget
name|bodyWidget
decl_stmt|;
DECL|method|enable (boolean enable)
specifier|public
specifier|static
name|void
name|enable
parameter_list|(
name|boolean
name|enable
parameter_list|)
block|{
if|if
condition|(
name|enable
operator|&&
name|activeHandler
operator|==
literal|null
condition|)
block|{
name|activeHandler
operator|=
name|GlobalKey
operator|.
name|add
argument_list|(
name|bodyWidget
argument_list|,
name|keys
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|enable
operator|&&
name|activeHandler
operator|!=
literal|null
condition|)
block|{
name|activeHandler
operator|.
name|removeHandler
argument_list|()
expr_stmt|;
name|activeHandler
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|register (Widget body)
specifier|static
name|void
name|register
parameter_list|(
name|Widget
name|body
parameter_list|)
block|{
specifier|final
name|KeyCommandSet
name|jumps
init|=
operator|new
name|KeyCommandSet
argument_list|()
decl_stmt|;
name|jumps
operator|.
name|add
argument_list|(
operator|new
name|KeyCommand
argument_list|(
literal|0
argument_list|,
literal|'o'
argument_list|,
name|Gerrit
operator|.
name|C
operator|.
name|jumpAllOpen
argument_list|()
argument_list|)
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
name|Gerrit
operator|.
name|display
argument_list|(
name|PageLinks
operator|.
name|toChangeQuery
argument_list|(
literal|"status:open"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|jumps
operator|.
name|add
argument_list|(
operator|new
name|KeyCommand
argument_list|(
literal|0
argument_list|,
literal|'m'
argument_list|,
name|Gerrit
operator|.
name|C
operator|.
name|jumpAllMerged
argument_list|()
argument_list|)
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
name|Gerrit
operator|.
name|display
argument_list|(
name|PageLinks
operator|.
name|toChangeQuery
argument_list|(
literal|"status:merged"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|jumps
operator|.
name|add
argument_list|(
operator|new
name|KeyCommand
argument_list|(
literal|0
argument_list|,
literal|'a'
argument_list|,
name|Gerrit
operator|.
name|C
operator|.
name|jumpAllAbandoned
argument_list|()
argument_list|)
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
name|Gerrit
operator|.
name|display
argument_list|(
name|PageLinks
operator|.
name|toChangeQuery
argument_list|(
literal|"status:abandoned"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|Gerrit
operator|.
name|isSignedIn
argument_list|()
condition|)
block|{
name|jumps
operator|.
name|add
argument_list|(
operator|new
name|KeyCommand
argument_list|(
literal|0
argument_list|,
literal|'i'
argument_list|,
name|Gerrit
operator|.
name|C
operator|.
name|jumpMine
argument_list|()
argument_list|)
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
name|Gerrit
operator|.
name|display
argument_list|(
name|PageLinks
operator|.
name|MINE
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|jumps
operator|.
name|add
argument_list|(
operator|new
name|KeyCommand
argument_list|(
literal|0
argument_list|,
literal|'d'
argument_list|,
name|Gerrit
operator|.
name|C
operator|.
name|jumpMineDrafts
argument_list|()
argument_list|)
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
name|Gerrit
operator|.
name|display
argument_list|(
name|PageLinks
operator|.
name|toChangeQuery
argument_list|(
literal|"owner:self is:draft"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|jumps
operator|.
name|add
argument_list|(
operator|new
name|KeyCommand
argument_list|(
literal|0
argument_list|,
literal|'c'
argument_list|,
name|Gerrit
operator|.
name|C
operator|.
name|jumpMineDraftComments
argument_list|()
argument_list|)
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
name|Gerrit
operator|.
name|display
argument_list|(
name|PageLinks
operator|.
name|toChangeQuery
argument_list|(
literal|"has:draft"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|jumps
operator|.
name|add
argument_list|(
operator|new
name|KeyCommand
argument_list|(
literal|0
argument_list|,
literal|'w'
argument_list|,
name|Gerrit
operator|.
name|C
operator|.
name|jumpMineWatched
argument_list|()
argument_list|)
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
name|Gerrit
operator|.
name|display
argument_list|(
name|PageLinks
operator|.
name|toChangeQuery
argument_list|(
literal|"is:watched status:open"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|jumps
operator|.
name|add
argument_list|(
operator|new
name|KeyCommand
argument_list|(
literal|0
argument_list|,
literal|'s'
argument_list|,
name|Gerrit
operator|.
name|C
operator|.
name|jumpMineStarred
argument_list|()
argument_list|)
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
name|Gerrit
operator|.
name|display
argument_list|(
name|PageLinks
operator|.
name|toChangeQuery
argument_list|(
literal|"is:starred"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
name|keys
operator|=
operator|new
name|KeyCommandSet
argument_list|(
name|Gerrit
operator|.
name|C
operator|.
name|sectionJumping
argument_list|()
argument_list|)
expr_stmt|;
name|keys
operator|.
name|add
argument_list|(
operator|new
name|CompoundKeyCommand
argument_list|(
literal|0
argument_list|,
literal|'g'
argument_list|,
literal|""
argument_list|,
name|jumps
argument_list|)
argument_list|)
expr_stmt|;
name|bodyWidget
operator|=
name|body
expr_stmt|;
name|activeHandler
operator|=
name|GlobalKey
operator|.
name|add
argument_list|(
name|body
argument_list|,
name|keys
argument_list|)
expr_stmt|;
block|}
DECL|method|JumpKeys ()
specifier|private
name|JumpKeys
parameter_list|()
block|{}
block|}
end_class

end_unit

