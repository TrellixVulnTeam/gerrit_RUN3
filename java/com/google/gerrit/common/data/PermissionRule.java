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

begin_class
DECL|class|PermissionRule
specifier|public
class|class
name|PermissionRule
implements|implements
name|Comparable
argument_list|<
name|PermissionRule
argument_list|>
block|{
DECL|field|FORCE_PUSH
specifier|public
specifier|static
specifier|final
name|String
name|FORCE_PUSH
init|=
literal|"Force Push"
decl_stmt|;
DECL|field|FORCE_EDIT
specifier|public
specifier|static
specifier|final
name|String
name|FORCE_EDIT
init|=
literal|"Force Edit"
decl_stmt|;
DECL|enum|Action
specifier|public
enum|enum
name|Action
block|{
DECL|enumConstant|ALLOW
name|ALLOW
block|,
DECL|enumConstant|DENY
name|DENY
block|,
DECL|enumConstant|BLOCK
name|BLOCK
block|,
DECL|enumConstant|INTERACTIVE
name|INTERACTIVE
block|,
DECL|enumConstant|BATCH
name|BATCH
block|}
DECL|field|action
specifier|protected
name|Action
name|action
init|=
name|Action
operator|.
name|ALLOW
decl_stmt|;
DECL|field|force
specifier|protected
name|boolean
name|force
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
DECL|field|group
specifier|protected
name|GroupReference
name|group
decl_stmt|;
DECL|method|PermissionRule ()
specifier|public
name|PermissionRule
parameter_list|()
block|{}
DECL|method|PermissionRule (GroupReference group)
specifier|public
name|PermissionRule
parameter_list|(
name|GroupReference
name|group
parameter_list|)
block|{
name|this
operator|.
name|group
operator|=
name|group
expr_stmt|;
block|}
DECL|method|getAction ()
specifier|public
name|Action
name|getAction
parameter_list|()
block|{
return|return
name|action
return|;
block|}
DECL|method|setAction (Action action)
specifier|public
name|void
name|setAction
parameter_list|(
name|Action
name|action
parameter_list|)
block|{
if|if
condition|(
name|action
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"action"
argument_list|)
throw|;
block|}
name|this
operator|.
name|action
operator|=
name|action
expr_stmt|;
block|}
DECL|method|isDeny ()
specifier|public
name|boolean
name|isDeny
parameter_list|()
block|{
return|return
name|action
operator|==
name|Action
operator|.
name|DENY
return|;
block|}
DECL|method|setDeny ()
specifier|public
name|void
name|setDeny
parameter_list|()
block|{
name|action
operator|=
name|Action
operator|.
name|DENY
expr_stmt|;
block|}
DECL|method|isBlock ()
specifier|public
name|boolean
name|isBlock
parameter_list|()
block|{
return|return
name|action
operator|==
name|Action
operator|.
name|BLOCK
return|;
block|}
DECL|method|setBlock ()
specifier|public
name|void
name|setBlock
parameter_list|()
block|{
name|action
operator|=
name|Action
operator|.
name|BLOCK
expr_stmt|;
block|}
DECL|method|getForce ()
specifier|public
name|Boolean
name|getForce
parameter_list|()
block|{
return|return
name|force
return|;
block|}
DECL|method|setForce (Boolean newForce)
specifier|public
name|void
name|setForce
parameter_list|(
name|Boolean
name|newForce
parameter_list|)
block|{
name|force
operator|=
name|newForce
expr_stmt|;
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
DECL|method|setMin (int min)
specifier|public
name|void
name|setMin
parameter_list|(
name|int
name|min
parameter_list|)
block|{
name|this
operator|.
name|min
operator|=
name|min
expr_stmt|;
block|}
DECL|method|setMax (int max)
specifier|public
name|void
name|setMax
parameter_list|(
name|int
name|max
parameter_list|)
block|{
name|this
operator|.
name|max
operator|=
name|max
expr_stmt|;
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
DECL|method|setRange (int newMin, int newMax)
specifier|public
name|void
name|setRange
parameter_list|(
name|int
name|newMin
parameter_list|,
name|int
name|newMax
parameter_list|)
block|{
if|if
condition|(
name|newMax
operator|<
name|newMin
condition|)
block|{
name|min
operator|=
name|newMax
expr_stmt|;
name|max
operator|=
name|newMin
expr_stmt|;
block|}
else|else
block|{
name|min
operator|=
name|newMin
expr_stmt|;
name|max
operator|=
name|newMax
expr_stmt|;
block|}
block|}
DECL|method|getGroup ()
specifier|public
name|GroupReference
name|getGroup
parameter_list|()
block|{
return|return
name|group
return|;
block|}
DECL|method|setGroup (GroupReference newGroup)
specifier|public
name|void
name|setGroup
parameter_list|(
name|GroupReference
name|newGroup
parameter_list|)
block|{
name|group
operator|=
name|newGroup
expr_stmt|;
block|}
DECL|method|mergeFrom (PermissionRule src)
name|void
name|mergeFrom
parameter_list|(
name|PermissionRule
name|src
parameter_list|)
block|{
if|if
condition|(
name|getAction
argument_list|()
operator|!=
name|src
operator|.
name|getAction
argument_list|()
condition|)
block|{
if|if
condition|(
name|getAction
argument_list|()
operator|==
name|Action
operator|.
name|BLOCK
operator|||
name|src
operator|.
name|getAction
argument_list|()
operator|==
name|Action
operator|.
name|BLOCK
condition|)
block|{
name|setAction
argument_list|(
name|Action
operator|.
name|BLOCK
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|getAction
argument_list|()
operator|==
name|Action
operator|.
name|DENY
operator|||
name|src
operator|.
name|getAction
argument_list|()
operator|==
name|Action
operator|.
name|DENY
condition|)
block|{
name|setAction
argument_list|(
name|Action
operator|.
name|DENY
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|getAction
argument_list|()
operator|==
name|Action
operator|.
name|BATCH
operator|||
name|src
operator|.
name|getAction
argument_list|()
operator|==
name|Action
operator|.
name|BATCH
condition|)
block|{
name|setAction
argument_list|(
name|Action
operator|.
name|BATCH
argument_list|)
expr_stmt|;
block|}
block|}
name|setForce
argument_list|(
name|getForce
argument_list|()
operator|||
name|src
operator|.
name|getForce
argument_list|()
argument_list|)
expr_stmt|;
name|setRange
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|getMin
argument_list|()
argument_list|,
name|src
operator|.
name|getMin
argument_list|()
argument_list|)
argument_list|,
name|Math
operator|.
name|max
argument_list|(
name|getMax
argument_list|()
argument_list|,
name|src
operator|.
name|getMax
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compareTo (PermissionRule o)
specifier|public
name|int
name|compareTo
parameter_list|(
name|PermissionRule
name|o
parameter_list|)
block|{
name|int
name|cmp
init|=
name|action
argument_list|(
name|this
argument_list|)
operator|-
name|action
argument_list|(
name|o
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|==
literal|0
condition|)
block|{
name|cmp
operator|=
name|range
argument_list|(
name|o
argument_list|)
operator|-
name|range
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cmp
operator|==
literal|0
condition|)
block|{
name|cmp
operator|=
name|group
argument_list|(
name|this
argument_list|)
operator|.
name|compareTo
argument_list|(
name|group
argument_list|(
name|o
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|cmp
return|;
block|}
DECL|method|action (PermissionRule a)
specifier|private
specifier|static
name|int
name|action
parameter_list|(
name|PermissionRule
name|a
parameter_list|)
block|{
switch|switch
condition|(
name|a
operator|.
name|getAction
argument_list|()
condition|)
block|{
case|case
name|DENY
case|:
return|return
literal|0
return|;
case|case
name|ALLOW
case|:
case|case
name|BATCH
case|:
case|case
name|BLOCK
case|:
case|case
name|INTERACTIVE
case|:
default|default:
return|return
literal|1
operator|+
name|a
operator|.
name|getAction
argument_list|()
operator|.
name|ordinal
argument_list|()
return|;
block|}
block|}
DECL|method|range (PermissionRule a)
specifier|private
specifier|static
name|int
name|range
parameter_list|(
name|PermissionRule
name|a
parameter_list|)
block|{
return|return
name|Math
operator|.
name|abs
argument_list|(
name|a
operator|.
name|getMin
argument_list|()
argument_list|)
operator|+
name|Math
operator|.
name|abs
argument_list|(
name|a
operator|.
name|getMax
argument_list|()
argument_list|)
return|;
block|}
DECL|method|group (PermissionRule a)
specifier|private
specifier|static
name|String
name|group
parameter_list|(
name|PermissionRule
name|a
parameter_list|)
block|{
return|return
name|a
operator|.
name|getGroup
argument_list|()
operator|.
name|getName
argument_list|()
operator|!=
literal|null
condition|?
name|a
operator|.
name|getGroup
argument_list|()
operator|.
name|getName
argument_list|()
else|:
literal|""
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
name|asString
argument_list|(
literal|true
argument_list|)
return|;
block|}
DECL|method|asString (boolean canUseRange)
specifier|public
name|String
name|asString
parameter_list|(
name|boolean
name|canUseRange
parameter_list|)
block|{
name|StringBuilder
name|r
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|getAction
argument_list|()
condition|)
block|{
case|case
name|ALLOW
case|:
break|break;
case|case
name|DENY
case|:
name|r
operator|.
name|append
argument_list|(
literal|"deny "
argument_list|)
expr_stmt|;
break|break;
case|case
name|BLOCK
case|:
name|r
operator|.
name|append
argument_list|(
literal|"block "
argument_list|)
expr_stmt|;
break|break;
case|case
name|INTERACTIVE
case|:
name|r
operator|.
name|append
argument_list|(
literal|"interactive "
argument_list|)
expr_stmt|;
break|break;
case|case
name|BATCH
case|:
name|r
operator|.
name|append
argument_list|(
literal|"batch "
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|getForce
argument_list|()
condition|)
block|{
name|r
operator|.
name|append
argument_list|(
literal|"+force "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|canUseRange
operator|&&
operator|(
name|getMin
argument_list|()
operator|!=
literal|0
operator|||
name|getMax
argument_list|()
operator|!=
literal|0
operator|)
condition|)
block|{
if|if
condition|(
literal|0
operator|<=
name|getMin
argument_list|()
condition|)
block|{
name|r
operator|.
name|append
argument_list|(
literal|'+'
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
literal|0
operator|<=
name|getMax
argument_list|()
condition|)
block|{
name|r
operator|.
name|append
argument_list|(
literal|'+'
argument_list|)
expr_stmt|;
block|}
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
name|r
operator|.
name|append
argument_list|(
name|getGroup
argument_list|()
operator|.
name|toConfigValue
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
DECL|method|fromString (String src, boolean mightUseRange)
specifier|public
specifier|static
name|PermissionRule
name|fromString
parameter_list|(
name|String
name|src
parameter_list|,
name|boolean
name|mightUseRange
parameter_list|)
block|{
specifier|final
name|String
name|orig
init|=
name|src
decl_stmt|;
specifier|final
name|PermissionRule
name|rule
init|=
operator|new
name|PermissionRule
argument_list|()
decl_stmt|;
name|src
operator|=
name|src
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|src
operator|.
name|startsWith
argument_list|(
literal|"deny "
argument_list|)
condition|)
block|{
name|rule
operator|.
name|setAction
argument_list|(
name|Action
operator|.
name|DENY
argument_list|)
expr_stmt|;
name|src
operator|=
name|src
operator|.
name|substring
argument_list|(
literal|"deny "
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|src
operator|.
name|startsWith
argument_list|(
literal|"block "
argument_list|)
condition|)
block|{
name|rule
operator|.
name|setAction
argument_list|(
name|Action
operator|.
name|BLOCK
argument_list|)
expr_stmt|;
name|src
operator|=
name|src
operator|.
name|substring
argument_list|(
literal|"block "
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|src
operator|.
name|startsWith
argument_list|(
literal|"interactive "
argument_list|)
condition|)
block|{
name|rule
operator|.
name|setAction
argument_list|(
name|Action
operator|.
name|INTERACTIVE
argument_list|)
expr_stmt|;
name|src
operator|=
name|src
operator|.
name|substring
argument_list|(
literal|"interactive "
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|src
operator|.
name|startsWith
argument_list|(
literal|"batch "
argument_list|)
condition|)
block|{
name|rule
operator|.
name|setAction
argument_list|(
name|Action
operator|.
name|BATCH
argument_list|)
expr_stmt|;
name|src
operator|=
name|src
operator|.
name|substring
argument_list|(
literal|"batch "
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|src
operator|.
name|startsWith
argument_list|(
literal|"+force "
argument_list|)
condition|)
block|{
name|rule
operator|.
name|setForce
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|src
operator|=
name|src
operator|.
name|substring
argument_list|(
literal|"+force "
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|mightUseRange
operator|&&
operator|!
name|GroupReference
operator|.
name|isGroupReference
argument_list|(
name|src
argument_list|)
condition|)
block|{
name|int
name|sp
init|=
name|src
operator|.
name|indexOf
argument_list|(
literal|' '
argument_list|)
decl_stmt|;
name|String
name|range
init|=
name|src
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|sp
argument_list|)
decl_stmt|;
if|if
condition|(
name|range
operator|.
name|matches
argument_list|(
literal|"^([+-]?\\d+)\\.\\.([+-]?\\d+)$"
argument_list|)
condition|)
block|{
name|int
name|dotdot
init|=
name|range
operator|.
name|indexOf
argument_list|(
literal|".."
argument_list|)
decl_stmt|;
name|int
name|min
init|=
name|parseInt
argument_list|(
name|range
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|dotdot
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|max
init|=
name|parseInt
argument_list|(
name|range
operator|.
name|substring
argument_list|(
name|dotdot
operator|+
literal|2
argument_list|)
argument_list|)
decl_stmt|;
name|rule
operator|.
name|setRange
argument_list|(
name|min
argument_list|,
name|max
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid range in rule: "
operator|+
name|orig
argument_list|)
throw|;
block|}
name|src
operator|=
name|src
operator|.
name|substring
argument_list|(
name|sp
operator|+
literal|1
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
name|String
name|groupName
init|=
name|GroupReference
operator|.
name|extractGroupName
argument_list|(
name|src
argument_list|)
decl_stmt|;
if|if
condition|(
name|groupName
operator|!=
literal|null
condition|)
block|{
name|GroupReference
name|group
init|=
operator|new
name|GroupReference
argument_list|()
decl_stmt|;
name|group
operator|.
name|setName
argument_list|(
name|groupName
argument_list|)
expr_stmt|;
name|rule
operator|.
name|setGroup
argument_list|(
name|group
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Rule must include group: "
operator|+
name|orig
argument_list|)
throw|;
block|}
return|return
name|rule
return|;
block|}
DECL|method|hasRange ()
specifier|public
name|boolean
name|hasRange
parameter_list|()
block|{
return|return
name|getMin
argument_list|()
operator|!=
literal|0
operator|||
name|getMax
argument_list|()
operator|!=
literal|0
return|;
block|}
DECL|method|parseInt (String value)
specifier|public
specifier|static
name|int
name|parseInt
parameter_list|(
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|.
name|startsWith
argument_list|(
literal|"+"
argument_list|)
condition|)
block|{
name|value
operator|=
name|value
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|obj
operator|instanceof
name|PermissionRule
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|PermissionRule
name|other
init|=
operator|(
name|PermissionRule
operator|)
name|obj
decl_stmt|;
return|return
name|action
operator|.
name|equals
argument_list|(
name|other
operator|.
name|action
argument_list|)
operator|&&
name|force
operator|==
name|other
operator|.
name|force
operator|&&
name|min
operator|==
name|other
operator|.
name|min
operator|&&
name|max
operator|==
name|other
operator|.
name|max
operator|&&
name|group
operator|.
name|equals
argument_list|(
name|other
operator|.
name|group
argument_list|)
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
return|return
name|group
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

