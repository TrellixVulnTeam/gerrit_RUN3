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
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Optional
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
name|io
operator|.
name|ByteStreams
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
name|Nullable
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
name|EditInfo
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
name|DynamicMap
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
name|AcceptsCreate
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
name|AcceptsDelete
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
name|AcceptsPost
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
name|ChildCollection
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
name|DefaultInput
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
name|IdString
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
name|RawInput
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
name|ResourceNotFoundException
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
name|Response
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
name|RestApiException
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
name|RestModifyView
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
name|reviewdb
operator|.
name|client
operator|.
name|PatchSet
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
name|edit
operator|.
name|ChangeEdit
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
name|edit
operator|.
name|ChangeEditJson
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
name|edit
operator|.
name|ChangeEditModifier
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
name|edit
operator|.
name|ChangeEditUtil
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
name|patch
operator|.
name|PatchListNotAvailableException
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
name|InvalidChangeOperationException
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
name|kohsuke
operator|.
name|args4j
operator|.
name|Option
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

begin_class
annotation|@
name|Singleton
DECL|class|ChangeEdits
specifier|public
class|class
name|ChangeEdits
implements|implements
name|ChildCollection
argument_list|<
name|ChangeResource
argument_list|,
name|ChangeEditResource
argument_list|>
implements|,
name|AcceptsCreate
argument_list|<
name|ChangeResource
argument_list|>
implements|,
name|AcceptsPost
argument_list|<
name|ChangeResource
argument_list|>
implements|,
name|AcceptsDelete
argument_list|<
name|ChangeResource
argument_list|>
block|{
DECL|field|views
specifier|private
specifier|final
name|DynamicMap
argument_list|<
name|RestView
argument_list|<
name|ChangeEditResource
argument_list|>
argument_list|>
name|views
decl_stmt|;
DECL|field|createFactory
specifier|private
specifier|final
name|Create
operator|.
name|Factory
name|createFactory
decl_stmt|;
DECL|field|deleteEditFactory
specifier|private
specifier|final
name|DeleteEdit
operator|.
name|Factory
name|deleteEditFactory
decl_stmt|;
DECL|field|detail
specifier|private
specifier|final
name|Provider
argument_list|<
name|Detail
argument_list|>
name|detail
decl_stmt|;
DECL|field|editUtil
specifier|private
specifier|final
name|ChangeEditUtil
name|editUtil
decl_stmt|;
DECL|field|post
specifier|private
specifier|final
name|Post
name|post
decl_stmt|;
annotation|@
name|Inject
DECL|method|ChangeEdits (DynamicMap<RestView<ChangeEditResource>> views, Create.Factory createFactory, Provider<Detail> detail, ChangeEditUtil editUtil, Post post, DeleteEdit.Factory deleteEditFactory)
name|ChangeEdits
parameter_list|(
name|DynamicMap
argument_list|<
name|RestView
argument_list|<
name|ChangeEditResource
argument_list|>
argument_list|>
name|views
parameter_list|,
name|Create
operator|.
name|Factory
name|createFactory
parameter_list|,
name|Provider
argument_list|<
name|Detail
argument_list|>
name|detail
parameter_list|,
name|ChangeEditUtil
name|editUtil
parameter_list|,
name|Post
name|post
parameter_list|,
name|DeleteEdit
operator|.
name|Factory
name|deleteEditFactory
parameter_list|)
block|{
name|this
operator|.
name|views
operator|=
name|views
expr_stmt|;
name|this
operator|.
name|createFactory
operator|=
name|createFactory
expr_stmt|;
name|this
operator|.
name|detail
operator|=
name|detail
expr_stmt|;
name|this
operator|.
name|editUtil
operator|=
name|editUtil
expr_stmt|;
name|this
operator|.
name|post
operator|=
name|post
expr_stmt|;
name|this
operator|.
name|deleteEditFactory
operator|=
name|deleteEditFactory
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|views ()
specifier|public
name|DynamicMap
argument_list|<
name|RestView
argument_list|<
name|ChangeEditResource
argument_list|>
argument_list|>
name|views
parameter_list|()
block|{
return|return
name|views
return|;
block|}
annotation|@
name|Override
DECL|method|list ()
specifier|public
name|RestView
argument_list|<
name|ChangeResource
argument_list|>
name|list
parameter_list|()
block|{
return|return
name|detail
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|parse (ChangeResource rsrc, IdString id)
specifier|public
name|ChangeEditResource
name|parse
parameter_list|(
name|ChangeResource
name|rsrc
parameter_list|,
name|IdString
name|id
parameter_list|)
throws|throws
name|ResourceNotFoundException
throws|,
name|AuthException
throws|,
name|IOException
throws|,
name|InvalidChangeOperationException
block|{
name|Optional
argument_list|<
name|ChangeEdit
argument_list|>
name|edit
init|=
name|editUtil
operator|.
name|byChange
argument_list|(
name|rsrc
operator|.
name|getChange
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|edit
operator|.
name|isPresent
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
name|id
argument_list|)
throw|;
block|}
return|return
operator|new
name|ChangeEditResource
argument_list|(
name|rsrc
argument_list|,
name|edit
operator|.
name|get
argument_list|()
argument_list|,
name|id
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|create (ChangeResource parent, IdString id)
specifier|public
name|Create
name|create
parameter_list|(
name|ChangeResource
name|parent
parameter_list|,
name|IdString
name|id
parameter_list|)
throws|throws
name|RestApiException
block|{
return|return
name|createFactory
operator|.
name|create
argument_list|(
name|parent
operator|.
name|getChange
argument_list|()
argument_list|,
name|id
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|post (ChangeResource parent)
specifier|public
name|Post
name|post
parameter_list|(
name|ChangeResource
name|parent
parameter_list|)
throws|throws
name|RestApiException
block|{
return|return
name|post
return|;
block|}
comment|/**   * Create handler that is activated when collection element is accessed   * but doesn't exist, e. g. PUT request with a path was called but   * change edit wasn't created yet. Change edit is created and PUT   * handler is called.   */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|delete (ChangeResource parent, IdString id)
specifier|public
name|DeleteEdit
name|delete
parameter_list|(
name|ChangeResource
name|parent
parameter_list|,
name|IdString
name|id
parameter_list|)
throws|throws
name|RestApiException
block|{
return|return
name|deleteEditFactory
operator|.
name|create
argument_list|(
name|parent
operator|.
name|getChange
argument_list|()
argument_list|,
name|id
operator|!=
literal|null
condition|?
name|id
operator|.
name|get
argument_list|()
else|:
literal|null
argument_list|)
return|;
block|}
DECL|class|Create
specifier|static
class|class
name|Create
implements|implements
name|RestModifyView
argument_list|<
name|ChangeResource
argument_list|,
name|Put
operator|.
name|Input
argument_list|>
block|{
DECL|interface|Factory
interface|interface
name|Factory
block|{
DECL|method|create (Change change, String path)
name|Create
name|create
parameter_list|(
name|Change
name|change
parameter_list|,
name|String
name|path
parameter_list|)
function_decl|;
block|}
DECL|field|db
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
decl_stmt|;
DECL|field|editUtil
specifier|private
specifier|final
name|ChangeEditUtil
name|editUtil
decl_stmt|;
DECL|field|editModifier
specifier|private
specifier|final
name|ChangeEditModifier
name|editModifier
decl_stmt|;
DECL|field|putEdit
specifier|private
specifier|final
name|Put
name|putEdit
decl_stmt|;
DECL|field|change
specifier|private
specifier|final
name|Change
name|change
decl_stmt|;
DECL|field|path
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
annotation|@
name|Inject
DECL|method|Create (Provider<ReviewDb> db, ChangeEditUtil editUtil, ChangeEditModifier editModifier, Put putEdit, @Assisted Change change, @Assisted @Nullable String path)
name|Create
parameter_list|(
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|,
name|ChangeEditUtil
name|editUtil
parameter_list|,
name|ChangeEditModifier
name|editModifier
parameter_list|,
name|Put
name|putEdit
parameter_list|,
annotation|@
name|Assisted
name|Change
name|change
parameter_list|,
annotation|@
name|Assisted
annotation|@
name|Nullable
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|editUtil
operator|=
name|editUtil
expr_stmt|;
name|this
operator|.
name|editModifier
operator|=
name|editModifier
expr_stmt|;
name|this
operator|.
name|putEdit
operator|=
name|putEdit
expr_stmt|;
name|this
operator|.
name|change
operator|=
name|change
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (ChangeResource resource, Put.Input input)
specifier|public
name|Response
argument_list|<
name|?
argument_list|>
name|apply
parameter_list|(
name|ChangeResource
name|resource
parameter_list|,
name|Put
operator|.
name|Input
name|input
parameter_list|)
throws|throws
name|AuthException
throws|,
name|IOException
throws|,
name|ResourceConflictException
throws|,
name|OrmException
throws|,
name|InvalidChangeOperationException
block|{
name|Optional
argument_list|<
name|ChangeEdit
argument_list|>
name|edit
init|=
name|editUtil
operator|.
name|byChange
argument_list|(
name|change
argument_list|)
decl_stmt|;
if|if
condition|(
name|edit
operator|.
name|isPresent
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"edit already exists for the change %s"
argument_list|,
name|resource
operator|.
name|getChange
argument_list|()
operator|.
name|getChangeId
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
name|edit
operator|=
name|createEdit
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|putEdit
operator|.
name|apply
argument_list|(
operator|new
name|ChangeEditResource
argument_list|(
name|resource
argument_list|,
name|edit
operator|.
name|get
argument_list|()
argument_list|,
name|path
argument_list|)
argument_list|,
name|input
argument_list|)
expr_stmt|;
block|}
return|return
name|Response
operator|.
name|none
argument_list|()
return|;
block|}
DECL|method|createEdit ()
specifier|private
name|Optional
argument_list|<
name|ChangeEdit
argument_list|>
name|createEdit
parameter_list|()
throws|throws
name|AuthException
throws|,
name|IOException
throws|,
name|ResourceConflictException
throws|,
name|OrmException
throws|,
name|InvalidChangeOperationException
block|{
name|editModifier
operator|.
name|createEdit
argument_list|(
name|change
argument_list|,
name|db
operator|.
name|get
argument_list|()
operator|.
name|patchSets
argument_list|()
operator|.
name|get
argument_list|(
name|change
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|editUtil
operator|.
name|byChange
argument_list|(
name|change
argument_list|)
return|;
block|}
block|}
DECL|class|DeleteEdit
specifier|static
class|class
name|DeleteEdit
implements|implements
name|RestModifyView
argument_list|<
name|ChangeResource
argument_list|,
name|DeleteEdit
operator|.
name|Input
argument_list|>
block|{
DECL|class|Input
specifier|public
specifier|static
class|class
name|Input
block|{     }
DECL|interface|Factory
interface|interface
name|Factory
block|{
DECL|method|create (Change change, String path)
name|DeleteEdit
name|create
parameter_list|(
name|Change
name|change
parameter_list|,
name|String
name|path
parameter_list|)
function_decl|;
block|}
DECL|field|editUtil
specifier|private
specifier|final
name|ChangeEditUtil
name|editUtil
decl_stmt|;
DECL|field|editModifier
specifier|private
specifier|final
name|ChangeEditModifier
name|editModifier
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
DECL|field|path
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
annotation|@
name|Inject
DECL|method|DeleteEdit (ChangeEditUtil editUtil, ChangeEditModifier editModifier, Provider<ReviewDb> db, @Assisted @Nullable String path)
name|DeleteEdit
parameter_list|(
name|ChangeEditUtil
name|editUtil
parameter_list|,
name|ChangeEditModifier
name|editModifier
parameter_list|,
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|,
annotation|@
name|Assisted
annotation|@
name|Nullable
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|editUtil
operator|=
name|editUtil
expr_stmt|;
name|this
operator|.
name|editModifier
operator|=
name|editModifier
expr_stmt|;
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (ChangeResource rsrc, DeleteEdit.Input in)
specifier|public
name|Response
argument_list|<
name|?
argument_list|>
name|apply
parameter_list|(
name|ChangeResource
name|rsrc
parameter_list|,
name|DeleteEdit
operator|.
name|Input
name|in
parameter_list|)
throws|throws
name|IOException
throws|,
name|AuthException
throws|,
name|ResourceConflictException
throws|,
name|OrmException
throws|,
name|InvalidChangeOperationException
throws|,
name|BadRequestException
block|{
name|Optional
argument_list|<
name|ChangeEdit
argument_list|>
name|edit
init|=
name|editUtil
operator|.
name|byChange
argument_list|(
name|rsrc
operator|.
name|getChange
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|edit
operator|.
name|isPresent
argument_list|()
operator|&&
name|path
operator|==
literal|null
condition|)
block|{
comment|// Edit is wiped out
name|editUtil
operator|.
name|delete
argument_list|(
name|edit
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|edit
operator|.
name|isPresent
argument_list|()
operator|&&
name|path
operator|!=
literal|null
condition|)
block|{
comment|// Edit is created on top of current patch set by deleting path.
comment|// Even if the latest patch set changed since the user triggered
comment|// the operation, deleting the whole file is probably still what
comment|// they intended.
name|editModifier
operator|.
name|createEdit
argument_list|(
name|rsrc
operator|.
name|getChange
argument_list|()
argument_list|,
name|db
operator|.
name|get
argument_list|()
operator|.
name|patchSets
argument_list|()
operator|.
name|get
argument_list|(
name|rsrc
operator|.
name|getChange
argument_list|()
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|edit
operator|=
name|editUtil
operator|.
name|byChange
argument_list|(
name|rsrc
operator|.
name|getChange
argument_list|()
argument_list|)
expr_stmt|;
name|editModifier
operator|.
name|deleteFile
argument_list|(
name|edit
operator|.
name|get
argument_list|()
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Bad request
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"change edit doesn't exist and no path was provided"
argument_list|)
throw|;
block|}
return|return
name|Response
operator|.
name|none
argument_list|()
return|;
block|}
block|}
DECL|class|Detail
specifier|static
class|class
name|Detail
implements|implements
name|RestReadView
argument_list|<
name|ChangeResource
argument_list|>
block|{
DECL|field|editUtil
specifier|private
specifier|final
name|ChangeEditUtil
name|editUtil
decl_stmt|;
DECL|field|editJson
specifier|private
specifier|final
name|ChangeEditJson
name|editJson
decl_stmt|;
DECL|field|fileInfoJson
specifier|private
specifier|final
name|FileInfoJson
name|fileInfoJson
decl_stmt|;
DECL|field|revisions
specifier|private
specifier|final
name|Revisions
name|revisions
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--base"
argument_list|,
name|metaVar
operator|=
literal|"revision-id"
argument_list|)
DECL|field|base
name|String
name|base
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--list"
argument_list|,
name|metaVar
operator|=
literal|"LIST"
argument_list|)
DECL|field|list
name|boolean
name|list
decl_stmt|;
annotation|@
name|Inject
DECL|method|Detail (ChangeEditUtil editUtil, ChangeEditJson editJson, FileInfoJson fileInfoJson, Revisions revisions)
name|Detail
parameter_list|(
name|ChangeEditUtil
name|editUtil
parameter_list|,
name|ChangeEditJson
name|editJson
parameter_list|,
name|FileInfoJson
name|fileInfoJson
parameter_list|,
name|Revisions
name|revisions
parameter_list|)
block|{
name|this
operator|.
name|editJson
operator|=
name|editJson
expr_stmt|;
name|this
operator|.
name|editUtil
operator|=
name|editUtil
expr_stmt|;
name|this
operator|.
name|fileInfoJson
operator|=
name|fileInfoJson
expr_stmt|;
name|this
operator|.
name|revisions
operator|=
name|revisions
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (ChangeResource rsrc)
specifier|public
name|Response
argument_list|<
name|EditInfo
argument_list|>
name|apply
parameter_list|(
name|ChangeResource
name|rsrc
parameter_list|)
throws|throws
name|AuthException
throws|,
name|IOException
throws|,
name|InvalidChangeOperationException
throws|,
name|ResourceNotFoundException
throws|,
name|OrmException
block|{
name|Optional
argument_list|<
name|ChangeEdit
argument_list|>
name|edit
init|=
name|editUtil
operator|.
name|byChange
argument_list|(
name|rsrc
operator|.
name|getChange
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|edit
operator|.
name|isPresent
argument_list|()
condition|)
block|{
return|return
name|Response
operator|.
name|none
argument_list|()
return|;
block|}
name|EditInfo
name|editInfo
init|=
name|editJson
operator|.
name|toEditInfo
argument_list|(
name|edit
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
condition|)
block|{
name|PatchSet
name|basePatchSet
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|base
operator|!=
literal|null
condition|)
block|{
name|RevisionResource
name|baseResource
init|=
name|revisions
operator|.
name|parse
argument_list|(
name|rsrc
argument_list|,
name|IdString
operator|.
name|fromDecoded
argument_list|(
name|base
argument_list|)
argument_list|)
decl_stmt|;
name|basePatchSet
operator|=
name|baseResource
operator|.
name|getPatchSet
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|editInfo
operator|.
name|files
operator|=
name|fileInfoJson
operator|.
name|toFileInfoMap
argument_list|(
name|rsrc
operator|.
name|getChange
argument_list|()
argument_list|,
name|edit
operator|.
name|get
argument_list|()
operator|.
name|getRevision
argument_list|()
argument_list|,
name|basePatchSet
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PatchListNotAvailableException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
return|return
name|Response
operator|.
name|ok
argument_list|(
name|editInfo
argument_list|)
return|;
block|}
block|}
comment|/**    * Post to edit collection resource. Two different operations are    * supported:    *<ul>    *<li>Create non existing change edit</li>    *<li>Restore path in existing change edit</li>    *</ul>    * The combination of two operations in one request is supported.    */
annotation|@
name|Singleton
DECL|class|Post
specifier|public
specifier|static
class|class
name|Post
implements|implements
name|RestModifyView
argument_list|<
name|ChangeResource
argument_list|,
name|Post
operator|.
name|Input
argument_list|>
block|{
DECL|class|Input
specifier|public
specifier|static
class|class
name|Input
block|{
DECL|field|restorePath
specifier|public
name|String
name|restorePath
decl_stmt|;
block|}
DECL|field|db
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
decl_stmt|;
DECL|field|editUtil
specifier|private
specifier|final
name|ChangeEditUtil
name|editUtil
decl_stmt|;
DECL|field|editModifier
specifier|private
specifier|final
name|ChangeEditModifier
name|editModifier
decl_stmt|;
annotation|@
name|Inject
DECL|method|Post (Provider<ReviewDb> db, ChangeEditUtil editUtil, ChangeEditModifier editModifier)
name|Post
parameter_list|(
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|,
name|ChangeEditUtil
name|editUtil
parameter_list|,
name|ChangeEditModifier
name|editModifier
parameter_list|)
block|{
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|editUtil
operator|=
name|editUtil
expr_stmt|;
name|this
operator|.
name|editModifier
operator|=
name|editModifier
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (ChangeResource resource, Post.Input input)
specifier|public
name|Response
argument_list|<
name|?
argument_list|>
name|apply
parameter_list|(
name|ChangeResource
name|resource
parameter_list|,
name|Post
operator|.
name|Input
name|input
parameter_list|)
throws|throws
name|AuthException
throws|,
name|InvalidChangeOperationException
throws|,
name|IOException
throws|,
name|ResourceConflictException
throws|,
name|OrmException
block|{
name|Optional
argument_list|<
name|ChangeEdit
argument_list|>
name|edit
init|=
name|editUtil
operator|.
name|byChange
argument_list|(
name|resource
operator|.
name|getChange
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|edit
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|edit
operator|=
name|createEdit
argument_list|(
name|resource
operator|.
name|getChange
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|input
operator|!=
literal|null
operator|&&
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|input
operator|.
name|restorePath
argument_list|)
condition|)
block|{
name|editModifier
operator|.
name|restoreFile
argument_list|(
name|edit
operator|.
name|get
argument_list|()
argument_list|,
name|input
operator|.
name|restorePath
argument_list|)
expr_stmt|;
block|}
return|return
name|Response
operator|.
name|none
argument_list|()
return|;
block|}
DECL|method|createEdit (Change change)
specifier|private
name|Optional
argument_list|<
name|ChangeEdit
argument_list|>
name|createEdit
parameter_list|(
name|Change
name|change
parameter_list|)
throws|throws
name|AuthException
throws|,
name|IOException
throws|,
name|ResourceConflictException
throws|,
name|OrmException
throws|,
name|InvalidChangeOperationException
block|{
name|editModifier
operator|.
name|createEdit
argument_list|(
name|change
argument_list|,
name|db
operator|.
name|get
argument_list|()
operator|.
name|patchSets
argument_list|()
operator|.
name|get
argument_list|(
name|change
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|editUtil
operator|.
name|byChange
argument_list|(
name|change
argument_list|)
return|;
block|}
block|}
comment|/**   * Put handler that is activated when PUT request is called on   * collection element.   */
annotation|@
name|Singleton
DECL|class|Put
specifier|public
specifier|static
class|class
name|Put
implements|implements
name|RestModifyView
argument_list|<
name|ChangeEditResource
argument_list|,
name|Put
operator|.
name|Input
argument_list|>
block|{
DECL|class|Input
specifier|public
specifier|static
class|class
name|Input
block|{
annotation|@
name|DefaultInput
DECL|field|content
specifier|public
name|RawInput
name|content
decl_stmt|;
block|}
DECL|field|editModifier
specifier|private
specifier|final
name|ChangeEditModifier
name|editModifier
decl_stmt|;
annotation|@
name|Inject
DECL|method|Put (ChangeEditModifier editModifier)
name|Put
parameter_list|(
name|ChangeEditModifier
name|editModifier
parameter_list|)
block|{
name|this
operator|.
name|editModifier
operator|=
name|editModifier
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (ChangeEditResource rsrc, Input input)
specifier|public
name|Response
argument_list|<
name|?
argument_list|>
name|apply
parameter_list|(
name|ChangeEditResource
name|rsrc
parameter_list|,
name|Input
name|input
parameter_list|)
throws|throws
name|AuthException
throws|,
name|ResourceConflictException
throws|,
name|IOException
block|{
try|try
block|{
name|editModifier
operator|.
name|modifyFile
argument_list|(
name|rsrc
operator|.
name|getChangeEdit
argument_list|()
argument_list|,
name|rsrc
operator|.
name|getPath
argument_list|()
argument_list|,
name|ByteStreams
operator|.
name|toByteArray
argument_list|(
name|input
operator|.
name|content
operator|.
name|getInputStream
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidChangeOperationException
decl||
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|Response
operator|.
name|none
argument_list|()
return|;
block|}
block|}
comment|/**    * Handler to delete a file.    *<p>    * This deletes the file from the repository completely. This is not the same    * as reverting or restoring a file to its previous contents.    */
annotation|@
name|Singleton
DECL|class|DeleteContent
specifier|static
class|class
name|DeleteContent
implements|implements
name|RestModifyView
argument_list|<
name|ChangeEditResource
argument_list|,
name|DeleteContent
operator|.
name|Input
argument_list|>
block|{
DECL|class|Input
specifier|public
specifier|static
class|class
name|Input
block|{     }
DECL|field|editModifier
specifier|private
specifier|final
name|ChangeEditModifier
name|editModifier
decl_stmt|;
annotation|@
name|Inject
DECL|method|DeleteContent (ChangeEditModifier editModifier)
name|DeleteContent
parameter_list|(
name|ChangeEditModifier
name|editModifier
parameter_list|)
block|{
name|this
operator|.
name|editModifier
operator|=
name|editModifier
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (ChangeEditResource rsrc, DeleteContent.Input input)
specifier|public
name|Response
argument_list|<
name|?
argument_list|>
name|apply
parameter_list|(
name|ChangeEditResource
name|rsrc
parameter_list|,
name|DeleteContent
operator|.
name|Input
name|input
parameter_list|)
throws|throws
name|AuthException
throws|,
name|ResourceConflictException
block|{
try|try
block|{
name|editModifier
operator|.
name|deleteFile
argument_list|(
name|rsrc
operator|.
name|getChangeEdit
argument_list|()
argument_list|,
name|rsrc
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidChangeOperationException
decl||
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|Response
operator|.
name|none
argument_list|()
return|;
block|}
block|}
annotation|@
name|Singleton
DECL|class|Get
specifier|static
class|class
name|Get
implements|implements
name|RestReadView
argument_list|<
name|ChangeEditResource
argument_list|>
block|{
DECL|field|fileContentUtil
specifier|private
specifier|final
name|FileContentUtil
name|fileContentUtil
decl_stmt|;
annotation|@
name|Inject
DECL|method|Get (FileContentUtil fileContentUtil)
name|Get
parameter_list|(
name|FileContentUtil
name|fileContentUtil
parameter_list|)
block|{
name|this
operator|.
name|fileContentUtil
operator|=
name|fileContentUtil
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (ChangeEditResource rsrc)
specifier|public
name|Response
argument_list|<
name|?
argument_list|>
name|apply
parameter_list|(
name|ChangeEditResource
name|rsrc
parameter_list|)
throws|throws
name|ResourceNotFoundException
throws|,
name|IOException
block|{
try|try
block|{
return|return
name|Response
operator|.
name|ok
argument_list|(
name|fileContentUtil
operator|.
name|getContent
argument_list|(
name|rsrc
operator|.
name|getChangeEdit
argument_list|()
operator|.
name|getChange
argument_list|()
operator|.
name|getProject
argument_list|()
argument_list|,
name|rsrc
operator|.
name|getChangeEdit
argument_list|()
operator|.
name|getRevision
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|rsrc
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ResourceNotFoundException
name|rnfe
parameter_list|)
block|{
return|return
name|Response
operator|.
name|none
argument_list|()
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

