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
name|common
operator|.
name|errors
operator|.
name|NoSuchGroupException
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

begin_comment
comment|/**  * A database accessor for read calls related to groups.  *  *<p>All calls which read group related details from the database (either ReviewDb or NoteDb) are  * gathered here. Other classes should always use this class instead of accessing the database  * directly. There are a few exceptions though: schema classes, wrapper classes, and classes  * executed during init. The latter ones should use {@code GroupsOnInit} instead.  *  *<p>If not explicitly stated, all methods of this class refer to<em>internal</em> groups.  */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|Groups
specifier|public
class|class
name|Groups
block|{
comment|/**    * Returns the {@code AccountGroup} for the specified UUID.    *    * @param db the {@code ReviewDb} instance to use for lookups    * @param groupUuid the UUID of the group    * @return the {@code AccountGroup} which has the specified UUID    * @throws OrmDuplicateKeyException if multiple groups are found for the specified UUID    * @throws OrmException if the group couldn't be retrieved from ReviewDb    * @throws NoSuchGroupException if a group with such a UUID doesn't exist    */
DECL|method|getExistingGroup (ReviewDb db, AccountGroup.UUID groupUuid)
specifier|public
name|AccountGroup
name|getExistingGroup
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
throws|,
name|NoSuchGroupException
block|{
name|Optional
argument_list|<
name|AccountGroup
argument_list|>
name|group
init|=
name|getGroup
argument_list|(
name|db
argument_list|,
name|groupUuid
argument_list|)
decl_stmt|;
return|return
name|group
operator|.
name|orElseThrow
argument_list|(
parameter_list|()
lambda|->
operator|new
name|NoSuchGroupException
argument_list|(
name|groupUuid
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Returns the {@code AccountGroup} for the specified ID if it exists.    *    * @param db the {@code ReviewDb} instance to use for lookups    * @param groupId the ID of the group    * @return the found {@code AccountGroup} if it exists, or else an empty {@code Optional}    * @throws OrmException if the group couldn't be retrieved from ReviewDb    */
DECL|method|getGroup (ReviewDb db, AccountGroup.Id groupId)
specifier|public
name|Optional
argument_list|<
name|AccountGroup
argument_list|>
name|getGroup
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
comment|/**    * Returns the {@code AccountGroup} for the specified UUID if it exists.    *    * @param db the {@code ReviewDb} instance to use for lookups    * @param groupUuid the UUID of the group    * @return the found {@code AccountGroup} if it exists, or else an empty {@code Optional}    * @throws OrmDuplicateKeyException if multiple groups are found for the specified UUID    * @throws OrmException if the group couldn't be retrieved from ReviewDb    */
DECL|method|getGroup (ReviewDb db, AccountGroup.UUID groupUuid)
specifier|public
name|Optional
argument_list|<
name|AccountGroup
argument_list|>
name|getGroup
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
DECL|method|getAll (ReviewDb db)
specifier|public
name|Stream
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
name|Streams
operator|.
name|stream
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
comment|/**    * Indicates whether the specified account is a member of the specified group.    *    *<p><strong>Note</strong>: This method doesn't check whether the account exists!    *    * @param db the {@code ReviewDb} instance to use for lookups    * @param groupUuid the UUID of the group    * @param accountId the ID of the account    * @return {@code true} if the account is a member of the group, or else {@code false}    * @throws OrmException if an error occurs while reading from ReviewDb    * @throws NoSuchGroupException if the specified group doesn't exist    */
DECL|method|isMember (ReviewDb db, AccountGroup.UUID groupUuid, Account.Id accountId)
specifier|public
name|boolean
name|isMember
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|AccountGroup
operator|.
name|UUID
name|groupUuid
parameter_list|,
name|Account
operator|.
name|Id
name|accountId
parameter_list|)
throws|throws
name|OrmException
throws|,
name|NoSuchGroupException
block|{
name|AccountGroup
name|group
init|=
name|getExistingGroup
argument_list|(
name|db
argument_list|,
name|groupUuid
argument_list|)
decl_stmt|;
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
comment|/**    * Indicates whether the specified group is a subgroup of the specified parent group.    *    *<p>The parent group must be an internal group whereas the subgroup may either be an internal or    * an external group.    *    *<p><strong>Note</strong>: This method doesn't check whether the subgroup exists!    *    * @param db the {@code ReviewDb} instance to use for lookups    * @param parentGroupUuid the UUID of the parent group    * @param subgroupUuid the UUID of the subgroup    * @return {@code true} if the group is a subgroup of the other group, or else {@code false}    * @throws OrmException if an error occurs while reading from ReviewDb    * @throws NoSuchGroupException if the specified parent group doesn't exist    */
DECL|method|isSubgroup ( ReviewDb db, AccountGroup.UUID parentGroupUuid, AccountGroup.UUID subgroupUuid)
specifier|public
name|boolean
name|isSubgroup
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|AccountGroup
operator|.
name|UUID
name|parentGroupUuid
parameter_list|,
name|AccountGroup
operator|.
name|UUID
name|subgroupUuid
parameter_list|)
throws|throws
name|OrmException
throws|,
name|NoSuchGroupException
block|{
name|AccountGroup
name|parentGroup
init|=
name|getExistingGroup
argument_list|(
name|db
argument_list|,
name|parentGroupUuid
argument_list|)
decl_stmt|;
name|AccountGroupById
operator|.
name|Key
name|key
init|=
operator|new
name|AccountGroupById
operator|.
name|Key
argument_list|(
name|parentGroup
operator|.
name|getId
argument_list|()
argument_list|,
name|subgroupUuid
argument_list|)
decl_stmt|;
return|return
name|db
operator|.
name|accountGroupById
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
comment|/**    * Returns the members (accounts) of a group.    *    *<p><strong>Note</strong>: This method doesn't check whether the accounts exist!    *    * @param db the {@code ReviewDb} instance to use for lookups    * @param groupUuid the UUID of the group    * @return a stream of the IDs of the members    * @throws OrmException if an error occurs while reading from ReviewDb    * @throws NoSuchGroupException if the specified group doesn't exist    */
DECL|method|getMembers (ReviewDb db, AccountGroup.UUID groupUuid)
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
name|UUID
name|groupUuid
parameter_list|)
throws|throws
name|OrmException
throws|,
name|NoSuchGroupException
block|{
name|AccountGroup
name|group
init|=
name|getExistingGroup
argument_list|(
name|db
argument_list|,
name|groupUuid
argument_list|)
decl_stmt|;
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
name|group
operator|.
name|getId
argument_list|()
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
comment|/**    * Returns the subgroups of a group.    *    *<p>This parent group must be an internal group whereas the subgroups can either be internal or    * external groups.    *    *<p><strong>Note</strong>: This method doesn't check whether the subgroups exist!    *    * @param db the {@code ReviewDb} instance to use for lookups    * @param groupUuid the UUID of the parent group    * @return a stream of the UUIDs of the subgroups    * @throws OrmException if an error occurs while reading from ReviewDb    * @throws NoSuchGroupException if the specified parent group doesn't exist    */
DECL|method|getSubgroups (ReviewDb db, AccountGroup.UUID groupUuid)
specifier|public
name|Stream
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|getSubgroups
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
throws|,
name|NoSuchGroupException
block|{
name|AccountGroup
name|group
init|=
name|getExistingGroup
argument_list|(
name|db
argument_list|,
name|groupUuid
argument_list|)
decl_stmt|;
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
name|group
operator|.
name|getId
argument_list|()
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
operator|.
name|distinct
argument_list|()
return|;
block|}
comment|/**    * Returns the groups of which the specified account is a member.    *    *<p><strong>Note</strong>: This method returns an empty stream if the account doesn't exist.    * This method doesn't check whether the groups exist.    *    * @param db the {@code ReviewDb} instance to use for lookups    * @param accountId the ID of the account    * @return a stream of the IDs of the groups of which the account is a member    * @throws OrmException if an error occurs while reading from ReviewDb    */
DECL|method|getGroupsWithMember (ReviewDb db, Account.Id accountId)
specifier|public
name|Stream
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
name|getGroupsWithMember
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|Account
operator|.
name|Id
name|accountId
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
name|byAccount
argument_list|(
name|accountId
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
name|getAccountGroupId
argument_list|)
return|;
block|}
comment|/**    * Returns the parent groups of the specified (sub)group.    *    *<p>The subgroup may either be an internal or an external group whereas the returned parent    * groups represent only internal groups.    *    *<p><strong>Note</strong>: This method returns an empty stream if the specified group doesn't    * exist. This method doesn't check whether the parent groups exist.    *    * @param db the {@code ReviewDb} instance to use for lookups    * @param subgroupUuid the UUID of the subgroup    * @return a stream of the IDs of the parent groups    * @throws OrmException if an error occurs while reading from ReviewDb    */
DECL|method|getParentGroups (ReviewDb db, AccountGroup.UUID subgroupUuid)
specifier|public
name|Stream
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
name|getParentGroups
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|AccountGroup
operator|.
name|UUID
name|subgroupUuid
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
name|byIncludeUUID
argument_list|(
name|subgroupUuid
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
name|getGroupId
argument_list|)
return|;
block|}
comment|/**    * Returns all known external groups. External groups are 'known' when they are specified as a    * subgroup of an internal group.    *    * @param db the {@code ReviewDb} instance to use for lookups    * @return a stream of the UUIDs of the known external groups    * @throws OrmException if an error occurs while reading from ReviewDb    */
DECL|method|getExternalGroups (ReviewDb db)
specifier|public
name|Stream
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|getExternalGroups
parameter_list|(
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
block|{
return|return
name|Streams
operator|.
name|stream
argument_list|(
name|db
operator|.
name|accountGroupById
argument_list|()
operator|.
name|all
argument_list|()
argument_list|)
operator|.
name|map
argument_list|(
name|AccountGroupById
operator|::
name|getIncludeUUID
argument_list|)
operator|.
name|distinct
argument_list|()
operator|.
name|filter
argument_list|(
name|groupUuid
lambda|->
operator|!
name|AccountGroup
operator|.
name|isInternalGroup
argument_list|(
name|groupUuid
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

