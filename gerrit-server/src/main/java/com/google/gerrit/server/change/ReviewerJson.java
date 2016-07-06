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
name|LabelValue
operator|.
name|formatValue
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
name|ImmutableList
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
name|Lists
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
name|LabelType
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
name|LabelTypes
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
name|Permission
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
name|common
operator|.
name|data
operator|.
name|SubmitRecord
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
name|ReviewerInfo
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
name|PatchSet
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
name|PatchSetApproval
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
name|ApprovalsUtil
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
name|AccountLoader
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
name|project
operator|.
name|SubmitRuleEvaluator
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
name|TreeMap
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|ReviewerJson
specifier|public
class|class
name|ReviewerJson
block|{
DECL|field|db
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
decl_stmt|;
DECL|field|changeDataFactory
specifier|private
specifier|final
name|ChangeData
operator|.
name|Factory
name|changeDataFactory
decl_stmt|;
DECL|field|approvalsUtil
specifier|private
specifier|final
name|ApprovalsUtil
name|approvalsUtil
decl_stmt|;
DECL|field|accountLoaderFactory
specifier|private
specifier|final
name|AccountLoader
operator|.
name|Factory
name|accountLoaderFactory
decl_stmt|;
annotation|@
name|Inject
DECL|method|ReviewerJson (Provider<ReviewDb> db, ChangeData.Factory changeDataFactory, ApprovalsUtil approvalsUtil, AccountLoader.Factory accountLoaderFactory)
name|ReviewerJson
parameter_list|(
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|,
name|ChangeData
operator|.
name|Factory
name|changeDataFactory
parameter_list|,
name|ApprovalsUtil
name|approvalsUtil
parameter_list|,
name|AccountLoader
operator|.
name|Factory
name|accountLoaderFactory
parameter_list|)
block|{
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|changeDataFactory
operator|=
name|changeDataFactory
expr_stmt|;
name|this
operator|.
name|approvalsUtil
operator|=
name|approvalsUtil
expr_stmt|;
name|this
operator|.
name|accountLoaderFactory
operator|=
name|accountLoaderFactory
expr_stmt|;
block|}
DECL|method|format (Collection<ReviewerResource> rsrcs)
specifier|public
name|List
argument_list|<
name|ReviewerInfo
argument_list|>
name|format
parameter_list|(
name|Collection
argument_list|<
name|ReviewerResource
argument_list|>
name|rsrcs
parameter_list|)
throws|throws
name|OrmException
block|{
name|List
argument_list|<
name|ReviewerInfo
argument_list|>
name|infos
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|rsrcs
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|AccountLoader
name|loader
init|=
name|accountLoaderFactory
operator|.
name|create
argument_list|(
literal|true
argument_list|)
decl_stmt|;
for|for
control|(
name|ReviewerResource
name|rsrc
range|:
name|rsrcs
control|)
block|{
name|ReviewerInfo
name|info
init|=
name|format
argument_list|(
operator|new
name|ReviewerInfo
argument_list|(
name|rsrc
operator|.
name|getReviewerUser
argument_list|()
operator|.
name|getAccountId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
argument_list|,
name|rsrc
operator|.
name|getReviewerControl
argument_list|()
argument_list|)
decl_stmt|;
name|loader
operator|.
name|put
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|infos
operator|.
name|add
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
name|loader
operator|.
name|fill
argument_list|()
expr_stmt|;
return|return
name|infos
return|;
block|}
DECL|method|format (ReviewerResource rsrc)
specifier|public
name|List
argument_list|<
name|ReviewerInfo
argument_list|>
name|format
parameter_list|(
name|ReviewerResource
name|rsrc
parameter_list|)
throws|throws
name|OrmException
block|{
return|return
name|format
argument_list|(
name|ImmutableList
operator|.
expr|<
name|ReviewerResource
operator|>
name|of
argument_list|(
name|rsrc
argument_list|)
argument_list|)
return|;
block|}
DECL|method|format (ReviewerInfo out, ChangeControl ctl)
specifier|public
name|ReviewerInfo
name|format
parameter_list|(
name|ReviewerInfo
name|out
parameter_list|,
name|ChangeControl
name|ctl
parameter_list|)
throws|throws
name|OrmException
block|{
name|PatchSet
operator|.
name|Id
name|psId
init|=
name|ctl
operator|.
name|getChange
argument_list|()
operator|.
name|currentPatchSetId
argument_list|()
decl_stmt|;
return|return
name|format
argument_list|(
name|out
argument_list|,
name|ctl
argument_list|,
name|approvalsUtil
operator|.
name|byPatchSetUser
argument_list|(
name|db
operator|.
name|get
argument_list|()
argument_list|,
name|ctl
argument_list|,
name|psId
argument_list|,
operator|new
name|Account
operator|.
name|Id
argument_list|(
name|out
operator|.
name|_accountId
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
DECL|method|format (ReviewerInfo out, ChangeControl ctl, Iterable<PatchSetApproval> approvals)
specifier|public
name|ReviewerInfo
name|format
parameter_list|(
name|ReviewerInfo
name|out
parameter_list|,
name|ChangeControl
name|ctl
parameter_list|,
name|Iterable
argument_list|<
name|PatchSetApproval
argument_list|>
name|approvals
parameter_list|)
throws|throws
name|OrmException
block|{
name|LabelTypes
name|labelTypes
init|=
name|ctl
operator|.
name|getLabelTypes
argument_list|()
decl_stmt|;
comment|// Don't use Maps.newTreeMap(Comparator) due to OpenJDK bug 100167.
name|out
operator|.
name|approvals
operator|=
operator|new
name|TreeMap
argument_list|<>
argument_list|(
name|labelTypes
operator|.
name|nameComparator
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|PatchSetApproval
name|ca
range|:
name|approvals
control|)
block|{
for|for
control|(
name|PermissionRange
name|pr
range|:
name|ctl
operator|.
name|getLabelRanges
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|pr
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LabelType
name|at
init|=
name|labelTypes
operator|.
name|byLabel
argument_list|(
name|ca
operator|.
name|getLabelId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|at
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|approvals
operator|.
name|put
argument_list|(
name|at
operator|.
name|getName
argument_list|()
argument_list|,
name|formatValue
argument_list|(
name|ca
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// Add dummy approvals for all permitted labels for the user even if they
comment|// do not exist in the DB.
name|ChangeData
name|cd
init|=
name|changeDataFactory
operator|.
name|create
argument_list|(
name|db
operator|.
name|get
argument_list|()
argument_list|,
name|ctl
argument_list|)
decl_stmt|;
name|PatchSet
name|ps
init|=
name|cd
operator|.
name|currentPatchSet
argument_list|()
decl_stmt|;
if|if
condition|(
name|ps
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|SubmitRecord
name|rec
range|:
operator|new
name|SubmitRuleEvaluator
argument_list|(
name|cd
argument_list|)
operator|.
name|setFastEvalLabels
argument_list|(
literal|true
argument_list|)
operator|.
name|setAllowDraft
argument_list|(
literal|true
argument_list|)
operator|.
name|evaluate
argument_list|()
control|)
block|{
if|if
condition|(
name|rec
operator|.
name|labels
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
for|for
control|(
name|SubmitRecord
operator|.
name|Label
name|label
range|:
name|rec
operator|.
name|labels
control|)
block|{
name|String
name|name
init|=
name|label
operator|.
name|label
decl_stmt|;
if|if
condition|(
operator|!
name|out
operator|.
name|approvals
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
operator|&&
operator|!
name|ctl
operator|.
name|getRange
argument_list|(
name|Permission
operator|.
name|forLabel
argument_list|(
name|name
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|out
operator|.
name|approvals
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|formatValue
argument_list|(
operator|(
name|short
operator|)
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|out
operator|.
name|approvals
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|out
operator|.
name|approvals
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|out
return|;
block|}
block|}
end_class

end_unit

