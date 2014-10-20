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
DECL|package|com.google.gerrit.server.git
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|git
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
name|common
operator|.
name|data
operator|.
name|GroupReference
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
name|AccountGroup
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
name|AccountGroup
operator|.
name|UUID
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
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
DECL|class|GroupList
specifier|public
class|class
name|GroupList
block|{
DECL|field|FILE_NAME
specifier|public
specifier|static
specifier|final
name|String
name|FILE_NAME
init|=
literal|"groups"
decl_stmt|;
DECL|field|byUUID
specifier|private
specifier|final
name|Map
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|,
name|GroupReference
argument_list|>
name|byUUID
decl_stmt|;
DECL|method|GroupList (Map<AccountGroup.UUID, GroupReference> byUUID)
specifier|private
name|GroupList
parameter_list|(
name|Map
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|,
name|GroupReference
argument_list|>
name|byUUID
parameter_list|)
block|{
name|this
operator|.
name|byUUID
operator|=
name|byUUID
expr_stmt|;
block|}
DECL|method|parse (String text, ValidationError.Sink errors)
specifier|public
specifier|static
name|GroupList
name|parse
parameter_list|(
name|String
name|text
parameter_list|,
name|ValidationError
operator|.
name|Sink
name|errors
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|,
name|GroupReference
argument_list|>
name|groupsByUUID
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|BufferedReader
name|br
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|text
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|s
decl_stmt|;
for|for
control|(
name|int
name|lineNumber
init|=
literal|1
init|;
operator|(
name|s
operator|=
name|br
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|;
name|lineNumber
operator|++
control|)
block|{
if|if
condition|(
name|s
operator|.
name|isEmpty
argument_list|()
operator|||
name|s
operator|.
name|startsWith
argument_list|(
literal|"#"
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|int
name|tab
init|=
name|s
operator|.
name|indexOf
argument_list|(
literal|'\t'
argument_list|)
decl_stmt|;
if|if
condition|(
name|tab
operator|<
literal|0
condition|)
block|{
name|errors
operator|.
name|error
argument_list|(
operator|new
name|ValidationError
argument_list|(
name|FILE_NAME
argument_list|,
name|lineNumber
argument_list|,
literal|"missing tab delimiter"
argument_list|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|AccountGroup
operator|.
name|UUID
name|uuid
init|=
operator|new
name|AccountGroup
operator|.
name|UUID
argument_list|(
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|tab
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|s
operator|.
name|substring
argument_list|(
name|tab
operator|+
literal|1
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|GroupReference
name|ref
init|=
operator|new
name|GroupReference
argument_list|(
name|uuid
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|groupsByUUID
operator|.
name|put
argument_list|(
name|uuid
argument_list|,
name|ref
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|GroupList
argument_list|(
name|groupsByUUID
argument_list|)
return|;
block|}
DECL|method|byUUID (AccountGroup.UUID uuid)
specifier|public
name|GroupReference
name|byUUID
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|uuid
parameter_list|)
block|{
return|return
name|byUUID
operator|.
name|get
argument_list|(
name|uuid
argument_list|)
return|;
block|}
DECL|method|resolve (GroupReference group)
specifier|public
name|GroupReference
name|resolve
parameter_list|(
name|GroupReference
name|group
parameter_list|)
block|{
if|if
condition|(
name|group
operator|!=
literal|null
condition|)
block|{
name|GroupReference
name|ref
init|=
name|byUUID
operator|.
name|get
argument_list|(
name|group
operator|.
name|getUUID
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|ref
operator|!=
literal|null
condition|)
block|{
return|return
name|ref
return|;
block|}
name|byUUID
operator|.
name|put
argument_list|(
name|group
operator|.
name|getUUID
argument_list|()
argument_list|,
name|group
argument_list|)
expr_stmt|;
block|}
return|return
name|group
return|;
block|}
DECL|method|references ()
specifier|public
name|Collection
argument_list|<
name|GroupReference
argument_list|>
name|references
parameter_list|()
block|{
return|return
name|byUUID
operator|.
name|values
argument_list|()
return|;
block|}
DECL|method|uuids ()
specifier|public
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|uuids
parameter_list|()
block|{
return|return
name|byUUID
operator|.
name|keySet
argument_list|()
return|;
block|}
DECL|method|put (UUID uuid, GroupReference reference)
specifier|public
name|void
name|put
parameter_list|(
name|UUID
name|uuid
parameter_list|,
name|GroupReference
name|reference
parameter_list|)
block|{
name|byUUID
operator|.
name|put
argument_list|(
name|uuid
argument_list|,
name|reference
argument_list|)
expr_stmt|;
block|}
DECL|method|pad (int len, String src)
specifier|private
specifier|static
name|String
name|pad
parameter_list|(
name|int
name|len
parameter_list|,
name|String
name|src
parameter_list|)
block|{
if|if
condition|(
name|len
operator|<=
name|src
operator|.
name|length
argument_list|()
condition|)
block|{
return|return
name|src
return|;
block|}
name|StringBuilder
name|r
init|=
operator|new
name|StringBuilder
argument_list|(
name|len
argument_list|)
decl_stmt|;
name|r
operator|.
name|append
argument_list|(
name|src
argument_list|)
expr_stmt|;
while|while
condition|(
name|r
operator|.
name|length
argument_list|()
operator|<
name|len
condition|)
block|{
name|r
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
return|return
name|r
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|sort (Collection<T> m)
specifier|private
specifier|static
parameter_list|<
name|T
extends|extends
name|Comparable
argument_list|<
name|?
super|super
name|T
argument_list|>
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|sort
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|m
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|T
argument_list|>
name|r
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|m
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|r
argument_list|)
expr_stmt|;
return|return
name|r
return|;
block|}
DECL|method|asText ()
specifier|public
name|String
name|asText
parameter_list|()
block|{
if|if
condition|(
name|byUUID
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|int
name|uuidLen
init|=
literal|40
decl_stmt|;
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|pad
argument_list|(
name|uuidLen
argument_list|,
literal|"# UUID"
argument_list|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|'\t'
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"Group Name"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|'#'
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
for|for
control|(
name|GroupReference
name|g
range|:
name|sort
argument_list|(
name|byUUID
operator|.
name|values
argument_list|()
argument_list|)
control|)
block|{
if|if
condition|(
name|g
operator|.
name|getUUID
argument_list|()
operator|!=
literal|null
operator|&&
name|g
operator|.
name|getName
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|pad
argument_list|(
name|uuidLen
argument_list|,
name|g
operator|.
name|getUUID
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|'\t'
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|g
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|retainUUIDs (Collection<AccountGroup.UUID> toBeRetained)
specifier|public
name|void
name|retainUUIDs
parameter_list|(
name|Collection
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|toBeRetained
parameter_list|)
block|{
name|byUUID
operator|.
name|keySet
argument_list|()
operator|.
name|retainAll
argument_list|(
name|toBeRetained
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

