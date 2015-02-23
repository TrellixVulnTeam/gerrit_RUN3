begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2015 The Android Open Source Project
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
name|event
operator|.
name|logical
operator|.
name|shared
operator|.
name|CloseHandler
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
name|PopupPanel
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
name|globalkey
operator|.
name|client
operator|.
name|NpTextArea
import|;
end_import

begin_class
DECL|class|TextAreaActionDialog
specifier|public
specifier|abstract
class|class
name|TextAreaActionDialog
extends|extends
name|CommentedActionDialog
implements|implements
name|CloseHandler
argument_list|<
name|PopupPanel
argument_list|>
block|{
DECL|field|message
specifier|protected
specifier|final
name|NpTextArea
name|message
decl_stmt|;
DECL|method|TextAreaActionDialog (String title, String heading)
specifier|public
name|TextAreaActionDialog
parameter_list|(
name|String
name|title
parameter_list|,
name|String
name|heading
parameter_list|)
block|{
name|super
argument_list|(
name|title
argument_list|,
name|heading
argument_list|)
expr_stmt|;
name|message
operator|=
operator|new
name|NpTextArea
argument_list|()
expr_stmt|;
name|message
operator|.
name|setCharacterWidth
argument_list|(
literal|60
argument_list|)
expr_stmt|;
name|message
operator|.
name|setVisibleLines
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|message
operator|.
name|getElement
argument_list|()
operator|.
name|setPropertyBoolean
argument_list|(
literal|"spellcheck"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|setFocusOn
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|contentPanel
operator|.
name|add
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
DECL|method|getMessageText ()
specifier|public
name|String
name|getMessageText
parameter_list|()
block|{
return|return
name|message
operator|.
name|getText
argument_list|()
operator|.
name|trim
argument_list|()
return|;
block|}
block|}
end_class

end_unit

