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
DECL|package|com.google.gerrit.server.index
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|index
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
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|registration
operator|.
name|DynamicSet
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
name|index
operator|.
name|Index
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
name|index
operator|.
name|IndexCollection
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
name|index
operator|.
name|IndexDefinition
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
name|index
operator|.
name|SiteIndexer
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
name|List
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

begin_class
DECL|class|OnlineReindexer
specifier|public
class|class
name|OnlineReindexer
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|,
name|I
extends|extends
name|Index
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
parameter_list|>
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
name|OnlineReindexer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|indexes
specifier|private
specifier|final
name|IndexCollection
argument_list|<
name|K
argument_list|,
name|V
argument_list|,
name|I
argument_list|>
name|indexes
decl_stmt|;
DECL|field|batchIndexer
specifier|private
specifier|final
name|SiteIndexer
argument_list|<
name|K
argument_list|,
name|V
argument_list|,
name|I
argument_list|>
name|batchIndexer
decl_stmt|;
DECL|field|oldVersion
specifier|private
specifier|final
name|int
name|oldVersion
decl_stmt|;
DECL|field|newVersion
specifier|private
specifier|final
name|int
name|newVersion
decl_stmt|;
DECL|field|listeners
specifier|private
specifier|final
name|DynamicSet
argument_list|<
name|OnlineUpgradeListener
argument_list|>
name|listeners
decl_stmt|;
DECL|field|index
specifier|private
name|I
name|index
decl_stmt|;
DECL|field|running
specifier|private
specifier|final
name|AtomicBoolean
name|running
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
DECL|method|OnlineReindexer ( IndexDefinition<K, V, I> def, int oldVersion, int newVersion, DynamicSet<OnlineUpgradeListener> listeners)
specifier|public
name|OnlineReindexer
parameter_list|(
name|IndexDefinition
argument_list|<
name|K
argument_list|,
name|V
argument_list|,
name|I
argument_list|>
name|def
parameter_list|,
name|int
name|oldVersion
parameter_list|,
name|int
name|newVersion
parameter_list|,
name|DynamicSet
argument_list|<
name|OnlineUpgradeListener
argument_list|>
name|listeners
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|def
operator|.
name|getName
argument_list|()
expr_stmt|;
name|this
operator|.
name|indexes
operator|=
name|def
operator|.
name|getIndexCollection
argument_list|()
expr_stmt|;
name|this
operator|.
name|batchIndexer
operator|=
name|def
operator|.
name|getSiteIndexer
argument_list|()
expr_stmt|;
name|this
operator|.
name|oldVersion
operator|=
name|oldVersion
expr_stmt|;
name|this
operator|.
name|newVersion
operator|=
name|newVersion
expr_stmt|;
name|this
operator|.
name|listeners
operator|=
name|listeners
expr_stmt|;
block|}
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{
if|if
condition|(
name|running
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|boolean
name|ok
init|=
literal|false
decl_stmt|;
try|try
block|{
name|reindex
argument_list|()
expr_stmt|;
name|ok
operator|=
literal|true
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
name|error
argument_list|(
literal|"Online reindex of {} schema version {} failed"
argument_list|,
name|name
argument_list|,
name|version
argument_list|(
name|index
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|running
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|ok
condition|)
block|{
for|for
control|(
name|OnlineUpgradeListener
name|listener
range|:
name|listeners
control|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
name|name
argument_list|,
name|oldVersion
argument_list|,
name|newVersion
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
decl_stmt|;
name|t
operator|.
name|setName
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Reindex %s v%d-v%d"
argument_list|,
name|name
argument_list|,
name|version
argument_list|(
name|indexes
operator|.
name|getSearchIndex
argument_list|()
argument_list|)
argument_list|,
name|newVersion
argument_list|)
argument_list|)
expr_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|isRunning ()
specifier|public
name|boolean
name|isRunning
parameter_list|()
block|{
return|return
name|running
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|getVersion ()
specifier|public
name|int
name|getVersion
parameter_list|()
block|{
return|return
name|newVersion
return|;
block|}
DECL|method|version (Index<?, ?> i)
specifier|private
specifier|static
name|int
name|version
parameter_list|(
name|Index
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|i
parameter_list|)
block|{
return|return
name|i
operator|.
name|getSchema
argument_list|()
operator|.
name|getVersion
argument_list|()
return|;
block|}
DECL|method|reindex ()
specifier|private
name|void
name|reindex
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|OnlineUpgradeListener
name|listener
range|:
name|listeners
control|)
block|{
name|listener
operator|.
name|onStart
argument_list|(
name|name
argument_list|,
name|oldVersion
argument_list|,
name|newVersion
argument_list|)
expr_stmt|;
block|}
name|index
operator|=
name|checkNotNull
argument_list|(
name|indexes
operator|.
name|getWriteIndex
argument_list|(
name|newVersion
argument_list|)
argument_list|,
literal|"not an active write schema version: %s %s"
argument_list|,
name|name
argument_list|,
name|newVersion
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Starting online reindex of {} from schema version {} to {}"
argument_list|,
name|name
argument_list|,
name|version
argument_list|(
name|indexes
operator|.
name|getSearchIndex
argument_list|()
argument_list|)
argument_list|,
name|version
argument_list|(
name|index
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|oldVersion
operator|!=
name|newVersion
condition|)
block|{
name|index
operator|.
name|deleteAll
argument_list|()
expr_stmt|;
block|}
name|SiteIndexer
operator|.
name|Result
name|result
init|=
name|batchIndexer
operator|.
name|indexAll
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|result
operator|.
name|success
argument_list|()
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Online reindex of {} schema version {} failed. Successfully"
operator|+
literal|" indexed {}, failed to index {}"
argument_list|,
name|name
argument_list|,
name|version
argument_list|(
name|index
argument_list|)
argument_list|,
name|result
operator|.
name|doneCount
argument_list|()
argument_list|,
name|result
operator|.
name|failedCount
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Reindex {} to version {} complete"
argument_list|,
name|name
argument_list|,
name|version
argument_list|(
name|index
argument_list|)
argument_list|)
expr_stmt|;
name|activateIndex
argument_list|()
expr_stmt|;
for|for
control|(
name|OnlineUpgradeListener
name|listener
range|:
name|listeners
control|)
block|{
name|listener
operator|.
name|onSuccess
argument_list|(
name|name
argument_list|,
name|oldVersion
argument_list|,
name|newVersion
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|activateIndex ()
specifier|public
name|void
name|activateIndex
parameter_list|()
block|{
name|indexes
operator|.
name|setSearchIndex
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Using {} schema version {}"
argument_list|,
name|name
argument_list|,
name|version
argument_list|(
name|index
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|index
operator|.
name|markReady
argument_list|(
literal|true
argument_list|)
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
literal|"Error activating new {} schema version {}"
argument_list|,
name|name
argument_list|,
name|version
argument_list|(
name|index
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|I
argument_list|>
name|toRemove
init|=
name|Lists
operator|.
name|newArrayListWithExpectedSize
argument_list|(
literal|1
argument_list|)
decl_stmt|;
for|for
control|(
name|I
name|i
range|:
name|indexes
operator|.
name|getWriteIndexes
argument_list|()
control|)
block|{
if|if
condition|(
name|version
argument_list|(
name|i
argument_list|)
operator|!=
name|version
argument_list|(
name|index
argument_list|)
condition|)
block|{
name|toRemove
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|I
name|i
range|:
name|toRemove
control|)
block|{
try|try
block|{
name|i
operator|.
name|markReady
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|indexes
operator|.
name|removeWriteIndex
argument_list|(
name|version
argument_list|(
name|i
argument_list|)
argument_list|)
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
literal|"Error deactivating old {} schema version {}"
argument_list|,
name|name
argument_list|,
name|version
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

