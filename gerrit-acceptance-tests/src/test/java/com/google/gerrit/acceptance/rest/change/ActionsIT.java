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
DECL|package|com.google.gerrit.acceptance.rest.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|rest
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
name|acceptance
operator|.
name|RestResponse
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
name|RestSession
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
name|ActionInfo
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
name|gson
operator|.
name|reflect
operator|.
name|TypeToken
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpStatus
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
name|lib
operator|.
name|Config
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_class
DECL|class|ActionsIT
specifier|public
class|class
name|ActionsIT
extends|extends
name|AbstractDaemonTest
block|{
annotation|@
name|ConfigSuite
operator|.
name|Config
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
annotation|@
name|Test
DECL|method|revisionActionsOneChangePerTopicUnapproved ()
specifier|public
name|void
name|revisionActionsOneChangePerTopicUnapproved
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|changeId
init|=
name|createChangeWithTopic
argument_list|(
literal|"foo1"
argument_list|)
operator|.
name|getChangeId
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|ActionInfo
argument_list|>
name|actions
init|=
name|getActions
argument_list|(
name|changeId
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|actions
argument_list|)
operator|.
name|containsKey
argument_list|(
literal|"cherrypick"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|actions
argument_list|)
operator|.
name|containsKey
argument_list|(
literal|"rebase"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|actions
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|revisionActionsOneChangePerTopic ()
specifier|public
name|void
name|revisionActionsOneChangePerTopic
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|changeId
init|=
name|createChangeWithTopic
argument_list|(
literal|"foo1"
argument_list|)
operator|.
name|getChangeId
argument_list|()
decl_stmt|;
name|approve
argument_list|(
name|changeId
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|ActionInfo
argument_list|>
name|actions
init|=
name|getActions
argument_list|(
name|changeId
argument_list|)
decl_stmt|;
name|commonActionsAssertions
argument_list|(
name|actions
argument_list|)
expr_stmt|;
comment|// We want to treat a single change in a topic not as a whole topic,
comment|// so regardless of how submitWholeTopic is configured:
name|noSubmitWholeTopicAssertions
argument_list|(
name|actions
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|revisionActionsTwoChangeChangesInTopic ()
specifier|public
name|void
name|revisionActionsTwoChangeChangesInTopic
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|changeId
init|=
name|createChangeWithTopic
argument_list|(
literal|"foo2"
argument_list|)
operator|.
name|getChangeId
argument_list|()
decl_stmt|;
name|approve
argument_list|(
name|changeId
argument_list|)
expr_stmt|;
comment|// create another change with the same topic
name|createChangeWithTopic
argument_list|(
literal|"foo2"
argument_list|)
operator|.
name|getChangeId
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|ActionInfo
argument_list|>
name|actions
init|=
name|getActions
argument_list|(
name|changeId
argument_list|)
decl_stmt|;
name|commonActionsAssertions
argument_list|(
name|actions
argument_list|)
expr_stmt|;
if|if
condition|(
name|isSubmitWholeTopicEnabled
argument_list|()
condition|)
block|{
name|ActionInfo
name|info
init|=
name|actions
operator|.
name|get
argument_list|(
literal|"submit"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|enabled
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|label
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"Submit whole topic"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|method
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"POST"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|title
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"Other changes in this topic are not ready"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|noSubmitWholeTopicAssertions
argument_list|(
name|actions
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|revisionActionsTwoChangeChangesInTopicReady ()
specifier|public
name|void
name|revisionActionsTwoChangeChangesInTopicReady
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|changeId
init|=
name|createChangeWithTopic
argument_list|(
literal|"foo2"
argument_list|)
operator|.
name|getChangeId
argument_list|()
decl_stmt|;
name|approve
argument_list|(
name|changeId
argument_list|)
expr_stmt|;
comment|// create another change with the same topic
name|String
name|changeId2
init|=
name|createChangeWithTopic
argument_list|(
literal|"foo2"
argument_list|)
operator|.
name|getChangeId
argument_list|()
decl_stmt|;
name|approve
argument_list|(
name|changeId2
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|ActionInfo
argument_list|>
name|actions
init|=
name|getActions
argument_list|(
name|changeId
argument_list|)
decl_stmt|;
name|commonActionsAssertions
argument_list|(
name|actions
argument_list|)
expr_stmt|;
if|if
condition|(
name|isSubmitWholeTopicEnabled
argument_list|()
condition|)
block|{
name|ActionInfo
name|info
init|=
name|actions
operator|.
name|get
argument_list|(
literal|"submit"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|enabled
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|label
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"Submit whole topic"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|method
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"POST"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|title
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"Submit all 2 changes of the same topic"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|noSubmitWholeTopicAssertions
argument_list|(
name|actions
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getActions (RestSession adminSession, String changeId)
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|ActionInfo
argument_list|>
name|getActions
parameter_list|(
name|RestSession
name|adminSession
parameter_list|,
name|String
name|changeId
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|newGson
argument_list|()
operator|.
name|fromJson
argument_list|(
name|adminSession
operator|.
name|get
argument_list|(
literal|"/changes/"
operator|+
name|changeId
operator|+
literal|"/revisions/1/actions"
argument_list|)
operator|.
name|getReader
argument_list|()
argument_list|,
operator|new
name|TypeToken
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|ActionInfo
argument_list|>
argument_list|>
argument_list|()
block|{}
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getActions (String changeId)
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|ActionInfo
argument_list|>
name|getActions
parameter_list|(
name|String
name|changeId
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getActions
argument_list|(
name|adminSession
argument_list|,
name|changeId
argument_list|)
return|;
block|}
DECL|method|noSubmitWholeTopicAssertions (Map<String, ActionInfo> actions)
specifier|private
name|void
name|noSubmitWholeTopicAssertions
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|ActionInfo
argument_list|>
name|actions
parameter_list|)
block|{
name|ActionInfo
name|info
init|=
name|actions
operator|.
name|get
argument_list|(
literal|"submit"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|enabled
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|label
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"Submit"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|method
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"POST"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|title
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"Submit patch set 1 into master"
argument_list|)
expr_stmt|;
block|}
DECL|method|commonActionsAssertions (Map<String, ActionInfo> actions)
specifier|private
name|void
name|commonActionsAssertions
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|ActionInfo
argument_list|>
name|actions
parameter_list|)
block|{
name|assertThat
argument_list|(
name|actions
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|actions
argument_list|)
operator|.
name|containsKey
argument_list|(
literal|"cherrypick"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|actions
argument_list|)
operator|.
name|containsKey
argument_list|(
literal|"submit"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|actions
argument_list|)
operator|.
name|containsKey
argument_list|(
literal|"rebase"
argument_list|)
expr_stmt|;
block|}
DECL|method|createChangeWithTopic (String topic)
specifier|private
name|PushOneCommit
operator|.
name|Result
name|createChangeWithTopic
parameter_list|(
name|String
name|topic
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
name|assertThat
argument_list|(
name|topic
argument_list|)
operator|.
name|isNotEmpty
argument_list|()
expr_stmt|;
return|return
name|push
operator|.
name|to
argument_list|(
name|git
argument_list|,
literal|"refs/for/master/"
operator|+
name|topic
argument_list|)
return|;
block|}
DECL|method|approve (RestSession adminSession, String changeId)
specifier|static
name|void
name|approve
parameter_list|(
name|RestSession
name|adminSession
parameter_list|,
name|String
name|changeId
parameter_list|)
throws|throws
name|IOException
block|{
name|RestResponse
name|r
init|=
name|adminSession
operator|.
name|post
argument_list|(
literal|"/changes/"
operator|+
name|changeId
operator|+
literal|"/revisions/current/review"
argument_list|,
operator|new
name|ReviewInput
argument_list|()
operator|.
name|label
argument_list|(
literal|"Code-Review"
argument_list|,
literal|2
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getStatusCode
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|HttpStatus
operator|.
name|SC_OK
argument_list|)
expr_stmt|;
name|r
operator|.
name|consume
argument_list|()
expr_stmt|;
block|}
DECL|method|approve (String changeId)
specifier|private
name|void
name|approve
parameter_list|(
name|String
name|changeId
parameter_list|)
throws|throws
name|IOException
block|{
name|approve
argument_list|(
name|adminSession
argument_list|,
name|changeId
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

