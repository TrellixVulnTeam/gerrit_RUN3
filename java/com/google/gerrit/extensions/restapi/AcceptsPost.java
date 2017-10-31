begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2012 The Android Open Source Project
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
DECL|package|com.google.gerrit.extensions.restapi
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|extensions
operator|.
name|restapi
package|;
end_package

begin_comment
comment|/**  * Optional interface for {@link RestCollection}.  *  *<p>Collections that implement this interface can accept a {@code POST} directly on the collection  * itself when no id was given in the path. This interface is intended to be used with  * TopLevelResource collections. Nested collections often bind POST on the parent collection to the  * view implementation handling the insertion of a new member.  */
end_comment

begin_interface
DECL|interface|AcceptsPost
specifier|public
interface|interface
name|AcceptsPost
parameter_list|<
name|P
extends|extends
name|RestResource
parameter_list|>
block|{
comment|/**    * Handle creation of a child resource by POST on the collection.    *    * @param parent parent collection handle.    * @return a view to perform the creation. The id of the newly created resource should be    *     determined from the input body.    * @throws RestApiException the view cannot be constructed.    */
DECL|method|post (P parent)
name|RestModifyView
argument_list|<
name|P
argument_list|,
name|?
argument_list|>
name|post
parameter_list|(
name|P
name|parent
parameter_list|)
throws|throws
name|RestApiException
function_decl|;
block|}
end_interface

end_unit

