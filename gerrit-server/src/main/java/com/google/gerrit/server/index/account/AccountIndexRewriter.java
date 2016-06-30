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
DECL|package|com.google.gerrit.server.index.account
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|index
operator|.
name|account
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
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
name|AccountState
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
name|index
operator|.
name|IndexRewriter
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
name|index
operator|.
name|QueryOptions
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
name|Predicate
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
name|QueryParseException
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
name|account
operator|.
name|AccountPredicates
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
name|Singleton
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|AccountIndexRewriter
specifier|public
class|class
name|AccountIndexRewriter
implements|implements
name|IndexRewriter
argument_list|<
name|AccountState
argument_list|>
block|{
DECL|field|indexes
specifier|private
specifier|final
name|AccountIndexCollection
name|indexes
decl_stmt|;
annotation|@
name|Inject
DECL|method|AccountIndexRewriter (AccountIndexCollection indexes)
name|AccountIndexRewriter
parameter_list|(
name|AccountIndexCollection
name|indexes
parameter_list|)
block|{
name|this
operator|.
name|indexes
operator|=
name|indexes
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|rewrite (Predicate<AccountState> in, QueryOptions opts)
specifier|public
name|Predicate
argument_list|<
name|AccountState
argument_list|>
name|rewrite
parameter_list|(
name|Predicate
argument_list|<
name|AccountState
argument_list|>
name|in
parameter_list|,
name|QueryOptions
name|opts
parameter_list|)
throws|throws
name|QueryParseException
block|{
if|if
condition|(
operator|!
name|AccountPredicates
operator|.
name|hasActive
argument_list|(
name|in
argument_list|)
condition|)
block|{
name|in
operator|=
name|Predicate
operator|.
name|and
argument_list|(
name|in
argument_list|,
name|AccountPredicates
operator|.
name|isActive
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|AccountIndex
name|index
init|=
name|indexes
operator|.
name|getSearchIndex
argument_list|()
decl_stmt|;
name|checkNotNull
argument_list|(
name|index
argument_list|,
literal|"no active search index configured for accounts"
argument_list|)
expr_stmt|;
return|return
operator|new
name|IndexedAccountQuery
argument_list|(
name|index
argument_list|,
name|in
argument_list|,
name|opts
argument_list|)
return|;
block|}
block|}
end_class

end_unit

