begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2018 The Android Open Source Project
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
DECL|package|com.google.gerrit.acceptance.server.rules
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
name|rules
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
name|project
operator|.
name|SubmitRuleEvaluator
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
name|SubmitRuleOptions
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
name|inject
operator|.
name|Inject
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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

begin_comment
comment|/**  * Tests the Prolog rules to make sure they work even when the change and account indexes are not  * available.  */
end_comment

begin_class
annotation|@
name|NoHttpd
DECL|class|RulesIT
specifier|public
class|class
name|RulesIT
extends|extends
name|AbstractDaemonTest
block|{
DECL|field|RULE_TEMPLATE
specifier|private
specifier|static
specifier|final
name|String
name|RULE_TEMPLATE
init|=
literal|"submit_rule(submit(W)) :- \n"
operator|+
literal|"%s,\n"
operator|+
literal|"W = label('OK', ok(user(1000000)))."
decl_stmt|;
DECL|field|evaluatorFactory
annotation|@
name|Inject
specifier|private
name|SubmitRuleEvaluator
operator|.
name|Factory
name|evaluatorFactory
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
block|{
comment|// We don't want caches to interfere with our tests. If we didn't, the cache would take
comment|// precedence over the index, which would never be called.
name|baseConfig
operator|.
name|setString
argument_list|(
literal|"cache"
argument_list|,
literal|"changes"
argument_list|,
literal|"memoryLimit"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|baseConfig
operator|.
name|setString
argument_list|(
literal|"cache"
argument_list|,
literal|"projects"
argument_list|,
literal|"memoryLimit"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUnresolvedCommentsCountPredicate ()
specifier|public
name|void
name|testUnresolvedCommentsCountPredicate
parameter_list|()
throws|throws
name|Exception
block|{
name|modifySubmitRules
argument_list|(
literal|"gerrit:unresolved_comments_count(0)"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|statusForRule
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|SubmitRecord
operator|.
name|Status
operator|.
name|OK
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUploaderPredicate ()
specifier|public
name|void
name|testUploaderPredicate
parameter_list|()
throws|throws
name|Exception
block|{
name|modifySubmitRules
argument_list|(
literal|"gerrit:uploader(U)"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|statusForRule
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|SubmitRecord
operator|.
name|Status
operator|.
name|OK
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUnresolvedCommentsCount ()
specifier|public
name|void
name|testUnresolvedCommentsCount
parameter_list|()
throws|throws
name|Exception
block|{
name|modifySubmitRules
argument_list|(
literal|"gerrit:commit_message_matches('.*')"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|statusForRule
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|SubmitRecord
operator|.
name|Status
operator|.
name|OK
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUserPredicate ()
specifier|public
name|void
name|testUserPredicate
parameter_list|()
throws|throws
name|Exception
block|{
name|modifySubmitRules
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"gerrit:commit_author(user(%d), '%s', '%s')"
argument_list|,
name|user
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|user
operator|.
name|fullName
argument_list|,
name|user
operator|.
name|email
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|statusForRule
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|SubmitRecord
operator|.
name|Status
operator|.
name|OK
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCommitAuthorPredicate ()
specifier|public
name|void
name|testCommitAuthorPredicate
parameter_list|()
throws|throws
name|Exception
block|{
name|modifySubmitRules
argument_list|(
literal|"gerrit:commit_author(Id)"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|statusForRule
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|SubmitRecord
operator|.
name|Status
operator|.
name|OK
argument_list|)
expr_stmt|;
block|}
DECL|method|statusForRule ()
specifier|private
name|SubmitRecord
operator|.
name|Status
name|statusForRule
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|oldHead
init|=
name|getRemoteHead
argument_list|()
operator|.
name|name
argument_list|()
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|result1
init|=
name|pushFactory
operator|.
name|create
argument_list|(
name|db
argument_list|,
name|user
operator|.
name|getIdent
argument_list|()
argument_list|,
name|testRepo
argument_list|)
operator|.
name|to
argument_list|(
literal|"refs/for/master"
argument_list|)
decl_stmt|;
name|testRepo
operator|.
name|reset
argument_list|(
name|oldHead
argument_list|)
expr_stmt|;
name|ChangeData
name|cd
init|=
name|result1
operator|.
name|getChange
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|SubmitRecord
argument_list|>
name|records
decl_stmt|;
try|try
init|(
name|AutoCloseable
name|changeIndex
init|=
name|disableChangeIndex
argument_list|()
init|)
block|{
try|try
init|(
name|AutoCloseable
name|accountIndex
init|=
name|disableAccountIndex
argument_list|()
init|)
block|{
name|SubmitRuleEvaluator
name|ruleEvaluator
init|=
name|evaluatorFactory
operator|.
name|create
argument_list|(
name|SubmitRuleOptions
operator|.
name|defaults
argument_list|()
argument_list|)
decl_stmt|;
name|records
operator|=
name|ruleEvaluator
operator|.
name|evaluate
argument_list|(
name|cd
argument_list|)
expr_stmt|;
block|}
block|}
name|assertThat
argument_list|(
name|records
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|SubmitRecord
name|record
init|=
name|records
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
return|return
name|record
operator|.
name|status
return|;
block|}
DECL|method|modifySubmitRules (String ruleTested)
specifier|private
name|void
name|modifySubmitRules
parameter_list|(
name|String
name|ruleTested
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|newContent
init|=
name|String
operator|.
name|format
argument_list|(
name|RULE_TEMPLATE
argument_list|,
name|ruleTested
argument_list|)
decl_stmt|;
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
init|)
block|{
name|TestRepository
argument_list|<
name|?
argument_list|>
name|testRepo
init|=
operator|new
name|TestRepository
argument_list|<>
argument_list|(
operator|(
name|InMemoryRepository
operator|)
name|repo
argument_list|)
decl_stmt|;
name|testRepo
operator|.
name|branch
argument_list|(
name|RefNames
operator|.
name|REFS_CONFIG
argument_list|)
operator|.
name|commit
argument_list|()
operator|.
name|author
argument_list|(
name|admin
operator|.
name|getIdent
argument_list|()
argument_list|)
operator|.
name|committer
argument_list|(
name|admin
operator|.
name|getIdent
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
literal|"rules.pl"
argument_list|,
name|newContent
argument_list|)
operator|.
name|message
argument_list|(
literal|"Modify rules.pl"
argument_list|)
operator|.
name|create
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

