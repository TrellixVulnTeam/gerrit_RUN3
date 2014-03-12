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
name|account
operator|.
name|AccountApi
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
name|FetchInfo
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
name|ChangeList
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
name|common
operator|.
name|changes
operator|.
name|ListChangesOption
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
name|AccountGeneralPreferences
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
name|AccountGeneralPreferences
operator|.
name|DownloadScheme
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
name|event
operator|.
name|dom
operator|.
name|client
operator|.
name|ChangeEvent
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
name|ChangeHandler
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
name|Anchor
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
name|FlexTable
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
name|HorizontalPanel
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
name|InlineLabel
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
name|ListBox
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
name|clippy
operator|.
name|client
operator|.
name|CopyableLabel
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
import|;
end_import

begin_class
DECL|class|DownloadBox
class|class
name|DownloadBox
extends|extends
name|VerticalPanel
block|{
DECL|field|change
specifier|private
specifier|final
name|ChangeInfo
name|change
decl_stmt|;
DECL|field|revision
specifier|private
specifier|final
name|String
name|revision
decl_stmt|;
DECL|field|psId
specifier|private
specifier|final
name|PatchSet
operator|.
name|Id
name|psId
decl_stmt|;
DECL|field|commandTable
specifier|private
specifier|final
name|FlexTable
name|commandTable
decl_stmt|;
DECL|field|scheme
specifier|private
specifier|final
name|ListBox
name|scheme
decl_stmt|;
DECL|field|fetch
specifier|private
name|NativeMap
argument_list|<
name|FetchInfo
argument_list|>
name|fetch
decl_stmt|;
DECL|method|DownloadBox (ChangeInfo change, String revision, PatchSet.Id psId)
name|DownloadBox
parameter_list|(
name|ChangeInfo
name|change
parameter_list|,
name|String
name|revision
parameter_list|,
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|)
block|{
name|this
operator|.
name|change
operator|=
name|change
expr_stmt|;
name|this
operator|.
name|revision
operator|=
name|revision
expr_stmt|;
name|this
operator|.
name|psId
operator|=
name|psId
expr_stmt|;
name|this
operator|.
name|commandTable
operator|=
operator|new
name|FlexTable
argument_list|()
expr_stmt|;
name|this
operator|.
name|scheme
operator|=
operator|new
name|ListBox
argument_list|()
expr_stmt|;
name|this
operator|.
name|scheme
operator|.
name|addChangeHandler
argument_list|(
operator|new
name|ChangeHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onChange
parameter_list|(
name|ChangeEvent
name|event
parameter_list|)
block|{
name|renderCommands
argument_list|()
expr_stmt|;
if|if
condition|(
name|Gerrit
operator|.
name|isSignedIn
argument_list|()
condition|)
block|{
name|saveScheme
argument_list|()
expr_stmt|;
block|}
block|}
block|}
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
name|downloadBox
argument_list|()
argument_list|)
expr_stmt|;
name|commandTable
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
name|downloadBoxTable
argument_list|()
argument_list|)
expr_stmt|;
name|scheme
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
name|downloadBoxScheme
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|commandTable
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
if|if
condition|(
name|fetch
operator|==
literal|null
condition|)
block|{
name|RestApi
name|call
init|=
name|ChangeApi
operator|.
name|detail
argument_list|(
name|change
operator|.
name|legacy_id
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
name|ChangeList
operator|.
name|addOptions
argument_list|(
name|call
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|revision
operator|.
name|equals
argument_list|(
name|change
operator|.
name|current_revision
argument_list|()
argument_list|)
condition|?
name|ListChangesOption
operator|.
name|CURRENT_REVISION
else|:
name|ListChangesOption
operator|.
name|ALL_REVISIONS
argument_list|,
name|ListChangesOption
operator|.
name|DOWNLOAD_COMMANDS
argument_list|)
argument_list|)
expr_stmt|;
name|call
operator|.
name|get
argument_list|(
operator|new
name|AsyncCallback
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
name|fetch
operator|=
name|result
operator|.
name|revision
argument_list|(
name|revision
argument_list|)
operator|.
name|fetch
argument_list|()
expr_stmt|;
name|renderScheme
argument_list|()
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
block|}
DECL|method|renderCommands ()
specifier|private
name|void
name|renderCommands
parameter_list|()
block|{
name|commandTable
operator|.
name|removeAllRows
argument_list|()
expr_stmt|;
if|if
condition|(
name|scheme
operator|.
name|getItemCount
argument_list|()
operator|>
literal|0
condition|)
block|{
name|FetchInfo
name|fetchInfo
init|=
name|fetch
operator|.
name|get
argument_list|(
name|scheme
operator|.
name|getValue
argument_list|(
name|scheme
operator|.
name|getSelectedIndex
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|commandName
range|:
name|Natives
operator|.
name|keys
argument_list|(
name|fetchInfo
operator|.
name|commands
argument_list|()
argument_list|)
control|)
block|{
name|CopyableLabel
name|copyLabel
init|=
operator|new
name|CopyableLabel
argument_list|(
name|fetchInfo
operator|.
name|command
argument_list|(
name|commandName
argument_list|)
argument_list|)
decl_stmt|;
name|copyLabel
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
name|downloadBoxCopyLabel
argument_list|()
argument_list|)
expr_stmt|;
name|insertCommand
argument_list|(
name|commandName
argument_list|,
name|copyLabel
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|change
operator|.
name|revision
argument_list|(
name|revision
argument_list|)
operator|.
name|commit
argument_list|()
operator|.
name|parents
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|1
condition|)
block|{
name|insertPatch
argument_list|()
expr_stmt|;
block|}
name|insertCommand
argument_list|(
literal|null
argument_list|,
name|scheme
argument_list|)
expr_stmt|;
block|}
DECL|method|insertPatch ()
specifier|private
name|void
name|insertPatch
parameter_list|()
block|{
name|String
name|id
init|=
name|revision
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|7
argument_list|)
decl_stmt|;
name|Anchor
name|patchBase64
init|=
operator|new
name|Anchor
argument_list|(
name|id
operator|+
literal|".diff.base64"
argument_list|)
decl_stmt|;
name|patchBase64
operator|.
name|setHref
argument_list|(
operator|new
name|RestApi
argument_list|(
literal|"/changes/"
argument_list|)
operator|.
name|id
argument_list|(
name|psId
operator|.
name|getParentKey
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|view
argument_list|(
literal|"revisions"
argument_list|)
operator|.
name|id
argument_list|(
name|revision
argument_list|)
operator|.
name|view
argument_list|(
literal|"patch"
argument_list|)
operator|.
name|addParameterTrue
argument_list|(
literal|"download"
argument_list|)
operator|.
name|url
argument_list|()
argument_list|)
expr_stmt|;
name|Anchor
name|patchZip
init|=
operator|new
name|Anchor
argument_list|(
name|id
operator|+
literal|".diff.zip"
argument_list|)
decl_stmt|;
name|patchZip
operator|.
name|setHref
argument_list|(
operator|new
name|RestApi
argument_list|(
literal|"/changes/"
argument_list|)
operator|.
name|id
argument_list|(
name|psId
operator|.
name|getParentKey
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|view
argument_list|(
literal|"revisions"
argument_list|)
operator|.
name|id
argument_list|(
name|revision
argument_list|)
operator|.
name|view
argument_list|(
literal|"patch"
argument_list|)
operator|.
name|addParameterTrue
argument_list|(
literal|"zip"
argument_list|)
operator|.
name|url
argument_list|()
argument_list|)
expr_stmt|;
name|HorizontalPanel
name|p
init|=
operator|new
name|HorizontalPanel
argument_list|()
decl_stmt|;
name|p
operator|.
name|add
argument_list|(
name|patchBase64
argument_list|)
expr_stmt|;
name|InlineLabel
name|spacer
init|=
operator|new
name|InlineLabel
argument_list|(
literal|"|"
argument_list|)
decl_stmt|;
name|spacer
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
name|downloadBoxSpacer
argument_list|()
argument_list|)
expr_stmt|;
name|p
operator|.
name|add
argument_list|(
name|spacer
argument_list|)
expr_stmt|;
name|p
operator|.
name|add
argument_list|(
name|patchZip
argument_list|)
expr_stmt|;
name|insertCommand
argument_list|(
literal|"Patch-File"
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
DECL|method|insertCommand (String commandName, Widget w)
specifier|private
name|void
name|insertCommand
parameter_list|(
name|String
name|commandName
parameter_list|,
name|Widget
name|w
parameter_list|)
block|{
name|int
name|row
init|=
name|commandTable
operator|.
name|getRowCount
argument_list|()
decl_stmt|;
name|commandTable
operator|.
name|insertRow
argument_list|(
name|row
argument_list|)
expr_stmt|;
name|commandTable
operator|.
name|getCellFormatter
argument_list|()
operator|.
name|addStyleName
argument_list|(
name|row
argument_list|,
literal|0
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|downloadBoxTableCommandColumn
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|commandName
operator|!=
literal|null
condition|)
block|{
name|commandTable
operator|.
name|setText
argument_list|(
name|row
argument_list|,
literal|0
argument_list|,
name|commandName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|w
operator|!=
literal|null
condition|)
block|{
name|commandTable
operator|.
name|setWidget
argument_list|(
name|row
argument_list|,
literal|1
argument_list|,
name|w
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|renderScheme ()
specifier|private
name|void
name|renderScheme
parameter_list|()
block|{
for|for
control|(
name|String
name|id
range|:
name|fetch
operator|.
name|keySet
argument_list|()
control|)
block|{
name|scheme
operator|.
name|addItem
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|scheme
operator|.
name|getItemCount
argument_list|()
operator|==
literal|0
condition|)
block|{
name|scheme
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|scheme
operator|.
name|getItemCount
argument_list|()
operator|==
literal|1
condition|)
block|{
name|scheme
operator|.
name|setSelectedIndex
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|scheme
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|select
init|=
literal|0
decl_stmt|;
name|String
name|find
init|=
name|getUserPreference
argument_list|()
decl_stmt|;
if|if
condition|(
name|find
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|scheme
operator|.
name|getItemCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|find
operator|.
name|equals
argument_list|(
name|scheme
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
name|select
operator|=
name|i
expr_stmt|;
break|break;
block|}
block|}
block|}
name|scheme
operator|.
name|setSelectedIndex
argument_list|(
name|select
argument_list|)
expr_stmt|;
block|}
block|}
name|renderCommands
argument_list|()
expr_stmt|;
block|}
DECL|method|getUserPreference ()
specifier|private
specifier|static
name|String
name|getUserPreference
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
name|DownloadScheme
name|pref
init|=
name|Gerrit
operator|.
name|getUserAccount
argument_list|()
operator|.
name|getGeneralPreferences
argument_list|()
operator|.
name|getDownloadUrl
argument_list|()
decl_stmt|;
if|if
condition|(
name|pref
operator|!=
literal|null
condition|)
block|{
switch|switch
condition|(
name|pref
condition|)
block|{
case|case
name|ANON_GIT
case|:
return|return
literal|"git"
return|;
case|case
name|ANON_HTTP
case|:
return|return
literal|"anonymous http"
return|;
case|case
name|HTTP
case|:
return|return
literal|"http"
return|;
case|case
name|SSH
case|:
return|return
literal|"ssh"
return|;
case|case
name|REPO_DOWNLOAD
case|:
return|return
literal|"repo"
return|;
default|default:
return|return
literal|null
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|saveScheme ()
specifier|private
name|void
name|saveScheme
parameter_list|()
block|{
name|DownloadScheme
name|scheme
init|=
name|getSelectedScheme
argument_list|()
decl_stmt|;
name|AccountGeneralPreferences
name|pref
init|=
name|Gerrit
operator|.
name|getUserAccount
argument_list|()
operator|.
name|getGeneralPreferences
argument_list|()
decl_stmt|;
if|if
condition|(
name|scheme
operator|!=
literal|null
operator|&&
name|scheme
operator|!=
name|pref
operator|.
name|getDownloadUrl
argument_list|()
condition|)
block|{
name|pref
operator|.
name|setDownloadUrl
argument_list|(
name|scheme
argument_list|)
expr_stmt|;
name|PreferenceInput
name|in
init|=
name|PreferenceInput
operator|.
name|create
argument_list|()
decl_stmt|;
name|in
operator|.
name|download_scheme
argument_list|(
name|scheme
argument_list|)
expr_stmt|;
name|AccountApi
operator|.
name|self
argument_list|()
operator|.
name|view
argument_list|(
literal|"preferences"
argument_list|)
operator|.
name|post
argument_list|(
name|in
argument_list|,
operator|new
name|AsyncCallback
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
block|{             }
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
DECL|method|getSelectedScheme ()
specifier|private
name|DownloadScheme
name|getSelectedScheme
parameter_list|()
block|{
name|String
name|id
init|=
name|scheme
operator|.
name|getValue
argument_list|(
name|scheme
operator|.
name|getSelectedIndex
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"git"
operator|.
name|equals
argument_list|(
name|id
argument_list|)
condition|)
block|{
return|return
name|DownloadScheme
operator|.
name|ANON_GIT
return|;
block|}
elseif|else
if|if
condition|(
literal|"anonymous http"
operator|.
name|equals
argument_list|(
name|id
argument_list|)
condition|)
block|{
return|return
name|DownloadScheme
operator|.
name|ANON_HTTP
return|;
block|}
elseif|else
if|if
condition|(
literal|"http"
operator|.
name|equals
argument_list|(
name|id
argument_list|)
condition|)
block|{
return|return
name|DownloadScheme
operator|.
name|HTTP
return|;
block|}
elseif|else
if|if
condition|(
literal|"ssh"
operator|.
name|equals
argument_list|(
name|id
argument_list|)
condition|)
block|{
return|return
name|DownloadScheme
operator|.
name|SSH
return|;
block|}
elseif|else
if|if
condition|(
literal|"repo"
operator|.
name|equals
argument_list|(
name|id
argument_list|)
condition|)
block|{
return|return
name|DownloadScheme
operator|.
name|REPO_DOWNLOAD
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|class|PreferenceInput
specifier|private
specifier|static
class|class
name|PreferenceInput
extends|extends
name|JavaScriptObject
block|{
DECL|method|create ()
specifier|static
name|PreferenceInput
name|create
parameter_list|()
block|{
return|return
name|createObject
argument_list|()
operator|.
name|cast
argument_list|()
return|;
block|}
DECL|method|download_scheme (DownloadScheme s)
specifier|final
name|void
name|download_scheme
parameter_list|(
name|DownloadScheme
name|s
parameter_list|)
block|{
name|download_scheme0
argument_list|(
name|s
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|download_scheme0 (String n)
specifier|private
specifier|final
specifier|native
name|void
name|download_scheme0
parameter_list|(
name|String
name|n
parameter_list|)
comment|/*-{       this.download_scheme = n;     }-*/
function_decl|;
DECL|method|PreferenceInput ()
specifier|protected
name|PreferenceInput
parameter_list|()
block|{     }
block|}
block|}
end_class

end_unit

