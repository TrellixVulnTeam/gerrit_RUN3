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
import|import static
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
name|Localizable
operator|.
name|localizable
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
name|Splitter
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
name|entities
operator|.
name|BranchNameKey
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
name|entities
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
name|entities
operator|.
name|Project
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
name|exceptions
operator|.
name|StorageException
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
name|change
operator|.
name|ChangeData
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
name|change
operator|.
name|InternalChangeQuery
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
name|assistedinject
operator|.
name|Assisted
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
DECL|class|ChangeIdHandler
specifier|public
class|class
name|ChangeIdHandler
extends|extends
name|OptionHandler
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
block|{
DECL|field|queryProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|InternalChangeQuery
argument_list|>
name|queryProvider
decl_stmt|;
annotation|@
name|Inject
DECL|method|ChangeIdHandler ( Provider<InternalChangeQuery> queryProvider, @Assisted final CmdLineParser parser, @Assisted final OptionDef option, @Assisted final Setter<Change.Id> setter)
specifier|public
name|ChangeIdHandler
parameter_list|(
comment|// TODO(dborowitz): Not sure whether this is injectable here.
name|Provider
argument_list|<
name|InternalChangeQuery
argument_list|>
name|queryProvider
parameter_list|,
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
name|Change
operator|.
name|Id
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
name|this
operator|.
name|queryProvider
operator|=
name|queryProvider
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
specifier|final
name|String
name|token
init|=
name|params
operator|.
name|getParameter
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|tokens
init|=
name|Splitter
operator|.
name|on
argument_list|(
literal|','
argument_list|)
operator|.
name|splitToList
argument_list|(
name|token
argument_list|)
decl_stmt|;
if|if
condition|(
name|tokens
operator|.
name|size
argument_list|()
operator|!=
literal|3
condition|)
block|{
throw|throw
operator|new
name|CmdLineException
argument_list|(
name|owner
argument_list|,
name|localizable
argument_list|(
literal|"change should be specified as<project>,<branch>,<change-id>"
argument_list|)
argument_list|)
throw|;
block|}
try|try
block|{
specifier|final
name|Change
operator|.
name|Key
name|key
init|=
name|Change
operator|.
name|Key
operator|.
name|parse
argument_list|(
name|tokens
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Project
operator|.
name|NameKey
name|project
init|=
name|Project
operator|.
name|nameKey
argument_list|(
name|tokens
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|BranchNameKey
name|branch
init|=
name|BranchNameKey
operator|.
name|create
argument_list|(
name|project
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|ChangeData
name|cd
range|:
name|queryProvider
operator|.
name|get
argument_list|()
operator|.
name|byBranchKey
argument_list|(
name|branch
argument_list|,
name|key
argument_list|)
control|)
block|{
name|setter
operator|.
name|addValue
argument_list|(
name|cd
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CmdLineException
argument_list|(
name|owner
argument_list|,
name|localizable
argument_list|(
literal|"Change-Id is not valid"
argument_list|)
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|StorageException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CmdLineException
argument_list|(
name|owner
argument_list|,
name|localizable
argument_list|(
literal|"Database error: %s"
argument_list|)
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
throw|throw
operator|new
name|CmdLineException
argument_list|(
name|owner
argument_list|,
name|localizable
argument_list|(
literal|"\"%s\": change not found"
argument_list|)
argument_list|,
name|token
argument_list|)
throw|;
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
literal|"CHANGE"
return|;
block|}
block|}
end_class

end_unit

