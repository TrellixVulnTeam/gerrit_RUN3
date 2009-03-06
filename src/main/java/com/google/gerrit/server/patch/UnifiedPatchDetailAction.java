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
DECL|package|com.google.gerrit.server.patch
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|patch
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|util
operator|.
name|RawParseUtils
operator|.
name|decode
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|util
operator|.
name|RawParseUtils
operator|.
name|nextLF
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
name|data
operator|.
name|PatchLine
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
name|data
operator|.
name|UnifiedPatchDetail
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
name|data
operator|.
name|PatchLine
operator|.
name|Type
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
name|reviewdb
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
name|client
operator|.
name|reviewdb
operator|.
name|ReviewDb
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
name|BaseServiceImplementation
operator|.
name|Failure
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
name|OrmException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Constants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|patch
operator|.
name|HunkHeader
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

begin_class
DECL|class|UnifiedPatchDetailAction
class|class
name|UnifiedPatchDetailAction
extends|extends
name|PatchDetailAction
argument_list|<
name|UnifiedPatchDetail
argument_list|>
block|{
DECL|method|UnifiedPatchDetailAction (final Patch.Key key)
name|UnifiedPatchDetailAction
parameter_list|(
specifier|final
name|Patch
operator|.
name|Key
name|key
parameter_list|)
block|{
name|super
argument_list|(
literal|null
argument_list|,
name|key
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|run (final ReviewDb db)
specifier|public
name|UnifiedPatchDetail
name|run
parameter_list|(
specifier|final
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
throws|,
name|Failure
block|{
name|init
argument_list|(
name|db
argument_list|)
expr_stmt|;
specifier|final
name|byte
index|[]
name|buf
init|=
name|file
operator|.
name|getBuffer
argument_list|()
decl_stmt|;
name|int
name|ptr
init|=
name|file
operator|.
name|getStartOffset
argument_list|()
decl_stmt|;
specifier|final
name|int
name|end
init|=
name|file
operator|.
name|getEndOffset
argument_list|()
decl_stmt|;
specifier|final
name|int
name|hdrEnd
decl_stmt|;
specifier|final
name|ArrayList
argument_list|<
name|PatchLine
argument_list|>
name|lines
init|=
operator|new
name|ArrayList
argument_list|<
name|PatchLine
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|getHunks
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|hdrEnd
operator|=
name|file
operator|.
name|getHunks
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStartOffset
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|file
operator|.
name|getForwardBinaryHunk
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|hdrEnd
operator|=
name|file
operator|.
name|getForwardBinaryHunk
argument_list|()
operator|.
name|getStartOffset
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|file
operator|.
name|getReverseBinaryHunk
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|hdrEnd
operator|=
name|file
operator|.
name|getReverseBinaryHunk
argument_list|()
operator|.
name|getStartOffset
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|hdrEnd
operator|=
name|end
expr_stmt|;
block|}
name|int
name|eol
decl_stmt|;
for|for
control|(
init|;
name|ptr
operator|<
name|hdrEnd
condition|;
name|ptr
operator|=
name|eol
control|)
block|{
name|eol
operator|=
name|nextLF
argument_list|(
name|buf
argument_list|,
name|ptr
argument_list|)
expr_stmt|;
name|lines
operator|.
name|add
argument_list|(
operator|new
name|PatchLine
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|Type
operator|.
name|FILE_HEADER
argument_list|,
name|decode
argument_list|(
name|Constants
operator|.
name|CHARSET
argument_list|,
name|buf
argument_list|,
name|ptr
argument_list|,
name|eol
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
specifier|final
name|HunkHeader
name|h
range|:
name|file
operator|.
name|getHunks
argument_list|()
control|)
block|{
specifier|final
name|int
name|hunkEnd
init|=
name|h
operator|.
name|getEndOffset
argument_list|()
decl_stmt|;
if|if
condition|(
name|ptr
operator|<
name|hunkEnd
condition|)
block|{
name|eol
operator|=
name|nextLF
argument_list|(
name|buf
argument_list|,
name|ptr
argument_list|)
expr_stmt|;
name|lines
operator|.
name|add
argument_list|(
operator|new
name|PatchLine
argument_list|(
name|h
operator|.
name|getOldImage
argument_list|()
operator|.
name|getStartLine
argument_list|()
argument_list|,
name|h
operator|.
name|getNewStartLine
argument_list|()
argument_list|,
name|Type
operator|.
name|HUNK_HEADER
argument_list|,
name|decode
argument_list|(
name|Constants
operator|.
name|CHARSET
argument_list|,
name|buf
argument_list|,
name|ptr
argument_list|,
name|eol
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|ptr
operator|=
name|eol
expr_stmt|;
block|}
name|int
name|oldLine
init|=
name|h
operator|.
name|getOldImage
argument_list|()
operator|.
name|getStartLine
argument_list|()
operator|-
literal|1
decl_stmt|;
name|int
name|newLine
init|=
name|h
operator|.
name|getNewStartLine
argument_list|()
operator|-
literal|1
decl_stmt|;
name|SCAN
label|:
for|for
control|(
init|;
name|ptr
operator|<
name|hunkEnd
condition|;
name|ptr
operator|=
name|eol
control|)
block|{
name|eol
operator|=
name|nextLF
argument_list|(
name|buf
argument_list|,
name|ptr
argument_list|)
expr_stmt|;
specifier|final
name|PatchLine
operator|.
name|Type
name|type
decl_stmt|;
switch|switch
condition|(
name|buf
index|[
name|ptr
index|]
condition|)
block|{
case|case
literal|' '
case|:
case|case
literal|'\n'
case|:
name|oldLine
operator|++
expr_stmt|;
name|newLine
operator|++
expr_stmt|;
name|type
operator|=
name|Type
operator|.
name|CONTEXT
expr_stmt|;
break|break;
case|case
literal|'-'
case|:
name|oldLine
operator|++
expr_stmt|;
name|type
operator|=
name|Type
operator|.
name|PRE_IMAGE
expr_stmt|;
break|break;
case|case
literal|'+'
case|:
name|newLine
operator|++
expr_stmt|;
name|type
operator|=
name|Type
operator|.
name|POST_IMAGE
expr_stmt|;
break|break;
case|case
literal|'\\'
case|:
name|type
operator|=
name|Type
operator|.
name|CONTEXT
expr_stmt|;
break|break;
default|default:
break|break
name|SCAN
break|;
block|}
specifier|final
name|PatchLine
name|pLine
init|=
operator|new
name|PatchLine
argument_list|(
name|oldLine
argument_list|,
name|newLine
argument_list|,
name|type
argument_list|,
name|decode
argument_list|(
name|Constants
operator|.
name|CHARSET
argument_list|,
name|buf
argument_list|,
name|ptr
argument_list|,
name|eol
argument_list|)
argument_list|)
decl_stmt|;
name|addComments
argument_list|(
name|pLine
argument_list|,
name|published
argument_list|,
literal|0
argument_list|,
name|oldLine
argument_list|)
expr_stmt|;
name|addComments
argument_list|(
name|pLine
argument_list|,
name|published
argument_list|,
literal|1
argument_list|,
name|newLine
argument_list|)
expr_stmt|;
if|if
condition|(
name|drafted
operator|!=
literal|null
condition|)
block|{
name|addComments
argument_list|(
name|pLine
argument_list|,
name|drafted
argument_list|,
literal|0
argument_list|,
name|oldLine
argument_list|)
expr_stmt|;
name|addComments
argument_list|(
name|pLine
argument_list|,
name|drafted
argument_list|,
literal|1
argument_list|,
name|newLine
argument_list|)
expr_stmt|;
block|}
name|lines
operator|.
name|add
argument_list|(
name|pLine
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|UnifiedPatchDetail
name|d
decl_stmt|;
name|d
operator|=
operator|new
name|UnifiedPatchDetail
argument_list|(
name|patch
argument_list|,
name|accountInfo
operator|.
name|create
argument_list|()
argument_list|)
expr_stmt|;
name|d
operator|.
name|setLines
argument_list|(
name|lines
argument_list|)
expr_stmt|;
return|return
name|d
return|;
block|}
block|}
end_class

end_unit

