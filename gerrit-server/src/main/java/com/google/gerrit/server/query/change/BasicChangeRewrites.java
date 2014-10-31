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
name|query
operator|.
name|IntPredicate
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
name|server
operator|.
name|query
operator|.
name|QueryRewriter
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
name|OutOfScopeException
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|name
operator|.
name|Named
import|;
end_import

begin_class
DECL|class|BasicChangeRewrites
specifier|public
class|class
name|BasicChangeRewrites
extends|extends
name|QueryRewriter
argument_list|<
name|ChangeData
argument_list|>
block|{
DECL|field|BUILDER
specifier|private
specifier|static
specifier|final
name|ChangeQueryBuilder
name|BUILDER
init|=
operator|new
name|ChangeQueryBuilder
argument_list|(
operator|new
name|ChangeQueryBuilder
operator|.
name|Arguments
argument_list|(
comment|//
operator|new
name|InvalidProvider
argument_list|<
name|ReviewDb
argument_list|>
argument_list|()
argument_list|,
comment|//
operator|new
name|InvalidProvider
argument_list|<
name|ChangeQueryRewriter
argument_list|>
argument_list|()
argument_list|,
comment|//
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
comment|//
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
DECL|field|mydef
specifier|private
specifier|static
specifier|final
name|QueryRewriter
operator|.
name|Definition
argument_list|<
name|ChangeData
argument_list|,
name|BasicChangeRewrites
argument_list|>
name|mydef
init|=
operator|new
name|QueryRewriter
operator|.
name|Definition
argument_list|<>
argument_list|(
name|BasicChangeRewrites
operator|.
name|class
argument_list|,
name|BUILDER
argument_list|)
decl_stmt|;
annotation|@
name|Inject
DECL|method|BasicChangeRewrites ()
specifier|public
name|BasicChangeRewrites
parameter_list|()
block|{
name|super
argument_list|(
name|mydef
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Rewrite
argument_list|(
literal|"-status:open"
argument_list|)
annotation|@
name|NoCostComputation
DECL|method|r00_notOpen ()
specifier|public
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|r00_notOpen
parameter_list|()
block|{
return|return
name|ChangeStatusPredicate
operator|.
name|closed
argument_list|()
return|;
block|}
annotation|@
name|Rewrite
argument_list|(
literal|"-status:closed"
argument_list|)
annotation|@
name|NoCostComputation
DECL|method|r00_notClosed ()
specifier|public
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|r00_notClosed
parameter_list|()
block|{
return|return
name|ChangeStatusPredicate
operator|.
name|open
argument_list|()
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|NoCostComputation
annotation|@
name|Rewrite
argument_list|(
literal|"-status:merged"
argument_list|)
DECL|method|r00_notMerged ()
specifier|public
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|r00_notMerged
parameter_list|()
block|{
return|return
name|or
argument_list|(
name|ChangeStatusPredicate
operator|.
name|open
argument_list|()
argument_list|,
name|ChangeStatusPredicate
operator|.
name|forStatus
argument_list|(
name|Change
operator|.
name|Status
operator|.
name|ABANDONED
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|NoCostComputation
annotation|@
name|Rewrite
argument_list|(
literal|"-status:abandoned"
argument_list|)
DECL|method|r00_notAbandoned ()
specifier|public
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|r00_notAbandoned
parameter_list|()
block|{
return|return
name|or
argument_list|(
name|ChangeStatusPredicate
operator|.
name|open
argument_list|()
argument_list|,
name|ChangeStatusPredicate
operator|.
name|forStatus
argument_list|(
name|Change
operator|.
name|Status
operator|.
name|MERGED
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|NoCostComputation
annotation|@
name|Rewrite
argument_list|(
literal|"A=(limit:*) B=(limit:*)"
argument_list|)
DECL|method|r00_smallestLimit ( @amedR) IntPredicate<ChangeData> a, @Named(R) IntPredicate<ChangeData> b)
specifier|public
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|r00_smallestLimit
parameter_list|(
annotation|@
name|Named
argument_list|(
literal|"A"
argument_list|)
name|IntPredicate
argument_list|<
name|ChangeData
argument_list|>
name|a
parameter_list|,
annotation|@
name|Named
argument_list|(
literal|"B"
argument_list|)
name|IntPredicate
argument_list|<
name|ChangeData
argument_list|>
name|b
parameter_list|)
block|{
return|return
name|a
operator|.
name|intValue
argument_list|()
operator|<=
name|b
operator|.
name|intValue
argument_list|()
condition|?
name|a
else|:
name|b
return|;
block|}
DECL|class|InvalidProvider
specifier|private
specifier|static
specifier|final
class|class
name|InvalidProvider
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Provider
argument_list|<
name|T
argument_list|>
block|{
annotation|@
name|Override
DECL|method|get ()
specifier|public
name|T
name|get
parameter_list|()
block|{
throw|throw
operator|new
name|OutOfScopeException
argument_list|(
literal|"Not available at init"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

