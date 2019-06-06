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
DECL|package|com.google.gerrit.server.mail.send
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
name|send
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
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
name|exceptions
operator|.
name|EmailException
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
name|RecipientType
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
name|mail
operator|.
name|Address
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
name|mail
operator|.
name|MailHeader
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|assistedinject
operator|.
name|Assisted
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|james
operator|.
name|mime4j
operator|.
name|dom
operator|.
name|field
operator|.
name|FieldName
import|;
end_import

begin_comment
comment|/** Send an email to inform users that parsing their inbound email failed. */
end_comment

begin_class
DECL|class|InboundEmailRejectionSender
specifier|public
class|class
name|InboundEmailRejectionSender
extends|extends
name|OutgoingEmail
block|{
comment|/** Used by the templating system to determine what error message should be sent */
DECL|enum|Error
specifier|public
enum|enum
name|Error
block|{
DECL|enumConstant|PARSING_ERROR
name|PARSING_ERROR
block|,
DECL|enumConstant|INACTIVE_ACCOUNT
name|INACTIVE_ACCOUNT
block|,
DECL|enumConstant|UNKNOWN_ACCOUNT
name|UNKNOWN_ACCOUNT
block|,
DECL|enumConstant|INTERNAL_EXCEPTION
name|INTERNAL_EXCEPTION
block|,
DECL|enumConstant|COMMENT_REJECTED
name|COMMENT_REJECTED
block|}
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (Address to, String threadId, Error reason)
name|InboundEmailRejectionSender
name|create
parameter_list|(
name|Address
name|to
parameter_list|,
name|String
name|threadId
parameter_list|,
name|Error
name|reason
parameter_list|)
function_decl|;
block|}
DECL|field|to
specifier|private
specifier|final
name|Address
name|to
decl_stmt|;
DECL|field|reason
specifier|private
specifier|final
name|Error
name|reason
decl_stmt|;
DECL|field|threadId
specifier|private
specifier|final
name|String
name|threadId
decl_stmt|;
annotation|@
name|Inject
DECL|method|InboundEmailRejectionSender ( EmailArguments ea, @Assisted Address to, @Assisted String threadId, @Assisted Error reason)
specifier|public
name|InboundEmailRejectionSender
parameter_list|(
name|EmailArguments
name|ea
parameter_list|,
annotation|@
name|Assisted
name|Address
name|to
parameter_list|,
annotation|@
name|Assisted
name|String
name|threadId
parameter_list|,
annotation|@
name|Assisted
name|Error
name|reason
parameter_list|)
block|{
name|super
argument_list|(
name|ea
argument_list|,
literal|"error"
argument_list|)
expr_stmt|;
name|this
operator|.
name|to
operator|=
name|requireNonNull
argument_list|(
name|to
argument_list|)
expr_stmt|;
name|this
operator|.
name|threadId
operator|=
name|requireNonNull
argument_list|(
name|threadId
argument_list|)
expr_stmt|;
name|this
operator|.
name|reason
operator|=
name|requireNonNull
argument_list|(
name|reason
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init ()
specifier|protected
name|void
name|init
parameter_list|()
throws|throws
name|EmailException
block|{
name|super
operator|.
name|init
argument_list|()
expr_stmt|;
name|setListIdHeader
argument_list|()
expr_stmt|;
name|setHeader
argument_list|(
name|FieldName
operator|.
name|SUBJECT
argument_list|,
literal|"[Gerrit Code Review] Unable to process your email"
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|RecipientType
operator|.
name|TO
argument_list|,
name|to
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|threadId
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|setHeader
argument_list|(
name|MailHeader
operator|.
name|REFERENCES
operator|.
name|fieldName
argument_list|()
argument_list|,
name|threadId
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setListIdHeader ()
specifier|private
name|void
name|setListIdHeader
parameter_list|()
block|{
comment|// Set a reasonable list id so that filters can be used to sort messages
name|setHeader
argument_list|(
literal|"List-Id"
argument_list|,
literal|"<gerrit-noreply."
operator|+
name|getGerritHost
argument_list|()
operator|+
literal|">"
argument_list|)
expr_stmt|;
if|if
condition|(
name|getSettingsUrl
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|setHeader
argument_list|(
literal|"List-Unsubscribe"
argument_list|,
literal|"<"
operator|+
name|getSettingsUrl
argument_list|()
operator|+
literal|">"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|format ()
specifier|protected
name|void
name|format
parameter_list|()
throws|throws
name|EmailException
block|{
name|appendText
argument_list|(
name|textTemplate
argument_list|(
literal|"InboundEmailRejection_"
operator|+
name|reason
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|useHtml
argument_list|()
condition|)
block|{
name|appendHtml
argument_list|(
name|soyHtmlTemplate
argument_list|(
literal|"InboundEmailRejectionHtml_"
operator|+
name|reason
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|setupSoyContext ()
specifier|protected
name|void
name|setupSoyContext
parameter_list|()
block|{
name|super
operator|.
name|setupSoyContext
argument_list|()
expr_stmt|;
name|footers
operator|.
name|add
argument_list|(
name|MailHeader
operator|.
name|MESSAGE_TYPE
operator|.
name|withDelimiter
argument_list|()
operator|+
name|messageClass
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|supportsHtml ()
specifier|protected
name|boolean
name|supportsHtml
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

