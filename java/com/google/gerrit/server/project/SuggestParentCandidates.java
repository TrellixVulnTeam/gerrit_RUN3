begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2011 The Android Open Source Project
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
comment|// limitations under the License
end_comment

begin_package
DECL|package|com.google.gerrit.server.project
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|project
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
operator|.
name|toList
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
name|Project
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
name|AllProjectsName
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
name|ProjectPermission
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
name|Set
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|SuggestParentCandidates
specifier|public
class|class
name|SuggestParentCandidates
block|{
DECL|field|projectCache
specifier|private
specifier|final
name|ProjectCache
name|projectCache
decl_stmt|;
DECL|field|permissionBackend
specifier|private
specifier|final
name|PermissionBackend
name|permissionBackend
decl_stmt|;
DECL|field|allProjects
specifier|private
specifier|final
name|AllProjectsName
name|allProjects
decl_stmt|;
annotation|@
name|Inject
DECL|method|SuggestParentCandidates ( ProjectCache projectCache, PermissionBackend permissionBackend, AllProjectsName allProjects)
name|SuggestParentCandidates
parameter_list|(
name|ProjectCache
name|projectCache
parameter_list|,
name|PermissionBackend
name|permissionBackend
parameter_list|,
name|AllProjectsName
name|allProjects
parameter_list|)
block|{
name|this
operator|.
name|projectCache
operator|=
name|projectCache
expr_stmt|;
name|this
operator|.
name|permissionBackend
operator|=
name|permissionBackend
expr_stmt|;
name|this
operator|.
name|allProjects
operator|=
name|allProjects
expr_stmt|;
block|}
DECL|method|getNameKeys ()
specifier|public
name|List
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
name|getNameKeys
parameter_list|()
throws|throws
name|PermissionBackendException
block|{
return|return
name|permissionBackend
operator|.
name|currentUser
argument_list|()
operator|.
name|filter
argument_list|(
name|ProjectPermission
operator|.
name|ACCESS
argument_list|,
name|parents
argument_list|()
argument_list|)
operator|.
name|stream
argument_list|()
operator|.
name|sorted
argument_list|()
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
return|;
block|}
DECL|method|parents ()
specifier|private
name|Set
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
name|parents
parameter_list|()
block|{
name|Set
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
name|parents
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Project
operator|.
name|NameKey
name|p
range|:
name|projectCache
operator|.
name|all
argument_list|()
control|)
block|{
name|ProjectState
name|ps
init|=
name|projectCache
operator|.
name|get
argument_list|(
name|p
argument_list|)
decl_stmt|;
if|if
condition|(
name|ps
operator|!=
literal|null
condition|)
block|{
name|Project
operator|.
name|NameKey
name|parent
init|=
name|ps
operator|.
name|getProject
argument_list|()
operator|.
name|getParent
argument_list|()
decl_stmt|;
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|parents
operator|.
name|add
argument_list|(
name|parent
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|parents
operator|.
name|add
argument_list|(
name|allProjects
argument_list|)
expr_stmt|;
return|return
name|parents
return|;
block|}
block|}
end_class

end_unit

