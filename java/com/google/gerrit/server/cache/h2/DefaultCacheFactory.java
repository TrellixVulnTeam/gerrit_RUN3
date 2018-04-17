begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2012 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.cache.h2
package|package
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
name|h2
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
name|base
operator|.
name|Strings
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
name|common
operator|.
name|cache
operator|.
name|CacheBuilder
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
name|CacheLoader
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
name|LoadingCache
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
name|Weigher
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
name|config
operator|.
name|FactoryModule
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
name|cache
operator|.
name|CacheBinding
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
name|CacheImpl
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
name|ForwardingRemovalListener
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
name|MemoryCacheFactory
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
name|PersistentCacheFactory
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
name|ConfigUtil
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
name|inject
operator|.
name|Inject
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
DECL|class|DefaultCacheFactory
specifier|public
class|class
name|DefaultCacheFactory
implements|implements
name|MemoryCacheFactory
block|{
annotation|@
name|CacheImpl
argument_list|(
name|type
operator|=
name|CacheImpl
operator|.
name|Type
operator|.
name|MEMORY
argument_list|)
DECL|class|MemoryCacheModule
specifier|public
specifier|static
class|class
name|MemoryCacheModule
extends|extends
name|FactoryModule
block|{
annotation|@
name|Override
DECL|method|configure ()
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|factory
argument_list|(
name|ForwardingRemovalListener
operator|.
name|Factory
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|MemoryCacheFactory
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|DefaultCacheFactory
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|CacheImpl
argument_list|(
name|type
operator|=
name|CacheImpl
operator|.
name|Type
operator|.
name|PERSISTENT
argument_list|)
DECL|class|PersistentCacheModule
specifier|public
specifier|static
class|class
name|PersistentCacheModule
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
name|bind
argument_list|(
name|PersistentCacheFactory
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|H2CacheFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|listener
argument_list|()
operator|.
name|to
argument_list|(
name|H2CacheFactory
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|cfg
specifier|private
specifier|final
name|Config
name|cfg
decl_stmt|;
DECL|field|forwardingRemovalListenerFactory
specifier|private
specifier|final
name|ForwardingRemovalListener
operator|.
name|Factory
name|forwardingRemovalListenerFactory
decl_stmt|;
annotation|@
name|Inject
DECL|method|DefaultCacheFactory ( @erritServerConfig Config config, ForwardingRemovalListener.Factory forwardingRemovalListenerFactory)
specifier|public
name|DefaultCacheFactory
parameter_list|(
annotation|@
name|GerritServerConfig
name|Config
name|config
parameter_list|,
name|ForwardingRemovalListener
operator|.
name|Factory
name|forwardingRemovalListenerFactory
parameter_list|)
block|{
name|this
operator|.
name|cfg
operator|=
name|config
expr_stmt|;
name|this
operator|.
name|forwardingRemovalListenerFactory
operator|=
name|forwardingRemovalListenerFactory
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|build (CacheBinding<K, V> def)
specifier|public
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|Cache
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|build
parameter_list|(
name|CacheBinding
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|def
parameter_list|)
block|{
return|return
name|create
argument_list|(
name|def
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|build (CacheBinding<K, V> def, CacheLoader<K, V> loader)
specifier|public
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|LoadingCache
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|build
parameter_list|(
name|CacheBinding
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|def
parameter_list|,
name|CacheLoader
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|loader
parameter_list|)
block|{
return|return
name|create
argument_list|(
name|def
argument_list|)
operator|.
name|build
argument_list|(
name|loader
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|create (CacheBinding<K, V> def)
specifier|private
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|CacheBuilder
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|create
parameter_list|(
name|CacheBinding
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|def
parameter_list|)
block|{
name|CacheBuilder
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|builder
init|=
name|newCacheBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|recordStats
argument_list|()
expr_stmt|;
name|builder
operator|.
name|maximumWeight
argument_list|(
name|cfg
operator|.
name|getLong
argument_list|(
literal|"cache"
argument_list|,
name|def
operator|.
name|name
argument_list|()
argument_list|,
literal|"memoryLimit"
argument_list|,
name|def
operator|.
name|maximumWeight
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|=
name|builder
operator|.
name|removalListener
argument_list|(
name|forwardingRemovalListenerFactory
operator|.
name|create
argument_list|(
name|def
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Weigher
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|weigher
init|=
name|def
operator|.
name|weigher
argument_list|()
decl_stmt|;
if|if
condition|(
name|weigher
operator|==
literal|null
condition|)
block|{
name|weigher
operator|=
name|unitWeight
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|weigher
argument_list|(
name|weigher
argument_list|)
expr_stmt|;
name|Long
name|age
init|=
name|def
operator|.
name|expireAfterWrite
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|has
argument_list|(
name|def
operator|.
name|name
argument_list|()
argument_list|,
literal|"maxAge"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|expireAfterWrite
argument_list|(
name|ConfigUtil
operator|.
name|getTimeUnit
argument_list|(
name|cfg
argument_list|,
literal|"cache"
argument_list|,
name|def
operator|.
name|name
argument_list|()
argument_list|,
literal|"maxAge"
argument_list|,
name|age
operator|!=
literal|null
condition|?
name|age
else|:
literal|0
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|age
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|expireAfterWrite
argument_list|(
name|age
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
DECL|method|has (String name, String var)
specifier|private
name|boolean
name|has
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|var
parameter_list|)
block|{
return|return
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|cfg
operator|.
name|getString
argument_list|(
literal|"cache"
argument_list|,
name|name
argument_list|,
name|var
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|newCacheBuilder ()
specifier|private
specifier|static
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|CacheBuilder
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|newCacheBuilder
parameter_list|()
block|{
return|return
operator|(
name|CacheBuilder
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
operator|)
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
return|;
block|}
DECL|method|unitWeight ()
specifier|private
specifier|static
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|Weigher
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|unitWeight
parameter_list|()
block|{
return|return
operator|new
name|Weigher
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|weigh
parameter_list|(
name|K
name|key
parameter_list|,
name|V
name|value
parameter_list|)
block|{
return|return
literal|1
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

