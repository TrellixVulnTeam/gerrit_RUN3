begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2014 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.restapi.account
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
name|account
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
name|ImmutableList
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
name|ListAccountsOption
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
name|ListOption
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
name|AccountInfo
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
name|AccountVisibility
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
name|AuthException
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
name|Response
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
name|Predicate
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
name|reviewdb
operator|.
name|client
operator|.
name|Account
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
name|AccountDirectory
operator|.
name|FillOptions
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
name|AccountInfoComparator
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
name|AccountLoader
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
name|AccountState
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
name|GerritServerConfig
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
name|GlobalPermission
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
name|PermissionBackend
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
name|account
operator|.
name|AccountPredicates
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
name|account
operator|.
name|AccountQueryBuilder
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
name|account
operator|.
name|AccountQueryProcessor
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
name|EnumSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
name|kohsuke
operator|.
name|args4j
operator|.
name|Option
import|;
end_import

begin_class
DECL|class|QueryAccounts
specifier|public
class|class
name|QueryAccounts
implements|implements
name|RestReadView
argument_list|<
name|TopLevelResource
argument_list|>
block|{
DECL|field|MAX_SUGGEST_RESULTS
specifier|private
specifier|static
specifier|final
name|int
name|MAX_SUGGEST_RESULTS
init|=
literal|100
decl_stmt|;
DECL|field|permissionBackend
specifier|private
specifier|final
name|PermissionBackend
name|permissionBackend
decl_stmt|;
DECL|field|accountLoaderFactory
specifier|private
specifier|final
name|AccountLoader
operator|.
name|Factory
name|accountLoaderFactory
decl_stmt|;
DECL|field|queryBuilder
specifier|private
specifier|final
name|AccountQueryBuilder
name|queryBuilder
decl_stmt|;
DECL|field|queryProcessor
specifier|private
specifier|final
name|AccountQueryProcessor
name|queryProcessor
decl_stmt|;
DECL|field|suggestConfig
specifier|private
specifier|final
name|boolean
name|suggestConfig
decl_stmt|;
DECL|field|suggestFrom
specifier|private
specifier|final
name|int
name|suggestFrom
decl_stmt|;
DECL|field|accountLoader
specifier|private
name|AccountLoader
name|accountLoader
decl_stmt|;
DECL|field|suggest
specifier|private
name|boolean
name|suggest
decl_stmt|;
DECL|field|suggestLimit
specifier|private
name|int
name|suggestLimit
init|=
literal|10
decl_stmt|;
DECL|field|query
specifier|private
name|String
name|query
decl_stmt|;
DECL|field|start
specifier|private
name|Integer
name|start
decl_stmt|;
DECL|field|options
specifier|private
name|EnumSet
argument_list|<
name|ListAccountsOption
argument_list|>
name|options
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--suggest"
argument_list|,
name|metaVar
operator|=
literal|"SUGGEST"
argument_list|,
name|usage
operator|=
literal|"suggest users"
argument_list|)
DECL|method|setSuggest (boolean suggest)
specifier|public
name|void
name|setSuggest
parameter_list|(
name|boolean
name|suggest
parameter_list|)
block|{
name|this
operator|.
name|suggest
operator|=
name|suggest
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
literal|"maximum number of users to return"
argument_list|)
DECL|method|setLimit (int n)
specifier|public
name|void
name|setLimit
parameter_list|(
name|int
name|n
parameter_list|)
block|{
name|queryProcessor
operator|.
name|setUserProvidedLimit
argument_list|(
name|n
argument_list|)
expr_stmt|;
if|if
condition|(
name|n
operator|<
literal|0
condition|)
block|{
name|suggestLimit
operator|=
literal|10
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|n
operator|==
literal|0
condition|)
block|{
name|suggestLimit
operator|=
name|MAX_SUGGEST_RESULTS
expr_stmt|;
block|}
else|else
block|{
name|suggestLimit
operator|=
name|Math
operator|.
name|min
argument_list|(
name|n
argument_list|,
name|MAX_SUGGEST_RESULTS
argument_list|)
expr_stmt|;
block|}
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
literal|"Output options per account"
argument_list|)
DECL|method|addOption (ListAccountsOption o)
specifier|public
name|void
name|addOption
parameter_list|(
name|ListAccountsOption
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
name|ListOption
operator|.
name|fromBits
argument_list|(
name|ListAccountsOption
operator|.
name|class
argument_list|,
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
name|Option
argument_list|(
name|name
operator|=
literal|"--query"
argument_list|,
name|aliases
operator|=
block|{
literal|"-q"
block|}
argument_list|,
name|metaVar
operator|=
literal|"QUERY"
argument_list|,
name|usage
operator|=
literal|"match users"
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
literal|"Number of accounts to skip"
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
name|Inject
DECL|method|QueryAccounts ( PermissionBackend permissionBackend, AccountLoader.Factory accountLoaderFactory, AccountQueryBuilder queryBuilder, AccountQueryProcessor queryProcessor, @GerritServerConfig Config cfg)
name|QueryAccounts
parameter_list|(
name|PermissionBackend
name|permissionBackend
parameter_list|,
name|AccountLoader
operator|.
name|Factory
name|accountLoaderFactory
parameter_list|,
name|AccountQueryBuilder
name|queryBuilder
parameter_list|,
name|AccountQueryProcessor
name|queryProcessor
parameter_list|,
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|)
block|{
name|this
operator|.
name|permissionBackend
operator|=
name|permissionBackend
expr_stmt|;
name|this
operator|.
name|accountLoaderFactory
operator|=
name|accountLoaderFactory
expr_stmt|;
name|this
operator|.
name|queryBuilder
operator|=
name|queryBuilder
expr_stmt|;
name|this
operator|.
name|queryProcessor
operator|=
name|queryProcessor
expr_stmt|;
name|this
operator|.
name|suggestFrom
operator|=
name|cfg
operator|.
name|getInt
argument_list|(
literal|"suggest"
argument_list|,
literal|null
argument_list|,
literal|"from"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|options
operator|=
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|ListAccountsOption
operator|.
name|class
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"off"
operator|.
name|equalsIgnoreCase
argument_list|(
name|cfg
operator|.
name|getString
argument_list|(
literal|"suggest"
argument_list|,
literal|null
argument_list|,
literal|"accounts"
argument_list|)
argument_list|)
condition|)
block|{
name|suggestConfig
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|boolean
name|suggest
decl_stmt|;
try|try
block|{
name|AccountVisibility
name|av
init|=
name|cfg
operator|.
name|getEnum
argument_list|(
literal|"suggest"
argument_list|,
literal|null
argument_list|,
literal|"accounts"
argument_list|,
name|AccountVisibility
operator|.
name|ALL
argument_list|)
decl_stmt|;
name|suggest
operator|=
operator|(
name|av
operator|!=
name|AccountVisibility
operator|.
name|NONE
operator|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|err
parameter_list|)
block|{
name|suggest
operator|=
name|cfg
operator|.
name|getBoolean
argument_list|(
literal|"suggest"
argument_list|,
literal|null
argument_list|,
literal|"accounts"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|suggestConfig
operator|=
name|suggest
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|apply (TopLevelResource rsrc)
specifier|public
name|Response
argument_list|<
name|List
argument_list|<
name|AccountInfo
argument_list|>
argument_list|>
name|apply
parameter_list|(
name|TopLevelResource
name|rsrc
parameter_list|)
throws|throws
name|RestApiException
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
if|if
condition|(
name|suggest
operator|&&
operator|(
operator|!
name|suggestConfig
operator|||
name|query
operator|.
name|length
argument_list|()
operator|<
name|suggestFrom
operator|)
condition|)
block|{
return|return
name|Response
operator|.
name|ok
argument_list|(
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
return|;
block|}
name|Set
argument_list|<
name|FillOptions
argument_list|>
name|fillOptions
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|FillOptions
operator|.
name|ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|options
operator|.
name|contains
argument_list|(
name|ListAccountsOption
operator|.
name|DETAILS
argument_list|)
condition|)
block|{
name|fillOptions
operator|.
name|addAll
argument_list|(
name|AccountLoader
operator|.
name|DETAILED_OPTIONS
argument_list|)
expr_stmt|;
block|}
name|boolean
name|modifyAccountCapabilityChecked
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|options
operator|.
name|contains
argument_list|(
name|ListAccountsOption
operator|.
name|ALL_EMAILS
argument_list|)
condition|)
block|{
name|permissionBackend
operator|.
name|currentUser
argument_list|()
operator|.
name|check
argument_list|(
name|GlobalPermission
operator|.
name|MODIFY_ACCOUNT
argument_list|)
expr_stmt|;
name|modifyAccountCapabilityChecked
operator|=
literal|true
expr_stmt|;
name|fillOptions
operator|.
name|add
argument_list|(
name|FillOptions
operator|.
name|EMAIL
argument_list|)
expr_stmt|;
name|fillOptions
operator|.
name|add
argument_list|(
name|FillOptions
operator|.
name|SECONDARY_EMAILS
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|suggest
condition|)
block|{
name|fillOptions
operator|.
name|addAll
argument_list|(
name|AccountLoader
operator|.
name|DETAILED_OPTIONS
argument_list|)
expr_stmt|;
name|fillOptions
operator|.
name|add
argument_list|(
name|FillOptions
operator|.
name|EMAIL
argument_list|)
expr_stmt|;
if|if
condition|(
name|modifyAccountCapabilityChecked
condition|)
block|{
name|fillOptions
operator|.
name|add
argument_list|(
name|FillOptions
operator|.
name|SECONDARY_EMAILS
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|permissionBackend
operator|.
name|currentUser
argument_list|()
operator|.
name|check
argument_list|(
name|GlobalPermission
operator|.
name|MODIFY_ACCOUNT
argument_list|)
expr_stmt|;
name|fillOptions
operator|.
name|add
argument_list|(
name|FillOptions
operator|.
name|SECONDARY_EMAILS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AuthException
name|e
parameter_list|)
block|{
comment|// Do nothing.
block|}
block|}
block|}
name|accountLoader
operator|=
name|accountLoaderFactory
operator|.
name|create
argument_list|(
name|fillOptions
argument_list|)
expr_stmt|;
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
literal|null
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
name|Map
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|AccountInfo
argument_list|>
name|matches
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
try|try
block|{
name|Predicate
argument_list|<
name|AccountState
argument_list|>
name|queryPred
decl_stmt|;
if|if
condition|(
name|suggest
condition|)
block|{
name|queryPred
operator|=
name|queryBuilder
operator|.
name|defaultQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|queryProcessor
operator|.
name|setUserProvidedLimit
argument_list|(
name|suggestLimit
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|queryPred
operator|=
name|queryBuilder
operator|.
name|parse
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|AccountPredicates
operator|.
name|hasActive
argument_list|(
name|queryPred
argument_list|)
condition|)
block|{
comment|// if neither 'is:active' nor 'is:inactive' appears in the query only
comment|// active accounts should be queried
name|queryPred
operator|=
name|AccountPredicates
operator|.
name|andActive
argument_list|(
name|queryPred
argument_list|)
expr_stmt|;
block|}
name|QueryResult
argument_list|<
name|AccountState
argument_list|>
name|result
init|=
name|queryProcessor
operator|.
name|query
argument_list|(
name|queryPred
argument_list|)
decl_stmt|;
for|for
control|(
name|AccountState
name|accountState
range|:
name|result
operator|.
name|entities
argument_list|()
control|)
block|{
name|Account
operator|.
name|Id
name|id
init|=
name|accountState
operator|.
name|account
argument_list|()
operator|.
name|id
argument_list|()
decl_stmt|;
name|matches
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|accountLoader
operator|.
name|get
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|accountLoader
operator|.
name|fill
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|AccountInfo
argument_list|>
name|sorted
init|=
name|AccountInfoComparator
operator|.
name|ORDER_NULLS_LAST
operator|.
name|sortedCopy
argument_list|(
name|matches
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|sorted
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
name|sorted
operator|.
name|get
argument_list|(
name|sorted
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|_moreAccounts
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|Response
operator|.
name|ok
argument_list|(
name|sorted
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|QueryParseException
name|e
parameter_list|)
block|{
if|if
condition|(
name|suggest
condition|)
block|{
return|return
name|Response
operator|.
name|ok
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
return|;
block|}
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

