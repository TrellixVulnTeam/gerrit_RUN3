begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2016 The Android Open Source Project
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
DECL|package|com.google.gerrit.mail
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|mail
package|;
end_package

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
name|Splitter
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
name|Iterables
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
name|java
operator|.
name|util
operator|.
name|StringJoiner
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_class
DECL|class|ParserUtil
specifier|public
class|class
name|ParserUtil
block|{
DECL|field|SIMPLE_EMAIL_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|SIMPLE_EMAIL_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+"
operator|+
literal|"(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})"
argument_list|)
decl_stmt|;
DECL|method|ParserUtil ()
specifier|private
name|ParserUtil
parameter_list|()
block|{}
comment|/**    * Trims the quotation that email clients add Example: On Sun, Nov 20, 2016 at 10:33 PM,    *<gerrit@gerritcodereview.com> wrote:    *    * @param comment Comment parsed from an email.    * @return Trimmed comment.    */
DECL|method|trimQuotation (String comment)
specifier|public
specifier|static
name|String
name|trimQuotation
parameter_list|(
name|String
name|comment
parameter_list|)
block|{
name|StringJoiner
name|j
init|=
operator|new
name|StringJoiner
argument_list|(
literal|"\n"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|lines
init|=
name|Splitter
operator|.
name|on
argument_list|(
literal|'\n'
argument_list|)
operator|.
name|splitToList
argument_list|(
name|comment
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|lines
operator|.
name|size
argument_list|()
operator|-
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|j
operator|.
name|add
argument_list|(
name|lines
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Check if the last line contains the full quotation pattern (date + email)
name|String
name|lastLine
init|=
name|lines
operator|.
name|get
argument_list|(
name|lines
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|containsQuotationPattern
argument_list|(
name|lastLine
argument_list|)
condition|)
block|{
if|if
condition|(
name|lines
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|j
operator|.
name|add
argument_list|(
name|lines
operator|.
name|get
argument_list|(
name|lines
operator|.
name|size
argument_list|()
operator|-
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|j
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
return|;
block|}
comment|// Check if the second last line + the last line contain the full quotation pattern. This is
comment|// necessary, as the quotation line can be split across the last two lines if it gets too long.
if|if
condition|(
name|lines
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|String
name|lastLines
init|=
name|lines
operator|.
name|get
argument_list|(
name|lines
operator|.
name|size
argument_list|()
operator|-
literal|2
argument_list|)
operator|+
name|lastLine
decl_stmt|;
if|if
condition|(
name|containsQuotationPattern
argument_list|(
name|lastLines
argument_list|)
condition|)
block|{
return|return
name|j
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
return|;
block|}
block|}
comment|// Add the last two lines
if|if
condition|(
name|lines
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|j
operator|.
name|add
argument_list|(
name|lines
operator|.
name|get
argument_list|(
name|lines
operator|.
name|size
argument_list|()
operator|-
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|j
operator|.
name|add
argument_list|(
name|lines
operator|.
name|get
argument_list|(
name|lines
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|j
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
return|;
block|}
comment|/** Check if string is an inline comment url on a patch set or the base */
DECL|method|isCommentUrl (String str, String changeUrl, Comment comment)
specifier|public
specifier|static
name|boolean
name|isCommentUrl
parameter_list|(
name|String
name|str
parameter_list|,
name|String
name|changeUrl
parameter_list|,
name|Comment
name|comment
parameter_list|)
block|{
name|int
name|lineNbr
init|=
name|comment
operator|.
name|range
operator|==
literal|null
condition|?
name|comment
operator|.
name|lineNbr
else|:
name|comment
operator|.
name|range
operator|.
name|startLine
decl_stmt|;
return|return
name|str
operator|.
name|equals
argument_list|(
name|filePath
argument_list|(
name|changeUrl
argument_list|,
name|comment
argument_list|)
operator|+
literal|"@"
operator|+
name|lineNbr
argument_list|)
operator|||
name|str
operator|.
name|equals
argument_list|(
name|filePath
argument_list|(
name|changeUrl
argument_list|,
name|comment
argument_list|)
operator|+
literal|"@a"
operator|+
name|lineNbr
argument_list|)
return|;
block|}
comment|/** Generate the fully qualified filepath */
DECL|method|filePath (String changeUrl, Comment comment)
specifier|public
specifier|static
name|String
name|filePath
parameter_list|(
name|String
name|changeUrl
parameter_list|,
name|Comment
name|comment
parameter_list|)
block|{
return|return
name|changeUrl
operator|+
literal|"/"
operator|+
name|comment
operator|.
name|key
operator|.
name|patchSetId
operator|+
literal|"/"
operator|+
name|comment
operator|.
name|key
operator|.
name|filename
return|;
block|}
comment|/**    * When parsing mail content, we need to append comments prematurely since we are parsing    * block-by-block and never know what comes next. This can result in a comment being parsed as two    * comments when it spans multiple blocks. This method takes care of merging those blocks or    * adding a new comment to the list of appropriate.    */
DECL|method|appendOrAddNewComment (MailComment comment, List<MailComment> comments)
specifier|public
specifier|static
name|void
name|appendOrAddNewComment
parameter_list|(
name|MailComment
name|comment
parameter_list|,
name|List
argument_list|<
name|MailComment
argument_list|>
name|comments
parameter_list|)
block|{
if|if
condition|(
name|comments
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|comments
operator|.
name|add
argument_list|(
name|comment
argument_list|)
expr_stmt|;
return|return;
block|}
name|MailComment
name|lastComment
init|=
name|Iterables
operator|.
name|getLast
argument_list|(
name|comments
argument_list|)
decl_stmt|;
if|if
condition|(
name|comment
operator|.
name|isSameCommentPath
argument_list|(
name|lastComment
argument_list|)
condition|)
block|{
comment|// Merge the two comments. Links should just be appended, while regular text that came from
comment|// different<div> elements should be separated by a paragraph.
name|lastComment
operator|.
name|message
operator|+=
operator|(
name|comment
operator|.
name|isLink
condition|?
literal|" "
else|:
literal|"\n\n"
operator|)
operator|+
name|comment
operator|.
name|message
expr_stmt|;
return|return;
block|}
name|comments
operator|.
name|add
argument_list|(
name|comment
argument_list|)
expr_stmt|;
block|}
DECL|method|containsQuotationPattern (String s)
specifier|private
specifier|static
name|boolean
name|containsQuotationPattern
parameter_list|(
name|String
name|s
parameter_list|)
block|{
comment|// Identifying the quotation line is hard, as it can be in any language.
comment|// We identify this line by it's characteristics: It usually contains a
comment|// valid email address, some digits for the date in groups of 1-4 in a row
comment|// as well as some characters.
comment|// Count occurrences of digit groups
name|int
name|numConsecutiveDigits
init|=
literal|0
decl_stmt|;
name|int
name|maxConsecutiveDigits
init|=
literal|0
decl_stmt|;
name|int
name|numDigitGroups
init|=
literal|0
decl_stmt|;
for|for
control|(
name|char
name|c
range|:
name|s
operator|.
name|toCharArray
argument_list|()
control|)
block|{
if|if
condition|(
name|c
operator|>=
literal|'0'
operator|&&
name|c
operator|<=
literal|'9'
condition|)
block|{
name|numConsecutiveDigits
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|numConsecutiveDigits
operator|>
literal|0
condition|)
block|{
name|maxConsecutiveDigits
operator|=
name|Integer
operator|.
name|max
argument_list|(
name|maxConsecutiveDigits
argument_list|,
name|numConsecutiveDigits
argument_list|)
expr_stmt|;
name|numConsecutiveDigits
operator|=
literal|0
expr_stmt|;
name|numDigitGroups
operator|++
expr_stmt|;
block|}
block|}
if|if
condition|(
name|numDigitGroups
argument_list|<
literal|4
operator|||
name|maxConsecutiveDigits
argument_list|>
literal|4
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// Check if the string contains an email address
return|return
name|SIMPLE_EMAIL_PATTERN
operator|.
name|matcher
argument_list|(
name|s
argument_list|)
operator|.
name|find
argument_list|()
return|;
block|}
block|}
end_class

end_unit

