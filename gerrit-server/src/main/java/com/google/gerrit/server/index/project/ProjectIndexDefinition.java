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
name|Inject
import|;
end_import

begin_class
DECL|class|ProjectIndexDefinition
specifier|public
class|class
name|ProjectIndexDefinition
extends|extends
name|IndexDefinition
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|,
name|ProjectData
argument_list|,
name|ProjectIndex
argument_list|>
block|{
annotation|@
name|Inject
DECL|method|ProjectIndexDefinition ( ProjectIndexCollection indexCollection, ProjectIndex.Factory indexFactory, @Nullable AllProjectsIndexer allProjectsIndexer)
name|ProjectIndexDefinition
parameter_list|(
name|ProjectIndexCollection
name|indexCollection
parameter_list|,
name|ProjectIndex
operator|.
name|Factory
name|indexFactory
parameter_list|,
annotation|@
name|Nullable
name|AllProjectsIndexer
name|allProjectsIndexer
parameter_list|)
block|{
name|super
argument_list|(
name|ProjectSchemaDefinitions
operator|.
name|INSTANCE
argument_list|,
name|indexCollection
argument_list|,
name|indexFactory
argument_list|,
name|allProjectsIndexer
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

