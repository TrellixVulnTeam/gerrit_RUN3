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

begin_comment
comment|/** Register the commands a Gerrit server in slave mode supports. */
end_comment

begin_class
DECL|class|SlaveCommandModule
specifier|public
class|class
name|SlaveCommandModule
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
name|command
argument_list|(
name|gerrit
argument_list|,
literal|"approve"
argument_list|)
operator|.
name|to
argument_list|(
name|ErrorSlaveMode
operator|.
name|class
argument_list|)
expr_stmt|;
name|command
argument_list|(
name|gerrit
argument_list|,
literal|"create-account"
argument_list|)
operator|.
name|to
argument_list|(
name|ErrorSlaveMode
operator|.
name|class
argument_list|)
expr_stmt|;
name|command
argument_list|(
name|gerrit
argument_list|,
literal|"create-project"
argument_list|)
operator|.
name|to
argument_list|(
name|ErrorSlaveMode
operator|.
name|class
argument_list|)
expr_stmt|;
name|command
argument_list|(
name|gerrit
argument_list|,
literal|"gsql"
argument_list|)
operator|.
name|to
argument_list|(
name|ErrorSlaveMode
operator|.
name|class
argument_list|)
expr_stmt|;
name|command
argument_list|(
name|gerrit
argument_list|,
literal|"receive-pack"
argument_list|)
operator|.
name|to
argument_list|(
name|ErrorSlaveMode
operator|.
name|class
argument_list|)
expr_stmt|;
name|command
argument_list|(
name|gerrit
argument_list|,
literal|"replicate"
argument_list|)
operator|.
name|to
argument_list|(
name|ErrorSlaveMode
operator|.
name|class
argument_list|)
expr_stmt|;
name|command
argument_list|(
name|gerrit
argument_list|,
literal|"review"
argument_list|)
operator|.
name|to
argument_list|(
name|ErrorSlaveMode
operator|.
name|class
argument_list|)
expr_stmt|;
name|command
argument_list|(
name|gerrit
argument_list|,
literal|"set-project-parent"
argument_list|)
operator|.
name|to
argument_list|(
name|ErrorSlaveMode
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

