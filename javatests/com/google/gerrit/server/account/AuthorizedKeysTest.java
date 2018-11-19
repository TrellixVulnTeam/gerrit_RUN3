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
name|truth
operator|.
name|Truth
operator|.
name|assertThat
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
name|testing
operator|.
name|GerritBaseTests
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
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
DECL|class|AuthorizedKeysTest
specifier|public
class|class
name|AuthorizedKeysTest
extends|extends
name|GerritBaseTests
block|{
DECL|field|KEY1
specifier|private
specifier|static
specifier|final
name|String
name|KEY1
init|=
literal|"ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAAAgQCgug5VyMXQGnem2H1KVC4/HcRcD4zzBqS"
operator|+
literal|"uJBRWVonSSoz3RoAZ7bWXCVVGwchtXwUURD689wFYdiPecOrWOUgeeyRq754YWRhU+W28"
operator|+
literal|"vf8IZixgjCmiBhaL2gt3wff6pP+NXJpTSA4aeWE5DfNK5tZlxlSxqkKOS8JRSUeNQov5T"
operator|+
literal|"w== john.doe@example.com"
decl_stmt|;
DECL|field|KEY1_WITH_NEWLINES
specifier|private
specifier|static
specifier|final
name|String
name|KEY1_WITH_NEWLINES
init|=
literal|"ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAAAgQCgug5VyMXQGnem2H1KVC4/HcRcD4zzBqS\n"
operator|+
literal|"uJBRWVonSSoz3RoAZ7bWXCVVGwchtXwUURD689wFYdiPecOrWOUgeeyRq754YWRhU+W28\n"
operator|+
literal|"vf8IZixgjCmiBhaL2gt3wff6pP+NXJpTSA4aeWE5DfNK5tZlxlSxqkKOS8JRSUeNQov5T\n"
operator|+
literal|"w== john.doe@example.com"
decl_stmt|;
DECL|field|KEY2
specifier|private
specifier|static
specifier|final
name|String
name|KEY2
init|=
literal|"ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAAAgQDm5yP7FmEoqzQRDyskX+9+N0q9GrvZeh5"
operator|+
literal|"RG52EUpE4ms/Ujm3ewV1LoGzc/lYKJAIbdcZQNJ9+06EfWZaIRA3oOwAPe1eCnX+aLr8E"
operator|+
literal|"6Tw2gDMQOGc5e9HfyXpC2pDvzauoZNYqLALOG3y/1xjo7IH8GYRS2B7zO/Mf9DdCcCKSf"
operator|+
literal|"w== john.doe@example.com"
decl_stmt|;
DECL|field|KEY3
specifier|private
specifier|static
specifier|final
name|String
name|KEY3
init|=
literal|"ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAAAgQCaS7RHEcZ/zjl9hkWkqnm29RNr2OQ/TZ5"
operator|+
literal|"jk2qBVMH3BgzPsTsEs+7ag9tfD8OCj+vOcwm626mQBZoR2e3niHa/9gnHBHFtOrGfzKbp"
operator|+
literal|"RjTWtiOZbB9HF+rqMVD+Dawo/oicX/dDg7VAgOFSPothe6RMhbgWf84UcK5aQd5eP5y+t"
operator|+
literal|"Q== john.doe@example.com"
decl_stmt|;
DECL|field|KEY4
specifier|private
specifier|static
specifier|final
name|String
name|KEY4
init|=
literal|"ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAAAgQDIJzW9BaAeO+upFletwwEBnGS15lJmS5i"
operator|+
literal|"08/NiFef0jXtNNKcLtnd13bq8jOi5VA2is0bwof1c8YbwcvUkdFa8RL5aXoyZBpfYZsWs"
operator|+
literal|"/YBLZGiHy5rjooMZQMnH37A50cBPnXr0AQz0WRBxLDBDyOZho+O/DfYAKv4rzPSQ3yw4+"
operator|+
literal|"w== john.doe@example.com"
decl_stmt|;
DECL|field|KEY5
specifier|private
specifier|static
specifier|final
name|String
name|KEY5
init|=
literal|"ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAAAgQCgBRKGhiXvY6D9sM+Vbth5Kate57YF7kD"
operator|+
literal|"rqIyUiYIMJK93/AXc8qR/J/p3OIFQAxvLz1qozAur3j5HaiwvxVU19IiSA0vafdhaDLRi"
operator|+
literal|"zRuEL5e/QOu9yGq9xkWApCmg6edpWAHG+Bx4AldU78MiZvzoB7gMMdxc9RmZ1gYj/DjxV"
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
DECL|method|test ()
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Optional
argument_list|<
name|AccountSshKey
argument_list|>
argument_list|>
name|keys
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|StringBuilder
name|expected
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|assertSerialization
argument_list|(
name|keys
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|assertParse
argument_list|(
name|expected
argument_list|,
name|keys
argument_list|)
expr_stmt|;
name|expected
operator|.
name|append
argument_list|(
name|addKey
argument_list|(
name|keys
argument_list|,
name|KEY1
argument_list|)
argument_list|)
expr_stmt|;
name|assertSerialization
argument_list|(
name|keys
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|assertParse
argument_list|(
name|expected
argument_list|,
name|keys
argument_list|)
expr_stmt|;
name|expected
operator|.
name|append
argument_list|(
name|addKey
argument_list|(
name|keys
argument_list|,
name|KEY2
argument_list|)
argument_list|)
expr_stmt|;
name|assertSerialization
argument_list|(
name|keys
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|assertParse
argument_list|(
name|expected
argument_list|,
name|keys
argument_list|)
expr_stmt|;
name|expected
operator|.
name|append
argument_list|(
name|addInvalidKey
argument_list|(
name|keys
argument_list|,
name|KEY3
argument_list|)
argument_list|)
expr_stmt|;
name|assertSerialization
argument_list|(
name|keys
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|assertParse
argument_list|(
name|expected
argument_list|,
name|keys
argument_list|)
expr_stmt|;
name|expected
operator|.
name|append
argument_list|(
name|addKey
argument_list|(
name|keys
argument_list|,
name|KEY4
argument_list|)
argument_list|)
expr_stmt|;
name|assertSerialization
argument_list|(
name|keys
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|assertParse
argument_list|(
name|expected
argument_list|,
name|keys
argument_list|)
expr_stmt|;
name|expected
operator|.
name|append
argument_list|(
name|addDeletedKey
argument_list|(
name|keys
argument_list|)
argument_list|)
expr_stmt|;
name|assertSerialization
argument_list|(
name|keys
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|assertParse
argument_list|(
name|expected
argument_list|,
name|keys
argument_list|)
expr_stmt|;
name|expected
operator|.
name|append
argument_list|(
name|addKey
argument_list|(
name|keys
argument_list|,
name|KEY5
argument_list|)
argument_list|)
expr_stmt|;
name|assertSerialization
argument_list|(
name|keys
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|assertParse
argument_list|(
name|expected
argument_list|,
name|keys
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|parseWindowsLineEndings ()
specifier|public
name|void
name|parseWindowsLineEndings
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Optional
argument_list|<
name|AccountSshKey
argument_list|>
argument_list|>
name|keys
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|StringBuilder
name|authorizedKeys
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|authorizedKeys
operator|.
name|append
argument_list|(
name|toWindowsLineEndings
argument_list|(
name|addKey
argument_list|(
name|keys
argument_list|,
name|KEY1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertParse
argument_list|(
name|authorizedKeys
argument_list|,
name|keys
argument_list|)
expr_stmt|;
name|authorizedKeys
operator|.
name|append
argument_list|(
name|toWindowsLineEndings
argument_list|(
name|addKey
argument_list|(
name|keys
argument_list|,
name|KEY2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertParse
argument_list|(
name|authorizedKeys
argument_list|,
name|keys
argument_list|)
expr_stmt|;
name|authorizedKeys
operator|.
name|append
argument_list|(
name|toWindowsLineEndings
argument_list|(
name|addInvalidKey
argument_list|(
name|keys
argument_list|,
name|KEY3
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertParse
argument_list|(
name|authorizedKeys
argument_list|,
name|keys
argument_list|)
expr_stmt|;
name|authorizedKeys
operator|.
name|append
argument_list|(
name|toWindowsLineEndings
argument_list|(
name|addKey
argument_list|(
name|keys
argument_list|,
name|KEY4
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertParse
argument_list|(
name|authorizedKeys
argument_list|,
name|keys
argument_list|)
expr_stmt|;
name|authorizedKeys
operator|.
name|append
argument_list|(
name|toWindowsLineEndings
argument_list|(
name|addDeletedKey
argument_list|(
name|keys
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertParse
argument_list|(
name|authorizedKeys
argument_list|,
name|keys
argument_list|)
expr_stmt|;
name|authorizedKeys
operator|.
name|append
argument_list|(
name|toWindowsLineEndings
argument_list|(
name|addKey
argument_list|(
name|keys
argument_list|,
name|KEY5
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertParse
argument_list|(
name|authorizedKeys
argument_list|,
name|keys
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|validity ()
specifier|public
name|void
name|validity
parameter_list|()
throws|throws
name|Exception
block|{
name|AccountSshKey
name|key
init|=
name|AccountSshKey
operator|.
name|create
argument_list|(
name|accountId
argument_list|,
operator|-
literal|1
argument_list|,
name|KEY1
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|key
operator|.
name|valid
argument_list|()
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
name|key
operator|=
name|AccountSshKey
operator|.
name|create
argument_list|(
name|accountId
argument_list|,
literal|0
argument_list|,
name|KEY1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|key
operator|.
name|valid
argument_list|()
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
name|key
operator|=
name|AccountSshKey
operator|.
name|create
argument_list|(
name|accountId
argument_list|,
literal|1
argument_list|,
name|KEY1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|key
operator|.
name|valid
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|getters ()
specifier|public
name|void
name|getters
parameter_list|()
throws|throws
name|Exception
block|{
name|AccountSshKey
name|key
init|=
name|AccountSshKey
operator|.
name|create
argument_list|(
name|accountId
argument_list|,
literal|1
argument_list|,
name|KEY1
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|key
operator|.
name|sshPublicKey
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|KEY1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|key
operator|.
name|algorithm
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|KEY1
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
name|encodedKey
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|KEY1
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
name|comment
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|KEY1
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
annotation|@
name|Test
DECL|method|keyWithNewLines ()
specifier|public
name|void
name|keyWithNewLines
parameter_list|()
throws|throws
name|Exception
block|{
name|AccountSshKey
name|key
init|=
name|AccountSshKey
operator|.
name|create
argument_list|(
name|accountId
argument_list|,
literal|1
argument_list|,
name|KEY1_WITH_NEWLINES
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|key
operator|.
name|sshPublicKey
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|KEY1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|key
operator|.
name|algorithm
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|KEY1
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
name|encodedKey
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|KEY1
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
name|comment
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|KEY1
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
DECL|method|toWindowsLineEndings (String s)
specifier|private
specifier|static
name|String
name|toWindowsLineEndings
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|s
operator|.
name|replaceAll
argument_list|(
literal|"\n"
argument_list|,
literal|"\r\n"
argument_list|)
return|;
block|}
DECL|method|assertSerialization ( List<Optional<AccountSshKey>> keys, StringBuilder expected)
specifier|private
specifier|static
name|void
name|assertSerialization
parameter_list|(
name|List
argument_list|<
name|Optional
argument_list|<
name|AccountSshKey
argument_list|>
argument_list|>
name|keys
parameter_list|,
name|StringBuilder
name|expected
parameter_list|)
block|{
name|assertThat
argument_list|(
name|AuthorizedKeys
operator|.
name|serialize
argument_list|(
name|keys
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|expected
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertParse ( StringBuilder authorizedKeys, List<Optional<AccountSshKey>> expectedKeys)
specifier|private
specifier|static
name|void
name|assertParse
parameter_list|(
name|StringBuilder
name|authorizedKeys
parameter_list|,
name|List
argument_list|<
name|Optional
argument_list|<
name|AccountSshKey
argument_list|>
argument_list|>
name|expectedKeys
parameter_list|)
block|{
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
name|List
argument_list|<
name|Optional
argument_list|<
name|AccountSshKey
argument_list|>
argument_list|>
name|parsedKeys
init|=
name|AuthorizedKeys
operator|.
name|parse
argument_list|(
name|accountId
argument_list|,
name|authorizedKeys
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|parsedKeys
argument_list|)
operator|.
name|containsExactlyElementsIn
argument_list|(
name|expectedKeys
argument_list|)
expr_stmt|;
name|int
name|seq
init|=
literal|1
decl_stmt|;
for|for
control|(
name|Optional
argument_list|<
name|AccountSshKey
argument_list|>
name|sshKey
range|:
name|parsedKeys
control|)
block|{
if|if
condition|(
name|sshKey
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|assertThat
argument_list|(
name|sshKey
operator|.
name|get
argument_list|()
operator|.
name|accountId
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|accountId
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sshKey
operator|.
name|get
argument_list|()
operator|.
name|seq
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|seq
argument_list|)
expr_stmt|;
block|}
name|seq
operator|++
expr_stmt|;
block|}
block|}
comment|/**    * Adds the given public key as new SSH key to the given list.    *    * @return the expected line for this key in the authorized_keys file    */
DECL|method|addKey (List<Optional<AccountSshKey>> keys, String pub)
specifier|private
specifier|static
name|String
name|addKey
parameter_list|(
name|List
argument_list|<
name|Optional
argument_list|<
name|AccountSshKey
argument_list|>
argument_list|>
name|keys
parameter_list|,
name|String
name|pub
parameter_list|)
block|{
name|AccountSshKey
name|key
init|=
name|AccountSshKey
operator|.
name|create
argument_list|(
operator|new
name|Account
operator|.
name|Id
argument_list|(
literal|1
argument_list|)
argument_list|,
name|keys
operator|.
name|size
argument_list|()
operator|+
literal|1
argument_list|,
name|pub
argument_list|)
decl_stmt|;
name|keys
operator|.
name|add
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|key
operator|.
name|sshPublicKey
argument_list|()
operator|+
literal|"\n"
return|;
block|}
comment|/**    * Adds the given public key as invalid SSH key to the given list.    *    * @return the expected line for this key in the authorized_keys file    */
DECL|method|addInvalidKey (List<Optional<AccountSshKey>> keys, String pub)
specifier|private
specifier|static
name|String
name|addInvalidKey
parameter_list|(
name|List
argument_list|<
name|Optional
argument_list|<
name|AccountSshKey
argument_list|>
argument_list|>
name|keys
parameter_list|,
name|String
name|pub
parameter_list|)
block|{
name|AccountSshKey
name|key
init|=
name|AccountSshKey
operator|.
name|createInvalid
argument_list|(
operator|new
name|Account
operator|.
name|Id
argument_list|(
literal|1
argument_list|)
argument_list|,
name|keys
operator|.
name|size
argument_list|()
operator|+
literal|1
argument_list|,
name|pub
argument_list|)
decl_stmt|;
name|keys
operator|.
name|add
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|AuthorizedKeys
operator|.
name|INVALID_KEY_COMMENT_PREFIX
operator|+
name|key
operator|.
name|sshPublicKey
argument_list|()
operator|+
literal|"\n"
return|;
block|}
comment|/**    * Adds a deleted SSH key to the given list.    *    * @return the expected line for this key in the authorized_keys file    */
DECL|method|addDeletedKey (List<Optional<AccountSshKey>> keys)
specifier|private
specifier|static
name|String
name|addDeletedKey
parameter_list|(
name|List
argument_list|<
name|Optional
argument_list|<
name|AccountSshKey
argument_list|>
argument_list|>
name|keys
parameter_list|)
block|{
name|keys
operator|.
name|add
argument_list|(
name|Optional
operator|.
name|empty
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|AuthorizedKeys
operator|.
name|DELETED_KEY_COMMENT
operator|+
literal|"\n"
return|;
block|}
block|}
end_class

end_unit

