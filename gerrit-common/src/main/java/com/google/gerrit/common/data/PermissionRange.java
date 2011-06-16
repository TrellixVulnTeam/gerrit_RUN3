begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2010 The Android Open Source Project
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
DECL|package|com.google.gerrit.common.data
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|common
operator|.
name|data
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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

begin_class
DECL|class|PermissionRange
specifier|public
class|class
name|PermissionRange
implements|implements
name|Comparable
argument_list|<
name|PermissionRange
argument_list|>
block|{
DECL|class|WithDefaults
specifier|public
specifier|static
class|class
name|WithDefaults
extends|extends
name|PermissionRange
block|{
DECL|field|defaultMin
specifier|protected
name|int
name|defaultMin
decl_stmt|;
DECL|field|defaultMax
specifier|protected
name|int
name|defaultMax
decl_stmt|;
DECL|method|WithDefaults ()
specifier|protected
name|WithDefaults
parameter_list|()
block|{     }
DECL|method|WithDefaults (String name, int min, int max, int defMin, int defMax)
specifier|public
name|WithDefaults
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|,
name|int
name|defMin
parameter_list|,
name|int
name|defMax
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|min
argument_list|,
name|max
argument_list|)
expr_stmt|;
name|setDefaultRange
argument_list|(
name|defMin
argument_list|,
name|defMax
argument_list|)
expr_stmt|;
block|}
DECL|method|getDefaultMin ()
specifier|public
name|int
name|getDefaultMin
parameter_list|()
block|{
return|return
name|defaultMin
return|;
block|}
DECL|method|getDefaultMax ()
specifier|public
name|int
name|getDefaultMax
parameter_list|()
block|{
return|return
name|defaultMax
return|;
block|}
DECL|method|setDefaultRange (int min, int max)
specifier|public
name|void
name|setDefaultRange
parameter_list|(
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|)
block|{
name|defaultMin
operator|=
name|min
expr_stmt|;
name|defaultMax
operator|=
name|max
expr_stmt|;
block|}
comment|/** @return all values between {@link #getMin()} and {@link #getMax()} */
DECL|method|getValuesAsList ()
specifier|public
name|List
argument_list|<
name|Integer
argument_list|>
name|getValuesAsList
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|Integer
argument_list|>
name|r
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|getRangeSize
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|min
init|;
name|i
operator|<=
name|max
condition|;
name|i
operator|++
control|)
block|{
name|r
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
comment|/** @return number of values between {@link #getMin()} and {@link #getMax()} */
DECL|method|getRangeSize ()
specifier|public
name|int
name|getRangeSize
parameter_list|()
block|{
return|return
name|max
operator|-
name|min
return|;
block|}
block|}
DECL|field|name
specifier|protected
name|String
name|name
decl_stmt|;
DECL|field|min
specifier|protected
name|int
name|min
decl_stmt|;
DECL|field|max
specifier|protected
name|int
name|max
decl_stmt|;
DECL|method|PermissionRange ()
specifier|protected
name|PermissionRange
parameter_list|()
block|{   }
DECL|method|PermissionRange (String name, int min, int max)
specifier|public
name|PermissionRange
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
if|if
condition|(
name|min
operator|<=
name|max
condition|)
block|{
name|this
operator|.
name|min
operator|=
name|min
expr_stmt|;
name|this
operator|.
name|max
operator|=
name|max
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|min
operator|=
name|max
expr_stmt|;
name|this
operator|.
name|max
operator|=
name|min
expr_stmt|;
block|}
block|}
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|isLabel ()
specifier|public
name|boolean
name|isLabel
parameter_list|()
block|{
return|return
name|Permission
operator|.
name|isLabel
argument_list|(
name|getName
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getLabel ()
specifier|public
name|String
name|getLabel
parameter_list|()
block|{
return|return
name|isLabel
argument_list|()
condition|?
name|getName
argument_list|()
operator|.
name|substring
argument_list|(
name|Permission
operator|.
name|LABEL
operator|.
name|length
argument_list|()
argument_list|)
else|:
literal|null
return|;
block|}
DECL|method|getMin ()
specifier|public
name|int
name|getMin
parameter_list|()
block|{
return|return
name|min
return|;
block|}
DECL|method|getMax ()
specifier|public
name|int
name|getMax
parameter_list|()
block|{
return|return
name|max
return|;
block|}
comment|/** True if the value is within the range. */
DECL|method|contains (int value)
specifier|public
name|boolean
name|contains
parameter_list|(
name|int
name|value
parameter_list|)
block|{
return|return
name|getMin
argument_list|()
operator|<=
name|value
operator|&&
name|value
operator|<=
name|getMax
argument_list|()
return|;
block|}
comment|/** Normalize the value to fit within the bounds of the range. */
DECL|method|squash (int value)
specifier|public
name|int
name|squash
parameter_list|(
name|int
name|value
parameter_list|)
block|{
return|return
name|Math
operator|.
name|min
argument_list|(
name|Math
operator|.
name|max
argument_list|(
name|getMin
argument_list|()
argument_list|,
name|value
argument_list|)
argument_list|,
name|getMax
argument_list|()
argument_list|)
return|;
block|}
comment|/** True both {@link #getMin()} and {@link #getMax()} are 0. */
DECL|method|isEmpty ()
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|getMin
argument_list|()
operator|==
literal|0
operator|&&
name|getMax
argument_list|()
operator|==
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo (PermissionRange o)
specifier|public
name|int
name|compareTo
parameter_list|(
name|PermissionRange
name|o
parameter_list|)
block|{
return|return
name|getName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|o
operator|.
name|getName
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
name|StringBuilder
name|r
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|getMin
argument_list|()
operator|<
literal|0
operator|&&
name|getMax
argument_list|()
operator|==
literal|0
condition|)
block|{
name|r
operator|.
name|append
argument_list|(
name|getMin
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|getMin
argument_list|()
operator|!=
name|getMax
argument_list|()
condition|)
block|{
if|if
condition|(
literal|0
operator|<=
name|getMin
argument_list|()
condition|)
name|r
operator|.
name|append
argument_list|(
literal|'+'
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|getMin
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|".."
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|0
operator|<=
name|getMax
argument_list|()
condition|)
name|r
operator|.
name|append
argument_list|(
literal|'+'
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|getMax
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
return|return
name|r
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

