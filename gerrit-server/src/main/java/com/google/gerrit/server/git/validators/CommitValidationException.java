begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2012 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.git.validators
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
operator|.
name|validators
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
name|List
import|;
end_import

begin_class
DECL|class|CommitValidationException
specifier|public
class|class
name|CommitValidationException
extends|extends
name|Exception
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|field|messages
specifier|private
specifier|final
name|List
argument_list|<
name|CommitValidationMessage
argument_list|>
name|messages
decl_stmt|;
DECL|method|CommitValidationException (String reason, List<CommitValidationMessage> messages)
specifier|public
name|CommitValidationException
parameter_list|(
name|String
name|reason
parameter_list|,
name|List
argument_list|<
name|CommitValidationMessage
argument_list|>
name|messages
parameter_list|)
block|{
name|super
argument_list|(
name|reason
argument_list|)
expr_stmt|;
name|this
operator|.
name|messages
operator|=
name|messages
expr_stmt|;
block|}
DECL|method|CommitValidationException (String reason)
specifier|public
name|CommitValidationException
parameter_list|(
name|String
name|reason
parameter_list|)
block|{
name|super
argument_list|(
name|reason
argument_list|)
expr_stmt|;
name|this
operator|.
name|messages
operator|=
operator|new
name|ArrayList
argument_list|<
name|CommitValidationMessage
argument_list|>
argument_list|()
expr_stmt|;
block|}
DECL|method|CommitValidationException (String reason, Throwable why)
specifier|public
name|CommitValidationException
parameter_list|(
name|String
name|reason
parameter_list|,
name|Throwable
name|why
parameter_list|)
block|{
name|super
argument_list|(
name|reason
argument_list|,
name|why
argument_list|)
expr_stmt|;
name|this
operator|.
name|messages
operator|=
operator|new
name|ArrayList
argument_list|<
name|CommitValidationMessage
argument_list|>
argument_list|()
expr_stmt|;
block|}
DECL|method|getMessages ()
specifier|public
name|List
argument_list|<
name|CommitValidationMessage
argument_list|>
name|getMessages
parameter_list|()
block|{
return|return
name|messages
return|;
block|}
block|}
end_class

end_unit

