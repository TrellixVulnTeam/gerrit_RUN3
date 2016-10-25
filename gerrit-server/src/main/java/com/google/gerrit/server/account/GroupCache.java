begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2009 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.account
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|account
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
name|gerrit
operator|.
name|common
operator|.
name|Nullable
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

begin_comment
comment|/** Tracks group objects in memory for efficient access. */
end_comment

begin_interface
DECL|interface|GroupCache
specifier|public
interface|interface
name|GroupCache
block|{
DECL|method|get (AccountGroup.Id groupId)
name|AccountGroup
name|get
parameter_list|(
name|AccountGroup
operator|.
name|Id
name|groupId
parameter_list|)
function_decl|;
DECL|method|get (AccountGroup.NameKey name)
name|AccountGroup
name|get
parameter_list|(
name|AccountGroup
operator|.
name|NameKey
name|name
parameter_list|)
function_decl|;
comment|/**    * Lookup a group definition by its UUID. The returned definition may be null    * if the group has been deleted and the UUID reference is stale, or was    * copied from another server.    */
annotation|@
name|Nullable
DECL|method|get (AccountGroup.UUID uuid)
name|AccountGroup
name|get
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|uuid
parameter_list|)
function_decl|;
comment|/** @return sorted list of groups. */
DECL|method|all ()
name|ImmutableList
argument_list|<
name|AccountGroup
argument_list|>
name|all
parameter_list|()
function_decl|;
comment|/** Notify the cache that a new group was constructed. */
DECL|method|onCreateGroup (AccountGroup.NameKey newGroupName)
name|void
name|onCreateGroup
parameter_list|(
name|AccountGroup
operator|.
name|NameKey
name|newGroupName
parameter_list|)
function_decl|;
DECL|method|evict (AccountGroup group)
name|void
name|evict
parameter_list|(
name|AccountGroup
name|group
parameter_list|)
function_decl|;
DECL|method|evictAfterRename (final AccountGroup.NameKey oldName, final AccountGroup.NameKey newName)
name|void
name|evictAfterRename
parameter_list|(
specifier|final
name|AccountGroup
operator|.
name|NameKey
name|oldName
parameter_list|,
specifier|final
name|AccountGroup
operator|.
name|NameKey
name|newName
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

