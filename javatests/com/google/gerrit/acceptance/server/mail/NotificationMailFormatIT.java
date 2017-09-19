begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2017 The Android Open Source Project
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
DECL|package|com.google.gerrit.acceptance.server.mail
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
name|mail
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
name|client
operator|.
name|GeneralPreferencesInfo
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
name|GeneralPreferencesInfo
operator|.
name|EmailFormat
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
name|FakeEmailSender
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
DECL|class|NotificationMailFormatIT
specifier|public
class|class
name|NotificationMailFormatIT
extends|extends
name|AbstractDaemonTest
block|{
annotation|@
name|Test
DECL|method|userReceivesPlaintextEmail ()
specifier|public
name|void
name|userReceivesPlaintextEmail
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Set user preference to receive only plaintext content
name|GeneralPreferencesInfo
name|i
init|=
operator|new
name|GeneralPreferencesInfo
argument_list|()
decl_stmt|;
name|i
operator|.
name|emailFormat
operator|=
name|EmailFormat
operator|.
name|PLAINTEXT
expr_stmt|;
name|gApi
operator|.
name|accounts
argument_list|()
operator|.
name|id
argument_list|(
name|admin
operator|.
name|getId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|setPreferences
argument_list|(
name|i
argument_list|)
expr_stmt|;
comment|// Create change as admin and review as user
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|createChange
argument_list|()
decl_stmt|;
name|setApiUser
argument_list|(
name|user
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
name|review
argument_list|(
name|ReviewInput
operator|.
name|recommend
argument_list|()
argument_list|)
expr_stmt|;
comment|// Check that admin has received only plaintext content
name|assertThat
argument_list|(
name|sender
operator|.
name|getMessages
argument_list|()
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|FakeEmailSender
operator|.
name|Message
name|m
init|=
name|sender
operator|.
name|getMessages
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|m
operator|.
name|body
argument_list|()
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|m
operator|.
name|htmlBody
argument_list|()
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertMailReplyTo
argument_list|(
name|m
argument_list|,
name|admin
operator|.
name|email
argument_list|)
expr_stmt|;
name|assertMailReplyTo
argument_list|(
name|m
argument_list|,
name|user
operator|.
name|email
argument_list|)
expr_stmt|;
comment|// Reset user preference
name|setApiUser
argument_list|(
name|admin
argument_list|)
expr_stmt|;
name|i
operator|.
name|emailFormat
operator|=
name|EmailFormat
operator|.
name|HTML_PLAINTEXT
expr_stmt|;
name|gApi
operator|.
name|accounts
argument_list|()
operator|.
name|id
argument_list|(
name|admin
operator|.
name|getId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|setPreferences
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|userReceivesHtmlAndPlaintextEmail ()
specifier|public
name|void
name|userReceivesHtmlAndPlaintextEmail
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Create change as admin and review as user
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|createChange
argument_list|()
decl_stmt|;
name|setApiUser
argument_list|(
name|user
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
name|review
argument_list|(
name|ReviewInput
operator|.
name|recommend
argument_list|()
argument_list|)
expr_stmt|;
comment|// Check that admin has received both HTML and plaintext content
name|assertThat
argument_list|(
name|sender
operator|.
name|getMessages
argument_list|()
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|FakeEmailSender
operator|.
name|Message
name|m
init|=
name|sender
operator|.
name|getMessages
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|m
operator|.
name|body
argument_list|()
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|m
operator|.
name|htmlBody
argument_list|()
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertMailReplyTo
argument_list|(
name|m
argument_list|,
name|admin
operator|.
name|email
argument_list|)
expr_stmt|;
name|assertMailReplyTo
argument_list|(
name|m
argument_list|,
name|user
operator|.
name|email
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

