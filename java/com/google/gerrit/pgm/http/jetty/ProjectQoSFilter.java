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
import|import static
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
name|ConfigUtil
operator|.
name|getTimeUnit
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|MINUTES
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
operator|.
name|SC_SERVICE_UNAVAILABLE
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
name|account
operator|.
name|AccountLimits
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
name|git
operator|.
name|QueueProvider
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
name|git
operator|.
name|WorkQueue
operator|.
name|CancelableRunnable
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
name|CommandExecutorQueueProvider
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|servlet
operator|.
name|ServletModule
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
name|Future
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
name|ScheduledThreadPoolExecutor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|Filter
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|FilterChain
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|FilterConfig
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletResponse
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
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
name|continuation
operator|.
name|Continuation
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
name|continuation
operator|.
name|ContinuationListener
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
name|continuation
operator|.
name|ContinuationSupport
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

begin_comment
comment|/**  * Use Jetty continuations to defer execution until threads are available.  *  *<p>We actually schedule a task into the same execution queue as the SSH daemon uses for command  * execution, and then park the web request in a continuation until an execution thread is  * available. This ensures that the overall JVM process doesn't exceed the configured limit on  * concurrent Git requests.  *  *<p>During Git request execution however we have to use the Jetty service thread, not the thread  * from the SSH execution queue. Trying to complete the request on the SSH execution queue caused  * Jetty's HTTP parser to crash, so we instead block the SSH execution queue thread and ask Jetty to  * resume processing on the web service thread.  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|Singleton
DECL|class|ProjectQoSFilter
specifier|public
class|class
name|ProjectQoSFilter
implements|implements
name|Filter
block|{
DECL|field|ATT_SPACE
specifier|private
specifier|static
specifier|final
name|String
name|ATT_SPACE
init|=
name|ProjectQoSFilter
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
DECL|field|TASK
specifier|private
specifier|static
specifier|final
name|String
name|TASK
init|=
name|ATT_SPACE
operator|+
literal|"/TASK"
decl_stmt|;
DECL|field|CANCEL
specifier|private
specifier|static
specifier|final
name|String
name|CANCEL
init|=
name|ATT_SPACE
operator|+
literal|"/CANCEL"
decl_stmt|;
DECL|field|FILTER_RE
specifier|private
specifier|static
specifier|final
name|String
name|FILTER_RE
init|=
literal|"^/(.*)/(git-upload-pack|git-receive-pack)$"
decl_stmt|;
DECL|field|URI_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|URI_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|FILTER_RE
argument_list|)
decl_stmt|;
DECL|class|Module
specifier|public
specifier|static
class|class
name|Module
extends|extends
name|ServletModule
block|{
annotation|@
name|Override
DECL|method|configureServlets ()
specifier|protected
name|void
name|configureServlets
parameter_list|()
block|{
name|bind
argument_list|(
name|QueueProvider
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|CommandExecutorQueueProvider
operator|.
name|class
argument_list|)
expr_stmt|;
name|filterRegex
argument_list|(
name|FILTER_RE
argument_list|)
operator|.
name|through
argument_list|(
name|ProjectQoSFilter
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|limitsFactory
specifier|private
specifier|final
name|AccountLimits
operator|.
name|Factory
name|limitsFactory
decl_stmt|;
DECL|field|user
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|user
decl_stmt|;
DECL|field|queue
specifier|private
specifier|final
name|QueueProvider
name|queue
decl_stmt|;
DECL|field|context
specifier|private
specifier|final
name|ServletContext
name|context
decl_stmt|;
DECL|field|maxWait
specifier|private
specifier|final
name|long
name|maxWait
decl_stmt|;
annotation|@
name|Inject
DECL|method|ProjectQoSFilter ( AccountLimits.Factory limitsFactory, Provider<CurrentUser> user, QueueProvider queue, ServletContext context, @GerritServerConfig Config cfg)
name|ProjectQoSFilter
parameter_list|(
name|AccountLimits
operator|.
name|Factory
name|limitsFactory
parameter_list|,
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|user
parameter_list|,
name|QueueProvider
name|queue
parameter_list|,
name|ServletContext
name|context
parameter_list|,
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|)
block|{
name|this
operator|.
name|limitsFactory
operator|=
name|limitsFactory
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|queue
operator|=
name|queue
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|maxWait
operator|=
name|MINUTES
operator|.
name|toMillis
argument_list|(
name|getTimeUnit
argument_list|(
name|cfg
argument_list|,
literal|"httpd"
argument_list|,
literal|null
argument_list|,
literal|"maxwait"
argument_list|,
literal|5
argument_list|,
name|MINUTES
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doFilter (ServletRequest request, ServletResponse response, FilterChain chain)
specifier|public
name|void
name|doFilter
parameter_list|(
name|ServletRequest
name|request
parameter_list|,
name|ServletResponse
name|response
parameter_list|,
name|FilterChain
name|chain
parameter_list|)
throws|throws
name|IOException
throws|,
name|ServletException
block|{
specifier|final
name|HttpServletRequest
name|req
init|=
operator|(
name|HttpServletRequest
operator|)
name|request
decl_stmt|;
specifier|final
name|HttpServletResponse
name|rsp
init|=
operator|(
name|HttpServletResponse
operator|)
name|response
decl_stmt|;
specifier|final
name|Continuation
name|cont
init|=
name|ContinuationSupport
operator|.
name|getContinuation
argument_list|(
name|req
argument_list|)
decl_stmt|;
if|if
condition|(
name|cont
operator|.
name|isInitial
argument_list|()
condition|)
block|{
name|TaskThunk
name|task
init|=
operator|new
name|TaskThunk
argument_list|(
name|cont
argument_list|,
name|req
argument_list|)
decl_stmt|;
if|if
condition|(
name|maxWait
operator|>
literal|0
condition|)
block|{
name|cont
operator|.
name|setTimeout
argument_list|(
name|maxWait
argument_list|)
expr_stmt|;
block|}
name|cont
operator|.
name|suspend
argument_list|(
name|rsp
argument_list|)
expr_stmt|;
name|cont
operator|.
name|setAttribute
argument_list|(
name|TASK
argument_list|,
name|task
argument_list|)
expr_stmt|;
name|Future
argument_list|<
name|?
argument_list|>
name|f
init|=
name|getExecutor
argument_list|()
operator|.
name|submit
argument_list|(
name|task
argument_list|)
decl_stmt|;
name|cont
operator|.
name|addContinuationListener
argument_list|(
operator|new
name|Listener
argument_list|(
name|f
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cont
operator|.
name|isExpired
argument_list|()
condition|)
block|{
name|rsp
operator|.
name|sendError
argument_list|(
name|SC_SERVICE_UNAVAILABLE
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cont
operator|.
name|isResumed
argument_list|()
operator|&&
name|cont
operator|.
name|getAttribute
argument_list|(
name|CANCEL
argument_list|)
operator|==
name|Boolean
operator|.
name|TRUE
condition|)
block|{
name|rsp
operator|.
name|sendError
argument_list|(
name|SC_SERVICE_UNAVAILABLE
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cont
operator|.
name|isResumed
argument_list|()
condition|)
block|{
name|TaskThunk
name|task
init|=
operator|(
name|TaskThunk
operator|)
name|cont
operator|.
name|getAttribute
argument_list|(
name|TASK
argument_list|)
decl_stmt|;
try|try
block|{
name|task
operator|.
name|begin
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
argument_list|)
expr_stmt|;
name|chain
operator|.
name|doFilter
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|task
operator|.
name|end
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|interrupted
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|context
operator|.
name|log
argument_list|(
literal|"Unexpected QoS continuation state, aborting request"
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|sendError
argument_list|(
name|SC_SERVICE_UNAVAILABLE
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getExecutor ()
specifier|private
name|ScheduledThreadPoolExecutor
name|getExecutor
parameter_list|()
block|{
name|QueueProvider
operator|.
name|QueueType
name|qt
init|=
name|limitsFactory
operator|.
name|create
argument_list|(
name|user
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|getQueueType
argument_list|()
decl_stmt|;
return|return
name|queue
operator|.
name|getQueue
argument_list|(
name|qt
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|init (FilterConfig config)
specifier|public
name|void
name|init
parameter_list|(
name|FilterConfig
name|config
parameter_list|)
block|{}
annotation|@
name|Override
DECL|method|destroy ()
specifier|public
name|void
name|destroy
parameter_list|()
block|{}
DECL|class|Listener
specifier|private
specifier|static
specifier|final
class|class
name|Listener
implements|implements
name|ContinuationListener
block|{
DECL|field|future
specifier|final
name|Future
argument_list|<
name|?
argument_list|>
name|future
decl_stmt|;
DECL|method|Listener (Future<?> future)
name|Listener
parameter_list|(
name|Future
argument_list|<
name|?
argument_list|>
name|future
parameter_list|)
block|{
name|this
operator|.
name|future
operator|=
name|future
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onComplete (Continuation self)
specifier|public
name|void
name|onComplete
parameter_list|(
name|Continuation
name|self
parameter_list|)
block|{}
annotation|@
name|Override
DECL|method|onTimeout (Continuation self)
specifier|public
name|void
name|onTimeout
parameter_list|(
name|Continuation
name|self
parameter_list|)
block|{
name|future
operator|.
name|cancel
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|TaskThunk
specifier|private
specifier|final
class|class
name|TaskThunk
implements|implements
name|CancelableRunnable
block|{
DECL|field|cont
specifier|private
specifier|final
name|Continuation
name|cont
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|lock
specifier|private
specifier|final
name|Object
name|lock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|field|done
specifier|private
name|boolean
name|done
decl_stmt|;
DECL|field|worker
specifier|private
name|Thread
name|worker
decl_stmt|;
DECL|method|TaskThunk (Continuation cont, HttpServletRequest req)
name|TaskThunk
parameter_list|(
name|Continuation
name|cont
parameter_list|,
name|HttpServletRequest
name|req
parameter_list|)
block|{
name|this
operator|.
name|cont
operator|=
name|cont
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|generateName
argument_list|(
name|req
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
name|cont
operator|.
name|resume
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|lock
init|)
block|{
while|while
condition|(
operator|!
name|done
condition|)
block|{
try|try
block|{
name|lock
operator|.
name|wait
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
if|if
condition|(
name|worker
operator|!=
literal|null
condition|)
block|{
name|worker
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
block|}
block|}
block|}
DECL|method|begin (Thread thread)
name|void
name|begin
parameter_list|(
name|Thread
name|thread
parameter_list|)
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|worker
operator|=
name|thread
expr_stmt|;
block|}
block|}
DECL|method|end ()
name|void
name|end
parameter_list|()
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|worker
operator|=
literal|null
expr_stmt|;
name|done
operator|=
literal|true
expr_stmt|;
name|lock
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|cancel ()
specifier|public
name|void
name|cancel
parameter_list|()
block|{
name|cont
operator|.
name|setAttribute
argument_list|(
name|CANCEL
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|cont
operator|.
name|resume
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|generateName (HttpServletRequest req)
specifier|private
name|String
name|generateName
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|)
block|{
name|String
name|userName
init|=
literal|""
decl_stmt|;
name|CurrentUser
name|who
init|=
name|user
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|who
operator|.
name|isIdentifiedUser
argument_list|()
condition|)
block|{
name|Optional
argument_list|<
name|String
argument_list|>
name|name
init|=
name|who
operator|.
name|asIdentifiedUser
argument_list|()
operator|.
name|getUserName
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|userName
operator|=
literal|" ("
operator|+
name|name
operator|.
name|get
argument_list|()
operator|+
literal|")"
expr_stmt|;
block|}
block|}
name|String
name|uri
init|=
name|req
operator|.
name|getServletPath
argument_list|()
decl_stmt|;
name|Matcher
name|m
init|=
name|URI_PATTERN
operator|.
name|matcher
argument_list|(
name|uri
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|matches
argument_list|()
condition|)
block|{
name|String
name|path
init|=
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|String
name|cmd
init|=
name|m
operator|.
name|group
argument_list|(
literal|2
argument_list|)
decl_stmt|;
return|return
name|cmd
operator|+
literal|" "
operator|+
name|path
operator|+
name|userName
return|;
block|}
return|return
name|req
operator|.
name|getMethod
argument_list|()
operator|+
literal|" "
operator|+
name|uri
operator|+
name|userName
return|;
block|}
block|}
block|}
end_class

end_unit

