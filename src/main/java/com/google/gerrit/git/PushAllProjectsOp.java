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
DECL|package|com.google.gerrit.git
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
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
name|gerrit
operator|.
name|client
operator|.
name|reviewdb
operator|.
name|Branch
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
name|client
operator|.
name|reviewdb
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
name|client
operator|.
name|reviewdb
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
name|server
operator|.
name|config
operator|.
name|WildProjectName
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
name|client
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
name|client
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
name|assistedinject
operator|.
name|Assisted
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
name|HashSet
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
name|TimeUnit
import|;
end_import

begin_class
DECL|class|PushAllProjectsOp
specifier|public
class|class
name|PushAllProjectsOp
extends|extends
name|DefaultQueueOp
block|{
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (String urlMatch)
name|PushAllProjectsOp
name|create
parameter_list|(
name|String
name|urlMatch
parameter_list|)
function_decl|;
block|}
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
name|PushAllProjectsOp
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|schema
specifier|private
specifier|final
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|schema
decl_stmt|;
DECL|field|replication
specifier|private
specifier|final
name|ReplicationQueue
name|replication
decl_stmt|;
DECL|field|wildProject
specifier|private
specifier|final
name|Project
operator|.
name|NameKey
name|wildProject
decl_stmt|;
DECL|field|urlMatch
specifier|private
specifier|final
name|String
name|urlMatch
decl_stmt|;
annotation|@
name|Inject
DECL|method|PushAllProjectsOp (final WorkQueue wq, final SchemaFactory<ReviewDb> sf, final ReplicationQueue rq, @WildProjectName final Project.NameKey wp, @Assisted @Nullable final String urlMatch)
specifier|public
name|PushAllProjectsOp
parameter_list|(
specifier|final
name|WorkQueue
name|wq
parameter_list|,
specifier|final
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|sf
parameter_list|,
specifier|final
name|ReplicationQueue
name|rq
parameter_list|,
annotation|@
name|WildProjectName
specifier|final
name|Project
operator|.
name|NameKey
name|wp
parameter_list|,
annotation|@
name|Assisted
annotation|@
name|Nullable
specifier|final
name|String
name|urlMatch
parameter_list|)
block|{
name|super
argument_list|(
name|wq
argument_list|)
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|sf
expr_stmt|;
name|this
operator|.
name|replication
operator|=
name|rq
expr_stmt|;
name|this
operator|.
name|wildProject
operator|=
name|wp
expr_stmt|;
name|this
operator|.
name|urlMatch
operator|=
name|urlMatch
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|start (final int delay, final TimeUnit unit)
specifier|public
name|void
name|start
parameter_list|(
specifier|final
name|int
name|delay
parameter_list|,
specifier|final
name|TimeUnit
name|unit
parameter_list|)
block|{
if|if
condition|(
name|replication
operator|.
name|isEnabled
argument_list|()
condition|)
block|{
name|super
operator|.
name|start
argument_list|(
name|delay
argument_list|,
name|unit
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
specifier|final
name|HashSet
argument_list|<
name|Branch
operator|.
name|NameKey
argument_list|>
name|pending
init|=
operator|new
name|HashSet
argument_list|<
name|Branch
operator|.
name|NameKey
argument_list|>
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|ReviewDb
name|db
init|=
name|schema
operator|.
name|open
argument_list|()
decl_stmt|;
try|try
block|{
for|for
control|(
specifier|final
name|Project
name|project
range|:
name|db
operator|.
name|projects
argument_list|()
operator|.
name|all
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|project
operator|.
name|getNameKey
argument_list|()
operator|.
name|equals
argument_list|(
name|wildProject
argument_list|)
condition|)
block|{
name|replication
operator|.
name|scheduleFullSync
argument_list|(
name|project
operator|.
name|getNameKey
argument_list|()
argument_list|,
name|urlMatch
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|db
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cannot enumerate known projects"
argument_list|,
name|e
argument_list|)
expr_stmt|;
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
name|String
name|s
init|=
literal|"Replicate All Projects"
decl_stmt|;
if|if
condition|(
name|urlMatch
operator|!=
literal|null
condition|)
block|{
name|s
operator|=
name|s
operator|+
literal|" to "
operator|+
name|urlMatch
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
block|}
end_class

end_unit

