begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2013 The Android Open Source Project
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
DECL|package|com.google.gerrit.acceptance
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
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
name|Optional
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
name|common
operator|.
name|FooterConstants
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
name|jcraft
operator|.
name|jsch
operator|.
name|JSch
import|;
end_import

begin_import
import|import
name|com
operator|.
name|jcraft
operator|.
name|jsch
operator|.
name|JSchException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|jcraft
operator|.
name|jsch
operator|.
name|Session
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
name|api
operator|.
name|FetchCommand
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
name|api
operator|.
name|Git
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
name|api
operator|.
name|PushCommand
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
name|api
operator|.
name|errors
operator|.
name|GitAPIException
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
name|internal
operator|.
name|storage
operator|.
name|dfs
operator|.
name|DfsRepositoryDescription
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
name|internal
operator|.
name|storage
operator|.
name|dfs
operator|.
name|InMemoryRepository
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
name|transport
operator|.
name|FetchResult
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
name|JschConfigSessionFactory
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
name|OpenSshConfig
operator|.
name|Host
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
name|SshSessionFactory
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
name|util
operator|.
name|FS
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_class
DECL|class|GitUtil
specifier|public
class|class
name|GitUtil
block|{
DECL|method|initSsh (final TestAccount a)
specifier|public
specifier|static
name|void
name|initSsh
parameter_list|(
specifier|final
name|TestAccount
name|a
parameter_list|)
block|{
specifier|final
name|Properties
name|config
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|config
operator|.
name|put
argument_list|(
literal|"StrictHostKeyChecking"
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|JSch
operator|.
name|setConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
comment|// register a JschConfigSessionFactory that adds the private key as identity
comment|// to the JSch instance of JGit so that SSH communication via JGit can
comment|// succeed
name|SshSessionFactory
operator|.
name|setInstance
argument_list|(
operator|new
name|JschConfigSessionFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|configure
parameter_list|(
name|Host
name|hc
parameter_list|,
name|Session
name|session
parameter_list|)
block|{
try|try
block|{
specifier|final
name|JSch
name|jsch
init|=
name|getJSch
argument_list|(
name|hc
argument_list|,
name|FS
operator|.
name|DETECTED
argument_list|)
decl_stmt|;
name|jsch
operator|.
name|addIdentity
argument_list|(
literal|"KeyPair"
argument_list|,
name|a
operator|.
name|privateKey
argument_list|()
argument_list|,
name|a
operator|.
name|sshKey
operator|.
name|getPublicKeyBlob
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JSchException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|cloneProject ( Project.NameKey project, String uri)
specifier|public
specifier|static
name|TestRepository
argument_list|<
name|InMemoryRepository
argument_list|>
name|cloneProject
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
name|String
name|uri
parameter_list|)
throws|throws
name|Exception
block|{
name|InMemoryRepository
name|dest
init|=
operator|new
name|InMemoryRepository
operator|.
name|Builder
argument_list|()
operator|.
name|setRepositoryDescription
argument_list|(
operator|new
name|DfsRepositoryDescription
argument_list|(
name|project
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
comment|// SshTransport depends on a real FS to read ~/.ssh/config, but
comment|// InMemoryRepository by default uses a null FS.
comment|// TODO(dborowitz): Remove when we no longer depend on SSH.
operator|.
name|setFS
argument_list|(
name|FS
operator|.
name|detect
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Config
name|cfg
init|=
name|dest
operator|.
name|getConfig
argument_list|()
decl_stmt|;
name|cfg
operator|.
name|setString
argument_list|(
literal|"remote"
argument_list|,
literal|"origin"
argument_list|,
literal|"url"
argument_list|,
name|uri
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|setString
argument_list|(
literal|"remote"
argument_list|,
literal|"origin"
argument_list|,
literal|"fetch"
argument_list|,
literal|"+refs/heads/*:refs/remotes/origin/*"
argument_list|)
expr_stmt|;
name|TestRepository
argument_list|<
name|InMemoryRepository
argument_list|>
name|testRepo
init|=
operator|new
name|TestRepository
argument_list|<>
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|FetchResult
name|result
init|=
name|testRepo
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
decl_stmt|;
name|String
name|originMaster
init|=
literal|"refs/remotes/origin/master"
decl_stmt|;
if|if
condition|(
name|result
operator|.
name|getTrackingRefUpdate
argument_list|(
name|originMaster
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|testRepo
operator|.
name|reset
argument_list|(
name|originMaster
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|TestRepository
argument_list|<>
argument_list|(
name|dest
argument_list|)
return|;
block|}
DECL|method|cloneProject ( Project.NameKey project, SshSession sshSession)
specifier|public
specifier|static
name|TestRepository
argument_list|<
name|InMemoryRepository
argument_list|>
name|cloneProject
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
name|SshSession
name|sshSession
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|cloneProject
argument_list|(
name|project
argument_list|,
name|sshSession
operator|.
name|getUrl
argument_list|()
operator|+
literal|"/"
operator|+
name|project
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
DECL|method|fetch (Git git, String spec)
specifier|public
specifier|static
name|void
name|fetch
parameter_list|(
name|Git
name|git
parameter_list|,
name|String
name|spec
parameter_list|)
throws|throws
name|GitAPIException
block|{
name|FetchCommand
name|fetch
init|=
name|git
operator|.
name|fetch
argument_list|()
decl_stmt|;
name|fetch
operator|.
name|setRefSpecs
argument_list|(
operator|new
name|RefSpec
argument_list|(
name|spec
argument_list|)
argument_list|)
expr_stmt|;
name|fetch
operator|.
name|call
argument_list|()
expr_stmt|;
block|}
DECL|method|pushHead (Git git, String ref, boolean pushTags)
specifier|public
specifier|static
name|PushResult
name|pushHead
parameter_list|(
name|Git
name|git
parameter_list|,
name|String
name|ref
parameter_list|,
name|boolean
name|pushTags
parameter_list|)
throws|throws
name|GitAPIException
block|{
return|return
name|pushHead
argument_list|(
name|git
argument_list|,
name|ref
argument_list|,
name|pushTags
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|pushHead (Git git, String ref, boolean pushTags, boolean force)
specifier|public
specifier|static
name|PushResult
name|pushHead
parameter_list|(
name|Git
name|git
parameter_list|,
name|String
name|ref
parameter_list|,
name|boolean
name|pushTags
parameter_list|,
name|boolean
name|force
parameter_list|)
throws|throws
name|GitAPIException
block|{
name|PushCommand
name|pushCmd
init|=
name|git
operator|.
name|push
argument_list|()
decl_stmt|;
name|pushCmd
operator|.
name|setForce
argument_list|(
name|force
argument_list|)
expr_stmt|;
name|pushCmd
operator|.
name|setRefSpecs
argument_list|(
operator|new
name|RefSpec
argument_list|(
literal|"HEAD:"
operator|+
name|ref
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|pushTags
condition|)
block|{
name|pushCmd
operator|.
name|setPushTags
argument_list|()
expr_stmt|;
block|}
name|Iterable
argument_list|<
name|PushResult
argument_list|>
name|r
init|=
name|pushCmd
operator|.
name|call
argument_list|()
decl_stmt|;
return|return
name|Iterables
operator|.
name|getOnlyElement
argument_list|(
name|r
argument_list|)
return|;
block|}
DECL|class|Commit
specifier|public
specifier|static
class|class
name|Commit
block|{
DECL|field|commit
specifier|private
specifier|final
name|RevCommit
name|commit
decl_stmt|;
DECL|field|changeId
specifier|private
specifier|final
name|String
name|changeId
decl_stmt|;
DECL|method|Commit (RevCommit commit, String changeId)
name|Commit
parameter_list|(
name|RevCommit
name|commit
parameter_list|,
name|String
name|changeId
parameter_list|)
block|{
name|this
operator|.
name|commit
operator|=
name|commit
expr_stmt|;
name|this
operator|.
name|changeId
operator|=
name|changeId
expr_stmt|;
block|}
DECL|method|getCommit ()
specifier|public
name|RevCommit
name|getCommit
parameter_list|()
block|{
return|return
name|commit
return|;
block|}
DECL|method|getChangeId ()
specifier|public
name|String
name|getChangeId
parameter_list|()
block|{
return|return
name|changeId
return|;
block|}
block|}
DECL|method|getChangeId (TestRepository<?> tr, ObjectId id)
specifier|public
specifier|static
name|Optional
argument_list|<
name|String
argument_list|>
name|getChangeId
parameter_list|(
name|TestRepository
argument_list|<
name|?
argument_list|>
name|tr
parameter_list|,
name|ObjectId
name|id
parameter_list|)
throws|throws
name|IOException
block|{
name|RevCommit
name|c
init|=
name|tr
operator|.
name|getRevWalk
argument_list|()
operator|.
name|parseCommit
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|tr
operator|.
name|getRevWalk
argument_list|()
operator|.
name|parseBody
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|ids
init|=
name|c
operator|.
name|getFooterLines
argument_list|(
name|FooterConstants
operator|.
name|CHANGE_ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|ids
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|Optional
operator|.
name|absent
argument_list|()
return|;
block|}
return|return
name|Optional
operator|.
name|of
argument_list|(
name|ids
operator|.
name|get
argument_list|(
name|ids
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

