begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2012 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.git
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|git
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
name|reviewdb
operator|.
name|client
operator|.
name|RefNames
operator|.
name|REFS_REJECT_COMMITS
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
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|extensions
operator|.
name|restapi
operator|.
name|AuthException
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
name|permissions
operator|.
name|PermissionBackend
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
name|permissions
operator|.
name|PermissionBackendException
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
name|permissions
operator|.
name|ProjectPermission
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
name|Provider
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
name|Date
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
name|TimeZone
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
name|IncorrectObjectTypeException
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
name|MissingObjectException
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

begin_class
annotation|@
name|Singleton
DECL|class|BanCommit
specifier|public
class|class
name|BanCommit
block|{
comment|/**    * Loads a list of commits to reject from {@code refs/meta/reject-commits}.    *    * @param repo repository from which the rejected commits should be loaded    * @param walk open revwalk on repo.    * @return NoteMap of commits to be rejected, null if there are none.    * @throws IOException the map cannot be loaded.    */
DECL|method|loadRejectCommitsMap (Repository repo, RevWalk walk)
specifier|public
specifier|static
name|NoteMap
name|loadRejectCommitsMap
parameter_list|(
name|Repository
name|repo
parameter_list|,
name|RevWalk
name|walk
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|Ref
name|ref
init|=
name|repo
operator|.
name|getRefDatabase
argument_list|()
operator|.
name|exactRef
argument_list|(
name|RefNames
operator|.
name|REFS_REJECT_COMMITS
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
name|NoteMap
operator|.
name|newEmptyMap
argument_list|()
return|;
block|}
name|RevCommit
name|map
init|=
name|walk
operator|.
name|parseCommit
argument_list|(
name|ref
operator|.
name|getObjectId
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|NoteMap
operator|.
name|read
argument_list|(
name|walk
operator|.
name|getObjectReader
argument_list|()
argument_list|,
name|map
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|badMap
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot load "
operator|+
name|RefNames
operator|.
name|REFS_REJECT_COMMITS
argument_list|,
name|badMap
argument_list|)
throw|;
block|}
block|}
DECL|field|currentUser
specifier|private
specifier|final
name|Provider
argument_list|<
name|IdentifiedUser
argument_list|>
name|currentUser
decl_stmt|;
DECL|field|repoManager
specifier|private
specifier|final
name|GitRepositoryManager
name|repoManager
decl_stmt|;
DECL|field|tz
specifier|private
specifier|final
name|TimeZone
name|tz
decl_stmt|;
DECL|field|permissionBackend
specifier|private
specifier|final
name|PermissionBackend
name|permissionBackend
decl_stmt|;
DECL|field|notesBranchUtilFactory
specifier|private
specifier|final
name|NotesBranchUtil
operator|.
name|Factory
name|notesBranchUtilFactory
decl_stmt|;
annotation|@
name|Inject
DECL|method|BanCommit ( Provider<IdentifiedUser> currentUser, GitRepositoryManager repoManager, @GerritPersonIdent PersonIdent gerritIdent, NotesBranchUtil.Factory notesBranchUtilFactory, PermissionBackend permissionBackend)
name|BanCommit
parameter_list|(
name|Provider
argument_list|<
name|IdentifiedUser
argument_list|>
name|currentUser
parameter_list|,
name|GitRepositoryManager
name|repoManager
parameter_list|,
annotation|@
name|GerritPersonIdent
name|PersonIdent
name|gerritIdent
parameter_list|,
name|NotesBranchUtil
operator|.
name|Factory
name|notesBranchUtilFactory
parameter_list|,
name|PermissionBackend
name|permissionBackend
parameter_list|)
block|{
name|this
operator|.
name|currentUser
operator|=
name|currentUser
expr_stmt|;
name|this
operator|.
name|repoManager
operator|=
name|repoManager
expr_stmt|;
name|this
operator|.
name|notesBranchUtilFactory
operator|=
name|notesBranchUtilFactory
expr_stmt|;
name|this
operator|.
name|permissionBackend
operator|=
name|permissionBackend
expr_stmt|;
name|this
operator|.
name|tz
operator|=
name|gerritIdent
operator|.
name|getTimeZone
argument_list|()
expr_stmt|;
block|}
DECL|method|ban ( Project.NameKey project, CurrentUser user, List<ObjectId> commitsToBan, String reason)
specifier|public
name|BanCommitResult
name|ban
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
name|CurrentUser
name|user
parameter_list|,
name|List
argument_list|<
name|ObjectId
argument_list|>
name|commitsToBan
parameter_list|,
name|String
name|reason
parameter_list|)
throws|throws
name|AuthException
throws|,
name|LockFailureException
throws|,
name|IOException
throws|,
name|PermissionBackendException
block|{
name|permissionBackend
operator|.
name|user
argument_list|(
name|user
argument_list|)
operator|.
name|project
argument_list|(
name|project
argument_list|)
operator|.
name|check
argument_list|(
name|ProjectPermission
operator|.
name|BAN_COMMIT
argument_list|)
expr_stmt|;
specifier|final
name|BanCommitResult
name|result
init|=
operator|new
name|BanCommitResult
argument_list|()
decl_stmt|;
name|NoteMap
name|banCommitNotes
init|=
name|NoteMap
operator|.
name|newEmptyMap
argument_list|()
decl_stmt|;
comment|// Add a note for each banned commit to notes.
try|try
init|(
name|Repository
name|repo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|project
argument_list|)
init|;
name|RevWalk
name|revWalk
operator|=
operator|new
name|RevWalk
argument_list|(
name|repo
argument_list|)
init|;
name|ObjectInserter
name|inserter
operator|=
name|repo
operator|.
name|newObjectInserter
argument_list|()
init|)
block|{
name|ObjectId
name|noteId
init|=
literal|null
decl_stmt|;
for|for
control|(
name|ObjectId
name|commitToBan
range|:
name|commitsToBan
control|)
block|{
try|try
block|{
name|revWalk
operator|.
name|parseCommit
argument_list|(
name|commitToBan
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MissingObjectException
name|e
parameter_list|)
block|{
comment|// Ignore exception, non-existing commits can be banned.
block|}
catch|catch
parameter_list|(
name|IncorrectObjectTypeException
name|e
parameter_list|)
block|{
name|result
operator|.
name|notACommit
argument_list|(
name|commitToBan
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|noteId
operator|==
literal|null
condition|)
block|{
name|noteId
operator|=
name|createNoteContent
argument_list|(
name|reason
argument_list|,
name|inserter
argument_list|)
expr_stmt|;
block|}
name|banCommitNotes
operator|.
name|set
argument_list|(
name|commitToBan
argument_list|,
name|noteId
argument_list|)
expr_stmt|;
block|}
name|NotesBranchUtil
name|notesBranchUtil
init|=
name|notesBranchUtilFactory
operator|.
name|create
argument_list|(
name|project
argument_list|,
name|repo
argument_list|,
name|inserter
argument_list|)
decl_stmt|;
name|NoteMap
name|newlyCreated
init|=
name|notesBranchUtil
operator|.
name|commitNewNotes
argument_list|(
name|banCommitNotes
argument_list|,
name|REFS_REJECT_COMMITS
argument_list|,
name|createPersonIdent
argument_list|()
argument_list|,
name|buildCommitMessage
argument_list|(
name|commitsToBan
argument_list|,
name|reason
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|Note
name|n
range|:
name|banCommitNotes
control|)
block|{
if|if
condition|(
name|newlyCreated
operator|.
name|contains
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|result
operator|.
name|commitBanned
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|.
name|commitAlreadyBanned
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
block|}
DECL|method|createNoteContent (String reason, ObjectInserter inserter)
specifier|private
name|ObjectId
name|createNoteContent
parameter_list|(
name|String
name|reason
parameter_list|,
name|ObjectInserter
name|inserter
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|noteContent
init|=
name|reason
operator|!=
literal|null
condition|?
name|reason
else|:
literal|""
decl_stmt|;
if|if
condition|(
name|noteContent
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
operator|!
name|noteContent
operator|.
name|endsWith
argument_list|(
literal|"\n"
argument_list|)
condition|)
block|{
name|noteContent
operator|=
name|noteContent
operator|+
literal|"\n"
expr_stmt|;
block|}
return|return
name|inserter
operator|.
name|insert
argument_list|(
name|Constants
operator|.
name|OBJ_BLOB
argument_list|,
name|noteContent
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
return|;
block|}
DECL|method|createPersonIdent ()
specifier|private
name|PersonIdent
name|createPersonIdent
parameter_list|()
block|{
name|Date
name|now
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
return|return
name|currentUser
operator|.
name|get
argument_list|()
operator|.
name|newCommitterIdent
argument_list|(
name|now
argument_list|,
name|tz
argument_list|)
return|;
block|}
DECL|method|buildCommitMessage (List<ObjectId> bannedCommits, String reason)
specifier|private
specifier|static
name|String
name|buildCommitMessage
parameter_list|(
name|List
argument_list|<
name|ObjectId
argument_list|>
name|bannedCommits
parameter_list|,
name|String
name|reason
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|commitMsg
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|commitMsg
operator|.
name|append
argument_list|(
literal|"Banning "
argument_list|)
expr_stmt|;
name|commitMsg
operator|.
name|append
argument_list|(
name|bannedCommits
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|commitMsg
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|commitMsg
operator|.
name|append
argument_list|(
name|bannedCommits
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|?
literal|"commit"
else|:
literal|"commits"
argument_list|)
expr_stmt|;
name|commitMsg
operator|.
name|append
argument_list|(
literal|"\n\n"
argument_list|)
expr_stmt|;
if|if
condition|(
name|reason
operator|!=
literal|null
condition|)
block|{
name|commitMsg
operator|.
name|append
argument_list|(
literal|"Reason: "
argument_list|)
expr_stmt|;
name|commitMsg
operator|.
name|append
argument_list|(
name|reason
argument_list|)
expr_stmt|;
name|commitMsg
operator|.
name|append
argument_list|(
literal|"\n\n"
argument_list|)
expr_stmt|;
block|}
name|commitMsg
operator|.
name|append
argument_list|(
literal|"The following commits are banned:\n"
argument_list|)
expr_stmt|;
specifier|final
name|StringBuilder
name|commitList
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|ObjectId
name|c
range|:
name|bannedCommits
control|)
block|{
if|if
condition|(
name|commitList
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|commitList
operator|.
name|append
argument_list|(
literal|",\n"
argument_list|)
expr_stmt|;
block|}
name|commitList
operator|.
name|append
argument_list|(
name|c
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|commitMsg
operator|.
name|append
argument_list|(
name|commitList
argument_list|)
expr_stmt|;
return|return
name|commitMsg
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

