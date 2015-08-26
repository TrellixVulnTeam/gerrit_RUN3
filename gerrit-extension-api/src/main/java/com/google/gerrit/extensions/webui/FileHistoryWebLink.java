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
DECL|package|com.google.gerrit.extensions.webui
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|extensions
operator|.
name|webui
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
name|WebLinkInfo
import|;
end_import

begin_interface
DECL|interface|FileHistoryWebLink
specifier|public
interface|interface
name|FileHistoryWebLink
extends|extends
name|WebLink
block|{
comment|/**    * {@link com.google.gerrit.extensions.common.WebLinkInfo}    * describing a link from a file to an external service displaying    * a log for that file.    *    *<p>In order for the web link to be visible    * {@link com.google.gerrit.extensions.common.WebLinkInfo#url}    * and {@link com.google.gerrit.extensions.common.WebLinkInfo#name}    * must be set.<p>    *    * @param projectName Name of the project    * @param revision Name of the revision (e.g. branch or commit ID)    * @param fileName Name of the file    * @return WebLinkInfo that links to a log for the file in external    * service, null if there should be no link.    */
DECL|method|getFileHistoryWebLink (String projectName, String revision, String fileName)
name|WebLinkInfo
name|getFileHistoryWebLink
parameter_list|(
name|String
name|projectName
parameter_list|,
name|String
name|revision
parameter_list|,
name|String
name|fileName
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

