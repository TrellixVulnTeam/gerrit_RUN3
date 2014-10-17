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
name|checkState
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
name|gerrit
operator|.
name|common
operator|.
name|data
operator|.
name|PatchScript
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
name|data
operator|.
name|PatchScript
operator|.
name|DisplayMethod
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
name|data
operator|.
name|PatchScript
operator|.
name|FileMode
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
name|WebLinkInfo
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
name|CacheControl
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
name|prettify
operator|.
name|common
operator|.
name|SparseFileContent
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
name|client
operator|.
name|AccountDiffPreference
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
name|Patch
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
name|Patch
operator|.
name|ChangeType
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
name|Project
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
name|WebLinks
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
name|LargeObjectException
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
name|PatchScriptFactory
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
name|gerrit
operator|.
name|server
operator|.
name|project
operator|.
name|NoSuchChangeException
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
name|ProjectCache
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
name|Inject
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
name|diff
operator|.
name|Edit
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
name|diff
operator|.
name|ReplaceEdit
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
name|CmdLineException
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
name|CmdLineParser
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
name|NamedOptionDef
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
name|org
operator|.
name|kohsuke
operator|.
name|args4j
operator|.
name|OptionDef
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
name|spi
operator|.
name|OptionHandler
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
name|spi
operator|.
name|Parameters
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
name|spi
operator|.
name|Setter
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
name|TimeUnit
import|;
end_import

begin_class
DECL|class|GetDiff
specifier|public
class|class
name|GetDiff
implements|implements
name|RestReadView
argument_list|<
name|FileResource
argument_list|>
block|{
DECL|field|projectCache
specifier|private
specifier|final
name|ProjectCache
name|projectCache
decl_stmt|;
DECL|field|patchScriptFactoryFactory
specifier|private
specifier|final
name|PatchScriptFactory
operator|.
name|Factory
name|patchScriptFactoryFactory
decl_stmt|;
DECL|field|revisions
specifier|private
specifier|final
name|Revisions
name|revisions
decl_stmt|;
DECL|field|webLinks
specifier|private
specifier|final
name|WebLinks
name|webLinks
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
literal|"REVISION"
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
literal|"--ignore-whitespace"
argument_list|)
DECL|field|ignoreWhitespace
name|IgnoreWhitespace
name|ignoreWhitespace
init|=
name|IgnoreWhitespace
operator|.
name|NONE
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--context"
argument_list|,
name|handler
operator|=
name|ContextOptionHandler
operator|.
name|class
argument_list|)
DECL|field|context
name|short
name|context
init|=
name|AccountDiffPreference
operator|.
name|DEFAULT_CONTEXT
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--intraline"
argument_list|)
DECL|field|intraline
name|boolean
name|intraline
decl_stmt|;
annotation|@
name|Inject
DECL|method|GetDiff (ProjectCache projectCache, PatchScriptFactory.Factory patchScriptFactoryFactory, Revisions revisions, WebLinks webLinks)
name|GetDiff
parameter_list|(
name|ProjectCache
name|projectCache
parameter_list|,
name|PatchScriptFactory
operator|.
name|Factory
name|patchScriptFactoryFactory
parameter_list|,
name|Revisions
name|revisions
parameter_list|,
name|WebLinks
name|webLinks
parameter_list|)
block|{
name|this
operator|.
name|projectCache
operator|=
name|projectCache
expr_stmt|;
name|this
operator|.
name|patchScriptFactoryFactory
operator|=
name|patchScriptFactoryFactory
expr_stmt|;
name|this
operator|.
name|revisions
operator|=
name|revisions
expr_stmt|;
name|this
operator|.
name|webLinks
operator|=
name|webLinks
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (FileResource resource)
specifier|public
name|Response
argument_list|<
name|Result
argument_list|>
name|apply
parameter_list|(
name|FileResource
name|resource
parameter_list|)
throws|throws
name|ResourceConflictException
throws|,
name|ResourceNotFoundException
throws|,
name|OrmException
throws|,
name|AuthException
throws|,
name|InvalidChangeOperationException
throws|,
name|IOException
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
name|resource
operator|.
name|getRevision
argument_list|()
operator|.
name|getChangeResource
argument_list|()
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
name|AccountDiffPreference
name|prefs
init|=
operator|new
name|AccountDiffPreference
argument_list|(
operator|new
name|Account
operator|.
name|Id
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|prefs
operator|.
name|setIgnoreWhitespace
argument_list|(
name|ignoreWhitespace
operator|.
name|whitespace
argument_list|)
expr_stmt|;
name|prefs
operator|.
name|setContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|prefs
operator|.
name|setIntralineDifference
argument_list|(
name|intraline
argument_list|)
expr_stmt|;
try|try
block|{
name|PatchScriptFactory
name|psf
init|=
name|patchScriptFactoryFactory
operator|.
name|create
argument_list|(
name|resource
operator|.
name|getRevision
argument_list|()
operator|.
name|getControl
argument_list|()
argument_list|,
name|resource
operator|.
name|getPatchKey
argument_list|()
operator|.
name|getFileName
argument_list|()
argument_list|,
name|basePatchSet
operator|!=
literal|null
condition|?
name|basePatchSet
operator|.
name|getId
argument_list|()
else|:
literal|null
argument_list|,
name|resource
operator|.
name|getPatchKey
argument_list|()
operator|.
name|getParentKey
argument_list|()
argument_list|,
name|prefs
argument_list|)
decl_stmt|;
name|psf
operator|.
name|setLoadHistory
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|psf
operator|.
name|setLoadComments
argument_list|(
name|context
operator|!=
name|AccountDiffPreference
operator|.
name|WHOLE_FILE_CONTEXT
argument_list|)
expr_stmt|;
name|PatchScript
name|ps
init|=
name|psf
operator|.
name|call
argument_list|()
decl_stmt|;
name|Content
name|content
init|=
operator|new
name|Content
argument_list|(
name|ps
argument_list|)
decl_stmt|;
for|for
control|(
name|Edit
name|edit
range|:
name|ps
operator|.
name|getEdits
argument_list|()
control|)
block|{
if|if
condition|(
name|edit
operator|.
name|getType
argument_list|()
operator|==
name|Edit
operator|.
name|Type
operator|.
name|EMPTY
condition|)
block|{
continue|continue;
block|}
name|content
operator|.
name|addCommon
argument_list|(
name|edit
operator|.
name|getBeginA
argument_list|()
argument_list|)
expr_stmt|;
name|checkState
argument_list|(
name|content
operator|.
name|nextA
operator|==
name|edit
operator|.
name|getBeginA
argument_list|()
argument_list|,
literal|"nextA = %d; want %d"
argument_list|,
name|content
operator|.
name|nextA
argument_list|,
name|edit
operator|.
name|getBeginA
argument_list|()
argument_list|)
expr_stmt|;
name|checkState
argument_list|(
name|content
operator|.
name|nextB
operator|==
name|edit
operator|.
name|getBeginB
argument_list|()
argument_list|,
literal|"nextB = %d; want %d"
argument_list|,
name|content
operator|.
name|nextB
argument_list|,
name|edit
operator|.
name|getBeginB
argument_list|()
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|edit
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|DELETE
case|:
case|case
name|INSERT
case|:
case|case
name|REPLACE
case|:
name|List
argument_list|<
name|Edit
argument_list|>
name|internalEdit
init|=
name|edit
operator|instanceof
name|ReplaceEdit
condition|?
operator|(
operator|(
name|ReplaceEdit
operator|)
name|edit
operator|)
operator|.
name|getInternalEdits
argument_list|()
else|:
literal|null
decl_stmt|;
name|content
operator|.
name|addDiff
argument_list|(
name|edit
operator|.
name|getEndA
argument_list|()
argument_list|,
name|edit
operator|.
name|getEndB
argument_list|()
argument_list|,
name|internalEdit
argument_list|)
expr_stmt|;
break|break;
case|case
name|EMPTY
case|:
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
block|}
name|content
operator|.
name|addCommon
argument_list|(
name|ps
operator|.
name|getA
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|ProjectState
name|state
init|=
name|projectCache
operator|.
name|get
argument_list|(
name|resource
operator|.
name|getRevision
argument_list|()
operator|.
name|getChange
argument_list|()
operator|.
name|getProject
argument_list|()
argument_list|)
decl_stmt|;
name|Result
name|result
init|=
operator|new
name|Result
argument_list|()
decl_stmt|;
if|if
condition|(
name|ps
operator|.
name|getDisplayMethodA
argument_list|()
operator|!=
name|DisplayMethod
operator|.
name|NONE
condition|)
block|{
name|result
operator|.
name|metaA
operator|=
operator|new
name|FileMeta
argument_list|()
expr_stmt|;
name|result
operator|.
name|metaA
operator|.
name|name
operator|=
name|MoreObjects
operator|.
name|firstNonNull
argument_list|(
name|ps
operator|.
name|getOldName
argument_list|()
argument_list|,
name|ps
operator|.
name|getNewName
argument_list|()
argument_list|)
expr_stmt|;
name|setContentType
argument_list|(
name|result
operator|.
name|metaA
argument_list|,
name|state
argument_list|,
name|ps
operator|.
name|getFileModeA
argument_list|()
argument_list|,
name|ps
operator|.
name|getMimeTypeA
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|metaA
operator|.
name|lines
operator|=
name|ps
operator|.
name|getA
argument_list|()
operator|.
name|size
argument_list|()
expr_stmt|;
comment|// TODO referring to the parent commit by refs/changes/12/60012/1^1
comment|// will likely not work for inline edits
name|String
name|rev
init|=
name|basePatchSet
operator|!=
literal|null
condition|?
name|basePatchSet
operator|.
name|getRefName
argument_list|()
else|:
name|resource
operator|.
name|getRevision
argument_list|()
operator|.
name|getPatchSet
argument_list|()
operator|.
name|getRefName
argument_list|()
operator|+
literal|"^1"
decl_stmt|;
name|result
operator|.
name|metaA
operator|.
name|webLinks
operator|=
name|getFileWebLinks
argument_list|(
name|state
operator|.
name|getProject
argument_list|()
argument_list|,
name|rev
argument_list|,
name|result
operator|.
name|metaA
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ps
operator|.
name|getDisplayMethodB
argument_list|()
operator|!=
name|DisplayMethod
operator|.
name|NONE
condition|)
block|{
name|result
operator|.
name|metaB
operator|=
operator|new
name|FileMeta
argument_list|()
expr_stmt|;
name|result
operator|.
name|metaB
operator|.
name|name
operator|=
name|ps
operator|.
name|getNewName
argument_list|()
expr_stmt|;
name|setContentType
argument_list|(
name|result
operator|.
name|metaB
argument_list|,
name|state
argument_list|,
name|ps
operator|.
name|getFileModeB
argument_list|()
argument_list|,
name|ps
operator|.
name|getMimeTypeB
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|metaB
operator|.
name|lines
operator|=
name|ps
operator|.
name|getB
argument_list|()
operator|.
name|size
argument_list|()
expr_stmt|;
name|result
operator|.
name|metaB
operator|.
name|webLinks
operator|=
name|getFileWebLinks
argument_list|(
name|state
operator|.
name|getProject
argument_list|()
argument_list|,
name|resource
operator|.
name|getRevision
argument_list|()
operator|.
name|getPatchSet
argument_list|()
operator|.
name|getRefName
argument_list|()
argument_list|,
name|result
operator|.
name|metaB
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|intraline
condition|)
block|{
if|if
condition|(
name|ps
operator|.
name|hasIntralineTimeout
argument_list|()
condition|)
block|{
name|result
operator|.
name|intralineStatus
operator|=
name|IntraLineStatus
operator|.
name|TIMEOUT
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ps
operator|.
name|hasIntralineFailure
argument_list|()
condition|)
block|{
name|result
operator|.
name|intralineStatus
operator|=
name|IntraLineStatus
operator|.
name|FAILURE
expr_stmt|;
block|}
else|else
block|{
name|result
operator|.
name|intralineStatus
operator|=
name|IntraLineStatus
operator|.
name|OK
expr_stmt|;
block|}
block|}
name|result
operator|.
name|changeType
operator|=
name|ps
operator|.
name|getChangeType
argument_list|()
expr_stmt|;
if|if
condition|(
name|ps
operator|.
name|getPatchHeader
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|result
operator|.
name|diffHeader
operator|=
name|ps
operator|.
name|getPatchHeader
argument_list|()
expr_stmt|;
block|}
name|result
operator|.
name|content
operator|=
name|content
operator|.
name|lines
expr_stmt|;
name|Response
argument_list|<
name|Result
argument_list|>
name|r
init|=
name|Response
operator|.
name|ok
argument_list|(
name|result
argument_list|)
decl_stmt|;
if|if
condition|(
name|resource
operator|.
name|isCacheable
argument_list|()
condition|)
block|{
name|r
operator|.
name|caching
argument_list|(
name|CacheControl
operator|.
name|PRIVATE
argument_list|(
literal|7
argument_list|,
name|TimeUnit
operator|.
name|DAYS
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchChangeException
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
catch|catch
parameter_list|(
name|LargeObjectException
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
block|}
DECL|method|getFileWebLinks (Project project, String rev, String file)
specifier|private
name|List
argument_list|<
name|WebLinkInfo
argument_list|>
name|getFileWebLinks
parameter_list|(
name|Project
name|project
parameter_list|,
name|String
name|rev
parameter_list|,
name|String
name|file
parameter_list|)
block|{
name|List
argument_list|<
name|WebLinkInfo
argument_list|>
name|links
init|=
name|webLinks
operator|.
name|getFileLinks
argument_list|(
name|project
operator|.
name|getName
argument_list|()
argument_list|,
name|rev
argument_list|,
name|file
argument_list|)
decl_stmt|;
return|return
operator|!
name|links
operator|.
name|isEmpty
argument_list|()
condition|?
name|links
else|:
literal|null
return|;
block|}
DECL|class|Result
specifier|static
class|class
name|Result
block|{
DECL|field|metaA
name|FileMeta
name|metaA
decl_stmt|;
DECL|field|metaB
name|FileMeta
name|metaB
decl_stmt|;
DECL|field|intralineStatus
name|IntraLineStatus
name|intralineStatus
decl_stmt|;
DECL|field|changeType
name|ChangeType
name|changeType
decl_stmt|;
DECL|field|diffHeader
name|List
argument_list|<
name|String
argument_list|>
name|diffHeader
decl_stmt|;
DECL|field|content
name|List
argument_list|<
name|ContentEntry
argument_list|>
name|content
decl_stmt|;
block|}
DECL|class|FileMeta
specifier|static
class|class
name|FileMeta
block|{
DECL|field|name
name|String
name|name
decl_stmt|;
DECL|field|contentType
name|String
name|contentType
decl_stmt|;
DECL|field|lines
name|Integer
name|lines
decl_stmt|;
DECL|field|webLinks
name|List
argument_list|<
name|WebLinkInfo
argument_list|>
name|webLinks
decl_stmt|;
block|}
DECL|method|setContentType (FileMeta meta, ProjectState project, FileMode fileMode, String mimeType)
specifier|private
name|void
name|setContentType
parameter_list|(
name|FileMeta
name|meta
parameter_list|,
name|ProjectState
name|project
parameter_list|,
name|FileMode
name|fileMode
parameter_list|,
name|String
name|mimeType
parameter_list|)
block|{
switch|switch
condition|(
name|fileMode
condition|)
block|{
case|case
name|FILE
case|:
if|if
condition|(
name|Patch
operator|.
name|COMMIT_MSG
operator|.
name|equals
argument_list|(
name|meta
operator|.
name|name
argument_list|)
condition|)
block|{
name|mimeType
operator|=
literal|"text/x-gerrit-commit-message"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|project
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|ProjectState
name|p
range|:
name|project
operator|.
name|tree
argument_list|()
control|)
block|{
name|String
name|t
init|=
name|p
operator|.
name|getConfig
argument_list|()
operator|.
name|getMimeTypes
argument_list|()
operator|.
name|getMimeType
argument_list|(
name|meta
operator|.
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
name|mimeType
operator|=
name|t
expr_stmt|;
break|break;
block|}
block|}
block|}
name|meta
operator|.
name|contentType
operator|=
name|mimeType
expr_stmt|;
break|break;
case|case
name|GITLINK
case|:
name|meta
operator|.
name|contentType
operator|=
literal|"x-git/gitlink"
expr_stmt|;
break|break;
case|case
name|SYMLINK
case|:
name|meta
operator|.
name|contentType
operator|=
literal|"x-git/symlink"
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"file mode: "
operator|+
name|fileMode
argument_list|)
throw|;
block|}
block|}
DECL|enum|IntraLineStatus
enum|enum
name|IntraLineStatus
block|{
DECL|enumConstant|OK
name|OK
block|,
DECL|enumConstant|TIMEOUT
name|TIMEOUT
block|,
DECL|enumConstant|FAILURE
name|FAILURE
block|}
DECL|class|Content
specifier|private
specifier|static
class|class
name|Content
block|{
DECL|field|lines
specifier|final
name|List
argument_list|<
name|ContentEntry
argument_list|>
name|lines
decl_stmt|;
DECL|field|fileA
specifier|final
name|SparseFileContent
name|fileA
decl_stmt|;
DECL|field|fileB
specifier|final
name|SparseFileContent
name|fileB
decl_stmt|;
DECL|field|ignoreWS
specifier|final
name|boolean
name|ignoreWS
decl_stmt|;
DECL|field|nextA
name|int
name|nextA
decl_stmt|;
DECL|field|nextB
name|int
name|nextB
decl_stmt|;
DECL|method|Content (PatchScript ps)
name|Content
parameter_list|(
name|PatchScript
name|ps
parameter_list|)
block|{
name|lines
operator|=
name|Lists
operator|.
name|newArrayListWithExpectedSize
argument_list|(
name|ps
operator|.
name|getEdits
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|2
argument_list|)
expr_stmt|;
name|fileA
operator|=
name|ps
operator|.
name|getA
argument_list|()
expr_stmt|;
name|fileB
operator|=
name|ps
operator|.
name|getB
argument_list|()
expr_stmt|;
name|ignoreWS
operator|=
name|ps
operator|.
name|isIgnoreWhitespace
argument_list|()
expr_stmt|;
block|}
DECL|method|addCommon (int end)
name|void
name|addCommon
parameter_list|(
name|int
name|end
parameter_list|)
block|{
name|end
operator|=
name|Math
operator|.
name|min
argument_list|(
name|end
argument_list|,
name|fileA
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|nextA
operator|>=
name|end
condition|)
block|{
return|return;
block|}
while|while
condition|(
name|nextA
operator|<
name|end
condition|)
block|{
if|if
condition|(
operator|!
name|fileA
operator|.
name|contains
argument_list|(
name|nextA
argument_list|)
condition|)
block|{
name|int
name|endRegion
init|=
name|Math
operator|.
name|min
argument_list|(
name|end
argument_list|,
name|nextA
operator|==
literal|0
condition|?
name|fileA
operator|.
name|first
argument_list|()
else|:
name|fileA
operator|.
name|next
argument_list|(
name|nextA
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|len
init|=
name|endRegion
operator|-
name|nextA
decl_stmt|;
name|entry
argument_list|()
operator|.
name|skip
operator|=
name|len
expr_stmt|;
name|nextA
operator|=
name|endRegion
expr_stmt|;
name|nextB
operator|+=
name|len
expr_stmt|;
continue|continue;
block|}
name|ContentEntry
name|e
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|nextA
init|;
name|i
operator|==
name|nextA
operator|&&
name|i
operator|<
name|end
condition|;
name|i
operator|=
name|fileA
operator|.
name|next
argument_list|(
name|i
argument_list|)
operator|,
name|nextA
operator|++
operator|,
name|nextB
operator|++
control|)
block|{
if|if
condition|(
name|ignoreWS
operator|&&
name|fileB
operator|.
name|contains
argument_list|(
name|nextB
argument_list|)
condition|)
block|{
if|if
condition|(
name|e
operator|==
literal|null
operator|||
name|e
operator|.
name|common
operator|==
literal|null
condition|)
block|{
name|e
operator|=
name|entry
argument_list|()
expr_stmt|;
name|e
operator|.
name|a
operator|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|end
operator|-
name|nextA
argument_list|)
expr_stmt|;
name|e
operator|.
name|b
operator|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|end
operator|-
name|nextA
argument_list|)
expr_stmt|;
name|e
operator|.
name|common
operator|=
literal|true
expr_stmt|;
block|}
name|e
operator|.
name|a
operator|.
name|add
argument_list|(
name|fileA
operator|.
name|get
argument_list|(
name|nextA
argument_list|)
argument_list|)
expr_stmt|;
name|e
operator|.
name|b
operator|.
name|add
argument_list|(
name|fileB
operator|.
name|get
argument_list|(
name|nextB
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|e
operator|==
literal|null
operator|||
name|e
operator|.
name|common
operator|!=
literal|null
condition|)
block|{
name|e
operator|=
name|entry
argument_list|()
expr_stmt|;
name|e
operator|.
name|ab
operator|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|end
operator|-
name|nextA
argument_list|)
expr_stmt|;
block|}
name|e
operator|.
name|ab
operator|.
name|add
argument_list|(
name|fileA
operator|.
name|get
argument_list|(
name|nextA
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|addDiff (int endA, int endB, List<Edit> internalEdit)
name|void
name|addDiff
parameter_list|(
name|int
name|endA
parameter_list|,
name|int
name|endB
parameter_list|,
name|List
argument_list|<
name|Edit
argument_list|>
name|internalEdit
parameter_list|)
block|{
name|int
name|lenA
init|=
name|endA
operator|-
name|nextA
decl_stmt|;
name|int
name|lenB
init|=
name|endB
operator|-
name|nextB
decl_stmt|;
name|checkState
argument_list|(
name|lenA
operator|>
literal|0
operator|||
name|lenB
operator|>
literal|0
argument_list|)
expr_stmt|;
name|ContentEntry
name|e
init|=
name|entry
argument_list|()
decl_stmt|;
if|if
condition|(
name|lenA
operator|>
literal|0
condition|)
block|{
name|e
operator|.
name|a
operator|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|lenA
argument_list|)
expr_stmt|;
for|for
control|(
init|;
name|nextA
operator|<
name|endA
condition|;
name|nextA
operator|++
control|)
block|{
name|e
operator|.
name|a
operator|.
name|add
argument_list|(
name|fileA
operator|.
name|get
argument_list|(
name|nextA
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|lenB
operator|>
literal|0
condition|)
block|{
name|e
operator|.
name|b
operator|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|lenB
argument_list|)
expr_stmt|;
for|for
control|(
init|;
name|nextB
operator|<
name|endB
condition|;
name|nextB
operator|++
control|)
block|{
name|e
operator|.
name|b
operator|.
name|add
argument_list|(
name|fileB
operator|.
name|get
argument_list|(
name|nextB
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|internalEdit
operator|!=
literal|null
operator|&&
operator|!
name|internalEdit
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|e
operator|.
name|editA
operator|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|internalEdit
operator|.
name|size
argument_list|()
operator|*
literal|2
argument_list|)
expr_stmt|;
name|e
operator|.
name|editB
operator|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|internalEdit
operator|.
name|size
argument_list|()
operator|*
literal|2
argument_list|)
expr_stmt|;
name|int
name|lastA
init|=
literal|0
decl_stmt|;
name|int
name|lastB
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Edit
name|edit
range|:
name|internalEdit
control|)
block|{
if|if
condition|(
name|edit
operator|.
name|getBeginA
argument_list|()
operator|!=
name|edit
operator|.
name|getEndA
argument_list|()
condition|)
block|{
name|e
operator|.
name|editA
operator|.
name|add
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|edit
operator|.
name|getBeginA
argument_list|()
operator|-
name|lastA
argument_list|,
name|edit
operator|.
name|getEndA
argument_list|()
operator|-
name|edit
operator|.
name|getBeginA
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|lastA
operator|=
name|edit
operator|.
name|getEndA
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|edit
operator|.
name|getBeginB
argument_list|()
operator|!=
name|edit
operator|.
name|getEndB
argument_list|()
condition|)
block|{
name|e
operator|.
name|editB
operator|.
name|add
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|edit
operator|.
name|getBeginB
argument_list|()
operator|-
name|lastB
argument_list|,
name|edit
operator|.
name|getEndB
argument_list|()
operator|-
name|edit
operator|.
name|getBeginB
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|lastB
operator|=
name|edit
operator|.
name|getEndB
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|entry ()
specifier|private
name|ContentEntry
name|entry
parameter_list|()
block|{
name|ContentEntry
name|e
init|=
operator|new
name|ContentEntry
argument_list|()
decl_stmt|;
name|lines
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return
name|e
return|;
block|}
block|}
DECL|enum|IgnoreWhitespace
enum|enum
name|IgnoreWhitespace
block|{
DECL|enumConstant|NONE
name|NONE
parameter_list|(
name|AccountDiffPreference
operator|.
name|Whitespace
operator|.
name|IGNORE_NONE
parameter_list|)
operator|,
DECL|enumConstant|TRAILING
constructor|TRAILING(AccountDiffPreference.Whitespace.IGNORE_SPACE_AT_EOL
block|)
enum|,
DECL|enumConstant|CHANGED
name|CHANGED
parameter_list|(
name|AccountDiffPreference
operator|.
name|Whitespace
operator|.
name|IGNORE_SPACE_CHANGE
parameter_list|)
operator|,
DECL|enumConstant|ALL
constructor|ALL(AccountDiffPreference.Whitespace.IGNORE_ALL_SPACE
block|)
class|;
end_class

begin_decl_stmt
DECL|field|whitespace
specifier|private
specifier|final
name|AccountDiffPreference
operator|.
name|Whitespace
name|whitespace
decl_stmt|;
end_decl_stmt

begin_constructor
DECL|method|IgnoreWhitespace (AccountDiffPreference.Whitespace whitespace)
specifier|private
name|IgnoreWhitespace
parameter_list|(
name|AccountDiffPreference
operator|.
name|Whitespace
name|whitespace
parameter_list|)
block|{
name|this
operator|.
name|whitespace
operator|=
name|whitespace
expr_stmt|;
block|}
end_constructor

begin_class
unit|}    static
DECL|class|ContentEntry
specifier|final
class|class
name|ContentEntry
block|{
comment|// Common lines to both sides.
DECL|field|ab
name|List
argument_list|<
name|String
argument_list|>
name|ab
decl_stmt|;
comment|// Lines of a.
DECL|field|a
name|List
argument_list|<
name|String
argument_list|>
name|a
decl_stmt|;
comment|// Lines of b.
DECL|field|b
name|List
argument_list|<
name|String
argument_list|>
name|b
decl_stmt|;
comment|// A list of changed sections of the corresponding line list.
comment|// Each entry is a character<offset, length> pair. The offset is from the
comment|// beginning of the first line in the list. Also, the offset includes an
comment|// implied trailing newline character for each line.
DECL|field|editA
name|List
argument_list|<
name|List
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|editA
decl_stmt|;
DECL|field|editB
name|List
argument_list|<
name|List
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|editB
decl_stmt|;
comment|// a and b are actually common with this whitespace ignore setting.
DECL|field|common
name|Boolean
name|common
decl_stmt|;
comment|// Number of lines to skip on both sides.
DECL|field|skip
name|Integer
name|skip
decl_stmt|;
block|}
end_class

begin_class
DECL|class|ContextOptionHandler
specifier|public
specifier|static
class|class
name|ContextOptionHandler
extends|extends
name|OptionHandler
argument_list|<
name|Short
argument_list|>
block|{
DECL|method|ContextOptionHandler ( CmdLineParser parser, OptionDef option, Setter<Short> setter)
specifier|public
name|ContextOptionHandler
parameter_list|(
name|CmdLineParser
name|parser
parameter_list|,
name|OptionDef
name|option
parameter_list|,
name|Setter
argument_list|<
name|Short
argument_list|>
name|setter
parameter_list|)
block|{
name|super
argument_list|(
name|parser
argument_list|,
name|option
argument_list|,
name|setter
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|parseArguments (final Parameters params)
specifier|public
specifier|final
name|int
name|parseArguments
parameter_list|(
specifier|final
name|Parameters
name|params
parameter_list|)
throws|throws
name|CmdLineException
block|{
specifier|final
name|String
name|value
init|=
name|params
operator|.
name|getParameter
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|short
name|context
decl_stmt|;
if|if
condition|(
literal|"all"
operator|.
name|equalsIgnoreCase
argument_list|(
name|value
argument_list|)
condition|)
block|{
name|context
operator|=
name|AccountDiffPreference
operator|.
name|WHOLE_FILE_CONTEXT
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|context
operator|=
name|Short
operator|.
name|parseShort
argument_list|(
name|value
argument_list|,
literal|10
argument_list|)
expr_stmt|;
if|if
condition|(
name|context
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|NumberFormatException
argument_list|()
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CmdLineException
argument_list|(
name|owner
argument_list|,
name|String
operator|.
name|format
argument_list|(
literal|"\"%s\" is not a valid value for \"%s\""
argument_list|,
name|value
argument_list|,
operator|(
operator|(
name|NamedOptionDef
operator|)
name|option
operator|)
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
name|setter
operator|.
name|addValue
argument_list|(
name|context
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|getDefaultMetaVariable ()
specifier|public
specifier|final
name|String
name|getDefaultMetaVariable
parameter_list|()
block|{
return|return
literal|"ALL|# LINES"
return|;
block|}
block|}
end_class

unit|}
end_unit

