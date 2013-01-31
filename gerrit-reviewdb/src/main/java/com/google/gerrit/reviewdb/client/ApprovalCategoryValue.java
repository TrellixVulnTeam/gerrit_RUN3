begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2008 The Android Open Source Project
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
DECL|package|com.google.gerrit.reviewdb.client
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|reviewdb
operator|.
name|client
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|client
operator|.
name|Column
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
name|client
operator|.
name|ShortKey
import|;
end_import

begin_comment
comment|/** Valid value for a {@link ApprovalCategory}. */
end_comment

begin_class
DECL|class|ApprovalCategoryValue
specifier|public
specifier|final
class|class
name|ApprovalCategoryValue
block|{
DECL|class|Id
specifier|public
specifier|static
class|class
name|Id
extends|extends
name|ShortKey
argument_list|<
name|ApprovalCategory
operator|.
name|Id
argument_list|>
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|1
argument_list|)
DECL|field|categoryId
specifier|protected
name|ApprovalCategory
operator|.
name|Id
name|categoryId
decl_stmt|;
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|2
argument_list|)
DECL|field|value
specifier|protected
name|short
name|value
decl_stmt|;
DECL|method|Id ()
specifier|protected
name|Id
parameter_list|()
block|{
name|categoryId
operator|=
operator|new
name|ApprovalCategory
operator|.
name|Id
argument_list|()
expr_stmt|;
block|}
DECL|method|Id (final ApprovalCategory.Id cat, final short v)
specifier|public
name|Id
parameter_list|(
specifier|final
name|ApprovalCategory
operator|.
name|Id
name|cat
parameter_list|,
specifier|final
name|short
name|v
parameter_list|)
block|{
name|categoryId
operator|=
name|cat
expr_stmt|;
name|value
operator|=
name|v
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getParentKey ()
specifier|public
name|ApprovalCategory
operator|.
name|Id
name|getParentKey
parameter_list|()
block|{
return|return
name|categoryId
return|;
block|}
annotation|@
name|Override
DECL|method|get ()
specifier|public
name|short
name|get
parameter_list|()
block|{
return|return
name|value
return|;
block|}
annotation|@
name|Override
DECL|method|set (short newValue)
specifier|protected
name|void
name|set
parameter_list|(
name|short
name|newValue
parameter_list|)
block|{
name|value
operator|=
name|newValue
expr_stmt|;
block|}
block|}
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|1
argument_list|,
name|name
operator|=
name|Column
operator|.
name|NONE
argument_list|)
DECL|field|key
specifier|protected
name|Id
name|key
decl_stmt|;
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|2
argument_list|,
name|length
operator|=
literal|50
argument_list|)
DECL|field|name
specifier|protected
name|String
name|name
decl_stmt|;
DECL|method|ApprovalCategoryValue ()
specifier|protected
name|ApprovalCategoryValue
parameter_list|()
block|{   }
DECL|method|ApprovalCategoryValue (final ApprovalCategoryValue.Id id, final String name)
specifier|public
name|ApprovalCategoryValue
parameter_list|(
specifier|final
name|ApprovalCategoryValue
operator|.
name|Id
name|id
parameter_list|,
specifier|final
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
DECL|method|getId ()
specifier|public
name|ApprovalCategoryValue
operator|.
name|Id
name|getId
parameter_list|()
block|{
return|return
name|key
return|;
block|}
DECL|method|getCategoryId ()
specifier|public
name|ApprovalCategory
operator|.
name|Id
name|getCategoryId
parameter_list|()
block|{
return|return
name|key
operator|.
name|categoryId
return|;
block|}
DECL|method|getValue ()
specifier|public
name|short
name|getValue
parameter_list|()
block|{
return|return
name|key
operator|.
name|value
return|;
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
DECL|method|setName (final String n)
specifier|public
name|void
name|setName
parameter_list|(
specifier|final
name|String
name|n
parameter_list|)
block|{
name|name
operator|=
name|n
expr_stmt|;
block|}
DECL|method|formatValue (short value)
specifier|public
specifier|static
name|String
name|formatValue
parameter_list|(
name|short
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|<
literal|0
condition|)
block|{
return|return
name|Short
operator|.
name|toString
argument_list|(
name|value
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|==
literal|0
condition|)
block|{
return|return
literal|" 0"
return|;
block|}
else|else
block|{
return|return
literal|"+"
operator|+
name|Short
operator|.
name|toString
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
DECL|method|formatValue ()
specifier|public
name|String
name|formatValue
parameter_list|()
block|{
return|return
name|formatValue
argument_list|(
name|getValue
argument_list|()
argument_list|)
return|;
block|}
DECL|method|format (String name, short value)
specifier|public
specifier|static
name|String
name|format
parameter_list|(
name|String
name|name
parameter_list|,
name|short
name|value
parameter_list|)
block|{
return|return
operator|new
name|StringBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|formatValue
argument_list|(
name|value
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
operator|.
name|append
argument_list|(
name|name
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|format ()
specifier|public
name|String
name|format
parameter_list|()
block|{
return|return
name|format
argument_list|(
name|getName
argument_list|()
argument_list|,
name|getValue
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

