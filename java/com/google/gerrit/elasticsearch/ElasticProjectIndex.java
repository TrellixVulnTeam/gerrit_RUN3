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
DECL|package|com.google.gerrit.elasticsearch
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|elasticsearch
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
name|elasticsearch
operator|.
name|ElasticMapping
operator|.
name|MappingProperties
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
name|elasticsearch
operator|.
name|bulk
operator|.
name|BulkRequest
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
name|elasticsearch
operator|.
name|bulk
operator|.
name|IndexRequest
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
name|elasticsearch
operator|.
name|bulk
operator|.
name|UpdateRequest
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
name|exceptions
operator|.
name|StorageException
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
name|Schema
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
name|query
operator|.
name|DataSource
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
name|Predicate
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
name|QueryParseException
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
name|config
operator|.
name|SitePaths
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
name|IndexUtils
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
name|gson
operator|.
name|JsonArray
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|JsonElement
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|JsonObject
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
name|assistedinject
operator|.
name|Assisted
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpStatus
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Response
import|;
end_import

begin_class
DECL|class|ElasticProjectIndex
specifier|public
class|class
name|ElasticProjectIndex
extends|extends
name|AbstractElasticIndex
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|,
name|ProjectData
argument_list|>
implements|implements
name|ProjectIndex
block|{
DECL|class|ProjectMapping
specifier|static
class|class
name|ProjectMapping
block|{
DECL|field|projects
name|MappingProperties
name|projects
decl_stmt|;
DECL|method|ProjectMapping (Schema<ProjectData> schema, ElasticQueryAdapter adapter)
name|ProjectMapping
parameter_list|(
name|Schema
argument_list|<
name|ProjectData
argument_list|>
name|schema
parameter_list|,
name|ElasticQueryAdapter
name|adapter
parameter_list|)
block|{
name|this
operator|.
name|projects
operator|=
name|ElasticMapping
operator|.
name|createMapping
argument_list|(
name|schema
argument_list|,
name|adapter
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|PROJECTS
specifier|static
specifier|final
name|String
name|PROJECTS
init|=
literal|"projects"
decl_stmt|;
DECL|field|mapping
specifier|private
specifier|final
name|ProjectMapping
name|mapping
decl_stmt|;
DECL|field|projectCache
specifier|private
specifier|final
name|Provider
argument_list|<
name|ProjectCache
argument_list|>
name|projectCache
decl_stmt|;
DECL|field|schema
specifier|private
specifier|final
name|Schema
argument_list|<
name|ProjectData
argument_list|>
name|schema
decl_stmt|;
annotation|@
name|Inject
DECL|method|ElasticProjectIndex ( ElasticConfiguration cfg, SitePaths sitePaths, Provider<ProjectCache> projectCache, ElasticRestClientProvider client, @Assisted Schema<ProjectData> schema)
name|ElasticProjectIndex
parameter_list|(
name|ElasticConfiguration
name|cfg
parameter_list|,
name|SitePaths
name|sitePaths
parameter_list|,
name|Provider
argument_list|<
name|ProjectCache
argument_list|>
name|projectCache
parameter_list|,
name|ElasticRestClientProvider
name|client
parameter_list|,
annotation|@
name|Assisted
name|Schema
argument_list|<
name|ProjectData
argument_list|>
name|schema
parameter_list|)
block|{
name|super
argument_list|(
name|cfg
argument_list|,
name|sitePaths
argument_list|,
name|schema
argument_list|,
name|client
argument_list|,
name|PROJECTS
argument_list|)
expr_stmt|;
name|this
operator|.
name|projectCache
operator|=
name|projectCache
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|this
operator|.
name|mapping
operator|=
operator|new
name|ProjectMapping
argument_list|(
name|schema
argument_list|,
name|client
operator|.
name|adapter
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|replace (ProjectData projectState)
specifier|public
name|void
name|replace
parameter_list|(
name|ProjectData
name|projectState
parameter_list|)
block|{
name|BulkRequest
name|bulk
init|=
operator|new
name|IndexRequest
argument_list|(
name|projectState
operator|.
name|getProject
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|indexName
argument_list|,
name|type
argument_list|,
name|client
operator|.
name|adapter
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|UpdateRequest
argument_list|<>
argument_list|(
name|schema
argument_list|,
name|projectState
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|uri
init|=
name|getURI
argument_list|(
name|type
argument_list|,
name|BULK
argument_list|)
decl_stmt|;
name|Response
name|response
init|=
name|postRequest
argument_list|(
name|uri
argument_list|,
name|bulk
argument_list|,
name|getRefreshParam
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|statusCode
init|=
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
decl_stmt|;
if|if
condition|(
name|statusCode
operator|!=
name|HttpStatus
operator|.
name|SC_OK
condition|)
block|{
throw|throw
operator|new
name|StorageException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Failed to replace project %s in index %s: %s"
argument_list|,
name|projectState
operator|.
name|getProject
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|indexName
argument_list|,
name|statusCode
argument_list|)
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getSource (Predicate<ProjectData> p, QueryOptions opts)
specifier|public
name|DataSource
argument_list|<
name|ProjectData
argument_list|>
name|getSource
parameter_list|(
name|Predicate
argument_list|<
name|ProjectData
argument_list|>
name|p
parameter_list|,
name|QueryOptions
name|opts
parameter_list|)
throws|throws
name|QueryParseException
block|{
name|JsonArray
name|sortArray
init|=
name|getSortArray
argument_list|(
name|ProjectField
operator|.
name|NAME
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|ElasticQuerySource
argument_list|(
name|p
argument_list|,
name|opts
operator|.
name|filterFields
argument_list|(
name|IndexUtils
operator|::
name|projectFields
argument_list|)
argument_list|,
name|type
argument_list|,
name|sortArray
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDeleteActions (Project.NameKey nameKey)
specifier|protected
name|String
name|getDeleteActions
parameter_list|(
name|Project
operator|.
name|NameKey
name|nameKey
parameter_list|)
block|{
return|return
name|delete
argument_list|(
name|type
argument_list|,
name|nameKey
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getMappings ()
specifier|protected
name|String
name|getMappings
parameter_list|()
block|{
return|return
name|getMappingsForSingleType
argument_list|(
name|PROJECTS
argument_list|,
name|mapping
operator|.
name|projects
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getId (ProjectData projectState)
specifier|protected
name|String
name|getId
parameter_list|(
name|ProjectData
name|projectState
parameter_list|)
block|{
return|return
name|projectState
operator|.
name|getProject
argument_list|()
operator|.
name|getName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|fromDocument (JsonObject json, Set<String> fields)
specifier|protected
name|ProjectData
name|fromDocument
parameter_list|(
name|JsonObject
name|json
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|fields
parameter_list|)
block|{
name|JsonElement
name|source
init|=
name|json
operator|.
name|get
argument_list|(
literal|"_source"
argument_list|)
decl_stmt|;
if|if
condition|(
name|source
operator|==
literal|null
condition|)
block|{
name|source
operator|=
name|json
operator|.
name|getAsJsonObject
argument_list|()
operator|.
name|get
argument_list|(
literal|"fields"
argument_list|)
expr_stmt|;
block|}
name|Project
operator|.
name|NameKey
name|nameKey
init|=
name|Project
operator|.
name|nameKey
argument_list|(
name|source
operator|.
name|getAsJsonObject
argument_list|()
operator|.
name|get
argument_list|(
name|ProjectField
operator|.
name|NAME
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|getAsString
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|projectCache
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|(
name|nameKey
argument_list|)
operator|.
name|toProjectData
argument_list|()
return|;
block|}
block|}
end_class

end_unit

