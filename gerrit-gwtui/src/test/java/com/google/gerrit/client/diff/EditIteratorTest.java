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
DECL|package|com.google.gerrit.client.diff
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|diff
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
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
name|client
operator|.
name|diff
operator|.
name|CodeMirrorDemo
operator|.
name|EditIterator
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|core
operator|.
name|client
operator|.
name|JavaScriptObject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|core
operator|.
name|client
operator|.
name|JsArrayString
import|;
end_import

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|gwt
operator|.
name|test
operator|.
name|GwtModule
import|;
end_import

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|gwt
operator|.
name|test
operator|.
name|GwtTest
import|;
end_import

begin_import
import|import
name|net
operator|.
name|codemirror
operator|.
name|lib
operator|.
name|LineCharacter
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

begin_comment
comment|/** Unit tests for EditIterator */
end_comment

begin_class
annotation|@
name|GwtModule
argument_list|(
literal|"com.google.gerrit.GerritGwtUI"
argument_list|)
DECL|class|EditIteratorTest
specifier|public
class|class
name|EditIteratorTest
extends|extends
name|GwtTest
block|{
DECL|field|lines
specifier|private
name|JsArrayString
name|lines
decl_stmt|;
DECL|method|assertLineChsEqual (LineCharacter a, LineCharacter b)
specifier|private
name|void
name|assertLineChsEqual
parameter_list|(
name|LineCharacter
name|a
parameter_list|,
name|LineCharacter
name|b
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|a
operator|.
name|getLine
argument_list|()
operator|+
literal|","
operator|+
name|a
operator|.
name|getCh
argument_list|()
argument_list|,
name|b
operator|.
name|getLine
argument_list|()
operator|+
literal|","
operator|+
name|b
operator|.
name|getCh
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|initialize ()
specifier|public
name|void
name|initialize
parameter_list|()
block|{
name|lines
operator|=
operator|(
name|JsArrayString
operator|)
name|JavaScriptObject
operator|.
name|createArray
argument_list|()
expr_stmt|;
name|lines
operator|.
name|push
argument_list|(
literal|"1st"
argument_list|)
expr_stmt|;
name|lines
operator|.
name|push
argument_list|(
literal|"2nd"
argument_list|)
expr_stmt|;
name|lines
operator|.
name|push
argument_list|(
literal|"3rd"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoAdvance ()
specifier|public
name|void
name|testNoAdvance
parameter_list|()
block|{
name|EditIterator
name|iter
init|=
operator|new
name|EditIterator
argument_list|(
name|lines
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertLineChsEqual
argument_list|(
name|LineCharacter
operator|.
name|create
argument_list|(
literal|0
argument_list|)
argument_list|,
name|iter
operator|.
name|advance
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSimpleAdvance ()
specifier|public
name|void
name|testSimpleAdvance
parameter_list|()
block|{
name|EditIterator
name|iter
init|=
operator|new
name|EditIterator
argument_list|(
name|lines
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertLineChsEqual
argument_list|(
name|LineCharacter
operator|.
name|create
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|,
name|iter
operator|.
name|advance
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEndsBeforeNewline ()
specifier|public
name|void
name|testEndsBeforeNewline
parameter_list|()
block|{
name|EditIterator
name|iter
init|=
operator|new
name|EditIterator
argument_list|(
name|lines
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertLineChsEqual
argument_list|(
name|LineCharacter
operator|.
name|create
argument_list|(
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|,
name|iter
operator|.
name|advance
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEndsOnNewline ()
specifier|public
name|void
name|testEndsOnNewline
parameter_list|()
block|{
name|EditIterator
name|iter
init|=
operator|new
name|EditIterator
argument_list|(
name|lines
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertLineChsEqual
argument_list|(
name|LineCharacter
operator|.
name|create
argument_list|(
literal|1
argument_list|)
argument_list|,
name|iter
operator|.
name|advance
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAcrossNewline ()
specifier|public
name|void
name|testAcrossNewline
parameter_list|()
block|{
name|EditIterator
name|iter
init|=
operator|new
name|EditIterator
argument_list|(
name|lines
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertLineChsEqual
argument_list|(
name|LineCharacter
operator|.
name|create
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|,
name|iter
operator|.
name|advance
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testContinueFromBeforeNewline ()
specifier|public
name|void
name|testContinueFromBeforeNewline
parameter_list|()
block|{
name|EditIterator
name|iter
init|=
operator|new
name|EditIterator
argument_list|(
name|lines
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|iter
operator|.
name|advance
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|assertLineChsEqual
argument_list|(
name|LineCharacter
operator|.
name|create
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|,
name|iter
operator|.
name|advance
argument_list|(
literal|7
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testContinueFromAfterNewline ()
specifier|public
name|void
name|testContinueFromAfterNewline
parameter_list|()
block|{
name|EditIterator
name|iter
init|=
operator|new
name|EditIterator
argument_list|(
name|lines
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|iter
operator|.
name|advance
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|assertLineChsEqual
argument_list|(
name|LineCharacter
operator|.
name|create
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|,
name|iter
operator|.
name|advance
argument_list|(
literal|6
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAcrossMultipleLines ()
specifier|public
name|void
name|testAcrossMultipleLines
parameter_list|()
block|{
name|EditIterator
name|iter
init|=
operator|new
name|EditIterator
argument_list|(
name|lines
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertLineChsEqual
argument_list|(
name|LineCharacter
operator|.
name|create
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|,
name|iter
operator|.
name|advance
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

