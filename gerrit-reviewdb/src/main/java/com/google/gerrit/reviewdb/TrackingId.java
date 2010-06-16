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
name|CompoundKey
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

begin_comment
comment|/** External tracking id associated with a {@link Change} */
end_comment

begin_class
DECL|class|TrackingId
specifier|public
specifier|final
class|class
name|TrackingId
block|{
DECL|field|TRACKING_ID_MAX_CHAR
specifier|public
specifier|static
specifier|final
name|int
name|TRACKING_ID_MAX_CHAR
init|=
literal|20
decl_stmt|;
DECL|field|TRACKING_SYSTEM_MAX_CHAR
specifier|public
specifier|static
specifier|final
name|int
name|TRACKING_SYSTEM_MAX_CHAR
init|=
literal|10
decl_stmt|;
comment|/** External tracking id */
DECL|class|Id
specifier|public
specifier|static
class|class
name|Id
extends|extends
name|StringKey
argument_list|<
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|client
operator|.
name|Key
argument_list|<
name|?
argument_list|>
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
argument_list|,
name|length
operator|=
name|TrackingId
operator|.
name|TRACKING_ID_MAX_CHAR
argument_list|)
DECL|field|id
specifier|protected
name|String
name|id
decl_stmt|;
DECL|method|Id ()
specifier|protected
name|Id
parameter_list|()
block|{     }
DECL|method|Id (final String id)
specifier|public
name|Id
parameter_list|(
specifier|final
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
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
name|id
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
name|id
operator|=
name|newValue
expr_stmt|;
block|}
block|}
comment|/** Name of external tracking system */
DECL|class|System
specifier|public
specifier|static
class|class
name|System
extends|extends
name|StringKey
argument_list|<
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|client
operator|.
name|Key
argument_list|<
name|?
argument_list|>
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
argument_list|,
name|length
operator|=
name|TrackingId
operator|.
name|TRACKING_SYSTEM_MAX_CHAR
argument_list|)
DECL|field|system
specifier|protected
name|String
name|system
decl_stmt|;
DECL|method|System ()
specifier|protected
name|System
parameter_list|()
block|{     }
DECL|method|System (final String s)
specifier|public
name|System
parameter_list|(
specifier|final
name|String
name|s
parameter_list|)
block|{
name|this
operator|.
name|system
operator|=
name|s
expr_stmt|;
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
name|system
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
name|system
operator|=
name|newValue
expr_stmt|;
block|}
block|}
DECL|class|Key
specifier|public
specifier|static
class|class
name|Key
extends|extends
name|CompoundKey
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
argument_list|)
DECL|field|trackingId
specifier|protected
name|Id
name|trackingId
decl_stmt|;
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|3
argument_list|)
DECL|field|trackingSystem
specifier|protected
name|System
name|trackingSystem
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
name|trackingId
operator|=
operator|new
name|Id
argument_list|()
expr_stmt|;
name|trackingSystem
operator|=
operator|new
name|System
argument_list|()
expr_stmt|;
block|}
DECL|method|Key (final Change.Id ch, final Id id, final System s)
specifier|protected
name|Key
parameter_list|(
specifier|final
name|Change
operator|.
name|Id
name|ch
parameter_list|,
specifier|final
name|Id
name|id
parameter_list|,
specifier|final
name|System
name|s
parameter_list|)
block|{
name|changeId
operator|=
name|ch
expr_stmt|;
name|trackingId
operator|=
name|id
expr_stmt|;
name|trackingSystem
operator|=
name|s
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
DECL|method|members ()
specifier|public
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|client
operator|.
name|Key
argument_list|<
name|?
argument_list|>
index|[]
name|members
parameter_list|()
block|{
return|return
operator|new
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|client
operator|.
name|Key
argument_list|<
name|?
argument_list|>
index|[]
block|{
name|trackingId
operator|,
name|trackingSystem
block|}
empty_stmt|;
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
DECL|method|TrackingId ()
specifier|protected
name|TrackingId
parameter_list|()
block|{   }
DECL|method|TrackingId (final Change.Id ch, final TrackingId.Id id, final TrackingId.System s)
specifier|public
name|TrackingId
parameter_list|(
specifier|final
name|Change
operator|.
name|Id
name|ch
parameter_list|,
specifier|final
name|TrackingId
operator|.
name|Id
name|id
parameter_list|,
specifier|final
name|TrackingId
operator|.
name|System
name|s
parameter_list|)
block|{
name|key
operator|=
operator|new
name|Key
argument_list|(
name|ch
argument_list|,
name|id
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
DECL|method|TrackingId (final Change.Id ch, final String id, final String s)
specifier|public
name|TrackingId
parameter_list|(
specifier|final
name|Change
operator|.
name|Id
name|ch
parameter_list|,
specifier|final
name|String
name|id
parameter_list|,
specifier|final
name|String
name|s
parameter_list|)
block|{
name|key
operator|=
operator|new
name|Key
argument_list|(
name|ch
argument_list|,
operator|new
name|TrackingId
operator|.
name|Id
argument_list|(
name|id
argument_list|)
argument_list|,
operator|new
name|TrackingId
operator|.
name|System
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getChangeId ()
specifier|public
name|Change
operator|.
name|Id
name|getChangeId
parameter_list|()
block|{
return|return
name|key
operator|.
name|changeId
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|key
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals (final Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
specifier|final
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|TrackingId
condition|)
block|{
specifier|final
name|TrackingId
name|tr
init|=
operator|(
name|TrackingId
operator|)
name|obj
decl_stmt|;
return|return
name|tr
operator|.
name|key
operator|.
name|equals
argument_list|(
name|tr
operator|.
name|key
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

