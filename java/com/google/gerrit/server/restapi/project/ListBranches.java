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
DECL|package|com.google.gerrit.server.restapi.project
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
name|project
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
name|ComparisonChain
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
name|extensions
operator|.
name|api
operator|.
name|projects
operator|.
name|BranchInfo
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
name|ProjectApi
operator|.
name|ListRefsRequest
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
name|ActionInfo
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
name|WebLinkInfo
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
name|webui
operator|.
name|UiAction
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
name|RefNames
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
name|WebLinks
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
name|extensions
operator|.
name|webui
operator|.
name|UiActions
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
name|git
operator|.
name|GitRepositoryManager
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
name|permissions
operator|.
name|RefPermission
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
name|BranchResource
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
name|ProjectResource
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
name|ProjectState
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
name|RefFilter
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|Comparator
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|RepositoryNotFoundException
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
name|Constants
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
name|Ref
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
name|Repository
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
DECL|class|ListBranches
specifier|public
class|class
name|ListBranches
implements|implements
name|RestReadView
argument_list|<
name|ProjectResource
argument_list|>
block|{
DECL|field|repoManager
specifier|private
specifier|final
name|GitRepositoryManager
name|repoManager
decl_stmt|;
DECL|field|permissionBackend
specifier|private
specifier|final
name|PermissionBackend
name|permissionBackend
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
DECL|field|branchViews
specifier|private
specifier|final
name|DynamicMap
argument_list|<
name|RestView
argument_list|<
name|BranchResource
argument_list|>
argument_list|>
name|branchViews
decl_stmt|;
DECL|field|uiActions
specifier|private
specifier|final
name|UiActions
name|uiActions
decl_stmt|;
DECL|field|webLinks
specifier|private
specifier|final
name|WebLinks
name|webLinks
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--limit"
argument_list|,
name|aliases
operator|=
block|{
literal|"-n"
block|}
argument_list|,
name|metaVar
operator|=
literal|"CNT"
argument_list|,
name|usage
operator|=
literal|"maximum number of branches to list"
argument_list|)
DECL|method|setLimit (int limit)
specifier|public
name|void
name|setLimit
parameter_list|(
name|int
name|limit
parameter_list|)
block|{
name|this
operator|.
name|limit
operator|=
name|limit
expr_stmt|;
block|}
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--start"
argument_list|,
name|aliases
operator|=
block|{
literal|"-S"
block|,
literal|"-s"
block|}
argument_list|,
name|metaVar
operator|=
literal|"CNT"
argument_list|,
name|usage
operator|=
literal|"number of branches to skip"
argument_list|)
DECL|method|setStart (int start)
specifier|public
name|void
name|setStart
parameter_list|(
name|int
name|start
parameter_list|)
block|{
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
block|}
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--match"
argument_list|,
name|aliases
operator|=
block|{
literal|"-m"
block|}
argument_list|,
name|metaVar
operator|=
literal|"MATCH"
argument_list|,
name|usage
operator|=
literal|"match branches substring"
argument_list|)
DECL|method|setMatchSubstring (String matchSubstring)
specifier|public
name|void
name|setMatchSubstring
parameter_list|(
name|String
name|matchSubstring
parameter_list|)
block|{
name|this
operator|.
name|matchSubstring
operator|=
name|matchSubstring
expr_stmt|;
block|}
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--regex"
argument_list|,
name|aliases
operator|=
block|{
literal|"-r"
block|}
argument_list|,
name|metaVar
operator|=
literal|"REGEX"
argument_list|,
name|usage
operator|=
literal|"match branches regex"
argument_list|)
DECL|method|setMatchRegex (String matchRegex)
specifier|public
name|void
name|setMatchRegex
parameter_list|(
name|String
name|matchRegex
parameter_list|)
block|{
name|this
operator|.
name|matchRegex
operator|=
name|matchRegex
expr_stmt|;
block|}
DECL|field|limit
specifier|private
name|int
name|limit
decl_stmt|;
DECL|field|start
specifier|private
name|int
name|start
decl_stmt|;
DECL|field|matchSubstring
specifier|private
name|String
name|matchSubstring
decl_stmt|;
DECL|field|matchRegex
specifier|private
name|String
name|matchRegex
decl_stmt|;
annotation|@
name|Inject
DECL|method|ListBranches ( GitRepositoryManager repoManager, PermissionBackend permissionBackend, Provider<CurrentUser> user, DynamicMap<RestView<BranchResource>> branchViews, UiActions uiActions, WebLinks webLinks)
specifier|public
name|ListBranches
parameter_list|(
name|GitRepositoryManager
name|repoManager
parameter_list|,
name|PermissionBackend
name|permissionBackend
parameter_list|,
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|user
parameter_list|,
name|DynamicMap
argument_list|<
name|RestView
argument_list|<
name|BranchResource
argument_list|>
argument_list|>
name|branchViews
parameter_list|,
name|UiActions
name|uiActions
parameter_list|,
name|WebLinks
name|webLinks
parameter_list|)
block|{
name|this
operator|.
name|repoManager
operator|=
name|repoManager
expr_stmt|;
name|this
operator|.
name|permissionBackend
operator|=
name|permissionBackend
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|branchViews
operator|=
name|branchViews
expr_stmt|;
name|this
operator|.
name|uiActions
operator|=
name|uiActions
expr_stmt|;
name|this
operator|.
name|webLinks
operator|=
name|webLinks
expr_stmt|;
block|}
DECL|method|request (ListRefsRequest<BranchInfo> request)
specifier|public
name|ListBranches
name|request
parameter_list|(
name|ListRefsRequest
argument_list|<
name|BranchInfo
argument_list|>
name|request
parameter_list|)
block|{
name|this
operator|.
name|setLimit
argument_list|(
name|request
operator|.
name|getLimit
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|setStart
argument_list|(
name|request
operator|.
name|getStart
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|setMatchSubstring
argument_list|(
name|request
operator|.
name|getSubstring
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|setMatchRegex
argument_list|(
name|request
operator|.
name|getRegex
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|apply (ProjectResource rsrc)
specifier|public
name|List
argument_list|<
name|BranchInfo
argument_list|>
name|apply
parameter_list|(
name|ProjectResource
name|rsrc
parameter_list|)
throws|throws
name|RestApiException
throws|,
name|IOException
throws|,
name|PermissionBackendException
block|{
name|rsrc
operator|.
name|getProjectState
argument_list|()
operator|.
name|checkStatePermitsRead
argument_list|()
expr_stmt|;
return|return
operator|new
name|RefFilter
argument_list|<
name|BranchInfo
argument_list|>
argument_list|(
name|Constants
operator|.
name|R_HEADS
argument_list|)
operator|.
name|subString
argument_list|(
name|matchSubstring
argument_list|)
operator|.
name|regex
argument_list|(
name|matchRegex
argument_list|)
operator|.
name|start
argument_list|(
name|start
argument_list|)
operator|.
name|limit
argument_list|(
name|limit
argument_list|)
operator|.
name|filter
argument_list|(
name|allBranches
argument_list|(
name|rsrc
argument_list|)
argument_list|)
return|;
block|}
DECL|method|toBranchInfo (BranchResource rsrc)
name|BranchInfo
name|toBranchInfo
parameter_list|(
name|BranchResource
name|rsrc
parameter_list|)
throws|throws
name|IOException
throws|,
name|ResourceNotFoundException
throws|,
name|PermissionBackendException
block|{
try|try
init|(
name|Repository
name|db
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|rsrc
operator|.
name|getNameKey
argument_list|()
argument_list|)
init|)
block|{
name|Ref
name|r
init|=
name|db
operator|.
name|exactRef
argument_list|(
name|rsrc
operator|.
name|getRef
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|()
throw|;
block|}
return|return
name|toBranchInfo
argument_list|(
name|rsrc
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|r
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryNotFoundException
name|noRepo
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|()
throw|;
block|}
block|}
DECL|method|allBranches (ProjectResource rsrc)
specifier|private
name|List
argument_list|<
name|BranchInfo
argument_list|>
name|allBranches
parameter_list|(
name|ProjectResource
name|rsrc
parameter_list|)
throws|throws
name|IOException
throws|,
name|ResourceNotFoundException
throws|,
name|PermissionBackendException
block|{
name|List
argument_list|<
name|Ref
argument_list|>
name|refs
decl_stmt|;
try|try
init|(
name|Repository
name|db
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|rsrc
operator|.
name|getNameKey
argument_list|()
argument_list|)
init|)
block|{
name|Collection
argument_list|<
name|Ref
argument_list|>
name|heads
init|=
name|db
operator|.
name|getRefDatabase
argument_list|()
operator|.
name|getRefs
argument_list|(
name|Constants
operator|.
name|R_HEADS
argument_list|)
operator|.
name|values
argument_list|()
decl_stmt|;
name|refs
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|heads
operator|.
name|size
argument_list|()
operator|+
literal|3
argument_list|)
expr_stmt|;
name|refs
operator|.
name|addAll
argument_list|(
name|heads
argument_list|)
expr_stmt|;
name|refs
operator|.
name|addAll
argument_list|(
name|db
operator|.
name|getRefDatabase
argument_list|()
operator|.
name|exactRef
argument_list|(
name|Constants
operator|.
name|HEAD
argument_list|,
name|RefNames
operator|.
name|REFS_CONFIG
argument_list|,
name|RefNames
operator|.
name|REFS_USERS_DEFAULT
argument_list|)
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryNotFoundException
name|noGitRepository
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|()
throw|;
block|}
return|return
name|toBranchInfo
argument_list|(
name|rsrc
argument_list|,
name|refs
argument_list|)
return|;
block|}
DECL|method|toBranchInfo (ProjectResource rsrc, List<Ref> refs)
specifier|private
name|List
argument_list|<
name|BranchInfo
argument_list|>
name|toBranchInfo
parameter_list|(
name|ProjectResource
name|rsrc
parameter_list|,
name|List
argument_list|<
name|Ref
argument_list|>
name|refs
parameter_list|)
throws|throws
name|PermissionBackendException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|targets
init|=
name|Sets
operator|.
name|newHashSetWithExpectedSize
argument_list|(
literal|1
argument_list|)
decl_stmt|;
for|for
control|(
name|Ref
name|ref
range|:
name|refs
control|)
block|{
if|if
condition|(
name|ref
operator|.
name|isSymbolic
argument_list|()
condition|)
block|{
name|targets
operator|.
name|add
argument_list|(
name|ref
operator|.
name|getTarget
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|PermissionBackend
operator|.
name|ForProject
name|perm
init|=
name|permissionBackend
operator|.
name|user
argument_list|(
name|user
argument_list|)
operator|.
name|project
argument_list|(
name|rsrc
operator|.
name|getNameKey
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|BranchInfo
argument_list|>
name|branches
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|refs
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Ref
name|ref
range|:
name|refs
control|)
block|{
if|if
condition|(
name|ref
operator|.
name|isSymbolic
argument_list|()
condition|)
block|{
comment|// A symbolic reference to another branch, instead of
comment|// showing the resolved value, show the name it references.
comment|//
name|String
name|target
init|=
name|ref
operator|.
name|getTarget
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|perm
operator|.
name|ref
argument_list|(
name|target
argument_list|)
operator|.
name|test
argument_list|(
name|RefPermission
operator|.
name|READ
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|target
operator|.
name|startsWith
argument_list|(
name|Constants
operator|.
name|R_HEADS
argument_list|)
condition|)
block|{
name|target
operator|=
name|target
operator|.
name|substring
argument_list|(
name|Constants
operator|.
name|R_HEADS
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|BranchInfo
name|b
init|=
operator|new
name|BranchInfo
argument_list|()
decl_stmt|;
name|b
operator|.
name|ref
operator|=
name|ref
operator|.
name|getName
argument_list|()
expr_stmt|;
name|b
operator|.
name|revision
operator|=
name|target
expr_stmt|;
name|branches
operator|.
name|add
argument_list|(
name|b
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Constants
operator|.
name|HEAD
operator|.
name|equals
argument_list|(
name|ref
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|b
operator|.
name|canDelete
operator|=
name|perm
operator|.
name|ref
argument_list|(
name|ref
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|testOrFalse
argument_list|(
name|RefPermission
operator|.
name|DELETE
argument_list|)
operator|&&
name|rsrc
operator|.
name|getProjectState
argument_list|()
operator|.
name|statePermitsWrite
argument_list|()
condition|?
literal|true
else|:
literal|null
expr_stmt|;
block|}
continue|continue;
block|}
if|if
condition|(
name|perm
operator|.
name|ref
argument_list|(
name|ref
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|test
argument_list|(
name|RefPermission
operator|.
name|READ
argument_list|)
condition|)
block|{
name|branches
operator|.
name|add
argument_list|(
name|createBranchInfo
argument_list|(
name|perm
operator|.
name|ref
argument_list|(
name|ref
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|ref
argument_list|,
name|rsrc
operator|.
name|getProjectState
argument_list|()
argument_list|,
name|rsrc
operator|.
name|getUser
argument_list|()
argument_list|,
name|targets
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|branches
argument_list|,
operator|new
name|BranchComparator
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|branches
return|;
block|}
DECL|class|BranchComparator
specifier|private
specifier|static
class|class
name|BranchComparator
implements|implements
name|Comparator
argument_list|<
name|BranchInfo
argument_list|>
block|{
annotation|@
name|Override
DECL|method|compare (BranchInfo a, BranchInfo b)
specifier|public
name|int
name|compare
parameter_list|(
name|BranchInfo
name|a
parameter_list|,
name|BranchInfo
name|b
parameter_list|)
block|{
return|return
name|ComparisonChain
operator|.
name|start
argument_list|()
operator|.
name|compareTrueFirst
argument_list|(
name|isHead
argument_list|(
name|a
argument_list|)
argument_list|,
name|isHead
argument_list|(
name|b
argument_list|)
argument_list|)
operator|.
name|compareTrueFirst
argument_list|(
name|isConfig
argument_list|(
name|a
argument_list|)
argument_list|,
name|isConfig
argument_list|(
name|b
argument_list|)
argument_list|)
operator|.
name|compare
argument_list|(
name|a
operator|.
name|ref
argument_list|,
name|b
operator|.
name|ref
argument_list|)
operator|.
name|result
argument_list|()
return|;
block|}
DECL|method|isHead (BranchInfo i)
specifier|private
specifier|static
name|boolean
name|isHead
parameter_list|(
name|BranchInfo
name|i
parameter_list|)
block|{
return|return
name|Constants
operator|.
name|HEAD
operator|.
name|equals
argument_list|(
name|i
operator|.
name|ref
argument_list|)
return|;
block|}
DECL|method|isConfig (BranchInfo i)
specifier|private
specifier|static
name|boolean
name|isConfig
parameter_list|(
name|BranchInfo
name|i
parameter_list|)
block|{
return|return
name|RefNames
operator|.
name|REFS_CONFIG
operator|.
name|equals
argument_list|(
name|i
operator|.
name|ref
argument_list|)
return|;
block|}
block|}
DECL|method|createBranchInfo ( PermissionBackend.ForRef perm, Ref ref, ProjectState projectState, CurrentUser user, Set<String> targets)
specifier|private
name|BranchInfo
name|createBranchInfo
parameter_list|(
name|PermissionBackend
operator|.
name|ForRef
name|perm
parameter_list|,
name|Ref
name|ref
parameter_list|,
name|ProjectState
name|projectState
parameter_list|,
name|CurrentUser
name|user
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|targets
parameter_list|)
block|{
name|BranchInfo
name|info
init|=
operator|new
name|BranchInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|ref
operator|=
name|ref
operator|.
name|getName
argument_list|()
expr_stmt|;
name|info
operator|.
name|revision
operator|=
name|ref
operator|.
name|getObjectId
argument_list|()
operator|!=
literal|null
condition|?
name|ref
operator|.
name|getObjectId
argument_list|()
operator|.
name|name
argument_list|()
else|:
literal|null
expr_stmt|;
name|info
operator|.
name|canDelete
operator|=
operator|!
name|targets
operator|.
name|contains
argument_list|(
name|ref
operator|.
name|getName
argument_list|()
argument_list|)
operator|&&
name|perm
operator|.
name|testOrFalse
argument_list|(
name|RefPermission
operator|.
name|DELETE
argument_list|)
operator|&&
name|projectState
operator|.
name|statePermitsWrite
argument_list|()
condition|?
literal|true
else|:
literal|null
expr_stmt|;
name|BranchResource
name|rsrc
init|=
operator|new
name|BranchResource
argument_list|(
name|projectState
argument_list|,
name|user
argument_list|,
name|ref
argument_list|)
decl_stmt|;
for|for
control|(
name|UiAction
operator|.
name|Description
name|d
range|:
name|uiActions
operator|.
name|from
argument_list|(
name|branchViews
argument_list|,
name|rsrc
argument_list|)
control|)
block|{
if|if
condition|(
name|info
operator|.
name|actions
operator|==
literal|null
condition|)
block|{
name|info
operator|.
name|actions
operator|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|info
operator|.
name|actions
operator|.
name|put
argument_list|(
name|d
operator|.
name|getId
argument_list|()
argument_list|,
operator|new
name|ActionInfo
argument_list|(
name|d
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|WebLinkInfo
argument_list|>
name|links
init|=
name|webLinks
operator|.
name|getBranchLinks
argument_list|(
name|projectState
operator|.
name|getName
argument_list|()
argument_list|,
name|ref
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|info
operator|.
name|webLinks
operator|=
name|links
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
name|links
expr_stmt|;
return|return
name|info
return|;
block|}
block|}
end_class

end_unit

