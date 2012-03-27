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
DECL|package|com.google.gerrit.server.mail
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|mail
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
name|server
operator|.
name|config
operator|.
name|AnonymousCowardName
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
name|ssh
operator|.
name|SshInfo
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

begin_comment
comment|/** Send notice to reviewers that a change has been rebased. */
end_comment

begin_class
DECL|class|RebasedPatchSetSender
specifier|public
class|class
name|RebasedPatchSetSender
extends|extends
name|ReplacePatchSetSender
block|{
DECL|interface|Factory
specifier|public
specifier|static
interface|interface
name|Factory
block|{
DECL|method|create (Change change)
name|RebasedPatchSetSender
name|create
parameter_list|(
name|Change
name|change
parameter_list|)
function_decl|;
block|}
annotation|@
name|Inject
DECL|method|RebasedPatchSetSender (EmailArguments ea, @AnonymousCowardName String anonymousCowardName, SshInfo si, @Assisted Change c)
specifier|public
name|RebasedPatchSetSender
parameter_list|(
name|EmailArguments
name|ea
parameter_list|,
annotation|@
name|AnonymousCowardName
name|String
name|anonymousCowardName
parameter_list|,
name|SshInfo
name|si
parameter_list|,
annotation|@
name|Assisted
name|Change
name|c
parameter_list|)
block|{
name|super
argument_list|(
name|ea
argument_list|,
name|anonymousCowardName
argument_list|,
name|si
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|formatChange ()
specifier|protected
name|void
name|formatChange
parameter_list|()
throws|throws
name|EmailException
block|{
name|appendText
argument_list|(
name|velocifyFile
argument_list|(
literal|"RebasedPatchSet.vm"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

