begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright 2009 Google Inc.
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
DECL|package|com.google.gerrit.git
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|git
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
name|client
operator|.
name|reviewdb
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
name|server
operator|.
name|GerritServer
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
name|TimeUnit
import|;
end_import

begin_class
DECL|class|MergeQueue
specifier|public
class|class
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
name|MergeQueue
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|active
specifier|private
specifier|static
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
argument_list|<
name|Branch
operator|.
name|NameKey
argument_list|,
name|MergeEntry
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|merge (final Branch.NameKey branch)
specifier|public
specifier|static
name|void
name|merge
parameter_list|(
specifier|final
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
try|try
block|{
name|mergeImpl
argument_list|(
name|branch
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
block|}
DECL|method|start (final Branch.NameKey branch)
specifier|public
specifier|static
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
DECL|method|schedule (final Branch.NameKey branch)
specifier|public
specifier|static
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
block|}
name|e
operator|.
name|needMerge
operator|=
literal|true
expr_stmt|;
name|scheduleJobImp
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
DECL|method|finish (final Branch.NameKey branch)
specifier|public
specifier|static
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
comment|// Not registered for a build? Shouldn't happen but ignore it.
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
name|scheduleJobImp
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
DECL|method|scheduleJobImp (final MergeEntry e)
specifier|private
specifier|static
name|void
name|scheduleJobImp
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
name|WorkQueue
operator|.
name|schedule
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|unschedule
argument_list|(
name|e
argument_list|)
expr_stmt|;
try|try
block|{
name|mergeImpl
argument_list|(
name|e
operator|.
name|dest
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|finish
argument_list|(
name|e
operator|.
name|dest
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
specifier|static
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
specifier|static
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
operator|new
name|MergeOp
argument_list|(
name|GerritServer
operator|.
name|getInstance
argument_list|()
argument_list|,
name|branch
argument_list|)
operator|.
name|merge
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
block|}
DECL|class|MergeEntry
specifier|private
specifier|static
class|class
name|MergeEntry
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
block|}
block|}
end_class

end_unit

