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
name|errors
operator|.
name|NoSuchGroupException
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
name|NameKey
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
name|OutputFormat
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
name|StringUtil
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
name|GroupCache
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
name|GroupControl
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
name|util
operator|.
name|TreeFormatter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|reflect
operator|.
name|TypeToken
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
name|Constants
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
name|BufferedWriter
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
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
import|;
end_import

begin_comment
comment|/** List projects visible to the calling user. */
end_comment

begin_class
DECL|class|ListProjects
specifier|public
class|class
name|ListProjects
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
name|ListProjects
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|enum|FilterType
specifier|public
specifier|static
enum|enum
name|FilterType
block|{
DECL|enumConstant|CODE
name|CODE
block|{
annotation|@
name|Override
name|boolean
name|matches
parameter_list|(
name|Repository
name|git
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|!
name|PERMISSIONS
operator|.
name|matches
argument_list|(
name|git
argument_list|)
return|;
block|}
block|}
block|,
DECL|enumConstant|PARENT_CANDIDATES
name|PARENT_CANDIDATES
block|{
annotation|@
name|Override
name|boolean
name|matches
parameter_list|(
name|Repository
name|git
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|,
DECL|enumConstant|PERMISSIONS
name|PERMISSIONS
block|{
annotation|@
name|Override
name|boolean
name|matches
parameter_list|(
name|Repository
name|git
parameter_list|)
throws|throws
name|IOException
block|{
name|Ref
name|head
init|=
name|git
operator|.
name|getRef
argument_list|(
name|Constants
operator|.
name|HEAD
argument_list|)
decl_stmt|;
return|return
name|head
operator|!=
literal|null
operator|&&
name|head
operator|.
name|isSymbolic
argument_list|()
operator|&&
name|GitRepositoryManager
operator|.
name|REF_CONFIG
operator|.
name|equals
argument_list|(
name|head
operator|.
name|getLeaf
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
block|,
DECL|enumConstant|ALL
name|ALL
block|{
annotation|@
name|Override
name|boolean
name|matches
parameter_list|(
name|Repository
name|git
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|;
DECL|method|matches (Repository git)
specifier|abstract
name|boolean
name|matches
parameter_list|(
name|Repository
name|git
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
DECL|field|currentUser
specifier|private
specifier|final
name|CurrentUser
name|currentUser
decl_stmt|;
DECL|field|projectCache
specifier|private
specifier|final
name|ProjectCache
name|projectCache
decl_stmt|;
DECL|field|groupCache
specifier|private
specifier|final
name|GroupCache
name|groupCache
decl_stmt|;
DECL|field|groupControlFactory
specifier|private
specifier|final
name|GroupControl
operator|.
name|Factory
name|groupControlFactory
decl_stmt|;
DECL|field|repoManager
specifier|private
specifier|final
name|GitRepositoryManager
name|repoManager
decl_stmt|;
DECL|field|projectNodeFactory
specifier|private
specifier|final
name|ProjectNode
operator|.
name|Factory
name|projectNodeFactory
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--format"
argument_list|,
name|metaVar
operator|=
literal|"FMT"
argument_list|,
name|usage
operator|=
literal|"Output display format"
argument_list|)
DECL|field|format
specifier|private
name|OutputFormat
name|format
init|=
name|OutputFormat
operator|.
name|TEXT
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--show-branch"
argument_list|,
name|aliases
operator|=
block|{
literal|"-b"
block|}
argument_list|,
name|multiValued
operator|=
literal|true
argument_list|,
name|usage
operator|=
literal|"displays the sha of each project in the specified branch"
argument_list|)
DECL|field|showBranch
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|showBranch
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--tree"
argument_list|,
name|aliases
operator|=
block|{
literal|"-t"
block|}
argument_list|,
name|usage
operator|=
literal|"displays project inheritance in a tree-like format\n"
operator|+
literal|"this option does not work together with the show-branch option"
argument_list|)
DECL|field|showTree
specifier|private
name|boolean
name|showTree
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--type"
argument_list|,
name|usage
operator|=
literal|"type of project"
argument_list|)
DECL|field|type
specifier|private
name|FilterType
name|type
init|=
name|FilterType
operator|.
name|CODE
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--description"
argument_list|,
name|aliases
operator|=
block|{
literal|"-d"
block|}
argument_list|,
name|usage
operator|=
literal|"include description of project in list"
argument_list|)
DECL|field|showDescription
specifier|private
name|boolean
name|showDescription
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--all"
argument_list|,
name|usage
operator|=
literal|"display all projects that are accessible by the calling user"
argument_list|)
DECL|field|all
specifier|private
name|boolean
name|all
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--limit"
argument_list|,
name|aliases
operator|=
block|{
literal|"-n"
block|}
argument_list|,
name|metaVar
operator|=
literal|"CNT"
argument_list|,
name|usage
operator|=
literal|"maximum number of projects to list"
argument_list|)
DECL|field|limit
specifier|private
name|int
name|limit
decl_stmt|;
DECL|field|matchPrefix
specifier|private
name|String
name|matchPrefix
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--has-acl-for"
argument_list|,
name|metaVar
operator|=
literal|"GROUP"
argument_list|,
name|usage
operator|=
literal|"displays only projects on which access rights for this group are directly assigned"
argument_list|)
DECL|field|groupUuid
specifier|private
name|AccountGroup
operator|.
name|UUID
name|groupUuid
decl_stmt|;
annotation|@
name|Inject
DECL|method|ListProjects (CurrentUser currentUser, ProjectCache projectCache, GroupCache groupCache, GroupControl.Factory groupControlFactory, GitRepositoryManager repoManager, ProjectNode.Factory projectNodeFactory)
specifier|protected
name|ListProjects
parameter_list|(
name|CurrentUser
name|currentUser
parameter_list|,
name|ProjectCache
name|projectCache
parameter_list|,
name|GroupCache
name|groupCache
parameter_list|,
name|GroupControl
operator|.
name|Factory
name|groupControlFactory
parameter_list|,
name|GitRepositoryManager
name|repoManager
parameter_list|,
name|ProjectNode
operator|.
name|Factory
name|projectNodeFactory
parameter_list|)
block|{
name|this
operator|.
name|currentUser
operator|=
name|currentUser
expr_stmt|;
name|this
operator|.
name|projectCache
operator|=
name|projectCache
expr_stmt|;
name|this
operator|.
name|groupCache
operator|=
name|groupCache
expr_stmt|;
name|this
operator|.
name|groupControlFactory
operator|=
name|groupControlFactory
expr_stmt|;
name|this
operator|.
name|repoManager
operator|=
name|repoManager
expr_stmt|;
name|this
operator|.
name|projectNodeFactory
operator|=
name|projectNodeFactory
expr_stmt|;
block|}
DECL|method|getShowBranch ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getShowBranch
parameter_list|()
block|{
return|return
name|showBranch
return|;
block|}
DECL|method|isShowTree ()
specifier|public
name|boolean
name|isShowTree
parameter_list|()
block|{
return|return
name|showTree
return|;
block|}
DECL|method|isShowDescription ()
specifier|public
name|boolean
name|isShowDescription
parameter_list|()
block|{
return|return
name|showDescription
return|;
block|}
DECL|method|getFormat ()
specifier|public
name|OutputFormat
name|getFormat
parameter_list|()
block|{
return|return
name|format
return|;
block|}
DECL|method|setFormat (OutputFormat fmt)
specifier|public
name|ListProjects
name|setFormat
parameter_list|(
name|OutputFormat
name|fmt
parameter_list|)
block|{
name|this
operator|.
name|format
operator|=
name|fmt
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setMatchPrefix (String prefix)
specifier|public
name|ListProjects
name|setMatchPrefix
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
name|this
operator|.
name|matchPrefix
operator|=
name|prefix
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|display (OutputStream out)
specifier|public
name|void
name|display
parameter_list|(
name|OutputStream
name|out
parameter_list|)
block|{
specifier|final
name|PrintWriter
name|stdout
decl_stmt|;
try|try
block|{
name|stdout
operator|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|BufferedWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
name|out
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
comment|// Our encoding is required by the specifications for the runtime.
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"JVM lacks UTF-8 encoding"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|int
name|found
init|=
literal|0
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|ProjectInfo
argument_list|>
name|output
init|=
name|Maps
operator|.
name|newTreeMap
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|hiddenNames
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|rejected
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|TreeMap
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|,
name|ProjectNode
argument_list|>
name|treeMap
init|=
operator|new
name|TreeMap
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|,
name|ProjectNode
argument_list|>
argument_list|()
decl_stmt|;
try|try
block|{
for|for
control|(
specifier|final
name|Project
operator|.
name|NameKey
name|projectName
range|:
name|scan
argument_list|()
control|)
block|{
specifier|final
name|ProjectState
name|e
init|=
name|projectCache
operator|.
name|get
argument_list|(
name|projectName
argument_list|)
decl_stmt|;
if|if
condition|(
name|e
operator|==
literal|null
condition|)
block|{
comment|// If we can't get it from the cache, pretend its not present.
comment|//
continue|continue;
block|}
specifier|final
name|ProjectControl
name|pctl
init|=
name|e
operator|.
name|controlFor
argument_list|(
name|currentUser
argument_list|)
decl_stmt|;
if|if
condition|(
name|groupUuid
operator|!=
literal|null
condition|)
block|{
try|try
block|{
if|if
condition|(
operator|!
name|groupControlFactory
operator|.
name|controlFor
argument_list|(
name|groupUuid
argument_list|)
operator|.
name|isVisible
argument_list|()
condition|)
block|{
break|break;
block|}
block|}
catch|catch
parameter_list|(
name|NoSuchGroupException
name|ex
parameter_list|)
block|{
break|break;
block|}
if|if
condition|(
operator|!
name|pctl
operator|.
name|getLocalGroups
argument_list|()
operator|.
name|contains
argument_list|(
name|GroupReference
operator|.
name|forGroup
argument_list|(
name|groupCache
operator|.
name|get
argument_list|(
name|groupUuid
argument_list|)
argument_list|)
argument_list|)
condition|)
block|{
continue|continue;
block|}
block|}
name|ProjectInfo
name|info
init|=
operator|new
name|ProjectInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|FilterType
operator|.
name|PARENT_CANDIDATES
condition|)
block|{
name|ProjectState
name|parentState
init|=
name|e
operator|.
name|getParentState
argument_list|()
decl_stmt|;
if|if
condition|(
name|parentState
operator|!=
literal|null
operator|&&
operator|!
name|output
operator|.
name|keySet
argument_list|()
operator|.
name|contains
argument_list|(
name|parentState
operator|.
name|getProject
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|&&
operator|!
name|rejected
operator|.
name|contains
argument_list|(
name|parentState
operator|.
name|getProject
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|ProjectControl
name|parentCtrl
init|=
name|parentState
operator|.
name|controlFor
argument_list|(
name|currentUser
argument_list|)
decl_stmt|;
if|if
condition|(
name|parentCtrl
operator|.
name|isVisible
argument_list|()
operator|||
name|parentCtrl
operator|.
name|isOwner
argument_list|()
condition|)
block|{
name|info
operator|.
name|name
operator|=
name|parentState
operator|.
name|getProject
argument_list|()
operator|.
name|getName
argument_list|()
expr_stmt|;
name|info
operator|.
name|description
operator|=
name|parentState
operator|.
name|getProject
argument_list|()
operator|.
name|getDescription
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|rejected
operator|.
name|add
argument_list|(
name|parentState
operator|.
name|getProject
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
continue|continue;
block|}
block|}
else|else
block|{
continue|continue;
block|}
block|}
else|else
block|{
specifier|final
name|boolean
name|isVisible
init|=
name|pctl
operator|.
name|isVisible
argument_list|()
operator|||
operator|(
name|all
operator|&&
name|pctl
operator|.
name|isOwner
argument_list|()
operator|)
decl_stmt|;
if|if
condition|(
name|showTree
operator|&&
operator|!
name|format
operator|.
name|isJson
argument_list|()
condition|)
block|{
name|treeMap
operator|.
name|put
argument_list|(
name|projectName
argument_list|,
name|projectNodeFactory
operator|.
name|create
argument_list|(
name|pctl
operator|.
name|getProject
argument_list|()
argument_list|,
name|isVisible
argument_list|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
operator|!
name|isVisible
operator|&&
operator|!
operator|(
name|showTree
operator|&&
name|pctl
operator|.
name|isOwner
argument_list|()
operator|)
condition|)
block|{
comment|// Require the project itself to be visible to the user.
comment|//
continue|continue;
block|}
name|info
operator|.
name|name
operator|=
name|projectName
operator|.
name|get
argument_list|()
expr_stmt|;
if|if
condition|(
name|showTree
operator|&&
name|format
operator|.
name|isJson
argument_list|()
condition|)
block|{
name|ProjectState
name|parent
init|=
name|e
operator|.
name|getParentState
argument_list|()
decl_stmt|;
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|ProjectControl
name|parentCtrl
init|=
name|parent
operator|.
name|controlFor
argument_list|(
name|currentUser
argument_list|)
decl_stmt|;
if|if
condition|(
name|parentCtrl
operator|.
name|isVisible
argument_list|()
operator|||
name|parentCtrl
operator|.
name|isOwner
argument_list|()
condition|)
block|{
name|info
operator|.
name|parent
operator|=
name|parent
operator|.
name|getProject
argument_list|()
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|info
operator|.
name|parent
operator|=
name|hiddenNames
operator|.
name|get
argument_list|(
name|parent
operator|.
name|getProject
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|info
operator|.
name|parent
operator|==
literal|null
condition|)
block|{
name|info
operator|.
name|parent
operator|=
literal|"?-"
operator|+
operator|(
name|hiddenNames
operator|.
name|size
argument_list|()
operator|+
literal|1
operator|)
expr_stmt|;
name|hiddenNames
operator|.
name|put
argument_list|(
name|parent
operator|.
name|getProject
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|info
operator|.
name|parent
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|showDescription
operator|&&
operator|!
name|e
operator|.
name|getProject
argument_list|()
operator|.
name|getDescription
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|info
operator|.
name|description
operator|=
name|e
operator|.
name|getProject
argument_list|()
operator|.
name|getDescription
argument_list|()
expr_stmt|;
block|}
try|try
block|{
if|if
condition|(
name|showBranch
operator|!=
literal|null
condition|)
block|{
name|Repository
name|git
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
if|if
condition|(
operator|!
name|type
operator|.
name|matches
argument_list|(
name|git
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|List
argument_list|<
name|Ref
argument_list|>
name|refs
init|=
name|getBranchRefs
argument_list|(
name|projectName
argument_list|,
name|pctl
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|hasValidRef
argument_list|(
name|refs
argument_list|)
condition|)
block|{
continue|continue;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|showBranch
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Ref
name|ref
init|=
name|refs
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|ref
operator|!=
literal|null
operator|&&
name|ref
operator|.
name|getObjectId
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|info
operator|.
name|branches
operator|==
literal|null
condition|)
block|{
name|info
operator|.
name|branches
operator|=
name|Maps
operator|.
name|newLinkedHashMap
argument_list|()
expr_stmt|;
block|}
name|info
operator|.
name|branches
operator|.
name|put
argument_list|(
name|showBranch
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|ref
operator|.
name|getObjectId
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
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
elseif|else
if|if
condition|(
operator|!
name|showTree
operator|&&
name|type
operator|!=
name|FilterType
operator|.
name|ALL
condition|)
block|{
name|Repository
name|git
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
if|if
condition|(
operator|!
name|type
operator|.
name|matches
argument_list|(
name|git
argument_list|)
condition|)
block|{
continue|continue;
block|}
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
block|}
catch|catch
parameter_list|(
name|RepositoryNotFoundException
name|err
parameter_list|)
block|{
comment|// If the Git repository is gone, the project doesn't actually exist anymore.
continue|continue;
block|}
catch|catch
parameter_list|(
name|IOException
name|err
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Unexpected error reading "
operator|+
name|projectName
argument_list|,
name|err
argument_list|)
expr_stmt|;
continue|continue;
block|}
block|}
if|if
condition|(
name|limit
operator|>
literal|0
operator|&&
operator|++
name|found
operator|>
name|limit
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|format
operator|.
name|isJson
argument_list|()
condition|)
block|{
name|output
operator|.
name|put
argument_list|(
name|info
operator|.
name|name
argument_list|,
name|info
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|showBranch
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|name
range|:
name|showBranch
control|)
block|{
name|String
name|ref
init|=
name|info
operator|.
name|branches
operator|!=
literal|null
condition|?
name|info
operator|.
name|branches
operator|.
name|get
argument_list|(
name|name
argument_list|)
else|:
literal|null
decl_stmt|;
if|if
condition|(
name|ref
operator|==
literal|null
condition|)
block|{
comment|// Print stub (forty '-' symbols)
name|ref
operator|=
literal|"----------------------------------------"
expr_stmt|;
block|}
name|stdout
operator|.
name|print
argument_list|(
name|ref
argument_list|)
expr_stmt|;
name|stdout
operator|.
name|print
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
block|}
name|stdout
operator|.
name|print
argument_list|(
name|info
operator|.
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|info
operator|.
name|description
operator|!=
literal|null
condition|)
block|{
comment|// We still want to list every project as one-liners, hence escaping \n.
name|stdout
operator|.
name|print
argument_list|(
literal|" - "
operator|+
name|StringUtil
operator|.
name|escapeString
argument_list|(
name|info
operator|.
name|description
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|stdout
operator|.
name|print
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|format
operator|.
name|isJson
argument_list|()
condition|)
block|{
name|format
operator|.
name|newGson
argument_list|()
operator|.
name|toJson
argument_list|(
name|output
argument_list|,
operator|new
name|TypeToken
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|ProjectInfo
argument_list|>
argument_list|>
argument_list|()
block|{}
operator|.
name|getType
argument_list|()
argument_list|,
name|stdout
argument_list|)
expr_stmt|;
name|stdout
operator|.
name|print
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|showTree
operator|&&
name|treeMap
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|printProjectTree
argument_list|(
name|stdout
argument_list|,
name|treeMap
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|stdout
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|scan ()
specifier|private
name|Iterable
argument_list|<
name|NameKey
argument_list|>
name|scan
parameter_list|()
block|{
if|if
condition|(
name|matchPrefix
operator|!=
literal|null
condition|)
block|{
return|return
name|projectCache
operator|.
name|byName
argument_list|(
name|matchPrefix
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|projectCache
operator|.
name|all
argument_list|()
return|;
block|}
block|}
DECL|method|printProjectTree (final PrintWriter stdout, final TreeMap<Project.NameKey, ProjectNode> treeMap)
specifier|private
name|void
name|printProjectTree
parameter_list|(
specifier|final
name|PrintWriter
name|stdout
parameter_list|,
specifier|final
name|TreeMap
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|,
name|ProjectNode
argument_list|>
name|treeMap
parameter_list|)
block|{
specifier|final
name|SortedSet
argument_list|<
name|ProjectNode
argument_list|>
name|sortedNodes
init|=
operator|new
name|TreeSet
argument_list|<
name|ProjectNode
argument_list|>
argument_list|()
decl_stmt|;
comment|// Builds the inheritance tree using a list.
comment|//
for|for
control|(
specifier|final
name|ProjectNode
name|key
range|:
name|treeMap
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|key
operator|.
name|isAllProjects
argument_list|()
condition|)
block|{
name|sortedNodes
operator|.
name|add
argument_list|(
name|key
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|ProjectNode
name|node
init|=
name|treeMap
operator|.
name|get
argument_list|(
name|key
operator|.
name|getParentName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
name|node
operator|.
name|addChild
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sortedNodes
operator|.
name|add
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|TreeFormatter
name|treeFormatter
init|=
operator|new
name|TreeFormatter
argument_list|(
name|stdout
argument_list|)
decl_stmt|;
name|treeFormatter
operator|.
name|printTree
argument_list|(
name|sortedNodes
argument_list|)
expr_stmt|;
name|stdout
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
DECL|method|getBranchRefs (Project.NameKey projectName, ProjectControl projectControl)
specifier|private
name|List
argument_list|<
name|Ref
argument_list|>
name|getBranchRefs
parameter_list|(
name|Project
operator|.
name|NameKey
name|projectName
parameter_list|,
name|ProjectControl
name|projectControl
parameter_list|)
block|{
name|Ref
index|[]
name|result
init|=
operator|new
name|Ref
index|[
name|showBranch
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
try|try
block|{
name|Repository
name|git
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
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|showBranch
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Ref
name|ref
init|=
name|git
operator|.
name|getRef
argument_list|(
name|showBranch
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|ref
operator|!=
literal|null
operator|&&
name|ref
operator|.
name|getObjectId
argument_list|()
operator|!=
literal|null
operator|&&
operator|(
name|projectControl
operator|.
name|controlForRef
argument_list|(
name|ref
operator|.
name|getLeaf
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|isVisible
argument_list|()
operator|)
operator|||
operator|(
name|all
operator|&&
name|projectControl
operator|.
name|isOwner
argument_list|()
operator|)
condition|)
block|{
name|result
index|[
name|i
index|]
operator|=
name|ref
expr_stmt|;
block|}
block|}
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
name|ioe
parameter_list|)
block|{
comment|// Fall through and return what is available.
block|}
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|result
argument_list|)
return|;
block|}
DECL|method|hasValidRef (List<Ref> refs)
specifier|private
specifier|static
name|boolean
name|hasValidRef
parameter_list|(
name|List
argument_list|<
name|Ref
argument_list|>
name|refs
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|refs
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
name|refs
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|!=
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|class|ProjectInfo
specifier|private
specifier|static
class|class
name|ProjectInfo
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
DECL|field|kind
specifier|final
name|String
name|kind
init|=
literal|"gerritcodereview#project"
decl_stmt|;
DECL|field|name
specifier|transient
name|String
name|name
decl_stmt|;
DECL|field|parent
name|String
name|parent
decl_stmt|;
DECL|field|description
name|String
name|description
decl_stmt|;
DECL|field|branches
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|branches
decl_stmt|;
block|}
block|}
end_class

end_unit

