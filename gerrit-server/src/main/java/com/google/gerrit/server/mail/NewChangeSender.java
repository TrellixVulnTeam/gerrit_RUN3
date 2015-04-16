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
name|HashSet
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
name|Set
import|;
end_import

begin_comment
comment|/** Sends an email alerting a user to a new change for them to review. */
end_comment

begin_class
DECL|class|NewChangeSender
specifier|public
specifier|abstract
class|class
name|NewChangeSender
extends|extends
name|ChangeEmail
block|{
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
argument_list|<>
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
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|NewChangeSender (EmailArguments ea, ChangeData cd)
specifier|protected
name|NewChangeSender
parameter_list|(
name|EmailArguments
name|ea
parameter_list|,
name|ChangeData
name|cd
parameter_list|)
throws|throws
name|OrmException
block|{
name|super
argument_list|(
name|ea
argument_list|,
literal|"newchange"
argument_list|,
name|cd
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
throws|throws
name|EmailException
block|{
name|super
operator|.
name|init
argument_list|()
expr_stmt|;
name|setHeader
argument_list|(
literal|"Message-ID"
argument_list|,
name|getChangeMessageThreadId
argument_list|()
argument_list|)
expr_stmt|;
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
literal|"NewChange.vm"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getReviewerNames ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getReviewerNames
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
return|return
literal|null
return|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Account
operator|.
name|Id
name|id
range|:
name|reviewers
control|)
block|{
name|names
operator|.
name|add
argument_list|(
name|getNameFor
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|names
return|;
block|}
block|}
end_class

end_unit

