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
name|NativeMap
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

begin_comment
comment|/** Groups available from {@code /groups/}. */
end_comment

begin_class
DECL|class|GroupMap
specifier|public
class|class
name|GroupMap
extends|extends
name|NativeMap
argument_list|<
name|GroupInfo
argument_list|>
block|{
DECL|method|all (AsyncCallback<GroupMap> callback)
specifier|public
specifier|static
name|void
name|all
parameter_list|(
name|AsyncCallback
argument_list|<
name|GroupMap
argument_list|>
name|callback
parameter_list|)
block|{
name|groups
argument_list|()
operator|.
name|get
argument_list|(
name|NativeMap
operator|.
name|copyKeysIntoChildren
argument_list|(
name|callback
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|match (String match, int limit, int start, AsyncCallback<GroupMap> cb)
specifier|public
specifier|static
name|void
name|match
parameter_list|(
name|String
name|match
parameter_list|,
name|int
name|limit
parameter_list|,
name|int
name|start
parameter_list|,
name|AsyncCallback
argument_list|<
name|GroupMap
argument_list|>
name|cb
parameter_list|)
block|{
name|RestApi
name|call
init|=
name|groups
argument_list|()
decl_stmt|;
if|if
condition|(
name|match
operator|!=
literal|null
condition|)
block|{
name|call
operator|.
name|addParameter
argument_list|(
literal|"m"
argument_list|,
name|match
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|limit
operator|>
literal|0
condition|)
block|{
name|call
operator|.
name|addParameter
argument_list|(
literal|"n"
argument_list|,
name|limit
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|start
operator|>
literal|0
condition|)
block|{
name|call
operator|.
name|addParameter
argument_list|(
literal|"S"
argument_list|,
name|start
argument_list|)
expr_stmt|;
block|}
name|call
operator|.
name|get
argument_list|(
name|NativeMap
operator|.
name|copyKeysIntoChildren
argument_list|(
name|cb
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|myOwned (AsyncCallback<GroupMap> cb)
specifier|public
specifier|static
name|void
name|myOwned
parameter_list|(
name|AsyncCallback
argument_list|<
name|GroupMap
argument_list|>
name|cb
parameter_list|)
block|{
name|myOwnedGroups
argument_list|()
operator|.
name|get
argument_list|(
name|NativeMap
operator|.
name|copyKeysIntoChildren
argument_list|(
name|cb
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|myOwned (String groupName, AsyncCallback<GroupMap> cb)
specifier|public
specifier|static
name|void
name|myOwned
parameter_list|(
name|String
name|groupName
parameter_list|,
name|AsyncCallback
argument_list|<
name|GroupMap
argument_list|>
name|cb
parameter_list|)
block|{
name|myOwnedGroups
argument_list|()
operator|.
name|addParameter
argument_list|(
literal|"q"
argument_list|,
name|groupName
argument_list|)
operator|.
name|get
argument_list|(
name|NativeMap
operator|.
name|copyKeysIntoChildren
argument_list|(
name|cb
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|myOwnedGroups ()
specifier|private
specifier|static
name|RestApi
name|myOwnedGroups
parameter_list|()
block|{
return|return
name|groups
argument_list|()
operator|.
name|addParameterTrue
argument_list|(
literal|"owned"
argument_list|)
return|;
block|}
DECL|method|groups ()
specifier|private
specifier|static
name|RestApi
name|groups
parameter_list|()
block|{
return|return
operator|new
name|RestApi
argument_list|(
literal|"groups"
argument_list|)
return|;
block|}
DECL|method|GroupMap ()
specifier|protected
name|GroupMap
parameter_list|()
block|{   }
block|}
end_class

end_unit

