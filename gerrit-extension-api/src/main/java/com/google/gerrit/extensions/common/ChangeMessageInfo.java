begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2014 The Android Open Source Project
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
name|sql
operator|.
name|Timestamp
import|;
end_import

begin_class
DECL|class|ChangeMessageInfo
specifier|public
class|class
name|ChangeMessageInfo
block|{
DECL|field|id
specifier|public
name|String
name|id
decl_stmt|;
DECL|field|tag
specifier|public
name|String
name|tag
decl_stmt|;
DECL|field|author
specifier|public
name|AccountInfo
name|author
decl_stmt|;
DECL|field|date
specifier|public
name|Timestamp
name|date
decl_stmt|;
DECL|field|message
specifier|public
name|String
name|message
decl_stmt|;
DECL|field|_revisionNumber
specifier|public
name|Integer
name|_revisionNumber
decl_stmt|;
block|}
end_class

end_unit

