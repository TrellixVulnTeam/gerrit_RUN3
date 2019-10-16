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
DECL|package|com.google.gerrit.server.restapi.change
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
name|change
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
name|extensions
operator|.
name|conditions
operator|.
name|BooleanCondition
operator|.
name|and
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
name|permissions
operator|.
name|RefPermission
operator|.
name|CREATE_CHANGE
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
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
name|flogger
operator|.
name|FluentLogger
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
name|extensions
operator|.
name|api
operator|.
name|changes
operator|.
name|RevertInput
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
name|common
operator|.
name|RevertSubmissionInfo
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
name|webui
operator|.
name|UiAction
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
name|ChangeUtil
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
name|change
operator|.
name|ChangeResource
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
name|ChangePermission
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
name|project
operator|.
name|ContributorAgreementsChecker
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
name|ArrayList
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
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|RandomStringUtils
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|RevertSubmission
specifier|public
class|class
name|RevertSubmission
extends|extends
name|RetryingRestModifyView
argument_list|<
name|ChangeResource
argument_list|,
name|RevertInput
argument_list|,
name|RevertSubmissionInfo
argument_list|>
implements|implements
name|UiAction
argument_list|<
name|ChangeResource
argument_list|>
block|{
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|FluentLogger
name|logger
init|=
name|FluentLogger
operator|.
name|forEnclosingClass
argument_list|()
decl_stmt|;
DECL|field|revert
specifier|private
specifier|final
name|Revert
name|revert
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
DECL|field|changeResourceFactory
specifier|private
specifier|final
name|ChangeResource
operator|.
name|Factory
name|changeResourceFactory
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
DECL|field|permissionBackend
specifier|private
specifier|final
name|PermissionBackend
name|permissionBackend
decl_stmt|;
DECL|field|projectCache
specifier|private
specifier|final
name|ProjectCache
name|projectCache
decl_stmt|;
DECL|field|psUtil
specifier|private
specifier|final
name|PatchSetUtil
name|psUtil
decl_stmt|;
DECL|field|contributorAgreements
specifier|private
specifier|final
name|ContributorAgreementsChecker
name|contributorAgreements
decl_stmt|;
annotation|@
name|Inject
DECL|method|RevertSubmission ( RetryHelper retryHelper, Revert revert, Provider<InternalChangeQuery> queryProvider, ChangeResource.Factory changeResourceFactory, Provider<CurrentUser> user, PermissionBackend permissionBackend, ProjectCache projectCache, PatchSetUtil psUtil, ContributorAgreementsChecker contributorAgreements)
name|RevertSubmission
parameter_list|(
name|RetryHelper
name|retryHelper
parameter_list|,
name|Revert
name|revert
parameter_list|,
name|Provider
argument_list|<
name|InternalChangeQuery
argument_list|>
name|queryProvider
parameter_list|,
name|ChangeResource
operator|.
name|Factory
name|changeResourceFactory
parameter_list|,
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|user
parameter_list|,
name|PermissionBackend
name|permissionBackend
parameter_list|,
name|ProjectCache
name|projectCache
parameter_list|,
name|PatchSetUtil
name|psUtil
parameter_list|,
name|ContributorAgreementsChecker
name|contributorAgreements
parameter_list|)
block|{
name|super
argument_list|(
name|retryHelper
argument_list|)
expr_stmt|;
name|this
operator|.
name|revert
operator|=
name|revert
expr_stmt|;
name|this
operator|.
name|queryProvider
operator|=
name|queryProvider
expr_stmt|;
name|this
operator|.
name|changeResourceFactory
operator|=
name|changeResourceFactory
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|permissionBackend
operator|=
name|permissionBackend
expr_stmt|;
name|this
operator|.
name|projectCache
operator|=
name|projectCache
expr_stmt|;
name|this
operator|.
name|psUtil
operator|=
name|psUtil
expr_stmt|;
name|this
operator|.
name|contributorAgreements
operator|=
name|contributorAgreements
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|applyImpl ( BatchUpdate.Factory updateFactory, ChangeResource changeResource, RevertInput input)
specifier|public
name|Response
argument_list|<
name|RevertSubmissionInfo
argument_list|>
name|applyImpl
parameter_list|(
name|BatchUpdate
operator|.
name|Factory
name|updateFactory
parameter_list|,
name|ChangeResource
name|changeResource
parameter_list|,
name|RevertInput
name|input
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|changeResource
operator|.
name|getChange
argument_list|()
operator|.
name|isMerged
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"change is %s."
argument_list|,
name|ChangeUtil
operator|.
name|status
argument_list|(
name|changeResource
operator|.
name|getChange
argument_list|()
argument_list|)
argument_list|)
argument_list|)
throw|;
block|}
name|String
name|submissionId
init|=
name|requireNonNull
argument_list|(
name|changeResource
operator|.
name|getChange
argument_list|()
operator|.
name|getSubmissionId
argument_list|()
argument_list|,
name|String
operator|.
name|format
argument_list|(
literal|"merged change %s has no submission ID"
argument_list|,
name|changeResource
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ChangeData
argument_list|>
name|changeDatas
init|=
name|queryProvider
operator|.
name|get
argument_list|()
operator|.
name|bySubmissionId
argument_list|(
name|submissionId
argument_list|)
decl_stmt|;
for|for
control|(
name|ChangeData
name|changeData
range|:
name|changeDatas
control|)
block|{
name|Change
name|change
init|=
name|changeData
operator|.
name|change
argument_list|()
decl_stmt|;
comment|// Might do the permission tests multiple times, but these are necessary to ensure that the
comment|// user has permissions to revert all changes. If they lack any permission, no revert will be
comment|// done.
name|contributorAgreements
operator|.
name|check
argument_list|(
name|change
operator|.
name|getProject
argument_list|()
argument_list|,
name|changeResource
operator|.
name|getUser
argument_list|()
argument_list|)
expr_stmt|;
name|permissionBackend
operator|.
name|currentUser
argument_list|()
operator|.
name|ref
argument_list|(
name|change
operator|.
name|getDest
argument_list|()
argument_list|)
operator|.
name|check
argument_list|(
name|CREATE_CHANGE
argument_list|)
expr_stmt|;
name|permissionBackend
operator|.
name|currentUser
argument_list|()
operator|.
name|change
argument_list|(
name|changeData
argument_list|)
operator|.
name|check
argument_list|(
name|ChangePermission
operator|.
name|READ
argument_list|)
expr_stmt|;
name|projectCache
operator|.
name|checkedGet
argument_list|(
name|change
operator|.
name|getProject
argument_list|()
argument_list|)
operator|.
name|checkStatePermitsWrite
argument_list|()
expr_stmt|;
name|requireNonNull
argument_list|(
name|psUtil
operator|.
name|get
argument_list|(
name|changeData
operator|.
name|notes
argument_list|()
argument_list|,
name|change
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
argument_list|,
name|String
operator|.
name|format
argument_list|(
literal|"current patch set %s of change %s not found"
argument_list|,
name|change
operator|.
name|currentPatchSetId
argument_list|()
argument_list|,
name|change
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|Response
operator|.
name|ok
argument_list|(
name|revertSubmission
argument_list|(
name|changeDatas
argument_list|,
name|input
argument_list|,
name|submissionId
argument_list|)
argument_list|)
return|;
block|}
DECL|method|revertSubmission ( List<ChangeData> changeDatas, RevertInput input, String submissionId)
specifier|private
name|RevertSubmissionInfo
name|revertSubmission
parameter_list|(
name|List
argument_list|<
name|ChangeData
argument_list|>
name|changeDatas
parameter_list|,
name|RevertInput
name|input
parameter_list|,
name|String
name|submissionId
parameter_list|)
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|ChangeInfo
argument_list|>
name|results
decl_stmt|;
name|results
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
if|if
condition|(
name|input
operator|.
name|topic
operator|==
literal|null
condition|)
block|{
name|input
operator|.
name|topic
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"revert-%s-%s"
argument_list|,
name|submissionId
argument_list|,
name|RandomStringUtils
operator|.
name|randomAlphabetic
argument_list|(
literal|10
argument_list|)
operator|.
name|toUpperCase
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|ChangeData
name|changeData
range|:
name|changeDatas
control|)
block|{
name|ChangeResource
name|change
init|=
name|changeResourceFactory
operator|.
name|create
argument_list|(
name|changeData
operator|.
name|notes
argument_list|()
argument_list|,
name|user
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
comment|// Reverts are done with retrying by using RetryingRestModifyView.
name|results
operator|.
name|add
argument_list|(
name|revert
operator|.
name|apply
argument_list|(
name|change
argument_list|,
name|input
argument_list|)
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|RevertSubmissionInfo
name|revertSubmissionInfo
init|=
operator|new
name|RevertSubmissionInfo
argument_list|()
decl_stmt|;
name|revertSubmissionInfo
operator|.
name|revertChanges
operator|=
name|results
expr_stmt|;
return|return
name|revertSubmissionInfo
return|;
block|}
annotation|@
name|Override
DECL|method|getDescription (ChangeResource rsrc)
specifier|public
name|Description
name|getDescription
parameter_list|(
name|ChangeResource
name|rsrc
parameter_list|)
block|{
name|Change
name|change
init|=
name|rsrc
operator|.
name|getChange
argument_list|()
decl_stmt|;
name|boolean
name|projectStatePermitsWrite
init|=
literal|false
decl_stmt|;
try|try
block|{
name|projectStatePermitsWrite
operator|=
name|projectCache
operator|.
name|checkedGet
argument_list|(
name|rsrc
operator|.
name|getProject
argument_list|()
argument_list|)
operator|.
name|statePermitsWrite
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|atSevere
argument_list|()
operator|.
name|withCause
argument_list|(
name|e
argument_list|)
operator|.
name|log
argument_list|(
literal|"Failed to check if project state permits write: %s"
argument_list|,
name|rsrc
operator|.
name|getProject
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|UiAction
operator|.
name|Description
argument_list|()
operator|.
name|setLabel
argument_list|(
literal|"Revert this change and all changes that have been submitted together with this change"
argument_list|)
operator|.
name|setTitle
argument_list|(
literal|"Revert submission"
argument_list|)
operator|.
name|setVisible
argument_list|(
name|and
argument_list|(
name|projectStatePermitsWrite
argument_list|,
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
name|ref
argument_list|(
name|change
operator|.
name|getDest
argument_list|()
argument_list|)
operator|.
name|testCond
argument_list|(
name|CREATE_CHANGE
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

