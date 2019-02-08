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
DECL|package|com.google.gerrit.acceptance.server.account
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|server
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
name|common
operator|.
name|truth
operator|.
name|Truth8
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
name|base
operator|.
name|Splitter
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
name|acceptance
operator|.
name|testsuite
operator|.
name|request
operator|.
name|RequestScopeOperations
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
name|AccountVisibility
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
name|ServerInitiated
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
name|AccountResolver
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
name|AccountResolver
operator|.
name|Result
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
name|AccountResolver
operator|.
name|UnresolvableAccountException
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
name|AccountState
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
name|externalids
operator|.
name|ExternalId
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
name|notedb
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
name|gerrit
operator|.
name|testing
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
name|util
operator|.
name|Optional
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
name|Test
import|;
end_import

begin_class
DECL|class|AccountResolverIT
specifier|public
class|class
name|AccountResolverIT
extends|extends
name|AbstractDaemonTest
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
name|setEnum
argument_list|(
literal|"accounts"
argument_list|,
literal|null
argument_list|,
literal|"visibility"
argument_list|,
name|AccountVisibility
operator|.
name|SAME_GROUP
argument_list|)
expr_stmt|;
return|return
name|cfg
return|;
block|}
DECL|field|accountsUpdateProvider
annotation|@
name|Inject
annotation|@
name|ServerInitiated
specifier|private
name|Provider
argument_list|<
name|AccountsUpdate
argument_list|>
name|accountsUpdateProvider
decl_stmt|;
DECL|field|accountOperations
annotation|@
name|Inject
specifier|private
name|AccountOperations
name|accountOperations
decl_stmt|;
DECL|field|accountResolver
annotation|@
name|Inject
specifier|private
name|AccountResolver
name|accountResolver
decl_stmt|;
DECL|field|self
annotation|@
name|Inject
specifier|private
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|self
decl_stmt|;
DECL|field|requestScopeOperations
annotation|@
name|Inject
specifier|private
name|RequestScopeOperations
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
DECL|method|bySelf ()
specifier|public
name|void
name|bySelf
parameter_list|()
throws|throws
name|Exception
block|{
name|assertThat
argument_list|(
name|resolve
argument_list|(
literal|"Self"
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|accountOperations
operator|.
name|newAccount
argument_list|()
operator|.
name|fullname
argument_list|(
literal|"self"
argument_list|)
operator|.
name|create
argument_list|()
expr_stmt|;
name|Result
name|result
init|=
name|resolveAsResult
argument_list|(
literal|"self"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|asIdSet
argument_list|()
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|admin
operator|.
name|id
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|isSelf
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|asUniqueUser
argument_list|()
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|self
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|resolveAsResult
argument_list|(
literal|"me"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|asIdSet
argument_list|()
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|admin
operator|.
name|id
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|isSelf
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|asUniqueUser
argument_list|()
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|self
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|requestScopeOperations
operator|.
name|setApiUserAnonymous
argument_list|()
expr_stmt|;
name|checkBySelfFails
argument_list|()
expr_stmt|;
name|requestScopeOperations
operator|.
name|setApiUserInternal
argument_list|()
expr_stmt|;
name|checkBySelfFails
argument_list|()
expr_stmt|;
block|}
DECL|method|checkBySelfFails ()
specifier|private
name|void
name|checkBySelfFails
parameter_list|()
throws|throws
name|Exception
block|{
name|Result
name|result
init|=
name|resolveAsResult
argument_list|(
literal|"self"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|asIdSet
argument_list|()
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|isSelf
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
try|try
block|{
name|result
operator|.
name|asUnique
argument_list|()
expr_stmt|;
name|assert_
argument_list|()
operator|.
name|fail
argument_list|(
literal|"expected UnresolvableAccountException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnresolvableAccountException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
argument_list|)
operator|.
name|hasMessageThat
argument_list|()
operator|.
name|isEqualTo
argument_list|(
literal|"Resolving account 'self' requires login"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|isSelf
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
block|}
name|result
operator|=
name|resolveAsResult
argument_list|(
literal|"me"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|asIdSet
argument_list|()
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|isSelf
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
try|try
block|{
name|result
operator|.
name|asUnique
argument_list|()
expr_stmt|;
name|assert_
argument_list|()
operator|.
name|fail
argument_list|(
literal|"expected UnresolvableAccountException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnresolvableAccountException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
argument_list|)
operator|.
name|hasMessageThat
argument_list|()
operator|.
name|isEqualTo
argument_list|(
literal|"Resolving account 'me' requires login"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|isSelf
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|bySelfInactive ()
specifier|public
name|void
name|bySelfInactive
parameter_list|()
throws|throws
name|Exception
block|{
name|gApi
operator|.
name|accounts
argument_list|()
operator|.
name|id
argument_list|(
name|user
operator|.
name|id
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|setActive
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|requestScopeOperations
operator|.
name|setApiUser
argument_list|(
name|user
operator|.
name|id
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|gApi
operator|.
name|accounts
argument_list|()
operator|.
name|id
argument_list|(
literal|"self"
argument_list|)
operator|.
name|getActive
argument_list|()
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
name|Result
name|result
init|=
name|resolveAsResult
argument_list|(
literal|"self"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|asIdSet
argument_list|()
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|user
operator|.
name|id
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|isSelf
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|asUniqueUser
argument_list|()
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|self
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|byExactAccountId ()
specifier|public
name|void
name|byExactAccountId
parameter_list|()
throws|throws
name|Exception
block|{
name|Account
operator|.
name|Id
name|existingId
init|=
name|accountOperations
operator|.
name|newAccount
argument_list|()
operator|.
name|create
argument_list|()
decl_stmt|;
name|Account
operator|.
name|Id
name|idWithExistingIdAsFullname
init|=
name|accountOperations
operator|.
name|newAccount
argument_list|()
operator|.
name|fullname
argument_list|(
name|existingId
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|Account
operator|.
name|Id
name|nonexistentId
init|=
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
decl_stmt|;
name|accountOperations
operator|.
name|newAccount
argument_list|()
operator|.
name|fullname
argument_list|(
name|nonexistentId
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|create
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|resolve
argument_list|(
name|existingId
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|existingId
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resolve
argument_list|(
name|nonexistentId
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|resolveByNameOrEmail
argument_list|(
name|existingId
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|idWithExistingIdAsFullname
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|byParenthesizedAccountId ()
specifier|public
name|void
name|byParenthesizedAccountId
parameter_list|()
throws|throws
name|Exception
block|{
name|Account
operator|.
name|Id
name|existingId
init|=
name|accountOperations
operator|.
name|newAccount
argument_list|()
operator|.
name|fullname
argument_list|(
literal|"Test User"
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|accountOperations
operator|.
name|newAccount
argument_list|()
operator|.
name|fullname
argument_list|(
name|existingId
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|create
argument_list|()
expr_stmt|;
name|Account
operator|.
name|Id
name|nonexistentId
init|=
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
decl_stmt|;
name|accountOperations
operator|.
name|newAccount
argument_list|()
operator|.
name|fullname
argument_list|(
literal|"Any Name ("
operator|+
name|nonexistentId
operator|+
literal|")"
argument_list|)
operator|.
name|create
argument_list|()
expr_stmt|;
name|accountOperations
operator|.
name|newAccount
argument_list|()
operator|.
name|fullname
argument_list|(
name|nonexistentId
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|create
argument_list|()
expr_stmt|;
name|String
name|existingInput
init|=
literal|"Any Name ("
operator|+
name|existingId
operator|+
literal|")"
decl_stmt|;
name|assertThat
argument_list|(
name|resolve
argument_list|(
name|existingInput
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|existingId
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resolve
argument_list|(
literal|"Any Name ("
operator|+
name|nonexistentId
operator|+
literal|")"
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|resolveByNameOrEmail
argument_list|(
name|existingInput
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
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
name|String
name|existingUsername
init|=
literal|"myusername"
decl_stmt|;
name|Account
operator|.
name|Id
name|idWithUsername
init|=
name|accountOperations
operator|.
name|newAccount
argument_list|()
operator|.
name|username
argument_list|(
name|existingUsername
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|Account
operator|.
name|Id
name|idWithExistingUsernameAsFullname
init|=
name|accountOperations
operator|.
name|newAccount
argument_list|()
operator|.
name|fullname
argument_list|(
name|existingUsername
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|String
name|nonexistentUsername
init|=
literal|"anotherusername"
decl_stmt|;
name|Account
operator|.
name|Id
name|idWithFullname
init|=
name|accountOperations
operator|.
name|newAccount
argument_list|()
operator|.
name|fullname
argument_list|(
literal|"anotherusername"
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|resolve
argument_list|(
name|existingUsername
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|idWithUsername
argument_list|)
expr_stmt|;
comment|// Doesn't short-circuit just because the input looks like a valid username.
name|assertThat
argument_list|(
name|ExternalId
operator|.
name|isValidUsername
argument_list|(
name|nonexistentUsername
argument_list|)
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|resolve
argument_list|(
name|nonexistentUsername
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|idWithFullname
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resolveByNameOrEmail
argument_list|(
name|existingUsername
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|idWithExistingUsernameAsFullname
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|byNameAndEmail ()
specifier|public
name|void
name|byNameAndEmail
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|email
init|=
name|name
argument_list|(
literal|"user@example.com"
argument_list|)
decl_stmt|;
name|Account
operator|.
name|Id
name|idWithEmail
init|=
name|accountOperations
operator|.
name|newAccount
argument_list|()
operator|.
name|preferredEmail
argument_list|(
name|email
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|accountOperations
operator|.
name|newAccount
argument_list|()
operator|.
name|fullname
argument_list|(
name|email
argument_list|)
operator|.
name|create
argument_list|()
expr_stmt|;
name|String
name|input
init|=
literal|"First Last<"
operator|+
name|email
operator|+
literal|">"
decl_stmt|;
name|assertThat
argument_list|(
name|resolve
argument_list|(
name|input
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|idWithEmail
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resolveByNameOrEmail
argument_list|(
name|input
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|idWithEmail
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|byNameAndEmailPrefersAccountsWithMatchingFullName ()
specifier|public
name|void
name|byNameAndEmailPrefersAccountsWithMatchingFullName
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|email
init|=
name|name
argument_list|(
literal|"user@example.com"
argument_list|)
decl_stmt|;
name|Account
operator|.
name|Id
name|id1
init|=
name|accountOperations
operator|.
name|newAccount
argument_list|()
operator|.
name|fullname
argument_list|(
literal|"Aaa Bbb"
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|setPreferredEmailBypassingUniquenessCheck
argument_list|(
name|id1
argument_list|,
name|email
argument_list|)
expr_stmt|;
name|Account
operator|.
name|Id
name|id2
init|=
name|accountOperations
operator|.
name|newAccount
argument_list|()
operator|.
name|fullname
argument_list|(
literal|"Ccc Ddd"
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|setPreferredEmailBypassingUniquenessCheck
argument_list|(
name|id2
argument_list|,
name|email
argument_list|)
expr_stmt|;
name|String
name|input
init|=
literal|"First Last<"
operator|+
name|email
operator|+
literal|">"
decl_stmt|;
name|assertThat
argument_list|(
name|resolve
argument_list|(
name|input
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|id1
argument_list|,
name|id2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resolveByNameOrEmail
argument_list|(
name|input
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|id1
argument_list|,
name|id2
argument_list|)
expr_stmt|;
name|Account
operator|.
name|Id
name|id3
init|=
name|accountOperations
operator|.
name|newAccount
argument_list|()
operator|.
name|fullname
argument_list|(
literal|"First Last"
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|setPreferredEmailBypassingUniquenessCheck
argument_list|(
name|id3
argument_list|,
name|email
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resolve
argument_list|(
name|input
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|id3
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resolveByNameOrEmail
argument_list|(
name|input
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|id3
argument_list|)
expr_stmt|;
name|Account
operator|.
name|Id
name|id4
init|=
name|accountOperations
operator|.
name|newAccount
argument_list|()
operator|.
name|fullname
argument_list|(
literal|"First Last"
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|setPreferredEmailBypassingUniquenessCheck
argument_list|(
name|id4
argument_list|,
name|email
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resolve
argument_list|(
name|input
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|id3
argument_list|,
name|id4
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resolveByNameOrEmail
argument_list|(
name|input
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|id3
argument_list|,
name|id4
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
name|String
name|email
init|=
name|name
argument_list|(
literal|"user@example.com"
argument_list|)
decl_stmt|;
name|Account
operator|.
name|Id
name|idWithEmail
init|=
name|accountOperations
operator|.
name|newAccount
argument_list|()
operator|.
name|preferredEmail
argument_list|(
name|email
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|accountOperations
operator|.
name|newAccount
argument_list|()
operator|.
name|fullname
argument_list|(
name|email
argument_list|)
operator|.
name|create
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|resolve
argument_list|(
name|email
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|idWithEmail
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resolveByNameOrEmail
argument_list|(
name|email
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|idWithEmail
argument_list|)
expr_stmt|;
block|}
comment|// Can't test for ByRealm because DefaultRealm with the default (OPENID) auth type doesn't support
comment|// email expansion, so anything that would return a non-null value from DefaultRealm#lookup would
comment|// just be an email address, handled by other tests. This could be avoided if we inject some sort
comment|// of custom test realm instance, but the ugliness is not worth it for this small bit of test
comment|// coverage.
annotation|@
name|Test
DECL|method|byFullName ()
specifier|public
name|void
name|byFullName
parameter_list|()
throws|throws
name|Exception
block|{
name|Account
operator|.
name|Id
name|id1
init|=
name|accountOperations
operator|.
name|newAccount
argument_list|()
operator|.
name|fullname
argument_list|(
literal|"Somebodys Name"
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|accountOperations
operator|.
name|newAccount
argument_list|()
operator|.
name|fullname
argument_list|(
literal|"A totally different name"
argument_list|)
operator|.
name|create
argument_list|()
expr_stmt|;
name|String
name|input
init|=
literal|"Somebodys name"
decl_stmt|;
name|assertThat
argument_list|(
name|resolve
argument_list|(
name|input
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|id1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resolveByNameOrEmail
argument_list|(
name|input
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|id1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|byDefaultSearch ()
specifier|public
name|void
name|byDefaultSearch
parameter_list|()
throws|throws
name|Exception
block|{
name|Account
operator|.
name|Id
name|id1
init|=
name|accountOperations
operator|.
name|newAccount
argument_list|()
operator|.
name|fullname
argument_list|(
literal|"John Doe"
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|Account
operator|.
name|Id
name|id2
init|=
name|accountOperations
operator|.
name|newAccount
argument_list|()
operator|.
name|fullname
argument_list|(
literal|"Jane Doe"
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|resolve
argument_list|(
literal|"doe"
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|id1
argument_list|,
name|id2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resolveByNameOrEmail
argument_list|(
literal|"doe"
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|id1
argument_list|,
name|id2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|onlyExactIdReturnsInactiveAccounts ()
specifier|public
name|void
name|onlyExactIdReturnsInactiveAccounts
parameter_list|()
throws|throws
name|Exception
block|{
name|TestAccount
name|account
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
name|fullname
argument_list|(
literal|"Inactiveuser Name"
argument_list|)
operator|.
name|preferredEmail
argument_list|(
literal|"inactiveuser@example.com"
argument_list|)
operator|.
name|username
argument_list|(
literal|"inactiveusername"
argument_list|)
operator|.
name|create
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|Account
operator|.
name|Id
name|id
init|=
name|account
operator|.
name|accountId
argument_list|()
decl_stmt|;
name|String
name|nameEmail
init|=
name|account
operator|.
name|fullname
argument_list|()
operator|.
name|get
argument_list|()
operator|+
literal|"<"
operator|+
name|account
operator|.
name|preferredEmail
argument_list|()
operator|.
name|get
argument_list|()
operator|+
literal|">"
decl_stmt|;
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|inputs
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|account
operator|.
name|fullname
argument_list|()
operator|.
name|get
argument_list|()
operator|+
literal|" ("
operator|+
name|account
operator|.
name|accountId
argument_list|()
operator|+
literal|")"
argument_list|,
name|account
operator|.
name|fullname
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|account
operator|.
name|preferredEmail
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|nameEmail
argument_list|,
name|Splitter
operator|.
name|on
argument_list|(
literal|' '
argument_list|)
operator|.
name|splitToList
argument_list|(
name|account
operator|.
name|fullname
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|resolve
argument_list|(
name|account
operator|.
name|accountId
argument_list|()
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|id
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|input
range|:
name|inputs
control|)
block|{
name|assertThat
argument_list|(
name|resolve
argument_list|(
name|input
argument_list|)
argument_list|)
operator|.
name|named
argument_list|(
literal|"results for %s (active)"
argument_list|,
name|input
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
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
name|setActive
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resolve
argument_list|(
name|account
operator|.
name|accountId
argument_list|()
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|id
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|input
range|:
name|inputs
control|)
block|{
name|Result
name|result
init|=
name|accountResolver
operator|.
name|resolve
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|asIdSet
argument_list|()
argument_list|)
operator|.
name|named
argument_list|(
literal|"results for %s (inactive)"
argument_list|,
name|input
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
try|try
block|{
name|result
operator|.
name|asUnique
argument_list|()
expr_stmt|;
name|assert_
argument_list|()
operator|.
name|fail
argument_list|(
literal|"expected UnresolvableAccountException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnresolvableAccountException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
argument_list|)
operator|.
name|hasMessageThat
argument_list|()
operator|.
name|isEqualTo
argument_list|(
literal|"Account '"
operator|+
name|input
operator|+
literal|"' only matches inactive accounts. To use an inactive account, retry"
operator|+
literal|" with one of the following exact account IDs:\n"
operator|+
name|id
operator|+
literal|": "
operator|+
name|nameEmail
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|resolveByNameOrEmail
argument_list|(
name|input
argument_list|)
argument_list|)
operator|.
name|named
argument_list|(
literal|"results by name or email for %s (inactive)"
argument_list|,
name|input
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|filterVisibility ()
specifier|public
name|void
name|filterVisibility
parameter_list|()
throws|throws
name|Exception
block|{
name|Account
operator|.
name|Id
name|id1
init|=
name|accountOperations
operator|.
name|newAccount
argument_list|()
operator|.
name|fullname
argument_list|(
literal|"John Doe"
argument_list|)
operator|.
name|preferredEmail
argument_list|(
literal|"johndoe@example.com"
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|Account
operator|.
name|Id
name|id2
init|=
name|accountOperations
operator|.
name|newAccount
argument_list|()
operator|.
name|fullname
argument_list|(
literal|"Jane Doe"
argument_list|)
operator|.
name|preferredEmail
argument_list|(
literal|"janedoe@example.com"
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
comment|// Admin can see all accounts. Use a variety of searches, including with/without
comment|// callerMayAssumeCandidatesAreVisible.
name|assertThat
argument_list|(
name|resolve
argument_list|(
name|id1
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|id1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resolve
argument_list|(
literal|"John Doe"
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|id1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resolve
argument_list|(
literal|"johndoe@example.com"
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|id1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resolve
argument_list|(
name|id2
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|id2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resolve
argument_list|(
literal|"Jane Doe"
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|id2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resolve
argument_list|(
literal|"janedoe@example.com"
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|id2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resolve
argument_list|(
literal|"doe"
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|id1
argument_list|,
name|id2
argument_list|)
expr_stmt|;
comment|// id2 can't see id1, and vice versa.
name|requestScopeOperations
operator|.
name|setApiUser
argument_list|(
name|id1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resolve
argument_list|(
name|id1
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|id1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resolve
argument_list|(
literal|"John Doe"
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|id1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resolve
argument_list|(
literal|"johndoe@example.com"
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|id1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resolve
argument_list|(
name|id2
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|resolve
argument_list|(
literal|"Jane Doe"
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|resolve
argument_list|(
literal|"janedoe@example.com"
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|resolve
argument_list|(
literal|"doe"
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|id1
argument_list|)
expr_stmt|;
name|requestScopeOperations
operator|.
name|setApiUser
argument_list|(
name|id2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resolve
argument_list|(
name|id1
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|resolve
argument_list|(
literal|"John Doe"
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|resolve
argument_list|(
literal|"johndoe@example.com"
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|resolve
argument_list|(
name|id2
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|id2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resolve
argument_list|(
literal|"Jane Doe"
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|id2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resolve
argument_list|(
literal|"janedoe@example.com"
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|id2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resolve
argument_list|(
literal|"doe"
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|id2
argument_list|)
expr_stmt|;
block|}
DECL|method|resolve (Object input)
specifier|private
name|ImmutableSet
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|resolve
parameter_list|(
name|Object
name|input
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|resolveAsResult
argument_list|(
name|input
argument_list|)
operator|.
name|asIdSet
argument_list|()
return|;
block|}
DECL|method|resolveAsResult (Object input)
specifier|private
name|Result
name|resolveAsResult
parameter_list|(
name|Object
name|input
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|accountResolver
operator|.
name|resolve
argument_list|(
name|input
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|resolveByNameOrEmail (Object input)
specifier|private
name|ImmutableSet
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|resolveByNameOrEmail
parameter_list|(
name|Object
name|input
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|accountResolver
operator|.
name|resolveByNameOrEmail
argument_list|(
name|input
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|asIdSet
argument_list|()
return|;
block|}
DECL|method|setPreferredEmailBypassingUniquenessCheck (Account.Id id, String email)
specifier|private
name|void
name|setPreferredEmailBypassingUniquenessCheck
parameter_list|(
name|Account
operator|.
name|Id
name|id
parameter_list|,
name|String
name|email
parameter_list|)
throws|throws
name|Exception
block|{
name|Optional
argument_list|<
name|AccountState
argument_list|>
name|result
init|=
name|accountsUpdateProvider
operator|.
name|get
argument_list|()
operator|.
name|update
argument_list|(
literal|"Force set preferred email"
argument_list|,
name|id
argument_list|,
parameter_list|(
name|s
parameter_list|,
name|u
parameter_list|)
lambda|->
name|u
operator|.
name|setPreferredEmail
argument_list|(
name|email
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|map
argument_list|(
name|a
lambda|->
name|a
operator|.
name|getAccount
argument_list|()
operator|.
name|getPreferredEmail
argument_list|()
argument_list|)
argument_list|)
operator|.
name|hasValue
argument_list|(
name|email
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

