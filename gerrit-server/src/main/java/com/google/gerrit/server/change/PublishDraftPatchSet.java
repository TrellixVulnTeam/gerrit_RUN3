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
name|server
operator|.
name|mail
operator|.
name|MailUtil
operator|.
name|getRecipientsFromFooters
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
name|Nullable
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
name|TimeUtil
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
name|errors
operator|.
name|EmailException
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
name|restapi
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
name|extensions
operator|.
name|restapi
operator|.
name|ResourceConflictException
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
name|restapi
operator|.
name|ResourceNotFoundException
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
name|restapi
operator|.
name|Response
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
name|restapi
operator|.
name|RestApiException
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
name|restapi
operator|.
name|RestModifyView
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
name|webui
operator|.
name|UiAction
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
name|CurrentUser
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
name|PatchSetUtil
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
name|AccountResolver
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
name|change
operator|.
name|PublishDraftPatchSet
operator|.
name|Input
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
name|git
operator|.
name|BatchUpdate
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
name|git
operator|.
name|BatchUpdate
operator|.
name|ChangeContext
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
name|git
operator|.
name|BatchUpdate
operator|.
name|Context
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
name|git
operator|.
name|UpdateException
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
name|mail
operator|.
name|MailUtil
operator|.
name|MailRecipients
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
name|ReplacePatchSetSender
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
name|Singleton
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
name|FooterLine
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
name|Collection
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
annotation|@
name|Singleton
DECL|class|PublishDraftPatchSet
specifier|public
class|class
name|PublishDraftPatchSet
implements|implements
name|RestModifyView
argument_list|<
name|RevisionResource
argument_list|,
name|Input
argument_list|>
implements|,
name|UiAction
argument_list|<
name|RevisionResource
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
name|PublishDraftPatchSet
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|class|Input
specifier|public
specifier|static
class|class
name|Input
block|{   }
DECL|field|accountResolver
specifier|private
specifier|final
name|AccountResolver
name|accountResolver
decl_stmt|;
DECL|field|approvalsUtil
specifier|private
specifier|final
name|ApprovalsUtil
name|approvalsUtil
decl_stmt|;
DECL|field|updateFactory
specifier|private
specifier|final
name|BatchUpdate
operator|.
name|Factory
name|updateFactory
decl_stmt|;
DECL|field|hooks
specifier|private
specifier|final
name|ChangeHooks
name|hooks
decl_stmt|;
DECL|field|createChangeSenderFactory
specifier|private
specifier|final
name|CreateChangeSender
operator|.
name|Factory
name|createChangeSenderFactory
decl_stmt|;
DECL|field|patchSetInfoFactory
specifier|private
specifier|final
name|PatchSetInfoFactory
name|patchSetInfoFactory
decl_stmt|;
DECL|field|psUtil
specifier|private
specifier|final
name|PatchSetUtil
name|psUtil
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
DECL|field|replacePatchSetFactory
specifier|private
specifier|final
name|ReplacePatchSetSender
operator|.
name|Factory
name|replacePatchSetFactory
decl_stmt|;
annotation|@
name|Inject
DECL|method|PublishDraftPatchSet ( AccountResolver accountResolver, ApprovalsUtil approvalsUtil, BatchUpdate.Factory updateFactory, ChangeHooks hooks, CreateChangeSender.Factory createChangeSenderFactory, PatchSetInfoFactory patchSetInfoFactory, PatchSetUtil psUtil, Provider<ReviewDb> dbProvider, ReplacePatchSetSender.Factory replacePatchSetFactory)
specifier|public
name|PublishDraftPatchSet
parameter_list|(
name|AccountResolver
name|accountResolver
parameter_list|,
name|ApprovalsUtil
name|approvalsUtil
parameter_list|,
name|BatchUpdate
operator|.
name|Factory
name|updateFactory
parameter_list|,
name|ChangeHooks
name|hooks
parameter_list|,
name|CreateChangeSender
operator|.
name|Factory
name|createChangeSenderFactory
parameter_list|,
name|PatchSetInfoFactory
name|patchSetInfoFactory
parameter_list|,
name|PatchSetUtil
name|psUtil
parameter_list|,
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|dbProvider
parameter_list|,
name|ReplacePatchSetSender
operator|.
name|Factory
name|replacePatchSetFactory
parameter_list|)
block|{
name|this
operator|.
name|accountResolver
operator|=
name|accountResolver
expr_stmt|;
name|this
operator|.
name|approvalsUtil
operator|=
name|approvalsUtil
expr_stmt|;
name|this
operator|.
name|updateFactory
operator|=
name|updateFactory
expr_stmt|;
name|this
operator|.
name|hooks
operator|=
name|hooks
expr_stmt|;
name|this
operator|.
name|createChangeSenderFactory
operator|=
name|createChangeSenderFactory
expr_stmt|;
name|this
operator|.
name|patchSetInfoFactory
operator|=
name|patchSetInfoFactory
expr_stmt|;
name|this
operator|.
name|psUtil
operator|=
name|psUtil
expr_stmt|;
name|this
operator|.
name|dbProvider
operator|=
name|dbProvider
expr_stmt|;
name|this
operator|.
name|replacePatchSetFactory
operator|=
name|replacePatchSetFactory
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (RevisionResource rsrc, Input input)
specifier|public
name|Response
argument_list|<
name|?
argument_list|>
name|apply
parameter_list|(
name|RevisionResource
name|rsrc
parameter_list|,
name|Input
name|input
parameter_list|)
throws|throws
name|RestApiException
throws|,
name|UpdateException
block|{
return|return
name|apply
argument_list|(
name|rsrc
operator|.
name|getUser
argument_list|()
argument_list|,
name|rsrc
operator|.
name|getChange
argument_list|()
argument_list|,
name|rsrc
operator|.
name|getPatchSet
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|rsrc
operator|.
name|getPatchSet
argument_list|()
argument_list|)
return|;
block|}
DECL|method|apply (CurrentUser u, Change c, PatchSet.Id psId, PatchSet ps)
specifier|private
name|Response
argument_list|<
name|?
argument_list|>
name|apply
parameter_list|(
name|CurrentUser
name|u
parameter_list|,
name|Change
name|c
parameter_list|,
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|,
name|PatchSet
name|ps
parameter_list|)
throws|throws
name|RestApiException
throws|,
name|UpdateException
block|{
try|try
init|(
name|BatchUpdate
name|bu
init|=
name|updateFactory
operator|.
name|create
argument_list|(
name|dbProvider
operator|.
name|get
argument_list|()
argument_list|,
name|c
operator|.
name|getProject
argument_list|()
argument_list|,
name|u
argument_list|,
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
init|)
block|{
name|bu
operator|.
name|addOp
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|,
operator|new
name|Op
argument_list|(
name|psId
argument_list|,
name|ps
argument_list|)
argument_list|)
expr_stmt|;
name|bu
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
return|return
name|Response
operator|.
name|none
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getDescription (RevisionResource rsrc)
specifier|public
name|UiAction
operator|.
name|Description
name|getDescription
parameter_list|(
name|RevisionResource
name|rsrc
parameter_list|)
block|{
try|try
block|{
return|return
operator|new
name|UiAction
operator|.
name|Description
argument_list|()
operator|.
name|setTitle
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Publish revision %d"
argument_list|,
name|rsrc
operator|.
name|getPatchSet
argument_list|()
operator|.
name|getPatchSetId
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setVisible
argument_list|(
name|rsrc
operator|.
name|getPatchSet
argument_list|()
operator|.
name|isDraft
argument_list|()
operator|&&
name|rsrc
operator|.
name|getControl
argument_list|()
operator|.
name|canPublish
argument_list|(
name|dbProvider
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|class|CurrentRevision
specifier|public
specifier|static
class|class
name|CurrentRevision
implements|implements
name|RestModifyView
argument_list|<
name|ChangeResource
argument_list|,
name|Input
argument_list|>
block|{
DECL|field|publish
specifier|private
specifier|final
name|PublishDraftPatchSet
name|publish
decl_stmt|;
annotation|@
name|Inject
DECL|method|CurrentRevision (PublishDraftPatchSet publish)
name|CurrentRevision
parameter_list|(
name|PublishDraftPatchSet
name|publish
parameter_list|)
block|{
name|this
operator|.
name|publish
operator|=
name|publish
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (ChangeResource rsrc, Input input)
specifier|public
name|Response
argument_list|<
name|?
argument_list|>
name|apply
parameter_list|(
name|ChangeResource
name|rsrc
parameter_list|,
name|Input
name|input
parameter_list|)
throws|throws
name|RestApiException
throws|,
name|UpdateException
block|{
return|return
name|publish
operator|.
name|apply
argument_list|(
name|rsrc
operator|.
name|getControl
argument_list|()
operator|.
name|getUser
argument_list|()
argument_list|,
name|rsrc
operator|.
name|getChange
argument_list|()
argument_list|,
name|rsrc
operator|.
name|getChange
argument_list|()
operator|.
name|currentPatchSetId
argument_list|()
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
DECL|class|Op
specifier|private
class|class
name|Op
extends|extends
name|BatchUpdate
operator|.
name|Op
block|{
DECL|field|psId
specifier|private
specifier|final
name|PatchSet
operator|.
name|Id
name|psId
decl_stmt|;
DECL|field|patchSet
specifier|private
name|PatchSet
name|patchSet
decl_stmt|;
DECL|field|change
specifier|private
name|Change
name|change
decl_stmt|;
DECL|field|wasDraftChange
specifier|private
name|boolean
name|wasDraftChange
decl_stmt|;
DECL|field|patchSetInfo
specifier|private
name|PatchSetInfo
name|patchSetInfo
decl_stmt|;
DECL|field|recipients
specifier|private
name|MailRecipients
name|recipients
decl_stmt|;
DECL|method|Op (PatchSet.Id psId, @Nullable PatchSet patchSet)
specifier|private
name|Op
parameter_list|(
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|,
annotation|@
name|Nullable
name|PatchSet
name|patchSet
parameter_list|)
block|{
name|this
operator|.
name|psId
operator|=
name|psId
expr_stmt|;
name|this
operator|.
name|patchSet
operator|=
name|patchSet
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|updateChange (ChangeContext ctx)
specifier|public
name|void
name|updateChange
parameter_list|(
name|ChangeContext
name|ctx
parameter_list|)
throws|throws
name|RestApiException
throws|,
name|OrmException
throws|,
name|IOException
block|{
if|if
condition|(
operator|!
name|ctx
operator|.
name|getControl
argument_list|()
operator|.
name|canPublish
argument_list|(
name|ctx
operator|.
name|getDb
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AuthException
argument_list|(
literal|"Cannot publish this draft patch set"
argument_list|)
throw|;
block|}
if|if
condition|(
name|patchSet
operator|==
literal|null
condition|)
block|{
name|patchSet
operator|=
name|psUtil
operator|.
name|get
argument_list|(
name|ctx
operator|.
name|getDb
argument_list|()
argument_list|,
name|ctx
operator|.
name|getNotes
argument_list|()
argument_list|,
name|psId
argument_list|)
expr_stmt|;
if|if
condition|(
name|patchSet
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
name|psId
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
name|saveChange
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|savePatchSet
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|addReviewers
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
DECL|method|saveChange (ChangeContext ctx)
specifier|private
name|void
name|saveChange
parameter_list|(
name|ChangeContext
name|ctx
parameter_list|)
block|{
name|change
operator|=
name|ctx
operator|.
name|getChange
argument_list|()
expr_stmt|;
name|ChangeUpdate
name|update
init|=
name|ctx
operator|.
name|getUpdate
argument_list|(
name|psId
argument_list|)
decl_stmt|;
name|wasDraftChange
operator|=
name|change
operator|.
name|getStatus
argument_list|()
operator|==
name|Change
operator|.
name|Status
operator|.
name|DRAFT
expr_stmt|;
if|if
condition|(
name|wasDraftChange
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
name|NEW
argument_list|)
expr_stmt|;
name|update
operator|.
name|setStatus
argument_list|(
name|change
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|ChangeUtil
operator|.
name|updated
argument_list|(
name|change
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|saveChange
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|savePatchSet (ChangeContext ctx)
specifier|private
name|void
name|savePatchSet
parameter_list|(
name|ChangeContext
name|ctx
parameter_list|)
throws|throws
name|RestApiException
throws|,
name|OrmException
block|{
if|if
condition|(
operator|!
name|patchSet
operator|.
name|isDraft
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
literal|"Patch set is not a draft"
argument_list|)
throw|;
block|}
name|patchSet
operator|.
name|setDraft
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// Force ETag invalidation if not done already
if|if
condition|(
operator|!
name|wasDraftChange
condition|)
block|{
name|ChangeUtil
operator|.
name|updated
argument_list|(
name|change
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|saveChange
argument_list|()
expr_stmt|;
block|}
name|ctx
operator|.
name|getDb
argument_list|()
operator|.
name|patchSets
argument_list|()
operator|.
name|update
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|patchSet
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|addReviewers (ChangeContext ctx)
specifier|private
name|void
name|addReviewers
parameter_list|(
name|ChangeContext
name|ctx
parameter_list|)
throws|throws
name|OrmException
throws|,
name|IOException
block|{
name|LabelTypes
name|labelTypes
init|=
name|ctx
operator|.
name|getControl
argument_list|()
operator|.
name|getLabelTypes
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|oldReviewers
init|=
name|approvalsUtil
operator|.
name|getReviewers
argument_list|(
name|ctx
operator|.
name|getDb
argument_list|()
argument_list|,
name|ctx
operator|.
name|getNotes
argument_list|()
argument_list|)
operator|.
name|values
argument_list|()
decl_stmt|;
name|RevCommit
name|commit
init|=
name|ctx
operator|.
name|getRevWalk
argument_list|()
operator|.
name|parseCommit
argument_list|(
name|ObjectId
operator|.
name|fromString
argument_list|(
name|patchSet
operator|.
name|getRevision
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|patchSetInfo
operator|=
name|patchSetInfoFactory
operator|.
name|get
argument_list|(
name|ctx
operator|.
name|getRevWalk
argument_list|()
argument_list|,
name|commit
argument_list|,
name|psId
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|FooterLine
argument_list|>
name|footerLines
init|=
name|commit
operator|.
name|getFooterLines
argument_list|()
decl_stmt|;
name|recipients
operator|=
name|getRecipientsFromFooters
argument_list|(
name|accountResolver
argument_list|,
name|patchSet
operator|.
name|isDraft
argument_list|()
argument_list|,
name|footerLines
argument_list|)
expr_stmt|;
name|recipients
operator|.
name|remove
argument_list|(
name|ctx
operator|.
name|getUser
argument_list|()
operator|.
name|getAccountId
argument_list|()
argument_list|)
expr_stmt|;
name|approvalsUtil
operator|.
name|addReviewers
argument_list|(
name|ctx
operator|.
name|getDb
argument_list|()
argument_list|,
name|ctx
operator|.
name|getUpdate
argument_list|(
name|psId
argument_list|)
argument_list|,
name|labelTypes
argument_list|,
name|change
argument_list|,
name|patchSet
argument_list|,
name|patchSetInfo
argument_list|,
name|recipients
operator|.
name|getReviewers
argument_list|()
argument_list|,
name|oldReviewers
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|postUpdate (Context ctx)
specifier|public
name|void
name|postUpdate
parameter_list|(
name|Context
name|ctx
parameter_list|)
throws|throws
name|OrmException
block|{
name|hooks
operator|.
name|doDraftPublishedHook
argument_list|(
name|change
argument_list|,
name|patchSet
argument_list|,
name|ctx
operator|.
name|getDb
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|patchSet
operator|.
name|isDraft
argument_list|()
operator|&&
name|change
operator|.
name|getStatus
argument_list|()
operator|==
name|Change
operator|.
name|Status
operator|.
name|DRAFT
condition|)
block|{
comment|// Skip emails if the patch set is still a draft.
return|return;
block|}
try|try
block|{
if|if
condition|(
name|wasDraftChange
condition|)
block|{
name|sendCreateChange
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sendReplacePatchSet
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|EmailException
decl||
name|OrmException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cannot send email for publishing draft "
operator|+
name|psId
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|sendCreateChange (Context ctx)
specifier|private
name|void
name|sendCreateChange
parameter_list|(
name|Context
name|ctx
parameter_list|)
throws|throws
name|EmailException
block|{
name|CreateChangeSender
name|cm
init|=
name|createChangeSenderFactory
operator|.
name|create
argument_list|(
name|change
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|cm
operator|.
name|setFrom
argument_list|(
name|ctx
operator|.
name|getUser
argument_list|()
operator|.
name|getAccountId
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
name|recipients
operator|.
name|getReviewers
argument_list|()
argument_list|)
expr_stmt|;
name|cm
operator|.
name|addExtraCC
argument_list|(
name|recipients
operator|.
name|getCcOnly
argument_list|()
argument_list|)
expr_stmt|;
name|cm
operator|.
name|send
argument_list|()
expr_stmt|;
block|}
DECL|method|sendReplacePatchSet (Context ctx)
specifier|private
name|void
name|sendReplacePatchSet
parameter_list|(
name|Context
name|ctx
parameter_list|)
throws|throws
name|EmailException
throws|,
name|OrmException
block|{
name|Account
operator|.
name|Id
name|accountId
init|=
name|ctx
operator|.
name|getUser
argument_list|()
operator|.
name|getAccountId
argument_list|()
decl_stmt|;
name|ChangeMessage
name|msg
init|=
operator|new
name|ChangeMessage
argument_list|(
operator|new
name|ChangeMessage
operator|.
name|Key
argument_list|(
name|change
operator|.
name|getId
argument_list|()
argument_list|,
name|ChangeUtil
operator|.
name|messageUUID
argument_list|(
name|ctx
operator|.
name|getDb
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|accountId
argument_list|,
name|ctx
operator|.
name|getWhen
argument_list|()
argument_list|,
name|psId
argument_list|)
decl_stmt|;
name|msg
operator|.
name|setMessage
argument_list|(
literal|"Uploaded patch set "
operator|+
name|psId
operator|.
name|get
argument_list|()
operator|+
literal|"."
argument_list|)
expr_stmt|;
name|ReplacePatchSetSender
name|cm
init|=
name|replacePatchSetFactory
operator|.
name|create
argument_list|(
name|change
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|cm
operator|.
name|setFrom
argument_list|(
name|accountId
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
name|setChangeMessage
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|cm
operator|.
name|addReviewers
argument_list|(
name|recipients
operator|.
name|getReviewers
argument_list|()
argument_list|)
expr_stmt|;
name|cm
operator|.
name|addExtraCC
argument_list|(
name|recipients
operator|.
name|getCcOnly
argument_list|()
argument_list|)
expr_stmt|;
name|cm
operator|.
name|send
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

