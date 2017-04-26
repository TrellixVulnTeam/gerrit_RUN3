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
name|gerrit
operator|.
name|server
operator|.
name|config
operator|.
name|SitePaths
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
name|OrmDuplicateKeyException
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
name|Singleton
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
annotation|@
name|Singleton
DECL|class|PostgresqlAccountPatchReviewStore
specifier|public
class|class
name|PostgresqlAccountPatchReviewStore
extends|extends
name|JdbcAccountPatchReviewStore
block|{
annotation|@
name|Inject
DECL|method|PostgresqlAccountPatchReviewStore (@erritServerConfig Config cfg, SitePaths sitePaths)
name|PostgresqlAccountPatchReviewStore
parameter_list|(
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|,
name|SitePaths
name|sitePaths
parameter_list|)
block|{
name|super
argument_list|(
name|cfg
argument_list|,
name|sitePaths
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|convertError (String op, SQLException err)
specifier|public
name|OrmException
name|convertError
parameter_list|(
name|String
name|op
parameter_list|,
name|SQLException
name|err
parameter_list|)
block|{
switch|switch
condition|(
name|getSQLStateInt
argument_list|(
name|err
argument_list|)
condition|)
block|{
case|case
literal|23505
case|:
comment|// DUPLICATE_KEY_1
return|return
operator|new
name|OrmDuplicateKeyException
argument_list|(
literal|"ACCOUNT_PATCH_REVIEWS"
argument_list|,
name|err
argument_list|)
return|;
case|case
literal|23514
case|:
comment|// CHECK CONSTRAINT VIOLATION
case|case
literal|23503
case|:
comment|// FOREIGN KEY CONSTRAINT VIOLATION
case|case
literal|23502
case|:
comment|// NOT NULL CONSTRAINT VIOLATION
case|case
literal|23001
case|:
comment|// RESTRICT VIOLATION
default|default:
if|if
condition|(
name|err
operator|.
name|getCause
argument_list|()
operator|==
literal|null
operator|&&
name|err
operator|.
name|getNextException
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|err
operator|.
name|initCause
argument_list|(
name|err
operator|.
name|getNextException
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|OrmException
argument_list|(
name|op
operator|+
literal|" failure on ACCOUNT_PATCH_REVIEWS"
argument_list|,
name|err
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

