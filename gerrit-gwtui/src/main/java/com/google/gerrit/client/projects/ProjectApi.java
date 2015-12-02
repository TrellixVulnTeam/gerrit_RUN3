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
DECL|package|com.google.gerrit.client.projects
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|projects
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
name|VoidResult
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
name|projects
operator|.
name|ConfigInfo
operator|.
name|ConfigParameterValue
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
name|rpc
operator|.
name|NativeMap
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
name|rpc
operator|.
name|NativeString
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
name|rpc
operator|.
name|RestApi
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
name|client
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
name|extensions
operator|.
name|client
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
name|extensions
operator|.
name|client
operator|.
name|SubmitType
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
name|gwt
operator|.
name|core
operator|.
name|client
operator|.
name|JavaScriptObject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|core
operator|.
name|client
operator|.
name|JsArray
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|user
operator|.
name|client
operator|.
name|rpc
operator|.
name|AsyncCallback
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
name|Map
operator|.
name|Entry
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
DECL|class|ProjectApi
specifier|public
class|class
name|ProjectApi
block|{
comment|/** Create a new project */
DECL|method|createProject (String projectName, String parent, Boolean createEmptyCcommit, Boolean permissionsOnly, AsyncCallback<VoidResult> cb)
specifier|public
specifier|static
name|void
name|createProject
parameter_list|(
name|String
name|projectName
parameter_list|,
name|String
name|parent
parameter_list|,
name|Boolean
name|createEmptyCcommit
parameter_list|,
name|Boolean
name|permissionsOnly
parameter_list|,
name|AsyncCallback
argument_list|<
name|VoidResult
argument_list|>
name|cb
parameter_list|)
block|{
name|ProjectInput
name|input
init|=
name|ProjectInput
operator|.
name|create
argument_list|()
decl_stmt|;
name|input
operator|.
name|setName
argument_list|(
name|projectName
argument_list|)
expr_stmt|;
name|input
operator|.
name|setParent
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|input
operator|.
name|setPermissionsOnly
argument_list|(
name|permissionsOnly
argument_list|)
expr_stmt|;
name|input
operator|.
name|setCreateEmptyCommit
argument_list|(
name|createEmptyCcommit
argument_list|)
expr_stmt|;
operator|new
name|RestApi
argument_list|(
literal|"/projects/"
argument_list|)
operator|.
name|id
argument_list|(
name|projectName
argument_list|)
operator|.
name|ifNoneMatch
argument_list|()
operator|.
name|put
argument_list|(
name|input
argument_list|,
name|cb
argument_list|)
expr_stmt|;
block|}
DECL|method|getRestApi (Project.NameKey name, String viewName, int limit, int start, String match)
specifier|private
specifier|static
name|RestApi
name|getRestApi
parameter_list|(
name|Project
operator|.
name|NameKey
name|name
parameter_list|,
name|String
name|viewName
parameter_list|,
name|int
name|limit
parameter_list|,
name|int
name|start
parameter_list|,
name|String
name|match
parameter_list|)
block|{
name|RestApi
name|call
init|=
name|project
argument_list|(
name|name
argument_list|)
operator|.
name|view
argument_list|(
name|viewName
argument_list|)
decl_stmt|;
name|call
operator|.
name|addParameter
argument_list|(
literal|"n"
argument_list|,
name|limit
argument_list|)
expr_stmt|;
name|call
operator|.
name|addParameter
argument_list|(
literal|"s"
argument_list|,
name|start
argument_list|)
expr_stmt|;
if|if
condition|(
name|match
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|match
operator|.
name|startsWith
argument_list|(
literal|"^"
argument_list|)
condition|)
block|{
name|call
operator|.
name|addParameter
argument_list|(
literal|"r"
argument_list|,
name|match
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|call
operator|.
name|addParameter
argument_list|(
literal|"m"
argument_list|,
name|match
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|call
return|;
block|}
comment|/** Retrieve all visible tags of the project */
DECL|method|getTags (Project.NameKey name, AsyncCallback<JsArray<TagInfo>> cb)
specifier|public
specifier|static
name|void
name|getTags
parameter_list|(
name|Project
operator|.
name|NameKey
name|name
parameter_list|,
name|AsyncCallback
argument_list|<
name|JsArray
argument_list|<
name|TagInfo
argument_list|>
argument_list|>
name|cb
parameter_list|)
block|{
name|project
argument_list|(
name|name
argument_list|)
operator|.
name|view
argument_list|(
literal|"tags"
argument_list|)
operator|.
name|get
argument_list|(
name|cb
argument_list|)
expr_stmt|;
block|}
DECL|method|getTags (Project.NameKey name, int limit, int start, String match, AsyncCallback<JsArray<TagInfo>> cb)
specifier|public
specifier|static
name|void
name|getTags
parameter_list|(
name|Project
operator|.
name|NameKey
name|name
parameter_list|,
name|int
name|limit
parameter_list|,
name|int
name|start
parameter_list|,
name|String
name|match
parameter_list|,
name|AsyncCallback
argument_list|<
name|JsArray
argument_list|<
name|TagInfo
argument_list|>
argument_list|>
name|cb
parameter_list|)
block|{
name|getRestApi
argument_list|(
name|name
argument_list|,
literal|"tags"
argument_list|,
name|limit
argument_list|,
name|start
argument_list|,
name|match
argument_list|)
operator|.
name|get
argument_list|(
name|cb
argument_list|)
expr_stmt|;
block|}
comment|/** Create a new branch */
DECL|method|createBranch (Project.NameKey name, String ref, String revision, AsyncCallback<BranchInfo> cb)
specifier|public
specifier|static
name|void
name|createBranch
parameter_list|(
name|Project
operator|.
name|NameKey
name|name
parameter_list|,
name|String
name|ref
parameter_list|,
name|String
name|revision
parameter_list|,
name|AsyncCallback
argument_list|<
name|BranchInfo
argument_list|>
name|cb
parameter_list|)
block|{
name|BranchInput
name|input
init|=
name|BranchInput
operator|.
name|create
argument_list|()
decl_stmt|;
name|input
operator|.
name|setRevision
argument_list|(
name|revision
argument_list|)
expr_stmt|;
name|project
argument_list|(
name|name
argument_list|)
operator|.
name|view
argument_list|(
literal|"branches"
argument_list|)
operator|.
name|id
argument_list|(
name|ref
argument_list|)
operator|.
name|ifNoneMatch
argument_list|()
operator|.
name|put
argument_list|(
name|input
argument_list|,
name|cb
argument_list|)
expr_stmt|;
block|}
comment|/** Retrieve all visible branches of the project */
DECL|method|getBranches (Project.NameKey name, AsyncCallback<JsArray<BranchInfo>> cb)
specifier|public
specifier|static
name|void
name|getBranches
parameter_list|(
name|Project
operator|.
name|NameKey
name|name
parameter_list|,
name|AsyncCallback
argument_list|<
name|JsArray
argument_list|<
name|BranchInfo
argument_list|>
argument_list|>
name|cb
parameter_list|)
block|{
name|project
argument_list|(
name|name
argument_list|)
operator|.
name|view
argument_list|(
literal|"branches"
argument_list|)
operator|.
name|get
argument_list|(
name|cb
argument_list|)
expr_stmt|;
block|}
DECL|method|getBranches (Project.NameKey name, int limit, int start, String match, AsyncCallback<JsArray<BranchInfo>> cb)
specifier|public
specifier|static
name|void
name|getBranches
parameter_list|(
name|Project
operator|.
name|NameKey
name|name
parameter_list|,
name|int
name|limit
parameter_list|,
name|int
name|start
parameter_list|,
name|String
name|match
parameter_list|,
name|AsyncCallback
argument_list|<
name|JsArray
argument_list|<
name|BranchInfo
argument_list|>
argument_list|>
name|cb
parameter_list|)
block|{
name|getRestApi
argument_list|(
name|name
argument_list|,
literal|"branches"
argument_list|,
name|limit
argument_list|,
name|start
argument_list|,
name|match
argument_list|)
operator|.
name|get
argument_list|(
name|cb
argument_list|)
expr_stmt|;
block|}
comment|/**    * Delete branches. One call is fired to the server to delete all the    * branches.    */
DECL|method|deleteBranches (Project.NameKey name, Set<String> refs, AsyncCallback<VoidResult> cb)
specifier|public
specifier|static
name|void
name|deleteBranches
parameter_list|(
name|Project
operator|.
name|NameKey
name|name
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|refs
parameter_list|,
name|AsyncCallback
argument_list|<
name|VoidResult
argument_list|>
name|cb
parameter_list|)
block|{
if|if
condition|(
name|refs
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|project
argument_list|(
name|name
argument_list|)
operator|.
name|view
argument_list|(
literal|"branches"
argument_list|)
operator|.
name|id
argument_list|(
name|refs
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|delete
argument_list|(
name|cb
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|DeleteBranchesInput
name|d
init|=
name|DeleteBranchesInput
operator|.
name|create
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|ref
range|:
name|refs
control|)
block|{
name|d
operator|.
name|addBranch
argument_list|(
name|ref
argument_list|)
expr_stmt|;
block|}
name|project
argument_list|(
name|name
argument_list|)
operator|.
name|view
argument_list|(
literal|"branches:delete"
argument_list|)
operator|.
name|post
argument_list|(
name|d
argument_list|,
name|cb
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getConfig (Project.NameKey name, AsyncCallback<ConfigInfo> cb)
specifier|public
specifier|static
name|void
name|getConfig
parameter_list|(
name|Project
operator|.
name|NameKey
name|name
parameter_list|,
name|AsyncCallback
argument_list|<
name|ConfigInfo
argument_list|>
name|cb
parameter_list|)
block|{
name|project
argument_list|(
name|name
argument_list|)
operator|.
name|view
argument_list|(
literal|"config"
argument_list|)
operator|.
name|get
argument_list|(
name|cb
argument_list|)
expr_stmt|;
block|}
DECL|method|setConfig (Project.NameKey name, String description, InheritableBoolean useContributorAgreements, InheritableBoolean useContentMerge, InheritableBoolean useSignedOffBy, InheritableBoolean createNewChangeForAllNotInTarget, InheritableBoolean requireChangeId, InheritableBoolean enableSignedPush, InheritableBoolean requireSignedPush, String maxObjectSizeLimit, SubmitType submitType, ProjectState state, Map<String, Map<String, ConfigParameterValue>> pluginConfigValues, AsyncCallback<ConfigInfo> cb)
specifier|public
specifier|static
name|void
name|setConfig
parameter_list|(
name|Project
operator|.
name|NameKey
name|name
parameter_list|,
name|String
name|description
parameter_list|,
name|InheritableBoolean
name|useContributorAgreements
parameter_list|,
name|InheritableBoolean
name|useContentMerge
parameter_list|,
name|InheritableBoolean
name|useSignedOffBy
parameter_list|,
name|InheritableBoolean
name|createNewChangeForAllNotInTarget
parameter_list|,
name|InheritableBoolean
name|requireChangeId
parameter_list|,
name|InheritableBoolean
name|enableSignedPush
parameter_list|,
name|InheritableBoolean
name|requireSignedPush
parameter_list|,
name|String
name|maxObjectSizeLimit
parameter_list|,
name|SubmitType
name|submitType
parameter_list|,
name|ProjectState
name|state
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|ConfigParameterValue
argument_list|>
argument_list|>
name|pluginConfigValues
parameter_list|,
name|AsyncCallback
argument_list|<
name|ConfigInfo
argument_list|>
name|cb
parameter_list|)
block|{
name|ConfigInput
name|in
init|=
name|ConfigInput
operator|.
name|create
argument_list|()
decl_stmt|;
name|in
operator|.
name|setDescription
argument_list|(
name|description
argument_list|)
expr_stmt|;
name|in
operator|.
name|setUseContributorAgreements
argument_list|(
name|useContributorAgreements
argument_list|)
expr_stmt|;
name|in
operator|.
name|setUseContentMerge
argument_list|(
name|useContentMerge
argument_list|)
expr_stmt|;
name|in
operator|.
name|setUseSignedOffBy
argument_list|(
name|useSignedOffBy
argument_list|)
expr_stmt|;
name|in
operator|.
name|setRequireChangeId
argument_list|(
name|requireChangeId
argument_list|)
expr_stmt|;
name|in
operator|.
name|setCreateNewChangeForAllNotInTarget
argument_list|(
name|createNewChangeForAllNotInTarget
argument_list|)
expr_stmt|;
if|if
condition|(
name|enableSignedPush
operator|!=
literal|null
condition|)
block|{
name|in
operator|.
name|setEnableSignedPush
argument_list|(
name|enableSignedPush
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|requireSignedPush
operator|!=
literal|null
condition|)
block|{
name|in
operator|.
name|setRequireSignedPush
argument_list|(
name|requireSignedPush
argument_list|)
expr_stmt|;
block|}
name|in
operator|.
name|setMaxObjectSizeLimit
argument_list|(
name|maxObjectSizeLimit
argument_list|)
expr_stmt|;
name|in
operator|.
name|setSubmitType
argument_list|(
name|submitType
argument_list|)
expr_stmt|;
name|in
operator|.
name|setState
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|in
operator|.
name|setPluginConfigValues
argument_list|(
name|pluginConfigValues
argument_list|)
expr_stmt|;
name|project
argument_list|(
name|name
argument_list|)
operator|.
name|view
argument_list|(
literal|"config"
argument_list|)
operator|.
name|put
argument_list|(
name|in
argument_list|,
name|cb
argument_list|)
expr_stmt|;
block|}
DECL|method|getParent (Project.NameKey name, final AsyncCallback<Project.NameKey> cb)
specifier|public
specifier|static
name|void
name|getParent
parameter_list|(
name|Project
operator|.
name|NameKey
name|name
parameter_list|,
specifier|final
name|AsyncCallback
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
name|cb
parameter_list|)
block|{
name|project
argument_list|(
name|name
argument_list|)
operator|.
name|view
argument_list|(
literal|"parent"
argument_list|)
operator|.
name|get
argument_list|(
operator|new
name|AsyncCallback
argument_list|<
name|NativeString
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|NativeString
name|result
parameter_list|)
block|{
name|cb
operator|.
name|onSuccess
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
name|result
operator|.
name|asString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|caught
parameter_list|)
block|{
name|cb
operator|.
name|onFailure
argument_list|(
name|caught
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|getChildren (Project.NameKey name, boolean recursive, AsyncCallback<JsArray<ProjectInfo>> cb)
specifier|public
specifier|static
name|void
name|getChildren
parameter_list|(
name|Project
operator|.
name|NameKey
name|name
parameter_list|,
name|boolean
name|recursive
parameter_list|,
name|AsyncCallback
argument_list|<
name|JsArray
argument_list|<
name|ProjectInfo
argument_list|>
argument_list|>
name|cb
parameter_list|)
block|{
name|RestApi
name|view
init|=
name|project
argument_list|(
name|name
argument_list|)
operator|.
name|view
argument_list|(
literal|"children"
argument_list|)
decl_stmt|;
if|if
condition|(
name|recursive
condition|)
block|{
name|view
operator|.
name|addParameterTrue
argument_list|(
literal|"recursive"
argument_list|)
expr_stmt|;
block|}
name|view
operator|.
name|get
argument_list|(
name|cb
argument_list|)
expr_stmt|;
block|}
DECL|method|getDescription (Project.NameKey name, AsyncCallback<NativeString> cb)
specifier|public
specifier|static
name|void
name|getDescription
parameter_list|(
name|Project
operator|.
name|NameKey
name|name
parameter_list|,
name|AsyncCallback
argument_list|<
name|NativeString
argument_list|>
name|cb
parameter_list|)
block|{
name|project
argument_list|(
name|name
argument_list|)
operator|.
name|view
argument_list|(
literal|"description"
argument_list|)
operator|.
name|get
argument_list|(
name|cb
argument_list|)
expr_stmt|;
block|}
DECL|method|setDescription (Project.NameKey name, String description, AsyncCallback<NativeString> cb)
specifier|public
specifier|static
name|void
name|setDescription
parameter_list|(
name|Project
operator|.
name|NameKey
name|name
parameter_list|,
name|String
name|description
parameter_list|,
name|AsyncCallback
argument_list|<
name|NativeString
argument_list|>
name|cb
parameter_list|)
block|{
name|RestApi
name|call
init|=
name|project
argument_list|(
name|name
argument_list|)
operator|.
name|view
argument_list|(
literal|"description"
argument_list|)
decl_stmt|;
if|if
condition|(
name|description
operator|!=
literal|null
operator|&&
operator|!
name|description
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|DescriptionInput
name|input
init|=
name|DescriptionInput
operator|.
name|create
argument_list|()
decl_stmt|;
name|input
operator|.
name|setDescription
argument_list|(
name|description
argument_list|)
expr_stmt|;
name|call
operator|.
name|put
argument_list|(
name|input
argument_list|,
name|cb
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|call
operator|.
name|delete
argument_list|(
name|cb
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setHead (Project.NameKey name, String ref, AsyncCallback<NativeString> cb)
specifier|public
specifier|static
name|void
name|setHead
parameter_list|(
name|Project
operator|.
name|NameKey
name|name
parameter_list|,
name|String
name|ref
parameter_list|,
name|AsyncCallback
argument_list|<
name|NativeString
argument_list|>
name|cb
parameter_list|)
block|{
name|RestApi
name|call
init|=
name|project
argument_list|(
name|name
argument_list|)
operator|.
name|view
argument_list|(
literal|"HEAD"
argument_list|)
decl_stmt|;
name|HeadInput
name|input
init|=
name|HeadInput
operator|.
name|create
argument_list|()
decl_stmt|;
name|input
operator|.
name|setRef
argument_list|(
name|ref
argument_list|)
expr_stmt|;
name|call
operator|.
name|put
argument_list|(
name|input
argument_list|,
name|cb
argument_list|)
expr_stmt|;
block|}
DECL|method|project (Project.NameKey name)
specifier|public
specifier|static
name|RestApi
name|project
parameter_list|(
name|Project
operator|.
name|NameKey
name|name
parameter_list|)
block|{
return|return
operator|new
name|RestApi
argument_list|(
literal|"/projects/"
argument_list|)
operator|.
name|id
argument_list|(
name|name
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
DECL|class|ProjectInput
specifier|private
specifier|static
class|class
name|ProjectInput
extends|extends
name|JavaScriptObject
block|{
DECL|method|create ()
specifier|static
name|ProjectInput
name|create
parameter_list|()
block|{
return|return
operator|(
name|ProjectInput
operator|)
name|createObject
argument_list|()
return|;
block|}
DECL|method|ProjectInput ()
specifier|protected
name|ProjectInput
parameter_list|()
block|{     }
DECL|method|setName (String n)
specifier|final
specifier|native
name|void
name|setName
parameter_list|(
name|String
name|n
parameter_list|)
comment|/*-{ if(n)this.name=n; }-*/
function_decl|;
DECL|method|setParent (String p)
specifier|final
specifier|native
name|void
name|setParent
parameter_list|(
name|String
name|p
parameter_list|)
comment|/*-{ if(p)this.parent=p; }-*/
function_decl|;
DECL|method|setPermissionsOnly (boolean po)
specifier|final
specifier|native
name|void
name|setPermissionsOnly
parameter_list|(
name|boolean
name|po
parameter_list|)
comment|/*-{ if(po)this.permissions_only=po; }-*/
function_decl|;
DECL|method|setCreateEmptyCommit (boolean cc)
specifier|final
specifier|native
name|void
name|setCreateEmptyCommit
parameter_list|(
name|boolean
name|cc
parameter_list|)
comment|/*-{ if(cc)this.create_empty_commit=cc; }-*/
function_decl|;
block|}
DECL|class|ConfigInput
specifier|private
specifier|static
class|class
name|ConfigInput
extends|extends
name|JavaScriptObject
block|{
DECL|method|create ()
specifier|static
name|ConfigInput
name|create
parameter_list|()
block|{
return|return
operator|(
name|ConfigInput
operator|)
name|createObject
argument_list|()
return|;
block|}
DECL|method|ConfigInput ()
specifier|protected
name|ConfigInput
parameter_list|()
block|{     }
DECL|method|setDescription (String d)
specifier|final
specifier|native
name|void
name|setDescription
parameter_list|(
name|String
name|d
parameter_list|)
comment|/*-{ if(d)this.description=d; }-*/
function_decl|;
DECL|method|setUseContributorAgreements (InheritableBoolean v)
specifier|final
name|void
name|setUseContributorAgreements
parameter_list|(
name|InheritableBoolean
name|v
parameter_list|)
block|{
name|setUseContributorAgreementsRaw
argument_list|(
name|v
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|setUseContributorAgreementsRaw (String v)
specifier|private
specifier|final
specifier|native
name|void
name|setUseContributorAgreementsRaw
parameter_list|(
name|String
name|v
parameter_list|)
comment|/*-{ if(v)this.use_contributor_agreements=v; }-*/
function_decl|;
DECL|method|setUseContentMerge (InheritableBoolean v)
specifier|final
name|void
name|setUseContentMerge
parameter_list|(
name|InheritableBoolean
name|v
parameter_list|)
block|{
name|setUseContentMergeRaw
argument_list|(
name|v
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|setUseContentMergeRaw (String v)
specifier|private
specifier|final
specifier|native
name|void
name|setUseContentMergeRaw
parameter_list|(
name|String
name|v
parameter_list|)
comment|/*-{ if(v)this.use_content_merge=v; }-*/
function_decl|;
DECL|method|setUseSignedOffBy (InheritableBoolean v)
specifier|final
name|void
name|setUseSignedOffBy
parameter_list|(
name|InheritableBoolean
name|v
parameter_list|)
block|{
name|setUseSignedOffByRaw
argument_list|(
name|v
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|setUseSignedOffByRaw (String v)
specifier|private
specifier|final
specifier|native
name|void
name|setUseSignedOffByRaw
parameter_list|(
name|String
name|v
parameter_list|)
comment|/*-{ if(v)this.use_signed_off_by=v; }-*/
function_decl|;
DECL|method|setRequireChangeId (InheritableBoolean v)
specifier|final
name|void
name|setRequireChangeId
parameter_list|(
name|InheritableBoolean
name|v
parameter_list|)
block|{
name|setRequireChangeIdRaw
argument_list|(
name|v
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|setRequireChangeIdRaw (String v)
specifier|private
specifier|final
specifier|native
name|void
name|setRequireChangeIdRaw
parameter_list|(
name|String
name|v
parameter_list|)
comment|/*-{ if(v)this.require_change_id=v; }-*/
function_decl|;
DECL|method|setCreateNewChangeForAllNotInTarget (InheritableBoolean v)
specifier|final
name|void
name|setCreateNewChangeForAllNotInTarget
parameter_list|(
name|InheritableBoolean
name|v
parameter_list|)
block|{
name|setCreateNewChangeForAllNotInTargetRaw
argument_list|(
name|v
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|setCreateNewChangeForAllNotInTargetRaw (String v)
specifier|private
specifier|final
specifier|native
name|void
name|setCreateNewChangeForAllNotInTargetRaw
parameter_list|(
name|String
name|v
parameter_list|)
comment|/*-{ if(v)this.create_new_change_for_all_not_in_target=v; }-*/
function_decl|;
DECL|method|setEnableSignedPush (InheritableBoolean v)
specifier|final
name|void
name|setEnableSignedPush
parameter_list|(
name|InheritableBoolean
name|v
parameter_list|)
block|{
name|setEnableSignedPushRaw
argument_list|(
name|v
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|setEnableSignedPushRaw (String v)
specifier|private
specifier|final
specifier|native
name|void
name|setEnableSignedPushRaw
parameter_list|(
name|String
name|v
parameter_list|)
comment|/*-{ if(v)this.enable_signed_push=v; }-*/
function_decl|;
DECL|method|setRequireSignedPush (InheritableBoolean v)
specifier|final
name|void
name|setRequireSignedPush
parameter_list|(
name|InheritableBoolean
name|v
parameter_list|)
block|{
name|setRequireSignedPushRaw
argument_list|(
name|v
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|setRequireSignedPushRaw (String v)
specifier|private
specifier|final
specifier|native
name|void
name|setRequireSignedPushRaw
parameter_list|(
name|String
name|v
parameter_list|)
comment|/*-{ if(v)this.require_signed_push=v; }-*/
function_decl|;
DECL|method|setMaxObjectSizeLimit (String l)
specifier|final
specifier|native
name|void
name|setMaxObjectSizeLimit
parameter_list|(
name|String
name|l
parameter_list|)
comment|/*-{ if(l)this.max_object_size_limit=l; }-*/
function_decl|;
DECL|method|setSubmitType (SubmitType t)
specifier|final
name|void
name|setSubmitType
parameter_list|(
name|SubmitType
name|t
parameter_list|)
block|{
name|setSubmitTypeRaw
argument_list|(
name|t
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|setSubmitTypeRaw (String t)
specifier|private
specifier|final
specifier|native
name|void
name|setSubmitTypeRaw
parameter_list|(
name|String
name|t
parameter_list|)
comment|/*-{ if(t)this.submit_type=t; }-*/
function_decl|;
DECL|method|setState (ProjectState s)
specifier|final
name|void
name|setState
parameter_list|(
name|ProjectState
name|s
parameter_list|)
block|{
name|setStateRaw
argument_list|(
name|s
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|setStateRaw (String s)
specifier|private
specifier|final
specifier|native
name|void
name|setStateRaw
parameter_list|(
name|String
name|s
parameter_list|)
comment|/*-{ if(s)this.state=s; }-*/
function_decl|;
DECL|method|setPluginConfigValues (Map<String, Map<String, ConfigParameterValue>> pluginConfigValues)
specifier|final
name|void
name|setPluginConfigValues
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|ConfigParameterValue
argument_list|>
argument_list|>
name|pluginConfigValues
parameter_list|)
block|{
if|if
condition|(
operator|!
name|pluginConfigValues
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|NativeMap
argument_list|<
name|ConfigParameterValueMap
argument_list|>
name|configValues
init|=
name|NativeMap
operator|.
name|create
argument_list|()
operator|.
name|cast
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|ConfigParameterValue
argument_list|>
argument_list|>
name|e
range|:
name|pluginConfigValues
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|ConfigParameterValueMap
name|values
init|=
name|ConfigParameterValueMap
operator|.
name|create
argument_list|()
decl_stmt|;
name|configValues
operator|.
name|put
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|values
argument_list|)
expr_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|ConfigParameterValue
argument_list|>
name|e2
range|:
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|values
operator|.
name|put
argument_list|(
name|e2
operator|.
name|getKey
argument_list|()
argument_list|,
name|e2
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|setPluginConfigValuesRaw
argument_list|(
name|configValues
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setPluginConfigValuesRaw (NativeMap<ConfigParameterValueMap> v)
specifier|private
specifier|final
specifier|native
name|void
name|setPluginConfigValuesRaw
parameter_list|(
name|NativeMap
argument_list|<
name|ConfigParameterValueMap
argument_list|>
name|v
parameter_list|)
comment|/*-{ this.plugin_config_values=v; }-*/
function_decl|;
block|}
DECL|class|ConfigParameterValueMap
specifier|private
specifier|static
class|class
name|ConfigParameterValueMap
extends|extends
name|JavaScriptObject
block|{
DECL|method|create ()
specifier|static
name|ConfigParameterValueMap
name|create
parameter_list|()
block|{
return|return
name|createObject
argument_list|()
operator|.
name|cast
argument_list|()
return|;
block|}
DECL|method|ConfigParameterValueMap ()
specifier|protected
name|ConfigParameterValueMap
parameter_list|()
block|{     }
DECL|method|put (String n, ConfigParameterValue v)
specifier|public
specifier|final
specifier|native
name|void
name|put
parameter_list|(
name|String
name|n
parameter_list|,
name|ConfigParameterValue
name|v
parameter_list|)
comment|/*-{ this[n] = v; }-*/
function_decl|;
block|}
DECL|class|BranchInput
specifier|private
specifier|static
class|class
name|BranchInput
extends|extends
name|JavaScriptObject
block|{
DECL|method|create ()
specifier|static
name|BranchInput
name|create
parameter_list|()
block|{
return|return
operator|(
name|BranchInput
operator|)
name|createObject
argument_list|()
return|;
block|}
DECL|method|BranchInput ()
specifier|protected
name|BranchInput
parameter_list|()
block|{     }
DECL|method|setRevision (String r)
specifier|final
specifier|native
name|void
name|setRevision
parameter_list|(
name|String
name|r
parameter_list|)
comment|/*-{ if(r)this.revision=r; }-*/
function_decl|;
block|}
DECL|class|DescriptionInput
specifier|private
specifier|static
class|class
name|DescriptionInput
extends|extends
name|JavaScriptObject
block|{
DECL|method|create ()
specifier|static
name|DescriptionInput
name|create
parameter_list|()
block|{
return|return
operator|(
name|DescriptionInput
operator|)
name|createObject
argument_list|()
return|;
block|}
DECL|method|DescriptionInput ()
specifier|protected
name|DescriptionInput
parameter_list|()
block|{     }
DECL|method|setDescription (String d)
specifier|final
specifier|native
name|void
name|setDescription
parameter_list|(
name|String
name|d
parameter_list|)
comment|/*-{ if(d)this.description=d; }-*/
function_decl|;
block|}
DECL|class|HeadInput
specifier|private
specifier|static
class|class
name|HeadInput
extends|extends
name|JavaScriptObject
block|{
DECL|method|create ()
specifier|static
name|HeadInput
name|create
parameter_list|()
block|{
return|return
name|createObject
argument_list|()
operator|.
name|cast
argument_list|()
return|;
block|}
DECL|method|HeadInput ()
specifier|protected
name|HeadInput
parameter_list|()
block|{     }
DECL|method|setRef (String r)
specifier|final
specifier|native
name|void
name|setRef
parameter_list|(
name|String
name|r
parameter_list|)
comment|/*-{ if(r)this.ref=r; }-*/
function_decl|;
block|}
DECL|class|DeleteBranchesInput
specifier|private
specifier|static
class|class
name|DeleteBranchesInput
extends|extends
name|JavaScriptObject
block|{
DECL|method|create ()
specifier|static
name|DeleteBranchesInput
name|create
parameter_list|()
block|{
name|DeleteBranchesInput
name|d
init|=
name|createObject
argument_list|()
operator|.
name|cast
argument_list|()
decl_stmt|;
name|d
operator|.
name|init
argument_list|()
expr_stmt|;
return|return
name|d
return|;
block|}
DECL|method|DeleteBranchesInput ()
specifier|protected
name|DeleteBranchesInput
parameter_list|()
block|{     }
DECL|method|init ()
specifier|final
specifier|native
name|void
name|init
parameter_list|()
comment|/*-{ this.branches = []; }-*/
function_decl|;
DECL|method|addBranch (String b)
specifier|final
specifier|native
name|void
name|addBranch
parameter_list|(
name|String
name|b
parameter_list|)
comment|/*-{ this.branches.push(b); }-*/
function_decl|;
block|}
block|}
end_class

end_unit

