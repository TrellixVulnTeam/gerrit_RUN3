begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2017 The Android Open Source Project
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
DECL|package|com.google.gerrit.acceptance.ssh
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|ssh
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|truth
operator|.
name|Truth
operator|.
name|assertThat
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|truth
operator|.
name|Truth
operator|.
name|assertWithMessage
import|;
end_import

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
name|group
operator|.
name|SystemGroupBackend
operator|.
name|REGISTERED_USERS
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
name|ImmutableList
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
name|ImmutableMap
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
name|flogger
operator|.
name|FluentLogger
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
name|acceptance
operator|.
name|AbstractDaemonTest
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
name|acceptance
operator|.
name|NoHttpd
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
name|acceptance
operator|.
name|Sandboxed
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
name|acceptance
operator|.
name|UseSsh
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
name|sshd
operator|.
name|Commands
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
name|Collections
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
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
annotation|@
name|NoHttpd
annotation|@
name|UseSsh
DECL|class|SshCommandsIT
specifier|public
class|class
name|SshCommandsIT
extends|extends
name|AbstractDaemonTest
block|{
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|FluentLogger
name|logger
init|=
name|FluentLogger
operator|.
name|forEnclosingClass
argument_list|()
decl_stmt|;
comment|// TODO: It would be better to dynamically generate these lists
DECL|field|COMMON_ROOT_COMMANDS
specifier|private
specifier|static
specifier|final
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|COMMON_ROOT_COMMANDS
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"apropos"
argument_list|,
literal|"close-connection"
argument_list|,
literal|"flush-caches"
argument_list|,
literal|"gc"
argument_list|,
literal|"logging"
argument_list|,
literal|"ls-groups"
argument_list|,
literal|"ls-members"
argument_list|,
literal|"ls-projects"
argument_list|,
literal|"ls-user-refs"
argument_list|,
literal|"plugin"
argument_list|,
literal|"reload-config"
argument_list|,
literal|"show-caches"
argument_list|,
literal|"show-connections"
argument_list|,
literal|"show-queue"
argument_list|,
literal|"version"
argument_list|)
decl_stmt|;
DECL|field|MASTER_ONLY_ROOT_COMMANDS
specifier|private
specifier|static
specifier|final
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|MASTER_ONLY_ROOT_COMMANDS
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"ban-commit"
argument_list|,
literal|"create-account"
argument_list|,
literal|"create-branch"
argument_list|,
literal|"create-group"
argument_list|,
literal|"create-project"
argument_list|,
literal|"gsql"
argument_list|,
literal|"index"
argument_list|,
literal|"query"
argument_list|,
literal|"receive-pack"
argument_list|,
literal|"rename-group"
argument_list|,
literal|"review"
argument_list|,
literal|"set-account"
argument_list|,
literal|"set-head"
argument_list|,
literal|"set-members"
argument_list|,
literal|"set-project"
argument_list|,
literal|"set-project-parent"
argument_list|,
literal|"set-reviewers"
argument_list|,
literal|"stream-events"
argument_list|,
literal|"test-submit"
argument_list|)
decl_stmt|;
DECL|field|MASTER_COMMANDS
specifier|private
specifier|static
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|MASTER_COMMANDS
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|Commands
operator|.
name|ROOT
argument_list|,
name|ImmutableList
operator|.
name|copyOf
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
block|{
name|addAll
parameter_list|(
name|COMMON_ROOT_COMMANDS
parameter_list|)
constructor_decl|;
name|addAll
parameter_list|(
name|MASTER_ONLY_ROOT_COMMANDS
parameter_list|)
constructor_decl|;
name|Collections
operator|.
name|sort
parameter_list|(
name|this
parameter_list|)
constructor_decl|;
block|}
block|}
argument_list|)
argument_list|,
literal|"index"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"changes"
argument_list|,
literal|"changes-in-project"
argument_list|)
argument_list|,
comment|// "activate" and "start" are not included
literal|"logging"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"ls"
argument_list|,
literal|"set"
argument_list|)
argument_list|,
literal|"plugin"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"add"
argument_list|,
literal|"enable"
argument_list|,
literal|"install"
argument_list|,
literal|"ls"
argument_list|,
literal|"reload"
argument_list|,
literal|"remove"
argument_list|,
literal|"rm"
argument_list|)
argument_list|,
literal|"test-submit"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"rule"
argument_list|,
literal|"type"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|SLAVE_COMMANDS
specifier|private
specifier|static
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|SLAVE_COMMANDS
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|Commands
operator|.
name|ROOT
argument_list|,
name|COMMON_ROOT_COMMANDS
argument_list|,
literal|"plugin"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"add"
argument_list|,
literal|"enable"
argument_list|,
literal|"install"
argument_list|,
literal|"ls"
argument_list|,
literal|"reload"
argument_list|,
literal|"remove"
argument_list|,
literal|"rm"
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|Test
annotation|@
name|Sandboxed
DECL|method|sshCommandCanBeExecuted ()
specifier|public
name|void
name|sshCommandCanBeExecuted
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Access Database capability is required to run the "gerrit gsql" command
name|allowGlobalCapabilities
argument_list|(
name|REGISTERED_USERS
argument_list|,
name|GlobalCapability
operator|.
name|ACCESS_DATABASE
argument_list|)
expr_stmt|;
name|testCommandExecution
argument_list|(
name|MASTER_COMMANDS
argument_list|)
expr_stmt|;
name|restartAsSlave
argument_list|()
expr_stmt|;
name|testCommandExecution
argument_list|(
name|SLAVE_COMMANDS
argument_list|)
expr_stmt|;
block|}
DECL|method|testCommandExecution (Map<String, List<String>> commands)
specifier|private
name|void
name|testCommandExecution
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|commands
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|String
name|root
range|:
name|commands
operator|.
name|keySet
argument_list|()
control|)
block|{
for|for
control|(
name|String
name|command
range|:
name|commands
operator|.
name|get
argument_list|(
name|root
argument_list|)
control|)
block|{
comment|// We can't assert that adminSshSession.hasError() is false, because using the --help
comment|// option causes the usage info to be written to stderr. Instead, we assert on the
comment|// content of the stderr, which will always start with "gerrit command" when the --help
comment|// option is used.
name|String
name|cmd
init|=
name|String
operator|.
name|format
argument_list|(
literal|"gerrit%s%s %s"
argument_list|,
name|root
operator|.
name|isEmpty
argument_list|()
condition|?
literal|""
else|:
literal|" "
argument_list|,
name|root
argument_list|,
name|command
argument_list|)
decl_stmt|;
name|logger
operator|.
name|atFine
argument_list|()
operator|.
name|log
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|adminSshSession
operator|.
name|exec
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%s --help"
argument_list|,
name|cmd
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|response
init|=
name|adminSshSession
operator|.
name|getError
argument_list|()
decl_stmt|;
name|assertWithMessage
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"command %s failed: %s"
argument_list|,
name|command
argument_list|,
name|response
argument_list|)
argument_list|)
operator|.
name|that
argument_list|(
name|response
argument_list|)
operator|.
name|startsWith
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|nonExistingCommandFails ()
specifier|public
name|void
name|nonExistingCommandFails
parameter_list|()
throws|throws
name|Exception
block|{
name|adminSshSession
operator|.
name|exec
argument_list|(
literal|"gerrit non-existing-command --help"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|adminSshSession
operator|.
name|getError
argument_list|()
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"fatal: gerrit: non-existing-command: not found"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Sandboxed
DECL|method|listCommands ()
specifier|public
name|void
name|listCommands
parameter_list|()
throws|throws
name|Exception
block|{
name|adminSshSession
operator|.
name|exec
argument_list|(
literal|"gerrit --help"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|commands
init|=
name|parseCommandsFromGerritHelpText
argument_list|(
name|adminSshSession
operator|.
name|getError
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|commands
argument_list|)
operator|.
name|containsExactlyElementsIn
argument_list|(
name|MASTER_COMMANDS
operator|.
name|get
argument_list|(
name|Commands
operator|.
name|ROOT
argument_list|)
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
name|restartAsSlave
argument_list|()
expr_stmt|;
name|adminSshSession
operator|.
name|exec
argument_list|(
literal|"gerrit --help"
argument_list|)
expr_stmt|;
name|commands
operator|=
name|parseCommandsFromGerritHelpText
argument_list|(
name|adminSshSession
operator|.
name|getError
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|commands
argument_list|)
operator|.
name|containsExactlyElementsIn
argument_list|(
name|SLAVE_COMMANDS
operator|.
name|get
argument_list|(
name|Commands
operator|.
name|ROOT
argument_list|)
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
block|}
DECL|method|parseCommandsFromGerritHelpText (String helpText)
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|parseCommandsFromGerritHelpText
parameter_list|(
name|String
name|helpText
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|commands
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|String
index|[]
name|lines
init|=
name|helpText
operator|.
name|split
argument_list|(
literal|"\\n"
argument_list|)
decl_stmt|;
comment|// Skip all lines including the line starting with "Available commands"
name|int
name|row
init|=
literal|0
decl_stmt|;
do|do
block|{
name|row
operator|++
expr_stmt|;
block|}
do|while
condition|(
name|row
operator|<
name|lines
operator|.
name|length
operator|&&
operator|!
name|lines
index|[
name|row
operator|-
literal|1
index|]
operator|.
name|startsWith
argument_list|(
literal|"Available commands"
argument_list|)
condition|)
do|;
comment|// Skip all empty lines
while|while
condition|(
name|lines
index|[
name|row
index|]
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|row
operator|++
expr_stmt|;
block|}
comment|// Parse commands from all lines that are indented (start with a space)
while|while
condition|(
name|row
operator|<
name|lines
operator|.
name|length
operator|&&
name|lines
index|[
name|row
index|]
operator|.
name|startsWith
argument_list|(
literal|" "
argument_list|)
condition|)
block|{
name|String
name|line
init|=
name|lines
index|[
name|row
index|]
operator|.
name|trim
argument_list|()
decl_stmt|;
comment|// Abort on empty line
if|if
condition|(
name|line
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
break|break;
block|}
comment|// Cut off command description if there is one
name|int
name|endOfCommand
init|=
name|line
operator|.
name|indexOf
argument_list|(
literal|' '
argument_list|)
decl_stmt|;
name|commands
operator|.
name|add
argument_list|(
name|endOfCommand
operator|>
literal|0
condition|?
name|line
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|line
operator|.
name|indexOf
argument_list|(
literal|' '
argument_list|)
argument_list|)
else|:
name|line
argument_list|)
expr_stmt|;
name|row
operator|++
expr_stmt|;
block|}
return|return
name|commands
return|;
block|}
block|}
end_class

end_unit

