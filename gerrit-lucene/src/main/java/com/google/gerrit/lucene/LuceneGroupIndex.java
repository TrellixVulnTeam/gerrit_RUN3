begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2017 The Android Open Source Project
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
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|index
operator|.
name|group
operator|.
name|GroupField
operator|.
name|UUID
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
name|AccountGroup
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
name|GroupCache
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
name|gerrit
operator|.
name|server
operator|.
name|config
operator|.
name|SitePaths
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
name|index
operator|.
name|IndexUtils
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
name|index
operator|.
name|QueryOptions
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
name|index
operator|.
name|Schema
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
name|index
operator|.
name|group
operator|.
name|GroupIndex
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
name|DataSource
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
name|Predicate
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
name|QueryParseException
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
name|gwtorm
operator|.
name|server
operator|.
name|ResultSet
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
name|SearcherFactory
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
name|Sort
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
name|SortField
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
name|TopFieldDocs
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
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|Iterator
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

begin_class
DECL|class|LuceneGroupIndex
specifier|public
class|class
name|LuceneGroupIndex
extends|extends
name|AbstractLuceneIndex
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|,
name|AccountGroup
argument_list|>
implements|implements
name|GroupIndex
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
name|LuceneGroupIndex
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|GROUPS
specifier|private
specifier|static
specifier|final
name|String
name|GROUPS
init|=
literal|"groups"
decl_stmt|;
DECL|field|UUID_SORT_FIELD
specifier|private
specifier|static
specifier|final
name|String
name|UUID_SORT_FIELD
init|=
name|sortFieldName
argument_list|(
name|UUID
argument_list|)
decl_stmt|;
DECL|method|idTerm (AccountGroup group)
specifier|private
specifier|static
name|Term
name|idTerm
parameter_list|(
name|AccountGroup
name|group
parameter_list|)
block|{
return|return
name|idTerm
argument_list|(
name|group
operator|.
name|getGroupUUID
argument_list|()
argument_list|)
return|;
block|}
DECL|method|idTerm (AccountGroup.UUID uuid)
specifier|private
specifier|static
name|Term
name|idTerm
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|uuid
parameter_list|)
block|{
return|return
name|QueryBuilder
operator|.
name|stringTerm
argument_list|(
name|UUID
operator|.
name|getName
argument_list|()
argument_list|,
name|uuid
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
DECL|field|indexWriterConfig
specifier|private
specifier|final
name|GerritIndexWriterConfig
name|indexWriterConfig
decl_stmt|;
DECL|field|queryBuilder
specifier|private
specifier|final
name|QueryBuilder
argument_list|<
name|AccountGroup
argument_list|>
name|queryBuilder
decl_stmt|;
DECL|field|groupCache
specifier|private
specifier|final
name|GroupCache
name|groupCache
decl_stmt|;
DECL|method|dir (Schema<AccountGroup> schema, Config cfg, SitePaths sitePaths)
specifier|private
specifier|static
name|Directory
name|dir
parameter_list|(
name|Schema
argument_list|<
name|AccountGroup
argument_list|>
name|schema
parameter_list|,
name|Config
name|cfg
parameter_list|,
name|SitePaths
name|sitePaths
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|LuceneIndexModule
operator|.
name|isInMemoryTest
argument_list|(
name|cfg
argument_list|)
condition|)
block|{
return|return
operator|new
name|RAMDirectory
argument_list|()
return|;
block|}
name|Path
name|indexDir
init|=
name|LuceneVersionManager
operator|.
name|getDir
argument_list|(
name|sitePaths
argument_list|,
name|GROUPS
operator|+
literal|"_"
argument_list|,
name|schema
argument_list|)
decl_stmt|;
return|return
name|FSDirectory
operator|.
name|open
argument_list|(
name|indexDir
argument_list|)
return|;
block|}
annotation|@
name|Inject
DECL|method|LuceneGroupIndex ( @erritServerConfig Config cfg, SitePaths sitePaths, GroupCache groupCache, @Assisted Schema<AccountGroup> schema)
name|LuceneGroupIndex
parameter_list|(
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|,
name|SitePaths
name|sitePaths
parameter_list|,
name|GroupCache
name|groupCache
parameter_list|,
annotation|@
name|Assisted
name|Schema
argument_list|<
name|AccountGroup
argument_list|>
name|schema
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|schema
argument_list|,
name|sitePaths
argument_list|,
name|dir
argument_list|(
name|schema
argument_list|,
name|cfg
argument_list|,
name|sitePaths
argument_list|)
argument_list|,
name|GROUPS
argument_list|,
literal|null
argument_list|,
operator|new
name|GerritIndexWriterConfig
argument_list|(
name|cfg
argument_list|,
name|GROUPS
argument_list|)
argument_list|,
operator|new
name|SearcherFactory
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|groupCache
operator|=
name|groupCache
expr_stmt|;
name|indexWriterConfig
operator|=
operator|new
name|GerritIndexWriterConfig
argument_list|(
name|cfg
argument_list|,
name|GROUPS
argument_list|)
expr_stmt|;
name|queryBuilder
operator|=
operator|new
name|QueryBuilder
argument_list|<>
argument_list|(
name|schema
argument_list|,
name|indexWriterConfig
operator|.
name|getAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|replace (AccountGroup group)
specifier|public
name|void
name|replace
parameter_list|(
name|AccountGroup
name|group
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
comment|// No parts of FillArgs are currently required, just use null.
name|replace
argument_list|(
name|idTerm
argument_list|(
name|group
argument_list|)
argument_list|,
name|toDocument
argument_list|(
name|group
argument_list|,
literal|null
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
decl||
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|delete (AccountGroup.UUID key)
specifier|public
name|void
name|delete
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|key
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|delete
argument_list|(
name|idTerm
argument_list|(
name|key
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
decl||
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getSource (Predicate<AccountGroup> p, QueryOptions opts)
specifier|public
name|DataSource
argument_list|<
name|AccountGroup
argument_list|>
name|getSource
parameter_list|(
name|Predicate
argument_list|<
name|AccountGroup
argument_list|>
name|p
parameter_list|,
name|QueryOptions
name|opts
parameter_list|)
throws|throws
name|QueryParseException
block|{
return|return
operator|new
name|QuerySource
argument_list|(
name|opts
argument_list|,
name|queryBuilder
operator|.
name|toQuery
argument_list|(
name|p
argument_list|)
argument_list|,
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
name|UUID_SORT_FIELD
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|STRING
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
DECL|class|QuerySource
specifier|private
class|class
name|QuerySource
implements|implements
name|DataSource
argument_list|<
name|AccountGroup
argument_list|>
block|{
DECL|field|opts
specifier|private
specifier|final
name|QueryOptions
name|opts
decl_stmt|;
DECL|field|query
specifier|private
specifier|final
name|Query
name|query
decl_stmt|;
DECL|field|sort
specifier|private
specifier|final
name|Sort
name|sort
decl_stmt|;
DECL|method|QuerySource (QueryOptions opts, Query query, Sort sort)
specifier|private
name|QuerySource
parameter_list|(
name|QueryOptions
name|opts
parameter_list|,
name|Query
name|query
parameter_list|,
name|Sort
name|sort
parameter_list|)
block|{
name|this
operator|.
name|opts
operator|=
name|opts
expr_stmt|;
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|sort
operator|=
name|sort
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCardinality ()
specifier|public
name|int
name|getCardinality
parameter_list|()
block|{
return|return
literal|10
return|;
block|}
annotation|@
name|Override
DECL|method|read ()
specifier|public
name|ResultSet
argument_list|<
name|AccountGroup
argument_list|>
name|read
parameter_list|()
throws|throws
name|OrmException
block|{
name|IndexSearcher
name|searcher
init|=
literal|null
decl_stmt|;
try|try
block|{
name|searcher
operator|=
name|acquire
argument_list|()
expr_stmt|;
name|int
name|realLimit
init|=
name|opts
operator|.
name|start
argument_list|()
operator|+
name|opts
operator|.
name|limit
argument_list|()
decl_stmt|;
name|TopFieldDocs
name|docs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|realLimit
argument_list|,
name|sort
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|AccountGroup
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|docs
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|opts
operator|.
name|start
argument_list|()
init|;
name|i
operator|<
name|docs
operator|.
name|scoreDocs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ScoreDoc
name|sd
init|=
name|docs
operator|.
name|scoreDocs
index|[
name|i
index|]
decl_stmt|;
name|Document
name|doc
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|sd
operator|.
name|doc
argument_list|,
name|IndexUtils
operator|.
name|groupFields
argument_list|(
name|opts
argument_list|)
argument_list|)
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|toAccountGroup
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|AccountGroup
argument_list|>
name|r
init|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|result
argument_list|)
decl_stmt|;
return|return
operator|new
name|ResultSet
argument_list|<
name|AccountGroup
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|AccountGroup
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|r
operator|.
name|iterator
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|AccountGroup
argument_list|>
name|toList
parameter_list|()
block|{
return|return
name|r
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
comment|// Do nothing.
block|}
block|}
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
name|OrmException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|searcher
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|release
argument_list|(
name|searcher
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
name|warn
argument_list|(
literal|"cannot release Lucene searcher"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
DECL|method|toAccountGroup (Document doc)
specifier|private
name|AccountGroup
name|toAccountGroup
parameter_list|(
name|Document
name|doc
parameter_list|)
block|{
name|AccountGroup
operator|.
name|UUID
name|uuid
init|=
operator|new
name|AccountGroup
operator|.
name|UUID
argument_list|(
name|doc
operator|.
name|getField
argument_list|(
name|UUID
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|stringValue
argument_list|()
argument_list|)
decl_stmt|;
comment|// Use the GroupCache rather than depending on any stored fields in the
comment|// document (of which there shouldn't be any).
return|return
name|groupCache
operator|.
name|get
argument_list|(
name|uuid
argument_list|)
return|;
block|}
block|}
end_class

end_unit

