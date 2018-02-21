begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2017 The Android Open Source Project
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
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|common
operator|.
name|Nullable
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_comment
comment|/**  * Functions for determining submittability based on label votes.  *  *<p>Only describes built-in label functions. Admins can extend the logic arbitrarily using Prolog  * rules, in which case the choice of function in the project config is ignored.  *  *<p>Function semantics are documented in {@code config-labels.txt}, and actual behavior is  * implemented in Prolog in {@code gerrit_common.pl}.  */
end_comment

begin_enum
DECL|enum|LabelFunction
specifier|public
enum|enum
name|LabelFunction
block|{
DECL|enumConstant|MAX_WITH_BLOCK
name|MAX_WITH_BLOCK
argument_list|(
literal|"MaxWithBlock"
argument_list|,
literal|true
argument_list|)
block|,
DECL|enumConstant|ANY_WITH_BLOCK
name|ANY_WITH_BLOCK
argument_list|(
literal|"AnyWithBlock"
argument_list|,
literal|true
argument_list|)
block|,
DECL|enumConstant|MAX_NO_BLOCK
name|MAX_NO_BLOCK
argument_list|(
literal|"MaxNoBlock"
argument_list|,
literal|false
argument_list|)
block|,
DECL|enumConstant|NO_BLOCK
name|NO_BLOCK
argument_list|(
literal|"NoBlock"
argument_list|,
literal|false
argument_list|)
block|,
DECL|enumConstant|NO_OP
name|NO_OP
argument_list|(
literal|"NoOp"
argument_list|,
literal|false
argument_list|)
block|,
DECL|enumConstant|PATCH_SET_LOCK
name|PATCH_SET_LOCK
argument_list|(
literal|"PatchSetLock"
argument_list|,
literal|false
argument_list|)
block|;
DECL|field|ALL
specifier|public
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|LabelFunction
argument_list|>
name|ALL
decl_stmt|;
static|static
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|LabelFunction
argument_list|>
name|all
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|LabelFunction
name|f
range|:
name|values
argument_list|()
control|)
block|{
name|all
operator|.
name|put
argument_list|(
name|f
operator|.
name|getFunctionName
argument_list|()
argument_list|,
name|f
argument_list|)
expr_stmt|;
block|}
name|ALL
operator|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|all
argument_list|)
expr_stmt|;
block|}
DECL|method|parse (@ullable String str)
specifier|public
specifier|static
name|Optional
argument_list|<
name|LabelFunction
argument_list|>
name|parse
parameter_list|(
annotation|@
name|Nullable
name|String
name|str
parameter_list|)
block|{
return|return
name|Optional
operator|.
name|ofNullable
argument_list|(
name|ALL
operator|.
name|get
argument_list|(
name|str
argument_list|)
argument_list|)
return|;
block|}
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|isBlock
specifier|private
specifier|final
name|boolean
name|isBlock
decl_stmt|;
DECL|method|LabelFunction (String name, boolean isBlock)
specifier|private
name|LabelFunction
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|isBlock
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|isBlock
operator|=
name|isBlock
expr_stmt|;
block|}
comment|/** The function name as defined in documentation and {@code project.config}. */
DECL|method|getFunctionName ()
specifier|public
name|String
name|getFunctionName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/** Whether the label is a "block" label, meaning a minimum vote will prevent submission. */
DECL|method|isBlock ()
specifier|public
name|boolean
name|isBlock
parameter_list|()
block|{
return|return
name|isBlock
return|;
block|}
block|}
end_enum

end_unit

