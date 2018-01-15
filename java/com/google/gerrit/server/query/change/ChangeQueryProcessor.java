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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkState
import|;
end_import

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
name|query
operator|.
name|change
operator|.
name|ChangeQueryBuilder
operator|.
name|FIELD_LIMIT
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
name|PluginDefinedInfo
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
name|index
operator|.
name|IndexConfig
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
name|QueryOptions
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
name|IndexPredicate
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
name|QueryProcessor
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
name|account
operator|.
name|AccountLimits
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
name|ChangeIndexCollection
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
name|ChangeIndexRewriter
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
name|ChangeSchemaDefinitions
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
name|IndexedChangeQuery
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
name|project
operator|.
name|ProjectCache
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
name|List
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

begin_comment
comment|/**  * Query processor for the change index.  *  *<p>Instances are one-time-use. Other singleton classes should inject a Provider rather than  * holding on to a single instance.  */
end_comment

begin_class
DECL|class|ChangeQueryProcessor
specifier|public
class|class
name|ChangeQueryProcessor
extends|extends
name|QueryProcessor
argument_list|<
name|ChangeData
argument_list|>
implements|implements
name|PluginDefinedAttributesFactory
block|{
comment|/**    * Register a ChangeAttributeFactory in a config Module like this:    *    *<p>bind(ChangeAttributeFactory.class) .annotatedWith(Exports.named("export-name"))    * .to(YourClass.class);    */
DECL|interface|ChangeAttributeFactory
specifier|public
interface|interface
name|ChangeAttributeFactory
block|{
DECL|method|create (ChangeData a, ChangeQueryProcessor qp, String plugin)
name|PluginDefinedInfo
name|create
parameter_list|(
name|ChangeData
name|a
parameter_list|,
name|ChangeQueryProcessor
name|qp
parameter_list|,
name|String
name|plugin
parameter_list|)
function_decl|;
block|}
DECL|field|db
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
decl_stmt|;
DECL|field|userProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|userProvider
decl_stmt|;
DECL|field|notesFactory
specifier|private
specifier|final
name|ChangeNotes
operator|.
name|Factory
name|notesFactory
decl_stmt|;
DECL|field|attributeFactories
specifier|private
specifier|final
name|DynamicMap
argument_list|<
name|ChangeAttributeFactory
argument_list|>
name|attributeFactories
decl_stmt|;
DECL|field|permissionBackend
specifier|private
specifier|final
name|PermissionBackend
name|permissionBackend
decl_stmt|;
DECL|field|projectCache
specifier|private
specifier|final
name|ProjectCache
name|projectCache
decl_stmt|;
static|static
block|{
comment|// It is assumed that basic rewrites do not touch visibleto predicates.
name|checkState
argument_list|(
operator|!
name|ChangeIsVisibleToPredicate
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|IndexPredicate
operator|.
name|class
argument_list|)
argument_list|,
literal|"ChangeQueryProcessor assumes visibleto is not used by the index rewriter."
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Inject
DECL|method|ChangeQueryProcessor ( Provider<CurrentUser> userProvider, AccountLimits.Factory limitsFactory, MetricMaker metricMaker, IndexConfig indexConfig, ChangeIndexCollection indexes, ChangeIndexRewriter rewriter, Provider<ReviewDb> db, ChangeNotes.Factory notesFactory, DynamicMap<ChangeAttributeFactory> attributeFactories, PermissionBackend permissionBackend, ProjectCache projectCache)
name|ChangeQueryProcessor
parameter_list|(
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|userProvider
parameter_list|,
name|AccountLimits
operator|.
name|Factory
name|limitsFactory
parameter_list|,
name|MetricMaker
name|metricMaker
parameter_list|,
name|IndexConfig
name|indexConfig
parameter_list|,
name|ChangeIndexCollection
name|indexes
parameter_list|,
name|ChangeIndexRewriter
name|rewriter
parameter_list|,
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|,
name|ChangeNotes
operator|.
name|Factory
name|notesFactory
parameter_list|,
name|DynamicMap
argument_list|<
name|ChangeAttributeFactory
argument_list|>
name|attributeFactories
parameter_list|,
name|PermissionBackend
name|permissionBackend
parameter_list|,
name|ProjectCache
name|projectCache
parameter_list|)
block|{
name|super
argument_list|(
name|metricMaker
argument_list|,
name|ChangeSchemaDefinitions
operator|.
name|INSTANCE
argument_list|,
name|indexConfig
argument_list|,
name|indexes
argument_list|,
name|rewriter
argument_list|,
name|FIELD_LIMIT
argument_list|,
parameter_list|()
lambda|->
name|limitsFactory
operator|.
name|create
argument_list|(
name|userProvider
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|getQueryLimit
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|userProvider
operator|=
name|userProvider
expr_stmt|;
name|this
operator|.
name|notesFactory
operator|=
name|notesFactory
expr_stmt|;
name|this
operator|.
name|attributeFactories
operator|=
name|attributeFactories
expr_stmt|;
name|this
operator|.
name|permissionBackend
operator|=
name|permissionBackend
expr_stmt|;
name|this
operator|.
name|projectCache
operator|=
name|projectCache
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|enforceVisibility (boolean enforce)
specifier|public
name|ChangeQueryProcessor
name|enforceVisibility
parameter_list|(
name|boolean
name|enforce
parameter_list|)
block|{
name|super
operator|.
name|enforceVisibility
argument_list|(
name|enforce
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|createOptions ( IndexConfig indexConfig, int start, int limit, Set<String> requestedFields)
specifier|protected
name|QueryOptions
name|createOptions
parameter_list|(
name|IndexConfig
name|indexConfig
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|limit
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|requestedFields
parameter_list|)
block|{
return|return
name|IndexedChangeQuery
operator|.
name|createOptions
argument_list|(
name|indexConfig
argument_list|,
name|start
argument_list|,
name|limit
argument_list|,
name|requestedFields
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|create (ChangeData cd)
specifier|public
name|List
argument_list|<
name|PluginDefinedInfo
argument_list|>
name|create
parameter_list|(
name|ChangeData
name|cd
parameter_list|)
block|{
name|List
argument_list|<
name|PluginDefinedInfo
argument_list|>
name|plugins
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|attributeFactories
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
name|String
name|plugin
range|:
name|attributeFactories
operator|.
name|plugins
argument_list|()
control|)
block|{
for|for
control|(
name|Provider
argument_list|<
name|ChangeAttributeFactory
argument_list|>
name|provider
range|:
name|attributeFactories
operator|.
name|byPlugin
argument_list|(
name|plugin
argument_list|)
operator|.
name|values
argument_list|()
control|)
block|{
name|PluginDefinedInfo
name|pda
init|=
literal|null
decl_stmt|;
try|try
block|{
name|pda
operator|=
name|provider
operator|.
name|get
argument_list|()
operator|.
name|create
argument_list|(
name|cd
argument_list|,
name|this
argument_list|,
name|plugin
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
comment|/* Eat runtime exceptions so that queries don't fail. */
block|}
if|if
condition|(
name|pda
operator|!=
literal|null
condition|)
block|{
name|pda
operator|.
name|name
operator|=
name|plugin
expr_stmt|;
name|plugins
operator|.
name|add
argument_list|(
name|pda
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|plugins
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|plugins
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|plugins
return|;
block|}
annotation|@
name|Override
DECL|method|enforceVisibility (Predicate<ChangeData> pred)
specifier|protected
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|enforceVisibility
parameter_list|(
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|pred
parameter_list|)
block|{
return|return
operator|new
name|AndChangeSource
argument_list|(
name|pred
argument_list|,
operator|new
name|ChangeIsVisibleToPredicate
argument_list|(
name|db
argument_list|,
name|notesFactory
argument_list|,
name|userProvider
operator|.
name|get
argument_list|()
argument_list|,
name|permissionBackend
argument_list|,
name|projectCache
argument_list|)
argument_list|,
name|start
argument_list|)
return|;
block|}
block|}
end_class

end_unit

