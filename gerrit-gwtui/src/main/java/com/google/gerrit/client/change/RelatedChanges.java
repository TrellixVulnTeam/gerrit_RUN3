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
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|common
operator|.
name|PageLinks
operator|.
name|op
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
name|event
operator|.
name|logical
operator|.
name|shared
operator|.
name|SelectionEvent
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
name|SelectionHandler
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
name|TabBar
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
name|TabBar
operator|.
name|Tab
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
name|TabPanel
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
DECL|class|RelatedChanges
class|class
name|RelatedChanges
extends|extends
name|TabPanel
block|{
DECL|field|tabs
specifier|private
name|List
argument_list|<
name|RelatedChangesTab
argument_list|>
name|tabs
decl_stmt|;
DECL|field|relatedChangesTab
specifier|private
name|RelatedChangesTab
name|relatedChangesTab
decl_stmt|;
DECL|field|conflictingChangesTab
specifier|private
name|RelatedChangesTab
name|conflictingChangesTab
decl_stmt|;
DECL|field|cherryPicksTab
specifier|private
name|RelatedChangesTab
name|cherryPicksTab
decl_stmt|;
DECL|field|sameTopicTab
specifier|private
name|RelatedChangesTab
name|sameTopicTab
decl_stmt|;
DECL|field|maxHeight
specifier|private
name|int
name|maxHeight
decl_stmt|;
DECL|field|selectedTab
specifier|private
name|int
name|selectedTab
decl_stmt|;
DECL|method|RelatedChanges ()
name|RelatedChanges
parameter_list|()
block|{
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|tabs
operator|=
operator|new
name|ArrayList
argument_list|<
name|RelatedChangesTab
argument_list|>
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
name|relatedChangesTabPanel
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|createTab (String title, String tooltip)
specifier|private
name|RelatedChangesTab
name|createTab
parameter_list|(
name|String
name|title
parameter_list|,
name|String
name|tooltip
parameter_list|)
block|{
return|return
name|createTab
argument_list|(
name|title
argument_list|,
name|tooltip
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|createTab (String title, String tooltip, Integer index)
specifier|private
name|RelatedChangesTab
name|createTab
parameter_list|(
name|String
name|title
parameter_list|,
name|String
name|tooltip
parameter_list|,
name|Integer
name|index
parameter_list|)
block|{
name|setVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|FlowPanel
name|panel
init|=
operator|new
name|FlowPanel
argument_list|()
decl_stmt|;
name|add
argument_list|(
name|panel
argument_list|,
name|title
argument_list|)
expr_stmt|;
name|selectedTab
operator|=
operator|-
literal|1
expr_stmt|;
name|TabBar
name|tabBar
init|=
name|getTabBar
argument_list|()
decl_stmt|;
name|tabBar
operator|.
name|addSelectionHandler
argument_list|(
operator|new
name|SelectionHandler
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSelection
parameter_list|(
name|SelectionEvent
argument_list|<
name|Integer
argument_list|>
name|event
parameter_list|)
block|{
if|if
condition|(
name|selectedTab
operator|>=
literal|0
condition|)
block|{
name|tabs
operator|.
name|get
argument_list|(
name|selectedTab
argument_list|)
operator|.
name|registerKeys
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|selectedTab
operator|=
name|event
operator|.
name|getSelectedItem
argument_list|()
expr_stmt|;
name|tabs
operator|.
name|get
argument_list|(
name|selectedTab
argument_list|)
operator|.
name|registerKeys
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|index
operator|=
name|index
operator|!=
literal|null
condition|?
name|index
else|:
name|tabBar
operator|.
name|getTabCount
argument_list|()
operator|-
literal|1
expr_stmt|;
name|Tab
name|tab
init|=
name|tabBar
operator|.
name|getTab
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|tab
operator|.
name|setWordWrap
argument_list|(
literal|false
argument_list|)
expr_stmt|;
operator|(
operator|(
name|Composite
operator|)
name|tab
operator|)
operator|.
name|setTitle
argument_list|(
name|tooltip
argument_list|)
expr_stmt|;
name|RelatedChangesTab
name|relatedChangesTab
init|=
operator|new
name|RelatedChangesTab
argument_list|(
name|this
argument_list|,
name|index
argument_list|,
name|panel
argument_list|)
decl_stmt|;
name|tabs
operator|.
name|add
argument_list|(
name|index
argument_list|,
name|relatedChangesTab
argument_list|)
expr_stmt|;
name|relatedChangesTab
operator|.
name|setMaxHeight
argument_list|(
name|maxHeight
argument_list|)
expr_stmt|;
if|if
condition|(
name|index
operator|==
literal|0
condition|)
block|{
name|selectTab
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
return|return
name|relatedChangesTab
return|;
block|}
DECL|method|setTabTitle (int index, String title)
name|void
name|setTabTitle
parameter_list|(
name|int
name|index
parameter_list|,
name|String
name|title
parameter_list|)
block|{
name|getTabBar
argument_list|()
operator|.
name|setTabText
argument_list|(
name|index
argument_list|,
name|title
argument_list|)
expr_stmt|;
block|}
DECL|method|set (final ChangeInfo info, final String revision)
name|void
name|set
parameter_list|(
specifier|final
name|ChangeInfo
name|info
parameter_list|,
specifier|final
name|String
name|revision
parameter_list|)
block|{
if|if
condition|(
name|info
operator|.
name|status
argument_list|()
operator|.
name|isOpen
argument_list|()
condition|)
block|{
name|setForOpenChange
argument_list|(
name|info
argument_list|,
name|revision
argument_list|)
expr_stmt|;
block|}
name|StringBuilder
name|cherryPicksQuery
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|cherryPicksQuery
operator|.
name|append
argument_list|(
name|op
argument_list|(
literal|"project"
argument_list|,
name|info
operator|.
name|project
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|cherryPicksQuery
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|op
argument_list|(
literal|"change"
argument_list|,
name|info
operator|.
name|change_id
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|cherryPicksQuery
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|op
argument_list|(
literal|"-change"
argument_list|,
name|info
operator|.
name|legacy_id
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|ChangeList
operator|.
name|query
argument_list|(
name|cherryPicksQuery
operator|.
name|toString
argument_list|()
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|ListChangesOption
operator|.
name|CURRENT_REVISION
argument_list|,
name|ListChangesOption
operator|.
name|CURRENT_COMMIT
argument_list|)
argument_list|,
operator|new
name|AsyncCallback
argument_list|<
name|ChangeList
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|ChangeList
name|result
parameter_list|)
block|{
name|JsArray
argument_list|<
name|ChangeAndCommit
argument_list|>
name|changes
init|=
name|convertChangeList
argument_list|(
name|result
argument_list|)
decl_stmt|;
if|if
condition|(
name|changes
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|getTab
argument_list|()
operator|.
name|setTitle
argument_list|(
name|Resources
operator|.
name|M
operator|.
name|cherryPicks
argument_list|(
name|changes
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|getTab
argument_list|()
operator|.
name|setChanges
argument_list|(
name|info
operator|.
name|project
argument_list|()
argument_list|,
name|revision
argument_list|,
name|changes
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
name|err
parameter_list|)
block|{
name|getTab
argument_list|()
operator|.
name|setTitle
argument_list|(
name|Resources
operator|.
name|M
operator|.
name|cherryPicks
argument_list|(
name|Resources
operator|.
name|C
operator|.
name|notAvailable
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|getTab
argument_list|()
operator|.
name|setError
argument_list|(
name|err
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|RelatedChangesTab
name|getTab
parameter_list|()
block|{
if|if
condition|(
name|cherryPicksTab
operator|==
literal|null
condition|)
block|{
name|cherryPicksTab
operator|=
name|createTab
argument_list|(
name|Resources
operator|.
name|C
operator|.
name|cherryPicks
argument_list|()
argument_list|,
name|Resources
operator|.
name|C
operator|.
name|cherryPicksTooltip
argument_list|()
argument_list|)
expr_stmt|;
name|cherryPicksTab
operator|.
name|setShowBranches
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
return|return
name|cherryPicksTab
return|;
block|}
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|info
operator|.
name|topic
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|info
operator|.
name|topic
argument_list|()
argument_list|)
condition|)
block|{
name|StringBuilder
name|topicQuery
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|topicQuery
operator|.
name|append
argument_list|(
literal|"status:open"
argument_list|)
expr_stmt|;
name|topicQuery
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|op
argument_list|(
literal|"project"
argument_list|,
name|info
operator|.
name|project
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|topicQuery
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|op
argument_list|(
literal|"branch"
argument_list|,
name|info
operator|.
name|branch
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|topicQuery
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|op
argument_list|(
literal|"topic"
argument_list|,
name|info
operator|.
name|topic
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|topicQuery
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|op
argument_list|(
literal|"-change"
argument_list|,
name|info
operator|.
name|legacy_id
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|ChangeList
operator|.
name|query
argument_list|(
name|topicQuery
operator|.
name|toString
argument_list|()
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|ListChangesOption
operator|.
name|CURRENT_REVISION
argument_list|,
name|ListChangesOption
operator|.
name|CURRENT_COMMIT
argument_list|)
argument_list|,
operator|new
name|AsyncCallback
argument_list|<
name|ChangeList
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|ChangeList
name|result
parameter_list|)
block|{
name|JsArray
argument_list|<
name|ChangeAndCommit
argument_list|>
name|changes
init|=
name|convertChangeList
argument_list|(
name|result
argument_list|)
decl_stmt|;
if|if
condition|(
name|changes
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|getTab
argument_list|()
operator|.
name|setTitle
argument_list|(
name|Resources
operator|.
name|M
operator|.
name|sameTopic
argument_list|(
name|changes
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|getTab
argument_list|()
operator|.
name|setChanges
argument_list|(
name|info
operator|.
name|project
argument_list|()
argument_list|,
name|revision
argument_list|,
name|changes
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
name|err
parameter_list|)
block|{
name|getTab
argument_list|()
operator|.
name|setTitle
argument_list|(
name|Resources
operator|.
name|M
operator|.
name|sameTopic
argument_list|(
name|Resources
operator|.
name|C
operator|.
name|notAvailable
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|getTab
argument_list|()
operator|.
name|setError
argument_list|(
name|err
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|RelatedChangesTab
name|getTab
parameter_list|()
block|{
if|if
condition|(
name|sameTopicTab
operator|==
literal|null
condition|)
block|{
name|sameTopicTab
operator|=
name|createTab
argument_list|(
name|Resources
operator|.
name|C
operator|.
name|sameTopic
argument_list|()
argument_list|,
name|Resources
operator|.
name|C
operator|.
name|sameTopicTooltip
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|sameTopicTab
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setForOpenChange (final ChangeInfo info, final String revision)
specifier|private
name|void
name|setForOpenChange
parameter_list|(
specifier|final
name|ChangeInfo
name|info
parameter_list|,
specifier|final
name|String
name|revision
parameter_list|)
block|{
name|ChangeApi
operator|.
name|revision
argument_list|(
name|info
operator|.
name|legacy_id
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|revision
argument_list|)
operator|.
name|view
argument_list|(
literal|"related"
argument_list|)
operator|.
name|get
argument_list|(
operator|new
name|AsyncCallback
argument_list|<
name|RelatedInfo
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|RelatedInfo
name|result
parameter_list|)
block|{
if|if
condition|(
name|result
operator|.
name|changes
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|getTab
argument_list|()
operator|.
name|setTitle
argument_list|(
name|Resources
operator|.
name|M
operator|.
name|relatedChanges
argument_list|(
name|result
operator|.
name|changes
argument_list|()
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|getTab
argument_list|()
operator|.
name|setChanges
argument_list|(
name|info
operator|.
name|project
argument_list|()
argument_list|,
name|revision
argument_list|,
name|result
operator|.
name|changes
argument_list|()
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
name|err
parameter_list|)
block|{
name|getTab
argument_list|()
operator|.
name|setTitle
argument_list|(
name|Resources
operator|.
name|M
operator|.
name|relatedChanges
argument_list|(
name|Resources
operator|.
name|C
operator|.
name|notAvailable
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|getTab
argument_list|()
operator|.
name|setError
argument_list|(
name|err
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|RelatedChangesTab
name|getTab
parameter_list|()
block|{
if|if
condition|(
name|relatedChangesTab
operator|==
literal|null
condition|)
block|{
name|relatedChangesTab
operator|=
name|createTab
argument_list|(
name|Resources
operator|.
name|C
operator|.
name|relatedChanges
argument_list|()
argument_list|,
name|Resources
operator|.
name|C
operator|.
name|relatedChangesTooltip
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
return|return
name|relatedChangesTab
return|;
block|}
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|info
operator|.
name|mergeable
argument_list|()
condition|)
block|{
name|StringBuilder
name|conflictsQuery
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|conflictsQuery
operator|.
name|append
argument_list|(
literal|"status:open"
argument_list|)
expr_stmt|;
name|conflictsQuery
operator|.
name|append
argument_list|(
literal|" is:mergeable"
argument_list|)
expr_stmt|;
name|conflictsQuery
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|op
argument_list|(
literal|"conflicts"
argument_list|,
name|info
operator|.
name|legacy_id
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|ChangeList
operator|.
name|query
argument_list|(
name|conflictsQuery
operator|.
name|toString
argument_list|()
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|ListChangesOption
operator|.
name|CURRENT_REVISION
argument_list|,
name|ListChangesOption
operator|.
name|CURRENT_COMMIT
argument_list|)
argument_list|,
operator|new
name|AsyncCallback
argument_list|<
name|ChangeList
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|ChangeList
name|result
parameter_list|)
block|{
name|JsArray
argument_list|<
name|ChangeAndCommit
argument_list|>
name|changes
init|=
name|convertChangeList
argument_list|(
name|result
argument_list|)
decl_stmt|;
if|if
condition|(
name|changes
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|getTab
argument_list|()
operator|.
name|setTitle
argument_list|(
name|Resources
operator|.
name|M
operator|.
name|conflictingChanges
argument_list|(
name|changes
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|getTab
argument_list|()
operator|.
name|setChanges
argument_list|(
name|info
operator|.
name|project
argument_list|()
argument_list|,
name|revision
argument_list|,
name|changes
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
name|err
parameter_list|)
block|{
name|getTab
argument_list|()
operator|.
name|setTitle
argument_list|(
name|Resources
operator|.
name|M
operator|.
name|conflictingChanges
argument_list|(
name|Resources
operator|.
name|C
operator|.
name|notAvailable
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|getTab
argument_list|()
operator|.
name|setError
argument_list|(
name|err
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|RelatedChangesTab
name|getTab
parameter_list|()
block|{
if|if
condition|(
name|conflictingChangesTab
operator|==
literal|null
condition|)
block|{
name|conflictingChangesTab
operator|=
name|createTab
argument_list|(
name|Resources
operator|.
name|C
operator|.
name|conflictingChanges
argument_list|()
argument_list|,
name|Resources
operator|.
name|C
operator|.
name|conflictingChangesTooltip
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|conflictingChangesTab
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setMaxHeight (int height)
name|void
name|setMaxHeight
parameter_list|(
name|int
name|height
parameter_list|)
block|{
name|maxHeight
operator|=
name|height
operator|-
operator|(
name|getTabBar
argument_list|()
operator|.
name|getOffsetHeight
argument_list|()
operator|+
literal|2
comment|/* padding */
operator|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|getTabBar
argument_list|()
operator|.
name|getTabCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|tabs
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|setMaxHeight
argument_list|(
name|maxHeight
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|convertChangeList (ChangeList l)
specifier|private
name|JsArray
argument_list|<
name|ChangeAndCommit
argument_list|>
name|convertChangeList
parameter_list|(
name|ChangeList
name|l
parameter_list|)
block|{
name|JsArray
argument_list|<
name|ChangeAndCommit
argument_list|>
name|arr
init|=
name|JavaScriptObject
operator|.
name|createArray
argument_list|()
operator|.
name|cast
argument_list|()
decl_stmt|;
for|for
control|(
name|ChangeInfo
name|i
range|:
name|Natives
operator|.
name|asList
argument_list|(
name|l
argument_list|)
control|)
block|{
if|if
condition|(
name|i
operator|.
name|current_revision
argument_list|()
operator|!=
literal|null
operator|&&
name|i
operator|.
name|revisions
argument_list|()
operator|.
name|containsKey
argument_list|(
name|i
operator|.
name|current_revision
argument_list|()
argument_list|)
condition|)
block|{
name|RevisionInfo
name|currentRevision
init|=
name|i
operator|.
name|revision
argument_list|(
name|i
operator|.
name|current_revision
argument_list|()
argument_list|)
decl_stmt|;
name|ChangeAndCommit
name|c
init|=
name|ChangeAndCommit
operator|.
name|create
argument_list|()
decl_stmt|;
name|c
operator|.
name|set_id
argument_list|(
name|i
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
name|c
operator|.
name|set_commit
argument_list|(
name|currentRevision
operator|.
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|c
operator|.
name|set_change_number
argument_list|(
name|i
operator|.
name|legacy_id
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|c
operator|.
name|set_revision_number
argument_list|(
name|currentRevision
operator|.
name|_number
argument_list|()
argument_list|)
expr_stmt|;
name|c
operator|.
name|set_branch
argument_list|(
name|i
operator|.
name|branch
argument_list|()
argument_list|)
expr_stmt|;
name|arr
operator|.
name|push
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|arr
return|;
block|}
DECL|class|RelatedInfo
specifier|private
specifier|static
class|class
name|RelatedInfo
extends|extends
name|JavaScriptObject
block|{
DECL|method|changes ()
specifier|final
specifier|native
name|JsArray
argument_list|<
name|ChangeAndCommit
argument_list|>
name|changes
parameter_list|()
comment|/*-{ return this.changes }-*/
function_decl|;
DECL|method|RelatedInfo ()
specifier|protected
name|RelatedInfo
parameter_list|()
block|{     }
block|}
DECL|class|ChangeAndCommit
specifier|static
class|class
name|ChangeAndCommit
extends|extends
name|JavaScriptObject
block|{
DECL|method|create ()
specifier|static
name|ChangeAndCommit
name|create
parameter_list|()
block|{
return|return
operator|(
name|ChangeAndCommit
operator|)
name|createObject
argument_list|()
return|;
block|}
DECL|method|id ()
specifier|final
specifier|native
name|String
name|id
parameter_list|()
comment|/*-{ return this.change_id }-*/
function_decl|;
DECL|method|commit ()
specifier|final
specifier|native
name|CommitInfo
name|commit
parameter_list|()
comment|/*-{ return this.commit }-*/
function_decl|;
DECL|method|branch ()
specifier|final
specifier|native
name|String
name|branch
parameter_list|()
comment|/*-{ return this.branch }-*/
function_decl|;
DECL|method|set_id (String i)
specifier|final
specifier|native
name|void
name|set_id
parameter_list|(
name|String
name|i
parameter_list|)
comment|/*-{ if(i)this.change_id=i; }-*/
function_decl|;
DECL|method|set_commit (CommitInfo c)
specifier|final
specifier|native
name|void
name|set_commit
parameter_list|(
name|CommitInfo
name|c
parameter_list|)
comment|/*-{ if(c)this.commit=c; }-*/
function_decl|;
DECL|method|set_branch (String b)
specifier|final
specifier|native
name|void
name|set_branch
parameter_list|(
name|String
name|b
parameter_list|)
comment|/*-{ if(b)this.branch=b; }-*/
function_decl|;
DECL|method|legacy_id ()
specifier|final
name|Change
operator|.
name|Id
name|legacy_id
parameter_list|()
block|{
return|return
name|has_change_number
argument_list|()
condition|?
operator|new
name|Change
operator|.
name|Id
argument_list|(
name|_change_number
argument_list|()
argument_list|)
else|:
literal|null
return|;
block|}
DECL|method|patch_set_id ()
specifier|final
name|PatchSet
operator|.
name|Id
name|patch_set_id
parameter_list|()
block|{
return|return
name|has_change_number
argument_list|()
operator|&&
name|has_revision_number
argument_list|()
condition|?
operator|new
name|PatchSet
operator|.
name|Id
argument_list|(
name|legacy_id
argument_list|()
argument_list|,
name|_revision_number
argument_list|()
argument_list|)
else|:
literal|null
return|;
block|}
DECL|method|has_change_number ()
specifier|final
specifier|native
name|boolean
name|has_change_number
parameter_list|()
comment|/*-{ return this.hasOwnProperty('_change_number') }-*/
function_decl|;
DECL|method|has_revision_number ()
specifier|final
specifier|native
name|boolean
name|has_revision_number
parameter_list|()
comment|/*-{ return this.hasOwnProperty('_revision_number') }-*/
function_decl|;
DECL|method|_change_number ()
specifier|final
specifier|native
name|int
name|_change_number
parameter_list|()
comment|/*-{ return this._change_number }-*/
function_decl|;
DECL|method|_revision_number ()
specifier|final
specifier|native
name|int
name|_revision_number
parameter_list|()
comment|/*-{ return this._revision_number }-*/
function_decl|;
DECL|method|set_change_number (int n)
specifier|final
specifier|native
name|void
name|set_change_number
parameter_list|(
name|int
name|n
parameter_list|)
comment|/*-{ this._change_number=n; }-*/
function_decl|;
DECL|method|set_revision_number (int n)
specifier|final
specifier|native
name|void
name|set_revision_number
parameter_list|(
name|int
name|n
parameter_list|)
comment|/*-{ this._revision_number=n; }-*/
function_decl|;
DECL|method|ChangeAndCommit ()
specifier|protected
name|ChangeAndCommit
parameter_list|()
block|{     }
block|}
block|}
end_class

end_unit

