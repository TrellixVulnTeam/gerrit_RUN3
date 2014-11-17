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
DECL|package|com.google.gerrit.server.git
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|git
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
name|base
operator|.
name|Preconditions
operator|.
name|checkArgument
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
name|annotations
operator|.
name|VisibleForTesting
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
name|MoreObjects
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
name|Iterables
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
name|Lists
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
name|gerrit
operator|.
name|server
operator|.
name|project
operator|.
name|NoSuchChangeException
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
name|Singleton
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * Normalizes votes on labels according to project config and permissions.  *<p>  * Votes are recorded in the database for a user based on the state of the  * project at that time: what labels are defined for the project, and what the  * user is allowed to vote on. Both of those can change between the time a vote  * is originally made and a later point, for example when a change is submitted.  * This class normalizes old votes against current project configuration.  */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|LabelNormalizer
specifier|public
class|class
name|LabelNormalizer
block|{
DECL|class|Result
specifier|public
specifier|static
class|class
name|Result
block|{
DECL|field|unchanged
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|PatchSetApproval
argument_list|>
name|unchanged
decl_stmt|;
DECL|field|updated
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|PatchSetApproval
argument_list|>
name|updated
decl_stmt|;
DECL|field|deleted
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|PatchSetApproval
argument_list|>
name|deleted
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|method|Result ( List<PatchSetApproval> unchanged, List<PatchSetApproval> updated, List<PatchSetApproval> deleted)
name|Result
parameter_list|(
name|List
argument_list|<
name|PatchSetApproval
argument_list|>
name|unchanged
parameter_list|,
name|List
argument_list|<
name|PatchSetApproval
argument_list|>
name|updated
parameter_list|,
name|List
argument_list|<
name|PatchSetApproval
argument_list|>
name|deleted
parameter_list|)
block|{
name|this
operator|.
name|unchanged
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|unchanged
argument_list|)
expr_stmt|;
name|this
operator|.
name|updated
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|updated
argument_list|)
expr_stmt|;
name|this
operator|.
name|deleted
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|deleted
argument_list|)
expr_stmt|;
block|}
DECL|method|getUnchanged ()
specifier|public
name|ImmutableList
argument_list|<
name|PatchSetApproval
argument_list|>
name|getUnchanged
parameter_list|()
block|{
return|return
name|unchanged
return|;
block|}
DECL|method|getUpdated ()
specifier|public
name|ImmutableList
argument_list|<
name|PatchSetApproval
argument_list|>
name|getUpdated
parameter_list|()
block|{
return|return
name|updated
return|;
block|}
DECL|method|getDeleted ()
specifier|public
name|ImmutableList
argument_list|<
name|PatchSetApproval
argument_list|>
name|getDeleted
parameter_list|()
block|{
return|return
name|deleted
return|;
block|}
DECL|method|getNormalized ()
specifier|public
name|Iterable
argument_list|<
name|PatchSetApproval
argument_list|>
name|getNormalized
parameter_list|()
block|{
return|return
name|Iterables
operator|.
name|concat
argument_list|(
name|unchanged
argument_list|,
name|updated
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|Result
condition|)
block|{
name|Result
name|r
init|=
operator|(
name|Result
operator|)
name|o
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|unchanged
argument_list|,
name|r
operator|.
name|unchanged
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|updated
argument_list|,
name|r
operator|.
name|updated
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|deleted
argument_list|,
name|r
operator|.
name|deleted
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|unchanged
argument_list|,
name|updated
argument_list|,
name|deleted
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|MoreObjects
operator|.
name|toStringHelper
argument_list|(
name|this
argument_list|)
operator|.
name|add
argument_list|(
literal|"unchanged"
argument_list|,
name|unchanged
argument_list|)
operator|.
name|add
argument_list|(
literal|"updated"
argument_list|,
name|updated
argument_list|)
operator|.
name|add
argument_list|(
literal|"deleted"
argument_list|,
name|deleted
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
DECL|field|changeFactory
specifier|private
specifier|final
name|ChangeControl
operator|.
name|GenericFactory
name|changeFactory
decl_stmt|;
DECL|field|userFactory
specifier|private
specifier|final
name|IdentifiedUser
operator|.
name|GenericFactory
name|userFactory
decl_stmt|;
annotation|@
name|Inject
DECL|method|LabelNormalizer (ChangeControl.GenericFactory changeFactory, IdentifiedUser.GenericFactory userFactory)
name|LabelNormalizer
parameter_list|(
name|ChangeControl
operator|.
name|GenericFactory
name|changeFactory
parameter_list|,
name|IdentifiedUser
operator|.
name|GenericFactory
name|userFactory
parameter_list|)
block|{
name|this
operator|.
name|changeFactory
operator|=
name|changeFactory
expr_stmt|;
name|this
operator|.
name|userFactory
operator|=
name|userFactory
expr_stmt|;
block|}
comment|/**    * @param change change containing the given approvals.    * @param approvals list of approvals.    * @return copies of approvals normalized to the defined ranges for the label    *     type and permissions for the user. Approvals for unknown labels are not    *     included in the output, nor are approvals where the user has no    *     permissions for that label.    * @throws NoSuchChangeException    */
DECL|method|normalize (Change change, Collection<PatchSetApproval> approvals)
specifier|public
name|Result
name|normalize
parameter_list|(
name|Change
name|change
parameter_list|,
name|Collection
argument_list|<
name|PatchSetApproval
argument_list|>
name|approvals
parameter_list|)
throws|throws
name|NoSuchChangeException
block|{
return|return
name|normalize
argument_list|(
name|changeFactory
operator|.
name|controlFor
argument_list|(
name|change
argument_list|,
name|userFactory
operator|.
name|create
argument_list|(
name|change
operator|.
name|getOwner
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|approvals
argument_list|)
return|;
block|}
comment|/**    * @param ctl change control containing the given approvals.    * @param approvals list of approvals.    * @return copies of approvals normalized to the defined ranges for the label    *     type and permissions for the user. Approvals for unknown labels are not    *     included in the output, nor are approvals where the user has no    *     permissions for that label.    */
DECL|method|normalize (ChangeControl ctl, Collection<PatchSetApproval> approvals)
specifier|public
name|Result
name|normalize
parameter_list|(
name|ChangeControl
name|ctl
parameter_list|,
name|Collection
argument_list|<
name|PatchSetApproval
argument_list|>
name|approvals
parameter_list|)
block|{
name|List
argument_list|<
name|PatchSetApproval
argument_list|>
name|unchanged
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|approvals
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|PatchSetApproval
argument_list|>
name|updated
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|approvals
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|PatchSetApproval
argument_list|>
name|deleted
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|approvals
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|LabelTypes
name|labelTypes
init|=
name|ctl
operator|.
name|getLabelTypes
argument_list|()
decl_stmt|;
for|for
control|(
name|PatchSetApproval
name|psa
range|:
name|approvals
control|)
block|{
name|Change
operator|.
name|Id
name|changeId
init|=
name|psa
operator|.
name|getKey
argument_list|()
operator|.
name|getParentKey
argument_list|()
operator|.
name|getParentKey
argument_list|()
decl_stmt|;
name|checkArgument
argument_list|(
name|changeId
operator|.
name|equals
argument_list|(
name|ctl
operator|.
name|getChange
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|,
literal|"Approval %s does not match change %s"
argument_list|,
name|psa
operator|.
name|getKey
argument_list|()
argument_list|,
name|ctl
operator|.
name|getChange
argument_list|()
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|psa
operator|.
name|isSubmit
argument_list|()
condition|)
block|{
name|unchanged
operator|.
name|add
argument_list|(
name|psa
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|LabelType
name|label
init|=
name|labelTypes
operator|.
name|byLabel
argument_list|(
name|psa
operator|.
name|getLabelId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|label
operator|==
literal|null
condition|)
block|{
name|deleted
operator|.
name|add
argument_list|(
name|psa
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|PatchSetApproval
name|copy
init|=
name|copy
argument_list|(
name|psa
argument_list|)
decl_stmt|;
name|applyTypeFloor
argument_list|(
name|label
argument_list|,
name|copy
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|applyRightFloor
argument_list|(
name|ctl
argument_list|,
name|label
argument_list|,
name|copy
argument_list|)
condition|)
block|{
name|deleted
operator|.
name|add
argument_list|(
name|psa
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|copy
operator|.
name|getValue
argument_list|()
operator|!=
name|psa
operator|.
name|getValue
argument_list|()
condition|)
block|{
name|updated
operator|.
name|add
argument_list|(
name|copy
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|unchanged
operator|.
name|add
argument_list|(
name|psa
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|Result
argument_list|(
name|unchanged
argument_list|,
name|updated
argument_list|,
name|deleted
argument_list|)
return|;
block|}
comment|/**    * @param ctl change control (for any user).    * @param lt label type.    * @param id account ID.    * @return whether the given account ID has any permissions to vote on this    *     label for this change.    */
DECL|method|canVote (ChangeControl ctl, LabelType lt, Account.Id id)
specifier|public
name|boolean
name|canVote
parameter_list|(
name|ChangeControl
name|ctl
parameter_list|,
name|LabelType
name|lt
parameter_list|,
name|Account
operator|.
name|Id
name|id
parameter_list|)
block|{
return|return
operator|!
name|getRange
argument_list|(
name|ctl
argument_list|,
name|lt
argument_list|,
name|id
argument_list|)
operator|.
name|isEmpty
argument_list|()
return|;
block|}
DECL|method|copy (PatchSetApproval src)
specifier|private
name|PatchSetApproval
name|copy
parameter_list|(
name|PatchSetApproval
name|src
parameter_list|)
block|{
return|return
operator|new
name|PatchSetApproval
argument_list|(
name|src
operator|.
name|getPatchSetId
argument_list|()
argument_list|,
name|src
argument_list|)
return|;
block|}
DECL|method|getRange (ChangeControl ctl, LabelType lt, Account.Id id)
specifier|private
name|PermissionRange
name|getRange
parameter_list|(
name|ChangeControl
name|ctl
parameter_list|,
name|LabelType
name|lt
parameter_list|,
name|Account
operator|.
name|Id
name|id
parameter_list|)
block|{
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
name|IdentifiedUser
name|user
init|=
name|userFactory
operator|.
name|create
argument_list|(
name|id
argument_list|)
decl_stmt|;
return|return
name|ctl
operator|.
name|forUser
argument_list|(
name|user
argument_list|)
operator|.
name|getRange
argument_list|(
name|permission
argument_list|)
return|;
block|}
DECL|method|applyRightFloor (ChangeControl ctl, LabelType lt, PatchSetApproval a)
specifier|private
name|boolean
name|applyRightFloor
parameter_list|(
name|ChangeControl
name|ctl
parameter_list|,
name|LabelType
name|lt
parameter_list|,
name|PatchSetApproval
name|a
parameter_list|)
block|{
name|PermissionRange
name|range
init|=
name|getRange
argument_list|(
name|ctl
argument_list|,
name|lt
argument_list|,
name|a
operator|.
name|getAccountId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|range
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
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
return|return
literal|true
return|;
block|}
DECL|method|applyTypeFloor (LabelType lt, PatchSetApproval a)
specifier|private
name|void
name|applyTypeFloor
parameter_list|(
name|LabelType
name|lt
parameter_list|,
name|PatchSetApproval
name|a
parameter_list|)
block|{
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
block|}
end_class

end_unit

