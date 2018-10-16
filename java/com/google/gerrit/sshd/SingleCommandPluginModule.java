begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2013 The Android Open Source Project
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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkState
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
name|PluginName
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
name|binder
operator|.
name|LinkedBindingBuilder
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
name|command
operator|.
name|Command
import|;
end_import

begin_comment
comment|/**  * Binds one SSH command to the plugin name itself.  *  *<p>Cannot be combined with {@link PluginCommandModule}.  */
end_comment

begin_class
DECL|class|SingleCommandPluginModule
specifier|public
specifier|abstract
class|class
name|SingleCommandPluginModule
extends|extends
name|CommandModule
block|{
DECL|field|command
specifier|private
name|CommandName
name|command
decl_stmt|;
annotation|@
name|Inject
DECL|method|setPluginName (@luginName String name)
name|void
name|setPluginName
parameter_list|(
annotation|@
name|PluginName
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|command
operator|=
name|Commands
operator|.
name|named
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|configure ()
specifier|protected
specifier|final
name|void
name|configure
parameter_list|()
block|{
name|checkState
argument_list|(
name|command
operator|!=
literal|null
argument_list|,
literal|"@PluginName must be provided"
argument_list|)
expr_stmt|;
name|configure
argument_list|(
name|bind
argument_list|(
name|Commands
operator|.
name|key
argument_list|(
name|command
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|configure (LinkedBindingBuilder<Command> bind)
specifier|protected
specifier|abstract
name|void
name|configure
parameter_list|(
name|LinkedBindingBuilder
argument_list|<
name|Command
argument_list|>
name|bind
parameter_list|)
function_decl|;
block|}
end_class

end_unit

