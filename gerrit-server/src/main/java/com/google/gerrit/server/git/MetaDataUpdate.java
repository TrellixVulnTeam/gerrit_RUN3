begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2011 The Android Open Source Project
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
name|extensions
operator|.
name|events
operator|.
name|GitReferenceUpdated
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
name|RepositoryNotFoundException
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/** Helps with the updating of a {@link VersionedMetaData}. */
end_comment

begin_class
DECL|class|MetaDataUpdate
specifier|public
class|class
name|MetaDataUpdate
implements|implements
name|AutoCloseable
block|{
DECL|class|User
specifier|public
specifier|static
class|class
name|User
block|{
DECL|field|factory
specifier|private
specifier|final
name|InternalFactory
name|factory
decl_stmt|;
DECL|field|mgr
specifier|private
specifier|final
name|GitRepositoryManager
name|mgr
decl_stmt|;
DECL|field|serverIdent
specifier|private
specifier|final
name|PersonIdent
name|serverIdent
decl_stmt|;
DECL|field|identifiedUser
specifier|private
specifier|final
name|Provider
argument_list|<
name|IdentifiedUser
argument_list|>
name|identifiedUser
decl_stmt|;
annotation|@
name|Inject
DECL|method|User (InternalFactory factory, GitRepositoryManager mgr, @GerritPersonIdent PersonIdent serverIdent, Provider<IdentifiedUser> identifiedUser)
name|User
parameter_list|(
name|InternalFactory
name|factory
parameter_list|,
name|GitRepositoryManager
name|mgr
parameter_list|,
annotation|@
name|GerritPersonIdent
name|PersonIdent
name|serverIdent
parameter_list|,
name|Provider
argument_list|<
name|IdentifiedUser
argument_list|>
name|identifiedUser
parameter_list|)
block|{
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
name|this
operator|.
name|mgr
operator|=
name|mgr
expr_stmt|;
name|this
operator|.
name|serverIdent
operator|=
name|serverIdent
expr_stmt|;
name|this
operator|.
name|identifiedUser
operator|=
name|identifiedUser
expr_stmt|;
block|}
DECL|method|getUserPersonIdent ()
specifier|public
name|PersonIdent
name|getUserPersonIdent
parameter_list|()
block|{
return|return
name|createPersonIdent
argument_list|(
name|identifiedUser
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
DECL|method|create (Project.NameKey name)
specifier|public
name|MetaDataUpdate
name|create
parameter_list|(
name|Project
operator|.
name|NameKey
name|name
parameter_list|)
throws|throws
name|RepositoryNotFoundException
throws|,
name|IOException
block|{
return|return
name|create
argument_list|(
name|name
argument_list|,
name|identifiedUser
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
DECL|method|create (Project.NameKey name, IdentifiedUser user)
specifier|public
name|MetaDataUpdate
name|create
parameter_list|(
name|Project
operator|.
name|NameKey
name|name
parameter_list|,
name|IdentifiedUser
name|user
parameter_list|)
throws|throws
name|RepositoryNotFoundException
throws|,
name|IOException
block|{
return|return
name|create
argument_list|(
name|name
argument_list|,
name|user
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * Create an update using an existing batch ref update.    *<p>    * This allows batching together updates to multiple metadata refs. For making    * multiple commits to a single metadata ref, see    * {@link VersionedMetaData#openUpdate(MetaDataUpdate)}.    *    * @param name project name.    * @param user user for the update.    * @param batch batch update to use; the caller is responsible for committing    *     the update.    */
DECL|method|create (Project.NameKey name, IdentifiedUser user, BatchRefUpdate batch)
specifier|public
name|MetaDataUpdate
name|create
parameter_list|(
name|Project
operator|.
name|NameKey
name|name
parameter_list|,
name|IdentifiedUser
name|user
parameter_list|,
name|BatchRefUpdate
name|batch
parameter_list|)
throws|throws
name|RepositoryNotFoundException
throws|,
name|IOException
block|{
return|return
name|create
argument_list|(
name|name
argument_list|,
name|mgr
operator|.
name|openRepository
argument_list|(
name|name
argument_list|)
argument_list|,
name|user
argument_list|,
name|batch
argument_list|)
return|;
block|}
comment|/**      * Create an update using an existing batch ref update.      *<p>      * This allows batching together updates to multiple metadata refs. For making      * multiple commits to a single metadata ref, see      * {@link VersionedMetaData#openUpdate(MetaDataUpdate)}.      *      * Important: Create a new MetaDataUpdate instance for each update:      *<pre>      *<code>      *   try (Repository repo = repoMgr.openRepository(allUsersName);      *       RevWalk rw = new RevWalk(repo) {      *     BatchRefUpdate batchUpdate = repo.getRefDatabase().newBatchUpdate();      *     // WRONG: create the MetaDataUpdate instance here and reuse it for      *     //        all updates in the loop      *     for{@code (Map.Entry<Account.Id, DiffPreferencesInfo> e : diffPrefsFromDb)} {      *       // CORRECT: create a new MetaDataUpdate instance for each update      *       try (MetaDataUpdate md =      *           metaDataUpdateFactory.create(allUsersName, batchUpdate)) {      *         md.setMessage("Import diff preferences from reviewdb\n");      *         VersionedAccountPreferences vPrefs =      *             VersionedAccountPreferences.forUser(e.getKey());      *         storeSection(vPrefs.getConfig(), UserConfigSections.DIFF, null,      *             e.getValue(), DiffPreferencesInfo.defaults());      *         vPrefs.commit(md);      *       } catch (ConfigInvalidException e) {      *         // TODO handle exception      *       }      *     }      *     batchUpdate.execute(rw, NullProgressMonitor.INSTANCE);      *   }      *</code>      *</pre>      *      * @param name project name.      * @param repository GIT respository      * @param user user for the update.      * @param batch batch update to use; the caller is responsible for committing      *     the update.      */
DECL|method|create (Project.NameKey name, Repository repository, IdentifiedUser user, BatchRefUpdate batch)
specifier|public
name|MetaDataUpdate
name|create
parameter_list|(
name|Project
operator|.
name|NameKey
name|name
parameter_list|,
name|Repository
name|repository
parameter_list|,
name|IdentifiedUser
name|user
parameter_list|,
name|BatchRefUpdate
name|batch
parameter_list|)
block|{
name|MetaDataUpdate
name|md
init|=
name|factory
operator|.
name|create
argument_list|(
name|name
argument_list|,
name|repository
argument_list|,
name|batch
argument_list|)
decl_stmt|;
name|md
operator|.
name|getCommitBuilder
argument_list|()
operator|.
name|setCommitter
argument_list|(
name|serverIdent
argument_list|)
expr_stmt|;
name|md
operator|.
name|setAuthor
argument_list|(
name|user
argument_list|)
expr_stmt|;
return|return
name|md
return|;
block|}
DECL|method|createPersonIdent (IdentifiedUser user)
specifier|private
name|PersonIdent
name|createPersonIdent
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
name|serverIdent
operator|.
name|getWhen
argument_list|()
argument_list|,
name|serverIdent
operator|.
name|getTimeZone
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|class|Server
specifier|public
specifier|static
class|class
name|Server
block|{
DECL|field|factory
specifier|private
specifier|final
name|InternalFactory
name|factory
decl_stmt|;
DECL|field|mgr
specifier|private
specifier|final
name|GitRepositoryManager
name|mgr
decl_stmt|;
DECL|field|serverIdent
specifier|private
specifier|final
name|PersonIdent
name|serverIdent
decl_stmt|;
annotation|@
name|Inject
DECL|method|Server (InternalFactory factory, GitRepositoryManager mgr, @GerritPersonIdent PersonIdent serverIdent)
name|Server
parameter_list|(
name|InternalFactory
name|factory
parameter_list|,
name|GitRepositoryManager
name|mgr
parameter_list|,
annotation|@
name|GerritPersonIdent
name|PersonIdent
name|serverIdent
parameter_list|)
block|{
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
name|this
operator|.
name|mgr
operator|=
name|mgr
expr_stmt|;
name|this
operator|.
name|serverIdent
operator|=
name|serverIdent
expr_stmt|;
block|}
DECL|method|create (Project.NameKey name)
specifier|public
name|MetaDataUpdate
name|create
parameter_list|(
name|Project
operator|.
name|NameKey
name|name
parameter_list|)
throws|throws
name|RepositoryNotFoundException
throws|,
name|IOException
block|{
return|return
name|create
argument_list|(
name|name
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/** @see User#create(Project.NameKey, IdentifiedUser, BatchRefUpdate) */
DECL|method|create (Project.NameKey name, BatchRefUpdate batch)
specifier|public
name|MetaDataUpdate
name|create
parameter_list|(
name|Project
operator|.
name|NameKey
name|name
parameter_list|,
name|BatchRefUpdate
name|batch
parameter_list|)
throws|throws
name|RepositoryNotFoundException
throws|,
name|IOException
block|{
name|MetaDataUpdate
name|md
init|=
name|factory
operator|.
name|create
argument_list|(
name|name
argument_list|,
name|mgr
operator|.
name|openRepository
argument_list|(
name|name
argument_list|)
argument_list|,
name|batch
argument_list|)
decl_stmt|;
name|md
operator|.
name|getCommitBuilder
argument_list|()
operator|.
name|setAuthor
argument_list|(
name|serverIdent
argument_list|)
expr_stmt|;
name|md
operator|.
name|getCommitBuilder
argument_list|()
operator|.
name|setCommitter
argument_list|(
name|serverIdent
argument_list|)
expr_stmt|;
return|return
name|md
return|;
block|}
block|}
DECL|interface|InternalFactory
interface|interface
name|InternalFactory
block|{
DECL|method|create (@ssisted Project.NameKey projectName, @Assisted Repository db, @Assisted @Nullable BatchRefUpdate batch)
name|MetaDataUpdate
name|create
parameter_list|(
annotation|@
name|Assisted
name|Project
operator|.
name|NameKey
name|projectName
parameter_list|,
annotation|@
name|Assisted
name|Repository
name|db
parameter_list|,
annotation|@
name|Assisted
annotation|@
name|Nullable
name|BatchRefUpdate
name|batch
parameter_list|)
function_decl|;
block|}
DECL|field|gitRefUpdated
specifier|private
specifier|final
name|GitReferenceUpdated
name|gitRefUpdated
decl_stmt|;
DECL|field|projectName
specifier|private
specifier|final
name|Project
operator|.
name|NameKey
name|projectName
decl_stmt|;
DECL|field|db
specifier|private
specifier|final
name|Repository
name|db
decl_stmt|;
DECL|field|batch
specifier|private
specifier|final
name|BatchRefUpdate
name|batch
decl_stmt|;
DECL|field|commit
specifier|private
specifier|final
name|CommitBuilder
name|commit
decl_stmt|;
DECL|field|allowEmpty
specifier|private
name|boolean
name|allowEmpty
decl_stmt|;
DECL|field|insertChangeId
specifier|private
name|boolean
name|insertChangeId
decl_stmt|;
DECL|field|author
specifier|private
name|IdentifiedUser
name|author
decl_stmt|;
annotation|@
name|AssistedInject
DECL|method|MetaDataUpdate (GitReferenceUpdated gitRefUpdated, @Assisted Project.NameKey projectName, @Assisted Repository db, @Assisted @Nullable BatchRefUpdate batch)
specifier|public
name|MetaDataUpdate
parameter_list|(
name|GitReferenceUpdated
name|gitRefUpdated
parameter_list|,
annotation|@
name|Assisted
name|Project
operator|.
name|NameKey
name|projectName
parameter_list|,
annotation|@
name|Assisted
name|Repository
name|db
parameter_list|,
annotation|@
name|Assisted
annotation|@
name|Nullable
name|BatchRefUpdate
name|batch
parameter_list|)
block|{
name|this
operator|.
name|gitRefUpdated
operator|=
name|gitRefUpdated
expr_stmt|;
name|this
operator|.
name|projectName
operator|=
name|projectName
expr_stmt|;
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|batch
operator|=
name|batch
expr_stmt|;
name|this
operator|.
name|commit
operator|=
operator|new
name|CommitBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|MetaDataUpdate (GitReferenceUpdated gitRefUpdated, Project.NameKey projectName, Repository db)
specifier|public
name|MetaDataUpdate
parameter_list|(
name|GitReferenceUpdated
name|gitRefUpdated
parameter_list|,
name|Project
operator|.
name|NameKey
name|projectName
parameter_list|,
name|Repository
name|db
parameter_list|)
block|{
name|this
argument_list|(
name|gitRefUpdated
argument_list|,
name|projectName
argument_list|,
name|db
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/** Set the commit message used when committing the update. */
DECL|method|setMessage (String message)
specifier|public
name|void
name|setMessage
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|getCommitBuilder
argument_list|()
operator|.
name|setMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
DECL|method|setAuthor (IdentifiedUser author)
specifier|public
name|void
name|setAuthor
parameter_list|(
name|IdentifiedUser
name|author
parameter_list|)
block|{
name|this
operator|.
name|author
operator|=
name|author
expr_stmt|;
name|getCommitBuilder
argument_list|()
operator|.
name|setAuthor
argument_list|(
name|author
operator|.
name|newCommitterIdent
argument_list|(
name|getCommitBuilder
argument_list|()
operator|.
name|getCommitter
argument_list|()
operator|.
name|getWhen
argument_list|()
argument_list|,
name|getCommitBuilder
argument_list|()
operator|.
name|getCommitter
argument_list|()
operator|.
name|getTimeZone
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|setAllowEmpty (boolean allowEmpty)
specifier|public
name|void
name|setAllowEmpty
parameter_list|(
name|boolean
name|allowEmpty
parameter_list|)
block|{
name|this
operator|.
name|allowEmpty
operator|=
name|allowEmpty
expr_stmt|;
block|}
DECL|method|setInsertChangeId (boolean insertChangeId)
specifier|public
name|void
name|setInsertChangeId
parameter_list|(
name|boolean
name|insertChangeId
parameter_list|)
block|{
name|this
operator|.
name|insertChangeId
operator|=
name|insertChangeId
expr_stmt|;
block|}
comment|/** @return batch in which to run the update, or {@code null} for no batch. */
DECL|method|getBatch ()
name|BatchRefUpdate
name|getBatch
parameter_list|()
block|{
return|return
name|batch
return|;
block|}
comment|/** Close the cached Repository handle. */
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
name|getRepository
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|getProjectName ()
name|Project
operator|.
name|NameKey
name|getProjectName
parameter_list|()
block|{
return|return
name|projectName
return|;
block|}
DECL|method|getRepository ()
specifier|public
name|Repository
name|getRepository
parameter_list|()
block|{
return|return
name|db
return|;
block|}
DECL|method|allowEmpty ()
name|boolean
name|allowEmpty
parameter_list|()
block|{
return|return
name|allowEmpty
return|;
block|}
DECL|method|insertChangeId ()
name|boolean
name|insertChangeId
parameter_list|()
block|{
return|return
name|insertChangeId
return|;
block|}
DECL|method|getCommitBuilder ()
specifier|public
name|CommitBuilder
name|getCommitBuilder
parameter_list|()
block|{
return|return
name|commit
return|;
block|}
DECL|method|fireGitRefUpdatedEvent (RefUpdate ru)
specifier|protected
name|void
name|fireGitRefUpdatedEvent
parameter_list|(
name|RefUpdate
name|ru
parameter_list|)
block|{
name|gitRefUpdated
operator|.
name|fire
argument_list|(
name|projectName
argument_list|,
name|ru
argument_list|,
name|author
operator|==
literal|null
condition|?
literal|null
else|:
name|author
operator|.
name|getAccount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

