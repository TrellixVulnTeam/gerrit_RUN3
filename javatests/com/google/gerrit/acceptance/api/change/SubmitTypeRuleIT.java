begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2015 The Android Open Source Project
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
DECL|package|com.google.gerrit.acceptance.api.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|api
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
name|extensions
operator|.
name|client
operator|.
name|SubmitType
operator|.
name|CHERRY_PICK
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
name|extensions
operator|.
name|client
operator|.
name|SubmitType
operator|.
name|FAST_FORWARD_ONLY
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
name|extensions
operator|.
name|client
operator|.
name|SubmitType
operator|.
name|MERGE_ALWAYS
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
name|extensions
operator|.
name|client
operator|.
name|SubmitType
operator|.
name|MERGE_IF_NECESSARY
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
name|extensions
operator|.
name|client
operator|.
name|SubmitType
operator|.
name|REBASE_ALWAYS
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
name|extensions
operator|.
name|client
operator|.
name|SubmitType
operator|.
name|REBASE_IF_NECESSARY
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
name|api
operator|.
name|projects
operator|.
name|BranchInput
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
name|SubmitType
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
name|TestSubmitRuleInfo
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
name|TestSubmitRuleInput
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
name|restapi
operator|.
name|ResourceConflictException
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
name|restapi
operator|.
name|RestApiException
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
name|RefNames
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
name|meta
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
name|meta
operator|.
name|VersionedMetaData
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
name|java
operator|.
name|io
operator|.
name|IOException
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
name|Git
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
name|lib
operator|.
name|CommitBuilder
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
DECL|class|SubmitTypeRuleIT
specifier|public
class|class
name|SubmitTypeRuleIT
extends|extends
name|AbstractDaemonTest
block|{
annotation|@
name|ConfigSuite
operator|.
name|Default
DECL|method|submitWholeTopicEnabled ()
specifier|public
specifier|static
name|Config
name|submitWholeTopicEnabled
parameter_list|()
block|{
return|return
name|submitWholeTopicEnabledConfig
argument_list|()
return|;
block|}
DECL|class|RulesPl
specifier|private
class|class
name|RulesPl
extends|extends
name|VersionedMetaData
block|{
DECL|field|FILENAME
specifier|private
specifier|static
specifier|final
name|String
name|FILENAME
init|=
literal|"rules.pl"
decl_stmt|;
DECL|field|rule
specifier|private
name|String
name|rule
decl_stmt|;
annotation|@
name|Override
DECL|method|getRefName ()
specifier|protected
name|String
name|getRefName
parameter_list|()
block|{
return|return
name|RefNames
operator|.
name|REFS_CONFIG
return|;
block|}
annotation|@
name|Override
DECL|method|onLoad ()
specifier|protected
name|void
name|onLoad
parameter_list|()
throws|throws
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|rule
operator|=
name|readUTF8
argument_list|(
name|FILENAME
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onSave (CommitBuilder commit)
specifier|protected
name|boolean
name|onSave
parameter_list|(
name|CommitBuilder
name|commit
parameter_list|)
throws|throws
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|TestSubmitRuleInput
name|in
init|=
operator|new
name|TestSubmitRuleInput
argument_list|()
decl_stmt|;
name|in
operator|.
name|rule
operator|=
name|rule
expr_stmt|;
try|try
block|{
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|testChangeId
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|current
argument_list|()
operator|.
name|testSubmitType
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RestApiException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ConfigInvalidException
argument_list|(
literal|"Invalid submit type rule"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|saveUTF8
argument_list|(
name|FILENAME
argument_list|,
name|rule
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
DECL|field|fileCounter
specifier|private
name|AtomicInteger
name|fileCounter
decl_stmt|;
DECL|field|testChangeId
specifier|private
name|Change
operator|.
name|Id
name|testChangeId
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
name|fileCounter
operator|=
operator|new
name|AtomicInteger
argument_list|()
expr_stmt|;
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|name
argument_list|(
name|project
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|branch
argument_list|(
literal|"test"
argument_list|)
operator|.
name|create
argument_list|(
operator|new
name|BranchInput
argument_list|()
argument_list|)
expr_stmt|;
name|testChangeId
operator|=
name|createChange
argument_list|(
literal|"test"
argument_list|,
literal|"test change"
argument_list|)
operator|.
name|getChange
argument_list|()
operator|.
name|getId
argument_list|()
expr_stmt|;
block|}
DECL|method|setRulesPl (String rule)
specifier|private
name|void
name|setRulesPl
parameter_list|(
name|String
name|rule
parameter_list|)
throws|throws
name|Exception
block|{
try|try
init|(
name|MetaDataUpdate
name|md
init|=
name|metaDataUpdateFactory
operator|.
name|create
argument_list|(
name|project
argument_list|)
init|)
block|{
name|RulesPl
name|r
init|=
operator|new
name|RulesPl
argument_list|()
decl_stmt|;
name|r
operator|.
name|load
argument_list|(
name|md
argument_list|)
expr_stmt|;
name|r
operator|.
name|rule
operator|=
name|rule
expr_stmt|;
name|r
operator|.
name|commit
argument_list|(
name|md
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|SUBMIT_TYPE_FROM_SUBJECT
specifier|private
specifier|static
specifier|final
name|String
name|SUBMIT_TYPE_FROM_SUBJECT
init|=
literal|"submit_type(fast_forward_only) :-"
operator|+
literal|"gerrit:commit_message(M),"
operator|+
literal|"regex_matches('.*FAST_FORWARD_ONLY.*', M),"
operator|+
literal|"!.\n"
operator|+
literal|"submit_type(merge_if_necessary) :-"
operator|+
literal|"gerrit:commit_message(M),"
operator|+
literal|"regex_matches('.*MERGE_IF_NECESSARY.*', M),"
operator|+
literal|"!.\n"
operator|+
literal|"submit_type(rebase_if_necessary) :-"
operator|+
literal|"gerrit:commit_message(M),"
operator|+
literal|"regex_matches('.*REBASE_IF_NECESSARY.*', M),"
operator|+
literal|"!.\n"
operator|+
literal|"submit_type(rebase_always) :-"
operator|+
literal|"gerrit:commit_message(M),"
operator|+
literal|"regex_matches('.*REBASE_ALWAYS.*', M),"
operator|+
literal|"!.\n"
operator|+
literal|"submit_type(merge_always) :-"
operator|+
literal|"gerrit:commit_message(M),"
operator|+
literal|"regex_matches('.*MERGE_ALWAYS.*', M),"
operator|+
literal|"!.\n"
operator|+
literal|"submit_type(cherry_pick) :-"
operator|+
literal|"gerrit:commit_message(M),"
operator|+
literal|"regex_matches('.*CHERRY_PICK.*', M),"
operator|+
literal|"!.\n"
operator|+
literal|"submit_type(T) :- gerrit:project_default_submit_type(T)."
decl_stmt|;
DECL|method|createChange (String dest, String subject)
specifier|private
name|PushOneCommit
operator|.
name|Result
name|createChange
parameter_list|(
name|String
name|dest
parameter_list|,
name|String
name|subject
parameter_list|)
throws|throws
name|Exception
block|{
name|PushOneCommit
name|push
init|=
name|pushFactory
operator|.
name|create
argument_list|(
name|admin
operator|.
name|getIdent
argument_list|()
argument_list|,
name|testRepo
argument_list|,
name|subject
argument_list|,
literal|"file"
operator|+
name|fileCounter
operator|.
name|incrementAndGet
argument_list|()
argument_list|,
name|PushOneCommit
operator|.
name|FILE_CONTENT
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
literal|"refs/for/"
operator|+
name|dest
argument_list|)
decl_stmt|;
name|r
operator|.
name|assertOkStatus
argument_list|()
expr_stmt|;
return|return
name|r
return|;
block|}
annotation|@
name|Test
DECL|method|unconditionalCherryPick ()
specifier|public
name|void
name|unconditionalCherryPick
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
name|assertSubmitType
argument_list|(
name|MERGE_IF_NECESSARY
argument_list|,
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|setRulesPl
argument_list|(
literal|"submit_type(cherry_pick)."
argument_list|)
expr_stmt|;
name|assertSubmitType
argument_list|(
name|CHERRY_PICK
argument_list|,
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|submitTypeFromSubject ()
specifier|public
name|void
name|submitTypeFromSubject
parameter_list|()
throws|throws
name|Exception
block|{
name|PushOneCommit
operator|.
name|Result
name|r1
init|=
name|createChange
argument_list|(
literal|"master"
argument_list|,
literal|"Default 1"
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r2
init|=
name|createChange
argument_list|(
literal|"master"
argument_list|,
literal|"FAST_FORWARD_ONLY 2"
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r3
init|=
name|createChange
argument_list|(
literal|"master"
argument_list|,
literal|"MERGE_IF_NECESSARY 3"
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r4
init|=
name|createChange
argument_list|(
literal|"master"
argument_list|,
literal|"REBASE_IF_NECESSARY 4"
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r5
init|=
name|createChange
argument_list|(
literal|"master"
argument_list|,
literal|"REBASE_ALWAYS 5"
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r6
init|=
name|createChange
argument_list|(
literal|"master"
argument_list|,
literal|"MERGE_ALWAYS 6"
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r7
init|=
name|createChange
argument_list|(
literal|"master"
argument_list|,
literal|"CHERRY_PICK 7"
argument_list|)
decl_stmt|;
name|assertSubmitType
argument_list|(
name|MERGE_IF_NECESSARY
argument_list|,
name|r1
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertSubmitType
argument_list|(
name|MERGE_IF_NECESSARY
argument_list|,
name|r2
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertSubmitType
argument_list|(
name|MERGE_IF_NECESSARY
argument_list|,
name|r3
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertSubmitType
argument_list|(
name|MERGE_IF_NECESSARY
argument_list|,
name|r4
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertSubmitType
argument_list|(
name|MERGE_IF_NECESSARY
argument_list|,
name|r5
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertSubmitType
argument_list|(
name|MERGE_IF_NECESSARY
argument_list|,
name|r6
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertSubmitType
argument_list|(
name|MERGE_IF_NECESSARY
argument_list|,
name|r7
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|setRulesPl
argument_list|(
name|SUBMIT_TYPE_FROM_SUBJECT
argument_list|)
expr_stmt|;
name|assertSubmitType
argument_list|(
name|MERGE_IF_NECESSARY
argument_list|,
name|r1
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertSubmitType
argument_list|(
name|FAST_FORWARD_ONLY
argument_list|,
name|r2
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertSubmitType
argument_list|(
name|MERGE_IF_NECESSARY
argument_list|,
name|r3
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertSubmitType
argument_list|(
name|REBASE_IF_NECESSARY
argument_list|,
name|r4
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertSubmitType
argument_list|(
name|REBASE_ALWAYS
argument_list|,
name|r5
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertSubmitType
argument_list|(
name|MERGE_ALWAYS
argument_list|,
name|r6
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertSubmitType
argument_list|(
name|CHERRY_PICK
argument_list|,
name|r7
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|submitTypeIsUsedForSubmit ()
specifier|public
name|void
name|submitTypeIsUsedForSubmit
parameter_list|()
throws|throws
name|Exception
block|{
name|setRulesPl
argument_list|(
name|SUBMIT_TYPE_FROM_SUBJECT
argument_list|)
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|createChange
argument_list|(
literal|"master"
argument_list|,
literal|"CHERRY_PICK 1"
argument_list|)
decl_stmt|;
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
operator|.
name|current
argument_list|()
operator|.
name|review
argument_list|(
name|ReviewInput
operator|.
name|approve
argument_list|()
argument_list|)
expr_stmt|;
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
operator|.
name|current
argument_list|()
operator|.
name|submit
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|RevCommit
argument_list|>
name|log
init|=
name|log
argument_list|(
literal|"master"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|log
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getShortMessage
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"CHERRY_PICK 1"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|log
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|isNotEqualTo
argument_list|(
name|r
operator|.
name|getCommit
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|log
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFullMessage
argument_list|()
argument_list|)
operator|.
name|contains
argument_list|(
literal|"Change-Id: "
operator|+
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|log
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFullMessage
argument_list|()
argument_list|)
operator|.
name|contains
argument_list|(
literal|"Reviewed-on: "
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|mixingSubmitTypesAcrossBranchesSucceeds ()
specifier|public
name|void
name|mixingSubmitTypesAcrossBranchesSucceeds
parameter_list|()
throws|throws
name|Exception
block|{
name|setRulesPl
argument_list|(
name|SUBMIT_TYPE_FROM_SUBJECT
argument_list|)
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r1
init|=
name|createChange
argument_list|(
literal|"master"
argument_list|,
literal|"MERGE_IF_NECESSARY 1"
argument_list|)
decl_stmt|;
name|RevCommit
name|initialCommit
init|=
name|r1
operator|.
name|getCommit
argument_list|()
operator|.
name|getParent
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|BranchInput
name|bin
init|=
operator|new
name|BranchInput
argument_list|()
decl_stmt|;
name|bin
operator|.
name|revision
operator|=
name|initialCommit
operator|.
name|name
argument_list|()
expr_stmt|;
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|name
argument_list|(
name|project
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|branch
argument_list|(
literal|"branch"
argument_list|)
operator|.
name|create
argument_list|(
name|bin
argument_list|)
expr_stmt|;
name|testRepo
operator|.
name|reset
argument_list|(
name|initialCommit
argument_list|)
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r2
init|=
name|createChange
argument_list|(
literal|"branch"
argument_list|,
literal|"MERGE_ALWAYS 1"
argument_list|)
decl_stmt|;
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|r1
operator|.
name|getChangeId
argument_list|()
argument_list|)
operator|.
name|topic
argument_list|(
name|name
argument_list|(
literal|"topic"
argument_list|)
argument_list|)
expr_stmt|;
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|r1
operator|.
name|getChangeId
argument_list|()
argument_list|)
operator|.
name|current
argument_list|()
operator|.
name|review
argument_list|(
name|ReviewInput
operator|.
name|approve
argument_list|()
argument_list|)
expr_stmt|;
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
name|topic
argument_list|(
name|name
argument_list|(
literal|"topic"
argument_list|)
argument_list|)
expr_stmt|;
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
name|current
argument_list|()
operator|.
name|review
argument_list|(
name|ReviewInput
operator|.
name|approve
argument_list|()
argument_list|)
expr_stmt|;
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
name|current
argument_list|()
operator|.
name|submit
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|log
argument_list|(
literal|"master"
argument_list|,
literal|1
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|r1
operator|.
name|getCommit
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|RevCommit
argument_list|>
name|branchLog
init|=
name|log
argument_list|(
literal|"branch"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|branchLog
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getParents
argument_list|()
argument_list|)
operator|.
name|hasLength
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|branchLog
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getParent
argument_list|(
literal|1
argument_list|)
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|r2
operator|.
name|getCommit
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|mixingSubmitTypesOnOneBranchFails ()
specifier|public
name|void
name|mixingSubmitTypesOnOneBranchFails
parameter_list|()
throws|throws
name|Exception
block|{
name|setRulesPl
argument_list|(
name|SUBMIT_TYPE_FROM_SUBJECT
argument_list|)
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r1
init|=
name|createChange
argument_list|(
literal|"master"
argument_list|,
literal|"CHERRY_PICK 1"
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r2
init|=
name|createChange
argument_list|(
literal|"master"
argument_list|,
literal|"MERGE_IF_NECESSARY 2"
argument_list|)
decl_stmt|;
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|r1
operator|.
name|getChangeId
argument_list|()
argument_list|)
operator|.
name|current
argument_list|()
operator|.
name|review
argument_list|(
name|ReviewInput
operator|.
name|approve
argument_list|()
argument_list|)
expr_stmt|;
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
name|current
argument_list|()
operator|.
name|review
argument_list|(
name|ReviewInput
operator|.
name|approve
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
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
name|current
argument_list|()
operator|.
name|submit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Expected ResourceConflictException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ResourceConflictException
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
literal|"Failed to submit 2 changes due to the following problems:\n"
operator|+
literal|"Change "
operator|+
name|r1
operator|.
name|getChange
argument_list|()
operator|.
name|getId
argument_list|()
operator|+
literal|": Change has submit type "
operator|+
literal|"CHERRY_PICK, but previously chose submit type MERGE_IF_NECESSARY "
operator|+
literal|"from change "
operator|+
name|r2
operator|.
name|getChange
argument_list|()
operator|.
name|getId
argument_list|()
operator|+
literal|" in the same batch"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|invalidSubmitRuleWithNoRulesInProject ()
specifier|public
name|void
name|invalidSubmitRuleWithNoRulesInProject
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|changeId
init|=
name|createChange
argument_list|(
literal|"master"
argument_list|,
literal|"change 1"
argument_list|)
operator|.
name|getChangeId
argument_list|()
decl_stmt|;
name|TestSubmitRuleInput
name|in
init|=
operator|new
name|TestSubmitRuleInput
argument_list|()
decl_stmt|;
name|in
operator|.
name|rule
operator|=
literal|"invalid prolog rule"
expr_stmt|;
comment|// We have no rules.pl by default. The fact that the default rules are showing up here is a bug.
name|List
argument_list|<
name|TestSubmitRuleInfo
argument_list|>
name|response
init|=
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|changeId
argument_list|)
operator|.
name|current
argument_list|()
operator|.
name|testSubmitRule
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|response
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|invalidPrologRuleInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|invalidSubmitRuleWithRulesInProject ()
specifier|public
name|void
name|invalidSubmitRuleWithRulesInProject
parameter_list|()
throws|throws
name|Exception
block|{
name|setRulesPl
argument_list|(
name|SUBMIT_TYPE_FROM_SUBJECT
argument_list|)
expr_stmt|;
name|String
name|changeId
init|=
name|createChange
argument_list|(
literal|"master"
argument_list|,
literal|"change 1"
argument_list|)
operator|.
name|getChangeId
argument_list|()
decl_stmt|;
name|TestSubmitRuleInput
name|in
init|=
operator|new
name|TestSubmitRuleInput
argument_list|()
decl_stmt|;
name|in
operator|.
name|rule
operator|=
literal|"invalid prolog rule"
expr_stmt|;
name|List
argument_list|<
name|TestSubmitRuleInfo
argument_list|>
name|response
init|=
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|changeId
argument_list|)
operator|.
name|current
argument_list|()
operator|.
name|testSubmitRule
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|response
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|invalidPrologRuleInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|invalidPrologRuleInfo ()
specifier|private
specifier|static
name|TestSubmitRuleInfo
name|invalidPrologRuleInfo
parameter_list|()
block|{
name|TestSubmitRuleInfo
name|info
init|=
operator|new
name|TestSubmitRuleInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|status
operator|=
literal|"RULE_ERROR"
expr_stmt|;
name|info
operator|.
name|errorMessage
operator|=
literal|"operator expected after expression at: invalid prolog rule end_of_file."
expr_stmt|;
return|return
name|info
return|;
block|}
DECL|method|log (String commitish, int n)
specifier|private
name|List
argument_list|<
name|RevCommit
argument_list|>
name|log
parameter_list|(
name|String
name|commitish
parameter_list|,
name|int
name|n
parameter_list|)
throws|throws
name|Exception
block|{
try|try
init|(
name|Repository
name|repo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|project
argument_list|)
init|;
name|Git
name|git
operator|=
operator|new
name|Git
argument_list|(
name|repo
argument_list|)
init|)
block|{
name|ObjectId
name|id
init|=
name|repo
operator|.
name|resolve
argument_list|(
name|commitish
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|id
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|git
operator|.
name|log
argument_list|()
operator|.
name|add
argument_list|(
name|id
argument_list|)
operator|.
name|setMaxCount
argument_list|(
name|n
argument_list|)
operator|.
name|call
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|method|assertSubmitType (SubmitType expected, String id)
specifier|private
name|void
name|assertSubmitType
parameter_list|(
name|SubmitType
name|expected
parameter_list|,
name|String
name|id
parameter_list|)
throws|throws
name|Exception
block|{
name|assertThat
argument_list|(
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|id
argument_list|)
operator|.
name|current
argument_list|()
operator|.
name|submitType
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

