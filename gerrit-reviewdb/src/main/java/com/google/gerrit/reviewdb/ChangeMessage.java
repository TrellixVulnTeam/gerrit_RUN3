begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2008 The Android Open Source Project
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
DECL|package|com.google.gerrit.reviewdb
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|reviewdb
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|client
operator|.
name|Column
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
name|client
operator|.
name|StringKey
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

begin_comment
comment|/** A message attached to a {@link Change}. */
end_comment

begin_class
DECL|class|ChangeMessage
specifier|public
specifier|final
class|class
name|ChangeMessage
block|{
DECL|class|Key
specifier|public
specifier|static
class|class
name|Key
extends|extends
name|StringKey
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|1
argument_list|)
DECL|field|changeId
specifier|protected
name|Change
operator|.
name|Id
name|changeId
decl_stmt|;
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|2
argument_list|,
name|length
operator|=
literal|40
argument_list|)
DECL|field|uuid
specifier|protected
name|String
name|uuid
decl_stmt|;
DECL|method|Key ()
specifier|protected
name|Key
parameter_list|()
block|{
name|changeId
operator|=
operator|new
name|Change
operator|.
name|Id
argument_list|()
expr_stmt|;
block|}
DECL|method|Key (final Change.Id change, final String uuid)
specifier|public
name|Key
parameter_list|(
specifier|final
name|Change
operator|.
name|Id
name|change
parameter_list|,
specifier|final
name|String
name|uuid
parameter_list|)
block|{
name|this
operator|.
name|changeId
operator|=
name|change
expr_stmt|;
name|this
operator|.
name|uuid
operator|=
name|uuid
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getParentKey ()
specifier|public
name|Change
operator|.
name|Id
name|getParentKey
parameter_list|()
block|{
return|return
name|changeId
return|;
block|}
annotation|@
name|Override
DECL|method|get ()
specifier|public
name|String
name|get
parameter_list|()
block|{
return|return
name|uuid
return|;
block|}
annotation|@
name|Override
DECL|method|set (String newValue)
specifier|protected
name|void
name|set
parameter_list|(
name|String
name|newValue
parameter_list|)
block|{
name|uuid
operator|=
name|newValue
expr_stmt|;
block|}
block|}
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|1
argument_list|,
name|name
operator|=
name|Column
operator|.
name|NONE
argument_list|)
DECL|field|key
specifier|protected
name|Key
name|key
decl_stmt|;
comment|/** Who wrote this comment; null if it was written by the Gerrit system. */
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|2
argument_list|,
name|name
operator|=
literal|"author_id"
argument_list|,
name|notNull
operator|=
literal|false
argument_list|)
DECL|field|author
specifier|protected
name|Account
operator|.
name|Id
name|author
decl_stmt|;
comment|/** When this comment was drafted. */
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|3
argument_list|)
DECL|field|writtenOn
specifier|protected
name|Timestamp
name|writtenOn
decl_stmt|;
comment|/** The text left by the user. */
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|4
argument_list|,
name|notNull
operator|=
literal|false
argument_list|,
name|length
operator|=
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
DECL|field|message
specifier|protected
name|String
name|message
decl_stmt|;
comment|/** Which patchset (if any) was this message generated from? */
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|5
argument_list|,
name|notNull
operator|=
literal|false
argument_list|)
DECL|field|patchset
specifier|protected
name|PatchSet
operator|.
name|Id
name|patchset
decl_stmt|;
DECL|method|ChangeMessage ()
specifier|protected
name|ChangeMessage
parameter_list|()
block|{   }
DECL|method|ChangeMessage (final ChangeMessage.Key k, final Account.Id a)
specifier|public
name|ChangeMessage
parameter_list|(
specifier|final
name|ChangeMessage
operator|.
name|Key
name|k
parameter_list|,
specifier|final
name|Account
operator|.
name|Id
name|a
parameter_list|)
block|{
name|this
argument_list|(
name|k
argument_list|,
name|a
argument_list|,
operator|new
name|Timestamp
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|ChangeMessage (final ChangeMessage.Key k, final Account.Id a, final Timestamp wo)
specifier|public
name|ChangeMessage
parameter_list|(
specifier|final
name|ChangeMessage
operator|.
name|Key
name|k
parameter_list|,
specifier|final
name|Account
operator|.
name|Id
name|a
parameter_list|,
specifier|final
name|Timestamp
name|wo
parameter_list|)
block|{
name|key
operator|=
name|k
expr_stmt|;
name|author
operator|=
name|a
expr_stmt|;
name|writtenOn
operator|=
name|wo
expr_stmt|;
block|}
DECL|method|getKey ()
specifier|public
name|ChangeMessage
operator|.
name|Key
name|getKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
comment|/** If null, the message was written 'by the Gerrit system'. */
DECL|method|getAuthor ()
specifier|public
name|Account
operator|.
name|Id
name|getAuthor
parameter_list|()
block|{
return|return
name|author
return|;
block|}
DECL|method|setAuthor (final Account.Id accountId)
specifier|public
name|void
name|setAuthor
parameter_list|(
specifier|final
name|Account
operator|.
name|Id
name|accountId
parameter_list|)
block|{
if|if
condition|(
name|author
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cannot modify author once assigned"
argument_list|)
throw|;
block|}
name|author
operator|=
name|accountId
expr_stmt|;
block|}
DECL|method|getWrittenOn ()
specifier|public
name|Timestamp
name|getWrittenOn
parameter_list|()
block|{
return|return
name|writtenOn
return|;
block|}
DECL|method|getMessage ()
specifier|public
name|String
name|getMessage
parameter_list|()
block|{
return|return
name|message
return|;
block|}
DECL|method|setMessage (final String s)
specifier|public
name|void
name|setMessage
parameter_list|(
specifier|final
name|String
name|s
parameter_list|)
block|{
name|message
operator|=
name|s
expr_stmt|;
block|}
DECL|method|getPatchSetId ()
specifier|public
name|PatchSet
operator|.
name|Id
name|getPatchSetId
parameter_list|()
block|{
return|return
name|patchset
return|;
block|}
DECL|method|setPatchSetId (PatchSet.Id id)
specifier|public
name|void
name|setPatchSetId
parameter_list|(
name|PatchSet
operator|.
name|Id
name|id
parameter_list|)
block|{
name|patchset
operator|=
name|id
expr_stmt|;
block|}
block|}
end_class

end_unit

