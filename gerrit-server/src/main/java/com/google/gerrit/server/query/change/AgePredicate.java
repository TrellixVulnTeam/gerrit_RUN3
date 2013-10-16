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
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|MILLISECONDS
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|SECONDS
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
name|config
operator|.
name|ConfigUtil
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
name|index
operator|.
name|ChangeField
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
name|index
operator|.
name|TimestampRangePredicate
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
name|util
operator|.
name|TimeUtil
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
name|Provider
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Timestamp
import|;
end_import

begin_class
DECL|class|AgePredicate
specifier|public
class|class
name|AgePredicate
extends|extends
name|TimestampRangePredicate
argument_list|<
name|ChangeData
argument_list|>
block|{
DECL|field|dbProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|dbProvider
decl_stmt|;
DECL|field|cut
specifier|private
specifier|final
name|long
name|cut
decl_stmt|;
DECL|method|AgePredicate (Provider<ReviewDb> dbProvider, String value)
name|AgePredicate
parameter_list|(
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|dbProvider
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|super
argument_list|(
name|ChangeField
operator|.
name|UPDATED
argument_list|,
name|ChangeQueryBuilder
operator|.
name|FIELD_AGE
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|this
operator|.
name|dbProvider
operator|=
name|dbProvider
expr_stmt|;
name|long
name|s
init|=
name|ConfigUtil
operator|.
name|getTimeUnit
argument_list|(
name|getValue
argument_list|()
argument_list|,
literal|0
argument_list|,
name|SECONDS
argument_list|)
decl_stmt|;
name|long
name|ms
init|=
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|s
argument_list|,
name|SECONDS
argument_list|)
decl_stmt|;
name|this
operator|.
name|cut
operator|=
name|TimeUtil
operator|.
name|nowMs
argument_list|()
operator|-
name|ms
expr_stmt|;
block|}
DECL|method|getMinTimestamp ()
specifier|public
name|Timestamp
name|getMinTimestamp
parameter_list|()
block|{
return|return
operator|new
name|Timestamp
argument_list|(
literal|0
argument_list|)
return|;
block|}
DECL|method|getMaxTimestamp ()
specifier|public
name|Timestamp
name|getMaxTimestamp
parameter_list|()
block|{
return|return
operator|new
name|Timestamp
argument_list|(
name|cut
argument_list|)
return|;
block|}
DECL|method|getCut ()
name|long
name|getCut
parameter_list|()
block|{
return|return
name|cut
operator|+
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|match (final ChangeData object)
specifier|public
name|boolean
name|match
parameter_list|(
specifier|final
name|ChangeData
name|object
parameter_list|)
throws|throws
name|OrmException
block|{
name|Change
name|change
init|=
name|object
operator|.
name|change
argument_list|(
name|dbProvider
argument_list|)
decl_stmt|;
return|return
name|change
operator|!=
literal|null
operator|&&
name|change
operator|.
name|getLastUpdatedOn
argument_list|()
operator|.
name|getTime
argument_list|()
operator|<=
name|cut
return|;
block|}
annotation|@
name|Override
DECL|method|getCost ()
specifier|public
name|int
name|getCost
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
block|}
end_class

end_unit

