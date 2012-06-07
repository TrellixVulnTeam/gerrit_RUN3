begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2009 The Android Open Source Project
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
name|common
operator|.
name|errors
operator|.
name|ProjectCreationFailedException
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
name|reviewdb
operator|.
name|client
operator|.
name|Project
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
name|server
operator|.
name|project
operator|.
name|CreateProject
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
name|CreateProjectArgs
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
name|SuggestParentCandidates
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
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Constants
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/** Create a new project. **/
end_comment

begin_class
annotation|@
name|RequiresCapability
argument_list|(
name|GlobalCapability
operator|.
name|CREATE_PROJECT
argument_list|)
DECL|class|CreateProjectCommand
specifier|final
class|class
name|CreateProjectCommand
extends|extends
name|SshCommand
block|{
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--name"
argument_list|,
name|aliases
operator|=
block|{
literal|"-n"
block|}
argument_list|,
name|metaVar
operator|=
literal|"NAME"
argument_list|,
name|usage
operator|=
literal|"name of project to be created (deprecated option)"
argument_list|)
DECL|method|setProjectNameFromOption (String name)
name|void
name|setProjectNameFromOption
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|projectName
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"NAME already supplied"
argument_list|)
throw|;
block|}
else|else
block|{
name|projectName
operator|=
name|name
expr_stmt|;
block|}
block|}
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--suggest-parents"
argument_list|,
name|aliases
operator|=
block|{
literal|"-S"
block|}
argument_list|,
name|usage
operator|=
literal|"suggest parent candidates, "
operator|+
literal|"if this option is used all other options and arguments are ignored"
argument_list|)
DECL|field|suggestParent
specifier|private
name|boolean
name|suggestParent
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--owner"
argument_list|,
name|aliases
operator|=
block|{
literal|"-o"
block|}
argument_list|,
name|usage
operator|=
literal|"owner(s) of project"
argument_list|)
DECL|field|ownerIds
specifier|private
name|List
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|ownerIds
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--parent"
argument_list|,
name|aliases
operator|=
block|{
literal|"-p"
block|}
argument_list|,
name|metaVar
operator|=
literal|"NAME"
argument_list|,
name|usage
operator|=
literal|"parent project"
argument_list|)
DECL|field|newParent
specifier|private
name|ProjectControl
name|newParent
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--permissions-only"
argument_list|,
name|usage
operator|=
literal|"create project for use only as parent"
argument_list|)
DECL|field|permissionsOnly
specifier|private
name|boolean
name|permissionsOnly
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
init|=
literal|""
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
literal|"project submit type\n"
operator|+
literal|"(default: MERGE_IF_NECESSARY)"
argument_list|)
DECL|field|submitType
specifier|private
name|SubmitType
name|submitType
init|=
name|SubmitType
operator|.
name|MERGE_IF_NECESSARY
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
DECL|field|contributorAgreements
specifier|private
name|boolean
name|contributorAgreements
decl_stmt|;
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
DECL|field|signedOffBy
specifier|private
name|boolean
name|signedOffBy
decl_stmt|;
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
DECL|field|contentMerge
specifier|private
name|boolean
name|contentMerge
decl_stmt|;
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
DECL|field|requireChangeID
specifier|private
name|boolean
name|requireChangeID
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--branch"
argument_list|,
name|aliases
operator|=
block|{
literal|"-b"
block|}
argument_list|,
name|metaVar
operator|=
literal|"BRANCH"
argument_list|,
name|usage
operator|=
literal|"initial branch name\n"
operator|+
literal|"(default: master)"
argument_list|)
DECL|field|branch
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|branch
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--empty-commit"
argument_list|,
name|usage
operator|=
literal|"to create initial empty commit"
argument_list|)
DECL|field|createEmptyCommit
specifier|private
name|boolean
name|createEmptyCommit
decl_stmt|;
DECL|field|projectName
specifier|private
name|String
name|projectName
decl_stmt|;
annotation|@
name|Argument
argument_list|(
name|index
operator|=
literal|0
argument_list|,
name|metaVar
operator|=
literal|"NAME"
argument_list|,
name|usage
operator|=
literal|"name of project to be created"
argument_list|)
DECL|method|setProjectNameFromArgument (String name)
name|void
name|setProjectNameFromArgument
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|projectName
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"--name already supplied"
argument_list|)
throw|;
block|}
else|else
block|{
name|projectName
operator|=
name|name
expr_stmt|;
block|}
block|}
annotation|@
name|Inject
DECL|field|CreateProjectFactory
specifier|private
name|CreateProject
operator|.
name|Factory
name|CreateProjectFactory
decl_stmt|;
annotation|@
name|Inject
DECL|field|suggestParentCandidatesFactory
specifier|private
name|SuggestParentCandidates
operator|.
name|Factory
name|suggestParentCandidatesFactory
decl_stmt|;
annotation|@
name|Override
DECL|method|run ()
specifier|protected
name|void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
if|if
condition|(
operator|!
name|suggestParent
condition|)
block|{
if|if
condition|(
name|projectName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|UnloggedFailure
argument_list|(
literal|1
argument_list|,
literal|"fatal: Project name is required."
argument_list|)
throw|;
block|}
specifier|final
name|CreateProjectArgs
name|args
init|=
operator|new
name|CreateProjectArgs
argument_list|()
decl_stmt|;
name|args
operator|.
name|setProjectName
argument_list|(
name|projectName
argument_list|)
expr_stmt|;
name|args
operator|.
name|ownerIds
operator|=
name|ownerIds
expr_stmt|;
name|args
operator|.
name|newParent
operator|=
name|newParent
expr_stmt|;
name|args
operator|.
name|permissionsOnly
operator|=
name|permissionsOnly
expr_stmt|;
name|args
operator|.
name|projectDescription
operator|=
name|projectDescription
expr_stmt|;
name|args
operator|.
name|submitType
operator|=
name|submitType
expr_stmt|;
name|args
operator|.
name|contributorAgreements
operator|=
name|contributorAgreements
expr_stmt|;
name|args
operator|.
name|signedOffBy
operator|=
name|signedOffBy
expr_stmt|;
name|args
operator|.
name|contentMerge
operator|=
name|contentMerge
expr_stmt|;
name|args
operator|.
name|changeIdRequired
operator|=
name|requireChangeID
expr_stmt|;
name|args
operator|.
name|branch
operator|=
name|branch
expr_stmt|;
name|args
operator|.
name|createEmptyCommit
operator|=
name|createEmptyCommit
expr_stmt|;
specifier|final
name|CreateProject
name|createProject
init|=
name|CreateProjectFactory
operator|.
name|create
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|createProject
operator|.
name|createProject
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|List
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
name|parentCandidates
init|=
name|suggestParentCandidatesFactory
operator|.
name|create
argument_list|()
operator|.
name|getNameKeys
argument_list|()
decl_stmt|;
for|for
control|(
name|Project
operator|.
name|NameKey
name|parent
range|:
name|parentCandidates
control|)
block|{
name|stdout
operator|.
name|print
argument_list|(
name|parent
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|ProjectCreationFailedException
name|err
parameter_list|)
block|{
throw|throw
operator|new
name|UnloggedFailure
argument_list|(
literal|1
argument_list|,
literal|"fatal: "
operator|+
name|err
operator|.
name|getMessage
argument_list|()
argument_list|,
name|err
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

