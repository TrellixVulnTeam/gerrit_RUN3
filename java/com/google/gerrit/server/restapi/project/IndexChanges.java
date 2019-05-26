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
DECL|package|com.google.gerrit.server.restapi.project
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|restapi
operator|.
name|project
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
name|git
operator|.
name|QueueProvider
operator|.
name|QueueType
operator|.
name|BATCH
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
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ListeningExecutorService
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
name|GlobalCapability
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
name|extensions
operator|.
name|annotations
operator|.
name|RequiresCapability
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
name|common
operator|.
name|Input
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
name|Response
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
name|RestModifyView
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
name|MultiProgressMonitor
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
name|MultiProgressMonitor
operator|.
name|Task
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
name|IndexExecutor
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
name|AllChangesIndexer
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
name|project
operator|.
name|ProjectResource
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
name|util
operator|.
name|concurrent
operator|.
name|Future
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
name|io
operator|.
name|NullOutputStream
import|;
end_import

begin_class
annotation|@
name|RequiresCapability
argument_list|(
name|GlobalCapability
operator|.
name|ADMINISTRATE_SERVER
argument_list|)
annotation|@
name|Singleton
DECL|class|IndexChanges
specifier|public
class|class
name|IndexChanges
implements|implements
name|RestModifyView
argument_list|<
name|ProjectResource
argument_list|,
name|Input
argument_list|>
block|{
DECL|field|allChangesIndexerProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|AllChangesIndexer
argument_list|>
name|allChangesIndexerProvider
decl_stmt|;
DECL|field|indexer
specifier|private
specifier|final
name|ChangeIndexer
name|indexer
decl_stmt|;
DECL|field|executor
specifier|private
specifier|final
name|ListeningExecutorService
name|executor
decl_stmt|;
annotation|@
name|Inject
DECL|method|IndexChanges ( Provider<AllChangesIndexer> allChangesIndexerProvider, ChangeIndexer indexer, @IndexExecutor(BATCH) ListeningExecutorService executor)
name|IndexChanges
parameter_list|(
name|Provider
argument_list|<
name|AllChangesIndexer
argument_list|>
name|allChangesIndexerProvider
parameter_list|,
name|ChangeIndexer
name|indexer
parameter_list|,
annotation|@
name|IndexExecutor
argument_list|(
name|BATCH
argument_list|)
name|ListeningExecutorService
name|executor
parameter_list|)
block|{
name|this
operator|.
name|allChangesIndexerProvider
operator|=
name|allChangesIndexerProvider
expr_stmt|;
name|this
operator|.
name|indexer
operator|=
name|indexer
expr_stmt|;
name|this
operator|.
name|executor
operator|=
name|executor
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (ProjectResource resource, Input input)
specifier|public
name|Response
operator|.
name|Accepted
name|apply
parameter_list|(
name|ProjectResource
name|resource
parameter_list|,
name|Input
name|input
parameter_list|)
block|{
name|Project
operator|.
name|NameKey
name|project
init|=
name|resource
operator|.
name|getNameKey
argument_list|()
decl_stmt|;
name|Task
name|mpt
init|=
operator|new
name|MultiProgressMonitor
argument_list|(
name|ByteStreams
operator|.
name|nullOutputStream
argument_list|()
argument_list|,
literal|"Reindexing project"
argument_list|)
operator|.
name|beginSubTask
argument_list|(
literal|""
argument_list|,
name|MultiProgressMonitor
operator|.
name|UNKNOWN
argument_list|)
decl_stmt|;
name|AllChangesIndexer
name|allChangesIndexer
init|=
name|allChangesIndexerProvider
operator|.
name|get
argument_list|()
decl_stmt|;
name|allChangesIndexer
operator|.
name|setVerboseOut
argument_list|(
name|NullOutputStream
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
comment|// The REST call is just a trigger for async reindexing, so it is safe to ignore the future's
comment|// return value.
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
name|Future
argument_list|<
name|?
argument_list|>
name|possiblyIgnoredError
init|=
name|executor
operator|.
name|submit
argument_list|(
name|allChangesIndexer
operator|.
name|reindexProject
argument_list|(
name|indexer
argument_list|,
name|project
argument_list|,
name|mpt
argument_list|,
name|mpt
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|Response
operator|.
name|accepted
argument_list|(
literal|"Project "
operator|+
name|project
operator|+
literal|" submitted for reindexing"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

