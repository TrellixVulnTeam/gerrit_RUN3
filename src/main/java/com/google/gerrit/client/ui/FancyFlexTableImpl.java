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
name|gerrit
operator|.
name|client
operator|.
name|ui
operator|.
name|FancyFlexTable
operator|.
name|MyFlexTable
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

begin_class
DECL|class|FancyFlexTableImpl
specifier|public
class|class
name|FancyFlexTableImpl
block|{
DECL|method|resetHtml (final MyFlexTable myTable, final SafeHtml body)
specifier|public
name|void
name|resetHtml
parameter_list|(
specifier|final
name|MyFlexTable
name|myTable
parameter_list|,
specifier|final
name|SafeHtml
name|body
parameter_list|)
block|{
name|SafeHtml
operator|.
name|set
argument_list|(
name|getBodyElement
argument_list|(
name|myTable
argument_list|)
argument_list|,
name|body
argument_list|)
expr_stmt|;
block|}
DECL|method|getBodyElement (HTMLTable myTable)
specifier|protected
specifier|static
specifier|native
name|Element
name|getBodyElement
parameter_list|(
name|HTMLTable
name|myTable
parameter_list|)
comment|/*-{ return myTable.@com.google.gwt.user.client.ui.HTMLTable::bodyElem; }-*/
function_decl|;
block|}
end_class

end_unit

