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
DECL|package|com.google.gerrit.server.plugins
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|plugins
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
name|annotations
operator|.
name|Export
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
name|Module
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
name|TypeLiteral
import|;
end_import

begin_interface
DECL|interface|ModuleGenerator
specifier|public
interface|interface
name|ModuleGenerator
block|{
DECL|method|setPluginName (String name)
name|void
name|setPluginName
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
DECL|method|export (Export export, Class<?> type)
name|void
name|export
parameter_list|(
name|Export
name|export
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|type
parameter_list|)
throws|throws
name|InvalidPluginException
function_decl|;
DECL|method|listen (TypeLiteral<?> tl, Class<?> clazz)
name|void
name|listen
parameter_list|(
name|TypeLiteral
argument_list|<
name|?
argument_list|>
name|tl
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
function_decl|;
DECL|method|create ()
name|Module
name|create
parameter_list|()
throws|throws
name|InvalidPluginException
function_decl|;
DECL|class|NOP
specifier|static
class|class
name|NOP
implements|implements
name|ModuleGenerator
block|{
annotation|@
name|Override
DECL|method|setPluginName (String name)
specifier|public
name|void
name|setPluginName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
comment|// do nothing
block|}
annotation|@
name|Override
DECL|method|listen (TypeLiteral<?> tl, Class<?> clazz)
specifier|public
name|void
name|listen
parameter_list|(
name|TypeLiteral
argument_list|<
name|?
argument_list|>
name|tl
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
block|{
comment|// do nothing
block|}
annotation|@
name|Override
DECL|method|export (Export export, Class<?> type)
specifier|public
name|void
name|export
parameter_list|(
name|Export
name|export
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|type
parameter_list|)
block|{
comment|// do nothing
block|}
annotation|@
name|Override
DECL|method|create ()
specifier|public
name|Module
name|create
parameter_list|()
throws|throws
name|InvalidPluginException
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_interface

end_unit

