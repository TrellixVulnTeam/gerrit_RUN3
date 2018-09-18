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
name|checkArgument
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
name|extensions
operator|.
name|api
operator|.
name|changes
operator|.
name|NotifyHandling
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
name|changes
operator|.
name|RecipientType
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
name|client
operator|.
name|ChangeKind
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
name|extensions
operator|.
name|restapi
operator|.
name|RestApiException
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
name|ChangeUtil
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
name|PatchSetUtil
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
name|change
operator|.
name|ChangeKindCache
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
name|change
operator|.
name|PatchSetInserter
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
name|index
operator|.
name|change
operator|.
name|ChangeIndexer
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
name|ChangeNotes
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
name|update
operator|.
name|BatchUpdate
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
name|update
operator|.
name|BatchUpdateOp
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
name|update
operator|.
name|RepoContext
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
name|update
operator|.
name|UpdateException
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
name|util
operator|.
name|time
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
comment|/**  * Utility functions to manipulate change edits.  *  *<p>This class contains methods to retrieve, publish and delete edits. For changing edits see  * {@link ChangeEditModifier}.  */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|ChangeEditUtil
specifier|public
class|class
name|ChangeEditUtil
block|{
DECL|field|gitManager
specifier|private
specifier|final
name|GitRepositoryManager
name|gitManager
decl_stmt|;
DECL|field|patchSetInserterFactory
specifier|private
specifier|final
name|PatchSetInserter
operator|.
name|Factory
name|patchSetInserterFactory
decl_stmt|;
DECL|field|indexer
specifier|private
specifier|final
name|ChangeIndexer
name|indexer
decl_stmt|;
DECL|field|db
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
decl_stmt|;
DECL|field|userProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|userProvider
decl_stmt|;
DECL|field|changeKindCache
specifier|private
specifier|final
name|ChangeKindCache
name|changeKindCache
decl_stmt|;
DECL|field|psUtil
specifier|private
specifier|final
name|PatchSetUtil
name|psUtil
decl_stmt|;
annotation|@
name|Inject
DECL|method|ChangeEditUtil ( GitRepositoryManager gitManager, PatchSetInserter.Factory patchSetInserterFactory, ChangeIndexer indexer, Provider<ReviewDb> db, Provider<CurrentUser> userProvider, ChangeKindCache changeKindCache, PatchSetUtil psUtil)
name|ChangeEditUtil
parameter_list|(
name|GitRepositoryManager
name|gitManager
parameter_list|,
name|PatchSetInserter
operator|.
name|Factory
name|patchSetInserterFactory
parameter_list|,
name|ChangeIndexer
name|indexer
parameter_list|,
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|,
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|userProvider
parameter_list|,
name|ChangeKindCache
name|changeKindCache
parameter_list|,
name|PatchSetUtil
name|psUtil
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
name|patchSetInserterFactory
operator|=
name|patchSetInserterFactory
expr_stmt|;
name|this
operator|.
name|indexer
operator|=
name|indexer
expr_stmt|;
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|userProvider
operator|=
name|userProvider
expr_stmt|;
name|this
operator|.
name|changeKindCache
operator|=
name|changeKindCache
expr_stmt|;
name|this
operator|.
name|psUtil
operator|=
name|psUtil
expr_stmt|;
block|}
comment|/**    * Retrieve edit for a given change.    *    *<p>At most one change edit can exist per user and change.    *    * @param notes change notes of change to retrieve change edits for.    * @return edit for this change for this user, if present.    * @throws AuthException if this is not a logged-in user.    * @throws IOException if an error occurs.    */
DECL|method|byChange (ChangeNotes notes)
specifier|public
name|Optional
argument_list|<
name|ChangeEdit
argument_list|>
name|byChange
parameter_list|(
name|ChangeNotes
name|notes
parameter_list|)
throws|throws
name|AuthException
throws|,
name|IOException
block|{
return|return
name|byChange
argument_list|(
name|notes
argument_list|,
name|userProvider
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Retrieve edit for a change and the given user.    *    *<p>At most one change edit can exist per user and change.    *    * @param notes change notes of change to retrieve change edits for.    * @param user user to retrieve edits as.    * @return edit for this change for this user, if present.    * @throws AuthException if this is not a logged-in user.    * @throws IOException if an error occurs.    */
DECL|method|byChange (ChangeNotes notes, CurrentUser user)
specifier|public
name|Optional
argument_list|<
name|ChangeEdit
argument_list|>
name|byChange
parameter_list|(
name|ChangeNotes
name|notes
parameter_list|,
name|CurrentUser
name|user
parameter_list|)
throws|throws
name|AuthException
throws|,
name|IOException
block|{
if|if
condition|(
operator|!
name|user
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
name|u
init|=
name|user
operator|.
name|asIdentifiedUser
argument_list|()
decl_stmt|;
name|Change
name|change
init|=
name|notes
operator|.
name|getChange
argument_list|()
decl_stmt|;
try|try
init|(
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
init|)
block|{
name|int
name|n
init|=
name|change
operator|.
name|currentPatchSetId
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
name|String
index|[]
name|refNames
init|=
operator|new
name|String
index|[
name|n
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|n
init|;
name|i
operator|>
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|refNames
index|[
name|i
operator|-
literal|1
index|]
operator|=
name|RefNames
operator|.
name|refsEdit
argument_list|(
name|u
operator|.
name|getAccountId
argument_list|()
argument_list|,
name|change
operator|.
name|getId
argument_list|()
argument_list|,
operator|new
name|PatchSet
operator|.
name|Id
argument_list|(
name|change
operator|.
name|getId
argument_list|()
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Ref
name|ref
init|=
name|repo
operator|.
name|getRefDatabase
argument_list|()
operator|.
name|firstExactRef
argument_list|(
name|refNames
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
name|Optional
operator|.
name|empty
argument_list|()
return|;
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
name|commit
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
name|PatchSet
name|basePs
init|=
name|getBasePatchSet
argument_list|(
name|notes
argument_list|,
name|ref
argument_list|)
decl_stmt|;
return|return
name|Optional
operator|.
name|of
argument_list|(
operator|new
name|ChangeEdit
argument_list|(
name|change
argument_list|,
name|ref
operator|.
name|getName
argument_list|()
argument_list|,
name|commit
argument_list|,
name|basePs
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
comment|/**    * Promote change edit to patch set, by squashing the edit into its parent.    *    * @param updateFactory factory for creating updates.    * @param notes the {@code ChangeNotes} of the change to which the change edit belongs    * @param user the current user    * @param edit change edit to publish    * @param notify Notify handling that defines to whom email notifications should be sent after the    *     change edit is published.    * @param accountsToNotify Accounts that should be notified after the change edit is published.    * @throws IOException    * @throws OrmException    * @throws UpdateException    * @throws RestApiException    */
DECL|method|publish ( BatchUpdate.Factory updateFactory, ChangeNotes notes, CurrentUser user, final ChangeEdit edit, NotifyHandling notify, ListMultimap<RecipientType, Account.Id> accountsToNotify)
specifier|public
name|void
name|publish
parameter_list|(
name|BatchUpdate
operator|.
name|Factory
name|updateFactory
parameter_list|,
name|ChangeNotes
name|notes
parameter_list|,
name|CurrentUser
name|user
parameter_list|,
specifier|final
name|ChangeEdit
name|edit
parameter_list|,
name|NotifyHandling
name|notify
parameter_list|,
name|ListMultimap
argument_list|<
name|RecipientType
argument_list|,
name|Account
operator|.
name|Id
argument_list|>
name|accountsToNotify
parameter_list|)
throws|throws
name|IOException
throws|,
name|OrmException
throws|,
name|RestApiException
throws|,
name|UpdateException
block|{
name|Change
name|change
init|=
name|edit
operator|.
name|getChange
argument_list|()
decl_stmt|;
try|try
init|(
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
init|;
name|ObjectInserter
name|oi
operator|=
name|repo
operator|.
name|newObjectInserter
argument_list|()
init|;
name|ObjectReader
name|reader
operator|=
name|oi
operator|.
name|newReader
argument_list|()
init|;
name|RevWalk
name|rw
operator|=
operator|new
name|RevWalk
argument_list|(
name|reader
argument_list|)
init|)
block|{
name|PatchSet
name|basePatchSet
init|=
name|edit
operator|.
name|getBasePatchSet
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|basePatchSet
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
name|change
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
literal|"only edit for current patch set can be published"
argument_list|)
throw|;
block|}
name|RevCommit
name|squashed
init|=
name|squashEdit
argument_list|(
name|rw
argument_list|,
name|oi
argument_list|,
name|edit
operator|.
name|getEditCommit
argument_list|()
argument_list|,
name|basePatchSet
argument_list|)
decl_stmt|;
name|PatchSet
operator|.
name|Id
name|psId
init|=
name|ChangeUtil
operator|.
name|nextPatchSetId
argument_list|(
name|repo
argument_list|,
name|change
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
decl_stmt|;
name|PatchSetInserter
name|inserter
init|=
name|patchSetInserterFactory
operator|.
name|create
argument_list|(
name|notes
argument_list|,
name|psId
argument_list|,
name|squashed
argument_list|)
operator|.
name|setNotify
argument_list|(
name|notify
argument_list|)
operator|.
name|setAccountsToNotify
argument_list|(
name|accountsToNotify
argument_list|)
decl_stmt|;
name|StringBuilder
name|message
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"Patch Set "
argument_list|)
operator|.
name|append
argument_list|(
name|inserter
operator|.
name|getPatchSetId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
decl_stmt|;
comment|// Previously checked that the base patch set is the current patch set.
name|ObjectId
name|prior
init|=
name|ObjectId
operator|.
name|fromString
argument_list|(
name|basePatchSet
operator|.
name|getRevision
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
name|ChangeKind
name|kind
init|=
name|changeKindCache
operator|.
name|getChangeKind
argument_list|(
name|change
operator|.
name|getProject
argument_list|()
argument_list|,
name|rw
argument_list|,
name|repo
operator|.
name|getConfig
argument_list|()
argument_list|,
name|prior
argument_list|,
name|squashed
argument_list|)
decl_stmt|;
if|if
condition|(
name|kind
operator|==
name|ChangeKind
operator|.
name|NO_CODE_CHANGE
condition|)
block|{
name|message
operator|.
name|append
argument_list|(
literal|"Commit message was updated."
argument_list|)
expr_stmt|;
name|inserter
operator|.
name|setDescription
argument_list|(
literal|"Edit commit message"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|message
operator|.
name|append
argument_list|(
literal|"Published edit on patch set "
argument_list|)
operator|.
name|append
argument_list|(
name|basePatchSet
operator|.
name|getPatchSetId
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
block|}
try|try
init|(
name|BatchUpdate
name|bu
init|=
name|updateFactory
operator|.
name|create
argument_list|(
name|db
operator|.
name|get
argument_list|()
argument_list|,
name|change
operator|.
name|getProject
argument_list|()
argument_list|,
name|user
argument_list|,
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
init|)
block|{
name|bu
operator|.
name|setRepository
argument_list|(
name|repo
argument_list|,
name|rw
argument_list|,
name|oi
argument_list|)
expr_stmt|;
name|bu
operator|.
name|addOp
argument_list|(
name|change
operator|.
name|getId
argument_list|()
argument_list|,
name|inserter
operator|.
name|setMessage
argument_list|(
name|message
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|bu
operator|.
name|addOp
argument_list|(
name|change
operator|.
name|getId
argument_list|()
argument_list|,
operator|new
name|BatchUpdateOp
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|updateRepo
parameter_list|(
name|RepoContext
name|ctx
parameter_list|)
throws|throws
name|Exception
block|{
name|ctx
operator|.
name|addRefUpdate
argument_list|(
name|edit
operator|.
name|getEditCommit
argument_list|()
operator|.
name|copy
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
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|bu
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Delete change edit.    *    * @param edit change edit to delete    * @throws IOException    * @throws OrmException    */
DECL|method|delete (ChangeEdit edit)
specifier|public
name|void
name|delete
parameter_list|(
name|ChangeEdit
name|edit
parameter_list|)
throws|throws
name|IOException
throws|,
name|OrmException
block|{
name|Change
name|change
init|=
name|edit
operator|.
name|getChange
argument_list|()
decl_stmt|;
try|try
init|(
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
init|)
block|{
name|deleteRef
argument_list|(
name|repo
argument_list|,
name|edit
argument_list|)
expr_stmt|;
block|}
name|indexer
operator|.
name|index
argument_list|(
name|db
operator|.
name|get
argument_list|()
argument_list|,
name|change
argument_list|)
expr_stmt|;
block|}
DECL|method|getBasePatchSet (ChangeNotes notes, Ref ref)
specifier|private
name|PatchSet
name|getBasePatchSet
parameter_list|(
name|ChangeNotes
name|notes
parameter_list|,
name|Ref
name|ref
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|int
name|pos
init|=
name|ref
operator|.
name|getName
argument_list|()
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
name|checkArgument
argument_list|(
name|pos
operator|>
literal|0
argument_list|,
literal|"invalid edit ref: %s"
argument_list|,
name|ref
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|psId
init|=
name|ref
operator|.
name|getName
argument_list|()
operator|.
name|substring
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
decl_stmt|;
return|return
name|psUtil
operator|.
name|get
argument_list|(
name|db
operator|.
name|get
argument_list|()
argument_list|,
name|notes
argument_list|,
operator|new
name|PatchSet
operator|.
name|Id
argument_list|(
name|notes
operator|.
name|getChange
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|psId
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|OrmException
decl||
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|squashEdit ( RevWalk rw, ObjectInserter inserter, RevCommit edit, PatchSet basePatchSet)
specifier|private
name|RevCommit
name|squashEdit
parameter_list|(
name|RevWalk
name|rw
parameter_list|,
name|ObjectInserter
name|inserter
parameter_list|,
name|RevCommit
name|edit
parameter_list|,
name|PatchSet
name|basePatchSet
parameter_list|)
throws|throws
name|IOException
throws|,
name|ResourceConflictException
block|{
name|RevCommit
name|parent
init|=
name|rw
operator|.
name|parseCommit
argument_list|(
name|ObjectId
operator|.
name|fromString
argument_list|(
name|basePatchSet
operator|.
name|getRevision
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|parent
operator|.
name|getTree
argument_list|()
operator|.
name|equals
argument_list|(
name|edit
operator|.
name|getTree
argument_list|()
argument_list|)
operator|&&
name|edit
operator|.
name|getFullMessage
argument_list|()
operator|.
name|equals
argument_list|(
name|parent
operator|.
name|getFullMessage
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
literal|"identical tree and message"
argument_list|)
throw|;
block|}
return|return
name|writeSquashedCommit
argument_list|(
name|rw
argument_list|,
name|inserter
argument_list|,
name|parent
argument_list|,
name|edit
argument_list|)
return|;
block|}
DECL|method|deleteRef (Repository repo, ChangeEdit edit)
specifier|private
specifier|static
name|void
name|deleteRef
parameter_list|(
name|Repository
name|repo
parameter_list|,
name|ChangeEdit
name|edit
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|refName
init|=
name|edit
operator|.
name|getRefName
argument_list|()
decl_stmt|;
name|RefUpdate
name|ru
init|=
name|repo
operator|.
name|updateRef
argument_list|(
name|refName
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|ru
operator|.
name|setExpectedOldObjectId
argument_list|(
name|edit
operator|.
name|getEditCommit
argument_list|()
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
name|result
init|=
name|ru
operator|.
name|delete
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|result
condition|)
block|{
case|case
name|FORCED
case|:
case|case
name|NEW
case|:
case|case
name|NO_CHANGE
case|:
break|break;
case|case
name|FAST_FORWARD
case|:
case|case
name|IO_FAILURE
case|:
case|case
name|LOCK_FAILURE
case|:
case|case
name|NOT_ATTEMPTED
case|:
case|case
name|REJECTED
case|:
case|case
name|REJECTED_CURRENT_BRANCH
case|:
case|case
name|RENAMED
case|:
case|case
name|REJECTED_MISSING_OBJECT
case|:
case|case
name|REJECTED_OTHER_REASON
case|:
default|default:
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Failed to delete ref %s: %s"
argument_list|,
name|refName
argument_list|,
name|result
argument_list|)
argument_list|)
throw|;
block|}
block|}
DECL|method|writeSquashedCommit ( RevWalk rw, ObjectInserter inserter, RevCommit parent, RevCommit edit)
specifier|private
specifier|static
name|RevCommit
name|writeSquashedCommit
parameter_list|(
name|RevWalk
name|rw
parameter_list|,
name|ObjectInserter
name|inserter
parameter_list|,
name|RevCommit
name|parent
parameter_list|,
name|RevCommit
name|edit
parameter_list|)
throws|throws
name|IOException
block|{
name|CommitBuilder
name|mergeCommit
init|=
operator|new
name|CommitBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|parent
operator|.
name|getParentCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|mergeCommit
operator|.
name|addParentId
argument_list|(
name|parent
operator|.
name|getParent
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|mergeCommit
operator|.
name|setAuthor
argument_list|(
name|parent
operator|.
name|getAuthorIdent
argument_list|()
argument_list|)
expr_stmt|;
name|mergeCommit
operator|.
name|setMessage
argument_list|(
name|edit
operator|.
name|getFullMessage
argument_list|()
argument_list|)
expr_stmt|;
name|mergeCommit
operator|.
name|setCommitter
argument_list|(
name|edit
operator|.
name|getCommitterIdent
argument_list|()
argument_list|)
expr_stmt|;
name|mergeCommit
operator|.
name|setTreeId
argument_list|(
name|edit
operator|.
name|getTree
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|rw
operator|.
name|parseCommit
argument_list|(
name|commit
argument_list|(
name|inserter
argument_list|,
name|mergeCommit
argument_list|)
argument_list|)
return|;
block|}
DECL|method|commit (ObjectInserter inserter, CommitBuilder mergeCommit)
specifier|private
specifier|static
name|ObjectId
name|commit
parameter_list|(
name|ObjectInserter
name|inserter
parameter_list|,
name|CommitBuilder
name|mergeCommit
parameter_list|)
throws|throws
name|IOException
block|{
name|ObjectId
name|id
init|=
name|inserter
operator|.
name|insert
argument_list|(
name|mergeCommit
argument_list|)
decl_stmt|;
name|inserter
operator|.
name|flush
argument_list|()
expr_stmt|;
return|return
name|id
return|;
block|}
block|}
end_class

end_unit

