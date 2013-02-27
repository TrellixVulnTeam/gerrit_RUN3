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
DECL|package|com.google.gerrit.server.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|change
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
name|Charsets
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
name|Joiner
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
name|Objects
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
name|Throwables
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
name|SubmitRecord
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
name|DefaultInput
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
name|RestModifyView
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
name|account
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
name|server
operator|.
name|change
operator|.
name|TestSubmitRule
operator|.
name|Input
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
name|RuleEvalException
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
name|SubmitRuleEvaluator
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
name|googlecode
operator|.
name|prolog_cafe
operator|.
name|lang
operator|.
name|Term
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
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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

begin_class
DECL|class|TestSubmitRule
specifier|public
class|class
name|TestSubmitRule
implements|implements
name|RestModifyView
argument_list|<
name|RevisionResource
argument_list|,
name|Input
argument_list|>
block|{
DECL|enum|Filters
specifier|public
enum|enum
name|Filters
block|{
DECL|enumConstant|RUN
DECL|enumConstant|SKIP
name|RUN
block|,
name|SKIP
block|;   }
DECL|class|Input
specifier|public
specifier|static
class|class
name|Input
block|{
annotation|@
name|DefaultInput
DECL|field|rule
specifier|public
name|String
name|rule
decl_stmt|;
DECL|field|filters
specifier|public
name|Filters
name|filters
decl_stmt|;
block|}
DECL|field|db
specifier|private
specifier|final
name|ReviewDb
name|db
decl_stmt|;
DECL|field|rules
specifier|private
specifier|final
name|RulesCache
name|rules
decl_stmt|;
DECL|field|accountInfoFactory
specifier|private
specifier|final
name|AccountInfo
operator|.
name|Loader
operator|.
name|Factory
name|accountInfoFactory
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--filters"
argument_list|,
name|usage
operator|=
literal|"impact of filters in parent projects"
argument_list|)
DECL|field|filters
specifier|private
name|Filters
name|filters
init|=
name|Filters
operator|.
name|RUN
decl_stmt|;
annotation|@
name|Inject
DECL|method|TestSubmitRule (ReviewDb db, RulesCache rules, AccountInfo.Loader.Factory infoFactory)
name|TestSubmitRule
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|RulesCache
name|rules
parameter_list|,
name|AccountInfo
operator|.
name|Loader
operator|.
name|Factory
name|infoFactory
parameter_list|)
block|{
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|rules
operator|=
name|rules
expr_stmt|;
name|this
operator|.
name|accountInfoFactory
operator|=
name|infoFactory
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (RevisionResource rsrc, Input input)
specifier|public
name|Object
name|apply
parameter_list|(
name|RevisionResource
name|rsrc
parameter_list|,
name|Input
name|input
parameter_list|)
throws|throws
name|OrmException
throws|,
name|BadRequestException
throws|,
name|AuthException
block|{
if|if
condition|(
name|input
operator|==
literal|null
condition|)
block|{
name|input
operator|=
operator|new
name|Input
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|input
operator|.
name|rule
operator|!=
literal|null
operator|&&
operator|!
name|rules
operator|.
name|isProjectRulesEnabled
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AuthException
argument_list|(
literal|"project rules are disabled"
argument_list|)
throw|;
block|}
name|input
operator|.
name|filters
operator|=
name|Objects
operator|.
name|firstNonNull
argument_list|(
name|input
operator|.
name|filters
argument_list|,
name|filters
argument_list|)
expr_stmt|;
name|SubmitRuleEvaluator
name|evaluator
init|=
operator|new
name|SubmitRuleEvaluator
argument_list|(
name|db
argument_list|,
name|rsrc
operator|.
name|getPatchSet
argument_list|()
argument_list|,
name|rsrc
operator|.
name|getControl
argument_list|()
operator|.
name|getProjectControl
argument_list|()
argument_list|,
name|rsrc
operator|.
name|getControl
argument_list|()
argument_list|,
name|rsrc
operator|.
name|getChange
argument_list|()
argument_list|,
operator|new
name|ChangeData
argument_list|(
name|rsrc
operator|.
name|getChange
argument_list|()
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|"locate_submit_rule"
argument_list|,
literal|"can_submit"
argument_list|,
literal|"locate_submit_filter"
argument_list|,
literal|"filter_submit_results"
argument_list|,
name|input
operator|.
name|filters
operator|==
name|Filters
operator|.
name|SKIP
argument_list|,
name|input
operator|.
name|rule
operator|!=
literal|null
condition|?
operator|new
name|ByteArrayInputStream
argument_list|(
name|input
operator|.
name|rule
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
else|:
literal|null
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Term
argument_list|>
name|results
decl_stmt|;
try|try
block|{
name|results
operator|=
name|eval
argument_list|(
name|evaluator
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuleEvalException
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
name|Joiner
operator|.
name|on
argument_list|(
literal|": "
argument_list|)
operator|.
name|skipNulls
argument_list|()
operator|.
name|join
argument_list|(
name|Iterables
operator|.
name|transform
argument_list|(
name|Throwables
operator|.
name|getCausalChain
argument_list|(
name|e
argument_list|)
argument_list|,
operator|new
name|Function
argument_list|<
name|Throwable
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
name|Throwable
name|in
parameter_list|)
block|{
return|return
name|in
operator|.
name|getMessage
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"rule failed: "
operator|+
name|msg
argument_list|)
throw|;
block|}
if|if
condition|(
name|results
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"rule %s has no solutions"
argument_list|,
name|evaluator
operator|.
name|getSubmitRule
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|SubmitRecord
argument_list|>
name|records
init|=
name|rsrc
operator|.
name|getControl
argument_list|()
operator|.
name|resultsToSubmitRecord
argument_list|(
name|evaluator
operator|.
name|getSubmitRule
argument_list|()
argument_list|,
name|results
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Record
argument_list|>
name|out
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|records
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|AccountInfo
operator|.
name|Loader
name|accounts
init|=
name|accountInfoFactory
operator|.
name|create
argument_list|(
literal|true
argument_list|)
decl_stmt|;
for|for
control|(
name|SubmitRecord
name|r
range|:
name|records
control|)
block|{
name|out
operator|.
name|add
argument_list|(
operator|new
name|Record
argument_list|(
name|r
argument_list|,
name|accounts
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|accounts
operator|.
name|fill
argument_list|()
expr_stmt|;
return|return
name|out
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|eval (SubmitRuleEvaluator evaluator)
specifier|private
specifier|static
name|List
argument_list|<
name|Term
argument_list|>
name|eval
parameter_list|(
name|SubmitRuleEvaluator
name|evaluator
parameter_list|)
throws|throws
name|RuleEvalException
block|{
return|return
name|evaluator
operator|.
name|evaluate
argument_list|()
operator|.
name|toJava
argument_list|()
return|;
block|}
DECL|class|Record
specifier|static
class|class
name|Record
block|{
DECL|field|status
name|SubmitRecord
operator|.
name|Status
name|status
decl_stmt|;
DECL|field|errorMessage
name|String
name|errorMessage
decl_stmt|;
DECL|field|ok
name|Map
argument_list|<
name|String
argument_list|,
name|AccountInfo
argument_list|>
name|ok
decl_stmt|;
DECL|field|reject
name|Map
argument_list|<
name|String
argument_list|,
name|AccountInfo
argument_list|>
name|reject
decl_stmt|;
DECL|field|need
name|Map
argument_list|<
name|String
argument_list|,
name|None
argument_list|>
name|need
decl_stmt|;
DECL|field|may
name|Map
argument_list|<
name|String
argument_list|,
name|AccountInfo
argument_list|>
name|may
decl_stmt|;
DECL|field|impossible
name|Map
argument_list|<
name|String
argument_list|,
name|None
argument_list|>
name|impossible
decl_stmt|;
DECL|method|Record (SubmitRecord r, AccountInfo.Loader accounts)
name|Record
parameter_list|(
name|SubmitRecord
name|r
parameter_list|,
name|AccountInfo
operator|.
name|Loader
name|accounts
parameter_list|)
block|{
name|this
operator|.
name|status
operator|=
name|r
operator|.
name|status
expr_stmt|;
name|this
operator|.
name|errorMessage
operator|=
name|r
operator|.
name|errorMessage
expr_stmt|;
if|if
condition|(
name|r
operator|.
name|labels
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|SubmitRecord
operator|.
name|Label
name|n
range|:
name|r
operator|.
name|labels
control|)
block|{
name|AccountInfo
name|who
init|=
name|n
operator|.
name|appliedBy
operator|!=
literal|null
condition|?
name|accounts
operator|.
name|get
argument_list|(
name|n
operator|.
name|appliedBy
argument_list|)
else|:
operator|new
name|AccountInfo
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|label
argument_list|(
name|n
argument_list|,
name|who
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|label (SubmitRecord.Label n, AccountInfo who)
specifier|private
name|void
name|label
parameter_list|(
name|SubmitRecord
operator|.
name|Label
name|n
parameter_list|,
name|AccountInfo
name|who
parameter_list|)
block|{
switch|switch
condition|(
name|n
operator|.
name|status
condition|)
block|{
case|case
name|OK
case|:
if|if
condition|(
name|ok
operator|==
literal|null
condition|)
block|{
name|ok
operator|=
name|Maps
operator|.
name|newLinkedHashMap
argument_list|()
expr_stmt|;
block|}
name|ok
operator|.
name|put
argument_list|(
name|n
operator|.
name|label
argument_list|,
name|who
argument_list|)
expr_stmt|;
break|break;
case|case
name|REJECT
case|:
if|if
condition|(
name|reject
operator|==
literal|null
condition|)
block|{
name|reject
operator|=
name|Maps
operator|.
name|newLinkedHashMap
argument_list|()
expr_stmt|;
block|}
name|reject
operator|.
name|put
argument_list|(
name|n
operator|.
name|label
argument_list|,
name|who
argument_list|)
expr_stmt|;
break|break;
case|case
name|NEED
case|:
if|if
condition|(
name|need
operator|==
literal|null
condition|)
block|{
name|need
operator|=
name|Maps
operator|.
name|newLinkedHashMap
argument_list|()
expr_stmt|;
block|}
name|need
operator|.
name|put
argument_list|(
name|n
operator|.
name|label
argument_list|,
operator|new
name|None
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|MAY
case|:
if|if
condition|(
name|may
operator|==
literal|null
condition|)
block|{
name|may
operator|=
name|Maps
operator|.
name|newLinkedHashMap
argument_list|()
expr_stmt|;
block|}
name|may
operator|.
name|put
argument_list|(
name|n
operator|.
name|label
argument_list|,
name|who
argument_list|)
expr_stmt|;
break|break;
case|case
name|IMPOSSIBLE
case|:
if|if
condition|(
name|impossible
operator|==
literal|null
condition|)
block|{
name|impossible
operator|=
name|Maps
operator|.
name|newLinkedHashMap
argument_list|()
expr_stmt|;
block|}
name|impossible
operator|.
name|put
argument_list|(
name|n
operator|.
name|label
argument_list|,
operator|new
name|None
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
DECL|class|None
specifier|static
class|class
name|None
block|{   }
block|}
end_class

end_unit

