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
name|checkState
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
name|annotations
operator|.
name|VisibleForTesting
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
name|Function
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
name|MoreObjects
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
name|Optional
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
name|Predicates
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
name|FluentIterable
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
name|gerrit
operator|.
name|server
operator|.
name|index
operator|.
name|FieldDef
operator|.
name|FillArgs
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
name|util
operator|.
name|Collections
import|;
end_import

begin_comment
comment|/** Specific version of a secondary index schema. */
end_comment

begin_class
DECL|class|Schema
specifier|public
class|class
name|Schema
parameter_list|<
name|T
parameter_list|>
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
name|Schema
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|class|Values
specifier|public
specifier|static
class|class
name|Values
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|field
specifier|private
specifier|final
name|FieldDef
argument_list|<
name|T
argument_list|,
name|?
argument_list|>
name|field
decl_stmt|;
DECL|field|values
specifier|private
specifier|final
name|Iterable
argument_list|<
name|?
argument_list|>
name|values
decl_stmt|;
DECL|method|Values (FieldDef<T, ?> field, Iterable<?> values)
specifier|private
name|Values
parameter_list|(
name|FieldDef
argument_list|<
name|T
argument_list|,
name|?
argument_list|>
name|field
parameter_list|,
name|Iterable
argument_list|<
name|?
argument_list|>
name|values
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
block|}
DECL|method|getField ()
specifier|public
name|FieldDef
argument_list|<
name|T
argument_list|,
name|?
argument_list|>
name|getField
parameter_list|()
block|{
return|return
name|field
return|;
block|}
DECL|method|getValues ()
specifier|public
name|Iterable
argument_list|<
name|?
argument_list|>
name|getValues
parameter_list|()
block|{
return|return
name|values
return|;
block|}
block|}
DECL|method|checkSame (FieldDef<T, ?> f1, FieldDef<T, ?> f2)
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|FieldDef
argument_list|<
name|T
argument_list|,
name|?
argument_list|>
name|checkSame
parameter_list|(
name|FieldDef
argument_list|<
name|T
argument_list|,
name|?
argument_list|>
name|f1
parameter_list|,
name|FieldDef
argument_list|<
name|T
argument_list|,
name|?
argument_list|>
name|f2
parameter_list|)
block|{
name|checkState
argument_list|(
name|f1
operator|==
name|f2
argument_list|,
literal|"Mismatched %s fields: %s != %s"
argument_list|,
name|f1
operator|.
name|getName
argument_list|()
argument_list|,
name|f1
argument_list|,
name|f2
argument_list|)
expr_stmt|;
return|return
name|f1
return|;
block|}
DECL|field|fields
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|FieldDef
argument_list|<
name|T
argument_list|,
name|?
argument_list|>
argument_list|>
name|fields
decl_stmt|;
DECL|field|storedFields
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|FieldDef
argument_list|<
name|T
argument_list|,
name|?
argument_list|>
argument_list|>
name|storedFields
decl_stmt|;
DECL|field|version
specifier|private
name|int
name|version
decl_stmt|;
DECL|method|Schema (Iterable<FieldDef<T, ?>> fields)
specifier|protected
name|Schema
parameter_list|(
name|Iterable
argument_list|<
name|FieldDef
argument_list|<
name|T
argument_list|,
name|?
argument_list|>
argument_list|>
name|fields
parameter_list|)
block|{
name|this
argument_list|(
literal|0
argument_list|,
name|fields
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|Schema (int version, Iterable<FieldDef<T, ?>> fields)
specifier|public
name|Schema
parameter_list|(
name|int
name|version
parameter_list|,
name|Iterable
argument_list|<
name|FieldDef
argument_list|<
name|T
argument_list|,
name|?
argument_list|>
argument_list|>
name|fields
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|FieldDef
argument_list|<
name|T
argument_list|,
name|?
argument_list|>
argument_list|>
name|b
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|FieldDef
argument_list|<
name|T
argument_list|,
name|?
argument_list|>
argument_list|>
name|sb
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|FieldDef
argument_list|<
name|T
argument_list|,
name|?
argument_list|>
name|f
range|:
name|fields
control|)
block|{
name|b
operator|.
name|put
argument_list|(
name|f
operator|.
name|getName
argument_list|()
argument_list|,
name|f
argument_list|)
expr_stmt|;
if|if
condition|(
name|f
operator|.
name|isStored
argument_list|()
condition|)
block|{
name|sb
operator|.
name|put
argument_list|(
name|f
operator|.
name|getName
argument_list|()
argument_list|,
name|f
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|fields
operator|=
name|b
operator|.
name|build
argument_list|()
expr_stmt|;
name|this
operator|.
name|storedFields
operator|=
name|sb
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
DECL|method|getVersion ()
specifier|public
specifier|final
name|int
name|getVersion
parameter_list|()
block|{
return|return
name|version
return|;
block|}
comment|/**    * Get all fields in this schema.    *<p>    * This is primarily useful for iteration. Most callers should prefer one    * of the helper methods {@link #getField(FieldDef, FieldDef...)} or {@link    * #hasField(FieldDef)} to looking up fields by name    *    * @return all fields in this schema indexed by name.    */
DECL|method|getFields ()
specifier|public
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|FieldDef
argument_list|<
name|T
argument_list|,
name|?
argument_list|>
argument_list|>
name|getFields
parameter_list|()
block|{
return|return
name|fields
return|;
block|}
comment|/**    * @return all fields in this schema where {@link FieldDef#isStored()} is    *     true.    */
DECL|method|getStoredFields ()
specifier|public
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|FieldDef
argument_list|<
name|T
argument_list|,
name|?
argument_list|>
argument_list|>
name|getStoredFields
parameter_list|()
block|{
return|return
name|storedFields
return|;
block|}
comment|/**    * Look up fields in this schema.    *    * @param first the preferred field to look up.    * @param rest additional fields to look up.    * @return the first field in the schema matching {@code first} or {@code    *     rest}, in order, or absent if no field matches.    */
annotation|@
name|SafeVarargs
DECL|method|getField (FieldDef<T, ?> first, FieldDef<T, ?>... rest)
specifier|public
specifier|final
name|Optional
argument_list|<
name|FieldDef
argument_list|<
name|T
argument_list|,
name|?
argument_list|>
argument_list|>
name|getField
parameter_list|(
name|FieldDef
argument_list|<
name|T
argument_list|,
name|?
argument_list|>
name|first
parameter_list|,
name|FieldDef
argument_list|<
name|T
argument_list|,
name|?
argument_list|>
modifier|...
name|rest
parameter_list|)
block|{
name|FieldDef
argument_list|<
name|T
argument_list|,
name|?
argument_list|>
name|field
init|=
name|fields
operator|.
name|get
argument_list|(
name|first
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
return|return
name|Optional
operator|.
expr|<
name|FieldDef
argument_list|<
name|T
argument_list|,
name|?
argument_list|>
operator|>
name|of
argument_list|(
name|checkSame
argument_list|(
name|field
argument_list|,
name|first
argument_list|)
argument_list|)
return|;
block|}
for|for
control|(
name|FieldDef
argument_list|<
name|T
argument_list|,
name|?
argument_list|>
name|f
range|:
name|rest
control|)
block|{
name|field
operator|=
name|fields
operator|.
name|get
argument_list|(
name|f
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
return|return
name|Optional
operator|.
expr|<
name|FieldDef
argument_list|<
name|T
argument_list|,
name|?
argument_list|>
operator|>
name|of
argument_list|(
name|checkSame
argument_list|(
name|field
argument_list|,
name|f
argument_list|)
argument_list|)
return|;
block|}
block|}
return|return
name|Optional
operator|.
name|absent
argument_list|()
return|;
block|}
comment|/**    * Check whether a field is present in this schema.    *    * @param field field to look up.    * @return whether the field is present.    */
DECL|method|hasField (FieldDef<T, ?> field)
specifier|public
specifier|final
name|boolean
name|hasField
parameter_list|(
name|FieldDef
argument_list|<
name|T
argument_list|,
name|?
argument_list|>
name|field
parameter_list|)
block|{
name|FieldDef
argument_list|<
name|T
argument_list|,
name|?
argument_list|>
name|f
init|=
name|fields
operator|.
name|get
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|checkSame
argument_list|(
name|f
argument_list|,
name|field
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**    * Build all fields in the schema from an input object.    *<p>    * Null values are omitted, as are fields which cause errors, which are    * logged.    *    * @param obj input object.    * @param fillArgs arguments for filling fields.    * @return all non-null field values from the object.    */
DECL|method|buildFields ( final T obj, final FillArgs fillArgs)
specifier|public
specifier|final
name|Iterable
argument_list|<
name|Values
argument_list|<
name|T
argument_list|>
argument_list|>
name|buildFields
parameter_list|(
specifier|final
name|T
name|obj
parameter_list|,
specifier|final
name|FillArgs
name|fillArgs
parameter_list|)
block|{
return|return
name|FluentIterable
operator|.
name|from
argument_list|(
name|fields
operator|.
name|values
argument_list|()
argument_list|)
operator|.
name|transform
argument_list|(
operator|new
name|Function
argument_list|<
name|FieldDef
argument_list|<
name|T
argument_list|,
name|?
argument_list|>
argument_list|,
name|Values
argument_list|<
name|T
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Values
argument_list|<
name|T
argument_list|>
name|apply
parameter_list|(
name|FieldDef
argument_list|<
name|T
argument_list|,
name|?
argument_list|>
name|f
parameter_list|)
block|{
name|Object
name|v
decl_stmt|;
try|try
block|{
name|v
operator|=
name|f
operator|.
name|get
argument_list|(
name|obj
argument_list|,
name|fillArgs
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"error getting field %s of %s"
argument_list|,
name|f
operator|.
name|getName
argument_list|()
argument_list|,
name|obj
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|f
operator|.
name|isRepeatable
argument_list|()
condition|)
block|{
return|return
operator|new
name|Values
argument_list|<>
argument_list|(
name|f
argument_list|,
operator|(
name|Iterable
argument_list|<
name|?
argument_list|>
operator|)
name|v
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|Values
argument_list|<>
argument_list|(
name|f
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|v
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
argument_list|)
operator|.
name|filter
argument_list|(
name|Predicates
operator|.
name|notNull
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|MoreObjects
operator|.
name|toStringHelper
argument_list|(
name|this
argument_list|)
operator|.
name|addValue
argument_list|(
name|fields
operator|.
name|keySet
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|setVersion (int version)
name|void
name|setVersion
parameter_list|(
name|int
name|version
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
block|}
block|}
end_class

end_unit

