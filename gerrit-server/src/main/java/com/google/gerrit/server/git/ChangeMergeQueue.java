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
DECL|package|com.google.gerrit.server.git
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|git
package|;
end_package

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
name|MILLISECONDS
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
name|SECONDS
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
name|Branch
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
name|RemotePeer
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
name|GerritRequestModule
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
name|ssh
operator|.
name|SshInfo
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
name|RequestContext
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
name|RequestScopePropagator
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
name|TimeUtil
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
name|AbstractModule
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
name|Injector
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
name|OutOfScopeException
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
name|Provides
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
name|RequestScoped
import|;
end_import

begin_import
import|import
name|com
operator|.
name|jcraft
operator|.
name|jsch
operator|.
name|HostKey
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|HashMap
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
name|java
operator|.
name|util
operator|.
name|Map
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
name|TimeUnit
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|ChangeMergeQueue
specifier|public
class|class
name|ChangeMergeQueue
implements|implements
name|MergeQueue
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ChangeMergeQueue
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|active
specifier|private
specifier|final
name|Map
argument_list|<
name|Branch
operator|.
name|NameKey
argument_list|,
name|MergeEntry
argument_list|>
name|active
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|recheck
specifier|private
specifier|final
name|Map
argument_list|<
name|Branch
operator|.
name|NameKey
argument_list|,
name|RecheckJob
argument_list|>
name|recheck
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|workQueue
specifier|private
specifier|final
name|WorkQueue
name|workQueue
decl_stmt|;
DECL|field|bgFactory
specifier|private
specifier|final
name|Provider
argument_list|<
name|MergeOp
operator|.
name|Factory
argument_list|>
name|bgFactory
decl_stmt|;
DECL|field|threadScoper
specifier|private
specifier|final
name|PerThreadRequestScope
operator|.
name|Scoper
name|threadScoper
decl_stmt|;
annotation|@
name|Inject
DECL|method|ChangeMergeQueue (final WorkQueue wq, Injector parent)
name|ChangeMergeQueue
parameter_list|(
specifier|final
name|WorkQueue
name|wq
parameter_list|,
name|Injector
name|parent
parameter_list|)
block|{
name|workQueue
operator|=
name|wq
expr_stmt|;
name|Injector
name|child
init|=
name|parent
operator|.
name|createChildInjector
argument_list|(
operator|new
name|AbstractModule
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|bindScope
argument_list|(
name|RequestScoped
operator|.
name|class
argument_list|,
name|PerThreadRequestScope
operator|.
name|REQUEST
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|RequestScopePropagator
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|PerThreadRequestScope
operator|.
name|Propagator
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|PerThreadRequestScope
operator|.
name|Propagator
operator|.
name|class
argument_list|)
expr_stmt|;
name|install
argument_list|(
operator|new
name|GerritRequestModule
argument_list|()
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|SocketAddress
operator|.
name|class
argument_list|)
operator|.
name|annotatedWith
argument_list|(
name|RemotePeer
operator|.
name|class
argument_list|)
operator|.
name|toProvider
argument_list|(
operator|new
name|Provider
argument_list|<
name|SocketAddress
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|SocketAddress
name|get
parameter_list|()
block|{
throw|throw
operator|new
name|OutOfScopeException
argument_list|(
literal|"No remote peer on merge thread"
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|SshInfo
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
operator|new
name|SshInfo
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|HostKey
argument_list|>
name|getHostKeys
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Provides
specifier|public
name|PerThreadRequestScope
operator|.
name|Scoper
name|provideScoper
parameter_list|(
specifier|final
name|PerThreadRequestScope
operator|.
name|Propagator
name|propagator
parameter_list|,
specifier|final
name|Provider
argument_list|<
name|RequestScopedReviewDbProvider
argument_list|>
name|dbProvider
parameter_list|)
block|{
specifier|final
name|RequestContext
name|requestContext
init|=
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
throw|throw
operator|new
name|OutOfScopeException
argument_list|(
literal|"No user on merge thread"
argument_list|)
throw|;
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
name|dbProvider
operator|.
name|get
argument_list|()
return|;
block|}
block|}
decl_stmt|;
return|return
operator|new
name|PerThreadRequestScope
operator|.
name|Scoper
argument_list|()
block|{
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Callable
argument_list|<
name|T
argument_list|>
name|scope
parameter_list|(
name|Callable
argument_list|<
name|T
argument_list|>
name|callable
parameter_list|)
block|{
return|return
name|propagator
operator|.
name|scope
argument_list|(
name|requestContext
argument_list|,
name|callable
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|bgFactory
operator|=
name|child
operator|.
name|getProvider
argument_list|(
name|MergeOp
operator|.
name|Factory
operator|.
name|class
argument_list|)
expr_stmt|;
name|threadScoper
operator|=
name|child
operator|.
name|getInstance
argument_list|(
name|PerThreadRequestScope
operator|.
name|Scoper
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|merge (Branch.NameKey branch)
specifier|public
name|void
name|merge
parameter_list|(
name|Branch
operator|.
name|NameKey
name|branch
parameter_list|)
block|{
if|if
condition|(
name|start
argument_list|(
name|branch
argument_list|)
condition|)
block|{
name|mergeImpl
argument_list|(
name|branch
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|start (final Branch.NameKey branch)
specifier|private
specifier|synchronized
name|boolean
name|start
parameter_list|(
specifier|final
name|Branch
operator|.
name|NameKey
name|branch
parameter_list|)
block|{
specifier|final
name|MergeEntry
name|e
init|=
name|active
operator|.
name|get
argument_list|(
name|branch
argument_list|)
decl_stmt|;
if|if
condition|(
name|e
operator|==
literal|null
condition|)
block|{
comment|// Let the caller attempt this merge, its the only one interested
comment|// in processing this branch right now.
comment|//
name|active
operator|.
name|put
argument_list|(
name|branch
argument_list|,
operator|new
name|MergeEntry
argument_list|(
name|branch
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
comment|// Request that the job queue handle this merge later.
comment|//
name|e
operator|.
name|needMerge
operator|=
literal|true
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|schedule (final Branch.NameKey branch)
specifier|public
specifier|synchronized
name|void
name|schedule
parameter_list|(
specifier|final
name|Branch
operator|.
name|NameKey
name|branch
parameter_list|)
block|{
name|MergeEntry
name|e
init|=
name|active
operator|.
name|get
argument_list|(
name|branch
argument_list|)
decl_stmt|;
if|if
condition|(
name|e
operator|==
literal|null
condition|)
block|{
name|e
operator|=
operator|new
name|MergeEntry
argument_list|(
name|branch
argument_list|)
expr_stmt|;
name|active
operator|.
name|put
argument_list|(
name|branch
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|e
operator|.
name|needMerge
operator|=
literal|true
expr_stmt|;
name|scheduleJob
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|e
operator|.
name|needMerge
operator|=
literal|true
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|recheckAfter (final Branch.NameKey branch, final long delay, final TimeUnit delayUnit)
specifier|public
specifier|synchronized
name|void
name|recheckAfter
parameter_list|(
specifier|final
name|Branch
operator|.
name|NameKey
name|branch
parameter_list|,
specifier|final
name|long
name|delay
parameter_list|,
specifier|final
name|TimeUnit
name|delayUnit
parameter_list|)
block|{
specifier|final
name|long
name|now
init|=
name|TimeUtil
operator|.
name|nowMs
argument_list|()
decl_stmt|;
specifier|final
name|long
name|at
init|=
name|now
operator|+
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|delay
argument_list|,
name|delayUnit
argument_list|)
decl_stmt|;
name|RecheckJob
name|e
init|=
name|recheck
operator|.
name|get
argument_list|(
name|branch
argument_list|)
decl_stmt|;
if|if
condition|(
name|e
operator|==
literal|null
condition|)
block|{
name|e
operator|=
operator|new
name|RecheckJob
argument_list|(
name|branch
argument_list|)
expr_stmt|;
name|workQueue
operator|.
name|getDefaultQueue
argument_list|()
operator|.
name|schedule
argument_list|(
name|e
argument_list|,
name|now
operator|-
name|at
argument_list|,
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|recheck
operator|.
name|put
argument_list|(
name|branch
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|e
operator|.
name|recheckAt
operator|=
name|Math
operator|.
name|max
argument_list|(
name|at
argument_list|,
name|e
operator|.
name|recheckAt
argument_list|)
expr_stmt|;
block|}
DECL|method|finish (final Branch.NameKey branch)
specifier|private
specifier|synchronized
name|void
name|finish
parameter_list|(
specifier|final
name|Branch
operator|.
name|NameKey
name|branch
parameter_list|)
block|{
specifier|final
name|MergeEntry
name|e
init|=
name|active
operator|.
name|get
argument_list|(
name|branch
argument_list|)
decl_stmt|;
if|if
condition|(
name|e
operator|==
literal|null
condition|)
block|{
comment|// Not registered? Shouldn't happen but ignore it.
comment|//
return|return;
block|}
if|if
condition|(
operator|!
name|e
operator|.
name|needMerge
condition|)
block|{
comment|// No additional merges are in progress, we can delete it.
comment|//
name|active
operator|.
name|remove
argument_list|(
name|branch
argument_list|)
expr_stmt|;
return|return;
block|}
name|scheduleJob
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
DECL|method|scheduleJob (final MergeEntry e)
specifier|private
name|void
name|scheduleJob
parameter_list|(
specifier|final
name|MergeEntry
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|e
operator|.
name|jobScheduled
condition|)
block|{
comment|// No job has been scheduled to execute this branch, but it needs
comment|// to run a merge again.
comment|//
name|e
operator|.
name|jobScheduled
operator|=
literal|true
expr_stmt|;
name|workQueue
operator|.
name|getDefaultQueue
argument_list|()
operator|.
name|schedule
argument_list|(
name|e
argument_list|,
literal|0
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|unschedule (final MergeEntry e)
specifier|private
specifier|synchronized
name|void
name|unschedule
parameter_list|(
specifier|final
name|MergeEntry
name|e
parameter_list|)
block|{
name|e
operator|.
name|jobScheduled
operator|=
literal|false
expr_stmt|;
name|e
operator|.
name|needMerge
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|mergeImpl (final Branch.NameKey branch)
specifier|private
name|void
name|mergeImpl
parameter_list|(
specifier|final
name|Branch
operator|.
name|NameKey
name|branch
parameter_list|)
block|{
try|try
block|{
name|threadScoper
operator|.
name|scope
argument_list|(
operator|new
name|Callable
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|bgFactory
operator|.
name|get
argument_list|()
operator|.
name|create
argument_list|(
name|branch
argument_list|)
operator|.
name|merge
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
operator|.
name|call
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Merge attempt for "
operator|+
name|branch
operator|+
literal|" failed"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|finish
argument_list|(
name|branch
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|recheck (final RecheckJob e)
specifier|private
specifier|synchronized
name|void
name|recheck
parameter_list|(
specifier|final
name|RecheckJob
name|e
parameter_list|)
block|{
specifier|final
name|long
name|remainingDelay
init|=
name|e
operator|.
name|recheckAt
operator|-
name|TimeUtil
operator|.
name|nowMs
argument_list|()
decl_stmt|;
if|if
condition|(
name|MILLISECONDS
operator|.
name|convert
argument_list|(
literal|10
argument_list|,
name|SECONDS
argument_list|)
operator|<
name|remainingDelay
condition|)
block|{
comment|// Woke up too early, the job deadline was pushed back.
comment|// Reschedule for the new deadline. We allow for a small
comment|// amount of fuzz due to multiple reschedule attempts in
comment|// a short period of time being caused by MergeOp.
comment|//
name|workQueue
operator|.
name|getDefaultQueue
argument_list|()
operator|.
name|schedule
argument_list|(
name|e
argument_list|,
name|remainingDelay
argument_list|,
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Schedule a merge attempt on this branch to see if we can
comment|// actually complete it this time.
comment|//
name|schedule
argument_list|(
name|e
operator|.
name|dest
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|MergeEntry
specifier|private
class|class
name|MergeEntry
implements|implements
name|Runnable
block|{
DECL|field|dest
specifier|final
name|Branch
operator|.
name|NameKey
name|dest
decl_stmt|;
DECL|field|needMerge
name|boolean
name|needMerge
decl_stmt|;
DECL|field|jobScheduled
name|boolean
name|jobScheduled
decl_stmt|;
DECL|method|MergeEntry (final Branch.NameKey d)
name|MergeEntry
parameter_list|(
specifier|final
name|Branch
operator|.
name|NameKey
name|d
parameter_list|)
block|{
name|dest
operator|=
name|d
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
name|unschedule
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|mergeImpl
argument_list|(
name|dest
argument_list|)
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
specifier|final
name|Project
operator|.
name|NameKey
name|project
init|=
name|dest
operator|.
name|getParentKey
argument_list|()
decl_stmt|;
return|return
literal|"submit "
operator|+
name|project
operator|.
name|get
argument_list|()
operator|+
literal|" "
operator|+
name|dest
operator|.
name|getShortName
argument_list|()
return|;
block|}
block|}
DECL|class|RecheckJob
specifier|private
class|class
name|RecheckJob
implements|implements
name|Runnable
block|{
DECL|field|dest
specifier|final
name|Branch
operator|.
name|NameKey
name|dest
decl_stmt|;
DECL|field|recheckAt
name|long
name|recheckAt
decl_stmt|;
DECL|method|RecheckJob (final Branch.NameKey d)
name|RecheckJob
parameter_list|(
specifier|final
name|Branch
operator|.
name|NameKey
name|d
parameter_list|)
block|{
name|dest
operator|=
name|d
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
name|recheck
argument_list|(
name|this
argument_list|)
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
specifier|final
name|Project
operator|.
name|NameKey
name|project
init|=
name|dest
operator|.
name|getParentKey
argument_list|()
decl_stmt|;
return|return
literal|"recheck "
operator|+
name|project
operator|.
name|get
argument_list|()
operator|+
literal|" "
operator|+
name|dest
operator|.
name|getShortName
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

