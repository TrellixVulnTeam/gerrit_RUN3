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
DECL|package|com.google.gerrit.server.project
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|project
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
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
name|extensions
operator|.
name|restapi
operator|.
name|RestResource
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
name|extensions
operator|.
name|restapi
operator|.
name|RestView
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
name|TypeLiteral
import|;
end_import

begin_class
DECL|class|ChildProjectResource
specifier|public
class|class
name|ChildProjectResource
implements|implements
name|RestResource
block|{
DECL|field|CHILD_PROJECT_KIND
specifier|public
specifier|static
specifier|final
name|TypeLiteral
argument_list|<
name|RestView
argument_list|<
name|ChildProjectResource
argument_list|>
argument_list|>
name|CHILD_PROJECT_KIND
init|=
operator|new
name|TypeLiteral
argument_list|<
name|RestView
argument_list|<
name|ChildProjectResource
argument_list|>
argument_list|>
argument_list|()
block|{}
decl_stmt|;
DECL|field|parent
specifier|private
specifier|final
name|ProjectResource
name|parent
decl_stmt|;
DECL|field|child
specifier|private
specifier|final
name|ProjectState
name|child
decl_stmt|;
DECL|method|ChildProjectResource (ProjectResource parent, ProjectState child)
specifier|public
name|ChildProjectResource
parameter_list|(
name|ProjectResource
name|parent
parameter_list|,
name|ProjectState
name|child
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|child
operator|=
name|child
expr_stmt|;
block|}
DECL|method|getParent ()
specifier|public
name|ProjectResource
name|getParent
parameter_list|()
block|{
return|return
name|parent
return|;
block|}
DECL|method|getChild ()
specifier|public
name|ProjectState
name|getChild
parameter_list|()
block|{
return|return
name|child
return|;
block|}
DECL|method|isDirectChild ()
specifier|public
name|boolean
name|isDirectChild
parameter_list|()
block|{
name|ProjectState
name|firstParent
init|=
name|Iterables
operator|.
name|getFirst
argument_list|(
name|child
operator|.
name|parents
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
name|firstParent
operator|!=
literal|null
operator|&&
name|parent
operator|.
name|getNameKey
argument_list|()
operator|.
name|equals
argument_list|(
name|firstParent
operator|.
name|getProject
argument_list|()
operator|.
name|getNameKey
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

