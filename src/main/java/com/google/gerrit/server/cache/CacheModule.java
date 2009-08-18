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
DECL|package|com.google.gerrit.server.cache
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
package|;
end_package

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
name|Key
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|name
operator|.
name|Names
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_comment
comment|/**  * Miniature DSL to support binding {@link Cache} instances in Guice.  */
end_comment

begin_class
DECL|class|CacheModule
specifier|public
specifier|abstract
class|class
name|CacheModule
extends|extends
name|AbstractModule
block|{
comment|/**    * Declare an unnamed in-memory cache.    *    * @param<K> type of key used to lookup entries.    * @param<V> type of value stored by the cache.    * @param type type literal for the cache, this literal will be used to match    *        injection sites.    * @return binding to describe the cache. Caller must set at least the name on    *         the returned binding.    */
DECL|method|core (final TypeLiteral<Cache<K, V>> type)
specifier|protected
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|UnnamedCacheBinding
name|core
parameter_list|(
specifier|final
name|TypeLiteral
argument_list|<
name|Cache
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
name|type
parameter_list|)
block|{
return|return
name|core
argument_list|(
name|Key
operator|.
name|get
argument_list|(
name|type
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Declare a named in-memory cache.    *    * @param<K> type of key used to lookup entries.    * @param<V> type of value stored by the cache.    * @param type type literal for the cache, this literal will be used to match    *        injection sites. Injection sites are matched by this type literal    *        and with {@code @Named} annotations.    * @return binding to describe the cache.    */
DECL|method|core (final TypeLiteral<Cache<K, V>> type, final String name)
specifier|protected
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|NamedCacheBinding
name|core
parameter_list|(
specifier|final
name|TypeLiteral
argument_list|<
name|Cache
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
name|type
parameter_list|,
specifier|final
name|String
name|name
parameter_list|)
block|{
return|return
name|core
argument_list|(
name|Key
operator|.
name|get
argument_list|(
name|type
argument_list|,
name|Names
operator|.
name|named
argument_list|(
name|name
argument_list|)
argument_list|)
argument_list|)
operator|.
name|name
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|core (final Key<Cache<K, V>> key)
specifier|private
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|UnnamedCacheBinding
name|core
parameter_list|(
specifier|final
name|Key
argument_list|<
name|Cache
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
name|key
parameter_list|)
block|{
specifier|final
name|boolean
name|disk
init|=
literal|false
decl_stmt|;
specifier|final
name|CacheProvider
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|b
init|=
operator|new
name|CacheProvider
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|(
name|disk
argument_list|)
decl_stmt|;
name|bind
argument_list|(
name|key
argument_list|)
operator|.
name|toProvider
argument_list|(
name|b
argument_list|)
expr_stmt|;
return|return
name|b
return|;
block|}
comment|/**    * Declare an unnamed in-memory/on-disk cache.    *    * @param<K> type of key used to find entries, must be {@link Serializable}.    * @param<V> type of value stored by the cache, must be {@link Serializable}.    * @param type type literal for the cache, this literal will be used to match    *        injection sites. Injection sites are matched by this type literal    *        and with {@code @Named} annotations.    * @return binding to describe the cache. Caller must set at least the name on    *         the returned binding.    */
DECL|method|disk ( final TypeLiteral<Cache<K, V>> type)
specifier|protected
parameter_list|<
name|K
extends|extends
name|Serializable
parameter_list|,
name|V
extends|extends
name|Serializable
parameter_list|>
name|UnnamedCacheBinding
name|disk
parameter_list|(
specifier|final
name|TypeLiteral
argument_list|<
name|Cache
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
name|type
parameter_list|)
block|{
return|return
name|disk
argument_list|(
name|Key
operator|.
name|get
argument_list|(
name|type
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Declare a named in-memory/on-disk cache.    *    * @param<K> type of key used to find entries, must be {@link Serializable}.    * @param<V> type of value stored by the cache, must be {@link Serializable}.    * @param type type literal for the cache, this literal will be used to match    *        injection sites. Injection sites are matched by this type literal    *        and with {@code @Named} annotations.    * @return binding to describe the cache.    */
DECL|method|disk ( final TypeLiteral<Cache<K, V>> type, final String name)
specifier|protected
parameter_list|<
name|K
extends|extends
name|Serializable
parameter_list|,
name|V
extends|extends
name|Serializable
parameter_list|>
name|NamedCacheBinding
name|disk
parameter_list|(
specifier|final
name|TypeLiteral
argument_list|<
name|Cache
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
name|type
parameter_list|,
specifier|final
name|String
name|name
parameter_list|)
block|{
return|return
name|disk
argument_list|(
name|Key
operator|.
name|get
argument_list|(
name|type
argument_list|,
name|Names
operator|.
name|named
argument_list|(
name|name
argument_list|)
argument_list|)
argument_list|)
operator|.
name|name
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|disk (final Key<Cache<K, V>> key)
specifier|private
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|UnnamedCacheBinding
name|disk
parameter_list|(
specifier|final
name|Key
argument_list|<
name|Cache
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
name|key
parameter_list|)
block|{
specifier|final
name|boolean
name|disk
init|=
literal|true
decl_stmt|;
specifier|final
name|CacheProvider
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|b
init|=
operator|new
name|CacheProvider
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|(
name|disk
argument_list|)
decl_stmt|;
name|bind
argument_list|(
name|key
argument_list|)
operator|.
name|toProvider
argument_list|(
name|b
argument_list|)
expr_stmt|;
return|return
name|b
return|;
block|}
block|}
end_class

end_unit

