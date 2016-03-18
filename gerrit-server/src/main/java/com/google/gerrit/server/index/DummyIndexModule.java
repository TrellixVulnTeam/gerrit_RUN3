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
DECL|package|com.google.gerrit.server.index
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|index
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
name|server
operator|.
name|index
operator|.
name|change
operator|.
name|ChangeIndex
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|index
operator|.
name|change
operator|.
name|DummyChangeIndex
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|query
operator|.
name|change
operator|.
name|ChangeData
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|AbstractModule
import|;
end_import

begin_class
DECL|class|DummyIndexModule
specifier|public
class|class
name|DummyIndexModule
extends|extends
name|AbstractModule
block|{
DECL|class|DummyChangeIndexFactory
specifier|private
specifier|static
class|class
name|DummyChangeIndexFactory
implements|implements
name|ChangeIndex
operator|.
name|Factory
block|{
annotation|@
name|Override
DECL|method|create (Schema<ChangeData> schema)
specifier|public
name|ChangeIndex
name|create
parameter_list|(
name|Schema
argument_list|<
name|ChangeData
argument_list|>
name|schema
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|configure ()
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|install
argument_list|(
operator|new
name|IndexModule
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|IndexConfig
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|IndexConfig
operator|.
name|createDefault
argument_list|()
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|Index
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
operator|new
name|DummyChangeIndex
argument_list|()
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|ChangeIndex
operator|.
name|Factory
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
operator|new
name|DummyChangeIndexFactory
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

