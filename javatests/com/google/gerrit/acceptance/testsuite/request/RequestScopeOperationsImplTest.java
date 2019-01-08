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
DECL|package|com.google.gerrit.acceptance.testsuite.request
package|package
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
name|request
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
name|AcceptanceTestRequestScope
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
name|account
operator|.
name|AccountOperations
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
name|account
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
name|IdentifiedUser
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
name|Sequences
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
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|RequestScopeOperationsImplTest
specifier|public
class|class
name|RequestScopeOperationsImplTest
extends|extends
name|AbstractDaemonTest
block|{
DECL|field|accountOperations
annotation|@
name|Inject
specifier|private
name|AccountOperations
name|accountOperations
decl_stmt|;
DECL|field|userProvider
annotation|@
name|Inject
specifier|private
name|Provider
argument_list|<
name|IdentifiedUser
argument_list|>
name|userProvider
decl_stmt|;
DECL|field|requestScopeOperations
annotation|@
name|Inject
specifier|private
name|RequestScopeOperationsImpl
name|requestScopeOperations
decl_stmt|;
DECL|field|sequences
annotation|@
name|Inject
specifier|private
name|Sequences
name|sequences
decl_stmt|;
annotation|@
name|Test
DECL|method|setApiUserToExistingUserById ()
specifier|public
name|void
name|setApiUserToExistingUserById
parameter_list|()
throws|throws
name|Exception
block|{
name|checkApiUser
argument_list|(
name|admin
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|AcceptanceTestRequestScope
operator|.
name|Context
name|oldCtx
init|=
name|requestScopeOperations
operator|.
name|setApiUser
argument_list|(
name|user
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|oldCtx
operator|.
name|getUser
argument_list|()
operator|.
name|getAccountId
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|admin
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|checkApiUser
argument_list|(
name|user
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|setApiUserToExistingUserByTestAccount ()
specifier|public
name|void
name|setApiUserToExistingUserByTestAccount
parameter_list|()
throws|throws
name|Exception
block|{
name|checkApiUser
argument_list|(
name|admin
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|TestAccount
name|testAccount
init|=
name|accountOperations
operator|.
name|account
argument_list|(
name|accountOperations
operator|.
name|newAccount
argument_list|()
operator|.
name|create
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|AcceptanceTestRequestScope
operator|.
name|Context
name|oldCtx
init|=
name|requestScopeOperations
operator|.
name|setApiUser
argument_list|(
name|testAccount
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|oldCtx
operator|.
name|getUser
argument_list|()
operator|.
name|getAccountId
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|admin
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|checkApiUser
argument_list|(
name|testAccount
operator|.
name|accountId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|setApiUserToNonExistingUser ()
specifier|public
name|void
name|setApiUserToNonExistingUser
parameter_list|()
throws|throws
name|Exception
block|{
name|checkApiUser
argument_list|(
name|admin
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|requestScopeOperations
operator|.
name|setApiUser
argument_list|(
operator|new
name|Account
operator|.
name|Id
argument_list|(
name|sequences
operator|.
name|nextAccountId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assert_
argument_list|()
operator|.
name|fail
argument_list|(
literal|"expected RuntimeException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
comment|// Expected.
block|}
name|checkApiUser
argument_list|(
name|admin
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|checkApiUser (Account.Id expected)
specifier|private
name|void
name|checkApiUser
parameter_list|(
name|Account
operator|.
name|Id
name|expected
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Test all supported ways that an acceptance test might query the active user.
name|assertThat
argument_list|(
name|gApi
operator|.
name|accounts
argument_list|()
operator|.
name|self
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|_accountId
argument_list|)
operator|.
name|named
argument_list|(
literal|"user from GerritApi"
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|expected
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|userProvider
operator|.
name|get
argument_list|()
operator|.
name|isIdentifiedUser
argument_list|()
argument_list|)
operator|.
name|named
argument_list|(
literal|"user from provider is an IdentifiedUser"
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|userProvider
operator|.
name|get
argument_list|()
operator|.
name|getAccountId
argument_list|()
argument_list|)
operator|.
name|named
argument_list|(
literal|"user from provider"
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|expected
argument_list|)
expr_stmt|;
name|AcceptanceTestRequestScope
operator|.
name|Context
name|ctx
init|=
name|atrScope
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|ctx
operator|.
name|getUser
argument_list|()
operator|.
name|isIdentifiedUser
argument_list|()
argument_list|)
operator|.
name|named
argument_list|(
literal|"user from AcceptanceTestRequestScope.Context is an IdentifiedUser"
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|ctx
operator|.
name|getUser
argument_list|()
operator|.
name|getAccountId
argument_list|()
argument_list|)
operator|.
name|named
argument_list|(
literal|"user from AcceptanceTestRequestScope.Context"
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

