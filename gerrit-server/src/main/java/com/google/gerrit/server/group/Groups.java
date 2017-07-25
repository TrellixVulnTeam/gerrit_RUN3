begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2017 The Android Open Source Project
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
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
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
name|Streams
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
name|AccountGroupById
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
name|AccountGroupMember
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
name|AccountGroupName
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
name|gwtorm
operator|.
name|server
operator|.
name|OrmDuplicateKeyException
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
name|gwtorm
operator|.
name|server
operator|.
name|ResultSet
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Stream
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|Groups
specifier|public
class|class
name|Groups
block|{
DECL|method|get (ReviewDb db, AccountGroup.Id groupId)
specifier|public
name|Optional
argument_list|<
name|AccountGroup
argument_list|>
name|get
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|AccountGroup
operator|.
name|Id
name|groupId
parameter_list|)
throws|throws
name|OrmException
block|{
return|return
name|Optional
operator|.
name|ofNullable
argument_list|(
name|db
operator|.
name|accountGroups
argument_list|()
operator|.
name|get
argument_list|(
name|groupId
argument_list|)
argument_list|)
return|;
block|}
DECL|method|get (ReviewDb db, AccountGroup.UUID groupUuid)
specifier|public
name|Optional
argument_list|<
name|AccountGroup
argument_list|>
name|get
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|AccountGroup
operator|.
name|UUID
name|groupUuid
parameter_list|)
throws|throws
name|OrmException
block|{
name|List
argument_list|<
name|AccountGroup
argument_list|>
name|accountGroups
init|=
name|db
operator|.
name|accountGroups
argument_list|()
operator|.
name|byUUID
argument_list|(
name|groupUuid
argument_list|)
operator|.
name|toList
argument_list|()
decl_stmt|;
if|if
condition|(
name|accountGroups
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|Optional
operator|.
name|of
argument_list|(
name|Iterables
operator|.
name|getOnlyElement
argument_list|(
name|accountGroups
argument_list|)
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|accountGroups
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|Optional
operator|.
name|empty
argument_list|()
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|OrmDuplicateKeyException
argument_list|(
literal|"Duplicate group UUID "
operator|+
name|groupUuid
argument_list|)
throw|;
block|}
block|}
DECL|method|get (ReviewDb db, AccountGroup.NameKey groupName)
specifier|public
name|Optional
argument_list|<
name|AccountGroup
argument_list|>
name|get
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|AccountGroup
operator|.
name|NameKey
name|groupName
parameter_list|)
throws|throws
name|OrmException
block|{
name|AccountGroupName
name|accountGroupName
init|=
name|db
operator|.
name|accountGroupNames
argument_list|()
operator|.
name|get
argument_list|(
name|groupName
argument_list|)
decl_stmt|;
if|if
condition|(
name|accountGroupName
operator|==
literal|null
condition|)
block|{
return|return
name|Optional
operator|.
name|empty
argument_list|()
return|;
block|}
name|AccountGroup
operator|.
name|Id
name|groupId
init|=
name|accountGroupName
operator|.
name|getId
argument_list|()
decl_stmt|;
return|return
name|Optional
operator|.
name|ofNullable
argument_list|(
name|db
operator|.
name|accountGroups
argument_list|()
operator|.
name|get
argument_list|(
name|groupId
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getAll (ReviewDb db)
specifier|public
name|ImmutableList
argument_list|<
name|AccountGroup
argument_list|>
name|getAll
parameter_list|(
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
block|{
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|db
operator|.
name|accountGroups
argument_list|()
operator|.
name|all
argument_list|()
argument_list|)
return|;
block|}
DECL|method|isMember (ReviewDb db, AccountGroup group, Account.Id accountId)
specifier|public
name|boolean
name|isMember
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|AccountGroup
name|group
parameter_list|,
name|Account
operator|.
name|Id
name|accountId
parameter_list|)
throws|throws
name|OrmException
block|{
name|AccountGroupMember
operator|.
name|Key
name|key
init|=
operator|new
name|AccountGroupMember
operator|.
name|Key
argument_list|(
name|accountId
argument_list|,
name|group
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|db
operator|.
name|accountGroupMembers
argument_list|()
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|!=
literal|null
return|;
block|}
DECL|method|getMembers (ReviewDb db, AccountGroup.Id groupId)
specifier|public
name|Stream
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|getMembers
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|AccountGroup
operator|.
name|Id
name|groupId
parameter_list|)
throws|throws
name|OrmException
block|{
name|ResultSet
argument_list|<
name|AccountGroupMember
argument_list|>
name|accountGroupMembers
init|=
name|db
operator|.
name|accountGroupMembers
argument_list|()
operator|.
name|byGroup
argument_list|(
name|groupId
argument_list|)
decl_stmt|;
return|return
name|Streams
operator|.
name|stream
argument_list|(
name|accountGroupMembers
argument_list|)
operator|.
name|map
argument_list|(
name|AccountGroupMember
operator|::
name|getAccountId
argument_list|)
return|;
block|}
DECL|method|getIncludes (ReviewDb db, AccountGroup.Id groupId)
specifier|public
name|Stream
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|getIncludes
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|AccountGroup
operator|.
name|Id
name|groupId
parameter_list|)
throws|throws
name|OrmException
block|{
name|ResultSet
argument_list|<
name|AccountGroupById
argument_list|>
name|accountGroupByIds
init|=
name|db
operator|.
name|accountGroupById
argument_list|()
operator|.
name|byGroup
argument_list|(
name|groupId
argument_list|)
decl_stmt|;
return|return
name|Streams
operator|.
name|stream
argument_list|(
name|accountGroupByIds
argument_list|)
operator|.
name|map
argument_list|(
name|AccountGroupById
operator|::
name|getIncludeUUID
argument_list|)
return|;
block|}
block|}
end_class

end_unit

