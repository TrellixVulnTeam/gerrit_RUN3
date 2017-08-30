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
name|gerrit
operator|.
name|extensions
operator|.
name|api
operator|.
name|changes
operator|.
name|CherryPickInput
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
name|Branch
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
name|git
operator|.
name|IntegrationException
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
name|CommitResource
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
name|RetryHelper
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
name|RetryingRestModifyView
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
name|revwalk
operator|.
name|RevCommit
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|CherryPickCommit
specifier|public
class|class
name|CherryPickCommit
extends|extends
name|RetryingRestModifyView
argument_list|<
name|CommitResource
argument_list|,
name|CherryPickInput
argument_list|,
name|ChangeInfo
argument_list|>
block|{
DECL|field|permissionBackend
specifier|private
specifier|final
name|PermissionBackend
name|permissionBackend
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
DECL|field|cherryPickChange
specifier|private
specifier|final
name|CherryPickChange
name|cherryPickChange
decl_stmt|;
DECL|field|json
specifier|private
specifier|final
name|ChangeJson
operator|.
name|Factory
name|json
decl_stmt|;
annotation|@
name|Inject
DECL|method|CherryPickCommit ( RetryHelper retryHelper, Provider<CurrentUser> user, CherryPickChange cherryPickChange, ChangeJson.Factory json, PermissionBackend permissionBackend)
name|CherryPickCommit
parameter_list|(
name|RetryHelper
name|retryHelper
parameter_list|,
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|user
parameter_list|,
name|CherryPickChange
name|cherryPickChange
parameter_list|,
name|ChangeJson
operator|.
name|Factory
name|json
parameter_list|,
name|PermissionBackend
name|permissionBackend
parameter_list|)
block|{
name|super
argument_list|(
name|retryHelper
argument_list|)
expr_stmt|;
name|this
operator|.
name|permissionBackend
operator|=
name|permissionBackend
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|cherryPickChange
operator|=
name|cherryPickChange
expr_stmt|;
name|this
operator|.
name|json
operator|=
name|json
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|applyImpl ( BatchUpdate.Factory updateFactory, CommitResource rsrc, CherryPickInput input)
specifier|public
name|ChangeInfo
name|applyImpl
parameter_list|(
name|BatchUpdate
operator|.
name|Factory
name|updateFactory
parameter_list|,
name|CommitResource
name|rsrc
parameter_list|,
name|CherryPickInput
name|input
parameter_list|)
throws|throws
name|OrmException
throws|,
name|IOException
throws|,
name|UpdateException
throws|,
name|RestApiException
throws|,
name|PermissionBackendException
throws|,
name|ConfigInvalidException
throws|,
name|NoSuchProjectException
block|{
name|RevCommit
name|commit
init|=
name|rsrc
operator|.
name|getCommit
argument_list|()
decl_stmt|;
name|String
name|message
init|=
name|Strings
operator|.
name|nullToEmpty
argument_list|(
name|input
operator|.
name|message
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|input
operator|.
name|message
operator|=
name|message
operator|.
name|isEmpty
argument_list|()
condition|?
name|commit
operator|.
name|getFullMessage
argument_list|()
else|:
name|message
expr_stmt|;
name|String
name|destination
init|=
name|Strings
operator|.
name|nullToEmpty
argument_list|(
name|input
operator|.
name|destination
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|input
operator|.
name|parent
operator|=
name|input
operator|.
name|parent
operator|==
literal|null
condition|?
literal|1
else|:
name|input
operator|.
name|parent
expr_stmt|;
name|Project
operator|.
name|NameKey
name|projectName
init|=
name|rsrc
operator|.
name|getProjectState
argument_list|()
operator|.
name|getProject
argument_list|()
operator|.
name|getNameKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|destination
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"destination must be non-empty"
argument_list|)
throw|;
block|}
name|String
name|refName
init|=
name|RefNames
operator|.
name|fullName
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|CreateChange
operator|.
name|checkValidCLA
argument_list|(
name|rsrc
operator|.
name|getProjectState
argument_list|()
operator|.
name|controlFor
argument_list|(
name|user
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|permissionBackend
operator|.
name|user
argument_list|(
name|user
argument_list|)
operator|.
name|project
argument_list|(
name|projectName
argument_list|)
operator|.
name|ref
argument_list|(
name|refName
argument_list|)
operator|.
name|check
argument_list|(
name|RefPermission
operator|.
name|CREATE_CHANGE
argument_list|)
expr_stmt|;
try|try
block|{
name|Change
operator|.
name|Id
name|cherryPickedChangeId
init|=
name|cherryPickChange
operator|.
name|cherryPick
argument_list|(
name|updateFactory
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|projectName
argument_list|,
name|commit
argument_list|,
name|input
argument_list|,
operator|new
name|Branch
operator|.
name|NameKey
argument_list|(
name|rsrc
operator|.
name|getProjectState
argument_list|()
operator|.
name|getProject
argument_list|()
operator|.
name|getNameKey
argument_list|()
argument_list|,
name|refName
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|json
operator|.
name|noOptions
argument_list|()
operator|.
name|format
argument_list|(
name|projectName
argument_list|,
name|cherryPickedChangeId
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|InvalidChangeOperationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IntegrationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

