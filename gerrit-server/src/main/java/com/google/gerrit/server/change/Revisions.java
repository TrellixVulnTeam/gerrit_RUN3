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
name|Joiner
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
name|client
operator|.
name|RevId
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
name|PatchSetUtil
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
name|ChangeEditUtil
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|Revisions
specifier|public
class|class
name|Revisions
implements|implements
name|ChildCollection
argument_list|<
name|ChangeResource
argument_list|,
name|RevisionResource
argument_list|>
block|{
DECL|field|views
specifier|private
specifier|final
name|DynamicMap
argument_list|<
name|RestView
argument_list|<
name|RevisionResource
argument_list|>
argument_list|>
name|views
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
DECL|field|editUtil
specifier|private
specifier|final
name|ChangeEditUtil
name|editUtil
decl_stmt|;
DECL|field|psUtil
specifier|private
specifier|final
name|PatchSetUtil
name|psUtil
decl_stmt|;
annotation|@
name|Inject
DECL|method|Revisions (DynamicMap<RestView<RevisionResource>> views, Provider<ReviewDb> dbProvider, ChangeEditUtil editUtil, PatchSetUtil psUtil)
name|Revisions
parameter_list|(
name|DynamicMap
argument_list|<
name|RestView
argument_list|<
name|RevisionResource
argument_list|>
argument_list|>
name|views
parameter_list|,
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|dbProvider
parameter_list|,
name|ChangeEditUtil
name|editUtil
parameter_list|,
name|PatchSetUtil
name|psUtil
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
name|dbProvider
operator|=
name|dbProvider
expr_stmt|;
name|this
operator|.
name|editUtil
operator|=
name|editUtil
expr_stmt|;
name|this
operator|.
name|psUtil
operator|=
name|psUtil
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
name|RevisionResource
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
throws|throws
name|ResourceNotFoundException
block|{
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|parse (ChangeResource change, IdString id)
specifier|public
name|RevisionResource
name|parse
parameter_list|(
name|ChangeResource
name|change
parameter_list|,
name|IdString
name|id
parameter_list|)
throws|throws
name|ResourceNotFoundException
throws|,
name|AuthException
throws|,
name|OrmException
throws|,
name|IOException
block|{
if|if
condition|(
name|id
operator|.
name|equals
argument_list|(
literal|"current"
argument_list|)
condition|)
block|{
name|PatchSet
name|ps
init|=
name|psUtil
operator|.
name|current
argument_list|(
name|dbProvider
operator|.
name|get
argument_list|()
argument_list|,
name|change
operator|.
name|getNotes
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|ps
operator|!=
literal|null
operator|&&
name|visible
argument_list|(
name|change
argument_list|,
name|ps
argument_list|)
condition|)
block|{
return|return
operator|new
name|RevisionResource
argument_list|(
name|change
argument_list|,
name|ps
argument_list|)
operator|.
name|doNotCache
argument_list|()
return|;
block|}
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
name|id
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|RevisionResource
argument_list|>
name|match
init|=
name|Lists
operator|.
name|newArrayListWithExpectedSize
argument_list|(
literal|2
argument_list|)
decl_stmt|;
for|for
control|(
name|RevisionResource
name|rsrc
range|:
name|find
argument_list|(
name|change
argument_list|,
name|id
operator|.
name|get
argument_list|()
argument_list|)
control|)
block|{
if|if
condition|(
name|visible
argument_list|(
name|change
argument_list|,
name|rsrc
operator|.
name|getPatchSet
argument_list|()
argument_list|)
condition|)
block|{
name|match
operator|.
name|add
argument_list|(
name|rsrc
argument_list|)
expr_stmt|;
block|}
block|}
switch|switch
condition|(
name|match
operator|.
name|size
argument_list|()
condition|)
block|{
case|case
literal|0
case|:
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
name|id
argument_list|)
throw|;
case|case
literal|1
case|:
return|return
name|match
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
literal|"Multiple patch sets for \""
operator|+
name|id
operator|.
name|get
argument_list|()
operator|+
literal|"\": "
operator|+
name|Joiner
operator|.
name|on
argument_list|(
literal|"; "
argument_list|)
operator|.
name|join
argument_list|(
name|match
argument_list|)
argument_list|)
throw|;
block|}
block|}
DECL|method|visible (ChangeResource change, PatchSet ps)
specifier|private
name|boolean
name|visible
parameter_list|(
name|ChangeResource
name|change
parameter_list|,
name|PatchSet
name|ps
parameter_list|)
throws|throws
name|OrmException
block|{
return|return
name|change
operator|.
name|getControl
argument_list|()
operator|.
name|isPatchVisible
argument_list|(
name|ps
argument_list|,
name|dbProvider
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
DECL|method|find (ChangeResource change, String id)
specifier|private
name|List
argument_list|<
name|RevisionResource
argument_list|>
name|find
parameter_list|(
name|ChangeResource
name|change
parameter_list|,
name|String
name|id
parameter_list|)
throws|throws
name|OrmException
throws|,
name|IOException
throws|,
name|AuthException
block|{
if|if
condition|(
name|id
operator|.
name|equals
argument_list|(
literal|"0"
argument_list|)
operator|||
name|id
operator|.
name|equals
argument_list|(
literal|"edit"
argument_list|)
condition|)
block|{
return|return
name|loadEdit
argument_list|(
name|change
argument_list|,
literal|null
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|id
operator|.
name|length
argument_list|()
operator|<
literal|6
operator|&&
name|id
operator|.
name|matches
argument_list|(
literal|"^[1-9][0-9]{0,4}$"
argument_list|)
condition|)
block|{
comment|// Legacy patch set number syntax.
return|return
name|byLegacyPatchSetId
argument_list|(
name|change
argument_list|,
name|id
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|id
operator|.
name|length
argument_list|()
operator|<
literal|4
operator|||
name|id
operator|.
name|length
argument_list|()
operator|>
name|RevId
operator|.
name|LEN
condition|)
block|{
comment|// Require a minimum of 4 digits.
comment|// Impossibly long identifier will never match.
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
else|else
block|{
name|List
argument_list|<
name|RevisionResource
argument_list|>
name|out
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|PatchSet
name|ps
range|:
name|psUtil
operator|.
name|byChange
argument_list|(
name|dbProvider
operator|.
name|get
argument_list|()
argument_list|,
name|change
operator|.
name|getNotes
argument_list|()
argument_list|)
control|)
block|{
if|if
condition|(
name|ps
operator|.
name|getRevision
argument_list|()
operator|!=
literal|null
operator|&&
name|ps
operator|.
name|getRevision
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|startsWith
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|out
operator|.
name|add
argument_list|(
operator|new
name|RevisionResource
argument_list|(
name|change
argument_list|,
name|ps
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Not an existing patch set on a change, but might be an edit.
if|if
condition|(
name|out
operator|.
name|isEmpty
argument_list|()
operator|&&
name|id
operator|.
name|length
argument_list|()
operator|==
name|RevId
operator|.
name|LEN
condition|)
block|{
return|return
name|loadEdit
argument_list|(
name|change
argument_list|,
operator|new
name|RevId
argument_list|(
name|id
argument_list|)
argument_list|)
return|;
block|}
return|return
name|out
return|;
block|}
block|}
DECL|method|byLegacyPatchSetId (ChangeResource change, String id)
specifier|private
name|List
argument_list|<
name|RevisionResource
argument_list|>
name|byLegacyPatchSetId
parameter_list|(
name|ChangeResource
name|change
parameter_list|,
name|String
name|id
parameter_list|)
throws|throws
name|OrmException
block|{
name|PatchSet
name|ps
init|=
name|psUtil
operator|.
name|get
argument_list|(
name|dbProvider
operator|.
name|get
argument_list|()
argument_list|,
name|change
operator|.
name|getNotes
argument_list|()
argument_list|,
operator|new
name|PatchSet
operator|.
name|Id
argument_list|(
name|change
operator|.
name|getId
argument_list|()
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|id
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|ps
operator|!=
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|RevisionResource
argument_list|(
name|change
argument_list|,
name|ps
argument_list|)
argument_list|)
return|;
block|}
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
DECL|method|loadEdit (ChangeResource change, RevId revid)
specifier|private
name|List
argument_list|<
name|RevisionResource
argument_list|>
name|loadEdit
parameter_list|(
name|ChangeResource
name|change
parameter_list|,
name|RevId
name|revid
parameter_list|)
throws|throws
name|AuthException
throws|,
name|IOException
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
name|change
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
condition|)
block|{
name|PatchSet
name|ps
init|=
operator|new
name|PatchSet
argument_list|(
operator|new
name|PatchSet
operator|.
name|Id
argument_list|(
name|change
operator|.
name|getId
argument_list|()
argument_list|,
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|ps
operator|.
name|setRevision
argument_list|(
name|edit
operator|.
name|get
argument_list|()
operator|.
name|getRevision
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|revid
operator|==
literal|null
operator|||
name|edit
operator|.
name|get
argument_list|()
operator|.
name|getRevision
argument_list|()
operator|.
name|equals
argument_list|(
name|revid
argument_list|)
condition|)
block|{
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|RevisionResource
argument_list|(
name|change
argument_list|,
name|ps
argument_list|,
name|edit
argument_list|)
argument_list|)
return|;
block|}
block|}
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
block|}
end_class

end_unit

