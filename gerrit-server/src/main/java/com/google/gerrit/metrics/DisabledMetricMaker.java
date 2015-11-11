begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2015 The Android Open Source Project
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
DECL|package|com.google.gerrit.metrics
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|metrics
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
name|registration
operator|.
name|RegistrationHandle
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_comment
comment|/** Exports no metrics, useful for running batch programs. */
end_comment

begin_class
DECL|class|DisabledMetricMaker
specifier|public
class|class
name|DisabledMetricMaker
extends|extends
name|MetricMaker
block|{
annotation|@
name|Override
DECL|method|newCounter (String name, Description desc)
specifier|public
name|Counter0
name|newCounter
parameter_list|(
name|String
name|name
parameter_list|,
name|Description
name|desc
parameter_list|)
block|{
return|return
operator|new
name|Counter0
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|incrementBy
parameter_list|(
name|long
name|value
parameter_list|)
block|{}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|newCounter (String name, Description desc, Field<F1> field1)
specifier|public
parameter_list|<
name|F1
parameter_list|>
name|Counter1
argument_list|<
name|F1
argument_list|>
name|newCounter
parameter_list|(
name|String
name|name
parameter_list|,
name|Description
name|desc
parameter_list|,
name|Field
argument_list|<
name|F1
argument_list|>
name|field1
parameter_list|)
block|{
return|return
operator|new
name|Counter1
argument_list|<
name|F1
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|incrementBy
parameter_list|(
name|F1
name|field1
parameter_list|,
name|long
name|value
parameter_list|)
block|{}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|newCounter (String name, Description desc, Field<F1> field1, Field<F2> field2)
specifier|public
parameter_list|<
name|F1
parameter_list|,
name|F2
parameter_list|>
name|Counter2
argument_list|<
name|F1
argument_list|,
name|F2
argument_list|>
name|newCounter
parameter_list|(
name|String
name|name
parameter_list|,
name|Description
name|desc
parameter_list|,
name|Field
argument_list|<
name|F1
argument_list|>
name|field1
parameter_list|,
name|Field
argument_list|<
name|F2
argument_list|>
name|field2
parameter_list|)
block|{
return|return
operator|new
name|Counter2
argument_list|<
name|F1
argument_list|,
name|F2
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|incrementBy
parameter_list|(
name|F1
name|field1
parameter_list|,
name|F2
name|field2
parameter_list|,
name|long
name|value
parameter_list|)
block|{}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|newCounter (String name, Description desc, Field<F1> field1, Field<F2> field2, Field<F3> field3)
specifier|public
parameter_list|<
name|F1
parameter_list|,
name|F2
parameter_list|,
name|F3
parameter_list|>
name|Counter3
argument_list|<
name|F1
argument_list|,
name|F2
argument_list|,
name|F3
argument_list|>
name|newCounter
parameter_list|(
name|String
name|name
parameter_list|,
name|Description
name|desc
parameter_list|,
name|Field
argument_list|<
name|F1
argument_list|>
name|field1
parameter_list|,
name|Field
argument_list|<
name|F2
argument_list|>
name|field2
parameter_list|,
name|Field
argument_list|<
name|F3
argument_list|>
name|field3
parameter_list|)
block|{
return|return
operator|new
name|Counter3
argument_list|<
name|F1
argument_list|,
name|F2
argument_list|,
name|F3
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|incrementBy
parameter_list|(
name|F1
name|field1
parameter_list|,
name|F2
name|field2
parameter_list|,
name|F3
name|field3
parameter_list|,
name|long
name|value
parameter_list|)
block|{}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|newTimer (String name, Description desc)
specifier|public
name|Timer0
name|newTimer
parameter_list|(
name|String
name|name
parameter_list|,
name|Description
name|desc
parameter_list|)
block|{
return|return
operator|new
name|Timer0
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|record
parameter_list|(
name|long
name|value
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
block|{}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|newTimer (String name, Description desc, Field<F1> field1)
specifier|public
parameter_list|<
name|F1
parameter_list|>
name|Timer1
argument_list|<
name|F1
argument_list|>
name|newTimer
parameter_list|(
name|String
name|name
parameter_list|,
name|Description
name|desc
parameter_list|,
name|Field
argument_list|<
name|F1
argument_list|>
name|field1
parameter_list|)
block|{
return|return
operator|new
name|Timer1
argument_list|<
name|F1
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|record
parameter_list|(
name|F1
name|field1
parameter_list|,
name|long
name|value
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
block|{}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|newTimer (String name, Description desc, Field<F1> field1, Field<F2> field2)
specifier|public
parameter_list|<
name|F1
parameter_list|,
name|F2
parameter_list|>
name|Timer2
argument_list|<
name|F1
argument_list|,
name|F2
argument_list|>
name|newTimer
parameter_list|(
name|String
name|name
parameter_list|,
name|Description
name|desc
parameter_list|,
name|Field
argument_list|<
name|F1
argument_list|>
name|field1
parameter_list|,
name|Field
argument_list|<
name|F2
argument_list|>
name|field2
parameter_list|)
block|{
return|return
operator|new
name|Timer2
argument_list|<
name|F1
argument_list|,
name|F2
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|record
parameter_list|(
name|F1
name|field1
parameter_list|,
name|F2
name|field2
parameter_list|,
name|long
name|value
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
block|{}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|newTimer (String name, Description desc, Field<F1> field1, Field<F2> field2, Field<F3> field3)
specifier|public
parameter_list|<
name|F1
parameter_list|,
name|F2
parameter_list|,
name|F3
parameter_list|>
name|Timer3
argument_list|<
name|F1
argument_list|,
name|F2
argument_list|,
name|F3
argument_list|>
name|newTimer
parameter_list|(
name|String
name|name
parameter_list|,
name|Description
name|desc
parameter_list|,
name|Field
argument_list|<
name|F1
argument_list|>
name|field1
parameter_list|,
name|Field
argument_list|<
name|F2
argument_list|>
name|field2
parameter_list|,
name|Field
argument_list|<
name|F3
argument_list|>
name|field3
parameter_list|)
block|{
return|return
operator|new
name|Timer3
argument_list|<
name|F1
argument_list|,
name|F2
argument_list|,
name|F3
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|record
parameter_list|(
name|F1
name|field1
parameter_list|,
name|F2
name|field2
parameter_list|,
name|F3
name|field3
parameter_list|,
name|long
name|value
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
block|{}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|newCallbackMetric (String name, Class<V> valueClass, Description desc)
specifier|public
parameter_list|<
name|V
parameter_list|>
name|CallbackMetric0
argument_list|<
name|V
argument_list|>
name|newCallbackMetric
parameter_list|(
name|String
name|name
parameter_list|,
name|Class
argument_list|<
name|V
argument_list|>
name|valueClass
parameter_list|,
name|Description
name|desc
parameter_list|)
block|{
return|return
operator|new
name|CallbackMetric0
argument_list|<
name|V
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|set
parameter_list|(
name|V
name|value
parameter_list|)
block|{}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|newTrigger (Set<CallbackMetric<?>> metrics, Runnable trigger)
specifier|public
name|RegistrationHandle
name|newTrigger
parameter_list|(
name|Set
argument_list|<
name|CallbackMetric
argument_list|<
name|?
argument_list|>
argument_list|>
name|metrics
parameter_list|,
name|Runnable
name|trigger
parameter_list|)
block|{
return|return
operator|new
name|RegistrationHandle
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{}
block|}
return|;
block|}
block|}
end_class

end_unit

