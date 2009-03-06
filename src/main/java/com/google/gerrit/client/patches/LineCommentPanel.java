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
DECL|package|com.google.gerrit.client.patches
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|patches
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
name|reviewdb
operator|.
name|PatchLineComment
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
name|Widget
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
DECL|class|LineCommentPanel
specifier|public
class|class
name|LineCommentPanel
extends|extends
name|Composite
block|{
DECL|method|toSafeHtml (final PatchLineComment msg)
specifier|public
specifier|static
name|SafeHtml
name|toSafeHtml
parameter_list|(
specifier|final
name|PatchLineComment
name|msg
parameter_list|)
block|{
return|return
operator|new
name|SafeHtmlBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|msg
operator|.
name|getMessage
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|)
operator|.
name|wikify
argument_list|()
return|;
block|}
DECL|field|comment
name|PatchLineComment
name|comment
decl_stmt|;
DECL|field|isRecent
name|boolean
name|isRecent
decl_stmt|;
DECL|method|LineCommentPanel (final PatchLineComment msg)
specifier|public
name|LineCommentPanel
parameter_list|(
specifier|final
name|PatchLineComment
name|msg
parameter_list|)
block|{
name|comment
operator|=
name|msg
expr_stmt|;
specifier|final
name|Widget
name|l
init|=
name|toSafeHtml
argument_list|(
name|msg
argument_list|)
operator|.
name|toBlockWidget
argument_list|()
decl_stmt|;
name|l
operator|.
name|setStyleName
argument_list|(
literal|"gerrit-PatchLineComment"
argument_list|)
expr_stmt|;
name|initWidget
argument_list|(
name|l
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

