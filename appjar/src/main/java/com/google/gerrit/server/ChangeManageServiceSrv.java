begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright 2009 Google Inc.
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
DECL|package|com.google.gerrit.server
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
package|;
end_package

begin_comment
comment|/** Publishes {@link ChangeManageServiceImpl} over JSON. */
end_comment

begin_class
DECL|class|ChangeManageServiceSrv
specifier|public
class|class
name|ChangeManageServiceSrv
extends|extends
name|GerritJsonServlet
block|{
annotation|@
name|Override
DECL|method|createServiceHandle ()
specifier|protected
name|Object
name|createServiceHandle
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|ChangeManageServiceImpl
argument_list|()
return|;
block|}
block|}
end_class

end_unit

