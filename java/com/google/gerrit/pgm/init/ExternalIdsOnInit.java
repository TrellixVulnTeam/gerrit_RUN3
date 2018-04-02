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
DECL|package|com.google.gerrit.pgm.init
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|pgm
operator|.
name|init
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
name|pgm
operator|.
name|init
operator|.
name|api
operator|.
name|AllUsersNameOnInitProvider
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
name|pgm
operator|.
name|init
operator|.
name|api
operator|.
name|InitFlags
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
name|GerritPersonIdentProvider
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
name|externalids
operator|.
name|ExternalId
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
name|externalids
operator|.
name|ExternalIdNotes
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
name|SitePaths
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
name|extensions
operator|.
name|events
operator|.
name|GitReferenceUpdated
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
name|meta
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
name|File
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
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|ConfigInvalidException
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
name|file
operator|.
name|FileRepository
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
name|lib
operator|.
name|RepositoryCache
operator|.
name|FileKey
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

begin_class
DECL|class|ExternalIdsOnInit
specifier|public
class|class
name|ExternalIdsOnInit
block|{
DECL|field|flags
specifier|private
specifier|final
name|InitFlags
name|flags
decl_stmt|;
DECL|field|site
specifier|private
specifier|final
name|SitePaths
name|site
decl_stmt|;
DECL|field|allUsers
specifier|private
specifier|final
name|String
name|allUsers
decl_stmt|;
annotation|@
name|Inject
DECL|method|ExternalIdsOnInit (InitFlags flags, SitePaths site, AllUsersNameOnInitProvider allUsers)
specifier|public
name|ExternalIdsOnInit
parameter_list|(
name|InitFlags
name|flags
parameter_list|,
name|SitePaths
name|site
parameter_list|,
name|AllUsersNameOnInitProvider
name|allUsers
parameter_list|)
block|{
name|this
operator|.
name|flags
operator|=
name|flags
expr_stmt|;
name|this
operator|.
name|site
operator|=
name|site
expr_stmt|;
name|this
operator|.
name|allUsers
operator|=
name|allUsers
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
DECL|method|insert (String commitMessage, Collection<ExternalId> extIds)
specifier|public
specifier|synchronized
name|void
name|insert
parameter_list|(
name|String
name|commitMessage
parameter_list|,
name|Collection
argument_list|<
name|ExternalId
argument_list|>
name|extIds
parameter_list|)
throws|throws
name|OrmException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|File
name|path
init|=
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
try|try
init|(
name|Repository
name|allUsersRepo
init|=
operator|new
name|FileRepository
argument_list|(
name|path
argument_list|)
init|)
block|{
name|ExternalIdNotes
name|extIdNotes
init|=
name|ExternalIdNotes
operator|.
name|loadNoCacheUpdate
argument_list|(
name|allUsersRepo
argument_list|)
decl_stmt|;
name|extIdNotes
operator|.
name|insert
argument_list|(
name|extIds
argument_list|)
expr_stmt|;
try|try
init|(
name|MetaDataUpdate
name|metaDataUpdate
init|=
operator|new
name|MetaDataUpdate
argument_list|(
name|GitReferenceUpdated
operator|.
name|DISABLED
argument_list|,
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
name|allUsers
argument_list|)
argument_list|,
name|allUsersRepo
argument_list|)
init|)
block|{
name|PersonIdent
name|serverIdent
init|=
operator|new
name|GerritPersonIdentProvider
argument_list|(
name|flags
operator|.
name|cfg
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|metaDataUpdate
operator|.
name|getCommitBuilder
argument_list|()
operator|.
name|setAuthor
argument_list|(
name|serverIdent
argument_list|)
expr_stmt|;
name|metaDataUpdate
operator|.
name|getCommitBuilder
argument_list|()
operator|.
name|setCommitter
argument_list|(
name|serverIdent
argument_list|)
expr_stmt|;
name|metaDataUpdate
operator|.
name|getCommitBuilder
argument_list|()
operator|.
name|setMessage
argument_list|(
name|commitMessage
argument_list|)
expr_stmt|;
name|extIdNotes
operator|.
name|commit
argument_list|(
name|metaDataUpdate
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|getPath ()
specifier|private
name|File
name|getPath
parameter_list|()
block|{
name|Path
name|basePath
init|=
name|site
operator|.
name|resolve
argument_list|(
name|flags
operator|.
name|cfg
operator|.
name|getString
argument_list|(
literal|"gerrit"
argument_list|,
literal|null
argument_list|,
literal|"basePath"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|basePath
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"gerrit.basePath must be configured"
argument_list|)
throw|;
block|}
return|return
name|FileKey
operator|.
name|resolve
argument_list|(
name|basePath
operator|.
name|resolve
argument_list|(
name|allUsers
argument_list|)
operator|.
name|toFile
argument_list|()
argument_list|,
name|FS
operator|.
name|DETECTED
argument_list|)
return|;
block|}
block|}
end_class

end_unit

