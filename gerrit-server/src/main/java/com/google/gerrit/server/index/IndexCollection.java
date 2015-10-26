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
DECL|package|com.google.gerrit.server.index
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|index
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
name|collect
operator|.
name|Lists
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
name|extensions
operator|.
name|events
operator|.
name|LifecycleListener
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
name|util
operator|.
name|Collection
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CopyOnWriteArrayList
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
name|CountDownLatch
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
name|AtomicReference
import|;
end_import

begin_comment
comment|/** Dynamic pointers to the index versions used for searching and writing. */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|IndexCollection
specifier|public
class|class
name|IndexCollection
implements|implements
name|LifecycleListener
block|{
DECL|field|writeIndexes
specifier|private
specifier|final
name|CopyOnWriteArrayList
argument_list|<
name|ChangeIndex
argument_list|>
name|writeIndexes
decl_stmt|;
DECL|field|searchIndex
specifier|private
specifier|final
name|AtomicReference
argument_list|<
name|ChangeIndex
argument_list|>
name|searchIndex
decl_stmt|;
DECL|field|initLatch
specifier|private
specifier|final
name|CountDownLatch
name|initLatch
decl_stmt|;
annotation|@
name|Inject
annotation|@
name|VisibleForTesting
DECL|method|IndexCollection ()
specifier|public
name|IndexCollection
parameter_list|()
block|{
name|this
operator|.
name|writeIndexes
operator|=
name|Lists
operator|.
name|newCopyOnWriteArrayList
argument_list|()
expr_stmt|;
name|this
operator|.
name|searchIndex
operator|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|initLatch
operator|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/** @return the current search index version. */
DECL|method|getSearchIndex ()
specifier|public
name|ChangeIndex
name|getSearchIndex
parameter_list|()
block|{
try|try
block|{
name|initLatch
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
return|return
name|searchIndex
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|setSearchIndex (ChangeIndex index)
specifier|public
name|void
name|setSearchIndex
parameter_list|(
name|ChangeIndex
name|index
parameter_list|)
block|{
name|ChangeIndex
name|old
init|=
name|searchIndex
operator|.
name|getAndSet
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|initLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
if|if
condition|(
name|old
operator|!=
literal|null
operator|&&
name|old
operator|!=
name|index
operator|&&
operator|!
name|writeIndexes
operator|.
name|contains
argument_list|(
name|old
argument_list|)
condition|)
block|{
name|old
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getWriteIndexes ()
specifier|public
name|Collection
argument_list|<
name|ChangeIndex
argument_list|>
name|getWriteIndexes
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableCollection
argument_list|(
name|writeIndexes
argument_list|)
return|;
block|}
DECL|method|addWriteIndex (ChangeIndex index)
specifier|public
specifier|synchronized
name|ChangeIndex
name|addWriteIndex
parameter_list|(
name|ChangeIndex
name|index
parameter_list|)
block|{
name|int
name|version
init|=
name|index
operator|.
name|getSchema
argument_list|()
operator|.
name|getVersion
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|writeIndexes
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|writeIndexes
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getSchema
argument_list|()
operator|.
name|getVersion
argument_list|()
operator|==
name|version
condition|)
block|{
return|return
name|writeIndexes
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|index
argument_list|)
return|;
block|}
block|}
name|writeIndexes
operator|.
name|add
argument_list|(
name|index
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
DECL|method|removeWriteIndex (int version)
specifier|public
specifier|synchronized
name|void
name|removeWriteIndex
parameter_list|(
name|int
name|version
parameter_list|)
block|{
name|int
name|removeIndex
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|writeIndexes
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|writeIndexes
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getSchema
argument_list|()
operator|.
name|getVersion
argument_list|()
operator|==
name|version
condition|)
block|{
name|removeIndex
operator|=
name|i
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|removeIndex
operator|>=
literal|0
condition|)
block|{
try|try
block|{
name|writeIndexes
operator|.
name|get
argument_list|(
name|removeIndex
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|writeIndexes
operator|.
name|remove
argument_list|(
name|removeIndex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getWriteIndex (int version)
specifier|public
name|ChangeIndex
name|getWriteIndex
parameter_list|(
name|int
name|version
parameter_list|)
block|{
for|for
control|(
name|ChangeIndex
name|i
range|:
name|writeIndexes
control|)
block|{
if|if
condition|(
name|i
operator|.
name|getSchema
argument_list|()
operator|.
name|getVersion
argument_list|()
operator|==
name|version
condition|)
block|{
return|return
name|i
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|ChangeIndex
name|read
init|=
name|searchIndex
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|read
operator|!=
literal|null
condition|)
block|{
name|read
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|ChangeIndex
name|write
range|:
name|writeIndexes
control|)
block|{
if|if
condition|(
name|write
operator|!=
name|read
condition|)
block|{
name|write
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

