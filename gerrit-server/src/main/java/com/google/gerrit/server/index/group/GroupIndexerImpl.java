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
DECL|package|com.google.gerrit.server.index.group
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
name|group
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
name|reviewdb
operator|.
name|client
operator|.
name|AccountGroup
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
name|GroupCache
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
name|Index
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
DECL|class|GroupIndexerImpl
specifier|public
class|class
name|GroupIndexerImpl
implements|implements
name|GroupIndexer
block|{
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (GroupIndexCollection indexes)
name|GroupIndexerImpl
name|create
parameter_list|(
name|GroupIndexCollection
name|indexes
parameter_list|)
function_decl|;
DECL|method|create (@ullable GroupIndex index)
name|GroupIndexerImpl
name|create
parameter_list|(
annotation|@
name|Nullable
name|GroupIndex
name|index
parameter_list|)
function_decl|;
block|}
DECL|field|groupCache
specifier|private
specifier|final
name|GroupCache
name|groupCache
decl_stmt|;
DECL|field|indexes
specifier|private
specifier|final
name|GroupIndexCollection
name|indexes
decl_stmt|;
DECL|field|index
specifier|private
specifier|final
name|GroupIndex
name|index
decl_stmt|;
annotation|@
name|AssistedInject
DECL|method|GroupIndexerImpl (GroupCache groupCache, @Assisted GroupIndexCollection indexes)
name|GroupIndexerImpl
parameter_list|(
name|GroupCache
name|groupCache
parameter_list|,
annotation|@
name|Assisted
name|GroupIndexCollection
name|indexes
parameter_list|)
block|{
name|this
operator|.
name|groupCache
operator|=
name|groupCache
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
DECL|method|GroupIndexerImpl (GroupCache groupCache, @Assisted GroupIndex index)
name|GroupIndexerImpl
parameter_list|(
name|GroupCache
name|groupCache
parameter_list|,
annotation|@
name|Assisted
name|GroupIndex
name|index
parameter_list|)
block|{
name|this
operator|.
name|groupCache
operator|=
name|groupCache
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
DECL|method|index (AccountGroup.UUID uuid)
specifier|public
name|void
name|index
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|uuid
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
name|AccountGroup
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
name|groupCache
operator|.
name|get
argument_list|(
name|uuid
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getWriteIndexes ()
specifier|private
name|Collection
argument_list|<
name|GroupIndex
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

