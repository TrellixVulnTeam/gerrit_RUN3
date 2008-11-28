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
name|AccountDashboardInfo
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

begin_class
DECL|class|MineScreen
specifier|public
class|class
name|MineScreen
extends|extends
name|Screen
block|{
DECL|field|table
specifier|private
name|ChangeTable
name|table
decl_stmt|;
DECL|field|byOwner
specifier|private
name|ChangeTable
operator|.
name|Section
name|byOwner
decl_stmt|;
DECL|field|forReview
specifier|private
name|ChangeTable
operator|.
name|Section
name|forReview
decl_stmt|;
DECL|field|closed
specifier|private
name|ChangeTable
operator|.
name|Section
name|closed
decl_stmt|;
DECL|method|MineScreen ()
specifier|public
name|MineScreen
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|MineScreen (final Account.Id id)
specifier|public
name|MineScreen
parameter_list|(
specifier|final
name|Account
operator|.
name|Id
name|id
parameter_list|)
block|{
name|super
argument_list|(
name|Util
operator|.
name|C
operator|.
name|mineHeading
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|=
operator|new
name|ChangeTable
argument_list|()
expr_stmt|;
name|byOwner
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
name|mineByMe
argument_list|()
argument_list|)
expr_stmt|;
name|forReview
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
name|mineForReview
argument_list|()
argument_list|)
expr_stmt|;
name|closed
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
name|mineClosed
argument_list|()
argument_list|)
expr_stmt|;
name|Util
operator|.
name|LIST_SVC
operator|.
name|forAccount
argument_list|(
name|id
argument_list|,
operator|new
name|AsyncCallback
argument_list|<
name|AccountDashboardInfo
argument_list|>
argument_list|()
block|{
specifier|public
name|void
name|onSuccess
parameter_list|(
specifier|final
name|AccountDashboardInfo
name|r
parameter_list|)
block|{
name|byOwner
operator|.
name|display
argument_list|(
name|r
operator|.
name|getByOwner
argument_list|()
argument_list|)
expr_stmt|;
name|forReview
operator|.
name|display
argument_list|(
name|r
operator|.
name|getForReview
argument_list|()
argument_list|)
expr_stmt|;
name|closed
operator|.
name|display
argument_list|(
name|r
operator|.
name|getClosed
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|onFailure
parameter_list|(
specifier|final
name|Throwable
name|caught
parameter_list|)
block|{
name|GWT
operator|.
name|log
argument_list|(
literal|"Fail"
argument_list|,
name|caught
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|table
operator|.
name|addSection
argument_list|(
name|byOwner
argument_list|)
expr_stmt|;
name|table
operator|.
name|addSection
argument_list|(
name|forReview
argument_list|)
expr_stmt|;
name|table
operator|.
name|addSection
argument_list|(
name|closed
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|table
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

