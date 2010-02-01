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
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|FormatUtil
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
name|ui
operator|.
name|AccountDashboardLink
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
name|ChangeDetail
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
name|PatchSetDetail
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
name|Account
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
name|ApprovalCategory
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
name|Branch
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
name|ChangeMessage
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
name|PatchSet
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
name|PatchSetInfo
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
name|Project
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
name|UserIdentity
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
name|logical
operator|.
name|shared
operator|.
name|OpenEvent
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
name|OpenHandler
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
name|DisclosurePanel
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
name|Collections
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
DECL|class|PatchSetPanel
class|class
name|PatchSetPanel
extends|extends
name|Composite
implements|implements
name|OpenHandler
argument_list|<
name|DisclosurePanel
argument_list|>
block|{
DECL|field|R_AUTHOR
specifier|private
specifier|static
specifier|final
name|int
name|R_AUTHOR
init|=
literal|0
decl_stmt|;
DECL|field|R_COMMITTER
specifier|private
specifier|static
specifier|final
name|int
name|R_COMMITTER
init|=
literal|1
decl_stmt|;
DECL|field|R_DOWNLOAD
specifier|private
specifier|static
specifier|final
name|int
name|R_DOWNLOAD
init|=
literal|2
decl_stmt|;
DECL|field|R_CNT
specifier|private
specifier|static
specifier|final
name|int
name|R_CNT
init|=
literal|3
decl_stmt|;
DECL|field|changeScreen
specifier|private
specifier|final
name|ChangeScreen
name|changeScreen
decl_stmt|;
DECL|field|changeDetail
specifier|private
specifier|final
name|ChangeDetail
name|changeDetail
decl_stmt|;
DECL|field|patchSet
specifier|private
specifier|final
name|PatchSet
name|patchSet
decl_stmt|;
DECL|field|body
specifier|private
specifier|final
name|FlowPanel
name|body
decl_stmt|;
DECL|field|infoTable
specifier|private
name|Grid
name|infoTable
decl_stmt|;
DECL|field|actionsPanel
specifier|private
name|Panel
name|actionsPanel
decl_stmt|;
DECL|field|patchTable
specifier|private
name|PatchTable
name|patchTable
decl_stmt|;
DECL|method|PatchSetPanel (final ChangeScreen parent, final ChangeDetail detail, final PatchSet ps)
name|PatchSetPanel
parameter_list|(
specifier|final
name|ChangeScreen
name|parent
parameter_list|,
specifier|final
name|ChangeDetail
name|detail
parameter_list|,
specifier|final
name|PatchSet
name|ps
parameter_list|)
block|{
name|changeScreen
operator|=
name|parent
expr_stmt|;
name|changeDetail
operator|=
name|detail
expr_stmt|;
name|patchSet
operator|=
name|ps
expr_stmt|;
name|body
operator|=
operator|new
name|FlowPanel
argument_list|()
expr_stmt|;
name|initWidget
argument_list|(
name|body
argument_list|)
expr_stmt|;
block|}
comment|/**    * Display the table showing the Author, Committer and Download links,    * followed by the action buttons.    */
DECL|method|ensureLoaded (final PatchSetDetail detail)
specifier|public
name|void
name|ensureLoaded
parameter_list|(
specifier|final
name|PatchSetDetail
name|detail
parameter_list|)
block|{
name|infoTable
operator|=
operator|new
name|Grid
argument_list|(
name|R_CNT
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|infoTable
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
name|infoBlock
argument_list|()
argument_list|)
expr_stmt|;
name|infoTable
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
name|patchSetInfoBlock
argument_list|()
argument_list|)
expr_stmt|;
name|initRow
argument_list|(
name|R_AUTHOR
argument_list|,
name|Util
operator|.
name|C
operator|.
name|patchSetInfoAuthor
argument_list|()
argument_list|)
expr_stmt|;
name|initRow
argument_list|(
name|R_COMMITTER
argument_list|,
name|Util
operator|.
name|C
operator|.
name|patchSetInfoCommitter
argument_list|()
argument_list|)
expr_stmt|;
name|initRow
argument_list|(
name|R_DOWNLOAD
argument_list|,
name|Util
operator|.
name|C
operator|.
name|patchSetInfoDownload
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|CellFormatter
name|itfmt
init|=
name|infoTable
operator|.
name|getCellFormatter
argument_list|()
decl_stmt|;
name|itfmt
operator|.
name|addStyleName
argument_list|(
literal|0
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
name|topmost
argument_list|()
argument_list|)
expr_stmt|;
name|itfmt
operator|.
name|addStyleName
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|topmost
argument_list|()
argument_list|)
expr_stmt|;
name|itfmt
operator|.
name|addStyleName
argument_list|(
name|R_CNT
operator|-
literal|1
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
name|bottomheader
argument_list|()
argument_list|)
expr_stmt|;
name|itfmt
operator|.
name|addStyleName
argument_list|(
name|R_AUTHOR
argument_list|,
literal|1
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|useridentity
argument_list|()
argument_list|)
expr_stmt|;
name|itfmt
operator|.
name|addStyleName
argument_list|(
name|R_COMMITTER
argument_list|,
literal|1
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|useridentity
argument_list|()
argument_list|)
expr_stmt|;
name|itfmt
operator|.
name|addStyleName
argument_list|(
name|R_DOWNLOAD
argument_list|,
literal|1
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|command
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|PatchSetInfo
name|info
init|=
name|detail
operator|.
name|getInfo
argument_list|()
decl_stmt|;
name|displayUserIdentity
argument_list|(
name|R_AUTHOR
argument_list|,
name|info
operator|.
name|getAuthor
argument_list|()
argument_list|)
expr_stmt|;
name|displayUserIdentity
argument_list|(
name|R_COMMITTER
argument_list|,
name|info
operator|.
name|getCommitter
argument_list|()
argument_list|)
expr_stmt|;
name|displayDownload
argument_list|()
expr_stmt|;
name|patchTable
operator|=
operator|new
name|PatchTable
argument_list|()
expr_stmt|;
name|patchTable
operator|.
name|setSavePointerId
argument_list|(
literal|"PatchTable "
operator|+
name|patchSet
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|patchTable
operator|.
name|display
argument_list|(
name|info
operator|.
name|getKey
argument_list|()
argument_list|,
name|detail
operator|.
name|getPatches
argument_list|()
argument_list|)
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
name|infoTable
argument_list|)
expr_stmt|;
name|actionsPanel
operator|=
operator|new
name|FlowPanel
argument_list|()
expr_stmt|;
name|actionsPanel
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
name|patchSetActions
argument_list|()
argument_list|)
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
name|actionsPanel
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
name|populateReviewAction
argument_list|()
expr_stmt|;
if|if
condition|(
name|changeDetail
operator|.
name|isCurrentPatchSet
argument_list|(
name|detail
argument_list|)
condition|)
block|{
name|populateActions
argument_list|(
name|detail
argument_list|)
expr_stmt|;
block|}
block|}
name|body
operator|.
name|add
argument_list|(
name|patchTable
argument_list|)
expr_stmt|;
block|}
DECL|method|displayDownload ()
specifier|private
name|void
name|displayDownload
parameter_list|()
block|{
specifier|final
name|Branch
operator|.
name|NameKey
name|branchKey
init|=
name|changeDetail
operator|.
name|getChange
argument_list|()
operator|.
name|getDest
argument_list|()
decl_stmt|;
specifier|final
name|Project
operator|.
name|NameKey
name|projectKey
init|=
name|changeDetail
operator|.
name|getChange
argument_list|()
operator|.
name|getProject
argument_list|()
decl_stmt|;
specifier|final
name|String
name|projectName
init|=
name|projectKey
operator|.
name|get
argument_list|()
decl_stmt|;
specifier|final
name|FlowPanel
name|downloads
init|=
operator|new
name|FlowPanel
argument_list|()
decl_stmt|;
if|if
condition|(
name|Gerrit
operator|.
name|getConfig
argument_list|()
operator|.
name|isUseRepoDownload
argument_list|()
condition|)
block|{
comment|// This site prefers usage of the 'repo' tool, so suggest
comment|// that for easy fetch.
comment|//
specifier|final
name|StringBuilder
name|r
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|"repo download "
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|projectName
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|changeDetail
operator|.
name|getChange
argument_list|()
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|patchSet
operator|.
name|getPatchSetId
argument_list|()
argument_list|)
expr_stmt|;
name|downloads
operator|.
name|add
argument_list|(
operator|new
name|CopyableLabel
argument_list|(
name|r
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|changeDetail
operator|.
name|isAllowsAnonymous
argument_list|()
condition|)
block|{
if|if
condition|(
name|Gerrit
operator|.
name|getConfig
argument_list|()
operator|.
name|getGitDaemonUrl
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|StringBuilder
name|r
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|"git pull "
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|Gerrit
operator|.
name|getConfig
argument_list|()
operator|.
name|getGitDaemonUrl
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|projectName
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|patchSet
operator|.
name|getRefName
argument_list|()
argument_list|)
expr_stmt|;
name|downloads
operator|.
name|add
argument_list|(
operator|new
name|CopyableLabel
argument_list|(
name|r
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|StringBuilder
name|r
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|"git pull "
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|GWT
operator|.
name|getHostPageBaseURL
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|"p/"
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|projectName
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|patchSet
operator|.
name|getRefName
argument_list|()
argument_list|)
expr_stmt|;
name|downloads
operator|.
name|add
argument_list|(
operator|new
name|CopyableLabel
argument_list|(
name|r
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|Gerrit
operator|.
name|isSignedIn
argument_list|()
operator|&&
name|Gerrit
operator|.
name|getUserAccount
argument_list|()
operator|.
name|getUserName
argument_list|()
operator|!=
literal|null
operator|&&
name|Gerrit
operator|.
name|getUserAccount
argument_list|()
operator|.
name|getUserName
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// The user is signed in and anonymous access isn't allowed.
comment|//
if|if
condition|(
name|Gerrit
operator|.
name|getConfig
argument_list|()
operator|.
name|getSshdAddress
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|String
name|sshAddr
init|=
name|Gerrit
operator|.
name|getConfig
argument_list|()
operator|.
name|getSshdAddress
argument_list|()
decl_stmt|;
specifier|final
name|StringBuilder
name|r
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|"git pull ssh://"
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|Gerrit
operator|.
name|getUserAccount
argument_list|()
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|"@"
argument_list|)
expr_stmt|;
if|if
condition|(
name|sshAddr
operator|.
name|startsWith
argument_list|(
literal|"*:"
argument_list|)
operator|||
literal|""
operator|.
name|equals
argument_list|(
name|sshAddr
argument_list|)
condition|)
block|{
name|r
operator|.
name|append
argument_list|(
name|Window
operator|.
name|Location
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sshAddr
operator|.
name|startsWith
argument_list|(
literal|"*"
argument_list|)
condition|)
block|{
name|sshAddr
operator|=
name|sshAddr
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|r
operator|.
name|append
argument_list|(
name|sshAddr
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|projectName
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|patchSet
operator|.
name|getRefName
argument_list|()
argument_list|)
expr_stmt|;
name|downloads
operator|.
name|add
argument_list|(
operator|new
name|CopyableLabel
argument_list|(
name|r
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|base
init|=
name|GWT
operator|.
name|getHostPageBaseURL
argument_list|()
decl_stmt|;
name|int
name|p
init|=
name|base
operator|.
name|indexOf
argument_list|(
literal|"://"
argument_list|)
decl_stmt|;
name|int
name|s
init|=
name|base
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|,
name|p
operator|+
literal|3
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|<
literal|0
condition|)
block|{
name|s
operator|=
name|base
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
name|String
name|host
init|=
name|base
operator|.
name|substring
argument_list|(
name|p
operator|+
literal|3
argument_list|,
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|host
operator|.
name|contains
argument_list|(
literal|"@"
argument_list|)
condition|)
block|{
name|host
operator|=
name|host
operator|.
name|substring
argument_list|(
name|host
operator|.
name|indexOf
argument_list|(
literal|'@'
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|final
name|StringBuilder
name|r
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|"git pull "
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|base
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
operator|+
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|Gerrit
operator|.
name|getUserAccount
argument_list|()
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|'@'
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|host
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|base
operator|.
name|substring
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|"p/"
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|projectName
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|patchSet
operator|.
name|getRefName
argument_list|()
argument_list|)
expr_stmt|;
name|downloads
operator|.
name|add
argument_list|(
operator|new
name|CopyableLabel
argument_list|(
name|r
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|infoTable
operator|.
name|setWidget
argument_list|(
name|R_DOWNLOAD
argument_list|,
literal|1
argument_list|,
name|downloads
argument_list|)
expr_stmt|;
block|}
DECL|method|displayUserIdentity (final int row, final UserIdentity who)
specifier|private
name|void
name|displayUserIdentity
parameter_list|(
specifier|final
name|int
name|row
parameter_list|,
specifier|final
name|UserIdentity
name|who
parameter_list|)
block|{
if|if
condition|(
name|who
operator|==
literal|null
condition|)
block|{
name|infoTable
operator|.
name|clearCell
argument_list|(
name|row
argument_list|,
literal|1
argument_list|)
expr_stmt|;
return|return;
block|}
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
name|setStyleName
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|patchSetUserIdentity
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|who
operator|.
name|getName
argument_list|()
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Account
operator|.
name|Id
name|aId
init|=
name|who
operator|.
name|getAccount
argument_list|()
decl_stmt|;
if|if
condition|(
name|aId
operator|!=
literal|null
condition|)
block|{
name|fp
operator|.
name|add
argument_list|(
operator|new
name|AccountDashboardLink
argument_list|(
name|who
operator|.
name|getName
argument_list|()
argument_list|,
name|aId
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|InlineLabel
name|lbl
init|=
operator|new
name|InlineLabel
argument_list|(
name|who
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|lbl
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
name|accountName
argument_list|()
argument_list|)
expr_stmt|;
name|fp
operator|.
name|add
argument_list|(
name|lbl
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|who
operator|.
name|getEmail
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|fp
operator|.
name|add
argument_list|(
operator|new
name|InlineLabel
argument_list|(
literal|"<"
operator|+
name|who
operator|.
name|getEmail
argument_list|()
operator|+
literal|">"
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|who
operator|.
name|getDate
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|fp
operator|.
name|add
argument_list|(
operator|new
name|InlineLabel
argument_list|(
name|FormatUtil
operator|.
name|mediumFormat
argument_list|(
name|who
operator|.
name|getDate
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|infoTable
operator|.
name|setWidget
argument_list|(
name|row
argument_list|,
literal|1
argument_list|,
name|fp
argument_list|)
expr_stmt|;
block|}
DECL|method|populateActions (final PatchSetDetail detail)
specifier|private
name|void
name|populateActions
parameter_list|(
specifier|final
name|PatchSetDetail
name|detail
parameter_list|)
block|{
specifier|final
name|boolean
name|isOpen
init|=
name|changeDetail
operator|.
name|getChange
argument_list|()
operator|.
name|getStatus
argument_list|()
operator|.
name|isOpen
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|ApprovalCategory
operator|.
name|Id
argument_list|>
name|allowed
init|=
name|changeDetail
operator|.
name|getCurrentActions
argument_list|()
decl_stmt|;
if|if
condition|(
name|allowed
operator|==
literal|null
condition|)
block|{
name|allowed
operator|=
name|Collections
operator|.
name|emptySet
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|isOpen
operator|&&
name|allowed
operator|.
name|contains
argument_list|(
name|ApprovalCategory
operator|.
name|SUBMIT
argument_list|)
condition|)
block|{
specifier|final
name|Button
name|b
init|=
operator|new
name|Button
argument_list|(
name|Util
operator|.
name|M
operator|.
name|submitPatchSet
argument_list|(
name|detail
operator|.
name|getPatchSet
argument_list|()
operator|.
name|getPatchSetId
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|b
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
name|b
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Util
operator|.
name|MANAGE_SVC
operator|.
name|submit
argument_list|(
name|patchSet
operator|.
name|getId
argument_list|()
argument_list|,
operator|new
name|GerritCallback
argument_list|<
name|ChangeDetail
argument_list|>
argument_list|()
block|{
specifier|public
name|void
name|onSuccess
parameter_list|(
name|ChangeDetail
name|result
parameter_list|)
block|{
name|onSubmitResult
argument_list|(
name|result
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
name|b
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
argument_list|)
expr_stmt|;
name|actionsPanel
operator|.
name|add
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|changeDetail
operator|.
name|canAbandon
argument_list|()
condition|)
block|{
specifier|final
name|Button
name|b
init|=
operator|new
name|Button
argument_list|(
name|Util
operator|.
name|C
operator|.
name|buttonAbandonChangeBegin
argument_list|()
argument_list|)
decl_stmt|;
name|b
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
operator|new
name|AbandonChangeDialog
argument_list|(
name|patchSet
operator|.
name|getId
argument_list|()
argument_list|,
operator|new
name|AsyncCallback
argument_list|<
name|ChangeDetail
argument_list|>
argument_list|()
block|{
specifier|public
name|void
name|onSuccess
parameter_list|(
name|ChangeDetail
name|result
parameter_list|)
block|{
name|changeScreen
operator|.
name|display
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|caught
parameter_list|)
block|{
name|b
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
operator|.
name|center
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|actionsPanel
operator|.
name|add
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|populateReviewAction ()
specifier|private
name|void
name|populateReviewAction
parameter_list|()
block|{
specifier|final
name|Button
name|b
init|=
operator|new
name|Button
argument_list|(
name|Util
operator|.
name|C
operator|.
name|buttonReview
argument_list|()
argument_list|)
decl_stmt|;
name|b
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
name|Gerrit
operator|.
name|display
argument_list|(
literal|"change,publish,"
operator|+
name|patchSet
operator|.
name|getId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
operator|new
name|PublishCommentScreen
argument_list|(
name|patchSet
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|actionsPanel
operator|.
name|add
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onOpen (final OpenEvent<DisclosurePanel> event)
specifier|public
name|void
name|onOpen
parameter_list|(
specifier|final
name|OpenEvent
argument_list|<
name|DisclosurePanel
argument_list|>
name|event
parameter_list|)
block|{
if|if
condition|(
name|infoTable
operator|==
literal|null
condition|)
block|{
name|Util
operator|.
name|DETAIL_SVC
operator|.
name|patchSetDetail
argument_list|(
name|patchSet
operator|.
name|getId
argument_list|()
argument_list|,
operator|new
name|GerritCallback
argument_list|<
name|PatchSetDetail
argument_list|>
argument_list|()
block|{
specifier|public
name|void
name|onSuccess
parameter_list|(
specifier|final
name|PatchSetDetail
name|result
parameter_list|)
block|{
name|ensureLoaded
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|initRow (final int row, final String name)
specifier|private
name|void
name|initRow
parameter_list|(
specifier|final
name|int
name|row
parameter_list|,
specifier|final
name|String
name|name
parameter_list|)
block|{
name|infoTable
operator|.
name|setText
argument_list|(
name|row
argument_list|,
literal|0
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|infoTable
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
name|header
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|onSubmitResult (final ChangeDetail result)
specifier|private
name|void
name|onSubmitResult
parameter_list|(
specifier|final
name|ChangeDetail
name|result
parameter_list|)
block|{
if|if
condition|(
name|result
operator|.
name|getChange
argument_list|()
operator|.
name|getStatus
argument_list|()
operator|==
name|Change
operator|.
name|Status
operator|.
name|NEW
condition|)
block|{
comment|// The submit failed. Try to locate the message and display
comment|// it to the user, it should be the last one created by Gerrit.
comment|//
name|ChangeMessage
name|msg
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|result
operator|.
name|getMessages
argument_list|()
operator|!=
literal|null
operator|&&
name|result
operator|.
name|getMessages
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
name|result
operator|.
name|getMessages
argument_list|()
operator|.
name|size
argument_list|()
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
if|if
condition|(
name|result
operator|.
name|getMessages
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getAuthor
argument_list|()
operator|==
literal|null
condition|)
block|{
name|msg
operator|=
name|result
operator|.
name|getMessages
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
if|if
condition|(
name|msg
operator|!=
literal|null
condition|)
block|{
operator|new
name|SubmitFailureDialog
argument_list|(
name|result
argument_list|,
name|msg
argument_list|)
operator|.
name|center
argument_list|()
expr_stmt|;
block|}
block|}
name|changeScreen
operator|.
name|display
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

