begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2009 The Android Open Source Project
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

begin_comment
comment|//
end_comment

begin_package
DECL|package|com.google.gerrit.server.patch
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|patch
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
name|server
operator|.
name|patch
operator|.
name|IntraLineLoader
operator|.
name|log
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
name|GerritServerConfig
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
name|AbstractModule
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
name|lib
operator|.
name|Config
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
name|ArrayBlockingQueue
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
name|BlockingQueue
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

begin_class
annotation|@
name|Singleton
DECL|class|IntraLineWorkerPool
specifier|public
class|class
name|IntraLineWorkerPool
block|{
DECL|class|Module
specifier|public
specifier|static
class|class
name|Module
extends|extends
name|AbstractModule
block|{
annotation|@
name|Override
DECL|method|configure ()
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|bind
argument_list|(
name|IntraLineWorkerPool
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|workerPool
specifier|private
specifier|final
name|BlockingQueue
argument_list|<
name|Worker
argument_list|>
name|workerPool
decl_stmt|;
annotation|@
name|Inject
DECL|method|IntraLineWorkerPool (@erritServerConfig Config cfg)
specifier|public
name|IntraLineWorkerPool
parameter_list|(
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|)
block|{
name|int
name|workers
init|=
name|cfg
operator|.
name|getInt
argument_list|(
literal|"cache"
argument_list|,
name|PatchListCacheImpl
operator|.
name|INTRA_NAME
argument_list|,
literal|"maxIdleWorkers"
argument_list|,
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|availableProcessors
argument_list|()
operator|*
literal|3
operator|/
literal|2
argument_list|)
decl_stmt|;
name|workerPool
operator|=
operator|new
name|ArrayBlockingQueue
argument_list|<
name|Worker
argument_list|>
argument_list|(
name|workers
argument_list|,
literal|true
comment|/* fair */
argument_list|)
expr_stmt|;
block|}
DECL|method|acquire ()
name|Worker
name|acquire
parameter_list|()
block|{
name|Worker
name|w
init|=
name|workerPool
operator|.
name|poll
argument_list|()
decl_stmt|;
if|if
condition|(
name|w
operator|==
literal|null
condition|)
block|{
comment|// If no worker is immediately available, start a new one.
comment|// Maximum parallelism is controlled by the web server.
name|w
operator|=
operator|new
name|Worker
argument_list|()
expr_stmt|;
name|w
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
return|return
name|w
return|;
block|}
DECL|method|release (Worker w)
name|void
name|release
parameter_list|(
name|Worker
name|w
parameter_list|)
block|{
if|if
condition|(
operator|!
name|workerPool
operator|.
name|offer
argument_list|(
name|w
argument_list|)
condition|)
block|{
comment|// If the idle worker pool is full, terminate the worker.
name|w
operator|.
name|shutdownGracefully
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|Worker
specifier|static
class|class
name|Worker
extends|extends
name|Thread
block|{
DECL|field|count
specifier|private
specifier|static
specifier|final
name|AtomicInteger
name|count
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|input
specifier|private
specifier|final
name|ArrayBlockingQueue
argument_list|<
name|Input
argument_list|>
name|input
decl_stmt|;
DECL|field|result
specifier|private
specifier|final
name|ArrayBlockingQueue
argument_list|<
name|Result
argument_list|>
name|result
decl_stmt|;
DECL|method|Worker ()
name|Worker
parameter_list|()
block|{
name|input
operator|=
operator|new
name|ArrayBlockingQueue
argument_list|<
name|Input
argument_list|>
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|ArrayBlockingQueue
argument_list|<
name|Result
argument_list|>
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|setName
argument_list|(
literal|"IntraLineDiff-"
operator|+
name|count
operator|.
name|getAndIncrement
argument_list|()
argument_list|)
expr_stmt|;
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|computeWithTimeout (IntraLineDiffKey key, long timeoutMillis)
name|Result
name|computeWithTimeout
parameter_list|(
name|IntraLineDiffKey
name|key
parameter_list|,
name|long
name|timeoutMillis
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|input
operator|.
name|offer
argument_list|(
operator|new
name|Input
argument_list|(
name|key
argument_list|)
argument_list|)
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cannot enqueue task to thread "
operator|+
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|Result
operator|.
name|TIMEOUT
return|;
block|}
name|Result
name|r
init|=
name|result
operator|.
name|poll
argument_list|(
name|timeoutMillis
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|!=
literal|null
condition|)
block|{
return|return
name|r
return|;
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
name|timeoutMillis
operator|+
literal|" ms timeout reached for IntraLineDiff"
operator|+
literal|" in project "
operator|+
name|key
operator|.
name|getProject
argument_list|()
operator|.
name|get
argument_list|()
operator|+
literal|" on commit "
operator|+
name|key
operator|.
name|getCommit
argument_list|()
operator|.
name|name
argument_list|()
operator|+
literal|" for path "
operator|+
name|key
operator|.
name|getPath
argument_list|()
operator|+
literal|" comparing "
operator|+
name|key
operator|.
name|getBlobA
argument_list|()
operator|.
name|name
argument_list|()
operator|+
literal|".."
operator|+
name|key
operator|.
name|getBlobB
argument_list|()
operator|.
name|name
argument_list|()
operator|+
literal|".  Killing "
operator|+
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|forcefullyKillThreadInAnUglyWay
argument_list|()
expr_stmt|;
return|return
name|Result
operator|.
name|TIMEOUT
return|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|forcefullyKillThreadInAnUglyWay ()
specifier|private
name|void
name|forcefullyKillThreadInAnUglyWay
parameter_list|()
block|{
try|try
block|{
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|error
parameter_list|)
block|{
comment|// Ignore any reason the thread won't stop.
name|log
operator|.
name|error
argument_list|(
literal|"Cannot stop runaway thread "
operator|+
name|getName
argument_list|()
argument_list|,
name|error
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|shutdownGracefully ()
specifier|private
name|void
name|shutdownGracefully
parameter_list|()
block|{
if|if
condition|(
operator|!
name|input
operator|.
name|offer
argument_list|(
name|Input
operator|.
name|END_THREAD
argument_list|)
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cannot gracefully stop thread "
operator|+
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
name|Input
name|in
decl_stmt|;
try|try
block|{
name|in
operator|=
name|input
operator|.
name|take
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Unexpected interrupt on "
operator|+
name|getName
argument_list|()
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|in
operator|==
name|Input
operator|.
name|END_THREAD
condition|)
block|{
return|return;
block|}
name|Result
name|r
decl_stmt|;
try|try
block|{
name|r
operator|=
operator|new
name|Result
argument_list|(
name|IntraLineLoader
operator|.
name|compute
argument_list|(
name|in
operator|.
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|error
parameter_list|)
block|{
name|r
operator|=
operator|new
name|Result
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|result
operator|.
name|offer
argument_list|(
name|r
argument_list|)
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cannot return result from "
operator|+
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|ThreadDeath
name|iHaveBeenShot
parameter_list|)
block|{
comment|// Handle thread death by gracefully returning to the caller,
comment|// allowing the thread to be destroyed.
block|}
block|}
DECL|class|Input
specifier|private
specifier|static
class|class
name|Input
block|{
DECL|field|END_THREAD
specifier|static
specifier|final
name|Input
name|END_THREAD
init|=
operator|new
name|Input
argument_list|(
literal|null
argument_list|)
decl_stmt|;
DECL|field|key
specifier|final
name|IntraLineDiffKey
name|key
decl_stmt|;
DECL|method|Input (IntraLineDiffKey key)
name|Input
parameter_list|(
name|IntraLineDiffKey
name|key
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
block|}
block|}
DECL|class|Result
specifier|static
class|class
name|Result
block|{
DECL|field|TIMEOUT
specifier|static
specifier|final
name|Result
name|TIMEOUT
init|=
operator|new
name|Result
argument_list|(
operator|(
name|IntraLineDiff
operator|)
literal|null
argument_list|)
decl_stmt|;
DECL|field|diff
specifier|final
name|IntraLineDiff
name|diff
decl_stmt|;
DECL|field|error
specifier|final
name|Exception
name|error
decl_stmt|;
DECL|method|Result (IntraLineDiff diff)
name|Result
parameter_list|(
name|IntraLineDiff
name|diff
parameter_list|)
block|{
name|this
operator|.
name|diff
operator|=
name|diff
expr_stmt|;
name|this
operator|.
name|error
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|Result (Exception error)
name|Result
parameter_list|(
name|Exception
name|error
parameter_list|)
block|{
name|this
operator|.
name|diff
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|error
operator|=
name|error
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

