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
name|common
operator|.
name|base
operator|.
name|Strings
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
name|ImmutableMap
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
name|TimeUnit
import|;
end_import

begin_comment
comment|/** Describes a metric created by {@link MetricMaker}. */
end_comment

begin_class
DECL|class|Description
specifier|public
class|class
name|Description
block|{
DECL|field|DESCRIPTION
specifier|public
specifier|static
specifier|final
name|String
name|DESCRIPTION
init|=
literal|"DESCRIPTION"
decl_stmt|;
DECL|field|UNIT
specifier|public
specifier|static
specifier|final
name|String
name|UNIT
init|=
literal|"UNIT"
decl_stmt|;
DECL|field|CUMULATIVE
specifier|public
specifier|static
specifier|final
name|String
name|CUMULATIVE
init|=
literal|"CUMULATIVE"
decl_stmt|;
DECL|field|RATE
specifier|public
specifier|static
specifier|final
name|String
name|RATE
init|=
literal|"RATE"
decl_stmt|;
DECL|field|GAUGE
specifier|public
specifier|static
specifier|final
name|String
name|GAUGE
init|=
literal|"GAUGE"
decl_stmt|;
DECL|field|CONSTANT
specifier|public
specifier|static
specifier|final
name|String
name|CONSTANT
init|=
literal|"CONSTANT"
decl_stmt|;
DECL|field|FIELD_ORDERING
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_ORDERING
init|=
literal|"FIELD_ORDERING"
decl_stmt|;
DECL|field|TRUE_VALUE
specifier|public
specifier|static
specifier|final
name|String
name|TRUE_VALUE
init|=
literal|"1"
decl_stmt|;
DECL|class|Units
specifier|public
specifier|static
class|class
name|Units
block|{
DECL|field|SECONDS
specifier|public
specifier|static
specifier|final
name|String
name|SECONDS
init|=
literal|"seconds"
decl_stmt|;
DECL|field|MILLISECONDS
specifier|public
specifier|static
specifier|final
name|String
name|MILLISECONDS
init|=
literal|"milliseconds"
decl_stmt|;
DECL|field|MICROSECONDS
specifier|public
specifier|static
specifier|final
name|String
name|MICROSECONDS
init|=
literal|"microseconds"
decl_stmt|;
DECL|field|NANOSECONDS
specifier|public
specifier|static
specifier|final
name|String
name|NANOSECONDS
init|=
literal|"nanoseconds"
decl_stmt|;
DECL|field|BYTES
specifier|public
specifier|static
specifier|final
name|String
name|BYTES
init|=
literal|"bytes"
decl_stmt|;
DECL|method|Units ()
specifier|private
name|Units
parameter_list|()
block|{     }
block|}
DECL|enum|FieldOrdering
specifier|public
specifier|static
enum|enum
name|FieldOrdering
block|{
comment|/** Default ordering places fields at end of the parent metric name. */
DECL|enumConstant|AT_END
name|AT_END
block|,
comment|/**      * Splits the metric name by inserting field values before the last '/' in      * the metric name. For example {@code "plugins/replication/push_latency"}      * with a {@code Field.ofString("remote")} will create submetrics named      * {@code "plugins/replication/some-server/push_latency"}.      */
DECL|enumConstant|PREFIX_FIELDS_BASENAME
name|PREFIX_FIELDS_BASENAME
block|;   }
DECL|field|annotations
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|annotations
decl_stmt|;
comment|/**    * Describe a metric.    *    * @param helpText a short one-sentence string explaining the values captured    *        by the metric. This may be made available to administrators as    *        documentation in the reporting tools.    */
DECL|method|Description (String helpText)
specifier|public
name|Description
parameter_list|(
name|String
name|helpText
parameter_list|)
block|{
name|annotations
operator|=
name|Maps
operator|.
name|newLinkedHashMapWithExpectedSize
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|annotations
operator|.
name|put
argument_list|(
name|DESCRIPTION
argument_list|,
name|helpText
argument_list|)
expr_stmt|;
block|}
comment|/** Unit used to describe the value, e.g. "requests", "seconds", etc. */
DECL|method|setUnit (String unitName)
specifier|public
name|Description
name|setUnit
parameter_list|(
name|String
name|unitName
parameter_list|)
block|{
name|annotations
operator|.
name|put
argument_list|(
name|UNIT
argument_list|,
name|unitName
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Mark the value as constant for the life of this process. Typically used for    * software versions, command line arguments, etc. that cannot change without    * a process restart.    */
DECL|method|setConstant ()
specifier|public
name|Description
name|setConstant
parameter_list|()
block|{
name|annotations
operator|.
name|put
argument_list|(
name|CONSTANT
argument_list|,
name|TRUE_VALUE
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Indicates the metric may be usefully interpreted as a count over short    * periods of time, such as request arrival rate. May only be applied to a    * {@link Counter0}.    */
DECL|method|setRate ()
specifier|public
name|Description
name|setRate
parameter_list|()
block|{
name|annotations
operator|.
name|put
argument_list|(
name|RATE
argument_list|,
name|TRUE_VALUE
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Instantaneously sampled value that may increase or decrease at a later    * time. Memory allocated or open network connections are examples of gauges.    */
DECL|method|setGauge ()
specifier|public
name|Description
name|setGauge
parameter_list|()
block|{
name|annotations
operator|.
name|put
argument_list|(
name|GAUGE
argument_list|,
name|TRUE_VALUE
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Indicates the metric accumulates over the lifespan of the process. A    * {@link Counter0} like total requests handled accumulates over the process    * and should be {@code setCumulative()}.    */
DECL|method|setCumulative ()
specifier|public
name|Description
name|setCumulative
parameter_list|()
block|{
name|annotations
operator|.
name|put
argument_list|(
name|CUMULATIVE
argument_list|,
name|TRUE_VALUE
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Configure how fields are ordered into submetric names. */
DECL|method|setFieldOrdering (FieldOrdering ordering)
specifier|public
name|Description
name|setFieldOrdering
parameter_list|(
name|FieldOrdering
name|ordering
parameter_list|)
block|{
name|annotations
operator|.
name|put
argument_list|(
name|FIELD_ORDERING
argument_list|,
name|ordering
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** True if the metric value never changes after startup. */
DECL|method|isConstant ()
specifier|public
name|boolean
name|isConstant
parameter_list|()
block|{
return|return
name|TRUE_VALUE
operator|.
name|equals
argument_list|(
name|annotations
operator|.
name|get
argument_list|(
name|CONSTANT
argument_list|)
argument_list|)
return|;
block|}
comment|/** True if the metric may be interpreted as a rate over time. */
DECL|method|isRate ()
specifier|public
name|boolean
name|isRate
parameter_list|()
block|{
return|return
name|TRUE_VALUE
operator|.
name|equals
argument_list|(
name|annotations
operator|.
name|get
argument_list|(
name|RATE
argument_list|)
argument_list|)
return|;
block|}
comment|/** True if the metric is an instantaneous sample. */
DECL|method|isGauge ()
specifier|public
name|boolean
name|isGauge
parameter_list|()
block|{
return|return
name|TRUE_VALUE
operator|.
name|equals
argument_list|(
name|annotations
operator|.
name|get
argument_list|(
name|GAUGE
argument_list|)
argument_list|)
return|;
block|}
comment|/** True if the metric accumulates over the lifespan of the process. */
DECL|method|isCumulative ()
specifier|public
name|boolean
name|isCumulative
parameter_list|()
block|{
return|return
name|TRUE_VALUE
operator|.
name|equals
argument_list|(
name|annotations
operator|.
name|get
argument_list|(
name|CUMULATIVE
argument_list|)
argument_list|)
return|;
block|}
comment|/** Get the suggested field ordering. */
DECL|method|getFieldOrdering ()
specifier|public
name|FieldOrdering
name|getFieldOrdering
parameter_list|()
block|{
name|String
name|o
init|=
name|annotations
operator|.
name|get
argument_list|(
name|FIELD_ORDERING
argument_list|)
decl_stmt|;
return|return
name|o
operator|!=
literal|null
condition|?
name|FieldOrdering
operator|.
name|valueOf
argument_list|(
name|o
argument_list|)
else|:
name|FieldOrdering
operator|.
name|AT_END
return|;
block|}
comment|/**    * Decode the unit as a unit of time.    *    * @return valid time unit.    * @throws IllegalArgumentException if the unit is not a valid unit of time.    */
DECL|method|getTimeUnit ()
specifier|public
name|TimeUnit
name|getTimeUnit
parameter_list|()
block|{
return|return
name|getTimeUnit
argument_list|(
name|annotations
operator|.
name|get
argument_list|(
name|UNIT
argument_list|)
argument_list|)
return|;
block|}
DECL|field|TIME_UNITS
specifier|private
specifier|static
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|TimeUnit
argument_list|>
name|TIME_UNITS
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|Units
operator|.
name|NANOSECONDS
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|,
name|Units
operator|.
name|MICROSECONDS
argument_list|,
name|TimeUnit
operator|.
name|MICROSECONDS
argument_list|,
name|Units
operator|.
name|MILLISECONDS
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|,
name|Units
operator|.
name|SECONDS
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
DECL|method|getTimeUnit (String unit)
specifier|public
specifier|static
name|TimeUnit
name|getTimeUnit
parameter_list|(
name|String
name|unit
parameter_list|)
block|{
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|unit
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"no unit configured"
argument_list|)
throw|;
block|}
name|TimeUnit
name|u
init|=
name|TIME_UNITS
operator|.
name|get
argument_list|(
name|unit
argument_list|)
decl_stmt|;
if|if
condition|(
name|u
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"unit %s not TimeUnit"
argument_list|,
name|unit
argument_list|)
argument_list|)
throw|;
block|}
return|return
name|u
return|;
block|}
comment|/** Immutable copy of all annotations (configurable properties). */
DECL|method|getAnnotations ()
specifier|public
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getAnnotations
parameter_list|()
block|{
return|return
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|annotations
argument_list|)
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
return|return
name|annotations
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

