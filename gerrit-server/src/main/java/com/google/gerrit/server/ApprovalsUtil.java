begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2009 The Android Open Source Project
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
name|common
operator|.
name|collect
operator|.
name|Sets
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
name|Account
operator|.
name|Id
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
name|reviewdb
operator|.
name|client
operator|.
name|PatchSetApproval
operator|.
name|LabelId
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
name|PatchSetInfo
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
name|server
operator|.
name|ReviewDb
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|server
operator|.
name|OrmException
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
name|List
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
comment|/**  * Utility functions to manipulate patchset approvals.  *<p>  * Approvals are overloaded, they represent both approvals and reviewers  * which should be CCed on a change.  To ensure that reviewers are not lost  * there must always be an approval on each patchset for each reviewer,  * even if the reviewer hasn't actually given a score to the change.  To  * mark the "no score" case, a dummy approval, which may live in any of  * the available categories, with a score of 0 is used.  */
end_comment

begin_class
DECL|class|ApprovalsUtil
specifier|public
class|class
name|ApprovalsUtil
block|{
DECL|field|db
specifier|private
specifier|final
name|ReviewDb
name|db
decl_stmt|;
annotation|@
name|Inject
DECL|method|ApprovalsUtil (ReviewDb db)
specifier|public
name|ApprovalsUtil
parameter_list|(
name|ReviewDb
name|db
parameter_list|)
block|{
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
block|}
comment|/**    * Resync the changeOpen status which is cached in the approvals table for    * performance reasons    */
DECL|method|syncChangeStatus (final Change change)
specifier|public
name|void
name|syncChangeStatus
parameter_list|(
specifier|final
name|Change
name|change
parameter_list|)
throws|throws
name|OrmException
block|{
specifier|final
name|List
argument_list|<
name|PatchSetApproval
argument_list|>
name|approvals
init|=
name|db
operator|.
name|patchSetApprovals
argument_list|()
operator|.
name|byChange
argument_list|(
name|change
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|toList
argument_list|()
decl_stmt|;
for|for
control|(
name|PatchSetApproval
name|a
range|:
name|approvals
control|)
block|{
name|a
operator|.
name|cache
argument_list|(
name|change
argument_list|)
expr_stmt|;
block|}
name|db
operator|.
name|patchSetApprovals
argument_list|()
operator|.
name|update
argument_list|(
name|approvals
argument_list|)
expr_stmt|;
block|}
comment|/**    * Moves the PatchSetApprovals to the specified PatchSet on the change from    * the prior PatchSet, while keeping the vetos.    *    * @param db database connection to use for updates.    * @param dest PatchSet to copy to    * @throws OrmException    * @return List<PatchSetApproval> The previous approvals    */
DECL|method|copyVetosToPatchSet (ReviewDb db, LabelTypes labelTypes, PatchSet.Id dest)
specifier|public
name|List
argument_list|<
name|PatchSetApproval
argument_list|>
name|copyVetosToPatchSet
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|LabelTypes
name|labelTypes
parameter_list|,
name|PatchSet
operator|.
name|Id
name|dest
parameter_list|)
throws|throws
name|OrmException
block|{
name|PatchSet
operator|.
name|Id
name|source
decl_stmt|;
if|if
condition|(
name|dest
operator|.
name|get
argument_list|()
operator|>
literal|1
condition|)
block|{
name|source
operator|=
operator|new
name|PatchSet
operator|.
name|Id
argument_list|(
name|dest
operator|.
name|getParentKey
argument_list|()
argument_list|,
name|dest
operator|.
name|get
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|OrmException
argument_list|(
literal|"Previous patch set could not be found"
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|PatchSetApproval
argument_list|>
name|patchSetApprovals
init|=
name|db
operator|.
name|patchSetApprovals
argument_list|()
operator|.
name|byChange
argument_list|(
name|dest
operator|.
name|getParentKey
argument_list|()
argument_list|)
operator|.
name|toList
argument_list|()
decl_stmt|;
for|for
control|(
name|PatchSetApproval
name|a
range|:
name|patchSetApprovals
control|)
block|{
name|LabelType
name|type
init|=
name|labelTypes
operator|.
name|byLabel
argument_list|(
name|a
operator|.
name|getLabelId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
operator|&&
name|a
operator|.
name|getPatchSetId
argument_list|()
operator|.
name|equals
argument_list|(
name|source
argument_list|)
operator|&&
name|type
operator|.
name|isCopyMinScore
argument_list|()
operator|&&
name|type
operator|.
name|isMaxNegative
argument_list|(
name|a
argument_list|)
condition|)
block|{
name|db
operator|.
name|patchSetApprovals
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
operator|new
name|PatchSetApproval
argument_list|(
name|dest
argument_list|,
name|a
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|patchSetApprovals
return|;
block|}
DECL|method|addReviewers (ReviewDb db, LabelTypes labelTypes, Change change, PatchSet ps, PatchSetInfo info, Set<Id> wantReviewers, Set<Account.Id> existingReviewers)
specifier|public
name|void
name|addReviewers
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|LabelTypes
name|labelTypes
parameter_list|,
name|Change
name|change
parameter_list|,
name|PatchSet
name|ps
parameter_list|,
name|PatchSetInfo
name|info
parameter_list|,
name|Set
argument_list|<
name|Id
argument_list|>
name|wantReviewers
parameter_list|,
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|existingReviewers
parameter_list|)
throws|throws
name|OrmException
block|{
name|List
argument_list|<
name|LabelType
argument_list|>
name|allTypes
init|=
name|labelTypes
operator|.
name|getLabelTypes
argument_list|()
decl_stmt|;
if|if
condition|(
name|allTypes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|need
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|wantReviewers
argument_list|)
decl_stmt|;
name|Account
operator|.
name|Id
name|authorId
init|=
name|info
operator|.
name|getAuthor
argument_list|()
operator|!=
literal|null
condition|?
name|info
operator|.
name|getAuthor
argument_list|()
operator|.
name|getAccount
argument_list|()
else|:
literal|null
decl_stmt|;
if|if
condition|(
name|authorId
operator|!=
literal|null
operator|&&
operator|!
name|ps
operator|.
name|isDraft
argument_list|()
condition|)
block|{
name|need
operator|.
name|add
argument_list|(
name|authorId
argument_list|)
expr_stmt|;
block|}
name|Account
operator|.
name|Id
name|committerId
init|=
name|info
operator|.
name|getCommitter
argument_list|()
operator|!=
literal|null
condition|?
name|info
operator|.
name|getCommitter
argument_list|()
operator|.
name|getAccount
argument_list|()
else|:
literal|null
decl_stmt|;
if|if
condition|(
name|committerId
operator|!=
literal|null
operator|&&
operator|!
name|ps
operator|.
name|isDraft
argument_list|()
condition|)
block|{
name|need
operator|.
name|add
argument_list|(
name|committerId
argument_list|)
expr_stmt|;
block|}
name|need
operator|.
name|remove
argument_list|(
name|change
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|need
operator|.
name|removeAll
argument_list|(
name|existingReviewers
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|PatchSetApproval
argument_list|>
name|cells
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|need
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|LabelId
name|labelId
init|=
name|Iterables
operator|.
name|getLast
argument_list|(
name|allTypes
argument_list|)
operator|.
name|getLabelId
argument_list|()
decl_stmt|;
for|for
control|(
name|Account
operator|.
name|Id
name|account
range|:
name|need
control|)
block|{
name|PatchSetApproval
name|psa
init|=
operator|new
name|PatchSetApproval
argument_list|(
operator|new
name|PatchSetApproval
operator|.
name|Key
argument_list|(
name|ps
operator|.
name|getId
argument_list|()
argument_list|,
name|account
argument_list|,
name|labelId
argument_list|)
argument_list|,
operator|(
name|short
operator|)
literal|0
argument_list|)
decl_stmt|;
name|psa
operator|.
name|cache
argument_list|(
name|change
argument_list|)
expr_stmt|;
name|cells
operator|.
name|add
argument_list|(
name|psa
argument_list|)
expr_stmt|;
block|}
name|db
operator|.
name|patchSetApprovals
argument_list|()
operator|.
name|insert
argument_list|(
name|cells
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

