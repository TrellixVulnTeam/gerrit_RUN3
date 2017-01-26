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
DECL|package|com.google.gerrit.server.group
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
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Joiner
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
name|audit
operator|.
name|GroupMemberAuditListener
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
name|TimeUtil
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
name|AccountGroupById
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
name|AccountGroupMember
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
name|gerrit
operator|.
name|server
operator|.
name|account
operator|.
name|AccountCache
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
name|gerrit
operator|.
name|server
operator|.
name|account
operator|.
name|UniversalGroupBackend
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
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|MessageFormat
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
name|Collection
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

begin_class
DECL|class|DbGroupMemberAuditListener
class|class
name|DbGroupMemberAuditListener
implements|implements
name|GroupMemberAuditListener
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DbGroupMemberAuditListener
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|schema
specifier|private
specifier|final
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|schema
decl_stmt|;
DECL|field|accountCache
specifier|private
specifier|final
name|AccountCache
name|accountCache
decl_stmt|;
DECL|field|groupCache
specifier|private
specifier|final
name|GroupCache
name|groupCache
decl_stmt|;
DECL|field|groupBackend
specifier|private
specifier|final
name|UniversalGroupBackend
name|groupBackend
decl_stmt|;
annotation|@
name|Inject
DECL|method|DbGroupMemberAuditListener (SchemaFactory<ReviewDb> schema, AccountCache accountCache, GroupCache groupCache, UniversalGroupBackend groupBackend)
name|DbGroupMemberAuditListener
parameter_list|(
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|schema
parameter_list|,
name|AccountCache
name|accountCache
parameter_list|,
name|GroupCache
name|groupCache
parameter_list|,
name|UniversalGroupBackend
name|groupBackend
parameter_list|)
block|{
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|this
operator|.
name|accountCache
operator|=
name|accountCache
expr_stmt|;
name|this
operator|.
name|groupCache
operator|=
name|groupCache
expr_stmt|;
name|this
operator|.
name|groupBackend
operator|=
name|groupBackend
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onAddAccountsToGroup (Account.Id me, Collection<AccountGroupMember> added)
specifier|public
name|void
name|onAddAccountsToGroup
parameter_list|(
name|Account
operator|.
name|Id
name|me
parameter_list|,
name|Collection
argument_list|<
name|AccountGroupMember
argument_list|>
name|added
parameter_list|)
block|{
name|List
argument_list|<
name|AccountGroupMemberAudit
argument_list|>
name|auditInserts
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|AccountGroupMember
name|m
range|:
name|added
control|)
block|{
name|AccountGroupMemberAudit
name|audit
init|=
operator|new
name|AccountGroupMemberAudit
argument_list|(
name|m
argument_list|,
name|me
argument_list|,
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
decl_stmt|;
name|auditInserts
operator|.
name|add
argument_list|(
name|audit
argument_list|)
expr_stmt|;
block|}
try|try
init|(
name|ReviewDb
name|db
init|=
name|schema
operator|.
name|open
argument_list|()
init|)
block|{
name|db
operator|.
name|accountGroupMembersAudit
argument_list|()
operator|.
name|insert
argument_list|(
name|auditInserts
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
name|logOrmExceptionForAccounts
argument_list|(
literal|"Cannot log add accounts to group event performed by user"
argument_list|,
name|me
argument_list|,
name|added
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|onDeleteAccountsFromGroup (Account.Id me, Collection<AccountGroupMember> removed)
specifier|public
name|void
name|onDeleteAccountsFromGroup
parameter_list|(
name|Account
operator|.
name|Id
name|me
parameter_list|,
name|Collection
argument_list|<
name|AccountGroupMember
argument_list|>
name|removed
parameter_list|)
block|{
name|List
argument_list|<
name|AccountGroupMemberAudit
argument_list|>
name|auditInserts
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|AccountGroupMemberAudit
argument_list|>
name|auditUpdates
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
try|try
init|(
name|ReviewDb
name|db
init|=
name|schema
operator|.
name|open
argument_list|()
init|)
block|{
for|for
control|(
name|AccountGroupMember
name|m
range|:
name|removed
control|)
block|{
name|AccountGroupMemberAudit
name|audit
init|=
literal|null
decl_stmt|;
for|for
control|(
name|AccountGroupMemberAudit
name|a
range|:
name|db
operator|.
name|accountGroupMembersAudit
argument_list|()
operator|.
name|byGroupAccount
argument_list|(
name|m
operator|.
name|getAccountGroupId
argument_list|()
argument_list|,
name|m
operator|.
name|getAccountId
argument_list|()
argument_list|)
control|)
block|{
if|if
condition|(
name|a
operator|.
name|isActive
argument_list|()
condition|)
block|{
name|audit
operator|=
name|a
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|audit
operator|!=
literal|null
condition|)
block|{
name|audit
operator|.
name|removed
argument_list|(
name|me
argument_list|,
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
expr_stmt|;
name|auditUpdates
operator|.
name|add
argument_list|(
name|audit
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|audit
operator|=
operator|new
name|AccountGroupMemberAudit
argument_list|(
name|m
argument_list|,
name|me
argument_list|,
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
expr_stmt|;
name|audit
operator|.
name|removedLegacy
argument_list|()
expr_stmt|;
name|auditInserts
operator|.
name|add
argument_list|(
name|audit
argument_list|)
expr_stmt|;
block|}
block|}
name|db
operator|.
name|accountGroupMembersAudit
argument_list|()
operator|.
name|update
argument_list|(
name|auditUpdates
argument_list|)
expr_stmt|;
name|db
operator|.
name|accountGroupMembersAudit
argument_list|()
operator|.
name|insert
argument_list|(
name|auditInserts
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
name|logOrmExceptionForAccounts
argument_list|(
literal|"Cannot log delete accounts from group event performed by user"
argument_list|,
name|me
argument_list|,
name|removed
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|onAddGroupsToGroup (Account.Id me, Collection<AccountGroupById> added)
specifier|public
name|void
name|onAddGroupsToGroup
parameter_list|(
name|Account
operator|.
name|Id
name|me
parameter_list|,
name|Collection
argument_list|<
name|AccountGroupById
argument_list|>
name|added
parameter_list|)
block|{
name|List
argument_list|<
name|AccountGroupByIdAud
argument_list|>
name|includesAudit
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|AccountGroupById
name|groupInclude
range|:
name|added
control|)
block|{
name|AccountGroupByIdAud
name|audit
init|=
operator|new
name|AccountGroupByIdAud
argument_list|(
name|groupInclude
argument_list|,
name|me
argument_list|,
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
decl_stmt|;
name|includesAudit
operator|.
name|add
argument_list|(
name|audit
argument_list|)
expr_stmt|;
block|}
try|try
init|(
name|ReviewDb
name|db
init|=
name|schema
operator|.
name|open
argument_list|()
init|)
block|{
name|db
operator|.
name|accountGroupByIdAud
argument_list|()
operator|.
name|insert
argument_list|(
name|includesAudit
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
name|logOrmExceptionForGroups
argument_list|(
literal|"Cannot log add groups to group event performed by user"
argument_list|,
name|me
argument_list|,
name|added
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|onDeleteGroupsFromGroup (Account.Id me, Collection<AccountGroupById> removed)
specifier|public
name|void
name|onDeleteGroupsFromGroup
parameter_list|(
name|Account
operator|.
name|Id
name|me
parameter_list|,
name|Collection
argument_list|<
name|AccountGroupById
argument_list|>
name|removed
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|AccountGroupByIdAud
argument_list|>
name|auditUpdates
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
try|try
init|(
name|ReviewDb
name|db
init|=
name|schema
operator|.
name|open
argument_list|()
init|)
block|{
for|for
control|(
specifier|final
name|AccountGroupById
name|g
range|:
name|removed
control|)
block|{
name|AccountGroupByIdAud
name|audit
init|=
literal|null
decl_stmt|;
for|for
control|(
name|AccountGroupByIdAud
name|a
range|:
name|db
operator|.
name|accountGroupByIdAud
argument_list|()
operator|.
name|byGroupInclude
argument_list|(
name|g
operator|.
name|getGroupId
argument_list|()
argument_list|,
name|g
operator|.
name|getIncludeUUID
argument_list|()
argument_list|)
control|)
block|{
if|if
condition|(
name|a
operator|.
name|isActive
argument_list|()
condition|)
block|{
name|audit
operator|=
name|a
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|audit
operator|!=
literal|null
condition|)
block|{
name|audit
operator|.
name|removed
argument_list|(
name|me
argument_list|,
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
expr_stmt|;
name|auditUpdates
operator|.
name|add
argument_list|(
name|audit
argument_list|)
expr_stmt|;
block|}
block|}
name|db
operator|.
name|accountGroupByIdAud
argument_list|()
operator|.
name|update
argument_list|(
name|auditUpdates
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
name|logOrmExceptionForGroups
argument_list|(
literal|"Cannot log delete groups from group event performed by user"
argument_list|,
name|me
argument_list|,
name|removed
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|logOrmExceptionForAccounts (String header, Account.Id me, Collection<AccountGroupMember> values, OrmException e)
specifier|private
name|void
name|logOrmExceptionForAccounts
parameter_list|(
name|String
name|header
parameter_list|,
name|Account
operator|.
name|Id
name|me
parameter_list|,
name|Collection
argument_list|<
name|AccountGroupMember
argument_list|>
name|values
parameter_list|,
name|OrmException
name|e
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|descriptions
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|AccountGroupMember
name|m
range|:
name|values
control|)
block|{
name|Account
operator|.
name|Id
name|accountId
init|=
name|m
operator|.
name|getAccountId
argument_list|()
decl_stmt|;
name|String
name|userName
init|=
name|accountCache
operator|.
name|get
argument_list|(
name|accountId
argument_list|)
operator|.
name|getUserName
argument_list|()
decl_stmt|;
name|AccountGroup
operator|.
name|Id
name|groupId
init|=
name|m
operator|.
name|getAccountGroupId
argument_list|()
decl_stmt|;
name|String
name|groupName
init|=
name|groupCache
operator|.
name|get
argument_list|(
name|groupId
argument_list|)
operator|.
name|getName
argument_list|()
decl_stmt|;
name|descriptions
operator|.
name|add
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"account {0}/{1}, group {2}/{3}"
argument_list|,
name|accountId
argument_list|,
name|userName
argument_list|,
name|groupId
argument_list|,
name|groupName
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|logOrmException
argument_list|(
name|header
argument_list|,
name|me
argument_list|,
name|descriptions
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
DECL|method|logOrmExceptionForGroups (String header, Account.Id me, Collection<AccountGroupById> values, OrmException e)
specifier|private
name|void
name|logOrmExceptionForGroups
parameter_list|(
name|String
name|header
parameter_list|,
name|Account
operator|.
name|Id
name|me
parameter_list|,
name|Collection
argument_list|<
name|AccountGroupById
argument_list|>
name|values
parameter_list|,
name|OrmException
name|e
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|descriptions
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|AccountGroupById
name|m
range|:
name|values
control|)
block|{
name|AccountGroup
operator|.
name|UUID
name|groupUuid
init|=
name|m
operator|.
name|getIncludeUUID
argument_list|()
decl_stmt|;
name|String
name|groupName
init|=
name|groupBackend
operator|.
name|get
argument_list|(
name|groupUuid
argument_list|)
operator|.
name|getName
argument_list|()
decl_stmt|;
name|AccountGroup
operator|.
name|Id
name|targetGroupId
init|=
name|m
operator|.
name|getGroupId
argument_list|()
decl_stmt|;
name|String
name|targetGroupName
init|=
name|groupCache
operator|.
name|get
argument_list|(
name|targetGroupId
argument_list|)
operator|.
name|getName
argument_list|()
decl_stmt|;
name|descriptions
operator|.
name|add
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"group {0}/{1}, group {2}/{3}"
argument_list|,
name|groupUuid
argument_list|,
name|groupName
argument_list|,
name|targetGroupId
argument_list|,
name|targetGroupName
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|logOrmException
argument_list|(
name|header
argument_list|,
name|me
argument_list|,
name|descriptions
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
DECL|method|logOrmException (String header, Account.Id me, Iterable<?> values, OrmException e)
specifier|private
name|void
name|logOrmException
parameter_list|(
name|String
name|header
parameter_list|,
name|Account
operator|.
name|Id
name|me
parameter_list|,
name|Iterable
argument_list|<
name|?
argument_list|>
name|values
parameter_list|,
name|OrmException
name|e
parameter_list|)
block|{
name|StringBuilder
name|message
init|=
operator|new
name|StringBuilder
argument_list|(
name|header
argument_list|)
decl_stmt|;
name|message
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|message
operator|.
name|append
argument_list|(
name|me
argument_list|)
expr_stmt|;
name|message
operator|.
name|append
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|message
operator|.
name|append
argument_list|(
name|accountCache
operator|.
name|get
argument_list|(
name|me
argument_list|)
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
name|message
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
expr_stmt|;
name|message
operator|.
name|append
argument_list|(
name|Joiner
operator|.
name|on
argument_list|(
literal|"; "
argument_list|)
operator|.
name|join
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
name|log
operator|.
name|error
argument_list|(
name|message
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

