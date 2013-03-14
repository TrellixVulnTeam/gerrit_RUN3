begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2011 The Android Open Source Project
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
DECL|package|com.google.gerrit.rules
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|rules
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
name|common
operator|.
name|data
operator|.
name|LabelType
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
name|common
operator|.
name|data
operator|.
name|LabelTypes
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
name|common
operator|.
name|data
operator|.
name|LabelValue
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
name|client
operator|.
name|Account
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
name|client
operator|.
name|AccountGroup
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
name|client
operator|.
name|Branch
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
name|reviewdb
operator|.
name|client
operator|.
name|Project
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
name|git
operator|.
name|ProjectConfig
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
name|project
operator|.
name|ProjectCache
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
name|project
operator|.
name|ProjectState
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_class
DECL|class|GerritCommonTest
specifier|public
class|class
name|GerritCommonTest
extends|extends
name|PrologTestCase
block|{
DECL|field|project
specifier|private
name|ProjectState
name|project
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|LabelTypes
name|types
init|=
operator|new
name|LabelTypes
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|category
argument_list|(
literal|"CRVW"
argument_list|,
literal|"Code-Review"
argument_list|,
name|value
argument_list|(
literal|2
argument_list|,
literal|"Looks good to me, approved"
argument_list|)
argument_list|,
name|value
argument_list|(
literal|1
argument_list|,
literal|"Looks good to me, but someone else must approve"
argument_list|)
argument_list|,
name|value
argument_list|(
literal|0
argument_list|,
literal|"No score"
argument_list|)
argument_list|,
name|value
argument_list|(
operator|-
literal|1
argument_list|,
literal|"I would prefer that you didn't submit this"
argument_list|)
argument_list|,
name|value
argument_list|(
operator|-
literal|2
argument_list|,
literal|"Do not submit"
argument_list|)
argument_list|)
argument_list|,
name|category
argument_list|(
literal|"VRIF"
argument_list|,
literal|"Verified"
argument_list|,
name|value
argument_list|(
literal|1
argument_list|,
literal|"Verified"
argument_list|)
argument_list|,
name|value
argument_list|(
literal|0
argument_list|,
literal|"No score"
argument_list|)
argument_list|,
name|value
argument_list|(
operator|-
literal|1
argument_list|,
literal|"Fails"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|ProjectConfig
name|config
init|=
operator|new
name|ProjectConfig
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"myproject"
argument_list|)
argument_list|)
decl_stmt|;
name|config
operator|.
name|createInMemory
argument_list|()
expr_stmt|;
name|project
operator|=
operator|new
name|ProjectState
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|types
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|load
argument_list|(
literal|"gerrit"
argument_list|,
literal|"gerrit_common_test.pl"
argument_list|,
operator|new
name|AbstractModule
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|bind
argument_list|(
name|ProjectCache
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
operator|new
name|Projects
argument_list|(
name|project
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setUpEnvironment (PrologEnvironment env)
specifier|protected
name|void
name|setUpEnvironment
parameter_list|(
name|PrologEnvironment
name|env
parameter_list|)
block|{
name|env
operator|.
name|set
argument_list|(
name|StoredValues
operator|.
name|CHANGE
argument_list|,
operator|new
name|Change
argument_list|(
operator|new
name|Change
operator|.
name|Key
argument_list|(
literal|"Ibeef"
argument_list|)
argument_list|,
operator|new
name|Change
operator|.
name|Id
argument_list|(
literal|1
argument_list|)
argument_list|,
operator|new
name|Account
operator|.
name|Id
argument_list|(
literal|2
argument_list|)
argument_list|,
operator|new
name|Branch
operator|.
name|NameKey
argument_list|(
name|project
operator|.
name|getProject
argument_list|()
operator|.
name|getNameKey
argument_list|()
argument_list|,
literal|"master"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|value (int value, String text)
specifier|private
specifier|static
name|LabelValue
name|value
parameter_list|(
name|int
name|value
parameter_list|,
name|String
name|text
parameter_list|)
block|{
return|return
operator|new
name|LabelValue
argument_list|(
operator|(
name|short
operator|)
name|value
argument_list|,
name|text
argument_list|)
return|;
block|}
DECL|method|category (String id, String name, LabelValue... values)
specifier|private
specifier|static
name|LabelType
name|category
parameter_list|(
name|String
name|id
parameter_list|,
name|String
name|name
parameter_list|,
name|LabelValue
modifier|...
name|values
parameter_list|)
block|{
return|return
operator|new
name|LabelType
argument_list|(
name|id
argument_list|,
name|name
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|values
argument_list|)
argument_list|)
return|;
block|}
DECL|class|Projects
specifier|private
specifier|static
class|class
name|Projects
implements|implements
name|ProjectCache
block|{
DECL|field|project
specifier|private
specifier|final
name|ProjectState
name|project
decl_stmt|;
DECL|method|Projects (ProjectState project)
specifier|private
name|Projects
parameter_list|(
name|ProjectState
name|project
parameter_list|)
block|{
name|this
operator|.
name|project
operator|=
name|project
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAllProjects ()
specifier|public
name|ProjectState
name|getAllProjects
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|get (Project.NameKey projectName)
specifier|public
name|ProjectState
name|get
parameter_list|(
name|Project
operator|.
name|NameKey
name|projectName
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|project
operator|.
name|getProject
argument_list|()
operator|.
name|getNameKey
argument_list|()
argument_list|,
name|projectName
argument_list|)
expr_stmt|;
return|return
name|project
return|;
block|}
annotation|@
name|Override
DECL|method|evict (Project p)
specifier|public
name|void
name|evict
parameter_list|(
name|Project
name|p
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|remove (Project p)
specifier|public
name|void
name|remove
parameter_list|(
name|Project
name|p
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|all ()
specifier|public
name|Iterable
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
name|all
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|guessRelevantGroupUUIDs ()
specifier|public
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|guessRelevantGroupUUIDs
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|byName (String prefix)
specifier|public
name|Iterable
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
name|byName
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|onCreateProject (Project.NameKey newProjectName)
specifier|public
name|void
name|onCreateProject
parameter_list|(
name|Project
operator|.
name|NameKey
name|newProjectName
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
block|}
end_class

end_unit

