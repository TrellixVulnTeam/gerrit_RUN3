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
DECL|package|com.google.gerrit.server.restapi.project
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|restapi
operator|.
name|project
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Constants
operator|.
name|R_TAGS
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
name|api
operator|.
name|projects
operator|.
name|TagInfo
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
name|projects
operator|.
name|TagInput
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
name|WebLinks
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
name|TagCache
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
name|project
operator|.
name|NoSuchProjectException
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
name|RefUtil
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
name|RefUtil
operator|.
name|InvalidRevisionException
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
name|assistedinject
operator|.
name|Assisted
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
name|TimeZone
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
name|lib
operator|.
name|Constants
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
name|RevObject
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

begin_class
DECL|class|CreateTag
specifier|public
class|class
name|CreateTag
implements|implements
name|RestModifyView
argument_list|<
name|ProjectResource
argument_list|,
name|TagInput
argument_list|>
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
name|CreateTag
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (String ref)
name|CreateTag
name|create
parameter_list|(
name|String
name|ref
parameter_list|)
function_decl|;
block|}
DECL|field|permissionBackend
specifier|private
specifier|final
name|PermissionBackend
name|permissionBackend
decl_stmt|;
DECL|field|identifiedUser
specifier|private
specifier|final
name|Provider
argument_list|<
name|IdentifiedUser
argument_list|>
name|identifiedUser
decl_stmt|;
DECL|field|repoManager
specifier|private
specifier|final
name|GitRepositoryManager
name|repoManager
decl_stmt|;
DECL|field|tagCache
specifier|private
specifier|final
name|TagCache
name|tagCache
decl_stmt|;
DECL|field|referenceUpdated
specifier|private
specifier|final
name|GitReferenceUpdated
name|referenceUpdated
decl_stmt|;
DECL|field|links
specifier|private
specifier|final
name|WebLinks
name|links
decl_stmt|;
DECL|field|ref
specifier|private
name|String
name|ref
decl_stmt|;
annotation|@
name|Inject
DECL|method|CreateTag ( PermissionBackend permissionBackend, Provider<IdentifiedUser> identifiedUser, GitRepositoryManager repoManager, TagCache tagCache, GitReferenceUpdated referenceUpdated, WebLinks webLinks, @Assisted String ref)
name|CreateTag
parameter_list|(
name|PermissionBackend
name|permissionBackend
parameter_list|,
name|Provider
argument_list|<
name|IdentifiedUser
argument_list|>
name|identifiedUser
parameter_list|,
name|GitRepositoryManager
name|repoManager
parameter_list|,
name|TagCache
name|tagCache
parameter_list|,
name|GitReferenceUpdated
name|referenceUpdated
parameter_list|,
name|WebLinks
name|webLinks
parameter_list|,
annotation|@
name|Assisted
name|String
name|ref
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
name|identifiedUser
operator|=
name|identifiedUser
expr_stmt|;
name|this
operator|.
name|repoManager
operator|=
name|repoManager
expr_stmt|;
name|this
operator|.
name|tagCache
operator|=
name|tagCache
expr_stmt|;
name|this
operator|.
name|referenceUpdated
operator|=
name|referenceUpdated
expr_stmt|;
name|this
operator|.
name|links
operator|=
name|webLinks
expr_stmt|;
name|this
operator|.
name|ref
operator|=
name|ref
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (ProjectResource resource, TagInput input)
specifier|public
name|TagInfo
name|apply
parameter_list|(
name|ProjectResource
name|resource
parameter_list|,
name|TagInput
name|input
parameter_list|)
throws|throws
name|RestApiException
throws|,
name|IOException
throws|,
name|PermissionBackendException
throws|,
name|NoSuchProjectException
block|{
if|if
condition|(
name|input
operator|==
literal|null
condition|)
block|{
name|input
operator|=
operator|new
name|TagInput
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|input
operator|.
name|ref
operator|!=
literal|null
operator|&&
operator|!
name|ref
operator|.
name|equals
argument_list|(
name|input
operator|.
name|ref
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"ref must match URL"
argument_list|)
throw|;
block|}
if|if
condition|(
name|input
operator|.
name|revision
operator|==
literal|null
condition|)
block|{
name|input
operator|.
name|revision
operator|=
name|Constants
operator|.
name|HEAD
expr_stmt|;
block|}
name|ref
operator|=
name|RefUtil
operator|.
name|normalizeTagRef
argument_list|(
name|ref
argument_list|)
expr_stmt|;
name|PermissionBackend
operator|.
name|ForRef
name|perm
init|=
name|permissionBackend
operator|.
name|user
argument_list|(
name|identifiedUser
argument_list|)
operator|.
name|project
argument_list|(
name|resource
operator|.
name|getNameKey
argument_list|()
argument_list|)
operator|.
name|ref
argument_list|(
name|ref
argument_list|)
decl_stmt|;
try|try
init|(
name|Repository
name|repo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|resource
operator|.
name|getNameKey
argument_list|()
argument_list|)
init|)
block|{
name|ObjectId
name|revid
init|=
name|RefUtil
operator|.
name|parseBaseRevision
argument_list|(
name|repo
argument_list|,
name|resource
operator|.
name|getNameKey
argument_list|()
argument_list|,
name|input
operator|.
name|revision
argument_list|)
decl_stmt|;
name|RevWalk
name|rw
init|=
name|RefUtil
operator|.
name|verifyConnected
argument_list|(
name|repo
argument_list|,
name|revid
argument_list|)
decl_stmt|;
name|RevObject
name|object
init|=
name|rw
operator|.
name|parseAny
argument_list|(
name|revid
argument_list|)
decl_stmt|;
name|rw
operator|.
name|reset
argument_list|()
expr_stmt|;
name|boolean
name|isAnnotated
init|=
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|input
operator|.
name|message
argument_list|)
operator|!=
literal|null
decl_stmt|;
name|boolean
name|isSigned
init|=
name|isAnnotated
operator|&&
name|input
operator|.
name|message
operator|.
name|contains
argument_list|(
literal|"-----BEGIN PGP SIGNATURE-----\n"
argument_list|)
decl_stmt|;
if|if
condition|(
name|isSigned
condition|)
block|{
throw|throw
operator|new
name|MethodNotAllowedException
argument_list|(
literal|"Cannot create signed tag \""
operator|+
name|ref
operator|+
literal|"\""
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|isAnnotated
operator|&&
operator|!
name|check
argument_list|(
name|perm
argument_list|,
name|RefPermission
operator|.
name|CREATE_TAG
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AuthException
argument_list|(
literal|"Cannot create annotated tag \""
operator|+
name|ref
operator|+
literal|"\""
argument_list|)
throw|;
block|}
else|else
block|{
name|perm
operator|.
name|check
argument_list|(
name|RefPermission
operator|.
name|CREATE
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|repo
operator|.
name|getRefDatabase
argument_list|()
operator|.
name|exactRef
argument_list|(
name|ref
argument_list|)
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
literal|"tag \""
operator|+
name|ref
operator|+
literal|"\" already exists"
argument_list|)
throw|;
block|}
try|try
init|(
name|Git
name|git
init|=
operator|new
name|Git
argument_list|(
name|repo
argument_list|)
init|)
block|{
name|TagCommand
name|tag
init|=
name|git
operator|.
name|tag
argument_list|()
operator|.
name|setObjectId
argument_list|(
name|object
argument_list|)
operator|.
name|setName
argument_list|(
name|ref
operator|.
name|substring
argument_list|(
name|R_TAGS
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setAnnotated
argument_list|(
name|isAnnotated
argument_list|)
operator|.
name|setSigned
argument_list|(
name|isSigned
argument_list|)
decl_stmt|;
if|if
condition|(
name|isAnnotated
condition|)
block|{
name|tag
operator|.
name|setMessage
argument_list|(
name|input
operator|.
name|message
argument_list|)
operator|.
name|setTagger
argument_list|(
name|identifiedUser
operator|.
name|get
argument_list|()
operator|.
name|newCommitterIdent
argument_list|(
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|,
name|TimeZone
operator|.
name|getDefault
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Ref
name|result
init|=
name|tag
operator|.
name|call
argument_list|()
decl_stmt|;
name|tagCache
operator|.
name|updateFastForward
argument_list|(
name|resource
operator|.
name|getNameKey
argument_list|()
argument_list|,
name|ref
argument_list|,
name|ObjectId
operator|.
name|zeroId
argument_list|()
argument_list|,
name|result
operator|.
name|getObjectId
argument_list|()
argument_list|)
expr_stmt|;
name|referenceUpdated
operator|.
name|fire
argument_list|(
name|resource
operator|.
name|getNameKey
argument_list|()
argument_list|,
name|ref
argument_list|,
name|ObjectId
operator|.
name|zeroId
argument_list|()
argument_list|,
name|result
operator|.
name|getObjectId
argument_list|()
argument_list|,
name|identifiedUser
operator|.
name|get
argument_list|()
operator|.
name|getAccount
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
name|RevWalk
name|w
init|=
operator|new
name|RevWalk
argument_list|(
name|repo
argument_list|)
init|)
block|{
return|return
name|ListTags
operator|.
name|createTagInfo
argument_list|(
name|perm
argument_list|,
name|result
argument_list|,
name|w
argument_list|,
name|resource
operator|.
name|getProjectState
argument_list|()
argument_list|,
name|links
argument_list|)
return|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|InvalidRevisionException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"Invalid base revision"
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|GitAPIException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cannot create tag \""
operator|+
name|ref
operator|+
literal|"\""
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|check (PermissionBackend.ForRef perm, RefPermission permission)
specifier|private
specifier|static
name|boolean
name|check
parameter_list|(
name|PermissionBackend
operator|.
name|ForRef
name|perm
parameter_list|,
name|RefPermission
name|permission
parameter_list|)
throws|throws
name|PermissionBackendException
block|{
try|try
block|{
name|perm
operator|.
name|check
argument_list|(
name|permission
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|AuthException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

