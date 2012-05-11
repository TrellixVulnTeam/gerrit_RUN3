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
name|Lists
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
name|inject
operator|.
name|Provider
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
name|LinkedList
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
comment|/** Command that executes some other command. */
end_comment

begin_class
DECL|class|AliasCommand
specifier|public
class|class
name|AliasCommand
extends|extends
name|BaseCommand
block|{
DECL|field|root
specifier|private
specifier|final
name|DispatchCommandProvider
name|root
decl_stmt|;
DECL|field|currentUser
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|currentUser
decl_stmt|;
DECL|field|command
specifier|private
specifier|final
name|CommandName
name|command
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
DECL|method|AliasCommand (@ommandNameCommands.ROOT) DispatchCommandProvider root, Provider<CurrentUser> currentUser, CommandName command)
name|AliasCommand
parameter_list|(
annotation|@
name|CommandName
argument_list|(
name|Commands
operator|.
name|ROOT
argument_list|)
name|DispatchCommandProvider
name|root
parameter_list|,
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|currentUser
parameter_list|,
name|CommandName
name|command
parameter_list|)
block|{
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
name|this
operator|.
name|currentUser
operator|=
name|currentUser
expr_stmt|;
name|this
operator|.
name|command
operator|=
name|command
expr_stmt|;
name|this
operator|.
name|atomicCmd
operator|=
name|Atomics
operator|.
name|newReference
argument_list|()
expr_stmt|;
block|}
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
try|try
block|{
name|begin
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
DECL|method|begin (Environment env)
specifier|private
name|void
name|begin
parameter_list|(
name|Environment
name|env
parameter_list|)
throws|throws
name|UnloggedFailure
throws|,
name|IOException
block|{
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
init|=
name|root
operator|.
name|getMap
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|chain
argument_list|(
name|command
argument_list|)
control|)
block|{
name|Provider
argument_list|<
name|?
extends|extends
name|Command
argument_list|>
name|p
init|=
name|map
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|UnloggedFailure
argument_list|(
literal|1
argument_list|,
name|getName
argument_list|()
operator|+
literal|": not found"
argument_list|)
throw|;
block|}
name|Command
name|cmd
init|=
name|p
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|cmd
operator|instanceof
name|DispatchCommand
operator|)
condition|)
block|{
throw|throw
operator|new
name|UnloggedFailure
argument_list|(
literal|1
argument_list|,
name|getName
argument_list|()
operator|+
literal|": not found"
argument_list|)
throw|;
block|}
name|map
operator|=
operator|(
operator|(
name|DispatchCommand
operator|)
name|cmd
operator|)
operator|.
name|getMap
argument_list|()
expr_stmt|;
block|}
name|Provider
argument_list|<
name|?
extends|extends
name|Command
argument_list|>
name|p
init|=
name|map
operator|.
name|get
argument_list|(
name|command
operator|.
name|value
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|UnloggedFailure
argument_list|(
literal|1
argument_list|,
name|getName
argument_list|()
operator|+
literal|": not found"
argument_list|)
throw|;
block|}
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
name|BaseCommand
name|bc
init|=
operator|(
name|BaseCommand
operator|)
name|cmd
decl_stmt|;
name|bc
operator|.
name|setName
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|bc
operator|.
name|setArguments
argument_list|(
name|getArguments
argument_list|()
argument_list|)
expr_stmt|;
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
DECL|method|chain (CommandName command)
specifier|private
specifier|static
name|LinkedList
argument_list|<
name|String
argument_list|>
name|chain
parameter_list|(
name|CommandName
name|command
parameter_list|)
block|{
name|LinkedList
argument_list|<
name|String
argument_list|>
name|chain
init|=
name|Lists
operator|.
name|newLinkedList
argument_list|()
decl_stmt|;
while|while
condition|(
name|command
operator|!=
literal|null
condition|)
block|{
name|chain
operator|.
name|addFirst
argument_list|(
name|command
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
name|command
operator|=
name|Commands
operator|.
name|parentOf
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
name|chain
operator|.
name|removeLast
argument_list|()
expr_stmt|;
return|return
name|chain
return|;
block|}
block|}
end_class

end_unit

