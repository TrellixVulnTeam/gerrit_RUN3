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
comment|// limitations under the License.package com.google.gerrit.server.git;
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
name|server
operator|.
name|index
operator|.
name|ChangeBatchIndexer
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
name|ChangeIndex
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
name|server
operator|.
name|project
operator|.
name|ProjectCache
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
name|assistedinject
operator|.
name|Assisted
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

begin_class
DECL|class|OnlineReindexer
specifier|public
class|class
name|OnlineReindexer
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
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (int version)
name|OnlineReindexer
name|create
parameter_list|(
name|int
name|version
parameter_list|)
function_decl|;
block|}
DECL|field|indexes
specifier|private
specifier|final
name|IndexCollection
name|indexes
decl_stmt|;
DECL|field|batchIndexer
specifier|private
specifier|final
name|ChangeBatchIndexer
name|batchIndexer
decl_stmt|;
DECL|field|projectCache
specifier|private
specifier|final
name|ProjectCache
name|projectCache
decl_stmt|;
DECL|field|version
specifier|private
specifier|final
name|int
name|version
decl_stmt|;
annotation|@
name|Inject
DECL|method|OnlineReindexer ( IndexCollection indexes, ChangeBatchIndexer batchIndexer, ProjectCache projectCache, @Assisted int version)
name|OnlineReindexer
parameter_list|(
name|IndexCollection
name|indexes
parameter_list|,
name|ChangeBatchIndexer
name|batchIndexer
parameter_list|,
name|ProjectCache
name|projectCache
parameter_list|,
annotation|@
name|Assisted
name|int
name|version
parameter_list|)
block|{
name|this
operator|.
name|indexes
operator|=
name|indexes
expr_stmt|;
name|this
operator|.
name|batchIndexer
operator|=
name|batchIndexer
expr_stmt|;
name|this
operator|.
name|projectCache
operator|=
name|projectCache
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
block|}
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
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
name|reindex
argument_list|()
expr_stmt|;
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
literal|"Reindex v%d-v%d"
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
argument_list|)
argument_list|)
expr_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|version (ChangeIndex i)
specifier|private
specifier|static
name|int
name|version
parameter_list|(
name|ChangeIndex
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
block|{
name|ChangeIndex
name|index
init|=
name|checkNotNull
argument_list|(
name|indexes
operator|.
name|getWriteIndex
argument_list|(
name|version
argument_list|)
argument_list|,
literal|"not an active write schema version: %s"
argument_list|,
name|version
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Starting online reindex from schema version {} to {}"
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
name|ChangeBatchIndexer
operator|.
name|Result
name|result
init|=
name|batchIndexer
operator|.
name|indexAll
argument_list|(
name|index
argument_list|,
name|projectCache
operator|.
name|all
argument_list|()
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|,
literal|null
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
literal|"Online reindex of schema version {} failed"
argument_list|,
name|version
argument_list|(
name|index
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
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
literal|"Reindex complete, using schema version {}"
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
literal|"Error activating new schema version {}"
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
name|ChangeIndex
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
name|ChangeIndex
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
name|ChangeIndex
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
literal|"Error deactivating old schema version {}"
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

