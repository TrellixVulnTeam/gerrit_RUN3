begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2018 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.extensions.webui
package|package
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
name|truth
operator|.
name|Truth
operator|.
name|assertThat
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
name|ImmutableSet
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
name|GroupMembership
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
name|PermissionBackend
operator|.
name|ForProject
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
operator|.
name|ForRef
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
operator|.
name|RefFilterOptions
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
name|PermissionBackendCondition
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
name|Map
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
name|easymock
operator|.
name|EasyMock
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
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|UiActionsTest
specifier|public
class|class
name|UiActionsTest
block|{
DECL|class|FakeForProject
specifier|private
specifier|static
class|class
name|FakeForProject
extends|extends
name|ForProject
block|{
DECL|field|allowValueQueries
specifier|private
name|boolean
name|allowValueQueries
init|=
literal|true
decl_stmt|;
annotation|@
name|Override
DECL|method|user ()
specifier|public
name|CurrentUser
name|user
parameter_list|()
block|{
return|return
operator|new
name|CurrentUser
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|GroupMembership
name|getEffectiveGroups
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"not implemented"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|getCacheKey
parameter_list|()
block|{
return|return
operator|new
name|Object
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isIdentifiedUser
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|Account
operator|.
name|Id
name|getAccountId
parameter_list|()
block|{
return|return
operator|new
name|Account
operator|.
name|Id
argument_list|(
literal|1
argument_list|)
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|resourcePath ()
specifier|public
name|String
name|resourcePath
parameter_list|()
block|{
return|return
literal|"/projects/test-project"
return|;
block|}
annotation|@
name|Override
DECL|method|user (CurrentUser user)
specifier|public
name|ForProject
name|user
parameter_list|(
name|CurrentUser
name|user
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"not implemented"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|ref (String ref)
specifier|public
name|ForRef
name|ref
parameter_list|(
name|String
name|ref
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"not implemented"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|check (ProjectPermission perm)
specifier|public
name|void
name|check
parameter_list|(
name|ProjectPermission
name|perm
parameter_list|)
throws|throws
name|AuthException
throws|,
name|PermissionBackendException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"not implemented"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|test (Collection<ProjectPermission> permSet)
specifier|public
name|Set
argument_list|<
name|ProjectPermission
argument_list|>
name|test
parameter_list|(
name|Collection
argument_list|<
name|ProjectPermission
argument_list|>
name|permSet
parameter_list|)
throws|throws
name|PermissionBackendException
block|{
name|assertThat
argument_list|(
name|allowValueQueries
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
return|return
name|ImmutableSet
operator|.
name|of
argument_list|(
name|ProjectPermission
operator|.
name|READ
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|filter (Map<String, Ref> refs, Repository repo, RefFilterOptions opts)
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Ref
argument_list|>
name|filter
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Ref
argument_list|>
name|refs
parameter_list|,
name|Repository
name|repo
parameter_list|,
name|RefFilterOptions
name|opts
parameter_list|)
throws|throws
name|PermissionBackendException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"not implemented"
argument_list|)
throw|;
block|}
DECL|method|disallowValueQueries ()
specifier|private
name|void
name|disallowValueQueries
parameter_list|()
block|{
name|allowValueQueries
operator|=
literal|false
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|permissionBackendConditionEvaluationDeduplicatesAndBackfills ()
specifier|public
name|void
name|permissionBackendConditionEvaluationDeduplicatesAndBackfills
parameter_list|()
throws|throws
name|Exception
block|{
name|FakeForProject
name|forProject
init|=
operator|new
name|FakeForProject
argument_list|()
decl_stmt|;
comment|// Create three conditions, two of which are identical
name|PermissionBackendCondition
name|cond1
init|=
operator|(
name|PermissionBackendCondition
operator|)
name|forProject
operator|.
name|testCond
argument_list|(
name|ProjectPermission
operator|.
name|CREATE_CHANGE
argument_list|)
decl_stmt|;
name|PermissionBackendCondition
name|cond2
init|=
operator|(
name|PermissionBackendCondition
operator|)
name|forProject
operator|.
name|testCond
argument_list|(
name|ProjectPermission
operator|.
name|READ
argument_list|)
decl_stmt|;
name|PermissionBackendCondition
name|cond3
init|=
operator|(
name|PermissionBackendCondition
operator|)
name|forProject
operator|.
name|testCond
argument_list|(
name|ProjectPermission
operator|.
name|CREATE_CHANGE
argument_list|)
decl_stmt|;
comment|// Set up the Mock to expect a call of bulkEvaluateTest to only contain cond{1,2} since cond3
comment|// needs to be identified as duplicate and not called out explicitly.
name|PermissionBackend
name|permissionBackendMock
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|PermissionBackend
operator|.
name|class
argument_list|)
decl_stmt|;
name|permissionBackendMock
operator|.
name|bulkEvaluateTest
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|cond1
argument_list|,
name|cond2
argument_list|)
argument_list|)
expr_stmt|;
name|EasyMock
operator|.
name|replay
argument_list|(
name|permissionBackendMock
argument_list|)
expr_stmt|;
name|UiActions
operator|.
name|evaluatePermissionBackendConditions
argument_list|(
name|permissionBackendMock
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|cond1
argument_list|,
name|cond2
argument_list|,
name|cond3
argument_list|)
argument_list|)
expr_stmt|;
comment|// Disallow queries for value to ensure that cond3 (previously left behind) is backfilled with
comment|// the value of cond1 and issues no additional call to PermissionBackend.
name|forProject
operator|.
name|disallowValueQueries
argument_list|()
expr_stmt|;
comment|// Assert the values of all conditions
name|assertThat
argument_list|(
name|cond1
operator|.
name|value
argument_list|()
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|cond2
operator|.
name|value
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|cond3
operator|.
name|value
argument_list|()
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

