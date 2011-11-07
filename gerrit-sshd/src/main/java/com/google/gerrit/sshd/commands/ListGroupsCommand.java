begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2011 The Android Open Source Project
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
name|common
operator|.
name|data
operator|.
name|GroupDetail
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
name|GroupList
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
name|errors
operator|.
name|NoSuchGroupException
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
name|VisibleGroups
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
name|gwtorm
operator|.
name|client
operator|.
name|OrmException
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

begin_class
DECL|class|ListGroupsCommand
specifier|public
class|class
name|ListGroupsCommand
extends|extends
name|BaseCommand
block|{
annotation|@
name|Inject
DECL|field|visibleGroupsFactory
specifier|private
name|VisibleGroups
operator|.
name|Factory
name|visibleGroupsFactory
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
name|parseCommandLine
argument_list|()
expr_stmt|;
name|ListGroupsCommand
operator|.
name|this
operator|.
name|display
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|display ()
specifier|private
name|void
name|display
parameter_list|()
throws|throws
name|Failure
block|{
specifier|final
name|PrintWriter
name|stdout
init|=
name|toPrintWriter
argument_list|(
name|out
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|GroupList
name|visibleGroups
init|=
name|visibleGroupsFactory
operator|.
name|create
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|GroupDetail
name|groupDetail
range|:
name|visibleGroups
operator|.
name|getGroups
argument_list|()
control|)
block|{
name|stdout
operator|.
name|print
argument_list|(
name|groupDetail
operator|.
name|group
operator|.
name|getName
argument_list|()
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|OrmException
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
name|NoSuchGroupException
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
end_class

end_unit

