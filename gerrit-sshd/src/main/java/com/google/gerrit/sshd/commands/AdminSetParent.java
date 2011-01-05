begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2010 The Android Open Source Project
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
comment|// limitations under the License
end_comment

begin_package
DECL|package|com.google.gerrit.sshd.commands
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|sshd
operator|.
name|commands
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
name|git
operator|.
name|ProjectConfig
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
name|ProjectCache
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
name|gerrit
operator|.
name|server
operator|.
name|project
operator|.
name|ProjectState
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
name|sshd
operator|.
name|AdminCommand
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
name|sshd
operator|.
name|BaseCommand
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
name|org
operator|.
name|apache
operator|.
name|sshd
operator|.
name|server
operator|.
name|Environment
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
name|errors
operator|.
name|RepositoryNotFoundException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|kohsuke
operator|.
name|args4j
operator|.
name|Argument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|kohsuke
operator|.
name|args4j
operator|.
name|Option
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
name|ArrayList
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
name|List
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
annotation|@
name|AdminCommand
DECL|class|AdminSetParent
specifier|final
class|class
name|AdminSetParent
extends|extends
name|BaseCommand
block|{
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--parent"
argument_list|,
name|aliases
operator|=
block|{
literal|"-p"
block|}
argument_list|,
name|metaVar
operator|=
literal|"NAME"
argument_list|,
name|usage
operator|=
literal|"new parent project"
argument_list|)
DECL|field|newParent
specifier|private
name|ProjectControl
name|newParent
decl_stmt|;
annotation|@
name|Argument
argument_list|(
name|index
operator|=
literal|0
argument_list|,
name|required
operator|=
literal|true
argument_list|,
name|multiValued
operator|=
literal|true
argument_list|,
name|metaVar
operator|=
literal|"NAME"
argument_list|,
name|usage
operator|=
literal|"projects to modify"
argument_list|)
DECL|field|children
specifier|private
name|List
argument_list|<
name|ProjectControl
argument_list|>
name|children
init|=
operator|new
name|ArrayList
argument_list|<
name|ProjectControl
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Inject
DECL|field|projectCache
specifier|private
name|ProjectCache
name|projectCache
decl_stmt|;
annotation|@
name|Inject
DECL|field|metaDataUpdateFactory
specifier|private
name|MetaDataUpdate
operator|.
name|User
name|metaDataUpdateFactory
decl_stmt|;
annotation|@
name|Inject
annotation|@
name|WildProjectName
DECL|field|wildProject
specifier|private
name|Project
operator|.
name|NameKey
name|wildProject
decl_stmt|;
annotation|@
name|Override
DECL|method|start (final Environment env)
specifier|public
name|void
name|start
parameter_list|(
specifier|final
name|Environment
name|env
parameter_list|)
block|{
name|startThread
argument_list|(
operator|new
name|CommandRunnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|parseCommandLine
argument_list|()
expr_stmt|;
name|updateParents
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|updateParents ()
specifier|private
name|void
name|updateParents
parameter_list|()
throws|throws
name|Failure
block|{
specifier|final
name|StringBuilder
name|err
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
name|grandParents
init|=
operator|new
name|HashSet
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
argument_list|()
decl_stmt|;
name|Project
operator|.
name|NameKey
name|newParentKey
decl_stmt|;
name|grandParents
operator|.
name|add
argument_list|(
name|wildProject
argument_list|)
expr_stmt|;
if|if
condition|(
name|newParent
operator|!=
literal|null
condition|)
block|{
name|newParentKey
operator|=
name|newParent
operator|.
name|getProject
argument_list|()
operator|.
name|getNameKey
argument_list|()
expr_stmt|;
comment|// Catalog all grandparents of the "parent", we want to
comment|// catch a cycle in the parent pointers before it occurs.
comment|//
name|Project
operator|.
name|NameKey
name|gp
init|=
name|newParent
operator|.
name|getProject
argument_list|()
operator|.
name|getParent
argument_list|()
decl_stmt|;
while|while
condition|(
name|gp
operator|!=
literal|null
operator|&&
name|grandParents
operator|.
name|add
argument_list|(
name|gp
argument_list|)
condition|)
block|{
specifier|final
name|ProjectState
name|s
init|=
name|projectCache
operator|.
name|get
argument_list|(
name|gp
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|!=
literal|null
condition|)
block|{
name|gp
operator|=
name|s
operator|.
name|getProject
argument_list|()
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
block|}
else|else
block|{
comment|// If no parent was selected, set to NULL to use the default.
comment|//
name|newParentKey
operator|=
literal|null
expr_stmt|;
block|}
for|for
control|(
specifier|final
name|ProjectControl
name|pc
range|:
name|children
control|)
block|{
specifier|final
name|Project
operator|.
name|NameKey
name|key
init|=
name|pc
operator|.
name|getProject
argument_list|()
operator|.
name|getNameKey
argument_list|()
decl_stmt|;
specifier|final
name|String
name|name
init|=
name|pc
operator|.
name|getProject
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|wildProject
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
comment|// Don't allow the wild card project to have a parent.
comment|//
name|err
operator|.
name|append
argument_list|(
literal|"error: Cannot set parent of '"
operator|+
name|name
operator|+
literal|"'\n"
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|grandParents
operator|.
name|contains
argument_list|(
name|key
argument_list|)
operator|||
name|key
operator|.
name|equals
argument_list|(
name|newParentKey
argument_list|)
condition|)
block|{
comment|// Try to avoid creating a cycle in the parent pointers.
comment|//
name|err
operator|.
name|append
argument_list|(
literal|"error: Cycle exists between '"
operator|+
name|name
operator|+
literal|"' and '"
operator|+
operator|(
name|newParentKey
operator|!=
literal|null
condition|?
name|newParentKey
operator|.
name|get
argument_list|()
else|:
name|wildProject
operator|.
name|get
argument_list|()
operator|)
operator|+
literal|"'\n"
argument_list|)
expr_stmt|;
continue|continue;
block|}
try|try
block|{
name|MetaDataUpdate
name|md
init|=
name|metaDataUpdateFactory
operator|.
name|create
argument_list|(
name|key
argument_list|)
decl_stmt|;
try|try
block|{
name|ProjectConfig
name|config
init|=
name|ProjectConfig
operator|.
name|read
argument_list|(
name|md
argument_list|)
decl_stmt|;
name|config
operator|.
name|getProject
argument_list|()
operator|.
name|setParentName
argument_list|(
name|newParentKey
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|md
operator|.
name|setMessage
argument_list|(
literal|"Inherit access from "
operator|+
name|newParentKey
operator|.
name|get
argument_list|()
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|config
operator|.
name|commit
argument_list|(
name|md
argument_list|)
condition|)
block|{
name|err
operator|.
name|append
argument_list|(
literal|"error: Could not update project "
operator|+
name|name
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|md
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|RepositoryNotFoundException
name|notFound
parameter_list|)
block|{
name|err
operator|.
name|append
argument_list|(
literal|"error: Project "
operator|+
name|name
operator|+
literal|" not found\n"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
literal|1
argument_list|,
literal|"Cannot update project "
operator|+
name|name
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ConfigInvalidException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
literal|1
argument_list|,
literal|"Cannot update project "
operator|+
name|name
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|err
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
while|while
condition|(
name|err
operator|.
name|charAt
argument_list|(
name|err
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|==
literal|'\n'
condition|)
block|{
name|err
operator|.
name|setLength
argument_list|(
name|err
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|UnloggedFailure
argument_list|(
literal|1
argument_list|,
name|err
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

