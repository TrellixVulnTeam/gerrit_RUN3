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
name|common
operator|.
name|base
operator|.
name|Function
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
name|common
operator|.
name|collect
operator|.
name|Iterables
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
name|common
operator|.
name|data
operator|.
name|GlobalCapability
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
name|annotations
operator|.
name|RequiresCapability
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
name|common
operator|.
name|ProjectInfo
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
name|ListChildProjects
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
name|ProjectResource
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
name|CommandMetaData
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
name|SshCommand
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
name|ArrayList
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
name|RequiresCapability
argument_list|(
name|GlobalCapability
operator|.
name|ADMINISTRATE_SERVER
argument_list|)
annotation|@
name|CommandMetaData
argument_list|(
name|name
operator|=
literal|"set-project-parent"
argument_list|,
name|description
operator|=
literal|"Change the project permissions are inherited from"
argument_list|)
DECL|class|AdminSetParent
specifier|final
class|class
name|AdminSetParent
extends|extends
name|SshCommand
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
name|AdminSetParent
operator|.
name|class
argument_list|)
decl_stmt|;
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
name|Option
argument_list|(
name|name
operator|=
literal|"--children-of"
argument_list|,
name|metaVar
operator|=
literal|"NAME"
argument_list|,
name|usage
operator|=
literal|"parent project for which the child projects should be reparented"
argument_list|)
DECL|field|oldParent
specifier|private
name|ProjectControl
name|oldParent
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--exclude"
argument_list|,
name|metaVar
operator|=
literal|"NAME"
argument_list|,
name|usage
operator|=
literal|"child project of old parent project which should not be reparented"
argument_list|)
DECL|field|excludedChildren
specifier|private
name|List
argument_list|<
name|ProjectControl
argument_list|>
name|excludedChildren
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
literal|false
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
argument_list|<>
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
DECL|field|allProjectsName
specifier|private
name|AllProjectsName
name|allProjectsName
decl_stmt|;
annotation|@
name|Inject
DECL|field|listChildProjects
specifier|private
name|ListChildProjects
name|listChildProjects
decl_stmt|;
DECL|field|newParentKey
specifier|private
name|Project
operator|.
name|NameKey
name|newParentKey
decl_stmt|;
annotation|@
name|Override
DECL|method|run ()
specifier|protected
name|void
name|run
parameter_list|()
throws|throws
name|Failure
block|{
if|if
condition|(
name|oldParent
operator|==
literal|null
operator|&&
name|children
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|UnloggedFailure
argument_list|(
literal|1
argument_list|,
literal|"fatal: child projects have to be specified as "
operator|+
literal|"arguments or the --children-of option has to be set"
argument_list|)
throw|;
block|}
if|if
condition|(
name|oldParent
operator|==
literal|null
operator|&&
operator|!
name|excludedChildren
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|UnloggedFailure
argument_list|(
literal|1
argument_list|,
literal|"fatal: --exclude can only be used together "
operator|+
literal|"with --children-of"
argument_list|)
throw|;
block|}
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
argument_list|<>
argument_list|()
decl_stmt|;
name|grandParents
operator|.
name|add
argument_list|(
name|allProjectsName
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
specifier|final
name|List
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
name|childProjects
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|ProjectControl
name|pc
range|:
name|children
control|)
block|{
name|childProjects
operator|.
name|add
argument_list|(
name|pc
operator|.
name|getProject
argument_list|()
operator|.
name|getNameKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|oldParent
operator|!=
literal|null
condition|)
block|{
name|childProjects
operator|.
name|addAll
argument_list|(
name|getChildrenForReparenting
argument_list|(
name|oldParent
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
specifier|final
name|Project
operator|.
name|NameKey
name|nameKey
range|:
name|childProjects
control|)
block|{
specifier|final
name|String
name|name
init|=
name|nameKey
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|allProjectsName
operator|.
name|equals
argument_list|(
name|nameKey
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
argument_list|)
operator|.
name|append
argument_list|(
name|name
argument_list|)
operator|.
name|append
argument_list|(
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
name|nameKey
argument_list|)
operator|||
name|nameKey
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
argument_list|)
operator|.
name|append
argument_list|(
name|name
argument_list|)
operator|.
name|append
argument_list|(
literal|"' and '"
argument_list|)
operator|.
name|append
argument_list|(
name|newParentKey
operator|!=
literal|null
condition|?
name|newParentKey
operator|.
name|get
argument_list|()
else|:
name|allProjectsName
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"'\n"
argument_list|)
expr_stmt|;
continue|continue;
block|}
try|try
init|(
name|MetaDataUpdate
name|md
init|=
name|metaDataUpdateFactory
operator|.
name|create
argument_list|(
name|nameKey
argument_list|)
init|)
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
argument_list|)
expr_stmt|;
name|md
operator|.
name|setMessage
argument_list|(
literal|"Inherit access from "
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
name|allProjectsName
operator|.
name|get
argument_list|()
operator|)
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|config
operator|.
name|commit
argument_list|(
name|md
argument_list|)
expr_stmt|;
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
argument_list|)
operator|.
name|append
argument_list|(
name|name
argument_list|)
operator|.
name|append
argument_list|(
literal|" not found\n"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|ConfigInvalidException
name|e
parameter_list|)
block|{
specifier|final
name|String
name|msg
init|=
literal|"Cannot update project "
operator|+
name|name
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|err
operator|.
name|append
argument_list|(
literal|"error: "
argument_list|)
operator|.
name|append
argument_list|(
name|msg
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
name|projectCache
operator|.
name|evict
argument_list|(
name|nameKey
argument_list|)
expr_stmt|;
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
comment|/**    * Returns the children of the specified parent project that should be    * reparented. The returned list of child projects does not contain projects    * that were specified to be excluded from reparenting.    */
DECL|method|getChildrenForReparenting (final ProjectControl parent)
specifier|private
name|List
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
name|getChildrenForReparenting
parameter_list|(
specifier|final
name|ProjectControl
name|parent
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
name|childProjects
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
name|excluded
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|excludedChildren
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|ProjectControl
name|excludedChild
range|:
name|excludedChildren
control|)
block|{
name|excluded
operator|.
name|add
argument_list|(
name|excludedChild
operator|.
name|getProject
argument_list|()
operator|.
name|getNameKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
name|automaticallyExcluded
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|excludedChildren
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|newParentKey
operator|!=
literal|null
condition|)
block|{
name|automaticallyExcluded
operator|.
name|addAll
argument_list|(
name|getAllParents
argument_list|(
name|newParentKey
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
specifier|final
name|ProjectInfo
name|child
range|:
name|listChildProjects
operator|.
name|apply
argument_list|(
operator|new
name|ProjectResource
argument_list|(
name|parent
argument_list|)
argument_list|)
control|)
block|{
specifier|final
name|Project
operator|.
name|NameKey
name|childName
init|=
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
name|child
operator|.
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|excluded
operator|.
name|contains
argument_list|(
name|childName
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|automaticallyExcluded
operator|.
name|contains
argument_list|(
name|childName
argument_list|)
condition|)
block|{
name|childProjects
operator|.
name|add
argument_list|(
name|childName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|stdout
operator|.
name|println
argument_list|(
literal|"Automatically excluded '"
operator|+
name|childName
operator|+
literal|"' "
operator|+
literal|"from reparenting because it is in the parent "
operator|+
literal|"line of the new parent '"
operator|+
name|newParentKey
operator|+
literal|"'."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|childProjects
return|;
block|}
DECL|method|getAllParents (final Project.NameKey projectName)
specifier|private
name|Set
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
name|getAllParents
parameter_list|(
specifier|final
name|Project
operator|.
name|NameKey
name|projectName
parameter_list|)
block|{
name|ProjectState
name|ps
init|=
name|projectCache
operator|.
name|get
argument_list|(
name|projectName
argument_list|)
decl_stmt|;
return|return
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|Iterables
operator|.
name|transform
argument_list|(
name|ps
operator|!=
literal|null
condition|?
name|ps
operator|.
name|parents
argument_list|()
else|:
name|Collections
operator|.
expr|<
name|ProjectState
operator|>
name|emptySet
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|ProjectState
argument_list|,
name|Project
operator|.
name|NameKey
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Project
operator|.
name|NameKey
name|apply
parameter_list|(
name|ProjectState
name|in
parameter_list|)
block|{
return|return
name|in
operator|.
name|getProject
argument_list|()
operator|.
name|getNameKey
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

