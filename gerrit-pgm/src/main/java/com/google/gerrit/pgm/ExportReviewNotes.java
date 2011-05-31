begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2010 The Android Open Source Project
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
DECL|package|com.google.gerrit.pgm
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|pgm
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
name|schema
operator|.
name|DataSourceProvider
operator|.
name|Context
operator|.
name|MULTI_USER
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
name|data
operator|.
name|ApprovalTypes
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
name|lifecycle
operator|.
name|LifecycleManager
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
name|lifecycle
operator|.
name|LifecycleModule
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
name|pgm
operator|.
name|util
operator|.
name|SiteProgram
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
name|GerritPersonIdentProvider
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
name|account
operator|.
name|AccountCacheImpl
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
name|account
operator|.
name|GroupCacheImpl
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
name|cache
operator|.
name|CachePool
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
name|ApprovalTypesProvider
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
name|AuthConfigModule
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
name|CanonicalWebUrl
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
name|CanonicalWebUrlProvider
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
name|FactoryModule
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
name|CodeReviewNoteCreationException
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
name|CreateCodeReviewNotes
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
name|git
operator|.
name|LocalDiskRepositoryManager
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
name|client
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
name|gwtorm
operator|.
name|client
operator|.
name|SchemaFactory
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
name|Injector
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
name|Scopes
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
name|lib
operator|.
name|ThreadSafeProgressMonitor
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
name|util
operator|.
name|BlockList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|kohsuke
operator|.
name|args4j
operator|.
name|Option
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

begin_comment
comment|/** Export review notes for all submitted changes in all projects. */
end_comment

begin_class
DECL|class|ExportReviewNotes
specifier|public
class|class
name|ExportReviewNotes
extends|extends
name|SiteProgram
block|{
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--threads"
argument_list|,
name|usage
operator|=
literal|"Number of concurrent threads to run"
argument_list|)
DECL|field|threads
specifier|private
name|int
name|threads
init|=
literal|2
decl_stmt|;
DECL|field|manager
specifier|private
specifier|final
name|LifecycleManager
name|manager
init|=
operator|new
name|LifecycleManager
argument_list|()
decl_stmt|;
DECL|field|textMonitor
specifier|private
specifier|final
name|TextProgressMonitor
name|textMonitor
init|=
operator|new
name|TextProgressMonitor
argument_list|()
decl_stmt|;
DECL|field|monitor
specifier|private
specifier|final
name|ThreadSafeProgressMonitor
name|monitor
init|=
operator|new
name|ThreadSafeProgressMonitor
argument_list|(
name|textMonitor
argument_list|)
decl_stmt|;
DECL|field|dbInjector
specifier|private
name|Injector
name|dbInjector
decl_stmt|;
DECL|field|gitInjector
specifier|private
name|Injector
name|gitInjector
decl_stmt|;
annotation|@
name|Inject
DECL|field|gitManager
specifier|private
name|GitRepositoryManager
name|gitManager
decl_stmt|;
annotation|@
name|Inject
DECL|field|database
specifier|private
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|database
decl_stmt|;
annotation|@
name|Inject
DECL|field|codeReviewNotesFactory
specifier|private
name|CreateCodeReviewNotes
operator|.
name|Factory
name|codeReviewNotesFactory
decl_stmt|;
DECL|field|changes
specifier|private
name|Map
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|,
name|List
argument_list|<
name|Change
argument_list|>
argument_list|>
name|changes
decl_stmt|;
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|int
name|run
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|threads
operator|<=
literal|0
condition|)
block|{
name|threads
operator|=
literal|1
expr_stmt|;
block|}
name|dbInjector
operator|=
name|createDbInjector
argument_list|(
name|MULTI_USER
argument_list|)
expr_stmt|;
name|gitInjector
operator|=
name|dbInjector
operator|.
name|createChildInjector
argument_list|(
operator|new
name|AbstractModule
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|bind
argument_list|(
name|GitRepositoryManager
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|LocalDiskRepositoryManager
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|ApprovalTypes
operator|.
name|class
argument_list|)
operator|.
name|toProvider
argument_list|(
name|ApprovalTypesProvider
operator|.
name|class
argument_list|)
operator|.
name|in
argument_list|(
name|Scopes
operator|.
name|SINGLETON
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|String
operator|.
name|class
argument_list|)
operator|.
name|annotatedWith
argument_list|(
name|CanonicalWebUrl
operator|.
name|class
argument_list|)
operator|.
name|toProvider
argument_list|(
name|CanonicalWebUrlProvider
operator|.
name|class
argument_list|)
operator|.
name|in
argument_list|(
name|Scopes
operator|.
name|SINGLETON
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|PersonIdent
operator|.
name|class
argument_list|)
operator|.
name|annotatedWith
argument_list|(
name|GerritPersonIdent
operator|.
name|class
argument_list|)
operator|.
name|toProvider
argument_list|(
name|GerritPersonIdentProvider
operator|.
name|class
argument_list|)
operator|.
name|in
argument_list|(
name|Scopes
operator|.
name|SINGLETON
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|CachePool
operator|.
name|class
argument_list|)
expr_stmt|;
name|install
argument_list|(
name|AccountCacheImpl
operator|.
name|module
argument_list|()
argument_list|)
expr_stmt|;
name|install
argument_list|(
name|GroupCacheImpl
operator|.
name|module
argument_list|()
argument_list|)
expr_stmt|;
name|install
argument_list|(
operator|new
name|AuthConfigModule
argument_list|()
argument_list|)
expr_stmt|;
name|install
argument_list|(
operator|new
name|FactoryModule
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|factory
argument_list|(
name|CreateCodeReviewNotes
operator|.
name|Factory
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|install
argument_list|(
operator|new
name|LifecycleModule
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|listener
argument_list|()
operator|.
name|to
argument_list|(
name|CachePool
operator|.
name|Lifecycle
operator|.
name|class
argument_list|)
expr_stmt|;
name|listener
argument_list|()
operator|.
name|to
argument_list|(
name|LocalDiskRepositoryManager
operator|.
name|Lifecycle
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|manager
operator|.
name|add
argument_list|(
name|dbInjector
argument_list|,
name|gitInjector
argument_list|)
expr_stmt|;
name|manager
operator|.
name|start
argument_list|()
expr_stmt|;
name|gitInjector
operator|.
name|injectMembers
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Change
argument_list|>
name|allChangeList
init|=
name|allChanges
argument_list|()
decl_stmt|;
name|monitor
operator|.
name|beginTask
argument_list|(
literal|"Scanning changes"
argument_list|,
name|allChangeList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|changes
operator|=
name|cluster
argument_list|(
name|allChangeList
argument_list|)
expr_stmt|;
name|allChangeList
operator|=
literal|null
expr_stmt|;
name|monitor
operator|.
name|startWorkers
argument_list|(
name|threads
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|tid
init|=
literal|0
init|;
name|tid
operator|<
name|threads
condition|;
name|tid
operator|++
control|)
block|{
operator|new
name|Worker
argument_list|()
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|monitor
operator|.
name|waitForCompletion
argument_list|()
expr_stmt|;
name|monitor
operator|.
name|endTask
argument_list|()
expr_stmt|;
name|manager
operator|.
name|stop
argument_list|()
expr_stmt|;
return|return
literal|0
return|;
block|}
DECL|method|allChanges ()
specifier|private
name|List
argument_list|<
name|Change
argument_list|>
name|allChanges
parameter_list|()
throws|throws
name|OrmException
block|{
specifier|final
name|ReviewDb
name|db
init|=
name|database
operator|.
name|open
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|db
operator|.
name|changes
argument_list|()
operator|.
name|all
argument_list|()
operator|.
name|toList
argument_list|()
return|;
block|}
finally|finally
block|{
name|db
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|cluster (List<Change> changes)
specifier|private
name|Map
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|,
name|List
argument_list|<
name|Change
argument_list|>
argument_list|>
name|cluster
parameter_list|(
name|List
argument_list|<
name|Change
argument_list|>
name|changes
parameter_list|)
block|{
name|HashMap
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|,
name|List
argument_list|<
name|Change
argument_list|>
argument_list|>
name|m
init|=
operator|new
name|HashMap
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|,
name|List
argument_list|<
name|Change
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Change
name|change
range|:
name|changes
control|)
block|{
if|if
condition|(
name|change
operator|.
name|getStatus
argument_list|()
operator|==
name|Change
operator|.
name|Status
operator|.
name|MERGED
condition|)
block|{
name|List
argument_list|<
name|Change
argument_list|>
name|l
init|=
name|m
operator|.
name|get
argument_list|(
name|change
operator|.
name|getProject
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|l
operator|==
literal|null
condition|)
block|{
name|l
operator|=
operator|new
name|BlockList
argument_list|<
name|Change
argument_list|>
argument_list|()
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
name|change
operator|.
name|getProject
argument_list|()
argument_list|,
name|l
argument_list|)
expr_stmt|;
block|}
name|l
operator|.
name|add
argument_list|(
name|change
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|monitor
operator|.
name|update
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|m
return|;
block|}
DECL|method|export (ReviewDb db, Project.NameKey project, List<Change> changes)
specifier|private
name|void
name|export
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
name|List
argument_list|<
name|Change
argument_list|>
name|changes
parameter_list|)
throws|throws
name|IOException
throws|,
name|OrmException
throws|,
name|CodeReviewNoteCreationException
throws|,
name|InterruptedException
block|{
specifier|final
name|Repository
name|git
decl_stmt|;
try|try
block|{
name|git
operator|=
name|gitManager
operator|.
name|openRepository
argument_list|(
name|project
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryNotFoundException
name|e
parameter_list|)
block|{
return|return;
block|}
try|try
block|{
name|CreateCodeReviewNotes
name|notes
init|=
name|codeReviewNotesFactory
operator|.
name|create
argument_list|(
name|db
argument_list|,
name|git
argument_list|)
decl_stmt|;
try|try
block|{
name|notes
operator|.
name|loadBase
argument_list|()
expr_stmt|;
for|for
control|(
name|Change
name|change
range|:
name|changes
control|)
block|{
name|monitor
operator|.
name|update
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|PatchSet
name|ps
init|=
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|get
argument_list|(
name|change
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|ps
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|notes
operator|.
name|add
argument_list|(
name|change
argument_list|,
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
expr_stmt|;
block|}
name|notes
operator|.
name|commit
argument_list|(
literal|"Exported prior reviews from Gerrit Code Review\n"
argument_list|)
expr_stmt|;
name|notes
operator|.
name|updateRef
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|notes
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|git
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|next ()
specifier|private
name|Map
operator|.
name|Entry
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|,
name|List
argument_list|<
name|Change
argument_list|>
argument_list|>
name|next
parameter_list|()
block|{
synchronized|synchronized
init|(
name|changes
init|)
block|{
if|if
condition|(
name|changes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|Project
operator|.
name|NameKey
name|name
init|=
name|changes
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Change
argument_list|>
name|list
init|=
name|changes
operator|.
name|remove
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
operator|new
name|Map
operator|.
name|Entry
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|,
name|List
argument_list|<
name|Change
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Project
operator|.
name|NameKey
name|getKey
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|Change
argument_list|>
name|getValue
parameter_list|()
block|{
return|return
name|list
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|Change
argument_list|>
name|setValue
parameter_list|(
name|List
argument_list|<
name|Change
argument_list|>
name|value
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
block|}
DECL|class|Worker
specifier|private
class|class
name|Worker
extends|extends
name|Thread
block|{
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
name|ReviewDb
name|db
decl_stmt|;
try|try
block|{
name|db
operator|=
name|database
operator|.
name|open
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
return|return;
block|}
try|try
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
name|Entry
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|,
name|List
argument_list|<
name|Change
argument_list|>
argument_list|>
name|next
init|=
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|export
argument_list|(
name|db
argument_list|,
name|next
operator|.
name|getKey
argument_list|()
argument_list|,
name|next
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CodeReviewNoteCreationException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
break|break;
block|}
block|}
block|}
finally|finally
block|{
name|monitor
operator|.
name|endWorker
argument_list|()
expr_stmt|;
name|db
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

