begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2016 The Android Open Source Project
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
DECL|package|com.google.gerrit.acceptance.ssh
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|ssh
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
name|common
operator|.
name|truth
operator|.
name|Truth
operator|.
name|assert_
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
operator|.
name|Result
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
name|UseSsh
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
name|ChangeMessageInfo
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
name|util
operator|.
name|ArrayList
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
name|Locale
import|;
end_import

begin_class
annotation|@
name|NoHttpd
annotation|@
name|UseSsh
DECL|class|AbandonRestoreIT
specifier|public
class|class
name|AbandonRestoreIT
extends|extends
name|AbstractDaemonTest
block|{
annotation|@
name|Test
DECL|method|withMessage ()
specifier|public
name|void
name|withMessage
parameter_list|()
throws|throws
name|Exception
block|{
name|Result
name|result
init|=
name|createChange
argument_list|()
decl_stmt|;
name|String
name|commit
init|=
name|result
operator|.
name|getCommit
argument_list|()
operator|.
name|name
argument_list|()
decl_stmt|;
name|executeCmd
argument_list|(
name|commit
argument_list|,
literal|"abandon"
argument_list|,
literal|"'abandon it'"
argument_list|)
expr_stmt|;
name|executeCmd
argument_list|(
name|commit
argument_list|,
literal|"restore"
argument_list|,
literal|"'restore it'"
argument_list|)
expr_stmt|;
name|assertChangeMessages
argument_list|(
name|result
operator|.
name|getChangeId
argument_list|()
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"Uploaded patch set 1."
argument_list|,
literal|"Abandoned\n\nabandon it"
argument_list|,
literal|"Restored\n\nrestore it"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|withoutMessage ()
specifier|public
name|void
name|withoutMessage
parameter_list|()
throws|throws
name|Exception
block|{
name|Result
name|result
init|=
name|createChange
argument_list|()
decl_stmt|;
name|String
name|commit
init|=
name|result
operator|.
name|getCommit
argument_list|()
operator|.
name|name
argument_list|()
decl_stmt|;
name|executeCmd
argument_list|(
name|commit
argument_list|,
literal|"abandon"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|executeCmd
argument_list|(
name|commit
argument_list|,
literal|"restore"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertChangeMessages
argument_list|(
name|result
operator|.
name|getChangeId
argument_list|()
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"Uploaded patch set 1."
argument_list|,
literal|"Abandoned"
argument_list|,
literal|"Restored"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|executeCmd (String commit, String op, String message)
specifier|private
name|void
name|executeCmd
parameter_list|(
name|String
name|commit
parameter_list|,
name|String
name|op
parameter_list|,
name|String
name|message
parameter_list|)
throws|throws
name|Exception
block|{
name|StringBuilder
name|command
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"gerrit review "
argument_list|)
operator|.
name|append
argument_list|(
name|commit
argument_list|)
operator|.
name|append
argument_list|(
literal|" --"
argument_list|)
operator|.
name|append
argument_list|(
name|op
argument_list|)
decl_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
condition|)
block|{
name|command
operator|.
name|append
argument_list|(
literal|" --message "
argument_list|)
operator|.
name|append
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
name|String
name|response
init|=
name|adminSshSession
operator|.
name|exec
argument_list|(
name|command
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|assert_
argument_list|()
operator|.
name|withFailureMessage
argument_list|(
name|adminSshSession
operator|.
name|getError
argument_list|()
argument_list|)
operator|.
name|that
argument_list|(
name|adminSshSession
operator|.
name|hasError
argument_list|()
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
argument_list|)
operator|.
name|doesNotContain
argument_list|(
literal|"error"
argument_list|)
expr_stmt|;
block|}
DECL|method|assertChangeMessages (String changeId, List<String> expected)
specifier|private
name|void
name|assertChangeMessages
parameter_list|(
name|String
name|changeId
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|expected
parameter_list|)
throws|throws
name|Exception
block|{
name|ChangeInfo
name|c
init|=
name|get
argument_list|(
name|changeId
argument_list|)
decl_stmt|;
name|Iterable
argument_list|<
name|ChangeMessageInfo
argument_list|>
name|messages
init|=
name|c
operator|.
name|messages
decl_stmt|;
name|assertThat
argument_list|(
name|messages
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|messages
argument_list|)
operator|.
name|hasSize
argument_list|(
name|expected
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|actual
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ChangeMessageInfo
name|info
range|:
name|messages
control|)
block|{
name|actual
operator|.
name|add
argument_list|(
name|info
operator|.
name|message
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|actual
argument_list|)
operator|.
name|containsExactlyElementsIn
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

