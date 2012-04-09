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
name|GitwebLink
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
name|Hyperlink
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
name|reviewdb
operator|.
name|client
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
name|dom
operator|.
name|client
operator|.
name|Style
operator|.
name|Display
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
name|Editor
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
name|EditorDelegate
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
name|ValueAwareEditor
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
name|adapters
operator|.
name|EditorSource
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
name|adapters
operator|.
name|ListEditor
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
name|List
import|;
end_import

begin_class
DECL|class|ProjectAccessEditor
specifier|public
class|class
name|ProjectAccessEditor
extends|extends
name|Composite
implements|implements
name|Editor
argument_list|<
name|ProjectAccess
argument_list|>
implements|,
name|ValueAwareEditor
argument_list|<
name|ProjectAccess
argument_list|>
block|{
DECL|interface|Binder
interface|interface
name|Binder
extends|extends
name|UiBinder
argument_list|<
name|HTMLPanel
argument_list|,
name|ProjectAccessEditor
argument_list|>
block|{   }
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
DECL|field|inheritsFrom
name|DivElement
name|inheritsFrom
decl_stmt|;
annotation|@
name|UiField
DECL|field|parentProject
name|Hyperlink
name|parentProject
decl_stmt|;
annotation|@
name|UiField
DECL|field|history
name|DivElement
name|history
decl_stmt|;
annotation|@
name|UiField
DECL|field|gitweb
name|Anchor
name|gitweb
decl_stmt|;
annotation|@
name|UiField
DECL|field|localContainer
name|FlowPanel
name|localContainer
decl_stmt|;
DECL|field|local
name|ListEditor
argument_list|<
name|AccessSection
argument_list|,
name|AccessSectionEditor
argument_list|>
name|local
decl_stmt|;
annotation|@
name|UiField
DECL|field|addSection
name|Anchor
name|addSection
decl_stmt|;
DECL|field|value
specifier|private
name|ProjectAccess
name|value
decl_stmt|;
DECL|field|editing
specifier|private
name|boolean
name|editing
decl_stmt|;
DECL|method|ProjectAccessEditor ()
specifier|public
name|ProjectAccessEditor
parameter_list|()
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
name|local
operator|=
name|ListEditor
operator|.
name|of
argument_list|(
operator|new
name|Source
argument_list|(
name|localContainer
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|UiHandler
argument_list|(
literal|"addSection"
argument_list|)
DECL|method|onAddSection (ClickEvent event)
name|void
name|onAddSection
parameter_list|(
name|ClickEvent
name|event
parameter_list|)
block|{
name|int
name|index
init|=
name|local
operator|.
name|getList
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
name|local
operator|.
name|getList
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|AccessSection
argument_list|(
literal|"refs/heads/*"
argument_list|)
argument_list|)
expr_stmt|;
name|AccessSectionEditor
name|editor
init|=
name|local
operator|.
name|getEditors
argument_list|()
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|editor
operator|.
name|enableEditing
argument_list|()
expr_stmt|;
name|editor
operator|.
name|editRefPattern
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setValue (ProjectAccess value)
specifier|public
name|void
name|setValue
parameter_list|(
name|ProjectAccess
name|value
parameter_list|)
block|{
comment|// If the owner can edit the Global Capabilities but they don't exist in this
comment|// project, create an empty one at the beginning of the list making it
comment|// possible to add permissions to it.
if|if
condition|(
name|editing
operator|&&
name|value
operator|.
name|isOwnerOf
argument_list|(
name|AccessSection
operator|.
name|GLOBAL_CAPABILITIES
argument_list|)
operator|&&
name|value
operator|.
name|getLocal
argument_list|(
name|AccessSection
operator|.
name|GLOBAL_CAPABILITIES
argument_list|)
operator|==
literal|null
condition|)
block|{
name|value
operator|.
name|getLocal
argument_list|()
operator|.
name|add
argument_list|(
literal|0
argument_list|,
operator|new
name|AccessSection
argument_list|(
name|AccessSection
operator|.
name|GLOBAL_CAPABILITIES
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|Project
operator|.
name|NameKey
name|parent
init|=
name|value
operator|.
name|getInheritsFrom
argument_list|()
decl_stmt|;
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|inheritsFrom
operator|.
name|getStyle
argument_list|()
operator|.
name|setDisplay
argument_list|(
name|Display
operator|.
name|BLOCK
argument_list|)
expr_stmt|;
name|parentProject
operator|.
name|setText
argument_list|(
name|parent
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|parentProject
operator|.
name|setTargetHistoryToken
argument_list|(
comment|//
name|Dispatcher
operator|.
name|toProjectAdmin
argument_list|(
name|parent
argument_list|,
name|ProjectScreen
operator|.
name|ACCESS
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|inheritsFrom
operator|.
name|getStyle
argument_list|()
operator|.
name|setDisplay
argument_list|(
name|Display
operator|.
name|NONE
argument_list|)
expr_stmt|;
block|}
specifier|final
name|GitwebLink
name|c
init|=
name|Gerrit
operator|.
name|getGitwebLink
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
operator|.
name|isConfigVisible
argument_list|()
operator|&&
name|c
operator|!=
literal|null
condition|)
block|{
name|history
operator|.
name|getStyle
argument_list|()
operator|.
name|setDisplay
argument_list|(
name|Display
operator|.
name|BLOCK
argument_list|)
expr_stmt|;
name|gitweb
operator|.
name|setText
argument_list|(
name|c
operator|.
name|getLinkName
argument_list|()
argument_list|)
expr_stmt|;
name|gitweb
operator|.
name|setHref
argument_list|(
name|c
operator|.
name|toFileHistory
argument_list|(
operator|new
name|Branch
operator|.
name|NameKey
argument_list|(
name|value
operator|.
name|getProjectName
argument_list|()
argument_list|,
literal|"refs/meta/config"
argument_list|)
argument_list|,
literal|"project.config"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|history
operator|.
name|getStyle
argument_list|()
operator|.
name|setDisplay
argument_list|(
name|Display
operator|.
name|NONE
argument_list|)
expr_stmt|;
block|}
name|addSection
operator|.
name|setVisible
argument_list|(
name|value
operator|!=
literal|null
operator|&&
name|editing
operator|&&
operator|!
name|value
operator|.
name|getOwnerOf
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|flush ()
specifier|public
name|void
name|flush
parameter_list|()
block|{
name|List
argument_list|<
name|AccessSection
argument_list|>
name|src
init|=
name|local
operator|.
name|getList
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|AccessSection
argument_list|>
name|keep
init|=
operator|new
name|ArrayList
argument_list|<
name|AccessSection
argument_list|>
argument_list|(
name|src
operator|.
name|size
argument_list|()
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
name|src
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|AccessSectionEditor
name|e
init|=
operator|(
name|AccessSectionEditor
operator|)
name|localContainer
operator|.
name|getWidget
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|e
operator|.
name|isDeleted
argument_list|()
operator|&&
operator|!
name|src
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getPermissions
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|keep
operator|.
name|add
argument_list|(
name|src
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|value
operator|.
name|setLocal
argument_list|(
name|keep
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onPropertyChange (String... paths)
specifier|public
name|void
name|onPropertyChange
parameter_list|(
name|String
modifier|...
name|paths
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|setDelegate (EditorDelegate<ProjectAccess> delegate)
specifier|public
name|void
name|setDelegate
parameter_list|(
name|EditorDelegate
argument_list|<
name|ProjectAccess
argument_list|>
name|delegate
parameter_list|)
block|{   }
DECL|method|setEditing (final boolean editing)
name|void
name|setEditing
parameter_list|(
specifier|final
name|boolean
name|editing
parameter_list|)
block|{
name|this
operator|.
name|editing
operator|=
name|editing
expr_stmt|;
name|addSection
operator|.
name|setVisible
argument_list|(
name|editing
argument_list|)
expr_stmt|;
block|}
DECL|class|Source
specifier|private
class|class
name|Source
extends|extends
name|EditorSource
argument_list|<
name|AccessSectionEditor
argument_list|>
block|{
DECL|field|container
specifier|private
specifier|final
name|FlowPanel
name|container
decl_stmt|;
DECL|method|Source (FlowPanel container)
name|Source
parameter_list|(
name|FlowPanel
name|container
parameter_list|)
block|{
name|this
operator|.
name|container
operator|=
name|container
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|create (int index)
specifier|public
name|AccessSectionEditor
name|create
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|AccessSectionEditor
name|subEditor
init|=
operator|new
name|AccessSectionEditor
argument_list|(
name|value
argument_list|)
decl_stmt|;
name|subEditor
operator|.
name|setEditing
argument_list|(
name|editing
argument_list|)
expr_stmt|;
name|container
operator|.
name|insert
argument_list|(
name|subEditor
argument_list|,
name|index
argument_list|)
expr_stmt|;
return|return
name|subEditor
return|;
block|}
annotation|@
name|Override
DECL|method|dispose (AccessSectionEditor subEditor)
specifier|public
name|void
name|dispose
parameter_list|(
name|AccessSectionEditor
name|subEditor
parameter_list|)
block|{
name|subEditor
operator|.
name|removeFromParent
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setIndex (AccessSectionEditor subEditor, int index)
specifier|public
name|void
name|setIndex
parameter_list|(
name|AccessSectionEditor
name|subEditor
parameter_list|,
name|int
name|index
parameter_list|)
block|{
name|container
operator|.
name|insert
argument_list|(
name|subEditor
argument_list|,
name|index
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

