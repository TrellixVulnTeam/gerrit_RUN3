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
name|Function
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
name|common
operator|.
name|collect
operator|.
name|Maps
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
name|dropwizard
operator|.
name|DropWizardMetricMaker
operator|.
name|HistogramImpl
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Metric
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|ConcurrentHashMap
import|;
end_import

begin_comment
comment|/** Abstract histogram broken down into buckets by {@link Field} values. */
end_comment

begin_class
DECL|class|BucketedHistogram
specifier|abstract
class|class
name|BucketedHistogram
implements|implements
name|BucketedMetric
block|{
DECL|field|metrics
specifier|private
specifier|final
name|DropWizardMetricMaker
name|metrics
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|ordering
specifier|private
specifier|final
name|Description
operator|.
name|FieldOrdering
name|ordering
decl_stmt|;
DECL|field|fields
specifier|protected
specifier|final
name|Field
argument_list|<
name|?
argument_list|>
index|[]
name|fields
decl_stmt|;
DECL|field|total
specifier|protected
specifier|final
name|HistogramImpl
name|total
decl_stmt|;
DECL|field|cells
specifier|private
specifier|final
name|Map
argument_list|<
name|Object
argument_list|,
name|HistogramImpl
argument_list|>
name|cells
decl_stmt|;
DECL|field|lock
specifier|private
specifier|final
name|Object
name|lock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|method|BucketedHistogram (DropWizardMetricMaker metrics, String name, Description desc, Field<?>... fields)
name|BucketedHistogram
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
name|this
operator|.
name|metrics
operator|=
name|metrics
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|ordering
operator|=
name|desc
operator|.
name|getFieldOrdering
argument_list|()
expr_stmt|;
name|this
operator|.
name|fields
operator|=
name|fields
expr_stmt|;
name|this
operator|.
name|total
operator|=
name|metrics
operator|.
name|newHistogramImpl
argument_list|(
name|name
operator|+
literal|"_total"
argument_list|)
expr_stmt|;
name|this
operator|.
name|cells
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
DECL|method|doRemove ()
name|void
name|doRemove
parameter_list|()
block|{
for|for
control|(
name|HistogramImpl
name|c
range|:
name|cells
operator|.
name|values
argument_list|()
control|)
block|{
name|c
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|total
operator|.
name|remove
argument_list|()
expr_stmt|;
name|metrics
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|method|forceCreate (Object f1, Object f2)
name|HistogramImpl
name|forceCreate
parameter_list|(
name|Object
name|f1
parameter_list|,
name|Object
name|f2
parameter_list|)
block|{
return|return
name|forceCreate
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|f1
argument_list|,
name|f2
argument_list|)
argument_list|)
return|;
block|}
DECL|method|forceCreate (Object f1, Object f2, Object f3)
name|HistogramImpl
name|forceCreate
parameter_list|(
name|Object
name|f1
parameter_list|,
name|Object
name|f2
parameter_list|,
name|Object
name|f3
parameter_list|)
block|{
return|return
name|forceCreate
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|f1
argument_list|,
name|f2
argument_list|,
name|f3
argument_list|)
argument_list|)
return|;
block|}
DECL|method|forceCreate (Object key)
name|HistogramImpl
name|forceCreate
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
name|HistogramImpl
name|c
init|=
name|cells
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|null
condition|)
block|{
return|return
name|c
return|;
block|}
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|c
operator|=
name|cells
operator|.
name|get
argument_list|(
name|key
argument_list|)
expr_stmt|;
if|if
condition|(
name|c
operator|==
literal|null
condition|)
block|{
name|c
operator|=
name|metrics
operator|.
name|newHistogramImpl
argument_list|(
name|submetric
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|cells
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
return|return
name|c
return|;
block|}
block|}
DECL|method|submetric (Object key)
specifier|private
name|String
name|submetric
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
return|return
name|DropWizardMetricMaker
operator|.
name|name
argument_list|(
name|ordering
argument_list|,
name|name
argument_list|,
name|name
argument_list|(
name|key
argument_list|)
argument_list|)
return|;
block|}
DECL|method|name (Object key)
specifier|abstract
name|String
name|name
parameter_list|(
name|Object
name|key
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|getTotal ()
specifier|public
name|Metric
name|getTotal
parameter_list|()
block|{
return|return
name|total
operator|.
name|metric
return|;
block|}
annotation|@
name|Override
DECL|method|getFields ()
specifier|public
name|Field
argument_list|<
name|?
argument_list|>
index|[]
name|getFields
parameter_list|()
block|{
return|return
name|fields
return|;
block|}
annotation|@
name|Override
DECL|method|getCells ()
specifier|public
name|Map
argument_list|<
name|Object
argument_list|,
name|Metric
argument_list|>
name|getCells
parameter_list|()
block|{
return|return
name|Maps
operator|.
name|transformValues
argument_list|(
name|cells
argument_list|,
operator|new
name|Function
argument_list|<
name|HistogramImpl
argument_list|,
name|Metric
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Metric
name|apply
parameter_list|(
name|HistogramImpl
name|in
parameter_list|)
block|{
return|return
name|in
operator|.
name|metric
return|;
block|}
block|}
argument_list|)
return|;
block|}
block|}
end_class

end_unit

