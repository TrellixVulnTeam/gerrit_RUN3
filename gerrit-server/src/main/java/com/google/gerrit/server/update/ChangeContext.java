begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2017 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.update
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|update
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
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
name|gerrit
operator|.
name|reviewdb
operator|.
name|client
operator|.
name|PatchSet
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
name|notedb
operator|.
name|ChangeNotes
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
name|notedb
operator|.
name|ChangeUpdate
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
name|project
operator|.
name|ChangeControl
import|;
end_import

begin_comment
comment|/**  * Context for performing the {@link BatchUpdate.Op#updateChange} phase.  *  *<p>A single {@code ChangeContext} corresponds to updating a single change; if a {@link  * BatchUpdate} spans multiple changes, then multiple {@code ChangeContext} instances will be  * created.  */
end_comment

begin_interface
DECL|interface|ChangeContext
specifier|public
interface|interface
name|ChangeContext
extends|extends
name|Context
block|{
comment|/**    * Get an update for this change at a given patch set.    *    *<p>A single operation can modify changes at different patch sets. Commits in the NoteDb graph    * within this update are created in patch set order.    *    *<p>To get the current patch set ID, use {@link com.google.gerrit.server.PatchSetUtil#current}.    *    * @param psId patch set ID.    * @return handle for change updates.    */
DECL|method|getUpdate (PatchSet.Id psId)
name|ChangeUpdate
name|getUpdate
parameter_list|(
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|)
function_decl|;
comment|/**    * @return control for this change. The user will be the same as {@link #getUser()}, and the    *     change data is read within the same transaction that {@code updateChange} is executing.    */
DECL|method|getControl ()
name|ChangeControl
name|getControl
parameter_list|()
function_decl|;
comment|/**    * @param bump whether to bump the value of {@link Change#getLastUpdatedOn()} field before storing    *     to ReviewDb. For NoteDb, the value is always incremented (assuming the update is not    *     otherwise a no-op).    */
DECL|method|bumpLastUpdatedOn (boolean bump)
name|void
name|bumpLastUpdatedOn
parameter_list|(
name|boolean
name|bump
parameter_list|)
function_decl|;
comment|/**    * Instruct {@link BatchUpdate} to delete this change.    *    *<p>If called, all other updates are ignored.    */
DECL|method|deleteChange ()
name|void
name|deleteChange
parameter_list|()
function_decl|;
comment|/** @return notes corresponding to {@link #getControl()}. */
DECL|method|getNotes ()
specifier|default
name|ChangeNotes
name|getNotes
parameter_list|()
block|{
return|return
name|checkNotNull
argument_list|(
name|getControl
argument_list|()
operator|.
name|getNotes
argument_list|()
argument_list|)
return|;
block|}
comment|/** @return change corresponding to {@link #getControl()}. */
DECL|method|getChange ()
specifier|default
name|Change
name|getChange
parameter_list|()
block|{
return|return
name|checkNotNull
argument_list|(
name|getControl
argument_list|()
operator|.
name|getChange
argument_list|()
argument_list|)
return|;
block|}
block|}
end_interface

end_unit

