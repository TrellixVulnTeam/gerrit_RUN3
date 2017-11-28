begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2011 The Android Open Source Project
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
name|gwtorm
operator|.
name|client
operator|.
name|Column
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
name|CompoundKey
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
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/** Inclusion of an {@link AccountGroup} in another {@link AccountGroup}. */
end_comment

begin_class
DECL|class|AccountGroupByIdAud
specifier|public
specifier|final
class|class
name|AccountGroupByIdAud
block|{
DECL|class|Key
specifier|public
specifier|static
class|class
name|Key
extends|extends
name|CompoundKey
argument_list|<
name|AccountGroup
operator|.
name|Id
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
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|1
argument_list|)
DECL|field|groupId
specifier|protected
name|AccountGroup
operator|.
name|Id
name|groupId
decl_stmt|;
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|2
argument_list|)
DECL|field|includeUUID
specifier|protected
name|AccountGroup
operator|.
name|UUID
name|includeUUID
decl_stmt|;
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|3
argument_list|)
DECL|field|addedOn
specifier|protected
name|Timestamp
name|addedOn
decl_stmt|;
DECL|method|Key ()
specifier|protected
name|Key
parameter_list|()
block|{
name|groupId
operator|=
operator|new
name|AccountGroup
operator|.
name|Id
argument_list|()
expr_stmt|;
name|includeUUID
operator|=
operator|new
name|AccountGroup
operator|.
name|UUID
argument_list|()
expr_stmt|;
block|}
DECL|method|Key (AccountGroup.Id g, AccountGroup.UUID u, Timestamp t)
specifier|public
name|Key
parameter_list|(
name|AccountGroup
operator|.
name|Id
name|g
parameter_list|,
name|AccountGroup
operator|.
name|UUID
name|u
parameter_list|,
name|Timestamp
name|t
parameter_list|)
block|{
name|groupId
operator|=
name|g
expr_stmt|;
name|includeUUID
operator|=
name|u
expr_stmt|;
name|addedOn
operator|=
name|t
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getParentKey ()
specifier|public
name|AccountGroup
operator|.
name|Id
name|getParentKey
parameter_list|()
block|{
return|return
name|groupId
return|;
block|}
DECL|method|getIncludeUUID ()
specifier|public
name|AccountGroup
operator|.
name|UUID
name|getIncludeUUID
parameter_list|()
block|{
return|return
name|includeUUID
return|;
block|}
DECL|method|getAddedOn ()
specifier|public
name|Timestamp
name|getAddedOn
parameter_list|()
block|{
return|return
name|addedOn
return|;
block|}
annotation|@
name|Override
DECL|method|members ()
specifier|public
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
index|[]
name|members
parameter_list|()
block|{
return|return
operator|new
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
index|[]
block|{
name|includeUUID
block|}
empty_stmt|;
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
literal|"Key{"
operator|+
literal|"groupId="
operator|+
name|groupId
operator|+
literal|", includeUUID="
operator|+
name|includeUUID
operator|+
literal|", addedOn="
operator|+
name|addedOn
operator|+
literal|'}'
return|;
block|}
block|}
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|1
argument_list|,
name|name
operator|=
name|Column
operator|.
name|NONE
argument_list|)
DECL|field|key
specifier|protected
name|Key
name|key
decl_stmt|;
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|2
argument_list|)
DECL|field|addedBy
specifier|protected
name|Account
operator|.
name|Id
name|addedBy
decl_stmt|;
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|3
argument_list|,
name|notNull
operator|=
literal|false
argument_list|)
DECL|field|removedBy
specifier|protected
name|Account
operator|.
name|Id
name|removedBy
decl_stmt|;
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|4
argument_list|,
name|notNull
operator|=
literal|false
argument_list|)
DECL|field|removedOn
specifier|protected
name|Timestamp
name|removedOn
decl_stmt|;
DECL|method|AccountGroupByIdAud ()
specifier|protected
name|AccountGroupByIdAud
parameter_list|()
block|{}
DECL|method|AccountGroupByIdAud (final AccountGroupById m, Account.Id adder, Timestamp when)
specifier|public
name|AccountGroupByIdAud
parameter_list|(
specifier|final
name|AccountGroupById
name|m
parameter_list|,
name|Account
operator|.
name|Id
name|adder
parameter_list|,
name|Timestamp
name|when
parameter_list|)
block|{
specifier|final
name|AccountGroup
operator|.
name|Id
name|group
init|=
name|m
operator|.
name|getGroupId
argument_list|()
decl_stmt|;
specifier|final
name|AccountGroup
operator|.
name|UUID
name|include
init|=
name|m
operator|.
name|getIncludeUUID
argument_list|()
decl_stmt|;
name|key
operator|=
operator|new
name|AccountGroupByIdAud
operator|.
name|Key
argument_list|(
name|group
argument_list|,
name|include
argument_list|,
name|when
argument_list|)
expr_stmt|;
name|addedBy
operator|=
name|adder
expr_stmt|;
block|}
DECL|method|AccountGroupByIdAud (AccountGroupByIdAud.Key key, Account.Id adder)
specifier|public
name|AccountGroupByIdAud
parameter_list|(
name|AccountGroupByIdAud
operator|.
name|Key
name|key
parameter_list|,
name|Account
operator|.
name|Id
name|adder
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|addedBy
operator|=
name|adder
expr_stmt|;
block|}
DECL|method|getKey ()
specifier|public
name|AccountGroupByIdAud
operator|.
name|Key
name|getKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
DECL|method|getGroupId ()
specifier|public
name|AccountGroup
operator|.
name|Id
name|getGroupId
parameter_list|()
block|{
return|return
name|key
operator|.
name|getParentKey
argument_list|()
return|;
block|}
DECL|method|getIncludeUUID ()
specifier|public
name|AccountGroup
operator|.
name|UUID
name|getIncludeUUID
parameter_list|()
block|{
return|return
name|key
operator|.
name|getIncludeUUID
argument_list|()
return|;
block|}
DECL|method|isActive ()
specifier|public
name|boolean
name|isActive
parameter_list|()
block|{
return|return
name|removedOn
operator|==
literal|null
return|;
block|}
DECL|method|removed (Account.Id deleter, Timestamp when)
specifier|public
name|void
name|removed
parameter_list|(
name|Account
operator|.
name|Id
name|deleter
parameter_list|,
name|Timestamp
name|when
parameter_list|)
block|{
name|removedBy
operator|=
name|deleter
expr_stmt|;
name|removedOn
operator|=
name|when
expr_stmt|;
block|}
DECL|method|getAddedBy ()
specifier|public
name|Account
operator|.
name|Id
name|getAddedBy
parameter_list|()
block|{
return|return
name|addedBy
return|;
block|}
DECL|method|getAddedOn ()
specifier|public
name|Timestamp
name|getAddedOn
parameter_list|()
block|{
return|return
name|key
operator|.
name|getAddedOn
argument_list|()
return|;
block|}
DECL|method|getRemovedBy ()
specifier|public
name|Account
operator|.
name|Id
name|getRemovedBy
parameter_list|()
block|{
return|return
name|removedBy
return|;
block|}
DECL|method|getRemovedOn ()
specifier|public
name|Timestamp
name|getRemovedOn
parameter_list|()
block|{
return|return
name|removedOn
return|;
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
name|AccountGroupByIdAud
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|AccountGroupByIdAud
name|a
init|=
operator|(
name|AccountGroupByIdAud
operator|)
name|o
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|key
argument_list|,
name|a
operator|.
name|key
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|addedBy
argument_list|,
name|a
operator|.
name|addedBy
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|removedBy
argument_list|,
name|a
operator|.
name|removedBy
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|removedOn
argument_list|,
name|a
operator|.
name|removedOn
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
name|key
argument_list|,
name|addedBy
argument_list|,
name|removedBy
argument_list|,
name|removedOn
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
literal|"key="
operator|+
name|key
operator|+
literal|", addedBy="
operator|+
name|addedBy
operator|+
literal|", removedBy="
operator|+
name|removedBy
operator|+
literal|", removedOn="
operator|+
name|removedOn
operator|+
literal|"}"
return|;
block|}
block|}
end_class

end_unit

