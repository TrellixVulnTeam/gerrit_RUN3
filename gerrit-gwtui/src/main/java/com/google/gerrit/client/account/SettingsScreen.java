begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2010 The Android Open Source Project
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
name|GerritUiExtensionPoint
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
name|api
operator|.
name|ExtensionPanel
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
name|api
operator|.
name|ExtensionSettingsScreen
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
name|MenuScreen
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
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_class
DECL|class|SettingsScreen
specifier|public
specifier|abstract
class|class
name|SettingsScreen
extends|extends
name|MenuScreen
block|{
DECL|field|allMenuNames
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|allMenuNames
decl_stmt|;
DECL|field|ambiguousMenuNames
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|ambiguousMenuNames
decl_stmt|;
DECL|method|SettingsScreen ()
specifier|public
name|SettingsScreen
parameter_list|()
block|{
name|setRequiresSignIn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|allMenuNames
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
name|ambiguousMenuNames
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
name|linkByGerrit
argument_list|(
name|Util
operator|.
name|C
operator|.
name|tabAccountSummary
argument_list|()
argument_list|,
name|PageLinks
operator|.
name|SETTINGS
argument_list|)
expr_stmt|;
name|linkByGerrit
argument_list|(
name|Util
operator|.
name|C
operator|.
name|tabPreferences
argument_list|()
argument_list|,
name|PageLinks
operator|.
name|SETTINGS_PREFERENCES
argument_list|)
expr_stmt|;
name|linkByGerrit
argument_list|(
name|Util
operator|.
name|C
operator|.
name|tabWatchedProjects
argument_list|()
argument_list|,
name|PageLinks
operator|.
name|SETTINGS_PROJECTS
argument_list|)
expr_stmt|;
name|linkByGerrit
argument_list|(
name|Util
operator|.
name|C
operator|.
name|tabContactInformation
argument_list|()
argument_list|,
name|PageLinks
operator|.
name|SETTINGS_CONTACT
argument_list|)
expr_stmt|;
if|if
condition|(
name|Gerrit
operator|.
name|info
argument_list|()
operator|.
name|hasSshd
argument_list|()
condition|)
block|{
name|linkByGerrit
argument_list|(
name|Util
operator|.
name|C
operator|.
name|tabSshKeys
argument_list|()
argument_list|,
name|PageLinks
operator|.
name|SETTINGS_SSHKEYS
argument_list|)
expr_stmt|;
block|}
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
name|isHttpPasswordSettingsEnabled
argument_list|()
condition|)
block|{
name|linkByGerrit
argument_list|(
name|Util
operator|.
name|C
operator|.
name|tabHttpAccess
argument_list|()
argument_list|,
name|PageLinks
operator|.
name|SETTINGS_HTTP_PASSWORD
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|Gerrit
operator|.
name|info
argument_list|()
operator|.
name|receive
argument_list|()
operator|.
name|enableSignedPush
argument_list|()
condition|)
block|{
name|linkByGerrit
argument_list|(
name|Util
operator|.
name|C
operator|.
name|tabGpgKeys
argument_list|()
argument_list|,
name|PageLinks
operator|.
name|SETTINGS_GPGKEYS
argument_list|)
expr_stmt|;
block|}
name|linkByGerrit
argument_list|(
name|Util
operator|.
name|C
operator|.
name|tabWebIdentities
argument_list|()
argument_list|,
name|PageLinks
operator|.
name|SETTINGS_WEBIDENT
argument_list|)
expr_stmt|;
name|linkByGerrit
argument_list|(
name|Util
operator|.
name|C
operator|.
name|tabMyGroups
argument_list|()
argument_list|,
name|PageLinks
operator|.
name|SETTINGS_MYGROUPS
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
name|useContributorAgreements
argument_list|()
condition|)
block|{
name|linkByGerrit
argument_list|(
name|Util
operator|.
name|C
operator|.
name|tabAgreements
argument_list|()
argument_list|,
name|PageLinks
operator|.
name|SETTINGS_AGREEMENTS
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|pluginName
range|:
name|ExtensionSettingsScreen
operator|.
name|Definition
operator|.
name|plugins
argument_list|()
control|)
block|{
for|for
control|(
name|ExtensionSettingsScreen
operator|.
name|Definition
name|def
range|:
name|Natives
operator|.
name|asList
argument_list|(
name|ExtensionSettingsScreen
operator|.
name|Definition
operator|.
name|get
argument_list|(
name|pluginName
argument_list|)
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|allMenuNames
operator|.
name|add
argument_list|(
name|def
operator|.
name|getMenu
argument_list|()
argument_list|)
condition|)
block|{
name|ambiguousMenuNames
operator|.
name|add
argument_list|(
name|def
operator|.
name|getMenu
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|String
name|pluginName
range|:
name|ExtensionSettingsScreen
operator|.
name|Definition
operator|.
name|plugins
argument_list|()
control|)
block|{
for|for
control|(
name|ExtensionSettingsScreen
operator|.
name|Definition
name|def
range|:
name|Natives
operator|.
name|asList
argument_list|(
name|ExtensionSettingsScreen
operator|.
name|Definition
operator|.
name|get
argument_list|(
name|pluginName
argument_list|)
argument_list|)
control|)
block|{
name|linkByPlugin
argument_list|(
name|pluginName
argument_list|,
name|def
operator|.
name|getMenu
argument_list|()
argument_list|,
name|PageLinks
operator|.
name|toSettings
argument_list|(
name|pluginName
argument_list|,
name|def
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|linkByGerrit (String text, String target)
specifier|private
name|void
name|linkByGerrit
parameter_list|(
name|String
name|text
parameter_list|,
name|String
name|target
parameter_list|)
block|{
name|allMenuNames
operator|.
name|add
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|link
argument_list|(
name|text
argument_list|,
name|target
argument_list|)
expr_stmt|;
block|}
DECL|method|linkByPlugin (String pluginName, String text, String target)
specifier|private
name|void
name|linkByPlugin
parameter_list|(
name|String
name|pluginName
parameter_list|,
name|String
name|text
parameter_list|,
name|String
name|target
parameter_list|)
block|{
if|if
condition|(
name|ambiguousMenuNames
operator|.
name|contains
argument_list|(
name|text
argument_list|)
condition|)
block|{
name|text
operator|+=
literal|" ("
operator|+
name|pluginName
operator|+
literal|")"
expr_stmt|;
block|}
name|link
argument_list|(
name|text
argument_list|,
name|target
argument_list|)
expr_stmt|;
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
name|setPageTitle
argument_list|(
name|Util
operator|.
name|C
operator|.
name|settingsHeading
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|createExtensionPoint ( GerritUiExtensionPoint extensionPoint)
specifier|protected
name|ExtensionPanel
name|createExtensionPoint
parameter_list|(
name|GerritUiExtensionPoint
name|extensionPoint
parameter_list|)
block|{
name|ExtensionPanel
name|extensionPanel
init|=
operator|new
name|ExtensionPanel
argument_list|(
name|extensionPoint
argument_list|)
decl_stmt|;
name|extensionPanel
operator|.
name|putObject
argument_list|(
name|GerritUiExtensionPoint
operator|.
name|Key
operator|.
name|ACCOUNT_INFO
argument_list|,
name|Gerrit
operator|.
name|getUserAccount
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|extensionPanel
return|;
block|}
block|}
end_class

end_unit

