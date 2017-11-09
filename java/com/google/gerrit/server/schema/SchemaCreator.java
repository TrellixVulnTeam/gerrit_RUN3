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
DECL|package|com.google.gerrit.server.schema
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|schema
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
name|ImmutableList
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
name|TimeUtil
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
name|GroupReference
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
name|metrics
operator|.
name|MetricMaker
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
name|client
operator|.
name|AccountGroup
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
name|AccountGroupName
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
name|CurrentSchemaVersion
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
name|SystemConfig
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
name|GerritPersonIdent
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
name|Sequences
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
name|GroupUUID
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
name|AllProjectsName
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
name|AllUsersName
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
name|config
operator|.
name|SitePath
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
name|group
operator|.
name|InternalGroup
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
name|group
operator|.
name|db
operator|.
name|GroupConfig
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
name|group
operator|.
name|db
operator|.
name|GroupNameNotes
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
name|group
operator|.
name|db
operator|.
name|GroupsUpdate
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
name|group
operator|.
name|db
operator|.
name|InternalGroupCreation
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
name|group
operator|.
name|db
operator|.
name|InternalGroupUpdate
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
name|index
operator|.
name|group
operator|.
name|GroupIndex
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
name|index
operator|.
name|group
operator|.
name|GroupIndexCollection
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
name|notedb
operator|.
name|NotesMigration
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
name|update
operator|.
name|RefUpdateUtil
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
name|jdbc
operator|.
name|JdbcExecutor
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
name|jdbc
operator|.
name|JdbcSchema
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
name|OrmDuplicateKeyException
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
name|Collections
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
name|lib
operator|.
name|BatchRefUpdate
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
name|RevWalk
import|;
end_import

begin_comment
comment|/** Creates the current database schema and populates initial code rows. */
end_comment

begin_class
DECL|class|SchemaCreator
specifier|public
class|class
name|SchemaCreator
block|{
DECL|field|site_path
annotation|@
name|SitePath
specifier|private
specifier|final
name|Path
name|site_path
decl_stmt|;
DECL|field|repoManager
specifier|private
specifier|final
name|GitRepositoryManager
name|repoManager
decl_stmt|;
DECL|field|allProjectsCreator
specifier|private
specifier|final
name|AllProjectsCreator
name|allProjectsCreator
decl_stmt|;
DECL|field|allUsersCreator
specifier|private
specifier|final
name|AllUsersCreator
name|allUsersCreator
decl_stmt|;
DECL|field|allUsersName
specifier|private
specifier|final
name|AllUsersName
name|allUsersName
decl_stmt|;
DECL|field|serverUser
specifier|private
specifier|final
name|PersonIdent
name|serverUser
decl_stmt|;
DECL|field|dataSourceType
specifier|private
specifier|final
name|DataSourceType
name|dataSourceType
decl_stmt|;
DECL|field|indexCollection
specifier|private
specifier|final
name|GroupIndexCollection
name|indexCollection
decl_stmt|;
DECL|field|writeGroupsToNoteDb
specifier|private
specifier|final
name|boolean
name|writeGroupsToNoteDb
decl_stmt|;
DECL|field|config
specifier|private
specifier|final
name|Config
name|config
decl_stmt|;
DECL|field|metricMaker
specifier|private
specifier|final
name|MetricMaker
name|metricMaker
decl_stmt|;
DECL|field|migration
specifier|private
specifier|final
name|NotesMigration
name|migration
decl_stmt|;
DECL|field|allProjectsName
specifier|private
specifier|final
name|AllProjectsName
name|allProjectsName
decl_stmt|;
annotation|@
name|Inject
DECL|method|SchemaCreator ( SitePaths site, GitRepositoryManager repoManager, AllProjectsCreator ap, AllUsersCreator auc, AllUsersName allUsersName, @GerritPersonIdent PersonIdent au, DataSourceType dst, GroupIndexCollection ic, @GerritServerConfig Config config, MetricMaker metricMaker, NotesMigration migration, AllProjectsName apName)
specifier|public
name|SchemaCreator
parameter_list|(
name|SitePaths
name|site
parameter_list|,
name|GitRepositoryManager
name|repoManager
parameter_list|,
name|AllProjectsCreator
name|ap
parameter_list|,
name|AllUsersCreator
name|auc
parameter_list|,
name|AllUsersName
name|allUsersName
parameter_list|,
annotation|@
name|GerritPersonIdent
name|PersonIdent
name|au
parameter_list|,
name|DataSourceType
name|dst
parameter_list|,
name|GroupIndexCollection
name|ic
parameter_list|,
annotation|@
name|GerritServerConfig
name|Config
name|config
parameter_list|,
name|MetricMaker
name|metricMaker
parameter_list|,
name|NotesMigration
name|migration
parameter_list|,
name|AllProjectsName
name|apName
parameter_list|)
block|{
name|this
argument_list|(
name|site
operator|.
name|site_path
argument_list|,
name|repoManager
argument_list|,
name|ap
argument_list|,
name|auc
argument_list|,
name|allUsersName
argument_list|,
name|au
argument_list|,
name|dst
argument_list|,
name|ic
argument_list|,
name|config
argument_list|,
name|metricMaker
argument_list|,
name|migration
argument_list|,
name|apName
argument_list|)
expr_stmt|;
block|}
DECL|method|SchemaCreator ( @itePath Path site, GitRepositoryManager repoManager, AllProjectsCreator ap, AllUsersCreator auc, AllUsersName allUsersName, @GerritPersonIdent PersonIdent au, DataSourceType dst, GroupIndexCollection ic, Config config, MetricMaker metricMaker, NotesMigration migration, AllProjectsName apName)
specifier|public
name|SchemaCreator
parameter_list|(
annotation|@
name|SitePath
name|Path
name|site
parameter_list|,
name|GitRepositoryManager
name|repoManager
parameter_list|,
name|AllProjectsCreator
name|ap
parameter_list|,
name|AllUsersCreator
name|auc
parameter_list|,
name|AllUsersName
name|allUsersName
parameter_list|,
annotation|@
name|GerritPersonIdent
name|PersonIdent
name|au
parameter_list|,
name|DataSourceType
name|dst
parameter_list|,
name|GroupIndexCollection
name|ic
parameter_list|,
name|Config
name|config
parameter_list|,
name|MetricMaker
name|metricMaker
parameter_list|,
name|NotesMigration
name|migration
parameter_list|,
name|AllProjectsName
name|apName
parameter_list|)
block|{
name|site_path
operator|=
name|site
expr_stmt|;
name|this
operator|.
name|repoManager
operator|=
name|repoManager
expr_stmt|;
name|allProjectsCreator
operator|=
name|ap
expr_stmt|;
name|allUsersCreator
operator|=
name|auc
expr_stmt|;
name|this
operator|.
name|allUsersName
operator|=
name|allUsersName
expr_stmt|;
name|serverUser
operator|=
name|au
expr_stmt|;
name|dataSourceType
operator|=
name|dst
expr_stmt|;
name|indexCollection
operator|=
name|ic
expr_stmt|;
comment|// TODO(aliceks): Remove this flag when all other necessary TODOs for writing groups to NoteDb
comment|// have been addressed.
comment|// Don't flip this flag in a production setting! We only added it to spread the implementation
comment|// of groups in NoteDb among several changes which are gradually merged.
name|writeGroupsToNoteDb
operator|=
name|config
operator|.
name|getBoolean
argument_list|(
literal|"user"
argument_list|,
literal|null
argument_list|,
literal|"writeGroupsToNoteDb"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
name|this
operator|.
name|allProjectsName
operator|=
name|apName
expr_stmt|;
name|this
operator|.
name|migration
operator|=
name|migration
expr_stmt|;
name|this
operator|.
name|metricMaker
operator|=
name|metricMaker
expr_stmt|;
block|}
DECL|method|create (ReviewDb db)
specifier|public
name|void
name|create
parameter_list|(
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
specifier|final
name|JdbcSchema
name|jdbc
init|=
operator|(
name|JdbcSchema
operator|)
name|db
decl_stmt|;
try|try
init|(
name|JdbcExecutor
name|e
init|=
operator|new
name|JdbcExecutor
argument_list|(
name|jdbc
argument_list|)
init|)
block|{
name|jdbc
operator|.
name|updateSchema
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
specifier|final
name|CurrentSchemaVersion
name|sVer
init|=
name|CurrentSchemaVersion
operator|.
name|create
argument_list|()
decl_stmt|;
name|sVer
operator|.
name|versionNbr
operator|=
name|SchemaVersion
operator|.
name|getBinaryVersion
argument_list|()
expr_stmt|;
name|db
operator|.
name|schemaVersion
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|sVer
argument_list|)
argument_list|)
expr_stmt|;
name|GroupReference
name|admins
init|=
name|createGroupReference
argument_list|(
literal|"Administrators"
argument_list|)
decl_stmt|;
name|GroupReference
name|batchUsers
init|=
name|createGroupReference
argument_list|(
literal|"Non-Interactive Users"
argument_list|)
decl_stmt|;
name|initSystemConfig
argument_list|(
name|db
argument_list|)
expr_stmt|;
name|allProjectsCreator
operator|.
name|setAdministrators
argument_list|(
name|admins
argument_list|)
operator|.
name|setBatchUsers
argument_list|(
name|batchUsers
argument_list|)
operator|.
name|create
argument_list|()
expr_stmt|;
comment|// We have to create the All-Users repository before we can use it to store the groups in it.
name|allUsersCreator
operator|.
name|setAdministrators
argument_list|(
name|admins
argument_list|)
operator|.
name|create
argument_list|()
expr_stmt|;
comment|// Don't rely on injection to construct Sequences, as it requires ReviewDb.
name|Sequences
name|seqs
init|=
operator|new
name|Sequences
argument_list|(
name|config
argument_list|,
parameter_list|()
lambda|->
name|db
argument_list|,
name|migration
argument_list|,
name|repoManager
argument_list|,
name|GitReferenceUpdated
operator|.
name|DISABLED
argument_list|,
name|allProjectsName
argument_list|,
name|allUsersName
argument_list|,
name|metricMaker
argument_list|)
decl_stmt|;
try|try
init|(
name|Repository
name|allUsersRepo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|allUsersName
argument_list|)
init|)
block|{
name|createAdminsGroup
argument_list|(
name|db
argument_list|,
name|seqs
argument_list|,
name|allUsersRepo
argument_list|,
name|admins
argument_list|)
expr_stmt|;
name|createBatchUsersGroup
argument_list|(
name|db
argument_list|,
name|seqs
argument_list|,
name|allUsersRepo
argument_list|,
name|batchUsers
argument_list|,
name|admins
operator|.
name|getUUID
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|dataSourceType
operator|.
name|getIndexScript
argument_list|()
operator|.
name|run
argument_list|(
name|db
argument_list|)
expr_stmt|;
block|}
DECL|method|createAdminsGroup ( ReviewDb db, Sequences seqs, Repository allUsersRepo, GroupReference groupReference)
specifier|private
name|void
name|createAdminsGroup
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|Sequences
name|seqs
parameter_list|,
name|Repository
name|allUsersRepo
parameter_list|,
name|GroupReference
name|groupReference
parameter_list|)
throws|throws
name|OrmException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|InternalGroupCreation
name|groupCreation
init|=
name|getGroupCreation
argument_list|(
name|seqs
argument_list|,
name|groupReference
argument_list|)
decl_stmt|;
name|InternalGroupUpdate
name|groupUpdate
init|=
name|InternalGroupUpdate
operator|.
name|builder
argument_list|()
operator|.
name|setDescription
argument_list|(
literal|"Gerrit Site Administrators"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|createGroup
argument_list|(
name|db
argument_list|,
name|allUsersRepo
argument_list|,
name|groupCreation
argument_list|,
name|groupUpdate
argument_list|)
expr_stmt|;
block|}
DECL|method|createBatchUsersGroup ( ReviewDb db, Sequences seqs, Repository allUsersRepo, GroupReference groupReference, AccountGroup.UUID adminsGroupUuid)
specifier|private
name|void
name|createBatchUsersGroup
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|Sequences
name|seqs
parameter_list|,
name|Repository
name|allUsersRepo
parameter_list|,
name|GroupReference
name|groupReference
parameter_list|,
name|AccountGroup
operator|.
name|UUID
name|adminsGroupUuid
parameter_list|)
throws|throws
name|OrmException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|InternalGroupCreation
name|groupCreation
init|=
name|getGroupCreation
argument_list|(
name|seqs
argument_list|,
name|groupReference
argument_list|)
decl_stmt|;
name|InternalGroupUpdate
name|groupUpdate
init|=
name|InternalGroupUpdate
operator|.
name|builder
argument_list|()
operator|.
name|setDescription
argument_list|(
literal|"Users who perform batch actions on Gerrit"
argument_list|)
operator|.
name|setOwnerGroupUUID
argument_list|(
name|adminsGroupUuid
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|createGroup
argument_list|(
name|db
argument_list|,
name|allUsersRepo
argument_list|,
name|groupCreation
argument_list|,
name|groupUpdate
argument_list|)
expr_stmt|;
block|}
DECL|method|createGroup ( ReviewDb db, Repository allUsersRepo, InternalGroupCreation groupCreation, InternalGroupUpdate groupUpdate)
specifier|private
name|void
name|createGroup
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|Repository
name|allUsersRepo
parameter_list|,
name|InternalGroupCreation
name|groupCreation
parameter_list|,
name|InternalGroupUpdate
name|groupUpdate
parameter_list|)
throws|throws
name|OrmException
throws|,
name|ConfigInvalidException
throws|,
name|IOException
block|{
name|InternalGroup
name|groupInReviewDb
init|=
name|createGroupInReviewDb
argument_list|(
name|db
argument_list|,
name|groupCreation
argument_list|,
name|groupUpdate
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|writeGroupsToNoteDb
condition|)
block|{
name|index
argument_list|(
name|groupInReviewDb
argument_list|)
expr_stmt|;
return|return;
block|}
name|InternalGroup
name|createdGroup
init|=
name|createGroupInNoteDb
argument_list|(
name|allUsersRepo
argument_list|,
name|groupCreation
argument_list|,
name|groupUpdate
argument_list|)
decl_stmt|;
name|index
argument_list|(
name|createdGroup
argument_list|)
expr_stmt|;
block|}
DECL|method|createGroupInReviewDb ( ReviewDb db, InternalGroupCreation groupCreation, InternalGroupUpdate groupUpdate)
specifier|private
specifier|static
name|InternalGroup
name|createGroupInReviewDb
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|InternalGroupCreation
name|groupCreation
parameter_list|,
name|InternalGroupUpdate
name|groupUpdate
parameter_list|)
throws|throws
name|OrmException
block|{
name|AccountGroup
name|group
init|=
name|GroupsUpdate
operator|.
name|createAccountGroup
argument_list|(
name|groupCreation
argument_list|,
name|groupUpdate
argument_list|)
decl_stmt|;
name|db
operator|.
name|accountGroupNames
argument_list|()
operator|.
name|insert
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
operator|new
name|AccountGroupName
argument_list|(
name|group
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|db
operator|.
name|accountGroups
argument_list|()
operator|.
name|insert
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|group
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|InternalGroup
operator|.
name|create
argument_list|(
name|group
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|()
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|()
argument_list|)
return|;
block|}
DECL|method|createGroupInNoteDb ( Repository allUsersRepo, InternalGroupCreation groupCreation, InternalGroupUpdate groupUpdate)
specifier|private
name|InternalGroup
name|createGroupInNoteDb
parameter_list|(
name|Repository
name|allUsersRepo
parameter_list|,
name|InternalGroupCreation
name|groupCreation
parameter_list|,
name|InternalGroupUpdate
name|groupUpdate
parameter_list|)
throws|throws
name|ConfigInvalidException
throws|,
name|IOException
throws|,
name|OrmDuplicateKeyException
block|{
name|GroupConfig
name|groupConfig
init|=
name|GroupConfig
operator|.
name|createForNewGroup
argument_list|(
name|allUsersRepo
argument_list|,
name|groupCreation
argument_list|)
decl_stmt|;
comment|// We don't add any initial members or subgroups and hence the provided functions should never
comment|// be called. To be on the safe side, we specify some valid functions.
name|groupConfig
operator|.
name|setGroupUpdate
argument_list|(
name|groupUpdate
argument_list|,
name|Account
operator|.
name|Id
operator|::
name|toString
argument_list|,
name|AccountGroup
operator|.
name|UUID
operator|::
name|get
argument_list|)
expr_stmt|;
name|AccountGroup
operator|.
name|NameKey
name|groupName
init|=
name|groupUpdate
operator|.
name|getName
argument_list|()
operator|.
name|orElseGet
argument_list|(
name|groupCreation
operator|::
name|getNameKey
argument_list|)
decl_stmt|;
name|GroupNameNotes
name|groupNameNotes
init|=
name|GroupNameNotes
operator|.
name|loadForNewGroup
argument_list|(
name|allUsersRepo
argument_list|,
name|groupCreation
operator|.
name|getGroupUUID
argument_list|()
argument_list|,
name|groupName
argument_list|)
decl_stmt|;
name|commit
argument_list|(
name|allUsersRepo
argument_list|,
name|groupConfig
argument_list|,
name|groupNameNotes
argument_list|)
expr_stmt|;
return|return
name|groupConfig
operator|.
name|getLoadedGroup
argument_list|()
operator|.
name|orElseThrow
argument_list|(
parameter_list|()
lambda|->
operator|new
name|IllegalStateException
argument_list|(
literal|"Created group wasn't automatically loaded"
argument_list|)
argument_list|)
return|;
block|}
DECL|method|commit ( Repository allUsersRepo, GroupConfig groupConfig, GroupNameNotes groupNameNotes)
specifier|private
name|void
name|commit
parameter_list|(
name|Repository
name|allUsersRepo
parameter_list|,
name|GroupConfig
name|groupConfig
parameter_list|,
name|GroupNameNotes
name|groupNameNotes
parameter_list|)
throws|throws
name|IOException
block|{
name|BatchRefUpdate
name|batchRefUpdate
init|=
name|allUsersRepo
operator|.
name|getRefDatabase
argument_list|()
operator|.
name|newBatchUpdate
argument_list|()
decl_stmt|;
try|try
init|(
name|MetaDataUpdate
name|metaDataUpdate
init|=
name|createMetaDataUpdate
argument_list|(
name|allUsersRepo
argument_list|,
name|batchRefUpdate
argument_list|)
init|)
block|{
name|groupConfig
operator|.
name|commit
argument_list|(
name|metaDataUpdate
argument_list|)
expr_stmt|;
block|}
comment|// MetaDataUpdates unfortunately can't be reused. -> Create a new one.
try|try
init|(
name|MetaDataUpdate
name|metaDataUpdate
init|=
name|createMetaDataUpdate
argument_list|(
name|allUsersRepo
argument_list|,
name|batchRefUpdate
argument_list|)
init|)
block|{
name|groupNameNotes
operator|.
name|commit
argument_list|(
name|metaDataUpdate
argument_list|)
expr_stmt|;
block|}
try|try
init|(
name|RevWalk
name|revWalk
init|=
operator|new
name|RevWalk
argument_list|(
name|allUsersRepo
argument_list|)
init|)
block|{
name|RefUpdateUtil
operator|.
name|executeChecked
argument_list|(
name|batchRefUpdate
argument_list|,
name|revWalk
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createMetaDataUpdate ( Repository allUsersRepo, @Nullable BatchRefUpdate batchRefUpdate)
specifier|private
name|MetaDataUpdate
name|createMetaDataUpdate
parameter_list|(
name|Repository
name|allUsersRepo
parameter_list|,
annotation|@
name|Nullable
name|BatchRefUpdate
name|batchRefUpdate
parameter_list|)
block|{
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
name|allUsersName
argument_list|,
name|allUsersRepo
argument_list|,
name|batchRefUpdate
argument_list|)
decl_stmt|;
name|metaDataUpdate
operator|.
name|getCommitBuilder
argument_list|()
operator|.
name|setAuthor
argument_list|(
name|serverUser
argument_list|)
expr_stmt|;
name|metaDataUpdate
operator|.
name|getCommitBuilder
argument_list|()
operator|.
name|setCommitter
argument_list|(
name|serverUser
argument_list|)
expr_stmt|;
return|return
name|metaDataUpdate
return|;
block|}
DECL|method|index (InternalGroup group)
specifier|private
name|void
name|index
parameter_list|(
name|InternalGroup
name|group
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|GroupIndex
name|groupIndex
range|:
name|indexCollection
operator|.
name|getWriteIndexes
argument_list|()
control|)
block|{
name|groupIndex
operator|.
name|replace
argument_list|(
name|group
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createGroupReference (String name)
specifier|private
name|GroupReference
name|createGroupReference
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|AccountGroup
operator|.
name|UUID
name|groupUuid
init|=
name|GroupUUID
operator|.
name|make
argument_list|(
name|name
argument_list|,
name|serverUser
argument_list|)
decl_stmt|;
return|return
operator|new
name|GroupReference
argument_list|(
name|groupUuid
argument_list|,
name|name
argument_list|)
return|;
block|}
DECL|method|getGroupCreation (Sequences seqs, GroupReference groupReference)
specifier|private
name|InternalGroupCreation
name|getGroupCreation
parameter_list|(
name|Sequences
name|seqs
parameter_list|,
name|GroupReference
name|groupReference
parameter_list|)
throws|throws
name|OrmException
block|{
name|int
name|next
init|=
name|seqs
operator|.
name|nextGroupId
argument_list|()
decl_stmt|;
return|return
name|InternalGroupCreation
operator|.
name|builder
argument_list|()
operator|.
name|setNameKey
argument_list|(
operator|new
name|AccountGroup
operator|.
name|NameKey
argument_list|(
name|groupReference
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setId
argument_list|(
operator|new
name|AccountGroup
operator|.
name|Id
argument_list|(
name|next
argument_list|)
argument_list|)
operator|.
name|setGroupUUID
argument_list|(
name|groupReference
operator|.
name|getUUID
argument_list|()
argument_list|)
operator|.
name|setCreatedOn
argument_list|(
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|initSystemConfig (ReviewDb db)
specifier|private
name|SystemConfig
name|initSystemConfig
parameter_list|(
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
block|{
name|SystemConfig
name|s
init|=
name|SystemConfig
operator|.
name|create
argument_list|()
decl_stmt|;
try|try
block|{
name|s
operator|.
name|sitePath
operator|=
name|site_path
operator|.
name|toRealPath
argument_list|()
operator|.
name|normalize
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|s
operator|.
name|sitePath
operator|=
name|site_path
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|normalize
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|db
operator|.
name|systemConfig
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|s
return|;
block|}
block|}
end_class

end_unit

