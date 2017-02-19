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
name|LabelType
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
name|LabelValue
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
name|googlecode
operator|.
name|prolog_cafe
operator|.
name|exceptions
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
name|ListTerm
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
name|StructureTerm
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
name|SymbolTerm
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Obtain a list of label types from the server configuration.  *  *<p>Unifies to a Prolog list of: {@code label_type(Label, Fun, Min, Max)} where:  *  *<ul>  *<li>{@code Label} - the newer style label name  *<li>{@code Fun} - legacy function name  *<li>{@code Min, Max} - the smallest and largest configured values.  *</ul>  */
end_comment

begin_class
DECL|class|PRED_get_legacy_label_types_1
class|class
name|PRED_get_legacy_label_types_1
extends|extends
name|Predicate
operator|.
name|P1
block|{
DECL|field|NONE
specifier|private
specifier|static
specifier|final
name|SymbolTerm
name|NONE
init|=
name|SymbolTerm
operator|.
name|intern
argument_list|(
literal|"none"
argument_list|)
decl_stmt|;
DECL|method|PRED_get_legacy_label_types_1 (Term a1, Operation n)
name|PRED_get_legacy_label_types_1
parameter_list|(
name|Term
name|a1
parameter_list|,
name|Operation
name|n
parameter_list|)
block|{
name|arg1
operator|=
name|a1
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
name|List
argument_list|<
name|LabelType
argument_list|>
name|list
init|=
name|StoredValues
operator|.
name|CHANGE_DATA
operator|.
name|get
argument_list|(
name|engine
argument_list|)
operator|.
name|getLabelTypes
argument_list|()
operator|.
name|getLabelTypes
argument_list|()
decl_stmt|;
name|Term
name|head
init|=
name|Prolog
operator|.
name|Nil
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
name|list
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
literal|0
operator|<=
name|idx
condition|;
name|idx
operator|--
control|)
block|{
name|head
operator|=
operator|new
name|ListTerm
argument_list|(
name|export
argument_list|(
name|list
operator|.
name|get
argument_list|(
name|idx
argument_list|)
argument_list|)
argument_list|,
name|head
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|a1
operator|.
name|unify
argument_list|(
name|head
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
DECL|field|symLabelType
specifier|static
specifier|final
name|SymbolTerm
name|symLabelType
init|=
name|SymbolTerm
operator|.
name|intern
argument_list|(
literal|"label_type"
argument_list|,
literal|4
argument_list|)
decl_stmt|;
DECL|method|export (LabelType type)
specifier|static
name|Term
name|export
parameter_list|(
name|LabelType
name|type
parameter_list|)
block|{
name|LabelValue
name|min
init|=
name|type
operator|.
name|getMin
argument_list|()
decl_stmt|;
name|LabelValue
name|max
init|=
name|type
operator|.
name|getMax
argument_list|()
decl_stmt|;
return|return
operator|new
name|StructureTerm
argument_list|(
name|symLabelType
argument_list|,
name|SymbolTerm
operator|.
name|intern
argument_list|(
name|type
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|SymbolTerm
operator|.
name|intern
argument_list|(
name|type
operator|.
name|getFunctionName
argument_list|()
argument_list|)
argument_list|,
name|min
operator|!=
literal|null
condition|?
operator|new
name|IntegerTerm
argument_list|(
name|min
operator|.
name|getValue
argument_list|()
argument_list|)
else|:
name|NONE
argument_list|,
name|max
operator|!=
literal|null
condition|?
operator|new
name|IntegerTerm
argument_list|(
name|max
operator|.
name|getValue
argument_list|()
argument_list|)
else|:
name|NONE
argument_list|)
return|;
block|}
block|}
end_class

end_unit

