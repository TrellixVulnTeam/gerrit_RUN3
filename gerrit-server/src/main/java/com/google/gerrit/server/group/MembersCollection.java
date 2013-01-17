begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2013 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.group
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|group
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
name|data
operator|.
name|GroupDetail
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
name|AcceptsCreate
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
name|AccountGroup
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
name|AccountGroupMember
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
name|account
operator|.
name|AccountResolver
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
name|GroupCache
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
name|GroupDetailFactory
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
name|group
operator|.
name|PutMembers
operator|.
name|PutMember
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
name|util
operator|.
name|Url
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

begin_class
DECL|class|MembersCollection
specifier|public
class|class
name|MembersCollection
implements|implements
name|ChildCollection
argument_list|<
name|GroupResource
argument_list|,
name|MemberResource
argument_list|>
implements|,
name|AcceptsCreate
argument_list|<
name|GroupResource
argument_list|>
block|{
DECL|field|views
specifier|private
specifier|final
name|DynamicMap
argument_list|<
name|RestView
argument_list|<
name|MemberResource
argument_list|>
argument_list|>
name|views
decl_stmt|;
DECL|field|list
specifier|private
specifier|final
name|Provider
argument_list|<
name|ListMembers
argument_list|>
name|list
decl_stmt|;
DECL|field|userGenericFactory
specifier|private
specifier|final
name|IdentifiedUser
operator|.
name|GenericFactory
name|userGenericFactory
decl_stmt|;
DECL|field|groupCache
specifier|private
specifier|final
name|GroupCache
name|groupCache
decl_stmt|;
DECL|field|groupDetailFactory
specifier|private
specifier|final
name|GroupDetailFactory
operator|.
name|Factory
name|groupDetailFactory
decl_stmt|;
DECL|field|accountResolver
specifier|private
specifier|final
name|AccountResolver
name|accountResolver
decl_stmt|;
DECL|field|put
specifier|private
specifier|final
name|Provider
argument_list|<
name|PutMembers
argument_list|>
name|put
decl_stmt|;
annotation|@
name|Inject
DECL|method|MembersCollection (final DynamicMap<RestView<MemberResource>> views, final Provider<ListMembers> list, final IdentifiedUser.GenericFactory userGenericFactory, final GroupCache groupCache, final GroupDetailFactory.Factory groupDetailFactory, final AccountResolver accountResolver, final Provider<PutMembers> put)
name|MembersCollection
parameter_list|(
specifier|final
name|DynamicMap
argument_list|<
name|RestView
argument_list|<
name|MemberResource
argument_list|>
argument_list|>
name|views
parameter_list|,
specifier|final
name|Provider
argument_list|<
name|ListMembers
argument_list|>
name|list
parameter_list|,
specifier|final
name|IdentifiedUser
operator|.
name|GenericFactory
name|userGenericFactory
parameter_list|,
specifier|final
name|GroupCache
name|groupCache
parameter_list|,
specifier|final
name|GroupDetailFactory
operator|.
name|Factory
name|groupDetailFactory
parameter_list|,
specifier|final
name|AccountResolver
name|accountResolver
parameter_list|,
specifier|final
name|Provider
argument_list|<
name|PutMembers
argument_list|>
name|put
parameter_list|)
block|{
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
name|this
operator|.
name|userGenericFactory
operator|=
name|userGenericFactory
expr_stmt|;
name|this
operator|.
name|groupCache
operator|=
name|groupCache
expr_stmt|;
name|this
operator|.
name|groupDetailFactory
operator|=
name|groupDetailFactory
expr_stmt|;
name|this
operator|.
name|accountResolver
operator|=
name|accountResolver
expr_stmt|;
name|this
operator|.
name|put
operator|=
name|put
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|list ()
specifier|public
name|RestView
argument_list|<
name|GroupResource
argument_list|>
name|list
parameter_list|()
throws|throws
name|ResourceNotFoundException
throws|,
name|AuthException
block|{
return|return
name|list
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|parse (final GroupResource parent, final String id)
specifier|public
name|MemberResource
name|parse
parameter_list|(
specifier|final
name|GroupResource
name|parent
parameter_list|,
specifier|final
name|String
name|id
parameter_list|)
throws|throws
name|ResourceNotFoundException
throws|,
name|Exception
block|{
specifier|final
name|Account
name|a
init|=
name|accountResolver
operator|.
name|find
argument_list|(
name|Url
operator|.
name|decode
argument_list|(
name|id
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
name|id
argument_list|)
throw|;
block|}
specifier|final
name|AccountGroup
name|group
init|=
name|groupCache
operator|.
name|get
argument_list|(
name|parent
operator|.
name|getControl
argument_list|()
operator|.
name|getGroup
argument_list|()
operator|.
name|getGroupUUID
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|GroupDetail
name|groupDetail
init|=
name|groupDetailFactory
operator|.
name|create
argument_list|(
name|group
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|call
argument_list|()
decl_stmt|;
if|if
condition|(
name|groupDetail
operator|.
name|members
operator|!=
literal|null
condition|)
block|{
for|for
control|(
specifier|final
name|AccountGroupMember
name|member
range|:
name|groupDetail
operator|.
name|members
control|)
block|{
if|if
condition|(
name|member
operator|.
name|getAccountId
argument_list|()
operator|.
name|equals
argument_list|(
name|a
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
return|return
operator|new
name|MemberResource
argument_list|(
name|userGenericFactory
operator|.
name|create
argument_list|(
name|a
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
name|id
argument_list|)
throw|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|create (final GroupResource group, final String id)
specifier|public
name|PutMember
name|create
parameter_list|(
specifier|final
name|GroupResource
name|group
parameter_list|,
specifier|final
name|String
name|id
parameter_list|)
block|{
return|return
operator|new
name|PutMember
argument_list|(
name|put
argument_list|,
name|Url
operator|.
name|decode
argument_list|(
name|id
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|views ()
specifier|public
name|DynamicMap
argument_list|<
name|RestView
argument_list|<
name|MemberResource
argument_list|>
argument_list|>
name|views
parameter_list|()
block|{
return|return
name|views
return|;
block|}
DECL|method|parse (final Account account)
specifier|public
specifier|static
name|MemberInfo
name|parse
parameter_list|(
specifier|final
name|Account
name|account
parameter_list|)
block|{
specifier|final
name|MemberInfo
name|accountInfo
init|=
operator|new
name|MemberInfo
argument_list|()
decl_stmt|;
name|accountInfo
operator|.
name|setId
argument_list|(
name|account
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|accountInfo
operator|.
name|fullName
operator|=
name|account
operator|.
name|getFullName
argument_list|()
expr_stmt|;
name|accountInfo
operator|.
name|preferredEmail
operator|=
name|account
operator|.
name|getPreferredEmail
argument_list|()
expr_stmt|;
name|accountInfo
operator|.
name|userName
operator|=
name|account
operator|.
name|getUserName
argument_list|()
expr_stmt|;
return|return
name|accountInfo
return|;
block|}
DECL|class|MemberInfo
specifier|static
class|class
name|MemberInfo
block|{
DECL|field|kind
specifier|final
name|String
name|kind
init|=
literal|"gerritcodereview#member"
decl_stmt|;
DECL|field|fullName
name|String
name|fullName
decl_stmt|;
DECL|field|id
name|String
name|id
decl_stmt|;
DECL|field|accountId
name|int
name|accountId
decl_stmt|;
DECL|field|preferredEmail
name|String
name|preferredEmail
decl_stmt|;
DECL|field|userName
name|String
name|userName
decl_stmt|;
DECL|method|setId (Account.Id i)
name|void
name|setId
parameter_list|(
name|Account
operator|.
name|Id
name|i
parameter_list|)
block|{
name|accountId
operator|=
name|i
operator|.
name|get
argument_list|()
expr_stmt|;
name|id
operator|=
name|Url
operator|.
name|encode
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|accountId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

