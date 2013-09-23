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
DECL|package|com.google.gerrit.acceptance.rest.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|rest
operator|.
name|change
package|;
end_package

begin_class
DECL|class|SubmitInput
specifier|public
class|class
name|SubmitInput
block|{
DECL|field|wait_for_merge
name|boolean
name|wait_for_merge
decl_stmt|;
DECL|method|waitForMerge ()
specifier|public
specifier|static
name|SubmitInput
name|waitForMerge
parameter_list|()
block|{
name|SubmitInput
name|in
init|=
operator|new
name|SubmitInput
argument_list|()
decl_stmt|;
name|in
operator|.
name|wait_for_merge
operator|=
literal|true
expr_stmt|;
return|return
name|in
return|;
block|}
block|}
end_class

end_unit

