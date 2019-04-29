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
DECL|package|com.google.gerrit.server.edit
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|edit
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
name|common
operator|.
name|CommitInfo
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
name|EditInfo
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
name|FetchInfo
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
name|config
operator|.
name|DownloadCommand
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
name|config
operator|.
name|DownloadScheme
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
name|registration
operator|.
name|Extension
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
name|CommonConverters
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
name|change
operator|.
name|DownloadCommandsJson
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
name|LinkedHashMap
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
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|revwalk
operator|.
name|RevCommit
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|ChangeEditJson
specifier|public
class|class
name|ChangeEditJson
block|{
DECL|field|downloadCommands
specifier|private
specifier|final
name|DynamicMap
argument_list|<
name|DownloadCommand
argument_list|>
name|downloadCommands
decl_stmt|;
DECL|field|downloadSchemes
specifier|private
specifier|final
name|DynamicMap
argument_list|<
name|DownloadScheme
argument_list|>
name|downloadSchemes
decl_stmt|;
DECL|field|userProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|userProvider
decl_stmt|;
annotation|@
name|Inject
DECL|method|ChangeEditJson ( DynamicMap<DownloadCommand> downloadCommand, DynamicMap<DownloadScheme> downloadSchemes, Provider<CurrentUser> userProvider)
name|ChangeEditJson
parameter_list|(
name|DynamicMap
argument_list|<
name|DownloadCommand
argument_list|>
name|downloadCommand
parameter_list|,
name|DynamicMap
argument_list|<
name|DownloadScheme
argument_list|>
name|downloadSchemes
parameter_list|,
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|userProvider
parameter_list|)
block|{
name|this
operator|.
name|downloadCommands
operator|=
name|downloadCommand
expr_stmt|;
name|this
operator|.
name|downloadSchemes
operator|=
name|downloadSchemes
expr_stmt|;
name|this
operator|.
name|userProvider
operator|=
name|userProvider
expr_stmt|;
block|}
DECL|method|toEditInfo (ChangeEdit edit, boolean downloadCommands)
specifier|public
name|EditInfo
name|toEditInfo
parameter_list|(
name|ChangeEdit
name|edit
parameter_list|,
name|boolean
name|downloadCommands
parameter_list|)
block|{
name|EditInfo
name|out
init|=
operator|new
name|EditInfo
argument_list|()
decl_stmt|;
name|out
operator|.
name|commit
operator|=
name|fillCommit
argument_list|(
name|edit
operator|.
name|getEditCommit
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|baseRevision
operator|=
name|edit
operator|.
name|getBasePatchSet
argument_list|()
operator|.
name|getCommitId
argument_list|()
operator|.
name|name
argument_list|()
expr_stmt|;
name|out
operator|.
name|basePatchSetNumber
operator|=
name|edit
operator|.
name|getBasePatchSet
argument_list|()
operator|.
name|getPatchSetId
argument_list|()
expr_stmt|;
name|out
operator|.
name|ref
operator|=
name|edit
operator|.
name|getRefName
argument_list|()
expr_stmt|;
if|if
condition|(
name|downloadCommands
condition|)
block|{
name|out
operator|.
name|fetch
operator|=
name|fillFetchMap
argument_list|(
name|edit
argument_list|)
expr_stmt|;
block|}
return|return
name|out
return|;
block|}
DECL|method|fillCommit (RevCommit editCommit)
specifier|private
specifier|static
name|CommitInfo
name|fillCommit
parameter_list|(
name|RevCommit
name|editCommit
parameter_list|)
block|{
name|CommitInfo
name|commit
init|=
operator|new
name|CommitInfo
argument_list|()
decl_stmt|;
name|commit
operator|.
name|commit
operator|=
name|editCommit
operator|.
name|toObjectId
argument_list|()
operator|.
name|getName
argument_list|()
expr_stmt|;
name|commit
operator|.
name|author
operator|=
name|CommonConverters
operator|.
name|toGitPerson
argument_list|(
name|editCommit
operator|.
name|getAuthorIdent
argument_list|()
argument_list|)
expr_stmt|;
name|commit
operator|.
name|committer
operator|=
name|CommonConverters
operator|.
name|toGitPerson
argument_list|(
name|editCommit
operator|.
name|getCommitterIdent
argument_list|()
argument_list|)
expr_stmt|;
name|commit
operator|.
name|subject
operator|=
name|editCommit
operator|.
name|getShortMessage
argument_list|()
expr_stmt|;
name|commit
operator|.
name|message
operator|=
name|editCommit
operator|.
name|getFullMessage
argument_list|()
expr_stmt|;
name|commit
operator|.
name|parents
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|editCommit
operator|.
name|getParentCount
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|RevCommit
name|p
range|:
name|editCommit
operator|.
name|getParents
argument_list|()
control|)
block|{
name|CommitInfo
name|i
init|=
operator|new
name|CommitInfo
argument_list|()
decl_stmt|;
name|i
operator|.
name|commit
operator|=
name|p
operator|.
name|name
argument_list|()
expr_stmt|;
name|commit
operator|.
name|parents
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|commit
return|;
block|}
DECL|method|fillFetchMap (ChangeEdit edit)
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|FetchInfo
argument_list|>
name|fillFetchMap
parameter_list|(
name|ChangeEdit
name|edit
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|FetchInfo
argument_list|>
name|r
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Extension
argument_list|<
name|DownloadScheme
argument_list|>
name|e
range|:
name|downloadSchemes
control|)
block|{
name|String
name|schemeName
init|=
name|e
operator|.
name|getExportName
argument_list|()
decl_stmt|;
name|DownloadScheme
name|scheme
init|=
name|e
operator|.
name|getProvider
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|scheme
operator|.
name|isEnabled
argument_list|()
operator|||
operator|(
name|scheme
operator|.
name|isAuthRequired
argument_list|()
operator|&&
operator|!
name|userProvider
operator|.
name|get
argument_list|()
operator|.
name|isIdentifiedUser
argument_list|()
operator|)
condition|)
block|{
continue|continue;
block|}
comment|// No fluff, just stuff
if|if
condition|(
operator|!
name|scheme
operator|.
name|isAuthSupported
argument_list|()
condition|)
block|{
continue|continue;
block|}
name|String
name|projectName
init|=
name|edit
operator|.
name|getChange
argument_list|()
operator|.
name|getProject
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
name|String
name|refName
init|=
name|edit
operator|.
name|getRefName
argument_list|()
decl_stmt|;
name|FetchInfo
name|fetchInfo
init|=
operator|new
name|FetchInfo
argument_list|(
name|scheme
operator|.
name|getUrl
argument_list|(
name|projectName
argument_list|)
argument_list|,
name|refName
argument_list|)
decl_stmt|;
name|r
operator|.
name|put
argument_list|(
name|schemeName
argument_list|,
name|fetchInfo
argument_list|)
expr_stmt|;
name|DownloadCommandsJson
operator|.
name|populateFetchMap
argument_list|(
name|scheme
argument_list|,
name|downloadCommands
argument_list|,
name|projectName
argument_list|,
name|refName
argument_list|,
name|fetchInfo
argument_list|)
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
block|}
end_class

end_unit

