begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2015 The Android Open Source Project
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
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|sshd
operator|.
name|CommandMetaData
operator|.
name|Mode
operator|.
name|MASTER_OR_SLAVE
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|flogger
operator|.
name|FluentLogger
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
name|GlobalCapability
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
name|extensions
operator|.
name|annotations
operator|.
name|RequiresCapability
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
name|AdminHighPriorityCommand
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
name|CommandMetaData
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
name|SshCommand
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
name|SshDaemon
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
name|SshSession
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
name|java
operator|.
name|io
operator|.
name|IOException
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|sshd
operator|.
name|common
operator|.
name|future
operator|.
name|CloseFuture
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|sshd
operator|.
name|common
operator|.
name|io
operator|.
name|IoAcceptor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|sshd
operator|.
name|common
operator|.
name|io
operator|.
name|IoSession
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|sshd
operator|.
name|common
operator|.
name|session
operator|.
name|helpers
operator|.
name|AbstractSession
import|;
end_import

begin_import
import|import
name|org
operator|.
name|kohsuke
operator|.
name|args4j
operator|.
name|Argument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|kohsuke
operator|.
name|args4j
operator|.
name|Option
import|;
end_import

begin_comment
comment|/** Close specified SSH connections */
end_comment

begin_class
annotation|@
name|AdminHighPriorityCommand
annotation|@
name|RequiresCapability
argument_list|(
name|GlobalCapability
operator|.
name|ADMINISTRATE_SERVER
argument_list|)
annotation|@
name|CommandMetaData
argument_list|(
name|name
operator|=
literal|"close-connection"
argument_list|,
name|description
operator|=
literal|"Close the specified SSH connection"
argument_list|,
name|runsAt
operator|=
name|MASTER_OR_SLAVE
argument_list|)
DECL|class|CloseConnection
specifier|final
class|class
name|CloseConnection
extends|extends
name|SshCommand
block|{
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|FluentLogger
name|logger
init|=
name|FluentLogger
operator|.
name|forEnclosingClass
argument_list|()
decl_stmt|;
DECL|field|sshDaemon
annotation|@
name|Inject
specifier|private
name|SshDaemon
name|sshDaemon
decl_stmt|;
annotation|@
name|Argument
argument_list|(
name|index
operator|=
literal|0
argument_list|,
name|multiValued
operator|=
literal|true
argument_list|,
name|required
operator|=
literal|true
argument_list|,
name|metaVar
operator|=
literal|"SESSION_ID"
argument_list|,
name|usage
operator|=
literal|"List of SSH session IDs to be closed"
argument_list|)
DECL|field|sessionIds
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|sessionIds
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--wait"
argument_list|,
name|usage
operator|=
literal|"wait for connection to close before exiting"
argument_list|)
DECL|field|wait
specifier|private
name|boolean
name|wait
decl_stmt|;
annotation|@
name|Override
DECL|method|run ()
specifier|protected
name|void
name|run
parameter_list|()
throws|throws
name|Failure
block|{
name|IoAcceptor
name|acceptor
init|=
name|sshDaemon
operator|.
name|getIoAcceptor
argument_list|()
decl_stmt|;
if|if
condition|(
name|acceptor
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
literal|1
argument_list|,
literal|"fatal: sshd no longer running"
argument_list|)
throw|;
block|}
for|for
control|(
name|String
name|sessionId
range|:
name|sessionIds
control|)
block|{
name|boolean
name|connectionFound
init|=
literal|false
decl_stmt|;
name|int
name|id
init|=
operator|(
name|int
operator|)
name|Long
operator|.
name|parseLong
argument_list|(
name|sessionId
argument_list|,
literal|16
argument_list|)
decl_stmt|;
for|for
control|(
name|IoSession
name|io
range|:
name|acceptor
operator|.
name|getManagedSessions
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|AbstractSession
name|serverSession
init|=
name|AbstractSession
operator|.
name|getSession
argument_list|(
name|io
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|SshSession
name|sshSession
init|=
name|serverSession
operator|!=
literal|null
condition|?
name|serverSession
operator|.
name|getAttribute
argument_list|(
name|SshSession
operator|.
name|KEY
argument_list|)
else|:
literal|null
decl_stmt|;
if|if
condition|(
name|sshSession
operator|!=
literal|null
operator|&&
name|sshSession
operator|.
name|getSessionId
argument_list|()
operator|==
name|id
condition|)
block|{
name|connectionFound
operator|=
literal|true
expr_stmt|;
name|stdout
operator|.
name|println
argument_list|(
literal|"closing connection "
operator|+
name|sessionId
operator|+
literal|"..."
argument_list|)
expr_stmt|;
name|CloseFuture
name|future
init|=
name|io
operator|.
name|close
argument_list|(
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|wait
condition|)
block|{
try|try
block|{
name|future
operator|.
name|await
argument_list|()
expr_stmt|;
name|stdout
operator|.
name|println
argument_list|(
literal|"closed connection "
operator|+
name|sessionId
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|atWarning
argument_list|()
operator|.
name|log
argument_list|(
literal|"Wait for connection to close interrupted: %s"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|connectionFound
condition|)
block|{
name|stderr
operator|.
name|print
argument_list|(
literal|"close connection "
operator|+
name|sessionId
operator|+
literal|": no such connection\n"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

