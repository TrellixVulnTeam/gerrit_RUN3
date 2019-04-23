begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2008 The Android Open Source Project
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
DECL|package|com.google.gerrit.reviewdb.client
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|reviewdb
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
name|extensions
operator|.
name|client
operator|.
name|InheritableBoolean
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
name|ProjectState
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
name|SubmitType
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
name|StringKey
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
name|HashMap
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

begin_comment
comment|/** Projects match a source code repository managed by Gerrit */
end_comment

begin_class
DECL|class|Project
specifier|public
specifier|final
class|class
name|Project
block|{
comment|/** Default submit type for new projects. */
DECL|field|DEFAULT_SUBMIT_TYPE
specifier|public
specifier|static
specifier|final
name|SubmitType
name|DEFAULT_SUBMIT_TYPE
init|=
name|SubmitType
operator|.
name|MERGE_IF_NECESSARY
decl_stmt|;
comment|/** Default submit type for root project (All-Projects). */
DECL|field|DEFAULT_ALL_PROJECTS_SUBMIT_TYPE
specifier|public
specifier|static
specifier|final
name|SubmitType
name|DEFAULT_ALL_PROJECTS_SUBMIT_TYPE
init|=
name|SubmitType
operator|.
name|MERGE_IF_NECESSARY
decl_stmt|;
DECL|method|nameKey (String name)
specifier|public
specifier|static
name|NameKey
name|nameKey
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|NameKey
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/** Project name key */
DECL|class|NameKey
specifier|public
specifier|static
class|class
name|NameKey
extends|extends
name|StringKey
argument_list|<
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|client
operator|.
name|Key
argument_list|<
name|?
argument_list|>
argument_list|>
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|field|name
specifier|protected
name|String
name|name
decl_stmt|;
DECL|method|NameKey ()
specifier|protected
name|NameKey
parameter_list|()
block|{}
DECL|method|NameKey (String n)
specifier|public
name|NameKey
parameter_list|(
name|String
name|n
parameter_list|)
block|{
name|name
operator|=
name|n
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get ()
specifier|public
name|String
name|get
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
DECL|method|set (String newValue)
specifier|protected
name|void
name|set
parameter_list|(
name|String
name|newValue
parameter_list|)
block|{
name|name
operator|=
name|newValue
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|get
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object b)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|b
parameter_list|)
block|{
if|if
condition|(
name|b
operator|instanceof
name|NameKey
condition|)
block|{
return|return
name|get
argument_list|()
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|NameKey
operator|)
name|b
operator|)
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/** Parse a Project.NameKey out of a string representation. */
DECL|method|parse (String str)
specifier|public
specifier|static
name|NameKey
name|parse
parameter_list|(
name|String
name|str
parameter_list|)
block|{
specifier|final
name|NameKey
name|r
init|=
operator|new
name|NameKey
argument_list|()
decl_stmt|;
name|r
operator|.
name|fromString
argument_list|(
name|str
argument_list|)
expr_stmt|;
return|return
name|r
return|;
block|}
DECL|method|asStringOrNull (NameKey key)
specifier|public
specifier|static
name|String
name|asStringOrNull
parameter_list|(
name|NameKey
name|key
parameter_list|)
block|{
return|return
name|key
operator|==
literal|null
condition|?
literal|null
else|:
name|key
operator|.
name|get
argument_list|()
return|;
block|}
block|}
DECL|field|name
specifier|protected
name|NameKey
name|name
decl_stmt|;
DECL|field|description
specifier|protected
name|String
name|description
decl_stmt|;
DECL|field|booleanConfigs
specifier|protected
name|Map
argument_list|<
name|BooleanProjectConfig
argument_list|,
name|InheritableBoolean
argument_list|>
name|booleanConfigs
decl_stmt|;
DECL|field|submitType
specifier|protected
name|SubmitType
name|submitType
decl_stmt|;
DECL|field|state
specifier|protected
name|ProjectState
name|state
decl_stmt|;
DECL|field|parent
specifier|protected
name|NameKey
name|parent
decl_stmt|;
DECL|field|maxObjectSizeLimit
specifier|protected
name|String
name|maxObjectSizeLimit
decl_stmt|;
DECL|field|defaultDashboardId
specifier|protected
name|String
name|defaultDashboardId
decl_stmt|;
DECL|field|localDefaultDashboardId
specifier|protected
name|String
name|localDefaultDashboardId
decl_stmt|;
DECL|field|configRefState
specifier|protected
name|String
name|configRefState
decl_stmt|;
DECL|method|Project ()
specifier|protected
name|Project
parameter_list|()
block|{}
DECL|method|Project (Project.NameKey nameKey)
specifier|public
name|Project
parameter_list|(
name|Project
operator|.
name|NameKey
name|nameKey
parameter_list|)
block|{
name|name
operator|=
name|nameKey
expr_stmt|;
name|submitType
operator|=
name|SubmitType
operator|.
name|MERGE_IF_NECESSARY
expr_stmt|;
name|state
operator|=
name|ProjectState
operator|.
name|ACTIVE
expr_stmt|;
name|booleanConfigs
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|Arrays
operator|.
name|stream
argument_list|(
name|BooleanProjectConfig
operator|.
name|values
argument_list|()
argument_list|)
operator|.
name|forEach
argument_list|(
name|c
lambda|->
name|booleanConfigs
operator|.
name|put
argument_list|(
name|c
argument_list|,
name|InheritableBoolean
operator|.
name|INHERIT
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getNameKey ()
specifier|public
name|Project
operator|.
name|NameKey
name|getNameKey
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
operator|!=
literal|null
condition|?
name|name
operator|.
name|get
argument_list|()
else|:
literal|null
return|;
block|}
DECL|method|getDescription ()
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|description
return|;
block|}
DECL|method|setDescription (String d)
specifier|public
name|void
name|setDescription
parameter_list|(
name|String
name|d
parameter_list|)
block|{
name|description
operator|=
name|d
expr_stmt|;
block|}
DECL|method|getMaxObjectSizeLimit ()
specifier|public
name|String
name|getMaxObjectSizeLimit
parameter_list|()
block|{
return|return
name|maxObjectSizeLimit
return|;
block|}
DECL|method|getBooleanConfig (BooleanProjectConfig config)
specifier|public
name|InheritableBoolean
name|getBooleanConfig
parameter_list|(
name|BooleanProjectConfig
name|config
parameter_list|)
block|{
return|return
name|booleanConfigs
operator|.
name|get
argument_list|(
name|config
argument_list|)
return|;
block|}
DECL|method|setBooleanConfig (BooleanProjectConfig config, InheritableBoolean val)
specifier|public
name|void
name|setBooleanConfig
parameter_list|(
name|BooleanProjectConfig
name|config
parameter_list|,
name|InheritableBoolean
name|val
parameter_list|)
block|{
name|booleanConfigs
operator|.
name|replace
argument_list|(
name|config
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
DECL|method|setMaxObjectSizeLimit (String limit)
specifier|public
name|void
name|setMaxObjectSizeLimit
parameter_list|(
name|String
name|limit
parameter_list|)
block|{
name|maxObjectSizeLimit
operator|=
name|limit
expr_stmt|;
block|}
comment|/**    * Submit type as configured in {@code project.config}.    *    *<p>Does not take inheritance into account, i.e. may return {@link SubmitType#INHERIT}.    *    * @return submit type.    */
DECL|method|getConfiguredSubmitType ()
specifier|public
name|SubmitType
name|getConfiguredSubmitType
parameter_list|()
block|{
return|return
name|submitType
return|;
block|}
DECL|method|setSubmitType (SubmitType type)
specifier|public
name|void
name|setSubmitType
parameter_list|(
name|SubmitType
name|type
parameter_list|)
block|{
name|submitType
operator|=
name|type
expr_stmt|;
block|}
DECL|method|getState ()
specifier|public
name|ProjectState
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
DECL|method|setState (ProjectState newState)
specifier|public
name|void
name|setState
parameter_list|(
name|ProjectState
name|newState
parameter_list|)
block|{
name|state
operator|=
name|newState
expr_stmt|;
block|}
DECL|method|getDefaultDashboard ()
specifier|public
name|String
name|getDefaultDashboard
parameter_list|()
block|{
return|return
name|defaultDashboardId
return|;
block|}
DECL|method|setDefaultDashboard (String defaultDashboardId)
specifier|public
name|void
name|setDefaultDashboard
parameter_list|(
name|String
name|defaultDashboardId
parameter_list|)
block|{
name|this
operator|.
name|defaultDashboardId
operator|=
name|defaultDashboardId
expr_stmt|;
block|}
DECL|method|getLocalDefaultDashboard ()
specifier|public
name|String
name|getLocalDefaultDashboard
parameter_list|()
block|{
return|return
name|localDefaultDashboardId
return|;
block|}
DECL|method|setLocalDefaultDashboard (String localDefaultDashboardId)
specifier|public
name|void
name|setLocalDefaultDashboard
parameter_list|(
name|String
name|localDefaultDashboardId
parameter_list|)
block|{
name|this
operator|.
name|localDefaultDashboardId
operator|=
name|localDefaultDashboardId
expr_stmt|;
block|}
comment|/**    * Returns the name key of the parent project.    *    * @return name key of the parent project, {@code null} if this project is the wild project,    *     {@code null} or the name key of the wild project if this project is a direct child of the    *     wild project    */
DECL|method|getParent ()
specifier|public
name|Project
operator|.
name|NameKey
name|getParent
parameter_list|()
block|{
return|return
name|parent
return|;
block|}
comment|/**    * Returns the name key of the parent project.    *    * @param allProjectsName name key of the wild project    * @return name key of the parent project, {@code null} if this project is the All-Projects    *     project    */
DECL|method|getParent (Project.NameKey allProjectsName)
specifier|public
name|Project
operator|.
name|NameKey
name|getParent
parameter_list|(
name|Project
operator|.
name|NameKey
name|allProjectsName
parameter_list|)
block|{
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
return|return
name|parent
return|;
block|}
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|allProjectsName
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|allProjectsName
return|;
block|}
DECL|method|getParentName ()
specifier|public
name|String
name|getParentName
parameter_list|()
block|{
return|return
name|parent
operator|!=
literal|null
condition|?
name|parent
operator|.
name|get
argument_list|()
else|:
literal|null
return|;
block|}
DECL|method|setParentName (String n)
specifier|public
name|void
name|setParentName
parameter_list|(
name|String
name|n
parameter_list|)
block|{
name|parent
operator|=
name|n
operator|!=
literal|null
condition|?
operator|new
name|NameKey
argument_list|(
name|n
argument_list|)
else|:
literal|null
expr_stmt|;
block|}
DECL|method|setParentName (NameKey n)
specifier|public
name|void
name|setParentName
parameter_list|(
name|NameKey
name|n
parameter_list|)
block|{
name|parent
operator|=
name|n
expr_stmt|;
block|}
comment|/** Returns the {@code ObjectId} as 40 digit hex of {@code refs/meta/config}'s HEAD. */
DECL|method|getConfigRefState ()
specifier|public
name|String
name|getConfigRefState
parameter_list|()
block|{
return|return
name|configRefState
return|;
block|}
comment|/** Sets the {@code ObjectId} as 40 digit hex of {@code refs/meta/config}'s HEAD. */
DECL|method|setConfigRefState (String state)
specifier|public
name|void
name|setConfigRefState
parameter_list|(
name|String
name|state
parameter_list|)
block|{
name|configRefState
operator|=
name|state
expr_stmt|;
block|}
block|}
end_class

end_unit

