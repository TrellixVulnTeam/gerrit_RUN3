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

begin_comment
comment|/** A single permission within an {@link AccessSection} of a project. */
end_comment

begin_class
DECL|class|Permission
specifier|public
class|class
name|Permission
implements|implements
name|Comparable
argument_list|<
name|Permission
argument_list|>
block|{
DECL|field|ABANDON
specifier|public
specifier|static
specifier|final
name|String
name|ABANDON
init|=
literal|"abandon"
decl_stmt|;
DECL|field|CREATE
specifier|public
specifier|static
specifier|final
name|String
name|CREATE
init|=
literal|"create"
decl_stmt|;
DECL|field|FORGE_AUTHOR
specifier|public
specifier|static
specifier|final
name|String
name|FORGE_AUTHOR
init|=
literal|"forgeAuthor"
decl_stmt|;
DECL|field|FORGE_COMMITTER
specifier|public
specifier|static
specifier|final
name|String
name|FORGE_COMMITTER
init|=
literal|"forgeCommitter"
decl_stmt|;
DECL|field|FORGE_SERVER
specifier|public
specifier|static
specifier|final
name|String
name|FORGE_SERVER
init|=
literal|"forgeServerAsCommitter"
decl_stmt|;
DECL|field|LABEL
specifier|public
specifier|static
specifier|final
name|String
name|LABEL
init|=
literal|"label-"
decl_stmt|;
DECL|field|OWNER
specifier|public
specifier|static
specifier|final
name|String
name|OWNER
init|=
literal|"owner"
decl_stmt|;
DECL|field|PUSH
specifier|public
specifier|static
specifier|final
name|String
name|PUSH
init|=
literal|"push"
decl_stmt|;
DECL|field|PUSH_MERGE
specifier|public
specifier|static
specifier|final
name|String
name|PUSH_MERGE
init|=
literal|"pushMerge"
decl_stmt|;
DECL|field|PUSH_TAG
specifier|public
specifier|static
specifier|final
name|String
name|PUSH_TAG
init|=
literal|"pushTag"
decl_stmt|;
DECL|field|READ
specifier|public
specifier|static
specifier|final
name|String
name|READ
init|=
literal|"read"
decl_stmt|;
DECL|field|REBASE
specifier|public
specifier|static
specifier|final
name|String
name|REBASE
init|=
literal|"rebase"
decl_stmt|;
DECL|field|SUBMIT
specifier|public
specifier|static
specifier|final
name|String
name|SUBMIT
init|=
literal|"submit"
decl_stmt|;
DECL|field|NAMES_LC
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|NAMES_LC
decl_stmt|;
DECL|field|labelIndex
specifier|private
specifier|static
specifier|final
name|int
name|labelIndex
decl_stmt|;
static|static
block|{
name|NAMES_LC
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|NAMES_LC
operator|.
name|add
argument_list|(
name|OWNER
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
name|NAMES_LC
operator|.
name|add
argument_list|(
name|READ
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
name|NAMES_LC
operator|.
name|add
argument_list|(
name|ABANDON
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
name|NAMES_LC
operator|.
name|add
argument_list|(
name|CREATE
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
name|NAMES_LC
operator|.
name|add
argument_list|(
name|FORGE_AUTHOR
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
name|NAMES_LC
operator|.
name|add
argument_list|(
name|FORGE_COMMITTER
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
name|NAMES_LC
operator|.
name|add
argument_list|(
name|FORGE_SERVER
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
name|NAMES_LC
operator|.
name|add
argument_list|(
name|PUSH
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
name|NAMES_LC
operator|.
name|add
argument_list|(
name|PUSH_MERGE
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
name|NAMES_LC
operator|.
name|add
argument_list|(
name|PUSH_TAG
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
name|NAMES_LC
operator|.
name|add
argument_list|(
name|LABEL
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
name|NAMES_LC
operator|.
name|add
argument_list|(
name|REBASE
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
name|NAMES_LC
operator|.
name|add
argument_list|(
name|SUBMIT
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
name|labelIndex
operator|=
name|NAMES_LC
operator|.
name|indexOf
argument_list|(
name|Permission
operator|.
name|LABEL
argument_list|)
expr_stmt|;
block|}
comment|/** @return true if the name is recognized as a permission name. */
DECL|method|isPermission (String varName)
specifier|public
specifier|static
name|boolean
name|isPermission
parameter_list|(
name|String
name|varName
parameter_list|)
block|{
name|String
name|lc
init|=
name|varName
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
if|if
condition|(
name|lc
operator|.
name|startsWith
argument_list|(
name|LABEL
argument_list|)
condition|)
block|{
return|return
name|LABEL
operator|.
name|length
argument_list|()
operator|<
name|lc
operator|.
name|length
argument_list|()
return|;
block|}
return|return
name|NAMES_LC
operator|.
name|contains
argument_list|(
name|lc
argument_list|)
return|;
block|}
comment|/** @return true if the permission name is actually for a review label. */
DECL|method|isLabel (String varName)
specifier|public
specifier|static
name|boolean
name|isLabel
parameter_list|(
name|String
name|varName
parameter_list|)
block|{
return|return
name|varName
operator|.
name|startsWith
argument_list|(
name|LABEL
argument_list|)
operator|&&
name|LABEL
operator|.
name|length
argument_list|()
operator|<
name|varName
operator|.
name|length
argument_list|()
return|;
block|}
comment|/** @return permission name for the given review label. */
DECL|method|forLabel (String labelName)
specifier|public
specifier|static
name|String
name|forLabel
parameter_list|(
name|String
name|labelName
parameter_list|)
block|{
return|return
name|LABEL
operator|+
name|labelName
return|;
block|}
DECL|field|name
specifier|protected
name|String
name|name
decl_stmt|;
DECL|field|exclusiveGroup
specifier|protected
name|boolean
name|exclusiveGroup
decl_stmt|;
DECL|field|rules
specifier|protected
name|List
argument_list|<
name|PermissionRule
argument_list|>
name|rules
decl_stmt|;
DECL|method|Permission ()
specifier|protected
name|Permission
parameter_list|()
block|{   }
DECL|method|Permission (String name)
specifier|public
name|Permission
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
DECL|method|isLabel ()
specifier|public
name|boolean
name|isLabel
parameter_list|()
block|{
return|return
name|isLabel
argument_list|(
name|getName
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getLabel ()
specifier|public
name|String
name|getLabel
parameter_list|()
block|{
if|if
condition|(
name|isLabel
argument_list|()
condition|)
block|{
return|return
name|getName
argument_list|()
operator|.
name|substring
argument_list|(
name|LABEL
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|getExclusiveGroup ()
specifier|public
name|Boolean
name|getExclusiveGroup
parameter_list|()
block|{
comment|// Only permit exclusive group behavior on non OWNER permissions,
comment|// otherwise an owner might lose access to a delegated subspace.
comment|//
return|return
name|exclusiveGroup
operator|&&
operator|!
name|OWNER
operator|.
name|equals
argument_list|(
name|getName
argument_list|()
argument_list|)
return|;
block|}
DECL|method|setExclusiveGroup (Boolean newExclusiveGroup)
specifier|public
name|void
name|setExclusiveGroup
parameter_list|(
name|Boolean
name|newExclusiveGroup
parameter_list|)
block|{
name|exclusiveGroup
operator|=
name|newExclusiveGroup
expr_stmt|;
block|}
DECL|method|getRules ()
specifier|public
name|List
argument_list|<
name|PermissionRule
argument_list|>
name|getRules
parameter_list|()
block|{
name|initRules
argument_list|()
expr_stmt|;
return|return
name|rules
return|;
block|}
DECL|method|setRules (List<PermissionRule> list)
specifier|public
name|void
name|setRules
parameter_list|(
name|List
argument_list|<
name|PermissionRule
argument_list|>
name|list
parameter_list|)
block|{
name|rules
operator|=
name|list
expr_stmt|;
block|}
DECL|method|add (PermissionRule rule)
specifier|public
name|void
name|add
parameter_list|(
name|PermissionRule
name|rule
parameter_list|)
block|{
name|initRules
argument_list|()
expr_stmt|;
name|rules
operator|.
name|add
argument_list|(
name|rule
argument_list|)
expr_stmt|;
block|}
DECL|method|remove (PermissionRule rule)
specifier|public
name|void
name|remove
parameter_list|(
name|PermissionRule
name|rule
parameter_list|)
block|{
if|if
condition|(
name|rule
operator|!=
literal|null
condition|)
block|{
name|removeRule
argument_list|(
name|rule
operator|.
name|getGroup
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|removeRule (GroupReference group)
specifier|public
name|void
name|removeRule
parameter_list|(
name|GroupReference
name|group
parameter_list|)
block|{
if|if
condition|(
name|rules
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Iterator
argument_list|<
name|PermissionRule
argument_list|>
name|itr
init|=
name|rules
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
name|sameGroup
argument_list|(
name|itr
operator|.
name|next
argument_list|()
argument_list|,
name|group
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
DECL|method|getRule (GroupReference group)
specifier|public
name|PermissionRule
name|getRule
parameter_list|(
name|GroupReference
name|group
parameter_list|)
block|{
return|return
name|getRule
argument_list|(
name|group
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|getRule (GroupReference group, boolean create)
specifier|public
name|PermissionRule
name|getRule
parameter_list|(
name|GroupReference
name|group
parameter_list|,
name|boolean
name|create
parameter_list|)
block|{
name|initRules
argument_list|()
expr_stmt|;
for|for
control|(
name|PermissionRule
name|r
range|:
name|rules
control|)
block|{
if|if
condition|(
name|sameGroup
argument_list|(
name|r
argument_list|,
name|group
argument_list|)
condition|)
block|{
return|return
name|r
return|;
block|}
block|}
if|if
condition|(
name|create
condition|)
block|{
name|PermissionRule
name|r
init|=
operator|new
name|PermissionRule
argument_list|(
name|group
argument_list|)
decl_stmt|;
name|rules
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
return|return
name|r
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|method|mergeFrom (Permission src)
name|void
name|mergeFrom
parameter_list|(
name|Permission
name|src
parameter_list|)
block|{
for|for
control|(
name|PermissionRule
name|srcRule
range|:
name|src
operator|.
name|getRules
argument_list|()
control|)
block|{
name|PermissionRule
name|dstRule
init|=
name|getRule
argument_list|(
name|srcRule
operator|.
name|getGroup
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|dstRule
operator|!=
literal|null
condition|)
block|{
name|dstRule
operator|.
name|mergeFrom
argument_list|(
name|srcRule
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|add
argument_list|(
name|srcRule
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|sameGroup (PermissionRule rule, GroupReference group)
specifier|private
specifier|static
name|boolean
name|sameGroup
parameter_list|(
name|PermissionRule
name|rule
parameter_list|,
name|GroupReference
name|group
parameter_list|)
block|{
if|if
condition|(
name|group
operator|.
name|getUUID
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|group
operator|.
name|getUUID
argument_list|()
operator|.
name|equals
argument_list|(
name|rule
operator|.
name|getGroup
argument_list|()
operator|.
name|getUUID
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|group
operator|.
name|getName
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|group
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|rule
operator|.
name|getGroup
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
DECL|method|initRules ()
specifier|private
name|void
name|initRules
parameter_list|()
block|{
if|if
condition|(
name|rules
operator|==
literal|null
condition|)
block|{
name|rules
operator|=
operator|new
name|ArrayList
argument_list|<
name|PermissionRule
argument_list|>
argument_list|(
literal|4
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|compareTo (Permission b)
specifier|public
name|int
name|compareTo
parameter_list|(
name|Permission
name|b
parameter_list|)
block|{
name|int
name|cmp
init|=
name|index
argument_list|(
name|this
argument_list|)
operator|-
name|index
argument_list|(
name|b
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|==
literal|0
condition|)
block|{
name|cmp
operator|=
name|getName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|b
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|cmp
return|;
block|}
DECL|method|index (Permission a)
specifier|private
specifier|static
name|int
name|index
parameter_list|(
name|Permission
name|a
parameter_list|)
block|{
if|if
condition|(
name|a
operator|.
name|isLabel
argument_list|()
condition|)
block|{
return|return
name|labelIndex
return|;
block|}
name|int
name|index
init|=
name|NAMES_LC
operator|.
name|indexOf
argument_list|(
name|a
operator|.
name|getName
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|)
decl_stmt|;
return|return
literal|0
operator|<=
name|index
condition|?
name|index
else|:
name|NAMES_LC
operator|.
name|size
argument_list|()
return|;
block|}
block|}
end_class

end_unit

