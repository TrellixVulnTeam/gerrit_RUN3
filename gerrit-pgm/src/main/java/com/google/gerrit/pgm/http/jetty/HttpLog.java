begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2010 The Android Open Source Project
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
DECL|package|com.google.gerrit.pgm.http.jetty
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|pgm
operator|.
name|http
operator|.
name|jetty
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
name|servlet
operator|.
name|GuiceHelper
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
name|eclipse
operator|.
name|jetty
operator|.
name|server
operator|.
name|Request
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|server
operator|.
name|RequestLog
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|server
operator|.
name|Response
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|util
operator|.
name|component
operator|.
name|AbstractLifeCycle
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

begin_comment
comment|/** Writes the {@code httpd_log} file with per-request data. */
end_comment

begin_class
DECL|class|HttpLog
class|class
name|HttpLog
extends|extends
name|AbstractLifeCycle
implements|implements
name|RequestLog
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
name|HttpLog
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
literal|"httpd_log"
decl_stmt|;
DECL|field|P_HOST
specifier|private
specifier|static
specifier|final
name|String
name|P_HOST
init|=
literal|"Host"
decl_stmt|;
DECL|field|P_USER
specifier|private
specifier|static
specifier|final
name|String
name|P_USER
init|=
literal|"User"
decl_stmt|;
DECL|field|P_METHOD
specifier|private
specifier|static
specifier|final
name|String
name|P_METHOD
init|=
literal|"Method"
decl_stmt|;
DECL|field|P_RESOURCE
specifier|private
specifier|static
specifier|final
name|String
name|P_RESOURCE
init|=
literal|"Resource"
decl_stmt|;
DECL|field|P_PROTOCOL
specifier|private
specifier|static
specifier|final
name|String
name|P_PROTOCOL
init|=
literal|"Version"
decl_stmt|;
DECL|field|P_STATUS
specifier|private
specifier|static
specifier|final
name|String
name|P_STATUS
init|=
literal|"Status"
decl_stmt|;
DECL|field|P_CONTENT_LENGTH
specifier|private
specifier|static
specifier|final
name|String
name|P_CONTENT_LENGTH
init|=
literal|"Content-Length"
decl_stmt|;
DECL|field|P_REFERER
specifier|private
specifier|static
specifier|final
name|String
name|P_REFERER
init|=
literal|"Referer"
decl_stmt|;
DECL|field|P_USER_AGENT
specifier|private
specifier|static
specifier|final
name|String
name|P_USER_AGENT
init|=
literal|"User-Agent"
decl_stmt|;
DECL|field|async
specifier|private
specifier|final
name|AsyncAppender
name|async
decl_stmt|;
DECL|field|userProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|userProvider
decl_stmt|;
DECL|method|HttpLog (final SitePaths site, final Provider<CurrentUser> userProvider)
name|HttpLog
parameter_list|(
specifier|final
name|SitePaths
name|site
parameter_list|,
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|userProvider
parameter_list|)
block|{
name|this
operator|.
name|userProvider
operator|=
name|userProvider
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
DECL|method|doStart ()
specifier|protected
name|void
name|doStart
parameter_list|()
throws|throws
name|Exception
block|{   }
annotation|@
name|Override
DECL|method|doStop ()
specifier|protected
name|void
name|doStop
parameter_list|()
throws|throws
name|Exception
block|{
name|async
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|log (final Request req, final Response rsp)
specifier|public
name|void
name|log
parameter_list|(
specifier|final
name|Request
name|req
parameter_list|,
specifier|final
name|Response
name|rsp
parameter_list|)
block|{
name|GuiceHelper
operator|.
name|runInContext
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|doLog
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|doLog (Request req, Response rsp)
specifier|private
name|void
name|doLog
parameter_list|(
name|Request
name|req
parameter_list|,
name|Response
name|rsp
parameter_list|)
block|{
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
literal|""
argument_list|,
comment|// message text
literal|"HTTPD"
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
name|String
name|uri
init|=
name|req
operator|.
name|getRequestURI
argument_list|()
decl_stmt|;
name|String
name|qs
init|=
name|req
operator|.
name|getQueryString
argument_list|()
decl_stmt|;
if|if
condition|(
name|qs
operator|!=
literal|null
condition|)
block|{
name|uri
operator|=
name|uri
operator|+
literal|"?"
operator|+
name|qs
expr_stmt|;
block|}
name|CurrentUser
name|user
init|=
name|userProvider
operator|.
name|get
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
name|who
init|=
operator|(
name|IdentifiedUser
operator|)
name|user
decl_stmt|;
if|if
condition|(
name|who
operator|.
name|getUserName
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|who
operator|.
name|getUserName
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|event
operator|.
name|setProperty
argument_list|(
name|P_USER
argument_list|,
name|who
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|event
operator|.
name|setProperty
argument_list|(
name|P_USER
argument_list|,
literal|"a/"
operator|+
name|who
operator|.
name|getAccountId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|set
argument_list|(
name|event
argument_list|,
name|P_HOST
argument_list|,
name|req
operator|.
name|getRemoteAddr
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|event
argument_list|,
name|P_METHOD
argument_list|,
name|req
operator|.
name|getMethod
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|event
argument_list|,
name|P_RESOURCE
argument_list|,
name|uri
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|event
argument_list|,
name|P_PROTOCOL
argument_list|,
name|req
operator|.
name|getProtocol
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|event
argument_list|,
name|P_STATUS
argument_list|,
name|rsp
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|event
argument_list|,
name|P_CONTENT_LENGTH
argument_list|,
name|rsp
operator|.
name|getContentCount
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|event
argument_list|,
name|P_REFERER
argument_list|,
name|req
operator|.
name|getHeader
argument_list|(
literal|"Referer"
argument_list|)
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|event
argument_list|,
name|P_USER_AGENT
argument_list|,
name|req
operator|.
name|getHeader
argument_list|(
literal|"User-Agent"
argument_list|)
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
DECL|method|set (LoggingEvent event, String key, String val)
specifier|private
specifier|static
name|void
name|set
parameter_list|(
name|LoggingEvent
name|event
parameter_list|,
name|String
name|key
parameter_list|,
name|String
name|val
parameter_list|)
block|{
if|if
condition|(
name|val
operator|!=
literal|null
operator|&&
operator|!
name|val
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|event
operator|.
name|setProperty
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|set (LoggingEvent event, String key, long val)
specifier|private
specifier|static
name|void
name|set
parameter_list|(
name|LoggingEvent
name|event
parameter_list|,
name|String
name|key
parameter_list|,
name|long
name|val
parameter_list|)
block|{
if|if
condition|(
literal|0
operator|<
name|val
condition|)
block|{
name|event
operator|.
name|setProperty
argument_list|(
name|key
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|val
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
DECL|field|dateFormat
specifier|private
specifier|final
name|SimpleDateFormat
name|dateFormat
decl_stmt|;
DECL|field|lastTimeMillis
specifier|private
name|long
name|lastTimeMillis
decl_stmt|;
DECL|field|lastTimeString
specifier|private
name|String
name|lastTimeString
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
name|dateFormat
operator|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"dd/MMM/yyyy:HH:mm:ss Z"
argument_list|)
expr_stmt|;
name|dateFormat
operator|.
name|setTimeZone
argument_list|(
name|tz
argument_list|)
expr_stmt|;
name|lastTimeMillis
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|lastTimeString
operator|=
name|dateFormat
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|(
name|lastTimeMillis
argument_list|)
argument_list|)
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
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|(
literal|128
argument_list|)
decl_stmt|;
name|opt
argument_list|(
name|buf
argument_list|,
name|event
argument_list|,
name|P_HOST
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
literal|'-'
argument_list|)
expr_stmt|;
comment|// identd on client system (never requested)
name|buf
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|opt
argument_list|(
name|buf
argument_list|,
name|event
argument_list|,
name|P_USER
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
literal|']'
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
literal|'"'
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|event
operator|.
name|getMDC
argument_list|(
name|P_METHOD
argument_list|)
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
name|getMDC
argument_list|(
name|P_RESOURCE
argument_list|)
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
name|getMDC
argument_list|(
name|P_PROTOCOL
argument_list|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|'"'
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
name|getMDC
argument_list|(
name|P_STATUS
argument_list|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|opt
argument_list|(
name|buf
argument_list|,
name|event
argument_list|,
name|P_CONTENT_LENGTH
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|dq_opt
argument_list|(
name|buf
argument_list|,
name|event
argument_list|,
name|P_REFERER
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|dq_opt
argument_list|(
name|buf
argument_list|,
name|event
argument_list|,
name|P_USER_AGENT
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
DECL|method|opt (StringBuilder buf, LoggingEvent event, String key)
specifier|private
name|void
name|opt
parameter_list|(
name|StringBuilder
name|buf
parameter_list|,
name|LoggingEvent
name|event
parameter_list|,
name|String
name|key
parameter_list|)
block|{
name|String
name|val
init|=
operator|(
name|String
operator|)
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
operator|==
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buf
operator|.
name|append
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|dq_opt (StringBuilder buf, LoggingEvent event, String key)
specifier|private
name|void
name|dq_opt
parameter_list|(
name|StringBuilder
name|buf
parameter_list|,
name|LoggingEvent
name|event
parameter_list|,
name|String
name|key
parameter_list|)
block|{
name|String
name|val
init|=
operator|(
name|String
operator|)
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
operator|==
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buf
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|val
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|formatDate (final long now, final StringBuilder sbuf)
specifier|private
name|void
name|formatDate
parameter_list|(
specifier|final
name|long
name|now
parameter_list|,
specifier|final
name|StringBuilder
name|sbuf
parameter_list|)
block|{
specifier|final
name|long
name|rounded
init|=
name|now
operator|-
call|(
name|int
call|)
argument_list|(
name|now
operator|%
literal|1000
argument_list|)
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
name|dateFormat
init|)
block|{
name|lastTimeMillis
operator|=
name|rounded
expr_stmt|;
name|lastTimeString
operator|=
name|dateFormat
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|(
name|lastTimeMillis
argument_list|)
argument_list|)
expr_stmt|;
name|sbuf
operator|.
name|append
argument_list|(
name|lastTimeString
argument_list|)
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

