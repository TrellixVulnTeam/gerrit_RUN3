begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2019 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.fixes.testing
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|fixes
operator|.
name|testing
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
name|truth
operator|.
name|Truth
operator|.
name|assertAbout
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
name|truth
operator|.
name|ListSubject
operator|.
name|elements
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
name|truth
operator|.
name|FailureMetadata
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
name|truth
operator|.
name|Subject
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
name|jgit
operator|.
name|diff
operator|.
name|ReplaceEdit
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
name|truth
operator|.
name|ListSubject
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
name|Edit
operator|.
name|Type
import|;
end_import

begin_class
DECL|class|GitEditSubject
specifier|public
class|class
name|GitEditSubject
extends|extends
name|Subject
block|{
DECL|method|assertThat (Edit edit)
specifier|public
specifier|static
name|GitEditSubject
name|assertThat
parameter_list|(
name|Edit
name|edit
parameter_list|)
block|{
return|return
name|assertAbout
argument_list|(
name|gitEdits
argument_list|()
argument_list|)
operator|.
name|that
argument_list|(
name|edit
argument_list|)
return|;
block|}
DECL|method|gitEdits ()
specifier|public
specifier|static
name|Subject
operator|.
name|Factory
argument_list|<
name|GitEditSubject
argument_list|,
name|Edit
argument_list|>
name|gitEdits
parameter_list|()
block|{
return|return
name|GitEditSubject
operator|::
operator|new
return|;
block|}
DECL|field|edit
specifier|private
specifier|final
name|Edit
name|edit
decl_stmt|;
DECL|method|GitEditSubject (FailureMetadata failureMetadata, Edit edit)
specifier|private
name|GitEditSubject
parameter_list|(
name|FailureMetadata
name|failureMetadata
parameter_list|,
name|Edit
name|edit
parameter_list|)
block|{
name|super
argument_list|(
name|failureMetadata
argument_list|,
name|edit
argument_list|)
expr_stmt|;
name|this
operator|.
name|edit
operator|=
name|edit
expr_stmt|;
block|}
DECL|method|hasRegions (int beginA, int endA, int beginB, int endB)
specifier|public
name|void
name|hasRegions
parameter_list|(
name|int
name|beginA
parameter_list|,
name|int
name|endA
parameter_list|,
name|int
name|beginB
parameter_list|,
name|int
name|endB
parameter_list|)
block|{
name|isNotNull
argument_list|()
expr_stmt|;
name|check
argument_list|(
literal|"beginA"
argument_list|)
operator|.
name|that
argument_list|(
name|edit
operator|.
name|getBeginA
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|beginA
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"endA"
argument_list|)
operator|.
name|that
argument_list|(
name|edit
operator|.
name|getEndA
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|endA
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"beginB"
argument_list|)
operator|.
name|that
argument_list|(
name|edit
operator|.
name|getBeginB
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|beginB
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"endB"
argument_list|)
operator|.
name|that
argument_list|(
name|edit
operator|.
name|getEndB
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|endB
argument_list|)
expr_stmt|;
block|}
DECL|method|hasType (Type type)
specifier|public
name|void
name|hasType
parameter_list|(
name|Type
name|type
parameter_list|)
block|{
name|isNotNull
argument_list|()
expr_stmt|;
name|check
argument_list|(
literal|"getType"
argument_list|)
operator|.
name|that
argument_list|(
name|edit
operator|.
name|getType
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
DECL|method|isInsert (int insertPos, int beginB, int insertedLength)
specifier|public
name|void
name|isInsert
parameter_list|(
name|int
name|insertPos
parameter_list|,
name|int
name|beginB
parameter_list|,
name|int
name|insertedLength
parameter_list|)
block|{
name|isNotNull
argument_list|()
expr_stmt|;
name|hasType
argument_list|(
name|Type
operator|.
name|INSERT
argument_list|)
expr_stmt|;
name|hasRegions
argument_list|(
name|insertPos
argument_list|,
name|insertPos
argument_list|,
name|beginB
argument_list|,
name|beginB
operator|+
name|insertedLength
argument_list|)
expr_stmt|;
block|}
DECL|method|isDelete (int deletePos, int deletedLength, int posB)
specifier|public
name|void
name|isDelete
parameter_list|(
name|int
name|deletePos
parameter_list|,
name|int
name|deletedLength
parameter_list|,
name|int
name|posB
parameter_list|)
block|{
name|isNotNull
argument_list|()
expr_stmt|;
name|hasType
argument_list|(
name|Type
operator|.
name|DELETE
argument_list|)
expr_stmt|;
name|hasRegions
argument_list|(
name|deletePos
argument_list|,
name|deletePos
operator|+
name|deletedLength
argument_list|,
name|posB
argument_list|,
name|posB
argument_list|)
expr_stmt|;
block|}
DECL|method|isReplace (int originalPos, int originalLength, int newPos, int newLength)
specifier|public
name|void
name|isReplace
parameter_list|(
name|int
name|originalPos
parameter_list|,
name|int
name|originalLength
parameter_list|,
name|int
name|newPos
parameter_list|,
name|int
name|newLength
parameter_list|)
block|{
name|isNotNull
argument_list|()
expr_stmt|;
name|hasType
argument_list|(
name|Type
operator|.
name|REPLACE
argument_list|)
expr_stmt|;
name|hasRegions
argument_list|(
name|originalPos
argument_list|,
name|originalPos
operator|+
name|originalLength
argument_list|,
name|newPos
argument_list|,
name|newPos
operator|+
name|newLength
argument_list|)
expr_stmt|;
block|}
DECL|method|isEmpty ()
specifier|public
name|void
name|isEmpty
parameter_list|()
block|{
name|isNotNull
argument_list|()
expr_stmt|;
name|hasType
argument_list|(
name|Type
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
DECL|method|internalEdits ()
specifier|public
name|ListSubject
argument_list|<
name|GitEditSubject
argument_list|,
name|Edit
argument_list|>
name|internalEdits
parameter_list|()
block|{
name|isNotNull
argument_list|()
expr_stmt|;
name|isInstanceOf
argument_list|(
name|ReplaceEdit
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|check
argument_list|(
literal|"internalEdits"
argument_list|)
operator|.
name|about
argument_list|(
name|elements
argument_list|()
argument_list|)
operator|.
name|thatCustom
argument_list|(
operator|(
operator|(
name|ReplaceEdit
operator|)
name|edit
operator|)
operator|.
name|getInternalEdits
argument_list|()
argument_list|,
name|gitEdits
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

