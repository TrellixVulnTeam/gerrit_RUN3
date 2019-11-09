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
name|gerrit
operator|.
name|entities
operator|.
name|RefNames
operator|.
name|REFS
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
name|entities
operator|.
name|RefNames
operator|.
name|REFS_SEQUENCES
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
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
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
name|github
operator|.
name|rholder
operator|.
name|retry
operator|.
name|RetryException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|github
operator|.
name|rholder
operator|.
name|retry
operator|.
name|Retryer
import|;
end_import

begin_import
import|import
name|com
operator|.
name|github
operator|.
name|rholder
operator|.
name|retry
operator|.
name|RetryerBuilder
import|;
end_import

begin_import
import|import
name|com
operator|.
name|github
operator|.
name|rholder
operator|.
name|retry
operator|.
name|StopStrategies
import|;
end_import

begin_import
import|import
name|com
operator|.
name|github
operator|.
name|rholder
operator|.
name|retry
operator|.
name|WaitStrategies
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
name|base
operator|.
name|Throwables
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
name|util
operator|.
name|concurrent
operator|.
name|Runnables
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
name|entities
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
name|entities
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
name|exceptions
operator|.
name|StorageException
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
name|git
operator|.
name|LockFailureException
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
name|git
operator|.
name|RefUpdateUtil
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
name|ArrayList
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
name|Optional
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
name|ExecutionException
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
name|locks
operator|.
name|Lock
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

begin_comment
comment|/**  * Class for managing an incrementing sequence backed by a git repository.  *  *<p>The current sequence number is stored as UTF-8 text in a blob pointed to by a ref in the  * {@code refs/sequences/*} namespace. Multiple processes can share the same sequence by  * incrementing the counter using normal git ref updates. To amortize the cost of these ref updates,  * processes can increment the counter by a larger number and hand out numbers from that range in  * memory until they run out. This means concurrent processes will hand out somewhat non-monotonic  * numbers.  */
end_comment

begin_class
DECL|class|RepoSequence
specifier|public
class|class
name|RepoSequence
block|{
annotation|@
name|FunctionalInterface
DECL|interface|Seed
specifier|public
interface|interface
name|Seed
block|{
DECL|method|get ()
name|int
name|get
parameter_list|()
function_decl|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|retryerBuilder ()
specifier|static
name|RetryerBuilder
argument_list|<
name|ImmutableList
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|retryerBuilder
parameter_list|()
block|{
return|return
name|RetryerBuilder
operator|.
expr|<
name|ImmutableList
argument_list|<
name|Integer
argument_list|>
operator|>
name|newBuilder
argument_list|()
operator|.
name|retryIfException
argument_list|(
name|t
lambda|->
name|t
operator|instanceof
name|StorageException
operator|&&
operator|(
operator|(
name|StorageException
operator|)
name|t
operator|)
operator|.
name|getCause
argument_list|()
operator|instanceof
name|LockFailureException
argument_list|)
operator|.
name|withWaitStrategy
argument_list|(
name|WaitStrategies
operator|.
name|join
argument_list|(
name|WaitStrategies
operator|.
name|exponentialWait
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|,
name|WaitStrategies
operator|.
name|randomWait
argument_list|(
literal|50
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
argument_list|)
argument_list|)
operator|.
name|withStopStrategy
argument_list|(
name|StopStrategies
operator|.
name|stopAfterDelay
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
return|;
block|}
DECL|field|RETRYER
specifier|private
specifier|static
specifier|final
name|Retryer
argument_list|<
name|ImmutableList
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|RETRYER
init|=
name|retryerBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
DECL|field|repoManager
specifier|private
specifier|final
name|GitRepositoryManager
name|repoManager
decl_stmt|;
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
DECL|field|refName
specifier|private
specifier|final
name|String
name|refName
decl_stmt|;
DECL|field|seed
specifier|private
specifier|final
name|Seed
name|seed
decl_stmt|;
DECL|field|floor
specifier|private
specifier|final
name|int
name|floor
decl_stmt|;
DECL|field|batchSize
specifier|private
specifier|final
name|int
name|batchSize
decl_stmt|;
DECL|field|afterReadRef
specifier|private
specifier|final
name|Runnable
name|afterReadRef
decl_stmt|;
DECL|field|retryer
specifier|private
specifier|final
name|Retryer
argument_list|<
name|ImmutableList
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|retryer
decl_stmt|;
comment|// Protects all non-final fields.
DECL|field|counterLock
specifier|private
specifier|final
name|Lock
name|counterLock
decl_stmt|;
DECL|field|limit
specifier|private
name|int
name|limit
decl_stmt|;
DECL|field|counter
specifier|private
name|int
name|counter
decl_stmt|;
DECL|field|acquireCount
annotation|@
name|VisibleForTesting
name|int
name|acquireCount
decl_stmt|;
DECL|method|RepoSequence ( GitRepositoryManager repoManager, GitReferenceUpdated gitRefUpdated, Project.NameKey projectName, String name, Seed seed, int batchSize)
specifier|public
name|RepoSequence
parameter_list|(
name|GitRepositoryManager
name|repoManager
parameter_list|,
name|GitReferenceUpdated
name|gitRefUpdated
parameter_list|,
name|Project
operator|.
name|NameKey
name|projectName
parameter_list|,
name|String
name|name
parameter_list|,
name|Seed
name|seed
parameter_list|,
name|int
name|batchSize
parameter_list|)
block|{
name|this
argument_list|(
name|repoManager
argument_list|,
name|gitRefUpdated
argument_list|,
name|projectName
argument_list|,
name|name
argument_list|,
name|seed
argument_list|,
name|batchSize
argument_list|,
name|Runnables
operator|.
name|doNothing
argument_list|()
argument_list|,
name|RETRYER
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|RepoSequence ( GitRepositoryManager repoManager, GitReferenceUpdated gitRefUpdated, Project.NameKey projectName, String name, Seed seed, int batchSize, int floor)
specifier|public
name|RepoSequence
parameter_list|(
name|GitRepositoryManager
name|repoManager
parameter_list|,
name|GitReferenceUpdated
name|gitRefUpdated
parameter_list|,
name|Project
operator|.
name|NameKey
name|projectName
parameter_list|,
name|String
name|name
parameter_list|,
name|Seed
name|seed
parameter_list|,
name|int
name|batchSize
parameter_list|,
name|int
name|floor
parameter_list|)
block|{
name|this
argument_list|(
name|repoManager
argument_list|,
name|gitRefUpdated
argument_list|,
name|projectName
argument_list|,
name|name
argument_list|,
name|seed
argument_list|,
name|batchSize
argument_list|,
name|Runnables
operator|.
name|doNothing
argument_list|()
argument_list|,
name|RETRYER
argument_list|,
name|floor
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|RepoSequence ( GitRepositoryManager repoManager, GitReferenceUpdated gitRefUpdated, Project.NameKey projectName, String name, Seed seed, int batchSize, Runnable afterReadRef, Retryer<ImmutableList<Integer>> retryer)
name|RepoSequence
parameter_list|(
name|GitRepositoryManager
name|repoManager
parameter_list|,
name|GitReferenceUpdated
name|gitRefUpdated
parameter_list|,
name|Project
operator|.
name|NameKey
name|projectName
parameter_list|,
name|String
name|name
parameter_list|,
name|Seed
name|seed
parameter_list|,
name|int
name|batchSize
parameter_list|,
name|Runnable
name|afterReadRef
parameter_list|,
name|Retryer
argument_list|<
name|ImmutableList
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|retryer
parameter_list|)
block|{
name|this
argument_list|(
name|repoManager
argument_list|,
name|gitRefUpdated
argument_list|,
name|projectName
argument_list|,
name|name
argument_list|,
name|seed
argument_list|,
name|batchSize
argument_list|,
name|afterReadRef
argument_list|,
name|retryer
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|RepoSequence ( GitRepositoryManager repoManager, GitReferenceUpdated gitRefUpdated, Project.NameKey projectName, String name, Seed seed, int batchSize, Runnable afterReadRef, Retryer<ImmutableList<Integer>> retryer, int floor)
name|RepoSequence
parameter_list|(
name|GitRepositoryManager
name|repoManager
parameter_list|,
name|GitReferenceUpdated
name|gitRefUpdated
parameter_list|,
name|Project
operator|.
name|NameKey
name|projectName
parameter_list|,
name|String
name|name
parameter_list|,
name|Seed
name|seed
parameter_list|,
name|int
name|batchSize
parameter_list|,
name|Runnable
name|afterReadRef
parameter_list|,
name|Retryer
argument_list|<
name|ImmutableList
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|retryer
parameter_list|,
name|int
name|floor
parameter_list|)
block|{
name|this
operator|.
name|repoManager
operator|=
name|requireNonNull
argument_list|(
name|repoManager
argument_list|,
literal|"repoManager"
argument_list|)
expr_stmt|;
name|this
operator|.
name|gitRefUpdated
operator|=
name|requireNonNull
argument_list|(
name|gitRefUpdated
argument_list|,
literal|"gitRefUpdated"
argument_list|)
expr_stmt|;
name|this
operator|.
name|projectName
operator|=
name|requireNonNull
argument_list|(
name|projectName
argument_list|,
literal|"projectName"
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|name
operator|!=
literal|null
operator|&&
operator|!
name|name
operator|.
name|startsWith
argument_list|(
name|REFS
argument_list|)
operator|&&
operator|!
name|name
operator|.
name|startsWith
argument_list|(
name|REFS_SEQUENCES
operator|.
name|substring
argument_list|(
name|REFS
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
argument_list|,
literal|"name should be a suffix to follow \"refs/sequences/\", got: %s"
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|refName
operator|=
name|RefNames
operator|.
name|REFS_SEQUENCES
operator|+
name|name
expr_stmt|;
name|this
operator|.
name|seed
operator|=
name|requireNonNull
argument_list|(
name|seed
argument_list|,
literal|"seed"
argument_list|)
expr_stmt|;
name|this
operator|.
name|floor
operator|=
name|floor
expr_stmt|;
name|checkArgument
argument_list|(
name|batchSize
operator|>
literal|0
argument_list|,
literal|"expected batchSize> 0, got: %s"
argument_list|,
name|batchSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|batchSize
operator|=
name|batchSize
expr_stmt|;
name|this
operator|.
name|afterReadRef
operator|=
name|requireNonNull
argument_list|(
name|afterReadRef
argument_list|,
literal|"afterReadRef"
argument_list|)
expr_stmt|;
name|this
operator|.
name|retryer
operator|=
name|requireNonNull
argument_list|(
name|retryer
argument_list|,
literal|"retryer"
argument_list|)
expr_stmt|;
name|counterLock
operator|=
operator|new
name|ReentrantLock
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Retrieves the next available sequence number.    *    *<p>This method is thread-safe.    *    * @return the next available sequence number    */
DECL|method|next ()
specifier|public
name|int
name|next
parameter_list|()
block|{
return|return
name|Iterables
operator|.
name|getOnlyElement
argument_list|(
name|next
argument_list|(
literal|1
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Retrieves the next N available sequence number.    *    *<p>This method is thread-safe.    *    * @param count the number of sequence numbers which should be returned    * @return the next N available sequence numbers    */
DECL|method|next (int count)
specifier|public
name|ImmutableList
argument_list|<
name|Integer
argument_list|>
name|next
parameter_list|(
name|int
name|count
parameter_list|)
block|{
if|if
condition|(
name|count
operator|==
literal|0
condition|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
name|checkArgument
argument_list|(
name|count
operator|>
literal|0
argument_list|,
literal|"count is negative: %s"
argument_list|,
name|count
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|retryer
operator|.
name|call
argument_list|(
parameter_list|()
lambda|->
block|{
name|counterLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|count
operator|==
literal|1
condition|)
block|{
if|if
condition|(
name|counter
operator|>=
name|limit
condition|)
block|{
name|acquire
argument_list|(
name|batchSize
argument_list|)
expr_stmt|;
block|}
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|counter
operator|++
argument_list|)
return|;
block|}
name|List
argument_list|<
name|Integer
argument_list|>
name|ids
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|count
argument_list|)
decl_stmt|;
while|while
condition|(
name|counter
operator|<
name|limit
condition|)
block|{
name|ids
operator|.
name|add
argument_list|(
name|counter
operator|++
argument_list|)
expr_stmt|;
if|if
condition|(
name|ids
operator|.
name|size
argument_list|()
operator|==
name|count
condition|)
block|{
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|ids
argument_list|)
return|;
block|}
block|}
name|acquire
argument_list|(
name|Math
operator|.
name|max
argument_list|(
name|count
operator|-
name|ids
operator|.
name|size
argument_list|()
argument_list|,
name|batchSize
argument_list|)
argument_list|)
expr_stmt|;
while|while
condition|(
name|ids
operator|.
name|size
argument_list|()
operator|<
name|count
condition|)
block|{
name|ids
operator|.
name|add
argument_list|(
name|counter
operator|++
argument_list|)
expr_stmt|;
block|}
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|ids
argument_list|)
return|;
block|}
finally|finally
block|{
name|counterLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
decl||
name|RetryException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|Throwables
operator|.
name|throwIfInstanceOf
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|,
name|StorageException
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|StorageException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Updates the next available sequence number in NoteDb in order to have a batch of sequence    * numbers available that can be handed out. {@link #counter} stores the next sequence number that    * can be handed out. When {@link #limit} is reached a new batch of sequence numbers needs to be    * retrieved by calling this method.    *    *<p><strong>Note:</strong> Callers are required to acquire the {@link #counterLock} before    * calling this method.    *    * @param count the number of sequence numbers which should be retrieved    */
DECL|method|acquire (int count)
specifier|private
name|void
name|acquire
parameter_list|(
name|int
name|count
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
name|projectName
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
init|)
block|{
name|Optional
argument_list|<
name|IntBlob
argument_list|>
name|blob
init|=
name|IntBlob
operator|.
name|parse
argument_list|(
name|repo
argument_list|,
name|refName
argument_list|,
name|rw
argument_list|)
decl_stmt|;
name|afterReadRef
operator|.
name|run
argument_list|()
expr_stmt|;
name|ObjectId
name|oldId
decl_stmt|;
name|int
name|next
decl_stmt|;
if|if
condition|(
operator|!
name|blob
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|oldId
operator|=
name|ObjectId
operator|.
name|zeroId
argument_list|()
expr_stmt|;
name|next
operator|=
name|seed
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|oldId
operator|=
name|blob
operator|.
name|get
argument_list|()
operator|.
name|id
argument_list|()
expr_stmt|;
name|next
operator|=
name|blob
operator|.
name|get
argument_list|()
operator|.
name|value
argument_list|()
expr_stmt|;
block|}
name|next
operator|=
name|Math
operator|.
name|max
argument_list|(
name|floor
argument_list|,
name|next
argument_list|)
expr_stmt|;
name|RefUpdate
name|refUpdate
init|=
name|IntBlob
operator|.
name|tryStore
argument_list|(
name|repo
argument_list|,
name|rw
argument_list|,
name|projectName
argument_list|,
name|refName
argument_list|,
name|oldId
argument_list|,
name|next
operator|+
name|count
argument_list|,
name|gitRefUpdated
argument_list|)
decl_stmt|;
name|RefUpdateUtil
operator|.
name|checkResult
argument_list|(
name|refUpdate
argument_list|)
expr_stmt|;
name|counter
operator|=
name|next
expr_stmt|;
name|limit
operator|=
name|counter
operator|+
name|count
expr_stmt|;
name|acquireCount
operator|++
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
name|StorageException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|storeNew (ObjectInserter ins, String name, int val)
specifier|public
specifier|static
name|ReceiveCommand
name|storeNew
parameter_list|(
name|ObjectInserter
name|ins
parameter_list|,
name|String
name|name
parameter_list|,
name|int
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|ObjectId
name|newId
init|=
name|ins
operator|.
name|insert
argument_list|(
name|OBJ_BLOB
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|val
argument_list|)
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|ReceiveCommand
argument_list|(
name|ObjectId
operator|.
name|zeroId
argument_list|()
argument_list|,
name|newId
argument_list|,
name|RefNames
operator|.
name|REFS_SEQUENCES
operator|+
name|name
argument_list|)
return|;
block|}
DECL|method|current ()
specifier|public
name|int
name|current
parameter_list|()
block|{
name|counterLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
init|(
name|Repository
name|repo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|projectName
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
init|)
block|{
name|Optional
argument_list|<
name|IntBlob
argument_list|>
name|blob
init|=
name|IntBlob
operator|.
name|parse
argument_list|(
name|repo
argument_list|,
name|refName
argument_list|,
name|rw
argument_list|)
decl_stmt|;
name|int
name|current
decl_stmt|;
if|if
condition|(
operator|!
name|blob
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|current
operator|=
name|seed
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|current
operator|=
name|blob
operator|.
name|get
argument_list|()
operator|.
name|value
argument_list|()
expr_stmt|;
block|}
return|return
name|current
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|StorageException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|counterLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

