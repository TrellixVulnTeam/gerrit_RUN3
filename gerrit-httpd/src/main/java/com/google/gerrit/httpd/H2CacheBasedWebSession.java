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
DECL|package|com.google.gerrit.httpd
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|httpd
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|MINUTES
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|Cache
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
name|common
operator|.
name|Nullable
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
name|httpd
operator|.
name|WebSessionManager
operator|.
name|Val
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
name|AnonymousUser
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
name|IdentifiedUser
operator|.
name|RequestFactory
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
name|cache
operator|.
name|CacheModule
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
name|AuthConfig
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
name|Module
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
name|assistedinject
operator|.
name|FactoryModuleBuilder
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
name|name
operator|.
name|Named
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
name|servlet
operator|.
name|RequestScoped
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
import|;
end_import

begin_class
annotation|@
name|RequestScoped
DECL|class|H2CacheBasedWebSession
specifier|public
class|class
name|H2CacheBasedWebSession
extends|extends
name|CacheBasedWebSession
block|{
DECL|method|module ()
specifier|public
specifier|static
name|Module
name|module
parameter_list|()
block|{
return|return
operator|new
name|CacheModule
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|persist
argument_list|(
name|WebSessionManager
operator|.
name|CACHE_NAME
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|Val
operator|.
name|class
argument_list|)
operator|.
name|maximumWeight
argument_list|(
literal|1024
argument_list|)
comment|// reasonable default for many sites
operator|.
name|expireAfterWrite
argument_list|(
name|CacheBasedWebSession
operator|.
name|MAX_AGE_MINUTES
argument_list|,
name|MINUTES
argument_list|)
comment|// expire sessions if they are inactive
expr_stmt|;
name|install
argument_list|(
operator|new
name|FactoryModuleBuilder
argument_list|()
operator|.
name|build
argument_list|(
name|WebSessionManagerFactory
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|DynamicItem
operator|.
name|itemOf
argument_list|(
name|binder
argument_list|()
argument_list|,
name|WebSession
operator|.
name|class
argument_list|)
expr_stmt|;
name|DynamicItem
operator|.
name|bind
argument_list|(
name|binder
argument_list|()
argument_list|,
name|WebSession
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|H2CacheBasedWebSession
operator|.
name|class
argument_list|)
operator|.
name|in
argument_list|(
name|RequestScoped
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
annotation|@
name|Inject
DECL|method|H2CacheBasedWebSession ( HttpServletRequest request, @Nullable HttpServletResponse response, WebSessionManagerFactory managerFactory, @Named(WebSessionManager.CACHE_NAME) Cache<String, Val> cache, AuthConfig authConfig, Provider<AnonymousUser> anonymousProvider, RequestFactory identified)
name|H2CacheBasedWebSession
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
annotation|@
name|Nullable
name|HttpServletResponse
name|response
parameter_list|,
name|WebSessionManagerFactory
name|managerFactory
parameter_list|,
annotation|@
name|Named
argument_list|(
name|WebSessionManager
operator|.
name|CACHE_NAME
argument_list|)
name|Cache
argument_list|<
name|String
argument_list|,
name|Val
argument_list|>
name|cache
parameter_list|,
name|AuthConfig
name|authConfig
parameter_list|,
name|Provider
argument_list|<
name|AnonymousUser
argument_list|>
name|anonymousProvider
parameter_list|,
name|RequestFactory
name|identified
parameter_list|)
block|{
name|super
argument_list|(
name|request
argument_list|,
name|response
argument_list|,
name|managerFactory
operator|.
name|create
argument_list|(
name|cache
argument_list|)
argument_list|,
name|authConfig
argument_list|,
name|anonymousProvider
argument_list|,
name|identified
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

