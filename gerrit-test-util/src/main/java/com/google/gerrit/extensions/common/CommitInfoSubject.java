begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2017 The Android Open Source Project
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
DECL|package|com.google.gerrit.extensions.common
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|extensions
operator|.
name|common
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
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|truth
operator|.
name|FailureStrategy
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
name|StringSubject
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
name|common
operator|.
name|truth
operator|.
name|SubjectFactory
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
name|Truth
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

begin_class
DECL|class|CommitInfoSubject
specifier|public
class|class
name|CommitInfoSubject
extends|extends
name|Subject
argument_list|<
name|CommitInfoSubject
argument_list|,
name|CommitInfo
argument_list|>
block|{
specifier|private
specifier|static
specifier|final
name|SubjectFactory
argument_list|<
name|CommitInfoSubject
argument_list|,
name|CommitInfo
argument_list|>
DECL|field|COMMIT_INFO_SUBJECT_FACTORY
name|COMMIT_INFO_SUBJECT_FACTORY
init|=
operator|new
name|SubjectFactory
argument_list|<
name|CommitInfoSubject
argument_list|,
name|CommitInfo
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|CommitInfoSubject
name|getSubject
parameter_list|(
name|FailureStrategy
name|failureStrategy
parameter_list|,
name|CommitInfo
name|commitInfo
parameter_list|)
block|{
return|return
operator|new
name|CommitInfoSubject
argument_list|(
name|failureStrategy
argument_list|,
name|commitInfo
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|method|assertThat (CommitInfo commitInfo)
specifier|public
specifier|static
name|CommitInfoSubject
name|assertThat
parameter_list|(
name|CommitInfo
name|commitInfo
parameter_list|)
block|{
return|return
name|assertAbout
argument_list|(
name|COMMIT_INFO_SUBJECT_FACTORY
argument_list|)
operator|.
name|that
argument_list|(
name|commitInfo
argument_list|)
return|;
block|}
DECL|method|CommitInfoSubject (FailureStrategy failureStrategy, CommitInfo commitInfo)
specifier|private
name|CommitInfoSubject
parameter_list|(
name|FailureStrategy
name|failureStrategy
parameter_list|,
name|CommitInfo
name|commitInfo
parameter_list|)
block|{
name|super
argument_list|(
name|failureStrategy
argument_list|,
name|commitInfo
argument_list|)
expr_stmt|;
block|}
DECL|method|commit ()
specifier|public
name|StringSubject
name|commit
parameter_list|()
block|{
name|isNotNull
argument_list|()
expr_stmt|;
name|CommitInfo
name|commitInfo
init|=
name|actual
argument_list|()
decl_stmt|;
return|return
name|Truth
operator|.
name|assertThat
argument_list|(
name|commitInfo
operator|.
name|commit
argument_list|)
operator|.
name|named
argument_list|(
literal|"commit"
argument_list|)
return|;
block|}
DECL|method|parents ()
specifier|public
name|ListSubject
argument_list|<
name|CommitInfoSubject
argument_list|,
name|CommitInfo
argument_list|>
name|parents
parameter_list|()
block|{
name|isNotNull
argument_list|()
expr_stmt|;
name|CommitInfo
name|commitInfo
init|=
name|actual
argument_list|()
decl_stmt|;
return|return
name|ListSubject
operator|.
name|assertThat
argument_list|(
name|commitInfo
operator|.
name|parents
argument_list|,
name|CommitInfoSubject
operator|::
name|assertThat
argument_list|)
operator|.
name|named
argument_list|(
literal|"parents"
argument_list|)
return|;
block|}
DECL|method|committer ()
specifier|public
name|GitPersonSubject
name|committer
parameter_list|()
block|{
name|isNotNull
argument_list|()
expr_stmt|;
name|CommitInfo
name|commitInfo
init|=
name|actual
argument_list|()
decl_stmt|;
return|return
name|GitPersonSubject
operator|.
name|assertThat
argument_list|(
name|commitInfo
operator|.
name|committer
argument_list|)
operator|.
name|named
argument_list|(
literal|"committer"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

