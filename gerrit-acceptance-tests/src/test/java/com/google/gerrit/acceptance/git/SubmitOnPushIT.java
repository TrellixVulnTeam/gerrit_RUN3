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
DECL|package|com.google.gerrit.acceptance.git
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|git
package|;
end_package

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
name|assertNull
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
name|common
operator|.
name|collect
operator|.
name|Iterables
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
name|NoHttpd
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
name|PushOneCommit
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
name|AccessSection
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
name|Permission
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
name|PermissionRule
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
name|server
operator|.
name|ApprovalsUtil
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
name|account
operator|.
name|GroupCache
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
name|CommitMergeStatus
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
name|git
operator|.
name|MetaDataUpdate
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
name|ProjectConfig
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
name|Inject
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
name|api
operator|.
name|errors
operator|.
name|GitAPIException
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
name|errors
operator|.
name|ConfigInvalidException
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
name|errors
operator|.
name|RepositoryNotFoundException
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
name|Repository
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
name|eclipse
operator|.
name|jgit
operator|.
name|transport
operator|.
name|RefSpec
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
name|io
operator|.
name|IOException
import|;
end_import

begin_class
annotation|@
name|NoHttpd
DECL|class|SubmitOnPushIT
specifier|public
class|class
name|SubmitOnPushIT
extends|extends
name|AbstractDaemonTest
block|{
annotation|@
name|Inject
DECL|field|repoManager
specifier|private
name|GitRepositoryManager
name|repoManager
decl_stmt|;
annotation|@
name|Inject
DECL|field|approvalsUtil
specifier|private
name|ApprovalsUtil
name|approvalsUtil
decl_stmt|;
annotation|@
name|Inject
DECL|field|groupCache
specifier|private
name|GroupCache
name|groupCache
decl_stmt|;
annotation|@
name|Inject
DECL|field|changeNotesFactory
specifier|private
name|ChangeNotes
operator|.
name|Factory
name|changeNotesFactory
decl_stmt|;
annotation|@
name|Inject
DECL|field|serverIdent
specifier|private
annotation|@
name|GerritPersonIdent
name|PersonIdent
name|serverIdent
decl_stmt|;
annotation|@
name|Inject
DECL|field|pushFactory
specifier|private
name|PushOneCommit
operator|.
name|Factory
name|pushFactory
decl_stmt|;
annotation|@
name|Test
DECL|method|submitOnPush ()
specifier|public
name|void
name|submitOnPush
parameter_list|()
throws|throws
name|GitAPIException
throws|,
name|OrmException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|grant
argument_list|(
name|Permission
operator|.
name|SUBMIT
argument_list|,
name|project
argument_list|,
literal|"refs/for/refs/heads/master"
argument_list|)
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|pushTo
argument_list|(
literal|"refs/for/master%submit"
argument_list|)
decl_stmt|;
name|r
operator|.
name|assertOkStatus
argument_list|()
expr_stmt|;
name|r
operator|.
name|assertChange
argument_list|(
name|Change
operator|.
name|Status
operator|.
name|MERGED
argument_list|,
literal|null
argument_list|,
name|admin
argument_list|)
expr_stmt|;
name|assertSubmitApproval
argument_list|(
name|r
operator|.
name|getPatchSetId
argument_list|()
argument_list|)
expr_stmt|;
name|assertCommit
argument_list|(
name|project
argument_list|,
literal|"refs/heads/master"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|submitOnPushWithTag ()
specifier|public
name|void
name|submitOnPushWithTag
parameter_list|()
throws|throws
name|GitAPIException
throws|,
name|OrmException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|grant
argument_list|(
name|Permission
operator|.
name|SUBMIT
argument_list|,
name|project
argument_list|,
literal|"refs/for/refs/heads/master"
argument_list|)
expr_stmt|;
name|grant
argument_list|(
name|Permission
operator|.
name|CREATE
argument_list|,
name|project
argument_list|,
literal|"refs/tags/*"
argument_list|)
expr_stmt|;
name|grant
argument_list|(
name|Permission
operator|.
name|PUSH
argument_list|,
name|project
argument_list|,
literal|"refs/tags/*"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|tag
init|=
literal|"v1.0"
decl_stmt|;
name|PushOneCommit
name|push
init|=
name|pushFactory
operator|.
name|create
argument_list|(
name|db
argument_list|,
name|admin
operator|.
name|getIdent
argument_list|()
argument_list|)
decl_stmt|;
name|push
operator|.
name|setTag
argument_list|(
name|tag
argument_list|)
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|push
operator|.
name|to
argument_list|(
name|git
argument_list|,
literal|"refs/for/master%submit"
argument_list|)
decl_stmt|;
name|r
operator|.
name|assertOkStatus
argument_list|()
expr_stmt|;
name|r
operator|.
name|assertChange
argument_list|(
name|Change
operator|.
name|Status
operator|.
name|MERGED
argument_list|,
literal|null
argument_list|,
name|admin
argument_list|)
expr_stmt|;
name|assertSubmitApproval
argument_list|(
name|r
operator|.
name|getPatchSetId
argument_list|()
argument_list|)
expr_stmt|;
name|assertCommit
argument_list|(
name|project
argument_list|,
literal|"refs/heads/master"
argument_list|)
expr_stmt|;
name|assertTag
argument_list|(
name|project
argument_list|,
literal|"refs/heads/master"
argument_list|,
name|tag
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|submitOnPushToRefsMetaConfig ()
specifier|public
name|void
name|submitOnPushToRefsMetaConfig
parameter_list|()
throws|throws
name|GitAPIException
throws|,
name|OrmException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|grant
argument_list|(
name|Permission
operator|.
name|SUBMIT
argument_list|,
name|project
argument_list|,
literal|"refs/for/refs/meta/config"
argument_list|)
expr_stmt|;
name|git
operator|.
name|fetch
argument_list|()
operator|.
name|setRefSpecs
argument_list|(
operator|new
name|RefSpec
argument_list|(
literal|"refs/meta/config:refs/meta/config"
argument_list|)
argument_list|)
operator|.
name|call
argument_list|()
expr_stmt|;
name|ObjectId
name|objectId
init|=
name|git
operator|.
name|getRepository
argument_list|()
operator|.
name|getRef
argument_list|(
literal|"refs/meta/config"
argument_list|)
operator|.
name|getObjectId
argument_list|()
decl_stmt|;
name|git
operator|.
name|checkout
argument_list|()
operator|.
name|setName
argument_list|(
name|objectId
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|call
argument_list|()
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|pushTo
argument_list|(
literal|"refs/for/refs/meta/config%submit"
argument_list|)
decl_stmt|;
name|r
operator|.
name|assertOkStatus
argument_list|()
expr_stmt|;
name|r
operator|.
name|assertChange
argument_list|(
name|Change
operator|.
name|Status
operator|.
name|MERGED
argument_list|,
literal|null
argument_list|,
name|admin
argument_list|)
expr_stmt|;
name|assertSubmitApproval
argument_list|(
name|r
operator|.
name|getPatchSetId
argument_list|()
argument_list|)
expr_stmt|;
name|assertCommit
argument_list|(
name|project
argument_list|,
literal|"refs/meta/config"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|submitOnPushMergeConflict ()
specifier|public
name|void
name|submitOnPushMergeConflict
parameter_list|()
throws|throws
name|GitAPIException
throws|,
name|OrmException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|String
name|master
init|=
literal|"refs/heads/master"
decl_stmt|;
name|ObjectId
name|objectId
init|=
name|git
operator|.
name|getRepository
argument_list|()
operator|.
name|getRef
argument_list|(
name|master
argument_list|)
operator|.
name|getObjectId
argument_list|()
decl_stmt|;
name|push
argument_list|(
name|master
argument_list|,
literal|"one change"
argument_list|,
literal|"a.txt"
argument_list|,
literal|"some content"
argument_list|)
expr_stmt|;
name|git
operator|.
name|checkout
argument_list|()
operator|.
name|setName
argument_list|(
name|objectId
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|call
argument_list|()
expr_stmt|;
name|grant
argument_list|(
name|Permission
operator|.
name|SUBMIT
argument_list|,
name|project
argument_list|,
literal|"refs/for/refs/heads/master"
argument_list|)
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|push
argument_list|(
literal|"refs/for/master%submit"
argument_list|,
literal|"other change"
argument_list|,
literal|"a.txt"
argument_list|,
literal|"other content"
argument_list|)
decl_stmt|;
name|r
operator|.
name|assertOkStatus
argument_list|()
expr_stmt|;
name|r
operator|.
name|assertChange
argument_list|(
name|Change
operator|.
name|Status
operator|.
name|NEW
argument_list|,
literal|null
argument_list|,
name|admin
argument_list|)
expr_stmt|;
name|r
operator|.
name|assertMessage
argument_list|(
name|CommitMergeStatus
operator|.
name|PATH_CONFLICT
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|submitOnPushSuccessfulMerge ()
specifier|public
name|void
name|submitOnPushSuccessfulMerge
parameter_list|()
throws|throws
name|GitAPIException
throws|,
name|OrmException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|String
name|master
init|=
literal|"refs/heads/master"
decl_stmt|;
name|ObjectId
name|objectId
init|=
name|git
operator|.
name|getRepository
argument_list|()
operator|.
name|getRef
argument_list|(
name|master
argument_list|)
operator|.
name|getObjectId
argument_list|()
decl_stmt|;
name|push
argument_list|(
name|master
argument_list|,
literal|"one change"
argument_list|,
literal|"a.txt"
argument_list|,
literal|"some content"
argument_list|)
expr_stmt|;
name|git
operator|.
name|checkout
argument_list|()
operator|.
name|setName
argument_list|(
name|objectId
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|call
argument_list|()
expr_stmt|;
name|grant
argument_list|(
name|Permission
operator|.
name|SUBMIT
argument_list|,
name|project
argument_list|,
literal|"refs/for/refs/heads/master"
argument_list|)
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|push
argument_list|(
literal|"refs/for/master%submit"
argument_list|,
literal|"other change"
argument_list|,
literal|"b.txt"
argument_list|,
literal|"other content"
argument_list|)
decl_stmt|;
name|r
operator|.
name|assertOkStatus
argument_list|()
expr_stmt|;
name|r
operator|.
name|assertChange
argument_list|(
name|Change
operator|.
name|Status
operator|.
name|MERGED
argument_list|,
literal|null
argument_list|,
name|admin
argument_list|)
expr_stmt|;
name|assertMergeCommit
argument_list|(
name|master
argument_list|,
literal|"other change"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|submitOnPushNewPatchSet ()
specifier|public
name|void
name|submitOnPushNewPatchSet
parameter_list|()
throws|throws
name|GitAPIException
throws|,
name|OrmException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|push
argument_list|(
literal|"refs/for/master"
argument_list|,
name|PushOneCommit
operator|.
name|SUBJECT
argument_list|,
literal|"a.txt"
argument_list|,
literal|"some content"
argument_list|)
decl_stmt|;
name|grant
argument_list|(
name|Permission
operator|.
name|SUBMIT
argument_list|,
name|project
argument_list|,
literal|"refs/for/refs/heads/master"
argument_list|)
expr_stmt|;
name|r
operator|=
name|push
argument_list|(
literal|"refs/for/master%submit"
argument_list|,
name|PushOneCommit
operator|.
name|SUBJECT
argument_list|,
literal|"a.txt"
argument_list|,
literal|"other content"
argument_list|,
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|assertOkStatus
argument_list|()
expr_stmt|;
name|r
operator|.
name|assertChange
argument_list|(
name|Change
operator|.
name|Status
operator|.
name|MERGED
argument_list|,
literal|null
argument_list|,
name|admin
argument_list|)
expr_stmt|;
name|Change
name|c
init|=
name|Iterables
operator|.
name|getOnlyElement
argument_list|(
name|db
operator|.
name|changes
argument_list|()
operator|.
name|byKey
argument_list|(
operator|new
name|Change
operator|.
name|Key
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
argument_list|)
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|byChange
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|toList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertSubmitApproval
argument_list|(
name|r
operator|.
name|getPatchSetId
argument_list|()
argument_list|)
expr_stmt|;
name|assertCommit
argument_list|(
name|project
argument_list|,
literal|"refs/heads/master"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|submitOnPushNotAllowed_Error ()
specifier|public
name|void
name|submitOnPushNotAllowed_Error
parameter_list|()
throws|throws
name|GitAPIException
throws|,
name|OrmException
throws|,
name|IOException
block|{
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|pushTo
argument_list|(
literal|"refs/for/master%submit"
argument_list|)
decl_stmt|;
name|r
operator|.
name|assertErrorStatus
argument_list|(
literal|"submit not allowed"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|submitOnPushNewPatchSetNotAllowed_Error ()
specifier|public
name|void
name|submitOnPushNewPatchSetNotAllowed_Error
parameter_list|()
throws|throws
name|GitAPIException
throws|,
name|OrmException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|push
argument_list|(
literal|"refs/for/master"
argument_list|,
name|PushOneCommit
operator|.
name|SUBJECT
argument_list|,
literal|"a.txt"
argument_list|,
literal|"some content"
argument_list|)
decl_stmt|;
name|r
operator|=
name|push
argument_list|(
literal|"refs/for/master%submit"
argument_list|,
name|PushOneCommit
operator|.
name|SUBJECT
argument_list|,
literal|"a.txt"
argument_list|,
literal|"other content"
argument_list|,
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|assertErrorStatus
argument_list|(
literal|"submit not allowed"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|submitOnPushingDraft_Error ()
specifier|public
name|void
name|submitOnPushingDraft_Error
parameter_list|()
throws|throws
name|GitAPIException
throws|,
name|OrmException
throws|,
name|IOException
block|{
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|pushTo
argument_list|(
literal|"refs/for/master%draft,submit"
argument_list|)
decl_stmt|;
name|r
operator|.
name|assertErrorStatus
argument_list|(
literal|"cannot submit draft"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|submitOnPushToNonExistingBranch_Error ()
specifier|public
name|void
name|submitOnPushToNonExistingBranch_Error
parameter_list|()
throws|throws
name|GitAPIException
throws|,
name|OrmException
throws|,
name|IOException
block|{
name|String
name|branchName
init|=
literal|"non-existing"
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|pushTo
argument_list|(
literal|"refs/for/"
operator|+
name|branchName
operator|+
literal|"%submit"
argument_list|)
decl_stmt|;
name|r
operator|.
name|assertErrorStatus
argument_list|(
literal|"branch "
operator|+
name|branchName
operator|+
literal|" not found"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|mergeOnPushToBranch ()
specifier|public
name|void
name|mergeOnPushToBranch
parameter_list|()
throws|throws
name|Exception
block|{
name|grant
argument_list|(
name|Permission
operator|.
name|PUSH
argument_list|,
name|project
argument_list|,
literal|"refs/heads/master"
argument_list|)
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|push
argument_list|(
literal|"refs/for/master"
argument_list|,
name|PushOneCommit
operator|.
name|SUBJECT
argument_list|,
literal|"a.txt"
argument_list|,
literal|"some content"
argument_list|)
decl_stmt|;
name|r
operator|.
name|assertOkStatus
argument_list|()
expr_stmt|;
name|git
operator|.
name|push
argument_list|()
operator|.
name|setRefSpecs
argument_list|(
operator|new
name|RefSpec
argument_list|(
name|r
operator|.
name|getCommitId
argument_list|()
operator|.
name|name
argument_list|()
operator|+
literal|":refs/heads/master"
argument_list|)
argument_list|)
operator|.
name|call
argument_list|()
expr_stmt|;
name|assertCommit
argument_list|(
name|project
argument_list|,
literal|"refs/heads/master"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|getSubmitter
argument_list|(
name|r
operator|.
name|getPatchSetId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Change
name|c
init|=
name|db
operator|.
name|changes
argument_list|()
operator|.
name|get
argument_list|(
name|r
operator|.
name|getPatchSetId
argument_list|()
operator|.
name|getParentKey
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|Change
operator|.
name|Status
operator|.
name|MERGED
argument_list|,
name|c
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|grant (String permission, Project.NameKey project, String ref)
specifier|private
name|void
name|grant
parameter_list|(
name|String
name|permission
parameter_list|,
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
name|String
name|ref
parameter_list|)
throws|throws
name|RepositoryNotFoundException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|MetaDataUpdate
name|md
init|=
name|metaDataUpdateFactory
operator|.
name|create
argument_list|(
name|project
argument_list|)
decl_stmt|;
name|md
operator|.
name|setMessage
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Grant %s on %s"
argument_list|,
name|permission
argument_list|,
name|ref
argument_list|)
argument_list|)
expr_stmt|;
name|ProjectConfig
name|config
init|=
name|ProjectConfig
operator|.
name|read
argument_list|(
name|md
argument_list|)
decl_stmt|;
name|AccessSection
name|s
init|=
name|config
operator|.
name|getAccessSection
argument_list|(
name|ref
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Permission
name|p
init|=
name|s
operator|.
name|getPermission
argument_list|(
name|permission
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|AccountGroup
name|adminGroup
init|=
name|groupCache
operator|.
name|get
argument_list|(
operator|new
name|AccountGroup
operator|.
name|NameKey
argument_list|(
literal|"Administrators"
argument_list|)
argument_list|)
decl_stmt|;
name|p
operator|.
name|add
argument_list|(
operator|new
name|PermissionRule
argument_list|(
name|config
operator|.
name|resolve
argument_list|(
name|adminGroup
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|config
operator|.
name|commit
argument_list|(
name|md
argument_list|)
expr_stmt|;
name|projectCache
operator|.
name|evict
argument_list|(
name|config
operator|.
name|getProject
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getSubmitter (PatchSet.Id patchSetId)
specifier|private
name|PatchSetApproval
name|getSubmitter
parameter_list|(
name|PatchSet
operator|.
name|Id
name|patchSetId
parameter_list|)
throws|throws
name|OrmException
block|{
name|Change
name|c
init|=
name|db
operator|.
name|changes
argument_list|()
operator|.
name|get
argument_list|(
name|patchSetId
operator|.
name|getParentKey
argument_list|()
argument_list|)
decl_stmt|;
name|ChangeNotes
name|notes
init|=
name|changeNotesFactory
operator|.
name|create
argument_list|(
name|c
argument_list|)
operator|.
name|load
argument_list|()
decl_stmt|;
return|return
name|approvalsUtil
operator|.
name|getSubmitter
argument_list|(
name|db
argument_list|,
name|notes
argument_list|,
name|patchSetId
argument_list|)
return|;
block|}
DECL|method|assertSubmitApproval (PatchSet.Id patchSetId)
specifier|private
name|void
name|assertSubmitApproval
parameter_list|(
name|PatchSet
operator|.
name|Id
name|patchSetId
parameter_list|)
throws|throws
name|OrmException
block|{
name|PatchSetApproval
name|a
init|=
name|getSubmitter
argument_list|(
name|patchSetId
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|a
operator|.
name|isSubmit
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|a
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|admin
operator|.
name|id
argument_list|,
name|a
operator|.
name|getAccountId
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertCommit (Project.NameKey project, String branch)
specifier|private
name|void
name|assertCommit
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
name|String
name|branch
parameter_list|)
throws|throws
name|IOException
block|{
name|Repository
name|r
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|project
argument_list|)
decl_stmt|;
try|try
block|{
name|RevWalk
name|rw
init|=
operator|new
name|RevWalk
argument_list|(
name|r
argument_list|)
decl_stmt|;
try|try
block|{
name|RevCommit
name|c
init|=
name|rw
operator|.
name|parseCommit
argument_list|(
name|r
operator|.
name|getRef
argument_list|(
name|branch
argument_list|)
operator|.
name|getObjectId
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|PushOneCommit
operator|.
name|SUBJECT
argument_list|,
name|c
operator|.
name|getShortMessage
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|admin
operator|.
name|email
argument_list|,
name|c
operator|.
name|getAuthorIdent
argument_list|()
operator|.
name|getEmailAddress
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|admin
operator|.
name|email
argument_list|,
name|c
operator|.
name|getCommitterIdent
argument_list|()
operator|.
name|getEmailAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|rw
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|assertMergeCommit (String branch, String subject)
specifier|private
name|void
name|assertMergeCommit
parameter_list|(
name|String
name|branch
parameter_list|,
name|String
name|subject
parameter_list|)
throws|throws
name|IOException
block|{
name|Repository
name|r
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|project
argument_list|)
decl_stmt|;
try|try
block|{
name|RevWalk
name|rw
init|=
operator|new
name|RevWalk
argument_list|(
name|r
argument_list|)
decl_stmt|;
try|try
block|{
name|RevCommit
name|c
init|=
name|rw
operator|.
name|parseCommit
argument_list|(
name|r
operator|.
name|getRef
argument_list|(
name|branch
argument_list|)
operator|.
name|getObjectId
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|c
operator|.
name|getParentCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Merge \""
operator|+
name|subject
operator|+
literal|"\""
argument_list|,
name|c
operator|.
name|getShortMessage
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|admin
operator|.
name|email
argument_list|,
name|c
operator|.
name|getAuthorIdent
argument_list|()
operator|.
name|getEmailAddress
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|serverIdent
operator|.
name|getEmailAddress
argument_list|()
argument_list|,
name|c
operator|.
name|getCommitterIdent
argument_list|()
operator|.
name|getEmailAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|rw
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|assertTag (Project.NameKey project, String branch, String tagName)
specifier|private
name|void
name|assertTag
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
name|String
name|branch
parameter_list|,
name|String
name|tagName
parameter_list|)
throws|throws
name|IOException
block|{
name|Repository
name|r
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|project
argument_list|)
decl_stmt|;
try|try
block|{
name|ObjectId
name|headCommit
init|=
name|r
operator|.
name|getRef
argument_list|(
name|branch
argument_list|)
operator|.
name|getObjectId
argument_list|()
decl_stmt|;
name|ObjectId
name|taggedCommit
init|=
name|r
operator|.
name|getRef
argument_list|(
name|tagName
argument_list|)
operator|.
name|getObjectId
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|headCommit
argument_list|,
name|taggedCommit
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|pushTo (String ref)
specifier|private
name|PushOneCommit
operator|.
name|Result
name|pushTo
parameter_list|(
name|String
name|ref
parameter_list|)
throws|throws
name|GitAPIException
throws|,
name|IOException
block|{
name|PushOneCommit
name|push
init|=
name|pushFactory
operator|.
name|create
argument_list|(
name|db
argument_list|,
name|admin
operator|.
name|getIdent
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|push
operator|.
name|to
argument_list|(
name|git
argument_list|,
name|ref
argument_list|)
return|;
block|}
DECL|method|push (String ref, String subject, String fileName, String content)
specifier|private
name|PushOneCommit
operator|.
name|Result
name|push
parameter_list|(
name|String
name|ref
parameter_list|,
name|String
name|subject
parameter_list|,
name|String
name|fileName
parameter_list|,
name|String
name|content
parameter_list|)
throws|throws
name|GitAPIException
throws|,
name|IOException
block|{
name|PushOneCommit
name|push
init|=
name|pushFactory
operator|.
name|create
argument_list|(
name|db
argument_list|,
name|admin
operator|.
name|getIdent
argument_list|()
argument_list|,
name|subject
argument_list|,
name|fileName
argument_list|,
name|content
argument_list|)
decl_stmt|;
return|return
name|push
operator|.
name|to
argument_list|(
name|git
argument_list|,
name|ref
argument_list|)
return|;
block|}
DECL|method|push (String ref, String subject, String fileName, String content, String changeId)
specifier|private
name|PushOneCommit
operator|.
name|Result
name|push
parameter_list|(
name|String
name|ref
parameter_list|,
name|String
name|subject
parameter_list|,
name|String
name|fileName
parameter_list|,
name|String
name|content
parameter_list|,
name|String
name|changeId
parameter_list|)
throws|throws
name|GitAPIException
throws|,
name|IOException
block|{
name|PushOneCommit
name|push
init|=
name|pushFactory
operator|.
name|create
argument_list|(
name|db
argument_list|,
name|admin
operator|.
name|getIdent
argument_list|()
argument_list|,
name|subject
argument_list|,
name|fileName
argument_list|,
name|content
argument_list|,
name|changeId
argument_list|)
decl_stmt|;
return|return
name|push
operator|.
name|to
argument_list|(
name|git
argument_list|,
name|ref
argument_list|)
return|;
block|}
block|}
end_class

end_unit

