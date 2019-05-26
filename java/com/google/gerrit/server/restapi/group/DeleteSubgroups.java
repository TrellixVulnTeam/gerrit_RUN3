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
DECL|package|com.google.gerrit.server.restapi.group
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|restapi
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
name|ImmutableList
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
name|Sets
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
name|GroupDescription
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
name|entities
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
name|exceptions
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
name|restapi
operator|.
name|AuthException
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
name|ResourceNotFoundException
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
name|Response
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
name|RestModifyView
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
name|UnprocessableEntityException
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
name|UserInitiated
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
name|GroupControl
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
name|group
operator|.
name|GroupResolver
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
name|group
operator|.
name|GroupResource
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
name|group
operator|.
name|SubgroupResource
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
name|group
operator|.
name|db
operator|.
name|GroupsUpdate
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
name|group
operator|.
name|db
operator|.
name|InternalGroupUpdate
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
name|restapi
operator|.
name|group
operator|.
name|AddSubgroups
operator|.
name|Input
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Singleton
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
name|HashSet
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

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|errors
operator|.
name|ConfigInvalidException
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|DeleteSubgroups
specifier|public
class|class
name|DeleteSubgroups
implements|implements
name|RestModifyView
argument_list|<
name|GroupResource
argument_list|,
name|Input
argument_list|>
block|{
DECL|field|groupResolver
specifier|private
specifier|final
name|GroupResolver
name|groupResolver
decl_stmt|;
DECL|field|groupsUpdateProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|GroupsUpdate
argument_list|>
name|groupsUpdateProvider
decl_stmt|;
annotation|@
name|Inject
DECL|method|DeleteSubgroups ( GroupResolver groupResolver, @UserInitiated Provider<GroupsUpdate> groupsUpdateProvider)
name|DeleteSubgroups
parameter_list|(
name|GroupResolver
name|groupResolver
parameter_list|,
annotation|@
name|UserInitiated
name|Provider
argument_list|<
name|GroupsUpdate
argument_list|>
name|groupsUpdateProvider
parameter_list|)
block|{
name|this
operator|.
name|groupResolver
operator|=
name|groupResolver
expr_stmt|;
name|this
operator|.
name|groupsUpdateProvider
operator|=
name|groupsUpdateProvider
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (GroupResource resource, Input input)
specifier|public
name|Response
argument_list|<
name|?
argument_list|>
name|apply
parameter_list|(
name|GroupResource
name|resource
parameter_list|,
name|Input
name|input
parameter_list|)
throws|throws
name|AuthException
throws|,
name|NotInternalGroupException
throws|,
name|UnprocessableEntityException
throws|,
name|ResourceNotFoundException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|GroupDescription
operator|.
name|Internal
name|internalGroup
init|=
name|resource
operator|.
name|asInternalGroup
argument_list|()
operator|.
name|orElseThrow
argument_list|(
name|NotInternalGroupException
operator|::
operator|new
argument_list|)
decl_stmt|;
name|input
operator|=
name|Input
operator|.
name|init
argument_list|(
name|input
argument_list|)
expr_stmt|;
specifier|final
name|GroupControl
name|control
init|=
name|resource
operator|.
name|getControl
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|control
operator|.
name|canRemoveGroup
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AuthException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Cannot delete groups from group %s"
argument_list|,
name|internalGroup
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|subgroupsToRemove
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|subgroupIdentifier
range|:
name|input
operator|.
name|groups
control|)
block|{
name|GroupDescription
operator|.
name|Basic
name|subgroup
init|=
name|groupResolver
operator|.
name|parse
argument_list|(
name|subgroupIdentifier
argument_list|)
decl_stmt|;
name|subgroupsToRemove
operator|.
name|add
argument_list|(
name|subgroup
operator|.
name|getGroupUUID
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|AccountGroup
operator|.
name|UUID
name|groupUuid
init|=
name|internalGroup
operator|.
name|getGroupUUID
argument_list|()
decl_stmt|;
try|try
block|{
name|removeSubgroups
argument_list|(
name|groupUuid
argument_list|,
name|subgroupsToRemove
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchGroupException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Group %s not found"
argument_list|,
name|groupUuid
argument_list|)
argument_list|)
throw|;
block|}
return|return
name|Response
operator|.
name|none
argument_list|()
return|;
block|}
DECL|method|removeSubgroups ( AccountGroup.UUID parentGroupUuid, Set<AccountGroup.UUID> removedSubgroupUuids)
specifier|private
name|void
name|removeSubgroups
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|parentGroupUuid
parameter_list|,
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|removedSubgroupUuids
parameter_list|)
throws|throws
name|NoSuchGroupException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|InternalGroupUpdate
name|groupUpdate
init|=
name|InternalGroupUpdate
operator|.
name|builder
argument_list|()
operator|.
name|setSubgroupModification
argument_list|(
name|subgroupUuids
lambda|->
name|Sets
operator|.
name|difference
argument_list|(
name|subgroupUuids
argument_list|,
name|removedSubgroupUuids
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|groupsUpdateProvider
operator|.
name|get
argument_list|()
operator|.
name|updateGroup
argument_list|(
name|parentGroupUuid
argument_list|,
name|groupUpdate
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Singleton
DECL|class|DeleteSubgroup
specifier|public
specifier|static
class|class
name|DeleteSubgroup
implements|implements
name|RestModifyView
argument_list|<
name|SubgroupResource
argument_list|,
name|Input
argument_list|>
block|{
DECL|field|delete
specifier|private
specifier|final
name|Provider
argument_list|<
name|DeleteSubgroups
argument_list|>
name|delete
decl_stmt|;
annotation|@
name|Inject
DECL|method|DeleteSubgroup (Provider<DeleteSubgroups> delete)
specifier|public
name|DeleteSubgroup
parameter_list|(
name|Provider
argument_list|<
name|DeleteSubgroups
argument_list|>
name|delete
parameter_list|)
block|{
name|this
operator|.
name|delete
operator|=
name|delete
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (SubgroupResource resource, Input input)
specifier|public
name|Response
argument_list|<
name|?
argument_list|>
name|apply
parameter_list|(
name|SubgroupResource
name|resource
parameter_list|,
name|Input
name|input
parameter_list|)
throws|throws
name|AuthException
throws|,
name|MethodNotAllowedException
throws|,
name|UnprocessableEntityException
throws|,
name|ResourceNotFoundException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|AddSubgroups
operator|.
name|Input
name|in
init|=
operator|new
name|AddSubgroups
operator|.
name|Input
argument_list|()
decl_stmt|;
name|in
operator|.
name|groups
operator|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|resource
operator|.
name|getMember
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|delete
operator|.
name|get
argument_list|()
operator|.
name|apply
argument_list|(
name|resource
argument_list|,
name|in
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

