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
DECL|package|com.google.gerrit.extensions.api.access
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
name|access
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_comment
comment|/** Gerrit permission for hosts, projects, refs, changes, labels and plugins. */
end_comment

begin_interface
DECL|interface|GerritPermission
specifier|public
interface|interface
name|GerritPermission
block|{
comment|/**    * @return the permission name used in {@code project.config} permissions if this permission is    *     exposed. Or else, return a lower camel case string of the permission enum.    */
DECL|method|permissionName ()
name|String
name|permissionName
parameter_list|()
function_decl|;
comment|/** @return readable identifier of this permission for exception message. */
DECL|method|describeForException ()
specifier|default
name|String
name|describeForException
parameter_list|()
block|{
return|return
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
operator|.
name|replace
argument_list|(
literal|'_'
argument_list|,
literal|' '
argument_list|)
return|;
block|}
block|}
end_interface

end_unit

