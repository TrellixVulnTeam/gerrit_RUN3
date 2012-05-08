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
name|server
operator|.
name|CurrentUser
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
name|server
operator|.
name|IdentifiedUser
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
name|server
operator|.
name|util
operator|.
name|IdGenerator
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
name|org
operator|.
name|apache
operator|.
name|mina
operator|.
name|core
operator|.
name|service
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
name|mina
operator|.
name|core
operator|.
name|session
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
name|server
operator|.
name|session
operator|.
name|ServerSession
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

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
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
name|Date
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
comment|/** Show the current SSH connections. */
end_comment

begin_class
annotation|@
name|RequiresCapability
argument_list|(
name|GlobalCapability
operator|.
name|VIEW_CONNECTIONS
argument_list|)
DECL|class|ShowConnections
specifier|final
class|class
name|ShowConnections
extends|extends
name|SshCommand
block|{
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--numeric"
argument_list|,
name|aliases
operator|=
block|{
literal|"-n"
block|}
argument_list|,
name|usage
operator|=
literal|"don't resolve names"
argument_list|)
DECL|field|numeric
specifier|private
name|boolean
name|numeric
decl_stmt|;
annotation|@
name|Inject
DECL|field|daemon
specifier|private
name|SshDaemon
name|daemon
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
specifier|final
name|IoAcceptor
name|acceptor
init|=
name|daemon
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
specifier|final
name|List
argument_list|<
name|IoSession
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|IoSession
argument_list|>
argument_list|(
name|acceptor
operator|.
name|getManagedSessions
argument_list|()
operator|.
name|values
argument_list|()
argument_list|)
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
name|IoSession
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|IoSession
name|arg0
parameter_list|,
name|IoSession
name|arg1
parameter_list|)
block|{
if|if
condition|(
name|arg0
operator|.
name|getCreationTime
argument_list|()
operator|<
name|arg1
operator|.
name|getCreationTime
argument_list|()
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|arg0
operator|.
name|getCreationTime
argument_list|()
operator|>
name|arg1
operator|.
name|getCreationTime
argument_list|()
condition|)
block|{
return|return
literal|1
return|;
block|}
return|return
call|(
name|int
call|)
argument_list|(
name|arg0
operator|.
name|getId
argument_list|()
operator|-
name|arg1
operator|.
name|getId
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
specifier|final
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|stdout
operator|.
name|print
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%-8s %8s %8s   %-15s %s\n"
argument_list|,
comment|//
literal|"Session"
argument_list|,
literal|"Start"
argument_list|,
literal|"Idle"
argument_list|,
literal|"User"
argument_list|,
literal|"Remote Host"
argument_list|)
argument_list|)
expr_stmt|;
name|stdout
operator|.
name|print
argument_list|(
literal|"--------------------------------------------------------------\n"
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|IoSession
name|io
range|:
name|list
control|)
block|{
name|ServerSession
name|s
init|=
operator|(
name|ServerSession
operator|)
name|ServerSession
operator|.
name|getSession
argument_list|(
name|io
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|SshSession
name|sd
init|=
name|s
operator|!=
literal|null
condition|?
name|s
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
specifier|final
name|SocketAddress
name|remoteAddress
init|=
name|io
operator|.
name|getRemoteAddress
argument_list|()
decl_stmt|;
specifier|final
name|long
name|start
init|=
name|io
operator|.
name|getCreationTime
argument_list|()
decl_stmt|;
specifier|final
name|long
name|idle
init|=
name|now
operator|-
name|io
operator|.
name|getLastIoTime
argument_list|()
decl_stmt|;
name|stdout
operator|.
name|print
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%8s %8s %8s  %-15.15s %.30s\n"
argument_list|,
comment|//
name|id
argument_list|(
name|sd
argument_list|)
argument_list|,
comment|//
name|time
argument_list|(
name|now
argument_list|,
name|start
argument_list|)
argument_list|,
comment|//
name|age
argument_list|(
name|idle
argument_list|)
argument_list|,
comment|//
name|username
argument_list|(
name|sd
argument_list|)
argument_list|,
comment|//
name|hostname
argument_list|(
name|remoteAddress
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|stdout
operator|.
name|print
argument_list|(
literal|"--\n"
argument_list|)
expr_stmt|;
block|}
DECL|method|id (final SshSession sd)
specifier|private
specifier|static
name|String
name|id
parameter_list|(
specifier|final
name|SshSession
name|sd
parameter_list|)
block|{
return|return
name|sd
operator|!=
literal|null
condition|?
name|IdGenerator
operator|.
name|format
argument_list|(
name|sd
operator|.
name|getSessionId
argument_list|()
argument_list|)
else|:
literal|""
return|;
block|}
DECL|method|time (final long now, final long time)
specifier|private
specifier|static
name|String
name|time
parameter_list|(
specifier|final
name|long
name|now
parameter_list|,
specifier|final
name|long
name|time
parameter_list|)
block|{
if|if
condition|(
name|time
operator|-
name|now
operator|<
literal|24
operator|*
literal|60
operator|*
literal|60
operator|*
literal|1000L
condition|)
block|{
return|return
operator|new
name|SimpleDateFormat
argument_list|(
literal|"HH:mm:ss"
argument_list|)
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|(
name|time
argument_list|)
argument_list|)
return|;
block|}
return|return
operator|new
name|SimpleDateFormat
argument_list|(
literal|"MMM-dd"
argument_list|)
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|(
name|time
argument_list|)
argument_list|)
return|;
block|}
DECL|method|age (long age)
specifier|private
specifier|static
name|String
name|age
parameter_list|(
name|long
name|age
parameter_list|)
block|{
name|age
operator|/=
literal|1000
expr_stmt|;
specifier|final
name|int
name|sec
init|=
call|(
name|int
call|)
argument_list|(
name|age
operator|%
literal|60
argument_list|)
decl_stmt|;
name|age
operator|/=
literal|60
expr_stmt|;
specifier|final
name|int
name|min
init|=
call|(
name|int
call|)
argument_list|(
name|age
operator|%
literal|60
argument_list|)
decl_stmt|;
name|age
operator|/=
literal|60
expr_stmt|;
specifier|final
name|int
name|hr
init|=
call|(
name|int
call|)
argument_list|(
name|age
operator|%
literal|60
argument_list|)
decl_stmt|;
return|return
name|String
operator|.
name|format
argument_list|(
literal|"%02d:%02d:%02d"
argument_list|,
name|hr
argument_list|,
name|min
argument_list|,
name|sec
argument_list|)
return|;
block|}
DECL|method|username (final SshSession sd)
specifier|private
name|String
name|username
parameter_list|(
specifier|final
name|SshSession
name|sd
parameter_list|)
block|{
if|if
condition|(
name|sd
operator|==
literal|null
condition|)
block|{
return|return
literal|""
return|;
block|}
specifier|final
name|CurrentUser
name|user
init|=
name|sd
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
if|if
condition|(
name|user
operator|instanceof
name|IdentifiedUser
condition|)
block|{
name|IdentifiedUser
name|u
init|=
operator|(
name|IdentifiedUser
operator|)
name|user
decl_stmt|;
if|if
condition|(
operator|!
name|numeric
condition|)
block|{
name|String
name|name
init|=
name|u
operator|.
name|getAccount
argument_list|()
operator|.
name|getUserName
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
operator|&&
operator|!
name|name
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|name
return|;
block|}
block|}
return|return
literal|"a/"
operator|+
name|u
operator|.
name|getAccountId
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|""
return|;
block|}
block|}
DECL|method|hostname (final SocketAddress remoteAddress)
specifier|private
name|String
name|hostname
parameter_list|(
specifier|final
name|SocketAddress
name|remoteAddress
parameter_list|)
block|{
if|if
condition|(
name|remoteAddress
operator|==
literal|null
condition|)
block|{
return|return
literal|"?"
return|;
block|}
name|String
name|host
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|remoteAddress
operator|instanceof
name|InetSocketAddress
condition|)
block|{
specifier|final
name|InetSocketAddress
name|sa
init|=
operator|(
name|InetSocketAddress
operator|)
name|remoteAddress
decl_stmt|;
specifier|final
name|InetAddress
name|in
init|=
name|sa
operator|.
name|getAddress
argument_list|()
decl_stmt|;
if|if
condition|(
name|numeric
condition|)
block|{
return|return
name|in
operator|.
name|getHostAddress
argument_list|()
return|;
block|}
if|if
condition|(
name|in
operator|!=
literal|null
condition|)
block|{
name|host
operator|=
name|in
operator|.
name|getCanonicalHostName
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|host
operator|=
name|sa
operator|.
name|getHostName
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|host
operator|==
literal|null
condition|)
block|{
name|host
operator|=
name|remoteAddress
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
return|return
name|host
return|;
block|}
block|}
end_class

end_unit

