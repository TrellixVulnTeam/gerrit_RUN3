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
DECL|package|com.google.gerrit.acceptance.rest.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|rest
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
name|common
operator|.
name|truth
operator|.
name|Truth
operator|.
name|assertThat
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
name|extensions
operator|.
name|client
operator|.
name|ListChangesOption
operator|.
name|CURRENT_REVISION
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
name|testing
operator|.
name|GerritJUnit
operator|.
name|assertThrows
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
name|Throwables
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
name|ImmutableList
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
name|acceptance
operator|.
name|ExtensionRegistry
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
name|acceptance
operator|.
name|ExtensionRegistry
operator|.
name|Registration
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
name|acceptance
operator|.
name|PushOneCommit
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
name|acceptance
operator|.
name|TestProjectInput
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
name|acceptance
operator|.
name|testsuite
operator|.
name|project
operator|.
name|ProjectOperations
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
name|FooterConstants
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
name|client
operator|.
name|InheritableBoolean
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
name|client
operator|.
name|SubmitType
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
name|registration
operator|.
name|DynamicItem
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
name|config
operator|.
name|UrlFormatter
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
name|ChangeMessageModifier
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
name|query
operator|.
name|change
operator|.
name|ChangeData
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
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|SubmitByRebaseAlwaysIT
specifier|public
class|class
name|SubmitByRebaseAlwaysIT
extends|extends
name|AbstractSubmitByRebase
block|{
DECL|field|urlFormatter
annotation|@
name|Inject
specifier|private
name|DynamicItem
argument_list|<
name|UrlFormatter
argument_list|>
name|urlFormatter
decl_stmt|;
DECL|field|projectOperations
annotation|@
name|Inject
specifier|private
name|ProjectOperations
name|projectOperations
decl_stmt|;
DECL|field|extensionRegistry
annotation|@
name|Inject
specifier|private
name|ExtensionRegistry
name|extensionRegistry
decl_stmt|;
annotation|@
name|Override
DECL|method|getSubmitType ()
specifier|protected
name|SubmitType
name|getSubmitType
parameter_list|()
block|{
return|return
name|SubmitType
operator|.
name|REBASE_ALWAYS
return|;
block|}
annotation|@
name|Test
annotation|@
name|TestProjectInput
argument_list|(
name|useContentMerge
operator|=
name|InheritableBoolean
operator|.
name|TRUE
argument_list|)
DECL|method|submitWithPossibleFastForward ()
specifier|public
name|void
name|submitWithPossibleFastForward
parameter_list|()
throws|throws
name|Throwable
block|{
name|RevCommit
name|oldHead
init|=
name|projectOperations
operator|.
name|project
argument_list|(
name|project
argument_list|)
operator|.
name|getHead
argument_list|(
literal|"master"
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|change
init|=
name|createChange
argument_list|()
decl_stmt|;
name|submit
argument_list|(
name|change
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|RevCommit
name|head
init|=
name|projectOperations
operator|.
name|project
argument_list|(
name|project
argument_list|)
operator|.
name|getHead
argument_list|(
literal|"master"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|head
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|isNotEqualTo
argument_list|(
name|change
operator|.
name|getCommit
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|head
operator|.
name|getParent
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|oldHead
argument_list|)
expr_stmt|;
name|assertApproved
argument_list|(
name|change
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertCurrentRevision
argument_list|(
name|change
operator|.
name|getChangeId
argument_list|()
argument_list|,
literal|2
argument_list|,
name|head
argument_list|)
expr_stmt|;
name|assertSubmitter
argument_list|(
name|change
operator|.
name|getChangeId
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertSubmitter
argument_list|(
name|change
operator|.
name|getChangeId
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertPersonEquals
argument_list|(
name|admin
operator|.
name|newIdent
argument_list|()
argument_list|,
name|head
operator|.
name|getAuthorIdent
argument_list|()
argument_list|)
expr_stmt|;
name|assertPersonEquals
argument_list|(
name|admin
operator|.
name|newIdent
argument_list|()
argument_list|,
name|head
operator|.
name|getCommitterIdent
argument_list|()
argument_list|)
expr_stmt|;
name|assertRefUpdatedEvents
argument_list|(
name|oldHead
argument_list|,
name|head
argument_list|)
expr_stmt|;
name|assertChangeMergedEvents
argument_list|(
name|change
operator|.
name|getChangeId
argument_list|()
argument_list|,
name|head
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|TestProjectInput
argument_list|(
name|useContentMerge
operator|=
name|InheritableBoolean
operator|.
name|TRUE
argument_list|)
DECL|method|alwaysAddFooters ()
specifier|public
name|void
name|alwaysAddFooters
parameter_list|()
throws|throws
name|Throwable
block|{
name|PushOneCommit
operator|.
name|Result
name|change1
init|=
name|createChange
argument_list|()
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|change2
init|=
name|createChange
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|getCurrentCommit
argument_list|(
name|change1
argument_list|)
operator|.
name|getFooterLines
argument_list|(
name|FooterConstants
operator|.
name|REVIEWED_BY
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|getCurrentCommit
argument_list|(
name|change2
argument_list|)
operator|.
name|getFooterLines
argument_list|(
name|FooterConstants
operator|.
name|REVIEWED_BY
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
comment|// change1 is a fast-forward, but should be rebased in cherry pick style
comment|// anyway, making change2 not a fast-forward, requiring a rebase.
name|approve
argument_list|(
name|change1
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|submit
argument_list|(
name|change2
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
comment|// ... but both changes should get reviewed-by footers.
name|assertLatestRevisionHasFooters
argument_list|(
name|change1
argument_list|)
expr_stmt|;
name|assertLatestRevisionHasFooters
argument_list|(
name|change2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|rebaseInvokesChangeMessageModifiers ()
specifier|public
name|void
name|rebaseInvokesChangeMessageModifiers
parameter_list|()
throws|throws
name|Throwable
block|{
name|ChangeMessageModifier
name|modifier1
init|=
parameter_list|(
name|msg
parameter_list|,
name|orig
parameter_list|,
name|tip
parameter_list|,
name|dest
parameter_list|)
lambda|->
name|msg
operator|+
literal|"This-change-before-rebase: "
operator|+
name|orig
operator|.
name|name
argument_list|()
operator|+
literal|"\n"
decl_stmt|;
name|ChangeMessageModifier
name|modifier2
init|=
parameter_list|(
name|msg
parameter_list|,
name|orig
parameter_list|,
name|tip
parameter_list|,
name|dest
parameter_list|)
lambda|->
name|msg
operator|+
literal|"Previous-step-tip: "
operator|+
name|tip
operator|.
name|name
argument_list|()
operator|+
literal|"\n"
decl_stmt|;
name|ChangeMessageModifier
name|modifier3
init|=
parameter_list|(
name|msg
parameter_list|,
name|orig
parameter_list|,
name|tip
parameter_list|,
name|dest
parameter_list|)
lambda|->
name|msg
operator|+
literal|"Dest: "
operator|+
name|dest
operator|.
name|shortName
argument_list|()
operator|+
literal|"\n"
decl_stmt|;
try|try
init|(
name|Registration
name|registration
init|=
name|extensionRegistry
operator|.
name|newRegistration
argument_list|()
operator|.
name|add
argument_list|(
name|modifier1
argument_list|)
operator|.
name|add
argument_list|(
name|modifier2
argument_list|)
operator|.
name|add
argument_list|(
name|modifier3
argument_list|)
init|)
block|{
name|ImmutableList
argument_list|<
name|PushOneCommit
operator|.
name|Result
argument_list|>
name|changes
init|=
name|submitWithRebase
argument_list|(
name|admin
argument_list|)
decl_stmt|;
name|ChangeData
name|cd1
init|=
name|changes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getChange
argument_list|()
decl_stmt|;
name|ChangeData
name|cd2
init|=
name|changes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getChange
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|cd2
operator|.
name|patchSets
argument_list|()
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|String
name|change1CurrentCommit
init|=
name|cd1
operator|.
name|currentPatchSet
argument_list|()
operator|.
name|commitId
argument_list|()
operator|.
name|name
argument_list|()
decl_stmt|;
name|String
name|change2Ps1Commit
init|=
name|cd2
operator|.
name|patchSet
argument_list|(
name|PatchSet
operator|.
name|id
argument_list|(
name|cd2
operator|.
name|getId
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|)
operator|.
name|commitId
argument_list|()
operator|.
name|name
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|cd2
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|revision
argument_list|(
literal|2
argument_list|)
operator|.
name|commit
argument_list|(
literal|false
argument_list|)
operator|.
name|message
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"Change 2\n\n"
operator|+
operator|(
literal|"Change-Id: "
operator|+
name|cd2
operator|.
name|change
argument_list|()
operator|.
name|getKey
argument_list|()
operator|+
literal|"\n"
operator|)
operator|+
operator|(
literal|"Reviewed-on: "
operator|+
name|urlFormatter
operator|.
name|get
argument_list|()
operator|.
name|getChangeViewUrl
argument_list|(
name|project
argument_list|,
name|cd2
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
operator|+
literal|"\n"
operator|)
operator|+
literal|"Reviewed-by: Administrator<admin@example.com>\n"
operator|+
operator|(
literal|"This-change-before-rebase: "
operator|+
name|change2Ps1Commit
operator|+
literal|"\n"
operator|)
operator|+
operator|(
literal|"Previous-step-tip: "
operator|+
name|change1CurrentCommit
operator|+
literal|"\n"
operator|)
operator|+
literal|"Dest: master\n"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|failingChangeMessageModifierShortCircuits ()
specifier|public
name|void
name|failingChangeMessageModifierShortCircuits
parameter_list|()
throws|throws
name|Throwable
block|{
name|ChangeMessageModifier
name|modifier1
init|=
parameter_list|(
name|msg
parameter_list|,
name|orig
parameter_list|,
name|tip
parameter_list|,
name|dest
parameter_list|)
lambda|->
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"boom"
argument_list|)
throw|;
block|}
decl_stmt|;
name|ChangeMessageModifier
name|modifier2
init|=
parameter_list|(
name|msg
parameter_list|,
name|orig
parameter_list|,
name|tip
parameter_list|,
name|dest
parameter_list|)
lambda|->
name|msg
operator|+
literal|"A-footer: value\n"
decl_stmt|;
try|try
init|(
name|Registration
name|registration
init|=
name|extensionRegistry
operator|.
name|newRegistration
argument_list|()
operator|.
name|add
argument_list|(
name|modifier1
argument_list|)
operator|.
name|add
argument_list|(
name|modifier2
argument_list|)
init|)
block|{
name|ResourceConflictException
name|thrown
init|=
name|assertThrows
argument_list|(
name|ResourceConflictException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|submitWithRebase
argument_list|()
argument_list|)
decl_stmt|;
name|Throwable
name|cause
init|=
name|Throwables
operator|.
name|getRootCause
argument_list|(
name|thrown
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|cause
argument_list|)
operator|.
name|isInstanceOf
argument_list|(
name|RuntimeException
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cause
argument_list|)
operator|.
name|hasMessageThat
argument_list|()
operator|.
name|isEqualTo
argument_list|(
literal|"boom"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|changeMessageModifierReturningNullShortCircuits ()
specifier|public
name|void
name|changeMessageModifierReturningNullShortCircuits
parameter_list|()
throws|throws
name|Throwable
block|{
name|ChangeMessageModifier
name|modifier1
init|=
parameter_list|(
name|msg
parameter_list|,
name|orig
parameter_list|,
name|tip
parameter_list|,
name|dest
parameter_list|)
lambda|->
literal|null
decl_stmt|;
name|ChangeMessageModifier
name|modifier2
init|=
parameter_list|(
name|msg
parameter_list|,
name|orig
parameter_list|,
name|tip
parameter_list|,
name|dest
parameter_list|)
lambda|->
name|msg
operator|+
literal|"A-footer: value\n"
decl_stmt|;
try|try
init|(
name|Registration
name|registration
init|=
name|extensionRegistry
operator|.
name|newRegistration
argument_list|()
operator|.
name|add
argument_list|(
name|modifier1
argument_list|,
literal|"modifier-1"
argument_list|)
operator|.
name|add
argument_list|(
name|modifier2
argument_list|,
literal|"modifier-2"
argument_list|)
init|)
block|{
name|ResourceConflictException
name|thrown
init|=
name|assertThrows
argument_list|(
name|ResourceConflictException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|submitWithRebase
argument_list|()
argument_list|)
decl_stmt|;
name|Throwable
name|cause
init|=
name|Throwables
operator|.
name|getRootCause
argument_list|(
name|thrown
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|cause
argument_list|)
operator|.
name|isInstanceOf
argument_list|(
name|RuntimeException
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cause
argument_list|)
operator|.
name|hasMessageThat
argument_list|()
operator|.
name|isEqualTo
argument_list|(
name|modifier1
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|".onSubmit from plugin modifier-1 returned null instead of new commit"
operator|+
literal|" message"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertLatestRevisionHasFooters (PushOneCommit.Result change)
specifier|private
name|void
name|assertLatestRevisionHasFooters
parameter_list|(
name|PushOneCommit
operator|.
name|Result
name|change
parameter_list|)
throws|throws
name|Throwable
block|{
name|RevCommit
name|c
init|=
name|getCurrentCommit
argument_list|(
name|change
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|getFooterLines
argument_list|(
name|FooterConstants
operator|.
name|CHANGE_ID
argument_list|)
argument_list|)
operator|.
name|isNotEmpty
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|getFooterLines
argument_list|(
name|FooterConstants
operator|.
name|REVIEWED_BY
argument_list|)
argument_list|)
operator|.
name|isNotEmpty
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|getFooterLines
argument_list|(
name|FooterConstants
operator|.
name|REVIEWED_ON
argument_list|)
argument_list|)
operator|.
name|isNotEmpty
argument_list|()
expr_stmt|;
block|}
DECL|method|getCurrentCommit (PushOneCommit.Result change)
specifier|private
name|RevCommit
name|getCurrentCommit
parameter_list|(
name|PushOneCommit
operator|.
name|Result
name|change
parameter_list|)
throws|throws
name|Throwable
block|{
name|testRepo
operator|.
name|git
argument_list|()
operator|.
name|fetch
argument_list|()
operator|.
name|setRemote
argument_list|(
literal|"origin"
argument_list|)
operator|.
name|call
argument_list|()
expr_stmt|;
name|ChangeInfo
name|info
init|=
name|get
argument_list|(
name|change
operator|.
name|getChangeId
argument_list|()
argument_list|,
name|CURRENT_REVISION
argument_list|)
decl_stmt|;
name|RevCommit
name|c
init|=
name|testRepo
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
name|info
operator|.
name|currentRevision
argument_list|)
argument_list|)
decl_stmt|;
name|testRepo
operator|.
name|getRevWalk
argument_list|()
operator|.
name|parseBody
argument_list|(
name|c
argument_list|)
expr_stmt|;
return|return
name|c
return|;
block|}
block|}
end_class

end_unit

