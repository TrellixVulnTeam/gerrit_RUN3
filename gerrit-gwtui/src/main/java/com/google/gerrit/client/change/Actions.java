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
name|actions
operator|.
name|ActionButton
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
name|actions
operator|.
name|ActionInfo
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
name|CommitInfo
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
name|RevisionInfo
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
name|reviewdb
operator|.
name|client
operator|.
name|Change
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
name|uibinder
operator|.
name|client
operator|.
name|UiHandler
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
name|Button
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
name|java
operator|.
name|util
operator|.
name|TreeSet
import|;
end_import

begin_class
DECL|class|Actions
class|class
name|Actions
extends|extends
name|Composite
block|{
DECL|field|CORE
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|CORE
init|=
block|{
literal|"abandon"
block|,
literal|"restore"
block|,
literal|"revert"
block|,
literal|"topic"
block|,
literal|"cherrypick"
block|,
literal|"submit"
block|,
literal|"rebase"
block|,
literal|"message"
block|}
decl_stmt|;
DECL|interface|Binder
interface|interface
name|Binder
extends|extends
name|UiBinder
argument_list|<
name|FlowPanel
argument_list|,
name|Actions
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
DECL|field|cherrypick
annotation|@
name|UiField
name|Button
name|cherrypick
decl_stmt|;
DECL|field|rebase
annotation|@
name|UiField
name|Button
name|rebase
decl_stmt|;
DECL|field|revert
annotation|@
name|UiField
name|Button
name|revert
decl_stmt|;
DECL|field|submit
annotation|@
name|UiField
name|Button
name|submit
decl_stmt|;
DECL|field|abandon
annotation|@
name|UiField
name|Button
name|abandon
decl_stmt|;
DECL|field|abandonAction
specifier|private
name|AbandonAction
name|abandonAction
decl_stmt|;
DECL|field|restore
annotation|@
name|UiField
name|Button
name|restore
decl_stmt|;
DECL|field|restoreAction
specifier|private
name|RestoreAction
name|restoreAction
decl_stmt|;
DECL|field|changeId
specifier|private
name|Change
operator|.
name|Id
name|changeId
decl_stmt|;
DECL|field|changeInfo
specifier|private
name|ChangeInfo
name|changeInfo
decl_stmt|;
DECL|field|revision
specifier|private
name|String
name|revision
decl_stmt|;
DECL|field|project
specifier|private
name|String
name|project
decl_stmt|;
DECL|field|subject
specifier|private
name|String
name|subject
decl_stmt|;
DECL|field|message
specifier|private
name|String
name|message
decl_stmt|;
DECL|field|canSubmit
specifier|private
name|boolean
name|canSubmit
decl_stmt|;
DECL|method|Actions ()
name|Actions
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
name|getElement
argument_list|()
operator|.
name|setId
argument_list|(
literal|"change_actions"
argument_list|)
expr_stmt|;
block|}
DECL|method|display (ChangeInfo info, String revision)
name|void
name|display
parameter_list|(
name|ChangeInfo
name|info
parameter_list|,
name|String
name|revision
parameter_list|)
block|{
name|this
operator|.
name|revision
operator|=
name|revision
expr_stmt|;
name|boolean
name|hasUser
init|=
name|Gerrit
operator|.
name|isSignedIn
argument_list|()
decl_stmt|;
name|RevisionInfo
name|revInfo
init|=
name|info
operator|.
name|revision
argument_list|(
name|revision
argument_list|)
decl_stmt|;
name|CommitInfo
name|commit
init|=
name|revInfo
operator|.
name|commit
argument_list|()
decl_stmt|;
name|changeId
operator|=
name|info
operator|.
name|legacy_id
argument_list|()
expr_stmt|;
name|project
operator|=
name|info
operator|.
name|project
argument_list|()
expr_stmt|;
name|subject
operator|=
name|commit
operator|.
name|subject
argument_list|()
expr_stmt|;
name|message
operator|=
name|commit
operator|.
name|message
argument_list|()
expr_stmt|;
name|changeInfo
operator|=
name|info
expr_stmt|;
name|initChangeActions
argument_list|(
name|info
argument_list|,
name|hasUser
argument_list|)
expr_stmt|;
name|initRevisionActions
argument_list|(
name|info
argument_list|,
name|revInfo
argument_list|,
name|hasUser
argument_list|)
expr_stmt|;
block|}
DECL|method|initChangeActions (ChangeInfo info, boolean hasUser)
specifier|private
name|void
name|initChangeActions
parameter_list|(
name|ChangeInfo
name|info
parameter_list|,
name|boolean
name|hasUser
parameter_list|)
block|{
name|NativeMap
argument_list|<
name|ActionInfo
argument_list|>
name|actions
init|=
name|info
operator|.
name|has_actions
argument_list|()
condition|?
name|info
operator|.
name|actions
argument_list|()
else|:
name|NativeMap
operator|.
expr|<
name|ActionInfo
operator|>
name|create
argument_list|()
decl_stmt|;
name|actions
operator|.
name|copyKeysIntoChildren
argument_list|(
literal|"id"
argument_list|)
expr_stmt|;
name|abandon
operator|.
name|setVisible
argument_list|(
name|hasUser
operator|&&
name|actions
operator|.
name|containsKey
argument_list|(
literal|"abandon"
argument_list|)
argument_list|)
expr_stmt|;
name|restore
operator|.
name|setVisible
argument_list|(
name|hasUser
operator|&&
name|actions
operator|.
name|containsKey
argument_list|(
literal|"restore"
argument_list|)
argument_list|)
expr_stmt|;
name|revert
operator|.
name|setVisible
argument_list|(
name|hasUser
operator|&&
name|actions
operator|.
name|containsKey
argument_list|(
literal|"revert"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasUser
condition|)
block|{
for|for
control|(
name|String
name|id
range|:
name|filterNonCore
argument_list|(
name|actions
argument_list|)
control|)
block|{
name|add
argument_list|(
operator|new
name|ActionButton
argument_list|(
name|info
argument_list|,
name|actions
operator|.
name|get
argument_list|(
name|id
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|initRevisionActions (ChangeInfo info, RevisionInfo revInfo, boolean hasUser)
specifier|private
name|void
name|initRevisionActions
parameter_list|(
name|ChangeInfo
name|info
parameter_list|,
name|RevisionInfo
name|revInfo
parameter_list|,
name|boolean
name|hasUser
parameter_list|)
block|{
name|NativeMap
argument_list|<
name|ActionInfo
argument_list|>
name|actions
init|=
name|revInfo
operator|.
name|has_actions
argument_list|()
condition|?
name|revInfo
operator|.
name|actions
argument_list|()
else|:
name|NativeMap
operator|.
expr|<
name|ActionInfo
operator|>
name|create
argument_list|()
decl_stmt|;
name|actions
operator|.
name|copyKeysIntoChildren
argument_list|(
literal|"id"
argument_list|)
expr_stmt|;
name|cherrypick
operator|.
name|setVisible
argument_list|(
name|hasUser
operator|&&
name|actions
operator|.
name|containsKey
argument_list|(
literal|"cherrypick"
argument_list|)
argument_list|)
expr_stmt|;
name|rebase
operator|.
name|setVisible
argument_list|(
name|hasUser
operator|&&
name|actions
operator|.
name|containsKey
argument_list|(
literal|"rebase"
argument_list|)
argument_list|)
expr_stmt|;
name|canSubmit
operator|=
name|hasUser
operator|&&
name|actions
operator|.
name|containsKey
argument_list|(
literal|"submit"
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasUser
condition|)
block|{
for|for
control|(
name|String
name|id
range|:
name|filterNonCore
argument_list|(
name|actions
argument_list|)
control|)
block|{
name|add
argument_list|(
operator|new
name|ActionButton
argument_list|(
name|info
argument_list|,
name|revInfo
argument_list|,
name|actions
operator|.
name|get
argument_list|(
name|id
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|add (ActionButton b)
specifier|private
name|void
name|add
parameter_list|(
name|ActionButton
name|b
parameter_list|)
block|{
operator|(
operator|(
name|FlowPanel
operator|)
name|getWidget
argument_list|()
operator|)
operator|.
name|add
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
DECL|method|filterNonCore (NativeMap<ActionInfo> m)
specifier|private
specifier|static
name|TreeSet
argument_list|<
name|String
argument_list|>
name|filterNonCore
parameter_list|(
name|NativeMap
argument_list|<
name|ActionInfo
argument_list|>
name|m
parameter_list|)
block|{
name|TreeSet
argument_list|<
name|String
argument_list|>
name|ids
init|=
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|m
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|id
range|:
name|CORE
control|)
block|{
name|ids
operator|.
name|remove
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
return|return
name|ids
return|;
block|}
DECL|method|setSubmitEnabled (boolean ok)
name|void
name|setSubmitEnabled
parameter_list|(
name|boolean
name|ok
parameter_list|)
block|{
name|submit
operator|.
name|setVisible
argument_list|(
name|ok
operator|&&
name|canSubmit
argument_list|)
expr_stmt|;
block|}
DECL|method|isSubmitEnabled ()
name|boolean
name|isSubmitEnabled
parameter_list|()
block|{
return|return
name|submit
operator|.
name|isVisible
argument_list|()
operator|&&
name|submit
operator|.
name|isEnabled
argument_list|()
return|;
block|}
annotation|@
name|UiHandler
argument_list|(
literal|"abandon"
argument_list|)
DECL|method|onAbandon (ClickEvent e)
name|void
name|onAbandon
parameter_list|(
name|ClickEvent
name|e
parameter_list|)
block|{
if|if
condition|(
name|abandonAction
operator|==
literal|null
condition|)
block|{
name|abandonAction
operator|=
operator|new
name|AbandonAction
argument_list|(
name|abandon
argument_list|,
name|changeId
argument_list|)
expr_stmt|;
block|}
name|abandonAction
operator|.
name|show
argument_list|()
expr_stmt|;
block|}
annotation|@
name|UiHandler
argument_list|(
literal|"restore"
argument_list|)
DECL|method|onRestore (ClickEvent e)
name|void
name|onRestore
parameter_list|(
name|ClickEvent
name|e
parameter_list|)
block|{
if|if
condition|(
name|restoreAction
operator|==
literal|null
condition|)
block|{
name|restoreAction
operator|=
operator|new
name|RestoreAction
argument_list|(
name|restore
argument_list|,
name|changeId
argument_list|)
expr_stmt|;
block|}
name|restoreAction
operator|.
name|show
argument_list|()
expr_stmt|;
block|}
annotation|@
name|UiHandler
argument_list|(
literal|"rebase"
argument_list|)
DECL|method|onRebase (ClickEvent e)
name|void
name|onRebase
parameter_list|(
name|ClickEvent
name|e
parameter_list|)
block|{
name|RebaseAction
operator|.
name|call
argument_list|(
name|changeId
argument_list|,
name|revision
argument_list|)
expr_stmt|;
block|}
annotation|@
name|UiHandler
argument_list|(
literal|"submit"
argument_list|)
DECL|method|onSubmit (ClickEvent e)
name|void
name|onSubmit
parameter_list|(
name|ClickEvent
name|e
parameter_list|)
block|{
name|SubmitAction
operator|.
name|call
argument_list|(
name|changeId
argument_list|,
name|revision
argument_list|)
expr_stmt|;
block|}
annotation|@
name|UiHandler
argument_list|(
literal|"cherrypick"
argument_list|)
DECL|method|onCherryPick (ClickEvent e)
name|void
name|onCherryPick
parameter_list|(
name|ClickEvent
name|e
parameter_list|)
block|{
name|CherryPickAction
operator|.
name|call
argument_list|(
name|cherrypick
argument_list|,
name|changeInfo
argument_list|,
name|revision
argument_list|,
name|project
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
annotation|@
name|UiHandler
argument_list|(
literal|"revert"
argument_list|)
DECL|method|onRevert (ClickEvent e)
name|void
name|onRevert
parameter_list|(
name|ClickEvent
name|e
parameter_list|)
block|{
name|RevertAction
operator|.
name|call
argument_list|(
name|cherrypick
argument_list|,
name|changeId
argument_list|,
name|revision
argument_list|,
name|project
argument_list|,
name|subject
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

