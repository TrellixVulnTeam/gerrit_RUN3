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
DECL|package|com.google.gerrit.server.group.db
package|package
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
name|Sets
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
name|Account
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
name|AccountGroupByIdAud
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
name|AccountGroupMemberAudit
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
name|GroupUUID
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
name|InternalGroup
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Timestamp
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
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|PersonIdent
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
comment|/** Unit tests for {@link AuditLogReader}. */
end_comment

begin_class
DECL|class|AuditLogReaderTest
specifier|public
specifier|final
class|class
name|AuditLogReaderTest
extends|extends
name|AbstractGroupTest
block|{
DECL|field|auditLogReader
specifier|private
name|AuditLogReader
name|auditLogReader
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
name|auditLogReader
operator|=
operator|new
name|AuditLogReader
argument_list|(
name|SERVER_ID
argument_list|,
name|allUsersName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|createGroupAsUserIdent ()
specifier|public
name|void
name|createGroupAsUserIdent
parameter_list|()
throws|throws
name|Exception
block|{
name|InternalGroup
name|group
init|=
name|createGroupAsUser
argument_list|(
literal|1
argument_list|,
literal|"test-group"
argument_list|)
decl_stmt|;
name|AccountGroup
operator|.
name|UUID
name|uuid
init|=
name|group
operator|.
name|getGroupUUID
argument_list|()
decl_stmt|;
name|AccountGroupMemberAudit
name|expAudit
init|=
name|createExpMemberAudit
argument_list|(
name|group
operator|.
name|getId
argument_list|()
argument_list|,
name|userId
argument_list|,
name|userId
argument_list|,
name|getTipTimestamp
argument_list|(
name|uuid
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|auditLogReader
operator|.
name|getMembersAudit
argument_list|(
name|allUsersRepo
argument_list|,
name|uuid
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|expAudit
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|createGroupAsServerIdent ()
specifier|public
name|void
name|createGroupAsServerIdent
parameter_list|()
throws|throws
name|Exception
block|{
name|InternalGroup
name|group
init|=
name|createGroup
argument_list|(
literal|1
argument_list|,
literal|"test-group"
argument_list|,
name|serverIdent
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|auditLogReader
operator|.
name|getMembersAudit
argument_list|(
name|allUsersRepo
argument_list|,
name|group
operator|.
name|getGroupUUID
argument_list|()
argument_list|)
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|addAndRemoveMember ()
specifier|public
name|void
name|addAndRemoveMember
parameter_list|()
throws|throws
name|Exception
block|{
name|InternalGroup
name|group
init|=
name|createGroupAsUser
argument_list|(
literal|1
argument_list|,
literal|"test-group"
argument_list|)
decl_stmt|;
name|AccountGroup
operator|.
name|UUID
name|uuid
init|=
name|group
operator|.
name|getGroupUUID
argument_list|()
decl_stmt|;
name|AccountGroupMemberAudit
name|expAudit1
init|=
name|createExpMemberAudit
argument_list|(
name|group
operator|.
name|getId
argument_list|()
argument_list|,
name|userId
argument_list|,
name|userId
argument_list|,
name|getTipTimestamp
argument_list|(
name|uuid
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|auditLogReader
operator|.
name|getMembersAudit
argument_list|(
name|allUsersRepo
argument_list|,
name|uuid
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|expAudit1
argument_list|)
expr_stmt|;
comment|// User adds account 100002 to the group.
name|Account
operator|.
name|Id
name|id
init|=
name|Account
operator|.
name|id
argument_list|(
literal|100002
argument_list|)
decl_stmt|;
name|addMembers
argument_list|(
name|uuid
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|AccountGroupMemberAudit
name|expAudit2
init|=
name|createExpMemberAudit
argument_list|(
name|group
operator|.
name|getId
argument_list|()
argument_list|,
name|id
argument_list|,
name|userId
argument_list|,
name|getTipTimestamp
argument_list|(
name|uuid
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|auditLogReader
operator|.
name|getMembersAudit
argument_list|(
name|allUsersRepo
argument_list|,
name|uuid
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|expAudit1
argument_list|,
name|expAudit2
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
comment|// User removes account 100002 from the group.
name|removeMembers
argument_list|(
name|uuid
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|expAudit2
operator|=
name|expAudit2
operator|.
name|toBuilder
argument_list|()
operator|.
name|removed
argument_list|(
name|userId
argument_list|,
name|getTipTimestamp
argument_list|(
name|uuid
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|auditLogReader
operator|.
name|getMembersAudit
argument_list|(
name|allUsersRepo
argument_list|,
name|uuid
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|expAudit1
argument_list|,
name|expAudit2
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|addMultiMembers ()
specifier|public
name|void
name|addMultiMembers
parameter_list|()
throws|throws
name|Exception
block|{
name|InternalGroup
name|group
init|=
name|createGroupAsUser
argument_list|(
literal|1
argument_list|,
literal|"test-group"
argument_list|)
decl_stmt|;
name|AccountGroup
operator|.
name|Id
name|groupId
init|=
name|group
operator|.
name|getId
argument_list|()
decl_stmt|;
name|AccountGroup
operator|.
name|UUID
name|uuid
init|=
name|group
operator|.
name|getGroupUUID
argument_list|()
decl_stmt|;
name|AccountGroupMemberAudit
name|expAudit1
init|=
name|createExpMemberAudit
argument_list|(
name|groupId
argument_list|,
name|userId
argument_list|,
name|userId
argument_list|,
name|getTipTimestamp
argument_list|(
name|uuid
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|auditLogReader
operator|.
name|getMembersAudit
argument_list|(
name|allUsersRepo
argument_list|,
name|uuid
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|expAudit1
argument_list|)
expr_stmt|;
name|Account
operator|.
name|Id
name|id1
init|=
name|Account
operator|.
name|id
argument_list|(
literal|100002
argument_list|)
decl_stmt|;
name|Account
operator|.
name|Id
name|id2
init|=
name|Account
operator|.
name|id
argument_list|(
literal|100003
argument_list|)
decl_stmt|;
name|addMembers
argument_list|(
name|uuid
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|id1
argument_list|,
name|id2
argument_list|)
argument_list|)
expr_stmt|;
name|AccountGroupMemberAudit
name|expAudit2
init|=
name|createExpMemberAudit
argument_list|(
name|groupId
argument_list|,
name|id1
argument_list|,
name|userId
argument_list|,
name|getTipTimestamp
argument_list|(
name|uuid
argument_list|)
argument_list|)
decl_stmt|;
name|AccountGroupMemberAudit
name|expAudit3
init|=
name|createExpMemberAudit
argument_list|(
name|groupId
argument_list|,
name|id2
argument_list|,
name|userId
argument_list|,
name|getTipTimestamp
argument_list|(
name|uuid
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|auditLogReader
operator|.
name|getMembersAudit
argument_list|(
name|allUsersRepo
argument_list|,
name|uuid
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|expAudit1
argument_list|,
name|expAudit2
argument_list|,
name|expAudit3
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|addAndRemoveSubgroups ()
specifier|public
name|void
name|addAndRemoveSubgroups
parameter_list|()
throws|throws
name|Exception
block|{
name|InternalGroup
name|group
init|=
name|createGroupAsUser
argument_list|(
literal|1
argument_list|,
literal|"test-group"
argument_list|)
decl_stmt|;
name|AccountGroup
operator|.
name|UUID
name|uuid
init|=
name|group
operator|.
name|getGroupUUID
argument_list|()
decl_stmt|;
name|InternalGroup
name|subgroup
init|=
name|createGroupAsUser
argument_list|(
literal|2
argument_list|,
literal|"test-group-2"
argument_list|)
decl_stmt|;
name|AccountGroup
operator|.
name|UUID
name|subgroupUuid
init|=
name|subgroup
operator|.
name|getGroupUUID
argument_list|()
decl_stmt|;
name|addSubgroups
argument_list|(
name|uuid
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|subgroupUuid
argument_list|)
argument_list|)
expr_stmt|;
name|AccountGroupByIdAud
name|expAudit
init|=
name|createExpGroupAudit
argument_list|(
name|group
operator|.
name|getId
argument_list|()
argument_list|,
name|subgroupUuid
argument_list|,
name|userId
argument_list|,
name|getTipTimestamp
argument_list|(
name|uuid
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|auditLogReader
operator|.
name|getSubgroupsAudit
argument_list|(
name|allUsersRepo
argument_list|,
name|uuid
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|expAudit
argument_list|)
expr_stmt|;
name|removeSubgroups
argument_list|(
name|uuid
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|subgroupUuid
argument_list|)
argument_list|)
expr_stmt|;
name|expAudit
operator|=
name|expAudit
operator|.
name|toBuilder
argument_list|()
operator|.
name|removed
argument_list|(
name|userId
argument_list|,
name|getTipTimestamp
argument_list|(
name|uuid
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|auditLogReader
operator|.
name|getSubgroupsAudit
argument_list|(
name|allUsersRepo
argument_list|,
name|uuid
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|expAudit
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|addMultiSubgroups ()
specifier|public
name|void
name|addMultiSubgroups
parameter_list|()
throws|throws
name|Exception
block|{
name|InternalGroup
name|group
init|=
name|createGroupAsUser
argument_list|(
literal|1
argument_list|,
literal|"test-group"
argument_list|)
decl_stmt|;
name|AccountGroup
operator|.
name|UUID
name|uuid
init|=
name|group
operator|.
name|getGroupUUID
argument_list|()
decl_stmt|;
name|InternalGroup
name|subgroup1
init|=
name|createGroupAsUser
argument_list|(
literal|2
argument_list|,
literal|"test-group-2"
argument_list|)
decl_stmt|;
name|InternalGroup
name|subgroup2
init|=
name|createGroupAsUser
argument_list|(
literal|3
argument_list|,
literal|"test-group-3"
argument_list|)
decl_stmt|;
name|AccountGroup
operator|.
name|UUID
name|subgroupUuid1
init|=
name|subgroup1
operator|.
name|getGroupUUID
argument_list|()
decl_stmt|;
name|AccountGroup
operator|.
name|UUID
name|subgroupUuid2
init|=
name|subgroup2
operator|.
name|getGroupUUID
argument_list|()
decl_stmt|;
name|addSubgroups
argument_list|(
name|uuid
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|subgroupUuid1
argument_list|,
name|subgroupUuid2
argument_list|)
argument_list|)
expr_stmt|;
name|AccountGroupByIdAud
name|expAudit1
init|=
name|createExpGroupAudit
argument_list|(
name|group
operator|.
name|getId
argument_list|()
argument_list|,
name|subgroupUuid1
argument_list|,
name|userId
argument_list|,
name|getTipTimestamp
argument_list|(
name|uuid
argument_list|)
argument_list|)
decl_stmt|;
name|AccountGroupByIdAud
name|expAudit2
init|=
name|createExpGroupAudit
argument_list|(
name|group
operator|.
name|getId
argument_list|()
argument_list|,
name|subgroupUuid2
argument_list|,
name|userId
argument_list|,
name|getTipTimestamp
argument_list|(
name|uuid
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|auditLogReader
operator|.
name|getSubgroupsAudit
argument_list|(
name|allUsersRepo
argument_list|,
name|uuid
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|expAudit1
argument_list|,
name|expAudit2
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|addAndRemoveMembersAndSubgroups ()
specifier|public
name|void
name|addAndRemoveMembersAndSubgroups
parameter_list|()
throws|throws
name|Exception
block|{
name|InternalGroup
name|group
init|=
name|createGroupAsUser
argument_list|(
literal|1
argument_list|,
literal|"test-group"
argument_list|)
decl_stmt|;
name|AccountGroup
operator|.
name|Id
name|groupId
init|=
name|group
operator|.
name|getId
argument_list|()
decl_stmt|;
name|AccountGroup
operator|.
name|UUID
name|uuid
init|=
name|group
operator|.
name|getGroupUUID
argument_list|()
decl_stmt|;
name|AccountGroupMemberAudit
name|expMemberAudit
init|=
name|createExpMemberAudit
argument_list|(
name|groupId
argument_list|,
name|userId
argument_list|,
name|userId
argument_list|,
name|getTipTimestamp
argument_list|(
name|uuid
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|auditLogReader
operator|.
name|getMembersAudit
argument_list|(
name|allUsersRepo
argument_list|,
name|uuid
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|expMemberAudit
argument_list|)
expr_stmt|;
name|Account
operator|.
name|Id
name|id1
init|=
name|Account
operator|.
name|id
argument_list|(
literal|100002
argument_list|)
decl_stmt|;
name|Account
operator|.
name|Id
name|id2
init|=
name|Account
operator|.
name|id
argument_list|(
literal|100003
argument_list|)
decl_stmt|;
name|Account
operator|.
name|Id
name|id3
init|=
name|Account
operator|.
name|id
argument_list|(
literal|100004
argument_list|)
decl_stmt|;
name|InternalGroup
name|subgroup1
init|=
name|createGroupAsUser
argument_list|(
literal|2
argument_list|,
literal|"test-group-2"
argument_list|)
decl_stmt|;
name|InternalGroup
name|subgroup2
init|=
name|createGroupAsUser
argument_list|(
literal|3
argument_list|,
literal|"test-group-3"
argument_list|)
decl_stmt|;
name|InternalGroup
name|subgroup3
init|=
name|createGroupAsUser
argument_list|(
literal|4
argument_list|,
literal|"test-group-4"
argument_list|)
decl_stmt|;
name|AccountGroup
operator|.
name|UUID
name|subgroupUuid1
init|=
name|subgroup1
operator|.
name|getGroupUUID
argument_list|()
decl_stmt|;
name|AccountGroup
operator|.
name|UUID
name|subgroupUuid2
init|=
name|subgroup2
operator|.
name|getGroupUUID
argument_list|()
decl_stmt|;
name|AccountGroup
operator|.
name|UUID
name|subgroupUuid3
init|=
name|subgroup3
operator|.
name|getGroupUUID
argument_list|()
decl_stmt|;
comment|// Add two accounts.
name|addMembers
argument_list|(
name|uuid
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|id1
argument_list|,
name|id2
argument_list|)
argument_list|)
expr_stmt|;
name|AccountGroupMemberAudit
name|expMemberAudit1
init|=
name|createExpMemberAudit
argument_list|(
name|groupId
argument_list|,
name|id1
argument_list|,
name|userId
argument_list|,
name|getTipTimestamp
argument_list|(
name|uuid
argument_list|)
argument_list|)
decl_stmt|;
name|AccountGroupMemberAudit
name|expMemberAudit2
init|=
name|createExpMemberAudit
argument_list|(
name|groupId
argument_list|,
name|id2
argument_list|,
name|userId
argument_list|,
name|getTipTimestamp
argument_list|(
name|uuid
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|auditLogReader
operator|.
name|getMembersAudit
argument_list|(
name|allUsersRepo
argument_list|,
name|uuid
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|expMemberAudit
argument_list|,
name|expMemberAudit1
argument_list|,
name|expMemberAudit2
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
comment|// Add one subgroup.
name|addSubgroups
argument_list|(
name|uuid
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|subgroupUuid1
argument_list|)
argument_list|)
expr_stmt|;
name|AccountGroupByIdAud
name|expGroupAudit1
init|=
name|createExpGroupAudit
argument_list|(
name|group
operator|.
name|getId
argument_list|()
argument_list|,
name|subgroupUuid1
argument_list|,
name|userId
argument_list|,
name|getTipTimestamp
argument_list|(
name|uuid
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|auditLogReader
operator|.
name|getSubgroupsAudit
argument_list|(
name|allUsersRepo
argument_list|,
name|uuid
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|expGroupAudit1
argument_list|)
expr_stmt|;
comment|// Remove one account.
name|removeMembers
argument_list|(
name|uuid
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|id2
argument_list|)
argument_list|)
expr_stmt|;
name|expMemberAudit2
operator|=
name|expMemberAudit2
operator|.
name|toBuilder
argument_list|()
operator|.
name|removed
argument_list|(
name|userId
argument_list|,
name|getTipTimestamp
argument_list|(
name|uuid
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|auditLogReader
operator|.
name|getMembersAudit
argument_list|(
name|allUsersRepo
argument_list|,
name|uuid
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|expMemberAudit
argument_list|,
name|expMemberAudit1
argument_list|,
name|expMemberAudit2
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
comment|// Add two subgroups.
name|addSubgroups
argument_list|(
name|uuid
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|subgroupUuid2
argument_list|,
name|subgroupUuid3
argument_list|)
argument_list|)
expr_stmt|;
name|AccountGroupByIdAud
name|expGroupAudit2
init|=
name|createExpGroupAudit
argument_list|(
name|group
operator|.
name|getId
argument_list|()
argument_list|,
name|subgroupUuid2
argument_list|,
name|userId
argument_list|,
name|getTipTimestamp
argument_list|(
name|uuid
argument_list|)
argument_list|)
decl_stmt|;
name|AccountGroupByIdAud
name|expGroupAudit3
init|=
name|createExpGroupAudit
argument_list|(
name|group
operator|.
name|getId
argument_list|()
argument_list|,
name|subgroupUuid3
argument_list|,
name|userId
argument_list|,
name|getTipTimestamp
argument_list|(
name|uuid
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|auditLogReader
operator|.
name|getSubgroupsAudit
argument_list|(
name|allUsersRepo
argument_list|,
name|uuid
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|expGroupAudit1
argument_list|,
name|expGroupAudit2
argument_list|,
name|expGroupAudit3
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
comment|// Add two account, including a removed account.
name|addMembers
argument_list|(
name|uuid
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|id2
argument_list|,
name|id3
argument_list|)
argument_list|)
expr_stmt|;
name|AccountGroupMemberAudit
name|expMemberAudit4
init|=
name|createExpMemberAudit
argument_list|(
name|groupId
argument_list|,
name|id2
argument_list|,
name|userId
argument_list|,
name|getTipTimestamp
argument_list|(
name|uuid
argument_list|)
argument_list|)
decl_stmt|;
name|AccountGroupMemberAudit
name|expMemberAudit3
init|=
name|createExpMemberAudit
argument_list|(
name|groupId
argument_list|,
name|id3
argument_list|,
name|userId
argument_list|,
name|getTipTimestamp
argument_list|(
name|uuid
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|auditLogReader
operator|.
name|getMembersAudit
argument_list|(
name|allUsersRepo
argument_list|,
name|uuid
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|expMemberAudit
argument_list|,
name|expMemberAudit1
argument_list|,
name|expMemberAudit2
argument_list|,
name|expMemberAudit4
argument_list|,
name|expMemberAudit3
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
comment|// Remove two subgroups.
name|removeSubgroups
argument_list|(
name|uuid
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|subgroupUuid1
argument_list|,
name|subgroupUuid3
argument_list|)
argument_list|)
expr_stmt|;
name|expGroupAudit1
operator|=
name|expGroupAudit1
operator|.
name|toBuilder
argument_list|()
operator|.
name|removed
argument_list|(
name|userId
argument_list|,
name|getTipTimestamp
argument_list|(
name|uuid
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|expGroupAudit3
operator|=
name|expGroupAudit3
operator|.
name|toBuilder
argument_list|()
operator|.
name|removed
argument_list|(
name|userId
argument_list|,
name|getTipTimestamp
argument_list|(
name|uuid
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|auditLogReader
operator|.
name|getSubgroupsAudit
argument_list|(
name|allUsersRepo
argument_list|,
name|uuid
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|expGroupAudit1
argument_list|,
name|expGroupAudit2
argument_list|,
name|expGroupAudit3
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
comment|// Add back one removed subgroup.
name|addSubgroups
argument_list|(
name|uuid
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|subgroupUuid1
argument_list|)
argument_list|)
expr_stmt|;
name|AccountGroupByIdAud
name|expGroupAudit4
init|=
name|createExpGroupAudit
argument_list|(
name|group
operator|.
name|getId
argument_list|()
argument_list|,
name|subgroupUuid1
argument_list|,
name|userId
argument_list|,
name|getTipTimestamp
argument_list|(
name|uuid
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|auditLogReader
operator|.
name|getSubgroupsAudit
argument_list|(
name|allUsersRepo
argument_list|,
name|uuid
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|expGroupAudit1
argument_list|,
name|expGroupAudit2
argument_list|,
name|expGroupAudit3
argument_list|,
name|expGroupAudit4
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
block|}
DECL|method|createGroupAsUser (int next, String groupName)
specifier|private
name|InternalGroup
name|createGroupAsUser
parameter_list|(
name|int
name|next
parameter_list|,
name|String
name|groupName
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|createGroup
argument_list|(
name|next
argument_list|,
name|groupName
argument_list|,
name|userIdent
argument_list|,
name|userId
argument_list|)
return|;
block|}
DECL|method|createGroup ( int next, String groupName, PersonIdent authorIdent, Account.Id authorId)
specifier|private
name|InternalGroup
name|createGroup
parameter_list|(
name|int
name|next
parameter_list|,
name|String
name|groupName
parameter_list|,
name|PersonIdent
name|authorIdent
parameter_list|,
name|Account
operator|.
name|Id
name|authorId
parameter_list|)
throws|throws
name|Exception
block|{
name|InternalGroupCreation
name|groupCreation
init|=
name|InternalGroupCreation
operator|.
name|builder
argument_list|()
operator|.
name|setGroupUUID
argument_list|(
name|GroupUUID
operator|.
name|make
argument_list|(
name|groupName
argument_list|,
name|serverIdent
argument_list|)
argument_list|)
operator|.
name|setNameKey
argument_list|(
name|AccountGroup
operator|.
name|nameKey
argument_list|(
name|groupName
argument_list|)
argument_list|)
operator|.
name|setId
argument_list|(
name|AccountGroup
operator|.
name|id
argument_list|(
name|next
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|InternalGroupUpdate
name|groupUpdate
init|=
name|authorIdent
operator|.
name|equals
argument_list|(
name|serverIdent
argument_list|)
condition|?
name|InternalGroupUpdate
operator|.
name|builder
argument_list|()
operator|.
name|setDescription
argument_list|(
literal|"Groups"
argument_list|)
operator|.
name|build
argument_list|()
else|:
name|InternalGroupUpdate
operator|.
name|builder
argument_list|()
operator|.
name|setDescription
argument_list|(
literal|"Groups"
argument_list|)
operator|.
name|setMemberModification
argument_list|(
name|members
lambda|->
name|ImmutableSet
operator|.
name|of
argument_list|(
name|authorId
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|GroupConfig
name|groupConfig
init|=
name|GroupConfig
operator|.
name|createForNewGroup
argument_list|(
name|allUsersName
argument_list|,
name|allUsersRepo
argument_list|,
name|groupCreation
argument_list|)
decl_stmt|;
name|groupConfig
operator|.
name|setGroupUpdate
argument_list|(
name|groupUpdate
argument_list|,
name|getAuditLogFormatter
argument_list|()
argument_list|)
expr_stmt|;
name|groupConfig
operator|.
name|commit
argument_list|(
name|createMetaDataUpdate
argument_list|(
name|authorIdent
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|groupConfig
operator|.
name|getLoadedGroup
argument_list|()
operator|.
name|orElseThrow
argument_list|(
parameter_list|()
lambda|->
operator|new
name|IllegalStateException
argument_list|(
literal|"create group failed"
argument_list|)
argument_list|)
return|;
block|}
DECL|method|updateGroup (AccountGroup.UUID uuid, InternalGroupUpdate groupUpdate)
specifier|private
name|void
name|updateGroup
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|uuid
parameter_list|,
name|InternalGroupUpdate
name|groupUpdate
parameter_list|)
throws|throws
name|Exception
block|{
name|GroupConfig
name|groupConfig
init|=
name|GroupConfig
operator|.
name|loadForGroup
argument_list|(
name|allUsersName
argument_list|,
name|allUsersRepo
argument_list|,
name|uuid
argument_list|)
decl_stmt|;
name|groupConfig
operator|.
name|setGroupUpdate
argument_list|(
name|groupUpdate
argument_list|,
name|getAuditLogFormatter
argument_list|()
argument_list|)
expr_stmt|;
name|groupConfig
operator|.
name|commit
argument_list|(
name|createMetaDataUpdate
argument_list|(
name|userIdent
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|addMembers (AccountGroup.UUID groupUuid, Set<Account.Id> ids)
specifier|private
name|void
name|addMembers
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|groupUuid
parameter_list|,
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|ids
parameter_list|)
throws|throws
name|Exception
block|{
name|InternalGroupUpdate
name|update
init|=
name|InternalGroupUpdate
operator|.
name|builder
argument_list|()
operator|.
name|setMemberModification
argument_list|(
name|memberIds
lambda|->
name|Sets
operator|.
name|union
argument_list|(
name|memberIds
argument_list|,
name|ids
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|updateGroup
argument_list|(
name|groupUuid
argument_list|,
name|update
argument_list|)
expr_stmt|;
block|}
DECL|method|removeMembers (AccountGroup.UUID groupUuid, Set<Account.Id> ids)
specifier|private
name|void
name|removeMembers
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|groupUuid
parameter_list|,
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|ids
parameter_list|)
throws|throws
name|Exception
block|{
name|InternalGroupUpdate
name|update
init|=
name|InternalGroupUpdate
operator|.
name|builder
argument_list|()
operator|.
name|setMemberModification
argument_list|(
name|memberIds
lambda|->
name|Sets
operator|.
name|difference
argument_list|(
name|memberIds
argument_list|,
name|ids
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|updateGroup
argument_list|(
name|groupUuid
argument_list|,
name|update
argument_list|)
expr_stmt|;
block|}
DECL|method|addSubgroups (AccountGroup.UUID groupUuid, Set<AccountGroup.UUID> uuids)
specifier|private
name|void
name|addSubgroups
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|groupUuid
parameter_list|,
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|uuids
parameter_list|)
throws|throws
name|Exception
block|{
name|InternalGroupUpdate
name|update
init|=
name|InternalGroupUpdate
operator|.
name|builder
argument_list|()
operator|.
name|setSubgroupModification
argument_list|(
name|memberIds
lambda|->
name|Sets
operator|.
name|union
argument_list|(
name|memberIds
argument_list|,
name|uuids
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|updateGroup
argument_list|(
name|groupUuid
argument_list|,
name|update
argument_list|)
expr_stmt|;
block|}
DECL|method|removeSubgroups (AccountGroup.UUID groupUuid, Set<AccountGroup.UUID> uuids)
specifier|private
name|void
name|removeSubgroups
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|groupUuid
parameter_list|,
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|uuids
parameter_list|)
throws|throws
name|Exception
block|{
name|InternalGroupUpdate
name|update
init|=
name|InternalGroupUpdate
operator|.
name|builder
argument_list|()
operator|.
name|setSubgroupModification
argument_list|(
name|memberIds
lambda|->
name|Sets
operator|.
name|difference
argument_list|(
name|memberIds
argument_list|,
name|uuids
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|updateGroup
argument_list|(
name|groupUuid
argument_list|,
name|update
argument_list|)
expr_stmt|;
block|}
DECL|method|createExpMemberAudit ( AccountGroup.Id groupId, Account.Id id, Account.Id addedBy, Timestamp addedOn)
specifier|private
specifier|static
name|AccountGroupMemberAudit
name|createExpMemberAudit
parameter_list|(
name|AccountGroup
operator|.
name|Id
name|groupId
parameter_list|,
name|Account
operator|.
name|Id
name|id
parameter_list|,
name|Account
operator|.
name|Id
name|addedBy
parameter_list|,
name|Timestamp
name|addedOn
parameter_list|)
block|{
return|return
name|AccountGroupMemberAudit
operator|.
name|builder
argument_list|()
operator|.
name|key
argument_list|(
name|AccountGroupMemberAudit
operator|.
name|key
argument_list|(
name|id
argument_list|,
name|groupId
argument_list|,
name|addedOn
argument_list|)
argument_list|)
operator|.
name|addedBy
argument_list|(
name|addedBy
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|createExpGroupAudit ( AccountGroup.Id groupId, AccountGroup.UUID uuid, Account.Id addedBy, Timestamp addedOn)
specifier|private
specifier|static
name|AccountGroupByIdAud
name|createExpGroupAudit
parameter_list|(
name|AccountGroup
operator|.
name|Id
name|groupId
parameter_list|,
name|AccountGroup
operator|.
name|UUID
name|uuid
parameter_list|,
name|Account
operator|.
name|Id
name|addedBy
parameter_list|,
name|Timestamp
name|addedOn
parameter_list|)
block|{
return|return
name|AccountGroupByIdAud
operator|.
name|builder
argument_list|()
operator|.
name|key
argument_list|(
name|AccountGroupByIdAud
operator|.
name|key
argument_list|(
name|groupId
argument_list|,
name|uuid
argument_list|,
name|addedOn
argument_list|)
argument_list|)
operator|.
name|addedBy
argument_list|(
name|addedBy
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

