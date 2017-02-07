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
name|RestResource
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
name|RestView
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
name|server
operator|.
name|IdentifiedUser
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
name|inject
operator|.
name|TypeLiteral
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|assistedinject
operator|.
name|AssistedInject
import|;
end_import

begin_class
DECL|class|ReviewerResource
specifier|public
class|class
name|ReviewerResource
implements|implements
name|RestResource
block|{
DECL|field|REVIEWER_KIND
specifier|public
specifier|static
specifier|final
name|TypeLiteral
argument_list|<
name|RestView
argument_list|<
name|ReviewerResource
argument_list|>
argument_list|>
name|REVIEWER_KIND
init|=
operator|new
name|TypeLiteral
argument_list|<
name|RestView
argument_list|<
name|ReviewerResource
argument_list|>
argument_list|>
argument_list|()
block|{}
decl_stmt|;
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (ChangeResource change, Account.Id id)
name|ReviewerResource
name|create
parameter_list|(
name|ChangeResource
name|change
parameter_list|,
name|Account
operator|.
name|Id
name|id
parameter_list|)
function_decl|;
DECL|method|create (RevisionResource revision, Account.Id id)
name|ReviewerResource
name|create
parameter_list|(
name|RevisionResource
name|revision
parameter_list|,
name|Account
operator|.
name|Id
name|id
parameter_list|)
function_decl|;
block|}
DECL|field|change
specifier|private
specifier|final
name|ChangeResource
name|change
decl_stmt|;
DECL|field|revision
specifier|private
specifier|final
name|RevisionResource
name|revision
decl_stmt|;
DECL|field|user
specifier|private
specifier|final
name|IdentifiedUser
name|user
decl_stmt|;
annotation|@
name|AssistedInject
DECL|method|ReviewerResource ( IdentifiedUser.GenericFactory userFactory, @Assisted ChangeResource change, @Assisted Account.Id id)
name|ReviewerResource
parameter_list|(
name|IdentifiedUser
operator|.
name|GenericFactory
name|userFactory
parameter_list|,
annotation|@
name|Assisted
name|ChangeResource
name|change
parameter_list|,
annotation|@
name|Assisted
name|Account
operator|.
name|Id
name|id
parameter_list|)
block|{
name|this
operator|.
name|change
operator|=
name|change
expr_stmt|;
name|this
operator|.
name|revision
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|userFactory
operator|.
name|create
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AssistedInject
DECL|method|ReviewerResource ( IdentifiedUser.GenericFactory userFactory, @Assisted RevisionResource revision, @Assisted Account.Id id)
name|ReviewerResource
parameter_list|(
name|IdentifiedUser
operator|.
name|GenericFactory
name|userFactory
parameter_list|,
annotation|@
name|Assisted
name|RevisionResource
name|revision
parameter_list|,
annotation|@
name|Assisted
name|Account
operator|.
name|Id
name|id
parameter_list|)
block|{
name|this
operator|.
name|revision
operator|=
name|revision
expr_stmt|;
name|this
operator|.
name|change
operator|=
name|revision
operator|.
name|getChangeResource
argument_list|()
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|userFactory
operator|.
name|create
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
DECL|method|getChangeResource ()
specifier|public
name|ChangeResource
name|getChangeResource
parameter_list|()
block|{
return|return
name|change
return|;
block|}
DECL|method|getRevisionResource ()
specifier|public
name|RevisionResource
name|getRevisionResource
parameter_list|()
block|{
return|return
name|revision
return|;
block|}
DECL|method|getChangeId ()
specifier|public
name|Change
operator|.
name|Id
name|getChangeId
parameter_list|()
block|{
return|return
name|change
operator|.
name|getId
argument_list|()
return|;
block|}
DECL|method|getChange ()
specifier|public
name|Change
name|getChange
parameter_list|()
block|{
return|return
name|change
operator|.
name|getChange
argument_list|()
return|;
block|}
DECL|method|getReviewerUser ()
specifier|public
name|IdentifiedUser
name|getReviewerUser
parameter_list|()
block|{
return|return
name|user
return|;
block|}
comment|/**    * @return the control for the caller's user (as opposed to the reviewer's user as returned by    *     {@link #getReviewerControl()}).    */
DECL|method|getControl ()
specifier|public
name|ChangeControl
name|getControl
parameter_list|()
block|{
return|return
name|change
operator|.
name|getControl
argument_list|()
return|;
block|}
comment|/**    * @return the control for the reviewer's user (as opposed to the caller's user as returned by    *     {@link #getControl()}).    */
DECL|method|getReviewerControl ()
specifier|public
name|ChangeControl
name|getReviewerControl
parameter_list|()
block|{
return|return
name|change
operator|.
name|getControl
argument_list|()
operator|.
name|forUser
argument_list|(
name|user
argument_list|)
return|;
block|}
block|}
end_class

end_unit

