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
DECL|package|com.google.gerrit.client
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|core
operator|.
name|client
operator|.
name|GWT
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|i18n
operator|.
name|client
operator|.
name|Constants
import|;
end_import

begin_interface
DECL|interface|CommonConstants
specifier|public
interface|interface
name|CommonConstants
extends|extends
name|Constants
block|{
DECL|field|C
specifier|public
specifier|static
specifier|final
name|CommonConstants
name|C
init|=
name|GWT
operator|.
name|create
argument_list|(
name|CommonConstants
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|inTheFuture ()
name|String
name|inTheFuture
parameter_list|()
function_decl|;
DECL|method|month ()
name|String
name|month
parameter_list|()
function_decl|;
DECL|method|months ()
name|String
name|months
parameter_list|()
function_decl|;
DECL|method|year ()
name|String
name|year
parameter_list|()
function_decl|;
DECL|method|years ()
name|String
name|years
parameter_list|()
function_decl|;
DECL|method|oneSecondAgo ()
name|String
name|oneSecondAgo
parameter_list|()
function_decl|;
DECL|method|oneMinuteAgo ()
name|String
name|oneMinuteAgo
parameter_list|()
function_decl|;
DECL|method|oneHourAgo ()
name|String
name|oneHourAgo
parameter_list|()
function_decl|;
DECL|method|oneDayAgo ()
name|String
name|oneDayAgo
parameter_list|()
function_decl|;
DECL|method|oneWeekAgo ()
name|String
name|oneWeekAgo
parameter_list|()
function_decl|;
DECL|method|oneMonthAgo ()
name|String
name|oneMonthAgo
parameter_list|()
function_decl|;
DECL|method|oneYearAgo ()
name|String
name|oneYearAgo
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

