begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2009 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.schema
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|schema
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Scopes
operator|.
name|SINGLETON
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
name|extensions
operator|.
name|config
operator|.
name|FactoryModule
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
name|reviewdb
operator|.
name|server
operator|.
name|ReviewDb
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|jdbc
operator|.
name|Database
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|server
operator|.
name|SchemaFactory
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
name|TypeLiteral
import|;
end_import

begin_comment
comment|/** Loads the database with standard dependencies. */
end_comment

begin_class
DECL|class|DatabaseModule
specifier|public
class|class
name|DatabaseModule
extends|extends
name|FactoryModule
block|{
annotation|@
name|Override
DECL|method|configure ()
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|bind
argument_list|(
operator|new
name|TypeLiteral
argument_list|<
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
argument_list|>
argument_list|()
block|{}
argument_list|)
operator|.
name|to
argument_list|(
operator|new
name|TypeLiteral
argument_list|<
name|Database
argument_list|<
name|ReviewDb
argument_list|>
argument_list|>
argument_list|()
block|{}
argument_list|)
operator|.
name|in
argument_list|(
name|SINGLETON
argument_list|)
expr_stmt|;
name|bind
argument_list|(
operator|new
name|TypeLiteral
argument_list|<
name|Database
argument_list|<
name|ReviewDb
argument_list|>
argument_list|>
argument_list|()
block|{}
argument_list|)
operator|.
name|toProvider
argument_list|(
name|ReviewDbDatabaseProvider
operator|.
name|class
argument_list|)
operator|.
name|in
argument_list|(
name|SINGLETON
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

