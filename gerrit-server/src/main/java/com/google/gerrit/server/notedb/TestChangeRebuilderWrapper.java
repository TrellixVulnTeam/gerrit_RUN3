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
name|notedb
operator|.
name|NoteDbUpdateManager
operator|.
name|Result
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
name|rebuild
operator|.
name|ChangeRebuilder
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
name|rebuild
operator|.
name|ChangeRebuilderImpl
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
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_class
annotation|@
name|VisibleForTesting
annotation|@
name|Singleton
DECL|class|TestChangeRebuilderWrapper
specifier|public
class|class
name|TestChangeRebuilderWrapper
extends|extends
name|ChangeRebuilder
block|{
DECL|field|delegate
specifier|private
specifier|final
name|ChangeRebuilderImpl
name|delegate
decl_stmt|;
DECL|field|failNextUpdate
specifier|private
specifier|final
name|AtomicBoolean
name|failNextUpdate
decl_stmt|;
DECL|field|stealNextUpdate
specifier|private
specifier|final
name|AtomicBoolean
name|stealNextUpdate
decl_stmt|;
annotation|@
name|Inject
DECL|method|TestChangeRebuilderWrapper (SchemaFactory<ReviewDb> schemaFactory, ChangeRebuilderImpl rebuilder)
name|TestChangeRebuilderWrapper
parameter_list|(
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|schemaFactory
parameter_list|,
name|ChangeRebuilderImpl
name|rebuilder
parameter_list|)
block|{
name|super
argument_list|(
name|schemaFactory
argument_list|)
expr_stmt|;
name|this
operator|.
name|delegate
operator|=
name|rebuilder
expr_stmt|;
name|this
operator|.
name|failNextUpdate
operator|=
operator|new
name|AtomicBoolean
argument_list|()
expr_stmt|;
name|this
operator|.
name|stealNextUpdate
operator|=
operator|new
name|AtomicBoolean
argument_list|()
expr_stmt|;
block|}
DECL|method|failNextUpdate ()
specifier|public
name|void
name|failNextUpdate
parameter_list|()
block|{
name|failNextUpdate
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|stealNextUpdate ()
specifier|public
name|void
name|stealNextUpdate
parameter_list|()
block|{
name|stealNextUpdate
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|rebuild (ReviewDb db, Change.Id changeId)
specifier|public
name|Result
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
name|IOException
throws|,
name|OrmException
block|{
if|if
condition|(
name|failNextUpdate
operator|.
name|getAndSet
argument_list|(
literal|false
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Update failed"
argument_list|)
throw|;
block|}
name|Result
name|result
init|=
name|delegate
operator|.
name|rebuild
argument_list|(
name|db
argument_list|,
name|changeId
argument_list|)
decl_stmt|;
if|if
condition|(
name|stealNextUpdate
operator|.
name|getAndSet
argument_list|(
literal|false
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Update stolen"
argument_list|)
throw|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|rebuild (NoteDbUpdateManager manager, ChangeBundle bundle)
specifier|public
name|Result
name|rebuild
parameter_list|(
name|NoteDbUpdateManager
name|manager
parameter_list|,
name|ChangeBundle
name|bundle
parameter_list|)
throws|throws
name|IOException
throws|,
name|OrmException
block|{
comment|// stealNextUpdate doesn't really apply in this case because the IOException
comment|// would normally come from the manager.execute() method, which isn't called
comment|// here.
return|return
name|delegate
operator|.
name|rebuild
argument_list|(
name|manager
argument_list|,
name|bundle
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|stage (ReviewDb db, Change.Id changeId)
specifier|public
name|NoteDbUpdateManager
name|stage
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
name|IOException
throws|,
name|OrmException
block|{
comment|// Don't inspect stealNextUpdate; that happens in execute() below.
return|return
name|delegate
operator|.
name|stage
argument_list|(
name|db
argument_list|,
name|changeId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|execute (ReviewDb db, Change.Id changeId, NoteDbUpdateManager manager)
specifier|public
name|Result
name|execute
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|Change
operator|.
name|Id
name|changeId
parameter_list|,
name|NoteDbUpdateManager
name|manager
parameter_list|)
throws|throws
name|OrmException
throws|,
name|IOException
block|{
if|if
condition|(
name|failNextUpdate
operator|.
name|getAndSet
argument_list|(
literal|false
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Update failed"
argument_list|)
throw|;
block|}
name|Result
name|result
init|=
name|delegate
operator|.
name|execute
argument_list|(
name|db
argument_list|,
name|changeId
argument_list|,
name|manager
argument_list|)
decl_stmt|;
if|if
condition|(
name|stealNextUpdate
operator|.
name|getAndSet
argument_list|(
literal|false
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Update stolen"
argument_list|)
throw|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|buildUpdates (NoteDbUpdateManager manager, ChangeBundle bundle)
specifier|public
name|void
name|buildUpdates
parameter_list|(
name|NoteDbUpdateManager
name|manager
parameter_list|,
name|ChangeBundle
name|bundle
parameter_list|)
throws|throws
name|IOException
throws|,
name|OrmException
block|{
comment|// Don't check for manual failure; that happens in execute().
name|delegate
operator|.
name|buildUpdates
argument_list|(
name|manager
argument_list|,
name|bundle
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

