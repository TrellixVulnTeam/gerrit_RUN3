begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2017 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.restapi.group
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|restapi
operator|.
name|group
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
name|Strings
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
name|extensions
operator|.
name|client
operator|.
name|ListGroupsOption
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
name|GroupInfo
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
name|BadRequestException
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
name|MethodNotAllowedException
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
name|RestReadView
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
name|TopLevelResource
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
name|index
operator|.
name|query
operator|.
name|QueryParseException
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
name|index
operator|.
name|query
operator|.
name|QueryResult
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
name|group
operator|.
name|InternalGroup
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
name|group
operator|.
name|InternalGroupDescription
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
name|permissions
operator|.
name|PermissionBackendException
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
name|group
operator|.
name|GroupQueryBuilder
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
name|group
operator|.
name|GroupQueryProcessor
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
name|Provider
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
name|EnumSet
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
name|org
operator|.
name|kohsuke
operator|.
name|args4j
operator|.
name|Option
import|;
end_import

begin_class
DECL|class|QueryGroups
specifier|public
class|class
name|QueryGroups
implements|implements
name|RestReadView
argument_list|<
name|TopLevelResource
argument_list|>
block|{
DECL|field|queryBuilder
specifier|private
specifier|final
name|GroupQueryBuilder
name|queryBuilder
decl_stmt|;
DECL|field|queryProcessorProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|GroupQueryProcessor
argument_list|>
name|queryProcessorProvider
decl_stmt|;
DECL|field|json
specifier|private
specifier|final
name|GroupJson
name|json
decl_stmt|;
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
DECL|field|options
specifier|private
name|EnumSet
argument_list|<
name|ListGroupsOption
argument_list|>
name|options
init|=
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|ListGroupsOption
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// TODO(ekempin): --query in ListGroups is marked as deprecated, once it is
comment|// removed we want to rename --query2 to --query here.
comment|/** --query (-q) is already used by {@link ListGroups} */
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--query2"
argument_list|,
name|aliases
operator|=
block|{
literal|"-q2"
block|}
argument_list|,
name|usage
operator|=
literal|"group query"
argument_list|)
DECL|method|setQuery (String query)
specifier|public
name|void
name|setQuery
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
block|}
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
literal|"maximum number of groups to list"
argument_list|)
DECL|method|setLimit (int limit)
specifier|public
name|void
name|setLimit
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
block|}
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--start"
argument_list|,
name|aliases
operator|=
block|{
literal|"-S"
block|}
argument_list|,
name|metaVar
operator|=
literal|"CNT"
argument_list|,
name|usage
operator|=
literal|"number of groups to skip"
argument_list|)
DECL|method|setStart (int start)
specifier|public
name|void
name|setStart
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
block|}
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"-o"
argument_list|,
name|usage
operator|=
literal|"Output options per group"
argument_list|)
DECL|method|addOption (ListGroupsOption o)
specifier|public
name|void
name|addOption
parameter_list|(
name|ListGroupsOption
name|o
parameter_list|)
block|{
name|options
operator|.
name|add
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"-O"
argument_list|,
name|usage
operator|=
literal|"Output option flags, in hex"
argument_list|)
DECL|method|setOptionFlagsHex (String hex)
specifier|public
name|void
name|setOptionFlagsHex
parameter_list|(
name|String
name|hex
parameter_list|)
block|{
name|options
operator|.
name|addAll
argument_list|(
name|ListGroupsOption
operator|.
name|fromBits
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|hex
argument_list|,
literal|16
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Inject
DECL|method|QueryGroups ( GroupQueryBuilder queryBuilder, Provider<GroupQueryProcessor> queryProcessorProvider, GroupJson json)
specifier|protected
name|QueryGroups
parameter_list|(
name|GroupQueryBuilder
name|queryBuilder
parameter_list|,
name|Provider
argument_list|<
name|GroupQueryProcessor
argument_list|>
name|queryProcessorProvider
parameter_list|,
name|GroupJson
name|json
parameter_list|)
block|{
name|this
operator|.
name|queryBuilder
operator|=
name|queryBuilder
expr_stmt|;
name|this
operator|.
name|queryProcessorProvider
operator|=
name|queryProcessorProvider
expr_stmt|;
name|this
operator|.
name|json
operator|=
name|json
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (TopLevelResource resource)
specifier|public
name|List
argument_list|<
name|GroupInfo
argument_list|>
name|apply
parameter_list|(
name|TopLevelResource
name|resource
parameter_list|)
throws|throws
name|BadRequestException
throws|,
name|MethodNotAllowedException
throws|,
name|OrmException
throws|,
name|PermissionBackendException
block|{
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|query
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"missing query field"
argument_list|)
throw|;
block|}
name|GroupQueryProcessor
name|queryProcessor
init|=
name|queryProcessorProvider
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|queryProcessor
operator|.
name|isDisabled
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|MethodNotAllowedException
argument_list|(
literal|"query disabled"
argument_list|)
throw|;
block|}
if|if
condition|(
name|start
operator|!=
literal|0
condition|)
block|{
name|queryProcessor
operator|.
name|setStart
argument_list|(
name|start
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|limit
operator|!=
literal|0
condition|)
block|{
name|queryProcessor
operator|.
name|setUserProvidedLimit
argument_list|(
name|limit
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|QueryResult
argument_list|<
name|InternalGroup
argument_list|>
name|result
init|=
name|queryProcessor
operator|.
name|query
argument_list|(
name|queryBuilder
operator|.
name|parse
argument_list|(
name|query
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|InternalGroup
argument_list|>
name|groups
init|=
name|result
operator|.
name|entities
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|GroupInfo
argument_list|>
name|groupInfos
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|groups
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|json
operator|.
name|addOptions
argument_list|(
name|options
argument_list|)
expr_stmt|;
for|for
control|(
name|InternalGroup
name|group
range|:
name|groups
control|)
block|{
name|groupInfos
operator|.
name|add
argument_list|(
name|json
operator|.
name|format
argument_list|(
operator|new
name|InternalGroupDescription
argument_list|(
name|group
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|groupInfos
operator|.
name|isEmpty
argument_list|()
operator|&&
name|result
operator|.
name|more
argument_list|()
condition|)
block|{
name|groupInfos
operator|.
name|get
argument_list|(
name|groupInfos
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|_moreGroups
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|groupInfos
return|;
block|}
catch|catch
parameter_list|(
name|QueryParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

