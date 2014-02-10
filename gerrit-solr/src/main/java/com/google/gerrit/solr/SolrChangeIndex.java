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
DECL|package|com.google.gerrit.solr
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|solr
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
name|IndexRewriteImpl
operator|.
name|CLOSED_STATUSES
import|;
end_import

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
name|IndexRewriteImpl
operator|.
name|OPEN_STATUSES
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|solr
operator|.
name|IndexVersionCheck
operator|.
name|SCHEMA_VERSIONS
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|solr
operator|.
name|IndexVersionCheck
operator|.
name|solrIndexConfig
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
name|common
operator|.
name|collect
operator|.
name|ImmutableList
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
name|Lists
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
name|Sets
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
name|events
operator|.
name|LifecycleListener
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
name|lucene
operator|.
name|QueryBuilder
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
name|ChangeField
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
name|ChangeIndex
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
name|FieldDef
operator|.
name|FillArgs
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
name|FieldType
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
name|IndexCollection
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
name|IndexRewriteImpl
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
name|Schema
operator|.
name|Values
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
name|ChangeDataSource
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
name|ChangeQueryBuilder
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
name|SortKeyPredicate
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
name|Provider
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
name|util
operator|.
name|Version
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|SolrQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|SolrQuery
operator|.
name|SortClause
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|SolrServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|SolrServerException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|CloudSolrServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrDocument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrDocumentList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrInputDocument
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
name|eclipse
operator|.
name|jgit
operator|.
name|storage
operator|.
name|file
operator|.
name|FileBasedConfig
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
name|util
operator|.
name|FS
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
name|sql
operator|.
name|Timestamp
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/** Secondary index implementation using a remote Solr instance. */
end_comment

begin_class
DECL|class|SolrChangeIndex
class|class
name|SolrChangeIndex
implements|implements
name|ChangeIndex
implements|,
name|LifecycleListener
block|{
DECL|field|CHANGES_OPEN
specifier|public
specifier|static
specifier|final
name|String
name|CHANGES_OPEN
init|=
literal|"changes_open"
decl_stmt|;
DECL|field|CHANGES_CLOSED
specifier|public
specifier|static
specifier|final
name|String
name|CHANGES_CLOSED
init|=
literal|"changes_closed"
decl_stmt|;
DECL|field|ID_FIELD
specifier|private
specifier|static
specifier|final
name|String
name|ID_FIELD
init|=
name|ChangeField
operator|.
name|LEGACY_ID
operator|.
name|getName
argument_list|()
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
DECL|field|changeDataFactory
specifier|private
specifier|final
name|ChangeData
operator|.
name|Factory
name|changeDataFactory
decl_stmt|;
DECL|field|fillArgs
specifier|private
specifier|final
name|FillArgs
name|fillArgs
decl_stmt|;
DECL|field|sitePaths
specifier|private
specifier|final
name|SitePaths
name|sitePaths
decl_stmt|;
DECL|field|indexes
specifier|private
specifier|final
name|IndexCollection
name|indexes
decl_stmt|;
DECL|field|openIndex
specifier|private
specifier|final
name|CloudSolrServer
name|openIndex
decl_stmt|;
DECL|field|closedIndex
specifier|private
specifier|final
name|CloudSolrServer
name|closedIndex
decl_stmt|;
DECL|field|schema
specifier|private
specifier|final
name|Schema
argument_list|<
name|ChangeData
argument_list|>
name|schema
decl_stmt|;
DECL|field|queryBuilder
specifier|private
specifier|final
name|QueryBuilder
name|queryBuilder
decl_stmt|;
DECL|method|SolrChangeIndex ( @erritServerConfig Config cfg, Provider<ReviewDb> db, ChangeData.Factory changeDataFactory, FillArgs fillArgs, SitePaths sitePaths, IndexCollection indexes, Schema<ChangeData> schema, String base)
name|SolrChangeIndex
parameter_list|(
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|,
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|,
name|ChangeData
operator|.
name|Factory
name|changeDataFactory
parameter_list|,
name|FillArgs
name|fillArgs
parameter_list|,
name|SitePaths
name|sitePaths
parameter_list|,
name|IndexCollection
name|indexes
parameter_list|,
name|Schema
argument_list|<
name|ChangeData
argument_list|>
name|schema
parameter_list|,
name|String
name|base
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|changeDataFactory
operator|=
name|changeDataFactory
expr_stmt|;
name|this
operator|.
name|fillArgs
operator|=
name|fillArgs
expr_stmt|;
name|this
operator|.
name|sitePaths
operator|=
name|sitePaths
expr_stmt|;
name|this
operator|.
name|indexes
operator|=
name|indexes
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|String
name|url
init|=
name|cfg
operator|.
name|getString
argument_list|(
literal|"index"
argument_list|,
literal|"solr"
argument_list|,
literal|"url"
argument_list|)
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|url
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"index.solr.url must be supplied"
argument_list|)
throw|;
block|}
comment|// Version is only used to determine the list of stop words used by the
comment|// analyzer, so use the latest version rather than trying to match the Solr
comment|// server version.
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
name|Version
name|v
init|=
name|Version
operator|.
name|LUCENE_CURRENT
decl_stmt|;
name|queryBuilder
operator|=
operator|new
name|QueryBuilder
argument_list|(
name|schema
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|(
name|v
argument_list|,
name|CharArraySet
operator|.
name|EMPTY_SET
argument_list|)
argument_list|)
expr_stmt|;
name|base
operator|=
name|Strings
operator|.
name|nullToEmpty
argument_list|(
name|base
argument_list|)
expr_stmt|;
name|openIndex
operator|=
operator|new
name|CloudSolrServer
argument_list|(
name|url
argument_list|)
expr_stmt|;
name|openIndex
operator|.
name|setDefaultCollection
argument_list|(
name|base
operator|+
name|CHANGES_OPEN
argument_list|)
expr_stmt|;
name|closedIndex
operator|=
operator|new
name|CloudSolrServer
argument_list|(
name|url
argument_list|)
expr_stmt|;
name|closedIndex
operator|.
name|setDefaultCollection
argument_list|(
name|base
operator|+
name|CHANGES_CLOSED
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{
name|indexes
operator|.
name|setSearchIndex
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|indexes
operator|.
name|addWriteIndex
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|openIndex
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|closedIndex
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSchema ()
specifier|public
name|Schema
argument_list|<
name|ChangeData
argument_list|>
name|getSchema
parameter_list|()
block|{
return|return
name|schema
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|insert (ChangeData cd)
specifier|public
name|void
name|insert
parameter_list|(
name|ChangeData
name|cd
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|id
init|=
name|cd
operator|.
name|getId
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|SolrInputDocument
name|doc
init|=
name|toDocument
argument_list|(
name|cd
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|cd
operator|.
name|change
argument_list|()
operator|.
name|getStatus
argument_list|()
operator|.
name|isOpen
argument_list|()
condition|)
block|{
name|closedIndex
operator|.
name|deleteById
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|openIndex
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|openIndex
operator|.
name|deleteById
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|closedIndex
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|OrmException
decl||
name|SolrServerException
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
name|commit
argument_list|(
name|openIndex
argument_list|)
expr_stmt|;
name|commit
argument_list|(
name|closedIndex
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|replace (ChangeData cd)
specifier|public
name|void
name|replace
parameter_list|(
name|ChangeData
name|cd
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|id
init|=
name|cd
operator|.
name|getId
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|SolrInputDocument
name|doc
init|=
name|toDocument
argument_list|(
name|cd
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|cd
operator|.
name|change
argument_list|()
operator|.
name|getStatus
argument_list|()
operator|.
name|isOpen
argument_list|()
condition|)
block|{
name|closedIndex
operator|.
name|deleteById
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|openIndex
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|openIndex
operator|.
name|deleteById
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|closedIndex
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|OrmException
decl||
name|SolrServerException
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
name|commit
argument_list|(
name|openIndex
argument_list|)
expr_stmt|;
name|commit
argument_list|(
name|closedIndex
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|delete (ChangeData cd)
specifier|public
name|void
name|delete
parameter_list|(
name|ChangeData
name|cd
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|id
init|=
name|cd
operator|.
name|getId
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|cd
operator|.
name|change
argument_list|()
operator|.
name|getStatus
argument_list|()
operator|.
name|isOpen
argument_list|()
condition|)
block|{
name|openIndex
operator|.
name|deleteById
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|commit
argument_list|(
name|openIndex
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|closedIndex
operator|.
name|deleteById
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|commit
argument_list|(
name|closedIndex
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|OrmException
decl||
name|SolrServerException
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
DECL|method|deleteAll ()
specifier|public
name|void
name|deleteAll
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|openIndex
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|closedIndex
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
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
name|commit
argument_list|(
name|openIndex
argument_list|)
expr_stmt|;
name|commit
argument_list|(
name|closedIndex
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSource (Predicate<ChangeData> p, int start, int limit)
specifier|public
name|ChangeDataSource
name|getSource
parameter_list|(
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|p
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|limit
parameter_list|)
throws|throws
name|QueryParseException
block|{
name|Set
argument_list|<
name|Change
operator|.
name|Status
argument_list|>
name|statuses
init|=
name|IndexRewriteImpl
operator|.
name|getPossibleStatus
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|SolrServer
argument_list|>
name|indexes
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Sets
operator|.
name|intersection
argument_list|(
name|statuses
argument_list|,
name|OPEN_STATUSES
argument_list|)
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|indexes
operator|.
name|add
argument_list|(
name|openIndex
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|Sets
operator|.
name|intersection
argument_list|(
name|statuses
argument_list|,
name|CLOSED_STATUSES
argument_list|)
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|indexes
operator|.
name|add
argument_list|(
name|closedIndex
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|QuerySource
argument_list|(
name|indexes
argument_list|,
name|queryBuilder
operator|.
name|toQuery
argument_list|(
name|p
argument_list|)
argument_list|,
name|start
argument_list|,
name|limit
argument_list|,
name|getSorts
argument_list|(
name|schema
argument_list|,
name|p
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|getSorts (Schema<ChangeData> schema, Predicate<ChangeData> p)
specifier|private
specifier|static
name|List
argument_list|<
name|SortClause
argument_list|>
name|getSorts
parameter_list|(
name|Schema
argument_list|<
name|ChangeData
argument_list|>
name|schema
parameter_list|,
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|p
parameter_list|)
block|{
if|if
condition|(
name|SortKeyPredicate
operator|.
name|hasSortKeyField
argument_list|(
name|schema
argument_list|)
condition|)
block|{
name|boolean
name|reverse
init|=
name|ChangeQueryBuilder
operator|.
name|hasNonTrivialSortKeyAfter
argument_list|(
name|schema
argument_list|,
name|p
argument_list|)
decl_stmt|;
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
operator|new
name|SortClause
argument_list|(
name|ChangeField
operator|.
name|SORTKEY
operator|.
name|getName
argument_list|()
argument_list|,
operator|!
name|reverse
condition|?
name|SolrQuery
operator|.
name|ORDER
operator|.
name|desc
else|:
name|SolrQuery
operator|.
name|ORDER
operator|.
name|asc
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
operator|new
name|SortClause
argument_list|(
name|ChangeField
operator|.
name|UPDATED
operator|.
name|getName
argument_list|()
argument_list|,
name|SolrQuery
operator|.
name|ORDER
operator|.
name|desc
argument_list|)
argument_list|,
operator|new
name|SortClause
argument_list|(
name|ChangeField
operator|.
name|LEGACY_ID
operator|.
name|getName
argument_list|()
argument_list|,
name|SolrQuery
operator|.
name|ORDER
operator|.
name|desc
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|method|commit (SolrServer server)
specifier|private
name|void
name|commit
parameter_list|(
name|SolrServer
name|server
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|server
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
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
DECL|class|QuerySource
specifier|private
class|class
name|QuerySource
implements|implements
name|ChangeDataSource
block|{
DECL|field|indexes
specifier|private
specifier|final
name|List
argument_list|<
name|SolrServer
argument_list|>
name|indexes
decl_stmt|;
DECL|field|query
specifier|private
specifier|final
name|SolrQuery
name|query
decl_stmt|;
DECL|method|QuerySource (List<SolrServer> indexes, Query q, int start, int limit, List<SortClause> sorts)
specifier|public
name|QuerySource
parameter_list|(
name|List
argument_list|<
name|SolrServer
argument_list|>
name|indexes
parameter_list|,
name|Query
name|q
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|limit
parameter_list|,
name|List
argument_list|<
name|SortClause
argument_list|>
name|sorts
parameter_list|)
block|{
name|this
operator|.
name|indexes
operator|=
name|indexes
expr_stmt|;
name|query
operator|=
operator|new
name|SolrQuery
argument_list|(
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|query
operator|.
name|setParam
argument_list|(
literal|"shards.tolerant"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|query
operator|.
name|setParam
argument_list|(
literal|"rows"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|limit
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|start
operator|!=
literal|0
condition|)
block|{
name|query
operator|.
name|setParam
argument_list|(
literal|"start"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|start
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|query
operator|.
name|setFields
argument_list|(
name|ID_FIELD
argument_list|)
expr_stmt|;
name|query
operator|.
name|setSorts
argument_list|(
name|sorts
argument_list|)
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
comment|// TODO: estimate from solr?
block|}
annotation|@
name|Override
DECL|method|hasChange ()
specifier|public
name|boolean
name|hasChange
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|query
operator|.
name|getQuery
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|read ()
specifier|public
name|ResultSet
argument_list|<
name|ChangeData
argument_list|>
name|read
parameter_list|()
throws|throws
name|OrmException
block|{
try|try
block|{
comment|// TODO Sort documents during merge to select only top N.
name|SolrDocumentList
name|docs
init|=
operator|new
name|SolrDocumentList
argument_list|()
decl_stmt|;
for|for
control|(
name|SolrServer
name|index
range|:
name|indexes
control|)
block|{
name|docs
operator|.
name|addAll
argument_list|(
name|index
operator|.
name|query
argument_list|(
name|query
argument_list|)
operator|.
name|getResults
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|ChangeData
argument_list|>
name|result
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|docs
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|SolrDocument
name|doc
range|:
name|docs
control|)
block|{
name|Integer
name|v
init|=
operator|(
name|Integer
operator|)
name|doc
operator|.
name|getFieldValue
argument_list|(
name|ID_FIELD
argument_list|)
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|changeDataFactory
operator|.
name|create
argument_list|(
name|db
operator|.
name|get
argument_list|()
argument_list|,
operator|new
name|Change
operator|.
name|Id
argument_list|(
name|v
operator|.
name|intValue
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|ChangeData
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
name|ChangeData
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|ChangeData
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
name|ChangeData
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
name|SolrServerException
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
block|}
block|}
DECL|method|toDocument (ChangeData cd)
specifier|private
name|SolrInputDocument
name|toDocument
parameter_list|(
name|ChangeData
name|cd
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|SolrInputDocument
name|result
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
for|for
control|(
name|Values
argument_list|<
name|ChangeData
argument_list|>
name|values
range|:
name|schema
operator|.
name|buildFields
argument_list|(
name|cd
argument_list|,
name|fillArgs
argument_list|)
control|)
block|{
name|add
argument_list|(
name|result
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
name|OrmException
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
DECL|method|add (SolrInputDocument doc, Values<ChangeData> values)
specifier|private
name|void
name|add
parameter_list|(
name|SolrInputDocument
name|doc
parameter_list|,
name|Values
argument_list|<
name|ChangeData
argument_list|>
name|values
parameter_list|)
throws|throws
name|OrmException
block|{
name|String
name|name
init|=
name|values
operator|.
name|getField
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|FieldType
argument_list|<
name|?
argument_list|>
name|type
init|=
name|values
operator|.
name|getField
argument_list|()
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|FieldType
operator|.
name|INTEGER
condition|)
block|{
for|for
control|(
name|Object
name|value
range|:
name|values
operator|.
name|getValues
argument_list|()
control|)
block|{
name|doc
operator|.
name|addField
argument_list|(
name|name
argument_list|,
operator|(
name|Integer
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|FieldType
operator|.
name|LONG
condition|)
block|{
for|for
control|(
name|Object
name|value
range|:
name|values
operator|.
name|getValues
argument_list|()
control|)
block|{
name|doc
operator|.
name|addField
argument_list|(
name|name
argument_list|,
operator|(
name|Long
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|FieldType
operator|.
name|TIMESTAMP
condition|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
name|boolean
name|legacy
init|=
name|values
operator|.
name|getField
argument_list|()
operator|==
name|ChangeField
operator|.
name|LEGACY_UPDATED
decl_stmt|;
if|if
condition|(
name|legacy
condition|)
block|{
for|for
control|(
name|Object
name|value
range|:
name|values
operator|.
name|getValues
argument_list|()
control|)
block|{
name|int
name|t
init|=
name|queryBuilder
operator|.
name|toIndexTimeInMinutes
argument_list|(
operator|(
name|Timestamp
operator|)
name|value
argument_list|)
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
name|name
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|Object
name|value
range|:
name|values
operator|.
name|getValues
argument_list|()
control|)
block|{
name|doc
operator|.
name|addField
argument_list|(
name|name
argument_list|,
operator|(
operator|(
name|Timestamp
operator|)
name|value
operator|)
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|FieldType
operator|.
name|EXACT
operator|||
name|type
operator|==
name|FieldType
operator|.
name|PREFIX
operator|||
name|type
operator|==
name|FieldType
operator|.
name|FULL_TEXT
condition|)
block|{
for|for
control|(
name|Object
name|value
range|:
name|values
operator|.
name|getValues
argument_list|()
control|)
block|{
name|doc
operator|.
name|addField
argument_list|(
name|name
argument_list|,
operator|(
name|String
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
name|QueryBuilder
operator|.
name|badFieldType
argument_list|(
name|type
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|markReady (boolean ready)
specifier|public
name|void
name|markReady
parameter_list|(
name|boolean
name|ready
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO Move the schema version information to a special meta-document
name|FileBasedConfig
name|cfg
init|=
operator|new
name|FileBasedConfig
argument_list|(
name|solrIndexConfig
argument_list|(
name|sitePaths
argument_list|)
argument_list|,
name|FS
operator|.
name|detect
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|e
range|:
name|SCHEMA_VERSIONS
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|cfg
operator|.
name|setInt
argument_list|(
literal|"index"
argument_list|,
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"schemaVersion"
argument_list|,
name|ready
condition|?
name|e
operator|.
name|getValue
argument_list|()
else|:
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|cfg
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

