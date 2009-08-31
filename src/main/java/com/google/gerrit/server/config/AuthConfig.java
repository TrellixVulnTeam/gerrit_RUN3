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
DECL|package|com.google.gerrit.server.config
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|config
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
name|reviewdb
operator|.
name|AccountExternalId
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
name|AccountGroup
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
name|AuthType
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
name|SystemConfig
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtjsonrpc
operator|.
name|server
operator|.
name|SignedToken
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtjsonrpc
operator|.
name|server
operator|.
name|XsrfException
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
name|Singleton
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
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
name|Collections
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

begin_comment
comment|/** Authentication related settings from {@code gerrit.config}. */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|AuthConfig
specifier|public
class|class
name|AuthConfig
block|{
DECL|field|loginType
specifier|private
specifier|final
name|AuthType
name|loginType
decl_stmt|;
DECL|field|httpHeader
specifier|private
specifier|final
name|String
name|httpHeader
decl_stmt|;
DECL|field|logoutUrl
specifier|private
specifier|final
name|String
name|logoutUrl
decl_stmt|;
DECL|field|trusted
specifier|private
specifier|final
name|String
index|[]
name|trusted
decl_stmt|;
DECL|field|emailReg
specifier|private
specifier|final
name|SignedToken
name|emailReg
decl_stmt|;
DECL|field|administratorGroup
specifier|private
specifier|final
name|AccountGroup
operator|.
name|Id
name|administratorGroup
decl_stmt|;
DECL|field|anonymousGroups
specifier|private
specifier|final
name|Set
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
name|anonymousGroups
decl_stmt|;
DECL|field|registeredGroups
specifier|private
specifier|final
name|Set
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
name|registeredGroups
decl_stmt|;
DECL|field|allowGoogleAccountUpgrade
specifier|private
specifier|final
name|boolean
name|allowGoogleAccountUpgrade
decl_stmt|;
annotation|@
name|Inject
DECL|method|AuthConfig (@erritServerConfig final Config cfg, final SystemConfig s)
name|AuthConfig
parameter_list|(
annotation|@
name|GerritServerConfig
specifier|final
name|Config
name|cfg
parameter_list|,
specifier|final
name|SystemConfig
name|s
parameter_list|)
throws|throws
name|XsrfException
block|{
name|loginType
operator|=
name|toType
argument_list|(
name|cfg
argument_list|)
expr_stmt|;
name|httpHeader
operator|=
name|cfg
operator|.
name|getString
argument_list|(
literal|"auth"
argument_list|,
literal|null
argument_list|,
literal|"httpheader"
argument_list|)
expr_stmt|;
name|logoutUrl
operator|=
name|cfg
operator|.
name|getString
argument_list|(
literal|"auth"
argument_list|,
literal|null
argument_list|,
literal|"logouturl"
argument_list|)
expr_stmt|;
name|trusted
operator|=
name|toTrusted
argument_list|(
name|cfg
argument_list|)
expr_stmt|;
name|emailReg
operator|=
operator|new
name|SignedToken
argument_list|(
literal|5
operator|*
literal|24
operator|*
literal|60
operator|*
literal|60
argument_list|,
name|s
operator|.
name|registerEmailPrivateKey
argument_list|)
expr_stmt|;
specifier|final
name|HashSet
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
name|r
init|=
operator|new
name|HashSet
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|r
operator|.
name|add
argument_list|(
name|s
operator|.
name|anonymousGroupId
argument_list|)
expr_stmt|;
name|r
operator|.
name|add
argument_list|(
name|s
operator|.
name|registeredGroupId
argument_list|)
expr_stmt|;
name|registeredGroups
operator|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|anonymousGroups
operator|=
name|Collections
operator|.
name|singleton
argument_list|(
name|s
operator|.
name|anonymousGroupId
argument_list|)
expr_stmt|;
name|administratorGroup
operator|=
name|s
operator|.
name|adminGroupId
expr_stmt|;
if|if
condition|(
name|loginType
operator|==
name|AuthType
operator|.
name|OPENID
condition|)
block|{
name|allowGoogleAccountUpgrade
operator|=
name|cfg
operator|.
name|getBoolean
argument_list|(
literal|"auth"
argument_list|,
literal|"allowgoogleaccountupgrade"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|allowGoogleAccountUpgrade
operator|=
literal|false
expr_stmt|;
block|}
block|}
DECL|method|toTrusted (final Config cfg)
specifier|private
name|String
index|[]
name|toTrusted
parameter_list|(
specifier|final
name|Config
name|cfg
parameter_list|)
block|{
specifier|final
name|String
index|[]
name|r
init|=
name|cfg
operator|.
name|getStringList
argument_list|(
literal|"auth"
argument_list|,
literal|null
argument_list|,
literal|"trustedopenid"
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
operator|new
name|String
index|[]
block|{
literal|"http://"
block|,
literal|"https://"
block|}
return|;
block|}
return|return
name|r
return|;
block|}
DECL|method|toType (final Config cfg)
specifier|private
specifier|static
name|AuthType
name|toType
parameter_list|(
specifier|final
name|Config
name|cfg
parameter_list|)
block|{
if|if
condition|(
name|isBecomeAnyoneEnabled
argument_list|()
condition|)
block|{
return|return
name|AuthType
operator|.
name|DEVELOPMENT_BECOME_ANY_ACCOUNT
return|;
block|}
return|return
name|ConfigUtil
operator|.
name|getEnum
argument_list|(
name|cfg
argument_list|,
literal|"auth"
argument_list|,
literal|null
argument_list|,
literal|"type"
argument_list|,
name|AuthType
operator|.
name|OPENID
argument_list|)
return|;
block|}
DECL|method|isBecomeAnyoneEnabled ()
specifier|private
specifier|static
name|boolean
name|isBecomeAnyoneEnabled
parameter_list|()
block|{
try|try
block|{
name|String
name|s
init|=
literal|"com.google.gerrit.server.http.BecomeAnyAccountLoginServlet"
decl_stmt|;
return|return
name|Boolean
operator|.
name|getBoolean
argument_list|(
name|s
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|SecurityException
name|se
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/** Type of user authentication used by this Gerrit server. */
DECL|method|getLoginType ()
specifier|public
name|AuthType
name|getLoginType
parameter_list|()
block|{
return|return
name|loginType
return|;
block|}
DECL|method|getLoginHttpHeader ()
specifier|public
name|String
name|getLoginHttpHeader
parameter_list|()
block|{
return|return
name|httpHeader
return|;
block|}
DECL|method|getLogoutURL ()
specifier|public
name|String
name|getLogoutURL
parameter_list|()
block|{
return|return
name|logoutUrl
return|;
block|}
DECL|method|getEmailRegistrationToken ()
specifier|public
name|SignedToken
name|getEmailRegistrationToken
parameter_list|()
block|{
return|return
name|emailReg
return|;
block|}
DECL|method|isAllowGoogleAccountUpgrade ()
specifier|public
name|boolean
name|isAllowGoogleAccountUpgrade
parameter_list|()
block|{
return|return
name|allowGoogleAccountUpgrade
return|;
block|}
comment|/** Identity of the magic group with full powers. */
DECL|method|getAdministratorsGroup ()
specifier|public
name|AccountGroup
operator|.
name|Id
name|getAdministratorsGroup
parameter_list|()
block|{
return|return
name|administratorGroup
return|;
block|}
comment|/** Groups that all users, including anonymous users, belong to. */
DECL|method|getAnonymousGroups ()
specifier|public
name|Set
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
name|getAnonymousGroups
parameter_list|()
block|{
return|return
name|anonymousGroups
return|;
block|}
comment|/** Groups that all users who have created an account belong to. */
DECL|method|getRegisteredGroups ()
specifier|public
name|Set
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
name|getRegisteredGroups
parameter_list|()
block|{
return|return
name|registeredGroups
return|;
block|}
DECL|method|isIdentityTrustable (final Collection<AccountExternalId> ids)
specifier|public
name|boolean
name|isIdentityTrustable
parameter_list|(
specifier|final
name|Collection
argument_list|<
name|AccountExternalId
argument_list|>
name|ids
parameter_list|)
block|{
switch|switch
condition|(
name|getLoginType
argument_list|()
condition|)
block|{
case|case
name|DEVELOPMENT_BECOME_ANY_ACCOUNT
case|:
case|case
name|HTTP
case|:
case|case
name|HTTP_LDAP
case|:
comment|// Its safe to assume yes for an HTTP authentication type, as the
comment|// only way in is through some external system that the admin trusts
comment|//
return|return
literal|true
return|;
case|case
name|OPENID
case|:
comment|// All identities must be trusted in order to trust the account.
comment|//
for|for
control|(
specifier|final
name|AccountExternalId
name|e
range|:
name|ids
control|)
block|{
if|if
condition|(
operator|!
name|isTrusted
argument_list|(
name|e
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
default|default:
comment|// Assume not, we don't understand the login format.
comment|//
return|return
literal|false
return|;
block|}
block|}
DECL|method|isTrusted (final AccountExternalId id)
specifier|private
name|boolean
name|isTrusted
parameter_list|(
specifier|final
name|AccountExternalId
name|id
parameter_list|)
block|{
if|if
condition|(
name|id
operator|.
name|isScheme
argument_list|(
name|AccountExternalId
operator|.
name|LEGACY_GAE
argument_list|)
condition|)
block|{
comment|// Assume this is a trusted token, its a legacy import from
comment|// a fairly well respected provider and only takes effect if
comment|// the administrator has the import still enabled
comment|//
return|return
name|isAllowGoogleAccountUpgrade
argument_list|()
return|;
block|}
if|if
condition|(
name|id
operator|.
name|isScheme
argument_list|(
name|AccountExternalId
operator|.
name|SCHEME_MAILTO
argument_list|)
condition|)
block|{
comment|// mailto identities are created by sending a unique validation
comment|// token to the address and asking them to come back to the site
comment|// with that token.
comment|//
return|return
literal|true
return|;
block|}
for|for
control|(
specifier|final
name|String
name|p
range|:
name|trusted
control|)
block|{
if|if
condition|(
name|matches
argument_list|(
name|p
argument_list|,
name|id
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|matches (final String p, final AccountExternalId id)
specifier|private
name|boolean
name|matches
parameter_list|(
specifier|final
name|String
name|p
parameter_list|,
specifier|final
name|AccountExternalId
name|id
parameter_list|)
block|{
if|if
condition|(
name|p
operator|.
name|startsWith
argument_list|(
literal|"^"
argument_list|)
operator|&&
name|p
operator|.
name|endsWith
argument_list|(
literal|"$"
argument_list|)
condition|)
block|{
return|return
name|id
operator|.
name|getExternalId
argument_list|()
operator|.
name|matches
argument_list|(
name|p
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|id
operator|.
name|getExternalId
argument_list|()
operator|.
name|startsWith
argument_list|(
name|p
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

