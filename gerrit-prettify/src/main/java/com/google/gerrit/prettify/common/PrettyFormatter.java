begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2010 The Android Open Source Project
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
DECL|package|com.google.gerrit.prettify.common
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|prettify
operator|.
name|common
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtexpui
operator|.
name|safehtml
operator|.
name|client
operator|.
name|SafeHtml
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtexpui
operator|.
name|safehtml
operator|.
name|client
operator|.
name|SafeHtmlBuilder
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
name|eclipse
operator|.
name|jgit
operator|.
name|diff
operator|.
name|ReplaceEdit
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
name|HashSet
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
name|Set
import|;
end_import

begin_class
DECL|class|PrettyFormatter
specifier|public
specifier|abstract
class|class
name|PrettyFormatter
implements|implements
name|SparseHtmlFile
block|{
DECL|class|EditFilter
specifier|public
specifier|static
specifier|abstract
class|class
name|EditFilter
block|{
DECL|method|getStyleName ()
specifier|abstract
name|String
name|getStyleName
parameter_list|()
function_decl|;
DECL|method|getBegin (Edit edit)
specifier|abstract
name|int
name|getBegin
parameter_list|(
name|Edit
name|edit
parameter_list|)
function_decl|;
DECL|method|getEnd (Edit edit)
specifier|abstract
name|int
name|getEnd
parameter_list|(
name|Edit
name|edit
parameter_list|)
function_decl|;
block|}
DECL|field|A
specifier|public
specifier|static
specifier|final
name|EditFilter
name|A
init|=
operator|new
name|EditFilter
argument_list|()
block|{
annotation|@
name|Override
name|String
name|getStyleName
parameter_list|()
block|{
return|return
literal|"wdd"
return|;
block|}
annotation|@
name|Override
name|int
name|getBegin
parameter_list|(
name|Edit
name|edit
parameter_list|)
block|{
return|return
name|edit
operator|.
name|getBeginA
argument_list|()
return|;
block|}
annotation|@
name|Override
name|int
name|getEnd
parameter_list|(
name|Edit
name|edit
parameter_list|)
block|{
return|return
name|edit
operator|.
name|getEndA
argument_list|()
return|;
block|}
block|}
decl_stmt|;
DECL|field|B
specifier|public
specifier|static
specifier|final
name|EditFilter
name|B
init|=
operator|new
name|EditFilter
argument_list|()
block|{
annotation|@
name|Override
name|String
name|getStyleName
parameter_list|()
block|{
return|return
literal|"wdi"
return|;
block|}
annotation|@
name|Override
name|int
name|getBegin
parameter_list|(
name|Edit
name|edit
parameter_list|)
block|{
return|return
name|edit
operator|.
name|getBeginB
argument_list|()
return|;
block|}
annotation|@
name|Override
name|int
name|getEnd
parameter_list|(
name|Edit
name|edit
parameter_list|)
block|{
return|return
name|edit
operator|.
name|getEndB
argument_list|()
return|;
block|}
block|}
decl_stmt|;
DECL|field|content
specifier|protected
name|SparseFileContent
name|content
decl_stmt|;
DECL|field|side
specifier|protected
name|EditFilter
name|side
decl_stmt|;
DECL|field|edits
specifier|protected
name|List
argument_list|<
name|Edit
argument_list|>
name|edits
decl_stmt|;
DECL|field|settings
specifier|protected
name|PrettySettings
name|settings
decl_stmt|;
DECL|field|trailingEdits
specifier|protected
name|Set
argument_list|<
name|Integer
argument_list|>
name|trailingEdits
decl_stmt|;
DECL|field|col
specifier|private
name|int
name|col
decl_stmt|;
DECL|field|lineIdx
specifier|private
name|int
name|lineIdx
decl_stmt|;
DECL|field|lastTag
specifier|private
name|Tag
name|lastTag
decl_stmt|;
DECL|field|buf
specifier|private
name|StringBuilder
name|buf
decl_stmt|;
DECL|method|getSafeHtmlLine (int lineNo)
specifier|public
name|SafeHtml
name|getSafeHtmlLine
parameter_list|(
name|int
name|lineNo
parameter_list|)
block|{
return|return
name|SafeHtml
operator|.
name|asis
argument_list|(
name|content
operator|.
name|get
argument_list|(
name|lineNo
argument_list|)
argument_list|)
return|;
block|}
DECL|method|size ()
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|content
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|contains (int idx)
specifier|public
name|boolean
name|contains
parameter_list|(
name|int
name|idx
parameter_list|)
block|{
return|return
name|content
operator|.
name|contains
argument_list|(
name|idx
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hasTrailingEdit (int idx)
specifier|public
name|boolean
name|hasTrailingEdit
parameter_list|(
name|int
name|idx
parameter_list|)
block|{
return|return
name|trailingEdits
operator|.
name|contains
argument_list|(
name|idx
argument_list|)
return|;
block|}
DECL|method|setEditFilter (EditFilter f)
specifier|public
name|void
name|setEditFilter
parameter_list|(
name|EditFilter
name|f
parameter_list|)
block|{
name|side
operator|=
name|f
expr_stmt|;
block|}
DECL|method|setEditList (List<Edit> all)
specifier|public
name|void
name|setEditList
parameter_list|(
name|List
argument_list|<
name|Edit
argument_list|>
name|all
parameter_list|)
block|{
name|edits
operator|=
name|all
expr_stmt|;
block|}
DECL|method|setPrettySettings (PrettySettings how)
specifier|public
name|void
name|setPrettySettings
parameter_list|(
name|PrettySettings
name|how
parameter_list|)
block|{
name|settings
operator|=
name|how
expr_stmt|;
block|}
comment|/**    * Parse and format a complete source code file.    *    * @param src raw content of the file to format. The line strings will be HTML    *        escaped before processing, so it must be the raw text.    */
DECL|method|format (SparseFileContent src)
specifier|public
name|void
name|format
parameter_list|(
name|SparseFileContent
name|src
parameter_list|)
block|{
name|content
operator|=
operator|new
name|SparseFileContent
argument_list|()
expr_stmt|;
name|content
operator|.
name|setSize
argument_list|(
name|src
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|trailingEdits
operator|=
operator|new
name|HashSet
argument_list|<
name|Integer
argument_list|>
argument_list|()
expr_stmt|;
name|String
name|html
init|=
name|toHTML
argument_list|(
name|src
argument_list|)
decl_stmt|;
if|if
condition|(
name|settings
operator|.
name|isSyntaxHighlighting
argument_list|()
operator|&&
name|getFileType
argument_list|()
operator|!=
literal|null
operator|&&
name|src
operator|.
name|isWholeFile
argument_list|()
condition|)
block|{
comment|// The prettify parsers don't like&#39; as an entity for the
comment|// single quote character. Replace them all out so we don't
comment|// confuse the parser.
comment|//
name|html
operator|=
name|html
operator|.
name|replaceAll
argument_list|(
literal|"&#39;"
argument_list|,
literal|"'"
argument_list|)
expr_stmt|;
name|html
operator|=
name|prettify
argument_list|(
name|html
argument_list|,
name|getFileType
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|html
operator|=
name|expandTabs
argument_list|(
name|html
argument_list|)
expr_stmt|;
name|html
operator|=
name|html
operator|.
name|replaceAll
argument_list|(
literal|"\n"
argument_list|,
literal|"<br />"
argument_list|)
expr_stmt|;
block|}
name|int
name|pos
init|=
literal|0
decl_stmt|;
name|int
name|textChunkStart
init|=
literal|0
decl_stmt|;
name|lastTag
operator|=
name|Tag
operator|.
name|NULL
expr_stmt|;
name|col
operator|=
literal|0
expr_stmt|;
name|lineIdx
operator|=
literal|0
expr_stmt|;
name|buf
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
while|while
condition|(
name|pos
operator|<=
name|html
operator|.
name|length
argument_list|()
condition|)
block|{
name|int
name|tagStart
init|=
name|html
operator|.
name|indexOf
argument_list|(
literal|'<'
argument_list|,
name|pos
argument_list|)
decl_stmt|;
if|if
condition|(
name|tagStart
operator|<
literal|0
condition|)
block|{
comment|// No more tags remaining. What's left is plain text.
comment|//
assert|assert
name|lastTag
operator|==
name|Tag
operator|.
name|NULL
assert|;
name|pos
operator|=
name|html
operator|.
name|length
argument_list|()
expr_stmt|;
if|if
condition|(
name|textChunkStart
operator|<
name|pos
condition|)
block|{
name|htmlText
argument_list|(
name|html
operator|.
name|substring
argument_list|(
name|textChunkStart
argument_list|,
name|pos
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|0
operator|<
name|buf
operator|.
name|length
argument_list|()
condition|)
block|{
name|content
operator|.
name|addLine
argument_list|(
name|src
operator|.
name|mapIndexToLine
argument_list|(
name|lineIdx
argument_list|)
argument_list|,
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
comment|// Assume no attribute contains '>' and that all tags
comment|// within the HTML will be well-formed.
comment|//
name|int
name|tagEnd
init|=
name|html
operator|.
name|indexOf
argument_list|(
literal|'>'
argument_list|,
name|tagStart
argument_list|)
decl_stmt|;
assert|assert
name|tagStart
operator|<
name|tagEnd
assert|;
name|pos
operator|=
name|tagEnd
operator|+
literal|1
expr_stmt|;
comment|// Handle any text between the end of the last tag,
comment|// and the start of this tag.
comment|//
if|if
condition|(
name|textChunkStart
operator|<
name|tagStart
condition|)
block|{
name|lastTag
operator|.
name|open
argument_list|(
name|buf
argument_list|,
name|html
argument_list|)
expr_stmt|;
name|htmlText
argument_list|(
name|html
operator|.
name|substring
argument_list|(
name|textChunkStart
argument_list|,
name|tagStart
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|textChunkStart
operator|=
name|pos
expr_stmt|;
if|if
condition|(
name|isBR
argument_list|(
name|html
argument_list|,
name|tagStart
argument_list|,
name|tagEnd
argument_list|)
condition|)
block|{
name|lastTag
operator|.
name|close
argument_list|(
name|buf
argument_list|,
name|html
argument_list|)
expr_stmt|;
name|content
operator|.
name|addLine
argument_list|(
name|src
operator|.
name|mapIndexToLine
argument_list|(
name|lineIdx
argument_list|)
argument_list|,
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
name|col
operator|=
literal|0
expr_stmt|;
name|lineIdx
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|html
operator|.
name|charAt
argument_list|(
name|tagStart
operator|+
literal|1
argument_list|)
operator|==
literal|'/'
condition|)
block|{
name|lastTag
operator|=
name|lastTag
operator|.
name|pop
argument_list|(
name|buf
argument_list|,
name|html
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|html
operator|.
name|charAt
argument_list|(
name|tagEnd
operator|-
literal|1
argument_list|)
operator|!=
literal|'/'
condition|)
block|{
name|lastTag
operator|=
operator|new
name|Tag
argument_list|(
name|lastTag
argument_list|,
name|tagStart
argument_list|,
name|tagEnd
argument_list|)
expr_stmt|;
block|}
block|}
name|buf
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|htmlText (String txt)
specifier|private
name|void
name|htmlText
parameter_list|(
name|String
name|txt
parameter_list|)
block|{
name|int
name|pos
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|pos
operator|<
name|txt
operator|.
name|length
argument_list|()
condition|)
block|{
name|int
name|start
init|=
name|txt
operator|.
name|indexOf
argument_list|(
literal|'&'
argument_list|,
name|pos
argument_list|)
decl_stmt|;
if|if
condition|(
name|start
operator|<
literal|0
condition|)
block|{
break|break;
block|}
name|cleanText
argument_list|(
name|txt
argument_list|,
name|pos
argument_list|,
name|start
argument_list|)
expr_stmt|;
name|pos
operator|=
name|txt
operator|.
name|indexOf
argument_list|(
literal|';'
argument_list|,
name|start
operator|+
literal|1
argument_list|)
operator|+
literal|1
expr_stmt|;
if|if
condition|(
name|settings
operator|.
name|getLineLength
argument_list|()
operator|<=
name|col
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|"<br />"
argument_list|)
expr_stmt|;
name|col
operator|=
literal|0
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
name|txt
operator|.
name|substring
argument_list|(
name|start
argument_list|,
name|pos
argument_list|)
argument_list|)
expr_stmt|;
name|col
operator|++
expr_stmt|;
block|}
name|cleanText
argument_list|(
name|txt
argument_list|,
name|pos
argument_list|,
name|txt
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|cleanText (String txt, int pos, int end)
specifier|private
name|void
name|cleanText
parameter_list|(
name|String
name|txt
parameter_list|,
name|int
name|pos
parameter_list|,
name|int
name|end
parameter_list|)
block|{
while|while
condition|(
name|pos
operator|<
name|end
condition|)
block|{
name|int
name|free
init|=
name|settings
operator|.
name|getLineLength
argument_list|()
operator|-
name|col
decl_stmt|;
if|if
condition|(
name|free
operator|<=
literal|0
condition|)
block|{
comment|// The current line is full. Throw an explicit line break
comment|// onto the end, and we'll continue on the next line.
comment|//
name|buf
operator|.
name|append
argument_list|(
literal|"<br />"
argument_list|)
expr_stmt|;
name|col
operator|=
literal|0
expr_stmt|;
name|free
operator|=
name|settings
operator|.
name|getLineLength
argument_list|()
expr_stmt|;
block|}
name|int
name|n
init|=
name|Math
operator|.
name|min
argument_list|(
name|end
operator|-
name|pos
argument_list|,
name|free
argument_list|)
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|txt
operator|.
name|substring
argument_list|(
name|pos
argument_list|,
name|pos
operator|+
name|n
argument_list|)
argument_list|)
expr_stmt|;
name|col
operator|+=
name|n
expr_stmt|;
name|pos
operator|+=
name|n
expr_stmt|;
block|}
block|}
comment|/** Run the prettify engine over the text and return the result. */
DECL|method|prettify (String html, String type)
specifier|protected
specifier|abstract
name|String
name|prettify
parameter_list|(
name|String
name|html
parameter_list|,
name|String
name|type
parameter_list|)
function_decl|;
DECL|method|isBR (String html, int tagStart, int tagEnd)
specifier|private
specifier|static
name|boolean
name|isBR
parameter_list|(
name|String
name|html
parameter_list|,
name|int
name|tagStart
parameter_list|,
name|int
name|tagEnd
parameter_list|)
block|{
return|return
name|tagEnd
operator|-
name|tagStart
operator|==
literal|5
comment|//
operator|&&
name|html
operator|.
name|charAt
argument_list|(
name|tagStart
operator|+
literal|1
argument_list|)
operator|==
literal|'b'
comment|//
operator|&&
name|html
operator|.
name|charAt
argument_list|(
name|tagStart
operator|+
literal|2
argument_list|)
operator|==
literal|'r'
comment|//
operator|&&
name|html
operator|.
name|charAt
argument_list|(
name|tagStart
operator|+
literal|3
argument_list|)
operator|==
literal|' '
return|;
block|}
DECL|class|Tag
specifier|private
specifier|static
class|class
name|Tag
block|{
DECL|field|NULL
specifier|static
specifier|final
name|Tag
name|NULL
init|=
operator|new
name|Tag
argument_list|(
literal|null
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
block|{
annotation|@
name|Override
name|void
name|open
parameter_list|(
name|StringBuilder
name|buf
parameter_list|,
name|String
name|html
parameter_list|)
block|{       }
annotation|@
name|Override
name|void
name|close
parameter_list|(
name|StringBuilder
name|buf
parameter_list|,
name|String
name|html
parameter_list|)
block|{       }
annotation|@
name|Override
name|Tag
name|pop
parameter_list|(
name|StringBuilder
name|buf
parameter_list|,
name|String
name|html
parameter_list|)
block|{
return|return
name|this
return|;
block|}
block|}
decl_stmt|;
DECL|field|parent
specifier|final
name|Tag
name|parent
decl_stmt|;
DECL|field|start
specifier|final
name|int
name|start
decl_stmt|;
DECL|field|end
specifier|final
name|int
name|end
decl_stmt|;
DECL|field|open
name|boolean
name|open
decl_stmt|;
DECL|method|Tag (Tag p, int s, int e)
name|Tag
parameter_list|(
name|Tag
name|p
parameter_list|,
name|int
name|s
parameter_list|,
name|int
name|e
parameter_list|)
block|{
name|parent
operator|=
name|p
expr_stmt|;
name|start
operator|=
name|s
expr_stmt|;
name|end
operator|=
name|e
expr_stmt|;
block|}
DECL|method|open (StringBuilder buf, String html)
name|void
name|open
parameter_list|(
name|StringBuilder
name|buf
parameter_list|,
name|String
name|html
parameter_list|)
block|{
if|if
condition|(
operator|!
name|open
condition|)
block|{
name|parent
operator|.
name|open
argument_list|(
name|buf
argument_list|,
name|html
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|html
operator|.
name|substring
argument_list|(
name|start
argument_list|,
name|end
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|open
operator|=
literal|true
expr_stmt|;
block|}
block|}
DECL|method|close (StringBuilder buf, String html)
name|void
name|close
parameter_list|(
name|StringBuilder
name|buf
parameter_list|,
name|String
name|html
parameter_list|)
block|{
name|pop
argument_list|(
name|buf
argument_list|,
name|html
argument_list|)
expr_stmt|;
name|parent
operator|.
name|close
argument_list|(
name|buf
argument_list|,
name|html
argument_list|)
expr_stmt|;
block|}
DECL|method|pop (StringBuilder buf, String html)
name|Tag
name|pop
parameter_list|(
name|StringBuilder
name|buf
parameter_list|,
name|String
name|html
parameter_list|)
block|{
if|if
condition|(
name|open
condition|)
block|{
name|int
name|sp
init|=
name|html
operator|.
name|indexOf
argument_list|(
literal|' '
argument_list|,
name|start
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|sp
operator|<
literal|0
operator|||
name|end
operator|<
name|sp
condition|)
block|{
name|sp
operator|=
name|end
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|"</"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|html
operator|.
name|substring
argument_list|(
name|start
operator|+
literal|1
argument_list|,
name|sp
argument_list|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
name|open
operator|=
literal|false
expr_stmt|;
block|}
return|return
name|parent
return|;
block|}
block|}
DECL|method|toHTML (SparseFileContent src)
specifier|private
name|String
name|toHTML
parameter_list|(
name|SparseFileContent
name|src
parameter_list|)
block|{
name|SafeHtml
name|html
decl_stmt|;
if|if
condition|(
name|settings
operator|.
name|isIntralineDifference
argument_list|()
condition|)
block|{
name|html
operator|=
name|colorLineEdits
argument_list|(
name|src
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|SafeHtmlBuilder
name|b
init|=
operator|new
name|SafeHtmlBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|index
init|=
name|src
operator|.
name|first
argument_list|()
init|;
name|index
operator|<
name|src
operator|.
name|size
argument_list|()
condition|;
name|index
operator|=
name|src
operator|.
name|next
argument_list|(
name|index
argument_list|)
control|)
block|{
name|b
operator|.
name|append
argument_list|(
name|src
operator|.
name|get
argument_list|(
name|index
argument_list|)
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
name|html
operator|=
name|b
expr_stmt|;
block|}
if|if
condition|(
name|settings
operator|.
name|isShowWhiteSpaceErrors
argument_list|()
condition|)
block|{
comment|// We need to do whitespace errors before showing tabs, because
comment|// these patterns rely on \t as a literal, before it expands.
comment|//
name|html
operator|=
name|showTabAfterSpace
argument_list|(
name|html
argument_list|)
expr_stmt|;
name|html
operator|=
name|showTrailingWhitespace
argument_list|(
name|html
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|settings
operator|.
name|isShowTabs
argument_list|()
condition|)
block|{
name|String
name|t
init|=
literal|1
operator|<
name|settings
operator|.
name|getTabSize
argument_list|()
condition|?
literal|"\t"
else|:
literal|""
decl_stmt|;
name|html
operator|=
name|html
operator|.
name|replaceAll
argument_list|(
literal|"\t"
argument_list|,
literal|"<span class=\"vt\">\u00BB</span>"
operator|+
name|t
argument_list|)
expr_stmt|;
block|}
return|return
name|html
operator|.
name|asString
argument_list|()
return|;
block|}
DECL|method|colorLineEdits (SparseFileContent src)
specifier|private
name|SafeHtml
name|colorLineEdits
parameter_list|(
name|SparseFileContent
name|src
parameter_list|)
block|{
comment|// Make a copy of the edits with a sentinel that is after all lines
comment|// in the source. That simplifies our loop below because we'll never
comment|// run off the end of the edit list.
comment|//
name|List
argument_list|<
name|Edit
argument_list|>
name|edits
init|=
operator|new
name|ArrayList
argument_list|<
name|Edit
argument_list|>
argument_list|(
name|this
operator|.
name|edits
operator|.
name|size
argument_list|()
operator|+
literal|1
argument_list|)
decl_stmt|;
name|edits
operator|.
name|addAll
argument_list|(
name|this
operator|.
name|edits
argument_list|)
expr_stmt|;
name|edits
operator|.
name|add
argument_list|(
operator|new
name|Edit
argument_list|(
name|src
operator|.
name|size
argument_list|()
argument_list|,
name|src
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|SafeHtmlBuilder
name|buf
init|=
operator|new
name|SafeHtmlBuilder
argument_list|()
decl_stmt|;
name|int
name|curIdx
init|=
literal|0
decl_stmt|;
name|Edit
name|curEdit
init|=
name|edits
operator|.
name|get
argument_list|(
name|curIdx
argument_list|)
decl_stmt|;
name|ReplaceEdit
name|lastReplace
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|Edit
argument_list|>
name|charEdits
init|=
literal|null
decl_stmt|;
name|int
name|lastPos
init|=
literal|0
decl_stmt|;
name|int
name|lastIdx
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|index
init|=
name|src
operator|.
name|first
argument_list|()
init|;
name|index
operator|<
name|src
operator|.
name|size
argument_list|()
condition|;
name|index
operator|=
name|src
operator|.
name|next
argument_list|(
name|index
argument_list|)
control|)
block|{
name|int
name|cmp
init|=
name|compare
argument_list|(
name|index
argument_list|,
name|curEdit
argument_list|)
decl_stmt|;
while|while
condition|(
literal|0
operator|<
name|cmp
condition|)
block|{
comment|// The index is after the edit. Skip to the next edit.
comment|//
name|curEdit
operator|=
name|edits
operator|.
name|get
argument_list|(
name|curIdx
operator|++
argument_list|)
expr_stmt|;
name|cmp
operator|=
name|compare
argument_list|(
name|index
argument_list|,
name|curEdit
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cmp
operator|<
literal|0
condition|)
block|{
comment|// index occurs before the edit. This is a line of context.
comment|//
name|buf
operator|.
name|append
argument_list|(
name|src
operator|.
name|get
argument_list|(
name|index
argument_list|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
continue|continue;
block|}
comment|// index occurs within the edit. The line is a modification.
comment|//
if|if
condition|(
name|curEdit
operator|instanceof
name|ReplaceEdit
condition|)
block|{
if|if
condition|(
name|lastReplace
operator|!=
name|curEdit
condition|)
block|{
name|lastReplace
operator|=
operator|(
name|ReplaceEdit
operator|)
name|curEdit
expr_stmt|;
name|charEdits
operator|=
name|lastReplace
operator|.
name|getInternalEdits
argument_list|()
expr_stmt|;
name|lastPos
operator|=
literal|0
expr_stmt|;
name|lastIdx
operator|=
literal|0
expr_stmt|;
block|}
specifier|final
name|String
name|line
init|=
name|src
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|+
literal|"\n"
decl_stmt|;
for|for
control|(
name|int
name|c
init|=
literal|0
init|;
name|c
operator|<
name|line
operator|.
name|length
argument_list|()
condition|;
control|)
block|{
if|if
condition|(
name|charEdits
operator|.
name|size
argument_list|()
operator|<=
name|lastIdx
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|line
operator|.
name|substring
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
specifier|final
name|Edit
name|edit
init|=
name|charEdits
operator|.
name|get
argument_list|(
name|lastIdx
argument_list|)
decl_stmt|;
specifier|final
name|int
name|b
init|=
name|side
operator|.
name|getBegin
argument_list|(
name|edit
argument_list|)
operator|-
name|lastPos
decl_stmt|;
specifier|final
name|int
name|e
init|=
name|side
operator|.
name|getEnd
argument_list|(
name|edit
argument_list|)
operator|-
name|lastPos
decl_stmt|;
if|if
condition|(
name|c
operator|<
name|b
condition|)
block|{
comment|// There is text at the start of this line that is common
comment|// with the other side. Copy it with no style around it.
comment|//
specifier|final
name|int
name|cmnLen
init|=
name|Math
operator|.
name|min
argument_list|(
name|b
argument_list|,
name|line
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|buf
operator|.
name|openSpan
argument_list|()
expr_stmt|;
name|buf
operator|.
name|setStyleName
argument_list|(
literal|"wdc"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|line
operator|.
name|substring
argument_list|(
name|c
argument_list|,
name|cmnLen
argument_list|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|closeSpan
argument_list|()
expr_stmt|;
name|c
operator|=
name|cmnLen
expr_stmt|;
block|}
specifier|final
name|int
name|modLen
init|=
name|Math
operator|.
name|min
argument_list|(
name|e
argument_list|,
name|line
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|<
name|e
operator|&&
name|c
operator|<
name|modLen
condition|)
block|{
name|buf
operator|.
name|openSpan
argument_list|()
expr_stmt|;
name|buf
operator|.
name|setStyleName
argument_list|(
name|side
operator|.
name|getStyleName
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|line
operator|.
name|substring
argument_list|(
name|c
argument_list|,
name|modLen
argument_list|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|closeSpan
argument_list|()
expr_stmt|;
if|if
condition|(
name|modLen
operator|==
name|line
operator|.
name|length
argument_list|()
condition|)
block|{
name|trailingEdits
operator|.
name|add
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
name|c
operator|=
name|modLen
expr_stmt|;
block|}
if|if
condition|(
name|e
operator|<=
name|c
condition|)
block|{
name|lastIdx
operator|++
expr_stmt|;
block|}
block|}
name|lastPos
operator|+=
name|line
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|buf
operator|.
name|append
argument_list|(
name|src
operator|.
name|get
argument_list|(
name|index
argument_list|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|buf
return|;
block|}
DECL|method|compare (int index, Edit edit)
specifier|private
name|int
name|compare
parameter_list|(
name|int
name|index
parameter_list|,
name|Edit
name|edit
parameter_list|)
block|{
if|if
condition|(
name|index
operator|<
name|side
operator|.
name|getBegin
argument_list|(
name|edit
argument_list|)
condition|)
block|{
return|return
operator|-
literal|1
return|;
comment|// index occurs before the edit.
block|}
elseif|else
if|if
condition|(
name|index
operator|<
name|side
operator|.
name|getEnd
argument_list|(
name|edit
argument_list|)
condition|)
block|{
return|return
literal|0
return|;
comment|// index occurs within the edit.
block|}
else|else
block|{
return|return
literal|1
return|;
comment|// index occurs after the edit.
block|}
block|}
DECL|method|showTabAfterSpace (SafeHtml src)
specifier|private
name|SafeHtml
name|showTabAfterSpace
parameter_list|(
name|SafeHtml
name|src
parameter_list|)
block|{
specifier|final
name|String
name|m
init|=
literal|"( ( |<span[^>]*>|</span>)*\t)"
decl_stmt|;
specifier|final
name|String
name|r
init|=
literal|"<span class=\"wse\""
comment|//
operator|+
literal|" title=\""
operator|+
name|PrettifyConstants
operator|.
name|C
operator|.
name|wseTabAfterSpace
argument_list|()
operator|+
literal|"\""
comment|//
operator|+
literal|">$1</span>"
decl_stmt|;
name|src
operator|=
name|src
operator|.
name|replaceFirst
argument_list|(
literal|"^"
operator|+
name|m
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|src
operator|=
name|src
operator|.
name|replaceAll
argument_list|(
literal|"\n"
operator|+
name|m
argument_list|,
literal|"\n"
operator|+
name|r
argument_list|)
expr_stmt|;
return|return
name|src
return|;
block|}
DECL|method|showTrailingWhitespace (SafeHtml src)
specifier|private
name|SafeHtml
name|showTrailingWhitespace
parameter_list|(
name|SafeHtml
name|src
parameter_list|)
block|{
specifier|final
name|String
name|r
init|=
literal|"<span class=\"wse\""
comment|//
operator|+
literal|" title=\""
operator|+
name|PrettifyConstants
operator|.
name|C
operator|.
name|wseTrailingSpace
argument_list|()
operator|+
literal|"\""
comment|//
operator|+
literal|">$1</span>$2"
decl_stmt|;
name|src
operator|=
name|src
operator|.
name|replaceAll
argument_list|(
literal|"([ \t][ \t]*)(\r?(</span>)?\n)"
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|src
operator|=
name|src
operator|.
name|replaceFirst
argument_list|(
literal|"([ \t][ \t]*)(\r?(</span>)?\n?)$"
argument_list|,
name|r
argument_list|)
expr_stmt|;
return|return
name|src
return|;
block|}
DECL|method|expandTabs (String html)
specifier|private
name|String
name|expandTabs
parameter_list|(
name|String
name|html
parameter_list|)
block|{
name|StringBuilder
name|tmp
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|settings
operator|.
name|isShowTabs
argument_list|()
condition|)
block|{
name|i
operator|=
literal|1
expr_stmt|;
block|}
for|for
control|(
init|;
name|i
operator|<
name|settings
operator|.
name|getTabSize
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|tmp
operator|.
name|append
argument_list|(
literal|"&nbsp;"
argument_list|)
expr_stmt|;
block|}
return|return
name|html
operator|.
name|replaceAll
argument_list|(
literal|"\t"
argument_list|,
name|tmp
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getFileType ()
specifier|private
name|String
name|getFileType
parameter_list|()
block|{
name|String
name|srcType
init|=
name|settings
operator|.
name|getFilename
argument_list|()
decl_stmt|;
if|if
condition|(
name|srcType
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|int
name|dot
init|=
name|srcType
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
decl_stmt|;
if|if
condition|(
name|dot
operator|<
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
literal|0
operator|<
name|dot
condition|)
block|{
name|srcType
operator|=
name|srcType
operator|.
name|substring
argument_list|(
name|dot
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|"txt"
operator|.
name|equalsIgnoreCase
argument_list|(
name|srcType
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|srcType
return|;
block|}
block|}
end_class

end_unit

