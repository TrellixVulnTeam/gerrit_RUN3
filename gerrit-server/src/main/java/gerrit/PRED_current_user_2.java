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
import|import static
name|com
operator|.
name|googlecode
operator|.
name|prolog_cafe
operator|.
name|lang
operator|.
name|SymbolTerm
operator|.
name|intern
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
name|rules
operator|.
name|PrologEnvironment
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
name|AnonymousUser
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
name|inject
operator|.
name|Provider
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
name|HashtableOfTerm
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
name|IntegerTerm
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
name|InternalException
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
name|StructureTerm
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

begin_comment
comment|/**  * Loads a CurrentUser object for a user identity.  *<p>  * Values are cached in the hash {@code current_user}, avoiding recreation  * during a single evaluation.  *  *<pre>  *   current_user(user(+AccountId), -CurrentUser).  *</pre>  */
end_comment

begin_class
DECL|class|PRED_current_user_2
class|class
name|PRED_current_user_2
extends|extends
name|Predicate
operator|.
name|P2
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
DECL|field|user
specifier|private
specifier|static
specifier|final
name|SymbolTerm
name|user
init|=
name|intern
argument_list|(
literal|"user"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
DECL|field|anonymous
specifier|private
specifier|static
specifier|final
name|SymbolTerm
name|anonymous
init|=
name|intern
argument_list|(
literal|"anonymous"
argument_list|)
decl_stmt|;
DECL|field|current_user
specifier|private
specifier|static
specifier|final
name|SymbolTerm
name|current_user
init|=
name|intern
argument_list|(
literal|"current_user"
argument_list|)
decl_stmt|;
DECL|method|PRED_current_user_2 (Term a1, Term a2, Operation n)
name|PRED_current_user_2
parameter_list|(
name|Term
name|a1
parameter_list|,
name|Term
name|a2
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
name|Term
name|a2
init|=
name|arg2
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
name|HashtableOfTerm
name|userHash
init|=
name|userHash
argument_list|(
name|engine
argument_list|)
decl_stmt|;
name|Term
name|userTerm
init|=
name|userHash
operator|.
name|get
argument_list|(
name|a1
argument_list|)
decl_stmt|;
if|if
condition|(
name|userTerm
operator|!=
literal|null
operator|&&
name|userTerm
operator|.
name|isJavaObject
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
operator|(
operator|(
operator|(
name|JavaObjectTerm
operator|)
name|userTerm
operator|)
operator|.
name|object
argument_list|()
operator|instanceof
name|CurrentUser
operator|)
condition|)
block|{
name|userTerm
operator|=
name|createUser
argument_list|(
name|engine
argument_list|,
name|a1
argument_list|,
name|userHash
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|userTerm
operator|=
name|createUser
argument_list|(
name|engine
argument_list|,
name|a1
argument_list|,
name|userHash
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
name|userTerm
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
name|cont
return|;
block|}
DECL|method|createUser (Prolog engine, Term key, HashtableOfTerm userHash)
specifier|public
name|Term
name|createUser
parameter_list|(
name|Prolog
name|engine
parameter_list|,
name|Term
name|key
parameter_list|,
name|HashtableOfTerm
name|userHash
parameter_list|)
block|{
if|if
condition|(
operator|!
name|key
operator|.
name|isStructure
argument_list|()
operator|||
name|key
operator|.
name|arity
argument_list|()
operator|!=
literal|1
operator|||
operator|!
operator|(
operator|(
name|StructureTerm
operator|)
name|key
operator|)
operator|.
name|functor
argument_list|()
operator|.
name|equals
argument_list|(
name|user
argument_list|)
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
literal|"user(int)"
argument_list|,
name|key
argument_list|)
throw|;
block|}
name|Term
name|idTerm
init|=
name|key
operator|.
name|arg
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|CurrentUser
name|user
decl_stmt|;
if|if
condition|(
name|idTerm
operator|.
name|isInteger
argument_list|()
condition|)
block|{
name|Account
operator|.
name|Id
name|accountId
init|=
operator|new
name|Account
operator|.
name|Id
argument_list|(
operator|(
operator|(
name|IntegerTerm
operator|)
name|idTerm
operator|)
operator|.
name|intValue
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|ReviewDb
name|db
init|=
name|StoredValues
operator|.
name|REVIEW_DB
operator|.
name|getOrNull
argument_list|(
name|engine
argument_list|)
decl_stmt|;
name|IdentifiedUser
operator|.
name|GenericFactory
name|userFactory
init|=
name|userFactory
argument_list|(
name|engine
argument_list|)
decl_stmt|;
if|if
condition|(
name|db
operator|!=
literal|null
condition|)
block|{
name|user
operator|=
name|userFactory
operator|.
name|create
argument_list|(
operator|new
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
argument_list|()
block|{
specifier|public
name|ReviewDb
name|get
parameter_list|()
block|{
return|return
name|db
return|;
block|}
block|}
argument_list|,
name|accountId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|user
operator|=
name|userFactory
operator|.
name|create
argument_list|(
name|accountId
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|idTerm
operator|.
name|equals
argument_list|(
name|anonymous
argument_list|)
condition|)
block|{
name|user
operator|=
name|anonymousUser
argument_list|(
name|engine
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalTypeException
argument_list|(
name|this
argument_list|,
literal|1
argument_list|,
literal|"user(int)"
argument_list|,
name|key
argument_list|)
throw|;
block|}
name|Term
name|userTerm
init|=
operator|new
name|JavaObjectTerm
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|userHash
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|userTerm
argument_list|)
expr_stmt|;
return|return
name|userTerm
return|;
block|}
DECL|method|userHash (Prolog engine)
specifier|private
specifier|static
name|HashtableOfTerm
name|userHash
parameter_list|(
name|Prolog
name|engine
parameter_list|)
block|{
name|Term
name|userHash
init|=
name|engine
operator|.
name|getHashManager
argument_list|()
operator|.
name|get
argument_list|(
name|current_user
argument_list|)
decl_stmt|;
if|if
condition|(
name|userHash
operator|==
literal|null
condition|)
block|{
name|HashtableOfTerm
name|users
init|=
operator|new
name|HashtableOfTerm
argument_list|()
decl_stmt|;
name|engine
operator|.
name|getHashManager
argument_list|()
operator|.
name|put
argument_list|(
name|current_user
argument_list|,
operator|new
name|JavaObjectTerm
argument_list|(
name|userHash
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|users
return|;
block|}
if|if
condition|(
name|userHash
operator|.
name|isJavaObject
argument_list|()
condition|)
block|{
name|Object
name|obj
init|=
operator|(
operator|(
name|JavaObjectTerm
operator|)
name|userHash
operator|)
operator|.
name|object
argument_list|()
decl_stmt|;
if|if
condition|(
name|obj
operator|instanceof
name|HashtableOfTerm
condition|)
block|{
return|return
operator|(
name|HashtableOfTerm
operator|)
name|obj
return|;
block|}
block|}
throw|throw
operator|new
name|InternalException
argument_list|(
name|current_user
operator|+
literal|" is not HashtableOfTerm"
argument_list|)
throw|;
block|}
DECL|method|anonymousUser (Prolog engine)
specifier|private
specifier|static
name|AnonymousUser
name|anonymousUser
parameter_list|(
name|Prolog
name|engine
parameter_list|)
block|{
name|PrologEnvironment
name|env
init|=
operator|(
name|PrologEnvironment
operator|)
name|engine
operator|.
name|control
decl_stmt|;
return|return
name|env
operator|.
name|getInjector
argument_list|()
operator|.
name|getInstance
argument_list|(
name|AnonymousUser
operator|.
name|class
argument_list|)
return|;
block|}
DECL|method|userFactory (Prolog engine)
specifier|private
specifier|static
name|IdentifiedUser
operator|.
name|GenericFactory
name|userFactory
parameter_list|(
name|Prolog
name|engine
parameter_list|)
block|{
name|PrologEnvironment
name|env
init|=
operator|(
name|PrologEnvironment
operator|)
name|engine
operator|.
name|control
decl_stmt|;
return|return
name|env
operator|.
name|getInjector
argument_list|()
operator|.
name|getInstance
argument_list|(
name|IdentifiedUser
operator|.
name|GenericFactory
operator|.
name|class
argument_list|)
return|;
block|}
block|}
end_class

end_unit

