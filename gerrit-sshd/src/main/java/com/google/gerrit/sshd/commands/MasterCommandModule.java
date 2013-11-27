begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2009 The Android Open Source Project
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
DECL|package|com.google.gerrit.sshd.commands
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|sshd
operator|.
name|commands
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
name|sshd
operator|.
name|CommandModule
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
name|sshd
operator|.
name|CommandName
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
name|sshd
operator|.
name|Commands
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
name|sshd
operator|.
name|DispatchCommandProvider
import|;
end_import

begin_comment
comment|/** Register the commands a Gerrit server in master mode supports. */
end_comment

begin_class
DECL|class|MasterCommandModule
specifier|public
class|class
name|MasterCommandModule
extends|extends
name|CommandModule
block|{
annotation|@
name|Override
DECL|method|configure ()
specifier|protected
name|void
name|configure
parameter_list|()
block|{
specifier|final
name|CommandName
name|gerrit
init|=
name|Commands
operator|.
name|named
argument_list|(
literal|"gerrit"
argument_list|)
decl_stmt|;
specifier|final
name|CommandName
name|testSubmit
init|=
name|Commands
operator|.
name|named
argument_list|(
name|gerrit
argument_list|,
literal|"test-submit"
argument_list|)
decl_stmt|;
name|command
argument_list|(
name|gerrit
argument_list|,
name|CreateAccountCommand
operator|.
name|class
argument_list|)
expr_stmt|;
name|command
argument_list|(
name|gerrit
argument_list|,
name|CreateGroupCommand
operator|.
name|class
argument_list|)
expr_stmt|;
name|command
argument_list|(
name|gerrit
argument_list|,
name|RenameGroupCommand
operator|.
name|class
argument_list|)
expr_stmt|;
name|command
argument_list|(
name|gerrit
argument_list|,
name|CreateProjectCommand
operator|.
name|class
argument_list|)
expr_stmt|;
name|command
argument_list|(
name|gerrit
argument_list|,
name|AdminQueryShell
operator|.
name|class
argument_list|)
expr_stmt|;
name|command
argument_list|(
name|gerrit
argument_list|,
name|SetReviewersCommand
operator|.
name|class
argument_list|)
expr_stmt|;
name|command
argument_list|(
name|gerrit
argument_list|,
name|Receive
operator|.
name|class
argument_list|)
expr_stmt|;
name|command
argument_list|(
name|gerrit
argument_list|,
name|AdminSetParent
operator|.
name|class
argument_list|)
expr_stmt|;
name|command
argument_list|(
name|gerrit
argument_list|,
name|ReviewCommand
operator|.
name|class
argument_list|)
expr_stmt|;
name|command
argument_list|(
name|gerrit
argument_list|,
name|SetAccountCommand
operator|.
name|class
argument_list|)
expr_stmt|;
name|command
argument_list|(
name|gerrit
argument_list|,
name|SetMembersCommand
operator|.
name|class
argument_list|)
expr_stmt|;
name|command
argument_list|(
name|gerrit
argument_list|,
name|SetProjectCommand
operator|.
name|class
argument_list|)
expr_stmt|;
name|command
argument_list|(
name|gerrit
argument_list|,
literal|"test-submit"
argument_list|)
operator|.
name|toProvider
argument_list|(
operator|new
name|DispatchCommandProvider
argument_list|(
name|testSubmit
argument_list|)
argument_list|)
expr_stmt|;
name|command
argument_list|(
name|testSubmit
argument_list|,
name|TestSubmitRuleCommand
operator|.
name|class
argument_list|)
expr_stmt|;
name|command
argument_list|(
name|testSubmit
argument_list|,
name|TestSubmitTypeCommand
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

