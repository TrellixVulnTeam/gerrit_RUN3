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
DECL|package|com.google.gerrit.acceptance.rest.account
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|rest
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
name|account
operator|.
name|ExternalId
operator|.
name|SCHEME_USERNAME
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
name|acceptance
operator|.
name|AbstractDaemonTest
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
name|acceptance
operator|.
name|RestResponse
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
name|acceptance
operator|.
name|Sandboxed
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
name|AccountExternalIdInfo
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
name|ExternalId
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|reflect
operator|.
name|TypeToken
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
name|List
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

begin_class
annotation|@
name|Sandboxed
DECL|class|ExternalIdIT
specifier|public
class|class
name|ExternalIdIT
extends|extends
name|AbstractDaemonTest
block|{
annotation|@
name|Test
DECL|method|getExternalIDs ()
specifier|public
name|void
name|getExternalIDs
parameter_list|()
throws|throws
name|Exception
block|{
name|Collection
argument_list|<
name|ExternalId
argument_list|>
name|expectedIds
init|=
name|accountCache
operator|.
name|get
argument_list|(
name|user
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|getExternalIds
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|AccountExternalIdInfo
argument_list|>
name|expectedIdInfos
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ExternalId
name|id
range|:
name|expectedIds
control|)
block|{
name|AccountExternalIdInfo
name|info
init|=
operator|new
name|AccountExternalIdInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|identity
operator|=
name|id
operator|.
name|key
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
name|info
operator|.
name|emailAddress
operator|=
name|id
operator|.
name|email
argument_list|()
expr_stmt|;
name|info
operator|.
name|canDelete
operator|=
operator|!
name|id
operator|.
name|isScheme
argument_list|(
name|SCHEME_USERNAME
argument_list|)
condition|?
literal|true
else|:
literal|null
expr_stmt|;
name|info
operator|.
name|trusted
operator|=
literal|true
expr_stmt|;
name|expectedIdInfos
operator|.
name|add
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
name|RestResponse
name|response
init|=
name|userRestSession
operator|.
name|get
argument_list|(
literal|"/accounts/self/external.ids"
argument_list|)
decl_stmt|;
name|response
operator|.
name|assertOK
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|AccountExternalIdInfo
argument_list|>
name|results
init|=
name|newGson
argument_list|()
operator|.
name|fromJson
argument_list|(
name|response
operator|.
name|getReader
argument_list|()
argument_list|,
operator|new
name|TypeToken
argument_list|<
name|List
argument_list|<
name|AccountExternalIdInfo
argument_list|>
argument_list|>
argument_list|()
block|{}
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|expectedIdInfos
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|results
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|results
argument_list|)
operator|.
name|containsExactlyElementsIn
argument_list|(
name|expectedIdInfos
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|deleteExternalIDs ()
specifier|public
name|void
name|deleteExternalIDs
parameter_list|()
throws|throws
name|Exception
block|{
name|setApiUser
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|AccountExternalIdInfo
argument_list|>
name|externalIds
init|=
name|gApi
operator|.
name|accounts
argument_list|()
operator|.
name|self
argument_list|()
operator|.
name|getExternalIds
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|toDelete
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|AccountExternalIdInfo
argument_list|>
name|expectedIds
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|AccountExternalIdInfo
name|id
range|:
name|externalIds
control|)
block|{
if|if
condition|(
name|id
operator|.
name|canDelete
operator|!=
literal|null
operator|&&
name|id
operator|.
name|canDelete
condition|)
block|{
name|toDelete
operator|.
name|add
argument_list|(
name|id
operator|.
name|identity
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|expectedIds
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|toDelete
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|RestResponse
name|response
init|=
name|userRestSession
operator|.
name|post
argument_list|(
literal|"/accounts/self/external.ids:delete"
argument_list|,
name|toDelete
argument_list|)
decl_stmt|;
name|response
operator|.
name|assertNoContent
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|AccountExternalIdInfo
argument_list|>
name|results
init|=
name|gApi
operator|.
name|accounts
argument_list|()
operator|.
name|self
argument_list|()
operator|.
name|getExternalIds
argument_list|()
decl_stmt|;
comment|// The external ID in WebSession will not be set for tests, resulting that
comment|// "mailto:user@example.com" can be deleted while "username:user" can't.
name|assertThat
argument_list|(
name|results
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|results
argument_list|)
operator|.
name|containsExactlyElementsIn
argument_list|(
name|expectedIds
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|deleteExternalIDs_Conflict ()
specifier|public
name|void
name|deleteExternalIDs_Conflict
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|toDelete
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|String
name|externalIdStr
init|=
literal|"username:"
operator|+
name|user
operator|.
name|username
decl_stmt|;
name|toDelete
operator|.
name|add
argument_list|(
name|externalIdStr
argument_list|)
expr_stmt|;
name|RestResponse
name|response
init|=
name|userRestSession
operator|.
name|post
argument_list|(
literal|"/accounts/self/external.ids:delete"
argument_list|,
name|toDelete
argument_list|)
decl_stmt|;
name|response
operator|.
name|assertConflict
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getEntityContent
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"External id %s cannot be deleted"
argument_list|,
name|externalIdStr
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|deleteExternalIDs_UnprocessableEntity ()
specifier|public
name|void
name|deleteExternalIDs_UnprocessableEntity
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|toDelete
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|String
name|externalIdStr
init|=
literal|"mailto:user@domain.com"
decl_stmt|;
name|toDelete
operator|.
name|add
argument_list|(
name|externalIdStr
argument_list|)
expr_stmt|;
name|RestResponse
name|response
init|=
name|userRestSession
operator|.
name|post
argument_list|(
literal|"/accounts/self/external.ids:delete"
argument_list|,
name|toDelete
argument_list|)
decl_stmt|;
name|response
operator|.
name|assertUnprocessableEntity
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getEntityContent
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"External id %s does not exist"
argument_list|,
name|externalIdStr
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

