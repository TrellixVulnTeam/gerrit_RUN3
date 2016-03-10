begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2014 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|change
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
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
name|common
operator|.
name|cache
operator|.
name|CacheBuilder
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
name|cache
operator|.
name|CacheLoader
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
name|cache
operator|.
name|LoadingCache
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
name|AccountInfo
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
name|reviewdb
operator|.
name|client
operator|.
name|Account
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
name|reviewdb
operator|.
name|server
operator|.
name|AccountExternalIdAccess
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
name|reviewdb
operator|.
name|server
operator|.
name|ReviewDb
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
name|account
operator|.
name|AccountState
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
name|GerritServerConfig
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|server
operator|.
name|OrmException
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
name|document
operator|.
name|Field
operator|.
name|Store
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
name|IntField
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
name|StringField
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
name|TextField
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
name|IndexWriterConfig
operator|.
name|OpenMode
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
name|IndexableField
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
name|BooleanClause
operator|.
name|Occur
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
name|BooleanQuery
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
name|PrefixQuery
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
name|RAMDirectory
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
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|concurrent
operator|.
name|ExecutionException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  * The suggest oracle may be called many times in rapid succession during the  * course of one operation.  * It would be easy to have a simple {@code Cache<Boolean, List<Account>>}  * with a short expiration time of 30s.  * Cache only has a single key we're just using Cache for the expiration behavior.  */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|ReviewerSuggestionCache
specifier|public
class|class
name|ReviewerSuggestionCache
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
name|ReviewerSuggestionCache
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|ID
specifier|private
specifier|static
specifier|final
name|String
name|ID
init|=
literal|"id"
decl_stmt|;
DECL|field|NAME
specifier|private
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"name"
decl_stmt|;
DECL|field|EMAIL
specifier|private
specifier|static
specifier|final
name|String
name|EMAIL
init|=
literal|"email"
decl_stmt|;
DECL|field|USERNAME
specifier|private
specifier|static
specifier|final
name|String
name|USERNAME
init|=
literal|"username"
decl_stmt|;
DECL|field|ALL
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|ALL
init|=
block|{
name|ID
block|,
name|NAME
block|,
name|EMAIL
block|,
name|USERNAME
block|}
decl_stmt|;
DECL|field|cache
specifier|private
specifier|final
name|LoadingCache
argument_list|<
name|Boolean
argument_list|,
name|IndexSearcher
argument_list|>
name|cache
decl_stmt|;
DECL|field|db
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
decl_stmt|;
annotation|@
name|Inject
DECL|method|ReviewerSuggestionCache (Provider<ReviewDb> db, @GerritServerConfig Config cfg)
name|ReviewerSuggestionCache
parameter_list|(
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|,
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|)
block|{
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|long
name|expiration
init|=
name|ConfigUtil
operator|.
name|getTimeUnit
argument_list|(
name|cfg
argument_list|,
literal|"suggest"
argument_list|,
literal|null
argument_list|,
literal|"fullTextSearchRefresh"
argument_list|,
name|TimeUnit
operator|.
name|HOURS
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|this
operator|.
name|cache
operator|=
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|maximumSize
argument_list|(
literal|1
argument_list|)
operator|.
name|refreshAfterWrite
argument_list|(
name|expiration
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
operator|.
name|build
argument_list|(
operator|new
name|CacheLoader
argument_list|<
name|Boolean
argument_list|,
name|IndexSearcher
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|IndexSearcher
name|load
parameter_list|(
name|Boolean
name|key
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|index
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|search (String query, int n)
specifier|public
name|List
argument_list|<
name|AccountInfo
argument_list|>
name|search
parameter_list|(
name|String
name|query
parameter_list|,
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexSearcher
name|searcher
init|=
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|searcher
operator|==
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|segments
init|=
name|Splitter
operator|.
name|on
argument_list|(
literal|' '
argument_list|)
operator|.
name|omitEmptyStrings
argument_list|()
operator|.
name|splitToList
argument_list|(
name|query
operator|.
name|toLowerCase
argument_list|()
argument_list|)
decl_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|q
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|field
range|:
name|ALL
control|)
block|{
name|BooleanQuery
operator|.
name|Builder
name|and
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|segments
control|)
block|{
name|and
operator|.
name|add
argument_list|(
operator|new
name|PrefixQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|s
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
name|q
operator|.
name|add
argument_list|(
name|and
operator|.
name|build
argument_list|()
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
name|TopDocs
name|results
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q
operator|.
name|build
argument_list|()
argument_list|,
name|n
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
name|List
argument_list|<
name|AccountInfo
argument_list|>
name|result
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ScoreDoc
name|h
range|:
name|hits
control|)
block|{
name|Document
name|doc
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|h
operator|.
name|doc
argument_list|)
decl_stmt|;
name|IndexableField
name|idField
init|=
name|checkNotNull
argument_list|(
name|doc
operator|.
name|getField
argument_list|(
name|ID
argument_list|)
argument_list|)
decl_stmt|;
name|AccountInfo
name|info
init|=
operator|new
name|AccountInfo
argument_list|(
name|idField
operator|.
name|numericValue
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|)
decl_stmt|;
name|info
operator|.
name|name
operator|=
name|doc
operator|.
name|get
argument_list|(
name|NAME
argument_list|)
expr_stmt|;
name|info
operator|.
name|email
operator|=
name|doc
operator|.
name|get
argument_list|(
name|EMAIL
argument_list|)
expr_stmt|;
name|info
operator|.
name|username
operator|=
name|doc
operator|.
name|get
argument_list|(
name|USERNAME
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|get ()
specifier|private
name|IndexSearcher
name|get
parameter_list|()
block|{
try|try
block|{
return|return
name|cache
operator|.
name|get
argument_list|(
literal|true
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Cannot fetch reviewers from cache"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
DECL|method|index ()
specifier|private
name|IndexSearcher
name|index
parameter_list|()
throws|throws
name|IOException
throws|,
name|OrmException
block|{
name|RAMDirectory
name|idx
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|config
init|=
operator|new
name|IndexWriterConfig
argument_list|(
operator|new
name|StandardAnalyzer
argument_list|(
name|CharArraySet
operator|.
name|EMPTY_SET
argument_list|)
argument_list|)
decl_stmt|;
name|config
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|CREATE
argument_list|)
expr_stmt|;
try|try
init|(
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|idx
argument_list|,
name|config
argument_list|)
init|)
block|{
for|for
control|(
name|Account
name|a
range|:
name|db
operator|.
name|get
argument_list|()
operator|.
name|accounts
argument_list|()
operator|.
name|all
argument_list|()
control|)
block|{
if|if
condition|(
name|a
operator|.
name|isActive
argument_list|()
condition|)
block|{
name|addAccount
argument_list|(
name|writer
argument_list|,
name|a
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
operator|new
name|IndexSearcher
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|idx
argument_list|)
argument_list|)
return|;
block|}
DECL|method|addAccount (IndexWriter writer, Account a)
specifier|private
name|void
name|addAccount
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|Account
name|a
parameter_list|)
throws|throws
name|IOException
throws|,
name|OrmException
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|IntField
argument_list|(
name|ID
argument_list|,
name|a
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|a
operator|.
name|getFullName
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
name|NAME
argument_list|,
name|a
operator|.
name|getFullName
argument_list|()
argument_list|,
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|a
operator|.
name|getPreferredEmail
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
name|EMAIL
argument_list|,
name|a
operator|.
name|getPreferredEmail
argument_list|()
argument_list|,
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
name|EMAIL
argument_list|,
name|a
operator|.
name|getPreferredEmail
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|,
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|AccountExternalIdAccess
name|extIdAccess
init|=
name|db
operator|.
name|get
argument_list|()
operator|.
name|accountExternalIds
argument_list|()
decl_stmt|;
name|String
name|username
init|=
name|AccountState
operator|.
name|getUserName
argument_list|(
name|extIdAccess
operator|.
name|byAccount
argument_list|(
name|a
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|username
operator|!=
literal|null
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
name|USERNAME
argument_list|,
name|username
argument_list|,
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

