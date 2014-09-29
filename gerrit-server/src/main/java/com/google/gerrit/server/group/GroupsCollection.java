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
name|common
operator|.
name|data
operator|.
name|GroupDescriptions
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
name|GroupReference
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
name|RestCollection
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
name|AnonymousUser
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
name|CurrentUser
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
name|account
operator|.
name|GroupBackends
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
name|javax
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|GroupsCollection
specifier|public
class|class
name|GroupsCollection
implements|implements
name|RestCollection
argument_list|<
name|TopLevelResource
argument_list|,
name|GroupResource
argument_list|>
implements|,
name|AcceptsCreate
argument_list|<
name|TopLevelResource
argument_list|>
block|{
DECL|field|views
specifier|private
specifier|final
name|DynamicMap
argument_list|<
name|RestView
argument_list|<
name|GroupResource
argument_list|>
argument_list|>
name|views
decl_stmt|;
DECL|field|list
specifier|private
specifier|final
name|Provider
argument_list|<
name|ListGroups
argument_list|>
name|list
decl_stmt|;
DECL|field|createGroup
specifier|private
specifier|final
name|CreateGroup
operator|.
name|Factory
name|createGroup
decl_stmt|;
DECL|field|groupControlFactory
specifier|private
specifier|final
name|GroupControl
operator|.
name|Factory
name|groupControlFactory
decl_stmt|;
DECL|field|groupBackend
specifier|private
specifier|final
name|GroupBackend
name|groupBackend
decl_stmt|;
DECL|field|self
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|self
decl_stmt|;
annotation|@
name|Inject
DECL|method|GroupsCollection (final DynamicMap<RestView<GroupResource>> views, final Provider<ListGroups> list, final CreateGroup.Factory createGroup, final GroupControl.Factory groupControlFactory, final GroupBackend groupBackend, final Provider<CurrentUser> self)
name|GroupsCollection
parameter_list|(
specifier|final
name|DynamicMap
argument_list|<
name|RestView
argument_list|<
name|GroupResource
argument_list|>
argument_list|>
name|views
parameter_list|,
specifier|final
name|Provider
argument_list|<
name|ListGroups
argument_list|>
name|list
parameter_list|,
specifier|final
name|CreateGroup
operator|.
name|Factory
name|createGroup
parameter_list|,
specifier|final
name|GroupControl
operator|.
name|Factory
name|groupControlFactory
parameter_list|,
specifier|final
name|GroupBackend
name|groupBackend
parameter_list|,
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|self
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
name|createGroup
operator|=
name|createGroup
expr_stmt|;
name|this
operator|.
name|groupControlFactory
operator|=
name|groupControlFactory
expr_stmt|;
name|this
operator|.
name|groupBackend
operator|=
name|groupBackend
expr_stmt|;
name|this
operator|.
name|self
operator|=
name|self
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|list ()
specifier|public
name|RestView
argument_list|<
name|TopLevelResource
argument_list|>
name|list
parameter_list|()
throws|throws
name|ResourceNotFoundException
throws|,
name|AuthException
block|{
specifier|final
name|CurrentUser
name|user
init|=
name|self
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|user
operator|instanceof
name|AnonymousUser
condition|)
block|{
throw|throw
operator|new
name|AuthException
argument_list|(
literal|"Authentication required"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
operator|!
operator|(
name|user
operator|.
name|isIdentifiedUser
argument_list|()
operator|)
condition|)
block|{
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|()
throw|;
block|}
return|return
name|list
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|parse (TopLevelResource parent, IdString id)
specifier|public
name|GroupResource
name|parse
parameter_list|(
name|TopLevelResource
name|parent
parameter_list|,
name|IdString
name|id
parameter_list|)
throws|throws
name|AuthException
throws|,
name|ResourceNotFoundException
block|{
specifier|final
name|CurrentUser
name|user
init|=
name|self
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|user
operator|instanceof
name|AnonymousUser
condition|)
block|{
throw|throw
operator|new
name|AuthException
argument_list|(
literal|"Authentication required"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
operator|!
operator|(
name|user
operator|.
name|isIdentifiedUser
argument_list|()
operator|)
condition|)
block|{
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
name|id
argument_list|)
throw|;
block|}
name|GroupDescription
operator|.
name|Basic
name|group
init|=
name|parseId
argument_list|(
name|id
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|group
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
name|id
operator|.
name|get
argument_list|()
argument_list|)
throw|;
block|}
name|GroupControl
name|ctl
init|=
name|groupControlFactory
operator|.
name|controlFor
argument_list|(
name|group
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|ctl
operator|.
name|isVisible
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
name|id
argument_list|)
throw|;
block|}
return|return
operator|new
name|GroupResource
argument_list|(
name|ctl
argument_list|)
return|;
block|}
comment|/**    * Parses a group ID from a request body and returns the group.    *    * @param id ID of the group, can be a group UUID, a group name or a legacy    *        group ID    * @return the group    * @throws UnprocessableEntityException thrown if the group ID cannot be    *         resolved or if the group is not visible to the calling user    */
DECL|method|parse (String id)
specifier|public
name|GroupDescription
operator|.
name|Basic
name|parse
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|UnprocessableEntityException
block|{
name|GroupDescription
operator|.
name|Basic
name|group
init|=
name|parseId
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|group
operator|==
literal|null
operator|||
operator|!
name|groupControlFactory
operator|.
name|controlFor
argument_list|(
name|group
argument_list|)
operator|.
name|isVisible
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|UnprocessableEntityException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Group Not Found: %s"
argument_list|,
name|id
argument_list|)
argument_list|)
throw|;
block|}
return|return
name|group
return|;
block|}
comment|/**    * Parses a group ID from a request body and returns the group if it is a    * Gerrit internal group.    *    * @param id ID of the group, can be a group UUID, a group name or a legacy    *        group ID    * @return the group    * @throws UnprocessableEntityException thrown if the group ID cannot be    *         resolved, if the group is not visible to the calling user or if    *         it's an external group    */
DECL|method|parseInternal (String id)
specifier|public
name|GroupDescription
operator|.
name|Basic
name|parseInternal
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|UnprocessableEntityException
block|{
name|GroupDescription
operator|.
name|Basic
name|group
init|=
name|parse
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|GroupDescriptions
operator|.
name|toAccountGroup
argument_list|(
name|group
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|UnprocessableEntityException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"External Group Not Allowed: %s"
argument_list|,
name|id
argument_list|)
argument_list|)
throw|;
block|}
return|return
name|group
return|;
block|}
comment|/**    * Parses a group ID and returns the group without making any permission    * check whether the current user can see the group.    *    * @param id ID of the group, can be a group UUID, a group name or a legacy    *        group ID    * @return the group, null if no group is found for the given group ID    */
DECL|method|parseId (String id)
specifier|public
name|GroupDescription
operator|.
name|Basic
name|parseId
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|AccountGroup
operator|.
name|UUID
name|uuid
init|=
operator|new
name|AccountGroup
operator|.
name|UUID
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|groupBackend
operator|.
name|handles
argument_list|(
name|uuid
argument_list|)
condition|)
block|{
name|GroupDescription
operator|.
name|Basic
name|d
init|=
name|groupBackend
operator|.
name|get
argument_list|(
name|uuid
argument_list|)
decl_stmt|;
if|if
condition|(
name|d
operator|!=
literal|null
condition|)
block|{
return|return
name|d
return|;
block|}
block|}
comment|// Might be a legacy AccountGroup.Id.
if|if
condition|(
name|id
operator|.
name|matches
argument_list|(
literal|"^[1-9][0-9]*$"
argument_list|)
condition|)
block|{
try|try
block|{
name|AccountGroup
operator|.
name|Id
name|legacyId
init|=
name|AccountGroup
operator|.
name|Id
operator|.
name|parse
argument_list|(
name|id
argument_list|)
decl_stmt|;
return|return
name|groupControlFactory
operator|.
name|controlFor
argument_list|(
name|legacyId
argument_list|)
operator|.
name|getGroup
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|invalidId
parameter_list|)
block|{       }
catch|catch
parameter_list|(
name|NoSuchGroupException
name|e
parameter_list|)
block|{       }
block|}
comment|// Might be a group name, be nice and accept unique names.
name|GroupReference
name|ref
init|=
name|GroupBackends
operator|.
name|findExactSuggestion
argument_list|(
name|groupBackend
argument_list|,
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|ref
operator|!=
literal|null
condition|)
block|{
name|GroupDescription
operator|.
name|Basic
name|d
init|=
name|groupBackend
operator|.
name|get
argument_list|(
name|ref
operator|.
name|getUUID
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|d
operator|!=
literal|null
condition|)
block|{
return|return
name|d
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|create (TopLevelResource root, IdString name)
specifier|public
name|CreateGroup
name|create
parameter_list|(
name|TopLevelResource
name|root
parameter_list|,
name|IdString
name|name
parameter_list|)
block|{
return|return
name|createGroup
operator|.
name|create
argument_list|(
name|name
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
name|GroupResource
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

