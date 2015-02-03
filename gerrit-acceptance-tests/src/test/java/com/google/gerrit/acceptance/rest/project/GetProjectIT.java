begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2015 The Android Open Source Project
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
DECL|package|com.google.gerrit.acceptance.rest.project
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
name|project
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
name|common
operator|.
name|ProjectInfo
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
name|HttpStatus
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
DECL|class|GetProjectIT
specifier|public
class|class
name|GetProjectIT
extends|extends
name|AbstractDaemonTest
block|{
annotation|@
name|Test
DECL|method|getProject ()
specifier|public
name|void
name|getProject
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|name
init|=
name|project
operator|.
name|get
argument_list|()
decl_stmt|;
name|RestResponse
name|r
init|=
name|adminSession
operator|.
name|get
argument_list|(
literal|"/projects/"
operator|+
name|name
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getStatusCode
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|HttpStatus
operator|.
name|SC_OK
argument_list|)
expr_stmt|;
name|ProjectInfo
name|p
init|=
name|newGson
argument_list|()
operator|.
name|fromJson
argument_list|(
name|r
operator|.
name|getReader
argument_list|()
argument_list|,
name|ProjectInfo
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|p
operator|.
name|name
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|getProjectWithGitSuffix ()
specifier|public
name|void
name|getProjectWithGitSuffix
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|name
init|=
name|project
operator|.
name|get
argument_list|()
decl_stmt|;
name|RestResponse
name|r
init|=
name|adminSession
operator|.
name|get
argument_list|(
literal|"/projects/"
operator|+
name|name
operator|+
literal|".git"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getStatusCode
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|HttpStatus
operator|.
name|SC_OK
argument_list|)
expr_stmt|;
name|ProjectInfo
name|p
init|=
name|newGson
argument_list|()
operator|.
name|fromJson
argument_list|(
name|r
operator|.
name|getReader
argument_list|()
argument_list|,
name|ProjectInfo
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|p
operator|.
name|name
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|getProjectNotExisting ()
specifier|public
name|void
name|getProjectNotExisting
parameter_list|()
throws|throws
name|Exception
block|{
name|RestResponse
name|r
init|=
name|adminSession
operator|.
name|get
argument_list|(
literal|"/projects/does-not-exist"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getStatusCode
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|HttpStatus
operator|.
name|SC_NOT_FOUND
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

