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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|MoreObjects
operator|.
name|firstNonNull
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
name|ProvisionException
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
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|apache
operator|.
name|http
operator|.
name|HttpHost
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

begin_class
annotation|@
name|Singleton
DECL|class|ElasticConfiguration
class|class
name|ElasticConfiguration
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
name|ElasticConfiguration
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SECTION_ELASTICSEARCH
specifier|static
specifier|final
name|String
name|SECTION_ELASTICSEARCH
init|=
literal|"elasticsearch"
decl_stmt|;
DECL|field|KEY_PASSWORD
specifier|static
specifier|final
name|String
name|KEY_PASSWORD
init|=
literal|"password"
decl_stmt|;
DECL|field|KEY_USERNAME
specifier|static
specifier|final
name|String
name|KEY_USERNAME
init|=
literal|"username"
decl_stmt|;
DECL|field|KEY_PREFIX
specifier|static
specifier|final
name|String
name|KEY_PREFIX
init|=
literal|"prefix"
decl_stmt|;
DECL|field|KEY_SERVER
specifier|static
specifier|final
name|String
name|KEY_SERVER
init|=
literal|"server"
decl_stmt|;
DECL|field|KEY_NUMBER_OF_SHARDS
specifier|static
specifier|final
name|String
name|KEY_NUMBER_OF_SHARDS
init|=
literal|"numberOfShards"
decl_stmt|;
DECL|field|KEY_NUMBER_OF_REPLICAS
specifier|static
specifier|final
name|String
name|KEY_NUMBER_OF_REPLICAS
init|=
literal|"numberOfReplicas"
decl_stmt|;
DECL|field|DEFAULT_PORT
specifier|static
specifier|final
name|String
name|DEFAULT_PORT
init|=
literal|"9200"
decl_stmt|;
DECL|field|DEFAULT_USERNAME
specifier|static
specifier|final
name|String
name|DEFAULT_USERNAME
init|=
literal|"elastic"
decl_stmt|;
DECL|field|DEFAULT_NUMBER_OF_SHARDS
specifier|static
specifier|final
name|int
name|DEFAULT_NUMBER_OF_SHARDS
init|=
literal|0
decl_stmt|;
DECL|field|DEFAULT_NUMBER_OF_REPLICAS
specifier|static
specifier|final
name|int
name|DEFAULT_NUMBER_OF_REPLICAS
init|=
literal|1
decl_stmt|;
DECL|field|cfg
specifier|private
specifier|final
name|Config
name|cfg
decl_stmt|;
DECL|field|hosts
specifier|private
specifier|final
name|List
argument_list|<
name|HttpHost
argument_list|>
name|hosts
decl_stmt|;
DECL|field|username
specifier|final
name|String
name|username
decl_stmt|;
DECL|field|password
specifier|final
name|String
name|password
decl_stmt|;
DECL|field|numberOfShards
specifier|final
name|int
name|numberOfShards
decl_stmt|;
DECL|field|numberOfReplicas
specifier|final
name|int
name|numberOfReplicas
decl_stmt|;
DECL|field|prefix
specifier|final
name|String
name|prefix
decl_stmt|;
annotation|@
name|Inject
DECL|method|ElasticConfiguration (@erritServerConfig Config cfg)
name|ElasticConfiguration
parameter_list|(
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|)
block|{
name|this
operator|.
name|cfg
operator|=
name|cfg
expr_stmt|;
name|this
operator|.
name|password
operator|=
name|cfg
operator|.
name|getString
argument_list|(
name|SECTION_ELASTICSEARCH
argument_list|,
literal|null
argument_list|,
name|KEY_PASSWORD
argument_list|)
expr_stmt|;
name|this
operator|.
name|username
operator|=
name|password
operator|==
literal|null
condition|?
literal|null
else|:
name|firstNonNull
argument_list|(
name|cfg
operator|.
name|getString
argument_list|(
name|SECTION_ELASTICSEARCH
argument_list|,
literal|null
argument_list|,
name|KEY_USERNAME
argument_list|)
argument_list|,
name|DEFAULT_USERNAME
argument_list|)
expr_stmt|;
name|this
operator|.
name|prefix
operator|=
name|Strings
operator|.
name|nullToEmpty
argument_list|(
name|cfg
operator|.
name|getString
argument_list|(
name|SECTION_ELASTICSEARCH
argument_list|,
literal|null
argument_list|,
name|KEY_PREFIX
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|numberOfShards
operator|=
name|cfg
operator|.
name|getInt
argument_list|(
name|SECTION_ELASTICSEARCH
argument_list|,
literal|null
argument_list|,
name|KEY_NUMBER_OF_SHARDS
argument_list|,
name|DEFAULT_NUMBER_OF_SHARDS
argument_list|)
expr_stmt|;
name|this
operator|.
name|numberOfReplicas
operator|=
name|cfg
operator|.
name|getInt
argument_list|(
name|SECTION_ELASTICSEARCH
argument_list|,
literal|null
argument_list|,
name|KEY_NUMBER_OF_REPLICAS
argument_list|,
name|DEFAULT_NUMBER_OF_REPLICAS
argument_list|)
expr_stmt|;
name|this
operator|.
name|hosts
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|server
range|:
name|cfg
operator|.
name|getStringList
argument_list|(
name|SECTION_ELASTICSEARCH
argument_list|,
literal|null
argument_list|,
name|KEY_SERVER
argument_list|)
control|)
block|{
try|try
block|{
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
name|server
argument_list|)
decl_stmt|;
name|int
name|port
init|=
name|uri
operator|.
name|getPort
argument_list|()
decl_stmt|;
name|HttpHost
name|httpHost
init|=
operator|new
name|HttpHost
argument_list|(
name|uri
operator|.
name|getHost
argument_list|()
argument_list|,
name|port
operator|==
operator|-
literal|1
condition|?
name|Integer
operator|.
name|valueOf
argument_list|(
name|DEFAULT_PORT
argument_list|)
else|:
name|port
argument_list|,
name|uri
operator|.
name|getScheme
argument_list|()
argument_list|)
decl_stmt|;
name|this
operator|.
name|hosts
operator|.
name|add
argument_list|(
name|httpHost
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
decl||
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Invalid server URI {}: {}"
argument_list|,
name|server
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|hosts
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ProvisionException
argument_list|(
literal|"No valid Elasticsearch servers configured"
argument_list|)
throw|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Elasticsearch servers: {}"
argument_list|,
name|hosts
argument_list|)
expr_stmt|;
block|}
DECL|method|getConfig ()
name|Config
name|getConfig
parameter_list|()
block|{
return|return
name|cfg
return|;
block|}
DECL|method|getHosts ()
name|HttpHost
index|[]
name|getHosts
parameter_list|()
block|{
return|return
name|hosts
operator|.
name|toArray
argument_list|(
operator|new
name|HttpHost
index|[
name|hosts
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
DECL|method|getIndexName (String name, int schemaVersion)
name|String
name|getIndexName
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|schemaVersion
parameter_list|)
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"%s%s_%04d"
argument_list|,
name|prefix
argument_list|,
name|name
argument_list|,
name|schemaVersion
argument_list|)
return|;
block|}
DECL|method|getNumberOfShards (ElasticQueryAdapter adapter)
name|int
name|getNumberOfShards
parameter_list|(
name|ElasticQueryAdapter
name|adapter
parameter_list|)
block|{
if|if
condition|(
name|numberOfShards
operator|==
name|DEFAULT_NUMBER_OF_SHARDS
condition|)
block|{
return|return
name|adapter
operator|.
name|getDefaultNumberOfShards
argument_list|()
return|;
block|}
return|return
name|numberOfShards
return|;
block|}
block|}
end_class

end_unit

