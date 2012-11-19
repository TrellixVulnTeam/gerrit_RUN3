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
DECL|package|com.google.gerrit.server.query.change
package|package
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
name|changes
operator|.
name|ListChangesOption
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
name|BinaryResult
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
name|TopLevelResource
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
name|OutputFormat
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
name|ChangeJson
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
name|ChangeJson
operator|.
name|ChangeInfo
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
name|ssh
operator|.
name|SshInfo
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
name|util
operator|.
name|BitSet
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
name|EnumSet
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
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_class
DECL|class|QueryChanges
specifier|public
class|class
name|QueryChanges
implements|implements
name|RestReadView
argument_list|<
name|TopLevelResource
argument_list|>
block|{
DECL|field|json
specifier|private
specifier|final
name|ChangeJson
name|json
decl_stmt|;
DECL|field|imp
specifier|private
specifier|final
name|QueryProcessor
name|imp
decl_stmt|;
DECL|field|reverse
specifier|private
name|boolean
name|reverse
decl_stmt|;
DECL|field|options
specifier|private
name|EnumSet
argument_list|<
name|ListChangesOption
argument_list|>
name|options
decl_stmt|;
annotation|@
name|Deprecated
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--format"
argument_list|,
name|usage
operator|=
literal|"(deprecated) output format"
argument_list|)
DECL|field|format
specifier|private
name|OutputFormat
name|format
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--query"
argument_list|,
name|aliases
operator|=
block|{
literal|"-q"
block|}
argument_list|,
name|metaVar
operator|=
literal|"QUERY"
argument_list|,
name|multiValued
operator|=
literal|true
argument_list|,
name|usage
operator|=
literal|"Query string"
argument_list|)
DECL|field|queries
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|queries
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--limit"
argument_list|,
name|aliases
operator|=
block|{
literal|"-n"
block|}
argument_list|,
name|metaVar
operator|=
literal|"CNT"
argument_list|,
name|usage
operator|=
literal|"Maximum number of results to return"
argument_list|)
DECL|method|setLimit (int limit)
specifier|public
name|void
name|setLimit
parameter_list|(
name|int
name|limit
parameter_list|)
block|{
name|imp
operator|.
name|setLimit
argument_list|(
name|limit
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"-o"
argument_list|,
name|multiValued
operator|=
literal|true
argument_list|,
name|usage
operator|=
literal|"Output options per change"
argument_list|)
DECL|method|addOption (ListChangesOption o)
specifier|public
name|void
name|addOption
parameter_list|(
name|ListChangesOption
name|o
parameter_list|)
block|{
name|options
operator|.
name|add
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"-O"
argument_list|,
name|usage
operator|=
literal|"Output option flags, in hex"
argument_list|)
DECL|method|setOptionFlagsHex (String hex)
name|void
name|setOptionFlagsHex
parameter_list|(
name|String
name|hex
parameter_list|)
block|{
name|options
operator|.
name|addAll
argument_list|(
name|ListChangesOption
operator|.
name|fromBits
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|hex
argument_list|,
literal|16
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"-P"
argument_list|,
name|metaVar
operator|=
literal|"SORTKEY"
argument_list|,
name|usage
operator|=
literal|"Previous changes before SORTKEY"
argument_list|)
DECL|method|setSortKeyAfter (String key)
specifier|public
name|void
name|setSortKeyAfter
parameter_list|(
name|String
name|key
parameter_list|)
block|{
comment|// Querying for the prior page of changes requires sortkey_after predicate.
comment|// Changes are shown most recent->least recent. The previous page of
comment|// results contains changes that were updated after the given key.
name|imp
operator|.
name|setSortkeyAfter
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|reverse
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"-N"
argument_list|,
name|metaVar
operator|=
literal|"SORTKEY"
argument_list|,
name|usage
operator|=
literal|"Next changes after SORTKEY"
argument_list|)
DECL|method|setSortKeyBefore (String key)
specifier|public
name|void
name|setSortKeyBefore
parameter_list|(
name|String
name|key
parameter_list|)
block|{
comment|// Querying for the next page of changes requires sortkey_before predicate.
comment|// Changes are shown most recent->least recent. The next page contains
comment|// changes that were updated before the given key.
name|imp
operator|.
name|setSortkeyBefore
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Inject
DECL|method|QueryChanges (ChangeJson json, QueryProcessor qp, SshInfo sshInfo, ChangeControl.Factory cf)
name|QueryChanges
parameter_list|(
name|ChangeJson
name|json
parameter_list|,
name|QueryProcessor
name|qp
parameter_list|,
name|SshInfo
name|sshInfo
parameter_list|,
name|ChangeControl
operator|.
name|Factory
name|cf
parameter_list|)
block|{
name|this
operator|.
name|json
operator|=
name|json
expr_stmt|;
name|this
operator|.
name|imp
operator|=
name|qp
expr_stmt|;
name|options
operator|=
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|ListChangesOption
operator|.
name|class
argument_list|)
expr_stmt|;
name|json
operator|.
name|setSshInfo
argument_list|(
name|sshInfo
argument_list|)
expr_stmt|;
name|json
operator|.
name|setChangeControlFactory
argument_list|(
name|cf
argument_list|)
expr_stmt|;
block|}
DECL|method|addQuery (String query)
specifier|public
name|void
name|addQuery
parameter_list|(
name|String
name|query
parameter_list|)
block|{
if|if
condition|(
name|queries
operator|==
literal|null
condition|)
block|{
name|queries
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|()
expr_stmt|;
block|}
name|queries
operator|.
name|add
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (TopLevelResource rsrc)
specifier|public
name|Object
name|apply
parameter_list|(
name|TopLevelResource
name|rsrc
parameter_list|)
throws|throws
name|BadRequestException
throws|,
name|AuthException
throws|,
name|OrmException
block|{
name|List
argument_list|<
name|List
argument_list|<
name|ChangeInfo
argument_list|>
argument_list|>
name|out
decl_stmt|;
try|try
block|{
name|out
operator|=
name|query
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|QueryParseException
name|e
parameter_list|)
block|{
comment|// This is a hack to detect an operator that requires authentication.
name|Pattern
name|p
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^Error in operator (.*:self)$"
argument_list|)
decl_stmt|;
name|Matcher
name|m
init|=
name|p
operator|.
name|matcher
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|matches
argument_list|()
condition|)
block|{
name|String
name|op
init|=
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|AuthException
argument_list|(
literal|"Must be signed-in to use "
operator|+
name|op
argument_list|)
throw|;
block|}
throw|throw
operator|new
name|BadRequestException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|format
operator|==
name|OutputFormat
operator|.
name|TEXT
condition|)
block|{
return|return
name|formatText
argument_list|(
name|out
argument_list|)
return|;
block|}
return|return
name|out
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|?
name|out
operator|.
name|get
argument_list|(
literal|0
argument_list|)
else|:
name|out
return|;
block|}
DECL|method|formatText (List<List<ChangeInfo>> res)
specifier|private
specifier|static
name|BinaryResult
name|formatText
parameter_list|(
name|List
argument_list|<
name|List
argument_list|<
name|ChangeInfo
argument_list|>
argument_list|>
name|res
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|boolean
name|firstQuery
init|=
literal|true
decl_stmt|;
for|for
control|(
name|List
argument_list|<
name|ChangeInfo
argument_list|>
name|info
range|:
name|res
control|)
block|{
if|if
condition|(
name|firstQuery
condition|)
block|{
name|firstQuery
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|ChangeInfo
name|c
range|:
name|info
control|)
block|{
name|String
name|id
init|=
operator|new
name|Change
operator|.
name|Key
argument_list|(
name|c
operator|.
name|changeId
argument_list|)
operator|.
name|abbreviate
argument_list|()
decl_stmt|;
name|String
name|subject
init|=
name|c
operator|.
name|subject
decl_stmt|;
if|if
condition|(
name|subject
operator|.
name|length
argument_list|()
operator|+
name|id
operator|.
name|length
argument_list|()
operator|>
literal|80
condition|)
block|{
name|subject
operator|=
name|subject
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|80
operator|-
name|id
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|subject
operator|.
name|replace
argument_list|(
literal|'\n'
argument_list|,
literal|' '
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|BinaryResult
operator|.
name|create
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
DECL|method|query ()
specifier|private
name|List
argument_list|<
name|List
argument_list|<
name|ChangeInfo
argument_list|>
argument_list|>
name|query
parameter_list|()
throws|throws
name|OrmException
throws|,
name|QueryParseException
block|{
if|if
condition|(
name|imp
operator|.
name|isDisabled
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|QueryParseException
argument_list|(
literal|"query disabled"
argument_list|)
throw|;
block|}
if|if
condition|(
name|queries
operator|==
literal|null
operator|||
name|queries
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|queries
operator|=
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"status:open"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|queries
operator|.
name|size
argument_list|()
operator|>
literal|10
condition|)
block|{
comment|// Hard-code a default maximum number of queries to prevent
comment|// users from submitting too much to the server in a single call.
throw|throw
operator|new
name|QueryParseException
argument_list|(
literal|"limit of 10 queries"
argument_list|)
throw|;
block|}
name|int
name|cnt
init|=
name|queries
operator|.
name|size
argument_list|()
decl_stmt|;
name|BitSet
name|more
init|=
operator|new
name|BitSet
argument_list|(
name|cnt
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|List
argument_list|<
name|ChangeData
argument_list|>
argument_list|>
name|data
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|cnt
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|n
init|=
literal|0
init|;
name|n
operator|<
name|cnt
condition|;
name|n
operator|++
control|)
block|{
name|String
name|query
init|=
name|queries
operator|.
name|get
argument_list|(
name|n
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ChangeData
argument_list|>
name|changes
init|=
name|imp
operator|.
name|queryChanges
argument_list|(
name|query
argument_list|)
decl_stmt|;
if|if
condition|(
name|imp
operator|.
name|getLimit
argument_list|()
operator|>
literal|0
operator|&&
name|changes
operator|.
name|size
argument_list|()
operator|>
name|imp
operator|.
name|getLimit
argument_list|()
condition|)
block|{
if|if
condition|(
name|reverse
condition|)
block|{
name|changes
operator|=
name|changes
operator|.
name|subList
argument_list|(
literal|1
argument_list|,
name|changes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|changes
operator|=
name|changes
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|imp
operator|.
name|getLimit
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|more
operator|.
name|set
argument_list|(
name|n
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|data
operator|.
name|add
argument_list|(
name|changes
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|List
argument_list|<
name|ChangeInfo
argument_list|>
argument_list|>
name|res
init|=
name|json
operator|.
name|addOptions
argument_list|(
name|options
argument_list|)
operator|.
name|formatList2
argument_list|(
name|data
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|n
init|=
literal|0
init|;
name|n
operator|<
name|cnt
condition|;
name|n
operator|++
control|)
block|{
name|List
argument_list|<
name|ChangeInfo
argument_list|>
name|info
init|=
name|res
operator|.
name|get
argument_list|(
name|n
argument_list|)
decl_stmt|;
if|if
condition|(
name|more
operator|.
name|get
argument_list|(
name|n
argument_list|)
operator|&&
operator|!
name|info
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|reverse
condition|)
block|{
name|info
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|_moreChanges
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|info
operator|.
name|get
argument_list|(
name|info
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|_moreChanges
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
return|return
name|res
return|;
block|}
block|}
end_class

end_unit

