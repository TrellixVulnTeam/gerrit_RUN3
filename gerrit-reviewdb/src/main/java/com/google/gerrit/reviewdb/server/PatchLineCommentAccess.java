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
DECL|package|com.google.gerrit.reviewdb.server
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|reviewdb
operator|.
name|server
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
name|reviewdb
operator|.
name|client
operator|.
name|Account
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
name|reviewdb
operator|.
name|client
operator|.
name|PatchLineComment
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
name|gwtorm
operator|.
name|server
operator|.
name|Access
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
name|server
operator|.
name|OrmException
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
name|server
operator|.
name|PrimaryKey
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
name|server
operator|.
name|Query
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
name|server
operator|.
name|ResultSet
import|;
end_import

begin_interface
DECL|interface|PatchLineCommentAccess
specifier|public
interface|interface
name|PatchLineCommentAccess
extends|extends
name|Access
argument_list|<
name|PatchLineComment
argument_list|,
name|PatchLineComment
operator|.
name|Key
argument_list|>
block|{
annotation|@
name|Override
annotation|@
name|PrimaryKey
argument_list|(
literal|"key"
argument_list|)
DECL|method|get (PatchLineComment.Key id)
name|PatchLineComment
name|get
parameter_list|(
name|PatchLineComment
operator|.
name|Key
name|id
parameter_list|)
throws|throws
name|OrmException
function_decl|;
annotation|@
name|Query
argument_list|(
literal|"WHERE key.patchKey.patchSetId.changeId = ?"
argument_list|)
DECL|method|byChange (Change.Id id)
name|ResultSet
argument_list|<
name|PatchLineComment
argument_list|>
name|byChange
parameter_list|(
name|Change
operator|.
name|Id
name|id
parameter_list|)
throws|throws
name|OrmException
function_decl|;
annotation|@
name|Query
argument_list|(
literal|"WHERE key.patchKey.patchSetId = ?"
argument_list|)
DECL|method|byPatchSet (PatchSet.Id id)
name|ResultSet
argument_list|<
name|PatchLineComment
argument_list|>
name|byPatchSet
parameter_list|(
name|PatchSet
operator|.
name|Id
name|id
parameter_list|)
throws|throws
name|OrmException
function_decl|;
annotation|@
name|Query
argument_list|(
literal|"WHERE key.patchKey.patchSetId.changeId = ?"
operator|+
literal|" AND key.patchKey.fileName = ? AND status = '"
operator|+
name|PatchLineComment
operator|.
name|STATUS_PUBLISHED
operator|+
literal|"' ORDER BY lineNbr,writtenOn"
argument_list|)
DECL|method|publishedByChangeFile (Change.Id id, String file)
name|ResultSet
argument_list|<
name|PatchLineComment
argument_list|>
name|publishedByChangeFile
parameter_list|(
name|Change
operator|.
name|Id
name|id
parameter_list|,
name|String
name|file
parameter_list|)
throws|throws
name|OrmException
function_decl|;
annotation|@
name|Query
argument_list|(
literal|"WHERE key.patchKey.patchSetId = ? AND status = '"
operator|+
name|PatchLineComment
operator|.
name|STATUS_PUBLISHED
operator|+
literal|"'"
argument_list|)
DECL|method|publishedByPatchSet (PatchSet.Id patchset)
name|ResultSet
argument_list|<
name|PatchLineComment
argument_list|>
name|publishedByPatchSet
parameter_list|(
name|PatchSet
operator|.
name|Id
name|patchset
parameter_list|)
throws|throws
name|OrmException
function_decl|;
annotation|@
name|Query
argument_list|(
literal|"WHERE key.patchKey.patchSetId = ? AND status = '"
operator|+
name|PatchLineComment
operator|.
name|STATUS_DRAFT
operator|+
literal|"' AND author = ? ORDER BY key.patchKey,lineNbr,writtenOn"
argument_list|)
DECL|method|draftByPatchSetAuthor (PatchSet.Id patchset, Account.Id author)
name|ResultSet
argument_list|<
name|PatchLineComment
argument_list|>
name|draftByPatchSetAuthor
parameter_list|(
name|PatchSet
operator|.
name|Id
name|patchset
parameter_list|,
name|Account
operator|.
name|Id
name|author
parameter_list|)
throws|throws
name|OrmException
function_decl|;
annotation|@
name|Query
argument_list|(
literal|"WHERE key.patchKey.patchSetId.changeId = ?"
operator|+
literal|" AND key.patchKey.fileName = ? AND author = ? AND status = '"
operator|+
name|PatchLineComment
operator|.
name|STATUS_DRAFT
operator|+
literal|"' ORDER BY lineNbr,writtenOn"
argument_list|)
DECL|method|draftByChangeFileAuthor (Change.Id id, String file, Account.Id author)
name|ResultSet
argument_list|<
name|PatchLineComment
argument_list|>
name|draftByChangeFileAuthor
parameter_list|(
name|Change
operator|.
name|Id
name|id
parameter_list|,
name|String
name|file
parameter_list|,
name|Account
operator|.
name|Id
name|author
parameter_list|)
throws|throws
name|OrmException
function_decl|;
annotation|@
name|Query
argument_list|(
literal|"WHERE status = '"
operator|+
name|PatchLineComment
operator|.
name|STATUS_DRAFT
operator|+
literal|"' AND author = ?"
argument_list|)
DECL|method|draftByAuthor (Account.Id author)
name|ResultSet
argument_list|<
name|PatchLineComment
argument_list|>
name|draftByAuthor
parameter_list|(
name|Account
operator|.
name|Id
name|author
parameter_list|)
throws|throws
name|OrmException
function_decl|;
annotation|@
name|Query
DECL|method|all ()
name|ResultSet
argument_list|<
name|PatchLineComment
argument_list|>
name|all
parameter_list|()
throws|throws
name|OrmException
function_decl|;
block|}
end_interface

end_unit

