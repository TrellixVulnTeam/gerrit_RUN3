begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2019 The Android Open Source Project
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
name|data
operator|.
name|LabelFunction
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
name|LabelType
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
name|LabelValue
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
name|LabelDefinitionInfo
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
name|LabelDefinitionInput
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
name|RestCollectionCreateView
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
name|meta
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
name|ProjectPermission
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
name|LabelDefinitionJson
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
name|LabelResource
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
name|ProjectResource
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

begin_class
annotation|@
name|Singleton
DECL|class|CreateLabel
specifier|public
class|class
name|CreateLabel
implements|implements
name|RestCollectionCreateView
argument_list|<
name|ProjectResource
argument_list|,
name|LabelResource
argument_list|,
name|LabelDefinitionInput
argument_list|>
block|{
DECL|field|permissionBackend
specifier|private
specifier|final
name|PermissionBackend
name|permissionBackend
decl_stmt|;
DECL|field|updateFactory
specifier|private
specifier|final
name|MetaDataUpdate
operator|.
name|User
name|updateFactory
decl_stmt|;
DECL|field|projectConfigFactory
specifier|private
specifier|final
name|ProjectConfig
operator|.
name|Factory
name|projectConfigFactory
decl_stmt|;
DECL|field|projectCache
specifier|private
specifier|final
name|ProjectCache
name|projectCache
decl_stmt|;
annotation|@
name|Inject
DECL|method|CreateLabel ( PermissionBackend permissionBackend, MetaDataUpdate.User updateFactory, ProjectConfig.Factory projectConfigFactory, ProjectCache projectCache)
specifier|public
name|CreateLabel
parameter_list|(
name|PermissionBackend
name|permissionBackend
parameter_list|,
name|MetaDataUpdate
operator|.
name|User
name|updateFactory
parameter_list|,
name|ProjectConfig
operator|.
name|Factory
name|projectConfigFactory
parameter_list|,
name|ProjectCache
name|projectCache
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
name|updateFactory
operator|=
name|updateFactory
expr_stmt|;
name|this
operator|.
name|projectConfigFactory
operator|=
name|projectConfigFactory
expr_stmt|;
name|this
operator|.
name|projectCache
operator|=
name|projectCache
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply ( ProjectResource rsrc, IdString id, LabelDefinitionInput input)
specifier|public
name|Response
argument_list|<
name|LabelDefinitionInfo
argument_list|>
name|apply
parameter_list|(
name|ProjectResource
name|rsrc
parameter_list|,
name|IdString
name|id
parameter_list|,
name|LabelDefinitionInput
name|input
parameter_list|)
throws|throws
name|AuthException
throws|,
name|BadRequestException
throws|,
name|ResourceConflictException
throws|,
name|PermissionBackendException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|permissionBackend
operator|.
name|currentUser
argument_list|()
operator|.
name|project
argument_list|(
name|rsrc
operator|.
name|getNameKey
argument_list|()
argument_list|)
operator|.
name|check
argument_list|(
name|ProjectPermission
operator|.
name|WRITE_CONFIG
argument_list|)
expr_stmt|;
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
name|LabelDefinitionInput
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|input
operator|.
name|name
operator|!=
literal|null
operator|&&
operator|!
name|input
operator|.
name|name
operator|.
name|equals
argument_list|(
name|id
operator|.
name|get
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"name in input must match name in URL"
argument_list|)
throw|;
block|}
try|try
init|(
name|MetaDataUpdate
name|md
init|=
name|updateFactory
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
name|projectConfigFactory
operator|.
name|read
argument_list|(
name|md
argument_list|)
decl_stmt|;
if|if
condition|(
name|config
operator|.
name|getLabelSections
argument_list|()
operator|.
name|containsKey
argument_list|(
name|id
operator|.
name|get
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
literal|"label "
operator|+
name|id
operator|.
name|get
argument_list|()
operator|+
literal|" already exists"
argument_list|)
throw|;
block|}
if|if
condition|(
name|input
operator|.
name|values
operator|==
literal|null
operator|||
name|input
operator|.
name|values
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"values are required"
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|LabelValue
argument_list|>
name|values
init|=
name|LabelDefinitionInputParser
operator|.
name|parseValues
argument_list|(
name|input
operator|.
name|values
argument_list|)
decl_stmt|;
name|LabelType
name|labelType
decl_stmt|;
try|try
block|{
name|labelType
operator|=
operator|new
name|LabelType
argument_list|(
name|id
operator|.
name|get
argument_list|()
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"invalid name: "
operator|+
name|id
operator|.
name|get
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|input
operator|.
name|function
operator|!=
literal|null
operator|&&
operator|!
name|input
operator|.
name|function
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|labelType
operator|.
name|setFunction
argument_list|(
name|LabelDefinitionInputParser
operator|.
name|parseFunction
argument_list|(
name|input
operator|.
name|function
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|labelType
operator|.
name|setFunction
argument_list|(
name|LabelFunction
operator|.
name|MAX_WITH_BLOCK
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|input
operator|.
name|defaultValue
operator|!=
literal|null
condition|)
block|{
name|labelType
operator|.
name|setDefaultValue
argument_list|(
name|LabelDefinitionInputParser
operator|.
name|parseDefaultValue
argument_list|(
name|labelType
argument_list|,
name|input
operator|.
name|defaultValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|input
operator|.
name|branches
operator|!=
literal|null
condition|)
block|{
name|labelType
operator|.
name|setRefPatterns
argument_list|(
name|LabelDefinitionInputParser
operator|.
name|parseBranches
argument_list|(
name|input
operator|.
name|branches
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|input
operator|.
name|canOverride
operator|!=
literal|null
condition|)
block|{
name|labelType
operator|.
name|setCanOverride
argument_list|(
name|input
operator|.
name|canOverride
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|input
operator|.
name|copyAnyScore
operator|!=
literal|null
condition|)
block|{
name|labelType
operator|.
name|setCopyAnyScore
argument_list|(
name|input
operator|.
name|copyAnyScore
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|input
operator|.
name|copyMinScore
operator|!=
literal|null
condition|)
block|{
name|labelType
operator|.
name|setCopyMinScore
argument_list|(
name|input
operator|.
name|copyMinScore
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|input
operator|.
name|copyMaxScore
operator|!=
literal|null
condition|)
block|{
name|labelType
operator|.
name|setCopyMaxScore
argument_list|(
name|input
operator|.
name|copyMaxScore
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|input
operator|.
name|copyAllScoresIfNoChange
operator|!=
literal|null
condition|)
block|{
name|labelType
operator|.
name|setCopyAllScoresIfNoChange
argument_list|(
name|input
operator|.
name|copyAllScoresIfNoChange
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|input
operator|.
name|copyAllScoresIfNoCodeChange
operator|!=
literal|null
condition|)
block|{
name|labelType
operator|.
name|setCopyAllScoresIfNoCodeChange
argument_list|(
name|input
operator|.
name|copyAllScoresIfNoCodeChange
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|input
operator|.
name|copyAllScoresOnTrivialRebase
operator|!=
literal|null
condition|)
block|{
name|labelType
operator|.
name|setCopyAllScoresOnTrivialRebase
argument_list|(
name|input
operator|.
name|copyAllScoresOnTrivialRebase
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|input
operator|.
name|copyAllScoresOnMergeFirstParentUpdate
operator|!=
literal|null
condition|)
block|{
name|labelType
operator|.
name|setCopyAllScoresOnMergeFirstParentUpdate
argument_list|(
name|input
operator|.
name|copyAllScoresOnMergeFirstParentUpdate
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|input
operator|.
name|allowPostSubmit
operator|!=
literal|null
condition|)
block|{
name|labelType
operator|.
name|setAllowPostSubmit
argument_list|(
name|input
operator|.
name|allowPostSubmit
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|input
operator|.
name|ignoreSelfApproval
operator|!=
literal|null
condition|)
block|{
name|labelType
operator|.
name|setIgnoreSelfApproval
argument_list|(
name|input
operator|.
name|ignoreSelfApproval
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|input
operator|.
name|commitMessage
operator|!=
literal|null
condition|)
block|{
name|md
operator|.
name|setMessage
argument_list|(
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|input
operator|.
name|commitMessage
operator|.
name|trim
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|md
operator|.
name|setMessage
argument_list|(
literal|"Update label"
argument_list|)
expr_stmt|;
block|}
name|config
operator|.
name|getLabelSections
argument_list|()
operator|.
name|put
argument_list|(
name|labelType
operator|.
name|getName
argument_list|()
argument_list|,
name|labelType
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
name|rsrc
operator|.
name|getProjectState
argument_list|()
operator|.
name|getProject
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|Response
operator|.
name|created
argument_list|(
name|LabelDefinitionJson
operator|.
name|format
argument_list|(
name|rsrc
operator|.
name|getNameKey
argument_list|()
argument_list|,
name|labelType
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

