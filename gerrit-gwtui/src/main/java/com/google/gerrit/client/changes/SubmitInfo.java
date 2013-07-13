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
name|reviewdb
operator|.
name|client
operator|.
name|Change
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
name|JavaScriptObject
import|;
end_import

begin_class
DECL|class|SubmitInfo
specifier|public
class|class
name|SubmitInfo
extends|extends
name|JavaScriptObject
block|{
DECL|method|status ()
specifier|final
name|Change
operator|.
name|Status
name|status
parameter_list|()
block|{
return|return
name|Change
operator|.
name|Status
operator|.
name|valueOf
argument_list|(
name|statusRaw
argument_list|()
argument_list|)
return|;
block|}
DECL|method|statusRaw ()
specifier|private
specifier|final
specifier|native
name|String
name|statusRaw
parameter_list|()
comment|/*-{ return this.status; }-*/
function_decl|;
DECL|method|SubmitInfo ()
specifier|protected
name|SubmitInfo
parameter_list|()
block|{   }
block|}
end_class

end_unit

