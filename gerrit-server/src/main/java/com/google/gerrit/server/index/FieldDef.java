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
name|base
operator|.
name|CharMatcher
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
name|Preconditions
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
name|TrackingFooters
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

begin_comment
comment|/**  * Definition of a field stored in the secondary index.  *  * @param<I> input type from which documents are created and search results are  *     returned.  * @param<T> type that should be extracted from the input object when converting  *     to an index document.  */
end_comment

begin_class
DECL|class|FieldDef
specifier|public
specifier|abstract
class|class
name|FieldDef
parameter_list|<
name|I
parameter_list|,
name|T
parameter_list|>
block|{
comment|/** Definition of a single (non-repeatable) field. */
DECL|class|Single
specifier|public
specifier|abstract
specifier|static
class|class
name|Single
parameter_list|<
name|I
parameter_list|,
name|T
parameter_list|>
extends|extends
name|FieldDef
argument_list|<
name|I
argument_list|,
name|T
argument_list|>
block|{
DECL|method|Single (String name, FieldType<T> type, boolean stored)
specifier|protected
name|Single
parameter_list|(
name|String
name|name
parameter_list|,
name|FieldType
argument_list|<
name|T
argument_list|>
name|type
parameter_list|,
name|boolean
name|stored
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|type
argument_list|,
name|stored
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isRepeatable ()
specifier|public
specifier|final
name|boolean
name|isRepeatable
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/** Definition of a repeatable field. */
DECL|class|Repeatable
specifier|public
specifier|abstract
specifier|static
class|class
name|Repeatable
parameter_list|<
name|I
parameter_list|,
name|T
parameter_list|>
extends|extends
name|FieldDef
argument_list|<
name|I
argument_list|,
name|Iterable
argument_list|<
name|T
argument_list|>
argument_list|>
block|{
DECL|method|Repeatable (String name, FieldType<T> type, boolean stored)
specifier|protected
name|Repeatable
parameter_list|(
name|String
name|name
parameter_list|,
name|FieldType
argument_list|<
name|T
argument_list|>
name|type
parameter_list|,
name|boolean
name|stored
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|type
argument_list|,
name|stored
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|type
operator|!=
name|FieldType
operator|.
name|INTEGER_RANGE
argument_list|,
literal|"Range queries against repeated fields are unsupported"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isRepeatable ()
specifier|public
specifier|final
name|boolean
name|isRepeatable
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
comment|/** Arguments needed to fill in missing data in the input object. */
DECL|class|FillArgs
specifier|public
specifier|static
class|class
name|FillArgs
block|{
DECL|field|trackingFooters
specifier|public
specifier|final
name|TrackingFooters
name|trackingFooters
decl_stmt|;
DECL|field|allowsDrafts
specifier|public
specifier|final
name|boolean
name|allowsDrafts
decl_stmt|;
annotation|@
name|Inject
DECL|method|FillArgs (TrackingFooters trackingFooters, @GerritServerConfig Config cfg)
name|FillArgs
parameter_list|(
name|TrackingFooters
name|trackingFooters
parameter_list|,
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|)
block|{
name|this
operator|.
name|trackingFooters
operator|=
name|trackingFooters
expr_stmt|;
name|this
operator|.
name|allowsDrafts
operator|=
name|cfg
operator|==
literal|null
condition|?
literal|true
else|:
name|cfg
operator|.
name|getBoolean
argument_list|(
literal|"change"
argument_list|,
literal|"allowDrafts"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|type
specifier|private
specifier|final
name|FieldType
argument_list|<
name|?
argument_list|>
name|type
decl_stmt|;
DECL|field|stored
specifier|private
specifier|final
name|boolean
name|stored
decl_stmt|;
DECL|method|FieldDef (String name, FieldType<?> type, boolean stored)
specifier|private
name|FieldDef
parameter_list|(
name|String
name|name
parameter_list|,
name|FieldType
argument_list|<
name|?
argument_list|>
name|type
parameter_list|,
name|boolean
name|stored
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|checkName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|stored
operator|=
name|stored
expr_stmt|;
block|}
DECL|method|checkName (String name)
specifier|private
specifier|static
name|String
name|checkName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|CharMatcher
name|m
init|=
name|CharMatcher
operator|.
name|anyOf
argument_list|(
literal|"abcdefghijklmnopqrstuvwxyz0123456789_"
argument_list|)
decl_stmt|;
name|checkArgument
argument_list|(
name|m
operator|.
name|matchesAllOf
argument_list|(
name|name
argument_list|)
argument_list|,
literal|"illegal field name: %s"
argument_list|,
name|name
argument_list|)
expr_stmt|;
return|return
name|name
return|;
block|}
comment|/** @return name of the field. */
DECL|method|getName ()
specifier|public
specifier|final
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**    * @return type of the field; for repeatable fields, the inner type, not the    *     iterable type.    */
DECL|method|getType ()
specifier|public
specifier|final
name|FieldType
argument_list|<
name|?
argument_list|>
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
comment|/** @return whether the field should be stored in the index. */
DECL|method|isStored ()
specifier|public
specifier|final
name|boolean
name|isStored
parameter_list|()
block|{
return|return
name|stored
return|;
block|}
comment|/**    * Get the field contents from the input object.    *    * @param input input object.    * @param args arbitrary arguments needed to fill in indexable fields of the    *     input object.    * @return the field value(s) to index.    *    * @throws OrmException    */
DECL|method|get (I input, FillArgs args)
specifier|public
specifier|abstract
name|T
name|get
parameter_list|(
name|I
name|input
parameter_list|,
name|FillArgs
name|args
parameter_list|)
throws|throws
name|OrmException
function_decl|;
comment|/** @return whether the field is repeatable. */
DECL|method|isRepeatable ()
specifier|public
specifier|abstract
name|boolean
name|isRepeatable
parameter_list|()
function_decl|;
block|}
end_class

end_unit

