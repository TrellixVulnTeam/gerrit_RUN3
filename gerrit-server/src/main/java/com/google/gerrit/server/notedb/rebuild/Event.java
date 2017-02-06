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
name|checkState
import|;
end_import

begin_import
import|import static
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
operator|.
name|ChangeRebuilderImpl
operator|.
name|MAX_WINDOW_MS
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|MoreObjects
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|MoreObjects
operator|.
name|ToStringHelper
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ComparisonChain
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
name|reviewdb
operator|.
name|server
operator|.
name|ReviewDbUtil
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
name|AbstractChangeUpdate
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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Timestamp
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
DECL|class|Event
specifier|abstract
class|class
name|Event
implements|implements
name|Comparable
argument_list|<
name|Event
argument_list|>
block|{
comment|// NOTE: EventList only supports direct subclasses, not an arbitrary
comment|// hierarchy.
DECL|field|user
specifier|final
name|Account
operator|.
name|Id
name|user
decl_stmt|;
DECL|field|realUser
specifier|final
name|Account
operator|.
name|Id
name|realUser
decl_stmt|;
DECL|field|tag
specifier|final
name|String
name|tag
decl_stmt|;
DECL|field|predatesChange
specifier|final
name|boolean
name|predatesChange
decl_stmt|;
comment|/** Dependencies of this event; other events that must happen before this one. */
DECL|field|deps
specifier|final
name|List
argument_list|<
name|Event
argument_list|>
name|deps
decl_stmt|;
DECL|field|when
name|Timestamp
name|when
decl_stmt|;
DECL|field|psId
name|PatchSet
operator|.
name|Id
name|psId
decl_stmt|;
DECL|method|Event ( PatchSet.Id psId, Account.Id effectiveUser, Account.Id realUser, Timestamp when, Timestamp changeCreatedOn, String tag)
specifier|protected
name|Event
parameter_list|(
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|,
name|Account
operator|.
name|Id
name|effectiveUser
parameter_list|,
name|Account
operator|.
name|Id
name|realUser
parameter_list|,
name|Timestamp
name|when
parameter_list|,
name|Timestamp
name|changeCreatedOn
parameter_list|,
name|String
name|tag
parameter_list|)
block|{
name|this
operator|.
name|psId
operator|=
name|psId
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|effectiveUser
expr_stmt|;
name|this
operator|.
name|realUser
operator|=
name|realUser
operator|!=
literal|null
condition|?
name|realUser
else|:
name|effectiveUser
expr_stmt|;
name|this
operator|.
name|tag
operator|=
name|tag
expr_stmt|;
comment|// Truncate timestamps at the change's createdOn timestamp.
name|predatesChange
operator|=
name|when
operator|.
name|before
argument_list|(
name|changeCreatedOn
argument_list|)
expr_stmt|;
name|this
operator|.
name|when
operator|=
name|predatesChange
condition|?
name|changeCreatedOn
else|:
name|when
expr_stmt|;
name|deps
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
DECL|method|checkUpdate (AbstractChangeUpdate update)
specifier|protected
name|void
name|checkUpdate
parameter_list|(
name|AbstractChangeUpdate
name|update
parameter_list|)
block|{
name|checkState
argument_list|(
name|Objects
operator|.
name|equals
argument_list|(
name|update
operator|.
name|getPatchSetId
argument_list|()
argument_list|,
name|psId
argument_list|)
argument_list|,
literal|"cannot apply event for %s to update for %s"
argument_list|,
name|update
operator|.
name|getPatchSetId
argument_list|()
argument_list|,
name|psId
argument_list|)
expr_stmt|;
name|checkState
argument_list|(
name|when
operator|.
name|getTime
argument_list|()
operator|-
name|update
operator|.
name|getWhen
argument_list|()
operator|.
name|getTime
argument_list|()
operator|<=
name|MAX_WINDOW_MS
argument_list|,
literal|"event at %s outside update window starting at %s"
argument_list|,
name|when
argument_list|,
name|update
operator|.
name|getWhen
argument_list|()
argument_list|)
expr_stmt|;
name|checkState
argument_list|(
name|Objects
operator|.
name|equals
argument_list|(
name|update
operator|.
name|getNullableAccountId
argument_list|()
argument_list|,
name|user
argument_list|)
argument_list|,
literal|"cannot apply event by %s to update by %s"
argument_list|,
name|user
argument_list|,
name|update
operator|.
name|getNullableAccountId
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|addDep (Event e)
name|Event
name|addDep
parameter_list|(
name|Event
name|e
parameter_list|)
block|{
name|deps
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * @return whether this event type must be unique per {@link ChangeUpdate}, i.e. there may be at    *     most one of this type.    */
DECL|method|uniquePerUpdate ()
specifier|abstract
name|boolean
name|uniquePerUpdate
parameter_list|()
function_decl|;
DECL|method|apply (ChangeUpdate update)
specifier|abstract
name|void
name|apply
parameter_list|(
name|ChangeUpdate
name|update
parameter_list|)
throws|throws
name|OrmException
throws|,
name|IOException
function_decl|;
DECL|method|isPostSubmitApproval ()
specifier|protected
name|boolean
name|isPostSubmitApproval
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|isSubmit ()
specifier|protected
name|boolean
name|isSubmit
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|ToStringHelper
name|helper
init|=
name|MoreObjects
operator|.
name|toStringHelper
argument_list|(
name|this
argument_list|)
operator|.
name|add
argument_list|(
literal|"psId"
argument_list|,
name|psId
argument_list|)
operator|.
name|add
argument_list|(
literal|"effectiveUser"
argument_list|,
name|user
argument_list|)
operator|.
name|add
argument_list|(
literal|"realUser"
argument_list|,
name|realUser
argument_list|)
operator|.
name|add
argument_list|(
literal|"when"
argument_list|,
name|when
argument_list|)
operator|.
name|add
argument_list|(
literal|"tag"
argument_list|,
name|tag
argument_list|)
decl_stmt|;
name|addToString
argument_list|(
name|helper
argument_list|)
expr_stmt|;
return|return
name|helper
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** @param helper toString helper to add fields to */
DECL|method|addToString (ToStringHelper helper)
specifier|protected
name|void
name|addToString
parameter_list|(
name|ToStringHelper
name|helper
parameter_list|)
block|{}
annotation|@
name|Override
DECL|method|compareTo (Event other)
specifier|public
name|int
name|compareTo
parameter_list|(
name|Event
name|other
parameter_list|)
block|{
return|return
name|ComparisonChain
operator|.
name|start
argument_list|()
operator|.
name|compareFalseFirst
argument_list|(
name|this
operator|.
name|isFinalUpdates
argument_list|()
argument_list|,
name|other
operator|.
name|isFinalUpdates
argument_list|()
argument_list|)
operator|.
name|compare
argument_list|(
name|this
operator|.
name|when
argument_list|,
name|other
operator|.
name|when
argument_list|)
operator|.
name|compareTrueFirst
argument_list|(
name|isPatchSet
argument_list|()
argument_list|,
name|isPatchSet
argument_list|()
argument_list|)
operator|.
name|compareTrueFirst
argument_list|(
name|this
operator|.
name|predatesChange
argument_list|,
name|other
operator|.
name|predatesChange
argument_list|)
operator|.
name|compare
argument_list|(
name|this
operator|.
name|user
argument_list|,
name|other
operator|.
name|user
argument_list|,
name|ReviewDbUtil
operator|.
name|intKeyOrdering
argument_list|()
argument_list|)
operator|.
name|compare
argument_list|(
name|this
operator|.
name|realUser
argument_list|,
name|other
operator|.
name|realUser
argument_list|,
name|ReviewDbUtil
operator|.
name|intKeyOrdering
argument_list|()
argument_list|)
operator|.
name|compare
argument_list|(
name|this
operator|.
name|psId
argument_list|,
name|other
operator|.
name|psId
argument_list|,
name|ReviewDbUtil
operator|.
name|intKeyOrdering
argument_list|()
operator|.
name|nullsLast
argument_list|()
argument_list|)
operator|.
name|result
argument_list|()
return|;
block|}
DECL|method|isPatchSet ()
specifier|private
name|boolean
name|isPatchSet
parameter_list|()
block|{
return|return
name|this
operator|instanceof
name|PatchSetEvent
return|;
block|}
DECL|method|isFinalUpdates ()
specifier|private
name|boolean
name|isFinalUpdates
parameter_list|()
block|{
return|return
name|this
operator|instanceof
name|FinalUpdatesEvent
return|;
block|}
block|}
end_class

end_unit

