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
DECL|package|com.google.gerrit.server.util
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|util
package|;
end_package

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
name|Throwables
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
name|reviewdb
operator|.
name|client
operator|.
name|Project
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
name|reviewdb
operator|.
name|server
operator|.
name|ReviewDb
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
name|RequestCleanup
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
name|RequestScopedReviewDbProvider
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
name|ProjectRunnable
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
name|Key
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
name|Scope
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
name|ServletScopes
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
name|Callable
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
name|Executors
import|;
end_import

begin_comment
comment|/**  * Base class for propagating request-scoped data between threads.  *<p>  * Request scopes are typically linked to a {@link ThreadLocal}, which is only  * available to the current thread.  In order to allow background work involving  * RequestScoped data, the ThreadLocal data must be copied from the request thread to  * the new background thread.  *<p>  * Every type of RequestScope must provide an implementation of  * RequestScopePropagator. See {@link #wrap(Callable)} for details on the  * implementation, usage, and restrictions.  *  * @see ThreadLocalRequestScopePropagator  */
end_comment

begin_class
DECL|class|RequestScopePropagator
specifier|public
specifier|abstract
class|class
name|RequestScopePropagator
block|{
DECL|field|scope
specifier|private
specifier|final
name|Scope
name|scope
decl_stmt|;
DECL|field|local
specifier|private
specifier|final
name|ThreadLocalRequestContext
name|local
decl_stmt|;
DECL|field|dbProviderProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|RequestScopedReviewDbProvider
argument_list|>
name|dbProviderProvider
decl_stmt|;
DECL|method|RequestScopePropagator (Scope scope, ThreadLocalRequestContext local, Provider<RequestScopedReviewDbProvider> dbProviderProvider)
specifier|protected
name|RequestScopePropagator
parameter_list|(
name|Scope
name|scope
parameter_list|,
name|ThreadLocalRequestContext
name|local
parameter_list|,
name|Provider
argument_list|<
name|RequestScopedReviewDbProvider
argument_list|>
name|dbProviderProvider
parameter_list|)
block|{
name|this
operator|.
name|scope
operator|=
name|scope
expr_stmt|;
name|this
operator|.
name|local
operator|=
name|local
expr_stmt|;
name|this
operator|.
name|dbProviderProvider
operator|=
name|dbProviderProvider
expr_stmt|;
block|}
comment|/**    * Wraps callable in a new {@link Callable} that propagates the current    * request state when the callable is invoked. The method must be called in a    * request scope and the returned Callable may only be invoked in a thread    * that is not already in a request scope. The returned Callable will inherit    * toString() from the passed in Callable. A    * {@link com.google.gerrit.server.git.WorkQueue.Executor} does not accept a    * Callable, so there is no ProjectCallable implementation. Implementations of    * this method must be consistent with Guice's    * {@link ServletScopes#continueRequest(Callable, java.util.Map)}.    *<p>    * There are some limitations:    *<ul>    *<li>Derived objects (i.e. anything marked created in a request scope) will    * not be transported.</li>    *<li>State changes to the request scoped context after this method is called    * will not be seen in the continued thread.</li>    *</ul>    *    * @param callable the Callable to wrap.    * @return a new Callable which will execute in the current request scope.    */
DECL|method|wrap (final Callable<T> callable)
specifier|public
specifier|final
parameter_list|<
name|T
parameter_list|>
name|Callable
argument_list|<
name|T
argument_list|>
name|wrap
parameter_list|(
specifier|final
name|Callable
argument_list|<
name|T
argument_list|>
name|callable
parameter_list|)
block|{
specifier|final
name|Callable
argument_list|<
name|T
argument_list|>
name|wrapped
init|=
name|wrapImpl
argument_list|(
name|context
argument_list|(
name|local
operator|.
name|getContext
argument_list|()
argument_list|,
name|cleanup
argument_list|(
name|callable
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|Callable
argument_list|<
name|T
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|T
name|call
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|wrapped
operator|.
name|call
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|callable
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
return|;
block|}
comment|/**    * Wraps runnable in a new {@link Runnable} that propagates the current    * request state when the runnable is invoked. The method must be called in a    * request scope and the returned Runnable may only be invoked in a thread    * that is not already in a request scope. The returned Runnable will inherit    * toString() from the passed in Runnable. Furthermore, if the passed runnable    * is of type {@link ProjectRunnable}, the returned runnable will be of the    * same type with the methods delegated.    *    * See {@link #wrap(Callable)} for details on implementation and usage.    *    * @param runnable the Runnable to wrap.    * @return a new Runnable which will execute in the current request scope.    */
DECL|method|wrap (final Runnable runnable)
specifier|public
specifier|final
name|Runnable
name|wrap
parameter_list|(
specifier|final
name|Runnable
name|runnable
parameter_list|)
block|{
specifier|final
name|Callable
argument_list|<
name|Object
argument_list|>
name|wrapped
init|=
name|wrap
argument_list|(
name|Executors
operator|.
name|callable
argument_list|(
name|runnable
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|runnable
operator|instanceof
name|ProjectRunnable
condition|)
block|{
return|return
operator|new
name|ProjectRunnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|wrapped
operator|.
name|call
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|Throwables
operator|.
name|propagateIfPossible
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
comment|// Not possible.
block|}
block|}
annotation|@
name|Override
specifier|public
name|Project
operator|.
name|NameKey
name|getProjectNameKey
parameter_list|()
block|{
return|return
operator|(
operator|(
name|ProjectRunnable
operator|)
name|runnable
operator|)
operator|.
name|getProjectNameKey
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getRemoteName
parameter_list|()
block|{
return|return
operator|(
operator|(
name|ProjectRunnable
operator|)
name|runnable
operator|)
operator|.
name|getRemoteName
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasCustomizedPrint
parameter_list|()
block|{
return|return
operator|(
operator|(
name|ProjectRunnable
operator|)
name|runnable
operator|)
operator|.
name|hasCustomizedPrint
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|runnable
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
return|;
block|}
else|else
block|{
return|return
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
try|try
block|{
name|wrapped
operator|.
name|call
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
comment|// Not possible.
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|runnable
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
comment|/**    * @see #wrap(Callable)    */
DECL|method|wrapImpl (final Callable<T> callable)
specifier|protected
specifier|abstract
parameter_list|<
name|T
parameter_list|>
name|Callable
argument_list|<
name|T
argument_list|>
name|wrapImpl
parameter_list|(
specifier|final
name|Callable
argument_list|<
name|T
argument_list|>
name|callable
parameter_list|)
function_decl|;
DECL|method|context (RequestContext context, final Callable<T> callable)
specifier|protected
parameter_list|<
name|T
parameter_list|>
name|Callable
argument_list|<
name|T
argument_list|>
name|context
parameter_list|(
name|RequestContext
name|context
parameter_list|,
specifier|final
name|Callable
argument_list|<
name|T
argument_list|>
name|callable
parameter_list|)
block|{
specifier|final
name|CurrentUser
name|user
init|=
name|context
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
return|return
operator|new
name|Callable
argument_list|<
name|T
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|T
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|RequestContext
name|old
init|=
name|local
operator|.
name|setContext
argument_list|(
operator|new
name|RequestContext
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|CurrentUser
name|getCurrentUser
parameter_list|()
block|{
return|return
name|user
return|;
block|}
annotation|@
name|Override
specifier|public
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|getReviewDbProvider
parameter_list|()
block|{
return|return
name|dbProviderProvider
operator|.
name|get
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|callable
operator|.
name|call
argument_list|()
return|;
block|}
finally|finally
block|{
name|local
operator|.
name|setContext
argument_list|(
name|old
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
DECL|method|cleanup (final Callable<T> callable)
specifier|protected
parameter_list|<
name|T
parameter_list|>
name|Callable
argument_list|<
name|T
argument_list|>
name|cleanup
parameter_list|(
specifier|final
name|Callable
argument_list|<
name|T
argument_list|>
name|callable
parameter_list|)
block|{
return|return
operator|new
name|Callable
argument_list|<
name|T
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|T
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|RequestCleanup
name|cleanup
init|=
name|scope
operator|.
name|scope
argument_list|(
name|Key
operator|.
name|get
argument_list|(
name|RequestCleanup
operator|.
name|class
argument_list|)
argument_list|,
operator|new
name|Provider
argument_list|<
name|RequestCleanup
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RequestCleanup
name|get
parameter_list|()
block|{
return|return
operator|new
name|RequestCleanup
argument_list|()
return|;
block|}
block|}
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|callable
operator|.
name|call
argument_list|()
return|;
block|}
finally|finally
block|{
name|cleanup
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

