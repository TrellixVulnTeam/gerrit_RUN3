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
DECL|package|com.google.gerrit.sshd.commands
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|sshd
operator|.
name|commands
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|sshd
operator|.
name|CommandMetaData
operator|.
name|Mode
operator|.
name|MASTER_OR_SLAVE
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
name|CanonicalWebUrl
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
name|documentation
operator|.
name|QueryDocumentationExecutor
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
name|documentation
operator|.
name|QueryDocumentationExecutor
operator|.
name|DocQueryException
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
name|documentation
operator|.
name|QueryDocumentationExecutor
operator|.
name|DocResult
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
name|sshd
operator|.
name|CommandMetaData
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
name|sshd
operator|.
name|SshCommand
import|;
end_import

begin_import
import|import
name|org
operator|.
name|kohsuke
operator|.
name|args4j
operator|.
name|Argument
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
name|javax
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_class
annotation|@
name|CommandMetaData
argument_list|(
name|name
operator|=
literal|"apropos"
argument_list|,
name|description
operator|=
literal|"Search in Gerrit documentation"
argument_list|,
name|runsAt
operator|=
name|MASTER_OR_SLAVE
argument_list|)
DECL|class|AproposCommand
specifier|final
class|class
name|AproposCommand
extends|extends
name|SshCommand
block|{
annotation|@
name|Inject
DECL|field|searcher
specifier|private
name|QueryDocumentationExecutor
name|searcher
decl_stmt|;
annotation|@
name|Inject
DECL|field|url
annotation|@
name|CanonicalWebUrl
name|String
name|url
decl_stmt|;
annotation|@
name|Argument
argument_list|(
name|index
operator|=
literal|0
argument_list|,
name|required
operator|=
literal|true
argument_list|,
name|metaVar
operator|=
literal|"QUERY"
argument_list|)
DECL|field|q
specifier|private
name|String
name|q
decl_stmt|;
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|List
argument_list|<
name|QueryDocumentationExecutor
operator|.
name|DocResult
argument_list|>
name|res
init|=
name|searcher
operator|.
name|doQuery
argument_list|(
name|q
argument_list|)
decl_stmt|;
for|for
control|(
name|DocResult
name|docResult
range|:
name|res
control|)
block|{
name|stdout
operator|.
name|println
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%s:\n%s%s\n"
argument_list|,
name|docResult
operator|.
name|title
argument_list|,
name|url
argument_list|,
name|docResult
operator|.
name|url
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|DocQueryException
name|dqe
parameter_list|)
block|{
throw|throw
operator|new
name|UnloggedFailure
argument_list|(
literal|1
argument_list|,
literal|"fatal: "
operator|+
name|dqe
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

