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
DECL|package|com.google.gerrit.acceptance
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
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
name|assertTrue
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
name|io
operator|.
name|Reader
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
name|Set
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
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
name|reviewdb
operator|.
name|server
operator|.
name|ReviewDb
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
name|gwtorm
operator|.
name|server
operator|.
name|SchemaFactory
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

begin_comment
comment|/**  * An example test that tests presence of system groups in a newly initialized  * review site.  *  * The test shows how to perform these checks via SSH, REST or using Gerrit  * internals.  */
end_comment

begin_class
DECL|class|SystemGroupsIT
specifier|public
class|class
name|SystemGroupsIT
extends|extends
name|AbstractDaemonTest
block|{
annotation|@
name|Inject
DECL|field|reviewDbProvider
specifier|private
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|reviewDbProvider
decl_stmt|;
annotation|@
name|Inject
DECL|field|accounts
specifier|private
name|AccountCreator
name|accounts
decl_stmt|;
DECL|field|admin
specifier|protected
name|TestAccount
name|admin
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
literal|"admin@sap.com"
argument_list|,
literal|"Administrator"
argument_list|,
literal|"Administrators"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|systemGroupsCreated_ssh ()
specifier|public
name|void
name|systemGroupsCreated_ssh
parameter_list|()
throws|throws
name|JSchException
throws|,
name|IOException
block|{
name|SshSession
name|session
init|=
operator|new
name|SshSession
argument_list|(
name|admin
argument_list|)
decl_stmt|;
name|String
name|result
init|=
name|session
operator|.
name|exec
argument_list|(
literal|"gerrit ls-groups"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|contains
argument_list|(
literal|"Administrators"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|contains
argument_list|(
literal|"Anonymous Users"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|contains
argument_list|(
literal|"Non-Interactive Users"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|contains
argument_list|(
literal|"Project Owners"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|contains
argument_list|(
literal|"Registered Users"
argument_list|)
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
DECL|class|Group
specifier|private
specifier|static
class|class
name|Group
block|{
DECL|field|id
name|String
name|id
decl_stmt|;
DECL|field|name
name|String
name|name
decl_stmt|;
DECL|field|url
name|String
name|url
decl_stmt|;
DECL|field|description
name|String
name|description
decl_stmt|;
DECL|field|groupId
name|Integer
name|groupId
decl_stmt|;
DECL|field|ownerId
name|String
name|ownerId
decl_stmt|;
block|}
empty_stmt|;
annotation|@
name|Test
DECL|method|systemGroupsCreated_rest ()
specifier|public
name|void
name|systemGroupsCreated_rest
parameter_list|()
throws|throws
name|IOException
block|{
name|RestSession
name|session
init|=
operator|new
name|RestSession
argument_list|(
name|admin
argument_list|)
decl_stmt|;
name|Reader
name|r
init|=
name|session
operator|.
name|get
argument_list|(
literal|"/groups/"
argument_list|)
decl_stmt|;
name|Gson
name|gson
init|=
operator|new
name|Gson
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
name|Map
argument_list|<
name|String
argument_list|,
name|Group
argument_list|>
name|result
init|=
name|gson
operator|.
name|fromJson
argument_list|(
name|r
argument_list|,
operator|new
name|TypeToken
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Group
argument_list|>
argument_list|>
argument_list|()
block|{}
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|names
init|=
name|result
operator|.
name|keySet
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|names
operator|.
name|contains
argument_list|(
literal|"Administrators"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|names
operator|.
name|contains
argument_list|(
literal|"Anonymous Users"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|names
operator|.
name|contains
argument_list|(
literal|"Non-Interactive Users"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|names
operator|.
name|contains
argument_list|(
literal|"Project Owners"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|names
operator|.
name|contains
argument_list|(
literal|"Registered Users"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|systemGroupsCreated_internals ()
specifier|public
name|void
name|systemGroupsCreated_internals
parameter_list|()
throws|throws
name|OrmException
block|{
name|ReviewDb
name|db
init|=
name|reviewDbProvider
operator|.
name|open
argument_list|()
decl_stmt|;
try|try
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|names
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|AccountGroup
name|g
range|:
name|db
operator|.
name|accountGroups
argument_list|()
operator|.
name|all
argument_list|()
control|)
block|{
name|names
operator|.
name|add
argument_list|(
name|g
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|names
operator|.
name|contains
argument_list|(
literal|"Administrators"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|names
operator|.
name|contains
argument_list|(
literal|"Anonymous Users"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|names
operator|.
name|contains
argument_list|(
literal|"Non-Interactive Users"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|names
operator|.
name|contains
argument_list|(
literal|"Project Owners"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|names
operator|.
name|contains
argument_list|(
literal|"Registered Users"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|db
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

