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
DECL|package|com.google.gerrit.server.mail
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|mail
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
name|common
operator|.
name|errors
operator|.
name|EmailException
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
name|AccountProjectWatch
operator|.
name|NotifyType
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
name|gwtorm
operator|.
name|server
operator|.
name|OrmException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|assistedinject
operator|.
name|Assisted
import|;
end_import

begin_comment
comment|/** Send notice about a change being restored by its owner. */
end_comment

begin_class
DECL|class|RestoredSender
specifier|public
class|class
name|RestoredSender
extends|extends
name|ReplyToChangeSender
block|{
DECL|interface|Factory
specifier|public
specifier|static
interface|interface
name|Factory
extends|extends
name|ReplyToChangeSender
operator|.
name|Factory
argument_list|<
name|RestoredSender
argument_list|>
block|{
annotation|@
name|Override
DECL|method|create (Change.Id id)
name|RestoredSender
name|create
parameter_list|(
name|Change
operator|.
name|Id
name|id
parameter_list|)
function_decl|;
block|}
annotation|@
name|Inject
DECL|method|RestoredSender (EmailArguments ea, @Assisted Change.Id id)
specifier|public
name|RestoredSender
parameter_list|(
name|EmailArguments
name|ea
parameter_list|,
annotation|@
name|Assisted
name|Change
operator|.
name|Id
name|id
parameter_list|)
throws|throws
name|OrmException
block|{
name|super
argument_list|(
name|ea
argument_list|,
literal|"restore"
argument_list|,
name|newChangeData
argument_list|(
name|ea
argument_list|,
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init ()
specifier|protected
name|void
name|init
parameter_list|()
throws|throws
name|EmailException
block|{
name|super
operator|.
name|init
argument_list|()
expr_stmt|;
name|ccAllApprovals
argument_list|()
expr_stmt|;
name|bccStarredBy
argument_list|()
expr_stmt|;
name|includeWatchers
argument_list|(
name|NotifyType
operator|.
name|ALL_COMMENTS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|formatChange ()
specifier|protected
name|void
name|formatChange
parameter_list|()
throws|throws
name|EmailException
block|{
name|appendText
argument_list|(
name|velocifyFile
argument_list|(
literal|"Restored.vm"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

