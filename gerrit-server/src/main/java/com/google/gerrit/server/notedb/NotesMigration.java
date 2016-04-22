begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2016 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.notedb
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|notedb
package|;
end_package

begin_comment
comment|/**  * Holds the current state of the NoteDb migration.  *<p>  * The migration will proceed one root entity type at a time. A<em>root  * entity</em> is an entity stored in ReviewDb whose key's  * {@code getParentKey()} method returns null. For an example of the entity  * hierarchy rooted at Change, see the diagram in  * {@code com.google.gerrit.reviewdb.client.Change}.  *<p>  * During a transitional period, each root entity group from ReviewDb may be  * either<em>written to</em> or<em>both written to and read from</em> NoteDb.  *<p>  * This class controls the state of the migration according to options in  * {@code gerrit.config}. In general, any changes to these options should only  * be made by adventurous administrators, who know what they're doing, on  * non-production data, for the purposes of testing the NoteDb implementation.  * Changing options quite likely requires re-running {@code RebuildNoteDb}. For  * these reasons, the options remain undocumented.  */
end_comment

begin_class
DECL|class|NotesMigration
specifier|public
specifier|abstract
class|class
name|NotesMigration
block|{
DECL|method|readChanges ()
specifier|public
specifier|abstract
name|boolean
name|readChanges
parameter_list|()
function_decl|;
DECL|method|writeChanges ()
specifier|public
specifier|abstract
name|boolean
name|writeChanges
parameter_list|()
function_decl|;
DECL|method|readAccounts ()
specifier|public
specifier|abstract
name|boolean
name|readAccounts
parameter_list|()
function_decl|;
DECL|method|writeAccounts ()
specifier|public
specifier|abstract
name|boolean
name|writeAccounts
parameter_list|()
function_decl|;
comment|/**    * Whether to fail when reading any data from NoteDb.    *<p>    * Used in conjunction with {@link #readChanges()} for tests.    */
DECL|method|failOnLoad ()
specifier|public
name|boolean
name|failOnLoad
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|enabled ()
specifier|public
name|boolean
name|enabled
parameter_list|()
block|{
return|return
name|writeChanges
argument_list|()
operator|||
name|readChanges
argument_list|()
operator|||
name|writeAccounts
argument_list|()
operator|||
name|readAccounts
argument_list|()
return|;
block|}
block|}
end_class

end_unit

