begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2009 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.mail
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
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|common
operator|.
name|errors
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
name|server
operator|.
name|IdentifiedUser
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

begin_class
DECL|class|RegisterNewEmailSender
specifier|public
class|class
name|RegisterNewEmailSender
extends|extends
name|OutgoingEmail
block|{
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (String address)
name|RegisterNewEmailSender
name|create
parameter_list|(
name|String
name|address
parameter_list|)
function_decl|;
block|}
DECL|field|tokenVerifier
specifier|private
specifier|final
name|EmailTokenVerifier
name|tokenVerifier
decl_stmt|;
DECL|field|user
specifier|private
specifier|final
name|IdentifiedUser
name|user
decl_stmt|;
DECL|field|addr
specifier|private
specifier|final
name|String
name|addr
decl_stmt|;
DECL|field|emailToken
specifier|private
name|String
name|emailToken
decl_stmt|;
annotation|@
name|Inject
DECL|method|RegisterNewEmailSender (EmailArguments ea, EmailTokenVerifier etv, IdentifiedUser callingUser, @Assisted final String address)
specifier|public
name|RegisterNewEmailSender
parameter_list|(
name|EmailArguments
name|ea
parameter_list|,
name|EmailTokenVerifier
name|etv
parameter_list|,
name|IdentifiedUser
name|callingUser
parameter_list|,
annotation|@
name|Assisted
specifier|final
name|String
name|address
parameter_list|)
block|{
name|super
argument_list|(
name|ea
argument_list|,
literal|"registernewemail"
argument_list|)
expr_stmt|;
name|tokenVerifier
operator|=
name|etv
expr_stmt|;
name|user
operator|=
name|callingUser
expr_stmt|;
name|addr
operator|=
name|address
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
name|setHeader
argument_list|(
literal|"Subject"
argument_list|,
literal|"[Gerrit Code Review] Email Verification"
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|RecipientType
operator|.
name|TO
argument_list|,
operator|new
name|Address
argument_list|(
name|addr
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|shouldSendMessage ()
specifier|protected
name|boolean
name|shouldSendMessage
parameter_list|()
block|{
return|return
literal|true
return|;
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
name|velocifyFile
argument_list|(
literal|"RegisterNewEmail.vm"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getUserNameEmail ()
specifier|public
name|String
name|getUserNameEmail
parameter_list|()
block|{
return|return
name|getUserNameEmailFor
argument_list|(
name|user
operator|.
name|getAccountId
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getEmailRegistrationToken ()
specifier|public
name|String
name|getEmailRegistrationToken
parameter_list|()
block|{
if|if
condition|(
name|emailToken
operator|==
literal|null
condition|)
block|{
name|emailToken
operator|=
name|checkNotNull
argument_list|(
name|tokenVerifier
operator|.
name|encode
argument_list|(
name|user
operator|.
name|getAccountId
argument_list|()
argument_list|,
name|addr
argument_list|)
argument_list|,
literal|"token"
argument_list|)
expr_stmt|;
block|}
return|return
name|emailToken
return|;
block|}
block|}
end_class

end_unit

