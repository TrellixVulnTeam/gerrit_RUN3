begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2017 The Android Open Source Project
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
DECL|package|com.google.gerrit.acceptance.api.project
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|api
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
name|acceptance
operator|.
name|testsuite
operator|.
name|group
operator|.
name|GroupOperations
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
name|testsuite
operator|.
name|project
operator|.
name|ProjectOperations
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
name|Permission
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
name|config
operator|.
name|AccessCheckInfo
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
name|config
operator|.
name|AccessCheckInput
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
name|ProjectConfig
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
name|testing
operator|.
name|Util
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
name|List
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
name|RefUpdate
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
name|RefUpdate
operator|.
name|Result
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

begin_class
DECL|class|CheckAccessIT
specifier|public
class|class
name|CheckAccessIT
extends|extends
name|AbstractDaemonTest
block|{
DECL|field|projectOperations
annotation|@
name|Inject
specifier|private
name|ProjectOperations
name|projectOperations
decl_stmt|;
DECL|field|groupOperations
annotation|@
name|Inject
specifier|private
name|GroupOperations
name|groupOperations
decl_stmt|;
DECL|field|normalProject
specifier|private
name|Project
operator|.
name|NameKey
name|normalProject
decl_stmt|;
DECL|field|secretProject
specifier|private
name|Project
operator|.
name|NameKey
name|secretProject
decl_stmt|;
DECL|field|secretRefProject
specifier|private
name|Project
operator|.
name|NameKey
name|secretRefProject
decl_stmt|;
DECL|field|privilegedUser
specifier|private
name|TestAccount
name|privilegedUser
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
name|normalProject
operator|=
name|projectOperations
operator|.
name|newProject
argument_list|()
operator|.
name|create
argument_list|()
expr_stmt|;
name|secretProject
operator|=
name|projectOperations
operator|.
name|newProject
argument_list|()
operator|.
name|create
argument_list|()
expr_stmt|;
name|secretRefProject
operator|=
name|projectOperations
operator|.
name|newProject
argument_list|()
operator|.
name|create
argument_list|()
expr_stmt|;
name|AccountGroup
operator|.
name|UUID
name|privilegedGroupUuid
init|=
name|groupOperations
operator|.
name|newGroup
argument_list|()
operator|.
name|name
argument_list|(
name|name
argument_list|(
literal|"privilegedGroup"
argument_list|)
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|privilegedUser
operator|=
name|accountCreator
operator|.
name|create
argument_list|(
literal|"privilegedUser"
argument_list|,
literal|"snowden@nsa.gov"
argument_list|,
literal|"Ed Snowden"
argument_list|)
expr_stmt|;
name|groupOperations
operator|.
name|group
argument_list|(
name|privilegedGroupUuid
argument_list|)
operator|.
name|forUpdate
argument_list|()
operator|.
name|addMember
argument_list|(
name|privilegedUser
operator|.
name|id
argument_list|)
operator|.
name|update
argument_list|()
expr_stmt|;
try|try
init|(
name|ProjectConfigUpdate
name|u
init|=
name|updateProject
argument_list|(
name|secretProject
argument_list|)
init|)
block|{
name|ProjectConfig
name|cfg
init|=
name|u
operator|.
name|getConfig
argument_list|()
decl_stmt|;
name|Util
operator|.
name|allow
argument_list|(
name|cfg
argument_list|,
name|Permission
operator|.
name|READ
argument_list|,
name|privilegedGroupUuid
argument_list|,
literal|"refs/*"
argument_list|)
expr_stmt|;
name|Util
operator|.
name|block
argument_list|(
name|cfg
argument_list|,
name|Permission
operator|.
name|READ
argument_list|,
name|SystemGroupBackend
operator|.
name|REGISTERED_USERS
argument_list|,
literal|"refs/*"
argument_list|)
expr_stmt|;
name|u
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
try|try
init|(
name|ProjectConfigUpdate
name|u
init|=
name|updateProject
argument_list|(
name|secretRefProject
argument_list|)
init|)
block|{
name|ProjectConfig
name|cfg
init|=
name|u
operator|.
name|getConfig
argument_list|()
decl_stmt|;
name|Util
operator|.
name|deny
argument_list|(
name|cfg
argument_list|,
name|Permission
operator|.
name|READ
argument_list|,
name|SystemGroupBackend
operator|.
name|ANONYMOUS_USERS
argument_list|,
literal|"refs/*"
argument_list|)
expr_stmt|;
name|Util
operator|.
name|allow
argument_list|(
name|cfg
argument_list|,
name|Permission
operator|.
name|READ
argument_list|,
name|privilegedGroupUuid
argument_list|,
literal|"refs/heads/secret/*"
argument_list|)
expr_stmt|;
name|Util
operator|.
name|block
argument_list|(
name|cfg
argument_list|,
name|Permission
operator|.
name|READ
argument_list|,
name|SystemGroupBackend
operator|.
name|REGISTERED_USERS
argument_list|,
literal|"refs/heads/secret/*"
argument_list|)
expr_stmt|;
name|Util
operator|.
name|allow
argument_list|(
name|cfg
argument_list|,
name|Permission
operator|.
name|READ
argument_list|,
name|SystemGroupBackend
operator|.
name|REGISTERED_USERS
argument_list|,
literal|"refs/heads/*"
argument_list|)
expr_stmt|;
name|u
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
comment|// Ref permission
try|try
init|(
name|ProjectConfigUpdate
name|u
init|=
name|updateProject
argument_list|(
name|normalProject
argument_list|)
init|)
block|{
name|ProjectConfig
name|cfg
init|=
name|u
operator|.
name|getConfig
argument_list|()
decl_stmt|;
name|Util
operator|.
name|allow
argument_list|(
name|cfg
argument_list|,
name|Permission
operator|.
name|VIEW_PRIVATE_CHANGES
argument_list|,
name|privilegedGroupUuid
argument_list|,
literal|"refs/*"
argument_list|)
expr_stmt|;
name|Util
operator|.
name|allow
argument_list|(
name|cfg
argument_list|,
name|Permission
operator|.
name|FORGE_SERVER
argument_list|,
name|privilegedGroupUuid
argument_list|,
literal|"refs/*"
argument_list|)
expr_stmt|;
name|u
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|emptyInput ()
specifier|public
name|void
name|emptyInput
parameter_list|()
throws|throws
name|Exception
block|{
name|exception
operator|.
name|expect
argument_list|(
name|BadRequestException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"input requires 'account'"
argument_list|)
expr_stmt|;
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|name
argument_list|(
name|normalProject
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|checkAccess
argument_list|(
operator|new
name|AccessCheckInput
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|nonexistentPermission ()
specifier|public
name|void
name|nonexistentPermission
parameter_list|()
throws|throws
name|Exception
block|{
name|AccessCheckInput
name|in
init|=
operator|new
name|AccessCheckInput
argument_list|()
decl_stmt|;
name|in
operator|.
name|account
operator|=
name|user
operator|.
name|email
expr_stmt|;
name|in
operator|.
name|permission
operator|=
literal|"notapermission"
expr_stmt|;
name|in
operator|.
name|ref
operator|=
literal|"refs/heads/master"
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|BadRequestException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"not recognized"
argument_list|)
expr_stmt|;
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|name
argument_list|(
name|normalProject
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|checkAccess
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|permissionLacksRef ()
specifier|public
name|void
name|permissionLacksRef
parameter_list|()
throws|throws
name|Exception
block|{
name|AccessCheckInput
name|in
init|=
operator|new
name|AccessCheckInput
argument_list|()
decl_stmt|;
name|in
operator|.
name|account
operator|=
name|user
operator|.
name|email
expr_stmt|;
name|in
operator|.
name|permission
operator|=
literal|"forge_author"
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|BadRequestException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"must set 'ref'"
argument_list|)
expr_stmt|;
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|name
argument_list|(
name|normalProject
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|checkAccess
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|changePermission ()
specifier|public
name|void
name|changePermission
parameter_list|()
throws|throws
name|Exception
block|{
name|AccessCheckInput
name|in
init|=
operator|new
name|AccessCheckInput
argument_list|()
decl_stmt|;
name|in
operator|.
name|account
operator|=
name|user
operator|.
name|email
expr_stmt|;
name|in
operator|.
name|permission
operator|=
literal|"rebase"
expr_stmt|;
name|in
operator|.
name|ref
operator|=
literal|"refs/heads/master"
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|BadRequestException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"recognized as ref permission"
argument_list|)
expr_stmt|;
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|name
argument_list|(
name|normalProject
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|checkAccess
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|nonexistentEmail ()
specifier|public
name|void
name|nonexistentEmail
parameter_list|()
throws|throws
name|Exception
block|{
name|AccessCheckInput
name|in
init|=
operator|new
name|AccessCheckInput
argument_list|()
decl_stmt|;
name|in
operator|.
name|account
operator|=
literal|"doesnotexist@invalid.com"
expr_stmt|;
name|in
operator|.
name|permission
operator|=
literal|"rebase"
expr_stmt|;
name|in
operator|.
name|ref
operator|=
literal|"refs/heads/master"
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|UnprocessableEntityException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"Account 'doesnotexist@invalid.com' not found"
argument_list|)
expr_stmt|;
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|name
argument_list|(
name|normalProject
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|checkAccess
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
DECL|class|TestCase
specifier|private
specifier|static
class|class
name|TestCase
block|{
DECL|field|input
name|AccessCheckInput
name|input
decl_stmt|;
DECL|field|project
name|String
name|project
decl_stmt|;
DECL|field|permission
name|String
name|permission
decl_stmt|;
DECL|field|want
name|int
name|want
decl_stmt|;
DECL|method|project (String mail, String project, int want)
specifier|static
name|TestCase
name|project
parameter_list|(
name|String
name|mail
parameter_list|,
name|String
name|project
parameter_list|,
name|int
name|want
parameter_list|)
block|{
name|TestCase
name|t
init|=
operator|new
name|TestCase
argument_list|()
decl_stmt|;
name|t
operator|.
name|input
operator|=
operator|new
name|AccessCheckInput
argument_list|()
expr_stmt|;
name|t
operator|.
name|input
operator|.
name|account
operator|=
name|mail
expr_stmt|;
name|t
operator|.
name|project
operator|=
name|project
expr_stmt|;
name|t
operator|.
name|want
operator|=
name|want
expr_stmt|;
return|return
name|t
return|;
block|}
DECL|method|projectRef (String mail, String project, String ref, int want)
specifier|static
name|TestCase
name|projectRef
parameter_list|(
name|String
name|mail
parameter_list|,
name|String
name|project
parameter_list|,
name|String
name|ref
parameter_list|,
name|int
name|want
parameter_list|)
block|{
name|TestCase
name|t
init|=
operator|new
name|TestCase
argument_list|()
decl_stmt|;
name|t
operator|.
name|input
operator|=
operator|new
name|AccessCheckInput
argument_list|()
expr_stmt|;
name|t
operator|.
name|input
operator|.
name|account
operator|=
name|mail
expr_stmt|;
name|t
operator|.
name|input
operator|.
name|ref
operator|=
name|ref
expr_stmt|;
name|t
operator|.
name|project
operator|=
name|project
expr_stmt|;
name|t
operator|.
name|want
operator|=
name|want
expr_stmt|;
return|return
name|t
return|;
block|}
DECL|method|projectRefPerm ( String mail, String project, String ref, String permission, int want)
specifier|static
name|TestCase
name|projectRefPerm
parameter_list|(
name|String
name|mail
parameter_list|,
name|String
name|project
parameter_list|,
name|String
name|ref
parameter_list|,
name|String
name|permission
parameter_list|,
name|int
name|want
parameter_list|)
block|{
name|TestCase
name|t
init|=
operator|new
name|TestCase
argument_list|()
decl_stmt|;
name|t
operator|.
name|input
operator|=
operator|new
name|AccessCheckInput
argument_list|()
expr_stmt|;
name|t
operator|.
name|input
operator|.
name|account
operator|=
name|mail
expr_stmt|;
name|t
operator|.
name|input
operator|.
name|ref
operator|=
name|ref
expr_stmt|;
name|t
operator|.
name|input
operator|.
name|permission
operator|=
name|permission
expr_stmt|;
name|t
operator|.
name|project
operator|=
name|project
expr_stmt|;
name|t
operator|.
name|want
operator|=
name|want
expr_stmt|;
return|return
name|t
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|httpGet ()
specifier|public
name|void
name|httpGet
parameter_list|()
throws|throws
name|Exception
block|{
name|RestResponse
name|rep
init|=
name|adminRestSession
operator|.
name|get
argument_list|(
literal|"/projects/"
operator|+
name|normalProject
operator|.
name|get
argument_list|()
operator|+
literal|"/check.access"
operator|+
literal|"?ref=refs/heads/master&perm=viewPrivateChanges&account="
operator|+
name|user
operator|.
name|email
argument_list|)
decl_stmt|;
name|rep
operator|.
name|assertOK
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|rep
operator|.
name|getEntityContent
argument_list|()
argument_list|)
operator|.
name|contains
argument_list|(
literal|"403"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|accessible ()
specifier|public
name|void
name|accessible
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|TestCase
argument_list|>
name|inputs
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|TestCase
operator|.
name|projectRefPerm
argument_list|(
name|user
operator|.
name|email
argument_list|,
name|normalProject
operator|.
name|get
argument_list|()
argument_list|,
literal|"refs/heads/master"
argument_list|,
name|Permission
operator|.
name|VIEW_PRIVATE_CHANGES
argument_list|,
literal|403
argument_list|)
argument_list|,
name|TestCase
operator|.
name|project
argument_list|(
name|user
operator|.
name|email
argument_list|,
name|normalProject
operator|.
name|get
argument_list|()
argument_list|,
literal|200
argument_list|)
argument_list|,
name|TestCase
operator|.
name|project
argument_list|(
name|user
operator|.
name|email
argument_list|,
name|secretProject
operator|.
name|get
argument_list|()
argument_list|,
literal|403
argument_list|)
argument_list|,
name|TestCase
operator|.
name|projectRef
argument_list|(
name|user
operator|.
name|email
argument_list|,
name|secretRefProject
operator|.
name|get
argument_list|()
argument_list|,
literal|"refs/heads/secret/master"
argument_list|,
literal|403
argument_list|)
argument_list|,
name|TestCase
operator|.
name|projectRef
argument_list|(
name|privilegedUser
operator|.
name|email
argument_list|,
name|secretRefProject
operator|.
name|get
argument_list|()
argument_list|,
literal|"refs/heads/secret/master"
argument_list|,
literal|200
argument_list|)
argument_list|,
name|TestCase
operator|.
name|projectRef
argument_list|(
name|privilegedUser
operator|.
name|email
argument_list|,
name|normalProject
operator|.
name|get
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|200
argument_list|)
argument_list|,
name|TestCase
operator|.
name|projectRef
argument_list|(
name|privilegedUser
operator|.
name|email
argument_list|,
name|secretProject
operator|.
name|get
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|200
argument_list|)
argument_list|,
name|TestCase
operator|.
name|projectRef
argument_list|(
name|privilegedUser
operator|.
name|email
argument_list|,
name|secretProject
operator|.
name|get
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|200
argument_list|)
argument_list|,
name|TestCase
operator|.
name|projectRefPerm
argument_list|(
name|privilegedUser
operator|.
name|email
argument_list|,
name|normalProject
operator|.
name|get
argument_list|()
argument_list|,
literal|"refs/heads/master"
argument_list|,
name|Permission
operator|.
name|VIEW_PRIVATE_CHANGES
argument_list|,
literal|200
argument_list|)
argument_list|,
name|TestCase
operator|.
name|projectRefPerm
argument_list|(
name|privilegedUser
operator|.
name|email
argument_list|,
name|normalProject
operator|.
name|get
argument_list|()
argument_list|,
literal|"refs/heads/master"
argument_list|,
name|Permission
operator|.
name|FORGE_SERVER
argument_list|,
literal|200
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|TestCase
name|tc
range|:
name|inputs
control|)
block|{
name|String
name|in
init|=
name|newGson
argument_list|()
operator|.
name|toJson
argument_list|(
name|tc
operator|.
name|input
argument_list|)
decl_stmt|;
name|AccessCheckInfo
name|info
init|=
literal|null
decl_stmt|;
try|try
block|{
name|info
operator|=
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|name
argument_list|(
name|tc
operator|.
name|project
argument_list|)
operator|.
name|checkAccess
argument_list|(
name|tc
operator|.
name|input
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RestApiException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"check.access(%s, %s): exception %s"
argument_list|,
name|tc
operator|.
name|project
argument_list|,
name|in
argument_list|,
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|int
name|want
init|=
name|tc
operator|.
name|want
decl_stmt|;
if|if
condition|(
name|want
operator|!=
name|info
operator|.
name|status
condition|)
block|{
name|fail
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"check.access(%s, %s) = %d, want %d"
argument_list|,
name|tc
operator|.
name|project
argument_list|,
name|in
argument_list|,
name|info
operator|.
name|status
argument_list|,
name|want
argument_list|)
argument_list|)
expr_stmt|;
block|}
switch|switch
condition|(
name|want
condition|)
block|{
case|case
literal|403
case|:
if|if
condition|(
name|tc
operator|.
name|permission
operator|!=
literal|null
condition|)
block|{
name|assertThat
argument_list|(
name|info
operator|.
name|message
argument_list|)
operator|.
name|contains
argument_list|(
literal|"lacks permission "
operator|+
name|tc
operator|.
name|permission
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
literal|404
case|:
name|assertThat
argument_list|(
name|info
operator|.
name|message
argument_list|)
operator|.
name|contains
argument_list|(
literal|"does not exist"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|200
case|:
name|assertThat
argument_list|(
name|info
operator|.
name|message
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
break|break;
default|default:
name|fail
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"unknown code %d"
argument_list|,
name|want
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|noBranches ()
specifier|public
name|void
name|noBranches
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|Repository
name|repo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|normalProject
argument_list|)
init|)
block|{
name|RefUpdate
name|u
init|=
name|repo
operator|.
name|updateRef
argument_list|(
name|RefNames
operator|.
name|REFS_HEADS
operator|+
literal|"master"
argument_list|)
decl_stmt|;
name|u
operator|.
name|setForceUpdate
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|u
operator|.
name|delete
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|Result
operator|.
name|FORCED
argument_list|)
expr_stmt|;
block|}
name|AccessCheckInput
name|input
init|=
operator|new
name|AccessCheckInput
argument_list|()
decl_stmt|;
name|input
operator|.
name|account
operator|=
name|privilegedUser
operator|.
name|email
expr_stmt|;
name|AccessCheckInfo
name|info
init|=
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|name
argument_list|(
name|normalProject
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|checkAccess
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|status
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|200
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|message
argument_list|)
operator|.
name|contains
argument_list|(
literal|"no branches"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

