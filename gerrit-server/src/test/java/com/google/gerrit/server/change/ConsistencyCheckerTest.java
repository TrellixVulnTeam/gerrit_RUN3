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
name|gerrit
operator|.
name|testutil
operator|.
name|TestChanges
operator|.
name|incrementPatchSet
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|testutil
operator|.
name|TestChanges
operator|.
name|newChange
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|testutil
operator|.
name|TestChanges
operator|.
name|newPatchSet
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singleton
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
name|RefUpdate
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
name|RevCommit
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
name|Test
import|;
end_import

begin_class
DECL|class|ConsistencyCheckerTest
specifier|public
class|class
name|ConsistencyCheckerTest
block|{
DECL|field|schemaFactory
specifier|private
name|InMemoryDatabase
name|schemaFactory
decl_stmt|;
DECL|field|db
specifier|private
name|ReviewDb
name|db
decl_stmt|;
DECL|field|repoManager
specifier|private
name|InMemoryRepositoryManager
name|repoManager
decl_stmt|;
DECL|field|checker
specifier|private
name|ConsistencyChecker
name|checker
decl_stmt|;
DECL|field|repo
specifier|private
name|TestRepository
argument_list|<
name|InMemoryRepository
argument_list|>
name|repo
decl_stmt|;
DECL|field|project
specifier|private
name|Project
operator|.
name|NameKey
name|project
decl_stmt|;
DECL|field|userId
specifier|private
name|Account
operator|.
name|Id
name|userId
decl_stmt|;
DECL|field|tip
specifier|private
name|RevCommit
name|tip
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
name|schemaFactory
operator|=
name|InMemoryDatabase
operator|.
name|newDatabase
argument_list|()
expr_stmt|;
name|schemaFactory
operator|.
name|create
argument_list|()
expr_stmt|;
name|db
operator|=
name|schemaFactory
operator|.
name|open
argument_list|()
expr_stmt|;
name|repoManager
operator|=
operator|new
name|InMemoryRepositoryManager
argument_list|()
expr_stmt|;
name|checker
operator|=
operator|new
name|ConsistencyChecker
argument_list|(
name|Providers
operator|.
expr|<
name|ReviewDb
operator|>
name|of
argument_list|(
name|db
argument_list|)
argument_list|,
name|repoManager
argument_list|)
expr_stmt|;
name|project
operator|=
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"repo"
argument_list|)
expr_stmt|;
name|repo
operator|=
operator|new
name|TestRepository
argument_list|<>
argument_list|(
name|repoManager
operator|.
name|createRepository
argument_list|(
name|project
argument_list|)
argument_list|)
expr_stmt|;
name|userId
operator|=
operator|new
name|Account
operator|.
name|Id
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|db
operator|.
name|accounts
argument_list|()
operator|.
name|insert
argument_list|(
name|singleton
argument_list|(
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
argument_list|)
argument_list|)
expr_stmt|;
name|tip
operator|=
name|repo
operator|.
name|branch
argument_list|(
literal|"master"
argument_list|)
operator|.
name|commit
argument_list|()
operator|.
name|create
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
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
if|if
condition|(
name|schemaFactory
operator|!=
literal|null
condition|)
block|{
name|InMemoryDatabase
operator|.
name|drop
argument_list|(
name|schemaFactory
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|validNewChange ()
specifier|public
name|void
name|validNewChange
parameter_list|()
throws|throws
name|Exception
block|{
name|Change
name|c
init|=
name|newChange
argument_list|(
name|project
argument_list|,
name|userId
argument_list|)
decl_stmt|;
name|db
operator|.
name|changes
argument_list|()
operator|.
name|insert
argument_list|(
name|singleton
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
name|RevCommit
name|commit1
init|=
name|repo
operator|.
name|branch
argument_list|(
name|c
operator|.
name|currentPatchSetId
argument_list|()
operator|.
name|toRefName
argument_list|()
argument_list|)
operator|.
name|commit
argument_list|()
operator|.
name|parent
argument_list|(
name|tip
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|PatchSet
name|ps1
init|=
name|newPatchSet
argument_list|(
name|c
operator|.
name|currentPatchSetId
argument_list|()
argument_list|,
name|commit1
argument_list|,
name|userId
argument_list|)
decl_stmt|;
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|insert
argument_list|(
name|singleton
argument_list|(
name|ps1
argument_list|)
argument_list|)
expr_stmt|;
name|incrementPatchSet
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|RevCommit
name|commit2
init|=
name|repo
operator|.
name|branch
argument_list|(
name|c
operator|.
name|currentPatchSetId
argument_list|()
operator|.
name|toRefName
argument_list|()
argument_list|)
operator|.
name|commit
argument_list|()
operator|.
name|parent
argument_list|(
name|tip
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|PatchSet
name|ps2
init|=
name|newPatchSet
argument_list|(
name|c
operator|.
name|currentPatchSetId
argument_list|()
argument_list|,
name|commit2
argument_list|,
name|userId
argument_list|)
decl_stmt|;
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|insert
argument_list|(
name|singleton
argument_list|(
name|ps2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|checker
operator|.
name|check
argument_list|(
name|c
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|validMergedChange ()
specifier|public
name|void
name|validMergedChange
parameter_list|()
throws|throws
name|Exception
block|{
name|Change
name|c
init|=
name|newChange
argument_list|(
name|project
argument_list|,
name|userId
argument_list|)
decl_stmt|;
name|c
operator|.
name|setStatus
argument_list|(
name|Change
operator|.
name|Status
operator|.
name|MERGED
argument_list|)
expr_stmt|;
name|db
operator|.
name|changes
argument_list|()
operator|.
name|insert
argument_list|(
name|singleton
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
name|RevCommit
name|commit1
init|=
name|repo
operator|.
name|branch
argument_list|(
name|c
operator|.
name|currentPatchSetId
argument_list|()
operator|.
name|toRefName
argument_list|()
argument_list|)
operator|.
name|commit
argument_list|()
operator|.
name|parent
argument_list|(
name|tip
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|PatchSet
name|ps1
init|=
name|newPatchSet
argument_list|(
name|c
operator|.
name|currentPatchSetId
argument_list|()
argument_list|,
name|commit1
argument_list|,
name|userId
argument_list|)
decl_stmt|;
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|insert
argument_list|(
name|singleton
argument_list|(
name|ps1
argument_list|)
argument_list|)
expr_stmt|;
name|incrementPatchSet
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|RevCommit
name|commit2
init|=
name|repo
operator|.
name|branch
argument_list|(
name|c
operator|.
name|currentPatchSetId
argument_list|()
operator|.
name|toRefName
argument_list|()
argument_list|)
operator|.
name|commit
argument_list|()
operator|.
name|parent
argument_list|(
name|tip
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|PatchSet
name|ps2
init|=
name|newPatchSet
argument_list|(
name|c
operator|.
name|currentPatchSetId
argument_list|()
argument_list|,
name|commit2
argument_list|,
name|userId
argument_list|)
decl_stmt|;
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|insert
argument_list|(
name|singleton
argument_list|(
name|ps2
argument_list|)
argument_list|)
expr_stmt|;
name|repo
operator|.
name|branch
argument_list|(
name|c
operator|.
name|getDest
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|update
argument_list|(
name|commit2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|checker
operator|.
name|check
argument_list|(
name|c
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|missingOwner ()
specifier|public
name|void
name|missingOwner
parameter_list|()
throws|throws
name|Exception
block|{
name|Change
name|c
init|=
name|newChange
argument_list|(
name|project
argument_list|,
operator|new
name|Account
operator|.
name|Id
argument_list|(
literal|2
argument_list|)
argument_list|)
decl_stmt|;
name|db
operator|.
name|changes
argument_list|()
operator|.
name|insert
argument_list|(
name|singleton
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
name|RevCommit
name|commit
init|=
name|repo
operator|.
name|branch
argument_list|(
name|c
operator|.
name|currentPatchSetId
argument_list|()
operator|.
name|toRefName
argument_list|()
argument_list|)
operator|.
name|commit
argument_list|()
operator|.
name|parent
argument_list|(
name|tip
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|PatchSet
name|ps
init|=
name|newPatchSet
argument_list|(
name|c
operator|.
name|currentPatchSetId
argument_list|()
argument_list|,
name|commit
argument_list|,
name|userId
argument_list|)
decl_stmt|;
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|insert
argument_list|(
name|singleton
argument_list|(
name|ps
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|checker
operator|.
name|check
argument_list|(
name|c
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|"Missing change owner: 2"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|missingRepo ()
specifier|public
name|void
name|missingRepo
parameter_list|()
throws|throws
name|Exception
block|{
name|Change
name|c
init|=
name|newChange
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"otherproject"
argument_list|)
argument_list|,
name|userId
argument_list|)
decl_stmt|;
name|db
operator|.
name|changes
argument_list|()
operator|.
name|insert
argument_list|(
name|singleton
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
name|PatchSet
name|ps
init|=
name|newPatchSet
argument_list|(
name|c
operator|.
name|currentPatchSetId
argument_list|()
argument_list|,
name|ObjectId
operator|.
name|fromString
argument_list|(
literal|"deadbeefdeadbeefdeadbeefdeadbeefdeadbeef"
argument_list|)
argument_list|,
name|userId
argument_list|)
decl_stmt|;
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|insert
argument_list|(
name|singleton
argument_list|(
name|ps
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|checker
operator|.
name|check
argument_list|(
name|c
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|"Destination repository not found: otherproject"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|invalidRevision ()
specifier|public
name|void
name|invalidRevision
parameter_list|()
throws|throws
name|Exception
block|{
name|Change
name|c
init|=
name|newChange
argument_list|(
name|project
argument_list|,
name|userId
argument_list|)
decl_stmt|;
name|db
operator|.
name|changes
argument_list|()
operator|.
name|insert
argument_list|(
name|singleton
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
name|PatchSet
name|ps
init|=
operator|new
name|PatchSet
argument_list|(
name|c
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
decl_stmt|;
name|ps
operator|.
name|setRevision
argument_list|(
operator|new
name|RevId
argument_list|(
literal|"fooooooooooooooooooooooooooooooooooooooo"
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
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|insert
argument_list|(
name|singleton
argument_list|(
name|ps
argument_list|)
argument_list|)
expr_stmt|;
name|incrementPatchSet
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|RevCommit
name|commit2
init|=
name|repo
operator|.
name|branch
argument_list|(
name|c
operator|.
name|currentPatchSetId
argument_list|()
operator|.
name|toRefName
argument_list|()
argument_list|)
operator|.
name|commit
argument_list|()
operator|.
name|parent
argument_list|(
name|tip
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|PatchSet
name|ps2
init|=
name|newPatchSet
argument_list|(
name|c
operator|.
name|currentPatchSetId
argument_list|()
argument_list|,
name|commit2
argument_list|,
name|userId
argument_list|)
decl_stmt|;
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|insert
argument_list|(
name|singleton
argument_list|(
name|ps2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|checker
operator|.
name|check
argument_list|(
name|c
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|"Invalid revision on patch set 1:"
operator|+
literal|" fooooooooooooooooooooooooooooooooooooooo"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|patchSetObjectMissing ()
specifier|public
name|void
name|patchSetObjectMissing
parameter_list|()
throws|throws
name|Exception
block|{
name|Change
name|c
init|=
name|newChange
argument_list|(
name|project
argument_list|,
name|userId
argument_list|)
decl_stmt|;
name|db
operator|.
name|changes
argument_list|()
operator|.
name|insert
argument_list|(
name|singleton
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
name|PatchSet
name|ps
init|=
name|newPatchSet
argument_list|(
name|c
operator|.
name|currentPatchSetId
argument_list|()
argument_list|,
name|ObjectId
operator|.
name|fromString
argument_list|(
literal|"deadbeefdeadbeefdeadbeefdeadbeefdeadbeef"
argument_list|)
argument_list|,
name|userId
argument_list|)
decl_stmt|;
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|insert
argument_list|(
name|singleton
argument_list|(
name|ps
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|checker
operator|.
name|check
argument_list|(
name|c
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|"Object missing: patch set 1: deadbeefdeadbeefdeadbeefdeadbeefdeadbeef"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|currentPatchSetMissing ()
specifier|public
name|void
name|currentPatchSetMissing
parameter_list|()
throws|throws
name|Exception
block|{
name|Change
name|c
init|=
name|newChange
argument_list|(
name|project
argument_list|,
name|userId
argument_list|)
decl_stmt|;
name|db
operator|.
name|changes
argument_list|()
operator|.
name|insert
argument_list|(
name|singleton
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|checker
operator|.
name|check
argument_list|(
name|c
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|"Current patch set 1 not found"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|duplicatePatchSetRevisions ()
specifier|public
name|void
name|duplicatePatchSetRevisions
parameter_list|()
throws|throws
name|Exception
block|{
name|Change
name|c
init|=
name|newChange
argument_list|(
name|project
argument_list|,
name|userId
argument_list|)
decl_stmt|;
name|db
operator|.
name|changes
argument_list|()
operator|.
name|insert
argument_list|(
name|singleton
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
name|RevCommit
name|commit1
init|=
name|repo
operator|.
name|branch
argument_list|(
name|c
operator|.
name|currentPatchSetId
argument_list|()
operator|.
name|toRefName
argument_list|()
argument_list|)
operator|.
name|commit
argument_list|()
operator|.
name|parent
argument_list|(
name|tip
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|PatchSet
name|ps1
init|=
name|newPatchSet
argument_list|(
name|c
operator|.
name|currentPatchSetId
argument_list|()
argument_list|,
name|commit1
argument_list|,
name|userId
argument_list|)
decl_stmt|;
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|insert
argument_list|(
name|singleton
argument_list|(
name|ps1
argument_list|)
argument_list|)
expr_stmt|;
name|incrementPatchSet
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|PatchSet
name|ps2
init|=
name|newPatchSet
argument_list|(
name|c
operator|.
name|currentPatchSetId
argument_list|()
argument_list|,
name|commit1
argument_list|,
name|userId
argument_list|)
decl_stmt|;
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|insert
argument_list|(
name|singleton
argument_list|(
name|ps2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|checker
operator|.
name|check
argument_list|(
name|c
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|"Multiple patch sets pointing to "
operator|+
name|commit1
operator|.
name|name
argument_list|()
operator|+
literal|": [1, 2]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|missingDestRef ()
specifier|public
name|void
name|missingDestRef
parameter_list|()
throws|throws
name|Exception
block|{
name|RefUpdate
name|ru
init|=
name|repo
operator|.
name|getRepository
argument_list|()
operator|.
name|updateRef
argument_list|(
literal|"refs/heads/master"
argument_list|)
decl_stmt|;
name|ru
operator|.
name|setForceUpdate
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ru
operator|.
name|delete
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|RefUpdate
operator|.
name|Result
operator|.
name|FORCED
argument_list|)
expr_stmt|;
name|Change
name|c
init|=
name|newChange
argument_list|(
name|project
argument_list|,
name|userId
argument_list|)
decl_stmt|;
name|db
operator|.
name|changes
argument_list|()
operator|.
name|insert
argument_list|(
name|singleton
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
name|RevCommit
name|commit
init|=
name|repo
operator|.
name|commit
argument_list|()
operator|.
name|create
argument_list|()
decl_stmt|;
name|PatchSet
name|ps
init|=
name|newPatchSet
argument_list|(
name|c
operator|.
name|currentPatchSetId
argument_list|()
argument_list|,
name|commit
argument_list|,
name|userId
argument_list|)
decl_stmt|;
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|insert
argument_list|(
name|singleton
argument_list|(
name|ps
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|checker
operator|.
name|check
argument_list|(
name|c
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|"Destination ref not found (may be new branch): master"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|mergedChangeIsNotMerged ()
specifier|public
name|void
name|mergedChangeIsNotMerged
parameter_list|()
throws|throws
name|Exception
block|{
name|Change
name|c
init|=
name|newChange
argument_list|(
name|project
argument_list|,
name|userId
argument_list|)
decl_stmt|;
name|c
operator|.
name|setStatus
argument_list|(
name|Change
operator|.
name|Status
operator|.
name|MERGED
argument_list|)
expr_stmt|;
name|db
operator|.
name|changes
argument_list|()
operator|.
name|insert
argument_list|(
name|singleton
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
name|RevCommit
name|commit
init|=
name|repo
operator|.
name|branch
argument_list|(
name|c
operator|.
name|currentPatchSetId
argument_list|()
operator|.
name|toRefName
argument_list|()
argument_list|)
operator|.
name|commit
argument_list|()
operator|.
name|parent
argument_list|(
name|tip
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|PatchSet
name|ps
init|=
name|newPatchSet
argument_list|(
name|c
operator|.
name|currentPatchSetId
argument_list|()
argument_list|,
name|commit
argument_list|,
name|userId
argument_list|)
decl_stmt|;
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|insert
argument_list|(
name|singleton
argument_list|(
name|ps
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|checker
operator|.
name|check
argument_list|(
name|c
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|"Patch set 1 ("
operator|+
name|commit
operator|.
name|name
argument_list|()
operator|+
literal|") is not merged into destination ref"
operator|+
literal|" master ("
operator|+
name|tip
operator|.
name|name
argument_list|()
operator|+
literal|"), but change status is MERGED"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|newChangeIsMerged ()
specifier|public
name|void
name|newChangeIsMerged
parameter_list|()
throws|throws
name|Exception
block|{
name|Change
name|c
init|=
name|newChange
argument_list|(
name|project
argument_list|,
name|userId
argument_list|)
decl_stmt|;
name|db
operator|.
name|changes
argument_list|()
operator|.
name|insert
argument_list|(
name|singleton
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
name|RevCommit
name|commit
init|=
name|repo
operator|.
name|branch
argument_list|(
name|c
operator|.
name|currentPatchSetId
argument_list|()
operator|.
name|toRefName
argument_list|()
argument_list|)
operator|.
name|commit
argument_list|()
operator|.
name|parent
argument_list|(
name|tip
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|PatchSet
name|ps
init|=
name|newPatchSet
argument_list|(
name|c
operator|.
name|currentPatchSetId
argument_list|()
argument_list|,
name|commit
argument_list|,
name|userId
argument_list|)
decl_stmt|;
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|insert
argument_list|(
name|singleton
argument_list|(
name|ps
argument_list|)
argument_list|)
expr_stmt|;
name|repo
operator|.
name|branch
argument_list|(
name|c
operator|.
name|getDest
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|update
argument_list|(
name|commit
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|checker
operator|.
name|check
argument_list|(
name|c
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|"Patch set 1 ("
operator|+
name|commit
operator|.
name|name
argument_list|()
operator|+
literal|") is merged into destination ref"
operator|+
literal|" master ("
operator|+
name|commit
operator|.
name|name
argument_list|()
operator|+
literal|"), but change status is NEW"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

