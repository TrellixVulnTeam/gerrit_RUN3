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

begin_comment
comment|/**  * Measures the statistical distribution of values in a stream of data.  *<p>  * Suitable uses are "response size in bytes", etc.  */
end_comment

begin_class
DECL|class|Histogram0
specifier|public
specifier|abstract
class|class
name|Histogram0
implements|implements
name|RegistrationHandle
block|{
comment|/**    * Record a sample of a specified amount.    *    * @param value to record    */
DECL|method|record (long value)
specifier|public
specifier|abstract
name|void
name|record
parameter_list|(
name|long
name|value
parameter_list|)
function_decl|;
block|}
end_class

end_unit

