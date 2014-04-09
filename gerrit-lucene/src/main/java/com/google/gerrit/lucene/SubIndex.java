begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2013 The Android Open Source Project
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
DECL|package|com.google.gerrit.lucene
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|lucene
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|MILLISECONDS
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
name|Maps
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
name|AbstractFuture
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
name|ListenableFuture
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
name|ThreadFactoryBuilder
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
name|lucene
operator|.
name|LuceneChangeIndex
operator|.
name|GerritIndexWriterConfig
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Term
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|TrackingIndexWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|ControlledRealTimeReopenThread
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|IndexSearcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|ReferenceManager
operator|.
name|RefreshListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|SearcherFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|SearcherManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|AlreadyClosedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|Directory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|FSDirectory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|concurrent
operator|.
name|ConcurrentMap
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
name|Executor
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
name|ScheduledThreadPoolExecutor
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
name|TimeoutException
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
name|AtomicBoolean
import|;
end_import

begin_comment
comment|/** Piece of the change index that is implemented as a separate Lucene index. */
end_comment

begin_class
DECL|class|SubIndex
class|class
name|SubIndex
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SubIndex
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|dir
specifier|private
specifier|final
name|Directory
name|dir
decl_stmt|;
DECL|field|writer
specifier|private
specifier|final
name|TrackingIndexWriter
name|writer
decl_stmt|;
DECL|field|searcherManager
specifier|private
specifier|final
name|SearcherManager
name|searcherManager
decl_stmt|;
DECL|field|reopenThread
specifier|private
specifier|final
name|ControlledRealTimeReopenThread
argument_list|<
name|IndexSearcher
argument_list|>
name|reopenThread
decl_stmt|;
DECL|field|refreshListeners
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|RefreshListener
argument_list|,
name|Boolean
argument_list|>
name|refreshListeners
decl_stmt|;
DECL|method|SubIndex (File file, GerritIndexWriterConfig writerConfig)
name|SubIndex
parameter_list|(
name|File
name|file
parameter_list|,
name|GerritIndexWriterConfig
name|writerConfig
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|FSDirectory
operator|.
name|open
argument_list|(
name|file
argument_list|)
argument_list|,
name|file
operator|.
name|getName
argument_list|()
argument_list|,
name|writerConfig
argument_list|)
expr_stmt|;
block|}
DECL|method|SubIndex (Directory dir, final String dirName, GerritIndexWriterConfig writerConfig)
name|SubIndex
parameter_list|(
name|Directory
name|dir
parameter_list|,
specifier|final
name|String
name|dirName
parameter_list|,
name|GerritIndexWriterConfig
name|writerConfig
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
name|IndexWriter
name|delegateWriter
decl_stmt|;
name|long
name|commitPeriod
init|=
name|writerConfig
operator|.
name|getCommitWithinMs
argument_list|()
decl_stmt|;
if|if
condition|(
name|commitPeriod
operator|<
literal|0
condition|)
block|{
name|delegateWriter
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|writerConfig
operator|.
name|getLuceneConfig
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|commitPeriod
operator|==
literal|0
condition|)
block|{
name|delegateWriter
operator|=
operator|new
name|AutoCommitWriter
argument_list|(
name|dir
argument_list|,
name|writerConfig
operator|.
name|getLuceneConfig
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|AutoCommitWriter
name|autoCommitWriter
init|=
operator|new
name|AutoCommitWriter
argument_list|(
name|dir
argument_list|,
name|writerConfig
operator|.
name|getLuceneConfig
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|delegateWriter
operator|=
name|autoCommitWriter
expr_stmt|;
operator|new
name|ScheduledThreadPoolExecutor
argument_list|(
literal|1
argument_list|,
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setNameFormat
argument_list|(
literal|"Commit-%d "
operator|+
name|dirName
argument_list|)
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|scheduleAtFixedRate
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|autoCommitWriter
operator|.
name|hasUncommittedChanges
argument_list|()
condition|)
block|{
name|autoCommitWriter
operator|.
name|manualFlush
argument_list|()
expr_stmt|;
name|autoCommitWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error committing Lucene index "
operator|+
name|dirName
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OutOfMemoryError
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error committing Lucene index "
operator|+
name|dirName
argument_list|,
name|e
argument_list|)
expr_stmt|;
try|try
block|{
name|autoCommitWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e2
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"SEVERE: Error closing Lucene index "
operator|+
name|dirName
operator|+
literal|" after OOM; index may be corrupted."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
argument_list|,
name|commitPeriod
argument_list|,
name|commitPeriod
argument_list|,
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
name|writer
operator|=
operator|new
name|TrackingIndexWriter
argument_list|(
name|delegateWriter
argument_list|)
expr_stmt|;
name|searcherManager
operator|=
operator|new
name|SearcherManager
argument_list|(
name|writer
operator|.
name|getIndexWriter
argument_list|()
argument_list|,
literal|true
argument_list|,
operator|new
name|SearcherFactory
argument_list|()
argument_list|)
expr_stmt|;
name|refreshListeners
operator|=
name|Maps
operator|.
name|newConcurrentMap
argument_list|()
expr_stmt|;
name|searcherManager
operator|.
name|addListener
argument_list|(
operator|new
name|RefreshListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|beforeRefresh
parameter_list|()
throws|throws
name|IOException
block|{       }
annotation|@
name|Override
specifier|public
name|void
name|afterRefresh
parameter_list|(
name|boolean
name|didRefresh
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|RefreshListener
name|l
range|:
name|refreshListeners
operator|.
name|keySet
argument_list|()
control|)
block|{
name|l
operator|.
name|afterRefresh
argument_list|(
name|didRefresh
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|reopenThread
operator|=
operator|new
name|ControlledRealTimeReopenThread
argument_list|<
name|IndexSearcher
argument_list|>
argument_list|(
name|writer
argument_list|,
name|searcherManager
argument_list|,
literal|0.500
comment|/* maximum stale age (seconds) */
argument_list|,
literal|0.010
comment|/* minimum stale age (seconds) */
argument_list|)
expr_stmt|;
name|reopenThread
operator|.
name|setName
argument_list|(
literal|"NRT "
operator|+
name|dirName
argument_list|)
expr_stmt|;
name|reopenThread
operator|.
name|setPriority
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getPriority
argument_list|()
operator|+
literal|2
argument_list|,
name|Thread
operator|.
name|MAX_PRIORITY
argument_list|)
argument_list|)
expr_stmt|;
name|reopenThread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|reopenThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|close ()
name|void
name|close
parameter_list|()
block|{
name|reopenThread
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|writer
operator|.
name|getIndexWriter
argument_list|()
operator|.
name|commit
argument_list|()
expr_stmt|;
try|try
block|{
name|writer
operator|.
name|getIndexWriter
argument_list|()
operator|.
name|close
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AlreadyClosedException
name|e
parameter_list|)
block|{
comment|// Ignore.
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"error closing Lucene writer"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"error closing Lucene directory"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|insert (Document doc)
name|ListenableFuture
argument_list|<
name|?
argument_list|>
name|insert
parameter_list|(
name|Document
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|NrtFuture
argument_list|(
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
argument_list|)
return|;
block|}
DECL|method|replace (Term term, Document doc)
name|ListenableFuture
argument_list|<
name|?
argument_list|>
name|replace
parameter_list|(
name|Term
name|term
parameter_list|,
name|Document
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|NrtFuture
argument_list|(
name|writer
operator|.
name|updateDocument
argument_list|(
name|term
argument_list|,
name|doc
argument_list|)
argument_list|)
return|;
block|}
DECL|method|delete (Term term)
name|ListenableFuture
argument_list|<
name|?
argument_list|>
name|delete
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|NrtFuture
argument_list|(
name|writer
operator|.
name|deleteDocuments
argument_list|(
name|term
argument_list|)
argument_list|)
return|;
block|}
DECL|method|deleteAll ()
name|void
name|deleteAll
parameter_list|()
throws|throws
name|IOException
block|{
name|writer
operator|.
name|deleteAll
argument_list|()
expr_stmt|;
block|}
DECL|method|acquire ()
name|IndexSearcher
name|acquire
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|searcherManager
operator|.
name|acquire
argument_list|()
return|;
block|}
DECL|method|release (IndexSearcher searcher)
name|void
name|release
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|searcherManager
operator|.
name|release
argument_list|(
name|searcher
argument_list|)
expr_stmt|;
block|}
DECL|class|NrtFuture
specifier|private
specifier|final
class|class
name|NrtFuture
extends|extends
name|AbstractFuture
argument_list|<
name|Void
argument_list|>
implements|implements
name|RefreshListener
block|{
DECL|field|gen
specifier|private
specifier|final
name|long
name|gen
decl_stmt|;
DECL|field|hasListeners
specifier|private
specifier|final
name|AtomicBoolean
name|hasListeners
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
DECL|method|NrtFuture (long gen)
name|NrtFuture
parameter_list|(
name|long
name|gen
parameter_list|)
block|{
name|this
operator|.
name|gen
operator|=
name|gen
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get ()
specifier|public
name|Void
name|get
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|ExecutionException
block|{
if|if
condition|(
operator|!
name|isDone
argument_list|()
condition|)
block|{
name|reopenThread
operator|.
name|waitForGeneration
argument_list|(
name|gen
argument_list|)
expr_stmt|;
name|set
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
name|super
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|get (long timeout, TimeUnit unit)
specifier|public
name|Void
name|get
parameter_list|(
name|long
name|timeout
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|TimeoutException
throws|,
name|ExecutionException
block|{
if|if
condition|(
operator|!
name|isDone
argument_list|()
condition|)
block|{
name|reopenThread
operator|.
name|waitForGeneration
argument_list|(
name|gen
argument_list|,
operator|(
name|int
operator|)
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|timeout
argument_list|,
name|unit
argument_list|)
argument_list|)
expr_stmt|;
name|set
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
name|super
operator|.
name|get
argument_list|(
name|timeout
argument_list|,
name|unit
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|isDone ()
specifier|public
name|boolean
name|isDone
parameter_list|()
block|{
if|if
condition|(
name|super
operator|.
name|isDone
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|isSearcherCurrent
argument_list|()
condition|)
block|{
name|set
argument_list|(
literal|null
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|addListener (Runnable listener, Executor executor)
specifier|public
name|void
name|addListener
parameter_list|(
name|Runnable
name|listener
parameter_list|,
name|Executor
name|executor
parameter_list|)
block|{
if|if
condition|(
name|hasListeners
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
operator|&&
operator|!
name|isDone
argument_list|()
condition|)
block|{
name|searcherManager
operator|.
name|addListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|addListener
argument_list|(
name|listener
argument_list|,
name|executor
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|cancel (boolean mayInterruptIfRunning)
specifier|public
name|boolean
name|cancel
parameter_list|(
name|boolean
name|mayInterruptIfRunning
parameter_list|)
block|{
if|if
condition|(
name|hasListeners
operator|.
name|get
argument_list|()
condition|)
block|{
name|refreshListeners
operator|.
name|put
argument_list|(
name|this
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
return|return
name|super
operator|.
name|cancel
argument_list|(
name|mayInterruptIfRunning
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|beforeRefresh ()
specifier|public
name|void
name|beforeRefresh
parameter_list|()
throws|throws
name|IOException
block|{     }
annotation|@
name|Override
DECL|method|afterRefresh (boolean didRefresh)
specifier|public
name|void
name|afterRefresh
parameter_list|(
name|boolean
name|didRefresh
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|isSearcherCurrent
argument_list|()
condition|)
block|{
name|refreshListeners
operator|.
name|remove
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|set
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|isSearcherCurrent ()
specifier|private
name|boolean
name|isSearcherCurrent
parameter_list|()
block|{
try|try
block|{
return|return
name|reopenThread
operator|.
name|waitForGeneration
argument_list|(
name|gen
argument_list|,
literal|0
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Interrupted waiting for searcher generation"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

