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
DECL|package|com.google.gerrit.client.ui
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|ui
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
name|dom
operator|.
name|client
operator|.
name|Element
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
name|DOM
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
name|FlexTable
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
name|HTMLTable
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtexpui
operator|.
name|safehtml
operator|.
name|client
operator|.
name|SafeHtml
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtexpui
operator|.
name|safehtml
operator|.
name|client
operator|.
name|SafeHtmlBuilder
import|;
end_import

begin_class
DECL|class|FancyFlexTableImplIE8
specifier|public
class|class
name|FancyFlexTableImplIE8
extends|extends
name|FancyFlexTableImpl
block|{
annotation|@
name|Override
DECL|method|resetHtml (FlexTable myTable, SafeHtml bodyHtml)
specifier|public
name|void
name|resetHtml
parameter_list|(
name|FlexTable
name|myTable
parameter_list|,
name|SafeHtml
name|bodyHtml
parameter_list|)
block|{
specifier|final
name|Element
name|oldBody
init|=
name|getBodyElement
argument_list|(
name|myTable
argument_list|)
decl_stmt|;
specifier|final
name|Element
name|newBody
init|=
name|parseBody
argument_list|(
name|bodyHtml
argument_list|)
decl_stmt|;
assert|assert
name|newBody
operator|!=
literal|null
assert|;
specifier|final
name|Element
name|tableElem
init|=
name|DOM
operator|.
name|getParent
argument_list|(
name|oldBody
argument_list|)
decl_stmt|;
name|tableElem
operator|.
name|removeChild
argument_list|(
name|oldBody
argument_list|)
expr_stmt|;
name|setBodyElement
argument_list|(
name|myTable
argument_list|,
name|newBody
argument_list|)
expr_stmt|;
name|DOM
operator|.
name|appendChild
argument_list|(
name|tableElem
argument_list|,
name|newBody
argument_list|)
expr_stmt|;
block|}
DECL|method|parseBody (SafeHtml body)
specifier|private
specifier|static
name|Element
name|parseBody
parameter_list|(
name|SafeHtml
name|body
parameter_list|)
block|{
specifier|final
name|SafeHtmlBuilder
name|b
init|=
operator|new
name|SafeHtmlBuilder
argument_list|()
decl_stmt|;
name|b
operator|.
name|openElement
argument_list|(
literal|"table"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|body
argument_list|)
expr_stmt|;
name|b
operator|.
name|closeElement
argument_list|(
literal|"table"
argument_list|)
expr_stmt|;
specifier|final
name|Element
name|newTable
init|=
name|SafeHtml
operator|.
name|parse
argument_list|(
name|b
argument_list|)
decl_stmt|;
for|for
control|(
name|Element
name|e
init|=
name|DOM
operator|.
name|getFirstChild
argument_list|(
name|newTable
argument_list|)
init|;
name|e
operator|!=
literal|null
condition|;
name|e
operator|=
name|DOM
operator|.
name|getNextSibling
argument_list|(
name|e
argument_list|)
control|)
block|{
if|if
condition|(
literal|"tbody"
operator|.
name|equals
argument_list|(
name|e
operator|.
name|getTagName
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|e
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|setBodyElement (HTMLTable myTable, Element newBody)
specifier|private
specifier|static
specifier|native
name|void
name|setBodyElement
parameter_list|(
name|HTMLTable
name|myTable
parameter_list|,
name|Element
name|newBody
parameter_list|)
comment|/*-{ myTable.@com.google.gwt.user.client.ui.HTMLTable::bodyElem = newBody; }-*/
function_decl|;
block|}
end_class

end_unit

