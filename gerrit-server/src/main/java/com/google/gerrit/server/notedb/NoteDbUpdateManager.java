begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2016 The Android Open Source Project
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
name|common
operator|.
name|collect
operator|.
name|ArrayListMultimap
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
name|ListMultimap
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
name|ChainedReceiveCommands
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
name|lib
operator|.
name|BatchRefUpdate
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
name|NullProgressMonitor
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
name|revwalk
operator|.
name|RevWalk
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
name|transport
operator|.
name|ReceiveCommand
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

begin_class
DECL|class|NoteDbUpdateManager
specifier|public
class|class
name|NoteDbUpdateManager
block|{
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (Project.NameKey projectName)
name|NoteDbUpdateManager
name|create
parameter_list|(
name|Project
operator|.
name|NameKey
name|projectName
parameter_list|)
function_decl|;
block|}
DECL|class|OpenRepo
specifier|private
specifier|static
class|class
name|OpenRepo
implements|implements
name|AutoCloseable
block|{
DECL|field|repo
specifier|final
name|Repository
name|repo
decl_stmt|;
DECL|field|rw
specifier|final
name|RevWalk
name|rw
decl_stmt|;
DECL|field|ins
specifier|final
name|ObjectInserter
name|ins
decl_stmt|;
DECL|field|cmds
specifier|final
name|ChainedReceiveCommands
name|cmds
decl_stmt|;
DECL|field|close
specifier|final
name|boolean
name|close
decl_stmt|;
DECL|method|OpenRepo (Repository repo, RevWalk rw, ObjectInserter ins, ChainedReceiveCommands cmds, boolean close)
name|OpenRepo
parameter_list|(
name|Repository
name|repo
parameter_list|,
name|RevWalk
name|rw
parameter_list|,
name|ObjectInserter
name|ins
parameter_list|,
name|ChainedReceiveCommands
name|cmds
parameter_list|,
name|boolean
name|close
parameter_list|)
block|{
name|this
operator|.
name|repo
operator|=
name|checkNotNull
argument_list|(
name|repo
argument_list|)
expr_stmt|;
name|this
operator|.
name|rw
operator|=
name|checkNotNull
argument_list|(
name|rw
argument_list|)
expr_stmt|;
name|this
operator|.
name|ins
operator|=
name|checkNotNull
argument_list|(
name|ins
argument_list|)
expr_stmt|;
name|this
operator|.
name|cmds
operator|=
name|checkNotNull
argument_list|(
name|cmds
argument_list|)
expr_stmt|;
name|this
operator|.
name|close
operator|=
name|close
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|close
condition|)
block|{
name|ins
operator|.
name|close
argument_list|()
expr_stmt|;
name|rw
operator|.
name|close
argument_list|()
expr_stmt|;
name|repo
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|field|repoManager
specifier|private
specifier|final
name|GitRepositoryManager
name|repoManager
decl_stmt|;
DECL|field|migration
specifier|private
specifier|final
name|NotesMigration
name|migration
decl_stmt|;
DECL|field|allUsersName
specifier|private
specifier|final
name|AllUsersName
name|allUsersName
decl_stmt|;
DECL|field|projectName
specifier|private
specifier|final
name|Project
operator|.
name|NameKey
name|projectName
decl_stmt|;
DECL|field|changeUpdates
specifier|private
specifier|final
name|ListMultimap
argument_list|<
name|String
argument_list|,
name|ChangeUpdate
argument_list|>
name|changeUpdates
decl_stmt|;
DECL|field|draftUpdates
specifier|private
specifier|final
name|ListMultimap
argument_list|<
name|String
argument_list|,
name|ChangeDraftUpdate
argument_list|>
name|draftUpdates
decl_stmt|;
DECL|field|changeRepo
specifier|private
name|OpenRepo
name|changeRepo
decl_stmt|;
DECL|field|allUsersRepo
specifier|private
name|OpenRepo
name|allUsersRepo
decl_stmt|;
annotation|@
name|AssistedInject
DECL|method|NoteDbUpdateManager (GitRepositoryManager repoManager, NotesMigration migration, AllUsersName allUsersName, @Assisted Project.NameKey projectName)
name|NoteDbUpdateManager
parameter_list|(
name|GitRepositoryManager
name|repoManager
parameter_list|,
name|NotesMigration
name|migration
parameter_list|,
name|AllUsersName
name|allUsersName
parameter_list|,
annotation|@
name|Assisted
name|Project
operator|.
name|NameKey
name|projectName
parameter_list|)
block|{
name|this
operator|.
name|repoManager
operator|=
name|repoManager
expr_stmt|;
name|this
operator|.
name|migration
operator|=
name|migration
expr_stmt|;
name|this
operator|.
name|allUsersName
operator|=
name|allUsersName
expr_stmt|;
name|this
operator|.
name|projectName
operator|=
name|projectName
expr_stmt|;
name|changeUpdates
operator|=
name|ArrayListMultimap
operator|.
name|create
argument_list|()
expr_stmt|;
name|draftUpdates
operator|=
name|ArrayListMultimap
operator|.
name|create
argument_list|()
expr_stmt|;
block|}
DECL|method|setChangeRepo (Repository repo, RevWalk rw, ObjectInserter ins, ChainedReceiveCommands cmds)
specifier|public
name|NoteDbUpdateManager
name|setChangeRepo
parameter_list|(
name|Repository
name|repo
parameter_list|,
name|RevWalk
name|rw
parameter_list|,
name|ObjectInserter
name|ins
parameter_list|,
name|ChainedReceiveCommands
name|cmds
parameter_list|)
block|{
name|checkState
argument_list|(
name|changeRepo
operator|==
literal|null
argument_list|,
literal|"change repo already initialized"
argument_list|)
expr_stmt|;
name|changeRepo
operator|=
operator|new
name|OpenRepo
argument_list|(
name|repo
argument_list|,
name|rw
argument_list|,
name|ins
argument_list|,
name|cmds
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getChangeRepo ()
name|Repository
name|getChangeRepo
parameter_list|()
throws|throws
name|IOException
block|{
name|initChangeRepo
argument_list|()
expr_stmt|;
return|return
name|changeRepo
operator|.
name|repo
return|;
block|}
DECL|method|getChangeRevWalk ()
name|RevWalk
name|getChangeRevWalk
parameter_list|()
throws|throws
name|IOException
block|{
name|initChangeRepo
argument_list|()
expr_stmt|;
return|return
name|changeRepo
operator|.
name|rw
return|;
block|}
DECL|method|getChangeCommands ()
name|ChainedReceiveCommands
name|getChangeCommands
parameter_list|()
throws|throws
name|IOException
block|{
name|initChangeRepo
argument_list|()
expr_stmt|;
return|return
name|changeRepo
operator|.
name|cmds
return|;
block|}
DECL|method|setAllUsersRepo (Repository repo, RevWalk rw, ObjectInserter ins, ChainedReceiveCommands cmds)
specifier|public
name|NoteDbUpdateManager
name|setAllUsersRepo
parameter_list|(
name|Repository
name|repo
parameter_list|,
name|RevWalk
name|rw
parameter_list|,
name|ObjectInserter
name|ins
parameter_list|,
name|ChainedReceiveCommands
name|cmds
parameter_list|)
block|{
name|checkState
argument_list|(
name|allUsersRepo
operator|==
literal|null
argument_list|,
literal|"allUsers repo already initialized"
argument_list|)
expr_stmt|;
name|allUsersRepo
operator|=
operator|new
name|OpenRepo
argument_list|(
name|repo
argument_list|,
name|rw
argument_list|,
name|ins
argument_list|,
name|cmds
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getAllUsersRepo ()
name|Repository
name|getAllUsersRepo
parameter_list|()
throws|throws
name|IOException
block|{
name|initAllUsersRepo
argument_list|()
expr_stmt|;
return|return
name|allUsersRepo
operator|.
name|repo
return|;
block|}
DECL|method|getAllUsersCommands ()
name|ChainedReceiveCommands
name|getAllUsersCommands
parameter_list|()
throws|throws
name|IOException
block|{
name|initAllUsersRepo
argument_list|()
expr_stmt|;
return|return
name|allUsersRepo
operator|.
name|cmds
return|;
block|}
DECL|method|initChangeRepo ()
specifier|private
name|void
name|initChangeRepo
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|changeRepo
operator|==
literal|null
condition|)
block|{
name|changeRepo
operator|=
name|openRepo
argument_list|(
name|projectName
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|initAllUsersRepo ()
specifier|private
name|void
name|initAllUsersRepo
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|allUsersRepo
operator|==
literal|null
condition|)
block|{
name|allUsersRepo
operator|=
name|openRepo
argument_list|(
name|allUsersName
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|openRepo (Project.NameKey p)
specifier|private
name|OpenRepo
name|openRepo
parameter_list|(
name|Project
operator|.
name|NameKey
name|p
parameter_list|)
throws|throws
name|IOException
block|{
name|Repository
name|repo
init|=
name|repoManager
operator|.
name|openMetadataRepository
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|ObjectInserter
name|ins
init|=
name|repo
operator|.
name|newObjectInserter
argument_list|()
decl_stmt|;
return|return
operator|new
name|OpenRepo
argument_list|(
name|repo
argument_list|,
operator|new
name|RevWalk
argument_list|(
name|ins
operator|.
name|newReader
argument_list|()
argument_list|)
argument_list|,
name|ins
argument_list|,
operator|new
name|ChainedReceiveCommands
argument_list|()
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|isEmpty ()
specifier|private
name|boolean
name|isEmpty
parameter_list|()
block|{
if|if
condition|(
operator|!
name|migration
operator|.
name|writeChanges
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
name|changeUpdates
operator|.
name|isEmpty
argument_list|()
operator|&&
name|draftUpdates
operator|.
name|isEmpty
argument_list|()
return|;
block|}
comment|/**    * Add an update to the list of updates to execute.    *<p>    * Updates should only be added to the manager after all mutations have been    * made, as this method may eagerly access the update.    *    * @param update the update to add.    */
DECL|method|add (ChangeUpdate update)
specifier|public
name|void
name|add
parameter_list|(
name|ChangeUpdate
name|update
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|update
operator|.
name|getProjectName
argument_list|()
operator|.
name|equals
argument_list|(
name|projectName
argument_list|)
argument_list|,
literal|"update for project %s cannot be added to manager for project %s"
argument_list|,
name|update
operator|.
name|getProjectName
argument_list|()
argument_list|,
name|projectName
argument_list|)
expr_stmt|;
name|changeUpdates
operator|.
name|put
argument_list|(
name|update
operator|.
name|getRefName
argument_list|()
argument_list|,
name|update
argument_list|)
expr_stmt|;
name|ChangeDraftUpdate
name|du
init|=
name|update
operator|.
name|getDraftUpdate
argument_list|()
decl_stmt|;
if|if
condition|(
name|du
operator|!=
literal|null
condition|)
block|{
name|draftUpdates
operator|.
name|put
argument_list|(
name|du
operator|.
name|getRefName
argument_list|()
argument_list|,
name|du
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|add (ChangeDraftUpdate draftUpdate)
specifier|public
name|void
name|add
parameter_list|(
name|ChangeDraftUpdate
name|draftUpdate
parameter_list|)
block|{
name|draftUpdates
operator|.
name|put
argument_list|(
name|draftUpdate
operator|.
name|getRefName
argument_list|()
argument_list|,
name|draftUpdate
argument_list|)
expr_stmt|;
block|}
DECL|method|execute ()
specifier|public
name|void
name|execute
parameter_list|()
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
return|return;
block|}
try|try
block|{
name|initChangeRepo
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|draftUpdates
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|initAllUsersRepo
argument_list|()
expr_stmt|;
block|}
name|addCommands
argument_list|()
expr_stmt|;
comment|// ChangeUpdates must execute before ChangeDraftUpdates.
comment|//
comment|// ChangeUpdate will automatically delete draft comments for any published
comment|// comments, but the updates to the two repos don't happen atomically.
comment|// Thus if the change meta update succeeds and the All-Users update fails,
comment|// we may have stale draft comments. Doing it in this order allows stale
comment|// comments to be filtered out by ChangeNotes, reflecting the fact that
comment|// comments can only go from DRAFT to PUBLISHED, not vice versa.
name|execute
argument_list|(
name|changeRepo
argument_list|)
expr_stmt|;
name|execute
argument_list|(
name|allUsersRepo
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|allUsersRepo
operator|!=
literal|null
condition|)
block|{
name|allUsersRepo
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|changeRepo
operator|!=
literal|null
condition|)
block|{
name|changeRepo
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|execute (OpenRepo or)
specifier|private
specifier|static
name|void
name|execute
parameter_list|(
name|OpenRepo
name|or
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|or
operator|==
literal|null
operator|||
name|or
operator|.
name|cmds
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
name|or
operator|.
name|ins
operator|.
name|flush
argument_list|()
expr_stmt|;
name|BatchRefUpdate
name|bru
init|=
name|or
operator|.
name|repo
operator|.
name|getRefDatabase
argument_list|()
operator|.
name|newBatchUpdate
argument_list|()
decl_stmt|;
name|or
operator|.
name|cmds
operator|.
name|addTo
argument_list|(
name|bru
argument_list|)
expr_stmt|;
name|bru
operator|.
name|execute
argument_list|(
name|or
operator|.
name|rw
argument_list|,
name|NullProgressMonitor
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
for|for
control|(
name|ReceiveCommand
name|cmd
range|:
name|bru
operator|.
name|getCommands
argument_list|()
control|)
block|{
if|if
condition|(
name|cmd
operator|.
name|getResult
argument_list|()
operator|!=
name|ReceiveCommand
operator|.
name|Result
operator|.
name|OK
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Update failed: "
operator|+
name|bru
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|addCommands ()
specifier|private
name|void
name|addCommands
parameter_list|()
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
return|return;
block|}
name|checkState
argument_list|(
name|changeRepo
operator|!=
literal|null
argument_list|,
literal|"must set change repo"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|draftUpdates
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|checkState
argument_list|(
name|allUsersRepo
operator|!=
literal|null
argument_list|,
literal|"must set all users repo"
argument_list|)
expr_stmt|;
block|}
name|addUpdates
argument_list|(
name|changeUpdates
argument_list|,
name|changeRepo
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|draftUpdates
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|addUpdates
argument_list|(
name|draftUpdates
argument_list|,
name|allUsersRepo
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addUpdates ( ListMultimap<String, ? extends AbstractChangeUpdate> updates, OpenRepo or)
specifier|private
specifier|static
name|void
name|addUpdates
parameter_list|(
name|ListMultimap
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|AbstractChangeUpdate
argument_list|>
name|updates
parameter_list|,
name|OpenRepo
name|or
parameter_list|)
throws|throws
name|OrmException
throws|,
name|IOException
block|{
for|for
control|(
name|String
name|refName
range|:
name|updates
operator|.
name|keySet
argument_list|()
control|)
block|{
name|ObjectId
name|old
init|=
name|firstNonNull
argument_list|(
name|or
operator|.
name|cmds
operator|.
name|getObjectId
argument_list|(
name|or
operator|.
name|repo
argument_list|,
name|refName
argument_list|)
argument_list|,
name|ObjectId
operator|.
name|zeroId
argument_list|()
argument_list|)
decl_stmt|;
name|ObjectId
name|curr
init|=
name|old
decl_stmt|;
for|for
control|(
name|AbstractChangeUpdate
name|u
range|:
name|updates
operator|.
name|get
argument_list|(
name|refName
argument_list|)
control|)
block|{
name|ObjectId
name|next
init|=
name|u
operator|.
name|apply
argument_list|(
name|or
operator|.
name|rw
argument_list|,
name|or
operator|.
name|ins
argument_list|,
name|curr
argument_list|)
decl_stmt|;
if|if
condition|(
name|next
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|curr
operator|=
name|next
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|old
operator|.
name|equals
argument_list|(
name|curr
argument_list|)
condition|)
block|{
name|or
operator|.
name|cmds
operator|.
name|add
argument_list|(
operator|new
name|ReceiveCommand
argument_list|(
name|old
argument_list|,
name|curr
argument_list|,
name|refName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

