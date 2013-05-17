begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2013 The Android Open Source Project
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
comment|// limitations under the License.package com.google.gerrit.server.git;
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
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|query
operator|.
name|change
operator|.
name|ChangeData
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
name|query
operator|.
name|change
operator|.
name|ChangeQueryBuilder
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|server
operator|.
name|OrmException
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
name|Map
import|;
end_import

begin_comment
comment|/**  * Fields indexed on change documents.  *<p>  * Each field corresponds to both a field name supported by  * {@link ChangeQueryBuilder} for querying that field, and a method on  * {@link ChangeData} used for populating the corresponding document fields in  * the secondary index.  *<p>  * Used to generate a schema for index implementations that require one.  */
end_comment

begin_class
DECL|class|ChangeField
specifier|public
class|class
name|ChangeField
block|{
comment|/** Legacy change ID. */
DECL|field|CHANGE_ID
specifier|public
specifier|static
specifier|final
name|FieldDef
argument_list|<
name|ChangeData
argument_list|,
name|Integer
argument_list|>
name|CHANGE_ID
init|=
operator|new
name|FieldDef
operator|.
name|Single
argument_list|<
name|ChangeData
argument_list|,
name|Integer
argument_list|>
argument_list|(
name|ChangeQueryBuilder
operator|.
name|FIELD_CHANGE
argument_list|,
name|FieldType
operator|.
name|INTEGER
argument_list|,
literal|true
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Integer
name|get
parameter_list|(
name|ChangeData
name|input
parameter_list|,
name|FillArgs
name|args
parameter_list|)
block|{
return|return
name|input
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
return|;
block|}
block|}
decl_stmt|;
comment|/** List of filenames modified in the current patch set. */
DECL|field|FILE
specifier|public
specifier|static
specifier|final
name|FieldDef
argument_list|<
name|ChangeData
argument_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
argument_list|>
name|FILE
init|=
operator|new
name|FieldDef
operator|.
name|Repeatable
argument_list|<
name|ChangeData
argument_list|,
name|String
argument_list|>
argument_list|(
name|ChangeQueryBuilder
operator|.
name|FIELD_FILE
argument_list|,
name|FieldType
operator|.
name|EXACT
argument_list|,
literal|false
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|String
argument_list|>
name|get
parameter_list|(
name|ChangeData
name|input
parameter_list|,
name|FillArgs
name|args
parameter_list|)
throws|throws
name|OrmException
block|{
return|return
name|input
operator|.
name|currentFilePaths
argument_list|(
name|args
operator|.
name|db
argument_list|,
name|args
operator|.
name|patchListCache
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|field|ALL
specifier|public
specifier|static
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|FieldDef
argument_list|<
name|ChangeData
argument_list|,
name|?
argument_list|>
argument_list|>
name|ALL
decl_stmt|;
static|static
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|FieldDef
argument_list|<
name|ChangeData
argument_list|,
name|?
argument_list|>
argument_list|>
name|fields
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|Field
name|f
range|:
name|ChangeField
operator|.
name|class
operator|.
name|getFields
argument_list|()
control|)
block|{
if|if
condition|(
name|Modifier
operator|.
name|isPublic
argument_list|(
name|f
operator|.
name|getModifiers
argument_list|()
argument_list|)
operator|&&
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
name|FieldDef
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
name|ChangeData
operator|.
name|class
condition|)
block|{
try|try
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|FieldDef
argument_list|<
name|ChangeData
argument_list|,
name|?
argument_list|>
name|fd
init|=
operator|(
name|FieldDef
argument_list|<
name|ChangeData
argument_list|,
name|?
argument_list|>
operator|)
name|f
operator|.
name|get
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|fields
operator|.
name|put
argument_list|(
name|fd
operator|.
name|getName
argument_list|()
argument_list|,
name|fd
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ExceptionInInitializerError
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ExceptionInInitializerError
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
name|ExceptionInInitializerError
argument_list|(
literal|"non-ChangeData ChangeField: "
operator|+
name|f
argument_list|)
throw|;
block|}
block|}
block|}
if|if
condition|(
name|fields
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ExceptionInInitializerError
argument_list|(
literal|"no ChangeFields found"
argument_list|)
throw|;
block|}
name|ALL
operator|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|fields
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

