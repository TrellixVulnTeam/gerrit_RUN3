begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2014 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.notedb
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|notedb
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Scopes
operator|.
name|SINGLETON
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|SECONDS
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
name|common
operator|.
name|data
operator|.
name|SubmitRecord
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
name|config
operator|.
name|FactoryModule
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
name|metrics
operator|.
name|DisabledMetricMaker
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
name|metrics
operator|.
name|MetricMaker
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
name|Change
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
name|Comment
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
name|CommentRange
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
name|PatchSet
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
name|GerritPersonIdent
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
name|InternalUser
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
name|CapabilityControl
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
name|FakeRealm
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
name|GroupBackend
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
name|Realm
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
name|config
operator|.
name|AnonymousCowardName
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
name|AnonymousCowardNameProvider
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
name|CanonicalWebUrl
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
name|DisableReverseDnsLookup
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
name|GerritServerConfig
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
name|GerritServerId
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
name|extensions
operator|.
name|events
operator|.
name|GitReferenceUpdated
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
name|GitModule
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
name|FakeAccountCache
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
name|GerritBaseTests
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
name|InMemoryRepositoryManager
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
name|TestChanges
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
name|TestNotesMigration
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
name|TestTimeUtil
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
name|Guice
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
name|internal
operator|.
name|storage
operator|.
name|dfs
operator|.
name|InMemoryRepository
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
name|junit
operator|.
name|TestRepository
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
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Timestamp
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimeZone
import|;
end_import

begin_class
annotation|@
name|Ignore
annotation|@
name|RunWith
argument_list|(
name|ConfigSuite
operator|.
name|class
argument_list|)
DECL|class|AbstractChangeNotesTest
specifier|public
specifier|abstract
class|class
name|AbstractChangeNotesTest
extends|extends
name|GerritBaseTests
block|{
annotation|@
name|ConfigSuite
operator|.
name|Default
DECL|method|changeNotesLegacy ()
specifier|public
specifier|static
name|Config
name|changeNotesLegacy
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
name|setBoolean
argument_list|(
literal|"notedb"
argument_list|,
literal|null
argument_list|,
literal|"writeJson"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
name|cfg
return|;
block|}
annotation|@
name|ConfigSuite
operator|.
name|Config
DECL|method|changeNotesJson ()
specifier|public
specifier|static
name|Config
name|changeNotesJson
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
name|setBoolean
argument_list|(
literal|"notedb"
argument_list|,
literal|null
argument_list|,
literal|"writeJson"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|cfg
return|;
block|}
annotation|@
name|ConfigSuite
operator|.
name|Parameter
DECL|field|testConfig
specifier|public
name|Config
name|testConfig
decl_stmt|;
DECL|field|TZ
specifier|private
specifier|static
specifier|final
name|TimeZone
name|TZ
init|=
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"America/Los_Angeles"
argument_list|)
decl_stmt|;
DECL|field|MIGRATION
specifier|private
specifier|static
specifier|final
name|NotesMigration
name|MIGRATION
init|=
operator|new
name|TestNotesMigration
argument_list|()
operator|.
name|setAllEnabled
argument_list|(
literal|true
argument_list|)
decl_stmt|;
DECL|field|otherUserId
specifier|protected
name|Account
operator|.
name|Id
name|otherUserId
decl_stmt|;
DECL|field|accountCache
specifier|protected
name|FakeAccountCache
name|accountCache
decl_stmt|;
DECL|field|changeOwner
specifier|protected
name|IdentifiedUser
name|changeOwner
decl_stmt|;
DECL|field|otherUser
specifier|protected
name|IdentifiedUser
name|otherUser
decl_stmt|;
DECL|field|repo
specifier|protected
name|InMemoryRepository
name|repo
decl_stmt|;
DECL|field|repoManager
specifier|protected
name|InMemoryRepositoryManager
name|repoManager
decl_stmt|;
DECL|field|serverIdent
specifier|protected
name|PersonIdent
name|serverIdent
decl_stmt|;
DECL|field|internalUser
specifier|protected
name|InternalUser
name|internalUser
decl_stmt|;
DECL|field|project
specifier|protected
name|Project
operator|.
name|NameKey
name|project
decl_stmt|;
DECL|field|rw
specifier|protected
name|RevWalk
name|rw
decl_stmt|;
DECL|field|tr
specifier|protected
name|TestRepository
argument_list|<
name|InMemoryRepository
argument_list|>
name|tr
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
DECL|field|updateManagerFactory
specifier|protected
name|NoteDbUpdateManager
operator|.
name|Factory
name|updateManagerFactory
decl_stmt|;
annotation|@
name|Inject
DECL|field|allUsers
specifier|protected
name|AllUsersName
name|allUsers
decl_stmt|;
annotation|@
name|Inject
DECL|field|args
specifier|protected
name|AbstractChangeNotes
operator|.
name|Args
name|args
decl_stmt|;
annotation|@
name|Inject
annotation|@
name|GerritServerId
DECL|field|serverId
specifier|private
name|String
name|serverId
decl_stmt|;
DECL|field|injector
specifier|protected
name|Injector
name|injector
decl_stmt|;
DECL|field|systemTimeZone
specifier|private
name|String
name|systemTimeZone
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
name|setTimeForTesting
argument_list|()
expr_stmt|;
name|serverIdent
operator|=
operator|new
name|PersonIdent
argument_list|(
literal|"Gerrit Server"
argument_list|,
literal|"noreply@gerrit.com"
argument_list|,
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|,
name|TZ
argument_list|)
expr_stmt|;
name|project
operator|=
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"test-project"
argument_list|)
expr_stmt|;
name|repoManager
operator|=
operator|new
name|InMemoryRepositoryManager
argument_list|()
expr_stmt|;
name|repo
operator|=
name|repoManager
operator|.
name|createRepository
argument_list|(
name|project
argument_list|)
expr_stmt|;
name|tr
operator|=
operator|new
name|TestRepository
argument_list|<>
argument_list|(
name|repo
argument_list|)
expr_stmt|;
name|rw
operator|=
name|tr
operator|.
name|getRevWalk
argument_list|()
expr_stmt|;
name|accountCache
operator|=
operator|new
name|FakeAccountCache
argument_list|()
expr_stmt|;
name|Account
name|co
init|=
operator|new
name|Account
argument_list|(
operator|new
name|Account
operator|.
name|Id
argument_list|(
literal|1
argument_list|)
argument_list|,
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
decl_stmt|;
name|co
operator|.
name|setFullName
argument_list|(
literal|"Change Owner"
argument_list|)
expr_stmt|;
name|co
operator|.
name|setPreferredEmail
argument_list|(
literal|"change@owner.com"
argument_list|)
expr_stmt|;
name|accountCache
operator|.
name|put
argument_list|(
name|co
argument_list|)
expr_stmt|;
name|Account
name|ou
init|=
operator|new
name|Account
argument_list|(
operator|new
name|Account
operator|.
name|Id
argument_list|(
literal|2
argument_list|)
argument_list|,
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
decl_stmt|;
name|ou
operator|.
name|setFullName
argument_list|(
literal|"Other Account"
argument_list|)
expr_stmt|;
name|ou
operator|.
name|setPreferredEmail
argument_list|(
literal|"other@account.com"
argument_list|)
expr_stmt|;
name|accountCache
operator|.
name|put
argument_list|(
name|ou
argument_list|)
expr_stmt|;
name|injector
operator|=
name|Guice
operator|.
name|createInjector
argument_list|(
operator|new
name|FactoryModule
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|configure
parameter_list|()
block|{
name|install
argument_list|(
operator|new
name|GitModule
argument_list|()
argument_list|)
expr_stmt|;
name|install
argument_list|(
name|NoteDbModule
operator|.
name|forTest
argument_list|(
name|testConfig
argument_list|)
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|AllUsersName
operator|.
name|class
argument_list|)
operator|.
name|toProvider
argument_list|(
name|AllUsersNameProvider
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|String
operator|.
name|class
argument_list|)
operator|.
name|annotatedWith
argument_list|(
name|GerritServerId
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
literal|"gerrit"
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|NotesMigration
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|MIGRATION
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|GitRepositoryManager
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|repoManager
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|ProjectCache
operator|.
name|class
argument_list|)
operator|.
name|toProvider
argument_list|(
name|Providers
operator|.
expr|<
name|ProjectCache
operator|>
name|of
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|CapabilityControl
operator|.
name|Factory
operator|.
name|class
argument_list|)
operator|.
name|toProvider
argument_list|(
name|Providers
operator|.
expr|<
name|CapabilityControl
operator|.
name|Factory
operator|>
name|of
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|Config
operator|.
name|class
argument_list|)
operator|.
name|annotatedWith
argument_list|(
name|GerritServerConfig
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|testConfig
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|String
operator|.
name|class
argument_list|)
operator|.
name|annotatedWith
argument_list|(
name|AnonymousCowardName
operator|.
name|class
argument_list|)
operator|.
name|toProvider
argument_list|(
name|AnonymousCowardNameProvider
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|String
operator|.
name|class
argument_list|)
operator|.
name|annotatedWith
argument_list|(
name|CanonicalWebUrl
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
literal|"http://localhost:8080/"
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|Boolean
operator|.
name|class
argument_list|)
operator|.
name|annotatedWith
argument_list|(
name|DisableReverseDnsLookup
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|Realm
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|FakeRealm
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|GroupBackend
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|SystemGroupBackend
operator|.
name|class
argument_list|)
operator|.
name|in
argument_list|(
name|SINGLETON
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|AccountCache
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|accountCache
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|PersonIdent
operator|.
name|class
argument_list|)
operator|.
name|annotatedWith
argument_list|(
name|GerritPersonIdent
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|serverIdent
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|GitReferenceUpdated
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|GitReferenceUpdated
operator|.
name|DISABLED
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|MetricMaker
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|DisabledMetricMaker
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|ReviewDb
operator|.
name|class
argument_list|)
operator|.
name|toProvider
argument_list|(
name|Providers
operator|.
expr|<
name|ReviewDb
operator|>
name|of
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|injector
operator|.
name|injectMembers
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|repoManager
operator|.
name|createRepository
argument_list|(
name|allUsers
argument_list|)
expr_stmt|;
name|changeOwner
operator|=
name|userFactory
operator|.
name|create
argument_list|(
name|co
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|otherUser
operator|=
name|userFactory
operator|.
name|create
argument_list|(
name|ou
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|otherUserId
operator|=
name|otherUser
operator|.
name|getAccountId
argument_list|()
expr_stmt|;
name|internalUser
operator|=
operator|new
name|InternalUser
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|setTimeForTesting ()
specifier|private
name|void
name|setTimeForTesting
parameter_list|()
block|{
name|systemTimeZone
operator|=
name|System
operator|.
name|setProperty
argument_list|(
literal|"user.timezone"
argument_list|,
literal|"US/Eastern"
argument_list|)
expr_stmt|;
name|TestTimeUtil
operator|.
name|resetWithClockStep
argument_list|(
literal|1
argument_list|,
name|SECONDS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|resetTime ()
specifier|public
name|void
name|resetTime
parameter_list|()
block|{
name|TestTimeUtil
operator|.
name|useSystemTime
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"user.timezone"
argument_list|,
name|systemTimeZone
argument_list|)
expr_stmt|;
block|}
DECL|method|newChange ()
specifier|protected
name|Change
name|newChange
parameter_list|()
throws|throws
name|Exception
block|{
name|Change
name|c
init|=
name|TestChanges
operator|.
name|newChange
argument_list|(
name|project
argument_list|,
name|changeOwner
operator|.
name|getAccountId
argument_list|()
argument_list|)
decl_stmt|;
name|ChangeUpdate
name|u
init|=
name|newUpdate
argument_list|(
name|c
argument_list|,
name|changeOwner
argument_list|)
decl_stmt|;
name|u
operator|.
name|setChangeId
argument_list|(
name|c
operator|.
name|getKey
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|u
operator|.
name|setBranch
argument_list|(
name|c
operator|.
name|getDest
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|u
operator|.
name|commit
argument_list|()
expr_stmt|;
return|return
name|c
return|;
block|}
DECL|method|newUpdate (Change c, CurrentUser user)
specifier|protected
name|ChangeUpdate
name|newUpdate
parameter_list|(
name|Change
name|c
parameter_list|,
name|CurrentUser
name|user
parameter_list|)
throws|throws
name|Exception
block|{
name|ChangeUpdate
name|update
init|=
name|TestChanges
operator|.
name|newUpdate
argument_list|(
name|injector
argument_list|,
name|c
argument_list|,
name|user
argument_list|)
decl_stmt|;
name|update
operator|.
name|setPatchSetId
argument_list|(
name|c
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
expr_stmt|;
name|update
operator|.
name|setAllowWriteToNewRef
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|update
return|;
block|}
DECL|method|newNotes (Change c)
specifier|protected
name|ChangeNotes
name|newNotes
parameter_list|(
name|Change
name|c
parameter_list|)
throws|throws
name|OrmException
block|{
return|return
operator|new
name|ChangeNotes
argument_list|(
name|args
argument_list|,
name|c
argument_list|)
operator|.
name|load
argument_list|()
return|;
block|}
DECL|method|submitRecord (String status, String errorMessage, SubmitRecord.Label... labels)
specifier|protected
specifier|static
name|SubmitRecord
name|submitRecord
parameter_list|(
name|String
name|status
parameter_list|,
name|String
name|errorMessage
parameter_list|,
name|SubmitRecord
operator|.
name|Label
modifier|...
name|labels
parameter_list|)
block|{
name|SubmitRecord
name|rec
init|=
operator|new
name|SubmitRecord
argument_list|()
decl_stmt|;
name|rec
operator|.
name|status
operator|=
name|SubmitRecord
operator|.
name|Status
operator|.
name|valueOf
argument_list|(
name|status
argument_list|)
expr_stmt|;
name|rec
operator|.
name|errorMessage
operator|=
name|errorMessage
expr_stmt|;
if|if
condition|(
name|labels
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|rec
operator|.
name|labels
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|labels
argument_list|)
expr_stmt|;
block|}
return|return
name|rec
return|;
block|}
DECL|method|submitLabel (String name, String status, Account.Id appliedBy)
specifier|protected
specifier|static
name|SubmitRecord
operator|.
name|Label
name|submitLabel
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|status
parameter_list|,
name|Account
operator|.
name|Id
name|appliedBy
parameter_list|)
block|{
name|SubmitRecord
operator|.
name|Label
name|label
init|=
operator|new
name|SubmitRecord
operator|.
name|Label
argument_list|()
decl_stmt|;
name|label
operator|.
name|label
operator|=
name|name
expr_stmt|;
name|label
operator|.
name|status
operator|=
name|SubmitRecord
operator|.
name|Label
operator|.
name|Status
operator|.
name|valueOf
argument_list|(
name|status
argument_list|)
expr_stmt|;
name|label
operator|.
name|appliedBy
operator|=
name|appliedBy
expr_stmt|;
return|return
name|label
return|;
block|}
DECL|method|newComment (PatchSet.Id psId, String filename, String UUID, CommentRange range, int line, IdentifiedUser commenter, String parentUUID, Timestamp t, String message, short side, String commitSHA1)
specifier|protected
name|Comment
name|newComment
parameter_list|(
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|,
name|String
name|filename
parameter_list|,
name|String
name|UUID
parameter_list|,
name|CommentRange
name|range
parameter_list|,
name|int
name|line
parameter_list|,
name|IdentifiedUser
name|commenter
parameter_list|,
name|String
name|parentUUID
parameter_list|,
name|Timestamp
name|t
parameter_list|,
name|String
name|message
parameter_list|,
name|short
name|side
parameter_list|,
name|String
name|commitSHA1
parameter_list|)
block|{
name|Comment
name|c
init|=
operator|new
name|Comment
argument_list|(
operator|new
name|Comment
operator|.
name|Key
argument_list|(
name|UUID
argument_list|,
name|filename
argument_list|,
name|psId
operator|.
name|get
argument_list|()
argument_list|)
argument_list|,
name|commenter
operator|.
name|getAccountId
argument_list|()
argument_list|,
name|t
argument_list|,
name|side
argument_list|,
name|message
argument_list|,
name|serverId
argument_list|)
decl_stmt|;
name|c
operator|.
name|lineNbr
operator|=
name|line
expr_stmt|;
name|c
operator|.
name|parentUuid
operator|=
name|parentUUID
expr_stmt|;
name|c
operator|.
name|revId
operator|=
name|commitSHA1
expr_stmt|;
name|c
operator|.
name|setRange
argument_list|(
name|range
argument_list|)
expr_stmt|;
return|return
name|c
return|;
block|}
DECL|method|truncate (Timestamp ts)
specifier|protected
specifier|static
name|Timestamp
name|truncate
parameter_list|(
name|Timestamp
name|ts
parameter_list|)
block|{
return|return
operator|new
name|Timestamp
argument_list|(
operator|(
name|ts
operator|.
name|getTime
argument_list|()
operator|/
literal|1000
operator|)
operator|*
literal|1000
argument_list|)
return|;
block|}
DECL|method|after (Change c, long millis)
specifier|protected
specifier|static
name|Timestamp
name|after
parameter_list|(
name|Change
name|c
parameter_list|,
name|long
name|millis
parameter_list|)
block|{
return|return
operator|new
name|Timestamp
argument_list|(
name|c
operator|.
name|getCreatedOn
argument_list|()
operator|.
name|getTime
argument_list|()
operator|+
name|millis
argument_list|)
return|;
block|}
block|}
end_class

end_unit

