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
name|gerrit
operator|.
name|common
operator|.
name|Nullable
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
name|PageLinks
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
name|ContributorAgreement
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
name|PermissionRule
operator|.
name|Action
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
name|metrics
operator|.
name|Counter0
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
name|metrics
operator|.
name|Description
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
name|metrics
operator|.
name|MetricMaker
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
name|AccountGroup
operator|.
name|UUID
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
name|BooleanProjectConfig
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
name|config
operator|.
name|CanonicalWebUrl
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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

begin_class
annotation|@
name|Singleton
DECL|class|ContributorAgreementsChecker
specifier|public
class|class
name|ContributorAgreementsChecker
block|{
DECL|field|canonicalWebUrl
specifier|private
specifier|final
name|String
name|canonicalWebUrl
decl_stmt|;
DECL|field|projectCache
specifier|private
specifier|final
name|ProjectCache
name|projectCache
decl_stmt|;
DECL|field|metrics
specifier|private
specifier|final
name|Metrics
name|metrics
decl_stmt|;
annotation|@
name|Singleton
DECL|class|Metrics
specifier|protected
specifier|static
class|class
name|Metrics
block|{
DECL|field|claCheckCount
specifier|final
name|Counter0
name|claCheckCount
decl_stmt|;
annotation|@
name|Inject
DECL|method|Metrics (MetricMaker metricMaker)
name|Metrics
parameter_list|(
name|MetricMaker
name|metricMaker
parameter_list|)
block|{
name|claCheckCount
operator|=
name|metricMaker
operator|.
name|newCounter
argument_list|(
literal|"license/cla_check_count"
argument_list|,
operator|new
name|Description
argument_list|(
literal|"Total number of CLA check requests"
argument_list|)
operator|.
name|setRate
argument_list|()
operator|.
name|setUnit
argument_list|(
literal|"requests"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Inject
DECL|method|ContributorAgreementsChecker ( @anonicalWebUrl @ullable String canonicalWebUrl, ProjectCache projectCache, Metrics metrics)
name|ContributorAgreementsChecker
parameter_list|(
annotation|@
name|CanonicalWebUrl
annotation|@
name|Nullable
name|String
name|canonicalWebUrl
parameter_list|,
name|ProjectCache
name|projectCache
parameter_list|,
name|Metrics
name|metrics
parameter_list|)
block|{
name|this
operator|.
name|canonicalWebUrl
operator|=
name|canonicalWebUrl
expr_stmt|;
name|this
operator|.
name|projectCache
operator|=
name|projectCache
expr_stmt|;
name|this
operator|.
name|metrics
operator|=
name|metrics
expr_stmt|;
block|}
comment|/**    * Checks if the user has signed a contributor agreement for the project.    *    * @throws AuthException if the user has not signed a contributor agreement for the project    * @throws IOException if project states could not be loaded    */
DECL|method|check (Project.NameKey project, CurrentUser user)
specifier|public
name|void
name|check
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
name|CurrentUser
name|user
parameter_list|)
throws|throws
name|IOException
throws|,
name|AuthException
block|{
name|metrics
operator|.
name|claCheckCount
operator|.
name|increment
argument_list|()
expr_stmt|;
name|ProjectState
name|projectState
init|=
name|projectCache
operator|.
name|checkedGet
argument_list|(
name|project
argument_list|)
decl_stmt|;
if|if
condition|(
name|projectState
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Can't load All-Projects"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|projectState
operator|.
name|is
argument_list|(
name|BooleanProjectConfig
operator|.
name|USE_CONTRIBUTOR_AGREEMENTS
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
operator|!
name|user
operator|.
name|isIdentifiedUser
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AuthException
argument_list|(
literal|"Must be logged in to verify Contributor Agreement"
argument_list|)
throw|;
block|}
name|IdentifiedUser
name|iUser
init|=
name|user
operator|.
name|asIdentifiedUser
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|ContributorAgreement
argument_list|>
name|contributorAgreements
init|=
name|projectCache
operator|.
name|getAllProjects
argument_list|()
operator|.
name|getConfig
argument_list|()
operator|.
name|getContributorAgreements
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|UUID
argument_list|>
name|okGroupIds
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ContributorAgreement
name|ca
range|:
name|contributorAgreements
control|)
block|{
name|List
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|groupIds
decl_stmt|;
name|groupIds
operator|=
name|okGroupIds
expr_stmt|;
for|for
control|(
name|PermissionRule
name|rule
range|:
name|ca
operator|.
name|getAccepted
argument_list|()
control|)
block|{
if|if
condition|(
operator|(
name|rule
operator|.
name|getAction
argument_list|()
operator|==
name|Action
operator|.
name|ALLOW
operator|)
operator|&&
operator|(
name|rule
operator|.
name|getGroup
argument_list|()
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|rule
operator|.
name|getGroup
argument_list|()
operator|.
name|getUUID
argument_list|()
operator|!=
literal|null
operator|)
condition|)
block|{
name|groupIds
operator|.
name|add
argument_list|(
operator|new
name|AccountGroup
operator|.
name|UUID
argument_list|(
name|rule
operator|.
name|getGroup
argument_list|()
operator|.
name|getUUID
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|iUser
operator|.
name|getEffectiveGroups
argument_list|()
operator|.
name|containsAnyOf
argument_list|(
name|okGroupIds
argument_list|)
condition|)
block|{
specifier|final
name|StringBuilder
name|msg
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|msg
operator|.
name|append
argument_list|(
literal|"A Contributor Agreement must be completed before uploading"
argument_list|)
expr_stmt|;
if|if
condition|(
name|canonicalWebUrl
operator|!=
literal|null
condition|)
block|{
name|msg
operator|.
name|append
argument_list|(
literal|":\n\n  "
argument_list|)
expr_stmt|;
name|msg
operator|.
name|append
argument_list|(
name|canonicalWebUrl
argument_list|)
expr_stmt|;
name|msg
operator|.
name|append
argument_list|(
literal|"#"
argument_list|)
expr_stmt|;
name|msg
operator|.
name|append
argument_list|(
name|PageLinks
operator|.
name|SETTINGS_AGREEMENTS
argument_list|)
expr_stmt|;
name|msg
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|msg
operator|.
name|append
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|AuthException
argument_list|(
name|msg
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

