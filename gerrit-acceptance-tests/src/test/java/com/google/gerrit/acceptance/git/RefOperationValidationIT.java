begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2019 The Android Open Source Project
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
DECL|package|com.google.gerrit.acceptance.git
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|git
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
name|assert_
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
name|GitUtil
operator|.
name|deleteRef
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Constants
operator|.
name|HEAD
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|transport
operator|.
name|ReceiveCommand
operator|.
name|Type
operator|.
name|CREATE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|transport
operator|.
name|ReceiveCommand
operator|.
name|Type
operator|.
name|DELETE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|transport
operator|.
name|ReceiveCommand
operator|.
name|Type
operator|.
name|UPDATE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|transport
operator|.
name|ReceiveCommand
operator|.
name|Type
operator|.
name|UPDATE_NONFASTFORWARD
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
name|PushOneCommit
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
name|projects
operator|.
name|BranchInput
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
name|DynamicSet
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
name|RegistrationHandle
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
name|server
operator|.
name|events
operator|.
name|RefReceivedEvent
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
name|validators
operator|.
name|RefOperationValidationListener
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
name|validators
operator|.
name|ValidationMessage
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
name|validators
operator|.
name|ValidationException
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
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|ObjectId
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
name|transport
operator|.
name|PushResult
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
name|transport
operator|.
name|ReceiveCommand
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
name|transport
operator|.
name|RemoteRefUpdate
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
DECL|class|RefOperationValidationIT
specifier|public
class|class
name|RefOperationValidationIT
extends|extends
name|AbstractDaemonTest
block|{
DECL|field|TEST_REF
specifier|private
specifier|static
specifier|final
name|String
name|TEST_REF
init|=
literal|"refs/heads/protected"
decl_stmt|;
DECL|field|validators
annotation|@
name|Inject
name|DynamicSet
argument_list|<
name|RefOperationValidationListener
argument_list|>
name|validators
decl_stmt|;
DECL|class|TestRefValidator
specifier|private
class|class
name|TestRefValidator
implements|implements
name|RefOperationValidationListener
implements|,
name|AutoCloseable
block|{
DECL|field|rejectType
specifier|private
specifier|final
name|ReceiveCommand
operator|.
name|Type
name|rejectType
decl_stmt|;
DECL|field|rejectRef
specifier|private
specifier|final
name|String
name|rejectRef
decl_stmt|;
DECL|field|handle
specifier|private
specifier|final
name|RegistrationHandle
name|handle
decl_stmt|;
DECL|method|TestRefValidator (ReceiveCommand.Type rejectType)
specifier|public
name|TestRefValidator
parameter_list|(
name|ReceiveCommand
operator|.
name|Type
name|rejectType
parameter_list|)
block|{
name|this
operator|.
name|rejectType
operator|=
name|rejectType
expr_stmt|;
name|this
operator|.
name|rejectRef
operator|=
name|TEST_REF
expr_stmt|;
name|this
operator|.
name|handle
operator|=
name|validators
operator|.
name|add
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onRefOperation (RefReceivedEvent refEvent)
specifier|public
name|List
argument_list|<
name|ValidationMessage
argument_list|>
name|onRefOperation
parameter_list|(
name|RefReceivedEvent
name|refEvent
parameter_list|)
throws|throws
name|ValidationException
block|{
if|if
condition|(
name|refEvent
operator|.
name|getRefName
argument_list|()
operator|.
name|equals
argument_list|(
name|rejectRef
argument_list|)
operator|&&
name|refEvent
operator|.
name|command
operator|.
name|getType
argument_list|()
operator|.
name|equals
argument_list|(
name|rejectType
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ValidationException
argument_list|(
name|rejectType
operator|.
name|name
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|Exception
block|{
name|handle
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|rejectRefCreation ()
specifier|public
name|void
name|rejectRefCreation
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|TestRefValidator
name|validator
init|=
operator|new
name|TestRefValidator
argument_list|(
name|CREATE
argument_list|)
init|)
block|{
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|name
argument_list|(
name|project
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|branch
argument_list|(
name|TEST_REF
argument_list|)
operator|.
name|create
argument_list|(
operator|new
name|BranchInput
argument_list|()
argument_list|)
expr_stmt|;
name|assert_
argument_list|()
operator|.
name|fail
argument_list|(
literal|"expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RestApiException
name|expected
parameter_list|)
block|{
name|assertThat
argument_list|(
name|expected
argument_list|)
operator|.
name|hasMessageThat
argument_list|()
operator|.
name|contains
argument_list|(
name|CREATE
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|rejectRefCreationByPush ()
specifier|public
name|void
name|rejectRefCreationByPush
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|TestRefValidator
name|validator
init|=
operator|new
name|TestRefValidator
argument_list|(
name|CREATE
argument_list|)
init|)
block|{
name|grant
argument_list|(
name|project
argument_list|,
literal|"refs/*"
argument_list|,
name|Permission
operator|.
name|PUSH
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|PushOneCommit
name|push1
init|=
name|pushFactory
operator|.
name|create
argument_list|(
name|db
argument_list|,
name|admin
operator|.
name|getIdent
argument_list|()
argument_list|,
name|testRepo
argument_list|,
literal|"change1"
argument_list|,
literal|"a.txt"
argument_list|,
literal|"content"
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r1
init|=
name|push1
operator|.
name|to
argument_list|(
literal|"refs/heads/master"
argument_list|)
decl_stmt|;
name|r1
operator|.
name|assertOkStatus
argument_list|()
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r2
init|=
name|push1
operator|.
name|to
argument_list|(
name|TEST_REF
argument_list|)
decl_stmt|;
name|r2
operator|.
name|assertErrorStatus
argument_list|(
name|CREATE
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|rejectRefDeletion ()
specifier|public
name|void
name|rejectRefDeletion
parameter_list|()
throws|throws
name|Exception
block|{
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|name
argument_list|(
name|project
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|branch
argument_list|(
name|TEST_REF
argument_list|)
operator|.
name|create
argument_list|(
operator|new
name|BranchInput
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
name|TestRefValidator
name|validator
init|=
operator|new
name|TestRefValidator
argument_list|(
name|DELETE
argument_list|)
init|)
block|{
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|name
argument_list|(
name|project
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|branch
argument_list|(
name|TEST_REF
argument_list|)
operator|.
name|delete
argument_list|()
expr_stmt|;
name|assert_
argument_list|()
operator|.
name|fail
argument_list|(
literal|"expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RestApiException
name|expected
parameter_list|)
block|{
name|assertThat
argument_list|(
name|expected
argument_list|)
operator|.
name|hasMessageThat
argument_list|()
operator|.
name|contains
argument_list|(
name|DELETE
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|rejectRefDeletionByPush ()
specifier|public
name|void
name|rejectRefDeletionByPush
parameter_list|()
throws|throws
name|Exception
block|{
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|name
argument_list|(
name|project
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|branch
argument_list|(
name|TEST_REF
argument_list|)
operator|.
name|create
argument_list|(
operator|new
name|BranchInput
argument_list|()
argument_list|)
expr_stmt|;
name|grant
argument_list|(
name|project
argument_list|,
literal|"refs/*"
argument_list|,
name|Permission
operator|.
name|DELETE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
try|try
init|(
name|TestRefValidator
name|validator
init|=
operator|new
name|TestRefValidator
argument_list|(
name|DELETE
argument_list|)
init|)
block|{
name|PushResult
name|result
init|=
name|deleteRef
argument_list|(
name|testRepo
argument_list|,
name|TEST_REF
argument_list|)
decl_stmt|;
name|RemoteRefUpdate
name|refUpdate
init|=
name|result
operator|.
name|getRemoteUpdate
argument_list|(
name|TEST_REF
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|refUpdate
operator|.
name|getMessage
argument_list|()
argument_list|)
operator|.
name|contains
argument_list|(
name|DELETE
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|rejectRefUpdateFastForward ()
specifier|public
name|void
name|rejectRefUpdateFastForward
parameter_list|()
throws|throws
name|Exception
block|{
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|name
argument_list|(
name|project
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|branch
argument_list|(
name|TEST_REF
argument_list|)
operator|.
name|create
argument_list|(
operator|new
name|BranchInput
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
name|TestRefValidator
name|validator
init|=
operator|new
name|TestRefValidator
argument_list|(
name|UPDATE
argument_list|)
init|)
block|{
name|grant
argument_list|(
name|project
argument_list|,
literal|"refs/*"
argument_list|,
name|Permission
operator|.
name|PUSH
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|PushOneCommit
name|push1
init|=
name|pushFactory
operator|.
name|create
argument_list|(
name|db
argument_list|,
name|admin
operator|.
name|getIdent
argument_list|()
argument_list|,
name|testRepo
argument_list|,
literal|"change1"
argument_list|,
literal|"a.txt"
argument_list|,
literal|"content"
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r1
init|=
name|push1
operator|.
name|to
argument_list|(
name|TEST_REF
argument_list|)
decl_stmt|;
name|r1
operator|.
name|assertErrorStatus
argument_list|(
name|UPDATE
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|rejectRefUpdateNonFastForward ()
specifier|public
name|void
name|rejectRefUpdateNonFastForward
parameter_list|()
throws|throws
name|Exception
block|{
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|name
argument_list|(
name|project
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|branch
argument_list|(
name|TEST_REF
argument_list|)
operator|.
name|create
argument_list|(
operator|new
name|BranchInput
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
name|TestRefValidator
name|validator
init|=
operator|new
name|TestRefValidator
argument_list|(
name|UPDATE_NONFASTFORWARD
argument_list|)
init|)
block|{
name|ObjectId
name|initial
init|=
name|repo
argument_list|()
operator|.
name|exactRef
argument_list|(
name|HEAD
argument_list|)
operator|.
name|getLeaf
argument_list|()
operator|.
name|getObjectId
argument_list|()
decl_stmt|;
name|grant
argument_list|(
name|project
argument_list|,
literal|"refs/*"
argument_list|,
name|Permission
operator|.
name|PUSH
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|PushOneCommit
name|push1
init|=
name|pushFactory
operator|.
name|create
argument_list|(
name|db
argument_list|,
name|admin
operator|.
name|getIdent
argument_list|()
argument_list|,
name|testRepo
argument_list|,
literal|"change1"
argument_list|,
literal|"a.txt"
argument_list|,
literal|"content"
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r1
init|=
name|push1
operator|.
name|to
argument_list|(
name|TEST_REF
argument_list|)
decl_stmt|;
name|r1
operator|.
name|assertOkStatus
argument_list|()
expr_stmt|;
comment|// Reset HEAD to initial so the new change is a non-fast forward
name|RefUpdate
name|ru
init|=
name|repo
argument_list|()
operator|.
name|updateRef
argument_list|(
name|HEAD
argument_list|)
decl_stmt|;
name|ru
operator|.
name|setNewObjectId
argument_list|(
name|initial
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ru
operator|.
name|forceUpdate
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|RefUpdate
operator|.
name|Result
operator|.
name|FORCED
argument_list|)
expr_stmt|;
name|PushOneCommit
name|push2
init|=
name|pushFactory
operator|.
name|create
argument_list|(
name|db
argument_list|,
name|admin
operator|.
name|getIdent
argument_list|()
argument_list|,
name|testRepo
argument_list|,
literal|"change2"
argument_list|,
literal|"b.txt"
argument_list|,
literal|"content"
argument_list|)
decl_stmt|;
name|push2
operator|.
name|setForce
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r2
init|=
name|push2
operator|.
name|to
argument_list|(
name|TEST_REF
argument_list|)
decl_stmt|;
name|r2
operator|.
name|assertErrorStatus
argument_list|(
name|UPDATE_NONFASTFORWARD
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|rejectRefUpdateNonFastForwardToExistingCommit ()
specifier|public
name|void
name|rejectRefUpdateNonFastForwardToExistingCommit
parameter_list|()
throws|throws
name|Exception
block|{
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|name
argument_list|(
name|project
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|branch
argument_list|(
name|TEST_REF
argument_list|)
operator|.
name|create
argument_list|(
operator|new
name|BranchInput
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
name|TestRefValidator
name|validator
init|=
operator|new
name|TestRefValidator
argument_list|(
name|UPDATE_NONFASTFORWARD
argument_list|)
init|)
block|{
name|grant
argument_list|(
name|project
argument_list|,
literal|"refs/*"
argument_list|,
name|Permission
operator|.
name|PUSH
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|PushOneCommit
name|push1
init|=
name|pushFactory
operator|.
name|create
argument_list|(
name|db
argument_list|,
name|admin
operator|.
name|getIdent
argument_list|()
argument_list|,
name|testRepo
argument_list|,
literal|"change1"
argument_list|,
literal|"a.txt"
argument_list|,
literal|"content"
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r1
init|=
name|push1
operator|.
name|to
argument_list|(
literal|"refs/heads/master"
argument_list|)
decl_stmt|;
name|r1
operator|.
name|assertOkStatus
argument_list|()
expr_stmt|;
name|ObjectId
name|push1Id
init|=
name|r1
operator|.
name|getCommit
argument_list|()
decl_stmt|;
name|PushOneCommit
name|push2
init|=
name|pushFactory
operator|.
name|create
argument_list|(
name|db
argument_list|,
name|admin
operator|.
name|getIdent
argument_list|()
argument_list|,
name|testRepo
argument_list|,
literal|"change2"
argument_list|,
literal|"b.txt"
argument_list|,
literal|"content"
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r2
init|=
name|push2
operator|.
name|to
argument_list|(
literal|"refs/heads/master"
argument_list|)
decl_stmt|;
name|r2
operator|.
name|assertOkStatus
argument_list|()
expr_stmt|;
name|ObjectId
name|push2Id
init|=
name|r2
operator|.
name|getCommit
argument_list|()
decl_stmt|;
name|RefUpdate
name|ru
init|=
name|repo
argument_list|()
operator|.
name|updateRef
argument_list|(
name|HEAD
argument_list|)
decl_stmt|;
name|ru
operator|.
name|setNewObjectId
argument_list|(
name|push1Id
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ru
operator|.
name|forceUpdate
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|RefUpdate
operator|.
name|Result
operator|.
name|FORCED
argument_list|)
expr_stmt|;
name|PushOneCommit
name|push3
init|=
name|pushFactory
operator|.
name|create
argument_list|(
name|db
argument_list|,
name|admin
operator|.
name|getIdent
argument_list|()
argument_list|,
name|testRepo
argument_list|,
literal|"change3"
argument_list|,
literal|"c.txt"
argument_list|,
literal|"content"
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r3
init|=
name|push3
operator|.
name|to
argument_list|(
name|TEST_REF
argument_list|)
decl_stmt|;
name|r3
operator|.
name|assertOkStatus
argument_list|()
expr_stmt|;
name|ru
operator|=
name|repo
argument_list|()
operator|.
name|updateRef
argument_list|(
name|HEAD
argument_list|)
expr_stmt|;
name|ru
operator|.
name|setNewObjectId
argument_list|(
name|push2Id
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ru
operator|.
name|forceUpdate
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|RefUpdate
operator|.
name|Result
operator|.
name|FORCED
argument_list|)
expr_stmt|;
name|PushOneCommit
name|push4
init|=
name|pushFactory
operator|.
name|create
argument_list|(
name|db
argument_list|,
name|admin
operator|.
name|getIdent
argument_list|()
argument_list|,
name|testRepo
argument_list|,
literal|"change4"
argument_list|,
literal|"d.txt"
argument_list|,
literal|"content"
argument_list|)
decl_stmt|;
name|push4
operator|.
name|setForce
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r4
init|=
name|push4
operator|.
name|to
argument_list|(
name|TEST_REF
argument_list|)
decl_stmt|;
name|r4
operator|.
name|assertErrorStatus
argument_list|(
name|UPDATE_NONFASTFORWARD
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

