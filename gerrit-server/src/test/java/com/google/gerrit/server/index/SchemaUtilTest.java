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
DECL|package|com.google.gerrit.server.index
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
name|truth
operator|.
name|Truth
operator|.
name|assertThat
import|;
end_import

begin_import
import|import static
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
name|SchemaUtil
operator|.
name|schema
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
name|GerritBaseTests
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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

begin_class
DECL|class|SchemaUtilTest
specifier|public
class|class
name|SchemaUtilTest
extends|extends
name|GerritBaseTests
block|{
DECL|class|TestSchemas
specifier|static
class|class
name|TestSchemas
block|{
DECL|field|V1
specifier|static
specifier|final
name|Schema
argument_list|<
name|String
argument_list|>
name|V1
init|=
name|schema
argument_list|()
decl_stmt|;
DECL|field|V2
specifier|static
specifier|final
name|Schema
argument_list|<
name|String
argument_list|>
name|V2
init|=
name|schema
argument_list|()
decl_stmt|;
DECL|field|V3
specifier|static
name|Schema
argument_list|<
name|String
argument_list|>
name|V3
init|=
name|schema
argument_list|()
decl_stmt|;
comment|// Not final, ignored.
DECL|field|V4
specifier|private
specifier|static
specifier|final
name|Schema
argument_list|<
name|String
argument_list|>
name|V4
init|=
name|schema
argument_list|()
decl_stmt|;
comment|// Ignored.
DECL|field|V10
specifier|static
name|Schema
argument_list|<
name|String
argument_list|>
name|V10
init|=
name|schema
argument_list|()
decl_stmt|;
DECL|field|V11
specifier|final
name|Schema
argument_list|<
name|String
argument_list|>
name|V11
init|=
name|schema
argument_list|()
decl_stmt|;
block|}
annotation|@
name|Test
DECL|method|schemasFromClassBuildsMap ()
specifier|public
name|void
name|schemasFromClassBuildsMap
parameter_list|()
block|{
name|Map
argument_list|<
name|Integer
argument_list|,
name|Schema
argument_list|<
name|String
argument_list|>
argument_list|>
name|all
init|=
name|SchemaUtil
operator|.
name|schemasFromClass
argument_list|(
name|TestSchemas
operator|.
name|class
argument_list|,
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|all
operator|.
name|keySet
argument_list|()
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|all
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|TestSchemas
operator|.
name|V1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|all
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|TestSchemas
operator|.
name|V2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|all
operator|.
name|get
argument_list|(
literal|4
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|TestSchemas
operator|.
name|V4
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|)
expr_stmt|;
name|SchemaUtil
operator|.
name|schemasFromClass
argument_list|(
name|TestSchemas
operator|.
name|class
argument_list|,
name|Object
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

