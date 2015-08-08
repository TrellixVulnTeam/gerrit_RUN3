begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2015 The Android Open Source Project
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
import|import
name|com
operator|.
name|google
operator|.
name|auto
operator|.
name|value
operator|.
name|AutoValue
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
name|Project
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
name|lib
operator|.
name|ObjectId
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
annotation|@
name|AutoValue
DECL|class|IntraLineDiffArgs
specifier|public
specifier|abstract
class|class
name|IntraLineDiffArgs
block|{
DECL|method|create (Text aText, Text bText, List<Edit> edits, Project.NameKey project, ObjectId commit, String path)
specifier|public
specifier|static
name|IntraLineDiffArgs
name|create
parameter_list|(
name|Text
name|aText
parameter_list|,
name|Text
name|bText
parameter_list|,
name|List
argument_list|<
name|Edit
argument_list|>
name|edits
parameter_list|,
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
name|ObjectId
name|commit
parameter_list|,
name|String
name|path
parameter_list|)
block|{
return|return
operator|new
name|AutoValue_IntraLineDiffArgs
argument_list|(
name|aText
argument_list|,
name|bText
argument_list|,
name|edits
argument_list|,
name|project
argument_list|,
name|commit
argument_list|,
name|path
argument_list|)
return|;
block|}
DECL|method|aText ()
specifier|public
specifier|abstract
name|Text
name|aText
parameter_list|()
function_decl|;
DECL|method|bText ()
specifier|public
specifier|abstract
name|Text
name|bText
parameter_list|()
function_decl|;
DECL|method|edits ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|Edit
argument_list|>
name|edits
parameter_list|()
function_decl|;
DECL|method|project ()
specifier|public
specifier|abstract
name|Project
operator|.
name|NameKey
name|project
parameter_list|()
function_decl|;
DECL|method|commit ()
specifier|public
specifier|abstract
name|ObjectId
name|commit
parameter_list|()
function_decl|;
DECL|method|path ()
specifier|public
specifier|abstract
name|String
name|path
parameter_list|()
function_decl|;
block|}
end_class

end_unit

