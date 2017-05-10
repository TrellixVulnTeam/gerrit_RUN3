begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2008 The Android Open Source Project
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
DECL|package|com.google.gerrit.reviewdb.server
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|reviewdb
operator|.
name|server
package|;
end_package

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
name|Change
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
name|SystemConfig
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
name|Relation
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
name|Schema
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
name|Sequence
import|;
end_import

begin_comment
comment|/**  * The review service database schema.  *  *<p>Root entities that are at the top level of some important data graph:  *  *<ul>  *<li>{@link Account}: Per-user account registration, preferences, identity.  *<li>{@link Change}: All review information about a single proposed change.  *<li>{@link SystemConfig}: Server-wide settings, managed by administrator.  *</ul>  */
end_comment

begin_interface
DECL|interface|ReviewDb
specifier|public
interface|interface
name|ReviewDb
extends|extends
name|Schema
block|{
comment|/* If you change anything, update SchemaVersion.C to use a new version. */
annotation|@
name|Relation
argument_list|(
name|id
operator|=
literal|1
argument_list|)
DECL|method|schemaVersion ()
name|SchemaVersionAccess
name|schemaVersion
parameter_list|()
function_decl|;
annotation|@
name|Relation
argument_list|(
name|id
operator|=
literal|2
argument_list|)
DECL|method|systemConfig ()
name|SystemConfigAccess
name|systemConfig
parameter_list|()
function_decl|;
comment|// Deleted @Relation(id = 3)
comment|// Deleted @Relation(id = 4)
annotation|@
name|Relation
argument_list|(
name|id
operator|=
literal|6
argument_list|)
DECL|method|accounts ()
name|AccountAccess
name|accounts
parameter_list|()
function_decl|;
comment|// Deleted @Relation(id = 7)
comment|// Deleted @Relation(id = 8)
annotation|@
name|Relation
argument_list|(
name|id
operator|=
literal|10
argument_list|)
DECL|method|accountGroups ()
name|AccountGroupAccess
name|accountGroups
parameter_list|()
function_decl|;
annotation|@
name|Relation
argument_list|(
name|id
operator|=
literal|11
argument_list|)
DECL|method|accountGroupNames ()
name|AccountGroupNameAccess
name|accountGroupNames
parameter_list|()
function_decl|;
annotation|@
name|Relation
argument_list|(
name|id
operator|=
literal|12
argument_list|)
DECL|method|accountGroupMembers ()
name|AccountGroupMemberAccess
name|accountGroupMembers
parameter_list|()
function_decl|;
annotation|@
name|Relation
argument_list|(
name|id
operator|=
literal|13
argument_list|)
DECL|method|accountGroupMembersAudit ()
name|AccountGroupMemberAuditAccess
name|accountGroupMembersAudit
parameter_list|()
function_decl|;
comment|// Deleted @Relation(id = 17)
comment|// Deleted @Relation(id = 18)
comment|// Deleted @Relation(id = 19)
comment|// Deleted @Relation(id = 20)
annotation|@
name|Relation
argument_list|(
name|id
operator|=
literal|21
argument_list|)
DECL|method|changes ()
name|ChangeAccess
name|changes
parameter_list|()
function_decl|;
annotation|@
name|Relation
argument_list|(
name|id
operator|=
literal|22
argument_list|)
DECL|method|patchSetApprovals ()
name|PatchSetApprovalAccess
name|patchSetApprovals
parameter_list|()
function_decl|;
annotation|@
name|Relation
argument_list|(
name|id
operator|=
literal|23
argument_list|)
DECL|method|changeMessages ()
name|ChangeMessageAccess
name|changeMessages
parameter_list|()
function_decl|;
annotation|@
name|Relation
argument_list|(
name|id
operator|=
literal|24
argument_list|)
DECL|method|patchSets ()
name|PatchSetAccess
name|patchSets
parameter_list|()
function_decl|;
comment|// Deleted @Relation(id = 25)
annotation|@
name|Relation
argument_list|(
name|id
operator|=
literal|26
argument_list|)
DECL|method|patchComments ()
name|PatchLineCommentAccess
name|patchComments
parameter_list|()
function_decl|;
comment|// Deleted @Relation(id = 28)
annotation|@
name|Relation
argument_list|(
name|id
operator|=
literal|29
argument_list|)
DECL|method|accountGroupById ()
name|AccountGroupByIdAccess
name|accountGroupById
parameter_list|()
function_decl|;
annotation|@
name|Relation
argument_list|(
name|id
operator|=
literal|30
argument_list|)
DECL|method|accountGroupByIdAud ()
name|AccountGroupByIdAudAccess
name|accountGroupByIdAud
parameter_list|()
function_decl|;
comment|/** Create the next unique id for an {@link Account}. */
annotation|@
name|Sequence
argument_list|(
name|startWith
operator|=
literal|1000000
argument_list|)
DECL|method|nextAccountId ()
name|int
name|nextAccountId
parameter_list|()
throws|throws
name|OrmException
function_decl|;
comment|/** Next unique id for a {@link AccountGroup}. */
annotation|@
name|Sequence
DECL|method|nextAccountGroupId ()
name|int
name|nextAccountGroupId
parameter_list|()
throws|throws
name|OrmException
function_decl|;
DECL|field|FIRST_CHANGE_ID
name|int
name|FIRST_CHANGE_ID
init|=
literal|1
decl_stmt|;
comment|/**    * Next unique id for a {@link Change}.    *    * @deprecated use {@link com.google.gerrit.server.Sequences#nextChangeId()}.    */
annotation|@
name|Sequence
argument_list|(
name|startWith
operator|=
name|FIRST_CHANGE_ID
argument_list|)
annotation|@
name|Deprecated
DECL|method|nextChangeId ()
name|int
name|nextChangeId
parameter_list|()
throws|throws
name|OrmException
function_decl|;
DECL|method|changesTablesEnabled ()
specifier|default
name|boolean
name|changesTablesEnabled
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_interface

end_unit

