begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2010 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.query.change
package|package
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
name|reviewdb
operator|.
name|AccountProjectWatch
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
name|Change
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
name|IdentifiedUser
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
name|OperatorPredicate
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
name|gwtorm
operator|.
name|client
operator|.
name|OrmException
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
name|HashMap
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
DECL|class|IsWatchedByPredicate
class|class
name|IsWatchedByPredicate
extends|extends
name|OperatorPredicate
argument_list|<
name|ChangeData
argument_list|>
block|{
DECL|method|describe (CurrentUser user)
specifier|private
specifier|static
name|String
name|describe
parameter_list|(
name|CurrentUser
name|user
parameter_list|)
block|{
if|if
condition|(
name|user
operator|instanceof
name|IdentifiedUser
condition|)
block|{
return|return
operator|(
operator|(
name|IdentifiedUser
operator|)
name|user
operator|)
operator|.
name|getAccountId
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
return|return
name|user
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|field|args
specifier|private
specifier|final
name|ChangeQueryBuilder
operator|.
name|Arguments
name|args
decl_stmt|;
DECL|field|user
specifier|private
specifier|final
name|CurrentUser
name|user
decl_stmt|;
DECL|field|rules
specifier|private
name|Map
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|,
name|List
argument_list|<
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
argument_list|>
argument_list|>
name|rules
decl_stmt|;
DECL|method|IsWatchedByPredicate (ChangeQueryBuilder.Arguments args, CurrentUser user)
name|IsWatchedByPredicate
parameter_list|(
name|ChangeQueryBuilder
operator|.
name|Arguments
name|args
parameter_list|,
name|CurrentUser
name|user
parameter_list|)
block|{
name|super
argument_list|(
name|ChangeQueryBuilder
operator|.
name|FIELD_WATCHEDBY
argument_list|,
name|describe
argument_list|(
name|user
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|args
operator|=
name|args
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|match (final ChangeData cd)
specifier|public
name|boolean
name|match
parameter_list|(
specifier|final
name|ChangeData
name|cd
parameter_list|)
throws|throws
name|OrmException
block|{
if|if
condition|(
name|rules
operator|==
literal|null
condition|)
block|{
name|ChangeQueryBuilder
name|builder
init|=
operator|new
name|ChangeQueryBuilder
argument_list|(
name|args
argument_list|,
name|user
argument_list|)
decl_stmt|;
name|rules
operator|=
operator|new
name|HashMap
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|,
name|List
argument_list|<
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|AccountProjectWatch
name|w
range|:
name|user
operator|.
name|getNotificationFilters
argument_list|()
control|)
block|{
name|List
argument_list|<
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
argument_list|>
name|list
init|=
name|rules
operator|.
name|get
argument_list|(
name|w
operator|.
name|getProjectNameKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|==
literal|null
condition|)
block|{
name|list
operator|=
operator|new
name|ArrayList
argument_list|<
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
argument_list|>
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|rules
operator|.
name|put
argument_list|(
name|w
operator|.
name|getProjectNameKey
argument_list|()
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|p
init|=
name|compile
argument_list|(
name|builder
argument_list|,
name|w
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|rules
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Change
name|change
init|=
name|cd
operator|.
name|change
argument_list|(
name|args
operator|.
name|dbProvider
argument_list|)
decl_stmt|;
if|if
condition|(
name|change
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Project
operator|.
name|NameKey
name|project
init|=
name|change
operator|.
name|getDest
argument_list|()
operator|.
name|getParentKey
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
argument_list|>
name|list
init|=
name|rules
operator|.
name|get
argument_list|(
name|project
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|==
literal|null
condition|)
block|{
name|list
operator|=
name|rules
operator|.
name|get
argument_list|(
name|args
operator|.
name|allProjectsName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|list
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|p
range|:
name|list
control|)
block|{
if|if
condition|(
name|p
operator|.
name|match
argument_list|(
name|cd
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|compile (ChangeQueryBuilder builder, AccountProjectWatch w)
specifier|private
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|compile
parameter_list|(
name|ChangeQueryBuilder
name|builder
parameter_list|,
name|AccountProjectWatch
name|w
parameter_list|)
block|{
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|p
init|=
name|builder
operator|.
name|is_visible
argument_list|()
decl_stmt|;
if|if
condition|(
name|w
operator|.
name|getFilter
argument_list|()
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|p
operator|=
name|Predicate
operator|.
name|and
argument_list|(
name|builder
operator|.
name|parse
argument_list|(
name|w
operator|.
name|getFilter
argument_list|()
argument_list|)
argument_list|,
name|p
argument_list|)
expr_stmt|;
if|if
condition|(
name|builder
operator|.
name|find
argument_list|(
name|p
argument_list|,
name|IsWatchedByPredicate
operator|.
name|class
argument_list|)
operator|!=
literal|null
condition|)
block|{
comment|// If the query is going to infinite loop, assume it
comment|// will never match and return null. Yes this test
comment|// prevents you from having a filter that matches what
comment|// another user is filtering on. :-)
comment|//
return|return
literal|null
return|;
block|}
name|p
operator|=
name|args
operator|.
name|rewriter
operator|.
name|get
argument_list|()
operator|.
name|rewrite
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|QueryParseException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
return|return
name|p
return|;
block|}
annotation|@
name|Override
DECL|method|getCost ()
specifier|public
name|int
name|getCost
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
block|}
end_class

end_unit

