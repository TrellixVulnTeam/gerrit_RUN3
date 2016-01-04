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
name|info
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
name|info
operator|.
name|AccountInfo
operator|.
name|AvatarInfo
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
name|gwt
operator|.
name|event
operator|.
name|dom
operator|.
name|client
operator|.
name|LoadEvent
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
name|LoadHandler
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
name|Timer
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
name|UIObject
import|;
end_import

begin_class
DECL|class|AvatarImage
specifier|public
class|class
name|AvatarImage
extends|extends
name|Image
implements|implements
name|LoadHandler
block|{
DECL|method|AvatarImage ()
specifier|public
name|AvatarImage
parameter_list|()
block|{
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|addLoadHandler
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
comment|/** A default sized avatar image. */
DECL|method|AvatarImage (AccountInfo account)
specifier|public
name|AvatarImage
parameter_list|(
name|AccountInfo
name|account
parameter_list|)
block|{
name|this
argument_list|(
name|account
argument_list|,
name|AccountInfo
operator|.
name|AvatarInfo
operator|.
name|DEFAULT_SIZE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * An avatar image for the given account using the requested size.    *    * @param account The account in which we are interested    * @param size A requested size. Note that the size can be ignored depending    *        on the avatar provider. A size<= 0 indicates to let the provider    *        decide a default size.    */
DECL|method|AvatarImage (AccountInfo account, int size)
specifier|public
name|AvatarImage
parameter_list|(
name|AccountInfo
name|account
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|this
argument_list|(
name|account
argument_list|,
name|size
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * An avatar image for the given account using the requested size.    *    * @param account The account in which we are interested    * @param size A requested size. Note that the size can be ignored depending    *        on the avatar provider. A size<= 0 indicates to let the provider    *        decide a default size.    * @param addPopup show avatar popup with user info on hovering over the    *        avatar image    */
DECL|method|AvatarImage (AccountInfo account, int size, boolean addPopup)
specifier|public
name|AvatarImage
parameter_list|(
name|AccountInfo
name|account
parameter_list|,
name|int
name|size
parameter_list|,
name|boolean
name|addPopup
parameter_list|)
block|{
name|addLoadHandler
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|setAccount
argument_list|(
name|account
argument_list|,
name|size
argument_list|,
name|addPopup
argument_list|)
expr_stmt|;
block|}
DECL|method|setAccount (AccountInfo account, int size, boolean addPopup)
specifier|public
name|void
name|setAccount
parameter_list|(
name|AccountInfo
name|account
parameter_list|,
name|int
name|size
parameter_list|,
name|boolean
name|addPopup
parameter_list|)
block|{
if|if
condition|(
name|account
operator|==
literal|null
condition|)
block|{
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isGerritServer
argument_list|(
name|account
argument_list|)
condition|)
block|{
name|setVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|setResource
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|gerritAvatar26
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|account
operator|.
name|hasAvatarInfo
argument_list|()
condition|)
block|{
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|AvatarInfo
name|info
init|=
name|account
operator|.
name|avatar
argument_list|(
name|size
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|setWidth
argument_list|(
name|info
operator|.
name|width
argument_list|()
operator|>
literal|0
condition|?
name|info
operator|.
name|width
argument_list|()
operator|+
literal|"px"
else|:
literal|""
argument_list|)
expr_stmt|;
name|setHeight
argument_list|(
name|info
operator|.
name|height
argument_list|()
operator|>
literal|0
condition|?
name|info
operator|.
name|height
argument_list|()
operator|+
literal|"px"
else|:
literal|""
argument_list|)
expr_stmt|;
name|setUrl
argument_list|(
name|info
operator|.
name|url
argument_list|()
argument_list|)
expr_stmt|;
name|popup
argument_list|(
name|account
argument_list|,
name|addPopup
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|account
operator|.
name|email
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|loadAvatar
argument_list|(
name|account
argument_list|,
name|size
argument_list|,
name|addPopup
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|account
operator|.
name|email
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|loadAvatar
argument_list|(
name|account
argument_list|,
name|size
argument_list|,
name|addPopup
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|loadAvatar (AccountInfo account, int size, boolean addPopup)
specifier|private
name|void
name|loadAvatar
parameter_list|(
name|AccountInfo
name|account
parameter_list|,
name|int
name|size
parameter_list|,
name|boolean
name|addPopup
parameter_list|)
block|{
if|if
condition|(
operator|!
name|Gerrit
operator|.
name|info
argument_list|()
operator|.
name|plugin
argument_list|()
operator|.
name|hasAvatars
argument_list|()
condition|)
block|{
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// TODO Kill /accounts/*/avatar URL.
name|String
name|u
init|=
name|account
operator|.
name|email
argument_list|()
decl_stmt|;
if|if
condition|(
name|Gerrit
operator|.
name|isSignedIn
argument_list|()
operator|&&
name|u
operator|.
name|equals
argument_list|(
name|Gerrit
operator|.
name|getUserAccount
argument_list|()
operator|.
name|email
argument_list|()
argument_list|)
condition|)
block|{
name|u
operator|=
literal|"self"
expr_stmt|;
block|}
name|RestApi
name|api
init|=
operator|new
name|RestApi
argument_list|(
literal|"/accounts/"
argument_list|)
operator|.
name|id
argument_list|(
name|u
argument_list|)
operator|.
name|view
argument_list|(
literal|"avatar"
argument_list|)
decl_stmt|;
if|if
condition|(
name|size
operator|>
literal|0
condition|)
block|{
name|api
operator|.
name|addParameter
argument_list|(
literal|"s"
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|setSize
argument_list|(
literal|""
argument_list|,
name|size
operator|+
literal|"px"
argument_list|)
expr_stmt|;
block|}
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|setUrl
argument_list|(
name|api
operator|.
name|url
argument_list|()
argument_list|)
expr_stmt|;
name|popup
argument_list|(
name|account
argument_list|,
name|addPopup
argument_list|)
expr_stmt|;
block|}
DECL|method|popup (AccountInfo account, boolean addPopup)
specifier|private
name|void
name|popup
parameter_list|(
name|AccountInfo
name|account
parameter_list|,
name|boolean
name|addPopup
parameter_list|)
block|{
if|if
condition|(
name|addPopup
condition|)
block|{
name|PopupHandler
name|popupHandler
init|=
operator|new
name|PopupHandler
argument_list|(
name|account
argument_list|,
name|this
argument_list|)
decl_stmt|;
name|addMouseOverHandler
argument_list|(
name|popupHandler
argument_list|)
expr_stmt|;
name|addMouseOutHandler
argument_list|(
name|popupHandler
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|onLoad (LoadEvent event)
specifier|public
name|void
name|onLoad
parameter_list|(
name|LoadEvent
name|event
parameter_list|)
block|{
name|setVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|isGerritServer (AccountInfo account)
specifier|private
specifier|static
name|boolean
name|isGerritServer
parameter_list|(
name|AccountInfo
name|account
parameter_list|)
block|{
return|return
name|account
operator|.
name|_accountId
argument_list|()
operator|==
literal|0
operator|&&
name|Util
operator|.
name|C
operator|.
name|messageNoAuthor
argument_list|()
operator|.
name|equals
argument_list|(
name|account
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
DECL|class|PopupHandler
specifier|private
specifier|static
class|class
name|PopupHandler
implements|implements
name|MouseOverHandler
implements|,
name|MouseOutHandler
block|{
DECL|field|account
specifier|private
specifier|final
name|AccountInfo
name|account
decl_stmt|;
DECL|field|target
specifier|private
specifier|final
name|UIObject
name|target
decl_stmt|;
DECL|field|popup
specifier|private
name|UserPopupPanel
name|popup
decl_stmt|;
DECL|field|showTimer
specifier|private
name|Timer
name|showTimer
decl_stmt|;
DECL|field|hideTimer
specifier|private
name|Timer
name|hideTimer
decl_stmt|;
DECL|method|PopupHandler (AccountInfo account, UIObject target)
specifier|public
name|PopupHandler
parameter_list|(
name|AccountInfo
name|account
parameter_list|,
name|UIObject
name|target
parameter_list|)
block|{
name|this
operator|.
name|account
operator|=
name|account
expr_stmt|;
name|this
operator|.
name|target
operator|=
name|target
expr_stmt|;
block|}
DECL|method|createPopupPanel (AccountInfo account)
specifier|private
name|UserPopupPanel
name|createPopupPanel
parameter_list|(
name|AccountInfo
name|account
parameter_list|)
block|{
name|UserPopupPanel
name|popup
init|=
operator|new
name|UserPopupPanel
argument_list|(
name|account
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|popup
operator|.
name|addDomHandler
argument_list|(
operator|new
name|MouseOverHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onMouseOver
parameter_list|(
name|MouseOverEvent
name|event
parameter_list|)
block|{
name|scheduleShow
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|,
name|MouseOverEvent
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|popup
operator|.
name|addDomHandler
argument_list|(
operator|new
name|MouseOutHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onMouseOut
parameter_list|(
name|MouseOutEvent
name|event
parameter_list|)
block|{
name|scheduleHide
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|,
name|MouseOutEvent
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|popup
return|;
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
name|scheduleShow
argument_list|()
expr_stmt|;
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
name|scheduleHide
argument_list|()
expr_stmt|;
block|}
DECL|method|scheduleShow ()
specifier|private
name|void
name|scheduleShow
parameter_list|()
block|{
if|if
condition|(
name|hideTimer
operator|!=
literal|null
condition|)
block|{
name|hideTimer
operator|.
name|cancel
argument_list|()
expr_stmt|;
name|hideTimer
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|popup
operator|!=
literal|null
operator|&&
name|popup
operator|.
name|isShowing
argument_list|()
operator|&&
name|popup
operator|.
name|isVisible
argument_list|()
operator|)
operator|||
name|showTimer
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|showTimer
operator|=
operator|new
name|Timer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
if|if
condition|(
name|popup
operator|==
literal|null
condition|)
block|{
name|popup
operator|=
name|createPopupPanel
argument_list|(
name|account
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|popup
operator|.
name|isShowing
argument_list|()
operator|||
operator|!
name|popup
operator|.
name|isVisible
argument_list|()
condition|)
block|{
name|popup
operator|.
name|showRelativeTo
argument_list|(
name|target
argument_list|)
expr_stmt|;
block|}
block|}
block|}
expr_stmt|;
name|showTimer
operator|.
name|schedule
argument_list|(
literal|600
argument_list|)
expr_stmt|;
block|}
DECL|method|scheduleHide ()
specifier|private
name|void
name|scheduleHide
parameter_list|()
block|{
if|if
condition|(
name|showTimer
operator|!=
literal|null
condition|)
block|{
name|showTimer
operator|.
name|cancel
argument_list|()
expr_stmt|;
name|showTimer
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|popup
operator|==
literal|null
operator|||
operator|!
name|popup
operator|.
name|isShowing
argument_list|()
operator|||
operator|!
name|popup
operator|.
name|isVisible
argument_list|()
operator|||
name|hideTimer
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|hideTimer
operator|=
operator|new
name|Timer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|popup
operator|.
name|hide
argument_list|()
expr_stmt|;
block|}
block|}
expr_stmt|;
name|hideTimer
operator|.
name|schedule
argument_list|(
literal|50
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

