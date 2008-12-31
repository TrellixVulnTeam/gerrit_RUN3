begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright 2008 Google Inc.
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
DECL|package|com.google.gerrit.client.reviewdb
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|reviewdb
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|client
operator|.
name|Column
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
name|client
operator|.
name|CompoundKey
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Timestamp
import|;
end_import

begin_comment
comment|/** Electronic acceptance of a {@link ContributorAgreement} by {@link Account} */
end_comment

begin_class
DECL|class|AccountAgreement
specifier|public
specifier|final
class|class
name|AccountAgreement
block|{
DECL|class|Key
specifier|public
specifier|static
class|class
name|Key
extends|extends
name|CompoundKey
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
block|{
annotation|@
name|Column
DECL|field|accountId
specifier|protected
name|Account
operator|.
name|Id
name|accountId
decl_stmt|;
annotation|@
name|Column
DECL|field|claId
specifier|protected
name|ContributorAgreement
operator|.
name|Id
name|claId
decl_stmt|;
DECL|method|Key ()
specifier|protected
name|Key
parameter_list|()
block|{
name|accountId
operator|=
operator|new
name|Account
operator|.
name|Id
argument_list|()
expr_stmt|;
name|claId
operator|=
operator|new
name|ContributorAgreement
operator|.
name|Id
argument_list|()
expr_stmt|;
block|}
DECL|method|Key (final Account.Id account, final ContributorAgreement.Id cla)
specifier|public
name|Key
parameter_list|(
specifier|final
name|Account
operator|.
name|Id
name|account
parameter_list|,
specifier|final
name|ContributorAgreement
operator|.
name|Id
name|cla
parameter_list|)
block|{
name|this
operator|.
name|accountId
operator|=
name|account
expr_stmt|;
name|this
operator|.
name|claId
operator|=
name|cla
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getParentKey ()
specifier|public
name|Account
operator|.
name|Id
name|getParentKey
parameter_list|()
block|{
return|return
name|accountId
return|;
block|}
annotation|@
name|Override
DECL|method|members ()
specifier|public
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|client
operator|.
name|Key
argument_list|<
name|?
argument_list|>
index|[]
name|members
parameter_list|()
block|{
return|return
operator|new
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|client
operator|.
name|Key
argument_list|<
name|?
argument_list|>
index|[]
block|{
name|claId
block|}
empty_stmt|;
block|}
block|}
DECL|field|NEW_CODE
specifier|protected
specifier|static
specifier|final
name|char
name|NEW_CODE
init|=
literal|'n'
decl_stmt|;
DECL|enum|Status
specifier|public
specifier|static
enum|enum
name|Status
block|{
DECL|enumConstant|NEW
name|NEW
parameter_list|(
name|NEW_CODE
parameter_list|)
operator|,
DECL|enumConstant|VERIFIED
constructor|VERIFIED('V'
block|)
enum|,
DECL|enumConstant|REJECTED
name|REJECTED
argument_list|(
literal|'R'
argument_list|)
enum|;
DECL|field|code
specifier|private
specifier|final
name|char
name|code
decl_stmt|;
DECL|method|Status (final char c)
specifier|private
name|Status
parameter_list|(
specifier|final
name|char
name|c
parameter_list|)
block|{
name|code
operator|=
name|c
expr_stmt|;
block|}
DECL|method|getCode ()
specifier|public
name|char
name|getCode
parameter_list|()
block|{
return|return
name|code
return|;
block|}
DECL|method|forCode (final char c)
specifier|public
specifier|static
name|Status
name|forCode
parameter_list|(
specifier|final
name|char
name|c
parameter_list|)
block|{
for|for
control|(
specifier|final
name|Status
name|s
range|:
name|Status
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|s
operator|.
name|code
operator|==
name|c
condition|)
block|{
return|return
name|s
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

begin_decl_stmt
annotation|@
name|Column
argument_list|(
name|name
operator|=
name|Column
operator|.
name|NONE
argument_list|)
DECL|field|key
specifier|protected
name|Key
name|key
decl_stmt|;
end_decl_stmt

begin_decl_stmt
annotation|@
name|Column
DECL|field|acceptedOn
specifier|protected
name|Timestamp
name|acceptedOn
decl_stmt|;
end_decl_stmt

begin_decl_stmt
annotation|@
name|Column
DECL|field|status
specifier|protected
name|char
name|status
decl_stmt|;
end_decl_stmt

begin_decl_stmt
annotation|@
name|Column
argument_list|(
name|notNull
operator|=
literal|false
argument_list|)
DECL|field|reviewedBy
specifier|protected
name|Account
operator|.
name|Id
name|reviewedBy
decl_stmt|;
end_decl_stmt

begin_decl_stmt
annotation|@
name|Column
argument_list|(
name|notNull
operator|=
literal|false
argument_list|)
DECL|field|reviewedOn
specifier|protected
name|Timestamp
name|reviewedOn
decl_stmt|;
end_decl_stmt

begin_decl_stmt
annotation|@
name|Column
argument_list|(
name|notNull
operator|=
literal|false
argument_list|,
name|length
operator|=
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
DECL|field|reviewComments
specifier|protected
name|String
name|reviewComments
decl_stmt|;
end_decl_stmt

begin_constructor
DECL|method|AccountAgreement ()
specifier|protected
name|AccountAgreement
parameter_list|()
block|{   }
end_constructor

begin_constructor
DECL|method|AccountAgreement (final AccountAgreement.Key k)
specifier|public
name|AccountAgreement
parameter_list|(
specifier|final
name|AccountAgreement
operator|.
name|Key
name|k
parameter_list|)
block|{
name|key
operator|=
name|k
expr_stmt|;
name|acceptedOn
operator|=
operator|new
name|Timestamp
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|status
operator|=
name|Status
operator|.
name|NEW
operator|.
name|getCode
argument_list|()
expr_stmt|;
block|}
end_constructor

begin_function
DECL|method|getKey ()
specifier|public
name|AccountAgreement
operator|.
name|Key
name|getKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
end_function

begin_function
DECL|method|getAgreementId ()
specifier|public
name|ContributorAgreement
operator|.
name|Id
name|getAgreementId
parameter_list|()
block|{
return|return
name|key
operator|.
name|claId
return|;
block|}
end_function

begin_function
DECL|method|getAcceptedOn ()
specifier|public
name|Timestamp
name|getAcceptedOn
parameter_list|()
block|{
return|return
name|acceptedOn
return|;
block|}
end_function

begin_function
DECL|method|getStatus ()
specifier|public
name|Status
name|getStatus
parameter_list|()
block|{
return|return
name|Status
operator|.
name|forCode
argument_list|(
name|status
argument_list|)
return|;
block|}
end_function

begin_function
DECL|method|getReviewedOn ()
specifier|public
name|Timestamp
name|getReviewedOn
parameter_list|()
block|{
return|return
name|reviewedOn
return|;
block|}
end_function

begin_function
DECL|method|getReviewedBy ()
specifier|public
name|Account
operator|.
name|Id
name|getReviewedBy
parameter_list|()
block|{
return|return
name|reviewedBy
return|;
block|}
end_function

begin_function
DECL|method|getReviewComments ()
specifier|public
name|String
name|getReviewComments
parameter_list|()
block|{
return|return
name|reviewComments
return|;
block|}
end_function

begin_function
DECL|method|setReviewComments (final String s)
specifier|public
name|void
name|setReviewComments
parameter_list|(
specifier|final
name|String
name|s
parameter_list|)
block|{
name|reviewComments
operator|=
name|s
expr_stmt|;
block|}
end_function

begin_function
DECL|method|review (final Status newStatus, final Account.Id by)
specifier|public
name|void
name|review
parameter_list|(
specifier|final
name|Status
name|newStatus
parameter_list|,
specifier|final
name|Account
operator|.
name|Id
name|by
parameter_list|)
block|{
name|status
operator|=
name|newStatus
operator|.
name|getCode
argument_list|()
expr_stmt|;
name|reviewedBy
operator|=
name|by
expr_stmt|;
name|reviewedOn
operator|=
operator|new
name|Timestamp
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
end_function

unit|}
end_unit

