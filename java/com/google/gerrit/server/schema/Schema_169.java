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
name|annotations
operator|.
name|VisibleForTesting
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
name|flogger
operator|.
name|FluentLogger
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
name|notedb
operator|.
name|CommentJsonMigrator
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
name|CommentJsonMigrator
operator|.
name|ProjectMigrationResult
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
name|MutableNotesMigration
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
name|Provider
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
name|SortedSet
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
name|ProgressMonitor
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
name|TextProgressMonitor
import|;
end_import

begin_comment
comment|/** Migrate NoteDb inline comments to JSON format. */
end_comment

begin_class
DECL|class|Schema_169
specifier|public
class|class
name|Schema_169
extends|extends
name|ReviewDbSchemaVersion
block|{
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|FluentLogger
name|logger
init|=
name|FluentLogger
operator|.
name|forEnclosingClass
argument_list|()
decl_stmt|;
DECL|field|migrator
specifier|private
specifier|final
name|CommentJsonMigrator
name|migrator
decl_stmt|;
DECL|field|repoManager
specifier|private
specifier|final
name|GitRepositoryManager
name|repoManager
decl_stmt|;
DECL|field|notesMigration
specifier|private
specifier|final
name|NotesMigration
name|notesMigration
decl_stmt|;
annotation|@
name|Inject
DECL|method|Schema_169 ( Provider<Schema_168> prior, CommentJsonMigrator migrator, GitRepositoryManager repoManager, @GerritServerConfig Config config)
name|Schema_169
parameter_list|(
name|Provider
argument_list|<
name|Schema_168
argument_list|>
name|prior
parameter_list|,
name|CommentJsonMigrator
name|migrator
parameter_list|,
name|GitRepositoryManager
name|repoManager
parameter_list|,
annotation|@
name|GerritServerConfig
name|Config
name|config
parameter_list|)
block|{
name|super
argument_list|(
name|prior
argument_list|)
expr_stmt|;
name|this
operator|.
name|migrator
operator|=
name|migrator
expr_stmt|;
name|this
operator|.
name|repoManager
operator|=
name|repoManager
expr_stmt|;
name|this
operator|.
name|notesMigration
operator|=
name|MutableNotesMigration
operator|.
name|fromConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|migrateData (ReviewDb db, UpdateUI ui)
specifier|protected
name|void
name|migrateData
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|UpdateUI
name|ui
parameter_list|)
throws|throws
name|OrmException
block|{
name|migrateData
argument_list|(
name|ui
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|migrateData (UpdateUI ui)
specifier|protected
name|void
name|migrateData
parameter_list|(
name|UpdateUI
name|ui
parameter_list|)
throws|throws
name|OrmException
block|{
comment|//  If the migration hasn't started, no need to look for non-JSON
if|if
condition|(
operator|!
name|notesMigration
operator|.
name|commitChangeWrites
argument_list|()
condition|)
block|{
return|return;
block|}
name|boolean
name|ok
init|=
literal|true
decl_stmt|;
name|ProgressMonitor
name|pm
init|=
operator|new
name|TextProgressMonitor
argument_list|()
decl_stmt|;
name|SortedSet
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
name|projects
init|=
name|repoManager
operator|.
name|list
argument_list|()
decl_stmt|;
name|pm
operator|.
name|beginTask
argument_list|(
literal|"Migrating projects"
argument_list|,
name|projects
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|skipped
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Project
operator|.
name|NameKey
name|project
range|:
name|projects
control|)
block|{
try|try
init|(
name|Repository
name|repo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|project
argument_list|)
init|)
block|{
name|ProjectMigrationResult
name|progress
init|=
name|migrator
operator|.
name|migrateProject
argument_list|(
name|project
argument_list|,
name|repo
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|skipped
operator|+=
name|progress
operator|.
name|skipped
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|ok
operator|=
literal|false
expr_stmt|;
name|logger
operator|.
name|atWarning
argument_list|()
operator|.
name|log
argument_list|(
literal|"Error migrating project "
operator|+
name|project
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|pm
operator|.
name|update
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|pm
operator|.
name|endTask
argument_list|()
expr_stmt|;
name|ui
operator|.
name|message
argument_list|(
literal|"Skipped "
operator|+
name|skipped
operator|+
literal|" project"
operator|+
operator|(
name|skipped
operator|==
literal|1
condition|?
literal|""
else|:
literal|"s"
operator|)
operator|+
literal|" with no legacy comments"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|ok
condition|)
block|{
throw|throw
operator|new
name|OrmException
argument_list|(
literal|"Migration failed"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

