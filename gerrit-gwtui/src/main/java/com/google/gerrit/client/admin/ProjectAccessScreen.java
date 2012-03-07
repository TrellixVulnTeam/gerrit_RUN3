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
name|ScreenLoadCallback
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
name|PageLinks
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
name|ProjectAccess
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
name|DivElement
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
name|SimpleBeanEditorDriver
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
name|Window
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
name|HTMLPanel
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
name|UIObject
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
DECL|class|ProjectAccessScreen
specifier|public
class|class
name|ProjectAccessScreen
extends|extends
name|ProjectScreen
block|{
DECL|interface|Binder
interface|interface
name|Binder
extends|extends
name|UiBinder
argument_list|<
name|HTMLPanel
argument_list|,
name|ProjectAccessScreen
argument_list|>
block|{   }
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
DECL|interface|Driver
interface|interface
name|Driver
extends|extends
name|SimpleBeanEditorDriver
argument_list|<
comment|//
name|ProjectAccess
argument_list|,
comment|//
name|ProjectAccessEditor
argument_list|>
block|{   }
annotation|@
name|UiField
DECL|field|editTools
name|DivElement
name|editTools
decl_stmt|;
annotation|@
name|UiField
DECL|field|edit
name|Button
name|edit
decl_stmt|;
annotation|@
name|UiField
DECL|field|cancel1
name|Button
name|cancel1
decl_stmt|;
annotation|@
name|UiField
DECL|field|cancel2
name|Button
name|cancel2
decl_stmt|;
annotation|@
name|UiField
DECL|field|accessEditor
name|ProjectAccessEditor
name|accessEditor
decl_stmt|;
annotation|@
name|UiField
DECL|field|commitTools
name|DivElement
name|commitTools
decl_stmt|;
annotation|@
name|UiField
DECL|field|commitMessage
name|NpTextArea
name|commitMessage
decl_stmt|;
annotation|@
name|UiField
DECL|field|commit
name|Button
name|commit
decl_stmt|;
DECL|field|driver
specifier|private
name|Driver
name|driver
decl_stmt|;
DECL|field|access
specifier|private
name|ProjectAccess
name|access
decl_stmt|;
DECL|method|ProjectAccessScreen (final Project.NameKey toShow)
specifier|public
name|ProjectAccessScreen
parameter_list|(
specifier|final
name|Project
operator|.
name|NameKey
name|toShow
parameter_list|)
block|{
name|super
argument_list|(
name|toShow
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onInitUI ()
specifier|protected
name|void
name|onInitUI
parameter_list|()
block|{
name|super
operator|.
name|onInitUI
argument_list|()
expr_stmt|;
name|add
argument_list|(
name|uiBinder
operator|.
name|createAndBindUi
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
name|driver
operator|=
name|GWT
operator|.
name|create
argument_list|(
name|Driver
operator|.
name|class
argument_list|)
expr_stmt|;
name|accessEditor
operator|.
name|setEditing
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|driver
operator|.
name|initialize
argument_list|(
name|accessEditor
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onLoad ()
specifier|protected
name|void
name|onLoad
parameter_list|()
block|{
name|super
operator|.
name|onLoad
argument_list|()
expr_stmt|;
name|Util
operator|.
name|PROJECT_SVC
operator|.
name|projectAccess
argument_list|(
name|getProjectKey
argument_list|()
argument_list|,
operator|new
name|ScreenLoadCallback
argument_list|<
name|ProjectAccess
argument_list|>
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|preDisplay
parameter_list|(
name|ProjectAccess
name|access
parameter_list|)
block|{
name|displayReadOnly
argument_list|(
name|access
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|displayReadOnly (ProjectAccess access)
specifier|private
name|void
name|displayReadOnly
parameter_list|(
name|ProjectAccess
name|access
parameter_list|)
block|{
name|this
operator|.
name|access
operator|=
name|access
expr_stmt|;
name|accessEditor
operator|.
name|setEditing
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|UIObject
operator|.
name|setVisible
argument_list|(
name|editTools
argument_list|,
operator|!
name|access
operator|.
name|getOwnerOf
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|edit
operator|.
name|setEnabled
argument_list|(
operator|!
name|access
operator|.
name|getOwnerOf
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|cancel1
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|UIObject
operator|.
name|setVisible
argument_list|(
name|commitTools
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|driver
operator|.
name|edit
argument_list|(
name|access
argument_list|)
expr_stmt|;
block|}
annotation|@
name|UiHandler
argument_list|(
literal|"edit"
argument_list|)
DECL|method|onEdit (ClickEvent event)
name|void
name|onEdit
parameter_list|(
name|ClickEvent
name|event
parameter_list|)
block|{
name|edit
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|cancel1
operator|.
name|setVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|UIObject
operator|.
name|setVisible
argument_list|(
name|commitTools
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|accessEditor
operator|.
name|setEditing
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|driver
operator|.
name|edit
argument_list|(
name|access
argument_list|)
expr_stmt|;
block|}
annotation|@
name|UiHandler
argument_list|(
name|value
operator|=
block|{
literal|"cancel1"
block|,
literal|"cancel2"
block|}
argument_list|)
DECL|method|onCancel (ClickEvent event)
name|void
name|onCancel
parameter_list|(
name|ClickEvent
name|event
parameter_list|)
block|{
name|Gerrit
operator|.
name|display
argument_list|(
name|PageLinks
operator|.
name|toProjectAcceess
argument_list|(
name|getProjectKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|UiHandler
argument_list|(
literal|"commit"
argument_list|)
DECL|method|onCommit (ClickEvent event)
name|void
name|onCommit
parameter_list|(
name|ClickEvent
name|event
parameter_list|)
block|{
name|ProjectAccess
name|access
init|=
name|driver
operator|.
name|flush
argument_list|()
decl_stmt|;
if|if
condition|(
name|driver
operator|.
name|hasErrors
argument_list|()
condition|)
block|{
name|Window
operator|.
name|alert
argument_list|(
name|Util
operator|.
name|C
operator|.
name|errorsMustBeFixed
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
name|message
init|=
name|commitMessage
operator|.
name|getText
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
literal|""
operator|.
name|equals
argument_list|(
name|message
argument_list|)
condition|)
block|{
name|message
operator|=
literal|null
expr_stmt|;
block|}
name|enable
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Util
operator|.
name|PROJECT_SVC
operator|.
name|changeProjectAccess
argument_list|(
comment|//
name|getProjectKey
argument_list|()
argument_list|,
comment|//
name|access
operator|.
name|getRevision
argument_list|()
argument_list|,
comment|//
name|message
argument_list|,
comment|//
name|access
operator|.
name|getLocal
argument_list|()
argument_list|,
comment|//
operator|new
name|GerritCallback
argument_list|<
name|ProjectAccess
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|ProjectAccess
name|access
parameter_list|)
block|{
name|enable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|commitMessage
operator|.
name|setText
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|displayReadOnly
argument_list|(
name|access
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|caught
parameter_list|)
block|{
name|enable
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
DECL|method|enable (boolean enabled)
specifier|private
name|void
name|enable
parameter_list|(
name|boolean
name|enabled
parameter_list|)
block|{
name|commitMessage
operator|.
name|setEnabled
argument_list|(
name|enabled
argument_list|)
expr_stmt|;
name|commit
operator|.
name|setEnabled
argument_list|(
name|enabled
argument_list|)
expr_stmt|;
name|cancel1
operator|.
name|setEnabled
argument_list|(
name|enabled
argument_list|)
expr_stmt|;
name|cancel2
operator|.
name|setEnabled
argument_list|(
name|enabled
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

