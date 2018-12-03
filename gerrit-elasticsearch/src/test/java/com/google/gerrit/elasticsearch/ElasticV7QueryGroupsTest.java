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
name|group
operator|.
name|AbstractQueryGroupsTest
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
name|testutil
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
name|BeforeClass
import|;
end_import

begin_class
DECL|class|ElasticV7QueryGroupsTest
specifier|public
class|class
name|ElasticV7QueryGroupsTest
extends|extends
name|AbstractQueryGroupsTest
block|{
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
name|container
operator|=
name|ElasticContainer
operator|.
name|createAndStart
argument_list|(
name|ElasticVersion
operator|.
name|V7_0
argument_list|)
expr_stmt|;
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
name|getSanitizedMethodName
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

