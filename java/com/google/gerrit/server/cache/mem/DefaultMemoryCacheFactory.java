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
DECL|package|com.google.gerrit.server.cache.mem
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
name|mem
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
name|NANOSECONDS
import|;
end_import

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
name|SECONDS
import|;
end_import

begin_import
import|import
name|com
operator|.
name|github
operator|.
name|benmanes
operator|.
name|caffeine
operator|.
name|cache
operator|.
name|Caffeine
import|;
end_import

begin_import
import|import
name|com
operator|.
name|github
operator|.
name|benmanes
operator|.
name|caffeine
operator|.
name|cache
operator|.
name|RemovalListener
import|;
end_import

begin_import
import|import
name|com
operator|.
name|github
operator|.
name|benmanes
operator|.
name|caffeine
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
name|github
operator|.
name|benmanes
operator|.
name|caffeine
operator|.
name|guava
operator|.
name|CaffeinatedGuava
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
name|RemovalNotification
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
name|server
operator|.
name|cache
operator|.
name|CacheBackend
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
name|CacheDef
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
name|time
operator|.
name|Duration
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
DECL|class|DefaultMemoryCacheFactory
class|class
name|DefaultMemoryCacheFactory
implements|implements
name|MemoryCacheFactory
block|{
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
DECL|method|DefaultMemoryCacheFactory ( @erritServerConfig Config config, ForwardingRemovalListener.Factory forwardingRemovalListenerFactory)
name|DefaultMemoryCacheFactory
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
DECL|method|build (CacheDef<K, V> def, CacheBackend backend)
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
name|CacheDef
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|def
parameter_list|,
name|CacheBackend
name|backend
parameter_list|)
block|{
return|return
name|backend
operator|.
name|isLegacyBackend
argument_list|()
condition|?
name|createLegacy
argument_list|(
name|def
argument_list|)
operator|.
name|build
argument_list|()
else|:
name|CaffeinatedGuava
operator|.
name|build
argument_list|(
name|create
argument_list|(
name|def
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|build ( CacheDef<K, V> def, CacheLoader<K, V> loader, CacheBackend backend)
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
name|CacheDef
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
parameter_list|,
name|CacheBackend
name|backend
parameter_list|)
block|{
return|return
name|backend
operator|.
name|isLegacyBackend
argument_list|()
condition|?
name|createLegacy
argument_list|(
name|def
argument_list|)
operator|.
name|build
argument_list|(
name|loader
argument_list|)
else|:
name|CaffeinatedGuava
operator|.
name|build
argument_list|(
name|create
argument_list|(
name|def
argument_list|)
argument_list|,
name|loader
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|createLegacy (CacheDef<K, V> def)
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
name|createLegacy
parameter_list|(
name|CacheDef
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
name|newLegacyCacheBuilder
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
name|configKey
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
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
name|Duration
name|expireAfterWrite
init|=
name|def
operator|.
name|expireAfterWrite
argument_list|()
decl_stmt|;
if|if
condition|(
name|has
argument_list|(
name|def
operator|.
name|configKey
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
name|configKey
argument_list|()
argument_list|,
literal|"maxAge"
argument_list|,
name|toSeconds
argument_list|(
name|expireAfterWrite
argument_list|)
argument_list|,
name|SECONDS
argument_list|)
argument_list|,
name|SECONDS
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|expireAfterWrite
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|expireAfterWrite
argument_list|(
name|expireAfterWrite
operator|.
name|toNanos
argument_list|()
argument_list|,
name|NANOSECONDS
argument_list|)
expr_stmt|;
block|}
name|Duration
name|expireAfterAccess
init|=
name|def
operator|.
name|expireFromMemoryAfterAccess
argument_list|()
decl_stmt|;
if|if
condition|(
name|has
argument_list|(
name|def
operator|.
name|configKey
argument_list|()
argument_list|,
literal|"expireFromMemoryAfterAccess"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|expireAfterAccess
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
name|configKey
argument_list|()
argument_list|,
literal|"expireFromMemoryAfterAccess"
argument_list|,
name|toSeconds
argument_list|(
name|expireAfterAccess
argument_list|)
argument_list|,
name|SECONDS
argument_list|)
argument_list|,
name|SECONDS
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|expireAfterAccess
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|expireAfterAccess
argument_list|(
name|expireAfterAccess
operator|.
name|toNanos
argument_list|()
argument_list|,
name|NANOSECONDS
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
DECL|method|create (CacheDef<K, V> def)
specifier|private
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|Caffeine
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|create
parameter_list|(
name|CacheDef
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|def
parameter_list|)
block|{
name|Caffeine
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
name|configKey
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
name|newRemovalListener
argument_list|(
name|def
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|weigher
argument_list|(
name|newWeigher
argument_list|(
name|def
operator|.
name|weigher
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Duration
name|expireAfterWrite
init|=
name|def
operator|.
name|expireAfterWrite
argument_list|()
decl_stmt|;
if|if
condition|(
name|has
argument_list|(
name|def
operator|.
name|configKey
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
name|configKey
argument_list|()
argument_list|,
literal|"maxAge"
argument_list|,
name|toSeconds
argument_list|(
name|expireAfterWrite
argument_list|)
argument_list|,
name|SECONDS
argument_list|)
argument_list|,
name|SECONDS
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|expireAfterWrite
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|expireAfterWrite
argument_list|(
name|expireAfterWrite
operator|.
name|toNanos
argument_list|()
argument_list|,
name|NANOSECONDS
argument_list|)
expr_stmt|;
block|}
name|Duration
name|expireAfterAccess
init|=
name|def
operator|.
name|expireFromMemoryAfterAccess
argument_list|()
decl_stmt|;
if|if
condition|(
name|has
argument_list|(
name|def
operator|.
name|configKey
argument_list|()
argument_list|,
literal|"expireFromMemoryAfterAccess"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|expireAfterAccess
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
name|configKey
argument_list|()
argument_list|,
literal|"expireFromMemoryAfterAccess"
argument_list|,
name|toSeconds
argument_list|(
name|expireAfterAccess
argument_list|)
argument_list|,
name|SECONDS
argument_list|)
argument_list|,
name|SECONDS
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|expireAfterAccess
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|expireAfterAccess
argument_list|(
name|expireAfterAccess
operator|.
name|toNanos
argument_list|()
argument_list|,
name|NANOSECONDS
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
DECL|method|toSeconds (@ullable Duration duration)
specifier|private
specifier|static
name|long
name|toSeconds
parameter_list|(
annotation|@
name|Nullable
name|Duration
name|duration
parameter_list|)
block|{
return|return
name|duration
operator|!=
literal|null
condition|?
name|duration
operator|.
name|getSeconds
argument_list|()
else|:
literal|0
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
DECL|method|newLegacyCacheBuilder ()
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
name|newLegacyCacheBuilder
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
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
parameter_list|(
name|key
parameter_list|,
name|value
parameter_list|)
lambda|->
literal|1
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
name|Caffeine
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
name|Caffeine
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
operator|)
name|Caffeine
operator|.
name|newBuilder
argument_list|()
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|newRemovalListener (String cacheName)
specifier|private
parameter_list|<
name|V
parameter_list|,
name|K
parameter_list|>
name|RemovalListener
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|newRemovalListener
parameter_list|(
name|String
name|cacheName
parameter_list|)
block|{
return|return
parameter_list|(
name|k
parameter_list|,
name|v
parameter_list|,
name|cause
parameter_list|)
lambda|->
name|forwardingRemovalListenerFactory
operator|.
name|create
argument_list|(
name|cacheName
argument_list|)
operator|.
name|onRemoval
argument_list|(
name|RemovalNotification
operator|.
name|create
argument_list|(
name|k
argument_list|,
name|v
argument_list|,
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|RemovalCause
operator|.
name|valueOf
argument_list|(
name|cause
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
DECL|method|newWeigher ( com.google.common.cache.Weigher<K, V> guavaWeigher)
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
name|newWeigher
parameter_list|(
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|Weigher
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|guavaWeigher
parameter_list|)
block|{
return|return
name|guavaWeigher
operator|==
literal|null
condition|?
name|Weigher
operator|.
name|singletonWeigher
argument_list|()
else|:
parameter_list|(
name|k
parameter_list|,
name|v
parameter_list|)
lambda|->
name|guavaWeigher
operator|.
name|weigh
argument_list|(
name|k
argument_list|,
name|v
argument_list|)
return|;
block|}
block|}
end_class

end_unit

