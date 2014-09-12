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

begin_class
DECL|class|WebLinkInfo
specifier|public
class|class
name|WebLinkInfo
block|{
DECL|field|name
specifier|public
name|String
name|name
decl_stmt|;
DECL|field|imageUrl
specifier|public
name|String
name|imageUrl
decl_stmt|;
DECL|field|url
specifier|public
name|String
name|url
decl_stmt|;
DECL|field|target
specifier|public
name|String
name|target
decl_stmt|;
DECL|method|WebLinkInfo (String name, String imageUrl, String url, String target)
specifier|public
name|WebLinkInfo
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|imageUrl
parameter_list|,
name|String
name|url
parameter_list|,
name|String
name|target
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|imageUrl
operator|=
name|imageUrl
expr_stmt|;
name|this
operator|.
name|url
operator|=
name|url
expr_stmt|;
name|this
operator|.
name|target
operator|=
name|target
expr_stmt|;
block|}
block|}
end_class

end_unit

