begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2014 The Android Open Source Project
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
name|common
operator|.
name|base
operator|.
name|Function
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
name|base
operator|.
name|Joiner
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
name|Iterables
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_class
DECL|class|MultipleProvidersForPluginException
class|class
name|MultipleProvidersForPluginException
extends|extends
name|IllegalArgumentException
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|method|MultipleProvidersForPluginException (File pluginSrcFile, Iterable<ServerPluginProvider> providersHandlers)
name|MultipleProvidersForPluginException
parameter_list|(
name|File
name|pluginSrcFile
parameter_list|,
name|Iterable
argument_list|<
name|ServerPluginProvider
argument_list|>
name|providersHandlers
parameter_list|)
block|{
name|super
argument_list|(
name|pluginSrcFile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" is claimed to be handled by more than one plugin provider: "
operator|+
name|providersListToString
argument_list|(
name|providersHandlers
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|providersListToString ( Iterable<ServerPluginProvider> providersHandlers)
specifier|private
specifier|static
name|String
name|providersListToString
parameter_list|(
name|Iterable
argument_list|<
name|ServerPluginProvider
argument_list|>
name|providersHandlers
parameter_list|)
block|{
name|Iterable
argument_list|<
name|String
argument_list|>
name|providerNames
init|=
name|Iterables
operator|.
name|transform
argument_list|(
name|providersHandlers
argument_list|,
operator|new
name|Function
argument_list|<
name|ServerPluginProvider
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
name|ServerPluginProvider
name|provider
parameter_list|)
block|{
return|return
name|provider
operator|.
name|getProviderPluginName
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
return|return
name|Joiner
operator|.
name|on
argument_list|(
literal|", "
argument_list|)
operator|.
name|join
argument_list|(
name|providerNames
argument_list|)
return|;
block|}
block|}
end_class

end_unit

