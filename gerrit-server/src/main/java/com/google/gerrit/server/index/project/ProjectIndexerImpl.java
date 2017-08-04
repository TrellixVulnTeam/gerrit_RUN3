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
DECL|package|com.google.gerrit.server.index.project
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
operator|.
name|project
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
name|collect
operator|.
name|ImmutableSet
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
name|extensions
operator|.
name|events
operator|.
name|ProjectIndexedListener
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
name|gerrit
operator|.
name|server
operator|.
name|project
operator|.
name|ProjectData
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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_class
DECL|class|ProjectIndexerImpl
specifier|public
class|class
name|ProjectIndexerImpl
implements|implements
name|ProjectIndexer
block|{
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (ProjectIndexCollection indexes)
name|ProjectIndexerImpl
name|create
parameter_list|(
name|ProjectIndexCollection
name|indexes
parameter_list|)
function_decl|;
DECL|method|create (@ullable ProjectIndex index)
name|ProjectIndexerImpl
name|create
parameter_list|(
annotation|@
name|Nullable
name|ProjectIndex
name|index
parameter_list|)
function_decl|;
block|}
DECL|field|projectCache
specifier|private
specifier|final
name|ProjectCache
name|projectCache
decl_stmt|;
DECL|field|indexedListener
specifier|private
specifier|final
name|DynamicSet
argument_list|<
name|ProjectIndexedListener
argument_list|>
name|indexedListener
decl_stmt|;
DECL|field|indexes
specifier|private
specifier|final
name|ProjectIndexCollection
name|indexes
decl_stmt|;
DECL|field|index
specifier|private
specifier|final
name|ProjectIndex
name|index
decl_stmt|;
annotation|@
name|AssistedInject
DECL|method|ProjectIndexerImpl ( ProjectCache projectCache, DynamicSet<ProjectIndexedListener> indexedListener, @Assisted ProjectIndexCollection indexes)
name|ProjectIndexerImpl
parameter_list|(
name|ProjectCache
name|projectCache
parameter_list|,
name|DynamicSet
argument_list|<
name|ProjectIndexedListener
argument_list|>
name|indexedListener
parameter_list|,
annotation|@
name|Assisted
name|ProjectIndexCollection
name|indexes
parameter_list|)
block|{
name|this
operator|.
name|projectCache
operator|=
name|projectCache
expr_stmt|;
name|this
operator|.
name|indexedListener
operator|=
name|indexedListener
expr_stmt|;
name|this
operator|.
name|indexes
operator|=
name|indexes
expr_stmt|;
name|this
operator|.
name|index
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|AssistedInject
DECL|method|ProjectIndexerImpl ( ProjectCache projectCache, DynamicSet<ProjectIndexedListener> indexedListener, @Assisted ProjectIndex index)
name|ProjectIndexerImpl
parameter_list|(
name|ProjectCache
name|projectCache
parameter_list|,
name|DynamicSet
argument_list|<
name|ProjectIndexedListener
argument_list|>
name|indexedListener
parameter_list|,
annotation|@
name|Assisted
name|ProjectIndex
name|index
parameter_list|)
block|{
name|this
operator|.
name|projectCache
operator|=
name|projectCache
expr_stmt|;
name|this
operator|.
name|indexedListener
operator|=
name|indexedListener
expr_stmt|;
name|this
operator|.
name|indexes
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|index (Project.NameKey nameKey)
specifier|public
name|void
name|index
parameter_list|(
name|Project
operator|.
name|NameKey
name|nameKey
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|Index
argument_list|<
name|?
argument_list|,
name|ProjectData
argument_list|>
name|i
range|:
name|getWriteIndexes
argument_list|()
control|)
block|{
name|i
operator|.
name|replace
argument_list|(
name|projectCache
operator|.
name|get
argument_list|(
name|nameKey
argument_list|)
operator|.
name|toProjectData
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|fireProjectIndexedEvent
argument_list|(
name|nameKey
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|fireProjectIndexedEvent (String name)
specifier|private
name|void
name|fireProjectIndexedEvent
parameter_list|(
name|String
name|name
parameter_list|)
block|{
for|for
control|(
name|ProjectIndexedListener
name|listener
range|:
name|indexedListener
control|)
block|{
name|listener
operator|.
name|onProjectIndexed
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getWriteIndexes ()
specifier|private
name|Collection
argument_list|<
name|ProjectIndex
argument_list|>
name|getWriteIndexes
parameter_list|()
block|{
if|if
condition|(
name|indexes
operator|!=
literal|null
condition|)
block|{
return|return
name|indexes
operator|.
name|getWriteIndexes
argument_list|()
return|;
block|}
return|return
name|index
operator|!=
literal|null
condition|?
name|Collections
operator|.
name|singleton
argument_list|(
name|index
argument_list|)
else|:
name|ImmutableSet
operator|.
name|of
argument_list|()
return|;
block|}
block|}
end_class

end_unit

