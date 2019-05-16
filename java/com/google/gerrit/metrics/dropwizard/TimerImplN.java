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
DECL|package|com.google.gerrit.metrics.dropwizard
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|metrics
operator|.
name|dropwizard
package|;
end_package

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
name|Joiner
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
name|ImmutableList
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
name|metrics
operator|.
name|Description
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
name|metrics
operator|.
name|Field
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
name|metrics
operator|.
name|Timer2
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
name|metrics
operator|.
name|Timer3
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
import|;
end_import

begin_comment
comment|/** Generalized implementation of N-dimensional timer metrics. */
end_comment

begin_class
DECL|class|TimerImplN
class|class
name|TimerImplN
extends|extends
name|BucketedTimer
implements|implements
name|BucketedMetric
block|{
DECL|method|TimerImplN (DropWizardMetricMaker metrics, String name, Description desc, Field<?>... fields)
name|TimerImplN
parameter_list|(
name|DropWizardMetricMaker
name|metrics
parameter_list|,
name|String
name|name
parameter_list|,
name|Description
name|desc
parameter_list|,
name|Field
argument_list|<
name|?
argument_list|>
modifier|...
name|fields
parameter_list|)
block|{
name|super
argument_list|(
name|metrics
argument_list|,
name|name
argument_list|,
name|desc
argument_list|,
name|fields
argument_list|)
expr_stmt|;
block|}
DECL|method|timer2 ()
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
name|timer2
parameter_list|()
block|{
return|return
operator|new
name|Timer2
argument_list|<
name|F1
argument_list|,
name|F2
argument_list|>
argument_list|(
name|name
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|doRecord
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
block|{
name|total
operator|.
name|record
argument_list|(
name|value
argument_list|,
name|unit
argument_list|)
expr_stmt|;
name|forceCreate
argument_list|(
name|field1
argument_list|,
name|field2
argument_list|)
operator|.
name|record
argument_list|(
name|value
argument_list|,
name|unit
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
name|doRemove
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
DECL|method|timer3 ()
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
name|timer3
parameter_list|()
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
argument_list|(
name|name
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|doRecord
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
block|{
name|total
operator|.
name|record
argument_list|(
name|value
argument_list|,
name|unit
argument_list|)
expr_stmt|;
name|forceCreate
argument_list|(
name|field1
argument_list|,
name|field2
argument_list|,
name|field3
argument_list|)
operator|.
name|record
argument_list|(
name|value
argument_list|,
name|unit
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
name|doRemove
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|name (Object key)
name|String
name|name
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
name|ImmutableList
argument_list|<
name|Object
argument_list|>
name|keyList
init|=
operator|(
name|ImmutableList
argument_list|<
name|Object
argument_list|>
operator|)
name|key
decl_stmt|;
name|String
index|[]
name|parts
init|=
operator|new
name|String
index|[
name|fields
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Function
argument_list|<
name|Object
argument_list|,
name|String
argument_list|>
name|fmt
init|=
operator|(
name|Function
argument_list|<
name|Object
argument_list|,
name|String
argument_list|>
operator|)
name|fields
index|[
name|i
index|]
operator|.
name|formatter
argument_list|()
decl_stmt|;
name|parts
index|[
name|i
index|]
operator|=
name|fmt
operator|.
name|apply
argument_list|(
name|keyList
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|replace
argument_list|(
literal|'/'
argument_list|,
literal|'-'
argument_list|)
expr_stmt|;
block|}
return|return
name|Joiner
operator|.
name|on
argument_list|(
literal|'/'
argument_list|)
operator|.
name|join
argument_list|(
name|parts
argument_list|)
return|;
block|}
block|}
end_class

end_unit

