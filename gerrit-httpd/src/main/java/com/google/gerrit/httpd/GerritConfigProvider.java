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
name|gerrit
operator|.
name|common
operator|.
name|data
operator|.
name|ApprovalTypes
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
name|GitwebLink
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
name|Project
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
name|DownloadSchemeConfig
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
name|WildProjectName
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
name|gwtexpui
operator|.
name|safehtml
operator|.
name|client
operator|.
name|RegexFindReplace
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
name|ArrayList
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
name|List
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
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|PatternSyntaxException
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
DECL|field|schemeConfig
specifier|private
specifier|final
name|DownloadSchemeConfig
name|schemeConfig
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
name|Project
operator|.
name|NameKey
name|wildProject
decl_stmt|;
DECL|field|sshInfo
specifier|private
specifier|final
name|SshInfo
name|sshInfo
decl_stmt|;
DECL|field|approvalTypes
specifier|private
specifier|final
name|ApprovalTypes
name|approvalTypes
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
annotation|@
name|Inject
DECL|method|GerritConfigProvider (final Realm r, @GerritServerConfig final Config gsc, final AuthConfig ac, final GitWebConfig gwc, @WildProjectName final Project.NameKey wp, final SshInfo si, final ApprovalTypes at, final ContactStore cs, final ServletContext sc, final DownloadSchemeConfig dc)
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
annotation|@
name|WildProjectName
specifier|final
name|Project
operator|.
name|NameKey
name|wp
parameter_list|,
specifier|final
name|SshInfo
name|si
parameter_list|,
specifier|final
name|ApprovalTypes
name|at
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
name|DownloadSchemeConfig
name|dc
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
name|schemeConfig
operator|=
name|dc
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
name|approvalTypes
operator|=
name|at
expr_stmt|;
name|contactStore
operator|=
name|cs
expr_stmt|;
name|servletContext
operator|=
name|sc
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
name|OPENID
case|:
name|config
operator|.
name|setAllowedOpenIDs
argument_list|(
name|authConfig
operator|.
name|getAllowedOpenIDs
argument_list|()
argument_list|)
expr_stmt|;
break|break;
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
break|break;
block|}
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
name|schemeConfig
operator|.
name|getDownloadScheme
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
name|setApprovalTypes
argument_list|(
name|approvalTypes
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
argument_list|<
name|Account
operator|.
name|FieldName
argument_list|>
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
name|GitwebLink
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
name|List
argument_list|<
name|RegexFindReplace
argument_list|>
name|links
init|=
operator|new
name|ArrayList
argument_list|<
name|RegexFindReplace
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|cfg
operator|.
name|getSubsections
argument_list|(
literal|"commentlink"
argument_list|)
control|)
block|{
name|String
name|match
init|=
name|cfg
operator|.
name|getString
argument_list|(
literal|"commentlink"
argument_list|,
name|name
argument_list|,
literal|"match"
argument_list|)
decl_stmt|;
comment|// Unfortunately this validation isn't entirely complete. Clients
comment|// can have exceptions trying to evaluate the pattern if they don't
comment|// support a token used, even if the server does support the token.
comment|//
comment|// At the minimum, we can trap problems related to unmatched groups.
try|try
block|{
name|Pattern
operator|.
name|compile
argument_list|(
name|match
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PatternSyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ProvisionException
argument_list|(
literal|"Invalid pattern \""
operator|+
name|match
operator|+
literal|"\" in commentlink."
operator|+
name|name
operator|+
literal|".match: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
name|String
name|link
init|=
name|cfg
operator|.
name|getString
argument_list|(
literal|"commentlink"
argument_list|,
name|name
argument_list|,
literal|"link"
argument_list|)
decl_stmt|;
name|String
name|html
init|=
name|cfg
operator|.
name|getString
argument_list|(
literal|"commentlink"
argument_list|,
name|name
argument_list|,
literal|"html"
argument_list|)
decl_stmt|;
if|if
condition|(
name|html
operator|==
literal|null
operator|||
name|html
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|html
operator|=
literal|"<a href=\""
operator|+
name|link
operator|+
literal|"\">$&</a>"
expr_stmt|;
block|}
name|links
operator|.
name|add
argument_list|(
operator|new
name|RegexFindReplace
argument_list|(
name|match
argument_list|,
name|html
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|config
operator|.
name|setCommentLinks
argument_list|(
name|links
argument_list|)
expr_stmt|;
name|config
operator|.
name|setBackgroundColor
argument_list|(
name|getThemeColor
argument_list|(
literal|"backgroundColor"
argument_list|,
literal|"#FFFFFF"
argument_list|)
argument_list|)
expr_stmt|;
name|config
operator|.
name|setTextColor
argument_list|(
name|getThemeColor
argument_list|(
literal|"textColor"
argument_list|,
literal|"#000000"
argument_list|)
argument_list|)
expr_stmt|;
name|config
operator|.
name|setTrimColor
argument_list|(
name|getThemeColor
argument_list|(
literal|"trimColor"
argument_list|,
literal|"#D4E9A9"
argument_list|)
argument_list|)
expr_stmt|;
name|config
operator|.
name|setSelectionColor
argument_list|(
name|getThemeColor
argument_list|(
literal|"selectionColor"
argument_list|,
literal|"#FFFFCC"
argument_list|)
argument_list|)
expr_stmt|;
name|config
operator|.
name|setTopMenuColor
argument_list|(
name|getThemeColor
argument_list|(
literal|"topMenuColor"
argument_list|,
name|config
operator|.
name|getTrimColor
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|config
return|;
block|}
DECL|method|getThemeColor (String name, String defaultValue)
specifier|private
name|String
name|getThemeColor
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|defaultValue
parameter_list|)
block|{
name|String
name|v
init|=
name|cfg
operator|.
name|getString
argument_list|(
literal|"theme"
argument_list|,
literal|null
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
operator|||
name|v
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|v
operator|=
name|defaultValue
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|v
operator|.
name|startsWith
argument_list|(
literal|"#"
argument_list|)
operator|&&
name|v
operator|.
name|matches
argument_list|(
literal|"^[0-9a-fA-F]{2,6}$"
argument_list|)
condition|)
block|{
name|v
operator|=
literal|"#"
operator|+
name|v
expr_stmt|;
block|}
return|return
name|v
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

