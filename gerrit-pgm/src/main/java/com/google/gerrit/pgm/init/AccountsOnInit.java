begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2017 The Android Open Source Project
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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkArgument
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
name|ImmutableSet
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
name|AccountsUpdate
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
DECL|class|AccountsOnInit
specifier|public
class|class
name|AccountsOnInit
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
DECL|method|AccountsOnInit (InitFlags flags, SitePaths site, AllUsersNameOnInitProvider allUsers)
specifier|public
name|AccountsOnInit
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
DECL|method|insert (ReviewDb db, Account account)
specifier|public
name|void
name|insert
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|Account
name|account
parameter_list|)
throws|throws
name|OrmException
throws|,
name|IOException
block|{
name|db
operator|.
name|accounts
argument_list|()
operator|.
name|insert
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|account
argument_list|)
argument_list|)
expr_stmt|;
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
name|repo
init|=
operator|new
name|FileRepository
argument_list|(
name|path
argument_list|)
init|;           ObjectInserter oi = repo.newObjectInserter()
block|)
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
name|AccountsUpdate
operator|.
name|createUserBranch
argument_list|(
name|repo
argument_list|,
name|oi
argument_list|,
name|serverIdent
argument_list|,
name|serverIdent
argument_list|,
name|account
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_function
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
name|checkArgument
argument_list|(
name|basePath
operator|!=
literal|null
argument_list|,
literal|"gerrit.basePath must be configured"
argument_list|)
expr_stmt|;
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
end_function

unit|}
end_unit

