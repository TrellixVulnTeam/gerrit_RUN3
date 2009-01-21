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
name|data
operator|.
name|SideBySidePatchDetail
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
name|UnifiedPatchDetail
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
name|patches
operator|.
name|PatchDetailService
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
name|Patch
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
name|PatchLineComment
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
name|git
operator|.
name|RepositoryCache
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
name|ChangeMail
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
name|ChangeUtil
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
name|GerritJsonServlet
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
name|GerritServer
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
name|OrmRunnable
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

begin_import
import|import
name|javax
operator|.
name|mail
operator|.
name|MessagingException
import|;
end_import

begin_class
DECL|class|PatchDetailServiceImpl
specifier|public
class|class
name|PatchDetailServiceImpl
extends|extends
name|BaseServiceImplementation
implements|implements
name|PatchDetailService
block|{
DECL|field|server
specifier|private
specifier|final
name|GerritServer
name|server
decl_stmt|;
DECL|method|PatchDetailServiceImpl (final GerritServer gs)
specifier|public
name|PatchDetailServiceImpl
parameter_list|(
specifier|final
name|GerritServer
name|gs
parameter_list|)
block|{
name|server
operator|=
name|gs
expr_stmt|;
block|}
DECL|method|sideBySidePatchDetail (final Patch.Key key, final AsyncCallback<SideBySidePatchDetail> callback)
specifier|public
name|void
name|sideBySidePatchDetail
parameter_list|(
specifier|final
name|Patch
operator|.
name|Key
name|key
parameter_list|,
specifier|final
name|AsyncCallback
argument_list|<
name|SideBySidePatchDetail
argument_list|>
name|callback
parameter_list|)
block|{
specifier|final
name|RepositoryCache
name|rc
init|=
name|server
operator|.
name|getRepositoryCache
argument_list|()
decl_stmt|;
if|if
condition|(
name|rc
operator|==
literal|null
condition|)
block|{
name|callback
operator|.
name|onFailure
argument_list|(
operator|new
name|Exception
argument_list|(
literal|"No Repository Cache configured"
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|run
argument_list|(
name|callback
argument_list|,
operator|new
name|SideBySidePatchDetailAction
argument_list|(
name|rc
argument_list|,
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|unifiedPatchDetail (final Patch.Key key, final AsyncCallback<UnifiedPatchDetail> callback)
specifier|public
name|void
name|unifiedPatchDetail
parameter_list|(
specifier|final
name|Patch
operator|.
name|Key
name|key
parameter_list|,
specifier|final
name|AsyncCallback
argument_list|<
name|UnifiedPatchDetail
argument_list|>
name|callback
parameter_list|)
block|{
name|run
argument_list|(
name|callback
argument_list|,
operator|new
name|UnifiedPatchDetailAction
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|myDrafts (final Patch.Key key, final AsyncCallback<List<PatchLineComment>> callback)
specifier|public
name|void
name|myDrafts
parameter_list|(
specifier|final
name|Patch
operator|.
name|Key
name|key
parameter_list|,
specifier|final
name|AsyncCallback
argument_list|<
name|List
argument_list|<
name|PatchLineComment
argument_list|>
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
name|List
argument_list|<
name|PatchLineComment
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|public
name|List
argument_list|<
name|PatchLineComment
argument_list|>
name|run
parameter_list|(
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
block|{
return|return
name|db
operator|.
name|patchComments
argument_list|()
operator|.
name|draft
argument_list|(
name|key
argument_list|,
name|Common
operator|.
name|getAccountId
argument_list|()
argument_list|)
operator|.
name|toList
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|saveDraft (final PatchLineComment comment, final AsyncCallback<PatchLineComment> callback)
specifier|public
name|void
name|saveDraft
parameter_list|(
specifier|final
name|PatchLineComment
name|comment
parameter_list|,
specifier|final
name|AsyncCallback
argument_list|<
name|PatchLineComment
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
name|PatchLineComment
argument_list|>
argument_list|()
block|{
specifier|public
name|PatchLineComment
name|run
parameter_list|(
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
throws|,
name|Failure
block|{
if|if
condition|(
name|comment
operator|.
name|getStatus
argument_list|()
operator|!=
name|PatchLineComment
operator|.
name|Status
operator|.
name|DRAFT
condition|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
operator|new
name|IllegalStateException
argument_list|(
literal|"Comment published"
argument_list|)
argument_list|)
throw|;
block|}
specifier|final
name|Patch
name|patch
init|=
name|db
operator|.
name|patches
argument_list|()
operator|.
name|get
argument_list|(
name|comment
operator|.
name|getKey
argument_list|()
operator|.
name|getParentKey
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Change
name|change
decl_stmt|;
if|if
condition|(
name|patch
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
name|change
operator|=
name|db
operator|.
name|changes
argument_list|()
operator|.
name|get
argument_list|(
name|patch
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
expr_stmt|;
name|assertCanRead
argument_list|(
name|change
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|comment
operator|.
name|getKey
argument_list|()
operator|.
name|get
argument_list|()
operator|==
literal|null
condition|)
block|{
specifier|final
name|PatchLineComment
name|nc
init|=
operator|new
name|PatchLineComment
argument_list|(
operator|new
name|PatchLineComment
operator|.
name|Key
argument_list|(
name|patch
operator|.
name|getKey
argument_list|()
argument_list|,
name|ChangeUtil
operator|.
name|messageUUID
argument_list|(
name|db
argument_list|)
argument_list|)
argument_list|,
name|comment
operator|.
name|getLine
argument_list|()
argument_list|,
name|me
argument_list|)
decl_stmt|;
name|nc
operator|.
name|setSide
argument_list|(
name|comment
operator|.
name|getSide
argument_list|()
argument_list|)
expr_stmt|;
name|nc
operator|.
name|setMessage
argument_list|(
name|comment
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|db
operator|.
name|patchComments
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|nc
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|nc
return|;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|me
operator|.
name|equals
argument_list|(
name|comment
operator|.
name|getAuthor
argument_list|()
argument_list|)
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
name|comment
operator|.
name|updated
argument_list|()
expr_stmt|;
name|db
operator|.
name|patchComments
argument_list|()
operator|.
name|update
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|comment
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|comment
return|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteDraft (final PatchLineComment.Key commentKey, final AsyncCallback<VoidResult> callback)
specifier|public
name|void
name|deleteDraft
parameter_list|(
specifier|final
name|PatchLineComment
operator|.
name|Key
name|commentKey
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
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
throws|,
name|Failure
block|{
specifier|final
name|PatchLineComment
name|comment
init|=
name|db
operator|.
name|patchComments
argument_list|()
operator|.
name|get
argument_list|(
name|commentKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|comment
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
name|Common
operator|.
name|getAccountId
argument_list|()
operator|.
name|equals
argument_list|(
name|comment
operator|.
name|getAuthor
argument_list|()
argument_list|)
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
name|comment
operator|.
name|getStatus
argument_list|()
operator|!=
name|PatchLineComment
operator|.
name|Status
operator|.
name|DRAFT
condition|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
operator|new
name|IllegalStateException
argument_list|(
literal|"Comment published"
argument_list|)
argument_list|)
throw|;
block|}
name|db
operator|.
name|patchComments
argument_list|()
operator|.
name|delete
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|comment
argument_list|)
argument_list|)
expr_stmt|;
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
DECL|method|publishComments (final PatchSet.Id psid, final String message, final Set<ApprovalCategoryValue.Id> approvals, final AsyncCallback<VoidResult> callback)
specifier|public
name|void
name|publishComments
parameter_list|(
specifier|final
name|PatchSet
operator|.
name|Id
name|psid
parameter_list|,
specifier|final
name|String
name|message
parameter_list|,
specifier|final
name|Set
argument_list|<
name|ApprovalCategoryValue
operator|.
name|Id
argument_list|>
name|approvals
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
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
throws|,
name|Failure
block|{
specifier|final
name|PublishResult
name|r
decl_stmt|;
name|r
operator|=
name|db
operator|.
name|run
argument_list|(
operator|new
name|OrmRunnable
argument_list|<
name|PublishResult
argument_list|,
name|ReviewDb
argument_list|>
argument_list|()
block|{
specifier|public
name|PublishResult
name|run
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|Transaction
name|txn
parameter_list|,
name|boolean
name|retry
parameter_list|)
throws|throws
name|OrmException
block|{
return|return
name|doPublishComments
argument_list|(
name|psid
argument_list|,
name|message
argument_list|,
name|approvals
argument_list|,
name|db
argument_list|,
name|txn
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
try|try
block|{
specifier|final
name|ChangeMail
name|cm
init|=
operator|new
name|ChangeMail
argument_list|(
name|server
argument_list|,
name|r
operator|.
name|change
argument_list|)
decl_stmt|;
name|cm
operator|.
name|setFrom
argument_list|(
name|Common
operator|.
name|getAccountId
argument_list|()
argument_list|)
expr_stmt|;
name|cm
operator|.
name|setPatchSet
argument_list|(
name|r
operator|.
name|patchSet
argument_list|,
name|r
operator|.
name|info
argument_list|)
expr_stmt|;
name|cm
operator|.
name|setChangeMessage
argument_list|(
name|r
operator|.
name|message
argument_list|)
expr_stmt|;
name|cm
operator|.
name|setPatchLineComments
argument_list|(
name|r
operator|.
name|comments
argument_list|)
expr_stmt|;
name|cm
operator|.
name|setReviewDb
argument_list|(
name|db
argument_list|)
expr_stmt|;
name|cm
operator|.
name|setHttpServletRequest
argument_list|(
name|GerritJsonServlet
operator|.
name|getCurrentCall
argument_list|()
operator|.
name|getHttpServletRequest
argument_list|()
argument_list|)
expr_stmt|;
name|cm
operator|.
name|sendComment
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessagingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
name|e
argument_list|)
throw|;
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
DECL|class|PublishResult
specifier|private
specifier|static
class|class
name|PublishResult
block|{
DECL|field|change
name|Change
name|change
decl_stmt|;
DECL|field|patchSet
name|PatchSet
name|patchSet
decl_stmt|;
DECL|field|info
name|PatchSetInfo
name|info
decl_stmt|;
DECL|field|message
name|ChangeMessage
name|message
decl_stmt|;
DECL|field|comments
name|List
argument_list|<
name|PatchLineComment
argument_list|>
name|comments
decl_stmt|;
block|}
DECL|method|doPublishComments (final PatchSet.Id psid, final String messageText, final Set<ApprovalCategoryValue.Id> approvals, final ReviewDb db, final Transaction txn)
specifier|private
name|PublishResult
name|doPublishComments
parameter_list|(
specifier|final
name|PatchSet
operator|.
name|Id
name|psid
parameter_list|,
specifier|final
name|String
name|messageText
parameter_list|,
specifier|final
name|Set
argument_list|<
name|ApprovalCategoryValue
operator|.
name|Id
argument_list|>
name|approvals
parameter_list|,
specifier|final
name|ReviewDb
name|db
parameter_list|,
specifier|final
name|Transaction
name|txn
parameter_list|)
throws|throws
name|OrmException
block|{
specifier|final
name|PublishResult
name|r
init|=
operator|new
name|PublishResult
argument_list|()
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
name|r
operator|.
name|change
operator|=
name|db
operator|.
name|changes
argument_list|()
operator|.
name|get
argument_list|(
name|psid
operator|.
name|getParentKey
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|patchSet
operator|=
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|get
argument_list|(
name|psid
argument_list|)
expr_stmt|;
name|r
operator|.
name|info
operator|=
name|db
operator|.
name|patchSetInfo
argument_list|()
operator|.
name|get
argument_list|(
name|psid
argument_list|)
expr_stmt|;
if|if
condition|(
name|r
operator|.
name|change
operator|==
literal|null
operator|||
name|r
operator|.
name|patchSet
operator|==
literal|null
operator|||
name|r
operator|.
name|info
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|OrmException
argument_list|(
operator|new
name|NoSuchEntityException
argument_list|()
argument_list|)
throw|;
block|}
name|r
operator|.
name|comments
operator|=
name|db
operator|.
name|patchComments
argument_list|()
operator|.
name|draft
argument_list|(
name|psid
argument_list|,
name|me
argument_list|)
operator|.
name|toList
argument_list|()
expr_stmt|;
specifier|final
name|Set
argument_list|<
name|Patch
operator|.
name|Key
argument_list|>
name|patchKeys
init|=
operator|new
name|HashSet
argument_list|<
name|Patch
operator|.
name|Key
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|PatchLineComment
name|c
range|:
name|r
operator|.
name|comments
control|)
block|{
name|patchKeys
operator|.
name|add
argument_list|(
name|c
operator|.
name|getKey
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
name|Patch
operator|.
name|Key
argument_list|,
name|Patch
argument_list|>
name|patches
init|=
name|db
operator|.
name|patches
argument_list|()
operator|.
name|toMap
argument_list|(
name|db
operator|.
name|patches
argument_list|()
operator|.
name|get
argument_list|(
name|patchKeys
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|PatchLineComment
name|c
range|:
name|r
operator|.
name|comments
control|)
block|{
specifier|final
name|Patch
name|p
init|=
name|patches
operator|.
name|get
argument_list|(
name|c
operator|.
name|getKey
argument_list|()
operator|.
name|getParentKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|p
operator|.
name|setCommentCount
argument_list|(
name|p
operator|.
name|getCommentCount
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
name|c
operator|.
name|setStatus
argument_list|(
name|PatchLineComment
operator|.
name|Status
operator|.
name|PUBLISHED
argument_list|)
expr_stmt|;
name|c
operator|.
name|updated
argument_list|()
expr_stmt|;
block|}
name|db
operator|.
name|patches
argument_list|()
operator|.
name|update
argument_list|(
name|patches
operator|.
name|values
argument_list|()
argument_list|,
name|txn
argument_list|)
expr_stmt|;
name|db
operator|.
name|patchComments
argument_list|()
operator|.
name|update
argument_list|(
name|r
operator|.
name|comments
argument_list|,
name|txn
argument_list|)
expr_stmt|;
specifier|final
name|StringBuilder
name|msgbuf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|ApprovalCategory
operator|.
name|Id
argument_list|,
name|ApprovalCategoryValue
operator|.
name|Id
argument_list|>
name|values
init|=
operator|new
name|HashMap
argument_list|<
name|ApprovalCategory
operator|.
name|Id
argument_list|,
name|ApprovalCategoryValue
operator|.
name|Id
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|ApprovalCategoryValue
operator|.
name|Id
name|v
range|:
name|approvals
control|)
block|{
name|values
operator|.
name|put
argument_list|(
name|v
operator|.
name|getParentKey
argument_list|()
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
specifier|final
name|boolean
name|open
init|=
name|r
operator|.
name|change
operator|.
name|getStatus
argument_list|()
operator|.
name|isOpen
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|ApprovalCategory
operator|.
name|Id
argument_list|,
name|ChangeApproval
argument_list|>
name|have
init|=
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
decl_stmt|;
for|for
control|(
specifier|final
name|ChangeApproval
name|a
range|:
name|db
operator|.
name|changeApprovals
argument_list|()
operator|.
name|byChangeUser
argument_list|(
name|r
operator|.
name|change
operator|.
name|getId
argument_list|()
argument_list|,
name|me
argument_list|)
control|)
block|{
name|have
operator|.
name|put
argument_list|(
name|a
operator|.
name|getCategoryId
argument_list|()
argument_list|,
name|a
argument_list|)
expr_stmt|;
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
name|getApprovalTypes
argument_list|()
control|)
block|{
specifier|final
name|ApprovalCategoryValue
operator|.
name|Id
name|v
init|=
name|values
operator|.
name|get
argument_list|(
name|at
operator|.
name|getCategory
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
specifier|final
name|ApprovalCategoryValue
name|val
init|=
name|at
operator|.
name|getValue
argument_list|(
name|v
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|ChangeApproval
name|mycatpp
init|=
name|have
operator|.
name|remove
argument_list|(
name|v
operator|.
name|getParentKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|mycatpp
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|msgbuf
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|msgbuf
operator|.
name|append
argument_list|(
literal|"; "
argument_list|)
expr_stmt|;
block|}
name|msgbuf
operator|.
name|append
argument_list|(
name|val
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|open
condition|)
block|{
name|mycatpp
operator|=
operator|new
name|ChangeApproval
argument_list|(
operator|new
name|ChangeApproval
operator|.
name|Key
argument_list|(
name|r
operator|.
name|change
operator|.
name|getId
argument_list|()
argument_list|,
name|me
argument_list|,
name|v
operator|.
name|getParentKey
argument_list|()
argument_list|)
argument_list|,
name|v
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
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
name|mycatpp
argument_list|)
argument_list|,
name|txn
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|mycatpp
operator|.
name|getValue
argument_list|()
operator|!=
name|v
operator|.
name|get
argument_list|()
condition|)
block|{
if|if
condition|(
name|msgbuf
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|msgbuf
operator|.
name|append
argument_list|(
literal|"; "
argument_list|)
expr_stmt|;
block|}
name|msgbuf
operator|.
name|append
argument_list|(
name|val
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|open
condition|)
block|{
name|mycatpp
operator|.
name|setValue
argument_list|(
name|v
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|mycatpp
operator|.
name|setGranted
argument_list|()
expr_stmt|;
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
name|mycatpp
argument_list|)
argument_list|,
name|txn
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|open
condition|)
block|{
name|db
operator|.
name|changeApprovals
argument_list|()
operator|.
name|delete
argument_list|(
name|have
operator|.
name|values
argument_list|()
argument_list|,
name|txn
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|msgbuf
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|msgbuf
operator|.
name|insert
argument_list|(
literal|0
argument_list|,
literal|"Patch Set "
operator|+
name|psid
operator|.
name|get
argument_list|()
operator|+
literal|": "
argument_list|)
expr_stmt|;
name|msgbuf
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|messageText
operator|!=
literal|null
condition|)
block|{
name|msgbuf
operator|.
name|append
argument_list|(
name|messageText
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|msgbuf
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|r
operator|.
name|message
operator|=
operator|new
name|ChangeMessage
argument_list|(
operator|new
name|ChangeMessage
operator|.
name|Key
argument_list|(
name|r
operator|.
name|change
operator|.
name|getId
argument_list|()
argument_list|,
name|ChangeUtil
operator|.
name|messageUUID
argument_list|(
name|db
argument_list|)
argument_list|)
argument_list|,
name|me
argument_list|)
expr_stmt|;
name|r
operator|.
name|message
operator|.
name|setMessage
argument_list|(
name|msgbuf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|db
operator|.
name|changeMessages
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|r
operator|.
name|message
argument_list|)
argument_list|,
name|txn
argument_list|)
expr_stmt|;
block|}
name|ChangeUtil
operator|.
name|updated
argument_list|(
name|r
operator|.
name|change
argument_list|)
expr_stmt|;
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
name|r
operator|.
name|change
argument_list|)
argument_list|,
name|txn
argument_list|)
expr_stmt|;
return|return
name|r
return|;
block|}
block|}
end_class

end_unit

