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
DECL|package|com.google.gerrit.server.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
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
name|gerrit
operator|.
name|extensions
operator|.
name|common
operator|.
name|CommentInfo
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Singleton
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|GetDraft
specifier|public
class|class
name|GetDraft
implements|implements
name|RestReadView
argument_list|<
name|DraftResource
argument_list|>
block|{
DECL|field|commentJson
specifier|private
specifier|final
name|CommentJson
name|commentJson
decl_stmt|;
annotation|@
name|Inject
DECL|method|GetDraft (CommentJson commentJson)
name|GetDraft
parameter_list|(
name|CommentJson
name|commentJson
parameter_list|)
block|{
name|this
operator|.
name|commentJson
operator|=
name|commentJson
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (DraftResource rsrc)
specifier|public
name|CommentInfo
name|apply
parameter_list|(
name|DraftResource
name|rsrc
parameter_list|)
throws|throws
name|OrmException
block|{
return|return
name|commentJson
operator|.
name|format
argument_list|(
name|rsrc
operator|.
name|getComment
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

