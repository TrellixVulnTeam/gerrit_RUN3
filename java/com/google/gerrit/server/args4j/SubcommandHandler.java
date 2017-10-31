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
DECL|package|com.google.gerrit.server.args4j
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|args4j
package|;
end_package

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
name|org
operator|.
name|kohsuke
operator|.
name|args4j
operator|.
name|CmdLineException
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
name|CmdLineParser
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
name|OptionDef
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
name|spi
operator|.
name|OptionHandler
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
name|spi
operator|.
name|Parameters
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
name|spi
operator|.
name|Setter
import|;
end_import

begin_class
DECL|class|SubcommandHandler
specifier|public
class|class
name|SubcommandHandler
extends|extends
name|OptionHandler
argument_list|<
name|String
argument_list|>
block|{
annotation|@
name|Inject
DECL|method|SubcommandHandler ( @ssisted final CmdLineParser parser, @Assisted final OptionDef option, @Assisted final Setter<String> setter)
specifier|public
name|SubcommandHandler
parameter_list|(
annotation|@
name|Assisted
specifier|final
name|CmdLineParser
name|parser
parameter_list|,
annotation|@
name|Assisted
specifier|final
name|OptionDef
name|option
parameter_list|,
annotation|@
name|Assisted
specifier|final
name|Setter
argument_list|<
name|String
argument_list|>
name|setter
parameter_list|)
block|{
name|super
argument_list|(
name|parser
argument_list|,
name|option
argument_list|,
name|setter
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|parseArguments (Parameters params)
specifier|public
specifier|final
name|int
name|parseArguments
parameter_list|(
name|Parameters
name|params
parameter_list|)
throws|throws
name|CmdLineException
block|{
name|setter
operator|.
name|addValue
argument_list|(
name|params
operator|.
name|getParameter
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|owner
operator|.
name|stopOptionParsing
argument_list|()
expr_stmt|;
return|return
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|getDefaultMetaVariable ()
specifier|public
specifier|final
name|String
name|getDefaultMetaVariable
parameter_list|()
block|{
return|return
literal|"COMMAND"
return|;
block|}
block|}
end_class

end_unit

