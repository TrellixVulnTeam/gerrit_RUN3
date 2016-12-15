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
DECL|package|com.google.gerrit.server.mail.receive
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|mail
operator|.
name|receive
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
name|Comment
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
name|mail
operator|.
name|Address
import|;
end_import

begin_import
import|import
name|org
operator|.
name|joda
operator|.
name|time
operator|.
name|DateTime
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Timestamp
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

begin_class
annotation|@
name|Ignore
DECL|class|AbstractParserTest
specifier|public
class|class
name|AbstractParserTest
block|{
DECL|field|changeURL
specifier|protected
specifier|static
specifier|final
name|String
name|changeURL
init|=
literal|"https://gerrit-review.googlesource.com/#/changes/123"
decl_stmt|;
DECL|method|assertChangeMessage (String message, MailComment comment)
specifier|protected
specifier|static
name|void
name|assertChangeMessage
parameter_list|(
name|String
name|message
parameter_list|,
name|MailComment
name|comment
parameter_list|)
block|{
name|assertThat
argument_list|(
name|comment
operator|.
name|fileName
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|comment
operator|.
name|message
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|comment
operator|.
name|inReplyTo
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|comment
operator|.
name|type
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|MailComment
operator|.
name|CommentType
operator|.
name|CHANGE_MESSAGE
argument_list|)
expr_stmt|;
block|}
DECL|method|assertInlineComment (String message, MailComment comment, Comment inReplyTo)
specifier|protected
specifier|static
name|void
name|assertInlineComment
parameter_list|(
name|String
name|message
parameter_list|,
name|MailComment
name|comment
parameter_list|,
name|Comment
name|inReplyTo
parameter_list|)
block|{
name|assertThat
argument_list|(
name|comment
operator|.
name|fileName
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|comment
operator|.
name|message
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|comment
operator|.
name|inReplyTo
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|inReplyTo
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|comment
operator|.
name|type
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|MailComment
operator|.
name|CommentType
operator|.
name|INLINE_COMMENT
argument_list|)
expr_stmt|;
block|}
DECL|method|assertFileComment (String message, MailComment comment, String file)
specifier|protected
specifier|static
name|void
name|assertFileComment
parameter_list|(
name|String
name|message
parameter_list|,
name|MailComment
name|comment
parameter_list|,
name|String
name|file
parameter_list|)
block|{
name|assertThat
argument_list|(
name|comment
operator|.
name|fileName
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|comment
operator|.
name|message
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|comment
operator|.
name|inReplyTo
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|comment
operator|.
name|type
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|MailComment
operator|.
name|CommentType
operator|.
name|FILE_COMMENT
argument_list|)
expr_stmt|;
block|}
DECL|method|newComment (String uuid, String file, String message, int line)
specifier|protected
specifier|static
name|Comment
name|newComment
parameter_list|(
name|String
name|uuid
parameter_list|,
name|String
name|file
parameter_list|,
name|String
name|message
parameter_list|,
name|int
name|line
parameter_list|)
block|{
name|Comment
name|c
init|=
operator|new
name|Comment
argument_list|(
operator|new
name|Comment
operator|.
name|Key
argument_list|(
name|uuid
argument_list|,
name|file
argument_list|,
literal|1
argument_list|)
argument_list|,
operator|new
name|Account
operator|.
name|Id
argument_list|(
literal|0
argument_list|)
argument_list|,
operator|new
name|Timestamp
argument_list|(
literal|0L
argument_list|)
argument_list|,
operator|(
name|short
operator|)
literal|0
argument_list|,
name|message
argument_list|,
literal|""
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|c
operator|.
name|lineNbr
operator|=
name|line
expr_stmt|;
return|return
name|c
return|;
block|}
comment|/** Returns a MailMessage.Builder with all required fields populated. */
DECL|method|newMailMessageBuilder ()
specifier|protected
specifier|static
name|MailMessage
operator|.
name|Builder
name|newMailMessageBuilder
parameter_list|()
block|{
name|MailMessage
operator|.
name|Builder
name|b
init|=
name|MailMessage
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b
operator|.
name|id
argument_list|(
literal|"id"
argument_list|)
expr_stmt|;
name|b
operator|.
name|from
argument_list|(
operator|new
name|Address
argument_list|(
literal|"Foo Bar"
argument_list|,
literal|"foo@bar.com"
argument_list|)
argument_list|)
expr_stmt|;
name|b
operator|.
name|dateReceived
argument_list|(
operator|new
name|DateTime
argument_list|()
argument_list|)
expr_stmt|;
name|b
operator|.
name|subject
argument_list|(
literal|""
argument_list|)
expr_stmt|;
return|return
name|b
return|;
block|}
comment|/** Returns a List of default comments for testing. */
DECL|method|defaultComments ()
specifier|protected
specifier|static
name|List
argument_list|<
name|Comment
argument_list|>
name|defaultComments
parameter_list|()
block|{
name|List
argument_list|<
name|Comment
argument_list|>
name|comments
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|comments
operator|.
name|add
argument_list|(
name|newComment
argument_list|(
literal|"c1"
argument_list|,
literal|"gerrit-server/test.txt"
argument_list|,
literal|"comment"
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|comments
operator|.
name|add
argument_list|(
name|newComment
argument_list|(
literal|"c2"
argument_list|,
literal|"gerrit-server/test.txt"
argument_list|,
literal|"comment"
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|comments
operator|.
name|add
argument_list|(
name|newComment
argument_list|(
literal|"c3"
argument_list|,
literal|"gerrit-server/test.txt"
argument_list|,
literal|"comment"
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|comments
operator|.
name|add
argument_list|(
name|newComment
argument_list|(
literal|"c4"
argument_list|,
literal|"gerrit-server/readme.txt"
argument_list|,
literal|"comment"
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|comments
return|;
block|}
block|}
end_class

end_unit

