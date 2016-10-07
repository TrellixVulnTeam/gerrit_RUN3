begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2016 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.notedb.rebuild
package|package
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
name|rebuild
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
name|gwtorm
operator|.
name|server
operator|.
name|OrmException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_class
DECL|class|FinalUpdatesEvent
class|class
name|FinalUpdatesEvent
extends|extends
name|Event
block|{
DECL|field|change
specifier|private
specifier|final
name|Change
name|change
decl_stmt|;
DECL|field|noteDbChange
specifier|private
specifier|final
name|Change
name|noteDbChange
decl_stmt|;
DECL|method|FinalUpdatesEvent (Change change, Change noteDbChange)
name|FinalUpdatesEvent
parameter_list|(
name|Change
name|change
parameter_list|,
name|Change
name|noteDbChange
parameter_list|)
block|{
name|super
argument_list|(
name|change
operator|.
name|currentPatchSetId
argument_list|()
argument_list|,
name|change
operator|.
name|getOwner
argument_list|()
argument_list|,
name|change
operator|.
name|getLastUpdatedOn
argument_list|()
argument_list|,
name|change
operator|.
name|getCreatedOn
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|change
operator|=
name|change
expr_stmt|;
name|this
operator|.
name|noteDbChange
operator|=
name|noteDbChange
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|uniquePerUpdate ()
name|boolean
name|uniquePerUpdate
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|Override
DECL|method|apply (ChangeUpdate update)
name|void
name|apply
parameter_list|(
name|ChangeUpdate
name|update
parameter_list|)
throws|throws
name|OrmException
block|{
if|if
condition|(
operator|!
name|Objects
operator|.
name|equals
argument_list|(
name|change
operator|.
name|getTopic
argument_list|()
argument_list|,
name|noteDbChange
operator|.
name|getTopic
argument_list|()
argument_list|)
condition|)
block|{
name|update
operator|.
name|setTopic
argument_list|(
name|change
operator|.
name|getTopic
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|Objects
operator|.
name|equals
argument_list|(
name|change
operator|.
name|getStatus
argument_list|()
argument_list|,
name|noteDbChange
operator|.
name|getStatus
argument_list|()
argument_list|)
condition|)
block|{
comment|// TODO(dborowitz): Stamp approximate approvals at this time.
name|update
operator|.
name|fixStatus
argument_list|(
name|change
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|change
operator|.
name|getSubmissionId
argument_list|()
operator|!=
literal|null
operator|&&
name|noteDbChange
operator|.
name|getSubmissionId
argument_list|()
operator|==
literal|null
condition|)
block|{
name|update
operator|.
name|setSubmissionId
argument_list|(
name|change
operator|.
name|getSubmissionId
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|Objects
operator|.
name|equals
argument_list|(
name|change
operator|.
name|getAssignee
argument_list|()
argument_list|,
name|noteDbChange
operator|.
name|getAssignee
argument_list|()
argument_list|)
condition|)
block|{
comment|// TODO(dborowitz): Parse intermediate values out from messages.
name|update
operator|.
name|setAssignee
argument_list|(
name|change
operator|.
name|getAssignee
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|update
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|update
operator|.
name|setSubjectForCommit
argument_list|(
literal|"Final NoteDb migration updates"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

