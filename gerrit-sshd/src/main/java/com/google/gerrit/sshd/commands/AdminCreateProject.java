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
name|reviewdb
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
name|ApprovalCategory
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
name|ProjectRight
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
name|ReviewDb
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
name|config
operator|.
name|AuthConfig
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
name|ReplicationQueue
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
name|AdminCommand
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
name|BaseCommand
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
name|client
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
name|org
operator|.
name|apache
operator|.
name|sshd
operator|.
name|server
operator|.
name|Environment
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
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Repository
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
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_comment
comment|/** Create a new project. **/
end_comment

begin_class
annotation|@
name|AdminCommand
DECL|class|AdminCreateProject
specifier|final
class|class
name|AdminCreateProject
extends|extends
name|BaseCommand
block|{
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--name"
argument_list|,
name|required
operator|=
literal|true
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
literal|"name of project to be created"
argument_list|)
DECL|field|projectName
specifier|private
name|String
name|projectName
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
literal|"owner of project\n"
operator|+
literal|"(default: Administrators)"
argument_list|)
DECL|field|ownerId
specifier|private
name|AccountGroup
operator|.
name|Id
name|ownerId
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
literal|"DESC"
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
name|String
name|branch
init|=
name|Constants
operator|.
name|MASTER
decl_stmt|;
annotation|@
name|Inject
DECL|field|db
specifier|private
name|ReviewDb
name|db
decl_stmt|;
annotation|@
name|Inject
DECL|field|repoManager
specifier|private
name|GitRepositoryManager
name|repoManager
decl_stmt|;
annotation|@
name|Inject
DECL|field|authConfig
specifier|private
name|AuthConfig
name|authConfig
decl_stmt|;
annotation|@
name|Inject
DECL|field|rq
specifier|private
name|ReplicationQueue
name|rq
decl_stmt|;
annotation|@
name|Override
DECL|method|start (final Environment env)
specifier|public
name|void
name|start
parameter_list|(
specifier|final
name|Environment
name|env
parameter_list|)
block|{
name|startThread
argument_list|(
operator|new
name|CommandRunnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|PrintWriter
name|p
init|=
name|toPrintWriter
argument_list|(
name|out
argument_list|)
decl_stmt|;
name|ownerId
operator|=
name|authConfig
operator|.
name|getAdministratorsGroup
argument_list|()
expr_stmt|;
name|parseCommandLine
argument_list|()
expr_stmt|;
try|try
block|{
name|validateParameters
argument_list|()
expr_stmt|;
name|Repository
name|repo
init|=
name|repoManager
operator|.
name|createRepository
argument_list|(
name|projectName
argument_list|)
decl_stmt|;
name|repo
operator|.
name|create
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|repo
operator|.
name|writeSymref
argument_list|(
name|Constants
operator|.
name|HEAD
argument_list|,
name|branch
argument_list|)
expr_stmt|;
name|repoManager
operator|.
name|setProjectDescription
argument_list|(
name|projectName
argument_list|,
name|projectDescription
argument_list|)
expr_stmt|;
name|createProject
argument_list|()
expr_stmt|;
name|rq
operator|.
name|replicateNewProject
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
name|projectName
argument_list|)
argument_list|,
name|branch
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|p
operator|.
name|print
argument_list|(
literal|"Error when trying to create project: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|p
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|createProject ()
specifier|private
name|void
name|createProject
parameter_list|()
throws|throws
name|OrmException
block|{
specifier|final
name|Project
operator|.
name|NameKey
name|newProjectNameKey
init|=
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
name|projectName
argument_list|)
decl_stmt|;
specifier|final
name|ProjectRight
operator|.
name|Key
name|prk
init|=
operator|new
name|ProjectRight
operator|.
name|Key
argument_list|(
name|newProjectNameKey
argument_list|,
name|ApprovalCategory
operator|.
name|OWN
argument_list|,
name|ownerId
argument_list|)
decl_stmt|;
specifier|final
name|ProjectRight
name|pr
init|=
operator|new
name|ProjectRight
argument_list|(
name|prk
argument_list|)
decl_stmt|;
name|pr
operator|.
name|setMaxValue
argument_list|(
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
name|pr
operator|.
name|setMinValue
argument_list|(
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
name|db
operator|.
name|projectRights
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|pr
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Project
name|newProject
init|=
operator|new
name|Project
argument_list|(
name|newProjectNameKey
argument_list|)
decl_stmt|;
name|newProject
operator|.
name|setDescription
argument_list|(
name|projectDescription
argument_list|)
expr_stmt|;
name|newProject
operator|.
name|setSubmitType
argument_list|(
name|submitType
argument_list|)
expr_stmt|;
name|newProject
operator|.
name|setUseContributorAgreements
argument_list|(
name|contributorAgreements
argument_list|)
expr_stmt|;
name|newProject
operator|.
name|setUseSignedOffBy
argument_list|(
name|signedOffBy
argument_list|)
expr_stmt|;
name|db
operator|.
name|projects
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|newProject
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|validateParameters ()
specifier|private
name|void
name|validateParameters
parameter_list|()
throws|throws
name|Failure
block|{
if|if
condition|(
name|projectName
operator|.
name|endsWith
argument_list|(
literal|".git"
argument_list|)
condition|)
block|{
name|projectName
operator|=
name|projectName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|projectName
operator|.
name|length
argument_list|()
operator|-
literal|".git"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
name|branch
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|branch
operator|=
name|branch
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|branch
operator|.
name|startsWith
argument_list|(
name|Constants
operator|.
name|R_HEADS
argument_list|)
condition|)
block|{
name|branch
operator|=
name|Constants
operator|.
name|R_HEADS
operator|+
name|branch
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|Repository
operator|.
name|isValidRefName
argument_list|(
name|branch
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
literal|1
argument_list|,
literal|"--branch \""
operator|+
name|branch
operator|+
literal|"\" is not a valid name"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

