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
name|checkState
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
name|Change
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
name|Comment
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
name|PatchSet
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
name|server
operator|.
name|CurrentUser
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
name|InternalUser
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
name|ChangeControl
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
name|Timestamp
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
name|Constants
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
comment|/** A single delta related to a specific patch-set of a change. */
end_comment

begin_class
DECL|class|AbstractChangeUpdate
specifier|public
specifier|abstract
class|class
name|AbstractChangeUpdate
block|{
DECL|field|migration
specifier|protected
specifier|final
name|NotesMigration
name|migration
decl_stmt|;
DECL|field|noteUtil
specifier|protected
specifier|final
name|ChangeNoteUtil
name|noteUtil
decl_stmt|;
DECL|field|anonymousCowardName
specifier|protected
specifier|final
name|String
name|anonymousCowardName
decl_stmt|;
DECL|field|accountId
specifier|protected
specifier|final
name|Account
operator|.
name|Id
name|accountId
decl_stmt|;
DECL|field|realAccountId
specifier|protected
specifier|final
name|Account
operator|.
name|Id
name|realAccountId
decl_stmt|;
DECL|field|authorIdent
specifier|protected
specifier|final
name|PersonIdent
name|authorIdent
decl_stmt|;
DECL|field|when
specifier|protected
specifier|final
name|Date
name|when
decl_stmt|;
DECL|field|readOnlySkewMs
specifier|private
specifier|final
name|long
name|readOnlySkewMs
decl_stmt|;
DECL|field|notes
annotation|@
name|Nullable
specifier|private
specifier|final
name|ChangeNotes
name|notes
decl_stmt|;
DECL|field|change
specifier|private
specifier|final
name|Change
name|change
decl_stmt|;
DECL|field|serverIdent
specifier|protected
specifier|final
name|PersonIdent
name|serverIdent
decl_stmt|;
DECL|field|psId
specifier|protected
name|PatchSet
operator|.
name|Id
name|psId
decl_stmt|;
DECL|field|result
specifier|private
name|ObjectId
name|result
decl_stmt|;
DECL|method|AbstractChangeUpdate ( Config cfg, NotesMigration migration, ChangeControl ctl, PersonIdent serverIdent, String anonymousCowardName, ChangeNoteUtil noteUtil, Date when)
specifier|protected
name|AbstractChangeUpdate
parameter_list|(
name|Config
name|cfg
parameter_list|,
name|NotesMigration
name|migration
parameter_list|,
name|ChangeControl
name|ctl
parameter_list|,
name|PersonIdent
name|serverIdent
parameter_list|,
name|String
name|anonymousCowardName
parameter_list|,
name|ChangeNoteUtil
name|noteUtil
parameter_list|,
name|Date
name|when
parameter_list|)
block|{
name|this
operator|.
name|migration
operator|=
name|migration
expr_stmt|;
name|this
operator|.
name|noteUtil
operator|=
name|noteUtil
expr_stmt|;
name|this
operator|.
name|serverIdent
operator|=
operator|new
name|PersonIdent
argument_list|(
name|serverIdent
argument_list|,
name|when
argument_list|)
expr_stmt|;
name|this
operator|.
name|anonymousCowardName
operator|=
name|anonymousCowardName
expr_stmt|;
name|this
operator|.
name|notes
operator|=
name|ctl
operator|.
name|getNotes
argument_list|()
expr_stmt|;
name|this
operator|.
name|change
operator|=
name|notes
operator|.
name|getChange
argument_list|()
expr_stmt|;
name|this
operator|.
name|accountId
operator|=
name|accountId
argument_list|(
name|ctl
operator|.
name|getUser
argument_list|()
argument_list|)
expr_stmt|;
name|Account
operator|.
name|Id
name|realAccountId
init|=
name|accountId
argument_list|(
name|ctl
operator|.
name|getUser
argument_list|()
operator|.
name|getRealUser
argument_list|()
argument_list|)
decl_stmt|;
name|this
operator|.
name|realAccountId
operator|=
name|realAccountId
operator|!=
literal|null
condition|?
name|realAccountId
else|:
name|accountId
expr_stmt|;
name|this
operator|.
name|authorIdent
operator|=
name|ident
argument_list|(
name|noteUtil
argument_list|,
name|serverIdent
argument_list|,
name|anonymousCowardName
argument_list|,
name|ctl
operator|.
name|getUser
argument_list|()
argument_list|,
name|when
argument_list|)
expr_stmt|;
name|this
operator|.
name|when
operator|=
name|when
expr_stmt|;
name|this
operator|.
name|readOnlySkewMs
operator|=
name|NoteDbChangeState
operator|.
name|getReadOnlySkew
argument_list|(
name|cfg
argument_list|)
expr_stmt|;
block|}
DECL|method|AbstractChangeUpdate ( Config cfg, NotesMigration migration, ChangeNoteUtil noteUtil, PersonIdent serverIdent, String anonymousCowardName, @Nullable ChangeNotes notes, @Nullable Change change, Account.Id accountId, Account.Id realAccountId, PersonIdent authorIdent, Date when)
specifier|protected
name|AbstractChangeUpdate
parameter_list|(
name|Config
name|cfg
parameter_list|,
name|NotesMigration
name|migration
parameter_list|,
name|ChangeNoteUtil
name|noteUtil
parameter_list|,
name|PersonIdent
name|serverIdent
parameter_list|,
name|String
name|anonymousCowardName
parameter_list|,
annotation|@
name|Nullable
name|ChangeNotes
name|notes
parameter_list|,
annotation|@
name|Nullable
name|Change
name|change
parameter_list|,
name|Account
operator|.
name|Id
name|accountId
parameter_list|,
name|Account
operator|.
name|Id
name|realAccountId
parameter_list|,
name|PersonIdent
name|authorIdent
parameter_list|,
name|Date
name|when
parameter_list|)
block|{
name|checkArgument
argument_list|(
operator|(
name|notes
operator|!=
literal|null
operator|&&
name|change
operator|==
literal|null
operator|)
operator|||
operator|(
name|notes
operator|==
literal|null
operator|&&
name|change
operator|!=
literal|null
operator|)
argument_list|,
literal|"exactly one of notes or change required"
argument_list|)
expr_stmt|;
name|this
operator|.
name|migration
operator|=
name|migration
expr_stmt|;
name|this
operator|.
name|noteUtil
operator|=
name|noteUtil
expr_stmt|;
name|this
operator|.
name|serverIdent
operator|=
operator|new
name|PersonIdent
argument_list|(
name|serverIdent
argument_list|,
name|when
argument_list|)
expr_stmt|;
name|this
operator|.
name|anonymousCowardName
operator|=
name|anonymousCowardName
expr_stmt|;
name|this
operator|.
name|notes
operator|=
name|notes
expr_stmt|;
name|this
operator|.
name|change
operator|=
name|change
operator|!=
literal|null
condition|?
name|change
else|:
name|notes
operator|.
name|getChange
argument_list|()
expr_stmt|;
name|this
operator|.
name|accountId
operator|=
name|accountId
expr_stmt|;
name|this
operator|.
name|realAccountId
operator|=
name|realAccountId
expr_stmt|;
name|this
operator|.
name|authorIdent
operator|=
name|authorIdent
expr_stmt|;
name|this
operator|.
name|when
operator|=
name|when
expr_stmt|;
name|this
operator|.
name|readOnlySkewMs
operator|=
name|NoteDbChangeState
operator|.
name|getReadOnlySkew
argument_list|(
name|cfg
argument_list|)
expr_stmt|;
block|}
DECL|method|checkUserType (CurrentUser user)
specifier|private
specifier|static
name|void
name|checkUserType
parameter_list|(
name|CurrentUser
name|user
parameter_list|)
block|{
name|checkArgument
argument_list|(
operator|(
name|user
operator|instanceof
name|IdentifiedUser
operator|)
operator|||
operator|(
name|user
operator|instanceof
name|InternalUser
operator|)
argument_list|,
literal|"user must be IdentifiedUser or InternalUser: %s"
argument_list|,
name|user
argument_list|)
expr_stmt|;
block|}
DECL|method|accountId (CurrentUser u)
specifier|private
specifier|static
name|Account
operator|.
name|Id
name|accountId
parameter_list|(
name|CurrentUser
name|u
parameter_list|)
block|{
name|checkUserType
argument_list|(
name|u
argument_list|)
expr_stmt|;
return|return
operator|(
name|u
operator|instanceof
name|IdentifiedUser
operator|)
condition|?
name|u
operator|.
name|getAccountId
argument_list|()
else|:
literal|null
return|;
block|}
DECL|method|ident ( ChangeNoteUtil noteUtil, PersonIdent serverIdent, String anonymousCowardName, CurrentUser u, Date when)
specifier|private
specifier|static
name|PersonIdent
name|ident
parameter_list|(
name|ChangeNoteUtil
name|noteUtil
parameter_list|,
name|PersonIdent
name|serverIdent
parameter_list|,
name|String
name|anonymousCowardName
parameter_list|,
name|CurrentUser
name|u
parameter_list|,
name|Date
name|when
parameter_list|)
block|{
name|checkUserType
argument_list|(
name|u
argument_list|)
expr_stmt|;
if|if
condition|(
name|u
operator|instanceof
name|IdentifiedUser
condition|)
block|{
return|return
name|noteUtil
operator|.
name|newIdent
argument_list|(
name|u
operator|.
name|asIdentifiedUser
argument_list|()
operator|.
name|getAccount
argument_list|()
argument_list|,
name|when
argument_list|,
name|serverIdent
argument_list|,
name|anonymousCowardName
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|u
operator|instanceof
name|InternalUser
condition|)
block|{
return|return
name|serverIdent
return|;
block|}
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
DECL|method|getId ()
specifier|public
name|Change
operator|.
name|Id
name|getId
parameter_list|()
block|{
return|return
name|change
operator|.
name|getId
argument_list|()
return|;
block|}
comment|/**    * @return notes for the state of this change prior to this update. If this update is part of a    *     series managed by a {@link NoteDbUpdateManager}, then this reflects the state prior to the    *     first update in the series. A null return value can only happen when the change is being    *     rebuilt from NoteDb. A change that is in the process of being created will result in a    *     non-null return value from this method, but a null return value from {@link    *     ChangeNotes#getRevision()}.    */
annotation|@
name|Nullable
DECL|method|getNotes ()
specifier|public
name|ChangeNotes
name|getNotes
parameter_list|()
block|{
return|return
name|notes
return|;
block|}
DECL|method|getChange ()
specifier|public
name|Change
name|getChange
parameter_list|()
block|{
return|return
name|change
return|;
block|}
DECL|method|getWhen ()
specifier|public
name|Date
name|getWhen
parameter_list|()
block|{
return|return
name|when
return|;
block|}
DECL|method|getPatchSetId ()
specifier|public
name|PatchSet
operator|.
name|Id
name|getPatchSetId
parameter_list|()
block|{
return|return
name|psId
return|;
block|}
DECL|method|setPatchSetId (PatchSet.Id psId)
specifier|public
name|void
name|setPatchSetId
parameter_list|(
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|psId
operator|==
literal|null
operator|||
name|psId
operator|.
name|getParentKey
argument_list|()
operator|.
name|equals
argument_list|(
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|psId
operator|=
name|psId
expr_stmt|;
block|}
DECL|method|getAccountId ()
specifier|public
name|Account
operator|.
name|Id
name|getAccountId
parameter_list|()
block|{
name|checkState
argument_list|(
name|accountId
operator|!=
literal|null
argument_list|,
literal|"author identity for %s is not from an IdentifiedUser: %s"
argument_list|,
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|authorIdent
operator|.
name|toExternalString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|accountId
return|;
block|}
DECL|method|getNullableAccountId ()
specifier|public
name|Account
operator|.
name|Id
name|getNullableAccountId
parameter_list|()
block|{
return|return
name|accountId
return|;
block|}
DECL|method|newIdent (Account author, Date when)
specifier|protected
name|PersonIdent
name|newIdent
parameter_list|(
name|Account
name|author
parameter_list|,
name|Date
name|when
parameter_list|)
block|{
return|return
name|noteUtil
operator|.
name|newIdent
argument_list|(
name|author
argument_list|,
name|when
argument_list|,
name|serverIdent
argument_list|,
name|anonymousCowardName
argument_list|)
return|;
block|}
comment|/** Whether no updates have been done. */
DECL|method|isEmpty ()
specifier|public
specifier|abstract
name|boolean
name|isEmpty
parameter_list|()
function_decl|;
comment|/**    * @return the NameKey for the project where the update will be stored, which is not necessarily    *     the same as the change's project.    */
DECL|method|getProjectName ()
specifier|protected
specifier|abstract
name|Project
operator|.
name|NameKey
name|getProjectName
parameter_list|()
function_decl|;
DECL|method|getRefName ()
specifier|protected
specifier|abstract
name|String
name|getRefName
parameter_list|()
function_decl|;
comment|/**    * Apply this update to the given inserter.    *    * @param rw walk for reading back any objects needed for the update.    * @param ins inserter to write to; callers should not flush.    * @param curr the current tip of the branch prior to this update.    * @return commit ID produced by inserting this update's commit, or null if this update is a no-op    *     and should be skipped. The zero ID is a valid return value, and indicates the ref should be    *     deleted.    * @throws OrmException if a Gerrit-level error occurred.    * @throws IOException if a lower-level error occurred.    */
DECL|method|apply (RevWalk rw, ObjectInserter ins, ObjectId curr)
specifier|final
name|ObjectId
name|apply
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
if|if
condition|(
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// Allow this method to proceed even if migration.failChangeWrites() = true.
comment|// This may be used by an auto-rebuilding step that the caller does not plan
comment|// to actually store.
name|checkArgument
argument_list|(
name|rw
operator|.
name|getObjectReader
argument_list|()
operator|.
name|getCreatedFromInserter
argument_list|()
operator|==
name|ins
argument_list|)
expr_stmt|;
name|checkNotReadOnly
argument_list|()
expr_stmt|;
name|ObjectId
name|z
init|=
name|ObjectId
operator|.
name|zeroId
argument_list|()
decl_stmt|;
name|CommitBuilder
name|cb
init|=
name|applyImpl
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
name|cb
operator|==
literal|null
condition|)
block|{
name|result
operator|=
name|z
expr_stmt|;
return|return
name|z
return|;
comment|// Impl intends to delete the ref.
block|}
elseif|else
if|if
condition|(
name|cb
operator|==
name|NO_OP_UPDATE
condition|)
block|{
return|return
literal|null
return|;
comment|// Impl is a no-op.
block|}
name|cb
operator|.
name|setAuthor
argument_list|(
name|authorIdent
argument_list|)
expr_stmt|;
name|cb
operator|.
name|setCommitter
argument_list|(
operator|new
name|PersonIdent
argument_list|(
name|serverIdent
argument_list|,
name|when
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|curr
operator|.
name|equals
argument_list|(
name|z
argument_list|)
condition|)
block|{
name|cb
operator|.
name|setParentId
argument_list|(
name|curr
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|cb
operator|.
name|setParentIds
argument_list|()
expr_stmt|;
comment|// Ref is currently nonexistent, commit has no parents.
block|}
if|if
condition|(
name|cb
operator|.
name|getTreeId
argument_list|()
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|curr
operator|.
name|equals
argument_list|(
name|z
argument_list|)
condition|)
block|{
name|cb
operator|.
name|setTreeId
argument_list|(
name|emptyTree
argument_list|(
name|ins
argument_list|)
argument_list|)
expr_stmt|;
comment|// No parent, assume empty tree.
block|}
else|else
block|{
name|RevCommit
name|p
init|=
name|rw
operator|.
name|parseCommit
argument_list|(
name|curr
argument_list|)
decl_stmt|;
name|cb
operator|.
name|setTreeId
argument_list|(
name|p
operator|.
name|getTree
argument_list|()
argument_list|)
expr_stmt|;
comment|// Copy tree from parent.
block|}
block|}
name|result
operator|=
name|ins
operator|.
name|insert
argument_list|(
name|cb
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|checkNotReadOnly ()
specifier|protected
name|void
name|checkNotReadOnly
parameter_list|()
throws|throws
name|OrmException
block|{
name|ChangeNotes
name|notes
init|=
name|getNotes
argument_list|()
decl_stmt|;
if|if
condition|(
name|notes
operator|==
literal|null
condition|)
block|{
comment|// Can only happen during ChangeRebuilder, which will never include a read-only lease.
return|return;
block|}
name|Timestamp
name|until
init|=
name|notes
operator|.
name|getReadOnlyUntil
argument_list|()
decl_stmt|;
if|if
condition|(
name|until
operator|!=
literal|null
operator|&&
name|NoteDbChangeState
operator|.
name|timeForReadOnlyCheck
argument_list|(
name|readOnlySkewMs
argument_list|)
operator|.
name|before
argument_list|(
name|until
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|OrmException
argument_list|(
literal|"change "
operator|+
name|notes
operator|.
name|getChangeId
argument_list|()
operator|+
literal|" is read-only until "
operator|+
name|until
argument_list|)
throw|;
block|}
block|}
comment|/**    * Create a commit containing the contents of this update.    *    * @param ins inserter to write to; callers should not flush.    * @return a new commit builder representing this commit, or null to indicate the meta ref should    *     be deleted as a result of this update. The parent, author, and committer fields in the    *     return value are always overwritten. The tree ID may be unset by this method, which    *     indicates to the caller that it should be copied from the parent commit. To indicate that    *     this update is a no-op (but this could not be determined by {@link #isEmpty()}), return the    *     sentinel {@link #NO_OP_UPDATE}.    * @throws OrmException if a Gerrit-level error occurred.    * @throws IOException if a lower-level error occurred.    */
DECL|method|applyImpl (RevWalk rw, ObjectInserter ins, ObjectId curr)
specifier|protected
specifier|abstract
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
function_decl|;
DECL|field|NO_OP_UPDATE
specifier|protected
specifier|static
specifier|final
name|CommitBuilder
name|NO_OP_UPDATE
init|=
operator|new
name|CommitBuilder
argument_list|()
decl_stmt|;
DECL|method|getResult ()
name|ObjectId
name|getResult
parameter_list|()
block|{
return|return
name|result
return|;
block|}
DECL|method|allowWriteToNewRef ()
specifier|public
name|boolean
name|allowWriteToNewRef
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
DECL|method|emptyTree (ObjectInserter ins)
specifier|private
specifier|static
name|ObjectId
name|emptyTree
parameter_list|(
name|ObjectInserter
name|ins
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|ins
operator|.
name|insert
argument_list|(
name|Constants
operator|.
name|OBJ_TREE
argument_list|,
operator|new
name|byte
index|[]
block|{}
argument_list|)
return|;
block|}
DECL|method|verifyComment (Comment c)
specifier|protected
name|void
name|verifyComment
parameter_list|(
name|Comment
name|c
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|c
operator|.
name|revId
operator|!=
literal|null
argument_list|,
literal|"RevId required for comment: %s"
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|c
operator|.
name|author
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
name|getAccountId
argument_list|()
argument_list|)
argument_list|,
literal|"The author for the following comment does not match the author of this %s (%s): %s"
argument_list|,
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|getAccountId
argument_list|()
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|c
operator|.
name|getRealAuthor
argument_list|()
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
name|realAccountId
argument_list|)
argument_list|,
literal|"The real author for the following comment does not match the real"
operator|+
literal|" author of this %s (%s): %s"
argument_list|,
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|realAccountId
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

