begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2008 The Android Open Source Project
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
DECL|package|com.google.gerrit.client.account
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|account
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
name|client
operator|.
name|data
operator|.
name|SshHostKey
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
name|client
operator|.
name|ui
operator|.
name|SmallHeading
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
name|user
operator|.
name|client
operator|.
name|ui
operator|.
name|Composite
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
name|user
operator|.
name|client
operator|.
name|ui
operator|.
name|FlowPanel
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
name|user
operator|.
name|client
operator|.
name|ui
operator|.
name|HTML
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
name|user
operator|.
name|client
operator|.
name|ui
operator|.
name|Label
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtexpui
operator|.
name|clippy
operator|.
name|client
operator|.
name|CopyableLabel
import|;
end_import

begin_class
DECL|class|SshHostKeyPanel
class|class
name|SshHostKeyPanel
extends|extends
name|Composite
block|{
DECL|method|SshHostKeyPanel (final SshHostKey info)
name|SshHostKeyPanel
parameter_list|(
specifier|final
name|SshHostKey
name|info
parameter_list|)
block|{
specifier|final
name|FlowPanel
name|body
init|=
operator|new
name|FlowPanel
argument_list|()
decl_stmt|;
name|body
operator|.
name|setStyleName
argument_list|(
literal|"gerrit-SshHostKeyPanel"
argument_list|)
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
operator|new
name|SmallHeading
argument_list|(
name|Util
operator|.
name|C
operator|.
name|sshHostKeyTitle
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|{
specifier|final
name|Label
name|fpLbl
init|=
operator|new
name|Label
argument_list|(
name|Util
operator|.
name|C
operator|.
name|sshHostKeyFingerprint
argument_list|()
argument_list|)
decl_stmt|;
name|fpLbl
operator|.
name|setStyleName
argument_list|(
literal|"gerrit-SshHostKeyPanel-Heading"
argument_list|)
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
name|fpLbl
argument_list|)
expr_stmt|;
specifier|final
name|Label
name|fpVal
init|=
operator|new
name|Label
argument_list|(
name|info
operator|.
name|getFingerprint
argument_list|()
argument_list|)
decl_stmt|;
name|fpVal
operator|.
name|setStyleName
argument_list|(
literal|"gerrit-SshHostKeyPanel-FingerprintData"
argument_list|)
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
name|fpVal
argument_list|)
expr_stmt|;
block|}
block|{
specifier|final
name|HTML
name|hdr
init|=
operator|new
name|HTML
argument_list|(
name|Util
operator|.
name|C
operator|.
name|sshHostKeyKnownHostEntry
argument_list|()
argument_list|)
decl_stmt|;
name|hdr
operator|.
name|setStyleName
argument_list|(
literal|"gerrit-SshHostKeyPanel-Heading"
argument_list|)
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
name|hdr
argument_list|)
expr_stmt|;
specifier|final
name|CopyableLabel
name|lbl
init|=
operator|new
name|CopyableLabel
argument_list|(
name|info
operator|.
name|getHostIdent
argument_list|()
operator|+
literal|" "
operator|+
name|info
operator|.
name|getHostKey
argument_list|()
argument_list|)
decl_stmt|;
name|lbl
operator|.
name|addStyleName
argument_list|(
literal|"gerrit-SshHostKeyPanel-KnownHostEntry"
argument_list|)
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
name|lbl
argument_list|)
expr_stmt|;
block|}
name|initWidget
argument_list|(
name|body
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

