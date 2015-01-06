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
DECL|package|com.google.gerrit.httpd
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|httpd
package|;
end_package

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
name|Function
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
name|Optional
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
name|Iterables
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
name|GerritConfig
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
name|GitwebConfig
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
name|change
operator|.
name|GetArchive
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
name|DownloadConfig
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
name|contact
operator|.
name|ContactStore
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
name|mail
operator|.
name|EmailSender
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
name|ssh
operator|.
name|SshInfo
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Provider
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
name|ProvisionException
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

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
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
name|javax
operator|.
name|servlet
operator|.
name|ServletContext
import|;
end_import

begin_class
DECL|class|GerritConfigProvider
class|class
name|GerritConfigProvider
implements|implements
name|Provider
argument_list|<
name|GerritConfig
argument_list|>
block|{
DECL|field|realm
specifier|private
specifier|final
name|Realm
name|realm
decl_stmt|;
DECL|field|cfg
specifier|private
specifier|final
name|Config
name|cfg
decl_stmt|;
DECL|field|authConfig
specifier|private
specifier|final
name|AuthConfig
name|authConfig
decl_stmt|;
DECL|field|downloadConfig
specifier|private
specifier|final
name|DownloadConfig
name|downloadConfig
decl_stmt|;
DECL|field|archiveFormats
specifier|private
specifier|final
name|GetArchive
operator|.
name|AllowedFormats
name|archiveFormats
decl_stmt|;
DECL|field|gitWebConfig
specifier|private
specifier|final
name|GitWebConfig
name|gitWebConfig
decl_stmt|;
DECL|field|wildProject
specifier|private
specifier|final
name|AllProjectsName
name|wildProject
decl_stmt|;
DECL|field|sshInfo
specifier|private
specifier|final
name|SshInfo
name|sshInfo
decl_stmt|;
DECL|field|emailSender
specifier|private
name|EmailSender
name|emailSender
decl_stmt|;
DECL|field|contactStore
specifier|private
specifier|final
name|ContactStore
name|contactStore
decl_stmt|;
DECL|field|servletContext
specifier|private
specifier|final
name|ServletContext
name|servletContext
decl_stmt|;
DECL|field|anonymousCowardName
specifier|private
specifier|final
name|String
name|anonymousCowardName
decl_stmt|;
annotation|@
name|Inject
DECL|method|GerritConfigProvider (final Realm r, @GerritServerConfig final Config gsc, final AuthConfig ac, final GitWebConfig gwc, final AllProjectsName wp, final SshInfo si, final ContactStore cs, final ServletContext sc, final DownloadConfig dc, final GetArchive.AllowedFormats af, final @AnonymousCowardName String acn)
name|GerritConfigProvider
parameter_list|(
specifier|final
name|Realm
name|r
parameter_list|,
annotation|@
name|GerritServerConfig
specifier|final
name|Config
name|gsc
parameter_list|,
specifier|final
name|AuthConfig
name|ac
parameter_list|,
specifier|final
name|GitWebConfig
name|gwc
parameter_list|,
specifier|final
name|AllProjectsName
name|wp
parameter_list|,
specifier|final
name|SshInfo
name|si
parameter_list|,
specifier|final
name|ContactStore
name|cs
parameter_list|,
specifier|final
name|ServletContext
name|sc
parameter_list|,
specifier|final
name|DownloadConfig
name|dc
parameter_list|,
specifier|final
name|GetArchive
operator|.
name|AllowedFormats
name|af
parameter_list|,
specifier|final
annotation|@
name|AnonymousCowardName
name|String
name|acn
parameter_list|)
block|{
name|realm
operator|=
name|r
expr_stmt|;
name|cfg
operator|=
name|gsc
expr_stmt|;
name|authConfig
operator|=
name|ac
expr_stmt|;
name|downloadConfig
operator|=
name|dc
expr_stmt|;
name|archiveFormats
operator|=
name|af
expr_stmt|;
name|gitWebConfig
operator|=
name|gwc
expr_stmt|;
name|sshInfo
operator|=
name|si
expr_stmt|;
name|wildProject
operator|=
name|wp
expr_stmt|;
name|contactStore
operator|=
name|cs
expr_stmt|;
name|servletContext
operator|=
name|sc
expr_stmt|;
name|anonymousCowardName
operator|=
name|acn
expr_stmt|;
block|}
annotation|@
name|Inject
argument_list|(
name|optional
operator|=
literal|true
argument_list|)
DECL|method|setEmailSender (final EmailSender d)
name|void
name|setEmailSender
parameter_list|(
specifier|final
name|EmailSender
name|d
parameter_list|)
block|{
name|emailSender
operator|=
name|d
expr_stmt|;
block|}
DECL|method|create ()
specifier|private
name|GerritConfig
name|create
parameter_list|()
throws|throws
name|MalformedURLException
block|{
specifier|final
name|GerritConfig
name|config
init|=
operator|new
name|GerritConfig
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|authConfig
operator|.
name|getAuthType
argument_list|()
condition|)
block|{
case|case
name|LDAP
case|:
case|case
name|LDAP_BIND
case|:
name|config
operator|.
name|setRegisterUrl
argument_list|(
name|cfg
operator|.
name|getString
argument_list|(
literal|"auth"
argument_list|,
literal|null
argument_list|,
literal|"registerurl"
argument_list|)
argument_list|)
expr_stmt|;
name|config
operator|.
name|setRegisterText
argument_list|(
name|cfg
operator|.
name|getString
argument_list|(
literal|"auth"
argument_list|,
literal|null
argument_list|,
literal|"registertext"
argument_list|)
argument_list|)
expr_stmt|;
name|config
operator|.
name|setEditFullNameUrl
argument_list|(
name|cfg
operator|.
name|getString
argument_list|(
literal|"auth"
argument_list|,
literal|null
argument_list|,
literal|"editFullNameUrl"
argument_list|)
argument_list|)
expr_stmt|;
name|config
operator|.
name|setHttpPasswordSettingsEnabled
argument_list|(
operator|!
name|authConfig
operator|.
name|isGitBasicAuth
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|CUSTOM_EXTENSION
case|:
name|config
operator|.
name|setRegisterUrl
argument_list|(
name|cfg
operator|.
name|getString
argument_list|(
literal|"auth"
argument_list|,
literal|null
argument_list|,
literal|"registerurl"
argument_list|)
argument_list|)
expr_stmt|;
name|config
operator|.
name|setRegisterText
argument_list|(
name|cfg
operator|.
name|getString
argument_list|(
literal|"auth"
argument_list|,
literal|null
argument_list|,
literal|"registertext"
argument_list|)
argument_list|)
expr_stmt|;
name|config
operator|.
name|setEditFullNameUrl
argument_list|(
name|cfg
operator|.
name|getString
argument_list|(
literal|"auth"
argument_list|,
literal|null
argument_list|,
literal|"editFullNameUrl"
argument_list|)
argument_list|)
expr_stmt|;
name|config
operator|.
name|setHttpPasswordUrl
argument_list|(
name|cfg
operator|.
name|getString
argument_list|(
literal|"auth"
argument_list|,
literal|null
argument_list|,
literal|"httpPasswordUrl"
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|HTTP
case|:
case|case
name|HTTP_LDAP
case|:
name|config
operator|.
name|setLoginUrl
argument_list|(
name|cfg
operator|.
name|getString
argument_list|(
literal|"auth"
argument_list|,
literal|null
argument_list|,
literal|"loginurl"
argument_list|)
argument_list|)
expr_stmt|;
name|config
operator|.
name|setLoginText
argument_list|(
name|cfg
operator|.
name|getString
argument_list|(
literal|"auth"
argument_list|,
literal|null
argument_list|,
literal|"logintext"
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|CLIENT_SSL_CERT_LDAP
case|:
case|case
name|DEVELOPMENT_BECOME_ANY_ACCOUNT
case|:
case|case
name|OPENID
case|:
case|case
name|OPENID_SSO
case|:
break|break;
block|}
name|config
operator|.
name|setSwitchAccountUrl
argument_list|(
name|cfg
operator|.
name|getString
argument_list|(
literal|"auth"
argument_list|,
literal|null
argument_list|,
literal|"switchAccountUrl"
argument_list|)
argument_list|)
expr_stmt|;
name|config
operator|.
name|setUseContributorAgreements
argument_list|(
name|cfg
operator|.
name|getBoolean
argument_list|(
literal|"auth"
argument_list|,
literal|"contributoragreements"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|config
operator|.
name|setGitDaemonUrl
argument_list|(
name|cfg
operator|.
name|getString
argument_list|(
literal|"gerrit"
argument_list|,
literal|null
argument_list|,
literal|"canonicalgiturl"
argument_list|)
argument_list|)
expr_stmt|;
name|config
operator|.
name|setGitHttpUrl
argument_list|(
name|cfg
operator|.
name|getString
argument_list|(
literal|"gerrit"
argument_list|,
literal|null
argument_list|,
literal|"gitHttpUrl"
argument_list|)
argument_list|)
expr_stmt|;
name|config
operator|.
name|setUseContactInfo
argument_list|(
name|contactStore
operator|!=
literal|null
operator|&&
name|contactStore
operator|.
name|isEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|config
operator|.
name|setDownloadSchemes
argument_list|(
name|downloadConfig
operator|.
name|getDownloadSchemes
argument_list|()
argument_list|)
expr_stmt|;
name|config
operator|.
name|setDownloadCommands
argument_list|(
name|downloadConfig
operator|.
name|getDownloadCommands
argument_list|()
argument_list|)
expr_stmt|;
name|config
operator|.
name|setAuthType
argument_list|(
name|authConfig
operator|.
name|getAuthType
argument_list|()
argument_list|)
expr_stmt|;
name|config
operator|.
name|setWildProject
argument_list|(
name|wildProject
argument_list|)
expr_stmt|;
name|config
operator|.
name|setDocumentationAvailable
argument_list|(
name|servletContext
operator|.
name|getResource
argument_list|(
literal|"/Documentation/index.html"
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|config
operator|.
name|setAnonymousCowardName
argument_list|(
name|anonymousCowardName
argument_list|)
expr_stmt|;
name|config
operator|.
name|setSuggestFrom
argument_list|(
name|cfg
operator|.
name|getInt
argument_list|(
literal|"suggest"
argument_list|,
literal|"from"
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|config
operator|.
name|setChangeUpdateDelay
argument_list|(
operator|(
name|int
operator|)
name|ConfigUtil
operator|.
name|getTimeUnit
argument_list|(
name|cfg
argument_list|,
literal|"change"
argument_list|,
literal|null
argument_list|,
literal|"updateDelay"
argument_list|,
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|config
operator|.
name|setChangeScreen
argument_list|(
name|cfg
operator|.
name|getEnum
argument_list|(
literal|"gerrit"
argument_list|,
literal|null
argument_list|,
literal|"changeScreen"
argument_list|,
name|AccountGeneralPreferences
operator|.
name|ChangeScreen
operator|.
name|CHANGE_SCREEN2
argument_list|)
argument_list|)
expr_stmt|;
name|config
operator|.
name|setLargeChangeSize
argument_list|(
name|cfg
operator|.
name|getInt
argument_list|(
literal|"change"
argument_list|,
literal|"largeChange"
argument_list|,
literal|500
argument_list|)
argument_list|)
expr_stmt|;
name|config
operator|.
name|setArchiveFormats
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|Iterables
operator|.
name|transform
argument_list|(
name|archiveFormats
operator|.
name|getAllowed
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|ArchiveFormat
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
name|ArchiveFormat
name|in
parameter_list|)
block|{
return|return
name|in
operator|.
name|getShortName
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|config
operator|.
name|setReportBugUrl
argument_list|(
name|cfg
operator|.
name|getString
argument_list|(
literal|"gerrit"
argument_list|,
literal|null
argument_list|,
literal|"reportBugUrl"
argument_list|)
argument_list|)
expr_stmt|;
name|config
operator|.
name|setReportBugText
argument_list|(
name|cfg
operator|.
name|getString
argument_list|(
literal|"gerrit"
argument_list|,
literal|null
argument_list|,
literal|"reportBugText"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Set
argument_list|<
name|Account
operator|.
name|FieldName
argument_list|>
name|fields
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Account
operator|.
name|FieldName
name|n
range|:
name|Account
operator|.
name|FieldName
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|realm
operator|.
name|allowsEdit
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|fields
operator|.
name|add
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|emailSender
operator|!=
literal|null
operator|&&
name|emailSender
operator|.
name|isEnabled
argument_list|()
operator|&&
name|realm
operator|.
name|allowsEdit
argument_list|(
name|Account
operator|.
name|FieldName
operator|.
name|REGISTER_NEW_EMAIL
argument_list|)
condition|)
block|{
name|fields
operator|.
name|add
argument_list|(
name|Account
operator|.
name|FieldName
operator|.
name|REGISTER_NEW_EMAIL
argument_list|)
expr_stmt|;
block|}
name|config
operator|.
name|setEditableAccountFields
argument_list|(
name|fields
argument_list|)
expr_stmt|;
if|if
condition|(
name|gitWebConfig
operator|.
name|getUrl
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|config
operator|.
name|setGitwebLink
argument_list|(
operator|new
name|GitwebConfig
argument_list|(
name|gitWebConfig
operator|.
name|getUrl
argument_list|()
argument_list|,
name|gitWebConfig
operator|.
name|getGitWebType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sshInfo
operator|!=
literal|null
operator|&&
operator|!
name|sshInfo
operator|.
name|getHostKeys
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|config
operator|.
name|setSshdAddress
argument_list|(
name|sshInfo
operator|.
name|getHostKeys
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|replyTitle
init|=
name|Optional
operator|.
name|fromNullable
argument_list|(
name|cfg
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
name|or
argument_list|(
literal|"Reply and score"
argument_list|)
operator|+
literal|" (Shortcut: a)"
decl_stmt|;
name|String
name|replyLabel
init|=
name|Optional
operator|.
name|fromNullable
argument_list|(
name|cfg
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
name|or
argument_list|(
literal|"Reply"
argument_list|)
operator|+
literal|"\u2026"
decl_stmt|;
name|config
operator|.
name|setReplyTitle
argument_list|(
name|replyTitle
argument_list|)
expr_stmt|;
name|config
operator|.
name|setReplyLabel
argument_list|(
name|replyLabel
argument_list|)
expr_stmt|;
return|return
name|config
return|;
block|}
annotation|@
name|Override
DECL|method|get ()
specifier|public
name|GerritConfig
name|get
parameter_list|()
block|{
try|try
block|{
return|return
name|create
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ProvisionException
argument_list|(
literal|"Cannot create GerritConfig instance"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

