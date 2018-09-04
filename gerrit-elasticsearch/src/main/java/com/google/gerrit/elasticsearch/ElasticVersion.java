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
DECL|package|com.google.gerrit.elasticsearch
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|elasticsearch
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Joiner
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_enum
DECL|enum|ElasticVersion
specifier|public
enum|enum
name|ElasticVersion
block|{
DECL|enumConstant|V2_4
name|V2_4
argument_list|(
literal|"2.4.*"
argument_list|)
block|,
DECL|enumConstant|V5_6
name|V5_6
argument_list|(
literal|"5.6.*"
argument_list|)
block|,
DECL|enumConstant|V6_2
name|V6_2
argument_list|(
literal|"6.2.*"
argument_list|)
block|,
DECL|enumConstant|V6_3
name|V6_3
argument_list|(
literal|"6.3.*"
argument_list|)
block|;
DECL|field|version
specifier|private
specifier|final
name|String
name|version
decl_stmt|;
DECL|field|pattern
specifier|private
specifier|final
name|Pattern
name|pattern
decl_stmt|;
DECL|method|ElasticVersion (String version)
name|ElasticVersion
parameter_list|(
name|String
name|version
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|pattern
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
name|version
argument_list|)
expr_stmt|;
block|}
DECL|class|UnsupportedVersion
specifier|public
specifier|static
class|class
name|UnsupportedVersion
extends|extends
name|ElasticException
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|method|UnsupportedVersion (String version)
name|UnsupportedVersion
parameter_list|(
name|String
name|version
parameter_list|)
block|{
name|super
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Unsupported version: [%s]. Supported versions: %s"
argument_list|,
name|version
argument_list|,
name|supportedVersions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|forVersion (String version)
specifier|public
specifier|static
name|ElasticVersion
name|forVersion
parameter_list|(
name|String
name|version
parameter_list|)
throws|throws
name|UnsupportedVersion
block|{
for|for
control|(
name|ElasticVersion
name|value
range|:
name|ElasticVersion
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|value
operator|.
name|pattern
operator|.
name|matcher
argument_list|(
name|version
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
return|return
name|value
return|;
block|}
block|}
throw|throw
operator|new
name|UnsupportedVersion
argument_list|(
name|version
argument_list|)
throw|;
block|}
DECL|method|supportedVersions ()
specifier|public
specifier|static
name|String
name|supportedVersions
parameter_list|()
block|{
return|return
name|Joiner
operator|.
name|on
argument_list|(
literal|", "
argument_list|)
operator|.
name|join
argument_list|(
name|ElasticVersion
operator|.
name|values
argument_list|()
argument_list|)
return|;
block|}
DECL|method|isV6 ()
specifier|public
name|boolean
name|isV6
parameter_list|()
block|{
return|return
name|version
operator|.
name|startsWith
argument_list|(
literal|"6."
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|version
return|;
block|}
block|}
end_enum

end_unit

