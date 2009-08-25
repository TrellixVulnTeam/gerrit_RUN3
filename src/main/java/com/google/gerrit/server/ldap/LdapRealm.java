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
DECL|package|com.google.gerrit.server.ldap
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
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
name|AccountState
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
name|account
operator|.
name|GroupCache
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
name|gerrit
operator|.
name|server
operator|.
name|cache
operator|.
name|SelfPopulatingCache
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
name|gwtorm
operator|.
name|client
operator|.
name|OrmException
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
name|client
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
name|naming
operator|.
name|directory
operator|.
name|InitialDirContext
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|LdapRealm
class|class
name|LdapRealm
implements|implements
name|Realm
block|{
DECL|field|log
specifier|private
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
specifier|private
specifier|static
specifier|final
name|String
name|LDAP
init|=
literal|"com.sun.jndi.ldap.LdapCtxFactory"
decl_stmt|;
DECL|field|USERNAME
specifier|private
specifier|static
specifier|final
name|String
name|USERNAME
init|=
literal|"username"
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
DECL|field|schema
specifier|private
specifier|final
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|schema
decl_stmt|;
DECL|field|emailExpander
specifier|private
specifier|final
name|EmailExpander
name|emailExpander
decl_stmt|;
DECL|field|accountFullName
specifier|private
specifier|final
name|String
name|accountFullName
decl_stmt|;
DECL|field|accountEmailAddress
specifier|private
specifier|final
name|String
name|accountEmailAddress
decl_stmt|;
DECL|field|accountSshUserName
specifier|private
specifier|final
name|String
name|accountSshUserName
decl_stmt|;
DECL|field|accountQuery
specifier|private
specifier|final
name|LdapQuery
name|accountQuery
decl_stmt|;
DECL|field|usernameCache
specifier|private
specifier|final
name|SelfPopulatingCache
argument_list|<
name|String
argument_list|,
name|Account
operator|.
name|Id
argument_list|>
name|usernameCache
decl_stmt|;
DECL|field|groupCache
specifier|private
specifier|final
name|GroupCache
name|groupCache
decl_stmt|;
DECL|field|groupName
specifier|private
specifier|final
name|String
name|groupName
decl_stmt|;
DECL|field|groupNeedsAccount
specifier|private
name|boolean
name|groupNeedsAccount
decl_stmt|;
DECL|field|groupMemberQuery
specifier|private
specifier|final
name|LdapQuery
name|groupMemberQuery
decl_stmt|;
DECL|field|membershipCache
specifier|private
specifier|final
name|SelfPopulatingCache
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
argument_list|>
name|membershipCache
decl_stmt|;
annotation|@
name|Inject
DECL|method|LdapRealm ( final GroupCache groupCache, final EmailExpander emailExpander, final SchemaFactory<ReviewDb> schema, @Named(LdapModule.GROUP_CACHE) final Cache<String, Set<AccountGroup.Id>> rawGroup, @Named(LdapModule.USERNAME_CACHE) final Cache<String, Account.Id> rawUsername, @GerritServerConfig final Config config)
name|LdapRealm
parameter_list|(
specifier|final
name|GroupCache
name|groupCache
parameter_list|,
specifier|final
name|EmailExpander
name|emailExpander
parameter_list|,
specifier|final
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|schema
parameter_list|,
annotation|@
name|Named
argument_list|(
name|LdapModule
operator|.
name|GROUP_CACHE
argument_list|)
specifier|final
name|Cache
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
argument_list|>
name|rawGroup
parameter_list|,
annotation|@
name|Named
argument_list|(
name|LdapModule
operator|.
name|USERNAME_CACHE
argument_list|)
specifier|final
name|Cache
argument_list|<
name|String
argument_list|,
name|Account
operator|.
name|Id
argument_list|>
name|rawUsername
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
name|groupCache
operator|=
name|groupCache
expr_stmt|;
name|this
operator|.
name|emailExpander
operator|=
name|emailExpander
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|this
operator|.
name|server
operator|=
name|required
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
name|optional
argument_list|(
name|config
argument_list|,
literal|"password"
argument_list|)
expr_stmt|;
comment|// Group query
comment|//
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|groupAtts
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|groupName
operator|=
name|reqdef
argument_list|(
name|config
argument_list|,
literal|"groupName"
argument_list|,
literal|"cn"
argument_list|)
expr_stmt|;
name|groupAtts
operator|.
name|add
argument_list|(
name|groupName
argument_list|)
expr_stmt|;
specifier|final
name|String
name|groupBase
init|=
name|required
argument_list|(
name|config
argument_list|,
literal|"groupBase"
argument_list|)
decl_stmt|;
specifier|final
name|SearchScope
name|groupScope
init|=
name|scope
argument_list|(
name|config
argument_list|,
literal|"groupScope"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|groupMemberPattern
init|=
name|reqdef
argument_list|(
name|config
argument_list|,
literal|"groupMemberPattern"
argument_list|,
literal|"(memberUid=${username})"
argument_list|)
decl_stmt|;
name|groupMemberQuery
operator|=
operator|new
name|LdapQuery
argument_list|(
name|groupBase
argument_list|,
name|groupScope
argument_list|,
name|groupMemberPattern
argument_list|,
name|groupAtts
argument_list|)
expr_stmt|;
if|if
condition|(
name|groupMemberQuery
operator|.
name|getParameters
argument_list|()
operator|.
name|length
operator|==
literal|0
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
name|membershipCache
operator|=
operator|new
name|SelfPopulatingCache
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
argument_list|>
argument_list|(
name|rawGroup
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
name|createEntry
parameter_list|(
specifier|final
name|String
name|username
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|queryForGroups
argument_list|(
name|username
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Set
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
name|missing
parameter_list|(
specifier|final
name|String
name|key
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
block|}
expr_stmt|;
comment|// Account query
comment|//
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
name|accountFullName
operator|=
name|optdef
argument_list|(
name|config
argument_list|,
literal|"accountFullName"
argument_list|,
literal|"displayName"
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
name|add
argument_list|(
name|accountFullName
argument_list|)
expr_stmt|;
block|}
name|accountEmailAddress
operator|=
name|optdef
argument_list|(
name|config
argument_list|,
literal|"accountEmailAddress"
argument_list|,
literal|"mail"
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
name|add
argument_list|(
name|accountEmailAddress
argument_list|)
expr_stmt|;
block|}
name|accountSshUserName
operator|=
name|optdef
argument_list|(
name|config
argument_list|,
literal|"accountSshUserName"
argument_list|,
literal|"uid"
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
name|add
argument_list|(
name|accountSshUserName
argument_list|)
expr_stmt|;
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
specifier|final
name|String
name|accountBase
init|=
name|required
argument_list|(
name|config
argument_list|,
literal|"accountBase"
argument_list|)
decl_stmt|;
specifier|final
name|SearchScope
name|accountScope
init|=
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
name|reqdef
argument_list|(
name|config
argument_list|,
literal|"accountPattern"
argument_list|,
literal|"(uid=${username})"
argument_list|)
decl_stmt|;
name|accountQuery
operator|=
operator|new
name|LdapQuery
argument_list|(
name|accountBase
argument_list|,
name|accountScope
argument_list|,
name|accountPattern
argument_list|,
name|accountAtts
argument_list|)
expr_stmt|;
if|if
condition|(
name|accountQuery
operator|.
name|getParameters
argument_list|()
operator|.
name|length
operator|==
literal|0
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
name|usernameCache
operator|=
operator|new
name|SelfPopulatingCache
argument_list|<
name|String
argument_list|,
name|Account
operator|.
name|Id
argument_list|>
argument_list|(
name|rawUsername
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Account
operator|.
name|Id
name|createEntry
parameter_list|(
specifier|final
name|String
name|username
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|queryForUsername
argument_list|(
name|username
argument_list|)
return|;
block|}
block|}
expr_stmt|;
block|}
DECL|method|scope (final Config c, final String setting)
specifier|private
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
name|ConfigUtil
operator|.
name|getEnum
argument_list|(
name|c
argument_list|,
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
specifier|private
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
DECL|method|required (final Config config, final String name)
specifier|private
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
DECL|method|optdef (final Config c, final String n, final String d)
specifier|private
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
return|return
name|v
index|[
literal|0
index|]
return|;
block|}
block|}
DECL|method|reqdef (final Config c, final String n, final String d)
specifier|private
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
switch|switch
condition|(
name|field
condition|)
block|{
case|case
name|FULL_NAME
case|:
return|return
name|accountFullName
operator|==
literal|null
return|;
comment|// only if not obtained from LDAP
case|case
name|SSH_USER_NAME
case|:
return|return
name|accountSshUserName
operator|==
literal|null
return|;
comment|// only if not obtained from LDAP
default|default:
return|return
literal|true
return|;
block|}
block|}
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
init|=
name|open
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|LdapQuery
operator|.
name|Result
name|m
init|=
name|findAccount
argument_list|(
name|ctx
argument_list|,
name|username
argument_list|)
decl_stmt|;
name|who
operator|.
name|setDisplayName
argument_list|(
name|m
operator|.
name|get
argument_list|(
name|accountFullName
argument_list|)
argument_list|)
expr_stmt|;
name|who
operator|.
name|setSshUserName
argument_list|(
name|m
operator|.
name|get
argument_list|(
name|accountSshUserName
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|accountEmailAddress
operator|!=
literal|null
condition|)
block|{
name|who
operator|.
name|setEmailAddress
argument_list|(
name|m
operator|.
name|get
argument_list|(
name|accountEmailAddress
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
name|membershipCache
operator|.
name|put
argument_list|(
name|username
argument_list|,
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
throw|throw
operator|new
name|AccountException
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
name|account
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|groups (final AccountState who)
specifier|public
name|Set
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
name|groups
parameter_list|(
specifier|final
name|AccountState
name|who
parameter_list|)
block|{
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
argument_list|()
decl_stmt|;
name|r
operator|.
name|addAll
argument_list|(
name|membershipCache
operator|.
name|get
argument_list|(
name|findId
argument_list|(
name|who
operator|.
name|getExternalIds
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|r
operator|.
name|addAll
argument_list|(
name|who
operator|.
name|getInternalGroups
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|r
return|;
block|}
DECL|method|queryForGroups (final String username)
specifier|private
name|Set
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
name|queryForGroups
parameter_list|(
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
name|DirContext
name|ctx
init|=
name|open
argument_list|()
decl_stmt|;
try|try
block|{
return|return
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
DECL|method|queryForGroups (final DirContext ctx, final String username, LdapQuery.Result account)
specifier|private
name|Set
argument_list|<
name|AccountGroup
operator|.
name|Id
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
name|USERNAME
argument_list|,
name|username
argument_list|)
expr_stmt|;
if|if
condition|(
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
name|ctx
argument_list|,
name|username
argument_list|)
expr_stmt|;
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
specifier|final
name|Set
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
name|actual
init|=
operator|new
name|HashSet
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
argument_list|()
decl_stmt|;
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
specifier|final
name|String
name|name
init|=
name|r
operator|.
name|get
argument_list|(
name|groupName
argument_list|)
decl_stmt|;
specifier|final
name|AccountGroup
name|group
init|=
name|groupCache
operator|.
name|lookup
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|group
operator|!=
literal|null
operator|&&
name|isLdapGroup
argument_list|(
name|group
argument_list|)
condition|)
block|{
name|actual
operator|.
name|add
argument_list|(
name|group
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
DECL|method|isLdapGroup (final AccountGroup group)
specifier|private
name|boolean
name|isLdapGroup
parameter_list|(
specifier|final
name|AccountGroup
name|group
parameter_list|)
block|{
return|return
name|group
operator|.
name|isAutomaticMembership
argument_list|()
return|;
block|}
DECL|method|findId (final Collection<AccountExternalId> ids)
specifier|private
specifier|static
name|String
name|findId
parameter_list|(
specifier|final
name|Collection
argument_list|<
name|AccountExternalId
argument_list|>
name|ids
parameter_list|)
block|{
for|for
control|(
specifier|final
name|AccountExternalId
name|i
range|:
name|ids
control|)
block|{
if|if
condition|(
name|i
operator|.
name|isScheme
argument_list|(
name|AccountExternalId
operator|.
name|SCHEME_GERRIT
argument_list|)
condition|)
block|{
return|return
name|i
operator|.
name|getSchemeRest
argument_list|(
name|AccountExternalId
operator|.
name|SCHEME_GERRIT
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|lookup (final String accountName)
specifier|public
name|Account
operator|.
name|Id
name|lookup
parameter_list|(
specifier|final
name|String
name|accountName
parameter_list|)
block|{
return|return
name|usernameCache
operator|.
name|get
argument_list|(
name|accountName
argument_list|)
return|;
block|}
DECL|method|queryForUsername (final String username)
specifier|private
name|Account
operator|.
name|Id
name|queryForUsername
parameter_list|(
specifier|final
name|String
name|username
parameter_list|)
block|{
try|try
block|{
specifier|final
name|ReviewDb
name|db
init|=
name|schema
operator|.
name|open
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|List
argument_list|<
name|AccountExternalId
argument_list|>
name|candidates
init|=
name|db
operator|.
name|accountExternalIds
argument_list|()
operator|.
name|byExternal
argument_list|(
name|AccountExternalId
operator|.
name|SCHEME_GERRIT
operator|+
name|username
argument_list|)
operator|.
name|toList
argument_list|()
decl_stmt|;
if|if
condition|(
name|candidates
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|candidates
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getAccountId
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
finally|finally
block|{
name|db
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Cannot query for username in database"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
DECL|method|open ()
specifier|private
name|DirContext
name|open
parameter_list|()
throws|throws
name|NamingException
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
DECL|method|findAccount (final DirContext ctx, final String username)
specifier|private
name|LdapQuery
operator|.
name|Result
name|findAccount
parameter_list|(
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
name|accountQuery
operator|.
name|query
argument_list|(
name|ctx
argument_list|,
name|params
argument_list|)
decl_stmt|;
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
block|}
end_class

end_unit

