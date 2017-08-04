begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2009 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.account
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|account
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
operator|.
name|toImmutableSet
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
name|gerrit
operator|.
name|common
operator|.
name|data
operator|.
name|GroupDetail
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
name|common
operator|.
name|errors
operator|.
name|NoSuchGroupException
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
name|Account
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
name|server
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
name|gerrit
operator|.
name|server
operator|.
name|group
operator|.
name|Groups
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
name|assistedinject
operator|.
name|Assisted
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
import|;
end_import

begin_class
DECL|class|GroupDetailFactory
specifier|public
class|class
name|GroupDetailFactory
implements|implements
name|Callable
argument_list|<
name|GroupDetail
argument_list|>
block|{
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (AccountGroup.UUID groupUuid)
name|GroupDetailFactory
name|create
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|groupUuid
parameter_list|)
function_decl|;
block|}
DECL|field|db
specifier|private
specifier|final
name|ReviewDb
name|db
decl_stmt|;
DECL|field|groupControl
specifier|private
specifier|final
name|GroupControl
operator|.
name|Factory
name|groupControl
decl_stmt|;
DECL|field|groups
specifier|private
specifier|final
name|Groups
name|groups
decl_stmt|;
DECL|field|groupIncludeCache
specifier|private
specifier|final
name|GroupIncludeCache
name|groupIncludeCache
decl_stmt|;
DECL|field|groupUuid
specifier|private
specifier|final
name|AccountGroup
operator|.
name|UUID
name|groupUuid
decl_stmt|;
DECL|field|control
specifier|private
name|GroupControl
name|control
decl_stmt|;
annotation|@
name|Inject
DECL|method|GroupDetailFactory ( ReviewDb db, GroupControl.Factory groupControl, Groups groups, GroupIncludeCache groupIncludeCache, @Assisted AccountGroup.UUID groupUuid)
name|GroupDetailFactory
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|GroupControl
operator|.
name|Factory
name|groupControl
parameter_list|,
name|Groups
name|groups
parameter_list|,
name|GroupIncludeCache
name|groupIncludeCache
parameter_list|,
annotation|@
name|Assisted
name|AccountGroup
operator|.
name|UUID
name|groupUuid
parameter_list|)
block|{
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|groupControl
operator|=
name|groupControl
expr_stmt|;
name|this
operator|.
name|groups
operator|=
name|groups
expr_stmt|;
name|this
operator|.
name|groupIncludeCache
operator|=
name|groupIncludeCache
expr_stmt|;
name|this
operator|.
name|groupUuid
operator|=
name|groupUuid
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|GroupDetail
name|call
parameter_list|()
throws|throws
name|OrmException
throws|,
name|NoSuchGroupException
block|{
name|control
operator|=
name|groupControl
operator|.
name|validateFor
argument_list|(
name|groupUuid
argument_list|)
expr_stmt|;
name|ImmutableSet
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|members
init|=
name|loadMembers
argument_list|()
decl_stmt|;
name|ImmutableSet
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|includes
init|=
name|loadIncludes
argument_list|()
decl_stmt|;
return|return
operator|new
name|GroupDetail
argument_list|(
name|members
argument_list|,
name|includes
argument_list|)
return|;
block|}
DECL|method|loadMembers ()
specifier|private
name|ImmutableSet
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|loadMembers
parameter_list|()
throws|throws
name|OrmException
throws|,
name|NoSuchGroupException
block|{
return|return
name|groups
operator|.
name|getMembers
argument_list|(
name|db
argument_list|,
name|groupUuid
argument_list|)
operator|.
name|filter
argument_list|(
name|control
operator|::
name|canSeeMember
argument_list|)
operator|.
name|collect
argument_list|(
name|toImmutableSet
argument_list|()
argument_list|)
return|;
block|}
DECL|method|loadIncludes ()
specifier|private
name|ImmutableSet
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|loadIncludes
parameter_list|()
block|{
if|if
condition|(
operator|!
name|control
operator|.
name|canSeeGroup
argument_list|()
condition|)
block|{
return|return
name|ImmutableSet
operator|.
name|of
argument_list|()
return|;
block|}
return|return
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|groupIncludeCache
operator|.
name|subgroupsOf
argument_list|(
name|groupUuid
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

