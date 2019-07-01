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
name|common
operator|.
name|base
operator|.
name|MoreObjects
operator|.
name|firstNonNull
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|auto
operator|.
name|value
operator|.
name|AutoValue
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
name|Enums
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
name|Splitter
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
name|common
operator|.
name|collect
operator|.
name|ListMultimap
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
name|common
operator|.
name|collect
operator|.
name|MultimapBuilder
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
name|Sets
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
name|git
operator|.
name|ValidationError
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
name|Set
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

begin_comment
comment|/**  * Parses/writes project watches from/to a {@link Config} file.  *  *<p>This is a low-level API. Read/write of project watches in a user branch should be done through  * {@link AccountsUpdate} or {@link AccountConfig}.  *  *<p>The config file has one 'project' section for all project watches of a project.  *  *<p>The project name is used as subsection name and the filters with the notify types that decide  * for which events email notifications should be sent are represented as 'notify' values in the  * subsection. A 'notify' value is formatted as {@code<filter>  * [<comma-separated-list-of-notify-types>]}:  *  *<pre>  *   [project "foo"]  *     notify = * [ALL_COMMENTS]  *     notify = branch:master [ALL_COMMENTS, NEW_PATCHSETS]  *     notify = branch:master owner:self [SUBMITTED_CHANGES]  *</pre>  *  *<p>If two notify values in the same subsection have the same filter they are merged on the next  * save, taking the union of the notify types.  *  *<p>For watch configurations that notify on no event the list of notify types is empty:  *  *<pre>  *   [project "foo"]  *     notify = branch:master []  *</pre>  *  *<p>Unknown notify types are ignored and removed on save.  *  *<p>The project watches are lazily parsed.  */
end_comment

begin_class
DECL|class|ProjectWatches
specifier|public
class|class
name|ProjectWatches
block|{
annotation|@
name|AutoValue
DECL|class|ProjectWatchKey
specifier|public
specifier|abstract
specifier|static
class|class
name|ProjectWatchKey
block|{
DECL|method|create (Project.NameKey project, @Nullable String filter)
specifier|public
specifier|static
name|ProjectWatchKey
name|create
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
annotation|@
name|Nullable
name|String
name|filter
parameter_list|)
block|{
return|return
operator|new
name|AutoValue_ProjectWatches_ProjectWatchKey
argument_list|(
name|project
argument_list|,
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|filter
argument_list|)
argument_list|)
return|;
block|}
DECL|method|project ()
specifier|public
specifier|abstract
name|Project
operator|.
name|NameKey
name|project
parameter_list|()
function_decl|;
DECL|method|filter ()
specifier|public
specifier|abstract
annotation|@
name|Nullable
name|String
name|filter
parameter_list|()
function_decl|;
block|}
DECL|enum|NotifyType
specifier|public
enum|enum
name|NotifyType
block|{
comment|// sort by name, except 'ALL' which should stay last
DECL|enumConstant|ABANDONED_CHANGES
name|ABANDONED_CHANGES
block|,
DECL|enumConstant|ALL_COMMENTS
name|ALL_COMMENTS
block|,
DECL|enumConstant|NEW_CHANGES
name|NEW_CHANGES
block|,
DECL|enumConstant|NEW_PATCHSETS
name|NEW_PATCHSETS
block|,
DECL|enumConstant|SUBMITTED_CHANGES
name|SUBMITTED_CHANGES
block|,
DECL|enumConstant|ALL
name|ALL
block|}
DECL|field|FILTER_ALL
specifier|public
specifier|static
specifier|final
name|String
name|FILTER_ALL
init|=
literal|"*"
decl_stmt|;
DECL|field|WATCH_CONFIG
specifier|public
specifier|static
specifier|final
name|String
name|WATCH_CONFIG
init|=
literal|"watch.config"
decl_stmt|;
DECL|field|PROJECT
specifier|public
specifier|static
specifier|final
name|String
name|PROJECT
init|=
literal|"project"
decl_stmt|;
DECL|field|KEY_NOTIFY
specifier|public
specifier|static
specifier|final
name|String
name|KEY_NOTIFY
init|=
literal|"notify"
decl_stmt|;
DECL|field|accountId
specifier|private
specifier|final
name|Account
operator|.
name|Id
name|accountId
decl_stmt|;
DECL|field|cfg
specifier|private
specifier|final
name|Config
name|cfg
decl_stmt|;
DECL|field|validationErrorSink
specifier|private
specifier|final
name|ValidationError
operator|.
name|Sink
name|validationErrorSink
decl_stmt|;
DECL|field|projectWatches
specifier|private
name|ImmutableMap
argument_list|<
name|ProjectWatchKey
argument_list|,
name|ImmutableSet
argument_list|<
name|NotifyType
argument_list|>
argument_list|>
name|projectWatches
decl_stmt|;
DECL|method|ProjectWatches (Account.Id accountId, Config cfg, ValidationError.Sink validationErrorSink)
name|ProjectWatches
parameter_list|(
name|Account
operator|.
name|Id
name|accountId
parameter_list|,
name|Config
name|cfg
parameter_list|,
name|ValidationError
operator|.
name|Sink
name|validationErrorSink
parameter_list|)
block|{
name|this
operator|.
name|accountId
operator|=
name|requireNonNull
argument_list|(
name|accountId
argument_list|,
literal|"accountId"
argument_list|)
expr_stmt|;
name|this
operator|.
name|cfg
operator|=
name|requireNonNull
argument_list|(
name|cfg
argument_list|,
literal|"cfg"
argument_list|)
expr_stmt|;
name|this
operator|.
name|validationErrorSink
operator|=
name|requireNonNull
argument_list|(
name|validationErrorSink
argument_list|,
literal|"validationErrorSink"
argument_list|)
expr_stmt|;
block|}
DECL|method|getProjectWatches ()
specifier|public
name|ImmutableMap
argument_list|<
name|ProjectWatchKey
argument_list|,
name|ImmutableSet
argument_list|<
name|NotifyType
argument_list|>
argument_list|>
name|getProjectWatches
parameter_list|()
block|{
if|if
condition|(
name|projectWatches
operator|==
literal|null
condition|)
block|{
name|parse
argument_list|()
expr_stmt|;
block|}
return|return
name|projectWatches
return|;
block|}
DECL|method|parse ()
specifier|public
name|void
name|parse
parameter_list|()
block|{
name|projectWatches
operator|=
name|parse
argument_list|(
name|accountId
argument_list|,
name|cfg
argument_list|,
name|validationErrorSink
argument_list|)
expr_stmt|;
block|}
comment|/**    * Parses project watches from the given config file and returns them as a map.    *    *<p>A project watch is defined on a project and has a filter to match changes for which the    * project watch should be applied. The project and the filter form the map key. The map value is    * a set of notify types that decide for which events email notifications should be sent.    *    *<p>A project watch on the {@code All-Projects} project applies for all projects unless the    * project has a matching project watch.    *    *<p>A project watch can have an empty set of notify types. An empty set of notify types means    * that no notification for matching changes should be set. This is different from no project    * watch as it overwrites matching project watches from the {@code All-Projects} project.    *    *<p>Since we must be able to differentiate a project watch with an empty set of notify types    * from no project watch we can't use a {@link Multimap} as return type.    *    * @param accountId the ID of the account for which the project watches should be parsed    * @param cfg the config file from which the project watches should be parsed    * @param validationErrorSink validation error sink    * @return the parsed project watches    */
annotation|@
name|VisibleForTesting
DECL|method|parse ( Account.Id accountId, Config cfg, ValidationError.Sink validationErrorSink)
specifier|public
specifier|static
name|ImmutableMap
argument_list|<
name|ProjectWatchKey
argument_list|,
name|ImmutableSet
argument_list|<
name|NotifyType
argument_list|>
argument_list|>
name|parse
parameter_list|(
name|Account
operator|.
name|Id
name|accountId
parameter_list|,
name|Config
name|cfg
parameter_list|,
name|ValidationError
operator|.
name|Sink
name|validationErrorSink
parameter_list|)
block|{
name|Map
argument_list|<
name|ProjectWatchKey
argument_list|,
name|Set
argument_list|<
name|NotifyType
argument_list|>
argument_list|>
name|projectWatches
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|projectName
range|:
name|cfg
operator|.
name|getSubsections
argument_list|(
name|PROJECT
argument_list|)
control|)
block|{
name|String
index|[]
name|notifyValues
init|=
name|cfg
operator|.
name|getStringList
argument_list|(
name|PROJECT
argument_list|,
name|projectName
argument_list|,
name|KEY_NOTIFY
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|nv
range|:
name|notifyValues
control|)
block|{
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|nv
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|NotifyValue
name|notifyValue
init|=
name|NotifyValue
operator|.
name|parse
argument_list|(
name|accountId
argument_list|,
name|projectName
argument_list|,
name|nv
argument_list|,
name|validationErrorSink
argument_list|)
decl_stmt|;
if|if
condition|(
name|notifyValue
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|ProjectWatchKey
name|key
init|=
name|ProjectWatchKey
operator|.
name|create
argument_list|(
name|Project
operator|.
name|nameKey
argument_list|(
name|projectName
argument_list|)
argument_list|,
name|notifyValue
operator|.
name|filter
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|projectWatches
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|projectWatches
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|NotifyType
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|projectWatches
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|.
name|addAll
argument_list|(
name|notifyValue
operator|.
name|notifyTypes
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|immutableCopyOf
argument_list|(
name|projectWatches
argument_list|)
return|;
block|}
DECL|method|save (Map<ProjectWatchKey, Set<NotifyType>> projectWatches)
specifier|public
name|Config
name|save
parameter_list|(
name|Map
argument_list|<
name|ProjectWatchKey
argument_list|,
name|Set
argument_list|<
name|NotifyType
argument_list|>
argument_list|>
name|projectWatches
parameter_list|)
block|{
name|this
operator|.
name|projectWatches
operator|=
name|immutableCopyOf
argument_list|(
name|projectWatches
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|projectName
range|:
name|cfg
operator|.
name|getSubsections
argument_list|(
name|PROJECT
argument_list|)
control|)
block|{
name|cfg
operator|.
name|unsetSection
argument_list|(
name|PROJECT
argument_list|,
name|projectName
argument_list|)
expr_stmt|;
block|}
name|ListMultimap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|notifyValuesByProject
init|=
name|MultimapBuilder
operator|.
name|hashKeys
argument_list|()
operator|.
name|arrayListValues
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|ProjectWatchKey
argument_list|,
name|Set
argument_list|<
name|NotifyType
argument_list|>
argument_list|>
name|e
range|:
name|projectWatches
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|NotifyValue
name|notifyValue
init|=
name|NotifyValue
operator|.
name|create
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
operator|.
name|filter
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|notifyValuesByProject
operator|.
name|put
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
operator|.
name|project
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|notifyValue
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|String
argument_list|>
argument_list|>
name|e
range|:
name|notifyValuesByProject
operator|.
name|asMap
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|cfg
operator|.
name|setStringList
argument_list|(
name|PROJECT
argument_list|,
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|KEY_NOTIFY
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|cfg
return|;
block|}
DECL|method|immutableCopyOf ( Map<ProjectWatchKey, Set<NotifyType>> projectWatches)
specifier|private
specifier|static
name|ImmutableMap
argument_list|<
name|ProjectWatchKey
argument_list|,
name|ImmutableSet
argument_list|<
name|NotifyType
argument_list|>
argument_list|>
name|immutableCopyOf
parameter_list|(
name|Map
argument_list|<
name|ProjectWatchKey
argument_list|,
name|Set
argument_list|<
name|NotifyType
argument_list|>
argument_list|>
name|projectWatches
parameter_list|)
block|{
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|ProjectWatchKey
argument_list|,
name|ImmutableSet
argument_list|<
name|NotifyType
argument_list|>
argument_list|>
name|b
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
name|projectWatches
operator|.
name|entrySet
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|forEach
argument_list|(
name|e
lambda|->
name|b
operator|.
name|put
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|AutoValue
DECL|class|NotifyValue
specifier|public
specifier|abstract
specifier|static
class|class
name|NotifyValue
block|{
DECL|method|parse ( Account.Id accountId, String project, String notifyValue, ValidationError.Sink validationErrorSink)
specifier|public
specifier|static
name|NotifyValue
name|parse
parameter_list|(
name|Account
operator|.
name|Id
name|accountId
parameter_list|,
name|String
name|project
parameter_list|,
name|String
name|notifyValue
parameter_list|,
name|ValidationError
operator|.
name|Sink
name|validationErrorSink
parameter_list|)
block|{
name|notifyValue
operator|=
name|notifyValue
operator|.
name|trim
argument_list|()
expr_stmt|;
name|int
name|i
init|=
name|notifyValue
operator|.
name|lastIndexOf
argument_list|(
literal|'['
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|<
literal|0
operator|||
name|notifyValue
operator|.
name|charAt
argument_list|(
name|notifyValue
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|!=
literal|']'
condition|)
block|{
name|validationErrorSink
operator|.
name|error
argument_list|(
operator|new
name|ValidationError
argument_list|(
name|WATCH_CONFIG
argument_list|,
name|String
operator|.
name|format
argument_list|(
literal|"Invalid project watch of account %d for project %s: %s"
argument_list|,
name|accountId
operator|.
name|get
argument_list|()
argument_list|,
name|project
argument_list|,
name|notifyValue
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|String
name|filter
init|=
name|notifyValue
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|filter
operator|.
name|isEmpty
argument_list|()
operator|||
name|FILTER_ALL
operator|.
name|equals
argument_list|(
name|filter
argument_list|)
condition|)
block|{
name|filter
operator|=
literal|null
expr_stmt|;
block|}
name|Set
argument_list|<
name|NotifyType
argument_list|>
name|notifyTypes
init|=
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|NotifyType
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|+
literal|1
operator|<
name|notifyValue
operator|.
name|length
argument_list|()
operator|-
literal|2
condition|)
block|{
for|for
control|(
name|String
name|nt
range|:
name|Splitter
operator|.
name|on
argument_list|(
literal|','
argument_list|)
operator|.
name|trimResults
argument_list|()
operator|.
name|splitToList
argument_list|(
name|notifyValue
operator|.
name|substring
argument_list|(
name|i
operator|+
literal|1
argument_list|,
name|notifyValue
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
control|)
block|{
name|NotifyType
name|notifyType
init|=
name|Enums
operator|.
name|getIfPresent
argument_list|(
name|NotifyType
operator|.
name|class
argument_list|,
name|nt
argument_list|)
operator|.
name|orNull
argument_list|()
decl_stmt|;
if|if
condition|(
name|notifyType
operator|==
literal|null
condition|)
block|{
name|validationErrorSink
operator|.
name|error
argument_list|(
operator|new
name|ValidationError
argument_list|(
name|WATCH_CONFIG
argument_list|,
name|String
operator|.
name|format
argument_list|(
literal|"Invalid notify type %s in project watch "
operator|+
literal|"of account %d for project %s: %s"
argument_list|,
name|nt
argument_list|,
name|accountId
operator|.
name|get
argument_list|()
argument_list|,
name|project
argument_list|,
name|notifyValue
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|notifyTypes
operator|.
name|add
argument_list|(
name|notifyType
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|create
argument_list|(
name|filter
argument_list|,
name|notifyTypes
argument_list|)
return|;
block|}
DECL|method|create (@ullable String filter, Collection<NotifyType> notifyTypes)
specifier|public
specifier|static
name|NotifyValue
name|create
parameter_list|(
annotation|@
name|Nullable
name|String
name|filter
parameter_list|,
name|Collection
argument_list|<
name|NotifyType
argument_list|>
name|notifyTypes
parameter_list|)
block|{
return|return
operator|new
name|AutoValue_ProjectWatches_NotifyValue
argument_list|(
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|filter
argument_list|)
argument_list|,
name|Sets
operator|.
name|immutableEnumSet
argument_list|(
name|notifyTypes
argument_list|)
argument_list|)
return|;
block|}
DECL|method|filter ()
specifier|public
specifier|abstract
annotation|@
name|Nullable
name|String
name|filter
parameter_list|()
function_decl|;
DECL|method|notifyTypes ()
specifier|public
specifier|abstract
name|ImmutableSet
argument_list|<
name|NotifyType
argument_list|>
name|notifyTypes
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|toString ()
specifier|public
specifier|final
name|String
name|toString
parameter_list|()
block|{
name|List
argument_list|<
name|NotifyType
argument_list|>
name|notifyTypes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|notifyTypes
argument_list|()
argument_list|)
decl_stmt|;
name|StringBuilder
name|notifyValue
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|notifyValue
operator|.
name|append
argument_list|(
name|firstNonNull
argument_list|(
name|filter
argument_list|()
argument_list|,
name|FILTER_ALL
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|" ["
argument_list|)
expr_stmt|;
name|Joiner
operator|.
name|on
argument_list|(
literal|", "
argument_list|)
operator|.
name|appendTo
argument_list|(
name|notifyValue
argument_list|,
name|notifyTypes
argument_list|)
expr_stmt|;
name|notifyValue
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
return|return
name|notifyValue
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

