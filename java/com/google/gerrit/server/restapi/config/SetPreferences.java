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
DECL|package|com.google.gerrit.server.restapi.config
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
name|config
package|;
end_package

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
name|config
operator|.
name|ConfigUtil
operator|.
name|loadSection
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
name|config
operator|.
name|ConfigUtil
operator|.
name|skipField
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
name|config
operator|.
name|ConfigUtil
operator|.
name|storeSection
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
name|restapi
operator|.
name|config
operator|.
name|GetPreferences
operator|.
name|readFromGit
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
name|GlobalCapability
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
name|annotations
operator|.
name|RequiresCapability
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
name|account
operator|.
name|GeneralPreferencesLoader
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
name|VersionedAccountPreferences
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
name|AllUsersName
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
name|ConfigResource
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
name|UserConfigSections
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
name|lang
operator|.
name|reflect
operator|.
name|Field
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
annotation|@
name|RequiresCapability
argument_list|(
name|GlobalCapability
operator|.
name|ADMINISTRATE_SERVER
argument_list|)
annotation|@
name|Singleton
DECL|class|SetPreferences
specifier|public
class|class
name|SetPreferences
implements|implements
name|RestModifyView
argument_list|<
name|ConfigResource
argument_list|,
name|GeneralPreferencesInfo
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
name|SetPreferences
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|loader
specifier|private
specifier|final
name|GeneralPreferencesLoader
name|loader
decl_stmt|;
DECL|field|gitManager
specifier|private
specifier|final
name|GitRepositoryManager
name|gitManager
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
DECL|field|allUsersName
specifier|private
specifier|final
name|AllUsersName
name|allUsersName
decl_stmt|;
DECL|field|accountCache
specifier|private
specifier|final
name|AccountCache
name|accountCache
decl_stmt|;
annotation|@
name|Inject
DECL|method|SetPreferences ( GeneralPreferencesLoader loader, GitRepositoryManager gitManager, Provider<MetaDataUpdate.User> metaDataUpdateFactory, AllUsersName allUsersName, AccountCache accountCache)
name|SetPreferences
parameter_list|(
name|GeneralPreferencesLoader
name|loader
parameter_list|,
name|GitRepositoryManager
name|gitManager
parameter_list|,
name|Provider
argument_list|<
name|MetaDataUpdate
operator|.
name|User
argument_list|>
name|metaDataUpdateFactory
parameter_list|,
name|AllUsersName
name|allUsersName
parameter_list|,
name|AccountCache
name|accountCache
parameter_list|)
block|{
name|this
operator|.
name|loader
operator|=
name|loader
expr_stmt|;
name|this
operator|.
name|gitManager
operator|=
name|gitManager
expr_stmt|;
name|this
operator|.
name|metaDataUpdateFactory
operator|=
name|metaDataUpdateFactory
expr_stmt|;
name|this
operator|.
name|allUsersName
operator|=
name|allUsersName
expr_stmt|;
name|this
operator|.
name|accountCache
operator|=
name|accountCache
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (ConfigResource rsrc, GeneralPreferencesInfo i)
specifier|public
name|GeneralPreferencesInfo
name|apply
parameter_list|(
name|ConfigResource
name|rsrc
parameter_list|,
name|GeneralPreferencesInfo
name|i
parameter_list|)
throws|throws
name|BadRequestException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
if|if
condition|(
operator|!
name|hasSetFields
argument_list|(
name|i
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"unsupported option"
argument_list|)
throw|;
block|}
return|return
name|writeToGit
argument_list|(
name|readFromGit
argument_list|(
name|gitManager
argument_list|,
name|loader
argument_list|,
name|allUsersName
argument_list|,
name|i
argument_list|)
argument_list|)
return|;
block|}
DECL|method|writeToGit (GeneralPreferencesInfo i)
specifier|private
name|GeneralPreferencesInfo
name|writeToGit
parameter_list|(
name|GeneralPreferencesInfo
name|i
parameter_list|)
throws|throws
name|RepositoryNotFoundException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
throws|,
name|BadRequestException
block|{
try|try
init|(
name|MetaDataUpdate
name|md
init|=
name|metaDataUpdateFactory
operator|.
name|get
argument_list|()
operator|.
name|create
argument_list|(
name|allUsersName
argument_list|)
init|)
block|{
name|VersionedAccountPreferences
name|p
init|=
name|VersionedAccountPreferences
operator|.
name|forDefault
argument_list|()
decl_stmt|;
name|p
operator|.
name|load
argument_list|(
name|md
argument_list|)
expr_stmt|;
name|storeSection
argument_list|(
name|p
operator|.
name|getConfig
argument_list|()
argument_list|,
name|UserConfigSections
operator|.
name|GENERAL
argument_list|,
literal|null
argument_list|,
name|i
argument_list|,
name|GeneralPreferencesInfo
operator|.
name|defaults
argument_list|()
argument_list|)
expr_stmt|;
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
operator|.
name|SetPreferences
operator|.
name|storeMyMenus
argument_list|(
name|p
argument_list|,
name|i
operator|.
name|my
argument_list|)
expr_stmt|;
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
operator|.
name|SetPreferences
operator|.
name|storeUrlAliases
argument_list|(
name|p
argument_list|,
name|i
operator|.
name|urlAliases
argument_list|)
expr_stmt|;
name|p
operator|.
name|commit
argument_list|(
name|md
argument_list|)
expr_stmt|;
name|accountCache
operator|.
name|evictAllNoReindex
argument_list|()
expr_stmt|;
name|GeneralPreferencesInfo
name|r
init|=
name|loadSection
argument_list|(
name|p
operator|.
name|getConfig
argument_list|()
argument_list|,
name|UserConfigSections
operator|.
name|GENERAL
argument_list|,
literal|null
argument_list|,
operator|new
name|GeneralPreferencesInfo
argument_list|()
argument_list|,
name|GeneralPreferencesInfo
operator|.
name|defaults
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
name|loader
operator|.
name|loadMyMenusAndUrlAliases
argument_list|(
name|r
argument_list|,
name|p
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
DECL|method|hasSetFields (GeneralPreferencesInfo in)
specifier|private
specifier|static
name|boolean
name|hasSetFields
parameter_list|(
name|GeneralPreferencesInfo
name|in
parameter_list|)
block|{
try|try
block|{
for|for
control|(
name|Field
name|field
range|:
name|in
operator|.
name|getClass
argument_list|()
operator|.
name|getDeclaredFields
argument_list|()
control|)
block|{
if|if
condition|(
name|skipField
argument_list|(
name|field
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|field
operator|.
name|get
argument_list|(
name|in
argument_list|)
operator|!=
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Unable to verify input"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

