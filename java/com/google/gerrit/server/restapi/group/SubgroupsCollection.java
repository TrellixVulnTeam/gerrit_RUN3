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
name|extensions
operator|.
name|registration
operator|.
name|DynamicMap
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
name|AcceptsCreate
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
name|ChildCollection
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
name|IdString
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
name|RestView
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
name|TopLevelResource
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
name|restapi
operator|.
name|group
operator|.
name|AddSubgroups
operator|.
name|PutSubgroup
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
name|Singleton
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|SubgroupsCollection
specifier|public
class|class
name|SubgroupsCollection
implements|implements
name|ChildCollection
argument_list|<
name|GroupResource
argument_list|,
name|SubgroupResource
argument_list|>
implements|,
name|AcceptsCreate
argument_list|<
name|GroupResource
argument_list|>
block|{
DECL|field|views
specifier|private
specifier|final
name|DynamicMap
argument_list|<
name|RestView
argument_list|<
name|SubgroupResource
argument_list|>
argument_list|>
name|views
decl_stmt|;
DECL|field|list
specifier|private
specifier|final
name|ListSubgroups
name|list
decl_stmt|;
DECL|field|groupsCollection
specifier|private
specifier|final
name|GroupsCollection
name|groupsCollection
decl_stmt|;
DECL|field|addSubgroups
specifier|private
specifier|final
name|AddSubgroups
name|addSubgroups
decl_stmt|;
annotation|@
name|Inject
DECL|method|SubgroupsCollection ( DynamicMap<RestView<SubgroupResource>> views, ListSubgroups list, GroupsCollection groupsCollection, AddSubgroups addSubgroups)
name|SubgroupsCollection
parameter_list|(
name|DynamicMap
argument_list|<
name|RestView
argument_list|<
name|SubgroupResource
argument_list|>
argument_list|>
name|views
parameter_list|,
name|ListSubgroups
name|list
parameter_list|,
name|GroupsCollection
name|groupsCollection
parameter_list|,
name|AddSubgroups
name|addSubgroups
parameter_list|)
block|{
name|this
operator|.
name|views
operator|=
name|views
expr_stmt|;
name|this
operator|.
name|list
operator|=
name|list
expr_stmt|;
name|this
operator|.
name|groupsCollection
operator|=
name|groupsCollection
expr_stmt|;
name|this
operator|.
name|addSubgroups
operator|=
name|addSubgroups
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|list ()
specifier|public
name|RestView
argument_list|<
name|GroupResource
argument_list|>
name|list
parameter_list|()
block|{
return|return
name|list
return|;
block|}
annotation|@
name|Override
DECL|method|parse (GroupResource resource, IdString id)
specifier|public
name|SubgroupResource
name|parse
parameter_list|(
name|GroupResource
name|resource
parameter_list|,
name|IdString
name|id
parameter_list|)
throws|throws
name|NotInternalGroupException
throws|,
name|AuthException
throws|,
name|ResourceNotFoundException
block|{
name|GroupDescription
operator|.
name|Internal
name|parent
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
name|GroupDescription
operator|.
name|Basic
name|member
init|=
name|groupsCollection
operator|.
name|parse
argument_list|(
name|TopLevelResource
operator|.
name|INSTANCE
argument_list|,
name|id
argument_list|)
operator|.
name|getGroup
argument_list|()
decl_stmt|;
if|if
condition|(
name|resource
operator|.
name|getControl
argument_list|()
operator|.
name|canSeeGroup
argument_list|()
operator|&&
name|isSubgroup
argument_list|(
name|parent
argument_list|,
name|member
argument_list|)
condition|)
block|{
return|return
operator|new
name|SubgroupResource
argument_list|(
name|resource
argument_list|,
name|member
argument_list|)
return|;
block|}
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
name|id
argument_list|)
throw|;
block|}
DECL|method|isSubgroup ( GroupDescription.Internal parent, GroupDescription.Basic member)
specifier|private
specifier|static
name|boolean
name|isSubgroup
parameter_list|(
name|GroupDescription
operator|.
name|Internal
name|parent
parameter_list|,
name|GroupDescription
operator|.
name|Basic
name|member
parameter_list|)
block|{
return|return
name|parent
operator|.
name|getSubgroups
argument_list|()
operator|.
name|contains
argument_list|(
name|member
operator|.
name|getGroupUUID
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|create (GroupResource group, IdString id)
specifier|public
name|PutSubgroup
name|create
parameter_list|(
name|GroupResource
name|group
parameter_list|,
name|IdString
name|id
parameter_list|)
block|{
return|return
operator|new
name|PutSubgroup
argument_list|(
name|addSubgroups
argument_list|,
name|id
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|views ()
specifier|public
name|DynamicMap
argument_list|<
name|RestView
argument_list|<
name|SubgroupResource
argument_list|>
argument_list|>
name|views
parameter_list|()
block|{
return|return
name|views
return|;
block|}
block|}
end_class

end_unit

