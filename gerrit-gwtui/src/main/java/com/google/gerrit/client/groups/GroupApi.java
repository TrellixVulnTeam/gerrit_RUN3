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
DECL|package|com.google.gerrit.client.groups
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|groups
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
name|client
operator|.
name|rpc
operator|.
name|NativeList
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
name|client
operator|.
name|rpc
operator|.
name|RestApi
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
name|gwt
operator|.
name|core
operator|.
name|client
operator|.
name|JavaScriptObject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|http
operator|.
name|client
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|user
operator|.
name|client
operator|.
name|rpc
operator|.
name|AsyncCallback
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
comment|/**  * A collection of static methods which work on the Gerrit REST API for specific  * groups.  */
end_comment

begin_class
DECL|class|GroupApi
specifier|public
class|class
name|GroupApi
block|{
comment|/** Add member to a group. */
DECL|method|addMember (AccountGroup.UUID groupUUID, String member, AsyncCallback<MemberInfo> cb)
specifier|public
specifier|static
name|void
name|addMember
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|groupUUID
parameter_list|,
name|String
name|member
parameter_list|,
name|AsyncCallback
argument_list|<
name|MemberInfo
argument_list|>
name|cb
parameter_list|)
block|{
operator|new
name|RestApi
argument_list|(
name|membersBase
argument_list|(
name|groupUUID
argument_list|)
operator|+
literal|"/"
operator|+
name|member
argument_list|)
operator|.
name|put
argument_list|(
name|cb
argument_list|)
expr_stmt|;
block|}
comment|/** Add members to a group. */
DECL|method|addMembers (AccountGroup.UUID groupUUID, Set<String> members, AsyncCallback<NativeList<MemberInfo>> cb)
specifier|public
specifier|static
name|void
name|addMembers
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|groupUUID
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|members
parameter_list|,
name|AsyncCallback
argument_list|<
name|NativeList
argument_list|<
name|MemberInfo
argument_list|>
argument_list|>
name|cb
parameter_list|)
block|{
name|RestApi
name|call
init|=
operator|new
name|RestApi
argument_list|(
name|membersBase
argument_list|(
name|groupUUID
argument_list|)
argument_list|)
decl_stmt|;
name|MemberInput
name|input
init|=
name|MemberInput
operator|.
name|create
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|member
range|:
name|members
control|)
block|{
name|input
operator|.
name|add_member
argument_list|(
name|member
argument_list|)
expr_stmt|;
block|}
name|call
operator|.
name|data
argument_list|(
name|input
argument_list|)
operator|.
name|put
argument_list|(
name|cb
argument_list|)
expr_stmt|;
block|}
DECL|method|membersBase (AccountGroup.UUID groupUUID)
specifier|private
specifier|static
name|String
name|membersBase
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|groupUUID
parameter_list|)
block|{
return|return
name|base
argument_list|(
name|groupUUID
argument_list|)
operator|+
literal|"members"
return|;
block|}
DECL|method|base (AccountGroup.UUID groupUUID)
specifier|private
specifier|static
name|String
name|base
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|groupUUID
parameter_list|)
block|{
name|String
name|id
init|=
name|URL
operator|.
name|encodePathSegment
argument_list|(
name|groupUUID
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
return|return
literal|"/groups/"
operator|+
name|id
operator|+
literal|"/"
return|;
block|}
DECL|class|MemberInput
specifier|private
specifier|static
class|class
name|MemberInput
extends|extends
name|JavaScriptObject
block|{
DECL|method|init ()
specifier|final
specifier|native
name|void
name|init
parameter_list|()
comment|/*-{ this.members = []; }-*/
function_decl|;
DECL|method|add_member (String n)
specifier|final
specifier|native
name|void
name|add_member
parameter_list|(
name|String
name|n
parameter_list|)
comment|/*-{ this.members.push(n); }-*/
function_decl|;
DECL|method|create ()
specifier|static
name|MemberInput
name|create
parameter_list|()
block|{
name|MemberInput
name|m
init|=
operator|(
name|MemberInput
operator|)
name|createObject
argument_list|()
decl_stmt|;
name|m
operator|.
name|init
argument_list|()
expr_stmt|;
return|return
name|m
return|;
block|}
DECL|method|MemberInput ()
specifier|protected
name|MemberInput
parameter_list|()
block|{     }
block|}
block|}
end_class

end_unit

