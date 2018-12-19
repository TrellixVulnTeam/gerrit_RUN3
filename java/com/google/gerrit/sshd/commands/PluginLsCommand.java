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
name|common
operator|.
name|base
operator|.
name|Strings
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
name|GlobalCapability
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
name|extensions
operator|.
name|annotations
operator|.
name|RequiresCapability
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
name|extensions
operator|.
name|common
operator|.
name|PluginInfo
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
name|extensions
operator|.
name|restapi
operator|.
name|TopLevelResource
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
name|json
operator|.
name|OutputFormat
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
name|plugins
operator|.
name|ListPlugins
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
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|util
operator|.
name|cli
operator|.
name|Options
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|reflect
operator|.
name|TypeToken
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
name|java
operator|.
name|util
operator|.
name|Map
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
name|Option
import|;
end_import

begin_class
annotation|@
name|RequiresCapability
argument_list|(
name|GlobalCapability
operator|.
name|VIEW_PLUGINS
argument_list|)
annotation|@
name|CommandMetaData
argument_list|(
name|name
operator|=
literal|"ls"
argument_list|,
name|description
operator|=
literal|"List the installed plugins"
argument_list|,
name|runsAt
operator|=
name|MASTER_OR_SLAVE
argument_list|)
DECL|class|PluginLsCommand
specifier|public
class|class
name|PluginLsCommand
extends|extends
name|SshCommand
block|{
DECL|field|list
annotation|@
name|Inject
annotation|@
name|Options
specifier|public
name|ListPlugins
name|list
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--format"
argument_list|,
name|usage
operator|=
literal|"output format"
argument_list|)
DECL|field|format
specifier|private
name|OutputFormat
name|format
init|=
name|OutputFormat
operator|.
name|TEXT
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
name|Map
argument_list|<
name|String
argument_list|,
name|PluginInfo
argument_list|>
name|output
init|=
name|list
operator|.
name|apply
argument_list|(
name|TopLevelResource
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
if|if
condition|(
name|format
operator|.
name|isJson
argument_list|()
condition|)
block|{
name|format
operator|.
name|newGson
argument_list|()
operator|.
name|toJson
argument_list|(
name|output
argument_list|,
operator|new
name|TypeToken
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|PluginInfo
argument_list|>
argument_list|>
argument_list|()
block|{}
operator|.
name|getType
argument_list|()
argument_list|,
name|stdout
argument_list|)
expr_stmt|;
name|stdout
operator|.
name|print
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|stdout
operator|.
name|format
argument_list|(
literal|"%-30s %-10s %-8s %s\n"
argument_list|,
literal|"Name"
argument_list|,
literal|"Version"
argument_list|,
literal|"Status"
argument_list|,
literal|"File"
argument_list|)
expr_stmt|;
name|stdout
operator|.
name|print
argument_list|(
literal|"-------------------------------------------------------------------------------\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|PluginInfo
argument_list|>
name|p
range|:
name|output
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|PluginInfo
name|info
init|=
name|p
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|stdout
operator|.
name|format
argument_list|(
literal|"%-30s %-10s %-8s %s\n"
argument_list|,
name|p
operator|.
name|getKey
argument_list|()
argument_list|,
name|Strings
operator|.
name|nullToEmpty
argument_list|(
name|info
operator|.
name|version
argument_list|)
argument_list|,
name|status
argument_list|(
name|info
operator|.
name|disabled
argument_list|)
argument_list|,
name|Strings
operator|.
name|nullToEmpty
argument_list|(
name|info
operator|.
name|filename
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|stdout
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
DECL|method|status (Boolean disabled)
specifier|private
name|String
name|status
parameter_list|(
name|Boolean
name|disabled
parameter_list|)
block|{
return|return
name|disabled
operator|!=
literal|null
operator|&&
name|disabled
operator|.
name|booleanValue
argument_list|()
condition|?
literal|"DISABLED"
else|:
literal|"ENABLED"
return|;
block|}
block|}
end_class

end_unit

