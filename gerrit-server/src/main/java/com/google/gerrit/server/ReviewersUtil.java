begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2016 The Android Open Source Project
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
DECL|package|com.google.gerrit.server
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
operator|.
name|toList
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
name|extensions
operator|.
name|common
operator|.
name|GroupBaseInfo
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
name|SuggestedReviewerInfo
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
name|Url
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
name|metrics
operator|.
name|Description
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
name|metrics
operator|.
name|Description
operator|.
name|Units
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
name|metrics
operator|.
name|MetricMaker
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
name|metrics
operator|.
name|Timer0
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
name|reviewdb
operator|.
name|client
operator|.
name|AccountExternalId
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
name|account
operator|.
name|AccountCache
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
name|AccountControl
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
name|account
operator|.
name|GroupBackend
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
name|GroupMembers
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
name|change
operator|.
name|PostReviewers
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
name|change
operator|.
name|SuggestReviewers
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
name|index
operator|.
name|account
operator|.
name|AccountIndex
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
name|index
operator|.
name|account
operator|.
name|AccountIndexCollection
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
name|notedb
operator|.
name|ChangeNotes
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
name|gerrit
operator|.
name|server
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
name|server
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Singleton
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
name|EnumSet
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
name|Objects
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
DECL|class|ReviewersUtil
specifier|public
class|class
name|ReviewersUtil
block|{
annotation|@
name|Singleton
DECL|class|Metrics
specifier|private
specifier|static
class|class
name|Metrics
block|{
DECL|field|queryAccountsLatency
specifier|final
name|Timer0
name|queryAccountsLatency
decl_stmt|;
DECL|field|recommendAccountsLatency
specifier|final
name|Timer0
name|recommendAccountsLatency
decl_stmt|;
DECL|field|loadAccountsLatency
specifier|final
name|Timer0
name|loadAccountsLatency
decl_stmt|;
DECL|field|queryGroupsLatency
specifier|final
name|Timer0
name|queryGroupsLatency
decl_stmt|;
annotation|@
name|Inject
DECL|method|Metrics (MetricMaker metricMaker)
name|Metrics
parameter_list|(
name|MetricMaker
name|metricMaker
parameter_list|)
block|{
name|queryAccountsLatency
operator|=
name|metricMaker
operator|.
name|newTimer
argument_list|(
literal|"reviewer_suggestion/query_accounts"
argument_list|,
operator|new
name|Description
argument_list|(
literal|"Latency for querying accounts for reviewer suggestion"
argument_list|)
operator|.
name|setCumulative
argument_list|()
operator|.
name|setUnit
argument_list|(
name|Units
operator|.
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|recommendAccountsLatency
operator|=
name|metricMaker
operator|.
name|newTimer
argument_list|(
literal|"reviewer_suggestion/recommend_accounts"
argument_list|,
operator|new
name|Description
argument_list|(
literal|"Latency for recommending accounts for reviewer suggestion"
argument_list|)
operator|.
name|setCumulative
argument_list|()
operator|.
name|setUnit
argument_list|(
name|Units
operator|.
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|loadAccountsLatency
operator|=
name|metricMaker
operator|.
name|newTimer
argument_list|(
literal|"reviewer_suggestion/load_accounts"
argument_list|,
operator|new
name|Description
argument_list|(
literal|"Latency for loading accounts for reviewer suggestion"
argument_list|)
operator|.
name|setCumulative
argument_list|()
operator|.
name|setUnit
argument_list|(
name|Units
operator|.
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|queryGroupsLatency
operator|=
name|metricMaker
operator|.
name|newTimer
argument_list|(
literal|"reviewer_suggestion/query_groups"
argument_list|,
operator|new
name|Description
argument_list|(
literal|"Latency for querying groups for reviewer suggestion"
argument_list|)
operator|.
name|setCumulative
argument_list|()
operator|.
name|setUnit
argument_list|(
name|Units
operator|.
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|MAX_SUFFIX
specifier|private
specifier|static
specifier|final
name|String
name|MAX_SUFFIX
init|=
literal|"\u9fa5"
decl_stmt|;
comment|// Generate a candidate list at 3x the size of what the user wants to see to
comment|// give the ranking algorithm a good set of candidates it can work with
DECL|field|CANDIDATE_LIST_MULTIPLIER
specifier|private
specifier|static
specifier|final
name|int
name|CANDIDATE_LIST_MULTIPLIER
init|=
literal|3
decl_stmt|;
DECL|field|accountCache
specifier|private
specifier|final
name|AccountCache
name|accountCache
decl_stmt|;
DECL|field|accountControl
specifier|private
specifier|final
name|AccountControl
name|accountControl
decl_stmt|;
DECL|field|accountIndexes
specifier|private
specifier|final
name|AccountIndexCollection
name|accountIndexes
decl_stmt|;
DECL|field|accountLoader
specifier|private
specifier|final
name|AccountLoader
name|accountLoader
decl_stmt|;
DECL|field|accountQueryBuilder
specifier|private
specifier|final
name|AccountQueryBuilder
name|accountQueryBuilder
decl_stmt|;
DECL|field|accountQueryProcessor
specifier|private
specifier|final
name|AccountQueryProcessor
name|accountQueryProcessor
decl_stmt|;
DECL|field|groupBackend
specifier|private
specifier|final
name|GroupBackend
name|groupBackend
decl_stmt|;
DECL|field|groupMembersFactory
specifier|private
specifier|final
name|GroupMembers
operator|.
name|Factory
name|groupMembersFactory
decl_stmt|;
DECL|field|currentUser
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|currentUser
decl_stmt|;
DECL|field|dbProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|dbProvider
decl_stmt|;
DECL|field|reviewerRecommender
specifier|private
specifier|final
name|ReviewerRecommender
name|reviewerRecommender
decl_stmt|;
DECL|field|metrics
specifier|private
specifier|final
name|Metrics
name|metrics
decl_stmt|;
annotation|@
name|Inject
DECL|method|ReviewersUtil (AccountCache accountCache, AccountControl.Factory accountControlFactory, AccountIndexCollection accountIndexes, AccountLoader.Factory accountLoaderFactory, AccountQueryBuilder accountQueryBuilder, AccountQueryProcessor accountQueryProcessor, GroupBackend groupBackend, GroupMembers.Factory groupMembersFactory, Provider<CurrentUser> currentUser, Provider<ReviewDb> dbProvider, ReviewerRecommender reviewerRecommender, Metrics metrics)
name|ReviewersUtil
parameter_list|(
name|AccountCache
name|accountCache
parameter_list|,
name|AccountControl
operator|.
name|Factory
name|accountControlFactory
parameter_list|,
name|AccountIndexCollection
name|accountIndexes
parameter_list|,
name|AccountLoader
operator|.
name|Factory
name|accountLoaderFactory
parameter_list|,
name|AccountQueryBuilder
name|accountQueryBuilder
parameter_list|,
name|AccountQueryProcessor
name|accountQueryProcessor
parameter_list|,
name|GroupBackend
name|groupBackend
parameter_list|,
name|GroupMembers
operator|.
name|Factory
name|groupMembersFactory
parameter_list|,
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|currentUser
parameter_list|,
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|dbProvider
parameter_list|,
name|ReviewerRecommender
name|reviewerRecommender
parameter_list|,
name|Metrics
name|metrics
parameter_list|)
block|{
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
name|SECONDARY_EMAILS
argument_list|)
decl_stmt|;
name|fillOptions
operator|.
name|addAll
argument_list|(
name|AccountLoader
operator|.
name|DETAILED_OPTIONS
argument_list|)
expr_stmt|;
name|this
operator|.
name|accountCache
operator|=
name|accountCache
expr_stmt|;
name|this
operator|.
name|accountControl
operator|=
name|accountControlFactory
operator|.
name|get
argument_list|()
expr_stmt|;
name|this
operator|.
name|accountIndexes
operator|=
name|accountIndexes
expr_stmt|;
name|this
operator|.
name|accountLoader
operator|=
name|accountLoaderFactory
operator|.
name|create
argument_list|(
name|fillOptions
argument_list|)
expr_stmt|;
name|this
operator|.
name|accountQueryBuilder
operator|=
name|accountQueryBuilder
expr_stmt|;
name|this
operator|.
name|accountQueryProcessor
operator|=
name|accountQueryProcessor
expr_stmt|;
name|this
operator|.
name|currentUser
operator|=
name|currentUser
expr_stmt|;
name|this
operator|.
name|dbProvider
operator|=
name|dbProvider
expr_stmt|;
name|this
operator|.
name|groupBackend
operator|=
name|groupBackend
expr_stmt|;
name|this
operator|.
name|groupMembersFactory
operator|=
name|groupMembersFactory
expr_stmt|;
name|this
operator|.
name|reviewerRecommender
operator|=
name|reviewerRecommender
expr_stmt|;
name|this
operator|.
name|metrics
operator|=
name|metrics
expr_stmt|;
block|}
DECL|interface|VisibilityControl
specifier|public
interface|interface
name|VisibilityControl
block|{
DECL|method|isVisibleTo (Account.Id account)
name|boolean
name|isVisibleTo
parameter_list|(
name|Account
operator|.
name|Id
name|account
parameter_list|)
throws|throws
name|OrmException
function_decl|;
block|}
DECL|method|suggestReviewers (ChangeNotes changeNotes, SuggestReviewers suggestReviewers, ProjectControl projectControl, VisibilityControl visibilityControl, boolean excludeGroups)
specifier|public
name|List
argument_list|<
name|SuggestedReviewerInfo
argument_list|>
name|suggestReviewers
parameter_list|(
name|ChangeNotes
name|changeNotes
parameter_list|,
name|SuggestReviewers
name|suggestReviewers
parameter_list|,
name|ProjectControl
name|projectControl
parameter_list|,
name|VisibilityControl
name|visibilityControl
parameter_list|,
name|boolean
name|excludeGroups
parameter_list|)
throws|throws
name|IOException
throws|,
name|OrmException
block|{
name|String
name|query
init|=
name|suggestReviewers
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|int
name|limit
init|=
name|suggestReviewers
operator|.
name|getLimit
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|suggestReviewers
operator|.
name|getSuggestAccounts
argument_list|()
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
name|List
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|candidateList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|query
argument_list|)
condition|)
block|{
name|candidateList
operator|=
name|suggestAccounts
argument_list|(
name|suggestReviewers
argument_list|,
name|visibilityControl
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|sortedRecommendations
init|=
name|recommendAccounts
argument_list|(
name|changeNotes
argument_list|,
name|suggestReviewers
argument_list|,
name|projectControl
argument_list|,
name|candidateList
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|SuggestedReviewerInfo
argument_list|>
name|suggestedReviewer
init|=
name|loadAccounts
argument_list|(
name|sortedRecommendations
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|excludeGroups
operator|&&
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|query
argument_list|)
condition|)
block|{
comment|// Add groups at the end as individual accounts are usually more
comment|// important.
name|suggestedReviewer
operator|.
name|addAll
argument_list|(
name|suggestAccountGroups
argument_list|(
name|suggestReviewers
argument_list|,
name|projectControl
argument_list|,
name|visibilityControl
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|suggestedReviewer
operator|.
name|size
argument_list|()
operator|<=
name|limit
condition|)
block|{
return|return
name|suggestedReviewer
return|;
block|}
return|return
name|suggestedReviewer
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|limit
argument_list|)
return|;
block|}
DECL|method|suggestAccounts (SuggestReviewers suggestReviewers, VisibilityControl visibilityControl)
specifier|private
name|List
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|suggestAccounts
parameter_list|(
name|SuggestReviewers
name|suggestReviewers
parameter_list|,
name|VisibilityControl
name|visibilityControl
parameter_list|)
throws|throws
name|OrmException
block|{
try|try
init|(
name|Timer0
operator|.
name|Context
name|ctx
init|=
name|metrics
operator|.
name|queryAccountsLatency
operator|.
name|start
argument_list|()
init|)
block|{
name|AccountIndex
name|searchIndex
init|=
name|accountIndexes
operator|.
name|getSearchIndex
argument_list|()
decl_stmt|;
if|if
condition|(
name|searchIndex
operator|!=
literal|null
condition|)
block|{
return|return
name|suggestAccountsFromIndex
argument_list|(
name|suggestReviewers
argument_list|)
return|;
block|}
return|return
name|suggestAccountsFromDb
argument_list|(
name|suggestReviewers
argument_list|,
name|visibilityControl
argument_list|)
return|;
block|}
block|}
DECL|method|suggestAccountsFromIndex ( SuggestReviewers suggestReviewers)
specifier|private
name|List
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|suggestAccountsFromIndex
parameter_list|(
name|SuggestReviewers
name|suggestReviewers
parameter_list|)
throws|throws
name|OrmException
block|{
try|try
block|{
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|matches
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|QueryResult
argument_list|<
name|AccountState
argument_list|>
name|result
init|=
name|accountQueryProcessor
operator|.
name|setLimit
argument_list|(
name|suggestReviewers
operator|.
name|getLimit
argument_list|()
operator|*
name|CANDIDATE_LIST_MULTIPLIER
argument_list|)
operator|.
name|query
argument_list|(
name|accountQueryBuilder
operator|.
name|defaultQuery
argument_list|(
name|suggestReviewers
operator|.
name|getQuery
argument_list|()
argument_list|)
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
name|getAccount
argument_list|()
operator|.
name|getId
argument_list|()
decl_stmt|;
name|matches
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|matches
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|QueryParseException
name|e
parameter_list|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
block|}
DECL|method|suggestAccountsFromDb ( SuggestReviewers suggestReviewers, VisibilityControl visibilityControl)
specifier|private
name|List
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|suggestAccountsFromDb
parameter_list|(
name|SuggestReviewers
name|suggestReviewers
parameter_list|,
name|VisibilityControl
name|visibilityControl
parameter_list|)
throws|throws
name|OrmException
block|{
name|String
name|query
init|=
name|suggestReviewers
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|int
name|limit
init|=
name|suggestReviewers
operator|.
name|getLimit
argument_list|()
operator|*
name|CANDIDATE_LIST_MULTIPLIER
decl_stmt|;
name|String
name|a
init|=
name|query
decl_stmt|;
name|String
name|b
init|=
name|a
operator|+
name|MAX_SUFFIX
decl_stmt|;
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|r
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Account
name|p
range|:
name|dbProvider
operator|.
name|get
argument_list|()
operator|.
name|accounts
argument_list|()
operator|.
name|suggestByFullName
argument_list|(
name|a
argument_list|,
name|b
argument_list|,
name|limit
argument_list|)
control|)
block|{
if|if
condition|(
name|p
operator|.
name|isActive
argument_list|()
condition|)
block|{
name|addSuggestion
argument_list|(
name|r
argument_list|,
name|p
operator|.
name|getId
argument_list|()
argument_list|,
name|visibilityControl
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|r
operator|.
name|size
argument_list|()
operator|<
name|limit
condition|)
block|{
for|for
control|(
name|Account
name|p
range|:
name|dbProvider
operator|.
name|get
argument_list|()
operator|.
name|accounts
argument_list|()
operator|.
name|suggestByPreferredEmail
argument_list|(
name|a
argument_list|,
name|b
argument_list|,
name|limit
operator|-
name|r
operator|.
name|size
argument_list|()
argument_list|)
control|)
block|{
if|if
condition|(
name|p
operator|.
name|isActive
argument_list|()
condition|)
block|{
name|addSuggestion
argument_list|(
name|r
argument_list|,
name|p
operator|.
name|getId
argument_list|()
argument_list|,
name|visibilityControl
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|r
operator|.
name|size
argument_list|()
operator|<
name|limit
condition|)
block|{
for|for
control|(
name|AccountExternalId
name|e
range|:
name|dbProvider
operator|.
name|get
argument_list|()
operator|.
name|accountExternalIds
argument_list|()
operator|.
name|suggestByEmailAddress
argument_list|(
name|a
argument_list|,
name|b
argument_list|,
name|limit
operator|-
name|r
operator|.
name|size
argument_list|()
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|r
operator|.
name|contains
argument_list|(
name|e
operator|.
name|getAccountId
argument_list|()
argument_list|)
condition|)
block|{
name|Account
name|p
init|=
name|accountCache
operator|.
name|get
argument_list|(
name|e
operator|.
name|getAccountId
argument_list|()
argument_list|)
operator|.
name|getAccount
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|.
name|isActive
argument_list|()
condition|)
block|{
name|addSuggestion
argument_list|(
name|r
argument_list|,
name|p
operator|.
name|getId
argument_list|()
argument_list|,
name|visibilityControl
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|r
argument_list|)
return|;
block|}
DECL|method|addSuggestion (Set<Account.Id> map, Account.Id account, VisibilityControl visibilityControl)
specifier|private
name|boolean
name|addSuggestion
parameter_list|(
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|map
parameter_list|,
name|Account
operator|.
name|Id
name|account
parameter_list|,
name|VisibilityControl
name|visibilityControl
parameter_list|)
throws|throws
name|OrmException
block|{
if|if
condition|(
operator|!
name|map
operator|.
name|contains
argument_list|(
name|account
argument_list|)
comment|// Can the suggestion see the change?
operator|&&
name|visibilityControl
operator|.
name|isVisibleTo
argument_list|(
name|account
argument_list|)
comment|// Can the current user see the account?
operator|&&
name|accountControl
operator|.
name|canSee
argument_list|(
name|account
argument_list|)
condition|)
block|{
name|map
operator|.
name|add
argument_list|(
name|account
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|recommendAccounts (ChangeNotes changeNotes, SuggestReviewers suggestReviewers, ProjectControl projectControl, List<Account.Id> candidateList)
specifier|private
name|List
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|recommendAccounts
parameter_list|(
name|ChangeNotes
name|changeNotes
parameter_list|,
name|SuggestReviewers
name|suggestReviewers
parameter_list|,
name|ProjectControl
name|projectControl
parameter_list|,
name|List
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|candidateList
parameter_list|)
throws|throws
name|OrmException
block|{
try|try
init|(
name|Timer0
operator|.
name|Context
name|ctx
init|=
name|metrics
operator|.
name|recommendAccountsLatency
operator|.
name|start
argument_list|()
init|)
block|{
return|return
name|reviewerRecommender
operator|.
name|suggestReviewers
argument_list|(
name|changeNotes
argument_list|,
name|suggestReviewers
argument_list|,
name|projectControl
argument_list|,
name|candidateList
argument_list|)
return|;
block|}
block|}
DECL|method|loadAccounts (List<Account.Id> accountIds)
specifier|private
name|List
argument_list|<
name|SuggestedReviewerInfo
argument_list|>
name|loadAccounts
parameter_list|(
name|List
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|accountIds
parameter_list|)
throws|throws
name|OrmException
block|{
try|try
init|(
name|Timer0
operator|.
name|Context
name|ctx
init|=
name|metrics
operator|.
name|loadAccountsLatency
operator|.
name|start
argument_list|()
init|)
block|{
name|List
argument_list|<
name|SuggestedReviewerInfo
argument_list|>
name|reviewer
init|=
name|accountIds
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|accountLoader
operator|::
name|get
argument_list|)
operator|.
name|filter
argument_list|(
name|Objects
operator|::
name|nonNull
argument_list|)
operator|.
name|map
argument_list|(
name|a
lambda|->
block|{
name|SuggestedReviewerInfo
name|info
init|=
operator|new
name|SuggestedReviewerInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|account
operator|=
name|a
expr_stmt|;
name|info
operator|.
name|count
operator|=
literal|1
expr_stmt|;
return|return
name|info
return|;
block|}
argument_list|)
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|accountLoader
operator|.
name|fill
argument_list|()
expr_stmt|;
return|return
name|reviewer
return|;
block|}
block|}
DECL|method|suggestAccountGroups ( SuggestReviewers suggestReviewers, ProjectControl projectControl, VisibilityControl visibilityControl)
specifier|private
name|List
argument_list|<
name|SuggestedReviewerInfo
argument_list|>
name|suggestAccountGroups
parameter_list|(
name|SuggestReviewers
name|suggestReviewers
parameter_list|,
name|ProjectControl
name|projectControl
parameter_list|,
name|VisibilityControl
name|visibilityControl
parameter_list|)
throws|throws
name|OrmException
throws|,
name|IOException
block|{
try|try
init|(
name|Timer0
operator|.
name|Context
name|ctx
init|=
name|metrics
operator|.
name|queryGroupsLatency
operator|.
name|start
argument_list|()
init|)
block|{
name|List
argument_list|<
name|SuggestedReviewerInfo
argument_list|>
name|groups
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|GroupReference
name|g
range|:
name|suggestAccountGroups
argument_list|(
name|suggestReviewers
argument_list|,
name|projectControl
argument_list|)
control|)
block|{
name|GroupAsReviewer
name|result
init|=
name|suggestGroupAsReviewer
argument_list|(
name|suggestReviewers
argument_list|,
name|projectControl
operator|.
name|getProject
argument_list|()
argument_list|,
name|g
argument_list|,
name|visibilityControl
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|.
name|allowed
operator|||
name|result
operator|.
name|allowedWithConfirmation
condition|)
block|{
name|GroupBaseInfo
name|info
init|=
operator|new
name|GroupBaseInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|id
operator|=
name|Url
operator|.
name|encode
argument_list|(
name|g
operator|.
name|getUUID
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|name
operator|=
name|g
operator|.
name|getName
argument_list|()
expr_stmt|;
name|SuggestedReviewerInfo
name|suggestedReviewerInfo
init|=
operator|new
name|SuggestedReviewerInfo
argument_list|()
decl_stmt|;
name|suggestedReviewerInfo
operator|.
name|group
operator|=
name|info
expr_stmt|;
name|suggestedReviewerInfo
operator|.
name|count
operator|=
name|result
operator|.
name|size
expr_stmt|;
if|if
condition|(
name|result
operator|.
name|allowedWithConfirmation
condition|)
block|{
name|suggestedReviewerInfo
operator|.
name|confirm
operator|=
literal|true
expr_stmt|;
block|}
name|groups
operator|.
name|add
argument_list|(
name|suggestedReviewerInfo
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|groups
return|;
block|}
block|}
DECL|method|suggestAccountGroups ( SuggestReviewers suggestReviewers, ProjectControl ctl)
specifier|private
name|List
argument_list|<
name|GroupReference
argument_list|>
name|suggestAccountGroups
parameter_list|(
name|SuggestReviewers
name|suggestReviewers
parameter_list|,
name|ProjectControl
name|ctl
parameter_list|)
block|{
return|return
name|Lists
operator|.
name|newArrayList
argument_list|(
name|Iterables
operator|.
name|limit
argument_list|(
name|groupBackend
operator|.
name|suggest
argument_list|(
name|suggestReviewers
operator|.
name|getQuery
argument_list|()
argument_list|,
name|ctl
argument_list|)
argument_list|,
name|suggestReviewers
operator|.
name|getLimit
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|class|GroupAsReviewer
specifier|private
specifier|static
class|class
name|GroupAsReviewer
block|{
DECL|field|allowed
name|boolean
name|allowed
decl_stmt|;
DECL|field|allowedWithConfirmation
name|boolean
name|allowedWithConfirmation
decl_stmt|;
DECL|field|size
name|int
name|size
decl_stmt|;
block|}
DECL|method|suggestGroupAsReviewer ( SuggestReviewers suggestReviewers, Project project, GroupReference group, VisibilityControl visibilityControl)
specifier|private
name|GroupAsReviewer
name|suggestGroupAsReviewer
parameter_list|(
name|SuggestReviewers
name|suggestReviewers
parameter_list|,
name|Project
name|project
parameter_list|,
name|GroupReference
name|group
parameter_list|,
name|VisibilityControl
name|visibilityControl
parameter_list|)
throws|throws
name|OrmException
throws|,
name|IOException
block|{
name|GroupAsReviewer
name|result
init|=
operator|new
name|GroupAsReviewer
argument_list|()
decl_stmt|;
name|int
name|maxAllowed
init|=
name|suggestReviewers
operator|.
name|getMaxAllowed
argument_list|()
decl_stmt|;
name|int
name|maxAllowedWithoutConfirmation
init|=
name|suggestReviewers
operator|.
name|getMaxAllowedWithoutConfirmation
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|PostReviewers
operator|.
name|isLegalReviewerGroup
argument_list|(
name|group
operator|.
name|getUUID
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|result
return|;
block|}
try|try
block|{
name|Set
argument_list|<
name|Account
argument_list|>
name|members
init|=
name|groupMembersFactory
operator|.
name|create
argument_list|(
name|currentUser
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|listAccounts
argument_list|(
name|group
operator|.
name|getUUID
argument_list|()
argument_list|,
name|project
operator|.
name|getNameKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|members
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|result
return|;
block|}
name|result
operator|.
name|size
operator|=
name|members
operator|.
name|size
argument_list|()
expr_stmt|;
if|if
condition|(
name|maxAllowed
operator|>
literal|0
operator|&&
name|result
operator|.
name|size
operator|>
name|maxAllowed
condition|)
block|{
return|return
name|result
return|;
block|}
name|boolean
name|needsConfirmation
init|=
name|result
operator|.
name|size
operator|>
name|maxAllowedWithoutConfirmation
decl_stmt|;
comment|// require that at least one member in the group can see the change
for|for
control|(
name|Account
name|account
range|:
name|members
control|)
block|{
if|if
condition|(
name|visibilityControl
operator|.
name|isVisibleTo
argument_list|(
name|account
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|needsConfirmation
condition|)
block|{
name|result
operator|.
name|allowedWithConfirmation
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|result
operator|.
name|allowed
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|NoSuchGroupException
name|e
parameter_list|)
block|{
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchProjectException
name|e
parameter_list|)
block|{
return|return
name|result
return|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

