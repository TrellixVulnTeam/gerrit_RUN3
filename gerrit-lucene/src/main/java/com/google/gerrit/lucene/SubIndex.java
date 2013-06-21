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
comment|// limitations under the License.package com.google.gerrit.server.git;
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
name|IndexWriter
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
name|Term
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
name|SearcherManager
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
name|FSDirectory
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
name|File
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

begin_comment
comment|/** Piece of the change index that is implemented as a separate Lucene index. */
end_comment

begin_class
DECL|class|SubIndex
class|class
name|SubIndex
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
name|SubIndex
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|dir
specifier|private
specifier|final
name|Directory
name|dir
decl_stmt|;
DECL|field|writer
specifier|private
specifier|final
name|IndexWriter
name|writer
decl_stmt|;
DECL|field|searcherManager
specifier|private
specifier|final
name|SearcherManager
name|searcherManager
decl_stmt|;
DECL|method|SubIndex (File file, IndexWriterConfig writerConfig)
name|SubIndex
parameter_list|(
name|File
name|file
parameter_list|,
name|IndexWriterConfig
name|writerConfig
parameter_list|)
throws|throws
name|IOException
block|{
name|dir
operator|=
name|FSDirectory
operator|.
name|open
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|writerConfig
argument_list|)
expr_stmt|;
name|searcherManager
operator|=
operator|new
name|SearcherManager
argument_list|(
name|writer
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|close ()
name|void
name|close
parameter_list|()
block|{
try|try
block|{
name|searcherManager
operator|.
name|close
argument_list|()
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
name|warn
argument_list|(
literal|"error closing Lucene searcher"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|writer
operator|.
name|close
argument_list|()
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
name|warn
argument_list|(
literal|"error closing Lucene writer"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|dir
operator|.
name|close
argument_list|()
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
name|warn
argument_list|(
literal|"error closing Lucene directory"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|insert (Document doc)
name|void
name|insert
parameter_list|(
name|Document
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
DECL|method|replace (Term term, Document doc)
name|void
name|replace
parameter_list|(
name|Term
name|term
parameter_list|,
name|Document
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|updateDocument
argument_list|(
name|term
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
DECL|method|delete (Term term)
name|void
name|delete
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|deleteDocuments
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
DECL|method|acquire ()
name|IndexSearcher
name|acquire
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|searcherManager
operator|.
name|acquire
argument_list|()
return|;
block|}
DECL|method|release (IndexSearcher searcher)
name|void
name|release
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|searcherManager
operator|.
name|release
argument_list|(
name|searcher
argument_list|)
expr_stmt|;
block|}
DECL|method|maybeRefresh ()
name|void
name|maybeRefresh
parameter_list|()
block|{
try|try
block|{
name|searcherManager
operator|.
name|maybeRefresh
argument_list|()
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
name|warn
argument_list|(
literal|"error refreshing indexer"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

