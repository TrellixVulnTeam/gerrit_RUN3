begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2008 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.ssh
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|ssh
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
name|client
operator|.
name|data
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
name|client
operator|.
name|reviewdb
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
name|client
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
name|client
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
name|client
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
name|client
operator|.
name|rpc
operator|.
name|Common
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
name|git
operator|.
name|InvalidRepositoryException
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
name|spearce
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_class
DECL|class|AbstractGitCommand
specifier|abstract
class|class
name|AbstractGitCommand
extends|extends
name|AbstractCommand
block|{
annotation|@
name|Argument
argument_list|(
name|index
operator|=
literal|0
argument_list|,
name|metaVar
operator|=
literal|"PROJECT.git"
argument_list|,
name|required
operator|=
literal|true
argument_list|,
name|usage
operator|=
literal|"project name"
argument_list|)
DECL|field|reqProjName
specifier|private
name|String
name|reqProjName
decl_stmt|;
DECL|field|repo
specifier|protected
name|Repository
name|repo
decl_stmt|;
DECL|field|cachedProj
specifier|protected
name|ProjectCache
operator|.
name|Entry
name|cachedProj
decl_stmt|;
DECL|field|proj
specifier|protected
name|Project
name|proj
decl_stmt|;
DECL|field|userAccount
specifier|protected
name|Account
name|userAccount
decl_stmt|;
annotation|@
name|Override
DECL|method|preRun ()
specifier|protected
name|void
name|preRun
parameter_list|()
throws|throws
name|Failure
block|{
name|super
operator|.
name|preRun
argument_list|()
expr_stmt|;
name|openReviewDb
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|protected
specifier|final
name|void
name|run
parameter_list|()
throws|throws
name|IOException
throws|,
name|Failure
block|{
name|String
name|projectName
init|=
name|reqProjName
decl_stmt|;
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
comment|// Be nice and drop the trailing ".git" suffix, which we never keep
comment|// in our database, but clients might mistakenly provide anyway.
comment|//
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
literal|4
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|projectName
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
comment|// Be nice and drop the leading "/" if supplied by an absolute path.
comment|// We don't have a file system hierarchy, just a flat namespace in
comment|// the database's Project entities. We never encode these with a
comment|// leading '/' but users might accidentally include them in Git URLs.
comment|//
name|projectName
operator|=
name|projectName
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|cachedProj
operator|=
name|Common
operator|.
name|getProjectCache
argument_list|()
operator|.
name|get
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
name|projectName
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|cachedProj
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
literal|1
argument_list|,
literal|"fatal: '"
operator|+
name|reqProjName
operator|+
literal|"': not a Gerrit project"
argument_list|)
throw|;
block|}
name|proj
operator|=
name|cachedProj
operator|.
name|getProject
argument_list|()
expr_stmt|;
if|if
condition|(
name|ProjectRight
operator|.
name|WILD_PROJECT
operator|.
name|equals
argument_list|(
name|proj
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
literal|1
argument_list|,
literal|"fatal: '"
operator|+
name|reqProjName
operator|+
literal|"': not a valid project"
argument_list|,
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot access the wildcard project"
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|canRead
argument_list|(
name|cachedProj
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
literal|1
argument_list|,
literal|"fatal: '"
operator|+
name|reqProjName
operator|+
literal|"': unknown project"
argument_list|,
operator|new
name|SecurityException
argument_list|(
literal|"Account lacks Read permission"
argument_list|)
argument_list|)
throw|;
block|}
try|try
block|{
name|repo
operator|=
name|getRepositoryCache
argument_list|()
operator|.
name|get
argument_list|(
name|proj
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidRepositoryException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
literal|1
argument_list|,
literal|"fatal: '"
operator|+
name|reqProjName
operator|+
literal|"': not a git archive"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|userAccount
operator|=
name|Common
operator|.
name|getAccountCache
argument_list|()
operator|.
name|get
argument_list|(
name|getAccountId
argument_list|()
argument_list|,
name|db
argument_list|)
expr_stmt|;
if|if
condition|(
name|userAccount
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
literal|1
argument_list|,
literal|"fatal: cannot query user database"
argument_list|,
operator|new
name|IllegalStateException
argument_list|(
literal|"Account record no longer in database"
argument_list|)
argument_list|)
throw|;
block|}
name|runImpl
argument_list|()
expr_stmt|;
block|}
DECL|method|canPerform (final ApprovalCategory.Id actionId, final short val)
specifier|protected
name|boolean
name|canPerform
parameter_list|(
specifier|final
name|ApprovalCategory
operator|.
name|Id
name|actionId
parameter_list|,
specifier|final
name|short
name|val
parameter_list|)
block|{
return|return
name|canPerform
argument_list|(
name|cachedProj
argument_list|,
name|actionId
argument_list|,
name|val
argument_list|)
return|;
block|}
DECL|method|runImpl ()
specifier|protected
specifier|abstract
name|void
name|runImpl
parameter_list|()
throws|throws
name|IOException
throws|,
name|Failure
function_decl|;
block|}
end_class

end_unit

