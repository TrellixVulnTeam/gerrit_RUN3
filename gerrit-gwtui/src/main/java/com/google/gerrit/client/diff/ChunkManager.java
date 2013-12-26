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
name|DiffInfo
operator|.
name|Region
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
name|DiffInfo
operator|.
name|Span
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
name|PaddingManager
operator|.
name|LinePaddingWidgetWrapper
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
name|rpc
operator|.
name|Natives
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
name|JsArray
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
name|google
operator|.
name|gwt
operator|.
name|dom
operator|.
name|client
operator|.
name|Element
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
name|dom
operator|.
name|client
operator|.
name|Style
operator|.
name|Unit
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
name|CodeMirror
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
name|CodeMirror
operator|.
name|LineClassWhere
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
name|CodeMirror
operator|.
name|LineHandle
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
name|Configuration
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
name|net
operator|.
name|codemirror
operator|.
name|lib
operator|.
name|TextMarker
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
import|;
end_import

begin_comment
comment|/** Colors modified regions for {@link SideBySide2}. */
end_comment

begin_class
DECL|class|ChunkManager
class|class
name|ChunkManager
block|{
DECL|field|host
specifier|private
specifier|final
name|SideBySide2
name|host
decl_stmt|;
DECL|field|cmA
specifier|private
specifier|final
name|CodeMirror
name|cmA
decl_stmt|;
DECL|field|cmB
specifier|private
specifier|final
name|CodeMirror
name|cmB
decl_stmt|;
DECL|field|sidePanel
specifier|private
specifier|final
name|SidePanel
name|sidePanel
decl_stmt|;
DECL|field|mapper
specifier|private
specifier|final
name|LineMapper
name|mapper
decl_stmt|;
DECL|field|chunks
specifier|private
name|List
argument_list|<
name|DiffChunkInfo
argument_list|>
name|chunks
decl_stmt|;
DECL|field|markers
specifier|private
name|List
argument_list|<
name|TextMarker
argument_list|>
name|markers
decl_stmt|;
DECL|field|undo
specifier|private
name|List
argument_list|<
name|Runnable
argument_list|>
name|undo
decl_stmt|;
DECL|field|paddingOnOtherSide
specifier|private
name|Map
argument_list|<
name|LineHandle
argument_list|,
name|LinePaddingWidgetWrapper
argument_list|>
name|paddingOnOtherSide
decl_stmt|;
DECL|method|ChunkManager (SideBySide2 host, CodeMirror cmA, CodeMirror cmB, SidePanel sidePanel)
name|ChunkManager
parameter_list|(
name|SideBySide2
name|host
parameter_list|,
name|CodeMirror
name|cmA
parameter_list|,
name|CodeMirror
name|cmB
parameter_list|,
name|SidePanel
name|sidePanel
parameter_list|)
block|{
name|this
operator|.
name|host
operator|=
name|host
expr_stmt|;
name|this
operator|.
name|cmA
operator|=
name|cmA
expr_stmt|;
name|this
operator|.
name|cmB
operator|=
name|cmB
expr_stmt|;
name|this
operator|.
name|sidePanel
operator|=
name|sidePanel
expr_stmt|;
name|this
operator|.
name|mapper
operator|=
operator|new
name|LineMapper
argument_list|()
expr_stmt|;
block|}
DECL|method|getLineMapper ()
name|LineMapper
name|getLineMapper
parameter_list|()
block|{
return|return
name|mapper
return|;
block|}
DECL|method|getFirst ()
name|DiffChunkInfo
name|getFirst
parameter_list|()
block|{
return|return
name|chunks
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
name|chunks
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
DECL|method|reset ()
name|void
name|reset
parameter_list|()
block|{
name|mapper
operator|.
name|reset
argument_list|()
expr_stmt|;
for|for
control|(
name|TextMarker
name|m
range|:
name|markers
control|)
block|{
name|m
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Runnable
name|r
range|:
name|undo
control|)
block|{
name|r
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|LinePaddingWidgetWrapper
name|x
range|:
name|paddingOnOtherSide
operator|.
name|values
argument_list|()
control|)
block|{
name|x
operator|.
name|getWidget
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|render (DiffInfo diff)
name|void
name|render
parameter_list|(
name|DiffInfo
name|diff
parameter_list|)
block|{
name|chunks
operator|=
operator|new
name|ArrayList
argument_list|<
name|DiffChunkInfo
argument_list|>
argument_list|()
expr_stmt|;
name|markers
operator|=
operator|new
name|ArrayList
argument_list|<
name|TextMarker
argument_list|>
argument_list|()
expr_stmt|;
name|undo
operator|=
operator|new
name|ArrayList
argument_list|<
name|Runnable
argument_list|>
argument_list|()
expr_stmt|;
name|paddingOnOtherSide
operator|=
operator|new
name|HashMap
argument_list|<
name|LineHandle
argument_list|,
name|LinePaddingWidgetWrapper
argument_list|>
argument_list|()
expr_stmt|;
name|String
name|diffColor
init|=
name|diff
operator|.
name|meta_a
argument_list|()
operator|==
literal|null
operator|||
name|diff
operator|.
name|meta_b
argument_list|()
operator|==
literal|null
condition|?
name|DiffTable
operator|.
name|style
operator|.
name|intralineBg
argument_list|()
else|:
name|DiffTable
operator|.
name|style
operator|.
name|diff
argument_list|()
decl_stmt|;
for|for
control|(
name|Region
name|current
range|:
name|Natives
operator|.
name|asList
argument_list|(
name|diff
operator|.
name|content
argument_list|()
argument_list|)
control|)
block|{
if|if
condition|(
name|current
operator|.
name|ab
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|mapper
operator|.
name|appendCommon
argument_list|(
name|current
operator|.
name|ab
argument_list|()
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|render
argument_list|(
name|current
argument_list|,
name|diffColor
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|render (Region region, String diffColor)
specifier|private
name|void
name|render
parameter_list|(
name|Region
name|region
parameter_list|,
name|String
name|diffColor
parameter_list|)
block|{
name|int
name|startA
init|=
name|mapper
operator|.
name|getLineA
argument_list|()
decl_stmt|;
name|int
name|startB
init|=
name|mapper
operator|.
name|getLineB
argument_list|()
decl_stmt|;
name|JsArrayString
name|a
init|=
name|region
operator|.
name|a
argument_list|()
decl_stmt|;
name|JsArrayString
name|b
init|=
name|region
operator|.
name|b
argument_list|()
decl_stmt|;
name|int
name|aLen
init|=
name|a
operator|!=
literal|null
condition|?
name|a
operator|.
name|length
argument_list|()
else|:
literal|0
decl_stmt|;
name|int
name|bLen
init|=
name|b
operator|!=
literal|null
condition|?
name|b
operator|.
name|length
argument_list|()
else|:
literal|0
decl_stmt|;
name|String
name|color
init|=
name|a
operator|==
literal|null
operator|||
name|b
operator|==
literal|null
condition|?
name|diffColor
else|:
name|DiffTable
operator|.
name|style
operator|.
name|intralineBg
argument_list|()
decl_stmt|;
name|colorLines
argument_list|(
name|cmA
argument_list|,
name|color
argument_list|,
name|startA
argument_list|,
name|aLen
argument_list|)
expr_stmt|;
name|colorLines
argument_list|(
name|cmB
argument_list|,
name|color
argument_list|,
name|startB
argument_list|,
name|bLen
argument_list|)
expr_stmt|;
name|markEdit
argument_list|(
name|cmA
argument_list|,
name|startA
argument_list|,
name|a
argument_list|,
name|region
operator|.
name|edit_a
argument_list|()
argument_list|)
expr_stmt|;
name|markEdit
argument_list|(
name|cmB
argument_list|,
name|startB
argument_list|,
name|b
argument_list|,
name|region
operator|.
name|edit_b
argument_list|()
argument_list|)
expr_stmt|;
name|addGutterTag
argument_list|(
name|region
argument_list|,
name|startA
argument_list|,
name|startB
argument_list|)
expr_stmt|;
name|mapper
operator|.
name|appendReplace
argument_list|(
name|aLen
argument_list|,
name|bLen
argument_list|)
expr_stmt|;
name|int
name|endA
init|=
name|mapper
operator|.
name|getLineA
argument_list|()
operator|-
literal|1
decl_stmt|;
name|int
name|endB
init|=
name|mapper
operator|.
name|getLineB
argument_list|()
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|aLen
operator|>
literal|0
condition|)
block|{
name|addDiffChunkAndPadding
argument_list|(
name|cmB
argument_list|,
name|endB
argument_list|,
name|endA
argument_list|,
name|aLen
argument_list|,
name|bLen
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bLen
operator|>
literal|0
condition|)
block|{
name|addDiffChunkAndPadding
argument_list|(
name|cmA
argument_list|,
name|endA
argument_list|,
name|endB
argument_list|,
name|bLen
argument_list|,
name|aLen
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addGutterTag (Region region, int startA, int startB)
specifier|private
name|void
name|addGutterTag
parameter_list|(
name|Region
name|region
parameter_list|,
name|int
name|startA
parameter_list|,
name|int
name|startB
parameter_list|)
block|{
if|if
condition|(
name|region
operator|.
name|a
argument_list|()
operator|==
literal|null
condition|)
block|{
name|sidePanel
operator|.
name|addGutter
argument_list|(
name|cmB
argument_list|,
name|startB
argument_list|,
name|SidePanel
operator|.
name|GutterType
operator|.
name|INSERT
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|region
operator|.
name|b
argument_list|()
operator|==
literal|null
condition|)
block|{
name|sidePanel
operator|.
name|addGutter
argument_list|(
name|cmA
argument_list|,
name|startA
argument_list|,
name|SidePanel
operator|.
name|GutterType
operator|.
name|DELETE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sidePanel
operator|.
name|addGutter
argument_list|(
name|cmB
argument_list|,
name|startB
argument_list|,
name|SidePanel
operator|.
name|GutterType
operator|.
name|EDIT
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|markEdit (CodeMirror cm, int startLine, JsArrayString lines, JsArray<Span> edits)
specifier|private
name|void
name|markEdit
parameter_list|(
name|CodeMirror
name|cm
parameter_list|,
name|int
name|startLine
parameter_list|,
name|JsArrayString
name|lines
parameter_list|,
name|JsArray
argument_list|<
name|Span
argument_list|>
name|edits
parameter_list|)
block|{
if|if
condition|(
name|lines
operator|==
literal|null
operator|||
name|edits
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|EditIterator
name|iter
init|=
operator|new
name|EditIterator
argument_list|(
name|lines
argument_list|,
name|startLine
argument_list|)
decl_stmt|;
name|Configuration
name|bg
init|=
name|Configuration
operator|.
name|create
argument_list|()
operator|.
name|set
argument_list|(
literal|"className"
argument_list|,
name|DiffTable
operator|.
name|style
operator|.
name|intralineBg
argument_list|()
argument_list|)
operator|.
name|set
argument_list|(
literal|"readOnly"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Configuration
name|diff
init|=
name|Configuration
operator|.
name|create
argument_list|()
operator|.
name|set
argument_list|(
literal|"className"
argument_list|,
name|DiffTable
operator|.
name|style
operator|.
name|diff
argument_list|()
argument_list|)
operator|.
name|set
argument_list|(
literal|"readOnly"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|LineCharacter
name|last
init|=
name|CodeMirror
operator|.
name|pos
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
for|for
control|(
name|Span
name|span
range|:
name|Natives
operator|.
name|asList
argument_list|(
name|edits
argument_list|)
control|)
block|{
name|LineCharacter
name|from
init|=
name|iter
operator|.
name|advance
argument_list|(
name|span
operator|.
name|skip
argument_list|()
argument_list|)
decl_stmt|;
name|LineCharacter
name|to
init|=
name|iter
operator|.
name|advance
argument_list|(
name|span
operator|.
name|mark
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|from
operator|.
name|getLine
argument_list|()
operator|==
name|last
operator|.
name|getLine
argument_list|()
condition|)
block|{
name|markers
operator|.
name|add
argument_list|(
name|cm
operator|.
name|markText
argument_list|(
name|last
argument_list|,
name|from
argument_list|,
name|bg
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|markers
operator|.
name|add
argument_list|(
name|cm
operator|.
name|markText
argument_list|(
name|CodeMirror
operator|.
name|pos
argument_list|(
name|from
operator|.
name|getLine
argument_list|()
argument_list|,
literal|0
argument_list|)
argument_list|,
name|from
argument_list|,
name|bg
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|markers
operator|.
name|add
argument_list|(
name|cm
operator|.
name|markText
argument_list|(
name|from
argument_list|,
name|to
argument_list|,
name|diff
argument_list|)
argument_list|)
expr_stmt|;
name|last
operator|=
name|to
expr_stmt|;
name|colorLines
argument_list|(
name|cm
argument_list|,
name|LineClassWhere
operator|.
name|BACKGROUND
argument_list|,
name|DiffTable
operator|.
name|style
operator|.
name|diff
argument_list|()
argument_list|,
name|from
operator|.
name|getLine
argument_list|()
argument_list|,
name|to
operator|.
name|getLine
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|colorLines (CodeMirror cm, String color, int line, int cnt)
specifier|private
name|void
name|colorLines
parameter_list|(
name|CodeMirror
name|cm
parameter_list|,
name|String
name|color
parameter_list|,
name|int
name|line
parameter_list|,
name|int
name|cnt
parameter_list|)
block|{
name|colorLines
argument_list|(
name|cm
argument_list|,
name|LineClassWhere
operator|.
name|WRAP
argument_list|,
name|color
argument_list|,
name|line
argument_list|,
name|line
operator|+
name|cnt
argument_list|)
expr_stmt|;
block|}
DECL|method|colorLines (final CodeMirror cm, final LineClassWhere where, final String className, final int start, final int end)
specifier|private
name|void
name|colorLines
parameter_list|(
specifier|final
name|CodeMirror
name|cm
parameter_list|,
specifier|final
name|LineClassWhere
name|where
parameter_list|,
specifier|final
name|String
name|className
parameter_list|,
specifier|final
name|int
name|start
parameter_list|,
specifier|final
name|int
name|end
parameter_list|)
block|{
if|if
condition|(
name|start
operator|<
name|end
condition|)
block|{
for|for
control|(
name|int
name|line
init|=
name|start
init|;
name|line
operator|<
name|end
condition|;
name|line
operator|++
control|)
block|{
name|cm
operator|.
name|addLineClass
argument_list|(
name|line
argument_list|,
name|where
argument_list|,
name|className
argument_list|)
expr_stmt|;
block|}
name|undo
operator|.
name|add
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
for|for
control|(
name|int
name|line
init|=
name|start
init|;
name|line
operator|<
name|end
condition|;
name|line
operator|++
control|)
block|{
name|cm
operator|.
name|removeLineClass
argument_list|(
name|line
argument_list|,
name|where
argument_list|,
name|className
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addDiffChunkAndPadding (CodeMirror cmToPad, int lineToPad, int lineOnOther, int chunkSize, boolean edit)
specifier|private
name|void
name|addDiffChunkAndPadding
parameter_list|(
name|CodeMirror
name|cmToPad
parameter_list|,
name|int
name|lineToPad
parameter_list|,
name|int
name|lineOnOther
parameter_list|,
name|int
name|chunkSize
parameter_list|,
name|boolean
name|edit
parameter_list|)
block|{
name|CodeMirror
name|otherCm
init|=
name|host
operator|.
name|otherCm
argument_list|(
name|cmToPad
argument_list|)
decl_stmt|;
name|paddingOnOtherSide
operator|.
name|put
argument_list|(
name|otherCm
operator|.
name|getLineHandle
argument_list|(
name|lineOnOther
argument_list|)
argument_list|,
operator|new
name|LinePaddingWidgetWrapper
argument_list|(
name|host
operator|.
name|addPaddingWidget
argument_list|(
name|cmToPad
argument_list|,
name|lineToPad
argument_list|,
literal|0
argument_list|,
name|Unit
operator|.
name|EM
argument_list|,
literal|null
argument_list|)
argument_list|,
name|lineToPad
argument_list|,
name|chunkSize
argument_list|)
argument_list|)
expr_stmt|;
name|chunks
operator|.
name|add
argument_list|(
operator|new
name|DiffChunkInfo
argument_list|(
name|otherCm
operator|.
name|side
argument_list|()
argument_list|,
name|lineOnOther
operator|-
name|chunkSize
operator|+
literal|1
argument_list|,
name|lineOnOther
argument_list|,
name|edit
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|diffChunkNav (final CodeMirror cm, final Direction dir)
name|Runnable
name|diffChunkNav
parameter_list|(
specifier|final
name|CodeMirror
name|cm
parameter_list|,
specifier|final
name|Direction
name|dir
parameter_list|)
block|{
return|return
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|int
name|line
init|=
name|cm
operator|.
name|hasActiveLine
argument_list|()
condition|?
name|cm
operator|.
name|getLineNumber
argument_list|(
name|cm
operator|.
name|getActiveLine
argument_list|()
argument_list|)
else|:
literal|0
decl_stmt|;
name|int
name|res
init|=
name|Collections
operator|.
name|binarySearch
argument_list|(
name|chunks
argument_list|,
operator|new
name|DiffChunkInfo
argument_list|(
name|cm
operator|.
name|side
argument_list|()
argument_list|,
name|line
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
argument_list|,
name|getDiffChunkComparator
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|res
operator|<
literal|0
condition|)
block|{
name|res
operator|=
operator|-
name|res
operator|-
operator|(
name|dir
operator|==
name|Direction
operator|.
name|PREV
condition|?
literal|1
else|:
literal|2
operator|)
expr_stmt|;
block|}
name|res
operator|=
name|res
operator|+
operator|(
name|dir
operator|==
name|Direction
operator|.
name|PREV
condition|?
operator|-
literal|1
else|:
literal|1
operator|)
expr_stmt|;
if|if
condition|(
name|res
operator|<
literal|0
operator|||
name|chunks
operator|.
name|size
argument_list|()
operator|<=
name|res
condition|)
block|{
return|return;
block|}
name|DiffChunkInfo
name|lookUp
init|=
name|chunks
operator|.
name|get
argument_list|(
name|res
argument_list|)
decl_stmt|;
comment|// If edit, skip the deletion chunk and set focus on the insertion one.
if|if
condition|(
name|lookUp
operator|.
name|isEdit
argument_list|()
operator|&&
name|lookUp
operator|.
name|getSide
argument_list|()
operator|==
name|DisplaySide
operator|.
name|A
condition|)
block|{
name|res
operator|=
name|res
operator|+
operator|(
name|dir
operator|==
name|Direction
operator|.
name|PREV
condition|?
operator|-
literal|1
else|:
literal|1
operator|)
expr_stmt|;
if|if
condition|(
name|res
operator|<
literal|0
operator|||
name|chunks
operator|.
name|size
argument_list|()
operator|<=
name|res
condition|)
block|{
return|return;
block|}
block|}
name|DiffChunkInfo
name|target
init|=
name|chunks
operator|.
name|get
argument_list|(
name|res
argument_list|)
decl_stmt|;
name|CodeMirror
name|targetCm
init|=
name|host
operator|.
name|getCmFromSide
argument_list|(
name|target
operator|.
name|getSide
argument_list|()
argument_list|)
decl_stmt|;
name|targetCm
operator|.
name|setCursor
argument_list|(
name|LineCharacter
operator|.
name|create
argument_list|(
name|target
operator|.
name|getStart
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|targetCm
operator|.
name|focus
argument_list|()
expr_stmt|;
name|targetCm
operator|.
name|scrollToY
argument_list|(
name|targetCm
operator|.
name|heightAtLine
argument_list|(
name|target
operator|.
name|getStart
argument_list|()
argument_list|,
literal|"local"
argument_list|)
operator|-
literal|0.5
operator|*
name|cmB
operator|.
name|getScrollbarV
argument_list|()
operator|.
name|getClientHeight
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
DECL|method|getDiffChunkComparator ()
specifier|private
name|Comparator
argument_list|<
name|DiffChunkInfo
argument_list|>
name|getDiffChunkComparator
parameter_list|()
block|{
comment|// Chunks are ordered by their starting line. If it's a deletion,
comment|// use its corresponding line on the revision side for comparison.
comment|// In the edit case, put the deletion chunk right before the
comment|// insertion chunk. This placement guarantees well-ordering.
return|return
operator|new
name|Comparator
argument_list|<
name|DiffChunkInfo
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|DiffChunkInfo
name|a
parameter_list|,
name|DiffChunkInfo
name|b
parameter_list|)
block|{
if|if
condition|(
name|a
operator|.
name|getSide
argument_list|()
operator|==
name|b
operator|.
name|getSide
argument_list|()
condition|)
block|{
return|return
name|a
operator|.
name|getStart
argument_list|()
operator|-
name|b
operator|.
name|getStart
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|a
operator|.
name|getSide
argument_list|()
operator|==
name|DisplaySide
operator|.
name|A
condition|)
block|{
name|int
name|comp
init|=
name|mapper
operator|.
name|lineOnOther
argument_list|(
name|a
operator|.
name|getSide
argument_list|()
argument_list|,
name|a
operator|.
name|getStart
argument_list|()
argument_list|)
operator|.
name|getLine
argument_list|()
operator|-
name|b
operator|.
name|getStart
argument_list|()
decl_stmt|;
return|return
name|comp
operator|==
literal|0
condition|?
operator|-
literal|1
else|:
name|comp
return|;
block|}
else|else
block|{
name|int
name|comp
init|=
name|a
operator|.
name|getStart
argument_list|()
operator|-
name|mapper
operator|.
name|lineOnOther
argument_list|(
name|b
operator|.
name|getSide
argument_list|()
argument_list|,
name|b
operator|.
name|getStart
argument_list|()
argument_list|)
operator|.
name|getLine
argument_list|()
decl_stmt|;
return|return
name|comp
operator|==
literal|0
condition|?
literal|1
else|:
name|comp
return|;
block|}
block|}
block|}
return|;
block|}
DECL|method|getDiffChunk (DisplaySide side, int line)
name|DiffChunkInfo
name|getDiffChunk
parameter_list|(
name|DisplaySide
name|side
parameter_list|,
name|int
name|line
parameter_list|)
block|{
name|int
name|res
init|=
name|Collections
operator|.
name|binarySearch
argument_list|(
name|chunks
argument_list|,
operator|new
name|DiffChunkInfo
argument_list|(
name|side
argument_list|,
name|line
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
argument_list|,
comment|// Dummy DiffChunkInfo
name|getDiffChunkComparator
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|res
operator|>=
literal|0
condition|)
block|{
return|return
name|chunks
operator|.
name|get
argument_list|(
name|res
argument_list|)
return|;
block|}
else|else
block|{
comment|// The line might be within a DiffChunk
name|res
operator|=
operator|-
name|res
operator|-
literal|1
expr_stmt|;
if|if
condition|(
name|res
operator|>
literal|0
condition|)
block|{
name|DiffChunkInfo
name|info
init|=
name|chunks
operator|.
name|get
argument_list|(
name|res
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|.
name|getSide
argument_list|()
operator|==
name|side
operator|&&
name|info
operator|.
name|getStart
argument_list|()
operator|<=
name|line
operator|&&
name|line
operator|<=
name|info
operator|.
name|getEnd
argument_list|()
condition|)
block|{
return|return
name|info
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|resizePadding (final CodeMirror cm, final LineHandle line, final DisplaySide side)
name|void
name|resizePadding
parameter_list|(
specifier|final
name|CodeMirror
name|cm
parameter_list|,
specifier|final
name|LineHandle
name|line
parameter_list|,
specifier|final
name|DisplaySide
name|side
parameter_list|)
block|{
if|if
condition|(
name|paddingOnOtherSide
operator|.
name|containsKey
argument_list|(
name|line
argument_list|)
condition|)
block|{
name|host
operator|.
name|defer
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|resizePaddingOnOtherSide
argument_list|(
name|side
argument_list|,
name|cm
operator|.
name|getLineNumber
argument_list|(
name|line
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|resizePaddingOnOtherSide (DisplaySide mySide, int line)
name|void
name|resizePaddingOnOtherSide
parameter_list|(
name|DisplaySide
name|mySide
parameter_list|,
name|int
name|line
parameter_list|)
block|{
name|CodeMirror
name|cm
init|=
name|host
operator|.
name|getCmFromSide
argument_list|(
name|mySide
argument_list|)
decl_stmt|;
name|LineHandle
name|handle
init|=
name|cm
operator|.
name|getLineHandle
argument_list|(
name|line
argument_list|)
decl_stmt|;
specifier|final
name|LinePaddingWidgetWrapper
name|otherWrapper
init|=
name|paddingOnOtherSide
operator|.
name|get
argument_list|(
name|handle
argument_list|)
decl_stmt|;
name|double
name|myChunkHeight
init|=
name|cm
operator|.
name|heightAtLine
argument_list|(
name|line
operator|+
literal|1
argument_list|)
operator|-
name|cm
operator|.
name|heightAtLine
argument_list|(
name|line
operator|-
name|otherWrapper
operator|.
name|getChunkLength
argument_list|()
operator|+
literal|1
argument_list|)
decl_stmt|;
name|Element
name|otherPadding
init|=
name|otherWrapper
operator|.
name|getElement
argument_list|()
decl_stmt|;
name|int
name|otherPaddingHeight
init|=
name|otherPadding
operator|.
name|getOffsetHeight
argument_list|()
decl_stmt|;
name|CodeMirror
name|otherCm
init|=
name|host
operator|.
name|otherCm
argument_list|(
name|cm
argument_list|)
decl_stmt|;
name|int
name|otherLine
init|=
name|otherWrapper
operator|.
name|getOtherLine
argument_list|()
decl_stmt|;
name|LineHandle
name|other
init|=
name|otherCm
operator|.
name|getLineHandle
argument_list|(
name|otherLine
argument_list|)
decl_stmt|;
if|if
condition|(
name|paddingOnOtherSide
operator|.
name|containsKey
argument_list|(
name|other
argument_list|)
condition|)
block|{
name|LinePaddingWidgetWrapper
name|myWrapper
init|=
name|paddingOnOtherSide
operator|.
name|get
argument_list|(
name|other
argument_list|)
decl_stmt|;
name|Element
name|myPadding
init|=
name|paddingOnOtherSide
operator|.
name|get
argument_list|(
name|other
argument_list|)
operator|.
name|getElement
argument_list|()
decl_stmt|;
name|int
name|myPaddingHeight
init|=
name|myPadding
operator|.
name|getOffsetHeight
argument_list|()
decl_stmt|;
name|myChunkHeight
operator|-=
name|myPaddingHeight
expr_stmt|;
name|double
name|otherChunkHeight
init|=
name|otherCm
operator|.
name|heightAtLine
argument_list|(
name|otherLine
operator|+
literal|1
argument_list|)
operator|-
name|otherCm
operator|.
name|heightAtLine
argument_list|(
name|otherLine
operator|-
name|myWrapper
operator|.
name|getChunkLength
argument_list|()
operator|+
literal|1
argument_list|)
operator|-
name|otherPaddingHeight
decl_stmt|;
name|double
name|delta
init|=
name|myChunkHeight
operator|-
name|otherChunkHeight
decl_stmt|;
if|if
condition|(
name|delta
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|myPaddingHeight
operator|!=
literal|0
condition|)
block|{
name|myPadding
operator|.
name|getStyle
argument_list|()
operator|.
name|setHeight
argument_list|(
operator|(
name|double
operator|)
literal|0
argument_list|,
name|Unit
operator|.
name|PX
argument_list|)
expr_stmt|;
name|myWrapper
operator|.
name|getWidget
argument_list|()
operator|.
name|changed
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|otherPaddingHeight
operator|!=
name|delta
condition|)
block|{
name|otherPadding
operator|.
name|getStyle
argument_list|()
operator|.
name|setHeight
argument_list|(
name|delta
argument_list|,
name|Unit
operator|.
name|PX
argument_list|)
expr_stmt|;
name|otherWrapper
operator|.
name|getWidget
argument_list|()
operator|.
name|changed
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|myPaddingHeight
operator|!=
operator|-
name|delta
condition|)
block|{
name|myPadding
operator|.
name|getStyle
argument_list|()
operator|.
name|setHeight
argument_list|(
operator|-
name|delta
argument_list|,
name|Unit
operator|.
name|PX
argument_list|)
expr_stmt|;
name|myWrapper
operator|.
name|getWidget
argument_list|()
operator|.
name|changed
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|otherPaddingHeight
operator|!=
literal|0
condition|)
block|{
name|otherPadding
operator|.
name|getStyle
argument_list|()
operator|.
name|setHeight
argument_list|(
operator|(
name|double
operator|)
literal|0
argument_list|,
name|Unit
operator|.
name|PX
argument_list|)
expr_stmt|;
name|otherWrapper
operator|.
name|getWidget
argument_list|()
operator|.
name|changed
argument_list|()
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|otherPaddingHeight
operator|!=
name|myChunkHeight
condition|)
block|{
name|otherPadding
operator|.
name|getStyle
argument_list|()
operator|.
name|setHeight
argument_list|(
name|myChunkHeight
argument_list|,
name|Unit
operator|.
name|PX
argument_list|)
expr_stmt|;
name|otherWrapper
operator|.
name|getWidget
argument_list|()
operator|.
name|changed
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

