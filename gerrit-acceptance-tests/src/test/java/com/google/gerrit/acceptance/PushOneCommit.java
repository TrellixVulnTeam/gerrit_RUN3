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
DECL|package|com.google.gerrit.acceptance
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
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
name|acceptance
operator|.
name|GitUtil
operator|.
name|add
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
name|acceptance
operator|.
name|GitUtil
operator|.
name|amendCommit
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
name|acceptance
operator|.
name|GitUtil
operator|.
name|createCommit
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
name|acceptance
operator|.
name|GitUtil
operator|.
name|pushHead
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
name|Function
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
name|Strings
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
name|Iterables
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
name|collect
operator|.
name|Sets
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
name|GitUtil
operator|.
name|Commit
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
name|notedb
operator|.
name|ChangeNotes
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
name|gerrit
operator|.
name|server
operator|.
name|query
operator|.
name|change
operator|.
name|InternalChangeQuery
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
name|api
operator|.
name|Git
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
name|api
operator|.
name|TagCommand
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
name|api
operator|.
name|errors
operator|.
name|ConcurrentRefUpdateException
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
name|api
operator|.
name|errors
operator|.
name|GitAPIException
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
name|api
operator|.
name|errors
operator|.
name|InvalidTagNameException
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
name|api
operator|.
name|errors
operator|.
name|NoHeadException
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
name|revwalk
operator|.
name|RevCommit
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
name|transport
operator|.
name|PushResult
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
name|transport
operator|.
name|RemoteRefUpdate
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
name|transport
operator|.
name|RemoteRefUpdate
operator|.
name|Status
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
name|Arrays
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
DECL|class|PushOneCommit
specifier|public
class|class
name|PushOneCommit
block|{
DECL|field|SUBJECT
specifier|public
specifier|static
specifier|final
name|String
name|SUBJECT
init|=
literal|"test commit"
decl_stmt|;
DECL|field|FILE_NAME
specifier|public
specifier|static
specifier|final
name|String
name|FILE_NAME
init|=
literal|"a.txt"
decl_stmt|;
DECL|field|FILE_CONTENT
specifier|public
specifier|static
specifier|final
name|String
name|FILE_CONTENT
init|=
literal|"some content"
decl_stmt|;
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create ( ReviewDb db, PersonIdent i)
name|PushOneCommit
name|create
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|PersonIdent
name|i
parameter_list|)
function_decl|;
DECL|method|create ( ReviewDb db, PersonIdent i, @Assisted(R) String subject, @Assisted(R) String fileName, @Assisted(R) String content)
name|PushOneCommit
name|create
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|PersonIdent
name|i
parameter_list|,
annotation|@
name|Assisted
argument_list|(
literal|"subject"
argument_list|)
name|String
name|subject
parameter_list|,
annotation|@
name|Assisted
argument_list|(
literal|"fileName"
argument_list|)
name|String
name|fileName
parameter_list|,
annotation|@
name|Assisted
argument_list|(
literal|"content"
argument_list|)
name|String
name|content
parameter_list|)
function_decl|;
DECL|method|create ( ReviewDb db, PersonIdent i, @Assisted(R) String subject, @Assisted(R) String fileName, @Assisted(R) String content, @Assisted(R) String changeId)
name|PushOneCommit
name|create
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|PersonIdent
name|i
parameter_list|,
annotation|@
name|Assisted
argument_list|(
literal|"subject"
argument_list|)
name|String
name|subject
parameter_list|,
annotation|@
name|Assisted
argument_list|(
literal|"fileName"
argument_list|)
name|String
name|fileName
parameter_list|,
annotation|@
name|Assisted
argument_list|(
literal|"content"
argument_list|)
name|String
name|content
parameter_list|,
annotation|@
name|Assisted
argument_list|(
literal|"changeId"
argument_list|)
name|String
name|changeId
parameter_list|)
function_decl|;
block|}
DECL|class|Tag
specifier|public
specifier|static
class|class
name|Tag
block|{
DECL|field|name
specifier|public
name|String
name|name
decl_stmt|;
DECL|method|Tag (String name)
specifier|public
name|Tag
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
block|}
DECL|class|AnnotatedTag
specifier|public
specifier|static
class|class
name|AnnotatedTag
extends|extends
name|Tag
block|{
DECL|field|message
specifier|public
name|String
name|message
decl_stmt|;
DECL|field|tagger
specifier|public
name|PersonIdent
name|tagger
decl_stmt|;
DECL|method|AnnotatedTag (String name, String message, PersonIdent tagger)
specifier|public
name|AnnotatedTag
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|message
parameter_list|,
name|PersonIdent
name|tagger
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
name|this
operator|.
name|tagger
operator|=
name|tagger
expr_stmt|;
block|}
block|}
DECL|field|notesFactory
specifier|private
specifier|final
name|ChangeNotes
operator|.
name|Factory
name|notesFactory
decl_stmt|;
DECL|field|approvalsUtil
specifier|private
specifier|final
name|ApprovalsUtil
name|approvalsUtil
decl_stmt|;
DECL|field|queryProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|InternalChangeQuery
argument_list|>
name|queryProvider
decl_stmt|;
DECL|field|db
specifier|private
specifier|final
name|ReviewDb
name|db
decl_stmt|;
DECL|field|i
specifier|private
specifier|final
name|PersonIdent
name|i
decl_stmt|;
DECL|field|subject
specifier|private
specifier|final
name|String
name|subject
decl_stmt|;
DECL|field|fileName
specifier|private
specifier|final
name|String
name|fileName
decl_stmt|;
DECL|field|content
specifier|private
specifier|final
name|String
name|content
decl_stmt|;
DECL|field|changeId
specifier|private
name|String
name|changeId
decl_stmt|;
DECL|field|tag
specifier|private
name|Tag
name|tag
decl_stmt|;
annotation|@
name|AssistedInject
DECL|method|PushOneCommit (ChangeNotes.Factory notesFactory, ApprovalsUtil approvalsUtil, Provider<InternalChangeQuery> queryProvider, @Assisted ReviewDb db, @Assisted PersonIdent i)
name|PushOneCommit
parameter_list|(
name|ChangeNotes
operator|.
name|Factory
name|notesFactory
parameter_list|,
name|ApprovalsUtil
name|approvalsUtil
parameter_list|,
name|Provider
argument_list|<
name|InternalChangeQuery
argument_list|>
name|queryProvider
parameter_list|,
annotation|@
name|Assisted
name|ReviewDb
name|db
parameter_list|,
annotation|@
name|Assisted
name|PersonIdent
name|i
parameter_list|)
block|{
name|this
argument_list|(
name|notesFactory
argument_list|,
name|approvalsUtil
argument_list|,
name|queryProvider
argument_list|,
name|db
argument_list|,
name|i
argument_list|,
name|SUBJECT
argument_list|,
name|FILE_NAME
argument_list|,
name|FILE_CONTENT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AssistedInject
DECL|method|PushOneCommit (ChangeNotes.Factory notesFactory, ApprovalsUtil approvalsUtil, Provider<InternalChangeQuery> queryProvider, @Assisted ReviewDb db, @Assisted PersonIdent i, @Assisted(R) String subject, @Assisted(R) String fileName, @Assisted(R) String content)
name|PushOneCommit
parameter_list|(
name|ChangeNotes
operator|.
name|Factory
name|notesFactory
parameter_list|,
name|ApprovalsUtil
name|approvalsUtil
parameter_list|,
name|Provider
argument_list|<
name|InternalChangeQuery
argument_list|>
name|queryProvider
parameter_list|,
annotation|@
name|Assisted
name|ReviewDb
name|db
parameter_list|,
annotation|@
name|Assisted
name|PersonIdent
name|i
parameter_list|,
annotation|@
name|Assisted
argument_list|(
literal|"subject"
argument_list|)
name|String
name|subject
parameter_list|,
annotation|@
name|Assisted
argument_list|(
literal|"fileName"
argument_list|)
name|String
name|fileName
parameter_list|,
annotation|@
name|Assisted
argument_list|(
literal|"content"
argument_list|)
name|String
name|content
parameter_list|)
block|{
name|this
argument_list|(
name|notesFactory
argument_list|,
name|approvalsUtil
argument_list|,
name|queryProvider
argument_list|,
name|db
argument_list|,
name|i
argument_list|,
name|subject
argument_list|,
name|fileName
argument_list|,
name|content
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AssistedInject
DECL|method|PushOneCommit (ChangeNotes.Factory notesFactory, ApprovalsUtil approvalsUtil, Provider<InternalChangeQuery> queryProvider, @Assisted ReviewDb db, @Assisted PersonIdent i, @Assisted(R) String subject, @Assisted(R) String fileName, @Assisted(R) String content, @Nullable @Assisted(R) String changeId)
name|PushOneCommit
parameter_list|(
name|ChangeNotes
operator|.
name|Factory
name|notesFactory
parameter_list|,
name|ApprovalsUtil
name|approvalsUtil
parameter_list|,
name|Provider
argument_list|<
name|InternalChangeQuery
argument_list|>
name|queryProvider
parameter_list|,
annotation|@
name|Assisted
name|ReviewDb
name|db
parameter_list|,
annotation|@
name|Assisted
name|PersonIdent
name|i
parameter_list|,
annotation|@
name|Assisted
argument_list|(
literal|"subject"
argument_list|)
name|String
name|subject
parameter_list|,
annotation|@
name|Assisted
argument_list|(
literal|"fileName"
argument_list|)
name|String
name|fileName
parameter_list|,
annotation|@
name|Assisted
argument_list|(
literal|"content"
argument_list|)
name|String
name|content
parameter_list|,
annotation|@
name|Nullable
annotation|@
name|Assisted
argument_list|(
literal|"changeId"
argument_list|)
name|String
name|changeId
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
name|notesFactory
operator|=
name|notesFactory
expr_stmt|;
name|this
operator|.
name|approvalsUtil
operator|=
name|approvalsUtil
expr_stmt|;
name|this
operator|.
name|queryProvider
operator|=
name|queryProvider
expr_stmt|;
name|this
operator|.
name|i
operator|=
name|i
expr_stmt|;
name|this
operator|.
name|subject
operator|=
name|subject
expr_stmt|;
name|this
operator|.
name|fileName
operator|=
name|fileName
expr_stmt|;
name|this
operator|.
name|content
operator|=
name|content
expr_stmt|;
name|this
operator|.
name|changeId
operator|=
name|changeId
expr_stmt|;
block|}
DECL|method|to (Git git, String ref)
specifier|public
name|Result
name|to
parameter_list|(
name|Git
name|git
parameter_list|,
name|String
name|ref
parameter_list|)
throws|throws
name|GitAPIException
throws|,
name|IOException
block|{
name|add
argument_list|(
name|git
argument_list|,
name|fileName
argument_list|,
name|content
argument_list|)
expr_stmt|;
return|return
name|execute
argument_list|(
name|git
argument_list|,
name|ref
argument_list|)
return|;
block|}
DECL|method|rm (Git git, String ref)
specifier|public
name|Result
name|rm
parameter_list|(
name|Git
name|git
parameter_list|,
name|String
name|ref
parameter_list|)
throws|throws
name|GitAPIException
block|{
name|GitUtil
operator|.
name|rm
argument_list|(
name|git
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
return|return
name|execute
argument_list|(
name|git
argument_list|,
name|ref
argument_list|)
return|;
block|}
DECL|method|execute (Git git, String ref)
specifier|private
name|Result
name|execute
parameter_list|(
name|Git
name|git
parameter_list|,
name|String
name|ref
parameter_list|)
throws|throws
name|GitAPIException
throws|,
name|ConcurrentRefUpdateException
throws|,
name|InvalidTagNameException
throws|,
name|NoHeadException
block|{
name|Commit
name|c
decl_stmt|;
if|if
condition|(
name|changeId
operator|!=
literal|null
condition|)
block|{
name|c
operator|=
name|amendCommit
argument_list|(
name|git
argument_list|,
name|i
argument_list|,
name|subject
argument_list|,
name|changeId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|c
operator|=
name|createCommit
argument_list|(
name|git
argument_list|,
name|i
argument_list|,
name|subject
argument_list|)
expr_stmt|;
name|changeId
operator|=
name|c
operator|.
name|getChangeId
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|tag
operator|!=
literal|null
condition|)
block|{
name|TagCommand
name|tagCommand
init|=
name|git
operator|.
name|tag
argument_list|()
operator|.
name|setName
argument_list|(
name|tag
operator|.
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|tag
operator|instanceof
name|AnnotatedTag
condition|)
block|{
name|AnnotatedTag
name|annotatedTag
init|=
operator|(
name|AnnotatedTag
operator|)
name|tag
decl_stmt|;
name|tagCommand
operator|.
name|setAnnotated
argument_list|(
literal|true
argument_list|)
operator|.
name|setMessage
argument_list|(
name|annotatedTag
operator|.
name|message
argument_list|)
operator|.
name|setTagger
argument_list|(
name|annotatedTag
operator|.
name|tagger
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tagCommand
operator|.
name|setAnnotated
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|tagCommand
operator|.
name|call
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|Result
argument_list|(
name|ref
argument_list|,
name|pushHead
argument_list|(
name|git
argument_list|,
name|ref
argument_list|,
name|tag
operator|!=
literal|null
argument_list|)
argument_list|,
name|c
argument_list|,
name|subject
argument_list|)
return|;
block|}
DECL|method|setTag (final Tag tag)
specifier|public
name|void
name|setTag
parameter_list|(
specifier|final
name|Tag
name|tag
parameter_list|)
block|{
name|this
operator|.
name|tag
operator|=
name|tag
expr_stmt|;
block|}
DECL|class|Result
specifier|public
class|class
name|Result
block|{
DECL|field|ref
specifier|private
specifier|final
name|String
name|ref
decl_stmt|;
DECL|field|result
specifier|private
specifier|final
name|PushResult
name|result
decl_stmt|;
DECL|field|commit
specifier|private
specifier|final
name|Commit
name|commit
decl_stmt|;
DECL|field|resSubj
specifier|private
specifier|final
name|String
name|resSubj
decl_stmt|;
DECL|method|Result (String ref, PushResult resSubj, Commit commit, String subject)
specifier|private
name|Result
parameter_list|(
name|String
name|ref
parameter_list|,
name|PushResult
name|resSubj
parameter_list|,
name|Commit
name|commit
parameter_list|,
name|String
name|subject
parameter_list|)
block|{
name|this
operator|.
name|ref
operator|=
name|ref
expr_stmt|;
name|this
operator|.
name|result
operator|=
name|resSubj
expr_stmt|;
name|this
operator|.
name|commit
operator|=
name|commit
expr_stmt|;
name|this
operator|.
name|resSubj
operator|=
name|subject
expr_stmt|;
block|}
DECL|method|getChange ()
specifier|public
name|ChangeData
name|getChange
parameter_list|()
throws|throws
name|OrmException
block|{
return|return
name|Iterables
operator|.
name|getOnlyElement
argument_list|(
name|queryProvider
operator|.
name|get
argument_list|()
operator|.
name|byKeyPrefix
argument_list|(
name|commit
operator|.
name|getChangeId
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getPatchSet ()
specifier|public
name|PatchSet
name|getPatchSet
parameter_list|()
throws|throws
name|OrmException
block|{
return|return
name|getChange
argument_list|()
operator|.
name|currentPatchSet
argument_list|()
return|;
block|}
DECL|method|getPatchSetId ()
specifier|public
name|PatchSet
operator|.
name|Id
name|getPatchSetId
parameter_list|()
throws|throws
name|OrmException
block|{
return|return
name|getChange
argument_list|()
operator|.
name|change
argument_list|()
operator|.
name|currentPatchSetId
argument_list|()
return|;
block|}
DECL|method|getChangeId ()
specifier|public
name|String
name|getChangeId
parameter_list|()
block|{
return|return
name|commit
operator|.
name|getChangeId
argument_list|()
return|;
block|}
DECL|method|getCommitId ()
specifier|public
name|ObjectId
name|getCommitId
parameter_list|()
block|{
return|return
name|commit
operator|.
name|getCommit
argument_list|()
operator|.
name|getId
argument_list|()
return|;
block|}
DECL|method|getCommit ()
specifier|public
name|RevCommit
name|getCommit
parameter_list|()
block|{
return|return
name|commit
operator|.
name|getCommit
argument_list|()
return|;
block|}
DECL|method|assertChange (Change.Status expectedStatus, String expectedTopic, TestAccount... expectedReviewers)
specifier|public
name|void
name|assertChange
parameter_list|(
name|Change
operator|.
name|Status
name|expectedStatus
parameter_list|,
name|String
name|expectedTopic
parameter_list|,
name|TestAccount
modifier|...
name|expectedReviewers
parameter_list|)
throws|throws
name|OrmException
block|{
name|Change
name|c
init|=
name|getChange
argument_list|()
operator|.
name|change
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|resSubj
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|c
operator|.
name|getSubject
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|expectedStatus
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|c
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|expectedTopic
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|c
operator|.
name|getTopic
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertReviewers
argument_list|(
name|c
argument_list|,
name|expectedReviewers
argument_list|)
expr_stmt|;
block|}
DECL|method|assertReviewers (Change c, TestAccount... expectedReviewers)
specifier|private
name|void
name|assertReviewers
parameter_list|(
name|Change
name|c
parameter_list|,
name|TestAccount
modifier|...
name|expectedReviewers
parameter_list|)
throws|throws
name|OrmException
block|{
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|expectedReviewerIds
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|Lists
operator|.
name|transform
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|expectedReviewers
argument_list|)
argument_list|,
operator|new
name|Function
argument_list|<
name|TestAccount
argument_list|,
name|Account
operator|.
name|Id
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Account
operator|.
name|Id
name|apply
parameter_list|(
name|TestAccount
name|a
parameter_list|)
block|{
return|return
name|a
operator|.
name|id
return|;
block|}
block|}
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|Account
operator|.
name|Id
name|accountId
range|:
name|approvalsUtil
operator|.
name|getReviewers
argument_list|(
name|db
argument_list|,
name|notesFactory
operator|.
name|create
argument_list|(
name|c
argument_list|)
argument_list|)
operator|.
name|values
argument_list|()
control|)
block|{
name|assertThat
argument_list|(
name|expectedReviewerIds
operator|.
name|remove
argument_list|(
name|accountId
argument_list|)
argument_list|)
operator|.
name|named
argument_list|(
literal|"unexpected reviewer "
operator|+
name|accountId
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
block|}
name|assertThat
argument_list|(
operator|(
name|Iterable
argument_list|<
name|?
argument_list|>
operator|)
name|expectedReviewerIds
argument_list|)
operator|.
name|named
argument_list|(
literal|"missing reviewers: "
operator|+
name|expectedReviewerIds
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
block|}
DECL|method|assertOkStatus ()
specifier|public
name|void
name|assertOkStatus
parameter_list|()
block|{
name|assertStatus
argument_list|(
name|Status
operator|.
name|OK
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|assertErrorStatus (String expectedMessage)
specifier|public
name|void
name|assertErrorStatus
parameter_list|(
name|String
name|expectedMessage
parameter_list|)
block|{
name|assertStatus
argument_list|(
name|Status
operator|.
name|REJECTED_OTHER_REASON
argument_list|,
name|expectedMessage
argument_list|)
expr_stmt|;
block|}
DECL|method|assertStatus (Status expectedStatus, String expectedMessage)
specifier|private
name|void
name|assertStatus
parameter_list|(
name|Status
name|expectedStatus
parameter_list|,
name|String
name|expectedMessage
parameter_list|)
block|{
name|RemoteRefUpdate
name|refUpdate
init|=
name|result
operator|.
name|getRemoteUpdate
argument_list|(
name|ref
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|expectedStatus
argument_list|)
operator|.
name|named
argument_list|(
name|message
argument_list|(
name|refUpdate
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|refUpdate
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|expectedMessage
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|refUpdate
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertMessage (String expectedMessage)
specifier|public
name|void
name|assertMessage
parameter_list|(
name|String
name|expectedMessage
parameter_list|)
block|{
name|RemoteRefUpdate
name|refUpdate
init|=
name|result
operator|.
name|getRemoteUpdate
argument_list|(
name|ref
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|message
argument_list|(
name|refUpdate
argument_list|)
operator|.
name|toLowerCase
argument_list|()
argument_list|)
operator|.
name|named
argument_list|(
name|message
argument_list|(
name|refUpdate
argument_list|)
argument_list|)
operator|.
name|contains
argument_list|(
name|expectedMessage
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|message (RemoteRefUpdate refUpdate)
specifier|private
name|String
name|message
parameter_list|(
name|RemoteRefUpdate
name|refUpdate
parameter_list|)
block|{
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|refUpdate
operator|.
name|getMessage
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
name|refUpdate
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
name|result
operator|.
name|getMessages
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

