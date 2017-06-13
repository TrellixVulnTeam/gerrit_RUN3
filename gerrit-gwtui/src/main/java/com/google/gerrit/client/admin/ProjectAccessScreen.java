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
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|common
operator|.
name|ProjectAccessUtil
operator|.
name|mergeSections
import|;
end_import

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
name|ProjectAccessUtil
operator|.
name|removeEmptyPermissionsAndSections
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
name|config
operator|.
name|CapabilityInfo
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
name|config
operator|.
name|ConfigServerApi
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
name|CallbackGroup
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
name|AccessSection
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
name|common
operator|.
name|errors
operator|.
name|UpdateParentFailedException
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
name|rpc
operator|.
name|AsyncCallback
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
name|Label
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
name|gwt
operator|.
name|user
operator|.
name|client
operator|.
name|ui
operator|.
name|VerticalPanel
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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtjsonrpc
operator|.
name|client
operator|.
name|RemoteJsonException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
block|{}
DECL|field|editTools
annotation|@
name|UiField
name|DivElement
name|editTools
decl_stmt|;
DECL|field|edit
annotation|@
name|UiField
name|Button
name|edit
decl_stmt|;
DECL|field|cancel1
annotation|@
name|UiField
name|Button
name|cancel1
decl_stmt|;
DECL|field|cancel2
annotation|@
name|UiField
name|Button
name|cancel2
decl_stmt|;
DECL|field|error
annotation|@
name|UiField
name|VerticalPanel
name|error
decl_stmt|;
DECL|field|accessEditor
annotation|@
name|UiField
name|ProjectAccessEditor
name|accessEditor
decl_stmt|;
DECL|field|commitTools
annotation|@
name|UiField
name|DivElement
name|commitTools
decl_stmt|;
DECL|field|commitMessage
annotation|@
name|UiField
name|NpTextArea
name|commitMessage
decl_stmt|;
DECL|field|commit
annotation|@
name|UiField
name|Button
name|commit
decl_stmt|;
DECL|field|review
annotation|@
name|UiField
name|Button
name|review
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
DECL|field|capabilityMap
specifier|private
name|NativeMap
argument_list|<
name|CapabilityInfo
argument_list|>
name|capabilityMap
decl_stmt|;
DECL|method|ProjectAccessScreen (Project.NameKey toShow)
specifier|public
name|ProjectAccessScreen
parameter_list|(
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
name|CallbackGroup
name|cbs
init|=
operator|new
name|CallbackGroup
argument_list|()
decl_stmt|;
name|ConfigServerApi
operator|.
name|capabilities
argument_list|(
name|cbs
operator|.
name|add
argument_list|(
operator|new
name|AsyncCallback
argument_list|<
name|NativeMap
argument_list|<
name|CapabilityInfo
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
name|NativeMap
argument_list|<
name|CapabilityInfo
argument_list|>
name|result
parameter_list|)
block|{
name|capabilityMap
operator|=
name|result
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
comment|// Handled by ScreenLoadCallback.onFailure().
block|}
block|}
argument_list|)
argument_list|)
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
name|cbs
operator|.
name|addFinal
argument_list|(
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
argument_list|)
expr_stmt|;
name|savedPanel
operator|=
name|ACCESS
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
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|allCapabilities
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|CapabilityInfo
name|c
range|:
name|Natives
operator|.
name|asList
argument_list|(
name|capabilityMap
operator|.
name|values
argument_list|()
argument_list|)
control|)
block|{
name|allCapabilities
operator|.
name|put
argument_list|(
name|c
operator|.
name|id
argument_list|()
argument_list|,
name|c
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|access
operator|.
name|setCapabilities
argument_list|(
name|allCapabilities
argument_list|)
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
operator|||
name|access
operator|.
name|canUpload
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
operator|||
name|access
operator|.
name|canUpload
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
DECL|method|onEdit (@uppressWarningsR) ClickEvent event)
name|void
name|onEdit
parameter_list|(
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
name|ClickEvent
name|event
parameter_list|)
block|{
name|resetEditors
argument_list|()
expr_stmt|;
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
name|commit
operator|.
name|setVisible
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
name|review
operator|.
name|setVisible
argument_list|(
name|access
operator|.
name|canUpload
argument_list|()
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
DECL|method|resetEditors ()
specifier|private
name|void
name|resetEditors
parameter_list|()
block|{
comment|// Push an empty instance through the driver before pushing the real
comment|// data. This will force GWT to delete and recreate the editors, which
comment|// is required to build initialize them as editable vs. read-only.
name|ProjectAccess
name|mock
init|=
operator|new
name|ProjectAccess
argument_list|()
decl_stmt|;
name|mock
operator|.
name|setProjectName
argument_list|(
name|access
operator|.
name|getProjectName
argument_list|()
argument_list|)
expr_stmt|;
name|mock
operator|.
name|setRevision
argument_list|(
name|access
operator|.
name|getRevision
argument_list|()
argument_list|)
expr_stmt|;
name|mock
operator|.
name|setLocal
argument_list|(
name|Collections
operator|.
expr|<
name|AccessSection
operator|>
name|emptyList
argument_list|()
argument_list|)
expr_stmt|;
name|mock
operator|.
name|setOwnerOf
argument_list|(
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
name|driver
operator|.
name|edit
argument_list|(
name|mock
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
DECL|method|onCancel (@uppressWarningsR) ClickEvent event)
name|void
name|onCancel
parameter_list|(
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
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
DECL|method|onCommit (@uppressWarningsR) ClickEvent event)
name|void
name|onCommit
parameter_list|(
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
name|ClickEvent
name|event
parameter_list|)
block|{
specifier|final
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
name|AdminConstants
operator|.
name|I
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
name|access
operator|.
name|getInheritsFrom
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
name|newAccess
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
name|error
operator|.
name|clear
argument_list|()
expr_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|diffs
init|=
name|getDiffs
argument_list|(
name|access
argument_list|,
name|newAccess
argument_list|)
decl_stmt|;
if|if
condition|(
name|diffs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|displayReadOnly
argument_list|(
name|newAccess
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|error
operator|.
name|add
argument_list|(
operator|new
name|Label
argument_list|(
name|Gerrit
operator|.
name|C
operator|.
name|projectAccessError
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|diff
range|:
name|diffs
control|)
block|{
name|error
operator|.
name|add
argument_list|(
operator|new
name|Label
argument_list|(
name|diff
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|access
operator|.
name|canUpload
argument_list|()
condition|)
block|{
name|error
operator|.
name|add
argument_list|(
operator|new
name|Label
argument_list|(
name|Gerrit
operator|.
name|C
operator|.
name|projectAccessProposeForReviewHint
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|getDiffs
parameter_list|(
name|ProjectAccess
name|wantedAccess
parameter_list|,
name|ProjectAccess
name|newAccess
parameter_list|)
block|{
name|List
argument_list|<
name|AccessSection
argument_list|>
name|wantedSections
init|=
name|mergeSections
argument_list|(
name|removeEmptyPermissionsAndSections
argument_list|(
name|wantedAccess
operator|.
name|getLocal
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|AccessSection
argument_list|>
name|newSections
init|=
name|removeEmptyPermissionsAndSections
argument_list|(
name|newAccess
operator|.
name|getLocal
argument_list|()
argument_list|)
decl_stmt|;
name|HashSet
argument_list|<
name|AccessSection
argument_list|>
name|same
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|wantedSections
argument_list|)
decl_stmt|;
name|HashSet
argument_list|<
name|AccessSection
argument_list|>
name|different
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|wantedSections
operator|.
name|size
argument_list|()
operator|+
name|newSections
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|different
operator|.
name|addAll
argument_list|(
name|wantedSections
argument_list|)
expr_stmt|;
name|different
operator|.
name|addAll
argument_list|(
name|newSections
argument_list|)
expr_stmt|;
name|same
operator|.
name|retainAll
argument_list|(
name|newSections
argument_list|)
expr_stmt|;
name|different
operator|.
name|removeAll
argument_list|(
name|same
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|differentNames
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|AccessSection
name|s
range|:
name|different
control|)
block|{
name|differentNames
operator|.
name|add
argument_list|(
name|s
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|differentNames
return|;
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
name|error
operator|.
name|clear
argument_list|()
expr_stmt|;
name|enable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|caught
operator|instanceof
name|RemoteJsonException
operator|&&
name|caught
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
name|UpdateParentFailedException
operator|.
name|MESSAGE
argument_list|)
condition|)
block|{
operator|new
name|ErrorDialog
argument_list|(
name|Gerrit
operator|.
name|M
operator|.
name|parentUpdateFailed
argument_list|(
name|caught
operator|.
name|getMessage
argument_list|()
operator|.
name|substring
argument_list|(
name|UpdateParentFailedException
operator|.
name|MESSAGE
operator|.
name|length
argument_list|()
operator|+
literal|1
argument_list|)
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
annotation|@
name|UiHandler
argument_list|(
literal|"review"
argument_list|)
DECL|method|onReview (@uppressWarningsR) ClickEvent event)
name|void
name|onReview
parameter_list|(
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
name|ClickEvent
name|event
parameter_list|)
block|{
specifier|final
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
name|AdminConstants
operator|.
name|I
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
name|reviewProjectAccess
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
name|access
operator|.
name|getInheritsFrom
argument_list|()
argument_list|,
comment|//
operator|new
name|GerritCallback
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|Change
operator|.
name|Id
name|changeId
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
name|error
operator|.
name|clear
argument_list|()
expr_stmt|;
if|if
condition|(
name|changeId
operator|!=
literal|null
condition|)
block|{
name|Gerrit
operator|.
name|display
argument_list|(
name|PageLinks
operator|.
name|toChange
argument_list|(
name|changeId
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|displayReadOnly
argument_list|(
name|access
argument_list|)
expr_stmt|;
block|}
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
name|error
operator|.
name|clear
argument_list|()
expr_stmt|;
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
operator|&&
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
name|review
operator|.
name|setEnabled
argument_list|(
name|enabled
operator|&&
name|access
operator|.
name|canUpload
argument_list|()
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

