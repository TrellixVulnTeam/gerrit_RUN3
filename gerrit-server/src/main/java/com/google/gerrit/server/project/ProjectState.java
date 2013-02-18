begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2008 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.project
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
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
name|base
operator|.
name|Predicate
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
name|common
operator|.
name|collect
operator|.
name|Maps
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
name|AccessSection
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
name|common
operator|.
name|data
operator|.
name|LabelType
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
name|LabelTypes
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
name|Permission
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
name|PermissionRule
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
name|client
operator|.
name|Project
operator|.
name|InheritableBoolean
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
name|rules
operator|.
name|PrologEnvironment
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
name|rules
operator|.
name|RulesCache
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
name|account
operator|.
name|CapabilityCollection
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
name|GroupMembership
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
name|ProjectConfig
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
name|com
operator|.
name|googlecode
operator|.
name|prolog_cafe
operator|.
name|compiler
operator|.
name|CompileException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|prolog_cafe
operator|.
name|lang
operator|.
name|PrologMachineCopy
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
name|Ref
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
name|io
operator|.
name|InputStream
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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

begin_comment
comment|/** Cached information on a project. */
end_comment

begin_class
DECL|class|ProjectState
specifier|public
class|class
name|ProjectState
block|{
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (ProjectConfig config)
name|ProjectState
name|create
parameter_list|(
name|ProjectConfig
name|config
parameter_list|)
function_decl|;
block|}
DECL|field|isAllProjects
specifier|private
specifier|final
name|boolean
name|isAllProjects
decl_stmt|;
DECL|field|allProjectsName
specifier|private
specifier|final
name|AllProjectsName
name|allProjectsName
decl_stmt|;
DECL|field|projectCache
specifier|private
specifier|final
name|ProjectCache
name|projectCache
decl_stmt|;
DECL|field|projectControlFactory
specifier|private
specifier|final
name|ProjectControl
operator|.
name|AssistedFactory
name|projectControlFactory
decl_stmt|;
DECL|field|envFactory
specifier|private
specifier|final
name|PrologEnvironment
operator|.
name|Factory
name|envFactory
decl_stmt|;
DECL|field|gitMgr
specifier|private
specifier|final
name|GitRepositoryManager
name|gitMgr
decl_stmt|;
DECL|field|rulesCache
specifier|private
specifier|final
name|RulesCache
name|rulesCache
decl_stmt|;
DECL|field|config
specifier|private
specifier|final
name|ProjectConfig
name|config
decl_stmt|;
DECL|field|localOwners
specifier|private
specifier|final
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|localOwners
decl_stmt|;
comment|/** Prolog rule state. */
DECL|field|rulesMachine
specifier|private
specifier|volatile
name|PrologMachineCopy
name|rulesMachine
decl_stmt|;
comment|/** Last system time the configuration's revision was examined. */
DECL|field|lastCheckTime
specifier|private
specifier|volatile
name|long
name|lastCheckTime
decl_stmt|;
comment|/** Local access sections, wrapped in SectionMatchers for faster evaluation. */
DECL|field|localAccessSections
specifier|private
specifier|volatile
name|List
argument_list|<
name|SectionMatcher
argument_list|>
name|localAccessSections
decl_stmt|;
comment|/** If this is all projects, the capabilities used by the server. */
DECL|field|capabilities
specifier|private
specifier|final
name|CapabilityCollection
name|capabilities
decl_stmt|;
annotation|@
name|Inject
DECL|method|ProjectState ( final ProjectCache projectCache, final AllProjectsName allProjectsName, final ProjectControl.AssistedFactory projectControlFactory, final PrologEnvironment.Factory envFactory, final GitRepositoryManager gitMgr, final RulesCache rulesCache, @Assisted final ProjectConfig config)
specifier|public
name|ProjectState
parameter_list|(
specifier|final
name|ProjectCache
name|projectCache
parameter_list|,
specifier|final
name|AllProjectsName
name|allProjectsName
parameter_list|,
specifier|final
name|ProjectControl
operator|.
name|AssistedFactory
name|projectControlFactory
parameter_list|,
specifier|final
name|PrologEnvironment
operator|.
name|Factory
name|envFactory
parameter_list|,
specifier|final
name|GitRepositoryManager
name|gitMgr
parameter_list|,
specifier|final
name|RulesCache
name|rulesCache
parameter_list|,
annotation|@
name|Assisted
specifier|final
name|ProjectConfig
name|config
parameter_list|)
block|{
name|this
operator|.
name|projectCache
operator|=
name|projectCache
expr_stmt|;
name|this
operator|.
name|isAllProjects
operator|=
name|config
operator|.
name|getProject
argument_list|()
operator|.
name|getNameKey
argument_list|()
operator|.
name|equals
argument_list|(
name|allProjectsName
argument_list|)
expr_stmt|;
name|this
operator|.
name|allProjectsName
operator|=
name|allProjectsName
expr_stmt|;
name|this
operator|.
name|projectControlFactory
operator|=
name|projectControlFactory
expr_stmt|;
name|this
operator|.
name|envFactory
operator|=
name|envFactory
expr_stmt|;
name|this
operator|.
name|gitMgr
operator|=
name|gitMgr
expr_stmt|;
name|this
operator|.
name|rulesCache
operator|=
name|rulesCache
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
name|this
operator|.
name|capabilities
operator|=
name|isAllProjects
condition|?
operator|new
name|CapabilityCollection
argument_list|(
name|config
operator|.
name|getAccessSection
argument_list|(
name|AccessSection
operator|.
name|GLOBAL_CAPABILITIES
argument_list|)
argument_list|)
else|:
literal|null
expr_stmt|;
if|if
condition|(
name|isAllProjects
operator|&&
operator|!
name|Permission
operator|.
name|canBeOnAllProjects
argument_list|(
name|AccessSection
operator|.
name|ALL
argument_list|,
name|Permission
operator|.
name|OWNER
argument_list|)
condition|)
block|{
name|localOwners
operator|=
name|Collections
operator|.
name|emptySet
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|HashSet
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|groups
init|=
operator|new
name|HashSet
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
argument_list|()
decl_stmt|;
name|AccessSection
name|all
init|=
name|config
operator|.
name|getAccessSection
argument_list|(
name|AccessSection
operator|.
name|ALL
argument_list|)
decl_stmt|;
if|if
condition|(
name|all
operator|!=
literal|null
condition|)
block|{
name|Permission
name|owner
init|=
name|all
operator|.
name|getPermission
argument_list|(
name|Permission
operator|.
name|OWNER
argument_list|)
decl_stmt|;
if|if
condition|(
name|owner
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|PermissionRule
name|rule
range|:
name|owner
operator|.
name|getRules
argument_list|()
control|)
block|{
name|GroupReference
name|ref
init|=
name|rule
operator|.
name|getGroup
argument_list|()
decl_stmt|;
if|if
condition|(
name|ref
operator|.
name|getUUID
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|groups
operator|.
name|add
argument_list|(
name|ref
operator|.
name|getUUID
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|localOwners
operator|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|groups
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|needsRefresh (long generation)
name|boolean
name|needsRefresh
parameter_list|(
name|long
name|generation
parameter_list|)
block|{
if|if
condition|(
name|generation
operator|<=
literal|0
condition|)
block|{
return|return
name|isRevisionOutOfDate
argument_list|()
return|;
block|}
if|if
condition|(
name|lastCheckTime
operator|!=
name|generation
condition|)
block|{
name|lastCheckTime
operator|=
name|generation
expr_stmt|;
return|return
name|isRevisionOutOfDate
argument_list|()
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|isRevisionOutOfDate ()
specifier|private
name|boolean
name|isRevisionOutOfDate
parameter_list|()
block|{
try|try
block|{
name|Repository
name|git
init|=
name|gitMgr
operator|.
name|openRepository
argument_list|(
name|getProject
argument_list|()
operator|.
name|getNameKey
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|Ref
name|ref
init|=
name|git
operator|.
name|getRef
argument_list|(
name|GitRepositoryManager
operator|.
name|REF_CONFIG
argument_list|)
decl_stmt|;
if|if
condition|(
name|ref
operator|==
literal|null
operator|||
name|ref
operator|.
name|getObjectId
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
operator|!
name|ref
operator|.
name|getObjectId
argument_list|()
operator|.
name|equals
argument_list|(
name|config
operator|.
name|getRevision
argument_list|()
argument_list|)
return|;
block|}
finally|finally
block|{
name|git
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|gone
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
comment|/**    * @return cached computation of all global capabilities. This should only be    *         invoked on the state from {@link ProjectCache#getAllProjects()}.    *         Null on any other project.    */
DECL|method|getCapabilityCollection ()
specifier|public
name|CapabilityCollection
name|getCapabilityCollection
parameter_list|()
block|{
return|return
name|capabilities
return|;
block|}
comment|/** @return Construct a new PrologEnvironment for the calling thread. */
DECL|method|newPrologEnvironment ()
specifier|public
name|PrologEnvironment
name|newPrologEnvironment
parameter_list|()
throws|throws
name|CompileException
block|{
name|PrologMachineCopy
name|pmc
init|=
name|rulesMachine
decl_stmt|;
if|if
condition|(
name|pmc
operator|==
literal|null
condition|)
block|{
name|pmc
operator|=
name|rulesCache
operator|.
name|loadMachine
argument_list|(
name|getProject
argument_list|()
operator|.
name|getNameKey
argument_list|()
argument_list|,
name|config
operator|.
name|getRulesId
argument_list|()
argument_list|)
expr_stmt|;
name|rulesMachine
operator|=
name|pmc
expr_stmt|;
block|}
return|return
name|envFactory
operator|.
name|create
argument_list|(
name|pmc
argument_list|)
return|;
block|}
comment|/**    * Like {@link #newPrologEnvironment()} but instead of reading the rules.pl    * read the provided input stream.    *    * @param name a name of the input stream. Could be any name.    * @param in InputStream to read prolog rules from    * @throws CompileException    */
DECL|method|newPrologEnvironment (String name, InputStream in)
specifier|public
name|PrologEnvironment
name|newPrologEnvironment
parameter_list|(
name|String
name|name
parameter_list|,
name|InputStream
name|in
parameter_list|)
throws|throws
name|CompileException
block|{
name|PrologMachineCopy
name|pmc
init|=
name|rulesCache
operator|.
name|loadMachine
argument_list|(
name|name
argument_list|,
name|in
argument_list|)
decl_stmt|;
return|return
name|envFactory
operator|.
name|create
argument_list|(
name|pmc
argument_list|)
return|;
block|}
DECL|method|getProject ()
specifier|public
name|Project
name|getProject
parameter_list|()
block|{
return|return
name|config
operator|.
name|getProject
argument_list|()
return|;
block|}
DECL|method|getConfig ()
specifier|public
name|ProjectConfig
name|getConfig
parameter_list|()
block|{
return|return
name|config
return|;
block|}
comment|/** Get the sections that pertain only to this project. */
DECL|method|getLocalAccessSections ()
name|List
argument_list|<
name|SectionMatcher
argument_list|>
name|getLocalAccessSections
parameter_list|()
block|{
name|List
argument_list|<
name|SectionMatcher
argument_list|>
name|sm
init|=
name|localAccessSections
decl_stmt|;
if|if
condition|(
name|sm
operator|==
literal|null
condition|)
block|{
name|Collection
argument_list|<
name|AccessSection
argument_list|>
name|fromConfig
init|=
name|config
operator|.
name|getAccessSections
argument_list|()
decl_stmt|;
name|sm
operator|=
operator|new
name|ArrayList
argument_list|<
name|SectionMatcher
argument_list|>
argument_list|(
name|fromConfig
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|AccessSection
name|section
range|:
name|fromConfig
control|)
block|{
if|if
condition|(
name|isAllProjects
condition|)
block|{
name|List
argument_list|<
name|Permission
argument_list|>
name|copy
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|section
operator|.
name|getPermissions
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Permission
name|p
range|:
name|section
operator|.
name|getPermissions
argument_list|()
control|)
block|{
if|if
condition|(
name|Permission
operator|.
name|canBeOnAllProjects
argument_list|(
name|section
operator|.
name|getName
argument_list|()
argument_list|,
name|p
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|copy
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
block|}
name|section
operator|=
operator|new
name|AccessSection
argument_list|(
name|section
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|section
operator|.
name|setPermissions
argument_list|(
name|copy
argument_list|)
expr_stmt|;
block|}
name|SectionMatcher
name|matcher
init|=
name|SectionMatcher
operator|.
name|wrap
argument_list|(
name|getProject
argument_list|()
operator|.
name|getNameKey
argument_list|()
argument_list|,
name|section
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|!=
literal|null
condition|)
block|{
name|sm
operator|.
name|add
argument_list|(
name|matcher
argument_list|)
expr_stmt|;
block|}
block|}
name|localAccessSections
operator|=
name|sm
expr_stmt|;
block|}
return|return
name|sm
return|;
block|}
comment|/**    * Obtain all local and inherited sections. This collection is looked up    * dynamically and is not cached. Callers should try to cache this result    * per-request as much as possible.    */
DECL|method|getAllSections ()
name|List
argument_list|<
name|SectionMatcher
argument_list|>
name|getAllSections
parameter_list|()
block|{
if|if
condition|(
name|isAllProjects
condition|)
block|{
return|return
name|getLocalAccessSections
argument_list|()
return|;
block|}
name|List
argument_list|<
name|SectionMatcher
argument_list|>
name|all
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|ProjectState
name|s
range|:
name|tree
argument_list|()
control|)
block|{
name|all
operator|.
name|addAll
argument_list|(
name|s
operator|.
name|getLocalAccessSections
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|all
return|;
block|}
comment|/**    * @return all {@link AccountGroup}'s to which the owner privilege for    *         'refs/*' is assigned for this project (the local owners), if there    *         are no local owners the local owners of the nearest parent project    *         that has local owners are returned    */
DECL|method|getOwners ()
specifier|public
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|getOwners
parameter_list|()
block|{
for|for
control|(
name|ProjectState
name|p
range|:
name|tree
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|p
operator|.
name|localOwners
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|p
operator|.
name|localOwners
return|;
block|}
block|}
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
comment|/**    * @return true if any of the groups listed in {@code groups} was declared to    *         be an owner of this project, or one of its parent projects..    */
DECL|method|isOwner (final GroupMembership groups)
name|boolean
name|isOwner
parameter_list|(
specifier|final
name|GroupMembership
name|groups
parameter_list|)
block|{
return|return
name|Iterables
operator|.
name|any
argument_list|(
name|tree
argument_list|()
argument_list|,
operator|new
name|Predicate
argument_list|<
name|ProjectState
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|ProjectState
name|in
parameter_list|)
block|{
return|return
name|groups
operator|.
name|containsAnyOf
argument_list|(
name|in
operator|.
name|localOwners
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|controlFor (final CurrentUser user)
specifier|public
name|ProjectControl
name|controlFor
parameter_list|(
specifier|final
name|CurrentUser
name|user
parameter_list|)
block|{
return|return
name|projectControlFactory
operator|.
name|create
argument_list|(
name|user
argument_list|,
name|this
argument_list|)
return|;
block|}
comment|/**    * @return an iterable that walks through this project and then the parents of    *         this project. Starts from this project and progresses up the    *         hierarchy to All-Projects.    */
DECL|method|tree ()
specifier|public
name|Iterable
argument_list|<
name|ProjectState
argument_list|>
name|tree
parameter_list|()
block|{
return|return
operator|new
name|Iterable
argument_list|<
name|ProjectState
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|ProjectState
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|ProjectHierarchyIterator
argument_list|(
name|projectCache
argument_list|,
name|allProjectsName
argument_list|,
name|ProjectState
operator|.
name|this
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**    * @return an iterable that walks through the parents of this project. Starts    *         from the immediate parent of this project and progresses up the    *         hierarchy to All-Projects.    */
DECL|method|parents ()
specifier|public
name|Iterable
argument_list|<
name|ProjectState
argument_list|>
name|parents
parameter_list|()
block|{
return|return
name|Iterables
operator|.
name|skip
argument_list|(
name|tree
argument_list|()
argument_list|,
literal|1
argument_list|)
return|;
block|}
DECL|method|isAllProjects ()
specifier|public
name|boolean
name|isAllProjects
parameter_list|()
block|{
return|return
name|isAllProjects
return|;
block|}
DECL|method|isUseContributorAgreements ()
specifier|public
name|boolean
name|isUseContributorAgreements
parameter_list|()
block|{
return|return
name|getInheritableBoolean
argument_list|(
operator|new
name|Function
argument_list|<
name|Project
argument_list|,
name|InheritableBoolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|InheritableBoolean
name|apply
parameter_list|(
name|Project
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|getUseContributorAgreements
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|isUseContentMerge ()
specifier|public
name|boolean
name|isUseContentMerge
parameter_list|()
block|{
return|return
name|getInheritableBoolean
argument_list|(
operator|new
name|Function
argument_list|<
name|Project
argument_list|,
name|InheritableBoolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|InheritableBoolean
name|apply
parameter_list|(
name|Project
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|getUseContentMerge
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|isUseSignedOffBy ()
specifier|public
name|boolean
name|isUseSignedOffBy
parameter_list|()
block|{
return|return
name|getInheritableBoolean
argument_list|(
operator|new
name|Function
argument_list|<
name|Project
argument_list|,
name|InheritableBoolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|InheritableBoolean
name|apply
parameter_list|(
name|Project
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|getUseSignedOffBy
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|isRequireChangeID ()
specifier|public
name|boolean
name|isRequireChangeID
parameter_list|()
block|{
return|return
name|getInheritableBoolean
argument_list|(
operator|new
name|Function
argument_list|<
name|Project
argument_list|,
name|InheritableBoolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|InheritableBoolean
name|apply
parameter_list|(
name|Project
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|getRequireChangeID
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|getLabelTypes ()
specifier|public
name|LabelTypes
name|getLabelTypes
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|LabelType
argument_list|>
name|types
init|=
name|Maps
operator|.
name|newLinkedHashMap
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ProjectState
argument_list|>
name|projects
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|tree
argument_list|()
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|reverse
argument_list|(
name|projects
argument_list|)
expr_stmt|;
for|for
control|(
name|ProjectState
name|s
range|:
name|projects
control|)
block|{
for|for
control|(
name|LabelType
name|type
range|:
name|s
operator|.
name|getConfig
argument_list|()
operator|.
name|getLabelSections
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|LabelType
name|old
init|=
name|types
operator|.
name|get
argument_list|(
name|type
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|old
operator|==
literal|null
operator|||
operator|!
name|old
operator|.
name|canOverride
argument_list|()
condition|)
block|{
name|types
operator|.
name|put
argument_list|(
name|type
operator|.
name|getName
argument_list|()
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|List
argument_list|<
name|LabelType
argument_list|>
name|all
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|types
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|LabelType
name|type
range|:
name|types
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|type
operator|.
name|getValues
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|all
operator|.
name|add
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|LabelTypes
argument_list|(
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|all
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getInheritableBoolean (Function<Project, InheritableBoolean> func)
specifier|private
name|boolean
name|getInheritableBoolean
parameter_list|(
name|Function
argument_list|<
name|Project
argument_list|,
name|InheritableBoolean
argument_list|>
name|func
parameter_list|)
block|{
for|for
control|(
name|ProjectState
name|s
range|:
name|tree
argument_list|()
control|)
block|{
switch|switch
condition|(
name|func
operator|.
name|apply
argument_list|(
name|s
operator|.
name|getProject
argument_list|()
argument_list|)
condition|)
block|{
case|case
name|TRUE
case|:
return|return
literal|true
return|;
case|case
name|FALSE
case|:
return|return
literal|false
return|;
case|case
name|INHERIT
case|:
default|default:
continue|continue;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

