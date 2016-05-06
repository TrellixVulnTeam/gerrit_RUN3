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
DECL|package|com.google.gerrit.reviewdb.client
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|reviewdb
operator|.
name|client
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
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|AccountSshKeyTest
specifier|public
class|class
name|AccountSshKeyTest
block|{
DECL|field|KEY
specifier|private
specifier|static
specifier|final
name|String
name|KEY
init|=
literal|"ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAAAgQCgug5VyMXQGnem2H1KVC4/HcRcD4zzBqS"
operator|+
literal|"uJBRWVonSSoz3RoAZ7bWXCVVGwchtXwUURD689wFYdiPecOrWOUgeeyRq754YWRhU+W28"
operator|+
literal|"vf8IZixgjCmiBhaL2gt3wff6pP+NXJpTSA4aeWE5DfNK5tZlxlSxqkKOS8JRSUeNQov5T"
operator|+
literal|"w== john.doe@example.com"
decl_stmt|;
DECL|field|accountId
specifier|private
specifier|final
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
literal|1
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testValidity ()
specifier|public
name|void
name|testValidity
parameter_list|()
throws|throws
name|Exception
block|{
name|AccountSshKey
name|key
init|=
operator|new
name|AccountSshKey
argument_list|(
operator|new
name|AccountSshKey
operator|.
name|Id
argument_list|(
name|accountId
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|,
name|KEY
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|key
operator|.
name|isValid
argument_list|()
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
name|key
operator|=
operator|new
name|AccountSshKey
argument_list|(
operator|new
name|AccountSshKey
operator|.
name|Id
argument_list|(
name|accountId
argument_list|,
literal|0
argument_list|)
argument_list|,
name|KEY
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|key
operator|.
name|isValid
argument_list|()
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
name|key
operator|=
operator|new
name|AccountSshKey
argument_list|(
operator|new
name|AccountSshKey
operator|.
name|Id
argument_list|(
name|accountId
argument_list|,
literal|1
argument_list|)
argument_list|,
name|KEY
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|key
operator|.
name|isValid
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetters ()
specifier|public
name|void
name|testGetters
parameter_list|()
throws|throws
name|Exception
block|{
name|AccountSshKey
name|key
init|=
operator|new
name|AccountSshKey
argument_list|(
operator|new
name|AccountSshKey
operator|.
name|Id
argument_list|(
name|accountId
argument_list|,
literal|1
argument_list|)
argument_list|,
name|KEY
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|key
operator|.
name|getSshPublicKey
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|KEY
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|key
operator|.
name|getAlgorithm
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|KEY
operator|.
name|split
argument_list|(
literal|" "
argument_list|)
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|key
operator|.
name|getEncodedKey
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|KEY
operator|.
name|split
argument_list|(
literal|" "
argument_list|)
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|key
operator|.
name|getComment
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|KEY
operator|.
name|split
argument_list|(
literal|" "
argument_list|)
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

