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
DECL|package|com.google.gerrit.server.plugins
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|plugins
package|;
end_package

begin_interface
DECL|interface|HttpModuleGenerator
specifier|public
interface|interface
name|HttpModuleGenerator
extends|extends
name|ModuleGenerator
block|{
DECL|method|export (String javascript)
name|void
name|export
parameter_list|(
name|String
name|javascript
parameter_list|)
function_decl|;
DECL|class|NOP
class|class
name|NOP
extends|extends
name|ModuleGenerator
operator|.
name|NOP
implements|implements
name|HttpModuleGenerator
block|{
annotation|@
name|Override
DECL|method|export (String javascript)
specifier|public
name|void
name|export
parameter_list|(
name|String
name|javascript
parameter_list|)
block|{
comment|// do nothing
block|}
block|}
block|}
end_interface

end_unit

