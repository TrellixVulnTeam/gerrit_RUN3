begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2012 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.git
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|git
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
name|git
operator|.
name|MergeUtil
operator|.
name|canFastForward
import|;
end_import

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
name|git
operator|.
name|MergeUtil
operator|.
name|canMerge
import|;
end_import

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
name|git
operator|.
name|MergeUtil
operator|.
name|commit
import|;
end_import

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
name|git
operator|.
name|MergeUtil
operator|.
name|createDryRunInserter
import|;
end_import

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
name|git
operator|.
name|MergeUtil
operator|.
name|hasMissingDependencies
import|;
end_import

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
name|git
operator|.
name|MergeUtil
operator|.
name|markCleanMerges
import|;
end_import

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
name|git
operator|.
name|MergeUtil
operator|.
name|mergeOneCommit
import|;
end_import

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
name|git
operator|.
name|MergeUtil
operator|.
name|newThreeWayMerger
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
name|common
operator|.
name|data
operator|.
name|ApprovalTypes
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
name|lib
operator|.
name|PersonIdent
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
name|RefUpdate
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
name|merge
operator|.
name|Merger
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
name|merge
operator|.
name|ThreeWayMerger
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
name|FooterKey
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

begin_class
DECL|class|CherryPick
specifier|public
class|class
name|CherryPick
extends|extends
name|SubmitStrategy
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
name|CherryPick
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|CRVW
specifier|private
specifier|static
specifier|final
name|ApprovalCategory
operator|.
name|Id
name|CRVW
init|=
comment|//
operator|new
name|ApprovalCategory
operator|.
name|Id
argument_list|(
literal|"CRVW"
argument_list|)
decl_stmt|;
DECL|field|VRIF
specifier|private
specifier|static
specifier|final
name|ApprovalCategory
operator|.
name|Id
name|VRIF
init|=
comment|//
operator|new
name|ApprovalCategory
operator|.
name|Id
argument_list|(
literal|"VRIF"
argument_list|)
decl_stmt|;
DECL|field|REVIEWED_ON
specifier|private
specifier|static
specifier|final
name|FooterKey
name|REVIEWED_ON
init|=
operator|new
name|FooterKey
argument_list|(
literal|"Reviewed-on"
argument_list|)
decl_stmt|;
DECL|field|CHANGE_ID
specifier|private
specifier|static
specifier|final
name|FooterKey
name|CHANGE_ID
init|=
operator|new
name|FooterKey
argument_list|(
literal|"Change-Id"
argument_list|)
decl_stmt|;
DECL|field|patchSetInfoFactory
specifier|private
specifier|final
name|PatchSetInfoFactory
name|patchSetInfoFactory
decl_stmt|;
DECL|field|urlProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|String
argument_list|>
name|urlProvider
decl_stmt|;
DECL|field|approvalTypes
specifier|private
specifier|final
name|ApprovalTypes
name|approvalTypes
decl_stmt|;
DECL|field|replication
specifier|private
specifier|final
name|GitReferenceUpdated
name|replication
decl_stmt|;
DECL|field|newCommits
specifier|private
specifier|final
name|Map
argument_list|<
name|Change
operator|.
name|Id
argument_list|,
name|CodeReviewCommit
argument_list|>
name|newCommits
decl_stmt|;
DECL|method|CherryPick (final SubmitStrategy.Arguments args, final PatchSetInfoFactory patchSetInfoFactory, final Provider<String> urlProvider, final ApprovalTypes approvalTypes, final GitReferenceUpdated replication)
name|CherryPick
parameter_list|(
specifier|final
name|SubmitStrategy
operator|.
name|Arguments
name|args
parameter_list|,
specifier|final
name|PatchSetInfoFactory
name|patchSetInfoFactory
parameter_list|,
specifier|final
name|Provider
argument_list|<
name|String
argument_list|>
name|urlProvider
parameter_list|,
specifier|final
name|ApprovalTypes
name|approvalTypes
parameter_list|,
specifier|final
name|GitReferenceUpdated
name|replication
parameter_list|)
block|{
name|super
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|this
operator|.
name|patchSetInfoFactory
operator|=
name|patchSetInfoFactory
expr_stmt|;
name|this
operator|.
name|urlProvider
operator|=
name|urlProvider
expr_stmt|;
name|this
operator|.
name|approvalTypes
operator|=
name|approvalTypes
expr_stmt|;
name|this
operator|.
name|replication
operator|=
name|replication
expr_stmt|;
name|this
operator|.
name|newCommits
operator|=
operator|new
name|HashMap
argument_list|<
name|Change
operator|.
name|Id
argument_list|,
name|CodeReviewCommit
argument_list|>
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|_run (final CodeReviewCommit mergeTip, final List<CodeReviewCommit> toMerge)
specifier|protected
name|CodeReviewCommit
name|_run
parameter_list|(
specifier|final
name|CodeReviewCommit
name|mergeTip
parameter_list|,
specifier|final
name|List
argument_list|<
name|CodeReviewCommit
argument_list|>
name|toMerge
parameter_list|)
throws|throws
name|MergeException
block|{
name|CodeReviewCommit
name|newMergeTip
init|=
name|mergeTip
decl_stmt|;
while|while
condition|(
operator|!
name|toMerge
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
specifier|final
name|CodeReviewCommit
name|n
init|=
name|toMerge
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|ThreeWayMerger
name|m
init|=
name|newThreeWayMerger
argument_list|(
name|args
operator|.
name|repo
argument_list|,
name|args
operator|.
name|inserter
argument_list|,
name|args
operator|.
name|useContentMerge
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|newMergeTip
operator|==
literal|null
condition|)
block|{
comment|// The branch is unborn. Take a fast-forward resolution to
comment|// create the branch.
comment|//
name|newMergeTip
operator|=
name|n
expr_stmt|;
name|n
operator|.
name|statusCode
operator|=
name|CommitMergeStatus
operator|.
name|CLEAN_MERGE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|n
operator|.
name|getParentCount
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// Refuse to merge a root commit into an existing branch,
comment|// we cannot obtain a delta for the cherry-pick to apply.
comment|//
name|n
operator|.
name|statusCode
operator|=
name|CommitMergeStatus
operator|.
name|CANNOT_CHERRY_PICK_ROOT
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|n
operator|.
name|getParentCount
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// If there is only one parent, a cherry-pick can be done by
comment|// taking the delta relative to that one parent and redoing
comment|// that on the current merge tip.
comment|//
name|m
operator|.
name|setBase
argument_list|(
name|n
operator|.
name|getParent
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|m
operator|.
name|merge
argument_list|(
name|newMergeTip
argument_list|,
name|n
argument_list|)
condition|)
block|{
name|newMergeTip
operator|=
name|writeCherryPickCommit
argument_list|(
name|m
argument_list|,
name|newMergeTip
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|n
operator|.
name|statusCode
operator|=
name|CommitMergeStatus
operator|.
name|PATH_CONFLICT
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// There are multiple parents, so this is a merge commit. We
comment|// don't want to cherry-pick the merge as clients can't easily
comment|// rebase their history with that merge present and replaced
comment|// by an equivalent merge with a different first parent. So
comment|// instead behave as though MERGE_IF_NECESSARY was configured.
comment|//
if|if
condition|(
operator|!
name|hasMissingDependencies
argument_list|(
name|args
operator|.
name|mergeSorter
argument_list|,
name|n
argument_list|)
condition|)
block|{
if|if
condition|(
name|args
operator|.
name|rw
operator|.
name|isMergedInto
argument_list|(
name|newMergeTip
argument_list|,
name|n
argument_list|)
condition|)
block|{
name|newMergeTip
operator|=
name|n
expr_stmt|;
block|}
else|else
block|{
name|newMergeTip
operator|=
name|mergeOneCommit
argument_list|(
name|args
operator|.
name|db
argument_list|,
name|args
operator|.
name|identifiedUserFactory
argument_list|,
name|args
operator|.
name|myIdent
argument_list|,
name|args
operator|.
name|repo
argument_list|,
name|args
operator|.
name|rw
argument_list|,
name|args
operator|.
name|inserter
argument_list|,
name|args
operator|.
name|canMergeFlag
argument_list|,
name|args
operator|.
name|useContentMerge
argument_list|,
name|args
operator|.
name|destBranch
argument_list|,
name|newMergeTip
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
specifier|final
name|PatchSetApproval
name|submitApproval
init|=
name|markCleanMerges
argument_list|(
name|args
operator|.
name|db
argument_list|,
name|args
operator|.
name|rw
argument_list|,
name|args
operator|.
name|canMergeFlag
argument_list|,
name|newMergeTip
argument_list|,
name|args
operator|.
name|alreadyAccepted
argument_list|)
decl_stmt|;
name|setRefLogIdent
argument_list|(
name|submitApproval
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// One or more dependencies were not met. The status was
comment|// already marked on the commit so we have nothing further
comment|// to perform at this time.
comment|//
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MergeException
argument_list|(
literal|"Cannot merge "
operator|+
name|n
operator|.
name|name
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MergeException
argument_list|(
literal|"Cannot merge "
operator|+
name|n
operator|.
name|name
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|newMergeTip
return|;
block|}
annotation|@
name|Override
DECL|method|getNewCommits ()
specifier|public
name|Map
argument_list|<
name|Change
operator|.
name|Id
argument_list|,
name|CodeReviewCommit
argument_list|>
name|getNewCommits
parameter_list|()
block|{
return|return
name|newCommits
return|;
block|}
annotation|@
name|Override
DECL|method|dryRun (final CodeReviewCommit mergeTip, final CodeReviewCommit toMerge)
specifier|public
name|boolean
name|dryRun
parameter_list|(
specifier|final
name|CodeReviewCommit
name|mergeTip
parameter_list|,
specifier|final
name|CodeReviewCommit
name|toMerge
parameter_list|)
throws|throws
name|MergeException
block|{
return|return
name|canCherryPick
argument_list|(
name|mergeTip
argument_list|,
name|toMerge
argument_list|)
return|;
block|}
DECL|method|canCherryPick (final CodeReviewCommit mergeTip, final CodeReviewCommit toMerge)
specifier|private
name|boolean
name|canCherryPick
parameter_list|(
specifier|final
name|CodeReviewCommit
name|mergeTip
parameter_list|,
specifier|final
name|CodeReviewCommit
name|toMerge
parameter_list|)
throws|throws
name|MergeException
block|{
if|if
condition|(
name|mergeTip
operator|==
literal|null
condition|)
block|{
comment|// The branch is unborn. Fast-forward is possible.
comment|//
return|return
literal|true
return|;
block|}
if|if
condition|(
name|toMerge
operator|.
name|getParentCount
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// Refuse to merge a root commit into an existing branch,
comment|// we cannot obtain a delta for the cherry-pick to apply.
comment|//
return|return
literal|false
return|;
block|}
if|if
condition|(
name|toMerge
operator|.
name|getParentCount
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// If there is only one parent, a cherry-pick can be done by
comment|// taking the delta relative to that one parent and redoing
comment|// that on the current merge tip.
comment|//
try|try
block|{
specifier|final
name|ThreeWayMerger
name|m
init|=
name|newThreeWayMerger
argument_list|(
name|args
operator|.
name|repo
argument_list|,
name|createDryRunInserter
argument_list|()
argument_list|,
name|args
operator|.
name|useContentMerge
argument_list|)
decl_stmt|;
name|m
operator|.
name|setBase
argument_list|(
name|toMerge
operator|.
name|getParent
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|m
operator|.
name|merge
argument_list|(
name|mergeTip
argument_list|,
name|toMerge
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MergeException
argument_list|(
literal|"Cannot merge "
operator|+
name|toMerge
operator|.
name|name
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|// There are multiple parents, so this is a merge commit. We
comment|// don't want to cherry-pick the merge as clients can't easily
comment|// rebase their history with that merge present and replaced
comment|// by an equivalent merge with a different first parent. So
comment|// instead behave as though MERGE_IF_NECESSARY was configured.
comment|//
return|return
name|canFastForward
argument_list|(
name|args
operator|.
name|mergeSorter
argument_list|,
name|mergeTip
argument_list|,
name|args
operator|.
name|rw
argument_list|,
name|toMerge
argument_list|)
operator|||
name|canMerge
argument_list|(
name|args
operator|.
name|mergeSorter
argument_list|,
name|args
operator|.
name|repo
argument_list|,
name|args
operator|.
name|useContentMerge
argument_list|,
name|mergeTip
argument_list|,
name|toMerge
argument_list|)
return|;
block|}
DECL|method|writeCherryPickCommit (final Merger m, final CodeReviewCommit mergeTip, final CodeReviewCommit n)
specifier|private
name|CodeReviewCommit
name|writeCherryPickCommit
parameter_list|(
specifier|final
name|Merger
name|m
parameter_list|,
specifier|final
name|CodeReviewCommit
name|mergeTip
parameter_list|,
specifier|final
name|CodeReviewCommit
name|n
parameter_list|)
throws|throws
name|IOException
throws|,
name|OrmException
block|{
name|args
operator|.
name|rw
operator|.
name|parseBody
argument_list|(
name|n
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|FooterLine
argument_list|>
name|footers
init|=
name|n
operator|.
name|getFooterLines
argument_list|()
decl_stmt|;
specifier|final
name|StringBuilder
name|msgbuf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|msgbuf
operator|.
name|append
argument_list|(
name|n
operator|.
name|getFullMessage
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|msgbuf
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// WTF, an empty commit message?
name|msgbuf
operator|.
name|append
argument_list|(
literal|"<no commit message provided>"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|msgbuf
operator|.
name|charAt
argument_list|(
name|msgbuf
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|!=
literal|'\n'
condition|)
block|{
comment|// Missing a trailing LF? Correct it (perhaps the editor was broken).
name|msgbuf
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|footers
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// Doesn't end in a "Signed-off-by: ..." style line? Add another line
comment|// break to start a new paragraph for the reviewed-by tag lines.
comment|//
name|msgbuf
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|contains
argument_list|(
name|footers
argument_list|,
name|CHANGE_ID
argument_list|,
name|n
operator|.
name|change
operator|.
name|getKey
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
condition|)
block|{
name|msgbuf
operator|.
name|append
argument_list|(
name|CHANGE_ID
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|msgbuf
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
expr_stmt|;
name|msgbuf
operator|.
name|append
argument_list|(
name|n
operator|.
name|change
operator|.
name|getKey
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|msgbuf
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|siteUrl
init|=
name|urlProvider
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|siteUrl
operator|!=
literal|null
condition|)
block|{
specifier|final
name|String
name|url
init|=
name|siteUrl
operator|+
name|n
operator|.
name|patchsetId
operator|.
name|getParentKey
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|contains
argument_list|(
name|footers
argument_list|,
name|REVIEWED_ON
argument_list|,
name|url
argument_list|)
condition|)
block|{
name|msgbuf
operator|.
name|append
argument_list|(
name|REVIEWED_ON
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|msgbuf
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
expr_stmt|;
name|msgbuf
operator|.
name|append
argument_list|(
name|url
argument_list|)
expr_stmt|;
name|msgbuf
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
block|}
name|PatchSetApproval
name|submitAudit
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|PatchSetApproval
argument_list|>
name|approvalList
init|=
literal|null
decl_stmt|;
try|try
block|{
name|approvalList
operator|=
name|args
operator|.
name|db
operator|.
name|patchSetApprovals
argument_list|()
operator|.
name|byPatchSet
argument_list|(
name|n
operator|.
name|patchsetId
argument_list|)
operator|.
name|toList
argument_list|()
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|approvalList
argument_list|,
operator|new
name|Comparator
argument_list|<
name|PatchSetApproval
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
specifier|final
name|PatchSetApproval
name|a
parameter_list|,
specifier|final
name|PatchSetApproval
name|b
parameter_list|)
block|{
return|return
name|a
operator|.
name|getGranted
argument_list|()
operator|.
name|compareTo
argument_list|(
name|b
operator|.
name|getGranted
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|PatchSetApproval
name|a
range|:
name|approvalList
control|)
block|{
if|if
condition|(
name|a
operator|.
name|getValue
argument_list|()
operator|<=
literal|0
condition|)
block|{
comment|// Negative votes aren't counted.
continue|continue;
block|}
if|if
condition|(
name|ApprovalCategory
operator|.
name|SUBMIT
operator|.
name|equals
argument_list|(
name|a
operator|.
name|getCategoryId
argument_list|()
argument_list|)
condition|)
block|{
comment|// Submit is treated specially, below (becomes committer)
comment|//
if|if
condition|(
name|submitAudit
operator|==
literal|null
operator|||
name|a
operator|.
name|getGranted
argument_list|()
operator|.
name|compareTo
argument_list|(
name|submitAudit
operator|.
name|getGranted
argument_list|()
argument_list|)
operator|>
literal|0
condition|)
block|{
name|submitAudit
operator|=
name|a
expr_stmt|;
block|}
continue|continue;
block|}
specifier|final
name|Account
name|acc
init|=
name|args
operator|.
name|identifiedUserFactory
operator|.
name|create
argument_list|(
name|a
operator|.
name|getAccountId
argument_list|()
argument_list|)
operator|.
name|getAccount
argument_list|()
decl_stmt|;
specifier|final
name|StringBuilder
name|identbuf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|acc
operator|.
name|getFullName
argument_list|()
operator|!=
literal|null
operator|&&
name|acc
operator|.
name|getFullName
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|identbuf
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|identbuf
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|identbuf
operator|.
name|append
argument_list|(
name|acc
operator|.
name|getFullName
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|acc
operator|.
name|getPreferredEmail
argument_list|()
operator|!=
literal|null
operator|&&
name|acc
operator|.
name|getPreferredEmail
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|isSignedOffBy
argument_list|(
name|footers
argument_list|,
name|acc
operator|.
name|getPreferredEmail
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|identbuf
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|identbuf
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|identbuf
operator|.
name|append
argument_list|(
literal|'<'
argument_list|)
expr_stmt|;
name|identbuf
operator|.
name|append
argument_list|(
name|acc
operator|.
name|getPreferredEmail
argument_list|()
argument_list|)
expr_stmt|;
name|identbuf
operator|.
name|append
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|identbuf
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// Nothing reasonable to describe them by? Ignore them.
continue|continue;
block|}
specifier|final
name|String
name|tag
decl_stmt|;
if|if
condition|(
name|CRVW
operator|.
name|equals
argument_list|(
name|a
operator|.
name|getCategoryId
argument_list|()
argument_list|)
condition|)
block|{
name|tag
operator|=
literal|"Reviewed-by"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|VRIF
operator|.
name|equals
argument_list|(
name|a
operator|.
name|getCategoryId
argument_list|()
argument_list|)
condition|)
block|{
name|tag
operator|=
literal|"Tested-by"
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|ApprovalType
name|at
init|=
name|approvalTypes
operator|.
name|byId
argument_list|(
name|a
operator|.
name|getCategoryId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|at
operator|==
literal|null
condition|)
block|{
comment|// A deprecated/deleted approval type, ignore it.
continue|continue;
block|}
name|tag
operator|=
name|at
operator|.
name|getCategory
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|replace
argument_list|(
literal|' '
argument_list|,
literal|'-'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|contains
argument_list|(
name|footers
argument_list|,
operator|new
name|FooterKey
argument_list|(
name|tag
argument_list|)
argument_list|,
name|identbuf
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
name|msgbuf
operator|.
name|append
argument_list|(
name|tag
argument_list|)
expr_stmt|;
name|msgbuf
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
expr_stmt|;
name|msgbuf
operator|.
name|append
argument_list|(
name|identbuf
argument_list|)
expr_stmt|;
name|msgbuf
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Can't read approval records for "
operator|+
name|n
operator|.
name|patchsetId
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
specifier|final
name|CommitBuilder
name|mergeCommit
init|=
operator|new
name|CommitBuilder
argument_list|()
decl_stmt|;
name|mergeCommit
operator|.
name|setTreeId
argument_list|(
name|m
operator|.
name|getResultTreeId
argument_list|()
argument_list|)
expr_stmt|;
name|mergeCommit
operator|.
name|setParentId
argument_list|(
name|mergeTip
argument_list|)
expr_stmt|;
name|mergeCommit
operator|.
name|setAuthor
argument_list|(
name|n
operator|.
name|getAuthorIdent
argument_list|()
argument_list|)
expr_stmt|;
name|mergeCommit
operator|.
name|setCommitter
argument_list|(
name|toCommitterIdent
argument_list|(
name|submitAudit
argument_list|)
argument_list|)
expr_stmt|;
name|mergeCommit
operator|.
name|setMessage
argument_list|(
name|msgbuf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|ObjectId
name|id
init|=
name|commit
argument_list|(
name|args
operator|.
name|inserter
argument_list|,
name|mergeCommit
argument_list|)
decl_stmt|;
specifier|final
name|CodeReviewCommit
name|newCommit
init|=
operator|(
name|CodeReviewCommit
operator|)
name|args
operator|.
name|rw
operator|.
name|parseCommit
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|n
operator|.
name|change
operator|.
name|nextPatchSetId
argument_list|()
expr_stmt|;
specifier|final
name|PatchSet
name|ps
init|=
operator|new
name|PatchSet
argument_list|(
name|n
operator|.
name|change
operator|.
name|currPatchSetId
argument_list|()
argument_list|)
decl_stmt|;
name|ps
operator|.
name|setCreatedOn
argument_list|(
operator|new
name|Timestamp
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|ps
operator|.
name|setUploader
argument_list|(
name|submitAudit
operator|.
name|getAccountId
argument_list|()
argument_list|)
expr_stmt|;
name|ps
operator|.
name|setRevision
argument_list|(
operator|new
name|RevId
argument_list|(
name|id
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|insertAncestors
argument_list|(
name|ps
operator|.
name|getId
argument_list|()
argument_list|,
name|newCommit
argument_list|)
expr_stmt|;
name|args
operator|.
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
name|n
operator|.
name|change
operator|.
name|setCurrentPatchSet
argument_list|(
name|patchSetInfoFactory
operator|.
name|get
argument_list|(
name|newCommit
argument_list|,
name|ps
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|args
operator|.
name|db
operator|.
name|changes
argument_list|()
operator|.
name|update
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|n
operator|.
name|change
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|approvalList
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|PatchSetApproval
name|a
range|:
name|approvalList
control|)
block|{
name|args
operator|.
name|db
operator|.
name|patchSetApprovals
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
operator|new
name|PatchSetApproval
argument_list|(
name|ps
operator|.
name|getId
argument_list|()
argument_list|,
name|a
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|RefUpdate
name|ru
init|=
name|args
operator|.
name|repo
operator|.
name|updateRef
argument_list|(
name|ps
operator|.
name|getRefName
argument_list|()
argument_list|)
decl_stmt|;
name|ru
operator|.
name|setExpectedOldObjectId
argument_list|(
name|ObjectId
operator|.
name|zeroId
argument_list|()
argument_list|)
expr_stmt|;
name|ru
operator|.
name|setNewObjectId
argument_list|(
name|newCommit
argument_list|)
expr_stmt|;
name|ru
operator|.
name|disableRefLog
argument_list|()
expr_stmt|;
if|if
condition|(
name|ru
operator|.
name|update
argument_list|(
name|args
operator|.
name|rw
argument_list|)
operator|!=
name|RefUpdate
operator|.
name|Result
operator|.
name|NEW
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Failed to create ref %s in %s: %s"
argument_list|,
name|ps
operator|.
name|getRefName
argument_list|()
argument_list|,
name|n
operator|.
name|change
operator|.
name|getDest
argument_list|()
operator|.
name|getParentKey
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|ru
operator|.
name|getResult
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
name|replication
operator|.
name|fire
argument_list|(
name|n
operator|.
name|change
operator|.
name|getProject
argument_list|()
argument_list|,
name|ru
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|newCommit
operator|.
name|copyFrom
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|newCommit
operator|.
name|statusCode
operator|=
name|CommitMergeStatus
operator|.
name|CLEAN_PICK
expr_stmt|;
name|newCommits
operator|.
name|put
argument_list|(
name|newCommit
operator|.
name|patchsetId
operator|.
name|getParentKey
argument_list|()
argument_list|,
name|newCommit
argument_list|)
expr_stmt|;
name|setRefLogIdent
argument_list|(
name|submitAudit
argument_list|)
expr_stmt|;
return|return
name|newCommit
return|;
block|}
DECL|method|insertAncestors (PatchSet.Id id, RevCommit src)
specifier|private
name|void
name|insertAncestors
parameter_list|(
name|PatchSet
operator|.
name|Id
name|id
parameter_list|,
name|RevCommit
name|src
parameter_list|)
throws|throws
name|OrmException
block|{
specifier|final
name|int
name|cnt
init|=
name|src
operator|.
name|getParentCount
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|PatchSetAncestor
argument_list|>
name|toInsert
init|=
operator|new
name|ArrayList
argument_list|<
name|PatchSetAncestor
argument_list|>
argument_list|(
name|cnt
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|p
init|=
literal|0
init|;
name|p
operator|<
name|cnt
condition|;
name|p
operator|++
control|)
block|{
name|PatchSetAncestor
name|a
decl_stmt|;
name|a
operator|=
operator|new
name|PatchSetAncestor
argument_list|(
operator|new
name|PatchSetAncestor
operator|.
name|Id
argument_list|(
name|id
argument_list|,
name|p
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|a
operator|.
name|setAncestorRevision
argument_list|(
operator|new
name|RevId
argument_list|(
name|src
operator|.
name|getParent
argument_list|(
name|p
argument_list|)
operator|.
name|getId
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|toInsert
operator|.
name|add
argument_list|(
name|a
argument_list|)
expr_stmt|;
block|}
name|args
operator|.
name|db
operator|.
name|patchSetAncestors
argument_list|()
operator|.
name|insert
argument_list|(
name|toInsert
argument_list|)
expr_stmt|;
block|}
DECL|method|contains (List<FooterLine> footers, FooterKey key, String val)
specifier|private
name|boolean
name|contains
parameter_list|(
name|List
argument_list|<
name|FooterLine
argument_list|>
name|footers
parameter_list|,
name|FooterKey
name|key
parameter_list|,
name|String
name|val
parameter_list|)
block|{
for|for
control|(
specifier|final
name|FooterLine
name|line
range|:
name|footers
control|)
block|{
if|if
condition|(
name|line
operator|.
name|matches
argument_list|(
name|key
argument_list|)
operator|&&
name|val
operator|.
name|equals
argument_list|(
name|line
operator|.
name|getValue
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|isSignedOffBy (List<FooterLine> footers, String email)
specifier|private
name|boolean
name|isSignedOffBy
parameter_list|(
name|List
argument_list|<
name|FooterLine
argument_list|>
name|footers
parameter_list|,
name|String
name|email
parameter_list|)
block|{
for|for
control|(
specifier|final
name|FooterLine
name|line
range|:
name|footers
control|)
block|{
if|if
condition|(
name|line
operator|.
name|matches
argument_list|(
name|FooterKey
operator|.
name|SIGNED_OFF_BY
argument_list|)
operator|&&
name|email
operator|.
name|equals
argument_list|(
name|line
operator|.
name|getEmailAddress
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|toCommitterIdent (final PatchSetApproval audit)
specifier|private
name|PersonIdent
name|toCommitterIdent
parameter_list|(
specifier|final
name|PatchSetApproval
name|audit
parameter_list|)
block|{
if|if
condition|(
name|audit
operator|!=
literal|null
condition|)
block|{
return|return
name|args
operator|.
name|identifiedUserFactory
operator|.
name|create
argument_list|(
name|audit
operator|.
name|getAccountId
argument_list|()
argument_list|)
operator|.
name|newCommitterIdent
argument_list|(
name|audit
operator|.
name|getGranted
argument_list|()
argument_list|,
name|args
operator|.
name|myIdent
operator|.
name|getTimeZone
argument_list|()
argument_list|)
return|;
block|}
return|return
name|args
operator|.
name|myIdent
return|;
block|}
block|}
end_class

end_unit

