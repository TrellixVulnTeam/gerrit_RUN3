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
name|Gerrit
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
name|Util
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
name|projects
operator|.
name|BranchInfo
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
name|projects
operator|.
name|ProjectApi
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
name|rpc
operator|.
name|GerritCallback
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
name|rpc
operator|.
name|Natives
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
name|Project
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
name|JsArray
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
name|FlowPanel
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
name|SuggestBox
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
name|SuggestOracle
operator|.
name|Suggestion
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
name|GlobalKey
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
name|HighlightSuggestOracle
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_class
DECL|class|CherryPickDialog
specifier|public
specifier|abstract
class|class
name|CherryPickDialog
extends|extends
name|TextAreaActionDialog
block|{
DECL|field|newBranch
specifier|private
name|SuggestBox
name|newBranch
decl_stmt|;
DECL|field|branches
specifier|private
name|List
argument_list|<
name|BranchInfo
argument_list|>
name|branches
decl_stmt|;
DECL|method|CherryPickDialog (Project.NameKey project)
specifier|public
name|CherryPickDialog
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|)
block|{
name|super
argument_list|(
name|Util
operator|.
name|C
operator|.
name|cherryPickTitle
argument_list|()
argument_list|,
name|Util
operator|.
name|C
operator|.
name|cherryPickCommitMessage
argument_list|()
argument_list|)
expr_stmt|;
name|ProjectApi
operator|.
name|getBranches
argument_list|(
name|project
argument_list|,
operator|new
name|GerritCallback
argument_list|<
name|JsArray
argument_list|<
name|BranchInfo
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|JsArray
argument_list|<
name|BranchInfo
argument_list|>
name|result
parameter_list|)
block|{
name|branches
operator|=
name|Natives
operator|.
name|asList
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|newBranch
operator|=
operator|new
name|SuggestBox
argument_list|(
operator|new
name|HighlightSuggestOracle
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|onRequestSuggestions
parameter_list|(
name|Request
name|request
parameter_list|,
name|Callback
name|done
parameter_list|)
block|{
name|LinkedList
argument_list|<
name|BranchSuggestion
argument_list|>
name|suggestions
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|BranchInfo
name|b
range|:
name|branches
control|)
block|{
if|if
condition|(
name|b
operator|.
name|ref
argument_list|()
operator|.
name|contains
argument_list|(
name|request
operator|.
name|getQuery
argument_list|()
argument_list|)
condition|)
block|{
name|suggestions
operator|.
name|add
argument_list|(
operator|new
name|BranchSuggestion
argument_list|(
name|b
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|done
operator|.
name|onSuggestionsReady
argument_list|(
name|request
argument_list|,
operator|new
name|Response
argument_list|(
name|suggestions
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|newBranch
operator|.
name|setWidth
argument_list|(
literal|"100%"
argument_list|)
expr_stmt|;
name|newBranch
operator|.
name|getElement
argument_list|()
operator|.
name|getStyle
argument_list|()
operator|.
name|setProperty
argument_list|(
literal|"boxSizing"
argument_list|,
literal|"border-box"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setCharacterWidth
argument_list|(
literal|70
argument_list|)
expr_stmt|;
specifier|final
name|FlowPanel
name|mwrap
init|=
operator|new
name|FlowPanel
argument_list|()
decl_stmt|;
name|mwrap
operator|.
name|setStyleName
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|commentedActionMessage
argument_list|()
argument_list|)
expr_stmt|;
name|mwrap
operator|.
name|add
argument_list|(
name|newBranch
argument_list|)
expr_stmt|;
name|panel
operator|.
name|insert
argument_list|(
name|mwrap
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|panel
operator|.
name|insert
argument_list|(
operator|new
name|SmallHeading
argument_list|(
name|Util
operator|.
name|C
operator|.
name|headingCherryPickBranch
argument_list|()
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|center ()
specifier|public
name|void
name|center
parameter_list|()
block|{
name|super
operator|.
name|center
argument_list|()
expr_stmt|;
name|GlobalKey
operator|.
name|dialog
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|newBranch
operator|.
name|setFocus
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|getDestinationBranch ()
specifier|public
name|String
name|getDestinationBranch
parameter_list|()
block|{
return|return
name|newBranch
operator|.
name|getText
argument_list|()
return|;
block|}
DECL|class|BranchSuggestion
class|class
name|BranchSuggestion
implements|implements
name|Suggestion
block|{
DECL|field|branch
specifier|private
name|BranchInfo
name|branch
decl_stmt|;
DECL|method|BranchSuggestion (BranchInfo branch)
specifier|public
name|BranchSuggestion
parameter_list|(
name|BranchInfo
name|branch
parameter_list|)
block|{
name|this
operator|.
name|branch
operator|=
name|branch
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDisplayString ()
specifier|public
name|String
name|getDisplayString
parameter_list|()
block|{
specifier|final
name|String
name|refsHeads
init|=
literal|"refs/heads/"
decl_stmt|;
if|if
condition|(
name|branch
operator|.
name|ref
argument_list|()
operator|.
name|startsWith
argument_list|(
name|refsHeads
argument_list|)
condition|)
block|{
return|return
name|branch
operator|.
name|ref
argument_list|()
operator|.
name|substring
argument_list|(
name|refsHeads
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
return|return
name|branch
operator|.
name|ref
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getReplacementString ()
specifier|public
name|String
name|getReplacementString
parameter_list|()
block|{
return|return
name|branch
operator|.
name|getShortName
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

