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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|truth
operator|.
name|Truth8
operator|.
name|assertThat
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
name|collect
operator|.
name|ImmutableList
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
name|LabelFunction
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
name|server
operator|.
name|config
operator|.
name|AllProjectsName
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
name|GitRepositoryManager
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
name|testing
operator|.
name|InMemoryDatabase
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
name|testing
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
name|gwtorm
operator|.
name|jdbc
operator|.
name|JdbcSchema
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
name|OrmException
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
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|Collections
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
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Repository
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
DECL|class|SchemaCreatorTest
specifier|public
class|class
name|SchemaCreatorTest
block|{
DECL|field|allProjects
annotation|@
name|Inject
specifier|private
name|AllProjectsName
name|allProjects
decl_stmt|;
DECL|field|repoManager
annotation|@
name|Inject
specifier|private
name|GitRepositoryManager
name|repoManager
decl_stmt|;
DECL|field|db
annotation|@
name|Inject
specifier|private
name|InMemoryDatabase
name|db
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
operator|new
name|InMemoryModule
argument_list|()
operator|.
name|inject
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|InMemoryDatabase
operator|.
name|drop
argument_list|(
name|db
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|getCauses_CreateSchema ()
specifier|public
name|void
name|getCauses_CreateSchema
parameter_list|()
throws|throws
name|OrmException
throws|,
name|SQLException
throws|,
name|IOException
block|{
comment|// Initially the schema should be empty.
name|String
index|[]
name|types
init|=
block|{
literal|"TABLE"
block|,
literal|"VIEW"
block|}
decl_stmt|;
try|try
init|(
name|JdbcSchema
name|d
init|=
operator|(
name|JdbcSchema
operator|)
name|db
operator|.
name|open
argument_list|()
init|;
name|ResultSet
name|rs
operator|=
name|d
operator|.
name|getConnection
argument_list|()
operator|.
name|getMetaData
argument_list|()
operator|.
name|getTables
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|types
argument_list|)
init|)
block|{
name|assertThat
argument_list|(
name|rs
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
block|}
comment|// Create the schema using the current schema version.
comment|//
name|db
operator|.
name|create
argument_list|()
expr_stmt|;
name|db
operator|.
name|assertSchemaVersion
argument_list|()
expr_stmt|;
comment|// By default sitePath is set to the current working directory.
comment|//
name|File
name|sitePath
init|=
operator|new
name|File
argument_list|(
literal|"."
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
decl_stmt|;
if|if
condition|(
name|sitePath
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"."
argument_list|)
condition|)
block|{
name|sitePath
operator|=
name|sitePath
operator|.
name|getParentFile
argument_list|()
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|db
operator|.
name|getSystemConfig
argument_list|()
operator|.
name|sitePath
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|sitePath
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getLabelTypes ()
specifier|private
name|LabelTypes
name|getLabelTypes
parameter_list|()
throws|throws
name|Exception
block|{
name|db
operator|.
name|create
argument_list|()
expr_stmt|;
name|ProjectConfig
name|c
init|=
operator|new
name|ProjectConfig
argument_list|(
name|allProjects
argument_list|)
decl_stmt|;
try|try
init|(
name|Repository
name|repo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|allProjects
argument_list|)
init|)
block|{
name|c
operator|.
name|load
argument_list|(
name|repo
argument_list|)
expr_stmt|;
return|return
operator|new
name|LabelTypes
argument_list|(
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|c
operator|.
name|getLabelSections
argument_list|()
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|createSchema_LabelTypes ()
specifier|public
name|void
name|createSchema_LabelTypes
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|labels
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|LabelType
name|label
range|:
name|getLabelTypes
argument_list|()
operator|.
name|getLabelTypes
argument_list|()
control|)
block|{
name|labels
operator|.
name|add
argument_list|(
name|label
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|labels
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|"Code-Review"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|createSchema_Label_CodeReview ()
specifier|public
name|void
name|createSchema_Label_CodeReview
parameter_list|()
throws|throws
name|Exception
block|{
name|LabelType
name|codeReview
init|=
name|getLabelTypes
argument_list|()
operator|.
name|byLabel
argument_list|(
literal|"Code-Review"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|codeReview
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|codeReview
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"Code-Review"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|codeReview
operator|.
name|getDefaultValue
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|codeReview
operator|.
name|getFunction
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|LabelFunction
operator|.
name|MAX_WITH_BLOCK
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|codeReview
operator|.
name|isCopyMinScore
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertValueRange
argument_list|(
name|codeReview
argument_list|,
operator|-
literal|2
argument_list|,
operator|-
literal|1
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
DECL|method|assertValueRange (LabelType label, Integer... range)
specifier|private
name|void
name|assertValueRange
parameter_list|(
name|LabelType
name|label
parameter_list|,
name|Integer
modifier|...
name|range
parameter_list|)
block|{
name|List
argument_list|<
name|Integer
argument_list|>
name|rangeList
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|range
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|rangeList
argument_list|)
operator|.
name|isNotEmpty
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|rangeList
argument_list|)
operator|.
name|isStrictlyOrdered
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|label
operator|.
name|getValues
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|v
lambda|->
operator|(
name|int
operator|)
name|v
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
operator|.
name|containsExactlyElementsIn
argument_list|(
name|rangeList
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|label
operator|.
name|getMax
argument_list|()
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|Collections
operator|.
name|max
argument_list|(
name|rangeList
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|label
operator|.
name|getMin
argument_list|()
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|Collections
operator|.
name|min
argument_list|(
name|rangeList
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|LabelValue
name|v
range|:
name|label
operator|.
name|getValues
argument_list|()
control|)
block|{
name|assertThat
argument_list|(
name|v
operator|.
name|getText
argument_list|()
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|v
operator|.
name|getText
argument_list|()
argument_list|)
operator|.
name|isNotEmpty
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

