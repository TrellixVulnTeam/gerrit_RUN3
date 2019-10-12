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
name|common
operator|.
name|io
operator|.
name|BaseEncoding
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
name|ParameterizedString
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
name|entities
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
name|server
operator|.
name|GerritPersonIdent
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
name|account
operator|.
name|AccountCache
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
name|account
operator|.
name|AccountState
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
name|AnonymousCowardName
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
name|GerritServerConfig
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
name|MailUtil
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
name|Provider
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
name|security
operator|.
name|MessageDigest
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
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
name|lib
operator|.
name|Config
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
name|PersonIdent
import|;
end_import

begin_comment
comment|/** Creates a {@link FromAddressGenerator} from the {@link GerritServerConfig} */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|FromAddressGeneratorProvider
specifier|public
class|class
name|FromAddressGeneratorProvider
implements|implements
name|Provider
argument_list|<
name|FromAddressGenerator
argument_list|>
block|{
DECL|field|generator
specifier|private
specifier|final
name|FromAddressGenerator
name|generator
decl_stmt|;
annotation|@
name|Inject
DECL|method|FromAddressGeneratorProvider ( @erritServerConfig Config cfg, @AnonymousCowardName String anonymousCowardName, @GerritPersonIdent PersonIdent myIdent, AccountCache accountCache)
name|FromAddressGeneratorProvider
parameter_list|(
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|,
annotation|@
name|AnonymousCowardName
name|String
name|anonymousCowardName
parameter_list|,
annotation|@
name|GerritPersonIdent
name|PersonIdent
name|myIdent
parameter_list|,
name|AccountCache
name|accountCache
parameter_list|)
block|{
specifier|final
name|String
name|from
init|=
name|cfg
operator|.
name|getString
argument_list|(
literal|"sendemail"
argument_list|,
literal|null
argument_list|,
literal|"from"
argument_list|)
decl_stmt|;
specifier|final
name|Address
name|srvAddr
init|=
name|toAddress
argument_list|(
name|myIdent
argument_list|)
decl_stmt|;
if|if
condition|(
name|from
operator|==
literal|null
operator|||
literal|"MIXED"
operator|.
name|equalsIgnoreCase
argument_list|(
name|from
argument_list|)
condition|)
block|{
name|ParameterizedString
name|name
init|=
operator|new
name|ParameterizedString
argument_list|(
literal|"${user} (Code Review)"
argument_list|)
decl_stmt|;
name|generator
operator|=
operator|new
name|PatternGen
argument_list|(
name|srvAddr
argument_list|,
name|accountCache
argument_list|,
name|anonymousCowardName
argument_list|,
name|name
argument_list|,
name|srvAddr
operator|.
name|getEmail
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"USER"
operator|.
name|equalsIgnoreCase
argument_list|(
name|from
argument_list|)
condition|)
block|{
name|String
index|[]
name|domains
init|=
name|cfg
operator|.
name|getStringList
argument_list|(
literal|"sendemail"
argument_list|,
literal|null
argument_list|,
literal|"allowedDomain"
argument_list|)
decl_stmt|;
name|Pattern
name|domainPattern
init|=
name|MailUtil
operator|.
name|glob
argument_list|(
name|domains
argument_list|)
decl_stmt|;
name|ParameterizedString
name|namePattern
init|=
operator|new
name|ParameterizedString
argument_list|(
literal|"${user} (Code Review)"
argument_list|)
decl_stmt|;
name|generator
operator|=
operator|new
name|UserGen
argument_list|(
name|accountCache
argument_list|,
name|domainPattern
argument_list|,
name|anonymousCowardName
argument_list|,
name|namePattern
argument_list|,
name|srvAddr
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"SERVER"
operator|.
name|equalsIgnoreCase
argument_list|(
name|from
argument_list|)
condition|)
block|{
name|generator
operator|=
operator|new
name|ServerGen
argument_list|(
name|srvAddr
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|Address
name|a
init|=
name|Address
operator|.
name|parse
argument_list|(
name|from
argument_list|)
decl_stmt|;
specifier|final
name|ParameterizedString
name|name
init|=
name|a
operator|.
name|getName
argument_list|()
operator|!=
literal|null
condition|?
operator|new
name|ParameterizedString
argument_list|(
name|a
operator|.
name|getName
argument_list|()
argument_list|)
else|:
literal|null
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
operator|||
name|name
operator|.
name|getParameterNames
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|generator
operator|=
operator|new
name|ServerGen
argument_list|(
name|a
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|generator
operator|=
operator|new
name|PatternGen
argument_list|(
name|srvAddr
argument_list|,
name|accountCache
argument_list|,
name|anonymousCowardName
argument_list|,
name|name
argument_list|,
name|a
operator|.
name|getEmail
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|toAddress (PersonIdent myIdent)
specifier|private
specifier|static
name|Address
name|toAddress
parameter_list|(
name|PersonIdent
name|myIdent
parameter_list|)
block|{
return|return
operator|new
name|Address
argument_list|(
name|myIdent
operator|.
name|getName
argument_list|()
argument_list|,
name|myIdent
operator|.
name|getEmailAddress
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|get ()
specifier|public
name|FromAddressGenerator
name|get
parameter_list|()
block|{
return|return
name|generator
return|;
block|}
DECL|class|UserGen
specifier|static
specifier|final
class|class
name|UserGen
implements|implements
name|FromAddressGenerator
block|{
DECL|field|accountCache
specifier|private
specifier|final
name|AccountCache
name|accountCache
decl_stmt|;
DECL|field|domainPattern
specifier|private
specifier|final
name|Pattern
name|domainPattern
decl_stmt|;
DECL|field|anonymousCowardName
specifier|private
specifier|final
name|String
name|anonymousCowardName
decl_stmt|;
DECL|field|nameRewriteTmpl
specifier|private
specifier|final
name|ParameterizedString
name|nameRewriteTmpl
decl_stmt|;
DECL|field|serverAddress
specifier|private
specifier|final
name|Address
name|serverAddress
decl_stmt|;
comment|/**      * From address generator for USER mode      *      * @param accountCache get user account from id      * @param domainPattern allowed user domain pattern that Gerrit can send as the user      * @param anonymousCowardName name used when user's full name is missing      * @param nameRewriteTmpl name template used for rewriting the sender's name when Gerrit can not      *     send as the user      * @param serverAddress serverAddress.name is used when fromId is null and serverAddress.email      *     is used when Gerrit can not send as the user      */
DECL|method|UserGen ( AccountCache accountCache, Pattern domainPattern, String anonymousCowardName, ParameterizedString nameRewriteTmpl, Address serverAddress)
name|UserGen
parameter_list|(
name|AccountCache
name|accountCache
parameter_list|,
name|Pattern
name|domainPattern
parameter_list|,
name|String
name|anonymousCowardName
parameter_list|,
name|ParameterizedString
name|nameRewriteTmpl
parameter_list|,
name|Address
name|serverAddress
parameter_list|)
block|{
name|this
operator|.
name|accountCache
operator|=
name|accountCache
expr_stmt|;
name|this
operator|.
name|domainPattern
operator|=
name|domainPattern
expr_stmt|;
name|this
operator|.
name|anonymousCowardName
operator|=
name|anonymousCowardName
expr_stmt|;
name|this
operator|.
name|nameRewriteTmpl
operator|=
name|nameRewriteTmpl
expr_stmt|;
name|this
operator|.
name|serverAddress
operator|=
name|serverAddress
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isGenericAddress (Account.Id fromId)
specifier|public
name|boolean
name|isGenericAddress
parameter_list|(
name|Account
operator|.
name|Id
name|fromId
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|from (Account.Id fromId)
specifier|public
name|Address
name|from
parameter_list|(
name|Account
operator|.
name|Id
name|fromId
parameter_list|)
block|{
name|String
name|senderName
decl_stmt|;
if|if
condition|(
name|fromId
operator|!=
literal|null
condition|)
block|{
name|Optional
argument_list|<
name|Account
argument_list|>
name|a
init|=
name|accountCache
operator|.
name|get
argument_list|(
name|fromId
argument_list|)
operator|.
name|map
argument_list|(
name|AccountState
operator|::
name|account
argument_list|)
decl_stmt|;
name|String
name|fullName
init|=
name|a
operator|.
name|map
argument_list|(
name|Account
operator|::
name|fullName
argument_list|)
operator|.
name|orElse
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|String
name|userEmail
init|=
name|a
operator|.
name|map
argument_list|(
name|Account
operator|::
name|preferredEmail
argument_list|)
operator|.
name|orElse
argument_list|(
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|canRelay
argument_list|(
name|userEmail
argument_list|)
condition|)
block|{
return|return
operator|new
name|Address
argument_list|(
name|fullName
argument_list|,
name|userEmail
argument_list|)
return|;
block|}
if|if
condition|(
name|fullName
operator|==
literal|null
operator|||
literal|""
operator|.
name|equals
argument_list|(
name|fullName
operator|.
name|trim
argument_list|()
argument_list|)
condition|)
block|{
name|fullName
operator|=
name|anonymousCowardName
expr_stmt|;
block|}
name|senderName
operator|=
name|nameRewriteTmpl
operator|.
name|replace
argument_list|(
literal|"user"
argument_list|,
name|fullName
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|senderName
operator|=
name|serverAddress
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
name|String
name|senderEmail
decl_stmt|;
name|ParameterizedString
name|senderEmailPattern
init|=
operator|new
name|ParameterizedString
argument_list|(
name|serverAddress
operator|.
name|getEmail
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|senderEmailPattern
operator|.
name|getParameterNames
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|senderEmail
operator|=
name|senderEmailPattern
operator|.
name|getRawPattern
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|senderEmail
operator|=
name|senderEmailPattern
operator|.
name|replace
argument_list|(
literal|"userHash"
argument_list|,
name|hashOf
argument_list|(
name|senderName
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|Address
argument_list|(
name|senderName
argument_list|,
name|senderEmail
argument_list|)
return|;
block|}
comment|/** check if Gerrit is allowed to send from {@code userEmail}. */
DECL|method|canRelay (String userEmail)
specifier|private
name|boolean
name|canRelay
parameter_list|(
name|String
name|userEmail
parameter_list|)
block|{
if|if
condition|(
name|userEmail
operator|!=
literal|null
condition|)
block|{
name|int
name|index
init|=
name|userEmail
operator|.
name|indexOf
argument_list|(
literal|'@'
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|>
literal|0
operator|&&
name|index
operator|<
name|userEmail
operator|.
name|length
argument_list|()
operator|-
literal|1
condition|)
block|{
return|return
name|domainPattern
operator|.
name|matcher
argument_list|(
name|userEmail
operator|.
name|substring
argument_list|(
name|index
operator|+
literal|1
argument_list|)
argument_list|)
operator|.
name|matches
argument_list|()
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
DECL|class|ServerGen
specifier|static
specifier|final
class|class
name|ServerGen
implements|implements
name|FromAddressGenerator
block|{
DECL|field|srvAddr
specifier|private
specifier|final
name|Address
name|srvAddr
decl_stmt|;
DECL|method|ServerGen (Address srvAddr)
name|ServerGen
parameter_list|(
name|Address
name|srvAddr
parameter_list|)
block|{
name|this
operator|.
name|srvAddr
operator|=
name|srvAddr
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isGenericAddress (Account.Id fromId)
specifier|public
name|boolean
name|isGenericAddress
parameter_list|(
name|Account
operator|.
name|Id
name|fromId
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|from (Account.Id fromId)
specifier|public
name|Address
name|from
parameter_list|(
name|Account
operator|.
name|Id
name|fromId
parameter_list|)
block|{
return|return
name|srvAddr
return|;
block|}
block|}
DECL|class|PatternGen
specifier|static
specifier|final
class|class
name|PatternGen
implements|implements
name|FromAddressGenerator
block|{
DECL|field|senderEmailPattern
specifier|private
specifier|final
name|ParameterizedString
name|senderEmailPattern
decl_stmt|;
DECL|field|serverAddress
specifier|private
specifier|final
name|Address
name|serverAddress
decl_stmt|;
DECL|field|accountCache
specifier|private
specifier|final
name|AccountCache
name|accountCache
decl_stmt|;
DECL|field|anonymousCowardName
specifier|private
specifier|final
name|String
name|anonymousCowardName
decl_stmt|;
DECL|field|namePattern
specifier|private
specifier|final
name|ParameterizedString
name|namePattern
decl_stmt|;
DECL|method|PatternGen ( final Address serverAddress, final AccountCache accountCache, final String anonymousCowardName, final ParameterizedString namePattern, final String senderEmail)
name|PatternGen
parameter_list|(
specifier|final
name|Address
name|serverAddress
parameter_list|,
specifier|final
name|AccountCache
name|accountCache
parameter_list|,
specifier|final
name|String
name|anonymousCowardName
parameter_list|,
specifier|final
name|ParameterizedString
name|namePattern
parameter_list|,
specifier|final
name|String
name|senderEmail
parameter_list|)
block|{
name|this
operator|.
name|senderEmailPattern
operator|=
operator|new
name|ParameterizedString
argument_list|(
name|senderEmail
argument_list|)
expr_stmt|;
name|this
operator|.
name|serverAddress
operator|=
name|serverAddress
expr_stmt|;
name|this
operator|.
name|accountCache
operator|=
name|accountCache
expr_stmt|;
name|this
operator|.
name|anonymousCowardName
operator|=
name|anonymousCowardName
expr_stmt|;
name|this
operator|.
name|namePattern
operator|=
name|namePattern
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isGenericAddress (Account.Id fromId)
specifier|public
name|boolean
name|isGenericAddress
parameter_list|(
name|Account
operator|.
name|Id
name|fromId
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|from (Account.Id fromId)
specifier|public
name|Address
name|from
parameter_list|(
name|Account
operator|.
name|Id
name|fromId
parameter_list|)
block|{
specifier|final
name|String
name|senderName
decl_stmt|;
if|if
condition|(
name|fromId
operator|!=
literal|null
condition|)
block|{
name|String
name|fullName
init|=
name|accountCache
operator|.
name|get
argument_list|(
name|fromId
argument_list|)
operator|.
name|map
argument_list|(
name|a
lambda|->
name|a
operator|.
name|account
argument_list|()
operator|.
name|fullName
argument_list|()
argument_list|)
operator|.
name|orElse
argument_list|(
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|fullName
operator|==
literal|null
operator|||
literal|""
operator|.
name|equals
argument_list|(
name|fullName
argument_list|)
condition|)
block|{
name|fullName
operator|=
name|anonymousCowardName
expr_stmt|;
block|}
name|senderName
operator|=
name|namePattern
operator|.
name|replace
argument_list|(
literal|"user"
argument_list|,
name|fullName
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|senderName
operator|=
name|serverAddress
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
name|String
name|senderEmail
decl_stmt|;
if|if
condition|(
name|senderEmailPattern
operator|.
name|getParameterNames
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|senderEmail
operator|=
name|senderEmailPattern
operator|.
name|getRawPattern
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|senderEmail
operator|=
name|senderEmailPattern
operator|.
name|replace
argument_list|(
literal|"userHash"
argument_list|,
name|hashOf
argument_list|(
name|senderName
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|Address
argument_list|(
name|senderName
argument_list|,
name|senderEmail
argument_list|)
return|;
block|}
block|}
DECL|method|hashOf (String data)
specifier|private
specifier|static
name|String
name|hashOf
parameter_list|(
name|String
name|data
parameter_list|)
block|{
try|try
block|{
name|MessageDigest
name|hash
init|=
name|MessageDigest
operator|.
name|getInstance
argument_list|(
literal|"MD5"
argument_list|)
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
name|hash
operator|.
name|digest
argument_list|(
name|data
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|BaseEncoding
operator|.
name|base64Url
argument_list|()
operator|.
name|encode
argument_list|(
name|bytes
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"No MD5 available"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

