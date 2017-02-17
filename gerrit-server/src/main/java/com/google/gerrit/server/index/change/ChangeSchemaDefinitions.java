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
DECL|package|com.google.gerrit.server.index.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|index
operator|.
name|change
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|index
operator|.
name|SchemaUtil
operator|.
name|schema
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
name|server
operator|.
name|index
operator|.
name|Schema
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
name|server
operator|.
name|index
operator|.
name|SchemaDefinitions
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
name|server
operator|.
name|query
operator|.
name|change
operator|.
name|ChangeData
import|;
end_import

begin_class
DECL|class|ChangeSchemaDefinitions
specifier|public
class|class
name|ChangeSchemaDefinitions
extends|extends
name|SchemaDefinitions
argument_list|<
name|ChangeData
argument_list|>
block|{
annotation|@
name|Deprecated
DECL|field|V32
specifier|static
specifier|final
name|Schema
argument_list|<
name|ChangeData
argument_list|>
name|V32
init|=
name|schema
argument_list|(
name|ChangeField
operator|.
name|LEGACY_ID
argument_list|,
name|ChangeField
operator|.
name|ID
argument_list|,
name|ChangeField
operator|.
name|STATUS
argument_list|,
name|ChangeField
operator|.
name|PROJECT
argument_list|,
name|ChangeField
operator|.
name|PROJECTS
argument_list|,
name|ChangeField
operator|.
name|REF
argument_list|,
name|ChangeField
operator|.
name|EXACT_TOPIC
argument_list|,
name|ChangeField
operator|.
name|FUZZY_TOPIC
argument_list|,
name|ChangeField
operator|.
name|UPDATED
argument_list|,
name|ChangeField
operator|.
name|FILE_PART
argument_list|,
name|ChangeField
operator|.
name|PATH
argument_list|,
name|ChangeField
operator|.
name|OWNER
argument_list|,
name|ChangeField
operator|.
name|COMMIT
argument_list|,
name|ChangeField
operator|.
name|TR
argument_list|,
name|ChangeField
operator|.
name|LABEL
argument_list|,
name|ChangeField
operator|.
name|COMMIT_MESSAGE
argument_list|,
name|ChangeField
operator|.
name|COMMENT
argument_list|,
name|ChangeField
operator|.
name|CHANGE
argument_list|,
name|ChangeField
operator|.
name|APPROVAL
argument_list|,
name|ChangeField
operator|.
name|MERGEABLE
argument_list|,
name|ChangeField
operator|.
name|ADDED
argument_list|,
name|ChangeField
operator|.
name|DELETED
argument_list|,
name|ChangeField
operator|.
name|DELTA
argument_list|,
name|ChangeField
operator|.
name|HASHTAG
argument_list|,
name|ChangeField
operator|.
name|COMMENTBY
argument_list|,
name|ChangeField
operator|.
name|PATCH_SET
argument_list|,
name|ChangeField
operator|.
name|GROUP
argument_list|,
name|ChangeField
operator|.
name|SUBMISSIONID
argument_list|,
name|ChangeField
operator|.
name|EDITBY
argument_list|,
name|ChangeField
operator|.
name|REVIEWEDBY
argument_list|,
name|ChangeField
operator|.
name|EXACT_COMMIT
argument_list|,
name|ChangeField
operator|.
name|AUTHOR
argument_list|,
name|ChangeField
operator|.
name|COMMITTER
argument_list|,
name|ChangeField
operator|.
name|DRAFTBY
argument_list|,
name|ChangeField
operator|.
name|HASHTAG_CASE_AWARE
argument_list|,
name|ChangeField
operator|.
name|STAR
argument_list|,
name|ChangeField
operator|.
name|STARBY
argument_list|,
name|ChangeField
operator|.
name|REVIEWER
argument_list|)
decl_stmt|;
DECL|field|V33
annotation|@
name|Deprecated
specifier|static
specifier|final
name|Schema
argument_list|<
name|ChangeData
argument_list|>
name|V33
init|=
name|schema
argument_list|(
name|V32
argument_list|,
name|ChangeField
operator|.
name|ASSIGNEE
argument_list|)
decl_stmt|;
annotation|@
name|Deprecated
DECL|field|V34
specifier|static
specifier|final
name|Schema
argument_list|<
name|ChangeData
argument_list|>
name|V34
init|=
operator|new
name|Schema
operator|.
name|Builder
argument_list|<
name|ChangeData
argument_list|>
argument_list|()
operator|.
name|add
argument_list|(
name|V33
argument_list|)
operator|.
name|remove
argument_list|(
name|ChangeField
operator|.
name|LABEL
argument_list|)
operator|.
name|add
argument_list|(
name|ChangeField
operator|.
name|LABEL2
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
annotation|@
name|Deprecated
DECL|field|V35
specifier|static
specifier|final
name|Schema
argument_list|<
name|ChangeData
argument_list|>
name|V35
init|=
name|schema
argument_list|(
name|V34
argument_list|,
name|ChangeField
operator|.
name|SUBMIT_RECORD
argument_list|,
name|ChangeField
operator|.
name|STORED_SUBMIT_RECORD_LENIENT
argument_list|,
name|ChangeField
operator|.
name|STORED_SUBMIT_RECORD_STRICT
argument_list|)
decl_stmt|;
annotation|@
name|Deprecated
DECL|field|V36
specifier|static
specifier|final
name|Schema
argument_list|<
name|ChangeData
argument_list|>
name|V36
init|=
name|schema
argument_list|(
name|V35
argument_list|,
name|ChangeField
operator|.
name|REF_STATE
argument_list|,
name|ChangeField
operator|.
name|REF_STATE_PATTERN
argument_list|)
decl_stmt|;
DECL|field|V37
annotation|@
name|Deprecated
specifier|static
specifier|final
name|Schema
argument_list|<
name|ChangeData
argument_list|>
name|V37
init|=
name|schema
argument_list|(
name|V36
argument_list|)
decl_stmt|;
annotation|@
name|Deprecated
DECL|field|V38
specifier|static
specifier|final
name|Schema
argument_list|<
name|ChangeData
argument_list|>
name|V38
init|=
name|schema
argument_list|(
name|V37
argument_list|,
name|ChangeField
operator|.
name|UNRESOLVED_COMMENT_COUNT
argument_list|)
decl_stmt|;
DECL|field|V39
specifier|static
specifier|final
name|Schema
argument_list|<
name|ChangeData
argument_list|>
name|V39
init|=
name|schema
argument_list|(
name|V38
argument_list|)
decl_stmt|;
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"changes"
decl_stmt|;
DECL|field|INSTANCE
specifier|public
specifier|static
specifier|final
name|ChangeSchemaDefinitions
name|INSTANCE
init|=
operator|new
name|ChangeSchemaDefinitions
argument_list|()
decl_stmt|;
DECL|method|ChangeSchemaDefinitions ()
specifier|private
name|ChangeSchemaDefinitions
parameter_list|()
block|{
name|super
argument_list|(
name|NAME
argument_list|,
name|ChangeData
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

