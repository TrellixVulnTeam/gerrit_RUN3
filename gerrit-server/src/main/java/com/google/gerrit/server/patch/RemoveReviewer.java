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
DECL|package|com.google.gerrit.server.patch
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|patch
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
name|ReviewerResult
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
name|server
operator|.
name|account
operator|.
name|AccountCache
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
name|account
operator|.
name|AccountState
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
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
import|;
end_import

begin_class
DECL|class|RemoveReviewer
specifier|public
class|class
name|RemoveReviewer
implements|implements
name|Callable
argument_list|<
name|ReviewerResult
argument_list|>
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RemoveReviewer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (Change.Id changeId, Set<Account.Id> reviewerId)
name|RemoveReviewer
name|create
parameter_list|(
name|Change
operator|.
name|Id
name|changeId
parameter_list|,
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|reviewerId
parameter_list|)
function_decl|;
block|}
DECL|field|changeControlFactory
specifier|private
specifier|final
name|ChangeControl
operator|.
name|Factory
name|changeControlFactory
decl_stmt|;
DECL|field|db
specifier|private
specifier|final
name|ReviewDb
name|db
decl_stmt|;
DECL|field|accountCache
specifier|private
specifier|final
name|AccountCache
name|accountCache
decl_stmt|;
DECL|field|changeId
specifier|private
specifier|final
name|Change
operator|.
name|Id
name|changeId
decl_stmt|;
DECL|field|ids
specifier|private
specifier|final
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|ids
decl_stmt|;
annotation|@
name|Inject
DECL|method|RemoveReviewer (ReviewDb db, ChangeControl.Factory changeControlFactory, AccountCache accountCache, @Assisted Change.Id changeId, @Assisted Set<Account.Id> ids)
name|RemoveReviewer
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|ChangeControl
operator|.
name|Factory
name|changeControlFactory
parameter_list|,
name|AccountCache
name|accountCache
parameter_list|,
annotation|@
name|Assisted
name|Change
operator|.
name|Id
name|changeId
parameter_list|,
annotation|@
name|Assisted
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|ids
parameter_list|)
block|{
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|changeControlFactory
operator|=
name|changeControlFactory
expr_stmt|;
name|this
operator|.
name|accountCache
operator|=
name|accountCache
expr_stmt|;
name|this
operator|.
name|changeId
operator|=
name|changeId
expr_stmt|;
name|this
operator|.
name|ids
operator|=
name|ids
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|ReviewerResult
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|ReviewerResult
name|result
init|=
operator|new
name|ReviewerResult
argument_list|()
decl_stmt|;
name|ChangeControl
name|ctl
init|=
name|changeControlFactory
operator|.
name|validateFor
argument_list|(
name|changeId
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|rejected
init|=
operator|new
name|HashSet
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|PatchSetApproval
argument_list|>
name|current
init|=
name|db
operator|.
name|patchSetApprovals
argument_list|()
operator|.
name|byChange
argument_list|(
name|changeId
argument_list|)
operator|.
name|toList
argument_list|()
decl_stmt|;
for|for
control|(
name|PatchSetApproval
name|psa
range|:
name|current
control|)
block|{
name|Account
operator|.
name|Id
name|who
init|=
name|psa
operator|.
name|getAccountId
argument_list|()
decl_stmt|;
if|if
condition|(
name|ids
operator|.
name|contains
argument_list|(
name|who
argument_list|)
operator|&&
operator|!
name|ctl
operator|.
name|canRemoveReviewer
argument_list|(
name|psa
argument_list|)
operator|&&
name|rejected
operator|.
name|add
argument_list|(
name|who
argument_list|)
condition|)
block|{
name|result
operator|.
name|addError
argument_list|(
operator|new
name|ReviewerResult
operator|.
name|Error
argument_list|(
name|ReviewerResult
operator|.
name|Error
operator|.
name|Type
operator|.
name|REMOVE_NOT_PERMITTED
argument_list|,
name|formatUser
argument_list|(
name|who
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|PatchSetApproval
argument_list|>
name|toDelete
init|=
operator|new
name|ArrayList
argument_list|<
name|PatchSetApproval
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|PatchSetApproval
name|psa
range|:
name|current
control|)
block|{
name|Account
operator|.
name|Id
name|who
init|=
name|psa
operator|.
name|getAccountId
argument_list|()
decl_stmt|;
if|if
condition|(
name|ids
operator|.
name|contains
argument_list|(
name|who
argument_list|)
operator|&&
operator|!
name|rejected
operator|.
name|contains
argument_list|(
name|who
argument_list|)
condition|)
block|{
name|toDelete
operator|.
name|add
argument_list|(
name|psa
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
name|db
operator|.
name|patchSetApprovals
argument_list|()
operator|.
name|delete
argument_list|(
name|toDelete
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|err
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Cannot remove reviewers from change "
operator|+
name|changeId
argument_list|,
name|err
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|failed
init|=
operator|new
name|HashSet
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|PatchSetApproval
name|psa
range|:
name|toDelete
control|)
block|{
name|failed
operator|.
name|add
argument_list|(
name|psa
operator|.
name|getAccountId
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Account
operator|.
name|Id
name|who
range|:
name|failed
control|)
block|{
name|result
operator|.
name|addError
argument_list|(
operator|new
name|ReviewerResult
operator|.
name|Error
argument_list|(
name|ReviewerResult
operator|.
name|Error
operator|.
name|Type
operator|.
name|COULD_NOT_REMOVE
argument_list|,
name|formatUser
argument_list|(
name|who
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
DECL|method|formatUser (Account.Id who)
specifier|private
name|String
name|formatUser
parameter_list|(
name|Account
operator|.
name|Id
name|who
parameter_list|)
block|{
name|AccountState
name|state
init|=
name|accountCache
operator|.
name|get
argument_list|(
name|who
argument_list|)
decl_stmt|;
if|if
condition|(
name|state
operator|!=
literal|null
condition|)
block|{
return|return
name|formatUser
argument_list|(
name|state
operator|.
name|getAccount
argument_list|()
argument_list|,
name|who
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|who
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
DECL|method|formatUser (Account a, Object fallback)
specifier|static
name|String
name|formatUser
parameter_list|(
name|Account
name|a
parameter_list|,
name|Object
name|fallback
parameter_list|)
block|{
if|if
condition|(
name|a
operator|.
name|getFullName
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|a
operator|.
name|getFullName
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|a
operator|.
name|getFullName
argument_list|()
return|;
block|}
if|if
condition|(
name|a
operator|.
name|getPreferredEmail
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|a
operator|.
name|getPreferredEmail
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|a
operator|.
name|getPreferredEmail
argument_list|()
return|;
block|}
if|if
condition|(
name|a
operator|.
name|getUserName
argument_list|()
operator|!=
literal|null
operator|&&
name|a
operator|.
name|getUserName
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|a
operator|.
name|getUserName
argument_list|()
return|;
block|}
return|return
name|fallback
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

