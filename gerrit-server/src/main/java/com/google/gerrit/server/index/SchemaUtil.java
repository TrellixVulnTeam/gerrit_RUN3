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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkArgument
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
name|collect
operator|.
name|ImmutableList
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
name|collect
operator|.
name|ImmutableMap
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
name|collect
operator|.
name|Maps
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Modifier
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|ParameterizedType
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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

begin_class
DECL|class|SchemaUtil
specifier|public
class|class
name|SchemaUtil
block|{
DECL|method|schemasFromClass ( Class<?> schemasClass, Class<V> valueClass)
specifier|public
specifier|static
parameter_list|<
name|V
parameter_list|>
name|ImmutableMap
argument_list|<
name|Integer
argument_list|,
name|Schema
argument_list|<
name|V
argument_list|>
argument_list|>
name|schemasFromClass
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|schemasClass
parameter_list|,
name|Class
argument_list|<
name|V
argument_list|>
name|valueClass
parameter_list|)
block|{
name|Map
argument_list|<
name|Integer
argument_list|,
name|Schema
argument_list|<
name|V
argument_list|>
argument_list|>
name|schemas
init|=
name|Maps
operator|.
name|newTreeMap
argument_list|()
decl_stmt|;
for|for
control|(
name|Field
name|f
range|:
name|schemasClass
operator|.
name|getDeclaredFields
argument_list|()
control|)
block|{
if|if
condition|(
name|Modifier
operator|.
name|isStatic
argument_list|(
name|f
operator|.
name|getModifiers
argument_list|()
argument_list|)
operator|&&
name|Modifier
operator|.
name|isFinal
argument_list|(
name|f
operator|.
name|getModifiers
argument_list|()
argument_list|)
operator|&&
name|Schema
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|f
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
name|ParameterizedType
name|t
init|=
operator|(
name|ParameterizedType
operator|)
name|f
operator|.
name|getGenericType
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|.
name|getActualTypeArguments
argument_list|()
index|[
literal|0
index|]
operator|==
name|valueClass
condition|)
block|{
try|try
block|{
name|f
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Schema
argument_list|<
name|V
argument_list|>
name|schema
init|=
operator|(
name|Schema
argument_list|<
name|V
argument_list|>
operator|)
name|f
operator|.
name|get
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|checkArgument
argument_list|(
name|f
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"V"
argument_list|)
argument_list|)
expr_stmt|;
name|schema
operator|.
name|setVersion
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|f
operator|.
name|getName
argument_list|()
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|schemas
operator|.
name|put
argument_list|(
name|schema
operator|.
name|getVersion
argument_list|()
argument_list|,
name|schema
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"non-"
operator|+
name|schemasClass
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" schema: "
operator|+
name|f
argument_list|)
throw|;
block|}
block|}
block|}
if|if
condition|(
name|schemas
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ExceptionInInitializerError
argument_list|(
literal|"no ChangeSchemas found"
argument_list|)
throw|;
block|}
return|return
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|schemas
argument_list|)
return|;
block|}
DECL|method|schema (Collection<FieldDef<V, ?>> fields)
specifier|public
specifier|static
parameter_list|<
name|V
parameter_list|>
name|Schema
argument_list|<
name|V
argument_list|>
name|schema
parameter_list|(
name|Collection
argument_list|<
name|FieldDef
argument_list|<
name|V
argument_list|,
name|?
argument_list|>
argument_list|>
name|fields
parameter_list|)
block|{
return|return
operator|new
name|Schema
argument_list|<>
argument_list|(
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|fields
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|SafeVarargs
DECL|method|schema (FieldDef<V, ?>.... fields)
specifier|public
specifier|static
parameter_list|<
name|V
parameter_list|>
name|Schema
argument_list|<
name|V
argument_list|>
name|schema
parameter_list|(
name|FieldDef
argument_list|<
name|V
argument_list|,
name|?
argument_list|>
modifier|...
name|fields
parameter_list|)
block|{
return|return
name|schema
argument_list|(
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|fields
argument_list|)
argument_list|)
return|;
block|}
DECL|method|SchemaUtil ()
specifier|private
name|SchemaUtil
parameter_list|()
block|{   }
block|}
end_class

end_unit

