begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2012 The Android Open Source Project
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
DECL|package|com.google.gerrit.client.dashboards
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|dashboards
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
name|ui
operator|.
name|NavigationTable
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
name|History
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
operator|.
name|FlexCellFormatter
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
name|Comparator
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
DECL|class|DashboardsTable
specifier|public
class|class
name|DashboardsTable
extends|extends
name|NavigationTable
argument_list|<
name|DashboardInfo
argument_list|>
block|{
DECL|method|DashboardsTable ()
specifier|public
name|DashboardsTable
parameter_list|()
block|{
name|super
argument_list|(
name|Util
operator|.
name|C
operator|.
name|dashboardItem
argument_list|()
argument_list|)
expr_stmt|;
name|initColumnHeaders
argument_list|()
expr_stmt|;
block|}
DECL|method|initColumnHeaders ()
specifier|protected
name|void
name|initColumnHeaders
parameter_list|()
block|{
specifier|final
name|FlexCellFormatter
name|fmt
init|=
name|table
operator|.
name|getFlexCellFormatter
argument_list|()
decl_stmt|;
name|fmt
operator|.
name|setColSpan
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|fmt
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
name|dataHeader
argument_list|()
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
literal|0
argument_list|,
literal|2
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
literal|0
argument_list|,
literal|1
argument_list|,
name|Util
operator|.
name|C
operator|.
name|dashboardName
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|setText
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|,
name|Util
operator|.
name|C
operator|.
name|dashboardDescription
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|display (DashboardMap dashes)
specifier|public
name|void
name|display
parameter_list|(
name|DashboardMap
name|dashes
parameter_list|)
block|{
while|while
condition|(
literal|1
operator|<
name|table
operator|.
name|getRowCount
argument_list|()
condition|)
block|{
name|table
operator|.
name|removeRow
argument_list|(
name|table
operator|.
name|getRowCount
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|DashboardInfo
argument_list|>
name|list
init|=
name|dashes
operator|.
name|values
argument_list|()
operator|.
name|asList
argument_list|()
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|list
argument_list|,
operator|new
name|Comparator
argument_list|<
name|DashboardInfo
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|DashboardInfo
name|a
parameter_list|,
name|DashboardInfo
name|b
parameter_list|)
block|{
return|return
name|a
operator|.
name|id
argument_list|()
operator|.
name|compareTo
argument_list|(
name|b
operator|.
name|id
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|String
name|section
init|=
literal|null
decl_stmt|;
for|for
control|(
name|DashboardInfo
name|d
range|:
name|list
control|)
block|{
if|if
condition|(
operator|!
name|d
operator|.
name|section
argument_list|()
operator|.
name|equals
argument_list|(
name|section
argument_list|)
condition|)
block|{
name|section
operator|=
name|d
operator|.
name|section
argument_list|()
expr_stmt|;
name|insertTitleRow
argument_list|(
name|table
operator|.
name|getRowCount
argument_list|()
argument_list|,
name|section
argument_list|)
expr_stmt|;
block|}
name|insert
argument_list|(
name|table
operator|.
name|getRowCount
argument_list|()
argument_list|,
name|d
argument_list|)
expr_stmt|;
block|}
name|finishDisplay
argument_list|()
expr_stmt|;
block|}
DECL|method|insertTitleRow (final int row, String section)
specifier|protected
name|void
name|insertTitleRow
parameter_list|(
specifier|final
name|int
name|row
parameter_list|,
name|String
name|section
parameter_list|)
block|{
name|table
operator|.
name|insertRow
argument_list|(
name|row
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
name|section
argument_list|)
expr_stmt|;
specifier|final
name|FlexCellFormatter
name|fmt
init|=
name|table
operator|.
name|getFlexCellFormatter
argument_list|()
decl_stmt|;
name|fmt
operator|.
name|setColSpan
argument_list|(
name|row
argument_list|,
literal|0
argument_list|,
literal|4
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
name|sectionHeader
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|insert (final int row, final DashboardInfo k)
specifier|protected
name|void
name|insert
parameter_list|(
specifier|final
name|int
name|row
parameter_list|,
specifier|final
name|DashboardInfo
name|k
parameter_list|)
block|{
name|table
operator|.
name|insertRow
argument_list|(
name|row
argument_list|)
expr_stmt|;
name|applyDataRowStyle
argument_list|(
name|row
argument_list|)
expr_stmt|;
specifier|final
name|FlexCellFormatter
name|fmt
init|=
name|table
operator|.
name|getFlexCellFormatter
argument_list|()
decl_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
name|row
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
literal|2
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
literal|3
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
name|populate
argument_list|(
name|row
argument_list|,
name|k
argument_list|)
expr_stmt|;
block|}
DECL|method|populate (final int row, final DashboardInfo k)
specifier|protected
name|void
name|populate
parameter_list|(
specifier|final
name|int
name|row
parameter_list|,
specifier|final
name|DashboardInfo
name|k
parameter_list|)
block|{
if|if
condition|(
name|k
operator|.
name|isDefault
argument_list|()
condition|)
block|{
name|table
operator|.
name|setWidget
argument_list|(
name|row
argument_list|,
literal|1
argument_list|,
operator|new
name|Image
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|greenCheck
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|FlexCellFormatter
name|fmt
init|=
name|table
operator|.
name|getFlexCellFormatter
argument_list|()
decl_stmt|;
name|fmt
operator|.
name|getElement
argument_list|(
name|row
argument_list|,
literal|1
argument_list|)
operator|.
name|setTitle
argument_list|(
name|Util
operator|.
name|C
operator|.
name|dashboardDefaultToolTip
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|table
operator|.
name|setWidget
argument_list|(
name|row
argument_list|,
literal|2
argument_list|,
operator|new
name|Anchor
argument_list|(
name|k
operator|.
name|name
argument_list|()
argument_list|,
literal|"#"
operator|+
name|link
argument_list|(
name|k
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|table
operator|.
name|setText
argument_list|(
name|row
argument_list|,
literal|3
argument_list|,
name|k
operator|.
name|description
argument_list|()
argument_list|)
expr_stmt|;
name|setRowItem
argument_list|(
name|row
argument_list|,
name|k
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getRowItemKey (final DashboardInfo item)
specifier|protected
name|Object
name|getRowItemKey
parameter_list|(
specifier|final
name|DashboardInfo
name|item
parameter_list|)
block|{
return|return
name|item
operator|.
name|name
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|onOpenRow (final int row)
specifier|protected
name|void
name|onOpenRow
parameter_list|(
specifier|final
name|int
name|row
parameter_list|)
block|{
if|if
condition|(
name|row
operator|>
literal|0
condition|)
block|{
name|movePointerTo
argument_list|(
name|row
argument_list|)
expr_stmt|;
block|}
name|History
operator|.
name|newItem
argument_list|(
name|link
argument_list|(
name|getRowItem
argument_list|(
name|row
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|link (final DashboardInfo item)
specifier|private
name|String
name|link
parameter_list|(
specifier|final
name|DashboardInfo
name|item
parameter_list|)
block|{
return|return
literal|"/dashboard/?"
operator|+
name|item
operator|.
name|parameters
argument_list|()
return|;
block|}
block|}
end_class

end_unit

