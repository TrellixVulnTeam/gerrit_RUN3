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
name|base
operator|.
name|MoreObjects
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
name|LinkedListMultimap
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
name|ListMultimap
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
name|TimeUtil
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
name|restapi
operator|.
name|AuthException
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
name|ConfigResource
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
name|Task
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
name|permissions
operator|.
name|GlobalPermission
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
name|permissions
operator|.
name|PermissionBackend
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
name|permissions
operator|.
name|PermissionBackendException
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
name|restapi
operator|.
name|config
operator|.
name|ListTasks
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
name|restapi
operator|.
name|config
operator|.
name|ListTasks
operator|.
name|TaskInfo
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
name|List
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
name|kohsuke
operator|.
name|args4j
operator|.
name|Option
import|;
end_import

begin_comment
comment|/** Display the current work queue. */
end_comment

begin_class
annotation|@
name|AdminHighPriorityCommand
annotation|@
name|CommandMetaData
argument_list|(
name|name
operator|=
literal|"show-queue"
argument_list|,
name|description
operator|=
literal|"Display the background work queues"
argument_list|,
name|runsAt
operator|=
name|MASTER_OR_SLAVE
argument_list|)
DECL|class|ShowQueue
specifier|final
class|class
name|ShowQueue
extends|extends
name|SshCommand
block|{
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--wide"
argument_list|,
name|aliases
operator|=
block|{
literal|"-w"
block|}
argument_list|,
name|usage
operator|=
literal|"display without line width truncation"
argument_list|)
DECL|field|wide
specifier|private
name|boolean
name|wide
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--by-queue"
argument_list|,
name|aliases
operator|=
block|{
literal|"-q"
block|}
argument_list|,
name|usage
operator|=
literal|"group tasks by queue and print queue info"
argument_list|)
DECL|field|groupByQueue
specifier|private
name|boolean
name|groupByQueue
decl_stmt|;
DECL|field|permissionBackend
annotation|@
name|Inject
specifier|private
name|PermissionBackend
name|permissionBackend
decl_stmt|;
DECL|field|listTasks
annotation|@
name|Inject
specifier|private
name|ListTasks
name|listTasks
decl_stmt|;
DECL|field|currentUser
annotation|@
name|Inject
specifier|private
name|IdentifiedUser
name|currentUser
decl_stmt|;
DECL|field|workQueue
annotation|@
name|Inject
specifier|private
name|WorkQueue
name|workQueue
decl_stmt|;
DECL|field|columns
specifier|private
name|int
name|columns
init|=
literal|80
decl_stmt|;
DECL|field|maxCommandWidth
specifier|private
name|int
name|maxCommandWidth
decl_stmt|;
annotation|@
name|Override
DECL|method|start (Environment env)
specifier|public
name|void
name|start
parameter_list|(
name|Environment
name|env
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|s
init|=
name|env
operator|.
name|getEnv
argument_list|()
operator|.
name|get
argument_list|(
name|Environment
operator|.
name|ENV_COLUMNS
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|!=
literal|null
operator|&&
operator|!
name|s
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
try|try
block|{
name|columns
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|err
parameter_list|)
block|{
name|columns
operator|=
literal|80
expr_stmt|;
block|}
block|}
name|super
operator|.
name|start
argument_list|(
name|env
argument_list|)
expr_stmt|;
block|}
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
name|maxCommandWidth
operator|=
name|wide
condition|?
name|Integer
operator|.
name|MAX_VALUE
else|:
name|columns
operator|-
literal|8
operator|-
literal|12
operator|-
literal|12
operator|-
literal|4
operator|-
literal|4
expr_stmt|;
name|stdout
operator|.
name|print
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%-8s %-12s %-12s %-4s %s\n"
argument_list|,
comment|//
literal|"Task"
argument_list|,
literal|"State"
argument_list|,
literal|"StartTime"
argument_list|,
literal|""
argument_list|,
literal|"Command"
argument_list|)
argument_list|)
expr_stmt|;
name|stdout
operator|.
name|print
argument_list|(
literal|"------------------------------------------------------------------------------\n"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|TaskInfo
argument_list|>
name|tasks
decl_stmt|;
try|try
block|{
name|tasks
operator|=
name|listTasks
operator|.
name|apply
argument_list|(
operator|new
name|ConfigResource
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AuthException
name|e
parameter_list|)
block|{
throw|throw
name|die
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|PermissionBackendException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
literal|1
argument_list|,
literal|"permission backend unavailable"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|boolean
name|viewAll
init|=
name|permissionBackend
operator|.
name|user
argument_list|(
name|currentUser
argument_list|)
operator|.
name|testOrFalse
argument_list|(
name|GlobalPermission
operator|.
name|VIEW_QUEUE
argument_list|)
decl_stmt|;
name|long
name|now
init|=
name|TimeUtil
operator|.
name|nowMs
argument_list|()
decl_stmt|;
if|if
condition|(
name|groupByQueue
condition|)
block|{
name|ListMultimap
argument_list|<
name|String
argument_list|,
name|TaskInfo
argument_list|>
name|byQueue
init|=
name|byQueue
argument_list|(
name|tasks
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|queueName
range|:
name|byQueue
operator|.
name|keySet
argument_list|()
control|)
block|{
name|ScheduledThreadPoolExecutor
name|e
init|=
name|workQueue
operator|.
name|getExecutor
argument_list|(
name|queueName
argument_list|)
decl_stmt|;
name|stdout
operator|.
name|print
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Queue: %s\n"
argument_list|,
name|queueName
argument_list|)
argument_list|)
expr_stmt|;
name|print
argument_list|(
name|byQueue
operator|.
name|get
argument_list|(
name|queueName
argument_list|)
argument_list|,
name|now
argument_list|,
name|viewAll
argument_list|,
name|e
operator|.
name|getCorePoolSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|print
argument_list|(
name|tasks
argument_list|,
name|now
argument_list|,
name|viewAll
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|byQueue (List<TaskInfo> tasks)
specifier|private
name|ListMultimap
argument_list|<
name|String
argument_list|,
name|TaskInfo
argument_list|>
name|byQueue
parameter_list|(
name|List
argument_list|<
name|TaskInfo
argument_list|>
name|tasks
parameter_list|)
block|{
name|ListMultimap
argument_list|<
name|String
argument_list|,
name|TaskInfo
argument_list|>
name|byQueue
init|=
name|LinkedListMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
for|for
control|(
name|TaskInfo
name|task
range|:
name|tasks
control|)
block|{
name|byQueue
operator|.
name|put
argument_list|(
name|task
operator|.
name|queueName
argument_list|,
name|task
argument_list|)
expr_stmt|;
block|}
return|return
name|byQueue
return|;
block|}
DECL|method|print (List<TaskInfo> tasks, long now, boolean viewAll, int threadPoolSize)
specifier|private
name|void
name|print
parameter_list|(
name|List
argument_list|<
name|TaskInfo
argument_list|>
name|tasks
parameter_list|,
name|long
name|now
parameter_list|,
name|boolean
name|viewAll
parameter_list|,
name|int
name|threadPoolSize
parameter_list|)
block|{
for|for
control|(
name|TaskInfo
name|task
range|:
name|tasks
control|)
block|{
name|String
name|start
decl_stmt|;
switch|switch
condition|(
name|task
operator|.
name|state
condition|)
block|{
case|case
name|DONE
case|:
case|case
name|CANCELLED
case|:
case|case
name|RUNNING
case|:
case|case
name|READY
case|:
name|start
operator|=
name|format
argument_list|(
name|task
operator|.
name|state
argument_list|)
expr_stmt|;
break|break;
case|case
name|OTHER
case|:
case|case
name|SLEEPING
case|:
default|default:
name|start
operator|=
name|time
argument_list|(
name|now
argument_list|,
name|task
operator|.
name|delay
argument_list|)
expr_stmt|;
break|break;
block|}
comment|// Shows information about tasks depending on the user rights
if|if
condition|(
name|viewAll
operator|||
name|task
operator|.
name|projectName
operator|==
literal|null
condition|)
block|{
name|String
name|command
init|=
name|task
operator|.
name|command
operator|.
name|length
argument_list|()
operator|<
name|maxCommandWidth
condition|?
name|task
operator|.
name|command
else|:
name|task
operator|.
name|command
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|maxCommandWidth
argument_list|)
decl_stmt|;
name|stdout
operator|.
name|print
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%8s %-12s %-12s %-4s %s\n"
argument_list|,
name|task
operator|.
name|id
argument_list|,
name|start
argument_list|,
name|startTime
argument_list|(
name|task
operator|.
name|startTime
argument_list|)
argument_list|,
literal|""
argument_list|,
name|command
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|remoteName
init|=
name|task
operator|.
name|remoteName
operator|!=
literal|null
condition|?
name|task
operator|.
name|remoteName
operator|+
literal|"/"
operator|+
name|task
operator|.
name|projectName
else|:
name|task
operator|.
name|projectName
decl_stmt|;
name|stdout
operator|.
name|print
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%8s %-12s %-4s %s\n"
argument_list|,
name|task
operator|.
name|id
argument_list|,
name|start
argument_list|,
name|startTime
argument_list|(
name|task
operator|.
name|startTime
argument_list|)
argument_list|,
name|MoreObjects
operator|.
name|firstNonNull
argument_list|(
name|remoteName
argument_list|,
literal|"n/a"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|stdout
operator|.
name|print
argument_list|(
literal|"------------------------------------------------------------------------------\n"
argument_list|)
expr_stmt|;
name|stdout
operator|.
name|print
argument_list|(
literal|"  "
operator|+
name|tasks
operator|.
name|size
argument_list|()
operator|+
literal|" tasks"
argument_list|)
expr_stmt|;
if|if
condition|(
name|threadPoolSize
operator|>
literal|0
condition|)
block|{
name|stdout
operator|.
name|print
argument_list|(
literal|", "
operator|+
name|threadPoolSize
operator|+
literal|" worker threads"
argument_list|)
expr_stmt|;
block|}
name|stdout
operator|.
name|print
argument_list|(
literal|"\n\n"
argument_list|)
expr_stmt|;
block|}
DECL|method|time (long now, long delay)
specifier|private
specifier|static
name|String
name|time
parameter_list|(
name|long
name|now
parameter_list|,
name|long
name|delay
parameter_list|)
block|{
name|Date
name|when
init|=
operator|new
name|Date
argument_list|(
name|now
operator|+
name|delay
argument_list|)
decl_stmt|;
return|return
name|format
argument_list|(
name|when
argument_list|,
name|delay
argument_list|)
return|;
block|}
DECL|method|startTime (Date when)
specifier|private
specifier|static
name|String
name|startTime
parameter_list|(
name|Date
name|when
parameter_list|)
block|{
return|return
name|format
argument_list|(
name|when
argument_list|,
name|TimeUtil
operator|.
name|nowMs
argument_list|()
operator|-
name|when
operator|.
name|getTime
argument_list|()
argument_list|)
return|;
block|}
DECL|method|format (Date when, long timeFromNow)
specifier|private
specifier|static
name|String
name|format
parameter_list|(
name|Date
name|when
parameter_list|,
name|long
name|timeFromNow
parameter_list|)
block|{
if|if
condition|(
name|timeFromNow
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
literal|"HH:mm:ss.SSS"
argument_list|)
operator|.
name|format
argument_list|(
name|when
argument_list|)
return|;
block|}
return|return
operator|new
name|SimpleDateFormat
argument_list|(
literal|"MMM-dd HH:mm"
argument_list|)
operator|.
name|format
argument_list|(
name|when
argument_list|)
return|;
block|}
DECL|method|format (Task.State state)
specifier|private
specifier|static
name|String
name|format
parameter_list|(
name|Task
operator|.
name|State
name|state
parameter_list|)
block|{
switch|switch
condition|(
name|state
condition|)
block|{
case|case
name|DONE
case|:
return|return
literal|"....... done"
return|;
case|case
name|CANCELLED
case|:
return|return
literal|"..... killed"
return|;
case|case
name|RUNNING
case|:
return|return
literal|""
return|;
case|case
name|READY
case|:
return|return
literal|"waiting ...."
return|;
case|case
name|SLEEPING
case|:
return|return
literal|"sleeping"
return|;
case|case
name|OTHER
case|:
default|default:
return|return
name|state
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

