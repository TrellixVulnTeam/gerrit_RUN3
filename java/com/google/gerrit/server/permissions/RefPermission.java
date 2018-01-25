begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2017 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.permissions
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|permissions
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
name|common
operator|.
name|data
operator|.
name|Permission
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_enum
DECL|enum|RefPermission
specifier|public
enum|enum
name|RefPermission
block|{
DECL|enumConstant|READ
name|READ
parameter_list|(
name|Permission
operator|.
name|READ
parameter_list|)
operator|,
DECL|enumConstant|CREATE
constructor|CREATE(Permission.CREATE
block|)
enum|,
DECL|enumConstant|DELETE
name|DELETE
argument_list|(
name|Permission
operator|.
name|DELETE
argument_list|)
operator|,
DECL|enumConstant|UPDATE
name|UPDATE
argument_list|(
name|Permission
operator|.
name|PUSH
argument_list|)
operator|,
DECL|enumConstant|FORCE_UPDATE
name|FORCE_UPDATE
operator|,
DECL|enumConstant|SET_HEAD
name|SET_HEAD
operator|,
DECL|enumConstant|FORGE_AUTHOR
name|FORGE_AUTHOR
argument_list|(
name|Permission
operator|.
name|FORGE_AUTHOR
argument_list|)
operator|,
DECL|enumConstant|FORGE_COMMITTER
name|FORGE_COMMITTER
argument_list|(
name|Permission
operator|.
name|FORGE_COMMITTER
argument_list|)
operator|,
DECL|enumConstant|FORGE_SERVER
name|FORGE_SERVER
argument_list|(
name|Permission
operator|.
name|FORGE_SERVER
argument_list|)
operator|,
DECL|enumConstant|MERGE
name|MERGE
operator|,
DECL|enumConstant|SKIP_VALIDATION
name|SKIP_VALIDATION
operator|,
comment|/** Create a change to code review a commit. */
DECL|enumConstant|CREATE_CHANGE
name|CREATE_CHANGE
operator|,
comment|/** Create a tag. */
DECL|enumConstant|CREATE_TAG
name|CREATE_TAG
argument_list|(
name|Permission
operator|.
name|CREATE_TAG
argument_list|)
operator|,
comment|/** Create a signed tag. */
DECL|enumConstant|CREATE_SIGNED_TAG
name|CREATE_SIGNED_TAG
argument_list|(
name|Permission
operator|.
name|CREATE_SIGNED_TAG
argument_list|)
operator|,
comment|/**    * Creates changes, then also immediately submits them during {@code push}.    *    *<p>This is similar to {@link #UPDATE} except it constructs changes first, then submits them    * according to the submit strategy, which may include cherry-pick or rebase. By creating changes    * for each commit, automatic server side rebase, and post-update review are enabled.    */
DECL|enumConstant|UPDATE_BY_SUBMIT
name|UPDATE_BY_SUBMIT
operator|,
comment|/**    * Can read all private changes on the ref. Typically granted to CI systems if they should run on    * private changes.    */
DECL|enumConstant|READ_PRIVATE_CHANGES
name|READ_PRIVATE_CHANGES
argument_list|(
name|Permission
operator|.
name|VIEW_PRIVATE_CHANGES
argument_list|)
operator|,
comment|/** Read access to ref's config section in {@code project.config}. */
DECL|enumConstant|READ_CONFIG
name|READ_CONFIG
operator|,
comment|/** Write access to ref's config section in {@code project.config}. */
DECL|enumConstant|WRITE_CONFIG
name|WRITE_CONFIG
enum|;
end_enum

begin_decl_stmt
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
end_decl_stmt

begin_expr_stmt
DECL|method|RefPermission ()
name|RefPermission
argument_list|()
block|{
name|name
operator|=
literal|null
block|;   }
DECL|method|RefPermission (String name)
name|RefPermission
argument_list|(
name|String
name|name
argument_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
block|;   }
comment|/** @return name used in {@code project.config} permissions. */
DECL|method|permissionName ()
specifier|public
name|Optional
argument_list|<
name|String
argument_list|>
name|permissionName
argument_list|()
block|{
return|return
name|Optional
operator|.
name|ofNullable
argument_list|(
name|name
argument_list|)
return|;
block|}
end_expr_stmt

begin_function
DECL|method|describeForException ()
specifier|public
name|String
name|describeForException
parameter_list|()
block|{
return|return
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
operator|.
name|replace
argument_list|(
literal|'_'
argument_list|,
literal|' '
argument_list|)
return|;
block|}
end_function

begin_comment
comment|/** @return the enum constant for a given permission name if present. */
end_comment

begin_function
DECL|method|fromName (String name)
specifier|public
specifier|static
name|Optional
argument_list|<
name|RefPermission
argument_list|>
name|fromName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|Arrays
operator|.
name|stream
argument_list|(
name|RefPermission
operator|.
name|values
argument_list|()
argument_list|)
operator|.
name|filter
argument_list|(
name|p
lambda|->
name|name
operator|.
name|equals
argument_list|(
name|p
operator|.
name|name
argument_list|)
argument_list|)
operator|.
name|findFirst
argument_list|()
return|;
block|}
end_function

unit|}
end_unit

