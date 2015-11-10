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
name|checkArgument
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
name|metrics
operator|.
name|dropwizard
operator|.
name|MetricResource
operator|.
name|METRIC_KIND
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
name|config
operator|.
name|ConfigResource
operator|.
name|CONFIG_KIND
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
name|gerrit
operator|.
name|extensions
operator|.
name|registration
operator|.
name|DynamicMap
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
name|extensions
operator|.
name|registration
operator|.
name|RegistrationHandle
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
name|extensions
operator|.
name|restapi
operator|.
name|RestApiModule
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
name|CallbackMetric
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
name|Counter
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
name|MetricMaker
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
name|Timer
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Scopes
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Singleton
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
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|MetricRegistry
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
name|HashMap
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
name|Map
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
comment|/**  * Connects Gerrit metric package onto DropWizard.  *  * @see<a href="http://www.dropwizard.io/">DropWizard</a>  */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|DropWizardMetricMaker
specifier|public
class|class
name|DropWizardMetricMaker
extends|extends
name|MetricMaker
block|{
DECL|class|Module
specifier|public
specifier|static
class|class
name|Module
extends|extends
name|RestApiModule
block|{
annotation|@
name|Override
DECL|method|configure ()
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|bind
argument_list|(
name|MetricRegistry
operator|.
name|class
argument_list|)
operator|.
name|in
argument_list|(
name|Scopes
operator|.
name|SINGLETON
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|DropWizardMetricMaker
operator|.
name|class
argument_list|)
operator|.
name|in
argument_list|(
name|Scopes
operator|.
name|SINGLETON
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|MetricMaker
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|DropWizardMetricMaker
operator|.
name|class
argument_list|)
expr_stmt|;
name|DynamicMap
operator|.
name|mapOf
argument_list|(
name|binder
argument_list|()
argument_list|,
name|METRIC_KIND
argument_list|)
expr_stmt|;
name|child
argument_list|(
name|CONFIG_KIND
argument_list|,
literal|"metrics"
argument_list|)
operator|.
name|to
argument_list|(
name|MetricsCollection
operator|.
name|class
argument_list|)
expr_stmt|;
name|get
argument_list|(
name|METRIC_KIND
argument_list|)
operator|.
name|to
argument_list|(
name|GetMetric
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|registry
specifier|private
specifier|final
name|MetricRegistry
name|registry
decl_stmt|;
DECL|field|descriptions
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|descriptions
decl_stmt|;
annotation|@
name|Inject
DECL|method|DropWizardMetricMaker (MetricRegistry registry)
name|DropWizardMetricMaker
parameter_list|(
name|MetricRegistry
name|registry
parameter_list|)
block|{
name|this
operator|.
name|registry
operator|=
name|registry
expr_stmt|;
name|this
operator|.
name|descriptions
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
DECL|method|getMetricMap ()
name|Map
argument_list|<
name|String
argument_list|,
name|Metric
argument_list|>
name|getMetricMap
parameter_list|()
block|{
return|return
name|registry
operator|.
name|getMetrics
argument_list|()
return|;
block|}
comment|/** Get the underlying metric implementation. */
DECL|method|getMetric (String name)
specifier|public
name|Metric
name|getMetric
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|registry
operator|.
name|getMetrics
argument_list|()
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/** Lookup annotations from a metric's {@link Description}.  */
DECL|method|getAnnotations (String name)
specifier|public
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getAnnotations
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|descriptions
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newCounter (String name, Description desc)
specifier|public
specifier|synchronized
name|Counter
name|newCounter
parameter_list|(
name|String
name|name
parameter_list|,
name|Description
name|desc
parameter_list|)
block|{
name|checkArgument
argument_list|(
operator|!
name|desc
operator|.
name|isGauge
argument_list|()
argument_list|,
literal|"counters must not be gauge"
argument_list|)
expr_stmt|;
name|checkNotDefined
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|descriptions
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|desc
operator|.
name|getAnnotations
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|desc
operator|.
name|isRate
argument_list|()
condition|)
block|{
specifier|final
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Meter
name|metric
init|=
name|registry
operator|.
name|meter
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
operator|new
name|CounterImpl
argument_list|(
name|name
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|incrementBy
parameter_list|(
name|long
name|delta
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|delta
operator|>=
literal|0
argument_list|,
literal|"counter delta must be>= 0"
argument_list|)
expr_stmt|;
name|metric
operator|.
name|mark
argument_list|(
name|delta
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
else|else
block|{
specifier|final
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Counter
name|metric
init|=
name|registry
operator|.
name|counter
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
operator|new
name|CounterImpl
argument_list|(
name|name
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|incrementBy
parameter_list|(
name|long
name|delta
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|delta
operator|>=
literal|0
argument_list|,
literal|"counter delta must be>= 0"
argument_list|)
expr_stmt|;
name|metric
operator|.
name|inc
argument_list|(
name|delta
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|newTimer (final String name, Description desc)
specifier|public
specifier|synchronized
name|Timer
name|newTimer
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
name|Description
name|desc
parameter_list|)
block|{
name|checkArgument
argument_list|(
operator|!
name|desc
operator|.
name|isGauge
argument_list|()
argument_list|,
literal|"timer must not be a gauge"
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
operator|!
name|desc
operator|.
name|isRate
argument_list|()
argument_list|,
literal|"timer must not be a rate"
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|desc
operator|.
name|isCumulative
argument_list|()
argument_list|,
literal|"timer must be cumulative"
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|desc
operator|.
name|getTimeUnit
argument_list|()
operator|!=
literal|null
argument_list|,
literal|"timer must have a unit"
argument_list|)
expr_stmt|;
name|checkNotDefined
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|descriptions
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|desc
operator|.
name|getAnnotations
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Timer
name|metric
init|=
name|registry
operator|.
name|timer
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
operator|new
name|Timer
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
block|{
name|checkArgument
argument_list|(
name|value
operator|>=
literal|0
argument_list|,
literal|"timer delta must be>= 0"
argument_list|)
expr_stmt|;
name|metric
operator|.
name|update
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
name|descriptions
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|registry
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
annotation|@
name|Override
DECL|method|newCallbackMetric (String name, Class<V> valueClass, Description desc)
specifier|public
parameter_list|<
name|V
parameter_list|>
name|CallbackMetric
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
name|checkNotDefined
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|descriptions
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|desc
operator|.
name|getAnnotations
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|CallbackMetricImpl
argument_list|<
name|V
argument_list|>
argument_list|(
name|name
argument_list|,
name|valueClass
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newTrigger ( Set<CallbackMetric<?>> metrics, Runnable trigger)
specifier|public
specifier|synchronized
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
for|for
control|(
name|CallbackMetric
argument_list|<
name|?
argument_list|>
name|m
range|:
name|metrics
control|)
block|{
name|checkNotDefined
argument_list|(
operator|(
operator|(
name|CallbackMetricImpl
argument_list|<
name|?
argument_list|>
operator|)
name|m
operator|)
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|metrics
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|CallbackMetric
argument_list|<
name|?
argument_list|>
name|m
range|:
name|metrics
control|)
block|{
name|CallbackMetricImpl
argument_list|<
name|?
argument_list|>
name|metric
init|=
operator|(
name|CallbackMetricImpl
argument_list|<
name|?
argument_list|>
operator|)
name|m
decl_stmt|;
name|registry
operator|.
name|register
argument_list|(
name|metric
operator|.
name|name
argument_list|,
name|metric
operator|.
name|gauge
argument_list|(
name|trigger
argument_list|)
argument_list|)
expr_stmt|;
name|names
operator|.
name|add
argument_list|(
name|metric
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
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
block|{
for|for
control|(
name|String
name|name
range|:
name|names
control|)
block|{
name|descriptions
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|registry
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
DECL|method|checkNotDefined (String name)
specifier|private
name|void
name|checkNotDefined
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|registry
operator|.
name|getNames
argument_list|()
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"metric %s already defined"
argument_list|,
name|name
argument_list|)
argument_list|)
throw|;
block|}
block|}
DECL|class|CounterImpl
specifier|private
specifier|abstract
class|class
name|CounterImpl
extends|extends
name|Counter
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|method|CounterImpl (String name)
name|CounterImpl
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|remove ()
specifier|public
name|void
name|remove
parameter_list|()
block|{
name|descriptions
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|registry
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|CallbackMetricImpl
specifier|private
specifier|static
class|class
name|CallbackMetricImpl
parameter_list|<
name|V
parameter_list|>
extends|extends
name|CallbackMetric
argument_list|<
name|V
argument_list|>
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|value
specifier|private
name|V
name|value
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|CallbackMetricImpl (String name, Class<V> valueClass)
name|CallbackMetricImpl
parameter_list|(
name|String
name|name
parameter_list|,
name|Class
argument_list|<
name|V
argument_list|>
name|valueClass
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
if|if
condition|(
name|valueClass
operator|==
name|Integer
operator|.
name|class
condition|)
block|{
name|value
operator|=
operator|(
name|V
operator|)
name|Integer
operator|.
name|valueOf
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|valueClass
operator|==
name|Long
operator|.
name|class
condition|)
block|{
name|value
operator|=
operator|(
name|V
operator|)
name|Long
operator|.
name|valueOf
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|valueClass
operator|==
name|Double
operator|.
name|class
condition|)
block|{
name|value
operator|=
operator|(
name|V
operator|)
name|Double
operator|.
name|valueOf
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|valueClass
operator|==
name|Float
operator|.
name|class
condition|)
block|{
name|value
operator|=
operator|(
name|V
operator|)
name|Float
operator|.
name|valueOf
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|valueClass
operator|==
name|String
operator|.
name|class
condition|)
block|{
name|value
operator|=
operator|(
name|V
operator|)
literal|""
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|valueClass
operator|==
name|Boolean
operator|.
name|class
condition|)
block|{
name|value
operator|=
operator|(
name|V
operator|)
name|Boolean
operator|.
name|FALSE
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unsupported value type "
operator|+
name|valueClass
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|set (V value)
specifier|public
name|void
name|set
parameter_list|(
name|V
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|remove ()
specifier|public
name|void
name|remove
parameter_list|()
block|{
comment|// Triggers register and remove the metric.
block|}
DECL|method|gauge (final Runnable trigger)
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Gauge
argument_list|<
name|V
argument_list|>
name|gauge
parameter_list|(
specifier|final
name|Runnable
name|trigger
parameter_list|)
block|{
return|return
operator|new
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Gauge
argument_list|<
name|V
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|V
name|getValue
parameter_list|()
block|{
name|trigger
operator|.
name|run
argument_list|()
expr_stmt|;
return|return
name|value
return|;
block|}
block|}
return|;
block|}
block|}
block|}
end_class

end_unit

