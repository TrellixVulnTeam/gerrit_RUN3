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
DECL|package|com.google.gerrit.server.schema
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|schema
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
name|server
operator|.
name|ReviewDb
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
name|jdbc
operator|.
name|JdbcSchema
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
name|schema
operator|.
name|sql
operator|.
name|SqlDialect
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
name|StatementExecutor
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
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_comment
comment|/** Create account_external_ids_byEmail index. */
end_comment

begin_class
DECL|class|Schema_145
specifier|public
class|class
name|Schema_145
extends|extends
name|SchemaVersion
block|{
annotation|@
name|Inject
DECL|method|Schema_145 (Provider<Schema_144> prior)
name|Schema_145
parameter_list|(
name|Provider
argument_list|<
name|Schema_144
argument_list|>
name|prior
parameter_list|)
block|{
name|super
argument_list|(
name|prior
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|migrateData (ReviewDb db, UpdateUI ui)
specifier|protected
name|void
name|migrateData
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|UpdateUI
name|ui
parameter_list|)
throws|throws
name|OrmException
throws|,
name|SQLException
block|{
name|JdbcSchema
name|schema
init|=
operator|(
name|JdbcSchema
operator|)
name|db
decl_stmt|;
name|SqlDialect
name|dialect
init|=
name|schema
operator|.
name|getDialect
argument_list|()
decl_stmt|;
try|try
init|(
name|StatementExecutor
name|e
init|=
name|newExecutor
argument_list|(
name|db
argument_list|)
init|)
block|{
name|dialect
operator|.
name|dropIndex
argument_list|(
name|e
argument_list|,
literal|"account_external_ids"
argument_list|,
literal|"account_external_ids_byEmail"
argument_list|)
expr_stmt|;
name|e
operator|.
name|execute
argument_list|(
literal|"CREATE INDEX account_external_ids_byEmail"
operator|+
literal|" ON account_external_ids"
operator|+
literal|" (email_address)"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

