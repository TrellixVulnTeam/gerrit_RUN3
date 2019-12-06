begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2019 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.fixes.fixCalculator
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|fixes
operator|.
name|fixCalculator
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|fixes
operator|.
name|testing
operator|.
name|FixResultSubject
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
name|gerrit
operator|.
name|server
operator|.
name|fixes
operator|.
name|testing
operator|.
name|GitEditSubject
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
name|gerrit
operator|.
name|testing
operator|.
name|GerritJUnit
operator|.
name|assertThrows
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
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
name|entities
operator|.
name|Comment
operator|.
name|Range
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
name|entities
operator|.
name|FixReplacement
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
name|restapi
operator|.
name|ResourceConflictException
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
name|fixes
operator|.
name|FixCalculator
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
name|fixes
operator|.
name|FixCalculator
operator|.
name|FixResult
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
name|patch
operator|.
name|Text
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
name|diff
operator|.
name|Edit
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
DECL|class|FixCalculatorVariousTest
specifier|public
class|class
name|FixCalculatorVariousTest
block|{
DECL|field|multilineContentString
specifier|private
specifier|static
specifier|final
name|String
name|multilineContentString
init|=
literal|"First line\nSecond line\nThird line\nFourth line\nFifth line\n"
decl_stmt|;
DECL|field|multilineContent
specifier|private
specifier|static
specifier|final
name|Text
name|multilineContent
init|=
operator|new
name|Text
argument_list|(
name|multilineContentString
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
DECL|method|calculateFixSingleReplacement ( String content, int startLine, int startChar, int endLine, int endChar, String replacement)
specifier|public
specifier|static
name|FixResult
name|calculateFixSingleReplacement
parameter_list|(
name|String
name|content
parameter_list|,
name|int
name|startLine
parameter_list|,
name|int
name|startChar
parameter_list|,
name|int
name|endLine
parameter_list|,
name|int
name|endChar
parameter_list|,
name|String
name|replacement
parameter_list|)
throws|throws
name|ResourceConflictException
block|{
name|FixReplacement
name|fixReplacement
init|=
operator|new
name|FixReplacement
argument_list|(
literal|"AnyPath"
argument_list|,
operator|new
name|Range
argument_list|(
name|startLine
argument_list|,
name|startChar
argument_list|,
name|endLine
argument_list|,
name|endChar
argument_list|)
argument_list|,
name|replacement
argument_list|)
decl_stmt|;
return|return
name|FixCalculator
operator|.
name|calculateFix
argument_list|(
operator|new
name|Text
argument_list|(
name|content
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|fixReplacement
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|lineNumberMustBePositive ()
specifier|public
name|void
name|lineNumberMustBePositive
parameter_list|()
block|{
name|assertThrows
argument_list|(
name|ResourceConflictException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|calculateFixSingleReplacement
argument_list|(
literal|"First line\nSecond line"
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|"Abc"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|insertAtTheEndOfSingleLineContentHasEOLMarkInvalidPosition ()
specifier|public
name|void
name|insertAtTheEndOfSingleLineContentHasEOLMarkInvalidPosition
parameter_list|()
throws|throws
name|Exception
block|{
name|assertThrows
argument_list|(
name|ResourceConflictException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|calculateFixSingleReplacement
argument_list|(
literal|"First line\n"
argument_list|,
literal|1
argument_list|,
literal|11
argument_list|,
literal|1
argument_list|,
literal|11
argument_list|,
literal|"Abc"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|severalChangesInTheSameLineNonSorted ()
specifier|public
name|void
name|severalChangesInTheSameLineNonSorted
parameter_list|()
throws|throws
name|Exception
block|{
name|FixReplacement
name|replace
init|=
operator|new
name|FixReplacement
argument_list|(
literal|"path"
argument_list|,
operator|new
name|Range
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|)
argument_list|,
literal|"ABC"
argument_list|)
decl_stmt|;
name|FixReplacement
name|insert
init|=
operator|new
name|FixReplacement
argument_list|(
literal|"path"
argument_list|,
operator|new
name|Range
argument_list|(
literal|2
argument_list|,
literal|5
argument_list|,
literal|2
argument_list|,
literal|5
argument_list|)
argument_list|,
literal|"DEFG"
argument_list|)
decl_stmt|;
name|FixReplacement
name|delete
init|=
operator|new
name|FixReplacement
argument_list|(
literal|"path"
argument_list|,
operator|new
name|Range
argument_list|(
literal|2
argument_list|,
literal|7
argument_list|,
literal|2
argument_list|,
literal|9
argument_list|)
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|FixResult
name|result
init|=
name|FixCalculator
operator|.
name|calculateFix
argument_list|(
name|multilineContent
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|replace
argument_list|,
name|delete
argument_list|,
name|insert
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|result
argument_list|)
operator|.
name|text
argument_list|()
operator|.
name|isEqualTo
argument_list|(
literal|"First line\nSABConDEFGd ne\nThird line\nFourth line\nFifth line\n"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
argument_list|)
operator|.
name|edits
argument_list|()
operator|.
name|hasSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|Edit
name|edit
init|=
name|result
operator|.
name|edits
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|edit
argument_list|)
operator|.
name|isReplace
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|edit
argument_list|)
operator|.
name|internalEdits
argument_list|()
operator|.
name|hasSize
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|edit
argument_list|)
operator|.
name|internalEdits
argument_list|()
operator|.
name|element
argument_list|(
literal|0
argument_list|)
operator|.
name|isReplace
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|edit
argument_list|)
operator|.
name|internalEdits
argument_list|()
operator|.
name|element
argument_list|(
literal|1
argument_list|)
operator|.
name|isInsert
argument_list|(
literal|5
argument_list|,
literal|6
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|edit
argument_list|)
operator|.
name|internalEdits
argument_list|()
operator|.
name|element
argument_list|(
literal|2
argument_list|)
operator|.
name|isDelete
argument_list|(
literal|7
argument_list|,
literal|2
argument_list|,
literal|12
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|severalChangesInConsecutiveLines ()
specifier|public
name|void
name|severalChangesInConsecutiveLines
parameter_list|()
throws|throws
name|Exception
block|{
name|FixReplacement
name|replace
init|=
operator|new
name|FixReplacement
argument_list|(
literal|"path"
argument_list|,
operator|new
name|Range
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|)
argument_list|,
literal|"ABC"
argument_list|)
decl_stmt|;
name|FixReplacement
name|insert
init|=
operator|new
name|FixReplacement
argument_list|(
literal|"path"
argument_list|,
operator|new
name|Range
argument_list|(
literal|3
argument_list|,
literal|5
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|)
argument_list|,
literal|"DEFG"
argument_list|)
decl_stmt|;
name|FixReplacement
name|delete
init|=
operator|new
name|FixReplacement
argument_list|(
literal|"path"
argument_list|,
operator|new
name|Range
argument_list|(
literal|4
argument_list|,
literal|7
argument_list|,
literal|4
argument_list|,
literal|9
argument_list|)
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|FixResult
name|result
init|=
name|FixCalculator
operator|.
name|calculateFix
argument_list|(
name|multilineContent
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|replace
argument_list|,
name|insert
argument_list|,
name|delete
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|result
argument_list|)
operator|.
name|text
argument_list|()
operator|.
name|isEqualTo
argument_list|(
literal|"First line\nSABCond line\nThirdDEFG line\nFourth ne\nFifth line\n"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
argument_list|)
operator|.
name|edits
argument_list|()
operator|.
name|hasSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|Edit
name|edit
init|=
name|result
operator|.
name|edits
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|edit
argument_list|)
operator|.
name|isReplace
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|edit
argument_list|)
operator|.
name|internalEdits
argument_list|()
operator|.
name|hasSize
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|edit
argument_list|)
operator|.
name|internalEdits
argument_list|()
operator|.
name|element
argument_list|(
literal|0
argument_list|)
operator|.
name|isReplace
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|edit
argument_list|)
operator|.
name|internalEdits
argument_list|()
operator|.
name|element
argument_list|(
literal|1
argument_list|)
operator|.
name|isInsert
argument_list|(
literal|17
argument_list|,
literal|18
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|edit
argument_list|)
operator|.
name|internalEdits
argument_list|()
operator|.
name|element
argument_list|(
literal|2
argument_list|)
operator|.
name|isDelete
argument_list|(
literal|30
argument_list|,
literal|2
argument_list|,
literal|35
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|severalChangesInNonConsecutiveLines ()
specifier|public
name|void
name|severalChangesInNonConsecutiveLines
parameter_list|()
throws|throws
name|Exception
block|{
name|FixReplacement
name|replace
init|=
operator|new
name|FixReplacement
argument_list|(
literal|"path"
argument_list|,
operator|new
name|Range
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|)
argument_list|,
literal|"ABC"
argument_list|)
decl_stmt|;
name|FixReplacement
name|insert
init|=
operator|new
name|FixReplacement
argument_list|(
literal|"path"
argument_list|,
operator|new
name|Range
argument_list|(
literal|3
argument_list|,
literal|5
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|)
argument_list|,
literal|"DEFG"
argument_list|)
decl_stmt|;
name|FixReplacement
name|delete
init|=
operator|new
name|FixReplacement
argument_list|(
literal|"path"
argument_list|,
operator|new
name|Range
argument_list|(
literal|5
argument_list|,
literal|9
argument_list|,
literal|6
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|FixResult
name|result
init|=
name|FixCalculator
operator|.
name|calculateFix
argument_list|(
name|multilineContent
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|replace
argument_list|,
name|insert
argument_list|,
name|delete
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|result
argument_list|)
operator|.
name|text
argument_list|()
operator|.
name|isEqualTo
argument_list|(
literal|"FABCst line\nSecond line\nThirdDEFG line\nFourth line\nFifth lin"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
argument_list|)
operator|.
name|edits
argument_list|()
operator|.
name|hasSize
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
argument_list|)
operator|.
name|edits
argument_list|()
operator|.
name|element
argument_list|(
literal|0
argument_list|)
operator|.
name|isReplace
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
argument_list|)
operator|.
name|edits
argument_list|()
operator|.
name|element
argument_list|(
literal|0
argument_list|)
operator|.
name|internalEdits
argument_list|()
operator|.
name|onlyElement
argument_list|()
operator|.
name|isReplace
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
argument_list|)
operator|.
name|edits
argument_list|()
operator|.
name|element
argument_list|(
literal|1
argument_list|)
operator|.
name|isReplace
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
argument_list|)
operator|.
name|edits
argument_list|()
operator|.
name|element
argument_list|(
literal|1
argument_list|)
operator|.
name|internalEdits
argument_list|()
operator|.
name|onlyElement
argument_list|()
operator|.
name|isInsert
argument_list|(
literal|5
argument_list|,
literal|5
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
argument_list|)
operator|.
name|edits
argument_list|()
operator|.
name|element
argument_list|(
literal|2
argument_list|)
operator|.
name|isReplace
argument_list|(
literal|4
argument_list|,
literal|1
argument_list|,
literal|4
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
argument_list|)
operator|.
name|edits
argument_list|()
operator|.
name|element
argument_list|(
literal|2
argument_list|)
operator|.
name|internalEdits
argument_list|()
operator|.
name|onlyElement
argument_list|()
operator|.
name|isDelete
argument_list|(
literal|9
argument_list|,
literal|2
argument_list|,
literal|9
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|multipleChanges ()
specifier|public
name|void
name|multipleChanges
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|str
init|=
literal|"First line\nSecond line\nThird line\nFourth line\nFifth line\nSixth line"
operator|+
literal|"\nSeventh line\nEighth line\nNinth line\nTenth line\n"
decl_stmt|;
name|Text
name|content
init|=
operator|new
name|Text
argument_list|(
name|str
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|FixReplacement
name|multiLineReplace
init|=
operator|new
name|FixReplacement
argument_list|(
literal|"path"
argument_list|,
operator|new
name|Range
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|,
literal|3
argument_list|)
argument_list|,
literal|"AB\nC\nDEFG\nQ\n"
argument_list|)
decl_stmt|;
name|FixReplacement
name|multiLineDelete
init|=
operator|new
name|FixReplacement
argument_list|(
literal|"path"
argument_list|,
operator|new
name|Range
argument_list|(
literal|4
argument_list|,
literal|8
argument_list|,
literal|5
argument_list|,
literal|8
argument_list|)
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|FixReplacement
name|singleLineInsert
init|=
operator|new
name|FixReplacement
argument_list|(
literal|"path"
argument_list|,
operator|new
name|Range
argument_list|(
literal|5
argument_list|,
literal|10
argument_list|,
literal|5
argument_list|,
literal|10
argument_list|)
argument_list|,
literal|"QWERTY"
argument_list|)
decl_stmt|;
name|FixReplacement
name|singleLineReplace
init|=
operator|new
name|FixReplacement
argument_list|(
literal|"path"
argument_list|,
operator|new
name|Range
argument_list|(
literal|7
argument_list|,
literal|3
argument_list|,
literal|7
argument_list|,
literal|7
argument_list|)
argument_list|,
literal|"XY"
argument_list|)
decl_stmt|;
name|FixReplacement
name|multiLineInsert
init|=
operator|new
name|FixReplacement
argument_list|(
literal|"path"
argument_list|,
operator|new
name|Range
argument_list|(
literal|8
argument_list|,
literal|7
argument_list|,
literal|8
argument_list|,
literal|7
argument_list|)
argument_list|,
literal|"KLMNO\nASDF"
argument_list|)
decl_stmt|;
name|FixReplacement
name|singleLineDelete
init|=
operator|new
name|FixReplacement
argument_list|(
literal|"path"
argument_list|,
operator|new
name|Range
argument_list|(
literal|10
argument_list|,
literal|3
argument_list|,
literal|10
argument_list|,
literal|7
argument_list|)
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|FixResult
name|result
init|=
name|FixCalculator
operator|.
name|calculateFix
argument_list|(
name|content
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|multiLineReplace
argument_list|,
name|multiLineDelete
argument_list|,
name|singleLineInsert
argument_list|,
name|singleLineReplace
argument_list|,
name|multiLineInsert
argument_list|,
name|singleLineDelete
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|result
argument_list|)
operator|.
name|text
argument_list|()
operator|.
name|isEqualTo
argument_list|(
literal|"FiAB\nC\nDEFG\nQ\nrd line\nFourth lneQWERTY\nSixth line\nSevXY line\nEighth KLMNO\nASDFline\nNinth line\nTenine\n"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
argument_list|)
operator|.
name|edits
argument_list|()
operator|.
name|hasSize
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
argument_list|)
operator|.
name|edits
argument_list|()
operator|.
name|element
argument_list|(
literal|0
argument_list|)
operator|.
name|isReplace
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|,
literal|0
argument_list|,
literal|6
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
argument_list|)
operator|.
name|edits
argument_list|()
operator|.
name|element
argument_list|(
literal|0
argument_list|)
operator|.
name|internalEdits
argument_list|()
operator|.
name|containsExactly
argument_list|(
operator|new
name|Edit
argument_list|(
literal|2
argument_list|,
literal|26
argument_list|,
literal|2
argument_list|,
literal|14
argument_list|)
argument_list|,
operator|new
name|Edit
argument_list|(
literal|42
argument_list|,
literal|54
argument_list|,
literal|30
argument_list|,
literal|30
argument_list|)
argument_list|,
operator|new
name|Edit
argument_list|(
literal|56
argument_list|,
literal|56
argument_list|,
literal|32
argument_list|,
literal|38
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
argument_list|)
operator|.
name|edits
argument_list|()
operator|.
name|element
argument_list|(
literal|1
argument_list|)
operator|.
name|isReplace
argument_list|(
literal|6
argument_list|,
literal|2
argument_list|,
literal|7
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
argument_list|)
operator|.
name|edits
argument_list|()
operator|.
name|element
argument_list|(
literal|1
argument_list|)
operator|.
name|internalEdits
argument_list|()
operator|.
name|containsExactly
argument_list|(
operator|new
name|Edit
argument_list|(
literal|3
argument_list|,
literal|7
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|)
argument_list|,
operator|new
name|Edit
argument_list|(
literal|20
argument_list|,
literal|20
argument_list|,
literal|18
argument_list|,
literal|28
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
argument_list|)
operator|.
name|edits
argument_list|()
operator|.
name|element
argument_list|(
literal|2
argument_list|)
operator|.
name|isReplace
argument_list|(
literal|9
argument_list|,
literal|1
argument_list|,
literal|11
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
argument_list|)
operator|.
name|edits
argument_list|()
operator|.
name|element
argument_list|(
literal|2
argument_list|)
operator|.
name|internalEdits
argument_list|()
operator|.
name|onlyElement
argument_list|()
operator|.
name|isDelete
argument_list|(
literal|3
argument_list|,
literal|4
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

