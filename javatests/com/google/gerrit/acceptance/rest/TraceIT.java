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
DECL|field|listener
specifier|private
name|TraceValidatingProjectCreationValidationListener
name|listener
decl_stmt|;
DECL|field|registrationHandle
specifier|private
name|RegistrationHandle
name|registrationHandle
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|listener
operator|=
operator|new
name|TraceValidatingProjectCreationValidationListener
argument_list|()
expr_stmt|;
name|registrationHandle
operator|=
name|projectCreationValidationListeners
operator|.
name|add
argument_list|(
name|listener
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
name|registrationHandle
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|withoutTrace ()
specifier|public
name|void
name|withoutTrace
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
name|listener
operator|.
name|foundTraceId
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|withTrace ()
specifier|public
name|void
name|withTrace
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
name|listener
operator|.
name|foundTraceId
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|withTraceTrue ()
specifier|public
name|void
name|withTraceTrue
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
literal|"=true"
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
name|listener
operator|.
name|foundTraceId
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|withTraceFalse ()
specifier|public
name|void
name|withTraceFalse
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
literal|"/projects/new4?"
operator|+
name|ParameterParser
operator|.
name|TRACE_PARAMETER
operator|+
literal|"=false"
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
name|listener
operator|.
name|foundTraceId
argument_list|)
operator|.
name|isFalse
argument_list|()
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
DECL|field|foundTraceId
name|Boolean
name|foundTraceId
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
name|foundTraceId
operator|=
name|LoggingContext
operator|.
name|getInstance
argument_list|()
operator|.
name|getTagsAsMap
argument_list|()
operator|.
name|containsKey
argument_list|(
literal|"TRACE_ID"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

