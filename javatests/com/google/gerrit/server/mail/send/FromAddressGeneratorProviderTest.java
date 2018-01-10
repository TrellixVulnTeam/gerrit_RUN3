begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2009 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.mail.send
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|mail
operator|.
name|send
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
name|easymock
operator|.
name|EasyMock
operator|.
name|createStrictMock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|EasyMock
operator|.
name|eq
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|EasyMock
operator|.
name|expect
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|EasyMock
operator|.
name|replay
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|EasyMock
operator|.
name|verify
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
name|ImmutableMap
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
name|common
operator|.
name|TimeUtil
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
name|client
operator|.
name|GeneralPreferencesInfo
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
name|config
operator|.
name|AllUsersName
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
name|AllUsersNameProvider
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
name|mail
operator|.
name|Address
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
name|Config
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
name|PersonIdent
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
DECL|class|FromAddressGeneratorProviderTest
specifier|public
class|class
name|FromAddressGeneratorProviderTest
block|{
DECL|field|config
specifier|private
name|Config
name|config
decl_stmt|;
DECL|field|ident
specifier|private
name|PersonIdent
name|ident
decl_stmt|;
DECL|field|accountCache
specifier|private
name|AccountCache
name|accountCache
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
name|config
operator|=
operator|new
name|Config
argument_list|()
expr_stmt|;
name|ident
operator|=
operator|new
name|PersonIdent
argument_list|(
literal|"NAME"
argument_list|,
literal|"e@email"
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|accountCache
operator|=
name|createStrictMock
argument_list|(
name|AccountCache
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|create ()
specifier|private
name|FromAddressGenerator
name|create
parameter_list|()
block|{
return|return
operator|new
name|FromAddressGeneratorProvider
argument_list|(
name|config
argument_list|,
literal|"Anonymous Coward"
argument_list|,
name|ident
argument_list|,
name|accountCache
argument_list|)
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|setFrom (String newFrom)
specifier|private
name|void
name|setFrom
parameter_list|(
name|String
name|newFrom
parameter_list|)
block|{
name|config
operator|.
name|setString
argument_list|(
literal|"sendemail"
argument_list|,
literal|null
argument_list|,
literal|"from"
argument_list|,
name|newFrom
argument_list|)
expr_stmt|;
block|}
DECL|method|setDomains (List<String> domains)
specifier|private
name|void
name|setDomains
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|domains
parameter_list|)
block|{
name|config
operator|.
name|setStringList
argument_list|(
literal|"sendemail"
argument_list|,
literal|null
argument_list|,
literal|"allowedDomain"
argument_list|,
name|domains
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|defaultIsMIXED ()
specifier|public
name|void
name|defaultIsMIXED
parameter_list|()
block|{
name|assertThat
argument_list|(
name|create
argument_list|()
argument_list|)
operator|.
name|isInstanceOf
argument_list|(
name|FromAddressGeneratorProvider
operator|.
name|PatternGen
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|selectUSER ()
specifier|public
name|void
name|selectUSER
parameter_list|()
block|{
name|setFrom
argument_list|(
literal|"USER"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|create
argument_list|()
argument_list|)
operator|.
name|isInstanceOf
argument_list|(
name|FromAddressGeneratorProvider
operator|.
name|UserGen
operator|.
name|class
argument_list|)
expr_stmt|;
name|setFrom
argument_list|(
literal|"user"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|create
argument_list|()
argument_list|)
operator|.
name|isInstanceOf
argument_list|(
name|FromAddressGeneratorProvider
operator|.
name|UserGen
operator|.
name|class
argument_list|)
expr_stmt|;
name|setFrom
argument_list|(
literal|"uSeR"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|create
argument_list|()
argument_list|)
operator|.
name|isInstanceOf
argument_list|(
name|FromAddressGeneratorProvider
operator|.
name|UserGen
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|USER_FullyConfiguredUser ()
specifier|public
name|void
name|USER_FullyConfiguredUser
parameter_list|()
block|{
name|setFrom
argument_list|(
literal|"USER"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|name
init|=
literal|"A U. Thor"
decl_stmt|;
specifier|final
name|String
name|email
init|=
literal|"a.u.thor@test.example.com"
decl_stmt|;
specifier|final
name|Account
operator|.
name|Id
name|user
init|=
name|user
argument_list|(
name|name
argument_list|,
name|email
argument_list|)
decl_stmt|;
name|replay
argument_list|(
name|accountCache
argument_list|)
expr_stmt|;
specifier|final
name|Address
name|r
init|=
name|create
argument_list|()
operator|.
name|from
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|r
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getEmail
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|email
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|accountCache
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|USER_NoFullNameUser ()
specifier|public
name|void
name|USER_NoFullNameUser
parameter_list|()
block|{
name|setFrom
argument_list|(
literal|"USER"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|email
init|=
literal|"a.u.thor@test.example.com"
decl_stmt|;
specifier|final
name|Account
operator|.
name|Id
name|user
init|=
name|user
argument_list|(
literal|null
argument_list|,
name|email
argument_list|)
decl_stmt|;
name|replay
argument_list|(
name|accountCache
argument_list|)
expr_stmt|;
specifier|final
name|Address
name|r
init|=
name|create
argument_list|()
operator|.
name|from
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|r
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getEmail
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|email
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|accountCache
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|USER_NoPreferredEmailUser ()
specifier|public
name|void
name|USER_NoPreferredEmailUser
parameter_list|()
block|{
name|setFrom
argument_list|(
literal|"USER"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|name
init|=
literal|"A U. Thor"
decl_stmt|;
specifier|final
name|Account
operator|.
name|Id
name|user
init|=
name|user
argument_list|(
name|name
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|replay
argument_list|(
name|accountCache
argument_list|)
expr_stmt|;
specifier|final
name|Address
name|r
init|=
name|create
argument_list|()
operator|.
name|from
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|r
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|name
operator|+
literal|" (Code Review)"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getEmail
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|ident
operator|.
name|getEmailAddress
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|accountCache
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|USER_NullUser ()
specifier|public
name|void
name|USER_NullUser
parameter_list|()
block|{
name|setFrom
argument_list|(
literal|"USER"
argument_list|)
expr_stmt|;
name|replay
argument_list|(
name|accountCache
argument_list|)
expr_stmt|;
specifier|final
name|Address
name|r
init|=
name|create
argument_list|()
operator|.
name|from
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|r
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|ident
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getEmail
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|ident
operator|.
name|getEmailAddress
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|accountCache
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|USERAllowDomain ()
specifier|public
name|void
name|USERAllowDomain
parameter_list|()
block|{
name|setFrom
argument_list|(
literal|"USER"
argument_list|)
expr_stmt|;
name|setDomains
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"*.example.com"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|name
init|=
literal|"A U. Thor"
decl_stmt|;
specifier|final
name|String
name|email
init|=
literal|"a.u.thor@test.example.com"
decl_stmt|;
specifier|final
name|Account
operator|.
name|Id
name|user
init|=
name|user
argument_list|(
name|name
argument_list|,
name|email
argument_list|)
decl_stmt|;
name|replay
argument_list|(
name|accountCache
argument_list|)
expr_stmt|;
specifier|final
name|Address
name|r
init|=
name|create
argument_list|()
operator|.
name|from
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|r
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getEmail
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|email
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|accountCache
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|USERNoAllowDomain ()
specifier|public
name|void
name|USERNoAllowDomain
parameter_list|()
block|{
name|setFrom
argument_list|(
literal|"USER"
argument_list|)
expr_stmt|;
name|setDomains
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"example.com"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|name
init|=
literal|"A U. Thor"
decl_stmt|;
specifier|final
name|String
name|email
init|=
literal|"a.u.thor@test.com"
decl_stmt|;
specifier|final
name|Account
operator|.
name|Id
name|user
init|=
name|user
argument_list|(
name|name
argument_list|,
name|email
argument_list|)
decl_stmt|;
name|replay
argument_list|(
name|accountCache
argument_list|)
expr_stmt|;
specifier|final
name|Address
name|r
init|=
name|create
argument_list|()
operator|.
name|from
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|r
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|name
operator|+
literal|" (Code Review)"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getEmail
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|ident
operator|.
name|getEmailAddress
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|accountCache
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|USERAllowDomainTwice ()
specifier|public
name|void
name|USERAllowDomainTwice
parameter_list|()
block|{
name|setFrom
argument_list|(
literal|"USER"
argument_list|)
expr_stmt|;
name|setDomains
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"example.com"
argument_list|)
argument_list|)
expr_stmt|;
name|setDomains
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"test.com"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|name
init|=
literal|"A U. Thor"
decl_stmt|;
specifier|final
name|String
name|email
init|=
literal|"a.u.thor@test.com"
decl_stmt|;
specifier|final
name|Account
operator|.
name|Id
name|user
init|=
name|user
argument_list|(
name|name
argument_list|,
name|email
argument_list|)
decl_stmt|;
name|replay
argument_list|(
name|accountCache
argument_list|)
expr_stmt|;
specifier|final
name|Address
name|r
init|=
name|create
argument_list|()
operator|.
name|from
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|r
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getEmail
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|email
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|accountCache
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|USERAllowDomainTwiceReverse ()
specifier|public
name|void
name|USERAllowDomainTwiceReverse
parameter_list|()
block|{
name|setFrom
argument_list|(
literal|"USER"
argument_list|)
expr_stmt|;
name|setDomains
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"test.com"
argument_list|)
argument_list|)
expr_stmt|;
name|setDomains
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"example.com"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|name
init|=
literal|"A U. Thor"
decl_stmt|;
specifier|final
name|String
name|email
init|=
literal|"a.u.thor@test.com"
decl_stmt|;
specifier|final
name|Account
operator|.
name|Id
name|user
init|=
name|user
argument_list|(
name|name
argument_list|,
name|email
argument_list|)
decl_stmt|;
name|replay
argument_list|(
name|accountCache
argument_list|)
expr_stmt|;
specifier|final
name|Address
name|r
init|=
name|create
argument_list|()
operator|.
name|from
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|r
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|name
operator|+
literal|" (Code Review)"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getEmail
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|ident
operator|.
name|getEmailAddress
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|accountCache
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|USERAllowTwoDomains ()
specifier|public
name|void
name|USERAllowTwoDomains
parameter_list|()
block|{
name|setFrom
argument_list|(
literal|"USER"
argument_list|)
expr_stmt|;
name|setDomains
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"example.com"
argument_list|,
literal|"test.com"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|name
init|=
literal|"A U. Thor"
decl_stmt|;
specifier|final
name|String
name|email
init|=
literal|"a.u.thor@test.com"
decl_stmt|;
specifier|final
name|Account
operator|.
name|Id
name|user
init|=
name|user
argument_list|(
name|name
argument_list|,
name|email
argument_list|)
decl_stmt|;
name|replay
argument_list|(
name|accountCache
argument_list|)
expr_stmt|;
specifier|final
name|Address
name|r
init|=
name|create
argument_list|()
operator|.
name|from
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|r
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getEmail
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|email
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|accountCache
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|selectSERVER ()
specifier|public
name|void
name|selectSERVER
parameter_list|()
block|{
name|setFrom
argument_list|(
literal|"SERVER"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|create
argument_list|()
argument_list|)
operator|.
name|isInstanceOf
argument_list|(
name|FromAddressGeneratorProvider
operator|.
name|ServerGen
operator|.
name|class
argument_list|)
expr_stmt|;
name|setFrom
argument_list|(
literal|"server"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|create
argument_list|()
argument_list|)
operator|.
name|isInstanceOf
argument_list|(
name|FromAddressGeneratorProvider
operator|.
name|ServerGen
operator|.
name|class
argument_list|)
expr_stmt|;
name|setFrom
argument_list|(
literal|"sErVeR"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|create
argument_list|()
argument_list|)
operator|.
name|isInstanceOf
argument_list|(
name|FromAddressGeneratorProvider
operator|.
name|ServerGen
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|SERVER_FullyConfiguredUser ()
specifier|public
name|void
name|SERVER_FullyConfiguredUser
parameter_list|()
block|{
name|setFrom
argument_list|(
literal|"SERVER"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|name
init|=
literal|"A U. Thor"
decl_stmt|;
specifier|final
name|String
name|email
init|=
literal|"a.u.thor@test.example.com"
decl_stmt|;
specifier|final
name|Account
operator|.
name|Id
name|user
init|=
name|userNoLookup
argument_list|(
name|name
argument_list|,
name|email
argument_list|)
decl_stmt|;
name|replay
argument_list|(
name|accountCache
argument_list|)
expr_stmt|;
specifier|final
name|Address
name|r
init|=
name|create
argument_list|()
operator|.
name|from
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|r
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|ident
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getEmail
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|ident
operator|.
name|getEmailAddress
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|accountCache
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|SERVER_NullUser ()
specifier|public
name|void
name|SERVER_NullUser
parameter_list|()
block|{
name|setFrom
argument_list|(
literal|"SERVER"
argument_list|)
expr_stmt|;
name|replay
argument_list|(
name|accountCache
argument_list|)
expr_stmt|;
specifier|final
name|Address
name|r
init|=
name|create
argument_list|()
operator|.
name|from
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|r
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|ident
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getEmail
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|ident
operator|.
name|getEmailAddress
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|accountCache
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|selectMIXED ()
specifier|public
name|void
name|selectMIXED
parameter_list|()
block|{
name|setFrom
argument_list|(
literal|"MIXED"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|create
argument_list|()
argument_list|)
operator|.
name|isInstanceOf
argument_list|(
name|FromAddressGeneratorProvider
operator|.
name|PatternGen
operator|.
name|class
argument_list|)
expr_stmt|;
name|setFrom
argument_list|(
literal|"mixed"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|create
argument_list|()
argument_list|)
operator|.
name|isInstanceOf
argument_list|(
name|FromAddressGeneratorProvider
operator|.
name|PatternGen
operator|.
name|class
argument_list|)
expr_stmt|;
name|setFrom
argument_list|(
literal|"mIxEd"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|create
argument_list|()
argument_list|)
operator|.
name|isInstanceOf
argument_list|(
name|FromAddressGeneratorProvider
operator|.
name|PatternGen
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|MIXED_FullyConfiguredUser ()
specifier|public
name|void
name|MIXED_FullyConfiguredUser
parameter_list|()
block|{
name|setFrom
argument_list|(
literal|"MIXED"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|name
init|=
literal|"A U. Thor"
decl_stmt|;
specifier|final
name|String
name|email
init|=
literal|"a.u.thor@test.example.com"
decl_stmt|;
specifier|final
name|Account
operator|.
name|Id
name|user
init|=
name|user
argument_list|(
name|name
argument_list|,
name|email
argument_list|)
decl_stmt|;
name|replay
argument_list|(
name|accountCache
argument_list|)
expr_stmt|;
specifier|final
name|Address
name|r
init|=
name|create
argument_list|()
operator|.
name|from
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|r
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|name
operator|+
literal|" (Code Review)"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getEmail
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|ident
operator|.
name|getEmailAddress
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|accountCache
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|MIXED_NoFullNameUser ()
specifier|public
name|void
name|MIXED_NoFullNameUser
parameter_list|()
block|{
name|setFrom
argument_list|(
literal|"MIXED"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|email
init|=
literal|"a.u.thor@test.example.com"
decl_stmt|;
specifier|final
name|Account
operator|.
name|Id
name|user
init|=
name|user
argument_list|(
literal|null
argument_list|,
name|email
argument_list|)
decl_stmt|;
name|replay
argument_list|(
name|accountCache
argument_list|)
expr_stmt|;
specifier|final
name|Address
name|r
init|=
name|create
argument_list|()
operator|.
name|from
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|r
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"Anonymous Coward (Code Review)"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getEmail
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|ident
operator|.
name|getEmailAddress
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|accountCache
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|MIXED_NoPreferredEmailUser ()
specifier|public
name|void
name|MIXED_NoPreferredEmailUser
parameter_list|()
block|{
name|setFrom
argument_list|(
literal|"MIXED"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|name
init|=
literal|"A U. Thor"
decl_stmt|;
specifier|final
name|Account
operator|.
name|Id
name|user
init|=
name|user
argument_list|(
name|name
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|replay
argument_list|(
name|accountCache
argument_list|)
expr_stmt|;
specifier|final
name|Address
name|r
init|=
name|create
argument_list|()
operator|.
name|from
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|r
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|name
operator|+
literal|" (Code Review)"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getEmail
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|ident
operator|.
name|getEmailAddress
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|accountCache
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|MIXED_NullUser ()
specifier|public
name|void
name|MIXED_NullUser
parameter_list|()
block|{
name|setFrom
argument_list|(
literal|"MIXED"
argument_list|)
expr_stmt|;
name|replay
argument_list|(
name|accountCache
argument_list|)
expr_stmt|;
specifier|final
name|Address
name|r
init|=
name|create
argument_list|()
operator|.
name|from
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|r
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|ident
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getEmail
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|ident
operator|.
name|getEmailAddress
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|accountCache
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|CUSTOM_FullyConfiguredUser ()
specifier|public
name|void
name|CUSTOM_FullyConfiguredUser
parameter_list|()
block|{
name|setFrom
argument_list|(
literal|"A ${user} B<my.server@email.address>"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|name
init|=
literal|"A U. Thor"
decl_stmt|;
specifier|final
name|String
name|email
init|=
literal|"a.u.thor@test.example.com"
decl_stmt|;
specifier|final
name|Account
operator|.
name|Id
name|user
init|=
name|user
argument_list|(
name|name
argument_list|,
name|email
argument_list|)
decl_stmt|;
name|replay
argument_list|(
name|accountCache
argument_list|)
expr_stmt|;
specifier|final
name|Address
name|r
init|=
name|create
argument_list|()
operator|.
name|from
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|r
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"A "
operator|+
name|name
operator|+
literal|" B"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getEmail
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"my.server@email.address"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|accountCache
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|CUSTOM_NoFullNameUser ()
specifier|public
name|void
name|CUSTOM_NoFullNameUser
parameter_list|()
block|{
name|setFrom
argument_list|(
literal|"A ${user} B<my.server@email.address>"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|email
init|=
literal|"a.u.thor@test.example.com"
decl_stmt|;
specifier|final
name|Account
operator|.
name|Id
name|user
init|=
name|user
argument_list|(
literal|null
argument_list|,
name|email
argument_list|)
decl_stmt|;
name|replay
argument_list|(
name|accountCache
argument_list|)
expr_stmt|;
specifier|final
name|Address
name|r
init|=
name|create
argument_list|()
operator|.
name|from
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|r
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"A Anonymous Coward B"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getEmail
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"my.server@email.address"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|accountCache
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|CUSTOM_NullUser ()
specifier|public
name|void
name|CUSTOM_NullUser
parameter_list|()
block|{
name|setFrom
argument_list|(
literal|"A ${user} B<my.server@email.address>"
argument_list|)
expr_stmt|;
name|replay
argument_list|(
name|accountCache
argument_list|)
expr_stmt|;
specifier|final
name|Address
name|r
init|=
name|create
argument_list|()
operator|.
name|from
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|r
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|ident
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getEmail
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"my.server@email.address"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|accountCache
argument_list|)
expr_stmt|;
block|}
DECL|method|user (String name, String email)
specifier|private
name|Account
operator|.
name|Id
name|user
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|email
parameter_list|)
block|{
specifier|final
name|AccountState
name|s
init|=
name|makeUser
argument_list|(
name|name
argument_list|,
name|email
argument_list|)
decl_stmt|;
name|expect
argument_list|(
name|accountCache
operator|.
name|get
argument_list|(
name|eq
argument_list|(
name|s
operator|.
name|getAccount
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
name|s
argument_list|)
expr_stmt|;
return|return
name|s
operator|.
name|getAccount
argument_list|()
operator|.
name|getId
argument_list|()
return|;
block|}
DECL|method|userNoLookup (String name, String email)
specifier|private
name|Account
operator|.
name|Id
name|userNoLookup
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|email
parameter_list|)
block|{
specifier|final
name|AccountState
name|s
init|=
name|makeUser
argument_list|(
name|name
argument_list|,
name|email
argument_list|)
decl_stmt|;
return|return
name|s
operator|.
name|getAccount
argument_list|()
operator|.
name|getId
argument_list|()
return|;
block|}
DECL|method|makeUser (String name, String email)
specifier|private
name|AccountState
name|makeUser
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|email
parameter_list|)
block|{
specifier|final
name|Account
operator|.
name|Id
name|userId
init|=
operator|new
name|Account
operator|.
name|Id
argument_list|(
literal|42
argument_list|)
decl_stmt|;
specifier|final
name|Account
name|account
init|=
operator|new
name|Account
argument_list|(
name|userId
argument_list|,
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
decl_stmt|;
name|account
operator|.
name|setFullName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|account
operator|.
name|setPreferredEmail
argument_list|(
name|email
argument_list|)
expr_stmt|;
return|return
operator|new
name|AccountState
argument_list|(
operator|new
name|AllUsersName
argument_list|(
name|AllUsersNameProvider
operator|.
name|DEFAULT
argument_list|)
argument_list|,
name|account
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|()
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|()
argument_list|,
name|GeneralPreferencesInfo
operator|.
name|defaults
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

