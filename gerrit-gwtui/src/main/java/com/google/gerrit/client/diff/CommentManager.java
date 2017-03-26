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
name|DiffObject
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
name|CallbackGroup
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
name|extensions
operator|.
name|client
operator|.
name|Side
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
name|Collection
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
name|Map
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

begin_comment
comment|/** Tracks comment widgets for {@link DiffScreen}. */
end_comment

begin_class
DECL|class|CommentManager
specifier|abstract
class|class
name|CommentManager
block|{
DECL|field|base
specifier|private
specifier|final
name|DiffObject
name|base
decl_stmt|;
DECL|field|revision
specifier|private
specifier|final
name|PatchSet
operator|.
name|Id
name|revision
decl_stmt|;
DECL|field|path
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
DECL|field|commentLinkProcessor
specifier|private
specifier|final
name|CommentLinkProcessor
name|commentLinkProcessor
decl_stmt|;
DECL|field|sideA
specifier|final
name|SortedMap
argument_list|<
name|Integer
argument_list|,
name|CommentGroup
argument_list|>
name|sideA
decl_stmt|;
DECL|field|sideB
specifier|final
name|SortedMap
argument_list|<
name|Integer
argument_list|,
name|CommentGroup
argument_list|>
name|sideB
decl_stmt|;
DECL|field|published
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|PublishedBox
argument_list|>
name|published
decl_stmt|;
DECL|field|unsavedDrafts
specifier|private
specifier|final
name|Set
argument_list|<
name|DraftBox
argument_list|>
name|unsavedDrafts
decl_stmt|;
DECL|field|host
specifier|final
name|DiffScreen
name|host
decl_stmt|;
DECL|field|attached
specifier|private
name|boolean
name|attached
decl_stmt|;
DECL|field|expandAll
specifier|private
name|boolean
name|expandAll
decl_stmt|;
DECL|field|open
specifier|private
name|boolean
name|open
decl_stmt|;
DECL|method|CommentManager ( DiffScreen host, DiffObject base, PatchSet.Id revision, String path, CommentLinkProcessor clp, boolean open)
name|CommentManager
parameter_list|(
name|DiffScreen
name|host
parameter_list|,
name|DiffObject
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
name|this
operator|.
name|host
operator|=
name|host
expr_stmt|;
name|this
operator|.
name|base
operator|=
name|base
expr_stmt|;
name|this
operator|.
name|revision
operator|=
name|revision
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|commentLinkProcessor
operator|=
name|clp
expr_stmt|;
name|this
operator|.
name|open
operator|=
name|open
expr_stmt|;
name|published
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|unsavedDrafts
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
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
DECL|method|setAttached (boolean attached)
name|void
name|setAttached
parameter_list|(
name|boolean
name|attached
parameter_list|)
block|{
name|this
operator|.
name|attached
operator|=
name|attached
expr_stmt|;
block|}
DECL|method|isAttached ()
name|boolean
name|isAttached
parameter_list|()
block|{
return|return
name|attached
return|;
block|}
DECL|method|setExpandAll (boolean expandAll)
name|void
name|setExpandAll
parameter_list|(
name|boolean
name|expandAll
parameter_list|)
block|{
name|this
operator|.
name|expandAll
operator|=
name|expandAll
expr_stmt|;
block|}
DECL|method|isExpandAll ()
name|boolean
name|isExpandAll
parameter_list|()
block|{
return|return
name|expandAll
return|;
block|}
DECL|method|isOpen ()
name|boolean
name|isOpen
parameter_list|()
block|{
return|return
name|open
return|;
block|}
DECL|method|getPath ()
name|String
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
DECL|method|getPublished ()
name|Map
argument_list|<
name|String
argument_list|,
name|PublishedBox
argument_list|>
name|getPublished
parameter_list|()
block|{
return|return
name|published
return|;
block|}
DECL|method|getCommentLinkProcessor ()
name|CommentLinkProcessor
name|getCommentLinkProcessor
parameter_list|()
block|{
return|return
name|commentLinkProcessor
return|;
block|}
DECL|method|renderDrafts (DisplaySide forSide, JsArray<CommentInfo> in)
name|void
name|renderDrafts
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
name|addDraftBox
argument_list|(
name|side
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|setUnsaved (DraftBox box, boolean isUnsaved)
name|void
name|setUnsaved
parameter_list|(
name|DraftBox
name|box
parameter_list|,
name|boolean
name|isUnsaved
parameter_list|)
block|{
if|if
condition|(
name|isUnsaved
condition|)
block|{
name|unsavedDrafts
operator|.
name|add
argument_list|(
name|box
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|unsavedDrafts
operator|.
name|remove
argument_list|(
name|box
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|saveAllDrafts (CallbackGroup cb)
name|void
name|saveAllDrafts
parameter_list|(
name|CallbackGroup
name|cb
parameter_list|)
block|{
for|for
control|(
name|DraftBox
name|box
range|:
name|unsavedDrafts
control|)
block|{
name|box
operator|.
name|save
argument_list|(
name|cb
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getStoredSideFromDisplaySide (DisplaySide side)
name|Side
name|getStoredSideFromDisplaySide
parameter_list|(
name|DisplaySide
name|side
parameter_list|)
block|{
if|if
condition|(
name|side
operator|==
name|DisplaySide
operator|.
name|A
operator|&&
operator|(
name|base
operator|.
name|isBaseOrAutoMerge
argument_list|()
operator|||
name|base
operator|.
name|isParent
argument_list|()
operator|)
condition|)
block|{
return|return
name|Side
operator|.
name|PARENT
return|;
block|}
return|return
name|Side
operator|.
name|REVISION
return|;
block|}
DECL|method|getParentNumFromDisplaySide (DisplaySide side)
name|int
name|getParentNumFromDisplaySide
parameter_list|(
name|DisplaySide
name|side
parameter_list|)
block|{
if|if
condition|(
name|side
operator|==
name|DisplaySide
operator|.
name|A
condition|)
block|{
return|return
name|base
operator|.
name|getParentNum
argument_list|()
return|;
block|}
return|return
literal|0
return|;
block|}
DECL|method|getPatchSetIdFromSide (DisplaySide side)
name|PatchSet
operator|.
name|Id
name|getPatchSetIdFromSide
parameter_list|(
name|DisplaySide
name|side
parameter_list|)
block|{
if|if
condition|(
name|side
operator|==
name|DisplaySide
operator|.
name|A
operator|&&
operator|(
name|base
operator|.
name|isPatchSet
argument_list|()
operator|||
name|base
operator|.
name|isEdit
argument_list|()
operator|)
condition|)
block|{
return|return
name|base
operator|.
name|asPatchSetId
argument_list|()
return|;
block|}
return|return
name|revision
return|;
block|}
DECL|method|displaySide (CommentInfo info, DisplaySide forSide)
name|DisplaySide
name|displaySide
parameter_list|(
name|CommentInfo
name|info
parameter_list|,
name|DisplaySide
name|forSide
parameter_list|)
block|{
if|if
condition|(
name|info
operator|.
name|side
argument_list|()
operator|==
name|Side
operator|.
name|PARENT
condition|)
block|{
return|return
operator|(
name|base
operator|.
name|isBaseOrAutoMerge
argument_list|()
operator|||
name|base
operator|.
name|isParent
argument_list|()
operator|)
condition|?
name|DisplaySide
operator|.
name|A
else|:
literal|null
return|;
block|}
return|return
name|forSide
return|;
block|}
DECL|method|adjustSelection (CodeMirror cm)
specifier|static
name|FromTo
name|adjustSelection
parameter_list|(
name|CodeMirror
name|cm
parameter_list|)
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
name|to
init|=
name|fromTo
operator|.
name|to
argument_list|()
decl_stmt|;
if|if
condition|(
name|to
operator|.
name|ch
argument_list|()
operator|==
literal|0
condition|)
block|{
name|to
operator|.
name|line
argument_list|(
name|to
operator|.
name|line
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|to
operator|.
name|ch
argument_list|(
name|cm
operator|.
name|getLine
argument_list|(
name|to
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
return|return
name|fromTo
return|;
block|}
DECL|method|group (DisplaySide side, int cmLinePlusOne)
specifier|abstract
name|CommentGroup
name|group
parameter_list|(
name|DisplaySide
name|side
parameter_list|,
name|int
name|cmLinePlusOne
parameter_list|)
function_decl|;
comment|/**    * Create a new {@link DraftBox} at the specified line and focus it.    *    * @param side which side the draft will appear on.    * @param line the line the draft will be at. Lines are 1-based. Line 0 is a special case creating    *     a file level comment.    */
DECL|method|insertNewDraft (DisplaySide side, int line)
name|void
name|insertNewDraft
parameter_list|(
name|DisplaySide
name|side
parameter_list|,
name|int
name|line
parameter_list|)
block|{
if|if
condition|(
name|line
operator|==
literal|0
condition|)
block|{
name|host
operator|.
name|skipManager
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
name|line
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
name|getParentNumFromDisplaySide
argument_list|(
name|side
argument_list|)
argument_list|,
name|line
argument_list|,
literal|null
argument_list|,
literal|false
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
DECL|method|getTokenSuffixForActiveLine (CodeMirror cm)
specifier|abstract
name|String
name|getTokenSuffixForActiveLine
parameter_list|(
name|CodeMirror
name|cm
parameter_list|)
function_decl|;
DECL|method|signInCallback (CodeMirror cm)
name|Runnable
name|signInCallback
parameter_list|(
name|CodeMirror
name|cm
parameter_list|)
block|{
return|return
parameter_list|()
lambda|->
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
name|token
operator|+=
literal|"@"
operator|+
name|getTokenSuffixForActiveLine
argument_list|(
name|cm
argument_list|)
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
return|;
block|}
DECL|method|newDraft (CodeMirror cm)
specifier|abstract
name|void
name|newDraft
parameter_list|(
name|CodeMirror
name|cm
parameter_list|)
function_decl|;
DECL|method|newDraftCallback (CodeMirror cm)
name|Runnable
name|newDraftCallback
parameter_list|(
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
name|signInCallback
argument_list|(
name|cm
argument_list|)
return|;
block|}
return|return
parameter_list|()
lambda|->
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
return|;
block|}
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
name|host
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
name|getCmFromSide
argument_list|(
name|side
argument_list|)
argument_list|,
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|cmLinePlusOne
operator|-
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|box
return|;
block|}
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
name|setOpenAll
argument_list|(
name|b
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
name|setOpenAll
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getMapForNav (DisplaySide side)
specifier|abstract
name|SortedMap
argument_list|<
name|Integer
argument_list|,
name|CommentGroup
argument_list|>
name|getMapForNav
parameter_list|(
name|DisplaySide
name|side
parameter_list|)
function_decl|;
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
parameter_list|()
lambda|->
block|{
comment|// Every comment appears in both side maps as a linked pair.
comment|// It is only necessary to search one side to find a comment
comment|// on either side of the editor pair.
name|SortedMap
argument_list|<
name|Integer
argument_list|,
name|CommentGroup
argument_list|>
name|map
init|=
name|getMapForNav
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
name|CommentGroup
name|g
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
name|g
operator|=
name|map
operator|.
name|get
argument_list|(
name|map
operator|.
name|firstKey
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
name|g
operator|.
name|getBoxCount
argument_list|()
operator|==
literal|0
condition|)
block|{
name|map
operator|=
name|map
operator|.
name|tailMap
argument_list|(
name|map
operator|.
name|firstKey
argument_list|()
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
name|g
operator|=
name|map
operator|.
name|get
argument_list|(
name|map
operator|.
name|firstKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|g
operator|=
name|map
operator|.
name|get
argument_list|(
name|map
operator|.
name|lastKey
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
name|g
operator|.
name|getBoxCount
argument_list|()
operator|==
literal|0
condition|)
block|{
name|map
operator|=
name|map
operator|.
name|headMap
argument_list|(
name|map
operator|.
name|lastKey
argument_list|()
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
name|g
operator|=
name|map
operator|.
name|get
argument_list|(
name|map
operator|.
name|lastKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
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
return|;
block|}
DECL|method|clearLine (DisplaySide side, int line, CommentGroup group)
name|void
name|clearLine
parameter_list|(
name|DisplaySide
name|side
parameter_list|,
name|int
name|line
parameter_list|,
name|CommentGroup
name|group
parameter_list|)
block|{
name|SortedMap
argument_list|<
name|Integer
argument_list|,
name|CommentGroup
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
name|line
argument_list|)
operator|==
name|group
condition|)
block|{
name|map
operator|.
name|remove
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
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
operator|+
literal|1
decl_stmt|;
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
name|side
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
name|host
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
name|getCmFromSide
argument_list|(
name|side
argument_list|)
argument_list|,
name|cmLinePlusOne
operator|-
literal|1
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
DECL|method|getLinesWithCommentGroups ()
specifier|abstract
name|Collection
argument_list|<
name|Integer
argument_list|>
name|getLinesWithCommentGroups
parameter_list|()
function_decl|;
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
break|break;
block|}
block|}
block|}
for|for
control|(
name|int
name|boxLine
range|:
name|getLinesWithCommentGroups
argument_list|()
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
name|getStartB
argument_list|()
argument_list|,
name|DisplaySide
operator|.
name|B
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
DECL|method|newDraftOnGutterClick (CodeMirror cm, String gutterClass, int line)
specifier|abstract
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
name|line
parameter_list|)
function_decl|;
DECL|method|getCommentGroupOnActiveLine (CodeMirror cm)
specifier|abstract
name|CommentGroup
name|getCommentGroupOnActiveLine
parameter_list|(
name|CodeMirror
name|cm
parameter_list|)
function_decl|;
DECL|method|toggleOpenBox (CodeMirror cm)
name|Runnable
name|toggleOpenBox
parameter_list|(
name|CodeMirror
name|cm
parameter_list|)
block|{
return|return
parameter_list|()
lambda|->
block|{
name|CommentGroup
name|group
init|=
name|getCommentGroupOnActiveLine
argument_list|(
name|cm
argument_list|)
decl_stmt|;
if|if
condition|(
name|group
operator|!=
literal|null
condition|)
block|{
name|group
operator|.
name|openCloseLast
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
DECL|method|openCloseAll (CodeMirror cm)
name|Runnable
name|openCloseAll
parameter_list|(
name|CodeMirror
name|cm
parameter_list|)
block|{
return|return
parameter_list|()
lambda|->
block|{
name|CommentGroup
name|group
init|=
name|getCommentGroupOnActiveLine
argument_list|(
name|cm
argument_list|)
decl_stmt|;
if|if
condition|(
name|group
operator|!=
literal|null
condition|)
block|{
name|group
operator|.
name|openCloseAll
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
DECL|method|map (DisplaySide side)
name|SortedMap
argument_list|<
name|Integer
argument_list|,
name|CommentGroup
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

