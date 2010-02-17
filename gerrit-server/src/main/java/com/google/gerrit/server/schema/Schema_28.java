begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2010 The Android Open Source Project
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
name|ApprovalCategory
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
name|ApprovalCategoryValue
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
name|RefRight
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
name|reviewdb
operator|.
name|SystemConfig
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
name|workflow
operator|.
name|NoOpFunction
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
name|client
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
name|sql
operator|.
name|Statement
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

begin_class
DECL|class|Schema_28
class|class
name|Schema_28
extends|extends
name|SchemaVersion
block|{
annotation|@
name|Inject
DECL|method|Schema_28 (Provider<Schema_27> prior)
name|Schema_28
parameter_list|(
name|Provider
argument_list|<
name|Schema_27
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
DECL|method|migrateData (ReviewDb db)
specifier|protected
name|void
name|migrateData
parameter_list|(
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
throws|,
name|SQLException
block|{
specifier|final
name|SystemConfig
name|cfg
init|=
name|db
operator|.
name|systemConfig
argument_list|()
operator|.
name|get
argument_list|(
operator|new
name|SystemConfig
operator|.
name|Key
argument_list|()
argument_list|)
decl_stmt|;
name|ApprovalCategory
name|cat
decl_stmt|;
name|initForgeIdentityCategory
argument_list|(
name|db
argument_list|,
name|cfg
argument_list|)
expr_stmt|;
comment|// Don't grant FORGE_COMMITTER to existing PUSH_HEAD rights. That
comment|// is considered a bug that we are fixing with this schema upgrade.
comment|// Administrators might need to relax permissions manually after the
comment|// upgrade if that forgery is critical to their workflow.
name|cat
operator|=
name|db
operator|.
name|approvalCategories
argument_list|()
operator|.
name|get
argument_list|(
name|ApprovalCategory
operator|.
name|PUSH_TAG
argument_list|)
expr_stmt|;
if|if
condition|(
name|cat
operator|!=
literal|null
operator|&&
literal|"Push Annotated Tag"
operator|.
name|equals
argument_list|(
name|cat
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|cat
operator|.
name|setName
argument_list|(
literal|"Push Tag"
argument_list|)
expr_stmt|;
name|db
operator|.
name|approvalCategories
argument_list|()
operator|.
name|update
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|cat
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Since we deleted Push Tags +3, drop anything using +3 down to +2.
comment|//
name|Statement
name|stmt
init|=
operator|(
operator|(
name|JdbcSchema
operator|)
name|db
operator|)
operator|.
name|getConnection
argument_list|()
operator|.
name|createStatement
argument_list|()
decl_stmt|;
try|try
block|{
name|stmt
operator|.
name|execute
argument_list|(
literal|"UPDATE ref_rights SET max_value = "
operator|+
name|ApprovalCategory
operator|.
name|PUSH_TAG_ANNOTATED
operator|+
literal|" WHERE max_value>= 3"
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|execute
argument_list|(
literal|"UPDATE ref_rights SET min_value = "
operator|+
name|ApprovalCategory
operator|.
name|PUSH_TAG_ANNOTATED
operator|+
literal|" WHERE min_value>= 3"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|initForgeIdentityCategory (final ReviewDb c, final SystemConfig sConfig)
specifier|private
name|void
name|initForgeIdentityCategory
parameter_list|(
specifier|final
name|ReviewDb
name|c
parameter_list|,
specifier|final
name|SystemConfig
name|sConfig
parameter_list|)
throws|throws
name|OrmException
block|{
specifier|final
name|ApprovalCategory
name|cat
decl_stmt|;
specifier|final
name|ArrayList
argument_list|<
name|ApprovalCategoryValue
argument_list|>
name|values
decl_stmt|;
name|cat
operator|=
operator|new
name|ApprovalCategory
argument_list|(
name|ApprovalCategory
operator|.
name|FORGE_IDENTITY
argument_list|,
literal|"Forge Identity"
argument_list|)
expr_stmt|;
name|cat
operator|.
name|setPosition
argument_list|(
operator|(
name|short
operator|)
operator|-
literal|1
argument_list|)
expr_stmt|;
name|cat
operator|.
name|setFunctionName
argument_list|(
name|NoOpFunction
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|values
operator|=
operator|new
name|ArrayList
argument_list|<
name|ApprovalCategoryValue
argument_list|>
argument_list|()
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
name|value
argument_list|(
name|cat
argument_list|,
name|ApprovalCategory
operator|.
name|FORGE_AUTHOR
argument_list|,
literal|"Forge Author Identity"
argument_list|)
argument_list|)
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
name|value
argument_list|(
name|cat
argument_list|,
name|ApprovalCategory
operator|.
name|FORGE_COMMITTER
argument_list|,
literal|"Forge Committer or Tagger Identity"
argument_list|)
argument_list|)
expr_stmt|;
name|c
operator|.
name|approvalCategories
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|cat
argument_list|)
argument_list|)
expr_stmt|;
name|c
operator|.
name|approvalCategoryValues
argument_list|()
operator|.
name|insert
argument_list|(
name|values
argument_list|)
expr_stmt|;
name|RefRight
name|right
init|=
operator|new
name|RefRight
argument_list|(
operator|new
name|RefRight
operator|.
name|Key
argument_list|(
name|sConfig
operator|.
name|wildProjectName
argument_list|,
operator|new
name|RefRight
operator|.
name|RefPattern
argument_list|(
literal|"refs/*"
argument_list|)
argument_list|,
name|ApprovalCategory
operator|.
name|FORGE_IDENTITY
argument_list|,
name|sConfig
operator|.
name|registeredGroupId
argument_list|)
argument_list|)
decl_stmt|;
name|right
operator|.
name|setMinValue
argument_list|(
name|ApprovalCategory
operator|.
name|FORGE_AUTHOR
argument_list|)
expr_stmt|;
name|right
operator|.
name|setMaxValue
argument_list|(
name|ApprovalCategory
operator|.
name|FORGE_AUTHOR
argument_list|)
expr_stmt|;
name|c
operator|.
name|refRights
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|right
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|value (final ApprovalCategory cat, final int value, final String name)
specifier|private
specifier|static
name|ApprovalCategoryValue
name|value
parameter_list|(
specifier|final
name|ApprovalCategory
name|cat
parameter_list|,
specifier|final
name|int
name|value
parameter_list|,
specifier|final
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|ApprovalCategoryValue
argument_list|(
operator|new
name|ApprovalCategoryValue
operator|.
name|Id
argument_list|(
name|cat
operator|.
name|getId
argument_list|()
argument_list|,
operator|(
name|short
operator|)
name|value
argument_list|)
argument_list|,
name|name
argument_list|)
return|;
block|}
block|}
end_class

end_unit

