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
DECL|package|com.google.gerrit.server.restapi.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|restapi
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
name|registration
operator|.
name|DynamicMap
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
name|ChildCollection
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
name|IdString
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
name|ResourceNotFoundException
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
name|extensions
operator|.
name|restapi
operator|.
name|TopLevelResource
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
name|mail
operator|.
name|Address
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
name|change
operator|.
name|ChangeResource
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
name|ReviewerResource
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
name|restapi
operator|.
name|account
operator|.
name|AccountsCollection
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
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|errors
operator|.
name|ConfigInvalidException
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|Reviewers
specifier|public
class|class
name|Reviewers
implements|implements
name|ChildCollection
argument_list|<
name|ChangeResource
argument_list|,
name|ReviewerResource
argument_list|>
block|{
DECL|field|views
specifier|private
specifier|final
name|DynamicMap
argument_list|<
name|RestView
argument_list|<
name|ReviewerResource
argument_list|>
argument_list|>
name|views
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
DECL|field|approvalsUtil
specifier|private
specifier|final
name|ApprovalsUtil
name|approvalsUtil
decl_stmt|;
DECL|field|accounts
specifier|private
specifier|final
name|AccountsCollection
name|accounts
decl_stmt|;
DECL|field|resourceFactory
specifier|private
specifier|final
name|ReviewerResource
operator|.
name|Factory
name|resourceFactory
decl_stmt|;
DECL|field|list
specifier|private
specifier|final
name|ListReviewers
name|list
decl_stmt|;
annotation|@
name|Inject
DECL|method|Reviewers ( Provider<ReviewDb> dbProvider, ApprovalsUtil approvalsUtil, AccountsCollection accounts, ReviewerResource.Factory resourceFactory, DynamicMap<RestView<ReviewerResource>> views, ListReviewers list)
name|Reviewers
parameter_list|(
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|dbProvider
parameter_list|,
name|ApprovalsUtil
name|approvalsUtil
parameter_list|,
name|AccountsCollection
name|accounts
parameter_list|,
name|ReviewerResource
operator|.
name|Factory
name|resourceFactory
parameter_list|,
name|DynamicMap
argument_list|<
name|RestView
argument_list|<
name|ReviewerResource
argument_list|>
argument_list|>
name|views
parameter_list|,
name|ListReviewers
name|list
parameter_list|)
block|{
name|this
operator|.
name|dbProvider
operator|=
name|dbProvider
expr_stmt|;
name|this
operator|.
name|approvalsUtil
operator|=
name|approvalsUtil
expr_stmt|;
name|this
operator|.
name|accounts
operator|=
name|accounts
expr_stmt|;
name|this
operator|.
name|resourceFactory
operator|=
name|resourceFactory
expr_stmt|;
name|this
operator|.
name|views
operator|=
name|views
expr_stmt|;
name|this
operator|.
name|list
operator|=
name|list
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|views ()
specifier|public
name|DynamicMap
argument_list|<
name|RestView
argument_list|<
name|ReviewerResource
argument_list|>
argument_list|>
name|views
parameter_list|()
block|{
return|return
name|views
return|;
block|}
annotation|@
name|Override
DECL|method|list ()
specifier|public
name|RestView
argument_list|<
name|ChangeResource
argument_list|>
name|list
parameter_list|()
block|{
return|return
name|list
return|;
block|}
annotation|@
name|Override
DECL|method|parse (ChangeResource rsrc, IdString id)
specifier|public
name|ReviewerResource
name|parse
parameter_list|(
name|ChangeResource
name|rsrc
parameter_list|,
name|IdString
name|id
parameter_list|)
throws|throws
name|OrmException
throws|,
name|ResourceNotFoundException
throws|,
name|AuthException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|Address
name|address
init|=
name|Address
operator|.
name|tryParse
argument_list|(
name|id
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
name|Account
operator|.
name|Id
name|accountId
init|=
literal|null
decl_stmt|;
try|try
block|{
name|accountId
operator|=
name|accounts
operator|.
name|parse
argument_list|(
name|TopLevelResource
operator|.
name|INSTANCE
argument_list|,
name|id
argument_list|)
operator|.
name|getUser
argument_list|()
operator|.
name|getAccountId
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ResourceNotFoundException
name|e
parameter_list|)
block|{
if|if
condition|(
name|address
operator|==
literal|null
condition|)
block|{
throw|throw
name|e
throw|;
block|}
block|}
comment|// See if the id exists as a reviewer for this change
if|if
condition|(
name|accountId
operator|!=
literal|null
operator|&&
name|fetchAccountIds
argument_list|(
name|rsrc
argument_list|)
operator|.
name|contains
argument_list|(
name|accountId
argument_list|)
condition|)
block|{
return|return
name|resourceFactory
operator|.
name|create
argument_list|(
name|rsrc
argument_list|,
name|accountId
argument_list|)
return|;
block|}
comment|// See if the address exists as a reviewer on the change
if|if
condition|(
name|address
operator|!=
literal|null
operator|&&
name|rsrc
operator|.
name|getNotes
argument_list|()
operator|.
name|getReviewersByEmail
argument_list|()
operator|.
name|all
argument_list|()
operator|.
name|contains
argument_list|(
name|address
argument_list|)
condition|)
block|{
return|return
operator|new
name|ReviewerResource
argument_list|(
name|rsrc
argument_list|,
name|address
argument_list|)
return|;
block|}
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
name|id
argument_list|)
throw|;
block|}
DECL|method|fetchAccountIds (ChangeResource rsrc)
specifier|private
name|Collection
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|fetchAccountIds
parameter_list|(
name|ChangeResource
name|rsrc
parameter_list|)
throws|throws
name|OrmException
block|{
return|return
name|approvalsUtil
operator|.
name|getReviewers
argument_list|(
name|dbProvider
operator|.
name|get
argument_list|()
argument_list|,
name|rsrc
operator|.
name|getNotes
argument_list|()
argument_list|)
operator|.
name|all
argument_list|()
return|;
block|}
block|}
end_class

end_unit

