begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2016 The Android Open Source Project
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
name|common
operator|.
name|MergeableInfo
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
name|ResourceNotFoundException
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
name|RestReadView
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
name|GerritServerConfig
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
name|InMemoryInserter
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
name|MergeUtil
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
name|BranchResource
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
name|java
operator|.
name|io
operator|.
name|IOException
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
name|Config
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
name|ObjectInserter
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
name|Ref
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
name|eclipse
operator|.
name|jgit
operator|.
name|merge
operator|.
name|Merger
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
name|merge
operator|.
name|ResolveMerger
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
name|RevWalk
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

begin_comment
comment|/** Check the mergeability at current branch for a git object references expression. */
end_comment

begin_class
DECL|class|CheckMergeability
specifier|public
class|class
name|CheckMergeability
implements|implements
name|RestReadView
argument_list|<
name|BranchResource
argument_list|>
block|{
DECL|field|source
specifier|private
name|String
name|source
decl_stmt|;
DECL|field|strategy
specifier|private
name|String
name|strategy
decl_stmt|;
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
literal|"--source"
argument_list|,
name|metaVar
operator|=
literal|"COMMIT"
argument_list|,
name|usage
operator|=
literal|"the source reference to merge, which could be any git object "
operator|+
literal|"references expression, refer to "
operator|+
literal|"org.eclipse.jgit.lib.Repository#resolve(String)"
argument_list|,
name|required
operator|=
literal|true
argument_list|)
DECL|method|setSource (String source)
specifier|public
name|void
name|setSource
parameter_list|(
name|String
name|source
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
block|}
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--strategy"
argument_list|,
name|metaVar
operator|=
literal|"STRATEGY"
argument_list|,
name|usage
operator|=
literal|"name of the merge strategy, refer to org.eclipse.jgit.merge.MergeStrategy"
argument_list|)
DECL|method|setStrategy (String strategy)
specifier|public
name|void
name|setStrategy
parameter_list|(
name|String
name|strategy
parameter_list|)
block|{
name|this
operator|.
name|strategy
operator|=
name|strategy
expr_stmt|;
block|}
DECL|field|gitManager
specifier|private
specifier|final
name|GitRepositoryManager
name|gitManager
decl_stmt|;
DECL|field|commits
specifier|private
specifier|final
name|CommitsCollection
name|commits
decl_stmt|;
annotation|@
name|Inject
DECL|method|CheckMergeability ( GitRepositoryManager gitManager, CommitsCollection commits, @GerritServerConfig Config cfg)
name|CheckMergeability
parameter_list|(
name|GitRepositoryManager
name|gitManager
parameter_list|,
name|CommitsCollection
name|commits
parameter_list|,
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|)
block|{
name|this
operator|.
name|gitManager
operator|=
name|gitManager
expr_stmt|;
name|this
operator|.
name|commits
operator|=
name|commits
expr_stmt|;
name|this
operator|.
name|strategy
operator|=
name|MergeUtil
operator|.
name|getMergeStrategy
argument_list|(
name|cfg
argument_list|)
operator|.
name|getName
argument_list|()
expr_stmt|;
name|this
operator|.
name|submitType
operator|=
name|cfg
operator|.
name|getEnum
argument_list|(
literal|"project"
argument_list|,
literal|null
argument_list|,
literal|"submitType"
argument_list|,
name|SubmitType
operator|.
name|MERGE_IF_NECESSARY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (BranchResource resource)
specifier|public
name|MergeableInfo
name|apply
parameter_list|(
name|BranchResource
name|resource
parameter_list|)
throws|throws
name|IOException
throws|,
name|OrmException
throws|,
name|BadRequestException
throws|,
name|ResourceNotFoundException
block|{
if|if
condition|(
operator|!
operator|(
name|submitType
operator|.
name|equals
argument_list|(
name|SubmitType
operator|.
name|MERGE_ALWAYS
argument_list|)
operator|||
name|submitType
operator|.
name|equals
argument_list|(
name|SubmitType
operator|.
name|MERGE_IF_NECESSARY
argument_list|)
operator|)
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"Submit type: "
operator|+
name|submitType
operator|+
literal|" is not supported"
argument_list|)
throw|;
block|}
name|MergeableInfo
name|result
init|=
operator|new
name|MergeableInfo
argument_list|()
decl_stmt|;
name|result
operator|.
name|submitType
operator|=
name|submitType
expr_stmt|;
name|result
operator|.
name|strategy
operator|=
name|strategy
expr_stmt|;
try|try
init|(
name|Repository
name|git
init|=
name|gitManager
operator|.
name|openRepository
argument_list|(
name|resource
operator|.
name|getNameKey
argument_list|()
argument_list|)
init|;
name|RevWalk
name|rw
operator|=
operator|new
name|RevWalk
argument_list|(
name|git
argument_list|)
init|;
name|ObjectInserter
name|inserter
operator|=
operator|new
name|InMemoryInserter
argument_list|(
name|git
argument_list|)
init|)
block|{
name|Merger
name|m
init|=
name|MergeUtil
operator|.
name|newMerger
argument_list|(
name|inserter
argument_list|,
name|git
operator|.
name|getConfig
argument_list|()
argument_list|,
name|strategy
argument_list|)
decl_stmt|;
name|Ref
name|destRef
init|=
name|git
operator|.
name|getRefDatabase
argument_list|()
operator|.
name|exactRef
argument_list|(
name|resource
operator|.
name|getRef
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|destRef
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
name|resource
operator|.
name|getRef
argument_list|()
argument_list|)
throw|;
block|}
name|RevCommit
name|targetCommit
init|=
name|rw
operator|.
name|parseCommit
argument_list|(
name|destRef
operator|.
name|getObjectId
argument_list|()
argument_list|)
decl_stmt|;
name|RevCommit
name|sourceCommit
init|=
name|MergeUtil
operator|.
name|resolveCommit
argument_list|(
name|git
argument_list|,
name|rw
argument_list|,
name|source
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|commits
operator|.
name|canRead
argument_list|(
name|resource
operator|.
name|getProjectState
argument_list|()
argument_list|,
name|git
argument_list|,
name|sourceCommit
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"do not have read permission for: "
operator|+
name|source
argument_list|)
throw|;
block|}
if|if
condition|(
name|rw
operator|.
name|isMergedInto
argument_list|(
name|sourceCommit
argument_list|,
name|targetCommit
argument_list|)
condition|)
block|{
name|result
operator|.
name|mergeable
operator|=
literal|true
expr_stmt|;
name|result
operator|.
name|commitMerged
operator|=
literal|true
expr_stmt|;
name|result
operator|.
name|contentMerged
operator|=
literal|true
expr_stmt|;
return|return
name|result
return|;
block|}
if|if
condition|(
name|m
operator|.
name|merge
argument_list|(
literal|false
argument_list|,
name|targetCommit
argument_list|,
name|sourceCommit
argument_list|)
condition|)
block|{
name|result
operator|.
name|mergeable
operator|=
literal|true
expr_stmt|;
name|result
operator|.
name|commitMerged
operator|=
literal|false
expr_stmt|;
name|result
operator|.
name|contentMerged
operator|=
name|m
operator|.
name|getResultTreeId
argument_list|()
operator|.
name|equals
argument_list|(
name|targetCommit
operator|.
name|getTree
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|.
name|mergeable
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|m
operator|instanceof
name|ResolveMerger
condition|)
block|{
name|result
operator|.
name|conflicts
operator|=
operator|(
operator|(
name|ResolveMerger
operator|)
name|m
operator|)
operator|.
name|getUnmergedPaths
argument_list|()
expr_stmt|;
block|}
block|}
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
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

