begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2008 The Android Open Source Project
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
DECL|package|com.google.gerrit.reviewdb.client
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|reviewdb
operator|.
name|client
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
name|Nullable
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
name|IntKey
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
name|StringKey
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Timestamp
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|Instant
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/** Named group of one or more accounts, typically used for access controls. */
end_comment

begin_class
DECL|class|AccountGroup
specifier|public
specifier|final
class|class
name|AccountGroup
block|{
comment|/**    * Time when the audit subsystem was implemented, used as the default value for {@link #createdOn}    * when one couldn't be determined from the audit log.    */
DECL|field|AUDIT_CREATION_INSTANT_MS
specifier|private
specifier|static
specifier|final
name|Instant
name|AUDIT_CREATION_INSTANT_MS
init|=
name|Instant
operator|.
name|ofEpochMilli
argument_list|(
literal|1244489460000L
argument_list|)
decl_stmt|;
DECL|method|auditCreationInstantTs ()
specifier|public
specifier|static
name|Timestamp
name|auditCreationInstantTs
parameter_list|()
block|{
return|return
name|Timestamp
operator|.
name|from
argument_list|(
name|AUDIT_CREATION_INSTANT_MS
argument_list|)
return|;
block|}
DECL|method|nameKey (String n)
specifier|public
specifier|static
name|NameKey
name|nameKey
parameter_list|(
name|String
name|n
parameter_list|)
block|{
return|return
operator|new
name|NameKey
argument_list|(
name|n
argument_list|)
return|;
block|}
comment|/** Group name key */
DECL|class|NameKey
specifier|public
specifier|static
class|class
name|NameKey
extends|extends
name|StringKey
argument_list|<
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|client
operator|.
name|Key
argument_list|<
name|?
argument_list|>
argument_list|>
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|field|name
specifier|protected
name|String
name|name
decl_stmt|;
DECL|method|NameKey ()
specifier|protected
name|NameKey
parameter_list|()
block|{}
DECL|method|NameKey (String n)
specifier|public
name|NameKey
parameter_list|(
name|String
name|n
parameter_list|)
block|{
name|name
operator|=
name|n
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get ()
specifier|public
name|String
name|get
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
DECL|method|set (String newValue)
specifier|protected
name|void
name|set
parameter_list|(
name|String
name|newValue
parameter_list|)
block|{
name|name
operator|=
name|newValue
expr_stmt|;
block|}
block|}
DECL|method|uuid (String n)
specifier|public
specifier|static
name|UUID
name|uuid
parameter_list|(
name|String
name|n
parameter_list|)
block|{
return|return
operator|new
name|UUID
argument_list|(
name|n
argument_list|)
return|;
block|}
comment|/** Globally unique identifier. */
DECL|class|UUID
specifier|public
specifier|static
class|class
name|UUID
extends|extends
name|StringKey
argument_list|<
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|client
operator|.
name|Key
argument_list|<
name|?
argument_list|>
argument_list|>
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|field|uuid
specifier|protected
name|String
name|uuid
decl_stmt|;
DECL|method|UUID ()
specifier|protected
name|UUID
parameter_list|()
block|{}
DECL|method|UUID (String n)
specifier|public
name|UUID
parameter_list|(
name|String
name|n
parameter_list|)
block|{
name|uuid
operator|=
name|n
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get ()
specifier|public
name|String
name|get
parameter_list|()
block|{
return|return
name|uuid
return|;
block|}
annotation|@
name|Override
DECL|method|set (String newValue)
specifier|protected
name|void
name|set
parameter_list|(
name|String
name|newValue
parameter_list|)
block|{
name|uuid
operator|=
name|newValue
expr_stmt|;
block|}
comment|/** Parse an {@link AccountGroup.UUID} out of a string representation. */
DECL|method|parse (String str)
specifier|public
specifier|static
name|UUID
name|parse
parameter_list|(
name|String
name|str
parameter_list|)
block|{
specifier|final
name|UUID
name|r
init|=
operator|new
name|UUID
argument_list|()
decl_stmt|;
name|r
operator|.
name|fromString
argument_list|(
name|str
argument_list|)
expr_stmt|;
return|return
name|r
return|;
block|}
comment|/** Parse an {@link AccountGroup.UUID} out of a ref-name. */
DECL|method|fromRef (String ref)
specifier|public
specifier|static
name|UUID
name|fromRef
parameter_list|(
name|String
name|ref
parameter_list|)
block|{
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
if|if
condition|(
name|ref
operator|.
name|startsWith
argument_list|(
name|RefNames
operator|.
name|REFS_GROUPS
argument_list|)
condition|)
block|{
return|return
name|fromRefPart
argument_list|(
name|ref
operator|.
name|substring
argument_list|(
name|RefNames
operator|.
name|REFS_GROUPS
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Parse an {@link AccountGroup.UUID} out of a part of a ref-name.      *      * @param refPart a ref name with the following syntax: {@code "12/1234..."}. We assume that the      *     caller has trimmed any prefix.      */
DECL|method|fromRefPart (String refPart)
specifier|public
specifier|static
name|UUID
name|fromRefPart
parameter_list|(
name|String
name|refPart
parameter_list|)
block|{
name|String
name|uuid
init|=
name|RefNames
operator|.
name|parseShardedUuidFromRefPart
argument_list|(
name|refPart
argument_list|)
decl_stmt|;
return|return
name|uuid
operator|!=
literal|null
condition|?
operator|new
name|AccountGroup
operator|.
name|UUID
argument_list|(
name|uuid
argument_list|)
else|:
literal|null
return|;
block|}
block|}
comment|/** @return true if the UUID is for a group managed within Gerrit. */
DECL|method|isInternalGroup (AccountGroup.UUID uuid)
specifier|public
specifier|static
name|boolean
name|isInternalGroup
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
name|matches
argument_list|(
literal|"^[0-9a-f]{40}$"
argument_list|)
return|;
block|}
DECL|method|id (int id)
specifier|public
specifier|static
name|Id
name|id
parameter_list|(
name|int
name|id
parameter_list|)
block|{
return|return
operator|new
name|Id
argument_list|(
name|id
argument_list|)
return|;
block|}
comment|/** Synthetic key to link to within the database */
DECL|class|Id
specifier|public
specifier|static
class|class
name|Id
extends|extends
name|IntKey
argument_list|<
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|client
operator|.
name|Key
argument_list|<
name|?
argument_list|>
argument_list|>
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|field|id
specifier|protected
name|int
name|id
decl_stmt|;
DECL|method|Id ()
specifier|protected
name|Id
parameter_list|()
block|{}
DECL|method|Id (int id)
specifier|public
name|Id
parameter_list|(
name|int
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get ()
specifier|public
name|int
name|get
parameter_list|()
block|{
return|return
name|id
return|;
block|}
annotation|@
name|Override
DECL|method|set (int newValue)
specifier|protected
name|void
name|set
parameter_list|(
name|int
name|newValue
parameter_list|)
block|{
name|id
operator|=
name|newValue
expr_stmt|;
block|}
comment|/** Parse an AccountGroup.Id out of a string representation. */
DECL|method|parse (String str)
specifier|public
specifier|static
name|Id
name|parse
parameter_list|(
name|String
name|str
parameter_list|)
block|{
specifier|final
name|Id
name|r
init|=
operator|new
name|Id
argument_list|()
decl_stmt|;
name|r
operator|.
name|fromString
argument_list|(
name|str
argument_list|)
expr_stmt|;
return|return
name|r
return|;
block|}
block|}
comment|/** Unique name of this group within the system. */
DECL|field|name
specifier|protected
name|NameKey
name|name
decl_stmt|;
comment|/** Unique identity, to link entities as {@link #name} can change. */
DECL|field|groupId
specifier|protected
name|Id
name|groupId
decl_stmt|;
comment|// DELETED: id = 3 (ownerGroupId)
comment|/** A textual description of the group's purpose. */
DECL|field|description
annotation|@
name|Nullable
specifier|protected
name|String
name|description
decl_stmt|;
comment|// DELETED: id = 5 (groupType)
comment|// DELETED: id = 6 (externalName)
DECL|field|visibleToAll
specifier|protected
name|boolean
name|visibleToAll
decl_stmt|;
comment|// DELETED: id = 8 (emailOnlyAuthors)
comment|/** Globally unique identifier name for this group. */
DECL|field|groupUUID
specifier|protected
name|UUID
name|groupUUID
decl_stmt|;
comment|/**    * Identity of the group whose members can manage this group.    *    *<p>This can be a self-reference to indicate the group's members manage itself.    */
DECL|field|ownerGroupUUID
specifier|protected
name|UUID
name|ownerGroupUUID
decl_stmt|;
DECL|field|createdOn
annotation|@
name|Nullable
specifier|protected
name|Timestamp
name|createdOn
decl_stmt|;
DECL|method|AccountGroup ()
specifier|protected
name|AccountGroup
parameter_list|()
block|{}
DECL|method|AccountGroup ( AccountGroup.NameKey newName, AccountGroup.Id newId, AccountGroup.UUID uuid, Timestamp createdOn)
specifier|public
name|AccountGroup
parameter_list|(
name|AccountGroup
operator|.
name|NameKey
name|newName
parameter_list|,
name|AccountGroup
operator|.
name|Id
name|newId
parameter_list|,
name|AccountGroup
operator|.
name|UUID
name|uuid
parameter_list|,
name|Timestamp
name|createdOn
parameter_list|)
block|{
name|name
operator|=
name|newName
expr_stmt|;
name|groupId
operator|=
name|newId
expr_stmt|;
name|visibleToAll
operator|=
literal|false
expr_stmt|;
name|groupUUID
operator|=
name|uuid
expr_stmt|;
name|ownerGroupUUID
operator|=
name|groupUUID
expr_stmt|;
name|this
operator|.
name|createdOn
operator|=
name|createdOn
expr_stmt|;
block|}
DECL|method|AccountGroup (AccountGroup other)
specifier|public
name|AccountGroup
parameter_list|(
name|AccountGroup
name|other
parameter_list|)
block|{
name|name
operator|=
name|other
operator|.
name|name
expr_stmt|;
name|groupId
operator|=
name|other
operator|.
name|groupId
expr_stmt|;
name|description
operator|=
name|other
operator|.
name|description
expr_stmt|;
name|visibleToAll
operator|=
name|other
operator|.
name|visibleToAll
expr_stmt|;
name|groupUUID
operator|=
name|other
operator|.
name|groupUUID
expr_stmt|;
name|ownerGroupUUID
operator|=
name|other
operator|.
name|ownerGroupUUID
expr_stmt|;
name|createdOn
operator|=
name|other
operator|.
name|createdOn
expr_stmt|;
block|}
DECL|method|getId ()
specifier|public
name|AccountGroup
operator|.
name|Id
name|getId
parameter_list|()
block|{
return|return
name|groupId
return|;
block|}
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|getNameKey ()
specifier|public
name|AccountGroup
operator|.
name|NameKey
name|getNameKey
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|setNameKey (AccountGroup.NameKey nameKey)
specifier|public
name|void
name|setNameKey
parameter_list|(
name|AccountGroup
operator|.
name|NameKey
name|nameKey
parameter_list|)
block|{
name|name
operator|=
name|nameKey
expr_stmt|;
block|}
DECL|method|getDescription ()
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|description
return|;
block|}
DECL|method|setDescription (String d)
specifier|public
name|void
name|setDescription
parameter_list|(
name|String
name|d
parameter_list|)
block|{
name|description
operator|=
name|d
expr_stmt|;
block|}
DECL|method|getOwnerGroupUUID ()
specifier|public
name|AccountGroup
operator|.
name|UUID
name|getOwnerGroupUUID
parameter_list|()
block|{
return|return
name|ownerGroupUUID
return|;
block|}
DECL|method|setOwnerGroupUUID (AccountGroup.UUID uuid)
specifier|public
name|void
name|setOwnerGroupUUID
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|uuid
parameter_list|)
block|{
name|ownerGroupUUID
operator|=
name|uuid
expr_stmt|;
block|}
DECL|method|setVisibleToAll (boolean visibleToAll)
specifier|public
name|void
name|setVisibleToAll
parameter_list|(
name|boolean
name|visibleToAll
parameter_list|)
block|{
name|this
operator|.
name|visibleToAll
operator|=
name|visibleToAll
expr_stmt|;
block|}
DECL|method|isVisibleToAll ()
specifier|public
name|boolean
name|isVisibleToAll
parameter_list|()
block|{
return|return
name|visibleToAll
return|;
block|}
DECL|method|getGroupUUID ()
specifier|public
name|AccountGroup
operator|.
name|UUID
name|getGroupUUID
parameter_list|()
block|{
return|return
name|groupUUID
return|;
block|}
DECL|method|setGroupUUID (AccountGroup.UUID uuid)
specifier|public
name|void
name|setGroupUUID
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|uuid
parameter_list|)
block|{
name|groupUUID
operator|=
name|uuid
expr_stmt|;
block|}
DECL|method|getCreatedOn ()
specifier|public
name|Timestamp
name|getCreatedOn
parameter_list|()
block|{
return|return
name|createdOn
operator|!=
literal|null
condition|?
name|createdOn
else|:
name|auditCreationInstantTs
argument_list|()
return|;
block|}
DECL|method|setCreatedOn (Timestamp createdOn)
specifier|public
name|void
name|setCreatedOn
parameter_list|(
name|Timestamp
name|createdOn
parameter_list|)
block|{
name|this
operator|.
name|createdOn
operator|=
name|createdOn
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|AccountGroup
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|AccountGroup
name|g
init|=
operator|(
name|AccountGroup
operator|)
name|o
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|name
argument_list|,
name|g
operator|.
name|name
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|groupId
argument_list|,
name|g
operator|.
name|groupId
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|description
argument_list|,
name|g
operator|.
name|description
argument_list|)
operator|&&
name|visibleToAll
operator|==
name|g
operator|.
name|visibleToAll
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|groupUUID
argument_list|,
name|g
operator|.
name|groupUUID
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|ownerGroupUUID
argument_list|,
name|g
operator|.
name|ownerGroupUUID
argument_list|)
comment|// Treat created on epoch identical regardless if underlying value is null.
operator|&&
name|getCreatedOn
argument_list|()
operator|.
name|equals
argument_list|(
name|g
operator|.
name|getCreatedOn
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|name
argument_list|,
name|groupId
argument_list|,
name|description
argument_list|,
name|visibleToAll
argument_list|,
name|groupUUID
argument_list|,
name|ownerGroupUUID
argument_list|,
name|createdOn
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"{"
operator|+
literal|"name="
operator|+
name|name
operator|+
literal|", groupId="
operator|+
name|groupId
operator|+
literal|", description="
operator|+
name|description
operator|+
literal|", visibleToAll="
operator|+
name|visibleToAll
operator|+
literal|", groupUUID="
operator|+
name|groupUUID
operator|+
literal|", ownerGroupUUID="
operator|+
name|ownerGroupUUID
operator|+
literal|", createdOn="
operator|+
name|createdOn
operator|+
literal|"}"
return|;
block|}
block|}
end_class

end_unit

