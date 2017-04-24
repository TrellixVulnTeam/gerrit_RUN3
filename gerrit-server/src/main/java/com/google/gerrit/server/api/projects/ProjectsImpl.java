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
DECL|package|com.google.gerrit.server.api.projects
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
name|projects
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
name|api
operator|.
name|projects
operator|.
name|ProjectApi
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
name|projects
operator|.
name|ProjectInput
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
name|projects
operator|.
name|Projects
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
name|ProjectInfo
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
name|ListProjects
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
name|ListProjects
operator|.
name|FilterType
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
name|SortedMap
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|ProjectsImpl
class|class
name|ProjectsImpl
implements|implements
name|Projects
block|{
DECL|field|projects
specifier|private
specifier|final
name|ProjectsCollection
name|projects
decl_stmt|;
DECL|field|api
specifier|private
specifier|final
name|ProjectApiImpl
operator|.
name|Factory
name|api
decl_stmt|;
DECL|field|listProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|ListProjects
argument_list|>
name|listProvider
decl_stmt|;
annotation|@
name|Inject
DECL|method|ProjectsImpl ( ProjectsCollection projects, ProjectApiImpl.Factory api, Provider<ListProjects> listProvider)
name|ProjectsImpl
parameter_list|(
name|ProjectsCollection
name|projects
parameter_list|,
name|ProjectApiImpl
operator|.
name|Factory
name|api
parameter_list|,
name|Provider
argument_list|<
name|ListProjects
argument_list|>
name|listProvider
parameter_list|)
block|{
name|this
operator|.
name|projects
operator|=
name|projects
expr_stmt|;
name|this
operator|.
name|api
operator|=
name|api
expr_stmt|;
name|this
operator|.
name|listProvider
operator|=
name|listProvider
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|name (String name)
specifier|public
name|ProjectApi
name|name
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|RestApiException
block|{
try|try
block|{
return|return
name|api
operator|.
name|create
argument_list|(
name|projects
operator|.
name|parse
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UnprocessableEntityException
name|e
parameter_list|)
block|{
return|return
name|api
operator|.
name|create
argument_list|(
name|name
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
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
literal|"Cannot retrieve project"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|create (String name)
specifier|public
name|ProjectApi
name|create
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|RestApiException
block|{
name|ProjectInput
name|in
init|=
operator|new
name|ProjectInput
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
DECL|method|create (ProjectInput in)
specifier|public
name|ProjectApi
name|create
parameter_list|(
name|ProjectInput
name|in
parameter_list|)
throws|throws
name|RestApiException
block|{
if|if
condition|(
name|in
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
literal|"input.name is required"
argument_list|)
throw|;
block|}
return|return
name|name
argument_list|(
name|in
operator|.
name|name
argument_list|)
operator|.
name|create
argument_list|(
name|in
argument_list|)
return|;
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
name|ProjectInfo
argument_list|>
name|getAsMap
parameter_list|()
throws|throws
name|RestApiException
block|{
try|try
block|{
return|return
name|list
argument_list|(
name|this
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|PermissionBackendException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RestApiException
argument_list|(
literal|"project list unavailable"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
return|;
block|}
DECL|method|list (ListRequest request)
specifier|private
name|SortedMap
argument_list|<
name|String
argument_list|,
name|ProjectInfo
argument_list|>
name|list
parameter_list|(
name|ListRequest
name|request
parameter_list|)
throws|throws
name|RestApiException
throws|,
name|PermissionBackendException
block|{
name|ListProjects
name|lp
init|=
name|listProvider
operator|.
name|get
argument_list|()
decl_stmt|;
name|lp
operator|.
name|setShowDescription
argument_list|(
name|request
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
name|lp
operator|.
name|setLimit
argument_list|(
name|request
operator|.
name|getLimit
argument_list|()
argument_list|)
expr_stmt|;
name|lp
operator|.
name|setStart
argument_list|(
name|request
operator|.
name|getStart
argument_list|()
argument_list|)
expr_stmt|;
name|lp
operator|.
name|setMatchPrefix
argument_list|(
name|request
operator|.
name|getPrefix
argument_list|()
argument_list|)
expr_stmt|;
name|lp
operator|.
name|setMatchSubstring
argument_list|(
name|request
operator|.
name|getSubstring
argument_list|()
argument_list|)
expr_stmt|;
name|lp
operator|.
name|setMatchRegex
argument_list|(
name|request
operator|.
name|getRegex
argument_list|()
argument_list|)
expr_stmt|;
name|lp
operator|.
name|setShowTree
argument_list|(
name|request
operator|.
name|getShowTree
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|branch
range|:
name|request
operator|.
name|getBranches
argument_list|()
control|)
block|{
name|lp
operator|.
name|addShowBranch
argument_list|(
name|branch
argument_list|)
expr_stmt|;
block|}
name|FilterType
name|type
decl_stmt|;
switch|switch
condition|(
name|request
operator|.
name|getFilterType
argument_list|()
condition|)
block|{
case|case
name|ALL
case|:
name|type
operator|=
name|FilterType
operator|.
name|ALL
expr_stmt|;
break|break;
case|case
name|CODE
case|:
name|type
operator|=
name|FilterType
operator|.
name|CODE
expr_stmt|;
break|break;
case|case
name|PARENT_CANDIDATES
case|:
name|type
operator|=
name|FilterType
operator|.
name|PARENT_CANDIDATES
expr_stmt|;
break|break;
case|case
name|PERMISSIONS
case|:
name|type
operator|=
name|FilterType
operator|.
name|PERMISSIONS
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"Unknown filter type: "
operator|+
name|request
operator|.
name|getFilterType
argument_list|()
argument_list|)
throw|;
block|}
name|lp
operator|.
name|setFilterType
argument_list|(
name|type
argument_list|)
expr_stmt|;
return|return
name|lp
operator|.
name|apply
argument_list|()
return|;
block|}
block|}
end_class

end_unit

