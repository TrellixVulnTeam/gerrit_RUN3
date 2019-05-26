begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2018 The Android Open Source Project
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
name|common
operator|.
name|collect
operator|.
name|MultimapBuilder
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
name|SetMultimap
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
name|index
operator|.
name|IndexConfig
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
name|QueryOptions
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
name|RefState
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
name|gerrit
operator|.
name|index
operator|.
name|project
operator|.
name|ProjectField
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
name|project
operator|.
name|ProjectIndex
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
name|project
operator|.
name|ProjectIndexCollection
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
name|query
operator|.
name|FieldBundle
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
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_class
DECL|class|StalenessChecker
specifier|public
class|class
name|StalenessChecker
block|{
DECL|field|FIELDS
specifier|private
specifier|static
specifier|final
name|ImmutableSet
argument_list|<
name|String
argument_list|>
name|FIELDS
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|ProjectField
operator|.
name|NAME
operator|.
name|getName
argument_list|()
argument_list|,
name|ProjectField
operator|.
name|REF_STATE
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|projectCache
specifier|private
specifier|final
name|ProjectCache
name|projectCache
decl_stmt|;
DECL|field|indexes
specifier|private
specifier|final
name|ProjectIndexCollection
name|indexes
decl_stmt|;
DECL|field|indexConfig
specifier|private
specifier|final
name|IndexConfig
name|indexConfig
decl_stmt|;
annotation|@
name|Inject
DECL|method|StalenessChecker ( ProjectCache projectCache, ProjectIndexCollection indexes, IndexConfig indexConfig)
name|StalenessChecker
parameter_list|(
name|ProjectCache
name|projectCache
parameter_list|,
name|ProjectIndexCollection
name|indexes
parameter_list|,
name|IndexConfig
name|indexConfig
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
name|indexes
operator|=
name|indexes
expr_stmt|;
name|this
operator|.
name|indexConfig
operator|=
name|indexConfig
expr_stmt|;
block|}
DECL|method|isStale (Project.NameKey project)
specifier|public
name|boolean
name|isStale
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|)
block|{
name|ProjectData
name|projectData
init|=
name|projectCache
operator|.
name|get
argument_list|(
name|project
argument_list|)
operator|.
name|toProjectData
argument_list|()
decl_stmt|;
name|ProjectIndex
name|i
init|=
name|indexes
operator|.
name|getSearchIndex
argument_list|()
decl_stmt|;
if|if
condition|(
name|i
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
comment|// No index; caller couldn't do anything if it is stale.
block|}
name|Optional
argument_list|<
name|FieldBundle
argument_list|>
name|result
init|=
name|i
operator|.
name|getRaw
argument_list|(
name|project
argument_list|,
name|QueryOptions
operator|.
name|create
argument_list|(
name|indexConfig
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
name|FIELDS
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|result
operator|.
name|isPresent
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
name|SetMultimap
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|,
name|RefState
argument_list|>
name|indexedRefStates
init|=
name|RefState
operator|.
name|parseStates
argument_list|(
name|result
operator|.
name|get
argument_list|()
operator|.
name|getValue
argument_list|(
name|ProjectField
operator|.
name|REF_STATE
argument_list|)
argument_list|)
decl_stmt|;
name|SetMultimap
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|,
name|RefState
argument_list|>
name|currentRefStates
init|=
name|MultimapBuilder
operator|.
name|hashKeys
argument_list|()
operator|.
name|hashSetValues
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
name|projectData
operator|.
name|tree
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|p
lambda|->
name|p
operator|.
name|getProject
argument_list|()
operator|.
name|getConfigRefState
argument_list|()
operator|!=
literal|null
argument_list|)
operator|.
name|forEach
argument_list|(
name|p
lambda|->
name|currentRefStates
operator|.
name|put
argument_list|(
name|p
operator|.
name|getProject
argument_list|()
operator|.
name|getNameKey
argument_list|()
argument_list|,
name|RefState
operator|.
name|create
argument_list|(
name|RefNames
operator|.
name|REFS_CONFIG
argument_list|,
name|p
operator|.
name|getProject
argument_list|()
operator|.
name|getConfigRefState
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|!
name|currentRefStates
operator|.
name|equals
argument_list|(
name|indexedRefStates
argument_list|)
return|;
block|}
block|}
end_class

end_unit

