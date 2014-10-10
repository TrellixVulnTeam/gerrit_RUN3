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
DECL|package|com.google.gerrit.acceptance.server.project
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
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
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
name|LabelType
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
name|changes
operator|.
name|CherryPickInput
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
name|changes
operator|.
name|ReviewInput
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
name|extensions
operator|.
name|common
operator|.
name|LabelInfo
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
name|Repository
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
annotation|@
name|NoHttpd
DECL|class|LabelTypeIT
specifier|public
class|class
name|LabelTypeIT
extends|extends
name|AbstractDaemonTest
block|{
annotation|@
name|ConfigSuite
operator|.
name|Config
DECL|method|noteDbEnabled ()
specifier|public
specifier|static
name|Config
name|noteDbEnabled
parameter_list|()
block|{
return|return
name|NotesMigration
operator|.
name|allEnabledConfig
argument_list|()
return|;
block|}
annotation|@
name|Inject
DECL|field|repoManager
specifier|private
name|GitRepositoryManager
name|repoManager
decl_stmt|;
DECL|field|codeReview
specifier|private
name|LabelType
name|codeReview
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
name|ProjectConfig
name|cfg
init|=
name|projectCache
operator|.
name|checkedGet
argument_list|(
name|allProjects
argument_list|)
operator|.
name|getConfig
argument_list|()
decl_stmt|;
name|codeReview
operator|=
name|checkNotNull
argument_list|(
name|cfg
operator|.
name|getLabelSections
argument_list|()
operator|.
name|get
argument_list|(
literal|"Code-Review"
argument_list|)
argument_list|)
expr_stmt|;
name|codeReview
operator|.
name|setCopyMinScore
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|codeReview
operator|.
name|setCopyMaxScore
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|codeReview
operator|.
name|setCopyAllScoresOnTrivialRebase
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|codeReview
operator|.
name|setCopyAllScoresIfNoCodeChange
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|codeReview
operator|.
name|setDefaultValue
argument_list|(
operator|(
name|short
operator|)
operator|-
literal|1
argument_list|)
expr_stmt|;
name|saveProjectConfig
argument_list|(
name|cfg
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|noCopyMinScoreOnRework ()
specifier|public
name|void
name|noCopyMinScoreOnRework
parameter_list|()
throws|throws
name|Exception
block|{
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|createChange
argument_list|()
decl_stmt|;
name|revision
argument_list|(
name|r
argument_list|)
operator|.
name|review
argument_list|(
name|ReviewInput
operator|.
name|reject
argument_list|()
argument_list|)
expr_stmt|;
name|assertApproval
argument_list|(
name|r
argument_list|,
operator|-
literal|2
argument_list|)
expr_stmt|;
name|r
operator|=
name|amendChange
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertApproval
argument_list|(
name|r
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|copyMinScoreOnRework ()
specifier|public
name|void
name|copyMinScoreOnRework
parameter_list|()
throws|throws
name|Exception
block|{
name|codeReview
operator|.
name|setCopyMinScore
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|saveLabelConfig
argument_list|()
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|createChange
argument_list|()
decl_stmt|;
name|revision
argument_list|(
name|r
argument_list|)
operator|.
name|review
argument_list|(
name|ReviewInput
operator|.
name|reject
argument_list|()
argument_list|)
expr_stmt|;
comment|//assertApproval(r, -2);
name|r
operator|=
name|amendChange
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertApproval
argument_list|(
name|r
argument_list|,
operator|-
literal|2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|noCopyMaxScoreOnRework ()
specifier|public
name|void
name|noCopyMaxScoreOnRework
parameter_list|()
throws|throws
name|Exception
block|{
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|createChange
argument_list|()
decl_stmt|;
name|revision
argument_list|(
name|r
argument_list|)
operator|.
name|review
argument_list|(
name|ReviewInput
operator|.
name|approve
argument_list|()
argument_list|)
expr_stmt|;
name|assertApproval
argument_list|(
name|r
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|r
operator|=
name|amendChange
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertApproval
argument_list|(
name|r
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|copyMaxScoreOnRework ()
specifier|public
name|void
name|copyMaxScoreOnRework
parameter_list|()
throws|throws
name|Exception
block|{
name|codeReview
operator|.
name|setCopyMaxScore
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|saveLabelConfig
argument_list|()
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|createChange
argument_list|()
decl_stmt|;
name|revision
argument_list|(
name|r
argument_list|)
operator|.
name|review
argument_list|(
name|ReviewInput
operator|.
name|approve
argument_list|()
argument_list|)
expr_stmt|;
name|assertApproval
argument_list|(
name|r
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|r
operator|=
name|amendChange
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertApproval
argument_list|(
name|r
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|noCopyNonMaxScoreOnRework ()
specifier|public
name|void
name|noCopyNonMaxScoreOnRework
parameter_list|()
throws|throws
name|Exception
block|{
name|codeReview
operator|.
name|setCopyMinScore
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|codeReview
operator|.
name|setCopyMaxScore
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|saveLabelConfig
argument_list|()
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|createChange
argument_list|()
decl_stmt|;
name|revision
argument_list|(
name|r
argument_list|)
operator|.
name|review
argument_list|(
name|ReviewInput
operator|.
name|recommend
argument_list|()
argument_list|)
expr_stmt|;
name|assertApproval
argument_list|(
name|r
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|r
operator|=
name|amendChange
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertApproval
argument_list|(
name|r
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|noCopyNonMinScoreOnRework ()
specifier|public
name|void
name|noCopyNonMinScoreOnRework
parameter_list|()
throws|throws
name|Exception
block|{
name|codeReview
operator|.
name|setCopyMinScore
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|codeReview
operator|.
name|setCopyMaxScore
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|saveLabelConfig
argument_list|()
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|createChange
argument_list|()
decl_stmt|;
name|revision
argument_list|(
name|r
argument_list|)
operator|.
name|review
argument_list|(
name|ReviewInput
operator|.
name|dislike
argument_list|()
argument_list|)
expr_stmt|;
name|assertApproval
argument_list|(
name|r
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|r
operator|=
name|amendChange
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertApproval
argument_list|(
name|r
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|noCopyAllScoresIfNoCodeChange ()
specifier|public
name|void
name|noCopyAllScoresIfNoCodeChange
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|file
init|=
literal|"a.txt"
decl_stmt|;
name|String
name|contents
init|=
literal|"contents"
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
argument_list|,
literal|"first subject"
argument_list|,
name|file
argument_list|,
name|contents
argument_list|)
decl_stmt|;
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
literal|"refs/for/master"
argument_list|)
decl_stmt|;
name|revision
argument_list|(
name|r
argument_list|)
operator|.
name|review
argument_list|(
name|ReviewInput
operator|.
name|recommend
argument_list|()
argument_list|)
expr_stmt|;
name|assertApproval
argument_list|(
name|r
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|push
operator|=
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
literal|"second subject"
argument_list|,
name|file
argument_list|,
name|contents
argument_list|,
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|=
name|push
operator|.
name|to
argument_list|(
name|git
argument_list|,
literal|"refs/for/master"
argument_list|)
expr_stmt|;
name|assertApproval
argument_list|(
name|r
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|copyAllScoresIfNoCodeChange ()
specifier|public
name|void
name|copyAllScoresIfNoCodeChange
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|file
init|=
literal|"a.txt"
decl_stmt|;
name|String
name|contents
init|=
literal|"contents"
decl_stmt|;
name|codeReview
operator|.
name|setCopyAllScoresIfNoCodeChange
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|saveLabelConfig
argument_list|()
expr_stmt|;
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
literal|"first subject"
argument_list|,
name|file
argument_list|,
name|contents
argument_list|)
decl_stmt|;
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
literal|"refs/for/master"
argument_list|)
decl_stmt|;
name|revision
argument_list|(
name|r
argument_list|)
operator|.
name|review
argument_list|(
name|ReviewInput
operator|.
name|recommend
argument_list|()
argument_list|)
expr_stmt|;
name|assertApproval
argument_list|(
name|r
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|push
operator|=
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
literal|"second subject"
argument_list|,
name|file
argument_list|,
name|contents
argument_list|,
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|=
name|push
operator|.
name|to
argument_list|(
name|git
argument_list|,
literal|"refs/for/master"
argument_list|)
expr_stmt|;
name|assertApproval
argument_list|(
name|r
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|noCopyAllScoresOnTrivialRebase ()
specifier|public
name|void
name|noCopyAllScoresOnTrivialRebase
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|subject
init|=
literal|"test commit"
decl_stmt|;
name|String
name|file
init|=
literal|"a.txt"
decl_stmt|;
name|String
name|contents
init|=
literal|"contents"
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
name|PushOneCommit
operator|.
name|Result
name|r1
init|=
name|push
operator|.
name|to
argument_list|(
name|git
argument_list|,
literal|"refs/for/master"
argument_list|)
decl_stmt|;
name|merge
argument_list|(
name|r1
argument_list|)
expr_stmt|;
name|push
operator|=
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
literal|"non-conflicting"
argument_list|,
literal|"b.txt"
argument_list|,
literal|"other contents"
argument_list|)
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r2
init|=
name|push
operator|.
name|to
argument_list|(
name|git
argument_list|,
literal|"refs/for/master"
argument_list|)
decl_stmt|;
name|merge
argument_list|(
name|r2
argument_list|)
expr_stmt|;
name|git
operator|.
name|checkout
argument_list|()
operator|.
name|setName
argument_list|(
name|r1
operator|.
name|getCommit
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|call
argument_list|()
expr_stmt|;
name|push
operator|=
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
name|file
argument_list|,
name|contents
argument_list|)
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r3
init|=
name|push
operator|.
name|to
argument_list|(
name|git
argument_list|,
literal|"refs/for/master"
argument_list|)
decl_stmt|;
name|revision
argument_list|(
name|r3
argument_list|)
operator|.
name|review
argument_list|(
name|ReviewInput
operator|.
name|recommend
argument_list|()
argument_list|)
expr_stmt|;
name|assertApproval
argument_list|(
name|r3
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|rebase
argument_list|(
name|r3
argument_list|)
expr_stmt|;
name|assertApproval
argument_list|(
name|r3
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|copyAllScoresOnTrivialRebase ()
specifier|public
name|void
name|copyAllScoresOnTrivialRebase
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|subject
init|=
literal|"test commit"
decl_stmt|;
name|String
name|file
init|=
literal|"a.txt"
decl_stmt|;
name|String
name|contents
init|=
literal|"contents"
decl_stmt|;
name|codeReview
operator|.
name|setCopyAllScoresOnTrivialRebase
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|saveLabelConfig
argument_list|()
expr_stmt|;
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
name|PushOneCommit
operator|.
name|Result
name|r1
init|=
name|push
operator|.
name|to
argument_list|(
name|git
argument_list|,
literal|"refs/for/master"
argument_list|)
decl_stmt|;
name|merge
argument_list|(
name|r1
argument_list|)
expr_stmt|;
name|push
operator|=
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
literal|"non-conflicting"
argument_list|,
literal|"b.txt"
argument_list|,
literal|"other contents"
argument_list|)
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r2
init|=
name|push
operator|.
name|to
argument_list|(
name|git
argument_list|,
literal|"refs/for/master"
argument_list|)
decl_stmt|;
name|merge
argument_list|(
name|r2
argument_list|)
expr_stmt|;
name|git
operator|.
name|checkout
argument_list|()
operator|.
name|setName
argument_list|(
name|r1
operator|.
name|getCommit
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|call
argument_list|()
expr_stmt|;
name|push
operator|=
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
name|file
argument_list|,
name|contents
argument_list|)
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r3
init|=
name|push
operator|.
name|to
argument_list|(
name|git
argument_list|,
literal|"refs/for/master"
argument_list|)
decl_stmt|;
name|revision
argument_list|(
name|r3
argument_list|)
operator|.
name|review
argument_list|(
name|ReviewInput
operator|.
name|recommend
argument_list|()
argument_list|)
expr_stmt|;
name|assertApproval
argument_list|(
name|r3
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|rebase
argument_list|(
name|r3
argument_list|)
expr_stmt|;
name|assertApproval
argument_list|(
name|r3
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|copyAllScoresOnTrivialRebaseAndCherryPick ()
specifier|public
name|void
name|copyAllScoresOnTrivialRebaseAndCherryPick
parameter_list|()
throws|throws
name|Exception
block|{
name|codeReview
operator|.
name|setCopyAllScoresOnTrivialRebase
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|saveLabelConfig
argument_list|()
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r1
init|=
name|createChange
argument_list|()
decl_stmt|;
name|git
operator|.
name|checkout
argument_list|()
operator|.
name|setName
argument_list|(
name|r1
operator|.
name|getCommit
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|call
argument_list|()
expr_stmt|;
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
name|PushOneCommit
operator|.
name|SUBJECT
argument_list|,
literal|"b.txt"
argument_list|,
literal|"other contents"
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r2
init|=
name|push
operator|.
name|to
argument_list|(
name|git
argument_list|,
literal|"refs/for/master"
argument_list|)
decl_stmt|;
name|revision
argument_list|(
name|r2
argument_list|)
operator|.
name|review
argument_list|(
name|ReviewInput
operator|.
name|recommend
argument_list|()
argument_list|)
expr_stmt|;
name|CherryPickInput
name|in
init|=
operator|new
name|CherryPickInput
argument_list|()
decl_stmt|;
name|in
operator|.
name|destination
operator|=
literal|"master"
expr_stmt|;
name|in
operator|.
name|message
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"%s\n\nChange-Id: %s"
argument_list|,
name|PushOneCommit
operator|.
name|SUBJECT
argument_list|,
name|r2
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|doAssertApproval
argument_list|(
literal|1
argument_list|,
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|r2
operator|.
name|getChangeId
argument_list|()
argument_list|)
operator|.
name|revision
argument_list|(
name|r2
operator|.
name|getCommit
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|cherryPick
argument_list|(
name|in
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|copyNoScoresOnReworkAndCherryPick ()
specifier|public
name|void
name|copyNoScoresOnReworkAndCherryPick
parameter_list|()
throws|throws
name|Exception
block|{
name|codeReview
operator|.
name|setCopyAllScoresOnTrivialRebase
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|saveLabelConfig
argument_list|()
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r1
init|=
name|createChange
argument_list|()
decl_stmt|;
name|git
operator|.
name|checkout
argument_list|()
operator|.
name|setName
argument_list|(
name|r1
operator|.
name|getCommit
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|call
argument_list|()
expr_stmt|;
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
name|PushOneCommit
operator|.
name|SUBJECT
argument_list|,
literal|"b.txt"
argument_list|,
literal|"other contents"
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r2
init|=
name|push
operator|.
name|to
argument_list|(
name|git
argument_list|,
literal|"refs/for/master"
argument_list|)
decl_stmt|;
name|revision
argument_list|(
name|r2
argument_list|)
operator|.
name|review
argument_list|(
name|ReviewInput
operator|.
name|recommend
argument_list|()
argument_list|)
expr_stmt|;
name|CherryPickInput
name|in
init|=
operator|new
name|CherryPickInput
argument_list|()
decl_stmt|;
name|in
operator|.
name|destination
operator|=
literal|"master"
expr_stmt|;
name|in
operator|.
name|message
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"Cherry pick\n\nChange-Id: %s"
argument_list|,
name|r2
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|doAssertApproval
argument_list|(
literal|0
argument_list|,
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|r2
operator|.
name|getChangeId
argument_list|()
argument_list|)
operator|.
name|revision
argument_list|(
name|r2
operator|.
name|getCommit
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|cherryPick
argument_list|(
name|in
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|saveLabelConfig ()
specifier|private
name|void
name|saveLabelConfig
parameter_list|()
throws|throws
name|Exception
block|{
name|ProjectConfig
name|cfg
init|=
name|projectCache
operator|.
name|checkedGet
argument_list|(
name|allProjects
argument_list|)
operator|.
name|getConfig
argument_list|()
decl_stmt|;
name|cfg
operator|.
name|getLabelSections
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
name|cfg
operator|.
name|getLabelSections
argument_list|()
operator|.
name|put
argument_list|(
name|codeReview
operator|.
name|getName
argument_list|()
argument_list|,
name|codeReview
argument_list|)
expr_stmt|;
name|saveProjectConfig
argument_list|(
name|cfg
argument_list|)
expr_stmt|;
block|}
DECL|method|saveProjectConfig (ProjectConfig cfg)
specifier|private
name|void
name|saveProjectConfig
parameter_list|(
name|ProjectConfig
name|cfg
parameter_list|)
throws|throws
name|Exception
block|{
name|MetaDataUpdate
name|md
init|=
name|metaDataUpdateFactory
operator|.
name|create
argument_list|(
name|allProjects
argument_list|)
decl_stmt|;
try|try
block|{
name|cfg
operator|.
name|commit
argument_list|(
name|md
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|md
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|merge (PushOneCommit.Result r)
specifier|private
name|void
name|merge
parameter_list|(
name|PushOneCommit
operator|.
name|Result
name|r
parameter_list|)
throws|throws
name|Exception
block|{
name|revision
argument_list|(
name|r
argument_list|)
operator|.
name|review
argument_list|(
name|ReviewInput
operator|.
name|approve
argument_list|()
argument_list|)
expr_stmt|;
name|revision
argument_list|(
name|r
argument_list|)
operator|.
name|submit
argument_list|()
expr_stmt|;
name|Repository
name|repo
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
name|assertEquals
argument_list|(
name|r
operator|.
name|getCommitId
argument_list|()
argument_list|,
name|repo
operator|.
name|getRef
argument_list|(
literal|"refs/heads/master"
argument_list|)
operator|.
name|getObjectId
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|repo
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|rebase (PushOneCommit.Result r)
specifier|private
name|void
name|rebase
parameter_list|(
name|PushOneCommit
operator|.
name|Result
name|r
parameter_list|)
throws|throws
name|Exception
block|{
name|revision
argument_list|(
name|r
argument_list|)
operator|.
name|rebase
argument_list|()
expr_stmt|;
block|}
DECL|method|assertApproval (PushOneCommit.Result r, int expected)
specifier|private
name|void
name|assertApproval
parameter_list|(
name|PushOneCommit
operator|.
name|Result
name|r
parameter_list|,
name|int
name|expected
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Don't use asserts from PushOneCommit so we can test the round-trip
comment|// through JSON instead of querying the DB directly.
name|ChangeInfo
name|c
init|=
name|get
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
decl_stmt|;
name|doAssertApproval
argument_list|(
name|expected
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
DECL|method|doAssertApproval (int expected, ChangeInfo c)
specifier|private
name|void
name|doAssertApproval
parameter_list|(
name|int
name|expected
parameter_list|,
name|ChangeInfo
name|c
parameter_list|)
block|{
name|LabelInfo
name|cr
init|=
name|c
operator|.
name|labels
operator|.
name|get
argument_list|(
literal|"Code-Review"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
operator|(
name|int
operator|)
name|cr
operator|.
name|defaultValue
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|cr
operator|.
name|all
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Administrator"
argument_list|,
name|cr
operator|.
name|all
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|name
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|cr
operator|.
name|all
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|value
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

