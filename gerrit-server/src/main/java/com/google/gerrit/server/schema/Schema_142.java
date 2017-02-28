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
name|client
operator|.
name|AccountExternalId
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
name|gerrit
operator|.
name|server
operator|.
name|account
operator|.
name|HashedPassword
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_class
DECL|class|Schema_142
specifier|public
class|class
name|Schema_142
extends|extends
name|SchemaVersion
block|{
annotation|@
name|Inject
DECL|method|Schema_142 (Provider<Schema_141> prior)
name|Schema_142
parameter_list|(
name|Provider
argument_list|<
name|Schema_141
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
name|List
argument_list|<
name|AccountExternalId
argument_list|>
name|newIds
init|=
name|db
operator|.
name|accountExternalIds
argument_list|()
operator|.
name|all
argument_list|()
operator|.
name|toList
argument_list|()
decl_stmt|;
for|for
control|(
name|AccountExternalId
name|id
range|:
name|newIds
control|)
block|{
if|if
condition|(
operator|!
name|id
operator|.
name|isScheme
argument_list|(
name|AccountExternalId
operator|.
name|SCHEME_USERNAME
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|String
name|password
init|=
name|id
operator|.
name|getPassword
argument_list|()
decl_stmt|;
if|if
condition|(
name|password
operator|!=
literal|null
condition|)
block|{
name|HashedPassword
name|hashed
init|=
name|HashedPassword
operator|.
name|fromPassword
argument_list|(
name|password
argument_list|)
decl_stmt|;
name|id
operator|.
name|setPassword
argument_list|(
name|hashed
operator|.
name|encode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|db
operator|.
name|accountExternalIds
argument_list|()
operator|.
name|upsert
argument_list|(
name|newIds
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

