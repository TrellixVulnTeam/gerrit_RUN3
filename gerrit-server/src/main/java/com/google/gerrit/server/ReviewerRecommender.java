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
name|ReviewerStateInternal
operator|.
name|REVIEWER
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
name|ImmutableMap
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
name|extensions
operator|.
name|registration
operator|.
name|DynamicMap
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
name|PatchSetApproval
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
name|change
operator|.
name|ReviewerSuggestion
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
name|change
operator|.
name|SuggestedReviewer
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
name|git
operator|.
name|WorkQueue
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
name|change
operator|.
name|ChangeField
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
name|change
operator|.
name|ChangeData
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
name|change
operator|.
name|ChangeQueryBuilder
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
name|change
operator|.
name|InternalChangeQuery
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|mutable
operator|.
name|MutableDouble
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
name|HashMap
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutionException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Future
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Stream
import|;
end_import

begin_class
DECL|class|ReviewerRecommender
specifier|public
class|class
name|ReviewerRecommender
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
name|ReviewersUtil
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|BASE_REVIEWER_WEIGHT
specifier|private
specifier|static
specifier|final
name|double
name|BASE_REVIEWER_WEIGHT
init|=
literal|10
decl_stmt|;
DECL|field|BASE_OWNER_WEIGHT
specifier|private
specifier|static
specifier|final
name|double
name|BASE_OWNER_WEIGHT
init|=
literal|1
decl_stmt|;
DECL|field|BASE_COMMENT_WEIGHT
specifier|private
specifier|static
specifier|final
name|double
name|BASE_COMMENT_WEIGHT
init|=
literal|0.5
decl_stmt|;
DECL|field|WEIGHTS
specifier|private
specifier|static
specifier|final
name|double
index|[]
name|WEIGHTS
init|=
operator|new
name|double
index|[]
block|{
name|BASE_REVIEWER_WEIGHT
block|,
name|BASE_OWNER_WEIGHT
block|,
name|BASE_COMMENT_WEIGHT
block|,}
decl_stmt|;
DECL|field|PLUGIN_QUERY_TIMEOUT
specifier|private
specifier|static
specifier|final
name|long
name|PLUGIN_QUERY_TIMEOUT
init|=
literal|500
decl_stmt|;
comment|//ms
DECL|field|changeQueryBuilder
specifier|private
specifier|final
name|ChangeQueryBuilder
name|changeQueryBuilder
decl_stmt|;
DECL|field|config
specifier|private
specifier|final
name|Config
name|config
decl_stmt|;
DECL|field|reviewerSuggestionPluginMap
specifier|private
specifier|final
name|DynamicMap
argument_list|<
name|ReviewerSuggestion
argument_list|>
name|reviewerSuggestionPluginMap
decl_stmt|;
DECL|field|internalChangeQuery
specifier|private
specifier|final
name|InternalChangeQuery
name|internalChangeQuery
decl_stmt|;
DECL|field|workQueue
specifier|private
specifier|final
name|WorkQueue
name|workQueue
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
DECL|field|approvalsUtil
specifier|private
specifier|final
name|ApprovalsUtil
name|approvalsUtil
decl_stmt|;
annotation|@
name|Inject
DECL|method|ReviewerRecommender (ChangeQueryBuilder changeQueryBuilder, DynamicMap<ReviewerSuggestion> reviewerSuggestionPluginMap, InternalChangeQuery internalChangeQuery, WorkQueue workQueue, Provider<ReviewDb> dbProvider, ApprovalsUtil approvalsUtil, @GerritServerConfig Config config)
name|ReviewerRecommender
parameter_list|(
name|ChangeQueryBuilder
name|changeQueryBuilder
parameter_list|,
name|DynamicMap
argument_list|<
name|ReviewerSuggestion
argument_list|>
name|reviewerSuggestionPluginMap
parameter_list|,
name|InternalChangeQuery
name|internalChangeQuery
parameter_list|,
name|WorkQueue
name|workQueue
parameter_list|,
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|dbProvider
parameter_list|,
name|ApprovalsUtil
name|approvalsUtil
parameter_list|,
annotation|@
name|GerritServerConfig
name|Config
name|config
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
name|changeQueryBuilder
operator|=
name|changeQueryBuilder
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
name|this
operator|.
name|internalChangeQuery
operator|=
name|internalChangeQuery
expr_stmt|;
name|this
operator|.
name|reviewerSuggestionPluginMap
operator|=
name|reviewerSuggestionPluginMap
expr_stmt|;
name|this
operator|.
name|workQueue
operator|=
name|workQueue
expr_stmt|;
name|this
operator|.
name|dbProvider
operator|=
name|dbProvider
expr_stmt|;
name|this
operator|.
name|approvalsUtil
operator|=
name|approvalsUtil
expr_stmt|;
block|}
DECL|method|suggestReviewers ( ChangeNotes changeNotes, SuggestReviewers suggestReviewers, ProjectControl projectControl, List<Account.Id> candidateList)
specifier|public
name|List
argument_list|<
name|Account
operator|.
name|Id
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
name|String
name|query
init|=
name|suggestReviewers
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|double
name|baseWeight
init|=
name|config
operator|.
name|getInt
argument_list|(
literal|"addReviewer"
argument_list|,
literal|"baseWeight"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|MutableDouble
argument_list|>
name|reviewerScores
decl_stmt|;
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
name|reviewerScores
operator|=
name|baseRankingForEmptyQuery
argument_list|(
name|baseWeight
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|reviewerScores
operator|=
name|baseRankingForCandidateList
argument_list|(
name|candidateList
argument_list|,
name|projectControl
argument_list|,
name|baseWeight
argument_list|)
expr_stmt|;
block|}
comment|// Send the query along with a candidate list to all plugins and merge the
comment|// results. Plugins don't necessarily need to use the candidates list, they
comment|// can also return non-candidate account ids.
name|List
argument_list|<
name|Callable
argument_list|<
name|Set
argument_list|<
name|SuggestedReviewer
argument_list|>
argument_list|>
argument_list|>
name|tasks
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|reviewerSuggestionPluginMap
operator|.
name|plugins
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Double
argument_list|>
name|weights
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|reviewerSuggestionPluginMap
operator|.
name|plugins
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|DynamicMap
operator|.
name|Entry
argument_list|<
name|ReviewerSuggestion
argument_list|>
name|plugin
range|:
name|reviewerSuggestionPluginMap
control|)
block|{
name|tasks
operator|.
name|add
argument_list|(
parameter_list|()
lambda|->
name|plugin
operator|.
name|getProvider
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|suggestReviewers
argument_list|(
name|projectControl
operator|.
name|getProject
argument_list|()
operator|.
name|getNameKey
argument_list|()
argument_list|,
name|changeNotes
operator|.
name|getChangeId
argument_list|()
argument_list|,
name|query
argument_list|,
name|reviewerScores
operator|.
name|keySet
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|pluginWeight
init|=
name|config
operator|.
name|getString
argument_list|(
literal|"addReviewer"
argument_list|,
name|plugin
operator|.
name|getPluginName
argument_list|()
operator|+
literal|"-"
operator|+
name|plugin
operator|.
name|getExportName
argument_list|()
argument_list|,
literal|"weight"
argument_list|)
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|pluginWeight
argument_list|)
condition|)
block|{
name|pluginWeight
operator|=
literal|"1"
expr_stmt|;
block|}
try|try
block|{
name|weights
operator|.
name|add
argument_list|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|pluginWeight
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Exception while parsing weight for "
operator|+
name|plugin
operator|.
name|getPluginName
argument_list|()
operator|+
literal|"-"
operator|+
name|plugin
operator|.
name|getExportName
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|weights
operator|.
name|add
argument_list|(
literal|1d
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
name|List
argument_list|<
name|Future
argument_list|<
name|Set
argument_list|<
name|SuggestedReviewer
argument_list|>
argument_list|>
argument_list|>
name|futures
init|=
name|workQueue
operator|.
name|getDefaultQueue
argument_list|()
operator|.
name|invokeAll
argument_list|(
name|tasks
argument_list|,
name|PLUGIN_QUERY_TIMEOUT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|Double
argument_list|>
name|weightIterator
init|=
name|weights
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|Future
argument_list|<
name|Set
argument_list|<
name|SuggestedReviewer
argument_list|>
argument_list|>
name|f
range|:
name|futures
control|)
block|{
name|double
name|weight
init|=
name|weightIterator
operator|.
name|next
argument_list|()
decl_stmt|;
for|for
control|(
name|SuggestedReviewer
name|s
range|:
name|f
operator|.
name|get
argument_list|()
control|)
block|{
if|if
condition|(
name|reviewerScores
operator|.
name|containsKey
argument_list|(
name|s
operator|.
name|account
argument_list|)
condition|)
block|{
name|reviewerScores
operator|.
name|get
argument_list|(
name|s
operator|.
name|account
argument_list|)
operator|.
name|add
argument_list|(
name|s
operator|.
name|score
operator|*
name|weight
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|reviewerScores
operator|.
name|put
argument_list|(
name|s
operator|.
name|account
argument_list|,
operator|new
name|MutableDouble
argument_list|(
name|s
operator|.
name|score
operator|*
name|weight
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|ExecutionException
decl||
name|InterruptedException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Exception while suggesting reviewers"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
if|if
condition|(
name|changeNotes
operator|!=
literal|null
condition|)
block|{
comment|// Remove change owner
name|reviewerScores
operator|.
name|remove
argument_list|(
name|changeNotes
operator|.
name|getChange
argument_list|()
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
comment|// Remove existing reviewers
name|reviewerScores
operator|.
name|keySet
argument_list|()
operator|.
name|removeAll
argument_list|(
name|approvalsUtil
operator|.
name|getReviewers
argument_list|(
name|dbProvider
operator|.
name|get
argument_list|()
argument_list|,
name|changeNotes
argument_list|)
operator|.
name|byState
argument_list|(
name|REVIEWER
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Sort results
name|Stream
argument_list|<
name|Entry
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|MutableDouble
argument_list|>
argument_list|>
name|sorted
init|=
name|reviewerScores
operator|.
name|entrySet
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|sorted
argument_list|(
name|Collections
operator|.
name|reverseOrder
argument_list|(
name|Map
operator|.
name|Entry
operator|.
name|comparingByValue
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|sortedSuggestions
init|=
name|sorted
operator|.
name|map
argument_list|(
name|Map
operator|.
name|Entry
operator|::
name|getKey
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|sortedSuggestions
return|;
block|}
DECL|method|baseRankingForEmptyQuery ( double baseWeight)
specifier|private
name|Map
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|MutableDouble
argument_list|>
name|baseRankingForEmptyQuery
parameter_list|(
name|double
name|baseWeight
parameter_list|)
throws|throws
name|OrmException
block|{
comment|// Get the user's last 50 changes, check approvals
try|try
block|{
name|List
argument_list|<
name|ChangeData
argument_list|>
name|result
init|=
name|internalChangeQuery
operator|.
name|setLimit
argument_list|(
literal|50
argument_list|)
operator|.
name|setRequestedFields
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|ChangeField
operator|.
name|REVIEWER
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
operator|.
name|query
argument_list|(
name|changeQueryBuilder
operator|.
name|owner
argument_list|(
literal|"self"
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|MutableDouble
argument_list|>
name|suggestions
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ChangeData
name|cd
range|:
name|result
control|)
block|{
for|for
control|(
name|PatchSetApproval
name|approval
range|:
name|cd
operator|.
name|currentApprovals
argument_list|()
control|)
block|{
name|Account
operator|.
name|Id
name|id
init|=
name|approval
operator|.
name|getAccountId
argument_list|()
decl_stmt|;
if|if
condition|(
name|suggestions
operator|.
name|containsKey
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|suggestions
operator|.
name|get
argument_list|(
name|id
argument_list|)
operator|.
name|add
argument_list|(
name|baseWeight
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|suggestions
operator|.
name|put
argument_list|(
name|id
argument_list|,
operator|new
name|MutableDouble
argument_list|(
name|baseWeight
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|suggestions
return|;
block|}
catch|catch
parameter_list|(
name|QueryParseException
name|e
parameter_list|)
block|{
comment|// Unhandled, because owner:self will never provoke a QueryParseException
name|log
operator|.
name|error
argument_list|(
literal|"Exception while suggesting reviewers"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|ImmutableMap
operator|.
name|of
argument_list|()
return|;
block|}
block|}
DECL|method|baseRankingForCandidateList ( List<Account.Id> candidates, ProjectControl projectControl, double baseWeight)
specifier|private
name|Map
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|MutableDouble
argument_list|>
name|baseRankingForCandidateList
parameter_list|(
name|List
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|candidates
parameter_list|,
name|ProjectControl
name|projectControl
parameter_list|,
name|double
name|baseWeight
parameter_list|)
throws|throws
name|OrmException
block|{
comment|// Get each reviewer's activity based on number of applied labels
comment|// (weighted 10d), number of comments (weighted 0.5d) and number of owned
comment|// changes (weighted 1d).
name|Map
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|MutableDouble
argument_list|>
name|reviewers
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|candidates
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|reviewers
return|;
block|}
name|List
argument_list|<
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
argument_list|>
name|predicates
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Account
operator|.
name|Id
name|id
range|:
name|candidates
control|)
block|{
try|try
block|{
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|projectQuery
init|=
name|changeQueryBuilder
operator|.
name|project
argument_list|(
name|projectControl
operator|.
name|getProject
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|// Get all labels for this project and create a compound OR query to
comment|// fetch all changes where users have applied one of these labels
name|List
argument_list|<
name|LabelType
argument_list|>
name|labelTypes
init|=
name|projectControl
operator|.
name|getLabelTypes
argument_list|()
operator|.
name|getLabelTypes
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
argument_list|>
name|labelPredicates
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|labelTypes
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
name|labelTypes
control|)
block|{
name|labelPredicates
operator|.
name|add
argument_list|(
name|changeQueryBuilder
operator|.
name|label
argument_list|(
name|type
operator|.
name|getName
argument_list|()
operator|+
literal|",user="
operator|+
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|reviewerQuery
init|=
name|Predicate
operator|.
name|and
argument_list|(
name|projectQuery
argument_list|,
name|Predicate
operator|.
name|or
argument_list|(
name|labelPredicates
argument_list|)
argument_list|)
decl_stmt|;
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|ownerQuery
init|=
name|Predicate
operator|.
name|and
argument_list|(
name|projectQuery
argument_list|,
name|changeQueryBuilder
operator|.
name|owner
argument_list|(
name|id
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|commentedByQuery
init|=
name|Predicate
operator|.
name|and
argument_list|(
name|projectQuery
argument_list|,
name|changeQueryBuilder
operator|.
name|commentby
argument_list|(
name|id
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|predicates
operator|.
name|add
argument_list|(
name|reviewerQuery
argument_list|)
expr_stmt|;
name|predicates
operator|.
name|add
argument_list|(
name|ownerQuery
argument_list|)
expr_stmt|;
name|predicates
operator|.
name|add
argument_list|(
name|commentedByQuery
argument_list|)
expr_stmt|;
name|reviewers
operator|.
name|put
argument_list|(
name|id
argument_list|,
operator|new
name|MutableDouble
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|QueryParseException
name|e
parameter_list|)
block|{
comment|// Unhandled: If an exception is thrown, we won't increase the
comment|// candidates's score
name|log
operator|.
name|error
argument_list|(
literal|"Exception while suggesting reviewers"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|List
argument_list|<
name|ChangeData
argument_list|>
argument_list|>
name|result
init|=
name|internalChangeQuery
operator|.
name|setLimit
argument_list|(
literal|100
operator|*
name|predicates
operator|.
name|size
argument_list|()
argument_list|)
operator|.
name|setRequestedFields
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
name|predicates
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|List
argument_list|<
name|ChangeData
argument_list|>
argument_list|>
name|queryResultIterator
init|=
name|result
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|reviewersIterator
init|=
name|reviewers
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
name|Account
operator|.
name|Id
name|currentId
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|queryResultIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|ChangeData
argument_list|>
name|currentResult
init|=
name|queryResultIterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|i
operator|%
name|WEIGHTS
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|currentId
operator|=
name|reviewersIterator
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
name|reviewers
operator|.
name|get
argument_list|(
name|currentId
argument_list|)
operator|.
name|add
argument_list|(
name|WEIGHTS
index|[
name|i
operator|%
name|WEIGHTS
operator|.
name|length
index|]
operator|*
name|baseWeight
operator|*
name|currentResult
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
return|return
name|reviewers
return|;
block|}
block|}
end_class

end_unit

