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
DECL|package|com.google.gerrit.server.patch
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|patch
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
name|client
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
name|reviewdb
operator|.
name|client
operator|.
name|Change
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
name|PatchSet
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
name|PatchSetInfo
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
name|RevId
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
name|UserIdentity
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
name|server
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
name|server
operator|.
name|account
operator|.
name|AccountByEmailCache
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
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|errors
operator|.
name|MissingObjectException
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
name|sql
operator|.
name|Timestamp
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Factory class creating PatchSetInfo from meta-data found in Git repository.  */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|PatchSetInfoFactory
specifier|public
class|class
name|PatchSetInfoFactory
block|{
DECL|field|repoManager
specifier|private
specifier|final
name|GitRepositoryManager
name|repoManager
decl_stmt|;
DECL|field|byEmailCache
specifier|private
specifier|final
name|AccountByEmailCache
name|byEmailCache
decl_stmt|;
annotation|@
name|Inject
DECL|method|PatchSetInfoFactory (final GitRepositoryManager grm, final AccountByEmailCache byEmailCache)
specifier|public
name|PatchSetInfoFactory
parameter_list|(
specifier|final
name|GitRepositoryManager
name|grm
parameter_list|,
specifier|final
name|AccountByEmailCache
name|byEmailCache
parameter_list|)
block|{
name|this
operator|.
name|repoManager
operator|=
name|grm
expr_stmt|;
name|this
operator|.
name|byEmailCache
operator|=
name|byEmailCache
expr_stmt|;
block|}
DECL|method|get (RevCommit src, PatchSet.Id psi)
specifier|public
name|PatchSetInfo
name|get
parameter_list|(
name|RevCommit
name|src
parameter_list|,
name|PatchSet
operator|.
name|Id
name|psi
parameter_list|)
block|{
name|PatchSetInfo
name|info
init|=
operator|new
name|PatchSetInfo
argument_list|(
name|psi
argument_list|)
decl_stmt|;
name|info
operator|.
name|setSubject
argument_list|(
name|src
operator|.
name|getShortMessage
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|setMessage
argument_list|(
name|src
operator|.
name|getFullMessage
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|setAuthor
argument_list|(
name|toUserIdentity
argument_list|(
name|src
operator|.
name|getAuthorIdent
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setCommitter
argument_list|(
name|toUserIdentity
argument_list|(
name|src
operator|.
name|getCommitterIdent
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setRevId
argument_list|(
name|src
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|info
return|;
block|}
DECL|method|get (ReviewDb db, PatchSet.Id patchSetId)
specifier|public
name|PatchSetInfo
name|get
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|PatchSet
operator|.
name|Id
name|patchSetId
parameter_list|)
throws|throws
name|PatchSetInfoNotAvailableException
block|{
try|try
block|{
specifier|final
name|PatchSet
name|patchSet
init|=
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|get
argument_list|(
name|patchSetId
argument_list|)
decl_stmt|;
specifier|final
name|Change
name|change
init|=
name|db
operator|.
name|changes
argument_list|()
operator|.
name|get
argument_list|(
name|patchSet
operator|.
name|getId
argument_list|()
operator|.
name|getParentKey
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|get
argument_list|(
name|change
argument_list|,
name|patchSet
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|PatchSetInfoNotAvailableException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|get (Change change, PatchSet patchSet)
specifier|public
name|PatchSetInfo
name|get
parameter_list|(
name|Change
name|change
parameter_list|,
name|PatchSet
name|patchSet
parameter_list|)
throws|throws
name|PatchSetInfoNotAvailableException
block|{
name|Repository
name|repo
decl_stmt|;
try|try
block|{
name|repo
operator|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|change
operator|.
name|getProject
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|PatchSetInfoNotAvailableException
argument_list|(
name|e
argument_list|)
throw|;
block|}
try|try
block|{
specifier|final
name|RevWalk
name|rw
init|=
operator|new
name|RevWalk
argument_list|(
name|repo
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|RevCommit
name|src
init|=
name|rw
operator|.
name|parseCommit
argument_list|(
name|ObjectId
operator|.
name|fromString
argument_list|(
name|patchSet
operator|.
name|getRevision
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|PatchSetInfo
name|info
init|=
name|get
argument_list|(
name|src
argument_list|,
name|patchSet
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|info
operator|.
name|setParents
argument_list|(
name|toParentInfos
argument_list|(
name|src
operator|.
name|getParents
argument_list|()
argument_list|,
name|rw
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|info
return|;
block|}
finally|finally
block|{
name|rw
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|PatchSetInfoNotAvailableException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|repo
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|toUserIdentity (final PersonIdent who)
specifier|private
name|UserIdentity
name|toUserIdentity
parameter_list|(
specifier|final
name|PersonIdent
name|who
parameter_list|)
block|{
specifier|final
name|UserIdentity
name|u
init|=
operator|new
name|UserIdentity
argument_list|()
decl_stmt|;
name|u
operator|.
name|setName
argument_list|(
name|who
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|u
operator|.
name|setEmail
argument_list|(
name|who
operator|.
name|getEmailAddress
argument_list|()
argument_list|)
expr_stmt|;
name|u
operator|.
name|setDate
argument_list|(
operator|new
name|Timestamp
argument_list|(
name|who
operator|.
name|getWhen
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|u
operator|.
name|setTimeZone
argument_list|(
name|who
operator|.
name|getTimeZoneOffset
argument_list|()
argument_list|)
expr_stmt|;
comment|// If only one account has access to this email address, select it
comment|// as the identity of the user.
comment|//
specifier|final
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|a
init|=
name|byEmailCache
operator|.
name|get
argument_list|(
name|u
operator|.
name|getEmail
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|u
operator|.
name|setAccount
argument_list|(
name|a
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|u
return|;
block|}
DECL|method|toParentInfos (final RevCommit[] parents, final RevWalk walk)
specifier|private
name|List
argument_list|<
name|PatchSetInfo
operator|.
name|ParentInfo
argument_list|>
name|toParentInfos
parameter_list|(
specifier|final
name|RevCommit
index|[]
name|parents
parameter_list|,
specifier|final
name|RevWalk
name|walk
parameter_list|)
throws|throws
name|IOException
throws|,
name|MissingObjectException
block|{
name|List
argument_list|<
name|PatchSetInfo
operator|.
name|ParentInfo
argument_list|>
name|pInfos
init|=
operator|new
name|ArrayList
argument_list|<
name|PatchSetInfo
operator|.
name|ParentInfo
argument_list|>
argument_list|(
name|parents
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|RevCommit
name|parent
range|:
name|parents
control|)
block|{
name|walk
operator|.
name|parseBody
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|RevId
name|rev
init|=
operator|new
name|RevId
argument_list|(
name|parent
operator|.
name|getId
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|msg
init|=
name|parent
operator|.
name|getShortMessage
argument_list|()
decl_stmt|;
name|pInfos
operator|.
name|add
argument_list|(
operator|new
name|PatchSetInfo
operator|.
name|ParentInfo
argument_list|(
name|rev
argument_list|,
name|msg
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|pInfos
return|;
block|}
block|}
end_class

end_unit

