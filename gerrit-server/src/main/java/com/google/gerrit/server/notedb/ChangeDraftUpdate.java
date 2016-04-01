begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2014 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.notedb
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|notedb
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
name|MoreObjects
operator|.
name|firstNonNull
import|;
end_import

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
name|checkArgument
import|;
end_import

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
name|auto
operator|.
name|value
operator|.
name|AutoValue
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
name|PatchLineComment
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
name|reviewdb
operator|.
name|client
operator|.
name|RevId
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
name|config
operator|.
name|AnonymousCowardName
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
name|assistedinject
operator|.
name|Assisted
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
name|assistedinject
operator|.
name|AssistedInject
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
name|RevWalk
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
name|Date
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

begin_comment
comment|/**  * A single delta to apply atomically to a change.  *<p>  * This delta contains only draft comments on a single patch set of a change by  * a single author. This delta will become a single commit in the All-Users  * repository.  *<p>  * This class is not thread safe.  */
end_comment

begin_class
DECL|class|ChangeDraftUpdate
specifier|public
class|class
name|ChangeDraftUpdate
extends|extends
name|AbstractChangeUpdate
block|{
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (ChangeNotes notes, Account.Id accountId, PersonIdent authorIdent, Date when)
name|ChangeDraftUpdate
name|create
parameter_list|(
name|ChangeNotes
name|notes
parameter_list|,
name|Account
operator|.
name|Id
name|accountId
parameter_list|,
name|PersonIdent
name|authorIdent
parameter_list|,
name|Date
name|when
parameter_list|)
function_decl|;
block|}
annotation|@
name|AutoValue
DECL|class|Key
specifier|static
specifier|abstract
class|class
name|Key
block|{
DECL|method|revId ()
specifier|abstract
name|RevId
name|revId
parameter_list|()
function_decl|;
DECL|method|key ()
specifier|abstract
name|PatchLineComment
operator|.
name|Key
name|key
parameter_list|()
function_decl|;
block|}
DECL|method|key (PatchLineComment c)
specifier|private
specifier|static
name|Key
name|key
parameter_list|(
name|PatchLineComment
name|c
parameter_list|)
block|{
return|return
operator|new
name|AutoValue_ChangeDraftUpdate_Key
argument_list|(
name|c
operator|.
name|getRevId
argument_list|()
argument_list|,
name|c
operator|.
name|getKey
argument_list|()
argument_list|)
return|;
block|}
DECL|field|draftsProject
specifier|private
specifier|final
name|AllUsersName
name|draftsProject
decl_stmt|;
comment|// TODO: can go back to a list?
DECL|field|put
specifier|private
name|Map
argument_list|<
name|Key
argument_list|,
name|PatchLineComment
argument_list|>
name|put
decl_stmt|;
DECL|field|delete
specifier|private
name|Set
argument_list|<
name|Key
argument_list|>
name|delete
decl_stmt|;
annotation|@
name|AssistedInject
DECL|method|ChangeDraftUpdate ( @erritPersonIdent PersonIdent serverIdent, @AnonymousCowardName String anonymousCowardName, NotesMigration migration, AllUsersName allUsers, ChangeNoteUtil noteUtil, @Assisted ChangeNotes notes, @Assisted Account.Id accountId, @Assisted PersonIdent authorIdent, @Assisted Date when)
specifier|private
name|ChangeDraftUpdate
parameter_list|(
annotation|@
name|GerritPersonIdent
name|PersonIdent
name|serverIdent
parameter_list|,
annotation|@
name|AnonymousCowardName
name|String
name|anonymousCowardName
parameter_list|,
name|NotesMigration
name|migration
parameter_list|,
name|AllUsersName
name|allUsers
parameter_list|,
name|ChangeNoteUtil
name|noteUtil
parameter_list|,
annotation|@
name|Assisted
name|ChangeNotes
name|notes
parameter_list|,
annotation|@
name|Assisted
name|Account
operator|.
name|Id
name|accountId
parameter_list|,
annotation|@
name|Assisted
name|PersonIdent
name|authorIdent
parameter_list|,
annotation|@
name|Assisted
name|Date
name|when
parameter_list|)
block|{
name|super
argument_list|(
name|migration
argument_list|,
name|noteUtil
argument_list|,
name|serverIdent
argument_list|,
name|anonymousCowardName
argument_list|,
name|notes
argument_list|,
name|accountId
argument_list|,
name|authorIdent
argument_list|,
name|when
argument_list|)
expr_stmt|;
name|this
operator|.
name|draftsProject
operator|=
name|allUsers
expr_stmt|;
name|this
operator|.
name|put
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|delete
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
block|}
DECL|method|putComment (PatchLineComment c)
specifier|public
name|void
name|putComment
parameter_list|(
name|PatchLineComment
name|c
parameter_list|)
block|{
name|verifyComment
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|c
operator|.
name|getStatus
argument_list|()
operator|==
name|PatchLineComment
operator|.
name|Status
operator|.
name|DRAFT
argument_list|,
literal|"Cannot insert a published comment into a ChangeDraftUpdate"
argument_list|)
expr_stmt|;
name|put
operator|.
name|put
argument_list|(
name|key
argument_list|(
name|c
argument_list|)
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteComment (PatchLineComment c)
specifier|public
name|void
name|deleteComment
parameter_list|(
name|PatchLineComment
name|c
parameter_list|)
block|{
name|verifyComment
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|delete
operator|.
name|add
argument_list|(
name|key
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteComment (RevId revId, PatchLineComment.Key key)
specifier|public
name|void
name|deleteComment
parameter_list|(
name|RevId
name|revId
parameter_list|,
name|PatchLineComment
operator|.
name|Key
name|key
parameter_list|)
block|{
name|delete
operator|.
name|add
argument_list|(
operator|new
name|AutoValue_ChangeDraftUpdate_Key
argument_list|(
name|revId
argument_list|,
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyComment (PatchLineComment comment)
specifier|private
name|void
name|verifyComment
parameter_list|(
name|PatchLineComment
name|comment
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|comment
operator|.
name|getAuthor
argument_list|()
operator|.
name|equals
argument_list|(
name|accountId
argument_list|)
argument_list|,
literal|"The author for the following comment does not match the author of"
operator|+
literal|" this ChangeDraftUpdate (%s): %s"
argument_list|,
name|accountId
argument_list|,
name|comment
argument_list|)
expr_stmt|;
block|}
comment|/** @return the tree id for the updated tree */
DECL|method|storeCommentsInNotes (RevWalk rw, ObjectInserter ins, ObjectId curr)
specifier|private
name|ObjectId
name|storeCommentsInNotes
parameter_list|(
name|RevWalk
name|rw
parameter_list|,
name|ObjectInserter
name|ins
parameter_list|,
name|ObjectId
name|curr
parameter_list|)
throws|throws
name|ConfigInvalidException
throws|,
name|OrmException
throws|,
name|IOException
block|{
name|RevisionNoteMap
name|rnm
init|=
name|getRevisionNoteMap
argument_list|(
name|rw
argument_list|,
name|curr
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|RevId
argument_list|>
name|updatedRevs
init|=
name|Sets
operator|.
name|newHashSetWithExpectedSize
argument_list|(
name|rnm
operator|.
name|revisionNotes
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|RevisionNoteBuilder
operator|.
name|Cache
name|cache
init|=
operator|new
name|RevisionNoteBuilder
operator|.
name|Cache
argument_list|(
name|rnm
argument_list|)
decl_stmt|;
for|for
control|(
name|PatchLineComment
name|c
range|:
name|put
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|delete
operator|.
name|contains
argument_list|(
name|key
argument_list|(
name|c
argument_list|)
argument_list|)
condition|)
block|{
name|cache
operator|.
name|get
argument_list|(
name|c
operator|.
name|getRevId
argument_list|()
argument_list|)
operator|.
name|putComment
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Key
name|k
range|:
name|delete
control|)
block|{
name|cache
operator|.
name|get
argument_list|(
name|k
operator|.
name|revId
argument_list|()
argument_list|)
operator|.
name|deleteComment
argument_list|(
name|k
operator|.
name|key
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|RevId
argument_list|,
name|RevisionNoteBuilder
argument_list|>
name|builders
init|=
name|cache
operator|.
name|getBuilders
argument_list|()
decl_stmt|;
name|boolean
name|hasComments
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|RevId
argument_list|,
name|RevisionNoteBuilder
argument_list|>
name|e
range|:
name|builders
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|updatedRevs
operator|.
name|add
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|ObjectId
name|id
init|=
name|ObjectId
operator|.
name|fromString
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
name|data
init|=
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|build
argument_list|(
name|noteUtil
argument_list|)
decl_stmt|;
if|if
condition|(
name|data
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|rnm
operator|.
name|noteMap
operator|.
name|remove
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|hasComments
operator|=
literal|true
expr_stmt|;
name|ObjectId
name|dataBlob
init|=
name|ins
operator|.
name|insert
argument_list|(
name|OBJ_BLOB
argument_list|,
name|data
argument_list|)
decl_stmt|;
name|rnm
operator|.
name|noteMap
operator|.
name|set
argument_list|(
name|id
argument_list|,
name|dataBlob
argument_list|)
expr_stmt|;
block|}
block|}
comment|// If we touched every revision and there are no comments left, tell the
comment|// caller to delete the entire ref.
name|boolean
name|touchedAllRevs
init|=
name|updatedRevs
operator|.
name|equals
argument_list|(
name|rnm
operator|.
name|revisionNotes
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|touchedAllRevs
operator|&&
operator|!
name|hasComments
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|rnm
operator|.
name|noteMap
operator|.
name|writeTree
argument_list|(
name|ins
argument_list|)
return|;
block|}
DECL|method|getRevisionNoteMap (RevWalk rw, ObjectId curr)
specifier|private
name|RevisionNoteMap
name|getRevisionNoteMap
parameter_list|(
name|RevWalk
name|rw
parameter_list|,
name|ObjectId
name|curr
parameter_list|)
throws|throws
name|ConfigInvalidException
throws|,
name|OrmException
throws|,
name|IOException
block|{
if|if
condition|(
name|migration
operator|.
name|readChanges
argument_list|()
condition|)
block|{
comment|// If reading from changes is enabled, then the old DraftCommentNotes
comment|// already parsed the revision notes. We can reuse them as long as the ref
comment|// hasn't advanced.
name|DraftCommentNotes
name|draftNotes
init|=
name|getNotes
argument_list|()
operator|.
name|load
argument_list|()
operator|.
name|getDraftCommentNotes
argument_list|()
decl_stmt|;
if|if
condition|(
name|draftNotes
operator|!=
literal|null
condition|)
block|{
name|ObjectId
name|idFromNotes
init|=
name|firstNonNull
argument_list|(
name|draftNotes
operator|.
name|getRevision
argument_list|()
argument_list|,
name|ObjectId
operator|.
name|zeroId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|idFromNotes
operator|.
name|equals
argument_list|(
name|curr
argument_list|)
condition|)
block|{
return|return
name|checkNotNull
argument_list|(
name|getNotes
argument_list|()
operator|.
name|revisionNoteMap
argument_list|)
return|;
block|}
block|}
block|}
name|NoteMap
name|noteMap
decl_stmt|;
if|if
condition|(
operator|!
name|curr
operator|.
name|equals
argument_list|(
name|ObjectId
operator|.
name|zeroId
argument_list|()
argument_list|)
condition|)
block|{
name|noteMap
operator|=
name|NoteMap
operator|.
name|read
argument_list|(
name|rw
operator|.
name|getObjectReader
argument_list|()
argument_list|,
name|rw
operator|.
name|parseCommit
argument_list|(
name|curr
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|noteMap
operator|=
name|NoteMap
operator|.
name|newEmptyMap
argument_list|()
expr_stmt|;
block|}
comment|// Even though reading from changes might not be enabled, we need to
comment|// parse any existing revision notes so we can merge them.
return|return
name|RevisionNoteMap
operator|.
name|parse
argument_list|(
name|noteUtil
argument_list|,
name|getId
argument_list|()
argument_list|,
name|rw
operator|.
name|getObjectReader
argument_list|()
argument_list|,
name|noteMap
argument_list|,
literal|true
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|applyImpl (RevWalk rw, ObjectInserter ins, ObjectId curr)
specifier|protected
name|CommitBuilder
name|applyImpl
parameter_list|(
name|RevWalk
name|rw
parameter_list|,
name|ObjectInserter
name|ins
parameter_list|,
name|ObjectId
name|curr
parameter_list|)
throws|throws
name|OrmException
throws|,
name|IOException
block|{
name|CommitBuilder
name|cb
init|=
operator|new
name|CommitBuilder
argument_list|()
decl_stmt|;
name|cb
operator|.
name|setMessage
argument_list|(
literal|"Update draft comments"
argument_list|)
expr_stmt|;
try|try
block|{
name|ObjectId
name|treeId
init|=
name|storeCommentsInNotes
argument_list|(
name|rw
argument_list|,
name|ins
argument_list|,
name|curr
argument_list|)
decl_stmt|;
if|if
condition|(
name|treeId
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
comment|// Delete ref.
block|}
name|cb
operator|.
name|setTreeId
argument_list|(
name|checkNotNull
argument_list|(
name|treeId
argument_list|)
argument_list|)
expr_stmt|;
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
return|return
name|cb
return|;
block|}
annotation|@
name|Override
DECL|method|getProjectName ()
specifier|protected
name|Project
operator|.
name|NameKey
name|getProjectName
parameter_list|()
block|{
return|return
name|draftsProject
return|;
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
name|refsDraftComments
argument_list|(
name|accountId
argument_list|,
name|getId
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|isEmpty ()
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|delete
operator|.
name|isEmpty
argument_list|()
operator|&&
name|put
operator|.
name|isEmpty
argument_list|()
return|;
block|}
block|}
end_class

end_unit

