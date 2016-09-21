begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2008 The Android Open Source Project
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
DECL|package|com.google.gerrit.reviewdb.client
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|reviewdb
operator|.
name|client
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|client
operator|.
name|Column
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
name|client
operator|.
name|StringKey
import|;
end_import

begin_comment
comment|/** A single modified file in a {@link PatchSet}. */
end_comment

begin_class
DECL|class|Patch
specifier|public
specifier|final
class|class
name|Patch
block|{
comment|/** Magical file name which represents the commit message. */
DECL|field|COMMIT_MSG
specifier|public
specifier|static
specifier|final
name|String
name|COMMIT_MSG
init|=
literal|"/COMMIT_MSG"
decl_stmt|;
comment|/** Magical file name which represents the merge list of a merge commit. */
DECL|field|MERGE_LIST
specifier|public
specifier|static
specifier|final
name|String
name|MERGE_LIST
init|=
literal|"/MERGE_LIST"
decl_stmt|;
comment|/**    * Checks if the given path represents a magic file. A magic file is a    * generated file that is automatically included into changes. It does not    * exist in the commit of the patch set.    *    * @param path the file path    * @return {@code true} if the path represents a magic file, otherwise    *         {@code false}.    */
DECL|method|isMagic (String path)
specifier|public
specifier|static
name|boolean
name|isMagic
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|COMMIT_MSG
operator|.
name|equals
argument_list|(
name|path
argument_list|)
operator|||
name|MERGE_LIST
operator|.
name|equals
argument_list|(
name|path
argument_list|)
return|;
block|}
DECL|class|Key
specifier|public
specifier|static
class|class
name|Key
extends|extends
name|StringKey
argument_list|<
name|PatchSet
operator|.
name|Id
argument_list|>
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|1
argument_list|,
name|name
operator|=
name|Column
operator|.
name|NONE
argument_list|)
DECL|field|patchSetId
specifier|protected
name|PatchSet
operator|.
name|Id
name|patchSetId
decl_stmt|;
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|2
argument_list|)
DECL|field|fileName
specifier|protected
name|String
name|fileName
decl_stmt|;
DECL|method|Key ()
specifier|protected
name|Key
parameter_list|()
block|{
name|patchSetId
operator|=
operator|new
name|PatchSet
operator|.
name|Id
argument_list|()
expr_stmt|;
block|}
DECL|method|Key (final PatchSet.Id ps, final String name)
specifier|public
name|Key
parameter_list|(
specifier|final
name|PatchSet
operator|.
name|Id
name|ps
parameter_list|,
specifier|final
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|patchSetId
operator|=
name|ps
expr_stmt|;
name|this
operator|.
name|fileName
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getParentKey ()
specifier|public
name|PatchSet
operator|.
name|Id
name|getParentKey
parameter_list|()
block|{
return|return
name|patchSetId
return|;
block|}
annotation|@
name|Override
DECL|method|get ()
specifier|public
name|String
name|get
parameter_list|()
block|{
return|return
name|fileName
return|;
block|}
annotation|@
name|Override
DECL|method|set (String newValue)
specifier|protected
name|void
name|set
parameter_list|(
name|String
name|newValue
parameter_list|)
block|{
name|fileName
operator|=
name|newValue
expr_stmt|;
block|}
comment|/** Parse a Patch.Id out of a string representation. */
DECL|method|parse (final String str)
specifier|public
specifier|static
name|Key
name|parse
parameter_list|(
specifier|final
name|String
name|str
parameter_list|)
block|{
specifier|final
name|Key
name|r
init|=
operator|new
name|Key
argument_list|()
decl_stmt|;
name|r
operator|.
name|fromString
argument_list|(
name|str
argument_list|)
expr_stmt|;
return|return
name|r
return|;
block|}
DECL|method|getFileName ()
specifier|public
name|String
name|getFileName
parameter_list|()
block|{
return|return
name|get
argument_list|()
return|;
block|}
block|}
comment|/** Type of modification made to the file path. */
DECL|enum|ChangeType
specifier|public
enum|enum
name|ChangeType
implements|implements
name|CodedEnum
block|{
comment|/** Path is being created/introduced by this patch. */
DECL|enumConstant|ADDED
name|ADDED
argument_list|(
literal|'A'
argument_list|)
block|,
comment|/** Path already exists, and has updated content. */
DECL|enumConstant|MODIFIED
name|MODIFIED
argument_list|(
literal|'M'
argument_list|)
block|,
comment|/** Path existed, but is being removed by this patch. */
DECL|enumConstant|DELETED
name|DELETED
argument_list|(
literal|'D'
argument_list|)
block|,
comment|/** Path existed at {@link Patch#getSourceFileName()} but was moved. */
DECL|enumConstant|RENAMED
name|RENAMED
argument_list|(
literal|'R'
argument_list|)
block|,
comment|/** Path was copied from {@link Patch#getSourceFileName()}. */
DECL|enumConstant|COPIED
name|COPIED
argument_list|(
literal|'C'
argument_list|)
block|,
comment|/** Sufficient amount of content changed to claim the file was rewritten. */
DECL|enumConstant|REWRITE
name|REWRITE
argument_list|(
literal|'W'
argument_list|)
block|;
DECL|field|code
specifier|private
specifier|final
name|char
name|code
decl_stmt|;
DECL|method|ChangeType (final char c)
name|ChangeType
parameter_list|(
specifier|final
name|char
name|c
parameter_list|)
block|{
name|code
operator|=
name|c
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCode ()
specifier|public
name|char
name|getCode
parameter_list|()
block|{
return|return
name|code
return|;
block|}
DECL|method|matches (String s)
specifier|public
name|boolean
name|matches
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|s
operator|!=
literal|null
operator|&&
name|s
operator|.
name|length
argument_list|()
operator|==
literal|1
operator|&&
name|s
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
name|code
return|;
block|}
DECL|method|forCode (final char c)
specifier|public
specifier|static
name|ChangeType
name|forCode
parameter_list|(
specifier|final
name|char
name|c
parameter_list|)
block|{
for|for
control|(
specifier|final
name|ChangeType
name|s
range|:
name|ChangeType
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|s
operator|.
name|code
operator|==
name|c
condition|)
block|{
return|return
name|s
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
comment|/** Type of formatting for this patch. */
DECL|enum|PatchType
specifier|public
enum|enum
name|PatchType
implements|implements
name|CodedEnum
block|{
comment|/**      * A textual difference between two versions.      *      *<p>      * A UNIFIED patch can be rendered in multiple ways. Most commonly, it is      * rendered as a side by side display using two columns, left column for the      * old version, right column for the new version. A UNIFIED patch can also      * be formatted in a number of standard "patch script" styles, but typically      * is formatted in the POSIX standard unified diff format.      *      *<p>      * Usually Gerrit renders a UNIFIED patch in a PatchScreen.SideBySide view,      * presenting the file in two columns. If the user chooses, a      * PatchScreen.Unified is also a valid display method.      * */
DECL|enumConstant|UNIFIED
name|UNIFIED
argument_list|(
literal|'U'
argument_list|)
block|,
comment|/**      * Difference of two (or more) binary contents.      *      *<p>      * A BINARY patch cannot be viewed in a text display, as it represents a      * change in binary content at the associated path, for example, an image      * file has been replaced with a different image.      *      *<p>      * Gerrit can only render a BINARY file in a PatchScreen.Unified view, as      * the only information it can display is the old and new file content      * hashes.      */
DECL|enumConstant|BINARY
name|BINARY
argument_list|(
literal|'B'
argument_list|)
block|;
DECL|field|code
specifier|private
specifier|final
name|char
name|code
decl_stmt|;
DECL|method|PatchType (final char c)
name|PatchType
parameter_list|(
specifier|final
name|char
name|c
parameter_list|)
block|{
name|code
operator|=
name|c
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCode ()
specifier|public
name|char
name|getCode
parameter_list|()
block|{
return|return
name|code
return|;
block|}
DECL|method|forCode (final char c)
specifier|public
specifier|static
name|PatchType
name|forCode
parameter_list|(
specifier|final
name|char
name|c
parameter_list|)
block|{
for|for
control|(
specifier|final
name|PatchType
name|s
range|:
name|PatchType
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|s
operator|.
name|code
operator|==
name|c
condition|)
block|{
return|return
name|s
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
DECL|field|key
specifier|protected
name|Key
name|key
decl_stmt|;
comment|/** What sort of change is this to the path; see {@link ChangeType}. */
DECL|field|changeType
specifier|protected
name|char
name|changeType
decl_stmt|;
comment|/** What type of patch is this; see {@link PatchType}. */
DECL|field|patchType
specifier|protected
name|char
name|patchType
decl_stmt|;
comment|/** Number of published comments on this patch. */
DECL|field|nbrComments
specifier|protected
name|int
name|nbrComments
decl_stmt|;
comment|/** Number of drafts by the current user; not persisted in the datastore. */
DECL|field|nbrDrafts
specifier|protected
name|int
name|nbrDrafts
decl_stmt|;
comment|/** Number of lines added to the file. */
DECL|field|insertions
specifier|protected
name|int
name|insertions
decl_stmt|;
comment|/** Number of lines deleted from the file. */
DECL|field|deletions
specifier|protected
name|int
name|deletions
decl_stmt|;
comment|/**    * Original if {@link #changeType} is {@link ChangeType#COPIED} or    * {@link ChangeType#RENAMED}.    */
DECL|field|sourceFileName
specifier|protected
name|String
name|sourceFileName
decl_stmt|;
comment|/** True if this patch has been reviewed by the current logged in user */
DECL|field|reviewedByCurrentUser
specifier|private
name|boolean
name|reviewedByCurrentUser
decl_stmt|;
DECL|method|Patch ()
specifier|protected
name|Patch
parameter_list|()
block|{   }
DECL|method|Patch (final Patch.Key newId)
specifier|public
name|Patch
parameter_list|(
specifier|final
name|Patch
operator|.
name|Key
name|newId
parameter_list|)
block|{
name|key
operator|=
name|newId
expr_stmt|;
name|setChangeType
argument_list|(
name|ChangeType
operator|.
name|MODIFIED
argument_list|)
expr_stmt|;
name|setPatchType
argument_list|(
name|PatchType
operator|.
name|UNIFIED
argument_list|)
expr_stmt|;
block|}
DECL|method|getKey ()
specifier|public
name|Patch
operator|.
name|Key
name|getKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
DECL|method|getCommentCount ()
specifier|public
name|int
name|getCommentCount
parameter_list|()
block|{
return|return
name|nbrComments
return|;
block|}
DECL|method|setCommentCount (final int n)
specifier|public
name|void
name|setCommentCount
parameter_list|(
specifier|final
name|int
name|n
parameter_list|)
block|{
name|nbrComments
operator|=
name|n
expr_stmt|;
block|}
DECL|method|getDraftCount ()
specifier|public
name|int
name|getDraftCount
parameter_list|()
block|{
return|return
name|nbrDrafts
return|;
block|}
DECL|method|setDraftCount (final int n)
specifier|public
name|void
name|setDraftCount
parameter_list|(
specifier|final
name|int
name|n
parameter_list|)
block|{
name|nbrDrafts
operator|=
name|n
expr_stmt|;
block|}
DECL|method|getInsertions ()
specifier|public
name|int
name|getInsertions
parameter_list|()
block|{
return|return
name|insertions
return|;
block|}
DECL|method|setInsertions (int n)
specifier|public
name|void
name|setInsertions
parameter_list|(
name|int
name|n
parameter_list|)
block|{
name|insertions
operator|=
name|n
expr_stmt|;
block|}
DECL|method|getDeletions ()
specifier|public
name|int
name|getDeletions
parameter_list|()
block|{
return|return
name|deletions
return|;
block|}
DECL|method|setDeletions (int n)
specifier|public
name|void
name|setDeletions
parameter_list|(
name|int
name|n
parameter_list|)
block|{
name|deletions
operator|=
name|n
expr_stmt|;
block|}
DECL|method|getChangeType ()
specifier|public
name|ChangeType
name|getChangeType
parameter_list|()
block|{
return|return
name|ChangeType
operator|.
name|forCode
argument_list|(
name|changeType
argument_list|)
return|;
block|}
DECL|method|setChangeType (final ChangeType type)
specifier|public
name|void
name|setChangeType
parameter_list|(
specifier|final
name|ChangeType
name|type
parameter_list|)
block|{
name|changeType
operator|=
name|type
operator|.
name|getCode
argument_list|()
expr_stmt|;
block|}
DECL|method|getPatchType ()
specifier|public
name|PatchType
name|getPatchType
parameter_list|()
block|{
return|return
name|PatchType
operator|.
name|forCode
argument_list|(
name|patchType
argument_list|)
return|;
block|}
DECL|method|setPatchType (final PatchType type)
specifier|public
name|void
name|setPatchType
parameter_list|(
specifier|final
name|PatchType
name|type
parameter_list|)
block|{
name|patchType
operator|=
name|type
operator|.
name|getCode
argument_list|()
expr_stmt|;
block|}
DECL|method|getFileName ()
specifier|public
name|String
name|getFileName
parameter_list|()
block|{
return|return
name|key
operator|.
name|fileName
return|;
block|}
DECL|method|getSourceFileName ()
specifier|public
name|String
name|getSourceFileName
parameter_list|()
block|{
return|return
name|sourceFileName
return|;
block|}
DECL|method|setSourceFileName (final String n)
specifier|public
name|void
name|setSourceFileName
parameter_list|(
specifier|final
name|String
name|n
parameter_list|)
block|{
name|sourceFileName
operator|=
name|n
expr_stmt|;
block|}
DECL|method|isReviewedByCurrentUser ()
specifier|public
name|boolean
name|isReviewedByCurrentUser
parameter_list|()
block|{
return|return
name|reviewedByCurrentUser
return|;
block|}
DECL|method|setReviewedByCurrentUser (boolean r)
specifier|public
name|void
name|setReviewedByCurrentUser
parameter_list|(
name|boolean
name|r
parameter_list|)
block|{
name|reviewedByCurrentUser
operator|=
name|r
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"[Patch "
operator|+
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit

