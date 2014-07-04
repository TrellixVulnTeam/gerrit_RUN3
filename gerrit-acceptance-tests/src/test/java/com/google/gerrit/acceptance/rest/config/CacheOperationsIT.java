begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2014 The Android Open Source Project
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
DECL|package|com.google.gerrit.acceptance.rest.config
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
name|config
package|;
end_package

begin_import
import|import static
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
name|PostCaches
operator|.
name|Operation
operator|.
name|FLUSH_ALL
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|server
operator|.
name|config
operator|.
name|ListCaches
operator|.
name|CacheInfo
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
name|PostCaches
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_class
DECL|class|CacheOperationsIT
specifier|public
class|class
name|CacheOperationsIT
extends|extends
name|AbstractDaemonTest
block|{
annotation|@
name|Test
DECL|method|flushAll ()
specifier|public
name|void
name|flushAll
parameter_list|()
throws|throws
name|IOException
block|{
name|RestResponse
name|r
init|=
name|adminSession
operator|.
name|get
argument_list|(
literal|"/config/server/caches/project_list"
argument_list|)
decl_stmt|;
name|CacheInfo
name|cacheInfo
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
name|CacheInfo
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|cacheInfo
operator|.
name|entries
operator|.
name|mem
operator|.
name|longValue
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|r
operator|=
name|adminSession
operator|.
name|post
argument_list|(
literal|"/config/server/caches/"
argument_list|,
operator|new
name|PostCaches
operator|.
name|Input
argument_list|(
name|FLUSH_ALL
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_OK
argument_list|,
name|r
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|consume
argument_list|()
expr_stmt|;
name|r
operator|=
name|adminSession
operator|.
name|get
argument_list|(
literal|"/config/server/caches/project_list"
argument_list|)
expr_stmt|;
name|cacheInfo
operator|=
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
name|CacheInfo
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|cacheInfo
operator|.
name|entries
operator|.
name|mem
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|flushAll_Forbidden ()
specifier|public
name|void
name|flushAll_Forbidden
parameter_list|()
throws|throws
name|IOException
block|{
name|RestResponse
name|r
init|=
name|userSession
operator|.
name|post
argument_list|(
literal|"/config/server/caches/"
argument_list|,
operator|new
name|PostCaches
operator|.
name|Input
argument_list|(
name|FLUSH_ALL
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_FORBIDDEN
argument_list|,
name|r
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

