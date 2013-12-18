begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2013 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|change
package|;
end_package

begin_comment
comment|/** Operation performed by a change relative to its parent. */
end_comment

begin_enum
DECL|enum|ChangeKind
specifier|public
enum|enum
name|ChangeKind
block|{
comment|/** Nontrivial content changes. */
DECL|enumConstant|REWORK
name|REWORK
block|,
comment|/** Conflict-free merge between the new parent and the prior patch set. */
DECL|enumConstant|TRIVIAL_REBASE
name|TRIVIAL_REBASE
block|,
comment|/** Same tree and same parents. */
DECL|enumConstant|NO_CODE_CHANGE
name|NO_CODE_CHANGE
block|; }
end_enum

end_unit

