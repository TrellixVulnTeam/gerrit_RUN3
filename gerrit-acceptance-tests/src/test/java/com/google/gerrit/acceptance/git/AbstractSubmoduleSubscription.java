begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2015 The Android Open Source Project
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
DECL|package|com.google.gerrit.acceptance.git
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|git
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|truth
operator|.
name|Truth
operator|.
name|assertThat
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
name|collect
operator|.
name|Iterables
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
name|acceptance
operator|.
name|AbstractDaemonTest
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
name|data
operator|.
name|Permission
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
name|SubscribeSection
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
name|git
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
name|git
operator|.
name|ProjectConfig
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
name|junit
operator|.
name|TestRepository
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
name|ObjectId
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
name|RevObject
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
name|RevTree
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
name|eclipse
operator|.
name|jgit
operator|.
name|transport
operator|.
name|PushResult
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
name|transport
operator|.
name|RefSpec
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
name|transport
operator|.
name|RemoteRefUpdate
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
name|transport
operator|.
name|RemoteRefUpdate
operator|.
name|Status
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_class
DECL|class|AbstractSubmoduleSubscription
specifier|public
specifier|abstract
class|class
name|AbstractSubmoduleSubscription
extends|extends
name|AbstractDaemonTest
block|{
DECL|method|getSubmitType ()
specifier|protected
name|SubmitType
name|getSubmitType
parameter_list|()
block|{
return|return
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
return|;
block|}
DECL|method|submitByMergeAlways ()
specifier|protected
specifier|static
name|Config
name|submitByMergeAlways
parameter_list|()
block|{
name|Config
name|cfg
init|=
operator|new
name|Config
argument_list|()
decl_stmt|;
name|cfg
operator|.
name|setBoolean
argument_list|(
literal|"change"
argument_list|,
literal|null
argument_list|,
literal|"submitWholeTopic"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|setEnum
argument_list|(
literal|"project"
argument_list|,
literal|null
argument_list|,
literal|"submitType"
argument_list|,
name|SubmitType
operator|.
name|MERGE_ALWAYS
argument_list|)
expr_stmt|;
return|return
name|cfg
return|;
block|}
DECL|method|submitByMergeIfNecessary ()
specifier|protected
specifier|static
name|Config
name|submitByMergeIfNecessary
parameter_list|()
block|{
name|Config
name|cfg
init|=
operator|new
name|Config
argument_list|()
decl_stmt|;
name|cfg
operator|.
name|setBoolean
argument_list|(
literal|"change"
argument_list|,
literal|null
argument_list|,
literal|"submitWholeTopic"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|setEnum
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
return|return
name|cfg
return|;
block|}
DECL|method|submitByCherryPickConfig ()
specifier|protected
specifier|static
name|Config
name|submitByCherryPickConfig
parameter_list|()
block|{
name|Config
name|cfg
init|=
operator|new
name|Config
argument_list|()
decl_stmt|;
name|cfg
operator|.
name|setBoolean
argument_list|(
literal|"change"
argument_list|,
literal|null
argument_list|,
literal|"submitWholeTopic"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|setEnum
argument_list|(
literal|"project"
argument_list|,
literal|null
argument_list|,
literal|"submitType"
argument_list|,
name|SubmitType
operator|.
name|CHERRY_PICK
argument_list|)
expr_stmt|;
return|return
name|cfg
return|;
block|}
DECL|method|submitByRebaseAlwaysConfig ()
specifier|protected
specifier|static
name|Config
name|submitByRebaseAlwaysConfig
parameter_list|()
block|{
name|Config
name|cfg
init|=
operator|new
name|Config
argument_list|()
decl_stmt|;
name|cfg
operator|.
name|setBoolean
argument_list|(
literal|"change"
argument_list|,
literal|null
argument_list|,
literal|"submitWholeTopic"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|setEnum
argument_list|(
literal|"project"
argument_list|,
literal|null
argument_list|,
literal|"submitType"
argument_list|,
name|SubmitType
operator|.
name|REBASE_ALWAYS
argument_list|)
expr_stmt|;
return|return
name|cfg
return|;
block|}
DECL|method|submitByRebaseIfNecessaryConfig ()
specifier|protected
specifier|static
name|Config
name|submitByRebaseIfNecessaryConfig
parameter_list|()
block|{
name|Config
name|cfg
init|=
operator|new
name|Config
argument_list|()
decl_stmt|;
name|cfg
operator|.
name|setBoolean
argument_list|(
literal|"change"
argument_list|,
literal|null
argument_list|,
literal|"submitWholeTopic"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|setEnum
argument_list|(
literal|"project"
argument_list|,
literal|null
argument_list|,
literal|"submitType"
argument_list|,
name|SubmitType
operator|.
name|REBASE_IF_NECESSARY
argument_list|)
expr_stmt|;
return|return
name|cfg
return|;
block|}
DECL|method|createProjectWithPush (String name, @Nullable Project.NameKey parent, boolean createEmptyCommit, SubmitType submitType)
specifier|protected
name|TestRepository
argument_list|<
name|?
argument_list|>
name|createProjectWithPush
parameter_list|(
name|String
name|name
parameter_list|,
annotation|@
name|Nullable
name|Project
operator|.
name|NameKey
name|parent
parameter_list|,
name|boolean
name|createEmptyCommit
parameter_list|,
name|SubmitType
name|submitType
parameter_list|)
throws|throws
name|Exception
block|{
name|Project
operator|.
name|NameKey
name|project
init|=
name|createProject
argument_list|(
name|name
argument_list|,
name|parent
argument_list|,
name|createEmptyCommit
argument_list|,
name|submitType
argument_list|)
decl_stmt|;
name|grant
argument_list|(
name|Permission
operator|.
name|PUSH
argument_list|,
name|project
argument_list|,
literal|"refs/heads/*"
argument_list|)
expr_stmt|;
name|grant
argument_list|(
name|Permission
operator|.
name|SUBMIT
argument_list|,
name|project
argument_list|,
literal|"refs/for/refs/heads/*"
argument_list|)
expr_stmt|;
return|return
name|cloneProject
argument_list|(
name|project
argument_list|)
return|;
block|}
DECL|method|createProjectWithPush (String name, @Nullable Project.NameKey parent)
specifier|protected
name|TestRepository
argument_list|<
name|?
argument_list|>
name|createProjectWithPush
parameter_list|(
name|String
name|name
parameter_list|,
annotation|@
name|Nullable
name|Project
operator|.
name|NameKey
name|parent
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|createProjectWithPush
argument_list|(
name|name
argument_list|,
name|parent
argument_list|,
literal|true
argument_list|,
name|getSubmitType
argument_list|()
argument_list|)
return|;
block|}
DECL|method|createProjectWithPush (String name, boolean createEmptyCommit)
specifier|protected
name|TestRepository
argument_list|<
name|?
argument_list|>
name|createProjectWithPush
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|createEmptyCommit
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|createProjectWithPush
argument_list|(
name|name
argument_list|,
literal|null
argument_list|,
name|createEmptyCommit
argument_list|,
name|getSubmitType
argument_list|()
argument_list|)
return|;
block|}
DECL|method|createProjectWithPush (String name)
specifier|protected
name|TestRepository
argument_list|<
name|?
argument_list|>
name|createProjectWithPush
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|createProjectWithPush
argument_list|(
name|name
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
name|getSubmitType
argument_list|()
argument_list|)
return|;
block|}
DECL|field|contentCounter
specifier|private
specifier|static
name|AtomicInteger
name|contentCounter
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|method|pushChangeTo (TestRepository<?> repo, String ref, String file, String content, String message, String topic)
specifier|protected
name|ObjectId
name|pushChangeTo
parameter_list|(
name|TestRepository
argument_list|<
name|?
argument_list|>
name|repo
parameter_list|,
name|String
name|ref
parameter_list|,
name|String
name|file
parameter_list|,
name|String
name|content
parameter_list|,
name|String
name|message
parameter_list|,
name|String
name|topic
parameter_list|)
throws|throws
name|Exception
block|{
name|ObjectId
name|ret
init|=
name|repo
operator|.
name|branch
argument_list|(
literal|"HEAD"
argument_list|)
operator|.
name|commit
argument_list|()
operator|.
name|insertChangeId
argument_list|()
operator|.
name|message
argument_list|(
name|message
argument_list|)
operator|.
name|add
argument_list|(
name|file
argument_list|,
name|content
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|String
name|pushedRef
init|=
name|ref
decl_stmt|;
if|if
condition|(
operator|!
name|topic
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|pushedRef
operator|+=
literal|"/"
operator|+
name|name
argument_list|(
name|topic
argument_list|)
expr_stmt|;
block|}
name|String
name|refspec
init|=
literal|"HEAD:"
operator|+
name|pushedRef
decl_stmt|;
name|Iterable
argument_list|<
name|PushResult
argument_list|>
name|res
init|=
name|repo
operator|.
name|git
argument_list|()
operator|.
name|push
argument_list|()
operator|.
name|setRemote
argument_list|(
literal|"origin"
argument_list|)
operator|.
name|setRefSpecs
argument_list|(
operator|new
name|RefSpec
argument_list|(
name|refspec
argument_list|)
argument_list|)
operator|.
name|call
argument_list|()
decl_stmt|;
name|RemoteRefUpdate
name|u
init|=
name|Iterables
operator|.
name|getOnlyElement
argument_list|(
name|res
argument_list|)
operator|.
name|getRemoteUpdate
argument_list|(
name|pushedRef
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|u
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|u
operator|.
name|getStatus
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|Status
operator|.
name|OK
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|u
operator|.
name|getNewObjectId
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|ret
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
DECL|method|pushChangeTo (TestRepository<?> repo, String ref, String message, String topic)
specifier|protected
name|ObjectId
name|pushChangeTo
parameter_list|(
name|TestRepository
argument_list|<
name|?
argument_list|>
name|repo
parameter_list|,
name|String
name|ref
parameter_list|,
name|String
name|message
parameter_list|,
name|String
name|topic
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|pushChangeTo
argument_list|(
name|repo
argument_list|,
name|ref
argument_list|,
literal|"a.txt"
argument_list|,
literal|"a contents: "
operator|+
name|contentCounter
operator|.
name|incrementAndGet
argument_list|()
argument_list|,
name|message
argument_list|,
name|topic
argument_list|)
return|;
block|}
DECL|method|pushChangeTo (TestRepository<?> repo, String branch)
specifier|protected
name|ObjectId
name|pushChangeTo
parameter_list|(
name|TestRepository
argument_list|<
name|?
argument_list|>
name|repo
parameter_list|,
name|String
name|branch
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|pushChangeTo
argument_list|(
name|repo
argument_list|,
literal|"refs/heads/"
operator|+
name|branch
argument_list|,
literal|"some change"
argument_list|,
literal|""
argument_list|)
return|;
block|}
DECL|method|allowSubmoduleSubscription (String submodule, String subBranch, String superproject, String superBranch, boolean match)
specifier|protected
name|void
name|allowSubmoduleSubscription
parameter_list|(
name|String
name|submodule
parameter_list|,
name|String
name|subBranch
parameter_list|,
name|String
name|superproject
parameter_list|,
name|String
name|superBranch
parameter_list|,
name|boolean
name|match
parameter_list|)
throws|throws
name|Exception
block|{
name|Project
operator|.
name|NameKey
name|sub
init|=
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
name|name
argument_list|(
name|submodule
argument_list|)
argument_list|)
decl_stmt|;
name|Project
operator|.
name|NameKey
name|superName
init|=
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
name|name
argument_list|(
name|superproject
argument_list|)
argument_list|)
decl_stmt|;
try|try
init|(
name|MetaDataUpdate
name|md
init|=
name|metaDataUpdateFactory
operator|.
name|create
argument_list|(
name|sub
argument_list|)
init|)
block|{
name|md
operator|.
name|setMessage
argument_list|(
literal|"Added superproject subscription"
argument_list|)
expr_stmt|;
name|ProjectConfig
name|pc
init|=
name|ProjectConfig
operator|.
name|read
argument_list|(
name|md
argument_list|)
decl_stmt|;
name|SubscribeSection
name|s
init|=
operator|new
name|SubscribeSection
argument_list|(
name|superName
argument_list|)
decl_stmt|;
name|String
name|refspec
decl_stmt|;
if|if
condition|(
name|superBranch
operator|==
literal|null
condition|)
block|{
name|refspec
operator|=
name|subBranch
expr_stmt|;
block|}
else|else
block|{
name|refspec
operator|=
name|subBranch
operator|+
literal|":"
operator|+
name|superBranch
expr_stmt|;
block|}
if|if
condition|(
name|match
condition|)
block|{
name|s
operator|.
name|addMatchingRefSpec
argument_list|(
name|refspec
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|s
operator|.
name|addMultiMatchRefSpec
argument_list|(
name|refspec
argument_list|)
expr_stmt|;
block|}
name|pc
operator|.
name|addSubscribeSection
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|ObjectId
name|oldId
init|=
name|pc
operator|.
name|getRevision
argument_list|()
decl_stmt|;
name|ObjectId
name|newId
init|=
name|pc
operator|.
name|commit
argument_list|(
name|md
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|newId
argument_list|)
operator|.
name|isNotEqualTo
argument_list|(
name|oldId
argument_list|)
expr_stmt|;
name|projectCache
operator|.
name|evict
argument_list|(
name|pc
operator|.
name|getProject
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|allowMatchingSubmoduleSubscription (String submodule, String subBranch, String superproject, String superBranch)
specifier|protected
name|void
name|allowMatchingSubmoduleSubscription
parameter_list|(
name|String
name|submodule
parameter_list|,
name|String
name|subBranch
parameter_list|,
name|String
name|superproject
parameter_list|,
name|String
name|superBranch
parameter_list|)
throws|throws
name|Exception
block|{
name|allowSubmoduleSubscription
argument_list|(
name|submodule
argument_list|,
name|subBranch
argument_list|,
name|superproject
argument_list|,
name|superBranch
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|createSubmoduleSubscription (TestRepository<?> repo, String branch, String subscribeToRepo, String subscribeToBranch)
specifier|protected
name|void
name|createSubmoduleSubscription
parameter_list|(
name|TestRepository
argument_list|<
name|?
argument_list|>
name|repo
parameter_list|,
name|String
name|branch
parameter_list|,
name|String
name|subscribeToRepo
parameter_list|,
name|String
name|subscribeToBranch
parameter_list|)
throws|throws
name|Exception
block|{
name|Config
name|config
init|=
operator|new
name|Config
argument_list|()
decl_stmt|;
name|prepareSubmoduleConfigEntry
argument_list|(
name|config
argument_list|,
name|subscribeToRepo
argument_list|,
name|subscribeToBranch
argument_list|)
expr_stmt|;
name|pushSubmoduleConfig
argument_list|(
name|repo
argument_list|,
name|branch
argument_list|,
name|config
argument_list|)
expr_stmt|;
block|}
DECL|method|createRelativeSubmoduleSubscription (TestRepository<?> repo, String branch, String subscribeToRepoPrefix, String subscribeToRepo, String subscribeToBranch)
specifier|protected
name|void
name|createRelativeSubmoduleSubscription
parameter_list|(
name|TestRepository
argument_list|<
name|?
argument_list|>
name|repo
parameter_list|,
name|String
name|branch
parameter_list|,
name|String
name|subscribeToRepoPrefix
parameter_list|,
name|String
name|subscribeToRepo
parameter_list|,
name|String
name|subscribeToBranch
parameter_list|)
throws|throws
name|Exception
block|{
name|Config
name|config
init|=
operator|new
name|Config
argument_list|()
decl_stmt|;
name|prepareRelativeSubmoduleConfigEntry
argument_list|(
name|config
argument_list|,
name|subscribeToRepoPrefix
argument_list|,
name|subscribeToRepo
argument_list|,
name|subscribeToBranch
argument_list|)
expr_stmt|;
name|pushSubmoduleConfig
argument_list|(
name|repo
argument_list|,
name|branch
argument_list|,
name|config
argument_list|)
expr_stmt|;
block|}
DECL|method|prepareRelativeSubmoduleConfigEntry (Config config, String subscribeToRepoPrefix, String subscribeToRepo, String subscribeToBranch)
specifier|protected
name|void
name|prepareRelativeSubmoduleConfigEntry
parameter_list|(
name|Config
name|config
parameter_list|,
name|String
name|subscribeToRepoPrefix
parameter_list|,
name|String
name|subscribeToRepo
parameter_list|,
name|String
name|subscribeToBranch
parameter_list|)
block|{
name|subscribeToRepo
operator|=
name|name
argument_list|(
name|subscribeToRepo
argument_list|)
expr_stmt|;
name|String
name|url
init|=
name|subscribeToRepoPrefix
operator|+
name|subscribeToRepo
decl_stmt|;
name|config
operator|.
name|setString
argument_list|(
literal|"submodule"
argument_list|,
name|subscribeToRepo
argument_list|,
literal|"path"
argument_list|,
name|subscribeToRepo
argument_list|)
expr_stmt|;
name|config
operator|.
name|setString
argument_list|(
literal|"submodule"
argument_list|,
name|subscribeToRepo
argument_list|,
literal|"url"
argument_list|,
name|url
argument_list|)
expr_stmt|;
if|if
condition|(
name|subscribeToBranch
operator|!=
literal|null
condition|)
block|{
name|config
operator|.
name|setString
argument_list|(
literal|"submodule"
argument_list|,
name|subscribeToRepo
argument_list|,
literal|"branch"
argument_list|,
name|subscribeToBranch
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|prepareSubmoduleConfigEntry (Config config, String subscribeToRepo, String subscribeToBranch)
specifier|protected
name|void
name|prepareSubmoduleConfigEntry
parameter_list|(
name|Config
name|config
parameter_list|,
name|String
name|subscribeToRepo
parameter_list|,
name|String
name|subscribeToBranch
parameter_list|)
block|{
comment|// The submodule subscription module checks for gerrit.canonicalWebUrl to
comment|// detect if it's configured for automatic updates. It doesn't matter if
comment|// it serves from that URL.
name|prepareSubmoduleConfigEntry
argument_list|(
name|config
argument_list|,
name|subscribeToRepo
argument_list|,
name|subscribeToRepo
argument_list|,
name|subscribeToBranch
argument_list|)
expr_stmt|;
block|}
DECL|method|prepareSubmoduleConfigEntry (Config config, String subscribeToRepo, String subscribeToRepoPath, String subscribeToBranch)
specifier|protected
name|void
name|prepareSubmoduleConfigEntry
parameter_list|(
name|Config
name|config
parameter_list|,
name|String
name|subscribeToRepo
parameter_list|,
name|String
name|subscribeToRepoPath
parameter_list|,
name|String
name|subscribeToBranch
parameter_list|)
block|{
name|subscribeToRepo
operator|=
name|name
argument_list|(
name|subscribeToRepo
argument_list|)
expr_stmt|;
name|subscribeToRepoPath
operator|=
name|name
argument_list|(
name|subscribeToRepoPath
argument_list|)
expr_stmt|;
comment|// The submodule subscription module checks for gerrit.canonicalWebUrl to
comment|// detect if it's configured for automatic updates. It doesn't matter if
comment|// it serves from that URL.
name|String
name|url
init|=
name|cfg
operator|.
name|getString
argument_list|(
literal|"gerrit"
argument_list|,
literal|null
argument_list|,
literal|"canonicalWebUrl"
argument_list|)
operator|+
literal|"/"
operator|+
name|subscribeToRepo
decl_stmt|;
name|config
operator|.
name|setString
argument_list|(
literal|"submodule"
argument_list|,
name|subscribeToRepoPath
argument_list|,
literal|"path"
argument_list|,
name|subscribeToRepoPath
argument_list|)
expr_stmt|;
name|config
operator|.
name|setString
argument_list|(
literal|"submodule"
argument_list|,
name|subscribeToRepoPath
argument_list|,
literal|"url"
argument_list|,
name|url
argument_list|)
expr_stmt|;
if|if
condition|(
name|subscribeToBranch
operator|!=
literal|null
condition|)
block|{
name|config
operator|.
name|setString
argument_list|(
literal|"submodule"
argument_list|,
name|subscribeToRepoPath
argument_list|,
literal|"branch"
argument_list|,
name|subscribeToBranch
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|pushSubmoduleConfig (TestRepository<?> repo, String branch, Config config)
specifier|protected
name|void
name|pushSubmoduleConfig
parameter_list|(
name|TestRepository
argument_list|<
name|?
argument_list|>
name|repo
parameter_list|,
name|String
name|branch
parameter_list|,
name|Config
name|config
parameter_list|)
throws|throws
name|Exception
block|{
name|repo
operator|.
name|branch
argument_list|(
literal|"HEAD"
argument_list|)
operator|.
name|commit
argument_list|()
operator|.
name|insertChangeId
argument_list|()
operator|.
name|message
argument_list|(
literal|"subject: adding new subscription"
argument_list|)
operator|.
name|add
argument_list|(
literal|".gitmodules"
argument_list|,
name|config
operator|.
name|toText
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|create
argument_list|()
expr_stmt|;
name|repo
operator|.
name|git
argument_list|()
operator|.
name|push
argument_list|()
operator|.
name|setRemote
argument_list|(
literal|"origin"
argument_list|)
operator|.
name|setRefSpecs
argument_list|(
operator|new
name|RefSpec
argument_list|(
literal|"HEAD:refs/heads/"
operator|+
name|branch
argument_list|)
argument_list|)
operator|.
name|call
argument_list|()
expr_stmt|;
block|}
DECL|method|expectToHaveSubmoduleState (TestRepository<?> repo, String branch, String submodule, TestRepository<?> subRepo, String subBranch)
specifier|protected
name|void
name|expectToHaveSubmoduleState
parameter_list|(
name|TestRepository
argument_list|<
name|?
argument_list|>
name|repo
parameter_list|,
name|String
name|branch
parameter_list|,
name|String
name|submodule
parameter_list|,
name|TestRepository
argument_list|<
name|?
argument_list|>
name|subRepo
parameter_list|,
name|String
name|subBranch
parameter_list|)
throws|throws
name|Exception
block|{
name|submodule
operator|=
name|name
argument_list|(
name|submodule
argument_list|)
expr_stmt|;
name|ObjectId
name|commitId
init|=
name|repo
operator|.
name|git
argument_list|()
operator|.
name|fetch
argument_list|()
operator|.
name|setRemote
argument_list|(
literal|"origin"
argument_list|)
operator|.
name|call
argument_list|()
operator|.
name|getAdvertisedRef
argument_list|(
literal|"refs/heads/"
operator|+
name|branch
argument_list|)
operator|.
name|getObjectId
argument_list|()
decl_stmt|;
name|ObjectId
name|subHead
init|=
name|subRepo
operator|.
name|git
argument_list|()
operator|.
name|fetch
argument_list|()
operator|.
name|setRemote
argument_list|(
literal|"origin"
argument_list|)
operator|.
name|call
argument_list|()
operator|.
name|getAdvertisedRef
argument_list|(
literal|"refs/heads/"
operator|+
name|subBranch
argument_list|)
operator|.
name|getObjectId
argument_list|()
decl_stmt|;
name|RevWalk
name|rw
init|=
name|repo
operator|.
name|getRevWalk
argument_list|()
decl_stmt|;
name|RevCommit
name|c
init|=
name|rw
operator|.
name|parseCommit
argument_list|(
name|commitId
argument_list|)
decl_stmt|;
name|rw
operator|.
name|parseBody
argument_list|(
name|c
operator|.
name|getTree
argument_list|()
argument_list|)
expr_stmt|;
name|RevTree
name|tree
init|=
name|c
operator|.
name|getTree
argument_list|()
decl_stmt|;
name|RevObject
name|actualId
init|=
name|repo
operator|.
name|get
argument_list|(
name|tree
argument_list|,
name|submodule
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|actualId
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|subHead
argument_list|)
expr_stmt|;
block|}
DECL|method|expectToHaveSubmoduleState (TestRepository<?> repo, String branch, String submodule, ObjectId expectedId)
specifier|protected
name|void
name|expectToHaveSubmoduleState
parameter_list|(
name|TestRepository
argument_list|<
name|?
argument_list|>
name|repo
parameter_list|,
name|String
name|branch
parameter_list|,
name|String
name|submodule
parameter_list|,
name|ObjectId
name|expectedId
parameter_list|)
throws|throws
name|Exception
block|{
name|submodule
operator|=
name|name
argument_list|(
name|submodule
argument_list|)
expr_stmt|;
name|ObjectId
name|commitId
init|=
name|repo
operator|.
name|git
argument_list|()
operator|.
name|fetch
argument_list|()
operator|.
name|setRemote
argument_list|(
literal|"origin"
argument_list|)
operator|.
name|call
argument_list|()
operator|.
name|getAdvertisedRef
argument_list|(
literal|"refs/heads/"
operator|+
name|branch
argument_list|)
operator|.
name|getObjectId
argument_list|()
decl_stmt|;
name|RevWalk
name|rw
init|=
name|repo
operator|.
name|getRevWalk
argument_list|()
decl_stmt|;
name|RevCommit
name|c
init|=
name|rw
operator|.
name|parseCommit
argument_list|(
name|commitId
argument_list|)
decl_stmt|;
name|rw
operator|.
name|parseBody
argument_list|(
name|c
operator|.
name|getTree
argument_list|()
argument_list|)
expr_stmt|;
name|RevTree
name|tree
init|=
name|c
operator|.
name|getTree
argument_list|()
decl_stmt|;
name|RevObject
name|actualId
init|=
name|repo
operator|.
name|get
argument_list|(
name|tree
argument_list|,
name|submodule
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|actualId
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|expectedId
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteAllSubscriptions (TestRepository<?> repo, String branch)
specifier|protected
name|void
name|deleteAllSubscriptions
parameter_list|(
name|TestRepository
argument_list|<
name|?
argument_list|>
name|repo
parameter_list|,
name|String
name|branch
parameter_list|)
throws|throws
name|Exception
block|{
name|repo
operator|.
name|git
argument_list|()
operator|.
name|fetch
argument_list|()
operator|.
name|setRemote
argument_list|(
literal|"origin"
argument_list|)
operator|.
name|call
argument_list|()
expr_stmt|;
name|repo
operator|.
name|reset
argument_list|(
literal|"refs/remotes/origin/"
operator|+
name|branch
argument_list|)
expr_stmt|;
name|ObjectId
name|expectedId
init|=
name|repo
operator|.
name|branch
argument_list|(
literal|"HEAD"
argument_list|)
operator|.
name|commit
argument_list|()
operator|.
name|insertChangeId
argument_list|()
operator|.
name|message
argument_list|(
literal|"delete contents in .gitmodules"
argument_list|)
operator|.
name|add
argument_list|(
literal|".gitmodules"
argument_list|,
literal|""
argument_list|)
comment|// Just remove the contents of the file!
operator|.
name|create
argument_list|()
decl_stmt|;
name|repo
operator|.
name|git
argument_list|()
operator|.
name|push
argument_list|()
operator|.
name|setRemote
argument_list|(
literal|"origin"
argument_list|)
operator|.
name|setRefSpecs
argument_list|(
operator|new
name|RefSpec
argument_list|(
literal|"HEAD:refs/heads/"
operator|+
name|branch
argument_list|)
argument_list|)
operator|.
name|call
argument_list|()
expr_stmt|;
name|ObjectId
name|actualId
init|=
name|repo
operator|.
name|git
argument_list|()
operator|.
name|fetch
argument_list|()
operator|.
name|setRemote
argument_list|(
literal|"origin"
argument_list|)
operator|.
name|call
argument_list|()
operator|.
name|getAdvertisedRef
argument_list|(
literal|"refs/heads/master"
argument_list|)
operator|.
name|getObjectId
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|actualId
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|expectedId
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteGitModulesFile (TestRepository<?> repo, String branch)
specifier|protected
name|void
name|deleteGitModulesFile
parameter_list|(
name|TestRepository
argument_list|<
name|?
argument_list|>
name|repo
parameter_list|,
name|String
name|branch
parameter_list|)
throws|throws
name|Exception
block|{
name|repo
operator|.
name|git
argument_list|()
operator|.
name|fetch
argument_list|()
operator|.
name|setRemote
argument_list|(
literal|"origin"
argument_list|)
operator|.
name|call
argument_list|()
expr_stmt|;
name|repo
operator|.
name|reset
argument_list|(
literal|"refs/remotes/origin/"
operator|+
name|branch
argument_list|)
expr_stmt|;
name|ObjectId
name|expectedId
init|=
name|repo
operator|.
name|branch
argument_list|(
literal|"HEAD"
argument_list|)
operator|.
name|commit
argument_list|()
operator|.
name|insertChangeId
argument_list|()
operator|.
name|message
argument_list|(
literal|"delete .gitmodules"
argument_list|)
operator|.
name|rm
argument_list|(
literal|".gitmodules"
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|repo
operator|.
name|git
argument_list|()
operator|.
name|push
argument_list|()
operator|.
name|setRemote
argument_list|(
literal|"origin"
argument_list|)
operator|.
name|setRefSpecs
argument_list|(
operator|new
name|RefSpec
argument_list|(
literal|"HEAD:refs/heads/"
operator|+
name|branch
argument_list|)
argument_list|)
operator|.
name|call
argument_list|()
expr_stmt|;
name|ObjectId
name|actualId
init|=
name|repo
operator|.
name|git
argument_list|()
operator|.
name|fetch
argument_list|()
operator|.
name|setRemote
argument_list|(
literal|"origin"
argument_list|)
operator|.
name|call
argument_list|()
operator|.
name|getAdvertisedRef
argument_list|(
literal|"refs/heads/master"
argument_list|)
operator|.
name|getObjectId
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|actualId
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|expectedId
argument_list|)
expr_stmt|;
block|}
DECL|method|hasSubmodule (TestRepository<?> repo, String branch, String submodule)
specifier|protected
name|boolean
name|hasSubmodule
parameter_list|(
name|TestRepository
argument_list|<
name|?
argument_list|>
name|repo
parameter_list|,
name|String
name|branch
parameter_list|,
name|String
name|submodule
parameter_list|)
throws|throws
name|Exception
block|{
name|submodule
operator|=
name|name
argument_list|(
name|submodule
argument_list|)
expr_stmt|;
name|Ref
name|branchTip
init|=
name|repo
operator|.
name|git
argument_list|()
operator|.
name|fetch
argument_list|()
operator|.
name|setRemote
argument_list|(
literal|"origin"
argument_list|)
operator|.
name|call
argument_list|()
operator|.
name|getAdvertisedRef
argument_list|(
literal|"refs/heads/"
operator|+
name|branch
argument_list|)
decl_stmt|;
if|if
condition|(
name|branchTip
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ObjectId
name|commitId
init|=
name|branchTip
operator|.
name|getObjectId
argument_list|()
decl_stmt|;
name|RevWalk
name|rw
init|=
name|repo
operator|.
name|getRevWalk
argument_list|()
decl_stmt|;
name|RevCommit
name|c
init|=
name|rw
operator|.
name|parseCommit
argument_list|(
name|commitId
argument_list|)
decl_stmt|;
name|rw
operator|.
name|parseBody
argument_list|(
name|c
operator|.
name|getTree
argument_list|()
argument_list|)
expr_stmt|;
name|RevTree
name|tree
init|=
name|c
operator|.
name|getTree
argument_list|()
decl_stmt|;
try|try
block|{
name|repo
operator|.
name|get
argument_list|(
name|tree
argument_list|,
name|submodule
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|AssertionError
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
DECL|method|expectToHaveCommitMessage (TestRepository<?> repo, String branch, String expectedMessage)
specifier|protected
name|void
name|expectToHaveCommitMessage
parameter_list|(
name|TestRepository
argument_list|<
name|?
argument_list|>
name|repo
parameter_list|,
name|String
name|branch
parameter_list|,
name|String
name|expectedMessage
parameter_list|)
throws|throws
name|Exception
block|{
name|ObjectId
name|commitId
init|=
name|repo
operator|.
name|git
argument_list|()
operator|.
name|fetch
argument_list|()
operator|.
name|setRemote
argument_list|(
literal|"origin"
argument_list|)
operator|.
name|call
argument_list|()
operator|.
name|getAdvertisedRef
argument_list|(
literal|"refs/heads/"
operator|+
name|branch
argument_list|)
operator|.
name|getObjectId
argument_list|()
decl_stmt|;
name|RevWalk
name|rw
init|=
name|repo
operator|.
name|getRevWalk
argument_list|()
decl_stmt|;
name|RevCommit
name|c
init|=
name|rw
operator|.
name|parseCommit
argument_list|(
name|commitId
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|getFullMessage
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|expectedMessage
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

