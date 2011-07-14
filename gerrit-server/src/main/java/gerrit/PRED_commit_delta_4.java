begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2011 The Android Open Source Project
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
DECL|package|gerrit
package|package
name|gerrit
package|;
end_package

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
name|PatchSetInfo
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
name|rules
operator|.
name|StoredValues
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
name|PatchList
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
name|PatchListEntry
import|;
end_import

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|prolog_cafe
operator|.
name|lang
operator|.
name|IllegalTypeException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|prolog_cafe
operator|.
name|lang
operator|.
name|JavaObjectTerm
import|;
end_import

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|prolog_cafe
operator|.
name|lang
operator|.
name|Operation
import|;
end_import

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|prolog_cafe
operator|.
name|lang
operator|.
name|PInstantiationException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|prolog_cafe
operator|.
name|lang
operator|.
name|Predicate
import|;
end_import

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|prolog_cafe
operator|.
name|lang
operator|.
name|Prolog
import|;
end_import

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|prolog_cafe
operator|.
name|lang
operator|.
name|PrologException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|prolog_cafe
operator|.
name|lang
operator|.
name|SymbolTerm
import|;
end_import

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|prolog_cafe
operator|.
name|lang
operator|.
name|Term
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
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  * Given a regular expression, checks it against the file list in the most  * recent patchset of a change. For all files that match the regex, returns the  * (new) path of the file, the change type, and the old path of the file if  * applicable (if the file was copied or renamed).  *  *<pre>  *   'commit_delta'(+Regex, -ChangeType, -NewPath, -OldPath)  *</pre>  */
end_comment

begin_class
DECL|class|PRED_commit_delta_4
specifier|public
class|class
name|PRED_commit_delta_4
extends|extends
name|Predicate
operator|.
name|P4
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
DECL|field|add
specifier|private
specifier|static
specifier|final
name|SymbolTerm
name|add
init|=
name|SymbolTerm
operator|.
name|intern
argument_list|(
literal|"add"
argument_list|)
decl_stmt|;
DECL|field|modify
specifier|private
specifier|static
specifier|final
name|SymbolTerm
name|modify
init|=
name|SymbolTerm
operator|.
name|intern
argument_list|(
literal|"modify"
argument_list|)
decl_stmt|;
DECL|field|delete
specifier|private
specifier|static
specifier|final
name|SymbolTerm
name|delete
init|=
name|SymbolTerm
operator|.
name|intern
argument_list|(
literal|"delete"
argument_list|)
decl_stmt|;
DECL|field|rename
specifier|private
specifier|static
specifier|final
name|SymbolTerm
name|rename
init|=
name|SymbolTerm
operator|.
name|intern
argument_list|(
literal|"rename"
argument_list|)
decl_stmt|;
DECL|field|copy
specifier|private
specifier|static
specifier|final
name|SymbolTerm
name|copy
init|=
name|SymbolTerm
operator|.
name|intern
argument_list|(
literal|"copy"
argument_list|)
decl_stmt|;
DECL|field|commit_delta_check
specifier|static
specifier|final
name|Operation
name|commit_delta_check
init|=
operator|new
name|PRED_commit_delta_check
argument_list|()
decl_stmt|;
DECL|field|commit_delta_next
specifier|static
specifier|final
name|Operation
name|commit_delta_next
init|=
operator|new
name|PRED_commit_delta_next
argument_list|()
decl_stmt|;
DECL|field|commit_delta_empty
specifier|static
specifier|final
name|Operation
name|commit_delta_empty
init|=
operator|new
name|PRED_commit_delta_empty
argument_list|()
decl_stmt|;
DECL|method|PRED_commit_delta_4 (Term a1, Term a2, Term a3, Term a4, Operation n)
specifier|public
name|PRED_commit_delta_4
parameter_list|(
name|Term
name|a1
parameter_list|,
name|Term
name|a2
parameter_list|,
name|Term
name|a3
parameter_list|,
name|Term
name|a4
parameter_list|,
name|Operation
name|n
parameter_list|)
block|{
name|arg1
operator|=
name|a1
expr_stmt|;
name|arg2
operator|=
name|a2
expr_stmt|;
name|arg3
operator|=
name|a3
expr_stmt|;
name|arg4
operator|=
name|a4
expr_stmt|;
name|cont
operator|=
name|n
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|exec (Prolog engine)
specifier|public
name|Operation
name|exec
parameter_list|(
name|Prolog
name|engine
parameter_list|)
throws|throws
name|PrologException
block|{
name|engine
operator|.
name|cont
operator|=
name|cont
expr_stmt|;
name|engine
operator|.
name|setB0
argument_list|()
expr_stmt|;
name|Term
name|a1
init|=
name|arg1
operator|.
name|dereference
argument_list|()
decl_stmt|;
if|if
condition|(
name|a1
operator|.
name|isVariable
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|PInstantiationException
argument_list|(
name|this
argument_list|,
literal|1
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|a1
operator|.
name|isSymbol
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalTypeException
argument_list|(
name|this
argument_list|,
literal|1
argument_list|,
literal|"symbol"
argument_list|,
name|a1
argument_list|)
throw|;
block|}
name|Pattern
name|regex
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|a1
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|engine
operator|.
name|areg1
operator|=
operator|new
name|JavaObjectTerm
argument_list|(
name|regex
argument_list|)
expr_stmt|;
name|engine
operator|.
name|areg2
operator|=
name|arg2
expr_stmt|;
name|engine
operator|.
name|areg3
operator|=
name|arg3
expr_stmt|;
name|engine
operator|.
name|areg4
operator|=
name|arg4
expr_stmt|;
name|PatchSetInfo
name|psInfo
init|=
name|StoredValues
operator|.
name|PATCH_SET_INFO
operator|.
name|get
argument_list|(
name|engine
argument_list|)
decl_stmt|;
name|PatchList
name|pl
init|=
name|StoredValues
operator|.
name|PATCH_LIST
operator|.
name|get
argument_list|(
name|engine
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|PatchListEntry
argument_list|>
name|iter
init|=
name|pl
operator|.
name|getPatches
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|engine
operator|.
name|areg5
operator|=
operator|new
name|JavaObjectTerm
argument_list|(
name|iter
argument_list|)
expr_stmt|;
return|return
name|engine
operator|.
name|jtry5
argument_list|(
name|commit_delta_check
argument_list|,
name|commit_delta_next
argument_list|)
return|;
block|}
DECL|class|PRED_commit_delta_check
specifier|private
specifier|static
specifier|final
class|class
name|PRED_commit_delta_check
extends|extends
name|Operation
block|{
annotation|@
name|Override
DECL|method|exec (Prolog engine)
specifier|public
name|Operation
name|exec
parameter_list|(
name|Prolog
name|engine
parameter_list|)
block|{
name|Term
name|a1
init|=
name|engine
operator|.
name|areg1
decl_stmt|;
name|Term
name|a2
init|=
name|engine
operator|.
name|areg2
decl_stmt|;
name|Term
name|a3
init|=
name|engine
operator|.
name|areg3
decl_stmt|;
name|Term
name|a4
init|=
name|engine
operator|.
name|areg4
decl_stmt|;
name|Term
name|a5
init|=
name|engine
operator|.
name|areg5
decl_stmt|;
name|Pattern
name|regex
init|=
call|(
name|Pattern
call|)
argument_list|(
operator|(
name|JavaObjectTerm
operator|)
name|a1
argument_list|)
operator|.
name|object
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|PatchListEntry
argument_list|>
name|iter
init|=
call|(
name|Iterator
argument_list|<
name|PatchListEntry
argument_list|>
call|)
argument_list|(
operator|(
name|JavaObjectTerm
operator|)
name|a5
argument_list|)
operator|.
name|object
argument_list|()
decl_stmt|;
if|if
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|PatchListEntry
name|patch
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|newName
init|=
name|patch
operator|.
name|getNewName
argument_list|()
decl_stmt|;
name|String
name|oldName
init|=
name|patch
operator|.
name|getOldName
argument_list|()
decl_stmt|;
name|Patch
operator|.
name|ChangeType
name|changeType
init|=
name|patch
operator|.
name|getChangeType
argument_list|()
decl_stmt|;
if|if
condition|(
name|regex
operator|.
name|matcher
argument_list|(
name|newName
argument_list|)
operator|.
name|matches
argument_list|()
operator|||
operator|(
name|oldName
operator|!=
literal|null
operator|&&
name|regex
operator|.
name|matcher
argument_list|(
name|oldName
argument_list|)
operator|.
name|matches
argument_list|()
operator|)
condition|)
block|{
name|SymbolTerm
name|changeSym
init|=
name|getTypeSymbol
argument_list|(
name|changeType
argument_list|)
decl_stmt|;
name|SymbolTerm
name|newSym
init|=
name|SymbolTerm
operator|.
name|create
argument_list|(
name|newName
argument_list|)
decl_stmt|;
name|SymbolTerm
name|oldSym
init|=
name|Prolog
operator|.
name|Nil
decl_stmt|;
if|if
condition|(
name|oldName
operator|!=
literal|null
condition|)
block|{
name|oldSym
operator|=
name|SymbolTerm
operator|.
name|create
argument_list|(
name|oldName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|a2
operator|.
name|unify
argument_list|(
name|changeSym
argument_list|,
name|engine
operator|.
name|trail
argument_list|)
condition|)
block|{
return|return
name|engine
operator|.
name|fail
argument_list|()
return|;
block|}
if|if
condition|(
operator|!
name|a3
operator|.
name|unify
argument_list|(
name|newSym
argument_list|,
name|engine
operator|.
name|trail
argument_list|)
condition|)
block|{
return|return
name|engine
operator|.
name|fail
argument_list|()
return|;
block|}
if|if
condition|(
operator|!
name|a4
operator|.
name|unify
argument_list|(
name|oldSym
argument_list|,
name|engine
operator|.
name|trail
argument_list|)
condition|)
block|{
return|return
name|engine
operator|.
name|fail
argument_list|()
return|;
block|}
return|return
name|engine
operator|.
name|cont
return|;
block|}
block|}
return|return
name|engine
operator|.
name|fail
argument_list|()
return|;
block|}
block|}
DECL|class|PRED_commit_delta_next
specifier|private
specifier|static
specifier|final
class|class
name|PRED_commit_delta_next
extends|extends
name|Operation
block|{
annotation|@
name|Override
DECL|method|exec (Prolog engine)
specifier|public
name|Operation
name|exec
parameter_list|(
name|Prolog
name|engine
parameter_list|)
block|{
return|return
name|engine
operator|.
name|trust
argument_list|(
name|commit_delta_empty
argument_list|)
return|;
block|}
block|}
DECL|class|PRED_commit_delta_empty
specifier|private
specifier|static
specifier|final
class|class
name|PRED_commit_delta_empty
extends|extends
name|Operation
block|{
annotation|@
name|Override
DECL|method|exec (Prolog engine)
specifier|public
name|Operation
name|exec
parameter_list|(
name|Prolog
name|engine
parameter_list|)
block|{
name|Term
name|a5
init|=
name|engine
operator|.
name|areg5
decl_stmt|;
name|Iterator
argument_list|<
name|PatchListEntry
argument_list|>
name|iter
init|=
call|(
name|Iterator
argument_list|<
name|PatchListEntry
argument_list|>
call|)
argument_list|(
operator|(
name|JavaObjectTerm
operator|)
name|a5
argument_list|)
operator|.
name|object
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
name|engine
operator|.
name|fail
argument_list|()
return|;
block|}
return|return
name|engine
operator|.
name|jtry5
argument_list|(
name|commit_delta_check
argument_list|,
name|commit_delta_next
argument_list|)
return|;
block|}
block|}
DECL|method|getTypeSymbol (Patch.ChangeType type)
specifier|private
specifier|static
name|SymbolTerm
name|getTypeSymbol
parameter_list|(
name|Patch
operator|.
name|ChangeType
name|type
parameter_list|)
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|ADDED
case|:
return|return
name|add
return|;
case|case
name|MODIFIED
case|:
return|return
name|modify
return|;
case|case
name|DELETED
case|:
return|return
name|delete
return|;
case|case
name|RENAMED
case|:
return|return
name|rename
return|;
case|case
name|COPIED
case|:
return|return
name|copy
return|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"ChangeType not recognized"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

