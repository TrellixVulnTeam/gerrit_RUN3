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
DECL|package|com.google.gerrit.client
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
package|;
end_package

begin_enum
DECL|enum|GerritUiExtensionPoint
specifier|public
enum|enum
name|GerritUiExtensionPoint
block|{
comment|/* ChangeScreen */
DECL|enumConstant|CHANGE_SCREEN_BELOW_CHANGE_INFO_BLOCK
name|CHANGE_SCREEN_BELOW_CHANGE_INFO_BLOCK
block|,
comment|/* MyProfileScreen */
DECL|enumConstant|PROFILE_SCREEN_BOTTOM
name|PROFILE_SCREEN_BOTTOM
block|,
comment|/* ProjectInfoScreen */
DECL|enumConstant|PROJECT_INFO_SCREEN_TOP
DECL|enumConstant|PROJECT_INFO_SCREEN_BOTTOM
name|PROJECT_INFO_SCREEN_TOP
block|,
name|PROJECT_INFO_SCREEN_BOTTOM
block|;
DECL|enum|Key
specifier|public
enum|enum
name|Key
block|{
DECL|enumConstant|CHANGE_ID
DECL|enumConstant|PROJECT_NAME
name|CHANGE_ID
block|,
name|PROJECT_NAME
block|}
block|}
end_enum

end_unit

