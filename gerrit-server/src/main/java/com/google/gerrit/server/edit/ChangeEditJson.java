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
DECL|package|com.google.gerrit.server.edit
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|edit
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
name|extensions
operator|.
name|common
operator|.
name|CommitInfo
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
name|EditInfo
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
name|CommonConverters
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
name|Singleton
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
name|revwalk
operator|.
name|RevCommit
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

begin_class
annotation|@
name|Singleton
DECL|class|ChangeEditJson
specifier|public
class|class
name|ChangeEditJson
block|{
DECL|method|toEditInfo (ChangeEdit edit)
specifier|public
name|EditInfo
name|toEditInfo
parameter_list|(
name|ChangeEdit
name|edit
parameter_list|)
throws|throws
name|IOException
block|{
name|EditInfo
name|out
init|=
operator|new
name|EditInfo
argument_list|()
decl_stmt|;
name|out
operator|.
name|commit
operator|=
name|fillCommit
argument_list|(
name|edit
operator|.
name|getEditCommit
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|out
return|;
block|}
DECL|method|fillCommit (RevCommit editCommit)
specifier|private
specifier|static
name|CommitInfo
name|fillCommit
parameter_list|(
name|RevCommit
name|editCommit
parameter_list|)
throws|throws
name|IOException
block|{
name|CommitInfo
name|commit
init|=
operator|new
name|CommitInfo
argument_list|()
decl_stmt|;
name|commit
operator|.
name|commit
operator|=
name|editCommit
operator|.
name|toObjectId
argument_list|()
operator|.
name|getName
argument_list|()
expr_stmt|;
name|commit
operator|.
name|parents
operator|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|commit
operator|.
name|author
operator|=
name|CommonConverters
operator|.
name|toGitPerson
argument_list|(
name|editCommit
operator|.
name|getAuthorIdent
argument_list|()
argument_list|)
expr_stmt|;
name|commit
operator|.
name|committer
operator|=
name|CommonConverters
operator|.
name|toGitPerson
argument_list|(
name|editCommit
operator|.
name|getCommitterIdent
argument_list|()
argument_list|)
expr_stmt|;
name|commit
operator|.
name|subject
operator|=
name|editCommit
operator|.
name|getShortMessage
argument_list|()
expr_stmt|;
name|commit
operator|.
name|message
operator|=
name|editCommit
operator|.
name|getFullMessage
argument_list|()
expr_stmt|;
name|CommitInfo
name|i
init|=
operator|new
name|CommitInfo
argument_list|()
decl_stmt|;
name|i
operator|.
name|commit
operator|=
name|editCommit
operator|.
name|getParent
argument_list|(
literal|0
argument_list|)
operator|.
name|toObjectId
argument_list|()
operator|.
name|getName
argument_list|()
expr_stmt|;
name|commit
operator|.
name|parents
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
return|return
name|commit
return|;
block|}
block|}
end_class

end_unit

