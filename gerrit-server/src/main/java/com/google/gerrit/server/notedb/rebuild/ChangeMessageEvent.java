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
name|reviewdb
operator|.
name|client
operator|.
name|ChangeMessage
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
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_class
DECL|class|ChangeMessageEvent
class|class
name|ChangeMessageEvent
extends|extends
name|Event
block|{
DECL|field|TOPIC_SET_REGEXP
specifier|private
specifier|static
specifier|final
name|Pattern
name|TOPIC_SET_REGEXP
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^Topic set to (.+)$"
argument_list|)
decl_stmt|;
DECL|field|TOPIC_CHANGED_REGEXP
specifier|private
specifier|static
specifier|final
name|Pattern
name|TOPIC_CHANGED_REGEXP
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^Topic changed from (.+) to (.+)$"
argument_list|)
decl_stmt|;
DECL|field|TOPIC_REMOVED_REGEXP
specifier|private
specifier|static
specifier|final
name|Pattern
name|TOPIC_REMOVED_REGEXP
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^Topic (.+) removed$"
argument_list|)
decl_stmt|;
DECL|field|message
specifier|private
specifier|final
name|ChangeMessage
name|message
decl_stmt|;
DECL|field|noteDbChange
specifier|private
specifier|final
name|Change
name|noteDbChange
decl_stmt|;
DECL|method|ChangeMessageEvent (ChangeMessage message, Change noteDbChange, Timestamp changeCreatedOn)
name|ChangeMessageEvent
parameter_list|(
name|ChangeMessage
name|message
parameter_list|,
name|Change
name|noteDbChange
parameter_list|,
name|Timestamp
name|changeCreatedOn
parameter_list|)
block|{
name|super
argument_list|(
name|message
operator|.
name|getPatchSetId
argument_list|()
argument_list|,
name|message
operator|.
name|getAuthor
argument_list|()
argument_list|,
name|message
operator|.
name|getWrittenOn
argument_list|()
argument_list|,
name|changeCreatedOn
argument_list|,
name|message
operator|.
name|getTag
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|message
operator|=
name|message
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
name|checkUpdate
argument_list|(
name|update
argument_list|)
expr_stmt|;
name|update
operator|.
name|setChangeMessage
argument_list|(
name|message
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|setTopic
argument_list|(
name|update
argument_list|)
expr_stmt|;
block|}
DECL|method|setTopic (ChangeUpdate update)
specifier|private
name|void
name|setTopic
parameter_list|(
name|ChangeUpdate
name|update
parameter_list|)
block|{
name|String
name|msg
init|=
name|message
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|msg
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|Matcher
name|m
init|=
name|TOPIC_SET_REGEXP
operator|.
name|matcher
argument_list|(
name|msg
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|matches
argument_list|()
condition|)
block|{
name|String
name|topic
init|=
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|update
operator|.
name|setTopic
argument_list|(
name|topic
argument_list|)
expr_stmt|;
name|noteDbChange
operator|.
name|setTopic
argument_list|(
name|topic
argument_list|)
expr_stmt|;
return|return;
block|}
name|m
operator|=
name|TOPIC_CHANGED_REGEXP
operator|.
name|matcher
argument_list|(
name|msg
argument_list|)
expr_stmt|;
if|if
condition|(
name|m
operator|.
name|matches
argument_list|()
condition|)
block|{
name|String
name|topic
init|=
name|m
operator|.
name|group
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|update
operator|.
name|setTopic
argument_list|(
name|topic
argument_list|)
expr_stmt|;
name|noteDbChange
operator|.
name|setTopic
argument_list|(
name|topic
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|TOPIC_REMOVED_REGEXP
operator|.
name|matcher
argument_list|(
name|msg
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
name|update
operator|.
name|setTopic
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|noteDbChange
operator|.
name|setTopic
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

