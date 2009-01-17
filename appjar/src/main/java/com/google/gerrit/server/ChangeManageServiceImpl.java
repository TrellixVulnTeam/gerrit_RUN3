begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright 2009 Google Inc.
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
name|gerrit
operator|.
name|client
operator|.
name|changes
operator|.
name|ChangeManageService
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
name|ApprovalCategoryValue
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
name|BaseServiceImplementation
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
name|rpc
operator|.
name|NoSuchEntityException
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
name|gerrit
operator|.
name|git
operator|.
name|MergeQueue
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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtjsonrpc
operator|.
name|client
operator|.
name|VoidResult
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
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|client
operator|.
name|Transaction
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
name|List
import|;
end_import

begin_class
DECL|class|ChangeManageServiceImpl
specifier|public
class|class
name|ChangeManageServiceImpl
extends|extends
name|BaseServiceImplementation
implements|implements
name|ChangeManageService
block|{
DECL|method|patchSetAction (final ApprovalCategoryValue.Id value, final PatchSet.Id patchSetId, final AsyncCallback<VoidResult> callback)
specifier|public
name|void
name|patchSetAction
parameter_list|(
specifier|final
name|ApprovalCategoryValue
operator|.
name|Id
name|value
parameter_list|,
specifier|final
name|PatchSet
operator|.
name|Id
name|patchSetId
parameter_list|,
specifier|final
name|AsyncCallback
argument_list|<
name|VoidResult
argument_list|>
name|callback
parameter_list|)
block|{
name|run
argument_list|(
name|callback
argument_list|,
operator|new
name|Action
argument_list|<
name|VoidResult
argument_list|>
argument_list|()
block|{
specifier|public
name|VoidResult
name|run
parameter_list|(
specifier|final
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
throws|,
name|Failure
block|{
specifier|final
name|Change
name|change
init|=
name|db
operator|.
name|changes
argument_list|()
operator|.
name|get
argument_list|(
name|patchSetId
operator|.
name|getParentKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|change
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
operator|new
name|NoSuchEntityException
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|patchSetId
operator|.
name|equals
argument_list|(
name|change
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
operator|new
name|IllegalStateException
argument_list|(
literal|"Patch set "
operator|+
name|patchSetId
operator|+
literal|" not current"
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|change
operator|.
name|getStatus
argument_list|()
operator|.
name|isClosed
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
operator|new
name|IllegalStateException
argument_list|(
literal|"Change"
operator|+
name|change
operator|.
name|getId
argument_list|()
operator|+
literal|" is closed"
argument_list|)
argument_list|)
throw|;
block|}
specifier|final
name|List
argument_list|<
name|ChangeApproval
argument_list|>
name|allApprovals
init|=
operator|new
name|ArrayList
argument_list|<
name|ChangeApproval
argument_list|>
argument_list|(
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
argument_list|)
decl_stmt|;
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
name|ChangeApproval
operator|.
name|Key
name|ak
init|=
operator|new
name|ChangeApproval
operator|.
name|Key
argument_list|(
name|change
operator|.
name|getId
argument_list|()
argument_list|,
name|me
argument_list|,
name|value
operator|.
name|getParentKey
argument_list|()
argument_list|)
decl_stmt|;
name|ChangeApproval
name|myAction
init|=
literal|null
decl_stmt|;
name|boolean
name|isnew
init|=
literal|true
decl_stmt|;
for|for
control|(
specifier|final
name|ChangeApproval
name|ca
range|:
name|allApprovals
control|)
block|{
if|if
condition|(
name|ak
operator|.
name|equals
argument_list|(
name|ca
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|isnew
operator|=
literal|false
expr_stmt|;
name|myAction
operator|=
name|ca
expr_stmt|;
name|myAction
operator|.
name|setValue
argument_list|(
name|value
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|myAction
operator|.
name|setGranted
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|myAction
operator|==
literal|null
condition|)
block|{
name|myAction
operator|=
operator|new
name|ChangeApproval
argument_list|(
name|ak
argument_list|,
name|value
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|allApprovals
operator|.
name|add
argument_list|(
name|myAction
argument_list|)
expr_stmt|;
block|}
specifier|final
name|ApprovalType
name|actionType
init|=
name|Common
operator|.
name|getGerritConfig
argument_list|()
operator|.
name|getApprovalType
argument_list|(
name|myAction
operator|.
name|getCategoryId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|actionType
operator|==
literal|null
operator|||
operator|!
name|actionType
operator|.
name|getCategory
argument_list|()
operator|.
name|isAction
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
operator|new
name|IllegalArgumentException
argument_list|(
name|actionType
operator|.
name|getCategory
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" not an action"
argument_list|)
argument_list|)
throw|;
block|}
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
for|for
control|(
name|ApprovalType
name|c
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
name|c
operator|.
name|getCategory
argument_list|()
operator|.
name|getFunction
argument_list|()
operator|.
name|run
argument_list|(
name|c
argument_list|,
name|fs
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|actionType
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
name|actionType
argument_list|,
name|fs
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
operator|new
name|IllegalStateException
argument_list|(
name|actionType
operator|.
name|getCategory
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" not permitted"
argument_list|)
argument_list|)
throw|;
block|}
name|fs
operator|.
name|normalize
argument_list|(
name|actionType
argument_list|,
name|myAction
argument_list|)
expr_stmt|;
if|if
condition|(
name|myAction
operator|.
name|getValue
argument_list|()
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
operator|new
name|IllegalStateException
argument_list|(
name|actionType
operator|.
name|getCategory
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" not permitted"
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|ApprovalCategory
operator|.
name|SUBMIT
operator|.
name|equals
argument_list|(
name|actionType
operator|.
name|getCategory
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|change
operator|.
name|getStatus
argument_list|()
operator|==
name|Change
operator|.
name|Status
operator|.
name|NEW
condition|)
block|{
name|change
operator|.
name|setStatus
argument_list|(
name|Change
operator|.
name|Status
operator|.
name|SUBMITTED
argument_list|)
expr_stmt|;
name|ChangeUtil
operator|.
name|updated
argument_list|(
name|change
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|Failure
argument_list|(
operator|new
name|IllegalArgumentException
argument_list|(
name|actionType
operator|.
name|getCategory
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" cannot be perfomed by Gerrit"
argument_list|)
argument_list|)
throw|;
block|}
specifier|final
name|Transaction
name|txn
init|=
name|db
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
name|db
operator|.
name|changes
argument_list|()
operator|.
name|update
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|change
argument_list|)
argument_list|,
name|txn
argument_list|)
expr_stmt|;
if|if
condition|(
name|change
operator|.
name|getStatus
argument_list|()
operator|.
name|isClosed
argument_list|()
condition|)
block|{
name|db
operator|.
name|changeApprovals
argument_list|()
operator|.
name|update
argument_list|(
name|fs
operator|.
name|getDirtyChangeApprovals
argument_list|()
argument_list|,
name|txn
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isnew
condition|)
block|{
name|db
operator|.
name|changeApprovals
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|myAction
argument_list|)
argument_list|,
name|txn
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|db
operator|.
name|changeApprovals
argument_list|()
operator|.
name|update
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|myAction
argument_list|)
argument_list|,
name|txn
argument_list|)
expr_stmt|;
block|}
name|txn
operator|.
name|commit
argument_list|()
expr_stmt|;
if|if
condition|(
name|change
operator|.
name|getStatus
argument_list|()
operator|==
name|Change
operator|.
name|Status
operator|.
name|SUBMITTED
condition|)
block|{
name|MergeQueue
operator|.
name|merge
argument_list|(
name|change
operator|.
name|getDest
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|VoidResult
operator|.
name|INSTANCE
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

