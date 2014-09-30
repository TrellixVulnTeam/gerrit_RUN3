begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2010 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.config
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|config
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
name|server
operator|.
name|account
operator|.
name|GroupBackend
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
name|util
operator|.
name|ServerRequestContext
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
name|util
operator|.
name|ThreadLocalRequestContext
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
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Config
import|;
end_import

begin_comment
comment|/**  * Provider of the group(s) which should become owners of a newly created  * project. Currently only supports {@code ownerGroup} declarations in the  * {@code "*"} repository, like so:  *  *<pre>  * [repository&quot;*&quot;]  *     ownerGroup = Registered Users  *     ownerGroup = Administrators  *</pre>  */
end_comment

begin_class
DECL|class|ProjectOwnerGroupsProvider
specifier|public
class|class
name|ProjectOwnerGroupsProvider
extends|extends
name|GroupSetProvider
block|{
annotation|@
name|Inject
DECL|method|ProjectOwnerGroupsProvider (GroupBackend gb, @GerritServerConfig final Config config, ThreadLocalRequestContext context, ServerRequestContext serverCtx)
specifier|public
name|ProjectOwnerGroupsProvider
parameter_list|(
name|GroupBackend
name|gb
parameter_list|,
annotation|@
name|GerritServerConfig
specifier|final
name|Config
name|config
parameter_list|,
name|ThreadLocalRequestContext
name|context
parameter_list|,
name|ServerRequestContext
name|serverCtx
parameter_list|)
block|{
name|super
argument_list|(
name|gb
argument_list|,
name|config
argument_list|,
name|context
argument_list|,
name|serverCtx
argument_list|,
literal|"repository"
argument_list|,
literal|"*"
argument_list|,
literal|"ownerGroup"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

