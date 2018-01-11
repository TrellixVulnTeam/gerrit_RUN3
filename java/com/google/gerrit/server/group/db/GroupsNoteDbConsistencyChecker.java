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
name|gerrit
operator|.
name|extensions
operator|.
name|api
operator|.
name|config
operator|.
name|ConsistencyCheckInfo
operator|.
name|ConsistencyProblemInfo
operator|.
name|error
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|extensions
operator|.
name|api
operator|.
name|config
operator|.
name|ConsistencyCheckInfo
operator|.
name|ConsistencyProblemInfo
operator|.
name|warning
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
name|collect
operator|.
name|BiMap
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
name|HashBiMap
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
name|extensions
operator|.
name|api
operator|.
name|config
operator|.
name|ConsistencyCheckInfo
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
name|extensions
operator|.
name|api
operator|.
name|config
operator|.
name|ConsistencyCheckInfo
operator|.
name|ConsistencyProblemInfo
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
name|RefNames
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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|Map
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
name|javax
operator|.
name|inject
operator|.
name|Singleton
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
name|ObjectId
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
name|ObjectLoader
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
name|Ref
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
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|notes
operator|.
name|Note
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
name|notes
operator|.
name|NoteMap
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
name|revwalk
operator|.
name|RevCommit
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
name|revwalk
operator|.
name|RevWalk
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

begin_comment
comment|/** Check the referential integrity of NoteDb group storage. */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|GroupsNoteDbConsistencyChecker
specifier|public
class|class
name|GroupsNoteDbConsistencyChecker
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
name|GroupsNoteDbConsistencyChecker
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * The result of a consistency check. The UUID map is only non-null if no problems were detected.    */
DECL|class|Result
specifier|public
specifier|static
class|class
name|Result
block|{
DECL|field|problems
specifier|public
name|List
argument_list|<
name|ConsistencyProblemInfo
argument_list|>
name|problems
decl_stmt|;
DECL|field|uuidToGroupMap
annotation|@
name|Nullable
specifier|public
name|Map
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|,
name|InternalGroup
argument_list|>
name|uuidToGroupMap
decl_stmt|;
block|}
comment|/** Checks for problems with the given All-Users repo. */
DECL|method|check (Repository repo)
specifier|public
name|Result
name|check
parameter_list|(
name|Repository
name|repo
parameter_list|)
throws|throws
name|IOException
block|{
name|Result
name|r
init|=
name|doCheck
argument_list|(
name|repo
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|r
operator|.
name|problems
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|r
operator|.
name|uuidToGroupMap
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
DECL|method|doCheck (Repository repo)
specifier|private
name|Result
name|doCheck
parameter_list|(
name|Repository
name|repo
parameter_list|)
throws|throws
name|IOException
block|{
name|Result
name|result
init|=
operator|new
name|Result
argument_list|()
decl_stmt|;
name|result
operator|.
name|problems
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|result
operator|.
name|uuidToGroupMap
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|BiMap
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|,
name|String
argument_list|>
name|uuidNameBiMap
init|=
name|HashBiMap
operator|.
name|create
argument_list|()
decl_stmt|;
comment|// Get all refs in an attempt to avoid seeing half committed group updates.
name|Map
argument_list|<
name|String
argument_list|,
name|Ref
argument_list|>
name|refs
init|=
name|repo
operator|.
name|getAllRefs
argument_list|()
decl_stmt|;
name|readGroups
argument_list|(
name|repo
argument_list|,
name|refs
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|readGroupNames
argument_list|(
name|repo
argument_list|,
name|refs
argument_list|,
name|result
argument_list|,
name|uuidNameBiMap
argument_list|)
expr_stmt|;
comment|// The sequential IDs are not keys in NoteDb, so no need to check them.
if|if
condition|(
operator|!
name|result
operator|.
name|problems
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|result
return|;
block|}
comment|// Continue checking if we could read data without problems.
name|result
operator|.
name|problems
operator|.
name|addAll
argument_list|(
name|checkGlobalConsistency
argument_list|(
name|result
operator|.
name|uuidToGroupMap
argument_list|,
name|uuidNameBiMap
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|readGroups (Repository repo, Map<String, Ref> refs, Result result)
specifier|private
name|void
name|readGroups
parameter_list|(
name|Repository
name|repo
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Ref
argument_list|>
name|refs
parameter_list|,
name|Result
name|result
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Ref
argument_list|>
name|entry
range|:
name|refs
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|startsWith
argument_list|(
name|RefNames
operator|.
name|REFS_GROUPS
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|AccountGroup
operator|.
name|UUID
name|uuid
init|=
name|AccountGroup
operator|.
name|UUID
operator|.
name|fromRef
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|uuid
operator|==
literal|null
condition|)
block|{
name|result
operator|.
name|problems
operator|.
name|add
argument_list|(
name|error
argument_list|(
literal|"null UUID from %s"
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
try|try
block|{
name|GroupConfig
name|cfg
init|=
name|GroupConfig
operator|.
name|loadForGroupSnapshot
argument_list|(
name|repo
argument_list|,
name|uuid
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getObjectId
argument_list|()
argument_list|)
decl_stmt|;
name|result
operator|.
name|uuidToGroupMap
operator|.
name|put
argument_list|(
name|uuid
argument_list|,
name|cfg
operator|.
name|getLoadedGroup
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConfigInvalidException
name|e
parameter_list|)
block|{
name|result
operator|.
name|problems
operator|.
name|add
argument_list|(
name|error
argument_list|(
literal|"group %s does not parse: %s"
argument_list|,
name|uuid
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|readGroupNames ( Repository repo, Map<String, Ref> refs, Result result, BiMap<AccountGroup.UUID, String> uuidNameBiMap)
specifier|private
name|void
name|readGroupNames
parameter_list|(
name|Repository
name|repo
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Ref
argument_list|>
name|refs
parameter_list|,
name|Result
name|result
parameter_list|,
name|BiMap
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|,
name|String
argument_list|>
name|uuidNameBiMap
parameter_list|)
throws|throws
name|IOException
block|{
name|Ref
name|ref
init|=
name|refs
operator|.
name|get
argument_list|(
name|RefNames
operator|.
name|REFS_GROUPNAMES
argument_list|)
decl_stmt|;
if|if
condition|(
name|ref
operator|==
literal|null
condition|)
block|{
name|String
name|msg
init|=
name|String
operator|.
name|format
argument_list|(
literal|"ref %s does not exist"
argument_list|,
name|RefNames
operator|.
name|REFS_GROUPNAMES
argument_list|)
decl_stmt|;
name|result
operator|.
name|problems
operator|.
name|add
argument_list|(
name|error
argument_list|(
name|msg
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
try|try
init|(
name|RevWalk
name|rw
init|=
operator|new
name|RevWalk
argument_list|(
name|repo
argument_list|)
init|)
block|{
name|RevCommit
name|c
init|=
name|rw
operator|.
name|parseCommit
argument_list|(
name|ref
operator|.
name|getObjectId
argument_list|()
argument_list|)
decl_stmt|;
name|NoteMap
name|nm
init|=
name|NoteMap
operator|.
name|read
argument_list|(
name|rw
operator|.
name|getObjectReader
argument_list|()
argument_list|,
name|c
argument_list|)
decl_stmt|;
for|for
control|(
name|Note
name|note
range|:
name|nm
control|)
block|{
name|ObjectLoader
name|ld
init|=
name|rw
operator|.
name|getObjectReader
argument_list|()
operator|.
name|open
argument_list|(
name|note
operator|.
name|getData
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
name|data
init|=
name|ld
operator|.
name|getCachedBytes
argument_list|()
decl_stmt|;
name|GroupReference
name|gRef
decl_stmt|;
try|try
block|{
name|gRef
operator|=
name|GroupNameNotes
operator|.
name|getFromNoteData
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConfigInvalidException
name|e
parameter_list|)
block|{
name|result
operator|.
name|problems
operator|.
name|add
argument_list|(
name|error
argument_list|(
literal|"notename entry %s: %s does not parse: %s"
argument_list|,
name|note
argument_list|,
operator|new
name|String
argument_list|(
name|data
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|ObjectId
name|nameKey
init|=
name|GroupNameNotes
operator|.
name|getNoteKey
argument_list|(
operator|new
name|AccountGroup
operator|.
name|NameKey
argument_list|(
name|gRef
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Objects
operator|.
name|equals
argument_list|(
name|nameKey
argument_list|,
name|note
argument_list|)
condition|)
block|{
name|result
operator|.
name|problems
operator|.
name|add
argument_list|(
name|error
argument_list|(
literal|"notename entry %s does not match name %s"
argument_list|,
name|note
argument_list|,
name|gRef
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// We trust SHA1 to have no collisions, so no need to check uniqueness of name.
name|uuidNameBiMap
operator|.
name|put
argument_list|(
name|gRef
operator|.
name|getUUID
argument_list|()
argument_list|,
name|gRef
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Check invariants of the group refs with the group name refs. */
DECL|method|checkGlobalConsistency ( Map<AccountGroup.UUID, InternalGroup> uuidToGroupMap, BiMap<AccountGroup.UUID, String> uuidNameBiMap)
specifier|private
name|List
argument_list|<
name|ConsistencyProblemInfo
argument_list|>
name|checkGlobalConsistency
parameter_list|(
name|Map
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|,
name|InternalGroup
argument_list|>
name|uuidToGroupMap
parameter_list|,
name|BiMap
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|,
name|String
argument_list|>
name|uuidNameBiMap
parameter_list|)
block|{
name|List
argument_list|<
name|ConsistencyProblemInfo
argument_list|>
name|problems
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Check consistency between the data coming from different refs.
for|for
control|(
name|AccountGroup
operator|.
name|UUID
name|uuid
range|:
name|uuidToGroupMap
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|uuidNameBiMap
operator|.
name|containsKey
argument_list|(
name|uuid
argument_list|)
condition|)
block|{
name|problems
operator|.
name|add
argument_list|(
name|error
argument_list|(
literal|"group %s has no entry in name map"
argument_list|,
name|uuid
argument_list|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|String
name|noteName
init|=
name|uuidNameBiMap
operator|.
name|get
argument_list|(
name|uuid
argument_list|)
decl_stmt|;
name|String
name|groupRefName
init|=
name|uuidToGroupMap
operator|.
name|get
argument_list|(
name|uuid
argument_list|)
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|Objects
operator|.
name|equals
argument_list|(
name|noteName
argument_list|,
name|groupRefName
argument_list|)
condition|)
block|{
name|problems
operator|.
name|add
argument_list|(
name|error
argument_list|(
literal|"inconsistent name for group %s (name map %s vs. group ref %s)"
argument_list|,
name|uuid
argument_list|,
name|noteName
argument_list|,
name|groupRefName
argument_list|)
argument_list|)
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
name|uuidNameBiMap
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|uuidToGroupMap
operator|.
name|containsKey
argument_list|(
name|uuid
argument_list|)
condition|)
block|{
name|problems
operator|.
name|add
argument_list|(
name|error
argument_list|(
literal|"name map has entry (%s, %s), entry missing as group ref"
argument_list|,
name|uuid
argument_list|,
name|uuidNameBiMap
operator|.
name|get
argument_list|(
name|uuid
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|problems
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// Check ids.
name|Map
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|,
name|InternalGroup
argument_list|>
name|groupById
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|InternalGroup
name|g
range|:
name|uuidToGroupMap
operator|.
name|values
argument_list|()
control|)
block|{
name|InternalGroup
name|before
init|=
name|groupById
operator|.
name|get
argument_list|(
name|g
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|before
operator|!=
literal|null
condition|)
block|{
name|problems
operator|.
name|add
argument_list|(
name|error
argument_list|(
literal|"shared group id %s for %s (%s) and %s (%s)"
argument_list|,
name|g
operator|.
name|getId
argument_list|()
argument_list|,
name|before
operator|.
name|getName
argument_list|()
argument_list|,
name|before
operator|.
name|getGroupUUID
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
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|groupById
operator|.
name|put
argument_list|(
name|g
operator|.
name|getId
argument_list|()
argument_list|,
name|g
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|problems
return|;
block|}
DECL|method|ensureConsistentWithGroupNameNotes ( Repository allUsersRepo, InternalGroup group)
specifier|public
specifier|static
name|void
name|ensureConsistentWithGroupNameNotes
parameter_list|(
name|Repository
name|allUsersRepo
parameter_list|,
name|InternalGroup
name|group
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|ConsistencyCheckInfo
operator|.
name|ConsistencyProblemInfo
argument_list|>
name|problems
init|=
name|GroupsNoteDbConsistencyChecker
operator|.
name|checkWithGroupNameNotes
argument_list|(
name|allUsersRepo
argument_list|,
name|group
operator|.
name|getNameKey
argument_list|()
argument_list|,
name|group
operator|.
name|getGroupUUID
argument_list|()
argument_list|)
decl_stmt|;
name|problems
operator|.
name|forEach
argument_list|(
name|GroupsNoteDbConsistencyChecker
operator|::
name|logConsistencyProblem
argument_list|)
expr_stmt|;
block|}
comment|/**    * Check group 'uuid' and 'name' read from 'group.config' with group name notes.    *    * @param allUsersRepo 'All-Users' repository.    * @param groupName the name of the group to be checked.    * @param groupUUID the {@code AccountGroup.UUID} of the group to be checked.    * @return a list of {@code ConsistencyProblemInfo} containing the problem details.    */
annotation|@
name|VisibleForTesting
DECL|method|checkWithGroupNameNotes ( Repository allUsersRepo, AccountGroup.NameKey groupName, AccountGroup.UUID groupUUID)
specifier|static
name|List
argument_list|<
name|ConsistencyProblemInfo
argument_list|>
name|checkWithGroupNameNotes
parameter_list|(
name|Repository
name|allUsersRepo
parameter_list|,
name|AccountGroup
operator|.
name|NameKey
name|groupName
parameter_list|,
name|AccountGroup
operator|.
name|UUID
name|groupUUID
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|Optional
argument_list|<
name|GroupReference
argument_list|>
name|groupRef
init|=
name|GroupNameNotes
operator|.
name|loadGroup
argument_list|(
name|allUsersRepo
argument_list|,
name|groupName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|groupRef
operator|.
name|isPresent
argument_list|()
condition|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|warning
argument_list|(
literal|"Group with name '%s' doesn't exist in the list of all names"
argument_list|,
name|groupName
argument_list|)
argument_list|)
return|;
block|}
name|AccountGroup
operator|.
name|UUID
name|uuid
init|=
name|groupRef
operator|.
name|get
argument_list|()
operator|.
name|getUUID
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ConsistencyProblemInfo
argument_list|>
name|problems
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|Objects
operator|.
name|equals
argument_list|(
name|groupUUID
argument_list|,
name|uuid
argument_list|)
condition|)
block|{
name|problems
operator|.
name|add
argument_list|(
name|warning
argument_list|(
literal|"group with name '%s' has UUID '%s' in 'group.config' but '%s' in group name notes"
argument_list|,
name|groupName
argument_list|,
name|groupUUID
argument_list|,
name|uuid
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|name
init|=
name|groupName
operator|.
name|get
argument_list|()
decl_stmt|;
name|String
name|actualName
init|=
name|groupRef
operator|.
name|get
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|Objects
operator|.
name|equals
argument_list|(
name|name
argument_list|,
name|actualName
argument_list|)
condition|)
block|{
name|problems
operator|.
name|add
argument_list|(
name|warning
argument_list|(
literal|"group note of name '%s' claims to represent name of '%s'"
argument_list|,
name|name
argument_list|,
name|actualName
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|problems
return|;
block|}
catch|catch
parameter_list|(
name|ConfigInvalidException
name|e
parameter_list|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|warning
argument_list|(
literal|"fail to check consistency with group name notes: %s"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|method|logConsistencyProblemAsWarning (String fmt, Object... args)
specifier|public
specifier|static
name|void
name|logConsistencyProblemAsWarning
parameter_list|(
name|String
name|fmt
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
name|logConsistencyProblem
argument_list|(
name|warning
argument_list|(
name|fmt
argument_list|,
name|args
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|logConsistencyProblem (ConsistencyProblemInfo p)
specifier|public
specifier|static
name|void
name|logConsistencyProblem
parameter_list|(
name|ConsistencyProblemInfo
name|p
parameter_list|)
block|{
if|if
condition|(
name|p
operator|.
name|status
operator|==
name|ConsistencyProblemInfo
operator|.
name|Status
operator|.
name|WARNING
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
name|p
operator|.
name|message
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|error
argument_list|(
name|p
operator|.
name|message
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|logFailToLoadFromGroupRefAsWarning (AccountGroup.UUID uuid)
specifier|public
specifier|static
name|void
name|logFailToLoadFromGroupRefAsWarning
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|uuid
parameter_list|)
block|{
name|logConsistencyProblem
argument_list|(
name|warning
argument_list|(
literal|"Group with UUID %s from group name notes failed to load from group ref"
argument_list|,
name|uuid
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

