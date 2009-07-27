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
name|AccountGroup
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
name|sql
operator|.
name|Timestamp
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
decl_stmt|;
DECL|field|EG_0
specifier|static
specifier|final
name|Timestamp
name|EG_0
init|=
operator|new
name|Timestamp
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|EG_D
specifier|static
specifier|final
name|Timestamp
name|EG_D
init|=
operator|new
name|Timestamp
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
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
name|ChangeApproval
argument_list|>
name|approvals
decl_stmt|;
DECL|field|hasNonZero
specifier|private
specifier|transient
name|int
name|hasNonZero
decl_stmt|;
DECL|field|sortOrder
specifier|private
specifier|transient
name|Timestamp
name|sortOrder
init|=
name|EG_D
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
name|ChangeApproval
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
DECL|method|getApprovalMap ()
specifier|public
name|Map
argument_list|<
name|ApprovalCategory
operator|.
name|Id
argument_list|,
name|ChangeApproval
argument_list|>
name|getApprovalMap
parameter_list|()
block|{
specifier|final
name|HashMap
argument_list|<
name|ApprovalCategory
operator|.
name|Id
argument_list|,
name|ChangeApproval
argument_list|>
name|r
decl_stmt|;
name|r
operator|=
operator|new
name|HashMap
argument_list|<
name|ApprovalCategory
operator|.
name|Id
argument_list|,
name|ChangeApproval
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
specifier|final
name|ChangeApproval
name|ca
range|:
name|approvals
control|)
block|{
name|r
operator|.
name|put
argument_list|(
name|ca
operator|.
name|getCategoryId
argument_list|()
argument_list|,
name|ca
argument_list|)
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
DECL|method|sortFirst ()
specifier|public
name|void
name|sortFirst
parameter_list|()
block|{
name|hasNonZero
operator|=
literal|1
expr_stmt|;
name|sortOrder
operator|=
name|ApprovalDetail
operator|.
name|EG_0
expr_stmt|;
block|}
DECL|method|add (final ChangeApproval ca)
specifier|public
name|void
name|add
parameter_list|(
specifier|final
name|ChangeApproval
name|ca
parameter_list|)
block|{
name|approvals
operator|.
name|add
argument_list|(
name|ca
argument_list|)
expr_stmt|;
specifier|final
name|Timestamp
name|g
init|=
name|ca
operator|.
name|getGranted
argument_list|()
decl_stmt|;
if|if
condition|(
name|g
operator|!=
literal|null
operator|&&
name|g
operator|.
name|compareTo
argument_list|(
name|sortOrder
argument_list|)
operator|<
literal|0
condition|)
block|{
name|sortOrder
operator|=
name|g
expr_stmt|;
block|}
if|if
condition|(
name|ca
operator|.
name|getValue
argument_list|()
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
DECL|method|applyProjectRights (final GroupCache groupCache, final Map<ApprovalCategory.Id, Collection<ProjectRight>> rights)
name|void
name|applyProjectRights
parameter_list|(
specifier|final
name|GroupCache
name|groupCache
parameter_list|,
specifier|final
name|Map
argument_list|<
name|ApprovalCategory
operator|.
name|Id
argument_list|,
name|Collection
argument_list|<
name|ProjectRight
argument_list|>
argument_list|>
name|rights
parameter_list|)
block|{
specifier|final
name|Set
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
name|groups
init|=
name|groupCache
operator|.
name|getEffectiveGroups
argument_list|(
name|account
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|ChangeApproval
name|a
range|:
name|approvals
control|)
block|{
name|Collection
argument_list|<
name|ProjectRight
argument_list|>
name|l
init|=
name|rights
operator|.
name|get
argument_list|(
name|a
operator|.
name|getCategoryId
argument_list|()
argument_list|)
decl_stmt|;
name|short
name|min
init|=
literal|0
decl_stmt|,
name|max
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|l
operator|!=
literal|null
condition|)
block|{
for|for
control|(
specifier|final
name|ProjectRight
name|r
range|:
name|l
control|)
block|{
if|if
condition|(
name|groups
operator|.
name|contains
argument_list|(
name|r
operator|.
name|getAccountGroupId
argument_list|()
argument_list|)
condition|)
block|{
name|min
operator|=
operator|(
name|short
operator|)
name|Math
operator|.
name|min
argument_list|(
name|min
argument_list|,
name|r
operator|.
name|getMinValue
argument_list|()
argument_list|)
expr_stmt|;
name|max
operator|=
operator|(
name|short
operator|)
name|Math
operator|.
name|max
argument_list|(
name|max
argument_list|,
name|r
operator|.
name|getMaxValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|a
operator|.
name|getValue
argument_list|()
operator|<
name|min
condition|)
block|{
name|a
operator|.
name|setValue
argument_list|(
name|min
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|a
operator|.
name|getValue
argument_list|()
operator|>
name|max
condition|)
block|{
name|a
operator|.
name|setValue
argument_list|(
name|max
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

