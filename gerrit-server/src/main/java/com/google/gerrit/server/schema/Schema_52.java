begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2011 The Android Open Source Project
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
name|CurrentSchemaVersion
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|ProvisionException
import|;
end_import

begin_class
DECL|class|Schema_52
specifier|public
class|class
name|Schema_52
extends|extends
name|SchemaVersion
block|{
annotation|@
name|Inject
DECL|method|Schema_52 ()
name|Schema_52
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|Provider
argument_list|<
name|SchemaVersion
argument_list|>
argument_list|()
block|{
specifier|public
name|SchemaVersion
name|get
parameter_list|()
block|{
throw|throw
operator|new
name|ProvisionException
argument_list|(
literal|"Cannot upgrade from 51"
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|upgradeFrom (UpdateUI ui, CurrentSchemaVersion curr, ReviewDb db, boolean toTargetVersion)
specifier|protected
name|void
name|upgradeFrom
parameter_list|(
name|UpdateUI
name|ui
parameter_list|,
name|CurrentSchemaVersion
name|curr
parameter_list|,
name|ReviewDb
name|db
parameter_list|,
name|boolean
name|toTargetVersion
parameter_list|)
throws|throws
name|OrmException
block|{
throw|throw
operator|new
name|OrmException
argument_list|(
literal|"Cannot upgrade from schema "
operator|+
name|curr
operator|.
name|versionNbr
operator|+
literal|"; manually run init from Gerrit Code Review 2.1.7"
operator|+
literal|" and restart this version to continue."
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

