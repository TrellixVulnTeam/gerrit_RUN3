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
name|Objects
operator|.
name|firstNonNull
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
name|Maps
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
name|restapi
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
name|extensions
operator|.
name|restapi
operator|.
name|BadRequestException
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
name|restapi
operator|.
name|ResourceConflictException
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
name|restapi
operator|.
name|RestReadView
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
name|PatchLineComment
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
name|change
operator|.
name|GetDraft
operator|.
name|Comment
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
name|change
operator|.
name|GetDraft
operator|.
name|Side
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
name|Comparator
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

begin_class
DECL|class|ListDrafts
class|class
name|ListDrafts
implements|implements
name|RestReadView
argument_list|<
name|RevisionResource
argument_list|>
block|{
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
DECL|method|ListDrafts (Provider<ReviewDb> db)
name|ListDrafts
parameter_list|(
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|)
block|{
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (RevisionResource rsrc)
specifier|public
name|Object
name|apply
parameter_list|(
name|RevisionResource
name|rsrc
parameter_list|)
throws|throws
name|AuthException
throws|,
name|BadRequestException
throws|,
name|ResourceConflictException
throws|,
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Comment
argument_list|>
argument_list|>
name|out
init|=
name|Maps
operator|.
name|newTreeMap
argument_list|()
decl_stmt|;
for|for
control|(
name|PatchLineComment
name|c
range|:
name|db
operator|.
name|get
argument_list|()
operator|.
name|patchComments
argument_list|()
operator|.
name|draftByPatchSetAuthor
argument_list|(
name|rsrc
operator|.
name|getPatchSet
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|rsrc
operator|.
name|getAccountId
argument_list|()
argument_list|)
control|)
block|{
name|Comment
name|o
init|=
operator|new
name|Comment
argument_list|(
name|c
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Comment
argument_list|>
name|list
init|=
name|out
operator|.
name|get
argument_list|(
name|o
operator|.
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|==
literal|null
condition|)
block|{
name|list
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|()
expr_stmt|;
name|out
operator|.
name|put
argument_list|(
name|o
operator|.
name|path
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
name|o
operator|.
name|path
operator|=
literal|null
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|List
argument_list|<
name|Comment
argument_list|>
name|list
range|:
name|out
operator|.
name|values
argument_list|()
control|)
block|{
name|Collections
operator|.
name|sort
argument_list|(
name|list
argument_list|,
operator|new
name|Comparator
argument_list|<
name|Comment
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Comment
name|a
parameter_list|,
name|Comment
name|b
parameter_list|)
block|{
name|int
name|c
init|=
name|firstNonNull
argument_list|(
name|a
operator|.
name|side
argument_list|,
name|Side
operator|.
name|REVISION
argument_list|)
operator|.
name|ordinal
argument_list|()
operator|-
name|firstNonNull
argument_list|(
name|b
operator|.
name|side
argument_list|,
name|Side
operator|.
name|REVISION
argument_list|)
operator|.
name|ordinal
argument_list|()
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|0
condition|)
block|{
name|c
operator|=
name|firstNonNull
argument_list|(
name|a
operator|.
name|line
argument_list|,
literal|0
argument_list|)
operator|-
name|firstNonNull
argument_list|(
name|b
operator|.
name|line
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|c
operator|==
literal|0
condition|)
block|{
name|c
operator|=
name|a
operator|.
name|id
operator|.
name|compareTo
argument_list|(
name|b
operator|.
name|id
argument_list|)
expr_stmt|;
block|}
return|return
name|c
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
return|return
name|out
return|;
block|}
block|}
end_class

end_unit

