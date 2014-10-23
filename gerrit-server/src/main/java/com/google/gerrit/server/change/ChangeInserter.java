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
DECL|package|com.google.gerrit.server.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|change
package|;
end_package

begin_import
import|import static
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
operator|.
name|INITIAL_PATCH_SET_ID
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
name|util
operator|.
name|concurrent
operator|.
name|CheckedFuture
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
name|ChangeHooks
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
name|extensions
operator|.
name|api
operator|.
name|changes
operator|.
name|HashtagsInput
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
name|client
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
name|gerrit
operator|.
name|server
operator|.
name|ApprovalsUtil
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
name|ChangeMessagesUtil
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
name|auth
operator|.
name|AuthException
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
name|extensions
operator|.
name|events
operator|.
name|GitReferenceUpdated
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
name|index
operator|.
name|ChangeIndexer
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
name|mail
operator|.
name|CreateChangeSender
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
name|ChangeUpdate
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
name|patch
operator|.
name|PatchSetInfoFactory
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
name|RefControl
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
name|validators
operator|.
name|ValidationException
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Provider
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
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|ObjectId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|revwalk
operator|.
name|RevCommit
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
name|io
operator|.
name|IOException
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
DECL|class|ChangeInserter
specifier|public
class|class
name|ChangeInserter
block|{
DECL|interface|Factory
specifier|public
specifier|static
interface|interface
name|Factory
block|{
DECL|method|create (RefControl ctl, Change c, RevCommit rc)
name|ChangeInserter
name|create
parameter_list|(
name|RefControl
name|ctl
parameter_list|,
name|Change
name|c
parameter_list|,
name|RevCommit
name|rc
parameter_list|)
function_decl|;
block|}
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
name|ChangeInserter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|dbProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|dbProvider
decl_stmt|;
DECL|field|updateFactory
specifier|private
specifier|final
name|ChangeUpdate
operator|.
name|Factory
name|updateFactory
decl_stmt|;
DECL|field|gitRefUpdated
specifier|private
specifier|final
name|GitReferenceUpdated
name|gitRefUpdated
decl_stmt|;
DECL|field|hooks
specifier|private
specifier|final
name|ChangeHooks
name|hooks
decl_stmt|;
DECL|field|approvalsUtil
specifier|private
specifier|final
name|ApprovalsUtil
name|approvalsUtil
decl_stmt|;
DECL|field|cmUtil
specifier|private
specifier|final
name|ChangeMessagesUtil
name|cmUtil
decl_stmt|;
DECL|field|indexer
specifier|private
specifier|final
name|ChangeIndexer
name|indexer
decl_stmt|;
DECL|field|createChangeSenderFactory
specifier|private
specifier|final
name|CreateChangeSender
operator|.
name|Factory
name|createChangeSenderFactory
decl_stmt|;
DECL|field|hashtagsUtil
specifier|private
specifier|final
name|HashtagsUtil
name|hashtagsUtil
decl_stmt|;
DECL|field|accountCache
specifier|private
specifier|final
name|AccountCache
name|accountCache
decl_stmt|;
DECL|field|refControl
specifier|private
specifier|final
name|RefControl
name|refControl
decl_stmt|;
DECL|field|change
specifier|private
specifier|final
name|Change
name|change
decl_stmt|;
DECL|field|patchSet
specifier|private
specifier|final
name|PatchSet
name|patchSet
decl_stmt|;
DECL|field|commit
specifier|private
specifier|final
name|RevCommit
name|commit
decl_stmt|;
DECL|field|patchSetInfo
specifier|private
specifier|final
name|PatchSetInfo
name|patchSetInfo
decl_stmt|;
DECL|field|changeMessage
specifier|private
name|ChangeMessage
name|changeMessage
decl_stmt|;
DECL|field|reviewers
specifier|private
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|reviewers
decl_stmt|;
DECL|field|extraCC
specifier|private
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|extraCC
decl_stmt|;
DECL|field|approvals
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Short
argument_list|>
name|approvals
decl_stmt|;
DECL|field|hashtags
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|hashtags
decl_stmt|;
DECL|field|runHooks
specifier|private
name|boolean
name|runHooks
decl_stmt|;
DECL|field|sendMail
specifier|private
name|boolean
name|sendMail
decl_stmt|;
annotation|@
name|Inject
DECL|method|ChangeInserter (Provider<ReviewDb> dbProvider, ChangeUpdate.Factory updateFactory, PatchSetInfoFactory patchSetInfoFactory, GitReferenceUpdated gitRefUpdated, ChangeHooks hooks, ApprovalsUtil approvalsUtil, ChangeMessagesUtil cmUtil, ChangeIndexer indexer, CreateChangeSender.Factory createChangeSenderFactory, HashtagsUtil hashtagsUtil, AccountCache accountCache, @Assisted RefControl refControl, @Assisted Change change, @Assisted RevCommit commit)
name|ChangeInserter
parameter_list|(
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|dbProvider
parameter_list|,
name|ChangeUpdate
operator|.
name|Factory
name|updateFactory
parameter_list|,
name|PatchSetInfoFactory
name|patchSetInfoFactory
parameter_list|,
name|GitReferenceUpdated
name|gitRefUpdated
parameter_list|,
name|ChangeHooks
name|hooks
parameter_list|,
name|ApprovalsUtil
name|approvalsUtil
parameter_list|,
name|ChangeMessagesUtil
name|cmUtil
parameter_list|,
name|ChangeIndexer
name|indexer
parameter_list|,
name|CreateChangeSender
operator|.
name|Factory
name|createChangeSenderFactory
parameter_list|,
name|HashtagsUtil
name|hashtagsUtil
parameter_list|,
name|AccountCache
name|accountCache
parameter_list|,
annotation|@
name|Assisted
name|RefControl
name|refControl
parameter_list|,
annotation|@
name|Assisted
name|Change
name|change
parameter_list|,
annotation|@
name|Assisted
name|RevCommit
name|commit
parameter_list|)
block|{
name|this
operator|.
name|dbProvider
operator|=
name|dbProvider
expr_stmt|;
name|this
operator|.
name|updateFactory
operator|=
name|updateFactory
expr_stmt|;
name|this
operator|.
name|gitRefUpdated
operator|=
name|gitRefUpdated
expr_stmt|;
name|this
operator|.
name|hooks
operator|=
name|hooks
expr_stmt|;
name|this
operator|.
name|approvalsUtil
operator|=
name|approvalsUtil
expr_stmt|;
name|this
operator|.
name|cmUtil
operator|=
name|cmUtil
expr_stmt|;
name|this
operator|.
name|indexer
operator|=
name|indexer
expr_stmt|;
name|this
operator|.
name|createChangeSenderFactory
operator|=
name|createChangeSenderFactory
expr_stmt|;
name|this
operator|.
name|hashtagsUtil
operator|=
name|hashtagsUtil
expr_stmt|;
name|this
operator|.
name|accountCache
operator|=
name|accountCache
expr_stmt|;
name|this
operator|.
name|refControl
operator|=
name|refControl
expr_stmt|;
name|this
operator|.
name|change
operator|=
name|change
expr_stmt|;
name|this
operator|.
name|commit
operator|=
name|commit
expr_stmt|;
name|this
operator|.
name|reviewers
operator|=
name|Collections
operator|.
name|emptySet
argument_list|()
expr_stmt|;
name|this
operator|.
name|extraCC
operator|=
name|Collections
operator|.
name|emptySet
argument_list|()
expr_stmt|;
name|this
operator|.
name|approvals
operator|=
name|Collections
operator|.
name|emptyMap
argument_list|()
expr_stmt|;
name|this
operator|.
name|hashtags
operator|=
name|Collections
operator|.
name|emptySet
argument_list|()
expr_stmt|;
name|this
operator|.
name|runHooks
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|sendMail
operator|=
literal|true
expr_stmt|;
name|patchSet
operator|=
operator|new
name|PatchSet
argument_list|(
operator|new
name|PatchSet
operator|.
name|Id
argument_list|(
name|change
operator|.
name|getId
argument_list|()
argument_list|,
name|INITIAL_PATCH_SET_ID
argument_list|)
argument_list|)
expr_stmt|;
name|patchSet
operator|.
name|setCreatedOn
argument_list|(
name|change
operator|.
name|getCreatedOn
argument_list|()
argument_list|)
expr_stmt|;
name|patchSet
operator|.
name|setUploader
argument_list|(
name|change
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|patchSet
operator|.
name|setRevision
argument_list|(
operator|new
name|RevId
argument_list|(
name|commit
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|patchSetInfo
operator|=
name|patchSetInfoFactory
operator|.
name|get
argument_list|(
name|commit
argument_list|,
name|patchSet
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|change
operator|.
name|setCurrentPatchSet
argument_list|(
name|patchSetInfo
argument_list|)
expr_stmt|;
name|ChangeUtil
operator|.
name|computeSortKey
argument_list|(
name|change
argument_list|)
expr_stmt|;
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
DECL|method|setMessage (ChangeMessage changeMessage)
specifier|public
name|ChangeInserter
name|setMessage
parameter_list|(
name|ChangeMessage
name|changeMessage
parameter_list|)
block|{
name|this
operator|.
name|changeMessage
operator|=
name|changeMessage
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setReviewers (Set<Account.Id> reviewers)
specifier|public
name|ChangeInserter
name|setReviewers
parameter_list|(
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|reviewers
parameter_list|)
block|{
name|this
operator|.
name|reviewers
operator|=
name|reviewers
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setExtraCC (Set<Account.Id> extraCC)
specifier|public
name|ChangeInserter
name|setExtraCC
parameter_list|(
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|extraCC
parameter_list|)
block|{
name|this
operator|.
name|extraCC
operator|=
name|extraCC
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setDraft (boolean draft)
specifier|public
name|ChangeInserter
name|setDraft
parameter_list|(
name|boolean
name|draft
parameter_list|)
block|{
name|change
operator|.
name|setStatus
argument_list|(
name|draft
condition|?
name|Change
operator|.
name|Status
operator|.
name|DRAFT
else|:
name|Change
operator|.
name|Status
operator|.
name|NEW
argument_list|)
expr_stmt|;
name|patchSet
operator|.
name|setDraft
argument_list|(
name|draft
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setHashtags (Set<String> hashtags)
specifier|public
name|ChangeInserter
name|setHashtags
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|hashtags
parameter_list|)
block|{
name|this
operator|.
name|hashtags
operator|=
name|hashtags
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setRunHooks (boolean runHooks)
specifier|public
name|ChangeInserter
name|setRunHooks
parameter_list|(
name|boolean
name|runHooks
parameter_list|)
block|{
name|this
operator|.
name|runHooks
operator|=
name|runHooks
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setSendMail (boolean sendMail)
specifier|public
name|ChangeInserter
name|setSendMail
parameter_list|(
name|boolean
name|sendMail
parameter_list|)
block|{
name|this
operator|.
name|sendMail
operator|=
name|sendMail
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getPatchSet ()
specifier|public
name|PatchSet
name|getPatchSet
parameter_list|()
block|{
return|return
name|patchSet
return|;
block|}
DECL|method|setApprovals (Map<String, Short> approvals)
specifier|public
name|ChangeInserter
name|setApprovals
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Short
argument_list|>
name|approvals
parameter_list|)
block|{
name|this
operator|.
name|approvals
operator|=
name|approvals
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getPatchSetInfo ()
specifier|public
name|PatchSetInfo
name|getPatchSetInfo
parameter_list|()
block|{
return|return
name|patchSetInfo
return|;
block|}
DECL|method|insert ()
specifier|public
name|Change
name|insert
parameter_list|()
throws|throws
name|OrmException
throws|,
name|IOException
block|{
name|ReviewDb
name|db
init|=
name|dbProvider
operator|.
name|get
argument_list|()
decl_stmt|;
name|ChangeControl
name|ctl
init|=
name|refControl
operator|.
name|getProjectControl
argument_list|()
operator|.
name|controlFor
argument_list|(
name|change
argument_list|)
decl_stmt|;
name|ChangeUpdate
name|update
init|=
name|updateFactory
operator|.
name|create
argument_list|(
name|ctl
argument_list|,
name|change
operator|.
name|getCreatedOn
argument_list|()
argument_list|)
decl_stmt|;
name|db
operator|.
name|changes
argument_list|()
operator|.
name|beginTransaction
argument_list|(
name|change
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|ChangeUtil
operator|.
name|insertAncestors
argument_list|(
name|db
argument_list|,
name|patchSet
operator|.
name|getId
argument_list|()
argument_list|,
name|commit
argument_list|)
expr_stmt|;
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|patchSet
argument_list|)
argument_list|)
expr_stmt|;
name|db
operator|.
name|changes
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|change
argument_list|)
argument_list|)
expr_stmt|;
name|LabelTypes
name|labelTypes
init|=
name|refControl
operator|.
name|getProjectControl
argument_list|()
operator|.
name|getLabelTypes
argument_list|()
decl_stmt|;
name|approvalsUtil
operator|.
name|addReviewers
argument_list|(
name|db
argument_list|,
name|update
argument_list|,
name|labelTypes
argument_list|,
name|change
argument_list|,
name|patchSet
argument_list|,
name|patchSetInfo
argument_list|,
name|reviewers
argument_list|,
name|Collections
operator|.
expr|<
name|Account
operator|.
name|Id
operator|>
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
name|approvalsUtil
operator|.
name|addApprovals
argument_list|(
name|db
argument_list|,
name|update
argument_list|,
name|labelTypes
argument_list|,
name|patchSet
argument_list|,
name|patchSetInfo
argument_list|,
name|ctl
argument_list|,
name|approvals
argument_list|)
expr_stmt|;
if|if
condition|(
name|messageIsForChange
argument_list|()
condition|)
block|{
name|cmUtil
operator|.
name|addChangeMessage
argument_list|(
name|db
argument_list|,
name|update
argument_list|,
name|changeMessage
argument_list|)
expr_stmt|;
block|}
name|db
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|db
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
name|update
operator|.
name|commit
argument_list|()
expr_stmt|;
if|if
condition|(
name|hashtags
operator|!=
literal|null
operator|&&
name|hashtags
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|HashtagsInput
name|input
init|=
operator|new
name|HashtagsInput
argument_list|()
decl_stmt|;
name|input
operator|.
name|add
operator|=
name|hashtags
expr_stmt|;
name|hashtagsUtil
operator|.
name|setHashtags
argument_list|(
name|ctl
argument_list|,
name|input
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ValidationException
decl||
name|AuthException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cannot add hashtags to change "
operator|+
name|change
operator|.
name|getId
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|CheckedFuture
argument_list|<
name|?
argument_list|,
name|IOException
argument_list|>
name|f
init|=
name|indexer
operator|.
name|indexAsync
argument_list|(
name|change
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|messageIsForChange
argument_list|()
condition|)
block|{
name|commitMessageNotForChange
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|sendMail
condition|)
block|{
try|try
block|{
name|CreateChangeSender
name|cm
init|=
name|createChangeSenderFactory
operator|.
name|create
argument_list|(
name|change
argument_list|)
decl_stmt|;
name|cm
operator|.
name|setFrom
argument_list|(
name|change
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|cm
operator|.
name|setPatchSet
argument_list|(
name|patchSet
argument_list|,
name|patchSetInfo
argument_list|)
expr_stmt|;
name|cm
operator|.
name|addReviewers
argument_list|(
name|reviewers
argument_list|)
expr_stmt|;
name|cm
operator|.
name|addExtraCC
argument_list|(
name|extraCC
argument_list|)
expr_stmt|;
name|cm
operator|.
name|send
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|err
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cannot send email for new change "
operator|+
name|change
operator|.
name|getId
argument_list|()
argument_list|,
name|err
argument_list|)
expr_stmt|;
block|}
block|}
name|f
operator|.
name|checkedGet
argument_list|()
expr_stmt|;
name|gitRefUpdated
operator|.
name|fire
argument_list|(
name|change
operator|.
name|getProject
argument_list|()
argument_list|,
name|patchSet
operator|.
name|getRefName
argument_list|()
argument_list|,
name|ObjectId
operator|.
name|zeroId
argument_list|()
argument_list|,
name|commit
argument_list|)
expr_stmt|;
if|if
condition|(
name|runHooks
condition|)
block|{
name|hooks
operator|.
name|doPatchsetCreatedHook
argument_list|(
name|change
argument_list|,
name|patchSet
argument_list|,
name|db
argument_list|)
expr_stmt|;
if|if
condition|(
name|hashtags
operator|!=
literal|null
operator|&&
name|hashtags
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|hooks
operator|.
name|doHashtagsChangedHook
argument_list|(
name|change
argument_list|,
name|accountCache
operator|.
name|get
argument_list|(
name|change
operator|.
name|getOwner
argument_list|()
argument_list|)
operator|.
name|getAccount
argument_list|()
argument_list|,
name|hashtags
argument_list|,
literal|null
argument_list|,
name|hashtags
argument_list|,
name|db
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|change
return|;
block|}
DECL|method|commitMessageNotForChange ()
specifier|private
name|void
name|commitMessageNotForChange
parameter_list|()
throws|throws
name|OrmException
throws|,
name|IOException
block|{
name|ReviewDb
name|db
init|=
name|dbProvider
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|changeMessage
operator|!=
literal|null
condition|)
block|{
name|Change
name|otherChange
init|=
name|db
operator|.
name|changes
argument_list|()
operator|.
name|get
argument_list|(
name|changeMessage
operator|.
name|getPatchSetId
argument_list|()
operator|.
name|getParentKey
argument_list|()
argument_list|)
decl_stmt|;
name|ChangeUtil
operator|.
name|bumpRowVersionNotLastUpdatedOn
argument_list|(
name|changeMessage
operator|.
name|getKey
argument_list|()
operator|.
name|getParentKey
argument_list|()
argument_list|,
name|db
argument_list|)
expr_stmt|;
name|ChangeControl
name|otherControl
init|=
name|refControl
operator|.
name|getProjectControl
argument_list|()
operator|.
name|controlFor
argument_list|(
name|otherChange
argument_list|)
decl_stmt|;
name|ChangeUpdate
name|updateForOtherChange
init|=
name|updateFactory
operator|.
name|create
argument_list|(
name|otherControl
argument_list|,
name|change
operator|.
name|getLastUpdatedOn
argument_list|()
argument_list|)
decl_stmt|;
name|cmUtil
operator|.
name|addChangeMessage
argument_list|(
name|db
argument_list|,
name|updateForOtherChange
argument_list|,
name|changeMessage
argument_list|)
expr_stmt|;
name|updateForOtherChange
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|messageIsForChange ()
specifier|private
name|boolean
name|messageIsForChange
parameter_list|()
block|{
if|if
condition|(
name|changeMessage
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Change
operator|.
name|Id
name|id
init|=
name|change
operator|.
name|getId
argument_list|()
decl_stmt|;
name|Change
operator|.
name|Id
name|msgId
init|=
name|changeMessage
operator|.
name|getKey
argument_list|()
operator|.
name|getParentKey
argument_list|()
decl_stmt|;
return|return
name|msgId
operator|.
name|equals
argument_list|(
name|id
argument_list|)
return|;
block|}
block|}
end_class

end_unit

