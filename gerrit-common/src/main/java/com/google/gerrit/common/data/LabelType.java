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
name|reviewdb
operator|.
name|client
operator|.
name|PatchSetApproval
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
name|reviewdb
operator|.
name|client
operator|.
name|PatchSetApproval
operator|.
name|LabelId
import|;
end_import

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
name|Collections
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_class
DECL|class|LabelType
specifier|public
class|class
name|LabelType
block|{
DECL|method|withDefaultValues (String name)
specifier|public
specifier|static
name|LabelType
name|withDefaultValues
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|checkName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|LabelValue
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<
name|LabelValue
argument_list|>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|values
operator|.
name|add
argument_list|(
operator|new
name|LabelValue
argument_list|(
operator|(
name|short
operator|)
literal|0
argument_list|,
literal|"Rejected"
argument_list|)
argument_list|)
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
operator|new
name|LabelValue
argument_list|(
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|"Approved"
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|LabelType
argument_list|(
name|name
argument_list|,
name|values
argument_list|)
return|;
block|}
DECL|method|checkId (String id)
specifier|private
specifier|static
name|String
name|checkId
parameter_list|(
name|String
name|id
parameter_list|)
block|{
if|if
condition|(
literal|"SUBM"
operator|.
name|equals
argument_list|(
name|id
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Reserved label ID \""
operator|+
name|id
operator|+
literal|"\""
argument_list|)
throw|;
block|}
if|if
condition|(
name|id
operator|!=
literal|null
operator|&&
name|id
operator|.
name|length
argument_list|()
operator|>
literal|4
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal label ID \""
operator|+
name|id
operator|+
literal|"\""
argument_list|)
throw|;
block|}
return|return
name|id
return|;
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
if|if
condition|(
literal|"SUBM"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Reserved label name \""
operator|+
name|name
operator|+
literal|"\""
argument_list|)
throw|;
block|}
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
operator|!
operator|(
operator|(
name|c
operator|>=
literal|'a'
operator|&&
name|c
operator|<=
literal|'z'
operator|)
operator|||
operator|(
name|c
operator|>=
literal|'A'
operator|&&
name|c
operator|<=
literal|'Z'
operator|)
operator|||
operator|(
name|c
operator|>=
literal|'0'
operator|&&
name|c
operator|<=
literal|'9'
operator|)
operator|||
name|c
operator|==
literal|'-'
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal label name \""
operator|+
name|name
operator|+
literal|"\""
argument_list|)
throw|;
block|}
block|}
return|return
name|name
return|;
block|}
DECL|method|defaultAbbreviation (String name)
specifier|public
specifier|static
name|String
name|defaultAbbreviation
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|StringBuilder
name|abbr
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
name|c
operator|>=
literal|'A'
operator|&&
name|c
operator|<=
literal|'Z'
condition|)
block|{
name|abbr
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|abbr
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|abbr
operator|.
name|append
argument_list|(
name|Character
operator|.
name|toUpperCase
argument_list|(
name|name
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|abbr
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|sortValues (List<LabelValue> values)
specifier|private
specifier|static
name|List
argument_list|<
name|LabelValue
argument_list|>
name|sortValues
parameter_list|(
name|List
argument_list|<
name|LabelValue
argument_list|>
name|values
parameter_list|)
block|{
name|values
operator|=
operator|new
name|ArrayList
argument_list|<
name|LabelValue
argument_list|>
argument_list|(
name|values
argument_list|)
expr_stmt|;
if|if
condition|(
name|values
operator|.
name|size
argument_list|()
operator|<=
literal|1
condition|)
block|{
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|values
argument_list|)
return|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|values
argument_list|,
operator|new
name|Comparator
argument_list|<
name|LabelValue
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|LabelValue
name|o1
parameter_list|,
name|LabelValue
name|o2
parameter_list|)
block|{
return|return
name|o1
operator|.
name|getValue
argument_list|()
operator|-
name|o2
operator|.
name|getValue
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|short
name|min
init|=
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|short
name|max
init|=
name|values
operator|.
name|get
argument_list|(
name|values
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|short
name|v
init|=
name|min
decl_stmt|;
name|short
name|i
init|=
literal|0
decl_stmt|;
name|List
argument_list|<
name|LabelValue
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|LabelValue
argument_list|>
argument_list|(
name|max
operator|-
name|min
operator|+
literal|1
argument_list|)
decl_stmt|;
comment|// Fill in any missing values with empty text.
while|while
condition|(
name|i
operator|<
name|values
operator|.
name|size
argument_list|()
condition|)
block|{
while|while
condition|(
name|v
operator|<
name|values
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getValue
argument_list|()
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|LabelValue
argument_list|(
name|v
operator|++
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|v
operator|++
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|values
operator|.
name|get
argument_list|(
name|i
operator|++
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|result
argument_list|)
return|;
block|}
DECL|field|name
specifier|protected
name|String
name|name
decl_stmt|;
DECL|field|id
specifier|protected
name|String
name|id
decl_stmt|;
DECL|field|abbreviatedName
specifier|protected
name|String
name|abbreviatedName
decl_stmt|;
DECL|field|functionName
specifier|protected
name|String
name|functionName
decl_stmt|;
DECL|field|copyMinScore
specifier|protected
name|boolean
name|copyMinScore
decl_stmt|;
DECL|field|values
specifier|protected
name|List
argument_list|<
name|LabelValue
argument_list|>
name|values
decl_stmt|;
DECL|field|maxNegative
specifier|protected
name|short
name|maxNegative
decl_stmt|;
DECL|field|maxPositive
specifier|protected
name|short
name|maxPositive
decl_stmt|;
DECL|field|canOverride
specifier|private
specifier|transient
name|boolean
name|canOverride
decl_stmt|;
DECL|field|intList
specifier|private
specifier|transient
name|List
argument_list|<
name|Integer
argument_list|>
name|intList
decl_stmt|;
DECL|field|byValue
specifier|private
specifier|transient
name|Map
argument_list|<
name|Short
argument_list|,
name|LabelValue
argument_list|>
name|byValue
decl_stmt|;
DECL|method|LabelType ()
specifier|protected
name|LabelType
parameter_list|()
block|{   }
DECL|method|LabelType (String name, List<LabelValue> valueList)
specifier|public
name|LabelType
parameter_list|(
name|String
name|name
parameter_list|,
name|List
argument_list|<
name|LabelValue
argument_list|>
name|valueList
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
name|canOverride
operator|=
literal|true
expr_stmt|;
name|values
operator|=
name|sortValues
argument_list|(
name|valueList
argument_list|)
expr_stmt|;
name|abbreviatedName
operator|=
name|defaultAbbreviation
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|functionName
operator|=
literal|"MaxWithBlock"
expr_stmt|;
name|maxNegative
operator|=
name|Short
operator|.
name|MIN_VALUE
expr_stmt|;
name|maxPositive
operator|=
name|Short
operator|.
name|MAX_VALUE
expr_stmt|;
if|if
condition|(
name|values
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getValue
argument_list|()
operator|<
literal|0
condition|)
block|{
name|maxNegative
operator|=
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|values
operator|.
name|get
argument_list|(
name|values
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|getValue
argument_list|()
operator|>
literal|0
condition|)
block|{
name|maxPositive
operator|=
name|values
operator|.
name|get
argument_list|(
name|values
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
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
annotation|@
name|Deprecated
DECL|method|getId ()
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
annotation|@
name|Deprecated
DECL|method|setId (String id)
specifier|public
name|void
name|setId
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|checkId
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
DECL|method|matches (PatchSetApproval psa)
specifier|public
name|boolean
name|matches
parameter_list|(
name|PatchSetApproval
name|psa
parameter_list|)
block|{
if|if
condition|(
name|id
operator|!=
literal|null
operator|&&
name|psa
operator|.
name|getLabelId
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|equals
argument_list|(
name|id
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
name|psa
operator|.
name|getLabelId
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|name
argument_list|)
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
DECL|method|setAbbreviatedName (String abbreviatedName)
specifier|public
name|void
name|setAbbreviatedName
parameter_list|(
name|String
name|abbreviatedName
parameter_list|)
block|{
name|this
operator|.
name|abbreviatedName
operator|=
name|abbreviatedName
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
DECL|method|setFunctionName (String functionName)
specifier|public
name|void
name|setFunctionName
parameter_list|(
name|String
name|functionName
parameter_list|)
block|{
name|this
operator|.
name|functionName
operator|=
name|functionName
expr_stmt|;
block|}
DECL|method|canOverride ()
specifier|public
name|boolean
name|canOverride
parameter_list|()
block|{
return|return
name|canOverride
return|;
block|}
DECL|method|setCanOverride (boolean canOverride)
specifier|public
name|void
name|setCanOverride
parameter_list|(
name|boolean
name|canOverride
parameter_list|)
block|{
name|this
operator|.
name|canOverride
operator|=
name|canOverride
expr_stmt|;
block|}
DECL|method|getValues ()
specifier|public
name|List
argument_list|<
name|LabelValue
argument_list|>
name|getValues
parameter_list|()
block|{
return|return
name|values
return|;
block|}
DECL|method|getMin ()
specifier|public
name|LabelValue
name|getMin
parameter_list|()
block|{
if|if
condition|(
name|values
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
DECL|method|getMax ()
specifier|public
name|LabelValue
name|getMax
parameter_list|()
block|{
if|if
condition|(
name|values
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|LabelValue
name|v
init|=
name|values
operator|.
name|get
argument_list|(
name|values
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
return|return
name|v
operator|.
name|getValue
argument_list|()
operator|>
literal|0
condition|?
name|v
else|:
literal|null
return|;
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
DECL|method|setCopyMinScore (boolean copyMinScore)
specifier|public
name|void
name|setCopyMinScore
parameter_list|(
name|boolean
name|copyMinScore
parameter_list|)
block|{
name|this
operator|.
name|copyMinScore
operator|=
name|copyMinScore
expr_stmt|;
block|}
DECL|method|isMaxNegative (PatchSetApproval ca)
specifier|public
name|boolean
name|isMaxNegative
parameter_list|(
name|PatchSetApproval
name|ca
parameter_list|)
block|{
return|return
name|maxNegative
operator|==
name|ca
operator|.
name|getValue
argument_list|()
return|;
block|}
DECL|method|isMaxPositive (PatchSetApproval ca)
specifier|public
name|boolean
name|isMaxPositive
parameter_list|(
name|PatchSetApproval
name|ca
parameter_list|)
block|{
return|return
name|maxPositive
operator|==
name|ca
operator|.
name|getValue
argument_list|()
return|;
block|}
DECL|method|getValue (short value)
specifier|public
name|LabelValue
name|getValue
parameter_list|(
name|short
name|value
parameter_list|)
block|{
name|initByValue
argument_list|()
expr_stmt|;
return|return
name|byValue
operator|.
name|get
argument_list|(
name|value
argument_list|)
return|;
block|}
DECL|method|getValue (final PatchSetApproval ca)
specifier|public
name|LabelValue
name|getValue
parameter_list|(
specifier|final
name|PatchSetApproval
name|ca
parameter_list|)
block|{
name|initByValue
argument_list|()
expr_stmt|;
return|return
name|byValue
operator|.
name|get
argument_list|(
name|ca
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
block|}
DECL|method|initByValue ()
specifier|private
name|void
name|initByValue
parameter_list|()
block|{
if|if
condition|(
name|byValue
operator|==
literal|null
condition|)
block|{
name|byValue
operator|=
operator|new
name|HashMap
argument_list|<
name|Short
argument_list|,
name|LabelValue
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
specifier|final
name|LabelValue
name|v
range|:
name|values
control|)
block|{
name|byValue
operator|.
name|put
argument_list|(
name|v
operator|.
name|getValue
argument_list|()
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getValuesAsList ()
specifier|public
name|List
argument_list|<
name|Integer
argument_list|>
name|getValuesAsList
parameter_list|()
block|{
if|if
condition|(
name|intList
operator|==
literal|null
condition|)
block|{
name|intList
operator|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|values
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|LabelValue
name|v
range|:
name|values
control|)
block|{
name|intList
operator|.
name|add
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|v
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|intList
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|reverse
argument_list|(
name|intList
argument_list|)
expr_stmt|;
block|}
return|return
name|intList
return|;
block|}
DECL|method|getLabelId ()
specifier|public
name|LabelId
name|getLabelId
parameter_list|()
block|{
return|return
operator|new
name|LabelId
argument_list|(
name|name
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
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|name
argument_list|)
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
decl_stmt|;
name|LabelValue
name|min
init|=
name|getMin
argument_list|()
decl_stmt|;
name|LabelValue
name|max
init|=
name|getMax
argument_list|()
decl_stmt|;
if|if
condition|(
name|min
operator|!=
literal|null
operator|&&
name|max
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
operator|new
name|PermissionRange
argument_list|(
name|Permission
operator|.
name|forLabel
argument_list|(
name|name
argument_list|)
argument_list|,
name|min
operator|.
name|getValue
argument_list|()
argument_list|,
name|max
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|min
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|min
operator|.
name|formatValue
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|max
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|max
operator|.
name|formatValue
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

