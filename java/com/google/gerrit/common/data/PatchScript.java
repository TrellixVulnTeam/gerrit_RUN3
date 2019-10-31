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
DECL|package|com.google.gerrit.common.data
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|common
operator|.
name|data
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
name|entities
operator|.
name|Change
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
name|Patch
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
name|Patch
operator|.
name|ChangeType
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
name|DiffPreferencesInfo
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
name|DiffPreferencesInfo
operator|.
name|Whitespace
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
name|prettify
operator|.
name|common
operator|.
name|SparseFileContent
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

begin_class
DECL|class|PatchScript
specifier|public
class|class
name|PatchScript
block|{
DECL|enum|DisplayMethod
specifier|public
enum|enum
name|DisplayMethod
block|{
DECL|enumConstant|NONE
name|NONE
block|,
DECL|enumConstant|DIFF
name|DIFF
block|,
DECL|enumConstant|IMG
name|IMG
block|}
DECL|enum|FileMode
specifier|public
enum|enum
name|FileMode
block|{
DECL|enumConstant|FILE
name|FILE
block|,
DECL|enumConstant|SYMLINK
name|SYMLINK
block|,
DECL|enumConstant|GITLINK
name|GITLINK
block|}
DECL|class|PatchScriptFileInfo
specifier|public
specifier|static
class|class
name|PatchScriptFileInfo
block|{
DECL|field|name
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|mode
specifier|public
specifier|final
name|FileMode
name|mode
decl_stmt|;
DECL|field|content
specifier|public
specifier|final
name|SparseFileContent
name|content
decl_stmt|;
DECL|field|displayMethod
specifier|public
specifier|final
name|DisplayMethod
name|displayMethod
decl_stmt|;
DECL|field|mimeType
specifier|public
specifier|final
name|String
name|mimeType
decl_stmt|;
DECL|field|commitId
specifier|public
specifier|final
name|String
name|commitId
decl_stmt|;
DECL|method|PatchScriptFileInfo ( String name, FileMode mode, SparseFileContent content, DisplayMethod displayMethod, String mimeType, String commitId)
specifier|public
name|PatchScriptFileInfo
parameter_list|(
name|String
name|name
parameter_list|,
name|FileMode
name|mode
parameter_list|,
name|SparseFileContent
name|content
parameter_list|,
name|DisplayMethod
name|displayMethod
parameter_list|,
name|String
name|mimeType
parameter_list|,
name|String
name|commitId
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|mode
operator|=
name|mode
expr_stmt|;
name|this
operator|.
name|content
operator|=
name|content
expr_stmt|;
name|this
operator|.
name|displayMethod
operator|=
name|displayMethod
expr_stmt|;
name|this
operator|.
name|mimeType
operator|=
name|mimeType
expr_stmt|;
name|this
operator|.
name|commitId
operator|=
name|commitId
expr_stmt|;
block|}
block|}
DECL|field|changeId
specifier|private
name|Change
operator|.
name|Key
name|changeId
decl_stmt|;
DECL|field|changeType
specifier|private
name|ChangeType
name|changeType
decl_stmt|;
DECL|field|header
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|header
decl_stmt|;
DECL|field|diffPrefs
specifier|private
name|DiffPreferencesInfo
name|diffPrefs
decl_stmt|;
DECL|field|edits
specifier|private
name|List
argument_list|<
name|Edit
argument_list|>
name|edits
decl_stmt|;
DECL|field|editsDueToRebase
specifier|private
name|Set
argument_list|<
name|Edit
argument_list|>
name|editsDueToRebase
decl_stmt|;
DECL|field|comments
specifier|private
name|CommentDetail
name|comments
decl_stmt|;
DECL|field|history
specifier|private
name|List
argument_list|<
name|Patch
argument_list|>
name|history
decl_stmt|;
DECL|field|hugeFile
specifier|private
name|boolean
name|hugeFile
decl_stmt|;
DECL|field|intralineFailure
specifier|private
name|boolean
name|intralineFailure
decl_stmt|;
DECL|field|intralineTimeout
specifier|private
name|boolean
name|intralineTimeout
decl_stmt|;
DECL|field|binary
specifier|private
name|boolean
name|binary
decl_stmt|;
DECL|field|fileInfoA
specifier|private
name|PatchScriptFileInfo
name|fileInfoA
decl_stmt|;
DECL|field|fileInfoB
specifier|private
name|PatchScriptFileInfo
name|fileInfoB
decl_stmt|;
DECL|method|PatchScript ( Change.Key ck, ChangeType ct, String on, String nn, FileMode om, FileMode nm, List<String> h, DiffPreferencesInfo dp, SparseFileContent ca, SparseFileContent cb, List<Edit> e, Set<Edit> editsDueToRebase, DisplayMethod ma, DisplayMethod mb, String mta, String mtb, CommentDetail cd, List<Patch> hist, boolean hf, boolean idf, boolean idt, boolean bin, String cma, String cmb)
specifier|public
name|PatchScript
parameter_list|(
name|Change
operator|.
name|Key
name|ck
parameter_list|,
name|ChangeType
name|ct
parameter_list|,
name|String
name|on
parameter_list|,
name|String
name|nn
parameter_list|,
name|FileMode
name|om
parameter_list|,
name|FileMode
name|nm
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|h
parameter_list|,
name|DiffPreferencesInfo
name|dp
parameter_list|,
name|SparseFileContent
name|ca
parameter_list|,
name|SparseFileContent
name|cb
parameter_list|,
name|List
argument_list|<
name|Edit
argument_list|>
name|e
parameter_list|,
name|Set
argument_list|<
name|Edit
argument_list|>
name|editsDueToRebase
parameter_list|,
name|DisplayMethod
name|ma
parameter_list|,
name|DisplayMethod
name|mb
parameter_list|,
name|String
name|mta
parameter_list|,
name|String
name|mtb
parameter_list|,
name|CommentDetail
name|cd
parameter_list|,
name|List
argument_list|<
name|Patch
argument_list|>
name|hist
parameter_list|,
name|boolean
name|hf
parameter_list|,
name|boolean
name|idf
parameter_list|,
name|boolean
name|idt
parameter_list|,
name|boolean
name|bin
parameter_list|,
name|String
name|cma
parameter_list|,
name|String
name|cmb
parameter_list|)
block|{
name|changeId
operator|=
name|ck
expr_stmt|;
name|changeType
operator|=
name|ct
expr_stmt|;
name|header
operator|=
name|h
expr_stmt|;
name|diffPrefs
operator|=
name|dp
expr_stmt|;
name|edits
operator|=
name|e
expr_stmt|;
name|this
operator|.
name|editsDueToRebase
operator|=
name|editsDueToRebase
expr_stmt|;
name|comments
operator|=
name|cd
expr_stmt|;
name|history
operator|=
name|hist
expr_stmt|;
name|hugeFile
operator|=
name|hf
expr_stmt|;
name|intralineFailure
operator|=
name|idf
expr_stmt|;
name|intralineTimeout
operator|=
name|idt
expr_stmt|;
name|binary
operator|=
name|bin
expr_stmt|;
name|fileInfoA
operator|=
operator|new
name|PatchScriptFileInfo
argument_list|(
name|on
argument_list|,
name|om
argument_list|,
name|ca
argument_list|,
name|ma
argument_list|,
name|mta
argument_list|,
name|cma
argument_list|)
expr_stmt|;
name|fileInfoB
operator|=
operator|new
name|PatchScriptFileInfo
argument_list|(
name|nn
argument_list|,
name|nm
argument_list|,
name|cb
argument_list|,
name|mb
argument_list|,
name|mtb
argument_list|,
name|cmb
argument_list|)
expr_stmt|;
block|}
DECL|method|getChangeId ()
specifier|public
name|Change
operator|.
name|Key
name|getChangeId
parameter_list|()
block|{
return|return
name|changeId
return|;
block|}
DECL|method|getDisplayMethodA ()
specifier|public
name|DisplayMethod
name|getDisplayMethodA
parameter_list|()
block|{
return|return
name|fileInfoA
operator|.
name|displayMethod
return|;
block|}
DECL|method|getDisplayMethodB ()
specifier|public
name|DisplayMethod
name|getDisplayMethodB
parameter_list|()
block|{
return|return
name|fileInfoB
operator|.
name|displayMethod
return|;
block|}
DECL|method|getFileModeA ()
specifier|public
name|FileMode
name|getFileModeA
parameter_list|()
block|{
return|return
name|fileInfoA
operator|.
name|mode
return|;
block|}
DECL|method|getFileModeB ()
specifier|public
name|FileMode
name|getFileModeB
parameter_list|()
block|{
return|return
name|fileInfoB
operator|.
name|mode
return|;
block|}
DECL|method|getPatchHeader ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getPatchHeader
parameter_list|()
block|{
return|return
name|header
return|;
block|}
DECL|method|getChangeType ()
specifier|public
name|ChangeType
name|getChangeType
parameter_list|()
block|{
return|return
name|changeType
return|;
block|}
DECL|method|getOldName ()
specifier|public
name|String
name|getOldName
parameter_list|()
block|{
return|return
name|fileInfoA
operator|.
name|name
return|;
block|}
DECL|method|getNewName ()
specifier|public
name|String
name|getNewName
parameter_list|()
block|{
return|return
name|fileInfoB
operator|.
name|name
return|;
block|}
DECL|method|getCommentDetail ()
specifier|public
name|CommentDetail
name|getCommentDetail
parameter_list|()
block|{
return|return
name|comments
return|;
block|}
DECL|method|getHistory ()
specifier|public
name|List
argument_list|<
name|Patch
argument_list|>
name|getHistory
parameter_list|()
block|{
return|return
name|history
return|;
block|}
DECL|method|getDiffPrefs ()
specifier|public
name|DiffPreferencesInfo
name|getDiffPrefs
parameter_list|()
block|{
return|return
name|diffPrefs
return|;
block|}
DECL|method|setDiffPrefs (DiffPreferencesInfo dp)
specifier|public
name|void
name|setDiffPrefs
parameter_list|(
name|DiffPreferencesInfo
name|dp
parameter_list|)
block|{
name|diffPrefs
operator|=
name|dp
expr_stmt|;
block|}
DECL|method|isHugeFile ()
specifier|public
name|boolean
name|isHugeFile
parameter_list|()
block|{
return|return
name|hugeFile
return|;
block|}
DECL|method|isIgnoreWhitespace ()
specifier|public
name|boolean
name|isIgnoreWhitespace
parameter_list|()
block|{
return|return
name|diffPrefs
operator|.
name|ignoreWhitespace
operator|!=
name|Whitespace
operator|.
name|IGNORE_NONE
return|;
block|}
DECL|method|hasIntralineFailure ()
specifier|public
name|boolean
name|hasIntralineFailure
parameter_list|()
block|{
return|return
name|intralineFailure
return|;
block|}
DECL|method|hasIntralineTimeout ()
specifier|public
name|boolean
name|hasIntralineTimeout
parameter_list|()
block|{
return|return
name|intralineTimeout
return|;
block|}
DECL|method|isExpandAllComments ()
specifier|public
name|boolean
name|isExpandAllComments
parameter_list|()
block|{
return|return
name|diffPrefs
operator|.
name|expandAllComments
return|;
block|}
DECL|method|getA ()
specifier|public
name|SparseFileContent
name|getA
parameter_list|()
block|{
return|return
name|fileInfoA
operator|.
name|content
return|;
block|}
DECL|method|getB ()
specifier|public
name|SparseFileContent
name|getB
parameter_list|()
block|{
return|return
name|fileInfoB
operator|.
name|content
return|;
block|}
DECL|method|getMimeTypeA ()
specifier|public
name|String
name|getMimeTypeA
parameter_list|()
block|{
return|return
name|fileInfoA
operator|.
name|mimeType
return|;
block|}
DECL|method|getMimeTypeB ()
specifier|public
name|String
name|getMimeTypeB
parameter_list|()
block|{
return|return
name|fileInfoB
operator|.
name|mimeType
return|;
block|}
DECL|method|getEdits ()
specifier|public
name|List
argument_list|<
name|Edit
argument_list|>
name|getEdits
parameter_list|()
block|{
return|return
name|edits
return|;
block|}
DECL|method|getEditsDueToRebase ()
specifier|public
name|Set
argument_list|<
name|Edit
argument_list|>
name|getEditsDueToRebase
parameter_list|()
block|{
return|return
name|editsDueToRebase
return|;
block|}
DECL|method|isBinary ()
specifier|public
name|boolean
name|isBinary
parameter_list|()
block|{
return|return
name|binary
return|;
block|}
DECL|method|getCommitIdA ()
specifier|public
name|String
name|getCommitIdA
parameter_list|()
block|{
return|return
name|fileInfoA
operator|.
name|commitId
return|;
block|}
DECL|method|getCommitIdB ()
specifier|public
name|String
name|getCommitIdB
parameter_list|()
block|{
return|return
name|fileInfoB
operator|.
name|commitId
return|;
block|}
DECL|method|getFileInfoA ()
specifier|public
name|PatchScriptFileInfo
name|getFileInfoA
parameter_list|()
block|{
return|return
name|fileInfoA
return|;
block|}
DECL|method|getFileInfoB ()
specifier|public
name|PatchScriptFileInfo
name|getFileInfoB
parameter_list|()
block|{
return|return
name|fileInfoB
return|;
block|}
block|}
end_class

end_unit

