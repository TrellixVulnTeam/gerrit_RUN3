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
DECL|package|com.google.gerrit.server.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|change
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
name|anyBoolean
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
name|anyObject
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
name|createMock
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
name|expectLastCall
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
name|common
operator|.
name|changes
operator|.
name|ListChangesOption
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
name|Branch
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
name|ChangeMessage
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
name|PatchSetApproval
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
name|ChangeAccess
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
name|ChangeMessageAccess
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
name|PatchSetAccess
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
name|PatchSetApprovalAccess
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
name|account
operator|.
name|AccountByEmailCache
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
name|change
operator|.
name|ChangeJson
operator|.
name|ChangeInfo
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
name|change
operator|.
name|ChangeJson
operator|.
name|ChangeMessageInfo
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
name|patch
operator|.
name|PatchListCache
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
name|query
operator|.
name|change
operator|.
name|ChangeData
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
name|ListResultSet
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
name|gwtorm
operator|.
name|server
operator|.
name|ResultSet
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
name|Binder
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
name|Module
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|easymock
operator|.
name|EasyMock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|easymock
operator|.
name|IAnswer
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
name|Collections
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
DECL|class|ChangeJsonTest
specifier|public
class|class
name|ChangeJsonTest
extends|extends
name|TestCase
block|{
DECL|method|testFormatChangeMessages ()
specifier|public
name|void
name|testFormatChangeMessages
parameter_list|()
throws|throws
name|OrmException
block|{
comment|// create mocks
specifier|final
name|CurrentUser
name|currentUser
init|=
name|createMock
argument_list|(
name|CurrentUser
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|GitRepositoryManager
name|grm
init|=
name|createMock
argument_list|(
name|GitRepositoryManager
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|AccountByEmailCache
name|abec
init|=
name|createMock
argument_list|(
name|AccountByEmailCache
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|AccountCache
name|ac
init|=
name|createMock
argument_list|(
name|AccountCache
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|AccountInfo
operator|.
name|Loader
operator|.
name|Factory
name|alf
init|=
name|createMock
argument_list|(
name|AccountInfo
operator|.
name|Loader
operator|.
name|Factory
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|CapabilityControl
operator|.
name|Factory
name|ccf
init|=
name|createMock
argument_list|(
name|CapabilityControl
operator|.
name|Factory
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|GroupBackend
name|gb
init|=
name|createMock
argument_list|(
name|GroupBackend
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|Realm
name|r
init|=
name|createMock
argument_list|(
name|Realm
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|PatchListCache
name|plc
init|=
name|createMock
argument_list|(
name|PatchListCache
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|ProjectCache
name|pc
init|=
name|createMock
argument_list|(
name|ProjectCache
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|Config
name|config
init|=
operator|new
name|Config
argument_list|()
decl_stmt|;
comment|// unable to mock
specifier|final
name|ReviewDb
name|rdb
init|=
name|createMock
argument_list|(
name|ReviewDb
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|ChangeAccess
name|ca
init|=
name|createMock
argument_list|(
name|ChangeAccess
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|PatchSetAccess
name|psa
init|=
name|createMock
argument_list|(
name|PatchSetAccess
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|PatchSetApprovalAccess
name|psaa
init|=
name|createMock
argument_list|(
name|PatchSetApprovalAccess
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|ChangeMessageAccess
name|cma
init|=
name|createMock
argument_list|(
name|ChangeMessageAccess
operator|.
name|class
argument_list|)
decl_stmt|;
name|AccountInfo
operator|.
name|Loader
name|accountLoader
init|=
name|createMock
argument_list|(
name|AccountInfo
operator|.
name|Loader
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// create ChangeJson instance
name|Module
name|mod
init|=
operator|new
name|Module
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|configure
parameter_list|(
name|Binder
name|binder
parameter_list|)
block|{
name|binder
operator|.
name|bind
argument_list|(
name|CurrentUser
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|currentUser
argument_list|)
expr_stmt|;
name|binder
operator|.
name|bind
argument_list|(
name|GitRepositoryManager
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|grm
argument_list|)
expr_stmt|;
name|binder
operator|.
name|bind
argument_list|(
name|AccountByEmailCache
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|abec
argument_list|)
expr_stmt|;
name|binder
operator|.
name|bind
argument_list|(
name|AccountCache
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|ac
argument_list|)
expr_stmt|;
name|binder
operator|.
name|bind
argument_list|(
name|AccountInfo
operator|.
name|Loader
operator|.
name|Factory
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|alf
argument_list|)
expr_stmt|;
name|binder
operator|.
name|bind
argument_list|(
name|CapabilityControl
operator|.
name|Factory
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|ccf
argument_list|)
expr_stmt|;
name|binder
operator|.
name|bind
argument_list|(
name|GroupBackend
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|gb
argument_list|)
expr_stmt|;
name|binder
operator|.
name|bind
argument_list|(
name|Realm
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|binder
operator|.
name|bind
argument_list|(
name|PatchListCache
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|plc
argument_list|)
expr_stmt|;
name|binder
operator|.
name|bind
argument_list|(
name|ProjectCache
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|pc
argument_list|)
expr_stmt|;
name|binder
operator|.
name|bind
argument_list|(
name|ReviewDb
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|rdb
argument_list|)
expr_stmt|;
name|binder
operator|.
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
name|config
argument_list|)
expr_stmt|;
name|binder
operator|.
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
literal|""
argument_list|)
expr_stmt|;
name|binder
operator|.
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
name|toInstance
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|ChangeJson
name|json
init|=
name|Guice
operator|.
name|createInjector
argument_list|(
name|mod
argument_list|)
operator|.
name|getInstance
argument_list|(
name|ChangeJson
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// define mock behavior for tests
name|expect
argument_list|(
name|alf
operator|.
name|create
argument_list|(
name|anyBoolean
argument_list|()
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
name|accountLoader
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|Project
operator|.
name|NameKey
name|proj
init|=
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"ProjectNameKey"
argument_list|)
decl_stmt|;
name|Branch
operator|.
name|NameKey
name|forBranch
init|=
operator|new
name|Branch
operator|.
name|NameKey
argument_list|(
name|proj
argument_list|,
literal|"BranchNameKey"
argument_list|)
decl_stmt|;
name|Change
operator|.
name|Key
name|changeKey123
init|=
operator|new
name|Change
operator|.
name|Key
argument_list|(
literal|"ChangeKey123"
argument_list|)
decl_stmt|;
name|Change
operator|.
name|Id
name|changeId123
init|=
operator|new
name|Change
operator|.
name|Id
argument_list|(
literal|123
argument_list|)
decl_stmt|;
name|Change
name|change123
init|=
operator|new
name|Change
argument_list|(
name|changeKey123
argument_list|,
name|changeId123
argument_list|,
literal|null
argument_list|,
name|forBranch
argument_list|)
decl_stmt|;
name|Change
operator|.
name|Key
name|changeKey234
init|=
operator|new
name|Change
operator|.
name|Key
argument_list|(
literal|"ChangeKey234"
argument_list|)
decl_stmt|;
name|Change
operator|.
name|Id
name|changeId234
init|=
operator|new
name|Change
operator|.
name|Id
argument_list|(
literal|234
argument_list|)
decl_stmt|;
name|Change
name|change234
init|=
operator|new
name|Change
argument_list|(
name|changeKey234
argument_list|,
name|changeId234
argument_list|,
literal|null
argument_list|,
name|forBranch
argument_list|)
decl_stmt|;
name|expect
argument_list|(
name|ca
operator|.
name|get
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
name|changeId123
argument_list|)
argument_list|)
argument_list|)
operator|.
name|andAnswer
argument_list|(
name|results
argument_list|(
name|Change
operator|.
name|class
argument_list|,
name|change123
argument_list|)
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|expect
argument_list|(
name|ca
operator|.
name|get
argument_list|(
name|changeId123
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
name|change123
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|expect
argument_list|(
name|ca
operator|.
name|get
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
name|changeId234
argument_list|)
argument_list|)
argument_list|)
operator|.
name|andAnswer
argument_list|(
name|results
argument_list|(
name|Change
operator|.
name|class
argument_list|,
name|change234
argument_list|)
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|ca
operator|.
name|get
argument_list|(
name|changeId234
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
name|change234
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|rdb
operator|.
name|changes
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|ca
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|expect
argument_list|(
name|psa
operator|.
name|get
argument_list|(
name|EasyMock
operator|.
expr|<
name|Iterable
argument_list|<
name|PatchSet
operator|.
name|Id
argument_list|>
operator|>
name|anyObject
argument_list|()
argument_list|)
argument_list|)
operator|.
name|andAnswer
argument_list|(
name|results
argument_list|(
name|PatchSet
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|expect
argument_list|(
name|rdb
operator|.
name|patchSets
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|psa
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|expect
argument_list|(
name|psaa
operator|.
name|byPatchSet
argument_list|(
name|anyObject
argument_list|(
name|PatchSet
operator|.
name|Id
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|andAnswer
argument_list|(
name|results
argument_list|(
name|PatchSetApproval
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|expect
argument_list|(
name|rdb
operator|.
name|patchSetApprovals
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|psaa
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|expect
argument_list|(
name|currentUser
operator|.
name|getStarredChanges
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|Collections
operator|.
expr|<
name|Change
operator|.
name|Id
operator|>
name|emptySet
argument_list|()
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|long
name|timeBase
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|ChangeMessage
name|changeMessage1
init|=
name|changeMessage
argument_list|(
name|changeId123
argument_list|,
literal|"cm1"
argument_list|,
literal|111
argument_list|,
name|timeBase
argument_list|,
literal|1111
argument_list|,
literal|"first message"
argument_list|)
decl_stmt|;
name|ChangeMessage
name|changeMessage2
init|=
name|changeMessage
argument_list|(
name|changeId123
argument_list|,
literal|"cm2"
argument_list|,
literal|222
argument_list|,
name|timeBase
operator|+
literal|1000
argument_list|,
literal|1111
argument_list|,
literal|"second message"
argument_list|)
decl_stmt|;
name|expect
argument_list|(
name|cma
operator|.
name|byChange
argument_list|(
name|changeId123
argument_list|)
argument_list|)
operator|.
name|andAnswer
argument_list|(
name|results
argument_list|(
name|ChangeMessage
operator|.
name|class
argument_list|,
name|changeMessage2
argument_list|,
name|changeMessage1
argument_list|)
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|expect
argument_list|(
name|cma
operator|.
name|byChange
argument_list|(
name|changeId234
argument_list|)
argument_list|)
operator|.
name|andAnswer
argument_list|(
name|results
argument_list|(
name|ChangeMessage
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|rdb
operator|.
name|changeMessages
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|cma
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|expect
argument_list|(
name|accountLoader
operator|.
name|get
argument_list|(
name|anyObject
argument_list|(
name|Account
operator|.
name|Id
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|andAnswer
argument_list|(
name|accountForId
argument_list|()
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|accountLoader
operator|.
name|fill
argument_list|()
expr_stmt|;
name|expectLastCall
argument_list|()
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|replay
argument_list|(
name|rdb
argument_list|,
name|ca
argument_list|,
name|psa
argument_list|,
name|psaa
argument_list|,
name|alf
argument_list|,
name|currentUser
argument_list|,
name|cma
argument_list|,
name|accountLoader
argument_list|)
expr_stmt|;
comment|// test 1: messages not returned by default
name|ChangeInfo
name|ci
init|=
name|json
operator|.
name|format
argument_list|(
operator|new
name|ChangeData
argument_list|(
name|changeId123
argument_list|)
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|ci
operator|.
name|messages
argument_list|)
expr_stmt|;
name|json
operator|.
name|addOption
argument_list|(
name|ListChangesOption
operator|.
name|MESSAGES
argument_list|)
expr_stmt|;
comment|// test 2: two change messages, in chronological order
name|ci
operator|=
name|json
operator|.
name|format
argument_list|(
operator|new
name|ChangeData
argument_list|(
name|changeId123
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|ci
operator|.
name|messages
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|ci
operator|.
name|messages
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|ChangeMessageInfo
argument_list|>
name|cmis
init|=
name|ci
operator|.
name|messages
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|changeMessage1
argument_list|,
name|cmis
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|changeMessage2
argument_list|,
name|cmis
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
comment|// test 3: no change messages
name|ci
operator|=
name|json
operator|.
name|format
argument_list|(
operator|new
name|ChangeData
argument_list|(
name|changeId234
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|ci
operator|.
name|messages
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ci
operator|.
name|messages
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|accountForId ()
specifier|private
specifier|static
name|IAnswer
argument_list|<
name|AccountInfo
argument_list|>
name|accountForId
parameter_list|()
block|{
return|return
operator|new
name|IAnswer
argument_list|<
name|AccountInfo
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|AccountInfo
name|answer
parameter_list|()
throws|throws
name|Throwable
block|{
name|Account
operator|.
name|Id
name|id
init|=
operator|(
name|Account
operator|.
name|Id
operator|)
name|EasyMock
operator|.
name|getCurrentArguments
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|AccountInfo
name|ai
init|=
operator|new
name|AccountInfo
argument_list|(
name|id
argument_list|)
decl_stmt|;
return|return
name|ai
return|;
block|}
block|}
return|;
block|}
DECL|method|results (Class<T> type, T... items)
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|IAnswer
argument_list|<
name|ResultSet
argument_list|<
name|T
argument_list|>
argument_list|>
name|results
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|,
name|T
modifier|...
name|items
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|T
argument_list|>
name|list
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|items
argument_list|)
decl_stmt|;
return|return
operator|new
name|IAnswer
argument_list|<
name|ResultSet
argument_list|<
name|T
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ResultSet
argument_list|<
name|T
argument_list|>
name|answer
parameter_list|()
throws|throws
name|Throwable
block|{
return|return
operator|new
name|ListResultSet
argument_list|<
name|T
argument_list|>
argument_list|(
name|list
argument_list|)
return|;
block|}
block|}
return|;
block|}
DECL|method|assertEquals (ChangeMessage cm, ChangeMessageInfo cmi)
specifier|private
specifier|static
name|void
name|assertEquals
parameter_list|(
name|ChangeMessage
name|cm
parameter_list|,
name|ChangeMessageInfo
name|cmi
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|cm
operator|.
name|getPatchSetId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
operator|(
name|int
operator|)
name|cmi
operator|.
name|_revisionNumber
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cm
operator|.
name|getMessage
argument_list|()
argument_list|,
name|cmi
operator|.
name|message
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cm
operator|.
name|getKey
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|cmi
operator|.
name|id
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cm
operator|.
name|getWrittenOn
argument_list|()
argument_list|,
name|cmi
operator|.
name|date
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|cmi
operator|.
name|author
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cm
operator|.
name|getAuthor
argument_list|()
argument_list|,
name|cmi
operator|.
name|author
operator|.
name|_id
argument_list|)
expr_stmt|;
block|}
DECL|method|changeMessage (Change.Id changeId, String uuid, int accountId, long time, int psId, String message)
specifier|private
specifier|static
name|ChangeMessage
name|changeMessage
parameter_list|(
name|Change
operator|.
name|Id
name|changeId
parameter_list|,
name|String
name|uuid
parameter_list|,
name|int
name|accountId
parameter_list|,
name|long
name|time
parameter_list|,
name|int
name|psId
parameter_list|,
name|String
name|message
parameter_list|)
block|{
name|ChangeMessage
operator|.
name|Key
name|key
init|=
operator|new
name|ChangeMessage
operator|.
name|Key
argument_list|(
name|changeId
argument_list|,
name|uuid
argument_list|)
decl_stmt|;
name|Account
operator|.
name|Id
name|author
init|=
operator|new
name|Account
operator|.
name|Id
argument_list|(
name|accountId
argument_list|)
decl_stmt|;
name|Timestamp
name|updated
init|=
operator|new
name|Timestamp
argument_list|(
name|time
argument_list|)
decl_stmt|;
name|PatchSet
operator|.
name|Id
name|ps
init|=
operator|new
name|PatchSet
operator|.
name|Id
argument_list|(
name|changeId
argument_list|,
name|psId
argument_list|)
decl_stmt|;
name|ChangeMessage
name|changeMessage
init|=
operator|new
name|ChangeMessage
argument_list|(
name|key
argument_list|,
name|author
argument_list|,
name|updated
argument_list|,
name|ps
argument_list|)
decl_stmt|;
name|changeMessage
operator|.
name|setMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
return|return
name|changeMessage
return|;
block|}
block|}
end_class

end_unit

