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
name|FLUSH
import|;
end_import

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
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|group
operator|.
name|SystemGroupBackend
operator|.
name|REGISTERED_USERS
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
name|common
operator|.
name|data
operator|.
name|GlobalCapability
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
name|AccountGroup
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
name|AllProjectsName
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
name|MetaDataUpdate
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
name|ProjectConfig
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
name|group
operator|.
name|SystemGroupBackend
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
name|ProjectCache
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
name|Util
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|Inject
DECL|field|projectCache
specifier|private
name|ProjectCache
name|projectCache
decl_stmt|;
annotation|@
name|Inject
DECL|field|allProjects
specifier|private
name|AllProjectsName
name|allProjects
decl_stmt|;
annotation|@
name|Inject
DECL|field|metaDataUpdateFactory
specifier|private
name|MetaDataUpdate
operator|.
name|Server
name|metaDataUpdateFactory
decl_stmt|;
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
annotation|@
name|Test
DECL|method|flushAll_BadRequest ()
specifier|public
name|void
name|flushAll_BadRequest
parameter_list|()
throws|throws
name|IOException
block|{
name|RestResponse
name|r
init|=
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
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"projects"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_BAD_REQUEST
argument_list|,
name|r
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|flush ()
specifier|public
name|void
name|flush
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
name|get
argument_list|(
literal|"/config/server/caches/projects"
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
literal|1
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
name|FLUSH
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"accounts"
argument_list|,
literal|"project_list"
argument_list|)
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
name|r
operator|=
name|adminSession
operator|.
name|get
argument_list|(
literal|"/config/server/caches/projects"
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
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|flush_Forbidden ()
specifier|public
name|void
name|flush_Forbidden
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
name|FLUSH
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"projects"
argument_list|)
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
annotation|@
name|Test
DECL|method|flush_BadRequest ()
specifier|public
name|void
name|flush_BadRequest
parameter_list|()
throws|throws
name|IOException
block|{
name|RestResponse
name|r
init|=
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
name|FLUSH
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_BAD_REQUEST
argument_list|,
name|r
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|flush_UnprocessableEntity ()
specifier|public
name|void
name|flush_UnprocessableEntity
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
literal|"/config/server/caches/projects"
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
name|FLUSH
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"projects"
argument_list|,
literal|"unprocessable"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_UNPROCESSABLE_ENTITY
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
literal|"/config/server/caches/projects"
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
block|}
annotation|@
name|Test
DECL|method|flushWebSessions_Forbidden ()
specifier|public
name|void
name|flushWebSessions_Forbidden
parameter_list|()
throws|throws
name|IOException
block|{
name|ProjectConfig
name|cfg
init|=
name|projectCache
operator|.
name|checkedGet
argument_list|(
name|allProjects
argument_list|)
operator|.
name|getConfig
argument_list|()
decl_stmt|;
name|AccountGroup
operator|.
name|UUID
name|registeredUsers
init|=
name|SystemGroupBackend
operator|.
name|getGroup
argument_list|(
name|REGISTERED_USERS
argument_list|)
operator|.
name|getUUID
argument_list|()
decl_stmt|;
name|Util
operator|.
name|allow
argument_list|(
name|cfg
argument_list|,
name|GlobalCapability
operator|.
name|VIEW_CACHES
argument_list|,
name|registeredUsers
argument_list|)
expr_stmt|;
name|Util
operator|.
name|allow
argument_list|(
name|cfg
argument_list|,
name|GlobalCapability
operator|.
name|FLUSH_CACHES
argument_list|,
name|registeredUsers
argument_list|)
expr_stmt|;
name|saveProjectConfig
argument_list|(
name|cfg
argument_list|)
expr_stmt|;
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
name|FLUSH
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"projects"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
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
name|FLUSH
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"web_sessions"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
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
DECL|method|saveProjectConfig (ProjectConfig cfg)
specifier|private
name|void
name|saveProjectConfig
parameter_list|(
name|ProjectConfig
name|cfg
parameter_list|)
throws|throws
name|IOException
block|{
name|MetaDataUpdate
name|md
init|=
name|metaDataUpdateFactory
operator|.
name|create
argument_list|(
name|allProjects
argument_list|)
decl_stmt|;
try|try
block|{
name|cfg
operator|.
name|commit
argument_list|(
name|md
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|md
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|projectCache
operator|.
name|evict
argument_list|(
name|allProjects
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

