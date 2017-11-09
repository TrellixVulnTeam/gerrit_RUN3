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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Constants
operator|.
name|OBJ_BLOB
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
name|hash
operator|.
name|Hashing
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
name|git
operator|.
name|VersionedMetaData
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
name|CommitBuilder
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
name|ObjectInserter
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
name|ObjectReader
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

begin_comment
comment|// TODO(aliceks): Add Javadoc descriptions.
end_comment

begin_class
DECL|class|GroupNameNotes
specifier|public
class|class
name|GroupNameNotes
extends|extends
name|VersionedMetaData
block|{
DECL|field|SECTION_NAME
specifier|private
specifier|static
specifier|final
name|String
name|SECTION_NAME
init|=
literal|"group"
decl_stmt|;
DECL|field|UUID_PARAM
specifier|private
specifier|static
specifier|final
name|String
name|UUID_PARAM
init|=
literal|"uuid"
decl_stmt|;
DECL|field|NAME_PARAM
specifier|private
specifier|static
specifier|final
name|String
name|NAME_PARAM
init|=
literal|"name"
decl_stmt|;
DECL|field|groupUuid
specifier|private
specifier|final
name|AccountGroup
operator|.
name|UUID
name|groupUuid
decl_stmt|;
DECL|field|oldGroupName
specifier|private
specifier|final
name|Optional
argument_list|<
name|AccountGroup
operator|.
name|NameKey
argument_list|>
name|oldGroupName
decl_stmt|;
DECL|field|newGroupName
specifier|private
specifier|final
name|Optional
argument_list|<
name|AccountGroup
operator|.
name|NameKey
argument_list|>
name|newGroupName
decl_stmt|;
DECL|field|nameConflicting
specifier|private
name|boolean
name|nameConflicting
decl_stmt|;
DECL|method|GroupNameNotes ( AccountGroup.UUID groupUuid, @Nullable AccountGroup.NameKey oldGroupName, @Nullable AccountGroup.NameKey newGroupName)
specifier|private
name|GroupNameNotes
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|groupUuid
parameter_list|,
annotation|@
name|Nullable
name|AccountGroup
operator|.
name|NameKey
name|oldGroupName
parameter_list|,
annotation|@
name|Nullable
name|AccountGroup
operator|.
name|NameKey
name|newGroupName
parameter_list|)
block|{
name|this
operator|.
name|groupUuid
operator|=
name|checkNotNull
argument_list|(
name|groupUuid
argument_list|)
expr_stmt|;
if|if
condition|(
name|Objects
operator|.
name|equals
argument_list|(
name|oldGroupName
argument_list|,
name|newGroupName
argument_list|)
condition|)
block|{
name|this
operator|.
name|oldGroupName
operator|=
name|Optional
operator|.
name|empty
argument_list|()
expr_stmt|;
name|this
operator|.
name|newGroupName
operator|=
name|Optional
operator|.
name|empty
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|oldGroupName
operator|=
name|Optional
operator|.
name|ofNullable
argument_list|(
name|oldGroupName
argument_list|)
expr_stmt|;
name|this
operator|.
name|newGroupName
operator|=
name|Optional
operator|.
name|ofNullable
argument_list|(
name|newGroupName
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|loadForRename ( Repository repository, AccountGroup.UUID groupUuid, AccountGroup.NameKey oldName, AccountGroup.NameKey newName)
specifier|public
specifier|static
name|GroupNameNotes
name|loadForRename
parameter_list|(
name|Repository
name|repository
parameter_list|,
name|AccountGroup
operator|.
name|UUID
name|groupUuid
parameter_list|,
name|AccountGroup
operator|.
name|NameKey
name|oldName
parameter_list|,
name|AccountGroup
operator|.
name|NameKey
name|newName
parameter_list|)
throws|throws
name|IOException
throws|,
name|ConfigInvalidException
throws|,
name|OrmDuplicateKeyException
block|{
name|checkNotNull
argument_list|(
name|oldName
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|newName
argument_list|)
expr_stmt|;
name|GroupNameNotes
name|groupNameNotes
init|=
operator|new
name|GroupNameNotes
argument_list|(
name|groupUuid
argument_list|,
name|oldName
argument_list|,
name|newName
argument_list|)
decl_stmt|;
name|groupNameNotes
operator|.
name|load
argument_list|(
name|repository
argument_list|)
expr_stmt|;
name|groupNameNotes
operator|.
name|ensureNewNameIsNotUsed
argument_list|()
expr_stmt|;
return|return
name|groupNameNotes
return|;
block|}
DECL|method|loadForNewGroup ( Repository repository, AccountGroup.UUID groupUuid, AccountGroup.NameKey groupName)
specifier|public
specifier|static
name|GroupNameNotes
name|loadForNewGroup
parameter_list|(
name|Repository
name|repository
parameter_list|,
name|AccountGroup
operator|.
name|UUID
name|groupUuid
parameter_list|,
name|AccountGroup
operator|.
name|NameKey
name|groupName
parameter_list|)
throws|throws
name|IOException
throws|,
name|ConfigInvalidException
throws|,
name|OrmDuplicateKeyException
block|{
name|checkNotNull
argument_list|(
name|groupName
argument_list|)
expr_stmt|;
name|GroupNameNotes
name|groupNameNotes
init|=
operator|new
name|GroupNameNotes
argument_list|(
name|groupUuid
argument_list|,
literal|null
argument_list|,
name|groupName
argument_list|)
decl_stmt|;
name|groupNameNotes
operator|.
name|load
argument_list|(
name|repository
argument_list|)
expr_stmt|;
name|groupNameNotes
operator|.
name|ensureNewNameIsNotUsed
argument_list|()
expr_stmt|;
return|return
name|groupNameNotes
return|;
block|}
DECL|method|loadAllGroupReferences (Repository repository)
specifier|public
specifier|static
name|ImmutableSet
argument_list|<
name|GroupReference
argument_list|>
name|loadAllGroupReferences
parameter_list|(
name|Repository
name|repository
parameter_list|)
throws|throws
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|Ref
name|ref
init|=
name|repository
operator|.
name|exactRef
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
return|return
name|ImmutableSet
operator|.
name|of
argument_list|()
return|;
block|}
try|try
init|(
name|RevWalk
name|revWalk
init|=
operator|new
name|RevWalk
argument_list|(
name|repository
argument_list|)
init|;
name|ObjectReader
name|reader
operator|=
name|revWalk
operator|.
name|getObjectReader
argument_list|()
init|)
block|{
name|RevCommit
name|notesCommit
init|=
name|revWalk
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
name|noteMap
init|=
name|NoteMap
operator|.
name|read
argument_list|(
name|reader
argument_list|,
name|notesCommit
argument_list|)
decl_stmt|;
name|ImmutableSet
operator|.
name|Builder
argument_list|<
name|GroupReference
argument_list|>
name|groupReferences
init|=
name|ImmutableSet
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|Note
name|note
range|:
name|noteMap
control|)
block|{
name|GroupReference
name|groupReference
init|=
name|getGroupReference
argument_list|(
name|reader
argument_list|,
name|note
operator|.
name|getData
argument_list|()
argument_list|)
decl_stmt|;
name|groupReferences
operator|.
name|add
argument_list|(
name|groupReference
argument_list|)
expr_stmt|;
block|}
return|return
name|groupReferences
operator|.
name|build
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getRefName ()
specifier|protected
name|String
name|getRefName
parameter_list|()
block|{
return|return
name|RefNames
operator|.
name|REFS_GROUPNAMES
return|;
block|}
annotation|@
name|Override
DECL|method|onLoad ()
specifier|protected
name|void
name|onLoad
parameter_list|()
throws|throws
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|nameConflicting
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|revision
operator|!=
literal|null
condition|)
block|{
name|NoteMap
name|noteMap
init|=
name|NoteMap
operator|.
name|read
argument_list|(
name|reader
argument_list|,
name|revision
argument_list|)
decl_stmt|;
if|if
condition|(
name|newGroupName
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|ObjectId
name|newNameId
init|=
name|getNoteKey
argument_list|(
name|newGroupName
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
name|nameConflicting
operator|=
name|noteMap
operator|.
name|contains
argument_list|(
name|newNameId
argument_list|)
expr_stmt|;
block|}
name|ensureOldNameIsPresent
argument_list|(
name|noteMap
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|ensureOldNameIsPresent (NoteMap noteMap)
specifier|private
name|void
name|ensureOldNameIsPresent
parameter_list|(
name|NoteMap
name|noteMap
parameter_list|)
throws|throws
name|IOException
throws|,
name|ConfigInvalidException
block|{
if|if
condition|(
name|oldGroupName
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|AccountGroup
operator|.
name|NameKey
name|oldName
init|=
name|oldGroupName
operator|.
name|get
argument_list|()
decl_stmt|;
name|ObjectId
name|noteKey
init|=
name|getNoteKey
argument_list|(
name|oldName
argument_list|)
decl_stmt|;
name|ObjectId
name|noteDataBlobId
init|=
name|noteMap
operator|.
name|get
argument_list|(
name|noteKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|noteDataBlobId
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ConfigInvalidException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Group name '%s' doesn't exist in the list of all names"
argument_list|,
name|oldName
argument_list|)
argument_list|)
throw|;
block|}
name|GroupReference
name|group
init|=
name|getGroupReference
argument_list|(
name|reader
argument_list|,
name|noteDataBlobId
argument_list|)
decl_stmt|;
name|AccountGroup
operator|.
name|UUID
name|foundUuid
init|=
name|group
operator|.
name|getUUID
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|Objects
operator|.
name|equals
argument_list|(
name|groupUuid
argument_list|,
name|foundUuid
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ConfigInvalidException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Name '%s' points to UUID '%s' and not to '%s'"
argument_list|,
name|oldName
argument_list|,
name|foundUuid
argument_list|,
name|groupUuid
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|ensureNewNameIsNotUsed ()
specifier|private
name|void
name|ensureNewNameIsNotUsed
parameter_list|()
throws|throws
name|OrmDuplicateKeyException
block|{
if|if
condition|(
name|newGroupName
operator|.
name|isPresent
argument_list|()
operator|&&
name|nameConflicting
condition|)
block|{
throw|throw
operator|new
name|OrmDuplicateKeyException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Name '%s' is already used"
argument_list|,
name|newGroupName
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|onSave (CommitBuilder commit)
specifier|protected
name|boolean
name|onSave
parameter_list|(
name|CommitBuilder
name|commit
parameter_list|)
throws|throws
name|IOException
throws|,
name|ConfigInvalidException
block|{
if|if
condition|(
operator|!
name|oldGroupName
operator|.
name|isPresent
argument_list|()
operator|&&
operator|!
name|newGroupName
operator|.
name|isPresent
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|NoteMap
name|noteMap
init|=
name|revision
operator|==
literal|null
condition|?
name|NoteMap
operator|.
name|newEmptyMap
argument_list|()
else|:
name|NoteMap
operator|.
name|read
argument_list|(
name|reader
argument_list|,
name|revision
argument_list|)
decl_stmt|;
if|if
condition|(
name|oldGroupName
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|removeNote
argument_list|(
name|noteMap
argument_list|,
name|oldGroupName
operator|.
name|get
argument_list|()
argument_list|,
name|inserter
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|newGroupName
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|addNote
argument_list|(
name|noteMap
argument_list|,
name|newGroupName
operator|.
name|get
argument_list|()
argument_list|,
name|groupUuid
argument_list|,
name|inserter
argument_list|)
expr_stmt|;
block|}
name|commit
operator|.
name|setTreeId
argument_list|(
name|noteMap
operator|.
name|writeTree
argument_list|(
name|inserter
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|removeNote ( NoteMap noteMap, AccountGroup.NameKey groupName, ObjectInserter inserter)
specifier|private
specifier|static
name|void
name|removeNote
parameter_list|(
name|NoteMap
name|noteMap
parameter_list|,
name|AccountGroup
operator|.
name|NameKey
name|groupName
parameter_list|,
name|ObjectInserter
name|inserter
parameter_list|)
throws|throws
name|IOException
block|{
name|ObjectId
name|noteKey
init|=
name|getNoteKey
argument_list|(
name|groupName
argument_list|)
decl_stmt|;
name|noteMap
operator|.
name|set
argument_list|(
name|noteKey
argument_list|,
literal|null
argument_list|,
name|inserter
argument_list|)
expr_stmt|;
block|}
DECL|method|addNote ( NoteMap noteMap, AccountGroup.NameKey groupName, AccountGroup.UUID groupUuid, ObjectInserter inserter)
specifier|private
specifier|static
name|void
name|addNote
parameter_list|(
name|NoteMap
name|noteMap
parameter_list|,
name|AccountGroup
operator|.
name|NameKey
name|groupName
parameter_list|,
name|AccountGroup
operator|.
name|UUID
name|groupUuid
parameter_list|,
name|ObjectInserter
name|inserter
parameter_list|)
throws|throws
name|IOException
block|{
name|ObjectId
name|noteKey
init|=
name|getNoteKey
argument_list|(
name|groupName
argument_list|)
decl_stmt|;
name|noteMap
operator|.
name|set
argument_list|(
name|noteKey
argument_list|,
name|getAsNoteData
argument_list|(
name|groupUuid
argument_list|,
name|groupName
argument_list|)
argument_list|,
name|inserter
argument_list|)
expr_stmt|;
block|}
comment|// Use the same approach as ExternalId.Key.sha1().
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|getNoteKey (AccountGroup.NameKey groupName)
specifier|private
specifier|static
name|ObjectId
name|getNoteKey
parameter_list|(
name|AccountGroup
operator|.
name|NameKey
name|groupName
parameter_list|)
block|{
return|return
name|ObjectId
operator|.
name|fromRaw
argument_list|(
name|Hashing
operator|.
name|sha1
argument_list|()
operator|.
name|hashString
argument_list|(
name|groupName
operator|.
name|get
argument_list|()
argument_list|,
name|UTF_8
argument_list|)
operator|.
name|asBytes
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getAsNoteData (AccountGroup.UUID uuid, AccountGroup.NameKey groupName)
specifier|private
specifier|static
name|String
name|getAsNoteData
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|uuid
parameter_list|,
name|AccountGroup
operator|.
name|NameKey
name|groupName
parameter_list|)
block|{
name|Config
name|config
init|=
operator|new
name|Config
argument_list|()
decl_stmt|;
name|config
operator|.
name|setString
argument_list|(
name|SECTION_NAME
argument_list|,
literal|null
argument_list|,
name|UUID_PARAM
argument_list|,
name|uuid
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|config
operator|.
name|setString
argument_list|(
name|SECTION_NAME
argument_list|,
literal|null
argument_list|,
name|NAME_PARAM
argument_list|,
name|groupName
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|config
operator|.
name|toText
argument_list|()
return|;
block|}
DECL|method|getGroupReference (ObjectReader reader, ObjectId noteDataBlobId)
specifier|private
specifier|static
name|GroupReference
name|getGroupReference
parameter_list|(
name|ObjectReader
name|reader
parameter_list|,
name|ObjectId
name|noteDataBlobId
parameter_list|)
throws|throws
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|byte
index|[]
name|noteData
init|=
name|reader
operator|.
name|open
argument_list|(
name|noteDataBlobId
argument_list|,
name|OBJ_BLOB
argument_list|)
operator|.
name|getCachedBytes
argument_list|()
decl_stmt|;
return|return
name|getFromNoteData
argument_list|(
name|noteData
argument_list|)
return|;
block|}
DECL|method|getFromNoteData (byte[] noteData)
specifier|private
specifier|static
name|GroupReference
name|getFromNoteData
parameter_list|(
name|byte
index|[]
name|noteData
parameter_list|)
throws|throws
name|ConfigInvalidException
block|{
name|Config
name|config
init|=
operator|new
name|Config
argument_list|()
decl_stmt|;
name|config
operator|.
name|fromText
argument_list|(
operator|new
name|String
argument_list|(
name|noteData
argument_list|,
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|uuid
init|=
name|config
operator|.
name|getString
argument_list|(
name|SECTION_NAME
argument_list|,
literal|null
argument_list|,
name|UUID_PARAM
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|config
operator|.
name|getString
argument_list|(
name|SECTION_NAME
argument_list|,
literal|null
argument_list|,
name|NAME_PARAM
argument_list|)
decl_stmt|;
if|if
condition|(
name|uuid
operator|==
literal|null
operator|||
name|name
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ConfigInvalidException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"UUID '%s' and name '%s' must be defined"
argument_list|,
name|uuid
argument_list|,
name|name
argument_list|)
argument_list|)
throw|;
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
name|uuid
argument_list|)
argument_list|,
name|name
argument_list|)
return|;
block|}
block|}
end_class

end_unit

