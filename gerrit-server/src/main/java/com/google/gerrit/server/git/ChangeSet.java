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
DECL|package|com.google.gerrit.server.git
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|git
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
name|ArrayListMultimap
import|;
end_import

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
name|HashMultimap
import|;
end_import

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
name|ImmutableCollection
import|;
end_import

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
name|ImmutableList
import|;
end_import

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
name|ImmutableSet
import|;
end_import

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
name|ListMultimap
import|;
end_import

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
name|Multimap
import|;
end_import

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
name|SetMultimap
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
name|Branch
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
name|Change
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
name|PatchSet
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
name|query
operator|.
name|change
operator|.
name|ChangeData
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
name|Set
import|;
end_import

begin_comment
comment|/**  * A set of changes grouped together to be submitted atomically.  *<p>  * This class is not thread safe.  */
end_comment

begin_class
DECL|class|ChangeSet
specifier|public
class|class
name|ChangeSet
block|{
DECL|field|changeData
specifier|private
specifier|final
name|ImmutableCollection
argument_list|<
name|ChangeData
argument_list|>
name|changeData
decl_stmt|;
DECL|method|ChangeSet (Iterable<ChangeData> changes)
specifier|public
name|ChangeSet
parameter_list|(
name|Iterable
argument_list|<
name|ChangeData
argument_list|>
name|changes
parameter_list|)
block|{
name|Set
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
name|ids
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|ImmutableSet
operator|.
name|Builder
argument_list|<
name|ChangeData
argument_list|>
name|cdb
init|=
name|ImmutableSet
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|ChangeData
name|cd
range|:
name|changes
control|)
block|{
if|if
condition|(
name|ids
operator|.
name|add
argument_list|(
name|cd
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
name|cdb
operator|.
name|add
argument_list|(
name|cd
argument_list|)
expr_stmt|;
block|}
block|}
name|changeData
operator|=
name|cdb
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
DECL|method|ChangeSet (ChangeData change)
specifier|public
name|ChangeSet
parameter_list|(
name|ChangeData
name|change
parameter_list|)
block|{
name|this
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|change
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|ids ()
specifier|public
name|ImmutableSet
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
name|ids
parameter_list|()
block|{
name|ImmutableSet
operator|.
name|Builder
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
name|ret
init|=
name|ImmutableSet
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|ChangeData
name|cd
range|:
name|changeData
control|)
block|{
name|ret
operator|.
name|add
argument_list|(
name|cd
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|patchIds ()
specifier|public
name|Set
argument_list|<
name|PatchSet
operator|.
name|Id
argument_list|>
name|patchIds
parameter_list|()
throws|throws
name|OrmException
block|{
name|Set
argument_list|<
name|PatchSet
operator|.
name|Id
argument_list|>
name|ret
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ChangeData
name|cd
range|:
name|changeData
control|)
block|{
name|ret
operator|.
name|add
argument_list|(
name|cd
operator|.
name|change
argument_list|()
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
DECL|method|branchesByProject ()
specifier|public
name|SetMultimap
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|,
name|Branch
operator|.
name|NameKey
argument_list|>
name|branchesByProject
parameter_list|()
throws|throws
name|OrmException
block|{
name|SetMultimap
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|,
name|Branch
operator|.
name|NameKey
argument_list|>
name|ret
init|=
name|HashMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
for|for
control|(
name|ChangeData
name|cd
range|:
name|changeData
control|)
block|{
name|ret
operator|.
name|put
argument_list|(
name|cd
operator|.
name|change
argument_list|()
operator|.
name|getProject
argument_list|()
argument_list|,
name|cd
operator|.
name|change
argument_list|()
operator|.
name|getDest
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
DECL|method|changesByProject ()
specifier|public
name|Multimap
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|,
name|Change
operator|.
name|Id
argument_list|>
name|changesByProject
parameter_list|()
throws|throws
name|OrmException
block|{
name|ListMultimap
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|,
name|Change
operator|.
name|Id
argument_list|>
name|ret
init|=
name|ArrayListMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
for|for
control|(
name|ChangeData
name|cd
range|:
name|changeData
control|)
block|{
name|ret
operator|.
name|put
argument_list|(
name|cd
operator|.
name|change
argument_list|()
operator|.
name|getProject
argument_list|()
argument_list|,
name|cd
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
DECL|method|changesByBranch ()
specifier|public
name|Multimap
argument_list|<
name|Branch
operator|.
name|NameKey
argument_list|,
name|Change
operator|.
name|Id
argument_list|>
name|changesByBranch
parameter_list|()
throws|throws
name|OrmException
block|{
name|ListMultimap
argument_list|<
name|Branch
operator|.
name|NameKey
argument_list|,
name|Change
operator|.
name|Id
argument_list|>
name|ret
init|=
name|ArrayListMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
for|for
control|(
name|ChangeData
name|cd
range|:
name|changeData
control|)
block|{
name|ret
operator|.
name|put
argument_list|(
name|cd
operator|.
name|change
argument_list|()
operator|.
name|getDest
argument_list|()
argument_list|,
name|cd
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
DECL|method|changes ()
specifier|public
name|ImmutableCollection
argument_list|<
name|ChangeData
argument_list|>
name|changes
parameter_list|()
block|{
return|return
name|changeData
return|;
block|}
DECL|method|size ()
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|changeData
operator|.
name|size
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
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
name|ids
argument_list|()
return|;
block|}
block|}
end_class

end_unit

