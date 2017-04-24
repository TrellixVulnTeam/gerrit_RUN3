begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2014 The Android Open Source Project
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
import|import static
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|FieldNamingPolicy
operator|.
name|LOWER_CASE_WITH_UNDERSCORES
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
operator|.
name|toList
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|binary
operator|.
name|Base64
operator|.
name|decodeBase64
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentFactory
operator|.
name|jsonBuilder
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
name|base
operator|.
name|Strings
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
name|FluentIterable
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
name|Iterables
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
name|Streams
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
name|index
operator|.
name|FieldDef
operator|.
name|FillArgs
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
name|server
operator|.
name|index
operator|.
name|Schema
operator|.
name|Values
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
name|Gson
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
name|GsonBuilder
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
name|JsonObject
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
name|protobuf
operator|.
name|ProtobufCodec
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
name|client
operator|.
name|http
operator|.
name|JestHttpClient
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
name|Delete
import|;
end_import

begin_import
import|import
name|io
operator|.
name|searchbox
operator|.
name|indices
operator|.
name|CreateIndex
import|;
end_import

begin_import
import|import
name|io
operator|.
name|searchbox
operator|.
name|indices
operator|.
name|DeleteIndex
import|;
end_import

begin_import
import|import
name|io
operator|.
name|searchbox
operator|.
name|indices
operator|.
name|IndicesExists
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

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentBuilder
import|;
end_import

begin_class
DECL|class|AbstractElasticIndex
specifier|abstract
class|class
name|AbstractElasticIndex
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
implements|implements
name|Index
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
DECL|method|decodeProtos ( JsonObject doc, String fieldName, ProtobufCodec<T> codec)
specifier|protected
specifier|static
parameter_list|<
name|T
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|decodeProtos
parameter_list|(
name|JsonObject
name|doc
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|ProtobufCodec
argument_list|<
name|T
argument_list|>
name|codec
parameter_list|)
block|{
name|JsonArray
name|field
init|=
name|doc
operator|.
name|getAsJsonArray
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|FluentIterable
operator|.
name|from
argument_list|(
name|field
argument_list|)
operator|.
name|transform
argument_list|(
name|i
lambda|->
name|codec
operator|.
name|decode
argument_list|(
name|decodeBase64
argument_list|(
name|i
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|toList
argument_list|()
return|;
block|}
DECL|field|schema
specifier|private
specifier|final
name|Schema
argument_list|<
name|V
argument_list|>
name|schema
decl_stmt|;
DECL|field|fillArgs
specifier|private
specifier|final
name|FillArgs
name|fillArgs
decl_stmt|;
DECL|field|sitePaths
specifier|private
specifier|final
name|SitePaths
name|sitePaths
decl_stmt|;
DECL|field|refresh
specifier|protected
specifier|final
name|boolean
name|refresh
decl_stmt|;
DECL|field|indexName
specifier|protected
specifier|final
name|String
name|indexName
decl_stmt|;
DECL|field|client
specifier|protected
specifier|final
name|JestHttpClient
name|client
decl_stmt|;
DECL|field|gson
specifier|protected
specifier|final
name|Gson
name|gson
decl_stmt|;
DECL|field|queryBuilder
specifier|protected
specifier|final
name|ElasticQueryBuilder
name|queryBuilder
decl_stmt|;
DECL|method|AbstractElasticIndex ( @erritServerConfig Config cfg, FillArgs fillArgs, SitePaths sitePaths, Schema<V> schema, JestClientBuilder clientBuilder, String indexName)
name|AbstractElasticIndex
parameter_list|(
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|,
name|FillArgs
name|fillArgs
parameter_list|,
name|SitePaths
name|sitePaths
parameter_list|,
name|Schema
argument_list|<
name|V
argument_list|>
name|schema
parameter_list|,
name|JestClientBuilder
name|clientBuilder
parameter_list|,
name|String
name|indexName
parameter_list|)
block|{
name|this
operator|.
name|fillArgs
operator|=
name|fillArgs
expr_stmt|;
name|this
operator|.
name|sitePaths
operator|=
name|sitePaths
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|this
operator|.
name|gson
operator|=
operator|new
name|GsonBuilder
argument_list|()
operator|.
name|setFieldNamingPolicy
argument_list|(
name|LOWER_CASE_WITH_UNDERSCORES
argument_list|)
operator|.
name|create
argument_list|()
expr_stmt|;
name|this
operator|.
name|queryBuilder
operator|=
operator|new
name|ElasticQueryBuilder
argument_list|()
expr_stmt|;
name|this
operator|.
name|indexName
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"%s%s%04d"
argument_list|,
name|Strings
operator|.
name|nullToEmpty
argument_list|(
name|cfg
operator|.
name|getString
argument_list|(
literal|"index"
argument_list|,
literal|null
argument_list|,
literal|"prefix"
argument_list|)
argument_list|)
argument_list|,
name|indexName
argument_list|,
name|schema
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|refresh
operator|=
name|clientBuilder
operator|.
name|refresh
expr_stmt|;
name|this
operator|.
name|client
operator|=
name|clientBuilder
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSchema ()
specifier|public
name|Schema
argument_list|<
name|V
argument_list|>
name|getSchema
parameter_list|()
block|{
return|return
name|schema
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
name|client
operator|.
name|shutdownClient
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|markReady (boolean ready)
specifier|public
name|void
name|markReady
parameter_list|(
name|boolean
name|ready
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexUtils
operator|.
name|setReady
argument_list|(
name|sitePaths
argument_list|,
name|indexName
argument_list|,
name|schema
operator|.
name|getVersion
argument_list|()
argument_list|,
name|ready
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|delete (K c)
specifier|public
name|void
name|delete
parameter_list|(
name|K
name|c
parameter_list|)
throws|throws
name|IOException
block|{
name|Bulk
name|bulk
init|=
name|addActions
argument_list|(
operator|new
name|Bulk
operator|.
name|Builder
argument_list|()
argument_list|,
name|c
argument_list|)
operator|.
name|refresh
argument_list|(
name|refresh
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
literal|"Failed to delete change %s in index %s: %s"
argument_list|,
name|c
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
DECL|method|deleteAll ()
specifier|public
name|void
name|deleteAll
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Delete the index, if it exists.
name|JestResult
name|result
init|=
name|client
operator|.
name|execute
argument_list|(
operator|new
name|IndicesExists
operator|.
name|Builder
argument_list|(
name|indexName
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|.
name|isSucceeded
argument_list|()
condition|)
block|{
name|result
operator|=
name|client
operator|.
name|execute
argument_list|(
operator|new
name|DeleteIndex
operator|.
name|Builder
argument_list|(
name|indexName
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
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
literal|"Failed to delete index %s: %s"
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
comment|// Recreate the index.
name|result
operator|=
name|client
operator|.
name|execute
argument_list|(
operator|new
name|CreateIndex
operator|.
name|Builder
argument_list|(
name|indexName
argument_list|)
operator|.
name|settings
argument_list|(
name|getMappings
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|result
operator|.
name|isSucceeded
argument_list|()
condition|)
block|{
name|String
name|error
init|=
name|String
operator|.
name|format
argument_list|(
literal|"Failed to create index %s: %s"
argument_list|,
name|indexName
argument_list|,
name|result
operator|.
name|getErrorMessage
argument_list|()
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|error
argument_list|)
throw|;
block|}
block|}
DECL|method|addActions (Bulk.Builder builder, K c)
specifier|protected
specifier|abstract
name|Bulk
operator|.
name|Builder
name|addActions
parameter_list|(
name|Bulk
operator|.
name|Builder
name|builder
parameter_list|,
name|K
name|c
parameter_list|)
function_decl|;
DECL|method|getMappings ()
specifier|protected
specifier|abstract
name|String
name|getMappings
parameter_list|()
function_decl|;
DECL|method|getId (V v)
specifier|protected
specifier|abstract
name|String
name|getId
parameter_list|(
name|V
name|v
parameter_list|)
function_decl|;
DECL|method|delete (String type, K c)
specifier|protected
name|Delete
name|delete
parameter_list|(
name|String
name|type
parameter_list|,
name|K
name|c
parameter_list|)
block|{
name|String
name|id
init|=
name|c
operator|.
name|toString
argument_list|()
decl_stmt|;
return|return
operator|new
name|Delete
operator|.
name|Builder
argument_list|(
name|id
argument_list|)
operator|.
name|index
argument_list|(
name|indexName
argument_list|)
operator|.
name|type
argument_list|(
name|type
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|insert (String type, V v)
specifier|protected
name|io
operator|.
name|searchbox
operator|.
name|core
operator|.
name|Index
name|insert
parameter_list|(
name|String
name|type
parameter_list|,
name|V
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|id
init|=
name|getId
argument_list|(
name|v
argument_list|)
decl_stmt|;
name|String
name|doc
init|=
name|toDoc
argument_list|(
name|v
argument_list|)
decl_stmt|;
return|return
operator|new
name|io
operator|.
name|searchbox
operator|.
name|core
operator|.
name|Index
operator|.
name|Builder
argument_list|(
name|doc
argument_list|)
operator|.
name|index
argument_list|(
name|indexName
argument_list|)
operator|.
name|type
argument_list|(
name|type
argument_list|)
operator|.
name|id
argument_list|(
name|id
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|shouldAddElement (Object element)
specifier|private
specifier|static
name|boolean
name|shouldAddElement
parameter_list|(
name|Object
name|element
parameter_list|)
block|{
return|return
operator|!
operator|(
name|element
operator|instanceof
name|String
operator|)
operator|||
operator|!
operator|(
operator|(
name|String
operator|)
name|element
operator|)
operator|.
name|isEmpty
argument_list|()
return|;
block|}
DECL|method|toDoc (V v)
specifier|private
name|String
name|toDoc
parameter_list|(
name|V
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentBuilder
name|builder
init|=
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
decl_stmt|;
for|for
control|(
name|Values
argument_list|<
name|V
argument_list|>
name|values
range|:
name|schema
operator|.
name|buildFields
argument_list|(
name|v
argument_list|,
name|fillArgs
argument_list|)
control|)
block|{
name|String
name|name
init|=
name|values
operator|.
name|getField
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|values
operator|.
name|getField
argument_list|()
operator|.
name|isRepeatable
argument_list|()
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|name
argument_list|,
name|Streams
operator|.
name|stream
argument_list|(
name|values
operator|.
name|getValues
argument_list|()
argument_list|)
operator|.
name|filter
argument_list|(
name|e
lambda|->
name|shouldAddElement
argument_list|(
name|e
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Object
name|element
init|=
name|Iterables
operator|.
name|getOnlyElement
argument_list|(
name|values
operator|.
name|getValues
argument_list|()
argument_list|,
literal|""
argument_list|)
decl_stmt|;
if|if
condition|(
name|shouldAddElement
argument_list|(
name|element
argument_list|)
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|name
argument_list|,
name|element
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|builder
operator|.
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
return|;
block|}
block|}
end_class

end_unit

