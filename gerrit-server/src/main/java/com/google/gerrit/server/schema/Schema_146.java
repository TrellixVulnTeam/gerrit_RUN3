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
DECL|package|com.google.gerrit.server.schema
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|schema
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Stopwatch
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
name|UncheckedIOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Statement
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
name|text
operator|.
name|ParseException
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
name|Map
operator|.
name|Entry
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutorService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantLock
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
name|internal
operator|.
name|storage
operator|.
name|file
operator|.
name|FileRepository
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
name|internal
operator|.
name|storage
operator|.
name|file
operator|.
name|GC
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
name|ProgressMonitor
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
name|RefUpdate
operator|.
name|Result
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
name|lib
operator|.
name|TextProgressMonitor
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
name|RevSort
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
comment|/**  * Make sure that for every account a user branch exists that has an initial empty commit with the  * registration date as commit time.  *  *<p>For accounts that don't have a user branch yet the user branch is created with an initial  * empty commit that has the registration date as commit time.  *  *<p>For accounts that already have a user branch the user branch is rewritten and an initial empty  * commit with the registration date as commit time is inserted (if such a commit doesn't exist  * yet).  */
end_comment

begin_class
DECL|class|Schema_146
specifier|public
class|class
name|Schema_146
extends|extends
name|SchemaVersion
block|{
DECL|field|CREATE_ACCOUNT_MSG
specifier|private
specifier|static
specifier|final
name|String
name|CREATE_ACCOUNT_MSG
init|=
literal|"Create Account"
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
DECL|field|serverIdent
specifier|private
specifier|final
name|PersonIdent
name|serverIdent
decl_stmt|;
DECL|field|i
specifier|private
name|AtomicInteger
name|i
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|sw
specifier|private
name|Stopwatch
name|sw
init|=
name|Stopwatch
operator|.
name|createStarted
argument_list|()
decl_stmt|;
DECL|field|gcLock
name|ReentrantLock
name|gcLock
init|=
operator|new
name|ReentrantLock
argument_list|()
decl_stmt|;
DECL|field|size
specifier|private
name|int
name|size
decl_stmt|;
annotation|@
name|Inject
DECL|method|Schema_146 ( Provider<Schema_145> prior, GitRepositoryManager repoManager, AllUsersName allUsersName, @GerritPersonIdent PersonIdent serverIdent)
name|Schema_146
parameter_list|(
name|Provider
argument_list|<
name|Schema_145
argument_list|>
name|prior
parameter_list|,
name|GitRepositoryManager
name|repoManager
parameter_list|,
name|AllUsersName
name|allUsersName
parameter_list|,
annotation|@
name|GerritPersonIdent
name|PersonIdent
name|serverIdent
parameter_list|)
block|{
name|super
argument_list|(
name|prior
argument_list|)
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
name|serverIdent
operator|=
name|serverIdent
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|migrateData (ReviewDb db, UpdateUI ui)
specifier|protected
name|void
name|migrateData
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|UpdateUI
name|ui
parameter_list|)
throws|throws
name|OrmException
throws|,
name|SQLException
block|{
name|ui
operator|.
name|message
argument_list|(
literal|"Migrating accounts"
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Entry
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|Timestamp
argument_list|>
argument_list|>
name|accounts
init|=
name|scanAccounts
argument_list|(
name|db
argument_list|,
name|ui
argument_list|)
operator|.
name|entrySet
argument_list|()
decl_stmt|;
name|gc
argument_list|(
name|ui
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|List
argument_list|<
name|Entry
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|Timestamp
argument_list|>
argument_list|>
argument_list|>
name|batches
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|Iterables
operator|.
name|partition
argument_list|(
name|accounts
argument_list|,
literal|500
argument_list|)
argument_list|)
decl_stmt|;
name|ExecutorService
name|pool
init|=
name|createExecutor
argument_list|(
name|ui
argument_list|)
decl_stmt|;
try|try
block|{
name|batches
operator|.
name|stream
argument_list|()
operator|.
name|forEach
argument_list|(
name|batch
lambda|->
name|pool
operator|.
name|submit
argument_list|(
parameter_list|()
lambda|->
name|processBatch
argument_list|(
name|batch
argument_list|,
name|ui
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|pool
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|pool
operator|.
name|awaitTermination
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|,
name|TimeUnit
operator|.
name|DAYS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|ui
operator|.
name|message
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"... (%.3f s) Migrated all %d accounts to schema 146"
argument_list|,
name|elapsed
argument_list|()
argument_list|,
name|i
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|createExecutor (UpdateUI ui)
specifier|private
name|ExecutorService
name|createExecutor
parameter_list|(
name|UpdateUI
name|ui
parameter_list|)
block|{
name|int
name|threads
decl_stmt|;
try|try
block|{
name|threads
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"threadcount"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|threads
operator|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|availableProcessors
argument_list|()
expr_stmt|;
block|}
name|ui
operator|.
name|message
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"... using %d threads ..."
argument_list|,
name|threads
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|threads
argument_list|)
return|;
block|}
DECL|method|processBatch (List<Entry<Account.Id, Timestamp>> batch, UpdateUI ui)
specifier|private
name|void
name|processBatch
parameter_list|(
name|List
argument_list|<
name|Entry
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|Timestamp
argument_list|>
argument_list|>
name|batch
parameter_list|,
name|UpdateUI
name|ui
parameter_list|)
block|{
try|try
init|(
name|Repository
name|repo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|allUsersName
argument_list|)
init|;
name|RevWalk
name|rw
operator|=
operator|new
name|RevWalk
argument_list|(
name|repo
argument_list|)
init|;
name|ObjectInserter
name|oi
operator|=
name|repo
operator|.
name|newObjectInserter
argument_list|()
init|)
block|{
name|ObjectId
name|emptyTree
init|=
name|emptyTree
argument_list|(
name|oi
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|Timestamp
argument_list|>
name|e
range|:
name|batch
control|)
block|{
name|String
name|refName
init|=
name|RefNames
operator|.
name|refsUsers
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|Ref
name|ref
init|=
name|repo
operator|.
name|exactRef
argument_list|(
name|refName
argument_list|)
decl_stmt|;
if|if
condition|(
name|ref
operator|!=
literal|null
condition|)
block|{
name|rewriteUserBranch
argument_list|(
name|repo
argument_list|,
name|rw
argument_list|,
name|oi
argument_list|,
name|emptyTree
argument_list|,
name|ref
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|createUserBranch
argument_list|(
name|repo
argument_list|,
name|oi
argument_list|,
name|emptyTree
argument_list|,
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|int
name|count
init|=
name|i
operator|.
name|incrementAndGet
argument_list|()
decl_stmt|;
name|showProgress
argument_list|(
name|ui
argument_list|,
name|count
argument_list|)
expr_stmt|;
if|if
condition|(
name|count
operator|%
literal|1000
operator|==
literal|0
condition|)
block|{
name|gc
argument_list|(
name|repo
argument_list|,
literal|true
argument_list|,
name|ui
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|UncheckedIOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|elapsed ()
specifier|private
name|double
name|elapsed
parameter_list|()
block|{
return|return
name|sw
operator|.
name|elapsed
argument_list|(
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
operator|/
literal|1000d
return|;
block|}
DECL|method|showProgress (UpdateUI ui, int count)
specifier|private
name|void
name|showProgress
parameter_list|(
name|UpdateUI
name|ui
parameter_list|,
name|int
name|count
parameter_list|)
block|{
if|if
condition|(
name|count
operator|%
literal|100
operator|==
literal|0
condition|)
block|{
name|ui
operator|.
name|message
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"... (%.3f s) migrated %d%% (%d/%d) accounts"
argument_list|,
name|elapsed
argument_list|()
argument_list|,
name|Math
operator|.
name|round
argument_list|(
literal|100.0
operator|*
name|count
operator|/
name|size
argument_list|)
argument_list|,
name|count
argument_list|,
name|size
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|gc (UpdateUI ui)
specifier|private
name|void
name|gc
parameter_list|(
name|UpdateUI
name|ui
parameter_list|)
block|{
try|try
init|(
name|Repository
name|repo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|allUsersName
argument_list|)
init|)
block|{
name|gc
argument_list|(
name|repo
argument_list|,
literal|false
argument_list|,
name|ui
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|UncheckedIOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|gc (Repository repo, boolean refsOnly, UpdateUI ui)
specifier|private
name|void
name|gc
parameter_list|(
name|Repository
name|repo
parameter_list|,
name|boolean
name|refsOnly
parameter_list|,
name|UpdateUI
name|ui
parameter_list|)
block|{
if|if
condition|(
name|repo
operator|instanceof
name|FileRepository
operator|&&
name|gcLock
operator|.
name|tryLock
argument_list|()
condition|)
block|{
name|ProgressMonitor
name|pm
init|=
literal|null
decl_stmt|;
try|try
block|{
name|pm
operator|=
operator|new
name|TextProgressMonitor
argument_list|()
expr_stmt|;
name|FileRepository
name|r
init|=
operator|(
name|FileRepository
operator|)
name|repo
decl_stmt|;
name|GC
name|gc
init|=
operator|new
name|GC
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|gc
operator|.
name|setProgressMonitor
argument_list|(
name|pm
argument_list|)
expr_stmt|;
name|pm
operator|.
name|beginTask
argument_list|(
literal|"gc"
argument_list|,
name|ProgressMonitor
operator|.
name|UNKNOWN
argument_list|)
expr_stmt|;
if|if
condition|(
name|refsOnly
condition|)
block|{
name|ui
operator|.
name|message
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"... (%.3f s) pack refs"
argument_list|,
name|elapsed
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|gc
operator|.
name|packRefs
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|ui
operator|.
name|message
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"... (%.3f s) gc --prune=now"
argument_list|,
name|elapsed
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|gc
operator|.
name|setExpire
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|)
expr_stmt|;
name|gc
operator|.
name|gc
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|ParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|gcLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
if|if
condition|(
name|pm
operator|!=
literal|null
condition|)
block|{
name|pm
operator|.
name|endTask
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|rewriteUserBranch ( Repository repo, RevWalk rw, ObjectInserter oi, ObjectId emptyTree, Ref ref, Timestamp registeredOn)
specifier|private
name|void
name|rewriteUserBranch
parameter_list|(
name|Repository
name|repo
parameter_list|,
name|RevWalk
name|rw
parameter_list|,
name|ObjectInserter
name|oi
parameter_list|,
name|ObjectId
name|emptyTree
parameter_list|,
name|Ref
name|ref
parameter_list|,
name|Timestamp
name|registeredOn
parameter_list|)
throws|throws
name|IOException
block|{
name|ObjectId
name|current
init|=
name|createInitialEmptyCommit
argument_list|(
name|oi
argument_list|,
name|emptyTree
argument_list|,
name|registeredOn
argument_list|)
decl_stmt|;
name|rw
operator|.
name|reset
argument_list|()
expr_stmt|;
name|rw
operator|.
name|sort
argument_list|(
name|RevSort
operator|.
name|TOPO
argument_list|)
expr_stmt|;
name|rw
operator|.
name|sort
argument_list|(
name|RevSort
operator|.
name|REVERSE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|rw
operator|.
name|markStart
argument_list|(
name|rw
operator|.
name|parseCommit
argument_list|(
name|ref
operator|.
name|getObjectId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|RevCommit
name|c
decl_stmt|;
while|while
condition|(
operator|(
name|c
operator|=
name|rw
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|isInitialEmptyCommit
argument_list|(
name|emptyTree
argument_list|,
name|c
argument_list|)
condition|)
block|{
return|return;
block|}
name|CommitBuilder
name|cb
init|=
operator|new
name|CommitBuilder
argument_list|()
decl_stmt|;
name|cb
operator|.
name|setParentId
argument_list|(
name|current
argument_list|)
expr_stmt|;
name|cb
operator|.
name|setTreeId
argument_list|(
name|c
operator|.
name|getTree
argument_list|()
argument_list|)
expr_stmt|;
name|cb
operator|.
name|setAuthor
argument_list|(
name|c
operator|.
name|getAuthorIdent
argument_list|()
argument_list|)
expr_stmt|;
name|cb
operator|.
name|setCommitter
argument_list|(
name|c
operator|.
name|getCommitterIdent
argument_list|()
argument_list|)
expr_stmt|;
name|cb
operator|.
name|setMessage
argument_list|(
name|c
operator|.
name|getFullMessage
argument_list|()
argument_list|)
expr_stmt|;
name|cb
operator|.
name|setEncoding
argument_list|(
name|c
operator|.
name|getEncoding
argument_list|()
argument_list|)
expr_stmt|;
name|current
operator|=
name|oi
operator|.
name|insert
argument_list|(
name|cb
argument_list|)
expr_stmt|;
block|}
name|oi
operator|.
name|flush
argument_list|()
expr_stmt|;
name|RefUpdate
name|ru
init|=
name|repo
operator|.
name|updateRef
argument_list|(
name|ref
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|ru
operator|.
name|setExpectedOldObjectId
argument_list|(
name|ref
operator|.
name|getObjectId
argument_list|()
argument_list|)
expr_stmt|;
name|ru
operator|.
name|setNewObjectId
argument_list|(
name|current
argument_list|)
expr_stmt|;
name|ru
operator|.
name|setForceUpdate
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ru
operator|.
name|setRefLogIdent
argument_list|(
name|serverIdent
argument_list|)
expr_stmt|;
name|ru
operator|.
name|setRefLogMessage
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Result
name|result
init|=
name|ru
operator|.
name|update
argument_list|()
decl_stmt|;
if|if
condition|(
name|result
operator|!=
name|Result
operator|.
name|FORCED
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Failed to update ref %s: %s"
argument_list|,
name|ref
operator|.
name|getName
argument_list|()
argument_list|,
name|result
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
DECL|method|createUserBranch ( Repository repo, ObjectInserter oi, ObjectId emptyTree, Account.Id accountId, Timestamp registeredOn)
specifier|public
name|void
name|createUserBranch
parameter_list|(
name|Repository
name|repo
parameter_list|,
name|ObjectInserter
name|oi
parameter_list|,
name|ObjectId
name|emptyTree
parameter_list|,
name|Account
operator|.
name|Id
name|accountId
parameter_list|,
name|Timestamp
name|registeredOn
parameter_list|)
throws|throws
name|IOException
block|{
name|ObjectId
name|id
init|=
name|createInitialEmptyCommit
argument_list|(
name|oi
argument_list|,
name|emptyTree
argument_list|,
name|registeredOn
argument_list|)
decl_stmt|;
name|String
name|refName
init|=
name|RefNames
operator|.
name|refsUsers
argument_list|(
name|accountId
argument_list|)
decl_stmt|;
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
name|ObjectId
operator|.
name|zeroId
argument_list|()
argument_list|)
expr_stmt|;
name|ru
operator|.
name|setNewObjectId
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|ru
operator|.
name|setRefLogIdent
argument_list|(
name|serverIdent
argument_list|)
expr_stmt|;
name|ru
operator|.
name|setRefLogMessage
argument_list|(
name|CREATE_ACCOUNT_MSG
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Result
name|result
init|=
name|ru
operator|.
name|update
argument_list|()
decl_stmt|;
if|if
condition|(
name|result
operator|!=
name|Result
operator|.
name|NEW
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Failed to update ref %s: %s"
argument_list|,
name|refName
argument_list|,
name|result
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
DECL|method|createInitialEmptyCommit ( ObjectInserter oi, ObjectId emptyTree, Timestamp registrationDate)
specifier|private
name|ObjectId
name|createInitialEmptyCommit
parameter_list|(
name|ObjectInserter
name|oi
parameter_list|,
name|ObjectId
name|emptyTree
parameter_list|,
name|Timestamp
name|registrationDate
parameter_list|)
throws|throws
name|IOException
block|{
name|PersonIdent
name|ident
init|=
operator|new
name|PersonIdent
argument_list|(
name|serverIdent
argument_list|,
name|registrationDate
argument_list|)
decl_stmt|;
name|CommitBuilder
name|cb
init|=
operator|new
name|CommitBuilder
argument_list|()
decl_stmt|;
name|cb
operator|.
name|setTreeId
argument_list|(
name|emptyTree
argument_list|)
expr_stmt|;
name|cb
operator|.
name|setCommitter
argument_list|(
name|ident
argument_list|)
expr_stmt|;
name|cb
operator|.
name|setAuthor
argument_list|(
name|ident
argument_list|)
expr_stmt|;
name|cb
operator|.
name|setMessage
argument_list|(
name|CREATE_ACCOUNT_MSG
argument_list|)
expr_stmt|;
return|return
name|oi
operator|.
name|insert
argument_list|(
name|cb
argument_list|)
return|;
block|}
DECL|method|isInitialEmptyCommit (ObjectId emptyTree, RevCommit c)
specifier|private
name|boolean
name|isInitialEmptyCommit
parameter_list|(
name|ObjectId
name|emptyTree
parameter_list|,
name|RevCommit
name|c
parameter_list|)
block|{
return|return
name|c
operator|.
name|getParentCount
argument_list|()
operator|==
literal|0
operator|&&
name|c
operator|.
name|getTree
argument_list|()
operator|.
name|equals
argument_list|(
name|emptyTree
argument_list|)
operator|&&
name|c
operator|.
name|getShortMessage
argument_list|()
operator|.
name|equals
argument_list|(
name|CREATE_ACCOUNT_MSG
argument_list|)
return|;
block|}
DECL|method|emptyTree (ObjectInserter oi)
specifier|private
specifier|static
name|ObjectId
name|emptyTree
parameter_list|(
name|ObjectInserter
name|oi
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|oi
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
DECL|method|scanAccounts (ReviewDb db, UpdateUI ui)
specifier|private
name|Map
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|Timestamp
argument_list|>
name|scanAccounts
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|UpdateUI
name|ui
parameter_list|)
throws|throws
name|SQLException
block|{
name|ui
operator|.
name|message
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"... (%.3f s) scan accounts"
argument_list|,
name|elapsed
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
try|try
init|(
name|Statement
name|stmt
init|=
name|newStatement
argument_list|(
name|db
argument_list|)
init|;
name|ResultSet
name|rs
operator|=
name|stmt
operator|.
name|executeQuery
argument_list|(
literal|"SELECT account_id, registered_on FROM accounts"
argument_list|)
init|)
block|{
name|HashMap
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|Timestamp
argument_list|>
name|m
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|rs
operator|.
name|next
argument_list|()
condition|)
block|{
name|m
operator|.
name|put
argument_list|(
operator|new
name|Account
operator|.
name|Id
argument_list|(
name|rs
operator|.
name|getInt
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|,
name|rs
operator|.
name|getTimestamp
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|size
operator|=
name|m
operator|.
name|size
argument_list|()
expr_stmt|;
return|return
name|m
return|;
block|}
block|}
block|}
end_class

end_unit

