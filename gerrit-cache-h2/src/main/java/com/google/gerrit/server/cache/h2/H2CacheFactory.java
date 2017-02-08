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
name|util
operator|.
name|concurrent
operator|.
name|ThreadFactoryBuilder
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
name|events
operator|.
name|LifecycleListener
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
name|DynamicMap
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
name|cache
operator|.
name|h2
operator|.
name|H2CacheImpl
operator|.
name|SqlStore
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
name|h2
operator|.
name|H2CacheImpl
operator|.
name|ValueHolder
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
name|gerrit
operator|.
name|server
operator|.
name|plugins
operator|.
name|Plugin
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
name|TypeLiteral
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
name|Files
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
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutorService
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
name|Executors
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
name|Future
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
name|ScheduledExecutorService
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

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|H2CacheFactory
class|class
name|H2CacheFactory
implements|implements
name|PersistentCacheFactory
implements|,
name|LifecycleListener
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|H2CacheFactory
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|defaultFactory
specifier|private
specifier|final
name|DefaultCacheFactory
name|defaultFactory
decl_stmt|;
DECL|field|config
specifier|private
specifier|final
name|Config
name|config
decl_stmt|;
DECL|field|cacheDir
specifier|private
specifier|final
name|Path
name|cacheDir
decl_stmt|;
DECL|field|caches
specifier|private
specifier|final
name|List
argument_list|<
name|H2CacheImpl
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
argument_list|>
name|caches
decl_stmt|;
DECL|field|cacheMap
specifier|private
specifier|final
name|DynamicMap
argument_list|<
name|Cache
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
argument_list|>
name|cacheMap
decl_stmt|;
DECL|field|executor
specifier|private
specifier|final
name|ExecutorService
name|executor
decl_stmt|;
DECL|field|cleanup
specifier|private
specifier|final
name|ScheduledExecutorService
name|cleanup
decl_stmt|;
DECL|field|h2CacheSize
specifier|private
specifier|final
name|long
name|h2CacheSize
decl_stmt|;
DECL|field|h2AutoServer
specifier|private
specifier|final
name|boolean
name|h2AutoServer
decl_stmt|;
annotation|@
name|Inject
DECL|method|H2CacheFactory ( DefaultCacheFactory defaultCacheFactory, @GerritServerConfig Config cfg, SitePaths site, DynamicMap<Cache<?, ?>> cacheMap)
name|H2CacheFactory
parameter_list|(
name|DefaultCacheFactory
name|defaultCacheFactory
parameter_list|,
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|,
name|SitePaths
name|site
parameter_list|,
name|DynamicMap
argument_list|<
name|Cache
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
argument_list|>
name|cacheMap
parameter_list|)
block|{
name|defaultFactory
operator|=
name|defaultCacheFactory
expr_stmt|;
name|config
operator|=
name|cfg
expr_stmt|;
name|cacheDir
operator|=
name|getCacheDir
argument_list|(
name|site
argument_list|,
name|cfg
operator|.
name|getString
argument_list|(
literal|"cache"
argument_list|,
literal|null
argument_list|,
literal|"directory"
argument_list|)
argument_list|)
expr_stmt|;
name|h2CacheSize
operator|=
name|cfg
operator|.
name|getLong
argument_list|(
literal|"cache"
argument_list|,
literal|null
argument_list|,
literal|"h2CacheSize"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|h2AutoServer
operator|=
name|cfg
operator|.
name|getBoolean
argument_list|(
literal|"cache"
argument_list|,
literal|null
argument_list|,
literal|"h2AutoServer"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|caches
operator|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|cacheMap
operator|=
name|cacheMap
expr_stmt|;
if|if
condition|(
name|cacheDir
operator|!=
literal|null
condition|)
block|{
name|executor
operator|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
literal|1
argument_list|,
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setNameFormat
argument_list|(
literal|"DiskCache-Store-%d"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|cleanup
operator|=
name|Executors
operator|.
name|newScheduledThreadPool
argument_list|(
literal|1
argument_list|,
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setNameFormat
argument_list|(
literal|"DiskCache-Prune-%d"
argument_list|)
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|executor
operator|=
literal|null
expr_stmt|;
name|cleanup
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|getCacheDir (SitePaths site, String name)
specifier|private
specifier|static
name|Path
name|getCacheDir
parameter_list|(
name|SitePaths
name|site
parameter_list|,
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Path
name|loc
init|=
name|site
operator|.
name|resolve
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Files
operator|.
name|exists
argument_list|(
name|loc
argument_list|)
condition|)
block|{
try|try
block|{
name|Files
operator|.
name|createDirectories
argument_list|(
name|loc
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Can't create disk cache: "
operator|+
name|loc
operator|.
name|toAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
if|if
condition|(
operator|!
name|Files
operator|.
name|isWritable
argument_list|(
name|loc
argument_list|)
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Can't write to disk cache: "
operator|+
name|loc
operator|.
name|toAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Enabling disk cache "
operator|+
name|loc
operator|.
name|toAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|loc
return|;
block|}
annotation|@
name|Override
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{
if|if
condition|(
name|executor
operator|!=
literal|null
condition|)
block|{
for|for
control|(
specifier|final
name|H2CacheImpl
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|cache
range|:
name|caches
control|)
block|{
name|executor
operator|.
name|execute
argument_list|(
name|cache
operator|::
name|start
argument_list|)
expr_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
name|Future
argument_list|<
name|?
argument_list|>
name|possiblyIgnoredError
init|=
name|cleanup
operator|.
name|schedule
argument_list|(
parameter_list|()
lambda|->
name|cache
operator|.
name|prune
argument_list|(
name|cleanup
argument_list|)
argument_list|,
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
if|if
condition|(
name|executor
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|cleanup
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|Runnable
argument_list|>
name|pending
init|=
name|executor
operator|.
name|shutdownNow
argument_list|()
decl_stmt|;
if|if
condition|(
name|executor
operator|.
name|awaitTermination
argument_list|(
literal|15
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
condition|)
block|{
if|if
condition|(
name|pending
operator|!=
literal|null
operator|&&
operator|!
name|pending
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Finishing %d disk cache updates"
argument_list|,
name|pending
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Runnable
name|update
range|:
name|pending
control|)
block|{
name|update
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Timeout waiting for disk cache to close"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Interrupted waiting for disk cache to shutdown"
argument_list|)
expr_stmt|;
block|}
block|}
synchronized|synchronized
init|(
name|caches
init|)
block|{
for|for
control|(
name|H2CacheImpl
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|cache
range|:
name|caches
control|)
block|{
name|cache
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|}
argument_list|)
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
name|long
name|limit
init|=
name|config
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
literal|"diskLimit"
argument_list|,
literal|128
operator|<<
literal|20
argument_list|)
decl_stmt|;
if|if
condition|(
name|cacheDir
operator|==
literal|null
operator|||
name|limit
operator|<=
literal|0
condition|)
block|{
return|return
name|defaultFactory
operator|.
name|build
argument_list|(
name|def
argument_list|)
return|;
block|}
name|SqlStore
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|store
init|=
name|newSqlStore
argument_list|(
name|def
operator|.
name|name
argument_list|()
argument_list|,
name|def
operator|.
name|keyType
argument_list|()
argument_list|,
name|limit
argument_list|,
name|def
operator|.
name|expireAfterWrite
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
decl_stmt|;
name|H2CacheImpl
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|cache
init|=
operator|new
name|H2CacheImpl
argument_list|<>
argument_list|(
name|executor
argument_list|,
name|store
argument_list|,
name|def
operator|.
name|keyType
argument_list|()
argument_list|,
operator|(
name|Cache
argument_list|<
name|K
argument_list|,
name|ValueHolder
argument_list|<
name|V
argument_list|>
argument_list|>
operator|)
name|defaultFactory
operator|.
name|create
argument_list|(
name|def
argument_list|,
literal|true
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|caches
init|)
block|{
name|caches
operator|.
name|add
argument_list|(
name|cache
argument_list|)
expr_stmt|;
block|}
return|return
name|cache
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
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
name|long
name|limit
init|=
name|config
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
literal|"diskLimit"
argument_list|,
name|def
operator|.
name|diskLimit
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|cacheDir
operator|==
literal|null
operator|||
name|limit
operator|<=
literal|0
condition|)
block|{
return|return
name|defaultFactory
operator|.
name|build
argument_list|(
name|def
argument_list|,
name|loader
argument_list|)
return|;
block|}
name|SqlStore
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|store
init|=
name|newSqlStore
argument_list|(
name|def
operator|.
name|name
argument_list|()
argument_list|,
name|def
operator|.
name|keyType
argument_list|()
argument_list|,
name|limit
argument_list|,
name|def
operator|.
name|expireAfterWrite
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
decl_stmt|;
name|Cache
argument_list|<
name|K
argument_list|,
name|ValueHolder
argument_list|<
name|V
argument_list|>
argument_list|>
name|mem
init|=
operator|(
name|Cache
argument_list|<
name|K
argument_list|,
name|ValueHolder
argument_list|<
name|V
argument_list|>
argument_list|>
operator|)
name|defaultFactory
operator|.
name|create
argument_list|(
name|def
argument_list|,
literal|true
argument_list|)
operator|.
name|build
argument_list|(
operator|(
name|CacheLoader
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
operator|)
operator|new
name|H2CacheImpl
operator|.
name|Loader
argument_list|<>
argument_list|(
name|executor
argument_list|,
name|store
argument_list|,
name|loader
argument_list|)
argument_list|)
decl_stmt|;
name|H2CacheImpl
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|cache
init|=
operator|new
name|H2CacheImpl
argument_list|<>
argument_list|(
name|executor
argument_list|,
name|store
argument_list|,
name|def
operator|.
name|keyType
argument_list|()
argument_list|,
name|mem
argument_list|)
decl_stmt|;
name|caches
operator|.
name|add
argument_list|(
name|cache
argument_list|)
expr_stmt|;
return|return
name|cache
return|;
block|}
annotation|@
name|Override
DECL|method|onStop (Plugin plugin)
specifier|public
name|void
name|onStop
parameter_list|(
name|Plugin
name|plugin
parameter_list|)
block|{
synchronized|synchronized
init|(
name|caches
init|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Provider
argument_list|<
name|Cache
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
argument_list|>
argument_list|>
name|entry
range|:
name|cacheMap
operator|.
name|byPlugin
argument_list|(
name|plugin
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Cache
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|cache
init|=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|caches
operator|.
name|remove
argument_list|(
name|cache
argument_list|)
condition|)
block|{
operator|(
operator|(
name|H2CacheImpl
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
operator|)
name|cache
operator|)
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|newSqlStore ( String name, TypeLiteral<K> keyType, long maxSize, Long expireAfterWrite)
specifier|private
parameter_list|<
name|V
parameter_list|,
name|K
parameter_list|>
name|SqlStore
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|newSqlStore
parameter_list|(
name|String
name|name
parameter_list|,
name|TypeLiteral
argument_list|<
name|K
argument_list|>
name|keyType
parameter_list|,
name|long
name|maxSize
parameter_list|,
name|Long
name|expireAfterWrite
parameter_list|)
block|{
name|StringBuilder
name|url
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|url
operator|.
name|append
argument_list|(
literal|"jdbc:h2:"
argument_list|)
operator|.
name|append
argument_list|(
name|cacheDir
operator|.
name|resolve
argument_list|(
name|name
argument_list|)
operator|.
name|toUri
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|h2CacheSize
operator|>=
literal|0
condition|)
block|{
name|url
operator|.
name|append
argument_list|(
literal|";CACHE_SIZE="
argument_list|)
expr_stmt|;
comment|// H2 CACHE_SIZE is always given in KB
name|url
operator|.
name|append
argument_list|(
name|h2CacheSize
operator|/
literal|1024
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|h2AutoServer
condition|)
block|{
name|url
operator|.
name|append
argument_list|(
literal|";AUTO_SERVER=TRUE"
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|SqlStore
argument_list|<>
argument_list|(
name|url
operator|.
name|toString
argument_list|()
argument_list|,
name|keyType
argument_list|,
name|maxSize
argument_list|,
name|expireAfterWrite
operator|==
literal|null
condition|?
literal|0
else|:
name|expireAfterWrite
operator|.
name|longValue
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

