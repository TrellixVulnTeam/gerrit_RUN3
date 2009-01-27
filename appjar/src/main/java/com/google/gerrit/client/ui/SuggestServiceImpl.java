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
DECL|package|com.google.gerrit.client.ui
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|ui
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
name|data
operator|.
name|AccountCache
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
name|data
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
name|AccountExternalId
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
name|client
operator|.
name|reviewdb
operator|.
name|Project
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
name|client
operator|.
name|rpc
operator|.
name|BaseServiceImplementation
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
name|rpc
operator|.
name|Common
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|user
operator|.
name|client
operator|.
name|rpc
operator|.
name|AsyncCallback
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
name|LinkedHashMap
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

begin_class
DECL|class|SuggestServiceImpl
specifier|public
class|class
name|SuggestServiceImpl
extends|extends
name|BaseServiceImplementation
implements|implements
name|SuggestService
block|{
DECL|field|MAX_SUFFIX
specifier|private
specifier|static
specifier|final
name|String
name|MAX_SUFFIX
init|=
literal|"\u9fa5"
decl_stmt|;
DECL|method|suggestProjectNameKey (final String query, final int limit, final AsyncCallback<List<Project.NameKey>> callback)
specifier|public
name|void
name|suggestProjectNameKey
parameter_list|(
specifier|final
name|String
name|query
parameter_list|,
specifier|final
name|int
name|limit
parameter_list|,
specifier|final
name|AsyncCallback
argument_list|<
name|List
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
argument_list|>
name|callback
parameter_list|)
block|{
name|run
argument_list|(
name|callback
argument_list|,
operator|new
name|Action
argument_list|<
name|List
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|public
name|List
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
name|run
parameter_list|(
specifier|final
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
block|{
specifier|final
name|String
name|a
init|=
name|query
decl_stmt|;
specifier|final
name|String
name|b
init|=
name|a
operator|+
name|MAX_SUFFIX
decl_stmt|;
specifier|final
name|int
name|max
init|=
literal|10
decl_stmt|;
specifier|final
name|int
name|n
init|=
name|limit
operator|<=
literal|0
condition|?
name|max
else|:
name|Math
operator|.
name|min
argument_list|(
name|limit
argument_list|,
name|max
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
name|r
init|=
operator|new
name|ArrayList
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Project
name|p
range|:
name|db
operator|.
name|projects
argument_list|()
operator|.
name|suggestByName
argument_list|(
name|a
argument_list|,
name|b
argument_list|,
name|n
argument_list|)
control|)
block|{
if|if
condition|(
name|canRead
argument_list|(
name|p
operator|.
name|getNameKey
argument_list|()
argument_list|)
condition|)
block|{
name|r
operator|.
name|add
argument_list|(
name|p
operator|.
name|getNameKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|r
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|suggestAccount (final String query, final int limit, final AsyncCallback<List<AccountInfo>> callback)
specifier|public
name|void
name|suggestAccount
parameter_list|(
specifier|final
name|String
name|query
parameter_list|,
specifier|final
name|int
name|limit
parameter_list|,
specifier|final
name|AsyncCallback
argument_list|<
name|List
argument_list|<
name|AccountInfo
argument_list|>
argument_list|>
name|callback
parameter_list|)
block|{
name|run
argument_list|(
name|callback
argument_list|,
operator|new
name|Action
argument_list|<
name|List
argument_list|<
name|AccountInfo
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|public
name|List
argument_list|<
name|AccountInfo
argument_list|>
name|run
parameter_list|(
specifier|final
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
block|{
specifier|final
name|String
name|a
init|=
name|query
decl_stmt|;
specifier|final
name|String
name|b
init|=
name|a
operator|+
name|MAX_SUFFIX
decl_stmt|;
specifier|final
name|int
name|max
init|=
literal|10
decl_stmt|;
specifier|final
name|int
name|n
init|=
name|limit
operator|<=
literal|0
condition|?
name|max
else|:
name|Math
operator|.
name|min
argument_list|(
name|limit
argument_list|,
name|max
argument_list|)
decl_stmt|;
specifier|final
name|LinkedHashMap
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|AccountInfo
argument_list|>
name|r
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|AccountInfo
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Account
name|p
range|:
name|db
operator|.
name|accounts
argument_list|()
operator|.
name|suggestByFullName
argument_list|(
name|a
argument_list|,
name|b
argument_list|,
name|n
argument_list|)
control|)
block|{
name|r
operator|.
name|put
argument_list|(
name|p
operator|.
name|getId
argument_list|()
argument_list|,
operator|new
name|AccountInfo
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|r
operator|.
name|size
argument_list|()
operator|<
name|n
condition|)
block|{
for|for
control|(
specifier|final
name|Account
name|p
range|:
name|db
operator|.
name|accounts
argument_list|()
operator|.
name|suggestByPreferredEmail
argument_list|(
name|a
argument_list|,
name|b
argument_list|,
name|n
operator|-
name|r
operator|.
name|size
argument_list|()
argument_list|)
control|)
block|{
name|r
operator|.
name|put
argument_list|(
name|p
operator|.
name|getId
argument_list|()
argument_list|,
operator|new
name|AccountInfo
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|r
operator|.
name|size
argument_list|()
operator|<
name|n
condition|)
block|{
specifier|final
name|AccountCache
name|ac
init|=
name|Common
operator|.
name|getAccountCache
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|AccountExternalId
name|e
range|:
name|db
operator|.
name|accountExternalIds
argument_list|()
operator|.
name|suggestByEmailAddress
argument_list|(
name|a
argument_list|,
name|b
argument_list|,
name|n
operator|-
name|r
operator|.
name|size
argument_list|()
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|r
operator|.
name|containsKey
argument_list|(
name|e
operator|.
name|getAccountId
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|Account
name|p
init|=
name|ac
operator|.
name|get
argument_list|(
name|e
operator|.
name|getAccountId
argument_list|()
argument_list|,
name|db
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|r
operator|.
name|put
argument_list|(
name|e
operator|.
name|getAccountId
argument_list|()
argument_list|,
operator|new
name|AccountInfo
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
operator|new
name|ArrayList
argument_list|<
name|AccountInfo
argument_list|>
argument_list|(
name|r
operator|.
name|values
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|suggestAccountGroup (final String query, final int limit, final AsyncCallback<List<AccountGroup>> callback)
specifier|public
name|void
name|suggestAccountGroup
parameter_list|(
specifier|final
name|String
name|query
parameter_list|,
specifier|final
name|int
name|limit
parameter_list|,
specifier|final
name|AsyncCallback
argument_list|<
name|List
argument_list|<
name|AccountGroup
argument_list|>
argument_list|>
name|callback
parameter_list|)
block|{
name|run
argument_list|(
name|callback
argument_list|,
operator|new
name|Action
argument_list|<
name|List
argument_list|<
name|AccountGroup
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|public
name|List
argument_list|<
name|AccountGroup
argument_list|>
name|run
parameter_list|(
specifier|final
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
block|{
specifier|final
name|String
name|a
init|=
name|query
decl_stmt|;
specifier|final
name|String
name|b
init|=
name|a
operator|+
name|MAX_SUFFIX
decl_stmt|;
specifier|final
name|int
name|max
init|=
literal|10
decl_stmt|;
specifier|final
name|int
name|n
init|=
name|limit
operator|<=
literal|0
condition|?
name|max
else|:
name|Math
operator|.
name|min
argument_list|(
name|limit
argument_list|,
name|max
argument_list|)
decl_stmt|;
return|return
name|db
operator|.
name|accountGroups
argument_list|()
operator|.
name|suggestByName
argument_list|(
name|a
argument_list|,
name|b
argument_list|,
name|n
argument_list|)
operator|.
name|toList
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

