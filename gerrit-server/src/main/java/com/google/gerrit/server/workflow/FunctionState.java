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
DECL|package|com.google.gerrit.server.workflow
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
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
name|common
operator|.
name|data
operator|.
name|LabelType
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
name|data
operator|.
name|LabelTypes
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
name|data
operator|.
name|LabelValue
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
name|data
operator|.
name|Permission
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
name|data
operator|.
name|PermissionRange
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
name|PatchSet
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
name|PatchSetApproval
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
name|CurrentUser
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
name|project
operator|.
name|ChangeControl
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|assistedinject
operator|.
name|Assisted
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
name|Collections
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
name|List
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
comment|/** State passed through to a {@link CategoryFunction}. */
end_comment

begin_class
DECL|class|FunctionState
specifier|public
class|class
name|FunctionState
block|{
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (ChangeControl c, PatchSet.Id psId, Collection<PatchSetApproval> all)
name|FunctionState
name|create
parameter_list|(
name|ChangeControl
name|c
parameter_list|,
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|,
name|Collection
argument_list|<
name|PatchSetApproval
argument_list|>
name|all
parameter_list|)
function_decl|;
block|}
DECL|field|labelTypes
specifier|private
specifier|final
name|LabelTypes
name|labelTypes
decl_stmt|;
DECL|field|userFactory
specifier|private
specifier|final
name|IdentifiedUser
operator|.
name|GenericFactory
name|userFactory
decl_stmt|;
DECL|field|approvals
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|PatchSetApproval
argument_list|>
argument_list|>
name|approvals
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|PatchSetApproval
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|valid
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|valid
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|callerChangeControl
specifier|private
specifier|final
name|ChangeControl
name|callerChangeControl
decl_stmt|;
DECL|field|change
specifier|private
specifier|final
name|Change
name|change
decl_stmt|;
annotation|@
name|Inject
DECL|method|FunctionState (final LabelTypes labelTypes, final IdentifiedUser.GenericFactory userFactory, @Assisted final ChangeControl c, @Assisted final PatchSet.Id psId, @Assisted final Collection<PatchSetApproval> all)
name|FunctionState
parameter_list|(
specifier|final
name|LabelTypes
name|labelTypes
parameter_list|,
specifier|final
name|IdentifiedUser
operator|.
name|GenericFactory
name|userFactory
parameter_list|,
annotation|@
name|Assisted
specifier|final
name|ChangeControl
name|c
parameter_list|,
annotation|@
name|Assisted
specifier|final
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|,
annotation|@
name|Assisted
specifier|final
name|Collection
argument_list|<
name|PatchSetApproval
argument_list|>
name|all
parameter_list|)
block|{
name|this
operator|.
name|labelTypes
operator|=
name|labelTypes
expr_stmt|;
name|this
operator|.
name|userFactory
operator|=
name|userFactory
expr_stmt|;
name|callerChangeControl
operator|=
name|c
expr_stmt|;
name|change
operator|=
name|c
operator|.
name|getChange
argument_list|()
expr_stmt|;
for|for
control|(
specifier|final
name|PatchSetApproval
name|ca
range|:
name|all
control|)
block|{
if|if
condition|(
name|psId
operator|.
name|equals
argument_list|(
name|ca
operator|.
name|getPatchSetId
argument_list|()
argument_list|)
condition|)
block|{
name|Collection
argument_list|<
name|PatchSetApproval
argument_list|>
name|l
init|=
name|approvals
operator|.
name|get
argument_list|(
name|ca
operator|.
name|getCategoryId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|l
operator|==
literal|null
condition|)
block|{
name|l
operator|=
operator|new
name|ArrayList
argument_list|<
name|PatchSetApproval
argument_list|>
argument_list|()
expr_stmt|;
name|LabelType
name|lt
init|=
name|labelTypes
operator|.
name|byId
argument_list|(
name|ca
operator|.
name|getCategoryId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|lt
operator|!=
literal|null
condition|)
block|{
comment|// TODO: Support arbitrary labels
name|approvals
operator|.
name|put
argument_list|(
name|lt
operator|.
name|getName
argument_list|()
argument_list|,
name|l
argument_list|)
expr_stmt|;
block|}
block|}
name|l
operator|.
name|add
argument_list|(
name|ca
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getLabelTypes ()
name|List
argument_list|<
name|LabelType
argument_list|>
name|getLabelTypes
parameter_list|()
block|{
return|return
name|labelTypes
operator|.
name|getLabelTypes
argument_list|()
return|;
block|}
DECL|method|getChange ()
name|Change
name|getChange
parameter_list|()
block|{
return|return
name|change
return|;
block|}
DECL|method|valid (final LabelType lt, final boolean v)
specifier|public
name|void
name|valid
parameter_list|(
specifier|final
name|LabelType
name|lt
parameter_list|,
specifier|final
name|boolean
name|v
parameter_list|)
block|{
name|valid
operator|.
name|put
argument_list|(
name|id
argument_list|(
name|lt
argument_list|)
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
DECL|method|isValid (final LabelType lt)
specifier|public
name|boolean
name|isValid
parameter_list|(
specifier|final
name|LabelType
name|lt
parameter_list|)
block|{
return|return
name|isValid
argument_list|(
name|lt
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
DECL|method|isValid (final String labelName)
specifier|public
name|boolean
name|isValid
parameter_list|(
specifier|final
name|String
name|labelName
parameter_list|)
block|{
specifier|final
name|Boolean
name|b
init|=
name|valid
operator|.
name|get
argument_list|(
name|labelName
argument_list|)
decl_stmt|;
return|return
name|b
operator|!=
literal|null
operator|&&
name|b
return|;
block|}
DECL|method|getApprovals (final LabelType lt)
specifier|public
name|Collection
argument_list|<
name|PatchSetApproval
argument_list|>
name|getApprovals
parameter_list|(
specifier|final
name|LabelType
name|lt
parameter_list|)
block|{
return|return
name|getApprovals
argument_list|(
name|lt
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getApprovals (final String labelName)
specifier|public
name|Collection
argument_list|<
name|PatchSetApproval
argument_list|>
name|getApprovals
parameter_list|(
specifier|final
name|String
name|labelName
parameter_list|)
block|{
specifier|final
name|Collection
argument_list|<
name|PatchSetApproval
argument_list|>
name|l
init|=
name|approvals
operator|.
name|get
argument_list|(
name|labelName
argument_list|)
decl_stmt|;
return|return
name|l
operator|!=
literal|null
condition|?
name|l
else|:
name|Collections
operator|.
expr|<
name|PatchSetApproval
operator|>
name|emptySet
argument_list|()
return|;
block|}
comment|/**    * Normalize the approval record down to the range permitted by the type, in    * case the type was modified since the approval was originally granted.    *<p>    */
DECL|method|applyTypeFloor (final LabelType lt, final PatchSetApproval a)
specifier|private
name|void
name|applyTypeFloor
parameter_list|(
specifier|final
name|LabelType
name|lt
parameter_list|,
specifier|final
name|PatchSetApproval
name|a
parameter_list|)
block|{
specifier|final
name|LabelValue
name|atMin
init|=
name|lt
operator|.
name|getMin
argument_list|()
decl_stmt|;
if|if
condition|(
name|atMin
operator|!=
literal|null
operator|&&
name|a
operator|.
name|getValue
argument_list|()
operator|<
name|atMin
operator|.
name|getValue
argument_list|()
condition|)
block|{
name|a
operator|.
name|setValue
argument_list|(
name|atMin
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|LabelValue
name|atMax
init|=
name|lt
operator|.
name|getMax
argument_list|()
decl_stmt|;
if|if
condition|(
name|atMax
operator|!=
literal|null
operator|&&
name|a
operator|.
name|getValue
argument_list|()
operator|>
name|atMax
operator|.
name|getValue
argument_list|()
condition|)
block|{
name|a
operator|.
name|setValue
argument_list|(
name|atMax
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Normalize the approval record to be inside the maximum range permitted by    * the RefRights granted to groups the account is a member of.    *<p>    * If multiple RefRights are matched (assigned to different groups the account    * is a member of) the lowest minValue and the highest maxValue of the union    * of them is used.    *<p>    */
DECL|method|applyRightFloor (final LabelType lt, final PatchSetApproval a)
specifier|private
name|void
name|applyRightFloor
parameter_list|(
specifier|final
name|LabelType
name|lt
parameter_list|,
specifier|final
name|PatchSetApproval
name|a
parameter_list|)
block|{
specifier|final
name|String
name|permission
init|=
name|Permission
operator|.
name|forLabel
argument_list|(
name|lt
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|IdentifiedUser
name|user
init|=
name|userFactory
operator|.
name|create
argument_list|(
name|a
operator|.
name|getAccountId
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|PermissionRange
name|range
init|=
name|controlFor
argument_list|(
name|user
argument_list|)
operator|.
name|getRange
argument_list|(
name|permission
argument_list|)
decl_stmt|;
name|a
operator|.
name|setValue
argument_list|(
operator|(
name|short
operator|)
name|range
operator|.
name|squash
argument_list|(
name|a
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|controlFor (CurrentUser user)
specifier|private
name|ChangeControl
name|controlFor
parameter_list|(
name|CurrentUser
name|user
parameter_list|)
block|{
return|return
name|callerChangeControl
operator|.
name|forUser
argument_list|(
name|user
argument_list|)
return|;
block|}
comment|/** Run<code>applyTypeFloor</code>,<code>applyRightFloor</code>. */
DECL|method|normalize (final LabelType lt, final PatchSetApproval ca)
specifier|public
name|void
name|normalize
parameter_list|(
specifier|final
name|LabelType
name|lt
parameter_list|,
specifier|final
name|PatchSetApproval
name|ca
parameter_list|)
block|{
name|applyTypeFloor
argument_list|(
name|lt
argument_list|,
name|ca
argument_list|)
expr_stmt|;
name|applyRightFloor
argument_list|(
name|lt
argument_list|,
name|ca
argument_list|)
expr_stmt|;
block|}
DECL|method|id (final LabelType lt)
specifier|private
specifier|static
name|String
name|id
parameter_list|(
specifier|final
name|LabelType
name|lt
parameter_list|)
block|{
return|return
name|lt
operator|.
name|getId
argument_list|()
return|;
block|}
block|}
end_class

end_unit

