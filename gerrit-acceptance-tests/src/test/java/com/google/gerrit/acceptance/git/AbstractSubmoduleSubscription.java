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
name|reviewdb
operator|.
name|client
operator|.
name|Project
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
name|RefSpec
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
name|Project
operator|.
name|NameKey
name|project
init|=
name|createProject
argument_list|(
name|name
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
DECL|method|createSubscription (TestRepository<?> repo, String branch, String subscribeToRepo, String subscribeToBranch)
specifier|protected
name|void
name|createSubscription
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
name|prepareSubscriptionConfigEntry
argument_list|(
name|config
argument_list|,
name|subscribeToRepo
argument_list|,
name|subscribeToBranch
argument_list|)
expr_stmt|;
name|pushSubscriptionConfig
argument_list|(
name|repo
argument_list|,
name|branch
argument_list|,
name|config
argument_list|)
expr_stmt|;
block|}
DECL|method|prepareSubscriptionConfigEntry (Config config, String subscribeToRepo, String subscribeToBranch)
specifier|protected
name|void
name|prepareSubscriptionConfigEntry
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
name|subscribeToRepo
operator|=
name|name
argument_list|(
name|subscribeToRepo
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
DECL|method|pushSubscriptionConfig (TestRepository<?> repo, String branch, Config config)
specifier|protected
name|void
name|pushSubscriptionConfig
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
block|}
end_class

end_unit

