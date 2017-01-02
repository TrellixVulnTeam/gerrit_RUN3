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
DECL|package|com.google.gerrit.server.query.group
package|package
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
name|server
operator|.
name|query
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
name|inject
operator|.
name|Inject
import|;
end_import

begin_comment
comment|/**  * Parses a query string meant to be applied to group objects.  */
end_comment

begin_class
DECL|class|GroupQueryBuilder
specifier|public
class|class
name|GroupQueryBuilder
extends|extends
name|QueryBuilder
argument_list|<
name|AccountGroup
argument_list|>
block|{
DECL|field|FIELD_UUID
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_UUID
init|=
literal|"uuid"
decl_stmt|;
DECL|field|mydef
specifier|private
specifier|static
specifier|final
name|QueryBuilder
operator|.
name|Definition
argument_list|<
name|AccountGroup
argument_list|,
name|GroupQueryBuilder
argument_list|>
name|mydef
init|=
operator|new
name|QueryBuilder
operator|.
name|Definition
argument_list|<>
argument_list|(
name|GroupQueryBuilder
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Inject
DECL|method|GroupQueryBuilder ()
name|GroupQueryBuilder
parameter_list|()
block|{
name|super
argument_list|(
name|mydef
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Operator
DECL|method|uuid (String uuid)
specifier|public
name|Predicate
argument_list|<
name|AccountGroup
argument_list|>
name|uuid
parameter_list|(
name|String
name|uuid
parameter_list|)
block|{
return|return
name|GroupPredicates
operator|.
name|uuid
argument_list|(
operator|new
name|AccountGroup
operator|.
name|UUID
argument_list|(
name|uuid
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

