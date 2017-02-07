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

begin_comment
comment|/** A comment parsed from inbound email */
end_comment

begin_class
DECL|class|MailComment
specifier|public
class|class
name|MailComment
block|{
DECL|enum|CommentType
enum|enum
name|CommentType
block|{
DECL|enumConstant|CHANGE_MESSAGE
name|CHANGE_MESSAGE
block|,
DECL|enumConstant|FILE_COMMENT
name|FILE_COMMENT
block|,
DECL|enumConstant|INLINE_COMMENT
name|INLINE_COMMENT
block|}
DECL|field|type
name|CommentType
name|type
decl_stmt|;
DECL|field|inReplyTo
name|Comment
name|inReplyTo
decl_stmt|;
DECL|field|fileName
name|String
name|fileName
decl_stmt|;
DECL|field|message
name|String
name|message
decl_stmt|;
DECL|method|MailComment ()
specifier|public
name|MailComment
parameter_list|()
block|{}
DECL|method|MailComment (String message, String fileName, Comment inReplyTo, CommentType type)
specifier|public
name|MailComment
parameter_list|(
name|String
name|message
parameter_list|,
name|String
name|fileName
parameter_list|,
name|Comment
name|inReplyTo
parameter_list|,
name|CommentType
name|type
parameter_list|)
block|{
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
name|this
operator|.
name|fileName
operator|=
name|fileName
expr_stmt|;
name|this
operator|.
name|inReplyTo
operator|=
name|inReplyTo
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
block|}
end_class

end_unit

