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
name|CharMatcher
operator|.
name|WHITESPACE
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
name|CharMatcher
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
name|common
operator|.
name|ChangeHooks
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
name|api
operator|.
name|changes
operator|.
name|HashtagsInput
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
name|registration
operator|.
name|DynamicSet
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
name|IdentifiedUser
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
name|auth
operator|.
name|AuthException
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
name|ChangeIndexer
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
name|ChangeNotes
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
name|ChangeUpdate
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
name|ChangeControl
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
name|validators
operator|.
name|HashtagValidationListener
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
name|validators
operator|.
name|ValidationException
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
name|HashSet
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
name|TreeSet
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|HashtagsUtil
specifier|public
class|class
name|HashtagsUtil
block|{
DECL|field|LEADER
specifier|private
specifier|static
specifier|final
name|CharMatcher
name|LEADER
init|=
name|WHITESPACE
operator|.
name|or
argument_list|(
name|CharMatcher
operator|.
name|is
argument_list|(
literal|'#'
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|updateFactory
specifier|private
specifier|final
name|ChangeUpdate
operator|.
name|Factory
name|updateFactory
decl_stmt|;
DECL|field|dbProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|dbProvider
decl_stmt|;
DECL|field|indexer
specifier|private
specifier|final
name|ChangeIndexer
name|indexer
decl_stmt|;
DECL|field|hooks
specifier|private
specifier|final
name|ChangeHooks
name|hooks
decl_stmt|;
DECL|field|hashtagValidationListeners
specifier|private
specifier|final
name|DynamicSet
argument_list|<
name|HashtagValidationListener
argument_list|>
name|hashtagValidationListeners
decl_stmt|;
annotation|@
name|Inject
DECL|method|HashtagsUtil (ChangeUpdate.Factory updateFactory, Provider<ReviewDb> dbProvider, ChangeIndexer indexer, ChangeHooks hooks, DynamicSet<HashtagValidationListener> hashtagValidationListeners)
name|HashtagsUtil
parameter_list|(
name|ChangeUpdate
operator|.
name|Factory
name|updateFactory
parameter_list|,
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|dbProvider
parameter_list|,
name|ChangeIndexer
name|indexer
parameter_list|,
name|ChangeHooks
name|hooks
parameter_list|,
name|DynamicSet
argument_list|<
name|HashtagValidationListener
argument_list|>
name|hashtagValidationListeners
parameter_list|)
block|{
name|this
operator|.
name|updateFactory
operator|=
name|updateFactory
expr_stmt|;
name|this
operator|.
name|dbProvider
operator|=
name|dbProvider
expr_stmt|;
name|this
operator|.
name|indexer
operator|=
name|indexer
expr_stmt|;
name|this
operator|.
name|hooks
operator|=
name|hooks
expr_stmt|;
name|this
operator|.
name|hashtagValidationListeners
operator|=
name|hashtagValidationListeners
expr_stmt|;
block|}
DECL|method|cleanupHashtag (String hashtag)
specifier|public
specifier|static
name|String
name|cleanupHashtag
parameter_list|(
name|String
name|hashtag
parameter_list|)
block|{
name|hashtag
operator|=
name|LEADER
operator|.
name|trimLeadingFrom
argument_list|(
name|hashtag
argument_list|)
expr_stmt|;
name|hashtag
operator|=
name|WHITESPACE
operator|.
name|trimTrailingFrom
argument_list|(
name|hashtag
argument_list|)
expr_stmt|;
return|return
name|hashtag
operator|.
name|toLowerCase
argument_list|()
return|;
block|}
DECL|method|extractTags (Set<String> input)
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|extractTags
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|input
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
if|if
condition|(
name|input
operator|==
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
else|else
block|{
name|HashSet
argument_list|<
name|String
argument_list|>
name|result
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|hashtag
range|:
name|input
control|)
block|{
if|if
condition|(
name|hashtag
operator|.
name|contains
argument_list|(
literal|","
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Hashtags may not contain commas"
argument_list|)
throw|;
block|}
name|hashtag
operator|=
name|cleanupHashtag
argument_list|(
name|hashtag
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|hashtag
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|hashtag
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
block|}
DECL|method|setHashtags (ChangeControl control, HashtagsInput input, boolean runHooks, boolean index)
specifier|public
name|TreeSet
argument_list|<
name|String
argument_list|>
name|setHashtags
parameter_list|(
name|ChangeControl
name|control
parameter_list|,
name|HashtagsInput
name|input
parameter_list|,
name|boolean
name|runHooks
parameter_list|,
name|boolean
name|index
parameter_list|)
throws|throws
name|IllegalArgumentException
throws|,
name|IOException
throws|,
name|ValidationException
throws|,
name|AuthException
throws|,
name|OrmException
block|{
if|if
condition|(
name|input
operator|==
literal|null
operator|||
operator|(
name|input
operator|.
name|add
operator|==
literal|null
operator|&&
name|input
operator|.
name|remove
operator|==
literal|null
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Hashtags are required"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|control
operator|.
name|canEditHashtags
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AuthException
argument_list|(
literal|"Editing hashtags not permitted"
argument_list|)
throw|;
block|}
name|ChangeUpdate
name|update
init|=
name|updateFactory
operator|.
name|create
argument_list|(
name|control
argument_list|)
decl_stmt|;
name|ChangeNotes
name|notes
init|=
name|control
operator|.
name|getNotes
argument_list|()
operator|.
name|load
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|existingHashtags
init|=
name|notes
operator|.
name|getHashtags
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|updatedHashtags
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|toAdd
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|extractTags
argument_list|(
name|input
operator|.
name|add
argument_list|)
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|toRemove
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|extractTags
argument_list|(
name|input
operator|.
name|remove
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|HashtagValidationListener
name|validator
range|:
name|hashtagValidationListeners
control|)
block|{
name|validator
operator|.
name|validateHashtags
argument_list|(
name|update
operator|.
name|getChange
argument_list|()
argument_list|,
name|toAdd
argument_list|,
name|toRemove
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|existingHashtags
operator|!=
literal|null
operator|&&
operator|!
name|existingHashtags
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|updatedHashtags
operator|.
name|addAll
argument_list|(
name|existingHashtags
argument_list|)
expr_stmt|;
name|toAdd
operator|.
name|removeAll
argument_list|(
name|existingHashtags
argument_list|)
expr_stmt|;
name|toRemove
operator|.
name|retainAll
argument_list|(
name|existingHashtags
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|toAdd
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|||
name|toRemove
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|updatedHashtags
operator|.
name|addAll
argument_list|(
name|toAdd
argument_list|)
expr_stmt|;
name|updatedHashtags
operator|.
name|removeAll
argument_list|(
name|toRemove
argument_list|)
expr_stmt|;
name|update
operator|.
name|setHashtags
argument_list|(
name|updatedHashtags
argument_list|)
expr_stmt|;
name|update
operator|.
name|commit
argument_list|()
expr_stmt|;
if|if
condition|(
name|index
condition|)
block|{
name|indexer
operator|.
name|index
argument_list|(
name|dbProvider
operator|.
name|get
argument_list|()
argument_list|,
name|update
operator|.
name|getChange
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|runHooks
condition|)
block|{
name|IdentifiedUser
name|currentUser
init|=
operator|(
operator|(
name|IdentifiedUser
operator|)
name|control
operator|.
name|getCurrentUser
argument_list|()
operator|)
decl_stmt|;
name|hooks
operator|.
name|doHashtagsChangedHook
argument_list|(
name|update
operator|.
name|getChange
argument_list|()
argument_list|,
name|currentUser
operator|.
name|getAccount
argument_list|()
argument_list|,
name|toAdd
argument_list|,
name|toRemove
argument_list|,
name|updatedHashtags
argument_list|,
name|dbProvider
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|TreeSet
argument_list|<>
argument_list|(
name|updatedHashtags
argument_list|)
return|;
block|}
block|}
end_class

end_unit

