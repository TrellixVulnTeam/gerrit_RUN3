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
DECL|package|com.google.gerrit.server.auth.ldap
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|auth
operator|.
name|ldap
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
name|reviewdb
operator|.
name|client
operator|.
name|AccountExternalId
operator|.
name|SCHEME_GERRIT
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
name|cache
operator|.
name|CacheLoader
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
name|cache
operator|.
name|LoadingCache
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
name|ParameterizedString
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
name|reviewdb
operator|.
name|client
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
name|reviewdb
operator|.
name|client
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
name|reviewdb
operator|.
name|server
operator|.
name|ReviewDb
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
name|AbstractRealm
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
name|AccountException
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
name|AuthRequest
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
name|EmailExpander
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
name|auth
operator|.
name|AuthenticationUnavailableException
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
name|GerritServerConfig
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|server
operator|.
name|SchemaFactory
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|name
operator|.
name|Named
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
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|ExecutionException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|CompositeName
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|Name
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|NamingException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|directory
operator|.
name|DirContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|LoginException
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|LdapRealm
class|class
name|LdapRealm
extends|extends
name|AbstractRealm
block|{
DECL|field|log
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|LdapRealm
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|LDAP
specifier|static
specifier|final
name|String
name|LDAP
init|=
literal|"com.sun.jndi.ldap.LdapCtxFactory"
decl_stmt|;
DECL|field|USERNAME
specifier|static
specifier|final
name|String
name|USERNAME
init|=
literal|"username"
decl_stmt|;
DECL|field|helper
specifier|private
specifier|final
name|Helper
name|helper
decl_stmt|;
DECL|field|authConfig
specifier|private
specifier|final
name|AuthConfig
name|authConfig
decl_stmt|;
DECL|field|emailExpander
specifier|private
specifier|final
name|EmailExpander
name|emailExpander
decl_stmt|;
DECL|field|usernameCache
specifier|private
specifier|final
name|LoadingCache
argument_list|<
name|String
argument_list|,
name|Optional
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
argument_list|>
name|usernameCache
decl_stmt|;
DECL|field|readOnlyAccountFields
specifier|private
specifier|final
name|Set
argument_list|<
name|Account
operator|.
name|FieldName
argument_list|>
name|readOnlyAccountFields
decl_stmt|;
DECL|field|fetchMemberOfEagerly
specifier|private
specifier|final
name|boolean
name|fetchMemberOfEagerly
decl_stmt|;
DECL|field|config
specifier|private
specifier|final
name|Config
name|config
decl_stmt|;
DECL|field|membershipCache
specifier|private
specifier|final
name|LoadingCache
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
argument_list|>
name|membershipCache
decl_stmt|;
annotation|@
name|Inject
DECL|method|LdapRealm ( Helper helper, AuthConfig authConfig, EmailExpander emailExpander, @Named(LdapModule.GROUP_CACHE) final LoadingCache<String, Set<AccountGroup.UUID>> membershipCache, @Named(LdapModule.USERNAME_CACHE) final LoadingCache<String, Optional<Account.Id>> usernameCache, @GerritServerConfig final Config config)
name|LdapRealm
parameter_list|(
name|Helper
name|helper
parameter_list|,
name|AuthConfig
name|authConfig
parameter_list|,
name|EmailExpander
name|emailExpander
parameter_list|,
annotation|@
name|Named
argument_list|(
name|LdapModule
operator|.
name|GROUP_CACHE
argument_list|)
specifier|final
name|LoadingCache
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
argument_list|>
name|membershipCache
parameter_list|,
annotation|@
name|Named
argument_list|(
name|LdapModule
operator|.
name|USERNAME_CACHE
argument_list|)
specifier|final
name|LoadingCache
argument_list|<
name|String
argument_list|,
name|Optional
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
argument_list|>
name|usernameCache
parameter_list|,
annotation|@
name|GerritServerConfig
specifier|final
name|Config
name|config
parameter_list|)
block|{
name|this
operator|.
name|helper
operator|=
name|helper
expr_stmt|;
name|this
operator|.
name|authConfig
operator|=
name|authConfig
expr_stmt|;
name|this
operator|.
name|emailExpander
operator|=
name|emailExpander
expr_stmt|;
name|this
operator|.
name|usernameCache
operator|=
name|usernameCache
expr_stmt|;
name|this
operator|.
name|membershipCache
operator|=
name|membershipCache
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
name|this
operator|.
name|readOnlyAccountFields
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
if|if
condition|(
name|optdef
argument_list|(
name|config
argument_list|,
literal|"accountFullName"
argument_list|,
literal|"DEFAULT"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|readOnlyAccountFields
operator|.
name|add
argument_list|(
name|Account
operator|.
name|FieldName
operator|.
name|FULL_NAME
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|optdef
argument_list|(
name|config
argument_list|,
literal|"accountSshUserName"
argument_list|,
literal|"DEFAULT"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|readOnlyAccountFields
operator|.
name|add
argument_list|(
name|Account
operator|.
name|FieldName
operator|.
name|USER_NAME
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|authConfig
operator|.
name|isAllowRegisterNewEmail
argument_list|()
condition|)
block|{
name|readOnlyAccountFields
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
name|fetchMemberOfEagerly
operator|=
name|optional
argument_list|(
name|config
argument_list|,
literal|"fetchMemberOfEagerly"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|scope (final Config c, final String setting)
specifier|static
name|SearchScope
name|scope
parameter_list|(
specifier|final
name|Config
name|c
parameter_list|,
specifier|final
name|String
name|setting
parameter_list|)
block|{
return|return
name|c
operator|.
name|getEnum
argument_list|(
literal|"ldap"
argument_list|,
literal|null
argument_list|,
name|setting
argument_list|,
name|SearchScope
operator|.
name|SUBTREE
argument_list|)
return|;
block|}
DECL|method|optional (final Config config, final String name)
specifier|static
name|String
name|optional
parameter_list|(
specifier|final
name|Config
name|config
parameter_list|,
specifier|final
name|String
name|name
parameter_list|)
block|{
return|return
name|config
operator|.
name|getString
argument_list|(
literal|"ldap"
argument_list|,
literal|null
argument_list|,
name|name
argument_list|)
return|;
block|}
DECL|method|optional (Config config, String name, int defaultValue)
specifier|static
name|int
name|optional
parameter_list|(
name|Config
name|config
parameter_list|,
name|String
name|name
parameter_list|,
name|int
name|defaultValue
parameter_list|)
block|{
return|return
name|config
operator|.
name|getInt
argument_list|(
literal|"ldap"
argument_list|,
name|name
argument_list|,
name|defaultValue
argument_list|)
return|;
block|}
DECL|method|optional (Config config, String name, String defaultValue)
specifier|static
name|String
name|optional
parameter_list|(
name|Config
name|config
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|defaultValue
parameter_list|)
block|{
specifier|final
name|String
name|v
init|=
name|optional
argument_list|(
name|config
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|v
argument_list|)
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
return|return
name|v
return|;
block|}
DECL|method|optional (Config config, String name, boolean defaultValue)
specifier|static
name|boolean
name|optional
parameter_list|(
name|Config
name|config
parameter_list|,
name|String
name|name
parameter_list|,
name|boolean
name|defaultValue
parameter_list|)
block|{
return|return
name|config
operator|.
name|getBoolean
argument_list|(
literal|"ldap"
argument_list|,
name|name
argument_list|,
name|defaultValue
argument_list|)
return|;
block|}
DECL|method|required (final Config config, final String name)
specifier|static
name|String
name|required
parameter_list|(
specifier|final
name|Config
name|config
parameter_list|,
specifier|final
name|String
name|name
parameter_list|)
block|{
specifier|final
name|String
name|v
init|=
name|optional
argument_list|(
name|config
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
literal|""
operator|.
name|equals
argument_list|(
name|v
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No ldap."
operator|+
name|name
operator|+
literal|" configured"
argument_list|)
throw|;
block|}
return|return
name|v
return|;
block|}
DECL|method|optionalList (final Config config, final String name)
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|optionalList
parameter_list|(
specifier|final
name|Config
name|config
parameter_list|,
specifier|final
name|String
name|name
parameter_list|)
block|{
name|String
index|[]
name|s
init|=
name|config
operator|.
name|getStringList
argument_list|(
literal|"ldap"
argument_list|,
literal|null
argument_list|,
name|name
argument_list|)
decl_stmt|;
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|s
argument_list|)
return|;
block|}
DECL|method|requiredList (final Config config, final String name)
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|requiredList
parameter_list|(
specifier|final
name|Config
name|config
parameter_list|,
specifier|final
name|String
name|name
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|vlist
init|=
name|optionalList
argument_list|(
name|config
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|vlist
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No ldap "
operator|+
name|name
operator|+
literal|" configured"
argument_list|)
throw|;
block|}
return|return
name|vlist
return|;
block|}
DECL|method|optdef (final Config c, final String n, final String d)
specifier|static
name|String
name|optdef
parameter_list|(
specifier|final
name|Config
name|c
parameter_list|,
specifier|final
name|String
name|n
parameter_list|,
specifier|final
name|String
name|d
parameter_list|)
block|{
specifier|final
name|String
index|[]
name|v
init|=
name|c
operator|.
name|getStringList
argument_list|(
literal|"ldap"
argument_list|,
literal|null
argument_list|,
name|n
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
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|d
return|;
block|}
elseif|else
if|if
condition|(
name|v
index|[
literal|0
index|]
operator|==
literal|null
operator|||
literal|""
operator|.
name|equals
argument_list|(
name|v
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
else|else
block|{
name|checkBackendCompliance
argument_list|(
name|n
argument_list|,
name|v
index|[
literal|0
index|]
argument_list|,
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|d
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|v
index|[
literal|0
index|]
return|;
block|}
block|}
DECL|method|reqdef (final Config c, final String n, final String d)
specifier|static
name|String
name|reqdef
parameter_list|(
specifier|final
name|Config
name|c
parameter_list|,
specifier|final
name|String
name|n
parameter_list|,
specifier|final
name|String
name|d
parameter_list|)
block|{
specifier|final
name|String
name|v
init|=
name|optdef
argument_list|(
name|c
argument_list|,
name|n
argument_list|,
name|d
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No ldap."
operator|+
name|n
operator|+
literal|" configured"
argument_list|)
throw|;
block|}
return|return
name|v
return|;
block|}
DECL|method|paramString (Config c, String n, String d)
specifier|static
name|ParameterizedString
name|paramString
parameter_list|(
name|Config
name|c
parameter_list|,
name|String
name|n
parameter_list|,
name|String
name|d
parameter_list|)
block|{
name|String
name|expression
init|=
name|optdef
argument_list|(
name|c
argument_list|,
name|n
argument_list|,
name|d
argument_list|)
decl_stmt|;
if|if
condition|(
name|expression
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|expression
operator|.
name|contains
argument_list|(
literal|"${"
argument_list|)
condition|)
block|{
return|return
operator|new
name|ParameterizedString
argument_list|(
name|expression
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|ParameterizedString
argument_list|(
literal|"${"
operator|+
name|expression
operator|+
literal|"}"
argument_list|)
return|;
block|}
block|}
DECL|method|checkBackendCompliance (String configOption, String suppliedValue, boolean disabledByBackend)
specifier|private
specifier|static
name|void
name|checkBackendCompliance
parameter_list|(
name|String
name|configOption
parameter_list|,
name|String
name|suppliedValue
parameter_list|,
name|boolean
name|disabledByBackend
parameter_list|)
block|{
if|if
condition|(
name|disabledByBackend
operator|&&
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|suppliedValue
argument_list|)
condition|)
block|{
name|String
name|msg
init|=
name|String
operator|.
name|format
argument_list|(
literal|"LDAP backend doesn't support: ldap.%s"
argument_list|,
name|configOption
argument_list|)
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|allowsEdit (final Account.FieldName field)
specifier|public
name|boolean
name|allowsEdit
parameter_list|(
specifier|final
name|Account
operator|.
name|FieldName
name|field
parameter_list|)
block|{
return|return
operator|!
name|readOnlyAccountFields
operator|.
name|contains
argument_list|(
name|field
argument_list|)
return|;
block|}
DECL|method|apply (ParameterizedString p, LdapQuery.Result m)
specifier|static
name|String
name|apply
parameter_list|(
name|ParameterizedString
name|p
parameter_list|,
name|LdapQuery
operator|.
name|Result
name|m
parameter_list|)
throws|throws
name|NamingException
block|{
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|values
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|name
range|:
name|m
operator|.
name|attributes
argument_list|()
control|)
block|{
name|values
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|m
operator|.
name|get
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|r
init|=
name|p
operator|.
name|replace
argument_list|(
name|values
argument_list|)
decl_stmt|;
return|return
name|r
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
name|r
return|;
block|}
annotation|@
name|Override
DECL|method|authenticate (final AuthRequest who)
specifier|public
name|AuthRequest
name|authenticate
parameter_list|(
specifier|final
name|AuthRequest
name|who
parameter_list|)
throws|throws
name|AccountException
block|{
if|if
condition|(
name|config
operator|.
name|getBoolean
argument_list|(
literal|"ldap"
argument_list|,
literal|"localUsernameToLowerCase"
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|who
operator|.
name|setLocalUser
argument_list|(
name|who
operator|.
name|getLocalUser
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|username
init|=
name|who
operator|.
name|getLocalUser
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|DirContext
name|ctx
decl_stmt|;
if|if
condition|(
name|authConfig
operator|.
name|getAuthType
argument_list|()
operator|==
name|AuthType
operator|.
name|LDAP_BIND
condition|)
block|{
name|ctx
operator|=
name|helper
operator|.
name|authenticate
argument_list|(
name|username
argument_list|,
name|who
operator|.
name|getPassword
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ctx
operator|=
name|helper
operator|.
name|open
argument_list|()
expr_stmt|;
block|}
try|try
block|{
specifier|final
name|Helper
operator|.
name|LdapSchema
name|schema
init|=
name|helper
operator|.
name|getSchema
argument_list|(
name|ctx
argument_list|)
decl_stmt|;
specifier|final
name|LdapQuery
operator|.
name|Result
name|m
init|=
name|helper
operator|.
name|findAccount
argument_list|(
name|schema
argument_list|,
name|ctx
argument_list|,
name|username
argument_list|,
name|fetchMemberOfEagerly
argument_list|)
decl_stmt|;
if|if
condition|(
name|authConfig
operator|.
name|getAuthType
argument_list|()
operator|==
name|AuthType
operator|.
name|LDAP
operator|&&
operator|!
name|who
operator|.
name|isSkipAuthentication
argument_list|()
condition|)
block|{
comment|// We found the user account, but we need to verify
comment|// the password matches it before we can continue.
comment|//
name|helper
operator|.
name|authenticate
argument_list|(
name|m
operator|.
name|getDN
argument_list|()
argument_list|,
name|who
operator|.
name|getPassword
argument_list|()
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|who
operator|.
name|setDisplayName
argument_list|(
name|apply
argument_list|(
name|schema
operator|.
name|accountFullName
argument_list|,
name|m
argument_list|)
argument_list|)
expr_stmt|;
name|who
operator|.
name|setUserName
argument_list|(
name|apply
argument_list|(
name|schema
operator|.
name|accountSshUserName
argument_list|,
name|m
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|schema
operator|.
name|accountEmailAddress
operator|!=
literal|null
condition|)
block|{
name|who
operator|.
name|setEmailAddress
argument_list|(
name|apply
argument_list|(
name|schema
operator|.
name|accountEmailAddress
argument_list|,
name|m
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|emailExpander
operator|.
name|canExpand
argument_list|(
name|username
argument_list|)
condition|)
block|{
comment|// If LDAP cannot give us a valid email address for this user
comment|// try expanding it through the older email expander code which
comment|// assumes a user name within a domain.
comment|//
name|who
operator|.
name|setEmailAddress
argument_list|(
name|emailExpander
operator|.
name|expand
argument_list|(
name|username
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Fill the cache with the user's current groups. We've already
comment|// spent the cost to open the LDAP connection, we might as well
comment|// do one more call to get their group membership. Since we are
comment|// in the middle of authenticating the user, its likely we will
comment|// need to know what access rights they have soon.
comment|//
if|if
condition|(
name|fetchMemberOfEagerly
condition|)
block|{
name|membershipCache
operator|.
name|put
argument_list|(
name|username
argument_list|,
name|helper
operator|.
name|queryForGroups
argument_list|(
name|ctx
argument_list|,
name|username
argument_list|,
name|m
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|who
return|;
block|}
finally|finally
block|{
try|try
block|{
name|ctx
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NamingException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Cannot close LDAP query handle"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|NamingException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cannot query LDAP to authenticate user"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|AuthenticationUnavailableException
argument_list|(
literal|"Cannot query LDAP for account"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|LoginException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cannot authenticate server via JAAS"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|AuthenticationUnavailableException
argument_list|(
literal|"Cannot query LDAP for account"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|onCreateAccount (final AuthRequest who, final Account account)
specifier|public
name|void
name|onCreateAccount
parameter_list|(
specifier|final
name|AuthRequest
name|who
parameter_list|,
specifier|final
name|Account
name|account
parameter_list|)
block|{
name|usernameCache
operator|.
name|put
argument_list|(
name|who
operator|.
name|getLocalUser
argument_list|()
argument_list|,
name|Optional
operator|.
name|of
argument_list|(
name|account
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lookup (String accountName)
specifier|public
name|Account
operator|.
name|Id
name|lookup
parameter_list|(
name|String
name|accountName
parameter_list|)
block|{
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|accountName
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
try|try
block|{
name|Optional
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|id
init|=
name|usernameCache
operator|.
name|get
argument_list|(
name|accountName
argument_list|)
decl_stmt|;
return|return
name|id
operator|!=
literal|null
condition|?
name|id
operator|.
name|orNull
argument_list|()
else|:
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Cannot lookup account %s in LDAP"
argument_list|,
name|accountName
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
DECL|class|UserLoader
specifier|static
class|class
name|UserLoader
extends|extends
name|CacheLoader
argument_list|<
name|String
argument_list|,
name|Optional
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
argument_list|>
block|{
DECL|field|schema
specifier|private
specifier|final
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|schema
decl_stmt|;
annotation|@
name|Inject
DECL|method|UserLoader (SchemaFactory<ReviewDb> schema)
name|UserLoader
parameter_list|(
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|schema
parameter_list|)
block|{
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|load (String username)
specifier|public
name|Optional
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|load
parameter_list|(
name|String
name|username
parameter_list|)
throws|throws
name|Exception
block|{
try|try
init|(
name|ReviewDb
name|db
init|=
name|schema
operator|.
name|open
argument_list|()
init|)
block|{
specifier|final
name|AccountExternalId
name|extId
init|=
name|db
operator|.
name|accountExternalIds
argument_list|()
operator|.
name|get
argument_list|(
operator|new
name|AccountExternalId
operator|.
name|Key
argument_list|(
name|SCHEME_GERRIT
argument_list|,
name|username
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|extId
operator|!=
literal|null
condition|)
block|{
return|return
name|Optional
operator|.
name|of
argument_list|(
name|extId
operator|.
name|getAccountId
argument_list|()
argument_list|)
return|;
block|}
return|return
name|Optional
operator|.
name|absent
argument_list|()
return|;
block|}
block|}
block|}
DECL|class|MemberLoader
specifier|static
class|class
name|MemberLoader
extends|extends
name|CacheLoader
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
argument_list|>
block|{
DECL|field|helper
specifier|private
specifier|final
name|Helper
name|helper
decl_stmt|;
annotation|@
name|Inject
DECL|method|MemberLoader (final Helper helper)
name|MemberLoader
parameter_list|(
specifier|final
name|Helper
name|helper
parameter_list|)
block|{
name|this
operator|.
name|helper
operator|=
name|helper
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|load (String username)
specifier|public
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|load
parameter_list|(
name|String
name|username
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|DirContext
name|ctx
init|=
name|helper
operator|.
name|open
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|helper
operator|.
name|queryForGroups
argument_list|(
name|ctx
argument_list|,
name|username
argument_list|,
literal|null
argument_list|)
return|;
block|}
finally|finally
block|{
try|try
block|{
name|ctx
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NamingException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Cannot close LDAP query handle"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|class|ExistenceLoader
specifier|static
class|class
name|ExistenceLoader
extends|extends
name|CacheLoader
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
block|{
DECL|field|helper
specifier|private
specifier|final
name|Helper
name|helper
decl_stmt|;
annotation|@
name|Inject
DECL|method|ExistenceLoader (final Helper helper)
name|ExistenceLoader
parameter_list|(
specifier|final
name|Helper
name|helper
parameter_list|)
block|{
name|this
operator|.
name|helper
operator|=
name|helper
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|load (final String groupDn)
specifier|public
name|Boolean
name|load
parameter_list|(
specifier|final
name|String
name|groupDn
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|DirContext
name|ctx
init|=
name|helper
operator|.
name|open
argument_list|()
decl_stmt|;
try|try
block|{
name|Name
name|compositeGroupName
init|=
operator|new
name|CompositeName
argument_list|()
operator|.
name|add
argument_list|(
name|groupDn
argument_list|)
decl_stmt|;
try|try
block|{
name|ctx
operator|.
name|getAttributes
argument_list|(
name|compositeGroupName
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|NamingException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
finally|finally
block|{
try|try
block|{
name|ctx
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NamingException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Cannot close LDAP query handle"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

