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
name|ChangeInfo
operator|.
name|IncludedInInfo
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
name|common
operator|.
name|data
operator|.
name|IncludedInDetail
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

begin_comment
comment|/** Displays a table of Branches and Tags containing the change record. */
end_comment

begin_class
DECL|class|IncludedInTable
specifier|public
class|class
name|IncludedInTable
extends|extends
name|Composite
implements|implements
name|OpenHandler
argument_list|<
name|DisclosurePanel
argument_list|>
block|{
DECL|field|table
specifier|private
specifier|final
name|Grid
name|table
decl_stmt|;
DECL|field|changeId
specifier|private
specifier|final
name|Change
operator|.
name|Id
name|changeId
decl_stmt|;
DECL|field|loaded
specifier|private
name|boolean
name|loaded
init|=
literal|false
decl_stmt|;
DECL|method|IncludedInTable (final Change.Id chId)
specifier|public
name|IncludedInTable
parameter_list|(
specifier|final
name|Change
operator|.
name|Id
name|chId
parameter_list|)
block|{
name|changeId
operator|=
name|chId
expr_stmt|;
name|table
operator|=
operator|new
name|Grid
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|initWidget
argument_list|(
name|table
argument_list|)
expr_stmt|;
block|}
DECL|method|loadTable (final IncludedInDetail detail)
specifier|public
name|void
name|loadTable
parameter_list|(
specifier|final
name|IncludedInDetail
name|detail
parameter_list|)
block|{
name|int
name|row
init|=
literal|0
decl_stmt|;
name|table
operator|.
name|resizeRows
argument_list|(
name|detail
operator|.
name|getBranches
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
name|table
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
name|changeTable
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|CellFormatter
name|fmt
init|=
name|table
operator|.
name|getCellFormatter
argument_list|()
decl_stmt|;
name|fmt
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
name|dataHeader
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|setText
argument_list|(
name|row
argument_list|,
literal|0
argument_list|,
name|Util
operator|.
name|C
operator|.
name|includedInTableBranch
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|String
name|branch
range|:
name|detail
operator|.
name|getBranches
argument_list|()
control|)
block|{
name|fmt
operator|.
name|addStyleName
argument_list|(
operator|++
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
name|dataCell
argument_list|()
argument_list|)
expr_stmt|;
name|fmt
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
name|leftMostCell
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|setText
argument_list|(
name|row
argument_list|,
literal|0
argument_list|,
name|branch
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|detail
operator|.
name|getTags
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|table
operator|.
name|resizeRows
argument_list|(
name|table
operator|.
name|getRowCount
argument_list|()
operator|+
literal|2
operator|+
name|detail
operator|.
name|getTags
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|row
operator|++
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
operator|++
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
name|dataHeader
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|setText
argument_list|(
name|row
argument_list|,
literal|0
argument_list|,
name|Util
operator|.
name|C
operator|.
name|includedInTableTag
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|String
name|tag
range|:
name|detail
operator|.
name|getTags
argument_list|()
control|)
block|{
name|fmt
operator|.
name|addStyleName
argument_list|(
operator|++
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
name|dataCell
argument_list|()
argument_list|)
expr_stmt|;
name|fmt
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
name|leftMostCell
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|setText
argument_list|(
name|row
argument_list|,
literal|0
argument_list|,
name|tag
argument_list|)
expr_stmt|;
block|}
block|}
name|table
operator|.
name|setVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|loaded
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onOpen (OpenEvent<DisclosurePanel> event)
specifier|public
name|void
name|onOpen
parameter_list|(
name|OpenEvent
argument_list|<
name|DisclosurePanel
argument_list|>
name|event
parameter_list|)
block|{
if|if
condition|(
operator|!
name|loaded
condition|)
block|{
name|ChangeApi
operator|.
name|includedIn
argument_list|(
name|changeId
operator|.
name|get
argument_list|()
argument_list|,
operator|new
name|GerritCallback
argument_list|<
name|IncludedInInfo
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|IncludedInInfo
name|r
parameter_list|)
block|{
name|IncludedInDetail
name|result
init|=
operator|new
name|IncludedInDetail
argument_list|()
decl_stmt|;
name|result
operator|.
name|setBranches
argument_list|(
name|toList
argument_list|(
name|r
operator|.
name|branches
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|setTags
argument_list|(
name|toList
argument_list|(
name|r
operator|.
name|tags
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|loadTable
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|toList
parameter_list|(
name|JsArrayString
name|in
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|r
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|in
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
name|in
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|r
operator|.
name|add
argument_list|(
name|in
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|r
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

