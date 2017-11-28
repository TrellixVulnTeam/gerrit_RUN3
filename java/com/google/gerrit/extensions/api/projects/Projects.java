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
DECL|package|com.google.gerrit.extensions.api.projects
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|extensions
operator|.
name|api
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
name|extensions
operator|.
name|restapi
operator|.
name|NotImplementedException
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
name|restapi
operator|.
name|RestApiException
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
name|SortedMap
import|;
end_import

begin_interface
DECL|interface|Projects
specifier|public
interface|interface
name|Projects
block|{
comment|/**    * Look up a project by name.    *    *<p><strong>Note:</strong> This method eagerly reads the project. Methods that mutate the    * project do not necessarily re-read the project. Therefore, calling a getter method on an    * instance after calling a mutation method on that same instance is not guaranteed to reflect the    * mutation. It is not recommended to store references to {@code ProjectApi} instances.    *    * @param name project name.    * @return API for accessing the project.    * @throws RestApiException if an error occurred.    */
DECL|method|name (String name)
name|ProjectApi
name|name
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|RestApiException
function_decl|;
comment|/**    * Create a project using the default configuration.    *    * @param name project name.    * @return API for accessing the newly-created project.    * @throws RestApiException if an error occurred.    */
DECL|method|create (String name)
name|ProjectApi
name|create
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|RestApiException
function_decl|;
comment|/**    * Create a project.    *    * @param in project creation input; name must be set.    * @return API for accessing the newly-created project.    * @throws RestApiException if an error occurred.    */
DECL|method|create (ProjectInput in)
name|ProjectApi
name|create
parameter_list|(
name|ProjectInput
name|in
parameter_list|)
throws|throws
name|RestApiException
function_decl|;
DECL|method|list ()
name|ListRequest
name|list
parameter_list|()
function_decl|;
comment|/**    * Query projects.    *    *<p>Example code: {@code query().withQuery("name:project").get()}    *    * @return API for setting parameters and getting result.    */
DECL|method|query ()
name|QueryRequest
name|query
parameter_list|()
function_decl|;
comment|/**    * Query projects.    *    *<p>Shortcut API for {@code query().withQuery(String)}.    *    * @see #query()    */
DECL|method|query (String query)
name|QueryRequest
name|query
parameter_list|(
name|String
name|query
parameter_list|)
function_decl|;
DECL|class|ListRequest
specifier|abstract
class|class
name|ListRequest
block|{
DECL|enum|FilterType
specifier|public
enum|enum
name|FilterType
block|{
DECL|enumConstant|CODE
name|CODE
block|,
DECL|enumConstant|PARENT_CANDIDATES
name|PARENT_CANDIDATES
block|,
DECL|enumConstant|PERMISSIONS
name|PERMISSIONS
block|,
DECL|enumConstant|ALL
name|ALL
block|}
DECL|field|branches
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|branches
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|description
specifier|private
name|boolean
name|description
decl_stmt|;
DECL|field|prefix
specifier|private
name|String
name|prefix
decl_stmt|;
DECL|field|substring
specifier|private
name|String
name|substring
decl_stmt|;
DECL|field|regex
specifier|private
name|String
name|regex
decl_stmt|;
DECL|field|limit
specifier|private
name|int
name|limit
decl_stmt|;
DECL|field|start
specifier|private
name|int
name|start
decl_stmt|;
DECL|field|showTree
specifier|private
name|boolean
name|showTree
decl_stmt|;
DECL|field|all
specifier|private
name|boolean
name|all
decl_stmt|;
DECL|field|type
specifier|private
name|FilterType
name|type
init|=
name|FilterType
operator|.
name|ALL
decl_stmt|;
DECL|field|state
specifier|private
name|ProjectState
name|state
init|=
literal|null
decl_stmt|;
DECL|method|get ()
specifier|public
name|List
argument_list|<
name|ProjectInfo
argument_list|>
name|get
parameter_list|()
throws|throws
name|RestApiException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|ProjectInfo
argument_list|>
name|map
init|=
name|getAsMap
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ProjectInfo
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|map
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|ProjectInfo
argument_list|>
name|e
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
comment|// ListProjects "helpfully" nulls out names when converting to a map.
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|name
operator|=
name|e
operator|.
name|getKey
argument_list|()
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|result
argument_list|)
return|;
block|}
DECL|method|getAsMap ()
specifier|public
specifier|abstract
name|SortedMap
argument_list|<
name|String
argument_list|,
name|ProjectInfo
argument_list|>
name|getAsMap
parameter_list|()
throws|throws
name|RestApiException
function_decl|;
DECL|method|withDescription (boolean description)
specifier|public
name|ListRequest
name|withDescription
parameter_list|(
name|boolean
name|description
parameter_list|)
block|{
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|withPrefix (String prefix)
specifier|public
name|ListRequest
name|withPrefix
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
name|this
operator|.
name|prefix
operator|=
name|prefix
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|withSubstring (String substring)
specifier|public
name|ListRequest
name|withSubstring
parameter_list|(
name|String
name|substring
parameter_list|)
block|{
name|this
operator|.
name|substring
operator|=
name|substring
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|withRegex (String regex)
specifier|public
name|ListRequest
name|withRegex
parameter_list|(
name|String
name|regex
parameter_list|)
block|{
name|this
operator|.
name|regex
operator|=
name|regex
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|withLimit (int limit)
specifier|public
name|ListRequest
name|withLimit
parameter_list|(
name|int
name|limit
parameter_list|)
block|{
name|this
operator|.
name|limit
operator|=
name|limit
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|withStart (int start)
specifier|public
name|ListRequest
name|withStart
parameter_list|(
name|int
name|start
parameter_list|)
block|{
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|addShowBranch (String branch)
specifier|public
name|ListRequest
name|addShowBranch
parameter_list|(
name|String
name|branch
parameter_list|)
block|{
name|branches
operator|.
name|add
argument_list|(
name|branch
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|withTree (boolean show)
specifier|public
name|ListRequest
name|withTree
parameter_list|(
name|boolean
name|show
parameter_list|)
block|{
name|showTree
operator|=
name|show
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|withType (FilterType type)
specifier|public
name|ListRequest
name|withType
parameter_list|(
name|FilterType
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
operator|!=
literal|null
condition|?
name|type
else|:
name|FilterType
operator|.
name|ALL
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|withAll (boolean all)
specifier|public
name|ListRequest
name|withAll
parameter_list|(
name|boolean
name|all
parameter_list|)
block|{
name|this
operator|.
name|all
operator|=
name|all
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|withState (ProjectState state)
specifier|public
name|ListRequest
name|withState
parameter_list|(
name|ProjectState
name|state
parameter_list|)
block|{
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getDescription ()
specifier|public
name|boolean
name|getDescription
parameter_list|()
block|{
return|return
name|description
return|;
block|}
DECL|method|getPrefix ()
specifier|public
name|String
name|getPrefix
parameter_list|()
block|{
return|return
name|prefix
return|;
block|}
DECL|method|getSubstring ()
specifier|public
name|String
name|getSubstring
parameter_list|()
block|{
return|return
name|substring
return|;
block|}
DECL|method|getRegex ()
specifier|public
name|String
name|getRegex
parameter_list|()
block|{
return|return
name|regex
return|;
block|}
DECL|method|getLimit ()
specifier|public
name|int
name|getLimit
parameter_list|()
block|{
return|return
name|limit
return|;
block|}
DECL|method|getStart ()
specifier|public
name|int
name|getStart
parameter_list|()
block|{
return|return
name|start
return|;
block|}
DECL|method|getBranches ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getBranches
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|branches
argument_list|)
return|;
block|}
DECL|method|getShowTree ()
specifier|public
name|boolean
name|getShowTree
parameter_list|()
block|{
return|return
name|showTree
return|;
block|}
DECL|method|getFilterType ()
specifier|public
name|FilterType
name|getFilterType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
DECL|method|isAll ()
specifier|public
name|boolean
name|isAll
parameter_list|()
block|{
return|return
name|all
return|;
block|}
DECL|method|getState ()
specifier|public
name|ProjectState
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
block|}
comment|/**    * API for setting parameters and getting result. Used for {@code query()}.    *    * @see #query()    */
DECL|class|QueryRequest
specifier|abstract
class|class
name|QueryRequest
block|{
DECL|field|query
specifier|private
name|String
name|query
decl_stmt|;
DECL|field|limit
specifier|private
name|int
name|limit
decl_stmt|;
DECL|field|start
specifier|private
name|int
name|start
decl_stmt|;
comment|/** Execute query and returns the matched projects as list. */
DECL|method|get ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|ProjectInfo
argument_list|>
name|get
parameter_list|()
throws|throws
name|RestApiException
function_decl|;
comment|/**      * Set query.      *      * @param query needs to be in human-readable form.      */
DECL|method|withQuery (String query)
specifier|public
name|QueryRequest
name|withQuery
parameter_list|(
name|String
name|query
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set limit for returned list of projects. Optional; server-default is used when not provided.      */
DECL|method|withLimit (int limit)
specifier|public
name|QueryRequest
name|withLimit
parameter_list|(
name|int
name|limit
parameter_list|)
block|{
name|this
operator|.
name|limit
operator|=
name|limit
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Set number of projects to skip. Optional; no projects are skipped when not provided. */
DECL|method|withStart (int start)
specifier|public
name|QueryRequest
name|withStart
parameter_list|(
name|int
name|start
parameter_list|)
block|{
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getQuery ()
specifier|public
name|String
name|getQuery
parameter_list|()
block|{
return|return
name|query
return|;
block|}
DECL|method|getLimit ()
specifier|public
name|int
name|getLimit
parameter_list|()
block|{
return|return
name|limit
return|;
block|}
DECL|method|getStart ()
specifier|public
name|int
name|getStart
parameter_list|()
block|{
return|return
name|start
return|;
block|}
block|}
comment|/**    * A default implementation which allows source compatibility when adding new methods to the    * interface.    */
DECL|class|NotImplemented
class|class
name|NotImplemented
implements|implements
name|Projects
block|{
annotation|@
name|Override
DECL|method|name (String name)
specifier|public
name|ProjectApi
name|name
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|RestApiException
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|create (ProjectInput in)
specifier|public
name|ProjectApi
name|create
parameter_list|(
name|ProjectInput
name|in
parameter_list|)
throws|throws
name|RestApiException
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|create (String name)
specifier|public
name|ProjectApi
name|create
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|RestApiException
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|list ()
specifier|public
name|ListRequest
name|list
parameter_list|()
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|query ()
specifier|public
name|QueryRequest
name|query
parameter_list|()
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|query (String query)
specifier|public
name|QueryRequest
name|query
parameter_list|(
name|String
name|query
parameter_list|)
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
block|}
block|}
end_interface

end_unit

