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
DECL|package|com.google.gerrit.server.query.change
package|package
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
name|extensions
operator|.
name|common
operator|.
name|ProjectInfo
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
name|index
operator|.
name|query
operator|.
name|OrPredicate
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
name|index
operator|.
name|query
operator|.
name|Predicate
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
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|CurrentUser
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
name|permissions
operator|.
name|PermissionBackendException
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
name|project
operator|.
name|ListChildProjects
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
name|project
operator|.
name|ProjectCache
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
name|project
operator|.
name|ProjectResource
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
name|project
operator|.
name|ProjectState
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
name|Collections
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
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
DECL|class|ParentProjectPredicate
specifier|public
class|class
name|ParentProjectPredicate
extends|extends
name|OrPredicate
argument_list|<
name|ChangeData
argument_list|>
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ParentProjectPredicate
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|value
specifier|protected
specifier|final
name|String
name|value
decl_stmt|;
DECL|method|ParentProjectPredicate ( ProjectCache projectCache, Provider<ListChildProjects> listChildProjects, Provider<CurrentUser> self, String value)
specifier|public
name|ParentProjectPredicate
parameter_list|(
name|ProjectCache
name|projectCache
parameter_list|,
name|Provider
argument_list|<
name|ListChildProjects
argument_list|>
name|listChildProjects
parameter_list|,
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|self
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|super
argument_list|(
name|predicates
argument_list|(
name|projectCache
argument_list|,
name|listChildProjects
argument_list|,
name|self
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
DECL|method|predicates ( ProjectCache projectCache, Provider<ListChildProjects> listChildProjects, Provider<CurrentUser> self, String value)
specifier|protected
specifier|static
name|List
argument_list|<
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
argument_list|>
name|predicates
parameter_list|(
name|ProjectCache
name|projectCache
parameter_list|,
name|Provider
argument_list|<
name|ListChildProjects
argument_list|>
name|listChildProjects
parameter_list|,
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|self
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|ProjectState
name|projectState
init|=
name|projectCache
operator|.
name|get
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
name|value
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|projectState
operator|==
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
name|List
argument_list|<
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
argument_list|>
name|r
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|r
operator|.
name|add
argument_list|(
operator|new
name|ProjectPredicate
argument_list|(
name|projectState
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|ProjectResource
name|proj
init|=
operator|new
name|ProjectResource
argument_list|(
name|projectState
operator|.
name|controlFor
argument_list|(
name|self
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|ListChildProjects
name|children
init|=
name|listChildProjects
operator|.
name|get
argument_list|()
decl_stmt|;
name|children
operator|.
name|setRecursive
argument_list|(
literal|true
argument_list|)
expr_stmt|;
for|for
control|(
name|ProjectInfo
name|p
range|:
name|children
operator|.
name|apply
argument_list|(
name|proj
argument_list|)
control|)
block|{
name|r
operator|.
name|add
argument_list|(
operator|new
name|ProjectPredicate
argument_list|(
name|p
operator|.
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|PermissionBackendException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"cannot check permissions to expand child projects"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|r
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
name|ChangeQueryBuilder
operator|.
name|FIELD_PARENTPROJECT
operator|+
literal|":"
operator|+
name|value
return|;
block|}
block|}
end_class

end_unit

