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
DECL|package|com.google.gerrit.client
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
package|;
end_package

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
name|InlineLabel
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
name|Label
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
name|RootPanel
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
name|event
operator|.
name|RpcCompleteEvent
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
name|event
operator|.
name|RpcCompleteHandler
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
name|event
operator|.
name|RpcStartEvent
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
name|event
operator|.
name|RpcStartHandler
import|;
end_import

begin_class
DECL|class|RpcStatus
specifier|public
class|class
name|RpcStatus
implements|implements
name|RpcStartHandler
implements|,
name|RpcCompleteHandler
block|{
DECL|field|INSTANCE
specifier|public
specifier|static
name|RpcStatus
name|INSTANCE
decl_stmt|;
DECL|field|hideDepth
specifier|private
specifier|static
name|int
name|hideDepth
decl_stmt|;
comment|/** Execute code, hiding the RPCs they execute from being shown visually. */
DECL|method|hide (Runnable run)
specifier|public
specifier|static
name|void
name|hide
parameter_list|(
name|Runnable
name|run
parameter_list|)
block|{
try|try
block|{
name|hideDepth
operator|++
expr_stmt|;
name|run
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|hideDepth
operator|--
expr_stmt|;
block|}
block|}
DECL|field|loading
specifier|private
specifier|final
name|Label
name|loading
decl_stmt|;
DECL|field|activeCalls
specifier|private
name|int
name|activeCalls
decl_stmt|;
DECL|method|RpcStatus ()
name|RpcStatus
parameter_list|()
block|{
name|loading
operator|=
operator|new
name|InlineLabel
argument_list|()
expr_stmt|;
name|loading
operator|.
name|setText
argument_list|(
name|Gerrit
operator|.
name|C
operator|.
name|rpcStatusWorking
argument_list|()
argument_list|)
expr_stmt|;
name|loading
operator|.
name|setStyleName
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|rpcStatus
argument_list|()
argument_list|)
expr_stmt|;
name|loading
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|RootPanel
operator|.
name|get
argument_list|()
operator|.
name|add
argument_list|(
name|loading
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onRpcStart (RpcStartEvent event)
specifier|public
name|void
name|onRpcStart
parameter_list|(
name|RpcStartEvent
name|event
parameter_list|)
block|{
name|onRpcStart
argument_list|()
expr_stmt|;
block|}
DECL|method|onRpcStart ()
specifier|public
name|void
name|onRpcStart
parameter_list|()
block|{
if|if
condition|(
operator|++
name|activeCalls
operator|==
literal|1
condition|)
block|{
if|if
condition|(
name|hideDepth
operator|==
literal|0
condition|)
block|{
name|loading
operator|.
name|setVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|onRpcComplete (RpcCompleteEvent event)
specifier|public
name|void
name|onRpcComplete
parameter_list|(
name|RpcCompleteEvent
name|event
parameter_list|)
block|{
name|onRpcComplete
argument_list|()
expr_stmt|;
block|}
DECL|method|onRpcComplete ()
specifier|public
name|void
name|onRpcComplete
parameter_list|()
block|{
if|if
condition|(
operator|--
name|activeCalls
operator|==
literal|0
condition|)
block|{
name|loading
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

