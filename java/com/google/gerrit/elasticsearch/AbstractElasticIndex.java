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
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkArgument
import|;
end_import

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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
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
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ArrayListMultimap
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
name|ListMultimap
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
name|common
operator|.
name|flogger
operator|.
name|FluentLogger
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
name|CharStreams
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
name|elasticsearch
operator|.
name|builders
operator|.
name|QueryBuilder
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
name|builders
operator|.
name|SearchSourceBuilder
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
name|DeleteRequest
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
name|FieldDef
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
name|FieldType
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
name|gson
operator|.
name|JsonParser
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
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|server
operator|.
name|OrmException
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
name|server
operator|.
name|ResultSet
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLEncoder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Timestamp
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Map
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
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
import|;
end_import

begin_import
import|import
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
name|HttpEntity
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
name|apache
operator|.
name|http
operator|.
name|StatusLine
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
name|client
operator|.
name|methods
operator|.
name|HttpPost
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
name|entity
operator|.
name|ContentType
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
name|nio
operator|.
name|entity
operator|.
name|NStringEntity
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
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|FluentLogger
name|logger
init|=
name|FluentLogger
operator|.
name|forEnclosingClass
argument_list|()
decl_stmt|;
DECL|field|BULK
specifier|protected
specifier|static
specifier|final
name|String
name|BULK
init|=
literal|"_bulk"
decl_stmt|;
DECL|field|MAPPINGS
specifier|protected
specifier|static
specifier|final
name|String
name|MAPPINGS
init|=
literal|"mappings"
decl_stmt|;
DECL|field|ORDER
specifier|protected
specifier|static
specifier|final
name|String
name|ORDER
init|=
literal|"order"
decl_stmt|;
DECL|field|SEARCH
specifier|protected
specifier|static
specifier|final
name|String
name|SEARCH
init|=
literal|"_search"
decl_stmt|;
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
DECL|method|getContent (Response response)
specifier|static
name|String
name|getContent
parameter_list|(
name|Response
name|response
parameter_list|)
throws|throws
name|IOException
block|{
name|HttpEntity
name|responseEntity
init|=
name|response
operator|.
name|getEntity
argument_list|()
decl_stmt|;
name|String
name|content
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|responseEntity
operator|!=
literal|null
condition|)
block|{
name|InputStream
name|contentStream
init|=
name|responseEntity
operator|.
name|getContent
argument_list|()
decl_stmt|;
try|try
init|(
name|Reader
name|reader
init|=
operator|new
name|InputStreamReader
argument_list|(
name|contentStream
argument_list|)
init|)
block|{
name|content
operator|=
name|CharStreams
operator|.
name|toString
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|content
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
DECL|field|sitePaths
specifier|private
specifier|final
name|SitePaths
name|sitePaths
decl_stmt|;
DECL|field|indexNameRaw
specifier|private
specifier|final
name|String
name|indexNameRaw
decl_stmt|;
DECL|field|type
specifier|protected
specifier|final
name|String
name|type
decl_stmt|;
DECL|field|client
specifier|protected
specifier|final
name|ElasticRestClientProvider
name|client
decl_stmt|;
DECL|field|indexName
specifier|protected
specifier|final
name|String
name|indexName
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
DECL|method|AbstractElasticIndex ( ElasticConfiguration cfg, SitePaths sitePaths, Schema<V> schema, ElasticRestClientProvider client, String indexName)
name|AbstractElasticIndex
parameter_list|(
name|ElasticConfiguration
name|cfg
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
name|ElasticRestClientProvider
name|client
parameter_list|,
name|String
name|indexName
parameter_list|)
block|{
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
name|cfg
operator|.
name|getIndexName
argument_list|(
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
name|indexNameRaw
operator|=
name|indexName
expr_stmt|;
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|client
operator|.
name|adapter
argument_list|()
operator|.
name|getType
argument_list|(
name|indexName
argument_list|)
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
comment|// Do nothing. Client is closed by the provider.
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
name|indexNameRaw
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
DECL|method|delete (K id)
specifier|public
name|void
name|delete
parameter_list|(
name|K
name|id
parameter_list|)
throws|throws
name|IOException
block|{
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
name|getDeleteActions
argument_list|(
name|id
argument_list|)
argument_list|,
name|uri
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
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Failed to delete %s from index %s: %s"
argument_list|,
name|id
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
DECL|method|deleteAll ()
specifier|public
name|void
name|deleteAll
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Delete the index, if it exists.
name|String
name|endpoint
init|=
name|indexName
operator|+
name|client
operator|.
name|adapter
argument_list|()
operator|.
name|indicesExistParam
argument_list|()
decl_stmt|;
name|Response
name|response
init|=
name|client
operator|.
name|get
argument_list|()
operator|.
name|performRequest
argument_list|(
literal|"HEAD"
argument_list|,
name|endpoint
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
operator|==
name|HttpStatus
operator|.
name|SC_OK
condition|)
block|{
name|response
operator|=
name|client
operator|.
name|get
argument_list|()
operator|.
name|performRequest
argument_list|(
literal|"DELETE"
argument_list|,
name|indexName
argument_list|)
expr_stmt|;
name|statusCode
operator|=
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
expr_stmt|;
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
name|statusCode
argument_list|)
argument_list|)
throw|;
block|}
block|}
comment|// Recreate the index.
name|response
operator|=
name|performRequest
argument_list|(
literal|"PUT"
argument_list|,
name|getMappings
argument_list|()
argument_list|,
name|indexName
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
expr_stmt|;
name|statusCode
operator|=
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
expr_stmt|;
if|if
condition|(
name|statusCode
operator|!=
name|HttpStatus
operator|.
name|SC_OK
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
name|statusCode
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
DECL|method|getDeleteActions (K id)
specifier|protected
specifier|abstract
name|String
name|getDeleteActions
parameter_list|(
name|K
name|id
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
DECL|method|getMappingsForSingleType (String candidateType, MappingProperties properties)
specifier|protected
name|String
name|getMappingsForSingleType
parameter_list|(
name|String
name|candidateType
parameter_list|,
name|MappingProperties
name|properties
parameter_list|)
block|{
return|return
name|getMappingsFor
argument_list|(
name|client
operator|.
name|adapter
argument_list|()
operator|.
name|getType
argument_list|(
name|candidateType
argument_list|)
argument_list|,
name|properties
argument_list|)
return|;
block|}
DECL|method|getMappingsFor (String type, MappingProperties properties)
specifier|protected
name|String
name|getMappingsFor
parameter_list|(
name|String
name|type
parameter_list|,
name|MappingProperties
name|properties
parameter_list|)
block|{
name|JsonObject
name|mappingType
init|=
operator|new
name|JsonObject
argument_list|()
decl_stmt|;
name|mappingType
operator|.
name|add
argument_list|(
name|type
argument_list|,
name|gson
operator|.
name|toJsonTree
argument_list|(
name|properties
argument_list|)
argument_list|)
expr_stmt|;
name|JsonObject
name|mappings
init|=
operator|new
name|JsonObject
argument_list|()
decl_stmt|;
name|mappings
operator|.
name|add
argument_list|(
name|MAPPINGS
argument_list|,
name|gson
operator|.
name|toJsonTree
argument_list|(
name|mappingType
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|gson
operator|.
name|toJson
argument_list|(
name|mappings
argument_list|)
return|;
block|}
DECL|method|delete (String type, K id)
specifier|protected
name|String
name|delete
parameter_list|(
name|String
name|type
parameter_list|,
name|K
name|id
parameter_list|)
block|{
return|return
operator|new
name|DeleteRequest
argument_list|(
name|id
operator|.
name|toString
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
name|toString
argument_list|()
return|;
block|}
DECL|method|fromDocument (JsonObject doc, Set<String> fields)
specifier|protected
specifier|abstract
name|V
name|fromDocument
parameter_list|(
name|JsonObject
name|doc
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|fields
parameter_list|)
function_decl|;
DECL|method|toFieldBundle (JsonObject doc)
specifier|protected
name|FieldBundle
name|toFieldBundle
parameter_list|(
name|JsonObject
name|doc
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|FieldDef
argument_list|<
name|V
argument_list|,
name|?
argument_list|>
argument_list|>
name|allFields
init|=
name|getSchema
argument_list|()
operator|.
name|getFields
argument_list|()
decl_stmt|;
name|ListMultimap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|rawFields
init|=
name|ArrayListMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|JsonElement
argument_list|>
name|element
range|:
name|doc
operator|.
name|get
argument_list|(
name|client
operator|.
name|adapter
argument_list|()
operator|.
name|rawFieldsKey
argument_list|()
argument_list|)
operator|.
name|getAsJsonObject
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|checkArgument
argument_list|(
name|allFields
operator|.
name|containsKey
argument_list|(
name|element
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|,
literal|"Unrecognized field "
operator|+
name|element
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|FieldType
argument_list|<
name|?
argument_list|>
name|type
init|=
name|allFields
operator|.
name|get
argument_list|(
name|element
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|getType
argument_list|()
decl_stmt|;
name|Iterable
argument_list|<
name|JsonElement
argument_list|>
name|innerItems
init|=
name|element
operator|.
name|getValue
argument_list|()
operator|.
name|isJsonArray
argument_list|()
condition|?
name|element
operator|.
name|getValue
argument_list|()
operator|.
name|getAsJsonArray
argument_list|()
else|:
name|Collections
operator|.
name|singleton
argument_list|(
name|element
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|JsonElement
name|inner
range|:
name|innerItems
control|)
block|{
if|if
condition|(
name|type
operator|==
name|FieldType
operator|.
name|EXACT
operator|||
name|type
operator|==
name|FieldType
operator|.
name|FULL_TEXT
operator|||
name|type
operator|==
name|FieldType
operator|.
name|PREFIX
condition|)
block|{
name|rawFields
operator|.
name|put
argument_list|(
name|element
operator|.
name|getKey
argument_list|()
argument_list|,
name|inner
operator|.
name|getAsString
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|FieldType
operator|.
name|INTEGER
operator|||
name|type
operator|==
name|FieldType
operator|.
name|INTEGER_RANGE
condition|)
block|{
name|rawFields
operator|.
name|put
argument_list|(
name|element
operator|.
name|getKey
argument_list|()
argument_list|,
name|inner
operator|.
name|getAsInt
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|FieldType
operator|.
name|LONG
condition|)
block|{
name|rawFields
operator|.
name|put
argument_list|(
name|element
operator|.
name|getKey
argument_list|()
argument_list|,
name|inner
operator|.
name|getAsLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|FieldType
operator|.
name|TIMESTAMP
condition|)
block|{
name|rawFields
operator|.
name|put
argument_list|(
name|element
operator|.
name|getKey
argument_list|()
argument_list|,
operator|new
name|Timestamp
argument_list|(
name|inner
operator|.
name|getAsLong
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|FieldType
operator|.
name|STORED_ONLY
condition|)
block|{
name|rawFields
operator|.
name|put
argument_list|(
name|element
operator|.
name|getKey
argument_list|()
argument_list|,
name|Base64
operator|.
name|decodeBase64
argument_list|(
name|inner
operator|.
name|getAsString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
name|FieldType
operator|.
name|badFieldType
argument_list|(
name|type
argument_list|)
throw|;
block|}
block|}
block|}
return|return
operator|new
name|FieldBundle
argument_list|(
name|rawFields
argument_list|)
return|;
block|}
DECL|method|toAction (String type, String id, String action)
specifier|protected
name|String
name|toAction
parameter_list|(
name|String
name|type
parameter_list|,
name|String
name|id
parameter_list|,
name|String
name|action
parameter_list|)
block|{
name|JsonObject
name|properties
init|=
operator|new
name|JsonObject
argument_list|()
decl_stmt|;
name|properties
operator|.
name|addProperty
argument_list|(
literal|"_id"
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|properties
operator|.
name|addProperty
argument_list|(
literal|"_index"
argument_list|,
name|indexName
argument_list|)
expr_stmt|;
name|properties
operator|.
name|addProperty
argument_list|(
literal|"_type"
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|JsonObject
name|jsonAction
init|=
operator|new
name|JsonObject
argument_list|()
decl_stmt|;
name|jsonAction
operator|.
name|add
argument_list|(
name|action
argument_list|,
name|properties
argument_list|)
expr_stmt|;
return|return
name|jsonAction
operator|.
name|toString
argument_list|()
operator|+
name|System
operator|.
name|lineSeparator
argument_list|()
return|;
block|}
DECL|method|addNamedElement (String name, JsonObject element, JsonArray array)
specifier|protected
name|void
name|addNamedElement
parameter_list|(
name|String
name|name
parameter_list|,
name|JsonObject
name|element
parameter_list|,
name|JsonArray
name|array
parameter_list|)
block|{
name|JsonObject
name|arrayElement
init|=
operator|new
name|JsonObject
argument_list|()
decl_stmt|;
name|arrayElement
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|element
argument_list|)
expr_stmt|;
name|array
operator|.
name|add
argument_list|(
name|arrayElement
argument_list|)
expr_stmt|;
block|}
DECL|method|getRefreshParam ()
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getRefreshParam
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"refresh"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
return|return
name|params
return|;
block|}
DECL|method|getSearch (SearchSourceBuilder searchSource, JsonArray sortArray)
specifier|protected
name|String
name|getSearch
parameter_list|(
name|SearchSourceBuilder
name|searchSource
parameter_list|,
name|JsonArray
name|sortArray
parameter_list|)
block|{
name|JsonObject
name|search
init|=
operator|new
name|JsonParser
argument_list|()
operator|.
name|parse
argument_list|(
name|searchSource
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|getAsJsonObject
argument_list|()
decl_stmt|;
name|search
operator|.
name|add
argument_list|(
literal|"sort"
argument_list|,
name|sortArray
argument_list|)
expr_stmt|;
return|return
name|gson
operator|.
name|toJson
argument_list|(
name|search
argument_list|)
return|;
block|}
DECL|method|getSortArray (String idFieldName)
specifier|protected
name|JsonArray
name|getSortArray
parameter_list|(
name|String
name|idFieldName
parameter_list|)
block|{
name|JsonObject
name|properties
init|=
operator|new
name|JsonObject
argument_list|()
decl_stmt|;
name|properties
operator|.
name|addProperty
argument_list|(
name|ORDER
argument_list|,
literal|"asc"
argument_list|)
expr_stmt|;
name|client
operator|.
name|adapter
argument_list|()
operator|.
name|setIgnoreUnmapped
argument_list|(
name|properties
argument_list|)
expr_stmt|;
name|JsonArray
name|sortArray
init|=
operator|new
name|JsonArray
argument_list|()
decl_stmt|;
name|addNamedElement
argument_list|(
name|idFieldName
argument_list|,
name|properties
argument_list|,
name|sortArray
argument_list|)
expr_stmt|;
return|return
name|sortArray
return|;
block|}
DECL|method|getURI (String type, String request)
specifier|protected
name|String
name|getURI
parameter_list|(
name|String
name|type
parameter_list|,
name|String
name|request
parameter_list|)
throws|throws
name|UnsupportedEncodingException
block|{
name|String
name|encodedType
init|=
name|URLEncoder
operator|.
name|encode
argument_list|(
name|type
argument_list|,
name|UTF_8
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|encodedIndexName
init|=
name|URLEncoder
operator|.
name|encode
argument_list|(
name|indexName
argument_list|,
name|UTF_8
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|encodedIndexName
operator|+
literal|"/"
operator|+
name|encodedType
operator|+
literal|"/"
operator|+
name|request
return|;
block|}
DECL|method|postRequest (Object payload, String uri, Map<String, String> params)
specifier|protected
name|Response
name|postRequest
parameter_list|(
name|Object
name|payload
parameter_list|,
name|String
name|uri
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|performRequest
argument_list|(
literal|"POST"
argument_list|,
name|payload
argument_list|,
name|uri
argument_list|,
name|params
argument_list|)
return|;
block|}
DECL|method|performRequest ( String method, Object payload, String uri, Map<String, String> params)
specifier|private
name|Response
name|performRequest
parameter_list|(
name|String
name|method
parameter_list|,
name|Object
name|payload
parameter_list|,
name|String
name|uri
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|payloadStr
init|=
name|payload
operator|instanceof
name|String
condition|?
operator|(
name|String
operator|)
name|payload
else|:
name|payload
operator|.
name|toString
argument_list|()
decl_stmt|;
name|HttpEntity
name|entity
init|=
operator|new
name|NStringEntity
argument_list|(
name|payloadStr
argument_list|,
name|ContentType
operator|.
name|APPLICATION_JSON
argument_list|)
decl_stmt|;
return|return
name|client
operator|.
name|get
argument_list|()
operator|.
name|performRequest
argument_list|(
name|method
argument_list|,
name|uri
argument_list|,
name|params
argument_list|,
name|entity
argument_list|)
return|;
block|}
DECL|class|ElasticQuerySource
specifier|protected
class|class
name|ElasticQuerySource
implements|implements
name|DataSource
argument_list|<
name|V
argument_list|>
block|{
DECL|field|opts
specifier|private
specifier|final
name|QueryOptions
name|opts
decl_stmt|;
DECL|field|search
specifier|private
specifier|final
name|String
name|search
decl_stmt|;
DECL|field|index
specifier|private
specifier|final
name|String
name|index
decl_stmt|;
DECL|method|ElasticQuerySource (Predicate<V> p, QueryOptions opts, String index, JsonArray sortArray)
name|ElasticQuerySource
parameter_list|(
name|Predicate
argument_list|<
name|V
argument_list|>
name|p
parameter_list|,
name|QueryOptions
name|opts
parameter_list|,
name|String
name|index
parameter_list|,
name|JsonArray
name|sortArray
parameter_list|)
throws|throws
name|QueryParseException
block|{
name|this
operator|.
name|opts
operator|=
name|opts
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|QueryBuilder
name|qb
init|=
name|queryBuilder
operator|.
name|toQueryBuilder
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|SearchSourceBuilder
name|searchSource
init|=
operator|new
name|SearchSourceBuilder
argument_list|(
name|client
operator|.
name|adapter
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
name|qb
argument_list|)
operator|.
name|from
argument_list|(
name|opts
operator|.
name|start
argument_list|()
argument_list|)
operator|.
name|size
argument_list|(
name|opts
operator|.
name|limit
argument_list|()
argument_list|)
operator|.
name|fields
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|opts
operator|.
name|fields
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|search
operator|=
name|getSearch
argument_list|(
name|searchSource
argument_list|,
name|sortArray
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCardinality ()
specifier|public
name|int
name|getCardinality
parameter_list|()
block|{
return|return
literal|10
return|;
block|}
annotation|@
name|Override
DECL|method|read ()
specifier|public
name|ResultSet
argument_list|<
name|V
argument_list|>
name|read
parameter_list|()
throws|throws
name|OrmException
block|{
return|return
name|readImpl
argument_list|(
parameter_list|(
name|doc
parameter_list|)
lambda|->
name|AbstractElasticIndex
operator|.
name|this
operator|.
name|fromDocument
argument_list|(
name|doc
argument_list|,
name|opts
operator|.
name|fields
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|readRaw ()
specifier|public
name|ResultSet
argument_list|<
name|FieldBundle
argument_list|>
name|readRaw
parameter_list|()
throws|throws
name|OrmException
block|{
return|return
name|readImpl
argument_list|(
name|AbstractElasticIndex
operator|.
name|this
operator|::
name|toFieldBundle
argument_list|)
return|;
block|}
DECL|method|readImpl (Function<JsonObject, T> mapper)
specifier|private
parameter_list|<
name|T
parameter_list|>
name|ResultSet
argument_list|<
name|T
argument_list|>
name|readImpl
parameter_list|(
name|Function
argument_list|<
name|JsonObject
argument_list|,
name|T
argument_list|>
name|mapper
parameter_list|)
throws|throws
name|OrmException
block|{
try|try
block|{
name|List
argument_list|<
name|T
argument_list|>
name|results
init|=
name|Collections
operator|.
name|emptyList
argument_list|()
decl_stmt|;
name|String
name|uri
init|=
name|getURI
argument_list|(
name|index
argument_list|,
name|SEARCH
argument_list|)
decl_stmt|;
name|Response
name|response
init|=
name|performRequest
argument_list|(
name|HttpPost
operator|.
name|METHOD_NAME
argument_list|,
name|search
argument_list|,
name|uri
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
decl_stmt|;
name|StatusLine
name|statusLine
init|=
name|response
operator|.
name|getStatusLine
argument_list|()
decl_stmt|;
if|if
condition|(
name|statusLine
operator|.
name|getStatusCode
argument_list|()
operator|==
name|HttpStatus
operator|.
name|SC_OK
condition|)
block|{
name|String
name|content
init|=
name|getContent
argument_list|(
name|response
argument_list|)
decl_stmt|;
name|JsonObject
name|obj
init|=
operator|new
name|JsonParser
argument_list|()
operator|.
name|parse
argument_list|(
name|content
argument_list|)
operator|.
name|getAsJsonObject
argument_list|()
operator|.
name|getAsJsonObject
argument_list|(
literal|"hits"
argument_list|)
decl_stmt|;
if|if
condition|(
name|obj
operator|.
name|get
argument_list|(
literal|"hits"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|JsonArray
name|json
init|=
name|obj
operator|.
name|getAsJsonArray
argument_list|(
literal|"hits"
argument_list|)
decl_stmt|;
name|results
operator|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|json
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|json
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|T
name|mapperResult
init|=
name|mapper
operator|.
name|apply
argument_list|(
name|json
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getAsJsonObject
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|mapperResult
operator|!=
literal|null
condition|)
block|{
name|results
operator|.
name|add
argument_list|(
name|mapperResult
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
block|{
name|logger
operator|.
name|atSevere
argument_list|()
operator|.
name|log
argument_list|(
name|statusLine
operator|.
name|getReasonPhrase
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|T
argument_list|>
name|r
init|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|results
argument_list|)
decl_stmt|;
return|return
operator|new
name|ResultSet
argument_list|<
name|T
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|T
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|r
operator|.
name|iterator
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|T
argument_list|>
name|toList
parameter_list|()
block|{
return|return
name|r
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
comment|// Do nothing.
block|}
block|}
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|OrmException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

