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
DECL|package|com.google.gerrit.server.git
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|git
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
name|Singleton
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|storage
operator|.
name|pack
operator|.
name|PackStatistics
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|transport
operator|.
name|PostUploadHook
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|UploadPackMetricsHook
specifier|public
class|class
name|UploadPackMetricsHook
implements|implements
name|PostUploadHook
block|{
DECL|field|upload
specifier|private
specifier|final
name|Counter
name|upload
decl_stmt|;
annotation|@
name|Inject
DECL|method|UploadPackMetricsHook (MetricMaker metricMaker)
name|UploadPackMetricsHook
parameter_list|(
name|MetricMaker
name|metricMaker
parameter_list|)
block|{
name|upload
operator|=
name|metricMaker
operator|.
name|newCounter
argument_list|(
literal|"git/upload-pack"
argument_list|,
operator|new
name|Description
argument_list|(
literal|"Total number of git-upload-pack requests"
argument_list|)
operator|.
name|setRate
argument_list|()
operator|.
name|setUnit
argument_list|(
literal|"requests"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onPostUpload (PackStatistics stats)
specifier|public
name|void
name|onPostUpload
parameter_list|(
name|PackStatistics
name|stats
parameter_list|)
block|{
name|upload
operator|.
name|increment
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

