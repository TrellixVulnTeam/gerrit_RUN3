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
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|common
operator|.
name|data
operator|.
name|LabelValue
operator|.
name|formatValue
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
name|ConfirmationCallback
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
name|ConfirmationDialog
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
name|ErrorDialog
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
name|account
operator|.
name|AccountInfo
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
name|ChangeInfo
operator|.
name|ApprovalInfo
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
name|ChangeInfo
operator|.
name|LabelInfo
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
name|NativeMap
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
name|NativeString
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
name|client
operator|.
name|ui
operator|.
name|AccountLink
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
name|AddMemberBox
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
name|ReviewerSuggestOracle
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
name|common
operator|.
name|data
operator|.
name|ApprovalDetail
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
name|common
operator|.
name|data
operator|.
name|SubmitRecord
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
name|Account
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
name|JavaScriptObject
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
name|event
operator|.
name|dom
operator|.
name|client
operator|.
name|ClickEvent
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
name|event
operator|.
name|dom
operator|.
name|client
operator|.
name|ClickHandler
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
name|Grid
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
operator|.
name|CellFormatter
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
name|Image
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
name|Panel
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
name|PushButton
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
name|SafeHtmlBuilder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/** Displays a table of {@link ApprovalDetail} objects for a change record. */
end_comment

begin_class
DECL|class|ApprovalTable
specifier|public
class|class
name|ApprovalTable
extends|extends
name|Composite
block|{
DECL|field|table
specifier|private
specifier|final
name|Grid
name|table
decl_stmt|;
DECL|field|missing
specifier|private
specifier|final
name|Widget
name|missing
decl_stmt|;
DECL|field|addReviewer
specifier|private
specifier|final
name|Panel
name|addReviewer
decl_stmt|;
DECL|field|reviewerSuggestOracle
specifier|private
specifier|final
name|ReviewerSuggestOracle
name|reviewerSuggestOracle
decl_stmt|;
DECL|field|addMemberBox
specifier|private
specifier|final
name|AddMemberBox
name|addMemberBox
decl_stmt|;
DECL|field|lastChange
specifier|private
name|ChangeInfo
name|lastChange
decl_stmt|;
DECL|field|rows
specifier|private
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|rows
decl_stmt|;
DECL|method|ApprovalTable ()
specifier|public
name|ApprovalTable
parameter_list|()
block|{
name|rows
operator|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
argument_list|()
expr_stmt|;
name|table
operator|=
operator|new
name|Grid
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|table
operator|.
name|addStyleName
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|infoTable
argument_list|()
argument_list|)
expr_stmt|;
name|missing
operator|=
operator|new
name|Widget
argument_list|()
block|{
block|{
name|setElement
argument_list|(
name|DOM
operator|.
name|createElement
argument_list|(
literal|"ul"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
expr_stmt|;
name|missing
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
name|missingApprovalList
argument_list|()
argument_list|)
expr_stmt|;
name|addReviewer
operator|=
operator|new
name|FlowPanel
argument_list|()
expr_stmt|;
name|addReviewer
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
name|addReviewer
argument_list|()
argument_list|)
expr_stmt|;
name|reviewerSuggestOracle
operator|=
operator|new
name|ReviewerSuggestOracle
argument_list|()
expr_stmt|;
name|addMemberBox
operator|=
operator|new
name|AddMemberBox
argument_list|(
name|Util
operator|.
name|C
operator|.
name|approvalTableAddReviewer
argument_list|()
argument_list|,
name|Util
operator|.
name|C
operator|.
name|approvalTableAddReviewerHint
argument_list|()
argument_list|,
name|reviewerSuggestOracle
argument_list|)
expr_stmt|;
name|addMemberBox
operator|.
name|addClickHandler
argument_list|(
operator|new
name|ClickHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onClick
parameter_list|(
specifier|final
name|ClickEvent
name|event
parameter_list|)
block|{
name|doAddReviewer
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|addReviewer
operator|.
name|add
argument_list|(
name|addMemberBox
argument_list|)
expr_stmt|;
name|addReviewer
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|FlowPanel
name|fp
init|=
operator|new
name|FlowPanel
argument_list|()
decl_stmt|;
name|fp
operator|.
name|add
argument_list|(
name|table
argument_list|)
expr_stmt|;
name|fp
operator|.
name|add
argument_list|(
name|missing
argument_list|)
expr_stmt|;
name|fp
operator|.
name|add
argument_list|(
name|addReviewer
argument_list|)
expr_stmt|;
name|initWidget
argument_list|(
name|fp
argument_list|)
expr_stmt|;
name|setStyleName
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|approvalTable
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|displayHeader (Collection<String> labels)
specifier|private
name|void
name|displayHeader
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|labels
parameter_list|)
block|{
name|table
operator|.
name|resizeColumns
argument_list|(
literal|2
operator|+
name|labels
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|CellFormatter
name|fmt
init|=
name|table
operator|.
name|getCellFormatter
argument_list|()
decl_stmt|;
name|int
name|col
init|=
literal|0
decl_stmt|;
name|table
operator|.
name|setText
argument_list|(
literal|0
argument_list|,
name|col
argument_list|,
name|Util
operator|.
name|C
operator|.
name|approvalTableReviewer
argument_list|()
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|setStyleName
argument_list|(
literal|0
argument_list|,
name|col
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|header
argument_list|()
argument_list|)
expr_stmt|;
name|col
operator|++
expr_stmt|;
name|table
operator|.
name|clearCell
argument_list|(
literal|0
argument_list|,
name|col
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|setStyleName
argument_list|(
literal|0
argument_list|,
name|col
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|header
argument_list|()
argument_list|)
expr_stmt|;
name|col
operator|++
expr_stmt|;
for|for
control|(
name|String
name|name
range|:
name|labels
control|)
block|{
name|table
operator|.
name|setText
argument_list|(
literal|0
argument_list|,
name|col
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|setStyleName
argument_list|(
literal|0
argument_list|,
name|col
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|header
argument_list|()
argument_list|)
expr_stmt|;
name|col
operator|++
expr_stmt|;
block|}
name|fmt
operator|.
name|addStyleName
argument_list|(
literal|0
argument_list|,
name|col
operator|-
literal|1
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|rightmost
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|display (ChangeInfo change)
name|void
name|display
parameter_list|(
name|ChangeInfo
name|change
parameter_list|)
block|{
name|lastChange
operator|=
name|change
expr_stmt|;
name|reviewerSuggestOracle
operator|.
name|setChange
argument_list|(
name|change
operator|.
name|legacy_id
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|ApprovalDetail
argument_list|>
name|byUser
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|Integer
argument_list|,
name|ApprovalDetail
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|AccountInfo
argument_list|>
name|accounts
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|Integer
argument_list|,
name|AccountInfo
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|missingLabels
init|=
name|initLabels
argument_list|(
name|change
argument_list|,
name|accounts
argument_list|,
name|byUser
argument_list|)
decl_stmt|;
name|removeAllChildren
argument_list|(
name|missing
operator|.
name|getElement
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|label
range|:
name|missingLabels
control|)
block|{
name|addMissingLabel
argument_list|(
name|Util
operator|.
name|M
operator|.
name|needApproval
argument_list|(
name|label
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|byUser
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|table
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|displayHeader
argument_list|(
name|change
operator|.
name|labels
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|resizeRows
argument_list|(
literal|1
operator|+
name|byUser
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|i
init|=
literal|1
decl_stmt|;
for|for
control|(
name|ApprovalDetail
name|ad
range|:
name|ApprovalDetail
operator|.
name|sort
argument_list|(
name|byUser
operator|.
name|values
argument_list|()
argument_list|,
name|change
operator|.
name|owner
argument_list|()
operator|.
name|_account_id
argument_list|()
argument_list|)
control|)
block|{
name|displayRow
argument_list|(
name|i
operator|++
argument_list|,
name|ad
argument_list|,
name|change
argument_list|,
name|accounts
operator|.
name|get
argument_list|(
name|ad
operator|.
name|getAccount
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|table
operator|.
name|setVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|Gerrit
operator|.
name|getConfig
argument_list|()
operator|.
name|testChangeMerge
argument_list|()
operator|&&
operator|!
name|change
operator|.
name|mergeable
argument_list|()
condition|)
block|{
name|addMissingLabel
argument_list|(
name|Util
operator|.
name|C
operator|.
name|messageNeedsRebaseOrHasDependency
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|missing
operator|.
name|setVisible
argument_list|(
name|DOM
operator|.
name|getChildCount
argument_list|(
name|missing
operator|.
name|getElement
argument_list|()
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|addReviewer
operator|.
name|setVisible
argument_list|(
name|Gerrit
operator|.
name|isSignedIn
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|removeAllChildren (Element el)
specifier|private
name|void
name|removeAllChildren
parameter_list|(
name|Element
name|el
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
name|DOM
operator|.
name|getChildCount
argument_list|(
name|el
argument_list|)
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|DOM
operator|.
name|removeChild
argument_list|(
name|el
argument_list|,
name|DOM
operator|.
name|getChild
argument_list|(
name|el
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addMissingLabel (String text)
specifier|private
name|void
name|addMissingLabel
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|Element
name|li
init|=
name|DOM
operator|.
name|createElement
argument_list|(
literal|"li"
argument_list|)
decl_stmt|;
name|li
operator|.
name|setClassName
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|missingApproval
argument_list|()
argument_list|)
expr_stmt|;
name|DOM
operator|.
name|setInnerText
argument_list|(
name|li
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|DOM
operator|.
name|appendChild
argument_list|(
name|missing
operator|.
name|getElement
argument_list|()
argument_list|,
name|li
argument_list|)
expr_stmt|;
block|}
DECL|method|removableReviewers (ChangeInfo change)
specifier|private
name|Set
argument_list|<
name|Integer
argument_list|>
name|removableReviewers
parameter_list|(
name|ChangeInfo
name|change
parameter_list|)
block|{
name|Set
argument_list|<
name|Integer
argument_list|>
name|result
init|=
operator|new
name|HashSet
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|change
operator|.
name|removable_reviewers
argument_list|()
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|change
operator|.
name|removable_reviewers
argument_list|()
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|result
operator|.
name|add
argument_list|(
name|change
operator|.
name|removable_reviewers
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|_account_id
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|initLabels (ChangeInfo change, Map<Integer, AccountInfo> accounts, Map<Integer, ApprovalDetail> byUser)
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|initLabels
parameter_list|(
name|ChangeInfo
name|change
parameter_list|,
name|Map
argument_list|<
name|Integer
argument_list|,
name|AccountInfo
argument_list|>
name|accounts
parameter_list|,
name|Map
argument_list|<
name|Integer
argument_list|,
name|ApprovalDetail
argument_list|>
name|byUser
parameter_list|)
block|{
name|Set
argument_list|<
name|Integer
argument_list|>
name|removableReviewers
init|=
name|removableReviewers
argument_list|(
name|change
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|missing
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|change
operator|.
name|labels
argument_list|()
control|)
block|{
name|LabelInfo
name|label
init|=
name|change
operator|.
name|label
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|String
name|min
init|=
literal|null
decl_stmt|;
name|String
name|max
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|v
range|:
name|label
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|min
operator|==
literal|null
condition|)
block|{
name|min
operator|=
name|v
expr_stmt|;
block|}
if|if
condition|(
name|v
operator|.
name|startsWith
argument_list|(
literal|"+"
argument_list|)
condition|)
block|{
name|max
operator|=
name|v
expr_stmt|;
block|}
block|}
if|if
condition|(
name|label
operator|.
name|status
argument_list|()
operator|==
name|SubmitRecord
operator|.
name|Label
operator|.
name|Status
operator|.
name|NEED
condition|)
block|{
name|missing
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|label
operator|.
name|all
argument_list|()
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|ApprovalInfo
name|ai
range|:
name|Natives
operator|.
name|asList
argument_list|(
name|label
operator|.
name|all
argument_list|()
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|accounts
operator|.
name|containsKey
argument_list|(
name|ai
operator|.
name|_account_id
argument_list|()
argument_list|)
condition|)
block|{
name|accounts
operator|.
name|put
argument_list|(
name|ai
operator|.
name|_account_id
argument_list|()
argument_list|,
name|ai
argument_list|)
expr_stmt|;
block|}
name|int
name|id
init|=
name|ai
operator|.
name|_account_id
argument_list|()
decl_stmt|;
name|ApprovalDetail
name|ad
init|=
name|byUser
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|ad
operator|==
literal|null
condition|)
block|{
name|ad
operator|=
operator|new
name|ApprovalDetail
argument_list|(
operator|new
name|Account
operator|.
name|Id
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|ad
operator|.
name|setCanRemove
argument_list|(
name|removableReviewers
operator|.
name|contains
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|byUser
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|ad
argument_list|)
expr_stmt|;
block|}
name|ad
operator|.
name|votable
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|ad
operator|.
name|value
argument_list|(
name|name
argument_list|,
name|ai
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|formatValue
argument_list|(
name|ai
operator|.
name|value
argument_list|()
argument_list|)
operator|.
name|equals
argument_list|(
name|max
argument_list|)
condition|)
block|{
name|ad
operator|.
name|approved
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|formatValue
argument_list|(
name|ai
operator|.
name|value
argument_list|()
argument_list|)
operator|.
name|equals
argument_list|(
name|min
argument_list|)
condition|)
block|{
name|ad
operator|.
name|rejected
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|missing
return|;
block|}
DECL|method|doAddReviewer ()
specifier|private
name|void
name|doAddReviewer
parameter_list|()
block|{
name|String
name|reviewer
init|=
name|addMemberBox
operator|.
name|getText
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|reviewer
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|addMemberBox
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|addReviewer
argument_list|(
name|reviewer
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|PostInput
specifier|private
specifier|static
class|class
name|PostInput
extends|extends
name|JavaScriptObject
block|{
DECL|method|create (String reviewer, boolean confirmed)
specifier|static
name|PostInput
name|create
parameter_list|(
name|String
name|reviewer
parameter_list|,
name|boolean
name|confirmed
parameter_list|)
block|{
name|PostInput
name|input
init|=
name|createObject
argument_list|()
operator|.
name|cast
argument_list|()
decl_stmt|;
name|input
operator|.
name|init
argument_list|(
name|reviewer
argument_list|,
name|confirmed
argument_list|)
expr_stmt|;
return|return
name|input
return|;
block|}
DECL|method|init (String reviewer, boolean confirmed)
specifier|private
specifier|native
name|void
name|init
parameter_list|(
name|String
name|reviewer
parameter_list|,
name|boolean
name|confirmed
parameter_list|)
comment|/*-{       this.reviewer = reviewer;       if (confirmed) {         this.confirmed = true;       }     }-*/
function_decl|;
DECL|method|PostInput ()
specifier|protected
name|PostInput
parameter_list|()
block|{     }
block|}
DECL|class|ReviewerInfo
specifier|private
specifier|static
class|class
name|ReviewerInfo
extends|extends
name|AccountInfo
block|{
DECL|method|approvals ()
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|approvals
parameter_list|()
block|{
return|return
name|Natives
operator|.
name|keys
argument_list|(
name|_approvals
argument_list|()
argument_list|)
return|;
block|}
DECL|method|approval (String l)
specifier|final
specifier|native
name|String
name|approval
parameter_list|(
name|String
name|l
parameter_list|)
comment|/*-{ return this.approvals[l]; }-*/
function_decl|;
DECL|method|_approvals ()
specifier|private
specifier|final
specifier|native
name|NativeMap
argument_list|<
name|NativeString
argument_list|>
name|_approvals
parameter_list|()
comment|/*-{ return this.approvals; }-*/
function_decl|;
DECL|method|ReviewerInfo ()
specifier|protected
name|ReviewerInfo
parameter_list|()
block|{     }
block|}
DECL|class|PostResult
specifier|private
specifier|static
class|class
name|PostResult
extends|extends
name|JavaScriptObject
block|{
DECL|method|reviewers ()
specifier|final
specifier|native
name|JsArray
argument_list|<
name|ReviewerInfo
argument_list|>
name|reviewers
parameter_list|()
comment|/*-{ return this.reviewers; }-*/
function_decl|;
DECL|method|confirm ()
specifier|final
specifier|native
name|boolean
name|confirm
parameter_list|()
comment|/*-{ return this.confirm || false; }-*/
function_decl|;
DECL|method|error ()
specifier|final
specifier|native
name|String
name|error
parameter_list|()
comment|/*-{ return this.error; }-*/
function_decl|;
DECL|method|PostResult ()
specifier|protected
name|PostResult
parameter_list|()
block|{     }
block|}
DECL|method|addReviewer (final String reviewer, boolean confirmed)
specifier|private
name|void
name|addReviewer
parameter_list|(
specifier|final
name|String
name|reviewer
parameter_list|,
name|boolean
name|confirmed
parameter_list|)
block|{
name|ChangeApi
operator|.
name|reviewers
argument_list|(
name|lastChange
operator|.
name|legacy_id
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|post
argument_list|(
name|PostInput
operator|.
name|create
argument_list|(
name|reviewer
argument_list|,
name|confirmed
argument_list|)
argument_list|,
operator|new
name|GerritCallback
argument_list|<
name|PostResult
argument_list|>
argument_list|()
block|{
specifier|public
name|void
name|onSuccess
parameter_list|(
name|PostResult
name|result
parameter_list|)
block|{
name|addMemberBox
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|addMemberBox
operator|.
name|setText
argument_list|(
literal|""
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|.
name|error
argument_list|()
operator|==
literal|null
condition|)
block|{
name|reload
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|result
operator|.
name|confirm
argument_list|()
condition|)
block|{
name|askForConfirmation
argument_list|(
name|result
operator|.
name|error
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
operator|new
name|ErrorDialog
argument_list|(
operator|new
name|SafeHtmlBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|result
operator|.
name|error
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|askForConfirmation
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|String
name|title
init|=
name|Util
operator|.
name|C
operator|.
name|approvalTableAddManyReviewersConfirmationDialogTitle
argument_list|()
decl_stmt|;
name|ConfirmationDialog
name|confirmationDialog
init|=
operator|new
name|ConfirmationDialog
argument_list|(
name|title
argument_list|,
operator|new
name|SafeHtmlBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|text
argument_list|)
argument_list|,
operator|new
name|ConfirmationCallback
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onOk
parameter_list|()
block|{
name|addReviewer
argument_list|(
name|reviewer
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|confirmationDialog
operator|.
name|center
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
specifier|final
name|Throwable
name|caught
parameter_list|)
block|{
name|addMemberBox
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|isNoSuchEntity
argument_list|(
name|caught
argument_list|)
condition|)
block|{
operator|new
name|ErrorDialog
argument_list|(
name|Util
operator|.
name|M
operator|.
name|reviewerNotFound
argument_list|(
name|reviewer
argument_list|)
argument_list|)
operator|.
name|center
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|onFailure
argument_list|(
name|caught
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|displayRow (int row, final ApprovalDetail ad, ChangeInfo change, AccountInfo account)
specifier|private
name|void
name|displayRow
parameter_list|(
name|int
name|row
parameter_list|,
specifier|final
name|ApprovalDetail
name|ad
parameter_list|,
name|ChangeInfo
name|change
parameter_list|,
name|AccountInfo
name|account
parameter_list|)
block|{
specifier|final
name|CellFormatter
name|fmt
init|=
name|table
operator|.
name|getCellFormatter
argument_list|()
decl_stmt|;
name|int
name|col
init|=
literal|0
decl_stmt|;
name|table
operator|.
name|setWidget
argument_list|(
name|row
argument_list|,
name|col
operator|++
argument_list|,
operator|new
name|AccountLink
argument_list|(
name|account
argument_list|)
argument_list|)
expr_stmt|;
name|rows
operator|.
name|put
argument_list|(
name|account
operator|.
name|_account_id
argument_list|()
argument_list|,
name|row
argument_list|)
expr_stmt|;
if|if
condition|(
name|ad
operator|.
name|canRemove
argument_list|()
condition|)
block|{
specifier|final
name|PushButton
name|remove
init|=
operator|new
name|PushButton
argument_list|(
comment|//
operator|new
name|Image
argument_list|(
name|Util
operator|.
name|R
operator|.
name|removeReviewerNormal
argument_list|()
argument_list|)
argument_list|,
comment|//
operator|new
name|Image
argument_list|(
name|Util
operator|.
name|R
operator|.
name|removeReviewerPressed
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|remove
operator|.
name|setTitle
argument_list|(
name|Util
operator|.
name|M
operator|.
name|removeReviewer
argument_list|(
name|account
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|remove
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
name|removeReviewer
argument_list|()
argument_list|)
expr_stmt|;
name|remove
operator|.
name|addStyleName
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|link
argument_list|()
argument_list|)
expr_stmt|;
name|remove
operator|.
name|addClickHandler
argument_list|(
operator|new
name|ClickHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onClick
parameter_list|(
name|ClickEvent
name|event
parameter_list|)
block|{
name|doRemove
argument_list|(
name|ad
argument_list|,
name|remove
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|table
operator|.
name|setWidget
argument_list|(
name|row
argument_list|,
name|col
argument_list|,
name|remove
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|table
operator|.
name|clearCell
argument_list|(
name|row
argument_list|,
name|col
argument_list|)
expr_stmt|;
block|}
name|fmt
operator|.
name|setStyleName
argument_list|(
name|row
argument_list|,
name|col
operator|++
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|removeReviewerCell
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|labelName
range|:
name|change
operator|.
name|labels
argument_list|()
control|)
block|{
name|fmt
operator|.
name|setStyleName
argument_list|(
name|row
argument_list|,
name|col
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|approvalscore
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|ad
operator|.
name|canVote
argument_list|(
name|labelName
argument_list|)
condition|)
block|{
name|fmt
operator|.
name|addStyleName
argument_list|(
name|row
argument_list|,
name|col
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|notVotable
argument_list|()
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|getElement
argument_list|(
name|row
argument_list|,
name|col
argument_list|)
operator|.
name|setTitle
argument_list|(
name|Gerrit
operator|.
name|C
operator|.
name|userCannotVoteToolTip
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ad
operator|.
name|isRejected
argument_list|(
name|labelName
argument_list|)
condition|)
block|{
name|table
operator|.
name|setWidget
argument_list|(
name|row
argument_list|,
name|col
argument_list|,
operator|new
name|Image
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|redNot
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ad
operator|.
name|isApproved
argument_list|(
name|labelName
argument_list|)
condition|)
block|{
name|table
operator|.
name|setWidget
argument_list|(
name|row
argument_list|,
name|col
argument_list|,
operator|new
name|Image
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|greenCheck
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|v
init|=
name|ad
operator|.
name|getValue
argument_list|(
name|labelName
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|0
condition|)
block|{
name|table
operator|.
name|clearCell
argument_list|(
name|row
argument_list|,
name|col
argument_list|)
expr_stmt|;
name|col
operator|++
expr_stmt|;
continue|continue;
block|}
name|String
name|vstr
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|ad
operator|.
name|getValue
argument_list|(
name|labelName
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|>
literal|0
condition|)
block|{
name|vstr
operator|=
literal|"+"
operator|+
name|vstr
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
name|row
argument_list|,
name|col
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|posscore
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fmt
operator|.
name|addStyleName
argument_list|(
name|row
argument_list|,
name|col
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|negscore
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|table
operator|.
name|setText
argument_list|(
name|row
argument_list|,
name|col
argument_list|,
name|vstr
argument_list|)
expr_stmt|;
block|}
name|col
operator|++
expr_stmt|;
block|}
name|fmt
operator|.
name|addStyleName
argument_list|(
name|row
argument_list|,
name|col
operator|-
literal|1
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|rightmost
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|reload ()
specifier|private
name|void
name|reload
parameter_list|()
block|{
name|ChangeApi
operator|.
name|detail
argument_list|(
name|lastChange
operator|.
name|legacy_id
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
operator|new
name|GerritCallback
argument_list|<
name|ChangeInfo
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|ChangeInfo
name|result
parameter_list|)
block|{
name|display
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|doRemove (ApprovalDetail ad, final PushButton remove)
specifier|private
name|void
name|doRemove
parameter_list|(
name|ApprovalDetail
name|ad
parameter_list|,
specifier|final
name|PushButton
name|remove
parameter_list|)
block|{
name|remove
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|ChangeApi
operator|.
name|reviewer
argument_list|(
name|lastChange
operator|.
name|legacy_id
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|ad
operator|.
name|getAccount
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|delete
argument_list|(
operator|new
name|GerritCallback
argument_list|<
name|JavaScriptObject
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|JavaScriptObject
name|result
parameter_list|)
block|{
name|reload
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
specifier|final
name|Throwable
name|caught
parameter_list|)
block|{
name|remove
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|super
operator|.
name|onFailure
argument_list|(
name|caught
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

