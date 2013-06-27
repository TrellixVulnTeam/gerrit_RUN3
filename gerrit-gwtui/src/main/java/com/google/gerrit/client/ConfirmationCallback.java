begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2010 The Android Open Source Project
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

begin_comment
comment|/**  * Interface that a caller must implement to react on the result of a  * {@link ConfirmationDialog}.  */
end_comment

begin_class
DECL|class|ConfirmationCallback
specifier|public
specifier|abstract
class|class
name|ConfirmationCallback
block|{
comment|/**    * Called when the {@link ConfirmationDialog} is finished with OK.    * To be overwritten by subclasses.    */
DECL|method|onOk ()
specifier|public
specifier|abstract
name|void
name|onOk
parameter_list|()
function_decl|;
comment|/**    * Called when the {@link ConfirmationDialog} is finished with Cancel.    * To be overwritten by subclasses.    */
DECL|method|onCancel ()
specifier|public
name|void
name|onCancel
parameter_list|()
block|{   }
block|}
end_class

end_unit

