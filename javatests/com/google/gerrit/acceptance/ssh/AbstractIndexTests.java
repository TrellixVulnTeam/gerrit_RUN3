begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2018 The Android Open Source Project
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
DECL|package|com.google.gerrit.acceptance.ssh
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|ssh
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|truth
operator|.
name|Truth
operator|.
name|assertThat
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
operator|.
name|toList
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Joiner
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
name|acceptance
operator|.
name|AbstractDaemonTest
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
name|acceptance
operator|.
name|ChangeIndexedCounter
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
name|acceptance
operator|.
name|ExtensionRegistry
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
name|acceptance
operator|.
name|ExtensionRegistry
operator|.
name|Registration
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
name|acceptance
operator|.
name|NoHttpd
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
name|acceptance
operator|.
name|PushOneCommit
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
name|acceptance
operator|.
name|UseSsh
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
name|acceptance
operator|.
name|config
operator|.
name|GerritConfig
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
name|common
operator|.
name|ChangeInfo
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
name|Inject
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
name|java
operator|.
name|util
operator|.
name|List
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
annotation|@
name|NoHttpd
annotation|@
name|UseSsh
DECL|class|AbstractIndexTests
specifier|public
specifier|abstract
class|class
name|AbstractIndexTests
extends|extends
name|AbstractDaemonTest
block|{
DECL|field|extensionRegistry
annotation|@
name|Inject
specifier|private
name|ExtensionRegistry
name|extensionRegistry
decl_stmt|;
comment|/** @param injector injector */
DECL|method|configureIndex (Injector injector)
specifier|public
name|void
name|configureIndex
parameter_list|(
name|Injector
name|injector
parameter_list|)
block|{}
annotation|@
name|Test
annotation|@
name|GerritConfig
argument_list|(
name|name
operator|=
literal|"index.autoReindexIfStale"
argument_list|,
name|value
operator|=
literal|"false"
argument_list|)
DECL|method|indexChange ()
specifier|public
name|void
name|indexChange
parameter_list|()
throws|throws
name|Exception
block|{
name|ChangeIndexedCounter
name|changeIndexedCounter
init|=
operator|new
name|ChangeIndexedCounter
argument_list|()
decl_stmt|;
try|try
init|(
name|Registration
name|registration
init|=
name|extensionRegistry
operator|.
name|newRegistration
argument_list|()
operator|.
name|add
argument_list|(
name|changeIndexedCounter
argument_list|)
init|)
block|{
name|configureIndex
argument_list|(
name|server
operator|.
name|getTestInjector
argument_list|()
argument_list|)
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|change
init|=
name|createChange
argument_list|(
literal|"first change"
argument_list|,
literal|"test1.txt"
argument_list|,
literal|"test1"
argument_list|)
decl_stmt|;
name|String
name|changeId
init|=
name|change
operator|.
name|getChangeId
argument_list|()
decl_stmt|;
name|String
name|changeLegacyId
init|=
name|change
operator|.
name|getChange
argument_list|()
operator|.
name|getId
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|ChangeInfo
name|changeInfo
init|=
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|changeId
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|disableChangeIndexWrites
argument_list|()
expr_stmt|;
name|amendChange
argument_list|(
name|changeId
argument_list|,
literal|"second test"
argument_list|,
literal|"test2.txt"
argument_list|,
literal|"test2"
argument_list|)
expr_stmt|;
name|assertChangeQuery
argument_list|(
name|change
operator|.
name|getChange
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|enableChangeIndexWrites
argument_list|()
expr_stmt|;
name|changeIndexedCounter
operator|.
name|clear
argument_list|()
expr_stmt|;
name|String
name|cmd
init|=
name|Joiner
operator|.
name|on
argument_list|(
literal|" "
argument_list|)
operator|.
name|join
argument_list|(
literal|"gerrit"
argument_list|,
literal|"index"
argument_list|,
literal|"changes"
argument_list|,
name|changeLegacyId
argument_list|)
decl_stmt|;
name|adminSshSession
operator|.
name|exec
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|adminSshSession
operator|.
name|assertSuccess
argument_list|()
expr_stmt|;
name|changeIndexedCounter
operator|.
name|assertReindexOf
argument_list|(
name|changeInfo
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertChangeQuery
argument_list|(
name|change
operator|.
name|getChange
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
annotation|@
name|GerritConfig
argument_list|(
name|name
operator|=
literal|"index.autoReindexIfStale"
argument_list|,
name|value
operator|=
literal|"false"
argument_list|)
DECL|method|indexProject ()
specifier|public
name|void
name|indexProject
parameter_list|()
throws|throws
name|Exception
block|{
name|ChangeIndexedCounter
name|changeIndexedCounter
init|=
operator|new
name|ChangeIndexedCounter
argument_list|()
decl_stmt|;
try|try
init|(
name|Registration
name|registration
init|=
name|extensionRegistry
operator|.
name|newRegistration
argument_list|()
operator|.
name|add
argument_list|(
name|changeIndexedCounter
argument_list|)
init|)
block|{
name|configureIndex
argument_list|(
name|server
operator|.
name|getTestInjector
argument_list|()
argument_list|)
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|change
init|=
name|createChange
argument_list|(
literal|"first change"
argument_list|,
literal|"test1.txt"
argument_list|,
literal|"test1"
argument_list|)
decl_stmt|;
name|String
name|changeId
init|=
name|change
operator|.
name|getChangeId
argument_list|()
decl_stmt|;
name|ChangeInfo
name|changeInfo
init|=
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|changeId
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|disableChangeIndexWrites
argument_list|()
expr_stmt|;
name|amendChange
argument_list|(
name|changeId
argument_list|,
literal|"second test"
argument_list|,
literal|"test2.txt"
argument_list|,
literal|"test2"
argument_list|)
expr_stmt|;
name|assertChangeQuery
argument_list|(
name|change
operator|.
name|getChange
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|enableChangeIndexWrites
argument_list|()
expr_stmt|;
name|changeIndexedCounter
operator|.
name|clear
argument_list|()
expr_stmt|;
name|String
name|cmd
init|=
name|Joiner
operator|.
name|on
argument_list|(
literal|" "
argument_list|)
operator|.
name|join
argument_list|(
literal|"gerrit"
argument_list|,
literal|"index"
argument_list|,
literal|"changes-in-project"
argument_list|,
name|project
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
name|adminSshSession
operator|.
name|exec
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|adminSshSession
operator|.
name|assertSuccess
argument_list|()
expr_stmt|;
name|boolean
name|indexing
init|=
literal|true
decl_stmt|;
while|while
condition|(
name|indexing
condition|)
block|{
name|String
name|out
init|=
name|adminSshSession
operator|.
name|exec
argument_list|(
literal|"gerrit show-queue --wide"
argument_list|)
decl_stmt|;
name|adminSshSession
operator|.
name|assertSuccess
argument_list|()
expr_stmt|;
name|indexing
operator|=
name|out
operator|.
name|contains
argument_list|(
literal|"Index all changes of project "
operator|+
name|project
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|changeIndexedCounter
operator|.
name|assertReindexOf
argument_list|(
name|changeInfo
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertChangeQuery
argument_list|(
name|change
operator|.
name|getChange
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertChangeQuery (ChangeData change, boolean assertTrue)
specifier|private
name|void
name|assertChangeQuery
parameter_list|(
name|ChangeData
name|change
parameter_list|,
name|boolean
name|assertTrue
parameter_list|)
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Integer
argument_list|>
name|ids
init|=
name|query
argument_list|(
literal|"message:second"
argument_list|)
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|c
lambda|->
name|c
operator|.
name|_number
argument_list|)
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|assertTrue
condition|)
block|{
name|assertThat
argument_list|(
name|ids
argument_list|)
operator|.
name|contains
argument_list|(
name|change
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
name|ids
argument_list|)
operator|.
name|doesNotContain
argument_list|(
name|change
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

