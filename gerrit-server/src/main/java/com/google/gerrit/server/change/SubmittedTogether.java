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
name|gerrit
operator|.
name|extensions
operator|.
name|client
operator|.
name|ChangeStatus
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
name|common
operator|.
name|ChangeInfo
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
name|git
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
name|git
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

begin_class
annotation|@
name|Singleton
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
name|SubmittedTogether
operator|.
name|class
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
DECL|field|dbProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|dbProvider
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
name|MergeSuperSet
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
name|Inject
DECL|method|SubmittedTogether (ChangeJson.Factory json, Provider<ReviewDb> dbProvider, Provider<InternalChangeQuery> queryProvider, MergeSuperSet mergeSuperSet, Provider<WalkSorter> sorter)
name|SubmittedTogether
parameter_list|(
name|ChangeJson
operator|.
name|Factory
name|json
parameter_list|,
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|dbProvider
parameter_list|,
name|Provider
argument_list|<
name|InternalChangeQuery
argument_list|>
name|queryProvider
parameter_list|,
name|MergeSuperSet
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
name|dbProvider
operator|=
name|dbProvider
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
annotation|@
name|Override
DECL|method|apply (ChangeResource resource)
specifier|public
name|List
argument_list|<
name|ChangeInfo
argument_list|>
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
name|Exception
block|{
try|try
block|{
name|Change
name|c
init|=
name|resource
operator|.
name|getChange
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ChangeData
argument_list|>
name|cds
decl_stmt|;
if|if
condition|(
name|c
operator|.
name|getStatus
argument_list|()
operator|.
name|isOpen
argument_list|()
condition|)
block|{
name|cds
operator|=
name|getForOpenChange
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|.
name|getStatus
argument_list|()
operator|.
name|asChangeStatus
argument_list|()
operator|==
name|ChangeStatus
operator|.
name|MERGED
condition|)
block|{
name|cds
operator|=
name|getForMergedChange
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|cds
operator|=
name|getForAbandonedChange
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|cds
operator|.
name|size
argument_list|()
operator|<=
literal|1
condition|)
block|{
name|cds
operator|=
name|Collections
operator|.
name|emptyList
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// Skip sorting for singleton lists, to avoid WalkSorter opening the
comment|// repo just to fill out the commit field in PatchSetData.
name|cds
operator|=
name|sort
argument_list|(
name|cds
argument_list|)
expr_stmt|;
block|}
return|return
name|json
operator|.
name|create
argument_list|(
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
name|CURRENT_COMMIT
argument_list|)
argument_list|)
operator|.
name|formatChangeDatas
argument_list|(
name|cds
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|OrmException
decl||
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error on getting a ChangeSet"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
DECL|method|getForOpenChange (Change c)
specifier|private
name|List
argument_list|<
name|ChangeData
argument_list|>
name|getForOpenChange
parameter_list|(
name|Change
name|c
parameter_list|)
throws|throws
name|OrmException
throws|,
name|IOException
block|{
name|ChangeSet
name|cs
init|=
name|mergeSuperSet
operator|.
name|completeChangeSet
argument_list|(
name|dbProvider
operator|.
name|get
argument_list|()
argument_list|,
name|c
argument_list|)
decl_stmt|;
return|return
name|cs
operator|.
name|changes
argument_list|()
operator|.
name|asList
argument_list|()
return|;
block|}
DECL|method|getForMergedChange (Change c)
specifier|private
name|List
argument_list|<
name|ChangeData
argument_list|>
name|getForMergedChange
parameter_list|(
name|Change
name|c
parameter_list|)
throws|throws
name|OrmException
block|{
return|return
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
return|;
block|}
DECL|method|getForAbandonedChange ()
specifier|private
name|List
argument_list|<
name|ChangeData
argument_list|>
name|getForAbandonedChange
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
DECL|method|sort (List<ChangeData> cds)
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
parameter_list|)
throws|throws
name|OrmException
throws|,
name|IOException
block|{
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
block|}
end_class

end_unit

