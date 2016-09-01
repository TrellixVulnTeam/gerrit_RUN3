begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2015 The Android Open Source Project
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
name|collect
operator|.
name|ImmutableMultimap
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
name|Multimap
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
name|InternalUser
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
name|ChangeCleanupConfig
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
name|ChangeControl
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
name|ChangeQueryProcessor
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
name|Singleton
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
name|Collection
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
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|AbandonUtil
specifier|public
class|class
name|AbandonUtil
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
name|AbandonUtil
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|cfg
specifier|private
specifier|final
name|ChangeCleanupConfig
name|cfg
decl_stmt|;
DECL|field|queryProcessor
specifier|private
specifier|final
name|ChangeQueryProcessor
name|queryProcessor
decl_stmt|;
DECL|field|queryBuilder
specifier|private
specifier|final
name|ChangeQueryBuilder
name|queryBuilder
decl_stmt|;
DECL|field|abandon
specifier|private
specifier|final
name|Abandon
name|abandon
decl_stmt|;
DECL|field|internalUser
specifier|private
specifier|final
name|InternalUser
name|internalUser
decl_stmt|;
annotation|@
name|Inject
DECL|method|AbandonUtil ( ChangeCleanupConfig cfg, InternalUser.Factory internalUserFactory, ChangeQueryProcessor queryProcessor, ChangeQueryBuilder queryBuilder, Abandon abandon)
name|AbandonUtil
parameter_list|(
name|ChangeCleanupConfig
name|cfg
parameter_list|,
name|InternalUser
operator|.
name|Factory
name|internalUserFactory
parameter_list|,
name|ChangeQueryProcessor
name|queryProcessor
parameter_list|,
name|ChangeQueryBuilder
name|queryBuilder
parameter_list|,
name|Abandon
name|abandon
parameter_list|)
block|{
name|this
operator|.
name|cfg
operator|=
name|cfg
expr_stmt|;
name|this
operator|.
name|queryProcessor
operator|=
name|queryProcessor
expr_stmt|;
name|this
operator|.
name|queryBuilder
operator|=
name|queryBuilder
expr_stmt|;
name|this
operator|.
name|abandon
operator|=
name|abandon
expr_stmt|;
name|internalUser
operator|=
name|internalUserFactory
operator|.
name|create
argument_list|()
expr_stmt|;
block|}
DECL|method|abandonInactiveOpenChanges ()
specifier|public
name|void
name|abandonInactiveOpenChanges
parameter_list|()
block|{
if|if
condition|(
name|cfg
operator|.
name|getAbandonAfter
argument_list|()
operator|<=
literal|0
condition|)
block|{
return|return;
block|}
try|try
block|{
name|String
name|query
init|=
literal|"status:new age:"
operator|+
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toMinutes
argument_list|(
name|cfg
operator|.
name|getAbandonAfter
argument_list|()
argument_list|)
operator|+
literal|"m"
decl_stmt|;
if|if
condition|(
operator|!
name|cfg
operator|.
name|getAbandonIfMergeable
argument_list|()
condition|)
block|{
name|query
operator|+=
literal|" -is:mergeable"
expr_stmt|;
block|}
name|List
argument_list|<
name|ChangeData
argument_list|>
name|changesToAbandon
init|=
name|queryProcessor
operator|.
name|enforceVisibility
argument_list|(
literal|false
argument_list|)
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
operator|.
name|entities
argument_list|()
decl_stmt|;
name|ImmutableMultimap
operator|.
name|Builder
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|,
name|ChangeControl
argument_list|>
name|builder
init|=
name|ImmutableMultimap
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|ChangeData
name|cd
range|:
name|changesToAbandon
control|)
block|{
name|ChangeControl
name|control
init|=
name|cd
operator|.
name|changeControl
argument_list|(
name|internalUser
argument_list|)
decl_stmt|;
name|builder
operator|.
name|put
argument_list|(
name|control
operator|.
name|getProject
argument_list|()
operator|.
name|getNameKey
argument_list|()
argument_list|,
name|control
argument_list|)
expr_stmt|;
block|}
name|int
name|count
init|=
literal|0
decl_stmt|;
name|Multimap
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|,
name|ChangeControl
argument_list|>
name|abandons
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|String
name|message
init|=
name|cfg
operator|.
name|getAbandonMessage
argument_list|()
decl_stmt|;
for|for
control|(
name|Project
operator|.
name|NameKey
name|project
range|:
name|abandons
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Collection
argument_list|<
name|ChangeControl
argument_list|>
name|changes
init|=
name|getValidChanges
argument_list|(
name|abandons
operator|.
name|get
argument_list|(
name|project
argument_list|)
argument_list|,
name|query
argument_list|)
decl_stmt|;
try|try
block|{
name|abandon
operator|.
name|batchAbandon
argument_list|(
name|project
argument_list|,
name|internalUser
argument_list|,
name|changes
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|count
operator|+=
name|changes
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|StringBuilder
name|msg
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"Failed to auto-abandon inactive change(s):"
argument_list|)
decl_stmt|;
for|for
control|(
name|ChangeControl
name|change
range|:
name|changes
control|)
block|{
name|msg
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|change
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|msg
operator|.
name|append
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
name|log
operator|.
name|error
argument_list|(
name|msg
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|log
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Auto-Abandoned %d of %d changes."
argument_list|,
name|count
argument_list|,
name|changesToAbandon
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|QueryParseException
decl||
name|OrmException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Failed to query inactive open changes for auto-abandoning."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getValidChanges ( Collection<ChangeControl> changeControls, String query)
specifier|private
name|Collection
argument_list|<
name|ChangeControl
argument_list|>
name|getValidChanges
parameter_list|(
name|Collection
argument_list|<
name|ChangeControl
argument_list|>
name|changeControls
parameter_list|,
name|String
name|query
parameter_list|)
throws|throws
name|OrmException
throws|,
name|QueryParseException
block|{
name|Collection
argument_list|<
name|ChangeControl
argument_list|>
name|validChanges
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ChangeControl
name|cc
range|:
name|changeControls
control|)
block|{
name|String
name|newQuery
init|=
name|query
operator|+
literal|" change:"
operator|+
name|cc
operator|.
name|getId
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ChangeData
argument_list|>
name|changesToAbandon
init|=
name|queryProcessor
operator|.
name|enforceVisibility
argument_list|(
literal|false
argument_list|)
operator|.
name|query
argument_list|(
name|queryBuilder
operator|.
name|parse
argument_list|(
name|newQuery
argument_list|)
argument_list|)
operator|.
name|entities
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|changesToAbandon
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|validChanges
operator|.
name|add
argument_list|(
name|cc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Change data with id \"{}\" does not satisfy the query \"{}\""
operator|+
literal|" any more, hence skipping it in clean up"
argument_list|,
name|cc
operator|.
name|getId
argument_list|()
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|validChanges
return|;
block|}
block|}
end_class

end_unit

