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
DECL|package|com.google.gerrit.server.notedb
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|notedb
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
name|ImmutableMultimap
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
name|util
operator|.
name|concurrent
operator|.
name|ListenableFuture
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
name|util
operator|.
name|concurrent
operator|.
name|ListeningExecutorService
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
name|project
operator|.
name|NoSuchChangeException
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
name|gwtorm
operator|.
name|server
operator|.
name|SchemaFactory
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
name|Repository
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
name|concurrent
operator|.
name|Callable
import|;
end_import

begin_class
DECL|class|ChangeRebuilder
specifier|public
specifier|abstract
class|class
name|ChangeRebuilder
block|{
DECL|field|schemaFactory
specifier|private
specifier|final
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|schemaFactory
decl_stmt|;
DECL|method|ChangeRebuilder (SchemaFactory<ReviewDb> schemaFactory)
specifier|protected
name|ChangeRebuilder
parameter_list|(
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|schemaFactory
parameter_list|)
block|{
name|this
operator|.
name|schemaFactory
operator|=
name|schemaFactory
expr_stmt|;
block|}
DECL|method|rebuildAsync ( final Change.Id id, ListeningExecutorService executor)
specifier|public
specifier|final
name|ListenableFuture
argument_list|<
name|NoteDbChangeState
argument_list|>
name|rebuildAsync
parameter_list|(
specifier|final
name|Change
operator|.
name|Id
name|id
parameter_list|,
name|ListeningExecutorService
name|executor
parameter_list|)
block|{
return|return
name|executor
operator|.
name|submit
argument_list|(
operator|new
name|Callable
argument_list|<
name|NoteDbChangeState
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NoteDbChangeState
name|call
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|ReviewDb
name|db
init|=
name|schemaFactory
operator|.
name|open
argument_list|()
init|)
block|{
return|return
name|rebuild
argument_list|(
name|db
argument_list|,
name|id
argument_list|)
return|;
block|}
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|rebuild (ReviewDb db, Change.Id changeId)
specifier|public
specifier|abstract
name|NoteDbChangeState
name|rebuild
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|Change
operator|.
name|Id
name|changeId
parameter_list|)
throws|throws
name|NoSuchChangeException
throws|,
name|IOException
throws|,
name|OrmException
throws|,
name|ConfigInvalidException
function_decl|;
DECL|method|rebuild (NoteDbUpdateManager manager, ChangeBundle bundle)
specifier|public
specifier|abstract
name|NoteDbChangeState
name|rebuild
parameter_list|(
name|NoteDbUpdateManager
name|manager
parameter_list|,
name|ChangeBundle
name|bundle
parameter_list|)
throws|throws
name|NoSuchChangeException
throws|,
name|IOException
throws|,
name|OrmException
throws|,
name|ConfigInvalidException
function_decl|;
DECL|method|rebuildProject (ReviewDb db, ImmutableMultimap<Project.NameKey, Change.Id> allChanges, Project.NameKey project, Repository allUsersRepo)
specifier|public
specifier|abstract
name|boolean
name|rebuildProject
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|ImmutableMultimap
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|,
name|Change
operator|.
name|Id
argument_list|>
name|allChanges
parameter_list|,
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
name|Repository
name|allUsersRepo
parameter_list|)
throws|throws
name|NoSuchChangeException
throws|,
name|IOException
throws|,
name|OrmException
throws|,
name|ConfigInvalidException
function_decl|;
block|}
end_class

end_unit

