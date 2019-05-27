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
DECL|package|com.google.gerrit.acceptance.rest
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
import|import static
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpStatus
operator|.
name|SC_CREATED
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|auto
operator|.
name|value
operator|.
name|AutoValue
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
name|ImmutableMap
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
name|collect
operator|.
name|Iterables
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
name|Expect
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
name|extensions
operator|.
name|registration
operator|.
name|DynamicSet
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
name|registration
operator|.
name|RegistrationHandle
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
name|httpd
operator|.
name|restapi
operator|.
name|ParameterParser
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
name|httpd
operator|.
name|restapi
operator|.
name|RestApiServlet
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
name|events
operator|.
name|CommitReceivedEvent
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
name|git
operator|.
name|WorkQueue
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
name|git
operator|.
name|validators
operator|.
name|CommitValidationException
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
name|git
operator|.
name|validators
operator|.
name|CommitValidationListener
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
name|git
operator|.
name|validators
operator|.
name|CommitValidationMessage
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
name|logging
operator|.
name|LoggingContext
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
name|logging
operator|.
name|PerformanceLogger
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
name|logging
operator|.
name|TraceContext
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
name|project
operator|.
name|CreateProjectArgs
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
name|validators
operator|.
name|ProjectCreationValidationListener
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
name|validators
operator|.
name|ValidationException
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|SortedMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedSet
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
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
DECL|class|TraceIT
specifier|public
class|class
name|TraceIT
extends|extends
name|AbstractDaemonTest
block|{
DECL|field|expect
annotation|@
name|Rule
specifier|public
specifier|final
name|Expect
name|expect
init|=
name|Expect
operator|.
name|create
argument_list|()
decl_stmt|;
DECL|field|projectCreationValidationListeners
annotation|@
name|Inject
specifier|private
name|DynamicSet
argument_list|<
name|ProjectCreationValidationListener
argument_list|>
name|projectCreationValidationListeners
decl_stmt|;
DECL|field|commitValidationListeners
annotation|@
name|Inject
specifier|private
name|DynamicSet
argument_list|<
name|CommitValidationListener
argument_list|>
name|commitValidationListeners
decl_stmt|;
DECL|field|performanceLoggers
annotation|@
name|Inject
specifier|private
name|DynamicSet
argument_list|<
name|PerformanceLogger
argument_list|>
name|performanceLoggers
decl_stmt|;
DECL|field|workQueue
annotation|@
name|Inject
specifier|private
name|WorkQueue
name|workQueue
decl_stmt|;
DECL|field|projectCreationListener
specifier|private
name|TraceValidatingProjectCreationValidationListener
name|projectCreationListener
decl_stmt|;
DECL|field|projectCreationListenerRegistrationHandle
specifier|private
name|RegistrationHandle
name|projectCreationListenerRegistrationHandle
decl_stmt|;
DECL|field|commitValidationListener
specifier|private
name|TraceValidatingCommitValidationListener
name|commitValidationListener
decl_stmt|;
DECL|field|commitValidationRegistrationHandle
specifier|private
name|RegistrationHandle
name|commitValidationRegistrationHandle
decl_stmt|;
DECL|field|testPerformanceLogger
specifier|private
name|TestPerformanceLogger
name|testPerformanceLogger
decl_stmt|;
DECL|field|performanceLoggerRegistrationHandle
specifier|private
name|RegistrationHandle
name|performanceLoggerRegistrationHandle
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|projectCreationListener
operator|=
operator|new
name|TraceValidatingProjectCreationValidationListener
argument_list|()
expr_stmt|;
name|projectCreationListenerRegistrationHandle
operator|=
name|projectCreationValidationListeners
operator|.
name|add
argument_list|(
literal|"gerrit"
argument_list|,
name|projectCreationListener
argument_list|)
expr_stmt|;
name|commitValidationListener
operator|=
operator|new
name|TraceValidatingCommitValidationListener
argument_list|()
expr_stmt|;
name|commitValidationRegistrationHandle
operator|=
name|commitValidationListeners
operator|.
name|add
argument_list|(
literal|"gerrit"
argument_list|,
name|commitValidationListener
argument_list|)
expr_stmt|;
name|testPerformanceLogger
operator|=
operator|new
name|TestPerformanceLogger
argument_list|()
expr_stmt|;
name|performanceLoggerRegistrationHandle
operator|=
name|performanceLoggers
operator|.
name|add
argument_list|(
literal|"gerrit"
argument_list|,
name|testPerformanceLogger
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|cleanup ()
specifier|public
name|void
name|cleanup
parameter_list|()
block|{
name|projectCreationListenerRegistrationHandle
operator|.
name|remove
argument_list|()
expr_stmt|;
name|commitValidationRegistrationHandle
operator|.
name|remove
argument_list|()
expr_stmt|;
name|performanceLoggerRegistrationHandle
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|restCallWithoutTrace ()
specifier|public
name|void
name|restCallWithoutTrace
parameter_list|()
throws|throws
name|Exception
block|{
name|RestResponse
name|response
init|=
name|adminRestSession
operator|.
name|put
argument_list|(
literal|"/projects/new1"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getStatusCode
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|SC_CREATED
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHeader
argument_list|(
name|RestApiServlet
operator|.
name|X_GERRIT_TRACE
argument_list|)
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|projectCreationListener
operator|.
name|traceId
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|projectCreationListener
operator|.
name|isLoggingForced
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|restCallWithTraceRequestParam ()
specifier|public
name|void
name|restCallWithTraceRequestParam
parameter_list|()
throws|throws
name|Exception
block|{
name|RestResponse
name|response
init|=
name|adminRestSession
operator|.
name|put
argument_list|(
literal|"/projects/new2?"
operator|+
name|ParameterParser
operator|.
name|TRACE_PARAMETER
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getStatusCode
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|SC_CREATED
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHeader
argument_list|(
name|RestApiServlet
operator|.
name|X_GERRIT_TRACE
argument_list|)
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|projectCreationListener
operator|.
name|traceId
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|projectCreationListener
operator|.
name|isLoggingForced
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|restCallWithTraceRequestParamAndProvidedTraceId ()
specifier|public
name|void
name|restCallWithTraceRequestParamAndProvidedTraceId
parameter_list|()
throws|throws
name|Exception
block|{
name|RestResponse
name|response
init|=
name|adminRestSession
operator|.
name|put
argument_list|(
literal|"/projects/new3?"
operator|+
name|ParameterParser
operator|.
name|TRACE_PARAMETER
operator|+
literal|"=issue/123"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getStatusCode
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|SC_CREATED
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHeader
argument_list|(
name|RestApiServlet
operator|.
name|X_GERRIT_TRACE
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"issue/123"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|projectCreationListener
operator|.
name|traceId
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"issue/123"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|projectCreationListener
operator|.
name|isLoggingForced
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|restCallWithTraceHeader ()
specifier|public
name|void
name|restCallWithTraceHeader
parameter_list|()
throws|throws
name|Exception
block|{
name|RestResponse
name|response
init|=
name|adminRestSession
operator|.
name|putWithHeader
argument_list|(
literal|"/projects/new4"
argument_list|,
operator|new
name|BasicHeader
argument_list|(
name|RestApiServlet
operator|.
name|X_GERRIT_TRACE
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getStatusCode
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|SC_CREATED
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHeader
argument_list|(
name|RestApiServlet
operator|.
name|X_GERRIT_TRACE
argument_list|)
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|projectCreationListener
operator|.
name|traceId
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|projectCreationListener
operator|.
name|isLoggingForced
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|restCallWithTraceHeaderAndProvidedTraceId ()
specifier|public
name|void
name|restCallWithTraceHeaderAndProvidedTraceId
parameter_list|()
throws|throws
name|Exception
block|{
name|RestResponse
name|response
init|=
name|adminRestSession
operator|.
name|putWithHeader
argument_list|(
literal|"/projects/new5"
argument_list|,
operator|new
name|BasicHeader
argument_list|(
name|RestApiServlet
operator|.
name|X_GERRIT_TRACE
argument_list|,
literal|"issue/123"
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getStatusCode
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|SC_CREATED
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHeader
argument_list|(
name|RestApiServlet
operator|.
name|X_GERRIT_TRACE
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"issue/123"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|projectCreationListener
operator|.
name|traceId
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"issue/123"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|projectCreationListener
operator|.
name|isLoggingForced
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|restCallWithTraceRequestParamAndTraceHeader ()
specifier|public
name|void
name|restCallWithTraceRequestParamAndTraceHeader
parameter_list|()
throws|throws
name|Exception
block|{
comment|// trace ID only specified by trace header
name|RestResponse
name|response
init|=
name|adminRestSession
operator|.
name|putWithHeader
argument_list|(
literal|"/projects/new6?trace"
argument_list|,
operator|new
name|BasicHeader
argument_list|(
name|RestApiServlet
operator|.
name|X_GERRIT_TRACE
argument_list|,
literal|"issue/123"
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getStatusCode
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|SC_CREATED
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHeader
argument_list|(
name|RestApiServlet
operator|.
name|X_GERRIT_TRACE
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"issue/123"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|projectCreationListener
operator|.
name|traceId
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"issue/123"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|projectCreationListener
operator|.
name|isLoggingForced
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
comment|// trace ID only specified by trace request parameter
name|response
operator|=
name|adminRestSession
operator|.
name|putWithHeader
argument_list|(
literal|"/projects/new7?trace=issue/123"
argument_list|,
operator|new
name|BasicHeader
argument_list|(
name|RestApiServlet
operator|.
name|X_GERRIT_TRACE
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getStatusCode
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|SC_CREATED
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHeader
argument_list|(
name|RestApiServlet
operator|.
name|X_GERRIT_TRACE
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"issue/123"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|projectCreationListener
operator|.
name|traceId
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"issue/123"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|projectCreationListener
operator|.
name|isLoggingForced
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
comment|// same trace ID specified by trace header and trace request parameter
name|response
operator|=
name|adminRestSession
operator|.
name|putWithHeader
argument_list|(
literal|"/projects/new8?trace=issue/123"
argument_list|,
operator|new
name|BasicHeader
argument_list|(
name|RestApiServlet
operator|.
name|X_GERRIT_TRACE
argument_list|,
literal|"issue/123"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getStatusCode
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|SC_CREATED
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHeader
argument_list|(
name|RestApiServlet
operator|.
name|X_GERRIT_TRACE
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"issue/123"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|projectCreationListener
operator|.
name|traceId
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"issue/123"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|projectCreationListener
operator|.
name|isLoggingForced
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
comment|// different trace IDs specified by trace header and trace request parameter
name|response
operator|=
name|adminRestSession
operator|.
name|putWithHeader
argument_list|(
literal|"/projects/new9?trace=issue/123"
argument_list|,
operator|new
name|BasicHeader
argument_list|(
name|RestApiServlet
operator|.
name|X_GERRIT_TRACE
argument_list|,
literal|"issue/456"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getStatusCode
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|SC_CREATED
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHeaders
argument_list|(
name|RestApiServlet
operator|.
name|X_GERRIT_TRACE
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|"issue/123"
argument_list|,
literal|"issue/456"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|projectCreationListener
operator|.
name|traceIds
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|"issue/123"
argument_list|,
literal|"issue/456"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|projectCreationListener
operator|.
name|isLoggingForced
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|pushWithoutTrace ()
specifier|public
name|void
name|pushWithoutTrace
parameter_list|()
throws|throws
name|Exception
block|{
name|PushOneCommit
name|push
init|=
name|pushFactory
operator|.
name|create
argument_list|(
name|admin
operator|.
name|newIdent
argument_list|()
argument_list|,
name|testRepo
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|push
operator|.
name|to
argument_list|(
literal|"refs/heads/master"
argument_list|)
decl_stmt|;
name|r
operator|.
name|assertOkStatus
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|commitValidationListener
operator|.
name|traceId
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|commitValidationListener
operator|.
name|isLoggingForced
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|pushWithTrace ()
specifier|public
name|void
name|pushWithTrace
parameter_list|()
throws|throws
name|Exception
block|{
name|PushOneCommit
name|push
init|=
name|pushFactory
operator|.
name|create
argument_list|(
name|admin
operator|.
name|newIdent
argument_list|()
argument_list|,
name|testRepo
argument_list|)
decl_stmt|;
name|push
operator|.
name|setPushOptions
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"trace"
argument_list|)
argument_list|)
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|push
operator|.
name|to
argument_list|(
literal|"refs/heads/master"
argument_list|)
decl_stmt|;
name|r
operator|.
name|assertOkStatus
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|commitValidationListener
operator|.
name|traceId
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|commitValidationListener
operator|.
name|isLoggingForced
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|pushWithTraceAndProvidedTraceId ()
specifier|public
name|void
name|pushWithTraceAndProvidedTraceId
parameter_list|()
throws|throws
name|Exception
block|{
name|PushOneCommit
name|push
init|=
name|pushFactory
operator|.
name|create
argument_list|(
name|admin
operator|.
name|newIdent
argument_list|()
argument_list|,
name|testRepo
argument_list|)
decl_stmt|;
name|push
operator|.
name|setPushOptions
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"trace=issue/123"
argument_list|)
argument_list|)
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|push
operator|.
name|to
argument_list|(
literal|"refs/heads/master"
argument_list|)
decl_stmt|;
name|r
operator|.
name|assertOkStatus
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|commitValidationListener
operator|.
name|traceId
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"issue/123"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|commitValidationListener
operator|.
name|isLoggingForced
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|pushForReviewWithoutTrace ()
specifier|public
name|void
name|pushForReviewWithoutTrace
parameter_list|()
throws|throws
name|Exception
block|{
name|PushOneCommit
name|push
init|=
name|pushFactory
operator|.
name|create
argument_list|(
name|admin
operator|.
name|newIdent
argument_list|()
argument_list|,
name|testRepo
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|push
operator|.
name|to
argument_list|(
literal|"refs/for/master"
argument_list|)
decl_stmt|;
name|r
operator|.
name|assertOkStatus
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|commitValidationListener
operator|.
name|traceId
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|commitValidationListener
operator|.
name|isLoggingForced
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|pushForReviewWithTrace ()
specifier|public
name|void
name|pushForReviewWithTrace
parameter_list|()
throws|throws
name|Exception
block|{
name|PushOneCommit
name|push
init|=
name|pushFactory
operator|.
name|create
argument_list|(
name|admin
operator|.
name|newIdent
argument_list|()
argument_list|,
name|testRepo
argument_list|)
decl_stmt|;
name|push
operator|.
name|setPushOptions
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"trace"
argument_list|)
argument_list|)
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|push
operator|.
name|to
argument_list|(
literal|"refs/for/master"
argument_list|)
decl_stmt|;
name|r
operator|.
name|assertOkStatus
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|commitValidationListener
operator|.
name|traceId
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|commitValidationListener
operator|.
name|isLoggingForced
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|pushForReviewWithTraceAndProvidedTraceId ()
specifier|public
name|void
name|pushForReviewWithTraceAndProvidedTraceId
parameter_list|()
throws|throws
name|Exception
block|{
name|PushOneCommit
name|push
init|=
name|pushFactory
operator|.
name|create
argument_list|(
name|admin
operator|.
name|newIdent
argument_list|()
argument_list|,
name|testRepo
argument_list|)
decl_stmt|;
name|push
operator|.
name|setPushOptions
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"trace=issue/123"
argument_list|)
argument_list|)
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|push
operator|.
name|to
argument_list|(
literal|"refs/for/master"
argument_list|)
decl_stmt|;
name|r
operator|.
name|assertOkStatus
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|commitValidationListener
operator|.
name|traceId
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"issue/123"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|commitValidationListener
operator|.
name|isLoggingForced
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|workQueueCopyLoggingContext ()
specifier|public
name|void
name|workQueueCopyLoggingContext
parameter_list|()
throws|throws
name|Exception
block|{
name|assertThat
argument_list|(
name|LoggingContext
operator|.
name|getInstance
argument_list|()
operator|.
name|getTags
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertForceLogging
argument_list|(
literal|false
argument_list|)
expr_stmt|;
try|try
init|(
name|TraceContext
name|traceContext
init|=
name|TraceContext
operator|.
name|open
argument_list|()
operator|.
name|forceLogging
argument_list|()
operator|.
name|addTag
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
init|)
block|{
name|SortedMap
argument_list|<
name|String
argument_list|,
name|SortedSet
argument_list|<
name|Object
argument_list|>
argument_list|>
name|tagMap
init|=
name|LoggingContext
operator|.
name|getInstance
argument_list|()
operator|.
name|getTags
argument_list|()
operator|.
name|asMap
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|tagMap
operator|.
name|keySet
argument_list|()
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tagMap
operator|.
name|get
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|"bar"
argument_list|)
expr_stmt|;
name|assertForceLogging
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|workQueue
operator|.
name|createQueue
argument_list|(
literal|1
argument_list|,
literal|"test-queue"
argument_list|)
operator|.
name|submit
argument_list|(
parameter_list|()
lambda|->
block|{
comment|// Verify that the tags and force logging flag have been propagated to the new
comment|// thread.
name|SortedMap
argument_list|<
name|String
argument_list|,
name|SortedSet
argument_list|<
name|Object
argument_list|>
argument_list|>
name|threadTagMap
init|=
name|LoggingContext
operator|.
name|getInstance
argument_list|()
operator|.
name|getTags
argument_list|()
operator|.
name|asMap
argument_list|()
decl_stmt|;
name|expect
operator|.
name|that
argument_list|(
name|threadTagMap
operator|.
name|keySet
argument_list|()
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|expect
operator|.
name|that
argument_list|(
name|threadTagMap
operator|.
name|get
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|"bar"
argument_list|)
expr_stmt|;
name|expect
operator|.
name|that
argument_list|(
name|LoggingContext
operator|.
name|getInstance
argument_list|()
operator|.
name|shouldForceLogging
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
block|}
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
comment|// Verify that tags and force logging flag in the outer thread are still set.
name|tagMap
operator|=
name|LoggingContext
operator|.
name|getInstance
argument_list|()
operator|.
name|getTags
argument_list|()
operator|.
name|asMap
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|tagMap
operator|.
name|keySet
argument_list|()
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tagMap
operator|.
name|get
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|"bar"
argument_list|)
expr_stmt|;
name|assertForceLogging
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|LoggingContext
operator|.
name|getInstance
argument_list|()
operator|.
name|getTags
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertForceLogging
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|performanceLoggingForRestCall ()
specifier|public
name|void
name|performanceLoggingForRestCall
parameter_list|()
throws|throws
name|Exception
block|{
name|RestResponse
name|response
init|=
name|adminRestSession
operator|.
name|put
argument_list|(
literal|"/projects/new10"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getStatusCode
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|SC_CREATED
argument_list|)
expr_stmt|;
comment|// This assertion assumes that the server invokes the PerformanceLogger plugins before it sends
comment|// the response to the client. If this assertion gets flaky it's likely that this got changed on
comment|// server-side.
name|assertThat
argument_list|(
name|testPerformanceLogger
operator|.
name|logEntries
argument_list|()
argument_list|)
operator|.
name|isNotEmpty
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|performanceLoggingForPush ()
specifier|public
name|void
name|performanceLoggingForPush
parameter_list|()
throws|throws
name|Exception
block|{
name|PushOneCommit
name|push
init|=
name|pushFactory
operator|.
name|create
argument_list|(
name|admin
operator|.
name|newIdent
argument_list|()
argument_list|,
name|testRepo
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|push
operator|.
name|to
argument_list|(
literal|"refs/heads/master"
argument_list|)
decl_stmt|;
name|r
operator|.
name|assertOkStatus
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|testPerformanceLogger
operator|.
name|logEntries
argument_list|()
argument_list|)
operator|.
name|isNotEmpty
argument_list|()
expr_stmt|;
block|}
DECL|method|assertForceLogging (boolean expected)
specifier|private
name|void
name|assertForceLogging
parameter_list|(
name|boolean
name|expected
parameter_list|)
block|{
name|assertThat
argument_list|(
name|LoggingContext
operator|.
name|getInstance
argument_list|()
operator|.
name|shouldForceLogging
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
DECL|class|TraceValidatingProjectCreationValidationListener
specifier|private
specifier|static
class|class
name|TraceValidatingProjectCreationValidationListener
implements|implements
name|ProjectCreationValidationListener
block|{
DECL|field|traceId
name|String
name|traceId
decl_stmt|;
DECL|field|traceIds
name|ImmutableSet
argument_list|<
name|String
argument_list|>
name|traceIds
decl_stmt|;
DECL|field|isLoggingForced
name|Boolean
name|isLoggingForced
decl_stmt|;
annotation|@
name|Override
DECL|method|validateNewProject (CreateProjectArgs args)
specifier|public
name|void
name|validateNewProject
parameter_list|(
name|CreateProjectArgs
name|args
parameter_list|)
throws|throws
name|ValidationException
block|{
name|this
operator|.
name|traceId
operator|=
name|Iterables
operator|.
name|getFirst
argument_list|(
name|LoggingContext
operator|.
name|getInstance
argument_list|()
operator|.
name|getTagsAsMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"TRACE_ID"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|traceIds
operator|=
name|LoggingContext
operator|.
name|getInstance
argument_list|()
operator|.
name|getTagsAsMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"TRACE_ID"
argument_list|)
expr_stmt|;
name|this
operator|.
name|isLoggingForced
operator|=
name|LoggingContext
operator|.
name|getInstance
argument_list|()
operator|.
name|shouldForceLogging
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|TraceValidatingCommitValidationListener
specifier|private
specifier|static
class|class
name|TraceValidatingCommitValidationListener
implements|implements
name|CommitValidationListener
block|{
DECL|field|traceId
name|String
name|traceId
decl_stmt|;
DECL|field|isLoggingForced
name|Boolean
name|isLoggingForced
decl_stmt|;
annotation|@
name|Override
DECL|method|onCommitReceived (CommitReceivedEvent receiveEvent)
specifier|public
name|List
argument_list|<
name|CommitValidationMessage
argument_list|>
name|onCommitReceived
parameter_list|(
name|CommitReceivedEvent
name|receiveEvent
parameter_list|)
throws|throws
name|CommitValidationException
block|{
name|this
operator|.
name|traceId
operator|=
name|Iterables
operator|.
name|getFirst
argument_list|(
name|LoggingContext
operator|.
name|getInstance
argument_list|()
operator|.
name|getTagsAsMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"TRACE_ID"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|isLoggingForced
operator|=
name|LoggingContext
operator|.
name|getInstance
argument_list|()
operator|.
name|shouldForceLogging
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
block|}
DECL|class|TestPerformanceLogger
specifier|private
specifier|static
class|class
name|TestPerformanceLogger
implements|implements
name|PerformanceLogger
block|{
DECL|field|logEntries
specifier|private
name|List
argument_list|<
name|PerformanceLogEntry
argument_list|>
name|logEntries
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|log (String operation, long durationMs, Map<String, Optional<Object>> metaData)
specifier|public
name|void
name|log
parameter_list|(
name|String
name|operation
parameter_list|,
name|long
name|durationMs
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Optional
argument_list|<
name|Object
argument_list|>
argument_list|>
name|metaData
parameter_list|)
block|{
name|logEntries
operator|.
name|add
argument_list|(
name|PerformanceLogEntry
operator|.
name|create
argument_list|(
name|operation
argument_list|,
name|metaData
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|logEntries ()
name|ImmutableList
argument_list|<
name|PerformanceLogEntry
argument_list|>
name|logEntries
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|logEntries
argument_list|)
return|;
block|}
block|}
annotation|@
name|AutoValue
DECL|class|PerformanceLogEntry
specifier|abstract
specifier|static
class|class
name|PerformanceLogEntry
block|{
DECL|method|create (String operation, Map<String, Optional<Object>> metaData)
specifier|static
name|PerformanceLogEntry
name|create
parameter_list|(
name|String
name|operation
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Optional
argument_list|<
name|Object
argument_list|>
argument_list|>
name|metaData
parameter_list|)
block|{
return|return
operator|new
name|AutoValue_TraceIT_PerformanceLogEntry
argument_list|(
name|operation
argument_list|,
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|metaData
argument_list|)
argument_list|)
return|;
block|}
DECL|method|operation ()
specifier|abstract
name|String
name|operation
parameter_list|()
function_decl|;
DECL|method|metaData ()
specifier|abstract
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|metaData
parameter_list|()
function_decl|;
block|}
block|}
end_class

end_unit

