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
DECL|package|com.google.gerrit.server.edit
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|edit
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
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|edit
operator|.
name|ChangeEditUtil
operator|.
name|editRefName
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
name|server
operator|.
name|edit
operator|.
name|ChangeEditUtil
operator|.
name|editRefPrefix
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
name|io
operator|.
name|ByteStreams
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
name|TimeUtil
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
name|extensions
operator|.
name|restapi
operator|.
name|RawInput
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
name|ResourceConflictException
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
name|project
operator|.
name|InvalidChangeOperationException
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
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|dircache
operator|.
name|DirCache
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
name|dircache
operator|.
name|DirCacheBuilder
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
name|dircache
operator|.
name|DirCacheEditor
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
name|dircache
operator|.
name|DirCacheEditor
operator|.
name|DeletePath
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
name|dircache
operator|.
name|DirCacheEditor
operator|.
name|PathEdit
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
name|dircache
operator|.
name|DirCacheEntry
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
name|FileMode
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
name|RefUpdate
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
name|merge
operator|.
name|MergeStrategy
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
name|merge
operator|.
name|ThreeWayMerger
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
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|treewalk
operator|.
name|TreeWalk
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
name|io
operator|.
name|InputStream
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
name|TimeZone
import|;
end_import

begin_comment
comment|/**  * Utility functions to manipulate change edits.  *<p>  * This class contains methods to modify edit's content.  * For retrieving, publishing and deleting edit see  * {@link ChangeEditUtil}.  *<p>  */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|ChangeEditModifier
specifier|public
class|class
name|ChangeEditModifier
block|{
DECL|enum|TreeOperation
specifier|private
specifier|static
enum|enum
name|TreeOperation
block|{
DECL|enumConstant|CHANGE_ENTRY
name|CHANGE_ENTRY
block|,
DECL|enumConstant|DELETE_ENTRY
name|DELETE_ENTRY
block|,
DECL|enumConstant|RESTORE_ENTRY
name|RESTORE_ENTRY
block|}
DECL|field|tz
specifier|private
specifier|final
name|TimeZone
name|tz
decl_stmt|;
DECL|field|gitManager
specifier|private
specifier|final
name|GitRepositoryManager
name|gitManager
decl_stmt|;
DECL|field|currentUser
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|currentUser
decl_stmt|;
annotation|@
name|Inject
DECL|method|ChangeEditModifier (@erritPersonIdent PersonIdent gerritIdent, GitRepositoryManager gitManager, Provider<CurrentUser> currentUser)
name|ChangeEditModifier
parameter_list|(
annotation|@
name|GerritPersonIdent
name|PersonIdent
name|gerritIdent
parameter_list|,
name|GitRepositoryManager
name|gitManager
parameter_list|,
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|currentUser
parameter_list|)
block|{
name|this
operator|.
name|gitManager
operator|=
name|gitManager
expr_stmt|;
name|this
operator|.
name|currentUser
operator|=
name|currentUser
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
comment|/**    * Create new change edit.    *    * @param change to create change edit for    * @param ps patch set to create change edit on    * @return result    * @throws AuthException    * @throws IOException    * @throws ResourceConflictException When change edit already    * exists for the change    */
DECL|method|createEdit (Change change, PatchSet ps)
specifier|public
name|RefUpdate
operator|.
name|Result
name|createEdit
parameter_list|(
name|Change
name|change
parameter_list|,
name|PatchSet
name|ps
parameter_list|)
throws|throws
name|AuthException
throws|,
name|IOException
throws|,
name|ResourceConflictException
block|{
if|if
condition|(
operator|!
name|currentUser
operator|.
name|get
argument_list|()
operator|.
name|isIdentifiedUser
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AuthException
argument_list|(
literal|"Authentication required"
argument_list|)
throw|;
block|}
name|IdentifiedUser
name|me
init|=
operator|(
name|IdentifiedUser
operator|)
name|currentUser
operator|.
name|get
argument_list|()
decl_stmt|;
name|Repository
name|repo
init|=
name|gitManager
operator|.
name|openRepository
argument_list|(
name|change
operator|.
name|getProject
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|refPrefix
init|=
name|editRefPrefix
argument_list|(
name|me
operator|.
name|getAccountId
argument_list|()
argument_list|,
name|change
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
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
name|getRefDatabase
argument_list|()
operator|.
name|getRefs
argument_list|(
name|refPrefix
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|refs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
literal|"edit already exists"
argument_list|)
throw|;
block|}
name|RevWalk
name|rw
init|=
operator|new
name|RevWalk
argument_list|(
name|repo
argument_list|)
decl_stmt|;
name|ObjectInserter
name|inserter
init|=
name|repo
operator|.
name|newObjectInserter
argument_list|()
decl_stmt|;
try|try
block|{
name|RevCommit
name|base
init|=
name|rw
operator|.
name|parseCommit
argument_list|(
name|ObjectId
operator|.
name|fromString
argument_list|(
name|ps
operator|.
name|getRevision
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|RevCommit
name|changeBase
init|=
name|base
operator|.
name|getParent
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|ObjectId
name|commit
init|=
name|createCommit
argument_list|(
name|me
argument_list|,
name|inserter
argument_list|,
name|base
argument_list|,
name|changeBase
argument_list|,
name|base
operator|.
name|getTree
argument_list|()
argument_list|)
decl_stmt|;
name|inserter
operator|.
name|flush
argument_list|()
expr_stmt|;
name|String
name|editRefName
init|=
name|editRefName
argument_list|(
name|me
operator|.
name|getAccountId
argument_list|()
argument_list|,
name|change
operator|.
name|getId
argument_list|()
argument_list|,
name|ps
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|update
argument_list|(
name|repo
argument_list|,
name|me
argument_list|,
name|editRefName
argument_list|,
name|rw
argument_list|,
name|ObjectId
operator|.
name|zeroId
argument_list|()
argument_list|,
name|commit
argument_list|)
return|;
block|}
finally|finally
block|{
name|rw
operator|.
name|release
argument_list|()
expr_stmt|;
name|inserter
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|repo
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Rebase change edit on latest patch set    *    * @param edit change edit that contains edit to rebase    * @param current patch set to rebase the edit on    * @throws AuthException    * @throws InvalidChangeOperationException    * @throws IOException    */
DECL|method|rebaseEdit (ChangeEdit edit, PatchSet current)
specifier|public
name|void
name|rebaseEdit
parameter_list|(
name|ChangeEdit
name|edit
parameter_list|,
name|PatchSet
name|current
parameter_list|)
throws|throws
name|AuthException
throws|,
name|InvalidChangeOperationException
throws|,
name|IOException
block|{
if|if
condition|(
operator|!
name|currentUser
operator|.
name|get
argument_list|()
operator|.
name|isIdentifiedUser
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AuthException
argument_list|(
literal|"Authentication required"
argument_list|)
throw|;
block|}
name|Change
name|change
init|=
name|edit
operator|.
name|getChange
argument_list|()
decl_stmt|;
name|IdentifiedUser
name|me
init|=
operator|(
name|IdentifiedUser
operator|)
name|currentUser
operator|.
name|get
argument_list|()
decl_stmt|;
name|String
name|refName
init|=
name|editRefName
argument_list|(
name|me
operator|.
name|getAccountId
argument_list|()
argument_list|,
name|change
operator|.
name|getId
argument_list|()
argument_list|,
name|current
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|Repository
name|repo
init|=
name|gitManager
operator|.
name|openRepository
argument_list|(
name|change
operator|.
name|getProject
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|RevWalk
name|rw
init|=
operator|new
name|RevWalk
argument_list|(
name|repo
argument_list|)
decl_stmt|;
name|BatchRefUpdate
name|ru
init|=
name|repo
operator|.
name|getRefDatabase
argument_list|()
operator|.
name|newBatchUpdate
argument_list|()
decl_stmt|;
name|ObjectInserter
name|inserter
init|=
name|repo
operator|.
name|newObjectInserter
argument_list|()
decl_stmt|;
try|try
block|{
name|RevCommit
name|editCommit
init|=
name|edit
operator|.
name|getEditCommit
argument_list|()
decl_stmt|;
if|if
condition|(
name|editCommit
operator|.
name|getParentCount
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|InvalidChangeOperationException
argument_list|(
literal|"Rebase edit against root commit not implemented"
argument_list|)
throw|;
block|}
name|RevCommit
name|tip
init|=
name|rw
operator|.
name|parseCommit
argument_list|(
name|ObjectId
operator|.
name|fromString
argument_list|(
name|current
operator|.
name|getRevision
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|ThreeWayMerger
name|m
init|=
name|MergeStrategy
operator|.
name|RESOLVE
operator|.
name|newMerger
argument_list|(
name|repo
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|m
operator|.
name|setObjectInserter
argument_list|(
name|inserter
argument_list|)
expr_stmt|;
name|m
operator|.
name|setBase
argument_list|(
name|ObjectId
operator|.
name|fromString
argument_list|(
name|edit
operator|.
name|getBasePatchSet
argument_list|()
operator|.
name|getRevision
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|m
operator|.
name|merge
argument_list|(
name|tip
argument_list|,
name|editCommit
argument_list|)
condition|)
block|{
name|ObjectId
name|tree
init|=
name|m
operator|.
name|getResultTreeId
argument_list|()
decl_stmt|;
name|CommitBuilder
name|commit
init|=
operator|new
name|CommitBuilder
argument_list|()
decl_stmt|;
name|commit
operator|.
name|setTreeId
argument_list|(
name|tree
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|tip
operator|.
name|getParentCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|commit
operator|.
name|addParentId
argument_list|(
name|tip
operator|.
name|getParent
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|commit
operator|.
name|setAuthor
argument_list|(
name|editCommit
operator|.
name|getAuthorIdent
argument_list|()
argument_list|)
expr_stmt|;
name|commit
operator|.
name|setCommitter
argument_list|(
operator|new
name|PersonIdent
argument_list|(
name|editCommit
operator|.
name|getCommitterIdent
argument_list|()
argument_list|,
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|commit
operator|.
name|setMessage
argument_list|(
name|editCommit
operator|.
name|getFullMessage
argument_list|()
argument_list|)
expr_stmt|;
name|ObjectId
name|newEdit
init|=
name|inserter
operator|.
name|insert
argument_list|(
name|commit
argument_list|)
decl_stmt|;
name|inserter
operator|.
name|flush
argument_list|()
expr_stmt|;
name|ru
operator|.
name|addCommand
argument_list|(
operator|new
name|ReceiveCommand
argument_list|(
name|ObjectId
operator|.
name|zeroId
argument_list|()
argument_list|,
name|newEdit
argument_list|,
name|refName
argument_list|)
argument_list|)
expr_stmt|;
name|ru
operator|.
name|addCommand
argument_list|(
operator|new
name|ReceiveCommand
argument_list|(
name|edit
operator|.
name|getRef
argument_list|()
operator|.
name|getObjectId
argument_list|()
argument_list|,
name|ObjectId
operator|.
name|zeroId
argument_list|()
argument_list|,
name|edit
operator|.
name|getRefName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|ru
operator|.
name|execute
argument_list|(
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
name|ru
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
literal|"failed: "
operator|+
name|cmd
argument_list|)
throw|;
block|}
block|}
block|}
else|else
block|{
comment|// TODO(davido): Allow to resolve conflicts inline
throw|throw
operator|new
name|InvalidChangeOperationException
argument_list|(
literal|"merge conflict"
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
name|rw
operator|.
name|release
argument_list|()
expr_stmt|;
name|inserter
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|repo
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Modify file in existing change edit from its base commit.    *    * @param edit change edit    * @param file path to modify    * @param content new content    * @return result    * @throws AuthException    * @throws InvalidChangeOperationException    * @throws IOException    */
DECL|method|modifyFile (ChangeEdit edit, String file, RawInput content)
specifier|public
name|RefUpdate
operator|.
name|Result
name|modifyFile
parameter_list|(
name|ChangeEdit
name|edit
parameter_list|,
name|String
name|file
parameter_list|,
name|RawInput
name|content
parameter_list|)
throws|throws
name|AuthException
throws|,
name|InvalidChangeOperationException
throws|,
name|IOException
block|{
return|return
name|modify
argument_list|(
name|TreeOperation
operator|.
name|CHANGE_ENTRY
argument_list|,
name|edit
argument_list|,
name|file
argument_list|,
name|content
argument_list|)
return|;
block|}
comment|/**    * Delete file in existing change edit.    *    * @param edit change edit    * @param file path to delete    * @return result    * @throws AuthException    * @throws InvalidChangeOperationException    * @throws IOException    */
DECL|method|deleteFile (ChangeEdit edit, String file)
specifier|public
name|RefUpdate
operator|.
name|Result
name|deleteFile
parameter_list|(
name|ChangeEdit
name|edit
parameter_list|,
name|String
name|file
parameter_list|)
throws|throws
name|AuthException
throws|,
name|InvalidChangeOperationException
throws|,
name|IOException
block|{
return|return
name|modify
argument_list|(
name|TreeOperation
operator|.
name|DELETE_ENTRY
argument_list|,
name|edit
argument_list|,
name|file
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * Restore file in existing change edit.    *    * @param edit change edit    * @param file path to restore    * @return result    * @throws AuthException    * @throws InvalidChangeOperationException    * @throws IOException    */
DECL|method|restoreFile (ChangeEdit edit, String file)
specifier|public
name|RefUpdate
operator|.
name|Result
name|restoreFile
parameter_list|(
name|ChangeEdit
name|edit
parameter_list|,
name|String
name|file
parameter_list|)
throws|throws
name|AuthException
throws|,
name|InvalidChangeOperationException
throws|,
name|IOException
block|{
return|return
name|modify
argument_list|(
name|TreeOperation
operator|.
name|RESTORE_ENTRY
argument_list|,
name|edit
argument_list|,
name|file
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|modify (TreeOperation op, ChangeEdit edit, String file, @Nullable RawInput content)
specifier|private
name|RefUpdate
operator|.
name|Result
name|modify
parameter_list|(
name|TreeOperation
name|op
parameter_list|,
name|ChangeEdit
name|edit
parameter_list|,
name|String
name|file
parameter_list|,
annotation|@
name|Nullable
name|RawInput
name|content
parameter_list|)
throws|throws
name|AuthException
throws|,
name|IOException
throws|,
name|InvalidChangeOperationException
block|{
if|if
condition|(
operator|!
name|currentUser
operator|.
name|get
argument_list|()
operator|.
name|isIdentifiedUser
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AuthException
argument_list|(
literal|"Authentication required"
argument_list|)
throw|;
block|}
name|IdentifiedUser
name|me
init|=
operator|(
name|IdentifiedUser
operator|)
name|currentUser
operator|.
name|get
argument_list|()
decl_stmt|;
name|Repository
name|repo
init|=
name|gitManager
operator|.
name|openRepository
argument_list|(
name|edit
operator|.
name|getChange
argument_list|()
operator|.
name|getProject
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|RevWalk
name|rw
init|=
operator|new
name|RevWalk
argument_list|(
name|repo
argument_list|)
decl_stmt|;
name|ObjectInserter
name|inserter
init|=
name|repo
operator|.
name|newObjectInserter
argument_list|()
decl_stmt|;
name|ObjectReader
name|reader
init|=
name|repo
operator|.
name|newObjectReader
argument_list|()
decl_stmt|;
try|try
block|{
name|String
name|refName
init|=
name|edit
operator|.
name|getRefName
argument_list|()
decl_stmt|;
name|RevCommit
name|prevEdit
init|=
name|edit
operator|.
name|getEditCommit
argument_list|()
decl_stmt|;
if|if
condition|(
name|prevEdit
operator|.
name|getParentCount
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|InvalidChangeOperationException
argument_list|(
literal|"Modify edit against root commit not implemented"
argument_list|)
throw|;
block|}
name|RevCommit
name|base
init|=
name|prevEdit
operator|.
name|getParent
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|base
operator|=
name|rw
operator|.
name|parseCommit
argument_list|(
name|base
argument_list|)
expr_stmt|;
name|ObjectId
name|newTree
init|=
name|writeNewTree
argument_list|(
name|op
argument_list|,
name|rw
argument_list|,
name|inserter
argument_list|,
name|prevEdit
argument_list|,
name|reader
argument_list|,
name|file
argument_list|,
name|toBlob
argument_list|(
name|inserter
argument_list|,
name|content
argument_list|)
argument_list|,
name|base
argument_list|)
decl_stmt|;
if|if
condition|(
name|ObjectId
operator|.
name|equals
argument_list|(
name|newTree
argument_list|,
name|prevEdit
operator|.
name|getTree
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidChangeOperationException
argument_list|(
literal|"no changes were made"
argument_list|)
throw|;
block|}
name|ObjectId
name|commit
init|=
name|createCommit
argument_list|(
name|me
argument_list|,
name|inserter
argument_list|,
name|prevEdit
argument_list|,
name|base
argument_list|,
name|newTree
argument_list|)
decl_stmt|;
name|inserter
operator|.
name|flush
argument_list|()
expr_stmt|;
return|return
name|update
argument_list|(
name|repo
argument_list|,
name|me
argument_list|,
name|refName
argument_list|,
name|rw
argument_list|,
name|prevEdit
argument_list|,
name|commit
argument_list|)
return|;
block|}
finally|finally
block|{
name|rw
operator|.
name|release
argument_list|()
expr_stmt|;
name|inserter
operator|.
name|release
argument_list|()
expr_stmt|;
name|reader
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|repo
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|toBlob (ObjectInserter ins, @Nullable RawInput content)
specifier|private
specifier|static
name|ObjectId
name|toBlob
parameter_list|(
name|ObjectInserter
name|ins
parameter_list|,
annotation|@
name|Nullable
name|RawInput
name|content
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|content
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|long
name|len
init|=
name|content
operator|.
name|getContentLength
argument_list|()
decl_stmt|;
name|InputStream
name|in
init|=
name|content
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
if|if
condition|(
name|len
operator|<
literal|0
condition|)
block|{
return|return
name|ins
operator|.
name|insert
argument_list|(
name|OBJ_BLOB
argument_list|,
name|ByteStreams
operator|.
name|toByteArray
argument_list|(
name|in
argument_list|)
argument_list|)
return|;
block|}
return|return
name|ins
operator|.
name|insert
argument_list|(
name|OBJ_BLOB
argument_list|,
name|len
argument_list|,
name|in
argument_list|)
return|;
block|}
DECL|method|createCommit (IdentifiedUser me, ObjectInserter inserter, RevCommit prevEdit, RevCommit base, ObjectId tree)
specifier|private
name|ObjectId
name|createCommit
parameter_list|(
name|IdentifiedUser
name|me
parameter_list|,
name|ObjectInserter
name|inserter
parameter_list|,
name|RevCommit
name|prevEdit
parameter_list|,
name|RevCommit
name|base
parameter_list|,
name|ObjectId
name|tree
parameter_list|)
throws|throws
name|IOException
block|{
name|CommitBuilder
name|builder
init|=
operator|new
name|CommitBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setTreeId
argument_list|(
name|tree
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setParentIds
argument_list|(
name|base
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setAuthor
argument_list|(
name|prevEdit
operator|.
name|getAuthorIdent
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setCommitter
argument_list|(
name|getCommitterIdent
argument_list|(
name|me
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setMessage
argument_list|(
name|prevEdit
operator|.
name|getFullMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|inserter
operator|.
name|insert
argument_list|(
name|builder
argument_list|)
return|;
block|}
DECL|method|update (Repository repo, IdentifiedUser me, String refName, RevWalk rw, ObjectId oldObjectId, ObjectId newEdit)
specifier|private
name|RefUpdate
operator|.
name|Result
name|update
parameter_list|(
name|Repository
name|repo
parameter_list|,
name|IdentifiedUser
name|me
parameter_list|,
name|String
name|refName
parameter_list|,
name|RevWalk
name|rw
parameter_list|,
name|ObjectId
name|oldObjectId
parameter_list|,
name|ObjectId
name|newEdit
parameter_list|)
throws|throws
name|IOException
block|{
name|RefUpdate
name|ru
init|=
name|repo
operator|.
name|updateRef
argument_list|(
name|refName
argument_list|)
decl_stmt|;
name|ru
operator|.
name|setExpectedOldObjectId
argument_list|(
name|oldObjectId
argument_list|)
expr_stmt|;
name|ru
operator|.
name|setNewObjectId
argument_list|(
name|newEdit
argument_list|)
expr_stmt|;
name|ru
operator|.
name|setRefLogIdent
argument_list|(
name|getRefLogIdent
argument_list|(
name|me
argument_list|)
argument_list|)
expr_stmt|;
name|ru
operator|.
name|setForceUpdate
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|RefUpdate
operator|.
name|Result
name|res
init|=
name|ru
operator|.
name|update
argument_list|(
name|rw
argument_list|)
decl_stmt|;
if|if
condition|(
name|res
operator|!=
name|RefUpdate
operator|.
name|Result
operator|.
name|NEW
operator|&&
name|res
operator|!=
name|RefUpdate
operator|.
name|Result
operator|.
name|FORCED
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"update failed: "
operator|+
name|ru
argument_list|)
throw|;
block|}
return|return
name|res
return|;
block|}
DECL|method|writeNewTree (TreeOperation op, RevWalk rw, ObjectInserter ins, RevCommit prevEdit, ObjectReader reader, String fileName, final @Nullable ObjectId content, RevCommit base)
specifier|private
specifier|static
name|ObjectId
name|writeNewTree
parameter_list|(
name|TreeOperation
name|op
parameter_list|,
name|RevWalk
name|rw
parameter_list|,
name|ObjectInserter
name|ins
parameter_list|,
name|RevCommit
name|prevEdit
parameter_list|,
name|ObjectReader
name|reader
parameter_list|,
name|String
name|fileName
parameter_list|,
specifier|final
annotation|@
name|Nullable
name|ObjectId
name|content
parameter_list|,
name|RevCommit
name|base
parameter_list|)
throws|throws
name|IOException
throws|,
name|InvalidChangeOperationException
block|{
name|DirCache
name|newTree
init|=
name|readTree
argument_list|(
name|reader
argument_list|,
name|prevEdit
argument_list|)
decl_stmt|;
name|DirCacheEditor
name|dce
init|=
name|newTree
operator|.
name|editor
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|op
condition|)
block|{
case|case
name|DELETE_ENTRY
case|:
name|dce
operator|.
name|add
argument_list|(
operator|new
name|DeletePath
argument_list|(
name|fileName
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|CHANGE_ENTRY
case|:
name|checkNotNull
argument_list|(
literal|"new content required"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|dce
operator|.
name|add
argument_list|(
operator|new
name|PathEdit
argument_list|(
name|fileName
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|apply
parameter_list|(
name|DirCacheEntry
name|ent
parameter_list|)
block|{
if|if
condition|(
name|ent
operator|.
name|getRawMode
argument_list|()
operator|==
literal|0
condition|)
block|{
name|ent
operator|.
name|setFileMode
argument_list|(
name|FileMode
operator|.
name|REGULAR_FILE
argument_list|)
expr_stmt|;
block|}
name|ent
operator|.
name|setObjectId
argument_list|(
name|content
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
break|break;
case|case
name|RESTORE_ENTRY
case|:
name|TreeWalk
name|tw
init|=
name|TreeWalk
operator|.
name|forPath
argument_list|(
name|rw
operator|.
name|getObjectReader
argument_list|()
argument_list|,
name|fileName
argument_list|,
name|base
operator|.
name|getTree
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
comment|// If the file does not exist in the base commit, try to restore it
comment|// from the base's parent commit.
if|if
condition|(
name|tw
operator|==
literal|null
operator|&&
name|base
operator|.
name|getParentCount
argument_list|()
operator|==
literal|1
condition|)
block|{
name|tw
operator|=
name|TreeWalk
operator|.
name|forPath
argument_list|(
name|rw
operator|.
name|getObjectReader
argument_list|()
argument_list|,
name|fileName
argument_list|,
name|rw
operator|.
name|parseCommit
argument_list|(
name|base
operator|.
name|getParent
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|getTree
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tw
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InvalidChangeOperationException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"cannot restore path %s: missing in base revision %s"
argument_list|,
name|fileName
argument_list|,
name|base
operator|.
name|abbreviate
argument_list|(
literal|8
argument_list|)
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
specifier|final
name|FileMode
name|mode
init|=
name|tw
operator|.
name|getFileMode
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|ObjectId
name|oid
init|=
name|tw
operator|.
name|getObjectId
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|dce
operator|.
name|add
argument_list|(
operator|new
name|PathEdit
argument_list|(
name|fileName
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|apply
parameter_list|(
name|DirCacheEntry
name|ent
parameter_list|)
block|{
name|ent
operator|.
name|setFileMode
argument_list|(
name|mode
argument_list|)
expr_stmt|;
name|ent
operator|.
name|setObjectId
argument_list|(
name|oid
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
break|break;
block|}
name|dce
operator|.
name|finish
argument_list|()
expr_stmt|;
return|return
name|newTree
operator|.
name|writeTree
argument_list|(
name|ins
argument_list|)
return|;
block|}
DECL|method|readTree (ObjectReader reader, RevCommit prevEdit)
specifier|private
specifier|static
name|DirCache
name|readTree
parameter_list|(
name|ObjectReader
name|reader
parameter_list|,
name|RevCommit
name|prevEdit
parameter_list|)
throws|throws
name|IOException
block|{
name|DirCache
name|dc
init|=
name|DirCache
operator|.
name|newInCore
argument_list|()
decl_stmt|;
name|DirCacheBuilder
name|b
init|=
name|dc
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b
operator|.
name|addTree
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|,
name|DirCacheEntry
operator|.
name|STAGE_0
argument_list|,
name|reader
argument_list|,
name|prevEdit
operator|.
name|getTree
argument_list|()
argument_list|)
expr_stmt|;
name|b
operator|.
name|finish
argument_list|()
expr_stmt|;
return|return
name|dc
return|;
block|}
DECL|method|getCommitterIdent (IdentifiedUser user)
specifier|private
name|PersonIdent
name|getCommitterIdent
parameter_list|(
name|IdentifiedUser
name|user
parameter_list|)
block|{
return|return
name|user
operator|.
name|newCommitterIdent
argument_list|(
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|,
name|tz
argument_list|)
return|;
block|}
DECL|method|getRefLogIdent (IdentifiedUser user)
specifier|private
name|PersonIdent
name|getRefLogIdent
parameter_list|(
name|IdentifiedUser
name|user
parameter_list|)
block|{
return|return
name|user
operator|.
name|newRefLogIdent
argument_list|(
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|,
name|tz
argument_list|)
return|;
block|}
block|}
end_class

end_unit

