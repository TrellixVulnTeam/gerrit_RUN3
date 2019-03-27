begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2018 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.logging
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|logging
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
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
name|base
operator|.
name|Stopwatch
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
name|base
operator|.
name|Strings
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
name|collect
operator|.
name|HashBasedTable
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
name|collect
operator|.
name|Table
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
name|Nullable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Consumer
import|;
end_import

begin_comment
comment|/**  * TraceContext that allows to set logging tags and enforce logging.  *  *<p>The logging tags are attached to all log entries that are triggered while the trace context is  * open. If force logging is enabled all logs that are triggered while the trace context is open are  * written to the log file regardless of the configured log level.  *  *<pre>  * try (TraceContext traceContext = TraceContext.open()  *         .addTag("tag-name", "tag-value")  *         .forceLogging()) {  *     // This gets logged as: A log [CONTEXT forced=true tag-name="tag-value" ]  *     // Since force logging is enabled this gets logged independently of the configured log  *     // level.  *     logger.atFinest().log("A log");  *  *     // do stuff  * }  *</pre>  *  *<p>The logging tags and the force logging flag are stored in the {@link LoggingContext}. {@link  * LoggingContextAwareExecutorService}, {@link LoggingContextAwareScheduledExecutorService} and the  * executor in {@link com.google.gerrit.server.git.WorkQueue} ensure that the logging context is  * automatically copied to background threads.  *  *<p>On close of the trace context newly set tags are unset. Force logging is disabled on close if  * it got enabled while the trace context was open.  *  *<p>Trace contexts can be nested:  *  *<pre>  * // Initially there are no tags  * logger.atSevere().log("log without tag");  *  * // a tag can be set by opening a trace context  * try (TraceContext ctx = TraceContext.open().addTag("tag1", "value1")) {  *   logger.atSevere().log("log with tag1=value1");  *  *   // while a trace context is open further tags can be added.  *   ctx.addTag("tag2", "value2")  *   logger.atSevere().log("log with tag1=value1 and tag2=value2");  *  *   // also by opening another trace context a another tag can be added  *   try (TraceContext ctx2 = TraceContext.open().addTag("tag3", "value3")) {  *     logger.atSevere().log("log with tag1=value1, tag2=value2 and tag3=value3");  *  *     // it's possible to have the same tag name with multiple values  *     ctx2.addTag("tag3", "value3a")  *     logger.atSevere().log("log with tag1=value1, tag2=value2, tag3=value3 and tag3=value3a");  *  *     // adding a tag with the same name and value as an existing tag has no effect  *     try (TraceContext ctx3 = TraceContext.open().addTag("tag3", "value3a")) {  *       logger.atSevere().log("log with tag1=value1, tag2=value2, tag3=value3 and tag3=value3a");  *     }  *  *     // closing ctx3 didn't remove tag3=value3a since it was already set before opening ctx3  *     logger.atSevere().log("log with tag1=value1, tag2=value2, tag3=value3 and tag3=value3a");  *   }  *  *   // closing ctx2 removed tag3=value3 and tag3-value3a  *   logger.atSevere().log("with tag1=value1 and tag2=value2");  * }  *  * // closing ctx1 removed tag1=value1 and tag2=value2  * logger.atSevere().log("log without tag");  *</pre>  */
end_comment

begin_class
DECL|class|TraceContext
specifier|public
class|class
name|TraceContext
implements|implements
name|AutoCloseable
block|{
DECL|field|PLUGIN_TAG
specifier|private
specifier|static
specifier|final
name|String
name|PLUGIN_TAG
init|=
literal|"PLUGIN"
decl_stmt|;
DECL|method|open ()
specifier|public
specifier|static
name|TraceContext
name|open
parameter_list|()
block|{
return|return
operator|new
name|TraceContext
argument_list|()
return|;
block|}
comment|/**    * Opens a new trace context for request tracing.    *    *<ul>    *<li>sets a tag with a trace ID    *<li>enables force logging    *</ul>    *    *<p>if no trace ID is provided a new trace ID is only generated if request tracing was not    * started yet. If request tracing was already started the given {@code traceIdConsumer} is    * invoked with the existing trace ID and no new logging tag is set.    *    *<p>No-op if {@code trace} is {@code false}.    *    * @param trace whether tracing should be started    * @param traceId trace ID that should be used for tracing, if {@code null} a trace ID is    *     generated    * @param traceIdConsumer consumer for the trace ID, should be used to return the generated trace    *     ID to the client, not invoked if {@code trace} is {@code false}    * @return the trace context    */
DECL|method|newTrace ( boolean trace, @Nullable String traceId, TraceIdConsumer traceIdConsumer)
specifier|public
specifier|static
name|TraceContext
name|newTrace
parameter_list|(
name|boolean
name|trace
parameter_list|,
annotation|@
name|Nullable
name|String
name|traceId
parameter_list|,
name|TraceIdConsumer
name|traceIdConsumer
parameter_list|)
block|{
if|if
condition|(
operator|!
name|trace
condition|)
block|{
comment|// Create an empty trace context.
return|return
name|open
argument_list|()
return|;
block|}
if|if
condition|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|traceId
argument_list|)
condition|)
block|{
name|traceIdConsumer
operator|.
name|accept
argument_list|(
name|RequestId
operator|.
name|Type
operator|.
name|TRACE_ID
operator|.
name|name
argument_list|()
argument_list|,
name|traceId
argument_list|)
expr_stmt|;
return|return
name|open
argument_list|()
operator|.
name|addTag
argument_list|(
name|RequestId
operator|.
name|Type
operator|.
name|TRACE_ID
argument_list|,
name|traceId
argument_list|)
operator|.
name|forceLogging
argument_list|()
return|;
block|}
name|Optional
argument_list|<
name|String
argument_list|>
name|existingTraceId
init|=
name|LoggingContext
operator|.
name|getInstance
argument_list|()
operator|.
name|getTagsAsMap
argument_list|()
operator|.
name|get
argument_list|(
name|RequestId
operator|.
name|Type
operator|.
name|TRACE_ID
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|stream
argument_list|()
operator|.
name|findAny
argument_list|()
decl_stmt|;
if|if
condition|(
name|existingTraceId
operator|.
name|isPresent
argument_list|()
condition|)
block|{
comment|// request tracing was already started, no need to generate a new trace ID
name|traceIdConsumer
operator|.
name|accept
argument_list|(
name|RequestId
operator|.
name|Type
operator|.
name|TRACE_ID
operator|.
name|name
argument_list|()
argument_list|,
name|existingTraceId
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|open
argument_list|()
return|;
block|}
name|RequestId
name|newTraceId
init|=
operator|new
name|RequestId
argument_list|()
decl_stmt|;
name|traceIdConsumer
operator|.
name|accept
argument_list|(
name|RequestId
operator|.
name|Type
operator|.
name|TRACE_ID
operator|.
name|name
argument_list|()
argument_list|,
name|newTraceId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|open
argument_list|()
operator|.
name|addTag
argument_list|(
name|RequestId
operator|.
name|Type
operator|.
name|TRACE_ID
argument_list|,
name|newTraceId
argument_list|)
operator|.
name|forceLogging
argument_list|()
return|;
block|}
annotation|@
name|FunctionalInterface
DECL|interface|TraceIdConsumer
specifier|public
interface|interface
name|TraceIdConsumer
block|{
DECL|method|accept (String tagName, String traceId)
name|void
name|accept
parameter_list|(
name|String
name|tagName
parameter_list|,
name|String
name|traceId
parameter_list|)
function_decl|;
block|}
comment|/**    * Opens a new timer that logs the time for an operation if request tracing is enabled.    *    *<p>If request tracing is not enabled this is a no-op.    *    * @param message the message    * @return the trace timer    */
DECL|method|newTimer (String message)
specifier|public
specifier|static
name|TraceTimer
name|newTimer
parameter_list|(
name|String
name|message
parameter_list|)
block|{
return|return
operator|new
name|TraceTimer
argument_list|(
name|message
argument_list|)
return|;
block|}
comment|/**    * Opens a new timer that logs the time for an operation if request tracing is enabled.    *    *<p>If request tracing is not enabled this is a no-op.    *    * @param format the message format string    * @param arg argument for the message    * @return the trace timer    */
DECL|method|newTimer (String format, Object arg)
specifier|public
specifier|static
name|TraceTimer
name|newTimer
parameter_list|(
name|String
name|format
parameter_list|,
name|Object
name|arg
parameter_list|)
block|{
return|return
operator|new
name|TraceTimer
argument_list|(
name|format
argument_list|,
name|arg
argument_list|)
return|;
block|}
comment|/**    * Opens a new timer that logs the time for an operation if request tracing is enabled.    *    *<p>If request tracing is not enabled this is a no-op.    *    * @param format the message format string    * @param arg1 first argument for the message    * @param arg2 second argument for the message    * @return the trace timer    */
DECL|method|newTimer (String format, Object arg1, Object arg2)
specifier|public
specifier|static
name|TraceTimer
name|newTimer
parameter_list|(
name|String
name|format
parameter_list|,
name|Object
name|arg1
parameter_list|,
name|Object
name|arg2
parameter_list|)
block|{
return|return
operator|new
name|TraceTimer
argument_list|(
name|format
argument_list|,
name|arg1
argument_list|,
name|arg2
argument_list|)
return|;
block|}
comment|/**    * Opens a new timer that logs the time for an operation if request tracing is enabled.    *    *<p>If request tracing is not enabled this is a no-op.    *    * @param format the message format string    * @param arg1 first argument for the message    * @param arg2 second argument for the message    * @param arg3 third argument for the message    * @return the trace timer    */
DECL|method|newTimer (String format, Object arg1, Object arg2, Object arg3)
specifier|public
specifier|static
name|TraceTimer
name|newTimer
parameter_list|(
name|String
name|format
parameter_list|,
name|Object
name|arg1
parameter_list|,
name|Object
name|arg2
parameter_list|,
name|Object
name|arg3
parameter_list|)
block|{
return|return
operator|new
name|TraceTimer
argument_list|(
name|format
argument_list|,
name|arg1
argument_list|,
name|arg2
argument_list|,
name|arg3
argument_list|)
return|;
block|}
comment|/**    * Opens a new timer that logs the time for an operation if request tracing is enabled.    *    *<p>If request tracing is not enabled this is a no-op.    *    * @param format the message format string    * @param arg1 first argument for the message    * @param arg2 second argument for the message    * @param arg3 third argument for the message    * @param arg4 fourth argument for the message    * @return the trace timer    */
DECL|method|newTimer ( String format, Object arg1, Object arg2, Object arg3, Object arg4)
specifier|public
specifier|static
name|TraceTimer
name|newTimer
parameter_list|(
name|String
name|format
parameter_list|,
name|Object
name|arg1
parameter_list|,
name|Object
name|arg2
parameter_list|,
name|Object
name|arg3
parameter_list|,
name|Object
name|arg4
parameter_list|)
block|{
return|return
operator|new
name|TraceTimer
argument_list|(
name|format
argument_list|,
name|arg1
argument_list|,
name|arg2
argument_list|,
name|arg3
argument_list|,
name|arg4
argument_list|)
return|;
block|}
DECL|class|TraceTimer
specifier|public
specifier|static
class|class
name|TraceTimer
implements|implements
name|AutoCloseable
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
DECL|field|logFn
specifier|private
specifier|final
name|Consumer
argument_list|<
name|Long
argument_list|>
name|logFn
decl_stmt|;
DECL|field|stopwatch
specifier|private
specifier|final
name|Stopwatch
name|stopwatch
decl_stmt|;
DECL|method|TraceTimer (String message)
specifier|private
name|TraceTimer
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|this
argument_list|(
name|elapsedMs
lambda|->
name|logger
operator|.
name|atFine
argument_list|()
operator|.
name|log
argument_list|(
name|message
operator|+
literal|" (%d ms)"
argument_list|,
name|elapsedMs
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|TraceTimer (String format, @Nullable Object arg)
specifier|private
name|TraceTimer
parameter_list|(
name|String
name|format
parameter_list|,
annotation|@
name|Nullable
name|Object
name|arg
parameter_list|)
block|{
name|this
argument_list|(
name|elapsedMs
lambda|->
name|logger
operator|.
name|atFine
argument_list|()
operator|.
name|log
argument_list|(
name|format
operator|+
literal|" (%d ms)"
argument_list|,
name|arg
argument_list|,
name|elapsedMs
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|TraceTimer (String format, @Nullable Object arg1, @Nullable Object arg2)
specifier|private
name|TraceTimer
parameter_list|(
name|String
name|format
parameter_list|,
annotation|@
name|Nullable
name|Object
name|arg1
parameter_list|,
annotation|@
name|Nullable
name|Object
name|arg2
parameter_list|)
block|{
name|this
argument_list|(
name|elapsedMs
lambda|->
name|logger
operator|.
name|atFine
argument_list|()
operator|.
name|log
argument_list|(
name|format
operator|+
literal|" (%d ms)"
argument_list|,
name|arg1
argument_list|,
name|arg2
argument_list|,
name|elapsedMs
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|TraceTimer ( String format, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3)
specifier|private
name|TraceTimer
parameter_list|(
name|String
name|format
parameter_list|,
annotation|@
name|Nullable
name|Object
name|arg1
parameter_list|,
annotation|@
name|Nullable
name|Object
name|arg2
parameter_list|,
annotation|@
name|Nullable
name|Object
name|arg3
parameter_list|)
block|{
name|this
argument_list|(
name|elapsedMs
lambda|->
name|logger
operator|.
name|atFine
argument_list|()
operator|.
name|log
argument_list|(
name|format
operator|+
literal|" (%d ms)"
argument_list|,
name|arg1
argument_list|,
name|arg2
argument_list|,
name|arg3
argument_list|,
name|elapsedMs
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|TraceTimer ( String format, @Nullable Object arg1, @Nullable Object arg2, @Nullable Object arg3, @Nullable Object arg4)
specifier|private
name|TraceTimer
parameter_list|(
name|String
name|format
parameter_list|,
annotation|@
name|Nullable
name|Object
name|arg1
parameter_list|,
annotation|@
name|Nullable
name|Object
name|arg2
parameter_list|,
annotation|@
name|Nullable
name|Object
name|arg3
parameter_list|,
annotation|@
name|Nullable
name|Object
name|arg4
parameter_list|)
block|{
name|this
argument_list|(
name|elapsedMs
lambda|->
name|logger
operator|.
name|atFine
argument_list|()
operator|.
name|log
argument_list|(
name|format
operator|+
literal|" (%d ms)"
argument_list|,
name|arg1
argument_list|,
name|arg2
argument_list|,
name|arg3
argument_list|,
name|arg4
argument_list|,
name|elapsedMs
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|TraceTimer (Consumer<Long> logFn)
specifier|private
name|TraceTimer
parameter_list|(
name|Consumer
argument_list|<
name|Long
argument_list|>
name|logFn
parameter_list|)
block|{
name|this
operator|.
name|logFn
operator|=
name|logFn
expr_stmt|;
name|this
operator|.
name|stopwatch
operator|=
name|Stopwatch
operator|.
name|createStarted
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
name|stopwatch
operator|.
name|stop
argument_list|()
expr_stmt|;
name|logFn
operator|.
name|accept
argument_list|(
name|stopwatch
operator|.
name|elapsed
argument_list|(
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Table<TAG_NAME, TAG_VALUE, REMOVE_ON_CLOSE>
DECL|field|tags
specifier|private
specifier|final
name|Table
argument_list|<
name|String
argument_list|,
name|String
argument_list|,
name|Boolean
argument_list|>
name|tags
init|=
name|HashBasedTable
operator|.
name|create
argument_list|()
decl_stmt|;
DECL|field|stopForceLoggingOnClose
specifier|private
name|boolean
name|stopForceLoggingOnClose
decl_stmt|;
DECL|method|TraceContext ()
specifier|private
name|TraceContext
parameter_list|()
block|{}
DECL|method|addTag (RequestId.Type requestId, Object tagValue)
specifier|public
name|TraceContext
name|addTag
parameter_list|(
name|RequestId
operator|.
name|Type
name|requestId
parameter_list|,
name|Object
name|tagValue
parameter_list|)
block|{
return|return
name|addTag
argument_list|(
name|requireNonNull
argument_list|(
name|requestId
argument_list|,
literal|"request ID is required"
argument_list|)
operator|.
name|name
argument_list|()
argument_list|,
name|tagValue
argument_list|)
return|;
block|}
DECL|method|addTag (String tagName, Object tagValue)
specifier|public
name|TraceContext
name|addTag
parameter_list|(
name|String
name|tagName
parameter_list|,
name|Object
name|tagValue
parameter_list|)
block|{
name|String
name|name
init|=
name|requireNonNull
argument_list|(
name|tagName
argument_list|,
literal|"tag name is required"
argument_list|)
decl_stmt|;
name|String
name|value
init|=
name|requireNonNull
argument_list|(
name|tagValue
argument_list|,
literal|"tag value is required"
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|tags
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|LoggingContext
operator|.
name|getInstance
argument_list|()
operator|.
name|addTag
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|addPluginTag (String pluginName)
specifier|public
name|TraceContext
name|addPluginTag
parameter_list|(
name|String
name|pluginName
parameter_list|)
block|{
return|return
name|addTag
argument_list|(
name|PLUGIN_TAG
argument_list|,
name|pluginName
argument_list|)
return|;
block|}
DECL|method|forceLogging ()
specifier|public
name|TraceContext
name|forceLogging
parameter_list|()
block|{
if|if
condition|(
name|stopForceLoggingOnClose
condition|)
block|{
return|return
name|this
return|;
block|}
name|stopForceLoggingOnClose
operator|=
operator|!
name|LoggingContext
operator|.
name|getInstance
argument_list|()
operator|.
name|forceLogging
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
for|for
control|(
name|Table
operator|.
name|Cell
argument_list|<
name|String
argument_list|,
name|String
argument_list|,
name|Boolean
argument_list|>
name|cell
range|:
name|tags
operator|.
name|cellSet
argument_list|()
control|)
block|{
if|if
condition|(
name|cell
operator|.
name|getValue
argument_list|()
condition|)
block|{
name|LoggingContext
operator|.
name|getInstance
argument_list|()
operator|.
name|removeTag
argument_list|(
name|cell
operator|.
name|getRowKey
argument_list|()
argument_list|,
name|cell
operator|.
name|getColumnKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|stopForceLoggingOnClose
condition|)
block|{
name|LoggingContext
operator|.
name|getInstance
argument_list|()
operator|.
name|forceLogging
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

