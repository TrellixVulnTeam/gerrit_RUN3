begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2014 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.api.changes
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|api
operator|.
name|changes
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
name|common
operator|.
name|ListChangesOption
operator|.
name|ALL_REVISIONS
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
name|common
operator|.
name|ListChangesOption
operator|.
name|CURRENT_ACTIONS
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
name|common
operator|.
name|ListChangesOption
operator|.
name|CURRENT_REVISION
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
name|common
operator|.
name|ListChangesOption
operator|.
name|DETAILED_LABELS
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
name|common
operator|.
name|ListChangesOption
operator|.
name|LABELS
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
name|common
operator|.
name|ListChangesOption
operator|.
name|MESSAGES
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
name|Lists
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
name|Maps
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
name|AccountInfo
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
name|ApprovalInfo
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
name|common
operator|.
name|ChangeMessageInfo
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
name|common
operator|.
name|LabelInfo
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
name|client
operator|.
name|Change
operator|.
name|Status
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_class
DECL|class|ChangeInfoMapper
class|class
name|ChangeInfoMapper
block|{
DECL|field|MAP
specifier|private
specifier|final
specifier|static
name|ImmutableMap
argument_list|<
name|Change
operator|.
name|Status
argument_list|,
name|ChangeStatus
argument_list|>
name|MAP
init|=
name|Maps
operator|.
name|immutableEnumMap
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
name|Status
operator|.
name|DRAFT
argument_list|,
name|ChangeStatus
operator|.
name|DRAFT
argument_list|,
name|Status
operator|.
name|NEW
argument_list|,
name|ChangeStatus
operator|.
name|NEW
argument_list|,
name|Status
operator|.
name|SUBMITTED
argument_list|,
name|ChangeStatus
operator|.
name|SUBMITTED
argument_list|,
name|Status
operator|.
name|MERGED
argument_list|,
name|ChangeStatus
operator|.
name|MERGED
argument_list|,
name|Status
operator|.
name|ABANDONED
argument_list|,
name|ChangeStatus
operator|.
name|ABANDONED
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|s
specifier|private
specifier|final
name|EnumSet
argument_list|<
name|ListChangesOption
argument_list|>
name|s
decl_stmt|;
DECL|method|ChangeInfoMapper (EnumSet<ListChangesOption> s)
name|ChangeInfoMapper
parameter_list|(
name|EnumSet
argument_list|<
name|ListChangesOption
argument_list|>
name|s
parameter_list|)
block|{
name|this
operator|.
name|s
operator|=
name|s
expr_stmt|;
block|}
DECL|method|map (ChangeJson.ChangeInfo i)
name|ChangeInfo
name|map
parameter_list|(
name|ChangeJson
operator|.
name|ChangeInfo
name|i
parameter_list|)
block|{
name|ChangeInfo
name|o
init|=
operator|new
name|ChangeInfo
argument_list|()
decl_stmt|;
name|mapCommon
argument_list|(
name|i
argument_list|,
name|o
argument_list|)
expr_stmt|;
if|if
condition|(
name|has
argument_list|(
name|LABELS
argument_list|)
operator|||
name|has
argument_list|(
name|DETAILED_LABELS
argument_list|)
condition|)
block|{
name|mapLabels
argument_list|(
name|i
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|has
argument_list|(
name|MESSAGES
argument_list|)
condition|)
block|{
name|mapMessages
argument_list|(
name|i
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|has
argument_list|(
name|ALL_REVISIONS
argument_list|)
operator|||
name|has
argument_list|(
name|CURRENT_REVISION
argument_list|)
condition|)
block|{
name|o
operator|.
name|revisions
operator|=
name|i
operator|.
name|revisions
expr_stmt|;
block|}
if|if
condition|(
name|has
argument_list|(
name|CURRENT_ACTIONS
argument_list|)
condition|)
block|{
name|o
operator|.
name|actions
operator|=
name|i
operator|.
name|actions
expr_stmt|;
block|}
return|return
name|o
return|;
block|}
DECL|method|mapCommon (ChangeJson.ChangeInfo i, ChangeInfo o)
specifier|private
name|void
name|mapCommon
parameter_list|(
name|ChangeJson
operator|.
name|ChangeInfo
name|i
parameter_list|,
name|ChangeInfo
name|o
parameter_list|)
block|{
name|o
operator|.
name|id
operator|=
name|i
operator|.
name|id
expr_stmt|;
name|o
operator|.
name|project
operator|=
name|i
operator|.
name|project
expr_stmt|;
name|o
operator|.
name|branch
operator|=
name|i
operator|.
name|branch
expr_stmt|;
name|o
operator|.
name|topic
operator|=
name|i
operator|.
name|topic
expr_stmt|;
name|o
operator|.
name|changeId
operator|=
name|i
operator|.
name|changeId
expr_stmt|;
name|o
operator|.
name|subject
operator|=
name|i
operator|.
name|subject
expr_stmt|;
name|o
operator|.
name|status
operator|=
name|MAP
operator|.
name|get
argument_list|(
name|i
operator|.
name|status
argument_list|)
expr_stmt|;
name|o
operator|.
name|created
operator|=
name|i
operator|.
name|created
expr_stmt|;
name|o
operator|.
name|updated
operator|=
name|i
operator|.
name|updated
expr_stmt|;
name|o
operator|.
name|starred
operator|=
name|i
operator|.
name|starred
expr_stmt|;
name|o
operator|.
name|reviewed
operator|=
name|i
operator|.
name|reviewed
expr_stmt|;
name|o
operator|.
name|mergeable
operator|=
name|i
operator|.
name|mergeable
expr_stmt|;
name|o
operator|.
name|insertions
operator|=
name|i
operator|.
name|insertions
expr_stmt|;
name|o
operator|.
name|deletions
operator|=
name|i
operator|.
name|deletions
expr_stmt|;
name|o
operator|.
name|owner
operator|=
name|fromAcountInfo
argument_list|(
name|i
operator|.
name|owner
argument_list|)
expr_stmt|;
name|o
operator|.
name|currentRevision
operator|=
name|i
operator|.
name|currentRevision
expr_stmt|;
block|}
DECL|method|mapMessages (ChangeJson.ChangeInfo i, ChangeInfo o)
specifier|private
name|void
name|mapMessages
parameter_list|(
name|ChangeJson
operator|.
name|ChangeInfo
name|i
parameter_list|,
name|ChangeInfo
name|o
parameter_list|)
block|{
name|List
argument_list|<
name|ChangeMessageInfo
argument_list|>
name|r
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|i
operator|.
name|messages
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|ChangeJson
operator|.
name|ChangeMessageInfo
name|m
range|:
name|i
operator|.
name|messages
control|)
block|{
name|ChangeMessageInfo
name|cmi
init|=
operator|new
name|ChangeMessageInfo
argument_list|()
decl_stmt|;
name|cmi
operator|.
name|id
operator|=
name|m
operator|.
name|id
expr_stmt|;
name|cmi
operator|.
name|author
operator|=
name|fromAcountInfo
argument_list|(
name|m
operator|.
name|author
argument_list|)
expr_stmt|;
name|cmi
operator|.
name|date
operator|=
name|m
operator|.
name|date
expr_stmt|;
name|cmi
operator|.
name|message
operator|=
name|m
operator|.
name|message
expr_stmt|;
name|cmi
operator|.
name|_revisionNumber
operator|=
name|m
operator|.
name|_revisionNumber
expr_stmt|;
name|r
operator|.
name|add
argument_list|(
name|cmi
argument_list|)
expr_stmt|;
block|}
name|o
operator|.
name|messages
operator|=
name|r
expr_stmt|;
block|}
DECL|method|mapLabels (ChangeJson.ChangeInfo i, ChangeInfo o)
specifier|private
name|void
name|mapLabels
parameter_list|(
name|ChangeJson
operator|.
name|ChangeInfo
name|i
parameter_list|,
name|ChangeInfo
name|o
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|LabelInfo
argument_list|>
name|r
init|=
name|Maps
operator|.
name|newLinkedHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|ChangeJson
operator|.
name|LabelInfo
argument_list|>
name|e
range|:
name|i
operator|.
name|labels
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|ChangeJson
operator|.
name|LabelInfo
name|li
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|LabelInfo
name|lo
init|=
operator|new
name|LabelInfo
argument_list|()
decl_stmt|;
name|lo
operator|.
name|approved
operator|=
name|fromAcountInfo
argument_list|(
name|li
operator|.
name|approved
argument_list|)
expr_stmt|;
name|lo
operator|.
name|rejected
operator|=
name|fromAcountInfo
argument_list|(
name|li
operator|.
name|rejected
argument_list|)
expr_stmt|;
name|lo
operator|.
name|recommended
operator|=
name|fromAcountInfo
argument_list|(
name|li
operator|.
name|recommended
argument_list|)
expr_stmt|;
name|lo
operator|.
name|disliked
operator|=
name|fromAcountInfo
argument_list|(
name|li
operator|.
name|disliked
argument_list|)
expr_stmt|;
name|lo
operator|.
name|value
operator|=
name|li
operator|.
name|value
expr_stmt|;
name|lo
operator|.
name|defaultValue
operator|=
name|li
operator|.
name|defaultValue
expr_stmt|;
name|lo
operator|.
name|optional
operator|=
name|li
operator|.
name|optional
expr_stmt|;
name|lo
operator|.
name|blocking
operator|=
name|li
operator|.
name|blocking
expr_stmt|;
name|lo
operator|.
name|values
operator|=
name|li
operator|.
name|values
expr_stmt|;
if|if
condition|(
name|li
operator|.
name|all
operator|!=
literal|null
condition|)
block|{
name|lo
operator|.
name|all
operator|=
name|Lists
operator|.
name|newArrayListWithExpectedSize
argument_list|(
name|li
operator|.
name|all
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|ChangeJson
operator|.
name|ApprovalInfo
name|ai
range|:
name|li
operator|.
name|all
control|)
block|{
name|lo
operator|.
name|all
operator|.
name|add
argument_list|(
name|fromApprovalInfo
argument_list|(
name|ai
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|r
operator|.
name|put
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|lo
argument_list|)
expr_stmt|;
block|}
name|o
operator|.
name|labels
operator|=
name|r
expr_stmt|;
block|}
DECL|method|has (ListChangesOption o)
specifier|private
name|boolean
name|has
parameter_list|(
name|ListChangesOption
name|o
parameter_list|)
block|{
return|return
name|s
operator|.
name|contains
argument_list|(
name|o
argument_list|)
return|;
block|}
DECL|method|fromApprovalInfo (ChangeJson.ApprovalInfo ai)
specifier|private
specifier|static
name|ApprovalInfo
name|fromApprovalInfo
parameter_list|(
name|ChangeJson
operator|.
name|ApprovalInfo
name|ai
parameter_list|)
block|{
name|ApprovalInfo
name|ao
init|=
operator|new
name|ApprovalInfo
argument_list|()
decl_stmt|;
name|ao
operator|.
name|value
operator|=
name|ai
operator|.
name|value
expr_stmt|;
name|ao
operator|.
name|date
operator|=
name|ai
operator|.
name|date
expr_stmt|;
name|fromAccount
argument_list|(
name|ai
argument_list|,
name|ao
argument_list|)
expr_stmt|;
return|return
name|ao
return|;
block|}
DECL|method|fromAcountInfo ( com.google.gerrit.server.account.AccountInfo i)
specifier|private
specifier|static
name|AccountInfo
name|fromAcountInfo
parameter_list|(
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
name|AccountInfo
name|i
parameter_list|)
block|{
if|if
condition|(
name|i
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|AccountInfo
name|ai
init|=
operator|new
name|AccountInfo
argument_list|()
decl_stmt|;
name|fromAccount
argument_list|(
name|i
argument_list|,
name|ai
argument_list|)
expr_stmt|;
return|return
name|ai
return|;
block|}
DECL|method|fromAccount ( com.google.gerrit.server.account.AccountInfo i, AccountInfo ai)
specifier|private
specifier|static
name|void
name|fromAccount
parameter_list|(
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
name|AccountInfo
name|i
parameter_list|,
name|AccountInfo
name|ai
parameter_list|)
block|{
name|ai
operator|.
name|_accountId
operator|=
name|i
operator|.
name|_accountId
expr_stmt|;
name|ai
operator|.
name|email
operator|=
name|i
operator|.
name|email
expr_stmt|;
name|ai
operator|.
name|name
operator|=
name|i
operator|.
name|name
expr_stmt|;
name|ai
operator|.
name|username
operator|=
name|i
operator|.
name|username
expr_stmt|;
block|}
block|}
end_class

end_unit

