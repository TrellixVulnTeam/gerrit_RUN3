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
DECL|package|com.google.gerrit.client.account
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|account
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
name|gwt
operator|.
name|i18n
operator|.
name|client
operator|.
name|LocaleInfo
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
name|ui
operator|.
name|ChangeListener
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
name|ui
operator|.
name|Composite
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
name|ui
operator|.
name|FlowPanel
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
name|ui
operator|.
name|Grid
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
name|ui
operator|.
name|ListBox
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
name|ui
operator|.
name|Widget
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtjsonrpc
operator|.
name|client
operator|.
name|VoidResult
import|;
end_import

begin_class
DECL|class|PreferencePanel
specifier|public
class|class
name|PreferencePanel
extends|extends
name|Composite
block|{
DECL|field|defaultContext
specifier|private
name|ListBox
name|defaultContext
decl_stmt|;
DECL|field|oldDefaultContext
specifier|private
name|short
name|oldDefaultContext
decl_stmt|;
DECL|method|PreferencePanel ()
specifier|public
name|PreferencePanel
parameter_list|()
block|{
specifier|final
name|FlowPanel
name|body
init|=
operator|new
name|FlowPanel
argument_list|()
decl_stmt|;
name|defaultContext
operator|=
operator|new
name|ListBox
argument_list|()
expr_stmt|;
for|for
control|(
specifier|final
name|short
name|v
range|:
name|Account
operator|.
name|CONTEXT_CHOICES
control|)
block|{
name|defaultContext
operator|.
name|addItem
argument_list|(
name|Util
operator|.
name|M
operator|.
name|lines
argument_list|(
name|v
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|v
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|defaultContext
operator|.
name|addChangeListener
argument_list|(
operator|new
name|ChangeListener
argument_list|()
block|{
specifier|public
name|void
name|onChange
parameter_list|(
name|Widget
name|sender
parameter_list|)
block|{
specifier|final
name|int
name|idx
init|=
name|defaultContext
operator|.
name|getSelectedIndex
argument_list|()
decl_stmt|;
if|if
condition|(
name|idx
operator|<
literal|0
condition|)
block|{
return|return;
block|}
specifier|final
name|short
name|newLines
init|=
name|Short
operator|.
name|parseShort
argument_list|(
name|defaultContext
operator|.
name|getValue
argument_list|(
name|idx
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|newLines
operator|==
name|oldDefaultContext
condition|)
block|{
return|return;
block|}
name|Util
operator|.
name|ACCOUNT_SVC
operator|.
name|changeDefaultContext
argument_list|(
name|newLines
argument_list|,
operator|new
name|GerritCallback
argument_list|<
name|VoidResult
argument_list|>
argument_list|()
block|{
specifier|public
name|void
name|onSuccess
parameter_list|(
specifier|final
name|VoidResult
name|result
parameter_list|)
block|{
name|oldDefaultContext
operator|=
name|newLines
expr_stmt|;
name|Gerrit
operator|.
name|getUserAccount
argument_list|()
operator|.
name|setDefaultContext
argument_list|(
name|newLines
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
specifier|final
name|Throwable
name|caught
parameter_list|)
block|{
name|setDefaultContext
argument_list|(
name|oldDefaultContext
argument_list|)
expr_stmt|;
name|super
operator|.
name|onFailure
argument_list|(
name|caught
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
specifier|final
name|int
name|labelIdx
decl_stmt|,
name|fieldIdx
decl_stmt|;
if|if
condition|(
name|LocaleInfo
operator|.
name|getCurrentLocale
argument_list|()
operator|.
name|isRTL
argument_list|()
condition|)
block|{
name|labelIdx
operator|=
literal|1
expr_stmt|;
name|fieldIdx
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|labelIdx
operator|=
literal|0
expr_stmt|;
name|fieldIdx
operator|=
literal|1
expr_stmt|;
block|}
specifier|final
name|Grid
name|formGrid
init|=
operator|new
name|Grid
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|formGrid
operator|.
name|setText
argument_list|(
literal|0
argument_list|,
name|labelIdx
argument_list|,
name|Util
operator|.
name|C
operator|.
name|defaultContext
argument_list|()
argument_list|)
expr_stmt|;
name|formGrid
operator|.
name|setWidget
argument_list|(
literal|0
argument_list|,
name|fieldIdx
argument_list|,
name|defaultContext
argument_list|)
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
name|formGrid
argument_list|)
expr_stmt|;
name|initWidget
argument_list|(
name|body
argument_list|)
expr_stmt|;
block|}
DECL|method|display (final Account account)
name|void
name|display
parameter_list|(
specifier|final
name|Account
name|account
parameter_list|)
block|{
name|setDefaultContext
argument_list|(
name|account
operator|.
name|getDefaultContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|setDefaultContext (final short lines)
specifier|private
name|void
name|setDefaultContext
parameter_list|(
specifier|final
name|short
name|lines
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|Account
operator|.
name|CONTEXT_CHOICES
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|Account
operator|.
name|CONTEXT_CHOICES
index|[
name|i
index|]
operator|==
name|lines
condition|)
block|{
name|oldDefaultContext
operator|=
name|lines
expr_stmt|;
name|defaultContext
operator|.
name|setSelectedIndex
argument_list|(
name|i
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
name|setDefaultContext
argument_list|(
name|Account
operator|.
name|DEFAULT_CONTEXT
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

