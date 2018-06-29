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
name|base
operator|.
name|MoreObjects
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
name|Singleton
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
name|Collections
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
name|Set
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
name|TimeUnit
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
DECL|field|DEFAULT_HOST
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_HOST
init|=
literal|"localhost"
decl_stmt|;
DECL|field|DEFAULT_PORT
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_PORT
init|=
literal|"9200"
decl_stmt|;
DECL|field|DEFAULT_PROTOCOL
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_PROTOCOL
init|=
literal|"http"
decl_stmt|;
DECL|field|cfg
specifier|private
specifier|final
name|Config
name|cfg
decl_stmt|;
DECL|field|hosts
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
DECL|field|requestCompression
specifier|final
name|boolean
name|requestCompression
decl_stmt|;
DECL|field|connectionTimeout
specifier|final
name|long
name|connectionTimeout
decl_stmt|;
DECL|field|maxConnectionIdleTime
specifier|final
name|long
name|maxConnectionIdleTime
decl_stmt|;
DECL|field|maxTotalConnection
specifier|final
name|int
name|maxTotalConnection
decl_stmt|;
DECL|field|readTimeout
specifier|final
name|int
name|readTimeout
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
name|username
operator|=
name|cfg
operator|.
name|getString
argument_list|(
literal|"elasticsearch"
argument_list|,
literal|null
argument_list|,
literal|"username"
argument_list|)
expr_stmt|;
name|this
operator|.
name|password
operator|=
name|cfg
operator|.
name|getString
argument_list|(
literal|"elasticsearch"
argument_list|,
literal|null
argument_list|,
literal|"password"
argument_list|)
expr_stmt|;
name|this
operator|.
name|requestCompression
operator|=
name|cfg
operator|.
name|getBoolean
argument_list|(
literal|"elasticsearch"
argument_list|,
literal|null
argument_list|,
literal|"requestCompression"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|connectionTimeout
operator|=
name|cfg
operator|.
name|getTimeUnit
argument_list|(
literal|"elasticsearch"
argument_list|,
literal|null
argument_list|,
literal|"connectionTimeout"
argument_list|,
literal|3000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxConnectionIdleTime
operator|=
name|cfg
operator|.
name|getTimeUnit
argument_list|(
literal|"elasticsearch"
argument_list|,
literal|null
argument_list|,
literal|"maxConnectionIdleTime"
argument_list|,
literal|3000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxTotalConnection
operator|=
name|cfg
operator|.
name|getInt
argument_list|(
literal|"elasticsearch"
argument_list|,
literal|null
argument_list|,
literal|"maxTotalConnection"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|this
operator|.
name|readTimeout
operator|=
operator|(
name|int
operator|)
name|cfg
operator|.
name|getTimeUnit
argument_list|(
literal|"elasticsearch"
argument_list|,
literal|null
argument_list|,
literal|"readTimeout"
argument_list|,
literal|3000
argument_list|,
name|TimeUnit
operator|.
name|MICROSECONDS
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
literal|"elasticsearch"
argument_list|,
literal|null
argument_list|,
literal|"prefix"
argument_list|)
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|subsections
init|=
name|cfg
operator|.
name|getSubsections
argument_list|(
literal|"elasticsearch"
argument_list|)
decl_stmt|;
if|if
condition|(
name|subsections
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|HttpHost
name|httpHost
init|=
operator|new
name|HttpHost
argument_list|(
name|DEFAULT_HOST
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|DEFAULT_PORT
argument_list|)
argument_list|,
name|DEFAULT_PROTOCOL
argument_list|)
decl_stmt|;
name|this
operator|.
name|hosts
operator|=
name|Collections
operator|.
name|singletonList
argument_list|(
name|httpHost
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|hosts
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|subsections
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|subsection
range|:
name|subsections
control|)
block|{
name|String
name|port
init|=
name|getString
argument_list|(
name|cfg
argument_list|,
name|subsection
argument_list|,
literal|"port"
argument_list|,
name|DEFAULT_PORT
argument_list|)
decl_stmt|;
name|String
name|host
init|=
name|getString
argument_list|(
name|cfg
argument_list|,
name|subsection
argument_list|,
literal|"hostname"
argument_list|,
name|DEFAULT_HOST
argument_list|)
decl_stmt|;
name|String
name|protocol
init|=
name|getString
argument_list|(
name|cfg
argument_list|,
name|subsection
argument_list|,
literal|"protocol"
argument_list|,
name|DEFAULT_PROTOCOL
argument_list|)
decl_stmt|;
name|HttpHost
name|httpHost
init|=
operator|new
name|HttpHost
argument_list|(
name|host
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|port
argument_list|)
argument_list|,
name|protocol
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
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Elasticsearch hosts: {}"
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
DECL|method|getString (Config cfg, String subsection, String name, String defaultValue)
specifier|private
name|String
name|getString
parameter_list|(
name|Config
name|cfg
parameter_list|,
name|String
name|subsection
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|defaultValue
parameter_list|)
block|{
return|return
name|MoreObjects
operator|.
name|firstNonNull
argument_list|(
name|cfg
operator|.
name|getString
argument_list|(
literal|"elasticsearch"
argument_list|,
name|subsection
argument_list|,
name|name
argument_list|)
argument_list|,
name|defaultValue
argument_list|)
return|;
block|}
block|}
end_class

end_unit

