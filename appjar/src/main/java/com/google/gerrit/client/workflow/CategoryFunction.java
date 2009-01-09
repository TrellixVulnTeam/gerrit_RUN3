begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright 2008 Google Inc.
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
DECL|package|com.google.gerrit.client.workflow
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|workflow
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
name|data
operator|.
name|ApprovalType
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
name|reviewdb
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
name|client
operator|.
name|reviewdb
operator|.
name|ApprovalCategory
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
name|reviewdb
operator|.
name|ChangeApproval
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
name|reviewdb
operator|.
name|ProjectRight
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/** Function to control {@link ChangeApproval}s in an {@link ApprovalCategory}. */
end_comment

begin_class
DECL|class|CategoryFunction
specifier|public
specifier|abstract
class|class
name|CategoryFunction
block|{
DECL|field|all
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|CategoryFunction
argument_list|>
name|all
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|CategoryFunction
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
name|all
operator|.
name|put
argument_list|(
name|SubmitFunction
operator|.
name|NAME
argument_list|,
operator|new
name|SubmitFunction
argument_list|()
argument_list|)
expr_stmt|;
name|all
operator|.
name|put
argument_list|(
name|MaxWithBlock
operator|.
name|NAME
argument_list|,
operator|new
name|MaxWithBlock
argument_list|()
argument_list|)
expr_stmt|;
name|all
operator|.
name|put
argument_list|(
name|NoOpFunction
operator|.
name|NAME
argument_list|,
operator|new
name|NoOpFunction
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Locate a function by name.    *     * @param functionName the function's unique name.    * @return the function implementation; null if the function is not known to    *         Gerrit and thus cannot be executed.    */
DECL|method|forName (final String functionName)
specifier|public
specifier|static
name|CategoryFunction
name|forName
parameter_list|(
specifier|final
name|String
name|functionName
parameter_list|)
block|{
return|return
name|all
operator|.
name|get
argument_list|(
name|functionName
argument_list|)
return|;
block|}
comment|/**    * Normalize ChangeApprovals and set the valid flag for this category.    *<p>    * Implementors should invoke:    *     *<pre>    * state.valid(at, true);    *</pre>    *<p>    * If the set of approvals from<code>state.getApprovals(at)</code> covers the    * requirements for the function, indicating the category has been completed.    *<p>    * An example implementation which requires at least one positive and no    * negatives might be:    *     *<pre>    * boolean neg = false, pos = false;    * for (final ChangeApproval ca : state.getApprovals(at)) {    *   state.normalize(ca);    *   neg |= ca.getValue()&lt; 0;    *   pos |= ca.getValue()&gt; 0;    * }    * state.valid(at, !neg&amp;&amp; pos);    *</pre>    *     * @param at the cached category description to process.    * @param state state to read approvals and project rights from, and to update    *        the valid status into.    */
DECL|method|run (ApprovalType at, FunctionState state)
specifier|public
specifier|abstract
name|void
name|run
parameter_list|(
name|ApprovalType
name|at
parameter_list|,
name|FunctionState
name|state
parameter_list|)
function_decl|;
DECL|method|isValid (final Account.Id accountId, final ApprovalType at, final FunctionState state)
specifier|public
name|boolean
name|isValid
parameter_list|(
specifier|final
name|Account
operator|.
name|Id
name|accountId
parameter_list|,
specifier|final
name|ApprovalType
name|at
parameter_list|,
specifier|final
name|FunctionState
name|state
parameter_list|)
block|{
for|for
control|(
specifier|final
name|ProjectRight
name|pr
range|:
name|state
operator|.
name|getAllRights
argument_list|(
name|at
argument_list|)
control|)
block|{
if|if
condition|(
name|state
operator|.
name|isMember
argument_list|(
name|accountId
argument_list|,
name|pr
operator|.
name|getAccountGroupId
argument_list|()
argument_list|)
operator|&&
operator|(
name|pr
operator|.
name|getMinValue
argument_list|()
operator|<
literal|0
operator|||
name|pr
operator|.
name|getMaxValue
argument_list|()
operator|>
literal|0
operator|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

