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
DECL|package|com.google.gerrit.server.schema
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|schema
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
name|ChangeMessage
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
name|gwtorm
operator|.
name|server
operator|.
name|ResultSet
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
name|LinkedList
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

begin_import
import|import
name|javax
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_class
DECL|class|Schema_60
specifier|public
class|class
name|Schema_60
extends|extends
name|SchemaVersion
block|{
annotation|@
name|Inject
DECL|method|Schema_60 (Provider<Schema_59> prior)
name|Schema_60
parameter_list|(
name|Provider
argument_list|<
name|Schema_59
argument_list|>
name|prior
parameter_list|)
block|{
name|super
argument_list|(
name|prior
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|migrateData (ReviewDb db, UpdateUI ui)
specifier|protected
name|void
name|migrateData
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|UpdateUI
name|ui
parameter_list|)
throws|throws
name|OrmException
block|{
name|Pattern
name|patternA
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"Patch Set ([0-9]+):.*"
argument_list|,
name|Pattern
operator|.
name|DOTALL
argument_list|)
decl_stmt|;
name|Pattern
name|patternB
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"Uploaded patch set ([0-9]+)."
argument_list|)
decl_stmt|;
name|ResultSet
argument_list|<
name|ChangeMessage
argument_list|>
name|results
init|=
name|db
operator|.
name|changeMessages
argument_list|()
operator|.
name|all
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ChangeMessage
argument_list|>
name|updates
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ChangeMessage
name|cm
range|:
name|results
control|)
block|{
name|Change
operator|.
name|Id
name|id
init|=
name|cm
operator|.
name|getKey
argument_list|()
operator|.
name|getParentKey
argument_list|()
decl_stmt|;
name|String
name|msg
init|=
name|cm
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|Matcher
name|matcherA
init|=
name|patternA
operator|.
name|matcher
argument_list|(
name|msg
argument_list|)
decl_stmt|;
name|Matcher
name|matcherB
init|=
name|patternB
operator|.
name|matcher
argument_list|(
name|msg
argument_list|)
decl_stmt|;
name|PatchSet
operator|.
name|Id
name|newId
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|matcherA
operator|.
name|matches
argument_list|()
condition|)
block|{
name|int
name|patchSetNum
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|matcherA
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|newId
operator|=
operator|new
name|PatchSet
operator|.
name|Id
argument_list|(
name|id
argument_list|,
name|patchSetNum
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|matcherB
operator|.
name|matches
argument_list|()
condition|)
block|{
name|int
name|patchSetNum
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|matcherB
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|newId
operator|=
operator|new
name|PatchSet
operator|.
name|Id
argument_list|(
name|id
argument_list|,
name|patchSetNum
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|newId
operator|!=
literal|null
condition|)
block|{
name|cm
operator|.
name|setPatchSetId
argument_list|(
name|newId
argument_list|)
expr_stmt|;
name|updates
operator|.
name|add
argument_list|(
name|cm
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|updates
operator|.
name|size
argument_list|()
operator|>=
literal|100
condition|)
block|{
name|db
operator|.
name|changeMessages
argument_list|()
operator|.
name|update
argument_list|(
name|updates
argument_list|)
expr_stmt|;
name|updates
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|updates
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|db
operator|.
name|changeMessages
argument_list|()
operator|.
name|update
argument_list|(
name|updates
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

