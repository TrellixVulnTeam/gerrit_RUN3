begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2013 The Android Open Source Project
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
DECL|package|com.google.gerrit.acceptance.rest.account
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
name|account
package|;
end_package

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
name|assertFalse
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
name|assertNotNull
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
name|AccessSection
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
name|common
operator|.
name|data
operator|.
name|Permission
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
name|PermissionRule
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
name|gson
operator|.
name|Gson
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|reflect
operator|.
name|TypeToken
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
name|errors
operator|.
name|ConfigInvalidException
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
DECL|class|CapabilitiesIT
specifier|public
class|class
name|CapabilitiesIT
extends|extends
name|AbstractDaemonTest
block|{
annotation|@
name|Test
DECL|method|testCapabilitiesUser ()
specifier|public
name|void
name|testCapabilitiesUser
parameter_list|()
throws|throws
name|IOException
throws|,
name|ConfigInvalidException
throws|,
name|IllegalArgumentException
throws|,
name|IllegalAccessException
throws|,
name|NoSuchFieldException
throws|,
name|SecurityException
block|{
name|grantAllCapabilities
argument_list|()
expr_stmt|;
name|RestResponse
name|r
init|=
name|userSession
operator|.
name|get
argument_list|(
literal|"/accounts/self/capabilities"
argument_list|)
decl_stmt|;
name|int
name|code
init|=
name|r
operator|.
name|getStatusCode
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|code
argument_list|,
literal|200
argument_list|)
expr_stmt|;
name|CapabilityInfo
name|info
init|=
operator|(
operator|new
name|Gson
argument_list|()
operator|)
operator|.
name|fromJson
argument_list|(
name|r
operator|.
name|getReader
argument_list|()
argument_list|,
operator|new
name|TypeToken
argument_list|<
name|CapabilityInfo
argument_list|>
argument_list|()
block|{}
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|c
range|:
name|GlobalCapability
operator|.
name|getAllNames
argument_list|()
control|)
block|{
if|if
condition|(
name|GlobalCapability
operator|.
name|ADMINISTRATE_SERVER
operator|.
name|equals
argument_list|(
name|c
argument_list|)
condition|)
block|{
name|assertFalse
argument_list|(
name|info
operator|.
name|administrateServer
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|GlobalCapability
operator|.
name|PRIORITY
operator|.
name|equals
argument_list|(
name|c
argument_list|)
condition|)
block|{
name|assertFalse
argument_list|(
name|info
operator|.
name|priority
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|GlobalCapability
operator|.
name|QUERY_LIMIT
operator|.
name|equals
argument_list|(
name|c
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|info
operator|.
name|queryLimit
operator|.
name|min
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|info
operator|.
name|queryLimit
operator|.
name|max
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"capability %s was not granted"
argument_list|,
name|c
argument_list|)
argument_list|,
operator|(
name|Boolean
operator|)
name|CapabilityInfo
operator|.
name|class
operator|.
name|getField
argument_list|(
name|c
argument_list|)
operator|.
name|get
argument_list|(
name|info
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testCapabilitiesAdmin ()
specifier|public
name|void
name|testCapabilitiesAdmin
parameter_list|()
throws|throws
name|IOException
throws|,
name|ConfigInvalidException
throws|,
name|IllegalArgumentException
throws|,
name|IllegalAccessException
throws|,
name|NoSuchFieldException
throws|,
name|SecurityException
block|{
name|RestResponse
name|r
init|=
name|adminSession
operator|.
name|get
argument_list|(
literal|"/accounts/self/capabilities"
argument_list|)
decl_stmt|;
name|int
name|code
init|=
name|r
operator|.
name|getStatusCode
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|code
argument_list|,
literal|200
argument_list|)
expr_stmt|;
name|CapabilityInfo
name|info
init|=
operator|(
operator|new
name|Gson
argument_list|()
operator|)
operator|.
name|fromJson
argument_list|(
name|r
operator|.
name|getReader
argument_list|()
argument_list|,
operator|new
name|TypeToken
argument_list|<
name|CapabilityInfo
argument_list|>
argument_list|()
block|{}
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|c
range|:
name|GlobalCapability
operator|.
name|getAllNames
argument_list|()
control|)
block|{
if|if
condition|(
name|GlobalCapability
operator|.
name|PRIORITY
operator|.
name|equals
argument_list|(
name|c
argument_list|)
condition|)
block|{
name|assertFalse
argument_list|(
name|info
operator|.
name|priority
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|GlobalCapability
operator|.
name|QUERY_LIMIT
operator|.
name|equals
argument_list|(
name|c
argument_list|)
condition|)
block|{
name|assertNotNull
argument_list|(
literal|"missing queryLimit"
argument_list|,
name|info
operator|.
name|queryLimit
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|info
operator|.
name|queryLimit
operator|.
name|min
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|500
argument_list|,
name|info
operator|.
name|queryLimit
operator|.
name|max
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|GlobalCapability
operator|.
name|ACCESS_DATABASE
operator|.
name|equals
argument_list|(
name|c
argument_list|)
condition|)
block|{
name|assertFalse
argument_list|(
name|info
operator|.
name|accessDatabase
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|GlobalCapability
operator|.
name|RUN_AS
operator|.
name|equals
argument_list|(
name|c
argument_list|)
condition|)
block|{
name|assertFalse
argument_list|(
name|info
operator|.
name|runAs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"capability %s was not granted"
argument_list|,
name|c
argument_list|)
argument_list|,
operator|(
name|Boolean
operator|)
name|CapabilityInfo
operator|.
name|class
operator|.
name|getField
argument_list|(
name|c
argument_list|)
operator|.
name|get
argument_list|(
name|info
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|grantAllCapabilities ()
specifier|private
name|void
name|grantAllCapabilities
parameter_list|()
throws|throws
name|IOException
throws|,
name|ConfigInvalidException
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
name|md
operator|.
name|setMessage
argument_list|(
literal|"Make super user"
argument_list|)
expr_stmt|;
name|ProjectConfig
name|config
init|=
name|ProjectConfig
operator|.
name|read
argument_list|(
name|md
argument_list|)
decl_stmt|;
name|AccessSection
name|s
init|=
name|config
operator|.
name|getAccessSection
argument_list|(
name|AccessSection
operator|.
name|GLOBAL_CAPABILITIES
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|c
range|:
name|GlobalCapability
operator|.
name|getAllNames
argument_list|()
control|)
block|{
if|if
condition|(
name|GlobalCapability
operator|.
name|ADMINISTRATE_SERVER
operator|.
name|equals
argument_list|(
name|c
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|Permission
name|p
init|=
name|s
operator|.
name|getPermission
argument_list|(
name|c
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|p
operator|.
name|add
argument_list|(
operator|new
name|PermissionRule
argument_list|(
name|config
operator|.
name|resolve
argument_list|(
name|SystemGroupBackend
operator|.
name|getGroup
argument_list|(
name|SystemGroupBackend
operator|.
name|REGISTERED_USERS
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|config
operator|.
name|commit
argument_list|(
name|md
argument_list|)
expr_stmt|;
name|projectCache
operator|.
name|evict
argument_list|(
name|config
operator|.
name|getProject
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

