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
DECL|package|com.google.gerrit.reviewdb
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
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
comment|/**  * Acceptance of a {@link ContributorAgreement} by an {@link AccountGroup}.  */
end_comment

begin_class
DECL|class|AccountGroupAgreement
specifier|public
specifier|final
class|class
name|AccountGroupAgreement
implements|implements
name|AbstractAgreement
block|{
DECL|class|Key
specifier|public
specifier|static
class|class
name|Key
extends|extends
name|CompoundKey
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|1
argument_list|)
DECL|field|groupId
specifier|protected
name|AccountGroup
operator|.
name|Id
name|groupId
decl_stmt|;
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|2
argument_list|)
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
name|groupId
operator|=
operator|new
name|AccountGroup
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
DECL|method|Key (final AccountGroup.Id group, final ContributorAgreement.Id cla)
specifier|public
name|Key
parameter_list|(
specifier|final
name|AccountGroup
operator|.
name|Id
name|group
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
name|groupId
operator|=
name|group
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
name|AccountGroup
operator|.
name|Id
name|getParentKey
parameter_list|()
block|{
return|return
name|groupId
return|;
block|}
DECL|method|getContributorAgreementId ()
specifier|public
name|ContributorAgreement
operator|.
name|Id
name|getContributorAgreementId
parameter_list|()
block|{
return|return
name|claId
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
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|1
argument_list|,
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
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|2
argument_list|)
DECL|field|acceptedOn
specifier|protected
name|Timestamp
name|acceptedOn
decl_stmt|;
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|3
argument_list|)
DECL|field|status
specifier|protected
name|char
name|status
decl_stmt|;
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|4
argument_list|,
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
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|5
argument_list|,
name|notNull
operator|=
literal|false
argument_list|)
DECL|field|reviewedOn
specifier|protected
name|Timestamp
name|reviewedOn
decl_stmt|;
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|6
argument_list|,
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
DECL|method|AccountGroupAgreement ()
specifier|protected
name|AccountGroupAgreement
parameter_list|()
block|{   }
DECL|method|AccountGroupAgreement (final AccountGroupAgreement.Key k)
specifier|public
name|AccountGroupAgreement
parameter_list|(
specifier|final
name|AccountGroupAgreement
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
DECL|method|getKey ()
specifier|public
name|AccountGroupAgreement
operator|.
name|Key
name|getKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
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
block|}
end_class

end_unit

