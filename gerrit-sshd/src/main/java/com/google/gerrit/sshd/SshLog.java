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
DECL|package|com.google.gerrit.sshd
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|sshd
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
name|lifecycle
operator|.
name|LifecycleListener
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
name|config
operator|.
name|SitePaths
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
name|SshScopes
operator|.
name|Context
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
name|Provider
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
name|Singleton
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Appender
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|AsyncAppender
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|DailyRollingFileAppender
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Layout
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|spi
operator|.
name|ErrorHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|spi
operator|.
name|LoggingEvent
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
name|eclipse
operator|.
name|jgit
operator|.
name|util
operator|.
name|QuotedString
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|Calendar
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
name|TimeZone
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|SshLog
class|class
name|SshLog
implements|implements
name|LifecycleListener
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|SshLog
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|LOG_NAME
specifier|private
specifier|static
specifier|final
name|String
name|LOG_NAME
init|=
literal|"sshd_log"
decl_stmt|;
DECL|field|P_SESSION
specifier|private
specifier|static
specifier|final
name|String
name|P_SESSION
init|=
literal|"session"
decl_stmt|;
DECL|field|P_USER_NAME
specifier|private
specifier|static
specifier|final
name|String
name|P_USER_NAME
init|=
literal|"userName"
decl_stmt|;
DECL|field|P_ACCOUNT_ID
specifier|private
specifier|static
specifier|final
name|String
name|P_ACCOUNT_ID
init|=
literal|"accountId"
decl_stmt|;
DECL|field|P_WAIT
specifier|private
specifier|static
specifier|final
name|String
name|P_WAIT
init|=
literal|"queueWaitTime"
decl_stmt|;
DECL|field|P_EXEC
specifier|private
specifier|static
specifier|final
name|String
name|P_EXEC
init|=
literal|"executionTime"
decl_stmt|;
DECL|field|P_STATUS
specifier|private
specifier|static
specifier|final
name|String
name|P_STATUS
init|=
literal|"status"
decl_stmt|;
DECL|field|session
specifier|private
specifier|final
name|Provider
argument_list|<
name|ServerSession
argument_list|>
name|session
decl_stmt|;
DECL|field|user
specifier|private
specifier|final
name|Provider
argument_list|<
name|IdentifiedUser
argument_list|>
name|user
decl_stmt|;
DECL|field|async
specifier|private
specifier|final
name|AsyncAppender
name|async
decl_stmt|;
annotation|@
name|Inject
DECL|method|SshLog (final Provider<ServerSession> session, final Provider<IdentifiedUser> user, final SitePaths site)
name|SshLog
parameter_list|(
specifier|final
name|Provider
argument_list|<
name|ServerSession
argument_list|>
name|session
parameter_list|,
specifier|final
name|Provider
argument_list|<
name|IdentifiedUser
argument_list|>
name|user
parameter_list|,
specifier|final
name|SitePaths
name|site
parameter_list|)
block|{
name|this
operator|.
name|session
operator|=
name|session
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
specifier|final
name|DailyRollingFileAppender
name|dst
init|=
operator|new
name|DailyRollingFileAppender
argument_list|()
decl_stmt|;
name|dst
operator|.
name|setName
argument_list|(
name|LOG_NAME
argument_list|)
expr_stmt|;
name|dst
operator|.
name|setLayout
argument_list|(
operator|new
name|MyLayout
argument_list|()
argument_list|)
expr_stmt|;
name|dst
operator|.
name|setEncoding
argument_list|(
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|dst
operator|.
name|setFile
argument_list|(
operator|new
name|File
argument_list|(
name|resolve
argument_list|(
name|site
operator|.
name|logs_dir
argument_list|)
argument_list|,
name|LOG_NAME
argument_list|)
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|dst
operator|.
name|setImmediateFlush
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|dst
operator|.
name|setAppend
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|dst
operator|.
name|setThreshold
argument_list|(
name|Level
operator|.
name|INFO
argument_list|)
expr_stmt|;
name|dst
operator|.
name|setErrorHandler
argument_list|(
operator|new
name|DieErrorHandler
argument_list|()
argument_list|)
expr_stmt|;
name|dst
operator|.
name|activateOptions
argument_list|()
expr_stmt|;
name|dst
operator|.
name|setErrorHandler
argument_list|(
operator|new
name|LogLogHandler
argument_list|()
argument_list|)
expr_stmt|;
name|async
operator|=
operator|new
name|AsyncAppender
argument_list|()
expr_stmt|;
name|async
operator|.
name|setBlocking
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|async
operator|.
name|setBufferSize
argument_list|(
literal|64
argument_list|)
expr_stmt|;
name|async
operator|.
name|setLocationInfo
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|async
operator|.
name|addAppender
argument_list|(
name|dst
argument_list|)
expr_stmt|;
name|async
operator|.
name|activateOptions
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|async
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|onLogin ()
name|void
name|onLogin
parameter_list|()
block|{
specifier|final
name|ServerSession
name|s
init|=
name|session
operator|.
name|get
argument_list|()
decl_stmt|;
specifier|final
name|SocketAddress
name|addr
init|=
name|s
operator|.
name|getIoSession
argument_list|()
operator|.
name|getRemoteAddress
argument_list|()
decl_stmt|;
name|async
operator|.
name|append
argument_list|(
name|log
argument_list|(
literal|"LOGIN FROM "
operator|+
name|format
argument_list|(
name|addr
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|onAuthFail (final ServerSession s, final String username)
name|void
name|onAuthFail
parameter_list|(
specifier|final
name|ServerSession
name|s
parameter_list|,
specifier|final
name|String
name|username
parameter_list|)
block|{
specifier|final
name|SocketAddress
name|addr
init|=
name|s
operator|.
name|getIoSession
argument_list|()
operator|.
name|getRemoteAddress
argument_list|()
decl_stmt|;
specifier|final
name|LoggingEvent
name|event
init|=
operator|new
name|LoggingEvent
argument_list|(
comment|//
name|Logger
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
comment|// fqnOfCategoryClass
literal|null
argument_list|,
comment|// logger (optional)
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
comment|// when
name|Level
operator|.
name|INFO
argument_list|,
comment|// level
literal|"AUTH FAILURE FROM "
operator|+
name|format
argument_list|(
name|addr
argument_list|)
argument_list|,
comment|// message text
literal|"SSHD"
argument_list|,
comment|// thread name
literal|null
argument_list|,
comment|// exception information
literal|null
argument_list|,
comment|// current NDC string
literal|null
argument_list|,
comment|// caller location
literal|null
comment|// MDC properties
argument_list|)
decl_stmt|;
name|event
operator|.
name|setProperty
argument_list|(
name|P_SESSION
argument_list|,
name|id
argument_list|(
name|s
operator|.
name|getAttribute
argument_list|(
name|SshUtil
operator|.
name|SESSION_ID
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|event
operator|.
name|setProperty
argument_list|(
name|P_USER_NAME
argument_list|,
name|username
argument_list|)
expr_stmt|;
specifier|final
name|String
name|error
init|=
name|s
operator|.
name|getAttribute
argument_list|(
name|SshUtil
operator|.
name|AUTH_ERROR
argument_list|)
decl_stmt|;
if|if
condition|(
name|error
operator|!=
literal|null
condition|)
block|{
name|event
operator|.
name|setProperty
argument_list|(
name|P_STATUS
argument_list|,
name|error
argument_list|)
expr_stmt|;
block|}
name|async
operator|.
name|append
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
DECL|method|onExecute (final Context ctx, final String commandLine, int exitValue)
name|void
name|onExecute
parameter_list|(
specifier|final
name|Context
name|ctx
parameter_list|,
specifier|final
name|String
name|commandLine
parameter_list|,
name|int
name|exitValue
parameter_list|)
block|{
name|String
name|cmd
init|=
name|QuotedString
operator|.
name|BOURNE
operator|.
name|quote
argument_list|(
name|commandLine
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmd
operator|==
name|commandLine
condition|)
block|{
name|cmd
operator|=
literal|"'"
operator|+
name|commandLine
operator|+
literal|"'"
expr_stmt|;
block|}
specifier|final
name|LoggingEvent
name|event
init|=
name|log
argument_list|(
name|cmd
argument_list|)
decl_stmt|;
name|event
operator|.
name|setProperty
argument_list|(
name|P_WAIT
argument_list|,
operator|(
name|ctx
operator|.
name|started
operator|-
name|ctx
operator|.
name|created
operator|)
operator|+
literal|"ms"
argument_list|)
expr_stmt|;
name|event
operator|.
name|setProperty
argument_list|(
name|P_EXEC
argument_list|,
operator|(
name|ctx
operator|.
name|finished
operator|-
name|ctx
operator|.
name|started
operator|)
operator|+
literal|"ms"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|status
decl_stmt|;
switch|switch
condition|(
name|exitValue
condition|)
block|{
case|case
name|BaseCommand
operator|.
name|STATUS_CANCEL
case|:
name|status
operator|=
literal|"killed"
expr_stmt|;
break|break;
case|case
name|BaseCommand
operator|.
name|STATUS_NOT_FOUND
case|:
name|status
operator|=
literal|"not-found"
expr_stmt|;
break|break;
case|case
name|BaseCommand
operator|.
name|STATUS_NOT_ADMIN
case|:
name|status
operator|=
literal|"not-admin"
expr_stmt|;
break|break;
default|default:
name|status
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|exitValue
argument_list|)
expr_stmt|;
break|break;
block|}
name|event
operator|.
name|setProperty
argument_list|(
name|P_STATUS
argument_list|,
name|status
argument_list|)
expr_stmt|;
name|async
operator|.
name|append
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
DECL|method|onLogout ()
name|void
name|onLogout
parameter_list|()
block|{
name|async
operator|.
name|append
argument_list|(
name|log
argument_list|(
literal|"LOGOUT"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|log (final String msg)
specifier|private
name|LoggingEvent
name|log
parameter_list|(
specifier|final
name|String
name|msg
parameter_list|)
block|{
specifier|final
name|ServerSession
name|s
init|=
name|session
operator|.
name|get
argument_list|()
decl_stmt|;
specifier|final
name|IdentifiedUser
name|u
init|=
name|user
operator|.
name|get
argument_list|()
decl_stmt|;
specifier|final
name|LoggingEvent
name|event
init|=
operator|new
name|LoggingEvent
argument_list|(
comment|//
name|Logger
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
comment|// fqnOfCategoryClass
literal|null
argument_list|,
comment|// logger (optional)
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
comment|// when
name|Level
operator|.
name|INFO
argument_list|,
comment|// level
name|msg
argument_list|,
comment|// message text
literal|"SSHD"
argument_list|,
comment|// thread name
literal|null
argument_list|,
comment|// exception information
literal|null
argument_list|,
comment|// current NDC string
literal|null
argument_list|,
comment|// caller location
literal|null
comment|// MDC properties
argument_list|)
decl_stmt|;
name|event
operator|.
name|setProperty
argument_list|(
name|P_SESSION
argument_list|,
name|id
argument_list|(
name|s
operator|.
name|getAttribute
argument_list|(
name|SshUtil
operator|.
name|SESSION_ID
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|event
operator|.
name|setProperty
argument_list|(
name|P_USER_NAME
argument_list|,
name|u
operator|.
name|getAccount
argument_list|()
operator|.
name|getSshUserName
argument_list|()
argument_list|)
expr_stmt|;
name|event
operator|.
name|setProperty
argument_list|(
name|P_ACCOUNT_ID
argument_list|,
literal|"a/"
operator|+
name|u
operator|.
name|getAccountId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|event
return|;
block|}
DECL|method|format (final SocketAddress remote)
specifier|private
specifier|static
name|String
name|format
parameter_list|(
specifier|final
name|SocketAddress
name|remote
parameter_list|)
block|{
if|if
condition|(
name|remote
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
name|remote
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
name|in
operator|!=
literal|null
condition|)
block|{
return|return
name|in
operator|.
name|getHostAddress
argument_list|()
return|;
block|}
specifier|final
name|String
name|hostName
init|=
name|sa
operator|.
name|getHostName
argument_list|()
decl_stmt|;
if|if
condition|(
name|hostName
operator|!=
literal|null
condition|)
block|{
return|return
name|hostName
return|;
block|}
block|}
return|return
name|remote
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|id (final Integer id)
specifier|private
specifier|static
name|String
name|id
parameter_list|(
specifier|final
name|Integer
name|id
parameter_list|)
block|{
return|return
name|id
operator|!=
literal|null
condition|?
name|IdGenerator
operator|.
name|format
argument_list|(
name|id
argument_list|)
else|:
literal|""
return|;
block|}
DECL|method|resolve (final File logs_dir)
specifier|private
specifier|static
name|File
name|resolve
parameter_list|(
specifier|final
name|File
name|logs_dir
parameter_list|)
block|{
try|try
block|{
return|return
name|logs_dir
operator|.
name|getCanonicalFile
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
name|logs_dir
operator|.
name|getAbsoluteFile
argument_list|()
return|;
block|}
block|}
DECL|class|MyLayout
specifier|private
specifier|static
specifier|final
class|class
name|MyLayout
extends|extends
name|Layout
block|{
DECL|field|calendar
specifier|private
specifier|final
name|Calendar
name|calendar
decl_stmt|;
DECL|field|lastTimeMillis
specifier|private
name|long
name|lastTimeMillis
decl_stmt|;
DECL|field|lastTimeString
specifier|private
specifier|final
name|char
index|[]
name|lastTimeString
init|=
operator|new
name|char
index|[
literal|20
index|]
decl_stmt|;
DECL|field|timeZone
specifier|private
specifier|final
name|char
index|[]
name|timeZone
decl_stmt|;
DECL|method|MyLayout ()
name|MyLayout
parameter_list|()
block|{
specifier|final
name|TimeZone
name|tz
init|=
name|TimeZone
operator|.
name|getDefault
argument_list|()
decl_stmt|;
name|calendar
operator|=
name|Calendar
operator|.
name|getInstance
argument_list|(
name|tz
argument_list|)
expr_stmt|;
specifier|final
name|SimpleDateFormat
name|sdf
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"Z"
argument_list|)
decl_stmt|;
name|sdf
operator|.
name|setTimeZone
argument_list|(
name|tz
argument_list|)
expr_stmt|;
name|timeZone
operator|=
name|sdf
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|)
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|format (LoggingEvent event)
specifier|public
name|String
name|format
parameter_list|(
name|LoggingEvent
name|event
parameter_list|)
block|{
specifier|final
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|(
literal|128
argument_list|)
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
name|formatDate
argument_list|(
name|event
operator|.
name|getTimeStamp
argument_list|()
argument_list|,
name|buf
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|timeZone
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
name|req
argument_list|(
name|P_SESSION
argument_list|,
name|buf
argument_list|,
name|event
argument_list|)
expr_stmt|;
name|req
argument_list|(
name|P_USER_NAME
argument_list|,
name|buf
argument_list|,
name|event
argument_list|)
expr_stmt|;
name|req
argument_list|(
name|P_ACCOUNT_ID
argument_list|,
name|buf
argument_list|,
name|event
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|event
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|opt
argument_list|(
name|P_WAIT
argument_list|,
name|buf
argument_list|,
name|event
argument_list|)
expr_stmt|;
name|opt
argument_list|(
name|P_EXEC
argument_list|,
name|buf
argument_list|,
name|event
argument_list|)
expr_stmt|;
name|opt
argument_list|(
name|P_STATUS
argument_list|,
name|buf
argument_list|,
name|event
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|formatDate (final long now, final StringBuffer sbuf)
specifier|private
name|void
name|formatDate
parameter_list|(
specifier|final
name|long
name|now
parameter_list|,
specifier|final
name|StringBuffer
name|sbuf
parameter_list|)
block|{
specifier|final
name|int
name|millis
init|=
call|(
name|int
call|)
argument_list|(
name|now
operator|%
literal|1000
argument_list|)
decl_stmt|;
specifier|final
name|long
name|rounded
init|=
name|now
operator|-
name|millis
decl_stmt|;
if|if
condition|(
name|rounded
operator|!=
name|lastTimeMillis
condition|)
block|{
synchronized|synchronized
init|(
name|calendar
init|)
block|{
specifier|final
name|int
name|start
init|=
name|sbuf
operator|.
name|length
argument_list|()
decl_stmt|;
name|calendar
operator|.
name|setTimeInMillis
argument_list|(
name|rounded
argument_list|)
expr_stmt|;
name|sbuf
operator|.
name|append
argument_list|(
name|calendar
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|YEAR
argument_list|)
argument_list|)
expr_stmt|;
name|sbuf
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
specifier|final
name|int
name|month
init|=
name|calendar
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|MONTH
argument_list|)
operator|+
literal|1
decl_stmt|;
if|if
condition|(
name|month
operator|<
literal|10
condition|)
name|sbuf
operator|.
name|append
argument_list|(
literal|'0'
argument_list|)
expr_stmt|;
name|sbuf
operator|.
name|append
argument_list|(
name|month
argument_list|)
expr_stmt|;
name|sbuf
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
specifier|final
name|int
name|day
init|=
name|calendar
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|DAY_OF_MONTH
argument_list|)
decl_stmt|;
if|if
condition|(
name|day
operator|<
literal|10
condition|)
name|sbuf
operator|.
name|append
argument_list|(
literal|'0'
argument_list|)
expr_stmt|;
name|sbuf
operator|.
name|append
argument_list|(
name|day
argument_list|)
expr_stmt|;
name|sbuf
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
specifier|final
name|int
name|hour
init|=
name|calendar
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|HOUR_OF_DAY
argument_list|)
decl_stmt|;
if|if
condition|(
name|hour
operator|<
literal|10
condition|)
name|sbuf
operator|.
name|append
argument_list|(
literal|'0'
argument_list|)
expr_stmt|;
name|sbuf
operator|.
name|append
argument_list|(
name|hour
argument_list|)
expr_stmt|;
name|sbuf
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
specifier|final
name|int
name|mins
init|=
name|calendar
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|MINUTE
argument_list|)
decl_stmt|;
if|if
condition|(
name|mins
operator|<
literal|10
condition|)
name|sbuf
operator|.
name|append
argument_list|(
literal|'0'
argument_list|)
expr_stmt|;
name|sbuf
operator|.
name|append
argument_list|(
name|mins
argument_list|)
expr_stmt|;
name|sbuf
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
specifier|final
name|int
name|secs
init|=
name|calendar
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|SECOND
argument_list|)
decl_stmt|;
if|if
condition|(
name|secs
operator|<
literal|10
condition|)
name|sbuf
operator|.
name|append
argument_list|(
literal|'0'
argument_list|)
expr_stmt|;
name|sbuf
operator|.
name|append
argument_list|(
name|secs
argument_list|)
expr_stmt|;
name|sbuf
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|sbuf
operator|.
name|getChars
argument_list|(
name|start
argument_list|,
name|sbuf
operator|.
name|length
argument_list|()
argument_list|,
name|lastTimeString
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|lastTimeMillis
operator|=
name|rounded
expr_stmt|;
block|}
block|}
else|else
block|{
name|sbuf
operator|.
name|append
argument_list|(
name|lastTimeString
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|millis
operator|<
literal|100
condition|)
block|{
name|sbuf
operator|.
name|append
argument_list|(
literal|'0'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|millis
operator|<
literal|10
condition|)
block|{
name|sbuf
operator|.
name|append
argument_list|(
literal|'0'
argument_list|)
expr_stmt|;
block|}
name|sbuf
operator|.
name|append
argument_list|(
name|millis
argument_list|)
expr_stmt|;
block|}
DECL|method|req (String key, StringBuffer buf, LoggingEvent event)
specifier|private
name|void
name|req
parameter_list|(
name|String
name|key
parameter_list|,
name|StringBuffer
name|buf
parameter_list|,
name|LoggingEvent
name|event
parameter_list|)
block|{
name|Object
name|val
init|=
name|event
operator|.
name|getMDC
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
if|if
condition|(
name|val
operator|!=
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buf
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|opt (String key, StringBuffer buf, LoggingEvent event)
specifier|private
name|void
name|opt
parameter_list|(
name|String
name|key
parameter_list|,
name|StringBuffer
name|buf
parameter_list|,
name|LoggingEvent
name|event
parameter_list|)
block|{
name|Object
name|val
init|=
name|event
operator|.
name|getMDC
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|!=
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|ignoresThrowable ()
specifier|public
name|boolean
name|ignoresThrowable
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|activateOptions ()
specifier|public
name|void
name|activateOptions
parameter_list|()
block|{     }
block|}
DECL|class|DieErrorHandler
specifier|private
specifier|static
specifier|final
class|class
name|DieErrorHandler
implements|implements
name|ErrorHandler
block|{
annotation|@
name|Override
DECL|method|error (String message, Exception e, int errorCode, LoggingEvent event)
specifier|public
name|void
name|error
parameter_list|(
name|String
name|message
parameter_list|,
name|Exception
name|e
parameter_list|,
name|int
name|errorCode
parameter_list|,
name|LoggingEvent
name|event
parameter_list|)
block|{
name|error
argument_list|(
name|e
operator|!=
literal|null
condition|?
name|e
operator|.
name|getMessage
argument_list|()
else|:
name|message
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|error (String message, Exception e, int errorCode)
specifier|public
name|void
name|error
parameter_list|(
name|String
name|message
parameter_list|,
name|Exception
name|e
parameter_list|,
name|int
name|errorCode
parameter_list|)
block|{
name|error
argument_list|(
name|e
operator|!=
literal|null
condition|?
name|e
operator|.
name|getMessage
argument_list|()
else|:
name|message
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|error (String message)
specifier|public
name|void
name|error
parameter_list|(
name|String
name|message
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Cannot open log file: "
operator|+
name|message
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|activateOptions ()
specifier|public
name|void
name|activateOptions
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|setAppender (Appender appender)
specifier|public
name|void
name|setAppender
parameter_list|(
name|Appender
name|appender
parameter_list|)
block|{     }
annotation|@
name|Override
DECL|method|setBackupAppender (Appender appender)
specifier|public
name|void
name|setBackupAppender
parameter_list|(
name|Appender
name|appender
parameter_list|)
block|{     }
annotation|@
name|Override
DECL|method|setLogger (Logger logger)
specifier|public
name|void
name|setLogger
parameter_list|(
name|Logger
name|logger
parameter_list|)
block|{     }
block|}
DECL|class|LogLogHandler
specifier|private
specifier|static
specifier|final
class|class
name|LogLogHandler
implements|implements
name|ErrorHandler
block|{
annotation|@
name|Override
DECL|method|error (String message, Exception e, int errorCode, LoggingEvent event)
specifier|public
name|void
name|error
parameter_list|(
name|String
name|message
parameter_list|,
name|Exception
name|e
parameter_list|,
name|int
name|errorCode
parameter_list|,
name|LoggingEvent
name|event
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
name|message
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|error (String message, Exception e, int errorCode)
specifier|public
name|void
name|error
parameter_list|(
name|String
name|message
parameter_list|,
name|Exception
name|e
parameter_list|,
name|int
name|errorCode
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
name|message
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|error (String message)
specifier|public
name|void
name|error
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|activateOptions ()
specifier|public
name|void
name|activateOptions
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|setAppender (Appender appender)
specifier|public
name|void
name|setAppender
parameter_list|(
name|Appender
name|appender
parameter_list|)
block|{     }
annotation|@
name|Override
DECL|method|setBackupAppender (Appender appender)
specifier|public
name|void
name|setBackupAppender
parameter_list|(
name|Appender
name|appender
parameter_list|)
block|{     }
annotation|@
name|Override
DECL|method|setLogger (Logger logger)
specifier|public
name|void
name|setLogger
parameter_list|(
name|Logger
name|logger
parameter_list|)
block|{     }
block|}
block|}
end_class

end_unit

