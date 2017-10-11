begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2012 The Android Open Source Project
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
DECL|package|com.google.gerrit.sshd.commands
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|sshd
operator|.
name|commands
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
name|projects
operator|.
name|ConfigInput
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
name|InheritableBoolean
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
name|ProjectState
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
name|SubmitType
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
name|PutConfig
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
name|sshd
operator|.
name|CommandMetaData
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
name|sshd
operator|.
name|SshCommand
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
name|org
operator|.
name|kohsuke
operator|.
name|args4j
operator|.
name|Argument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|kohsuke
operator|.
name|args4j
operator|.
name|Option
import|;
end_import

begin_class
annotation|@
name|CommandMetaData
argument_list|(
name|name
operator|=
literal|"set-project"
argument_list|,
name|description
operator|=
literal|"Change a project's settings"
argument_list|)
DECL|class|SetProjectCommand
specifier|final
class|class
name|SetProjectCommand
extends|extends
name|SshCommand
block|{
annotation|@
name|Argument
argument_list|(
name|index
operator|=
literal|0
argument_list|,
name|required
operator|=
literal|true
argument_list|,
name|metaVar
operator|=
literal|"NAME"
argument_list|,
name|usage
operator|=
literal|"name of the project"
argument_list|)
DECL|field|projectControl
specifier|private
name|ProjectControl
name|projectControl
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--description"
argument_list|,
name|aliases
operator|=
block|{
literal|"-d"
block|}
argument_list|,
name|metaVar
operator|=
literal|"DESCRIPTION"
argument_list|,
name|usage
operator|=
literal|"description of project"
argument_list|)
DECL|field|projectDescription
specifier|private
name|String
name|projectDescription
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--submit-type"
argument_list|,
name|aliases
operator|=
block|{
literal|"-t"
block|}
argument_list|,
name|usage
operator|=
literal|"project submit type\n(default: MERGE_IF_NECESSARY)"
argument_list|)
DECL|field|submitType
specifier|private
name|SubmitType
name|submitType
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--contributor-agreements"
argument_list|,
name|usage
operator|=
literal|"if contributor agreement is required"
argument_list|)
DECL|field|contributorAgreements
specifier|private
name|InheritableBoolean
name|contributorAgreements
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--signed-off-by"
argument_list|,
name|usage
operator|=
literal|"if signed-off-by is required"
argument_list|)
DECL|field|signedOffBy
specifier|private
name|InheritableBoolean
name|signedOffBy
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--content-merge"
argument_list|,
name|usage
operator|=
literal|"allow automatic conflict resolving within files"
argument_list|)
DECL|field|contentMerge
specifier|private
name|InheritableBoolean
name|contentMerge
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--change-id"
argument_list|,
name|usage
operator|=
literal|"if change-id is required"
argument_list|)
DECL|field|requireChangeID
specifier|private
name|InheritableBoolean
name|requireChangeID
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--use-contributor-agreements"
argument_list|,
name|aliases
operator|=
block|{
literal|"--ca"
block|}
argument_list|,
name|usage
operator|=
literal|"if contributor agreement is required"
argument_list|)
DECL|method|setUseContributorArgreements (@uppressWarningsR) boolean on)
name|void
name|setUseContributorArgreements
parameter_list|(
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
name|boolean
name|on
parameter_list|)
block|{
name|contributorAgreements
operator|=
name|InheritableBoolean
operator|.
name|TRUE
expr_stmt|;
block|}
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--no-contributor-agreements"
argument_list|,
name|aliases
operator|=
block|{
literal|"--nca"
block|}
argument_list|,
name|usage
operator|=
literal|"if contributor agreement is not required"
argument_list|)
DECL|method|setNoContributorArgreements (@uppressWarningsR) boolean on)
name|void
name|setNoContributorArgreements
parameter_list|(
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
name|boolean
name|on
parameter_list|)
block|{
name|contributorAgreements
operator|=
name|InheritableBoolean
operator|.
name|FALSE
expr_stmt|;
block|}
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--use-signed-off-by"
argument_list|,
name|aliases
operator|=
block|{
literal|"--so"
block|}
argument_list|,
name|usage
operator|=
literal|"if signed-off-by is required"
argument_list|)
DECL|method|setUseSignedOffBy (@uppressWarningsR) boolean on)
name|void
name|setUseSignedOffBy
parameter_list|(
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
name|boolean
name|on
parameter_list|)
block|{
name|signedOffBy
operator|=
name|InheritableBoolean
operator|.
name|TRUE
expr_stmt|;
block|}
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--no-signed-off-by"
argument_list|,
name|aliases
operator|=
block|{
literal|"--nso"
block|}
argument_list|,
name|usage
operator|=
literal|"if signed-off-by is not required"
argument_list|)
DECL|method|setNoSignedOffBy (@uppressWarningsR) boolean on)
name|void
name|setNoSignedOffBy
parameter_list|(
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
name|boolean
name|on
parameter_list|)
block|{
name|signedOffBy
operator|=
name|InheritableBoolean
operator|.
name|FALSE
expr_stmt|;
block|}
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--use-content-merge"
argument_list|,
name|usage
operator|=
literal|"allow automatic conflict resolving within files"
argument_list|)
DECL|method|setUseContentMerge (@uppressWarningsR) boolean on)
name|void
name|setUseContentMerge
parameter_list|(
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
name|boolean
name|on
parameter_list|)
block|{
name|contentMerge
operator|=
name|InheritableBoolean
operator|.
name|TRUE
expr_stmt|;
block|}
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--no-content-merge"
argument_list|,
name|usage
operator|=
literal|"don't allow automatic conflict resolving within files"
argument_list|)
DECL|method|setNoContentMerge (@uppressWarningsR) boolean on)
name|void
name|setNoContentMerge
parameter_list|(
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
name|boolean
name|on
parameter_list|)
block|{
name|contentMerge
operator|=
name|InheritableBoolean
operator|.
name|FALSE
expr_stmt|;
block|}
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--require-change-id"
argument_list|,
name|aliases
operator|=
block|{
literal|"--id"
block|}
argument_list|,
name|usage
operator|=
literal|"if change-id is required"
argument_list|)
DECL|method|setRequireChangeId (@uppressWarningsR) boolean on)
name|void
name|setRequireChangeId
parameter_list|(
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
name|boolean
name|on
parameter_list|)
block|{
name|requireChangeID
operator|=
name|InheritableBoolean
operator|.
name|TRUE
expr_stmt|;
block|}
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--no-change-id"
argument_list|,
name|aliases
operator|=
block|{
literal|"--nid"
block|}
argument_list|,
name|usage
operator|=
literal|"if change-id is not required"
argument_list|)
DECL|method|setNoChangeId (@uppressWarningsR) boolean on)
name|void
name|setNoChangeId
parameter_list|(
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
name|boolean
name|on
parameter_list|)
block|{
name|requireChangeID
operator|=
name|InheritableBoolean
operator|.
name|FALSE
expr_stmt|;
block|}
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--project-state"
argument_list|,
name|aliases
operator|=
block|{
literal|"--ps"
block|}
argument_list|,
name|usage
operator|=
literal|"project's visibility state"
argument_list|)
DECL|field|state
specifier|private
name|ProjectState
name|state
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--max-object-size-limit"
argument_list|,
name|usage
operator|=
literal|"max Git object size for this project"
argument_list|)
DECL|field|maxObjectSizeLimit
specifier|private
name|String
name|maxObjectSizeLimit
decl_stmt|;
DECL|field|putConfig
annotation|@
name|Inject
specifier|private
name|PutConfig
name|putConfig
decl_stmt|;
annotation|@
name|Override
DECL|method|run ()
specifier|protected
name|void
name|run
parameter_list|()
throws|throws
name|Failure
block|{
name|ConfigInput
name|configInput
init|=
operator|new
name|ConfigInput
argument_list|()
decl_stmt|;
name|configInput
operator|.
name|requireChangeId
operator|=
name|requireChangeID
expr_stmt|;
name|configInput
operator|.
name|submitType
operator|=
name|submitType
expr_stmt|;
name|configInput
operator|.
name|useContentMerge
operator|=
name|contentMerge
expr_stmt|;
name|configInput
operator|.
name|useContributorAgreements
operator|=
name|contributorAgreements
expr_stmt|;
name|configInput
operator|.
name|useSignedOffBy
operator|=
name|signedOffBy
expr_stmt|;
name|configInput
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|configInput
operator|.
name|maxObjectSizeLimit
operator|=
name|maxObjectSizeLimit
expr_stmt|;
comment|// Description is different to other parameters, null won't result in
comment|// keeping the existing description, it would delete it.
if|if
condition|(
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|projectDescription
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|configInput
operator|.
name|description
operator|=
name|projectDescription
expr_stmt|;
block|}
else|else
block|{
name|configInput
operator|.
name|description
operator|=
name|projectControl
operator|.
name|getProject
argument_list|()
operator|.
name|getDescription
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|putConfig
operator|.
name|apply
argument_list|(
operator|new
name|ProjectResource
argument_list|(
name|projectControl
operator|.
name|getProjectState
argument_list|()
argument_list|,
name|projectControl
operator|.
name|getUser
argument_list|()
argument_list|)
argument_list|,
name|configInput
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RestApiException
decl||
name|PermissionBackendException
name|e
parameter_list|)
block|{
throw|throw
name|die
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

