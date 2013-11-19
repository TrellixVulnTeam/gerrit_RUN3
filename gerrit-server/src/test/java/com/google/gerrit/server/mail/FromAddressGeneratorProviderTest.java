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
DECL|package|com.google.gerrit.server.mail
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
package|;
end_package

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
name|assertNotNull
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
name|assertTrue
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
name|AccountExternalId
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
name|util
operator|.
name|TimeUtil
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
DECL|method|setFrom (final String newFrom)
specifier|private
name|void
name|setFrom
parameter_list|(
specifier|final
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
annotation|@
name|Test
DECL|method|testDefaultIsMIXED ()
specifier|public
name|void
name|testDefaultIsMIXED
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|create
argument_list|()
operator|instanceof
name|FromAddressGeneratorProvider
operator|.
name|PatternGen
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSelectUSER ()
specifier|public
name|void
name|testSelectUSER
parameter_list|()
block|{
name|setFrom
argument_list|(
literal|"USER"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|create
argument_list|()
operator|instanceof
name|FromAddressGeneratorProvider
operator|.
name|UserGen
argument_list|)
expr_stmt|;
name|setFrom
argument_list|(
literal|"user"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|create
argument_list|()
operator|instanceof
name|FromAddressGeneratorProvider
operator|.
name|UserGen
argument_list|)
expr_stmt|;
name|setFrom
argument_list|(
literal|"uSeR"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|create
argument_list|()
operator|instanceof
name|FromAddressGeneratorProvider
operator|.
name|UserGen
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUSER_FullyConfiguredUser ()
specifier|public
name|void
name|testUSER_FullyConfiguredUser
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
name|assertNotNull
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|name
argument_list|,
name|r
operator|.
name|name
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|email
argument_list|,
name|r
operator|.
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
DECL|method|testUSER_NoFullNameUser ()
specifier|public
name|void
name|testUSER_NoFullNameUser
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
name|assertNotNull
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|r
operator|.
name|name
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|email
argument_list|,
name|r
operator|.
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
DECL|method|testUSER_NoPreferredEmailUser ()
specifier|public
name|void
name|testUSER_NoPreferredEmailUser
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
name|assertNotNull
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|name
argument_list|,
name|r
operator|.
name|name
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ident
operator|.
name|getEmailAddress
argument_list|()
argument_list|,
name|r
operator|.
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
DECL|method|testUSER_NullUser ()
specifier|public
name|void
name|testUSER_NullUser
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
name|assertNotNull
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ident
operator|.
name|getName
argument_list|()
argument_list|,
name|r
operator|.
name|name
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ident
operator|.
name|getEmailAddress
argument_list|()
argument_list|,
name|r
operator|.
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
DECL|method|testSelectSERVER ()
specifier|public
name|void
name|testSelectSERVER
parameter_list|()
block|{
name|setFrom
argument_list|(
literal|"SERVER"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|create
argument_list|()
operator|instanceof
name|FromAddressGeneratorProvider
operator|.
name|ServerGen
argument_list|)
expr_stmt|;
name|setFrom
argument_list|(
literal|"server"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|create
argument_list|()
operator|instanceof
name|FromAddressGeneratorProvider
operator|.
name|ServerGen
argument_list|)
expr_stmt|;
name|setFrom
argument_list|(
literal|"sErVeR"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|create
argument_list|()
operator|instanceof
name|FromAddressGeneratorProvider
operator|.
name|ServerGen
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSERVER_FullyConfiguredUser ()
specifier|public
name|void
name|testSERVER_FullyConfiguredUser
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
name|assertNotNull
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ident
operator|.
name|getName
argument_list|()
argument_list|,
name|r
operator|.
name|name
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ident
operator|.
name|getEmailAddress
argument_list|()
argument_list|,
name|r
operator|.
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
DECL|method|testSERVER_NullUser ()
specifier|public
name|void
name|testSERVER_NullUser
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
name|assertNotNull
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ident
operator|.
name|getName
argument_list|()
argument_list|,
name|r
operator|.
name|name
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ident
operator|.
name|getEmailAddress
argument_list|()
argument_list|,
name|r
operator|.
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
DECL|method|testSelectMIXED ()
specifier|public
name|void
name|testSelectMIXED
parameter_list|()
block|{
name|setFrom
argument_list|(
literal|"MIXED"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|create
argument_list|()
operator|instanceof
name|FromAddressGeneratorProvider
operator|.
name|PatternGen
argument_list|)
expr_stmt|;
name|setFrom
argument_list|(
literal|"mixed"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|create
argument_list|()
operator|instanceof
name|FromAddressGeneratorProvider
operator|.
name|PatternGen
argument_list|)
expr_stmt|;
name|setFrom
argument_list|(
literal|"mIxEd"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|create
argument_list|()
operator|instanceof
name|FromAddressGeneratorProvider
operator|.
name|PatternGen
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMIXED_FullyConfiguredUser ()
specifier|public
name|void
name|testMIXED_FullyConfiguredUser
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
name|assertNotNull
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|name
operator|+
literal|" (Code Review)"
argument_list|,
name|r
operator|.
name|name
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ident
operator|.
name|getEmailAddress
argument_list|()
argument_list|,
name|r
operator|.
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
DECL|method|testMIXED_NoFullNameUser ()
specifier|public
name|void
name|testMIXED_NoFullNameUser
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
name|assertNotNull
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Anonymous Coward (Code Review)"
argument_list|,
name|r
operator|.
name|name
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ident
operator|.
name|getEmailAddress
argument_list|()
argument_list|,
name|r
operator|.
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
DECL|method|testMIXED_NoPreferredEmailUser ()
specifier|public
name|void
name|testMIXED_NoPreferredEmailUser
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
name|assertNotNull
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|name
operator|+
literal|" (Code Review)"
argument_list|,
name|r
operator|.
name|name
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ident
operator|.
name|getEmailAddress
argument_list|()
argument_list|,
name|r
operator|.
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
DECL|method|testMIXED_NullUser ()
specifier|public
name|void
name|testMIXED_NullUser
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
name|assertNotNull
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ident
operator|.
name|getName
argument_list|()
argument_list|,
name|r
operator|.
name|name
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ident
operator|.
name|getEmailAddress
argument_list|()
argument_list|,
name|r
operator|.
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
DECL|method|testCUSTOM_FullyConfiguredUser ()
specifier|public
name|void
name|testCUSTOM_FullyConfiguredUser
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
name|assertNotNull
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"A "
operator|+
name|name
operator|+
literal|" B"
argument_list|,
name|r
operator|.
name|name
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"my.server@email.address"
argument_list|,
name|r
operator|.
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
DECL|method|testCUSTOM_NoFullNameUser ()
specifier|public
name|void
name|testCUSTOM_NoFullNameUser
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
name|assertNotNull
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"A Anonymous Coward B"
argument_list|,
name|r
operator|.
name|name
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"my.server@email.address"
argument_list|,
name|r
operator|.
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
DECL|method|testCUSTOM_NullUser ()
specifier|public
name|void
name|testCUSTOM_NullUser
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
name|assertNotNull
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ident
operator|.
name|getName
argument_list|()
argument_list|,
name|r
operator|.
name|name
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"my.server@email.address"
argument_list|,
name|r
operator|.
name|email
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|accountCache
argument_list|)
expr_stmt|;
block|}
DECL|method|user (final String name, final String email)
specifier|private
name|Account
operator|.
name|Id
name|user
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
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
DECL|method|userNoLookup (final String name, final String email)
specifier|private
name|Account
operator|.
name|Id
name|userNoLookup
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
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
DECL|method|makeUser (final String name, final String email)
specifier|private
name|AccountState
name|makeUser
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
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
name|account
argument_list|,
name|Collections
operator|.
expr|<
name|AccountGroup
operator|.
name|UUID
operator|>
name|emptySet
argument_list|()
argument_list|,
name|Collections
operator|.
expr|<
name|AccountExternalId
operator|>
name|emptySet
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

