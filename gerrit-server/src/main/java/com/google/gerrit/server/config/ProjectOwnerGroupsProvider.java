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
name|reviewdb
operator|.
name|AccountGroup
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
name|reviewdb
operator|.
name|ReviewDb
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|client
operator|.
name|SchemaFactory
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

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
implements|implements
name|Provider
argument_list|<
name|Set
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
argument_list|>
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ProjectOwnerGroupsProvider
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|groupIds
specifier|private
specifier|final
name|Set
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
name|groupIds
decl_stmt|;
annotation|@
name|Inject
DECL|method|ProjectOwnerGroupsProvider (@erritServerConfig final Config config, SchemaFactory<ReviewDb> db, @ProjectCreatorGroups Set<AccountGroup.Id> creatorGroups)
name|ProjectOwnerGroupsProvider
parameter_list|(
annotation|@
name|GerritServerConfig
specifier|final
name|Config
name|config
parameter_list|,
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|,
annotation|@
name|ProjectCreatorGroups
name|Set
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
name|creatorGroups
parameter_list|)
block|{
name|String
index|[]
name|names
init|=
name|config
operator|.
name|getStringList
argument_list|(
literal|"repository"
argument_list|,
literal|"*"
argument_list|,
literal|"ownerGroup"
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
name|ownerGroups
init|=
name|ConfigUtil
operator|.
name|groupsFor
argument_list|(
name|db
argument_list|,
name|names
argument_list|,
name|log
argument_list|)
decl_stmt|;
if|if
condition|(
name|ownerGroups
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|groupIds
operator|=
name|creatorGroups
expr_stmt|;
block|}
else|else
block|{
name|groupIds
operator|=
name|ownerGroups
expr_stmt|;
block|}
block|}
DECL|method|get ()
specifier|public
name|Set
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
name|get
parameter_list|()
block|{
return|return
name|groupIds
return|;
block|}
block|}
end_class

end_unit

