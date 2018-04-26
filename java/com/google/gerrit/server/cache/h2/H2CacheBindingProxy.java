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
name|inject
operator|.
name|TypeLiteral
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

begin_class
DECL|class|H2CacheBindingProxy
class|class
name|H2CacheBindingProxy
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
implements|implements
name|CacheBinding
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
DECL|field|MSG_NOT_SUPPORTED
specifier|private
specifier|static
specifier|final
name|String
name|MSG_NOT_SUPPORTED
init|=
literal|"This is read-only wrapper. Modifications are not supported"
decl_stmt|;
DECL|field|source
specifier|private
specifier|final
name|CacheBinding
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|source
decl_stmt|;
DECL|method|H2CacheBindingProxy (CacheBinding<K, V> source)
name|H2CacheBindingProxy
parameter_list|(
name|CacheBinding
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|source
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|expireAfterWrite (TimeUnit unit)
specifier|public
name|Long
name|expireAfterWrite
parameter_list|(
name|TimeUnit
name|unit
parameter_list|)
block|{
return|return
name|source
operator|.
name|expireAfterWrite
argument_list|(
name|unit
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|weigher ()
specifier|public
name|Weigher
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|weigher
parameter_list|()
block|{
name|Weigher
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|weigher
init|=
name|source
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
return|return
literal|null
return|;
block|}
comment|// introduce weigher that performs calculations
comment|// on value that is being stored not on ValueHolder
return|return
operator|(
name|Weigher
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
operator|)
operator|new
name|Weigher
argument_list|<
name|K
argument_list|,
name|ValueHolder
argument_list|<
name|V
argument_list|>
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
name|ValueHolder
argument_list|<
name|V
argument_list|>
name|value
parameter_list|)
block|{
return|return
name|weigher
operator|.
name|weigh
argument_list|(
name|key
argument_list|,
name|value
operator|.
name|value
argument_list|)
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|name ()
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|source
operator|.
name|name
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|configKey ()
specifier|public
name|String
name|configKey
parameter_list|()
block|{
return|return
name|source
operator|.
name|configKey
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|keyType ()
specifier|public
name|TypeLiteral
argument_list|<
name|K
argument_list|>
name|keyType
parameter_list|()
block|{
return|return
name|source
operator|.
name|keyType
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|valueType ()
specifier|public
name|TypeLiteral
argument_list|<
name|V
argument_list|>
name|valueType
parameter_list|()
block|{
return|return
name|source
operator|.
name|valueType
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|maximumWeight ()
specifier|public
name|long
name|maximumWeight
parameter_list|()
block|{
return|return
name|source
operator|.
name|maximumWeight
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|diskLimit ()
specifier|public
name|long
name|diskLimit
parameter_list|()
block|{
return|return
name|source
operator|.
name|diskLimit
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|loader ()
specifier|public
name|CacheLoader
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|loader
parameter_list|()
block|{
return|return
name|source
operator|.
name|loader
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|configKey (String configKey)
specifier|public
name|CacheBinding
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|configKey
parameter_list|(
name|String
name|configKey
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|MSG_NOT_SUPPORTED
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|maximumWeight (long weight)
specifier|public
name|CacheBinding
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|maximumWeight
parameter_list|(
name|long
name|weight
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|MSG_NOT_SUPPORTED
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|diskLimit (long limit)
specifier|public
name|CacheBinding
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|diskLimit
parameter_list|(
name|long
name|limit
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|MSG_NOT_SUPPORTED
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|expireAfterWrite (long duration, TimeUnit durationUnits)
specifier|public
name|CacheBinding
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|expireAfterWrite
parameter_list|(
name|long
name|duration
parameter_list|,
name|TimeUnit
name|durationUnits
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|MSG_NOT_SUPPORTED
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|loader (Class<? extends CacheLoader<K, V>> clazz)
specifier|public
name|CacheBinding
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|loader
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|CacheLoader
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
name|clazz
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|MSG_NOT_SUPPORTED
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|weigher (Class<? extends Weigher<K, V>> clazz)
specifier|public
name|CacheBinding
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|weigher
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Weigher
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
name|clazz
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|MSG_NOT_SUPPORTED
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

