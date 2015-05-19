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
DECL|package|com.google.gerrit.extensions.events
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|extensions
operator|.
name|events
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
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  * Notified whenever the garbage collector has run successfully on a project.  */
end_comment

begin_interface
annotation|@
name|ExtensionPoint
DECL|interface|GarbageCollectorListener
specifier|public
interface|interface
name|GarbageCollectorListener
block|{
DECL|interface|Event
specifier|public
interface|interface
name|Event
block|{
comment|/** @return The name of the project that has been garbage collected. */
DECL|method|getProjectName ()
name|String
name|getProjectName
parameter_list|()
function_decl|;
comment|/**      * Properties describing the result of the garbage collection performed by      * JGit      *      * @see org.eclipse.jgit.api.GarbageCollectCommand#call()      */
DECL|method|getStatistics ()
name|Properties
name|getStatistics
parameter_list|()
function_decl|;
block|}
DECL|method|onGarbageCollected (Event event)
name|void
name|onGarbageCollected
parameter_list|(
name|Event
name|event
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

