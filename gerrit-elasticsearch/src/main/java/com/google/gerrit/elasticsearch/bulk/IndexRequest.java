begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2018 The Android Open Source Project
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
DECL|package|com.google.gerrit.elasticsearch.bulk
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|elasticsearch
operator|.
name|bulk
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
name|elasticsearch
operator|.
name|ElasticQueryAdapter
import|;
end_import

begin_class
DECL|class|IndexRequest
specifier|public
class|class
name|IndexRequest
extends|extends
name|ActionRequest
block|{
DECL|method|IndexRequest (String id, String index, String type, ElasticQueryAdapter adapter)
specifier|public
name|IndexRequest
parameter_list|(
name|String
name|id
parameter_list|,
name|String
name|index
parameter_list|,
name|String
name|type
parameter_list|,
name|ElasticQueryAdapter
name|adapter
parameter_list|)
block|{
name|super
argument_list|(
literal|"index"
argument_list|,
name|id
argument_list|,
name|index
argument_list|,
name|type
argument_list|,
name|adapter
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

