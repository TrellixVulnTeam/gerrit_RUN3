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
DECL|package|com.google.gerrit.server.extensions.webui
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|extensions
operator|.
name|webui
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
name|extensions
operator|.
name|conditions
operator|.
name|BooleanCondition
operator|.
name|and
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
name|extensions
operator|.
name|conditions
operator|.
name|BooleanCondition
operator|.
name|or
import|;
end_import

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
name|annotations
operator|.
name|VisibleForTesting
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
name|Predicate
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
name|Streams
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
name|flogger
operator|.
name|FluentLogger
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
name|Nullable
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
name|api
operator|.
name|access
operator|.
name|GlobalOrPluginPermission
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
name|conditions
operator|.
name|BooleanCondition
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
name|extensions
operator|.
name|registration
operator|.
name|Extension
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
name|PluginName
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
name|RestCollection
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
name|RestResource
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
name|RestView
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
name|webui
operator|.
name|PrivateInternals_UiActionDescription
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
name|webui
operator|.
name|UiAction
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
name|webui
operator|.
name|UiAction
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
name|Field
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
name|Timer1
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
name|PermissionBackendCondition
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
annotation|@
name|Singleton
DECL|class|UiActions
specifier|public
class|class
name|UiActions
block|{
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|FluentLogger
name|logger
init|=
name|FluentLogger
operator|.
name|forEnclosingClass
argument_list|()
decl_stmt|;
DECL|method|enabled ()
specifier|public
specifier|static
name|Predicate
argument_list|<
name|UiAction
operator|.
name|Description
argument_list|>
name|enabled
parameter_list|()
block|{
return|return
name|UiAction
operator|.
name|Description
operator|::
name|isEnabled
return|;
block|}
DECL|field|permissionBackend
specifier|private
specifier|final
name|PermissionBackend
name|permissionBackend
decl_stmt|;
DECL|field|uiActionLatency
specifier|private
specifier|final
name|Timer1
argument_list|<
name|String
argument_list|>
name|uiActionLatency
decl_stmt|;
annotation|@
name|Inject
DECL|method|UiActions (PermissionBackend permissionBackend, MetricMaker metricMaker)
name|UiActions
parameter_list|(
name|PermissionBackend
name|permissionBackend
parameter_list|,
name|MetricMaker
name|metricMaker
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
name|uiActionLatency
operator|=
name|metricMaker
operator|.
name|newTimer
argument_list|(
literal|"http/server/rest_api/ui_actions/latency"
argument_list|,
operator|new
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|metrics
operator|.
name|Description
argument_list|(
literal|"Latency for RestView#getDescription calls"
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
argument_list|,
name|Field
operator|.
name|ofString
argument_list|(
literal|"view"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|from ( RestCollection<?, R> collection, R resource)
specifier|public
parameter_list|<
name|R
extends|extends
name|RestResource
parameter_list|>
name|Iterable
argument_list|<
name|UiAction
operator|.
name|Description
argument_list|>
name|from
parameter_list|(
name|RestCollection
argument_list|<
name|?
argument_list|,
name|R
argument_list|>
name|collection
parameter_list|,
name|R
name|resource
parameter_list|)
block|{
return|return
name|from
argument_list|(
name|collection
operator|.
name|views
argument_list|()
argument_list|,
name|resource
argument_list|)
return|;
block|}
DECL|method|from ( DynamicMap<RestView<R>> views, R resource)
specifier|public
parameter_list|<
name|R
extends|extends
name|RestResource
parameter_list|>
name|Iterable
argument_list|<
name|UiAction
operator|.
name|Description
argument_list|>
name|from
parameter_list|(
name|DynamicMap
argument_list|<
name|RestView
argument_list|<
name|R
argument_list|>
argument_list|>
name|views
parameter_list|,
name|R
name|resource
parameter_list|)
block|{
name|List
argument_list|<
name|UiAction
operator|.
name|Description
argument_list|>
name|descs
init|=
name|Streams
operator|.
name|stream
argument_list|(
name|views
argument_list|)
operator|.
name|map
argument_list|(
name|e
lambda|->
name|describe
argument_list|(
name|e
argument_list|,
name|resource
argument_list|)
argument_list|)
operator|.
name|filter
argument_list|(
name|Objects
operator|::
name|nonNull
argument_list|)
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|PermissionBackendCondition
argument_list|>
name|conds
init|=
name|Streams
operator|.
name|concat
argument_list|(
name|descs
operator|.
name|stream
argument_list|()
operator|.
name|flatMap
argument_list|(
name|u
lambda|->
name|Streams
operator|.
name|stream
argument_list|(
name|visibleCondition
argument_list|(
name|u
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|descs
operator|.
name|stream
argument_list|()
operator|.
name|flatMap
argument_list|(
name|u
lambda|->
name|Streams
operator|.
name|stream
argument_list|(
name|enabledCondition
argument_list|(
name|u
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|evaluatePermissionBackendConditions
argument_list|(
name|permissionBackend
argument_list|,
name|conds
argument_list|)
expr_stmt|;
return|return
name|descs
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|Description
operator|::
name|isVisible
argument_list|)
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|evaluatePermissionBackendConditions ( PermissionBackend perm, List<PermissionBackendCondition> conds)
specifier|static
name|void
name|evaluatePermissionBackendConditions
parameter_list|(
name|PermissionBackend
name|perm
parameter_list|,
name|List
argument_list|<
name|PermissionBackendCondition
argument_list|>
name|conds
parameter_list|)
block|{
name|Map
argument_list|<
name|PermissionBackendCondition
argument_list|,
name|PermissionBackendCondition
argument_list|>
name|dedupedConds
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|conds
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|PermissionBackendCondition
name|cond
range|:
name|conds
control|)
block|{
name|dedupedConds
operator|.
name|put
argument_list|(
name|cond
argument_list|,
name|cond
argument_list|)
expr_stmt|;
block|}
name|perm
operator|.
name|bulkEvaluateTest
argument_list|(
name|dedupedConds
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|PermissionBackendCondition
name|cond
range|:
name|conds
control|)
block|{
name|cond
operator|.
name|set
argument_list|(
name|dedupedConds
operator|.
name|get
argument_list|(
name|cond
argument_list|)
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|visibleCondition (Description u)
specifier|private
specifier|static
name|Iterable
argument_list|<
name|PermissionBackendCondition
argument_list|>
name|visibleCondition
parameter_list|(
name|Description
name|u
parameter_list|)
block|{
return|return
name|u
operator|.
name|getVisibleCondition
argument_list|()
operator|.
name|reduce
argument_list|()
operator|.
name|children
argument_list|(
name|PermissionBackendCondition
operator|.
name|class
argument_list|)
return|;
block|}
DECL|method|enabledCondition (Description u)
specifier|private
specifier|static
name|Iterable
argument_list|<
name|PermissionBackendCondition
argument_list|>
name|enabledCondition
parameter_list|(
name|Description
name|u
parameter_list|)
block|{
return|return
name|u
operator|.
name|getEnabledCondition
argument_list|()
operator|.
name|reduce
argument_list|()
operator|.
name|children
argument_list|(
name|PermissionBackendCondition
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Nullable
DECL|method|describe ( Extension<RestView<R>> e, R resource)
specifier|private
parameter_list|<
name|R
extends|extends
name|RestResource
parameter_list|>
name|UiAction
operator|.
name|Description
name|describe
parameter_list|(
name|Extension
argument_list|<
name|RestView
argument_list|<
name|R
argument_list|>
argument_list|>
name|e
parameter_list|,
name|R
name|resource
parameter_list|)
block|{
name|int
name|d
init|=
name|e
operator|.
name|getExportName
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|'.'
argument_list|)
decl_stmt|;
if|if
condition|(
name|d
operator|<
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
name|RestView
argument_list|<
name|R
argument_list|>
name|view
decl_stmt|;
try|try
block|{
name|view
operator|=
name|e
operator|.
name|getProvider
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|err
parameter_list|)
block|{
name|logger
operator|.
name|atSevere
argument_list|()
operator|.
name|withCause
argument_list|(
name|err
argument_list|)
operator|.
name|log
argument_list|(
literal|"error creating view %s.%s"
argument_list|,
name|e
operator|.
name|getPluginName
argument_list|()
argument_list|,
name|e
operator|.
name|getExportName
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|view
operator|instanceof
name|UiAction
operator|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
name|name
init|=
name|e
operator|.
name|getExportName
argument_list|()
operator|.
name|substring
argument_list|(
name|d
operator|+
literal|1
argument_list|)
decl_stmt|;
name|UiAction
operator|.
name|Description
name|dsc
decl_stmt|;
try|try
init|(
name|Timer1
operator|.
name|Context
name|ignored
init|=
name|uiActionLatency
operator|.
name|start
argument_list|(
name|name
argument_list|)
init|)
block|{
name|dsc
operator|=
operator|(
operator|(
name|UiAction
argument_list|<
name|R
argument_list|>
operator|)
name|view
operator|)
operator|.
name|getDescription
argument_list|(
name|resource
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|dsc
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Set
argument_list|<
name|GlobalOrPluginPermission
argument_list|>
name|globalRequired
decl_stmt|;
try|try
block|{
name|globalRequired
operator|=
name|GlobalPermission
operator|.
name|fromAnnotation
argument_list|(
name|e
operator|.
name|getPluginName
argument_list|()
argument_list|,
name|view
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionBackendException
name|err
parameter_list|)
block|{
name|logger
operator|.
name|atSevere
argument_list|()
operator|.
name|withCause
argument_list|(
name|err
argument_list|)
operator|.
name|log
argument_list|(
literal|"exception testing view %s.%s"
argument_list|,
name|e
operator|.
name|getPluginName
argument_list|()
argument_list|,
name|e
operator|.
name|getExportName
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
name|globalRequired
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|PermissionBackend
operator|.
name|WithUser
name|withUser
init|=
name|permissionBackend
operator|.
name|currentUser
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|GlobalOrPluginPermission
argument_list|>
name|i
init|=
name|globalRequired
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|BooleanCondition
name|p
init|=
name|withUser
operator|.
name|testCond
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|p
operator|=
name|or
argument_list|(
name|p
argument_list|,
name|withUser
operator|.
name|testCond
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|dsc
operator|.
name|setVisible
argument_list|(
name|and
argument_list|(
name|p
argument_list|,
name|dsc
operator|.
name|getVisibleCondition
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|PrivateInternals_UiActionDescription
operator|.
name|setMethod
argument_list|(
name|dsc
argument_list|,
name|e
operator|.
name|getExportName
argument_list|()
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|d
argument_list|)
argument_list|)
expr_stmt|;
name|PrivateInternals_UiActionDescription
operator|.
name|setId
argument_list|(
name|dsc
argument_list|,
name|PluginName
operator|.
name|GERRIT
operator|.
name|equals
argument_list|(
name|e
operator|.
name|getPluginName
argument_list|()
argument_list|)
condition|?
name|name
else|:
name|e
operator|.
name|getPluginName
argument_list|()
operator|+
literal|'~'
operator|+
name|name
argument_list|)
expr_stmt|;
return|return
name|dsc
return|;
block|}
block|}
end_class

end_unit

