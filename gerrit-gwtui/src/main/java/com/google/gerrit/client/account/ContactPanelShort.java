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
DECL|package|com.google.gerrit.client.account
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|account
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
name|ErrorDialog
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
name|rpc
operator|.
name|CallbackGroup
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
name|NativeString
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
name|OnEditEnabler
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
name|common
operator|.
name|errors
operator|.
name|EmailException
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
name|AccountFieldName
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
name|ChangeEvent
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
name|ChangeHandler
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
name|i18n
operator|.
name|client
operator|.
name|LocaleInfo
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
name|FormPanel
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
name|FormPanel
operator|.
name|SubmitEvent
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
name|Grid
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
name|HTML
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
name|ListBox
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
name|VerticalPanel
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
name|NpTextBox
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
name|user
operator|.
name|client
operator|.
name|AutoCenterDialogBox
import|;
end_import

begin_class
DECL|class|ContactPanelShort
class|class
name|ContactPanelShort
extends|extends
name|Composite
block|{
DECL|field|body
specifier|protected
specifier|final
name|FlowPanel
name|body
decl_stmt|;
DECL|field|labelIdx
specifier|protected
name|int
name|labelIdx
decl_stmt|;
DECL|field|fieldIdx
specifier|protected
name|int
name|fieldIdx
decl_stmt|;
DECL|field|save
specifier|protected
name|Button
name|save
decl_stmt|;
DECL|field|currentEmail
specifier|private
name|String
name|currentEmail
decl_stmt|;
DECL|field|haveAccount
specifier|protected
name|boolean
name|haveAccount
decl_stmt|;
DECL|field|haveEmails
specifier|private
name|boolean
name|haveEmails
decl_stmt|;
DECL|field|nameTxt
name|NpTextBox
name|nameTxt
decl_stmt|;
DECL|field|emailPick
specifier|private
name|ListBox
name|emailPick
decl_stmt|;
DECL|field|registerNewEmail
specifier|private
name|Button
name|registerNewEmail
decl_stmt|;
DECL|field|onEditEnabler
specifier|private
name|OnEditEnabler
name|onEditEnabler
decl_stmt|;
DECL|method|ContactPanelShort ()
name|ContactPanelShort
parameter_list|()
block|{
name|body
operator|=
operator|new
name|FlowPanel
argument_list|()
expr_stmt|;
name|initWidget
argument_list|(
name|body
argument_list|)
expr_stmt|;
block|}
DECL|method|onInitUI ()
specifier|protected
name|void
name|onInitUI
parameter_list|()
block|{
if|if
condition|(
name|LocaleInfo
operator|.
name|getCurrentLocale
argument_list|()
operator|.
name|isRTL
argument_list|()
condition|)
block|{
name|labelIdx
operator|=
literal|1
expr_stmt|;
name|fieldIdx
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|labelIdx
operator|=
literal|0
expr_stmt|;
name|fieldIdx
operator|=
literal|1
expr_stmt|;
block|}
name|nameTxt
operator|=
operator|new
name|NpTextBox
argument_list|()
expr_stmt|;
name|nameTxt
operator|.
name|setVisibleLength
argument_list|(
literal|60
argument_list|)
expr_stmt|;
name|nameTxt
operator|.
name|setReadOnly
argument_list|(
operator|!
name|canEditFullName
argument_list|()
argument_list|)
expr_stmt|;
name|emailPick
operator|=
operator|new
name|ListBox
argument_list|()
expr_stmt|;
specifier|final
name|Grid
name|infoPlainText
init|=
operator|new
name|Grid
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|infoPlainText
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
name|infoBlock
argument_list|()
argument_list|)
expr_stmt|;
name|infoPlainText
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
name|accountInfoBlock
argument_list|()
argument_list|)
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
name|infoPlainText
argument_list|)
expr_stmt|;
name|registerNewEmail
operator|=
operator|new
name|Button
argument_list|(
name|Util
operator|.
name|C
operator|.
name|buttonOpenRegisterNewEmail
argument_list|()
argument_list|)
expr_stmt|;
name|registerNewEmail
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|registerNewEmail
operator|.
name|addClickHandler
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
name|doRegisterNewEmail
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
specifier|final
name|FlowPanel
name|emailLine
init|=
operator|new
name|FlowPanel
argument_list|()
decl_stmt|;
name|emailLine
operator|.
name|add
argument_list|(
name|emailPick
argument_list|)
expr_stmt|;
if|if
condition|(
name|canRegisterNewEmail
argument_list|()
condition|)
block|{
name|emailLine
operator|.
name|add
argument_list|(
name|registerNewEmail
argument_list|)
expr_stmt|;
block|}
name|int
name|row
init|=
literal|0
decl_stmt|;
if|if
condition|(
operator|!
name|Gerrit
operator|.
name|info
argument_list|()
operator|.
name|auth
argument_list|()
operator|.
name|canEdit
argument_list|(
name|AccountFieldName
operator|.
name|USER_NAME
argument_list|)
operator|&&
name|Gerrit
operator|.
name|info
argument_list|()
operator|.
name|auth
argument_list|()
operator|.
name|siteHasUsernames
argument_list|()
condition|)
block|{
name|infoPlainText
operator|.
name|resizeRows
argument_list|(
name|infoPlainText
operator|.
name|getRowCount
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
name|row
argument_list|(
name|infoPlainText
argument_list|,
name|row
operator|++
argument_list|,
name|Util
operator|.
name|C
operator|.
name|userName
argument_list|()
argument_list|,
operator|new
name|UsernameField
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|canEditFullName
argument_list|()
condition|)
block|{
name|FlowPanel
name|nameLine
init|=
operator|new
name|FlowPanel
argument_list|()
decl_stmt|;
name|nameLine
operator|.
name|add
argument_list|(
name|nameTxt
argument_list|)
expr_stmt|;
if|if
condition|(
name|Gerrit
operator|.
name|info
argument_list|()
operator|.
name|auth
argument_list|()
operator|.
name|editFullNameUrl
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|Button
name|edit
init|=
operator|new
name|Button
argument_list|(
name|Util
operator|.
name|C
operator|.
name|linkEditFullName
argument_list|()
argument_list|)
decl_stmt|;
name|edit
operator|.
name|addClickHandler
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
name|Window
operator|.
name|open
argument_list|(
name|Gerrit
operator|.
name|info
argument_list|()
operator|.
name|auth
argument_list|()
operator|.
name|editFullNameUrl
argument_list|()
argument_list|,
literal|"_blank"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|nameLine
operator|.
name|add
argument_list|(
name|edit
argument_list|)
expr_stmt|;
block|}
name|Button
name|reload
init|=
operator|new
name|Button
argument_list|(
name|Util
operator|.
name|C
operator|.
name|linkReloadContact
argument_list|()
argument_list|)
decl_stmt|;
name|reload
operator|.
name|addClickHandler
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
name|Window
operator|.
name|Location
operator|.
name|replace
argument_list|(
name|Gerrit
operator|.
name|loginRedirect
argument_list|(
name|PageLinks
operator|.
name|SETTINGS_CONTACT
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|nameLine
operator|.
name|add
argument_list|(
name|reload
argument_list|)
expr_stmt|;
name|row
argument_list|(
name|infoPlainText
argument_list|,
name|row
operator|++
argument_list|,
name|Util
operator|.
name|C
operator|.
name|contactFieldFullName
argument_list|()
argument_list|,
name|nameLine
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|row
argument_list|(
name|infoPlainText
argument_list|,
name|row
operator|++
argument_list|,
name|Util
operator|.
name|C
operator|.
name|contactFieldFullName
argument_list|()
argument_list|,
name|nameTxt
argument_list|)
expr_stmt|;
block|}
name|row
argument_list|(
name|infoPlainText
argument_list|,
name|row
operator|++
argument_list|,
name|Util
operator|.
name|C
operator|.
name|contactFieldEmail
argument_list|()
argument_list|,
name|emailLine
argument_list|)
expr_stmt|;
name|infoPlainText
operator|.
name|getCellFormatter
argument_list|()
operator|.
name|addStyleName
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|topmost
argument_list|()
argument_list|)
expr_stmt|;
name|infoPlainText
operator|.
name|getCellFormatter
argument_list|()
operator|.
name|addStyleName
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|topmost
argument_list|()
argument_list|)
expr_stmt|;
name|infoPlainText
operator|.
name|getCellFormatter
argument_list|()
operator|.
name|addStyleName
argument_list|(
name|row
operator|-
literal|1
argument_list|,
literal|0
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|bottomheader
argument_list|()
argument_list|)
expr_stmt|;
name|save
operator|=
operator|new
name|Button
argument_list|(
name|Util
operator|.
name|C
operator|.
name|buttonSaveChanges
argument_list|()
argument_list|)
expr_stmt|;
name|save
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|save
operator|.
name|addClickHandler
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
name|doSave
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|emailPick
operator|.
name|addChangeHandler
argument_list|(
operator|new
name|ChangeHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onChange
parameter_list|(
name|ChangeEvent
name|event
parameter_list|)
block|{
specifier|final
name|int
name|idx
init|=
name|emailPick
operator|.
name|getSelectedIndex
argument_list|()
decl_stmt|;
specifier|final
name|String
name|v
init|=
literal|0
operator|<=
name|idx
condition|?
name|emailPick
operator|.
name|getValue
argument_list|(
name|idx
argument_list|)
else|:
literal|null
decl_stmt|;
if|if
condition|(
name|Util
operator|.
name|C
operator|.
name|buttonOpenRegisterNewEmail
argument_list|()
operator|.
name|equals
argument_list|(
name|v
argument_list|)
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|emailPick
operator|.
name|getItemCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|currentEmail
operator|.
name|equals
argument_list|(
name|emailPick
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
name|emailPick
operator|.
name|setSelectedIndex
argument_list|(
name|i
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
name|doRegisterNewEmail
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|save
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|onEditEnabler
operator|=
operator|new
name|OnEditEnabler
argument_list|(
name|save
argument_list|,
name|nameTxt
argument_list|)
expr_stmt|;
block|}
DECL|method|canEditFullName ()
specifier|private
name|boolean
name|canEditFullName
parameter_list|()
block|{
return|return
name|Gerrit
operator|.
name|info
argument_list|()
operator|.
name|auth
argument_list|()
operator|.
name|canEdit
argument_list|(
name|AccountFieldName
operator|.
name|FULL_NAME
argument_list|)
return|;
block|}
DECL|method|canRegisterNewEmail ()
specifier|private
name|boolean
name|canRegisterNewEmail
parameter_list|()
block|{
return|return
name|Gerrit
operator|.
name|info
argument_list|()
operator|.
name|auth
argument_list|()
operator|.
name|canEdit
argument_list|(
name|AccountFieldName
operator|.
name|REGISTER_NEW_EMAIL
argument_list|)
return|;
block|}
DECL|method|hideSaveButton ()
name|void
name|hideSaveButton
parameter_list|()
block|{
name|save
operator|.
name|setVisible
argument_list|(
literal|false
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
name|super
operator|.
name|onLoad
argument_list|()
expr_stmt|;
name|onInitUI
argument_list|()
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
name|save
argument_list|)
expr_stmt|;
name|display
argument_list|(
name|Gerrit
operator|.
name|getUserAccount
argument_list|()
argument_list|)
expr_stmt|;
name|emailPick
operator|.
name|clear
argument_list|()
expr_stmt|;
name|emailPick
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|registerNewEmail
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|haveAccount
operator|=
literal|false
expr_stmt|;
name|haveEmails
operator|=
literal|false
expr_stmt|;
name|CallbackGroup
name|group
init|=
operator|new
name|CallbackGroup
argument_list|()
decl_stmt|;
name|AccountApi
operator|.
name|getName
argument_list|(
literal|"self"
argument_list|,
name|group
operator|.
name|add
argument_list|(
operator|new
name|GerritCallback
argument_list|<
name|NativeString
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|NativeString
name|result
parameter_list|)
block|{
name|nameTxt
operator|.
name|setText
argument_list|(
name|result
operator|.
name|asString
argument_list|()
argument_list|)
expr_stmt|;
name|haveAccount
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
block|{}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|AccountApi
operator|.
name|getEmails
argument_list|(
literal|"self"
argument_list|,
name|group
operator|.
name|addFinal
argument_list|(
operator|new
name|GerritCallback
argument_list|<
name|JsArray
argument_list|<
name|EmailInfo
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|JsArray
argument_list|<
name|EmailInfo
argument_list|>
name|result
parameter_list|)
block|{
for|for
control|(
name|EmailInfo
name|i
range|:
name|Natives
operator|.
name|asList
argument_list|(
name|result
argument_list|)
control|)
block|{
name|emailPick
operator|.
name|addItem
argument_list|(
name|i
operator|.
name|email
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|.
name|isPreferred
argument_list|()
condition|)
block|{
name|currentEmail
operator|=
name|i
operator|.
name|email
argument_list|()
expr_stmt|;
block|}
block|}
name|haveEmails
operator|=
literal|true
expr_stmt|;
name|postLoad
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|postLoad ()
specifier|private
name|void
name|postLoad
parameter_list|()
block|{
if|if
condition|(
name|haveAccount
operator|&&
name|haveEmails
condition|)
block|{
name|updateEmailList
argument_list|()
expr_stmt|;
name|registerNewEmail
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|save
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|onEditEnabler
operator|.
name|updateOriginalValue
argument_list|(
name|nameTxt
argument_list|)
expr_stmt|;
block|}
name|display
argument_list|()
expr_stmt|;
block|}
DECL|method|display ()
name|void
name|display
parameter_list|()
block|{}
DECL|method|row (Grid info, int row, String name, Widget field)
specifier|protected
name|void
name|row
parameter_list|(
name|Grid
name|info
parameter_list|,
name|int
name|row
parameter_list|,
name|String
name|name
parameter_list|,
name|Widget
name|field
parameter_list|)
block|{
name|info
operator|.
name|setText
argument_list|(
name|row
argument_list|,
name|labelIdx
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|info
operator|.
name|setWidget
argument_list|(
name|row
argument_list|,
name|fieldIdx
argument_list|,
name|field
argument_list|)
expr_stmt|;
name|info
operator|.
name|getCellFormatter
argument_list|()
operator|.
name|addStyleName
argument_list|(
name|row
argument_list|,
literal|0
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|header
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|display (AccountInfo account)
specifier|protected
name|void
name|display
parameter_list|(
name|AccountInfo
name|account
parameter_list|)
block|{
name|currentEmail
operator|=
name|account
operator|.
name|email
argument_list|()
expr_stmt|;
name|nameTxt
operator|.
name|setText
argument_list|(
name|account
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|save
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|onEditEnabler
operator|.
name|updateOriginalValue
argument_list|(
name|nameTxt
argument_list|)
expr_stmt|;
block|}
DECL|method|doRegisterNewEmail ()
specifier|private
name|void
name|doRegisterNewEmail
parameter_list|()
block|{
if|if
condition|(
operator|!
name|canRegisterNewEmail
argument_list|()
condition|)
block|{
return|return;
block|}
specifier|final
name|AutoCenterDialogBox
name|box
init|=
operator|new
name|AutoCenterDialogBox
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|VerticalPanel
name|body
init|=
operator|new
name|VerticalPanel
argument_list|()
decl_stmt|;
specifier|final
name|NpTextBox
name|inEmail
init|=
operator|new
name|NpTextBox
argument_list|()
decl_stmt|;
name|inEmail
operator|.
name|setVisibleLength
argument_list|(
literal|60
argument_list|)
expr_stmt|;
specifier|final
name|Button
name|register
init|=
operator|new
name|Button
argument_list|(
name|Util
operator|.
name|C
operator|.
name|buttonSendRegisterNewEmail
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Button
name|cancel
init|=
operator|new
name|Button
argument_list|(
name|Util
operator|.
name|C
operator|.
name|buttonCancel
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|FormPanel
name|form
init|=
operator|new
name|FormPanel
argument_list|()
decl_stmt|;
name|form
operator|.
name|addSubmitHandler
argument_list|(
operator|new
name|FormPanel
operator|.
name|SubmitHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSubmit
parameter_list|(
name|SubmitEvent
name|event
parameter_list|)
block|{
name|event
operator|.
name|cancel
argument_list|()
expr_stmt|;
specifier|final
name|String
name|addr
init|=
name|inEmail
operator|.
name|getText
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|addr
operator|.
name|contains
argument_list|(
literal|"@"
argument_list|)
condition|)
block|{
operator|new
name|ErrorDialog
argument_list|(
name|Util
operator|.
name|C
operator|.
name|invalidUserEmail
argument_list|()
argument_list|)
operator|.
name|center
argument_list|()
expr_stmt|;
return|return;
block|}
name|inEmail
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|register
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|AccountApi
operator|.
name|registerEmail
argument_list|(
literal|"self"
argument_list|,
name|addr
argument_list|,
operator|new
name|GerritCallback
argument_list|<
name|EmailInfo
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|EmailInfo
name|result
parameter_list|)
block|{
name|box
operator|.
name|hide
argument_list|()
expr_stmt|;
if|if
condition|(
name|Gerrit
operator|.
name|info
argument_list|()
operator|.
name|auth
argument_list|()
operator|.
name|isDev
argument_list|()
condition|)
block|{
name|currentEmail
operator|=
name|addr
expr_stmt|;
if|if
condition|(
name|emailPick
operator|.
name|getItemCount
argument_list|()
operator|==
literal|0
condition|)
block|{
name|AccountInfo
name|me
init|=
name|Gerrit
operator|.
name|getUserAccount
argument_list|()
decl_stmt|;
name|me
operator|.
name|email
argument_list|(
name|addr
argument_list|)
expr_stmt|;
name|onSaveSuccess
argument_list|(
name|me
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|save
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|updateEmailList
argument_list|()
expr_stmt|;
block|}
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
block|{
name|inEmail
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|register
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|caught
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
name|EmailException
operator|.
name|MESSAGE
argument_list|)
condition|)
block|{
specifier|final
name|ErrorDialog
name|d
init|=
operator|new
name|ErrorDialog
argument_list|(
name|caught
operator|.
name|getMessage
argument_list|()
operator|.
name|substring
argument_list|(
name|EmailException
operator|.
name|MESSAGE
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|d
operator|.
name|setText
argument_list|(
name|Util
operator|.
name|C
operator|.
name|errorDialogTitleRegisterNewEmail
argument_list|()
argument_list|)
expr_stmt|;
name|d
operator|.
name|center
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|onFailure
argument_list|(
name|caught
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|form
operator|.
name|setWidget
argument_list|(
name|body
argument_list|)
expr_stmt|;
name|register
operator|.
name|addClickHandler
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
name|form
operator|.
name|submit
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|cancel
operator|.
name|addClickHandler
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
name|box
operator|.
name|hide
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
specifier|final
name|FlowPanel
name|buttons
init|=
operator|new
name|FlowPanel
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
name|patchSetActions
argument_list|()
argument_list|)
expr_stmt|;
name|buttons
operator|.
name|add
argument_list|(
name|register
argument_list|)
expr_stmt|;
name|buttons
operator|.
name|add
argument_list|(
name|cancel
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Gerrit
operator|.
name|info
argument_list|()
operator|.
name|auth
argument_list|()
operator|.
name|isDev
argument_list|()
condition|)
block|{
name|body
operator|.
name|add
argument_list|(
operator|new
name|HTML
argument_list|(
name|Util
operator|.
name|C
operator|.
name|descRegisterNewEmail
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|body
operator|.
name|add
argument_list|(
name|inEmail
argument_list|)
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
name|buttons
argument_list|)
expr_stmt|;
name|box
operator|.
name|setText
argument_list|(
name|Util
operator|.
name|C
operator|.
name|titleRegisterNewEmail
argument_list|()
argument_list|)
expr_stmt|;
name|box
operator|.
name|setWidget
argument_list|(
name|form
argument_list|)
expr_stmt|;
name|box
operator|.
name|center
argument_list|()
expr_stmt|;
name|inEmail
operator|.
name|setFocus
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|doSave ()
name|void
name|doSave
parameter_list|()
block|{
specifier|final
name|String
name|newName
decl_stmt|;
name|String
name|name
init|=
name|canEditFullName
argument_list|()
condition|?
name|nameTxt
operator|.
name|getText
argument_list|()
else|:
literal|null
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
operator|&&
name|name
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|newName
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|newName
operator|=
name|name
expr_stmt|;
block|}
specifier|final
name|String
name|newEmail
decl_stmt|;
if|if
condition|(
name|emailPick
operator|.
name|isEnabled
argument_list|()
operator|&&
name|emailPick
operator|.
name|getSelectedIndex
argument_list|()
operator|>=
literal|0
condition|)
block|{
specifier|final
name|String
name|v
init|=
name|emailPick
operator|.
name|getValue
argument_list|(
name|emailPick
operator|.
name|getSelectedIndex
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|Util
operator|.
name|C
operator|.
name|buttonOpenRegisterNewEmail
argument_list|()
operator|.
name|equals
argument_list|(
name|v
argument_list|)
condition|)
block|{
name|newEmail
operator|=
name|currentEmail
expr_stmt|;
block|}
else|else
block|{
name|newEmail
operator|=
name|v
expr_stmt|;
block|}
block|}
else|else
block|{
name|newEmail
operator|=
name|currentEmail
expr_stmt|;
block|}
name|save
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|registerNewEmail
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|CallbackGroup
name|group
init|=
operator|new
name|CallbackGroup
argument_list|()
decl_stmt|;
if|if
condition|(
name|currentEmail
operator|!=
literal|null
operator|&&
operator|!
name|newEmail
operator|.
name|equals
argument_list|(
name|currentEmail
argument_list|)
condition|)
block|{
name|AccountApi
operator|.
name|setPreferredEmail
argument_list|(
literal|"self"
argument_list|,
name|newEmail
argument_list|,
name|group
operator|.
name|add
argument_list|(
operator|new
name|GerritCallback
argument_list|<
name|NativeString
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|NativeString
name|result
parameter_list|)
block|{}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|AccountApi
operator|.
name|setName
argument_list|(
literal|"self"
argument_list|,
name|newName
argument_list|,
name|group
operator|.
name|add
argument_list|(
operator|new
name|GerritCallback
argument_list|<
name|NativeString
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|NativeString
name|result
parameter_list|)
block|{}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|caught
parameter_list|)
block|{
name|save
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|registerNewEmail
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|super
operator|.
name|onFailure
argument_list|(
name|caught
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|group
operator|.
name|done
argument_list|()
expr_stmt|;
name|group
operator|.
name|addListener
argument_list|(
operator|new
name|GerritCallback
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|Void
name|result
parameter_list|)
block|{
name|currentEmail
operator|=
name|newEmail
expr_stmt|;
name|AccountInfo
name|me
init|=
name|Gerrit
operator|.
name|getUserAccount
argument_list|()
decl_stmt|;
name|me
operator|.
name|email
argument_list|(
name|currentEmail
argument_list|)
expr_stmt|;
name|me
operator|.
name|name
argument_list|(
name|newName
argument_list|)
expr_stmt|;
name|onSaveSuccess
argument_list|(
name|me
argument_list|)
expr_stmt|;
name|registerNewEmail
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|onSaveSuccess (AccountInfo result)
name|void
name|onSaveSuccess
parameter_list|(
name|AccountInfo
name|result
parameter_list|)
block|{
name|AccountInfo
name|me
init|=
name|Gerrit
operator|.
name|getUserAccount
argument_list|()
decl_stmt|;
name|me
operator|.
name|name
argument_list|(
name|result
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|me
operator|.
name|email
argument_list|(
name|result
operator|.
name|email
argument_list|()
argument_list|)
expr_stmt|;
name|Gerrit
operator|.
name|refreshMenuBar
argument_list|()
expr_stmt|;
name|display
argument_list|(
name|me
argument_list|)
expr_stmt|;
block|}
DECL|method|emailListIndexOf (String value)
specifier|private
name|int
name|emailListIndexOf
parameter_list|(
name|String
name|value
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|emailPick
operator|.
name|getItemCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|value
operator|.
name|equalsIgnoreCase
argument_list|(
name|emailPick
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
return|return
name|i
return|;
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
DECL|method|updateEmailList ()
specifier|private
name|void
name|updateEmailList
parameter_list|()
block|{
if|if
condition|(
name|currentEmail
operator|!=
literal|null
condition|)
block|{
name|int
name|index
init|=
name|emailListIndexOf
argument_list|(
name|currentEmail
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|==
operator|-
literal|1
condition|)
block|{
name|emailPick
operator|.
name|addItem
argument_list|(
name|currentEmail
argument_list|)
expr_stmt|;
name|emailPick
operator|.
name|setSelectedIndex
argument_list|(
name|emailPick
operator|.
name|getItemCount
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|emailPick
operator|.
name|setSelectedIndex
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|emailPick
operator|.
name|getItemCount
argument_list|()
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|currentEmail
operator|==
literal|null
condition|)
block|{
name|int
name|index
init|=
name|emailListIndexOf
argument_list|(
literal|""
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|!=
operator|-
literal|1
condition|)
block|{
name|emailPick
operator|.
name|removeItem
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
name|emailPick
operator|.
name|insertItem
argument_list|(
literal|""
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|emailPick
operator|.
name|setSelectedIndex
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|emailPick
operator|.
name|setVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|emailPick
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|canRegisterNewEmail
argument_list|()
condition|)
block|{
specifier|final
name|String
name|t
init|=
name|Util
operator|.
name|C
operator|.
name|buttonOpenRegisterNewEmail
argument_list|()
decl_stmt|;
name|int
name|index
init|=
name|emailListIndexOf
argument_list|(
name|t
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|!=
operator|-
literal|1
condition|)
block|{
name|emailPick
operator|.
name|removeItem
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
name|emailPick
operator|.
name|addItem
argument_list|(
literal|"... "
operator|+
name|t
operator|+
literal|"  "
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|emailPick
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

