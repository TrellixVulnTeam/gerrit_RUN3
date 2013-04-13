begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2011 The Android Open Source Project
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
DECL|package|com.google.gerrit.common.data
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|common
operator|.
name|data
package|;
end_package

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
name|Collections
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

begin_comment
comment|/** Server wide capabilities. Represented as {@link Permission} objects. */
end_comment

begin_class
DECL|class|GlobalCapability
specifier|public
class|class
name|GlobalCapability
block|{
comment|/** Ability to access the database (with gsql). */
DECL|field|ACCESS_DATABASE
specifier|public
specifier|static
specifier|final
name|String
name|ACCESS_DATABASE
init|=
literal|"accessDatabase"
decl_stmt|;
comment|/**    * Denotes the server's administrators.    *<p>    * This is similar to UNIX root, or Windows SYSTEM account. Any user that    * has this capability can perform almost any other action, or can grant    * themselves the power to perform any other action on the site. Most of    * the other capabilities and permissions fall-back to the predicate    * "OR user has capability ADMINISTRATE_SERVER".    */
DECL|field|ADMINISTRATE_SERVER
specifier|public
specifier|static
specifier|final
name|String
name|ADMINISTRATE_SERVER
init|=
literal|"administrateServer"
decl_stmt|;
comment|/** Can create any account on the server. */
DECL|field|CREATE_ACCOUNT
specifier|public
specifier|static
specifier|final
name|String
name|CREATE_ACCOUNT
init|=
literal|"createAccount"
decl_stmt|;
comment|/** Can create any group on the server. */
DECL|field|CREATE_GROUP
specifier|public
specifier|static
specifier|final
name|String
name|CREATE_GROUP
init|=
literal|"createGroup"
decl_stmt|;
comment|/** Can create any project on the server. */
DECL|field|CREATE_PROJECT
specifier|public
specifier|static
specifier|final
name|String
name|CREATE_PROJECT
init|=
literal|"createProject"
decl_stmt|;
comment|/**    * Denotes who may email change reviewers and watchers.    *<p>    * This can be used to deny build bots from emailing reviewers and people who    * watch the change. Instead, only the authors of the change and those who    * starred it will be emailed. The allow rules are evaluated before deny    * rules, however the default is to allow emailing, if no explicit rule is    * matched.    */
DECL|field|EMAIL_REVIEWERS
specifier|public
specifier|static
specifier|final
name|String
name|EMAIL_REVIEWERS
init|=
literal|"emailReviewers"
decl_stmt|;
comment|/** Can flush any cache except the active web_sessions cache. */
DECL|field|FLUSH_CACHES
specifier|public
specifier|static
specifier|final
name|String
name|FLUSH_CACHES
init|=
literal|"flushCaches"
decl_stmt|;
comment|/** Can terminate any task using the kill command. */
DECL|field|KILL_TASK
specifier|public
specifier|static
specifier|final
name|String
name|KILL_TASK
init|=
literal|"killTask"
decl_stmt|;
comment|/** Queue a user can access to submit their tasks to. */
DECL|field|PRIORITY
specifier|public
specifier|static
specifier|final
name|String
name|PRIORITY
init|=
literal|"priority"
decl_stmt|;
comment|/** Maximum result limit per executed query. */
DECL|field|QUERY_LIMIT
specifier|public
specifier|static
specifier|final
name|String
name|QUERY_LIMIT
init|=
literal|"queryLimit"
decl_stmt|;
comment|/** Can run the Git garbage collection. */
DECL|field|RUN_GC
specifier|public
specifier|static
specifier|final
name|String
name|RUN_GC
init|=
literal|"runGC"
decl_stmt|;
comment|/** Forcefully restart replication to any configured destination. */
DECL|field|START_REPLICATION
specifier|public
specifier|static
specifier|final
name|String
name|START_REPLICATION
init|=
literal|"startReplication"
decl_stmt|;
comment|/** Can perform streaming of Gerrit events. */
DECL|field|STREAM_EVENTS
specifier|public
specifier|static
specifier|final
name|String
name|STREAM_EVENTS
init|=
literal|"streamEvents"
decl_stmt|;
comment|/** Can view the server's current cache states. */
DECL|field|VIEW_CACHES
specifier|public
specifier|static
specifier|final
name|String
name|VIEW_CACHES
init|=
literal|"viewCaches"
decl_stmt|;
comment|/** Can view open connections to the server's SSH port. */
DECL|field|VIEW_CONNECTIONS
specifier|public
specifier|static
specifier|final
name|String
name|VIEW_CONNECTIONS
init|=
literal|"viewConnections"
decl_stmt|;
comment|/** Can view all pending tasks in the queue (not just the filtered set). */
DECL|field|VIEW_QUEUE
specifier|public
specifier|static
specifier|final
name|String
name|VIEW_QUEUE
init|=
literal|"viewQueue"
decl_stmt|;
DECL|field|NAMES_ALL
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|NAMES_ALL
decl_stmt|;
DECL|field|NAMES_LC
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|NAMES_LC
decl_stmt|;
static|static
block|{
name|NAMES_ALL
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|NAMES_ALL
operator|.
name|add
argument_list|(
name|ACCESS_DATABASE
argument_list|)
expr_stmt|;
name|NAMES_ALL
operator|.
name|add
argument_list|(
name|ADMINISTRATE_SERVER
argument_list|)
expr_stmt|;
name|NAMES_ALL
operator|.
name|add
argument_list|(
name|CREATE_ACCOUNT
argument_list|)
expr_stmt|;
name|NAMES_ALL
operator|.
name|add
argument_list|(
name|CREATE_GROUP
argument_list|)
expr_stmt|;
name|NAMES_ALL
operator|.
name|add
argument_list|(
name|CREATE_PROJECT
argument_list|)
expr_stmt|;
name|NAMES_ALL
operator|.
name|add
argument_list|(
name|EMAIL_REVIEWERS
argument_list|)
expr_stmt|;
name|NAMES_ALL
operator|.
name|add
argument_list|(
name|FLUSH_CACHES
argument_list|)
expr_stmt|;
name|NAMES_ALL
operator|.
name|add
argument_list|(
name|KILL_TASK
argument_list|)
expr_stmt|;
name|NAMES_ALL
operator|.
name|add
argument_list|(
name|PRIORITY
argument_list|)
expr_stmt|;
name|NAMES_ALL
operator|.
name|add
argument_list|(
name|QUERY_LIMIT
argument_list|)
expr_stmt|;
name|NAMES_ALL
operator|.
name|add
argument_list|(
name|RUN_GC
argument_list|)
expr_stmt|;
name|NAMES_ALL
operator|.
name|add
argument_list|(
name|START_REPLICATION
argument_list|)
expr_stmt|;
name|NAMES_ALL
operator|.
name|add
argument_list|(
name|STREAM_EVENTS
argument_list|)
expr_stmt|;
name|NAMES_ALL
operator|.
name|add
argument_list|(
name|VIEW_CACHES
argument_list|)
expr_stmt|;
name|NAMES_ALL
operator|.
name|add
argument_list|(
name|VIEW_CONNECTIONS
argument_list|)
expr_stmt|;
name|NAMES_ALL
operator|.
name|add
argument_list|(
name|VIEW_QUEUE
argument_list|)
expr_stmt|;
name|NAMES_LC
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|NAMES_ALL
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|name
range|:
name|NAMES_ALL
control|)
block|{
name|NAMES_LC
operator|.
name|add
argument_list|(
name|name
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** @return all valid capability names. */
DECL|method|getAllNames ()
specifier|public
specifier|static
name|Collection
argument_list|<
name|String
argument_list|>
name|getAllNames
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|NAMES_ALL
argument_list|)
return|;
block|}
comment|/** @return true if the name is recognized as a capability name. */
DECL|method|isCapability (String varName)
specifier|public
specifier|static
name|boolean
name|isCapability
parameter_list|(
name|String
name|varName
parameter_list|)
block|{
return|return
name|NAMES_LC
operator|.
name|contains
argument_list|(
name|varName
operator|.
name|toLowerCase
argument_list|()
argument_list|)
return|;
block|}
comment|/** @return true if the capability should have a range attached. */
DECL|method|hasRange (String varName)
specifier|public
specifier|static
name|boolean
name|hasRange
parameter_list|(
name|String
name|varName
parameter_list|)
block|{
return|return
name|QUERY_LIMIT
operator|.
name|equalsIgnoreCase
argument_list|(
name|varName
argument_list|)
return|;
block|}
comment|/** @return the valid range for the capability if it has one, otherwise null. */
DECL|method|getRange (String varName)
specifier|public
specifier|static
name|PermissionRange
operator|.
name|WithDefaults
name|getRange
parameter_list|(
name|String
name|varName
parameter_list|)
block|{
if|if
condition|(
name|QUERY_LIMIT
operator|.
name|equalsIgnoreCase
argument_list|(
name|varName
argument_list|)
condition|)
block|{
return|return
operator|new
name|PermissionRange
operator|.
name|WithDefaults
argument_list|(
name|varName
argument_list|,
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|0
argument_list|,
literal|500
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|GlobalCapability ()
specifier|private
name|GlobalCapability
parameter_list|()
block|{
comment|// Utility class, do not create instances.
block|}
block|}
end_class

end_unit

