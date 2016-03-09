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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|MoreObjects
operator|.
name|firstNonNull
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
name|ChangeNoteUtil
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
name|CommentsInNotesUtil
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
name|lib
operator|.
name|Ref
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
name|Repository
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
DECL|method|newUpdate (Injector injector, GitRepositoryManager repoManager, NotesMigration migration, Change c, final AllUsersName allUsers, final CurrentUser user)
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
name|AllUsersName
name|allUsers
parameter_list|,
specifier|final
name|CurrentUser
name|user
parameter_list|)
throws|throws
name|Exception
block|{
name|injector
operator|=
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
name|CurrentUser
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|user
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|ChangeUpdate
name|update
init|=
name|injector
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
name|injector
operator|.
name|getInstance
argument_list|(
name|ChangeNoteUtil
operator|.
name|class
argument_list|)
argument_list|,
name|injector
operator|.
name|getInstance
argument_list|(
name|CommentsInNotesUtil
operator|.
name|class
argument_list|)
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
decl_stmt|;
name|ChangeNotes
name|notes
init|=
name|update
operator|.
name|getChangeNotes
argument_list|()
decl_stmt|;
name|boolean
name|hasPatchSets
init|=
name|notes
operator|.
name|getPatchSets
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|notes
operator|.
name|getPatchSets
argument_list|()
operator|.
name|isEmpty
argument_list|()
decl_stmt|;
if|if
condition|(
name|hasPatchSets
operator|||
operator|!
name|migration
operator|.
name|readChanges
argument_list|()
condition|)
block|{
return|return
name|update
return|;
block|}
comment|// Change doesn't exist yet. Notedb requires that there be a commit for the
comment|// first patch set, so create one.
try|try
init|(
name|Repository
name|repo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|c
operator|.
name|getProject
argument_list|()
argument_list|)
init|)
block|{
name|TestRepository
argument_list|<
name|Repository
argument_list|>
name|tr
init|=
operator|new
name|TestRepository
argument_list|<>
argument_list|(
name|repo
argument_list|)
decl_stmt|;
name|PersonIdent
name|ident
init|=
name|user
operator|.
name|asIdentifiedUser
argument_list|()
operator|.
name|newCommitterIdent
argument_list|(
name|update
operator|.
name|getWhen
argument_list|()
argument_list|,
name|TimeZone
operator|.
name|getDefault
argument_list|()
argument_list|)
decl_stmt|;
name|TestRepository
argument_list|<
name|Repository
argument_list|>
operator|.
name|CommitBuilder
name|cb
init|=
name|tr
operator|.
name|commit
argument_list|()
operator|.
name|author
argument_list|(
name|ident
argument_list|)
operator|.
name|committer
argument_list|(
name|ident
argument_list|)
operator|.
name|message
argument_list|(
name|firstNonNull
argument_list|(
name|c
operator|.
name|getSubject
argument_list|()
argument_list|,
literal|"Test change"
argument_list|)
argument_list|)
decl_stmt|;
name|Ref
name|parent
init|=
name|repo
operator|.
name|exactRef
argument_list|(
name|c
operator|.
name|getDest
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|cb
operator|.
name|parent
argument_list|(
name|tr
operator|.
name|getRevWalk
argument_list|()
operator|.
name|parseCommit
argument_list|(
name|parent
operator|.
name|getObjectId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|update
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
name|update
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
name|update
operator|.
name|setCommit
argument_list|(
name|tr
operator|.
name|getRevWalk
argument_list|()
argument_list|,
name|cb
operator|.
name|create
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|update
return|;
block|}
block|}
DECL|method|stubChangeControl ( GitRepositoryManager repoManager, NotesMigration migration, Change c, AllUsersName allUsers, ChangeNoteUtil changeNoteUtil, CommentsInNotesUtil commentsUtil, CurrentUser user)
specifier|private
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
name|AllUsersName
name|allUsers
parameter_list|,
name|ChangeNoteUtil
name|changeNoteUtil
parameter_list|,
name|CommentsInNotesUtil
name|commentsUtil
parameter_list|,
name|CurrentUser
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
name|createMock
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
name|getProject
argument_list|()
argument_list|)
operator|.
name|andStubReturn
argument_list|(
operator|new
name|Project
argument_list|(
name|c
operator|.
name|getProject
argument_list|()
argument_list|)
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
name|changeNoteUtil
argument_list|,
name|commentsUtil
argument_list|,
name|c
operator|.
name|getProject
argument_list|()
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
name|expect
argument_list|(
name|ctl
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|andStubReturn
argument_list|(
name|c
operator|.
name|getId
argument_list|()
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

