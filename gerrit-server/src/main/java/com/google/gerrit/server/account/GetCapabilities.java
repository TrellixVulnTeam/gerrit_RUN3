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
DECL|package|com.google.gerrit.server.account
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|account
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
name|common
operator|.
name|data
operator|.
name|GlobalCapability
operator|.
name|PRIORITY
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
name|gerrit
operator|.
name|common
operator|.
name|data
operator|.
name|GlobalCapability
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
name|PermissionRange
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
name|api
operator|.
name|access
operator|.
name|PluginPermission
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
name|config
operator|.
name|CapabilityDefinition
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
name|OptionUtil
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
name|OutputFormat
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
name|AccountResource
operator|.
name|Capability
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
name|QueueProvider
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
name|gson
operator|.
name|reflect
operator|.
name|TypeToken
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
name|LinkedHashMap
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
name|kohsuke
operator|.
name|args4j
operator|.
name|Option
import|;
end_import

begin_class
DECL|class|GetCapabilities
class|class
name|GetCapabilities
implements|implements
name|RestReadView
argument_list|<
name|AccountResource
argument_list|>
block|{
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"-q"
argument_list|,
name|metaVar
operator|=
literal|"CAP"
argument_list|,
name|usage
operator|=
literal|"Capability to inspect"
argument_list|)
DECL|method|addQuery (String name)
name|void
name|addQuery
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|query
operator|==
literal|null
condition|)
block|{
name|query
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|Iterables
operator|.
name|addAll
argument_list|(
name|query
argument_list|,
name|OptionUtil
operator|.
name|splitOptionValue
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|field|query
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|query
decl_stmt|;
DECL|field|permissionBackend
specifier|private
specifier|final
name|PermissionBackend
name|permissionBackend
decl_stmt|;
DECL|field|limitsFactory
specifier|private
specifier|final
name|AccountLimits
operator|.
name|Factory
name|limitsFactory
decl_stmt|;
DECL|field|self
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|self
decl_stmt|;
DECL|field|pluginCapabilities
specifier|private
specifier|final
name|DynamicMap
argument_list|<
name|CapabilityDefinition
argument_list|>
name|pluginCapabilities
decl_stmt|;
annotation|@
name|Inject
DECL|method|GetCapabilities ( PermissionBackend permissionBackend, AccountLimits.Factory limitsFactory, Provider<CurrentUser> self, DynamicMap<CapabilityDefinition> pluginCapabilities)
name|GetCapabilities
parameter_list|(
name|PermissionBackend
name|permissionBackend
parameter_list|,
name|AccountLimits
operator|.
name|Factory
name|limitsFactory
parameter_list|,
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|self
parameter_list|,
name|DynamicMap
argument_list|<
name|CapabilityDefinition
argument_list|>
name|pluginCapabilities
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
name|limitsFactory
operator|=
name|limitsFactory
expr_stmt|;
name|this
operator|.
name|self
operator|=
name|self
expr_stmt|;
name|this
operator|.
name|pluginCapabilities
operator|=
name|pluginCapabilities
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (AccountResource resource)
specifier|public
name|Object
name|apply
parameter_list|(
name|AccountResource
name|resource
parameter_list|)
throws|throws
name|AuthException
throws|,
name|PermissionBackendException
block|{
name|PermissionBackend
operator|.
name|WithUser
name|perm
init|=
name|permissionBackend
operator|.
name|user
argument_list|(
name|self
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|self
operator|.
name|get
argument_list|()
operator|.
name|hasSameAccountId
argument_list|(
name|resource
operator|.
name|getUser
argument_list|()
argument_list|)
condition|)
block|{
name|perm
operator|.
name|check
argument_list|(
name|GlobalPermission
operator|.
name|ADMINISTRATE_SERVER
argument_list|)
expr_stmt|;
name|perm
operator|=
name|permissionBackend
operator|.
name|user
argument_list|(
name|resource
operator|.
name|getUser
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|have
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|GlobalOrPluginPermission
name|p
range|:
name|perm
operator|.
name|test
argument_list|(
name|permissionsToTest
argument_list|()
argument_list|)
control|)
block|{
name|have
operator|.
name|put
argument_list|(
name|p
operator|.
name|permissionName
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|AccountLimits
name|limits
init|=
name|limitsFactory
operator|.
name|create
argument_list|(
name|resource
operator|.
name|getUser
argument_list|()
argument_list|)
decl_stmt|;
name|addRanges
argument_list|(
name|have
argument_list|,
name|limits
argument_list|)
expr_stmt|;
name|addPriority
argument_list|(
name|have
argument_list|,
name|limits
argument_list|)
expr_stmt|;
return|return
name|OutputFormat
operator|.
name|JSON
operator|.
name|newGson
argument_list|()
operator|.
name|toJsonTree
argument_list|(
name|have
argument_list|,
operator|new
name|TypeToken
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
block|{}
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
DECL|method|permissionsToTest ()
specifier|private
name|Set
argument_list|<
name|GlobalOrPluginPermission
argument_list|>
name|permissionsToTest
parameter_list|()
block|{
name|Set
argument_list|<
name|GlobalOrPluginPermission
argument_list|>
name|toTest
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|GlobalPermission
name|p
range|:
name|GlobalPermission
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|want
argument_list|(
name|p
operator|.
name|permissionName
argument_list|()
argument_list|)
condition|)
block|{
name|toTest
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|String
name|pluginName
range|:
name|pluginCapabilities
operator|.
name|plugins
argument_list|()
control|)
block|{
for|for
control|(
name|String
name|capability
range|:
name|pluginCapabilities
operator|.
name|byPlugin
argument_list|(
name|pluginName
argument_list|)
operator|.
name|keySet
argument_list|()
control|)
block|{
name|PluginPermission
name|p
init|=
operator|new
name|PluginPermission
argument_list|(
name|pluginName
argument_list|,
name|capability
argument_list|)
decl_stmt|;
if|if
condition|(
name|want
argument_list|(
name|p
operator|.
name|permissionName
argument_list|()
argument_list|)
condition|)
block|{
name|toTest
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|toTest
return|;
block|}
DECL|method|want (String name)
specifier|private
name|boolean
name|want
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|query
operator|==
literal|null
operator|||
name|query
operator|.
name|contains
argument_list|(
name|name
operator|.
name|toLowerCase
argument_list|()
argument_list|)
return|;
block|}
DECL|method|addRanges (Map<String, Object> have, AccountLimits limits)
specifier|private
name|void
name|addRanges
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|have
parameter_list|,
name|AccountLimits
name|limits
parameter_list|)
block|{
for|for
control|(
name|String
name|name
range|:
name|GlobalCapability
operator|.
name|getRangeNames
argument_list|()
control|)
block|{
if|if
condition|(
name|want
argument_list|(
name|name
argument_list|)
operator|&&
name|limits
operator|.
name|hasExplicitRange
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|have
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|Range
argument_list|(
name|limits
operator|.
name|getRange
argument_list|(
name|name
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|addPriority (Map<String, Object> have, AccountLimits limits)
specifier|private
name|void
name|addPriority
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|have
parameter_list|,
name|AccountLimits
name|limits
parameter_list|)
block|{
name|QueueProvider
operator|.
name|QueueType
name|queue
init|=
name|limits
operator|.
name|getQueueType
argument_list|()
decl_stmt|;
if|if
condition|(
name|queue
operator|!=
name|QueueProvider
operator|.
name|QueueType
operator|.
name|INTERACTIVE
operator|||
operator|(
name|query
operator|!=
literal|null
operator|&&
name|query
operator|.
name|contains
argument_list|(
name|PRIORITY
argument_list|)
operator|)
condition|)
block|{
name|have
operator|.
name|put
argument_list|(
name|PRIORITY
argument_list|,
name|queue
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|Range
specifier|private
specifier|static
class|class
name|Range
block|{
DECL|field|range
specifier|private
specifier|transient
name|PermissionRange
name|range
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
DECL|field|min
specifier|private
name|int
name|min
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
DECL|field|max
specifier|private
name|int
name|max
decl_stmt|;
DECL|method|Range (PermissionRange r)
name|Range
parameter_list|(
name|PermissionRange
name|r
parameter_list|)
block|{
name|range
operator|=
name|r
expr_stmt|;
name|min
operator|=
name|r
operator|.
name|getMin
argument_list|()
expr_stmt|;
name|max
operator|=
name|r
operator|.
name|getMax
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|range
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
annotation|@
name|Singleton
DECL|class|CheckOne
specifier|static
class|class
name|CheckOne
implements|implements
name|RestReadView
argument_list|<
name|AccountResource
operator|.
name|Capability
argument_list|>
block|{
annotation|@
name|Override
DECL|method|apply (Capability resource)
specifier|public
name|Response
argument_list|<
name|String
argument_list|>
name|apply
parameter_list|(
name|Capability
name|resource
parameter_list|)
block|{
return|return
name|Response
operator|.
name|ok
argument_list|(
literal|"ok"
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

