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
name|Gerrit
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
name|changes
operator|.
name|CommentInfo
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
name|UnifiedChunkManager
operator|.
name|LineSidePair
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
name|patches
operator|.
name|SkippedLine
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
name|gerrit
operator|.
name|client
operator|.
name|ui
operator|.
name|CommentLinkProcessor
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
name|PatchSet
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
name|Pos
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
operator|.
name|FromTo
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
import|;
end_import

begin_comment
comment|/** Tracks comment widgets for {@link Unified}. */
end_comment

begin_class
DECL|class|UnifiedCommentManager
class|class
name|UnifiedCommentManager
extends|extends
name|CommentManager
block|{
DECL|field|host
specifier|private
specifier|final
name|Unified
name|host
decl_stmt|;
DECL|field|sideA
specifier|private
specifier|final
name|SortedMap
argument_list|<
name|Integer
argument_list|,
name|UnifiedCommentGroup
argument_list|>
name|sideA
decl_stmt|;
DECL|field|sideB
specifier|private
specifier|final
name|SortedMap
argument_list|<
name|Integer
argument_list|,
name|UnifiedCommentGroup
argument_list|>
name|sideB
decl_stmt|;
DECL|method|UnifiedCommentManager (Unified host, PatchSet.Id base, PatchSet.Id revision, String path, CommentLinkProcessor clp, boolean open)
name|UnifiedCommentManager
parameter_list|(
name|Unified
name|host
parameter_list|,
name|PatchSet
operator|.
name|Id
name|base
parameter_list|,
name|PatchSet
operator|.
name|Id
name|revision
parameter_list|,
name|String
name|path
parameter_list|,
name|CommentLinkProcessor
name|clp
parameter_list|,
name|boolean
name|open
parameter_list|)
block|{
name|super
argument_list|(
name|base
argument_list|,
name|revision
argument_list|,
name|path
argument_list|,
name|clp
argument_list|,
name|open
argument_list|)
expr_stmt|;
name|this
operator|.
name|host
operator|=
name|host
expr_stmt|;
name|sideA
operator|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
expr_stmt|;
name|sideB
operator|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDiffScreen ()
name|Unified
name|getDiffScreen
parameter_list|()
block|{
return|return
name|host
return|;
block|}
annotation|@
name|Override
DECL|method|setExpandAllComments (boolean b)
name|void
name|setExpandAllComments
parameter_list|(
name|boolean
name|b
parameter_list|)
block|{
name|setExpandAll
argument_list|(
name|b
argument_list|)
expr_stmt|;
for|for
control|(
name|UnifiedCommentGroup
name|g
range|:
name|sideA
operator|.
name|values
argument_list|()
control|)
block|{
name|g
operator|.
name|setOpenAll
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|UnifiedCommentGroup
name|g
range|:
name|sideB
operator|.
name|values
argument_list|()
control|)
block|{
name|g
operator|.
name|setOpenAll
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|commentNav (final CodeMirror src, final Direction dir)
name|Runnable
name|commentNav
parameter_list|(
specifier|final
name|CodeMirror
name|src
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
name|SortedMap
argument_list|<
name|Integer
argument_list|,
name|UnifiedCommentGroup
argument_list|>
name|map
init|=
name|map
argument_list|(
name|src
operator|.
name|side
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|line
init|=
name|src
operator|.
name|extras
argument_list|()
operator|.
name|hasActiveLine
argument_list|()
condition|?
name|src
operator|.
name|getLineNumber
argument_list|(
name|src
operator|.
name|extras
argument_list|()
operator|.
name|activeLine
argument_list|()
argument_list|)
operator|+
literal|1
else|:
literal|0
decl_stmt|;
if|if
condition|(
name|dir
operator|==
name|Direction
operator|.
name|NEXT
condition|)
block|{
name|map
operator|=
name|map
operator|.
name|tailMap
argument_list|(
name|line
operator|+
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|map
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
name|line
operator|=
name|map
operator|.
name|firstKey
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|map
operator|=
name|map
operator|.
name|headMap
argument_list|(
name|line
argument_list|)
expr_stmt|;
if|if
condition|(
name|map
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
name|line
operator|=
name|map
operator|.
name|lastKey
argument_list|()
expr_stmt|;
block|}
name|UnifiedCommentGroup
name|g
init|=
name|map
operator|.
name|get
argument_list|(
name|line
argument_list|)
decl_stmt|;
name|CodeMirror
name|cm
init|=
name|g
operator|.
name|getCm
argument_list|()
decl_stmt|;
name|double
name|y
init|=
name|cm
operator|.
name|heightAtLine
argument_list|(
name|g
operator|.
name|getLine
argument_list|()
operator|-
literal|1
argument_list|,
literal|"local"
argument_list|)
decl_stmt|;
name|cm
operator|.
name|setCursor
argument_list|(
name|Pos
operator|.
name|create
argument_list|(
name|g
operator|.
name|getLine
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|cm
operator|.
name|scrollToY
argument_list|(
name|y
operator|-
literal|0.5
operator|*
name|cm
operator|.
name|scrollbarV
argument_list|()
operator|.
name|getClientHeight
argument_list|()
argument_list|)
expr_stmt|;
name|cm
operator|.
name|focus
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
DECL|method|render (CommentsCollections in, boolean expandAll)
name|void
name|render
parameter_list|(
name|CommentsCollections
name|in
parameter_list|,
name|boolean
name|expandAll
parameter_list|)
block|{
if|if
condition|(
name|in
operator|.
name|publishedBase
operator|!=
literal|null
condition|)
block|{
name|renderPublished
argument_list|(
name|DisplaySide
operator|.
name|A
argument_list|,
name|in
operator|.
name|publishedBase
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|.
name|publishedRevision
operator|!=
literal|null
condition|)
block|{
name|renderPublished
argument_list|(
name|DisplaySide
operator|.
name|B
argument_list|,
name|in
operator|.
name|publishedRevision
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|.
name|draftsBase
operator|!=
literal|null
condition|)
block|{
name|renderDrafts
argument_list|(
name|DisplaySide
operator|.
name|A
argument_list|,
name|in
operator|.
name|draftsBase
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|.
name|draftsRevision
operator|!=
literal|null
condition|)
block|{
name|renderDrafts
argument_list|(
name|DisplaySide
operator|.
name|B
argument_list|,
name|in
operator|.
name|draftsRevision
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|expandAll
condition|)
block|{
name|setExpandAllComments
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|CommentGroup
name|g
range|:
name|sideA
operator|.
name|values
argument_list|()
control|)
block|{
name|g
operator|.
name|init
argument_list|(
name|host
operator|.
name|getDiffTable
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|CommentGroup
name|g
range|:
name|sideB
operator|.
name|values
argument_list|()
control|)
block|{
name|g
operator|.
name|init
argument_list|(
name|host
operator|.
name|getDiffTable
argument_list|()
argument_list|)
expr_stmt|;
name|g
operator|.
name|handleRedraw
argument_list|()
expr_stmt|;
block|}
name|setAttached
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|renderPublished (DisplaySide forSide, JsArray<CommentInfo> in)
name|void
name|renderPublished
parameter_list|(
name|DisplaySide
name|forSide
parameter_list|,
name|JsArray
argument_list|<
name|CommentInfo
argument_list|>
name|in
parameter_list|)
block|{
for|for
control|(
name|CommentInfo
name|info
range|:
name|Natives
operator|.
name|asList
argument_list|(
name|in
argument_list|)
control|)
block|{
name|DisplaySide
name|side
init|=
name|displaySide
argument_list|(
name|info
argument_list|,
name|forSide
argument_list|)
decl_stmt|;
if|if
condition|(
name|side
operator|!=
literal|null
condition|)
block|{
name|int
name|cmLinePlusOne
init|=
name|host
operator|.
name|getCmLine
argument_list|(
name|info
operator|.
name|line
argument_list|()
operator|-
literal|1
argument_list|,
name|side
argument_list|)
decl_stmt|;
name|UnifiedCommentGroup
name|group
init|=
name|group
argument_list|(
name|side
argument_list|,
name|cmLinePlusOne
argument_list|)
decl_stmt|;
name|PublishedBox
name|box
init|=
operator|new
name|PublishedBox
argument_list|(
name|group
argument_list|,
name|getCommentLinkProcessor
argument_list|()
argument_list|,
name|getPatchSetIdFromSide
argument_list|(
name|side
argument_list|)
argument_list|,
name|info
argument_list|,
name|isOpen
argument_list|()
argument_list|)
decl_stmt|;
name|group
operator|.
name|add
argument_list|(
name|box
argument_list|)
expr_stmt|;
name|box
operator|.
name|setAnnotation
argument_list|(
name|getDiffScreen
argument_list|()
operator|.
name|getDiffTable
argument_list|()
operator|.
name|scrollbar
operator|.
name|comment
argument_list|(
name|host
operator|.
name|getCm
argument_list|()
argument_list|,
name|cmLinePlusOne
argument_list|)
argument_list|)
expr_stmt|;
name|getPublished
argument_list|()
operator|.
name|put
argument_list|(
name|info
operator|.
name|id
argument_list|()
argument_list|,
name|box
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|newDraftOnGutterClick (CodeMirror cm, String gutterClass, int cmLinePlusOne)
name|void
name|newDraftOnGutterClick
parameter_list|(
name|CodeMirror
name|cm
parameter_list|,
name|String
name|gutterClass
parameter_list|,
name|int
name|cmLinePlusOne
parameter_list|)
block|{
name|DisplaySide
name|side
init|=
name|gutterClass
operator|.
name|equals
argument_list|(
name|UnifiedTable
operator|.
name|style
operator|.
name|lineNumbersLeft
argument_list|()
argument_list|)
condition|?
name|DisplaySide
operator|.
name|A
else|:
name|DisplaySide
operator|.
name|B
decl_stmt|;
if|if
condition|(
name|cm
operator|.
name|somethingSelected
argument_list|()
condition|)
block|{
name|FromTo
name|fromTo
init|=
name|cm
operator|.
name|getSelectedRange
argument_list|()
decl_stmt|;
name|Pos
name|end
init|=
name|fromTo
operator|.
name|to
argument_list|()
decl_stmt|;
if|if
condition|(
name|end
operator|.
name|ch
argument_list|()
operator|==
literal|0
condition|)
block|{
name|end
operator|.
name|line
argument_list|(
name|end
operator|.
name|line
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|end
operator|.
name|ch
argument_list|(
name|cm
operator|.
name|getLine
argument_list|(
name|end
operator|.
name|line
argument_list|()
argument_list|)
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|LineSidePair
name|pair
init|=
name|host
operator|.
name|getLineSidePairFromCmLine
argument_list|(
name|cmLinePlusOne
operator|-
literal|1
argument_list|)
decl_stmt|;
name|int
name|line
init|=
name|pair
operator|.
name|getLine
argument_list|()
decl_stmt|;
if|if
condition|(
name|pair
operator|.
name|getSide
argument_list|()
operator|!=
name|side
condition|)
block|{
name|line
operator|=
name|host
operator|.
name|lineOnOther
argument_list|(
name|pair
operator|.
name|getSide
argument_list|()
argument_list|,
name|line
argument_list|)
operator|.
name|getLine
argument_list|()
expr_stmt|;
block|}
name|addDraftBox
argument_list|(
name|side
argument_list|,
name|CommentInfo
operator|.
name|create
argument_list|(
name|getPath
argument_list|()
argument_list|,
name|getStoredSideFromDisplaySide
argument_list|(
name|side
argument_list|)
argument_list|,
name|line
operator|+
literal|1
argument_list|,
name|CommentRange
operator|.
name|create
argument_list|(
name|fromTo
argument_list|)
argument_list|)
argument_list|)
operator|.
name|setEdit
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|cm
operator|.
name|setSelection
argument_list|(
name|cm
operator|.
name|getCursor
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|insertNewDraft
argument_list|(
name|side
argument_list|,
name|cmLinePlusOne
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Create a new {@link DraftBox} at the specified line and focus it.    *    * @param side which side the draft will appear on.    * @param cmLinePlusOne the line the draft will be at, plus one.    *        Lines are 1-based. Line 0 is a special case creating a file level comment.    */
annotation|@
name|Override
DECL|method|insertNewDraft (DisplaySide side, int cmLinePlusOne)
name|void
name|insertNewDraft
parameter_list|(
name|DisplaySide
name|side
parameter_list|,
name|int
name|cmLinePlusOne
parameter_list|)
block|{
if|if
condition|(
name|cmLinePlusOne
operator|==
literal|0
condition|)
block|{
name|getDiffScreen
argument_list|()
operator|.
name|getSkipManager
argument_list|()
operator|.
name|ensureFirstLineIsVisible
argument_list|()
expr_stmt|;
block|}
name|CommentGroup
name|group
init|=
name|group
argument_list|(
name|side
argument_list|,
name|cmLinePlusOne
argument_list|)
decl_stmt|;
if|if
condition|(
literal|0
operator|<
name|group
operator|.
name|getBoxCount
argument_list|()
condition|)
block|{
name|CommentBox
name|last
init|=
name|group
operator|.
name|getCommentBox
argument_list|(
name|group
operator|.
name|getBoxCount
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|last
operator|instanceof
name|DraftBox
condition|)
block|{
operator|(
operator|(
name|DraftBox
operator|)
name|last
operator|)
operator|.
name|setEdit
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
operator|(
operator|(
name|PublishedBox
operator|)
name|last
operator|)
operator|.
name|doReply
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|LineSidePair
name|pair
init|=
name|host
operator|.
name|getLineSidePairFromCmLine
argument_list|(
name|cmLinePlusOne
operator|-
literal|1
argument_list|)
decl_stmt|;
name|int
name|line
init|=
name|pair
operator|.
name|getLine
argument_list|()
decl_stmt|;
if|if
condition|(
name|pair
operator|.
name|getSide
argument_list|()
operator|!=
name|side
condition|)
block|{
name|line
operator|=
name|host
operator|.
name|lineOnOther
argument_list|(
name|pair
operator|.
name|getSide
argument_list|()
argument_list|,
name|line
argument_list|)
operator|.
name|getLine
argument_list|()
expr_stmt|;
block|}
name|addDraftBox
argument_list|(
name|side
argument_list|,
name|CommentInfo
operator|.
name|create
argument_list|(
name|getPath
argument_list|()
argument_list|,
name|getStoredSideFromDisplaySide
argument_list|(
name|side
argument_list|)
argument_list|,
name|line
operator|+
literal|1
argument_list|,
literal|null
argument_list|)
argument_list|)
operator|.
name|setEdit
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|addDraftBox (DisplaySide side, CommentInfo info)
name|DraftBox
name|addDraftBox
parameter_list|(
name|DisplaySide
name|side
parameter_list|,
name|CommentInfo
name|info
parameter_list|)
block|{
name|int
name|cmLinePlusOne
init|=
name|host
operator|.
name|getCmLine
argument_list|(
name|info
operator|.
name|line
argument_list|()
operator|-
literal|1
argument_list|,
name|side
argument_list|)
operator|+
literal|1
decl_stmt|;
name|UnifiedCommentGroup
name|group
init|=
name|group
argument_list|(
name|side
argument_list|,
name|cmLinePlusOne
argument_list|)
decl_stmt|;
name|DraftBox
name|box
init|=
operator|new
name|DraftBox
argument_list|(
name|group
argument_list|,
name|getCommentLinkProcessor
argument_list|()
argument_list|,
name|getPatchSetIdFromSide
argument_list|(
name|side
argument_list|)
argument_list|,
name|info
argument_list|,
name|isExpandAll
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|.
name|inReplyTo
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|PublishedBox
name|r
init|=
name|getPublished
argument_list|()
operator|.
name|get
argument_list|(
name|info
operator|.
name|inReplyTo
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|!=
literal|null
condition|)
block|{
name|r
operator|.
name|setReplyBox
argument_list|(
name|box
argument_list|)
expr_stmt|;
block|}
block|}
name|group
operator|.
name|add
argument_list|(
name|box
argument_list|)
expr_stmt|;
name|box
operator|.
name|setAnnotation
argument_list|(
name|getDiffScreen
argument_list|()
operator|.
name|getDiffTable
argument_list|()
operator|.
name|scrollbar
operator|.
name|draft
argument_list|(
name|host
operator|.
name|getCm
argument_list|()
argument_list|,
name|cmLinePlusOne
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|box
return|;
block|}
annotation|@
name|Override
DECL|method|splitSkips (int context, List<SkippedLine> skips)
name|List
argument_list|<
name|SkippedLine
argument_list|>
name|splitSkips
parameter_list|(
name|int
name|context
parameter_list|,
name|List
argument_list|<
name|SkippedLine
argument_list|>
name|skips
parameter_list|)
block|{
if|if
condition|(
name|sideA
operator|.
name|containsKey
argument_list|(
literal|0
argument_list|)
operator|||
name|sideB
operator|.
name|containsKey
argument_list|(
literal|0
argument_list|)
condition|)
block|{
comment|// Special case of file comment; cannot skip first line.
for|for
control|(
name|SkippedLine
name|skip
range|:
name|skips
control|)
block|{
if|if
condition|(
name|skip
operator|.
name|getStartA
argument_list|()
operator|==
literal|0
condition|)
block|{
name|skip
operator|.
name|incrementStart
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|TreeSet
argument_list|<
name|Integer
argument_list|>
name|allBoxLines
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|(
name|sideA
operator|.
name|tailMap
argument_list|(
literal|1
argument_list|)
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
name|allBoxLines
operator|.
name|addAll
argument_list|(
name|sideB
operator|.
name|tailMap
argument_list|(
literal|1
argument_list|)
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|boxLine
range|:
name|allBoxLines
control|)
block|{
name|List
argument_list|<
name|SkippedLine
argument_list|>
name|temp
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|skips
operator|.
name|size
argument_list|()
operator|+
literal|2
argument_list|)
decl_stmt|;
for|for
control|(
name|SkippedLine
name|skip
range|:
name|skips
control|)
block|{
name|int
name|startLine
init|=
name|host
operator|.
name|getCmLine
argument_list|(
name|skip
operator|.
name|getStartA
argument_list|()
argument_list|,
name|DisplaySide
operator|.
name|A
argument_list|)
decl_stmt|;
name|int
name|deltaBefore
init|=
name|boxLine
operator|-
name|startLine
decl_stmt|;
name|int
name|deltaAfter
init|=
name|startLine
operator|+
name|skip
operator|.
name|getSize
argument_list|()
operator|-
name|boxLine
decl_stmt|;
if|if
condition|(
name|deltaBefore
operator|<
operator|-
name|context
operator|||
name|deltaAfter
operator|<
operator|-
name|context
condition|)
block|{
name|temp
operator|.
name|add
argument_list|(
name|skip
argument_list|)
expr_stmt|;
comment|// Size guaranteed to be greater than 1
block|}
elseif|else
if|if
condition|(
name|deltaBefore
operator|>
name|context
operator|&&
name|deltaAfter
operator|>
name|context
condition|)
block|{
name|SkippedLine
name|before
init|=
operator|new
name|SkippedLine
argument_list|(
name|skip
operator|.
name|getStartA
argument_list|()
argument_list|,
name|skip
operator|.
name|getStartB
argument_list|()
argument_list|,
name|skip
operator|.
name|getSize
argument_list|()
operator|-
name|deltaAfter
operator|-
name|context
argument_list|)
decl_stmt|;
name|skip
operator|.
name|incrementStart
argument_list|(
name|deltaBefore
operator|+
name|context
argument_list|)
expr_stmt|;
name|checkAndAddSkip
argument_list|(
name|temp
argument_list|,
name|before
argument_list|)
expr_stmt|;
name|checkAndAddSkip
argument_list|(
name|temp
argument_list|,
name|skip
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|deltaAfter
operator|>
name|context
condition|)
block|{
name|skip
operator|.
name|incrementStart
argument_list|(
name|deltaBefore
operator|+
name|context
argument_list|)
expr_stmt|;
name|checkAndAddSkip
argument_list|(
name|temp
argument_list|,
name|skip
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|deltaBefore
operator|>
name|context
condition|)
block|{
name|skip
operator|.
name|reduceSize
argument_list|(
name|deltaAfter
operator|+
name|context
argument_list|)
expr_stmt|;
name|checkAndAddSkip
argument_list|(
name|temp
argument_list|,
name|skip
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|temp
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|temp
return|;
block|}
name|skips
operator|=
name|temp
expr_stmt|;
block|}
return|return
name|skips
return|;
block|}
DECL|method|checkAndAddSkip (List<SkippedLine> out, SkippedLine s)
specifier|private
specifier|static
name|void
name|checkAndAddSkip
parameter_list|(
name|List
argument_list|<
name|SkippedLine
argument_list|>
name|out
parameter_list|,
name|SkippedLine
name|s
parameter_list|)
block|{
if|if
condition|(
name|s
operator|.
name|getSize
argument_list|()
operator|>
literal|1
condition|)
block|{
name|out
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|clearLine (DisplaySide side, int cmLinePlusOne, CommentGroup group)
name|void
name|clearLine
parameter_list|(
name|DisplaySide
name|side
parameter_list|,
name|int
name|cmLinePlusOne
parameter_list|,
name|CommentGroup
name|group
parameter_list|)
block|{
name|SortedMap
argument_list|<
name|Integer
argument_list|,
name|UnifiedCommentGroup
argument_list|>
name|map
init|=
name|map
argument_list|(
name|side
argument_list|)
decl_stmt|;
if|if
condition|(
name|map
operator|.
name|get
argument_list|(
name|cmLinePlusOne
argument_list|)
operator|==
name|group
condition|)
block|{
name|map
operator|.
name|remove
argument_list|(
name|cmLinePlusOne
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|toggleOpenBox (final CodeMirror cm)
name|Runnable
name|toggleOpenBox
parameter_list|(
specifier|final
name|CodeMirror
name|cm
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
if|if
condition|(
name|cm
operator|.
name|extras
argument_list|()
operator|.
name|hasActiveLine
argument_list|()
condition|)
block|{
name|UnifiedCommentGroup
name|w
init|=
name|map
argument_list|(
name|cm
operator|.
name|side
argument_list|()
argument_list|)
operator|.
name|get
argument_list|(
name|cm
operator|.
name|getLineNumber
argument_list|(
name|cm
operator|.
name|extras
argument_list|()
operator|.
name|activeLine
argument_list|()
argument_list|)
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|w
operator|!=
literal|null
condition|)
block|{
name|w
operator|.
name|openCloseLast
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|openCloseAll (final CodeMirror cm)
name|Runnable
name|openCloseAll
parameter_list|(
specifier|final
name|CodeMirror
name|cm
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
if|if
condition|(
name|cm
operator|.
name|extras
argument_list|()
operator|.
name|hasActiveLine
argument_list|()
condition|)
block|{
name|CommentGroup
name|w
init|=
name|map
argument_list|(
name|cm
operator|.
name|side
argument_list|()
argument_list|)
operator|.
name|get
argument_list|(
name|cm
operator|.
name|getLineNumber
argument_list|(
name|cm
operator|.
name|extras
argument_list|()
operator|.
name|activeLine
argument_list|()
argument_list|)
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|w
operator|!=
literal|null
condition|)
block|{
name|w
operator|.
name|openCloseAll
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|newDraftCallback (final CodeMirror cm)
name|Runnable
name|newDraftCallback
parameter_list|(
specifier|final
name|CodeMirror
name|cm
parameter_list|)
block|{
if|if
condition|(
operator|!
name|Gerrit
operator|.
name|isSignedIn
argument_list|()
condition|)
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
name|String
name|token
init|=
name|host
operator|.
name|getToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|cm
operator|.
name|extras
argument_list|()
operator|.
name|hasActiveLine
argument_list|()
condition|)
block|{
name|LineHandle
name|handle
init|=
name|cm
operator|.
name|extras
argument_list|()
operator|.
name|activeLine
argument_list|()
decl_stmt|;
name|int
name|line
init|=
name|cm
operator|.
name|getLineNumber
argument_list|(
name|handle
argument_list|)
operator|+
literal|1
decl_stmt|;
name|token
operator|+=
literal|"@"
operator|+
name|line
expr_stmt|;
block|}
name|Gerrit
operator|.
name|doSignIn
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
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
if|if
condition|(
name|cm
operator|.
name|extras
argument_list|()
operator|.
name|hasActiveLine
argument_list|()
condition|)
block|{
name|newDraft
argument_list|(
name|cm
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
DECL|method|newDraft (CodeMirror cm)
specifier|private
name|void
name|newDraft
parameter_list|(
name|CodeMirror
name|cm
parameter_list|)
block|{
name|int
name|cmLine
init|=
name|cm
operator|.
name|getLineNumber
argument_list|(
name|cm
operator|.
name|extras
argument_list|()
operator|.
name|activeLine
argument_list|()
argument_list|)
decl_stmt|;
name|LineSidePair
name|pair
init|=
name|host
operator|.
name|getLineSidePairFromCmLine
argument_list|(
name|cmLine
argument_list|)
decl_stmt|;
name|DisplaySide
name|side
init|=
name|pair
operator|.
name|getSide
argument_list|()
decl_stmt|;
if|if
condition|(
name|cm
operator|.
name|somethingSelected
argument_list|()
condition|)
block|{
comment|// TODO: Handle range comment
block|}
else|else
block|{
name|insertNewDraft
argument_list|(
name|side
argument_list|,
name|cmLine
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|group (DisplaySide side, int cmLinePlusOne)
specifier|private
name|UnifiedCommentGroup
name|group
parameter_list|(
name|DisplaySide
name|side
parameter_list|,
name|int
name|cmLinePlusOne
parameter_list|)
block|{
name|UnifiedCommentGroup
name|w
init|=
name|map
argument_list|(
name|side
argument_list|)
operator|.
name|get
argument_list|(
name|cmLinePlusOne
argument_list|)
decl_stmt|;
if|if
condition|(
name|w
operator|!=
literal|null
condition|)
block|{
return|return
name|w
return|;
block|}
name|UnifiedCommentGroup
name|g
init|=
operator|new
name|UnifiedCommentGroup
argument_list|(
name|this
argument_list|,
name|host
operator|.
name|getCm
argument_list|()
argument_list|,
name|side
argument_list|,
name|cmLinePlusOne
argument_list|)
decl_stmt|;
if|if
condition|(
name|side
operator|==
name|DisplaySide
operator|.
name|A
condition|)
block|{
name|sideA
operator|.
name|put
argument_list|(
name|cmLinePlusOne
argument_list|,
name|g
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sideB
operator|.
name|put
argument_list|(
name|cmLinePlusOne
argument_list|,
name|g
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isAttached
argument_list|()
condition|)
block|{
name|g
operator|.
name|init
argument_list|(
name|getDiffScreen
argument_list|()
operator|.
name|getDiffTable
argument_list|()
argument_list|)
expr_stmt|;
name|g
operator|.
name|handleRedraw
argument_list|()
expr_stmt|;
block|}
return|return
name|g
return|;
block|}
DECL|method|map (DisplaySide side)
specifier|private
name|SortedMap
argument_list|<
name|Integer
argument_list|,
name|UnifiedCommentGroup
argument_list|>
name|map
parameter_list|(
name|DisplaySide
name|side
parameter_list|)
block|{
return|return
name|side
operator|==
name|DisplaySide
operator|.
name|A
condition|?
name|sideA
else|:
name|sideB
return|;
block|}
block|}
end_class

end_unit

