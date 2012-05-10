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
name|reviewdb
operator|.
name|Project
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
name|Iterator
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

begin_comment
comment|/** Portion of a {@link Project} describing access rules. */
end_comment

begin_class
DECL|class|AccessSection
specifier|public
class|class
name|AccessSection
implements|implements
name|Comparable
argument_list|<
name|AccessSection
argument_list|>
block|{
comment|/** Special name given to the global capabilities; not a valid reference. */
DECL|field|GLOBAL_CAPABILITIES
specifier|public
specifier|static
specifier|final
name|String
name|GLOBAL_CAPABILITIES
init|=
literal|"GLOBAL_CAPABILITIES"
decl_stmt|;
comment|/** Pattern that matches all references in a project. */
DECL|field|ALL
specifier|public
specifier|static
specifier|final
name|String
name|ALL
init|=
literal|"refs/*"
decl_stmt|;
comment|/** Pattern that matches all branches in a project. */
DECL|field|HEADS
specifier|public
specifier|static
specifier|final
name|String
name|HEADS
init|=
literal|"refs/heads/*"
decl_stmt|;
comment|/** Prefix that triggers a regular expression pattern. */
DECL|field|REGEX_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|REGEX_PREFIX
init|=
literal|"^"
decl_stmt|;
comment|/** @return true if the name is likely to be a valid access section name. */
DECL|method|isAccessSection (String name)
specifier|public
specifier|static
name|boolean
name|isAccessSection
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|name
operator|.
name|startsWith
argument_list|(
literal|"refs/"
argument_list|)
operator|||
name|name
operator|.
name|startsWith
argument_list|(
literal|"^refs/"
argument_list|)
return|;
block|}
DECL|field|name
specifier|protected
name|String
name|name
decl_stmt|;
DECL|field|permissions
specifier|protected
name|List
argument_list|<
name|Permission
argument_list|>
name|permissions
decl_stmt|;
DECL|method|AccessSection ()
specifier|protected
name|AccessSection
parameter_list|()
block|{   }
DECL|method|AccessSection (String refPattern)
specifier|public
name|AccessSection
parameter_list|(
name|String
name|refPattern
parameter_list|)
block|{
name|setName
argument_list|(
name|refPattern
argument_list|)
expr_stmt|;
block|}
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|setName (String name)
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
DECL|method|getPermissions ()
specifier|public
name|List
argument_list|<
name|Permission
argument_list|>
name|getPermissions
parameter_list|()
block|{
if|if
condition|(
name|permissions
operator|==
literal|null
condition|)
block|{
name|permissions
operator|=
operator|new
name|ArrayList
argument_list|<
name|Permission
argument_list|>
argument_list|()
expr_stmt|;
block|}
return|return
name|permissions
return|;
block|}
DECL|method|setPermissions (List<Permission> list)
specifier|public
name|void
name|setPermissions
parameter_list|(
name|List
argument_list|<
name|Permission
argument_list|>
name|list
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Permission
name|p
range|:
name|list
control|)
block|{
if|if
condition|(
operator|!
name|names
operator|.
name|add
argument_list|(
name|p
operator|.
name|getName
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|()
throw|;
block|}
block|}
name|permissions
operator|=
name|list
expr_stmt|;
block|}
DECL|method|getPermission (String name)
specifier|public
name|Permission
name|getPermission
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|getPermission
argument_list|(
name|name
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|getPermission (String name, boolean create)
specifier|public
name|Permission
name|getPermission
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|create
parameter_list|)
block|{
for|for
control|(
name|Permission
name|p
range|:
name|getPermissions
argument_list|()
control|)
block|{
if|if
condition|(
name|p
operator|.
name|getName
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|p
return|;
block|}
block|}
if|if
condition|(
name|create
condition|)
block|{
name|Permission
name|p
init|=
operator|new
name|Permission
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|permissions
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
return|return
name|p
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|method|remove (Permission permission)
specifier|public
name|void
name|remove
parameter_list|(
name|Permission
name|permission
parameter_list|)
block|{
if|if
condition|(
name|permission
operator|!=
literal|null
condition|)
block|{
name|removePermission
argument_list|(
name|permission
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|removePermission (String name)
specifier|public
name|void
name|removePermission
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|permissions
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Iterator
argument_list|<
name|Permission
argument_list|>
name|itr
init|=
name|permissions
operator|.
name|iterator
argument_list|()
init|;
name|itr
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
if|if
condition|(
name|name
operator|.
name|equalsIgnoreCase
argument_list|(
name|itr
operator|.
name|next
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|itr
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|mergeFrom (AccessSection section)
specifier|public
name|void
name|mergeFrom
parameter_list|(
name|AccessSection
name|section
parameter_list|)
block|{
for|for
control|(
name|Permission
name|src
range|:
name|section
operator|.
name|getPermissions
argument_list|()
control|)
block|{
name|Permission
name|dst
init|=
name|getPermission
argument_list|(
name|src
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|dst
operator|!=
literal|null
condition|)
block|{
name|dst
operator|.
name|mergeFrom
argument_list|(
name|src
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|permissions
operator|.
name|add
argument_list|(
name|src
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|compareTo (AccessSection o)
specifier|public
name|int
name|compareTo
parameter_list|(
name|AccessSection
name|o
parameter_list|)
block|{
return|return
name|comparePattern
argument_list|()
operator|.
name|compareTo
argument_list|(
name|o
operator|.
name|comparePattern
argument_list|()
argument_list|)
return|;
block|}
DECL|method|comparePattern ()
specifier|private
name|String
name|comparePattern
parameter_list|()
block|{
if|if
condition|(
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|REGEX_PREFIX
argument_list|)
condition|)
block|{
return|return
name|getName
argument_list|()
operator|.
name|substring
argument_list|(
name|REGEX_PREFIX
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
return|return
name|getName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"AccessSection["
operator|+
name|getName
argument_list|()
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit

