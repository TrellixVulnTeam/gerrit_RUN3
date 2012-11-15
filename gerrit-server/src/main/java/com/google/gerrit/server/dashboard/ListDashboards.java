begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2012 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.dashboard
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|dashboard
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
name|Config
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
name|ObjectLoader
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
name|eclipse
operator|.
name|jgit
operator|.
name|revwalk
operator|.
name|RevCommit
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
name|revwalk
operator|.
name|RevTree
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
name|revwalk
operator|.
name|RevWalk
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
name|treewalk
operator|.
name|TreeWalk
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
name|ByteArrayOutputStream
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
comment|/** List projects visible to the calling user. */
end_comment

begin_class
DECL|class|ListDashboards
specifier|public
class|class
name|ListDashboards
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
name|ListDashboards
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|REFS_DASHBOARDS
specifier|private
specifier|static
name|String
name|REFS_DASHBOARDS
init|=
literal|"refs/meta/dashboards/"
decl_stmt|;
DECL|enum|Level
specifier|public
specifier|static
enum|enum
name|Level
block|{
DECL|enumConstant|PROJECT
name|PROJECT
block|}
empty_stmt|;
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
DECL|field|repoManager
specifier|private
specifier|final
name|GitRepositoryManager
name|repoManager
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
name|JSON
decl_stmt|;
DECL|field|level
specifier|private
name|Level
name|level
decl_stmt|;
DECL|field|entityName
specifier|private
name|String
name|entityName
decl_stmt|;
annotation|@
name|Inject
DECL|method|ListDashboards (CurrentUser currentUser, ProjectCache projectCache, GitRepositoryManager repoManager)
specifier|protected
name|ListDashboards
parameter_list|(
name|CurrentUser
name|currentUser
parameter_list|,
name|ProjectCache
name|projectCache
parameter_list|,
name|GitRepositoryManager
name|repoManager
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
name|repoManager
operator|=
name|repoManager
expr_stmt|;
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
name|ListDashboards
name|setFormat
parameter_list|(
name|OutputFormat
name|fmt
parameter_list|)
block|{
if|if
condition|(
operator|!
name|format
operator|.
name|isJson
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|format
operator|.
name|name
argument_list|()
operator|+
literal|" not supported"
argument_list|)
throw|;
block|}
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
DECL|method|setLevel (Level level)
specifier|public
name|ListDashboards
name|setLevel
parameter_list|(
name|Level
name|level
parameter_list|)
block|{
name|this
operator|.
name|level
operator|=
name|level
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setEntityName (String entityName)
specifier|public
name|ListDashboards
name|setEntityName
parameter_list|(
name|String
name|entityName
parameter_list|)
block|{
name|this
operator|.
name|entityName
operator|=
name|entityName
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
try|try
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|DashboardInfo
argument_list|>
name|dashboards
decl_stmt|;
if|if
condition|(
name|level
operator|!=
literal|null
condition|)
block|{
switch|switch
condition|(
name|level
condition|)
block|{
case|case
name|PROJECT
case|:
name|dashboards
operator|=
name|projectDashboards
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
name|entityName
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"unsupported dashboard level: "
operator|+
name|level
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|dashboards
operator|=
name|Maps
operator|.
name|newTreeMap
argument_list|()
expr_stmt|;
block|}
name|format
operator|.
name|newGson
argument_list|()
operator|.
name|toJson
argument_list|(
name|dashboards
argument_list|,
operator|new
name|TypeToken
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|DashboardInfo
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
finally|finally
block|{
name|stdout
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|projectDashboards (final Project.NameKey projectName)
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|DashboardInfo
argument_list|>
name|projectDashboards
parameter_list|(
specifier|final
name|Project
operator|.
name|NameKey
name|projectName
parameter_list|)
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|DashboardInfo
argument_list|>
name|dashboards
init|=
name|Maps
operator|.
name|newTreeMap
argument_list|()
decl_stmt|;
specifier|final
name|ProjectState
name|projectState
init|=
name|projectCache
operator|.
name|get
argument_list|(
name|projectName
argument_list|)
decl_stmt|;
specifier|final
name|ProjectControl
name|projectControl
init|=
name|projectState
operator|.
name|controlFor
argument_list|(
name|currentUser
argument_list|)
decl_stmt|;
if|if
condition|(
name|projectState
operator|==
literal|null
operator|||
operator|!
name|projectControl
operator|.
name|isVisible
argument_list|()
condition|)
block|{
return|return
name|dashboards
return|;
block|}
name|Repository
name|repo
init|=
literal|null
decl_stmt|;
name|RevWalk
name|revWalk
init|=
literal|null
decl_stmt|;
try|try
block|{
name|repo
operator|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|projectName
argument_list|)
expr_stmt|;
name|revWalk
operator|=
operator|new
name|RevWalk
argument_list|(
name|repo
argument_list|)
expr_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Ref
argument_list|>
name|refs
init|=
name|repo
operator|.
name|getRefDatabase
argument_list|()
operator|.
name|getRefs
argument_list|(
name|REFS_DASHBOARDS
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|Ref
name|ref
range|:
name|refs
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|projectControl
operator|.
name|controlForRef
argument_list|(
name|ref
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|canRead
argument_list|()
condition|)
block|{
name|dashboards
operator|.
name|putAll
argument_list|(
name|loadDashboards
argument_list|(
name|projectName
argument_list|,
name|repo
argument_list|,
name|revWalk
argument_list|,
name|ref
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Failed to load dashboards of project "
operator|+
name|projectName
operator|.
name|get
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|revWalk
operator|!=
literal|null
condition|)
block|{
name|revWalk
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|repo
operator|!=
literal|null
condition|)
block|{
name|repo
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|dashboards
return|;
block|}
DECL|method|loadDashboards ( final Project.NameKey projectName, final Repository repo, final RevWalk revWalk, final Ref ref)
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|DashboardInfo
argument_list|>
name|loadDashboards
parameter_list|(
specifier|final
name|Project
operator|.
name|NameKey
name|projectName
parameter_list|,
specifier|final
name|Repository
name|repo
parameter_list|,
specifier|final
name|RevWalk
name|revWalk
parameter_list|,
specifier|final
name|Ref
name|ref
parameter_list|)
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|DashboardInfo
argument_list|>
name|dashboards
init|=
name|Maps
operator|.
name|newTreeMap
argument_list|()
decl_stmt|;
name|TreeWalk
name|treeWalk
init|=
operator|new
name|TreeWalk
argument_list|(
name|repo
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|RevCommit
name|commit
init|=
name|revWalk
operator|.
name|parseCommit
argument_list|(
name|ref
operator|.
name|getObjectId
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|RevTree
name|tree
init|=
name|commit
operator|.
name|getTree
argument_list|()
decl_stmt|;
name|treeWalk
operator|.
name|addTree
argument_list|(
name|tree
argument_list|)
expr_stmt|;
name|treeWalk
operator|.
name|setRecursive
argument_list|(
literal|true
argument_list|)
expr_stmt|;
while|while
condition|(
name|treeWalk
operator|.
name|next
argument_list|()
condition|)
block|{
name|DashboardInfo
name|info
init|=
operator|new
name|DashboardInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|dashboardName
operator|=
name|treeWalk
operator|.
name|getPathString
argument_list|()
expr_stmt|;
name|info
operator|.
name|refName
operator|=
name|ref
operator|.
name|getName
argument_list|()
expr_stmt|;
name|info
operator|.
name|projectName
operator|=
name|projectName
operator|.
name|get
argument_list|()
expr_stmt|;
name|info
operator|.
name|id
operator|=
name|createId
argument_list|(
name|info
operator|.
name|refName
argument_list|,
name|info
operator|.
name|dashboardName
argument_list|)
expr_stmt|;
specifier|final
name|ObjectLoader
name|loader
init|=
name|repo
operator|.
name|open
argument_list|(
name|treeWalk
operator|.
name|getObjectId
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|loader
operator|.
name|copyTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|Config
name|dashboardConfig
init|=
operator|new
name|Config
argument_list|()
decl_stmt|;
name|dashboardConfig
operator|.
name|fromText
argument_list|(
operator|new
name|String
argument_list|(
name|out
operator|.
name|toByteArray
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|description
operator|=
name|dashboardConfig
operator|.
name|getString
argument_list|(
literal|"main"
argument_list|,
literal|null
argument_list|,
literal|"description"
argument_list|)
expr_stmt|;
specifier|final
name|StringBuilder
name|query
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|query
operator|.
name|append
argument_list|(
literal|"title="
argument_list|)
expr_stmt|;
name|query
operator|.
name|append
argument_list|(
name|info
operator|.
name|dashboardName
operator|.
name|replaceAll
argument_list|(
literal|" "
argument_list|,
literal|"+"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|sections
init|=
name|dashboardConfig
operator|.
name|getSubsections
argument_list|(
literal|"section"
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|section
range|:
name|sections
control|)
block|{
name|query
operator|.
name|append
argument_list|(
literal|"&"
argument_list|)
expr_stmt|;
name|query
operator|.
name|append
argument_list|(
name|section
operator|.
name|replaceAll
argument_list|(
literal|" "
argument_list|,
literal|"+"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|append
argument_list|(
literal|"="
argument_list|)
expr_stmt|;
name|query
operator|.
name|append
argument_list|(
name|dashboardConfig
operator|.
name|getString
argument_list|(
literal|"section"
argument_list|,
name|section
argument_list|,
literal|"query"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|parameters
operator|=
name|query
operator|.
name|toString
argument_list|()
expr_stmt|;
name|dashboards
operator|.
name|put
argument_list|(
name|info
operator|.
name|id
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Failed to load dashboards of project "
operator|+
name|projectName
operator|.
name|get
argument_list|()
operator|+
literal|" from ref "
operator|+
name|ref
operator|.
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConfigInvalidException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Failed to load dashboards of project "
operator|+
name|projectName
operator|.
name|get
argument_list|()
operator|+
literal|" from ref "
operator|+
name|ref
operator|.
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|treeWalk
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
return|return
name|dashboards
return|;
block|}
DECL|method|createId (final String refName, final String dashboardName)
specifier|private
specifier|static
name|String
name|createId
parameter_list|(
specifier|final
name|String
name|refName
parameter_list|,
specifier|final
name|String
name|dashboardName
parameter_list|)
block|{
return|return
name|refName
operator|+
literal|":"
operator|+
name|dashboardName
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
DECL|class|DashboardInfo
specifier|private
specifier|static
class|class
name|DashboardInfo
block|{
DECL|field|kind
specifier|final
name|String
name|kind
init|=
literal|"gerritcodereview#dashboard"
decl_stmt|;
DECL|field|id
name|String
name|id
decl_stmt|;
DECL|field|dashboardName
name|String
name|dashboardName
decl_stmt|;
DECL|field|refName
name|String
name|refName
decl_stmt|;
DECL|field|projectName
name|String
name|projectName
decl_stmt|;
DECL|field|description
name|String
name|description
decl_stmt|;
DECL|field|parameters
name|String
name|parameters
decl_stmt|;
block|}
block|}
end_class

end_unit

