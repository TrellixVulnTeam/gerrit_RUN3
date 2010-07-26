begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2008 The Android Open Source Project
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
DECL|package|com.google.gerrit.client.changes
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|changes
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
name|Gerrit
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
name|GerritCallback
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
name|common
operator|.
name|PageLinks
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
name|common
operator|.
name|data
operator|.
name|ChangeInfo
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
name|common
operator|.
name|data
operator|.
name|SingleListChangeInfo
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
name|RevId
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
name|KeyUtil
import|;
end_import

begin_class
DECL|class|QueryScreen
specifier|public
class|class
name|QueryScreen
extends|extends
name|PagedSingleListScreen
block|{
DECL|method|forQuery (String query)
specifier|public
specifier|static
name|QueryScreen
name|forQuery
parameter_list|(
name|String
name|query
parameter_list|)
block|{
return|return
name|forQuery
argument_list|(
name|query
argument_list|,
name|PageLinks
operator|.
name|TOP
argument_list|)
return|;
block|}
DECL|method|forQuery (String query, String position)
specifier|public
specifier|static
name|QueryScreen
name|forQuery
parameter_list|(
name|String
name|query
parameter_list|,
name|String
name|position
parameter_list|)
block|{
return|return
operator|new
name|QueryScreen
argument_list|(
name|KeyUtil
operator|.
name|encode
argument_list|(
name|query
argument_list|)
argument_list|,
name|position
argument_list|)
return|;
block|}
DECL|field|query
specifier|private
specifier|final
name|String
name|query
decl_stmt|;
DECL|method|QueryScreen (final String encQuery, final String positionToken)
specifier|public
name|QueryScreen
parameter_list|(
specifier|final
name|String
name|encQuery
parameter_list|,
specifier|final
name|String
name|positionToken
parameter_list|)
block|{
name|super
argument_list|(
literal|"q,"
operator|+
name|encQuery
argument_list|,
name|positionToken
argument_list|)
expr_stmt|;
name|query
operator|=
name|KeyUtil
operator|.
name|decode
argument_list|(
name|encQuery
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onInitUI ()
specifier|protected
name|void
name|onInitUI
parameter_list|()
block|{
name|super
operator|.
name|onInitUI
argument_list|()
expr_stmt|;
name|setWindowTitle
argument_list|(
name|Util
operator|.
name|M
operator|.
name|changeQueryWindowTitle
argument_list|(
name|query
argument_list|)
argument_list|)
expr_stmt|;
name|setPageTitle
argument_list|(
name|Util
operator|.
name|M
operator|.
name|changeQueryPageTitle
argument_list|(
name|query
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|loadCallback ()
specifier|protected
name|AsyncCallback
argument_list|<
name|SingleListChangeInfo
argument_list|>
name|loadCallback
parameter_list|()
block|{
return|return
operator|new
name|GerritCallback
argument_list|<
name|SingleListChangeInfo
argument_list|>
argument_list|()
block|{
specifier|public
specifier|final
name|void
name|onSuccess
parameter_list|(
specifier|final
name|SingleListChangeInfo
name|result
parameter_list|)
block|{
if|if
condition|(
name|isAttached
argument_list|()
condition|)
block|{
if|if
condition|(
name|result
operator|.
name|getChanges
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
name|isSingleQuery
argument_list|(
name|query
argument_list|)
condition|)
block|{
specifier|final
name|ChangeInfo
name|c
init|=
name|result
operator|.
name|getChanges
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Gerrit
operator|.
name|display
argument_list|(
name|PageLinks
operator|.
name|toChange
argument_list|(
name|c
argument_list|)
argument_list|,
operator|new
name|ChangeScreen
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Gerrit
operator|.
name|setQueryString
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|display
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|QueryScreen
operator|.
name|this
operator|.
name|display
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|loadPrev ()
specifier|protected
name|void
name|loadPrev
parameter_list|()
block|{
name|Util
operator|.
name|LIST_SVC
operator|.
name|allQueryPrev
argument_list|(
name|query
argument_list|,
name|pos
argument_list|,
name|pageSize
argument_list|,
name|loadCallback
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|loadNext ()
specifier|protected
name|void
name|loadNext
parameter_list|()
block|{
name|Util
operator|.
name|LIST_SVC
operator|.
name|allQueryNext
argument_list|(
name|query
argument_list|,
name|pos
argument_list|,
name|pageSize
argument_list|,
name|loadCallback
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|isSingleQuery (String query)
specifier|private
specifier|static
name|boolean
name|isSingleQuery
parameter_list|(
name|String
name|query
parameter_list|)
block|{
if|if
condition|(
name|query
operator|.
name|matches
argument_list|(
literal|"^[1-9][0-9]*$"
argument_list|)
condition|)
block|{
comment|// Legacy numeric identifier.
comment|//
return|return
literal|true
return|;
block|}
if|if
condition|(
name|query
operator|.
name|matches
argument_list|(
literal|"^[iI][0-9a-f]{4,}$"
argument_list|)
condition|)
block|{
comment|// Newer style Change-Id.
comment|//
return|return
literal|true
return|;
block|}
if|if
condition|(
name|query
operator|.
name|matches
argument_list|(
literal|"^([0-9a-fA-F]{4,"
operator|+
name|RevId
operator|.
name|LEN
operator|+
literal|"})$"
argument_list|)
condition|)
block|{
comment|// Commit SHA-1 of any change.
comment|//
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

