begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2013 The Android Open Source Project
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
name|spi
operator|.
name|LoggingEvent
import|;
end_import

begin_class
DECL|class|HttpLogLayout
specifier|public
specifier|final
class|class
name|HttpLogLayout
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
DECL|method|HttpLogLayout ()
specifier|public
name|HttpLogLayout
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
name|HttpLog
operator|.
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
literal|'['
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|event
operator|.
name|getThreadName
argument_list|()
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
name|HttpLog
operator|.
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
name|HttpLog
operator|.
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
name|HttpLog
operator|.
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
name|HttpLog
operator|.
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
name|HttpLog
operator|.
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
name|HttpLog
operator|.
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
name|opt
argument_list|(
name|buf
argument_list|,
name|event
argument_list|,
name|HttpLog
operator|.
name|P_LATENCY
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
name|HttpLog
operator|.
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
name|HttpLog
operator|.
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
DECL|method|formatDate (long now, StringBuilder sbuf)
specifier|private
name|void
name|formatDate
parameter_list|(
name|long
name|now
parameter_list|,
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
block|{}
block|}
end_class

end_unit

