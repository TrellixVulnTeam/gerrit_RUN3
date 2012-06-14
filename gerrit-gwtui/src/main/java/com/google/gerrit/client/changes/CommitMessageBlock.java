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
DECL|package|com.google.gerrit.client.changes
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|changes
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
name|CommentLinkProcessor
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
name|PreElement
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
name|Style
operator|.
name|Display
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
DECL|class|CommitMessageBlock
specifier|public
class|class
name|CommitMessageBlock
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
name|CommitMessageBlock
argument_list|>
block|{   }
DECL|field|uiBinder
specifier|private
specifier|static
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
annotation|@
name|UiField
DECL|field|commitSummaryPre
name|PreElement
name|commitSummaryPre
decl_stmt|;
annotation|@
name|UiField
DECL|field|commitBodyPre
name|PreElement
name|commitBodyPre
decl_stmt|;
DECL|method|CommitMessageBlock ()
specifier|public
name|CommitMessageBlock
parameter_list|()
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
block|}
DECL|method|display (final String commitMessage)
specifier|public
name|void
name|display
parameter_list|(
specifier|final
name|String
name|commitMessage
parameter_list|)
block|{
name|String
index|[]
name|splitCommitMessage
init|=
name|commitMessage
operator|.
name|split
argument_list|(
literal|"\n"
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|String
name|commitSummary
init|=
name|splitCommitMessage
index|[
literal|0
index|]
decl_stmt|;
name|String
name|commitBody
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|splitCommitMessage
operator|.
name|length
operator|>
literal|1
condition|)
block|{
name|commitBody
operator|=
name|splitCommitMessage
index|[
literal|1
index|]
expr_stmt|;
block|}
comment|// Linkify commit summary
name|SafeHtml
name|commitSummaryLinkified
init|=
operator|new
name|SafeHtmlBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|commitSummary
argument_list|)
decl_stmt|;
name|commitSummaryLinkified
operator|=
name|commitSummaryLinkified
operator|.
name|linkify
argument_list|()
expr_stmt|;
name|commitSummaryLinkified
operator|=
name|CommentLinkProcessor
operator|.
name|apply
argument_list|(
name|commitSummaryLinkified
argument_list|)
expr_stmt|;
name|commitSummaryPre
operator|.
name|setInnerHTML
argument_list|(
name|commitSummaryLinkified
operator|.
name|asString
argument_list|()
argument_list|)
expr_stmt|;
comment|// Hide commit body if there is no body
if|if
condition|(
name|commitBody
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|commitBodyPre
operator|.
name|getStyle
argument_list|()
operator|.
name|setDisplay
argument_list|(
name|Display
operator|.
name|NONE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Linkify commit body
name|SafeHtml
name|commitBodyLinkified
init|=
operator|new
name|SafeHtmlBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|commitBody
argument_list|)
decl_stmt|;
name|commitBodyLinkified
operator|=
name|commitBodyLinkified
operator|.
name|linkify
argument_list|()
expr_stmt|;
name|commitBodyLinkified
operator|=
name|CommentLinkProcessor
operator|.
name|apply
argument_list|(
name|commitBodyLinkified
argument_list|)
expr_stmt|;
name|commitBodyPre
operator|.
name|setInnerHTML
argument_list|(
name|commitBodyLinkified
operator|.
name|asString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

