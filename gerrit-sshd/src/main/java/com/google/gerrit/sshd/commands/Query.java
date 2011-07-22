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
name|server
operator|.
name|query
operator|.
name|change
operator|.
name|QueryProcessor
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
name|util
operator|.
name|List
import|;
end_import

begin_class
DECL|class|Query
class|class
name|Query
extends|extends
name|BaseCommand
block|{
annotation|@
name|Inject
DECL|field|processor
specifier|private
name|QueryProcessor
name|processor
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--format"
argument_list|,
name|metaVar
operator|=
literal|"FMT"
argument_list|,
name|usage
operator|=
literal|"Output display format"
argument_list|)
DECL|method|setFormat (QueryProcessor.OutputFormat format)
name|void
name|setFormat
parameter_list|(
name|QueryProcessor
operator|.
name|OutputFormat
name|format
parameter_list|)
block|{
name|processor
operator|.
name|setOutput
argument_list|(
name|out
argument_list|,
name|format
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--current-patch-set"
argument_list|,
name|usage
operator|=
literal|"Include information about current patch set"
argument_list|)
DECL|method|setCurrentPatchSet (boolean on)
name|void
name|setCurrentPatchSet
parameter_list|(
name|boolean
name|on
parameter_list|)
block|{
name|processor
operator|.
name|setIncludeCurrentPatchSet
argument_list|(
name|on
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--patch-sets"
argument_list|,
name|usage
operator|=
literal|"Include information about all patch sets"
argument_list|)
DECL|method|setPatchSets (boolean on)
name|void
name|setPatchSets
parameter_list|(
name|boolean
name|on
parameter_list|)
block|{
name|processor
operator|.
name|setIncludePatchSets
argument_list|(
name|on
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--all-approvals"
argument_list|,
name|usage
operator|=
literal|"Include information about all patch sets and approvals"
argument_list|)
DECL|method|setApprovals (boolean on)
name|void
name|setApprovals
parameter_list|(
name|boolean
name|on
parameter_list|)
block|{
if|if
condition|(
name|on
condition|)
block|{
name|processor
operator|.
name|setIncludePatchSets
argument_list|(
name|on
argument_list|)
expr_stmt|;
block|}
name|processor
operator|.
name|setIncludeApprovals
argument_list|(
name|on
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--comments"
argument_list|,
name|usage
operator|=
literal|"Include patch set and inline comments"
argument_list|)
DECL|method|setComments (boolean on)
name|void
name|setComments
parameter_list|(
name|boolean
name|on
parameter_list|)
block|{
name|processor
operator|.
name|setIncludeComments
argument_list|(
name|on
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Argument
argument_list|(
name|index
operator|=
literal|0
argument_list|,
name|required
operator|=
literal|true
argument_list|,
name|multiValued
operator|=
literal|true
argument_list|,
name|metaVar
operator|=
literal|"QUERY"
argument_list|,
name|usage
operator|=
literal|"Query to execute"
argument_list|)
DECL|field|query
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|query
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
name|processor
operator|.
name|setOutput
argument_list|(
name|out
argument_list|,
name|QueryProcessor
operator|.
name|OutputFormat
operator|.
name|TEXT
argument_list|)
expr_stmt|;
name|parseCommandLine
argument_list|()
expr_stmt|;
name|processor
operator|.
name|query
argument_list|(
name|join
argument_list|(
name|query
argument_list|,
literal|" "
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|join (List<String> list, String sep)
specifier|private
specifier|static
name|String
name|join
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|list
parameter_list|,
name|String
name|sep
parameter_list|)
block|{
name|StringBuilder
name|r
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|list
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|r
operator|.
name|append
argument_list|(
name|sep
argument_list|)
expr_stmt|;
block|}
name|r
operator|.
name|append
argument_list|(
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|r
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

