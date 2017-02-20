begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2015 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.api.groups
package|package
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
name|groups
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
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
name|api
operator|.
name|groups
operator|.
name|GroupApi
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
name|api
operator|.
name|groups
operator|.
name|GroupInput
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
name|api
operator|.
name|groups
operator|.
name|Groups
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
name|client
operator|.
name|ListGroupsOption
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
name|GroupInfo
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
name|BadRequestException
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
name|RestApiException
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
name|AccountsCollection
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
name|CreateGroup
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
name|GroupsCollection
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
name|ListGroups
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
name|QueryGroups
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
name|permissions
operator|.
name|GlobalPermission
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
name|permissions
operator|.
name|PermissionBackend
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
name|permissions
operator|.
name|PermissionBackendException
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
name|project
operator|.
name|ProjectsCollection
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedMap
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|GroupsImpl
class|class
name|GroupsImpl
implements|implements
name|Groups
block|{
DECL|field|accounts
specifier|private
specifier|final
name|AccountsCollection
name|accounts
decl_stmt|;
DECL|field|groups
specifier|private
specifier|final
name|GroupsCollection
name|groups
decl_stmt|;
DECL|field|projects
specifier|private
specifier|final
name|ProjectsCollection
name|projects
decl_stmt|;
DECL|field|listGroups
specifier|private
specifier|final
name|Provider
argument_list|<
name|ListGroups
argument_list|>
name|listGroups
decl_stmt|;
DECL|field|queryGroups
specifier|private
specifier|final
name|Provider
argument_list|<
name|QueryGroups
argument_list|>
name|queryGroups
decl_stmt|;
DECL|field|user
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|user
decl_stmt|;
DECL|field|permissionBackend
specifier|private
specifier|final
name|PermissionBackend
name|permissionBackend
decl_stmt|;
DECL|field|createGroup
specifier|private
specifier|final
name|CreateGroup
operator|.
name|Factory
name|createGroup
decl_stmt|;
DECL|field|api
specifier|private
specifier|final
name|GroupApiImpl
operator|.
name|Factory
name|api
decl_stmt|;
annotation|@
name|Inject
DECL|method|GroupsImpl ( AccountsCollection accounts, GroupsCollection groups, ProjectsCollection projects, Provider<ListGroups> listGroups, Provider<QueryGroups> queryGroups, Provider<CurrentUser> user, PermissionBackend permissionBackend, CreateGroup.Factory createGroup, GroupApiImpl.Factory api)
name|GroupsImpl
parameter_list|(
name|AccountsCollection
name|accounts
parameter_list|,
name|GroupsCollection
name|groups
parameter_list|,
name|ProjectsCollection
name|projects
parameter_list|,
name|Provider
argument_list|<
name|ListGroups
argument_list|>
name|listGroups
parameter_list|,
name|Provider
argument_list|<
name|QueryGroups
argument_list|>
name|queryGroups
parameter_list|,
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|user
parameter_list|,
name|PermissionBackend
name|permissionBackend
parameter_list|,
name|CreateGroup
operator|.
name|Factory
name|createGroup
parameter_list|,
name|GroupApiImpl
operator|.
name|Factory
name|api
parameter_list|)
block|{
name|this
operator|.
name|accounts
operator|=
name|accounts
expr_stmt|;
name|this
operator|.
name|groups
operator|=
name|groups
expr_stmt|;
name|this
operator|.
name|projects
operator|=
name|projects
expr_stmt|;
name|this
operator|.
name|listGroups
operator|=
name|listGroups
expr_stmt|;
name|this
operator|.
name|queryGroups
operator|=
name|queryGroups
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|permissionBackend
operator|=
name|permissionBackend
expr_stmt|;
name|this
operator|.
name|createGroup
operator|=
name|createGroup
expr_stmt|;
name|this
operator|.
name|api
operator|=
name|api
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|id (String id)
specifier|public
name|GroupApi
name|id
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|RestApiException
block|{
return|return
name|api
operator|.
name|create
argument_list|(
name|groups
operator|.
name|parse
argument_list|(
name|TopLevelResource
operator|.
name|INSTANCE
argument_list|,
name|IdString
operator|.
name|fromDecoded
argument_list|(
name|id
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|create (String name)
specifier|public
name|GroupApi
name|create
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|RestApiException
block|{
name|GroupInput
name|in
init|=
operator|new
name|GroupInput
argument_list|()
decl_stmt|;
name|in
operator|.
name|name
operator|=
name|name
expr_stmt|;
return|return
name|create
argument_list|(
name|in
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|create (GroupInput in)
specifier|public
name|GroupApi
name|create
parameter_list|(
name|GroupInput
name|in
parameter_list|)
throws|throws
name|RestApiException
block|{
if|if
condition|(
name|checkNotNull
argument_list|(
name|in
argument_list|,
literal|"GroupInput"
argument_list|)
operator|.
name|name
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"GroupInput must specify name"
argument_list|)
throw|;
block|}
try|try
block|{
name|CreateGroup
name|impl
init|=
name|createGroup
operator|.
name|create
argument_list|(
name|in
operator|.
name|name
argument_list|)
decl_stmt|;
name|permissionBackend
operator|.
name|user
argument_list|(
name|user
argument_list|)
operator|.
name|checkAny
argument_list|(
name|GlobalPermission
operator|.
name|fromAnnotation
argument_list|(
name|impl
operator|.
name|getClass
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|GroupInfo
name|info
init|=
name|impl
operator|.
name|apply
argument_list|(
name|TopLevelResource
operator|.
name|INSTANCE
argument_list|,
name|in
argument_list|)
decl_stmt|;
return|return
name|id
argument_list|(
name|info
operator|.
name|id
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|OrmException
decl||
name|IOException
decl||
name|PermissionBackendException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RestApiException
argument_list|(
literal|"Cannot create group "
operator|+
name|in
operator|.
name|name
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|list ()
specifier|public
name|ListRequest
name|list
parameter_list|()
block|{
return|return
operator|new
name|ListRequest
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|SortedMap
argument_list|<
name|String
argument_list|,
name|GroupInfo
argument_list|>
name|getAsMap
parameter_list|()
throws|throws
name|RestApiException
block|{
return|return
name|list
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
return|;
block|}
DECL|method|list (ListRequest req)
specifier|private
name|SortedMap
argument_list|<
name|String
argument_list|,
name|GroupInfo
argument_list|>
name|list
parameter_list|(
name|ListRequest
name|req
parameter_list|)
throws|throws
name|RestApiException
block|{
name|TopLevelResource
name|tlr
init|=
name|TopLevelResource
operator|.
name|INSTANCE
decl_stmt|;
name|ListGroups
name|list
init|=
name|listGroups
operator|.
name|get
argument_list|()
decl_stmt|;
name|list
operator|.
name|setOptions
argument_list|(
name|req
operator|.
name|getOptions
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|project
range|:
name|req
operator|.
name|getProjects
argument_list|()
control|)
block|{
try|try
block|{
name|list
operator|.
name|addProject
argument_list|(
name|projects
operator|.
name|parse
argument_list|(
name|tlr
argument_list|,
name|IdString
operator|.
name|fromDecoded
argument_list|(
name|project
argument_list|)
argument_list|)
operator|.
name|getControl
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RestApiException
argument_list|(
literal|"Error looking up project "
operator|+
name|project
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
for|for
control|(
name|String
name|group
range|:
name|req
operator|.
name|getGroups
argument_list|()
control|)
block|{
name|list
operator|.
name|addGroup
argument_list|(
name|groups
operator|.
name|parse
argument_list|(
name|group
argument_list|)
operator|.
name|getGroupUUID
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|list
operator|.
name|setVisibleToAll
argument_list|(
name|req
operator|.
name|getVisibleToAll
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|req
operator|.
name|getUser
argument_list|()
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|list
operator|.
name|setUser
argument_list|(
name|accounts
operator|.
name|parse
argument_list|(
name|req
operator|.
name|getUser
argument_list|()
argument_list|)
operator|.
name|getAccountId
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RestApiException
argument_list|(
literal|"Error looking up user "
operator|+
name|req
operator|.
name|getUser
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
name|list
operator|.
name|setOwned
argument_list|(
name|req
operator|.
name|getOwned
argument_list|()
argument_list|)
expr_stmt|;
name|list
operator|.
name|setLimit
argument_list|(
name|req
operator|.
name|getLimit
argument_list|()
argument_list|)
expr_stmt|;
name|list
operator|.
name|setStart
argument_list|(
name|req
operator|.
name|getStart
argument_list|()
argument_list|)
expr_stmt|;
name|list
operator|.
name|setMatchSubstring
argument_list|(
name|req
operator|.
name|getSubstring
argument_list|()
argument_list|)
expr_stmt|;
name|list
operator|.
name|setSuggest
argument_list|(
name|req
operator|.
name|getSuggest
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|list
operator|.
name|apply
argument_list|(
name|tlr
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RestApiException
argument_list|(
literal|"Cannot list groups"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|query ()
specifier|public
name|QueryRequest
name|query
parameter_list|()
block|{
return|return
operator|new
name|QueryRequest
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|GroupInfo
argument_list|>
name|get
parameter_list|()
throws|throws
name|RestApiException
block|{
return|return
name|GroupsImpl
operator|.
name|this
operator|.
name|query
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|query (String query)
specifier|public
name|QueryRequest
name|query
parameter_list|(
name|String
name|query
parameter_list|)
block|{
return|return
name|query
argument_list|()
operator|.
name|withQuery
argument_list|(
name|query
argument_list|)
return|;
block|}
DECL|method|query (QueryRequest r)
specifier|private
name|List
argument_list|<
name|GroupInfo
argument_list|>
name|query
parameter_list|(
name|QueryRequest
name|r
parameter_list|)
throws|throws
name|RestApiException
block|{
try|try
block|{
name|QueryGroups
name|myQueryGroups
init|=
name|queryGroups
operator|.
name|get
argument_list|()
decl_stmt|;
name|myQueryGroups
operator|.
name|setQuery
argument_list|(
name|r
operator|.
name|getQuery
argument_list|()
argument_list|)
expr_stmt|;
name|myQueryGroups
operator|.
name|setLimit
argument_list|(
name|r
operator|.
name|getLimit
argument_list|()
argument_list|)
expr_stmt|;
name|myQueryGroups
operator|.
name|setStart
argument_list|(
name|r
operator|.
name|getStart
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|ListGroupsOption
name|option
range|:
name|r
operator|.
name|getOptions
argument_list|()
control|)
block|{
name|myQueryGroups
operator|.
name|addOption
argument_list|(
name|option
argument_list|)
expr_stmt|;
block|}
return|return
name|myQueryGroups
operator|.
name|apply
argument_list|(
name|TopLevelResource
operator|.
name|INSTANCE
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RestApiException
argument_list|(
literal|"Cannot query groups"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

