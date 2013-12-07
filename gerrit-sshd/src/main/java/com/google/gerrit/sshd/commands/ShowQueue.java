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
name|git
operator|.
name|TaskInfoFactory
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
name|ProjectTask
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
name|project
operator|.
name|ProjectCache
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
name|project
operator|.
name|ProjectState
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
literal|"Display the background work queues, including replication"
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
name|Inject
DECL|field|workQueue
specifier|private
name|WorkQueue
name|workQueue
decl_stmt|;
annotation|@
name|Inject
DECL|field|projectCache
specifier|private
name|ProjectCache
name|projectCache
decl_stmt|;
annotation|@
name|Inject
DECL|field|currentUser
specifier|private
name|IdentifiedUser
name|currentUser
decl_stmt|;
DECL|field|columns
specifier|private
name|int
name|columns
init|=
literal|80
decl_stmt|;
DECL|field|taskNameWidth
specifier|private
name|int
name|taskNameWidth
decl_stmt|;
annotation|@
name|Override
DECL|method|start (final Environment env)
specifier|public
name|void
name|start
parameter_list|(
specifier|final
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
block|{
name|taskNameWidth
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
specifier|final
name|List
argument_list|<
name|QueueTaskInfo
argument_list|>
name|pending
init|=
name|getSortedTaskInfoList
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
literal|"----------------------------------------------"
operator|+
literal|"--------------------------------\n"
argument_list|)
expr_stmt|;
name|int
name|numberOfPendingTasks
init|=
literal|0
decl_stmt|;
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
name|boolean
name|viewAll
init|=
name|currentUser
operator|.
name|getCapabilities
argument_list|()
operator|.
name|canViewQueue
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|QueueTaskInfo
name|taskInfo
range|:
name|pending
control|)
block|{
specifier|final
name|long
name|delay
init|=
name|taskInfo
operator|.
name|delayMillis
decl_stmt|;
specifier|final
name|Task
operator|.
name|State
name|state
init|=
name|taskInfo
operator|.
name|state
decl_stmt|;
specifier|final
name|String
name|start
decl_stmt|;
switch|switch
condition|(
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
name|state
argument_list|)
expr_stmt|;
break|break;
default|default:
name|start
operator|=
name|time
argument_list|(
name|now
argument_list|,
name|delay
argument_list|)
expr_stmt|;
break|break;
block|}
name|boolean
name|regularUserCanSee
init|=
literal|false
decl_stmt|;
name|boolean
name|hasCustomizedPrint
init|=
literal|true
decl_stmt|;
comment|// If the user is not administrator, check if has rights to see
comment|// the Task
name|Project
operator|.
name|NameKey
name|projectName
init|=
literal|null
decl_stmt|;
name|String
name|remoteName
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|viewAll
condition|)
block|{
name|projectName
operator|=
name|taskInfo
operator|.
name|getProjectNameKey
argument_list|()
expr_stmt|;
name|remoteName
operator|=
name|taskInfo
operator|.
name|getRemoteName
argument_list|()
expr_stmt|;
name|hasCustomizedPrint
operator|=
name|taskInfo
operator|.
name|hasCustomizedPrint
argument_list|()
expr_stmt|;
name|ProjectState
name|e
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|projectName
operator|!=
literal|null
condition|)
block|{
name|e
operator|=
name|projectCache
operator|.
name|get
argument_list|(
name|projectName
argument_list|)
expr_stmt|;
block|}
name|regularUserCanSee
operator|=
name|e
operator|!=
literal|null
operator|&&
name|e
operator|.
name|controlFor
argument_list|(
name|currentUser
argument_list|)
operator|.
name|isVisible
argument_list|()
expr_stmt|;
if|if
condition|(
name|regularUserCanSee
condition|)
block|{
name|numberOfPendingTasks
operator|++
expr_stmt|;
block|}
block|}
name|String
name|startTime
init|=
name|startTime
argument_list|(
name|taskInfo
operator|.
name|getStartTime
argument_list|()
argument_list|)
decl_stmt|;
comment|// Shows information about tasks depending on the user rights
if|if
condition|(
name|viewAll
operator|||
operator|(
operator|!
name|hasCustomizedPrint
operator|&&
name|regularUserCanSee
operator|)
condition|)
block|{
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
comment|//
name|id
argument_list|(
name|taskInfo
operator|.
name|getTaskId
argument_list|()
argument_list|)
argument_list|,
name|start
argument_list|,
name|startTime
argument_list|,
literal|""
argument_list|,
name|taskInfo
operator|.
name|getTaskString
argument_list|(
name|taskNameWidth
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|regularUserCanSee
condition|)
block|{
if|if
condition|(
name|remoteName
operator|==
literal|null
condition|)
block|{
name|remoteName
operator|=
name|projectName
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|remoteName
operator|=
name|remoteName
operator|+
literal|"/"
operator|+
name|projectName
expr_stmt|;
block|}
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
comment|//
name|id
argument_list|(
name|taskInfo
operator|.
name|getTaskId
argument_list|()
argument_list|)
argument_list|,
name|start
argument_list|,
name|startTime
argument_list|,
name|remoteName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|stdout
operator|.
name|print
argument_list|(
literal|"----------------------------------------------"
operator|+
literal|"--------------------------------\n"
argument_list|)
expr_stmt|;
if|if
condition|(
name|viewAll
condition|)
block|{
name|numberOfPendingTasks
operator|=
name|pending
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
name|stdout
operator|.
name|print
argument_list|(
literal|"  "
operator|+
name|numberOfPendingTasks
operator|+
literal|" tasks\n"
argument_list|)
expr_stmt|;
block|}
DECL|method|getSortedTaskInfoList ()
specifier|private
name|List
argument_list|<
name|QueueTaskInfo
argument_list|>
name|getSortedTaskInfoList
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|QueueTaskInfo
argument_list|>
name|taskInfos
init|=
name|workQueue
operator|.
name|getTaskInfos
argument_list|(
operator|new
name|TaskInfoFactory
argument_list|<
name|QueueTaskInfo
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|QueueTaskInfo
name|getTaskInfo
parameter_list|(
name|Task
argument_list|<
name|?
argument_list|>
name|task
parameter_list|)
block|{
return|return
operator|new
name|QueueTaskInfo
argument_list|(
name|task
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|taskInfos
argument_list|,
operator|new
name|Comparator
argument_list|<
name|QueueTaskInfo
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|QueueTaskInfo
name|a
parameter_list|,
name|QueueTaskInfo
name|b
parameter_list|)
block|{
if|if
condition|(
name|a
operator|.
name|state
operator|!=
name|b
operator|.
name|state
condition|)
block|{
return|return
name|a
operator|.
name|state
operator|.
name|ordinal
argument_list|()
operator|-
name|b
operator|.
name|state
operator|.
name|ordinal
argument_list|()
return|;
block|}
name|int
name|cmp
init|=
name|Long
operator|.
name|signum
argument_list|(
name|a
operator|.
name|delayMillis
operator|-
name|b
operator|.
name|delayMillis
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|!=
literal|0
condition|)
block|{
return|return
name|cmp
return|;
block|}
return|return
name|a
operator|.
name|getTaskString
argument_list|(
name|taskNameWidth
argument_list|)
operator|.
name|compareTo
argument_list|(
name|b
operator|.
name|getTaskString
argument_list|(
name|taskNameWidth
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|taskInfos
return|;
block|}
DECL|method|id (final int id)
specifier|private
specifier|static
name|String
name|id
parameter_list|(
specifier|final
name|int
name|id
parameter_list|)
block|{
return|return
name|IdGenerator
operator|.
name|format
argument_list|(
name|id
argument_list|)
return|;
block|}
DECL|method|time (final long now, final long delay)
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
name|delay
parameter_list|)
block|{
specifier|final
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
DECL|method|startTime (final Date when)
specifier|private
specifier|static
name|String
name|startTime
parameter_list|(
specifier|final
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
DECL|method|format (final Date when, final long timeFromNow)
specifier|private
specifier|static
name|String
name|format
parameter_list|(
specifier|final
name|Date
name|when
parameter_list|,
specifier|final
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
DECL|method|format (final Task.State state)
specifier|private
specifier|static
name|String
name|format
parameter_list|(
specifier|final
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
default|default:
return|return
name|state
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
DECL|class|QueueTaskInfo
specifier|private
specifier|static
class|class
name|QueueTaskInfo
block|{
DECL|field|delayMillis
specifier|private
specifier|final
name|long
name|delayMillis
decl_stmt|;
DECL|field|state
specifier|private
specifier|final
name|Task
operator|.
name|State
name|state
decl_stmt|;
DECL|field|task
specifier|private
specifier|final
name|Task
argument_list|<
name|?
argument_list|>
name|task
decl_stmt|;
DECL|method|QueueTaskInfo (Task<?> task)
name|QueueTaskInfo
parameter_list|(
name|Task
argument_list|<
name|?
argument_list|>
name|task
parameter_list|)
block|{
name|this
operator|.
name|task
operator|=
name|task
expr_stmt|;
name|this
operator|.
name|delayMillis
operator|=
name|task
operator|.
name|getDelay
argument_list|(
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|task
operator|.
name|getState
argument_list|()
expr_stmt|;
block|}
DECL|method|getRemoteName ()
name|String
name|getRemoteName
parameter_list|()
block|{
if|if
condition|(
name|task
operator|instanceof
name|ProjectTask
condition|)
block|{
return|return
operator|(
operator|(
name|ProjectTask
argument_list|<
name|?
argument_list|>
operator|)
name|task
operator|)
operator|.
name|getRemoteName
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|getProjectNameKey ()
name|Project
operator|.
name|NameKey
name|getProjectNameKey
parameter_list|()
block|{
if|if
condition|(
name|task
operator|instanceof
name|ProjectTask
argument_list|<
name|?
argument_list|>
condition|)
block|{
return|return
operator|(
operator|(
name|ProjectTask
argument_list|<
name|?
argument_list|>
operator|)
name|task
operator|)
operator|.
name|getProjectNameKey
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|hasCustomizedPrint ()
name|boolean
name|hasCustomizedPrint
parameter_list|()
block|{
if|if
condition|(
name|task
operator|instanceof
name|ProjectTask
argument_list|<
name|?
argument_list|>
condition|)
block|{
return|return
operator|(
operator|(
name|ProjectTask
argument_list|<
name|?
argument_list|>
operator|)
name|task
operator|)
operator|.
name|hasCustomizedPrint
argument_list|()
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|getTaskId ()
name|int
name|getTaskId
parameter_list|()
block|{
return|return
name|task
operator|.
name|getTaskId
argument_list|()
return|;
block|}
DECL|method|getStartTime ()
name|Date
name|getStartTime
parameter_list|()
block|{
return|return
name|task
operator|.
name|getStartTime
argument_list|()
return|;
block|}
DECL|method|getTaskString (int maxLength)
name|String
name|getTaskString
parameter_list|(
name|int
name|maxLength
parameter_list|)
block|{
name|String
name|s
init|=
name|task
operator|.
name|toString
argument_list|()
decl_stmt|;
return|return
name|s
operator|.
name|length
argument_list|()
operator|<
name|maxLength
condition|?
name|s
else|:
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|maxLength
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

