begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2014 The Android Open Source Project
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
DECL|package|com.google.gerrit.client.editor
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|editor
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
name|VoidResult
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
name|DiffPreferences
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
name|ChangeFileApi
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
name|client
operator|.
name|ui
operator|.
name|Screen
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
name|reviewdb
operator|.
name|client
operator|.
name|Patch
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
name|KeyPressEvent
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
name|net
operator|.
name|codemirror
operator|.
name|lib
operator|.
name|CodeMirror
import|;
end_import

begin_import
import|import
name|net
operator|.
name|codemirror
operator|.
name|lib
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|net
operator|.
name|codemirror
operator|.
name|lib
operator|.
name|ModeInjector
import|;
end_import

begin_class
DECL|class|EditScreen
specifier|public
class|class
name|EditScreen
extends|extends
name|Screen
block|{
DECL|interface|Binder
interface|interface
name|Binder
extends|extends
name|UiBinder
argument_list|<
name|HTMLPanel
argument_list|,
name|EditScreen
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
DECL|field|revision
specifier|private
specifier|final
name|PatchSet
operator|.
name|Id
name|revision
decl_stmt|;
DECL|field|path
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
DECL|field|prefs
specifier|private
name|DiffPreferences
name|prefs
decl_stmt|;
DECL|field|cm
specifier|private
name|CodeMirror
name|cm
decl_stmt|;
DECL|field|type
specifier|private
name|String
name|type
decl_stmt|;
DECL|field|filePath
annotation|@
name|UiField
name|Element
name|filePath
decl_stmt|;
DECL|field|cancel
annotation|@
name|UiField
name|Button
name|cancel
decl_stmt|;
DECL|field|save
annotation|@
name|UiField
name|Button
name|save
decl_stmt|;
DECL|field|editor
annotation|@
name|UiField
name|Element
name|editor
decl_stmt|;
DECL|method|EditScreen (Patch.Key patch)
specifier|public
name|EditScreen
parameter_list|(
name|Patch
operator|.
name|Key
name|patch
parameter_list|)
block|{
name|this
operator|.
name|revision
operator|=
name|patch
operator|.
name|getParentKey
argument_list|()
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|patch
operator|.
name|get
argument_list|()
expr_stmt|;
name|prefs
operator|=
name|DiffPreferences
operator|.
name|create
argument_list|(
name|Gerrit
operator|.
name|getAccountDiffPreference
argument_list|()
argument_list|)
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
name|addDomHandler
argument_list|(
name|GlobalKey
operator|.
name|STOP_PROPAGATION
argument_list|,
name|KeyPressEvent
operator|.
name|getType
argument_list|()
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
name|initPath
argument_list|()
expr_stmt|;
name|setHeaderVisible
argument_list|(
literal|false
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
name|cmGroup
init|=
operator|new
name|CallbackGroup
argument_list|()
decl_stmt|;
name|CodeMirror
operator|.
name|initLibrary
argument_list|(
name|cmGroup
operator|.
name|add
argument_list|(
name|CallbackGroup
operator|.
expr|<
name|Void
operator|>
name|emptyCallback
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|CallbackGroup
name|group
init|=
operator|new
name|CallbackGroup
argument_list|()
decl_stmt|;
specifier|final
name|AsyncCallback
argument_list|<
name|Void
argument_list|>
name|modeInjectorCb
init|=
name|group
operator|.
name|add
argument_list|(
name|CallbackGroup
operator|.
expr|<
name|Void
operator|>
name|emptyCallback
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|Patch
operator|.
name|COMMIT_MSG
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|)
block|{
comment|// No need to inject "text/plain", just fire the callback
name|modeInjectorCb
operator|.
name|onSuccess
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ChangeFileApi
operator|.
name|getContentType
argument_list|(
name|revision
argument_list|,
name|path
argument_list|,
name|cmGroup
operator|.
name|addFinal
argument_list|(
operator|new
name|GerritCallback
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|String
name|result
parameter_list|)
block|{
name|type
operator|=
name|result
expr_stmt|;
name|injectMode
argument_list|(
name|result
argument_list|,
name|modeInjectorCb
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ChangeFileApi
operator|.
name|getContentOrMessage
argument_list|(
name|revision
argument_list|,
name|path
argument_list|,
name|group
operator|.
name|addFinal
argument_list|(
operator|new
name|ScreenLoadCallback
argument_list|<
name|String
argument_list|>
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|preDisplay
parameter_list|(
name|String
name|content
parameter_list|)
block|{
name|initEditor
argument_list|(
name|content
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onShowView ()
specifier|public
name|void
name|onShowView
parameter_list|()
block|{
name|super
operator|.
name|onShowView
argument_list|()
expr_stmt|;
if|if
condition|(
name|prefs
operator|.
name|hideTopMenu
argument_list|()
condition|)
block|{
name|Gerrit
operator|.
name|setHeaderVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|int
name|rest
init|=
name|Gerrit
operator|.
name|getHeaderFooterHeight
argument_list|()
operator|+
literal|30
decl_stmt|;
comment|// Estimate
name|cm
operator|.
name|setHeight
argument_list|(
name|Window
operator|.
name|getClientHeight
argument_list|()
operator|-
name|rest
argument_list|)
expr_stmt|;
name|cm
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|cm
operator|.
name|focus
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onUnload ()
specifier|protected
name|void
name|onUnload
parameter_list|()
block|{
name|super
operator|.
name|onUnload
argument_list|()
expr_stmt|;
name|Gerrit
operator|.
name|setHeaderVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|UiHandler
argument_list|(
literal|"save"
argument_list|)
DECL|method|onSave (@uppressWarningsR) ClickEvent e)
name|void
name|onSave
parameter_list|(
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
name|ClickEvent
name|e
parameter_list|)
block|{
name|ChangeFileApi
operator|.
name|putContentOrMessage
argument_list|(
name|revision
argument_list|,
name|path
argument_list|,
name|cm
operator|.
name|getValue
argument_list|()
argument_list|,
operator|new
name|GerritCallback
argument_list|<
name|VoidResult
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|VoidResult
name|result
parameter_list|)
block|{
name|Gerrit
operator|.
name|display
argument_list|(
name|PageLinks
operator|.
name|toChangeInEditMode
argument_list|(
name|revision
operator|.
name|getParentKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|UiHandler
argument_list|(
literal|"cancel"
argument_list|)
DECL|method|onCancel (@uppressWarningsR) ClickEvent e)
name|void
name|onCancel
parameter_list|(
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
name|ClickEvent
name|e
parameter_list|)
block|{
name|Gerrit
operator|.
name|display
argument_list|(
name|PageLinks
operator|.
name|toChangeInEditMode
argument_list|(
name|revision
operator|.
name|getParentKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|initEditor (String content)
specifier|private
name|void
name|initEditor
parameter_list|(
name|String
name|content
parameter_list|)
block|{
name|cm
operator|=
name|CodeMirror
operator|.
name|create
argument_list|(
name|editor
argument_list|,
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
name|cm
operator|.
name|setValue
argument_list|(
name|content
argument_list|)
expr_stmt|;
block|}
DECL|method|injectMode (String type, AsyncCallback<Void> cb)
specifier|private
name|void
name|injectMode
parameter_list|(
name|String
name|type
parameter_list|,
name|AsyncCallback
argument_list|<
name|Void
argument_list|>
name|cb
parameter_list|)
block|{
operator|new
name|ModeInjector
argument_list|()
operator|.
name|add
argument_list|(
name|type
argument_list|)
operator|.
name|inject
argument_list|(
name|cb
argument_list|)
expr_stmt|;
block|}
DECL|method|getConfig ()
specifier|private
name|Configuration
name|getConfig
parameter_list|()
block|{
comment|// TODO(davido): Retrieve user preferences from AllUsers repository
return|return
name|Configuration
operator|.
name|create
argument_list|()
operator|.
name|set
argument_list|(
literal|"readOnly"
argument_list|,
literal|false
argument_list|)
operator|.
name|set
argument_list|(
literal|"cursorBlinkRate"
argument_list|,
literal|0
argument_list|)
operator|.
name|set
argument_list|(
literal|"cursorHeight"
argument_list|,
literal|0.85
argument_list|)
operator|.
name|set
argument_list|(
literal|"lineNumbers"
argument_list|,
literal|true
argument_list|)
operator|.
name|set
argument_list|(
literal|"tabSize"
argument_list|,
literal|4
argument_list|)
operator|.
name|set
argument_list|(
literal|"lineWrapping"
argument_list|,
literal|false
argument_list|)
operator|.
name|set
argument_list|(
literal|"styleSelectedText"
argument_list|,
literal|true
argument_list|)
operator|.
name|set
argument_list|(
literal|"showTrailingSpace"
argument_list|,
literal|true
argument_list|)
operator|.
name|set
argument_list|(
literal|"keyMap"
argument_list|,
literal|"default"
argument_list|)
operator|.
name|set
argument_list|(
literal|"mode"
argument_list|,
name|ModeInjector
operator|.
name|getContentType
argument_list|(
name|type
argument_list|)
argument_list|)
return|;
block|}
DECL|method|initPath ()
specifier|private
name|void
name|initPath
parameter_list|()
block|{
name|filePath
operator|.
name|setInnerText
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

