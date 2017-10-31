begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2016 The Android Open Source Project
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
DECL|package|com.google.gerrit.lucene
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|lucene
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
name|MINUTES
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
name|collect
operator|.
name|ImmutableMap
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|standard
operator|.
name|StandardAnalyzer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|util
operator|.
name|CharArraySet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexWriterConfig
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexWriterConfig
operator|.
name|OpenMode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Config
import|;
end_import

begin_comment
comment|/** Combination of Lucene {@link IndexWriterConfig} with additional Gerrit-specific options. */
end_comment

begin_class
DECL|class|GerritIndexWriterConfig
class|class
name|GerritIndexWriterConfig
block|{
DECL|field|CUSTOM_CHAR_MAPPING
specifier|private
specifier|static
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|CUSTOM_CHAR_MAPPING
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"_"
argument_list|,
literal|" "
argument_list|,
literal|"."
argument_list|,
literal|" "
argument_list|)
decl_stmt|;
DECL|field|luceneConfig
specifier|private
specifier|final
name|IndexWriterConfig
name|luceneConfig
decl_stmt|;
DECL|field|commitWithinMs
specifier|private
name|long
name|commitWithinMs
decl_stmt|;
DECL|field|analyzer
specifier|private
specifier|final
name|CustomMappingAnalyzer
name|analyzer
decl_stmt|;
DECL|method|GerritIndexWriterConfig (Config cfg, String name)
name|GerritIndexWriterConfig
parameter_list|(
name|Config
name|cfg
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|analyzer
operator|=
operator|new
name|CustomMappingAnalyzer
argument_list|(
operator|new
name|StandardAnalyzer
argument_list|(
name|CharArraySet
operator|.
name|EMPTY_SET
argument_list|)
argument_list|,
name|CUSTOM_CHAR_MAPPING
argument_list|)
expr_stmt|;
name|luceneConfig
operator|=
operator|new
name|IndexWriterConfig
argument_list|(
name|analyzer
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|CREATE_OR_APPEND
argument_list|)
operator|.
name|setCommitOnClose
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|double
name|m
init|=
literal|1
operator|<<
literal|20
decl_stmt|;
name|luceneConfig
operator|.
name|setRAMBufferSizeMB
argument_list|(
name|cfg
operator|.
name|getLong
argument_list|(
literal|"index"
argument_list|,
name|name
argument_list|,
literal|"ramBufferSize"
argument_list|,
call|(
name|long
call|)
argument_list|(
name|IndexWriterConfig
operator|.
name|DEFAULT_RAM_BUFFER_SIZE_MB
operator|*
name|m
argument_list|)
argument_list|)
operator|/
name|m
argument_list|)
expr_stmt|;
name|luceneConfig
operator|.
name|setMaxBufferedDocs
argument_list|(
name|cfg
operator|.
name|getInt
argument_list|(
literal|"index"
argument_list|,
name|name
argument_list|,
literal|"maxBufferedDocs"
argument_list|,
name|IndexWriterConfig
operator|.
name|DEFAULT_MAX_BUFFERED_DOCS
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|commitWithinMs
operator|=
name|ConfigUtil
operator|.
name|getTimeUnit
argument_list|(
name|cfg
argument_list|,
literal|"index"
argument_list|,
name|name
argument_list|,
literal|"commitWithin"
argument_list|,
name|MILLISECONDS
operator|.
name|convert
argument_list|(
literal|5
argument_list|,
name|MINUTES
argument_list|)
argument_list|,
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|commitWithinMs
operator|=
name|cfg
operator|.
name|getLong
argument_list|(
literal|"index"
argument_list|,
name|name
argument_list|,
literal|"commitWithin"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getAnalyzer ()
name|CustomMappingAnalyzer
name|getAnalyzer
parameter_list|()
block|{
return|return
name|analyzer
return|;
block|}
DECL|method|getLuceneConfig ()
name|IndexWriterConfig
name|getLuceneConfig
parameter_list|()
block|{
return|return
name|luceneConfig
return|;
block|}
DECL|method|getCommitWithinMs ()
name|long
name|getCommitWithinMs
parameter_list|()
block|{
return|return
name|commitWithinMs
return|;
block|}
block|}
end_class

end_unit

