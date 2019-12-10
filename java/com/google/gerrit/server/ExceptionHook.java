begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2019 The Android Open Source Project
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
DECL|package|com.google.gerrit.server
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
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
name|annotations
operator|.
name|ExtensionPoint
import|;
end_import

begin_comment
comment|/**  * Allows implementors to control how certain exceptions should be handled.  *  *<p>This interface is intended to be implemented for cluster setups with multiple primary nodes to  * control the behavior for handling exceptions that are thrown by a lower layer that handles the  * consensus and synchronization between different server nodes. E.g. if an operation fails because  * consensus for a Git update could not be achieved (e.g. due to slow responding server nodes) this  * interface can be used to retry the request instead of failing it immediately.  */
end_comment

begin_interface
annotation|@
name|ExtensionPoint
DECL|interface|ExceptionHook
specifier|public
interface|interface
name|ExceptionHook
block|{
comment|/**    * Whether an operation should be retried if it failed with the given throwable.    *    *<p>Only affects operations that are executed with {@link    * com.google.gerrit.server.update.RetryHelper}.    *    * @param throwable throwable that was thrown while executing the operation    * @return whether the operation should be retried    */
DECL|method|shouldRetry (Throwable throwable)
specifier|default
name|boolean
name|shouldRetry
parameter_list|(
name|Throwable
name|throwable
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
end_interface

end_unit

