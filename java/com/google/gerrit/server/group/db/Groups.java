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
DECL|package|com.google.gerrit.server.group.db
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
operator|.
name|db
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
name|collect
operator|.
name|ImmutableSet
operator|.
name|toImmutableSet
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
name|ImmutableSet
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
name|AccountGroupByIdAud
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
name|AccountGroupMemberAudit
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
name|config
operator|.
name|AllUsersName
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
name|group
operator|.
name|InternalGroup
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
name|notedb
operator|.
name|GroupsMigration
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
name|io
operator|.
name|IOException
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
name|lib
operator|.
name|Repository
import|;
end_import

begin_comment
comment|/**  * A database accessor for read calls related to groups.  *  *<p>All calls which read group related details from the database (either ReviewDb or NoteDb) are  * gathered here. Other classes should always use this class instead of accessing the database  * directly. There are a few exceptions though: schema classes, wrapper classes, and classes  * executed during init. The latter ones should use {@code GroupsOnInit} instead.  *  *<p>Most callers should not need to read groups directly from the database; they should use the  * {@link com.google.gerrit.server.account.GroupCache GroupCache} instead.  *  *<p>If not explicitly stated, all methods of this class refer to<em>internal</em> groups.  */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|Groups
specifier|public
class|class
name|Groups
block|{
DECL|field|groupsMigration
specifier|private
specifier|final
name|GroupsMigration
name|groupsMigration
decl_stmt|;
DECL|field|repoManager
specifier|private
specifier|final
name|GitRepositoryManager
name|repoManager
decl_stmt|;
DECL|field|allUsersName
specifier|private
specifier|final
name|AllUsersName
name|allUsersName
decl_stmt|;
DECL|field|auditLogReader
specifier|private
specifier|final
name|AuditLogReader
name|auditLogReader
decl_stmt|;
annotation|@
name|Inject
DECL|method|Groups ( GroupsMigration groupsMigration, GitRepositoryManager repoManager, AllUsersName allUsersName, AuditLogReader auditLogReader)
specifier|public
name|Groups
parameter_list|(
name|GroupsMigration
name|groupsMigration
parameter_list|,
name|GitRepositoryManager
name|repoManager
parameter_list|,
name|AllUsersName
name|allUsersName
parameter_list|,
name|AuditLogReader
name|auditLogReader
parameter_list|)
block|{
name|this
operator|.
name|groupsMigration
operator|=
name|groupsMigration
expr_stmt|;
name|this
operator|.
name|repoManager
operator|=
name|repoManager
expr_stmt|;
name|this
operator|.
name|allUsersName
operator|=
name|allUsersName
expr_stmt|;
name|this
operator|.
name|auditLogReader
operator|=
name|auditLogReader
expr_stmt|;
block|}
comment|/**    * Returns the {@code AccountGroup} for the specified ID if it exists.    *    * @param db the {@code ReviewDb} instance to use for lookups    * @param groupId the ID of the group    * @return the found {@code AccountGroup} if it exists, or else an empty {@code Optional}    * @throws OrmException if the group couldn't be retrieved from ReviewDb    */
DECL|method|getGroupFromReviewDb (ReviewDb db, AccountGroup.Id groupId)
specifier|public
specifier|static
name|Optional
argument_list|<
name|InternalGroup
argument_list|>
name|getGroupFromReviewDb
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
name|AccountGroup
name|accountGroup
init|=
name|db
operator|.
name|accountGroups
argument_list|()
operator|.
name|get
argument_list|(
name|groupId
argument_list|)
decl_stmt|;
if|if
condition|(
name|accountGroup
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
return|return
name|Optional
operator|.
name|of
argument_list|(
name|asInternalGroup
argument_list|(
name|db
argument_list|,
name|accountGroup
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Returns the {@code InternalGroup} for the specified UUID if it exists.    *    * @param db the {@code ReviewDb} instance to use for lookups    * @param groupUuid the UUID of the group    * @return the found {@code InternalGroup} if it exists, or else an empty {@code Optional}    * @throws OrmDuplicateKeyException if multiple groups are found for the specified UUID    * @throws OrmException if the group couldn't be retrieved from ReviewDb    * @throws IOException if the group couldn't be retrieved from NoteDb    * @throws ConfigInvalidException if the group couldn't be retrieved from NoteDb    */
DECL|method|getGroup (ReviewDb db, AccountGroup.UUID groupUuid)
specifier|public
name|Optional
argument_list|<
name|InternalGroup
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
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
if|if
condition|(
name|groupsMigration
operator|.
name|readFromNoteDb
argument_list|()
condition|)
block|{
try|try
init|(
name|Repository
name|allUsersRepo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|allUsersName
argument_list|)
init|)
block|{
return|return
name|getGroupFromNoteDb
argument_list|(
name|allUsersRepo
argument_list|,
name|groupUuid
argument_list|)
return|;
block|}
block|}
name|Optional
argument_list|<
name|AccountGroup
argument_list|>
name|accountGroup
init|=
name|getGroupFromReviewDb
argument_list|(
name|db
argument_list|,
name|groupUuid
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|accountGroup
operator|.
name|isPresent
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
return|return
name|Optional
operator|.
name|of
argument_list|(
name|asInternalGroup
argument_list|(
name|db
argument_list|,
name|accountGroup
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getGroupFromNoteDb ( Repository allUsersRepository, AccountGroup.UUID groupUuid)
specifier|private
specifier|static
name|Optional
argument_list|<
name|InternalGroup
argument_list|>
name|getGroupFromNoteDb
parameter_list|(
name|Repository
name|allUsersRepository
parameter_list|,
name|AccountGroup
operator|.
name|UUID
name|groupUuid
parameter_list|)
throws|throws
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|GroupConfig
name|groupConfig
init|=
name|GroupConfig
operator|.
name|loadForGroup
argument_list|(
name|allUsersRepository
argument_list|,
name|groupUuid
argument_list|)
decl_stmt|;
name|Optional
argument_list|<
name|InternalGroup
argument_list|>
name|loadedGroup
init|=
name|groupConfig
operator|.
name|getLoadedGroup
argument_list|()
decl_stmt|;
if|if
condition|(
name|loadedGroup
operator|.
name|isPresent
argument_list|()
condition|)
block|{
comment|// Check consistency with group name notes.
name|GroupsNoteDbConsistencyChecker
operator|.
name|ensureConsistentWithGroupNameNotes
argument_list|(
name|allUsersRepository
argument_list|,
name|loadedGroup
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|loadedGroup
return|;
block|}
DECL|method|asInternalGroup (ReviewDb db, AccountGroup accountGroup)
specifier|public
specifier|static
name|InternalGroup
name|asInternalGroup
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|AccountGroup
name|accountGroup
parameter_list|)
throws|throws
name|OrmException
block|{
name|ImmutableSet
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|members
init|=
name|getMembersFromReviewDb
argument_list|(
name|db
argument_list|,
name|accountGroup
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|collect
argument_list|(
name|toImmutableSet
argument_list|()
argument_list|)
decl_stmt|;
name|ImmutableSet
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|subgroups
init|=
name|getSubgroupsFromReviewDb
argument_list|(
name|db
argument_list|,
name|accountGroup
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|collect
argument_list|(
name|toImmutableSet
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|InternalGroup
operator|.
name|create
argument_list|(
name|accountGroup
argument_list|,
name|members
argument_list|,
name|subgroups
argument_list|)
return|;
block|}
comment|/**    * Returns the {@code AccountGroup} for the specified UUID.    *    * @param db the {@code ReviewDb} instance to use for lookups    * @param groupUuid the UUID of the group    * @return the {@code AccountGroup} which has the specified UUID    * @throws OrmDuplicateKeyException if multiple groups are found for the specified UUID    * @throws OrmException if the group couldn't be retrieved from ReviewDb    * @throws NoSuchGroupException if a group with such a UUID doesn't exist    */
DECL|method|getExistingGroupFromReviewDb (ReviewDb db, AccountGroup.UUID groupUuid)
specifier|static
name|AccountGroup
name|getExistingGroupFromReviewDb
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
name|getGroupFromReviewDb
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
comment|/**    * Returns the {@code AccountGroup} for the specified UUID if it exists.    *    * @param db the {@code ReviewDb} instance to use for lookups    * @param groupUuid the UUID of the group    * @return the found {@code AccountGroup} if it exists, or else an empty {@code Optional}    * @throws OrmDuplicateKeyException if multiple groups are found for the specified UUID    * @throws OrmException if the group couldn't be retrieved from ReviewDb    */
DECL|method|getGroupFromReviewDb ( ReviewDb db, AccountGroup.UUID groupUuid)
specifier|private
specifier|static
name|Optional
argument_list|<
name|AccountGroup
argument_list|>
name|getGroupFromReviewDb
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
comment|/**    * Returns {@code GroupReference}s for all internal groups.    *    * @param db the {@code ReviewDb} instance to use for lookups    * @return a stream of the {@code GroupReference}s of all internal groups    * @throws OrmException if an error occurs while reading from ReviewDb    * @throws IOException if an error occurs while reading from NoteDb    * @throws ConfigInvalidException if the data in NoteDb is in an incorrect format    */
DECL|method|getAllGroupReferences (ReviewDb db)
specifier|public
name|Stream
argument_list|<
name|GroupReference
argument_list|>
name|getAllGroupReferences
parameter_list|(
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
if|if
condition|(
name|groupsMigration
operator|.
name|readFromNoteDb
argument_list|()
condition|)
block|{
try|try
init|(
name|Repository
name|allUsersRepo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|allUsersName
argument_list|)
init|)
block|{
return|return
name|GroupNameNotes
operator|.
name|loadAllGroups
argument_list|(
name|allUsersRepo
argument_list|)
operator|.
name|stream
argument_list|()
return|;
block|}
block|}
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
operator|.
name|map
argument_list|(
name|group
lambda|->
operator|new
name|GroupReference
argument_list|(
name|group
operator|.
name|getGroupUUID
argument_list|()
argument_list|,
name|group
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Returns the members (accounts) of a group.    *    *<p><strong>Note</strong>: This method doesn't check whether the accounts exist!    *    * @param db the {@code ReviewDb} instance to use for lookups    * @param groupId the ID of the group    * @return a stream of the IDs of the members    * @throws OrmException if an error occurs while reading from ReviewDb    */
DECL|method|getMembersFromReviewDb (ReviewDb db, AccountGroup.Id groupId)
specifier|public
specifier|static
name|Stream
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|getMembersFromReviewDb
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
comment|/**    * Returns the subgroups of a group.    *    *<p>This parent group must be an internal group whereas the subgroups can either be internal or    * external groups.    *    *<p><strong>Note</strong>: This method doesn't check whether the subgroups exist!    *    * @param db the {@code ReviewDb} instance to use for lookups    * @param groupId the ID of the group    * @return a stream of the UUIDs of the subgroups    * @throws OrmException if an error occurs while reading from ReviewDb    */
DECL|method|getSubgroupsFromReviewDb ( ReviewDb db, AccountGroup.Id groupId)
specifier|public
specifier|static
name|Stream
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|getSubgroupsFromReviewDb
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
operator|.
name|distinct
argument_list|()
return|;
block|}
comment|/**    * Returns the groups of which the specified account is a member.    *    *<p><strong>Note</strong>: This method returns an empty stream if the account doesn't exist.    * This method doesn't check whether the groups exist.    *    * @param db the {@code ReviewDb} instance to use for lookups    * @param accountId the ID of the account    * @return a stream of the IDs of the groups of which the account is a member    * @throws OrmException if an error occurs while reading from ReviewDb    */
DECL|method|getGroupsWithMemberFromReviewDb ( ReviewDb db, Account.Id accountId)
specifier|public
specifier|static
name|Stream
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
name|getGroupsWithMemberFromReviewDb
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
DECL|method|getParentGroupsFromReviewDb ( ReviewDb db, AccountGroup.UUID subgroupUuid)
specifier|public
specifier|static
name|Stream
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
name|getParentGroupsFromReviewDb
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
comment|/**    * Returns all known external groups. External groups are 'known' when they are specified as a    * subgroup of an internal group.    *    * @param db the {@code ReviewDb} instance to use for lookups    * @return a stream of the UUIDs of the known external groups    * @throws OrmException if an error occurs while reading from ReviewDb    * @throws IOException if an error occurs while reading from NoteDb    * @throws ConfigInvalidException if the data in NoteDb is in an incorrect format    */
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
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
if|if
condition|(
name|groupsMigration
operator|.
name|readFromNoteDb
argument_list|()
condition|)
block|{
try|try
init|(
name|Repository
name|allUsersRepo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|allUsersName
argument_list|)
init|)
block|{
return|return
name|getExternalGroupsFromNoteDb
argument_list|(
name|allUsersRepo
argument_list|)
return|;
block|}
block|}
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
DECL|method|getExternalGroupsFromNoteDb (Repository allUsersRepo)
specifier|private
specifier|static
name|Stream
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|getExternalGroupsFromNoteDb
parameter_list|(
name|Repository
name|allUsersRepo
parameter_list|)
throws|throws
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|ImmutableList
argument_list|<
name|GroupReference
argument_list|>
name|allInternalGroups
init|=
name|GroupNameNotes
operator|.
name|loadAllGroups
argument_list|(
name|allUsersRepo
argument_list|)
decl_stmt|;
name|ImmutableSet
operator|.
name|Builder
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|allSubgroups
init|=
name|ImmutableSet
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|GroupReference
name|internalGroup
range|:
name|allInternalGroups
control|)
block|{
name|Optional
argument_list|<
name|InternalGroup
argument_list|>
name|group
init|=
name|getGroupFromNoteDb
argument_list|(
name|allUsersRepo
argument_list|,
name|internalGroup
operator|.
name|getUUID
argument_list|()
argument_list|)
decl_stmt|;
name|group
operator|.
name|map
argument_list|(
name|InternalGroup
operator|::
name|getSubgroups
argument_list|)
operator|.
name|ifPresent
argument_list|(
name|allSubgroups
operator|::
name|addAll
argument_list|)
expr_stmt|;
block|}
return|return
name|allSubgroups
operator|.
name|build
argument_list|()
operator|.
name|stream
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
comment|/**    * Returns the membership audit records for a given group.    *    * @param db the {@code ReviewDb} instance to use for lookups    * @param repo All-Users repository.    * @param groupUuid the UUID of the group    * @return the audit records, in arbitrary order; empty if the group does not exist    * @throws OrmException if an error occurs while reading from ReviewDb    * @throws IOException if an error occurs while reading from NoteDb    * @throws ConfigInvalidException if the group couldn't be retrieved from NoteDb    */
DECL|method|getMembersAudit ( ReviewDb db, Repository repo, AccountGroup.UUID groupUuid)
specifier|public
name|List
argument_list|<
name|AccountGroupMemberAudit
argument_list|>
name|getMembersAudit
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|Repository
name|repo
parameter_list|,
name|AccountGroup
operator|.
name|UUID
name|groupUuid
parameter_list|)
throws|throws
name|OrmException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
if|if
condition|(
name|groupsMigration
operator|.
name|readFromNoteDb
argument_list|()
condition|)
block|{
return|return
name|auditLogReader
operator|.
name|getMembersAudit
argument_list|(
name|repo
argument_list|,
name|groupUuid
argument_list|)
return|;
block|}
name|Optional
argument_list|<
name|AccountGroup
argument_list|>
name|group
init|=
name|getGroupFromReviewDb
argument_list|(
name|db
argument_list|,
name|groupUuid
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|group
operator|.
name|isPresent
argument_list|()
condition|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
return|return
name|db
operator|.
name|accountGroupMembersAudit
argument_list|()
operator|.
name|byGroup
argument_list|(
name|group
operator|.
name|get
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|toList
argument_list|()
return|;
block|}
comment|/**    * Returns the subgroup audit records for a given group.    *    * @param db the {@code ReviewDb} instance to use for lookups    * @param repo All-Users repository.    * @param groupUuid the UUID of the group    * @return the audit records, in arbitrary order; empty if the group does not exist    * @throws OrmException if an error occurs while reading from ReviewDb    * @throws IOException if an error occurs while reading from NoteDb    * @throws ConfigInvalidException if the group couldn't be retrieved from NoteDb    */
DECL|method|getSubgroupsAudit ( ReviewDb db, Repository repo, AccountGroup.UUID groupUuid)
specifier|public
name|List
argument_list|<
name|AccountGroupByIdAud
argument_list|>
name|getSubgroupsAudit
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|Repository
name|repo
parameter_list|,
name|AccountGroup
operator|.
name|UUID
name|groupUuid
parameter_list|)
throws|throws
name|OrmException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
if|if
condition|(
name|groupsMigration
operator|.
name|readFromNoteDb
argument_list|()
condition|)
block|{
return|return
name|auditLogReader
operator|.
name|getSubgroupsAudit
argument_list|(
name|repo
argument_list|,
name|groupUuid
argument_list|)
return|;
block|}
name|Optional
argument_list|<
name|AccountGroup
argument_list|>
name|group
init|=
name|getGroupFromReviewDb
argument_list|(
name|db
argument_list|,
name|groupUuid
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|group
operator|.
name|isPresent
argument_list|()
condition|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
return|return
name|db
operator|.
name|accountGroupByIdAud
argument_list|()
operator|.
name|byGroup
argument_list|(
name|group
operator|.
name|get
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|toList
argument_list|()
return|;
block|}
block|}
end_class

end_unit

