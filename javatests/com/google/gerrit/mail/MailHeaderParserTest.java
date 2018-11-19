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
DECL|package|com.google.gerrit.mail
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
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
name|testing
operator|.
name|GerritBaseTests
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|Instant
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|LocalDateTime
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|Month
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|ZoneOffset
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
DECL|class|MailHeaderParserTest
specifier|public
class|class
name|MailHeaderParserTest
extends|extends
name|GerritBaseTests
block|{
annotation|@
name|Test
DECL|method|parseMetadataFromHeader ()
specifier|public
name|void
name|parseMetadataFromHeader
parameter_list|()
block|{
comment|// This tests if the metadata parser is able to parse metadata from the
comment|// email headers of the message.
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
literal|""
argument_list|)
expr_stmt|;
name|b
operator|.
name|dateReceived
argument_list|(
name|Instant
operator|.
name|now
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
name|b
operator|.
name|addAdditionalHeader
argument_list|(
name|MailHeader
operator|.
name|CHANGE_NUMBER
operator|.
name|fieldWithDelimiter
argument_list|()
operator|+
literal|"123"
argument_list|)
expr_stmt|;
name|b
operator|.
name|addAdditionalHeader
argument_list|(
name|MailHeader
operator|.
name|PATCH_SET
operator|.
name|fieldWithDelimiter
argument_list|()
operator|+
literal|"1"
argument_list|)
expr_stmt|;
name|b
operator|.
name|addAdditionalHeader
argument_list|(
name|MailHeader
operator|.
name|MESSAGE_TYPE
operator|.
name|fieldWithDelimiter
argument_list|()
operator|+
literal|"comment"
argument_list|)
expr_stmt|;
name|b
operator|.
name|addAdditionalHeader
argument_list|(
name|MailHeader
operator|.
name|COMMENT_DATE
operator|.
name|fieldWithDelimiter
argument_list|()
operator|+
literal|"Tue, 25 Oct 2016 02:11:35 -0700"
argument_list|)
expr_stmt|;
name|Address
name|author
init|=
operator|new
name|Address
argument_list|(
literal|"Diffy"
argument_list|,
literal|"test@gerritcodereview.com"
argument_list|)
decl_stmt|;
name|b
operator|.
name|from
argument_list|(
name|author
argument_list|)
expr_stmt|;
name|MailMetadata
name|meta
init|=
name|MailHeaderParser
operator|.
name|parse
argument_list|(
name|b
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|meta
operator|.
name|author
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|author
operator|.
name|getEmail
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|meta
operator|.
name|changeNumber
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|123
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|meta
operator|.
name|patchSet
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|meta
operator|.
name|messageType
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"comment"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|meta
operator|.
name|timestamp
operator|.
name|toInstant
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|LocalDateTime
operator|.
name|of
argument_list|(
literal|2016
argument_list|,
name|Month
operator|.
name|OCTOBER
argument_list|,
literal|25
argument_list|,
literal|9
argument_list|,
literal|11
argument_list|,
literal|35
argument_list|)
operator|.
name|atOffset
argument_list|(
name|ZoneOffset
operator|.
name|UTC
argument_list|)
operator|.
name|toInstant
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|parseMetadataFromText ()
specifier|public
name|void
name|parseMetadataFromText
parameter_list|()
block|{
comment|// This tests if the metadata parser is able to parse metadata from the
comment|// the text body of the message.
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
literal|""
argument_list|)
expr_stmt|;
name|b
operator|.
name|dateReceived
argument_list|(
name|Instant
operator|.
name|now
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
name|StringBuilder
name|stringBuilder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|stringBuilder
operator|.
name|append
argument_list|(
name|MailHeader
operator|.
name|CHANGE_NUMBER
operator|.
name|withDelimiter
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"123\r\n"
argument_list|)
expr_stmt|;
name|stringBuilder
operator|.
name|append
argument_list|(
literal|"> "
argument_list|)
operator|.
name|append
argument_list|(
name|MailHeader
operator|.
name|PATCH_SET
operator|.
name|withDelimiter
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"1\n"
argument_list|)
expr_stmt|;
name|stringBuilder
operator|.
name|append
argument_list|(
name|MailHeader
operator|.
name|MESSAGE_TYPE
operator|.
name|withDelimiter
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"comment\n"
argument_list|)
expr_stmt|;
name|stringBuilder
operator|.
name|append
argument_list|(
name|MailHeader
operator|.
name|COMMENT_DATE
operator|.
name|withDelimiter
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"Tue, 25 Oct 2016 02:11:35 -0700\r\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|textContent
argument_list|(
name|stringBuilder
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Address
name|author
init|=
operator|new
name|Address
argument_list|(
literal|"Diffy"
argument_list|,
literal|"test@gerritcodereview.com"
argument_list|)
decl_stmt|;
name|b
operator|.
name|from
argument_list|(
name|author
argument_list|)
expr_stmt|;
name|MailMetadata
name|meta
init|=
name|MailHeaderParser
operator|.
name|parse
argument_list|(
name|b
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|meta
operator|.
name|author
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|author
operator|.
name|getEmail
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|meta
operator|.
name|changeNumber
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|123
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|meta
operator|.
name|patchSet
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|meta
operator|.
name|messageType
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"comment"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|meta
operator|.
name|timestamp
operator|.
name|toInstant
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|LocalDateTime
operator|.
name|of
argument_list|(
literal|2016
argument_list|,
name|Month
operator|.
name|OCTOBER
argument_list|,
literal|25
argument_list|,
literal|9
argument_list|,
literal|11
argument_list|,
literal|35
argument_list|)
operator|.
name|atOffset
argument_list|(
name|ZoneOffset
operator|.
name|UTC
argument_list|)
operator|.
name|toInstant
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|parseMetadataFromHTML ()
specifier|public
name|void
name|parseMetadataFromHTML
parameter_list|()
block|{
comment|// This tests if the metadata parser is able to parse metadata from the
comment|// the HTML body of the message.
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
literal|""
argument_list|)
expr_stmt|;
name|b
operator|.
name|dateReceived
argument_list|(
name|Instant
operator|.
name|now
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
name|StringBuilder
name|stringBuilder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|stringBuilder
operator|.
name|append
argument_list|(
literal|"<div id\"someid\">"
argument_list|)
operator|.
name|append
argument_list|(
name|MailHeader
operator|.
name|CHANGE_NUMBER
operator|.
name|withDelimiter
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"123</div>"
argument_list|)
expr_stmt|;
name|stringBuilder
operator|.
name|append
argument_list|(
literal|"<div>"
argument_list|)
operator|.
name|append
argument_list|(
name|MailHeader
operator|.
name|PATCH_SET
operator|.
name|withDelimiter
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"1</div>"
argument_list|)
expr_stmt|;
name|stringBuilder
operator|.
name|append
argument_list|(
literal|"<div>"
argument_list|)
operator|.
name|append
argument_list|(
name|MailHeader
operator|.
name|MESSAGE_TYPE
operator|.
name|withDelimiter
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"comment</div>"
argument_list|)
expr_stmt|;
name|stringBuilder
operator|.
name|append
argument_list|(
literal|"<div>"
argument_list|)
operator|.
name|append
argument_list|(
name|MailHeader
operator|.
name|COMMENT_DATE
operator|.
name|withDelimiter
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"Tue, 25 Oct 2016 02:11:35 -0700"
argument_list|)
operator|.
name|append
argument_list|(
literal|"</div>"
argument_list|)
expr_stmt|;
name|b
operator|.
name|htmlContent
argument_list|(
name|stringBuilder
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Address
name|author
init|=
operator|new
name|Address
argument_list|(
literal|"Diffy"
argument_list|,
literal|"test@gerritcodereview.com"
argument_list|)
decl_stmt|;
name|b
operator|.
name|from
argument_list|(
name|author
argument_list|)
expr_stmt|;
name|MailMetadata
name|meta
init|=
name|MailHeaderParser
operator|.
name|parse
argument_list|(
name|b
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|meta
operator|.
name|author
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|author
operator|.
name|getEmail
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|meta
operator|.
name|changeNumber
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|123
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|meta
operator|.
name|patchSet
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|meta
operator|.
name|messageType
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"comment"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|meta
operator|.
name|timestamp
operator|.
name|toInstant
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|LocalDateTime
operator|.
name|of
argument_list|(
literal|2016
argument_list|,
name|Month
operator|.
name|OCTOBER
argument_list|,
literal|25
argument_list|,
literal|9
argument_list|,
literal|11
argument_list|,
literal|35
argument_list|)
operator|.
name|atOffset
argument_list|(
name|ZoneOffset
operator|.
name|UTC
argument_list|)
operator|.
name|toInstant
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

