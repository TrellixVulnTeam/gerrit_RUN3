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
DECL|package|com.google.gerrit.server
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtjsonrpc
operator|.
name|client
operator|.
name|RemoteJsonService
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
name|AbstractModule
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
name|Injector
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

begin_comment
comment|/** Creates {@link GerritJsonServlet} with a {@link RemoteJsonService}. */
end_comment

begin_class
DECL|class|GerritJsonServletProvider
class|class
name|GerritJsonServletProvider
implements|implements
name|Provider
argument_list|<
name|GerritJsonServlet
argument_list|>
block|{
annotation|@
name|Inject
DECL|field|injector
specifier|private
name|Injector
name|injector
decl_stmt|;
DECL|field|serviceClass
specifier|private
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|RemoteJsonService
argument_list|>
name|serviceClass
decl_stmt|;
DECL|method|GerritJsonServletProvider (final Class<? extends RemoteJsonService> c)
name|GerritJsonServletProvider
parameter_list|(
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|RemoteJsonService
argument_list|>
name|c
parameter_list|)
block|{
name|serviceClass
operator|=
name|c
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get ()
specifier|public
name|GerritJsonServlet
name|get
parameter_list|()
block|{
specifier|final
name|RemoteJsonService
name|srv
init|=
name|injector
operator|.
name|getInstance
argument_list|(
name|serviceClass
argument_list|)
decl_stmt|;
return|return
name|injector
operator|.
name|createChildInjector
argument_list|(
operator|new
name|AbstractModule
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|bind
argument_list|(
name|RemoteJsonService
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|srv
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
operator|.
name|getInstance
argument_list|(
name|GerritJsonServlet
operator|.
name|class
argument_list|)
return|;
block|}
block|}
end_class

end_unit

