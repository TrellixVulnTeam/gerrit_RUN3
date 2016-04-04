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
DECL|package|com.google.gerrit.acceptance.rest.account
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|rest
operator|.
name|account
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
name|RestResponse
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
name|client
operator|.
name|EditPreferencesInfo
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
name|client
operator|.
name|KeyMapType
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
name|client
operator|.
name|Theme
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
DECL|class|EditPreferencesIT
specifier|public
class|class
name|EditPreferencesIT
extends|extends
name|AbstractDaemonTest
block|{
annotation|@
name|Test
DECL|method|getSetEditPreferences ()
specifier|public
name|void
name|getSetEditPreferences
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|endPoint
init|=
literal|"/accounts/"
operator|+
name|admin
operator|.
name|email
operator|+
literal|"/preferences.edit"
decl_stmt|;
name|RestResponse
name|r
init|=
name|adminSession
operator|.
name|get
argument_list|(
name|endPoint
argument_list|)
decl_stmt|;
name|r
operator|.
name|assertOK
argument_list|()
expr_stmt|;
name|EditPreferencesInfo
name|out
init|=
name|getEditPrefInfo
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|lineLength
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|tabSize
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|8
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|cursorBlinkRate
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|hideTopMenu
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|showTabs
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|showWhitespaceErrors
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|syntaxHighlighting
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|hideLineNumbers
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|matchBrackets
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|autoCloseBrackets
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|theme
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|Theme
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|keyMapType
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|KeyMapType
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
comment|// change some default values
name|out
operator|.
name|lineLength
operator|=
literal|80
expr_stmt|;
name|out
operator|.
name|tabSize
operator|=
literal|4
expr_stmt|;
name|out
operator|.
name|cursorBlinkRate
operator|=
literal|500
expr_stmt|;
name|out
operator|.
name|hideTopMenu
operator|=
literal|true
expr_stmt|;
name|out
operator|.
name|showTabs
operator|=
literal|false
expr_stmt|;
name|out
operator|.
name|showWhitespaceErrors
operator|=
literal|true
expr_stmt|;
name|out
operator|.
name|syntaxHighlighting
operator|=
literal|false
expr_stmt|;
name|out
operator|.
name|hideLineNumbers
operator|=
literal|true
expr_stmt|;
name|out
operator|.
name|matchBrackets
operator|=
literal|false
expr_stmt|;
name|out
operator|.
name|autoCloseBrackets
operator|=
literal|true
expr_stmt|;
name|out
operator|.
name|theme
operator|=
name|Theme
operator|.
name|TWILIGHT
expr_stmt|;
name|out
operator|.
name|keyMapType
operator|=
name|KeyMapType
operator|.
name|EMACS
expr_stmt|;
name|r
operator|=
name|adminSession
operator|.
name|put
argument_list|(
name|endPoint
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|r
operator|.
name|assertOK
argument_list|()
expr_stmt|;
name|EditPreferencesInfo
name|info
init|=
name|getEditPrefInfo
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|assertEditPreferences
argument_list|(
name|info
argument_list|,
name|out
argument_list|)
expr_stmt|;
comment|// Partially filled input record
name|EditPreferencesInfo
name|in
init|=
operator|new
name|EditPreferencesInfo
argument_list|()
decl_stmt|;
name|in
operator|.
name|tabSize
operator|=
literal|42
expr_stmt|;
name|r
operator|=
name|adminSession
operator|.
name|put
argument_list|(
name|endPoint
argument_list|,
name|in
argument_list|)
expr_stmt|;
name|r
operator|.
name|assertOK
argument_list|()
expr_stmt|;
name|info
operator|=
name|getEditPrefInfo
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|out
operator|.
name|tabSize
operator|=
name|in
operator|.
name|tabSize
expr_stmt|;
name|assertEditPreferences
argument_list|(
name|info
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
DECL|method|getEditPrefInfo (RestResponse r)
specifier|private
name|EditPreferencesInfo
name|getEditPrefInfo
parameter_list|(
name|RestResponse
name|r
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|newGson
argument_list|()
operator|.
name|fromJson
argument_list|(
name|r
operator|.
name|getReader
argument_list|()
argument_list|,
name|EditPreferencesInfo
operator|.
name|class
argument_list|)
return|;
block|}
DECL|method|assertEditPreferences (EditPreferencesInfo out, EditPreferencesInfo in)
specifier|private
name|void
name|assertEditPreferences
parameter_list|(
name|EditPreferencesInfo
name|out
parameter_list|,
name|EditPreferencesInfo
name|in
parameter_list|)
throws|throws
name|Exception
block|{
name|assertThat
argument_list|(
name|out
operator|.
name|lineLength
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|in
operator|.
name|lineLength
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|tabSize
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|in
operator|.
name|tabSize
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|cursorBlinkRate
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|in
operator|.
name|cursorBlinkRate
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|hideTopMenu
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|in
operator|.
name|hideTopMenu
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|showTabs
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|showWhitespaceErrors
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|in
operator|.
name|showWhitespaceErrors
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|syntaxHighlighting
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|hideLineNumbers
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|in
operator|.
name|hideLineNumbers
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|matchBrackets
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|autoCloseBrackets
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|in
operator|.
name|autoCloseBrackets
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|theme
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|in
operator|.
name|theme
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|keyMapType
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|in
operator|.
name|keyMapType
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

