begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2009 The Android Open Source Project
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
DECL|package|com.google.gerrit.sshd
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|sshd
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|auto
operator|.
name|value
operator|.
name|AutoAnnotation
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Key
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Annotation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|sshd
operator|.
name|server
operator|.
name|Command
import|;
end_import

begin_comment
comment|/** Utilities to support {@link CommandName} construction. */
end_comment

begin_class
DECL|class|Commands
specifier|public
class|class
name|Commands
block|{
comment|/** Magic value signaling the top level. */
DECL|field|ROOT
specifier|public
specifier|static
specifier|final
name|String
name|ROOT
init|=
literal|""
decl_stmt|;
comment|/** Magic value signaling the top level. */
DECL|field|CMD_ROOT
specifier|public
specifier|static
specifier|final
name|CommandName
name|CMD_ROOT
init|=
name|named
argument_list|(
name|ROOT
argument_list|)
decl_stmt|;
DECL|method|key (String name)
specifier|public
specifier|static
name|Key
argument_list|<
name|Command
argument_list|>
name|key
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|key
argument_list|(
name|named
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
DECL|method|key (CommandName name)
specifier|public
specifier|static
name|Key
argument_list|<
name|Command
argument_list|>
name|key
parameter_list|(
name|CommandName
name|name
parameter_list|)
block|{
return|return
name|Key
operator|.
name|get
argument_list|(
name|Command
operator|.
name|class
argument_list|,
name|name
argument_list|)
return|;
block|}
DECL|method|key (CommandName parent, String name)
specifier|public
specifier|static
name|Key
argument_list|<
name|Command
argument_list|>
name|key
parameter_list|(
name|CommandName
name|parent
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|Key
operator|.
name|get
argument_list|(
name|Command
operator|.
name|class
argument_list|,
name|named
argument_list|(
name|parent
argument_list|,
name|name
argument_list|)
argument_list|)
return|;
block|}
DECL|method|key (CommandName parent, String name, String descr)
specifier|public
specifier|static
name|Key
argument_list|<
name|Command
argument_list|>
name|key
parameter_list|(
name|CommandName
name|parent
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|descr
parameter_list|)
block|{
return|return
name|Key
operator|.
name|get
argument_list|(
name|Command
operator|.
name|class
argument_list|,
name|named
argument_list|(
name|parent
argument_list|,
name|name
argument_list|,
name|descr
argument_list|)
argument_list|)
return|;
block|}
comment|/** Create a CommandName annotation for the supplied name. */
annotation|@
name|AutoAnnotation
DECL|method|named (String value)
specifier|public
specifier|static
name|CommandName
name|named
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
operator|new
name|AutoAnnotation_Commands_named
argument_list|(
name|value
argument_list|)
return|;
block|}
comment|/** Create a CommandName annotation for the supplied name. */
DECL|method|named (CommandName parent, String name)
specifier|public
specifier|static
name|CommandName
name|named
parameter_list|(
name|CommandName
name|parent
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|NestedCommandNameImpl
argument_list|(
name|parent
argument_list|,
name|name
argument_list|)
return|;
block|}
comment|/** Create a CommandName annotation for the supplied name and description. */
DECL|method|named (CommandName parent, String name, String descr)
specifier|public
specifier|static
name|CommandName
name|named
parameter_list|(
name|CommandName
name|parent
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|descr
parameter_list|)
block|{
return|return
operator|new
name|NestedCommandNameImpl
argument_list|(
name|parent
argument_list|,
name|name
argument_list|,
name|descr
argument_list|)
return|;
block|}
comment|/** Return the name of this command, possibly including any parents. */
DECL|method|nameOf (CommandName name)
specifier|public
specifier|static
name|String
name|nameOf
parameter_list|(
name|CommandName
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|instanceof
name|NestedCommandNameImpl
condition|)
block|{
return|return
name|nameOf
argument_list|(
operator|(
operator|(
name|NestedCommandNameImpl
operator|)
name|name
operator|)
operator|.
name|parent
argument_list|)
operator|+
literal|" "
operator|+
name|name
operator|.
name|value
argument_list|()
return|;
block|}
return|return
name|name
operator|.
name|value
argument_list|()
return|;
block|}
comment|/** Is the second command a direct child of the first command? */
DECL|method|isChild (CommandName parent, CommandName name)
specifier|public
specifier|static
name|boolean
name|isChild
parameter_list|(
name|CommandName
name|parent
parameter_list|,
name|CommandName
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|instanceof
name|NestedCommandNameImpl
condition|)
block|{
return|return
name|parent
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|NestedCommandNameImpl
operator|)
name|name
operator|)
operator|.
name|parent
argument_list|)
return|;
block|}
if|if
condition|(
name|parent
operator|==
name|CMD_ROOT
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|parentOf (CommandName name)
specifier|static
name|CommandName
name|parentOf
parameter_list|(
name|CommandName
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|instanceof
name|NestedCommandNameImpl
condition|)
block|{
return|return
operator|(
operator|(
name|NestedCommandNameImpl
operator|)
name|name
operator|)
operator|.
name|parent
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|class|NestedCommandNameImpl
specifier|static
specifier|final
class|class
name|NestedCommandNameImpl
implements|implements
name|CommandName
block|{
DECL|field|parent
specifier|private
specifier|final
name|CommandName
name|parent
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|descr
specifier|private
specifier|final
name|String
name|descr
decl_stmt|;
DECL|method|NestedCommandNameImpl (CommandName parent, String name)
name|NestedCommandNameImpl
parameter_list|(
name|CommandName
name|parent
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|descr
operator|=
literal|""
expr_stmt|;
block|}
DECL|method|NestedCommandNameImpl (CommandName parent, String name, String descr)
name|NestedCommandNameImpl
parameter_list|(
name|CommandName
name|parent
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|descr
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|descr
operator|=
name|descr
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|value ()
specifier|public
name|String
name|value
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|descr ()
specifier|public
name|String
name|descr
parameter_list|()
block|{
return|return
name|descr
return|;
block|}
annotation|@
name|Override
DECL|method|annotationType ()
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|Annotation
argument_list|>
name|annotationType
parameter_list|()
block|{
return|return
name|CommandName
operator|.
name|class
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
name|parent
operator|.
name|hashCode
argument_list|()
operator|*
literal|31
operator|+
name|value
argument_list|()
operator|.
name|hashCode
argument_list|()
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
return|return
name|obj
operator|instanceof
name|NestedCommandNameImpl
operator|&&
name|parent
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|NestedCommandNameImpl
operator|)
name|obj
operator|)
operator|.
name|parent
argument_list|)
operator|&&
name|value
argument_list|()
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|NestedCommandNameImpl
operator|)
name|obj
operator|)
operator|.
name|value
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
return|return
literal|"CommandName["
operator|+
name|nameOf
argument_list|(
name|this
argument_list|)
operator|+
literal|"]"
return|;
block|}
block|}
DECL|method|Commands ()
specifier|private
name|Commands
parameter_list|()
block|{}
block|}
end_class

end_unit

