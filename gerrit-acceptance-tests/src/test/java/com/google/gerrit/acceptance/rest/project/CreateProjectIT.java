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
DECL|package|com.google.gerrit.acceptance.rest.project
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|rest
operator|.
name|project
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|rest
operator|.
name|project
operator|.
name|ProjectAssert
operator|.
name|assertProjectInfo
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|rest
operator|.
name|project
operator|.
name|ProjectAssert
operator|.
name|assertProjectOwners
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
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
name|Lists
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
name|acceptance
operator|.
name|AbstractDaemonTest
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
name|acceptance
operator|.
name|AccountCreator
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
name|acceptance
operator|.
name|RestResponse
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
name|acceptance
operator|.
name|RestSession
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
name|acceptance
operator|.
name|TestAccount
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
name|reviewdb
operator|.
name|client
operator|.
name|Project
operator|.
name|InheritableBoolean
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
operator|.
name|SubmitType
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
name|group
operator|.
name|SystemGroupBackend
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
name|ProjectCache
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
name|gson
operator|.
name|Gson
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|reflect
operator|.
name|TypeToken
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
name|jcraft
operator|.
name|jsch
operator|.
name|JSchException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpStatus
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
name|Repository
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
name|revwalk
operator|.
name|RevCommit
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
name|revwalk
operator|.
name|RevWalk
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
name|treewalk
operator|.
name|TreeWalk
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|Collections
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
DECL|class|CreateProjectIT
specifier|public
class|class
name|CreateProjectIT
extends|extends
name|AbstractDaemonTest
block|{
annotation|@
name|Inject
DECL|field|accounts
specifier|private
name|AccountCreator
name|accounts
decl_stmt|;
annotation|@
name|Inject
DECL|field|projectCache
specifier|private
name|ProjectCache
name|projectCache
decl_stmt|;
annotation|@
name|Inject
DECL|field|groupCache
specifier|private
name|GroupCache
name|groupCache
decl_stmt|;
annotation|@
name|Inject
DECL|field|git
specifier|private
name|GitRepositoryManager
name|git
decl_stmt|;
DECL|field|admin
specifier|private
name|TestAccount
name|admin
decl_stmt|;
DECL|field|session
specifier|private
name|RestSession
name|session
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|admin
operator|=
name|accounts
operator|.
name|create
argument_list|(
literal|"admin"
argument_list|,
literal|"admin@example.com"
argument_list|,
literal|"Administrator"
argument_list|,
literal|"Administrators"
argument_list|)
expr_stmt|;
name|session
operator|=
operator|new
name|RestSession
argument_list|(
name|server
argument_list|,
name|admin
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateProject ()
specifier|public
name|void
name|testCreateProject
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|newProjectName
init|=
literal|"newProject"
decl_stmt|;
name|RestResponse
name|r
init|=
name|session
operator|.
name|put
argument_list|(
literal|"/projects/"
operator|+
name|newProjectName
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_CREATED
argument_list|,
name|r
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|ProjectInfo
name|p
init|=
operator|(
operator|new
name|Gson
argument_list|()
operator|)
operator|.
name|fromJson
argument_list|(
name|r
operator|.
name|getReader
argument_list|()
argument_list|,
operator|new
name|TypeToken
argument_list|<
name|ProjectInfo
argument_list|>
argument_list|()
block|{}
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|newProjectName
argument_list|,
name|p
operator|.
name|name
argument_list|)
expr_stmt|;
name|ProjectState
name|projectState
init|=
name|projectCache
operator|.
name|get
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
name|newProjectName
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|projectState
argument_list|)
expr_stmt|;
name|assertProjectInfo
argument_list|(
name|projectState
operator|.
name|getProject
argument_list|()
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|assertHead
argument_list|(
name|newProjectName
argument_list|,
literal|"refs/heads/master"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateProjectWithNameMismatch_BadRequest ()
specifier|public
name|void
name|testCreateProjectWithNameMismatch_BadRequest
parameter_list|()
throws|throws
name|IOException
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
literal|"otherName"
expr_stmt|;
name|RestResponse
name|r
init|=
name|session
operator|.
name|put
argument_list|(
literal|"/projects/someName"
argument_list|,
name|in
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_BAD_REQUEST
argument_list|,
name|r
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateProjectWithProperties ()
specifier|public
name|void
name|testCreateProjectWithProperties
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|newProjectName
init|=
literal|"newProject"
decl_stmt|;
name|ProjectInput
name|in
init|=
operator|new
name|ProjectInput
argument_list|()
decl_stmt|;
name|in
operator|.
name|description
operator|=
literal|"Test description"
expr_stmt|;
name|in
operator|.
name|submit_type
operator|=
name|SubmitType
operator|.
name|CHERRY_PICK
expr_stmt|;
name|in
operator|.
name|use_contributor_agreements
operator|=
name|InheritableBoolean
operator|.
name|TRUE
expr_stmt|;
name|in
operator|.
name|use_signed_off_by
operator|=
name|InheritableBoolean
operator|.
name|TRUE
expr_stmt|;
name|in
operator|.
name|use_content_merge
operator|=
name|InheritableBoolean
operator|.
name|TRUE
expr_stmt|;
name|in
operator|.
name|require_change_id
operator|=
name|InheritableBoolean
operator|.
name|TRUE
expr_stmt|;
name|RestResponse
name|r
init|=
name|session
operator|.
name|put
argument_list|(
literal|"/projects/"
operator|+
name|newProjectName
argument_list|,
name|in
argument_list|)
decl_stmt|;
name|ProjectInfo
name|p
init|=
operator|(
operator|new
name|Gson
argument_list|()
operator|)
operator|.
name|fromJson
argument_list|(
name|r
operator|.
name|getReader
argument_list|()
argument_list|,
operator|new
name|TypeToken
argument_list|<
name|ProjectInfo
argument_list|>
argument_list|()
block|{}
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|newProjectName
argument_list|,
name|p
operator|.
name|name
argument_list|)
expr_stmt|;
name|Project
name|project
init|=
name|projectCache
operator|.
name|get
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
name|newProjectName
argument_list|)
argument_list|)
operator|.
name|getProject
argument_list|()
decl_stmt|;
name|assertProjectInfo
argument_list|(
name|project
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|in
operator|.
name|description
argument_list|,
name|project
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|in
operator|.
name|submit_type
argument_list|,
name|project
operator|.
name|getSubmitType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|in
operator|.
name|use_contributor_agreements
argument_list|,
name|project
operator|.
name|getUseContributorAgreements
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|in
operator|.
name|use_signed_off_by
argument_list|,
name|project
operator|.
name|getUseSignedOffBy
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|in
operator|.
name|use_content_merge
argument_list|,
name|project
operator|.
name|getUseContentMerge
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|in
operator|.
name|require_change_id
argument_list|,
name|project
operator|.
name|getRequireChangeID
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateChildProject ()
specifier|public
name|void
name|testCreateChildProject
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|parentName
init|=
literal|"parent"
decl_stmt|;
name|RestResponse
name|r
init|=
name|session
operator|.
name|put
argument_list|(
literal|"/projects/"
operator|+
name|parentName
argument_list|)
decl_stmt|;
name|r
operator|.
name|consume
argument_list|()
expr_stmt|;
specifier|final
name|String
name|childName
init|=
literal|"child"
decl_stmt|;
name|ProjectInput
name|in
init|=
operator|new
name|ProjectInput
argument_list|()
decl_stmt|;
name|in
operator|.
name|parent
operator|=
name|parentName
expr_stmt|;
name|r
operator|=
name|session
operator|.
name|put
argument_list|(
literal|"/projects/"
operator|+
name|childName
argument_list|,
name|in
argument_list|)
expr_stmt|;
name|Project
name|project
init|=
name|projectCache
operator|.
name|get
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
name|childName
argument_list|)
argument_list|)
operator|.
name|getProject
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|in
operator|.
name|parent
argument_list|,
name|project
operator|.
name|getParentName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testCreateChildProjectUnderNonExistingParent_UnprocessableEntity ()
specifier|public
name|void
name|testCreateChildProjectUnderNonExistingParent_UnprocessableEntity
parameter_list|()
throws|throws
name|IOException
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
name|parent
operator|=
literal|"non-existing-project"
expr_stmt|;
name|RestResponse
name|r
init|=
name|session
operator|.
name|put
argument_list|(
literal|"/projects/child"
argument_list|,
name|in
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_UNPROCESSABLE_ENTITY
argument_list|,
name|r
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateProjectWithOwner ()
specifier|public
name|void
name|testCreateProjectWithOwner
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|newProjectName
init|=
literal|"newProject"
decl_stmt|;
name|ProjectInput
name|in
init|=
operator|new
name|ProjectInput
argument_list|()
decl_stmt|;
name|in
operator|.
name|owners
operator|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|in
operator|.
name|owners
operator|.
name|add
argument_list|(
literal|"Anonymous Users"
argument_list|)
expr_stmt|;
comment|// by name
name|in
operator|.
name|owners
operator|.
name|add
argument_list|(
name|SystemGroupBackend
operator|.
name|REGISTERED_USERS
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
comment|// by UUID
name|in
operator|.
name|owners
operator|.
name|add
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|groupCache
operator|.
name|get
argument_list|(
operator|new
name|AccountGroup
operator|.
name|NameKey
argument_list|(
literal|"Administrators"
argument_list|)
argument_list|)
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// by ID
name|session
operator|.
name|put
argument_list|(
literal|"/projects/"
operator|+
name|newProjectName
argument_list|,
name|in
argument_list|)
expr_stmt|;
name|ProjectState
name|projectState
init|=
name|projectCache
operator|.
name|get
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
name|newProjectName
argument_list|)
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|expectedOwnerIds
init|=
name|Sets
operator|.
name|newHashSetWithExpectedSize
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|expectedOwnerIds
operator|.
name|add
argument_list|(
name|SystemGroupBackend
operator|.
name|ANONYMOUS_USERS
argument_list|)
expr_stmt|;
name|expectedOwnerIds
operator|.
name|add
argument_list|(
name|SystemGroupBackend
operator|.
name|REGISTERED_USERS
argument_list|)
expr_stmt|;
name|expectedOwnerIds
operator|.
name|add
argument_list|(
name|groupUuid
argument_list|(
literal|"Administrators"
argument_list|)
argument_list|)
expr_stmt|;
name|assertProjectOwners
argument_list|(
name|expectedOwnerIds
argument_list|,
name|projectState
argument_list|)
expr_stmt|;
block|}
DECL|method|testCreateProjectWithNonExistingOwner_UnprocessableEntity ()
specifier|public
name|void
name|testCreateProjectWithNonExistingOwner_UnprocessableEntity
parameter_list|()
throws|throws
name|IOException
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
name|owners
operator|=
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"non-existing-group"
argument_list|)
expr_stmt|;
name|RestResponse
name|r
init|=
name|session
operator|.
name|put
argument_list|(
literal|"/projects/newProject"
argument_list|,
name|in
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_UNPROCESSABLE_ENTITY
argument_list|,
name|r
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreatePermissionOnlyProject ()
specifier|public
name|void
name|testCreatePermissionOnlyProject
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|newProjectName
init|=
literal|"newProject"
decl_stmt|;
name|ProjectInput
name|in
init|=
operator|new
name|ProjectInput
argument_list|()
decl_stmt|;
name|in
operator|.
name|permissions_only
operator|=
literal|true
expr_stmt|;
name|session
operator|.
name|put
argument_list|(
literal|"/projects/"
operator|+
name|newProjectName
argument_list|,
name|in
argument_list|)
expr_stmt|;
name|assertHead
argument_list|(
name|newProjectName
argument_list|,
name|RefNames
operator|.
name|REFS_CONFIG
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateProjectWithEmptyCommit ()
specifier|public
name|void
name|testCreateProjectWithEmptyCommit
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|newProjectName
init|=
literal|"newProject"
decl_stmt|;
name|ProjectInput
name|in
init|=
operator|new
name|ProjectInput
argument_list|()
decl_stmt|;
name|in
operator|.
name|create_empty_commit
operator|=
literal|true
expr_stmt|;
name|session
operator|.
name|put
argument_list|(
literal|"/projects/"
operator|+
name|newProjectName
argument_list|,
name|in
argument_list|)
expr_stmt|;
name|assertEmptyCommit
argument_list|(
name|newProjectName
argument_list|,
literal|"refs/heads/master"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateProjectWithBranches ()
specifier|public
name|void
name|testCreateProjectWithBranches
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|newProjectName
init|=
literal|"newProject"
decl_stmt|;
name|ProjectInput
name|in
init|=
operator|new
name|ProjectInput
argument_list|()
decl_stmt|;
name|in
operator|.
name|create_empty_commit
operator|=
literal|true
expr_stmt|;
name|in
operator|.
name|branches
operator|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|in
operator|.
name|branches
operator|.
name|add
argument_list|(
literal|"refs/heads/test"
argument_list|)
expr_stmt|;
name|in
operator|.
name|branches
operator|.
name|add
argument_list|(
literal|"refs/heads/master"
argument_list|)
expr_stmt|;
name|in
operator|.
name|branches
operator|.
name|add
argument_list|(
literal|"release"
argument_list|)
expr_stmt|;
comment|// without 'refs/heads' prefix
name|session
operator|.
name|put
argument_list|(
literal|"/projects/"
operator|+
name|newProjectName
argument_list|,
name|in
argument_list|)
expr_stmt|;
name|assertHead
argument_list|(
name|newProjectName
argument_list|,
literal|"refs/heads/test"
argument_list|)
expr_stmt|;
name|assertEmptyCommit
argument_list|(
name|newProjectName
argument_list|,
literal|"refs/heads/test"
argument_list|,
literal|"refs/heads/master"
argument_list|,
literal|"refs/heads/release"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateProjectWithoutCapability_Forbidden ()
specifier|public
name|void
name|testCreateProjectWithoutCapability_Forbidden
parameter_list|()
throws|throws
name|OrmException
throws|,
name|JSchException
throws|,
name|IOException
block|{
name|TestAccount
name|user
init|=
name|accounts
operator|.
name|create
argument_list|(
literal|"user"
argument_list|,
literal|"user@example.com"
argument_list|,
literal|"User"
argument_list|)
decl_stmt|;
name|RestResponse
name|r
init|=
operator|new
name|RestSession
argument_list|(
name|server
argument_list|,
name|user
argument_list|)
operator|.
name|put
argument_list|(
literal|"/projects/newProject"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_FORBIDDEN
argument_list|,
name|r
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateProjectWhenProjectAlreadyExists_Conflict ()
specifier|public
name|void
name|testCreateProjectWhenProjectAlreadyExists_Conflict
parameter_list|()
throws|throws
name|OrmException
throws|,
name|JSchException
throws|,
name|IOException
block|{
name|RestResponse
name|r
init|=
name|session
operator|.
name|put
argument_list|(
literal|"/projects/All-Projects"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_CONFLICT
argument_list|,
name|r
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|groupUuid (String groupName)
specifier|private
name|AccountGroup
operator|.
name|UUID
name|groupUuid
parameter_list|(
name|String
name|groupName
parameter_list|)
block|{
return|return
name|groupCache
operator|.
name|get
argument_list|(
operator|new
name|AccountGroup
operator|.
name|NameKey
argument_list|(
name|groupName
argument_list|)
argument_list|)
operator|.
name|getGroupUUID
argument_list|()
return|;
block|}
DECL|method|assertHead (String projectName, String expectedRef)
specifier|private
name|void
name|assertHead
parameter_list|(
name|String
name|projectName
parameter_list|,
name|String
name|expectedRef
parameter_list|)
throws|throws
name|RepositoryNotFoundException
throws|,
name|IOException
block|{
name|Repository
name|repo
init|=
name|git
operator|.
name|openRepository
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
name|projectName
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
name|expectedRef
argument_list|,
name|repo
operator|.
name|getRef
argument_list|(
name|Constants
operator|.
name|HEAD
argument_list|)
operator|.
name|getTarget
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|repo
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|assertEmptyCommit (String projectName, String... refs)
specifier|private
name|void
name|assertEmptyCommit
parameter_list|(
name|String
name|projectName
parameter_list|,
name|String
modifier|...
name|refs
parameter_list|)
throws|throws
name|RepositoryNotFoundException
throws|,
name|IOException
block|{
name|Repository
name|repo
init|=
name|git
operator|.
name|openRepository
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
name|projectName
argument_list|)
argument_list|)
decl_stmt|;
name|RevWalk
name|rw
init|=
operator|new
name|RevWalk
argument_list|(
name|repo
argument_list|)
decl_stmt|;
name|TreeWalk
name|tw
init|=
operator|new
name|TreeWalk
argument_list|(
name|repo
argument_list|)
decl_stmt|;
try|try
block|{
for|for
control|(
name|String
name|ref
range|:
name|refs
control|)
block|{
name|RevCommit
name|commit
init|=
name|rw
operator|.
name|lookupCommit
argument_list|(
name|repo
operator|.
name|getRef
argument_list|(
name|ref
argument_list|)
operator|.
name|getObjectId
argument_list|()
argument_list|)
decl_stmt|;
name|rw
operator|.
name|parseBody
argument_list|(
name|commit
argument_list|)
expr_stmt|;
name|tw
operator|.
name|addTree
argument_list|(
name|commit
operator|.
name|getTree
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"ref "
operator|+
name|ref
operator|+
literal|" has non empty commit"
argument_list|,
name|tw
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|tw
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|tw
operator|.
name|release
argument_list|()
expr_stmt|;
name|rw
operator|.
name|release
argument_list|()
expr_stmt|;
name|repo
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
DECL|class|ProjectInput
specifier|private
specifier|static
class|class
name|ProjectInput
block|{
DECL|field|name
name|String
name|name
decl_stmt|;
DECL|field|parent
name|String
name|parent
decl_stmt|;
DECL|field|description
name|String
name|description
decl_stmt|;
DECL|field|permissions_only
name|boolean
name|permissions_only
decl_stmt|;
DECL|field|create_empty_commit
name|boolean
name|create_empty_commit
decl_stmt|;
DECL|field|submit_type
name|SubmitType
name|submit_type
decl_stmt|;
DECL|field|branches
name|List
argument_list|<
name|String
argument_list|>
name|branches
decl_stmt|;
DECL|field|owners
name|List
argument_list|<
name|String
argument_list|>
name|owners
decl_stmt|;
DECL|field|use_contributor_agreements
name|InheritableBoolean
name|use_contributor_agreements
decl_stmt|;
DECL|field|use_signed_off_by
name|InheritableBoolean
name|use_signed_off_by
decl_stmt|;
DECL|field|use_content_merge
name|InheritableBoolean
name|use_content_merge
decl_stmt|;
DECL|field|require_change_id
name|InheritableBoolean
name|require_change_id
decl_stmt|;
block|}
block|}
end_class

end_unit

