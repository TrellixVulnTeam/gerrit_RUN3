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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
operator|.
name|toImmutableSet
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
name|reviewdb
operator|.
name|server
operator|.
name|ReviewDbUtil
operator|.
name|unwrapDb
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
name|common
operator|.
name|collect
operator|.
name|ImmutableList
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
name|AccountState
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
name|gerrit
operator|.
name|server
operator|.
name|audit
operator|.
name|group
operator|.
name|GroupAuditEvent
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
name|audit
operator|.
name|group
operator|.
name|GroupAuditListener
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
name|audit
operator|.
name|group
operator|.
name|GroupMemberAuditEvent
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
name|audit
operator|.
name|group
operator|.
name|GroupSubgroupAuditEvent
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
name|ResultSet
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
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

begin_class
DECL|class|DbGroupMemberAuditListener
class|class
name|DbGroupMemberAuditListener
implements|implements
name|GroupAuditListener
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
DECL|method|DbGroupMemberAuditListener ( SchemaFactory<ReviewDb> schema, AccountCache accountCache, GroupCache groupCache, UniversalGroupBackend groupBackend)
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
DECL|method|onAddMembers (GroupMemberAuditEvent event)
specifier|public
name|void
name|onAddMembers
parameter_list|(
name|GroupMemberAuditEvent
name|event
parameter_list|)
block|{
name|Optional
argument_list|<
name|InternalGroup
argument_list|>
name|updatedGroup
init|=
name|groupCache
operator|.
name|get
argument_list|(
name|event
operator|.
name|getUpdatedGroup
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|updatedGroup
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|logFailToLoadUpdatedGroup
argument_list|(
name|event
argument_list|)
expr_stmt|;
return|return;
block|}
name|InternalGroup
name|group
init|=
name|updatedGroup
operator|.
name|get
argument_list|()
decl_stmt|;
try|try
init|(
name|ReviewDb
name|db
init|=
name|unwrapDb
argument_list|(
name|schema
operator|.
name|open
argument_list|()
argument_list|)
init|)
block|{
name|db
operator|.
name|accountGroupMembersAudit
argument_list|()
operator|.
name|insert
argument_list|(
name|toAccountGroupMemberAudits
argument_list|(
name|event
argument_list|,
name|group
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
name|logOrmException
argument_list|(
literal|"Cannot log add accounts to group event performed by user"
argument_list|,
name|event
argument_list|,
name|group
operator|.
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|onDeleteMembers (GroupMemberAuditEvent event)
specifier|public
name|void
name|onDeleteMembers
parameter_list|(
name|GroupMemberAuditEvent
name|event
parameter_list|)
block|{
name|Optional
argument_list|<
name|InternalGroup
argument_list|>
name|updatedGroup
init|=
name|groupCache
operator|.
name|get
argument_list|(
name|event
operator|.
name|getUpdatedGroup
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|updatedGroup
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|logFailToLoadUpdatedGroup
argument_list|(
name|event
argument_list|)
expr_stmt|;
return|return;
block|}
name|InternalGroup
name|group
init|=
name|updatedGroup
operator|.
name|get
argument_list|()
decl_stmt|;
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
name|unwrapDb
argument_list|(
name|schema
operator|.
name|open
argument_list|()
argument_list|)
init|)
block|{
for|for
control|(
name|Account
operator|.
name|Id
name|accountId
range|:
name|event
operator|.
name|getModifiedMembers
argument_list|()
control|)
block|{
name|AccountGroupMemberAudit
name|audit
init|=
literal|null
decl_stmt|;
name|ResultSet
argument_list|<
name|AccountGroupMemberAudit
argument_list|>
name|audits
init|=
name|db
operator|.
name|accountGroupMembersAudit
argument_list|()
operator|.
name|byGroupAccount
argument_list|(
name|group
operator|.
name|getId
argument_list|()
argument_list|,
name|accountId
argument_list|)
decl_stmt|;
for|for
control|(
name|AccountGroupMemberAudit
name|a
range|:
name|audits
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
name|event
operator|.
name|getActor
argument_list|()
argument_list|,
name|event
operator|.
name|getTimestamp
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
continue|continue;
block|}
name|AccountGroupMember
operator|.
name|Key
name|key
init|=
operator|new
name|AccountGroupMember
operator|.
name|Key
argument_list|(
name|accountId
argument_list|,
name|group
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|audit
operator|=
operator|new
name|AccountGroupMemberAudit
argument_list|(
operator|new
name|AccountGroupMember
argument_list|(
name|key
argument_list|)
argument_list|,
name|event
operator|.
name|getActor
argument_list|()
argument_list|,
name|event
operator|.
name|getTimestamp
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
name|logOrmException
argument_list|(
literal|"Cannot log delete accounts from group event performed by user"
argument_list|,
name|event
argument_list|,
name|group
operator|.
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|onAddSubgroups (GroupSubgroupAuditEvent event)
specifier|public
name|void
name|onAddSubgroups
parameter_list|(
name|GroupSubgroupAuditEvent
name|event
parameter_list|)
block|{
name|Optional
argument_list|<
name|InternalGroup
argument_list|>
name|updatedGroup
init|=
name|groupCache
operator|.
name|get
argument_list|(
name|event
operator|.
name|getUpdatedGroup
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|updatedGroup
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|logFailToLoadUpdatedGroup
argument_list|(
name|event
argument_list|)
expr_stmt|;
return|return;
block|}
name|InternalGroup
name|group
init|=
name|updatedGroup
operator|.
name|get
argument_list|()
decl_stmt|;
try|try
init|(
name|ReviewDb
name|db
init|=
name|unwrapDb
argument_list|(
name|schema
operator|.
name|open
argument_list|()
argument_list|)
init|)
block|{
name|db
operator|.
name|accountGroupByIdAud
argument_list|()
operator|.
name|insert
argument_list|(
name|toAccountGroupByIdAudits
argument_list|(
name|event
argument_list|,
name|group
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
name|logOrmException
argument_list|(
literal|"Cannot log add groups to group event performed by user"
argument_list|,
name|event
argument_list|,
name|group
operator|.
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|onDeleteSubgroups (GroupSubgroupAuditEvent event)
specifier|public
name|void
name|onDeleteSubgroups
parameter_list|(
name|GroupSubgroupAuditEvent
name|event
parameter_list|)
block|{
name|Optional
argument_list|<
name|InternalGroup
argument_list|>
name|updatedGroup
init|=
name|groupCache
operator|.
name|get
argument_list|(
name|event
operator|.
name|getUpdatedGroup
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|updatedGroup
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|logFailToLoadUpdatedGroup
argument_list|(
name|event
argument_list|)
expr_stmt|;
return|return;
block|}
name|InternalGroup
name|group
init|=
name|updatedGroup
operator|.
name|get
argument_list|()
decl_stmt|;
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
name|unwrapDb
argument_list|(
name|schema
operator|.
name|open
argument_list|()
argument_list|)
init|)
block|{
for|for
control|(
name|AccountGroup
operator|.
name|UUID
name|uuid
range|:
name|event
operator|.
name|getModifiedSubgroups
argument_list|()
control|)
block|{
name|AccountGroupByIdAud
name|audit
init|=
literal|null
decl_stmt|;
name|ResultSet
argument_list|<
name|AccountGroupByIdAud
argument_list|>
name|audits
init|=
name|db
operator|.
name|accountGroupByIdAud
argument_list|()
operator|.
name|byGroupInclude
argument_list|(
name|updatedGroup
operator|.
name|get
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|uuid
argument_list|)
decl_stmt|;
for|for
control|(
name|AccountGroupByIdAud
name|a
range|:
name|audits
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
name|event
operator|.
name|getActor
argument_list|()
argument_list|,
name|event
operator|.
name|getTimestamp
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
name|logOrmException
argument_list|(
literal|"Cannot log delete groups from group event performed by user"
argument_list|,
name|event
argument_list|,
name|group
operator|.
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|logFailToLoadUpdatedGroup (GroupAuditEvent event)
specifier|private
name|void
name|logFailToLoadUpdatedGroup
parameter_list|(
name|GroupAuditEvent
name|event
parameter_list|)
block|{
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|descriptions
init|=
name|createEventDescriptions
argument_list|(
name|event
argument_list|,
literal|"(fail to load group)"
argument_list|)
decl_stmt|;
name|String
name|message
init|=
name|createErrorMessage
argument_list|(
literal|"Fail to load the updated group"
argument_list|,
name|event
operator|.
name|getActor
argument_list|()
argument_list|,
name|descriptions
argument_list|)
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
DECL|method|logOrmException ( String header, GroupAuditEvent event, String updatedGroupName, OrmException e)
specifier|private
name|void
name|logOrmException
parameter_list|(
name|String
name|header
parameter_list|,
name|GroupAuditEvent
name|event
parameter_list|,
name|String
name|updatedGroupName
parameter_list|,
name|OrmException
name|e
parameter_list|)
block|{
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|descriptions
init|=
name|createEventDescriptions
argument_list|(
name|event
argument_list|,
name|updatedGroupName
argument_list|)
decl_stmt|;
name|String
name|message
init|=
name|createErrorMessage
argument_list|(
name|header
argument_list|,
name|event
operator|.
name|getActor
argument_list|()
argument_list|,
name|descriptions
argument_list|)
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|message
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
DECL|method|createEventDescriptions ( GroupAuditEvent event, String updatedGroupName)
specifier|private
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|createEventDescriptions
parameter_list|(
name|GroupAuditEvent
name|event
parameter_list|,
name|String
name|updatedGroupName
parameter_list|)
block|{
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|String
argument_list|>
name|builder
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
if|if
condition|(
name|event
operator|instanceof
name|GroupMemberAuditEvent
condition|)
block|{
name|GroupMemberAuditEvent
name|memberAuditEvent
init|=
operator|(
name|GroupMemberAuditEvent
operator|)
name|event
decl_stmt|;
for|for
control|(
name|Account
operator|.
name|Id
name|accountId
range|:
name|memberAuditEvent
operator|.
name|getModifiedMembers
argument_list|()
control|)
block|{
name|String
name|userName
init|=
name|getUserName
argument_list|(
name|accountId
argument_list|)
operator|.
name|orElse
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|builder
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
name|event
operator|.
name|getUpdatedGroup
argument_list|()
argument_list|,
name|updatedGroupName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|event
operator|instanceof
name|GroupSubgroupAuditEvent
condition|)
block|{
name|GroupSubgroupAuditEvent
name|subgroupAuditEvent
init|=
operator|(
name|GroupSubgroupAuditEvent
operator|)
name|event
decl_stmt|;
for|for
control|(
name|AccountGroup
operator|.
name|UUID
name|groupUuid
range|:
name|subgroupAuditEvent
operator|.
name|getModifiedSubgroups
argument_list|()
control|)
block|{
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
name|builder
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
name|subgroupAuditEvent
operator|.
name|getUpdatedGroup
argument_list|()
argument_list|,
name|updatedGroupName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|createErrorMessage ( String header, Account.Id me, ImmutableList<String> descriptions)
specifier|private
name|String
name|createErrorMessage
parameter_list|(
name|String
name|header
parameter_list|,
name|Account
operator|.
name|Id
name|me
parameter_list|,
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|descriptions
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
name|getUserName
argument_list|(
name|me
argument_list|)
operator|.
name|orElse
argument_list|(
literal|null
argument_list|)
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
name|descriptions
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|message
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getUserName (Account.Id accountId)
specifier|private
name|Optional
argument_list|<
name|String
argument_list|>
name|getUserName
parameter_list|(
name|Account
operator|.
name|Id
name|accountId
parameter_list|)
block|{
return|return
name|accountCache
operator|.
name|get
argument_list|(
name|accountId
argument_list|)
operator|.
name|map
argument_list|(
name|AccountState
operator|::
name|getUserName
argument_list|)
operator|.
name|orElse
argument_list|(
name|Optional
operator|.
name|empty
argument_list|()
argument_list|)
return|;
block|}
DECL|method|toAccountGroupMemberAudits ( GroupMemberAuditEvent event, AccountGroup.Id updatedGroupId)
specifier|private
specifier|static
name|ImmutableSet
argument_list|<
name|AccountGroupMemberAudit
argument_list|>
name|toAccountGroupMemberAudits
parameter_list|(
name|GroupMemberAuditEvent
name|event
parameter_list|,
name|AccountGroup
operator|.
name|Id
name|updatedGroupId
parameter_list|)
block|{
name|Timestamp
name|timestamp
init|=
name|event
operator|.
name|getTimestamp
argument_list|()
decl_stmt|;
name|Account
operator|.
name|Id
name|actor
init|=
name|event
operator|.
name|getActor
argument_list|()
decl_stmt|;
return|return
name|event
operator|.
name|getModifiedMembers
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|member
lambda|->
operator|new
name|AccountGroupMemberAudit
argument_list|(
operator|new
name|AccountGroupMemberAudit
operator|.
name|Key
argument_list|(
name|member
argument_list|,
name|updatedGroupId
argument_list|,
name|timestamp
argument_list|)
argument_list|,
name|actor
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|toImmutableSet
argument_list|()
argument_list|)
return|;
block|}
DECL|method|toAccountGroupByIdAudits ( GroupSubgroupAuditEvent event, AccountGroup.Id updatedGroupId)
specifier|private
specifier|static
name|ImmutableSet
argument_list|<
name|AccountGroupByIdAud
argument_list|>
name|toAccountGroupByIdAudits
parameter_list|(
name|GroupSubgroupAuditEvent
name|event
parameter_list|,
name|AccountGroup
operator|.
name|Id
name|updatedGroupId
parameter_list|)
block|{
name|Timestamp
name|timestamp
init|=
name|event
operator|.
name|getTimestamp
argument_list|()
decl_stmt|;
name|Account
operator|.
name|Id
name|actor
init|=
name|event
operator|.
name|getActor
argument_list|()
decl_stmt|;
return|return
name|event
operator|.
name|getModifiedSubgroups
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|subgroup
lambda|->
operator|new
name|AccountGroupByIdAud
argument_list|(
operator|new
name|AccountGroupByIdAud
operator|.
name|Key
argument_list|(
name|updatedGroupId
argument_list|,
name|subgroup
argument_list|,
name|timestamp
argument_list|)
argument_list|,
name|actor
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|toImmutableSet
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

