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
DECL|package|com.google.gerrit.client.data
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|data
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/** Summary information needed for screens showing a single list of changes}. */
end_comment

begin_class
DECL|class|SingleListChangeInfo
specifier|public
class|class
name|SingleListChangeInfo
block|{
DECL|field|accounts
specifier|protected
name|AccountInfoCache
name|accounts
decl_stmt|;
DECL|field|changes
specifier|protected
name|List
argument_list|<
name|ChangeInfo
argument_list|>
name|changes
decl_stmt|;
DECL|method|SingleListChangeInfo ()
specifier|public
name|SingleListChangeInfo
parameter_list|()
block|{   }
DECL|method|getAccounts ()
specifier|public
name|AccountInfoCache
name|getAccounts
parameter_list|()
block|{
return|return
name|accounts
return|;
block|}
DECL|method|setAccounts (final AccountInfoCache ac)
specifier|public
name|void
name|setAccounts
parameter_list|(
specifier|final
name|AccountInfoCache
name|ac
parameter_list|)
block|{
name|accounts
operator|=
name|ac
expr_stmt|;
block|}
DECL|method|getChanges ()
specifier|public
name|List
argument_list|<
name|ChangeInfo
argument_list|>
name|getChanges
parameter_list|()
block|{
return|return
name|changes
return|;
block|}
DECL|method|setChanges (List<ChangeInfo> c)
specifier|public
name|void
name|setChanges
parameter_list|(
name|List
argument_list|<
name|ChangeInfo
argument_list|>
name|c
parameter_list|)
block|{
name|changes
operator|=
name|c
expr_stmt|;
block|}
block|}
end_class

end_unit

