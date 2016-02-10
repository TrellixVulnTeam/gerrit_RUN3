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
name|Capable
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
name|ChangeStatus
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
name|BadRequestException
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
name|MethodNotAllowedException
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
name|restapi
operator|.
name|TopLevelResource
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
name|UnprocessableEntityException
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
name|reviewdb
operator|.
name|client
operator|.
name|RefNames
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
name|ChangeFinder
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
name|Sequences
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
name|GerritServerConfig
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
name|InvalidChangeOperationException
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
name|ProjectResource
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
name|ProjectsCollection
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
name|Config
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
name|io
operator|.
name|UnsupportedEncodingException
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimeZone
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|CreateChange
specifier|public
class|class
name|CreateChange
implements|implements
name|RestModifyView
argument_list|<
name|TopLevelResource
argument_list|,
name|ChangeInfo
argument_list|>
block|{
DECL|field|db
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
decl_stmt|;
DECL|field|gitManager
specifier|private
specifier|final
name|GitRepositoryManager
name|gitManager
decl_stmt|;
DECL|field|seq
specifier|private
specifier|final
name|Sequences
name|seq
decl_stmt|;
DECL|field|serverTimeZone
specifier|private
specifier|final
name|TimeZone
name|serverTimeZone
decl_stmt|;
DECL|field|user
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|user
decl_stmt|;
DECL|field|projectsCollection
specifier|private
specifier|final
name|ProjectsCollection
name|projectsCollection
decl_stmt|;
DECL|field|changeInserterFactory
specifier|private
specifier|final
name|ChangeInserter
operator|.
name|Factory
name|changeInserterFactory
decl_stmt|;
DECL|field|jsonFactory
specifier|private
specifier|final
name|ChangeJson
operator|.
name|Factory
name|jsonFactory
decl_stmt|;
DECL|field|changeFinder
specifier|private
specifier|final
name|ChangeFinder
name|changeFinder
decl_stmt|;
DECL|field|updateFactory
specifier|private
specifier|final
name|BatchUpdate
operator|.
name|Factory
name|updateFactory
decl_stmt|;
DECL|field|psUtil
specifier|private
specifier|final
name|PatchSetUtil
name|psUtil
decl_stmt|;
DECL|field|allowDrafts
specifier|private
specifier|final
name|boolean
name|allowDrafts
decl_stmt|;
annotation|@
name|Inject
DECL|method|CreateChange (Provider<ReviewDb> db, GitRepositoryManager gitManager, Sequences seq, @GerritPersonIdent PersonIdent myIdent, Provider<CurrentUser> user, ProjectsCollection projectsCollection, ChangeInserter.Factory changeInserterFactory, ChangeJson.Factory json, ChangeFinder changeFinder, BatchUpdate.Factory updateFactory, PatchSetUtil psUtil, @GerritServerConfig Config config)
name|CreateChange
parameter_list|(
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|,
name|GitRepositoryManager
name|gitManager
parameter_list|,
name|Sequences
name|seq
parameter_list|,
annotation|@
name|GerritPersonIdent
name|PersonIdent
name|myIdent
parameter_list|,
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|user
parameter_list|,
name|ProjectsCollection
name|projectsCollection
parameter_list|,
name|ChangeInserter
operator|.
name|Factory
name|changeInserterFactory
parameter_list|,
name|ChangeJson
operator|.
name|Factory
name|json
parameter_list|,
name|ChangeFinder
name|changeFinder
parameter_list|,
name|BatchUpdate
operator|.
name|Factory
name|updateFactory
parameter_list|,
name|PatchSetUtil
name|psUtil
parameter_list|,
annotation|@
name|GerritServerConfig
name|Config
name|config
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
name|gitManager
operator|=
name|gitManager
expr_stmt|;
name|this
operator|.
name|seq
operator|=
name|seq
expr_stmt|;
name|this
operator|.
name|serverTimeZone
operator|=
name|myIdent
operator|.
name|getTimeZone
argument_list|()
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|projectsCollection
operator|=
name|projectsCollection
expr_stmt|;
name|this
operator|.
name|changeInserterFactory
operator|=
name|changeInserterFactory
expr_stmt|;
name|this
operator|.
name|jsonFactory
operator|=
name|json
expr_stmt|;
name|this
operator|.
name|changeFinder
operator|=
name|changeFinder
expr_stmt|;
name|this
operator|.
name|updateFactory
operator|=
name|updateFactory
expr_stmt|;
name|this
operator|.
name|psUtil
operator|=
name|psUtil
expr_stmt|;
name|this
operator|.
name|allowDrafts
operator|=
name|config
operator|.
name|getBoolean
argument_list|(
literal|"change"
argument_list|,
literal|"allowDrafts"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (TopLevelResource parent, ChangeInfo input)
specifier|public
name|Response
argument_list|<
name|ChangeInfo
argument_list|>
name|apply
parameter_list|(
name|TopLevelResource
name|parent
parameter_list|,
name|ChangeInfo
name|input
parameter_list|)
throws|throws
name|OrmException
throws|,
name|IOException
throws|,
name|InvalidChangeOperationException
throws|,
name|RestApiException
throws|,
name|UpdateException
block|{
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|input
operator|.
name|project
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"project must be non-empty"
argument_list|)
throw|;
block|}
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|input
operator|.
name|branch
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"branch must be non-empty"
argument_list|)
throw|;
block|}
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|input
operator|.
name|subject
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"commit message must be non-empty"
argument_list|)
throw|;
block|}
if|if
condition|(
name|input
operator|.
name|status
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|input
operator|.
name|status
operator|!=
name|ChangeStatus
operator|.
name|NEW
operator|&&
name|input
operator|.
name|status
operator|!=
name|ChangeStatus
operator|.
name|DRAFT
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"unsupported change status"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|allowDrafts
operator|&&
name|input
operator|.
name|status
operator|==
name|ChangeStatus
operator|.
name|DRAFT
condition|)
block|{
throw|throw
operator|new
name|MethodNotAllowedException
argument_list|(
literal|"draft workflow is disabled"
argument_list|)
throw|;
block|}
block|}
name|String
name|refName
init|=
name|RefNames
operator|.
name|fullName
argument_list|(
name|input
operator|.
name|branch
argument_list|)
decl_stmt|;
name|ProjectResource
name|rsrc
init|=
name|projectsCollection
operator|.
name|parse
argument_list|(
name|input
operator|.
name|project
argument_list|)
decl_stmt|;
name|Capable
name|r
init|=
name|rsrc
operator|.
name|getControl
argument_list|()
operator|.
name|canPushToAtLeastOneRef
argument_list|()
decl_stmt|;
if|if
condition|(
name|r
operator|!=
name|Capable
operator|.
name|OK
condition|)
block|{
throw|throw
operator|new
name|AuthException
argument_list|(
name|r
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
name|RefControl
name|refControl
init|=
name|rsrc
operator|.
name|getControl
argument_list|()
operator|.
name|controlForRef
argument_list|(
name|refName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|refControl
operator|.
name|canUpload
argument_list|()
operator|||
operator|!
name|refControl
operator|.
name|canRead
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AuthException
argument_list|(
literal|"cannot upload review"
argument_list|)
throw|;
block|}
name|Project
operator|.
name|NameKey
name|project
init|=
name|rsrc
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
name|rw
operator|=
operator|new
name|RevWalk
argument_list|(
name|git
argument_list|)
init|)
block|{
name|ObjectId
name|parentCommit
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|groups
decl_stmt|;
if|if
condition|(
name|input
operator|.
name|baseChange
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|ChangeControl
argument_list|>
name|ctls
init|=
name|changeFinder
operator|.
name|find
argument_list|(
name|input
operator|.
name|baseChange
argument_list|,
name|rsrc
operator|.
name|getControl
argument_list|()
operator|.
name|getUser
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|ctls
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|InvalidChangeOperationException
argument_list|(
literal|"Base change not found: "
operator|+
name|input
operator|.
name|baseChange
argument_list|)
throw|;
block|}
name|ChangeControl
name|ctl
init|=
name|Iterables
operator|.
name|getOnlyElement
argument_list|(
name|ctls
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|ctl
operator|.
name|isVisible
argument_list|(
name|db
operator|.
name|get
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidChangeOperationException
argument_list|(
literal|"Base change not found: "
operator|+
name|input
operator|.
name|baseChange
argument_list|)
throw|;
block|}
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
name|ctl
operator|.
name|getNotes
argument_list|()
argument_list|)
decl_stmt|;
name|parentCommit
operator|=
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
expr_stmt|;
name|groups
operator|=
name|ps
operator|.
name|getGroups
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|Ref
name|destRef
init|=
name|git
operator|.
name|getRefDatabase
argument_list|()
operator|.
name|exactRef
argument_list|(
name|refName
argument_list|)
decl_stmt|;
if|if
condition|(
name|destRef
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|UnprocessableEntityException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Branch %s does not exist."
argument_list|,
name|refName
argument_list|)
argument_list|)
throw|;
block|}
name|parentCommit
operator|=
name|destRef
operator|.
name|getObjectId
argument_list|()
expr_stmt|;
name|groups
operator|=
name|Collections
operator|.
name|emptyList
argument_list|()
expr_stmt|;
block|}
name|RevCommit
name|mergeTip
init|=
name|rw
operator|.
name|parseCommit
argument_list|(
name|parentCommit
argument_list|)
decl_stmt|;
name|Timestamp
name|now
init|=
name|TimeUtil
operator|.
name|nowTs
argument_list|()
decl_stmt|;
name|IdentifiedUser
name|me
init|=
name|user
operator|.
name|get
argument_list|()
operator|.
name|asIdentifiedUser
argument_list|()
decl_stmt|;
name|PersonIdent
name|author
init|=
name|me
operator|.
name|newCommitterIdent
argument_list|(
name|now
argument_list|,
name|serverTimeZone
argument_list|)
decl_stmt|;
name|ObjectId
name|id
init|=
name|ChangeIdUtil
operator|.
name|computeChangeId
argument_list|(
name|mergeTip
operator|.
name|getTree
argument_list|()
argument_list|,
name|mergeTip
argument_list|,
name|author
argument_list|,
name|author
argument_list|,
name|input
operator|.
name|subject
argument_list|)
decl_stmt|;
name|String
name|commitMessage
init|=
name|ChangeIdUtil
operator|.
name|insertId
argument_list|(
name|input
operator|.
name|subject
argument_list|,
name|id
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
name|RevCommit
name|c
init|=
name|newCommit
argument_list|(
name|oi
argument_list|,
name|rw
argument_list|,
name|author
argument_list|,
name|mergeTip
argument_list|,
name|commitMessage
argument_list|)
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
name|ChangeInserter
name|ins
init|=
name|changeInserterFactory
operator|.
name|create
argument_list|(
name|changeId
argument_list|,
name|c
argument_list|,
name|refName
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
decl_stmt|;
name|ins
operator|.
name|setMessage
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Uploaded patch set %s."
argument_list|,
name|ins
operator|.
name|getPatchSetId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|topic
init|=
name|input
operator|.
name|topic
decl_stmt|;
if|if
condition|(
name|topic
operator|!=
literal|null
condition|)
block|{
name|topic
operator|=
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|topic
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ins
operator|.
name|setTopic
argument_list|(
name|topic
argument_list|)
expr_stmt|;
name|ins
operator|.
name|setDraft
argument_list|(
name|input
operator|.
name|status
operator|!=
literal|null
operator|&&
name|input
operator|.
name|status
operator|==
name|ChangeStatus
operator|.
name|DRAFT
argument_list|)
expr_stmt|;
name|ins
operator|.
name|setGroups
argument_list|(
name|groups
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
name|me
argument_list|,
name|now
argument_list|)
init|)
block|{
name|bu
operator|.
name|setRepository
argument_list|(
name|git
argument_list|,
name|rw
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
name|ChangeJson
name|json
init|=
name|jsonFactory
operator|.
name|create
argument_list|(
name|ChangeJson
operator|.
name|NO_OPTIONS
argument_list|)
decl_stmt|;
return|return
name|Response
operator|.
name|created
argument_list|(
name|json
operator|.
name|format
argument_list|(
name|ins
operator|.
name|getChange
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
DECL|method|newCommit (ObjectInserter oi, RevWalk rw, PersonIdent authorIdent, RevCommit mergeTip, String commitMessage)
specifier|private
specifier|static
name|RevCommit
name|newCommit
parameter_list|(
name|ObjectInserter
name|oi
parameter_list|,
name|RevWalk
name|rw
parameter_list|,
name|PersonIdent
name|authorIdent
parameter_list|,
name|RevCommit
name|mergeTip
parameter_list|,
name|String
name|commitMessage
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
name|setTreeId
argument_list|(
name|mergeTip
operator|.
name|getTree
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|commit
operator|.
name|setParentId
argument_list|(
name|mergeTip
argument_list|)
expr_stmt|;
name|commit
operator|.
name|setAuthor
argument_list|(
name|authorIdent
argument_list|)
expr_stmt|;
name|commit
operator|.
name|setCommitter
argument_list|(
name|authorIdent
argument_list|)
expr_stmt|;
name|commit
operator|.
name|setMessage
argument_list|(
name|commitMessage
argument_list|)
expr_stmt|;
return|return
name|rw
operator|.
name|parseCommit
argument_list|(
name|insert
argument_list|(
name|oi
argument_list|,
name|commit
argument_list|)
argument_list|)
return|;
block|}
DECL|method|insert (ObjectInserter inserter, CommitBuilder commit)
specifier|private
specifier|static
name|ObjectId
name|insert
parameter_list|(
name|ObjectInserter
name|inserter
parameter_list|,
name|CommitBuilder
name|commit
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnsupportedEncodingException
block|{
name|ObjectId
name|id
init|=
name|inserter
operator|.
name|insert
argument_list|(
name|commit
argument_list|)
decl_stmt|;
name|inserter
operator|.
name|flush
argument_list|()
expr_stmt|;
return|return
name|id
return|;
block|}
block|}
end_class

end_unit

