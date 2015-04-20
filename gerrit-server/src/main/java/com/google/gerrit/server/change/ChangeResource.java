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
name|hash
operator|.
name|Hasher
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
name|hash
operator|.
name|Hashing
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
name|RestResource
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
name|RestResource
operator|.
name|HasETag
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
name|RestView
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
name|server
operator|.
name|CurrentUser
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
name|changedetail
operator|.
name|RebaseChange
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
name|project
operator|.
name|ProjectState
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
name|TypeLiteral
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

begin_class
DECL|class|ChangeResource
specifier|public
class|class
name|ChangeResource
implements|implements
name|RestResource
implements|,
name|HasETag
block|{
DECL|field|CHANGE_KIND
specifier|public
specifier|static
specifier|final
name|TypeLiteral
argument_list|<
name|RestView
argument_list|<
name|ChangeResource
argument_list|>
argument_list|>
name|CHANGE_KIND
init|=
operator|new
name|TypeLiteral
argument_list|<
name|RestView
argument_list|<
name|ChangeResource
argument_list|>
argument_list|>
argument_list|()
block|{}
decl_stmt|;
DECL|field|control
specifier|private
specifier|final
name|ChangeControl
name|control
decl_stmt|;
DECL|field|rebaseChange
specifier|private
specifier|final
name|RebaseChange
name|rebaseChange
decl_stmt|;
DECL|method|ChangeResource (ChangeControl control, RebaseChange rebaseChange)
specifier|public
name|ChangeResource
parameter_list|(
name|ChangeControl
name|control
parameter_list|,
name|RebaseChange
name|rebaseChange
parameter_list|)
block|{
name|this
operator|.
name|control
operator|=
name|control
expr_stmt|;
name|this
operator|.
name|rebaseChange
operator|=
name|rebaseChange
expr_stmt|;
block|}
DECL|method|ChangeResource (ChangeResource copy)
specifier|protected
name|ChangeResource
parameter_list|(
name|ChangeResource
name|copy
parameter_list|)
block|{
name|this
operator|.
name|control
operator|=
name|copy
operator|.
name|control
expr_stmt|;
name|this
operator|.
name|rebaseChange
operator|=
name|copy
operator|.
name|rebaseChange
expr_stmt|;
block|}
DECL|method|getControl ()
specifier|public
name|ChangeControl
name|getControl
parameter_list|()
block|{
return|return
name|control
return|;
block|}
DECL|method|getChange ()
specifier|public
name|Change
name|getChange
parameter_list|()
block|{
return|return
name|getControl
argument_list|()
operator|.
name|getChange
argument_list|()
return|;
block|}
DECL|method|getNotes ()
specifier|public
name|ChangeNotes
name|getNotes
parameter_list|()
block|{
return|return
name|getControl
argument_list|()
operator|.
name|getNotes
argument_list|()
return|;
block|}
comment|// This includes all information relevant for ETag computation
comment|// unrelated to the UI.
DECL|method|prepareETag (Hasher h, CurrentUser user)
specifier|public
name|void
name|prepareETag
parameter_list|(
name|Hasher
name|h
parameter_list|,
name|CurrentUser
name|user
parameter_list|)
block|{
name|h
operator|.
name|putLong
argument_list|(
name|getChange
argument_list|()
operator|.
name|getLastUpdatedOn
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|)
operator|.
name|putInt
argument_list|(
name|getChange
argument_list|()
operator|.
name|getRowVersion
argument_list|()
argument_list|)
operator|.
name|putInt
argument_list|(
name|user
operator|.
name|isIdentifiedUser
argument_list|()
condition|?
operator|(
operator|(
name|IdentifiedUser
operator|)
name|user
operator|)
operator|.
name|getAccountId
argument_list|()
operator|.
name|get
argument_list|()
else|:
literal|0
argument_list|)
operator|.
name|putBoolean
argument_list|(
name|rebaseChange
operator|!=
literal|null
operator|&&
name|rebaseChange
operator|.
name|canRebase
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|20
index|]
decl_stmt|;
name|ObjectId
name|noteId
decl_stmt|;
try|try
block|{
name|noteId
operator|=
name|getNotes
argument_list|()
operator|.
name|loadRevision
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
name|noteId
operator|=
literal|null
expr_stmt|;
comment|// This ETag will be invalidated if it loads next time.
block|}
name|hashObjectId
argument_list|(
name|h
argument_list|,
name|noteId
argument_list|,
name|buf
argument_list|)
expr_stmt|;
comment|// TODO(dborowitz): Include more notedb and other related refs, e.g. drafts
comment|// and edits.
for|for
control|(
name|ProjectState
name|p
range|:
name|control
operator|.
name|getProjectControl
argument_list|()
operator|.
name|getProjectState
argument_list|()
operator|.
name|tree
argument_list|()
control|)
block|{
name|hashObjectId
argument_list|(
name|h
argument_list|,
name|p
operator|.
name|getConfig
argument_list|()
operator|.
name|getRevision
argument_list|()
argument_list|,
name|buf
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getETag ()
specifier|public
name|String
name|getETag
parameter_list|()
block|{
name|CurrentUser
name|user
init|=
name|control
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
name|Hasher
name|h
init|=
name|Hashing
operator|.
name|md5
argument_list|()
operator|.
name|newHasher
argument_list|()
operator|.
name|putBoolean
argument_list|(
name|user
operator|.
name|getStarredChanges
argument_list|()
operator|.
name|contains
argument_list|(
name|getChange
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|prepareETag
argument_list|(
name|h
argument_list|,
name|user
argument_list|)
expr_stmt|;
return|return
name|h
operator|.
name|hash
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|hashObjectId (Hasher h, ObjectId id, byte[] buf)
specifier|private
name|void
name|hashObjectId
parameter_list|(
name|Hasher
name|h
parameter_list|,
name|ObjectId
name|id
parameter_list|,
name|byte
index|[]
name|buf
parameter_list|)
block|{
name|MoreObjects
operator|.
name|firstNonNull
argument_list|(
name|id
argument_list|,
name|ObjectId
operator|.
name|zeroId
argument_list|()
argument_list|)
operator|.
name|copyRawTo
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|h
operator|.
name|putBytes
argument_list|(
name|buf
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

