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
name|entities
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
name|extensions
operator|.
name|client
operator|.
name|GeneralPreferencesInfo
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
name|config
operator|.
name|DownloadScheme
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
name|DynamicMap
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
name|Extension
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
name|IdString
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
name|ResourceNotFoundException
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
name|UserInitiated
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
name|AccountState
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
name|AccountsUpdate
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
name|StoredPreferences
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

begin_class
annotation|@
name|Singleton
DECL|class|SetPreferences
specifier|public
class|class
name|SetPreferences
implements|implements
name|RestModifyView
argument_list|<
name|AccountResource
argument_list|,
name|GeneralPreferencesInfo
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
DECL|field|accountsUpdateProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|AccountsUpdate
argument_list|>
name|accountsUpdateProvider
decl_stmt|;
DECL|field|downloadSchemes
specifier|private
specifier|final
name|DynamicMap
argument_list|<
name|DownloadScheme
argument_list|>
name|downloadSchemes
decl_stmt|;
annotation|@
name|Inject
DECL|method|SetPreferences ( Provider<CurrentUser> self, PermissionBackend permissionBackend, @UserInitiated Provider<AccountsUpdate> accountsUpdateProvider, DynamicMap<DownloadScheme> downloadSchemes)
name|SetPreferences
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
annotation|@
name|UserInitiated
name|Provider
argument_list|<
name|AccountsUpdate
argument_list|>
name|accountsUpdateProvider
parameter_list|,
name|DynamicMap
argument_list|<
name|DownloadScheme
argument_list|>
name|downloadSchemes
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
name|accountsUpdateProvider
operator|=
name|accountsUpdateProvider
expr_stmt|;
name|this
operator|.
name|downloadSchemes
operator|=
name|downloadSchemes
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (AccountResource rsrc, GeneralPreferencesInfo input)
specifier|public
name|Response
argument_list|<
name|GeneralPreferencesInfo
argument_list|>
name|apply
parameter_list|(
name|AccountResource
name|rsrc
parameter_list|,
name|GeneralPreferencesInfo
name|input
parameter_list|)
throws|throws
name|RestApiException
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
name|MODIFY_ACCOUNT
argument_list|)
expr_stmt|;
block|}
name|checkDownloadScheme
argument_list|(
name|input
operator|.
name|downloadScheme
argument_list|)
expr_stmt|;
name|StoredPreferences
operator|.
name|validateMy
argument_list|(
name|input
operator|.
name|my
argument_list|)
expr_stmt|;
name|Account
operator|.
name|Id
name|id
init|=
name|rsrc
operator|.
name|getUser
argument_list|()
operator|.
name|getAccountId
argument_list|()
decl_stmt|;
return|return
name|Response
operator|.
name|ok
argument_list|(
name|accountsUpdateProvider
operator|.
name|get
argument_list|()
operator|.
name|update
argument_list|(
literal|"Set General Preferences via API"
argument_list|,
name|id
argument_list|,
name|u
lambda|->
name|u
operator|.
name|setGeneralPreferences
argument_list|(
name|input
argument_list|)
argument_list|)
operator|.
name|map
argument_list|(
name|AccountState
operator|::
name|generalPreferences
argument_list|)
operator|.
name|orElseThrow
argument_list|(
parameter_list|()
lambda|->
operator|new
name|ResourceNotFoundException
argument_list|(
name|IdString
operator|.
name|fromDecoded
argument_list|(
name|id
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
DECL|method|checkDownloadScheme (String downloadScheme)
specifier|private
name|void
name|checkDownloadScheme
parameter_list|(
name|String
name|downloadScheme
parameter_list|)
throws|throws
name|BadRequestException
block|{
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|downloadScheme
argument_list|)
condition|)
block|{
return|return;
block|}
for|for
control|(
name|Extension
argument_list|<
name|DownloadScheme
argument_list|>
name|e
range|:
name|downloadSchemes
control|)
block|{
if|if
condition|(
name|e
operator|.
name|getExportName
argument_list|()
operator|.
name|equals
argument_list|(
name|downloadScheme
argument_list|)
operator|&&
name|e
operator|.
name|getProvider
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|isEnabled
argument_list|()
condition|)
block|{
return|return;
block|}
block|}
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"Unsupported download scheme: "
operator|+
name|downloadScheme
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

