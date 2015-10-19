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
DECL|package|com.google.gerrit.testutil
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|testutil
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
name|expect
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
name|Ordering
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
name|PatchSetInfo
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
name|RevId
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
name|notedb
operator|.
name|ChangeDraftUpdate
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
name|ChangeNotes
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
name|ChangeUpdate
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
name|NotesMigration
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
name|ChangeControl
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
name|Injector
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_comment
comment|/**  * Utility functions to create and manipulate Change, ChangeUpdate, and  * ChangeControl objects for testing.  */
end_comment

begin_class
DECL|class|TestChanges
specifier|public
class|class
name|TestChanges
block|{
DECL|field|nextChangeId
specifier|private
specifier|static
specifier|final
name|AtomicInteger
name|nextChangeId
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|method|newChange (Project.NameKey project, Account.Id userId)
specifier|public
specifier|static
name|Change
name|newChange
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
name|Account
operator|.
name|Id
name|userId
parameter_list|)
block|{
return|return
name|newChange
argument_list|(
name|project
argument_list|,
name|userId
argument_list|,
name|nextChangeId
operator|.
name|getAndIncrement
argument_list|()
argument_list|)
return|;
block|}
DECL|method|newChange (Project.NameKey project, Account.Id userId, int id)
specifier|public
specifier|static
name|Change
name|newChange
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
name|Account
operator|.
name|Id
name|userId
parameter_list|,
name|int
name|id
parameter_list|)
block|{
name|Change
operator|.
name|Id
name|changeId
init|=
operator|new
name|Change
operator|.
name|Id
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|Change
name|c
init|=
operator|new
name|Change
argument_list|(
operator|new
name|Change
operator|.
name|Key
argument_list|(
literal|"Iabcd1234abcd1234abcd1234abcd1234abcd1234"
argument_list|)
argument_list|,
name|changeId
argument_list|,
name|userId
argument_list|,
operator|new
name|Branch
operator|.
name|NameKey
argument_list|(
name|project
argument_list|,
literal|"master"
argument_list|)
argument_list|,
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
decl_stmt|;
name|incrementPatchSet
argument_list|(
name|c
argument_list|)
expr_stmt|;
return|return
name|c
return|;
block|}
DECL|method|newPatchSet (PatchSet.Id id, ObjectId revision, Account.Id userId)
specifier|public
specifier|static
name|PatchSet
name|newPatchSet
parameter_list|(
name|PatchSet
operator|.
name|Id
name|id
parameter_list|,
name|ObjectId
name|revision
parameter_list|,
name|Account
operator|.
name|Id
name|userId
parameter_list|)
block|{
return|return
name|newPatchSet
argument_list|(
name|id
argument_list|,
name|revision
operator|.
name|name
argument_list|()
argument_list|,
name|userId
argument_list|)
return|;
block|}
DECL|method|newPatchSet (PatchSet.Id id, String revision, Account.Id userId)
specifier|public
specifier|static
name|PatchSet
name|newPatchSet
parameter_list|(
name|PatchSet
operator|.
name|Id
name|id
parameter_list|,
name|String
name|revision
parameter_list|,
name|Account
operator|.
name|Id
name|userId
parameter_list|)
block|{
name|PatchSet
name|ps
init|=
operator|new
name|PatchSet
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|ps
operator|.
name|setRevision
argument_list|(
operator|new
name|RevId
argument_list|(
name|revision
argument_list|)
argument_list|)
expr_stmt|;
name|ps
operator|.
name|setUploader
argument_list|(
name|userId
argument_list|)
expr_stmt|;
name|ps
operator|.
name|setCreatedOn
argument_list|(
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|ps
return|;
block|}
DECL|method|newUpdate (Injector injector, GitRepositoryManager repoManager, NotesMigration migration, Change c, final AllUsersNameProvider allUsers, final IdentifiedUser user)
specifier|public
specifier|static
name|ChangeUpdate
name|newUpdate
parameter_list|(
name|Injector
name|injector
parameter_list|,
name|GitRepositoryManager
name|repoManager
parameter_list|,
name|NotesMigration
name|migration
parameter_list|,
name|Change
name|c
parameter_list|,
specifier|final
name|AllUsersNameProvider
name|allUsers
parameter_list|,
specifier|final
name|IdentifiedUser
name|user
parameter_list|)
throws|throws
name|OrmException
block|{
return|return
name|injector
operator|.
name|createChildInjector
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
name|factory
argument_list|(
name|ChangeUpdate
operator|.
name|Factory
operator|.
name|class
argument_list|)
expr_stmt|;
name|factory
argument_list|(
name|ChangeDraftUpdate
operator|.
name|Factory
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|IdentifiedUser
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|user
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
name|allUsers
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
operator|.
name|getInstance
argument_list|(
name|ChangeUpdate
operator|.
name|Factory
operator|.
name|class
argument_list|)
operator|.
name|create
argument_list|(
name|stubChangeControl
argument_list|(
name|repoManager
argument_list|,
name|migration
argument_list|,
name|c
argument_list|,
name|allUsers
argument_list|,
name|user
argument_list|)
argument_list|,
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|,
name|Ordering
operator|.
expr|<
name|String
operator|>
name|natural
argument_list|()
argument_list|)
return|;
block|}
DECL|method|stubChangeControl ( GitRepositoryManager repoManager, NotesMigration migration, Change c, AllUsersNameProvider allUsers, IdentifiedUser user)
specifier|public
specifier|static
name|ChangeControl
name|stubChangeControl
parameter_list|(
name|GitRepositoryManager
name|repoManager
parameter_list|,
name|NotesMigration
name|migration
parameter_list|,
name|Change
name|c
parameter_list|,
name|AllUsersNameProvider
name|allUsers
parameter_list|,
name|IdentifiedUser
name|user
parameter_list|)
throws|throws
name|OrmException
block|{
name|ChangeControl
name|ctl
init|=
name|EasyMock
operator|.
name|createNiceMock
argument_list|(
name|ChangeControl
operator|.
name|class
argument_list|)
decl_stmt|;
name|expect
argument_list|(
name|ctl
operator|.
name|getChange
argument_list|()
argument_list|)
operator|.
name|andStubReturn
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|ctl
operator|.
name|getUser
argument_list|()
argument_list|)
operator|.
name|andStubReturn
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|ChangeNotes
name|notes
init|=
operator|new
name|ChangeNotes
argument_list|(
name|repoManager
argument_list|,
name|migration
argument_list|,
name|allUsers
argument_list|,
name|c
argument_list|)
operator|.
name|load
argument_list|()
decl_stmt|;
name|expect
argument_list|(
name|ctl
operator|.
name|getNotes
argument_list|()
argument_list|)
operator|.
name|andStubReturn
argument_list|(
name|notes
argument_list|)
expr_stmt|;
name|EasyMock
operator|.
name|replay
argument_list|(
name|ctl
argument_list|)
expr_stmt|;
return|return
name|ctl
return|;
block|}
DECL|method|incrementPatchSet (Change change)
specifier|public
specifier|static
name|void
name|incrementPatchSet
parameter_list|(
name|Change
name|change
parameter_list|)
block|{
name|PatchSet
operator|.
name|Id
name|curr
init|=
name|change
operator|.
name|currentPatchSetId
argument_list|()
decl_stmt|;
name|PatchSetInfo
name|ps
init|=
operator|new
name|PatchSetInfo
argument_list|(
operator|new
name|PatchSet
operator|.
name|Id
argument_list|(
name|change
operator|.
name|getId
argument_list|()
argument_list|,
name|curr
operator|!=
literal|null
condition|?
name|curr
operator|.
name|get
argument_list|()
operator|+
literal|1
else|:
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|ps
operator|.
name|setSubject
argument_list|(
literal|"Change subject"
argument_list|)
expr_stmt|;
name|change
operator|.
name|setCurrentPatchSet
argument_list|(
name|ps
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

