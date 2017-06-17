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
DECL|package|com.google.gerrit.acceptance.rest.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|rest
operator|.
name|change
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
name|net
operator|.
name|HttpHeaders
operator|.
name|ACCESS_CONTROL_ALLOW_CREDENTIALS
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
name|net
operator|.
name|HttpHeaders
operator|.
name|ACCESS_CONTROL_ALLOW_HEADERS
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
name|net
operator|.
name|HttpHeaders
operator|.
name|ACCESS_CONTROL_ALLOW_METHODS
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
name|net
operator|.
name|HttpHeaders
operator|.
name|ACCESS_CONTROL_ALLOW_ORIGIN
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
name|net
operator|.
name|HttpHeaders
operator|.
name|ACCESS_CONTROL_MAX_AGE
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
name|net
operator|.
name|HttpHeaders
operator|.
name|ACCESS_CONTROL_REQUEST_HEADERS
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
name|net
operator|.
name|HttpHeaders
operator|.
name|ACCESS_CONTROL_REQUEST_METHOD
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
name|net
operator|.
name|HttpHeaders
operator|.
name|AUTHORIZATION
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
name|net
operator|.
name|HttpHeaders
operator|.
name|CONTENT_TYPE
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
name|net
operator|.
name|HttpHeaders
operator|.
name|ORIGIN
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
name|net
operator|.
name|HttpHeaders
operator|.
name|VARY
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
name|assertThat
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
name|base
operator|.
name|Splitter
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
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
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
name|truth
operator|.
name|StringSubject
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
name|RestResponse
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
name|restapi
operator|.
name|RestApiException
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
name|testutil
operator|.
name|ConfigSuite
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Stream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|Header
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|fluent
operator|.
name|Executor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|fluent
operator|.
name|Request
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|cookie
operator|.
name|Cookie
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|impl
operator|.
name|client
operator|.
name|BasicCookieStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|message
operator|.
name|BasicHeader
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
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|CorsIT
specifier|public
class|class
name|CorsIT
extends|extends
name|AbstractDaemonTest
block|{
annotation|@
name|ConfigSuite
operator|.
name|Default
DECL|method|allowExampleDotCom ()
specifier|public
specifier|static
name|Config
name|allowExampleDotCom
parameter_list|()
block|{
name|Config
name|cfg
init|=
operator|new
name|Config
argument_list|()
decl_stmt|;
name|cfg
operator|.
name|setString
argument_list|(
literal|"auth"
argument_list|,
literal|null
argument_list|,
literal|"type"
argument_list|,
literal|"DEVELOPMENT_BECOME_ANY_ACCOUNT"
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|setStringList
argument_list|(
literal|"site"
argument_list|,
literal|null
argument_list|,
literal|"allowOriginRegex"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"https?://(.+[.])?example[.]com"
argument_list|,
literal|"http://friend[.]ly"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|cfg
return|;
block|}
annotation|@
name|Test
DECL|method|missingOriginIsAllowedWithNoCorsResponseHeaders ()
specifier|public
name|void
name|missingOriginIsAllowedWithNoCorsResponseHeaders
parameter_list|()
throws|throws
name|Exception
block|{
name|Result
name|change
init|=
name|createChange
argument_list|()
decl_stmt|;
name|String
name|url
init|=
literal|"/changes/"
operator|+
name|change
operator|.
name|getChangeId
argument_list|()
operator|+
literal|"/detail"
decl_stmt|;
name|RestResponse
name|r
init|=
name|adminRestSession
operator|.
name|get
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|r
operator|.
name|assertOK
argument_list|()
expr_stmt|;
name|String
name|allowOrigin
init|=
name|r
operator|.
name|getHeader
argument_list|(
name|ACCESS_CONTROL_ALLOW_ORIGIN
argument_list|)
decl_stmt|;
name|String
name|allowCred
init|=
name|r
operator|.
name|getHeader
argument_list|(
name|ACCESS_CONTROL_ALLOW_CREDENTIALS
argument_list|)
decl_stmt|;
name|String
name|maxAge
init|=
name|r
operator|.
name|getHeader
argument_list|(
name|ACCESS_CONTROL_MAX_AGE
argument_list|)
decl_stmt|;
name|String
name|allowMethods
init|=
name|r
operator|.
name|getHeader
argument_list|(
name|ACCESS_CONTROL_ALLOW_METHODS
argument_list|)
decl_stmt|;
name|String
name|allowHeaders
init|=
name|r
operator|.
name|getHeader
argument_list|(
name|ACCESS_CONTROL_ALLOW_HEADERS
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|allowOrigin
argument_list|)
operator|.
name|named
argument_list|(
name|ACCESS_CONTROL_ALLOW_ORIGIN
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|allowCred
argument_list|)
operator|.
name|named
argument_list|(
name|ACCESS_CONTROL_ALLOW_CREDENTIALS
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|maxAge
argument_list|)
operator|.
name|named
argument_list|(
name|ACCESS_CONTROL_MAX_AGE
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|allowMethods
argument_list|)
operator|.
name|named
argument_list|(
name|ACCESS_CONTROL_ALLOW_METHODS
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|allowHeaders
argument_list|)
operator|.
name|named
argument_list|(
name|ACCESS_CONTROL_ALLOW_HEADERS
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|origins ()
specifier|public
name|void
name|origins
parameter_list|()
throws|throws
name|Exception
block|{
name|Result
name|change
init|=
name|createChange
argument_list|()
decl_stmt|;
name|String
name|url
init|=
literal|"/changes/"
operator|+
name|change
operator|.
name|getChangeId
argument_list|()
operator|+
literal|"/detail"
decl_stmt|;
name|check
argument_list|(
name|url
argument_list|,
literal|true
argument_list|,
literal|"http://example.com"
argument_list|)
expr_stmt|;
name|check
argument_list|(
name|url
argument_list|,
literal|true
argument_list|,
literal|"https://sub.example.com"
argument_list|)
expr_stmt|;
name|check
argument_list|(
name|url
argument_list|,
literal|true
argument_list|,
literal|"http://friend.ly"
argument_list|)
expr_stmt|;
name|check
argument_list|(
name|url
argument_list|,
literal|false
argument_list|,
literal|"http://evil.attacker"
argument_list|)
expr_stmt|;
name|check
argument_list|(
name|url
argument_list|,
literal|false
argument_list|,
literal|"http://friendsly"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|putWithServerOriginAcceptedWithNoCorsResponseHeaders ()
specifier|public
name|void
name|putWithServerOriginAcceptedWithNoCorsResponseHeaders
parameter_list|()
throws|throws
name|Exception
block|{
name|Result
name|change
init|=
name|createChange
argument_list|()
decl_stmt|;
name|String
name|origin
init|=
name|adminRestSession
operator|.
name|url
argument_list|()
decl_stmt|;
name|RestResponse
name|r
init|=
name|adminRestSession
operator|.
name|putWithHeader
argument_list|(
literal|"/changes/"
operator|+
name|change
operator|.
name|getChangeId
argument_list|()
operator|+
literal|"/topic"
argument_list|,
operator|new
name|BasicHeader
argument_list|(
name|ORIGIN
argument_list|,
name|origin
argument_list|)
argument_list|,
literal|"A"
argument_list|)
decl_stmt|;
name|r
operator|.
name|assertOK
argument_list|()
expr_stmt|;
name|checkCors
argument_list|(
name|r
argument_list|,
literal|false
argument_list|,
name|origin
argument_list|)
expr_stmt|;
name|checkTopic
argument_list|(
name|change
argument_list|,
literal|"A"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|putWithOtherOriginAccepted ()
specifier|public
name|void
name|putWithOtherOriginAccepted
parameter_list|()
throws|throws
name|Exception
block|{
name|Result
name|change
init|=
name|createChange
argument_list|()
decl_stmt|;
name|String
name|origin
init|=
literal|"http://example.com"
decl_stmt|;
name|RestResponse
name|r
init|=
name|adminRestSession
operator|.
name|putWithHeader
argument_list|(
literal|"/changes/"
operator|+
name|change
operator|.
name|getChangeId
argument_list|()
operator|+
literal|"/topic"
argument_list|,
operator|new
name|BasicHeader
argument_list|(
name|ORIGIN
argument_list|,
name|origin
argument_list|)
argument_list|,
literal|"A"
argument_list|)
decl_stmt|;
name|r
operator|.
name|assertOK
argument_list|()
expr_stmt|;
name|checkCors
argument_list|(
name|r
argument_list|,
literal|true
argument_list|,
name|origin
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|preflightOk ()
specifier|public
name|void
name|preflightOk
parameter_list|()
throws|throws
name|Exception
block|{
name|Result
name|change
init|=
name|createChange
argument_list|()
decl_stmt|;
name|String
name|origin
init|=
literal|"http://example.com"
decl_stmt|;
name|Request
name|req
init|=
name|Request
operator|.
name|Options
argument_list|(
name|adminRestSession
operator|.
name|url
argument_list|()
operator|+
literal|"/a/changes/"
operator|+
name|change
operator|.
name|getChangeId
argument_list|()
operator|+
literal|"/detail"
argument_list|)
decl_stmt|;
name|req
operator|.
name|addHeader
argument_list|(
name|ORIGIN
argument_list|,
name|origin
argument_list|)
expr_stmt|;
name|req
operator|.
name|addHeader
argument_list|(
name|ACCESS_CONTROL_REQUEST_METHOD
argument_list|,
literal|"GET"
argument_list|)
expr_stmt|;
name|req
operator|.
name|addHeader
argument_list|(
name|ACCESS_CONTROL_REQUEST_HEADERS
argument_list|,
literal|"X-Requested-With"
argument_list|)
expr_stmt|;
name|RestResponse
name|res
init|=
name|adminRestSession
operator|.
name|execute
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|res
operator|.
name|assertOK
argument_list|()
expr_stmt|;
name|String
name|vary
init|=
name|res
operator|.
name|getHeader
argument_list|(
name|VARY
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|vary
argument_list|)
operator|.
name|named
argument_list|(
name|VARY
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|Splitter
operator|.
name|on
argument_list|(
literal|", "
argument_list|)
operator|.
name|splitToList
argument_list|(
name|vary
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|ORIGIN
argument_list|,
name|ACCESS_CONTROL_REQUEST_METHOD
argument_list|,
name|ACCESS_CONTROL_REQUEST_HEADERS
argument_list|)
expr_stmt|;
name|checkCors
argument_list|(
name|res
argument_list|,
literal|true
argument_list|,
name|origin
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|preflightBadOrigin ()
specifier|public
name|void
name|preflightBadOrigin
parameter_list|()
throws|throws
name|Exception
block|{
name|Result
name|change
init|=
name|createChange
argument_list|()
decl_stmt|;
name|Request
name|req
init|=
name|Request
operator|.
name|Options
argument_list|(
name|adminRestSession
operator|.
name|url
argument_list|()
operator|+
literal|"/a/changes/"
operator|+
name|change
operator|.
name|getChangeId
argument_list|()
operator|+
literal|"/detail"
argument_list|)
decl_stmt|;
name|req
operator|.
name|addHeader
argument_list|(
name|ORIGIN
argument_list|,
literal|"http://evil.attacker"
argument_list|)
expr_stmt|;
name|req
operator|.
name|addHeader
argument_list|(
name|ACCESS_CONTROL_REQUEST_METHOD
argument_list|,
literal|"GET"
argument_list|)
expr_stmt|;
name|adminRestSession
operator|.
name|execute
argument_list|(
name|req
argument_list|)
operator|.
name|assertBadRequest
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|preflightBadMethod ()
specifier|public
name|void
name|preflightBadMethod
parameter_list|()
throws|throws
name|Exception
block|{
name|Result
name|change
init|=
name|createChange
argument_list|()
decl_stmt|;
name|Request
name|req
init|=
name|Request
operator|.
name|Options
argument_list|(
name|adminRestSession
operator|.
name|url
argument_list|()
operator|+
literal|"/a/changes/"
operator|+
name|change
operator|.
name|getChangeId
argument_list|()
operator|+
literal|"/detail"
argument_list|)
decl_stmt|;
name|req
operator|.
name|addHeader
argument_list|(
name|ORIGIN
argument_list|,
literal|"http://example.com"
argument_list|)
expr_stmt|;
name|req
operator|.
name|addHeader
argument_list|(
name|ACCESS_CONTROL_REQUEST_METHOD
argument_list|,
literal|"CALL"
argument_list|)
expr_stmt|;
name|adminRestSession
operator|.
name|execute
argument_list|(
name|req
argument_list|)
operator|.
name|assertBadRequest
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|preflightBadHeader ()
specifier|public
name|void
name|preflightBadHeader
parameter_list|()
throws|throws
name|Exception
block|{
name|Result
name|change
init|=
name|createChange
argument_list|()
decl_stmt|;
name|Request
name|req
init|=
name|Request
operator|.
name|Options
argument_list|(
name|adminRestSession
operator|.
name|url
argument_list|()
operator|+
literal|"/a/changes/"
operator|+
name|change
operator|.
name|getChangeId
argument_list|()
operator|+
literal|"/detail"
argument_list|)
decl_stmt|;
name|req
operator|.
name|addHeader
argument_list|(
name|ORIGIN
argument_list|,
literal|"http://example.com"
argument_list|)
expr_stmt|;
name|req
operator|.
name|addHeader
argument_list|(
name|ACCESS_CONTROL_REQUEST_METHOD
argument_list|,
literal|"GET"
argument_list|)
expr_stmt|;
name|req
operator|.
name|addHeader
argument_list|(
name|ACCESS_CONTROL_REQUEST_HEADERS
argument_list|,
literal|"X-Secret-Auth-Token"
argument_list|)
expr_stmt|;
name|adminRestSession
operator|.
name|execute
argument_list|(
name|req
argument_list|)
operator|.
name|assertBadRequest
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|crossDomainPutTopic ()
specifier|public
name|void
name|crossDomainPutTopic
parameter_list|()
throws|throws
name|Exception
block|{
name|Result
name|change
init|=
name|createChange
argument_list|()
decl_stmt|;
name|BasicCookieStore
name|cookies
init|=
operator|new
name|BasicCookieStore
argument_list|()
decl_stmt|;
name|Executor
name|http
init|=
name|Executor
operator|.
name|newInstance
argument_list|()
operator|.
name|cookieStore
argument_list|(
name|cookies
argument_list|)
decl_stmt|;
name|Request
name|req
init|=
name|Request
operator|.
name|Get
argument_list|(
name|canonicalWebUrl
operator|.
name|get
argument_list|()
operator|+
literal|"/login/?account_id="
operator|+
name|admin
operator|.
name|id
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
name|HttpResponse
name|r
init|=
name|http
operator|.
name|execute
argument_list|(
name|req
argument_list|)
operator|.
name|returnResponse
argument_list|()
decl_stmt|;
name|String
name|auth
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Cookie
name|c
range|:
name|cookies
operator|.
name|getCookies
argument_list|()
control|)
block|{
if|if
condition|(
literal|"GerritAccount"
operator|.
name|equals
argument_list|(
name|c
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|auth
operator|=
name|c
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
block|}
name|assertThat
argument_list|(
name|auth
argument_list|)
operator|.
name|named
argument_list|(
literal|"GerritAccount cookie"
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|cookies
operator|.
name|clear
argument_list|()
expr_stmt|;
name|UrlEncoded
name|url
init|=
operator|new
name|UrlEncoded
argument_list|(
name|canonicalWebUrl
operator|.
name|get
argument_list|()
operator|+
literal|"/changes/"
operator|+
name|change
operator|.
name|getChangeId
argument_list|()
operator|+
literal|"/topic"
argument_list|)
decl_stmt|;
name|url
operator|.
name|put
argument_list|(
literal|"$m"
argument_list|,
literal|"PUT"
argument_list|)
expr_stmt|;
name|url
operator|.
name|put
argument_list|(
literal|"$ct"
argument_list|,
literal|"application/json; charset=US-ASCII"
argument_list|)
expr_stmt|;
name|url
operator|.
name|put
argument_list|(
literal|"access_token"
argument_list|,
name|auth
argument_list|)
expr_stmt|;
name|String
name|origin
init|=
literal|"http://example.com"
decl_stmt|;
name|req
operator|=
name|Request
operator|.
name|Post
argument_list|(
name|url
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|req
operator|.
name|setHeader
argument_list|(
name|CONTENT_TYPE
argument_list|,
literal|"text/plain"
argument_list|)
expr_stmt|;
name|req
operator|.
name|setHeader
argument_list|(
name|ORIGIN
argument_list|,
name|origin
argument_list|)
expr_stmt|;
name|req
operator|.
name|bodyByteArray
argument_list|(
literal|"{\"topic\":\"test-xd\"}"
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|US_ASCII
argument_list|)
argument_list|)
expr_stmt|;
name|r
operator|=
name|http
operator|.
name|execute
argument_list|(
name|req
argument_list|)
operator|.
name|returnResponse
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|200
argument_list|)
expr_stmt|;
name|Header
name|vary
init|=
name|r
operator|.
name|getFirstHeader
argument_list|(
name|VARY
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|vary
argument_list|)
operator|.
name|named
argument_list|(
name|VARY
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|Splitter
operator|.
name|on
argument_list|(
literal|", "
argument_list|)
operator|.
name|splitToList
argument_list|(
name|vary
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
operator|.
name|named
argument_list|(
name|VARY
argument_list|)
operator|.
name|contains
argument_list|(
name|ORIGIN
argument_list|)
expr_stmt|;
name|Header
name|allowOrigin
init|=
name|r
operator|.
name|getFirstHeader
argument_list|(
name|ACCESS_CONTROL_ALLOW_ORIGIN
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|allowOrigin
argument_list|)
operator|.
name|named
argument_list|(
name|ACCESS_CONTROL_ALLOW_ORIGIN
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|allowOrigin
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|named
argument_list|(
name|ACCESS_CONTROL_ALLOW_ORIGIN
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|origin
argument_list|)
expr_stmt|;
name|checkTopic
argument_list|(
name|change
argument_list|,
literal|"test-xd"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|crossDomainRejectsBadOrigin ()
specifier|public
name|void
name|crossDomainRejectsBadOrigin
parameter_list|()
throws|throws
name|Exception
block|{
name|Result
name|change
init|=
name|createChange
argument_list|()
decl_stmt|;
name|UrlEncoded
name|url
init|=
operator|new
name|UrlEncoded
argument_list|(
name|canonicalWebUrl
operator|.
name|get
argument_list|()
operator|+
literal|"/changes/"
operator|+
name|change
operator|.
name|getChangeId
argument_list|()
operator|+
literal|"/topic"
argument_list|)
decl_stmt|;
name|url
operator|.
name|put
argument_list|(
literal|"$m"
argument_list|,
literal|"PUT"
argument_list|)
expr_stmt|;
name|url
operator|.
name|put
argument_list|(
literal|"$ct"
argument_list|,
literal|"application/json; charset=US-ASCII"
argument_list|)
expr_stmt|;
name|Request
name|req
init|=
name|Request
operator|.
name|Post
argument_list|(
name|url
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|req
operator|.
name|setHeader
argument_list|(
name|CONTENT_TYPE
argument_list|,
literal|"text/plain"
argument_list|)
expr_stmt|;
name|req
operator|.
name|setHeader
argument_list|(
name|ORIGIN
argument_list|,
literal|"http://evil.attacker"
argument_list|)
expr_stmt|;
name|req
operator|.
name|bodyByteArray
argument_list|(
literal|"{\"topic\":\"test-xd\"}"
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|US_ASCII
argument_list|)
argument_list|)
expr_stmt|;
name|adminRestSession
operator|.
name|execute
argument_list|(
name|req
argument_list|)
operator|.
name|assertBadRequest
argument_list|()
expr_stmt|;
name|checkTopic
argument_list|(
name|change
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|checkTopic (Result change, @Nullable String topic)
specifier|private
name|void
name|checkTopic
parameter_list|(
name|Result
name|change
parameter_list|,
annotation|@
name|Nullable
name|String
name|topic
parameter_list|)
throws|throws
name|RestApiException
block|{
name|ChangeInfo
name|info
init|=
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|change
operator|.
name|getChangeId
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|StringSubject
name|t
init|=
name|assertThat
argument_list|(
name|info
operator|.
name|topic
argument_list|)
operator|.
name|named
argument_list|(
literal|"topic"
argument_list|)
decl_stmt|;
if|if
condition|(
name|topic
operator|!=
literal|null
condition|)
block|{
name|t
operator|.
name|isEqualTo
argument_list|(
name|topic
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|t
operator|.
name|isNull
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|check (String url, boolean accept, String origin)
specifier|private
name|void
name|check
parameter_list|(
name|String
name|url
parameter_list|,
name|boolean
name|accept
parameter_list|,
name|String
name|origin
parameter_list|)
throws|throws
name|Exception
block|{
name|Header
name|hdr
init|=
operator|new
name|BasicHeader
argument_list|(
name|ORIGIN
argument_list|,
name|origin
argument_list|)
decl_stmt|;
name|RestResponse
name|r
init|=
name|adminRestSession
operator|.
name|getWithHeader
argument_list|(
name|url
argument_list|,
name|hdr
argument_list|)
decl_stmt|;
name|r
operator|.
name|assertOK
argument_list|()
expr_stmt|;
name|checkCors
argument_list|(
name|r
argument_list|,
name|accept
argument_list|,
name|origin
argument_list|)
expr_stmt|;
block|}
DECL|method|checkCors (RestResponse r, boolean accept, String origin)
specifier|private
name|void
name|checkCors
parameter_list|(
name|RestResponse
name|r
parameter_list|,
name|boolean
name|accept
parameter_list|,
name|String
name|origin
parameter_list|)
block|{
name|String
name|vary
init|=
name|r
operator|.
name|getHeader
argument_list|(
name|VARY
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|vary
argument_list|)
operator|.
name|named
argument_list|(
name|VARY
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|Splitter
operator|.
name|on
argument_list|(
literal|", "
argument_list|)
operator|.
name|splitToList
argument_list|(
name|vary
argument_list|)
argument_list|)
operator|.
name|named
argument_list|(
name|VARY
argument_list|)
operator|.
name|contains
argument_list|(
name|ORIGIN
argument_list|)
expr_stmt|;
name|String
name|allowOrigin
init|=
name|r
operator|.
name|getHeader
argument_list|(
name|ACCESS_CONTROL_ALLOW_ORIGIN
argument_list|)
decl_stmt|;
name|String
name|allowCred
init|=
name|r
operator|.
name|getHeader
argument_list|(
name|ACCESS_CONTROL_ALLOW_CREDENTIALS
argument_list|)
decl_stmt|;
name|String
name|maxAge
init|=
name|r
operator|.
name|getHeader
argument_list|(
name|ACCESS_CONTROL_MAX_AGE
argument_list|)
decl_stmt|;
name|String
name|allowMethods
init|=
name|r
operator|.
name|getHeader
argument_list|(
name|ACCESS_CONTROL_ALLOW_METHODS
argument_list|)
decl_stmt|;
name|String
name|allowHeaders
init|=
name|r
operator|.
name|getHeader
argument_list|(
name|ACCESS_CONTROL_ALLOW_HEADERS
argument_list|)
decl_stmt|;
if|if
condition|(
name|accept
condition|)
block|{
name|assertThat
argument_list|(
name|allowOrigin
argument_list|)
operator|.
name|named
argument_list|(
name|ACCESS_CONTROL_ALLOW_ORIGIN
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|origin
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|allowCred
argument_list|)
operator|.
name|named
argument_list|(
name|ACCESS_CONTROL_ALLOW_CREDENTIALS
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"true"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|maxAge
argument_list|)
operator|.
name|named
argument_list|(
name|ACCESS_CONTROL_MAX_AGE
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"600"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|allowMethods
argument_list|)
operator|.
name|named
argument_list|(
name|ACCESS_CONTROL_ALLOW_METHODS
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|Splitter
operator|.
name|on
argument_list|(
literal|", "
argument_list|)
operator|.
name|splitToList
argument_list|(
name|allowMethods
argument_list|)
argument_list|)
operator|.
name|named
argument_list|(
name|ACCESS_CONTROL_ALLOW_METHODS
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|"GET"
argument_list|,
literal|"HEAD"
argument_list|,
literal|"POST"
argument_list|,
literal|"PUT"
argument_list|,
literal|"DELETE"
argument_list|,
literal|"OPTIONS"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|allowHeaders
argument_list|)
operator|.
name|named
argument_list|(
name|ACCESS_CONTROL_ALLOW_HEADERS
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|Splitter
operator|.
name|on
argument_list|(
literal|", "
argument_list|)
operator|.
name|splitToList
argument_list|(
name|allowHeaders
argument_list|)
argument_list|)
operator|.
name|named
argument_list|(
name|ACCESS_CONTROL_ALLOW_HEADERS
argument_list|)
operator|.
name|containsExactlyElementsIn
argument_list|(
name|Stream
operator|.
name|of
argument_list|(
name|AUTHORIZATION
argument_list|,
name|CONTENT_TYPE
argument_list|,
literal|"X-Gerrit-Auth"
argument_list|,
literal|"X-Requested-With"
argument_list|)
operator|.
name|map
argument_list|(
name|s
lambda|->
name|s
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|ImmutableSet
operator|.
name|toImmutableSet
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
name|allowOrigin
argument_list|)
operator|.
name|named
argument_list|(
name|ACCESS_CONTROL_ALLOW_ORIGIN
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|allowCred
argument_list|)
operator|.
name|named
argument_list|(
name|ACCESS_CONTROL_ALLOW_CREDENTIALS
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|maxAge
argument_list|)
operator|.
name|named
argument_list|(
name|ACCESS_CONTROL_MAX_AGE
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|allowMethods
argument_list|)
operator|.
name|named
argument_list|(
name|ACCESS_CONTROL_ALLOW_METHODS
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|allowHeaders
argument_list|)
operator|.
name|named
argument_list|(
name|ACCESS_CONTROL_ALLOW_HEADERS
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

