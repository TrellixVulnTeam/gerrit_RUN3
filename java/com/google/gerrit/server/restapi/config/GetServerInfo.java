begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2015 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.restapi.config
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|restapi
operator|.
name|config
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
operator|.
name|toList
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|CharMatcher
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Strings
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|data
operator|.
name|ContributorAgreement
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
name|common
operator|.
name|AccountsInfo
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
name|common
operator|.
name|AuthInfo
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
name|common
operator|.
name|ChangeConfigInfo
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
name|common
operator|.
name|DownloadInfo
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
name|common
operator|.
name|DownloadSchemeInfo
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
name|common
operator|.
name|GerritInfo
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
name|common
operator|.
name|PluginConfigInfo
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
name|common
operator|.
name|ReceiveInfo
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
name|common
operator|.
name|ServerInfo
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
name|common
operator|.
name|SshdInfo
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
name|common
operator|.
name|SuggestInfo
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
name|common
operator|.
name|UserConfigInfo
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
name|config
operator|.
name|CloneCommand
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
name|config
operator|.
name|DownloadCommand
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
name|config
operator|.
name|DownloadScheme
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
name|restapi
operator|.
name|Response
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
name|restapi
operator|.
name|RestReadView
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
name|webui
operator|.
name|WebUiPlugin
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
name|server
operator|.
name|EnableSignedPush
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
name|server
operator|.
name|account
operator|.
name|AccountVisibilityProvider
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
name|server
operator|.
name|account
operator|.
name|Realm
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
name|server
operator|.
name|avatar
operator|.
name|AvatarProvider
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
name|server
operator|.
name|change
operator|.
name|ArchiveFormat
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
name|server
operator|.
name|config
operator|.
name|AllProjectsName
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
name|server
operator|.
name|config
operator|.
name|AllUsersName
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
name|server
operator|.
name|config
operator|.
name|AnonymousCowardName
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
name|server
operator|.
name|config
operator|.
name|AuthConfig
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
name|server
operator|.
name|config
operator|.
name|ConfigResource
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
name|server
operator|.
name|config
operator|.
name|ConfigUtil
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
name|server
operator|.
name|config
operator|.
name|GerritServerConfig
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
name|server
operator|.
name|config
operator|.
name|SitePaths
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
name|server
operator|.
name|documentation
operator|.
name|QueryDocumentationExecutor
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
name|server
operator|.
name|index
operator|.
name|change
operator|.
name|ChangeField
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
name|server
operator|.
name|index
operator|.
name|change
operator|.
name|ChangeIndexCollection
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
name|server
operator|.
name|permissions
operator|.
name|PermissionBackendException
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
name|server
operator|.
name|plugincontext
operator|.
name|PluginItemContext
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
name|server
operator|.
name|plugincontext
operator|.
name|PluginMapContext
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
name|server
operator|.
name|plugincontext
operator|.
name|PluginSetContext
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
name|server
operator|.
name|project
operator|.
name|ProjectCache
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
name|server
operator|.
name|restapi
operator|.
name|change
operator|.
name|AllowedFormats
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
name|server
operator|.
name|submit
operator|.
name|MergeSuperSet
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Config
import|;
end_import

begin_class
DECL|class|GetServerInfo
specifier|public
class|class
name|GetServerInfo
implements|implements
name|RestReadView
argument_list|<
name|ConfigResource
argument_list|>
block|{
DECL|field|config
specifier|private
specifier|final
name|Config
name|config
decl_stmt|;
DECL|field|accountVisibilityProvider
specifier|private
specifier|final
name|AccountVisibilityProvider
name|accountVisibilityProvider
decl_stmt|;
DECL|field|authConfig
specifier|private
specifier|final
name|AuthConfig
name|authConfig
decl_stmt|;
DECL|field|realm
specifier|private
specifier|final
name|Realm
name|realm
decl_stmt|;
DECL|field|downloadSchemes
specifier|private
specifier|final
name|PluginMapContext
argument_list|<
name|DownloadScheme
argument_list|>
name|downloadSchemes
decl_stmt|;
DECL|field|downloadCommands
specifier|private
specifier|final
name|PluginMapContext
argument_list|<
name|DownloadCommand
argument_list|>
name|downloadCommands
decl_stmt|;
DECL|field|cloneCommands
specifier|private
specifier|final
name|PluginMapContext
argument_list|<
name|CloneCommand
argument_list|>
name|cloneCommands
decl_stmt|;
DECL|field|plugins
specifier|private
specifier|final
name|PluginSetContext
argument_list|<
name|WebUiPlugin
argument_list|>
name|plugins
decl_stmt|;
DECL|field|archiveFormats
specifier|private
specifier|final
name|AllowedFormats
name|archiveFormats
decl_stmt|;
DECL|field|allProjectsName
specifier|private
specifier|final
name|AllProjectsName
name|allProjectsName
decl_stmt|;
DECL|field|allUsersName
specifier|private
specifier|final
name|AllUsersName
name|allUsersName
decl_stmt|;
DECL|field|anonymousCowardName
specifier|private
specifier|final
name|String
name|anonymousCowardName
decl_stmt|;
DECL|field|avatar
specifier|private
specifier|final
name|PluginItemContext
argument_list|<
name|AvatarProvider
argument_list|>
name|avatar
decl_stmt|;
DECL|field|enableSignedPush
specifier|private
specifier|final
name|boolean
name|enableSignedPush
decl_stmt|;
DECL|field|docSearcher
specifier|private
specifier|final
name|QueryDocumentationExecutor
name|docSearcher
decl_stmt|;
DECL|field|projectCache
specifier|private
specifier|final
name|ProjectCache
name|projectCache
decl_stmt|;
DECL|field|agreementJson
specifier|private
specifier|final
name|AgreementJson
name|agreementJson
decl_stmt|;
DECL|field|indexes
specifier|private
specifier|final
name|ChangeIndexCollection
name|indexes
decl_stmt|;
DECL|field|sitePaths
specifier|private
specifier|final
name|SitePaths
name|sitePaths
decl_stmt|;
annotation|@
name|Inject
DECL|method|GetServerInfo ( @erritServerConfig Config config, AccountVisibilityProvider accountVisibilityProvider, AuthConfig authConfig, Realm realm, PluginMapContext<DownloadScheme> downloadSchemes, PluginMapContext<DownloadCommand> downloadCommands, PluginMapContext<CloneCommand> cloneCommands, PluginSetContext<WebUiPlugin> webUiPlugins, AllowedFormats archiveFormats, AllProjectsName allProjectsName, AllUsersName allUsersName, @AnonymousCowardName String anonymousCowardName, PluginItemContext<AvatarProvider> avatar, @EnableSignedPush boolean enableSignedPush, QueryDocumentationExecutor docSearcher, ProjectCache projectCache, AgreementJson agreementJson, ChangeIndexCollection indexes, SitePaths sitePaths)
specifier|public
name|GetServerInfo
parameter_list|(
annotation|@
name|GerritServerConfig
name|Config
name|config
parameter_list|,
name|AccountVisibilityProvider
name|accountVisibilityProvider
parameter_list|,
name|AuthConfig
name|authConfig
parameter_list|,
name|Realm
name|realm
parameter_list|,
name|PluginMapContext
argument_list|<
name|DownloadScheme
argument_list|>
name|downloadSchemes
parameter_list|,
name|PluginMapContext
argument_list|<
name|DownloadCommand
argument_list|>
name|downloadCommands
parameter_list|,
name|PluginMapContext
argument_list|<
name|CloneCommand
argument_list|>
name|cloneCommands
parameter_list|,
name|PluginSetContext
argument_list|<
name|WebUiPlugin
argument_list|>
name|webUiPlugins
parameter_list|,
name|AllowedFormats
name|archiveFormats
parameter_list|,
name|AllProjectsName
name|allProjectsName
parameter_list|,
name|AllUsersName
name|allUsersName
parameter_list|,
annotation|@
name|AnonymousCowardName
name|String
name|anonymousCowardName
parameter_list|,
name|PluginItemContext
argument_list|<
name|AvatarProvider
argument_list|>
name|avatar
parameter_list|,
annotation|@
name|EnableSignedPush
name|boolean
name|enableSignedPush
parameter_list|,
name|QueryDocumentationExecutor
name|docSearcher
parameter_list|,
name|ProjectCache
name|projectCache
parameter_list|,
name|AgreementJson
name|agreementJson
parameter_list|,
name|ChangeIndexCollection
name|indexes
parameter_list|,
name|SitePaths
name|sitePaths
parameter_list|)
block|{
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
name|this
operator|.
name|accountVisibilityProvider
operator|=
name|accountVisibilityProvider
expr_stmt|;
name|this
operator|.
name|authConfig
operator|=
name|authConfig
expr_stmt|;
name|this
operator|.
name|realm
operator|=
name|realm
expr_stmt|;
name|this
operator|.
name|downloadSchemes
operator|=
name|downloadSchemes
expr_stmt|;
name|this
operator|.
name|downloadCommands
operator|=
name|downloadCommands
expr_stmt|;
name|this
operator|.
name|cloneCommands
operator|=
name|cloneCommands
expr_stmt|;
name|this
operator|.
name|plugins
operator|=
name|webUiPlugins
expr_stmt|;
name|this
operator|.
name|archiveFormats
operator|=
name|archiveFormats
expr_stmt|;
name|this
operator|.
name|allProjectsName
operator|=
name|allProjectsName
expr_stmt|;
name|this
operator|.
name|allUsersName
operator|=
name|allUsersName
expr_stmt|;
name|this
operator|.
name|anonymousCowardName
operator|=
name|anonymousCowardName
expr_stmt|;
name|this
operator|.
name|avatar
operator|=
name|avatar
expr_stmt|;
name|this
operator|.
name|enableSignedPush
operator|=
name|enableSignedPush
expr_stmt|;
name|this
operator|.
name|docSearcher
operator|=
name|docSearcher
expr_stmt|;
name|this
operator|.
name|projectCache
operator|=
name|projectCache
expr_stmt|;
name|this
operator|.
name|agreementJson
operator|=
name|agreementJson
expr_stmt|;
name|this
operator|.
name|indexes
operator|=
name|indexes
expr_stmt|;
name|this
operator|.
name|sitePaths
operator|=
name|sitePaths
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (ConfigResource rsrc)
specifier|public
name|Response
argument_list|<
name|ServerInfo
argument_list|>
name|apply
parameter_list|(
name|ConfigResource
name|rsrc
parameter_list|)
throws|throws
name|PermissionBackendException
block|{
name|ServerInfo
name|info
init|=
operator|new
name|ServerInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|accounts
operator|=
name|getAccountsInfo
argument_list|()
expr_stmt|;
name|info
operator|.
name|auth
operator|=
name|getAuthInfo
argument_list|()
expr_stmt|;
name|info
operator|.
name|change
operator|=
name|getChangeInfo
argument_list|()
expr_stmt|;
name|info
operator|.
name|download
operator|=
name|getDownloadInfo
argument_list|()
expr_stmt|;
name|info
operator|.
name|gerrit
operator|=
name|getGerritInfo
argument_list|()
expr_stmt|;
name|info
operator|.
name|noteDbEnabled
operator|=
literal|true
expr_stmt|;
name|info
operator|.
name|plugin
operator|=
name|getPluginInfo
argument_list|()
expr_stmt|;
name|info
operator|.
name|defaultTheme
operator|=
name|getDefaultTheme
argument_list|()
expr_stmt|;
name|info
operator|.
name|sshd
operator|=
name|getSshdInfo
argument_list|()
expr_stmt|;
name|info
operator|.
name|suggest
operator|=
name|getSuggestInfo
argument_list|()
expr_stmt|;
name|info
operator|.
name|user
operator|=
name|getUserInfo
argument_list|()
expr_stmt|;
name|info
operator|.
name|receive
operator|=
name|getReceiveInfo
argument_list|()
expr_stmt|;
return|return
name|Response
operator|.
name|ok
argument_list|(
name|info
argument_list|)
return|;
block|}
DECL|method|getAccountsInfo ()
specifier|private
name|AccountsInfo
name|getAccountsInfo
parameter_list|()
block|{
name|AccountsInfo
name|info
init|=
operator|new
name|AccountsInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|visibility
operator|=
name|accountVisibilityProvider
operator|.
name|get
argument_list|()
expr_stmt|;
return|return
name|info
return|;
block|}
DECL|method|getAuthInfo ()
specifier|private
name|AuthInfo
name|getAuthInfo
parameter_list|()
throws|throws
name|PermissionBackendException
block|{
name|AuthInfo
name|info
init|=
operator|new
name|AuthInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|authType
operator|=
name|authConfig
operator|.
name|getAuthType
argument_list|()
expr_stmt|;
name|info
operator|.
name|useContributorAgreements
operator|=
name|toBoolean
argument_list|(
name|authConfig
operator|.
name|isUseContributorAgreements
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|editableAccountFields
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|realm
operator|.
name|getEditableFields
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|switchAccountUrl
operator|=
name|authConfig
operator|.
name|getSwitchAccountUrl
argument_list|()
expr_stmt|;
name|info
operator|.
name|gitBasicAuthPolicy
operator|=
name|authConfig
operator|.
name|getGitBasicAuthPolicy
argument_list|()
expr_stmt|;
if|if
condition|(
name|info
operator|.
name|useContributorAgreements
operator|!=
literal|null
condition|)
block|{
name|Collection
argument_list|<
name|ContributorAgreement
argument_list|>
name|agreements
init|=
name|projectCache
operator|.
name|getAllProjects
argument_list|()
operator|.
name|getConfig
argument_list|()
operator|.
name|getContributorAgreements
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|agreements
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|info
operator|.
name|contributorAgreements
operator|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|agreements
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|ContributorAgreement
name|agreement
range|:
name|agreements
control|)
block|{
name|info
operator|.
name|contributorAgreements
operator|.
name|add
argument_list|(
name|agreementJson
operator|.
name|format
argument_list|(
name|agreement
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
switch|switch
condition|(
name|info
operator|.
name|authType
condition|)
block|{
case|case
name|LDAP
case|:
case|case
name|LDAP_BIND
case|:
name|info
operator|.
name|registerUrl
operator|=
name|authConfig
operator|.
name|getRegisterUrl
argument_list|()
expr_stmt|;
name|info
operator|.
name|registerText
operator|=
name|authConfig
operator|.
name|getRegisterText
argument_list|()
expr_stmt|;
name|info
operator|.
name|editFullNameUrl
operator|=
name|authConfig
operator|.
name|getEditFullNameUrl
argument_list|()
expr_stmt|;
break|break;
case|case
name|CUSTOM_EXTENSION
case|:
name|info
operator|.
name|registerUrl
operator|=
name|authConfig
operator|.
name|getRegisterUrl
argument_list|()
expr_stmt|;
name|info
operator|.
name|registerText
operator|=
name|authConfig
operator|.
name|getRegisterText
argument_list|()
expr_stmt|;
name|info
operator|.
name|editFullNameUrl
operator|=
name|authConfig
operator|.
name|getEditFullNameUrl
argument_list|()
expr_stmt|;
name|info
operator|.
name|httpPasswordUrl
operator|=
name|authConfig
operator|.
name|getHttpPasswordUrl
argument_list|()
expr_stmt|;
break|break;
case|case
name|HTTP
case|:
case|case
name|HTTP_LDAP
case|:
name|info
operator|.
name|loginUrl
operator|=
name|authConfig
operator|.
name|getLoginUrl
argument_list|()
expr_stmt|;
name|info
operator|.
name|loginText
operator|=
name|authConfig
operator|.
name|getLoginText
argument_list|()
expr_stmt|;
break|break;
case|case
name|CLIENT_SSL_CERT_LDAP
case|:
case|case
name|DEVELOPMENT_BECOME_ANY_ACCOUNT
case|:
case|case
name|OAUTH
case|:
case|case
name|OPENID
case|:
case|case
name|OPENID_SSO
case|:
break|break;
block|}
return|return
name|info
return|;
block|}
DECL|method|getChangeInfo ()
specifier|private
name|ChangeConfigInfo
name|getChangeInfo
parameter_list|()
block|{
name|ChangeConfigInfo
name|info
init|=
operator|new
name|ChangeConfigInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|allowBlame
operator|=
name|toBoolean
argument_list|(
name|config
operator|.
name|getBoolean
argument_list|(
literal|"change"
argument_list|,
literal|"allowBlame"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|boolean
name|hasAssigneeInIndex
init|=
name|indexes
operator|.
name|getSearchIndex
argument_list|()
operator|.
name|getSchema
argument_list|()
operator|.
name|hasField
argument_list|(
name|ChangeField
operator|.
name|ASSIGNEE
argument_list|)
decl_stmt|;
name|info
operator|.
name|showAssigneeInChangesTable
operator|=
name|toBoolean
argument_list|(
name|config
operator|.
name|getBoolean
argument_list|(
literal|"change"
argument_list|,
literal|"showAssigneeInChangesTable"
argument_list|,
literal|false
argument_list|)
operator|&&
name|hasAssigneeInIndex
argument_list|)
expr_stmt|;
name|info
operator|.
name|largeChange
operator|=
name|config
operator|.
name|getInt
argument_list|(
literal|"change"
argument_list|,
literal|"largeChange"
argument_list|,
literal|500
argument_list|)
expr_stmt|;
name|info
operator|.
name|replyTooltip
operator|=
name|Optional
operator|.
name|ofNullable
argument_list|(
name|config
operator|.
name|getString
argument_list|(
literal|"change"
argument_list|,
literal|null
argument_list|,
literal|"replyTooltip"
argument_list|)
argument_list|)
operator|.
name|orElse
argument_list|(
literal|"Reply and score"
argument_list|)
operator|+
literal|" (Shortcut: a)"
expr_stmt|;
name|info
operator|.
name|replyLabel
operator|=
name|Optional
operator|.
name|ofNullable
argument_list|(
name|config
operator|.
name|getString
argument_list|(
literal|"change"
argument_list|,
literal|null
argument_list|,
literal|"replyLabel"
argument_list|)
argument_list|)
operator|.
name|orElse
argument_list|(
literal|"Reply"
argument_list|)
operator|+
literal|"\u2026"
expr_stmt|;
name|info
operator|.
name|updateDelay
operator|=
operator|(
name|int
operator|)
name|ConfigUtil
operator|.
name|getTimeUnit
argument_list|(
name|config
argument_list|,
literal|"change"
argument_list|,
literal|null
argument_list|,
literal|"updateDelay"
argument_list|,
literal|300
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|info
operator|.
name|submitWholeTopic
operator|=
name|toBoolean
argument_list|(
name|MergeSuperSet
operator|.
name|wholeTopicEnabled
argument_list|(
name|config
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|excludeMergeableInChangeInfo
operator|=
name|toBoolean
argument_list|(
name|this
operator|.
name|config
operator|.
name|getBoolean
argument_list|(
literal|"change"
argument_list|,
literal|"api"
argument_list|,
literal|"excludeMergeableInChangeInfo"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|disablePrivateChanges
operator|=
name|toBoolean
argument_list|(
name|this
operator|.
name|config
operator|.
name|getBoolean
argument_list|(
literal|"change"
argument_list|,
literal|null
argument_list|,
literal|"disablePrivateChanges"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|info
return|;
block|}
DECL|method|getDownloadInfo ()
specifier|private
name|DownloadInfo
name|getDownloadInfo
parameter_list|()
block|{
name|DownloadInfo
name|info
init|=
operator|new
name|DownloadInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|schemes
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|downloadSchemes
operator|.
name|runEach
argument_list|(
name|extension
lambda|->
block|{
name|DownloadScheme
name|scheme
init|=
name|extension
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|scheme
operator|.
name|isEnabled
argument_list|()
operator|&&
name|scheme
operator|.
name|getUrl
argument_list|(
literal|"${project}"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|info
operator|.
name|schemes
operator|.
name|put
argument_list|(
name|extension
operator|.
name|getExportName
argument_list|()
argument_list|,
name|getDownloadSchemeInfo
argument_list|(
name|scheme
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|info
operator|.
name|archives
operator|=
name|archiveFormats
operator|.
name|getAllowed
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|ArchiveFormat
operator|::
name|getShortName
argument_list|)
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|info
return|;
block|}
DECL|method|getDownloadSchemeInfo (DownloadScheme scheme)
specifier|private
name|DownloadSchemeInfo
name|getDownloadSchemeInfo
parameter_list|(
name|DownloadScheme
name|scheme
parameter_list|)
block|{
name|DownloadSchemeInfo
name|info
init|=
operator|new
name|DownloadSchemeInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|url
operator|=
name|scheme
operator|.
name|getUrl
argument_list|(
literal|"${project}"
argument_list|)
expr_stmt|;
name|info
operator|.
name|isAuthRequired
operator|=
name|toBoolean
argument_list|(
name|scheme
operator|.
name|isAuthRequired
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|isAuthSupported
operator|=
name|toBoolean
argument_list|(
name|scheme
operator|.
name|isAuthSupported
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|commands
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|downloadCommands
operator|.
name|runEach
argument_list|(
name|extension
lambda|->
block|{
name|String
name|commandName
init|=
name|extension
operator|.
name|getExportName
argument_list|()
decl_stmt|;
name|DownloadCommand
name|command
init|=
name|extension
operator|.
name|get
argument_list|()
decl_stmt|;
name|String
name|c
init|=
name|command
operator|.
name|getCommand
argument_list|(
name|scheme
argument_list|,
literal|"${project}"
argument_list|,
literal|"${ref}"
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|null
condition|)
block|{
name|info
operator|.
name|commands
operator|.
name|put
argument_list|(
name|commandName
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|info
operator|.
name|cloneCommands
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|cloneCommands
operator|.
name|runEach
argument_list|(
name|extension
lambda|->
block|{
name|String
name|commandName
init|=
name|extension
operator|.
name|getExportName
argument_list|()
decl_stmt|;
name|CloneCommand
name|command
init|=
name|extension
operator|.
name|getProvider
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
name|String
name|c
init|=
name|command
operator|.
name|getCommand
argument_list|(
name|scheme
argument_list|,
literal|"${project-path}/${project-base-name}"
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|null
condition|)
block|{
name|c
operator|=
name|c
operator|.
name|replaceAll
argument_list|(
literal|"\\$\\{project-path\\}/\\$\\{project-base-name\\}"
argument_list|,
literal|"\\$\\{project\\}"
argument_list|)
expr_stmt|;
name|info
operator|.
name|cloneCommands
operator|.
name|put
argument_list|(
name|commandName
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|info
return|;
block|}
DECL|method|getGerritInfo ()
specifier|private
name|GerritInfo
name|getGerritInfo
parameter_list|()
block|{
name|GerritInfo
name|info
init|=
operator|new
name|GerritInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|allProjects
operator|=
name|allProjectsName
operator|.
name|get
argument_list|()
expr_stmt|;
name|info
operator|.
name|allUsers
operator|=
name|allUsersName
operator|.
name|get
argument_list|()
expr_stmt|;
name|info
operator|.
name|reportBugUrl
operator|=
name|config
operator|.
name|getString
argument_list|(
literal|"gerrit"
argument_list|,
literal|null
argument_list|,
literal|"reportBugUrl"
argument_list|)
expr_stmt|;
name|info
operator|.
name|docUrl
operator|=
name|getDocUrl
argument_list|()
expr_stmt|;
name|info
operator|.
name|docSearch
operator|=
name|docSearcher
operator|.
name|isAvailable
argument_list|()
expr_stmt|;
name|info
operator|.
name|editGpgKeys
operator|=
name|toBoolean
argument_list|(
name|enableSignedPush
operator|&&
name|config
operator|.
name|getBoolean
argument_list|(
literal|"gerrit"
argument_list|,
literal|null
argument_list|,
literal|"editGpgKeys"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|primaryWeblinkName
operator|=
name|config
operator|.
name|getString
argument_list|(
literal|"gerrit"
argument_list|,
literal|null
argument_list|,
literal|"primaryWeblinkName"
argument_list|)
expr_stmt|;
return|return
name|info
return|;
block|}
DECL|method|getDocUrl ()
specifier|private
name|String
name|getDocUrl
parameter_list|()
block|{
name|String
name|docUrl
init|=
name|config
operator|.
name|getString
argument_list|(
literal|"gerrit"
argument_list|,
literal|null
argument_list|,
literal|"docUrl"
argument_list|)
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|docUrl
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|CharMatcher
operator|.
name|is
argument_list|(
literal|'/'
argument_list|)
operator|.
name|trimTrailingFrom
argument_list|(
name|docUrl
argument_list|)
operator|+
literal|'/'
return|;
block|}
DECL|method|getPluginInfo ()
specifier|private
name|PluginConfigInfo
name|getPluginInfo
parameter_list|()
block|{
name|PluginConfigInfo
name|info
init|=
operator|new
name|PluginConfigInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|hasAvatars
operator|=
name|toBoolean
argument_list|(
name|avatar
operator|.
name|hasImplementation
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|jsResourcePaths
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|info
operator|.
name|htmlResourcePaths
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|plugins
operator|.
name|runEach
argument_list|(
name|plugin
lambda|->
block|{
name|String
name|path
init|=
name|String
operator|.
name|format
argument_list|(
literal|"plugins/%s/%s"
argument_list|,
name|plugin
operator|.
name|getPluginName
argument_list|()
argument_list|,
name|plugin
operator|.
name|getJavaScriptResourcePath
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|path
operator|.
name|endsWith
argument_list|(
literal|".html"
argument_list|)
condition|)
block|{
name|info
operator|.
name|htmlResourcePaths
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|info
operator|.
name|jsResourcePaths
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|info
return|;
block|}
DECL|field|DEFAULT_THEME
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_THEME
init|=
literal|"/static/"
operator|+
name|SitePaths
operator|.
name|THEME_FILENAME
decl_stmt|;
DECL|method|getDefaultTheme ()
specifier|private
name|String
name|getDefaultTheme
parameter_list|()
block|{
if|if
condition|(
name|config
operator|.
name|getString
argument_list|(
literal|"theme"
argument_list|,
literal|null
argument_list|,
literal|"enableDefault"
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|// If not explicitly enabled or disabled, check for the existence of the theme file.
return|return
name|Files
operator|.
name|exists
argument_list|(
name|sitePaths
operator|.
name|site_theme
argument_list|)
condition|?
name|DEFAULT_THEME
else|:
literal|null
return|;
block|}
if|if
condition|(
name|config
operator|.
name|getBoolean
argument_list|(
literal|"theme"
argument_list|,
literal|null
argument_list|,
literal|"enableDefault"
argument_list|,
literal|true
argument_list|)
condition|)
block|{
comment|// Return non-null theme path without checking for file existence. Even if the file doesn't
comment|// exist under the site path, it may be served from a CDN (in which case it's up to the admin
comment|// to also pass a proper asset path to the index Soy template).
return|return
name|DEFAULT_THEME
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|getSshdInfo ()
specifier|private
name|SshdInfo
name|getSshdInfo
parameter_list|()
block|{
name|String
index|[]
name|addr
init|=
name|config
operator|.
name|getStringList
argument_list|(
literal|"sshd"
argument_list|,
literal|null
argument_list|,
literal|"listenAddress"
argument_list|)
decl_stmt|;
if|if
condition|(
name|addr
operator|.
name|length
operator|==
literal|1
operator|&&
name|isOff
argument_list|(
name|addr
index|[
literal|0
index|]
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|SshdInfo
argument_list|()
return|;
block|}
DECL|method|isOff (String listenHostname)
specifier|private
specifier|static
name|boolean
name|isOff
parameter_list|(
name|String
name|listenHostname
parameter_list|)
block|{
return|return
literal|"off"
operator|.
name|equalsIgnoreCase
argument_list|(
name|listenHostname
argument_list|)
operator|||
literal|"none"
operator|.
name|equalsIgnoreCase
argument_list|(
name|listenHostname
argument_list|)
operator|||
literal|"no"
operator|.
name|equalsIgnoreCase
argument_list|(
name|listenHostname
argument_list|)
return|;
block|}
DECL|method|getSuggestInfo ()
specifier|private
name|SuggestInfo
name|getSuggestInfo
parameter_list|()
block|{
name|SuggestInfo
name|info
init|=
operator|new
name|SuggestInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|from
operator|=
name|config
operator|.
name|getInt
argument_list|(
literal|"suggest"
argument_list|,
literal|"from"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
name|info
return|;
block|}
DECL|method|getUserInfo ()
specifier|private
name|UserConfigInfo
name|getUserInfo
parameter_list|()
block|{
name|UserConfigInfo
name|info
init|=
operator|new
name|UserConfigInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|anonymousCowardName
operator|=
name|anonymousCowardName
expr_stmt|;
return|return
name|info
return|;
block|}
DECL|method|getReceiveInfo ()
specifier|private
name|ReceiveInfo
name|getReceiveInfo
parameter_list|()
block|{
name|ReceiveInfo
name|info
init|=
operator|new
name|ReceiveInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|enableSignedPush
operator|=
name|enableSignedPush
expr_stmt|;
return|return
name|info
return|;
block|}
DECL|method|toBoolean (boolean v)
specifier|private
specifier|static
name|Boolean
name|toBoolean
parameter_list|(
name|boolean
name|v
parameter_list|)
block|{
return|return
name|v
condition|?
name|v
else|:
literal|null
return|;
block|}
block|}
end_class

end_unit

