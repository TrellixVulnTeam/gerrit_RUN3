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
DECL|package|com.google.gerrit.client
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
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
name|ui
operator|.
name|AccountGroupSuggestOracle
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
name|ui
operator|.
name|AccountSuggestOracle
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
name|ui
operator|.
name|ProjectNameSuggestOracle
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
name|ui
operator|.
name|SuggestOracle
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtexpui
operator|.
name|safehtml
operator|.
name|client
operator|.
name|HighlightSuggestOracle
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
name|Arrays
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
name|TreeSet
import|;
end_import

begin_class
DECL|class|SearchSuggestOracle
specifier|public
class|class
name|SearchSuggestOracle
extends|extends
name|HighlightSuggestOracle
block|{
DECL|field|paramSuggester
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|ParamSuggester
argument_list|>
name|paramSuggester
init|=
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|ParamSuggester
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"project:"
argument_list|,
literal|"p:"
argument_list|,
literal|"parentproject:"
argument_list|)
argument_list|,
operator|new
name|ProjectNameSuggestOracle
argument_list|()
argument_list|)
argument_list|,
operator|new
name|ParamSuggester
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"owner:"
argument_list|,
literal|"o:"
argument_list|,
literal|"reviewer:"
argument_list|,
literal|"r:"
argument_list|,
literal|"commentby:"
argument_list|,
literal|"reviewedby:"
argument_list|,
literal|"author:"
argument_list|,
literal|"committer:"
argument_list|,
literal|"from:"
argument_list|,
literal|"assignee:"
argument_list|,
literal|"cc:"
argument_list|)
argument_list|,
operator|new
name|AccountSuggestOracle
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onRequestSuggestions
parameter_list|(
specifier|final
name|Request
name|request
parameter_list|,
specifier|final
name|Callback
name|done
parameter_list|)
block|{
name|super
operator|.
name|onRequestSuggestions
argument_list|(
name|request
argument_list|,
operator|new
name|Callback
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuggestionsReady
parameter_list|(
specifier|final
name|Request
name|request
parameter_list|,
specifier|final
name|Response
name|response
parameter_list|)
block|{
if|if
condition|(
literal|"self"
operator|.
name|startsWith
argument_list|(
name|request
operator|.
name|getQuery
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|ArrayList
argument_list|<
name|SuggestOracle
operator|.
name|Suggestion
argument_list|>
name|r
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|response
operator|.
name|getSuggestions
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|1
argument_list|)
decl_stmt|;
name|r
operator|.
name|addAll
argument_list|(
name|response
operator|.
name|getSuggestions
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|add
argument_list|(
operator|new
name|SuggestOracle
operator|.
name|Suggestion
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getDisplayString
parameter_list|()
block|{
return|return
name|getReplacementString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getReplacementString
parameter_list|()
block|{
return|return
literal|"self"
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|response
operator|.
name|setSuggestions
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
name|done
operator|.
name|onSuggestionsReady
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
argument_list|,
operator|new
name|ParamSuggester
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"ownerin:"
argument_list|,
literal|"reviewerin:"
argument_list|)
argument_list|,
operator|new
name|AccountGroupSuggestOracle
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|suggestions
specifier|private
specifier|static
specifier|final
name|TreeSet
argument_list|<
name|String
argument_list|>
name|suggestions
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
decl_stmt|;
static|static
block|{
name|suggestions
operator|.
name|add
argument_list|(
literal|"age:"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"age:1week"
argument_list|)
expr_stmt|;
comment|// Give an example age
name|suggestions
operator|.
name|add
argument_list|(
literal|"change:"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"owner:"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"owner:self"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"ownerin:"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"author:"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"committer:"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"reviewer:"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"reviewer:self"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"reviewerin:"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"reviewedby:"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"commit:"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"comment:"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"message:"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"commentby:"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"from:"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"file:"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"conflicts:"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"project:"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"projects:"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"parentproject:"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"branch:"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"topic:"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"intopic:"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"ref:"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"tr:"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"bug:"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"label:"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"query:"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"has:"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"has:draft"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"has:edit"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"has:star"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"has:stars"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"has:unresolved"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"star:"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"is:"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"is:starred"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"is:watched"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"is:reviewed"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"is:owner"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"is:reviewer"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"is:open"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"is:pending"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"is:draft"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"is:closed"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"is:merged"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"is:abandoned"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"is:mergeable"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"status:"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"status:open"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"status:pending"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"status:reviewed"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"status:closed"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"status:merged"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"status:abandoned"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"status:draft"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"added:"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"deleted:"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"delta:"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"size:"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"unresolved:"
argument_list|)
expr_stmt|;
if|if
condition|(
name|Gerrit
operator|.
name|isNoteDbEnabled
argument_list|()
condition|)
block|{
name|suggestions
operator|.
name|add
argument_list|(
literal|"cc:"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"hashtag:"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|Gerrit
operator|.
name|info
argument_list|()
operator|.
name|change
argument_list|()
operator|.
name|showAssignee
argument_list|()
condition|)
block|{
name|suggestions
operator|.
name|add
argument_list|(
literal|"is:assigned"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"is:unassigned"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"assignee:"
argument_list|)
expr_stmt|;
block|}
name|suggestions
operator|.
name|add
argument_list|(
literal|"AND"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"OR"
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
literal|"NOT"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|requestDefaultSuggestions (Request request, Callback done)
specifier|public
name|void
name|requestDefaultSuggestions
parameter_list|(
name|Request
name|request
parameter_list|,
name|Callback
name|done
parameter_list|)
block|{
specifier|final
name|ArrayList
argument_list|<
name|SearchSuggestion
argument_list|>
name|r
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// No text - show some default suggestions.
name|r
operator|.
name|add
argument_list|(
operator|new
name|SearchSuggestion
argument_list|(
literal|"status:open"
argument_list|,
literal|"status:open"
argument_list|)
argument_list|)
expr_stmt|;
name|r
operator|.
name|add
argument_list|(
operator|new
name|SearchSuggestion
argument_list|(
literal|"age:1week"
argument_list|,
literal|"age:1week"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|Gerrit
operator|.
name|isSignedIn
argument_list|()
condition|)
block|{
name|r
operator|.
name|add
argument_list|(
operator|new
name|SearchSuggestion
argument_list|(
literal|"owner:self"
argument_list|,
literal|"owner:self"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|done
operator|.
name|onSuggestionsReady
argument_list|(
name|request
argument_list|,
operator|new
name|Response
argument_list|(
name|r
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onRequestSuggestions (Request request, Callback done)
specifier|protected
name|void
name|onRequestSuggestions
parameter_list|(
name|Request
name|request
parameter_list|,
name|Callback
name|done
parameter_list|)
block|{
specifier|final
name|String
name|query
init|=
name|request
operator|.
name|getQuery
argument_list|()
decl_stmt|;
specifier|final
name|String
name|lastWord
init|=
name|getLastWord
argument_list|(
name|query
argument_list|)
decl_stmt|;
if|if
condition|(
name|lastWord
operator|==
literal|null
condition|)
block|{
comment|// Starting a new word - don't show suggestions yet.
name|done
operator|.
name|onSuggestionsReady
argument_list|(
name|request
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return;
block|}
for|for
control|(
specifier|final
name|ParamSuggester
name|ps
range|:
name|paramSuggester
control|)
block|{
if|if
condition|(
name|ps
operator|.
name|applicable
argument_list|(
name|lastWord
argument_list|)
condition|)
block|{
name|ps
operator|.
name|suggest
argument_list|(
name|lastWord
argument_list|,
name|request
argument_list|,
name|done
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
specifier|final
name|ArrayList
argument_list|<
name|SearchSuggestion
argument_list|>
name|r
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|suggestion
range|:
name|suggestions
operator|.
name|tailSet
argument_list|(
name|lastWord
argument_list|)
control|)
block|{
if|if
condition|(
operator|(
name|lastWord
operator|.
name|length
argument_list|()
operator|<
name|suggestion
operator|.
name|length
argument_list|()
operator|)
operator|&&
name|suggestion
operator|.
name|startsWith
argument_list|(
name|lastWord
argument_list|)
condition|)
block|{
if|if
condition|(
name|suggestion
operator|.
name|contains
argument_list|(
literal|"self"
argument_list|)
operator|&&
operator|!
name|Gerrit
operator|.
name|isSignedIn
argument_list|()
condition|)
block|{
continue|continue;
block|}
name|r
operator|.
name|add
argument_list|(
operator|new
name|SearchSuggestion
argument_list|(
name|suggestion
argument_list|,
name|query
operator|+
name|suggestion
operator|.
name|substring
argument_list|(
name|lastWord
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|done
operator|.
name|onSuggestionsReady
argument_list|(
name|request
argument_list|,
operator|new
name|Response
argument_list|(
name|r
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getLastWord (final String query)
specifier|private
name|String
name|getLastWord
parameter_list|(
specifier|final
name|String
name|query
parameter_list|)
block|{
specifier|final
name|int
name|lastSpace
init|=
name|query
operator|.
name|lastIndexOf
argument_list|(
literal|' '
argument_list|)
decl_stmt|;
if|if
condition|(
name|lastSpace
operator|==
name|query
operator|.
name|length
argument_list|()
operator|-
literal|1
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|lastSpace
operator|==
operator|-
literal|1
condition|)
block|{
return|return
name|query
return|;
block|}
return|return
name|query
operator|.
name|substring
argument_list|(
name|lastSpace
operator|+
literal|1
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getQueryPattern (final String query)
specifier|protected
name|String
name|getQueryPattern
parameter_list|(
specifier|final
name|String
name|query
parameter_list|)
block|{
return|return
name|super
operator|.
name|getQueryPattern
argument_list|(
name|getLastWord
argument_list|(
name|query
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|isHTML ()
specifier|protected
name|boolean
name|isHTML
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
DECL|class|SearchSuggestion
specifier|private
specifier|static
class|class
name|SearchSuggestion
implements|implements
name|SuggestOracle
operator|.
name|Suggestion
block|{
DECL|field|suggestion
specifier|private
specifier|final
name|String
name|suggestion
decl_stmt|;
DECL|field|fullQuery
specifier|private
specifier|final
name|String
name|fullQuery
decl_stmt|;
DECL|method|SearchSuggestion (String suggestion, String fullQuery)
name|SearchSuggestion
parameter_list|(
name|String
name|suggestion
parameter_list|,
name|String
name|fullQuery
parameter_list|)
block|{
name|this
operator|.
name|suggestion
operator|=
name|suggestion
expr_stmt|;
comment|// Add a space to the query if it is a complete operation (e.g.
comment|// "status:open") so the user can keep on typing.
name|this
operator|.
name|fullQuery
operator|=
name|fullQuery
operator|.
name|endsWith
argument_list|(
literal|":"
argument_list|)
condition|?
name|fullQuery
else|:
name|fullQuery
operator|+
literal|" "
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDisplayString ()
specifier|public
name|String
name|getDisplayString
parameter_list|()
block|{
return|return
name|suggestion
return|;
block|}
annotation|@
name|Override
DECL|method|getReplacementString ()
specifier|public
name|String
name|getReplacementString
parameter_list|()
block|{
return|return
name|fullQuery
return|;
block|}
block|}
DECL|class|ParamSuggester
specifier|private
specifier|static
class|class
name|ParamSuggester
block|{
DECL|field|operators
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|operators
decl_stmt|;
DECL|field|parameterSuggestionOracle
specifier|private
specifier|final
name|SuggestOracle
name|parameterSuggestionOracle
decl_stmt|;
DECL|method|ParamSuggester (final List<String> operators, final SuggestOracle parameterSuggestionOracle)
name|ParamSuggester
parameter_list|(
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|operators
parameter_list|,
specifier|final
name|SuggestOracle
name|parameterSuggestionOracle
parameter_list|)
block|{
name|this
operator|.
name|operators
operator|=
name|operators
expr_stmt|;
name|this
operator|.
name|parameterSuggestionOracle
operator|=
name|parameterSuggestionOracle
expr_stmt|;
block|}
DECL|method|applicable (final String query)
name|boolean
name|applicable
parameter_list|(
specifier|final
name|String
name|query
parameter_list|)
block|{
specifier|final
name|String
name|operator
init|=
name|getApplicableOperator
argument_list|(
name|query
argument_list|,
name|operators
argument_list|)
decl_stmt|;
return|return
name|operator
operator|!=
literal|null
operator|&&
name|query
operator|.
name|length
argument_list|()
operator|>
name|operator
operator|.
name|length
argument_list|()
return|;
block|}
DECL|method|getApplicableOperator (final String lastWord, final List<String> operators)
specifier|private
name|String
name|getApplicableOperator
parameter_list|(
specifier|final
name|String
name|lastWord
parameter_list|,
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|operators
parameter_list|)
block|{
for|for
control|(
specifier|final
name|String
name|operator
range|:
name|operators
control|)
block|{
if|if
condition|(
name|lastWord
operator|.
name|startsWith
argument_list|(
name|operator
argument_list|)
condition|)
block|{
return|return
name|operator
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|suggest (final String lastWord, final Request request, final Callback done)
name|void
name|suggest
parameter_list|(
specifier|final
name|String
name|lastWord
parameter_list|,
specifier|final
name|Request
name|request
parameter_list|,
specifier|final
name|Callback
name|done
parameter_list|)
block|{
specifier|final
name|String
name|operator
init|=
name|getApplicableOperator
argument_list|(
name|lastWord
argument_list|,
name|operators
argument_list|)
decl_stmt|;
name|parameterSuggestionOracle
operator|.
name|requestSuggestions
argument_list|(
operator|new
name|Request
argument_list|(
name|lastWord
operator|.
name|substring
argument_list|(
name|operator
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|request
operator|.
name|getLimit
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Callback
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuggestionsReady
parameter_list|(
specifier|final
name|Request
name|req
parameter_list|,
specifier|final
name|Response
name|response
parameter_list|)
block|{
specifier|final
name|String
name|query
init|=
name|request
operator|.
name|getQuery
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|SearchSuggestOracle
operator|.
name|Suggestion
argument_list|>
name|r
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|response
operator|.
name|getSuggestions
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|SearchSuggestOracle
operator|.
name|Suggestion
name|s
range|:
name|response
operator|.
name|getSuggestions
argument_list|()
control|)
block|{
name|r
operator|.
name|add
argument_list|(
operator|new
name|SearchSuggestion
argument_list|(
name|s
operator|.
name|getDisplayString
argument_list|()
argument_list|,
name|query
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|query
operator|.
name|length
argument_list|()
operator|-
name|lastWord
operator|.
name|length
argument_list|()
argument_list|)
operator|+
name|operator
operator|+
name|quoteIfNeeded
argument_list|(
name|s
operator|.
name|getReplacementString
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|done
operator|.
name|onSuggestionsReady
argument_list|(
name|request
argument_list|,
operator|new
name|Response
argument_list|(
name|r
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
name|quoteIfNeeded
parameter_list|(
specifier|final
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
operator|!
name|s
operator|.
name|matches
argument_list|(
literal|"^\\S*$"
argument_list|)
condition|)
block|{
return|return
literal|"\""
operator|+
name|s
operator|+
literal|"\""
return|;
block|}
return|return
name|s
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

