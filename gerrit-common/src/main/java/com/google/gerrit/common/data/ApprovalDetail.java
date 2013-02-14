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
DECL|package|com.google.gerrit.common.data
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|common
operator|.
name|data
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
name|PatchSetApproval
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
name|Comparator
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
name|HashSet
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_class
DECL|class|ApprovalDetail
specifier|public
class|class
name|ApprovalDetail
block|{
DECL|field|SORT
specifier|public
specifier|static
specifier|final
name|Comparator
argument_list|<
name|ApprovalDetail
argument_list|>
name|SORT
init|=
operator|new
name|Comparator
argument_list|<
name|ApprovalDetail
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|ApprovalDetail
name|o1
parameter_list|,
name|ApprovalDetail
name|o2
parameter_list|)
block|{
return|return
name|o1
operator|.
name|hasNonZero
operator|-
name|o2
operator|.
name|hasNonZero
return|;
block|}
block|}
decl_stmt|;
DECL|field|account
specifier|protected
name|Account
operator|.
name|Id
name|account
decl_stmt|;
DECL|field|approvals
specifier|protected
name|List
argument_list|<
name|PatchSetApproval
argument_list|>
name|approvals
decl_stmt|;
DECL|field|canRemove
specifier|protected
name|boolean
name|canRemove
decl_stmt|;
DECL|field|votable
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|votable
decl_stmt|;
DECL|field|approved
specifier|private
specifier|transient
name|Set
argument_list|<
name|String
argument_list|>
name|approved
decl_stmt|;
DECL|field|rejected
specifier|private
specifier|transient
name|Set
argument_list|<
name|String
argument_list|>
name|rejected
decl_stmt|;
DECL|field|values
specifier|private
specifier|transient
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|values
decl_stmt|;
DECL|field|hasNonZero
specifier|private
specifier|transient
name|int
name|hasNonZero
decl_stmt|;
DECL|method|ApprovalDetail ()
specifier|protected
name|ApprovalDetail
parameter_list|()
block|{   }
DECL|method|ApprovalDetail (final Account.Id id)
specifier|public
name|ApprovalDetail
parameter_list|(
specifier|final
name|Account
operator|.
name|Id
name|id
parameter_list|)
block|{
name|account
operator|=
name|id
expr_stmt|;
name|approvals
operator|=
operator|new
name|ArrayList
argument_list|<
name|PatchSetApproval
argument_list|>
argument_list|()
expr_stmt|;
block|}
DECL|method|getAccount ()
specifier|public
name|Account
operator|.
name|Id
name|getAccount
parameter_list|()
block|{
return|return
name|account
return|;
block|}
DECL|method|canRemove ()
specifier|public
name|boolean
name|canRemove
parameter_list|()
block|{
return|return
name|canRemove
return|;
block|}
DECL|method|setCanRemove (boolean removeable)
specifier|public
name|void
name|setCanRemove
parameter_list|(
name|boolean
name|removeable
parameter_list|)
block|{
name|canRemove
operator|=
name|removeable
expr_stmt|;
block|}
DECL|method|approved (String label)
specifier|public
name|void
name|approved
parameter_list|(
name|String
name|label
parameter_list|)
block|{
if|if
condition|(
name|approved
operator|==
literal|null
condition|)
block|{
name|approved
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|approved
operator|.
name|add
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|hasNonZero
operator|=
literal|1
expr_stmt|;
block|}
DECL|method|rejected (String label)
specifier|public
name|void
name|rejected
parameter_list|(
name|String
name|label
parameter_list|)
block|{
if|if
condition|(
name|rejected
operator|==
literal|null
condition|)
block|{
name|rejected
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|rejected
operator|.
name|add
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|hasNonZero
operator|=
literal|1
expr_stmt|;
block|}
DECL|method|votable (String label)
specifier|public
name|void
name|votable
parameter_list|(
name|String
name|label
parameter_list|)
block|{
if|if
condition|(
name|votable
operator|==
literal|null
condition|)
block|{
name|votable
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|votable
operator|.
name|add
argument_list|(
name|label
argument_list|)
expr_stmt|;
block|}
DECL|method|value (String label, int value)
specifier|public
name|void
name|value
parameter_list|(
name|String
name|label
parameter_list|,
name|int
name|value
parameter_list|)
block|{
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
name|values
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|values
operator|.
name|put
argument_list|(
name|label
argument_list|,
name|value
argument_list|)
expr_stmt|;
if|if
condition|(
name|value
operator|!=
literal|0
condition|)
block|{
name|hasNonZero
operator|=
literal|1
expr_stmt|;
block|}
block|}
DECL|method|isApproved (String label)
specifier|public
name|boolean
name|isApproved
parameter_list|(
name|String
name|label
parameter_list|)
block|{
return|return
name|approved
operator|!=
literal|null
operator|&&
name|approved
operator|.
name|contains
argument_list|(
name|label
argument_list|)
return|;
block|}
DECL|method|isRejected (String label)
specifier|public
name|boolean
name|isRejected
parameter_list|(
name|String
name|label
parameter_list|)
block|{
return|return
name|rejected
operator|!=
literal|null
operator|&&
name|rejected
operator|.
name|contains
argument_list|(
name|label
argument_list|)
return|;
block|}
DECL|method|canVote (String label)
specifier|public
name|boolean
name|canVote
parameter_list|(
name|String
name|label
parameter_list|)
block|{
return|return
name|votable
operator|!=
literal|null
operator|&&
name|votable
operator|.
name|contains
argument_list|(
name|label
argument_list|)
return|;
block|}
DECL|method|getValue (String label)
specifier|public
name|int
name|getValue
parameter_list|(
name|String
name|label
parameter_list|)
block|{
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
name|Integer
name|v
init|=
name|values
operator|.
name|get
argument_list|(
name|label
argument_list|)
decl_stmt|;
return|return
name|v
operator|!=
literal|null
condition|?
name|v
else|:
literal|0
return|;
block|}
block|}
end_class

end_unit

