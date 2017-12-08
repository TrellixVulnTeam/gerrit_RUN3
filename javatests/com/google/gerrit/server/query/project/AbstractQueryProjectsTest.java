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
DECL|package|com.google.gerrit.server.query.project
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
name|common
operator|.
name|base
operator|.
name|CharMatcher
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
name|AnonymousUser
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
name|Accounts
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
name|AccountsUpdate
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
name|query
operator|.
name|account
operator|.
name|InternalAccountQuery
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
name|ManualRequestContext
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
name|OneOffRequestContext
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
name|testing
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
name|testing
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|Test
import|;
end_import

begin_class
annotation|@
name|Ignore
DECL|class|AbstractQueryProjectsTest
specifier|public
specifier|abstract
class|class
name|AbstractQueryProjectsTest
extends|extends
name|GerritServerTests
block|{
DECL|field|accounts
annotation|@
name|Inject
specifier|protected
name|Accounts
name|accounts
decl_stmt|;
DECL|field|accountsUpdate
annotation|@
name|Inject
specifier|protected
name|AccountsUpdate
operator|.
name|Server
name|accountsUpdate
decl_stmt|;
DECL|field|accountCache
annotation|@
name|Inject
specifier|protected
name|AccountCache
name|accountCache
decl_stmt|;
DECL|field|accountManager
annotation|@
name|Inject
specifier|protected
name|AccountManager
name|accountManager
decl_stmt|;
DECL|field|gApi
annotation|@
name|Inject
specifier|protected
name|GerritApi
name|gApi
decl_stmt|;
DECL|field|userFactory
annotation|@
name|Inject
specifier|protected
name|IdentifiedUser
operator|.
name|GenericFactory
name|userFactory
decl_stmt|;
DECL|field|anonymousUser
annotation|@
name|Inject
specifier|private
name|Provider
argument_list|<
name|AnonymousUser
argument_list|>
name|anonymousUser
decl_stmt|;
DECL|field|schemaFactory
annotation|@
name|Inject
specifier|protected
name|InMemoryDatabase
name|schemaFactory
decl_stmt|;
DECL|field|schemaCreator
annotation|@
name|Inject
specifier|protected
name|SchemaCreator
name|schemaCreator
decl_stmt|;
DECL|field|requestContext
annotation|@
name|Inject
specifier|protected
name|ThreadLocalRequestContext
name|requestContext
decl_stmt|;
DECL|field|oneOffRequestContext
annotation|@
name|Inject
specifier|protected
name|OneOffRequestContext
name|oneOffRequestContext
decl_stmt|;
DECL|field|internalAccountQuery
annotation|@
name|Inject
specifier|protected
name|InternalAccountQuery
name|internalAccountQuery
decl_stmt|;
DECL|field|allProjects
annotation|@
name|Inject
specifier|protected
name|AllProjectsName
name|allProjects
decl_stmt|;
DECL|field|lifecycle
specifier|protected
name|LifecycleManager
name|lifecycle
decl_stmt|;
DECL|field|injector
specifier|protected
name|Injector
name|injector
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
name|injector
operator|=
name|createInjector
argument_list|()
expr_stmt|;
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
name|setUpDatabase
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|cleanUp ()
specifier|public
name|void
name|cleanUp
parameter_list|()
block|{
name|lifecycle
operator|.
name|stop
argument_list|()
expr_stmt|;
name|db
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|setUpDatabase ()
specifier|protected
name|void
name|setUpDatabase
parameter_list|()
throws|throws
name|Exception
block|{
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
DECL|method|setAnonymous ()
specifier|protected
name|void
name|setAnonymous
parameter_list|()
block|{
name|requestContext
operator|.
name|setContext
argument_list|(
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
name|anonymousUser
operator|.
name|get
argument_list|()
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
argument_list|)
expr_stmt|;
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
DECL|method|byName ()
specifier|public
name|void
name|byName
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQuery
argument_list|(
literal|"name:project"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"name:non-existing"
argument_list|)
expr_stmt|;
name|ProjectInfo
name|project
init|=
name|createProject
argument_list|(
name|name
argument_list|(
literal|"project"
argument_list|)
argument_list|)
decl_stmt|;
name|assertQuery
argument_list|(
literal|"name:"
operator|+
name|project
operator|.
name|name
argument_list|,
name|project
argument_list|)
expr_stmt|;
comment|// only exact match
name|ProjectInfo
name|projectWithHyphen
init|=
name|createProject
argument_list|(
name|name
argument_list|(
literal|"project-with-hyphen"
argument_list|)
argument_list|)
decl_stmt|;
name|createProject
argument_list|(
name|name
argument_list|(
literal|"project-no-match-with-hyphen"
argument_list|)
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"name:"
operator|+
name|projectWithHyphen
operator|.
name|name
argument_list|,
name|projectWithHyphen
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|byInname ()
specifier|public
name|void
name|byInname
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|namePart
init|=
name|getSanitizedMethodName
argument_list|()
decl_stmt|;
name|namePart
operator|=
name|CharMatcher
operator|.
name|is
argument_list|(
literal|'_'
argument_list|)
operator|.
name|removeFrom
argument_list|(
name|namePart
argument_list|)
expr_stmt|;
name|ProjectInfo
name|project1
init|=
name|createProject
argument_list|(
name|name
argument_list|(
literal|"project-"
operator|+
name|namePart
argument_list|)
argument_list|)
decl_stmt|;
name|ProjectInfo
name|project2
init|=
name|createProject
argument_list|(
name|name
argument_list|(
literal|"project-"
operator|+
name|namePart
operator|+
literal|"-2"
argument_list|)
argument_list|)
decl_stmt|;
name|ProjectInfo
name|project3
init|=
name|createProject
argument_list|(
name|name
argument_list|(
literal|"project-"
operator|+
name|namePart
operator|+
literal|"3"
argument_list|)
argument_list|)
decl_stmt|;
name|assertQuery
argument_list|(
literal|"inname:"
operator|+
name|namePart
argument_list|,
name|project1
argument_list|,
name|project2
argument_list|,
name|project3
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"inname:"
operator|+
name|namePart
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
argument_list|,
name|project1
argument_list|,
name|project2
argument_list|,
name|project3
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"inname:"
operator|+
name|namePart
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
argument_list|,
name|project1
argument_list|,
name|project2
argument_list|,
name|project3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|byDescription ()
specifier|public
name|void
name|byDescription
parameter_list|()
throws|throws
name|Exception
block|{
name|ProjectInfo
name|project1
init|=
name|createProjectWithDescription
argument_list|(
name|name
argument_list|(
literal|"project1"
argument_list|)
argument_list|,
literal|"This is a test project."
argument_list|)
decl_stmt|;
name|ProjectInfo
name|project2
init|=
name|createProjectWithDescription
argument_list|(
name|name
argument_list|(
literal|"project2"
argument_list|)
argument_list|,
literal|"ANOTHER TEST PROJECT."
argument_list|)
decl_stmt|;
name|createProjectWithDescription
argument_list|(
name|name
argument_list|(
literal|"project3"
argument_list|)
argument_list|,
literal|"Maintainers of project foo."
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"description:test"
argument_list|,
name|project1
argument_list|,
name|project2
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"description:non-existing"
argument_list|)
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
literal|"description operator requires a value"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"description:\"\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|byDefaultField ()
specifier|public
name|void
name|byDefaultField
parameter_list|()
throws|throws
name|Exception
block|{
name|ProjectInfo
name|project1
init|=
name|createProject
argument_list|(
name|name
argument_list|(
literal|"foo-project"
argument_list|)
argument_list|)
decl_stmt|;
name|ProjectInfo
name|project2
init|=
name|createProject
argument_list|(
name|name
argument_list|(
literal|"project2"
argument_list|)
argument_list|)
decl_stmt|;
name|ProjectInfo
name|project3
init|=
name|createProjectWithDescription
argument_list|(
name|name
argument_list|(
literal|"project3"
argument_list|)
argument_list|,
literal|"decription that contains foo and the UUID of project2: "
operator|+
name|project2
operator|.
name|id
argument_list|)
decl_stmt|;
name|assertQuery
argument_list|(
literal|"non-existing"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"foo"
argument_list|,
name|project1
argument_list|,
name|project3
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|project2
operator|.
name|id
argument_list|,
name|project2
argument_list|,
name|project3
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
name|ProjectInfo
name|project1
init|=
name|createProject
argument_list|(
name|name
argument_list|(
literal|"project1"
argument_list|)
argument_list|)
decl_stmt|;
name|ProjectInfo
name|project2
init|=
name|createProject
argument_list|(
name|name
argument_list|(
literal|"project2"
argument_list|)
argument_list|)
decl_stmt|;
name|ProjectInfo
name|project3
init|=
name|createProject
argument_list|(
name|name
argument_list|(
literal|"project3"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|query
init|=
literal|"name:"
operator|+
name|project1
operator|.
name|name
operator|+
literal|" OR name:"
operator|+
name|project2
operator|.
name|name
operator|+
literal|" OR name:"
operator|+
name|project3
operator|.
name|name
decl_stmt|;
name|List
argument_list|<
name|ProjectInfo
argument_list|>
name|result
init|=
name|assertQuery
argument_list|(
name|query
argument_list|,
name|project1
argument_list|,
name|project2
argument_list|,
name|project3
argument_list|)
decl_stmt|;
name|result
operator|=
name|assertQuery
argument_list|(
name|newQuery
argument_list|(
name|query
argument_list|)
operator|.
name|withLimit
argument_list|(
literal|2
argument_list|)
argument_list|,
name|result
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|)
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
name|ProjectInfo
name|project1
init|=
name|createProject
argument_list|(
name|name
argument_list|(
literal|"project1"
argument_list|)
argument_list|)
decl_stmt|;
name|ProjectInfo
name|project2
init|=
name|createProject
argument_list|(
name|name
argument_list|(
literal|"project2"
argument_list|)
argument_list|)
decl_stmt|;
name|ProjectInfo
name|project3
init|=
name|createProject
argument_list|(
name|name
argument_list|(
literal|"project3"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|query
init|=
literal|"name:"
operator|+
name|project1
operator|.
name|name
operator|+
literal|" OR name:"
operator|+
name|project2
operator|.
name|name
operator|+
literal|" OR name:"
operator|+
name|project3
operator|.
name|name
decl_stmt|;
name|List
argument_list|<
name|ProjectInfo
argument_list|>
name|result
init|=
name|assertQuery
argument_list|(
name|query
argument_list|,
name|project1
argument_list|,
name|project2
argument_list|,
name|project3
argument_list|)
decl_stmt|;
name|assertQuery
argument_list|(
name|newQuery
argument_list|(
name|query
argument_list|)
operator|.
name|withStart
argument_list|(
literal|1
argument_list|)
argument_list|,
name|result
operator|.
name|subList
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|asAnonymous ()
specifier|public
name|void
name|asAnonymous
parameter_list|()
throws|throws
name|Exception
block|{
name|ProjectInfo
name|project
init|=
name|createProject
argument_list|(
name|name
argument_list|(
literal|"project"
argument_list|)
argument_list|)
decl_stmt|;
name|setAnonymous
argument_list|()
expr_stmt|;
name|assertQuery
argument_list|(
literal|"name:"
operator|+
name|project
operator|.
name|name
argument_list|)
expr_stmt|;
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
try|try
init|(
name|ManualRequestContext
name|ctx
init|=
name|oneOffRequestContext
operator|.
name|open
argument_list|()
init|)
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
name|accountsUpdate
operator|.
name|create
argument_list|()
operator|.
name|update
argument_list|(
name|id
argument_list|,
name|u
lambda|->
block|{
name|u
operator|.
name|setFullName
argument_list|(
name|fullName
argument_list|)
operator|.
name|setPreferredEmail
argument_list|(
name|email
argument_list|)
operator|.
name|setActive
argument_list|(
name|active
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
return|return
name|id
return|;
block|}
block|}
DECL|method|createProject (String name)
specifier|protected
name|ProjectInfo
name|createProject
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|Exception
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
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|create
argument_list|(
name|in
argument_list|)
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|createProjectWithDescription (String name, String description)
specifier|protected
name|ProjectInfo
name|createProjectWithDescription
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|description
parameter_list|)
throws|throws
name|Exception
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
name|in
operator|.
name|description
operator|=
name|description
expr_stmt|;
return|return
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|create
argument_list|(
name|in
argument_list|)
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|getProject (Project.NameKey nameKey)
specifier|protected
name|ProjectInfo
name|getProject
parameter_list|(
name|Project
operator|.
name|NameKey
name|nameKey
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|name
argument_list|(
name|nameKey
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|assertQuery (Object query, ProjectInfo... projects)
specifier|protected
name|List
argument_list|<
name|ProjectInfo
argument_list|>
name|assertQuery
parameter_list|(
name|Object
name|query
parameter_list|,
name|ProjectInfo
modifier|...
name|projects
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
name|projects
argument_list|)
return|;
block|}
DECL|method|assertQuery (QueryRequest query, ProjectInfo... projects)
specifier|protected
name|List
argument_list|<
name|ProjectInfo
argument_list|>
name|assertQuery
parameter_list|(
name|QueryRequest
name|query
parameter_list|,
name|ProjectInfo
modifier|...
name|projects
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|assertQuery
argument_list|(
name|query
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|projects
argument_list|)
argument_list|)
return|;
block|}
DECL|method|assertQuery (QueryRequest query, List<ProjectInfo> projects)
specifier|protected
name|List
argument_list|<
name|ProjectInfo
argument_list|>
name|assertQuery
parameter_list|(
name|QueryRequest
name|query
parameter_list|,
name|List
argument_list|<
name|ProjectInfo
argument_list|>
name|projects
parameter_list|)
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|ProjectInfo
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
name|String
argument_list|>
name|names
init|=
name|names
argument_list|(
name|result
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|names
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
name|projects
argument_list|)
argument_list|)
operator|.
name|containsExactlyElementsIn
argument_list|(
name|names
argument_list|(
name|projects
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|result
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
block|{
return|return
name|gApi
operator|.
name|projects
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
DECL|method|format ( QueryRequest query, List<ProjectInfo> actualProjects, List<ProjectInfo> expectedProjects)
specifier|protected
name|String
name|format
parameter_list|(
name|QueryRequest
name|query
parameter_list|,
name|List
argument_list|<
name|ProjectInfo
argument_list|>
name|actualProjects
parameter_list|,
name|List
argument_list|<
name|ProjectInfo
argument_list|>
name|expectedProjects
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
literal|"' with expected projects "
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|format
argument_list|(
name|expectedProjects
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
name|actualProjects
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
DECL|method|format (Iterable<ProjectInfo> projects)
specifier|protected
name|String
name|format
parameter_list|(
name|Iterable
argument_list|<
name|ProjectInfo
argument_list|>
name|projects
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
name|ProjectInfo
argument_list|>
name|it
init|=
name|projects
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
name|ProjectInfo
name|p
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
name|p
operator|.
name|id
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
name|p
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
literal|"parent="
argument_list|)
operator|.
name|append
argument_list|(
name|p
operator|.
name|parent
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
literal|"description="
argument_list|)
operator|.
name|append
argument_list|(
name|p
operator|.
name|description
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
DECL|method|names (ProjectInfo... projects)
specifier|protected
specifier|static
name|Iterable
argument_list|<
name|String
argument_list|>
name|names
parameter_list|(
name|ProjectInfo
modifier|...
name|projects
parameter_list|)
block|{
return|return
name|names
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|projects
argument_list|)
argument_list|)
return|;
block|}
DECL|method|names (List<ProjectInfo> projects)
specifier|protected
specifier|static
name|Iterable
argument_list|<
name|String
argument_list|>
name|names
parameter_list|(
name|List
argument_list|<
name|ProjectInfo
argument_list|>
name|projects
parameter_list|)
block|{
return|return
name|projects
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|p
lambda|->
name|p
operator|.
name|name
argument_list|)
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
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
return|return
name|name
operator|+
literal|"_"
operator|+
name|getSanitizedMethodName
argument_list|()
return|;
block|}
block|}
end_class

end_unit

