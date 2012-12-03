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
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Throwables
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
name|Cache
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
name|ImmutableSet
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
name|util
operator|.
name|ssl
operator|.
name|BlindSSLSocketFactory
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
name|java
operator|.
name|security
operator|.
name|PrivilegedActionException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedExceptionAction
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
name|Collections
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
name|Properties
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
name|Context
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
name|NamingEnumeration
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
name|PartialResultException
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
name|Attribute
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
name|naming
operator|.
name|directory
operator|.
name|InitialDirContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|SSLSocketFactory
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
name|Subject
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
name|LoginContext
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
DECL|class|Helper
annotation|@
name|Singleton
class|class
name|Helper
block|{
DECL|field|LDAP_UUID
specifier|static
specifier|final
name|String
name|LDAP_UUID
init|=
literal|"ldap:"
decl_stmt|;
DECL|field|groupsByInclude
specifier|private
specifier|final
name|Cache
argument_list|<
name|String
argument_list|,
name|ImmutableSet
argument_list|<
name|String
argument_list|>
argument_list|>
name|groupsByInclude
decl_stmt|;
DECL|field|config
specifier|private
specifier|final
name|Config
name|config
decl_stmt|;
DECL|field|server
specifier|private
specifier|final
name|String
name|server
decl_stmt|;
DECL|field|username
specifier|private
specifier|final
name|String
name|username
decl_stmt|;
DECL|field|password
specifier|private
specifier|final
name|String
name|password
decl_stmt|;
DECL|field|referral
specifier|private
specifier|final
name|String
name|referral
decl_stmt|;
DECL|field|sslVerify
specifier|private
specifier|final
name|boolean
name|sslVerify
decl_stmt|;
DECL|field|authentication
specifier|private
specifier|final
name|String
name|authentication
decl_stmt|;
DECL|field|ldapSchema
specifier|private
specifier|volatile
name|LdapSchema
name|ldapSchema
decl_stmt|;
DECL|field|readTimeOutMillis
specifier|private
specifier|final
name|String
name|readTimeOutMillis
decl_stmt|;
annotation|@
name|Inject
DECL|method|Helper (@erritServerConfig final Config config, @Named(LdapModule.GROUPS_BYINCLUDE_CACHE) Cache<String, ImmutableSet<String>> groupsByInclude)
name|Helper
parameter_list|(
annotation|@
name|GerritServerConfig
specifier|final
name|Config
name|config
parameter_list|,
annotation|@
name|Named
argument_list|(
name|LdapModule
operator|.
name|GROUPS_BYINCLUDE_CACHE
argument_list|)
name|Cache
argument_list|<
name|String
argument_list|,
name|ImmutableSet
argument_list|<
name|String
argument_list|>
argument_list|>
name|groupsByInclude
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
name|server
operator|=
name|LdapRealm
operator|.
name|optional
argument_list|(
name|config
argument_list|,
literal|"server"
argument_list|)
expr_stmt|;
name|this
operator|.
name|username
operator|=
name|LdapRealm
operator|.
name|optional
argument_list|(
name|config
argument_list|,
literal|"username"
argument_list|)
expr_stmt|;
name|this
operator|.
name|password
operator|=
name|LdapRealm
operator|.
name|optional
argument_list|(
name|config
argument_list|,
literal|"password"
argument_list|)
expr_stmt|;
name|this
operator|.
name|referral
operator|=
name|LdapRealm
operator|.
name|optional
argument_list|(
name|config
argument_list|,
literal|"referral"
argument_list|)
expr_stmt|;
name|this
operator|.
name|sslVerify
operator|=
name|config
operator|.
name|getBoolean
argument_list|(
literal|"ldap"
argument_list|,
literal|"sslverify"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|authentication
operator|=
name|LdapRealm
operator|.
name|optional
argument_list|(
name|config
argument_list|,
literal|"authentication"
argument_list|)
expr_stmt|;
name|String
name|timeout
init|=
name|LdapRealm
operator|.
name|optional
argument_list|(
name|config
argument_list|,
literal|"readTimeout"
argument_list|)
decl_stmt|;
if|if
condition|(
name|timeout
operator|!=
literal|null
condition|)
block|{
name|readTimeOutMillis
operator|=
name|Long
operator|.
name|toString
argument_list|(
name|ConfigUtil
operator|.
name|getTimeUnit
argument_list|(
name|timeout
argument_list|,
literal|0
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|readTimeOutMillis
operator|=
literal|null
expr_stmt|;
block|}
name|this
operator|.
name|groupsByInclude
operator|=
name|groupsByInclude
expr_stmt|;
block|}
DECL|method|createContextProperties ()
specifier|private
name|Properties
name|createContextProperties
parameter_list|()
block|{
specifier|final
name|Properties
name|env
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|env
operator|.
name|put
argument_list|(
name|Context
operator|.
name|INITIAL_CONTEXT_FACTORY
argument_list|,
name|LdapRealm
operator|.
name|LDAP
argument_list|)
expr_stmt|;
name|env
operator|.
name|put
argument_list|(
name|Context
operator|.
name|PROVIDER_URL
argument_list|,
name|server
argument_list|)
expr_stmt|;
if|if
condition|(
name|server
operator|.
name|startsWith
argument_list|(
literal|"ldaps:"
argument_list|)
operator|&&
operator|!
name|sslVerify
condition|)
block|{
name|Class
argument_list|<
name|?
extends|extends
name|SSLSocketFactory
argument_list|>
name|factory
init|=
name|BlindSSLSocketFactory
operator|.
name|class
decl_stmt|;
name|env
operator|.
name|put
argument_list|(
literal|"java.naming.ldap.factory.socket"
argument_list|,
name|factory
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|readTimeOutMillis
operator|!=
literal|null
condition|)
block|{
name|env
operator|.
name|put
argument_list|(
literal|"com.sun.jndi.ldap.read.timeout"
argument_list|,
name|readTimeOutMillis
argument_list|)
expr_stmt|;
block|}
return|return
name|env
return|;
block|}
DECL|method|open ()
name|DirContext
name|open
parameter_list|()
throws|throws
name|NamingException
throws|,
name|LoginException
block|{
specifier|final
name|Properties
name|env
init|=
name|createContextProperties
argument_list|()
decl_stmt|;
name|env
operator|.
name|put
argument_list|(
name|Context
operator|.
name|SECURITY_AUTHENTICATION
argument_list|,
name|authentication
operator|!=
literal|null
condition|?
name|authentication
else|:
literal|"simple"
argument_list|)
expr_stmt|;
name|env
operator|.
name|put
argument_list|(
name|Context
operator|.
name|REFERRAL
argument_list|,
name|referral
operator|!=
literal|null
condition|?
name|referral
else|:
literal|"ignore"
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"GSSAPI"
operator|.
name|equals
argument_list|(
name|authentication
argument_list|)
condition|)
block|{
return|return
name|kerberosOpen
argument_list|(
name|env
argument_list|)
return|;
block|}
else|else
block|{
if|if
condition|(
name|username
operator|!=
literal|null
condition|)
block|{
name|env
operator|.
name|put
argument_list|(
name|Context
operator|.
name|SECURITY_PRINCIPAL
argument_list|,
name|username
argument_list|)
expr_stmt|;
name|env
operator|.
name|put
argument_list|(
name|Context
operator|.
name|SECURITY_CREDENTIALS
argument_list|,
name|password
operator|!=
literal|null
condition|?
name|password
else|:
literal|""
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|InitialDirContext
argument_list|(
name|env
argument_list|)
return|;
block|}
block|}
DECL|method|kerberosOpen (final Properties env)
specifier|private
name|DirContext
name|kerberosOpen
parameter_list|(
specifier|final
name|Properties
name|env
parameter_list|)
throws|throws
name|LoginException
throws|,
name|NamingException
block|{
name|LoginContext
name|ctx
init|=
operator|new
name|LoginContext
argument_list|(
literal|"KerberosLogin"
argument_list|)
decl_stmt|;
name|ctx
operator|.
name|login
argument_list|()
expr_stmt|;
name|Subject
name|subject
init|=
name|ctx
operator|.
name|getSubject
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|Subject
operator|.
name|doAs
argument_list|(
name|subject
argument_list|,
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|DirContext
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DirContext
name|run
parameter_list|()
throws|throws
name|NamingException
block|{
return|return
operator|new
name|InitialDirContext
argument_list|(
name|env
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|PrivilegedActionException
name|e
parameter_list|)
block|{
name|Throwables
operator|.
name|propagateIfPossible
argument_list|(
name|e
operator|.
name|getException
argument_list|()
argument_list|,
name|NamingException
operator|.
name|class
argument_list|)
expr_stmt|;
name|Throwables
operator|.
name|propagateIfPossible
argument_list|(
name|e
operator|.
name|getException
argument_list|()
argument_list|,
name|RuntimeException
operator|.
name|class
argument_list|)
expr_stmt|;
name|LdapRealm
operator|.
name|log
operator|.
name|warn
argument_list|(
literal|"Internal error"
argument_list|,
name|e
operator|.
name|getException
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
finally|finally
block|{
name|ctx
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|authenticate (String dn, String password)
name|DirContext
name|authenticate
parameter_list|(
name|String
name|dn
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|AccountException
block|{
specifier|final
name|Properties
name|env
init|=
name|createContextProperties
argument_list|()
decl_stmt|;
name|env
operator|.
name|put
argument_list|(
name|Context
operator|.
name|SECURITY_AUTHENTICATION
argument_list|,
literal|"simple"
argument_list|)
expr_stmt|;
name|env
operator|.
name|put
argument_list|(
name|Context
operator|.
name|SECURITY_PRINCIPAL
argument_list|,
name|dn
argument_list|)
expr_stmt|;
name|env
operator|.
name|put
argument_list|(
name|Context
operator|.
name|SECURITY_CREDENTIALS
argument_list|,
name|password
operator|!=
literal|null
condition|?
name|password
else|:
literal|""
argument_list|)
expr_stmt|;
name|env
operator|.
name|put
argument_list|(
name|Context
operator|.
name|REFERRAL
argument_list|,
name|referral
operator|!=
literal|null
condition|?
name|referral
else|:
literal|"ignore"
argument_list|)
expr_stmt|;
try|try
block|{
return|return
operator|new
name|InitialDirContext
argument_list|(
name|env
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NamingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AccountException
argument_list|(
literal|"Incorrect username or password"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getSchema (DirContext ctx)
name|LdapSchema
name|getSchema
parameter_list|(
name|DirContext
name|ctx
parameter_list|)
block|{
if|if
condition|(
name|ldapSchema
operator|==
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|ldapSchema
operator|==
literal|null
condition|)
block|{
name|ldapSchema
operator|=
operator|new
name|LdapSchema
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|ldapSchema
return|;
block|}
DECL|method|findAccount (final Helper.LdapSchema schema, final DirContext ctx, final String username)
name|LdapQuery
operator|.
name|Result
name|findAccount
parameter_list|(
specifier|final
name|Helper
operator|.
name|LdapSchema
name|schema
parameter_list|,
specifier|final
name|DirContext
name|ctx
parameter_list|,
specifier|final
name|String
name|username
parameter_list|)
throws|throws
name|NamingException
throws|,
name|AccountException
block|{
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
name|LdapRealm
operator|.
name|USERNAME
argument_list|,
name|username
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|LdapQuery
operator|.
name|Result
argument_list|>
name|res
init|=
operator|new
name|ArrayList
argument_list|<
name|LdapQuery
operator|.
name|Result
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|LdapQuery
name|accountQuery
range|:
name|schema
operator|.
name|accountQueryList
control|)
block|{
name|res
operator|.
name|addAll
argument_list|(
name|accountQuery
operator|.
name|query
argument_list|(
name|ctx
argument_list|,
name|params
argument_list|)
argument_list|)
expr_stmt|;
block|}
switch|switch
condition|(
name|res
operator|.
name|size
argument_list|()
condition|)
block|{
case|case
literal|0
case|:
throw|throw
operator|new
name|AccountException
argument_list|(
literal|"No such user:"
operator|+
name|username
argument_list|)
throw|;
case|case
literal|1
case|:
return|return
name|res
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|AccountException
argument_list|(
literal|"Duplicate users: "
operator|+
name|username
argument_list|)
throw|;
block|}
block|}
DECL|method|queryForGroups (final DirContext ctx, final String username, LdapQuery.Result account)
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|queryForGroups
parameter_list|(
specifier|final
name|DirContext
name|ctx
parameter_list|,
specifier|final
name|String
name|username
parameter_list|,
name|LdapQuery
operator|.
name|Result
name|account
parameter_list|)
throws|throws
name|NamingException
throws|,
name|AccountException
block|{
specifier|final
name|LdapSchema
name|schema
init|=
name|getSchema
argument_list|(
name|ctx
argument_list|)
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|groupDNs
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|schema
operator|.
name|groupMemberQueryList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|schema
operator|.
name|groupNeedsAccount
condition|)
block|{
if|if
condition|(
name|account
operator|==
literal|null
condition|)
block|{
name|account
operator|=
name|findAccount
argument_list|(
name|schema
argument_list|,
name|ctx
argument_list|,
name|username
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|name
range|:
name|schema
operator|.
name|groupMemberQueryList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getParameters
argument_list|()
control|)
block|{
name|params
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|account
operator|.
name|get
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|params
operator|.
name|put
argument_list|(
name|LdapRealm
operator|.
name|USERNAME
argument_list|,
name|username
argument_list|)
expr_stmt|;
for|for
control|(
name|LdapQuery
name|groupMemberQuery
range|:
name|schema
operator|.
name|groupMemberQueryList
control|)
block|{
for|for
control|(
name|LdapQuery
operator|.
name|Result
name|r
range|:
name|groupMemberQuery
operator|.
name|query
argument_list|(
name|ctx
argument_list|,
name|params
argument_list|)
control|)
block|{
name|recursivelyExpandGroups
argument_list|(
name|groupDNs
argument_list|,
name|schema
argument_list|,
name|ctx
argument_list|,
name|r
operator|.
name|getDN
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|schema
operator|.
name|accountMemberField
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|account
operator|==
literal|null
condition|)
block|{
name|account
operator|=
name|findAccount
argument_list|(
name|schema
argument_list|,
name|ctx
argument_list|,
name|username
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Attribute
name|groupAtt
init|=
name|account
operator|.
name|getAll
argument_list|(
name|schema
operator|.
name|accountMemberField
argument_list|)
decl_stmt|;
if|if
condition|(
name|groupAtt
operator|!=
literal|null
condition|)
block|{
specifier|final
name|NamingEnumeration
argument_list|<
name|?
argument_list|>
name|groups
init|=
name|groupAtt
operator|.
name|getAll
argument_list|()
decl_stmt|;
try|try
block|{
while|while
condition|(
name|groups
operator|.
name|hasMore
argument_list|()
condition|)
block|{
specifier|final
name|String
name|nextDN
init|=
operator|(
name|String
operator|)
name|groups
operator|.
name|next
argument_list|()
decl_stmt|;
name|recursivelyExpandGroups
argument_list|(
name|groupDNs
argument_list|,
name|schema
argument_list|,
name|ctx
argument_list|,
name|nextDN
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|PartialResultException
name|e
parameter_list|)
block|{         }
block|}
block|}
specifier|final
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|actual
init|=
operator|new
name|HashSet
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|dn
range|:
name|groupDNs
control|)
block|{
name|actual
operator|.
name|add
argument_list|(
operator|new
name|AccountGroup
operator|.
name|UUID
argument_list|(
name|LDAP_UUID
operator|+
name|dn
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|actual
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|actual
argument_list|)
return|;
block|}
block|}
DECL|method|recursivelyExpandGroups (final Set<String> groupDNs, final LdapSchema schema, final DirContext ctx, final String groupDN)
specifier|private
name|void
name|recursivelyExpandGroups
parameter_list|(
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|groupDNs
parameter_list|,
specifier|final
name|LdapSchema
name|schema
parameter_list|,
specifier|final
name|DirContext
name|ctx
parameter_list|,
specifier|final
name|String
name|groupDN
parameter_list|)
block|{
if|if
condition|(
name|groupDNs
operator|.
name|add
argument_list|(
name|groupDN
argument_list|)
operator|&&
name|schema
operator|.
name|accountMemberField
operator|!=
literal|null
condition|)
block|{
name|ImmutableSet
argument_list|<
name|String
argument_list|>
name|cachedGroupDNs
init|=
name|groupsByInclude
operator|.
name|getIfPresent
argument_list|(
name|groupDN
argument_list|)
decl_stmt|;
if|if
condition|(
name|cachedGroupDNs
operator|==
literal|null
condition|)
block|{
comment|// Recursively identify the groups it is a member of.
name|ImmutableSet
operator|.
name|Builder
argument_list|<
name|String
argument_list|>
name|dns
init|=
name|ImmutableSet
operator|.
name|builder
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|Name
name|compositeGroupName
init|=
operator|new
name|CompositeName
argument_list|()
operator|.
name|add
argument_list|(
name|groupDN
argument_list|)
decl_stmt|;
specifier|final
name|Attribute
name|in
init|=
name|ctx
operator|.
name|getAttributes
argument_list|(
name|compositeGroupName
argument_list|)
operator|.
name|get
argument_list|(
name|schema
operator|.
name|accountMemberField
argument_list|)
decl_stmt|;
if|if
condition|(
name|in
operator|!=
literal|null
condition|)
block|{
specifier|final
name|NamingEnumeration
argument_list|<
name|?
argument_list|>
name|groups
init|=
name|in
operator|.
name|getAll
argument_list|()
decl_stmt|;
try|try
block|{
while|while
condition|(
name|groups
operator|.
name|hasMore
argument_list|()
condition|)
block|{
name|dns
operator|.
name|add
argument_list|(
operator|(
name|String
operator|)
name|groups
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|PartialResultException
name|e
parameter_list|)
block|{             }
block|}
block|}
catch|catch
parameter_list|(
name|NamingException
name|e
parameter_list|)
block|{
name|LdapRealm
operator|.
name|log
operator|.
name|warn
argument_list|(
literal|"Could not find group "
operator|+
name|groupDN
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|cachedGroupDNs
operator|=
name|dns
operator|.
name|build
argument_list|()
expr_stmt|;
name|groupsByInclude
operator|.
name|put
argument_list|(
name|groupDN
argument_list|,
name|cachedGroupDNs
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|dn
range|:
name|cachedGroupDNs
control|)
block|{
name|recursivelyExpandGroups
argument_list|(
name|groupDNs
argument_list|,
name|schema
argument_list|,
name|ctx
argument_list|,
name|dn
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|LdapSchema
class|class
name|LdapSchema
block|{
DECL|field|type
specifier|final
name|LdapType
name|type
decl_stmt|;
DECL|field|accountFullName
specifier|final
name|ParameterizedString
name|accountFullName
decl_stmt|;
DECL|field|accountEmailAddress
specifier|final
name|ParameterizedString
name|accountEmailAddress
decl_stmt|;
DECL|field|accountSshUserName
specifier|final
name|ParameterizedString
name|accountSshUserName
decl_stmt|;
DECL|field|accountMemberField
specifier|final
name|String
name|accountMemberField
decl_stmt|;
DECL|field|accountQueryList
specifier|final
name|List
argument_list|<
name|LdapQuery
argument_list|>
name|accountQueryList
decl_stmt|;
DECL|field|groupNeedsAccount
name|boolean
name|groupNeedsAccount
decl_stmt|;
DECL|field|groupBases
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|groupBases
decl_stmt|;
DECL|field|groupScope
specifier|final
name|SearchScope
name|groupScope
decl_stmt|;
DECL|field|groupPattern
specifier|final
name|ParameterizedString
name|groupPattern
decl_stmt|;
DECL|field|groupName
specifier|final
name|ParameterizedString
name|groupName
decl_stmt|;
DECL|field|groupMemberQueryList
specifier|final
name|List
argument_list|<
name|LdapQuery
argument_list|>
name|groupMemberQueryList
decl_stmt|;
DECL|method|LdapSchema (final DirContext ctx)
name|LdapSchema
parameter_list|(
specifier|final
name|DirContext
name|ctx
parameter_list|)
block|{
name|type
operator|=
name|discoverLdapType
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|groupMemberQueryList
operator|=
operator|new
name|ArrayList
argument_list|<
name|LdapQuery
argument_list|>
argument_list|()
expr_stmt|;
name|accountQueryList
operator|=
operator|new
name|ArrayList
argument_list|<
name|LdapQuery
argument_list|>
argument_list|()
expr_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|accountAtts
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|// Group query
comment|//
name|groupBases
operator|=
name|LdapRealm
operator|.
name|optionalList
argument_list|(
name|config
argument_list|,
literal|"groupBase"
argument_list|)
expr_stmt|;
name|groupScope
operator|=
name|LdapRealm
operator|.
name|scope
argument_list|(
name|config
argument_list|,
literal|"groupScope"
argument_list|)
expr_stmt|;
name|groupPattern
operator|=
name|LdapRealm
operator|.
name|paramString
argument_list|(
name|config
argument_list|,
literal|"groupPattern"
argument_list|,
name|type
operator|.
name|groupPattern
argument_list|()
argument_list|)
expr_stmt|;
name|groupName
operator|=
name|LdapRealm
operator|.
name|paramString
argument_list|(
name|config
argument_list|,
literal|"groupName"
argument_list|,
name|type
operator|.
name|groupName
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|groupMemberPattern
init|=
name|LdapRealm
operator|.
name|optdef
argument_list|(
name|config
argument_list|,
literal|"groupMemberPattern"
argument_list|,
name|type
operator|.
name|groupMemberPattern
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|groupBase
range|:
name|groupBases
control|)
block|{
if|if
condition|(
name|groupMemberPattern
operator|!=
literal|null
condition|)
block|{
specifier|final
name|LdapQuery
name|groupMemberQuery
init|=
operator|new
name|LdapQuery
argument_list|(
name|groupBase
argument_list|,
name|groupScope
argument_list|,
operator|new
name|ParameterizedString
argument_list|(
name|groupMemberPattern
argument_list|)
argument_list|,
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|groupMemberQuery
operator|.
name|getParameters
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No variables in ldap.groupMemberPattern"
argument_list|)
throw|;
block|}
for|for
control|(
specifier|final
name|String
name|name
range|:
name|groupMemberQuery
operator|.
name|getParameters
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|LdapRealm
operator|.
name|USERNAME
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|groupNeedsAccount
operator|=
literal|true
expr_stmt|;
name|accountAtts
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
name|groupMemberQueryList
operator|.
name|add
argument_list|(
name|groupMemberQuery
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Account query
comment|//
name|accountFullName
operator|=
name|LdapRealm
operator|.
name|paramString
argument_list|(
name|config
argument_list|,
literal|"accountFullName"
argument_list|,
name|type
operator|.
name|accountFullName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|accountFullName
operator|!=
literal|null
condition|)
block|{
name|accountAtts
operator|.
name|addAll
argument_list|(
name|accountFullName
operator|.
name|getParameterNames
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|accountEmailAddress
operator|=
name|LdapRealm
operator|.
name|paramString
argument_list|(
name|config
argument_list|,
literal|"accountEmailAddress"
argument_list|,
name|type
operator|.
name|accountEmailAddress
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|accountEmailAddress
operator|!=
literal|null
condition|)
block|{
name|accountAtts
operator|.
name|addAll
argument_list|(
name|accountEmailAddress
operator|.
name|getParameterNames
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|accountSshUserName
operator|=
name|LdapRealm
operator|.
name|paramString
argument_list|(
name|config
argument_list|,
literal|"accountSshUserName"
argument_list|,
name|type
operator|.
name|accountSshUserName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|accountSshUserName
operator|!=
literal|null
condition|)
block|{
name|accountAtts
operator|.
name|addAll
argument_list|(
name|accountSshUserName
operator|.
name|getParameterNames
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|accountMemberField
operator|=
name|LdapRealm
operator|.
name|optdef
argument_list|(
name|config
argument_list|,
literal|"accountMemberField"
argument_list|,
name|type
operator|.
name|accountMemberField
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|accountMemberField
operator|!=
literal|null
condition|)
block|{
name|accountAtts
operator|.
name|add
argument_list|(
name|accountMemberField
argument_list|)
expr_stmt|;
block|}
specifier|final
name|SearchScope
name|accountScope
init|=
name|LdapRealm
operator|.
name|scope
argument_list|(
name|config
argument_list|,
literal|"accountScope"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|accountPattern
init|=
name|LdapRealm
operator|.
name|reqdef
argument_list|(
name|config
argument_list|,
literal|"accountPattern"
argument_list|,
name|type
operator|.
name|accountPattern
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|accountBase
range|:
name|LdapRealm
operator|.
name|requiredList
argument_list|(
name|config
argument_list|,
literal|"accountBase"
argument_list|)
control|)
block|{
specifier|final
name|LdapQuery
name|accountQuery
init|=
operator|new
name|LdapQuery
argument_list|(
name|accountBase
argument_list|,
name|accountScope
argument_list|,
operator|new
name|ParameterizedString
argument_list|(
name|accountPattern
argument_list|)
argument_list|,
name|accountAtts
argument_list|)
decl_stmt|;
if|if
condition|(
name|accountQuery
operator|.
name|getParameters
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No variables in ldap.accountPattern"
argument_list|)
throw|;
block|}
name|accountQueryList
operator|.
name|add
argument_list|(
name|accountQuery
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|discoverLdapType (DirContext ctx)
name|LdapType
name|discoverLdapType
parameter_list|(
name|DirContext
name|ctx
parameter_list|)
block|{
try|try
block|{
return|return
name|LdapType
operator|.
name|guessType
argument_list|(
name|ctx
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NamingException
name|e
parameter_list|)
block|{
name|LdapRealm
operator|.
name|log
operator|.
name|warn
argument_list|(
literal|"Cannot discover type of LDAP server at "
operator|+
name|server
operator|+
literal|", assuming the server is RFC 2307 compliant."
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|LdapType
operator|.
name|RFC_2307
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

