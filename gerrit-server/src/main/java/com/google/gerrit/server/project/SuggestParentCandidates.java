begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2011 The Android Open Source Project
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
comment|// limitations under the License
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
name|config
operator|.
name|AllProjectsName
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
name|server
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
name|inject
operator|.
name|Singleton
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
name|Comparator
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|SuggestParentCandidates
specifier|public
class|class
name|SuggestParentCandidates
block|{
DECL|field|projectControlFactory
specifier|private
specifier|final
name|ProjectControl
operator|.
name|Factory
name|projectControlFactory
decl_stmt|;
DECL|field|projectCache
specifier|private
specifier|final
name|ProjectCache
name|projectCache
decl_stmt|;
DECL|field|allProject
specifier|private
specifier|final
name|AllProjectsName
name|allProject
decl_stmt|;
annotation|@
name|Inject
DECL|method|SuggestParentCandidates (final ProjectControl.Factory projectControlFactory, final ProjectCache projectCache, final AllProjectsName allProject)
name|SuggestParentCandidates
parameter_list|(
specifier|final
name|ProjectControl
operator|.
name|Factory
name|projectControlFactory
parameter_list|,
specifier|final
name|ProjectCache
name|projectCache
parameter_list|,
specifier|final
name|AllProjectsName
name|allProject
parameter_list|)
block|{
name|this
operator|.
name|projectControlFactory
operator|=
name|projectControlFactory
expr_stmt|;
name|this
operator|.
name|projectCache
operator|=
name|projectCache
expr_stmt|;
name|this
operator|.
name|allProject
operator|=
name|allProject
expr_stmt|;
block|}
DECL|method|getNameKeys ()
specifier|public
name|List
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
name|getNameKeys
parameter_list|()
throws|throws
name|OrmException
throws|,
name|NoSuchProjectException
block|{
name|List
argument_list|<
name|Project
argument_list|>
name|pList
init|=
name|getProjects
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
name|nameKeys
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|pList
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Project
name|p
range|:
name|pList
control|)
block|{
name|nameKeys
operator|.
name|add
argument_list|(
name|p
operator|.
name|getNameKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|nameKeys
return|;
block|}
DECL|method|getProjects ()
specifier|public
name|List
argument_list|<
name|Project
argument_list|>
name|getProjects
parameter_list|()
throws|throws
name|OrmException
throws|,
name|NoSuchProjectException
block|{
name|Set
argument_list|<
name|Project
argument_list|>
name|projects
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|(
operator|new
name|Comparator
argument_list|<
name|Project
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Project
name|o1
parameter_list|,
name|Project
name|o2
parameter_list|)
block|{
return|return
name|o1
operator|.
name|getName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|o2
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
for|for
control|(
name|Project
operator|.
name|NameKey
name|p
range|:
name|projectCache
operator|.
name|all
argument_list|()
control|)
block|{
try|try
block|{
specifier|final
name|ProjectControl
name|control
init|=
name|projectControlFactory
operator|.
name|controlFor
argument_list|(
name|p
argument_list|)
decl_stmt|;
specifier|final
name|Project
operator|.
name|NameKey
name|parentK
init|=
name|control
operator|.
name|getProject
argument_list|()
operator|.
name|getParent
argument_list|()
decl_stmt|;
if|if
condition|(
name|parentK
operator|!=
literal|null
condition|)
block|{
name|ProjectControl
name|pControl
init|=
name|projectControlFactory
operator|.
name|controlFor
argument_list|(
name|parentK
argument_list|)
decl_stmt|;
if|if
condition|(
name|pControl
operator|.
name|isVisible
argument_list|()
operator|||
name|pControl
operator|.
name|isOwner
argument_list|()
condition|)
block|{
name|projects
operator|.
name|add
argument_list|(
name|pControl
operator|.
name|getProject
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|NoSuchProjectException
name|e
parameter_list|)
block|{
continue|continue;
block|}
block|}
name|projects
operator|.
name|add
argument_list|(
name|projectControlFactory
operator|.
name|controlFor
argument_list|(
name|allProject
argument_list|)
operator|.
name|getProject
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|projects
argument_list|)
return|;
block|}
block|}
end_class

end_unit

