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
DECL|package|com.google.gerrit.client.data
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
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
name|client
operator|.
name|changes
operator|.
name|ChangeScreen
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
name|ChangeMessage
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
name|client
operator|.
name|reviewdb
operator|.
name|PatchSetAncestor
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
name|RevId
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
name|ReviewDb
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
name|Common
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
name|workflow
operator|.
name|FunctionState
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
name|client
operator|.
name|OrmException
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
name|Collections
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

begin_comment
comment|/** Detail necessary to display {@link ChangeScreen}. */
end_comment

begin_class
DECL|class|ChangeDetail
specifier|public
class|class
name|ChangeDetail
block|{
DECL|field|accounts
specifier|protected
name|AccountInfoCache
name|accounts
decl_stmt|;
DECL|field|allowsAnonymous
specifier|protected
name|boolean
name|allowsAnonymous
decl_stmt|;
DECL|field|canAbandon
specifier|protected
name|boolean
name|canAbandon
decl_stmt|;
DECL|field|change
specifier|protected
name|Change
name|change
decl_stmt|;
DECL|field|dependsOn
specifier|protected
name|List
argument_list|<
name|ChangeInfo
argument_list|>
name|dependsOn
decl_stmt|;
DECL|field|neededBy
specifier|protected
name|List
argument_list|<
name|ChangeInfo
argument_list|>
name|neededBy
decl_stmt|;
DECL|field|patchSets
specifier|protected
name|List
argument_list|<
name|PatchSet
argument_list|>
name|patchSets
decl_stmt|;
DECL|field|approvals
specifier|protected
name|List
argument_list|<
name|ApprovalDetail
argument_list|>
name|approvals
decl_stmt|;
DECL|field|missingApprovals
specifier|protected
name|Set
argument_list|<
name|ApprovalCategory
operator|.
name|Id
argument_list|>
name|missingApprovals
decl_stmt|;
DECL|field|messages
specifier|protected
name|List
argument_list|<
name|ChangeMessage
argument_list|>
name|messages
decl_stmt|;
DECL|field|currentPatchSetId
specifier|protected
name|PatchSet
operator|.
name|Id
name|currentPatchSetId
decl_stmt|;
DECL|field|currentDetail
specifier|protected
name|PatchSetDetail
name|currentDetail
decl_stmt|;
DECL|field|currentActions
specifier|protected
name|Set
argument_list|<
name|ApprovalCategory
operator|.
name|Id
argument_list|>
name|currentActions
decl_stmt|;
DECL|method|ChangeDetail ()
specifier|public
name|ChangeDetail
parameter_list|()
block|{   }
DECL|method|load (final ReviewDb db, final AccountInfoCacheFactory acc, final Change c, final boolean allowAnon, final boolean canAbdn)
specifier|public
name|void
name|load
parameter_list|(
specifier|final
name|ReviewDb
name|db
parameter_list|,
specifier|final
name|AccountInfoCacheFactory
name|acc
parameter_list|,
specifier|final
name|Change
name|c
parameter_list|,
specifier|final
name|boolean
name|allowAnon
parameter_list|,
specifier|final
name|boolean
name|canAbdn
parameter_list|)
throws|throws
name|OrmException
block|{
name|change
operator|=
name|c
expr_stmt|;
specifier|final
name|Account
operator|.
name|Id
name|owner
init|=
name|change
operator|.
name|getOwner
argument_list|()
decl_stmt|;
name|acc
operator|.
name|want
argument_list|(
name|owner
argument_list|)
expr_stmt|;
name|allowsAnonymous
operator|=
name|allowAnon
expr_stmt|;
name|canAbandon
operator|=
name|canAbdn
expr_stmt|;
name|patchSets
operator|=
name|db
operator|.
name|patchSets
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
expr_stmt|;
name|messages
operator|=
name|db
operator|.
name|changeMessages
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
expr_stmt|;
for|for
control|(
specifier|final
name|ChangeMessage
name|m
range|:
name|messages
control|)
block|{
name|acc
operator|.
name|want
argument_list|(
name|m
operator|.
name|getAuthor
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|ChangeApproval
argument_list|>
name|allApprovals
init|=
name|db
operator|.
name|changeApprovals
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
if|if
condition|(
operator|!
name|change
operator|.
name|getStatus
argument_list|()
operator|.
name|isClosed
argument_list|()
condition|)
block|{
specifier|final
name|Account
operator|.
name|Id
name|me
init|=
name|Common
operator|.
name|getAccountId
argument_list|()
decl_stmt|;
specifier|final
name|FunctionState
name|fs
init|=
operator|new
name|FunctionState
argument_list|(
name|change
argument_list|,
name|allApprovals
argument_list|)
decl_stmt|;
name|missingApprovals
operator|=
operator|new
name|HashSet
argument_list|<
name|ApprovalCategory
operator|.
name|Id
argument_list|>
argument_list|()
expr_stmt|;
name|currentActions
operator|=
operator|new
name|HashSet
argument_list|<
name|ApprovalCategory
operator|.
name|Id
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
specifier|final
name|ApprovalType
name|at
range|:
name|Common
operator|.
name|getGerritConfig
argument_list|()
operator|.
name|getApprovalTypes
argument_list|()
control|)
block|{
name|at
operator|.
name|getCategory
argument_list|()
operator|.
name|getFunction
argument_list|()
operator|.
name|run
argument_list|(
name|at
argument_list|,
name|fs
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|fs
operator|.
name|isValid
argument_list|(
name|at
argument_list|)
condition|)
block|{
name|missingApprovals
operator|.
name|add
argument_list|(
name|at
operator|.
name|getCategory
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
specifier|final
name|ApprovalType
name|at
range|:
name|Common
operator|.
name|getGerritConfig
argument_list|()
operator|.
name|getActionTypes
argument_list|()
control|)
block|{
if|if
condition|(
name|at
operator|.
name|getCategory
argument_list|()
operator|.
name|getFunction
argument_list|()
operator|.
name|isValid
argument_list|(
name|me
argument_list|,
name|at
argument_list|,
name|fs
argument_list|)
condition|)
block|{
name|currentActions
operator|.
name|add
argument_list|(
name|at
operator|.
name|getCategory
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|final
name|HashMap
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|ApprovalDetail
argument_list|>
name|ad
init|=
operator|new
name|HashMap
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|ApprovalDetail
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ChangeApproval
name|ca
range|:
name|allApprovals
control|)
block|{
name|ApprovalDetail
name|d
init|=
name|ad
operator|.
name|get
argument_list|(
name|ca
operator|.
name|getAccountId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|d
operator|==
literal|null
condition|)
block|{
name|d
operator|=
operator|new
name|ApprovalDetail
argument_list|(
name|ca
operator|.
name|getAccountId
argument_list|()
argument_list|)
expr_stmt|;
name|ad
operator|.
name|put
argument_list|(
name|d
operator|.
name|getAccount
argument_list|()
argument_list|,
name|d
argument_list|)
expr_stmt|;
block|}
name|d
operator|.
name|add
argument_list|(
name|ca
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ad
operator|.
name|containsKey
argument_list|(
name|owner
argument_list|)
condition|)
block|{
comment|// Ensure the owner always sorts to the top of the table
comment|//
specifier|final
name|ApprovalDetail
name|d
init|=
name|ad
operator|.
name|get
argument_list|(
name|owner
argument_list|)
decl_stmt|;
name|d
operator|.
name|hasNonZero
operator|=
literal|1
expr_stmt|;
name|d
operator|.
name|sortOrder
operator|=
name|ApprovalDetail
operator|.
name|EG_0
expr_stmt|;
block|}
name|acc
operator|.
name|want
argument_list|(
name|ad
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
name|approvals
operator|=
operator|new
name|ArrayList
argument_list|<
name|ApprovalDetail
argument_list|>
argument_list|(
name|ad
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|approvals
argument_list|,
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
specifier|final
name|ApprovalDetail
name|o1
parameter_list|,
specifier|final
name|ApprovalDetail
name|o2
parameter_list|)
block|{
name|int
name|cmp
decl_stmt|;
name|cmp
operator|=
name|o2
operator|.
name|hasNonZero
operator|-
name|o1
operator|.
name|hasNonZero
expr_stmt|;
if|if
condition|(
name|cmp
operator|!=
literal|0
condition|)
return|return
name|cmp
return|;
return|return
name|o1
operator|.
name|sortOrder
operator|.
name|compareTo
argument_list|(
name|o2
operator|.
name|sortOrder
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|currentPatchSetId
operator|=
name|change
operator|.
name|currentPatchSetId
argument_list|()
expr_stmt|;
if|if
condition|(
name|currentPatchSetId
operator|!=
literal|null
condition|)
block|{
name|currentDetail
operator|=
operator|new
name|PatchSetDetail
argument_list|()
expr_stmt|;
name|currentDetail
operator|.
name|load
argument_list|(
name|db
argument_list|,
name|getCurrentPatchSet
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|HashSet
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
name|changesToGet
init|=
operator|new
name|HashSet
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
name|ancestorOrder
init|=
operator|new
name|ArrayList
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|PatchSetAncestor
name|a
range|:
name|db
operator|.
name|patchSetAncestors
argument_list|()
operator|.
name|ancestorsOf
argument_list|(
name|currentPatchSetId
argument_list|)
operator|.
name|toList
argument_list|()
control|)
block|{
for|for
control|(
name|PatchSet
name|p
range|:
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|byRevision
argument_list|(
name|a
operator|.
name|getAncestorRevision
argument_list|()
argument_list|)
control|)
block|{
specifier|final
name|Change
operator|.
name|Id
name|ck
init|=
name|p
operator|.
name|getId
argument_list|()
operator|.
name|getParentKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|changesToGet
operator|.
name|add
argument_list|(
name|ck
argument_list|)
condition|)
block|{
name|ancestorOrder
operator|.
name|add
argument_list|(
name|ck
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|final
name|RevId
name|cprev
init|=
name|getCurrentPatchSet
argument_list|()
operator|.
name|getRevision
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|PatchSetAncestor
argument_list|>
name|descendants
init|=
name|cprev
operator|!=
literal|null
condition|?
name|db
operator|.
name|patchSetAncestors
argument_list|()
operator|.
name|descendantsOf
argument_list|(
name|cprev
argument_list|)
operator|.
name|toList
argument_list|()
else|:
name|Collections
operator|.
expr|<
name|PatchSetAncestor
operator|>
name|emptyList
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|PatchSetAncestor
name|a
range|:
name|descendants
control|)
block|{
name|changesToGet
operator|.
name|add
argument_list|(
name|a
operator|.
name|getPatchSet
argument_list|()
operator|.
name|getParentKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Map
argument_list|<
name|Change
operator|.
name|Id
argument_list|,
name|Change
argument_list|>
name|m
init|=
name|db
operator|.
name|changes
argument_list|()
operator|.
name|toMap
argument_list|(
name|db
operator|.
name|changes
argument_list|()
operator|.
name|get
argument_list|(
name|changesToGet
argument_list|)
argument_list|)
decl_stmt|;
name|dependsOn
operator|=
operator|new
name|ArrayList
argument_list|<
name|ChangeInfo
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
specifier|final
name|Change
operator|.
name|Id
name|a
range|:
name|ancestorOrder
control|)
block|{
specifier|final
name|Change
name|ac
init|=
name|m
operator|.
name|get
argument_list|(
name|a
argument_list|)
decl_stmt|;
if|if
condition|(
name|ac
operator|!=
literal|null
condition|)
block|{
name|dependsOn
operator|.
name|add
argument_list|(
operator|new
name|ChangeInfo
argument_list|(
name|ac
argument_list|,
name|acc
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|neededBy
operator|=
operator|new
name|ArrayList
argument_list|<
name|ChangeInfo
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
specifier|final
name|PatchSetAncestor
name|a
range|:
name|descendants
control|)
block|{
specifier|final
name|Change
name|ac
init|=
name|m
operator|.
name|get
argument_list|(
name|a
operator|.
name|getPatchSet
argument_list|()
operator|.
name|getParentKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|ac
operator|!=
literal|null
condition|)
block|{
name|neededBy
operator|.
name|add
argument_list|(
operator|new
name|ChangeInfo
argument_list|(
name|ac
argument_list|,
name|acc
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|neededBy
argument_list|,
operator|new
name|Comparator
argument_list|<
name|ChangeInfo
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
specifier|final
name|ChangeInfo
name|o1
parameter_list|,
specifier|final
name|ChangeInfo
name|o2
parameter_list|)
block|{
return|return
name|o1
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
operator|-
name|o2
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
name|accounts
operator|=
name|acc
operator|.
name|create
argument_list|()
expr_stmt|;
block|}
DECL|method|getAccounts ()
specifier|public
name|AccountInfoCache
name|getAccounts
parameter_list|()
block|{
return|return
name|accounts
return|;
block|}
DECL|method|isAllowsAnonymous ()
specifier|public
name|boolean
name|isAllowsAnonymous
parameter_list|()
block|{
return|return
name|allowsAnonymous
return|;
block|}
DECL|method|canAbandon ()
specifier|public
name|boolean
name|canAbandon
parameter_list|()
block|{
return|return
name|canAbandon
return|;
block|}
DECL|method|getChange ()
specifier|public
name|Change
name|getChange
parameter_list|()
block|{
return|return
name|change
return|;
block|}
DECL|method|getDependsOn ()
specifier|public
name|List
argument_list|<
name|ChangeInfo
argument_list|>
name|getDependsOn
parameter_list|()
block|{
return|return
name|dependsOn
return|;
block|}
DECL|method|getNeededBy ()
specifier|public
name|List
argument_list|<
name|ChangeInfo
argument_list|>
name|getNeededBy
parameter_list|()
block|{
return|return
name|neededBy
return|;
block|}
DECL|method|getMessages ()
specifier|public
name|List
argument_list|<
name|ChangeMessage
argument_list|>
name|getMessages
parameter_list|()
block|{
return|return
name|messages
return|;
block|}
DECL|method|getPatchSets ()
specifier|public
name|List
argument_list|<
name|PatchSet
argument_list|>
name|getPatchSets
parameter_list|()
block|{
return|return
name|patchSets
return|;
block|}
DECL|method|getApprovals ()
specifier|public
name|List
argument_list|<
name|ApprovalDetail
argument_list|>
name|getApprovals
parameter_list|()
block|{
return|return
name|approvals
return|;
block|}
DECL|method|getMissingApprovals ()
specifier|public
name|Set
argument_list|<
name|ApprovalCategory
operator|.
name|Id
argument_list|>
name|getMissingApprovals
parameter_list|()
block|{
return|return
name|missingApprovals
return|;
block|}
DECL|method|getCurrentActions ()
specifier|public
name|Set
argument_list|<
name|ApprovalCategory
operator|.
name|Id
argument_list|>
name|getCurrentActions
parameter_list|()
block|{
return|return
name|currentActions
return|;
block|}
DECL|method|isCurrentPatchSet (final PatchSetDetail detail)
specifier|public
name|boolean
name|isCurrentPatchSet
parameter_list|(
specifier|final
name|PatchSetDetail
name|detail
parameter_list|)
block|{
return|return
name|currentPatchSetId
operator|!=
literal|null
operator|&&
name|detail
operator|.
name|getPatchSet
argument_list|()
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
name|currentPatchSetId
argument_list|)
return|;
block|}
DECL|method|getCurrentPatchSet ()
specifier|public
name|PatchSet
name|getCurrentPatchSet
parameter_list|()
block|{
if|if
condition|(
name|currentPatchSetId
operator|!=
literal|null
condition|)
block|{
comment|// We search through the list backwards because its *very* likely
comment|// that the current patch set is also the last patch set.
comment|//
for|for
control|(
name|int
name|i
init|=
name|patchSets
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
specifier|final
name|PatchSet
name|ps
init|=
name|patchSets
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|ps
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
name|currentPatchSetId
argument_list|)
condition|)
block|{
return|return
name|ps
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|getCurrentPatchSetDetail ()
specifier|public
name|PatchSetDetail
name|getCurrentPatchSetDetail
parameter_list|()
block|{
return|return
name|currentDetail
return|;
block|}
DECL|method|getDescription ()
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|currentDetail
operator|!=
literal|null
condition|?
name|currentDetail
operator|.
name|getInfo
argument_list|()
operator|.
name|getMessage
argument_list|()
else|:
literal|""
return|;
block|}
block|}
end_class

end_unit

