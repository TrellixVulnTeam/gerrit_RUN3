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
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|data
operator|.
name|GarbageCollectionResult
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
name|data
operator|.
name|GlobalCapability
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
name|annotations
operator|.
name|RequiresCapability
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
name|server
operator|.
name|git
operator|.
name|GarbageCollection
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
name|ProjectControl
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
name|BaseCommand
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
name|Argument
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
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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

begin_comment
comment|/** Runs the Git garbage collection. */
end_comment

begin_class
annotation|@
name|RequiresCapability
argument_list|(
name|GlobalCapability
operator|.
name|RUN_GC
argument_list|)
annotation|@
name|CommandMetaData
argument_list|(
name|name
operator|=
literal|"gc"
argument_list|,
name|description
operator|=
literal|"Run Git garbage collection"
argument_list|)
DECL|class|GarbageCollectionCommand
specifier|public
class|class
name|GarbageCollectionCommand
extends|extends
name|BaseCommand
block|{
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--all"
argument_list|,
name|usage
operator|=
literal|"runs the Git garbage collection for all projects"
argument_list|)
DECL|field|all
specifier|private
name|boolean
name|all
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--show-progress"
argument_list|,
name|usage
operator|=
literal|"progress information is shown"
argument_list|)
DECL|field|showProgress
specifier|private
name|boolean
name|showProgress
decl_stmt|;
annotation|@
name|Argument
argument_list|(
name|index
operator|=
literal|0
argument_list|,
name|required
operator|=
literal|false
argument_list|,
name|multiValued
operator|=
literal|true
argument_list|,
name|metaVar
operator|=
literal|"NAME"
argument_list|,
name|usage
operator|=
literal|"projects for which the Git garbage collection should be run"
argument_list|)
DECL|field|projects
specifier|private
name|List
argument_list|<
name|ProjectControl
argument_list|>
name|projects
init|=
operator|new
name|ArrayList
argument_list|<
name|ProjectControl
argument_list|>
argument_list|()
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
DECL|field|garbageCollectionFactory
specifier|private
name|GarbageCollection
operator|.
name|Factory
name|garbageCollectionFactory
decl_stmt|;
DECL|field|stdout
specifier|private
name|PrintWriter
name|stdout
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
name|startThread
argument_list|(
operator|new
name|CommandRunnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|stdout
operator|=
name|toPrintWriter
argument_list|(
name|out
argument_list|)
expr_stmt|;
try|try
block|{
name|parseCommandLine
argument_list|()
expr_stmt|;
name|verifyCommandLine
argument_list|()
expr_stmt|;
name|runGC
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
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyCommandLine ()
specifier|private
name|void
name|verifyCommandLine
parameter_list|()
throws|throws
name|UnloggedFailure
block|{
if|if
condition|(
operator|!
name|all
operator|&&
name|projects
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|UnloggedFailure
argument_list|(
literal|1
argument_list|,
literal|"needs projects as command arguments or --all option"
argument_list|)
throw|;
block|}
if|if
condition|(
name|all
operator|&&
operator|!
name|projects
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|UnloggedFailure
argument_list|(
literal|1
argument_list|,
literal|"either specify projects as command arguments or use --all option"
argument_list|)
throw|;
block|}
block|}
DECL|method|runGC ()
specifier|private
name|void
name|runGC
parameter_list|()
block|{
name|List
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
name|projectNames
decl_stmt|;
if|if
condition|(
name|all
condition|)
block|{
name|projectNames
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|projectCache
operator|.
name|all
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|projectNames
operator|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|projects
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|ProjectControl
name|pc
range|:
name|projects
control|)
block|{
name|projectNames
operator|.
name|add
argument_list|(
name|pc
operator|.
name|getProject
argument_list|()
operator|.
name|getNameKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|GarbageCollectionResult
name|result
init|=
name|garbageCollectionFactory
operator|.
name|create
argument_list|()
operator|.
name|run
argument_list|(
name|projectNames
argument_list|,
name|showProgress
condition|?
name|stdout
else|:
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|.
name|hasErrors
argument_list|()
condition|)
block|{
for|for
control|(
name|GarbageCollectionResult
operator|.
name|Error
name|e
range|:
name|result
operator|.
name|getErrors
argument_list|()
control|)
block|{
name|String
name|msg
decl_stmt|;
switch|switch
condition|(
name|e
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|REPOSITORY_NOT_FOUND
case|:
name|msg
operator|=
literal|"error: project \""
operator|+
name|e
operator|.
name|getProjectName
argument_list|()
operator|+
literal|"\" not found"
expr_stmt|;
break|break;
case|case
name|GC_ALREADY_SCHEDULED
case|:
name|msg
operator|=
literal|"error: garbage collection for project \""
operator|+
name|e
operator|.
name|getProjectName
argument_list|()
operator|+
literal|"\" was already scheduled"
expr_stmt|;
break|break;
case|case
name|GC_FAILED
case|:
name|msg
operator|=
literal|"error: garbage collection for project \""
operator|+
name|e
operator|.
name|getProjectName
argument_list|()
operator|+
literal|"\" failed"
expr_stmt|;
break|break;
default|default:
name|msg
operator|=
literal|"error: garbage collection for project \""
operator|+
name|e
operator|.
name|getProjectName
argument_list|()
operator|+
literal|"\" failed: "
operator|+
name|e
operator|.
name|getType
argument_list|()
expr_stmt|;
block|}
name|stdout
operator|.
name|print
argument_list|(
name|msg
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

