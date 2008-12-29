begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright 2008 Google Inc.
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
name|client
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
name|client
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
name|DisclosureEvent
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
name|DisclosureHandler
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
name|HTMLTable
operator|.
name|CellFormatter
import|;
end_import

begin_class
DECL|class|PatchSetPanel
class|class
name|PatchSetPanel
extends|extends
name|Composite
implements|implements
name|DisclosureHandler
block|{
DECL|field|R_DOWNLOAD
specifier|private
specifier|static
specifier|final
name|int
name|R_DOWNLOAD
init|=
literal|0
decl_stmt|;
DECL|field|R_CNT
specifier|private
specifier|static
specifier|final
name|int
name|R_CNT
init|=
literal|1
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
DECL|field|patchTable
specifier|private
name|PatchTable
name|patchTable
decl_stmt|;
DECL|method|PatchSetPanel (final ChangeDetail detail, final PatchSet ps)
name|PatchSetPanel
parameter_list|(
specifier|final
name|ChangeDetail
name|detail
parameter_list|,
specifier|final
name|PatchSet
name|ps
parameter_list|)
block|{
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
literal|"gerrit-InfoBlock"
argument_list|)
expr_stmt|;
name|infoTable
operator|.
name|addStyleName
argument_list|(
literal|"gerrit-PatchSetInfoBlock"
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
literal|"topmost"
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
literal|"topmost"
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
literal|"bottomheader"
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
literal|"command"
argument_list|)
expr_stmt|;
name|infoTable
operator|.
name|setText
argument_list|(
name|R_DOWNLOAD
argument_list|,
literal|1
argument_list|,
name|Util
operator|.
name|M
operator|.
name|repoDownload
argument_list|(
name|changeDetail
operator|.
name|getChange
argument_list|()
operator|.
name|getDest
argument_list|()
operator|.
name|getParentKey
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|changeDetail
operator|.
name|getChange
argument_list|()
operator|.
name|getChangeId
argument_list|()
argument_list|,
name|patchSet
operator|.
name|getPatchSetId
argument_list|()
argument_list|)
argument_list|)
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
literal|"patchTable "
operator|+
name|changeDetail
operator|.
name|getChange
argument_list|()
operator|.
name|getChangeId
argument_list|()
operator|+
literal|" "
operator|+
name|patchSet
operator|.
name|getPatchSetId
argument_list|()
argument_list|)
expr_stmt|;
name|patchTable
operator|.
name|display
argument_list|(
name|detail
operator|.
name|getPatches
argument_list|()
argument_list|)
expr_stmt|;
name|patchTable
operator|.
name|finishDisplay
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
name|infoTable
argument_list|)
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
name|patchTable
argument_list|)
expr_stmt|;
block|}
DECL|method|onOpen (final DisclosureEvent event)
specifier|public
name|void
name|onOpen
parameter_list|(
specifier|final
name|DisclosureEvent
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
DECL|method|onClose (final DisclosureEvent event)
specifier|public
name|void
name|onClose
parameter_list|(
specifier|final
name|DisclosureEvent
name|event
parameter_list|)
block|{   }
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
literal|"header"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

