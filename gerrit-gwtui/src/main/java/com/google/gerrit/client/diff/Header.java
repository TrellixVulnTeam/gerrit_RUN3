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
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|client
operator|.
name|KeyUtil
import|;
end_import

begin_class
DECL|class|Header
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
DECL|field|reviewed
annotation|@
name|UiField
name|CheckBox
name|reviewed
decl_stmt|;
DECL|field|filePath
annotation|@
name|UiField
name|Element
name|filePath
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
DECL|field|keys
specifier|private
specifier|final
name|KeyCommandSet
name|keys
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
DECL|method|Header (KeyCommandSet keys, PatchSet.Id patchSetId, String path)
name|Header
parameter_list|(
name|KeyCommandSet
name|keys
parameter_list|,
name|PatchSet
operator|.
name|Id
name|patchSetId
parameter_list|,
name|String
name|path
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
name|toChange2
argument_list|(
name|patchSetId
operator|.
name|getParentKey
argument_list|()
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|patchSetId
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|formatPath (String path)
specifier|private
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
annotation|@
name|Override
DECL|method|onLoad ()
specifier|protected
name|void
name|onLoad
parameter_list|()
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
name|get
argument_list|(
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
name|result
operator|.
name|copyKeysIntoChildren
argument_list|(
literal|"path"
argument_list|)
expr_stmt|;
name|JsArray
argument_list|<
name|FileInfo
argument_list|>
name|files
init|=
name|result
operator|.
name|values
argument_list|()
decl_stmt|;
name|FileInfo
operator|.
name|sortFileInfoByPath
argument_list|(
name|files
argument_list|)
expr_stmt|;
name|int
name|index
init|=
literal|0
decl_stmt|;
comment|// TODO: Maybe use patchIndex.
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
name|index
operator|=
name|i
expr_stmt|;
block|}
block|}
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
name|index
operator|==
literal|0
condition|?
literal|null
else|:
name|files
operator|.
name|get
argument_list|(
name|index
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
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
name|index
operator|==
name|files
operator|.
name|length
argument_list|()
operator|-
literal|1
condition|?
literal|null
else|:
name|files
operator|.
name|get
argument_list|(
name|index
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|setReviewed (boolean r)
name|void
name|setReviewed
parameter_list|(
name|boolean
name|r
parameter_list|)
block|{
name|reviewed
operator|.
name|setValue
argument_list|(
name|r
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|isReviewed ()
name|boolean
name|isReviewed
parameter_list|()
block|{
return|return
name|reviewed
operator|.
name|getValue
argument_list|()
return|;
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
name|RestApi
name|api
init|=
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
decl_stmt|;
if|if
condition|(
name|event
operator|.
name|getValue
argument_list|()
condition|)
block|{
name|api
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
name|api
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
DECL|method|url (FileInfo info)
specifier|private
name|String
name|url
parameter_list|(
name|FileInfo
name|info
parameter_list|)
block|{
name|Change
operator|.
name|Id
name|c
init|=
name|patchSetId
operator|.
name|getParentKey
argument_list|()
decl_stmt|;
name|StringBuilder
name|p
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|p
operator|.
name|append
argument_list|(
literal|"/c/"
argument_list|)
operator|.
name|append
argument_list|(
name|c
argument_list|)
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
name|p
operator|.
name|append
argument_list|(
name|patchSetId
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
operator|.
name|append
argument_list|(
name|KeyUtil
operator|.
name|encode
argument_list|(
name|info
operator|.
name|path
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|p
operator|.
name|append
argument_list|(
name|info
operator|.
name|binary
argument_list|()
condition|?
literal|",unified"
else|:
literal|",cm"
argument_list|)
expr_stmt|;
return|return
name|p
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|setupNav (InlineHyperlink link, int key, String help, FileInfo info)
specifier|private
name|void
name|setupNav
parameter_list|(
name|InlineHyperlink
name|link
parameter_list|,
name|int
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
name|getFileName
argument_list|(
name|info
operator|.
name|path
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|keys
operator|.
name|add
argument_list|(
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
block|}
else|else
block|{
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
name|UpToChangeCommand2
argument_list|(
name|patchSetId
argument_list|,
literal|0
argument_list|,
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getFileName (String path)
specifier|private
specifier|static
name|String
name|getFileName
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|String
name|fileName
init|=
name|Patch
operator|.
name|COMMIT_MSG
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|?
name|Util
operator|.
name|C
operator|.
name|commitMessage
argument_list|()
else|:
name|path
decl_stmt|;
name|int
name|s
init|=
name|fileName
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
return|return
name|s
operator|>=
literal|0
condition|?
name|fileName
operator|.
name|substring
argument_list|(
name|s
operator|+
literal|1
argument_list|)
else|:
name|fileName
return|;
block|}
DECL|method|hasPrev ()
name|boolean
name|hasPrev
parameter_list|()
block|{
return|return
name|hasPrev
return|;
block|}
DECL|method|hasNext ()
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|hasNext
return|;
block|}
block|}
end_class

end_unit

