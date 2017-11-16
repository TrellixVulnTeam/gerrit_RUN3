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
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
name|config
operator|.
name|GerritServerConfig
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
name|group
operator|.
name|InternalGroup
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
name|index
operator|.
name|group
operator|.
name|GroupField
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
name|group
operator|.
name|GroupIndex
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
name|io
operator|.
name|searchbox
operator|.
name|client
operator|.
name|JestResult
import|;
end_import

begin_import
import|import
name|io
operator|.
name|searchbox
operator|.
name|core
operator|.
name|Bulk
import|;
end_import

begin_import
import|import
name|io
operator|.
name|searchbox
operator|.
name|core
operator|.
name|Bulk
operator|.
name|Builder
import|;
end_import

begin_import
import|import
name|io
operator|.
name|searchbox
operator|.
name|core
operator|.
name|search
operator|.
name|sort
operator|.
name|Sort
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
name|Set
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
name|Config
import|;
end_import

begin_class
DECL|class|ElasticGroupIndex
specifier|public
class|class
name|ElasticGroupIndex
extends|extends
name|AbstractElasticIndex
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|,
name|InternalGroup
argument_list|>
implements|implements
name|GroupIndex
block|{
DECL|class|GroupMapping
specifier|static
class|class
name|GroupMapping
block|{
DECL|field|groups
name|MappingProperties
name|groups
decl_stmt|;
DECL|method|GroupMapping (Schema<InternalGroup> schema)
name|GroupMapping
parameter_list|(
name|Schema
argument_list|<
name|InternalGroup
argument_list|>
name|schema
parameter_list|)
block|{
name|this
operator|.
name|groups
operator|=
name|ElasticMapping
operator|.
name|createMapping
argument_list|(
name|schema
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|GROUPS
specifier|static
specifier|final
name|String
name|GROUPS
init|=
literal|"groups"
decl_stmt|;
DECL|field|GROUPS_PREFIX
specifier|static
specifier|final
name|String
name|GROUPS_PREFIX
init|=
name|GROUPS
operator|+
literal|"_"
decl_stmt|;
DECL|field|mapping
specifier|private
specifier|final
name|GroupMapping
name|mapping
decl_stmt|;
DECL|field|groupCache
specifier|private
specifier|final
name|Provider
argument_list|<
name|GroupCache
argument_list|>
name|groupCache
decl_stmt|;
annotation|@
name|Inject
DECL|method|ElasticGroupIndex ( @erritServerConfig Config cfg, SitePaths sitePaths, Provider<GroupCache> groupCache, JestClientBuilder clientBuilder, @Assisted Schema<InternalGroup> schema)
name|ElasticGroupIndex
parameter_list|(
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|,
name|SitePaths
name|sitePaths
parameter_list|,
name|Provider
argument_list|<
name|GroupCache
argument_list|>
name|groupCache
parameter_list|,
name|JestClientBuilder
name|clientBuilder
parameter_list|,
annotation|@
name|Assisted
name|Schema
argument_list|<
name|InternalGroup
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
name|clientBuilder
argument_list|,
name|GROUPS_PREFIX
argument_list|)
expr_stmt|;
name|this
operator|.
name|groupCache
operator|=
name|groupCache
expr_stmt|;
name|this
operator|.
name|mapping
operator|=
operator|new
name|GroupMapping
argument_list|(
name|schema
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|replace (InternalGroup group)
specifier|public
name|void
name|replace
parameter_list|(
name|InternalGroup
name|group
parameter_list|)
throws|throws
name|IOException
block|{
name|Bulk
name|bulk
init|=
operator|new
name|Bulk
operator|.
name|Builder
argument_list|()
operator|.
name|defaultIndex
argument_list|(
name|indexName
argument_list|)
operator|.
name|defaultType
argument_list|(
name|GROUPS
argument_list|)
operator|.
name|addAction
argument_list|(
name|insert
argument_list|(
name|GROUPS
argument_list|,
name|group
argument_list|)
argument_list|)
operator|.
name|refresh
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|JestResult
name|result
init|=
name|client
operator|.
name|execute
argument_list|(
name|bulk
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|result
operator|.
name|isSucceeded
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Failed to replace group %s in index %s: %s"
argument_list|,
name|group
operator|.
name|getGroupUUID
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|indexName
argument_list|,
name|result
operator|.
name|getErrorMessage
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getSource (Predicate<InternalGroup> p, QueryOptions opts)
specifier|public
name|DataSource
argument_list|<
name|InternalGroup
argument_list|>
name|getSource
parameter_list|(
name|Predicate
argument_list|<
name|InternalGroup
argument_list|>
name|p
parameter_list|,
name|QueryOptions
name|opts
parameter_list|)
throws|throws
name|QueryParseException
block|{
name|Sort
name|sort
init|=
operator|new
name|Sort
argument_list|(
name|GroupField
operator|.
name|UUID
operator|.
name|getName
argument_list|()
argument_list|,
name|Sort
operator|.
name|Sorting
operator|.
name|ASC
argument_list|)
decl_stmt|;
name|sort
operator|.
name|setIgnoreUnmapped
argument_list|()
expr_stmt|;
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
name|groupFields
argument_list|)
argument_list|,
name|GROUPS
argument_list|,
name|sort
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|addActions (Builder builder, AccountGroup.UUID c)
specifier|protected
name|Builder
name|addActions
parameter_list|(
name|Builder
name|builder
parameter_list|,
name|AccountGroup
operator|.
name|UUID
name|c
parameter_list|)
block|{
return|return
name|builder
operator|.
name|addAction
argument_list|(
name|delete
argument_list|(
name|GROUPS
argument_list|,
name|c
argument_list|)
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
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|GroupMapping
argument_list|>
name|mappings
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"mappings"
argument_list|,
name|mapping
argument_list|)
decl_stmt|;
return|return
name|gson
operator|.
name|toJson
argument_list|(
name|mappings
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getId (InternalGroup group)
specifier|protected
name|String
name|getId
parameter_list|(
name|InternalGroup
name|group
parameter_list|)
block|{
return|return
name|group
operator|.
name|getGroupUUID
argument_list|()
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|fromDocument (JsonObject json, Set<String> fields)
specifier|protected
name|InternalGroup
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
name|AccountGroup
operator|.
name|UUID
name|uuid
init|=
operator|new
name|AccountGroup
operator|.
name|UUID
argument_list|(
name|source
operator|.
name|getAsJsonObject
argument_list|()
operator|.
name|get
argument_list|(
name|GroupField
operator|.
name|UUID
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|getAsString
argument_list|()
argument_list|)
decl_stmt|;
comment|// Use the GroupCache rather than depending on any stored fields in the
comment|// document (of which there shouldn't be any).
return|return
name|groupCache
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|(
name|uuid
argument_list|)
operator|.
name|orElse
argument_list|(
literal|null
argument_list|)
return|;
block|}
block|}
end_class

end_unit

