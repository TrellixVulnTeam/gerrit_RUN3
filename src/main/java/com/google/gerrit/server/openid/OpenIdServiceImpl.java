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
DECL|package|com.google.gerrit.server.openid
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|openid
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
name|client
operator|.
name|Link
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
name|client
operator|.
name|SignInDialog
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
name|client
operator|.
name|SignInDialog
operator|.
name|Mode
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
name|client
operator|.
name|auth
operator|.
name|openid
operator|.
name|DiscoveryResult
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
name|client
operator|.
name|auth
operator|.
name|openid
operator|.
name|OpenIdService
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
name|client
operator|.
name|auth
operator|.
name|openid
operator|.
name|OpenIdUtil
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
name|gerrit
operator|.
name|server
operator|.
name|UrlEncoded
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
name|AccountException
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
name|AccountManager
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
name|cache
operator|.
name|Cache
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
name|cache
operator|.
name|SelfPopulatingCache
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
name|CanonicalWebUrl
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
name|Nullable
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
name|http
operator|.
name|WebSession
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|user
operator|.
name|client
operator|.
name|rpc
operator|.
name|AsyncCallback
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|client
operator|.
name|KeyUtil
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|name
operator|.
name|Named
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openid4java
operator|.
name|consumer
operator|.
name|ConsumerException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openid4java
operator|.
name|consumer
operator|.
name|ConsumerManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openid4java
operator|.
name|consumer
operator|.
name|VerificationResult
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openid4java
operator|.
name|discovery
operator|.
name|DiscoveryException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openid4java
operator|.
name|discovery
operator|.
name|DiscoveryInformation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openid4java
operator|.
name|discovery
operator|.
name|Identifier
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openid4java
operator|.
name|message
operator|.
name|AuthRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openid4java
operator|.
name|message
operator|.
name|Message
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openid4java
operator|.
name|message
operator|.
name|MessageException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openid4java
operator|.
name|message
operator|.
name|MessageExtension
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openid4java
operator|.
name|message
operator|.
name|ParameterList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openid4java
operator|.
name|message
operator|.
name|ax
operator|.
name|AxMessage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openid4java
operator|.
name|message
operator|.
name|ax
operator|.
name|FetchRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openid4java
operator|.
name|message
operator|.
name|ax
operator|.
name|FetchResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openid4java
operator|.
name|message
operator|.
name|sreg
operator|.
name|SRegMessage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openid4java
operator|.
name|message
operator|.
name|sreg
operator|.
name|SRegRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openid4java
operator|.
name|message
operator|.
name|sreg
operator|.
name|SRegResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|List
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|Cookie
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|OpenIdServiceImpl
class|class
name|OpenIdServiceImpl
implements|implements
name|OpenIdService
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|OpenIdServiceImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|RETURN_URL
specifier|static
specifier|final
name|String
name|RETURN_URL
init|=
literal|"OpenID"
decl_stmt|;
DECL|field|P_MODE
specifier|private
specifier|static
specifier|final
name|String
name|P_MODE
init|=
literal|"gerrit.mode"
decl_stmt|;
DECL|field|P_TOKEN
specifier|private
specifier|static
specifier|final
name|String
name|P_TOKEN
init|=
literal|"gerrit.token"
decl_stmt|;
DECL|field|P_REMEMBER
specifier|private
specifier|static
specifier|final
name|String
name|P_REMEMBER
init|=
literal|"gerrit.remember"
decl_stmt|;
DECL|field|LASTID_AGE
specifier|private
specifier|static
specifier|final
name|int
name|LASTID_AGE
init|=
literal|365
operator|*
literal|24
operator|*
literal|60
operator|*
literal|60
decl_stmt|;
comment|// seconds
DECL|field|OPENID_MODE
specifier|private
specifier|static
specifier|final
name|String
name|OPENID_MODE
init|=
literal|"openid.mode"
decl_stmt|;
DECL|field|OMODE_CANCEL
specifier|private
specifier|static
specifier|final
name|String
name|OMODE_CANCEL
init|=
literal|"cancel"
decl_stmt|;
DECL|field|SCHEMA_EMAIL
specifier|private
specifier|static
specifier|final
name|String
name|SCHEMA_EMAIL
init|=
literal|"http://schema.openid.net/contact/email"
decl_stmt|;
DECL|field|SCHEMA_FIRSTNAME
specifier|private
specifier|static
specifier|final
name|String
name|SCHEMA_FIRSTNAME
init|=
literal|"http://schema.openid.net/namePerson/first"
decl_stmt|;
DECL|field|SCHEMA_LASTNAME
specifier|private
specifier|static
specifier|final
name|String
name|SCHEMA_LASTNAME
init|=
literal|"http://schema.openid.net/namePerson/last"
decl_stmt|;
DECL|field|webSession
specifier|private
specifier|final
name|Provider
argument_list|<
name|WebSession
argument_list|>
name|webSession
decl_stmt|;
DECL|field|identifiedUser
specifier|private
specifier|final
name|Provider
argument_list|<
name|IdentifiedUser
argument_list|>
name|identifiedUser
decl_stmt|;
DECL|field|urlProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|String
argument_list|>
name|urlProvider
decl_stmt|;
DECL|field|accountManager
specifier|private
specifier|final
name|AccountManager
name|accountManager
decl_stmt|;
DECL|field|manager
specifier|private
specifier|final
name|ConsumerManager
name|manager
decl_stmt|;
DECL|field|discoveryCache
specifier|private
specifier|final
name|SelfPopulatingCache
argument_list|<
name|String
argument_list|,
name|List
argument_list|>
name|discoveryCache
decl_stmt|;
annotation|@
name|Inject
DECL|method|OpenIdServiceImpl (final Provider<WebSession> cf, final Provider<IdentifiedUser> iu, @CanonicalWebUrl @Nullable final Provider<String> up, @Named(R) final Cache<String, List> openidCache, final AccountManager am)
name|OpenIdServiceImpl
parameter_list|(
specifier|final
name|Provider
argument_list|<
name|WebSession
argument_list|>
name|cf
parameter_list|,
specifier|final
name|Provider
argument_list|<
name|IdentifiedUser
argument_list|>
name|iu
parameter_list|,
annotation|@
name|CanonicalWebUrl
annotation|@
name|Nullable
specifier|final
name|Provider
argument_list|<
name|String
argument_list|>
name|up
parameter_list|,
annotation|@
name|Named
argument_list|(
literal|"openid"
argument_list|)
specifier|final
name|Cache
argument_list|<
name|String
argument_list|,
name|List
argument_list|>
name|openidCache
parameter_list|,
specifier|final
name|AccountManager
name|am
parameter_list|)
throws|throws
name|ConsumerException
block|{
name|webSession
operator|=
name|cf
expr_stmt|;
name|identifiedUser
operator|=
name|iu
expr_stmt|;
name|urlProvider
operator|=
name|up
expr_stmt|;
name|accountManager
operator|=
name|am
expr_stmt|;
name|manager
operator|=
operator|new
name|ConsumerManager
argument_list|()
expr_stmt|;
name|discoveryCache
operator|=
operator|new
name|SelfPopulatingCache
argument_list|<
name|String
argument_list|,
name|List
argument_list|>
argument_list|(
name|openidCache
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|List
name|createEntry
parameter_list|(
specifier|final
name|String
name|url
parameter_list|)
throws|throws
name|Exception
block|{
try|try
block|{
specifier|final
name|List
argument_list|<
name|?
argument_list|>
name|list
init|=
name|manager
operator|.
name|discover
argument_list|(
name|url
argument_list|)
decl_stmt|;
return|return
name|list
operator|!=
literal|null
operator|&&
operator|!
name|list
operator|.
name|isEmpty
argument_list|()
condition|?
name|list
else|:
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|DiscoveryException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
expr_stmt|;
block|}
DECL|method|discover (final String openidIdentifier, final SignInDialog.Mode mode, final boolean remember, final String returnToken, final AsyncCallback<DiscoveryResult> callback)
specifier|public
name|void
name|discover
parameter_list|(
specifier|final
name|String
name|openidIdentifier
parameter_list|,
specifier|final
name|SignInDialog
operator|.
name|Mode
name|mode
parameter_list|,
specifier|final
name|boolean
name|remember
parameter_list|,
specifier|final
name|String
name|returnToken
parameter_list|,
specifier|final
name|AsyncCallback
argument_list|<
name|DiscoveryResult
argument_list|>
name|callback
parameter_list|)
block|{
specifier|final
name|State
name|state
decl_stmt|;
name|state
operator|=
name|init
argument_list|(
name|openidIdentifier
argument_list|,
name|mode
argument_list|,
name|remember
argument_list|,
name|returnToken
argument_list|)
expr_stmt|;
if|if
condition|(
name|state
operator|==
literal|null
condition|)
block|{
name|callback
operator|.
name|onSuccess
argument_list|(
operator|new
name|DiscoveryResult
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|AuthRequest
name|aReq
decl_stmt|;
try|try
block|{
name|aReq
operator|=
name|manager
operator|.
name|authenticate
argument_list|(
name|state
operator|.
name|discovered
argument_list|,
name|state
operator|.
name|retTo
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|aReq
operator|.
name|setRealm
argument_list|(
name|state
operator|.
name|contextUrl
argument_list|)
expr_stmt|;
if|if
condition|(
name|requestRegistration
argument_list|(
name|aReq
argument_list|)
condition|)
block|{
specifier|final
name|SRegRequest
name|sregReq
init|=
name|SRegRequest
operator|.
name|createFetchRequest
argument_list|()
decl_stmt|;
name|sregReq
operator|.
name|addAttribute
argument_list|(
literal|"fullname"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|sregReq
operator|.
name|addAttribute
argument_list|(
literal|"email"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|aReq
operator|.
name|addExtension
argument_list|(
name|sregReq
argument_list|)
expr_stmt|;
specifier|final
name|FetchRequest
name|fetch
init|=
name|FetchRequest
operator|.
name|createFetchRequest
argument_list|()
decl_stmt|;
name|fetch
operator|.
name|addAttribute
argument_list|(
literal|"FirstName"
argument_list|,
name|SCHEMA_FIRSTNAME
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fetch
operator|.
name|addAttribute
argument_list|(
literal|"LastName"
argument_list|,
name|SCHEMA_LASTNAME
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fetch
operator|.
name|addAttribute
argument_list|(
literal|"Email"
argument_list|,
name|SCHEMA_EMAIL
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|aReq
operator|.
name|addExtension
argument_list|(
name|fetch
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|MessageException
name|e
parameter_list|)
block|{
name|callback
operator|.
name|onSuccess
argument_list|(
operator|new
name|DiscoveryResult
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|ConsumerException
name|e
parameter_list|)
block|{
name|callback
operator|.
name|onSuccess
argument_list|(
operator|new
name|DiscoveryResult
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|callback
operator|.
name|onSuccess
argument_list|(
operator|new
name|DiscoveryResult
argument_list|(
literal|true
argument_list|,
name|aReq
operator|.
name|getDestinationUrl
argument_list|(
literal|false
argument_list|)
argument_list|,
name|aReq
operator|.
name|getParameterMap
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|requestRegistration (final AuthRequest aReq)
specifier|private
name|boolean
name|requestRegistration
parameter_list|(
specifier|final
name|AuthRequest
name|aReq
parameter_list|)
block|{
if|if
condition|(
name|AuthRequest
operator|.
name|SELECT_ID
operator|.
name|equals
argument_list|(
name|aReq
operator|.
name|getIdentity
argument_list|()
argument_list|)
condition|)
block|{
comment|// We don't know anything about the identity, as the provider
comment|// will offer the user a way to indicate their identity. Skip
comment|// any database query operation and assume we must ask for the
comment|// registration information, in case the identity is new to us.
comment|//
return|return
literal|true
return|;
block|}
else|else
block|{
comment|// We might already have this account on file. Look for it.
comment|//
return|return
name|accountManager
operator|.
name|equals
argument_list|(
name|aReq
operator|.
name|getIdentity
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/** Called by {@link OpenIdLoginServlet} doGet, doPost */
DECL|method|doAuth (final HttpServletRequest req, final HttpServletResponse rsp)
name|void
name|doAuth
parameter_list|(
specifier|final
name|HttpServletRequest
name|req
parameter_list|,
specifier|final
name|HttpServletResponse
name|rsp
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|OMODE_CANCEL
operator|.
name|equals
argument_list|(
name|req
operator|.
name|getParameter
argument_list|(
name|OPENID_MODE
argument_list|)
argument_list|)
condition|)
block|{
name|cancel
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Process the authentication response.
comment|//
specifier|final
name|SignInDialog
operator|.
name|Mode
name|mode
init|=
name|signInMode
argument_list|(
name|req
argument_list|)
decl_stmt|;
specifier|final
name|String
name|openidIdentifier
init|=
name|req
operator|.
name|getParameter
argument_list|(
literal|"openid.identity"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|returnToken
init|=
name|req
operator|.
name|getParameter
argument_list|(
name|P_TOKEN
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|remember
init|=
literal|"1"
operator|.
name|equals
argument_list|(
name|req
operator|.
name|getParameter
argument_list|(
name|P_REMEMBER
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|State
name|state
decl_stmt|;
name|state
operator|=
name|init
argument_list|(
name|openidIdentifier
argument_list|,
name|mode
argument_list|,
name|remember
argument_list|,
name|returnToken
argument_list|)
expr_stmt|;
if|if
condition|(
name|state
operator|==
literal|null
condition|)
block|{
comment|// Re-discovery must have failed, we can't run a login.
comment|//
name|cancel
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|String
name|returnTo
init|=
name|req
operator|.
name|getParameter
argument_list|(
literal|"openid.return_to"
argument_list|)
decl_stmt|;
if|if
condition|(
name|returnTo
operator|!=
literal|null
operator|&&
name|returnTo
operator|.
name|contains
argument_list|(
literal|"openid.rpnonce="
argument_list|)
condition|)
block|{
comment|// Some providers (claimid.com) seem to embed these request
comment|// parameters into our return_to URL, and then give us them
comment|// in the return_to request parameter. But not all.
comment|//
name|state
operator|.
name|retTo
operator|.
name|put
argument_list|(
literal|"openid.rpnonce"
argument_list|,
name|req
operator|.
name|getParameter
argument_list|(
literal|"openid.rpnonce"
argument_list|)
argument_list|)
expr_stmt|;
name|state
operator|.
name|retTo
operator|.
name|put
argument_list|(
literal|"openid.rpsig"
argument_list|,
name|req
operator|.
name|getParameter
argument_list|(
literal|"openid.rpsig"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|VerificationResult
name|result
init|=
name|manager
operator|.
name|verify
argument_list|(
name|state
operator|.
name|retTo
operator|.
name|toString
argument_list|()
argument_list|,
operator|new
name|ParameterList
argument_list|(
name|req
operator|.
name|getParameterMap
argument_list|()
argument_list|)
argument_list|,
name|state
operator|.
name|discovered
argument_list|)
decl_stmt|;
specifier|final
name|Identifier
name|user
init|=
name|result
operator|.
name|getVerifiedId
argument_list|()
decl_stmt|;
if|if
condition|(
name|user
operator|==
literal|null
comment|/* authentication failure */
condition|)
block|{
if|if
condition|(
literal|"Nonce verification failed."
operator|.
name|equals
argument_list|(
name|result
operator|.
name|getStatusMsg
argument_list|()
argument_list|)
condition|)
block|{
comment|// We might be suffering from clock skew on this system.
comment|//
name|log
operator|.
name|error
argument_list|(
literal|"OpenID failure: "
operator|+
name|result
operator|.
name|getStatusMsg
argument_list|()
operator|+
literal|"  Likely caused by clock skew on this server,"
operator|+
literal|" install/configure NTP."
argument_list|)
expr_stmt|;
name|cancelWithError
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|result
operator|.
name|getStatusMsg
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|result
operator|.
name|getStatusMsg
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// Authentication failed.
comment|//
name|log
operator|.
name|error
argument_list|(
literal|"OpenID failure: "
operator|+
name|result
operator|.
name|getStatusMsg
argument_list|()
argument_list|)
expr_stmt|;
name|cancelWithError
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|result
operator|.
name|getStatusMsg
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Assume authentication was canceled.
comment|//
name|cancel
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
specifier|final
name|Message
name|authRsp
init|=
name|result
operator|.
name|getAuthResponse
argument_list|()
decl_stmt|;
name|SRegResponse
name|sregRsp
init|=
literal|null
decl_stmt|;
name|FetchResponse
name|fetchRsp
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|authRsp
operator|.
name|hasExtension
argument_list|(
name|SRegMessage
operator|.
name|OPENID_NS_SREG
argument_list|)
condition|)
block|{
specifier|final
name|MessageExtension
name|ext
init|=
name|authRsp
operator|.
name|getExtension
argument_list|(
name|SRegMessage
operator|.
name|OPENID_NS_SREG
argument_list|)
decl_stmt|;
if|if
condition|(
name|ext
operator|instanceof
name|SRegResponse
condition|)
block|{
name|sregRsp
operator|=
operator|(
name|SRegResponse
operator|)
name|ext
expr_stmt|;
block|}
block|}
if|if
condition|(
name|authRsp
operator|.
name|hasExtension
argument_list|(
name|AxMessage
operator|.
name|OPENID_NS_AX
argument_list|)
condition|)
block|{
specifier|final
name|MessageExtension
name|ext
init|=
name|authRsp
operator|.
name|getExtension
argument_list|(
name|AxMessage
operator|.
name|OPENID_NS_AX
argument_list|)
decl_stmt|;
if|if
condition|(
name|ext
operator|instanceof
name|FetchResponse
condition|)
block|{
name|fetchRsp
operator|=
operator|(
name|FetchResponse
operator|)
name|ext
expr_stmt|;
block|}
block|}
specifier|final
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
name|AuthRequest
name|areq
init|=
operator|new
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
name|AuthRequest
argument_list|(
name|user
operator|.
name|getIdentifier
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|sregRsp
operator|!=
literal|null
condition|)
block|{
name|areq
operator|.
name|setDisplayName
argument_list|(
name|sregRsp
operator|.
name|getAttributeValue
argument_list|(
literal|"fullname"
argument_list|)
argument_list|)
expr_stmt|;
name|areq
operator|.
name|setEmailAddress
argument_list|(
name|sregRsp
operator|.
name|getAttributeValue
argument_list|(
literal|"email"
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fetchRsp
operator|!=
literal|null
condition|)
block|{
specifier|final
name|String
name|firstName
init|=
name|fetchRsp
operator|.
name|getAttributeValue
argument_list|(
literal|"FirstName"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|lastName
init|=
name|fetchRsp
operator|.
name|getAttributeValue
argument_list|(
literal|"LastName"
argument_list|)
decl_stmt|;
specifier|final
name|StringBuilder
name|n
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|firstName
operator|!=
literal|null
operator|&&
name|firstName
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|n
operator|.
name|append
argument_list|(
name|firstName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|lastName
operator|!=
literal|null
operator|&&
name|lastName
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|n
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|n
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|n
operator|.
name|append
argument_list|(
name|lastName
argument_list|)
expr_stmt|;
block|}
name|areq
operator|.
name|setDisplayName
argument_list|(
name|n
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|?
name|n
operator|.
name|toString
argument_list|()
else|:
literal|null
argument_list|)
expr_stmt|;
name|areq
operator|.
name|setEmailAddress
argument_list|(
name|fetchRsp
operator|.
name|getAttributeValue
argument_list|(
literal|"Email"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
switch|switch
condition|(
name|mode
condition|)
block|{
case|case
name|REGISTER
case|:
case|case
name|SIGN_IN
case|:
specifier|final
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
name|AuthResult
name|arsp
decl_stmt|;
name|arsp
operator|=
name|accountManager
operator|.
name|authenticate
argument_list|(
name|areq
argument_list|)
expr_stmt|;
specifier|final
name|Cookie
name|lastId
init|=
operator|new
name|Cookie
argument_list|(
name|OpenIdUtil
operator|.
name|LASTID_COOKIE
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|lastId
operator|.
name|setPath
argument_list|(
name|req
operator|.
name|getContextPath
argument_list|()
operator|+
literal|"/"
argument_list|)
expr_stmt|;
if|if
condition|(
name|remember
condition|)
block|{
name|lastId
operator|.
name|setValue
argument_list|(
name|user
operator|.
name|getIdentifier
argument_list|()
argument_list|)
expr_stmt|;
name|lastId
operator|.
name|setMaxAge
argument_list|(
name|LASTID_AGE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|lastId
operator|.
name|setMaxAge
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|rsp
operator|.
name|addCookie
argument_list|(
name|lastId
argument_list|)
expr_stmt|;
name|webSession
operator|.
name|get
argument_list|()
operator|.
name|login
argument_list|(
name|arsp
operator|.
name|getAccountId
argument_list|()
argument_list|,
name|remember
argument_list|)
expr_stmt|;
name|callback
argument_list|(
name|arsp
operator|.
name|isNew
argument_list|()
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
break|break;
case|case
name|LINK_IDENTIY
case|:
name|accountManager
operator|.
name|link
argument_list|(
name|identifiedUser
operator|.
name|get
argument_list|()
operator|.
name|getAccountId
argument_list|()
argument_list|,
name|areq
argument_list|)
expr_stmt|;
name|callback
argument_list|(
literal|false
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
catch|catch
parameter_list|(
name|AccountException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"OpenID authentication failure"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|cancelWithError
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
literal|"Contact site administrator"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|isSignIn (final SignInDialog.Mode mode)
specifier|private
name|boolean
name|isSignIn
parameter_list|(
specifier|final
name|SignInDialog
operator|.
name|Mode
name|mode
parameter_list|)
block|{
switch|switch
condition|(
name|mode
condition|)
block|{
case|case
name|SIGN_IN
case|:
case|case
name|REGISTER
case|:
return|return
literal|true
return|;
default|default:
return|return
literal|false
return|;
block|}
block|}
DECL|method|signInMode (final HttpServletRequest req)
specifier|private
specifier|static
name|Mode
name|signInMode
parameter_list|(
specifier|final
name|HttpServletRequest
name|req
parameter_list|)
block|{
try|try
block|{
return|return
name|SignInDialog
operator|.
name|Mode
operator|.
name|valueOf
argument_list|(
name|req
operator|.
name|getParameter
argument_list|(
name|P_MODE
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
return|return
name|SignInDialog
operator|.
name|Mode
operator|.
name|SIGN_IN
return|;
block|}
block|}
DECL|method|callback (final boolean isNew, final HttpServletRequest req, final HttpServletResponse rsp)
specifier|private
name|void
name|callback
parameter_list|(
specifier|final
name|boolean
name|isNew
parameter_list|,
specifier|final
name|HttpServletRequest
name|req
parameter_list|,
specifier|final
name|HttpServletResponse
name|rsp
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|token
init|=
name|req
operator|.
name|getParameter
argument_list|(
name|P_TOKEN
argument_list|)
decl_stmt|;
if|if
condition|(
name|token
operator|==
literal|null
operator|||
name|token
operator|.
name|isEmpty
argument_list|()
operator|||
name|token
operator|.
name|startsWith
argument_list|(
literal|"SignInFailure,"
argument_list|)
condition|)
block|{
name|token
operator|=
name|Link
operator|.
name|MINE
expr_stmt|;
block|}
specifier|final
name|StringBuilder
name|rdr
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|rdr
operator|.
name|append
argument_list|(
name|urlProvider
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|rdr
operator|.
name|append
argument_list|(
literal|'#'
argument_list|)
expr_stmt|;
if|if
condition|(
name|isNew
operator|&&
operator|!
name|token
operator|.
name|startsWith
argument_list|(
name|Link
operator|.
name|REGISTER
operator|+
literal|","
argument_list|)
condition|)
block|{
name|rdr
operator|.
name|append
argument_list|(
name|Link
operator|.
name|REGISTER
argument_list|)
expr_stmt|;
name|rdr
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
name|rdr
operator|.
name|append
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|sendRedirect
argument_list|(
name|rdr
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|cancel (final HttpServletRequest req, final HttpServletResponse rsp)
specifier|private
name|void
name|cancel
parameter_list|(
specifier|final
name|HttpServletRequest
name|req
parameter_list|,
specifier|final
name|HttpServletResponse
name|rsp
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|isSignIn
argument_list|(
name|signInMode
argument_list|(
name|req
argument_list|)
argument_list|)
condition|)
block|{
name|webSession
operator|.
name|get
argument_list|()
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
name|callback
argument_list|(
literal|false
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
DECL|method|cancelWithError (final HttpServletRequest req, final HttpServletResponse rsp, final String errorDetail)
specifier|private
name|void
name|cancelWithError
parameter_list|(
specifier|final
name|HttpServletRequest
name|req
parameter_list|,
specifier|final
name|HttpServletResponse
name|rsp
parameter_list|,
specifier|final
name|String
name|errorDetail
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|SignInDialog
operator|.
name|Mode
name|mode
init|=
name|signInMode
argument_list|(
name|req
argument_list|)
decl_stmt|;
if|if
condition|(
name|isSignIn
argument_list|(
name|mode
argument_list|)
condition|)
block|{
name|webSession
operator|.
name|get
argument_list|()
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
specifier|final
name|StringBuilder
name|rdr
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|rdr
operator|.
name|append
argument_list|(
name|urlProvider
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|rdr
operator|.
name|append
argument_list|(
literal|'#'
argument_list|)
expr_stmt|;
name|rdr
operator|.
name|append
argument_list|(
literal|"SignInFailure"
argument_list|)
expr_stmt|;
name|rdr
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|rdr
operator|.
name|append
argument_list|(
name|mode
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|rdr
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|rdr
operator|.
name|append
argument_list|(
name|errorDetail
operator|!=
literal|null
condition|?
name|KeyUtil
operator|.
name|encode
argument_list|(
name|errorDetail
argument_list|)
else|:
literal|""
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|sendRedirect
argument_list|(
name|rdr
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|init (final String openidIdentifier, final SignInDialog.Mode mode, final boolean remember, final String returnToken)
specifier|private
name|State
name|init
parameter_list|(
specifier|final
name|String
name|openidIdentifier
parameter_list|,
specifier|final
name|SignInDialog
operator|.
name|Mode
name|mode
parameter_list|,
specifier|final
name|boolean
name|remember
parameter_list|,
specifier|final
name|String
name|returnToken
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|?
argument_list|>
name|list
init|=
name|discoveryCache
operator|.
name|get
argument_list|(
name|openidIdentifier
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|==
literal|null
operator|||
name|list
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|String
name|contextUrl
init|=
name|urlProvider
operator|.
name|get
argument_list|()
decl_stmt|;
specifier|final
name|DiscoveryInformation
name|discovered
init|=
name|manager
operator|.
name|associate
argument_list|(
name|list
argument_list|)
decl_stmt|;
specifier|final
name|UrlEncoded
name|retTo
init|=
operator|new
name|UrlEncoded
argument_list|(
name|contextUrl
operator|+
name|RETURN_URL
argument_list|)
decl_stmt|;
name|retTo
operator|.
name|put
argument_list|(
name|P_MODE
argument_list|,
name|mode
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|returnToken
operator|!=
literal|null
operator|&&
name|returnToken
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|retTo
operator|.
name|put
argument_list|(
name|P_TOKEN
argument_list|,
name|returnToken
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|remember
condition|)
block|{
name|retTo
operator|.
name|put
argument_list|(
name|P_REMEMBER
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|State
argument_list|(
name|discovered
argument_list|,
name|retTo
argument_list|,
name|contextUrl
argument_list|)
return|;
block|}
DECL|class|State
specifier|private
specifier|static
class|class
name|State
block|{
DECL|field|discovered
specifier|final
name|DiscoveryInformation
name|discovered
decl_stmt|;
DECL|field|retTo
specifier|final
name|UrlEncoded
name|retTo
decl_stmt|;
DECL|field|contextUrl
specifier|final
name|String
name|contextUrl
decl_stmt|;
DECL|method|State (final DiscoveryInformation d, final UrlEncoded r, final String c)
name|State
parameter_list|(
specifier|final
name|DiscoveryInformation
name|d
parameter_list|,
specifier|final
name|UrlEncoded
name|r
parameter_list|,
specifier|final
name|String
name|c
parameter_list|)
block|{
name|discovered
operator|=
name|d
expr_stmt|;
name|retTo
operator|=
name|r
expr_stmt|;
name|contextUrl
operator|=
name|c
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

