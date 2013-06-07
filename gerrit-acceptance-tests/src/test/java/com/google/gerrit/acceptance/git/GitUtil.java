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
name|SshSession
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
name|TempFileUtil
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
name|TestAccount
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
name|AddCommand
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
name|CloneCommand
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
name|CommitCommand
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
name|PersonIdent
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
name|ChangeIdUtil
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
name|BufferedWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileWriter
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
DECL|method|createProject (SshSession s, String name)
specifier|public
specifier|static
name|void
name|createProject
parameter_list|(
name|SshSession
name|s
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|JSchException
throws|,
name|IOException
block|{
name|createProject
argument_list|(
name|s
argument_list|,
name|name
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|createProject (SshSession s, String name, Project.NameKey parent)
specifier|public
specifier|static
name|void
name|createProject
parameter_list|(
name|SshSession
name|s
parameter_list|,
name|String
name|name
parameter_list|,
name|Project
operator|.
name|NameKey
name|parent
parameter_list|)
throws|throws
name|JSchException
throws|,
name|IOException
block|{
name|createProject
argument_list|(
name|s
argument_list|,
name|name
argument_list|,
name|parent
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|createProject (SshSession s, String name, Project.NameKey parent, boolean emptyCommit)
specifier|public
specifier|static
name|void
name|createProject
parameter_list|(
name|SshSession
name|s
parameter_list|,
name|String
name|name
parameter_list|,
name|Project
operator|.
name|NameKey
name|parent
parameter_list|,
name|boolean
name|emptyCommit
parameter_list|)
throws|throws
name|JSchException
throws|,
name|IOException
block|{
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"gerrit create-project"
argument_list|)
expr_stmt|;
if|if
condition|(
name|emptyCommit
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|" --empty-commit"
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
literal|" --name \""
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"\""
argument_list|)
expr_stmt|;
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|" --parent \""
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|parent
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"\""
argument_list|)
expr_stmt|;
block|}
name|s
operator|.
name|exec
argument_list|(
name|b
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|cloneProject (String url)
specifier|public
specifier|static
name|Git
name|cloneProject
parameter_list|(
name|String
name|url
parameter_list|)
throws|throws
name|GitAPIException
throws|,
name|IOException
block|{
specifier|final
name|File
name|gitDir
init|=
name|TempFileUtil
operator|.
name|createTempDirectory
argument_list|()
decl_stmt|;
specifier|final
name|CloneCommand
name|cloneCmd
init|=
name|Git
operator|.
name|cloneRepository
argument_list|()
decl_stmt|;
name|cloneCmd
operator|.
name|setURI
argument_list|(
name|url
argument_list|)
expr_stmt|;
name|cloneCmd
operator|.
name|setDirectory
argument_list|(
name|gitDir
argument_list|)
expr_stmt|;
return|return
name|cloneCmd
operator|.
name|call
argument_list|()
return|;
block|}
DECL|method|add (Git git, String path, String content)
specifier|public
specifier|static
name|void
name|add
parameter_list|(
name|Git
name|git
parameter_list|,
name|String
name|path
parameter_list|,
name|String
name|content
parameter_list|)
throws|throws
name|GitAPIException
throws|,
name|IOException
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|git
operator|.
name|getRepository
argument_list|()
operator|.
name|getDirectory
argument_list|()
operator|.
name|getParentFile
argument_list|()
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|File
name|p
init|=
name|f
operator|.
name|getParentFile
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|p
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|p
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"failed to create dir: "
operator|+
name|p
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
throw|;
block|}
name|FileWriter
name|w
init|=
operator|new
name|FileWriter
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|BufferedWriter
name|out
init|=
operator|new
name|BufferedWriter
argument_list|(
name|w
argument_list|)
decl_stmt|;
try|try
block|{
name|out
operator|.
name|write
argument_list|(
name|content
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|final
name|AddCommand
name|addCmd
init|=
name|git
operator|.
name|add
argument_list|()
decl_stmt|;
name|addCmd
operator|.
name|addFilepattern
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|addCmd
operator|.
name|call
argument_list|()
expr_stmt|;
block|}
DECL|method|createCommit (Git git, PersonIdent i, String msg)
specifier|public
specifier|static
name|String
name|createCommit
parameter_list|(
name|Git
name|git
parameter_list|,
name|PersonIdent
name|i
parameter_list|,
name|String
name|msg
parameter_list|)
throws|throws
name|GitAPIException
throws|,
name|IOException
block|{
return|return
name|createCommit
argument_list|(
name|git
argument_list|,
name|i
argument_list|,
name|msg
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|amendCommit (Git git, PersonIdent i, String msg, String changeId)
specifier|public
specifier|static
name|void
name|amendCommit
parameter_list|(
name|Git
name|git
parameter_list|,
name|PersonIdent
name|i
parameter_list|,
name|String
name|msg
parameter_list|,
name|String
name|changeId
parameter_list|)
throws|throws
name|GitAPIException
throws|,
name|IOException
block|{
name|msg
operator|=
name|ChangeIdUtil
operator|.
name|insertId
argument_list|(
name|msg
argument_list|,
name|ObjectId
operator|.
name|fromString
argument_list|(
name|changeId
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|createCommit
argument_list|(
name|git
argument_list|,
name|i
argument_list|,
name|msg
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|createCommit (Git git, PersonIdent i, String msg, boolean insertChangeId, boolean amend)
specifier|private
specifier|static
name|String
name|createCommit
parameter_list|(
name|Git
name|git
parameter_list|,
name|PersonIdent
name|i
parameter_list|,
name|String
name|msg
parameter_list|,
name|boolean
name|insertChangeId
parameter_list|,
name|boolean
name|amend
parameter_list|)
throws|throws
name|GitAPIException
throws|,
name|IOException
block|{
name|ObjectId
name|changeId
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|insertChangeId
condition|)
block|{
name|changeId
operator|=
name|computeChangeId
argument_list|(
name|git
argument_list|,
name|i
argument_list|,
name|msg
argument_list|)
expr_stmt|;
name|msg
operator|=
name|ChangeIdUtil
operator|.
name|insertId
argument_list|(
name|msg
argument_list|,
name|changeId
argument_list|)
expr_stmt|;
block|}
specifier|final
name|CommitCommand
name|commitCmd
init|=
name|git
operator|.
name|commit
argument_list|()
decl_stmt|;
name|commitCmd
operator|.
name|setAmend
argument_list|(
name|amend
argument_list|)
expr_stmt|;
name|commitCmd
operator|.
name|setAuthor
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|commitCmd
operator|.
name|setCommitter
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|commitCmd
operator|.
name|setMessage
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|commitCmd
operator|.
name|call
argument_list|()
expr_stmt|;
return|return
name|changeId
operator|!=
literal|null
condition|?
literal|"I"
operator|+
name|changeId
operator|.
name|getName
argument_list|()
else|:
literal|null
return|;
block|}
DECL|method|computeChangeId (Git git, PersonIdent i, String msg)
specifier|private
specifier|static
name|ObjectId
name|computeChangeId
parameter_list|(
name|Git
name|git
parameter_list|,
name|PersonIdent
name|i
parameter_list|,
name|String
name|msg
parameter_list|)
throws|throws
name|IOException
block|{
name|RevWalk
name|rw
init|=
operator|new
name|RevWalk
argument_list|(
name|git
operator|.
name|getRepository
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|RevCommit
name|parent
init|=
name|rw
operator|.
name|lookupCommit
argument_list|(
name|git
operator|.
name|getRepository
argument_list|()
operator|.
name|getRef
argument_list|(
name|Constants
operator|.
name|HEAD
argument_list|)
operator|.
name|getObjectId
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|ChangeIdUtil
operator|.
name|computeChangeId
argument_list|(
name|parent
operator|.
name|getTree
argument_list|()
argument_list|,
name|parent
operator|.
name|getId
argument_list|()
argument_list|,
name|i
argument_list|,
name|i
argument_list|,
name|msg
argument_list|)
return|;
block|}
finally|finally
block|{
name|rw
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|pushHead (Git git, String ref)
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
block|}
end_class

end_unit

