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
name|checkArgument
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
name|notedb
operator|.
name|ChangeNoteUtil
operator|.
name|FOOTER_LABEL
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
name|notedb
operator|.
name|ChangeNoteUtil
operator|.
name|FOOTER_PATCH_SET
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
name|notedb
operator|.
name|ChangeNoteUtil
operator|.
name|FOOTER_STATUS
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
name|notedb
operator|.
name|ChangeNoteUtil
operator|.
name|FOOTER_SUBMITTED_WITH
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
name|notedb
operator|.
name|ChangeNoteUtil
operator|.
name|GERRIT_PLACEHOLDER_HOST
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
name|annotations
operator|.
name|VisibleForTesting
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
name|Optional
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
name|common
operator|.
name|collect
operator|.
name|Maps
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
name|SubmitRecord
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
name|Project
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
name|MetaDataUpdate
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
name|ProjectCache
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
name|util
operator|.
name|LabelVote
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|assistedinject
operator|.
name|AssistedInject
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
name|errors
operator|.
name|ConfigInvalidException
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
name|Repository
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
name|RevCommit
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
name|Comparator
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
name|Map
import|;
end_import

begin_comment
comment|/**  * A single delta to apply atomically to a change.  *<p>  * This delta becomes a single commit on the notes branch, so there are  * limitations on the set of modifications that can be handled in a single  * update. In particular, there is a single author and timestamp for each  * update.  *<p>  * This class is not thread-safe.  */
end_comment

begin_class
DECL|class|ChangeUpdate
specifier|public
class|class
name|ChangeUpdate
extends|extends
name|VersionedMetaData
block|{
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (ChangeControl ctl)
name|ChangeUpdate
name|create
parameter_list|(
name|ChangeControl
name|ctl
parameter_list|)
function_decl|;
DECL|method|create (ChangeControl ctl, Date when)
name|ChangeUpdate
name|create
parameter_list|(
name|ChangeControl
name|ctl
parameter_list|,
name|Date
name|when
parameter_list|)
function_decl|;
annotation|@
name|VisibleForTesting
DECL|method|create (ChangeControl ctl, Date when, Comparator<String> labelNameComparator)
name|ChangeUpdate
name|create
parameter_list|(
name|ChangeControl
name|ctl
parameter_list|,
name|Date
name|when
parameter_list|,
name|Comparator
argument_list|<
name|String
argument_list|>
name|labelNameComparator
parameter_list|)
function_decl|;
block|}
DECL|field|migration
specifier|private
specifier|final
name|NotesMigration
name|migration
decl_stmt|;
DECL|field|repoManager
specifier|private
specifier|final
name|GitRepositoryManager
name|repoManager
decl_stmt|;
DECL|field|accountCache
specifier|private
specifier|final
name|AccountCache
name|accountCache
decl_stmt|;
DECL|field|updateFactory
specifier|private
specifier|final
name|MetaDataUpdate
operator|.
name|User
name|updateFactory
decl_stmt|;
DECL|field|ctl
specifier|private
specifier|final
name|ChangeControl
name|ctl
decl_stmt|;
DECL|field|serverIdent
specifier|private
specifier|final
name|PersonIdent
name|serverIdent
decl_stmt|;
DECL|field|when
specifier|private
specifier|final
name|Date
name|when
decl_stmt|;
DECL|field|approvals
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Optional
argument_list|<
name|Short
argument_list|>
argument_list|>
name|approvals
decl_stmt|;
DECL|field|reviewers
specifier|private
specifier|final
name|Map
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|ReviewerState
argument_list|>
name|reviewers
decl_stmt|;
DECL|field|status
specifier|private
name|Change
operator|.
name|Status
name|status
decl_stmt|;
DECL|field|subject
specifier|private
name|String
name|subject
decl_stmt|;
DECL|field|psId
specifier|private
name|PatchSet
operator|.
name|Id
name|psId
decl_stmt|;
DECL|field|submitRecords
specifier|private
name|List
argument_list|<
name|SubmitRecord
argument_list|>
name|submitRecords
decl_stmt|;
annotation|@
name|AssistedInject
DECL|method|ChangeUpdate ( @erritPersonIdent PersonIdent serverIdent, GitRepositoryManager repoManager, NotesMigration migration, AccountCache accountCache, MetaDataUpdate.User updateFactory, ProjectCache projectCache, IdentifiedUser user, @Assisted ChangeControl ctl)
specifier|private
name|ChangeUpdate
parameter_list|(
annotation|@
name|GerritPersonIdent
name|PersonIdent
name|serverIdent
parameter_list|,
name|GitRepositoryManager
name|repoManager
parameter_list|,
name|NotesMigration
name|migration
parameter_list|,
name|AccountCache
name|accountCache
parameter_list|,
name|MetaDataUpdate
operator|.
name|User
name|updateFactory
parameter_list|,
name|ProjectCache
name|projectCache
parameter_list|,
name|IdentifiedUser
name|user
parameter_list|,
annotation|@
name|Assisted
name|ChangeControl
name|ctl
parameter_list|)
block|{
name|this
argument_list|(
name|serverIdent
argument_list|,
name|repoManager
argument_list|,
name|migration
argument_list|,
name|accountCache
argument_list|,
name|updateFactory
argument_list|,
name|projectCache
argument_list|,
name|ctl
argument_list|,
name|serverIdent
operator|.
name|getWhen
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AssistedInject
DECL|method|ChangeUpdate ( @erritPersonIdent PersonIdent serverIdent, GitRepositoryManager repoManager, NotesMigration migration, AccountCache accountCache, MetaDataUpdate.User updateFactory, ProjectCache projectCache, @Assisted ChangeControl ctl, @Assisted Date when)
specifier|private
name|ChangeUpdate
parameter_list|(
annotation|@
name|GerritPersonIdent
name|PersonIdent
name|serverIdent
parameter_list|,
name|GitRepositoryManager
name|repoManager
parameter_list|,
name|NotesMigration
name|migration
parameter_list|,
name|AccountCache
name|accountCache
parameter_list|,
name|MetaDataUpdate
operator|.
name|User
name|updateFactory
parameter_list|,
name|ProjectCache
name|projectCache
parameter_list|,
annotation|@
name|Assisted
name|ChangeControl
name|ctl
parameter_list|,
annotation|@
name|Assisted
name|Date
name|when
parameter_list|)
block|{
name|this
argument_list|(
name|serverIdent
argument_list|,
name|repoManager
argument_list|,
name|migration
argument_list|,
name|accountCache
argument_list|,
name|updateFactory
argument_list|,
name|ctl
argument_list|,
name|when
argument_list|,
name|projectCache
operator|.
name|get
argument_list|(
name|getProjectName
argument_list|(
name|ctl
argument_list|)
argument_list|)
operator|.
name|getLabelTypes
argument_list|()
operator|.
name|nameComparator
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getProjectName (ChangeControl ctl)
specifier|private
specifier|static
name|Project
operator|.
name|NameKey
name|getProjectName
parameter_list|(
name|ChangeControl
name|ctl
parameter_list|)
block|{
return|return
name|ctl
operator|.
name|getChange
argument_list|()
operator|.
name|getDest
argument_list|()
operator|.
name|getParentKey
argument_list|()
return|;
block|}
annotation|@
name|AssistedInject
DECL|method|ChangeUpdate ( @erritPersonIdent PersonIdent serverIdent, GitRepositoryManager repoManager, NotesMigration migration, AccountCache accountCache, MetaDataUpdate.User updateFactory, @Assisted ChangeControl ctl, @Assisted Date when, @Assisted Comparator<String> labelNameComparator)
specifier|private
name|ChangeUpdate
parameter_list|(
annotation|@
name|GerritPersonIdent
name|PersonIdent
name|serverIdent
parameter_list|,
name|GitRepositoryManager
name|repoManager
parameter_list|,
name|NotesMigration
name|migration
parameter_list|,
name|AccountCache
name|accountCache
parameter_list|,
name|MetaDataUpdate
operator|.
name|User
name|updateFactory
parameter_list|,
annotation|@
name|Assisted
name|ChangeControl
name|ctl
parameter_list|,
annotation|@
name|Assisted
name|Date
name|when
parameter_list|,
annotation|@
name|Assisted
name|Comparator
argument_list|<
name|String
argument_list|>
name|labelNameComparator
parameter_list|)
block|{
name|this
operator|.
name|repoManager
operator|=
name|repoManager
expr_stmt|;
name|this
operator|.
name|migration
operator|=
name|migration
expr_stmt|;
name|this
operator|.
name|accountCache
operator|=
name|accountCache
expr_stmt|;
name|this
operator|.
name|updateFactory
operator|=
name|updateFactory
expr_stmt|;
name|this
operator|.
name|ctl
operator|=
name|ctl
expr_stmt|;
name|this
operator|.
name|when
operator|=
name|when
expr_stmt|;
name|this
operator|.
name|serverIdent
operator|=
name|serverIdent
expr_stmt|;
name|this
operator|.
name|approvals
operator|=
name|Maps
operator|.
name|newTreeMap
argument_list|(
name|labelNameComparator
argument_list|)
expr_stmt|;
name|this
operator|.
name|reviewers
operator|=
name|Maps
operator|.
name|newLinkedHashMap
argument_list|()
expr_stmt|;
block|}
DECL|method|getChange ()
specifier|public
name|Change
name|getChange
parameter_list|()
block|{
return|return
name|ctl
operator|.
name|getChange
argument_list|()
return|;
block|}
DECL|method|getUser ()
specifier|public
name|IdentifiedUser
name|getUser
parameter_list|()
block|{
return|return
operator|(
name|IdentifiedUser
operator|)
name|ctl
operator|.
name|getCurrentUser
argument_list|()
return|;
block|}
DECL|method|getWhen ()
specifier|public
name|Date
name|getWhen
parameter_list|()
block|{
return|return
name|when
return|;
block|}
DECL|method|setStatus (Change.Status status)
specifier|public
name|void
name|setStatus
parameter_list|(
name|Change
operator|.
name|Status
name|status
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|status
operator|!=
name|Change
operator|.
name|Status
operator|.
name|SUBMITTED
argument_list|,
literal|"use submit(Iterable<PatchSetApproval>)"
argument_list|)
expr_stmt|;
name|this
operator|.
name|status
operator|=
name|status
expr_stmt|;
block|}
DECL|method|putApproval (String label, short value)
specifier|public
name|void
name|putApproval
parameter_list|(
name|String
name|label
parameter_list|,
name|short
name|value
parameter_list|)
block|{
name|approvals
operator|.
name|put
argument_list|(
name|label
argument_list|,
name|Optional
operator|.
name|of
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|removeApproval (String label)
specifier|public
name|void
name|removeApproval
parameter_list|(
name|String
name|label
parameter_list|)
block|{
name|approvals
operator|.
name|put
argument_list|(
name|label
argument_list|,
name|Optional
operator|.
expr|<
name|Short
operator|>
name|absent
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|submit (Iterable<SubmitRecord> submitRecords)
specifier|public
name|void
name|submit
parameter_list|(
name|Iterable
argument_list|<
name|SubmitRecord
argument_list|>
name|submitRecords
parameter_list|)
block|{
name|status
operator|=
name|Change
operator|.
name|Status
operator|.
name|SUBMITTED
expr_stmt|;
name|this
operator|.
name|submitRecords
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|submitRecords
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
operator|!
name|this
operator|.
name|submitRecords
operator|.
name|isEmpty
argument_list|()
argument_list|,
literal|"no submit records specified at submit time"
argument_list|)
expr_stmt|;
block|}
DECL|method|setSubject (String subject)
specifier|public
name|void
name|setSubject
parameter_list|(
name|String
name|subject
parameter_list|)
block|{
name|this
operator|.
name|subject
operator|=
name|subject
expr_stmt|;
block|}
DECL|method|setPatchSetId (PatchSet.Id psId)
specifier|public
name|void
name|setPatchSetId
parameter_list|(
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|psId
operator|==
literal|null
operator|||
name|psId
operator|.
name|getParentKey
argument_list|()
operator|.
name|equals
argument_list|(
name|getChange
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|psId
operator|=
name|psId
expr_stmt|;
block|}
DECL|method|putReviewer (Account.Id reviewer, ReviewerState type)
specifier|public
name|void
name|putReviewer
parameter_list|(
name|Account
operator|.
name|Id
name|reviewer
parameter_list|,
name|ReviewerState
name|type
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|type
operator|!=
name|ReviewerState
operator|.
name|REMOVED
argument_list|,
literal|"invalid ReviewerType"
argument_list|)
expr_stmt|;
name|reviewers
operator|.
name|put
argument_list|(
name|reviewer
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
DECL|method|removeReviewer (Account.Id reviewer)
specifier|public
name|void
name|removeReviewer
parameter_list|(
name|Account
operator|.
name|Id
name|reviewer
parameter_list|)
block|{
name|reviewers
operator|.
name|put
argument_list|(
name|reviewer
argument_list|,
name|ReviewerState
operator|.
name|REMOVED
argument_list|)
expr_stmt|;
block|}
DECL|method|load ()
specifier|private
name|void
name|load
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|migration
operator|.
name|write
argument_list|()
operator|&&
name|getRevision
argument_list|()
operator|==
literal|null
condition|)
block|{
name|Repository
name|repo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|getChange
argument_list|()
operator|.
name|getProject
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|load
argument_list|(
name|repo
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConfigInvalidException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|repo
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|commit (MetaDataUpdate md)
specifier|public
name|RevCommit
name|commit
parameter_list|(
name|MetaDataUpdate
name|md
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"use commit()"
argument_list|)
throw|;
block|}
DECL|method|commit ()
specifier|public
name|RevCommit
name|commit
parameter_list|()
throws|throws
name|IOException
block|{
name|BatchMetaDataUpdate
name|batch
init|=
name|openUpdate
argument_list|()
decl_stmt|;
try|try
block|{
name|batch
operator|.
name|write
argument_list|(
operator|new
name|CommitBuilder
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|batch
operator|.
name|commit
argument_list|()
return|;
block|}
finally|finally
block|{
name|batch
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|newIdent (Account author, Date when)
specifier|private
name|PersonIdent
name|newIdent
parameter_list|(
name|Account
name|author
parameter_list|,
name|Date
name|when
parameter_list|)
block|{
return|return
operator|new
name|PersonIdent
argument_list|(
name|author
operator|.
name|getFullName
argument_list|()
argument_list|,
name|author
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
operator|+
literal|"@"
operator|+
name|GERRIT_PLACEHOLDER_HOST
argument_list|,
name|when
argument_list|,
name|serverIdent
operator|.
name|getTimeZone
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|openUpdate (MetaDataUpdate update)
specifier|public
name|BatchMetaDataUpdate
name|openUpdate
parameter_list|(
name|MetaDataUpdate
name|update
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"use openUpdate()"
argument_list|)
throw|;
block|}
DECL|method|openUpdate ()
specifier|public
name|BatchMetaDataUpdate
name|openUpdate
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|migration
operator|.
name|write
argument_list|()
condition|)
block|{
name|load
argument_list|()
expr_stmt|;
name|MetaDataUpdate
name|md
init|=
name|updateFactory
operator|.
name|create
argument_list|(
name|getChange
argument_list|()
operator|.
name|getProject
argument_list|()
argument_list|,
name|getUser
argument_list|()
argument_list|)
decl_stmt|;
name|md
operator|.
name|setAllowEmpty
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|openUpdate
argument_list|(
name|md
argument_list|)
return|;
block|}
return|return
operator|new
name|BatchMetaDataUpdate
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
name|CommitBuilder
name|commit
parameter_list|)
block|{
comment|// Do nothing.
block|}
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
name|VersionedMetaData
name|config
parameter_list|,
name|CommitBuilder
name|commit
parameter_list|)
block|{
comment|// Do nothing.
block|}
annotation|@
name|Override
specifier|public
name|RevCommit
name|createRef
parameter_list|(
name|String
name|refName
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|RevCommit
name|commit
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|RevCommit
name|commitAt
parameter_list|(
name|ObjectId
name|revision
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
comment|// Do nothing.
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|getRefName ()
specifier|protected
name|String
name|getRefName
parameter_list|()
block|{
return|return
name|ChangeNoteUtil
operator|.
name|changeRefName
argument_list|(
name|getChange
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|onSave (CommitBuilder commit)
specifier|protected
name|boolean
name|onSave
parameter_list|(
name|CommitBuilder
name|commit
parameter_list|)
block|{
if|if
condition|(
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|commit
operator|.
name|setAuthor
argument_list|(
name|newIdent
argument_list|(
name|getUser
argument_list|()
operator|.
name|getAccount
argument_list|()
argument_list|,
name|when
argument_list|)
argument_list|)
expr_stmt|;
name|commit
operator|.
name|setCommitter
argument_list|(
operator|new
name|PersonIdent
argument_list|(
name|serverIdent
argument_list|,
name|when
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|ps
init|=
name|psId
operator|!=
literal|null
condition|?
name|psId
operator|.
name|get
argument_list|()
else|:
name|getChange
argument_list|()
operator|.
name|currentPatchSetId
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
name|StringBuilder
name|msg
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|subject
operator|!=
literal|null
condition|)
block|{
name|msg
operator|.
name|append
argument_list|(
name|subject
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|msg
operator|.
name|append
argument_list|(
literal|"Update patch set "
argument_list|)
operator|.
name|append
argument_list|(
name|ps
argument_list|)
expr_stmt|;
block|}
name|msg
operator|.
name|append
argument_list|(
literal|"\n\n"
argument_list|)
expr_stmt|;
name|addFooter
argument_list|(
name|msg
argument_list|,
name|FOOTER_PATCH_SET
argument_list|,
name|ps
argument_list|)
expr_stmt|;
if|if
condition|(
name|status
operator|!=
literal|null
condition|)
block|{
name|addFooter
argument_list|(
name|msg
argument_list|,
name|FOOTER_STATUS
argument_list|,
name|status
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|ReviewerState
argument_list|>
name|e
range|:
name|reviewers
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Account
name|account
init|=
name|accountCache
operator|.
name|get
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|getAccount
argument_list|()
decl_stmt|;
name|PersonIdent
name|ident
init|=
name|newIdent
argument_list|(
name|account
argument_list|,
name|when
argument_list|)
decl_stmt|;
name|addFooter
argument_list|(
name|msg
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|getFooterKey
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|ident
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"<"
argument_list|)
operator|.
name|append
argument_list|(
name|ident
operator|.
name|getEmailAddress
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|">\n"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Optional
argument_list|<
name|Short
argument_list|>
argument_list|>
name|e
range|:
name|approvals
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|addFooter
argument_list|(
name|msg
argument_list|,
name|FOOTER_LABEL
argument_list|,
literal|'-'
argument_list|,
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|addFooter
argument_list|(
name|msg
argument_list|,
name|FOOTER_LABEL
argument_list|,
operator|new
name|LabelVote
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|formatWithEquals
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|submitRecords
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|SubmitRecord
name|rec
range|:
name|submitRecords
control|)
block|{
name|addFooter
argument_list|(
name|msg
argument_list|,
name|FOOTER_SUBMITTED_WITH
argument_list|)
operator|.
name|append
argument_list|(
name|rec
operator|.
name|status
argument_list|)
expr_stmt|;
if|if
condition|(
name|rec
operator|.
name|errorMessage
operator|!=
literal|null
condition|)
block|{
name|msg
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
operator|.
name|append
argument_list|(
name|sanitizeFooter
argument_list|(
name|rec
operator|.
name|errorMessage
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|msg
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
if|if
condition|(
name|rec
operator|.
name|labels
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|SubmitRecord
operator|.
name|Label
name|label
range|:
name|rec
operator|.
name|labels
control|)
block|{
name|addFooter
argument_list|(
name|msg
argument_list|,
name|FOOTER_SUBMITTED_WITH
argument_list|)
operator|.
name|append
argument_list|(
name|label
operator|.
name|status
argument_list|)
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
operator|.
name|append
argument_list|(
name|label
operator|.
name|label
argument_list|)
expr_stmt|;
if|if
condition|(
name|label
operator|.
name|appliedBy
operator|!=
literal|null
condition|)
block|{
name|PersonIdent
name|ident
init|=
name|newIdent
argument_list|(
name|accountCache
operator|.
name|get
argument_list|(
name|label
operator|.
name|appliedBy
argument_list|)
operator|.
name|getAccount
argument_list|()
argument_list|,
name|when
argument_list|)
decl_stmt|;
name|msg
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
operator|.
name|append
argument_list|(
name|ident
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"<"
argument_list|)
operator|.
name|append
argument_list|(
name|ident
operator|.
name|getEmailAddress
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
block|}
name|msg
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|commit
operator|.
name|setMessage
argument_list|(
name|msg
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|isEmpty ()
specifier|private
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|approvals
operator|.
name|isEmpty
argument_list|()
operator|&&
name|reviewers
operator|.
name|isEmpty
argument_list|()
operator|&&
name|status
operator|==
literal|null
operator|&&
name|submitRecords
operator|==
literal|null
return|;
block|}
DECL|method|addFooter (StringBuilder sb, FooterKey footer)
specifier|private
specifier|static
name|StringBuilder
name|addFooter
parameter_list|(
name|StringBuilder
name|sb
parameter_list|,
name|FooterKey
name|footer
parameter_list|)
block|{
return|return
name|sb
operator|.
name|append
argument_list|(
name|footer
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
return|;
block|}
DECL|method|addFooter (StringBuilder sb, FooterKey footer, Object... values)
specifier|private
specifier|static
name|void
name|addFooter
parameter_list|(
name|StringBuilder
name|sb
parameter_list|,
name|FooterKey
name|footer
parameter_list|,
name|Object
modifier|...
name|values
parameter_list|)
block|{
name|addFooter
argument_list|(
name|sb
argument_list|,
name|footer
argument_list|)
expr_stmt|;
for|for
control|(
name|Object
name|value
range|:
name|values
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
DECL|method|sanitizeFooter (String value)
specifier|private
specifier|static
name|String
name|sanitizeFooter
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|value
operator|.
name|replace
argument_list|(
literal|'\n'
argument_list|,
literal|' '
argument_list|)
operator|.
name|replace
argument_list|(
literal|'\0'
argument_list|,
literal|' '
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|onLoad ()
specifier|protected
name|void
name|onLoad
parameter_list|()
throws|throws
name|IOException
throws|,
name|ConfigInvalidException
block|{
comment|// Do nothing; just reads current revision.
block|}
block|}
end_class

end_unit

