begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2019 The Android Open Source Project
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
DECL|package|com.google.gerrit.extensions.common
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|extensions
operator|.
name|common
package|;
end_package

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

begin_comment
comment|/** Input for the REST API that describes additions, updates and deletions of label definitions. */
end_comment

begin_class
DECL|class|BatchLabelInput
specifier|public
class|class
name|BatchLabelInput
block|{
DECL|field|commitMessage
specifier|public
name|String
name|commitMessage
decl_stmt|;
DECL|field|delete
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|delete
decl_stmt|;
DECL|field|create
specifier|public
name|List
argument_list|<
name|LabelDefinitionInput
argument_list|>
name|create
decl_stmt|;
DECL|field|update
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|LabelDefinitionInput
argument_list|>
name|update
decl_stmt|;
block|}
end_class

end_unit

