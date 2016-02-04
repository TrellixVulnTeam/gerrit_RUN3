begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2009 The Android Open Source Project
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
DECL|package|com.google.gerrit.server
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
package|;
end_package

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
name|ImmutableSet
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
name|Ordering
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
name|primitives
operator|.
name|Ints
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
name|change
operator|.
name|ChangeInserter
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
name|ChangeMessages
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
name|ChangeTriplet
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
name|git
operator|.
name|validators
operator|.
name|CommitValidators
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
name|RevertedSender
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
name|gerrit
operator|.
name|server
operator|.
name|util
operator|.
name|IdGenerator
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
name|errors
operator|.
name|IncorrectObjectTypeException
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
name|MissingObjectException
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
name|RepositoryNotFoundException
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
name|ObjectInserter
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
name|Ref
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
name|RefDatabase
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
name|revwalk
operator|.
name|RevWalk
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
name|util
operator|.
name|ChangeIdUtil
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
name|text
operator|.
name|MessageFormat
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
annotation|@
name|Singleton
DECL|class|ChangeUtil
specifier|public
class|class
name|ChangeUtil
block|{
DECL|field|uuidLock
specifier|private
specifier|static
specifier|final
name|Object
name|uuidLock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|field|SEED
specifier|private
specifier|static
specifier|final
name|int
name|SEED
init|=
literal|0x2418e6f9
decl_stmt|;
DECL|field|uuidPrefix
specifier|private
specifier|static
name|int
name|uuidPrefix
decl_stmt|;
DECL|field|uuidSeq
specifier|private
specifier|static
name|int
name|uuidSeq
decl_stmt|;
DECL|field|SUBJECT_MAX_LENGTH
specifier|private
specifier|static
specifier|final
name|int
name|SUBJECT_MAX_LENGTH
init|=
literal|80
decl_stmt|;
DECL|field|SUBJECT_CROP_APPENDIX
specifier|private
specifier|static
specifier|final
name|String
name|SUBJECT_CROP_APPENDIX
init|=
literal|"..."
decl_stmt|;
DECL|field|SUBJECT_CROP_RANGE
specifier|private
specifier|static
specifier|final
name|int
name|SUBJECT_CROP_RANGE
init|=
literal|10
decl_stmt|;
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
name|ChangeUtil
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|TO_PS_ID
specifier|public
specifier|static
specifier|final
name|Function
argument_list|<
name|PatchSet
argument_list|,
name|Integer
argument_list|>
name|TO_PS_ID
init|=
operator|new
name|Function
argument_list|<
name|PatchSet
argument_list|,
name|Integer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Integer
name|apply
parameter_list|(
name|PatchSet
name|in
parameter_list|)
block|{
return|return
name|in
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
return|;
block|}
block|}
decl_stmt|;
DECL|field|PS_ID_ORDER
specifier|public
specifier|static
specifier|final
name|Ordering
argument_list|<
name|PatchSet
argument_list|>
name|PS_ID_ORDER
init|=
name|Ordering
operator|.
name|natural
argument_list|()
operator|.
name|onResultOf
argument_list|(
name|TO_PS_ID
argument_list|)
decl_stmt|;
comment|/**    * Generate a new unique identifier for change message entities.    *    * @param db the database connection, used to increment the change message    *        allocation sequence.    * @return the new unique identifier.    * @throws OrmException the database couldn't be incremented.    */
DECL|method|messageUUID (ReviewDb db)
specifier|public
specifier|static
name|String
name|messageUUID
parameter_list|(
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
block|{
name|int
name|p
decl_stmt|;
name|int
name|s
decl_stmt|;
synchronized|synchronized
init|(
name|uuidLock
init|)
block|{
if|if
condition|(
name|uuidSeq
operator|==
literal|0
condition|)
block|{
name|uuidPrefix
operator|=
name|db
operator|.
name|nextChangeMessageId
argument_list|()
expr_stmt|;
name|uuidSeq
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
name|p
operator|=
name|uuidPrefix
expr_stmt|;
name|s
operator|=
name|uuidSeq
operator|--
expr_stmt|;
block|}
name|String
name|u
init|=
name|IdGenerator
operator|.
name|format
argument_list|(
name|IdGenerator
operator|.
name|mix
argument_list|(
name|SEED
argument_list|,
name|p
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|l
init|=
name|IdGenerator
operator|.
name|format
argument_list|(
name|IdGenerator
operator|.
name|mix
argument_list|(
name|p
argument_list|,
name|s
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|u
operator|+
literal|'_'
operator|+
name|l
return|;
block|}
DECL|method|bumpRowVersionNotLastUpdatedOn (Change.Id id, ReviewDb db)
specifier|public
specifier|static
name|void
name|bumpRowVersionNotLastUpdatedOn
parameter_list|(
name|Change
operator|.
name|Id
name|id
parameter_list|,
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
block|{
comment|// Empty update of Change to bump rowVersion, changing its ETag.
name|Change
name|c
init|=
name|db
operator|.
name|changes
argument_list|()
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|null
condition|)
block|{
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
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|updated (Change c)
specifier|public
specifier|static
name|void
name|updated
parameter_list|(
name|Change
name|c
parameter_list|)
block|{
name|c
operator|.
name|setLastUpdatedOn
argument_list|(
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|nextPatchSetId (Map<String, Ref> allRefs, PatchSet.Id id)
specifier|public
specifier|static
name|PatchSet
operator|.
name|Id
name|nextPatchSetId
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Ref
argument_list|>
name|allRefs
parameter_list|,
name|PatchSet
operator|.
name|Id
name|id
parameter_list|)
block|{
name|PatchSet
operator|.
name|Id
name|next
init|=
name|nextPatchSetId
argument_list|(
name|id
argument_list|)
decl_stmt|;
while|while
condition|(
name|allRefs
operator|.
name|containsKey
argument_list|(
name|next
operator|.
name|toRefName
argument_list|()
argument_list|)
condition|)
block|{
name|next
operator|=
name|nextPatchSetId
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
return|return
name|next
return|;
block|}
DECL|method|nextPatchSetId (Repository git, PatchSet.Id id)
specifier|public
specifier|static
name|PatchSet
operator|.
name|Id
name|nextPatchSetId
parameter_list|(
name|Repository
name|git
parameter_list|,
name|PatchSet
operator|.
name|Id
name|id
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|nextPatchSetId
argument_list|(
name|git
operator|.
name|getRefDatabase
argument_list|()
operator|.
name|getRefs
argument_list|(
name|RefDatabase
operator|.
name|ALL
argument_list|)
argument_list|,
name|id
argument_list|)
return|;
block|}
DECL|method|cropSubject (String subject)
specifier|public
specifier|static
name|String
name|cropSubject
parameter_list|(
name|String
name|subject
parameter_list|)
block|{
if|if
condition|(
name|subject
operator|.
name|length
argument_list|()
operator|>
name|SUBJECT_MAX_LENGTH
condition|)
block|{
name|int
name|maxLength
init|=
name|SUBJECT_MAX_LENGTH
operator|-
name|SUBJECT_CROP_APPENDIX
operator|.
name|length
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|cropPosition
init|=
name|maxLength
init|;
name|cropPosition
operator|>
name|maxLength
operator|-
name|SUBJECT_CROP_RANGE
condition|;
name|cropPosition
operator|--
control|)
block|{
if|if
condition|(
name|Character
operator|.
name|isWhitespace
argument_list|(
name|subject
operator|.
name|charAt
argument_list|(
name|cropPosition
operator|-
literal|1
argument_list|)
argument_list|)
condition|)
block|{
return|return
name|subject
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|cropPosition
argument_list|)
operator|+
name|SUBJECT_CROP_APPENDIX
return|;
block|}
block|}
return|return
name|subject
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|maxLength
argument_list|)
operator|+
name|SUBJECT_CROP_APPENDIX
return|;
block|}
return|return
name|subject
return|;
block|}
DECL|field|user
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|user
decl_stmt|;
DECL|field|db
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
decl_stmt|;
DECL|field|seq
specifier|private
specifier|final
name|Sequences
name|seq
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
DECL|field|psUtil
specifier|private
specifier|final
name|PatchSetUtil
name|psUtil
decl_stmt|;
DECL|field|changeControlFactory
specifier|private
specifier|final
name|ChangeControl
operator|.
name|GenericFactory
name|changeControlFactory
decl_stmt|;
DECL|field|revertedSenderFactory
specifier|private
specifier|final
name|RevertedSender
operator|.
name|Factory
name|revertedSenderFactory
decl_stmt|;
DECL|field|changeInserterFactory
specifier|private
specifier|final
name|ChangeInserter
operator|.
name|Factory
name|changeInserterFactory
decl_stmt|;
DECL|field|gitManager
specifier|private
specifier|final
name|GitRepositoryManager
name|gitManager
decl_stmt|;
DECL|field|updateFactory
specifier|private
specifier|final
name|BatchUpdate
operator|.
name|Factory
name|updateFactory
decl_stmt|;
DECL|field|changeMessagesUtil
specifier|private
specifier|final
name|ChangeMessagesUtil
name|changeMessagesUtil
decl_stmt|;
DECL|field|changeUpdateFactory
specifier|private
specifier|final
name|ChangeUpdate
operator|.
name|Factory
name|changeUpdateFactory
decl_stmt|;
annotation|@
name|Inject
DECL|method|ChangeUtil (Provider<CurrentUser> user, Provider<ReviewDb> db, Sequences seq, Provider<InternalChangeQuery> queryProvider, PatchSetUtil psUtil, ChangeControl.GenericFactory changeControlFactory, RevertedSender.Factory revertedSenderFactory, ChangeInserter.Factory changeInserterFactory, GitRepositoryManager gitManager, BatchUpdate.Factory updateFactory, ChangeMessagesUtil changeMessagesUtil, ChangeUpdate.Factory changeUpdateFactory)
name|ChangeUtil
parameter_list|(
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|user
parameter_list|,
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|,
name|Sequences
name|seq
parameter_list|,
name|Provider
argument_list|<
name|InternalChangeQuery
argument_list|>
name|queryProvider
parameter_list|,
name|PatchSetUtil
name|psUtil
parameter_list|,
name|ChangeControl
operator|.
name|GenericFactory
name|changeControlFactory
parameter_list|,
name|RevertedSender
operator|.
name|Factory
name|revertedSenderFactory
parameter_list|,
name|ChangeInserter
operator|.
name|Factory
name|changeInserterFactory
parameter_list|,
name|GitRepositoryManager
name|gitManager
parameter_list|,
name|BatchUpdate
operator|.
name|Factory
name|updateFactory
parameter_list|,
name|ChangeMessagesUtil
name|changeMessagesUtil
parameter_list|,
name|ChangeUpdate
operator|.
name|Factory
name|changeUpdateFactory
parameter_list|)
block|{
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|seq
operator|=
name|seq
expr_stmt|;
name|this
operator|.
name|queryProvider
operator|=
name|queryProvider
expr_stmt|;
name|this
operator|.
name|psUtil
operator|=
name|psUtil
expr_stmt|;
name|this
operator|.
name|changeControlFactory
operator|=
name|changeControlFactory
expr_stmt|;
name|this
operator|.
name|revertedSenderFactory
operator|=
name|revertedSenderFactory
expr_stmt|;
name|this
operator|.
name|changeInserterFactory
operator|=
name|changeInserterFactory
expr_stmt|;
name|this
operator|.
name|gitManager
operator|=
name|gitManager
expr_stmt|;
name|this
operator|.
name|updateFactory
operator|=
name|updateFactory
expr_stmt|;
name|this
operator|.
name|changeMessagesUtil
operator|=
name|changeMessagesUtil
expr_stmt|;
name|this
operator|.
name|changeUpdateFactory
operator|=
name|changeUpdateFactory
expr_stmt|;
block|}
DECL|method|revert (ChangeControl ctl, PatchSet.Id patchSetId, String message, PersonIdent myIdent)
specifier|public
name|Change
operator|.
name|Id
name|revert
parameter_list|(
name|ChangeControl
name|ctl
parameter_list|,
name|PatchSet
operator|.
name|Id
name|patchSetId
parameter_list|,
name|String
name|message
parameter_list|,
name|PersonIdent
name|myIdent
parameter_list|)
throws|throws
name|NoSuchChangeException
throws|,
name|OrmException
throws|,
name|MissingObjectException
throws|,
name|IncorrectObjectTypeException
throws|,
name|IOException
throws|,
name|RestApiException
throws|,
name|UpdateException
block|{
name|Change
operator|.
name|Id
name|changeIdToRevert
init|=
name|patchSetId
operator|.
name|getParentKey
argument_list|()
decl_stmt|;
name|PatchSet
name|patch
init|=
name|psUtil
operator|.
name|get
argument_list|(
name|db
operator|.
name|get
argument_list|()
argument_list|,
name|ctl
operator|.
name|getNotes
argument_list|()
argument_list|,
name|patchSetId
argument_list|)
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
name|NoSuchChangeException
argument_list|(
name|changeIdToRevert
argument_list|)
throw|;
block|}
name|Change
name|changeToRevert
init|=
name|db
operator|.
name|get
argument_list|()
operator|.
name|changes
argument_list|()
operator|.
name|get
argument_list|(
name|changeIdToRevert
argument_list|)
decl_stmt|;
name|Project
operator|.
name|NameKey
name|project
init|=
name|ctl
operator|.
name|getProject
argument_list|()
operator|.
name|getNameKey
argument_list|()
decl_stmt|;
try|try
init|(
name|Repository
name|git
init|=
name|gitManager
operator|.
name|openRepository
argument_list|(
name|project
argument_list|)
init|;
name|RevWalk
name|revWalk
operator|=
operator|new
name|RevWalk
argument_list|(
name|git
argument_list|)
init|)
block|{
name|RevCommit
name|commitToRevert
init|=
name|revWalk
operator|.
name|parseCommit
argument_list|(
name|ObjectId
operator|.
name|fromString
argument_list|(
name|patch
operator|.
name|getRevision
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|PersonIdent
name|authorIdent
init|=
name|user
operator|.
name|get
argument_list|()
operator|.
name|asIdentifiedUser
argument_list|()
operator|.
name|newCommitterIdent
argument_list|(
name|myIdent
operator|.
name|getWhen
argument_list|()
argument_list|,
name|myIdent
operator|.
name|getTimeZone
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|commitToRevert
operator|.
name|getParentCount
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
literal|"Cannot revert initial commit"
argument_list|)
throw|;
block|}
name|RevCommit
name|parentToCommitToRevert
init|=
name|commitToRevert
operator|.
name|getParent
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|revWalk
operator|.
name|parseHeaders
argument_list|(
name|parentToCommitToRevert
argument_list|)
expr_stmt|;
name|CommitBuilder
name|revertCommitBuilder
init|=
operator|new
name|CommitBuilder
argument_list|()
decl_stmt|;
name|revertCommitBuilder
operator|.
name|addParentId
argument_list|(
name|commitToRevert
argument_list|)
expr_stmt|;
name|revertCommitBuilder
operator|.
name|setTreeId
argument_list|(
name|parentToCommitToRevert
operator|.
name|getTree
argument_list|()
argument_list|)
expr_stmt|;
name|revertCommitBuilder
operator|.
name|setAuthor
argument_list|(
name|authorIdent
argument_list|)
expr_stmt|;
name|revertCommitBuilder
operator|.
name|setCommitter
argument_list|(
name|authorIdent
argument_list|)
expr_stmt|;
if|if
condition|(
name|message
operator|==
literal|null
condition|)
block|{
name|message
operator|=
name|MessageFormat
operator|.
name|format
argument_list|(
name|ChangeMessages
operator|.
name|get
argument_list|()
operator|.
name|revertChangeDefaultMessage
argument_list|,
name|changeToRevert
operator|.
name|getSubject
argument_list|()
argument_list|,
name|patch
operator|.
name|getRevision
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ObjectId
name|computedChangeId
init|=
name|ChangeIdUtil
operator|.
name|computeChangeId
argument_list|(
name|parentToCommitToRevert
operator|.
name|getTree
argument_list|()
argument_list|,
name|commitToRevert
argument_list|,
name|authorIdent
argument_list|,
name|myIdent
argument_list|,
name|message
argument_list|)
decl_stmt|;
name|revertCommitBuilder
operator|.
name|setMessage
argument_list|(
name|ChangeIdUtil
operator|.
name|insertId
argument_list|(
name|message
argument_list|,
name|computedChangeId
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|RevCommit
name|revertCommit
decl_stmt|;
name|ChangeInserter
name|ins
decl_stmt|;
name|Change
operator|.
name|Id
name|changeId
init|=
operator|new
name|Change
operator|.
name|Id
argument_list|(
name|seq
operator|.
name|nextChangeId
argument_list|()
argument_list|)
decl_stmt|;
try|try
init|(
name|ObjectInserter
name|oi
init|=
name|git
operator|.
name|newObjectInserter
argument_list|()
init|)
block|{
name|ObjectId
name|id
init|=
name|oi
operator|.
name|insert
argument_list|(
name|revertCommitBuilder
argument_list|)
decl_stmt|;
name|oi
operator|.
name|flush
argument_list|()
expr_stmt|;
name|revertCommit
operator|=
name|revWalk
operator|.
name|parseCommit
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|ins
operator|=
name|changeInserterFactory
operator|.
name|create
argument_list|(
name|changeId
argument_list|,
name|revertCommit
argument_list|,
name|ctl
operator|.
name|getChange
argument_list|()
operator|.
name|getDest
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|setValidatePolicy
argument_list|(
name|CommitValidators
operator|.
name|Policy
operator|.
name|GERRIT
argument_list|)
operator|.
name|setTopic
argument_list|(
name|changeToRevert
operator|.
name|getTopic
argument_list|()
argument_list|)
expr_stmt|;
name|ChangeMessage
name|changeMessage
init|=
operator|new
name|ChangeMessage
argument_list|(
operator|new
name|ChangeMessage
operator|.
name|Key
argument_list|(
name|patchSetId
operator|.
name|getParentKey
argument_list|()
argument_list|,
name|ChangeUtil
operator|.
name|messageUUID
argument_list|(
name|db
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|user
operator|.
name|get
argument_list|()
operator|.
name|getAccountId
argument_list|()
argument_list|,
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|,
name|patchSetId
argument_list|)
decl_stmt|;
name|StringBuilder
name|msgBuf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|msgBuf
operator|.
name|append
argument_list|(
literal|"Patch Set "
argument_list|)
operator|.
name|append
argument_list|(
name|patchSetId
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|": Reverted"
argument_list|)
expr_stmt|;
name|msgBuf
operator|.
name|append
argument_list|(
literal|"\n\n"
argument_list|)
expr_stmt|;
name|msgBuf
operator|.
name|append
argument_list|(
literal|"This patchset was reverted in change: "
argument_list|)
operator|.
name|append
argument_list|(
literal|"I"
argument_list|)
operator|.
name|append
argument_list|(
name|computedChangeId
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|changeMessage
operator|.
name|setMessage
argument_list|(
name|msgBuf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ChangeUpdate
name|update
init|=
name|changeUpdateFactory
operator|.
name|create
argument_list|(
name|ctl
argument_list|,
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
decl_stmt|;
name|changeMessagesUtil
operator|.
name|addChangeMessage
argument_list|(
name|db
operator|.
name|get
argument_list|()
argument_list|,
name|update
argument_list|,
name|changeMessage
argument_list|)
expr_stmt|;
name|update
operator|.
name|commit
argument_list|()
expr_stmt|;
name|ins
operator|.
name|setMessage
argument_list|(
literal|"Uploaded patch set 1."
argument_list|)
expr_stmt|;
try|try
init|(
name|BatchUpdate
name|bu
init|=
name|updateFactory
operator|.
name|create
argument_list|(
name|db
operator|.
name|get
argument_list|()
argument_list|,
name|project
argument_list|,
name|ctl
operator|.
name|getUser
argument_list|()
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
name|setRepository
argument_list|(
name|git
argument_list|,
name|revWalk
argument_list|,
name|oi
argument_list|)
expr_stmt|;
name|bu
operator|.
name|insertChange
argument_list|(
name|ins
argument_list|)
expr_stmt|;
name|bu
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
block|}
try|try
block|{
name|RevertedSender
name|cm
init|=
name|revertedSenderFactory
operator|.
name|create
argument_list|(
name|changeId
argument_list|)
decl_stmt|;
name|cm
operator|.
name|setFrom
argument_list|(
name|user
operator|.
name|get
argument_list|()
operator|.
name|getAccountId
argument_list|()
argument_list|)
expr_stmt|;
name|cm
operator|.
name|setChangeMessage
argument_list|(
name|ins
operator|.
name|getChangeMessage
argument_list|()
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
literal|"Cannot send email for revert change "
operator|+
name|changeId
argument_list|,
name|err
argument_list|)
expr_stmt|;
block|}
return|return
name|changeId
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|NoSuchChangeException
argument_list|(
name|changeIdToRevert
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getMessage (ChangeNotes notes)
specifier|public
name|String
name|getMessage
parameter_list|(
name|ChangeNotes
name|notes
parameter_list|)
throws|throws
name|NoSuchChangeException
throws|,
name|OrmException
throws|,
name|MissingObjectException
throws|,
name|IncorrectObjectTypeException
throws|,
name|IOException
block|{
name|Change
operator|.
name|Id
name|changeId
init|=
name|notes
operator|.
name|getChangeId
argument_list|()
decl_stmt|;
name|PatchSet
name|ps
init|=
name|psUtil
operator|.
name|current
argument_list|(
name|db
operator|.
name|get
argument_list|()
argument_list|,
name|notes
argument_list|)
decl_stmt|;
if|if
condition|(
name|ps
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NoSuchChangeException
argument_list|(
name|changeId
argument_list|)
throw|;
block|}
try|try
init|(
name|Repository
name|git
init|=
name|gitManager
operator|.
name|openRepository
argument_list|(
name|notes
operator|.
name|getProjectName
argument_list|()
argument_list|)
init|;
name|RevWalk
name|revWalk
operator|=
operator|new
name|RevWalk
argument_list|(
name|git
argument_list|)
init|)
block|{
name|RevCommit
name|commit
init|=
name|revWalk
operator|.
name|parseCommit
argument_list|(
name|ObjectId
operator|.
name|fromString
argument_list|(
name|ps
operator|.
name|getRevision
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|commit
operator|.
name|getFullMessage
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|NoSuchChangeException
argument_list|(
name|changeId
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Find changes matching the given identifier.    *    * @param id change identifier, either a numeric ID, a Change-Id, or    *     project~branch~id triplet.    * @param user user to wrap in controls.    * @return possibly-empty list of controls for all matching changes,    *     corresponding to the given user; may or may not be visible.    * @throws OrmException if an error occurred querying the database.    */
DECL|method|findChanges (String id, CurrentUser user)
specifier|public
name|List
argument_list|<
name|ChangeControl
argument_list|>
name|findChanges
parameter_list|(
name|String
name|id
parameter_list|,
name|CurrentUser
name|user
parameter_list|)
throws|throws
name|OrmException
block|{
comment|// Try legacy id
if|if
condition|(
operator|!
name|id
operator|.
name|isEmpty
argument_list|()
operator|&&
name|id
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|!=
literal|'0'
condition|)
block|{
name|Integer
name|n
init|=
name|Ints
operator|.
name|tryParse
argument_list|(
name|id
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|n
operator|!=
literal|null
condition|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|changeControlFactory
operator|.
name|controlFor
argument_list|(
operator|new
name|Change
operator|.
name|Id
argument_list|(
name|n
argument_list|)
argument_list|,
name|user
argument_list|)
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|NoSuchChangeException
name|e
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
block|}
comment|// Use the index to search for changes, but don't return any stored fields,
comment|// to force rereading in case the index is stale.
name|InternalChangeQuery
name|query
init|=
name|queryProvider
operator|.
name|get
argument_list|()
operator|.
name|setRequestedFields
argument_list|(
name|ImmutableSet
operator|.
expr|<
name|String
operator|>
name|of
argument_list|()
argument_list|)
decl_stmt|;
comment|// Try isolated changeId
if|if
condition|(
operator|!
name|id
operator|.
name|contains
argument_list|(
literal|"~"
argument_list|)
condition|)
block|{
return|return
name|asChangeControls
argument_list|(
name|query
operator|.
name|byKeyPrefix
argument_list|(
name|id
argument_list|)
argument_list|,
name|user
argument_list|)
return|;
block|}
comment|// Try change triplet
name|Optional
argument_list|<
name|ChangeTriplet
argument_list|>
name|triplet
init|=
name|ChangeTriplet
operator|.
name|parse
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|triplet
operator|.
name|isPresent
argument_list|()
condition|)
block|{
return|return
name|asChangeControls
argument_list|(
name|query
operator|.
name|byBranchKey
argument_list|(
name|triplet
operator|.
name|get
argument_list|()
operator|.
name|branch
argument_list|()
argument_list|,
name|triplet
operator|.
name|get
argument_list|()
operator|.
name|id
argument_list|()
argument_list|)
argument_list|,
name|user
argument_list|)
return|;
block|}
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
DECL|method|asChangeControls (List<ChangeData> cds, CurrentUser user)
specifier|private
name|List
argument_list|<
name|ChangeControl
argument_list|>
name|asChangeControls
parameter_list|(
name|List
argument_list|<
name|ChangeData
argument_list|>
name|cds
parameter_list|,
name|CurrentUser
name|user
parameter_list|)
throws|throws
name|OrmException
block|{
name|List
argument_list|<
name|ChangeControl
argument_list|>
name|ctls
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|cds
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|ChangeData
name|cd
range|:
name|cds
control|)
block|{
name|ctls
operator|.
name|add
argument_list|(
name|cd
operator|.
name|changeControl
argument_list|(
name|user
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|ctls
return|;
block|}
DECL|method|nextPatchSetId (PatchSet.Id id)
specifier|public
specifier|static
name|PatchSet
operator|.
name|Id
name|nextPatchSetId
parameter_list|(
name|PatchSet
operator|.
name|Id
name|id
parameter_list|)
block|{
return|return
operator|new
name|PatchSet
operator|.
name|Id
argument_list|(
name|id
operator|.
name|getParentKey
argument_list|()
argument_list|,
name|id
operator|.
name|get
argument_list|()
operator|+
literal|1
argument_list|)
return|;
block|}
block|}
end_class

end_unit

