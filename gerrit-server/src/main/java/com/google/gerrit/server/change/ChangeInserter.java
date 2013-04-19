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
name|config
operator|.
name|TrackingFooters
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
name|Set
import|;
end_import

begin_class
DECL|class|ChangeInserter
specifier|public
class|class
name|ChangeInserter
block|{
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
DECL|field|trackingFooters
specifier|private
specifier|final
name|TrackingFooters
name|trackingFooters
decl_stmt|;
annotation|@
name|Inject
DECL|method|ChangeInserter (final GitReferenceUpdated gitRefUpdated, ChangeHooks hooks, ApprovalsUtil approvalsUtil, TrackingFooters trackingFooters)
specifier|public
name|ChangeInserter
parameter_list|(
specifier|final
name|GitReferenceUpdated
name|gitRefUpdated
parameter_list|,
name|ChangeHooks
name|hooks
parameter_list|,
name|ApprovalsUtil
name|approvalsUtil
parameter_list|,
name|TrackingFooters
name|trackingFooters
parameter_list|)
block|{
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
name|trackingFooters
operator|=
name|trackingFooters
expr_stmt|;
block|}
DECL|method|insertChange (ReviewDb db, Change change, PatchSet ps, RevCommit commit, LabelTypes labelTypes, PatchSetInfo info, Set<Account.Id> reviewers)
specifier|public
name|void
name|insertChange
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|Change
name|change
parameter_list|,
name|PatchSet
name|ps
parameter_list|,
name|RevCommit
name|commit
parameter_list|,
name|LabelTypes
name|labelTypes
parameter_list|,
name|PatchSetInfo
name|info
parameter_list|,
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|reviewers
parameter_list|)
throws|throws
name|OrmException
block|{
name|insertChange
argument_list|(
name|db
argument_list|,
name|change
argument_list|,
literal|null
argument_list|,
name|ps
argument_list|,
name|commit
argument_list|,
name|labelTypes
argument_list|,
name|info
argument_list|,
name|reviewers
argument_list|)
expr_stmt|;
block|}
DECL|method|insertChange (ReviewDb db, Change change, ChangeMessage changeMessage, PatchSet ps, RevCommit commit, LabelTypes labelTypes, PatchSetInfo info, Set<Account.Id> reviewers)
specifier|public
name|void
name|insertChange
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|Change
name|change
parameter_list|,
name|ChangeMessage
name|changeMessage
parameter_list|,
name|PatchSet
name|ps
parameter_list|,
name|RevCommit
name|commit
parameter_list|,
name|LabelTypes
name|labelTypes
parameter_list|,
name|PatchSetInfo
name|info
parameter_list|,
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|reviewers
parameter_list|)
throws|throws
name|OrmException
block|{
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
name|ps
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
name|ps
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
name|ChangeUtil
operator|.
name|updateTrackingIds
argument_list|(
name|db
argument_list|,
name|change
argument_list|,
name|trackingFooters
argument_list|,
name|commit
operator|.
name|getFooterLines
argument_list|()
argument_list|)
expr_stmt|;
name|approvalsUtil
operator|.
name|addReviewers
argument_list|(
name|db
argument_list|,
name|labelTypes
argument_list|,
name|change
argument_list|,
name|ps
argument_list|,
name|info
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
if|if
condition|(
name|changeMessage
operator|!=
literal|null
condition|)
block|{
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
name|changeMessage
argument_list|)
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
name|gitRefUpdated
operator|.
name|fire
argument_list|(
name|change
operator|.
name|getProject
argument_list|()
argument_list|,
name|ps
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
name|hooks
operator|.
name|doPatchsetCreatedHook
argument_list|(
name|change
argument_list|,
name|ps
argument_list|,
name|db
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

