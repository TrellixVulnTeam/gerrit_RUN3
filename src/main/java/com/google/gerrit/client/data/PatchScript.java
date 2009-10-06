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
DECL|package|com.google.gerrit.client.data
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
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
name|client
operator|.
name|data
operator|.
name|PatchScriptSettings
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
name|client
operator|.
name|reviewdb
operator|.
name|Change
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
name|java
operator|.
name|util
operator|.
name|List
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
specifier|static
enum|enum
name|DisplayMethod
block|{
DECL|enumConstant|NONE
DECL|enumConstant|DIFF
DECL|enumConstant|IMG
name|NONE
block|,
name|DIFF
block|,
name|IMG
block|}
DECL|field|changeId
specifier|protected
name|Change
operator|.
name|Key
name|changeId
decl_stmt|;
DECL|field|header
specifier|protected
name|List
argument_list|<
name|String
argument_list|>
name|header
decl_stmt|;
DECL|field|settings
specifier|protected
name|PatchScriptSettings
name|settings
decl_stmt|;
DECL|field|a
specifier|protected
name|SparseFileContent
name|a
decl_stmt|;
DECL|field|b
specifier|protected
name|SparseFileContent
name|b
decl_stmt|;
DECL|field|edits
specifier|protected
name|List
argument_list|<
name|Edit
argument_list|>
name|edits
decl_stmt|;
DECL|field|displayMethodA
specifier|protected
name|DisplayMethod
name|displayMethodA
decl_stmt|;
DECL|field|displayMethodB
specifier|protected
name|DisplayMethod
name|displayMethodB
decl_stmt|;
DECL|method|PatchScript (final Change.Key ck, final List<String> h, final PatchScriptSettings s, final SparseFileContent ca, final SparseFileContent cb, final List<Edit> e, final DisplayMethod ma, final DisplayMethod mb)
specifier|public
name|PatchScript
parameter_list|(
specifier|final
name|Change
operator|.
name|Key
name|ck
parameter_list|,
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|h
parameter_list|,
specifier|final
name|PatchScriptSettings
name|s
parameter_list|,
specifier|final
name|SparseFileContent
name|ca
parameter_list|,
specifier|final
name|SparseFileContent
name|cb
parameter_list|,
specifier|final
name|List
argument_list|<
name|Edit
argument_list|>
name|e
parameter_list|,
specifier|final
name|DisplayMethod
name|ma
parameter_list|,
specifier|final
name|DisplayMethod
name|mb
parameter_list|)
block|{
name|changeId
operator|=
name|ck
expr_stmt|;
name|header
operator|=
name|h
expr_stmt|;
name|settings
operator|=
name|s
expr_stmt|;
name|a
operator|=
name|ca
expr_stmt|;
name|b
operator|=
name|cb
expr_stmt|;
name|edits
operator|=
name|e
expr_stmt|;
name|displayMethodA
operator|=
name|ma
expr_stmt|;
name|displayMethodB
operator|=
name|mb
expr_stmt|;
block|}
DECL|method|PatchScript ()
specifier|protected
name|PatchScript
parameter_list|()
block|{   }
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
name|displayMethodA
return|;
block|}
DECL|method|getDisplayMethodB ()
specifier|public
name|DisplayMethod
name|getDisplayMethodB
parameter_list|()
block|{
return|return
name|displayMethodB
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
DECL|method|getContext ()
specifier|public
name|int
name|getContext
parameter_list|()
block|{
return|return
name|settings
operator|.
name|getContext
argument_list|()
return|;
block|}
DECL|method|isIgnoreWhitespace ()
specifier|public
name|boolean
name|isIgnoreWhitespace
parameter_list|()
block|{
return|return
name|settings
operator|.
name|getWhitespace
argument_list|()
operator|!=
name|Whitespace
operator|.
name|IGNORE_NONE
return|;
block|}
DECL|method|getA ()
specifier|public
name|SparseFileContent
name|getA
parameter_list|()
block|{
return|return
name|a
return|;
block|}
DECL|method|getB ()
specifier|public
name|SparseFileContent
name|getB
parameter_list|()
block|{
return|return
name|b
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
DECL|method|getHunks ()
specifier|public
name|Iterable
argument_list|<
name|EditList
operator|.
name|Hunk
argument_list|>
name|getHunks
parameter_list|()
block|{
return|return
operator|new
name|EditList
argument_list|(
name|edits
argument_list|,
name|getContext
argument_list|()
argument_list|,
name|a
operator|.
name|size
argument_list|()
argument_list|,
name|b
operator|.
name|size
argument_list|()
argument_list|)
operator|.
name|getHunks
argument_list|()
return|;
block|}
block|}
end_class

end_unit

