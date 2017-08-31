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
DECL|package|com.google.gerrit.server.account
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|account
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
name|common
operator|.
name|AccountVisibility
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
name|config
operator|.
name|GerritServerConfig
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
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Config
import|;
end_import

begin_class
DECL|class|AccountVisibilityProvider
specifier|public
class|class
name|AccountVisibilityProvider
implements|implements
name|Provider
argument_list|<
name|AccountVisibility
argument_list|>
block|{
DECL|field|accountVisibility
specifier|private
specifier|final
name|AccountVisibility
name|accountVisibility
decl_stmt|;
annotation|@
name|Inject
DECL|method|AccountVisibilityProvider (@erritServerConfig Config cfg)
name|AccountVisibilityProvider
parameter_list|(
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|)
block|{
name|AccountVisibility
name|av
decl_stmt|;
if|if
condition|(
name|cfg
operator|.
name|getString
argument_list|(
literal|"accounts"
argument_list|,
literal|null
argument_list|,
literal|"visibility"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|av
operator|=
name|cfg
operator|.
name|getEnum
argument_list|(
literal|"accounts"
argument_list|,
literal|null
argument_list|,
literal|"visibility"
argument_list|,
name|AccountVisibility
operator|.
name|ALL
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|av
operator|=
name|AccountVisibility
operator|.
name|ALL
expr_stmt|;
block|}
name|accountVisibility
operator|=
name|av
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get ()
specifier|public
name|AccountVisibility
name|get
parameter_list|()
block|{
return|return
name|accountVisibility
return|;
block|}
block|}
end_class

end_unit

