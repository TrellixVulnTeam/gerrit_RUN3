begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright 2008 Google Inc.
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
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|FormatUtil
operator|.
name|mediumFormat
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
name|Link
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
name|reviewdb
operator|.
name|Account
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
name|Common
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
name|ui
operator|.
name|AccountScreen
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
name|LazyTabChild
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
name|SourcesTabEvents
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
name|TabListener
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
name|TabPanel
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
name|HTMLTable
operator|.
name|CellFormatter
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
name|List
import|;
end_import

begin_class
DECL|class|AccountSettings
specifier|public
class|class
name|AccountSettings
extends|extends
name|AccountScreen
block|{
DECL|field|initialTabToken
specifier|private
name|String
name|initialTabToken
decl_stmt|;
DECL|field|labelIdx
DECL|field|fieldIdx
specifier|private
name|int
name|labelIdx
decl_stmt|,
name|fieldIdx
decl_stmt|;
DECL|field|info
specifier|private
name|Grid
name|info
decl_stmt|;
DECL|field|tabTokens
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|tabTokens
decl_stmt|;
DECL|field|tabs
specifier|private
name|TabPanel
name|tabs
decl_stmt|;
DECL|field|prefsPanel
specifier|private
name|PreferencePanel
name|prefsPanel
decl_stmt|;
DECL|method|AccountSettings (final String tabToken)
specifier|public
name|AccountSettings
parameter_list|(
specifier|final
name|String
name|tabToken
parameter_list|)
block|{
name|super
argument_list|(
name|Util
operator|.
name|C
operator|.
name|accountSettingsHeading
argument_list|()
argument_list|)
expr_stmt|;
name|initialTabToken
operator|=
name|tabToken
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getScreenCacheToken ()
specifier|public
name|Object
name|getScreenCacheToken
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|recycleThis (final Screen newScreen)
specifier|public
name|Screen
name|recycleThis
parameter_list|(
specifier|final
name|Screen
name|newScreen
parameter_list|)
block|{
specifier|final
name|AccountSettings
name|s
init|=
operator|(
name|AccountSettings
operator|)
name|newScreen
decl_stmt|;
name|initialTabToken
operator|=
name|s
operator|.
name|initialTabToken
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|onLoad ()
specifier|public
name|void
name|onLoad
parameter_list|()
block|{
if|if
condition|(
name|info
operator|==
literal|null
condition|)
block|{
name|initUI
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|onLoad
argument_list|()
expr_stmt|;
name|display
argument_list|(
name|Gerrit
operator|.
name|getUserAccount
argument_list|()
argument_list|)
expr_stmt|;
name|tabs
operator|.
name|selectTab
argument_list|(
name|tabTokens
operator|.
name|indexOf
argument_list|(
name|initialTabToken
argument_list|)
argument_list|)
expr_stmt|;
name|Util
operator|.
name|ACCOUNT_SVC
operator|.
name|myAccount
argument_list|(
operator|new
name|GerritCallback
argument_list|<
name|Account
argument_list|>
argument_list|()
block|{
specifier|public
name|void
name|onSuccess
parameter_list|(
specifier|final
name|Account
name|result
parameter_list|)
block|{
if|if
condition|(
name|isAttached
argument_list|()
condition|)
block|{
name|display
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|initUI ()
specifier|private
name|void
name|initUI
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
name|info
operator|=
operator|new
name|Grid
argument_list|(
literal|5
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|info
operator|.
name|setStyleName
argument_list|(
literal|"gerrit-InfoBlock"
argument_list|)
expr_stmt|;
name|info
operator|.
name|addStyleName
argument_list|(
literal|"gerrit-AccountInfoBlock"
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|infoRow
argument_list|(
literal|0
argument_list|,
name|Util
operator|.
name|C
operator|.
name|fullName
argument_list|()
argument_list|)
expr_stmt|;
name|infoRow
argument_list|(
literal|1
argument_list|,
name|Util
operator|.
name|C
operator|.
name|preferredEmail
argument_list|()
argument_list|)
expr_stmt|;
name|infoRow
argument_list|(
literal|2
argument_list|,
name|Util
operator|.
name|C
operator|.
name|sshUserName
argument_list|()
argument_list|)
expr_stmt|;
name|infoRow
argument_list|(
literal|3
argument_list|,
name|Util
operator|.
name|C
operator|.
name|registeredOn
argument_list|()
argument_list|)
expr_stmt|;
name|infoRow
argument_list|(
literal|4
argument_list|,
name|Util
operator|.
name|C
operator|.
name|accountId
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|CellFormatter
name|fmt
init|=
name|info
operator|.
name|getCellFormatter
argument_list|()
decl_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|"topmost"
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
literal|"topmost"
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
literal|4
argument_list|,
literal|0
argument_list|,
literal|"bottomheader"
argument_list|)
expr_stmt|;
name|prefsPanel
operator|=
operator|new
name|PreferencePanel
argument_list|()
expr_stmt|;
name|tabTokens
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|tabs
operator|=
operator|new
name|TabPanel
argument_list|()
expr_stmt|;
name|tabs
operator|.
name|setWidth
argument_list|(
literal|"98%"
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|tabs
argument_list|)
expr_stmt|;
name|tabs
operator|.
name|add
argument_list|(
name|prefsPanel
argument_list|,
name|Util
operator|.
name|C
operator|.
name|tabPreferences
argument_list|()
argument_list|)
expr_stmt|;
name|tabTokens
operator|.
name|add
argument_list|(
name|Link
operator|.
name|SETTINGS
argument_list|)
expr_stmt|;
name|tabs
operator|.
name|add
argument_list|(
operator|new
name|LazyTabChild
argument_list|<
name|ContactPanel
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|ContactPanel
name|create
parameter_list|()
block|{
return|return
operator|new
name|ContactPanel
argument_list|(
name|AccountSettings
operator|.
name|this
argument_list|)
return|;
block|}
block|}
argument_list|,
name|Util
operator|.
name|C
operator|.
name|tabContactInformation
argument_list|()
argument_list|)
expr_stmt|;
name|tabTokens
operator|.
name|add
argument_list|(
name|Link
operator|.
name|SETTINGS_CONTACT
argument_list|)
expr_stmt|;
name|tabs
operator|.
name|add
argument_list|(
operator|new
name|LazyTabChild
argument_list|<
name|SshKeyPanel
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|SshKeyPanel
name|create
parameter_list|()
block|{
return|return
operator|new
name|SshKeyPanel
argument_list|()
return|;
block|}
block|}
argument_list|,
name|Util
operator|.
name|C
operator|.
name|tabSshKeys
argument_list|()
argument_list|)
expr_stmt|;
name|tabTokens
operator|.
name|add
argument_list|(
name|Link
operator|.
name|SETTINGS_SSHKEYS
argument_list|)
expr_stmt|;
name|tabs
operator|.
name|add
argument_list|(
operator|new
name|LazyTabChild
argument_list|<
name|ExternalIdPanel
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|ExternalIdPanel
name|create
parameter_list|()
block|{
return|return
operator|new
name|ExternalIdPanel
argument_list|()
return|;
block|}
block|}
argument_list|,
name|Util
operator|.
name|C
operator|.
name|tabWebIdentities
argument_list|()
argument_list|)
expr_stmt|;
name|tabTokens
operator|.
name|add
argument_list|(
name|Link
operator|.
name|SETTINGS_WEBIDENT
argument_list|)
expr_stmt|;
if|if
condition|(
name|Common
operator|.
name|getGerritConfig
argument_list|()
operator|.
name|isUseContributorAgreements
argument_list|()
condition|)
block|{
name|tabs
operator|.
name|add
argument_list|(
operator|new
name|LazyTabChild
argument_list|<
name|AgreementPanel
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|AgreementPanel
name|create
parameter_list|()
block|{
return|return
operator|new
name|AgreementPanel
argument_list|()
return|;
block|}
block|}
argument_list|,
name|Util
operator|.
name|C
operator|.
name|tabAgreements
argument_list|()
argument_list|)
expr_stmt|;
name|tabTokens
operator|.
name|add
argument_list|(
name|Link
operator|.
name|SETTINGS_AGREEMENTS
argument_list|)
expr_stmt|;
block|}
name|tabs
operator|.
name|addTabListener
argument_list|(
operator|new
name|TabListener
argument_list|()
block|{
specifier|public
name|boolean
name|onBeforeTabSelected
parameter_list|(
name|SourcesTabEvents
name|sender
parameter_list|,
name|int
name|tabIndex
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|void
name|onTabSelected
parameter_list|(
name|SourcesTabEvents
name|sender
parameter_list|,
name|int
name|tabIndex
parameter_list|)
block|{
name|Gerrit
operator|.
name|display
argument_list|(
name|tabTokens
operator|.
name|get
argument_list|(
name|tabIndex
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|infoRow (final int row, final String name)
specifier|private
name|void
name|infoRow
parameter_list|(
specifier|final
name|int
name|row
parameter_list|,
specifier|final
name|String
name|name
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
name|getCellFormatter
argument_list|()
operator|.
name|addStyleName
argument_list|(
name|row
argument_list|,
literal|0
argument_list|,
literal|"header"
argument_list|)
expr_stmt|;
block|}
DECL|method|display (final Account account)
name|void
name|display
parameter_list|(
specifier|final
name|Account
name|account
parameter_list|)
block|{
name|info
operator|.
name|setText
argument_list|(
literal|0
argument_list|,
name|fieldIdx
argument_list|,
name|account
operator|.
name|getFullName
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|setText
argument_list|(
literal|1
argument_list|,
name|fieldIdx
argument_list|,
name|account
operator|.
name|getPreferredEmail
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|setText
argument_list|(
literal|2
argument_list|,
name|fieldIdx
argument_list|,
name|account
operator|.
name|getSshUserName
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|setText
argument_list|(
literal|3
argument_list|,
name|fieldIdx
argument_list|,
name|mediumFormat
argument_list|(
name|account
operator|.
name|getRegisteredOn
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setText
argument_list|(
literal|4
argument_list|,
name|fieldIdx
argument_list|,
name|account
operator|.
name|getId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|prefsPanel
operator|.
name|display
argument_list|(
name|account
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

