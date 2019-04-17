begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2018 The Android Open Source Project
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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|truth
operator|.
name|Truth
operator|.
name|assertThat
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
name|LabelId
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
name|testing
operator|.
name|GerritBaseTests
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|Instant
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
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|LabelFunctionTest
specifier|public
class|class
name|LabelFunctionTest
extends|extends
name|GerritBaseTests
block|{
DECL|field|LABEL_NAME
specifier|private
specifier|static
specifier|final
name|String
name|LABEL_NAME
init|=
literal|"Verified"
decl_stmt|;
DECL|field|LABEL_ID
specifier|private
specifier|static
specifier|final
name|LabelId
name|LABEL_ID
init|=
name|LabelId
operator|.
name|create
argument_list|(
name|LABEL_NAME
argument_list|)
decl_stmt|;
DECL|field|CHANGE_ID
specifier|private
specifier|static
specifier|final
name|Change
operator|.
name|Id
name|CHANGE_ID
init|=
operator|new
name|Change
operator|.
name|Id
argument_list|(
literal|100
argument_list|)
decl_stmt|;
DECL|field|PS_ID
specifier|private
specifier|static
specifier|final
name|PatchSet
operator|.
name|Id
name|PS_ID
init|=
operator|new
name|PatchSet
operator|.
name|Id
argument_list|(
name|CHANGE_ID
argument_list|,
literal|1
argument_list|)
decl_stmt|;
DECL|field|VERIFIED_LABEL
specifier|private
specifier|static
specifier|final
name|LabelType
name|VERIFIED_LABEL
init|=
name|makeLabel
argument_list|()
decl_stmt|;
DECL|field|APPROVAL_2
specifier|private
specifier|static
specifier|final
name|PatchSetApproval
name|APPROVAL_2
init|=
name|makeApproval
argument_list|(
literal|2
argument_list|)
decl_stmt|;
DECL|field|APPROVAL_1
specifier|private
specifier|static
specifier|final
name|PatchSetApproval
name|APPROVAL_1
init|=
name|makeApproval
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|APPROVAL_0
specifier|private
specifier|static
specifier|final
name|PatchSetApproval
name|APPROVAL_0
init|=
name|makeApproval
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|APPROVAL_M1
specifier|private
specifier|static
specifier|final
name|PatchSetApproval
name|APPROVAL_M1
init|=
name|makeApproval
argument_list|(
operator|-
literal|1
argument_list|)
decl_stmt|;
DECL|field|APPROVAL_M2
specifier|private
specifier|static
specifier|final
name|PatchSetApproval
name|APPROVAL_M2
init|=
name|makeApproval
argument_list|(
operator|-
literal|2
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|checkLabelNameIsCorrect ()
specifier|public
name|void
name|checkLabelNameIsCorrect
parameter_list|()
block|{
for|for
control|(
name|LabelFunction
name|function
range|:
name|LabelFunction
operator|.
name|values
argument_list|()
control|)
block|{
name|SubmitRecord
operator|.
name|Label
name|myLabel
init|=
name|function
operator|.
name|check
argument_list|(
name|VERIFIED_LABEL
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|myLabel
operator|.
name|label
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"Verified"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|checkFunctionDoesNothing ()
specifier|public
name|void
name|checkFunctionDoesNothing
parameter_list|()
block|{
name|checkNothingHappens
argument_list|(
name|LabelFunction
operator|.
name|NO_BLOCK
argument_list|)
expr_stmt|;
name|checkNothingHappens
argument_list|(
name|LabelFunction
operator|.
name|NO_OP
argument_list|)
expr_stmt|;
name|checkNothingHappens
argument_list|(
name|LabelFunction
operator|.
name|PATCH_SET_LOCK
argument_list|)
expr_stmt|;
name|checkNothingHappens
argument_list|(
name|LabelFunction
operator|.
name|ANY_WITH_BLOCK
argument_list|)
expr_stmt|;
name|checkLabelIsRequired
argument_list|(
name|LabelFunction
operator|.
name|MAX_WITH_BLOCK
argument_list|)
expr_stmt|;
name|checkLabelIsRequired
argument_list|(
name|LabelFunction
operator|.
name|MAX_NO_BLOCK
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|checkBlockWorks ()
specifier|public
name|void
name|checkBlockWorks
parameter_list|()
block|{
name|checkBlockWorks
argument_list|(
name|LabelFunction
operator|.
name|ANY_WITH_BLOCK
argument_list|)
expr_stmt|;
name|checkBlockWorks
argument_list|(
name|LabelFunction
operator|.
name|MAX_WITH_BLOCK
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|checkMaxWorks ()
specifier|public
name|void
name|checkMaxWorks
parameter_list|()
block|{
name|checkMaxIsEnforced
argument_list|(
name|LabelFunction
operator|.
name|MAX_NO_BLOCK
argument_list|)
expr_stmt|;
name|checkMaxIsEnforced
argument_list|(
name|LabelFunction
operator|.
name|MAX_WITH_BLOCK
argument_list|)
expr_stmt|;
name|checkMaxValidatesTheLabel
argument_list|(
name|LabelFunction
operator|.
name|MAX_NO_BLOCK
argument_list|)
expr_stmt|;
name|checkMaxValidatesTheLabel
argument_list|(
name|LabelFunction
operator|.
name|MAX_WITH_BLOCK
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|checkMaxNoBlockIgnoresMin ()
specifier|public
name|void
name|checkMaxNoBlockIgnoresMin
parameter_list|()
block|{
name|List
argument_list|<
name|PatchSetApproval
argument_list|>
name|approvals
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|APPROVAL_M2
argument_list|,
name|APPROVAL_2
argument_list|,
name|APPROVAL_M2
argument_list|)
decl_stmt|;
name|SubmitRecord
operator|.
name|Label
name|myLabel
init|=
name|LabelFunction
operator|.
name|MAX_NO_BLOCK
operator|.
name|check
argument_list|(
name|VERIFIED_LABEL
argument_list|,
name|approvals
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|myLabel
operator|.
name|status
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|SubmitRecord
operator|.
name|Label
operator|.
name|Status
operator|.
name|OK
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|myLabel
operator|.
name|appliedBy
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|APPROVAL_2
operator|.
name|getAccountId
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|makeLabel ()
specifier|private
specifier|static
name|LabelType
name|makeLabel
parameter_list|()
block|{
name|List
argument_list|<
name|LabelValue
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// The label text is irrelevant here, only the numerical value is used
name|values
operator|.
name|add
argument_list|(
operator|new
name|LabelValue
argument_list|(
operator|(
name|short
operator|)
operator|-
literal|2
argument_list|,
literal|"Great job, please fix compilation."
argument_list|)
argument_list|)
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
operator|new
name|LabelValue
argument_list|(
operator|(
name|short
operator|)
operator|-
literal|1
argument_list|,
literal|"Really good, please make some minor changes."
argument_list|)
argument_list|)
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
operator|new
name|LabelValue
argument_list|(
operator|(
name|short
operator|)
literal|0
argument_list|,
literal|"No vote."
argument_list|)
argument_list|)
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
operator|new
name|LabelValue
argument_list|(
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|"Closest thing perfection."
argument_list|)
argument_list|)
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
operator|new
name|LabelValue
argument_list|(
operator|(
name|short
operator|)
literal|2
argument_list|,
literal|"Perfect!"
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|LabelType
argument_list|(
name|LABEL_NAME
argument_list|,
name|values
argument_list|)
return|;
block|}
DECL|method|makeApproval (int value)
specifier|private
specifier|static
name|PatchSetApproval
name|makeApproval
parameter_list|(
name|int
name|value
parameter_list|)
block|{
name|Account
operator|.
name|Id
name|accountId
init|=
operator|new
name|Account
operator|.
name|Id
argument_list|(
literal|10000
operator|+
name|value
argument_list|)
decl_stmt|;
name|PatchSetApproval
operator|.
name|Key
name|key
init|=
name|makeKey
argument_list|(
name|PS_ID
argument_list|,
name|accountId
argument_list|,
name|LABEL_ID
argument_list|)
decl_stmt|;
return|return
operator|new
name|PatchSetApproval
argument_list|(
name|key
argument_list|,
operator|(
name|short
operator|)
name|value
argument_list|,
name|Date
operator|.
name|from
argument_list|(
name|Instant
operator|.
name|now
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|makeKey ( PatchSet.Id psId, Account.Id accountId, LabelId labelId)
specifier|private
specifier|static
name|PatchSetApproval
operator|.
name|Key
name|makeKey
parameter_list|(
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|,
name|Account
operator|.
name|Id
name|accountId
parameter_list|,
name|LabelId
name|labelId
parameter_list|)
block|{
return|return
name|PatchSetApproval
operator|.
name|key
argument_list|(
name|psId
argument_list|,
name|accountId
argument_list|,
name|labelId
argument_list|)
return|;
block|}
DECL|method|checkBlockWorks (LabelFunction function)
specifier|private
specifier|static
name|void
name|checkBlockWorks
parameter_list|(
name|LabelFunction
name|function
parameter_list|)
block|{
name|List
argument_list|<
name|PatchSetApproval
argument_list|>
name|approvals
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|APPROVAL_1
argument_list|,
name|APPROVAL_M2
argument_list|,
name|APPROVAL_2
argument_list|)
decl_stmt|;
name|SubmitRecord
operator|.
name|Label
name|myLabel
init|=
name|function
operator|.
name|check
argument_list|(
name|VERIFIED_LABEL
argument_list|,
name|approvals
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|myLabel
operator|.
name|status
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|SubmitRecord
operator|.
name|Label
operator|.
name|Status
operator|.
name|REJECT
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|myLabel
operator|.
name|appliedBy
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|APPROVAL_M2
operator|.
name|getAccountId
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|checkNothingHappens (LabelFunction function)
specifier|private
specifier|static
name|void
name|checkNothingHappens
parameter_list|(
name|LabelFunction
name|function
parameter_list|)
block|{
name|SubmitRecord
operator|.
name|Label
name|myLabel
init|=
name|function
operator|.
name|check
argument_list|(
name|VERIFIED_LABEL
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|myLabel
operator|.
name|status
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|SubmitRecord
operator|.
name|Label
operator|.
name|Status
operator|.
name|MAY
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|myLabel
operator|.
name|appliedBy
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
block|}
DECL|method|checkLabelIsRequired (LabelFunction function)
specifier|private
specifier|static
name|void
name|checkLabelIsRequired
parameter_list|(
name|LabelFunction
name|function
parameter_list|)
block|{
name|SubmitRecord
operator|.
name|Label
name|myLabel
init|=
name|function
operator|.
name|check
argument_list|(
name|VERIFIED_LABEL
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|myLabel
operator|.
name|status
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|SubmitRecord
operator|.
name|Label
operator|.
name|Status
operator|.
name|NEED
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|myLabel
operator|.
name|appliedBy
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
block|}
DECL|method|checkMaxIsEnforced (LabelFunction function)
specifier|private
specifier|static
name|void
name|checkMaxIsEnforced
parameter_list|(
name|LabelFunction
name|function
parameter_list|)
block|{
name|List
argument_list|<
name|PatchSetApproval
argument_list|>
name|approvals
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|APPROVAL_1
argument_list|,
name|APPROVAL_0
argument_list|)
decl_stmt|;
name|SubmitRecord
operator|.
name|Label
name|myLabel
init|=
name|function
operator|.
name|check
argument_list|(
name|VERIFIED_LABEL
argument_list|,
name|approvals
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|myLabel
operator|.
name|status
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|SubmitRecord
operator|.
name|Label
operator|.
name|Status
operator|.
name|NEED
argument_list|)
expr_stmt|;
block|}
DECL|method|checkMaxValidatesTheLabel (LabelFunction function)
specifier|private
specifier|static
name|void
name|checkMaxValidatesTheLabel
parameter_list|(
name|LabelFunction
name|function
parameter_list|)
block|{
name|List
argument_list|<
name|PatchSetApproval
argument_list|>
name|approvals
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|APPROVAL_1
argument_list|,
name|APPROVAL_2
argument_list|,
name|APPROVAL_M1
argument_list|)
decl_stmt|;
name|SubmitRecord
operator|.
name|Label
name|myLabel
init|=
name|function
operator|.
name|check
argument_list|(
name|VERIFIED_LABEL
argument_list|,
name|approvals
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|myLabel
operator|.
name|status
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|SubmitRecord
operator|.
name|Label
operator|.
name|Status
operator|.
name|OK
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|myLabel
operator|.
name|appliedBy
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|APPROVAL_2
operator|.
name|getAccountId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

