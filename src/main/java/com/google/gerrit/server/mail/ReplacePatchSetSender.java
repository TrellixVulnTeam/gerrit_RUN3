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
name|gerrit
operator|.
name|client
operator|.
name|reviewdb
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
name|client
operator|.
name|reviewdb
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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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

begin_comment
comment|/** Send notice of new patch sets for reviewers. */
end_comment

begin_class
DECL|class|ReplacePatchSetSender
specifier|public
class|class
name|ReplacePatchSetSender
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
name|ReplacePatchSetSender
name|create
parameter_list|(
name|Change
name|change
parameter_list|)
function_decl|;
block|}
DECL|field|reviewers
specifier|private
specifier|final
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|reviewers
init|=
operator|new
name|HashSet
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|extraCC
specifier|private
specifier|final
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|extraCC
init|=
operator|new
name|HashSet
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Inject
DECL|method|ReplacePatchSetSender (@ssisted Change c)
specifier|public
name|ReplacePatchSetSender
parameter_list|(
annotation|@
name|Assisted
name|Change
name|c
parameter_list|)
block|{
name|super
argument_list|(
name|c
argument_list|,
literal|"newpatchset"
argument_list|)
expr_stmt|;
block|}
DECL|method|addReviewers (final Collection<Account.Id> cc)
specifier|public
name|void
name|addReviewers
parameter_list|(
specifier|final
name|Collection
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|cc
parameter_list|)
block|{
name|reviewers
operator|.
name|addAll
argument_list|(
name|cc
argument_list|)
expr_stmt|;
block|}
DECL|method|addExtraCC (final Collection<Account.Id> cc)
specifier|public
name|void
name|addExtraCC
parameter_list|(
specifier|final
name|Collection
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|cc
parameter_list|)
block|{
name|extraCC
operator|.
name|addAll
argument_list|(
name|cc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init ()
specifier|protected
name|void
name|init
parameter_list|()
block|{
name|super
operator|.
name|init
argument_list|()
expr_stmt|;
if|if
condition|(
name|fromId
operator|!=
literal|null
condition|)
block|{
comment|// Don't call yourself a reviewer of your own patch set.
comment|//
name|reviewers
operator|.
name|remove
argument_list|(
name|fromId
argument_list|)
expr_stmt|;
block|}
name|add
argument_list|(
name|RecipientType
operator|.
name|TO
argument_list|,
name|reviewers
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|RecipientType
operator|.
name|CC
argument_list|,
name|extraCC
argument_list|)
expr_stmt|;
name|rcptToAuthors
argument_list|(
name|RecipientType
operator|.
name|CC
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|format ()
specifier|protected
name|void
name|format
parameter_list|()
block|{
name|formatSalutation
argument_list|()
expr_stmt|;
name|formatChangeDetail
argument_list|()
expr_stmt|;
block|}
DECL|method|formatSalutation ()
specifier|private
name|void
name|formatSalutation
parameter_list|()
block|{
if|if
condition|(
name|reviewers
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|formatDest
argument_list|()
expr_stmt|;
if|if
condition|(
name|getChangeUrl
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|appendText
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|appendText
argument_list|(
literal|"    "
operator|+
name|getChangeUrl
argument_list|()
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|appendText
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
name|appendText
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|appendText
argument_list|(
literal|"Hello"
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|i
init|=
name|reviewers
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|appendText
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|appendText
argument_list|(
name|getNameFor
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|appendText
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
name|appendText
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|appendText
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|appendText
argument_list|(
literal|"I'd like you to reexamine change "
operator|+
name|change
operator|.
name|getKey
argument_list|()
operator|.
name|abbreviate
argument_list|()
operator|+
literal|"."
argument_list|)
expr_stmt|;
if|if
condition|(
name|getChangeUrl
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|appendText
argument_list|(
literal|"  Please visit\n"
argument_list|)
expr_stmt|;
name|appendText
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|appendText
argument_list|(
literal|"    "
operator|+
name|getChangeUrl
argument_list|()
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|appendText
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|appendText
argument_list|(
literal|"to look at patch set "
operator|+
name|patchSet
operator|.
name|getPatchSetId
argument_list|()
argument_list|)
expr_stmt|;
name|appendText
argument_list|(
literal|":\n"
argument_list|)
expr_stmt|;
block|}
name|appendText
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|formatDest
argument_list|()
expr_stmt|;
name|appendText
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|formatDest ()
specifier|private
name|void
name|formatDest
parameter_list|()
block|{
name|appendText
argument_list|(
literal|"Change "
operator|+
name|change
operator|.
name|getKey
argument_list|()
operator|.
name|abbreviate
argument_list|()
argument_list|)
expr_stmt|;
name|appendText
argument_list|(
literal|" (patch set "
operator|+
name|patchSet
operator|.
name|getPatchSetId
argument_list|()
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|appendText
argument_list|(
literal|" for "
argument_list|)
expr_stmt|;
name|appendText
argument_list|(
name|change
operator|.
name|getDest
argument_list|()
operator|.
name|getShortName
argument_list|()
argument_list|)
expr_stmt|;
name|appendText
argument_list|(
literal|" in "
argument_list|)
expr_stmt|;
name|appendText
argument_list|(
name|projectName
argument_list|)
expr_stmt|;
name|appendText
argument_list|(
literal|":\n"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

