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
name|client
operator|.
name|rpc
operator|.
name|Natives
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
name|ui
operator|.
name|InlineHyperlink
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
name|extensions
operator|.
name|common
operator|.
name|ListChangesOption
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
name|core
operator|.
name|client
operator|.
name|JsArray
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
name|http
operator|.
name|client
operator|.
name|URL
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
name|EnumSet
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
name|ListIterator
import|;
end_import

begin_class
DECL|class|DashboardTable
specifier|public
class|class
name|DashboardTable
extends|extends
name|ChangeTable2
block|{
DECL|field|sections
specifier|private
name|List
argument_list|<
name|Section
argument_list|>
name|sections
decl_stmt|;
DECL|field|title
specifier|private
name|String
name|title
decl_stmt|;
DECL|field|titles
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|titles
decl_stmt|;
DECL|field|queries
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|queries
decl_stmt|;
DECL|method|DashboardTable (String params)
specifier|public
name|DashboardTable
parameter_list|(
name|String
name|params
parameter_list|)
block|{
name|titles
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|queries
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|String
name|foreach
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|kvPair
range|:
name|params
operator|.
name|split
argument_list|(
literal|"[,;&]"
argument_list|)
control|)
block|{
name|String
index|[]
name|kv
init|=
name|kvPair
operator|.
name|split
argument_list|(
literal|"="
argument_list|,
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
name|kv
operator|.
name|length
operator|!=
literal|2
operator|||
name|kv
index|[
literal|0
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
literal|"title"
operator|.
name|equals
argument_list|(
name|kv
index|[
literal|0
index|]
argument_list|)
condition|)
block|{
name|title
operator|=
name|URL
operator|.
name|decodeQueryString
argument_list|(
name|kv
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"foreach"
operator|.
name|equals
argument_list|(
name|kv
index|[
literal|0
index|]
argument_list|)
condition|)
block|{
name|foreach
operator|=
name|URL
operator|.
name|decodeQueryString
argument_list|(
name|kv
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|titles
operator|.
name|add
argument_list|(
name|URL
operator|.
name|decodeQueryString
argument_list|(
name|kv
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|queries
operator|.
name|add
argument_list|(
name|URL
operator|.
name|decodeQueryString
argument_list|(
name|kv
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|foreach
operator|!=
literal|null
condition|)
block|{
name|ListIterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|queries
operator|.
name|listIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|it
operator|.
name|set
argument_list|(
name|it
operator|.
name|next
argument_list|()
operator|+
literal|" "
operator|+
name|foreach
argument_list|)
expr_stmt|;
block|}
block|}
name|addStyleName
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|accountDashboard
argument_list|()
argument_list|)
expr_stmt|;
name|sections
operator|=
operator|new
name|ArrayList
argument_list|<
name|ChangeTable2
operator|.
name|Section
argument_list|>
argument_list|()
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|title
range|:
name|titles
control|)
block|{
name|Section
name|s
init|=
operator|new
name|Section
argument_list|()
decl_stmt|;
name|String
name|query
init|=
name|removeLimit
argument_list|(
name|queries
operator|.
name|get
argument_list|(
name|i
operator|++
argument_list|)
argument_list|)
decl_stmt|;
name|s
operator|.
name|setTitleWidget
argument_list|(
operator|new
name|InlineHyperlink
argument_list|(
name|title
argument_list|,
name|PageLinks
operator|.
name|toChangeQuery
argument_list|(
name|query
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|addSection
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|sections
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|removeLimit (String query)
specifier|private
name|String
name|removeLimit
parameter_list|(
name|String
name|query
parameter_list|)
block|{
name|StringBuilder
name|unlimitedQuery
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
index|[]
name|operators
init|=
name|query
operator|.
name|split
argument_list|(
literal|" "
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|o
range|:
name|operators
control|)
block|{
if|if
condition|(
operator|!
name|o
operator|.
name|startsWith
argument_list|(
literal|"limit:"
argument_list|)
condition|)
block|{
name|unlimitedQuery
operator|.
name|append
argument_list|(
name|o
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|unlimitedQuery
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
return|;
block|}
DECL|method|getTitle ()
specifier|public
name|String
name|getTitle
parameter_list|()
block|{
return|return
name|title
return|;
block|}
annotation|@
name|Override
DECL|method|onLoad ()
specifier|protected
name|void
name|onLoad
parameter_list|()
block|{
name|super
operator|.
name|onLoad
argument_list|()
expr_stmt|;
if|if
condition|(
name|queries
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|ChangeList
operator|.
name|next
argument_list|(
name|queries
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|0
argument_list|,
name|PagedSingleListScreen
operator|.
name|MAX_SORTKEY
argument_list|,
operator|new
name|GerritCallback
argument_list|<
name|ChangeList
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|ChangeList
name|result
parameter_list|)
block|{
name|updateColumnsForLabels
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|sections
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|display
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|finishDisplay
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|queries
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|ChangeList
operator|.
name|query
argument_list|(
operator|new
name|GerritCallback
argument_list|<
name|JsArray
argument_list|<
name|ChangeList
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|JsArray
argument_list|<
name|ChangeList
argument_list|>
name|result
parameter_list|)
block|{
name|List
argument_list|<
name|ChangeList
argument_list|>
name|cls
init|=
name|Natives
operator|.
name|asList
argument_list|(
name|result
argument_list|)
decl_stmt|;
name|updateColumnsForLabels
argument_list|(
name|cls
operator|.
name|toArray
argument_list|(
operator|new
name|ChangeList
index|[
name|cls
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|cls
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|sections
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|display
argument_list|(
name|cls
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|finishDisplay
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|,
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|ListChangesOption
operator|.
name|class
argument_list|)
argument_list|,
name|queries
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|queries
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

