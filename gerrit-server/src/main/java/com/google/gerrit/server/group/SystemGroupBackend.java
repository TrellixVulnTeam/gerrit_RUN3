begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2013 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.group
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|group
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

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
name|toSet
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
name|annotations
operator|.
name|VisibleForTesting
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
name|MoreObjects
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
name|ImmutableMap
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
name|GroupDescription
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
name|GroupReference
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
name|IdentifiedUser
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
name|StartupCheck
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
name|StartupException
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
name|AbstractGroupBackend
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
name|GroupMembership
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
name|ListGroupMembership
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
name|project
operator|.
name|ProjectState
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
name|SortedMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
annotation|@
name|Singleton
DECL|class|SystemGroupBackend
specifier|public
class|class
name|SystemGroupBackend
extends|extends
name|AbstractGroupBackend
block|{
DECL|field|SYSTEM_GROUP_SCHEME
specifier|public
specifier|static
specifier|final
name|String
name|SYSTEM_GROUP_SCHEME
init|=
literal|"global:"
decl_stmt|;
comment|/** Common UUID assigned to the "Anonymous Users" group. */
DECL|field|ANONYMOUS_USERS
specifier|public
specifier|static
specifier|final
name|AccountGroup
operator|.
name|UUID
name|ANONYMOUS_USERS
init|=
operator|new
name|AccountGroup
operator|.
name|UUID
argument_list|(
name|SYSTEM_GROUP_SCHEME
operator|+
literal|"Anonymous-Users"
argument_list|)
decl_stmt|;
comment|/** Common UUID assigned to the "Registered Users" group. */
DECL|field|REGISTERED_USERS
specifier|public
specifier|static
specifier|final
name|AccountGroup
operator|.
name|UUID
name|REGISTERED_USERS
init|=
operator|new
name|AccountGroup
operator|.
name|UUID
argument_list|(
name|SYSTEM_GROUP_SCHEME
operator|+
literal|"Registered-Users"
argument_list|)
decl_stmt|;
comment|/** Common UUID assigned to the "Project Owners" placeholder group. */
DECL|field|PROJECT_OWNERS
specifier|public
specifier|static
specifier|final
name|AccountGroup
operator|.
name|UUID
name|PROJECT_OWNERS
init|=
operator|new
name|AccountGroup
operator|.
name|UUID
argument_list|(
name|SYSTEM_GROUP_SCHEME
operator|+
literal|"Project-Owners"
argument_list|)
decl_stmt|;
comment|/** Common UUID assigned to the "Change Owner" placeholder group. */
DECL|field|CHANGE_OWNER
specifier|public
specifier|static
specifier|final
name|AccountGroup
operator|.
name|UUID
name|CHANGE_OWNER
init|=
operator|new
name|AccountGroup
operator|.
name|UUID
argument_list|(
name|SYSTEM_GROUP_SCHEME
operator|+
literal|"Change-Owner"
argument_list|)
decl_stmt|;
DECL|field|all
specifier|private
specifier|static
specifier|final
name|AccountGroup
operator|.
name|UUID
index|[]
name|all
init|=
block|{
name|ANONYMOUS_USERS
block|,
name|REGISTERED_USERS
block|,
name|PROJECT_OWNERS
block|,
name|CHANGE_OWNER
block|,   }
decl_stmt|;
DECL|method|isSystemGroup (AccountGroup.UUID uuid)
specifier|public
specifier|static
name|boolean
name|isSystemGroup
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|uuid
parameter_list|)
block|{
return|return
name|uuid
operator|.
name|get
argument_list|()
operator|.
name|startsWith
argument_list|(
name|SYSTEM_GROUP_SCHEME
argument_list|)
return|;
block|}
DECL|method|isAnonymousOrRegistered (GroupReference ref)
specifier|public
specifier|static
name|boolean
name|isAnonymousOrRegistered
parameter_list|(
name|GroupReference
name|ref
parameter_list|)
block|{
return|return
name|isAnonymousOrRegistered
argument_list|(
name|ref
operator|.
name|getUUID
argument_list|()
argument_list|)
return|;
block|}
DECL|method|isAnonymousOrRegistered (AccountGroup.UUID uuid)
specifier|public
specifier|static
name|boolean
name|isAnonymousOrRegistered
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|uuid
parameter_list|)
block|{
return|return
name|ANONYMOUS_USERS
operator|.
name|equals
argument_list|(
name|uuid
argument_list|)
operator|||
name|REGISTERED_USERS
operator|.
name|equals
argument_list|(
name|uuid
argument_list|)
return|;
block|}
DECL|field|reservedNames
specifier|private
specifier|final
name|ImmutableSet
argument_list|<
name|String
argument_list|>
name|reservedNames
decl_stmt|;
DECL|field|namesToGroups
specifier|private
specifier|final
name|SortedMap
argument_list|<
name|String
argument_list|,
name|GroupReference
argument_list|>
name|namesToGroups
decl_stmt|;
DECL|field|names
specifier|private
specifier|final
name|ImmutableSet
argument_list|<
name|String
argument_list|>
name|names
decl_stmt|;
DECL|field|uuids
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|,
name|GroupReference
argument_list|>
name|uuids
decl_stmt|;
annotation|@
name|Inject
annotation|@
name|VisibleForTesting
DECL|method|SystemGroupBackend (@erritServerConfig Config cfg)
specifier|public
name|SystemGroupBackend
parameter_list|(
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|)
block|{
name|SortedMap
argument_list|<
name|String
argument_list|,
name|GroupReference
argument_list|>
name|n
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|,
name|GroupReference
argument_list|>
name|u
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
name|ImmutableSet
operator|.
name|Builder
argument_list|<
name|String
argument_list|>
name|reservedNamesBuilder
init|=
name|ImmutableSet
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|AccountGroup
operator|.
name|UUID
name|uuid
range|:
name|all
control|)
block|{
name|int
name|c
init|=
name|uuid
operator|.
name|get
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
name|String
name|defaultName
init|=
name|uuid
operator|.
name|get
argument_list|()
operator|.
name|substring
argument_list|(
name|c
operator|+
literal|1
argument_list|)
operator|.
name|replace
argument_list|(
literal|'-'
argument_list|,
literal|' '
argument_list|)
decl_stmt|;
name|reservedNamesBuilder
operator|.
name|add
argument_list|(
name|defaultName
argument_list|)
expr_stmt|;
name|String
name|configuredName
init|=
name|cfg
operator|.
name|getString
argument_list|(
literal|"groups"
argument_list|,
name|uuid
operator|.
name|get
argument_list|()
argument_list|,
literal|"name"
argument_list|)
decl_stmt|;
name|GroupReference
name|ref
init|=
operator|new
name|GroupReference
argument_list|(
name|uuid
argument_list|,
name|MoreObjects
operator|.
name|firstNonNull
argument_list|(
name|configuredName
argument_list|,
name|defaultName
argument_list|)
argument_list|)
decl_stmt|;
name|n
operator|.
name|put
argument_list|(
name|ref
operator|.
name|getName
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
argument_list|,
name|ref
argument_list|)
expr_stmt|;
name|u
operator|.
name|put
argument_list|(
name|ref
operator|.
name|getUUID
argument_list|()
argument_list|,
name|ref
argument_list|)
expr_stmt|;
block|}
name|reservedNames
operator|=
name|reservedNamesBuilder
operator|.
name|build
argument_list|()
expr_stmt|;
name|namesToGroups
operator|=
name|Collections
operator|.
name|unmodifiableSortedMap
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|names
operator|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|namesToGroups
operator|.
name|values
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|r
lambda|->
name|r
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|collect
argument_list|(
name|toSet
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|uuids
operator|=
name|u
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
DECL|method|getGroup (AccountGroup.UUID uuid)
specifier|public
name|GroupReference
name|getGroup
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|uuid
parameter_list|)
block|{
return|return
name|checkNotNull
argument_list|(
name|uuids
operator|.
name|get
argument_list|(
name|uuid
argument_list|)
argument_list|,
literal|"group %s not found"
argument_list|,
name|uuid
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getNames ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getNames
parameter_list|()
block|{
return|return
name|names
return|;
block|}
DECL|method|getReservedNames ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getReservedNames
parameter_list|()
block|{
return|return
name|reservedNames
return|;
block|}
annotation|@
name|Override
DECL|method|handles (AccountGroup.UUID uuid)
specifier|public
name|boolean
name|handles
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|uuid
parameter_list|)
block|{
return|return
name|isSystemGroup
argument_list|(
name|uuid
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|get (AccountGroup.UUID uuid)
specifier|public
name|GroupDescription
operator|.
name|Basic
name|get
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|uuid
parameter_list|)
block|{
specifier|final
name|GroupReference
name|ref
init|=
name|uuids
operator|.
name|get
argument_list|(
name|uuid
argument_list|)
decl_stmt|;
if|if
condition|(
name|ref
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|GroupDescription
operator|.
name|Basic
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|ref
operator|.
name|getName
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|AccountGroup
operator|.
name|UUID
name|getGroupUUID
parameter_list|()
block|{
return|return
name|ref
operator|.
name|getUUID
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getUrl
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getEmailAddress
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|suggest (String name, ProjectState project)
specifier|public
name|Collection
argument_list|<
name|GroupReference
argument_list|>
name|suggest
parameter_list|(
name|String
name|name
parameter_list|,
name|ProjectState
name|project
parameter_list|)
block|{
name|String
name|nameLC
init|=
name|name
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
name|SortedMap
argument_list|<
name|String
argument_list|,
name|GroupReference
argument_list|>
name|matches
init|=
name|namesToGroups
operator|.
name|tailMap
argument_list|(
name|nameLC
argument_list|)
decl_stmt|;
if|if
condition|(
name|matches
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
name|List
argument_list|<
name|GroupReference
argument_list|>
name|r
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|matches
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|GroupReference
argument_list|>
name|e
range|:
name|matches
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|e
operator|.
name|getKey
argument_list|()
operator|.
name|startsWith
argument_list|(
name|nameLC
argument_list|)
condition|)
block|{
name|r
operator|.
name|add
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
return|return
name|r
return|;
block|}
annotation|@
name|Override
DECL|method|membershipsOf (IdentifiedUser user)
specifier|public
name|GroupMembership
name|membershipsOf
parameter_list|(
name|IdentifiedUser
name|user
parameter_list|)
block|{
return|return
operator|new
name|ListGroupMembership
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|ANONYMOUS_USERS
argument_list|,
name|REGISTERED_USERS
argument_list|)
argument_list|)
return|;
block|}
DECL|class|NameCheck
specifier|public
specifier|static
class|class
name|NameCheck
implements|implements
name|StartupCheck
block|{
DECL|field|cfg
specifier|private
specifier|final
name|Config
name|cfg
decl_stmt|;
DECL|field|groupCache
specifier|private
specifier|final
name|GroupCache
name|groupCache
decl_stmt|;
annotation|@
name|Inject
DECL|method|NameCheck (@erritServerConfig Config cfg, GroupCache groupCache)
name|NameCheck
parameter_list|(
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|,
name|GroupCache
name|groupCache
parameter_list|)
block|{
name|this
operator|.
name|cfg
operator|=
name|cfg
expr_stmt|;
name|this
operator|.
name|groupCache
operator|=
name|groupCache
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|check ()
specifier|public
name|void
name|check
parameter_list|()
throws|throws
name|StartupException
block|{
name|Map
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|,
name|String
argument_list|>
name|configuredNames
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|byLowerCaseConfiguredName
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|AccountGroup
operator|.
name|UUID
name|uuid
range|:
name|all
control|)
block|{
name|String
name|configuredName
init|=
name|cfg
operator|.
name|getString
argument_list|(
literal|"groups"
argument_list|,
name|uuid
operator|.
name|get
argument_list|()
argument_list|,
literal|"name"
argument_list|)
decl_stmt|;
if|if
condition|(
name|configuredName
operator|!=
literal|null
condition|)
block|{
name|configuredNames
operator|.
name|put
argument_list|(
name|uuid
argument_list|,
name|configuredName
argument_list|)
expr_stmt|;
name|byLowerCaseConfiguredName
operator|.
name|put
argument_list|(
name|configuredName
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
argument_list|,
name|uuid
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|configuredNames
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
for|for
control|(
name|AccountGroup
name|g
range|:
name|groupCache
operator|.
name|all
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|g
operator|.
name|getName
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
if|if
condition|(
name|byLowerCaseConfiguredName
operator|.
name|keySet
argument_list|()
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|AccountGroup
operator|.
name|UUID
name|uuidSystemGroup
init|=
name|byLowerCaseConfiguredName
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|StartupException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"The configured name '%s' for system group '%s' is ambiguous"
operator|+
literal|" with the name '%s' of existing group '%s'."
operator|+
literal|" Please remove/change the value for groups.%s.name in"
operator|+
literal|" gerrit.config."
argument_list|,
name|configuredNames
operator|.
name|get
argument_list|(
name|uuidSystemGroup
argument_list|)
argument_list|,
name|uuidSystemGroup
operator|.
name|get
argument_list|()
argument_list|,
name|g
operator|.
name|getName
argument_list|()
argument_list|,
name|g
operator|.
name|getGroupUUID
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|uuidSystemGroup
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

