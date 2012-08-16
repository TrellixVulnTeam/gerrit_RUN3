begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2011 The Android Open Source Project
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
DECL|package|gerrit
package|package
name|gerrit
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
name|data
operator|.
name|Permission
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
name|common
operator|.
name|data
operator|.
name|PermissionRange
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
name|rules
operator|.
name|StoredValues
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
name|CurrentUser
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
name|project
operator|.
name|ChangeControl
import|;
end_import

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|prolog_cafe
operator|.
name|lang
operator|.
name|IllegalTypeException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|prolog_cafe
operator|.
name|lang
operator|.
name|IntegerTerm
import|;
end_import

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|prolog_cafe
operator|.
name|lang
operator|.
name|JavaObjectTerm
import|;
end_import

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|prolog_cafe
operator|.
name|lang
operator|.
name|Operation
import|;
end_import

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|prolog_cafe
operator|.
name|lang
operator|.
name|PInstantiationException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|prolog_cafe
operator|.
name|lang
operator|.
name|Predicate
import|;
end_import

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|prolog_cafe
operator|.
name|lang
operator|.
name|Prolog
import|;
end_import

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|prolog_cafe
operator|.
name|lang
operator|.
name|PrologException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|prolog_cafe
operator|.
name|lang
operator|.
name|Term
import|;
end_import

begin_comment
comment|/**  * Resolves the valid range for a label on a CurrentUser.  *  *<pre>  *   '$user_label_range'(+Label, +CurrentUser, -Min, -Max)  *</pre>  */
end_comment

begin_class
DECL|class|PRED__user_label_range_4
class|class
name|PRED__user_label_range_4
extends|extends
name|Predicate
operator|.
name|P4
block|{
DECL|method|PRED__user_label_range_4 (Term a1, Term a2, Term a3, Term a4, Operation n)
name|PRED__user_label_range_4
parameter_list|(
name|Term
name|a1
parameter_list|,
name|Term
name|a2
parameter_list|,
name|Term
name|a3
parameter_list|,
name|Term
name|a4
parameter_list|,
name|Operation
name|n
parameter_list|)
block|{
name|arg1
operator|=
name|a1
expr_stmt|;
name|arg2
operator|=
name|a2
expr_stmt|;
name|arg3
operator|=
name|a3
expr_stmt|;
name|arg4
operator|=
name|a4
expr_stmt|;
name|cont
operator|=
name|n
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|exec (Prolog engine)
specifier|public
name|Operation
name|exec
parameter_list|(
name|Prolog
name|engine
parameter_list|)
throws|throws
name|PrologException
block|{
name|engine
operator|.
name|setB0
argument_list|()
expr_stmt|;
name|Term
name|a1
init|=
name|arg1
operator|.
name|dereference
argument_list|()
decl_stmt|;
name|Term
name|a2
init|=
name|arg2
operator|.
name|dereference
argument_list|()
decl_stmt|;
name|Term
name|a3
init|=
name|arg3
operator|.
name|dereference
argument_list|()
decl_stmt|;
name|Term
name|a4
init|=
name|arg4
operator|.
name|dereference
argument_list|()
decl_stmt|;
if|if
condition|(
name|a1
operator|.
name|isVariable
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|PInstantiationException
argument_list|(
name|this
argument_list|,
literal|1
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|a1
operator|.
name|isSymbol
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalTypeException
argument_list|(
name|this
argument_list|,
literal|1
argument_list|,
literal|"atom"
argument_list|,
name|a1
argument_list|)
throw|;
block|}
name|String
name|label
init|=
name|a1
operator|.
name|name
argument_list|()
decl_stmt|;
if|if
condition|(
name|a2
operator|.
name|isVariable
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|PInstantiationException
argument_list|(
name|this
argument_list|,
literal|2
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|a2
operator|.
name|isJavaObject
argument_list|()
operator|||
operator|!
name|a2
operator|.
name|convertible
argument_list|(
name|CurrentUser
operator|.
name|class
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalTypeException
argument_list|(
name|this
argument_list|,
literal|2
argument_list|,
literal|"CurrentUser)"
argument_list|,
name|a2
argument_list|)
throw|;
block|}
name|CurrentUser
name|user
init|=
call|(
name|CurrentUser
call|)
argument_list|(
operator|(
name|JavaObjectTerm
operator|)
name|a2
argument_list|)
operator|.
name|object
argument_list|()
decl_stmt|;
name|ChangeControl
name|ctl
init|=
name|StoredValues
operator|.
name|CHANGE_CONTROL
operator|.
name|get
argument_list|(
name|engine
argument_list|)
operator|.
name|forUser
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|PermissionRange
name|range
init|=
name|ctl
operator|.
name|getRange
argument_list|(
name|Permission
operator|.
name|LABEL
operator|+
name|label
argument_list|)
decl_stmt|;
if|if
condition|(
name|range
operator|==
literal|null
condition|)
block|{
return|return
name|engine
operator|.
name|fail
argument_list|()
return|;
block|}
name|IntegerTerm
name|min
init|=
operator|new
name|IntegerTerm
argument_list|(
name|range
operator|.
name|getMin
argument_list|()
argument_list|)
decl_stmt|;
name|IntegerTerm
name|max
init|=
operator|new
name|IntegerTerm
argument_list|(
name|range
operator|.
name|getMax
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|a3
operator|.
name|unify
argument_list|(
name|min
argument_list|,
name|engine
operator|.
name|trail
argument_list|)
condition|)
block|{
return|return
name|engine
operator|.
name|fail
argument_list|()
return|;
block|}
if|if
condition|(
operator|!
name|a4
operator|.
name|unify
argument_list|(
name|max
argument_list|,
name|engine
operator|.
name|trail
argument_list|)
condition|)
block|{
return|return
name|engine
operator|.
name|fail
argument_list|()
return|;
block|}
return|return
name|cont
return|;
block|}
block|}
end_class

end_unit

