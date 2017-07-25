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
DECL|package|com.google.gerrit.server.group
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|group
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
name|gerrit
operator|.
name|common
operator|.
name|data
operator|.
name|GroupDetail
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
name|common
operator|.
name|errors
operator|.
name|NoSuchGroupException
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
name|common
operator|.
name|AccountInfo
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
name|restapi
operator|.
name|MethodNotAllowedException
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
name|restapi
operator|.
name|RestReadView
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
name|client
operator|.
name|Account
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
name|client
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
name|server
operator|.
name|account
operator|.
name|AccountLoader
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
name|GroupCache
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
name|GroupDetailFactory
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
name|api
operator|.
name|accounts
operator|.
name|AccountInfoComparator
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
name|server
operator|.
name|OrmException
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
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|org
operator|.
name|kohsuke
operator|.
name|args4j
operator|.
name|Option
import|;
end_import

begin_class
DECL|class|ListMembers
specifier|public
class|class
name|ListMembers
implements|implements
name|RestReadView
argument_list|<
name|GroupResource
argument_list|>
block|{
DECL|field|groupCache
specifier|private
specifier|final
name|GroupCache
name|groupCache
decl_stmt|;
DECL|field|groupDetailFactory
specifier|private
specifier|final
name|GroupDetailFactory
operator|.
name|Factory
name|groupDetailFactory
decl_stmt|;
DECL|field|accountLoader
specifier|private
specifier|final
name|AccountLoader
name|accountLoader
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--recursive"
argument_list|,
name|usage
operator|=
literal|"to resolve included groups recursively"
argument_list|)
DECL|field|recursive
specifier|private
name|boolean
name|recursive
decl_stmt|;
annotation|@
name|Inject
DECL|method|ListMembers ( GroupCache groupCache, GroupDetailFactory.Factory groupDetailFactory, AccountLoader.Factory accountLoaderFactory)
specifier|protected
name|ListMembers
parameter_list|(
name|GroupCache
name|groupCache
parameter_list|,
name|GroupDetailFactory
operator|.
name|Factory
name|groupDetailFactory
parameter_list|,
name|AccountLoader
operator|.
name|Factory
name|accountLoaderFactory
parameter_list|)
block|{
name|this
operator|.
name|groupCache
operator|=
name|groupCache
expr_stmt|;
name|this
operator|.
name|groupDetailFactory
operator|=
name|groupDetailFactory
expr_stmt|;
name|this
operator|.
name|accountLoader
operator|=
name|accountLoaderFactory
operator|.
name|create
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|setRecursive (boolean recursive)
specifier|public
name|ListMembers
name|setRecursive
parameter_list|(
name|boolean
name|recursive
parameter_list|)
block|{
name|this
operator|.
name|recursive
operator|=
name|recursive
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|apply (GroupResource resource)
specifier|public
name|List
argument_list|<
name|AccountInfo
argument_list|>
name|apply
parameter_list|(
name|GroupResource
name|resource
parameter_list|)
throws|throws
name|MethodNotAllowedException
throws|,
name|OrmException
block|{
if|if
condition|(
name|resource
operator|.
name|toAccountGroup
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|MethodNotAllowedException
argument_list|()
throw|;
block|}
return|return
name|apply
argument_list|(
name|resource
operator|.
name|getGroupUUID
argument_list|()
argument_list|)
return|;
block|}
DECL|method|apply (AccountGroup group)
specifier|public
name|List
argument_list|<
name|AccountInfo
argument_list|>
name|apply
parameter_list|(
name|AccountGroup
name|group
parameter_list|)
throws|throws
name|OrmException
block|{
return|return
name|apply
argument_list|(
name|group
operator|.
name|getGroupUUID
argument_list|()
argument_list|)
return|;
block|}
DECL|method|apply (AccountGroup.UUID groupId)
specifier|public
name|List
argument_list|<
name|AccountInfo
argument_list|>
name|apply
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|groupId
parameter_list|)
throws|throws
name|OrmException
block|{
specifier|final
name|Map
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|AccountInfo
argument_list|>
name|members
init|=
name|getMembers
argument_list|(
name|groupId
argument_list|,
operator|new
name|HashSet
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|AccountInfo
argument_list|>
name|memberInfos
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|members
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|memberInfos
argument_list|,
name|AccountInfoComparator
operator|.
name|ORDER_NULLS_FIRST
argument_list|)
expr_stmt|;
return|return
name|memberInfos
return|;
block|}
DECL|method|getMembers ( final AccountGroup.UUID groupUUID, HashSet<AccountGroup.UUID> seenGroups)
specifier|private
name|Map
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|AccountInfo
argument_list|>
name|getMembers
parameter_list|(
specifier|final
name|AccountGroup
operator|.
name|UUID
name|groupUUID
parameter_list|,
name|HashSet
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|seenGroups
parameter_list|)
throws|throws
name|OrmException
block|{
name|seenGroups
operator|.
name|add
argument_list|(
name|groupUUID
argument_list|)
expr_stmt|;
specifier|final
name|Map
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|AccountInfo
argument_list|>
name|members
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|AccountGroup
name|group
init|=
name|groupCache
operator|.
name|get
argument_list|(
name|groupUUID
argument_list|)
decl_stmt|;
if|if
condition|(
name|group
operator|==
literal|null
condition|)
block|{
comment|// the included group is an external group and can't be resolved
return|return
name|Collections
operator|.
name|emptyMap
argument_list|()
return|;
block|}
specifier|final
name|GroupDetail
name|groupDetail
decl_stmt|;
try|try
block|{
name|groupDetail
operator|=
name|groupDetailFactory
operator|.
name|create
argument_list|(
name|group
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|call
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchGroupException
name|e
parameter_list|)
block|{
comment|// the included group is not visible
return|return
name|Collections
operator|.
name|emptyMap
argument_list|()
return|;
block|}
for|for
control|(
name|Account
operator|.
name|Id
name|member
range|:
name|groupDetail
operator|.
name|getMembers
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|members
operator|.
name|containsKey
argument_list|(
name|member
argument_list|)
condition|)
block|{
name|members
operator|.
name|put
argument_list|(
name|member
argument_list|,
name|accountLoader
operator|.
name|get
argument_list|(
name|member
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|recursive
condition|)
block|{
for|for
control|(
name|AccountGroup
operator|.
name|UUID
name|includedGroupUuid
range|:
name|groupDetail
operator|.
name|getIncludes
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|seenGroups
operator|.
name|contains
argument_list|(
name|includedGroupUuid
argument_list|)
condition|)
block|{
name|members
operator|.
name|putAll
argument_list|(
name|getMembers
argument_list|(
name|includedGroupUuid
argument_list|,
name|seenGroups
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|accountLoader
operator|.
name|fill
argument_list|()
expr_stmt|;
return|return
name|members
return|;
block|}
block|}
end_class

end_unit

