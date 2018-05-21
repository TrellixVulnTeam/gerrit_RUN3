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
DECL|package|com.google.gerrit.elasticsearch.testing
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|elasticsearch
operator|.
name|testing
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
name|server
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
name|server
operator|.
name|index
operator|.
name|IndexModule
operator|.
name|IndexType
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Key
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
name|TypeLiteral
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
name|Collection
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
DECL|class|ElasticTestUtils
specifier|public
specifier|final
class|class
name|ElasticTestUtils
block|{
DECL|class|ElasticNodeInfo
specifier|public
specifier|static
class|class
name|ElasticNodeInfo
block|{
DECL|field|port
specifier|public
specifier|final
name|int
name|port
decl_stmt|;
DECL|method|ElasticNodeInfo (int port)
specifier|public
name|ElasticNodeInfo
parameter_list|(
name|int
name|port
parameter_list|)
block|{
name|this
operator|.
name|port
operator|=
name|port
expr_stmt|;
block|}
block|}
DECL|method|configure (Config config, int port, String prefix)
specifier|public
specifier|static
name|void
name|configure
parameter_list|(
name|Config
name|config
parameter_list|,
name|int
name|port
parameter_list|,
name|String
name|prefix
parameter_list|)
block|{
name|config
operator|.
name|setEnum
argument_list|(
literal|"index"
argument_list|,
literal|null
argument_list|,
literal|"type"
argument_list|,
name|IndexType
operator|.
name|ELASTICSEARCH
argument_list|)
expr_stmt|;
name|config
operator|.
name|setString
argument_list|(
literal|"elasticsearch"
argument_list|,
literal|"test"
argument_list|,
literal|"protocol"
argument_list|,
literal|"http"
argument_list|)
expr_stmt|;
name|config
operator|.
name|setString
argument_list|(
literal|"elasticsearch"
argument_list|,
literal|"test"
argument_list|,
literal|"hostname"
argument_list|,
literal|"localhost"
argument_list|)
expr_stmt|;
name|config
operator|.
name|setInt
argument_list|(
literal|"elasticsearch"
argument_list|,
literal|"test"
argument_list|,
literal|"port"
argument_list|,
name|port
argument_list|)
expr_stmt|;
name|config
operator|.
name|setString
argument_list|(
literal|"elasticsearch"
argument_list|,
literal|null
argument_list|,
literal|"prefix"
argument_list|,
name|prefix
argument_list|)
expr_stmt|;
name|config
operator|.
name|setString
argument_list|(
literal|"index"
argument_list|,
literal|null
argument_list|,
literal|"maxLimit"
argument_list|,
literal|"10000"
argument_list|)
expr_stmt|;
block|}
DECL|method|createAllIndexes (Injector injector)
specifier|public
specifier|static
name|void
name|createAllIndexes
parameter_list|(
name|Injector
name|injector
parameter_list|)
throws|throws
name|IOException
block|{
name|Collection
argument_list|<
name|IndexDefinition
argument_list|<
name|?
argument_list|,
name|?
argument_list|,
name|?
argument_list|>
argument_list|>
name|indexDefs
init|=
name|injector
operator|.
name|getInstance
argument_list|(
name|Key
operator|.
name|get
argument_list|(
operator|new
name|TypeLiteral
argument_list|<
name|Collection
argument_list|<
name|IndexDefinition
argument_list|<
name|?
argument_list|,
name|?
argument_list|,
name|?
argument_list|>
argument_list|>
argument_list|>
argument_list|()
block|{}
block|)
block|)
class|;
end_class

begin_for
for|for
control|(
name|IndexDefinition
argument_list|<
name|?
argument_list|,
name|?
argument_list|,
name|?
argument_list|>
name|indexDef
range|:
name|indexDefs
control|)
block|{
name|indexDef
operator|.
name|getIndexCollection
argument_list|()
operator|.
name|getSearchIndex
argument_list|()
operator|.
name|deleteAll
argument_list|()
expr_stmt|;
block|}
end_for

begin_expr_stmt
unit|}    private
DECL|method|ElasticTestUtils ()
name|ElasticTestUtils
argument_list|()
block|{
comment|// hide default constructor
block|}
end_expr_stmt

unit|}
end_unit

