begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2015 The Android Open Source Project
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
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|IdentifiedUser
import|;
end_import

begin_class
DECL|class|AbstractGroupBackend
specifier|public
specifier|abstract
class|class
name|AbstractGroupBackend
implements|implements
name|GroupBackend
block|{
annotation|@
name|Override
DECL|method|memberOfAny (IdentifiedUser user, Iterable<AccountGroup.UUID> ids)
specifier|public
name|boolean
name|memberOfAny
parameter_list|(
name|IdentifiedUser
name|user
parameter_list|,
name|Iterable
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|ids
parameter_list|)
block|{
return|return
name|membershipsOf
argument_list|(
name|user
argument_list|)
operator|.
name|containsAnyOf
argument_list|(
name|ids
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|isVisibleToAll (AccountGroup.UUID uuid)
specifier|public
name|boolean
name|isVisibleToAll
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|uuid
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

