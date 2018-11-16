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
DECL|package|com.google.gerrit.server
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
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
name|annotations
operator|.
name|VisibleForTesting
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
name|gerrit
operator|.
name|metrics
operator|.
name|Description
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
name|metrics
operator|.
name|Description
operator|.
name|Units
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
name|metrics
operator|.
name|Field
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
name|metrics
operator|.
name|MetricMaker
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
name|metrics
operator|.
name|Timer2
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
name|AllProjectsName
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
name|AllUsersName
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
name|extensions
operator|.
name|events
operator|.
name|GitReferenceUpdated
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
name|git
operator|.
name|GitRepositoryManager
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
name|notedb
operator|.
name|RepoSequence
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
name|Singleton
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

begin_class
annotation|@
name|Singleton
DECL|class|Sequences
specifier|public
class|class
name|Sequences
block|{
DECL|field|NAME_ACCOUNTS
specifier|public
specifier|static
specifier|final
name|String
name|NAME_ACCOUNTS
init|=
literal|"accounts"
decl_stmt|;
DECL|field|NAME_GROUPS
specifier|public
specifier|static
specifier|final
name|String
name|NAME_GROUPS
init|=
literal|"groups"
decl_stmt|;
DECL|field|NAME_CHANGES
specifier|public
specifier|static
specifier|final
name|String
name|NAME_CHANGES
init|=
literal|"changes"
decl_stmt|;
DECL|method|getChangeSequenceGap (Config cfg)
specifier|public
specifier|static
name|int
name|getChangeSequenceGap
parameter_list|(
name|Config
name|cfg
parameter_list|)
block|{
return|return
name|cfg
operator|.
name|getInt
argument_list|(
literal|"noteDb"
argument_list|,
literal|"changes"
argument_list|,
literal|"initialSequenceGap"
argument_list|,
literal|1000
argument_list|)
return|;
block|}
DECL|enum|SequenceType
specifier|private
enum|enum
name|SequenceType
block|{
DECL|enumConstant|ACCOUNTS
name|ACCOUNTS
block|,
DECL|enumConstant|CHANGES
name|CHANGES
block|,
DECL|enumConstant|GROUPS
name|GROUPS
block|;   }
DECL|field|accountSeq
specifier|private
specifier|final
name|RepoSequence
name|accountSeq
decl_stmt|;
DECL|field|changeSeq
specifier|private
specifier|final
name|RepoSequence
name|changeSeq
decl_stmt|;
DECL|field|groupSeq
specifier|private
specifier|final
name|RepoSequence
name|groupSeq
decl_stmt|;
DECL|field|nextIdLatency
specifier|private
specifier|final
name|Timer2
argument_list|<
name|SequenceType
argument_list|,
name|Boolean
argument_list|>
name|nextIdLatency
decl_stmt|;
annotation|@
name|Inject
DECL|method|Sequences ( @erritServerConfig Config cfg, GitRepositoryManager repoManager, GitReferenceUpdated gitRefUpdated, AllProjectsName allProjects, AllUsersName allUsers, MetricMaker metrics)
specifier|public
name|Sequences
parameter_list|(
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|,
name|GitRepositoryManager
name|repoManager
parameter_list|,
name|GitReferenceUpdated
name|gitRefUpdated
parameter_list|,
name|AllProjectsName
name|allProjects
parameter_list|,
name|AllUsersName
name|allUsers
parameter_list|,
name|MetricMaker
name|metrics
parameter_list|)
block|{
name|int
name|accountBatchSize
init|=
name|cfg
operator|.
name|getInt
argument_list|(
literal|"noteDb"
argument_list|,
literal|"accounts"
argument_list|,
literal|"sequenceBatchSize"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|accountSeq
operator|=
operator|new
name|RepoSequence
argument_list|(
name|repoManager
argument_list|,
name|gitRefUpdated
argument_list|,
name|allUsers
argument_list|,
name|NAME_ACCOUNTS
argument_list|,
parameter_list|()
lambda|->
name|ReviewDb
operator|.
name|FIRST_ACCOUNT_ID
argument_list|,
name|accountBatchSize
argument_list|)
expr_stmt|;
name|int
name|changeBatchSize
init|=
name|cfg
operator|.
name|getInt
argument_list|(
literal|"noteDb"
argument_list|,
literal|"changes"
argument_list|,
literal|"sequenceBatchSize"
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|changeSeq
operator|=
operator|new
name|RepoSequence
argument_list|(
name|repoManager
argument_list|,
name|gitRefUpdated
argument_list|,
name|allProjects
argument_list|,
name|NAME_CHANGES
argument_list|,
parameter_list|()
lambda|->
name|ReviewDb
operator|.
name|FIRST_CHANGE_ID
argument_list|,
name|changeBatchSize
argument_list|)
expr_stmt|;
name|int
name|groupBatchSize
init|=
literal|1
decl_stmt|;
name|groupSeq
operator|=
operator|new
name|RepoSequence
argument_list|(
name|repoManager
argument_list|,
name|gitRefUpdated
argument_list|,
name|allUsers
argument_list|,
name|NAME_GROUPS
argument_list|,
parameter_list|()
lambda|->
name|ReviewDb
operator|.
name|FIRST_GROUP_ID
argument_list|,
name|groupBatchSize
argument_list|)
expr_stmt|;
name|nextIdLatency
operator|=
name|metrics
operator|.
name|newTimer
argument_list|(
literal|"sequence/next_id_latency"
argument_list|,
operator|new
name|Description
argument_list|(
literal|"Latency of requesting IDs from repo sequences"
argument_list|)
operator|.
name|setCumulative
argument_list|()
operator|.
name|setUnit
argument_list|(
name|Units
operator|.
name|MILLISECONDS
argument_list|)
argument_list|,
name|Field
operator|.
name|ofEnum
argument_list|(
name|SequenceType
operator|.
name|class
argument_list|,
literal|"sequence"
argument_list|)
argument_list|,
name|Field
operator|.
name|ofBoolean
argument_list|(
literal|"multiple"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|nextAccountId ()
specifier|public
name|int
name|nextAccountId
parameter_list|()
throws|throws
name|OrmException
block|{
try|try
init|(
name|Timer2
operator|.
name|Context
name|timer
init|=
name|nextIdLatency
operator|.
name|start
argument_list|(
name|SequenceType
operator|.
name|ACCOUNTS
argument_list|,
literal|false
argument_list|)
init|)
block|{
return|return
name|accountSeq
operator|.
name|next
argument_list|()
return|;
block|}
block|}
DECL|method|nextChangeId ()
specifier|public
name|int
name|nextChangeId
parameter_list|()
throws|throws
name|OrmException
block|{
try|try
init|(
name|Timer2
operator|.
name|Context
name|timer
init|=
name|nextIdLatency
operator|.
name|start
argument_list|(
name|SequenceType
operator|.
name|CHANGES
argument_list|,
literal|false
argument_list|)
init|)
block|{
return|return
name|changeSeq
operator|.
name|next
argument_list|()
return|;
block|}
block|}
DECL|method|nextChangeIds (int count)
specifier|public
name|ImmutableList
argument_list|<
name|Integer
argument_list|>
name|nextChangeIds
parameter_list|(
name|int
name|count
parameter_list|)
throws|throws
name|OrmException
block|{
try|try
init|(
name|Timer2
operator|.
name|Context
name|timer
init|=
name|nextIdLatency
operator|.
name|start
argument_list|(
name|SequenceType
operator|.
name|CHANGES
argument_list|,
name|count
operator|>
literal|1
argument_list|)
init|)
block|{
return|return
name|changeSeq
operator|.
name|next
argument_list|(
name|count
argument_list|)
return|;
block|}
block|}
DECL|method|nextGroupId ()
specifier|public
name|int
name|nextGroupId
parameter_list|()
throws|throws
name|OrmException
block|{
try|try
init|(
name|Timer2
operator|.
name|Context
name|timer
init|=
name|nextIdLatency
operator|.
name|start
argument_list|(
name|SequenceType
operator|.
name|GROUPS
argument_list|,
literal|false
argument_list|)
init|)
block|{
return|return
name|groupSeq
operator|.
name|next
argument_list|()
return|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|getChangeIdRepoSequence ()
specifier|public
name|RepoSequence
name|getChangeIdRepoSequence
parameter_list|()
block|{
return|return
name|changeSeq
return|;
block|}
block|}
end_class

end_unit

