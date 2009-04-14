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
name|Link
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
name|rpc
operator|.
name|Common
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
name|ScreenLoadCallback
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

begin_class
DECL|class|AccountDashboardScreen
specifier|public
class|class
name|AccountDashboardScreen
extends|extends
name|Screen
block|{
DECL|field|ownerId
specifier|private
specifier|final
name|Account
operator|.
name|Id
name|ownerId
decl_stmt|;
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
DECL|method|AccountDashboardScreen (final Account.Id id)
specifier|public
name|AccountDashboardScreen
parameter_list|(
specifier|final
name|Account
operator|.
name|Id
name|id
parameter_list|)
block|{
name|ownerId
operator|=
name|id
operator|!=
literal|null
condition|?
name|id
else|:
name|Common
operator|.
name|getAccountId
argument_list|()
expr_stmt|;
name|setRequiresSignIn
argument_list|(
name|ownerId
operator|==
literal|null
argument_list|)
expr_stmt|;
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
literal|""
argument_list|)
expr_stmt|;
name|forReview
operator|=
operator|new
name|ChangeTable
operator|.
name|Section
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|closed
operator|=
operator|new
name|ChangeTable
operator|.
name|Section
argument_list|(
literal|""
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
name|table
operator|.
name|setSavePointerId
argument_list|(
name|Link
operator|.
name|toAccountDashboard
argument_list|(
name|ownerId
argument_list|)
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
name|Util
operator|.
name|LIST_SVC
operator|.
name|forAccount
argument_list|(
name|ownerId
argument_list|,
operator|new
name|ScreenLoadCallback
argument_list|<
name|AccountDashboardInfo
argument_list|>
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|preDisplay
parameter_list|(
specifier|final
name|AccountDashboardInfo
name|r
parameter_list|)
block|{
name|display
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|display (final AccountDashboardInfo r)
specifier|private
name|void
name|display
parameter_list|(
specifier|final
name|AccountDashboardInfo
name|r
parameter_list|)
block|{
name|table
operator|.
name|setAccountInfoCache
argument_list|(
name|r
operator|.
name|getAccounts
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|AccountInfo
name|o
init|=
name|r
operator|.
name|getAccounts
argument_list|()
operator|.
name|get
argument_list|(
name|r
operator|.
name|getOwner
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|String
name|name
init|=
name|FormatUtil
operator|.
name|name
argument_list|(
name|o
argument_list|)
decl_stmt|;
name|setTitleText
argument_list|(
name|Util
operator|.
name|M
operator|.
name|accountDashboardTitle
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|byOwner
operator|.
name|setTitleText
argument_list|(
name|Util
operator|.
name|M
operator|.
name|changesStartedBy
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|forReview
operator|.
name|setTitleText
argument_list|(
name|Util
operator|.
name|M
operator|.
name|changesReviewableBy
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|closed
operator|.
name|setTitleText
argument_list|(
name|Util
operator|.
name|C
operator|.
name|changesRecentlyClosed
argument_list|()
argument_list|)
expr_stmt|;
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
name|table
operator|.
name|finishDisplay
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

