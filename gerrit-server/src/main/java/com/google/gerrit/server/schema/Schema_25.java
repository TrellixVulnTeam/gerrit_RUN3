begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2010 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.schema
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|schema
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
name|AccountGroup
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
name|ApprovalCategory
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
name|Project
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
name|RefRight
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
name|ReviewDb
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
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|jdbc
operator|.
name|JdbcSchema
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Provider
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Statement
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
name|Set
import|;
end_import

begin_class
DECL|class|Schema_25
class|class
name|Schema_25
extends|extends
name|SchemaVersion
block|{
DECL|field|nonActions
specifier|private
name|Set
argument_list|<
name|ApprovalCategory
operator|.
name|Id
argument_list|>
name|nonActions
decl_stmt|;
annotation|@
name|Inject
DECL|method|Schema_25 (Provider<Schema_24> prior)
name|Schema_25
parameter_list|(
name|Provider
argument_list|<
name|Schema_24
argument_list|>
name|prior
parameter_list|)
block|{
name|super
argument_list|(
name|prior
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|migrateData (ReviewDb db, UpdateUI ui)
specifier|protected
name|void
name|migrateData
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|UpdateUI
name|ui
parameter_list|)
throws|throws
name|OrmException
throws|,
name|SQLException
block|{
name|nonActions
operator|=
operator|new
name|HashSet
argument_list|<
name|ApprovalCategory
operator|.
name|Id
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|ApprovalCategory
name|c
range|:
name|db
operator|.
name|approvalCategories
argument_list|()
operator|.
name|all
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|c
operator|.
name|isAction
argument_list|()
condition|)
block|{
name|nonActions
operator|.
name|add
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|RefRight
argument_list|>
name|rights
init|=
operator|new
name|ArrayList
argument_list|<
name|RefRight
argument_list|>
argument_list|()
decl_stmt|;
name|Statement
name|stmt
init|=
operator|(
operator|(
name|JdbcSchema
operator|)
name|db
operator|)
operator|.
name|getConnection
argument_list|()
operator|.
name|createStatement
argument_list|()
decl_stmt|;
try|try
block|{
name|ResultSet
name|rs
init|=
name|stmt
operator|.
name|executeQuery
argument_list|(
literal|"SELECT * FROM project_rights"
argument_list|)
decl_stmt|;
try|try
block|{
while|while
condition|(
name|rs
operator|.
name|next
argument_list|()
condition|)
block|{
name|rights
operator|.
name|add
argument_list|(
name|toRefRight
argument_list|(
name|rs
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|rs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|db
operator|.
name|refRights
argument_list|()
operator|.
name|insert
argument_list|(
name|rights
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|execute
argument_list|(
literal|"CREATE INDEX ref_rights_byCatGroup"
operator|+
literal|" ON ref_rights (category_id, group_id)"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|toRefRight (ResultSet rs)
specifier|private
name|RefRight
name|toRefRight
parameter_list|(
name|ResultSet
name|rs
parameter_list|)
throws|throws
name|SQLException
block|{
name|short
name|min_value
init|=
name|rs
operator|.
name|getShort
argument_list|(
literal|"min_value"
argument_list|)
decl_stmt|;
name|short
name|max_value
init|=
name|rs
operator|.
name|getShort
argument_list|(
literal|"max_value"
argument_list|)
decl_stmt|;
name|String
name|category_id
init|=
name|rs
operator|.
name|getString
argument_list|(
literal|"category_id"
argument_list|)
decl_stmt|;
name|int
name|group_id
init|=
name|rs
operator|.
name|getInt
argument_list|(
literal|"group_id"
argument_list|)
decl_stmt|;
name|String
name|project_name
init|=
name|rs
operator|.
name|getString
argument_list|(
literal|"project_name"
argument_list|)
decl_stmt|;
name|ApprovalCategory
operator|.
name|Id
name|category
init|=
operator|new
name|ApprovalCategory
operator|.
name|Id
argument_list|(
name|category_id
argument_list|)
decl_stmt|;
name|Project
operator|.
name|NameKey
name|project
init|=
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
name|project_name
argument_list|)
decl_stmt|;
name|AccountGroup
operator|.
name|Id
name|group
init|=
operator|new
name|AccountGroup
operator|.
name|Id
argument_list|(
name|group_id
argument_list|)
decl_stmt|;
name|RefRight
operator|.
name|RefPattern
name|ref
decl_stmt|;
if|if
condition|(
name|category
operator|.
name|equals
argument_list|(
name|ApprovalCategory
operator|.
name|SUBMIT
argument_list|)
operator|||
name|category
operator|.
name|equals
argument_list|(
name|ApprovalCategory
operator|.
name|PUSH_HEAD
argument_list|)
operator|||
name|nonActions
operator|.
name|contains
argument_list|(
name|category
argument_list|)
condition|)
block|{
comment|// Explicitly related to a branch head.
name|ref
operator|=
operator|new
name|RefRight
operator|.
name|RefPattern
argument_list|(
literal|"refs/heads/*"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|category
operator|.
name|equals
argument_list|(
name|ApprovalCategory
operator|.
name|PUSH_TAG
argument_list|)
condition|)
block|{
comment|// Explicitly related to the tag namespace.
name|ref
operator|=
operator|new
name|RefRight
operator|.
name|RefPattern
argument_list|(
literal|"refs/tags/*"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|category
operator|.
name|equals
argument_list|(
name|ApprovalCategory
operator|.
name|READ
argument_list|)
operator|||
name|category
operator|.
name|equals
argument_list|(
name|ApprovalCategory
operator|.
name|OWN
argument_list|)
condition|)
block|{
comment|// Currently these are project-wide rights, so apply that way.
name|ref
operator|=
operator|new
name|RefRight
operator|.
name|RefPattern
argument_list|(
literal|"refs/*"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Assume project wide for the default.
name|ref
operator|=
operator|new
name|RefRight
operator|.
name|RefPattern
argument_list|(
literal|"refs/*"
argument_list|)
expr_stmt|;
block|}
name|RefRight
operator|.
name|Key
name|key
init|=
operator|new
name|RefRight
operator|.
name|Key
argument_list|(
name|project
argument_list|,
name|ref
argument_list|,
name|category
argument_list|,
name|group
argument_list|)
decl_stmt|;
name|RefRight
name|r
init|=
operator|new
name|RefRight
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|r
operator|.
name|setMinValue
argument_list|(
name|min_value
argument_list|)
expr_stmt|;
name|r
operator|.
name|setMaxValue
argument_list|(
name|max_value
argument_list|)
expr_stmt|;
return|return
name|r
return|;
block|}
block|}
end_class

end_unit

