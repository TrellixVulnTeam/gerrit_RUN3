begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2009 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.config
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|config
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Scopes
operator|.
name|SINGLETON
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
name|securestore
operator|.
name|DefaultSecureStore
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
name|securestore
operator|.
name|SecureStore
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
name|securestore
operator|.
name|SecureStoreProvider
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
name|Guice
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
name|ProvisionException
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
name|nio
operator|.
name|file
operator|.
name|Path
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
name|errors
operator|.
name|ConfigInvalidException
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

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|storage
operator|.
name|file
operator|.
name|FileBasedConfig
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
name|util
operator|.
name|FS
import|;
end_import

begin_comment
comment|/** Creates {@link GerritServerConfig}. */
end_comment

begin_class
DECL|class|GerritServerConfigModule
specifier|public
class|class
name|GerritServerConfigModule
extends|extends
name|AbstractModule
block|{
DECL|method|getSecureStoreClassName (Path sitePath)
specifier|public
specifier|static
name|String
name|getSecureStoreClassName
parameter_list|(
name|Path
name|sitePath
parameter_list|)
block|{
if|if
condition|(
name|sitePath
operator|!=
literal|null
condition|)
block|{
return|return
name|getSecureStoreFromGerritConfig
argument_list|(
name|sitePath
argument_list|)
return|;
block|}
name|String
name|secureStoreProperty
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"gerrit.secure_store_class"
argument_list|)
decl_stmt|;
return|return
name|nullToDefault
argument_list|(
name|secureStoreProperty
argument_list|)
return|;
block|}
DECL|method|getSecureStoreFromGerritConfig (Path sitePath)
specifier|private
specifier|static
name|String
name|getSecureStoreFromGerritConfig
parameter_list|(
name|Path
name|sitePath
parameter_list|)
block|{
name|AbstractModule
name|m
init|=
operator|new
name|AbstractModule
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|bind
argument_list|(
name|Path
operator|.
name|class
argument_list|)
operator|.
name|annotatedWith
argument_list|(
name|SitePath
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|sitePath
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|SitePaths
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|Injector
name|injector
init|=
name|Guice
operator|.
name|createInjector
argument_list|(
name|m
argument_list|)
decl_stmt|;
name|SitePaths
name|site
init|=
name|injector
operator|.
name|getInstance
argument_list|(
name|SitePaths
operator|.
name|class
argument_list|)
decl_stmt|;
name|FileBasedConfig
name|cfg
init|=
operator|new
name|FileBasedConfig
argument_list|(
name|site
operator|.
name|gerrit_config
operator|.
name|toFile
argument_list|()
argument_list|,
name|FS
operator|.
name|DETECTED
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|cfg
operator|.
name|getFile
argument_list|()
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
name|DefaultSecureStore
operator|.
name|class
operator|.
name|getName
argument_list|()
return|;
block|}
try|try
block|{
name|cfg
operator|.
name|load
argument_list|()
expr_stmt|;
name|String
name|className
init|=
name|cfg
operator|.
name|getString
argument_list|(
literal|"gerrit"
argument_list|,
literal|null
argument_list|,
literal|"secureStoreClass"
argument_list|)
decl_stmt|;
return|return
name|nullToDefault
argument_list|(
name|className
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|ConfigInvalidException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ProvisionException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|nullToDefault (String className)
specifier|private
specifier|static
name|String
name|nullToDefault
parameter_list|(
name|String
name|className
parameter_list|)
block|{
return|return
name|className
operator|!=
literal|null
condition|?
name|className
else|:
name|DefaultSecureStore
operator|.
name|class
operator|.
name|getName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|configure ()
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|bind
argument_list|(
name|SitePaths
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|TrackingFooters
operator|.
name|class
argument_list|)
operator|.
name|toProvider
argument_list|(
name|TrackingFootersProvider
operator|.
name|class
argument_list|)
operator|.
name|in
argument_list|(
name|SINGLETON
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|Config
operator|.
name|class
argument_list|)
operator|.
name|annotatedWith
argument_list|(
name|GerritServerConfig
operator|.
name|class
argument_list|)
operator|.
name|toProvider
argument_list|(
name|GerritServerConfigProvider
operator|.
name|class
argument_list|)
operator|.
name|in
argument_list|(
name|SINGLETON
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|SecureStore
operator|.
name|class
argument_list|)
operator|.
name|toProvider
argument_list|(
name|SecureStoreProvider
operator|.
name|class
argument_list|)
operator|.
name|in
argument_list|(
name|SINGLETON
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

