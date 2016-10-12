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
DECL|package|com.google.gerrit.server.extensions.events
package|package
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
name|extensions
operator|.
name|api
operator|.
name|changes
operator|.
name|NotifyHandling
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
name|common
operator|.
name|AccountInfo
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
name|common
operator|.
name|ApprovalInfo
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
name|common
operator|.
name|ChangeInfo
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
name|common
operator|.
name|RevisionInfo
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
name|events
operator|.
name|ReviewerDeletedListener
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
name|registration
operator|.
name|DynamicSet
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
name|server
operator|.
name|GpgException
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
name|PatchListNotAvailableException
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
name|Map
import|;
end_import

begin_class
DECL|class|ReviewerDeleted
specifier|public
class|class
name|ReviewerDeleted
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
name|ReviewerDeleted
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|listeners
specifier|private
specifier|final
name|DynamicSet
argument_list|<
name|ReviewerDeletedListener
argument_list|>
name|listeners
decl_stmt|;
DECL|field|util
specifier|private
specifier|final
name|EventUtil
name|util
decl_stmt|;
annotation|@
name|Inject
DECL|method|ReviewerDeleted (DynamicSet<ReviewerDeletedListener> listeners, EventUtil util)
name|ReviewerDeleted
parameter_list|(
name|DynamicSet
argument_list|<
name|ReviewerDeletedListener
argument_list|>
name|listeners
parameter_list|,
name|EventUtil
name|util
parameter_list|)
block|{
name|this
operator|.
name|listeners
operator|=
name|listeners
expr_stmt|;
name|this
operator|.
name|util
operator|=
name|util
expr_stmt|;
block|}
DECL|method|fire (Change change, PatchSet patchSet, Account reviewer, Account remover, String message, Map<String, Short> newApprovals, Map<String, Short> oldApprovals, NotifyHandling notify, Timestamp when)
specifier|public
name|void
name|fire
parameter_list|(
name|Change
name|change
parameter_list|,
name|PatchSet
name|patchSet
parameter_list|,
name|Account
name|reviewer
parameter_list|,
name|Account
name|remover
parameter_list|,
name|String
name|message
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Short
argument_list|>
name|newApprovals
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Short
argument_list|>
name|oldApprovals
parameter_list|,
name|NotifyHandling
name|notify
parameter_list|,
name|Timestamp
name|when
parameter_list|)
block|{
if|if
condition|(
operator|!
name|listeners
operator|.
name|iterator
argument_list|()
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return;
block|}
try|try
block|{
name|Event
name|event
init|=
operator|new
name|Event
argument_list|(
name|util
operator|.
name|changeInfo
argument_list|(
name|change
argument_list|)
argument_list|,
name|util
operator|.
name|revisionInfo
argument_list|(
name|change
operator|.
name|getProject
argument_list|()
argument_list|,
name|patchSet
argument_list|)
argument_list|,
name|util
operator|.
name|accountInfo
argument_list|(
name|reviewer
argument_list|)
argument_list|,
name|util
operator|.
name|accountInfo
argument_list|(
name|remover
argument_list|)
argument_list|,
name|message
argument_list|,
name|util
operator|.
name|approvals
argument_list|(
name|reviewer
argument_list|,
name|newApprovals
argument_list|,
name|when
argument_list|)
argument_list|,
name|util
operator|.
name|approvals
argument_list|(
name|reviewer
argument_list|,
name|oldApprovals
argument_list|,
name|when
argument_list|)
argument_list|,
name|notify
argument_list|,
name|when
argument_list|)
decl_stmt|;
for|for
control|(
name|ReviewerDeletedListener
name|listener
range|:
name|listeners
control|)
block|{
try|try
block|{
name|listener
operator|.
name|onReviewerDeleted
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|util
operator|.
name|logEventListenerError
argument_list|(
name|this
argument_list|,
name|listener
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|PatchListNotAvailableException
decl||
name|GpgException
decl||
name|IOException
decl||
name|OrmException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Couldn't fire event"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|Event
specifier|private
specifier|static
class|class
name|Event
extends|extends
name|AbstractRevisionEvent
implements|implements
name|ReviewerDeletedListener
operator|.
name|Event
block|{
DECL|field|reviewer
specifier|private
specifier|final
name|AccountInfo
name|reviewer
decl_stmt|;
DECL|field|comment
specifier|private
specifier|final
name|String
name|comment
decl_stmt|;
DECL|field|newApprovals
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|ApprovalInfo
argument_list|>
name|newApprovals
decl_stmt|;
DECL|field|oldApprovals
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|ApprovalInfo
argument_list|>
name|oldApprovals
decl_stmt|;
DECL|method|Event (ChangeInfo change, RevisionInfo revision, AccountInfo reviewer, AccountInfo remover, String comment, Map<String, ApprovalInfo> newApprovals, Map<String, ApprovalInfo> oldApprovals, NotifyHandling notify, Timestamp when)
name|Event
parameter_list|(
name|ChangeInfo
name|change
parameter_list|,
name|RevisionInfo
name|revision
parameter_list|,
name|AccountInfo
name|reviewer
parameter_list|,
name|AccountInfo
name|remover
parameter_list|,
name|String
name|comment
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|ApprovalInfo
argument_list|>
name|newApprovals
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|ApprovalInfo
argument_list|>
name|oldApprovals
parameter_list|,
name|NotifyHandling
name|notify
parameter_list|,
name|Timestamp
name|when
parameter_list|)
block|{
name|super
argument_list|(
name|change
argument_list|,
name|revision
argument_list|,
name|remover
argument_list|,
name|when
argument_list|,
name|notify
argument_list|)
expr_stmt|;
name|this
operator|.
name|reviewer
operator|=
name|reviewer
expr_stmt|;
name|this
operator|.
name|comment
operator|=
name|comment
expr_stmt|;
name|this
operator|.
name|newApprovals
operator|=
name|newApprovals
expr_stmt|;
name|this
operator|.
name|oldApprovals
operator|=
name|oldApprovals
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getReviewer ()
specifier|public
name|AccountInfo
name|getReviewer
parameter_list|()
block|{
return|return
name|reviewer
return|;
block|}
annotation|@
name|Override
DECL|method|getComment ()
specifier|public
name|String
name|getComment
parameter_list|()
block|{
return|return
name|comment
return|;
block|}
annotation|@
name|Override
DECL|method|getNewApprovals ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|ApprovalInfo
argument_list|>
name|getNewApprovals
parameter_list|()
block|{
return|return
name|newApprovals
return|;
block|}
annotation|@
name|Override
DECL|method|getOldApprovals ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|ApprovalInfo
argument_list|>
name|getOldApprovals
parameter_list|()
block|{
return|return
name|oldApprovals
return|;
block|}
block|}
block|}
end_class

end_unit

