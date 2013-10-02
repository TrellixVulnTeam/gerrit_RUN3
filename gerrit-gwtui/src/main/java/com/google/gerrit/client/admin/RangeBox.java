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
DECL|package|com.google.gerrit.client.admin
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|admin
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|editor
operator|.
name|client
operator|.
name|IsEditor
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|editor
operator|.
name|client
operator|.
name|adapters
operator|.
name|TakesValueEditor
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|text
operator|.
name|shared
operator|.
name|Renderer
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|user
operator|.
name|client
operator|.
name|ui
operator|.
name|Composite
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|user
operator|.
name|client
operator|.
name|ui
operator|.
name|IntegerBox
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|user
operator|.
name|client
operator|.
name|ui
operator|.
name|ValueBoxBase
operator|.
name|TextAlignment
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|user
operator|.
name|client
operator|.
name|ui
operator|.
name|ValueListBox
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_class
DECL|class|RangeBox
specifier|abstract
class|class
name|RangeBox
extends|extends
name|Composite
implements|implements
name|IsEditor
argument_list|<
name|TakesValueEditor
argument_list|<
name|Integer
argument_list|>
argument_list|>
block|{
DECL|field|rangeRenderer
specifier|static
specifier|final
name|RangeRenderer
name|rangeRenderer
init|=
operator|new
name|RangeRenderer
argument_list|()
decl_stmt|;
DECL|class|RangeRenderer
specifier|private
specifier|static
class|class
name|RangeRenderer
implements|implements
name|Renderer
argument_list|<
name|Integer
argument_list|>
block|{
annotation|@
name|Override
DECL|method|render (Integer object)
specifier|public
name|String
name|render
parameter_list|(
name|Integer
name|object
parameter_list|)
block|{
if|if
condition|(
literal|0
operator|<=
name|object
condition|)
block|{
return|return
literal|"+"
operator|+
name|object
return|;
block|}
else|else
block|{
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|object
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|render (Integer object, Appendable appendable)
specifier|public
name|void
name|render
parameter_list|(
name|Integer
name|object
parameter_list|,
name|Appendable
name|appendable
parameter_list|)
throws|throws
name|IOException
block|{
name|appendable
operator|.
name|append
argument_list|(
name|render
argument_list|(
name|object
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|List
specifier|static
class|class
name|List
extends|extends
name|RangeBox
block|{
DECL|field|list
specifier|final
name|ValueListBox
argument_list|<
name|Integer
argument_list|>
name|list
decl_stmt|;
DECL|method|List ()
name|List
parameter_list|()
block|{
name|list
operator|=
operator|new
name|ValueListBox
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|rangeRenderer
argument_list|)
expr_stmt|;
name|initWidget
argument_list|(
name|list
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setEnabled (boolean on)
name|void
name|setEnabled
parameter_list|(
name|boolean
name|on
parameter_list|)
block|{
name|list
operator|.
name|getElement
argument_list|()
operator|.
name|setPropertyBoolean
argument_list|(
literal|"disabled"
argument_list|,
operator|!
name|on
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|asEditor ()
specifier|public
name|TakesValueEditor
argument_list|<
name|Integer
argument_list|>
name|asEditor
parameter_list|()
block|{
return|return
name|list
operator|.
name|asEditor
argument_list|()
return|;
block|}
block|}
DECL|class|Box
specifier|static
class|class
name|Box
extends|extends
name|RangeBox
block|{
DECL|field|box
specifier|private
specifier|final
name|IntegerBox
name|box
decl_stmt|;
DECL|method|Box ()
name|Box
parameter_list|()
block|{
name|box
operator|=
operator|new
name|IntegerBox
argument_list|()
expr_stmt|;
name|box
operator|.
name|setVisibleLength
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|box
operator|.
name|setAlignment
argument_list|(
name|TextAlignment
operator|.
name|RIGHT
argument_list|)
expr_stmt|;
name|initWidget
argument_list|(
name|box
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setEnabled (boolean on)
name|void
name|setEnabled
parameter_list|(
name|boolean
name|on
parameter_list|)
block|{
name|box
operator|.
name|getElement
argument_list|()
operator|.
name|setPropertyBoolean
argument_list|(
literal|"disabled"
argument_list|,
operator|!
name|on
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|asEditor ()
specifier|public
name|TakesValueEditor
argument_list|<
name|Integer
argument_list|>
name|asEditor
parameter_list|()
block|{
return|return
name|box
operator|.
name|asEditor
argument_list|()
return|;
block|}
block|}
DECL|method|setEnabled (boolean on)
specifier|abstract
name|void
name|setEnabled
parameter_list|(
name|boolean
name|on
parameter_list|)
function_decl|;
block|}
end_class

end_unit

