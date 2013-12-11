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
DECL|package|com.google.gerrit.server.util
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|util
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
name|Objects
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
name|Strings
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
name|LabelType
import|;
end_import

begin_comment
comment|/** A single vote on a label, consisting of a label name and a value. */
end_comment

begin_class
DECL|class|LabelVote
specifier|public
class|class
name|LabelVote
block|{
DECL|method|parse (String text)
specifier|public
specifier|static
name|LabelVote
name|parse
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|checkArgument
argument_list|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|text
argument_list|)
argument_list|,
literal|"Empty label vote"
argument_list|)
expr_stmt|;
if|if
condition|(
name|text
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'-'
condition|)
block|{
return|return
operator|new
name|LabelVote
argument_list|(
name|text
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|,
operator|(
name|short
operator|)
literal|0
argument_list|)
return|;
block|}
name|short
name|sign
init|=
literal|0
decl_stmt|;
name|int
name|i
decl_stmt|;
for|for
control|(
name|i
operator|=
name|text
operator|.
name|length
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|int
name|c
init|=
name|text
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|'-'
condition|)
block|{
name|sign
operator|=
operator|(
name|short
operator|)
operator|-
literal|1
expr_stmt|;
break|break;
block|}
elseif|else
if|if
condition|(
name|c
operator|==
literal|'+'
condition|)
block|{
name|sign
operator|=
operator|(
name|short
operator|)
literal|1
expr_stmt|;
break|break;
block|}
elseif|else
if|if
condition|(
operator|!
operator|(
literal|'0'
operator|<=
name|c
operator|&&
name|c
operator|<=
literal|'9'
operator|)
condition|)
block|{
break|break;
block|}
block|}
if|if
condition|(
name|sign
operator|==
literal|0
condition|)
block|{
return|return
operator|new
name|LabelVote
argument_list|(
name|text
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
return|;
block|}
return|return
operator|new
name|LabelVote
argument_list|(
name|text
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
argument_list|,
call|(
name|short
call|)
argument_list|(
name|sign
operator|*
name|Short
operator|.
name|parseShort
argument_list|(
name|text
operator|.
name|substring
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
DECL|method|parseWithEquals (String text)
specifier|public
specifier|static
name|LabelVote
name|parseWithEquals
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|checkArgument
argument_list|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|text
argument_list|)
argument_list|,
literal|"Empty label vote"
argument_list|)
expr_stmt|;
name|int
name|e
init|=
name|text
operator|.
name|lastIndexOf
argument_list|(
literal|'='
argument_list|)
decl_stmt|;
name|checkArgument
argument_list|(
name|e
operator|>=
literal|0
argument_list|,
literal|"Label vote missing '=': %s"
argument_list|,
name|text
argument_list|)
expr_stmt|;
return|return
operator|new
name|LabelVote
argument_list|(
name|text
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|e
argument_list|)
argument_list|,
name|Short
operator|.
name|parseShort
argument_list|(
name|text
operator|.
name|substring
argument_list|(
name|e
operator|+
literal|1
argument_list|)
argument_list|,
name|text
operator|.
name|length
argument_list|()
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
DECL|field|value
specifier|private
specifier|final
name|short
name|value
decl_stmt|;
DECL|method|LabelVote (String name, short value)
specifier|public
name|LabelVote
parameter_list|(
name|String
name|name
parameter_list|,
name|short
name|value
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|LabelType
operator|.
name|checkName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
DECL|method|getLabel ()
specifier|public
name|String
name|getLabel
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|getValue ()
specifier|public
name|short
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
DECL|method|format ()
specifier|public
name|String
name|format
parameter_list|()
block|{
if|if
condition|(
name|value
operator|==
operator|(
name|short
operator|)
literal|0
condition|)
block|{
return|return
literal|'-'
operator|+
name|name
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|==
operator|(
name|short
operator|)
literal|1
condition|)
block|{
return|return
name|name
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|<
literal|0
condition|)
block|{
return|return
name|name
operator|+
name|value
return|;
block|}
else|else
block|{
return|return
name|name
operator|+
literal|'+'
operator|+
name|value
return|;
block|}
block|}
DECL|method|formatWithEquals ()
specifier|public
name|String
name|formatWithEquals
parameter_list|()
block|{
if|if
condition|(
name|value
operator|<=
operator|(
name|short
operator|)
literal|0
condition|)
block|{
return|return
name|name
operator|+
literal|'='
operator|+
name|value
return|;
block|}
else|else
block|{
return|return
name|name
operator|+
literal|"=+"
operator|+
name|value
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|LabelVote
condition|)
block|{
name|LabelVote
name|l
init|=
operator|(
name|LabelVote
operator|)
name|o
decl_stmt|;
return|return
name|Objects
operator|.
name|equal
argument_list|(
name|name
argument_list|,
name|l
operator|.
name|name
argument_list|)
operator|&&
name|value
operator|==
name|l
operator|.
name|value
return|;
block|}
return|return
literal|false
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
return|return
name|format
argument_list|()
return|;
block|}
block|}
end_class

end_unit

