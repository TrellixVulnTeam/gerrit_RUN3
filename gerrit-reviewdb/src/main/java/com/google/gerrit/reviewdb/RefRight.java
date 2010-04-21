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
DECL|package|com.google.gerrit.reviewdb
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|reviewdb
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
name|CompoundKey
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
name|StringKey
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_comment
comment|/** Grant to use an {@link ApprovalCategory} in the scope of a git ref. */
end_comment

begin_class
DECL|class|RefRight
specifier|public
specifier|final
class|class
name|RefRight
block|{
comment|/** Pattern that matches all references in a project. */
DECL|field|ALL
specifier|public
specifier|static
specifier|final
name|String
name|ALL
init|=
literal|"refs/*"
decl_stmt|;
DECL|class|RefPattern
specifier|public
specifier|static
class|class
name|RefPattern
extends|extends
name|StringKey
argument_list|<
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|client
operator|.
name|Key
argument_list|<
name|?
argument_list|>
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
DECL|field|pattern
specifier|protected
name|String
name|pattern
decl_stmt|;
DECL|method|RefPattern ()
specifier|protected
name|RefPattern
parameter_list|()
block|{     }
DECL|method|RefPattern (final String pattern)
specifier|public
name|RefPattern
parameter_list|(
specifier|final
name|String
name|pattern
parameter_list|)
block|{
name|this
operator|.
name|pattern
operator|=
name|pattern
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get ()
specifier|public
name|String
name|get
parameter_list|()
block|{
return|return
name|pattern
return|;
block|}
annotation|@
name|Override
DECL|method|set (String pattern)
specifier|protected
name|void
name|set
parameter_list|(
name|String
name|pattern
parameter_list|)
block|{
name|this
operator|.
name|pattern
operator|=
name|pattern
expr_stmt|;
block|}
block|}
DECL|class|Key
specifier|public
specifier|static
class|class
name|Key
extends|extends
name|CompoundKey
argument_list|<
name|Project
operator|.
name|NameKey
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
DECL|field|projectName
specifier|protected
name|Project
operator|.
name|NameKey
name|projectName
decl_stmt|;
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|2
argument_list|)
DECL|field|refPattern
specifier|protected
name|RefPattern
name|refPattern
decl_stmt|;
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|3
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
literal|4
argument_list|)
DECL|field|groupId
specifier|protected
name|AccountGroup
operator|.
name|Id
name|groupId
decl_stmt|;
DECL|method|Key ()
specifier|protected
name|Key
parameter_list|()
block|{
name|projectName
operator|=
operator|new
name|Project
operator|.
name|NameKey
argument_list|()
expr_stmt|;
name|refPattern
operator|=
operator|new
name|RefPattern
argument_list|()
expr_stmt|;
name|categoryId
operator|=
operator|new
name|ApprovalCategory
operator|.
name|Id
argument_list|()
expr_stmt|;
name|groupId
operator|=
operator|new
name|AccountGroup
operator|.
name|Id
argument_list|()
expr_stmt|;
block|}
DECL|method|Key (final Project.NameKey projectName, final RefPattern refPattern, final ApprovalCategory.Id categoryId, final AccountGroup.Id groupId)
specifier|public
name|Key
parameter_list|(
specifier|final
name|Project
operator|.
name|NameKey
name|projectName
parameter_list|,
specifier|final
name|RefPattern
name|refPattern
parameter_list|,
specifier|final
name|ApprovalCategory
operator|.
name|Id
name|categoryId
parameter_list|,
specifier|final
name|AccountGroup
operator|.
name|Id
name|groupId
parameter_list|)
block|{
name|this
operator|.
name|projectName
operator|=
name|projectName
expr_stmt|;
name|this
operator|.
name|refPattern
operator|=
name|refPattern
expr_stmt|;
name|this
operator|.
name|categoryId
operator|=
name|categoryId
expr_stmt|;
name|this
operator|.
name|groupId
operator|=
name|groupId
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getParentKey ()
specifier|public
name|Project
operator|.
name|NameKey
name|getParentKey
parameter_list|()
block|{
return|return
name|projectName
return|;
block|}
DECL|method|getProjectNameKey ()
specifier|public
name|Project
operator|.
name|NameKey
name|getProjectNameKey
parameter_list|()
block|{
return|return
name|projectName
return|;
block|}
DECL|method|getRefPattern ()
specifier|public
name|String
name|getRefPattern
parameter_list|()
block|{
return|return
name|refPattern
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|members ()
specifier|public
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|client
operator|.
name|Key
argument_list|<
name|?
argument_list|>
index|[]
name|members
parameter_list|()
block|{
return|return
operator|new
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|client
operator|.
name|Key
argument_list|<
name|?
argument_list|>
index|[]
block|{
name|refPattern
operator|,
name|categoryId
operator|,
name|groupId
block|}
empty_stmt|;
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
name|Key
name|key
decl_stmt|;
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|2
argument_list|)
DECL|field|minValue
specifier|protected
name|short
name|minValue
decl_stmt|;
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|3
argument_list|)
DECL|field|maxValue
specifier|protected
name|short
name|maxValue
decl_stmt|;
DECL|method|RefRight ()
specifier|protected
name|RefRight
parameter_list|()
block|{   }
DECL|method|RefRight (RefRight.Key key)
specifier|public
name|RefRight
parameter_list|(
name|RefRight
operator|.
name|Key
name|key
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
block|}
DECL|method|getKey ()
specifier|public
name|RefRight
operator|.
name|Key
name|getKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
DECL|method|getRefPattern ()
specifier|public
name|String
name|getRefPattern
parameter_list|()
block|{
return|return
name|key
operator|.
name|refPattern
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|getProjectNameKey ()
specifier|public
name|Project
operator|.
name|NameKey
name|getProjectNameKey
parameter_list|()
block|{
return|return
name|getKey
argument_list|()
operator|.
name|getProjectNameKey
argument_list|()
return|;
block|}
DECL|method|getApprovalCategoryId ()
specifier|public
name|ApprovalCategory
operator|.
name|Id
name|getApprovalCategoryId
parameter_list|()
block|{
return|return
name|key
operator|.
name|categoryId
return|;
block|}
DECL|method|getAccountGroupId ()
specifier|public
name|AccountGroup
operator|.
name|Id
name|getAccountGroupId
parameter_list|()
block|{
return|return
name|key
operator|.
name|groupId
return|;
block|}
DECL|method|getMinValue ()
specifier|public
name|short
name|getMinValue
parameter_list|()
block|{
return|return
name|minValue
return|;
block|}
DECL|method|setMinValue (final short m)
specifier|public
name|void
name|setMinValue
parameter_list|(
specifier|final
name|short
name|m
parameter_list|)
block|{
name|minValue
operator|=
name|m
expr_stmt|;
block|}
DECL|method|getMaxValue ()
specifier|public
name|short
name|getMaxValue
parameter_list|()
block|{
return|return
name|maxValue
return|;
block|}
DECL|method|setMaxValue (final short m)
specifier|public
name|void
name|setMaxValue
parameter_list|(
specifier|final
name|short
name|m
parameter_list|)
block|{
name|maxValue
operator|=
name|m
expr_stmt|;
block|}
DECL|class|RefPatternOrder
specifier|private
specifier|static
class|class
name|RefPatternOrder
implements|implements
name|Comparator
argument_list|<
name|RefRight
argument_list|>
block|{
annotation|@
name|Override
DECL|method|compare (RefRight a, RefRight b)
specifier|public
name|int
name|compare
parameter_list|(
name|RefRight
name|a
parameter_list|,
name|RefRight
name|b
parameter_list|)
block|{
name|int
name|aLength
init|=
name|a
operator|.
name|getRefPattern
argument_list|()
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|bLength
init|=
name|b
operator|.
name|getRefPattern
argument_list|()
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|bLength
operator|-
name|aLength
operator|)
operator|==
literal|0
condition|)
block|{
return|return
name|a
operator|.
name|getApprovalCategoryId
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|compareTo
argument_list|(
name|b
operator|.
name|getApprovalCategoryId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
return|return
name|bLength
operator|-
name|aLength
return|;
block|}
block|}
DECL|field|REF_PATTERN_ORDER
specifier|public
specifier|static
specifier|final
name|RefPatternOrder
name|REF_PATTERN_ORDER
init|=
operator|new
name|RefPatternOrder
argument_list|()
decl_stmt|;
block|}
end_class

end_unit

