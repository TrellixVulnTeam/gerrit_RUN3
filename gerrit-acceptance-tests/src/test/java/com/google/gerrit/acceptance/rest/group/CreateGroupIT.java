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
DECL|package|com.google.gerrit.acceptance.rest.group
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
name|group
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
name|acceptance
operator|.
name|rest
operator|.
name|group
operator|.
name|GroupAssert
operator|.
name|assertGroupInfo
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
name|assertNotNull
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
name|AccountCreator
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
name|acceptance
operator|.
name|RestSession
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
name|TestAccount
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
name|account
operator|.
name|GroupCache
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
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|server
operator|.
name|OrmException
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
name|jcraft
operator|.
name|jsch
operator|.
name|JSchException
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
DECL|class|CreateGroupIT
specifier|public
class|class
name|CreateGroupIT
extends|extends
name|AbstractDaemonTest
block|{
annotation|@
name|Inject
DECL|field|accounts
specifier|private
name|AccountCreator
name|accounts
decl_stmt|;
annotation|@
name|Inject
DECL|field|groupCache
specifier|private
name|GroupCache
name|groupCache
decl_stmt|;
DECL|field|admin
specifier|private
name|TestAccount
name|admin
decl_stmt|;
DECL|field|session
specifier|private
name|RestSession
name|session
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|admin
operator|=
name|accounts
operator|.
name|create
argument_list|(
literal|"admin"
argument_list|,
literal|"admin@example.com"
argument_list|,
literal|"Administrator"
argument_list|,
literal|"Administrators"
argument_list|)
expr_stmt|;
name|session
operator|=
operator|new
name|RestSession
argument_list|(
name|admin
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateGroup ()
specifier|public
name|void
name|testCreateGroup
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|newGroupName
init|=
literal|"newGroup"
decl_stmt|;
name|RestResponse
name|r
init|=
name|session
operator|.
name|put
argument_list|(
literal|"/groups/"
operator|+
name|newGroupName
argument_list|)
decl_stmt|;
name|GroupInfo
name|g
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
name|GroupInfo
argument_list|>
argument_list|()
block|{}
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|newGroupName
argument_list|,
name|g
operator|.
name|name
argument_list|)
expr_stmt|;
name|AccountGroup
name|group
init|=
name|groupCache
operator|.
name|get
argument_list|(
operator|new
name|AccountGroup
operator|.
name|NameKey
argument_list|(
name|newGroupName
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|group
argument_list|)
expr_stmt|;
name|assertGroupInfo
argument_list|(
name|group
argument_list|,
name|g
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateGroupWithProperties ()
specifier|public
name|void
name|testCreateGroupWithProperties
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|newGroupName
init|=
literal|"newGroup"
decl_stmt|;
name|GroupInput
name|in
init|=
operator|new
name|GroupInput
argument_list|()
decl_stmt|;
name|in
operator|.
name|description
operator|=
literal|"Test description"
expr_stmt|;
name|in
operator|.
name|visible_to_all
operator|=
literal|true
expr_stmt|;
name|in
operator|.
name|owner_id
operator|=
name|groupCache
operator|.
name|get
argument_list|(
operator|new
name|AccountGroup
operator|.
name|NameKey
argument_list|(
literal|"Administrators"
argument_list|)
argument_list|)
operator|.
name|getGroupUUID
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
name|RestResponse
name|r
init|=
name|session
operator|.
name|put
argument_list|(
literal|"/groups/"
operator|+
name|newGroupName
argument_list|,
name|in
argument_list|)
decl_stmt|;
name|GroupInfo
name|g
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
name|GroupInfo
argument_list|>
argument_list|()
block|{}
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|newGroupName
argument_list|,
name|g
operator|.
name|name
argument_list|)
expr_stmt|;
name|AccountGroup
name|group
init|=
name|groupCache
operator|.
name|get
argument_list|(
operator|new
name|AccountGroup
operator|.
name|NameKey
argument_list|(
name|newGroupName
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|in
operator|.
name|description
argument_list|,
name|group
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|in
operator|.
name|visible_to_all
argument_list|,
name|group
operator|.
name|isVisibleToAll
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|in
operator|.
name|owner_id
argument_list|,
name|group
operator|.
name|getOwnerGroupUUID
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateGroupWithoutCapability_Forbidden ()
specifier|public
name|void
name|testCreateGroupWithoutCapability_Forbidden
parameter_list|()
throws|throws
name|OrmException
throws|,
name|JSchException
throws|,
name|IOException
block|{
name|TestAccount
name|user
init|=
name|accounts
operator|.
name|create
argument_list|(
literal|"user"
argument_list|,
literal|"user@example.com"
argument_list|,
literal|"User"
argument_list|)
decl_stmt|;
name|RestResponse
name|r
init|=
operator|(
operator|new
name|RestSession
argument_list|(
name|user
argument_list|)
operator|)
operator|.
name|put
argument_list|(
literal|"/groups/newGroup"
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
DECL|method|testCreateGroupWhenGroupAlreadyExists_Conflict ()
specifier|public
name|void
name|testCreateGroupWhenGroupAlreadyExists_Conflict
parameter_list|()
throws|throws
name|OrmException
throws|,
name|JSchException
throws|,
name|IOException
block|{
name|RestResponse
name|r
init|=
name|session
operator|.
name|put
argument_list|(
literal|"/groups/Administrators"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_CONFLICT
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

