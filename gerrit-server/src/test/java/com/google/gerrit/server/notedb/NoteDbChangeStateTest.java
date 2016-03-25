begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2016 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.notedb
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|notedb
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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|truth
operator|.
name|Truth
operator|.
name|assertThat
import|;
end_import

begin_import
import|import static
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
name|NoteDbChangeState
operator|.
name|applyDelta
import|;
end_import

begin_import
import|import static
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
name|NoteDbChangeState
operator|.
name|parse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|ObjectId
operator|.
name|zeroId
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
name|collect
operator|.
name|ImmutableMap
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
name|notedb
operator|.
name|NoteDbChangeState
operator|.
name|Delta
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
name|testutil
operator|.
name|TestChanges
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
name|client
operator|.
name|KeyUtil
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
name|StandardKeyEncoder
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/** Unit tests for {@link NoteDbChangeState}. */
end_comment

begin_class
DECL|class|NoteDbChangeStateTest
specifier|public
class|class
name|NoteDbChangeStateTest
block|{
static|static
block|{
name|KeyUtil
operator|.
name|setEncoderImpl
argument_list|(
operator|new
name|StandardKeyEncoder
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|field|SHA1
name|ObjectId
name|SHA1
init|=
name|ObjectId
operator|.
name|fromString
argument_list|(
literal|"deadbeefdeadbeefdeadbeefdeadbeefdeadbeef"
argument_list|)
decl_stmt|;
DECL|field|SHA2
name|ObjectId
name|SHA2
init|=
name|ObjectId
operator|.
name|fromString
argument_list|(
literal|"abcd1234abcd1234abcd1234abcd1234abcd1234"
argument_list|)
decl_stmt|;
DECL|field|SHA3
name|ObjectId
name|SHA3
init|=
name|ObjectId
operator|.
name|fromString
argument_list|(
literal|"badc0feebadc0feebadc0feebadc0feebadc0fee"
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|parseWithoutDrafts ()
specifier|public
name|void
name|parseWithoutDrafts
parameter_list|()
block|{
name|NoteDbChangeState
name|state
init|=
name|parse
argument_list|(
operator|new
name|Change
operator|.
name|Id
argument_list|(
literal|1
argument_list|)
argument_list|,
name|SHA1
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|state
operator|.
name|getChangeId
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
operator|new
name|Change
operator|.
name|Id
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|state
operator|.
name|getChangeMetaId
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|SHA1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|state
operator|.
name|getDraftIds
argument_list|()
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|state
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|SHA1
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|parseWithDrafts ()
specifier|public
name|void
name|parseWithDrafts
parameter_list|()
block|{
name|NoteDbChangeState
name|state
init|=
name|parse
argument_list|(
operator|new
name|Change
operator|.
name|Id
argument_list|(
literal|1
argument_list|)
argument_list|,
name|SHA1
operator|.
name|name
argument_list|()
operator|+
literal|",2003="
operator|+
name|SHA2
operator|.
name|name
argument_list|()
operator|+
literal|",1001="
operator|+
name|SHA3
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|state
operator|.
name|getChangeId
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
operator|new
name|Change
operator|.
name|Id
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|state
operator|.
name|getChangeMetaId
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|SHA1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|state
operator|.
name|getDraftIds
argument_list|()
argument_list|)
operator|.
name|containsExactly
argument_list|(
operator|new
name|Account
operator|.
name|Id
argument_list|(
literal|1001
argument_list|)
argument_list|,
name|SHA3
argument_list|,
operator|new
name|Account
operator|.
name|Id
argument_list|(
literal|2003
argument_list|)
argument_list|,
name|SHA2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|state
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|SHA1
operator|.
name|name
argument_list|()
operator|+
literal|",1001="
operator|+
name|SHA3
operator|.
name|name
argument_list|()
operator|+
literal|",2003="
operator|+
name|SHA2
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|applyDeltaToNullWithNoNewMetaId ()
specifier|public
name|void
name|applyDeltaToNullWithNoNewMetaId
parameter_list|()
block|{
name|Change
name|c
init|=
name|newChange
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|getNoteDbState
argument_list|()
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|applyDelta
argument_list|(
name|c
argument_list|,
name|Delta
operator|.
name|create
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|,
name|noMetaId
argument_list|()
argument_list|,
name|noDrafts
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|getNoteDbState
argument_list|()
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|applyDelta
argument_list|(
name|c
argument_list|,
name|Delta
operator|.
name|create
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|,
name|noMetaId
argument_list|()
argument_list|,
name|drafts
argument_list|(
operator|new
name|Account
operator|.
name|Id
argument_list|(
literal|1001
argument_list|)
argument_list|,
name|zeroId
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|getNoteDbState
argument_list|()
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|applyDeltaToMetaId ()
specifier|public
name|void
name|applyDeltaToMetaId
parameter_list|()
block|{
name|Change
name|c
init|=
name|newChange
argument_list|()
decl_stmt|;
name|applyDelta
argument_list|(
name|c
argument_list|,
name|Delta
operator|.
name|create
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|,
name|metaId
argument_list|(
name|SHA1
argument_list|)
argument_list|,
name|noDrafts
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|getNoteDbState
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|SHA1
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|applyDelta
argument_list|(
name|c
argument_list|,
name|Delta
operator|.
name|create
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|,
name|metaId
argument_list|(
name|SHA2
argument_list|)
argument_list|,
name|noDrafts
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|getNoteDbState
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|SHA2
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
comment|// No-op delta.
name|applyDelta
argument_list|(
name|c
argument_list|,
name|Delta
operator|.
name|create
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|,
name|noMetaId
argument_list|()
argument_list|,
name|noDrafts
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|getNoteDbState
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|SHA2
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
comment|// Set to zero clears the field.
name|applyDelta
argument_list|(
name|c
argument_list|,
name|Delta
operator|.
name|create
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|,
name|metaId
argument_list|(
name|zeroId
argument_list|()
argument_list|)
argument_list|,
name|noDrafts
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|getNoteDbState
argument_list|()
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|applyDeltaToDrafts ()
specifier|public
name|void
name|applyDeltaToDrafts
parameter_list|()
block|{
name|Change
name|c
init|=
name|newChange
argument_list|()
decl_stmt|;
name|applyDelta
argument_list|(
name|c
argument_list|,
name|Delta
operator|.
name|create
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|,
name|metaId
argument_list|(
name|SHA1
argument_list|)
argument_list|,
name|drafts
argument_list|(
operator|new
name|Account
operator|.
name|Id
argument_list|(
literal|1001
argument_list|)
argument_list|,
name|SHA2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|getNoteDbState
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|SHA1
operator|.
name|name
argument_list|()
operator|+
literal|",1001="
operator|+
name|SHA2
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|applyDelta
argument_list|(
name|c
argument_list|,
name|Delta
operator|.
name|create
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|,
name|noMetaId
argument_list|()
argument_list|,
name|drafts
argument_list|(
operator|new
name|Account
operator|.
name|Id
argument_list|(
literal|2003
argument_list|)
argument_list|,
name|SHA3
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|getNoteDbState
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|SHA1
operator|.
name|name
argument_list|()
operator|+
literal|",1001="
operator|+
name|SHA2
operator|.
name|name
argument_list|()
operator|+
literal|",2003="
operator|+
name|SHA3
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|applyDelta
argument_list|(
name|c
argument_list|,
name|Delta
operator|.
name|create
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|,
name|noMetaId
argument_list|()
argument_list|,
name|drafts
argument_list|(
operator|new
name|Account
operator|.
name|Id
argument_list|(
literal|2003
argument_list|)
argument_list|,
name|zeroId
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|getNoteDbState
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|SHA1
operator|.
name|name
argument_list|()
operator|+
literal|",1001="
operator|+
name|SHA2
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|applyDelta
argument_list|(
name|c
argument_list|,
name|Delta
operator|.
name|create
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|,
name|metaId
argument_list|(
name|SHA3
argument_list|)
argument_list|,
name|noDrafts
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|getNoteDbState
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|SHA3
operator|.
name|name
argument_list|()
operator|+
literal|",1001="
operator|+
name|SHA2
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|newChange ()
specifier|private
specifier|static
name|Change
name|newChange
parameter_list|()
block|{
return|return
name|TestChanges
operator|.
name|newChange
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"project"
argument_list|)
argument_list|,
operator|new
name|Account
operator|.
name|Id
argument_list|(
literal|12345
argument_list|)
argument_list|)
return|;
block|}
comment|// Static factory methods to avoid type arguments when using as method args.
DECL|method|noMetaId ()
specifier|private
specifier|static
name|Optional
argument_list|<
name|ObjectId
argument_list|>
name|noMetaId
parameter_list|()
block|{
return|return
name|Optional
operator|.
name|absent
argument_list|()
return|;
block|}
DECL|method|metaId (ObjectId id)
specifier|private
specifier|static
name|Optional
argument_list|<
name|ObjectId
argument_list|>
name|metaId
parameter_list|(
name|ObjectId
name|id
parameter_list|)
block|{
return|return
name|Optional
operator|.
name|of
argument_list|(
name|id
argument_list|)
return|;
block|}
DECL|method|noDrafts ()
specifier|private
specifier|static
name|ImmutableMap
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|ObjectId
argument_list|>
name|noDrafts
parameter_list|()
block|{
return|return
name|ImmutableMap
operator|.
name|of
argument_list|()
return|;
block|}
DECL|method|drafts (Object... args)
specifier|private
specifier|static
name|ImmutableMap
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|ObjectId
argument_list|>
name|drafts
parameter_list|(
name|Object
modifier|...
name|args
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|args
operator|.
name|length
operator|%
literal|2
operator|==
literal|0
argument_list|)
expr_stmt|;
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|ObjectId
argument_list|>
name|b
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|args
operator|.
name|length
operator|/
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|b
operator|.
name|put
argument_list|(
operator|(
name|Account
operator|.
name|Id
operator|)
name|args
index|[
literal|2
operator|*
name|i
index|]
argument_list|,
operator|(
name|ObjectId
operator|)
name|args
index|[
literal|2
operator|*
name|i
operator|+
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|b
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

