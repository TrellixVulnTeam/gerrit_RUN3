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
name|checkArgument
import|;
end_import

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
name|checkState
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
name|ioutil
operator|.
name|BasicSerialization
operator|.
name|readString
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
name|ioutil
operator|.
name|BasicSerialization
operator|.
name|writeString
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|ObjectIdSerialization
operator|.
name|readNotNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|ObjectIdSerialization
operator|.
name|writeNotNull
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
name|MoreObjects
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
name|common
operator|.
name|cache
operator|.
name|Weigher
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
name|BiMap
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
name|ImmutableBiMap
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
name|common
operator|.
name|SubmitType
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
name|Branch
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
name|cache
operator|.
name|CacheModule
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
name|CodeReviewCommit
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
name|MergeException
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
name|strategy
operator|.
name|SubmitStrategyFactory
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
name|project
operator|.
name|NoSuchProjectException
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
name|Module
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|name
operator|.
name|Named
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
name|errors
operator|.
name|IncorrectObjectTypeException
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
name|errors
operator|.
name|MissingObjectException
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
name|Constants
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
name|ObjectId
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
name|Ref
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
name|Repository
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
name|revwalk
operator|.
name|RevCommit
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
name|revwalk
operator|.
name|RevFlag
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
name|revwalk
operator|.
name|RevWalk
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
name|ObjectInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|Objects
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
annotation|@
name|Singleton
DECL|class|MergeabilityCacheImpl
specifier|public
class|class
name|MergeabilityCacheImpl
implements|implements
name|MergeabilityCache
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
name|MergeabilityCacheImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|CACHE_NAME
specifier|private
specifier|static
specifier|final
name|String
name|CACHE_NAME
init|=
literal|"mergeability"
decl_stmt|;
DECL|field|SUBMIT_TYPES
specifier|public
specifier|static
specifier|final
name|BiMap
argument_list|<
name|SubmitType
argument_list|,
name|Character
argument_list|>
name|SUBMIT_TYPES
init|=
name|ImmutableBiMap
operator|.
name|of
argument_list|(
name|SubmitType
operator|.
name|FAST_FORWARD_ONLY
argument_list|,
literal|'F'
argument_list|,
name|SubmitType
operator|.
name|MERGE_IF_NECESSARY
argument_list|,
literal|'M'
argument_list|,
name|SubmitType
operator|.
name|REBASE_IF_NECESSARY
argument_list|,
literal|'R'
argument_list|,
name|SubmitType
operator|.
name|MERGE_ALWAYS
argument_list|,
literal|'A'
argument_list|,
name|SubmitType
operator|.
name|CHERRY_PICK
argument_list|,
literal|'C'
argument_list|)
decl_stmt|;
static|static
block|{
name|checkState
argument_list|(
name|SUBMIT_TYPES
operator|.
name|size
argument_list|()
operator|==
name|SubmitType
operator|.
name|values
argument_list|()
operator|.
name|length
argument_list|,
literal|"SubmitType<-> char BiMap needs updating"
argument_list|)
expr_stmt|;
block|}
DECL|method|module ()
specifier|public
specifier|static
name|Module
name|module
parameter_list|()
block|{
return|return
operator|new
name|CacheModule
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|persist
argument_list|(
name|CACHE_NAME
argument_list|,
name|EntryKey
operator|.
name|class
argument_list|,
name|Boolean
operator|.
name|class
argument_list|)
operator|.
name|maximumWeight
argument_list|(
literal|1
operator|<<
literal|20
argument_list|)
operator|.
name|weigher
argument_list|(
name|MergeabilityWeigher
operator|.
name|class
argument_list|)
operator|.
name|loader
argument_list|(
name|Loader
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|MergeabilityCache
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|MergeabilityCacheImpl
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
DECL|method|toId (Ref ref)
specifier|public
specifier|static
name|ObjectId
name|toId
parameter_list|(
name|Ref
name|ref
parameter_list|)
block|{
return|return
name|ref
operator|!=
literal|null
operator|&&
name|ref
operator|.
name|getObjectId
argument_list|()
operator|!=
literal|null
condition|?
name|ref
operator|.
name|getObjectId
argument_list|()
else|:
name|ObjectId
operator|.
name|zeroId
argument_list|()
return|;
block|}
DECL|class|EntryKey
specifier|public
specifier|static
class|class
name|EntryKey
implements|implements
name|Serializable
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|field|commit
specifier|private
name|ObjectId
name|commit
decl_stmt|;
DECL|field|into
specifier|private
name|ObjectId
name|into
decl_stmt|;
DECL|field|submitType
specifier|private
name|SubmitType
name|submitType
decl_stmt|;
DECL|field|mergeStrategy
specifier|private
name|String
name|mergeStrategy
decl_stmt|;
comment|// Only used for loading, not stored.
DECL|field|load
specifier|private
specifier|transient
name|LoadHelper
name|load
decl_stmt|;
DECL|method|EntryKey (ObjectId commit, ObjectId into, SubmitType submitType, String mergeStrategy)
specifier|public
name|EntryKey
parameter_list|(
name|ObjectId
name|commit
parameter_list|,
name|ObjectId
name|into
parameter_list|,
name|SubmitType
name|submitType
parameter_list|,
name|String
name|mergeStrategy
parameter_list|)
block|{
name|this
operator|.
name|commit
operator|=
name|checkNotNull
argument_list|(
name|commit
argument_list|,
literal|"commit"
argument_list|)
expr_stmt|;
name|this
operator|.
name|into
operator|=
name|checkNotNull
argument_list|(
name|into
argument_list|,
literal|"into"
argument_list|)
expr_stmt|;
name|this
operator|.
name|submitType
operator|=
name|checkNotNull
argument_list|(
name|submitType
argument_list|,
literal|"submitType"
argument_list|)
expr_stmt|;
name|this
operator|.
name|mergeStrategy
operator|=
name|checkNotNull
argument_list|(
name|mergeStrategy
argument_list|,
literal|"mergeStrategy"
argument_list|)
expr_stmt|;
block|}
DECL|method|EntryKey (ObjectId commit, ObjectId into, SubmitType submitType, String mergeStrategy, Branch.NameKey dest, Repository repo, ReviewDb db)
specifier|private
name|EntryKey
parameter_list|(
name|ObjectId
name|commit
parameter_list|,
name|ObjectId
name|into
parameter_list|,
name|SubmitType
name|submitType
parameter_list|,
name|String
name|mergeStrategy
parameter_list|,
name|Branch
operator|.
name|NameKey
name|dest
parameter_list|,
name|Repository
name|repo
parameter_list|,
name|ReviewDb
name|db
parameter_list|)
block|{
name|this
argument_list|(
name|commit
argument_list|,
name|into
argument_list|,
name|submitType
argument_list|,
name|mergeStrategy
argument_list|)
expr_stmt|;
name|load
operator|=
operator|new
name|LoadHelper
argument_list|(
name|dest
argument_list|,
name|repo
argument_list|,
name|db
argument_list|)
expr_stmt|;
block|}
DECL|method|getCommit ()
specifier|public
name|ObjectId
name|getCommit
parameter_list|()
block|{
return|return
name|commit
return|;
block|}
DECL|method|getInto ()
specifier|public
name|ObjectId
name|getInto
parameter_list|()
block|{
return|return
name|into
return|;
block|}
DECL|method|getSubmitType ()
specifier|public
name|SubmitType
name|getSubmitType
parameter_list|()
block|{
return|return
name|submitType
return|;
block|}
DECL|method|getMergeStrategy ()
specifier|public
name|String
name|getMergeStrategy
parameter_list|()
block|{
return|return
name|mergeStrategy
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|EntryKey
condition|)
block|{
name|EntryKey
name|k
init|=
operator|(
name|EntryKey
operator|)
name|o
decl_stmt|;
return|return
name|commit
operator|.
name|equals
argument_list|(
name|k
operator|.
name|commit
argument_list|)
operator|&&
name|into
operator|.
name|equals
argument_list|(
name|k
operator|.
name|into
argument_list|)
operator|&&
name|submitType
operator|==
name|k
operator|.
name|submitType
operator|&&
name|mergeStrategy
operator|.
name|equals
argument_list|(
name|k
operator|.
name|mergeStrategy
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|commit
argument_list|,
name|into
argument_list|,
name|submitType
argument_list|,
name|mergeStrategy
argument_list|)
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
name|MoreObjects
operator|.
name|toStringHelper
argument_list|(
name|this
argument_list|)
operator|.
name|add
argument_list|(
literal|"commit"
argument_list|,
name|commit
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
literal|"into"
argument_list|,
name|into
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|addValue
argument_list|(
name|submitType
argument_list|)
operator|.
name|addValue
argument_list|(
name|mergeStrategy
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|writeObject (ObjectOutputStream out)
specifier|private
name|void
name|writeObject
parameter_list|(
name|ObjectOutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|writeNotNull
argument_list|(
name|out
argument_list|,
name|commit
argument_list|)
expr_stmt|;
name|writeNotNull
argument_list|(
name|out
argument_list|,
name|into
argument_list|)
expr_stmt|;
name|Character
name|c
init|=
name|SUBMIT_TYPES
operator|.
name|get
argument_list|(
name|submitType
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid submit type: "
operator|+
name|submitType
argument_list|)
throw|;
block|}
name|out
operator|.
name|writeChar
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|writeString
argument_list|(
name|out
argument_list|,
name|mergeStrategy
argument_list|)
expr_stmt|;
block|}
DECL|method|readObject (ObjectInputStream in)
specifier|private
name|void
name|readObject
parameter_list|(
name|ObjectInputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|commit
operator|=
name|readNotNull
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|into
operator|=
name|readNotNull
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|char
name|t
init|=
name|in
operator|.
name|readChar
argument_list|()
decl_stmt|;
name|submitType
operator|=
name|SUBMIT_TYPES
operator|.
name|inverse
argument_list|()
operator|.
name|get
argument_list|(
name|t
argument_list|)
expr_stmt|;
if|if
condition|(
name|submitType
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid submit type code: "
operator|+
name|t
argument_list|)
throw|;
block|}
name|mergeStrategy
operator|=
name|readString
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|LoadHelper
specifier|private
specifier|static
class|class
name|LoadHelper
block|{
DECL|field|dest
specifier|private
specifier|final
name|Branch
operator|.
name|NameKey
name|dest
decl_stmt|;
DECL|field|repo
specifier|private
specifier|final
name|Repository
name|repo
decl_stmt|;
DECL|field|db
specifier|private
specifier|final
name|ReviewDb
name|db
decl_stmt|;
DECL|method|LoadHelper (Branch.NameKey dest, Repository repo, ReviewDb db)
specifier|private
name|LoadHelper
parameter_list|(
name|Branch
operator|.
name|NameKey
name|dest
parameter_list|,
name|Repository
name|repo
parameter_list|,
name|ReviewDb
name|db
parameter_list|)
block|{
name|this
operator|.
name|dest
operator|=
name|checkNotNull
argument_list|(
name|dest
argument_list|,
literal|"dest"
argument_list|)
expr_stmt|;
name|this
operator|.
name|repo
operator|=
name|checkNotNull
argument_list|(
name|repo
argument_list|,
literal|"repo"
argument_list|)
expr_stmt|;
name|this
operator|.
name|db
operator|=
name|checkNotNull
argument_list|(
name|db
argument_list|,
literal|"db"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Singleton
DECL|class|Loader
specifier|public
specifier|static
class|class
name|Loader
extends|extends
name|CacheLoader
argument_list|<
name|EntryKey
argument_list|,
name|Boolean
argument_list|>
block|{
DECL|field|submitStrategyFactory
specifier|private
specifier|final
name|SubmitStrategyFactory
name|submitStrategyFactory
decl_stmt|;
annotation|@
name|Inject
DECL|method|Loader (SubmitStrategyFactory submitStrategyFactory)
name|Loader
parameter_list|(
name|SubmitStrategyFactory
name|submitStrategyFactory
parameter_list|)
block|{
name|this
operator|.
name|submitStrategyFactory
operator|=
name|submitStrategyFactory
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|load (EntryKey key)
specifier|public
name|Boolean
name|load
parameter_list|(
name|EntryKey
name|key
parameter_list|)
throws|throws
name|NoSuchProjectException
throws|,
name|MergeException
throws|,
name|IOException
block|{
name|checkArgument
argument_list|(
name|key
operator|.
name|load
operator|!=
literal|null
argument_list|,
literal|"Key cannot be loaded: %s"
argument_list|,
name|key
argument_list|)
expr_stmt|;
if|if
condition|(
name|key
operator|.
name|into
operator|.
name|equals
argument_list|(
name|ObjectId
operator|.
name|zeroId
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
comment|// Assume yes on new branch.
block|}
try|try
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Ref
argument_list|>
name|refs
init|=
name|key
operator|.
name|load
operator|.
name|repo
operator|.
name|getAllRefs
argument_list|()
decl_stmt|;
name|RevWalk
name|rw
init|=
name|CodeReviewCommit
operator|.
name|newRevWalk
argument_list|(
name|key
operator|.
name|load
operator|.
name|repo
argument_list|)
decl_stmt|;
try|try
block|{
name|RevFlag
name|canMerge
init|=
name|rw
operator|.
name|newFlag
argument_list|(
literal|"CAN_MERGE"
argument_list|)
decl_stmt|;
name|CodeReviewCommit
name|rev
init|=
name|parse
argument_list|(
name|rw
argument_list|,
name|key
operator|.
name|commit
argument_list|)
decl_stmt|;
name|rev
operator|.
name|add
argument_list|(
name|canMerge
argument_list|)
expr_stmt|;
name|CodeReviewCommit
name|tip
init|=
name|parse
argument_list|(
name|rw
argument_list|,
name|key
operator|.
name|into
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|RevCommit
argument_list|>
name|accepted
init|=
name|alreadyAccepted
argument_list|(
name|rw
argument_list|,
name|refs
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
name|accepted
operator|.
name|add
argument_list|(
name|tip
argument_list|)
expr_stmt|;
name|accepted
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|rev
operator|.
name|getParents
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|submitStrategyFactory
operator|.
name|create
argument_list|(
name|key
operator|.
name|submitType
argument_list|,
name|key
operator|.
name|load
operator|.
name|db
argument_list|,
name|key
operator|.
name|load
operator|.
name|repo
argument_list|,
name|rw
argument_list|,
literal|null
comment|/*inserter*/
argument_list|,
name|canMerge
argument_list|,
name|accepted
argument_list|,
name|key
operator|.
name|load
operator|.
name|dest
argument_list|)
operator|.
name|dryRun
argument_list|(
name|tip
argument_list|,
name|rev
argument_list|)
return|;
block|}
finally|finally
block|{
name|rw
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|key
operator|.
name|load
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|alreadyAccepted (RevWalk rw, Collection<Ref> refs)
specifier|private
specifier|static
name|Set
argument_list|<
name|RevCommit
argument_list|>
name|alreadyAccepted
parameter_list|(
name|RevWalk
name|rw
parameter_list|,
name|Collection
argument_list|<
name|Ref
argument_list|>
name|refs
parameter_list|)
throws|throws
name|MissingObjectException
throws|,
name|IOException
block|{
name|Set
argument_list|<
name|RevCommit
argument_list|>
name|accepted
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|Ref
name|r
range|:
name|refs
control|)
block|{
if|if
condition|(
name|r
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|Constants
operator|.
name|R_HEADS
argument_list|)
operator|||
name|r
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|Constants
operator|.
name|R_TAGS
argument_list|)
condition|)
block|{
try|try
block|{
name|accepted
operator|.
name|add
argument_list|(
name|rw
operator|.
name|parseCommit
argument_list|(
name|r
operator|.
name|getObjectId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IncorrectObjectTypeException
name|nonCommit
parameter_list|)
block|{
comment|// Not a commit? Skip over it.
block|}
block|}
block|}
return|return
name|accepted
return|;
block|}
DECL|method|parse (RevWalk rw, ObjectId id)
specifier|private
specifier|static
name|CodeReviewCommit
name|parse
parameter_list|(
name|RevWalk
name|rw
parameter_list|,
name|ObjectId
name|id
parameter_list|)
throws|throws
name|MissingObjectException
throws|,
name|IncorrectObjectTypeException
throws|,
name|IOException
block|{
return|return
operator|(
name|CodeReviewCommit
operator|)
name|rw
operator|.
name|parseCommit
argument_list|(
name|id
argument_list|)
return|;
block|}
block|}
DECL|class|MergeabilityWeigher
specifier|public
specifier|static
class|class
name|MergeabilityWeigher
implements|implements
name|Weigher
argument_list|<
name|EntryKey
argument_list|,
name|Boolean
argument_list|>
block|{
annotation|@
name|Override
DECL|method|weigh (EntryKey k, Boolean v)
specifier|public
name|int
name|weigh
parameter_list|(
name|EntryKey
name|k
parameter_list|,
name|Boolean
name|v
parameter_list|)
block|{
return|return
literal|16
operator|+
literal|2
operator|*
operator|(
literal|16
operator|+
literal|20
operator|)
operator|+
literal|3
operator|*
literal|8
comment|// Size of EntryKey, 64-bit JVM.
operator|+
literal|8
return|;
comment|// Size of Boolean.
block|}
block|}
DECL|field|cache
specifier|private
specifier|final
name|LoadingCache
argument_list|<
name|EntryKey
argument_list|,
name|Boolean
argument_list|>
name|cache
decl_stmt|;
annotation|@
name|Inject
DECL|method|MergeabilityCacheImpl (@amedCACHE_NAME) LoadingCache<EntryKey, Boolean> cache)
name|MergeabilityCacheImpl
parameter_list|(
annotation|@
name|Named
argument_list|(
name|CACHE_NAME
argument_list|)
name|LoadingCache
argument_list|<
name|EntryKey
argument_list|,
name|Boolean
argument_list|>
name|cache
parameter_list|)
block|{
name|this
operator|.
name|cache
operator|=
name|cache
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get (ObjectId commit, Ref intoRef, SubmitType submitType, String mergeStrategy, Branch.NameKey dest, Repository repo, ReviewDb db)
specifier|public
name|boolean
name|get
parameter_list|(
name|ObjectId
name|commit
parameter_list|,
name|Ref
name|intoRef
parameter_list|,
name|SubmitType
name|submitType
parameter_list|,
name|String
name|mergeStrategy
parameter_list|,
name|Branch
operator|.
name|NameKey
name|dest
parameter_list|,
name|Repository
name|repo
parameter_list|,
name|ReviewDb
name|db
parameter_list|)
block|{
name|ObjectId
name|into
init|=
name|intoRef
operator|!=
literal|null
condition|?
name|intoRef
operator|.
name|getObjectId
argument_list|()
else|:
name|ObjectId
operator|.
name|zeroId
argument_list|()
decl_stmt|;
name|EntryKey
name|key
init|=
operator|new
name|EntryKey
argument_list|(
name|commit
argument_list|,
name|into
argument_list|,
name|submitType
argument_list|,
name|mergeStrategy
argument_list|,
name|dest
argument_list|,
name|repo
argument_list|,
name|db
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|cache
operator|.
name|get
argument_list|(
name|key
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
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Error checking mergeability of %s into %s (%s)"
argument_list|,
name|key
operator|.
name|commit
operator|.
name|name
argument_list|()
argument_list|,
name|key
operator|.
name|into
operator|.
name|name
argument_list|()
argument_list|,
name|key
operator|.
name|submitType
operator|.
name|name
argument_list|()
argument_list|)
argument_list|,
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getIfPresent (ObjectId commit, Ref intoRef, SubmitType submitType, String mergeStrategy)
specifier|public
name|boolean
name|getIfPresent
parameter_list|(
name|ObjectId
name|commit
parameter_list|,
name|Ref
name|intoRef
parameter_list|,
name|SubmitType
name|submitType
parameter_list|,
name|String
name|mergeStrategy
parameter_list|)
block|{
return|return
name|cache
operator|.
name|getIfPresent
argument_list|(
operator|new
name|EntryKey
argument_list|(
name|commit
argument_list|,
name|toId
argument_list|(
name|intoRef
argument_list|)
argument_list|,
name|submitType
argument_list|,
name|mergeStrategy
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

