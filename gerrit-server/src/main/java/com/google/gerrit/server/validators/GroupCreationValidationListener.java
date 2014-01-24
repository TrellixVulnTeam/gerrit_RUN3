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
DECL|package|com.google.gerrit.server.validators
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|validators
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
name|CreateGroupArgs
import|;
end_import

begin_comment
comment|/**  * Listener to provide validation on group creation.  */
end_comment

begin_interface
annotation|@
name|ExtensionPoint
DECL|interface|GroupCreationValidationListener
specifier|public
interface|interface
name|GroupCreationValidationListener
block|{
comment|/**    * Group creation validation.    *    * Invoked by Gerrit just before a new group is going to be created.    *    * @param args arguments for the group creation    * @throws ValidationException if validation fails    */
DECL|method|validateNewGroup (CreateGroupArgs args)
specifier|public
name|void
name|validateNewGroup
parameter_list|(
name|CreateGroupArgs
name|args
parameter_list|)
throws|throws
name|ValidationException
function_decl|;
block|}
end_interface

end_unit

