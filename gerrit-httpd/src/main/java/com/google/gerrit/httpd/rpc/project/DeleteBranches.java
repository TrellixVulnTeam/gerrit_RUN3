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
DECL|package|com.google.gerrit.httpd.rpc.project
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|httpd
operator|.
name|rpc
operator|.
name|project
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
name|common
operator|.
name|ChangeHooks
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
name|httpd
operator|.
name|rpc
operator|.
name|Handler
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
name|IdentifiedUser
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
name|ReplicationQueue
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
name|NoSuchProjectException
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
name|ProjectControl
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
name|assistedinject
operator|.
name|Assisted
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
name|RepositoryNotFoundException
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
name|RefUpdate
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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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

begin_class
DECL|class|DeleteBranches
class|class
name|DeleteBranches
extends|extends
name|Handler
argument_list|<
name|Set
argument_list|<
name|Branch
operator|.
name|NameKey
argument_list|>
argument_list|>
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
name|DeleteBranches
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|interface|Factory
interface|interface
name|Factory
block|{
DECL|method|create (@ssisted Project.NameKey name, @Assisted Set<Branch.NameKey> toRemove)
name|DeleteBranches
name|create
parameter_list|(
annotation|@
name|Assisted
name|Project
operator|.
name|NameKey
name|name
parameter_list|,
annotation|@
name|Assisted
name|Set
argument_list|<
name|Branch
operator|.
name|NameKey
argument_list|>
name|toRemove
parameter_list|)
function_decl|;
block|}
DECL|field|projectControlFactory
specifier|private
specifier|final
name|ProjectControl
operator|.
name|Factory
name|projectControlFactory
decl_stmt|;
DECL|field|repoManager
specifier|private
specifier|final
name|GitRepositoryManager
name|repoManager
decl_stmt|;
DECL|field|replication
specifier|private
specifier|final
name|ReplicationQueue
name|replication
decl_stmt|;
DECL|field|identifiedUser
specifier|private
specifier|final
name|IdentifiedUser
name|identifiedUser
decl_stmt|;
DECL|field|hooks
specifier|private
specifier|final
name|ChangeHooks
name|hooks
decl_stmt|;
DECL|field|db
specifier|private
specifier|final
name|ReviewDb
name|db
decl_stmt|;
DECL|field|projectName
specifier|private
specifier|final
name|Project
operator|.
name|NameKey
name|projectName
decl_stmt|;
DECL|field|toRemove
specifier|private
specifier|final
name|Set
argument_list|<
name|Branch
operator|.
name|NameKey
argument_list|>
name|toRemove
decl_stmt|;
annotation|@
name|Inject
DECL|method|DeleteBranches (final ProjectControl.Factory projectControlFactory, final GitRepositoryManager repoManager, final ReplicationQueue replication, final IdentifiedUser identifiedUser, final ChangeHooks hooks, final ReviewDb db, @Assisted Project.NameKey name, @Assisted Set<Branch.NameKey> toRemove)
name|DeleteBranches
parameter_list|(
specifier|final
name|ProjectControl
operator|.
name|Factory
name|projectControlFactory
parameter_list|,
specifier|final
name|GitRepositoryManager
name|repoManager
parameter_list|,
specifier|final
name|ReplicationQueue
name|replication
parameter_list|,
specifier|final
name|IdentifiedUser
name|identifiedUser
parameter_list|,
specifier|final
name|ChangeHooks
name|hooks
parameter_list|,
specifier|final
name|ReviewDb
name|db
parameter_list|,
annotation|@
name|Assisted
name|Project
operator|.
name|NameKey
name|name
parameter_list|,
annotation|@
name|Assisted
name|Set
argument_list|<
name|Branch
operator|.
name|NameKey
argument_list|>
name|toRemove
parameter_list|)
block|{
name|this
operator|.
name|projectControlFactory
operator|=
name|projectControlFactory
expr_stmt|;
name|this
operator|.
name|repoManager
operator|=
name|repoManager
expr_stmt|;
name|this
operator|.
name|replication
operator|=
name|replication
expr_stmt|;
name|this
operator|.
name|identifiedUser
operator|=
name|identifiedUser
expr_stmt|;
name|this
operator|.
name|hooks
operator|=
name|hooks
expr_stmt|;
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|projectName
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|toRemove
operator|=
name|toRemove
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|Set
argument_list|<
name|Branch
operator|.
name|NameKey
argument_list|>
name|call
parameter_list|()
throws|throws
name|NoSuchProjectException
throws|,
name|RepositoryNotFoundException
throws|,
name|OrmException
throws|,
name|IOException
block|{
specifier|final
name|ProjectControl
name|projectControl
init|=
name|projectControlFactory
operator|.
name|controlFor
argument_list|(
name|projectName
argument_list|)
decl_stmt|;
specifier|final
name|Iterator
argument_list|<
name|Branch
operator|.
name|NameKey
argument_list|>
name|branchIt
init|=
name|toRemove
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|branchIt
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|Branch
operator|.
name|NameKey
name|k
init|=
name|branchIt
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|projectName
operator|.
name|equals
argument_list|(
name|k
operator|.
name|getParentKey
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"All keys must be from same project"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|projectControl
operator|.
name|controlForRef
argument_list|(
name|k
argument_list|)
operator|.
name|canDelete
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cannot delete "
operator|+
name|k
operator|.
name|getShortName
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|db
operator|.
name|changes
argument_list|()
operator|.
name|byBranchOpenAll
argument_list|(
name|k
argument_list|)
operator|.
name|iterator
argument_list|()
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|branchIt
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
specifier|final
name|Set
argument_list|<
name|Branch
operator|.
name|NameKey
argument_list|>
name|deleted
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
specifier|final
name|Repository
name|r
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|projectName
argument_list|)
decl_stmt|;
try|try
block|{
for|for
control|(
specifier|final
name|Branch
operator|.
name|NameKey
name|branchKey
range|:
name|toRemove
control|)
block|{
specifier|final
name|String
name|refname
init|=
name|branchKey
operator|.
name|get
argument_list|()
decl_stmt|;
specifier|final
name|RefUpdate
operator|.
name|Result
name|result
decl_stmt|;
specifier|final
name|RefUpdate
name|u
decl_stmt|;
try|try
block|{
name|u
operator|=
name|r
operator|.
name|updateRef
argument_list|(
name|refname
argument_list|)
expr_stmt|;
name|u
operator|.
name|setForceUpdate
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|result
operator|=
name|u
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cannot delete "
operator|+
name|branchKey
argument_list|,
name|e
argument_list|)
expr_stmt|;
continue|continue;
block|}
switch|switch
condition|(
name|result
condition|)
block|{
case|case
name|NEW
case|:
case|case
name|NO_CHANGE
case|:
case|case
name|FAST_FORWARD
case|:
case|case
name|FORCED
case|:
name|deleted
operator|.
name|add
argument_list|(
name|branchKey
argument_list|)
expr_stmt|;
name|replication
operator|.
name|scheduleUpdate
argument_list|(
name|projectName
argument_list|,
name|refname
argument_list|)
expr_stmt|;
name|hooks
operator|.
name|doRefUpdatedHook
argument_list|(
name|branchKey
argument_list|,
name|u
argument_list|,
name|identifiedUser
operator|.
name|getAccount
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|REJECTED_CURRENT_BRANCH
case|:
name|log
operator|.
name|warn
argument_list|(
literal|"Cannot delete "
operator|+
name|branchKey
operator|+
literal|": "
operator|+
name|result
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
break|break;
default|default:
name|log
operator|.
name|error
argument_list|(
literal|"Cannot delete "
operator|+
name|branchKey
operator|+
literal|": "
operator|+
name|result
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
finally|finally
block|{
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|deleted
return|;
block|}
block|}
end_class

end_unit

