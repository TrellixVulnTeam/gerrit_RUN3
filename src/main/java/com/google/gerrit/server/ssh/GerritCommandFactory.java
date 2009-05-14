begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2008 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.ssh
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|ssh
package|;
end_package

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
name|CommandFactory
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

begin_comment
comment|/** Creates a command implementation based on the client input. */
end_comment

begin_class
DECL|class|GerritCommandFactory
class|class
name|GerritCommandFactory
implements|implements
name|CommandFactory
block|{
DECL|field|commands
specifier|private
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|Factory
argument_list|>
name|commands
decl_stmt|;
DECL|method|GerritCommandFactory ()
name|GerritCommandFactory
parameter_list|()
block|{
name|commands
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Factory
argument_list|>
argument_list|()
expr_stmt|;
name|commands
operator|.
name|put
argument_list|(
literal|"gerrit-upload-pack"
argument_list|,
operator|new
name|Factory
argument_list|()
block|{
specifier|public
name|AbstractCommand
name|create
parameter_list|()
block|{
return|return
operator|new
name|Upload
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|commands
operator|.
name|put
argument_list|(
literal|"gerrit-receive-pack"
argument_list|,
operator|new
name|Factory
argument_list|()
block|{
specifier|public
name|AbstractCommand
name|create
parameter_list|()
block|{
return|return
operator|new
name|Receive
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|commands
operator|.
name|put
argument_list|(
literal|"gerrit-flush-caches"
argument_list|,
operator|new
name|Factory
argument_list|()
block|{
specifier|public
name|AbstractCommand
name|create
parameter_list|()
block|{
return|return
operator|new
name|AdminFlushCaches
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|commands
operator|.
name|put
argument_list|(
literal|"gerrit-ls-projects"
argument_list|,
operator|new
name|Factory
argument_list|()
block|{
specifier|public
name|AbstractCommand
name|create
parameter_list|()
block|{
return|return
operator|new
name|ListProjects
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|commands
operator|.
name|put
argument_list|(
literal|"gerrit-show-caches"
argument_list|,
operator|new
name|Factory
argument_list|()
block|{
specifier|public
name|AbstractCommand
name|create
parameter_list|()
block|{
return|return
operator|new
name|AdminShowCaches
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|commands
operator|.
name|put
argument_list|(
literal|"gerrit-show-connections"
argument_list|,
operator|new
name|Factory
argument_list|()
block|{
specifier|public
name|AbstractCommand
name|create
parameter_list|()
block|{
return|return
operator|new
name|AdminShowConnections
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|alias
argument_list|(
literal|"gerrit-upload-pack"
argument_list|,
literal|"git-upload-pack"
argument_list|)
expr_stmt|;
name|alias
argument_list|(
literal|"gerrit-receive-pack"
argument_list|,
literal|"git-receive-pack"
argument_list|)
expr_stmt|;
block|}
DECL|method|alias (final String from, final String to)
specifier|private
name|void
name|alias
parameter_list|(
specifier|final
name|String
name|from
parameter_list|,
specifier|final
name|String
name|to
parameter_list|)
block|{
name|commands
operator|.
name|put
argument_list|(
name|to
argument_list|,
name|commands
operator|.
name|get
argument_list|(
name|from
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|createCommand (final String commandLine)
specifier|public
name|Command
name|createCommand
parameter_list|(
specifier|final
name|String
name|commandLine
parameter_list|)
block|{
specifier|final
name|int
name|sp1
init|=
name|commandLine
operator|.
name|indexOf
argument_list|(
literal|' '
argument_list|)
decl_stmt|;
name|String
name|cmd
decl_stmt|,
name|args
decl_stmt|;
if|if
condition|(
literal|0
operator|<
name|sp1
condition|)
block|{
name|cmd
operator|=
name|commandLine
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|sp1
argument_list|)
expr_stmt|;
name|args
operator|=
name|commandLine
operator|.
name|substring
argument_list|(
name|sp1
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|cmd
operator|=
name|commandLine
expr_stmt|;
name|args
operator|=
literal|""
expr_stmt|;
block|}
comment|// Support newer-style "git receive-pack" requests by converting
comment|// to the older-style "git-receive-pack".
comment|//
if|if
condition|(
literal|"git"
operator|.
name|equals
argument_list|(
name|cmd
argument_list|)
operator|||
literal|"gerrit"
operator|.
name|equals
argument_list|(
name|cmd
argument_list|)
condition|)
block|{
name|cmd
operator|+=
literal|"-"
expr_stmt|;
specifier|final
name|int
name|sp2
init|=
name|args
operator|.
name|indexOf
argument_list|(
literal|' '
argument_list|)
decl_stmt|;
if|if
condition|(
literal|0
operator|<
name|sp2
condition|)
block|{
name|cmd
operator|+=
name|args
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|sp2
argument_list|)
expr_stmt|;
name|args
operator|=
name|args
operator|.
name|substring
argument_list|(
name|sp2
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|cmd
operator|+=
name|args
expr_stmt|;
name|args
operator|=
literal|""
expr_stmt|;
block|}
block|}
specifier|final
name|AbstractCommand
name|c
init|=
name|create
argument_list|(
name|cmd
argument_list|)
decl_stmt|;
name|c
operator|.
name|setCommandLine
argument_list|(
name|cmd
argument_list|,
name|args
argument_list|)
expr_stmt|;
return|return
name|c
return|;
block|}
DECL|method|create (final String cmd)
specifier|private
name|AbstractCommand
name|create
parameter_list|(
specifier|final
name|String
name|cmd
parameter_list|)
block|{
specifier|final
name|Factory
name|f
init|=
name|commands
operator|.
name|get
argument_list|(
name|cmd
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|!=
literal|null
condition|)
block|{
return|return
name|f
operator|.
name|create
argument_list|()
return|;
block|}
return|return
operator|new
name|AbstractCommand
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|run
parameter_list|()
throws|throws
name|Failure
block|{
throw|throw
operator|new
name|UnloggedFailure
argument_list|(
literal|127
argument_list|,
literal|"gerrit: "
operator|+
name|getName
argument_list|()
operator|+
literal|": not found"
argument_list|)
throw|;
block|}
block|}
return|;
block|}
DECL|interface|Factory
specifier|protected
specifier|static
interface|interface
name|Factory
block|{
DECL|method|create ()
name|AbstractCommand
name|create
parameter_list|()
function_decl|;
block|}
block|}
end_class

end_unit

