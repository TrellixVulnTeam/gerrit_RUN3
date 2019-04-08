begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright 2008 Google Inc.
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
DECL|package|com.google.gwtorm.client
package|package
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|client
package|;
end_package

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
comment|/**  * Abstract key type using a single integer value.  *  *<p>Applications should subclass this type to create their own entity-specific key classes.  *  * @param<P> the parent key type. Use {@link Key} if no parent key is needed.  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
DECL|class|IntKey
specifier|public
specifier|abstract
class|class
name|IntKey
parameter_list|<
name|P
extends|extends
name|Key
parameter_list|<
name|?
parameter_list|>
parameter_list|>
implements|implements
name|Key
argument_list|<
name|P
argument_list|>
implements|,
name|Serializable
block|{
comment|/** @return id of the entity instance. */
DECL|method|get ()
specifier|public
specifier|abstract
name|int
name|get
parameter_list|()
function_decl|;
comment|/** @param newValue the new value of this key. */
DECL|method|set (int newValue)
specifier|protected
specifier|abstract
name|void
name|set
parameter_list|(
name|int
name|newValue
parameter_list|)
function_decl|;
comment|/** @return the parent key instance; null if this is a root level key. */
annotation|@
name|Override
DECL|method|getParentKey ()
specifier|public
name|P
name|getParentKey
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|hc
init|=
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|getParentKey
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|hc
operator|*=
literal|31
expr_stmt|;
name|hc
operator|+=
name|getParentKey
argument_list|()
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
return|return
name|hc
return|;
block|}
annotation|@
name|Override
DECL|method|equals (final Object b)
specifier|public
name|boolean
name|equals
parameter_list|(
specifier|final
name|Object
name|b
parameter_list|)
block|{
if|if
condition|(
name|b
operator|==
literal|null
operator|||
name|b
operator|.
name|getClass
argument_list|()
operator|!=
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|IntKey
argument_list|<
name|P
argument_list|>
name|q
init|=
name|cast
argument_list|(
name|b
argument_list|)
decl_stmt|;
return|return
name|get
argument_list|()
operator|==
name|q
operator|.
name|get
argument_list|()
operator|&&
name|KeyUtil
operator|.
name|eq
argument_list|(
name|getParentKey
argument_list|()
argument_list|,
name|q
operator|.
name|getParentKey
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
specifier|final
name|StringBuffer
name|r
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
if|if
condition|(
name|getParentKey
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|r
operator|.
name|append
argument_list|(
name|getParentKey
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
name|r
operator|.
name|append
argument_list|(
name|get
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|r
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|fromString (final String in)
specifier|public
name|void
name|fromString
parameter_list|(
specifier|final
name|String
name|in
parameter_list|)
block|{
name|set
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|KeyUtil
operator|.
name|parseFromString
argument_list|(
name|getParentKey
argument_list|()
argument_list|,
name|in
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|cast (final Object b)
specifier|private
specifier|static
parameter_list|<
name|A
extends|extends
name|Key
argument_list|<
name|?
argument_list|>
parameter_list|>
name|IntKey
argument_list|<
name|A
argument_list|>
name|cast
parameter_list|(
specifier|final
name|Object
name|b
parameter_list|)
block|{
return|return
operator|(
name|IntKey
argument_list|<
name|A
argument_list|>
operator|)
name|b
return|;
block|}
block|}
end_class

end_unit

