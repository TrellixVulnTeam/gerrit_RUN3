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
DECL|package|com.google.gerrit.client.diff
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|diff
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Double
operator|.
name|POSITIVE_INFINITY
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
name|diff
operator|.
name|UnifiedChunkManager
operator|.
name|LineSidePair
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
name|patches
operator|.
name|PatchUtil
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
name|ConfigInfoCache
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
name|GeneralPreferencesInfo
operator|.
name|DiffView
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
name|JsArrayString
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
name|Scheduler
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
name|Scheduler
operator|.
name|ScheduledCommand
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
name|dom
operator|.
name|client
operator|.
name|NativeEvent
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
name|event
operator|.
name|dom
operator|.
name|client
operator|.
name|FocusEvent
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
name|FocusHandler
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
name|ImageResourceRenderer
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
name|CodeMirror
operator|.
name|GutterClickHandler
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
operator|.
name|LineHandle
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
name|Pos
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
name|ScrollInfo
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
name|List
import|;
end_import

begin_class
DECL|class|Unified
specifier|public
class|class
name|Unified
extends|extends
name|DiffScreen
block|{
DECL|interface|Binder
interface|interface
name|Binder
extends|extends
name|UiBinder
argument_list|<
name|FlowPanel
argument_list|,
name|Unified
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
annotation|@
name|UiField
argument_list|(
name|provided
operator|=
literal|true
argument_list|)
DECL|field|diffTable
name|UnifiedTable
name|diffTable
decl_stmt|;
DECL|field|cm
specifier|private
name|CodeMirror
name|cm
decl_stmt|;
DECL|field|chunkManager
specifier|private
name|UnifiedChunkManager
name|chunkManager
decl_stmt|;
DECL|field|commentManager
specifier|private
name|UnifiedCommentManager
name|commentManager
decl_stmt|;
DECL|field|skipManager
specifier|private
name|UnifiedSkipManager
name|skipManager
decl_stmt|;
DECL|field|autoHideDiffTableHeader
specifier|private
name|boolean
name|autoHideDiffTableHeader
decl_stmt|;
DECL|method|Unified ( PatchSet.Id base, PatchSet.Id revision, String path, DisplaySide startSide, int startLine)
specifier|public
name|Unified
parameter_list|(
name|PatchSet
operator|.
name|Id
name|base
parameter_list|,
name|PatchSet
operator|.
name|Id
name|revision
parameter_list|,
name|String
name|path
parameter_list|,
name|DisplaySide
name|startSide
parameter_list|,
name|int
name|startLine
parameter_list|)
block|{
name|super
argument_list|(
name|base
argument_list|,
name|revision
argument_list|,
name|path
argument_list|,
name|startSide
argument_list|,
name|startLine
argument_list|,
name|DiffView
operator|.
name|UNIFIED_DIFF
argument_list|)
expr_stmt|;
name|diffTable
operator|=
operator|new
name|UnifiedTable
argument_list|(
name|this
argument_list|,
name|base
argument_list|,
name|revision
argument_list|,
name|path
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
DECL|method|getScreenLoadCallback ( final CommentsCollections comments)
name|ScreenLoadCallback
argument_list|<
name|ConfigInfoCache
operator|.
name|Entry
argument_list|>
name|getScreenLoadCallback
parameter_list|(
specifier|final
name|CommentsCollections
name|comments
parameter_list|)
block|{
return|return
operator|new
name|ScreenLoadCallback
argument_list|<
name|ConfigInfoCache
operator|.
name|Entry
argument_list|>
argument_list|(
name|Unified
operator|.
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|preDisplay
parameter_list|(
name|ConfigInfoCache
operator|.
name|Entry
name|result
parameter_list|)
block|{
name|commentManager
operator|=
operator|new
name|UnifiedCommentManager
argument_list|(
name|Unified
operator|.
name|this
argument_list|,
name|base
argument_list|,
name|revision
argument_list|,
name|path
argument_list|,
name|result
operator|.
name|getCommentLinkProcessor
argument_list|()
argument_list|,
name|getChangeStatus
argument_list|()
operator|.
name|isOpen
argument_list|()
argument_list|)
expr_stmt|;
name|setTheme
argument_list|(
name|result
operator|.
name|getTheme
argument_list|()
argument_list|)
expr_stmt|;
name|display
argument_list|(
name|comments
argument_list|)
expr_stmt|;
name|header
operator|.
name|setupPrevNextFiles
argument_list|(
name|comments
argument_list|)
expr_stmt|;
block|}
block|}
return|;
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
name|operation
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|resizeCodeMirror
argument_list|()
expr_stmt|;
name|cm
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|setLineLength
argument_list|(
name|Patch
operator|.
name|COMMIT_MSG
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|?
literal|72
else|:
name|prefs
operator|.
name|lineLength
argument_list|()
argument_list|)
expr_stmt|;
name|diffTable
operator|.
name|refresh
argument_list|()
expr_stmt|;
if|if
condition|(
name|getStartLine
argument_list|()
operator|==
literal|0
condition|)
block|{
name|DiffChunkInfo
name|d
init|=
name|chunkManager
operator|.
name|getFirst
argument_list|()
decl_stmt|;
if|if
condition|(
name|d
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|d
operator|.
name|isEdit
argument_list|()
operator|&&
name|d
operator|.
name|getSide
argument_list|()
operator|==
name|DisplaySide
operator|.
name|A
condition|)
block|{
name|setStartSide
argument_list|(
name|DisplaySide
operator|.
name|B
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|setStartSide
argument_list|(
name|d
operator|.
name|getSide
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|setStartLine
argument_list|(
name|chunkManager
operator|.
name|getCmLine
argument_list|(
name|d
operator|.
name|getStart
argument_list|()
argument_list|,
name|d
operator|.
name|getSide
argument_list|()
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|getStartSide
argument_list|()
operator|!=
literal|null
operator|&&
name|getStartLine
argument_list|()
operator|>
literal|0
condition|)
block|{
name|cm
operator|.
name|scrollToLine
argument_list|(
name|chunkManager
operator|.
name|getCmLine
argument_list|(
name|getStartLine
argument_list|()
operator|-
literal|1
argument_list|,
name|getStartSide
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|cm
operator|.
name|focus
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|cm
operator|.
name|setCursor
argument_list|(
name|Pos
operator|.
name|create
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|cm
operator|.
name|focus
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|Gerrit
operator|.
name|isSignedIn
argument_list|()
operator|&&
name|prefs
operator|.
name|autoReview
argument_list|()
condition|)
block|{
name|header
operator|.
name|autoReview
argument_list|()
expr_stmt|;
block|}
name|prefetchNextFile
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|registerCmEvents (final CodeMirror cm)
name|void
name|registerCmEvents
parameter_list|(
specifier|final
name|CodeMirror
name|cm
parameter_list|)
block|{
name|super
operator|.
name|registerCmEvents
argument_list|(
name|cm
argument_list|)
expr_stmt|;
name|cm
operator|.
name|on
argument_list|(
literal|"scroll"
argument_list|,
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|ScrollInfo
name|si
init|=
name|cm
operator|.
name|getScrollInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|autoHideDiffTableHeader
condition|)
block|{
name|updateDiffTableHeader
argument_list|(
name|si
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|maybeRegisterRenderEntireFileKeyMap
argument_list|(
name|cm
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|registerKeys ()
specifier|public
name|void
name|registerKeys
parameter_list|()
block|{
name|super
operator|.
name|registerKeys
argument_list|()
expr_stmt|;
name|registerHandlers
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFocusHandler ()
name|FocusHandler
name|getFocusHandler
parameter_list|()
block|{
return|return
operator|new
name|FocusHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onFocus
parameter_list|(
name|FocusEvent
name|event
parameter_list|)
block|{
name|cm
operator|.
name|focus
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
DECL|method|display (final CommentsCollections comments)
specifier|private
name|void
name|display
parameter_list|(
specifier|final
name|CommentsCollections
name|comments
parameter_list|)
block|{
specifier|final
name|DiffInfo
name|diff
init|=
name|getDiff
argument_list|()
decl_stmt|;
name|setThemeStyles
argument_list|(
name|prefs
operator|.
name|theme
argument_list|()
operator|.
name|isDark
argument_list|()
argument_list|)
expr_stmt|;
name|setShowIntraline
argument_list|(
name|prefs
operator|.
name|intralineDifference
argument_list|()
argument_list|)
expr_stmt|;
comment|// TODO: Handle showLineNumbers preference
name|cm
operator|=
name|newCm
argument_list|(
name|diff
operator|.
name|metaA
argument_list|()
operator|==
literal|null
condition|?
name|diff
operator|.
name|metaB
argument_list|()
else|:
name|diff
operator|.
name|metaA
argument_list|()
argument_list|,
name|diff
operator|.
name|textUnified
argument_list|()
argument_list|,
name|diffTable
operator|.
name|cm
argument_list|)
expr_stmt|;
name|setShowTabs
argument_list|(
name|prefs
operator|.
name|showTabs
argument_list|()
argument_list|)
expr_stmt|;
name|chunkManager
operator|=
operator|new
name|UnifiedChunkManager
argument_list|(
name|this
argument_list|,
name|cm
argument_list|,
name|diffTable
operator|.
name|scrollbar
argument_list|)
expr_stmt|;
name|skipManager
operator|=
operator|new
name|UnifiedSkipManager
argument_list|(
name|this
argument_list|,
name|commentManager
argument_list|)
expr_stmt|;
name|operation
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
comment|// Estimate initial CodeMirror height, fixed up in onShowView.
name|int
name|height
init|=
name|Window
operator|.
name|getClientHeight
argument_list|()
operator|-
operator|(
name|Gerrit
operator|.
name|getHeaderFooterHeight
argument_list|()
operator|+
literal|18
operator|)
decl_stmt|;
name|cm
operator|.
name|setHeight
argument_list|(
name|height
argument_list|)
expr_stmt|;
name|render
argument_list|(
name|diff
argument_list|)
expr_stmt|;
name|commentManager
operator|.
name|render
argument_list|(
name|comments
argument_list|,
name|prefs
operator|.
name|expandAllComments
argument_list|()
argument_list|)
expr_stmt|;
name|skipManager
operator|.
name|render
argument_list|(
name|prefs
operator|.
name|context
argument_list|()
argument_list|,
name|diff
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|registerCmEvents
argument_list|(
name|cm
argument_list|)
expr_stmt|;
name|setPrefsAction
argument_list|(
operator|new
name|PreferencesAction
argument_list|(
name|this
argument_list|,
name|prefs
argument_list|)
argument_list|)
expr_stmt|;
name|header
operator|.
name|init
argument_list|(
name|getPrefsAction
argument_list|()
argument_list|,
name|getSideBySideDiffLink
argument_list|()
argument_list|,
name|diff
operator|.
name|unifiedWebLinks
argument_list|()
argument_list|)
expr_stmt|;
name|setAutoHideDiffHeader
argument_list|(
name|prefs
operator|.
name|autoHideDiffTableHeader
argument_list|()
argument_list|)
expr_stmt|;
name|setupSyntaxHighlighting
argument_list|()
expr_stmt|;
block|}
DECL|method|getSideBySideDiffLink ()
specifier|private
name|List
argument_list|<
name|InlineHyperlink
argument_list|>
name|getSideBySideDiffLink
parameter_list|()
block|{
name|InlineHyperlink
name|toSideBySideDiffLink
init|=
operator|new
name|InlineHyperlink
argument_list|()
decl_stmt|;
name|toSideBySideDiffLink
operator|.
name|setHTML
argument_list|(
operator|new
name|ImageResourceRenderer
argument_list|()
operator|.
name|render
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|sideBySideDiff
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|toSideBySideDiffLink
operator|.
name|setTargetHistoryToken
argument_list|(
name|Dispatcher
operator|.
name|toSideBySide
argument_list|(
name|base
argument_list|,
name|revision
argument_list|,
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|toSideBySideDiffLink
operator|.
name|setTitle
argument_list|(
name|PatchUtil
operator|.
name|C
operator|.
name|sideBySideDiff
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
name|toSideBySideDiffLink
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newCm ( DiffInfo.FileMeta meta, String contents, Element parent)
name|CodeMirror
name|newCm
parameter_list|(
name|DiffInfo
operator|.
name|FileMeta
name|meta
parameter_list|,
name|String
name|contents
parameter_list|,
name|Element
name|parent
parameter_list|)
block|{
name|JsArrayString
name|gutters
init|=
name|JavaScriptObject
operator|.
name|createArray
argument_list|()
operator|.
name|cast
argument_list|()
decl_stmt|;
name|gutters
operator|.
name|push
argument_list|(
name|UnifiedTable
operator|.
name|style
operator|.
name|lineNumbersLeft
argument_list|()
argument_list|)
expr_stmt|;
name|gutters
operator|.
name|push
argument_list|(
name|UnifiedTable
operator|.
name|style
operator|.
name|lineNumbersRight
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|CodeMirror
operator|.
name|create
argument_list|(
name|parent
argument_list|,
name|Configuration
operator|.
name|create
argument_list|()
operator|.
name|set
argument_list|(
literal|"cursorBlinkRate"
argument_list|,
name|prefs
operator|.
name|cursorBlinkRate
argument_list|()
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
literal|"gutters"
argument_list|,
name|gutters
argument_list|)
operator|.
name|set
argument_list|(
literal|"inputStyle"
argument_list|,
literal|"textarea"
argument_list|)
operator|.
name|set
argument_list|(
literal|"keyMap"
argument_list|,
literal|"vim_ro"
argument_list|)
operator|.
name|set
argument_list|(
literal|"lineNumbers"
argument_list|,
literal|false
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
literal|"matchBrackets"
argument_list|,
name|prefs
operator|.
name|matchBrackets
argument_list|()
argument_list|)
operator|.
name|set
argument_list|(
literal|"mode"
argument_list|,
name|getFileSize
argument_list|()
operator|==
name|FileSize
operator|.
name|SMALL
condition|?
name|getContentType
argument_list|(
name|meta
argument_list|)
else|:
literal|null
argument_list|)
operator|.
name|set
argument_list|(
literal|"readOnly"
argument_list|,
literal|true
argument_list|)
operator|.
name|set
argument_list|(
literal|"scrollbarStyle"
argument_list|,
literal|"overlay"
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
name|prefs
operator|.
name|showWhitespaceErrors
argument_list|()
argument_list|)
operator|.
name|set
argument_list|(
literal|"tabSize"
argument_list|,
name|prefs
operator|.
name|tabSize
argument_list|()
argument_list|)
operator|.
name|set
argument_list|(
literal|"theme"
argument_list|,
name|prefs
operator|.
name|theme
argument_list|()
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|)
operator|.
name|set
argument_list|(
literal|"value"
argument_list|,
name|meta
operator|!=
literal|null
condition|?
name|contents
else|:
literal|""
argument_list|)
operator|.
name|set
argument_list|(
literal|"viewportMargin"
argument_list|,
name|renderEntireFile
argument_list|()
condition|?
name|POSITIVE_INFINITY
else|:
literal|10
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setShowLineNumbers (boolean b)
name|void
name|setShowLineNumbers
parameter_list|(
name|boolean
name|b
parameter_list|)
block|{
comment|// TODO: Implement this
block|}
DECL|method|onGutterClick (final int cmLine)
specifier|private
name|GutterClickHandler
name|onGutterClick
parameter_list|(
specifier|final
name|int
name|cmLine
parameter_list|)
block|{
return|return
operator|new
name|GutterClickHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|handle
parameter_list|(
name|CodeMirror
name|instance
parameter_list|,
name|int
name|line
parameter_list|,
name|String
name|gutter
parameter_list|,
name|NativeEvent
name|clickEvent
parameter_list|)
block|{
if|if
condition|(
name|clickEvent
operator|.
name|getButton
argument_list|()
operator|==
name|NativeEvent
operator|.
name|BUTTON_LEFT
operator|&&
operator|!
name|clickEvent
operator|.
name|getMetaKey
argument_list|()
operator|&&
operator|!
name|clickEvent
operator|.
name|getAltKey
argument_list|()
operator|&&
operator|!
name|clickEvent
operator|.
name|getCtrlKey
argument_list|()
operator|&&
operator|!
name|clickEvent
operator|.
name|getShiftKey
argument_list|()
condition|)
block|{
name|cm
operator|.
name|setCursor
argument_list|(
name|Pos
operator|.
name|create
argument_list|(
name|cmLine
argument_list|)
argument_list|)
expr_stmt|;
name|Scheduler
operator|.
name|get
argument_list|()
operator|.
name|scheduleDeferred
argument_list|(
operator|new
name|ScheduledCommand
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|()
block|{
name|commentManager
operator|.
name|newDraftCallback
argument_list|(
name|cm
argument_list|)
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
DECL|method|setLineNumber (DisplaySide side, final int cmLine, int line)
name|LineHandle
name|setLineNumber
parameter_list|(
name|DisplaySide
name|side
parameter_list|,
specifier|final
name|int
name|cmLine
parameter_list|,
name|int
name|line
parameter_list|)
block|{
name|Label
name|gutter
init|=
operator|new
name|Label
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|line
argument_list|)
argument_list|)
decl_stmt|;
name|gutter
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
name|onGutterClick
argument_list|(
name|cmLine
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|diffTable
operator|.
name|add
argument_list|(
name|gutter
argument_list|)
expr_stmt|;
name|gutter
operator|.
name|setStyleName
argument_list|(
name|UnifiedTable
operator|.
name|style
operator|.
name|unifiedLineNumber
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|cm
operator|.
name|setGutterMarker
argument_list|(
name|cmLine
argument_list|,
name|side
operator|==
name|DisplaySide
operator|.
name|A
condition|?
name|UnifiedTable
operator|.
name|style
operator|.
name|lineNumbersLeft
argument_list|()
else|:
name|UnifiedTable
operator|.
name|style
operator|.
name|lineNumbersRight
argument_list|()
argument_list|,
name|gutter
operator|.
name|getElement
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setSyntaxHighlighting (boolean b)
name|void
name|setSyntaxHighlighting
parameter_list|(
name|boolean
name|b
parameter_list|)
block|{
specifier|final
name|DiffInfo
name|diff
init|=
name|getDiff
argument_list|()
decl_stmt|;
if|if
condition|(
name|b
condition|)
block|{
name|injectMode
argument_list|(
name|diff
argument_list|,
operator|new
name|AsyncCallback
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|Void
name|result
parameter_list|)
block|{
if|if
condition|(
name|prefs
operator|.
name|syntaxHighlighting
argument_list|()
condition|)
block|{
name|cm
operator|.
name|setOption
argument_list|(
literal|"mode"
argument_list|,
name|getContentType
argument_list|(
name|diff
operator|.
name|metaA
argument_list|()
operator|==
literal|null
condition|?
name|diff
operator|.
name|metaB
argument_list|()
else|:
name|diff
operator|.
name|metaA
argument_list|()
argument_list|)
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
name|prefs
operator|.
name|syntaxHighlighting
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|cm
operator|.
name|setOption
argument_list|(
literal|"mode"
argument_list|,
operator|(
name|String
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|setAutoHideDiffHeader (boolean autoHide)
name|void
name|setAutoHideDiffHeader
parameter_list|(
name|boolean
name|autoHide
parameter_list|)
block|{
if|if
condition|(
name|autoHide
condition|)
block|{
name|updateDiffTableHeader
argument_list|(
name|cm
operator|.
name|getScrollInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|diffTable
operator|.
name|setHeaderVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|autoHideDiffTableHeader
operator|=
name|autoHide
expr_stmt|;
block|}
DECL|method|updateDiffTableHeader (ScrollInfo si)
specifier|private
name|void
name|updateDiffTableHeader
parameter_list|(
name|ScrollInfo
name|si
parameter_list|)
block|{
if|if
condition|(
name|si
operator|.
name|top
argument_list|()
operator|==
literal|0
condition|)
block|{
name|diffTable
operator|.
name|setHeaderVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|si
operator|.
name|top
argument_list|()
operator|>
literal|0.5
operator|*
name|si
operator|.
name|clientHeight
argument_list|()
condition|)
block|{
name|diffTable
operator|.
name|setHeaderVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|updateActiveLine (final CodeMirror cm)
name|Runnable
name|updateActiveLine
parameter_list|(
specifier|final
name|CodeMirror
name|cm
parameter_list|)
block|{
return|return
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
comment|// The rendering of active lines has to be deferred. Reflow
comment|// caused by adding and removing styles chokes Firefox when arrow
comment|// key (or j/k) is held down. Performance on Chrome is fine
comment|// without the deferral.
comment|//
name|Scheduler
operator|.
name|get
argument_list|()
operator|.
name|scheduleDeferred
argument_list|(
operator|new
name|ScheduledCommand
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|()
block|{
name|LineHandle
name|handle
init|=
name|cm
operator|.
name|getLineHandleVisualStart
argument_list|(
name|cm
operator|.
name|getCursor
argument_list|(
literal|"end"
argument_list|)
operator|.
name|line
argument_list|()
argument_list|)
decl_stmt|;
name|cm
operator|.
name|extras
argument_list|()
operator|.
name|activeLine
argument_list|(
name|handle
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|getCmFromSide (DisplaySide side)
name|CodeMirror
name|getCmFromSide
parameter_list|(
name|DisplaySide
name|side
parameter_list|)
block|{
return|return
name|cm
return|;
block|}
DECL|method|getCmLine (int line, DisplaySide side)
name|int
name|getCmLine
parameter_list|(
name|int
name|line
parameter_list|,
name|DisplaySide
name|side
parameter_list|)
block|{
return|return
name|chunkManager
operator|.
name|getCmLine
argument_list|(
name|line
argument_list|,
name|side
argument_list|)
return|;
block|}
DECL|method|getLineSidePairFromCmLine (int cmLine)
name|LineSidePair
name|getLineSidePairFromCmLine
parameter_list|(
name|int
name|cmLine
parameter_list|)
block|{
return|return
name|chunkManager
operator|.
name|getLineSidePairFromCmLine
argument_list|(
name|cmLine
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|operation (final Runnable apply)
name|void
name|operation
parameter_list|(
specifier|final
name|Runnable
name|apply
parameter_list|)
block|{
name|cm
operator|.
name|operation
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|apply
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCms ()
name|CodeMirror
index|[]
name|getCms
parameter_list|()
block|{
return|return
operator|new
name|CodeMirror
index|[]
block|{
name|cm
block|}
return|;
block|}
DECL|method|getCm ()
name|CodeMirror
name|getCm
parameter_list|()
block|{
return|return
name|cm
return|;
block|}
annotation|@
name|Override
DECL|method|getDiffTable ()
name|UnifiedTable
name|getDiffTable
parameter_list|()
block|{
return|return
name|diffTable
return|;
block|}
annotation|@
name|Override
DECL|method|getChunkManager ()
name|UnifiedChunkManager
name|getChunkManager
parameter_list|()
block|{
return|return
name|chunkManager
return|;
block|}
annotation|@
name|Override
DECL|method|getCommentManager ()
name|UnifiedCommentManager
name|getCommentManager
parameter_list|()
block|{
return|return
name|commentManager
return|;
block|}
annotation|@
name|Override
DECL|method|getSkipManager ()
name|UnifiedSkipManager
name|getSkipManager
parameter_list|()
block|{
return|return
name|skipManager
return|;
block|}
block|}
end_class

end_unit

