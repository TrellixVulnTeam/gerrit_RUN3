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
DECL|package|com.google.gerrit.server.account
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|account
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
name|checkArgument
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
name|Throwables
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
name|Iterables
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
name|AccountInfo
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
name|server
operator|.
name|account
operator|.
name|AccountDirectory
operator|.
name|DirectoryException
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
name|account
operator|.
name|AccountDirectory
operator|.
name|FillOptions
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
name|assistedinject
operator|.
name|Assisted
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
name|AssistedInject
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
name|Collection
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
name|HashMap
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_class
DECL|class|AccountLoader
specifier|public
class|class
name|AccountLoader
block|{
DECL|field|DETAILED_OPTIONS
specifier|public
specifier|static
specifier|final
name|Set
argument_list|<
name|FillOptions
argument_list|>
name|DETAILED_OPTIONS
init|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|EnumSet
operator|.
name|of
argument_list|(
name|FillOptions
operator|.
name|ID
argument_list|,
name|FillOptions
operator|.
name|NAME
argument_list|,
name|FillOptions
operator|.
name|EMAIL
argument_list|,
name|FillOptions
operator|.
name|USERNAME
argument_list|,
name|FillOptions
operator|.
name|STATUS
argument_list|,
name|FillOptions
operator|.
name|AVATARS
argument_list|)
argument_list|)
decl_stmt|;
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (boolean detailed)
name|AccountLoader
name|create
parameter_list|(
name|boolean
name|detailed
parameter_list|)
function_decl|;
DECL|method|create (Set<FillOptions> options)
name|AccountLoader
name|create
parameter_list|(
name|Set
argument_list|<
name|FillOptions
argument_list|>
name|options
parameter_list|)
function_decl|;
block|}
DECL|field|directory
specifier|private
specifier|final
name|InternalAccountDirectory
name|directory
decl_stmt|;
DECL|field|options
specifier|private
specifier|final
name|Set
argument_list|<
name|FillOptions
argument_list|>
name|options
decl_stmt|;
DECL|field|created
specifier|private
specifier|final
name|Map
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|AccountInfo
argument_list|>
name|created
decl_stmt|;
DECL|field|provided
specifier|private
specifier|final
name|List
argument_list|<
name|AccountInfo
argument_list|>
name|provided
decl_stmt|;
annotation|@
name|AssistedInject
DECL|method|AccountLoader (InternalAccountDirectory directory, @Assisted boolean detailed)
name|AccountLoader
parameter_list|(
name|InternalAccountDirectory
name|directory
parameter_list|,
annotation|@
name|Assisted
name|boolean
name|detailed
parameter_list|)
block|{
name|this
argument_list|(
name|directory
argument_list|,
name|detailed
condition|?
name|DETAILED_OPTIONS
else|:
name|InternalAccountDirectory
operator|.
name|ID_ONLY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AssistedInject
DECL|method|AccountLoader (InternalAccountDirectory directory, @Assisted Set<FillOptions> options)
name|AccountLoader
parameter_list|(
name|InternalAccountDirectory
name|directory
parameter_list|,
annotation|@
name|Assisted
name|Set
argument_list|<
name|FillOptions
argument_list|>
name|options
parameter_list|)
block|{
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
name|this
operator|.
name|options
operator|=
name|options
expr_stmt|;
name|created
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|provided
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
DECL|method|get (Account.Id id)
specifier|public
name|AccountInfo
name|get
parameter_list|(
name|Account
operator|.
name|Id
name|id
parameter_list|)
block|{
if|if
condition|(
name|id
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|AccountInfo
name|info
init|=
name|created
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|==
literal|null
condition|)
block|{
name|info
operator|=
operator|new
name|AccountInfo
argument_list|(
name|id
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|created
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
return|return
name|info
return|;
block|}
DECL|method|put (AccountInfo info)
specifier|public
name|void
name|put
parameter_list|(
name|AccountInfo
name|info
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|info
operator|.
name|_accountId
operator|!=
literal|null
argument_list|,
literal|"_accountId field required"
argument_list|)
expr_stmt|;
name|provided
operator|.
name|add
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
DECL|method|fill ()
specifier|public
name|void
name|fill
parameter_list|()
throws|throws
name|OrmException
block|{
try|try
block|{
name|directory
operator|.
name|fillAccountInfo
argument_list|(
name|Iterables
operator|.
name|concat
argument_list|(
name|created
operator|.
name|values
argument_list|()
argument_list|,
name|provided
argument_list|)
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DirectoryException
name|e
parameter_list|)
block|{
name|Throwables
operator|.
name|throwIfInstanceOf
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|,
name|OrmException
operator|.
name|class
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|OrmException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|fill (Collection<? extends AccountInfo> infos)
specifier|public
name|void
name|fill
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|AccountInfo
argument_list|>
name|infos
parameter_list|)
throws|throws
name|OrmException
block|{
for|for
control|(
name|AccountInfo
name|info
range|:
name|infos
control|)
block|{
name|put
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
name|fill
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

