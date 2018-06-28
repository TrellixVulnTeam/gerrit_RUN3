begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2017 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.project
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|project
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
name|collect
operator|.
name|ImmutableMap
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
name|AccessSection
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
name|InvalidNameException
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
name|PermissionDeniedException
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
name|api
operator|.
name|access
operator|.
name|ProjectAccessInput
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
name|ChangeJson
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
name|ProjectConfig
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
name|permissions
operator|.
name|PermissionBackend
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
name|permissions
operator|.
name|PermissionBackendException
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
name|permissions
operator|.
name|RefPermission
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
name|update
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
name|update
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
name|List
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
name|ObjectReader
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

begin_class
annotation|@
name|Singleton
DECL|class|CreateAccessChange
specifier|public
class|class
name|CreateAccessChange
implements|implements
name|RestModifyView
argument_list|<
name|ProjectResource
argument_list|,
name|ProjectAccessInput
argument_list|>
block|{
DECL|field|permissionBackend
specifier|private
specifier|final
name|PermissionBackend
name|permissionBackend
decl_stmt|;
DECL|field|seq
specifier|private
specifier|final
name|Sequences
name|seq
decl_stmt|;
DECL|field|changeInserterFactory
specifier|private
specifier|final
name|ChangeInserter
operator|.
name|Factory
name|changeInserterFactory
decl_stmt|;
DECL|field|updateFactory
specifier|private
specifier|final
name|BatchUpdate
operator|.
name|Factory
name|updateFactory
decl_stmt|;
DECL|field|metaDataUpdateFactory
specifier|private
specifier|final
name|Provider
argument_list|<
name|MetaDataUpdate
operator|.
name|User
argument_list|>
name|metaDataUpdateFactory
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
DECL|field|setAccess
specifier|private
specifier|final
name|SetAccessUtil
name|setAccess
decl_stmt|;
DECL|field|jsonFactory
specifier|private
specifier|final
name|ChangeJson
operator|.
name|Factory
name|jsonFactory
decl_stmt|;
annotation|@
name|Inject
DECL|method|CreateAccessChange ( PermissionBackend permissionBackend, ChangeInserter.Factory changeInserterFactory, BatchUpdate.Factory updateFactory, Sequences seq, Provider<MetaDataUpdate.User> metaDataUpdateFactory, Provider<ReviewDb> db, SetAccessUtil accessUtil, ChangeJson.Factory jsonFactory)
name|CreateAccessChange
parameter_list|(
name|PermissionBackend
name|permissionBackend
parameter_list|,
name|ChangeInserter
operator|.
name|Factory
name|changeInserterFactory
parameter_list|,
name|BatchUpdate
operator|.
name|Factory
name|updateFactory
parameter_list|,
name|Sequences
name|seq
parameter_list|,
name|Provider
argument_list|<
name|MetaDataUpdate
operator|.
name|User
argument_list|>
name|metaDataUpdateFactory
parameter_list|,
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|,
name|SetAccessUtil
name|accessUtil
parameter_list|,
name|ChangeJson
operator|.
name|Factory
name|jsonFactory
parameter_list|)
block|{
name|this
operator|.
name|permissionBackend
operator|=
name|permissionBackend
expr_stmt|;
name|this
operator|.
name|seq
operator|=
name|seq
expr_stmt|;
name|this
operator|.
name|changeInserterFactory
operator|=
name|changeInserterFactory
expr_stmt|;
name|this
operator|.
name|updateFactory
operator|=
name|updateFactory
expr_stmt|;
name|this
operator|.
name|metaDataUpdateFactory
operator|=
name|metaDataUpdateFactory
expr_stmt|;
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|setAccess
operator|=
name|accessUtil
expr_stmt|;
name|this
operator|.
name|jsonFactory
operator|=
name|jsonFactory
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (ProjectResource rsrc, ProjectAccessInput input)
specifier|public
name|Response
argument_list|<
name|ChangeInfo
argument_list|>
name|apply
parameter_list|(
name|ProjectResource
name|rsrc
parameter_list|,
name|ProjectAccessInput
name|input
parameter_list|)
throws|throws
name|PermissionBackendException
throws|,
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
throws|,
name|OrmException
throws|,
name|InvalidNameException
throws|,
name|UpdateException
throws|,
name|RestApiException
block|{
name|MetaDataUpdate
operator|.
name|User
name|metaDataUpdateUser
init|=
name|metaDataUpdateFactory
operator|.
name|get
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|AccessSection
argument_list|>
name|removals
init|=
name|setAccess
operator|.
name|getAccessSections
argument_list|(
name|input
operator|.
name|remove
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|AccessSection
argument_list|>
name|additions
init|=
name|setAccess
operator|.
name|getAccessSections
argument_list|(
name|input
operator|.
name|add
argument_list|)
decl_stmt|;
name|PermissionBackend
operator|.
name|ForRef
name|metaRef
init|=
name|permissionBackend
operator|.
name|user
argument_list|(
name|rsrc
operator|.
name|getUser
argument_list|()
argument_list|)
operator|.
name|project
argument_list|(
name|rsrc
operator|.
name|getNameKey
argument_list|()
argument_list|)
operator|.
name|ref
argument_list|(
name|RefNames
operator|.
name|REFS_CONFIG
argument_list|)
decl_stmt|;
try|try
block|{
name|metaRef
operator|.
name|check
argument_list|(
name|RefPermission
operator|.
name|READ
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AuthException
name|denied
parameter_list|)
block|{
throw|throw
operator|new
name|PermissionDeniedException
argument_list|(
name|RefNames
operator|.
name|REFS_CONFIG
operator|+
literal|" not visible"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|rsrc
operator|.
name|getControl
argument_list|()
operator|.
name|isOwner
argument_list|()
condition|)
block|{
try|try
block|{
name|metaRef
operator|.
name|check
argument_list|(
name|RefPermission
operator|.
name|CREATE_CHANGE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AuthException
name|denied
parameter_list|)
block|{
throw|throw
operator|new
name|PermissionDeniedException
argument_list|(
literal|"cannot create change for "
operator|+
name|RefNames
operator|.
name|REFS_CONFIG
argument_list|)
throw|;
block|}
block|}
name|Project
operator|.
name|NameKey
name|newParentProjectName
init|=
name|input
operator|.
name|parent
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
name|input
operator|.
name|parent
argument_list|)
decl_stmt|;
try|try
init|(
name|MetaDataUpdate
name|md
init|=
name|metaDataUpdateUser
operator|.
name|create
argument_list|(
name|rsrc
operator|.
name|getNameKey
argument_list|()
argument_list|)
init|)
block|{
name|ProjectConfig
name|config
init|=
name|ProjectConfig
operator|.
name|read
argument_list|(
name|md
argument_list|)
decl_stmt|;
name|setAccess
operator|.
name|validateChanges
argument_list|(
name|config
argument_list|,
name|removals
argument_list|,
name|additions
argument_list|)
expr_stmt|;
name|setAccess
operator|.
name|applyChanges
argument_list|(
name|config
argument_list|,
name|removals
argument_list|,
name|additions
argument_list|)
expr_stmt|;
try|try
block|{
name|setAccess
operator|.
name|setParentName
argument_list|(
name|rsrc
operator|.
name|getUser
argument_list|()
operator|.
name|asIdentifiedUser
argument_list|()
argument_list|,
name|config
argument_list|,
name|rsrc
operator|.
name|getNameKey
argument_list|()
argument_list|,
name|newParentProjectName
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AuthException
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
name|md
operator|.
name|setMessage
argument_list|(
literal|"Review access change"
argument_list|)
expr_stmt|;
name|md
operator|.
name|setInsertChangeId
argument_list|(
literal|true
argument_list|)
expr_stmt|;
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
name|RevCommit
name|commit
init|=
name|config
operator|.
name|commitToNewRef
argument_list|(
name|md
argument_list|,
operator|new
name|PatchSet
operator|.
name|Id
argument_list|(
name|changeId
argument_list|,
name|Change
operator|.
name|INITIAL_PATCH_SET_ID
argument_list|)
operator|.
name|toRefName
argument_list|()
argument_list|)
decl_stmt|;
try|try
init|(
name|ObjectInserter
name|objInserter
init|=
name|md
operator|.
name|getRepository
argument_list|()
operator|.
name|newObjectInserter
argument_list|()
init|;
name|ObjectReader
name|objReader
operator|=
name|objInserter
operator|.
name|newReader
argument_list|()
init|;
name|RevWalk
name|rw
operator|=
operator|new
name|RevWalk
argument_list|(
name|objReader
argument_list|)
init|;
name|BatchUpdate
name|bu
operator|=
name|updateFactory
operator|.
name|create
argument_list|(
name|db
operator|.
name|get
argument_list|()
argument_list|,
name|rsrc
operator|.
name|getNameKey
argument_list|()
argument_list|,
name|rsrc
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
name|md
operator|.
name|getRepository
argument_list|()
argument_list|,
name|rw
argument_list|,
name|objInserter
argument_list|)
expr_stmt|;
name|ChangeInserter
name|ins
init|=
name|newInserter
argument_list|(
name|changeId
argument_list|,
name|commit
argument_list|)
decl_stmt|;
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
return|return
name|Response
operator|.
name|created
argument_list|(
name|jsonFactory
operator|.
name|noOptions
argument_list|()
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
catch|catch
parameter_list|(
name|InvalidNameException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|// ProjectConfig doesn't currently support fusing into a BatchUpdate.
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|newInserter (Change.Id changeId, RevCommit commit)
specifier|private
name|ChangeInserter
name|newInserter
parameter_list|(
name|Change
operator|.
name|Id
name|changeId
parameter_list|,
name|RevCommit
name|commit
parameter_list|)
block|{
return|return
name|changeInserterFactory
operator|.
name|create
argument_list|(
name|changeId
argument_list|,
name|commit
argument_list|,
name|RefNames
operator|.
name|REFS_CONFIG
argument_list|)
operator|.
name|setMessage
argument_list|(
comment|// Same message as in ReceiveCommits.CreateRequest.
name|ApprovalsUtil
operator|.
name|renderMessageWithApprovals
argument_list|(
literal|1
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|()
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setValidate
argument_list|(
literal|false
argument_list|)
operator|.
name|setUpdateRef
argument_list|(
literal|false
argument_list|)
return|;
block|}
block|}
end_class

end_unit

