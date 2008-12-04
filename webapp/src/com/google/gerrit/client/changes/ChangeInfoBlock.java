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
name|account
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
name|client
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
name|gwt
operator|.
name|i18n
operator|.
name|client
operator|.
name|DateTimeFormat
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
name|Grid
import|;
end_import

begin_class
DECL|class|ChangeInfoBlock
specifier|public
class|class
name|ChangeInfoBlock
extends|extends
name|Composite
block|{
DECL|field|R_OWNER
specifier|private
specifier|static
specifier|final
name|int
name|R_OWNER
init|=
literal|0
decl_stmt|;
DECL|field|R_PROJECT
specifier|private
specifier|static
specifier|final
name|int
name|R_PROJECT
init|=
literal|1
decl_stmt|;
DECL|field|R_BRANCH
specifier|private
specifier|static
specifier|final
name|int
name|R_BRANCH
init|=
literal|2
decl_stmt|;
DECL|field|R_UPLOADED
specifier|private
specifier|static
specifier|final
name|int
name|R_UPLOADED
init|=
literal|3
decl_stmt|;
DECL|field|R_STATUS
specifier|private
specifier|static
specifier|final
name|int
name|R_STATUS
init|=
literal|4
decl_stmt|;
DECL|field|R_CNT
specifier|private
specifier|static
specifier|final
name|int
name|R_CNT
init|=
literal|5
decl_stmt|;
DECL|field|dtfmt
specifier|private
specifier|static
specifier|final
name|DateTimeFormat
name|dtfmt
init|=
name|DateTimeFormat
operator|.
name|getMediumDateTimeFormat
argument_list|()
decl_stmt|;
DECL|field|table
specifier|private
specifier|final
name|Grid
name|table
decl_stmt|;
DECL|method|ChangeInfoBlock ()
specifier|public
name|ChangeInfoBlock
parameter_list|()
block|{
name|table
operator|=
operator|new
name|Grid
argument_list|(
name|R_CNT
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|table
operator|.
name|setStyleName
argument_list|(
literal|"gerrit-ChangeInfoBlock"
argument_list|)
expr_stmt|;
name|initRow
argument_list|(
name|R_OWNER
argument_list|,
name|Util
operator|.
name|C
operator|.
name|changeInfoBlockOwner
argument_list|()
argument_list|)
expr_stmt|;
name|initRow
argument_list|(
name|R_PROJECT
argument_list|,
name|Util
operator|.
name|C
operator|.
name|changeInfoBlockProject
argument_list|()
argument_list|)
expr_stmt|;
name|initRow
argument_list|(
name|R_BRANCH
argument_list|,
name|Util
operator|.
name|C
operator|.
name|changeInfoBlockBranch
argument_list|()
argument_list|)
expr_stmt|;
name|initRow
argument_list|(
name|R_UPLOADED
argument_list|,
name|Util
operator|.
name|C
operator|.
name|changeInfoBlockUploaded
argument_list|()
argument_list|)
expr_stmt|;
name|initRow
argument_list|(
name|R_STATUS
argument_list|,
name|Util
operator|.
name|C
operator|.
name|changeInfoBlockStatus
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|getCellFormatter
argument_list|()
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
name|table
operator|.
name|getCellFormatter
argument_list|()
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
name|table
operator|.
name|getCellFormatter
argument_list|()
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
name|initWidget
argument_list|(
name|table
argument_list|)
expr_stmt|;
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
name|table
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
name|table
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
DECL|method|display (final ChangeDetail detail)
specifier|public
name|void
name|display
parameter_list|(
specifier|final
name|ChangeDetail
name|detail
parameter_list|)
block|{
specifier|final
name|Change
name|chg
init|=
name|detail
operator|.
name|getChange
argument_list|()
decl_stmt|;
specifier|final
name|Branch
operator|.
name|NameKey
name|dst
init|=
name|chg
operator|.
name|getDest
argument_list|()
decl_stmt|;
name|table
operator|.
name|setWidget
argument_list|(
name|R_OWNER
argument_list|,
literal|1
argument_list|,
operator|new
name|AccountDashboardLink
argument_list|(
name|detail
operator|.
name|getOwner
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|table
operator|.
name|setText
argument_list|(
name|R_PROJECT
argument_list|,
literal|1
argument_list|,
name|dst
operator|.
name|getParentKey
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|setText
argument_list|(
name|R_BRANCH
argument_list|,
literal|1
argument_list|,
name|dst
operator|.
name|getShortName
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|setText
argument_list|(
name|R_UPLOADED
argument_list|,
literal|1
argument_list|,
name|dtfmt
operator|.
name|format
argument_list|(
name|chg
operator|.
name|getCreatedOn
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|table
operator|.
name|setText
argument_list|(
name|R_STATUS
argument_list|,
literal|1
argument_list|,
name|Util
operator|.
name|toLongString
argument_list|(
name|chg
operator|.
name|getStatus
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|chg
operator|.
name|getStatus
argument_list|()
operator|.
name|isClosed
argument_list|()
condition|)
block|{
name|table
operator|.
name|getCellFormatter
argument_list|()
operator|.
name|addStyleName
argument_list|(
name|R_STATUS
argument_list|,
literal|1
argument_list|,
literal|"closedstate"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|table
operator|.
name|getCellFormatter
argument_list|()
operator|.
name|removeStyleName
argument_list|(
name|R_STATUS
argument_list|,
literal|1
argument_list|,
literal|"closedstate"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

