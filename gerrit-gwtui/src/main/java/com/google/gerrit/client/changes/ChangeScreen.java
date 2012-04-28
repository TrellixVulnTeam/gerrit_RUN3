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
name|ui
operator|.
name|CommentPanel
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
name|ComplexDisclosurePanel
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
name|ExpandAllCommand
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
name|LinkMenuBar
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
name|NeedsSignInKeyCommand
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
name|data
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
name|common
operator|.
name|data
operator|.
name|AccountInfoCache
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
name|gerrit
operator|.
name|reviewdb
operator|.
name|client
operator|.
name|Change
operator|.
name|Status
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
name|event
operator|.
name|logical
operator|.
name|shared
operator|.
name|ValueChangeHandler
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
name|shared
operator|.
name|HandlerRegistration
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
name|i18n
operator|.
name|client
operator|.
name|LocaleInfo
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
name|Panel
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
name|java
operator|.
name|sql
operator|.
name|Timestamp
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
DECL|class|ChangeScreen
specifier|public
class|class
name|ChangeScreen
extends|extends
name|Screen
implements|implements
name|ValueChangeHandler
argument_list|<
name|ChangeDetail
argument_list|>
block|{
DECL|field|changeId
specifier|private
specifier|final
name|Change
operator|.
name|Id
name|changeId
decl_stmt|;
DECL|field|openPatchSetId
specifier|private
specifier|final
name|PatchSet
operator|.
name|Id
name|openPatchSetId
decl_stmt|;
DECL|field|detailCache
specifier|private
name|ChangeDetailCache
name|detailCache
decl_stmt|;
DECL|field|starred
specifier|private
name|StarCache
name|starred
decl_stmt|;
DECL|field|starChange
specifier|private
name|Image
name|starChange
decl_stmt|;
DECL|field|descriptionBlock
specifier|private
name|ChangeDescriptionBlock
name|descriptionBlock
decl_stmt|;
DECL|field|approvals
specifier|private
name|ApprovalTable
name|approvals
decl_stmt|;
DECL|field|includedInTable
specifier|private
name|IncludedInTable
name|includedInTable
decl_stmt|;
DECL|field|includedInPanel
specifier|private
name|DisclosurePanel
name|includedInPanel
decl_stmt|;
DECL|field|dependenciesPanel
specifier|private
name|ComplexDisclosurePanel
name|dependenciesPanel
decl_stmt|;
DECL|field|dependencies
specifier|private
name|ChangeTable
name|dependencies
decl_stmt|;
DECL|field|dependsOn
specifier|private
name|ChangeTable
operator|.
name|Section
name|dependsOn
decl_stmt|;
DECL|field|neededBy
specifier|private
name|ChangeTable
operator|.
name|Section
name|neededBy
decl_stmt|;
DECL|field|patchSetsBlock
specifier|private
name|PatchSetsBlock
name|patchSetsBlock
decl_stmt|;
DECL|field|comments
specifier|private
name|Panel
name|comments
decl_stmt|;
DECL|field|keysNavigation
specifier|private
name|KeyCommandSet
name|keysNavigation
decl_stmt|;
DECL|field|keysAction
specifier|private
name|KeyCommandSet
name|keysAction
decl_stmt|;
DECL|field|regNavigation
specifier|private
name|HandlerRegistration
name|regNavigation
decl_stmt|;
DECL|field|regAction
specifier|private
name|HandlerRegistration
name|regAction
decl_stmt|;
DECL|field|patchesGrid
specifier|private
name|Grid
name|patchesGrid
decl_stmt|;
DECL|field|patchesList
specifier|private
name|ListBox
name|patchesList
decl_stmt|;
comment|/**    * The change id for which the old version history is valid.    */
DECL|field|currentChangeId
specifier|private
specifier|static
name|Change
operator|.
name|Id
name|currentChangeId
decl_stmt|;
comment|/**    * Which patch set id is the diff base.    */
DECL|field|diffBaseId
specifier|private
specifier|static
name|PatchSet
operator|.
name|Id
name|diffBaseId
decl_stmt|;
DECL|method|ChangeScreen (final Change.Id toShow)
specifier|public
name|ChangeScreen
parameter_list|(
specifier|final
name|Change
operator|.
name|Id
name|toShow
parameter_list|)
block|{
name|changeId
operator|=
name|toShow
expr_stmt|;
name|openPatchSetId
operator|=
literal|null
expr_stmt|;
comment|// If we have any diff stored, make sure they are applicable to the
comment|// current change, discard them otherwise.
comment|//
if|if
condition|(
name|currentChangeId
operator|!=
literal|null
operator|&&
operator|!
name|currentChangeId
operator|.
name|equals
argument_list|(
name|toShow
argument_list|)
condition|)
block|{
name|diffBaseId
operator|=
literal|null
expr_stmt|;
block|}
name|currentChangeId
operator|=
name|toShow
expr_stmt|;
block|}
DECL|method|ChangeScreen (final PatchSet.Id toShow)
specifier|public
name|ChangeScreen
parameter_list|(
specifier|final
name|PatchSet
operator|.
name|Id
name|toShow
parameter_list|)
block|{
name|changeId
operator|=
name|toShow
operator|.
name|getParentKey
argument_list|()
expr_stmt|;
name|openPatchSetId
operator|=
name|toShow
expr_stmt|;
block|}
DECL|method|ChangeScreen (final ChangeInfo c)
specifier|public
name|ChangeScreen
parameter_list|(
specifier|final
name|ChangeInfo
name|c
parameter_list|)
block|{
name|this
argument_list|(
name|c
operator|.
name|getId
argument_list|()
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
name|detailCache
operator|.
name|refresh
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
if|if
condition|(
name|regNavigation
operator|!=
literal|null
condition|)
block|{
name|regNavigation
operator|.
name|removeHandler
argument_list|()
expr_stmt|;
name|regNavigation
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|regAction
operator|!=
literal|null
condition|)
block|{
name|regAction
operator|.
name|removeHandler
argument_list|()
expr_stmt|;
name|regAction
operator|=
literal|null
expr_stmt|;
block|}
name|super
operator|.
name|onUnload
argument_list|()
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
name|regNavigation
operator|=
name|GlobalKey
operator|.
name|add
argument_list|(
name|this
argument_list|,
name|keysNavigation
argument_list|)
expr_stmt|;
name|regAction
operator|=
name|GlobalKey
operator|.
name|add
argument_list|(
name|this
argument_list|,
name|keysAction
argument_list|)
expr_stmt|;
if|if
condition|(
name|openPatchSetId
operator|!=
literal|null
condition|)
block|{
name|patchSetsBlock
operator|.
name|activate
argument_list|(
name|openPatchSetId
argument_list|)
expr_stmt|;
block|}
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
name|ChangeCache
name|cache
init|=
name|ChangeCache
operator|.
name|get
argument_list|(
name|changeId
argument_list|)
decl_stmt|;
name|detailCache
operator|=
name|cache
operator|.
name|getChangeDetailCache
argument_list|()
expr_stmt|;
name|detailCache
operator|.
name|addValueChangeHandler
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|starred
operator|=
name|cache
operator|.
name|getStarCache
argument_list|()
expr_stmt|;
name|addStyleName
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|changeScreen
argument_list|()
argument_list|)
expr_stmt|;
name|keysNavigation
operator|=
operator|new
name|KeyCommandSet
argument_list|(
name|Gerrit
operator|.
name|C
operator|.
name|sectionNavigation
argument_list|()
argument_list|)
expr_stmt|;
name|keysAction
operator|=
operator|new
name|KeyCommandSet
argument_list|(
name|Gerrit
operator|.
name|C
operator|.
name|sectionActions
argument_list|()
argument_list|)
expr_stmt|;
name|keysNavigation
operator|.
name|add
argument_list|(
operator|new
name|UpToListKeyCommand
argument_list|(
literal|0
argument_list|,
literal|'u'
argument_list|,
name|Util
operator|.
name|C
operator|.
name|upToChangeList
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|keysNavigation
operator|.
name|add
argument_list|(
operator|new
name|ExpandCollapseDependencySectionKeyCommand
argument_list|(
literal|0
argument_list|,
literal|'d'
argument_list|,
name|Util
operator|.
name|C
operator|.
name|expandCollapseDependencies
argument_list|()
argument_list|)
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
name|keysAction
operator|.
name|add
argument_list|(
name|starred
operator|.
expr|new
name|KeyCommand
argument_list|(
literal|0
argument_list|,
literal|'s'
argument_list|,
name|Util
operator|.
name|C
operator|.
name|changeTableStar
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|keysAction
operator|.
name|add
argument_list|(
operator|new
name|PublishCommentsKeyCommand
argument_list|(
literal|0
argument_list|,
literal|'r'
argument_list|,
name|Util
operator|.
name|C
operator|.
name|keyPublishComments
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|starChange
operator|=
name|starred
operator|.
name|createStar
argument_list|()
expr_stmt|;
name|starChange
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
name|changeScreenStarIcon
argument_list|()
argument_list|)
expr_stmt|;
name|setTitleWest
argument_list|(
name|starChange
argument_list|)
expr_stmt|;
block|}
name|descriptionBlock
operator|=
operator|new
name|ChangeDescriptionBlock
argument_list|()
expr_stmt|;
name|add
argument_list|(
name|descriptionBlock
argument_list|)
expr_stmt|;
name|approvals
operator|=
operator|new
name|ApprovalTable
argument_list|()
expr_stmt|;
name|add
argument_list|(
name|approvals
argument_list|)
expr_stmt|;
name|includedInPanel
operator|=
operator|new
name|DisclosurePanel
argument_list|(
name|Util
operator|.
name|C
operator|.
name|changeScreenIncludedIn
argument_list|()
argument_list|)
expr_stmt|;
name|includedInTable
operator|=
operator|new
name|IncludedInTable
argument_list|(
name|changeId
argument_list|)
expr_stmt|;
name|includedInPanel
operator|.
name|setContent
argument_list|(
name|includedInTable
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|includedInPanel
argument_list|)
expr_stmt|;
name|dependencies
operator|=
operator|new
name|ChangeTable
argument_list|()
block|{
block|{
name|table
operator|.
name|setWidth
argument_list|(
literal|"auto"
argument_list|)
expr_stmt|;
block|}
block|}
expr_stmt|;
name|dependsOn
operator|=
operator|new
name|ChangeTable
operator|.
name|Section
argument_list|(
name|Util
operator|.
name|C
operator|.
name|changeScreenDependsOn
argument_list|()
argument_list|)
expr_stmt|;
name|neededBy
operator|=
operator|new
name|ChangeTable
operator|.
name|Section
argument_list|(
name|Util
operator|.
name|C
operator|.
name|changeScreenNeededBy
argument_list|()
argument_list|)
expr_stmt|;
name|dependencies
operator|.
name|addSection
argument_list|(
name|dependsOn
argument_list|)
expr_stmt|;
name|dependencies
operator|.
name|addSection
argument_list|(
name|neededBy
argument_list|)
expr_stmt|;
name|dependenciesPanel
operator|=
operator|new
name|ComplexDisclosurePanel
argument_list|(
name|Util
operator|.
name|C
operator|.
name|changeScreenDependencies
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|dependenciesPanel
operator|.
name|setContent
argument_list|(
name|dependencies
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|dependenciesPanel
argument_list|)
expr_stmt|;
name|patchesList
operator|=
operator|new
name|ListBox
argument_list|()
expr_stmt|;
name|patchesList
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
specifier|final
name|int
name|index
init|=
name|patchesList
operator|.
name|getSelectedIndex
argument_list|()
decl_stmt|;
specifier|final
name|String
name|selectedPatchSet
init|=
name|patchesList
operator|.
name|getValue
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|==
literal|0
condition|)
block|{
name|diffBaseId
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|diffBaseId
operator|=
name|PatchSet
operator|.
name|Id
operator|.
name|parse
argument_list|(
name|selectedPatchSet
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|patchSetsBlock
operator|!=
literal|null
condition|)
block|{
name|patchSetsBlock
operator|.
name|refresh
argument_list|(
name|diffBaseId
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|patchesGrid
operator|=
operator|new
name|Grid
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|patchesGrid
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
name|selectPatchSetOldVersion
argument_list|()
argument_list|)
expr_stmt|;
name|patchesGrid
operator|.
name|setText
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|Util
operator|.
name|C
operator|.
name|oldVersionHistory
argument_list|()
argument_list|)
expr_stmt|;
name|patchesGrid
operator|.
name|setWidget
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
name|patchesList
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|patchesGrid
argument_list|)
expr_stmt|;
name|patchSetsBlock
operator|=
operator|new
name|PatchSetsBlock
argument_list|()
expr_stmt|;
name|add
argument_list|(
name|patchSetsBlock
argument_list|)
expr_stmt|;
name|comments
operator|=
operator|new
name|FlowPanel
argument_list|()
expr_stmt|;
name|comments
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
name|changeComments
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|comments
argument_list|)
expr_stmt|;
block|}
DECL|method|displayTitle (final Change.Key changeId, final String subject)
specifier|private
name|void
name|displayTitle
parameter_list|(
specifier|final
name|Change
operator|.
name|Key
name|changeId
parameter_list|,
specifier|final
name|String
name|subject
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|titleBuf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|LocaleInfo
operator|.
name|getCurrentLocale
argument_list|()
operator|.
name|isRTL
argument_list|()
condition|)
block|{
if|if
condition|(
name|subject
operator|!=
literal|null
condition|)
block|{
name|titleBuf
operator|.
name|append
argument_list|(
name|subject
argument_list|)
expr_stmt|;
name|titleBuf
operator|.
name|append
argument_list|(
literal|" :"
argument_list|)
expr_stmt|;
block|}
name|titleBuf
operator|.
name|append
argument_list|(
name|Util
operator|.
name|M
operator|.
name|changeScreenTitleId
argument_list|(
name|changeId
operator|.
name|abbreviate
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|titleBuf
operator|.
name|append
argument_list|(
name|Util
operator|.
name|M
operator|.
name|changeScreenTitleId
argument_list|(
name|changeId
operator|.
name|abbreviate
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|subject
operator|!=
literal|null
condition|)
block|{
name|titleBuf
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
expr_stmt|;
name|titleBuf
operator|.
name|append
argument_list|(
name|subject
argument_list|)
expr_stmt|;
block|}
block|}
name|setPageTitle
argument_list|(
name|titleBuf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onValueChange (ValueChangeEvent<ChangeDetail> event)
specifier|public
name|void
name|onValueChange
parameter_list|(
name|ValueChangeEvent
argument_list|<
name|ChangeDetail
argument_list|>
name|event
parameter_list|)
block|{
if|if
condition|(
name|isAttached
argument_list|()
condition|)
block|{
name|display
argument_list|(
name|event
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|display (final ChangeDetail detail)
specifier|private
name|void
name|display
parameter_list|(
specifier|final
name|ChangeDetail
name|detail
parameter_list|)
block|{
name|displayTitle
argument_list|(
name|detail
operator|.
name|getChange
argument_list|()
operator|.
name|getKey
argument_list|()
argument_list|,
name|detail
operator|.
name|getChange
argument_list|()
operator|.
name|getSubject
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|Status
operator|.
name|MERGED
operator|==
name|detail
operator|.
name|getChange
argument_list|()
operator|.
name|getStatus
argument_list|()
condition|)
block|{
name|includedInPanel
operator|.
name|setVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|includedInPanel
operator|.
name|addOpenHandler
argument_list|(
name|includedInTable
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|includedInPanel
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|dependencies
operator|.
name|setAccountInfoCache
argument_list|(
name|detail
operator|.
name|getAccounts
argument_list|()
argument_list|)
expr_stmt|;
name|approvals
operator|.
name|setAccountInfoCache
argument_list|(
name|detail
operator|.
name|getAccounts
argument_list|()
argument_list|)
expr_stmt|;
name|descriptionBlock
operator|.
name|display
argument_list|(
name|detail
operator|.
name|getChange
argument_list|()
argument_list|,
name|detail
operator|.
name|getCurrentPatchSetDetail
argument_list|()
operator|.
name|getInfo
argument_list|()
argument_list|,
name|detail
operator|.
name|getAccounts
argument_list|()
argument_list|)
expr_stmt|;
name|dependsOn
operator|.
name|display
argument_list|(
name|detail
operator|.
name|getDependsOn
argument_list|()
argument_list|)
expr_stmt|;
name|neededBy
operator|.
name|display
argument_list|(
name|detail
operator|.
name|getNeededBy
argument_list|()
argument_list|)
expr_stmt|;
name|approvals
operator|.
name|display
argument_list|(
name|detail
argument_list|)
expr_stmt|;
if|if
condition|(
name|detail
operator|.
name|getCurrentPatchSetDetail
argument_list|()
operator|.
name|getInfo
argument_list|()
operator|.
name|getParents
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|patchesList
operator|.
name|addItem
argument_list|(
name|Util
operator|.
name|C
operator|.
name|autoMerge
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|patchesList
operator|.
name|addItem
argument_list|(
name|Util
operator|.
name|C
operator|.
name|baseDiffItem
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|PatchSet
name|pId
range|:
name|detail
operator|.
name|getPatchSets
argument_list|()
control|)
block|{
if|if
condition|(
name|patchesList
operator|!=
literal|null
condition|)
block|{
name|patchesList
operator|.
name|addItem
argument_list|(
name|Util
operator|.
name|M
operator|.
name|patchSetHeader
argument_list|(
name|pId
operator|.
name|getPatchSetId
argument_list|()
argument_list|)
argument_list|,
name|pId
operator|.
name|getId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|diffBaseId
operator|!=
literal|null
operator|&&
name|patchesList
operator|!=
literal|null
condition|)
block|{
name|patchesList
operator|.
name|setSelectedIndex
argument_list|(
name|diffBaseId
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|patchSetsBlock
operator|.
name|display
argument_list|(
name|detail
argument_list|,
name|diffBaseId
argument_list|)
expr_stmt|;
name|addComments
argument_list|(
name|detail
argument_list|)
expr_stmt|;
comment|// If any dependency change is still open, or is outdated,
comment|// show our dependency list.
comment|//
name|boolean
name|depsOpen
init|=
literal|false
decl_stmt|;
name|int
name|outdated
init|=
literal|0
decl_stmt|;
if|if
condition|(
operator|!
name|detail
operator|.
name|getChange
argument_list|()
operator|.
name|getStatus
argument_list|()
operator|.
name|isClosed
argument_list|()
operator|&&
name|detail
operator|.
name|getDependsOn
argument_list|()
operator|!=
literal|null
condition|)
block|{
for|for
control|(
specifier|final
name|ChangeInfo
name|ci
range|:
name|detail
operator|.
name|getDependsOn
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|ci
operator|.
name|isLatest
argument_list|()
condition|)
block|{
name|depsOpen
operator|=
literal|true
expr_stmt|;
name|outdated
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ci
operator|.
name|getStatus
argument_list|()
operator|!=
name|Change
operator|.
name|Status
operator|.
name|MERGED
condition|)
block|{
name|depsOpen
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
name|dependenciesPanel
operator|.
name|setOpen
argument_list|(
name|depsOpen
argument_list|)
expr_stmt|;
name|dependenciesPanel
operator|.
name|getHeader
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
if|if
condition|(
name|outdated
operator|>
literal|0
condition|)
block|{
name|dependenciesPanel
operator|.
name|getHeader
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|InlineLabel
argument_list|(
name|Util
operator|.
name|M
operator|.
name|outdatedHeader
argument_list|(
name|outdated
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|isCurrentView
argument_list|()
condition|)
block|{
name|display
argument_list|()
expr_stmt|;
block|}
name|patchSetsBlock
operator|.
name|setRegisterKeys
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|addComments (final ChangeDetail detail)
specifier|private
name|void
name|addComments
parameter_list|(
specifier|final
name|ChangeDetail
name|detail
parameter_list|)
block|{
name|comments
operator|.
name|clear
argument_list|()
expr_stmt|;
specifier|final
name|AccountInfoCache
name|accts
init|=
name|detail
operator|.
name|getAccounts
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|ChangeMessage
argument_list|>
name|msgList
init|=
name|detail
operator|.
name|getMessages
argument_list|()
decl_stmt|;
name|HorizontalPanel
name|title
init|=
operator|new
name|HorizontalPanel
argument_list|()
decl_stmt|;
name|title
operator|.
name|setWidth
argument_list|(
literal|"100%"
argument_list|)
expr_stmt|;
name|title
operator|.
name|add
argument_list|(
operator|new
name|Label
argument_list|(
name|Util
operator|.
name|C
operator|.
name|changeScreenComments
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|msgList
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|title
operator|.
name|add
argument_list|(
name|messagesMenuBar
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|title
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
name|blockHeader
argument_list|()
argument_list|)
expr_stmt|;
name|comments
operator|.
name|add
argument_list|(
name|title
argument_list|)
expr_stmt|;
specifier|final
name|long
name|AGE
init|=
literal|7
operator|*
literal|24
operator|*
literal|60
operator|*
literal|60
operator|*
literal|1000L
decl_stmt|;
specifier|final
name|Timestamp
name|aged
init|=
operator|new
name|Timestamp
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|AGE
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
name|msgList
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|ChangeMessage
name|msg
init|=
name|msgList
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|AccountInfo
name|author
decl_stmt|;
if|if
condition|(
name|msg
operator|.
name|getAuthor
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|author
operator|=
name|accts
operator|.
name|get
argument_list|(
name|msg
operator|.
name|getAuthor
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|Account
name|gerrit
init|=
operator|new
name|Account
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|gerrit
operator|.
name|setFullName
argument_list|(
name|Util
operator|.
name|C
operator|.
name|messageNoAuthor
argument_list|()
argument_list|)
expr_stmt|;
name|author
operator|=
operator|new
name|AccountInfo
argument_list|(
name|gerrit
argument_list|)
expr_stmt|;
block|}
name|boolean
name|isRecent
decl_stmt|;
if|if
condition|(
name|i
operator|==
name|msgList
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|)
block|{
name|isRecent
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
comment|// TODO Instead of opening messages by strict age, do it by "unread"?
name|isRecent
operator|=
name|msg
operator|.
name|getWrittenOn
argument_list|()
operator|.
name|after
argument_list|(
name|aged
argument_list|)
expr_stmt|;
block|}
specifier|final
name|CommentPanel
name|cp
init|=
operator|new
name|CommentPanel
argument_list|(
name|author
argument_list|,
name|msg
operator|.
name|getWrittenOn
argument_list|()
argument_list|,
name|msg
operator|.
name|getMessage
argument_list|()
argument_list|)
decl_stmt|;
name|cp
operator|.
name|setRecent
argument_list|(
name|isRecent
argument_list|)
expr_stmt|;
name|cp
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
name|commentPanelBorder
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|==
name|msgList
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|)
block|{
name|cp
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
name|commentPanelLast
argument_list|()
argument_list|)
expr_stmt|;
name|cp
operator|.
name|setOpen
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|comments
operator|.
name|add
argument_list|(
name|cp
argument_list|)
expr_stmt|;
block|}
name|comments
operator|.
name|setVisible
argument_list|(
name|msgList
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|messagesMenuBar ()
specifier|private
name|LinkMenuBar
name|messagesMenuBar
parameter_list|()
block|{
specifier|final
name|Panel
name|c
init|=
name|comments
decl_stmt|;
specifier|final
name|LinkMenuBar
name|menuBar
init|=
operator|new
name|LinkMenuBar
argument_list|()
decl_stmt|;
name|menuBar
operator|.
name|addItem
argument_list|(
name|Util
operator|.
name|C
operator|.
name|messageExpandRecent
argument_list|()
argument_list|,
operator|new
name|ExpandAllCommand
argument_list|(
name|c
argument_list|,
literal|true
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|expand
parameter_list|(
specifier|final
name|CommentPanel
name|w
parameter_list|)
block|{
name|w
operator|.
name|setOpen
argument_list|(
name|w
operator|.
name|isRecent
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|menuBar
operator|.
name|addItem
argument_list|(
name|Util
operator|.
name|C
operator|.
name|messageExpandAll
argument_list|()
argument_list|,
operator|new
name|ExpandAllCommand
argument_list|(
name|c
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|menuBar
operator|.
name|addItem
argument_list|(
name|Util
operator|.
name|C
operator|.
name|messageCollapseAll
argument_list|()
argument_list|,
operator|new
name|ExpandAllCommand
argument_list|(
name|c
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|menuBar
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
name|commentPanelMenuBar
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|menuBar
return|;
block|}
DECL|class|UpToListKeyCommand
specifier|public
class|class
name|UpToListKeyCommand
extends|extends
name|KeyCommand
block|{
DECL|method|UpToListKeyCommand (int mask, char key, String help)
specifier|public
name|UpToListKeyCommand
parameter_list|(
name|int
name|mask
parameter_list|,
name|char
name|key
parameter_list|,
name|String
name|help
parameter_list|)
block|{
name|super
argument_list|(
name|mask
argument_list|,
name|key
argument_list|,
name|help
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onKeyPress (final KeyPressEvent event)
specifier|public
name|void
name|onKeyPress
parameter_list|(
specifier|final
name|KeyPressEvent
name|event
parameter_list|)
block|{
name|Gerrit
operator|.
name|displayLastChangeList
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|ExpandCollapseDependencySectionKeyCommand
specifier|public
class|class
name|ExpandCollapseDependencySectionKeyCommand
extends|extends
name|KeyCommand
block|{
DECL|method|ExpandCollapseDependencySectionKeyCommand (int mask, char key, String help)
specifier|public
name|ExpandCollapseDependencySectionKeyCommand
parameter_list|(
name|int
name|mask
parameter_list|,
name|char
name|key
parameter_list|,
name|String
name|help
parameter_list|)
block|{
name|super
argument_list|(
name|mask
argument_list|,
name|key
argument_list|,
name|help
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onKeyPress (KeyPressEvent event)
specifier|public
name|void
name|onKeyPress
parameter_list|(
name|KeyPressEvent
name|event
parameter_list|)
block|{
name|dependenciesPanel
operator|.
name|setOpen
argument_list|(
operator|!
name|dependenciesPanel
operator|.
name|isOpen
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|PublishCommentsKeyCommand
specifier|public
class|class
name|PublishCommentsKeyCommand
extends|extends
name|NeedsSignInKeyCommand
block|{
DECL|method|PublishCommentsKeyCommand (int mask, char key, String help)
specifier|public
name|PublishCommentsKeyCommand
parameter_list|(
name|int
name|mask
parameter_list|,
name|char
name|key
parameter_list|,
name|String
name|help
parameter_list|)
block|{
name|super
argument_list|(
name|mask
argument_list|,
name|key
argument_list|,
name|help
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onKeyPress (final KeyPressEvent event)
specifier|public
name|void
name|onKeyPress
parameter_list|(
specifier|final
name|KeyPressEvent
name|event
parameter_list|)
block|{
name|PatchSet
operator|.
name|Id
name|currentPatchSetId
init|=
name|patchSetsBlock
operator|.
name|getCurrentPatchSet
argument_list|()
operator|.
name|getId
argument_list|()
decl_stmt|;
name|Gerrit
operator|.
name|display
argument_list|(
name|Dispatcher
operator|.
name|toPublish
argument_list|(
name|currentPatchSetId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

