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
DECL|package|com.google.gerrit.extensions.api.config
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|extensions
operator|.
name|api
operator|.
name|config
package|;
end_package

begin_class
DECL|class|AccessCheckInfo
specifier|public
class|class
name|AccessCheckInfo
block|{
DECL|class|Result
specifier|public
specifier|static
class|class
name|Result
block|{
DECL|field|message
specifier|public
name|String
name|message
decl_stmt|;
comment|// HTTP status code.
DECL|field|status
specifier|public
name|int
name|status
decl_stmt|;
block|}
DECL|field|result
specifier|public
name|Result
name|result
decl_stmt|;
comment|// for future extension, we may add inputs / results for bulk checks.
block|}
end_class

end_unit

