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
DECL|package|com.google.gerrit.client.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|change
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
name|Dispatcher
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
name|client
operator|.
name|changes
operator|.
name|CommentInfo
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
name|client
operator|.
name|diff
operator|.
name|DisplaySide
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
name|client
operator|.
name|ui
operator|.
name|CommentLinkProcessor
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
name|client
operator|.
name|ui
operator|.
name|InlineHyperlink
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
name|extensions
operator|.
name|client
operator|.
name|Side
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
name|PatchSet
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
name|core
operator|.
name|client
operator|.
name|GWT
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
name|uibinder
operator|.
name|client
operator|.
name|UiBinder
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
name|uibinder
operator|.
name|client
operator|.
name|UiField
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
name|HTMLPanel
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
DECL|class|LineComment
class|class
name|LineComment
extends|extends
name|Composite
block|{
DECL|interface|Binder
interface|interface
name|Binder
extends|extends
name|UiBinder
argument_list|<
name|HTMLPanel
argument_list|,
name|LineComment
argument_list|>
block|{}
DECL|field|uiBinder
specifier|private
specifier|static
specifier|final
name|Binder
name|uiBinder
init|=
name|GWT
operator|.
name|create
argument_list|(
name|Binder
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|sideLoc
annotation|@
name|UiField
name|Element
name|sideLoc
decl_stmt|;
DECL|field|psLoc
annotation|@
name|UiField
name|Element
name|psLoc
decl_stmt|;
DECL|field|psNum
annotation|@
name|UiField
name|Element
name|psNum
decl_stmt|;
DECL|field|fileLoc
annotation|@
name|UiField
name|Element
name|fileLoc
decl_stmt|;
DECL|field|lineLoc
annotation|@
name|UiField
name|Element
name|lineLoc
decl_stmt|;
DECL|field|line
annotation|@
name|UiField
name|InlineHyperlink
name|line
decl_stmt|;
DECL|field|message
annotation|@
name|UiField
name|Element
name|message
decl_stmt|;
DECL|method|LineComment (CommentLinkProcessor clp, PatchSet.Id defaultPs, CommentInfo info)
name|LineComment
parameter_list|(
name|CommentLinkProcessor
name|clp
parameter_list|,
name|PatchSet
operator|.
name|Id
name|defaultPs
parameter_list|,
name|CommentInfo
name|info
parameter_list|)
block|{
name|initWidget
argument_list|(
name|uiBinder
operator|.
name|createAndBindUi
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
name|PatchSet
operator|.
name|Id
name|ps
decl_stmt|;
if|if
condition|(
name|info
operator|.
name|patchSet
argument_list|()
operator|!=
name|defaultPs
operator|.
name|get
argument_list|()
condition|)
block|{
name|ps
operator|=
operator|new
name|PatchSet
operator|.
name|Id
argument_list|(
name|defaultPs
operator|.
name|getParentKey
argument_list|()
argument_list|,
name|info
operator|.
name|patchSet
argument_list|()
argument_list|)
expr_stmt|;
name|psNum
operator|.
name|setInnerText
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|info
operator|.
name|patchSet
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|sideLoc
operator|.
name|removeFromParent
argument_list|()
expr_stmt|;
name|sideLoc
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|info
operator|.
name|side
argument_list|()
operator|==
name|Side
operator|.
name|PARENT
condition|)
block|{
name|ps
operator|=
name|defaultPs
expr_stmt|;
name|psLoc
operator|.
name|removeFromParent
argument_list|()
expr_stmt|;
name|psLoc
operator|=
literal|null
expr_stmt|;
name|psNum
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|ps
operator|=
name|defaultPs
expr_stmt|;
name|sideLoc
operator|.
name|removeFromParent
argument_list|()
expr_stmt|;
name|sideLoc
operator|=
literal|null
expr_stmt|;
name|psLoc
operator|.
name|removeFromParent
argument_list|()
expr_stmt|;
name|psLoc
operator|=
literal|null
expr_stmt|;
name|psNum
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|info
operator|.
name|hasLine
argument_list|()
condition|)
block|{
name|fileLoc
operator|.
name|removeFromParent
argument_list|()
expr_stmt|;
name|fileLoc
operator|=
literal|null
expr_stmt|;
name|line
operator|.
name|setTargetHistoryToken
argument_list|(
name|url
argument_list|(
name|ps
argument_list|,
name|info
argument_list|)
argument_list|)
expr_stmt|;
name|line
operator|.
name|setText
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|info
operator|.
name|line
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|lineLoc
operator|.
name|removeFromParent
argument_list|()
expr_stmt|;
name|lineLoc
operator|=
literal|null
expr_stmt|;
name|line
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|info
operator|.
name|message
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|message
operator|.
name|setInnerSafeHtml
argument_list|(
name|clp
operator|.
name|apply
argument_list|(
operator|new
name|SafeHtmlBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|info
operator|.
name|message
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|)
operator|.
name|wikify
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|url (PatchSet.Id ps, CommentInfo info)
specifier|private
specifier|static
name|String
name|url
parameter_list|(
name|PatchSet
operator|.
name|Id
name|ps
parameter_list|,
name|CommentInfo
name|info
parameter_list|)
block|{
return|return
name|Dispatcher
operator|.
name|toSideBySide
argument_list|(
literal|null
argument_list|,
name|ps
argument_list|,
name|info
operator|.
name|path
argument_list|()
argument_list|,
name|info
operator|.
name|side
argument_list|()
operator|==
name|Side
operator|.
name|PARENT
condition|?
name|DisplaySide
operator|.
name|A
else|:
name|DisplaySide
operator|.
name|B
argument_list|,
name|info
operator|.
name|line
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

