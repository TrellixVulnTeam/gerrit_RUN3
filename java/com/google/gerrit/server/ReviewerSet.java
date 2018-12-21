begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2016 The Android Open Source Project
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
DECL|package|com.google.gerrit.server
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
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
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|notedb
operator|.
name|ReviewerStateInternal
operator|.
name|CC
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|notedb
operator|.
name|ReviewerStateInternal
operator|.
name|REVIEWER
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
name|HashBasedTable
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
name|ImmutableSet
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
name|ImmutableTable
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
name|Table
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
name|notedb
operator|.
name|ReviewerStateInternal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Timestamp
import|;
end_import

begin_comment
comment|/**  * Set of reviewers on a change.  *  *<p>A given account may appear in multiple states and at different timestamps. No reviewers with  * state {@link ReviewerStateInternal#REMOVED} are ever exposed by this interface.  */
end_comment

begin_class
DECL|class|ReviewerSet
specifier|public
class|class
name|ReviewerSet
block|{
DECL|field|EMPTY
specifier|private
specifier|static
specifier|final
name|ReviewerSet
name|EMPTY
init|=
operator|new
name|ReviewerSet
argument_list|(
name|ImmutableTable
operator|.
name|of
argument_list|()
argument_list|)
decl_stmt|;
DECL|method|fromApprovals (Iterable<PatchSetApproval> approvals)
specifier|public
specifier|static
name|ReviewerSet
name|fromApprovals
parameter_list|(
name|Iterable
argument_list|<
name|PatchSetApproval
argument_list|>
name|approvals
parameter_list|)
block|{
name|PatchSetApproval
name|first
init|=
literal|null
decl_stmt|;
name|Table
argument_list|<
name|ReviewerStateInternal
argument_list|,
name|Account
operator|.
name|Id
argument_list|,
name|Timestamp
argument_list|>
name|reviewers
init|=
name|HashBasedTable
operator|.
name|create
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
if|if
condition|(
name|first
operator|==
literal|null
condition|)
block|{
name|first
operator|=
name|psa
expr_stmt|;
block|}
else|else
block|{
name|checkArgument
argument_list|(
name|first
operator|.
name|getKey
argument_list|()
operator|.
name|getParentKey
argument_list|()
operator|.
name|getParentKey
argument_list|()
operator|.
name|equals
argument_list|(
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
argument_list|)
argument_list|,
literal|"multiple change IDs: %s, %s"
argument_list|,
name|first
operator|.
name|getKey
argument_list|()
argument_list|,
name|psa
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Account
operator|.
name|Id
name|id
init|=
name|psa
operator|.
name|getAccountId
argument_list|()
decl_stmt|;
name|reviewers
operator|.
name|put
argument_list|(
name|REVIEWER
argument_list|,
name|id
argument_list|,
name|psa
operator|.
name|getGranted
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|psa
operator|.
name|getValue
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|reviewers
operator|.
name|remove
argument_list|(
name|CC
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|ReviewerSet
argument_list|(
name|reviewers
argument_list|)
return|;
block|}
DECL|method|fromTable (Table<ReviewerStateInternal, Account.Id, Timestamp> table)
specifier|public
specifier|static
name|ReviewerSet
name|fromTable
parameter_list|(
name|Table
argument_list|<
name|ReviewerStateInternal
argument_list|,
name|Account
operator|.
name|Id
argument_list|,
name|Timestamp
argument_list|>
name|table
parameter_list|)
block|{
return|return
operator|new
name|ReviewerSet
argument_list|(
name|table
argument_list|)
return|;
block|}
DECL|method|empty ()
specifier|public
specifier|static
name|ReviewerSet
name|empty
parameter_list|()
block|{
return|return
name|EMPTY
return|;
block|}
DECL|field|table
specifier|private
specifier|final
name|ImmutableTable
argument_list|<
name|ReviewerStateInternal
argument_list|,
name|Account
operator|.
name|Id
argument_list|,
name|Timestamp
argument_list|>
name|table
decl_stmt|;
DECL|field|accounts
specifier|private
name|ImmutableSet
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|accounts
decl_stmt|;
DECL|method|ReviewerSet (Table<ReviewerStateInternal, Account.Id, Timestamp> table)
specifier|private
name|ReviewerSet
parameter_list|(
name|Table
argument_list|<
name|ReviewerStateInternal
argument_list|,
name|Account
operator|.
name|Id
argument_list|,
name|Timestamp
argument_list|>
name|table
parameter_list|)
block|{
name|this
operator|.
name|table
operator|=
name|ImmutableTable
operator|.
name|copyOf
argument_list|(
name|table
argument_list|)
expr_stmt|;
block|}
DECL|method|all ()
specifier|public
name|ImmutableSet
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|all
parameter_list|()
block|{
if|if
condition|(
name|accounts
operator|==
literal|null
condition|)
block|{
comment|// Idempotent and immutable, don't bother locking.
name|accounts
operator|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|table
operator|.
name|columnKeySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|accounts
return|;
block|}
DECL|method|byState (ReviewerStateInternal state)
specifier|public
name|ImmutableSet
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|byState
parameter_list|(
name|ReviewerStateInternal
name|state
parameter_list|)
block|{
return|return
name|table
operator|.
name|row
argument_list|(
name|state
argument_list|)
operator|.
name|keySet
argument_list|()
return|;
block|}
DECL|method|asTable ()
specifier|public
name|ImmutableTable
argument_list|<
name|ReviewerStateInternal
argument_list|,
name|Account
operator|.
name|Id
argument_list|,
name|Timestamp
argument_list|>
name|asTable
parameter_list|()
block|{
return|return
name|table
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
return|return
operator|(
name|o
operator|instanceof
name|ReviewerSet
operator|)
operator|&&
name|table
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|ReviewerSet
operator|)
name|o
operator|)
operator|.
name|table
argument_list|)
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
name|table
operator|.
name|hashCode
argument_list|()
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
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
name|table
return|;
block|}
block|}
end_class

end_unit

