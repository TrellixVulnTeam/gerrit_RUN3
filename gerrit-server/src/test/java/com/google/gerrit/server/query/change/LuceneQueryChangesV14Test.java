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
DECL|package|com.google.gerrit.server.query.change
package|package
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
name|testutil
operator|.
name|InMemoryModule
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
name|Guice
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
name|Injector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Config
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|LuceneQueryChangesV14Test
specifier|public
class|class
name|LuceneQueryChangesV14Test
extends|extends
name|LuceneQueryChangesTest
block|{
annotation|@
name|Override
DECL|method|createInjector ()
specifier|protected
name|Injector
name|createInjector
parameter_list|()
block|{
name|Config
name|luceneConfig
init|=
operator|new
name|Config
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|InMemoryModule
operator|.
name|setDefaults
argument_list|(
name|luceneConfig
argument_list|)
expr_stmt|;
comment|// Latest version with a Lucene 4 index.
name|luceneConfig
operator|.
name|setInt
argument_list|(
literal|"index"
argument_list|,
literal|"lucene"
argument_list|,
literal|"testVersion"
argument_list|,
literal|14
argument_list|)
expr_stmt|;
return|return
name|Guice
operator|.
name|createInjector
argument_list|(
operator|new
name|InMemoryModule
argument_list|(
name|luceneConfig
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Ignore
annotation|@
name|Test
DECL|method|byCommentBy ()
specifier|public
name|void
name|byCommentBy
parameter_list|()
block|{
comment|// Ignore.
block|}
annotation|@
name|Override
annotation|@
name|Ignore
annotation|@
name|Test
DECL|method|byFrom ()
specifier|public
name|void
name|byFrom
parameter_list|()
block|{
comment|// Ignore.
block|}
block|}
end_class

end_unit

