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
DECL|package|com.google.gerrit.client.info
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|info
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
name|reviewdb
operator|.
name|client
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
name|gwt
operator|.
name|core
operator|.
name|client
operator|.
name|JavaScriptObject
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

begin_class
DECL|class|FileInfo
specifier|public
class|class
name|FileInfo
extends|extends
name|JavaScriptObject
block|{
DECL|method|path ()
specifier|public
specifier|final
specifier|native
name|String
name|path
parameter_list|()
comment|/*-{ return this.path; }-*/
function_decl|;
DECL|method|oldPath ()
specifier|public
specifier|final
specifier|native
name|String
name|oldPath
parameter_list|()
comment|/*-{ return this.old_path; }-*/
function_decl|;
DECL|method|linesInserted ()
specifier|public
specifier|final
specifier|native
name|int
name|linesInserted
parameter_list|()
comment|/*-{ return this.lines_inserted || 0; }-*/
function_decl|;
DECL|method|linesDeleted ()
specifier|public
specifier|final
specifier|native
name|int
name|linesDeleted
parameter_list|()
comment|/*-{ return this.lines_deleted || 0; }-*/
function_decl|;
DECL|method|binary ()
specifier|public
specifier|final
specifier|native
name|boolean
name|binary
parameter_list|()
comment|/*-{ return this.binary || false; }-*/
function_decl|;
DECL|method|status ()
specifier|public
specifier|final
specifier|native
name|String
name|status
parameter_list|()
comment|/*-{ return this.status; }-*/
function_decl|;
DECL|method|_row ()
specifier|public
specifier|final
specifier|native
name|int
name|_row
parameter_list|()
comment|/*-{ return this._row }-*/
function_decl|;
DECL|method|_row (int r)
specifier|public
specifier|final
specifier|native
name|void
name|_row
parameter_list|(
name|int
name|r
parameter_list|)
comment|/*-{ this._row = r }-*/
function_decl|;
DECL|method|sortFileInfoByPath (JsArray<FileInfo> list)
specifier|public
specifier|static
name|void
name|sortFileInfoByPath
parameter_list|(
name|JsArray
argument_list|<
name|FileInfo
argument_list|>
name|list
parameter_list|)
block|{
name|Collections
operator|.
name|sort
argument_list|(
name|Natives
operator|.
name|asList
argument_list|(
name|list
argument_list|)
argument_list|,
operator|new
name|Comparator
argument_list|<
name|FileInfo
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|FileInfo
name|a
parameter_list|,
name|FileInfo
name|b
parameter_list|)
block|{
if|if
condition|(
name|Patch
operator|.
name|COMMIT_MSG
operator|.
name|equals
argument_list|(
name|a
operator|.
name|path
argument_list|()
argument_list|)
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|Patch
operator|.
name|COMMIT_MSG
operator|.
name|equals
argument_list|(
name|b
operator|.
name|path
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|1
return|;
block|}
comment|// Look at file suffixes to check if it makes sense to use a different order
name|int
name|s1
init|=
name|a
operator|.
name|path
argument_list|()
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
decl_stmt|;
name|int
name|s2
init|=
name|b
operator|.
name|path
argument_list|()
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
decl_stmt|;
if|if
condition|(
name|s1
operator|>
literal|0
operator|&&
name|s2
operator|>
literal|0
operator|&&
name|a
operator|.
name|path
argument_list|()
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|s1
argument_list|)
operator|.
name|equals
argument_list|(
name|b
operator|.
name|path
argument_list|()
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|s2
argument_list|)
argument_list|)
condition|)
block|{
name|String
name|suffixA
init|=
name|a
operator|.
name|path
argument_list|()
operator|.
name|substring
argument_list|(
name|s1
argument_list|)
decl_stmt|;
name|String
name|suffixB
init|=
name|b
operator|.
name|path
argument_list|()
operator|.
name|substring
argument_list|(
name|s2
argument_list|)
decl_stmt|;
comment|// C++ and C: give priority to header files (.h/.hpp/...)
if|if
condition|(
name|suffixA
operator|.
name|indexOf
argument_list|(
literal|".h"
argument_list|)
operator|==
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|suffixB
operator|.
name|indexOf
argument_list|(
literal|".h"
argument_list|)
operator|==
literal|0
condition|)
block|{
return|return
literal|1
return|;
block|}
block|}
return|return
name|a
operator|.
name|path
argument_list|()
operator|.
name|compareTo
argument_list|(
name|b
operator|.
name|path
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|getFileName (String path)
specifier|public
specifier|static
name|String
name|getFileName
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|String
name|fileName
init|=
name|Patch
operator|.
name|COMMIT_MSG
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|?
literal|"Commit Message"
else|:
name|path
decl_stmt|;
name|int
name|s
init|=
name|fileName
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
return|return
name|s
operator|>=
literal|0
condition|?
name|fileName
operator|.
name|substring
argument_list|(
name|s
operator|+
literal|1
argument_list|)
else|:
name|fileName
return|;
block|}
DECL|method|FileInfo ()
specifier|protected
name|FileInfo
parameter_list|()
block|{   }
block|}
end_class

end_unit

