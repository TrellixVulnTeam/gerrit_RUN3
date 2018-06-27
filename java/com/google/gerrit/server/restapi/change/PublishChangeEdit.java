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
name|PublishChangeEditInput
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
name|restapi
operator|.
name|AcceptsPost
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
name|ChildCollection
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
name|NotImplementedException
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
name|RestView
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
name|ChangeEditResource
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
name|change
operator|.
name|NotifyUtil
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
name|edit
operator|.
name|ChangeEdit
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
name|edit
operator|.
name|ChangeEditUtil
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
name|Optional
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
DECL|class|PublishChangeEdit
specifier|public
class|class
name|PublishChangeEdit
implements|implements
name|ChildCollection
argument_list|<
name|ChangeResource
argument_list|,
name|ChangeEditResource
operator|.
name|Publish
argument_list|>
implements|,
name|AcceptsPost
argument_list|<
name|ChangeResource
argument_list|>
block|{
DECL|field|publish
specifier|private
specifier|final
name|Publish
name|publish
decl_stmt|;
annotation|@
name|Inject
DECL|method|PublishChangeEdit (Publish publish)
name|PublishChangeEdit
parameter_list|(
name|Publish
name|publish
parameter_list|)
block|{
name|this
operator|.
name|publish
operator|=
name|publish
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|views ()
specifier|public
name|DynamicMap
argument_list|<
name|RestView
argument_list|<
name|ChangeEditResource
operator|.
name|Publish
argument_list|>
argument_list|>
name|views
parameter_list|()
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|list ()
specifier|public
name|RestView
argument_list|<
name|ChangeResource
argument_list|>
name|list
parameter_list|()
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|parse (ChangeResource parent, IdString id)
specifier|public
name|ChangeEditResource
operator|.
name|Publish
name|parse
parameter_list|(
name|ChangeResource
name|parent
parameter_list|,
name|IdString
name|id
parameter_list|)
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|post (ChangeResource parent)
specifier|public
name|Publish
name|post
parameter_list|(
name|ChangeResource
name|parent
parameter_list|)
throws|throws
name|RestApiException
block|{
return|return
name|publish
return|;
block|}
annotation|@
name|Singleton
DECL|class|Publish
specifier|public
specifier|static
class|class
name|Publish
extends|extends
name|RetryingRestModifyView
argument_list|<
name|ChangeResource
argument_list|,
name|PublishChangeEditInput
argument_list|,
name|Response
argument_list|<
name|?
argument_list|>
argument_list|>
block|{
DECL|field|editUtil
specifier|private
specifier|final
name|ChangeEditUtil
name|editUtil
decl_stmt|;
DECL|field|notifyUtil
specifier|private
specifier|final
name|NotifyUtil
name|notifyUtil
decl_stmt|;
DECL|field|contributorAgreementsChecker
specifier|private
specifier|final
name|ContributorAgreementsChecker
name|contributorAgreementsChecker
decl_stmt|;
annotation|@
name|Inject
DECL|method|Publish ( RetryHelper retryHelper, ChangeEditUtil editUtil, NotifyUtil notifyUtil, ContributorAgreementsChecker contributorAgreementsChecker)
name|Publish
parameter_list|(
name|RetryHelper
name|retryHelper
parameter_list|,
name|ChangeEditUtil
name|editUtil
parameter_list|,
name|NotifyUtil
name|notifyUtil
parameter_list|,
name|ContributorAgreementsChecker
name|contributorAgreementsChecker
parameter_list|)
block|{
name|super
argument_list|(
name|retryHelper
argument_list|)
expr_stmt|;
name|this
operator|.
name|editUtil
operator|=
name|editUtil
expr_stmt|;
name|this
operator|.
name|notifyUtil
operator|=
name|notifyUtil
expr_stmt|;
name|this
operator|.
name|contributorAgreementsChecker
operator|=
name|contributorAgreementsChecker
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|applyImpl ( BatchUpdate.Factory updateFactory, ChangeResource rsrc, PublishChangeEditInput in)
specifier|protected
name|Response
argument_list|<
name|?
argument_list|>
name|applyImpl
parameter_list|(
name|BatchUpdate
operator|.
name|Factory
name|updateFactory
parameter_list|,
name|ChangeResource
name|rsrc
parameter_list|,
name|PublishChangeEditInput
name|in
parameter_list|)
throws|throws
name|IOException
throws|,
name|OrmException
throws|,
name|RestApiException
throws|,
name|UpdateException
throws|,
name|ConfigInvalidException
throws|,
name|NoSuchProjectException
block|{
name|contributorAgreementsChecker
operator|.
name|check
argument_list|(
name|rsrc
operator|.
name|getProject
argument_list|()
argument_list|,
name|rsrc
operator|.
name|getUser
argument_list|()
argument_list|)
expr_stmt|;
name|Optional
argument_list|<
name|ChangeEdit
argument_list|>
name|edit
init|=
name|editUtil
operator|.
name|byChange
argument_list|(
name|rsrc
operator|.
name|getNotes
argument_list|()
argument_list|,
name|rsrc
operator|.
name|getUser
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|edit
operator|.
name|isPresent
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
literal|"no edit exists for change %s"
argument_list|,
name|rsrc
operator|.
name|getChange
argument_list|()
operator|.
name|getChangeId
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|in
operator|==
literal|null
condition|)
block|{
name|in
operator|=
operator|new
name|PublishChangeEditInput
argument_list|()
expr_stmt|;
block|}
name|editUtil
operator|.
name|publish
argument_list|(
name|updateFactory
argument_list|,
name|rsrc
operator|.
name|getNotes
argument_list|()
argument_list|,
name|rsrc
operator|.
name|getUser
argument_list|()
argument_list|,
name|edit
operator|.
name|get
argument_list|()
argument_list|,
name|in
operator|.
name|notify
argument_list|,
name|notifyUtil
operator|.
name|resolveAccounts
argument_list|(
name|in
operator|.
name|notifyDetails
argument_list|)
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
block|}
end_class

end_unit

