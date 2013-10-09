begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2012 The Android Open Source Project
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
DECL|package|com.google.gerrit.extensions.annotations
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|extensions
operator|.
name|annotations
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|RetentionPolicy
operator|.
name|RUNTIME
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|ElementType
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Retention
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Target
import|;
end_import

begin_comment
comment|/**  * Annotation on {@link com.google.gerrit.sshd.SshCommand} or  * {@link com.google.gerrit.httpd.restapi.RestApiServlet} declaring a  * capability must be granted.  */
end_comment

begin_annotation_defn
annotation|@
name|Target
argument_list|(
block|{
name|ElementType
operator|.
name|TYPE
block|}
argument_list|)
annotation|@
name|Retention
argument_list|(
name|RUNTIME
argument_list|)
DECL|annotation|RequiresCapability
specifier|public
annotation_defn|@interface
name|RequiresCapability
block|{
comment|/**  Name of the capability required to invoke this action. */
DECL|method|value ()
name|String
name|value
parameter_list|()
function_decl|;
comment|/** Scope of the named capability. */
DECL|method|scope ()
DECL|field|CapabilityScope.CONTEXT
name|CapabilityScope
name|scope
parameter_list|()
default|default
name|CapabilityScope
operator|.
name|CONTEXT
function_decl|;
block|}
end_annotation_defn

end_unit

