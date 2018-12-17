begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2012 The Android Open Source Project
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
name|extensions
operator|.
name|registration
operator|.
name|DynamicSet
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
name|AccessPath
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
name|RequestInfo
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
name|RequestListener
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
name|GerritServerConfig
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
name|logging
operator|.
name|PerformanceLogContext
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
name|logging
operator|.
name|PerformanceLogger
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
name|logging
operator|.
name|TraceContext
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
name|plugincontext
operator|.
name|PluginSetContext
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
name|io
operator|.
name|PrintWriter
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
name|Environment
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
name|channel
operator|.
name|ChannelSession
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
name|lib
operator|.
name|Config
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

begin_class
DECL|class|SshCommand
specifier|public
specifier|abstract
class|class
name|SshCommand
extends|extends
name|BaseCommand
block|{
DECL|field|performanceLoggers
annotation|@
name|Inject
specifier|private
name|DynamicSet
argument_list|<
name|PerformanceLogger
argument_list|>
name|performanceLoggers
decl_stmt|;
DECL|field|requestListeners
annotation|@
name|Inject
specifier|private
name|PluginSetContext
argument_list|<
name|RequestListener
argument_list|>
name|requestListeners
decl_stmt|;
DECL|field|config
annotation|@
name|Inject
annotation|@
name|GerritServerConfig
specifier|private
name|Config
name|config
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--trace"
argument_list|,
name|usage
operator|=
literal|"enable request tracing"
argument_list|)
DECL|field|trace
specifier|private
name|boolean
name|trace
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--trace-id"
argument_list|,
name|usage
operator|=
literal|"trace ID (can only be set if --trace was set too)"
argument_list|)
DECL|field|traceId
specifier|private
name|String
name|traceId
decl_stmt|;
DECL|field|stdout
specifier|protected
name|PrintWriter
name|stdout
decl_stmt|;
DECL|field|stderr
specifier|protected
name|PrintWriter
name|stderr
decl_stmt|;
annotation|@
name|Override
DECL|method|start (ChannelSession channel, Environment env)
specifier|public
name|void
name|start
parameter_list|(
name|ChannelSession
name|channel
parameter_list|,
name|Environment
name|env
parameter_list|)
throws|throws
name|IOException
block|{
name|startThread
argument_list|(
parameter_list|()
lambda|->
block|{
name|parseCommandLine
argument_list|()
expr_stmt|;
name|stdout
operator|=
name|toPrintWriter
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|stderr
operator|=
name|toPrintWriter
argument_list|(
name|err
argument_list|)
expr_stmt|;
try|try
init|(
name|TraceContext
name|traceContext
init|=
name|enableTracing
argument_list|()
init|;
name|PerformanceLogContext
name|performanceLogContext
operator|=
operator|new
name|PerformanceLogContext
argument_list|(
name|config
argument_list|,
name|performanceLoggers
argument_list|)
init|)
block|{
name|RequestInfo
name|requestInfo
init|=
name|RequestInfo
operator|.
name|builder
argument_list|(
name|RequestInfo
operator|.
name|RequestType
operator|.
name|SSH
argument_list|,
name|user
argument_list|,
name|traceContext
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|requestListeners
operator|.
name|runEach
argument_list|(
name|l
lambda|->
name|l
operator|.
name|onRequest
argument_list|(
name|requestInfo
argument_list|)
argument_list|)
expr_stmt|;
name|SshCommand
operator|.
name|this
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|stdout
operator|.
name|flush
argument_list|()
expr_stmt|;
name|stderr
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|,
name|AccessPath
operator|.
name|SSH_COMMAND
argument_list|)
expr_stmt|;
block|}
DECL|method|run ()
specifier|protected
specifier|abstract
name|void
name|run
parameter_list|()
throws|throws
name|UnloggedFailure
throws|,
name|Failure
throws|,
name|Exception
function_decl|;
DECL|method|enableTracing ()
specifier|private
name|TraceContext
name|enableTracing
parameter_list|()
throws|throws
name|UnloggedFailure
block|{
if|if
condition|(
operator|!
name|trace
operator|&&
name|traceId
operator|!=
literal|null
condition|)
block|{
throw|throw
name|die
argument_list|(
literal|"A trace ID can only be set if --trace was specified."
argument_list|)
throw|;
block|}
return|return
name|TraceContext
operator|.
name|newTrace
argument_list|(
name|trace
argument_list|,
name|traceId
argument_list|,
parameter_list|(
name|tagName
parameter_list|,
name|traceId
parameter_list|)
lambda|->
name|stderr
operator|.
name|println
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%s: %s"
argument_list|,
name|tagName
argument_list|,
name|traceId
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

