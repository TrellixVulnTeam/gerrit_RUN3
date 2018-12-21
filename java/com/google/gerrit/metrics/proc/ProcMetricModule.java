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
DECL|package|com.google.gerrit.metrics.proc
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|metrics
operator|.
name|proc
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
name|base
operator|.
name|Supplier
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
name|ImmutableSet
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
name|common
operator|.
name|Version
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
name|CallbackMetric0
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
name|CallbackMetric1
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
name|Description
operator|.
name|Units
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
name|MetricMaker
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|GarbageCollectorMXBean
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|MemoryMXBean
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|MemoryUsage
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ThreadMXBean
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

begin_class
DECL|class|ProcMetricModule
specifier|public
class|class
name|ProcMetricModule
extends|extends
name|MetricModule
block|{
annotation|@
name|Override
DECL|method|configure (MetricMaker metrics)
specifier|protected
name|void
name|configure
parameter_list|(
name|MetricMaker
name|metrics
parameter_list|)
block|{
name|buildLabel
argument_list|(
name|metrics
argument_list|)
expr_stmt|;
name|procUptime
argument_list|(
name|metrics
argument_list|)
expr_stmt|;
name|procCpuUsage
argument_list|(
name|metrics
argument_list|)
expr_stmt|;
name|procJvmGc
argument_list|(
name|metrics
argument_list|)
expr_stmt|;
name|procJvmMemory
argument_list|(
name|metrics
argument_list|)
expr_stmt|;
name|procJvmThread
argument_list|(
name|metrics
argument_list|)
expr_stmt|;
block|}
DECL|method|buildLabel (MetricMaker metrics)
specifier|private
name|void
name|buildLabel
parameter_list|(
name|MetricMaker
name|metrics
parameter_list|)
block|{
name|metrics
operator|.
name|newConstantMetric
argument_list|(
literal|"build/label"
argument_list|,
name|Strings
operator|.
name|nullToEmpty
argument_list|(
name|Version
operator|.
name|getVersion
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Description
argument_list|(
literal|"Version of Gerrit server software"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|procUptime (MetricMaker metrics)
specifier|private
name|void
name|procUptime
parameter_list|(
name|MetricMaker
name|metrics
parameter_list|)
block|{
name|metrics
operator|.
name|newConstantMetric
argument_list|(
literal|"proc/birth_timestamp"
argument_list|,
name|Long
operator|.
name|valueOf
argument_list|(
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toMicros
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
argument_list|)
argument_list|,
operator|new
name|Description
argument_list|(
literal|"Time at which the process started"
argument_list|)
operator|.
name|setUnit
argument_list|(
name|Units
operator|.
name|MICROSECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|newCallbackMetric
argument_list|(
literal|"proc/uptime"
argument_list|,
name|Long
operator|.
name|class
argument_list|,
operator|new
name|Description
argument_list|(
literal|"Uptime of this process"
argument_list|)
operator|.
name|setUnit
argument_list|(
name|Units
operator|.
name|MILLISECONDS
argument_list|)
argument_list|,
name|ManagementFactory
operator|.
name|getRuntimeMXBean
argument_list|()
operator|::
name|getUptime
argument_list|)
expr_stmt|;
block|}
DECL|method|procCpuUsage (MetricMaker metrics)
specifier|private
name|void
name|procCpuUsage
parameter_list|(
name|MetricMaker
name|metrics
parameter_list|)
block|{
specifier|final
name|OperatingSystemMXBeanProvider
name|provider
init|=
name|OperatingSystemMXBeanProvider
operator|.
name|Factory
operator|.
name|create
argument_list|()
decl_stmt|;
if|if
condition|(
name|provider
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|provider
operator|.
name|getProcessCpuTime
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
name|metrics
operator|.
name|newCallbackMetric
argument_list|(
literal|"proc/cpu/usage"
argument_list|,
name|Double
operator|.
name|class
argument_list|,
operator|new
name|Description
argument_list|(
literal|"CPU time used by the process"
argument_list|)
operator|.
name|setCumulative
argument_list|()
operator|.
name|setUnit
argument_list|(
name|Units
operator|.
name|SECONDS
argument_list|)
argument_list|,
operator|new
name|Supplier
argument_list|<
name|Double
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Double
name|get
parameter_list|()
block|{
return|return
name|provider
operator|.
name|getProcessCpuTime
argument_list|()
operator|/
literal|1e9
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|provider
operator|.
name|getOpenFileDescriptorCount
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
name|metrics
operator|.
name|newCallbackMetric
argument_list|(
literal|"proc/num_open_fds"
argument_list|,
name|Long
operator|.
name|class
argument_list|,
operator|new
name|Description
argument_list|(
literal|"Number of open file descriptors"
argument_list|)
operator|.
name|setGauge
argument_list|()
operator|.
name|setUnit
argument_list|(
literal|"fds"
argument_list|)
argument_list|,
name|provider
operator|::
name|getOpenFileDescriptorCount
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|procJvmMemory (MetricMaker metrics)
specifier|private
name|void
name|procJvmMemory
parameter_list|(
name|MetricMaker
name|metrics
parameter_list|)
block|{
name|CallbackMetric0
argument_list|<
name|Long
argument_list|>
name|heapCommitted
init|=
name|metrics
operator|.
name|newCallbackMetric
argument_list|(
literal|"proc/jvm/memory/heap_committed"
argument_list|,
name|Long
operator|.
name|class
argument_list|,
operator|new
name|Description
argument_list|(
literal|"Amount of memory guaranteed for user objects."
argument_list|)
operator|.
name|setGauge
argument_list|()
operator|.
name|setUnit
argument_list|(
name|Units
operator|.
name|BYTES
argument_list|)
argument_list|)
decl_stmt|;
name|CallbackMetric0
argument_list|<
name|Long
argument_list|>
name|heapUsed
init|=
name|metrics
operator|.
name|newCallbackMetric
argument_list|(
literal|"proc/jvm/memory/heap_used"
argument_list|,
name|Long
operator|.
name|class
argument_list|,
operator|new
name|Description
argument_list|(
literal|"Amount of memory holding user objects."
argument_list|)
operator|.
name|setGauge
argument_list|()
operator|.
name|setUnit
argument_list|(
name|Units
operator|.
name|BYTES
argument_list|)
argument_list|)
decl_stmt|;
name|CallbackMetric0
argument_list|<
name|Long
argument_list|>
name|nonHeapCommitted
init|=
name|metrics
operator|.
name|newCallbackMetric
argument_list|(
literal|"proc/jvm/memory/non_heap_committed"
argument_list|,
name|Long
operator|.
name|class
argument_list|,
operator|new
name|Description
argument_list|(
literal|"Amount of memory guaranteed for classes, etc."
argument_list|)
operator|.
name|setGauge
argument_list|()
operator|.
name|setUnit
argument_list|(
name|Units
operator|.
name|BYTES
argument_list|)
argument_list|)
decl_stmt|;
name|CallbackMetric0
argument_list|<
name|Long
argument_list|>
name|nonHeapUsed
init|=
name|metrics
operator|.
name|newCallbackMetric
argument_list|(
literal|"proc/jvm/memory/non_heap_used"
argument_list|,
name|Long
operator|.
name|class
argument_list|,
operator|new
name|Description
argument_list|(
literal|"Amount of memory holding classes, etc."
argument_list|)
operator|.
name|setGauge
argument_list|()
operator|.
name|setUnit
argument_list|(
name|Units
operator|.
name|BYTES
argument_list|)
argument_list|)
decl_stmt|;
name|CallbackMetric0
argument_list|<
name|Integer
argument_list|>
name|objectPendingFinalizationCount
init|=
name|metrics
operator|.
name|newCallbackMetric
argument_list|(
literal|"proc/jvm/memory/object_pending_finalization_count"
argument_list|,
name|Integer
operator|.
name|class
argument_list|,
operator|new
name|Description
argument_list|(
literal|"Approximate number of objects needing finalization."
argument_list|)
operator|.
name|setGauge
argument_list|()
operator|.
name|setUnit
argument_list|(
literal|"objects"
argument_list|)
argument_list|)
decl_stmt|;
name|MemoryMXBean
name|memory
init|=
name|ManagementFactory
operator|.
name|getMemoryMXBean
argument_list|()
decl_stmt|;
name|metrics
operator|.
name|newTrigger
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|heapCommitted
argument_list|,
name|heapUsed
argument_list|,
name|nonHeapCommitted
argument_list|,
name|nonHeapUsed
argument_list|,
name|objectPendingFinalizationCount
argument_list|)
argument_list|,
parameter_list|()
lambda|->
block|{
try|try
block|{
name|MemoryUsage
name|stats
init|=
name|memory
operator|.
name|getHeapMemoryUsage
argument_list|()
decl_stmt|;
name|heapCommitted
operator|.
name|set
argument_list|(
name|stats
operator|.
name|getCommitted
argument_list|()
argument_list|)
expr_stmt|;
name|heapUsed
operator|.
name|set
argument_list|(
name|stats
operator|.
name|getUsed
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// MXBean may throw due to a bug in Java 7; ignore.
block|}
name|MemoryUsage
name|stats
init|=
name|memory
operator|.
name|getNonHeapMemoryUsage
argument_list|()
decl_stmt|;
name|nonHeapCommitted
operator|.
name|set
argument_list|(
name|stats
operator|.
name|getCommitted
argument_list|()
argument_list|)
expr_stmt|;
name|nonHeapUsed
operator|.
name|set
argument_list|(
name|stats
operator|.
name|getUsed
argument_list|()
argument_list|)
expr_stmt|;
name|objectPendingFinalizationCount
operator|.
name|set
argument_list|(
name|memory
operator|.
name|getObjectPendingFinalizationCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|procJvmGc (MetricMaker metrics)
specifier|private
name|void
name|procJvmGc
parameter_list|(
name|MetricMaker
name|metrics
parameter_list|)
block|{
name|CallbackMetric1
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|gcCount
init|=
name|metrics
operator|.
name|newCallbackMetric
argument_list|(
literal|"proc/jvm/gc/count"
argument_list|,
name|Long
operator|.
name|class
argument_list|,
operator|new
name|Description
argument_list|(
literal|"Number of GCs"
argument_list|)
operator|.
name|setCumulative
argument_list|()
argument_list|,
name|Field
operator|.
name|ofString
argument_list|(
literal|"gc_name"
argument_list|,
literal|"The name of the garbage collector"
argument_list|)
argument_list|)
decl_stmt|;
name|CallbackMetric1
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|gcTime
init|=
name|metrics
operator|.
name|newCallbackMetric
argument_list|(
literal|"proc/jvm/gc/time"
argument_list|,
name|Long
operator|.
name|class
argument_list|,
operator|new
name|Description
argument_list|(
literal|"Approximate accumulated GC elapsed time"
argument_list|)
operator|.
name|setCumulative
argument_list|()
operator|.
name|setUnit
argument_list|(
name|Units
operator|.
name|MILLISECONDS
argument_list|)
argument_list|,
name|Field
operator|.
name|ofString
argument_list|(
literal|"gc_name"
argument_list|,
literal|"The name of the garbage collector"
argument_list|)
argument_list|)
decl_stmt|;
name|metrics
operator|.
name|newTrigger
argument_list|(
name|gcCount
argument_list|,
name|gcTime
argument_list|,
parameter_list|()
lambda|->
block|{
for|for
control|(
name|GarbageCollectorMXBean
name|gc
range|:
name|ManagementFactory
operator|.
name|getGarbageCollectorMXBeans
argument_list|()
control|)
block|{
name|long
name|count
init|=
name|gc
operator|.
name|getCollectionCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|count
operator|!=
operator|-
literal|1
condition|)
block|{
name|gcCount
operator|.
name|set
argument_list|(
name|gc
operator|.
name|getName
argument_list|()
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
name|long
name|time
init|=
name|gc
operator|.
name|getCollectionTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|time
operator|!=
operator|-
literal|1
condition|)
block|{
name|gcTime
operator|.
name|set
argument_list|(
name|gc
operator|.
name|getName
argument_list|()
argument_list|,
name|time
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|procJvmThread (MetricMaker metrics)
specifier|private
name|void
name|procJvmThread
parameter_list|(
name|MetricMaker
name|metrics
parameter_list|)
block|{
name|ThreadMXBean
name|thread
init|=
name|ManagementFactory
operator|.
name|getThreadMXBean
argument_list|()
decl_stmt|;
name|metrics
operator|.
name|newCallbackMetric
argument_list|(
literal|"proc/jvm/thread/num_live"
argument_list|,
name|Integer
operator|.
name|class
argument_list|,
operator|new
name|Description
argument_list|(
literal|"Current live thread count"
argument_list|)
operator|.
name|setGauge
argument_list|()
operator|.
name|setUnit
argument_list|(
literal|"threads"
argument_list|)
argument_list|,
name|thread
operator|::
name|getThreadCount
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

