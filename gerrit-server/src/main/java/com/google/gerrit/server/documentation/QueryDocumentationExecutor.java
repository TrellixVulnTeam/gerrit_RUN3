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
DECL|package|com.google.gerrit.server.documentation
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|documentation
package|;
end_package

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
name|Lists
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
name|Singleton
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
name|document
operator|.
name|Document
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
name|DirectoryReader
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
name|IndexReader
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
name|queryparser
operator|.
name|classic
operator|.
name|ParseException
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
name|queryparser
operator|.
name|classic
operator|.
name|QueryParser
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
name|search
operator|.
name|IndexSearcher
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
name|search
operator|.
name|Query
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
name|search
operator|.
name|ScoreDoc
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
name|search
operator|.
name|TopDocs
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
name|store
operator|.
name|Directory
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
name|store
operator|.
name|IndexOutput
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
name|store
operator|.
name|RAMDirectory
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
name|util
operator|.
name|Version
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipEntry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipInputStream
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|QueryDocumentationExecutor
specifier|public
class|class
name|QueryDocumentationExecutor
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|QueryDocumentationExecutor
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|LUCENE_VERSION
specifier|private
specifier|static
specifier|final
name|Version
name|LUCENE_VERSION
init|=
name|Version
operator|.
name|LUCENE_46
decl_stmt|;
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|parser
specifier|private
name|QueryParser
name|parser
decl_stmt|;
DECL|class|DocResult
specifier|public
specifier|static
class|class
name|DocResult
block|{
DECL|field|title
specifier|public
name|String
name|title
decl_stmt|;
DECL|field|url
specifier|public
name|String
name|url
decl_stmt|;
DECL|field|content
specifier|public
name|String
name|content
decl_stmt|;
block|}
annotation|@
name|Inject
DECL|method|QueryDocumentationExecutor ()
specifier|public
name|QueryDocumentationExecutor
parameter_list|()
block|{
try|try
block|{
name|Directory
name|dir
init|=
name|readIndexDirectory
argument_list|()
decl_stmt|;
if|if
condition|(
name|dir
operator|==
literal|null
condition|)
block|{
name|searcher
operator|=
literal|null
expr_stmt|;
name|parser
operator|=
literal|null
expr_stmt|;
return|return;
block|}
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|StandardAnalyzer
name|analyzer
init|=
operator|new
name|StandardAnalyzer
argument_list|(
name|LUCENE_VERSION
argument_list|)
decl_stmt|;
name|parser
operator|=
operator|new
name|QueryParser
argument_list|(
name|LUCENE_VERSION
argument_list|,
name|Constants
operator|.
name|DOC_FIELD
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cannot initialize documentation full text index"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|searcher
operator|=
literal|null
expr_stmt|;
name|parser
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|doQuery (String q)
specifier|public
name|List
argument_list|<
name|DocResult
argument_list|>
name|doQuery
parameter_list|(
name|String
name|q
parameter_list|)
throws|throws
name|DocQueryException
block|{
if|if
condition|(
name|parser
operator|==
literal|null
operator|||
name|searcher
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|DocQueryException
argument_list|(
literal|"Documentation search not available"
argument_list|)
throw|;
block|}
try|try
block|{
name|Query
name|query
init|=
name|parser
operator|.
name|parse
argument_list|(
name|q
argument_list|)
decl_stmt|;
comment|// TODO(fishywang): Currently as we don't have much documentation, we just use MAX_VALUE here
comment|// and skipped paging. Maybe add paging later.
name|TopDocs
name|results
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
name|results
operator|.
name|scoreDocs
decl_stmt|;
name|int
name|totalHits
init|=
name|results
operator|.
name|totalHits
decl_stmt|;
name|List
argument_list|<
name|DocResult
argument_list|>
name|out
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|totalHits
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|totalHits
condition|;
name|i
operator|++
control|)
block|{
name|DocResult
name|result
init|=
operator|new
name|DocResult
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|hits
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|result
operator|.
name|url
operator|=
name|doc
operator|.
name|get
argument_list|(
name|Constants
operator|.
name|URL_FIELD
argument_list|)
expr_stmt|;
name|result
operator|.
name|title
operator|=
name|doc
operator|.
name|get
argument_list|(
name|Constants
operator|.
name|TITLE_FIELD
argument_list|)
expr_stmt|;
name|out
operator|.
name|add
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
return|return
name|out
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DocQueryException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DocQueryException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|readIndexDirectory ()
specifier|protected
name|Directory
name|readIndexDirectory
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|4096
index|]
decl_stmt|;
name|InputStream
name|index
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|Constants
operator|.
name|INDEX_ZIP
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"No index available"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|ZipInputStream
name|zip
init|=
operator|new
name|ZipInputStream
argument_list|(
name|index
argument_list|)
decl_stmt|;
try|try
block|{
name|ZipEntry
name|entry
decl_stmt|;
while|while
condition|(
operator|(
name|entry
operator|=
name|zip
operator|.
name|getNextEntry
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|IndexOutput
name|out
init|=
name|dir
operator|.
name|createOutput
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|int
name|count
decl_stmt|;
while|while
condition|(
operator|(
name|count
operator|=
name|zip
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|out
operator|.
name|writeBytes
argument_list|(
name|buffer
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|zip
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// We must NOT call dir.close() here, as DirectoryReader.open() expects an opened directory.
return|return
name|dir
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
DECL|class|DocQueryException
specifier|public
specifier|static
class|class
name|DocQueryException
extends|extends
name|Exception
block|{
DECL|method|DocQueryException ()
name|DocQueryException
parameter_list|()
block|{     }
DECL|method|DocQueryException (String msg)
name|DocQueryException
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
DECL|method|DocQueryException (String msg, Throwable e)
name|DocQueryException
parameter_list|(
name|String
name|msg
parameter_list|,
name|Throwable
name|e
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
DECL|method|DocQueryException (Throwable e)
name|DocQueryException
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|super
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

