begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2014 The Android Open Source Project
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
DECL|package|com.google.gerrit.common
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|common
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
name|server
operator|.
name|CurrentUser
import|;
end_import

begin_comment
comment|/** Distributes Events to ChangeListeners.  Register listeners here. */
end_comment

begin_interface
DECL|interface|EventSource
specifier|public
interface|interface
name|EventSource
block|{
DECL|method|addEventListener (EventListener listener, CurrentUser user)
name|void
name|addEventListener
parameter_list|(
name|EventListener
name|listener
parameter_list|,
name|CurrentUser
name|user
parameter_list|)
function_decl|;
DECL|method|removeEventListener (EventListener listener)
name|void
name|removeEventListener
parameter_list|(
name|EventListener
name|listener
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

