begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2012 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.schema
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|schema
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
name|Strings
operator|.
name|isNullOrEmpty
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
name|Preconditions
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
name|common
operator|.
name|collect
operator|.
name|Maps
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
name|Sets
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
name|reviewdb
operator|.
name|client
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
name|GerritPersonIdent
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
name|extensions
operator|.
name|events
operator|.
name|GitReferenceUpdated
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
name|git
operator|.
name|GitRepositoryManager
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
name|git
operator|.
name|MetaDataUpdate
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
name|git
operator|.
name|ProjectConfig
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
name|OrmException
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
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|errors
operator|.
name|ConfigInvalidException
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
name|errors
operator|.
name|RepositoryNotFoundException
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
name|PersonIdent
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
name|Repository
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Statement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|ldap
operator|.
name|LdapName
import|;
end_import

begin_class
DECL|class|Schema_69
specifier|public
class|class
name|Schema_69
extends|extends
name|SchemaVersion
block|{
DECL|field|mgr
specifier|private
specifier|final
name|GitRepositoryManager
name|mgr
decl_stmt|;
DECL|field|serverUser
specifier|private
specifier|final
name|PersonIdent
name|serverUser
decl_stmt|;
annotation|@
name|Inject
DECL|method|Schema_69 (Provider<Schema_68> prior, GitRepositoryManager mgr, @GerritPersonIdent PersonIdent serverUser)
name|Schema_69
parameter_list|(
name|Provider
argument_list|<
name|Schema_68
argument_list|>
name|prior
parameter_list|,
name|GitRepositoryManager
name|mgr
parameter_list|,
annotation|@
name|GerritPersonIdent
name|PersonIdent
name|serverUser
parameter_list|)
block|{
name|super
argument_list|(
name|prior
argument_list|)
expr_stmt|;
name|this
operator|.
name|mgr
operator|=
name|mgr
expr_stmt|;
name|this
operator|.
name|serverUser
operator|=
name|serverUser
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|migrateData (ReviewDb db, UpdateUI ui)
specifier|protected
name|void
name|migrateData
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|UpdateUI
name|ui
parameter_list|)
throws|throws
name|OrmException
throws|,
name|SQLException
block|{
comment|// Find all groups that have an LDAP type.
name|Map
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|,
name|GroupReference
argument_list|>
name|ldapUUIDMap
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|toResolve
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
name|toDelete
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|AccountGroup
operator|.
name|NameKey
argument_list|>
name|namesToDelete
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
try|try
init|(
name|Statement
name|stmt
init|=
name|newStatement
argument_list|(
name|db
argument_list|)
init|;
name|ResultSet
name|rs
operator|=
name|stmt
operator|.
name|executeQuery
argument_list|(
literal|"SELECT group_id, group_uuid, external_name, name FROM account_groups"
operator|+
literal|" WHERE group_type ='LDAP'"
argument_list|)
init|)
block|{
while|while
condition|(
name|rs
operator|.
name|next
argument_list|()
condition|)
block|{
name|AccountGroup
operator|.
name|Id
name|groupId
init|=
operator|new
name|AccountGroup
operator|.
name|Id
argument_list|(
name|rs
operator|.
name|getInt
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|AccountGroup
operator|.
name|UUID
name|groupUUID
init|=
operator|new
name|AccountGroup
operator|.
name|UUID
argument_list|(
name|rs
operator|.
name|getString
argument_list|(
literal|2
argument_list|)
argument_list|)
decl_stmt|;
name|AccountGroup
operator|.
name|NameKey
name|name
init|=
operator|new
name|AccountGroup
operator|.
name|NameKey
argument_list|(
name|rs
operator|.
name|getString
argument_list|(
literal|4
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|dn
init|=
name|rs
operator|.
name|getString
argument_list|(
literal|3
argument_list|)
decl_stmt|;
if|if
condition|(
name|isNullOrEmpty
argument_list|(
name|dn
argument_list|)
condition|)
block|{
comment|// The LDAP group does not have a DN. Determine if the UUID is used.
name|toResolve
operator|.
name|add
argument_list|(
name|groupUUID
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|toDelete
operator|.
name|add
argument_list|(
name|groupId
argument_list|)
expr_stmt|;
name|namesToDelete
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|GroupReference
name|ref
init|=
name|groupReference
argument_list|(
name|dn
argument_list|)
decl_stmt|;
name|ldapUUIDMap
operator|.
name|put
argument_list|(
name|groupUUID
argument_list|,
name|ref
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
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|toDelete
operator|.
name|isEmpty
argument_list|()
operator|&&
name|toResolve
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
comment|// No ldap groups. Nothing to do.
block|}
name|ui
operator|.
name|message
argument_list|(
literal|"Update LDAP groups to be GroupReferences."
argument_list|)
expr_stmt|;
comment|// Update the groupOwnerUUID for LDAP groups to point to the new UUID.
name|List
argument_list|<
name|AccountGroup
argument_list|>
name|toUpdate
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|resolveToUpdate
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|,
name|AccountGroup
argument_list|>
name|resolveGroups
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|AccountGroup
name|g
range|:
name|db
operator|.
name|accountGroups
argument_list|()
operator|.
name|all
argument_list|()
control|)
block|{
if|if
condition|(
name|ldapUUIDMap
operator|.
name|containsKey
argument_list|(
name|g
operator|.
name|getGroupUUID
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
comment|// Ignore the LDAP groups with a valid DN.
block|}
elseif|else
if|if
condition|(
name|toResolve
operator|.
name|contains
argument_list|(
name|g
operator|.
name|getGroupUUID
argument_list|()
argument_list|)
condition|)
block|{
name|resolveGroups
operator|.
name|put
argument_list|(
name|g
operator|.
name|getGroupUUID
argument_list|()
argument_list|,
name|g
argument_list|)
expr_stmt|;
comment|// Keep the ones to resolve.
continue|continue;
block|}
name|GroupReference
name|ref
init|=
name|ldapUUIDMap
operator|.
name|get
argument_list|(
name|g
operator|.
name|getOwnerGroupUUID
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|ref
operator|!=
literal|null
condition|)
block|{
comment|// Update the owner group UUID to the new ldap UUID scheme.
name|g
operator|.
name|setOwnerGroupUUID
argument_list|(
name|ref
operator|.
name|getUUID
argument_list|()
argument_list|)
expr_stmt|;
name|toUpdate
operator|.
name|add
argument_list|(
name|g
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|toResolve
operator|.
name|contains
argument_list|(
name|g
operator|.
name|getOwnerGroupUUID
argument_list|()
argument_list|)
condition|)
block|{
comment|// The unresolved group is used as an owner.
comment|// Add to the list of LDAP groups to be made INTERNAL.
name|resolveToUpdate
operator|.
name|add
argument_list|(
name|g
operator|.
name|getOwnerGroupUUID
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|toResolve
operator|.
name|removeAll
argument_list|(
name|resolveToUpdate
argument_list|)
expr_stmt|;
comment|// Update project.config group references to use the new LDAP GroupReference
for|for
control|(
name|Project
operator|.
name|NameKey
name|name
range|:
name|mgr
operator|.
name|list
argument_list|()
control|)
block|{
name|Repository
name|git
decl_stmt|;
try|try
block|{
name|git
operator|=
name|mgr
operator|.
name|openRepository
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|OrmException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|OrmException
argument_list|(
name|e
argument_list|)
throw|;
block|}
try|try
block|{
name|MetaDataUpdate
name|md
init|=
operator|new
name|MetaDataUpdate
argument_list|(
name|GitReferenceUpdated
operator|.
name|DISABLED
argument_list|,
name|name
argument_list|,
name|git
argument_list|)
decl_stmt|;
name|md
operator|.
name|getCommitBuilder
argument_list|()
operator|.
name|setAuthor
argument_list|(
name|serverUser
argument_list|)
expr_stmt|;
name|md
operator|.
name|getCommitBuilder
argument_list|()
operator|.
name|setCommitter
argument_list|(
name|serverUser
argument_list|)
expr_stmt|;
name|ProjectConfig
name|config
init|=
name|ProjectConfig
operator|.
name|read
argument_list|(
name|md
argument_list|)
decl_stmt|;
comment|// Update the existing refences to the new reference.
name|boolean
name|updated
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|,
name|GroupReference
argument_list|>
name|entry
range|:
name|ldapUUIDMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|GroupReference
name|ref
init|=
name|config
operator|.
name|getGroup
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|ref
operator|!=
literal|null
condition|)
block|{
name|updated
operator|=
literal|true
expr_stmt|;
name|ref
operator|.
name|setName
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|ref
operator|.
name|setUUID
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getUUID
argument_list|()
argument_list|)
expr_stmt|;
name|config
operator|.
name|resolve
argument_list|(
name|ref
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Determine if a toResolve group is used and should be made INTERNAL.
name|Iterator
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|iter
init|=
name|toResolve
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|AccountGroup
operator|.
name|UUID
name|uuid
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|config
operator|.
name|getGroup
argument_list|(
name|uuid
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|resolveToUpdate
operator|.
name|add
argument_list|(
name|uuid
argument_list|)
expr_stmt|;
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|updated
condition|)
block|{
continue|continue;
block|}
name|md
operator|.
name|setMessage
argument_list|(
literal|"Switch LDAP group UUIDs to DNs\n"
argument_list|)
expr_stmt|;
name|config
operator|.
name|commit
argument_list|(
name|md
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|OrmException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ConfigInvalidException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|OrmException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|git
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
for|for
control|(
name|AccountGroup
operator|.
name|UUID
name|uuid
range|:
name|resolveToUpdate
control|)
block|{
name|AccountGroup
name|group
init|=
name|resolveGroups
operator|.
name|get
argument_list|(
name|uuid
argument_list|)
decl_stmt|;
name|ui
operator|.
name|message
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"*** Group has no DN and is in use: %s"
argument_list|,
name|group
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|AccountGroup
operator|.
name|UUID
name|uuid
range|:
name|toResolve
control|)
block|{
name|AccountGroup
name|group
init|=
name|resolveGroups
operator|.
name|get
argument_list|(
name|uuid
argument_list|)
decl_stmt|;
name|toDelete
operator|.
name|add
argument_list|(
name|group
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|namesToDelete
operator|.
name|add
argument_list|(
name|group
operator|.
name|getNameKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Update group owners
name|db
operator|.
name|accountGroups
argument_list|()
operator|.
name|update
argument_list|(
name|toUpdate
argument_list|)
expr_stmt|;
comment|// Delete existing LDAP groups
name|db
operator|.
name|accountGroupNames
argument_list|()
operator|.
name|deleteKeys
argument_list|(
name|namesToDelete
argument_list|)
expr_stmt|;
name|db
operator|.
name|accountGroups
argument_list|()
operator|.
name|deleteKeys
argument_list|(
name|toDelete
argument_list|)
expr_stmt|;
block|}
DECL|method|groupReference (String dn)
specifier|private
specifier|static
name|GroupReference
name|groupReference
parameter_list|(
name|String
name|dn
parameter_list|)
throws|throws
name|NamingException
block|{
name|LdapName
name|name
init|=
operator|new
name|LdapName
argument_list|(
name|dn
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
operator|!
name|name
operator|.
name|isEmpty
argument_list|()
argument_list|,
literal|"Invalid LDAP dn: %s"
argument_list|,
name|dn
argument_list|)
expr_stmt|;
name|String
name|cn
init|=
name|name
operator|.
name|get
argument_list|(
name|name
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|int
name|index
init|=
name|cn
operator|.
name|indexOf
argument_list|(
literal|'='
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|>=
literal|0
condition|)
block|{
name|cn
operator|=
name|cn
operator|.
name|substring
argument_list|(
name|index
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|GroupReference
argument_list|(
operator|new
name|AccountGroup
operator|.
name|UUID
argument_list|(
literal|"ldap:"
operator|+
name|dn
argument_list|)
argument_list|,
literal|"ldap/"
operator|+
name|cn
argument_list|)
return|;
block|}
block|}
end_class

end_unit

