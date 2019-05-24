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
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|index
operator|.
name|IndexConfig
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
name|index
operator|.
name|project
operator|.
name|ProjectIndex
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
name|index
operator|.
name|account
operator|.
name|AccountIndex
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
name|change
operator|.
name|ChangeIndex
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
name|group
operator|.
name|GroupIndex
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
name|AbstractModule
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
name|Provides
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|assistedinject
operator|.
name|FactoryModuleBuilder
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
DECL|class|AbstractIndexModule
specifier|public
specifier|abstract
class|class
name|AbstractIndexModule
extends|extends
name|AbstractModule
block|{
DECL|field|threads
specifier|private
specifier|final
name|int
name|threads
decl_stmt|;
DECL|field|singleVersions
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|singleVersions
decl_stmt|;
DECL|field|slave
specifier|private
specifier|final
name|boolean
name|slave
decl_stmt|;
DECL|method|AbstractIndexModule (Map<String, Integer> singleVersions, int threads, boolean slave)
specifier|protected
name|AbstractIndexModule
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|singleVersions
parameter_list|,
name|int
name|threads
parameter_list|,
name|boolean
name|slave
parameter_list|)
block|{
name|this
operator|.
name|singleVersions
operator|=
name|singleVersions
expr_stmt|;
name|this
operator|.
name|threads
operator|=
name|threads
expr_stmt|;
name|this
operator|.
name|slave
operator|=
name|slave
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|configure ()
specifier|protected
name|void
name|configure
parameter_list|()
block|{
if|if
condition|(
name|slave
condition|)
block|{
name|bind
argument_list|(
name|AccountIndex
operator|.
name|Factory
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|s
lambda|->
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|ChangeIndex
operator|.
name|Factory
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|s
lambda|->
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|ProjectIndex
operator|.
name|Factory
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|s
lambda|->
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|install
argument_list|(
operator|new
name|FactoryModuleBuilder
argument_list|()
operator|.
name|implement
argument_list|(
name|AccountIndex
operator|.
name|class
argument_list|,
name|getAccountIndex
argument_list|()
argument_list|)
operator|.
name|build
argument_list|(
name|AccountIndex
operator|.
name|Factory
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|install
argument_list|(
operator|new
name|FactoryModuleBuilder
argument_list|()
operator|.
name|implement
argument_list|(
name|ChangeIndex
operator|.
name|class
argument_list|,
name|getChangeIndex
argument_list|()
argument_list|)
operator|.
name|build
argument_list|(
name|ChangeIndex
operator|.
name|Factory
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|install
argument_list|(
operator|new
name|FactoryModuleBuilder
argument_list|()
operator|.
name|implement
argument_list|(
name|ProjectIndex
operator|.
name|class
argument_list|,
name|getProjectIndex
argument_list|()
argument_list|)
operator|.
name|build
argument_list|(
name|ProjectIndex
operator|.
name|Factory
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|install
argument_list|(
operator|new
name|FactoryModuleBuilder
argument_list|()
operator|.
name|implement
argument_list|(
name|GroupIndex
operator|.
name|class
argument_list|,
name|getGroupIndex
argument_list|()
argument_list|)
operator|.
name|build
argument_list|(
name|GroupIndex
operator|.
name|Factory
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|install
argument_list|(
operator|new
name|IndexModule
argument_list|(
name|threads
argument_list|,
name|slave
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|singleVersions
operator|==
literal|null
condition|)
block|{
name|install
argument_list|(
operator|new
name|MultiVersionModule
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|install
argument_list|(
operator|new
name|SingleVersionModule
argument_list|(
name|singleVersions
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getAccountIndex ()
specifier|protected
specifier|abstract
name|Class
argument_list|<
name|?
extends|extends
name|AccountIndex
argument_list|>
name|getAccountIndex
parameter_list|()
function_decl|;
DECL|method|getChangeIndex ()
specifier|protected
specifier|abstract
name|Class
argument_list|<
name|?
extends|extends
name|ChangeIndex
argument_list|>
name|getChangeIndex
parameter_list|()
function_decl|;
DECL|method|getGroupIndex ()
specifier|protected
specifier|abstract
name|Class
argument_list|<
name|?
extends|extends
name|GroupIndex
argument_list|>
name|getGroupIndex
parameter_list|()
function_decl|;
DECL|method|getProjectIndex ()
specifier|protected
specifier|abstract
name|Class
argument_list|<
name|?
extends|extends
name|ProjectIndex
argument_list|>
name|getProjectIndex
parameter_list|()
function_decl|;
DECL|method|getVersionManager ()
specifier|protected
specifier|abstract
name|Class
argument_list|<
name|?
extends|extends
name|VersionManager
argument_list|>
name|getVersionManager
parameter_list|()
function_decl|;
annotation|@
name|Provides
annotation|@
name|Singleton
DECL|method|provideIndexConfig (@erritServerConfig Config cfg)
name|IndexConfig
name|provideIndexConfig
parameter_list|(
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|)
block|{
return|return
name|getIndexConfig
argument_list|(
name|cfg
argument_list|)
return|;
block|}
DECL|method|getIndexConfig (@erritServerConfig Config cfg)
specifier|protected
name|IndexConfig
name|getIndexConfig
parameter_list|(
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|)
block|{
return|return
name|IndexConfig
operator|.
name|fromConfig
argument_list|(
name|cfg
argument_list|)
operator|.
name|separateChangeSubIndexes
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|class|MultiVersionModule
specifier|private
class|class
name|MultiVersionModule
extends|extends
name|LifecycleModule
block|{
annotation|@
name|Override
DECL|method|configure ()
specifier|public
name|void
name|configure
parameter_list|()
block|{
name|Class
argument_list|<
name|?
extends|extends
name|VersionManager
argument_list|>
name|versionManagerClass
init|=
name|getVersionManager
argument_list|()
decl_stmt|;
name|bind
argument_list|(
name|VersionManager
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|versionManagerClass
argument_list|)
expr_stmt|;
name|listener
argument_list|()
operator|.
name|to
argument_list|(
name|versionManagerClass
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

