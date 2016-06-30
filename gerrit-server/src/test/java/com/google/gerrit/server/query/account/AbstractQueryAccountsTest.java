begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2016 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.query.account
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|query
operator|.
name|account
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
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
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
name|base
operator|.
name|Function
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
name|FluentIterable
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
name|extensions
operator|.
name|api
operator|.
name|GerritApi
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
name|accounts
operator|.
name|Accounts
operator|.
name|QueryRequest
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
name|AccountInfo
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
name|lifecycle
operator|.
name|LifecycleManager
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
name|reviewdb
operator|.
name|server
operator|.
name|ReviewDb
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
name|account
operator|.
name|AccountCache
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
name|AccountManager
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
name|AuthRequest
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
name|query
operator|.
name|change
operator|.
name|InternalChangeQuery
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
name|schema
operator|.
name|SchemaCreator
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
name|util
operator|.
name|RequestContext
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
name|util
operator|.
name|ThreadLocalRequestContext
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
name|testutil
operator|.
name|ConfigSuite
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
name|testutil
operator|.
name|GerritServerTests
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
name|testutil
operator|.
name|InMemoryDatabase
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
name|Injector
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
name|util
operator|.
name|Providers
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
name|Config
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|Ignore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TestName
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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

begin_class
annotation|@
name|Ignore
DECL|class|AbstractQueryAccountsTest
specifier|public
specifier|abstract
class|class
name|AbstractQueryAccountsTest
extends|extends
name|GerritServerTests
block|{
annotation|@
name|ConfigSuite
operator|.
name|Default
DECL|method|defaultConfig ()
specifier|public
specifier|static
name|Config
name|defaultConfig
parameter_list|()
block|{
name|Config
name|cfg
init|=
operator|new
name|Config
argument_list|()
decl_stmt|;
name|cfg
operator|.
name|setInt
argument_list|(
literal|"index"
argument_list|,
literal|null
argument_list|,
literal|"maxPages"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
return|return
name|cfg
return|;
block|}
annotation|@
name|Rule
DECL|field|testName
specifier|public
specifier|final
name|TestName
name|testName
init|=
operator|new
name|TestName
argument_list|()
decl_stmt|;
annotation|@
name|Inject
DECL|field|accountCache
specifier|protected
name|AccountCache
name|accountCache
decl_stmt|;
annotation|@
name|Inject
DECL|field|accountManager
specifier|protected
name|AccountManager
name|accountManager
decl_stmt|;
annotation|@
name|Inject
DECL|field|gApi
specifier|protected
name|GerritApi
name|gApi
decl_stmt|;
annotation|@
name|Inject
DECL|field|userFactory
specifier|protected
name|IdentifiedUser
operator|.
name|GenericFactory
name|userFactory
decl_stmt|;
annotation|@
name|Inject
DECL|field|schemaFactory
specifier|protected
name|InMemoryDatabase
name|schemaFactory
decl_stmt|;
annotation|@
name|Inject
DECL|field|internalChangeQuery
specifier|protected
name|InternalChangeQuery
name|internalChangeQuery
decl_stmt|;
annotation|@
name|Inject
DECL|field|schemaCreator
specifier|protected
name|SchemaCreator
name|schemaCreator
decl_stmt|;
annotation|@
name|Inject
DECL|field|requestContext
specifier|protected
name|ThreadLocalRequestContext
name|requestContext
decl_stmt|;
DECL|field|lifecycle
specifier|protected
name|LifecycleManager
name|lifecycle
decl_stmt|;
DECL|field|db
specifier|protected
name|ReviewDb
name|db
decl_stmt|;
DECL|field|currentUserInfo
specifier|protected
name|AccountInfo
name|currentUserInfo
decl_stmt|;
DECL|field|user
specifier|protected
name|CurrentUser
name|user
decl_stmt|;
DECL|method|createInjector ()
specifier|protected
specifier|abstract
name|Injector
name|createInjector
parameter_list|()
function_decl|;
annotation|@
name|Before
DECL|method|setUpInjector ()
specifier|public
name|void
name|setUpInjector
parameter_list|()
throws|throws
name|Exception
block|{
name|lifecycle
operator|=
operator|new
name|LifecycleManager
argument_list|()
expr_stmt|;
name|Injector
name|injector
init|=
name|createInjector
argument_list|()
decl_stmt|;
name|lifecycle
operator|.
name|add
argument_list|(
name|injector
argument_list|)
expr_stmt|;
name|injector
operator|.
name|injectMembers
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|lifecycle
operator|.
name|start
argument_list|()
expr_stmt|;
name|db
operator|=
name|schemaFactory
operator|.
name|open
argument_list|()
expr_stmt|;
name|schemaCreator
operator|.
name|create
argument_list|(
name|db
argument_list|)
expr_stmt|;
name|Account
operator|.
name|Id
name|userId
init|=
name|createAccount
argument_list|(
literal|"user"
argument_list|,
literal|"User"
argument_list|,
literal|"user@example.com"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|user
operator|=
name|userFactory
operator|.
name|create
argument_list|(
name|userId
argument_list|)
expr_stmt|;
name|requestContext
operator|.
name|setContext
argument_list|(
name|newRequestContext
argument_list|(
name|userId
argument_list|)
argument_list|)
expr_stmt|;
name|currentUserInfo
operator|=
name|gApi
operator|.
name|accounts
argument_list|()
operator|.
name|id
argument_list|(
name|userId
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
DECL|method|newRequestContext (Account.Id requestUserId)
specifier|protected
name|RequestContext
name|newRequestContext
parameter_list|(
name|Account
operator|.
name|Id
name|requestUserId
parameter_list|)
block|{
specifier|final
name|CurrentUser
name|requestUser
init|=
name|userFactory
operator|.
name|create
argument_list|(
name|requestUserId
argument_list|)
decl_stmt|;
return|return
operator|new
name|RequestContext
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|CurrentUser
name|getUser
parameter_list|()
block|{
return|return
name|requestUser
return|;
block|}
annotation|@
name|Override
specifier|public
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|getReviewDbProvider
parameter_list|()
block|{
return|return
name|Providers
operator|.
name|of
argument_list|(
name|db
argument_list|)
return|;
block|}
block|}
return|;
block|}
annotation|@
name|After
DECL|method|tearDownInjector ()
specifier|public
name|void
name|tearDownInjector
parameter_list|()
block|{
if|if
condition|(
name|lifecycle
operator|!=
literal|null
condition|)
block|{
name|lifecycle
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|requestContext
operator|.
name|setContext
argument_list|(
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|db
operator|!=
literal|null
condition|)
block|{
name|db
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|InMemoryDatabase
operator|.
name|drop
argument_list|(
name|schemaFactory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|byId ()
specifier|public
name|void
name|byId
parameter_list|()
throws|throws
name|Exception
block|{
name|AccountInfo
name|user
init|=
name|newAccount
argument_list|(
literal|"user"
argument_list|)
decl_stmt|;
name|assertQuery
argument_list|(
literal|"9999999"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|currentUserInfo
operator|.
name|_accountId
argument_list|,
name|currentUserInfo
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|user
operator|.
name|_accountId
argument_list|,
name|user
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|bySelf ()
specifier|public
name|void
name|bySelf
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQuery
argument_list|(
literal|"self"
argument_list|,
name|currentUserInfo
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|byEmail ()
specifier|public
name|void
name|byEmail
parameter_list|()
throws|throws
name|Exception
block|{
name|AccountInfo
name|user1
init|=
name|newAccountWithEmail
argument_list|(
literal|"user1"
argument_list|,
name|name
argument_list|(
literal|"user1@example.com"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|domain
init|=
name|name
argument_list|(
literal|"test.com"
argument_list|)
decl_stmt|;
name|AccountInfo
name|user2
init|=
name|newAccountWithEmail
argument_list|(
literal|"user2"
argument_list|,
literal|"user2@"
operator|+
name|domain
argument_list|)
decl_stmt|;
name|AccountInfo
name|user3
init|=
name|newAccountWithEmail
argument_list|(
literal|"user3"
argument_list|,
literal|"user3@"
operator|+
name|domain
argument_list|)
decl_stmt|;
name|String
name|prefix
init|=
name|name
argument_list|(
literal|"prefix"
argument_list|)
decl_stmt|;
name|AccountInfo
name|user4
init|=
name|newAccountWithEmail
argument_list|(
literal|"user4"
argument_list|,
name|prefix
operator|+
literal|"user4@example.com"
argument_list|)
decl_stmt|;
name|AccountInfo
name|user5
init|=
name|newAccountWithEmail
argument_list|(
literal|"user5"
argument_list|,
name|name
argument_list|(
literal|"user5MixedCase@example.com"
argument_list|)
argument_list|)
decl_stmt|;
name|assertQuery
argument_list|(
literal|"notexisting@test.com"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|currentUserInfo
operator|.
name|email
argument_list|,
name|currentUserInfo
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"email:"
operator|+
name|currentUserInfo
operator|.
name|email
argument_list|,
name|currentUserInfo
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|user1
operator|.
name|email
argument_list|,
name|user1
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"email:"
operator|+
name|user1
operator|.
name|email
argument_list|,
name|user1
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|domain
argument_list|,
name|user2
argument_list|,
name|user3
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"email:"
operator|+
name|prefix
argument_list|,
name|user4
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|user5
operator|.
name|email
argument_list|,
name|user5
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"email:"
operator|+
name|user5
operator|.
name|email
argument_list|,
name|user5
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"email:"
operator|+
name|user5
operator|.
name|email
operator|.
name|toUpperCase
argument_list|()
argument_list|,
name|user5
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|byUsername ()
specifier|public
name|void
name|byUsername
parameter_list|()
throws|throws
name|Exception
block|{
name|AccountInfo
name|user1
init|=
name|newAccount
argument_list|(
literal|"myuser"
argument_list|)
decl_stmt|;
name|assertQuery
argument_list|(
literal|"notexisting"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"Not Existing"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|user1
operator|.
name|username
argument_list|,
name|user1
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"username:"
operator|+
name|user1
operator|.
name|username
argument_list|,
name|user1
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"username:"
operator|+
name|user1
operator|.
name|username
operator|.
name|toUpperCase
argument_list|()
argument_list|,
name|user1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|isActive ()
specifier|public
name|void
name|isActive
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|domain
init|=
name|name
argument_list|(
literal|"test.com"
argument_list|)
decl_stmt|;
name|AccountInfo
name|user1
init|=
name|newAccountWithEmail
argument_list|(
literal|"user1"
argument_list|,
literal|"user1@"
operator|+
name|domain
argument_list|)
decl_stmt|;
name|AccountInfo
name|user2
init|=
name|newAccountWithEmail
argument_list|(
literal|"user2"
argument_list|,
literal|"user2@"
operator|+
name|domain
argument_list|)
decl_stmt|;
name|AccountInfo
name|user3
init|=
name|newAccount
argument_list|(
literal|"user3"
argument_list|,
literal|"user3@"
operator|+
name|domain
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|AccountInfo
name|user4
init|=
name|newAccount
argument_list|(
literal|"user4"
argument_list|,
literal|"user4@"
operator|+
name|domain
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// by default only active accounts are returned
name|assertQuery
argument_list|(
name|domain
argument_list|,
name|user1
argument_list|,
name|user2
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"name:"
operator|+
name|domain
argument_list|,
name|user1
argument_list|,
name|user2
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"is:active name:"
operator|+
name|domain
argument_list|,
name|user1
argument_list|,
name|user2
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"is:inactive name:"
operator|+
name|domain
argument_list|,
name|user3
argument_list|,
name|user4
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|byName ()
specifier|public
name|void
name|byName
parameter_list|()
throws|throws
name|Exception
block|{
name|AccountInfo
name|user1
init|=
name|newAccountWithFullName
argument_list|(
literal|"jdoe"
argument_list|,
literal|"John Doe"
argument_list|)
decl_stmt|;
name|AccountInfo
name|user2
init|=
name|newAccountWithFullName
argument_list|(
literal|"jroe"
argument_list|,
literal|"Jane Roe"
argument_list|)
decl_stmt|;
name|assertQuery
argument_list|(
literal|"notexisting"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"Not Existing"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|quote
argument_list|(
name|user1
operator|.
name|name
argument_list|)
argument_list|,
name|user1
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"name:"
operator|+
name|quote
argument_list|(
name|user1
operator|.
name|name
argument_list|)
argument_list|,
name|user1
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"John"
argument_list|,
name|user1
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"john"
argument_list|,
name|user1
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"Doe"
argument_list|,
name|user1
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"doe"
argument_list|,
name|user1
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"DOE"
argument_list|,
name|user1
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"name:John"
argument_list|,
name|user1
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"name:john"
argument_list|,
name|user1
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"name:Doe"
argument_list|,
name|user1
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"name:doe"
argument_list|,
name|user1
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"name:DOE"
argument_list|,
name|user1
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|quote
argument_list|(
name|user2
operator|.
name|name
argument_list|)
argument_list|,
name|user2
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"name:"
operator|+
name|quote
argument_list|(
name|user2
operator|.
name|name
argument_list|)
argument_list|,
name|user2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|withLimit ()
specifier|public
name|void
name|withLimit
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|domain
init|=
name|name
argument_list|(
literal|"test.com"
argument_list|)
decl_stmt|;
name|AccountInfo
name|user1
init|=
name|newAccountWithEmail
argument_list|(
literal|"user1"
argument_list|,
literal|"user1@"
operator|+
name|domain
argument_list|)
decl_stmt|;
name|AccountInfo
name|user2
init|=
name|newAccountWithEmail
argument_list|(
literal|"user2"
argument_list|,
literal|"user2@"
operator|+
name|domain
argument_list|)
decl_stmt|;
name|AccountInfo
name|user3
init|=
name|newAccountWithEmail
argument_list|(
literal|"user3"
argument_list|,
literal|"user3@"
operator|+
name|domain
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|AccountInfo
argument_list|>
name|result
init|=
name|assertQuery
argument_list|(
name|domain
argument_list|,
name|user1
argument_list|,
name|user2
argument_list|,
name|user3
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|get
argument_list|(
name|result
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|_moreAccounts
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|result
operator|=
name|assertQuery
argument_list|(
name|newQuery
argument_list|(
name|domain
argument_list|)
operator|.
name|withLimit
argument_list|(
literal|2
argument_list|)
argument_list|,
name|user1
argument_list|,
name|user2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|get
argument_list|(
name|result
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|_moreAccounts
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|withStart ()
specifier|public
name|void
name|withStart
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|domain
init|=
name|name
argument_list|(
literal|"test.com"
argument_list|)
decl_stmt|;
name|AccountInfo
name|user1
init|=
name|newAccountWithEmail
argument_list|(
literal|"user1"
argument_list|,
literal|"user1@"
operator|+
name|domain
argument_list|)
decl_stmt|;
name|AccountInfo
name|user2
init|=
name|newAccountWithEmail
argument_list|(
literal|"user2"
argument_list|,
literal|"user2@"
operator|+
name|domain
argument_list|)
decl_stmt|;
name|AccountInfo
name|user3
init|=
name|newAccountWithEmail
argument_list|(
literal|"user3"
argument_list|,
literal|"user3@"
operator|+
name|domain
argument_list|)
decl_stmt|;
name|assertQuery
argument_list|(
name|domain
argument_list|,
name|user1
argument_list|,
name|user2
argument_list|,
name|user3
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|newQuery
argument_list|(
name|domain
argument_list|)
operator|.
name|withStart
argument_list|(
literal|1
argument_list|)
argument_list|,
name|user2
argument_list|,
name|user3
argument_list|)
expr_stmt|;
block|}
DECL|method|newAccount (String username)
specifier|protected
name|AccountInfo
name|newAccount
parameter_list|(
name|String
name|username
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|newAccountWithEmail
argument_list|(
name|username
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|newAccountWithEmail (String username, String email)
specifier|protected
name|AccountInfo
name|newAccountWithEmail
parameter_list|(
name|String
name|username
parameter_list|,
name|String
name|email
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|newAccount
argument_list|(
name|username
argument_list|,
name|email
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|newAccountWithFullName (String username, String fullName)
specifier|protected
name|AccountInfo
name|newAccountWithFullName
parameter_list|(
name|String
name|username
parameter_list|,
name|String
name|fullName
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|newAccount
argument_list|(
name|username
argument_list|,
name|fullName
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|newAccount (String username, String email, boolean active)
specifier|protected
name|AccountInfo
name|newAccount
parameter_list|(
name|String
name|username
parameter_list|,
name|String
name|email
parameter_list|,
name|boolean
name|active
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|newAccount
argument_list|(
name|username
argument_list|,
literal|null
argument_list|,
name|email
argument_list|,
name|active
argument_list|)
return|;
block|}
DECL|method|newAccount (String username, String fullName, String email, boolean active)
specifier|protected
name|AccountInfo
name|newAccount
parameter_list|(
name|String
name|username
parameter_list|,
name|String
name|fullName
parameter_list|,
name|String
name|email
parameter_list|,
name|boolean
name|active
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|uniqueName
init|=
name|name
argument_list|(
name|username
argument_list|)
decl_stmt|;
try|try
block|{
name|gApi
operator|.
name|accounts
argument_list|()
operator|.
name|id
argument_list|(
name|uniqueName
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"user "
operator|+
name|uniqueName
operator|+
literal|" already exists"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ResourceNotFoundException
name|e
parameter_list|)
block|{
comment|// expected: user does not exist yet
block|}
name|Account
operator|.
name|Id
name|id
init|=
name|createAccount
argument_list|(
name|uniqueName
argument_list|,
name|fullName
argument_list|,
name|email
argument_list|,
name|active
argument_list|)
decl_stmt|;
return|return
name|gApi
operator|.
name|accounts
argument_list|()
operator|.
name|id
argument_list|(
name|id
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|quote (String s)
specifier|protected
name|String
name|quote
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
literal|"\""
operator|+
name|s
operator|+
literal|"\""
return|;
block|}
DECL|method|name (String name)
specifier|protected
name|String
name|name
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
name|suffix
init|=
name|testName
operator|.
name|getMethodName
argument_list|()
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|contains
argument_list|(
literal|"@"
argument_list|)
condition|)
block|{
return|return
name|name
operator|+
literal|"."
operator|+
name|suffix
return|;
block|}
return|return
name|name
operator|+
literal|"_"
operator|+
name|suffix
return|;
block|}
DECL|method|createAccount (String username, String fullName, String email, boolean active)
specifier|private
name|Account
operator|.
name|Id
name|createAccount
parameter_list|(
name|String
name|username
parameter_list|,
name|String
name|fullName
parameter_list|,
name|String
name|email
parameter_list|,
name|boolean
name|active
parameter_list|)
throws|throws
name|Exception
block|{
name|Account
operator|.
name|Id
name|id
init|=
name|accountManager
operator|.
name|authenticate
argument_list|(
name|AuthRequest
operator|.
name|forUser
argument_list|(
name|username
argument_list|)
argument_list|)
operator|.
name|getAccountId
argument_list|()
decl_stmt|;
if|if
condition|(
name|email
operator|!=
literal|null
condition|)
block|{
name|accountManager
operator|.
name|link
argument_list|(
name|id
argument_list|,
name|AuthRequest
operator|.
name|forEmail
argument_list|(
name|email
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Account
name|a
init|=
name|db
operator|.
name|accounts
argument_list|()
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|a
operator|.
name|setFullName
argument_list|(
name|fullName
argument_list|)
expr_stmt|;
name|a
operator|.
name|setPreferredEmail
argument_list|(
name|email
argument_list|)
expr_stmt|;
name|a
operator|.
name|setActive
argument_list|(
name|active
argument_list|)
expr_stmt|;
name|db
operator|.
name|accounts
argument_list|()
operator|.
name|update
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|a
argument_list|)
argument_list|)
expr_stmt|;
name|accountCache
operator|.
name|evict
argument_list|(
name|id
argument_list|)
expr_stmt|;
return|return
name|id
return|;
block|}
DECL|method|newQuery (Object query)
specifier|protected
name|QueryRequest
name|newQuery
parameter_list|(
name|Object
name|query
parameter_list|)
throws|throws
name|RestApiException
block|{
return|return
name|gApi
operator|.
name|accounts
argument_list|()
operator|.
name|query
argument_list|(
name|query
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
DECL|method|assertQuery (Object query, AccountInfo... accounts)
specifier|protected
name|List
argument_list|<
name|AccountInfo
argument_list|>
name|assertQuery
parameter_list|(
name|Object
name|query
parameter_list|,
name|AccountInfo
modifier|...
name|accounts
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|assertQuery
argument_list|(
name|newQuery
argument_list|(
name|query
argument_list|)
argument_list|,
name|accounts
argument_list|)
return|;
block|}
DECL|method|assertQuery (QueryRequest query, AccountInfo... accounts)
specifier|protected
name|List
argument_list|<
name|AccountInfo
argument_list|>
name|assertQuery
parameter_list|(
name|QueryRequest
name|query
parameter_list|,
name|AccountInfo
modifier|...
name|accounts
parameter_list|)
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|AccountInfo
argument_list|>
name|result
init|=
name|query
operator|.
name|get
argument_list|()
decl_stmt|;
name|Iterable
argument_list|<
name|Integer
argument_list|>
name|ids
init|=
name|ids
argument_list|(
name|result
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|ids
argument_list|)
operator|.
name|named
argument_list|(
name|format
argument_list|(
name|query
argument_list|,
name|result
argument_list|,
name|accounts
argument_list|)
argument_list|)
operator|.
name|containsExactlyElementsIn
argument_list|(
name|ids
argument_list|(
name|accounts
argument_list|)
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|format (QueryRequest query, Iterable<AccountInfo> actualIds, AccountInfo... expectedAccounts)
specifier|private
name|String
name|format
parameter_list|(
name|QueryRequest
name|query
parameter_list|,
name|Iterable
argument_list|<
name|AccountInfo
argument_list|>
name|actualIds
parameter_list|,
name|AccountInfo
modifier|...
name|expectedAccounts
parameter_list|)
block|{
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"query '"
argument_list|)
operator|.
name|append
argument_list|(
name|query
operator|.
name|getQuery
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"' with expected accounts "
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|format
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|expectedAccounts
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|" and result "
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|format
argument_list|(
name|actualIds
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|format (Iterable<AccountInfo> accounts)
specifier|private
name|String
name|format
parameter_list|(
name|Iterable
argument_list|<
name|AccountInfo
argument_list|>
name|accounts
parameter_list|)
block|{
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"["
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|AccountInfo
argument_list|>
name|it
init|=
name|accounts
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|AccountInfo
name|a
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"{"
argument_list|)
operator|.
name|append
argument_list|(
name|a
operator|.
name|_accountId
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
literal|"name="
argument_list|)
operator|.
name|append
argument_list|(
name|a
operator|.
name|name
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
literal|"email="
argument_list|)
operator|.
name|append
argument_list|(
name|a
operator|.
name|email
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
literal|"username="
argument_list|)
operator|.
name|append
argument_list|(
name|a
operator|.
name|username
argument_list|)
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
if|if
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
block|}
name|b
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|ids (AccountInfo... accounts)
specifier|protected
specifier|static
name|Iterable
argument_list|<
name|Integer
argument_list|>
name|ids
parameter_list|(
name|AccountInfo
modifier|...
name|accounts
parameter_list|)
block|{
return|return
name|FluentIterable
operator|.
name|from
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|accounts
argument_list|)
argument_list|)
operator|.
name|transform
argument_list|(
operator|new
name|Function
argument_list|<
name|AccountInfo
argument_list|,
name|Integer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Integer
name|apply
parameter_list|(
name|AccountInfo
name|in
parameter_list|)
block|{
return|return
name|in
operator|.
name|_accountId
return|;
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|ids (Iterable<AccountInfo> accounts)
specifier|protected
specifier|static
name|Iterable
argument_list|<
name|Integer
argument_list|>
name|ids
parameter_list|(
name|Iterable
argument_list|<
name|AccountInfo
argument_list|>
name|accounts
parameter_list|)
block|{
return|return
name|FluentIterable
operator|.
name|from
argument_list|(
name|accounts
argument_list|)
operator|.
name|transform
argument_list|(
operator|new
name|Function
argument_list|<
name|AccountInfo
argument_list|,
name|Integer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Integer
name|apply
parameter_list|(
name|AccountInfo
name|in
parameter_list|)
block|{
return|return
name|in
operator|.
name|_accountId
return|;
block|}
block|}
argument_list|)
return|;
block|}
block|}
end_class

end_unit

