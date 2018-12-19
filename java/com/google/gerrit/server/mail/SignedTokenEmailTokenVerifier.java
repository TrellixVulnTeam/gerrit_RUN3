begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2012 The Android Open Source Project
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
name|checkState
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
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
name|server
operator|.
name|config
operator|.
name|AuthConfig
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
name|send
operator|.
name|RegisterNewEmailSender
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
name|AbstractModule
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
name|Singleton
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|util
operator|.
name|Base64
import|;
end_import

begin_comment
comment|/** Verifies the token sent by {@link RegisterNewEmailSender}. */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|SignedTokenEmailTokenVerifier
specifier|public
class|class
name|SignedTokenEmailTokenVerifier
implements|implements
name|EmailTokenVerifier
block|{
DECL|field|emailRegistrationToken
specifier|private
specifier|final
name|SignedToken
name|emailRegistrationToken
decl_stmt|;
DECL|class|Module
specifier|public
specifier|static
class|class
name|Module
extends|extends
name|AbstractModule
block|{
annotation|@
name|Override
DECL|method|configure ()
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|bind
argument_list|(
name|EmailTokenVerifier
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|SignedTokenEmailTokenVerifier
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Inject
DECL|method|SignedTokenEmailTokenVerifier (AuthConfig config)
name|SignedTokenEmailTokenVerifier
parameter_list|(
name|AuthConfig
name|config
parameter_list|)
block|{
name|emailRegistrationToken
operator|=
name|config
operator|.
name|getEmailRegistrationToken
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|encode (Account.Id accountId, String emailAddress)
specifier|public
name|String
name|encode
parameter_list|(
name|Account
operator|.
name|Id
name|accountId
parameter_list|,
name|String
name|emailAddress
parameter_list|)
block|{
name|checkEmailRegistrationToken
argument_list|()
expr_stmt|;
try|try
block|{
name|String
name|payload
init|=
name|String
operator|.
name|format
argument_list|(
literal|"%s:%s"
argument_list|,
name|accountId
argument_list|,
name|emailAddress
argument_list|)
decl_stmt|;
name|byte
index|[]
name|utf8
init|=
name|payload
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
decl_stmt|;
name|String
name|base64
init|=
name|Base64
operator|.
name|encodeBytes
argument_list|(
name|utf8
argument_list|)
decl_stmt|;
return|return
name|emailRegistrationToken
operator|.
name|newToken
argument_list|(
name|base64
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|XsrfException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|decode (String tokenString)
specifier|public
name|ParsedToken
name|decode
parameter_list|(
name|String
name|tokenString
parameter_list|)
throws|throws
name|InvalidTokenException
block|{
name|checkEmailRegistrationToken
argument_list|()
expr_stmt|;
name|ValidToken
name|token
decl_stmt|;
try|try
block|{
name|token
operator|=
name|emailRegistrationToken
operator|.
name|checkToken
argument_list|(
name|tokenString
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XsrfException
name|err
parameter_list|)
block|{
throw|throw
operator|new
name|InvalidTokenException
argument_list|(
name|err
argument_list|)
throw|;
block|}
if|if
condition|(
name|token
operator|==
literal|null
operator|||
name|token
operator|.
name|getData
argument_list|()
operator|==
literal|null
operator|||
name|token
operator|.
name|getData
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|InvalidTokenException
argument_list|()
throw|;
block|}
name|String
name|payload
init|=
operator|new
name|String
argument_list|(
name|Base64
operator|.
name|decode
argument_list|(
name|token
operator|.
name|getData
argument_list|()
argument_list|)
argument_list|,
name|UTF_8
argument_list|)
decl_stmt|;
name|Matcher
name|matcher
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^([0-9]+):(.+@.+)$"
argument_list|)
operator|.
name|matcher
argument_list|(
name|payload
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|matcher
operator|.
name|matches
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|InvalidTokenException
argument_list|()
throw|;
block|}
name|Account
operator|.
name|Id
name|id
init|=
name|Account
operator|.
name|Id
operator|.
name|tryParse
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|orElseThrow
argument_list|(
name|InvalidTokenException
operator|::
operator|new
argument_list|)
decl_stmt|;
name|String
name|newEmail
init|=
name|matcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
decl_stmt|;
return|return
operator|new
name|ParsedToken
argument_list|(
name|id
argument_list|,
name|newEmail
argument_list|)
return|;
block|}
DECL|method|checkEmailRegistrationToken ()
specifier|private
name|void
name|checkEmailRegistrationToken
parameter_list|()
block|{
name|checkState
argument_list|(
name|emailRegistrationToken
operator|!=
literal|null
argument_list|,
literal|"'auth.registerEmailPrivateKey' not set in gerrit.config"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

