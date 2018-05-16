begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2017 The Android Open Source Project
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
name|OperatingSystemMXBean
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
DECL|class|OperatingSystemMXBeanProvider
class|class
name|OperatingSystemMXBeanProvider
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|OperatingSystemMXBeanProvider
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|sys
specifier|private
specifier|final
name|OperatingSystemMXBean
name|sys
decl_stmt|;
DECL|field|getProcessCpuTime
specifier|private
specifier|final
name|Method
name|getProcessCpuTime
decl_stmt|;
DECL|field|getOpenFileDescriptorCount
specifier|private
specifier|final
name|Method
name|getOpenFileDescriptorCount
decl_stmt|;
DECL|class|Factory
specifier|static
class|class
name|Factory
block|{
DECL|method|create ()
specifier|static
name|OperatingSystemMXBeanProvider
name|create
parameter_list|()
block|{
name|OperatingSystemMXBean
name|sys
init|=
name|ManagementFactory
operator|.
name|getOperatingSystemMXBean
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|Arrays
operator|.
name|asList
argument_list|(
literal|"com.sun.management.UnixOperatingSystemMXBean"
argument_list|,
literal|"com.ibm.lang.management.UnixOperatingSystemMXBean"
argument_list|)
control|)
block|{
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|impl
init|=
name|Class
operator|.
name|forName
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|impl
operator|.
name|isInstance
argument_list|(
name|sys
argument_list|)
condition|)
block|{
return|return
operator|new
name|OperatingSystemMXBeanProvider
argument_list|(
name|sys
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|ReflectiveOperationException
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"No implementation for {}"
argument_list|,
name|name
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|log
operator|.
name|warn
argument_list|(
literal|"No implementation of UnixOperatingSystemMXBean found"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
DECL|method|OperatingSystemMXBeanProvider (OperatingSystemMXBean sys)
specifier|private
name|OperatingSystemMXBeanProvider
parameter_list|(
name|OperatingSystemMXBean
name|sys
parameter_list|)
throws|throws
name|ReflectiveOperationException
block|{
name|this
operator|.
name|sys
operator|=
name|sys
expr_stmt|;
name|getProcessCpuTime
operator|=
name|sys
operator|.
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
literal|"getProcessCpuTime"
argument_list|,
operator|new
name|Class
argument_list|<
name|?
argument_list|>
index|[]
block|{}
block|)
empty_stmt|;
name|getProcessCpuTime
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|getOpenFileDescriptorCount
operator|=
name|sys
operator|.
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
literal|"getOpenFileDescriptorCount"
argument_list|,
operator|new
name|Class
argument_list|<
name|?
argument_list|>
index|[]
block|{}
block|)
class|;
end_class

begin_expr_stmt
name|getOpenFileDescriptorCount
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
end_expr_stmt

begin_function
unit|}    public
DECL|method|getProcessCpuTime ()
name|long
name|getProcessCpuTime
parameter_list|()
block|{
try|try
block|{
return|return
operator|(
name|long
operator|)
name|getProcessCpuTime
operator|.
name|invoke
argument_list|(
name|sys
argument_list|,
operator|new
name|Object
index|[]
block|{}
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ReflectiveOperationException
name|e
parameter_list|)
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
end_function

begin_function
DECL|method|getOpenFileDescriptorCount ()
specifier|public
name|long
name|getOpenFileDescriptorCount
parameter_list|()
block|{
try|try
block|{
return|return
operator|(
name|long
operator|)
name|getOpenFileDescriptorCount
operator|.
name|invoke
argument_list|(
name|sys
argument_list|,
operator|new
name|Object
index|[]
block|{}
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ReflectiveOperationException
name|e
parameter_list|)
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
end_function

unit|}
end_unit

