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
DECL|package|com.google.gerrit.client.projects
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|projects
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
name|ui
operator|.
name|CommentLinkProcessor
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
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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

begin_comment
comment|/** Cache of {@link ConfigInfo} objects by project name. */
end_comment

begin_class
DECL|class|ConfigInfoCache
specifier|public
class|class
name|ConfigInfoCache
block|{
DECL|field|PROJECT_LIMIT
specifier|private
specifier|static
specifier|final
name|int
name|PROJECT_LIMIT
init|=
literal|25
decl_stmt|;
DECL|field|CHANGE_LIMIT
specifier|private
specifier|static
specifier|final
name|int
name|CHANGE_LIMIT
init|=
literal|100
decl_stmt|;
DECL|field|instance
specifier|private
specifier|static
specifier|final
name|ConfigInfoCache
name|instance
init|=
name|GWT
operator|.
name|create
argument_list|(
name|ConfigInfoCache
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|class|Entry
specifier|public
specifier|static
class|class
name|Entry
block|{
DECL|field|info
specifier|private
specifier|final
name|ConfigInfo
name|info
decl_stmt|;
DECL|field|commentLinkProcessor
specifier|private
name|CommentLinkProcessor
name|commentLinkProcessor
decl_stmt|;
DECL|method|Entry (ConfigInfo info)
specifier|private
name|Entry
parameter_list|(
name|ConfigInfo
name|info
parameter_list|)
block|{
name|this
operator|.
name|info
operator|=
name|info
expr_stmt|;
block|}
DECL|method|getCommentLinkProcessor ()
specifier|public
name|CommentLinkProcessor
name|getCommentLinkProcessor
parameter_list|()
block|{
if|if
condition|(
name|commentLinkProcessor
operator|==
literal|null
condition|)
block|{
name|commentLinkProcessor
operator|=
operator|new
name|CommentLinkProcessor
argument_list|(
name|info
operator|.
name|commentlinks
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|commentLinkProcessor
return|;
block|}
DECL|method|getTheme ()
specifier|public
name|ThemeInfo
name|getTheme
parameter_list|()
block|{
return|return
name|info
operator|.
name|theme
argument_list|()
return|;
block|}
DECL|method|getExtensionPanelNames (String extensionPoint)
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getExtensionPanelNames
parameter_list|(
name|String
name|extensionPoint
parameter_list|)
block|{
return|return
name|Natives
operator|.
name|asList
argument_list|(
name|info
operator|.
name|extensionPanelNames
argument_list|()
operator|.
name|get
argument_list|(
name|extensionPoint
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|method|get (Project.NameKey name, AsyncCallback<Entry> cb)
specifier|public
specifier|static
name|void
name|get
parameter_list|(
name|Project
operator|.
name|NameKey
name|name
parameter_list|,
name|AsyncCallback
argument_list|<
name|Entry
argument_list|>
name|cb
parameter_list|)
block|{
name|instance
operator|.
name|getImpl
argument_list|(
name|name
operator|.
name|get
argument_list|()
argument_list|,
name|cb
argument_list|)
expr_stmt|;
block|}
DECL|method|get (Change.Id changeId, AsyncCallback<Entry> cb)
specifier|public
specifier|static
name|void
name|get
parameter_list|(
name|Change
operator|.
name|Id
name|changeId
parameter_list|,
name|AsyncCallback
argument_list|<
name|Entry
argument_list|>
name|cb
parameter_list|)
block|{
name|instance
operator|.
name|getImpl
argument_list|(
name|changeId
operator|.
name|get
argument_list|()
argument_list|,
name|cb
argument_list|)
expr_stmt|;
block|}
DECL|method|add (ChangeInfo info)
specifier|public
specifier|static
name|void
name|add
parameter_list|(
name|ChangeInfo
name|info
parameter_list|)
block|{
name|instance
operator|.
name|changeToProject
operator|.
name|put
argument_list|(
name|info
operator|.
name|legacyId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|info
operator|.
name|project
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|field|cache
specifier|private
specifier|final
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Entry
argument_list|>
name|cache
decl_stmt|;
DECL|field|changeToProject
specifier|private
specifier|final
name|LinkedHashMap
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|changeToProject
decl_stmt|;
DECL|method|ConfigInfoCache ()
specifier|protected
name|ConfigInfoCache
parameter_list|()
block|{
name|cache
operator|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Entry
argument_list|>
argument_list|(
name|PROJECT_LIMIT
argument_list|)
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|boolean
name|removeEldestEntry
parameter_list|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|ConfigInfoCache
operator|.
name|Entry
argument_list|>
name|e
parameter_list|)
block|{
return|return
name|size
argument_list|()
operator|>
name|PROJECT_LIMIT
return|;
block|}
block|}
expr_stmt|;
name|changeToProject
operator|=
operator|new
name|LinkedHashMap
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
argument_list|(
name|CHANGE_LIMIT
argument_list|)
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|boolean
name|removeEldestEntry
parameter_list|(
name|Map
operator|.
name|Entry
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|e
parameter_list|)
block|{
return|return
name|size
argument_list|()
operator|>
name|CHANGE_LIMIT
return|;
block|}
block|}
expr_stmt|;
block|}
DECL|method|getImpl (final String name, final AsyncCallback<Entry> cb)
specifier|private
name|void
name|getImpl
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|AsyncCallback
argument_list|<
name|Entry
argument_list|>
name|cb
parameter_list|)
block|{
name|Entry
name|e
init|=
name|cache
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|e
operator|!=
literal|null
condition|)
block|{
name|cb
operator|.
name|onSuccess
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
name|ProjectApi
operator|.
name|getConfig
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
name|name
argument_list|)
argument_list|,
operator|new
name|AsyncCallback
argument_list|<
name|ConfigInfo
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|ConfigInfo
name|result
parameter_list|)
block|{
name|Entry
name|e
init|=
operator|new
name|Entry
argument_list|(
name|result
argument_list|)
decl_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|cb
operator|.
name|onSuccess
argument_list|(
name|e
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
name|cb
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
DECL|method|getImpl (final Integer id, final AsyncCallback<Entry> cb)
specifier|private
name|void
name|getImpl
parameter_list|(
specifier|final
name|Integer
name|id
parameter_list|,
specifier|final
name|AsyncCallback
argument_list|<
name|Entry
argument_list|>
name|cb
parameter_list|)
block|{
name|String
name|name
init|=
name|changeToProject
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
name|getImpl
argument_list|(
name|name
argument_list|,
name|cb
argument_list|)
expr_stmt|;
return|return;
block|}
name|ChangeApi
operator|.
name|change
argument_list|(
name|id
argument_list|)
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
name|changeToProject
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|result
operator|.
name|project
argument_list|()
argument_list|)
expr_stmt|;
name|getImpl
argument_list|(
name|result
operator|.
name|project
argument_list|()
argument_list|,
name|cb
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
name|cb
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
end_class

end_unit

