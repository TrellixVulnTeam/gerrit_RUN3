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
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
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
name|extensions
operator|.
name|registration
operator|.
name|DynamicItem
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
name|lifecycle
operator|.
name|LifecycleModule
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
name|change
operator|.
name|AccountPatchReviewStore
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
DECL|class|H2AccountPatchReviewStore
specifier|public
class|class
name|H2AccountPatchReviewStore
extends|extends
name|JdbcAccountPatchReviewStore
block|{
annotation|@
name|VisibleForTesting
DECL|class|InMemoryModule
specifier|public
specifier|static
class|class
name|InMemoryModule
extends|extends
name|LifecycleModule
block|{
annotation|@
name|Override
DECL|method|configure ()
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|H2AccountPatchReviewStore
name|inMemoryStore
init|=
operator|new
name|H2AccountPatchReviewStore
argument_list|()
decl_stmt|;
name|DynamicItem
operator|.
name|bind
argument_list|(
name|binder
argument_list|()
argument_list|,
name|AccountPatchReviewStore
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|inMemoryStore
argument_list|)
expr_stmt|;
name|listener
argument_list|()
operator|.
name|toInstance
argument_list|(
name|inMemoryStore
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Inject
DECL|method|H2AccountPatchReviewStore (@erritServerConfig Config cfg, SitePaths sitePaths)
name|H2AccountPatchReviewStore
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
comment|/**    * Creates an in-memory H2 database to store the reviewed flags. This should be used for tests    * only.    */
annotation|@
name|VisibleForTesting
DECL|method|H2AccountPatchReviewStore ()
specifier|private
name|H2AccountPatchReviewStore
parameter_list|()
block|{
comment|// DB_CLOSE_DELAY=-1: By default the content of an in-memory H2 database is
comment|// lost at the moment the last connection is closed. This option keeps the
comment|// content as long as the vm lives.
name|super
argument_list|(
name|createDataSource
argument_list|(
literal|"jdbc:h2:mem:account_patch_reviews;DB_CLOSE_DELAY=-1"
argument_list|)
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
literal|23001
case|:
comment|// UNIQUE CONSTRAINT VIOLATION
case|case
literal|23505
case|:
comment|// DUPLICATE_KEY_1
return|return
operator|new
name|OrmDuplicateKeyException
argument_list|(
literal|"account_patch_reviews"
argument_list|,
name|err
argument_list|)
return|;
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
literal|" failure on account_patch_reviews"
argument_list|,
name|err
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

