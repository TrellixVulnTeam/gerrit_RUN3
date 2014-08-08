begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2014 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.notedb
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|notedb
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
name|checkState
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
name|base
operator|.
name|Objects
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
name|ComparisonChain
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
name|util
operator|.
name|concurrent
operator|.
name|ListenableFuture
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
name|ListeningExecutorService
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
name|GerritPersonIdent
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
name|IdentifiedUser
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
name|GitRepositoryManager
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
name|VersionedMetaData
operator|.
name|BatchMetaDataUpdate
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
name|NoSuchChangeException
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
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|BatchRefUpdate
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
name|CommitBuilder
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
name|PersonIdent
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|concurrent
operator|.
name|Callable
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
name|TimeUnit
import|;
end_import

begin_class
DECL|class|ChangeRebuilder
specifier|public
class|class
name|ChangeRebuilder
block|{
DECL|field|TS_WINDOW_MS
specifier|private
specifier|static
specifier|final
name|long
name|TS_WINDOW_MS
init|=
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|convert
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
DECL|field|serverIdent
specifier|private
specifier|final
name|PersonIdent
name|serverIdent
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
DECL|field|controlFactory
specifier|private
specifier|final
name|ChangeControl
operator|.
name|GenericFactory
name|controlFactory
decl_stmt|;
DECL|field|userFactory
specifier|private
specifier|final
name|IdentifiedUser
operator|.
name|GenericFactory
name|userFactory
decl_stmt|;
DECL|field|updateFactory
specifier|private
specifier|final
name|ChangeUpdate
operator|.
name|Factory
name|updateFactory
decl_stmt|;
annotation|@
name|Inject
DECL|method|ChangeRebuilder (@erritPersonIdent PersonIdent serverIdent, Provider<ReviewDb> dbProvider, GitRepositoryManager repoManager, ChangeControl.GenericFactory controlFactory, IdentifiedUser.GenericFactory userFactory, ChangeUpdate.Factory updateFactory)
name|ChangeRebuilder
parameter_list|(
annotation|@
name|GerritPersonIdent
name|PersonIdent
name|serverIdent
parameter_list|,
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|dbProvider
parameter_list|,
name|GitRepositoryManager
name|repoManager
parameter_list|,
name|ChangeControl
operator|.
name|GenericFactory
name|controlFactory
parameter_list|,
name|IdentifiedUser
operator|.
name|GenericFactory
name|userFactory
parameter_list|,
name|ChangeUpdate
operator|.
name|Factory
name|updateFactory
parameter_list|)
block|{
name|this
operator|.
name|serverIdent
operator|=
name|serverIdent
expr_stmt|;
name|this
operator|.
name|dbProvider
operator|=
name|dbProvider
expr_stmt|;
name|this
operator|.
name|controlFactory
operator|=
name|controlFactory
expr_stmt|;
name|this
operator|.
name|userFactory
operator|=
name|userFactory
expr_stmt|;
name|this
operator|.
name|updateFactory
operator|=
name|updateFactory
expr_stmt|;
block|}
DECL|method|rebuildAsync (final Change change, ListeningExecutorService executor, final BatchRefUpdate bru)
specifier|public
name|ListenableFuture
argument_list|<
name|?
argument_list|>
name|rebuildAsync
parameter_list|(
specifier|final
name|Change
name|change
parameter_list|,
name|ListeningExecutorService
name|executor
parameter_list|,
specifier|final
name|BatchRefUpdate
name|bru
parameter_list|)
block|{
return|return
name|executor
operator|.
name|submit
argument_list|(
operator|new
name|Callable
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|rebuild
argument_list|(
name|change
argument_list|,
name|bru
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|rebuild (Change change, BatchRefUpdate bru)
specifier|public
name|void
name|rebuild
parameter_list|(
name|Change
name|change
parameter_list|,
name|BatchRefUpdate
name|bru
parameter_list|)
throws|throws
name|NoSuchChangeException
throws|,
name|IOException
throws|,
name|OrmException
block|{
name|ReviewDb
name|db
init|=
name|dbProvider
operator|.
name|get
argument_list|()
decl_stmt|;
name|Change
operator|.
name|Id
name|changeId
init|=
name|change
operator|.
name|getId
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Event
argument_list|>
name|events
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|PatchSet
name|ps
range|:
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|byChange
argument_list|(
name|changeId
argument_list|)
control|)
block|{
name|events
operator|.
name|add
argument_list|(
operator|new
name|PatchSetEvent
argument_list|(
name|ps
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|PatchSetApproval
name|psa
range|:
name|db
operator|.
name|patchSetApprovals
argument_list|()
operator|.
name|byChange
argument_list|(
name|changeId
argument_list|)
control|)
block|{
name|events
operator|.
name|add
argument_list|(
operator|new
name|ApprovalEvent
argument_list|(
name|psa
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|events
argument_list|)
expr_stmt|;
name|BatchMetaDataUpdate
name|batch
init|=
literal|null
decl_stmt|;
name|ChangeUpdate
name|update
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Event
name|e
range|:
name|events
control|)
block|{
if|if
condition|(
operator|!
name|sameUpdate
argument_list|(
name|e
argument_list|,
name|update
argument_list|)
condition|)
block|{
if|if
condition|(
name|update
operator|!=
literal|null
condition|)
block|{
name|writeToBatch
argument_list|(
name|batch
argument_list|,
name|update
argument_list|)
expr_stmt|;
block|}
name|IdentifiedUser
name|user
init|=
name|userFactory
operator|.
name|create
argument_list|(
name|dbProvider
argument_list|,
name|e
operator|.
name|who
argument_list|)
decl_stmt|;
name|update
operator|=
name|updateFactory
operator|.
name|create
argument_list|(
name|controlFactory
operator|.
name|controlFor
argument_list|(
name|change
argument_list|,
name|user
argument_list|)
argument_list|,
name|e
operator|.
name|when
argument_list|)
expr_stmt|;
name|update
operator|.
name|setPatchSetId
argument_list|(
name|e
operator|.
name|psId
argument_list|)
expr_stmt|;
if|if
condition|(
name|batch
operator|==
literal|null
condition|)
block|{
name|batch
operator|=
name|update
operator|.
name|openUpdateInBatch
argument_list|(
name|bru
argument_list|)
expr_stmt|;
block|}
block|}
name|e
operator|.
name|apply
argument_list|(
name|update
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|batch
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|update
operator|!=
literal|null
condition|)
block|{
name|writeToBatch
argument_list|(
name|batch
argument_list|,
name|update
argument_list|)
expr_stmt|;
block|}
comment|// Since the BatchMetaDataUpdates generated by all ChangeRebuilders on a
comment|// given project are backed by the same BatchRefUpdate, we need to
comment|// synchronize on the BatchRefUpdate. Therefore, since commit on a
comment|// BatchMetaDataUpdate is the only method that modifies a BatchRefUpdate,
comment|// we can just synchronize this call.
synchronized|synchronized
init|(
name|bru
init|)
block|{
name|batch
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|writeToBatch (BatchMetaDataUpdate batch, ChangeUpdate update)
specifier|private
name|void
name|writeToBatch
parameter_list|(
name|BatchMetaDataUpdate
name|batch
parameter_list|,
name|ChangeUpdate
name|update
parameter_list|)
throws|throws
name|IOException
block|{
name|CommitBuilder
name|commit
init|=
operator|new
name|CommitBuilder
argument_list|()
decl_stmt|;
name|commit
operator|.
name|setCommitter
argument_list|(
operator|new
name|PersonIdent
argument_list|(
name|serverIdent
argument_list|,
name|update
operator|.
name|getWhen
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|batch
operator|.
name|write
argument_list|(
name|update
argument_list|,
name|commit
argument_list|)
expr_stmt|;
block|}
DECL|method|round (Date when)
specifier|private
specifier|static
name|long
name|round
parameter_list|(
name|Date
name|when
parameter_list|)
block|{
return|return
name|when
operator|.
name|getTime
argument_list|()
operator|/
name|TS_WINDOW_MS
return|;
block|}
DECL|method|sameUpdate (Event event, ChangeUpdate update)
specifier|private
specifier|static
name|boolean
name|sameUpdate
parameter_list|(
name|Event
name|event
parameter_list|,
name|ChangeUpdate
name|update
parameter_list|)
block|{
return|return
name|update
operator|!=
literal|null
operator|&&
name|round
argument_list|(
name|event
operator|.
name|when
argument_list|)
operator|==
name|round
argument_list|(
name|update
operator|.
name|getWhen
argument_list|()
argument_list|)
operator|&&
name|event
operator|.
name|who
operator|.
name|equals
argument_list|(
name|update
operator|.
name|getUser
argument_list|()
argument_list|)
operator|&&
name|event
operator|.
name|psId
operator|.
name|equals
argument_list|(
name|update
operator|.
name|getPatchSetId
argument_list|()
argument_list|)
return|;
block|}
DECL|class|Event
specifier|private
specifier|static
specifier|abstract
class|class
name|Event
implements|implements
name|Comparable
argument_list|<
name|Event
argument_list|>
block|{
DECL|field|psId
specifier|final
name|PatchSet
operator|.
name|Id
name|psId
decl_stmt|;
DECL|field|who
specifier|final
name|Account
operator|.
name|Id
name|who
decl_stmt|;
DECL|field|when
specifier|final
name|Timestamp
name|when
decl_stmt|;
DECL|method|Event (PatchSet.Id psId, Account.Id who, Timestamp when)
specifier|protected
name|Event
parameter_list|(
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|,
name|Account
operator|.
name|Id
name|who
parameter_list|,
name|Timestamp
name|when
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
name|who
operator|=
name|who
expr_stmt|;
name|this
operator|.
name|when
operator|=
name|when
expr_stmt|;
block|}
DECL|method|checkUpdate (ChangeUpdate update)
specifier|protected
name|void
name|checkUpdate
parameter_list|(
name|ChangeUpdate
name|update
parameter_list|)
block|{
name|checkState
argument_list|(
name|Objects
operator|.
name|equal
argument_list|(
name|update
operator|.
name|getPatchSetId
argument_list|()
argument_list|,
name|psId
argument_list|)
argument_list|,
literal|"cannot apply event for %s to update for %s"
argument_list|,
name|update
operator|.
name|getPatchSetId
argument_list|()
argument_list|,
name|psId
argument_list|)
expr_stmt|;
name|checkState
argument_list|(
name|when
operator|.
name|getTime
argument_list|()
operator|-
name|update
operator|.
name|getWhen
argument_list|()
operator|.
name|getTime
argument_list|()
operator|<=
name|TS_WINDOW_MS
argument_list|,
literal|"event at %s outside update window starting at %s"
argument_list|,
name|when
argument_list|,
name|update
operator|.
name|getWhen
argument_list|()
argument_list|)
expr_stmt|;
name|checkState
argument_list|(
name|Objects
operator|.
name|equal
argument_list|(
name|update
operator|.
name|getUser
argument_list|()
operator|.
name|getAccountId
argument_list|()
argument_list|,
name|who
argument_list|)
argument_list|,
literal|"cannot apply event by %s to update by %s"
argument_list|,
name|who
argument_list|,
name|update
operator|.
name|getUser
argument_list|()
operator|.
name|getAccountId
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|apply (ChangeUpdate update)
specifier|abstract
name|void
name|apply
parameter_list|(
name|ChangeUpdate
name|update
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|compareTo (Event other)
specifier|public
name|int
name|compareTo
parameter_list|(
name|Event
name|other
parameter_list|)
block|{
return|return
name|ComparisonChain
operator|.
name|start
argument_list|()
comment|// TODO(dborowitz): Smarter bucketing: pick a bucket start time T and
comment|// include all events up to T + TS_WINDOW_MS but no further.
comment|// Interleaving different authors complicates things.
operator|.
name|compare
argument_list|(
name|round
argument_list|(
name|when
argument_list|)
argument_list|,
name|round
argument_list|(
name|other
operator|.
name|when
argument_list|)
argument_list|)
operator|.
name|compare
argument_list|(
name|who
operator|.
name|get
argument_list|()
argument_list|,
name|other
operator|.
name|who
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|compare
argument_list|(
name|psId
operator|.
name|get
argument_list|()
argument_list|,
name|other
operator|.
name|psId
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|result
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
name|Objects
operator|.
name|toStringHelper
argument_list|(
name|this
argument_list|)
operator|.
name|add
argument_list|(
literal|"psId"
argument_list|,
name|psId
argument_list|)
operator|.
name|add
argument_list|(
literal|"who"
argument_list|,
name|who
argument_list|)
operator|.
name|add
argument_list|(
literal|"when"
argument_list|,
name|when
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
DECL|class|ApprovalEvent
specifier|private
specifier|static
class|class
name|ApprovalEvent
extends|extends
name|Event
block|{
DECL|field|psa
specifier|private
name|PatchSetApproval
name|psa
decl_stmt|;
DECL|method|ApprovalEvent (PatchSetApproval psa)
name|ApprovalEvent
parameter_list|(
name|PatchSetApproval
name|psa
parameter_list|)
block|{
name|super
argument_list|(
name|psa
operator|.
name|getPatchSetId
argument_list|()
argument_list|,
name|psa
operator|.
name|getAccountId
argument_list|()
argument_list|,
name|psa
operator|.
name|getGranted
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|psa
operator|=
name|psa
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (ChangeUpdate update)
name|void
name|apply
parameter_list|(
name|ChangeUpdate
name|update
parameter_list|)
block|{
name|checkUpdate
argument_list|(
name|update
argument_list|)
expr_stmt|;
name|update
operator|.
name|putApproval
argument_list|(
name|psa
operator|.
name|getLabel
argument_list|()
argument_list|,
name|psa
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|PatchSetEvent
specifier|private
specifier|static
class|class
name|PatchSetEvent
extends|extends
name|Event
block|{
DECL|field|ps
specifier|private
specifier|final
name|PatchSet
name|ps
decl_stmt|;
DECL|method|PatchSetEvent (PatchSet ps)
name|PatchSetEvent
parameter_list|(
name|PatchSet
name|ps
parameter_list|)
block|{
name|super
argument_list|(
name|ps
operator|.
name|getId
argument_list|()
argument_list|,
name|ps
operator|.
name|getUploader
argument_list|()
argument_list|,
name|ps
operator|.
name|getCreatedOn
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|ps
operator|=
name|ps
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (ChangeUpdate update)
name|void
name|apply
parameter_list|(
name|ChangeUpdate
name|update
parameter_list|)
block|{
name|checkUpdate
argument_list|(
name|update
argument_list|)
expr_stmt|;
if|if
condition|(
name|ps
operator|.
name|getPatchSetId
argument_list|()
operator|==
literal|1
condition|)
block|{
name|update
operator|.
name|setSubject
argument_list|(
literal|"Create change"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|update
operator|.
name|setSubject
argument_list|(
literal|"Create patch set "
operator|+
name|ps
operator|.
name|getPatchSetId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

