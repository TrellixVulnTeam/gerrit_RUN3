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
name|util
operator|.
name|concurrent
operator|.
name|ListeningScheduledExecutorService
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
name|CurrentUser
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
name|query
operator|.
name|change
operator|.
name|ChangeData
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
name|util
operator|.
name|RequestContext
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
name|util
operator|.
name|ThreadLocalRequestContext
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
name|OutOfScopeException
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|assistedinject
operator|.
name|Assisted
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
name|assistedinject
operator|.
name|AssistedInject
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
name|util
operator|.
name|Providers
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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

begin_comment
comment|/**  * Helper for (re)indexing a change document.  *<p>  * Indexing is run in the background, as it may require substantial work to  * compute some of the fields and/or update the index.  */
end_comment

begin_class
DECL|class|ChangeIndexerImpl
specifier|public
class|class
name|ChangeIndexerImpl
extends|extends
name|ChangeIndexer
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ChangeIndexerImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|indexes
specifier|private
specifier|final
name|IndexCollection
name|indexes
decl_stmt|;
DECL|field|index
specifier|private
specifier|final
name|ChangeIndex
name|index
decl_stmt|;
DECL|field|schemaFactory
specifier|private
specifier|final
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|schemaFactory
decl_stmt|;
DECL|field|context
specifier|private
specifier|final
name|ThreadLocalRequestContext
name|context
decl_stmt|;
annotation|@
name|AssistedInject
DECL|method|ChangeIndexerImpl (@ndexExecutor ListeningScheduledExecutorService executor, SchemaFactory<ReviewDb> schemaFactory, ThreadLocalRequestContext context, @Assisted ChangeIndex index)
name|ChangeIndexerImpl
parameter_list|(
annotation|@
name|IndexExecutor
name|ListeningScheduledExecutorService
name|executor
parameter_list|,
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|schemaFactory
parameter_list|,
name|ThreadLocalRequestContext
name|context
parameter_list|,
annotation|@
name|Assisted
name|ChangeIndex
name|index
parameter_list|)
block|{
name|super
argument_list|(
name|executor
argument_list|)
expr_stmt|;
name|this
operator|.
name|schemaFactory
operator|=
name|schemaFactory
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|indexes
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|AssistedInject
DECL|method|ChangeIndexerImpl (@ndexExecutor ListeningScheduledExecutorService executor, SchemaFactory<ReviewDb> schemaFactory, ThreadLocalRequestContext context, @Assisted IndexCollection indexes)
name|ChangeIndexerImpl
parameter_list|(
annotation|@
name|IndexExecutor
name|ListeningScheduledExecutorService
name|executor
parameter_list|,
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|schemaFactory
parameter_list|,
name|ThreadLocalRequestContext
name|context
parameter_list|,
annotation|@
name|Assisted
name|IndexCollection
name|indexes
parameter_list|)
block|{
name|super
argument_list|(
name|executor
argument_list|)
expr_stmt|;
name|this
operator|.
name|schemaFactory
operator|=
name|schemaFactory
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|index
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|indexes
operator|=
name|indexes
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|indexTask (ChangeData cd)
specifier|public
name|Callable
argument_list|<
name|Void
argument_list|>
name|indexTask
parameter_list|(
name|ChangeData
name|cd
parameter_list|)
block|{
return|return
operator|new
name|Task
argument_list|(
name|cd
argument_list|)
return|;
block|}
DECL|class|Task
specifier|private
class|class
name|Task
implements|implements
name|Callable
argument_list|<
name|Void
argument_list|>
block|{
DECL|field|cd
specifier|private
specifier|final
name|ChangeData
name|cd
decl_stmt|;
DECL|method|Task (ChangeData cd)
specifier|private
name|Task
parameter_list|(
name|ChangeData
name|cd
parameter_list|)
block|{
name|this
operator|.
name|cd
operator|=
name|cd
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
specifier|final
name|ReviewDb
name|db
init|=
name|schemaFactory
operator|.
name|open
argument_list|()
decl_stmt|;
try|try
block|{
name|context
operator|.
name|setContext
argument_list|(
operator|new
name|RequestContext
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|getReviewDbProvider
parameter_list|()
block|{
return|return
name|Providers
operator|.
name|of
argument_list|(
name|db
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CurrentUser
name|getCurrentUser
parameter_list|()
block|{
throw|throw
operator|new
name|OutOfScopeException
argument_list|(
literal|"No user during ChangeIndexer"
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|indexes
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|ChangeIndex
name|i
range|:
name|indexes
operator|.
name|getWriteIndexes
argument_list|()
control|)
block|{
name|i
operator|.
name|replace
argument_list|(
name|cd
argument_list|)
expr_stmt|;
comment|// TODO(dborowitz): Parallelize these
block|}
block|}
else|else
block|{
name|index
operator|.
name|replace
argument_list|(
name|cd
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
finally|finally
block|{
name|context
operator|.
name|setContext
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|db
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Failed to index change %d in %s"
argument_list|,
name|cd
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|cd
operator|.
name|getChange
argument_list|()
operator|.
name|getProject
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"index-change-"
operator|+
name|cd
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

