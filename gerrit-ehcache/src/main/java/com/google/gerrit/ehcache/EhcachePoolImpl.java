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
DECL|package|com.google.gerrit.ehcache
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|ehcache
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
name|google
operator|.
name|gerrit
operator|.
name|lifecycle
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
name|cache
operator|.
name|CachePool
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
name|CacheProvider
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
name|EntryCreator
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
name|EvictionPolicy
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
name|ProxyCache
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
name|net
operator|.
name|sf
operator|.
name|ehcache
operator|.
name|CacheManager
import|;
end_import

begin_import
import|import
name|net
operator|.
name|sf
operator|.
name|ehcache
operator|.
name|Ehcache
import|;
end_import

begin_import
import|import
name|net
operator|.
name|sf
operator|.
name|ehcache
operator|.
name|config
operator|.
name|CacheConfiguration
import|;
end_import

begin_import
import|import
name|net
operator|.
name|sf
operator|.
name|ehcache
operator|.
name|config
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|net
operator|.
name|sf
operator|.
name|ehcache
operator|.
name|config
operator|.
name|DiskStoreConfiguration
import|;
end_import

begin_import
import|import
name|net
operator|.
name|sf
operator|.
name|ehcache
operator|.
name|store
operator|.
name|MemoryStoreEvictionPolicy
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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

begin_comment
comment|/** Pool of all declared caches created by {@link CacheModule}s. */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|EhcachePoolImpl
specifier|public
class|class
name|EhcachePoolImpl
implements|implements
name|CachePool
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
name|EhcachePoolImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|class|Module
specifier|public
specifier|static
class|class
name|Module
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
name|CachePool
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|EhcachePoolImpl
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|EhcachePoolImpl
operator|.
name|class
argument_list|)
expr_stmt|;
name|listener
argument_list|()
operator|.
name|to
argument_list|(
name|EhcachePoolImpl
operator|.
name|Lifecycle
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|Lifecycle
specifier|public
specifier|static
class|class
name|Lifecycle
implements|implements
name|LifecycleListener
block|{
DECL|field|cachePool
specifier|private
specifier|final
name|EhcachePoolImpl
name|cachePool
decl_stmt|;
annotation|@
name|Inject
DECL|method|Lifecycle (final EhcachePoolImpl cachePool)
name|Lifecycle
parameter_list|(
specifier|final
name|EhcachePoolImpl
name|cachePool
parameter_list|)
block|{
name|this
operator|.
name|cachePool
operator|=
name|cachePool
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{
name|cachePool
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|cachePool
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|field|config
specifier|private
specifier|final
name|Config
name|config
decl_stmt|;
DECL|field|site
specifier|private
specifier|final
name|SitePaths
name|site
decl_stmt|;
DECL|field|lock
specifier|private
specifier|final
name|Object
name|lock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|field|caches
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|CacheProvider
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
argument_list|>
name|caches
decl_stmt|;
DECL|field|manager
specifier|private
name|CacheManager
name|manager
decl_stmt|;
annotation|@
name|Inject
DECL|method|EhcachePoolImpl (@erritServerConfig final Config cfg, final SitePaths site)
name|EhcachePoolImpl
parameter_list|(
annotation|@
name|GerritServerConfig
specifier|final
name|Config
name|cfg
parameter_list|,
specifier|final
name|SitePaths
name|site
parameter_list|)
block|{
name|this
operator|.
name|config
operator|=
name|cfg
expr_stmt|;
name|this
operator|.
name|site
operator|=
name|site
expr_stmt|;
name|this
operator|.
name|caches
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|CacheProvider
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|,
literal|"unchecked"
block|}
argument_list|)
DECL|method|start ()
specifier|private
name|void
name|start
parameter_list|()
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
if|if
condition|(
name|manager
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cache pool has already been started"
argument_list|)
throw|;
block|}
try|try
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"net.sf.ehcache.skipUpdateCheck"
argument_list|,
literal|""
operator|+
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SecurityException
name|e
parameter_list|)
block|{
comment|// Ignore it, the system is just going to ping some external page
comment|// using a background thread and there's not much we can do about
comment|// it now.
block|}
name|manager
operator|=
operator|new
name|CacheManager
argument_list|(
operator|new
name|Factory
argument_list|()
operator|.
name|toConfiguration
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|CacheProvider
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|p
range|:
name|caches
operator|.
name|values
argument_list|()
control|)
block|{
name|Ehcache
name|eh
init|=
name|manager
operator|.
name|getEhcache
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|EntryCreator
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|c
init|=
name|p
operator|.
name|getEntryCreator
argument_list|()
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|null
condition|)
block|{
name|p
operator|.
name|bind
argument_list|(
operator|new
name|PopulatingCache
argument_list|(
name|eh
argument_list|,
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|p
operator|.
name|bind
argument_list|(
operator|new
name|SimpleCache
argument_list|(
name|eh
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|stop ()
specifier|private
name|void
name|stop
parameter_list|()
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
if|if
condition|(
name|manager
operator|!=
literal|null
condition|)
block|{
name|manager
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**<i>Discouraged</i> Get the underlying cache descriptions, for statistics. */
DECL|method|getCacheManager ()
specifier|public
name|CacheManager
name|getCacheManager
parameter_list|()
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
return|return
name|manager
return|;
block|}
block|}
DECL|method|register (final CacheProvider<K, V> provider)
specifier|public
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|ProxyCache
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|register
parameter_list|(
specifier|final
name|CacheProvider
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|provider
parameter_list|)
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
if|if
condition|(
name|manager
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cache pool has already been started"
argument_list|)
throw|;
block|}
specifier|final
name|String
name|n
init|=
name|provider
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|caches
operator|.
name|containsKey
argument_list|(
name|n
argument_list|)
operator|&&
name|caches
operator|.
name|get
argument_list|(
name|n
argument_list|)
operator|!=
name|provider
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cache \""
operator|+
name|n
operator|+
literal|"\" already defined"
argument_list|)
throw|;
block|}
name|caches
operator|.
name|put
argument_list|(
name|n
argument_list|,
name|provider
argument_list|)
expr_stmt|;
return|return
operator|new
name|ProxyCache
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|()
return|;
block|}
block|}
DECL|class|Factory
specifier|private
class|class
name|Factory
block|{
DECL|field|MB
specifier|private
specifier|static
specifier|final
name|int
name|MB
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
DECL|field|mgr
specifier|private
specifier|final
name|Configuration
name|mgr
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|method|toConfiguration ()
name|Configuration
name|toConfiguration
parameter_list|()
block|{
name|configureDiskStore
argument_list|()
expr_stmt|;
name|configureDefaultCache
argument_list|()
expr_stmt|;
for|for
control|(
name|CacheProvider
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|p
range|:
name|caches
operator|.
name|values
argument_list|()
control|)
block|{
specifier|final
name|String
name|name
init|=
name|p
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|final
name|CacheConfiguration
name|c
init|=
name|newCache
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|c
operator|.
name|setMemoryStoreEvictionPolicyFromObject
argument_list|(
name|toPolicy
argument_list|(
name|p
operator|.
name|evictionPolicy
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|c
operator|.
name|setMaxElementsInMemory
argument_list|(
name|getInt
argument_list|(
name|name
argument_list|,
literal|"memorylimit"
argument_list|,
name|p
operator|.
name|memoryLimit
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|c
operator|.
name|setTimeToIdleSeconds
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|c
operator|.
name|setTimeToLiveSeconds
argument_list|(
name|getSeconds
argument_list|(
name|name
argument_list|,
literal|"maxage"
argument_list|,
name|p
operator|.
name|maxAge
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|c
operator|.
name|setEternal
argument_list|(
name|c
operator|.
name|getTimeToLiveSeconds
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|p
operator|.
name|disk
argument_list|()
operator|&&
name|mgr
operator|.
name|getDiskStoreConfiguration
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|c
operator|.
name|setMaxElementsOnDisk
argument_list|(
name|getInt
argument_list|(
name|name
argument_list|,
literal|"disklimit"
argument_list|,
name|p
operator|.
name|diskLimit
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|v
init|=
name|c
operator|.
name|getDiskSpoolBufferSizeMB
argument_list|()
operator|*
name|MB
decl_stmt|;
name|v
operator|=
name|getInt
argument_list|(
name|name
argument_list|,
literal|"diskbuffer"
argument_list|,
name|v
argument_list|)
operator|/
name|MB
expr_stmt|;
name|c
operator|.
name|setDiskSpoolBufferSizeMB
argument_list|(
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
name|v
argument_list|)
argument_list|)
expr_stmt|;
name|c
operator|.
name|setOverflowToDisk
argument_list|(
name|c
operator|.
name|getMaxElementsOnDisk
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|c
operator|.
name|setDiskPersistent
argument_list|(
name|c
operator|.
name|getMaxElementsOnDisk
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
name|mgr
operator|.
name|addCache
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
return|return
name|mgr
return|;
block|}
DECL|method|toPolicy (final EvictionPolicy policy)
specifier|private
name|MemoryStoreEvictionPolicy
name|toPolicy
parameter_list|(
specifier|final
name|EvictionPolicy
name|policy
parameter_list|)
block|{
switch|switch
condition|(
name|policy
condition|)
block|{
case|case
name|LFU
case|:
return|return
name|MemoryStoreEvictionPolicy
operator|.
name|LFU
return|;
case|case
name|LRU
case|:
return|return
name|MemoryStoreEvictionPolicy
operator|.
name|LRU
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unsupported "
operator|+
name|policy
argument_list|)
throw|;
block|}
block|}
DECL|method|getInt (String n, String s, int d)
specifier|private
name|int
name|getInt
parameter_list|(
name|String
name|n
parameter_list|,
name|String
name|s
parameter_list|,
name|int
name|d
parameter_list|)
block|{
return|return
name|config
operator|.
name|getInt
argument_list|(
literal|"cache"
argument_list|,
name|n
argument_list|,
name|s
argument_list|,
name|d
argument_list|)
return|;
block|}
DECL|method|getSeconds (String n, String s, long d)
specifier|private
name|long
name|getSeconds
parameter_list|(
name|String
name|n
parameter_list|,
name|String
name|s
parameter_list|,
name|long
name|d
parameter_list|)
block|{
name|d
operator|=
name|MINUTES
operator|.
name|convert
argument_list|(
name|d
argument_list|,
name|SECONDS
argument_list|)
expr_stmt|;
name|long
name|m
init|=
name|ConfigUtil
operator|.
name|getTimeUnit
argument_list|(
name|config
argument_list|,
literal|"cache"
argument_list|,
name|n
argument_list|,
name|s
argument_list|,
name|d
argument_list|,
name|MINUTES
argument_list|)
decl_stmt|;
return|return
name|SECONDS
operator|.
name|convert
argument_list|(
name|m
argument_list|,
name|MINUTES
argument_list|)
return|;
block|}
DECL|method|configureDiskStore ()
specifier|private
name|void
name|configureDiskStore
parameter_list|()
block|{
name|boolean
name|needDisk
init|=
literal|false
decl_stmt|;
for|for
control|(
name|CacheProvider
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|p
range|:
name|caches
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|p
operator|.
name|disk
argument_list|()
condition|)
block|{
name|needDisk
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|needDisk
condition|)
block|{
return|return;
block|}
name|File
name|loc
init|=
name|site
operator|.
name|resolve
argument_list|(
name|config
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
decl_stmt|;
if|if
condition|(
name|loc
operator|==
literal|null
condition|)
block|{       }
elseif|else
if|if
condition|(
name|loc
operator|.
name|exists
argument_list|()
operator|||
name|loc
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
if|if
condition|(
name|loc
operator|.
name|canWrite
argument_list|()
condition|)
block|{
specifier|final
name|DiskStoreConfiguration
name|c
init|=
operator|new
name|DiskStoreConfiguration
argument_list|()
decl_stmt|;
name|c
operator|.
name|setPath
argument_list|(
name|loc
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|addDiskStore
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Enabling disk cache "
operator|+
name|loc
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Can't write to disk cache: "
operator|+
name|loc
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Can't create disk cache: "
operator|+
name|loc
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|newConfiguration ()
specifier|private
name|CacheConfiguration
name|newConfiguration
parameter_list|()
block|{
name|CacheConfiguration
name|c
init|=
operator|new
name|CacheConfiguration
argument_list|()
decl_stmt|;
name|c
operator|.
name|setMaxElementsInMemory
argument_list|(
literal|1024
argument_list|)
expr_stmt|;
name|c
operator|.
name|setMemoryStoreEvictionPolicyFromObject
argument_list|(
name|MemoryStoreEvictionPolicy
operator|.
name|LFU
argument_list|)
expr_stmt|;
name|c
operator|.
name|setTimeToIdleSeconds
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|c
operator|.
name|setTimeToLiveSeconds
argument_list|(
literal|0
comment|/* infinite */
argument_list|)
expr_stmt|;
name|c
operator|.
name|setEternal
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|mgr
operator|.
name|getDiskStoreConfiguration
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|c
operator|.
name|setMaxElementsOnDisk
argument_list|(
literal|16384
argument_list|)
expr_stmt|;
name|c
operator|.
name|setOverflowToDisk
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|c
operator|.
name|setDiskPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|c
operator|.
name|setDiskSpoolBufferSizeMB
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|c
operator|.
name|setDiskExpiryThreadIntervalSeconds
argument_list|(
literal|60
operator|*
literal|60
argument_list|)
expr_stmt|;
block|}
return|return
name|c
return|;
block|}
DECL|method|configureDefaultCache ()
specifier|private
name|void
name|configureDefaultCache
parameter_list|()
block|{
name|mgr
operator|.
name|setDefaultCacheConfiguration
argument_list|(
name|newConfiguration
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|newCache (final String name)
specifier|private
name|CacheConfiguration
name|newCache
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
block|{
name|CacheConfiguration
name|c
init|=
name|newConfiguration
argument_list|()
decl_stmt|;
name|c
operator|.
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|c
return|;
block|}
block|}
block|}
end_class

end_unit

