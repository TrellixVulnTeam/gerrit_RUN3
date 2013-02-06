begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2009 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.mail
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|mail
package|;
end_package

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
name|HashBasedTable
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
name|Table
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
name|LabelValue
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
name|errors
operator|.
name|EmailException
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
name|AccountProjectWatch
operator|.
name|NotifyType
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
name|PatchSetApproval
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
name|assistedinject
operator|.
name|Assisted
import|;
end_import

begin_comment
comment|/** Send notice about a change successfully merged. */
end_comment

begin_class
DECL|class|MergedSender
specifier|public
class|class
name|MergedSender
extends|extends
name|ReplyToChangeSender
block|{
DECL|interface|Factory
specifier|public
specifier|static
interface|interface
name|Factory
block|{
DECL|method|create (Change change)
specifier|public
name|MergedSender
name|create
parameter_list|(
name|Change
name|change
parameter_list|)
function_decl|;
block|}
DECL|field|labelTypes
specifier|private
specifier|final
name|LabelTypes
name|labelTypes
decl_stmt|;
annotation|@
name|Inject
DECL|method|MergedSender (EmailArguments ea, LabelTypes lt, @Assisted Change c)
specifier|public
name|MergedSender
parameter_list|(
name|EmailArguments
name|ea
parameter_list|,
name|LabelTypes
name|lt
parameter_list|,
annotation|@
name|Assisted
name|Change
name|c
parameter_list|)
block|{
name|super
argument_list|(
name|ea
argument_list|,
name|c
argument_list|,
literal|"merged"
argument_list|)
expr_stmt|;
name|labelTypes
operator|=
name|lt
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init ()
specifier|protected
name|void
name|init
parameter_list|()
throws|throws
name|EmailException
block|{
name|super
operator|.
name|init
argument_list|()
expr_stmt|;
name|ccAllApprovals
argument_list|()
expr_stmt|;
name|bccStarredBy
argument_list|()
expr_stmt|;
name|includeWatchers
argument_list|(
name|NotifyType
operator|.
name|ALL_COMMENTS
argument_list|)
expr_stmt|;
name|includeWatchers
argument_list|(
name|NotifyType
operator|.
name|SUBMITTED_CHANGES
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|formatChange ()
specifier|protected
name|void
name|formatChange
parameter_list|()
throws|throws
name|EmailException
block|{
name|appendText
argument_list|(
name|velocifyFile
argument_list|(
literal|"Merged.vm"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getApprovals ()
specifier|public
name|String
name|getApprovals
parameter_list|()
block|{
try|try
block|{
name|Table
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|String
argument_list|,
name|PatchSetApproval
argument_list|>
name|pos
init|=
name|HashBasedTable
operator|.
name|create
argument_list|()
decl_stmt|;
name|Table
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|String
argument_list|,
name|PatchSetApproval
argument_list|>
name|neg
init|=
name|HashBasedTable
operator|.
name|create
argument_list|()
decl_stmt|;
for|for
control|(
name|PatchSetApproval
name|ca
range|:
name|args
operator|.
name|db
operator|.
name|get
argument_list|()
operator|.
name|patchSetApprovals
argument_list|()
operator|.
name|byPatchSet
argument_list|(
name|patchSet
operator|.
name|getId
argument_list|()
argument_list|)
control|)
block|{
name|LabelType
name|lt
init|=
name|labelTypes
operator|.
name|byId
argument_list|(
name|ca
operator|.
name|getCategoryId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|lt
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|ca
operator|.
name|getValue
argument_list|()
operator|>
literal|0
condition|)
block|{
name|pos
operator|.
name|put
argument_list|(
name|ca
operator|.
name|getAccountId
argument_list|()
argument_list|,
name|lt
operator|.
name|getName
argument_list|()
argument_list|,
name|ca
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ca
operator|.
name|getValue
argument_list|()
operator|<
literal|0
condition|)
block|{
name|neg
operator|.
name|put
argument_list|(
name|ca
operator|.
name|getAccountId
argument_list|()
argument_list|,
name|lt
operator|.
name|getName
argument_list|()
argument_list|,
name|ca
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|format
argument_list|(
literal|"Approvals"
argument_list|,
name|pos
argument_list|)
operator|+
name|format
argument_list|(
literal|"Objections"
argument_list|,
name|neg
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|err
parameter_list|)
block|{
comment|// Don't list the approvals
block|}
return|return
literal|""
return|;
block|}
DECL|method|format (String type, Table<Account.Id, String, PatchSetApproval> approvals)
specifier|private
name|String
name|format
parameter_list|(
name|String
name|type
parameter_list|,
name|Table
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|String
argument_list|,
name|PatchSetApproval
argument_list|>
name|approvals
parameter_list|)
block|{
name|StringBuilder
name|txt
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|approvals
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|""
return|;
block|}
name|txt
operator|.
name|append
argument_list|(
name|type
argument_list|)
operator|.
name|append
argument_list|(
literal|":\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|Account
operator|.
name|Id
name|id
range|:
name|approvals
operator|.
name|rowKeySet
argument_list|()
control|)
block|{
name|txt
operator|.
name|append
argument_list|(
literal|"  "
argument_list|)
expr_stmt|;
name|txt
operator|.
name|append
argument_list|(
name|getNameFor
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|txt
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
expr_stmt|;
name|boolean
name|first
init|=
literal|true
decl_stmt|;
for|for
control|(
name|LabelType
name|lt
range|:
name|labelTypes
operator|.
name|getLabelTypes
argument_list|()
control|)
block|{
name|PatchSetApproval
name|ca
init|=
name|approvals
operator|.
name|get
argument_list|(
name|id
argument_list|,
name|lt
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|ca
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|first
condition|)
block|{
name|first
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|txt
operator|.
name|append
argument_list|(
literal|"; "
argument_list|)
expr_stmt|;
block|}
name|LabelValue
name|v
init|=
name|lt
operator|.
name|getValue
argument_list|(
name|ca
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
name|txt
operator|.
name|append
argument_list|(
name|v
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|txt
operator|.
name|append
argument_list|(
name|lt
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|txt
operator|.
name|append
argument_list|(
literal|'='
argument_list|)
expr_stmt|;
name|txt
operator|.
name|append
argument_list|(
name|LabelValue
operator|.
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
name|txt
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
name|txt
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
return|return
name|txt
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

