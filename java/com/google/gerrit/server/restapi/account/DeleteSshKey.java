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
DECL|package|com.google.gerrit.server.restapi.account
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
name|account
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
name|exceptions
operator|.
name|StorageException
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
name|Input
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
name|account
operator|.
name|AccountResource
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
name|VersionedAuthorizedKeys
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
name|GlobalPermission
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
name|ssh
operator|.
name|SshKeyCache
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
name|errors
operator|.
name|RepositoryNotFoundException
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|DeleteSshKey
specifier|public
class|class
name|DeleteSshKey
implements|implements
name|RestModifyView
argument_list|<
name|AccountResource
operator|.
name|SshKey
argument_list|,
name|Input
argument_list|>
block|{
DECL|field|self
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|self
decl_stmt|;
DECL|field|permissionBackend
specifier|private
specifier|final
name|PermissionBackend
name|permissionBackend
decl_stmt|;
DECL|field|authorizedKeys
specifier|private
specifier|final
name|VersionedAuthorizedKeys
operator|.
name|Accessor
name|authorizedKeys
decl_stmt|;
DECL|field|sshKeyCache
specifier|private
specifier|final
name|SshKeyCache
name|sshKeyCache
decl_stmt|;
annotation|@
name|Inject
DECL|method|DeleteSshKey ( Provider<CurrentUser> self, PermissionBackend permissionBackend, VersionedAuthorizedKeys.Accessor authorizedKeys, SshKeyCache sshKeyCache)
name|DeleteSshKey
parameter_list|(
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|self
parameter_list|,
name|PermissionBackend
name|permissionBackend
parameter_list|,
name|VersionedAuthorizedKeys
operator|.
name|Accessor
name|authorizedKeys
parameter_list|,
name|SshKeyCache
name|sshKeyCache
parameter_list|)
block|{
name|this
operator|.
name|self
operator|=
name|self
expr_stmt|;
name|this
operator|.
name|permissionBackend
operator|=
name|permissionBackend
expr_stmt|;
name|this
operator|.
name|authorizedKeys
operator|=
name|authorizedKeys
expr_stmt|;
name|this
operator|.
name|sshKeyCache
operator|=
name|sshKeyCache
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (AccountResource.SshKey rsrc, Input input)
specifier|public
name|Response
argument_list|<
name|?
argument_list|>
name|apply
parameter_list|(
name|AccountResource
operator|.
name|SshKey
name|rsrc
parameter_list|,
name|Input
name|input
parameter_list|)
throws|throws
name|AuthException
throws|,
name|StorageException
throws|,
name|RepositoryNotFoundException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
throws|,
name|PermissionBackendException
block|{
if|if
condition|(
operator|!
name|self
operator|.
name|get
argument_list|()
operator|.
name|hasSameAccountId
argument_list|(
name|rsrc
operator|.
name|getUser
argument_list|()
argument_list|)
condition|)
block|{
name|permissionBackend
operator|.
name|currentUser
argument_list|()
operator|.
name|check
argument_list|(
name|GlobalPermission
operator|.
name|ADMINISTRATE_SERVER
argument_list|)
expr_stmt|;
block|}
name|authorizedKeys
operator|.
name|deleteKey
argument_list|(
name|rsrc
operator|.
name|getUser
argument_list|()
operator|.
name|getAccountId
argument_list|()
argument_list|,
name|rsrc
operator|.
name|getSshKey
argument_list|()
operator|.
name|seq
argument_list|()
argument_list|)
expr_stmt|;
name|rsrc
operator|.
name|getUser
argument_list|()
operator|.
name|getUserName
argument_list|()
operator|.
name|ifPresent
argument_list|(
name|sshKeyCache
operator|::
name|evict
argument_list|)
expr_stmt|;
return|return
name|Response
operator|.
name|none
argument_list|()
return|;
block|}
block|}
end_class

end_unit

