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
DECL|package|com.google.gerrit.server.restapi.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|restapi
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
name|gerrit
operator|.
name|extensions
operator|.
name|api
operator|.
name|changes
operator|.
name|SubmittedTogetherOption
operator|.
name|NON_VISIBLE_CHANGES
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|reverseOrder
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
name|exceptions
operator|.
name|StorageException
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
name|changes
operator|.
name|SubmittedTogetherInfo
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
name|changes
operator|.
name|SubmittedTogetherOption
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
name|ListChangesOption
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
name|ResourceConflictException
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
name|reviewdb
operator|.
name|client
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
name|server
operator|.
name|change
operator|.
name|ChangeJson
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
name|ChangeResource
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
name|WalkSorter
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
name|WalkSorter
operator|.
name|PatchSetData
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
name|InternalChangeQuery
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
name|submit
operator|.
name|ChangeSet
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
name|submit
operator|.
name|MergeSuperSet
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
name|io
operator|.
name|IOException
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|List
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
DECL|class|SubmittedTogether
specifier|public
class|class
name|SubmittedTogether
implements|implements
name|RestReadView
argument_list|<
name|ChangeResource
argument_list|>
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
DECL|field|options
specifier|private
specifier|final
name|EnumSet
argument_list|<
name|SubmittedTogetherOption
argument_list|>
name|options
init|=
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|SubmittedTogetherOption
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|jsonOpt
specifier|private
specifier|final
name|EnumSet
argument_list|<
name|ListChangesOption
argument_list|>
name|jsonOpt
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|ListChangesOption
operator|.
name|CURRENT_REVISION
argument_list|,
name|ListChangesOption
operator|.
name|SUBMITTABLE
argument_list|)
decl_stmt|;
DECL|field|COMPARATOR
specifier|private
specifier|static
specifier|final
name|Comparator
argument_list|<
name|ChangeData
argument_list|>
name|COMPARATOR
init|=
name|Comparator
operator|.
name|comparing
argument_list|(
name|ChangeData
operator|::
name|project
argument_list|)
operator|.
name|thenComparing
argument_list|(
name|cd
lambda|->
name|cd
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|reverseOrder
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|json
specifier|private
specifier|final
name|ChangeJson
operator|.
name|Factory
name|json
decl_stmt|;
DECL|field|queryProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|InternalChangeQuery
argument_list|>
name|queryProvider
decl_stmt|;
DECL|field|mergeSuperSet
specifier|private
specifier|final
name|Provider
argument_list|<
name|MergeSuperSet
argument_list|>
name|mergeSuperSet
decl_stmt|;
DECL|field|sorter
specifier|private
specifier|final
name|Provider
argument_list|<
name|WalkSorter
argument_list|>
name|sorter
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"-o"
argument_list|,
name|usage
operator|=
literal|"Output options"
argument_list|)
DECL|method|addOption (String option)
name|void
name|addOption
parameter_list|(
name|String
name|option
parameter_list|)
block|{
for|for
control|(
name|ListChangesOption
name|o
range|:
name|ListChangesOption
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|o
operator|.
name|name
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|option
argument_list|)
condition|)
block|{
name|jsonOpt
operator|.
name|add
argument_list|(
name|o
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
for|for
control|(
name|SubmittedTogetherOption
name|o
range|:
name|SubmittedTogetherOption
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|o
operator|.
name|name
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|option
argument_list|)
condition|)
block|{
name|options
operator|.
name|add
argument_list|(
name|o
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"option not recognized: "
operator|+
name|option
argument_list|)
throw|;
block|}
annotation|@
name|Inject
DECL|method|SubmittedTogether ( ChangeJson.Factory json, Provider<InternalChangeQuery> queryProvider, Provider<MergeSuperSet> mergeSuperSet, Provider<WalkSorter> sorter)
name|SubmittedTogether
parameter_list|(
name|ChangeJson
operator|.
name|Factory
name|json
parameter_list|,
name|Provider
argument_list|<
name|InternalChangeQuery
argument_list|>
name|queryProvider
parameter_list|,
name|Provider
argument_list|<
name|MergeSuperSet
argument_list|>
name|mergeSuperSet
parameter_list|,
name|Provider
argument_list|<
name|WalkSorter
argument_list|>
name|sorter
parameter_list|)
block|{
name|this
operator|.
name|json
operator|=
name|json
expr_stmt|;
name|this
operator|.
name|queryProvider
operator|=
name|queryProvider
expr_stmt|;
name|this
operator|.
name|mergeSuperSet
operator|=
name|mergeSuperSet
expr_stmt|;
name|this
operator|.
name|sorter
operator|=
name|sorter
expr_stmt|;
block|}
DECL|method|addListChangesOption (EnumSet<ListChangesOption> o)
specifier|public
name|SubmittedTogether
name|addListChangesOption
parameter_list|(
name|EnumSet
argument_list|<
name|ListChangesOption
argument_list|>
name|o
parameter_list|)
block|{
name|jsonOpt
operator|.
name|addAll
argument_list|(
name|o
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|addSubmittedTogetherOption (EnumSet<SubmittedTogetherOption> o)
specifier|public
name|SubmittedTogether
name|addSubmittedTogetherOption
parameter_list|(
name|EnumSet
argument_list|<
name|SubmittedTogetherOption
argument_list|>
name|o
parameter_list|)
block|{
name|options
operator|.
name|addAll
argument_list|(
name|o
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|apply (ChangeResource resource)
specifier|public
name|Object
name|apply
parameter_list|(
name|ChangeResource
name|resource
parameter_list|)
throws|throws
name|AuthException
throws|,
name|BadRequestException
throws|,
name|ResourceConflictException
throws|,
name|IOException
throws|,
name|PermissionBackendException
block|{
name|SubmittedTogetherInfo
name|info
init|=
name|applyInfo
argument_list|(
name|resource
argument_list|)
decl_stmt|;
if|if
condition|(
name|options
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|info
operator|.
name|changes
return|;
block|}
return|return
name|info
return|;
block|}
DECL|method|applyInfo (ChangeResource resource)
specifier|public
name|SubmittedTogetherInfo
name|applyInfo
parameter_list|(
name|ChangeResource
name|resource
parameter_list|)
throws|throws
name|AuthException
throws|,
name|IOException
throws|,
name|PermissionBackendException
block|{
name|Change
name|c
init|=
name|resource
operator|.
name|getChange
argument_list|()
decl_stmt|;
try|try
block|{
name|List
argument_list|<
name|ChangeData
argument_list|>
name|cds
decl_stmt|;
name|int
name|hidden
decl_stmt|;
if|if
condition|(
name|c
operator|.
name|isNew
argument_list|()
condition|)
block|{
name|ChangeSet
name|cs
init|=
name|mergeSuperSet
operator|.
name|get
argument_list|()
operator|.
name|completeChangeSet
argument_list|(
name|c
argument_list|,
name|resource
operator|.
name|getUser
argument_list|()
argument_list|)
decl_stmt|;
name|cds
operator|=
name|ensureRequiredDataIsLoaded
argument_list|(
name|cs
operator|.
name|changes
argument_list|()
operator|.
name|asList
argument_list|()
argument_list|)
expr_stmt|;
name|hidden
operator|=
name|cs
operator|.
name|nonVisibleChanges
argument_list|()
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|.
name|isMerged
argument_list|()
condition|)
block|{
name|cds
operator|=
name|queryProvider
operator|.
name|get
argument_list|()
operator|.
name|bySubmissionId
argument_list|(
name|c
operator|.
name|getSubmissionId
argument_list|()
argument_list|)
expr_stmt|;
name|hidden
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|cds
operator|=
name|Collections
operator|.
name|emptyList
argument_list|()
expr_stmt|;
name|hidden
operator|=
literal|0
expr_stmt|;
block|}
if|if
condition|(
name|hidden
operator|!=
literal|0
operator|&&
operator|!
name|options
operator|.
name|contains
argument_list|(
name|NON_VISIBLE_CHANGES
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AuthException
argument_list|(
literal|"change would be submitted with a change that you cannot see"
argument_list|)
throw|;
block|}
name|cds
operator|=
name|sort
argument_list|(
name|cds
argument_list|,
name|hidden
argument_list|)
expr_stmt|;
name|SubmittedTogetherInfo
name|info
init|=
operator|new
name|SubmittedTogetherInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|changes
operator|=
name|json
operator|.
name|create
argument_list|(
name|jsonOpt
argument_list|)
operator|.
name|format
argument_list|(
name|cds
argument_list|)
expr_stmt|;
name|info
operator|.
name|nonVisibleChanges
operator|=
name|hidden
expr_stmt|;
return|return
name|info
return|;
block|}
catch|catch
parameter_list|(
name|StorageException
decl||
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|atSevere
argument_list|()
operator|.
name|withCause
argument_list|(
name|e
argument_list|)
operator|.
name|log
argument_list|(
literal|"Error on getting a ChangeSet"
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
DECL|method|sort (List<ChangeData> cds, int hidden)
specifier|private
name|List
argument_list|<
name|ChangeData
argument_list|>
name|sort
parameter_list|(
name|List
argument_list|<
name|ChangeData
argument_list|>
name|cds
parameter_list|,
name|int
name|hidden
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|cds
operator|.
name|size
argument_list|()
operator|<=
literal|1
operator|&&
name|hidden
operator|==
literal|0
condition|)
block|{
comment|// Skip sorting for singleton lists, to avoid WalkSorter opening the
comment|// repo just to fill out the commit field in PatchSetData.
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
name|long
name|numProjectsDistinct
init|=
name|cds
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|ChangeData
operator|::
name|project
argument_list|)
operator|.
name|distinct
argument_list|()
operator|.
name|count
argument_list|()
decl_stmt|;
name|long
name|numProjects
init|=
name|cds
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|ChangeData
operator|::
name|project
argument_list|)
operator|.
name|count
argument_list|()
decl_stmt|;
if|if
condition|(
name|numProjects
operator|==
name|numProjectsDistinct
operator|||
name|numProjectsDistinct
operator|>
literal|5
condition|)
block|{
comment|// We either have only a single change per project which means that WalkSorter won't make a
comment|// difference compared to our index-backed sort, or we are looking at more than 5 projects
comment|// which would make WalkSorter too expensive for this call.
return|return
name|cds
operator|.
name|stream
argument_list|()
operator|.
name|sorted
argument_list|(
name|COMPARATOR
argument_list|)
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
return|;
block|}
comment|// Perform more expensive walk-sort.
name|List
argument_list|<
name|ChangeData
argument_list|>
name|sorted
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|cds
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|PatchSetData
name|psd
range|:
name|sorter
operator|.
name|get
argument_list|()
operator|.
name|sort
argument_list|(
name|cds
argument_list|)
control|)
block|{
name|sorted
operator|.
name|add
argument_list|(
name|psd
operator|.
name|data
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|sorted
return|;
block|}
DECL|method|ensureRequiredDataIsLoaded (List<ChangeData> cds)
specifier|private
specifier|static
name|List
argument_list|<
name|ChangeData
argument_list|>
name|ensureRequiredDataIsLoaded
parameter_list|(
name|List
argument_list|<
name|ChangeData
argument_list|>
name|cds
parameter_list|)
block|{
comment|// TODO(hiesel): Instead of calling these manually, either implement a helper that brings a
comment|// database-backed change on-par with an index-backed change in terms of the populated fields in
comment|// ChangeData or check if any of the ChangeDatas was loaded from the database and allow
comment|// lazyloading if so.
for|for
control|(
name|ChangeData
name|cd
range|:
name|cds
control|)
block|{
name|cd
operator|.
name|submitRecords
argument_list|(
name|ChangeJson
operator|.
name|SUBMIT_RULE_OPTIONS_LENIENT
argument_list|)
expr_stmt|;
name|cd
operator|.
name|submitRecords
argument_list|(
name|ChangeJson
operator|.
name|SUBMIT_RULE_OPTIONS_STRICT
argument_list|)
expr_stmt|;
name|cd
operator|.
name|currentPatchSet
argument_list|()
expr_stmt|;
block|}
return|return
name|cds
return|;
block|}
block|}
end_class

end_unit

