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
name|Key
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

begin_comment
comment|/** Types of approvals that can be associated with a {@link Change}. */
end_comment

begin_class
DECL|class|ApprovalCategory
specifier|public
specifier|final
class|class
name|ApprovalCategory
block|{
comment|/** Id of the special "Submit" action (and category). */
DECL|field|SUBMIT_ID
specifier|public
specifier|static
specifier|final
name|String
name|SUBMIT_ID
init|=
literal|"SUBM"
decl_stmt|;
DECL|method|isSubmit (PatchSetApproval a)
specifier|public
specifier|static
name|boolean
name|isSubmit
parameter_list|(
name|PatchSetApproval
name|a
parameter_list|)
block|{
return|return
name|SUBMIT_ID
operator|.
name|equals
argument_list|(
name|a
operator|.
name|getCategoryId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
DECL|class|Id
specifier|public
specifier|static
class|class
name|Id
extends|extends
name|StringKey
argument_list|<
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
argument_list|,
name|length
operator|=
literal|4
argument_list|)
DECL|field|id
specifier|protected
name|String
name|id
decl_stmt|;
DECL|method|Id ()
specifier|protected
name|Id
parameter_list|()
block|{     }
DECL|method|Id (final String a)
specifier|public
name|Id
parameter_list|(
specifier|final
name|String
name|a
parameter_list|)
block|{
name|id
operator|=
name|a
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
name|id
return|;
block|}
annotation|@
name|Override
DECL|method|set (String newValue)
specifier|protected
name|void
name|set
parameter_list|(
name|String
name|newValue
parameter_list|)
block|{
name|id
operator|=
name|newValue
expr_stmt|;
block|}
block|}
comment|/** Internal short unique identifier for this category. */
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|1
argument_list|)
DECL|field|categoryId
specifier|protected
name|Id
name|categoryId
decl_stmt|;
comment|/** Unique name for this category, shown in the web interface to users. */
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|2
argument_list|,
name|length
operator|=
literal|20
argument_list|)
DECL|field|name
specifier|protected
name|String
name|name
decl_stmt|;
comment|/** Abbreviated form of {@link #name} for display in very wide tables. */
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|3
argument_list|,
name|length
operator|=
literal|4
argument_list|,
name|notNull
operator|=
literal|false
argument_list|)
DECL|field|abbreviatedName
specifier|protected
name|String
name|abbreviatedName
decl_stmt|;
comment|/** Order of this category within the Approvals table when presented. */
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|4
argument_list|)
DECL|field|position
specifier|protected
name|short
name|position
decl_stmt|;
comment|/** Identity of the function used to aggregate the category's value. */
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|5
argument_list|)
DECL|field|functionName
specifier|protected
name|String
name|functionName
decl_stmt|;
comment|/** If set, the minimum score is copied during patch set replacement. */
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|6
argument_list|)
DECL|field|copyMinScore
specifier|protected
name|boolean
name|copyMinScore
decl_stmt|;
comment|/** Computed name derived from {@link #name}. */
DECL|field|labelName
specifier|protected
name|String
name|labelName
decl_stmt|;
DECL|method|ApprovalCategory ()
specifier|protected
name|ApprovalCategory
parameter_list|()
block|{   }
DECL|method|ApprovalCategory (final ApprovalCategory.Id id, final String name)
specifier|public
name|ApprovalCategory
parameter_list|(
specifier|final
name|ApprovalCategory
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
name|categoryId
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|functionName
operator|=
literal|"MaxWithBlock"
expr_stmt|;
block|}
DECL|method|getId ()
specifier|public
name|ApprovalCategory
operator|.
name|Id
name|getId
parameter_list|()
block|{
return|return
name|categoryId
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
name|labelName
operator|=
literal|null
expr_stmt|;
block|}
comment|/** Clean version of {@link #getName()}, e.g. "Code Review" is "Code-Review". */
DECL|method|getLabelName ()
specifier|public
name|String
name|getLabelName
parameter_list|()
block|{
if|if
condition|(
name|labelName
operator|==
literal|null
condition|)
block|{
name|StringBuilder
name|r
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|name
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|name
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
literal|'0'
operator|<=
name|c
operator|&&
name|c
operator|<=
literal|'9'
operator|)
comment|//
operator|||
operator|(
literal|'a'
operator|<=
name|c
operator|&&
name|c
operator|<=
literal|'z'
operator|)
comment|//
operator|||
operator|(
literal|'A'
operator|<=
name|c
operator|&&
name|c
operator|<=
literal|'Z'
operator|)
comment|//
operator|||
operator|(
name|c
operator|==
literal|'-'
operator|)
condition|)
block|{
name|r
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|==
literal|' '
condition|)
block|{
name|r
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
block|}
block|}
name|labelName
operator|=
name|r
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
return|return
name|labelName
return|;
block|}
DECL|method|getAbbreviatedName ()
specifier|public
name|String
name|getAbbreviatedName
parameter_list|()
block|{
return|return
name|abbreviatedName
return|;
block|}
DECL|method|setAbbreviatedName (final String n)
specifier|public
name|void
name|setAbbreviatedName
parameter_list|(
specifier|final
name|String
name|n
parameter_list|)
block|{
name|abbreviatedName
operator|=
name|n
expr_stmt|;
block|}
DECL|method|getPosition ()
specifier|public
name|short
name|getPosition
parameter_list|()
block|{
return|return
name|position
return|;
block|}
DECL|method|setPosition (final short p)
specifier|public
name|void
name|setPosition
parameter_list|(
specifier|final
name|short
name|p
parameter_list|)
block|{
name|position
operator|=
name|p
expr_stmt|;
block|}
DECL|method|getFunctionName ()
specifier|public
name|String
name|getFunctionName
parameter_list|()
block|{
return|return
name|functionName
return|;
block|}
DECL|method|setFunctionName (final String name)
specifier|public
name|void
name|setFunctionName
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
block|{
name|functionName
operator|=
name|name
expr_stmt|;
block|}
DECL|method|isCopyMinScore ()
specifier|public
name|boolean
name|isCopyMinScore
parameter_list|()
block|{
return|return
name|copyMinScore
return|;
block|}
DECL|method|setCopyMinScore (final boolean copy)
specifier|public
name|void
name|setCopyMinScore
parameter_list|(
specifier|final
name|boolean
name|copy
parameter_list|)
block|{
name|copyMinScore
operator|=
name|copy
expr_stmt|;
block|}
block|}
end_class

end_unit

