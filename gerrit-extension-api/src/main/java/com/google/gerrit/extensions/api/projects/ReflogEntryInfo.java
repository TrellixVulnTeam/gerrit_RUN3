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
DECL|package|com.google.gerrit.extensions.api.projects
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
name|projects
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
name|common
operator|.
name|GitPerson
import|;
end_import

begin_class
DECL|class|ReflogEntryInfo
specifier|public
class|class
name|ReflogEntryInfo
block|{
DECL|field|oldId
specifier|public
name|String
name|oldId
decl_stmt|;
DECL|field|newId
specifier|public
name|String
name|newId
decl_stmt|;
DECL|field|who
specifier|public
name|GitPerson
name|who
decl_stmt|;
DECL|field|comment
specifier|public
name|String
name|comment
decl_stmt|;
DECL|method|ReflogEntryInfo (String oldId, String newId, GitPerson who, String comment)
specifier|public
name|ReflogEntryInfo
parameter_list|(
name|String
name|oldId
parameter_list|,
name|String
name|newId
parameter_list|,
name|GitPerson
name|who
parameter_list|,
name|String
name|comment
parameter_list|)
block|{
name|this
operator|.
name|oldId
operator|=
name|oldId
expr_stmt|;
name|this
operator|.
name|newId
operator|=
name|newId
expr_stmt|;
name|this
operator|.
name|who
operator|=
name|who
expr_stmt|;
name|this
operator|.
name|comment
operator|=
name|comment
expr_stmt|;
block|}
block|}
end_class

end_unit

