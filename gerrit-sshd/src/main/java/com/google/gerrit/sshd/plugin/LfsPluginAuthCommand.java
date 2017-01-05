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
DECL|package|com.google.gerrit.sshd.plugin
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|sshd
operator|.
name|plugin
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
name|extensions
operator|.
name|registration
operator|.
name|DynamicItem
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
name|sshd
operator|.
name|CommandModule
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

begin_class
DECL|class|LfsPluginAuthCommand
specifier|public
class|class
name|LfsPluginAuthCommand
extends|extends
name|SshCommand
block|{
DECL|interface|LfsSshPluginAuth
specifier|public
interface|interface
name|LfsSshPluginAuth
block|{
DECL|method|authenticate (CurrentUser user, List<String> args)
name|String
name|authenticate
parameter_list|(
name|CurrentUser
name|user
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|args
parameter_list|)
throws|throws
name|UnloggedFailure
throws|,
name|Failure
function_decl|;
block|}
DECL|class|Module
specifier|public
specifier|static
class|class
name|Module
extends|extends
name|CommandModule
block|{
annotation|@
name|Override
DECL|method|configure ()
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|command
argument_list|(
literal|"git-lfs-authenticate"
argument_list|)
operator|.
name|to
argument_list|(
name|LfsPluginAuthCommand
operator|.
name|class
argument_list|)
expr_stmt|;
name|DynamicItem
operator|.
name|itemOf
argument_list|(
name|binder
argument_list|()
argument_list|,
name|LfsSshPluginAuth
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|auth
specifier|private
specifier|final
name|DynamicItem
argument_list|<
name|LfsSshPluginAuth
argument_list|>
name|auth
decl_stmt|;
DECL|field|user
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|user
decl_stmt|;
annotation|@
name|Argument
argument_list|(
name|index
operator|=
literal|0
argument_list|,
name|multiValued
operator|=
literal|true
argument_list|,
name|metaVar
operator|=
literal|"PARAMS"
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
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Inject
DECL|method|LfsPluginAuthCommand (DynamicItem<LfsSshPluginAuth> auth, Provider<CurrentUser> user)
name|LfsPluginAuthCommand
parameter_list|(
name|DynamicItem
argument_list|<
name|LfsSshPluginAuth
argument_list|>
name|auth
parameter_list|,
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|user
parameter_list|)
block|{
name|this
operator|.
name|auth
operator|=
name|auth
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
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
name|UnloggedFailure
throws|,
name|Failure
throws|,
name|Exception
block|{
name|LfsSshPluginAuth
name|pluginAuth
init|=
name|auth
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|pluginAuth
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
literal|1
argument_list|,
literal|"Server configuration error:"
operator|+
literal|" LFS auth over SSH is not properly configured."
argument_list|)
throw|;
block|}
name|stdout
operator|.
name|print
argument_list|(
name|pluginAuth
operator|.
name|authenticate
argument_list|(
name|user
operator|.
name|get
argument_list|()
argument_list|,
name|args
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

