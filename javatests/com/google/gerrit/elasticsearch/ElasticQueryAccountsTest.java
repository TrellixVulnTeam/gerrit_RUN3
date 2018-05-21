begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2016 The Android Open Source Project
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
name|ElasticTestUtils
operator|.
name|ElasticNodeInfo
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
name|query
operator|.
name|account
operator|.
name|AbstractQueryAccountsTest
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
name|testing
operator|.
name|ConfigSuite
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
name|testing
operator|.
name|InMemoryModule
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
name|testing
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
name|inject
operator|.
name|Guice
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
name|Injector
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
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AssumptionViolatedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_class
DECL|class|ElasticQueryAccountsTest
specifier|public
class|class
name|ElasticQueryAccountsTest
extends|extends
name|AbstractQueryAccountsTest
block|{
annotation|@
name|ConfigSuite
operator|.
name|Default
DECL|method|defaultConfig ()
specifier|public
specifier|static
name|Config
name|defaultConfig
parameter_list|()
block|{
return|return
name|IndexConfig
operator|.
name|createForElasticsearch
argument_list|()
return|;
block|}
DECL|field|nodeInfo
specifier|private
specifier|static
name|ElasticNodeInfo
name|nodeInfo
decl_stmt|;
DECL|field|container
specifier|private
specifier|static
name|ElasticContainer
argument_list|<
name|?
argument_list|>
name|container
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|startIndexService ()
specifier|public
specifier|static
name|void
name|startIndexService
parameter_list|()
block|{
if|if
condition|(
name|nodeInfo
operator|!=
literal|null
condition|)
block|{
comment|// do not start Elasticsearch twice
return|return;
block|}
comment|// Assumption violation is not natively supported by Testcontainers.
comment|// See https://github.com/testcontainers/testcontainers-java/issues/343
try|try
block|{
name|container
operator|=
operator|new
name|ElasticContainer
argument_list|<>
argument_list|()
expr_stmt|;
name|container
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
throw|throw
operator|new
name|AssumptionViolatedException
argument_list|(
literal|"Unable to start container[might be docker related]"
argument_list|)
throw|;
block|}
name|nodeInfo
operator|=
operator|new
name|ElasticNodeInfo
argument_list|(
name|container
operator|.
name|getHttpHost
argument_list|()
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|stopElasticsearchServer ()
specifier|public
specifier|static
name|void
name|stopElasticsearchServer
parameter_list|()
block|{
if|if
condition|(
name|container
operator|!=
literal|null
condition|)
block|{
name|container
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testName ()
specifier|private
name|String
name|testName
parameter_list|()
block|{
return|return
name|testName
operator|.
name|getMethodName
argument_list|()
operator|.
name|toLowerCase
argument_list|()
operator|+
literal|"_"
return|;
block|}
annotation|@
name|Override
DECL|method|initAfterLifecycleStart ()
specifier|protected
name|void
name|initAfterLifecycleStart
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|initAfterLifecycleStart
argument_list|()
expr_stmt|;
name|ElasticTestUtils
operator|.
name|createAllIndexes
argument_list|(
name|injector
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createInjector ()
specifier|protected
name|Injector
name|createInjector
parameter_list|()
block|{
name|Config
name|elasticsearchConfig
init|=
operator|new
name|Config
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|InMemoryModule
operator|.
name|setDefaults
argument_list|(
name|elasticsearchConfig
argument_list|)
expr_stmt|;
name|String
name|indicesPrefix
init|=
name|testName
argument_list|()
decl_stmt|;
name|ElasticTestUtils
operator|.
name|configure
argument_list|(
name|elasticsearchConfig
argument_list|,
name|nodeInfo
operator|.
name|port
argument_list|,
name|indicesPrefix
argument_list|)
expr_stmt|;
return|return
name|Guice
operator|.
name|createInjector
argument_list|(
operator|new
name|InMemoryModule
argument_list|(
name|elasticsearchConfig
argument_list|,
name|notesMigration
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

