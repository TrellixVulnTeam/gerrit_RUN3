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
name|ChangeApi
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
name|ReviewInfo
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
name|diff
operator|.
name|DiffInfo
operator|.
name|Region
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
name|info
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
name|info
operator|.
name|FileInfo
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
name|info
operator|.
name|WebLinkInfo
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
name|RestApi
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
name|Patch
operator|.
name|ChangeType
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
name|Style
operator|.
name|Visibility
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
name|event
operator|.
name|logical
operator|.
name|shared
operator|.
name|ValueChangeEvent
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
name|CheckBox
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
name|KeyCommand
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
name|KeyCommandSet
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
DECL|class|Header
specifier|public
class|class
name|Header
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
name|Header
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
static|static
block|{
name|Resources
operator|.
name|I
operator|.
name|style
argument_list|()
operator|.
name|ensureInjected
argument_list|()
expr_stmt|;
block|}
DECL|enum|ReviewedState
specifier|private
enum|enum
name|ReviewedState
block|{
DECL|enumConstant|AUTO_REVIEW
DECL|enumConstant|LOADED
name|AUTO_REVIEW
block|,
name|LOADED
block|}
DECL|field|reviewed
annotation|@
name|UiField
name|CheckBox
name|reviewed
decl_stmt|;
DECL|field|project
annotation|@
name|UiField
name|Element
name|project
decl_stmt|;
DECL|field|filePath
annotation|@
name|UiField
name|Element
name|filePath
decl_stmt|;
DECL|field|fileNumber
annotation|@
name|UiField
name|Element
name|fileNumber
decl_stmt|;
DECL|field|fileCount
annotation|@
name|UiField
name|Element
name|fileCount
decl_stmt|;
DECL|field|noDiff
annotation|@
name|UiField
name|Element
name|noDiff
decl_stmt|;
DECL|field|linkPanel
annotation|@
name|UiField
name|FlowPanel
name|linkPanel
decl_stmt|;
DECL|field|prev
annotation|@
name|UiField
name|InlineHyperlink
name|prev
decl_stmt|;
DECL|field|up
annotation|@
name|UiField
name|InlineHyperlink
name|up
decl_stmt|;
DECL|field|next
annotation|@
name|UiField
name|InlineHyperlink
name|next
decl_stmt|;
DECL|field|preferences
annotation|@
name|UiField
name|Image
name|preferences
decl_stmt|;
DECL|field|keys
specifier|private
specifier|final
name|KeyCommandSet
name|keys
decl_stmt|;
DECL|field|base
specifier|private
specifier|final
name|PatchSet
operator|.
name|Id
name|base
decl_stmt|;
DECL|field|patchSetId
specifier|private
specifier|final
name|PatchSet
operator|.
name|Id
name|patchSetId
decl_stmt|;
DECL|field|path
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
DECL|field|diffScreenType
specifier|private
specifier|final
name|DiffView
name|diffScreenType
decl_stmt|;
DECL|field|prefs
specifier|private
specifier|final
name|DiffPreferences
name|prefs
decl_stmt|;
DECL|field|hasPrev
specifier|private
name|boolean
name|hasPrev
decl_stmt|;
DECL|field|hasNext
specifier|private
name|boolean
name|hasNext
decl_stmt|;
DECL|field|nextPath
specifier|private
name|String
name|nextPath
decl_stmt|;
DECL|field|files
specifier|private
name|JsArray
argument_list|<
name|FileInfo
argument_list|>
name|files
decl_stmt|;
DECL|field|prefsAction
specifier|private
name|PreferencesAction
name|prefsAction
decl_stmt|;
DECL|field|reviewedState
specifier|private
name|ReviewedState
name|reviewedState
decl_stmt|;
DECL|method|Header (KeyCommandSet keys, PatchSet.Id base, PatchSet.Id patchSetId, String path, DiffView diffSreenType, DiffPreferences prefs)
name|Header
parameter_list|(
name|KeyCommandSet
name|keys
parameter_list|,
name|PatchSet
operator|.
name|Id
name|base
parameter_list|,
name|PatchSet
operator|.
name|Id
name|patchSetId
parameter_list|,
name|String
name|path
parameter_list|,
name|DiffView
name|diffSreenType
parameter_list|,
name|DiffPreferences
name|prefs
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
name|this
operator|.
name|keys
operator|=
name|keys
expr_stmt|;
name|this
operator|.
name|base
operator|=
name|base
expr_stmt|;
name|this
operator|.
name|patchSetId
operator|=
name|patchSetId
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|diffScreenType
operator|=
name|diffSreenType
expr_stmt|;
name|this
operator|.
name|prefs
operator|=
name|prefs
expr_stmt|;
if|if
condition|(
operator|!
name|Gerrit
operator|.
name|isSignedIn
argument_list|()
condition|)
block|{
name|reviewed
operator|.
name|getElement
argument_list|()
operator|.
name|getStyle
argument_list|()
operator|.
name|setVisibility
argument_list|(
name|Visibility
operator|.
name|HIDDEN
argument_list|)
expr_stmt|;
block|}
name|SafeHtml
operator|.
name|setInnerHTML
argument_list|(
name|filePath
argument_list|,
name|formatPath
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|up
operator|.
name|setTargetHistoryToken
argument_list|(
name|PageLinks
operator|.
name|toChange
argument_list|(
name|patchSetId
operator|.
name|getParentKey
argument_list|()
argument_list|,
name|base
operator|!=
literal|null
condition|?
name|base
operator|.
name|getId
argument_list|()
else|:
literal|null
argument_list|,
name|patchSetId
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|formatPath (String path)
specifier|public
specifier|static
name|SafeHtml
name|formatPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|SafeHtmlBuilder
name|b
init|=
operator|new
name|SafeHtmlBuilder
argument_list|()
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
return|return
name|b
operator|.
name|append
argument_list|(
name|Util
operator|.
name|C
operator|.
name|commitMessage
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|Patch
operator|.
name|MERGE_LIST
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|)
block|{
return|return
name|b
operator|.
name|append
argument_list|(
name|Util
operator|.
name|C
operator|.
name|mergeList
argument_list|()
argument_list|)
return|;
block|}
name|int
name|s
init|=
name|path
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
operator|+
literal|1
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
name|path
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|s
argument_list|)
argument_list|)
expr_stmt|;
name|b
operator|.
name|openElement
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|path
operator|.
name|substring
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
name|b
operator|.
name|closeElement
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
return|return
name|b
return|;
block|}
DECL|method|findCurrentFileIndex (JsArray<FileInfo> files)
specifier|private
name|int
name|findCurrentFileIndex
parameter_list|(
name|JsArray
argument_list|<
name|FileInfo
argument_list|>
name|files
parameter_list|)
block|{
name|int
name|currIndex
init|=
literal|0
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
name|files
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|path
operator|.
name|equals
argument_list|(
name|files
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|path
argument_list|()
argument_list|)
condition|)
block|{
name|currIndex
operator|=
name|i
expr_stmt|;
break|break;
block|}
block|}
return|return
name|currIndex
return|;
block|}
annotation|@
name|Override
DECL|method|onLoad ()
specifier|protected
name|void
name|onLoad
parameter_list|()
block|{
name|DiffApi
operator|.
name|list
argument_list|(
name|patchSetId
argument_list|,
name|base
argument_list|,
operator|new
name|GerritCallback
argument_list|<
name|NativeMap
argument_list|<
name|FileInfo
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
name|FileInfo
argument_list|>
name|result
parameter_list|)
block|{
name|files
operator|=
name|result
operator|.
name|values
argument_list|()
expr_stmt|;
name|FileInfo
operator|.
name|sortFileInfoByPath
argument_list|(
name|files
argument_list|)
expr_stmt|;
name|fileNumber
operator|.
name|setInnerText
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|Natives
operator|.
name|asList
argument_list|(
name|files
argument_list|)
operator|.
name|indexOf
argument_list|(
name|result
operator|.
name|get
argument_list|(
name|path
argument_list|)
argument_list|)
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|fileCount
operator|.
name|setInnerText
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|files
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|Gerrit
operator|.
name|isSignedIn
argument_list|()
condition|)
block|{
name|ChangeApi
operator|.
name|revision
argument_list|(
name|patchSetId
argument_list|)
operator|.
name|view
argument_list|(
literal|"files"
argument_list|)
operator|.
name|addParameterTrue
argument_list|(
literal|"reviewed"
argument_list|)
operator|.
name|get
argument_list|(
operator|new
name|AsyncCallback
argument_list|<
name|JsArrayString
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|JsArrayString
name|result
parameter_list|)
block|{
name|boolean
name|b
init|=
name|Natives
operator|.
name|asList
argument_list|(
name|result
argument_list|)
operator|.
name|contains
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|reviewed
operator|.
name|setValue
argument_list|(
name|b
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|b
operator|&&
name|reviewedState
operator|==
name|ReviewedState
operator|.
name|AUTO_REVIEW
condition|)
block|{
name|postAutoReviewed
argument_list|()
expr_stmt|;
block|}
name|reviewedState
operator|=
name|ReviewedState
operator|.
name|LOADED
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
block|{             }
block|}
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|autoReview ()
name|void
name|autoReview
parameter_list|()
block|{
if|if
condition|(
name|reviewedState
operator|==
name|ReviewedState
operator|.
name|LOADED
operator|&&
operator|!
name|reviewed
operator|.
name|getValue
argument_list|()
condition|)
block|{
name|postAutoReviewed
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|reviewedState
operator|=
name|ReviewedState
operator|.
name|AUTO_REVIEW
expr_stmt|;
block|}
block|}
DECL|method|setChangeInfo (ChangeInfo info)
name|void
name|setChangeInfo
parameter_list|(
name|ChangeInfo
name|info
parameter_list|)
block|{
name|project
operator|.
name|setInnerText
argument_list|(
name|info
operator|.
name|project
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|init (PreferencesAction pa, List<InlineHyperlink> links, List<WebLinkInfo> webLinks)
name|void
name|init
parameter_list|(
name|PreferencesAction
name|pa
parameter_list|,
name|List
argument_list|<
name|InlineHyperlink
argument_list|>
name|links
parameter_list|,
name|List
argument_list|<
name|WebLinkInfo
argument_list|>
name|webLinks
parameter_list|)
block|{
name|prefsAction
operator|=
name|pa
expr_stmt|;
name|prefsAction
operator|.
name|setPartner
argument_list|(
name|preferences
argument_list|)
expr_stmt|;
for|for
control|(
name|InlineHyperlink
name|link
range|:
name|links
control|)
block|{
name|linkPanel
operator|.
name|add
argument_list|(
name|link
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|WebLinkInfo
name|webLink
range|:
name|webLinks
control|)
block|{
name|linkPanel
operator|.
name|add
argument_list|(
name|webLink
operator|.
name|toAnchor
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|UiHandler
argument_list|(
literal|"reviewed"
argument_list|)
DECL|method|onValueChange (ValueChangeEvent<Boolean> event)
name|void
name|onValueChange
parameter_list|(
name|ValueChangeEvent
argument_list|<
name|Boolean
argument_list|>
name|event
parameter_list|)
block|{
if|if
condition|(
name|event
operator|.
name|getValue
argument_list|()
condition|)
block|{
name|reviewed
argument_list|()
operator|.
name|put
argument_list|(
name|CallbackGroup
operator|.
expr|<
name|ReviewInfo
operator|>
name|emptyCallback
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|reviewed
argument_list|()
operator|.
name|delete
argument_list|(
name|CallbackGroup
operator|.
expr|<
name|ReviewInfo
operator|>
name|emptyCallback
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|postAutoReviewed ()
specifier|private
name|void
name|postAutoReviewed
parameter_list|()
block|{
name|reviewed
argument_list|()
operator|.
name|background
argument_list|()
operator|.
name|put
argument_list|(
operator|new
name|AsyncCallback
argument_list|<
name|ReviewInfo
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|ReviewInfo
name|result
parameter_list|)
block|{
name|reviewed
operator|.
name|setValue
argument_list|(
literal|true
argument_list|,
literal|false
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
block|{         }
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|reviewed ()
specifier|private
name|RestApi
name|reviewed
parameter_list|()
block|{
return|return
name|ChangeApi
operator|.
name|revision
argument_list|(
name|patchSetId
argument_list|)
operator|.
name|view
argument_list|(
literal|"files"
argument_list|)
operator|.
name|id
argument_list|(
name|path
argument_list|)
operator|.
name|view
argument_list|(
literal|"reviewed"
argument_list|)
return|;
block|}
annotation|@
name|UiHandler
argument_list|(
literal|"preferences"
argument_list|)
DECL|method|onPreferences (@uppressWarningsR) ClickEvent e)
name|void
name|onPreferences
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
name|prefsAction
operator|.
name|show
argument_list|()
expr_stmt|;
block|}
DECL|method|url (FileInfo info)
specifier|private
name|String
name|url
parameter_list|(
name|FileInfo
name|info
parameter_list|)
block|{
return|return
name|diffScreenType
operator|==
name|DiffView
operator|.
name|UNIFIED_DIFF
condition|?
name|Dispatcher
operator|.
name|toUnified
argument_list|(
name|base
argument_list|,
name|patchSetId
argument_list|,
name|info
operator|.
name|path
argument_list|()
argument_list|)
else|:
name|Dispatcher
operator|.
name|toSideBySide
argument_list|(
name|base
argument_list|,
name|patchSetId
argument_list|,
name|info
operator|.
name|path
argument_list|()
argument_list|)
return|;
block|}
DECL|method|setupNav (InlineHyperlink link, char key, String help, FileInfo info)
specifier|private
name|KeyCommand
name|setupNav
parameter_list|(
name|InlineHyperlink
name|link
parameter_list|,
name|char
name|key
parameter_list|,
name|String
name|help
parameter_list|,
name|FileInfo
name|info
parameter_list|)
block|{
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
specifier|final
name|String
name|url
init|=
name|url
argument_list|(
name|info
argument_list|)
decl_stmt|;
name|link
operator|.
name|setTargetHistoryToken
argument_list|(
name|url
argument_list|)
expr_stmt|;
name|link
operator|.
name|setTitle
argument_list|(
name|PatchUtil
operator|.
name|M
operator|.
name|fileNameWithShortcutKey
argument_list|(
name|FileInfo
operator|.
name|getFileName
argument_list|(
name|info
operator|.
name|path
argument_list|()
argument_list|)
argument_list|,
name|Character
operator|.
name|toString
argument_list|(
name|key
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|KeyCommand
name|k
init|=
operator|new
name|KeyCommand
argument_list|(
literal|0
argument_list|,
name|key
argument_list|,
name|help
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|onKeyPress
parameter_list|(
name|KeyPressEvent
name|event
parameter_list|)
block|{
name|Gerrit
operator|.
name|display
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|keys
operator|.
name|add
argument_list|(
name|k
argument_list|)
expr_stmt|;
if|if
condition|(
name|link
operator|==
name|prev
condition|)
block|{
name|hasPrev
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|hasNext
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|k
return|;
block|}
name|link
operator|.
name|getElement
argument_list|()
operator|.
name|getStyle
argument_list|()
operator|.
name|setVisibility
argument_list|(
name|Visibility
operator|.
name|HIDDEN
argument_list|)
expr_stmt|;
name|keys
operator|.
name|add
argument_list|(
operator|new
name|UpToChangeCommand
argument_list|(
name|patchSetId
argument_list|,
literal|0
argument_list|,
name|key
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
DECL|method|shouldSkipFile (FileInfo curr, CommentsCollections comments)
specifier|private
name|boolean
name|shouldSkipFile
parameter_list|(
name|FileInfo
name|curr
parameter_list|,
name|CommentsCollections
name|comments
parameter_list|)
block|{
return|return
name|prefs
operator|.
name|skipDeleted
argument_list|()
operator|&&
name|ChangeType
operator|.
name|DELETED
operator|.
name|matches
argument_list|(
name|curr
operator|.
name|status
argument_list|()
argument_list|)
operator|||
name|prefs
operator|.
name|skipUnchanged
argument_list|()
operator|&&
name|ChangeType
operator|.
name|RENAMED
operator|.
name|matches
argument_list|(
name|curr
operator|.
name|status
argument_list|()
argument_list|)
operator|||
name|prefs
operator|.
name|skipUncommented
argument_list|()
operator|&&
operator|!
name|comments
operator|.
name|hasCommentForPath
argument_list|(
name|curr
operator|.
name|path
argument_list|()
argument_list|)
return|;
block|}
DECL|method|setupPrevNextFiles (CommentsCollections comments)
name|void
name|setupPrevNextFiles
parameter_list|(
name|CommentsCollections
name|comments
parameter_list|)
block|{
name|FileInfo
name|prevInfo
init|=
literal|null
decl_stmt|;
name|FileInfo
name|nextInfo
init|=
literal|null
decl_stmt|;
name|int
name|currIndex
init|=
name|findCurrentFileIndex
argument_list|(
name|files
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|currIndex
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
name|FileInfo
name|curr
init|=
name|files
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|shouldSkipFile
argument_list|(
name|curr
argument_list|,
name|comments
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|prevInfo
operator|=
name|curr
expr_stmt|;
break|break;
block|}
for|for
control|(
name|int
name|i
init|=
name|currIndex
operator|+
literal|1
init|;
name|i
operator|<
name|files
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|FileInfo
name|curr
init|=
name|files
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|shouldSkipFile
argument_list|(
name|curr
argument_list|,
name|comments
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|nextInfo
operator|=
name|curr
expr_stmt|;
break|break;
block|}
name|KeyCommand
name|p
init|=
name|setupNav
argument_list|(
name|prev
argument_list|,
literal|'['
argument_list|,
name|PatchUtil
operator|.
name|C
operator|.
name|previousFileHelp
argument_list|()
argument_list|,
name|prevInfo
argument_list|)
decl_stmt|;
name|KeyCommand
name|n
init|=
name|setupNav
argument_list|(
name|next
argument_list|,
literal|']'
argument_list|,
name|PatchUtil
operator|.
name|C
operator|.
name|nextFileHelp
argument_list|()
argument_list|,
name|nextInfo
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
operator|&&
name|n
operator|!=
literal|null
condition|)
block|{
name|keys
operator|.
name|pair
argument_list|(
name|p
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
name|nextPath
operator|=
name|nextInfo
operator|!=
literal|null
condition|?
name|nextInfo
operator|.
name|path
argument_list|()
else|:
literal|null
expr_stmt|;
block|}
DECL|method|toggleReviewed ()
name|Runnable
name|toggleReviewed
parameter_list|()
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
name|reviewed
operator|.
name|setValue
argument_list|(
operator|!
name|reviewed
operator|.
name|getValue
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
DECL|method|navigate (Direction dir)
name|Runnable
name|navigate
parameter_list|(
name|Direction
name|dir
parameter_list|)
block|{
switch|switch
condition|(
name|dir
condition|)
block|{
case|case
name|PREV
case|:
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
operator|(
name|hasPrev
condition|?
name|prev
else|:
name|up
operator|)
operator|.
name|go
argument_list|()
expr_stmt|;
block|}
block|}
return|;
case|case
name|NEXT
case|:
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
operator|(
name|hasNext
condition|?
name|next
else|:
name|up
operator|)
operator|.
name|go
argument_list|()
expr_stmt|;
block|}
block|}
return|;
default|default:
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
block|{           }
block|}
return|;
block|}
block|}
DECL|method|reviewedAndNext ()
name|Runnable
name|reviewedAndNext
parameter_list|()
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
if|if
condition|(
name|Gerrit
operator|.
name|isSignedIn
argument_list|()
condition|)
block|{
name|reviewed
operator|.
name|setValue
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|navigate
argument_list|(
name|Direction
operator|.
name|NEXT
argument_list|)
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
DECL|method|getNextPath ()
name|String
name|getNextPath
parameter_list|()
block|{
return|return
name|nextPath
return|;
block|}
DECL|method|setNoDiff (DiffInfo diff)
name|void
name|setNoDiff
parameter_list|(
name|DiffInfo
name|diff
parameter_list|)
block|{
if|if
condition|(
name|diff
operator|.
name|binary
argument_list|()
condition|)
block|{
name|UIObject
operator|.
name|setVisible
argument_list|(
name|noDiff
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// Don't bother showing "No Differences"
block|}
else|else
block|{
name|JsArray
argument_list|<
name|Region
argument_list|>
name|regions
init|=
name|diff
operator|.
name|content
argument_list|()
decl_stmt|;
name|boolean
name|b
init|=
name|regions
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|||
operator|(
name|regions
operator|.
name|length
argument_list|()
operator|==
literal|1
operator|&&
name|regions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|ab
argument_list|()
operator|!=
literal|null
operator|)
decl_stmt|;
name|UIObject
operator|.
name|setVisible
argument_list|(
name|noDiff
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

