begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2011 The Android Open Source Project
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/** Tracks group inclusions in memory for efficient access. */
end_comment

begin_interface
DECL|interface|GroupIncludeCache
specifier|public
interface|interface
name|GroupIncludeCache
block|{
comment|/** @return groups directly a member of the passed group. */
DECL|method|membersOf (AccountGroup.UUID group)
specifier|public
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|membersOf
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|group
parameter_list|)
function_decl|;
comment|/** @return any groups the passed group belongs to. */
DECL|method|memberIn (AccountGroup.UUID groupId)
specifier|public
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|memberIn
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|groupId
parameter_list|)
function_decl|;
DECL|method|evictMembersOf (AccountGroup.UUID groupId)
specifier|public
name|void
name|evictMembersOf
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|groupId
parameter_list|)
function_decl|;
DECL|method|evictMemberIn (AccountGroup.UUID groupId)
specifier|public
name|void
name|evictMemberIn
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|groupId
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

