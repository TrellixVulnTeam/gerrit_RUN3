begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2017 The Android Open Source Project
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
DECL|package|com.google.gerrit.acceptance.api.group
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|api
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
name|NoHttpd
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
name|Sandboxed
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
name|extensions
operator|.
name|api
operator|.
name|config
operator|.
name|ConsistencyCheckInfo
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
name|api
operator|.
name|config
operator|.
name|ConsistencyCheckInfo
operator|.
name|ConsistencyProblemInfo
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
name|api
operator|.
name|config
operator|.
name|ConsistencyCheckInput
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
name|GroupInfo
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
name|client
operator|.
name|RefNames
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
name|db
operator|.
name|GroupConfig
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
name|db
operator|.
name|GroupNameNotes
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
name|db
operator|.
name|testing
operator|.
name|GroupTestUtil
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
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|RefRename
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
name|RefUpdate
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
name|RefUpdate
operator|.
name|Result
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
name|Repository
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

begin_comment
comment|/**  * Checks that invalid group configurations are flagged. Since the inconsistencies are global to the  * test server configuration, and leak from one test method into the next one, there is no way for  * this test to not be sandboxed.  */
end_comment

begin_class
annotation|@
name|Sandboxed
annotation|@
name|NoHttpd
DECL|class|GroupsConsistencyIT
specifier|public
class|class
name|GroupsConsistencyIT
extends|extends
name|AbstractDaemonTest
block|{
DECL|field|gAdmin
specifier|private
name|GroupInfo
name|gAdmin
decl_stmt|;
DECL|field|g1
specifier|private
name|GroupInfo
name|g1
decl_stmt|;
DECL|field|g2
specifier|private
name|GroupInfo
name|g2
decl_stmt|;
DECL|field|BOGUS_UUID
specifier|private
specifier|static
specifier|final
name|String
name|BOGUS_UUID
init|=
literal|"deadbeefdeadbeefdeadbeefdeadbeefdeadbeef"
decl_stmt|;
annotation|@
name|Before
DECL|method|basicSetup ()
specifier|public
name|void
name|basicSetup
parameter_list|()
throws|throws
name|Exception
block|{
name|allowGlobalCapabilities
argument_list|(
name|REGISTERED_USERS
argument_list|,
name|GlobalCapability
operator|.
name|ACCESS_DATABASE
argument_list|)
expr_stmt|;
name|String
name|name1
init|=
name|createGroup
argument_list|(
literal|"g1"
argument_list|)
decl_stmt|;
name|String
name|name2
init|=
name|createGroup
argument_list|(
literal|"g2"
argument_list|)
decl_stmt|;
name|gApi
operator|.
name|groups
argument_list|()
operator|.
name|id
argument_list|(
name|name1
argument_list|)
operator|.
name|addMembers
argument_list|(
name|user
operator|.
name|fullName
argument_list|)
expr_stmt|;
name|gApi
operator|.
name|groups
argument_list|()
operator|.
name|id
argument_list|(
name|name2
argument_list|)
operator|.
name|addMembers
argument_list|(
name|admin
operator|.
name|fullName
argument_list|)
expr_stmt|;
name|gApi
operator|.
name|groups
argument_list|()
operator|.
name|id
argument_list|(
name|name1
argument_list|)
operator|.
name|addGroups
argument_list|(
name|name2
argument_list|)
expr_stmt|;
name|this
operator|.
name|g1
operator|=
name|gApi
operator|.
name|groups
argument_list|()
operator|.
name|id
argument_list|(
name|name1
argument_list|)
operator|.
name|detail
argument_list|()
expr_stmt|;
name|this
operator|.
name|g2
operator|=
name|gApi
operator|.
name|groups
argument_list|()
operator|.
name|id
argument_list|(
name|name2
argument_list|)
operator|.
name|detail
argument_list|()
expr_stmt|;
name|this
operator|.
name|gAdmin
operator|=
name|gApi
operator|.
name|groups
argument_list|()
operator|.
name|id
argument_list|(
literal|"Administrators"
argument_list|)
operator|.
name|detail
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|allGood ()
specifier|public
name|void
name|allGood
parameter_list|()
throws|throws
name|Exception
block|{
name|assertThat
argument_list|(
name|check
argument_list|()
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|missingGroupNameRef ()
specifier|public
name|void
name|missingGroupNameRef
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|Repository
name|repo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|allUsers
argument_list|)
init|)
block|{
name|RefUpdate
name|ru
init|=
name|repo
operator|.
name|updateRef
argument_list|(
name|RefNames
operator|.
name|REFS_GROUPNAMES
argument_list|)
decl_stmt|;
name|ru
operator|.
name|setForceUpdate
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|RefUpdate
operator|.
name|Result
name|result
init|=
name|ru
operator|.
name|delete
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|result
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|Result
operator|.
name|FORCED
argument_list|)
expr_stmt|;
block|}
name|assertError
argument_list|(
literal|"refs/meta/group-names does not exist"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|missingGroupRef ()
specifier|public
name|void
name|missingGroupRef
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|Repository
name|repo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|allUsers
argument_list|)
init|)
block|{
name|RefUpdate
name|ru
init|=
name|repo
operator|.
name|updateRef
argument_list|(
name|RefNames
operator|.
name|refsGroups
argument_list|(
operator|new
name|AccountGroup
operator|.
name|UUID
argument_list|(
name|g1
operator|.
name|id
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|ru
operator|.
name|setForceUpdate
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|RefUpdate
operator|.
name|Result
name|result
init|=
name|ru
operator|.
name|delete
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|result
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|Result
operator|.
name|FORCED
argument_list|)
expr_stmt|;
block|}
name|assertError
argument_list|(
literal|"missing as group ref"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|parseGroupRef ()
specifier|public
name|void
name|parseGroupRef
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|Repository
name|repo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|allUsers
argument_list|)
init|)
block|{
name|RefRename
name|ru
init|=
name|repo
operator|.
name|renameRef
argument_list|(
name|RefNames
operator|.
name|refsGroups
argument_list|(
operator|new
name|AccountGroup
operator|.
name|UUID
argument_list|(
name|g1
operator|.
name|id
argument_list|)
argument_list|)
argument_list|,
name|RefNames
operator|.
name|REFS_GROUPS
operator|+
name|BOGUS_UUID
argument_list|)
decl_stmt|;
name|RefUpdate
operator|.
name|Result
name|result
init|=
name|ru
operator|.
name|rename
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|result
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|Result
operator|.
name|RENAMED
argument_list|)
expr_stmt|;
block|}
name|assertError
argument_list|(
literal|"null UUID from"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|missingNameEntry ()
specifier|public
name|void
name|missingNameEntry
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|Repository
name|repo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|allUsers
argument_list|)
init|)
block|{
name|RefRename
name|ru
init|=
name|repo
operator|.
name|renameRef
argument_list|(
name|RefNames
operator|.
name|refsGroups
argument_list|(
operator|new
name|AccountGroup
operator|.
name|UUID
argument_list|(
name|g1
operator|.
name|id
argument_list|)
argument_list|)
argument_list|,
name|RefNames
operator|.
name|refsGroups
argument_list|(
operator|new
name|AccountGroup
operator|.
name|UUID
argument_list|(
name|BOGUS_UUID
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|RefUpdate
operator|.
name|Result
name|result
init|=
name|ru
operator|.
name|rename
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|result
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|Result
operator|.
name|RENAMED
argument_list|)
expr_stmt|;
block|}
name|assertError
argument_list|(
literal|"group "
operator|+
name|BOGUS_UUID
operator|+
literal|" has no entry in name map"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|groupRefDoesNotParse ()
specifier|public
name|void
name|groupRefDoesNotParse
parameter_list|()
throws|throws
name|Exception
block|{
name|updateGroupFile
argument_list|(
name|RefNames
operator|.
name|refsGroups
argument_list|(
operator|new
name|AccountGroup
operator|.
name|UUID
argument_list|(
name|g1
operator|.
name|id
argument_list|)
argument_list|)
argument_list|,
name|GroupConfig
operator|.
name|GROUP_CONFIG_FILE
argument_list|,
literal|"[this is not valid\n"
argument_list|)
expr_stmt|;
name|assertError
argument_list|(
literal|"does not parse"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|nameRefDoesNotParse ()
specifier|public
name|void
name|nameRefDoesNotParse
parameter_list|()
throws|throws
name|Exception
block|{
name|updateGroupFile
argument_list|(
name|RefNames
operator|.
name|REFS_GROUPNAMES
argument_list|,
name|GroupNameNotes
operator|.
name|getNoteKey
argument_list|(
operator|new
name|AccountGroup
operator|.
name|NameKey
argument_list|(
name|g1
operator|.
name|name
argument_list|)
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|,
literal|"[this is not valid\n"
argument_list|)
expr_stmt|;
name|assertError
argument_list|(
literal|"does not parse"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|inconsistentName ()
specifier|public
name|void
name|inconsistentName
parameter_list|()
throws|throws
name|Exception
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
literal|"group"
argument_list|,
literal|null
argument_list|,
literal|"name"
argument_list|,
literal|"not really"
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|setString
argument_list|(
literal|"group"
argument_list|,
literal|null
argument_list|,
literal|"id"
argument_list|,
literal|"42"
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|setString
argument_list|(
literal|"group"
argument_list|,
literal|null
argument_list|,
literal|"ownerGroupUuid"
argument_list|,
name|gAdmin
operator|.
name|id
argument_list|)
expr_stmt|;
name|updateGroupFile
argument_list|(
name|RefNames
operator|.
name|refsGroups
argument_list|(
operator|new
name|AccountGroup
operator|.
name|UUID
argument_list|(
name|g1
operator|.
name|id
argument_list|)
argument_list|)
argument_list|,
name|GroupConfig
operator|.
name|GROUP_CONFIG_FILE
argument_list|,
name|cfg
operator|.
name|toText
argument_list|()
argument_list|)
expr_stmt|;
name|assertError
argument_list|(
literal|"inconsistent name"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|sharedGroupID ()
specifier|public
name|void
name|sharedGroupID
parameter_list|()
throws|throws
name|Exception
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
literal|"group"
argument_list|,
literal|null
argument_list|,
literal|"name"
argument_list|,
name|g1
operator|.
name|name
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|setInt
argument_list|(
literal|"group"
argument_list|,
literal|null
argument_list|,
literal|"id"
argument_list|,
name|g2
operator|.
name|groupId
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|setString
argument_list|(
literal|"group"
argument_list|,
literal|null
argument_list|,
literal|"ownerGroupUuid"
argument_list|,
name|gAdmin
operator|.
name|id
argument_list|)
expr_stmt|;
name|updateGroupFile
argument_list|(
name|RefNames
operator|.
name|refsGroups
argument_list|(
operator|new
name|AccountGroup
operator|.
name|UUID
argument_list|(
name|g1
operator|.
name|id
argument_list|)
argument_list|)
argument_list|,
name|GroupConfig
operator|.
name|GROUP_CONFIG_FILE
argument_list|,
name|cfg
operator|.
name|toText
argument_list|()
argument_list|)
expr_stmt|;
name|assertError
argument_list|(
literal|"shared group id"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|unknownOwnerGroup ()
specifier|public
name|void
name|unknownOwnerGroup
parameter_list|()
throws|throws
name|Exception
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
literal|"group"
argument_list|,
literal|null
argument_list|,
literal|"name"
argument_list|,
name|g1
operator|.
name|name
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|setInt
argument_list|(
literal|"group"
argument_list|,
literal|null
argument_list|,
literal|"id"
argument_list|,
name|g1
operator|.
name|groupId
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|setString
argument_list|(
literal|"group"
argument_list|,
literal|null
argument_list|,
literal|"ownerGroupUuid"
argument_list|,
name|BOGUS_UUID
argument_list|)
expr_stmt|;
name|updateGroupFile
argument_list|(
name|RefNames
operator|.
name|refsGroups
argument_list|(
operator|new
name|AccountGroup
operator|.
name|UUID
argument_list|(
name|g1
operator|.
name|id
argument_list|)
argument_list|)
argument_list|,
name|GroupConfig
operator|.
name|GROUP_CONFIG_FILE
argument_list|,
name|cfg
operator|.
name|toText
argument_list|()
argument_list|)
expr_stmt|;
name|assertError
argument_list|(
literal|"nonexistent owner group"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|nameWithoutGroupRef ()
specifier|public
name|void
name|nameWithoutGroupRef
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|bogusName
init|=
literal|"bogus name"
decl_stmt|;
name|Config
name|config
init|=
operator|new
name|Config
argument_list|()
decl_stmt|;
name|config
operator|.
name|setString
argument_list|(
literal|"group"
argument_list|,
literal|null
argument_list|,
literal|"uuid"
argument_list|,
name|BOGUS_UUID
argument_list|)
expr_stmt|;
name|config
operator|.
name|setString
argument_list|(
literal|"group"
argument_list|,
literal|null
argument_list|,
literal|"name"
argument_list|,
name|bogusName
argument_list|)
expr_stmt|;
name|updateGroupFile
argument_list|(
name|RefNames
operator|.
name|REFS_GROUPNAMES
argument_list|,
name|GroupNameNotes
operator|.
name|getNoteKey
argument_list|(
operator|new
name|AccountGroup
operator|.
name|NameKey
argument_list|(
name|bogusName
argument_list|)
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|,
name|config
operator|.
name|toText
argument_list|()
argument_list|)
expr_stmt|;
name|assertError
argument_list|(
literal|"entry missing as group ref"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|nonexistentMember ()
specifier|public
name|void
name|nonexistentMember
parameter_list|()
throws|throws
name|Exception
block|{
name|updateGroupFile
argument_list|(
name|RefNames
operator|.
name|refsGroups
argument_list|(
operator|new
name|AccountGroup
operator|.
name|UUID
argument_list|(
name|g1
operator|.
name|id
argument_list|)
argument_list|)
argument_list|,
literal|"members"
argument_list|,
literal|"314159265\n"
argument_list|)
expr_stmt|;
name|assertError
argument_list|(
literal|"nonexistent member 314159265"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|nonexistentSubgroup ()
specifier|public
name|void
name|nonexistentSubgroup
parameter_list|()
throws|throws
name|Exception
block|{
name|updateGroupFile
argument_list|(
name|RefNames
operator|.
name|refsGroups
argument_list|(
operator|new
name|AccountGroup
operator|.
name|UUID
argument_list|(
name|g1
operator|.
name|id
argument_list|)
argument_list|)
argument_list|,
literal|"subgroups"
argument_list|,
name|BOGUS_UUID
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|assertError
argument_list|(
literal|"has nonexistent subgroup"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|cyclicSubgroup ()
specifier|public
name|void
name|cyclicSubgroup
parameter_list|()
throws|throws
name|Exception
block|{
name|updateGroupFile
argument_list|(
name|RefNames
operator|.
name|refsGroups
argument_list|(
operator|new
name|AccountGroup
operator|.
name|UUID
argument_list|(
name|g1
operator|.
name|id
argument_list|)
argument_list|)
argument_list|,
literal|"subgroups"
argument_list|,
name|g1
operator|.
name|id
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|assertWarning
argument_list|(
literal|"cyclic"
argument_list|)
expr_stmt|;
block|}
DECL|method|assertError (String msg)
specifier|private
name|void
name|assertError
parameter_list|(
name|String
name|msg
parameter_list|)
throws|throws
name|Exception
block|{
name|assertConsistency
argument_list|(
name|msg
argument_list|,
name|ConsistencyProblemInfo
operator|.
name|Status
operator|.
name|ERROR
argument_list|)
expr_stmt|;
block|}
DECL|method|assertWarning (String msg)
specifier|private
name|void
name|assertWarning
parameter_list|(
name|String
name|msg
parameter_list|)
throws|throws
name|Exception
block|{
name|assertConsistency
argument_list|(
name|msg
argument_list|,
name|ConsistencyProblemInfo
operator|.
name|Status
operator|.
name|WARNING
argument_list|)
expr_stmt|;
block|}
DECL|method|check ()
specifier|private
name|List
argument_list|<
name|ConsistencyProblemInfo
argument_list|>
name|check
parameter_list|()
throws|throws
name|Exception
block|{
name|ConsistencyCheckInput
name|in
init|=
operator|new
name|ConsistencyCheckInput
argument_list|()
decl_stmt|;
name|in
operator|.
name|checkGroups
operator|=
operator|new
name|ConsistencyCheckInput
operator|.
name|CheckGroupsInput
argument_list|()
expr_stmt|;
name|ConsistencyCheckInfo
name|info
init|=
name|gApi
operator|.
name|config
argument_list|()
operator|.
name|server
argument_list|()
operator|.
name|checkConsistency
argument_list|(
name|in
argument_list|)
decl_stmt|;
return|return
name|info
operator|.
name|checkGroupsResult
operator|.
name|problems
return|;
block|}
DECL|method|assertConsistency (String msg, ConsistencyProblemInfo.Status want)
specifier|private
name|void
name|assertConsistency
parameter_list|(
name|String
name|msg
parameter_list|,
name|ConsistencyProblemInfo
operator|.
name|Status
name|want
parameter_list|)
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|ConsistencyProblemInfo
argument_list|>
name|problems
init|=
name|check
argument_list|()
decl_stmt|;
for|for
control|(
name|ConsistencyProblemInfo
name|i
range|:
name|problems
control|)
block|{
if|if
condition|(
operator|!
name|i
operator|.
name|status
operator|.
name|equals
argument_list|(
name|want
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|i
operator|.
name|message
operator|.
name|contains
argument_list|(
name|msg
argument_list|)
condition|)
block|{
return|return;
block|}
block|}
name|fail
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"could not find %s substring '%s' in %s"
argument_list|,
name|want
argument_list|,
name|msg
argument_list|,
name|problems
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|updateGroupFile (String refName, String fileName, String content)
specifier|private
name|void
name|updateGroupFile
parameter_list|(
name|String
name|refName
parameter_list|,
name|String
name|fileName
parameter_list|,
name|String
name|content
parameter_list|)
throws|throws
name|Exception
block|{
name|GroupTestUtil
operator|.
name|updateGroupFile
argument_list|(
name|repoManager
argument_list|,
name|allUsers
argument_list|,
name|serverIdent
operator|.
name|get
argument_list|()
argument_list|,
name|refName
argument_list|,
name|fileName
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

