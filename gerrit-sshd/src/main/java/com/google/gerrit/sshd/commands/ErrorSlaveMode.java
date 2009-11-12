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
name|BaseCommand
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * A command which just throws an error because it shouldn't be ran on this  * server. This is used when a user tries to run a command on a server in Slave  * Mode, but the command only applies to the Master server.  */
end_comment

begin_class
DECL|class|ErrorSlaveMode
specifier|final
class|class
name|ErrorSlaveMode
extends|extends
name|BaseCommand
block|{
annotation|@
name|Override
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{
name|String
name|msg
init|=
literal|"error: That command is disabled on this server.\n\n"
operator|+
literal|"Please use the master server URL.\n"
decl_stmt|;
try|try
block|{
name|err
operator|.
name|write
argument_list|(
name|msg
operator|.
name|getBytes
argument_list|(
name|ENC
argument_list|)
argument_list|)
expr_stmt|;
name|err
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// Ignore errors writing to the client
block|}
name|onExit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

