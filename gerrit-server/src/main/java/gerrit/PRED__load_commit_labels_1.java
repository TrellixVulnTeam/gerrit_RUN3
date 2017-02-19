begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright 2011 Google Inc. All Rights Reserved.
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
name|common
operator|.
name|data
operator|.
name|LabelType
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
name|LabelTypes
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
name|PatchSetApproval
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
name|query
operator|.
name|change
operator|.
name|ChangeData
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
name|googlecode
operator|.
name|prolog_cafe
operator|.
name|exceptions
operator|.
name|JavaException
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
name|exceptions
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
name|ListTerm
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
comment|/** Exports list of {@code commit_label( label('Code-Review', 2), user(12345789) )}. */
end_comment

begin_class
DECL|class|PRED__load_commit_labels_1
class|class
name|PRED__load_commit_labels_1
extends|extends
name|Predicate
operator|.
name|P1
block|{
DECL|field|sym_commit_label
specifier|private
specifier|static
specifier|final
name|SymbolTerm
name|sym_commit_label
init|=
name|SymbolTerm
operator|.
name|intern
argument_list|(
literal|"commit_label"
argument_list|,
literal|2
argument_list|)
decl_stmt|;
DECL|field|sym_label
specifier|private
specifier|static
specifier|final
name|SymbolTerm
name|sym_label
init|=
name|SymbolTerm
operator|.
name|intern
argument_list|(
literal|"label"
argument_list|,
literal|2
argument_list|)
decl_stmt|;
DECL|field|sym_user
specifier|private
specifier|static
specifier|final
name|SymbolTerm
name|sym_user
init|=
name|SymbolTerm
operator|.
name|intern
argument_list|(
literal|"user"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
DECL|method|PRED__load_commit_labels_1 (Term a1, Operation n)
name|PRED__load_commit_labels_1
parameter_list|(
name|Term
name|a1
parameter_list|,
name|Operation
name|n
parameter_list|)
block|{
name|arg1
operator|=
name|a1
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
name|listHead
init|=
name|Prolog
operator|.
name|Nil
decl_stmt|;
try|try
block|{
name|ChangeData
name|cd
init|=
name|StoredValues
operator|.
name|CHANGE_DATA
operator|.
name|get
argument_list|(
name|engine
argument_list|)
decl_stmt|;
name|LabelTypes
name|types
init|=
name|cd
operator|.
name|getLabelTypes
argument_list|()
decl_stmt|;
for|for
control|(
name|PatchSetApproval
name|a
range|:
name|cd
operator|.
name|currentApprovals
argument_list|()
control|)
block|{
name|LabelType
name|t
init|=
name|types
operator|.
name|byLabel
argument_list|(
name|a
operator|.
name|getLabelId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|t
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|StructureTerm
name|labelTerm
init|=
operator|new
name|StructureTerm
argument_list|(
name|sym_label
argument_list|,
name|SymbolTerm
operator|.
name|intern
argument_list|(
name|t
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
operator|new
name|IntegerTerm
argument_list|(
name|a
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|StructureTerm
name|userTerm
init|=
operator|new
name|StructureTerm
argument_list|(
name|sym_user
argument_list|,
operator|new
name|IntegerTerm
argument_list|(
name|a
operator|.
name|getAccountId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|listHead
operator|=
operator|new
name|ListTerm
argument_list|(
operator|new
name|StructureTerm
argument_list|(
name|sym_commit_label
argument_list|,
name|labelTerm
argument_list|,
name|userTerm
argument_list|)
argument_list|,
name|listHead
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|OrmException
name|err
parameter_list|)
block|{
throw|throw
operator|new
name|JavaException
argument_list|(
name|this
argument_list|,
literal|1
argument_list|,
name|err
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|a1
operator|.
name|unify
argument_list|(
name|listHead
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
block|}
end_class

end_unit

