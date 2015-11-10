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
DECL|package|com.google.gerrit.testutil
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|testutil
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
name|notedb
operator|.
name|NotesMigration
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
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TestRule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|Description
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|model
operator|.
name|Statement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|ConfigSuite
operator|.
name|class
argument_list|)
DECL|class|GerritServerTests
specifier|public
class|class
name|GerritServerTests
extends|extends
name|GerritBaseTests
block|{
annotation|@
name|ConfigSuite
operator|.
name|Parameter
DECL|field|config
specifier|public
name|Config
name|config
decl_stmt|;
annotation|@
name|ConfigSuite
operator|.
name|Name
DECL|field|configName
specifier|private
name|String
name|configName
decl_stmt|;
DECL|method|isNoteDbTestEnabled ()
specifier|public
specifier|static
name|boolean
name|isNoteDbTestEnabled
parameter_list|()
block|{
specifier|final
name|String
index|[]
name|RUN_FLAGS
init|=
block|{
literal|"yes"
block|,
literal|"y"
block|,
literal|"true"
block|}
decl_stmt|;
name|String
name|value
init|=
name|System
operator|.
name|getenv
argument_list|(
literal|"GERRIT_ENABLE_NOTEDB"
argument_list|)
decl_stmt|;
return|return
name|value
operator|!=
literal|null
operator|&&
name|Arrays
operator|.
name|asList
argument_list|(
name|RUN_FLAGS
argument_list|)
operator|.
name|contains
argument_list|(
name|value
operator|.
name|toLowerCase
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Rule
DECL|field|testRunner
specifier|public
name|TestRule
name|testRunner
init|=
operator|new
name|TestRule
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Statement
name|apply
parameter_list|(
specifier|final
name|Statement
name|base
parameter_list|,
specifier|final
name|Description
name|description
parameter_list|)
block|{
return|return
operator|new
name|Statement
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|evaluate
parameter_list|()
throws|throws
name|Throwable
block|{
name|beforeTest
argument_list|()
expr_stmt|;
try|try
block|{
name|base
operator|.
name|evaluate
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|afterTest
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
block|}
decl_stmt|;
DECL|method|beforeTest ()
specifier|public
name|void
name|beforeTest
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|isNoteDbTestEnabled
argument_list|()
condition|)
block|{
name|NotesMigration
operator|.
name|setAllEnabledConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|afterTest ()
specifier|public
name|void
name|afterTest
parameter_list|()
block|{   }
block|}
end_class

end_unit

