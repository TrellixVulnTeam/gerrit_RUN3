begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2011 The Android Open Source Project
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
DECL|package|com.google.gerrit.httpd.rpc.project
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|httpd
operator|.
name|rpc
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
name|data
operator|.
name|Permission
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
name|PermissionRule
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
name|ProjectAccess
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
name|RefConfigSection
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
name|NoSuchGroupException
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
name|httpd
operator|.
name|rpc
operator|.
name|Handler
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
name|AccountGroup
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
name|account
operator|.
name|GroupBackend
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
name|GroupControl
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
name|AllProjectsName
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
name|project
operator|.
name|ProjectControl
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
name|assistedinject
operator|.
name|Assisted
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
DECL|class|ProjectAccessFactory
class|class
name|ProjectAccessFactory
extends|extends
name|Handler
argument_list|<
name|ProjectAccess
argument_list|>
block|{
DECL|interface|Factory
interface|interface
name|Factory
block|{
DECL|method|create (@ssisted Project.NameKey name)
name|ProjectAccessFactory
name|create
parameter_list|(
annotation|@
name|Assisted
name|Project
operator|.
name|NameKey
name|name
parameter_list|)
function_decl|;
block|}
DECL|field|groupBackend
specifier|private
specifier|final
name|GroupBackend
name|groupBackend
decl_stmt|;
DECL|field|projectCache
specifier|private
specifier|final
name|ProjectCache
name|projectCache
decl_stmt|;
DECL|field|projectControlFactory
specifier|private
specifier|final
name|ProjectControl
operator|.
name|Factory
name|projectControlFactory
decl_stmt|;
DECL|field|groupControlFactory
specifier|private
specifier|final
name|GroupControl
operator|.
name|Factory
name|groupControlFactory
decl_stmt|;
DECL|field|metaDataUpdateFactory
specifier|private
specifier|final
name|MetaDataUpdate
operator|.
name|Server
name|metaDataUpdateFactory
decl_stmt|;
DECL|field|allProjectsName
specifier|private
specifier|final
name|AllProjectsName
name|allProjectsName
decl_stmt|;
DECL|field|projectName
specifier|private
specifier|final
name|Project
operator|.
name|NameKey
name|projectName
decl_stmt|;
DECL|field|pc
specifier|private
name|ProjectControl
name|pc
decl_stmt|;
annotation|@
name|Inject
DECL|method|ProjectAccessFactory (final GroupBackend groupBackend, final ProjectCache projectCache, final ProjectControl.Factory projectControlFactory, final GroupControl.Factory groupControlFactory, final MetaDataUpdate.Server metaDataUpdateFactory, final AllProjectsName allProjectsName, @Assisted final Project.NameKey name)
name|ProjectAccessFactory
parameter_list|(
specifier|final
name|GroupBackend
name|groupBackend
parameter_list|,
specifier|final
name|ProjectCache
name|projectCache
parameter_list|,
specifier|final
name|ProjectControl
operator|.
name|Factory
name|projectControlFactory
parameter_list|,
specifier|final
name|GroupControl
operator|.
name|Factory
name|groupControlFactory
parameter_list|,
specifier|final
name|MetaDataUpdate
operator|.
name|Server
name|metaDataUpdateFactory
parameter_list|,
specifier|final
name|AllProjectsName
name|allProjectsName
parameter_list|,
annotation|@
name|Assisted
specifier|final
name|Project
operator|.
name|NameKey
name|name
parameter_list|)
block|{
name|this
operator|.
name|groupBackend
operator|=
name|groupBackend
expr_stmt|;
name|this
operator|.
name|projectCache
operator|=
name|projectCache
expr_stmt|;
name|this
operator|.
name|projectControlFactory
operator|=
name|projectControlFactory
expr_stmt|;
name|this
operator|.
name|groupControlFactory
operator|=
name|groupControlFactory
expr_stmt|;
name|this
operator|.
name|metaDataUpdateFactory
operator|=
name|metaDataUpdateFactory
expr_stmt|;
name|this
operator|.
name|allProjectsName
operator|=
name|allProjectsName
expr_stmt|;
name|this
operator|.
name|projectName
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|ProjectAccess
name|call
parameter_list|()
throws|throws
name|NoSuchProjectException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|pc
operator|=
name|open
argument_list|()
expr_stmt|;
comment|// Load the current configuration from the repository, ensuring its the most
comment|// recent version available. If it differs from what was in the project
comment|// state, force a cache flush now.
comment|//
name|ProjectConfig
name|config
decl_stmt|;
name|MetaDataUpdate
name|md
init|=
name|metaDataUpdateFactory
operator|.
name|create
argument_list|(
name|projectName
argument_list|)
decl_stmt|;
try|try
block|{
name|config
operator|=
name|ProjectConfig
operator|.
name|read
argument_list|(
name|md
argument_list|)
expr_stmt|;
if|if
condition|(
name|config
operator|.
name|updateGroupNames
argument_list|(
name|groupBackend
argument_list|)
condition|)
block|{
name|md
operator|.
name|setMessage
argument_list|(
literal|"Update group names\n"
argument_list|)
expr_stmt|;
name|config
operator|.
name|commit
argument_list|(
name|md
argument_list|)
expr_stmt|;
name|projectCache
operator|.
name|evict
argument_list|(
name|config
operator|.
name|getProject
argument_list|()
argument_list|)
expr_stmt|;
name|pc
operator|=
name|open
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|config
operator|.
name|getRevision
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|config
operator|.
name|getRevision
argument_list|()
operator|.
name|equals
argument_list|(
name|pc
operator|.
name|getProjectState
argument_list|()
operator|.
name|getConfig
argument_list|()
operator|.
name|getRevision
argument_list|()
argument_list|)
condition|)
block|{
name|projectCache
operator|.
name|evict
argument_list|(
name|config
operator|.
name|getProject
argument_list|()
argument_list|)
expr_stmt|;
name|pc
operator|=
name|open
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|md
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|final
name|RefControl
name|metaConfigControl
init|=
name|pc
operator|.
name|controlForRef
argument_list|(
name|GitRepositoryManager
operator|.
name|REF_CONFIG
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|AccessSection
argument_list|>
name|local
init|=
operator|new
name|ArrayList
argument_list|<
name|AccessSection
argument_list|>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|ownerOf
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|,
name|Boolean
argument_list|>
name|visibleGroups
init|=
operator|new
name|HashMap
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|,
name|Boolean
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|AccessSection
name|section
range|:
name|config
operator|.
name|getAccessSections
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|section
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|AccessSection
operator|.
name|GLOBAL_CAPABILITIES
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
if|if
condition|(
name|pc
operator|.
name|isOwner
argument_list|()
condition|)
block|{
name|local
operator|.
name|add
argument_list|(
name|section
argument_list|)
expr_stmt|;
name|ownerOf
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|RefConfigSection
operator|.
name|isValid
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|RefControl
name|rc
init|=
name|pc
operator|.
name|controlForRef
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|rc
operator|.
name|isOwner
argument_list|()
operator|||
name|metaConfigControl
operator|.
name|isVisible
argument_list|()
condition|)
block|{
name|local
operator|.
name|add
argument_list|(
name|section
argument_list|)
expr_stmt|;
name|ownerOf
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|rc
operator|.
name|isVisible
argument_list|()
condition|)
block|{
comment|// Filter the section to only add rules describing groups that
comment|// are visible to the current-user. This includes any group the
comment|// user is a member of, as well as groups they own or that
comment|// are visible to all users.
name|AccessSection
name|dst
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Permission
name|srcPerm
range|:
name|section
operator|.
name|getPermissions
argument_list|()
control|)
block|{
name|Permission
name|dstPerm
init|=
literal|null
decl_stmt|;
for|for
control|(
name|PermissionRule
name|srcRule
range|:
name|srcPerm
operator|.
name|getRules
argument_list|()
control|)
block|{
name|AccountGroup
operator|.
name|UUID
name|group
init|=
name|srcRule
operator|.
name|getGroup
argument_list|()
operator|.
name|getUUID
argument_list|()
decl_stmt|;
if|if
condition|(
name|group
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|Boolean
name|canSeeGroup
init|=
name|visibleGroups
operator|.
name|get
argument_list|(
name|group
argument_list|)
decl_stmt|;
if|if
condition|(
name|canSeeGroup
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|canSeeGroup
operator|=
name|groupControlFactory
operator|.
name|controlFor
argument_list|(
name|group
argument_list|)
operator|.
name|isVisible
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchGroupException
name|e
parameter_list|)
block|{
name|canSeeGroup
operator|=
name|Boolean
operator|.
name|FALSE
expr_stmt|;
block|}
name|visibleGroups
operator|.
name|put
argument_list|(
name|group
argument_list|,
name|canSeeGroup
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|canSeeGroup
condition|)
block|{
if|if
condition|(
name|dstPerm
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|dst
operator|==
literal|null
condition|)
block|{
name|dst
operator|=
operator|new
name|AccessSection
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|local
operator|.
name|add
argument_list|(
name|dst
argument_list|)
expr_stmt|;
block|}
name|dstPerm
operator|=
name|dst
operator|.
name|getPermission
argument_list|(
name|srcPerm
operator|.
name|getName
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|dstPerm
operator|.
name|add
argument_list|(
name|srcRule
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
if|if
condition|(
name|ownerOf
operator|.
name|isEmpty
argument_list|()
operator|&&
name|pc
operator|.
name|isOwnerAnyRef
argument_list|()
condition|)
block|{
comment|// Special case: If the section list is empty, this project has no current
comment|// access control information. Rely on what ProjectControl determines
comment|// is ownership, which probably means falling back to site administrators.
name|ownerOf
operator|.
name|add
argument_list|(
name|AccessSection
operator|.
name|ALL
argument_list|)
expr_stmt|;
block|}
specifier|final
name|ProjectAccess
name|detail
init|=
operator|new
name|ProjectAccess
argument_list|()
decl_stmt|;
name|detail
operator|.
name|setProjectName
argument_list|(
name|projectName
argument_list|)
expr_stmt|;
if|if
condition|(
name|config
operator|.
name|getRevision
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|detail
operator|.
name|setRevision
argument_list|(
name|config
operator|.
name|getRevision
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|detail
operator|.
name|setInheritsFrom
argument_list|(
name|config
operator|.
name|getProject
argument_list|()
operator|.
name|getParent
argument_list|(
name|allProjectsName
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|projectName
operator|.
name|equals
argument_list|(
name|allProjectsName
argument_list|)
condition|)
block|{
if|if
condition|(
name|pc
operator|.
name|isOwner
argument_list|()
condition|)
block|{
name|ownerOf
operator|.
name|add
argument_list|(
name|AccessSection
operator|.
name|GLOBAL_CAPABILITIES
argument_list|)
expr_stmt|;
block|}
block|}
name|detail
operator|.
name|setLocal
argument_list|(
name|local
argument_list|)
expr_stmt|;
name|detail
operator|.
name|setOwnerOf
argument_list|(
name|ownerOf
argument_list|)
expr_stmt|;
name|detail
operator|.
name|setCanUpload
argument_list|(
name|pc
operator|.
name|isOwner
argument_list|()
operator|||
operator|(
name|metaConfigControl
operator|.
name|isVisible
argument_list|()
operator|&&
name|metaConfigControl
operator|.
name|canUpload
argument_list|()
operator|)
argument_list|)
expr_stmt|;
name|detail
operator|.
name|setConfigVisible
argument_list|(
name|pc
operator|.
name|isOwner
argument_list|()
operator|||
name|metaConfigControl
operator|.
name|isVisible
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|detail
return|;
block|}
DECL|method|open ()
specifier|private
name|ProjectControl
name|open
parameter_list|()
throws|throws
name|NoSuchProjectException
block|{
return|return
name|projectControlFactory
operator|.
name|validateFor
argument_list|(
comment|//
name|projectName
argument_list|,
comment|//
name|ProjectControl
operator|.
name|OWNER
operator||
name|ProjectControl
operator|.
name|VISIBLE
argument_list|)
return|;
block|}
block|}
end_class

end_unit

