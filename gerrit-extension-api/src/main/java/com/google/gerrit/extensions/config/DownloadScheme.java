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
DECL|package|com.google.gerrit.extensions.config
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|extensions
operator|.
name|config
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
name|annotations
operator|.
name|ExtensionPoint
import|;
end_import

begin_class
annotation|@
name|ExtensionPoint
DECL|class|DownloadScheme
specifier|public
specifier|abstract
class|class
name|DownloadScheme
block|{
comment|/**    * Returns the URL of this download scheme.    *    * @param project the name of the project for which the URL should be returned    * @return URL of the download scheme    */
DECL|method|getUrl (String project)
specifier|public
specifier|abstract
name|String
name|getUrl
parameter_list|(
name|String
name|project
parameter_list|)
function_decl|;
comment|/** @return whether this scheme requires authentication */
DECL|method|isAuthRequired ()
specifier|public
specifier|abstract
name|boolean
name|isAuthRequired
parameter_list|()
function_decl|;
comment|/** @return whether this scheme supports authentication */
DECL|method|isAuthSupported ()
specifier|public
name|boolean
name|isAuthSupported
parameter_list|()
block|{
return|return
name|isAuthRequired
argument_list|()
return|;
block|}
comment|/** @return whether the download scheme is enabled */
DECL|method|isEnabled ()
specifier|public
specifier|abstract
name|boolean
name|isEnabled
parameter_list|()
function_decl|;
block|}
end_class

end_unit

