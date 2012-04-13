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
DECL|package|com.google.gerrit.sshd
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|sshd
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
name|Sets
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
name|util
operator|.
name|concurrent
operator|.
name|Atomics
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
name|account
operator|.
name|CapabilityControl
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
name|args4j
operator|.
name|SubcommandHandler
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
name|assistedinject
operator|.
name|Assisted
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
name|Command
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
name|atomic
operator|.
name|AtomicReference
import|;
end_import

begin_comment
comment|/**  * Command that dispatches to a subcommand from its command table.  */
end_comment

begin_class
DECL|class|DispatchCommand
specifier|final
class|class
name|DispatchCommand
extends|extends
name|BaseCommand
block|{
DECL|interface|Factory
interface|interface
name|Factory
block|{
DECL|method|create (Map<String, Provider<Command>> map)
name|DispatchCommand
name|create
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Provider
argument_list|<
name|Command
argument_list|>
argument_list|>
name|map
parameter_list|)
function_decl|;
block|}
DECL|field|currentUser
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|currentUser
decl_stmt|;
DECL|field|commands
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Provider
argument_list|<
name|Command
argument_list|>
argument_list|>
name|commands
decl_stmt|;
DECL|field|atomicCmd
specifier|private
specifier|final
name|AtomicReference
argument_list|<
name|Command
argument_list|>
name|atomicCmd
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
literal|true
argument_list|,
name|metaVar
operator|=
literal|"COMMAND"
argument_list|,
name|handler
operator|=
name|SubcommandHandler
operator|.
name|class
argument_list|)
DECL|field|commandName
specifier|private
name|String
name|commandName
decl_stmt|;
annotation|@
name|Argument
argument_list|(
name|index
operator|=
literal|1
argument_list|,
name|multiValued
operator|=
literal|true
argument_list|,
name|metaVar
operator|=
literal|"ARG"
argument_list|)
DECL|field|args
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|args
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Inject
DECL|method|DispatchCommand (final Provider<CurrentUser> cu, @Assisted final Map<String, Provider<Command>> all)
name|DispatchCommand
parameter_list|(
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|cu
parameter_list|,
annotation|@
name|Assisted
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Provider
argument_list|<
name|Command
argument_list|>
argument_list|>
name|all
parameter_list|)
block|{
name|currentUser
operator|=
name|cu
expr_stmt|;
name|commands
operator|=
name|all
expr_stmt|;
name|atomicCmd
operator|=
name|Atomics
operator|.
name|newReference
argument_list|()
expr_stmt|;
block|}
DECL|method|getMap ()
name|Map
argument_list|<
name|String
argument_list|,
name|Provider
argument_list|<
name|Command
argument_list|>
argument_list|>
name|getMap
parameter_list|()
block|{
return|return
name|commands
return|;
block|}
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
try|try
block|{
name|parseCommandLine
argument_list|()
expr_stmt|;
specifier|final
name|Provider
argument_list|<
name|Command
argument_list|>
name|p
init|=
name|commands
operator|.
name|get
argument_list|(
name|commandName
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
name|String
name|msg
init|=
operator|(
name|getName
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|?
literal|"Gerrit Code Review"
else|:
name|getName
argument_list|()
operator|)
operator|+
literal|": "
operator|+
name|commandName
operator|+
literal|": not found"
decl_stmt|;
throw|throw
operator|new
name|UnloggedFailure
argument_list|(
literal|1
argument_list|,
name|msg
argument_list|)
throw|;
block|}
specifier|final
name|Command
name|cmd
init|=
name|p
operator|.
name|get
argument_list|()
decl_stmt|;
name|checkRequiresCapability
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
if|if
condition|(
name|cmd
operator|instanceof
name|BaseCommand
condition|)
block|{
specifier|final
name|BaseCommand
name|bc
init|=
operator|(
name|BaseCommand
operator|)
name|cmd
decl_stmt|;
if|if
condition|(
name|getName
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
name|bc
operator|.
name|setName
argument_list|(
name|commandName
argument_list|)
expr_stmt|;
else|else
name|bc
operator|.
name|setName
argument_list|(
name|getName
argument_list|()
operator|+
literal|" "
operator|+
name|commandName
argument_list|)
expr_stmt|;
name|bc
operator|.
name|setArguments
argument_list|(
name|args
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|args
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|args
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
name|commandName
operator|+
literal|" does not take arguments"
argument_list|)
throw|;
block|}
name|provideStateTo
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|atomicCmd
operator|.
name|set
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|start
argument_list|(
name|env
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnloggedFailure
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|msg
operator|.
name|endsWith
argument_list|(
literal|"\n"
argument_list|)
condition|)
block|{
name|msg
operator|+=
literal|"\n"
expr_stmt|;
block|}
name|err
operator|.
name|write
argument_list|(
name|msg
operator|.
name|getBytes
argument_list|(
name|ENC
argument_list|)
argument_list|)
expr_stmt|;
name|err
operator|.
name|flush
argument_list|()
expr_stmt|;
name|onExit
argument_list|(
name|e
operator|.
name|exitCode
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|checkRequiresCapability (Command cmd)
specifier|private
name|void
name|checkRequiresCapability
parameter_list|(
name|Command
name|cmd
parameter_list|)
throws|throws
name|UnloggedFailure
block|{
name|RequiresCapability
name|rc
init|=
name|cmd
operator|.
name|getClass
argument_list|()
operator|.
name|getAnnotation
argument_list|(
name|RequiresCapability
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|rc
operator|!=
literal|null
condition|)
block|{
name|CurrentUser
name|user
init|=
name|currentUser
operator|.
name|get
argument_list|()
decl_stmt|;
name|CapabilityControl
name|ctl
init|=
name|user
operator|.
name|getCapabilities
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|ctl
operator|.
name|canPerform
argument_list|(
name|rc
operator|.
name|value
argument_list|()
argument_list|)
operator|&&
operator|!
name|ctl
operator|.
name|canAdministrateServer
argument_list|()
condition|)
block|{
name|String
name|msg
init|=
name|String
operator|.
name|format
argument_list|(
literal|"fatal: %s does not have \"%s\" capability."
argument_list|,
name|user
operator|.
name|getUserName
argument_list|()
argument_list|,
name|rc
operator|.
name|value
argument_list|()
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|UnloggedFailure
argument_list|(
name|BaseCommand
operator|.
name|STATUS_NOT_ADMIN
argument_list|,
name|msg
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|destroy ()
specifier|public
name|void
name|destroy
parameter_list|()
block|{
name|Command
name|cmd
init|=
name|atomicCmd
operator|.
name|getAndSet
argument_list|(
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmd
operator|!=
literal|null
condition|)
block|{
name|cmd
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|usage ()
specifier|protected
name|String
name|usage
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|usage
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|usage
operator|.
name|append
argument_list|(
literal|"Available commands"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|getName
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|usage
operator|.
name|append
argument_list|(
literal|" of "
argument_list|)
expr_stmt|;
name|usage
operator|.
name|append
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|usage
operator|.
name|append
argument_list|(
literal|" are:\n"
argument_list|)
expr_stmt|;
name|usage
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|name
range|:
name|Sets
operator|.
name|newTreeSet
argument_list|(
name|commands
operator|.
name|keySet
argument_list|()
argument_list|)
control|)
block|{
name|usage
operator|.
name|append
argument_list|(
literal|"   "
argument_list|)
expr_stmt|;
name|usage
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|usage
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
name|usage
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|usage
operator|.
name|append
argument_list|(
literal|"See '"
argument_list|)
expr_stmt|;
if|if
condition|(
name|getName
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|' '
argument_list|)
operator|<
literal|0
condition|)
block|{
name|usage
operator|.
name|append
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|usage
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|usage
operator|.
name|append
argument_list|(
literal|"COMMAND --help' for more information.\n"
argument_list|)
expr_stmt|;
name|usage
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
return|return
name|usage
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getCommandName ()
specifier|public
name|String
name|getCommandName
parameter_list|()
block|{
return|return
name|commandName
return|;
block|}
block|}
end_class

end_unit

