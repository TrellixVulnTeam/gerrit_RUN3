begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2016 The Android Open Source Project
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
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
operator|.
name|toList
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
name|restapi
operator|.
name|Response
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
name|RestReadView
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
name|account
operator|.
name|AccountInfoCacheFactory
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
name|AccountJson
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
name|java
operator|.
name|util
operator|.
name|Collections
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

begin_class
DECL|class|GetPastAssignees
specifier|public
class|class
name|GetPastAssignees
implements|implements
name|RestReadView
argument_list|<
name|ChangeResource
argument_list|>
block|{
DECL|field|accountInfos
specifier|private
specifier|final
name|AccountInfoCacheFactory
operator|.
name|Factory
name|accountInfos
decl_stmt|;
annotation|@
name|Inject
DECL|method|GetPastAssignees (AccountInfoCacheFactory.Factory accountInfosFactory)
name|GetPastAssignees
parameter_list|(
name|AccountInfoCacheFactory
operator|.
name|Factory
name|accountInfosFactory
parameter_list|)
block|{
name|this
operator|.
name|accountInfos
operator|=
name|accountInfosFactory
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (ChangeResource rsrc)
specifier|public
name|Response
argument_list|<
name|List
argument_list|<
name|AccountInfo
argument_list|>
argument_list|>
name|apply
parameter_list|(
name|ChangeResource
name|rsrc
parameter_list|)
throws|throws
name|OrmException
block|{
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|pastAssignees
init|=
name|rsrc
operator|.
name|getControl
argument_list|()
operator|.
name|getNotes
argument_list|()
operator|.
name|load
argument_list|()
operator|.
name|getPastAssignees
argument_list|()
decl_stmt|;
if|if
condition|(
name|pastAssignees
operator|==
literal|null
condition|)
block|{
return|return
name|Response
operator|.
name|ok
argument_list|(
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
return|;
block|}
name|AccountInfoCacheFactory
name|accountInfoFactory
init|=
name|accountInfos
operator|.
name|create
argument_list|()
decl_stmt|;
return|return
name|Response
operator|.
name|ok
argument_list|(
name|pastAssignees
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|accountInfoFactory
operator|::
name|get
argument_list|)
operator|.
name|map
argument_list|(
name|AccountJson
operator|::
name|toAccountInfo
argument_list|)
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

