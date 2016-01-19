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
name|reviewdb
operator|.
name|client
operator|.
name|Change
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
name|gerrit
operator|.
name|testutil
operator|.
name|InMemoryRepositoryManager
operator|.
name|Repo
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
name|junit
operator|.
name|TestRepository
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
name|eclipse
operator|.
name|jgit
operator|.
name|revwalk
operator|.
name|RevCommit
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
DECL|class|LuceneQueryChangesTest
specifier|public
class|class
name|LuceneQueryChangesTest
extends|extends
name|AbstractQueryChangesTest
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
name|Test
DECL|method|fullTextWithSpecialChars ()
specifier|public
name|void
name|fullTextWithSpecialChars
parameter_list|()
throws|throws
name|Exception
block|{
name|TestRepository
argument_list|<
name|Repo
argument_list|>
name|repo
init|=
name|createProject
argument_list|(
literal|"repo"
argument_list|)
decl_stmt|;
name|RevCommit
name|commit1
init|=
name|repo
operator|.
name|parseBody
argument_list|(
name|repo
operator|.
name|commit
argument_list|()
operator|.
name|message
argument_list|(
literal|"foo_bar_foo"
argument_list|)
operator|.
name|create
argument_list|()
argument_list|)
decl_stmt|;
name|Change
name|change1
init|=
name|insert
argument_list|(
name|repo
argument_list|,
name|newChangeForCommit
argument_list|(
name|repo
argument_list|,
name|commit1
argument_list|)
argument_list|)
decl_stmt|;
name|RevCommit
name|commit2
init|=
name|repo
operator|.
name|parseBody
argument_list|(
name|repo
operator|.
name|commit
argument_list|()
operator|.
name|message
argument_list|(
literal|"one.two.three"
argument_list|)
operator|.
name|create
argument_list|()
argument_list|)
decl_stmt|;
name|Change
name|change2
init|=
name|insert
argument_list|(
name|repo
argument_list|,
name|newChangeForCommit
argument_list|(
name|repo
argument_list|,
name|commit2
argument_list|)
argument_list|)
decl_stmt|;
name|assertQuery
argument_list|(
literal|"message:foo_ba"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"message:bar"
argument_list|,
name|change1
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"message:foo_bar"
argument_list|,
name|change1
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"message:foo bar"
argument_list|,
name|change1
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"message:two"
argument_list|,
name|change2
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"message:one.two"
argument_list|,
name|change2
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"message:one two"
argument_list|,
name|change2
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

